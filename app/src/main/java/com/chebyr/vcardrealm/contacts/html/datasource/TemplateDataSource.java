package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;

import com.chebyr.vcardrealm.contacts.html.datasource.data.ContactData;
import com.chebyr.vcardrealm.contacts.html.datasource.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;
import com.chebyr.vcardrealm.contacts.html.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class TemplateDataSource extends PositionalDataSource<TemplateData>
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private static String assetsPath = "";

    private ContentResolver contentResolver;
    private FileUtil fileUtil;
    List<ContactData> contactDataList;


    public TemplateDataSource(Context context, List<ContactData> contactDataList)
    {
        contentResolver = context.getContentResolver();
        fileUtil = new FileUtil(context);
        this.contactDataList = contactDataList;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<TemplateData> callback)
    {
        callback.onResult(loadAssets(params.requestedLoadSize, params.requestedStartPosition), params.requestedStartPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<TemplateData> callback)
    {
        callback.onResult(loadAssets(params.loadSize, params.startPosition));
    }

    public List<TemplateData> loadAssets(int noOfAssets, int startPosition)
    {
        String templatePath = assetsPath + "business_card.html";
        String logoPhotoPath = assetsPath + "logo.png";
        String backgroundPhotoPath = assetsPath + "background.png";

        List<TemplateData> templateDataList = new ArrayList<>();
        for(int assetCount = startPosition; assetCount < startPosition + noOfAssets; assetCount++)
        {
            TemplateData templateData = new TemplateData();

            templateData.htmlStream = fileUtil.openVCardAsset(templatePath);
            templateData.logoPhotoStream = fileUtil.openVCardAsset(logoPhotoPath);
            templateData.backgroundPhotoStream = fileUtil.openVCardAsset(backgroundPhotoPath);

            templateDataList.add(templateData);
        }
        return templateDataList;
    }

    public static class Factory extends DataSource.Factory<Integer, TemplateData>
    {
        private Context context;


        List<ContactData> contactDataList;

        public Factory(Context context)
        {
            this.context = context;
        }

        public void setContactDataList(List<ContactData> contactDataList)
        {
            this.contactDataList = contactDataList;
        }

        public void setFilter(String filterState)
        {

        }

        @Override
        public DataSource<Integer, TemplateData> create()
        {
            return new TemplateDataSource(context, contactDataList);
        }
    }
}
