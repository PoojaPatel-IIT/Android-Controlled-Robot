package com.chairrobotic.bluetoothbasedapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



public class Splash_screen extends Activity{

	// Splash screen timer
		protected void onCreate(Bundle savedInstanceState) {
	        // TODO Auto-generated method stub
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.splash_activity);
	 
	        Thread timerThread = new Thread(){
	            public void run(){
	                try{
	                    sleep(3500);
	                }catch(InterruptedException e){
	                    e.printStackTrace();
	                }finally{
	                	
	                	
	                    Intent intent = new Intent(Splash_screen.this,MainActivity.class);
	                    startActivity(intent);
	                }
	            }
	        };
	        timerThread.start();
	               
		}
		 
	    @Override
	    protected void onPause() {
	        // TODO Auto-generated method stub
	        super.onPause();
	        finish();
	    }
	 
	}