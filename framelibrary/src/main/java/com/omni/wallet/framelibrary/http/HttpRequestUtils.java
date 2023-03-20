package com.omni.wallet.framelibrary.http;

import android.content.Context;

import com.omni.wallet.baselibrary.common.Constants;
import com.omni.wallet.baselibrary.http.HttpUtils;
import com.omni.wallet.framelibrary.http.callback.DefaultHttpCallback;
import com.omni.wallet.framelibrary.http.callback.DefaultHttpListCallback;

import java.io.File;
import java.util.Map;


/**
 * 接口请求工具类
 */
public class HttpRequestUtils {
    private static final String TAG = HttpRequestUtils.class.getSimpleName();

    private HttpRequestUtils() {
    }

    // 线上环境
    private static final String SERVICE_ADDRESS_BASE_ONLINE = "https://butler.xfangl.com";
    // 测试环境
    private static final String SERVICE_ADDRESS_BASE_TEST = "http://47.92.118.113:292";
//    // 徐大哥本地
//    private static final String SERVICE_ADDRESS_BASE_TEST = "http://10.39.1.92:8089";
//    // 英浩本地
//    private static final String SERVICE_ADDRESS_BASE_TEST = "http://10.39.2.88:8089";
//    // 尧尧本地
//    private static final String SERVICE_ADDRESS_BASE_TEST = "http://10.39.1.225:8089";

    // H5静态页面的测试环境
    private static final String SERVICE_ADDRESS_H5_TEST = "https://xh5.xfangl.com";
    // H5静态页面的正式环境
    private static final String SERVICE_ADDRESS_H5_ONLINE = "https://h5.xfangl.com";
    //
    // 竞拍长连接
    public static final String AUCTION_SOCKET = "";

    /**
     * 根据是否Debug模式获取地址前缀
     */
    public static String getBaseAddress() {
        if (Constants.isDebug) {
            return SERVICE_ADDRESS_BASE_TEST;
        } else {
            return SERVICE_ADDRESS_BASE_ONLINE;
        }
    }

    /**
     * 根据是否Debug模式获取静态H5页面的Host
     */
    private static String getH5Address() {
        if (Constants.isDebug) {
            return SERVICE_ADDRESS_H5_TEST;
        } else {
            return SERVICE_ADDRESS_H5_ONLINE;
        }
    }


