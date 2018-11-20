package com.example.settings;


import android.content.Context;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.example.settings.SettingsInstrumentedTest.LAUNCH_TIMEOUT;
import static com.example.settings.SettingsInstrumentedTest.SETTINGS_PACKAGE;
import static com.example.settings.SettingsInstrumentedTest.getLauncherPackageName;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class BatterySettingsTest {

    private static final int TIMEOUT = 1000;
    private UiDevice mDevice;


    @Before
    public void setup() {
//        Instantiate a UiDevice object which will represent the current state of the smartphone’s screen.
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();
        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the setting app
        Context context = getApplicationContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(SETTINGS_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        mDevice.wait(Until.hasObject(By.pkg(SETTINGS_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void shouldVerifyBatteryNotification() throws UiObjectNotFoundException {
        // * UiScrollable is a  UiCollection and provides support for searching
        // * for items in scrollable layout elements.
        UiScrollable settingList = new UiScrollable(new UiSelector().scrollable(true)
                .className("android.support.v7.widget.RecyclerView"));
        settingList.scrollTextIntoView("Battery");
        mDevice.findObject(By.text("Battery")).click();
        mDevice.wait(Until.hasObject(By.desc("Battery saver")), TIMEOUT);
        mDevice.findObject(By.text("Battery saver")).click();
        mDevice.wait(Until.hasObject(By.text("OFF")), TIMEOUT);
        mDevice.findObject(By.text("OFF")).click();
        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.text("Battery saver is on")), TIMEOUT);
        UiObject2 notificationTitle = mDevice.findObject(By.text("Battery saver is on"));
        assertEquals(notificationTitle.getText(), "Battery saver is on");
        mDevice.findObject(By.text("Turn off battery saver")).click();
        mDevice.wait(Until.hasObject(By.text("OFF")), TIMEOUT);
        UiObject2 status = mDevice.findObject(By.text("OFF"));
        assertEquals("OFF", status.getText());
    }

    @After
    public void tearDown() {
        mDevice.pressBack();
    }
 }
