package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.data.Contact;
import com.chebyr.vcardrealm.contacts.html.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class TemplateDataSource extends PositionalDataSource<TemplateData>
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
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<TemplateData> callback)
    {
        List<TemplateData> templateDataList = loadAssets(params.requestedLoadSize, params.requestedStartPosition);
        callback.onResult(templateDataList, params.requestedStartPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<TemplateData> callback)
    {
        List<TemplateData> templateDataList = loadAssets(params.loadSize, params.startPosition);
        callback.onResult(templateDataList);
    }

    private List<TemplateData> loadAssets(int noOfAssets, int startPosition)
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

        List<Contact> contactDataList;

        public Factory(Context context)
        {
            this.context = context;
        }

        public void setContactDataList(List<Contact> contactDataList)
        {
            this.contactDataList = contactDataList;
        }

        public void setFilter(String filterState)
        {

        }

        @Override
        public DataSource<Integer, TemplateData> create()
        {
            Log.d(TAG, "Create TemplateDataSource");
            return new TemplateDataSource(context, contactDataList);
        }
    }
}
