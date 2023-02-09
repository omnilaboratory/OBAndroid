# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-----------------------------------------规则描述--------------------------------------------------
#关键字                      描述
#keep                        保留类和类中的成员，防止被混淆或移除
#keepnames                   保留类和类中的成员，防止被混淆，成员没有被引用会被移除
#keepclassmembers            只保留类中的成员，防止被混淆或移除
#keepclassmembernames        只保留类中的成员，防止被混淆，成员没有引用会被移除
#keepclasseswithmembers      保留类和类中的成员，防止被混淆或移除，保留指明的成员
#keepclasseswithmembernames  保留类和类中的成员，防止被混淆，保留指明的成员，成员没有引用会被移除

#通配符      描述
#<field>     匹配类中的所有字段
#<method>    匹配类中所有的方法
#<init>      匹配类中所有的构造函数
#*           匹配任意长度字符，不包含包名分隔符(.)
#**          匹配任意长度字符，包含包名分隔符(.)
#***         匹配任意参数类型
#---------------------------------------------------------------------------------------------------
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*         # 混淆时所采用的算法
-optimizationpasses 5                                                            # 指定代码的压缩级别
-allowaccessmodification                                                        # 允许访问并修改有修饰符的类和类的成员
#-------------------------------------------通用区域----------------------------------------------
#混淆时不使用大小写混合类名
-dontusemixedcaseclassnames
#不跳过library中的非public的类
-dontskipnonpubliclibraryclasses
#打印混淆的详细信息
-verbose
# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
#不进行优化，建议使用此选项，理由见上
-dontoptimize
#不进行预校验，预校验是作用在Java平台上的，Android平台上不需要这项功能，去掉之后还可以加快混淆速度
-dontpreverify
#保留注解参数
-keepattributes *Annotation*
#保留Google原生服务需要的类
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
#//保留native方法的类名和方法名
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
#//保留自定义View,如"属性动画"中的set/get方法
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
   public <init>(android.branch.Context);
   public <init>(android.branch.Context, android.util.AttributeSet);
   public <init>(android.branch.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    public <init>(android.branch.Context, android.util.AttributeSet);
    public <init>(android.branch.Context, android.util.AttributeSet, int);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
#//保留混淆枚举中的values()和valueOf()方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#//Parcelable实现类中的CREATOR字段是绝对不能改变的，包括大小写
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
#不混淆Parcelable和它的子类，还有Creator成员变量
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class **.R$* {
  *;
 }
-keepclassmembers class * {
    void *(**On*Event);
}


#webview
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}

#不需要混淆的Android类
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.branch.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.branch.ContentProvider
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**

# 删除代码中Log相关的代码
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static *** d(...);
    public static *** e(...);
    public static *** i(...);
    public static *** v(...);
    public static *** println(...);
    public static *** w(...);
    public static *** wtf(...);
}

#保留使用@keep注解的类名和方法
-keep,allowobfuscation @interface android.support.annotation.Keep
-keep @android.support.annotation.Keep class *
#---------------------------------------------------------------------------------------------------

################# 抑制所有警告 #################
-ignorewarnings
################# 抑制所有警告 #################

#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------注解相关---------------------------------
#注解不混淆
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }
-keepattributes *Annotation*

#-keep @com.sdic_crit.android.baselibrary.ioc class *
###保留类中使用了自定义IOC注解和权限框架注解的成员变量和方法，防止被混淆和移除
-keepclassmembers class * {
    @com.sdic_crit.android.baselibrary.ioc.* <fields>;
    @com.sdic_crit.android.baselibrary.ioc.* <methods>;
    @com.sdic_crit.android.baselibrary.permission.* <methods>;
}
#-keepclassmembers, allowobfuscation class * {
#    @com.sdic_crit.android.baselibrary.ioc.* <fields>;
#    @com.sdic_crit.android.baselibrary.ioc.* <methods>;
#}


## 自定义IOC框架相关，里边所有使用注解的方法参数都是View，混淆之后注解失效
##//保留Activity中参数是View的方法，如XML中配置android:onClick=”buttonClick”属性，Activity中调用的buttonClick(View view)方法
#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#   private void *(android.view.View);
#}
#
##//保留Fragment中参数是View的方法
#-keepclassmembers class * extends android.support.v4.app.Fragment {
#   public void *(android.view.View);
#   private void *(android.view.View);
#}


###权限框架相关
##//保留拨打电话权限相关
#-keepclassmembers class com.sdic_crit.android.framelibrary.view.BottomServiceNumView {
#   private void callPhoneNumber();
#   private void deniedCallPhone();
#}
#
#-keepclassmembers class com.sdic_crit.android.ui.fragment.FragmentMine {
#   private void callPhoneNumber();
#   private void deniedCallPhone();
#}
#-------------------------------------------------------------------------


