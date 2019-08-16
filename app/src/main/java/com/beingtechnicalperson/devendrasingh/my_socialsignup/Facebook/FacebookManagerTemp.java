package com.beingtechnicalperson.devendrasingh.my_socialsignup.Facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.trainerkit.facebook.OnFacebookLoginListener;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookManagerTemp implements FacebookCallback<LoginResult> {

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private Activity activity;
    private OnFacebookLoginListener listener;

    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    public FacebookManagerTemp(Activity activity) {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        this.activity = activity;

        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        if (loginManager != null) {
            loginManager.registerCallback(callbackManager, this);
        }
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    accessToken = currentAccessToken;
                }
            }
        };
        accessToken = AccessToken.getCurrentAccessToken();

    }

    public void doLogin() {

        if (loginManager != null) {
            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
            if (isLoggedIn) {
                fetchProfileDetail(accessToken);


            } else {
                loginManager.logInWithReadPermissions(activity,
                        Arrays.asList("public_profile", "email"));
            }
        } else {
            if (listener != null) {
                listener.onFacebookError("Login manager is not initialized");
            }
        }
    }

    public void doLogout() {
        if (loginManager != null) {
            loginManager.logOut();
        } else {
            if (listener != null) {
                listener.onFacebookError("You must login first");
            }
        }
    }

    public void setOnLoginListener(OnFacebookLoginListener listener) {
        this.listener = listener;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(LoginResult result) {
        fetchProfileDetail(result.getAccessToken());

    }

    public void fetchProfileDetail(final AccessToken fbAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(fbAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    String picURL = "";
                    JSONObject jsonObject = response.getJSONObject();
                    jsonObject.optString("name");
                    jsonObject.optString("id");
                    jsonObject.optString("email");
                    if (jsonObject.has("picture")) {

                        //{"data":{"url":"https:\/\/fbcdn-profile-a.akamaihd.net\/hprofile-ak-xpl1\/v\/t1.0-1\/p320x320\/13233156_1056689067736401_8875841216895027796_n.jpg?oh=e0a2202a403e51aeea7b6c078f6cd227&oe=57E63A60&__gda__=1473889794_fbe61a61e4c2649f200e55f591a186bd","is_silhouette":false,"width":320,"height":320}}

                        JSONObject picture = jsonObject.getJSONObject("picture");
                        JSONObject picData = picture.getJSONObject("data");
                        Log.e("pic", "" + picture.toString());
                        picURL = picData.getString("url");

                        Log.e("MyResponse", jsonObject.toString());


                    }
                    String gender = "-1";
                    if (jsonObject.optString("gender").equals("male")) {
                        gender = "1";
                    } else if (jsonObject.optString("gender").equals("female")) {
                        gender = "0";
                    }


                    if (listener != null) {
                        listener.onFacebookLogin(jsonObject.optString("name"), jsonObject.optString("email"), jsonObject.optString("id"), picURL, gender);
                    } else {
                        listener.onFacebookError("Error Occured");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(e.getLocalizedMessage()!=null)
                    listener.onFacebookError(e.getLocalizedMessage());
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,name,picture.height(300).width(300),gender");
        request.setParameters(parameters);
        request.executeAsync();
    }
	
	/*public ArrayList<ContactBean> getFbFriendList(){
		return fbFriendList;
	}*/


    public String getUserPic(String userID) {
        String imageURL = "https://graph.facebook.com/" + userID + "/picture?type=normal";
        return imageURL;
    }

    @Override
    public void onCancel() {

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            doLogin();
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(activity, "Facebook login was canceled", Toast.LENGTH_SHORT).show();
                //listener.onFacebookError("Login cancelled");
                Toast.makeText(activity, "Login cancelled", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onError(final FacebookException error) {
        if (error instanceof com.facebook.FacebookAuthorizationException) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
                doLogin();
            }
        } else {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    error.printStackTrace();
                    //Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public AccessToken getCurrentToken() {
        return accessToken;
    }

    public void onDestroy() {
        accessTokenTracker.startTracking();
    }


}
