package com.chebyr.vcardrealm.contacts.datasource;

import android.content.Context;
import android.util.SparseArray;

import com.chebyr.vcardrealm.contacts.data.TemplateData;
import com.chebyr.vcardrealm.contacts.util.FileManager;

public class TemplateDataSource
{
    private static String TAG = TemplateDataSource.class.getSimpleName();

    private FileManager fileManager;

    private SparseArray<String> templateArray;

    public TemplateDataSource(Context context)
    {
        fileManager = new FileManager(context);
        templateArray = new SparseArray<>();
        templateArray.append(0, "/Avatar/");
        templateArray.append(1, "/BookStore/");
        templateArray.append(2, "/Envelope/");
        templateArray.append(3, "/Feyenoord/");
        templateArray.append(4, "/FindingNemo/");
        templateArray.append(5, "/GhostRider/");
        templateArray.append(6, "/ModernBlue/");
        templateArray.append(7, "/Origami/");
        templateArray.append(8, "/RosesAndHearts/");
        templateArray.append(9, "/Terminator/");
        templateArray.append(10, "/Woody/");
    }

    public TemplateData loadTemplate(long contactID)
    {
        int templateID = (int)contactID % 11;

        TemplateData templateData = new TemplateData();

        String folderPath = FileManager.vcardDirectoryPath + templateArray.get(templateID);

        templateData.folderUrl = "file://" + folderPath;
        templateData.html = fileManager.readTextFile(folderPath + "index.html");
        templateData.logoPhotoPath = folderPath + "logo.png";
        templateData.backgroundPhotoPath = folderPath + "background.png";
        templateData.photoPath = folderPath + "photo.png";

        return templateData;
    }
}
