package com.omni.wallet.baselibrary.utils;

import java.util.regex.Pattern;

/**
 * 银行卡工具类
 */

public class BankCardUtils {

    /**
     * 校验过程：
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，将个位十位数字相加，即将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     * 校验银行卡卡号
     */
    public static boolean checkBankCard(String bankCard) {
        if (bankCard.length() < 15 || bankCard.length() > 19) {
            return false;
        }
        char bit = getBankCardCheckCode(bankCard.substring(0, bankCard.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return bankCard.charAt(bankCard.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhn 校验算法获得校验位
     */
    public static char getBankCardCheckCode(String nonCheckCodeBankCard) {
        if (nonCheckCodeBankCard == null || nonCheckCodeBankCard.trim().length() == 0
                || !nonCheckCodeBankCard.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeBankCard.trim().toCharArray();
        int luhnSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhnSum += k;
        }
        return (luhnSum % 10 == 0) ? '0' : (char) ((10 - luhnSum % 10) + '0');
    }

    /**
     * 获取银行卡发卡行
     */
    public String getBankName(String bankCode) {
        if (Pattern.matches(s1, bankCode) || Pattern.matches(s2, bankCode) || Pattern.matches(s3, bankCode)) {
            return "邮储银行";
        } else if (Pattern.matches(s4, bankCode) || Pattern.matches(s5, bankCode) || Pattern.matches(s6, bankCode) || Pattern.matches(s7, bankCode) || Pattern.matches(s8, bankCode) || Pattern.matches(s9, bankCode) || Pattern.matches(s10, bankCode) || Pattern.matches(s11, bankCode) || Pattern.matches(s12, bankCode) || Pattern.matches(s13, bankCode)) {
            return "工商银行";
        } else if (Pattern.matches(s14, bankCode) || Pattern.matches(s15, bankCode) || Pattern.matches(s16, bankCode) || Pattern.matches(s17, bankCode) || Pattern.matches(s18, bankCode)) {
            return "农业银行";
        } else if (Pattern.matches(s19, bankCode) || Pattern.matches(s20, bankCode) || Pattern.matches(s21, bankCode) || Pattern.matches(s22, bankCode) || Pattern.matches(s23, bankCode) || Pattern.matches(s24, bankCode) || Pattern.matches(s25, bankCode)) {
            return "中国银行";
        } else if (Pattern.matches(s26, bankCode) || Pattern.matches(s27, bankCode) || Pattern.matches(s28, bankCode) || Pattern.matches(s29, bankCode) || Pattern.matches(s30, bankCode) || Pattern.matches(s31, bankCode) || Pattern.matches(s32, bankCode) || Pattern.matches(s33, bankCode)) {
            return "建设银行";
        } else if (Pattern.matches(s34, bankCode) || Pattern.matches(s35, bankCode) || Pattern.matches(s36, bankCode) || Pattern.matches(s37, bankCode) || Pattern.matches(s38, bankCode) || Pattern.matches(s39, bankCode) || Pattern.matches(s40, bankCode)) {
            return "交通银行";
        } else if (Pattern.matches(s41, bankCode) || Pattern.matches(s42, bankCode) || Pattern.matches(s43, bankCode) || Pattern.matches(s44, bankCode) || Pattern.matches(s45, bankCode) || Pattern.matches(s46, bankCode)) {
            return "招商银行";
        } else if (Pattern.matches(s47, bankCode) || Pattern.matches(s48, bankCode) || Pattern.matches(s49, bankCode)) {
            return "民生银行";
        } else if (Pattern.matches(s50, bankCode) || Pattern.matches(s51, bankCode) || Pattern.matches(s52, bankCode) || Pattern.matches(s53, bankCode) || Pattern.matches(s54, bankCode)) {
            return "光大银行";
        } else if (Pattern.matches(s55, bankCode) || Pattern.matches(s56, bankCode) || Pattern.matches(s57, bankCode) || Pattern.matches(s58, bankCode)) {
            return "中信银行";
        } else if (Pattern.matches(s59, bankCode) || Pattern.matches(s60, bankCode)) {
            return "华夏银行";
        } else if (Pattern.matches(s61, bankCode) || Pattern.matches(s62, bankCode) || Pattern.matches(s63, bankCode) || Pattern.matches(s64, bankCode)) {
            return "平安银行";
        } else if (Pattern.matches(s65, bankCode) || Pattern.matches(s66, bankCode) || Pattern.matches(s67, bankCode) || Pattern.matches(s68, bankCode) || Pattern.matches(s69, bankCode)) {
            return "兴业银行";
        } else if (Pattern.matches(s70, bankCode) || Pattern.matches(s71, bankCode) || Pattern.matches(s72, bankCode)) {
            return "上海银行";
        } else if (Pattern.matches(s73, bankCode) || Pattern.matches(s74, bankCode) || Pattern.matches(s75, bankCode) || Pattern.matches(s76, bankCode) || Pattern.matches(s77, bankCode)) {
            return "浦发银行";
        } else if (Pattern.matches(s79, bankCode) || Pattern.matches(s80, bankCode) || Pattern.matches(s81, bankCode) || Pattern.matches(s82, bankCode)) {
            return "广发银行";
        } else if (Pattern.matches(s83, bankCode)) {
            return "渤海银行";
        } else if (Pattern.matches(s84, bankCode)) {
            return "广州银行";
        } else if (Pattern.matches(s85, bankCode) || Pattern.matches(s86, bankCode)) {
            return "金华银行";
        } else if (Pattern.matches(s87, bankCode) || Pattern.matches(s88, bankCode)) {
            return "温州银行";
        } else if (Pattern.matches(s89, bankCode) || Pattern.matches(s90, bankCode) || Pattern.matches(s91, bankCode)) {
            return "徽商银行";
        } else if (Pattern.matches(s92, bankCode) || Pattern.matches(s93, bankCode) || Pattern.matches(s94, bankCode)) {
            return "江苏银行";
        } else if (Pattern.matches(s95, bankCode) || Pattern.matches(s96, bankCode)) {
            return "南京银行";
        } else if (Pattern.matches(s97, bankCode) || Pattern.matches(s98, bankCode) || Pattern.matches(s99, bankCode)) {
            return "宁波银行";
        } else if (Pattern.matches(s100, bankCode) || Pattern.matches(s101, bankCode)) {
            return "北京银行";
        } else if (Pattern.matches(s102, bankCode) || Pattern.matches(s103, bankCode)) {
            return "北京农村商业银行";
        } else if (Pattern.matches(s104, bankCode) || Pattern.matches(s105, bankCode) || Pattern.matches(s106, bankCode) || Pattern.matches(s107, bankCode)) {
            return "汇丰银行";
        } else if (Pattern.matches(s108, bankCode) || Pattern.matches(s109, bankCode)) {
            return "渣打银行";
        } else if (Pattern.matches(s110, bankCode) || Pattern.matches(s111, bankCode)) {
            return "花旗银行";
        } else if (Pattern.matches(s112, bankCode) || Pattern.matches(s113, bankCode) || Pattern.matches(s114, bankCode)) {
            return "东亚银行";
        } else if (Pattern.matches(s115, bankCode)) {
            return "广东华兴银行";
        } else if (Pattern.matches(s116, bankCode)) {
            return "深圳农村商业银行";
        } else if (Pattern.matches(s117, bankCode)) {
            return "广州农村商业银行";
        } else if (Pattern.matches(s118, bankCode) || Pattern.matches(s119, bankCode)) {
            return "东莞农村商业银行";
        } else if (Pattern.matches(s120, bankCode) || Pattern.matches(s121, bankCode) || Pattern.matches(s122, bankCode)) {
            return "东莞市商业银行";
        } else if (Pattern.matches(s123, bankCode) || Pattern.matches(s124, bankCode)) {
            return "广东省农村信用社联合社";
        } else if (Pattern.matches(s125, bankCode) || Pattern.matches(s126, bankCode) || Pattern.matches(s127, bankCode)) {
            return "大新银行";
        } else if (Pattern.matches(s128, bankCode) || Pattern.matches(s129, bankCode)) {
            return "永享银行";
        } else if (Pattern.matches(s130, bankCode) || Pattern.matches(s131, bankCode) || Pattern.matches(s132, bankCode)) {
            return "星展银行香港有限公司";
        } else if (Pattern.matches(s133, bankCode) || Pattern.matches(s134, bankCode)) {
            return "恒丰银行";
        } else if (Pattern.matches(s136, bankCode) || Pattern.matches(s135, bankCode) | Pattern.matches(s137, bankCode)) {
            return "天津市商业银行";
        } else if (Pattern.matches(s138, bankCode) || Pattern.matches(s139, bankCode)) {
            return "浙商银行";
        } else if (Pattern.matches(s140, bankCode) || Pattern.matches(s141, bankCode) || Pattern.matches(s142, bankCode) || Pattern.matches(s143, bankCode)) {
            return "南洋商业银行";
        } else if (Pattern.matches(s144, bankCode) || Pattern.matches(s145, bankCode) || Pattern.matches(s146, bankCode)) {
            return "厦门银行";
        } else if (Pattern.matches(s147, bankCode) || Pattern.matches(s148, bankCode) || Pattern.matches(s149, bankCode)) {
            return "福建海峡银行";
        } else if (Pattern.matches(s150, bankCode) || Pattern.matches(s151, bankCode) || Pattern.matches(s152, bankCode)) {
            return "吉林银行";
        } else if (Pattern.matches(s153, bankCode) || Pattern.matches(s154, bankCode)) {
            return "汉口银行";
        } else if (Pattern.matches(s155, bankCode) || Pattern.matches(s156, bankCode) || Pattern.matches(s157, bankCode) || Pattern.matches(s158, bankCode)) {
            return "盛京银行";
        } else if (Pattern.matches(s159, bankCode) || Pattern.matches(s160, bankCode) || Pattern.matches(s161, bankCode)) {
            return "大连银行";
        } else if (Pattern.matches(s162, bankCode) || Pattern.matches(s163, bankCode)) {
            return "河北银行";
        } else if (Pattern.matches(s164, bankCode) || Pattern.matches(s165, bankCode)) {
            return "乌鲁木齐商业银行";
        } else if (Pattern.matches(s166, bankCode) || Pattern.matches(s167, bankCode) || Pattern.matches(s168, bankCode)) {
            return "绍兴银行";
        } else if (Pattern.matches(s169, bankCode)) {
            return "成都商业银行";
        } else if (Pattern.matches(s170, bankCode) || Pattern.matches(s171, bankCode) || Pattern.matches(s172, bankCode)) {
            return "抚顺银行";
        } else if (Pattern.matches(s173, bankCode) || Pattern.matches(s174, bankCode) || Pattern.matches(s175, bankCode)) {
            return "郑州银行";
        } else if (Pattern.matches(s176, bankCode) || Pattern.matches(s177, bankCode)) {
            return "宁夏银行";
        } else if (Pattern.matches(s178, bankCode) || Pattern.matches(s179, bankCode)) {
            return "重庆银行";
        } else if (Pattern.matches(s180, bankCode) || Pattern.matches(s181, bankCode) || Pattern.matches(s182, bankCode)) {
            return "哈尔滨银行";
        } else if (Pattern.matches(s183, bankCode) || Pattern.matches(s184, bankCode)) {
            return "兰州银行";
        } else if (Pattern.matches(s185, bankCode) || Pattern.matches(s186, bankCode)) {
            return "青岛银行";
        } else if (Pattern.matches(s187, bankCode) || Pattern.matches(s188, bankCode)) {
            return "秦皇岛市商业银行";
        } else if (Pattern.matches(s189, bankCode) || Pattern.matches(s190, bankCode) || Pattern.matches(s191, bankCode)) {
            return "青海银行";
        } else if (Pattern.matches(s192, bankCode) || Pattern.matches(s193, bankCode) || Pattern.matches(s194, bankCode) || Pattern.matches(s195, bankCode) || Pattern.matches(s196, bankCode)) {
            return "台州银行";
        } else if (Pattern.matches(s197, bankCode) || Pattern.matches(s198, bankCode) || Pattern.matches(s199, bankCode) || Pattern.matches(s200, bankCode)) {
            return "长沙银行";
        } else if (Pattern.matches(s201, bankCode) || Pattern.matches(s202, bankCode) || Pattern.matches(s203, bankCode) || Pattern.matches(s204, bankCode)) {
            return "泉州银行";
        } else if (Pattern.matches(s205, bankCode) || Pattern.matches(s206, bankCode) || Pattern.matches(s207, bankCode)) {
            return "包商银行";
        } else if (Pattern.matches(s208, bankCode) || Pattern.matches(s209, bankCode) || Pattern.matches(s210, bankCode) || Pattern.matches(s211, bankCode)) {
            return "龙江银行";
        } else if (Pattern.matches(s212, bankCode) || Pattern.matches(s213, bankCode) || Pattern.matches(s214, bankCode)) {
            return "上海农商银行";
        } else if (Pattern.matches(s215, bankCode) || Pattern.matches(s216, bankCode)) {
            return "浙江泰隆商业银行";
        } else if (Pattern.matches(s217, bankCode) || Pattern.matches(s218, bankCode)) {
            return "内蒙古银行";
        } else if (Pattern.matches(s219, bankCode) || Pattern.matches(s220, bankCode)) {
            return "广西北部湾银行";
        } else if (Pattern.matches(s221, bankCode) || Pattern.matches(s222, bankCode) || Pattern.matches(s223, bankCode)) {
            return "桂林银行";
        } else if (Pattern.matches(s224, bankCode) || Pattern.matches(s225, bankCode) || Pattern.matches(s226, bankCode) || Pattern.matches(s227, bankCode) || Pattern.matches(s228, bankCode)) {
            return "龙江银行";
        } else if (Pattern.matches(s229, bankCode) || Pattern.matches(s230, bankCode)) {
            return "成都农村商业银行";
        } else if (Pattern.matches(s231, bankCode) || Pattern.matches(s232, bankCode)) {
            return "福建省农村信用社联合社";
        } else if (Pattern.matches(s233, bankCode) || Pattern.matches(s234, bankCode)) {
            return "天津农村商业银行";
        } else if (Pattern.matches(s235, bankCode) || Pattern.matches(s236, bankCode)) {
            return "江苏省农村信用社联合社";
        } else if (Pattern.matches(s237, bankCode)) {
            return "湖南省农村信用社联合社";
        } else if (Pattern.matches(s238, bankCode) || Pattern.matches(s239, bankCode)) {
            return "江西省农村信用社联合社";
        } else if (Pattern.matches(s240, bankCode) || Pattern.matches(s241, bankCode)) {
            return "商丘市商业银行";
        } else if (Pattern.matches(s242, bankCode) || Pattern.matches(s243, bankCode)) {
            return "华融湘江银行";
        } else if (Pattern.matches(s244, bankCode)) {
            return "衡水市商业银行";
        } else if (Pattern.matches(s245, bankCode)) {
            return "重庆南川石银村镇银行";
        } else if (Pattern.matches(s246, bankCode)) {
            return "湖南省农村信用社联合社";
        } else if (Pattern.matches(s247, bankCode)) {
            return "邢台银行";
        } else if (Pattern.matches(s248, bankCode)) {
            return "临汾市尧都区农村信用合作联社";
        } else if (Pattern.matches(s249, bankCode) || Pattern.matches(s250, bankCode)) {
            return "东营银行";
        } else if (Pattern.matches(s251, bankCode) || Pattern.matches(s252, bankCode)) {
            return "上饶银行";
        } else if (Pattern.matches(s253, bankCode) || Pattern.matches(s254, bankCode)) {
            return "德州银行";
        } else if (Pattern.matches(ss254, bankCode)) {
            return "承德银行";
        } else if (Pattern.matches(s255, bankCode)) {
            return "云南农村信用社";
        } else if (Pattern.matches(s257, bankCode) || Pattern.matches(s258, bankCode) || Pattern.matches(s256, bankCode)) {
            return "柳州银行";
        } else if (Pattern.matches(s259, bankCode) || Pattern.matches(s260, bankCode)) {
            return "威海市商业银行";
        } else if (Pattern.matches(s261, bankCode)) {
            return "湖州银行";
        } else if (Pattern.matches(s262, bankCode) || Pattern.matches(s263, bankCode)) {
            return "潍坊银行";
        } else if (Pattern.matches(s264, bankCode) || Pattern.matches(s265, bankCode)) {
            return "赣州银行";
        } else if (Pattern.matches(s266, bankCode)) {
            return "日照银行";
        } else if (Pattern.matches(s267, bankCode) || Pattern.matches(s268, bankCode) || Pattern.matches(s269, bankCode)) {
            return "南昌银行";
        } else if (Pattern.matches(s270, bankCode) || Pattern.matches(s271, bankCode) || Pattern.matches(s272, bankCode)) {
            return "贵阳银行";
        } else if (Pattern.matches(s273, bankCode) || Pattern.matches(s274, bankCode)) {
            return "锦州银行";
        } else if (Pattern.matches(s275, bankCode) || Pattern.matches(s276, bankCode)) {
            return "齐商银行";
        } else if (Pattern.matches(s277, bankCode) || Pattern.matches(s278, bankCode)) {
            return "珠海华润银行";
        } else if (Pattern.matches(s279, bankCode)) {
            return "葫芦岛市商业银行";
        } else if (Pattern.matches(s280, bankCode) || Pattern.matches(s281, bankCode)) {
            return "宜昌市商业银行";
        } else if (Pattern.matches(s282, bankCode) || Pattern.matches(s283, bankCode)) {
            return "杭州商业银行";
        } else if (Pattern.matches(s284, bankCode)) {
            return "苏州市商业银行";
        } else if (Pattern.matches(s285, bankCode)) {
            return "辽阳银行";
        } else if (Pattern.matches(s286, bankCode)) {
            return "洛阳银行";
        } else if (Pattern.matches(s287, bankCode) || Pattern.matches(s288, bankCode)) {
            return "焦作市商业银行";
        } else if (Pattern.matches(s289, bankCode)) {
            return "镇江市商业银行";
        } else if (Pattern.matches(s290, bankCode)) {
            return "法国兴业银行";
        } else if (Pattern.matches(s291, bankCode)) {
            return "大华银行";
        } else if (Pattern.matches(s292, bankCode)) {
            return "企业银行";
        } else if (Pattern.matches(s293, bankCode)) {
            return "华侨银行";
        } else if (Pattern.matches(s294, bankCode) || Pattern.matches(s295, bankCode) || Pattern.matches(s296, bankCode) || Pattern.matches(s297, bankCode)) {
            return "恒生银行";
        } else if (Pattern.matches(s298, bankCode)) {
            return "临沂商业银行";
        } else if (Pattern.matches(s299, bankCode)) {
            return "烟台商业银行";
        } else if (Pattern.matches(s300, bankCode) || Pattern.matches(s301, bankCode)) {
            return "齐鲁银行";
        } else if (Pattern.matches(s302, bankCode) || Pattern.matches(s303, bankCode)) {
            return "BC卡公司";
        } else if (Pattern.matches(s304, bankCode) || Pattern.matches(s305, bankCode) || Pattern.matches(s306, bankCode) || Pattern.matches(s307, bankCode)) {
            return "集友银行";
        } else if (Pattern.matches(s308, bankCode) || Pattern.matches(s309, bankCode) || Pattern.matches(s310, bankCode) || Pattern.matches(s311, bankCode)) {
            return "大丰银行";
        } else if (Pattern.matches(s312, bankCode) || Pattern.matches(s313, bankCode)) {
            return "AEON信贷财务亚洲有限公司";
        } else if (Pattern.matches(s314, bankCode)) {
            return "澳门BDA";
        } else {
            return "";
        }
    }

    /**
     * 获取银行卡类型
     */
    public String getCardType(String bankCode) {
        if (Pattern.matches(s1, bankCode) || Pattern.matches(s2, bankCode) || Pattern.matches(s4, bankCode) || Pattern.matches(s5, bankCode) || Pattern.matches(s6, bankCode) || Pattern.matches(s7, bankCode)
                || Pattern.matches(s14, bankCode) || Pattern.matches(s15, bankCode) || Pattern.matches(s16, bankCode)
                || Pattern.matches(s19, bankCode) || Pattern.matches(s20, bankCode) || Pattern.matches(s26, bankCode) || Pattern.matches(s27, bankCode) || Pattern.matches(s28, bankCode)
                || Pattern.matches(s34, bankCode) || Pattern.matches(s35, bankCode) || Pattern.matches(s36, bankCode) || Pattern.matches(s41, bankCode) || Pattern.matches(s42, bankCode) || Pattern.matches(s43, bankCode)
                || Pattern.matches(s47, bankCode) || Pattern.matches(s50, bankCode) || Pattern.matches(s51, bankCode) || Pattern.matches(s52, bankCode) || Pattern.matches(s53, bankCode)
                || Pattern.matches(s55, bankCode) || Pattern.matches(s56, bankCode) || Pattern.matches(s59, bankCode) || Pattern.matches(s61, bankCode) || Pattern.matches(s62, bankCode)
                || Pattern.matches(s65, bankCode) || Pattern.matches(s66, bankCode) || Pattern.matches(s67, bankCode) || Pattern.matches(s70, bankCode) || Pattern.matches(s71, bankCode)
                || Pattern.matches(s73, bankCode) || Pattern.matches(s74, bankCode) || Pattern.matches(s79, bankCode) || Pattern.matches(s80, bankCode)
                || Pattern.matches(s83, bankCode) || Pattern.matches(s84, bankCode) || Pattern.matches(s85, bankCode) || Pattern.matches(s87, bankCode) || Pattern.matches(s89, bankCode) || Pattern.matches(s90, bankCode)
                || Pattern.matches(s92, bankCode) || Pattern.matches(s93, bankCode) || Pattern.matches(s95, bankCode) || Pattern.matches(s97, bankCode) || Pattern.matches(s98, bankCode)
                || Pattern.matches(s100, bankCode) || Pattern.matches(s102, bankCode) || Pattern.matches(s104, bankCode) || Pattern.matches(s105, bankCode) || Pattern.matches(s106, bankCode)
                || Pattern.matches(s108, bankCode) || Pattern.matches(s110, bankCode) || Pattern.matches(s112, bankCode) || Pattern.matches(s115, bankCode) || Pattern.matches(s116, bankCode) || Pattern.matches(s117, bankCode)
                || Pattern.matches(s118, bankCode) || Pattern.matches(s120, bankCode) || Pattern.matches(s121, bankCode) || Pattern.matches(s123, bankCode) || Pattern.matches(s124, bankCode)
                || Pattern.matches(s125, bankCode) || Pattern.matches(s126, bankCode) || Pattern.matches(s128, bankCode) || Pattern.matches(s130, bankCode) || Pattern.matches(s131, bankCode) || Pattern.matches(s132, bankCode)
                || Pattern.matches(s133, bankCode) || Pattern.matches(s134, bankCode) | Pattern.matches(s135, bankCode) | Pattern.matches(s136, bankCode) || Pattern.matches(s138, bankCode) || Pattern.matches(s139, bankCode)
                || Pattern.matches(s140, bankCode) || Pattern.matches(s141, bankCode) || Pattern.matches(s144, bankCode) || Pattern.matches(s145, bankCode) || Pattern.matches(s146, bankCode)
                || Pattern.matches(s147, bankCode) || Pattern.matches(s148, bankCode) || Pattern.matches(s150, bankCode) || Pattern.matches(s151, bankCode) || Pattern.matches(s152, bankCode)
                || Pattern.matches(s153, bankCode) || Pattern.matches(s154, bankCode) || Pattern.matches(s155, bankCode) || Pattern.matches(s156, bankCode) || Pattern.matches(s157, bankCode) || Pattern.matches(s159, bankCode)
                || Pattern.matches(s160, bankCode) || Pattern.matches(s162, bankCode) || Pattern.matches(s164, bankCode) || Pattern.matches(s166, bankCode) || Pattern.matches(s167, bankCode)
                || Pattern.matches(s169, bankCode) || Pattern.matches(s170, bankCode) || Pattern.matches(s171, bankCode) || Pattern.matches(s172, bankCode) || Pattern.matches(s173, bankCode) || Pattern.matches(s174, bankCode) || Pattern.matches(s175, bankCode)
                || Pattern.matches(s176, bankCode) || Pattern.matches(s178, bankCode) || Pattern.matches(s179, bankCode) || Pattern.matches(s180, bankCode) || Pattern.matches(s181, bankCode) || Pattern.matches(s182, bankCode)
                || Pattern.matches(s183, bankCode) || Pattern.matches(s184, bankCode) || Pattern.matches(s185, bankCode) || Pattern.matches(s186, bankCode) || Pattern.matches(s187, bankCode) || Pattern.matches(s188, bankCode)
                || Pattern.matches(s189, bankCode) || Pattern.matches(s192, bankCode) || Pattern.matches(s193, bankCode) || Pattern.matches(s194, bankCode) || Pattern.matches(s197, bankCode) || Pattern.matches(s198, bankCode)
                || Pattern.matches(s201, bankCode) || Pattern.matches(s202, bankCode) || Pattern.matches(s203, bankCode) || Pattern.matches(s205, bankCode) || Pattern.matches(s206, bankCode) || Pattern.matches(s208, bankCode)
                || Pattern.matches(s209, bankCode) || Pattern.matches(s210, bankCode) || Pattern.matches(s212, bankCode) || Pattern.matches(s217, bankCode) || Pattern.matches(s219, bankCode)
                || Pattern.matches(s221, bankCode) || Pattern.matches(s222, bankCode) || Pattern.matches(s224, bankCode) || Pattern.matches(s225, bankCode) || Pattern.matches(s226, bankCode) || Pattern.matches(s229, bankCode)
                || Pattern.matches(s231, bankCode) || Pattern.matches(s233, bankCode) || Pattern.matches(s235, bankCode) || Pattern.matches(s238, bankCode) || Pattern.matches(s240, bankCode) || Pattern.matches(s242, bankCode)
                || Pattern.matches(s244, bankCode) || Pattern.matches(s245, bankCode) || Pattern.matches(s246, bankCode) || Pattern.matches(s247, bankCode) || Pattern.matches(s249, bankCode) || Pattern.matches(s251, bankCode)
                || Pattern.matches(s253, bankCode) || Pattern.matches(s256, bankCode) || Pattern.matches(s257, bankCode) || Pattern.matches(s259, bankCode) || Pattern.matches(s262, bankCode) || Pattern.matches(s264, bankCode)
                || Pattern.matches(s267, bankCode) || Pattern.matches(s268, bankCode) || Pattern.matches(s270, bankCode) || Pattern.matches(s271, bankCode) || Pattern.matches(s273, bankCode) || Pattern.matches(s275, bankCode) || Pattern.matches(s277, bankCode)
                || Pattern.matches(s279, bankCode) || Pattern.matches(s280, bankCode) || Pattern.matches(s282, bankCode) || Pattern.matches(s284, bankCode) || Pattern.matches(s285, bankCode) || Pattern.matches(s286, bankCode)
                || Pattern.matches(s287, bankCode) || Pattern.matches(s288, bankCode) || Pattern.matches(s289, bankCode) || Pattern.matches(s290, bankCode) || Pattern.matches(s291, bankCode) || Pattern.matches(s292, bankCode)
                || Pattern.matches(s293, bankCode) || Pattern.matches(s294, bankCode) || Pattern.matches(s295, bankCode) || Pattern.matches(s296, bankCode) || Pattern.matches(s298, bankCode) || Pattern.matches(s299, bankCode) || Pattern.matches(s300, bankCode)
                || Pattern.matches(s302, bankCode) || Pattern.matches(s304, bankCode) || Pattern.matches(s305, bankCode) || Pattern.matches(s308, bankCode) || Pattern.matches(s309, bankCode)
                || Pattern.matches(s312, bankCode) || Pattern.matches(s314, bankCode)) {
            return "储蓄卡";
        } else if (Pattern.matches(s3, bankCode) || Pattern.matches(s8, bankCode) || Pattern.matches(s9, bankCode) || Pattern.matches(s10, bankCode) || Pattern.matches(s17, bankCode) || Pattern.matches(s21, bankCode)
                || Pattern.matches(s29, bankCode) || Pattern.matches(s30, bankCode) || Pattern.matches(s31, bankCode) || Pattern.matches(s37, bankCode) || Pattern.matches(s38, bankCode)
                || Pattern.matches(s44, bankCode) || Pattern.matches(s45, bankCode) || Pattern.matches(s48, bankCode) || Pattern.matches(s49, bankCode) || Pattern.matches(s54, bankCode)
                || Pattern.matches(s57, bankCode) || Pattern.matches(s58, bankCode) || Pattern.matches(s60, bankCode) || Pattern.matches(s63, bankCode) || Pattern.matches(s68, bankCode) || Pattern.matches(s72, bankCode)
                || Pattern.matches(s75, bankCode) || Pattern.matches(s81, bankCode) || Pattern.matches(s82, bankCode) || Pattern.matches(s86, bankCode) || Pattern.matches(s88, bankCode)
                || Pattern.matches(s91, bankCode) || Pattern.matches(s94, bankCode) || Pattern.matches(s96, bankCode) || Pattern.matches(s99, bankCode) || Pattern.matches(s101, bankCode) || Pattern.matches(s103, bankCode)
                || Pattern.matches(s107, bankCode) || Pattern.matches(s109, bankCode) || Pattern.matches(s111, bankCode) || Pattern.matches(s113, bankCode) || Pattern.matches(s114, bankCode) || Pattern.matches(s119, bankCode)
                || Pattern.matches(s122, bankCode) || Pattern.matches(s127, bankCode) || Pattern.matches(s129, bankCode) | Pattern.matches(s137, bankCode) || Pattern.matches(s142, bankCode)
                || Pattern.matches(s158, bankCode) || Pattern.matches(s161, bankCode) || Pattern.matches(s163, bankCode) || Pattern.matches(s165, bankCode) || Pattern.matches(s167, bankCode)
                || Pattern.matches(s177, bankCode) || Pattern.matches(s191, bankCode) || Pattern.matches(s190, bankCode) || Pattern.matches(s195, bankCode) || Pattern.matches(s199, bankCode)
                || Pattern.matches(s204, bankCode) || Pattern.matches(s207, bankCode) || Pattern.matches(s211, bankCode) || Pattern.matches(s214, bankCode) || Pattern.matches(s216, bankCode)
                || Pattern.matches(s218, bankCode) || Pattern.matches(s220, bankCode) || Pattern.matches(s223, bankCode) || Pattern.matches(s228, bankCode) || Pattern.matches(s230, bankCode) || Pattern.matches(s232, bankCode)
                || Pattern.matches(s234, bankCode) || Pattern.matches(s236, bankCode) || Pattern.matches(s237, bankCode) || Pattern.matches(s239, bankCode) || Pattern.matches(s241, bankCode) || Pattern.matches(s243, bankCode)
                || Pattern.matches(s248, bankCode) || Pattern.matches(s250, bankCode) || Pattern.matches(s252, bankCode) || Pattern.matches(s254, bankCode) || Pattern.matches(ss254, bankCode) || Pattern.matches(s255, bankCode)
                || Pattern.matches(s258, bankCode) || Pattern.matches(s260, bankCode) || Pattern.matches(s261, bankCode) || Pattern.matches(s263, bankCode) || Pattern.matches(s265, bankCode) || Pattern.matches(s266, bankCode)
                || Pattern.matches(s269, bankCode) || Pattern.matches(s272, bankCode) || Pattern.matches(s274, bankCode) || Pattern.matches(s276, bankCode) || Pattern.matches(s278, bankCode) || Pattern.matches(s281, bankCode)
                || Pattern.matches(s283, bankCode) || Pattern.matches(s297, bankCode) || Pattern.matches(s301, bankCode) || Pattern.matches(s303, bankCode) || Pattern.matches(s306, bankCode)
                || Pattern.matches(s313, bankCode)) {
            return "信用卡(贷记卡)";
        } else if (Pattern.matches(s11, bankCode) || Pattern.matches(s18, bankCode) || Pattern.matches(s22, bankCode) || Pattern.matches(s23, bankCode) || Pattern.matches(s32, bankCode) || Pattern.matches(s33, bankCode)
                || Pattern.matches(s39, bankCode) || Pattern.matches(s76, bankCode) || Pattern.matches(s196, bankCode) || Pattern.matches(s213, bankCode) || Pattern.matches(s215, bankCode)
                || Pattern.matches(s227, bankCode)) {
            return "准贷记卡";
        } else if (Pattern.matches(s12, bankCode) || Pattern.matches(s13, bankCode) || Pattern.matches(s24, bankCode) || Pattern.matches(s25, bankCode) || Pattern.matches(s40, bankCode)
                || Pattern.matches(s46, bankCode) || Pattern.matches(s64, bankCode) || Pattern.matches(s69, bankCode) || Pattern.matches(s77, bankCode) || Pattern.matches(s143, bankCode)
                || Pattern.matches(s149, bankCode) || Pattern.matches(s200, bankCode) || Pattern.matches(s307, bankCode) || Pattern.matches(s310, bankCode) || Pattern.matches(s311, bankCode)) {
            return "预付费卡";
        } else {
            return "未知";
        }
    }

    // "DC":"储蓄卡","CC":"信用卡","SCC":"准贷记卡","PC":"预付费卡"
    String s1 = "^(621096|621098|622150|622151|622181|622188|622199|955100|621095|620062|621285|621798|621799|621797|620529|621622|621599|621674|623218|623219)\\d{13}$";
    String s2 = "^(62215049|62215050|62215051|62218850|62218851|62218849)\\d{11}$";
    String s3 = "^(622812|622810|622811|628310|625919)\\d{10}$";

    String s4 = "^(620200|620302|620402|620403|620404|620406|620407|620409|620410|620411|620412|620502|620503|620405|620408|620512|620602|620604|620607|620611|620612|620704|620706|620707|620708|620709|620710|620609|620712|620713|620714|620802|620711|620904|620905|621001|620902|621103|621105|621106|621107|621102|621203|621204|621205|621206|621207|621208|621209|621210|621302|621303|621202|621305|621306|621307|621309|621311|621313|621211|621315|621304|621402|621404|621405|621406|621407|621408|621409|621410|621502|621317|621511|621602|621603|621604|621605|621608|621609|621610|621611|621612|621613|621614|621615|621616|621617|621607|621606|621804|621807|621813|621814|621817|621901|621904|621905|621906|621907|621908|621909|621910|621911|621912|621913|621915|622002|621903|622004|622005|622006|622007|622008|622010|622011|622012|621914|622015|622016|622003|622018|622019|622020|622102|622103|622104|622105|622013|622111|622114|622017|622110|622303|622304|622305|622306|622307|622308|622309|622314|622315|622317|622302|622402|622403|622404|622313|622504|622505|622509|622513|622517|622502|622604|622605|622606|622510|622703|622715|622806|622902|622903|622706|623002|623006|623008|623011|623012|622904|623015|623100|623202|623301|623400|623500|623602|623803|623901|623014|624100|624200|624301|624402|623700|624000)\\d{12}$";
    String s5 = "^(622200|622202|622203|622208|621225|620058|621281|900000|621558|621559|621722|621723|620086|621226|621618|620516|621227|621288|621721|900010|623062|621670|621720|621379|621240|621724|621762|621414|621375|622926|622927|622928|622929|622930|622931|621733|621732|621372|621369|621763)\\d{13}$";
    String s6 = "^(402791|427028|427038|548259|621376|621423|621428|621434|621761|621749|621300|621378|622944|622949|621371|621730|621734|621433|621370|621764|621464|621765|621750|621377|621367|621374|621731|621781)\\d{10}$";
    String s7 = "^(9558)\\d{15}$";
    String s8 = "^(370246|370248|370249|370247|370267|374738|374739)\\d{9}$";
    String s9 = "^(427010|427018|427019|427020|427029|427030|427039|438125|438126|451804|451810|451811|458071|489734|489735|489736|510529|427062|524091|427064|530970|530990|558360|524047|525498|622230|622231|622232|622233|622234|622235|622237|622239|622240|622245|622238|451804|451810|451811|458071|628288|628286|622206|526836|513685|543098|458441|622246|544210|548943|356879|356880|356881|356882|528856|625330|625331|625332|622236|524374|550213|625929|625927|625939|625987|625930|625114|622159|625021|625022|625932|622889|625900|625915|625916|622171|625931|625113|625928|625914|625986|625925|625921|625926|625942|622158|625917|625922|625934|625933|625920|625924|625017|625018|625019)\\d{10}$";
    String s10 = "^(45806|53098|45806|53098)\\d{11}$";
    String s11 = "^(622210|622211|622212|622213|622214|622220|622223|622225|622229|622215|622224)\\d{10}$";
    String s12 = "^(620054|620142|620184|620030|620050|620143|620149|620124|620183|620094|620186|620148|620185)\\d{10}$";
    String s13 = "^(620114|620187|620046)\\d{13}$";

    String s14 = "^(622841|622824|622826|622848|620059|621282|622828|622823|621336|621619|622821|622822|622825|622827|622845|622849|623018|623206|621671|622840|622843|622844|622846|622847|620501)\\d{13}$";
    String s15 = "^(95595|95596|95597|95598|95599)\\d{14}$";
    String s16 = "^(103)\\d{16}$";
    String s17 = "^(403361|404117|404118|404119|404120|404121|463758|519412|519413|520082|520083|552599|558730|514027|622836|622837|628268|625996|625998|625997|622838|625336|625826|625827|544243|548478|628269)\\d{10}$";
    String s18 = "^(622820|622830)\\d{10}$";

    String s19 = "^(621660|621661|621662|621663|621665|621667|621668|621669|621666|456351|601382|621256|621212|621283|620061|621725|621330|621331|621332|621333|621297|621568|621569|621672|623208|621620|621756|621757|621758|621759|621785|621786|621787|621788|621789|621790|622273|622274|622771|622772|622770|621741|621041)\\d{13}$";
    String s20 = "^(621293|621294|621342|621343|621364|621394|621648|621248|621215|621249|621231|621638|621334|621395|623040|622348)\\d{10}$";
    String s21 = "^(625908|625910|625909|356833|356835|409665|409666|409668|409669|409670|409671|409672|512315|512316|512411|512412|514957|409667|438088|552742|553131|514958|622760|628388|518377|622788|628313|628312|622750|622751|625145|622479|622480|622789|625140|622346|622347)\\d{10}$";
    String s22 = "^(518378|518379|518474|518475|518476|524865|525745|525746|547766|558868|622752|622753|622755|524864|622757|622758|622759|622761|622762|622763|622756|622754|622764|622765|558869|625905|625906|625907|625333)\\d{10}$";
    String s23 = "^(53591|49102|377677)\\d{11}$";
    String s24 = "^(620514|620025|620026|620210|620211|620019|620035|620202|620203|620048|620515|920000)\\d{10}$";
    String s25 = "^(620040|620531|620513|921000|620038)\\d{13}$";

    String s26 = "^(621284|436742|589970|620060|621081|621467|621598|621621|621700|622280|622700|623211|623668)\\d{13}$";
    String s27 = "^(421349|434061|434062|524094|526410|552245|621080|621082|621466|621488|621499|622966|622988|622382|621487|621083|621084|620107)\\d{10}$";
    String s28 = "^(436742193|622280193)\\d{10}$";
    String s29 = "^(553242)\\d{12}$";
    String s30 = "^(625362|625363|628316|628317|356896|356899|356895|436718|436738|436745|436748|489592|531693|532450|532458|544887|552801|557080|558895|559051|622166|622168|622708|625964|625965|625966|628266|628366|622381|622675|622676|622677)\\d{10}$";
    String s31 = "^(5453242|5491031|5544033)\\d{11}$";
    String s32 = "^(622725|622728|436728|453242|491031|544033|622707|625955|625956)\\d{10}$";
    String s33 = "^(53242|53243)\\d{11}$";

    String s34 = "^(622261|622260|622262|621002|621069|621436|621335)\\d{13}$";
    String s35 = "^(620013)\\d{10}$";
    String s36 = "^(405512|601428|405512|601428|622258|622259|405512|601428)\\d{11}$";
    String s37 = "^(49104|53783)\\d{11}$";
    String s38 = "^(434910|458123|458124|520169|522964|552853|622250|622251|521899|622253|622656|628216|622252|955590|955591|955592|955593|628218|625028|625029)\\d{10}$";
    String s39 = "^(622254|622255|622256|622257|622284)\\d{10}$";
    String s40 = "^(620021|620521)\\d{13}$";

    String s41 = "^(402658|410062|468203|512425|524011|622580|622588|622598|622609|95555|621286|621483|621485|621486|621299)(\\d{10}|\\d{11})$";
    String s42 = "^(690755)\\d{9}$";
    String s43 = "^(690755)\\d{12}$";
    String s44 = "^(356885|356886|356887|356888|356890|439188|439227|479228|479229|521302|356889|545620|545621|545947|545948|552534|552587|622575|622576|622577|622578|622579|545619|622581|622582|545623|628290|439225|518710|518718|628362|439226|628262|625802|625803)\\d{10}$";
    String s45 = "^(370285|370286|370287|370289)\\d{9}$";
    String s46 = "^(620520)\\d{13}$";
    //民生银行
    String s47 = "^(622615|622616|622618|622622|622617|622619|415599|421393|421865|427570|427571|472067|472068|622620)\\d{10}$";
    String s48 = "^(545392|545393|545431|545447|356859|356857|407405|421869|421870|421871|512466|356856|528948|552288|622600|622601|622602|517636|622621|628258|556610|622603|464580|464581|523952|545217|553161|356858|622623|625912|625913|625911)\\d{10}$";
    String s49 = "^(377155|377152|377153|377158)\\d{9}$";

    String s50 = "^(303)\\d{13}$";
    String s51 = "^(90030)\\d{11}$";
    String s52 = "^(620535)\\d{13}$";
    String s53 = "^(620085|622660|622662|622663|622664|622665|622666|622667|622669|622670|622671|622672|622668|622661|622674|622673|620518|621489|621492)\\d{10}$";
    String s54 = "^(356837|356838|486497|622657|622685|622659|622687|625978|625980|625981|625979|356839|356840|406252|406254|425862|481699|524090|543159|622161|622570|622650|622655|622658|625975|625977|628201|628202|625339|625976)\\d{10}$";

    String s55 = "^(433670|433680|442729|442730|620082|622690|622691|622692|622696|622698|622998|622999|433671|968807|968808|968809|621771|621767|621768|621770|621772|621773|622453|622456)\\d{10}$";
    String s56 = "^(622459)\\d{11}$";
    String s57 = "^(376968|376969|376966)\\d{9}$";
    String s58 = "^(400360|403391|403392|404158|404159|404171|404172|404173|404174|404157|433667|433668|433669|514906|403393|520108|433666|558916|622678|622679|622680|622688|622689|628206|556617|628209|518212|628208|356390|356391|356392|622916|622918|622919)\\d{10}$";

    String s59 = "^(622630|622631|622632|622633|999999|621222|623020|623021|623022|623023)\\d{10}$";
    String s60 = "^(523959|528709|539867|539868|622637|622638|628318|528708|622636|625967|625968|625969)\\d{10}$";

    String s61 = "^(621626|623058)\\d{13}$";
    String s62 = "^(602907|622986|622989|622298|627069|627068|627066|627067|412963|415752|415753|622535|622536|622538|622539|998800|412962|622983)\\d{10}$";
    String s63 = "^(531659|622157|528020|622155|622156|526855|356869|356868|625360|625361|628296|435744|435745|483536|622525|622526|998801|998802)\\d{10}$";
    String s64 = "^(620010)\\d{10}$";
    //兴业银行
    String s65 = "^(438589)\\d{12}$";
    String s66 = "^(90592)\\d{11}$";
    String s67 = "^(966666|622909|438588|622908)\\d{12}$";
    String s68 = "^(461982|486493|486494|486861|523036|451289|527414|528057|622901|622902|622922|628212|451290|524070|625084|625085|625086|625087|548738|549633|552398|625082|625083|625960|625961|625962|625963)\\d{10}$";
    String s69 = "^(620010)\\d{10}$";

    String s70 = "^(621050|622172|622985|622987|620522|622267|622278|622279|622468|622892|940021)\\d{12}$";
    String s71 = "^(438600)\\d{10}$";
    String s72 = "^(356827|356828|356830|402673|402674|486466|519498|520131|524031|548838|622148|622149|622268|356829|622300|628230|622269|625099|625953)\\d{10}$";

    String s73 = "^(622516|622517|622518|622521|622522|622523|984301|984303|621352|621793|621795|621796|621351|621390|621792|621791)\\d{10}$";
    String s74 = "^(84301|84336|84373|84385|84390|87000|87010|87030|87040|84380|84361|87050|84342)\\d{11}$";
    String s75 = "^(356851|356852|404738|404739|456418|498451|515672|356850|517650|525998|622177|622277|628222|622500|628221|622176|622276|622228|625957|625958|625993|625831)\\d{10}$";
    String s76 = "^(622520|622519)\\d{10}$";
    String s77 = "^(620530)\\d{13}$";

    //    String s78 = "^(622516|622517|622518|622521|622522|622523|984301|984303|621352|621793|621795|621796|621351|621390|621792|621791)\\d{10}$";
    String s79 = "^(622568|6858001|6858009|621462)\\d{13}$";
    String s80 = "^(9111)\\d{15}$";
    String s81 = "^(406365|406366|428911|436768|436769|436770|487013|491032|491033|491034|491035|491036|491037|491038|436771|518364|520152|520382|541709|541710|548844|552794|493427|622555|622556|622557|622558|622559|622560|528931|558894|625072|625071|628260|628259|625805|625806|625807|625808|625809|625810)\\d{10}$";
    String s82 = "^(685800|6858000)\\d{13}$";

    String s83 = "^(621268|622684|622884|621453)\\d{10}$";
    String s84 = "^(603445|622467|940016|621463)\\d{13}$";

    String s85 = "^(622449|940051)\\d{10}$";
    String s86 = "^(622450|628204)\\d{10}$";
    //温州银行
    String s87 = "^(621977)\\d{10}$";
    String s88 = "^(622868|622899|628255)\\d{10}$";

    String s89 = "^(622877|622879|621775|623203)\\d{13}$";
    String s90 = "^(603601|622137|622327|622340|622366)\\d{11}$";
    String s91 = "^(628251|622651|625828)\\d{10}$";

    String s92 = "^(621076|622173|622131|621579|622876)\\d{13}$";
    String s93 = "^(504923|622422|622447|940076)\\d{10}$";
    String s94 = "^(628210|622283|625902)\\d{10}$";
    //南京银行
    String s95 = "^(621777|622305|621259)\\d{10}$";
    String s96 = "^(622303|628242|622595|622596)\\d{10}$";

    String s97 = "^(621279|622281|622316|940022)\\d{10}$";
    String s98 = "^(621418)\\d{13}$";
    String s99 = "^(625903|622778|628207|512431|520194|622282|622318)\\d{10}$";
    String s100 = "^(625903|622778|628207|512431|520194|622282|622318)\\d{10}$";
    //北京银行
    String s101 = "^(522001|622163|622853|628203|622851|622852)\\d{10}$";

    String s102 = "^(620088|621068|622138|621066|621560)\\d{13}$";
    String s103 = "^(625526|625186|628336)\\d{10}$";

    String s104 = "^(622946)\\d{10}$";
    String s105 = "^(622406|621442)\\d{11}$";
    String s106 = "^(622407|621443)\\d{13}$";
    String s107 = "^(622360|622361|625034|625096|625098)\\d{10}$";
    //渣打银行
    String s108 = "^(622948|621740|622942|622994)\\d{10}$";
    String s109 = "^(622482|622483|622484)\\d{10}$";

    String s110 = "^(621062|621063)\\d{10}$";
    String s111 = "^(625076|625077|625074|625075|622371|625091)\\d{10}$";
    //东亚银行
    String s112 = "^(622933|622938|623031|622943|621411)\\d{13}$";
    String s113 = "^(622372|622471|622472|622265|622266|625972|625973)\\d{10}$";
    String s114 = "^(622365)\\d{11}$";

    String s115 = "^(621469|621625)\\d{13}$";
    String s116 = "^(622128|622129|623035)\\d{10}$";
    String s117 = "^(909810|940035|621522|622439)\\d{12}$";

    String s118 = "^(622328|940062|623038)\\d{13}$";
    String s119 = "^(625288|625888)\\d{10}$";

    String s120 = "^(622333|940050)\\d{10}$";
    String s121 = "^(621439|623010)\\d{13}$";
    String s122 = "^(622888)\\d{10}$";

    String s123 = "^(622302)\\d{10}$";
    String s124 = "^(622477|622509|622510|622362|621018|621518)\\d{13}$";

    String s125 = "^(622297|621277)\\d{10}$";
    String s126 = "^(622375|622489)\\d{11}$";
    String s127 = "^(622293|622295|622296|622373|622451|622294|625940)\\d{10}$";

    String s128 = "^(622871|622958|622963|622957|622861|622932|622862|621298)\\d{10}$";
    String s129 = "^(622798|625010|622775|622785)\\d{10}$";

    String s130 = "^(621016|621015)\\d{13}$";
    String s131 = "^(622487|622490|622491|622492)\\d{10}$";
    String s132 = "^(622487|622490|622491|622492|621744|621745|621746|621747)\\d{11}$";

    String s133 = "^(623078)\\d{13}$";
    String s134 = "^(622384|940034)\\d{11}$";

    String s135 = "^(940015|622331)\\d{12}$";
    String s136 = "^(6091201)\\d{11}$";
    String s137 = "^(622426|628205)\\d{10}$";

    String s138 = "^(621019|622309|621019)\\d{13}$";
    String s139 = "^(6223091100|6223092900|6223093310|6223093320|6223093330|6223093370|6223093380|6223096510|6223097910)\\d{9}$";

    String s140 = "^(621213|621289|621290|621291|621292|621042|621743)\\d{13}$";
    String s141 = "^(623041|622351)\\d{10}$";
    String s142 = "^(625046|625044|625058|622349|622350)\\d{10}$";
    String s143 = "^(620208|620209|625093|625095)\\d{10}$";
    //厦门银行
    String s144 = "^(622393|940023)\\d{10}$";
    String s145 = "^(6886592)\\d{11}$";
    String s146 = "^(623019|621600|)\\d{13}$";

    String s147 = "^(622388)\\d{10}$";
    String s148 = "^(621267|623063)\\d{12}$";
    String s149 = "^(620043|)\\d{12}$";

    String s150 = "^(622865|623131)\\d{13}$";
    String s151 = "^(940012)\\d{10}$";
    String s152 = "^(622178|622179|628358)\\d{10}$";
    //汉口银行
    String s153 = "^(990027)\\d{12}$";
    String s154 = "^(622325|623105|623029)\\d{10}$";

    String s155 = "^(566666)\\d{12}$";
    String s156 = "^(622455|940039)\\d{13}$";
    String s157 = "^(623108|623081)\\d{10}$";
    String s158 = "^(622466|628285)\\d{10}$";

    String s159 = "^(603708)\\d{11}$";
    String s160 = "^(622993|623069|623070|623172|623173)\\d{13}$";
    String s161 = "^(622383|622385|628299)\\d{10}$";

    String s162 = "^(622498|622499|623000|940046)\\d{13}$";
    String s163 = "^(622921|628321)\\d{10}$";
    //乌鲁木齐商业银行
    String s164 = "^(621751|622143|940001|621754)\\d{13}$";
    String s165 = "^(622476|628278)\\d{10}$";

    String s166 = "^(622486)\\d{10}$";
    String s167 = "^(603602|623026|623086)\\d{12}$";
    String s168 = "^(628291)\\d{10}$";

    String s169 = "^(622152|622154|622996|622997|940027|622153|622135|621482|621532)\\d{13}$";
    String s170 = "^(622442)\\d{11}$";
    String s171 = "^(940053)\\d{12}$";
    String s172 = "^(622442|623099)\\d{13}$";

    String s173 = "^(622421)\\d{13}$";
    String s174 = "^(940056)\\d{11}$";
    String s175 = "^(96828)\\d{11}$";
    //宁夏银行
    String s176 = "^(621529|622429|621417|623089|623200)\\d{13}$";
    String s177 = "^(628214|625529|622428)\\d{10}$";

    String s178 = "^(9896)\\d{12}$";
    String s179 = "^(622134|940018|623016)\\d{10}$";

    String s180 = "^(621577|622425)\\d{13}$";
    String s181 = "^(940049)\\d{12}$";
    String s182 = "^(622425)\\d{11}$";

    String s183 = "^(622139|940040|628263)\\d{10}$";
    String s184 = "^(621242|621538|621496)\\d{13}$";

    String s185 = "^(621252|622146|940061|628239)\\d{10}$";
    String s186 = "^(621419|623170)\\d{13}$";

    String s187 = "^(62249802|94004602)\\d{11}$";
    String s188 = "^(621237|623003)\\d{13}$";
    //青海银行
    String s189 = "^(622310|940068)\\d{11}$";
    String s190 = "^(622817|628287|625959)\\d{10}$";
    String s191 = "^(62536601)\\d{8}$";

    String s192 = "^(622427)\\d{10}$";
    String s193 = "^(940069)\\d{11}$";
    String s194 = "^(623039)\\d{13}$";
    String s195 = "^(622321|628273)\\d{10}$";
    String s196 = "^(625001)\\d{10}$";

    String s197 = "^(694301)\\d{12}$";
    String s198 = "^(940071|622368|621446)\\d{13}$";
    String s199 = "^(625901|622898|622900|628281|628282|622806|628283)\\d{10}$";
    String s200 = "^(620519)\\d{13}$";

    String s201 = "^(683970|940074)\\d{12}$";
    String s202 = "^(622370)\\d{13}$";
    String s203 = "^(621437)\\d{13}$";
    String s204 = "^(628319)\\d{10}$";

    String s205 = "^(622336|621760)\\d{11}$";
    String s206 = "^(622165)\\d{10}$";
    String s207 = "^(622315|625950|628295)\\d{10}$";

    String s208 = "^(621037|621097|621588|622977)\\d{13}$";
    String s209 = "^(62321601)\\d{11}$";
    String s210 = "^(622860)\\d{10}$";
    String s211 = "^(622644|628333)\\d{10}$";

    String s212 = "^(622478|940013|621495)\\d{10}$";
    String s213 = "^(625500)\\d{10}$";
    String s214 = "^(622611|622722|628211|625989)\\d{10}$";

    String s215 = "^(622717)\\d{10}$";
    String s216 = "^(628275|622565|622287)\\d{10}$";
    //内蒙古银行
    String s217 = "^(622147|621633)\\d{13}$";
    String s218 = "^(628252)\\d{10}$";

    String s219 = "^(623001)\\d{10}$";
    String s220 = "^(628227)\\d{10}$";

    String s221 = "^(621456)\\d{11}$";
    String s222 = "^(621562)\\d{13}$";
    String s223 = "^(628219)\\d{10}$";

    String s224 = "^(621037|621097|621588|622977)\\d{13}$";
    String s225 = "^(62321601)\\d{11}$";
    String s226 = "^(622475|622860)\\d{10}$";
    String s227 = "^(625588)\\d{10}$";
    String s228 = "^(622270|628368|625090|622644|628333)\\d{10}$";

    String s229 = "^(623088)\\d{13}$";
    String s230 = "^(622829|628301|622808|628308)\\d{10}$";

    String s231 = "^(622127|622184|621701|621251|621589|623036)\\d{13}$";
    String s232 = "^(628232|622802|622290)\\d{10}$";

    String s233 = "^(622531|622329)\\d{13}$";
    String s234 = "^(622829|628301)\\d{10}$";

    String s235 = "^(621578|623066|622452|622324)\\d{13}$";
    String s236 = "^(622815|622816|628226)\\d{10}$";
    String s237 = "^(622906|628386|625519|625506)\\d{10}$";

    String s238 = "^(621592)\\d{10}$";
    String s239 = "^(628392)\\d{10}$";
    //商丘市商业银行
    String s240 = "^(621748)\\d{13}$";
    String s241 = "^(628271)\\d{10}$";

    String s242 = "^(621366|621388)\\d{13}$";
    String s243 = "^(628328)\\d{10}$";

    String s244 = "^(621239|623068)\\d{13}$";
    String s245 = "^(621653004)\\d{10}$";
    String s246 = "^(622169|621519|621539|623090)\\d{13}$";
    String s247 = "^(621238|620528)\\d{13}$";
    String s248 = "^(628382|625158)\\d{10}$";

    String s249 = "^(621004)\\d{12}$";
    String s250 = "^(628217)\\d{10}$";

    String s251 = "^(621416)\\d{10}$";
    String s252 = "^(628217)\\d{10}$";
    //德州银行
    String s253 = "^(622937)\\d{13}$";
    String s254 = "^(628397)\\d{10}$";
    //德州银行
    String ss254 = "^(628397)\\d{10}$";
    //云南农村信用社
    String s255 = "^(622469|628307)\\d{10}$";
    //柳州银行
    String s256 = "^(622292|622291|621412)\\d{12}$";
    String s257 = "^(622880|622881)\\d{10}$";
    String s258 = "^(62829)\\d{10}$";

    String s259 = "^(623102)\\d{10}$";
    String s260 = "^(628234)\\d{10}$";

    String s261 = "^(628306)\\d{10}$";

    String s262 = "^(622391|940072)\\d{10}$";
    String s263 = "^(628391)\\d{10}$";

    String s264 = "^(622967|940073)\\d{13}$";
    String s265 = "^(628233)\\d{10}$";
    String s266 = "^(628257)\\d{10}$";

    String s267 = "^(621269|622275)\\d{10}$";
    String s268 = "^(940006)\\d{11}$";
    String s269 = "^(628305)\\d{11}$";
    //贵阳银行
    String s270 = "^(622133|621735)\\d{13}$";
    String s271 = "^(888)\\d{13}$";
    String s272 = "^(628213)\\d{10}$";

    String s273 = "^(622990|940003)\\d{11}$";
    String s274 = "^(628261)\\d{10}$";

    String s275 = "^(622311|940057)\\d{11}$";
    String s276 = "^(628311)\\d{10}$";

    String s277 = "^(622363|940048)\\d{13}$";
    String s278 = "^(628270)\\d{10}$";
    //    葫芦岛市商业银行
    String s279 = "^(622398|940054)\\d{10}$";

    String s280 = "^(940055)\\d{11}$";
    String s281 = "^(622397)\\d{11}$";

    String s282 = "^(603367|622878)\\d{12}$";
    String s283 = "^(622397)\\d{11}$";

    String s284 = "^(603506)\\d{13}$";

    String s285 = "^(622399|940043)\\d{11}$";

    String s286 = "^(622420|940041)\\d{11}$";

    String s287 = "^(622338)\\d{13}$";
    String s288 = "^(940032)\\d{10}$";

    String s289 = "^(622394|940025)\\d{10}$";

    String s290 = "^(621245)\\d{10}$";

    String s291 = "^(621328)\\d{13}$";

    String s292 = "^(621651)\\d{13}$";

    String s293 = "^(621077)\\d{10}$";

    String s294 = "^(622409|621441)\\d{13}$";
    String s295 = "^(622410|621440)\\d{11}$";
    String s296 = "^(622950|622951)\\d{10}$";
    String s297 = "^(625026|625024|622376|622378|622377|625092)\\d{10}$";

    String s298 = "^(622359|940066)\\d{13}$";

    String s299 = "^(622886)\\d{10}$";

    String s300 = "^(940008|622379)\\d{13}";
    String s301 = "^(628379)\\d{10}$";

    String s302 = "^(620011|620027|620031|620039|620103|620106|620120|620123|620125|620220|620278|620812|621006|621011|621012|621020|621023|621025|621027|621031|620132|621039|621078|621220|621003)\\d{10}$";
    String s303 = "^(625003|625011|625012|625020|625023|625025|625027|625031|621032|625039|625078|625079|625103|625106|625006|625112|625120|625123|625125|625127|625131|625032|625139|625178|625179|625220|625320|625111|625132|625244)\\d{10}$";

    String s304 = "^(622355|623042)\\d{10}$";
    String s305 = "^(621043|621742)\\d{13}$";
    String s306 = "^(622352|622353|625048|625053|625060)\\d{10}$";
    String s307 = "^(620206|620207)\\d{10}$";

    String s308 = "^(622547|622548|622546)\\d{13}$";
    String s309 = "^(625198|625196|625147)\\d{10}$";
    String s310 = "^(620072)\\d{13}$";
    String s311 = "^(620204|620205)\\d{10}$";

    String s312 = "^(621064|622941|622974)\\d{10}$";
    String s313 = "^(622493)\\d{10}$";

    String s314 = "^(621274|621324)\\d{13}$";
}
