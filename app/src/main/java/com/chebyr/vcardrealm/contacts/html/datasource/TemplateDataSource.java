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
import com.chebyr.vcardrealm.contacts.html.viewmodel.TemplateParser;

import java.io.InputStream;

public class TemplateDataSource extends PositionalDataSource<TemplateData>
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private ContentResolver contentResolver;
    private FileUtil fileUtil;
    TemplateParser templateParser;

    public TemplateDataSource(Context context, ContactRepository contactRepository)
    {
        contentResolver = context.getContentResolver();
        fileUtil = new FileUtil(context);
        templateParser = new TemplateParser(context);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<TemplateData> callback) {

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<TemplateData> callback) {

    }

    public boolean openVCardAsset(String assetName)
    {
            InputStream inputStream = fileUtil.openVCardAsset(assetName);
            return templateParser.parseInputStream(inputStream);
    }

    public boolean openVCardFile(String fileName)
    {
        InputStream inputStream = fileUtil.openVCardFile(fileName);
        return templateParser.parseInputStream(inputStream);
    }

    public boolean openVCardURL(String urlString)
    {
        InputStream inputStream = fileUtil.openVCardURL(urlString);
        return templateParser.parseInputStream(inputStream);
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
