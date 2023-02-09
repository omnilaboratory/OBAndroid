package com.omni.testnet.baselibrary.utils;

/**
 * 数字工具类
 */

public class NumberUtils {
    private static String[] s1 = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
    private static String[] s2 = {"十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};

    public static String toChinese(String string) {
        String result = "";
        int n = string.length();
        for (int i = 0; i < n; i++) {
            int num = string.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }
            System.out.println("  " + result);
        }
        System.out.println(result);
        return result;
    }
//    //num 表示数字，lower表示小写，upper表示大写
//    private static final String[] num_lower = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
//    private static final String[] num_upper = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
//
//    //unit 表示单位权值，lower表示小写，upper表示大写
//    private static final String[] unit_lower = {"", "十", "百", "千"};
//    private static final String[] unit_upper = {"", "拾", "佰", "仟"};
//    private static final String[] unit_common = {"", "万", "亿", "兆", "京", "垓", "秭", "穰", "沟", "涧", "正", "载"};
//
//    //允许的格式
//    private static final List<String> promessTypes = Arrays.asList("INTEGER", "INT", "LONG", "DECIMAL", "FLOAT", "DOUBLE", "STRING", "BYTE", "TYPE", "SHORT");
//
//    /**
//     * 数字转化为小写的汉字
//     *
//     * @param num 将要转化的数字
//     * @return
//     */
//    public static String toChineseLower(Object num) {
//        return format(num, num_lower, unit_lower);
//    }
//
//    /**
//     * 数字转化为大写的汉字
//     *
//     * @param num 将要转化的数字
//     * @return
//     */
//    public static String toChineseUpper(Object num) {
//        return format(num, num_upper, unit_upper);
//    }
//
//    /**
//     * 格式化数字
//     *
//     * @param num      原数字
//     * @param numArray 数字大小写数组
//     * @param unit     单位权值
//     * @return
//     */
//    private static String format(Object num, String[] numArray, String[] unit) {
//        if (!promessTypes.contains(num.getClass().getSimpleName().toUpperCase())) {
//            throw new RuntimeException("不支持的格式类型");
//        }
//        //获取整数部分
//        String intnum = getInt(String.valueOf(num));
//        //获取小数部分
//        String decimal = getFraction(String.valueOf(num));
//        //格式化整数部分
//        String result = formatIntPart(intnum, numArray, unit);
//        if (!"".equals(decimal)) {//小数部分不为空
//            //格式化小数
//            result += "点" + formatFractionalPart(decimal, numArray);
//        }
//        return result;
//    }
//
//    /**
//     * 格式化整数部分
//     *
//     * @param num      整数部分
//     * @param numArray 数字大小写数组
//     * @return
//     */
//    private static String formatIntPart(String num, String[] numArray, String[] unit) {
//
//        //按4位分割成不同的组（不足四位的前面补0）
//        Integer[] intnums = split2IntArray(num);
//
//        boolean zero = false;
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < intnums.length; i++) {
//            //格式化当前4位
//            String r = formatInt(intnums[i], numArray, unit);
//            if ("".equals(r)) {//
//                if ((i + 1) == intnums.length) {
//                    sb.append(numArray[0]);//结果中追加“零”
//                } else {
//                    zero = true;
//                }
//            } else {//当前4位格式化结果不为空（即不为0）
//                if (zero || (i > 0 && intnums[i] < 1000)) {//如果前4位为0，当前4位不为0
//                    sb.append(numArray[0]);//结果中追加“零”
//                }
//                sb.append(r);
//                sb.append(unit_common[intnums.length - 1 - i]);//在结果中添加权值
//                zero = false;
//            }
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 格式化小数部分
//     *
//     * @param decimal  小数部分
//     * @param numArray 数字大小写数组
//     * @return
//     */
//    private static String formatFractionalPart(String decimal, String[] numArray) {
//        char[] val = String.valueOf(decimal).toCharArray();
//        int len = val.length;
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < len; i++) {
//            int n = Integer.valueOf(val[i] + "");
//            sb.append(numArray[n]);
//        }
//        return sb.toString();
//    }
//
//    /**
//     * 获取整数部分
//     *
//     * @param num
//     * @return
//     */
//    private static String getInt(String num) {
//        //检查格式
//        checkNum(num);
//
//        char[] val = String.valueOf(num).toCharArray();
//        StringBuffer sb = new StringBuffer();
//        int t, s = 0;
//        for (int i = 0; i < val.length; i++) {
//            if (val[i] == '.') {
//                break;
//            }
//            t = Integer.parseInt(val[i] + "", 16);
//            if (s + t == 0) {
//                continue;
//            }
//            sb.append(t);
//            s += t;
//        }
//        return (sb.length() == 0 ? "0" : sb.toString());
//    }
//
//    /**
//     * 获取小数部分
//     *
//     * @param num
//     * @return
//     */
//    private static String getFraction(String num) {
//        int i = num.lastIndexOf(".");
//        if (num.indexOf(".") != i) {
//            throw new RuntimeException("数字格式不正确，最多只能有一位小数点！");
//        }
//        String fraction = "";
//        if (i >= 0) {
//            fraction = getInt(new StringBuffer(num).reverse().toString());
//            if (fraction.equals("0")) {
//                return "";
//            }
//        }
//        return new StringBuffer(fraction).reverse().toString();
//    }
//
//    /**
//     * 检查数字格式
//     *
//     * @param num
//     */
//    private static void checkNum(String num) {
//        if (num.indexOf(".") != num.lastIndexOf(".")) {
//            throw new RuntimeException("数字[" + num + "]格式不正确!");
//        }
//        if (num.indexOf("-") != num.lastIndexOf("-") || num.lastIndexOf("-") > 0) {
//            throw new RuntimeException("数字[" + num + "]格式不正确！");
//        }
//        if (num.indexOf("+") != num.lastIndexOf("+")) {
//            throw new RuntimeException("数字[" + num + "]格式不正确！");
//        }
//        if (num.indexOf("+") != num.lastIndexOf("+")) {
//            throw new RuntimeException("数字[" + num + "]格式不正确！");
//        }
//        if (num.replaceAll("[\\d|\\.|\\-|\\+]", "").length() > 0) {
//            throw new RuntimeException("数字[" + num + "]格式不正确！");
//        }
//    }
}
