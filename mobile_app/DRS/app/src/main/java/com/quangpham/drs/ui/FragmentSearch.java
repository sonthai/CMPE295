package com.quangpham.drs.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.quangpham.drs.LoginActivity;
import com.quangpham.drs.MainActivity;
import com.quangpham.drs.R;
import com.quangpham.drs.dto.ProductInfo;
import com.quangpham.drs.utils.AppConstant;
import com.quangpham.drs.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.app.Activity.RESULT_OK;
import static com.quangpham.drs.utils.AppConstant.REQUEST_IMAGE_CAPTURE;
import static com.quangpham.drs.utils.StringUtils.capitalizeAndShorten;
import static com.quangpham.drs.utils.StringUtils.removeSpecialCharacters;

/**
 * Created by quangpham on 4/4/18.
 */

public class FragmentSearch extends Fragment {

    public View rootView;
    private DownloadImageTask mDownloadImageTask = null;
    private View mProgressView;
    private GridView mFormView;
    private ImageView cameraView;
    private boolean isNewSearch = false;
    public static ArrayList<ProductInfo> lsProduct = new ArrayList<>();

    public static Fragment newInstance() {

        return new FragmentSearch();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        Button btnCamera = rootView.findViewById(R.id.btn_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                lsProduct = new ArrayList<>();
                dispatchTakePictureIntent();
            }
        });
        final ProductInfoAdapter productInfoAdapter = new ProductInfoAdapter(this.getContext(), lsProduct);
        cameraView = rootView.findViewById(R.id.camera);
        mProgressView = rootView.findViewById(R.id.search_progress);
        mFormView = rootView.findViewById(R.id.search_form);
        mFormView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ProductInfo productInfo = lsProduct.get(position);
                productInfo.toggleFavorite();

                productInfoAdapter.notifyDataSetChanged();
                mFormView.invalidateViews();

                if (productInfo.isFavorite()) {
                    //save into favorite list
                    if (MainActivity.getIndexEntryFavoritedList(MainActivity.favoritedProductsInfo, productInfo) == -1) {
                        MainActivity.favoritedProductsInfo.add(productInfo);

                        //save into database
                        SQLiteDatabase db = LoginActivity.mDBHelper.getWritableDatabase();
                        Cursor cursor = LoginActivity.mDBHelper.getProductEntry(db, productInfo);
                        if (!(cursor != null && cursor.moveToFirst())) {
                            LoginActivity.mDBHelper.insertProductEntry(productInfo);
                        }
                        db.close();
                    }
                } else { //remove from favorite list & db
                    int index = MainActivity.getIndexEntryFavoritedList(MainActivity.favoritedProductsInfo, productInfo);
                    if (index >= 0) {
                        MainActivity.favoritedProductsInfo.remove(index);

                        //delete from database
                        LoginActivity.mDBHelper.removeProductEntry(productInfo);
                    }
                }
            }
        });

        if (lsProduct.size() > 0) {
            mFormView.setAdapter(new ProductInfoAdapter(this.getContext(), lsProduct));
        } else {
            showCameraImage(true);
        }

        return rootView;
    }



    public void dispatchTakePictureIntent() {

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), AppConstant.IMAGE_DIR);
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "image.jpg");
        Uri uriSavedImage = Uri.fromFile(image);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //load images
            showProgress(true);
            if (mDownloadImageTask == null) {
                mDownloadImageTask = new DownloadImageTask(mFormView);
                mDownloadImageTask.execute((Void) null);
            }
        }
    }

    public void showCameraImage(boolean show) {
        if (show)
            cameraView.setVisibility(View.VISIBLE);
        else
            cameraView.setVisibility(View.GONE);
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

            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Boolean> {

        GridView layout;

        public DownloadImageTask(GridView l) {
            this.layout = l;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                File imageFile = new File(Environment.getExternalStorageDirectory(),"/"+AppConstant.IMAGE_DIR+"/image.jpg" );

                InputStream inputStream = new FileInputStream(imageFile);
                byte[] bytes;
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bytes = output.toByteArray();
                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

                //send to REST API
                StringEntity entity = null;
                try {
                    JSONObject jsonObject = new JSONObject();
                    String email = MainActivity.user.getEmail();
                    jsonObject.accumulate("id", "pxq" + System.currentTimeMillis());
                    jsonObject.accumulate("image", encoded);
                    jsonObject.accumulate("email", email);
                    jsonObject.accumulate("quantity", AppConstant.NUMBER_IMAGES_SEARCH_TAB);
                    String gender = MainActivity.user.getGender();
                    if (gender != null & !gender.equals("null")) {
                        jsonObject.accumulate("gender", gender.toLowerCase());
                    }
                    String sJson = jsonObject.toString();

                    entity = new StringEntity(sJson, "UTF-8");
                    entity.setContentType("application/json");
                } catch (JSONException e) {
                    Log.e("HttpEntity", e.getStackTrace().toString());
                }

                HttpUtils.post(AppConstant.URL_REST_API_PROCESS_IMAGE, entity, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String resMsg = response.getString("responseMsg");
                            String resCode = response.getString("responseCode");
                            JSONArray data = response.getJSONArray("data");

                            if (resCode.equals("OK")) {
                                Log.d("PROCESS_IMAGE", "SUCCESSFUL--- this is response : " + resMsg);

                                try {
                                    Log.d("DownloadImageTask","Downloading images from network");

                                    for(int i = 0; i < data.length(); i++) {
                                        String imageName = data.getJSONObject(i).getString("image");
                                        String productName = data.getJSONObject(i).getString("productName");
                                        productName = removeSpecialCharacters(capitalizeAndShorten(productName));
                                        int price = data.getJSONObject(i).getInt("price");
                                        String imageUrl = imageName;
                                        Bitmap image = null;
                                        try {
                                            image = BitmapFactory.decodeStream(new URL(imageUrl).openConnection().getInputStream());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (image != null) {
                                            lsProduct.add(new ProductInfo(productName, imageName, String.valueOf(price), image, MainActivity.user.getEmail()));
                                        }
                                    }

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d("PROCESS_IMAGE", "FAIL--- this is response : " + resMsg);
                            }
                        } catch (JSONException e) {}
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("PROCESS_IMAGE", "Failed to connect to the server - responseString" + responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("PROCESS_IMAGE", "Failed to connect to the server - JSONObject");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            if (success) {
                showCameraImage(false);
                mDownloadImageTask = null;

                //process the favorite star
                if (MainActivity.favoritedProductsInfo.size() > 0) {
                    for (int i = 0; i < lsProduct.size(); i++) {
                        int index = MainActivity.getIndexEntryFavoritedList(MainActivity.favoritedProductsInfo, lsProduct.get(i));
                        if (index >= 0) {
                            lsProduct.get(i).toggleFavorite();
                        }
                    }
                }
                ProductInfoAdapter productInfoAdapter = new ProductInfoAdapter(FragmentSearch.this.getContext(), lsProduct);
                mFormView.setAdapter(productInfoAdapter);
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
            mDownloadImageTask = null;
        }
    }
}
