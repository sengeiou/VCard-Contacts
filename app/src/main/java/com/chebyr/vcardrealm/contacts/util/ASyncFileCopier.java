package com.chebyr.vcardrealm.contacts.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


class ASyncFileCopier extends AsyncTask<Context, String, Boolean>
{
    private static String TAG = ASyncFileCopier.class.getSimpleName();

    private String directoryName;
    private AssetManager assetManager;

    public void initialize(String directoryName)
    {
        this.directoryName = directoryName;
    }

    @Override
    protected Boolean doInBackground(Context... contexts)
    {
        Context context = contexts[0];
        assetManager = context.getAssets();

        // Get the directory for the user's public pictures directory.
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        String rootPath = externalStorageDirectory.getAbsolutePath();
        Log.d(TAG, "externalStorageDirectory: " + rootPath);

        File vcardDirectory = createDirectory(rootPath, directoryName);
        if(vcardDirectory == null)
            return false;

        copyAssets(context, "", vcardDirectory.getAbsolutePath());
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean)
    {
        Log.d(TAG, "Assets copied");
        super.onPostExecute(aBoolean);
    }

    private void copyAssets(Context context, String sourcePath, String destinationPath)
    {
        try
        {
            String[] assetNames = assetManager.list(sourcePath);

            for (String assetName : assetNames)
            {
                if(assetName.contains("index") || assetName.contains("photo") || assetName.contains("logo") || assetName.contains("background"))
                {
                    String fullFileName = copyFile(sourcePath, assetName, destinationPath);
                    Log.d(TAG, "File copied:" + fullFileName);
                    scanFile(context, fullFileName);
                }
                else if(!assetName.contains("."))
                {
                    Log.d(TAG, "Create directory: " + assetName);
                    File directory = createDirectory(destinationPath, assetName);
                    if(directory != null)
                    {
                        String subDirectory = destinationPath + "/" + assetName;
                        copyAssets(context, assetName, subDirectory);
                    }
                }
            }
            Log.d(TAG, "Scan Directory:" + destinationPath);
            scanFile(context, destinationPath);
        }
        catch (Exception e)
        {
            Log.d(TAG, e.getMessage());
        }
    }

    private String copyFile(String sourcePath, String assetName, String destDirectory)
    {
        String assetPath = sourcePath + "/" + assetName;
        Log.d(TAG, "Copy file: " + assetPath + " to " + destDirectory);
        File outFile = new File(destDirectory, assetName);

        try(InputStream inputStream = assetManager.open(assetPath))
        {
            try(OutputStream outputStream = new FileOutputStream(outFile))
            {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1)
                    outputStream.write(buffer, 0, read);
            }
            catch (FileNotFoundException e)
            {
                Log.d(TAG, e.getMessage());
                return null;
            }
        }
        catch (IOException e)
        {
            Log.d(TAG, e.getMessage());
            return null;
        }

        return outFile.getAbsolutePath();
    }

    private File createDirectory(String parent, String directoryName)
    {
        File file = new File(parent, directoryName);

        if(file.isDirectory())
        {
            Log.d(TAG, "Directory already exists");
            return file;
        }

        if (!file.mkdirs())
        {
            Log.d(TAG, "Directory not created");
            return null;
        }

        Log.d(TAG, "Directory '" + file + "' created");
        return file;
    }

    public static void scanFile(Context context, String fileName)
    {
//        MediaScannerConnection.scanFile(context, new String[]{fileName}, null, ASyncFileCopier::onScanCompleted);
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri fileContentUri = Uri.parse(fileName); // With 'permFile' being the File object
        mediaScannerIntent.setData(fileContentUri);
        context.sendBroadcast(mediaScannerIntent); // With 'this' being the context, e.g. the activity
    }

    private static void onScanCompleted(String path, Uri uri)
    {
        //String a = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xml");
        //Log.d(TAG, "path=" + path + " uri=" + uri);
    }
}
