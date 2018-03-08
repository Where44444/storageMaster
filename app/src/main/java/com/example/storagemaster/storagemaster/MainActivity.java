package com.example.storagemaster.storagemaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.view.menu.MenuAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // JAMES - used for the filename
    public static final String FILENAME = "storageMaster.txt";
    // JAMES - const variables to be used for passing values to the new activities
    public static final String POS = "itemPosition";

    public static Category category = new Category();

    // making the user  object
    public static User user;

    public static ItemListAdapter adapter = null;

    public static ArrayList<Category> inventory = new ArrayList<Category>();

    public static NavigationView navigationView; //findViewById(R.id.nav_view);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Gson gson = new Gson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String json = preferences.getString("user", null);
        if (json != null) {
            category = gson.fromJson(json, Category.class);
        }

        //Alex's Excellent CustomAdapter, allows multiple objects to appear in each item in a listview
        adapter = new ItemListAdapter(this, category.items);
        ListView lv = (ListView) findViewById(R.id.itemlist);
 //       generateListContent();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //This runs when an item is clicked in the listview, anywhere on the bar except the buttons or quantity box
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NewItem.class);
                intent.putExtra(POS, Integer.toString(position));
                startActivity(intent);
            }
        });

        // JAMES - set the fab to start the new item activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewItem.class);
                int noItem = -1;
                intent.putExtra(POS, Integer.toString(noItem));
                startActivity(intent);
            }
        });

        // JAMES - this button is just for testing the pop-ups. It will be taken out later.
        Button newList = findViewById(R.id.newButton);
        newList.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewListActivity.class));
            }
        });

        // JAMES - this button is just for testing the slider. It will be taken out later.
//        Button newSlide = findViewById(R.id.sliderButton);
//        newSlide.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, SlideBarActivity.class));
//            }
//        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Henry Function for adding a ListName/string to Nav Drawer
        Menu menu = navigationView.getMenu(); //access to the nav drawer menu
        addNavDrawerItems(menu); //for now just add 15 listNames
    }


    /**
     * Function for adding 15 ListNames/string to Nav Drawer
     * the add parameters are
     * 1: which group they pertain to, all of our lists will belong to the same group.
     * 2: the specific item id
     * 3: the placement of the item in the menu
     * 4: the item's title
     * by Henry
     */
    public static void addNavDrawerItems(Menu menu)
    {
        menu.clear();
        //Toast.makeText(MainActivity.this, "Inventory Size: " + inventory.size(), Toast.LENGTH_SHORT).show();
        //inventory.add(new Category());
        for (int i = 0; i < inventory.size(); i++) {
            SpannableString listName = new SpannableString(inventory.get(i).getCategoryName());
            listName.setSpan(new RelativeSizeSpan(1.2f),0,listName.length(),0);
            //String listName = inventory.get(i).getCategoryName();
            menu.add(1, i, i, listName);
            //Toast.makeText(this, "List: " + i, Toast.LENGTH_SHORT).show();
        }

        //Spannable strings are strings that allow a manipulation of color and size.
        SpannableString newList = new SpannableString("new list..."); //new string
        newList.setSpan(new ForegroundColorSpan(Color.GRAY), 0, newList.length(), 0);//change color to Gray
        newList.setSpan(new RelativeSizeSpan(1.2f),0,newList.length(),0);//make the font size bigger.
        /*The new list id will be -1 to differentiate from the real Categories*/
        menu.add(2, -1, 99, newList);
        //menu.add(1, 16, 98, "Test List");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Test for the Alex's Excellent Item List 0_0
    private void generateListContent() {
        for (int i = 0; i < 5; i++) {
            category.addItem("Item "+i, i+1, i);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        /*menu.add(1, 1, 0, "List 1");

        menu.add(1, 2, 1, "List 2");

        menu.add(1, 3, 2, "List 3");*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == -1) {
            startActivity(new Intent(MainActivity.this, NewListActivity.class));
        }
        else{
            item.setCheckable(true);//leaves the list selected highlighted in the nav drawer
        }

        Toast.makeText(MainActivity.this, item.getTitle() + " Was Selected", Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        Gson gson = new Gson();
        String json = gson.toJson(category);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString("user", json);
        edit.apply();

        super.onStop();

    }
}
