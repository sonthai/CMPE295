package com.quangpham.drs;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static Toolbar toolbar;
    public static UserInfo user = new UserInfo();
    //public static UserInfo user = new UserInfo("xuanquangpham@yahoo.com", "2018");
    public static MainActivity mainActivity;
    public static boolean isExistInDB = false;

    public static ArrayList<ProductInfo> favoritedProductsInfo = new ArrayList<>();

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        //Add toolbar
        toolbar = findViewById(R.id.toolbar);

        FeedReaderDBHelper.resetTable(LoginActivity.mDBHelper.getWritableDatabase());

        SQLiteDatabase db = LoginActivity.mDBHelper.getWritableDatabase();
        Cursor dbList = LoginActivity.mDBHelper.getAllUserEntry(db);
        String user = MainActivity.user.getEmail().split("@")[0];
        if (dbList == null || !dbList.moveToFirst()) toolbar.setTitle("Welcome " + user + "!");
        else { //get Name
            String name = dbList.getString(dbList.getColumnIndexOrThrow(UserInfo.FeedEntry.COLUMN_NAME_FULLNAME));
            if (!name.equals(""))
                toolbar.setTitle("Welcome back, " + name.split(" ")[0] + "!");
            else {
                toolbar.setTitle("Welcome back " + user + "!");
            }
        }
        dbList.close();
        db.close();
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(mViewPager);

        // Attach the page change listener to tab strip and **not** the view pager inside the activity
        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("- CMPE295 MASTER PROJECT -");
            builder.setMessage(" \n\n" +
                    "DYNAMIC RECOMMENDATION SYSTEM\n\n" +
                    "Advisor: Rex Tsou\n\n" +
                    "Team Members:\n" +
                    "- Nguyen Ngo\n" +
                    "- Quang Pham\n" +
                    "- Son Thai\n" +
                    "- Andrew Wong\n");

            // add a button
            builder.setPositiveButton("Close", null);

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        if (id == R.id.action_sign_out) {
            MainActivity.user = new UserInfo();
            //move to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int getIndexEntryFavoritedList(ArrayList<ProductInfo> arrlist, ProductInfo productInfo) {
        for (int i = 0; i < arrlist.size(); i++) {
            ProductInfo productInfo1 = arrlist.get(i);
            if (productInfo1.getProductImageName().equals(productInfo.getProductImageName()))
                return i;
        }
        return -1;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        final int PAGE_COUNT = 5;
        private int tabIcons[] = {R.drawable.home, R.drawable.search, R.drawable.location, R.drawable.history, R.drawable.profile};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)
                return FragmentHome.newInstance();

            if (position == 1)
                return FragmentSearch.newInstance();

            if (position == 2)
                return FragmentLocation.newInstance();

            if (position == 3)
                return FragmentHistory.newInstance();

            return FragmentProfile.newInstance();
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public int getPageIconResId(int position) {
            return tabIcons[position];
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
