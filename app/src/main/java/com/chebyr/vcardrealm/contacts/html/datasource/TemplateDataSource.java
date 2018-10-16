package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.ItemKeyedDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.data.Contact;
import com.chebyr.vcardrealm.contacts.html.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class TemplateDataSource extends ItemKeyedDataSource<Integer, TemplateData>
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private static String assetsPath = "";

    private ContentResolver contentResolver;
    private FileUtil fileUtil;
    List<Contact> contactDataList;


    public TemplateDataSource(Context context, List<Contact> contactDataList)
    {
        contentResolver = context.getContentResolver();
        fileUtil = new FileUtil(context);
        this.contactDataList = contactDataList;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<TemplateData> callback)
    {
        List<TemplateData> templateDataList = loadAssets(params.requestedLoadSize, params.requestedInitialKey);
        callback.onResult(templateDataList);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<TemplateData> callback)
    {
        List<TemplateData> templateDataList = loadAssets(params.requestedLoadSize, params.key);
        callback.onResult(templateDataList);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<TemplateData> callback)
    {
        List<TemplateData> templateDataList = loadAssets(params.requestedLoadSize, params.key);
        callback.onResult(templateDataList);
    }

    @NonNull
    @Override
    public Integer getKey(@NonNull TemplateData item)
    {
        return null;
    }

    private List<TemplateData> loadAssets(int noOfAssets, int key)
    {
        String htmlPath = assetsPath + "business_card.html";
        String cssPath = assetsPath + "business_card.css";
        String logoPhotoPath = assetsPath + "logo.png";
        String backgroundPhotoPath = assetsPath + "background.png";

        List<TemplateData> templateDataList = new ArrayList<>();
        for(int assetCount = 0; assetCount < contactDataList.size(); assetCount++)
        {
            TemplateData templateData = new TemplateData();

            templateData.contactID = contactDataList.get(assetCount).contactID;
            templateData.html = new String(fileUtil.readTextAsset(htmlPath));
            templateData.css = new String(fileUtil.readTextAsset(cssPath));
            templateData.logoPhoto = fileUtil.readBitmapAsset(logoPhotoPath);
            templateData.backgroundPhoto = fileUtil.readBitmapAsset(backgroundPhotoPath);

//            Log.d(TAG, " templateData.contactID: " + templateData.contactID);

            templateDataList.add(templateData);
        }
        return templateDataList;
    }

    public static class Factory extends DataSource.Factory<Integer, TemplateData>
    {
        private Context context;

        List<Contact> contactList;

        public Factory(Context context)
        {
            this.context = context;
        }

        public void setContactList(List<Contact> contactList)
        {
            this.contactList = contactList;
        }

        public void setFilter(String filterState)
        {

        }

        @Override
        public DataSource<Integer, TemplateData> create()
        {
            Log.d(TAG, "Create TemplateDataSource");
            return new TemplateDataSource(context, contactList);
        }
    }
}
