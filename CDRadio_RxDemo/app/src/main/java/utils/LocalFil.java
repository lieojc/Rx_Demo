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
 * Created by 廖华凯 on 2016/11/21.
 */
public class LocalFil {
    private static File tunerFile;
    private static File businessFile;
    private static File bandFile;
    private static BufferedOutputStream bos_band;
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
                LogUtils.showLog("create biz file :", "success");
            }else {
                LogUtils.showLog("create biz file :", "fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("create biz file error:", e.getMessage());
        }
        try {
            bos=new BufferedOutputStream(new FileOutputStream(businessFile,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.showLog("cannot find biz file to create stream :", e.getMessage());
        }

    }

    private static File newFile;
    private static String newFileName;
    private static BufferedOutputStream newBufferedOutPutStream;

    /**
     * 创建新文件
     * @param fileName 文件名，注意不要带后缀，如.bin/.txt
     * @return 最终生成的文件名，名字上含生成日期
     */
    public synchronized static String prepareFile(String fileName){
        File dir;
        newFileName=fileName+ System.currentTimeMillis()+".bin";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir=new File(Environment.getExternalStorageDirectory(), BaseApplication.getGlobalContext().getPackageName());
        }else {
            dir=new File(BaseApplication.getGlobalContext().getCacheDir(), BaseApplication.getGlobalContext().getPackageName());

        }
        if(!dir.exists()){
            boolean isSuccess = dir.mkdirs();
            if(isSuccess){
                LogUtils.showLog("prepareFile :", "success");
            }else {
                LogUtils.showLog("prepareFile :", "fail");
            }
        }
        if (dir.exists()) {
            newFile=new File(dir,newFileName);
        }else {
            newFile=new File(BaseApplication.getGlobalContext().getFilesDir(),newFileName);
        }

        if(newFile.exists()){
            newFile.delete();
        }
        try {
            boolean isSuccess = newFile.createNewFile();
            if(isSuccess){
                LogUtils.showLog("prepareFile:", "success");
            }else {
                LogUtils.showLog("prepareFile:", "fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("prepareFile error:", e.getMessage());
        }
        try {
            newBufferedOutPutStream=new BufferedOutputStream(new FileOutputStream(newFile,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.showLog("cannot find band file to create stream :", e.getMessage());
        }
        return newFileName;
    }

    public static void writeNewFile(final byte[]data,int indexStart,int length){
        try {
            newBufferedOutPutStream.write(data, indexStart, length);
            newBufferedOutPutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("write local file() error:", e.getMessage());
        }
    }

    public static void stopWritingNewFile(){
        if(newBufferedOutPutStream!=null){
            try {
                newBufferedOutPutStream.flush();
                newBufferedOutPutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public synchronized static void prepareBandFile(){
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
            bandFile=new File(dir,"band_data.bin");
        }else {
            bandFile=new File(BaseApplication.getGlobalContext().getFilesDir(),"band_data.bin");
        }

        if(bandFile.exists()){
            bandFile.delete();
        }
        try {
            boolean isSuccess = bandFile.createNewFile();
            if(isSuccess){
                LogUtils.showLog("create band file :", "success");
            }else {
                LogUtils.showLog("create band file :", "fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("create band file error:", e.getMessage());
        }
        try {
            bos_band=new BufferedOutputStream(new FileOutputStream(bandFile,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.showLog("cannot find band file to create stream :", e.getMessage());
        }

    }

    public static void writeBandFile(final byte[]data){
        try {
            bos_band.write(data, 10, 4096);
            bos_band.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("write local file() error:", e.getMessage());
        }
    }


    public static void writeTunerFile(final byte[] data){
        try {
            bos.write(data, 10, 4096);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.showLog("write local file() error:", e.getMessage());
        }
    }

    public static void writeBusinessFile(final byte[] data){
        try {
            bos.write(data);
//            bos.write(data, 10, 4096);
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

    public static void stopWritingBandFile(){
        if(bos_band!=null){
            try {
                bos_band.flush();
                bos_band.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
