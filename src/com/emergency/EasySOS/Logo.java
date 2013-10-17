package com.emergency.EasySOS;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

/**
 * @author: adrianm
 * Created Date:
 * Description:
 * Changes:
 */

public class Logo extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo);

//        MediaPlayer logoMusic;

        //start soundtrack
//        logoMusic = MediaPlayer.create(Logo.this, R.raw.sample_sound);
//        logoMusic.start();

        //call another activity after 5s
        Thread logoTimer = new Thread() {
            public void run(){
                try{
                    sleep(1000);
                    Intent menuIntent = new Intent("com.emergency.EasySOS.MAIN");  //call a new activity
                    startActivity(menuIntent);
                }
                catch (InterruptedException ex){
                    ex.printStackTrace();
                }
                finally {
                    finish();
                }
            }
        };
        logoTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
//        logoMusic.release();
    }
}
