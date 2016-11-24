package com.anton.dietpro.models;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.anton.dietpro.models.PFC;

import java.util.ArrayList;

/**
 * Created by Anton Vasilev on 02.11.16.
 */

public class Product {


    public static final String TABLE_NAME = "product";
    private int id;
    private PFC pfc; /// < Содержит БЖУ
    private String name; /// < Название продукта
    private Double weight; /// < масса продукта в граммах
    private String description; /// <описание продукта
    private String url; /// < url картинки

    public Product() {
        this.id = 0;
    }

    public Product(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Double getWeight() {
        return this.weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public PFC getPfc() {
        return pfc;
    }

    public void setPfc(PFC pfc) {
        this.pfc = pfc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Расчет калорийности продукта
     *
     * @return Количество калорий в указанной массе продукта
     */
    public double getCalories()
    {
        return this.weight * pfc.getCalories();
    }
    /**
     * Расчет калорийности продукта
     *
     * @return Количество калорий в указанной массе продукта
     */
    public double getCalories(Double weight) {
        this.setWeight(weight);
        return this.weight * pfc.getCalories();
    }

    public static ArrayList<Product> getProductList(Context context){
        ArrayList<Product> products = new ArrayList<>();
        DietDB dbHelper = new DietDB(context);
        dbHelper.create_db();
        dbHelper.open();
        if (dbHelper.database == null){
            Toast.makeText(context,"Нет подключения к БД",Toast.LENGTH_SHORT).show();
            return null;
        }
        Cursor c = dbHelper.database.rawQuery("select * from " + DietDB.TABLE_PRODUCT,null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int proteinColIndex = c.getColumnIndex("protein");
            int fatColIndex = c.getColumnIndex("fat");
            int carbohydrateColIndex = c.getColumnIndex("carbohydrate");
            int urlColIndex = c.getColumnIndex("img");

            do {
                Product product = new Product();
                product.setId(Integer.valueOf(c.getString(idColIndex)));
                product.setName(c.getString(nameColIndex));
                product.setPfc(new PFC(c.getDouble(proteinColIndex), c.getDouble(fatColIndex)
                                , c.getDouble(carbohydrateColIndex)));
                product.setUrl(c.getString(urlColIndex));
                products.add(product);
            } while (c.moveToNext());
        } else {
            Product emptyProduct = new Product("Продукты не найдены");
            products.add(emptyProduct);
        }
        c.close();
        dbHelper.close();
        return products;
    }

    public static Product getProductById(long id, Context context){
        Product product;
        DietDB dbHelper = new DietDB(context);
        dbHelper.create_db();
        dbHelper.open();
        if (dbHelper.database == null){
            Toast.makeText(context,"Нет подключения к БД",Toast.LENGTH_SHORT).show();
            return null;
        }
        Cursor c = dbHelper.database.rawQuery("select * from " + DietDB.TABLE_PRODUCT
                + " where id = " + id + " limit 1",null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int proteinColIndex = c.getColumnIndex("protein");
            int fatColIndex = c.getColumnIndex("fat");
            int carbohydrateColIndex = c.getColumnIndex("carbohydrate");
            int urlColIndex = c.getColumnIndex("img");

            product = new Product();
            product.setId(Integer.valueOf(c.getString(idColIndex)));
            product.setName(c.getString(nameColIndex));
            product.setPfc(new PFC(c.getDouble(proteinColIndex), c.getDouble(fatColIndex)
                    , c.getDouble(carbohydrateColIndex)));
            product.setUrl(c.getString(urlColIndex))
            ;
        } else {
            product = new Product("Продукты не найдены");
        }
        c.close();
        dbHelper.close();
        return product;
    }




}

