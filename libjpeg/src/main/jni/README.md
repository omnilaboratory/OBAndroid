Buid libjpeg-turbo-android-lib
==================

1.To get started, ensure you have the latest NDK
You must configure the path of JDK and Android SDK.

    echo "export ANDROID_HOME='Your android ndk path'" >> ~/.bash_profile

    source ~/.bash_profile


2.Build libjpeg-turbo.so

    cd ../libjpeg-turbo-android/libjpeg-turbo/jni

    ndk-buld

3.You can get libjpegpi.so in 

     ../libjpeg-turbo-android/libjpeg-turbo/libs/armeabi


4.Copy libjpegpi.so to ../bither-android-lib/libjpeg-turbo-android/use-libjpeg-trubo-adnroid/jni

     cd ../bither-android-lib/libjpeg-turbo-android/use-libjpeg-trubo-adnroid/jni

     ndk-build

5.You can get libjpegpi.so and libpijni.so 


6.Use libjpeg-turbo in java 

     static {

        System.loadLibrary("jpegpi");
       
        System.loadLibrary("pijni");

     }
 and you must use class of "com.pi.common.util.NativeUtil"
