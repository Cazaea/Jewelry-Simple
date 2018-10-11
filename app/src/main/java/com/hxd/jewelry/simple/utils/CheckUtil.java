package com.hxd.jewelry.simple.utils;

import android.net.ParseException;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Cazaea
 * @time 2017/5/8 17:26
 * @mail wistorm@sina.com
 */

public class CheckUtil {

    private static final String TAG = CheckUtil.class.getSimpleName();

    /**
     * 检验VIN格式
     * <p>
     * 计算方法：
     * VIN码从从第一位开始，码数字的对应值×该位的加权值，计算全部17位的乘积值相加除以11，所得的余数，即为第九位校验值
     * <p>
     * VIN码各位数字的"对应值"：
     * 0 1 2 3 4 5 6 7 8 9
     * 0 1 2 3 4 5 6 7 8 9
     * <p>
     * A B C D E F G H J K L M N P R S T U V W X Y Z
     * 1 2 3 4 5 6 7 8 1 2 3 4 5 7 9 2 3 4 5 6 7 8 9
     * <p>
     * VIN码从第1位到第17位的"加权值"：
     * 1 2 3 4 5 6 7 8 9 icon_home_10 icon_home_11 icon_home_12 13 14 15 16 17
     * 8 7 6 5 4 3 2 icon_home_10 0 9 8 7 6 5 4 3 2
     * <p>
     * 例子：
     * 车辆识别码：UU6JA69691D713820第九位为9为校验码，我们可以验证下是否正确。
     * 4×8+4×7+6×6+1×5+1×4+6×3+9×2+6×icon_home_10+1×9+4×8+7×7+1×6+3×5+8×4+2×3+0×0 = 350
     * 350除以11，得31，余9，该余数9即为校验码，和识别码的校验位相同。如果余数为10，则检验位为字母“X”
     * <p>
     * 另外：VIN中不会包含 I、O、Q 三个英文字母
     * （因为字母I 与 数字1 太相似字母O 和Q与 数字0太相似 为了防止读错和混淆）
     *
     * @param vin
     * @return
     */
    public boolean isCarVIN(String vin) {
        Map<Integer, Integer> vinMapWeighting = null;
        Map<Character, Integer> vinMapValue = null;
        vinMapWeighting = new HashMap<Integer, Integer>();
        vinMapValue = new HashMap<Character, Integer>();
        vinMapWeighting.put(1, 8);
        vinMapWeighting.put(2, 7);
        vinMapWeighting.put(3, 6);
        vinMapWeighting.put(4, 5);
        vinMapWeighting.put(5, 4);
        vinMapWeighting.put(6, 3);
        vinMapWeighting.put(7, 2);
        vinMapWeighting.put(8, 10);
        vinMapWeighting.put(9, 0);
        vinMapWeighting.put(10, 9);
        vinMapWeighting.put(11, 8);
        vinMapWeighting.put(12, 7);
        vinMapWeighting.put(13, 6);
        vinMapWeighting.put(14, 5);
        vinMapWeighting.put(15, 4);
        vinMapWeighting.put(16, 3);
        vinMapWeighting.put(17, 2);

        vinMapValue.put('0', 0);
        vinMapValue.put('1', 1);
        vinMapValue.put('2', 2);
        vinMapValue.put('3', 3);
        vinMapValue.put('4', 4);
        vinMapValue.put('5', 5);
        vinMapValue.put('6', 6);
        vinMapValue.put('7', 7);
        vinMapValue.put('8', 8);
        vinMapValue.put('9', 9);
        vinMapValue.put('A', 1);
        vinMapValue.put('B', 2);
        vinMapValue.put('C', 3);
        vinMapValue.put('D', 4);
        vinMapValue.put('E', 5);
        vinMapValue.put('F', 6);
        vinMapValue.put('G', 7);
        vinMapValue.put('H', 8);
        vinMapValue.put('J', 1);
        vinMapValue.put('K', 2);
        vinMapValue.put('M', 4);
        vinMapValue.put('L', 3);
        vinMapValue.put('N', 5);
        vinMapValue.put('P', 7);
        vinMapValue.put('R', 9);
        vinMapValue.put('S', 2);
        vinMapValue.put('T', 3);
        vinMapValue.put('U', 4);
        vinMapValue.put('V', 5);
        vinMapValue.put('W', 6);
        vinMapValue.put('X', 7);
        vinMapValue.put('Y', 8);
        vinMapValue.put('Z', 9);
        boolean resultFlag = false;
        String upperVIN = vin.toUpperCase();
        //排除字母O、I
        if (vin == null || upperVIN.indexOf("O") >= 0 || upperVIN.indexOf("I") >= 0) {
            resultFlag = false;
        } else {
            //1:长度为17
            if (vin.length() == 17) {
                char[] vinArr = upperVIN.toCharArray();
                int amount = 0;
                for (int i = 0; i < vinArr.length; i++) {
                    //VIN码从从第一位开始，码数字的对应值×该位的加权值，计算全部17位的乘积值相加
                    amount += vinMapValue.get(vinArr[i]) * vinMapWeighting.get(i + 1);
                }
                //乘积值相加除以11、若余数为10，即为字母Ｘ
                if (amount % 11 == 10) {
                    if (vinArr[8] == 'X') {
                        resultFlag = true;
                    } else {
                        resultFlag = false;
                    }

                } else {
                    //VIN码从从第一位开始，码数字的对应值×该位的加权值，
                    //计算全部17位的乘积值相加除以11，所得的余数，即为第九位校验值
                    if (amount % 11 != vinMapValue.get(vinArr[8])) {
                        resultFlag = false;
                    } else {
                        resultFlag = true;
                    }
                }
            }
            //1:长度不为17
            if (!vin.equals("") && vin.length() != 17) {
                resultFlag = false;
            }
        }
        return resultFlag;
    }

