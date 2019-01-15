package com.chebyr.vcardrealm.contacts.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

public class FileManager
{
    private static final String TAG = FileManager.class.getSimpleName();
    public static String assetsPath = "file:///android_asset/";
    public static String rootPath;
    public static String vcardDirectoryPath;

    public AssetManager assetManager;

    public FileManager(Context context)
    {
        assetManager = context.getAssets();
    }

    public void initVCardDirectory(Context context, String directoryName)
    {
        Log.d(TAG, "Create Directory in external storage and copy Assets");

        File externalStorage = Environment.getExternalStorageDirectory();
        rootPath = externalStorage.getAbsolutePath();
        vcardDirectoryPath = rootPath + "/" + directoryName;
        Log.d(TAG, "externalStorage: " + rootPath);

        AsyncFileCopier AsyncFileCopier = new AsyncFileCopier();
        AsyncFileCopier.setDirectory(directoryName);
        AsyncFileCopier.execute(context);
    }

    public InputStream getBitmapStream(Bitmap photo)
    {
        Log.d(TAG, "getBitmapStream: " + photo);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100 , byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        InputStream contactPhotoStream = new ByteArrayInputStream(bytes);
        return contactPhotoStream;
    }

    public byte[] readTextAsset(String assetName)
    {
        try(InputStream inputStream = assetManager.open(assetName))
        {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            return buffer;
        }
        catch (Exception exception)
        {
            Log.d(TAG, exception.toString());
            return null;
        }
    }

    public String readTextFile(String fileName)
    {
        StringBuilder text = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            br.close();
            return text.toString();
        }
        catch(Exception e)
        {
            Log.d(TAG, e.getMessage());
            return null;
        }
    }

    public void writeFile(String fileName)
    {
        try(OutputStream outputStream = new FileOutputStream(fileName))
        {

            //... do stuff to your streams
        }
        catch(Exception e)
        {
            //Handle the error... but the streams are still open!
        }
    }

    public InputStream openVCardFile(String fileName)
    {
        try
        {
            File file = new File(fileName);
            InputStream inputStream = new FileInputStream(file);
            return inputStream;
        }
        catch (Exception e)
        {
            Log.d(TAG,e.toString());
            return null;
        }
    }

    public InputStream openVCardURL(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            InputStream inputStream = url.openStream();
            return inputStream;
        }
        catch (Exception e)
        {
            Log.d(TAG,e.toString());
            return null;
        }
    }


    public String saveVCardFile(Context context, String fullFileName, String text)
    {
        try
        {
            File file = new File(fullFileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(text);
            outputStreamWriter.close();
            AsyncFileCopier.scanFile(context, fullFileName);
            return fullFileName;
        }
        catch (Exception e)
        {
            Log.d(TAG,e.toString());
            return null;
        }
    }

    public String saveVCardSnapshot(Context context, String fullFileName, Bitmap bitmap)
    {
        try
        {
            File file = new File(fullFileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            AsyncFileCopier.scanFile(context, fullFileName);
            return fullFileName;
        }
        catch (Exception exception)
        {
            Log.d(TAG,exception.toString());
            return null;
        }
    }

    void updateExternalStorageState()
    {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        }
        else
        {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

    }

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable()
    {
        //<files-path name="name" path="path" />
        //Represents files in the files/ subdirectory of your app's internal storage area. This subdirectory is the same as the value returned by Context.getFilesDir()

        //<external-path name="name" path="path" />
        //Represents files in the root of your app's external storage area. The path Context.getExternalFilesDir() returns the files/ subdirectory of this this root.

        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    static class AsyncFileCopier extends AsyncTask<Context, String, Boolean>
    {
        private String directoryName;
        private AssetManager assetManager;

        public void setDirectory(String directoryName)
        {
            this.directoryName = directoryName;
        }

        @Override
        protected Boolean doInBackground(Context... contexts)
        {
            Context context = contexts[0];
            assetManager = context.getAssets();

            File vcardDirectory = createDirectory(rootPath, directoryName);
            if(vcardDirectory == null)
                return false;

            copyAssets(context, "", vcardDirectoryPath);
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
                    if(assetName.contains("index") || assetName.contains("photo") || assetName.contains("logo")
                            || assetName.contains("background") || assetName.contains("style"))
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
            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileContentUri = Uri.parse(fileName); // With 'permFile' being the File object
            mediaScannerIntent.setData(fileContentUri);
            context.sendBroadcast(mediaScannerIntent); // With 'this' being the context, e.g. the activity
        }
    }
}