    // 登录接口
    private static final String LOGIN = getBaseAddress() + "/butler/user/login";
    // 获取下拉属性接口
    private static final String GET_CONDITION = getBaseAddress() + "/butler/user/getSysAttributesList";
    // 首页接口
    private static final String MAIN_PAGE_INFO = getBaseAddress() + "/butlerBuilding/index/getIndex";
    // 我的页面接口
    private static final String USER_INFO = getBaseAddress() + "/butler/user/findButlerInfo";
    // 资金明细接口
    private static final String USER_MONEY_DETAILS = getBaseAddress() + "/butler/user/findMoneyDetails";
    // 客户通讯录接口
    private static final String USER_CUSTOMER_COMMUNICATION = getBaseAddress() + "/butler/user/findCustomerCommunication";
    // 业主认证列表页面接口
    private static final String SHOW_OWNER_CERTIFICATION = getBaseAddress() + "/butler/user/showOwnerCertification";
    // 业主信息认证页面底部按钮接口
    private static final String OWNER_CERTIFICATION = getBaseAddress() + "/butler/user/ownerCertification";
    // 忘记密码接口（验证验证码）
    private static final String VERIFY_SMS = getBaseAddress() + "/butler/user/verificationCode";
    // 修改密码接口（重置密码）
    private static final String FORGET_PSW = getBaseAddress() + "/butler/user/forgetPassword";
    // 发送验证码接口
    private static final String FORGOT_PSW_SEND_SMS = getBaseAddress() + "/butler/user/sendcode";
    // 更新业主信息接口
    private static final String UPDATE_OWNER = getBaseAddress() + "/butler/user/addOwners";
    // 获取业主管理信息接口
    private static final String GET_OWNER_MANAGER_INFO = getBaseAddress() + "/butlerBuilding/index/getBusiness";
    // 获取业主管理信息接口
    private static final String GET_OWNER_INFO = getBaseAddress() + "/butlerBuilding/index/getOwnerInformation";
    // 获取省接口
    private static final String GET_PROVINCE_LIST = getBaseAddress() + "/butlerBuilding/index/getProvinceList";
    // 获取市接口
    private static final String GET_CITY_LIST = getBaseAddress() + "/butlerBuilding/index/getCityList";
    // 检查更新接口
    private static final String CHECK_UPDATE = getBaseAddress() + "/butler/user/checkUpdate";
    // 修改密码接口
    private static final String MODIFY_PSW = getBaseAddress() + "/butler/user/updatePassword";
    // 更换头像接口
    private static final String UPDATE_HEADER = getBaseAddress() + "/butler/user/updateUserInfo";
    // 打开红包接口
    private static final String RED_PACKET_MONEY = getBaseAddress() + "/butler/user/butlerGetMoney";
    // 获取二维码名片接口
    private static final String QR_CODE_CREATE_IMAGE = getBaseAddress() + "/butler/user/createImage";
    // 获取活动邀请记录列表接口
    private static final String GET_ACTION_INVITE_LIST = getBaseAddress() + "/butler/view/house/list";
    // 获取活动邀请明细接口
    private static final String GET_ACTION_INVITE_DETAILS = getBaseAddress() + "/butler/view/house/detail";
    // 查询用户提现信息接口
    private static final String USER_CASH_INFO = getBaseAddress() + "/butler/withdraw/getInfo";
    // 用户提现接口
    private static final String USER_CASH = getBaseAddress() + "/butler/withdraw/apply";
    // 提现说明接口
    private static final String EXTRACT_TYPE = getBaseAddress() + "/butler/withdraw/getExtractType";
    // 获取首页看房团分享信息接口
    private static final String GET_LOOK_HOUSE_ACT_SHARE_INFO = getBaseAddress() + "/butler/view/house/getShareInfo";
    // 获取意向客户手机号验证码接口
    private static final String GET_INTENT_CUSTOMER_SMS_CODE = getBaseAddress() + "/butler/report/checkCode";
    // 添加意向客户接口
    private static final String ADD_INTENT_CUSTOMER = getBaseAddress() + "/butler/report/insert";
    // 获取意向客户列表接口
    private static final String GET_REPORT_LIST = getBaseAddress() + "/butler/report/list";
    // 获取贡献值明细列表接口
    private static final String GET_CONTRIBUTION_VALUE_LIST = getBaseAddress() + "/butler/user/findValueDetails";
    //***************************************H5页面地址********************************************//
    // 邀请业主赚佣金的H5地址
    public static final String INVITE_OWNER = getH5Address() + "/mobile/page/xflProperty/OwnerCommission.html";
    // 用户协议
    public static final String USER_AGREEMENT = getH5Address() + "/mobile/page/agreement/property/userAgreement.html";
    // 隐私协议
    public static final String USER_SECRET_AGREEMENT = getH5Address() + "/mobile/page/agreement/property/privacyProtocol.html";
    // 关于我们
    public static final String USER_ABOUT_US = getH5Address() + "/mobile/page/agreement/property/about.html";
//    // 朱岩本地
//    public static final String INVITE_OWNER = "http://10.39.0.158:8080/page/xflProperty/OwnerCommission.html";

    //***************************************接口方法********************************************//

