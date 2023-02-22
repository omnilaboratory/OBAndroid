package com.omni.wallet.data;

import android.content.Context;

import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.utils.TimeFormatUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetsTestData {
    private final static String TAG = AssetsTestData.class.getSimpleName();

    private final static List<Map<String,Object>> testDataList = new ArrayList<>();

    public void hasBalanceData(Context context){
        AssetsDao assetsDao = new AssetsDao(context);
        assetsDao.insertAsset("0","btc");
        assetsDao.insertAsset("2147483651","ftoken");
        assetsDao.changeAssetIsUse("0",1);
        assetsDao.changeAssetIsUse("2147483651",1);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        try {
            long now = TimeFormatUtil.getCurrentDayMills();
            long date = now - ConstantInOB.DAY_MILLIS * 365;
            assetsDataDao.insertAssetsData("0",24000.85,2.5687411,0.0515874,date);
            assetsDataDao.insertAssetsData("2147483651",1,200,100,date);
            double value = (2.5687411+0.0515874)*24000.85 + (200 + 100) * 1;
            assetsValueDataDao.insertAssetValueData(value,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }




    }

    public void oneDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-1);
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        double value = (2.5687411+0.0515874)*24000.85 + (200 + 100) * 1;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void fourDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 2; i < 4; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (2.5687411+0.0515874)*24000.85 + (200 + 100) * 1;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-4);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",20000.85,2.5687411,0.0515874,date);
        assetsDataDao.insertAssetsData("2147483651",1,200,100,date);
        double value = (2.5687411+0.0515874)*20000.85 + (200 + 100) * 1;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void tenDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 5; i < 10; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (2.5687411+0.0515874)*20000.85 + (200 + 100) * 1;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-10);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18000.624,1.5687411,0.0515874,date);
        assetsDataDao.insertAssetsData("2147483651",0.996,200,100,date);
        double value = (1.5687411+0.0515874)*18000.624 + (200 + 100) * 0.996;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void thirteenDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 10; i < 13; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (1.5687411+0.0515874)*18000.624 + (200 + 100) * 0.996;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-13);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18056.624,1.5687411,0.0465874,date);
        assetsDataDao.insertAssetsData("2147483651",1.001,200,88,date);
        double value = (1.5687411+0.0465874)*18056.624 + (200 + 88) * 1.001;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void fourteenDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        long date = now - ConstantInOB.DAY_MILLIS * (365-14);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18556.624,1.7687411,0.0465874,date);
        assetsDataDao.insertAssetsData("2147483651",1.001,200,88,date);
        double value = (1.7687411+0.0465874)*18556.624 + (200 + 88) * 1.001;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void fifteenDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        long date = now - ConstantInOB.DAY_MILLIS * (365-15);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18556.624,1.7687411,0.0465874,date);
        assetsDataDao.insertAssetsData("2147483651",1.001,200,88,date);
        double value = (1.7687411+0.0465874)*18556.624 + (200 + 88) * 1.001;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void twentyDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 10; i < 20; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (1.5687411+0.0515874)*18000.624 + (200 + 100) * 0.996;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-20);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18056.624,1.5687411,0.0465874,date);
        assetsDataDao.insertAssetsData("2147483651",1.001,200,88,date);
        double value = (1.5687411+0.0465874)*18056.624 + (200 + 88) * 1.001;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void twentyOneDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        long date = now - ConstantInOB.DAY_MILLIS * (365-21);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18556.624,1.7687411,0.0465874,date);
        assetsDataDao.insertAssetsData("2147483651",1.001,200,88,date);
        double value = (1.7687411+0.0465874)*18556.624 + (200 + 88) * 1.001;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public void twentyTwoDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        long date = now - ConstantInOB.DAY_MILLIS * (365-21);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18556.624,1.7687411,0.0465874,date);
        assetsDataDao.insertAssetsData("2147483651",1.001,200,88,date);
        double value = (1.7687411+0.0465874)*18556.624 + (200 + 88) * 1.001;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

}
