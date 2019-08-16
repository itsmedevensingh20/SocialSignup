package com.beingtechnicalperson.devendrasingh.my_socialsignup.Google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public class Google_SignUp implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    Context context;
    public static final int RESULT_OK = -1;
    public static final int RC_SIGN_IN = 9001;
    String TAG = "Google Plus";
    private OnClientConnectedListener listener;


    public Google_SignUp(Context context, OnClientConnectedListener listener) {
        this.context = context;
        this.listener = listener;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Plus.API)
                .enableAutoManage((FragmentActivity) context/* FragmentActivity */, 1, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    public void onPause() {
        mGoogleApiClient.disconnect();
    }


    public void onStart() {
        mGoogleApiClient.connect();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN && mGoogleApiClient.isConnected() && resultCode == RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            // signIn();
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String gender = "-1";
           /* try {
                Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                int p_gender = person.getGender();

                Log.e("gender", "" + person.getGender());

                if (p_gender == 0)//male
                {
                    gender = "1";//male
                } else if (p_gender == 1) {
                    gender = "0";//female
                } else if (p_gender == 2) {
                    gender = "2";//other
                }

            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
*/
            String imageUrl = "";
            String email = "";
            String mobile = " ";
            if (acct.getPhotoUrl() != null) {
                imageUrl = acct.getPhotoUrl().toString();

                Log.e("fp url", "" + imageUrl);
            }

            //for get email
            if (acct.getEmail() != null) {
                email = acct.getEmail();
            }

            //for get name from social media
            if (acct.getDisplayName() != null) {
                String fullname = acct.getDisplayName();
                if (fullname.contains(" ")) {
                    String n[] = fullname.split(" ");
//                    userName = n[0];
//                    lastName = n[1];
                } else {
                    //.userName = fullname;
                }
            }
            //uniqueId = acct.getId();
//            UserBean.getObect().profilePicUrl = imageUrl;
//            UserBean.getObect().picUrl = imageUrl;
            //emailId = email;
            //accountType = "2";
            listener.onGoogleProfileFetchComplete(acct.getId(), acct.getDisplayName(), acct.getEmail());
        } else {
            listener.onClientFailed(result.getStatus().getStatusMessage());
            signOut();
        }
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        /*if(status.isSuccess()){
                            Log.e("Logout","success");
                        }else{
                            Log.e("Logout","Failed");
                        }*/
                    }
                });
    }

    private void revokeAccess() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                            ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
                        }
                    });
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void signIn() {
        revokeAccess();
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        ((OnClientConnectedListener) context).onClientFailed(connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("connect", "success");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("suspend", "success "+ i);

    }

    public interface OnClientConnectedListener {

        void onClientFailed(String msg);

        void onGoogleProfileFetchComplete(String id, String displayName, String email);
    }


}
