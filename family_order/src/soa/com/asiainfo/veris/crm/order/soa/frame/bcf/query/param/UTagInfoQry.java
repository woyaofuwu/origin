
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class UTagInfoQry
{
    /**
     * 翻译td_s_tag表的参数信息，这里用缓存的方法翻译，避免了多次查询影响效率
     * 
     * @param tagCode
     *            标记编码
     * @param tagField
     *            需要翻译的信息
     * @return 翻译结果
     * @throws Exception
     * @author xj
     */
    public static String getTagCharByTagCode(String tagCode) throws Exception
    {
        String eparchyCode = CSBizBean.getTradeEparchyCode(); // 标记对应的地市
        String tagField = "TAG_CHAR";

        return getTagXxxByTagCode(tagCode, tagField, eparchyCode);
    }

    public static String getTagCharByTagCodeEpachy(String eparchyCode, String tagCode) throws Exception
    {
        String tagField = "TAG_CHAR";

        return getTagXxxByTagCode(tagCode, tagField, eparchyCode);
    }

    public static String getTagInfoByTagCode(String tagCode) throws Exception
    {
        String eparchyCode = CSBizBean.getTradeEparchyCode(); // 标记对应的地市
        String tagField = "TAG_INFO";

        return getTagXxxByTagCode(tagCode, tagField, eparchyCode);
    }

    public static String getTagInfoByTagCodeEpachy(String tagCode, String eparchyCode) throws Exception
    {
        String tagField = "TAG_INFO";

        return getTagXxxByTagCode(tagCode, tagField, eparchyCode);
    }

    public static String getTagNumberByTagCodeEpachy(String tagCode, String eparchyCode) throws Exception
    {
        String tagField = "TAG_NUMBER";

        return getTagXxxByTagCode(tagCode, tagField, eparchyCode);
    }

    private static String getTagXxxByTagCode(String tagCode, String tagField, String eparchyCode) throws Exception
    {
        String useTag = "0"; // 标记对应的有效标识：0-有效 1-无效

        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_TAG", new String[]
        { "EPARCHY_CODE", "TAG_CODE", "USE_TAG" }, tagField, new String[]
        { eparchyCode, tagCode, useTag });
    }

}
