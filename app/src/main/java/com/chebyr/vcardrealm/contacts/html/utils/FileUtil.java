package com.chebyr.vcardrealm.contacts.html.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

public class FileUtil implements MediaScannerConnection.OnScanCompletedListener
{
    private static final String TAG = FileUtil.class.getSimpleName();

    Context mContext;
    AssetManager mAssetManager;

    public FileUtil(Context context)
    {
        mContext = context;
        mAssetManager = mContext.getAssets();
    }

    public String initVCardsDirectory()
    {
        Log.d(TAG, "Create VCardCallerID Directory and copy VCards from Assets");

        File directory = createDirectoryInExternalStorage("VCardCallerID");
        if(directory == null)
            return null;

        copyAssetsToDirectory(directory);

        String fullDirectoryName = directory.getAbsolutePath();
        Log.d(TAG, "Scan Directory:" + fullDirectoryName);
        MediaScannerConnection.scanFile(mContext, new String[]{directory.getAbsolutePath()}, null, this);

        return fullDirectoryName;
    }

    private File createDirectoryInExternalStorage(String directoryName)
    {
        // Get the directory for the user's public pictures directory.
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, directoryName);

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

    public byte[] readTextAsset(String assetName)
    {
        try
        {
            InputStream inputStream = mAssetManager.open(assetName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            return buffer;
        }
        catch (Exception exception)
        {
            Log.d(TAG, exception.toString());
            return null;
        }
    }

    public Bitmap readBitmapAsset(String assetName)
    {
        try
        {
            InputStream inputStream = mAssetManager.open(assetName);
            return BitmapFactory.decodeStream(inputStream);
        }
        catch (Exception exception)
        {
            Log.d(TAG, exception.toString());
            return null;
        }
    }

    public void readFile(String fileName)
    {
        InputStream mInputStream = null;
        try
        {
            mInputStream = new FileInputStream(fileName);
            //... do stuff to your streams
        }
        catch(FileNotFoundException fnex)
        {
            //Handle the error... but the streams are still open!
        }
        finally
        {
            //close input
            if (mInputStream != null)
            {
                try
                {
                    mInputStream.close();
                }
                catch(IOException ioex)
                {
                    //Very bad things just happened... handle it
                }
            }
        }
    }

    public void writeFile(String fileName)
    {
        OutputStream mOutputStream = null;
        try
        {
            mOutputStream = new FileOutputStream(fileName);
            //... do stuff to your streams
        }
        catch(FileNotFoundException fnex)
        {
            //Handle the error... but the streams are still open!
        }
        finally
        {
            //Close output
            if (mOutputStream != null)
            {
                try
                {
                    mOutputStream.close();
                }
                catch(IOException ioex)
                {
                    //Very bad things just happened... handle it
                }
            }
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


    public String saveVCardFile(String fullFileName, String text)
    {
        try
        {
            File file = new File(fullFileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(text);
            outputStreamWriter.close();
            MediaScannerConnection.scanFile(mContext, new String[]{fullFileName}, null, null);
            return fullFileName;
        }
        catch (Exception e)
        {
            Log.d(TAG,e.toString());
            return null;
        }
    }

    public String saveVCardSnapshot(String fullFileName, Bitmap bitmap)
    {
        try
        {
            File file = new File(fullFileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            MediaScannerConnection.scanFile(mContext, new String[]{fullFileName}, null, this);
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
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            return true;
        }
        return false;
    }

    public void copyAssetsToDirectory(File directoryName)
    {
        String[] files = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try
        {
            files = mAssetManager.list("");

            for(String fileName : files)
            {
                if(fileName.endsWith(".xml"))
                {
                    inputStream = mAssetManager.open(fileName);
                    File outFile = new File(directoryName, fileName);
                    outputStream = new FileOutputStream(outFile);
                    copyFile(inputStream, outputStream);

                    String fullFileName = outFile.getAbsolutePath();
                    //Log.d(TAG, "Scan File:" + fullFileName);
                    MediaScannerConnection.scanFile(mContext, new String[]{fullFileName}, null, this);
                }
            }
        }
        catch(Exception exception)
        {
            Log.d(TAG, exception.toString());
        }
        finally
        {
            try
            {
                if (inputStream != null) ;
                    inputStream.close();
            }
            catch (Exception exception)
            {
                Log.d(TAG, exception.toString());
            }

            try
            {
                if(outputStream != null)
                    outputStream.close();
            }
            catch (Exception exception)
            {
                Log.d(TAG, exception.toString());
            }
        }
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1)
            outputStream.write(buffer, 0, read);
    }

    @Override
    public void onScanCompleted(String path, Uri uri)
    {
        //String a = MimeTypeMap.getSingleton().getMimeTypeFromExtension("xml");
        //Log.d(TAG, "path=" + path + " uri=" + uri);
    }


}
