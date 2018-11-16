package es.source.code.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sail on 2018/11/4.
 */

public class UpdateFood {

    @SerializedName("更新菜品数量")
    public int updateCounts;

    @SerializedName("菜品列表")
    public List<FoodItem> foodItemList;


}