    /**
     * 登录接口
     *
     * @param context  上下文
     * @param phone    手机号
     * @param psw      密码
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void login(Context context, String phone, String psw, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(LOGIN)
                .addParams("account", phone)
                .addParams("password", psw)
                .execute(callback);
    }

    /**
     * 根据type获取筛选条件
     *
     * @param context  上下文
     * @param type     类型
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getSelectCondition(Context context, int type, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(GET_CONDITION)
                .addParams("type", type)
                .execute(callback);
    }

    public static <T> void searchCustomer(Context context, String text, DefaultHttpListCallback<T> callback) {

    }

    /**
     * 首页接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getMainPageInfo(Context context, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(MAIN_PAGE_INFO)
                .execute(callback);
    }

    /**
     * 我的页面接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getUserInfo(Context context, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(USER_INFO)
                .execute(callback);
    }

    /**
     * 资金明细接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void findMoneyDetails(Context context, String companyId, int budgetType, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(USER_MONEY_DETAILS)
                .addParams("companyId", companyId)
                .addParams("budgetType", budgetType)
                .execute(callback);
    }

    /**
     * 客户通讯录接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void findCustomerCommunication(Context context, int type, int page, int num, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(USER_CUSTOMER_COMMUNICATION)
                .addParams("type", type)
                .addParams("pageNo", page)
                .addParams("pageSize", num)
                .execute(callback);
    }

    /**
     * 业主认证列表页面
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void showOwnerCertification(Context context, int page, int num, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(SHOW_OWNER_CERTIFICATION)
                .addParams("pageNo", page)
                .addParams("pageSize", num)
                .execute(callback);
    }

    /**
     * 业主信息认证页面底部按钮
     *
     * @param context           上下文
     * @param isAuth            是否认证(1认证通过   2认证未通过)
     * @param butlerRoomOwnerId 物业管家房间业主关系表id
     * @param callback          回调
     * @param <T>               泛型
     */
    public static <T> void ownerCertification(Context context, int isAuth, String butlerRoomOwnerId, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(OWNER_CERTIFICATION)
                .addParams("isAuth", isAuth)
                .addParams("butlerRoomOwnerId", butlerRoomOwnerId)
                .execute(callback);
    }

    /**
     * 修改密码
     *
     * @param context  上下文
     * @param phone    手机号
     * @param code     验证码
     * @param newPsw   新密码
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void forgetPsw(Context context, String phone, String code, String newPsw, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(FORGET_PSW)
                .addParams("account", phone)
                .addParams("code", code)
                .addParams("password", newPsw)
                .execute(callback);
    }


    /**
     * 验证短信验证码
     *
     * @param context  上下文
     * @param phone    手机号
     * @param code     验证码
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void verifySMSCode(Context context, String phone, String code, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(VERIFY_SMS)
                .addParams("account", phone)
                .addParams("code", code)
                .execute(callback);
    }

    /**
     * 忘记密码发送短信验证码
     *
     * @param context  上下文
     * @param phone    手机号
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void forgotPswSendSMS(Context context, String phone, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(FORGOT_PSW_SEND_SMS)
                .addParams("mobile", phone)
                .execute(callback);
    }

    /**
     * 新增业主接口
     *
     * @param context  上下文
     * @param params   参数Map
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void updateOwner(Context context, Map<String, Object> params, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(UPDATE_OWNER)
                .addParams(params)
                .execute(callback);
    }

    /**
     * 获取业主管理信息接口
     *
     * @param context    上下文
     * @param buildingId 楼盘ID
     * @param callback   回调
     * @param <T>        泛型
     */
    public static <T> void getOwnerManagerInfo(Context context, String buildingId, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(GET_OWNER_MANAGER_INFO)
                .addParams("buildingId", buildingId)
                .execute(callback);
    }

    /**
     * 获取业主信息
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getOwnerInfo(Context context, Map<String, Object> params, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(GET_OWNER_INFO)
                .addParams(params)
                .execute(callback);
    }

    /**
     * 获取省列表
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getProvinceList(Context context, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(GET_PROVINCE_LIST)
                .execute(callback);
    }

    /**
     * 获取市列表
     *
     * @param context    上下文
     * @param provinceId 省ID
     * @param callback   回调
     * @param <T>        泛型
     */
    public static <T> void getCityList(Context context, String provinceId, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(GET_CITY_LIST)
                .addParams("provinceid", provinceId)
                .execute(callback);
    }

