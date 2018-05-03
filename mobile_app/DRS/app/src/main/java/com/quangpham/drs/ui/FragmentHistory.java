package com.quangpham.drs.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.quangpham.drs.LoginActivity;
import com.quangpham.drs.MainActivity;
import com.quangpham.drs.R;
import com.quangpham.drs.dto.ProductInfo;

/**
 * Created by quangpham on 4/4/18.
 */

public class FragmentHistory extends Fragment {

    private View rootView;
    private View mProgressView;
    private GridView mFormView;
    private ImageView shoppingCartView;

    public static Fragment newInstance() {
        return new FragmentHistory();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //load from the DB into favorite list
        if (MainActivity.favoritedProductsInfo.size() == 0) {
            SQLiteDatabase db = LoginActivity.mDBHelper.getWritableDatabase();
            Cursor cursor = LoginActivity.mDBHelper.getAllProductEntryByEmail(db, MainActivity.user.getEmail());
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ProductInfo productInfo = new ProductInfo(cursor.getString(cursor.getColumnIndexOrThrow(ProductInfo.FeedEntry.COLUMN_NAME_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(ProductInfo.FeedEntry.COLUMN_NAME_IMAGE_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(ProductInfo.FeedEntry.COLUMN_NAME_PRICE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(ProductInfo.FeedEntry.COLUMN_NAME_IMAGE_BASE64)),
                            cursor.getString(cursor.getColumnIndexOrThrow(ProductInfo.FeedEntry.COLUMN_NAME_USER_EMAIL)));

                    MainActivity.favoritedProductsInfo.add(productInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        mProgressView = rootView.findViewById(R.id.history_progress);



        final ProductInfoAdapter productInfoAdapter = new ProductInfoAdapter(
                                                        FragmentHistory.this.getContext(),
                                                        MainActivity.favoritedProductsInfo);
        productInfoAdapter.setShoppingList(true);

        mFormView = rootView.findViewById(R.id.history_form);
        mFormView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //remove from lsProduct list
                ProductInfo productInfo = MainActivity.favoritedProductsInfo.get(position);
                if (MainActivity.favoritedProductsInfo.remove(productInfo)) {
                    //remove from database
                    LoginActivity.mDBHelper.removeProductEntry(productInfo);

                    //disable star from home tab
                    int index = MainActivity.getIndexEntryFavoritedList(FragmentHome.lsProduct, productInfo);
                    if (index >= 0) {
                        FragmentHome.lsProduct.get(index).toggleFavorite();
                    }
                    //disable star from search tab
                    index = MainActivity.getIndexEntryFavoritedList(FragmentSearch.lsProduct, productInfo);
                    if (index >= 0) {
                        FragmentSearch.lsProduct.get(index).toggleFavorite();
                    }
                    refresh();
                }

                // This tells the GridView to redraw itself
                // in turn calling your BooksAdapter's getView method again for each cell
                productInfoAdapter.notifyDataSetChanged();
                mFormView.invalidateViews();
            }
        });

        //load images from memory
        showProgress(true);
        mFormView.setAdapter(productInfoAdapter);
        showProgress(false);

        shoppingCartView = rootView.findViewById(R.id.shopping_cart);

        refresh();

        return rootView;
    }

    public void refresh() {
        if (MainActivity.favoritedProductsInfo.size() == 0) {
            shoppingCartView.setVisibility(View.VISIBLE);
            mFormView.setVisibility(View.GONE);
        } else {
            shoppingCartView.setVisibility(View.GONE);
            mFormView.setVisibility(View.VISIBLE);
        }
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

}
