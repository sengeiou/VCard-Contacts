package com.chebyr.vcardrealm.contacts.datasource;

import android.content.ContentResolver;
import android.content.Context;

import com.chebyr.vcardrealm.contacts.data.TemplateData;
import com.chebyr.vcardrealm.contacts.util.FileUtil;

import java.util.HashMap;

public class TemplateDataSource
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private static String folderPath = "Woody/";

    private ContentResolver contentResolver;
    private FileUtil fileUtil;

    private HashMap<Long, String> templateMap;

    public TemplateDataSource(Context context)
    {
        contentResolver = context.getContentResolver();
        fileUtil = new FileUtil(context);
    }

    public TemplateData loadTemplate(long contactID)
    {
        String htmlPath = folderPath + "business_card.html";
        String cssPath = folderPath + "business_card.css";

        TemplateData templateData = new TemplateData();

        templateData.html = new String(fileUtil.readTextAsset(htmlPath));
        templateData.css = new String(fileUtil.readTextAsset(cssPath));
        templateData.logoPhotoPath = folderPath + "logo.png";
        templateData.backgroundPhotoPath = folderPath + "background.png";
        templateData.folderPath = folderPath;

        return templateData;
    }
}
