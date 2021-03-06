package com.example.storagemaster.storagemaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.storagemaster.storagemaster.MainActivity.POSC;

/**
 * Create a new category or edit an existing category
 *
 *
 */
public class NewListActivity extends AppCompatActivity {

    private static final String TAG = "NewListActivity";
    Button saveButton;
    Button deleteButton;

    EditText listName;

    // this variable is to get the correct category from the inventory
    private int catPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = .06f;

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(height*.8));
        getWindow().setSoftInputMode(20);
        Log.d(TAG, "changed display size");

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.50f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);


        // initialize the edit text'
        listName = findViewById(R.id.editText);

        // initialize the buttons
        saveButton = findViewById(R.id.button2);
        deleteButton = findViewById(R.id.button);

        catPosition = Integer.parseInt(getIntent().getStringExtra(POSC));

        if(catPosition > -1){
            listName.setText(MainActivity.navigationView.getMenu().getItem(catPosition).getTitle());
            this.setTitle("Edit List");
        }

        //set on click listeners
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Menu menu = MainActivity.navigationView.getMenu(); //access to the nav drawer menu

                if (catPosition > 0) {
                    listName = findViewById(R.id.editText);
                    MainActivity.user.inventory.get(catPosition).setCategoryName(listName.getText().toString());
                    MainActivity.listedited = true;
                }
                else{
                    if (listName.toString() == null) {
                        Toast.makeText(getApplicationContext(), "No Name Entered!", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Category newCategory = new Category();
                        newCategory.setCategoryName(listName.getText().toString());
                        MainActivity.user.inventory.add(newCategory);
                        MainActivity.newlistadded = true;
                        Log.d(TAG, "list added to inventory");
                    }
                }
                MainActivity.addNavDrawerItems(menu);

                Log.d(TAG, "addNavDrawerItems called");
                if(catPosition > -1)
                    menu.getItem(catPosition).setChecked(true);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Menu menu = MainActivity.navigationView.getMenu(); //access to the nav drawer menu

                if (catPosition > 0) {
                    MainActivity.user.inventory.remove(catPosition);
                    menu.removeItem(catPosition);
                    MainActivity.isdeleting = true;
                }

                MainActivity.addNavDrawerItems(menu);
                Log.d(TAG, "" + catPosition);
                finish();
            }
        });
    }
}
