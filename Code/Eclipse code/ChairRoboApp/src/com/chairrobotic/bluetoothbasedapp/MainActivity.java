/**
 *  @version 1.1 (23.12.2015)

 *  @developer Asheesh Chourey (https://plus.google.com/u/0/+AsheeshChourey)
 *  @author ( http://robotronix.co.in/ )
 * 
 */

package com.chairrobotic.bluetoothbasedapp;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
 
public class MainActivity extends Activity {
  private static final String TAG = "bluetooth1";
   
  Button btnOn, btnOff,up, down, left, right, frwrd, reverse,stop,opn,cls;
   
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private OutputStream outStream = null;
   
  // SPP UUID service 
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
  // MAC-address of Bluetooth module 
  private static String address = "98:D3:31:40:42:B0";
   
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
 
    //setContentView(R.layout.activity_main);
 
    //btnOn = (Button) findViewById(R.id.btnOn);
    //btnOff = (Button) findViewById(R.id.btnOff);
    up= (Button) findViewById(R.id.button1);
    down=(Button) findViewById(R.id.down);
    frwrd=(Button) findViewById(R.id.fwd);
    reverse=(Button) findViewById(R.id.rev);
    left=(Button) findViewById(R.id.left);
    right=(Button) findViewById(R.id.right);
    opn=(Button) findViewById(R.id.openweel);
    cls=(Button) findViewById(R.id.closeweel);
    stop=(Button) findViewById(R.id.stop);
    
     
    btAdapter = BluetoothAdapter.getDefaultAdapter();
    checkBTState();
 
    up.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        sendData("3"
        		+ "");
        Toast.makeText(getBaseContext(), "up", Toast.LENGTH_SHORT).show();
      }
    });
    down.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          sendData("1");
          Toast.makeText(getBaseContext(), "down", Toast.LENGTH_SHORT).show();
        }
      });frwrd.setOnClickListener(new OnClickListener() {
          public void onClick(View v) {
              sendData("2");
              Toast.makeText(getBaseContext(), "forward", Toast.LENGTH_SHORT).show();
            }
          });reverse.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  sendData("8");
                  Toast.makeText(getBaseContext(), "reverse", Toast.LENGTH_SHORT).show();
                }
              });left.setOnClickListener(new OnClickListener() {
                  public void onClick(View v) {
                      sendData("4");
                      Toast.makeText(getBaseContext(), "left", Toast.LENGTH_SHORT).show();
                    }
                  });right.setOnClickListener(new OnClickListener() {
                      public void onClick(View v) {
                          sendData("6");
                          Toast.makeText(getBaseContext(), "Right", Toast.LENGTH_SHORT).show();
                        }
                      });opn.setOnClickListener(new OnClickListener() {
                          public void onClick(View v) {
                              sendData("7");
                              Toast.makeText(getBaseContext(), "open", Toast.LENGTH_SHORT).show();
                            }
                          });cls.setOnClickListener(new OnClickListener() {
                              public void onClick(View v) {
                                  sendData("9");
                                  Toast.makeText(getBaseContext(), "close", Toast.LENGTH_SHORT).show();
                                }
                              });
    stop.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        sendData("5");
        Toast.makeText(getBaseContext(), "Stop", Toast.LENGTH_SHORT).show();
      }
    });
  }
  
  private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
      if(Build.VERSION.SDK_INT >= 10){
          try {
              final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
              return (BluetoothSocket) m.invoke(device, MY_UUID);
          } catch (Exception e) {
              Log.e(TAG, "Could not create Insecure RFComm Connection",e);
          }
      }
      return  device.createRfcommSocketToServiceRecord(MY_UUID);
  }
   
  @Override
  public void onResume() {
    super.onResume();
 
    Log.d(TAG, "...onResume - try connect...");
   
    // Set up a pointer to the remote node using it's address.
    BluetoothDevice device = btAdapter.getRemoteDevice(address);
   
    // Two things are needed to make a connection:
    //   A MAC address, which we got above.
    //   A Service ID or UUID.  In this case we are using the
    //     UUID for SPP.
   
	try {
		btSocket = createBluetoothSocket(device);
	} catch (IOException e1) {
		errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
	}
    
    /*try {
      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }*/
   
    // Discovery is resource intensive.  Make sure it isn't going on
    // when you attempt to connect and pass your message.
    btAdapter.cancelDiscovery();
   
    // Establish the connection.  This will block until it connects.
    Log.d(TAG, "...Connecting...");
    try {
      btSocket.connect();
      Log.d(TAG, "...Connection ok...");
    } catch (IOException e) {
      try {
        btSocket.close();
      } catch (IOException e2) {
        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
      }
    }
     
    // Create a data stream so we can talk to server.
    Log.d(TAG, "...Create Socket...");
 
    try {
      outStream = btSocket.getOutputStream();
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
    }
  }
 
  @Override
  public void onPause() {
    super.onPause();
 
    Log.d(TAG, "...In onPause()...");
 
    if (outStream != null) {
      try {
        outStream.flush();
      } catch (IOException e) {
        errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
      }
    }
 
    try     {
      btSocket.close();
    } catch (IOException e2) {
      errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
    }
  }
   
  private void checkBTState() {
    // Check for Bluetooth support and then check to make sure it is turned on
    // Emulator doesn't support Bluetooth and will return null
    if(btAdapter==null) { 
      errorExit("Fatal Error", "Bluetooth not support");
    } else {
      if (btAdapter.isEnabled()) {
        Log.d(TAG, "...Bluetooth ON...");
      } else {
        //Prompt user to turn on Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
      }
    }
  }
 
  private void errorExit(String title, String message){
    Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
    finish();
  }
 
  private void sendData(String message) {
    byte[] msgBuffer = message.getBytes();
 
    Log.d(TAG, "...Send data: " + message + "...");
 
    try {
      outStream.write(msgBuffer);
    } catch (IOException e) {
      String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
      if (address.equals("00:00:00:00:00:00")) 
        msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
      	msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
       
      	errorExit("Fatal Error", msg);       
    }
  }
}

