package com.omni.wallet_mainnet.utils;

import android.content.Context;

import com.omni.wallet_mainnet.SharedPreferences.WalletInfo;
import com.omni.wallet_mainnet.framelibrary.entity.User;

public class MoveDataAboutWalletInfoSP {
    public static void moveData(Context context) {
        boolean isMoved = User.getInstance().getWalletInfoMoved(context);

        boolean isSecreted = User.getInstance().getSeedSecreted(context);

        if (!isMoved){
            String initWalletType = User.getInstance().getInitWalletType(context);

            String macaroonString = User.getInstance().getMacaroonString(context);

            String seedString = User.getInstance().getSeedString(context);

            String recoverySeedString = User.getInstance().getRecoverySeedString(context);

            boolean headerBinChecked = User.getInstance().isHeaderBinChecked(context);

            boolean filterHeaderBinChecked = User.getInstance().isFilterHeaderBinChecked(context);

            boolean neutrinoDbChecked = User.getInstance().isNeutrinoDbChecked(context);

            String alias = User.getInstance().getAlias(context);

            String walletAddress = User.getInstance().getWalletAddress(context);

            String fromPubKey = User.getInstance().getFromPubKey(context);

            long totalBlock = User.getInstance().getTotalBlock(context);

            int assetsCount = User.getInstance().getAssetsCount(context);

            String assetListString = User.getInstance().getAssetListString(context);

            String passwordSecret = User.getInstance().getPasswordMd5(context);

            String newPassSecret = User.getInstance().getNewPasswordMd5(context);

            WalletInfo.getInstance().setInitWalletType(context,initWalletType);

            WalletInfo.getInstance().setMacaroonString(context,macaroonString);

            if (!isSecreted){
                String newSeedString = SecretAESOperator.getInstance().encrypt(seedString);

                WalletInfo.getInstance().setSeedString(context,newSeedString);

                String newRecoverySeedString = SecretAESOperator.getInstance().encrypt(recoverySeedString);

                WalletInfo.getInstance().setRecoverySeedString(context,newRecoverySeedString);
            }

            WalletInfo.getInstance().setHeaderBinChecked(context,headerBinChecked);

            WalletInfo.getInstance().setFilterHeaderBinChecked(context,filterHeaderBinChecked);

            WalletInfo.getInstance().setNeutrinoDbChecked(context,neutrinoDbChecked);

            WalletInfo.getInstance().setAlias(context,alias);

            WalletInfo.getInstance().setWalletAddress(context,walletAddress);

            WalletInfo.getInstance().setFromPubKey(context,fromPubKey);

            WalletInfo.getInstance().setTotalBlock(context,totalBlock);

            WalletInfo.getInstance().setAssetsCount(context,assetsCount);

            WalletInfo.getInstance().setAssetListString(context,assetListString);

            WalletInfo.getInstance().setPasswordSecret(context,passwordSecret);

            WalletInfo.getInstance().setNewPassSecret(context,newPassSecret);

            User.getInstance().setWalletInfoMoved(context,true);

            User.getInstance().setSeedSecreted(context,true);
        }


    }
}
