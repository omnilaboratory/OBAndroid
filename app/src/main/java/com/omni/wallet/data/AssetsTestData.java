package com.omni.wallet.data;

import android.content.Context;

import com.omni.wallet.base.ConstantInOB;
import com.omni.wallet.utils.TimeFormatUtil;

import java.text.ParseException;

public class AssetsTestData {

    public static void hasBalanceData(Context context){
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
            double value = (2.5687411+0.0515874)*24000.85 + (200 + 100) * 1.0;
            assetsValueDataDao.insertAssetValueData(value,date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void oneDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-1);
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        double value = (2.5687411+0.0515874)*24000.85 + (200 + 100) * 1.0;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public static void fourDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 2; i < 4; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (2.5687411+0.0515874)*24000.85 + (200 + 100) * 1.0;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-4);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",20000.85,2.5687411,0.0515874,date);
        assetsDataDao.insertAssetsData("2147483651",1,200,100,date);
        double value = (2.5687411+0.0515874)*20000.85 + (200 + 100) * 1.0;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public static void tenDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 5; i < 10; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (2.5687411+0.0515874)*20000.85 + (200 + 100) * 1.0;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-10);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18000.624,1.5687411,0.0515874,date);
        assetsDataDao.insertAssetsData("2147483651",0.996,200,100,date);
        double value = (1.5687411+0.0515874)*18000.624 + (200 + 100) * 0.996;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public static void thirteenDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 11; i < 13; i++) {
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

    public static void fourteenDayData(Context context){
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

    public static void fifteenDayData(Context context){
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

    public static void twentyDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 16; i < 20; i++) {
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

    public static void twentyOneDayData(Context context){
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

    public static void twentyTwoDayData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        long date = now - ConstantInOB.DAY_MILLIS * (365-22);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",18556.624,1.7687411,0.0465874,date);
        assetsDataDao.insertAssetsData("2147483651",1.001,200,88,date);
        double value = (1.7687411+0.0465874)*18556.624 + (200 + 88) * 1.001;
        assetsValueDataDao.insertAssetValueData(value,date);
    }
    public static void fourWeekData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 23; i < 28; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (1.7687411+0.0465874)*18556.624 + (200 + 88) * 1.001;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-28);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",26000.6894,2.5687411,0.1,date);
        assetsDataDao.insertAssetsData("2147483651",0.998,158,56,date);
        double value = (2.5687411+0.1)*26000.6894 + (158 + 56) * 0.998;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public static void tenWeekData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 29; i < 70; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (2.5687411+0.1)*26000.6894 + (158 + 56) * 0.998;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-70);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",26000.6894,2.5687411,0,date);
        assetsDataDao.insertAssetsData("2147483651",0.998,20,0,date);
        double value = (2.5687411+0)*26000.6894 + (20.0 + 0) * 0.998;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public static void thirtyWeekData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 71; i < 210; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (2.5687411+0)*26000.6894 + (20.0 + 0) * 0.998;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now - ConstantInOB.DAY_MILLIS * (365-210);
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",31234.518,2.0087411,1,date);
        assetsDataDao.insertAssetsData("2147483651",1.002,20,300,date);
        double value = (2.0087411+1)*31234.518 + (20.0 + 300) * 1.002;
        assetsValueDataDao.insertAssetValueData(value,date);
    }
    public static void fiftyTwoWeekData(Context context){
        long now = 0;
        try {
            now = TimeFormatUtil.getCurrentDayMills();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        for (int i = 211; i < 365; i++) {
            long date = now - ConstantInOB.DAY_MILLIS * (365 - i);
            double value = (2.0087411+1)*31234.518 + (20.0 + 300) * 1.002;
            assetsValueDataDao.insertAssetValueData(value,date);
        }
        long date = now;
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.insertAssetsData("0",36258.1858,0.5,0.8,date);
        assetsDataDao.insertAssetsData("2147483651",1,20,300,date);
        double value = (2.0087411+1)*31234.518 + (20.0 + 300) * 1.002;
        assetsValueDataDao.insertAssetValueData(value,date);
    }

    public static void clearData(Context context){
        AssetsDataDao assetsDataDao = new AssetsDataDao(context);
        assetsDataDao.clearData();
        AssetsValueDataDao assetsValueDataDao = new AssetsValueDataDao(context);
        assetsValueDataDao.clearData();
    }


}
