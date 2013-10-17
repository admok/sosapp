package com.emergency.EasySOS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;


public class Main extends Activity{
    /**
     * Called when the activity is first created.
     */
    private UiLifecycleHelper uiHelper;
    private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";
    private static final String PERMISSION = "publish_actions";

    private GraphUser user;
//    private LoginButton loginButton;
    private TextView greetings;
    private boolean resumeAlarm = false;
    private boolean canPresentShareDialog;
    private Button postStatusUpdateButton;
    private GraphPlace place;
    private List<GraphUser> tags;
    private int alarmStatus = 0;
    private GoogleMap googleMap;
    private double lat;
    private double lng;

//    private enum PendingAction {
//        NONE,
//        POST_PHOTO,
//        POST_STATUS_UPDATE
//    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.emergency.EasySOS",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        final MediaPlayer buttonSound = MediaPlayer.create(Main.this, R.raw.alarm);

//        Button setup = (Button) findViewById(R.id.setup);
        Button contacts = (Button) findViewById(R.id.contacts);
        Button mapButton = (Button) findViewById(R.id.mapButton);
        ImageView alertStart  = (ImageView) findViewById(R.id.alertStart);

        greetings = (TextView) findViewById(R.id.welcome);

        GetCurrentLocation();

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                startActivity(new Intent("com.emergency.EasySOS.CONTACT"));
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                startActivity(new Intent("com.emergency.EasySOS.MAP"));
            }
        });

        alertStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(alarmStatus == 1){
                    buttonSound.pause();
                    alarmStatus = 0;
                }
                else
                {
                    buttonSound.start();
                    alarmStatus++;
                }
            }
        });

        postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostStatusUpdate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if(session.getState().isOpened()){
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                greetings.setText("Hello " + user.getName() + "!");
                            }
                            Main.this.user = user;
                            postStatusUpdateButton.setEnabled(true);
                        }
            }).executeAsync();
        }

        else if(session.getState().isClosed()){
            greetings.setText("");
            postStatusUpdateButton.setEnabled(false);
        }
    }

    private void UpdateUI(Session session) {
        session = Session.getActiveSession();
    }

    private void onClickPostStatusUpdate() {
        performPublish(canPresentShareDialog);
    }

    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }

    private void performPublish(boolean allowNoSession) {
        Session session = Session.getActiveSession();
        if (session != null) {
            if (hasPublishPermission()) {
                // We can do the action right away.
                postStatusUpdate();
                return;
            } else if (session.isOpened()) {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
                return;
            }
        }

        if (allowNoSession) {
        }
    }

    private void postStatusUpdate() {
        if (canPresentShareDialog) {
            FacebookDialog shareDialog = createShareDialogBuilder().build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (user != null && hasPublishPermission()) {
            final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()),  "http://maps.google.com/maps?q=" + Double.toString(lat) + "," + Double.toString(lng) );
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, place, tags, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            showPublishResult(message, response.getGraphObject(), response.getError());
                        }
                    });
            request.executeAsync();
        } else {

        }
    }

    private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
        return new FacebookDialog.ShareDialogBuilder(this)
                .setName("Hello Facebook")
                .setDescription("The 'Hello Facebook' sample application showcases simple Facebook integration")
                .setLink("http://developers.facebook.com/android");
    }

    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = getString(R.string.successfully_posted_post, message, id);
        } else {
            title = getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }


    private void GetCurrentLocation() {

        double[] d = getlocation();
        lat = d[0];
        lng = d[1];

//        googleMap
//                .addMarker(new MarkerOptions()
//                        .position(new LatLng(lat, lng))
//                        .title("Current Location")
//                        .icon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.dot_blue)));
//
//        googleMap
//                .animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        new LatLng(lat, lng), 5));
    }

    public double[] getlocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        double[] gps = new double[2];

        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }
}
