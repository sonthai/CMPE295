package com.quangpham.drs;

/**
 * Created by quangpham on 3/31/18.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.loopj.android.http.*;
import com.quangpham.drs.dto.UserInfo;
import com.quangpham.drs.utils.AppConstant;
import com.quangpham.drs.utils.FeedReaderDBHelper;
import com.quangpham.drs.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public boolean isNotFound = false;
    public boolean isWrongPwd = false;
    public boolean isSuccessLogin = false;
    public boolean isSuccessRegister = false;

    public static FeedReaderDBHelper mDBHelper;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBHelper = new FeedReaderDBHelper(this);


        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_empty_input));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
                mPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = mPasswordView;
                cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        String fullName;
        String yearJoined;
        String gender;
        String avatar;
        String birthDate;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            //Try login first
            StringEntity entity = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", mEmail);
                jsonObject.accumulate("password", mPassword);
                String sJson = jsonObject.toString();

                entity = new StringEntity(sJson, "UTF-8");
                entity.setContentType("application/json");
            } catch (JSONException e) {
                Log.e("HttpEntity", e.getStackTrace().toString());
            }

            HttpUtils.post(AppConstant.URL_REST_API_LOGIN, entity, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String resMsg = response.getString("responseMsg");
                        String resCode = response.getString("responseCode");
                        JSONArray data = response.getJSONArray("data");

                        if (resCode.equals("OK")) {
                            fullName = data.getJSONObject(0).getString("FullName");
                            yearJoined = data.getJSONObject(0).getString("YearJoined");
                            gender = data.getJSONObject(0).getString("Gender");
                            birthDate = data.getJSONObject(0).getString("BirthDate");
                            avatar = data.getJSONObject(0).getString("Avatar");

                            Log.d("LOGIN", "SUCCESS--- this is response : " + resMsg);
                            isSuccessLogin = true;
                        } else {
                            if (resMsg.contains("does not exist")) {
                                Log.d("LOGIN", "FAIL-does not exist--- this is response : " + resMsg);
                                isNotFound = true;

                            } else { //wrong pwd
                                Log.d("LOGIN", "FAIL-wrong pwd--- this is response : " + resMsg);
                                isWrongPwd = true;
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("LOGIN", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("LOGIN", "Failed to connect to the server - responseString");
                    Toast.makeText(LoginActivity.this, "There is an error occurred when " +
                            "connecting to the server .", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("LOGIN", "Failed to connect to the server - JSONObject");
                    Toast.makeText(LoginActivity.this, "There is an error occurred when " +
                            "connecting to the server .", Toast.LENGTH_LONG).show();
                }
            });

            if (isSuccessLogin) { //login success
                return isSuccessLogin;

            } else { //login fails

                if (isWrongPwd) { //fails because wrong pwd
                    return false;
                }
                if (isNotFound) { //not in the db, so create new account

                    //register new account here
                    HttpUtils.post(AppConstant.URL_REST_API_REGISTER, entity, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String resMsg = response.getString("responseMsg");
                                String resCode = response.getString("responseCode");

                                if (resCode.equals("OK")) {
                                    Log.d("REGISTER", "SUCCESS--- this is response : " + resMsg);
                                    isSuccessRegister = true;
                                }
                            } catch (JSONException e) {
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e("REGISTER", "FAIL--- this is response : " + responseString);
                            Toast.makeText(LoginActivity.this, "There is an error occurred when " +
                                    "connecting to the server .", Toast.LENGTH_LONG).show();
                        }
                    });
                    return isSuccessRegister;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);

                MainActivity.user.setEmail(mEmail);

                if (isSuccessLogin) {
                    if (fullName != null && !fullName.equals("null")) {
                        MainActivity.user.setFullName(fullName);
                        MainActivity.user.setGender(gender);
                        MainActivity.user.setBirthDate(birthDate);
                    } else {
                        MainActivity.user.setFullName("null");
                        MainActivity.user.setGender("null");
                        MainActivity.user.setBirthDate("null");
                    }

                    if (avatar != null && !avatar.equals("null")) {
                        MainActivity.user.setAvatar(avatar);
                    } else {
                        MainActivity.user.setAvatar("null");
                    }

                    if (yearJoined != null && !yearJoined.equals("null")) {
                        MainActivity.user.setYearJoined(yearJoined);
                    } else {
                        MainActivity.user.setYearJoined(String.valueOf(year));
                    }
                } else if (isSuccessRegister) {
                    //new user doesn't have any of below info
                    //until he/she updates them
                    MainActivity.user.setFullName("null");
                    MainActivity.user.setGender("null");
                    MainActivity.user.setBirthDate("null");
                    MainActivity.user.setAvatar("null");
                    MainActivity.user.setYearJoined(String.valueOf(year));

                    //write into local db
                    SQLiteDatabase db = LoginActivity.mDBHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_EMAIL, mEmail);
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_FULLNAME, "null");
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_GENDER, "null");
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_BIRTHDATE, "null");
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_AVATAR_BASE64, "null");
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_YEAR_JOINED, String.valueOf(year));
                    db.insert(UserInfo.FeedEntry.TABLE_NAME, null, values);
                    db.close();
                }

                //move to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                //reset flag
                resetFlags();
                finish();
            } else {
                if (isWrongPwd) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
                resetFlags();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        public void resetFlags() {
            isNotFound = isWrongPwd = isSuccessLogin = isSuccessRegister = false;
        }
    }
}

