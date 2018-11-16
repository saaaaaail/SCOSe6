package es.source.code.pullxml;

import java.util.List;



/**
 * Created by sail on 2018/11/4.
 */

public class UpdateFood {

    private String updateCounts;

    private List<FoodItem> foodItemList;

    public String getUpdateCounts() {
        return updateCounts;
    }

    public void setUpdateCounts(String updateCounts) {
        this.updateCounts = updateCounts;
    }

    public List<FoodItem> getFoodItemList() {
        return foodItemList;
    }

    public void setFoodItemList(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
    }
}
