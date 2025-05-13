package com.example.foodshoptestcase.Helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.example.foodshoptestcase.Domain.ItemsModel;

import java.util.ArrayList;

public class ManagmentCart {
    private TinyDB tinyDB;
    private Context context;

    public ManagmentCart(Context context) {
        tinyDB = new TinyDB(context);
        this.context = context;
    }

    public void insertItems(ItemsModel item) {
        ArrayList<ItemsModel> listFood = getListCart();
        boolean existAlready = false;
        int index = -1;

        for (int i = 0; i < listFood.size(); i++) {
            if (listFood.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                index = i;
                break;
            }
        }

        if (existAlready) {
            listFood.get(index).setNumberInCart(item.getNumberInCart());
        } else {
            listFood.add(item);
        }
        tinyDB.putListObject("CartList", listFood);
        Toast.makeText(context, "Добавлено в корзину", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<ItemsModel> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public void minusItem(ArrayList<ItemsModel> listItems, int position, ChangeNumberItemsListener listener) {
        if (listItems.get(position).getNumberInCart() == 1) {
            listItems.remove(position);
        } else {
            listItems.get(position).setNumberInCart(listItems.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listItems);
        listener.onChanged();
    }

    public void plusItem(ArrayList<ItemsModel> listItems, int position, ChangeNumberItemsListener listener) {
        listItems.get(position).setNumberInCart(listItems.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItems);
        listener.onChanged();
    }

    public double getTotalFee() {
        ArrayList<ItemsModel> listFood = getListCart();
        double fee = 0.0;
        for (ItemsModel item : listFood) {
            fee += item.getPrice() * item.getNumberInCart();
        }
        return fee;
    }

    public void toggleFavorite(ItemsModel item) {
        ArrayList<ItemsModel> favoriteList = getFavoriteList();
        //boolean isFavorite = item.getLovers();
        int index = -1;

        for (int i = 0; i < favoriteList.size(); i++) {
            if (favoriteList.get(i).getTitle().equals(item.getTitle())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Элемент уже в избранном, удаляем его
            favoriteList.remove(index);
            item.setLovers(false);
            Toast.makeText(context, "Удалено из избранного", Toast.LENGTH_SHORT).show();
            Log.d("ManagmentCart", "Removed from favorites: " + item.getTitle());
        } else {
            // Элемент не в избранном, добавляем его
            item.setLovers(true);
            favoriteList.add(item);
            Toast.makeText(context, "Добавлено в избранное  ", Toast.LENGTH_SHORT).show();
            Log.d("ManagmentCart", "Added to favorites: " + item.getTitle());
        }

        tinyDB.putListObject("FavoriteList", favoriteList);
    }

    public ArrayList<ItemsModel> getFavoriteList() {
        ArrayList<ItemsModel> favoriteList = tinyDB.getListObject("FavoriteList");
        if (favoriteList == null) {
            Log.d("ManagmentCart", "Favorite list is null, returning empty list");
            favoriteList = new ArrayList<>();
        } else {
            Log.d("ManagmentCart", "Favorite list size: " + favoriteList.size());
        }
        return favoriteList;
    }
}