package utils;

import android.os.Environment;

import com.skycaster.l_cdradiorx.bases.BaseApplication;
import com.skycaster.l_cdradiorx.utils.LogUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 郑力嘉 on 2016/12/14.
 */
public class LocalFil1 {
    private static File tunerFile;
    private static File businessFile;
    private static BufferedOutputStream bos;
    /**
     * 测试用
     * @return 生成一个大小5000 bytes的“升级文件”
     */
    public static File getUpgradeSourceFile(){
        File src;
        File dir= BaseApplication.getGlobalContext().getCacheDir();
        src=new File(dir,"upgrade.txt");
        if(!src.exists()){
            try {
                src.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(src));
            byte[] buf=new byte[5000];
            for(int i=0;i<5000;i++){
                buf[i]= (byte) Math.random();
            }
            bos.write(buf);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return src;
    }

    public synchronized static void prepareTunerFile(){
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir=new File(Environment.getExternalStorageDirectory(), BaseApplication.getGlobalContext().getPackageName());
        }else {
            dir=new File(BaseApplication.getGlobalContext().getCacheDir(), BaseApplication.getGlobalContext().getPackageName());

        }
        if(!dir.exists()){
            boolean isSuccess = dir.mkdirs();
            if(isSuccess){
                LogUtils.showLog("create dir :", "success");
            }else {
                LogUtils.showLog("create dir :", "fail");
            }
        }
        if (dir.exists()) {
            tunerFile=new File(dir,"tuner_data.bin");
        }else {
            tunerFile=new File(BaseApplication.getGlobalContext().getFilesDir(),"tuner_data.bin");
        }

        if(tunerFile.exists()){
            tunerFile.delete();
        }
        try {
            boolean isSuccess = tunerFile.createNewFile();
            if(isSuccess){
                LogUtils.showLog("create tuner file :", "success");
            }else {
                LogUtils.showLog("create tuner file :", "fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("create tuner file error:", e.getMessage());
        }
        try {
            bos=new BufferedOutputStream(new FileOutputStream(tunerFile,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.showLog("cannot find tuner file to create stream :", e.getMessage());
        }

    }

    public synchronized static void prepareBusinessFile(){
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir=new File(Environment.getExternalStorageDirectory(), BaseApplication.getGlobalContext().getPackageName());
        }else {
            dir=new File(BaseApplication.getGlobalContext().getCacheDir(), BaseApplication.getGlobalContext().getPackageName());

        }
        if(!dir.exists()){
            boolean isSuccess = dir.mkdirs();
            if(isSuccess){
                LogUtils.showLog("create dir :", "success");
            }else {
                LogUtils.showLog("create dir :", "fail");
            }
        }
        if (dir.exists()) {
            businessFile=new File(dir,"business_data.bin");
        }else {
            businessFile=new File(BaseApplication.getGlobalContext().getFilesDir(),"business_data.bin");
        }

        if(businessFile.exists()){
            businessFile.delete();
        }
        try {
            boolean isSuccess = businessFile.createNewFile();
            if(isSuccess){
                LogUtils.showLog("create tuner file :", "success");
            }else {
                LogUtils.showLog("create tuner file :", "fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("create tuner file error:", e.getMessage());
        }
        try {
            bos=new BufferedOutputStream(new FileOutputStream(businessFile,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.showLog("cannot find tuner file to create stream :", e.getMessage());
        }

    }


    public static void writeTunerFile(final byte[] data){
        try {
            bos.write(data, 0, data.length);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("write local file() error:", e.getMessage());
        }
    }

    public static void writeBusinessFile(final byte[] data){
        try {
            bos.write(data);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("write Business file() error:", e.getMessage());
        }
    }

    public static void stopWriting(){
        if(bos!=null){
            try {
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
