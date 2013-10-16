package com.emergency.EasySOS;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    String contactOptions[] = {"Police", "Hospital", "Test"};
    String contactNumber[] = {"999", "9999", "1234"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactOptions));
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
}
