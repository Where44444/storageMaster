package com.example.storagemaster.storagemaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainAct";

    // JAMES - used for the filename
    public static final String FILENAME = "storageMaster.txt";
    // JAMES - const variables to be used for passing values to the new activities
    public static final String POSI = "itemPosition";
    public static final String POSC = "categoryPosition";
    // JAMES - consts for getting out of sharedPreferences
    public static final String USER = "user";
    public static final String ALCAT = "ListCat";

    public static ItemListAdapter adapter = null;
    public static ShoppingListAdapter adapterShopping = null;
    public static User user = new User();

    public static ListView lv;
    public static int ID = 0;
    public static boolean isWindowOpen = false; //Variable used to prevent multiple conflicting windows from opening
    public static int currentCategory = 0;
    public static boolean isdeleting = false;
    public static boolean newlistadded = false;
    public static boolean listedited = false;

    public static NavigationView navigationView; //findViewById(R.id.nav_view);

    /**
     * Sets the current list and loads shared preferences
     * Also creates and loads the shopping list if it doesn't exist yet
     * Sets the item list adapter to work with the listview
     * Initializes the navDrawer
     * @param savedInstanceState Definitely not a savedInstanceState ;)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "at the very, very beginning");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "before loading from shared preferences");

        Gson gson = new Gson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String json = preferences.getString(USER, null);

        if (json != null) {
            Log.v(TAG, json);
            user = gson.fromJson(json, User.class);

            // hopefully a temporary fix. Goes through all of the lists to connect the correct
            // items to the inventory items.
            for(int item = 0; item < user.inventory.get(0).items.size(); item++){
                for(int i = 1; i < user.inventory.size(); i++){
                    for(int it = 0; it < user.inventory.get(i).items.size(); it++){
                        if (user.inventory.get(0).items.get(item).getItemName().equals(user.inventory.get(i).items.get(it).getItemName())){
                            user.inventory.get(0).items.set(item, user.inventory.get(i).items.get(it));
                        }
                    }
                }
            }
        }

        Log.i(TAG, "loaded from shared preferences");

        if(user.inventory.size() == 0) {
            Category category = new Category();
            category.setCategoryName("Shopping List");
            user.inventory.add(category);
            Log.i(TAG, "added Shopping List to user inventory");
            category = new Category();
            category.setCategoryName("Main");
            user.inventory.add(category);
            Log.i(TAG, "added Main to user inventory");
        }


        //Alex's Excellent CustomAdapter, allows multiple objects to appear in each item in a listview
        this.setTitle(user.inventory.get(0).getCategoryName());
        adapterShopping = new ShoppingListAdapter(this, user.inventory.get(0).items);
        lv = (ListView) findViewById(R.id.itemlist);
        adapterShopping.setID(0);
        lv.setAdapter(adapterShopping);
        adapterShopping.notifyDataSetChanged();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //This runs when an item is clicked in the listview, anywhere on the bar except the buttons or quantity box
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!MainActivity.isWindowOpen) {
                    MainActivity.isWindowOpen = true;
                    Intent intent = new Intent(MainActivity.this, NewItem.class);
                    intent.putExtra(POSI, Integer.toString(position));
                    intent.putExtra(POSC, Integer.toString(ID));
                    startActivity(intent);
                }
            }
        });
        Log.i(TAG, "set item list adapter");

        // JAMES - set the fab to start the new item activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewItem.class);
                int noItem = -1;
                intent.putExtra(POSI, Integer.toString(noItem));
                intent.putExtra(POSC, Integer.toString(ID));
                startActivity(intent);
            }
        });
        Log.i(TAG, "set fab listener");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Henry Function for adding a ListName/string to Nav Drawer
        Menu menu = navigationView.getMenu(); //access to the nav drawer menu
        addNavDrawerItems(menu); //update the nav drawer with correct category names
        menu.getItem(0).setCheckable(true);//make it checkable
        menu.getItem(0).setChecked(true);//check the default list/shopping list
        Log.i(TAG, "passed drawer layout");
    }

    /**
     * Function for adding ListNames/strings to Nav Drawer
     * the add parameters are:
     * 1: which group they pertain to, all of our lists will belong to the same group.
     * 2: the specific item id
     * 3: the placement of the item in the menu
     * 4: the item's title
     * @param menu this is the navigation drawer menu
     * by Henry
     */
    public static void addNavDrawerItems(Menu menu)
    {
        menu.clear();

        SpannableString shoppingList = new SpannableString("Shopping List"); //new string
        shoppingList.setSpan(new ForegroundColorSpan(Color.GRAY), 0, shoppingList.length(), 0);//change color to Gray
        shoppingList.setSpan(new RelativeSizeSpan(1.2f),0,shoppingList.length(),0);//make the font size bigger.
        menu.add(1, 0, 0, shoppingList);


        for (int i = 1; i < user.inventory.size(); i++) {
           // if (user.inventory != null)
            SpannableString listName = new SpannableString("     ");
            if(user.inventory.get(i).getCategoryName() != null){
                listName = new SpannableString(user.inventory.get(i).getCategoryName());
            }
            listName.setSpan(new RelativeSizeSpan(1.2f),0,listName.length(),0);
            menu.add(2, i, i, listName);
        }

        //Spannable strings are strings that allow a manipulation of color and size.
        SpannableString newList = new SpannableString("New Inventory List..."); //new string
        newList.setSpan(new ForegroundColorSpan(Color.GRAY), 0, newList.length(), 0);//change color to Gray
        newList.setSpan(new RelativeSizeSpan(1.2f),0,newList.length(),0);//make the font size bigger.
        /*The new list id will be -1 to differentiate from the real Categories*/
        menu.add(3, -1, 100, newList);
    }

    /**
     * the code to close the nav drawer
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Fill the ... settings menu
     *
     * @param menu The menu in the settings menu.
     * @return if menu inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * The actions performed when an item is pressed in the settings menu
     *
     * @param item the specific item pressed in the settings menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.hints) {
            startActivity(new Intent(this, HintActivity.class));

            Toast.makeText(MainActivity.this, "Hints Selected", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.edit_list){
           int position = -99;//default if there isn't a menu item
            //add the category to inventory
            position = currentCategory;
            if (position == 0){
                Toast.makeText(MainActivity.this, "Cannot edit/delete Shopping List", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, NewListActivity.class);
                intent.putExtra(POSC, Integer.toString(position));
                currentCategory = position;
                startActivityForResult(intent, 0);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This runs when the newlist activity is finished running, it checks if the list was
     * deleted or edited and changes the name and/or current selected list depending on the function
     *
     * @param requestCode Just 0, don't ask questions
     * @param resultCode I... I don't know
     * @param data Just stop reading
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Log.i(TAG, "Current list:" + currentCategory);
            if (listedited) {
                listedited = false;
                Log.i(TAG, "Editing list name " + currentCategory);
                this.setTitle(user.inventory.get(currentCategory).getCategoryName());
            }
            else if (isdeleting) {
                isdeleting = false;
                Log.i(TAG, "Deleting list " + currentCategory);
                if (currentCategory == 1) //Set shopping list adapter
                {
                    Log.i(TAG, "Moving to shopping list " + currentCategory);
                    adapterShopping.setID(0);
                    ID = 0;
                    lv.setAdapter(adapterShopping);
                    Collections.sort(MainActivity.user.inventory.get(0).items, new ShoppingCompare());
                    adapterShopping.notifyDataSetChanged();
                    this.setTitle(user.inventory.get(0).getCategoryName());
                    navigationView.getMenu().getItem(0).setCheckable(true);
                    currentCategory--;
                }
                else //Set normal adapter
                {
                    Log.i(TAG, "Moving to list " + currentCategory);
                    adapter = new ItemListAdapter(this, user.inventory.get(currentCategory - 1).items);
                    adapter.setID(currentCategory - 1);
                    ID = currentCategory - 1;
                    lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    this.setTitle(user.inventory.get(currentCategory - 1).getCategoryName());
                    navigationView.getMenu().getItem(currentCategory - 1).setCheckable(true);
                    currentCategory--;
                }
            }
            else if (newlistadded)
            {
                newlistadded = false;
                Log.i(TAG, "New list " + currentCategory);
                currentCategory = user.inventory.size() - 1;
                adapter = new ItemListAdapter(this, user.inventory.get(currentCategory).items);
                adapter.setID(currentCategory);
                ID = currentCategory;
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                this.setTitle(user.inventory.get(currentCategory).getCategoryName());
                navigationView.getMenu().getItem(currentCategory).setCheckable(true);
            }
            else //New list activity exited
            {
                Log.i(TAG, "New list activity exited " + currentCategory);
                if (user.inventory.size() == 1) //Set shopping list adapter
                {
                    Log.i(TAG, "Moving to shopping list " + currentCategory);
                    adapterShopping.setID(0);
                    ID = 0;
                    lv.setAdapter(adapterShopping);
                    Collections.sort(MainActivity.user.inventory.get(0).items, new ShoppingCompare());
                    adapterShopping.notifyDataSetChanged();
                    this.setTitle(user.inventory.get(0).getCategoryName());
                    navigationView.getMenu().getItem(0).setCheckable(true);
                    currentCategory = 0;
                }
                else {
                    Log.i(TAG, "Moving to list " + currentCategory);
                    currentCategory = user.inventory.size() - 1;
                    adapter = new ItemListAdapter(this, user.inventory.get(currentCategory).items);
                    adapter.setID(currentCategory);
                    ID = currentCategory;
                    lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    this.setTitle(user.inventory.get(currentCategory).getCategoryName());
                    navigationView.getMenu().getItem(currentCategory).setCheckable(true);
                }
            }
        }
    }

    /**
     * The actions performed when a specific category is pressed in the navigation drawer
     *
     * @param item The specific category pressed in the nav drawer
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ID = id;
        currentCategory = id;
        if (id == -1) {
            int position = -99;

            Intent intent = new Intent(MainActivity.this, NewListActivity.class);
            intent.putExtra(POSC, Integer.toString(position));
            startActivityForResult(intent, 0);
            //startActivity(new Intent(MainActivity.this, NewListActivity.class));
        }
        else{
            for (int i = 0; i < navigationView.getMenu().size(); i++){
                navigationView.getMenu().getItem(i).setChecked(false);
            }
            this.setTitle(user.inventory.get(id).getCategoryName());
            item.setCheckable(true);//leaves the list selected highlighted in the nav drawer
            //Set adapter to new category here
            //Shopping list is 0
            if (id > 0) { //Regular categories - ♠♣♥♦(Alex)♠♣♥♦
                adapter = new ItemListAdapter(this, user.inventory.get(id).items);
                adapter.setID(id);
                lv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            else //Shopping category - ♠♣♥♦(Alex)♠♣♥♦
            {
                adapterShopping.setID(id);
                lv.setAdapter(adapterShopping);
                Collections.sort(MainActivity.user.inventory.get(0).items, new ShoppingCompare());
                adapterShopping.notifyDataSetChanged();
            }

        }

        Toast.makeText(MainActivity.this, item.getTitle() + " Was Selected", Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * The actions preformed when the app stops.
     * Saves all of the user information to the device.
     */
    @Override
    protected void onStop() {
        Gson gson = new Gson();
        String json = gson.toJson(user);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.clear();
        edit.putString(USER, json);

        edit.apply();

        super.onStop();

    }
}
