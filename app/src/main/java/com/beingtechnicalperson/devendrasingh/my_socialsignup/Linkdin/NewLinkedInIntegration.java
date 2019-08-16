package com.beingtechnicalperson.devendrasingh.my_socialsignup.Linkdin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.beingtechnicalperson.devendrasingh.my_socialsignup.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;

public class NewLinkedInIntegration extends Activity {
    /*CONSTANT FOR THE AUTHORIZATION PROCESS*/

    /****FILL THIS WITH YOUR INFORMATION*********/
//This is the public api key of our application
    private static final String API_KEY = "This will be in Your developer Linkdin Account";
    //This is the private api key of our application
    private static final String SECRET_KEY = "This will be in Your developer Linkdin Account";
    //This is any string we want to use. This will be used for avoiding CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
    private static final String STATE = "DCEeFWf45A53sdfKef424";
    //This is the url that LinkedIn Auth process will redirect to. We can put whatever we want that starts with http:// or https:// .
//We use a made up url that we will intercept when redirecting. Avoid Uppercases.
    private static final String REDIRECT_URI = "This will be in Your developer Linkdin Account";
    /*********************************************/

//These are constants used for build the urls
    private static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    /*---------------------------------------*/
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";
    String profileUrl = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";
    String accessToken;
    String linkedInUserEmailAddress;
    String emailAddress = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";
    private WebView webView;
    private ProgressDialog pd;
    String deviceId, location, country;
    String linkedInUserId, linkedInUserFirstName, linkedInUserLastName, linkedInUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkdinintegration);

        //get he webView from the layout
        webView = findViewById(R.id.web_view);
        deviceId = getIntent().getStringExtra("deviceId");
        location = getIntent().getStringExtra("location");
        country = getIntent().getStringExtra("country");
        //Request focus for the webview
        webView.requestFocus(View.FOCUS_DOWN);
        webView.clearHistory();
        webView.clearCache(true);
        pd = ProgressDialog.show(this, "", "Linkdin SignUp Loading...", true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //This method will be executed each time a page finished loading.
                //The only we do is dismiss the progressDialog, in case we are showing any.
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                //This method will be called when the Auth proccess redirect to our RedirectUri.
                //We will check the url looking for our RedirectUri.
                if (authorizationUrl.startsWith(REDIRECT_URI)) {
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
                    //If not, that means the request may be a result of CSRF and must be rejected.
                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(STATE)) {
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    //Generate URL for requesting Access Token
                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                    //We make the request in a AsyncTask
                    new PostRequestAsyncTask().execute(accessTokenUrl);

                } else {
                    //Default behaviour
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });
        String authUrl = getAuthorizationUrl();
        Log.i("Authorize", "Loading Auth Url: " + authUrl);
        webView.loadUrl(authUrl);
    }

    /**
     * Method that generates the url for get the access token from the Service
     *
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken) {
        return ACCESS_TOKEN_URL
                + QUESTION_MARK
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE
                + AMPERSAND
                + RESPONSE_TYPE_VALUE + EQUALS + authorizationToken
                + AMPERSAND
                + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + SECRET_KEY;
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return Url
     */
    private static String getAuthorizationUrl() {

        return AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI + "&scope=r_liteprofile%20r_emailaddress%20w_member_social";
    }


    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
//            pd = ProgressDialog.show(NewLinkedInIntegration.this, "", "Loading", true);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length > 0) {
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpost = new HttpPost(url);
                try {
                    HttpResponse response = httpClient.execute(httpost);
                    if (response != null) {
                        //If status is OK 200
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String result = EntityUtils.toString(response.getEntity());
                            JSONObject resultJson = new JSONObject(result);
                            int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;
                            String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                            Log.e("Token", "" + accessToken);
                            if (expiresIn > 0 && accessToken != null) {
                                Log.i("Authorize", "This is the access Token: " + accessToken + ". It will expires in " + expiresIn + " secs");
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.SECOND, expiresIn);
                                long expireDate = calendar.getTimeInMillis();
                                SharedPreferences preferences = NewLinkedInIntegration.this.getSharedPreferences("user_info", 0);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putLong("expires", expireDate);
                                editor.putString("accessToken", accessToken);
                                editor.commit();

                                return true;
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                } catch (ParseException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (status) {
                SharedPreferences preferences = NewLinkedInIntegration.this.getSharedPreferences("user_info", 0);
                accessToken = preferences.getString("accessToken", null);
                try {
                    if (accessToken != null) {
                        new GetProfileRequestAsyncTask().execute(profileUrl);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void sendGetRequest(String urlString, String accessToken) throws Exception {
        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setRequestProperty("cache-control", "no-cache");
        con.setRequestProperty("X-Restli-Protocol-Version", "2.0.0");
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            jsonString.append(line);
        }
        JSONObject jsonObject = new JSONObject(jsonString.toString());
        Log.d("Complete json object", jsonObject.toString());
        try {
            linkedInUserId = jsonObject.getString("id");
            String country = jsonObject.getJSONObject("firstName").getJSONObject("preferredLocale").getString("country");
            String language = jsonObject.getJSONObject("firstName").getJSONObject("preferredLocale").getString("language");
            String getFirstnameKey = language + "_" + country;
            linkedInUserFirstName = jsonObject.getJSONObject("firstName").getJSONObject("localized").getString(getFirstnameKey);
            linkedInUserLastName = jsonObject.getJSONObject("firstName").getJSONObject("localized").getString(getFirstnameKey);
            linkedInUserProfile = jsonObject.getJSONObject("profilePicture").getJSONObject("displayImage~").getJSONArray("elements").getJSONObject(0).getJSONArray("identifiers").getJSONObject(0).getString("identifier");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendGetRequestForEmail(String urlString, String accessToken) throws Exception {
        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setRequestProperty("cache-control", "no-cache");
        con.setRequestProperty("X-Restli-Protocol-Version", "2.0.0");
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            jsonString.append(line);
        }
        JSONObject jsonObject = new JSONObject(jsonString.toString());
        linkedInUserEmailAddress = jsonObject.getJSONArray("elements").getJSONObject(0).getJSONObject("handle~").getString("emailAddress");
        Log.d("email json object", jsonObject.toString());
        Intent intent = new Intent();
        intent.putExtra("email", linkedInUserEmailAddress);
        intent.putExtra("firstname", linkedInUserFirstName);
        intent.putExtra("lastname", linkedInUserLastName);
        intent.putExtra("profile", linkedInUserProfile);
        intent.putExtra("id", linkedInUserId);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(NewLinkedInIntegration.this, "", "Loading..", true);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            if (urls.length > 0) {
                try {
                    sendGetRequest(profileUrl, accessToken);
                    sendGetRequestForEmail(emailAddress, accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            if (data != null) {

                try {
                    String welcomeTextString = String.format("Welcome %1$s %2$s, You are a %3$s", data.getString("firstName"), data.getString("lastName"), data.getString("headline"));

                } catch (JSONException e) {
                    Log.e("Authorize", "Error Parsing json " + e.getLocalizedMessage());
                }
            }
        }


    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
