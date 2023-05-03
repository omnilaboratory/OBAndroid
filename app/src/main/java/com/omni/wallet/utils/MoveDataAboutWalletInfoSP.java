package com.omni.wallet.utils;

import android.content.Context;

import com.omni.wallet.SharedPreferences.WalletInfo;
import com.omni.wallet.common.ConstantInOB;
import com.omni.wallet.common.NetworkType;
import com.omni.wallet.framelibrary.entity.User;

public class MoveDataAboutWalletInfoSP {
    public static void moveData(Context context) {
        boolean isMoved = User.getInstance().getWalletInfoMoved(context);

        boolean isSecreted = User.getInstance().getSeedSecreted(context);

        NetworkType networkType = ConstantInOB.networkType;

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

            String nodeVersion = User.getInstance().getNodeVersion(context);

            WalletInfo.getInstance().setInitWalletType(context,initWalletType,networkType);

            WalletInfo.getInstance().setMacaroonString(context,macaroonString,networkType);

            if (!isSecreted){
                String newSeedString = SecretAESOperator.getInstance().encrypt(seedString);

                WalletInfo.getInstance().setSeedString(context,newSeedString,networkType);

                String newRecoverySeedString = SecretAESOperator.getInstance().encrypt(recoverySeedString);

                WalletInfo.getInstance().setRecoverySeedString(context,newRecoverySeedString,networkType);
            }

            WalletInfo.getInstance().setHeaderBinChecked(context,headerBinChecked,networkType);

            WalletInfo.getInstance().setFilterHeaderBinChecked(context,filterHeaderBinChecked,networkType);

            WalletInfo.getInstance().setNeutrinoDbChecked(context,neutrinoDbChecked,networkType);

            WalletInfo.getInstance().setAlias(context,alias,networkType);

            WalletInfo.getInstance().setWalletAddress(context,walletAddress,networkType);

            WalletInfo.getInstance().setFromPubKey(context,fromPubKey,networkType);

            WalletInfo.getInstance().setTotalBlock(context,totalBlock,networkType);

            WalletInfo.getInstance().setAssetsCount(context,assetsCount,networkType);

            WalletInfo.getInstance().setAssetListString(context,assetListString,networkType);

            WalletInfo.getInstance().setPasswordSecret(context,passwordSecret,networkType);

            WalletInfo.getInstance().setNewPassSecret(context,newPassSecret,networkType);

            WalletInfo.getInstance().setNodeVersion(context,nodeVersion,networkType);

            User.getInstance().setWalletInfoMoved(context,true);

            User.getInstance().setSeedSecreted(context,true);
        }

    }
}
