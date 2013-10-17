package com.emergency.EasySOS;

import android.*;
import android.app.ListActivity;
import android.content.Intent;
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

public class ContactOptions extends ListActivity {
    String classNames[] = {"ContactList", "EmergencyContacts"};
    String contactOptions[] = {"Phone Contacts", "Emergency Contacts"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactOptions));
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id){
        super.onListItemClick(lv, v, position, id);
        String openClass = classNames[position];
        try{
            Class selected = Class.forName("com.emergency.EasySOS." + openClass);
            Intent selectedIntent = new Intent(this, selected);
            startActivity(selectedIntent);
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }
}
