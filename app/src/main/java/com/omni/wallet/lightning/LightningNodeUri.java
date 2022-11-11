package com.omni.wallet.lightning;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class LightningNodeUri implements Serializable {

    private String mPubKey;
    private String mHost;
    private String mNickname;
    private String mDescription;
    private String mImage;

    private LightningNodeUri(@NonNull String pubKey, String host, String nickname, String description, String image) {
        mPubKey = pubKey;
        mHost = host;
        mNickname = nickname;
        mDescription = description;
        mImage = image;
    }

    @NonNull
    public String getPubKey() {
        return mPubKey;
    }

    public String getHost() {
        return mHost;
    }

    public String getNickname() {
        return mNickname;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImage() {
        return mImage;
    }

    public String getAsString() {
        String uri = mPubKey;
        if (mHost != null) {
            uri = uri + "@" + mHost;
        }
        return uri;
    }

    public boolean isTorUri() {
        if (getHost() == null) {
            return false;
        }
        return getHost().toLowerCase().contains("onion");
    }

    public static class Builder {
        private String mPubKey;
        private String mHost;
        private String mNickname;
        private String mDescription;
        private String mImage;

        public Builder setPubKey(@NonNull String pubKey) {
            this.mPubKey = pubKey;

            return this;
        }

        public Builder setHost(String host) {
            this.mHost = host;

            return this;
        }

        public Builder setNickname(String nickname) {
            this.mNickname = nickname;

            return this;
        }

        public Builder setDescription(String description) {
            this.mDescription = description;

            return this;
        }

        public Builder setImage(String image) {
            this.mImage = image;

            return this;
        }

        public LightningNodeUri build() {
            return new LightningNodeUri(mPubKey, mHost, mNickname, mDescription, mImage);
        }
    }
}
