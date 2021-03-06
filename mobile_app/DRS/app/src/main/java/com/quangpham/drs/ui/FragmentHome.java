package com.quangpham.drs.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.quangpham.drs.utils.StringUtils.capitalizeAndShorten;
import static com.quangpham.drs.utils.StringUtils.removeSpecialCharacters;

/**
 * Created by quangpham on 4/4/18.
 */

public class FragmentHome extends Fragment {

    public View rootView;
    private DownloadImageTask mDownloadImageTask = null;
    private View mProgressView;
    private GridView mFormView;
    private boolean isFirstLoad = false;
    public static ArrayList<ProductInfo> lsProduct = new ArrayList<>();

    public static Fragment newInstance() {
        return new FragmentHome();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mProgressView = rootView.findViewById(R.id.home_progress);

        final ProductInfoAdapter productInfoAdapter = new ProductInfoAdapter(FragmentHome.this.getContext(), lsProduct);
        productInfoAdapter.setPromotion(true);

        mFormView = rootView.findViewById(R.id.home_form);
        mFormView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ProductInfo productInfo = lsProduct.get(position);
                productInfo.toggleFavorite();

                // This tells the GridView to redraw itself
                // in turn calling your BooksAdapter's getView method again for each cell
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

        if (!isFirstLoad) {
            //load images
            showProgress(true);

            if (mDownloadImageTask == null) {
                mDownloadImageTask = new DownloadImageTask(mFormView);
                mDownloadImageTask.execute((Void) null);
            }
            isFirstLoad = true;
        } else { //load from memory
            ProductInfoAdapter productInfoAdapter1 = new ProductInfoAdapter(FragmentHome.this.getContext(), lsProduct);
            productInfoAdapter1.setPromotion(true);
            mFormView.setAdapter(productInfoAdapter1);
        }

        return rootView;
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

    private void loadRecommendation() {

        //send to REST API
        StringEntity entity = null;
        try {
            JSONObject jsonObject = new JSONObject();
            String email = MainActivity.user.getEmail();
            jsonObject.accumulate("quantity", AppConstant.NUMBER_IMAGES_HOME_TAB);
            jsonObject.accumulate("email", MainActivity.user.getEmail());
            String sJson = jsonObject.toString();

            entity = new StringEntity(sJson, "UTF-8");
            entity.setContentType("application/json");
        } catch (JSONException e) {
            Log.e("RECOMMENDATION", e.getStackTrace().toString());
        }

        HttpUtils.post(AppConstant.URL_REST_API_PROCESS_RECOMMENDATION, entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String resMsg = response.getString("responseMsg");
                    String resCode = response.getString("responseCode");
                    JSONArray data = response.getJSONArray("data");

                    if (resCode.equals("OK")) {
                        Log.d("RECOMMENDATION", "SUCCESS--- this is response : " + resMsg);

                        for(int i = 0; i < data.length(); i++) {
                            String imageName = data.getJSONObject(i).getString("image");
                            String productName = data.getJSONObject(i).getString("productName");
                            productName = removeSpecialCharacters(capitalizeAndShorten(productName));
                            int price = data.getJSONObject(i).getInt("price");
                            String imageUrl = imageName;
                            Bitmap image = null;
                            try {
                                image = BitmapFactory.decodeStream(new URL(imageUrl).openConnection().getInputStream());
                                Log.d("Loading...", "image");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (image != null) {
                                lsProduct.add(new ProductInfo(productName, imageName, String.valueOf(price), image, MainActivity.user.getEmail()));
                            }
                        }
                    } else {
                        Log.d("RECOMMENDATION", "FAIL--- this is response : " + resMsg);
                    }
                } catch (JSONException e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("PROCESS_IMAGE", "Failed to connect to the server - responseString");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("PROCESS_IMAGE", "Failed to connect to the server - JSONObject");
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Boolean> {

        private Bitmap image;
        GridView layout;

        public DownloadImageTask(GridView l){ layout = l;}

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                loadRecommendation();
                Log.d("DownloadImageTask","Downloading images from network");

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            mDownloadImageTask = null;
            showProgress(false);

            if (success) {
                //process the star
                if (MainActivity.favoritedProductsInfo.size() > 0) {
                    for (int i = 0; i < lsProduct.size(); i++) {
                        int index = MainActivity.getIndexEntryFavoritedList(MainActivity.favoritedProductsInfo, lsProduct.get(i));
                        if (index >= 0) {
                            lsProduct.get(i).toggleFavorite();
                        }
                    }
                }

                ProductInfoAdapter productInfoAdapter = new ProductInfoAdapter(FragmentHome.this.getContext(), lsProduct);
                productInfoAdapter.setPromotion(true);
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
