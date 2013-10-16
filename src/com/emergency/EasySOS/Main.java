package com.emergency.EasySOS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

        Button setup = (Button) findViewById(R.id.setup);
        Button contacts = (Button) findViewById(R.id.contacts);
        Button stop = (Button) findViewById(R.id.stopalarm);
        ImageView alert  = (ImageView) findViewById(R.id.alertView);

        greetings = (TextView) findViewById(R.id.welcome);

//        start Facebook Login
//        Session.openActiveSession(this, true, new Session.StatusCallback() {
//
//            // callback when session changes state
//            @Override
//            public void call(Session session, SessionState state, Exception exception) {
//                if (session.isOpened()) {
//
//                    // make request to the /me API
//                    Request.newMeRequest(session, new Request.GraphUserCallback() {
//
//                        // callback after Graph API response with user object
//                        @Override
//                        public void onCompleted(GraphUser user, Response response) {
//                            if (user != null) {
//                                TextView welcome = (TextView) findViewById(R.id.welcome);
//                                welcome.setText("Hello " + user.getName() + "!");
//                            }
//                        }
//                    }).executeAsync();
//                }
//            }
//        });

//        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
//            @Override
//            public void onUserInfoFetched(GraphUser user) {
//                Main.this.user = user;
//            }
//        });

//        welcome = (TextView) findViewById(R.id.welcome);
//        welcome.setText("Hello " + user.getName() + "!");

        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                startActivity(new Intent("com.emergency.EasySOS.SETUP"));
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                startActivity(new Intent("com.emergency.EasySOS.CONTACT"));
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                buttonSound.pause();
            }
        });

        alert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                buttonSound.start();
            }
        });

        postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostStatusUpdate();
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
//    }

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
                        }
            }).executeAsync();
        }

        else if(session.getState().isClosed()){
            greetings.setText("");
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
            final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
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
}
