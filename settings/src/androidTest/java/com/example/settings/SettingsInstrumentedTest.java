package com.example.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiCollection;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class SettingsInstrumentedTest {

    private static final String TAG = SettingsInstrumentedTest.class.getSimpleName();
    static final int LAUNCH_TIMEOUT = 5000;
    static final String SETTINGS_PACKAGE = "com.android.settings";
    private static final String STRING_TO_BE_TYPED = "Battery";
    private UiDevice mDevice;
    private final String[] mRecySettingsArray = new String[]{"Network & Internet", "Connected devices", "Apps & notifications" ,
            "Battery" , "Display" , "Sound" , "Storage" , "Security & location" , "Users & accounts" ,
            "Accessibility" , "Google" , "System"};

    @Before
    public void initSetup(){
        mDevice = UiDevice.getInstance(getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the settings app
        Context context = getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(SETTINGS_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        mDevice.wait(Until.hasObject(By.pkg(SETTINGS_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

    }

    /*@Test
    public void checkPreconditions() {
        assertThat(mDevice, notNullValue());
    }*/

    @Test
    public void testClickSearchView() throws InterruptedException, UiObjectNotFoundException {
//        mDevice.findObject(By.res(SETTINGS_PACKAGE, "search_action_bar"))
        mDevice.findObject(By.desc("Search settings"))
                .click();

        mDevice.wait(Until.hasObject(By.text("Search...")), LAUNCH_TIMEOUT);

        UiObject changedText = mDevice.findObject(new UiSelector().className("android.widget.EditText"));
        changedText.setText(STRING_TO_BE_TYPED);
        changedText.waitForExists(LAUNCH_TIMEOUT);
        mDevice.pressEnter();

        assertThat(changedText.getText(), is(equalTo(STRING_TO_BE_TYPED)));
    }

    @Test
    public void testNumberOfItemsIn_SettingContainer(){
        UiCollection lSettingsRecycler = new UiCollection(new UiSelector()
                .className("android.support.v7.widget.RecyclerView"));

        int count = 0;
        try {
            count = lSettingsRecycler.getChildCount();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Settings recycler count: "+count);
    }


    @Test
    public void testNumberOfItemsIn_SettingContainerFromJSON(){

        JSONObject jsonObj = convertXMLtoJSON();

        try {
            JSONObject jsonObjHierarchy = jsonObj.getJSONObject("hierarchy");
            JSONObject jsonObjNode = jsonObjHierarchy.getJSONObject("node");
            JSONArray jsonArrayNode = jsonObjNode.getJSONArray("node");

            JSONObject jsonObjNode1 = jsonArrayNode.getJSONObject(0)
                    .getJSONObject("node")
                    .getJSONObject("node")
                    .getJSONObject("node")
                    .getJSONObject("node")
                    .getJSONObject("node");

            JSONArray jsonArray2 = jsonObjNode1.getJSONArray("node");

            JSONObject jsonObjRecycler = jsonArray2.getJSONObject(1)
                    .getJSONObject("node");

            String resId = jsonObjRecycler.getString("resource-id");

            UiCollection lSettingsRecycler = new UiCollection(new UiSelector()
                    .resourceId(resId));


            int count = lSettingsRecycler.getChildCount();
            Log.d(TAG, "Settings recycler count from JSON: "+count);

        } catch (JSONException | UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testVisibleTextContent(){
        List<String> recyclerTitleList = new ArrayList<>();

        UiObject2 obj =  mDevice.findObject(By.res("com.android.settings:id/dashboard_container"));

        List<UiObject2> title1 = obj.findObjects(By.res("android:id/title"));

        for (UiObject2 object1 : title1){
            recyclerTitleList.add(object1.getText());
//            Log.d(TAG, "TITLE: "+ object1.getText());
        }

        UiScrollable recyclerView = new UiScrollable(new UiSelector()
                .className("android.support.v7.widget.RecyclerView"));
        try {
            recyclerView.scrollToEnd(3);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        UiObject2 obj2 =  mDevice.findObject(By.res("com.android.settings:id/dashboard_container"));

        List<UiObject2> title2 = obj2.findObjects(By.res("android:id/title"));

        for (UiObject2 object2 : title2) {
            if(!recyclerTitleList.contains(object2.getText())){
                recyclerTitleList.add(object2.getText());
            }
//            Log.d(TAG, "TITLE: "+ object2.getText());
        }

        for (String title : recyclerTitleList) {
            Log.d(TAG, "TITLE: "+ title);
        }

//        assertArrayEquals(mRecySettingsArray, recyclerTitleList.toArray());
    }



    @Test
    public void testScroll_ClickSystem(){
        UiScrollable recyclerView = new UiScrollable(new UiSelector()
                .className("android.support.v7.widget.RecyclerView"));
        try {
            recyclerView.scrollToEnd(3);
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        UiObject linLaySystem = mDevice.findObject(new UiSelector().text("System"));
        linLaySystem.waitForExists(100);
        try {
            linLaySystem.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        //        count number of items in Settings/System recycler
        UiCollection settingsDrawer = new UiCollection(new UiSelector()
                .resourceId("com.android.settings:id/list"));
        int count = 0;
        try {
            count = settingsDrawer.getChildCount();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        try {
            UiObject obj = settingsDrawer.getChild(new UiSelector().className("android.widget.LinearLayout"));
            int cc = obj.getChildCount();
            Log.d(TAG, "Settings/System recycler count: "+count + " child count: "+ cc);

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }



    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    public static String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    public JSONObject convertXMLtoJSON(){
        // Load XML for parsing.
        AssetManager assetManager = getApplicationContext().getAssets();
        InputStream inputStream;
        JSONObject jsonObject = null;
        try {
            inputStream = assetManager.open("dump_1757104218857796769.uix");
            XmlToJson xmlToJson = new XmlToJson.Builder(inputStream, null).build();
            inputStream.close();

//          convert to a JSONObject
            jsonObject = xmlToJson.toJson();

            String result = jsonObject.toString();

            Log.d(TAG, "JSON-->>  "+result);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return jsonObject;
    }
}
