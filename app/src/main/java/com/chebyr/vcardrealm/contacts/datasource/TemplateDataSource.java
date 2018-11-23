package com.chebyr.vcardrealm.contacts.datasource;

import android.content.ContentResolver;
import android.content.Context;

import com.chebyr.vcardrealm.contacts.data.TemplateData;
import com.chebyr.vcardrealm.contacts.util.FileManager;

import java.util.HashMap;

public class TemplateDataSource
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private static String folderPath = "GhostRider/";

    private ContentResolver contentResolver;
    private FileManager fileManager;

    private HashMap<Long, String> templateMap;

    public TemplateDataSource(Context context)
    {
        contentResolver = context.getContentResolver();
        fileManager = new FileManager(context);
    }

    public TemplateData loadTemplate(long contactID)
    {
        String htmlPath = folderPath + "index.html";
        String cssPath = folderPath + "style.css";

        TemplateData templateData = new TemplateData();

        templateData.html = new String(fileManager.readTextAsset(htmlPath));
        templateData.css = new String(fileManager.readTextAsset(cssPath));
        templateData.logoPhotoPath = folderPath + "logo.png";
        templateData.backgroundPhotoPath = folderPath + "background.png";
        templateData.folderPath = folderPath;

        return templateData;
    }
}
