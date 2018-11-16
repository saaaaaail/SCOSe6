package es.source.code.event;

import es.source.code.model.FoodContent;

/**
 * Created by sail on 2018/11/1.
 */

public class EventMessage {
    private int code;
    private FoodContent foodContent;

    public FoodContent getFoodContent() {
        return foodContent;
    }

    public void setFoodContent(FoodContent foodContent) {
        this.foodContent = foodContent;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
