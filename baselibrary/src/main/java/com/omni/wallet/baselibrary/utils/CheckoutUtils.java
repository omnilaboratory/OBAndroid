package com.omni.wallet.baselibrary.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 合法性校验工具类
 */

public class CheckoutUtils {
    private static final String TAG = CheckoutUtils.class.getSimpleName();

    /**
     * 姓名合法性校验 （支持诸如：阿沛·阿旺晋美、卡尔·马克思等类型姓名匹配）
     */
    public static boolean isName(String name) {
        // 点前边要求1-15个汉字，点后边要求2-15个汉字，注意中间的点，别输错
        String regex = "[\u4E00-\u9FA5]{1,15}(?:·[\u4E00-\u9FA5]{2,15})*";
        return Pattern.matches(regex, name);
    }

    /**
     * 校验手机号是否11位、是否数字、是否1开头
     */
    public static boolean isAvailablePhone(String phone) {
        String regex = "^[1][0-9]{10}$";
        return Pattern.matches(regex, phone);
    }

    /**
     * 验证手机号格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1]\\d{1}\\d{9}";
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }


    /**
     * 检验字符串是否只有数字和字母（待定）
     */
    public static boolean isLegal(String content) {
        String str = "[a-zA-Z0-9_\\u4e00-\\u9fa5]*";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(content);
        return m.matches();
    }

    /**
     * 检验IP是否合法(待定)
     */
    public static boolean isIPLegal(String ip) {
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(ip);
        return m.matches();
    }

    /**
     * 判断身份证号格式是否正确
     **/
    public static boolean isSFZ(String sfz) {
        Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
        // String str =
        // "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        // Pattern p = Pattern.compile(str);
        Matcher idNumMatcher = idNumPattern.matcher(sfz);
        // Matcher m = p.matcher(sfz);
        return idNumMatcher.matches();
    }


    /**
     * 判断email格式是否正确
     ***/
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 校验是否不含有汉字
     */
    public static boolean isNotCharacters(String text) {
        String regex = "^[A-Za-z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(text);
        return match.matches();
    }

    /**
     * 判断除 汉字、字母、数字。的字符串， 符合时返回true
     */
    public static boolean isCharacter(String text) {
        String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(text);
        return match.matches();
    }

    /**
     * 邮箱是否合法
     */
    public static boolean isValidEmail(String mail) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
        Matcher mc = pattern.matcher(mail);
        return mc.matches();
    }

    /**
     * 密码合法性校验 （只支持数字和字母,首尾可以有空格）
     */
    public static boolean isPassword(String password) {
        if (!TextUtils.isEmpty(password)) {
            // String regex = "^\\s*[a-zA-Z0-9]{6,16}\\s*$";//开头结尾可以有空格
            String regex = "[a-zA-Z0-9]{6,16}";// 开头结尾和中间不能有空格，长度6-16位数
            return Pattern.matches(regex, password);
        }
        return false;
    }

    /**
     * 校验密码是否是数字字母组合形式(长度8-16位)
     */
    public static boolean checkIsNumAndChar(String psw) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        return psw.matches(regex);
    }


    /**
     * 判断字符串是不是数字
     */
    public static boolean checkIsNumber(String str) {
//        Pattern pattern = Pattern.compile("^[-+]?[0-9]");
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断字符串是不是数字(包括小数)
     */
    public static boolean checkIsNumber2(String str) {
        Pattern pattern = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 判断字符串是否是纯字母
     *
     * @param str 需要检查的字符窜
     * @return true ：是   false：否
     */
    public static boolean isPureAlphabet(String str) {
        String regExp = "^([A-Za-z]+)$";
        Pattern pat = Pattern.compile(regExp);
        Matcher mat = pat.matcher(str);
        return mat.matches();
    }

    /**
     * 校验是否字母开头
     */
    public static boolean checkIsStartWithChar(String str) {
        char c = str.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断字符串是否包含数字字母
     */
    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) { //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) { //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        return isDigit && isLetter;
    }
}
