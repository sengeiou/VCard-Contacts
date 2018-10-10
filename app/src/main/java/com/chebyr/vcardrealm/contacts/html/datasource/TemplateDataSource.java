package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.arch.paging.PositionalDataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.datasource.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.repository.ContactRepository;
import com.chebyr.vcardrealm.contacts.html.utils.FileUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TemplateDataSource extends PositionalDataSource<TemplateData>
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private ContentResolver contentResolver;
    private FileUtil fileUtil;

    private String assetPath = "file:///android_asset/business_card.html";

    public TemplateDataSource(Context context, ContactRepository contactRepository)
    {
        contentResolver = context.getContentResolver();
        fileUtil = new FileUtil(context);
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
        List<TemplateData> templateDataList = new ArrayList<>();
        for(int assetCount = startPosition; assetCount < startPosition + noOfAssets; assetCount++)
        {
            TemplateData templateData = new TemplateData();

            templateData.inputStream = fileUtil.openVCardAsset(assetPath);
        }
        return templateDataList;
    }

    public static class Factory extends DataSource.Factory<Integer, TemplateData>
    {
        private Context context;
        private ContactRepository contactRepository;

        public Factory(Context context, ContactRepository contactRepository)
        {
            this.context = context;
            this.contactRepository = contactRepository;
        }

        public void setFilter(String filterState)
        {

        }

        @Override
        public DataSource<Integer, TemplateData> create()
        {
            return new TemplateDataSource(context, contactRepository);
        }
    }
}
