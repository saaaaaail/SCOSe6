package es.source.code.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sail on 2018/11/4.
 */


public class ResultM {

    @SerializedName("RESULTCODE")
    public int resultcode;

    @SerializedName("MESSAGE")
    public String message;
}
