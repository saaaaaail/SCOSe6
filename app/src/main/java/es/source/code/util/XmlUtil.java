package es.source.code.util;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import es.source.code.pullxml.FoodItem;
import es.source.code.pullxml.UpdateFood;

/**
 * Created by sail on 2018/11/5.
 */

public class XmlUtil {
    private XmlUtil(){}
    public static class InstanceHolder{
        private static XmlUtil instance = new XmlUtil();
    }

    public static XmlUtil getInstance(){
        return InstanceHolder.instance;
    }
    public UpdateFood parseXMLWithPull(String xmlData) throws Exception{
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlData));
        int eventType = parser.getEventType();
        UpdateFood updateFood = new UpdateFood();
        FoodItem foodItem = new FoodItem();
        List<FoodItem> foods = new ArrayList<>();
        String foodName = "";
        String price = "";
        String cate= "";
        String updatecount = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String nodeName = parser.getName();
            switch (eventType) {
                // 开始解析某个结点
                case XmlPullParser.START_TAG: {
                    if("updateCounts".equals(nodeName)){
                        updatecount = parser.nextText();
                    }else if("foodItemList".equals(nodeName)){

                    }else if("esd.scos.servlet.FoodItem".equals(nodeName)){

                    }else if("foodName".equals(nodeName)){
                        foodName = parser.nextText();
                        //Log.d("xml", "name is " + foodName);
                    }else if("price".equals(nodeName)){
                        price = parser.nextText();
                        //Log.d("xml", "price is " + price);
                    }else if("category".equals(nodeName)){
                        cate = parser.nextText();
                        //Log.d("xml", "category is " + cate);
                    }

                    break;
                }
                // 完成解析某个结点
                case XmlPullParser.END_TAG: {
                    if("updateCounts".equals(nodeName)){
                        updateFood.setUpdateCounts(updatecount);
                    } else if ("esd.scos.servlet.FoodItem".equals(nodeName)) {
                        foodItem.setFoodName(foodName);
                        foodItem.setPrice(price);
                        foodItem.setCategory(cate);
                        foods.add(foodItem);
                    }else if("foodItemList".equals(nodeName)){
                        updateFood.setFoodItemList(foods);
                    }
                    break;
                }
                default:
                    break;
            }
            eventType = parser.next();
        }
        return updateFood;
    }

}
