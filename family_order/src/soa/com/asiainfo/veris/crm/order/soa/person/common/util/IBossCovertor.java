
package com.asiainfo.veris.crm.order.soa.person.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class IBossCovertor
{

    /** ************************* 证件类型转换开始 ************************** */
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

    /** ************************* 用户状态转换开始 ************************** */
    static IData userStateData = new DataMap();

    // 如有不同的用户状态，请在此处添加
    // 当前只添加了 语音服务的状态
    static
    {
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
    }

    /** ************************* 证件类型转换结束 ************************** */

    public static String getCrmPsptType(String ibossPspType)
    {
        if (ibossPspType == null || ibossPspType.length() == 0)
            return "";
        return psptTypeData.getString("IBOSS" + ibossPspType, "");
    }

    public static String getIBossPsptType(String crmPspType)
    {
        if (crmPspType == null || crmPspType.length() == 0)
            return "";
        return psptTypeData.getString("CRM" + crmPspType, "");
    }

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
        if (crmUserState == null || crmUserState.length() == 0)
            return "";
        return userStateData.getString(crmUserState, "");
    }
}
