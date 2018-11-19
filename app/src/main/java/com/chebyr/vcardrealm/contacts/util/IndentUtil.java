package com.chebyr.vcardrealm.contacts.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class IndentUtil
{
    private static String TAG = IndentUtil.class.getSimpleName();

    private static String INTENT_VIEW ="android.intent.action.VIEW"; // Display the data to the user
    private static String INTENT_SEND = "android.intent.action.SEND"; // Deliver some data to someone else
    private static String INTENT_SENDTO = "android.intent.action.SENDTO"; //Send a message to someone specified by the data

    public static String WHATSAPP = "com.whatsapp";
    public static String GOOGLE_MAPS = "com.google.android.apps.maps";
    public static String ANDROID_DIALER = "com.android.dialer";
    public static String FACEBOOK_APP = "com.facebook.katana";
    public static String FACEBOOK_MESSENGER = "com.facebook.orca";
    public static String ANDROID_SMS = "com.google.android.apps.messaging";
    public static String ANDROID_CONTACTS = "com.android.contacts";
    public static String GMAIL = "com.google.android.gm";



    private Context context;
    private PackageManager packageManager;

    public IndentUtil(Context context)
    {
        this.context = context;
        packageManager = context.getPackageManager();
    }

    public void getDefaultApp()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));

        final ResolveInfo mInfo = packageManager.resolveActivity(intent, 0);
        ApplicationInfo applicationInfo = mInfo.activityInfo.applicationInfo;
        CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);
    }

    public void launchApplication(String packageName, String uriText)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");

            Uri uri = Uri.parse(uriText);

            PackageInfo info = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            intent.setPackage(packageName);

            intent.setData(uri);
            context.startActivity(Intent.createChooser(intent, "Send email"));

        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.d(TAG, "Package not Installed");
        }
    }

    public Intent getLaunchIntentForPackage(String packageName)
    {
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        Log.d(TAG, intent.getAction());
        return intent;
    }

    public Drawable getApplicationIcon (String packageName)
    {
        try
        {
            return packageManager.getApplicationIcon(packageName);
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
            return null;
        }
    }

    public void getPackageInfo()
    {
        List<PackageInfo> apps = packageManager.getInstalledPackages(0);

        ArrayList<AppInfo> res = new ArrayList<AppInfo>();
        for(int i=0;i<apps.size();i++)
        {
            PackageInfo packageInfo = apps.get(i);

            AppInfo appInfo = new AppInfo();
            appInfo.appname = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            appInfo.pname = packageInfo.packageName;
            appInfo.versionName = packageInfo.versionName;
            appInfo.versionCode = packageInfo.versionCode;
            appInfo.icon = packageInfo.applicationInfo.loadIcon(packageManager);

            Log.d(TAG, appInfo.pname);
            res.add(appInfo);
        }
    }

    public class AppInfo
    {
        String appname;
        String pname;
        String versionName;
        int versionCode;
        Drawable icon;
    }
}