#---------------------------------1.实体类---------------------------------
#不混淆的实体类
#baseLibrary
-keep class testnet.baselibrary.entity.** {*;}
-dontwarn testnet.baselibrary.entity.**
#frameLibrary
-keep class testnet.framelibrary.entity.** {*;}
-dontwarn  testnet.framelibrary.entity.**
#gallery
-keep class testnet.gallery.entity.** {*;}
-dontwarn  testnet.gallery.entity.**
#app
-keep class testnet.entity.** {*;}
-dontwarn testnet.entity.**
-keep class testnet.view.filterMenu.buildingClaimFilterMenu.entity.** {*;}
-dontwarn testnet.view.filterMenu.buildingClaimFilterMenu.entity.**
-keep class testnet.view.filterMenu.buildingFilterMenu.entity.** {*;}
-dontwarn testnet.view.filterMenu.buildingFilterMenu.entity.**
-keep class testnet.view.filterMenu.UserJourneyFilterMenu.entity.** {*;}
-dontwarn testnet.view.filterMenu.UserJourneyFilterMenu.entity.**
#thirdSuppord
-keep class testnet.thirdsupport.umeng.share.bean.** {*;}
-dontwarn  testnet.thirdsupport.umeng.share.bean.**
-keep class testnet.thirdsupport.umeng.push.entity.** {*;}
-dontwarn  testnet.thirdsupport.umeng.push.entity.**
-keep class testnet.thirdsupport.location.entity.** {*;}
-dontwarn testnet.thirdsupport.location.entity.**
-keep class testnet.thirdsupport.tiktok.auth.bean.** {*;}
-dontwarn testnet.thirdsupport.tiktok.auth.bean.**
-keep class testnet.thirdsupport.tiktok.share.bean.** {*;}
-dontwarn testnet.thirdsupport.tiktok.share.bean.**

#IM
-keep class testnet.im.entity.** {*;}
-dontwarn testnet.im.entity.**



#-------------------------------------------------------------------------
#-----------反射修改了TabLayout的Indicator宽度，不混淆TabLaout避免报错------------------
-keep class android.support.design.widget.TabLayout{*;}
-keepclasseswithmembernames class android.support.design.widget.TabLayout {*;}
#-------------------------------------------------------------------------
##不知道干嘛的，百度地图集成之后好多warming之后才添加的
#-keepattributes EnclosingMethod
#-keepattributes InnerClasses
#-dontoptimize
#-optimizations optimization_filter
#---------------------------------2.第三方包-------------------------------

#微信
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}

##百度定位
##jar不知道写这里会不会报错
##-libraryjars libs/BaiduLBS_Android.jar #替换成自己所用版本的jar包
#-keep class com.baidu.mapapi.** {*;}
#-keep class com.baidu.** { *; }
#-keep class vi.com.gdi.bgl.android.**{*;}


#GreenDao
#greendao3.2.0,此是针对3.2.0，如果是之前的，可能需要更换下包名
-keep class org.greenrobot.greendao.**{*;}
-keep public interface org.greenrobot.greendao.**
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keep class net.sqlcipher.database.**{*;}
-keep public interface net.sqlcipher.database.**
-dontwarn net.sqlcipher.database.**
-dontwarn org.greenrobot.greendao.**

##百度OCR相关
#-keep class com.baidu.ocr.sdk.**{*;}
#
##阿里OOS云存储文件上传
#-keep class com.alibaba.sdk.android.oss.** { *; }
#-dontwarn okio.**
#-dontwarn org.apache.commons.codec.binary.**


#阿里巴巴ARouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

#butterKnife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.** -keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

##极光推送相关
#-dontoptimize
#-dontpreverify
#
#-dontwarn cn.jpush.**
#-keep class cn.jpush.** { *; }
#-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
#
#-dontwarn cn.jiguang.**
#-keep class cn.jiguang.** { *; }
##极光推送2.0.5 ~ 2.1.7 版本有引入 gson 和 protobuf ，增加排除混淆的配置。(2.1.8版本不需配置)


##==================gson && protobuf==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}


#KLog相关
-keep class org.dom4j.** {*;}
-dontwarn org.dom4j.**


#Gson混淆
-keep class com.google.gson.** { *; }
-dontwarn  com.google.gson.**

#OKHttp3混淆
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

# Glide混淆
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

#EventBus混淆
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


#友盟分享
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep class com.umeng.commonsdk.** {*;}

-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class com.sdic_crit.R$*{
    public static final int *;
}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class com.linkedin.** { *; }
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keepattributes Signature

#友盟统计混淆
-keep class com.umeng.** {*;}
-keepclassmembers class * {
    public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#友盟推送混淆
-dontwarn com.umeng.**
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**
-keepattributes *Annotation*
-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}
-keep public class **.R$*{
    public static final int *;
}

#SDK需要引用导入工程的资源文件，通过了反射机制得到资源引用文件R.java，
#但是在开发者通过proguard等混淆/优化工具处理apk时，proguard可能会将R.java删除，如果遇到这个问题，请添加如下配置
-keep public class testnet.R$*{
    public static final int *;
}

#环信
-keep class org.xmlpull.** {*;}
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**
-keep class com.superrtc.** {*;}
#环信使用的百度地图相关混淆
-keep class com.baidu.** {*;}
-keep class vi.com.gdi.bgl.**{*;}
# 必加的，不加的话环信发送图片或者语音会崩溃
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}

#百度地图
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**

# 快钱聚合支付混淆过滤
-dontwarn com.kuaiqian.**
-keep class com.kuaiqian.** {*;}

# 微信混淆过滤
-dontwarn  com.tencent.**
-keep class com.tencent.** {*;}
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-keep class com.tencent.mm.sdk.** {*;}

# 内部WebView混淆过滤
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
# 友盟混淆过滤
-dontwarn com.umeng.**
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class com.meizu.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}
#-------------------------------------------------------------------------
