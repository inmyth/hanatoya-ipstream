package jp.hanatoya.ipcam.utils;


import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.query.Query;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.repo.SwitchDao;

public class MyFileUtils {

    private static final String DIRNAME = "ipcam";
    private static final String FILENAME = "data.json";

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void saveToSdCard(String json) {
        // Get the directory for the user's public pictures directory.
        FileWriter writer;
        File dir = new File(Environment.getExternalStorageDirectory(), DIRNAME);
        if (!dir.mkdirs()) {
            Log.e("File System", "Directory not created");
        }

        try {
            if (!dir.isDirectory()) {
                throw new IOException("Unable to create directory. Maybe the SD card is mounted?");
            }
            File outputFile = new File(dir, FILENAME);
            writer = new FileWriter(outputFile);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSettingsFileExist(){
        File dir = new File(Environment.getExternalStorageDirectory(), DIRNAME);
        File file = new File(dir, FILENAME);
        return file.exists();
    }

    public static boolean importDb(CamDao camDao, SwitchDao switchDao){

        File dir = new File(Environment.getExternalStorageDirectory(), DIRNAME);
        File file = new File(dir, FILENAME);
        try {
        FileInputStream is = new FileInputStream(file);
        int size = 0;

            size = is.available();

        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String mResponse = new String(buffer);
        ArrayList<Cam> cams = GsonUtils.toBean(mResponse, new TypeToken<ArrayList<Cam>>(){}.getType());
        if (cams == null){
            return false;
        }
        for (Cam c : cams){
            camDao.insertOrReplace(c);
            for (Switch s : c.getSwitches()){
                switchDao.insertOrReplace(s);
            }
        }
        return true;
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        } catch (JsonParseException e){
            return false;
        }

}


    public static void dumpDb(CamDao camDao, SwitchDao switchDao){
        List<Cam> cams = camDao.loadAll();
        if (cams != null){
            for (Cam c : cams){
                Query query = switchDao.queryBuilder().where(SwitchDao.Properties.CamId.eq(c.getId())).orderAsc(SwitchDao.Properties.Id).build();
                c.setSwitches(query.list());
            }

            String json = GsonUtils.toJson(cams);
            saveToSdCard(json);
        }
    }
}
