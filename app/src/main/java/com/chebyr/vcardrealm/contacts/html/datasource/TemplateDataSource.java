package com.chebyr.vcardrealm.contacts.html.datasource;

import android.arch.paging.DataSource;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.chebyr.vcardrealm.contacts.html.data.Contact;
import com.chebyr.vcardrealm.contacts.html.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class TemplateDataSource
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private static String assetsPath = "";

    private ContentResolver contentResolver;
    private FileUtil fileUtil;

    public TemplateDataSource(Context context)
    {
        contentResolver = context.getContentResolver();
        fileUtil = new FileUtil(context);
    }

    public TemplateData loadTemplate(long contactID)
    {
        String htmlPath = assetsPath + "business_card.html";
        String cssPath = assetsPath + "business_card.css";
        String logoPhotoPath = assetsPath + "logo.png";
        String backgroundPhotoPath = assetsPath + "background.png";

        TemplateData templateData = new TemplateData();

        templateData.html = new String(fileUtil.readTextAsset(htmlPath));
        templateData.css = new String(fileUtil.readTextAsset(cssPath));
        templateData.logoPhoto = fileUtil.readBitmapAsset(logoPhotoPath);
        templateData.backgroundPhoto = fileUtil.readBitmapAsset(backgroundPhotoPath);

        return templateData;
    }
}
