package com.example.storagemaster.storagemaster;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * Created by Alex on 3/2/2018.
 * Stores the user information, a password and name
 * and a list of categories called "inventory"
 */
public class User {
    private String name;
    private String password;
    public ArrayList<Category> inventory;

    private static final String TAG = "User";

    public User(){
        name = "";
        password = "";
        inventory = new ArrayList<>();
    }

    /**
     * originally used to get the shopping list from the other lists.
     *
     * @return returns the shopping list.
     */
    public ArrayList<Item> getShoppingList() {
        ArrayList<Item> list = inventory.get(0).items; //Main list that we'll return
        //Searching through the categories
        for (int i = 0; i < inventory.size(); i++) {
            ArrayList<Item> tempItems = inventory.get(i).items; //Items we'll look through
            //Searching through the item list
            for (int j = 0; j < tempItems.size(); j++) {
                //If the quantity is less than or equal to the desired quantity
                /*if (tempItems.get(j).getQuantity() <= tempItems.get(j).getMin()) {
                    list.add(tempItems.get(j));
                }*/
            }
        }
        Collections.sort(list, new ShoppingCompare());
        return list;
    }

    /**
     * Can be used to add a category. Checks to make sure it doesn't exist.
     *
     * @param name The new category's name.
     * @param context the context
     * @return true if the category was added.
     */
    public boolean addCategory(String name, Context context) {
        boolean itemFound = false;
        //Logic that ensures the category isn't already in the list
        for (int i = 0; i < inventory.size(); i++) {
            if (Objects.equals(inventory.get(i).getCategoryName(), name)) {
                itemFound = true;
                Toast.makeText(context, "Category name already in list - Cannot Add", Toast.LENGTH_SHORT).show();
                Log.i("User1", "User attempted to make duplicate category: " + name);
                return false;
            }
        }
        if (!itemFound) {
            Category category = new Category();
            category.setCategoryName(name);
            Collections.sort(inventory);
            Toast.makeText(context, "Category successfully added to list", Toast.LENGTH_SHORT).show();
            Log.i("User1", "User added a valid category: " + name);
        }
        return true;
    }

    /**
     * Can be used to remove a category.
     *
     * @param name the category name.
     * @param context just the context.
     * @return true if the category existed and has been removed, otherwise false.
     */
    public boolean removeCategory(String name, Context context) {

        boolean itemFound = false;
        //Logic that searches for the item in the list
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getCategoryName() == name) {
                inventory.remove(inventory.get(i));
                itemFound = true;
                Toast.makeText(context, "Item successfully removed", Toast.LENGTH_SHORT).show();
                Log.i("User1", "User successfully removed category: " + name);
                return true;
            }
        }
        if (!itemFound) {
            Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT).show();
            Log.e("User1", "User attempted to remove an item that doesn't exist: " + name);
        }
        return false;
    }

    /**
     * Was going to be used to move an item from one category to another
     * @param name
     * @param categoryFrom
     * @param categoryTo
     * @param context
     * @return
     */
    public boolean moveItem(String name, String categoryFrom, String categoryTo, Context context) {
        boolean itemFound = false;
        Category cFrom = null;
        Category cTo;
        //Logic that searches for the item in the list
        for (int i = 0; i < inventory.size() && !itemFound; i++) {
            if (inventory.get(i).getCategoryName() == categoryFrom) {
                cFrom = inventory.get(i);
                itemFound = true;
            }
        }

        if (!itemFound) {
            Toast.makeText(context, "1st category not found", Toast.LENGTH_SHORT).show();
            Log.e("User1", "User attempted to move an item from a category that doesn't exist: " + categoryFrom);
            return false;
        }

        itemFound = false;
        for (int i = 0; i < inventory.size() && !itemFound; i++) {
            if (inventory.get(i).getCategoryName() == categoryTo) {
                cTo = inventory.get(i);
                itemFound = true;
            }
        }
        if (!itemFound) {
            Toast.makeText(context, "2nd category not found", Toast.LENGTH_SHORT).show();
            Log.e("User1", "User attempted to move an item to a category that doesn't exist: " + categoryTo);
            return false;
        }

        itemFound = false;

        for (int i = 0; i < cFrom.items.size() && !itemFound; i++) {
            if (cFrom.items.get(i).getItemName().toString() == name) {
                cFrom = inventory.get(i);
                itemFound = true;
            }
        }
        if (!itemFound) {
            Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT).show();
            Log.e("User1", "User attempted to move an item that doesn't exist: " + name);
            return false;
        }

        return true;
    }

}
