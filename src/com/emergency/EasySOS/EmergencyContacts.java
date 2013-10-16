package com.emergency.EasySOS;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author: adrianm
 * Created Date:
 * Description:
 * Changes:
 */

public class EmergencyContacts extends ListActivity {
    final Context context = this;

    String contactOptions[] = {"Police/Ambulance", "Fire and Rescue Department (Bomba)", "Tenaga Breakdown", "Civil Defence", "Tourist Police"};
    String contactNumber[] = {"999", "994", "15454", "991", "0321496590"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactOptions));

        // add PhoneStateListener
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id){
        super.onListItemClick(lv, v, position, id);
        String dialContact = contactNumber[position];
        try{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + dialContact));
            startActivity(callIntent);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void onResume(){
        super.onResume();
    }

    protected void onStop(){
        super.onStop();
    }

    //monitor phone call activities
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }
}
