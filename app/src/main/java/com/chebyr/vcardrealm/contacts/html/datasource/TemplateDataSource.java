package com.chebyr.vcardrealm.contacts.html.datasource;

import android.content.ContentResolver;
import android.content.Context;

import com.chebyr.vcardrealm.contacts.html.data.TemplateData;
import com.chebyr.vcardrealm.contacts.html.utils.FileUtil;

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

        TemplateData templateData = new TemplateData();

        templateData.html = new String(fileUtil.readTextAsset(htmlPath));
        templateData.css = new String(fileUtil.readTextAsset(cssPath));
        templateData.logoPhotoPath = assetsPath + "logo.png";
        templateData.backgroundPhotoPath = assetsPath + "background.png";

        return templateData;
    }
}
