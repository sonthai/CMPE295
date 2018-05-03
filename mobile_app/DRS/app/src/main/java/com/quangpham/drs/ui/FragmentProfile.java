package com.quangpham.drs.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.quangpham.drs.LoginActivity;
import com.quangpham.drs.MainActivity;
import com.quangpham.drs.R;
import com.quangpham.drs.dto.ProductInfo;
import com.quangpham.drs.dto.UserInfo;
import com.quangpham.drs.utils.AppConstant;
import com.quangpham.drs.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.app.Activity.RESULT_OK;
import static com.quangpham.drs.utils.AppConstant.REQUEST_IMAGE_CAPTURE;

/**
 * Created by quangpham on 4/4/18.
 */

public class FragmentProfile extends Fragment {

    private View rootView;
    private EditText oldPasswordView;
    private EditText newPasswordView;
    private View mProgressView;
    private View mProfileFormView;
    private View mUserInoFormView;

    private EditText fullNameView;
    private EditText birthDateView;
    private EditText genderView;
    private View mProgressUserInfoView;
    private View mUserInfoView;

    private UserProfileTask mAuthTask = null;
    private UserInfoTask mUpdateInfoTask = null;
    private boolean isSuccess = false;
    private ImageView imageProfile;

    public static Fragment newInstance() {
        return new FragmentProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        EditText usernameView = rootView.findViewById(R.id.user_name);
        usernameView.setText(MainActivity.user.getEmail());

        oldPasswordView = rootView.findViewById(R.id.old_password);
        newPasswordView = rootView.findViewById(R.id.new_password);
        Button updateView = rootView.findViewById(R.id.submit_button);
        updateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdatePwd();
            }
        });
        mProfileFormView = rootView.findViewById(R.id.profile_form);
        mProgressView = rootView.findViewById(R.id.profile_progress);

        fullNameView = rootView.findViewById(R.id.full_name);
        birthDateView = rootView.findViewById(R.id.DOB);
        genderView = rootView.findViewById(R.id.gender);
        Button submitView = rootView.findViewById(R.id.user_info_submit_button);
        submitView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUserInfoUpdate();
            }
        });
        mUserInfoView = rootView.findViewById(R.id.user_info_layout);
        mProgressUserInfoView = rootView.findViewById(R.id.user_info_progress);

        imageProfile = rootView.findViewById(R.id.profile_image);
        if (MainActivity.user.getAvatar() != null && !MainActivity.user.getAvatar().equals("null")) {
            imageProfile.setImageBitmap(ProductInfo.covertBase64ToBitmap(MainActivity.user.getAvatar()));
        }
        imageProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAvatarImage();
            }
        });
        TextView editAvatar = rootView.findViewById(R.id.edit_avatar);
        editAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAvatarImage();
            }
        });

        String userName = MainActivity.user.getFullName();
        if (userName != null && !userName.equals("null")) {
            fullNameView.setText(MainActivity.user.getFullName());
            birthDateView.setText(MainActivity.user.getBirthDate());
            genderView.setText(MainActivity.user.getGender());
            submitView.setText("UPDATE");
        }

        return rootView;
    }

    private void takeAvatarImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageProfile.setImageBitmap(imageBitmap);
            //update into local variable
            MainActivity.user.setAvatar(ProductInfo.getBitmapBase64(imageBitmap));

            //update into db
            LoginActivity.mDBHelper.updateAvatarByID(MainActivity.user);

            //update into back-end
            showProgressUserInfo(true);
            mUpdateInfoTask = new UserInfoTask(MainActivity.user, true);
            mUpdateInfoTask.execute((Void) null);
        }

    }

    private void attemptUpdatePwd() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        oldPasswordView.setError(null);
        newPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String oldPwd = oldPasswordView.getText().toString();
        String newPwd = newPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(oldPwd)) {
            oldPasswordView.setError(getString(R.string.error_empty_input));
            focusView = oldPasswordView;
            cancel = true;
        } else {
            if (!isPasswordValid(oldPwd)) {
                oldPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = oldPasswordView;
                cancel = true;
            }
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(newPwd)) {
            newPasswordView.setError(getString(R.string.error_empty_input));
            focusView = newPasswordView;
            cancel = true;
        } else {
            if (!isPasswordValid(newPwd)) {
                newPasswordView.setError(getString(R.string.error_invalid_password));
                focusView = newPasswordView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserProfileTask(oldPwd, newPwd);
            mAuthTask.execute((Void) null);
        }
    }

    private void attemptUserInfoUpdate() {
        if (mUpdateInfoTask != null) {
            return;
        }

        // Reset errors.
        fullNameView.setError(null);
        birthDateView.setError(null);
        genderView.setError(null);

        // Store values at the time of the login attempt.
        String mFullName = fullNameView.getText().toString();
        String mDOB = birthDateView.getText().toString();
        String mGender = genderView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid fullname, if the user entered one.
        if (TextUtils.isEmpty(mFullName)) {
            fullNameView.setError(getString(R.string.error_empty_input));
            focusView = fullNameView;
            cancel = true;
        }

        // Check for a valid date of birth, if the user entered one.
        if (TextUtils.isEmpty(mDOB)) {
            birthDateView.setError(getString(R.string.error_empty_input));
            focusView = birthDateView;
            cancel = true;
        } else {
            if (!isDOBValid(mDOB)) {
                birthDateView.setError(getString(R.string.error_DOB));
                focusView = birthDateView;
                cancel = true;
            }
        }

        // Check for a valid date of birth, if the user entered one.
        if (TextUtils.isEmpty(mGender)) {
            genderView.setError(getString(R.string.error_empty_input));
            focusView = genderView;
            cancel = true;
        } else {
            if (!isGenderValid(mGender)) {
                genderView.setError(getString(R.string.error_gender));
                focusView = genderView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgressUserInfo(true);
            mUpdateInfoTask = new UserInfoTask(new UserInfo(MainActivity.user.getEmail(), mFullName, mGender, mDOB), false);
            mUpdateInfoTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isDOBValid(String password) {
        String mDOB = birthDateView.getText().toString();
        String date[] = mDOB.split("/");

        if (date.length != 3) return false;

        int day, month, year;
        try {
            day = Integer.valueOf(date[1]);
            month = Integer.valueOf(date[0]);
            year = Integer.valueOf(date[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        if (day > 31 || day < 1) return false;
        if (month > 12 || month < 1) return false;
        if (year > 2018 || year < 1900) return false;

        return true;
    }

    private boolean isGenderValid(String password) {
        String mGender = genderView.getText().toString().toUpperCase();
        if (mGender.equals("MALE") || mGender.equals("FEMALE"))
            return true;

        return false;
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

            mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProfileFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgressUserInfo(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mUserInfoView.setVisibility(show ? View.GONE : View.VISIBLE);
            mUserInfoView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUserInfoView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressUserInfoView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressUserInfoView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressUserInfoView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mUserInfoView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressUserInfoView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserProfileTask extends AsyncTask<Void, Void, Boolean> {

        private final String oldPwd;
        private final String newPwd;

        UserProfileTask(String mOldPwd, String mNewPwd) {
            oldPwd = mOldPwd;
            newPwd = mNewPwd;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            StringEntity entity = null;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", MainActivity.user.getEmail());
                jsonObject.accumulate("old_password", oldPwd);
                jsonObject.accumulate("new_password", newPwd);
                String sJson = jsonObject.toString();

                entity = new StringEntity(sJson, "UTF-8");
                entity.setContentType("application/json");
            } catch (JSONException e) {
                Log.e("HttpEntity", e.getStackTrace().toString());
            }

            HttpUtils.post(AppConstant.URL_REST_API_UPDATE_PASSWORD, entity, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        String resMsg = response.getString("responseMsg");
                        String resCode = response.getString("responseCode");

                        if (resCode.equals("OK")) {
                            Log.d("PROFILE", "SUCCESS--- this is response : " + resMsg);
                            isSuccess = true;

                        } else {
                            isSuccess = false;
                        }
                    } catch (JSONException e) {}
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("PROFILE", "Failed to connect to the server - responseString" + responseString);
                    Toast.makeText(rootView.getContext(), "There is an error occurred when " +
                            "connecting to the server .", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("PROFILE", "Failed to connect to the server - JSONObject");
                    Toast.makeText(rootView.getContext(), "There is an error occurred when " +
                            "connecting to the server .", Toast.LENGTH_LONG).show();
                }
            });

            return isSuccess;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(rootView.getContext(), "The info has saved successfully.", Toast.LENGTH_LONG).show();
                //reset flag
                resetFlags();
                resetView();
            } else {
                oldPasswordView.setError(getString(R.string.error_incorrect_password));
                oldPasswordView.requestFocus();
                resetFlags();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        public void resetFlags() {
            isSuccess = false;
        }

        public void resetView() {
            oldPasswordView.setText("");
            newPasswordView.setText("");
        }

    }

    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {

        private final UserInfo userInfo;
        private final boolean isUpdateAvatar;

        public UserInfoTask(UserInfo userInfo, boolean isUpdateAvatar) {
            this.userInfo = userInfo;
            this.isUpdateAvatar = isUpdateAvatar;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (!isUpdateAvatar) {
                //write into local variable
                MainActivity.user.setFullName(userInfo.getFullName());
                MainActivity.user.setGender(userInfo.getGender());
                MainActivity.user.setBirthDate(userInfo.getBirthDate());

                //write into local DB
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                if (MainActivity.isExistInDB) {
                    LoginActivity.mDBHelper.updateUserEntryByID(userInfo);
                } else {
                    SQLiteDatabase db = LoginActivity.mDBHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_EMAIL, userInfo.getEmail());
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_FULLNAME, userInfo.getFullName());
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_GENDER, userInfo.getGender());
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_BIRTHDATE, userInfo.getBirthDate());
                    String avatar = (userInfo.getAvatar() != null ? userInfo.getAvatar() : "");
                    values.put(UserInfo.FeedEntry.COLUMN_NAME_AVATAR_BASE64, avatar);

                    values.put(UserInfo.FeedEntry.COLUMN_NAME_YEAR_JOINED, String.valueOf(year));
                    db.insert(UserInfo.FeedEntry.TABLE_NAME, null, values);
                    db.close();
                }

                //send data into back-end
                StringEntity entity = null;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("email", userInfo.getEmail());
                    jsonObject.accumulate("fullName", userInfo.getFullName());
                    jsonObject.accumulate("gender", userInfo.getGender());
                    jsonObject.accumulate("birthDate", userInfo.getBirthDate());
                    String avatar = (userInfo.getAvatar() != null && !userInfo.getAvatar().equals("null") ? userInfo.getAvatar() : "null");
                    jsonObject.accumulate("avatarBase64", avatar);
                    jsonObject.accumulate("yearJoined", String.valueOf(year));
                    String sJson = jsonObject.toString();

                    entity = new StringEntity(sJson, "UTF-8");
                    entity.setContentType("application/json");
                } catch (JSONException e) {
                    Log.e("HttpEntity", e.getStackTrace().toString());
                }

                HttpUtils.post(AppConstant.URL_REST_API_UPDATE_PROFILE, entity, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String resMsg = response.getString("responseMsg");
                            String resCode = response.getString("responseCode");

                            if (resCode.equals("OK")) {
                                Log.d("PROFILE", "SUCCESS--- this is response : " + resMsg);
                            } else {
                                Log.d("PROFILE", "FAILED--- this is response : " + resMsg);
                            }
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("PROFILE", "Failed to connect to the server - responseString" + responseString);
                        Toast.makeText(rootView.getContext(), "There is an error occurred when " +
                                "connecting to the server .", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("PROFILE", "Failed to connect to the server - JSONObject");
                        Toast.makeText(rootView.getContext(), "There is an error occurred when " +
                                "connecting to the server .", Toast.LENGTH_LONG).show();
                    }
                });

            } else { //update avatar only
                //send data into back-end
                StringEntity entity = null;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("email", userInfo.getEmail());
                    String avatar = (userInfo.getAvatar() != null && !userInfo.getAvatar().equals("null") ? userInfo.getAvatar() : "null");
                    if (userInfo.getFullName() == null || userInfo.getFullName().equals("null")) {
                        jsonObject.accumulate("fullName", "null");
                        jsonObject.accumulate("gender", "null");
                        jsonObject.accumulate("birthDate", "null");
                        jsonObject.accumulate("avatarBase64", avatar);
                        jsonObject.accumulate("yearJoined", "null");
                    } else {
                        jsonObject.accumulate("fullName", userInfo.getFullName());
                        jsonObject.accumulate("gender", userInfo.getGender());
                        jsonObject.accumulate("birthDate", userInfo.getBirthDate());
                        jsonObject.accumulate("avatarBase64", avatar);
                        jsonObject.accumulate("yearJoined", userInfo.getYearJoined());
                    }
                    String sJson = jsonObject.toString();

                    entity = new StringEntity(sJson, "UTF-8");
                    entity.setContentType("application/json");
                } catch (JSONException e) {
                    Log.e("HttpEntity", e.getStackTrace().toString());
                }

                HttpUtils.post(AppConstant.URL_REST_API_UPDATE_PROFILE, entity, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String resMsg = response.getString("responseMsg");
                            String resCode = response.getString("responseCode");

                            if (resCode.equals("OK")) {
                                Log.d("PROFILE", "SUCCESS--- this is response : " + resMsg);
                            } else {
                                Log.d("PROFILE", "FAILED--- this is response : " + resMsg);
                            }
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("PROFILE", "Failed to connect to the server - responseString" + responseString);
                        Toast.makeText(rootView.getContext(), "There is an error occurred when " +
                                "connecting to the server .", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("PROFILE", "Failed to connect to the server - JSONObject");
                        Toast.makeText(rootView.getContext(), "There is an error occurred when " +
                                "connecting to the server .", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUpdateInfoTask = null;
            showProgressUserInfo(false);

            if (success) {
                Toast.makeText(rootView.getContext(), "The info has saved successfully.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateInfoTask = null;
            showProgressUserInfo(false);
        }
    }
}
