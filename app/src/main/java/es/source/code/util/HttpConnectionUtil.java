package es.source.code.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.source.code.callback.OnDataFinishedListener;

/**
 * Created by sail on 2018/11/4.
 */

public class HttpConnectionUtil {

    private static Context mContext;

    public static class InstanceHolder{
        private static HttpConnectionUtil instance = new HttpConnectionUtil();
    }



    public static HttpConnectionUtil getInstance(Context Context){
        mContext = Context;
        return InstanceHolder.instance;
    }
    class HttpGetTask extends AsyncTask<String,Integer,String>{

        String urlString;

        public HttpGetTask(String urlString){
            this.urlString=urlString;
        }
        @Override
        protected String doInBackground(String... strings) {
            String result = Get(urlString);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (onDataFinishedListener!=null){
                if(s!=null){
                    onDataFinishedListener.onDataSuccess(s);
                }else{
                    onDataFinishedListener.onDataFail();
                }
            }
        }
    }

    class HttpPostTask extends AsyncTask<String,Integer,String>{

        String urlString;

        public HttpPostTask(String urlString){
            this.urlString=urlString;
        }
        @Override
        protected String doInBackground(String... strings) {
            String result = Post(urlString);
            //System.out.println("HttpPostTask: doInBackground: "+result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (onDataFinishedListener!=null){
                if(s!=null){
                    onDataFinishedListener.onDataSuccess(s);
                }else{
                    onDataFinishedListener.onDataFail();
                }
            }
        }
    }

    public static String Get(String urlString){
        try{
            int length=0;
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            URL url = new URL(urlString);
            //连接
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //Get
            connection.setRequestMethod("GET");
            //
            connection.connect();
            //响应码
            int responseCode = connection.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK){
                InputStreamReader isr = new InputStreamReader(
                        new BufferedInputStream(connection.getInputStream()),"utf-8");
                char[] buf = new char[1024];
                int c = 0;
                StringBuilder sb = new StringBuilder();
                while((c=isr.read(buf))!=-1){
                    sb = sb.append(new String(buf,0,c));
                    length+=c;
                }
                isr.close();
                System.out.println("Get&Post: "+sb.toString());
                System.out.println("Get&Post: 字符流长度为"+length);
                return sb.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String Post(String urlString){
        try{
            SharedPreferenceUtil spu = SharedPreferenceUtil.getInstance(mContext);
            String cookiesString = spu.getCookies();
            if(cookiesString==null){
                cookiesString=" ";
            }

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            URL url = new URL(urlString);
            //连接
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty("Cookie", cookiesString);
            //Post
            connection.setRequestMethod("POST");
            //

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.connect();
            //响应码

            int responseCode = connection.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK){
                InputStreamReader isr = new InputStreamReader(connection.getInputStream(),"utf-8");
                char[] buf = new char[1024];
                int c = 0;
                StringBuilder sb = new StringBuilder();
                while((c=isr.read(buf))!=-1){
                    String s = new String(buf,0,c);
                    Log.i("Get&Post: ",""+s);
                    sb = sb.append(s);
                }
                isr.close();
                //System.out.print("Get&Post: "+sb.toString());

                //获取cookie
                Map<String,List<String>> map=connection.getHeaderFields();
                System.out.println("Header: "+map.toString());
                Set<String> set=map.keySet();
                System.out.println("keyset: "+set.toString());
                Iterator itr = set.iterator();
                while(itr.hasNext()){
                    String key = (String)itr.next();
                    System.out.println("key: "+key);
                    if(key!=null&&key.equals("Set-Cookie")){
                        System.out.println("key=" + key+",开始获取cookie");
                        List<String> list = map.get(key);
                        StringBuilder builder = new StringBuilder();
                        for (String str : list) {
                            builder.append(str).toString();
                        }
                        cookiesString=builder.toString();
                        spu.setCookies(cookiesString);
                        System.out.println("cookie="+cookiesString);
                    }
                }
                if(cookiesString!=null){
                    spu.setCookies(cookiesString);
                }

                return sb.toString();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessageByGet(String urlString){
        HttpGetTask httpGetTask = new HttpGetTask(urlString);
        httpGetTask.execute();
    }

    public void sendMessageByPost(String urlString){
        HttpPostTask httpPostTask = new HttpPostTask(urlString);
        httpPostTask.execute();
    }



    OnDataFinishedListener onDataFinishedListener;

    public void setOnDataFinishedListener(OnDataFinishedListener onDataFinishedListener) {
        this.onDataFinishedListener = onDataFinishedListener;
    }

}
