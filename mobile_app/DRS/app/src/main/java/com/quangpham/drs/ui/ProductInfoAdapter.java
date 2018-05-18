package com.quangpham.drs.ui;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quangpham.drs.R;
import com.quangpham.drs.dto.ProductInfo;
import com.quangpham.drs.utils.AppConstant;

import java.util.ArrayList;

/**
 * Created by quangpham on 4/5/18.
 */

public class ProductInfoAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<ProductInfo> mproductInfos;
    private boolean isShoppingList = false;
    private boolean isPromotion = false;

    public ProductInfoAdapter(Context context, ArrayList<ProductInfo> productInfos) {
        this.mContext = context;
        this.mproductInfos = productInfos;
    }

    @Override
    public int getCount() {
        return mproductInfos.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ProductInfo productInfo = mproductInfos.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.product_grid, null);
        }

        final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageview_cover_art);
        final TextView nameTextView = (TextView)convertView.findViewById(R.id.textview_product_name);
        final TextView priceTextView = (TextView)convertView.findViewById(R.id.textview_product_price);
        final TextView priceDiscountTextView = (TextView)convertView.findViewById(R.id.textview_product_price_discount);
        final ImageView imageViewFavorite = (ImageView)convertView.findViewById(R.id.imageview_favorite);

        imageView.setImageBitmap(productInfo.getProductImage());
        nameTextView.setText(productInfo.getProductName());

        priceTextView.setText("$"+productInfo.getProductPrice());

        if (isPromotion) {
            int discountPrice = (int) (Integer.valueOf(productInfo.getProductPrice()) * AppConstant.PROMO_RATE);
            priceDiscountTextView.setText("$" + discountPrice);
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            priceDiscountTextView.setVisibility(View.GONE);
        }

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

    public boolean isPromotion() {
        return isPromotion;
    }

    public void setPromotion(boolean promotion) {
        isPromotion = promotion;
    }

}

