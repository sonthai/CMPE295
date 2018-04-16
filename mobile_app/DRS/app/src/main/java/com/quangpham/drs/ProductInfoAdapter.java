package com.quangpham.drs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by quangpham on 4/5/18.
 */

public class ProductInfoAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<ProductInfo> mproductInfos;
    private boolean isShoppingList = false;

    // 1
    public ProductInfoAdapter(Context context, ArrayList<ProductInfo> productInfos) {
        this.mContext = context;
        this.mproductInfos = productInfos;
    }

    // 2
    @Override
    public int getCount() {
        return mproductInfos.size();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ProductInfo productInfo = mproductInfos.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.product_grid, null);
        }

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_product_name);
        final TextView authorTextView = (TextView)convertView.findViewById(R.id.textview_product_price);
        final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        imageView.setImageBitmap(productInfo.getProductImage());
        nameTextView.setText(capitalizeAndShorten(productInfo.getProductName()));

        authorTextView.setText("$"+productInfo.getProductPrice());

        if (isShoppingList) {
            imageViewFavorite.setImageResource(R.drawable.delete);
        } else {
            imageViewFavorite.setImageResource(
                    productInfo.isFavorite() ? R.drawable.star_enabled : R.drawable.star_disabled);
        }

        return convertView;
    }

    public boolean isShoppingList() {
        return isShoppingList;
    }

    public void setShoppingList(boolean shoppingList) {
        isShoppingList = shoppingList;
    }

    private String capitalizeAndShorten(String source) {
        String result = "";

        String[] strArr = source.split(" ");
        int len = strArr.length;
        for (int i = 0; i < len; i++) {
            String str = strArr[i];
//            if (str.length() < 10)
                result += str.toUpperCase() + " ";
            if (result.length() > 35 && i < (len -1))
                return (result + strArr[len - 1]).trim();
        }
        return result.trim();
    }
}

