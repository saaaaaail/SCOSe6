package es.source.code.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sail on 2018/11/5.
 */

public class FoodItem {

    @SerializedName("菜品名称")
    public String foodName;

    @SerializedName("菜品价格")
    public int price;

    @SerializedName("菜品类型")
    public int category;
}