    /**
     * 检查更新接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void checkUpdate(Context context, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(CHECK_UPDATE)
                .execute(callback);
    }

    /**
     * 修改密码
     *
     * @param context  上下文
     * @param newPsw1  新密码
     * @param newPsw2  确认新密码
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void modifyPsw(Context context, String newPsw1, String newPsw2, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(MODIFY_PSW)
                .addParams("oldpassword", newPsw1)
                .addParams("newpassword", newPsw2)
                .execute(callback);
    }

    /**
     * 修改头像接口
     *
     * @param context  上下文
     * @param heard    头像
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void updateHeader(Context context, File heard, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .upload()
                .url(UPDATE_HEADER)
                .addHeader("Content-Type", "multipart/form-data")
                .addParams("headurl", heard)
                .execute(callback);
    }

    /**
     * 领取红包接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getRedPacketMoney(Context context, String roomId, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(RED_PACKET_MONEY)
                .addParams("roomId", roomId)
                .execute(callback);
    }

    /**
     * 获取二维码名片接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getCreateImage(Context context, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(QR_CODE_CREATE_IMAGE)
                .execute(callback);
    }

    /**
     * 获取看房团分享信息的接口
     *
     * @param context  上下文
     * @param actId    看房团活动ID
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getLookHouseActShareInfo(Context context, String actId, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(GET_LOOK_HOUSE_ACT_SHARE_INFO)
                .addParams("houseId", actId)
                .addParams("type", "1")// 分享方式 1app;2小程序;3H5
                .execute(callback);
    }

    /**
     * 获取活动邀请记录列表接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getActionInviteList(Context context, int page, int num, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .get()
                .url(GET_ACTION_INVITE_LIST)
                .addParams("pageNo", page)
                .addParams("pageSize", num)
                .execute(callback);
    }

    /**
     * 获取活动邀请明细接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getActionInviteDetails(Context context, String houseId, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .get()
                .url(GET_ACTION_INVITE_DETAILS)
                .addParams("houseId", houseId)
                .execute(callback);
    }

    /**
     * 获取用户提现银行卡信息接口
     *
     * @param context  上下文
     * @param type     3微信提现
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getUserCashInfo(Context context, String type, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(USER_CASH_INFO)
                .addParams("type", type)
                .execute(callback);
    }

    /**
     * 普通用户提现接口
     *
     * @param context  上下文
     * @param price    金额
     * @param type     3微信提现
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void userCash(Context context, String price, String type, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(USER_CASH)
                .addParams("price", price)
                .addParams("type", type)
                .execute(callback);
    }

    /**
     * 获取提现方式
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getExtractType(Context context, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .url(EXTRACT_TYPE)
                .execute(callback);
    }

    /**
     * 获取意向客户验证码
     *
     * @param context  上下文
     * @param mobile   手机号
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getCustomerSMSVerifyCode(Context context, String mobile, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(GET_INTENT_CUSTOMER_SMS_CODE)
                .addParams("mobile", mobile)
                .execute(callback);
    }

    /**
     * 添加意向客户
     *
     * @param context  上下文
     * @param params   参数Map
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void addIntentCustomer(Context context, Map<String, Object> params, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .post()
                .url(ADD_INTENT_CUSTOMER)
                .addParams(params)
                .execute(callback);
    }

    /**
     * 获取意向客户列表接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getInviteCustomerList(Context context, int page, int num, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .get()
                .url(GET_REPORT_LIST)
                .addParams("pageNo", page)
                .addParams("pageSize", num)
                .execute(callback);
    }

    /**
     * 获取意向客户列表接口
     *
     * @param context  上下文
     * @param callback 回调
     * @param <T>      泛型
     */
    public static <T> void getContributionValuerList(Context context, int page, int num, DefaultHttpCallback<T> callback) {
        HttpUtils.with(context)
                .get()
                .url(GET_CONTRIBUTION_VALUE_LIST)
                .addParams("pageNo", page)
                .addParams("pageSize", num)
                .execute(callback);
    }
}
