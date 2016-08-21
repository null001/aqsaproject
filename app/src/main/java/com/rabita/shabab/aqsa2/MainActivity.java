package com.rabita.shabab.aqsa2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;


import android.provider.Settings.Secure;




public class MainActivity extends ActionBarActivity implements SwipyRefreshLayout.OnRefreshListener {

    public static String TAG="AqsaProject";
    Context applicationContext;
    GoogleCloudMessaging gcmObj;
    public String regId="";
    public static final String REG_ID="regId";

    SharedPreferences storage;
    SharedPreferences dataStorage;
    SharedPreferences contentStorage;
    SharedPreferences imageStorage;
    SharedPreferences dateStorage;
    SharedPreferences idStorage;

    public SharedPreferences.Editor editor;
    public String devName;
    public String devId;
    ProgressDialog progDialog;


    private SwipyRefreshLayout swipeRefreshLayout;
    private List<String> contents = new ArrayList<String>(10);
    private List<String> images = new ArrayList<String>(10);
    private List<String> dates = new ArrayList<String>(10);
    private List<String> headerArray = new ArrayList<String>(10);
    private List<String> ids = new ArrayList<String>(10);



    private String contentsString;
    private String headersString;
    private String imagesString;
    private String datesString;
    private String idsString;

    custom_adapter adapter;


    ListView list;
    Gson gson;

    int oldArticleFlag = 1;
    int getOldArticleNum = 10;




    //navigation drawer
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private String [] navigationItlemTitle;
    private ArrayList<navDrawerItem> navDrawerItems;
    private navListAdapter navAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.applicationContext=getApplicationContext();
        this.storage = getSharedPreferences("User", Context.MODE_PRIVATE);
        this.dataStorage = getSharedPreferences("ArticlesData",Context.MODE_PRIVATE);
        this.devName = Build.MODEL;
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        this.devId = Secure.getString(applicationContext.getContentResolver(),Secure.ANDROID_ID);



        this.progDialog = new ProgressDialog(this);
        this.progDialog.setMessage("please wait...");
        this.progDialog.setCancelable(false);

        Log.d(TAG,"in onCreate");
        //unregister();
        String storedId=this.storage.getString(this.REG_ID,"");
        //Log.d(TAG,"storageId = "+storageId);
        this.list = (ListView) findViewById(R.id.mainList);


        if(TextUtils.isEmpty(storedId)){
            //Log.d(TAG,"id is not registered");
            registerInBackground();
        }else
        {
         //   Log.d(TAG,"id is "+storedId);
        }

        drawerList =(ListView) findViewById(R.id.nav_drawer);
        navigationItlemTitle = getResources().getStringArray(R.array.navigation_drawer_items);
        navDrawerItems = new ArrayList<navDrawerItem>();
        addItemsToNav();
        navAdapter = new navListAdapter(applicationContext,navDrawerItems);

        drawerList.setAdapter(navAdapter);

        swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.scrollView);
        swipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        swipeRefreshLayout.setOnRefreshListener(this);

        this.adapter = new custom_adapter(applicationContext,headerArray,contents,dates);
        this.list.setAdapter(this.adapter);
        gson = new Gson();



    }

    void addItemsToNav()
    {
        int size = navigationItlemTitle.length;
        for (int i=0 ;i<size;i++)
        {
            navDrawerItems.add(new navDrawerItem(navigationItlemTitle[i]));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG,"option menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d(TAG,"in onOptionsItemSelected");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkGoogleServices()
    {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();
            } else {
                Toast.makeText(
                        applicationContext,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }

        } else {
           /* Toast.makeText(
                    applicationContext,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();*/
        }
    }

    private void registerInBackground()
    {
        Log.d(TAG,"in registerInBackground");
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                Log.d(TAG,"in doInBackground");

                String msg="";
                if(gcmObj==null)
                {
                    gcmObj=GoogleCloudMessaging.getInstance(applicationContext);
                }

                try {
                    MainActivity.this.regId = gcmObj.register(ApplicationConstants.APPID);

                    msg=MainActivity.this.regId;
                }catch (IOException e){
                    msg="Error-1-:"+e.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                super.onPostExecute(msg);
                Log.d(TAG,"in onPostExecute");
                if(regId !=null) {
                    if (storeRegIDInLocal(MainActivity.this.regId)) {
                        Toast.makeText(applicationContext, "regId saved Correctly", Toast.LENGTH_LONG);
                        Log.d(TAG, "saved correctly");
                    } else {
                        Log.d(TAG, "error in storing regId");

                    }
                }else
                {
                    Log.d(TAG,"ERROR: GCM does not register the device ");
                }
            }


        }.execute();


    }

    private boolean storeRegIDInLocal(String data)
    {
        Log.d(TAG,"in storeRegIDInLocal");
        this.editor = this.storage.edit();
        this.editor.putString(this.REG_ID,data);
        if(this.editor.commit()){
            StoreIdinServer();
            return true;
        }
        StoreIdinServer();
        return false;

    }

    private void StoreIdinServer()
    {

        Log.d(TAG,"in StoreIdinServer");

        RequestParams params = new RequestParams();
        params.put(MainActivity.this.REG_ID,MainActivity.this.regId);
        params.add("devName",MainActivity.this.devName);
        params.add("devId", MainActivity.this.devId);
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG,"registerId= "+MainActivity.this.regId);
        client.post(ApplicationConstants.SERVER_URL,params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG,"Success Response from server: "+new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(applicationContext,
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Requested resource not found");
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(applicationContext,
                            "Something went wrong at server end",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Something went wrong at server end");
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(
                            applicationContext,
                            "Unexpected Error occcured! [Most common Error: Device might "
                                    + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Unexpected Error occcured! [Most common Error: Device might "
                            + "not be connected to Internet or remote server is not up and running], check for other errors as well");
                    Log.d(TAG,"code = "+statusCode);
                }
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection)
    {
        //requestArticles();
        Log.d(TAG,swipyRefreshLayoutDirection.toString());
        if(swipyRefreshLayoutDirection.toString().equals("TOP"))
        {
            requestArticles();
        }else
        {
            if(!this.contents.isEmpty()) {
                getOldArticleNum = getOldArticleNum * oldArticleFlag;
                requestOldArticles();
                oldArticleFlag++;
            }
            else
            {
                requestArticles();
            }

        }

    }

    private boolean unregister()
    {
        Log.d(TAG,"in unregister");
        if(this.storage.edit().remove(this.REG_ID).commit())
            return true;
        else
            return false;
    }
    // not used
    private boolean checkGoogleServicesFlag()
    {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;

        } else {
           return true;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //Log.i(TAG,"in onDestroy:"+unregister());
    }
    private void requestArticles()
    {

        String ArticleId =gson.toJson(MainActivity.this.ids);

        Log.d(TAG," "+ArticleId);

        RequestParams params = new RequestParams();
        params.put("article", "true");
        params.put("id", ArticleId);
        params.put("articleNum",11);


        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.SERVER_URL_ARTICLE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Log.d(TAG,new String(responseBody));

                try {

                    JSONObject data = new JSONObject(new String(responseBody));
                    JSONArray jsonArray = data.getJSONArray("articles");
                    String header;
                    String content;
                    String image;
                    String date;
                    String id;
                    String onlyContent;

                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {

                        content = jsonArray.getJSONObject(i).getString("content");
                        image = jsonArray.getJSONObject(i).getString("image");
                        date = jsonArray.getJSONObject(i).getString("time");
                        id = jsonArray.getJSONObject(i).getString("id");
                        header = utility.getHeader(content);
                        onlyContent = utility.getContent(content);

                        //Log.d(TAG,"id is : "+id);
                        MainActivity.this.headerArray.add(header);
                        MainActivity.this.contents.add(onlyContent);
                        MainActivity.this.images.add(image);
                        MainActivity.this.dates.add(date);
                        MainActivity.this.ids.add(id);

                    }

                    //Log.d(TAG,"in request Article header: "+headerArray.toString());

                    //Log.d(TAG,"in request Article content: "+contents.toString());
                    //Log.d(TAG, "in request Article image: " + images.toString());
                    MainActivity.this.adapter.notifyDataSetChanged();

                    //MainActivity.this.list.setAdapter(new custom_adapter(applicationContext,headerArray,contents,dates));

                    MainActivity.this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Log.d(TAG,"item Clicked");
                            itemClicked(position);

                        }
                    });

                } catch (JSONException e) {
                    Log.d(TAG,"response body "+new String(responseBody));
                    e.printStackTrace();
                }

                //storeArticles();

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(applicationContext,
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(applicationContext,
                            "Something went wrong at server end",
                            Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(
                            applicationContext,
                            "Unexpected Error occcured! [Most common Error: Device might "
                                    + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                            Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }


            }
        });
    }
    private void requestOldArticles()
    {
        String lastId = this.ids.get(ids.size()-1);
        RequestParams params = new RequestParams();
        params.put("oldArticle", "true");
        params.put("articleNum",getOldArticleNum);
        params.put("lastId",lastId);

        Log.d(TAG,"id before request");


        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.SERVER_URL_OLD_ARTICLE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Log.d(TAG,new String(responseBody));

                try {
                    JSONObject data = new JSONObject(new String(responseBody));
                    JSONArray jsonArray = data.getJSONArray("articles");
                    Log.d(TAG,"number or article "+jsonArray.length());
                    String header;
                    String content;
                    String image;
                    String date;
                    String id;
                    String onlyContent;

                    for (int i = 0; i < jsonArray.length(); i++) {

                        content = jsonArray.getJSONObject(i).getString("content");
                        image = jsonArray.getJSONObject(i).getString("image");
                        date = jsonArray.getJSONObject(i).getString("time");
                        id = jsonArray.getJSONObject(i).getString("id");
                        header = utility.getHeader(content);
                        onlyContent = utility.getContent(content);

                        //Log.d(TAG,"id is : "+id);
                        MainActivity.this.headerArray.add(header);
                        MainActivity.this.contents.add(onlyContent);
                        MainActivity.this.images.add(image);
                        MainActivity.this.dates.add(date);
                        MainActivity.this.ids.add(id);

                    }

                    //Log.d(TAG,"in request Article header: "+headerArray.toString());

                    //Log.d(TAG,"in request Article content: "+contents.toString());
                    //Log.d(TAG, "in request Article image: " + images.toString());
                    MainActivity.this.adapter.notifyDataSetChanged();

                    MainActivity.this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Log.d(TAG,"item Clicked");
                            itemClicked(position);

                        }
                    });

                } catch (JSONException e) {
                    Log.d(TAG,"response body "+new String(responseBody));
                    //e.printStackTrace();
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(applicationContext,
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(applicationContext,
                            "Something went wrong at server end",
                            Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(
                            applicationContext,
                            "Unexpected Error occcured! [Most common Error: Device might "
                                    + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                            Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }


            }
        });
    }
    private void storeArticles()
    {
        if (!this.contents.isEmpty())
        {
            int size = this.contents.size();
            ArrayList<String> contentsTemp = new ArrayList<String>(10);
            ArrayList<String> headerTemp = new ArrayList<>(10);
            ArrayList<String> imagesTemp = new ArrayList<String>(10);
            ArrayList<String> datesTemp = new ArrayList<>(10);
            ArrayList<String> idsTemp = new ArrayList<>(10);
            Log.d(TAG, "content Length " + size);

            for (int i = 0; i < size; i++) {

                contentsTemp.add(i, contents.get(i));
                headerTemp.add(i, headerArray.get(i));
                imagesTemp.add(i, images.get(i));
                datesTemp.add(i, dates.get(i));
                idsTemp.add(i, ids.get(i));

            }
            this.contents.clear();
            this.contents.addAll(contentsTemp);
            contentsTemp = null;

            this.headerArray.clear();
            this.headerArray.addAll(headerTemp);
            headerTemp = null;

            this.images.clear();
            this.images.addAll(imagesTemp);
            imagesTemp = null;

            this.dates.clear();
            this.dates.addAll(datesTemp);
            datesTemp = null;

            this.ids.clear();
            this.ids.addAll(idsTemp);
            idsTemp = null;


            this.contentsString = gson.toJson(this.contents);
            this.headersString = gson.toJson(this.headerArray);
            this.imagesString = gson.toJson(this.images);
            this.datesString = gson.toJson(this.dates);
            this.idsString = gson.toJson(this.ids);
            Log.d(TAG, "id String before storing" + this.idsString);

            this.editor = this.dataStorage.edit();
            this.editor.putString("headers", headersString);
            this.editor.commit();

            this.imageStorage = getSharedPreferences("imageData", Context.MODE_PRIVATE);
            this.editor = imageStorage.edit();
            this.editor.putString("images", imagesString);
            this.editor.commit();

            this.contentStorage = getSharedPreferences("contentData", Context.MODE_PRIVATE);
            this.editor = contentStorage.edit();
            this.editor.putString("contents", contentsString);
            this.editor.commit();

            this.dateStorage = getSharedPreferences("dateData", Context.MODE_PRIVATE);
            this.editor = this.dateStorage.edit();
            this.editor.putString("dates", datesString);
            this.editor.commit();

            this.idStorage = getSharedPreferences("idData", Context.MODE_PRIVATE);
            this.editor = this.idStorage.edit();
            this.editor.putString("id", idsString);


            if (this.editor.commit())
                Log.d(TAG, "stored successfully");
            else
                Log.d(TAG, "Faild in ");
            //Log.d(TAG,"testData"+this.dataStorage.getString("headers",""));
        }
    }
    private void retrieveData()
    {
        String error = new String();

        this.dataStorage = getSharedPreferences("ArticlesData",Context.MODE_PRIVATE);
        this.headersString = this.dataStorage.getString("headers",error);

        this.imageStorage = getSharedPreferences("imageData",Context.MODE_PRIVATE);
        this.imagesString = this.imageStorage.getString("images", error);

        this.contentStorage = getSharedPreferences("contentData",Context.MODE_PRIVATE);
        this.contentsString = this.contentStorage.getString("contents", error);

        this.dateStorage = getSharedPreferences("dateData",Context.MODE_PRIVATE);
        this.datesString = this.dateStorage.getString("dates",error);

        this.idStorage = getSharedPreferences("idData", Context.MODE_PRIVATE);
        this.idsString = this.idStorage.getString("id",error);

        //this.datesString = this.dataStorage.getString("dates", error);

       /* Log.d(TAG,"header String after storing : "+headersString+" error is : "+error);
        Log.d(TAG,"imageString String after storing : "+imagesString+" error is : "+error);
        Log.d(TAG,"content String after storing : "+contentsString+" error is : "+error);
        Log.d(TAG,"ID String after storing : "+idsString+" error is : "+error);
        Log.d(TAG,"date String after storing : "+datesString+" error is : "+error);*/


        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        if(gson.fromJson(this.headersString,type) != null)
            this.headerArray = gson.fromJson(this.headersString,type);
        if(gson.fromJson(this.contentsString,type) != null)
            this.contents = gson.fromJson(this.contentsString,type);
        if(gson.fromJson(this.imagesString,type) != null)
            this.images = gson.fromJson(this.imagesString,type);
        if(gson.fromJson(this.datesString,type) != null)
            this.dates = gson.fromJson(this.datesString,type);
        if(gson.fromJson(this.idsString,type) != null)
            this.ids = gson.fromJson(this.idsString,type);




        //Log.d(TAG, "stored data: " + this.dates.toString());
        this.adapter = new custom_adapter(applicationContext,headerArray,contents,dates);
        this.list.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();

        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClicked(position);
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        clearDate();
        storeArticles();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

         checkGoogleServices();
        //retrieveData();
    }

    private void clearDate()
    {
        this.storage = getSharedPreferences("ArticlesData",Context.MODE_PRIVATE);
        this.storage.edit().clear().commit();
    }

    public void clearData2(View view)
    {
        this.storage = getSharedPreferences("ArticlesData",Context.MODE_PRIVATE);
        this.storage.edit().clear().commit();
        this.contentStorage = getSharedPreferences("contentData",Context.MODE_PRIVATE);
        this.contentStorage.edit().clear().commit();
        this.imageStorage = getSharedPreferences("imageData",Context.MODE_PRIVATE);
        this.imageStorage.edit().clear().commit();
        this.dateStorage = getSharedPreferences("dateData",Context.MODE_PRIVATE);
        this.dateStorage.edit().clear().commit();
        this.idStorage = getSharedPreferences("idData",Context.MODE_PRIVATE);
        this.idStorage.edit().clear().commit();
        this.headerArray.clear();
        this.images.clear();
        this.contents.clear();
        this.dates.clear();
        this.ids.clear();

        Log.d(TAG,"data is cleared");

    }

    private void itemClicked(int position)
    {
        Log.d(TAG,"contents : "+contents+" positions : "+position);
        String content = this.contents.get(position);
        String image = this.images.get(position);
        String date = this.dates.get(position);
        Intent intent = new Intent(applicationContext, Article_activity.class);
        intent.putExtra("content", content);
        intent.putExtra("image", image);
        intent.putExtra("date", date);

        startActivityForResult(intent, 0);
    }


}
