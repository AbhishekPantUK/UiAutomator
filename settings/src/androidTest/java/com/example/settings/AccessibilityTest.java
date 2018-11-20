package com.example.settings;


import android.content.Context;
import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.settings.SettingsInstrumentedTest.LAUNCH_TIMEOUT;
import static com.example.settings.SettingsInstrumentedTest.SETTINGS_PACKAGE;
import static com.example.settings.SettingsInstrumentedTest.getLauncherPackageName;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class AccessibilityTest {

    private UiDevice mDevice;
    private static final int TIMEOUT = 1000;

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

    @Test
    public void verifyAccessbilityService_test() throws UiObjectNotFoundException {
        UiScrollable settingList = new UiScrollable(new UiSelector().scrollable(true)
                .className("android.support.v7.widget.RecyclerView"));
        settingList.scrollTextIntoView("Accessibility");
        mDevice.findObject(By.text("Accessibility")).click();

        mDevice.wait(Until.hasObject(By.desc("Accessibility shortcut")), TIMEOUT);
        mDevice.findObject(By.text("Accessibility shortcut")).click();
        mDevice.wait(Until.hasObject(By.desc("Off")), TIMEOUT);
        UiObject2 switchBar = mDevice.findObject(By.text("switch_bar"));
        switchBar.click();
        assertEquals(switchBar.getText(), "ON");
    }

}