    /**
     * 判断是否为国内手机号
     */
    public static boolean isPhoneNumber(String str) throws PatternSyntaxException {
        String regExp = "^(13[0-9]|14[56789]|15[^4]|16[6]|17[0-8]|18[0-9]|19[89])\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 判断是否为国内手机号格式
     */
    public static boolean isPhoneNumberType(String str) throws PatternSyntaxException {
        String regExp = "^(1[3-9])\\d{9}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 校验银行卡卡号
     *
     * @param cardNo 银行卡号
     * @return
     */
    public static boolean isBankCard(String cardNo) {

//        char bit = getBankCardCheckCode(cardNo.substring(0, cardNo.length() - 1));
//        return bit != 'N' && cardNo.charAt(cardNo.length() - 1) == bit;

        try {
            int[] cardNoArr = new int[cardNo.length()];
            for (int i = 0; i < cardNo.length(); i++) {
                cardNoArr[i] = Integer.valueOf(String.valueOf(cardNo.charAt(i)));
            }
            for (int i = cardNoArr.length - 2; i >= 0; i -= 2) {
                cardNoArr[i] <<= 1;
                cardNoArr[i] = cardNoArr[i] / 10 + cardNoArr[i] % 10;
            }
            int sum = 0;
            for (int aCardNoArr : cardNoArr) {
                sum += aCardNoArr;
            }
            return sum % 10 == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            //如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /*********************************** 身份证验证开始 ****************************************/

    /**
     * 身份证号码验证
     * 1、号码的结构 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，
     * 八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 2、地址码(前六位数）表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。
     * 3、出生日期码（第七位至十四位）
     * 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。
     * 4、顺序码（第十五位至十七位）表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。
     * 5、校验码（第十八位数）
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 icon_home_10 5 8 4 2 1 6 3 7 9 icon_home_10 5 8 4 2
     * （2）计算模 Y = mod(S, icon_home_11) （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 icon_home_10 校验码: 1 0 X 9 8 7 6 5 4 3 2
     */

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    public static String checkIDCard(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = {"1", "0", "X", "9", "8", "7", "6", "5", "4",
                "3", "2"};
        String[] Wi = {"7", "9", "icon_home_10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "icon_home_10", "5", "8", "4", "2"};
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (/*IDStr.length() != 15 && */IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为18位。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        Ai = IDStr.substring(0, 17);
        if (!isNumeric(Ai)) {
            errorInfo = "身份证号码除最后一位外，都应为数字。";/*"身份证15位号码都应为数字 ; */
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            errorInfo = "身份证生日无效";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                errorInfo = "身份证生日不在有效范围";
                return errorInfo;
            }
        } catch (NumberFormatException | java.text.ParseException e) {
            e.printStackTrace();
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return errorInfo;
        }
        // =====================(end)=====================

        // ================ 地区码是否有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误";
            return errorInfo;
        }
        // ======================(end)======================

        // ================ 判断最后一位的值 ================
        int TotalMulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalMulAiWi = TotalMulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalMulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (!Ai.equals(IDStr)) {
            errorInfo = "身份证无效，不是合法的身份证号码";
            return errorInfo;
        }
        // =====================(end)=====================
        return "";
    }

    /**
     * 功能：设置地区编码
     *
     * @return HashTable 对象
     */
    @SuppressWarnings("unchecked")
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("icon_home_11", "北京");
        hashtable.put("icon_home_12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 功能：判断字符串是否为数字
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param strDate 日期格式字符串
     * @return
     */
    public static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param args
     * @throws ParseException
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws ParseException {
        String IDCardNum = "210181198807193116";
        CheckUtil cu = new CheckUtil();
        Log.d(TAG, "校验身份证号==> " + cu.checkIDCard(IDCardNum));
    }
    /*********************************** 身份证验证结束 ****************************************/

}
