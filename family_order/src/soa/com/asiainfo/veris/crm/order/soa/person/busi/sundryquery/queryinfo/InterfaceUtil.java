
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class InterfaceUtil
{
    /*************************** 证件类型转换开始 ***************************/
    static IData psptTypeData = new DataMap();

    // 如有不同的证件类型，请在此处添加
    static
    {
        psptTypeData.put("IBOSS" + "00", "0");// 身份证
        psptTypeData.put("IBOSS" + "01", "1");// VIP卡号
        psptTypeData.put("IBOSS" + "02", "A");// 护照
        psptTypeData.put("IBOSS" + "03", "C");// 军官证
        psptTypeData.put("IBOSS" + "99", "Z");// 其他证件

        psptTypeData.put("CRM" + "0", "00");// 身份证
        psptTypeData.put("CRM" + "1", "01");// VIP卡号
        psptTypeData.put("CRM" + "A", "02");// 护照
        psptTypeData.put("CRM" + "C", "03");// 军官证
        psptTypeData.put("CRM" + "Z", "99");// 其他证件

    }

    public static Map VALIDSTATE_MAP = new HashMap(); // 可以办理易登机服务的用户状态

    static
    {
        VALIDSTATE_MAP.put("0", "开通");
        VALIDSTATE_MAP.put("N", "人工开机");
    }

    /**
     * 3.客户登记身份证件的连续6位数字
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:zhuyu
     * @date:2014-6-27
     */
    public static boolean checkContainInPspt(String inpasswd, String psptId) throws Exception
    {
        if (psptId.indexOf(inpasswd) != -1)
        {
            return true;
        }
        return false;
    }

    /**
     * 4.客户手机号码中的连续6位数字。
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:zhuyu
     * @date:2014-6-27
     */
    public static boolean checkContainInSn(String inpasswd, String serialNumber) throws Exception
    {
        if (serialNumber.indexOf(inpasswd) != -1)
        {
            return true;
        }
        return false;
    }

    /**
     * 5.客户手机号码中前三位+后三位或后三位+前三位的组合
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:zhuyu
     * @date:2014-6-27
     */
    public static boolean checkContainInSnQh3Bit(String inpasswd, String serialNumber) throws Exception
    {
        String q3b = serialNumber.substring(0, 3); // 手机号码前三位
        String h3b = serialNumber.substring(serialNumber.length() - 3); // 手机号码后三位

        if ((q3b + h3b).equals(inpasswd))
        {
            return true;
        }
        else if ((h3b + q3b).equals(inpasswd))
        {
            return true;
        }
        return false;
    }

    /**
     * 6.密码的不同号码数需大于3，例如：112211，不同号码数为2
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:zhuyu
     * @date:2014-6-27
     */
    public static boolean checkPasswdRepeatBitNum(String inpasswd) throws Exception
    {
        IData bitMap = new DataMap();
        for (int i = 0; i < inpasswd.length(); i++)
        {
            char key = inpasswd.charAt(i);
            bitMap.put("KEY" + key, "VALUE");
        }
        if (bitMap.size() <= 3)
        {
            return true;
        }

        return false;
    }

    /**
     * 8.密码不能全为偶数或奇数，如24680
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:zhuyu
     * @date:2014-6-27
     */
    public static boolean checkPasswsAllJO(String inpasswd) throws Exception
    {
        IData numMap = new DataMap();
        for (int i = 0; i < inpasswd.length(); i++)
        {
            char num = inpasswd.charAt(i);
            int mod = num % 2; // 结果是0或1
            numMap.put("KEY" + mod, "VALUE");
        }
        if (numMap.size() == 1)
        {
            return true;
        }
        return false;
    }

    /**
     * 7.前三位的密码不能后三位密码一致，如123123
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:zhuyu
     * @date:2014-6-27
     */
    public static boolean checkPasswsQh3Bit(String inpasswd) throws Exception
    {
        String q3b = inpasswd.substring(0, 3); // 密码前三位
        String h3b = inpasswd.substring(inpasswd.length() - 3); // 密码后三位
        if (q3b.equals(h3b))
        {
            return true;
        }
        return false;
    }

    /**
     * 2.重复号码：如"666666"或"000000"等
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     * @author:zhuyu
     * @date:2014-6-27
     */
    public static boolean checkRepeatNumber(String inpasswd) throws Exception
    {
        /**
         * 算法简单描述： 相邻位相减为0
         */
        for (int i = 0; i < (inpasswd.length() - 1); i++)
        {
            if ((new Integer(inpasswd.charAt(i + 1)) - new Integer(inpasswd.charAt(i))) != 0)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean checkSerieisNumber(String inpasswd) throws Exception
    {

        /**
         * 算法简单描述： 相邻位相减等于正负一
         */
        // 递增
        for (int i = 0; i < (inpasswd.length() - 1); i++)
        {
            int n1 = new Integer(inpasswd.charAt(i + 1)) - new Integer(inpasswd.charAt(i));
            if (n1 >= 0 && n1 != 1)
            {
                return false;
            }
        }
        // 递减
        for (int i = 0; i < (inpasswd.length() - 1); i++)
        {
            int n1 = new Integer(inpasswd.charAt(i + 1)) - new Integer(inpasswd.charAt(i));
            if (n1 <= 0 && n1 != -1)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较身份证号码，支持15位身份证和18位身份证转换对比
     * 
     * @author zhuyu
     * @date 2014-07-10
     * @param psptId
     * @param inPsptId
     * @return
     */
    public static boolean comparePsptId(String psptId, String inpsptId)
    {
        // 两个身份证号码不一样
        if (psptId.length() == 15 && inpsptId.length() == 18)
        {
            inpsptId = inpsptId.substring(0, 6) + inpsptId.substring(8, 17);
        }

        if (psptId.length() == 18 && inpsptId.length() == 15)
        {
            psptId = psptId.substring(0, 6) + psptId.substring(8, 17);
        }

        // 相同返回true
        if (psptId.equalsIgnoreCase(inpsptId))
        {
            return true;
        }
        // 不相同返回false
        return false;
    }

    public static String convertBrandCode(String brandCode)
    {

        if (StringUtils.isBlank(brandCode))
        {
            return "09";
        }
        brandCode = brandCode.trim();
        if ("G001".equals(brandCode))
        {
            return "01";
        }
        else if ("G002".equals(brandCode))
        {
            return "02";
        }
        else if ("G010".equals(brandCode))
        {
            return "03";
        }
        else
        {
            return "09";
        }
    }

    public static String convertEnabletag(String enableTag)
    {
        if (StringUtils.equals(enableTag, "0"))
        {
            return "立即生效";
        }
        else if (StringUtils.equals(enableTag, "1"))
        {
            return "下账期生效";
        }
        else if (StringUtils.equals(enableTag, "2"))
        {
            return "次日生效";
        }
        else if (StringUtils.equals(enableTag, "3"))
        {
            return "可选立即或下账期生效";
        }
        else if (StringUtils.equals(enableTag, "4"))
        {
            return "绝对时间";
        }
        return "";
    }

    public static String convertStr(String source, int model)
    {
        String result = "";
        if ((source == null) || ("".equals(source.trim())))
        {
            return "";
        }

        result = source;

        if (model == 1)
        {
            if (source.indexOf("+") > -1)
            {
                result = source.replaceAll("\\+", "%2B");
            }
        }
        else if (model == 2)
        {
            if (source.indexOf("%2B") > -1)
            {
                result = source.replaceAll("%2B", "+");
            }
        }

        return result;
    }

    /**
     * 将 服务属性拆串
     * 
     * @param value
     * @return
     * @throws Exception
     */
    public static IData detachStr(String value) throws Exception
    {
        IData data = new DataMap();
        String[] param = value.split("\\|");
        data.put("PAY_TYPE", "0".equals(param[1]) ? "按时间触发" : "按金额触发");
        data.put("PAY_TIME", param[2]);
        data.put("PAYBNUM", param[3]);
        return data;
    }

    public static String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    /**
     * 根据KIND_ID获取APPLY_TYPE
     * 
     * @param kindId
     * @return
     * @throws Exception
     */
    public static String getApplyType(String kindId) throws Exception
    {
        String applyType = "";
        if ("BIP2C021_T2002021_1_0".equals(kindId))
        { // 四元素采集操作反馈
            applyType = "21";
        }
        else if ("BIP2C023_T2002023_1_0".equals(kindId))
        { // 信息搜集操作反馈
            applyType = "22";
        }
        else if ("BIP2C025_T2002025_1_0".equals(kindId))
        { // 参数配置操作反馈
            applyType = "23";
        }
        else if ("BIP2C027_T2002027_1_0".equals(kindId))
        { // 固件升级操作反馈
            applyType = "24";
        }
        else if ("BIP2C029_T2002029_1_0".equals(kindId))
        { // 固件回退操作反馈
            applyType = "25";
        }
        else if ("BIP2C031_T2002031_1_0".equals(kindId))
        { // 应用软件下载安装操作反馈
            applyType = "26";
        }
        else if ("BIP2C033_T2002033_1_0".equals(kindId))
        { // 终端锁定操作反馈
            applyType = "27";
        }
        else if ("BIP2C035_T2002035_1_0".equals(kindId))
        { // 终端解锁操作反馈
            applyType = "28";
        }
        else if ("BIP2C037_T2002037_1_0".equals(kindId))
        { // 终端数据清除操作反馈
            applyType = "29";
        }
        else
        {
            applyType = "20";
        }
        return applyType;
    }

    public static String getBrandName(String strBrandId)
    {
        String strBrandOut = "";
        if ("01".equals(strBrandId))
        {
            strBrandOut = "全球通"; // 全球通
        }
        else if ("02".equals(strBrandId))
        {
            strBrandOut = "神州行"; // 神州行
        }
        else if ("03".equals(strBrandId))
        {
            strBrandOut = "动感地带"; // 动感地带
        }
        else
        {
            strBrandOut = "其它"; // 其它品牌
        }
        return strBrandOut;
    }

    /**
     * 品牌
     */
    public static String getBrandParam(String param)
    {
        String result = "";
        /*
         * --GS03,G010 动感地带 --G001 全球通 --G002,GS01 神州行
         */

        if ("VPMN".equals(param))
            result = "3";
        else if ("G801".equals(param))
            result = "3";
        else if ("G802".equals(param))
            result = "3";
        else if ("VPCN".equals(param))
            result = "3";
        else if ("VPFN".equals(param))
            result = "3";
        else if ("IP01".equals(param))
            result = "3";
        else if ("G005".equals(param))
            result = "3";
        else if ("G001".equals(param))
            result = "0";
        else if ("G002".equals(param))
            result = "1";
        else if ("G010".equals(param) || "GS03".equals(param))
            result = "2";
        else if ("GS01".equals(param))
            result = "1";
        else if ("G003".equals(param))
            result = "3";
        else
            result = "3";

        return result;
    }

    public static String getCrmPsptType(String ibossPspType)
    {
        if (ibossPspType == null || ibossPspType.length() == 0)
            return "";
        return psptTypeData.getString("IBOSS" + ibossPspType, "");
    }

    // 客户级别
    public static String getCustLevel(String classId)
    {
        String level = "";
        if ("4".equals(classId))
        {
            level = "3";
        }
        else if ("3".equals(classId))
        {
            level = "2";
        }
        else if ("2".equals(classId))
        {
            level = "1";
        }
        else
        {
            level = "0";
        }

        return level;
    }

    /**
     * 客户级别
     */
    public static String getCustLevelParam(String param)
    {
        String result = "";

        if ("0".equals(param))
            result = "300";
        else if ("1".equals(param))
            result = "304";
        else if ("2".equals(param))
            result = "303";
        else if ("3".equals(param))
            result = "302";
        else if ("4".equals(param))
            result = "301";
        else if ("A".equals(param))
            result = "302";
        else if ("B".equals(param))
            result = "301";
        else if ("C".equals(param))
            result = "303";
        else if ("D".equals(param))
            result = "302";
        else if ("E".equals(param))
            result = "301";
        else
            result = "100";

        return result;
    }

    public static String getIBossPsptType(String crmPspType)
    {
        if (crmPspType == null || crmPspType.length() == 0)
            return "";
        return psptTypeData.getString("CRM" + crmPspType, "");
    }

    /*************************** 证件类型转换结束 ***************************/

    /** ************************* 用户状态转换开始 ************************** */

    // 如有不同的用户状态，请在此处添加

    public static String getIBossUserRank(String viptypecode, String vipclassid)
    {
        String ibossrank = "100";// 普通客户
        if (viptypecode.equals("0") || viptypecode.equals("2"))
        {
            if (vipclassid.equals("1"))
                ibossrank = "304";
            else if (vipclassid.equals("2"))
                ibossrank = "303";
            else if (vipclassid.equals("3"))
                ibossrank = "302";
            else if (vipclassid.equals("4"))
                ibossrank = "301";
        }
        else if (viptypecode.equals("3"))
        {
            if (vipclassid.equals("1"))
                ibossrank = "304";
            else if (vipclassid.equals("2"))
                ibossrank = "303";
            else if (vipclassid.equals("8"))
                ibossrank = "302";
        }
        return ibossrank;
    }

    public static String getIBossUserState(String crmUserState)
    {
        IData userStateData = new DataMap();
        userStateData.put("N", "00");// 信用有效时长开通
        userStateData.put("T", "01");// 骚扰电话半停机
        userStateData.put("0", "00");// 开通
        userStateData.put("1", "02");// 申请停机
        userStateData.put("2", "02");// 挂失停机
        userStateData.put("3", "02");// 并机停机
        userStateData.put("4", "02");// 局方停机
        userStateData.put("5", "02");// 欠费停机
        userStateData.put("6", "04");// 申请销号
        userStateData.put("7", "02");// 高额停机
        userStateData.put("8", "03");// 欠费预销号
        userStateData.put("9", "04");// 欠费销号
        userStateData.put("A", "01");// 欠费半停机
        userStateData.put("B", "01");// 高额半停机
        userStateData.put("E", "02");// 转网销号停机
        userStateData.put("F", "02");// 申请预销停机
        userStateData.put("G", "01");// 申请半停机
        userStateData.put("I", "02");// 申请停机（收月租）
        if (StringUtils.isBlank(crmUserState))
        {
            return "";
        }
        else
        {
            return userStateData.getString(crmUserState, "");
        }
    }

    public static String getUserState4Mobile(String crmUserState) // 手机营业厅用户状态转化
    {
        IData userStateData4Mobile = new DataMap();
        userStateData4Mobile.put("0", "00");
        userStateData4Mobile.put("1", "02");
        userStateData4Mobile.put("2", "02");
        userStateData4Mobile.put("3", "02");
        userStateData4Mobile.put("4", "02");
        userStateData4Mobile.put("5", "02");
        userStateData4Mobile.put("6", "04");
        userStateData4Mobile.put("7", "02");
        userStateData4Mobile.put("8", "03");
        userStateData4Mobile.put("9", "03");
        userStateData4Mobile.put("A", "01");
        userStateData4Mobile.put("B", "01");
        userStateData4Mobile.put("C", "02");
        userStateData4Mobile.put("D", "02");
        userStateData4Mobile.put("E", "04");
        userStateData4Mobile.put("F", "03");
        userStateData4Mobile.put("G", "01");
        userStateData4Mobile.put("H", "03");
        userStateData4Mobile.put("I", "02");
        userStateData4Mobile.put("J", "02");
        userStateData4Mobile.put("K", "02");
        userStateData4Mobile.put("L", "02");
        userStateData4Mobile.put("M", "02");
        userStateData4Mobile.put("N", "00");
        userStateData4Mobile.put("O", "02");
        userStateData4Mobile.put("Q", "02");

        if (StringUtils.isBlank(crmUserState))
        {
            return "";
        }
        else
        {
            return userStateData4Mobile.getString(crmUserState, "");
        }
    }

    /**
     * 获取用户状态编码 xuwb5
     */
    public static String getUserStateParam(String param)
    {
        String result = "";

        if ("0".equals(param))
            result = "00";
        else if ("1".equals(param))
            result = "02";
        else if ("2".equals(param))
            result = "02";
        else if ("3".equals(param))
            result = "02";
        else if ("4".equals(param))
            result = "02";
        else if ("5".equals(param))
            result = "02";
        else if ("6".equals(param))
            result = "04";
        else if ("7".equals(param))
            result = "02";
        else if ("8".equals(param))
            result = "03";
        else if ("9".equals(param))
            result = "03";
        else if ("A".equals(param))
            result = "01";
        else if ("B".equals(param))
            result = "01";
        else if ("C".equals(param))
            result = "02";
        else if ("D".equals(param))
            result = "02";
        else if ("E".equals(param))
            result = "04";
        else if ("F".equals(param))
            result = "03";
        else if ("G".equals(param))
            result = "01";
        else if ("H".equals(param))
            result = "03";
        else if ("I".equals(param))
            result = "02";
        else if ("J".equals(param))
            result = "02";
        else if ("K".equals(param))
            result = "02";
        else if ("L".equals(param))
            result = "02";
        else if ("M".equals(param))
            result = "02";
        else if ("N".equals(param))
            result = "00";
        else if ("O".equals(param))
            result = "02";
        else if ("Q".equals(param))
            result = "02";
        else
            result = "00";

        return result;
    }

    /*
     * 品牌代码转换 输入值： G001：全球通 G002：神州行 G010：动感地带 返回值： 01：全球通；02：神州行；03：动感地带；09：其他品牌
     */
    public static String transBrand(String brandCode)
    {
        String brand_code = "";
        if ("G001".equals(brandCode))
        {
            brand_code = "01"; // 全球通
        }
        else if ("G002".equals(brandCode))
        {
            brand_code = "02"; // 神州行
        }
        else if ("G010".equals(brandCode))
        {
            brand_code = "03"; // 动感地带
        }
        else
        {
            brand_code = "09"; // 其它品牌
        }
        return brand_code;
    }

    /**
     * 截取必要字段
     * 
     * @author zhuyu
     * @param data
     * @return
     */
    public IData getIBossData(IData data)
    {
        String IBOSS_PREX = "IBOSS_";
        IData returndata = new DataMap();
        if (data != null)
        {
            Iterator it = data.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                if (((String) key).startsWith(IBOSS_PREX))
                    returndata.put(((String) key).replaceAll(IBOSS_PREX, ""), entry.getValue());
            }
        }
        return returndata;
    }

}
