package com.anton.dietpro.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.anton.dietpro.R;
import com.anton.dietpro.models.PFC;

import java.util.ArrayList;

/**
 * Created by Anton Vasilev on 02.11.16.
 */

public class Product extends Item {


    public static final String TABLE_NAME = "product";
    private PFC pfc; /// < Содержит БЖУ
    private Double weight; /// < масса продукта в граммах
    private String description; /// <описание продукта
    private String url; /// < url картинки
    private Integer nutritionId;

    public Product() {
        setInit();
    }

    public Product(String name) {
        setInit();
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
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getNutritionId() {
        return this.nutritionId;
    }

    public void setNutritionId(Integer nutritionId) {
        this.nutritionId = nutritionId;
    }

    /**
     * Расчет калорийности продукта
     *
     * @return Количество калорий в указанной массе продукта
     */
    public double getCalories()
    {

        return this.pfc!=null?(this.weight/100) * this.pfc.getCalories():0;
    }

    /**
     * Расчет калорийности продукта
     *
     * @return Количество калорий в указанной массе продукта
     */
    public double getCalories(Double weight) {
        this.setWeight(weight);
        return this.weight * this.pfc.getCalories();
    }

    public static ArrayList<Product> getProductList(Context context){
        return Product.getProductList(context,0,null,null);
    }

    public static Product getProductById(long id, Context context){
        return Product.getProductList(context,id,null,null).get(0);
    }


    public static ArrayList<Product> getSearchProductList(Context context, String query) {
        return Product.getProductList(context,0,query,null);
    }

    public static ArrayList<Product> getDietListSorted(Context applicationContext, boolean orderBy) {
        return orderBy?Product.getProductList(applicationContext,0,null,"name asc"):Product.getProductList(applicationContext,0,null,"name desc");
    }

    private void setInit() {
        this.id = 0;
        this.nutritionId = 0;
        this.pfc = new PFC();
        this.description = "";
        this.name = "";
        this.weight = new Double(0);
    }

    private static ArrayList<Product> getProductList(Context context, long id, String query, String orderBy) {
        ArrayList<Product> products = new ArrayList<>();
        DietDB dbHelper = DietDB.openDB(context);
        if (dbHelper.database == null){
            Toast.makeText(context,context.getString(R.string.errorDB),Toast.LENGTH_SHORT).show();
            return null;
        }

        String selection = null;
        String[] selectionArgs = null;
        String limit = null;
        if(query != null ){
            if(!query.isEmpty()) {
                selection = "name like ?";
                selectionArgs = new String[]{"%"+query+"%"};
                limit ="10";
            }
        }
        else if(id>0){
            selection = "id = ?";
            selectionArgs = new String[]{String.valueOf(id)};
            limit = "1";
        }

        Cursor c = dbHelper.database.query(true,DietDB.TABLE_PRODUCT,new String[]{"id","name","protein","fat","carbohydrate","img"}
                ,selection,selectionArgs,null,null,orderBy,limit);
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
            Product emptyProduct = new Product(context.getString(R.string.listProductsNotFound));
            products.add(emptyProduct);
        }
        c.close();
        dbHelper.close();
        return products;
    }

}


