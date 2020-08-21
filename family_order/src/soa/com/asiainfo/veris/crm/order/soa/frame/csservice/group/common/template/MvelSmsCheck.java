
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.template;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class MvelSmsCheck
{

    /**
     * @param tradeAllData
     * @param attrCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByAttr(IData tradeAllData, String attrCode, String attr_value) throws Exception
    {
        boolean flag = false;

        IDataset attrTradeDatas = tradeAllData.getDataset("TF_B_TRADE_ATTR");

        if (IDataUtil.isNotEmpty(attrTradeDatas))
        {

            for (int i = 0, size = attrTradeDatas.size(); i < size; i++)
            {
                IData attrTradeData = attrTradeDatas.getData(i);

                if (StringUtils.equals(attrCode, attrTradeData.getString("ATTR_CODE")) && StringUtils.equals(attr_value, attrTradeData.getString("ATTR_VALUE")))
                {
                    flag = true;
                    break;
                }
            }

        }

        return flag;
    }

    /**
     * 主台账表objCode字段有值才发短信
     * 
     * @param tradeAllData
     * @param brandCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByBatchOrder(IData tradeAllData, String objCode) throws Exception
    {
        boolean flag = false;

        IDataset tradeDatas = tradeAllData.getDataset("TF_B_TRADE");

        if (IDataUtil.isNotEmpty(tradeDatas))
        {
            String batch_id = tradeDatas.getData(0).getString(objCode);

            if (StringUtils.isNotBlank(batch_id))
            {
                return true;
            }

        }

        return flag;
    }

    /**
     * modify_tag配0 则匹配上发短信，modify_tag配1 则没匹配上发短信
     * 
     * @param tradeAllData
     * @param brandCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByBrand(IData tradeAllData, String brandCode, String modifyTag) throws Exception
    {
        boolean flag = false;

        IDataset tradeDatas = tradeAllData.getDataset("TF_B_TRADE");

        if (IDataUtil.isNotEmpty(tradeDatas))
        {
            String brand_code = tradeDatas.getData(0).getString("BRAND_CODE");

            if (StringUtils.equals("0", modifyTag) && StringUtils.equals(brandCode, brand_code))
            {
                return true;
            }

            if (StringUtils.equals("1", modifyTag) && !StringUtils.equals(brandCode, brand_code))
            {
                return true;
            }
        }

        return flag;
    }

    /**
     * 根据diacnt_code和modify_tag匹配短信模板
     * 
     * @param tradeAllData
     * @param discntCode
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByDisOper(IData tradeAllData, String discntCode, String modifyTag) throws Exception
    {
        boolean flag = false;

        IDataset disTradeDatas = tradeAllData.getDataset("TF_B_TRADE_DISCNT");

        if (IDataUtil.isNotEmpty(disTradeDatas))
        {

            for (int i = 0, size = disTradeDatas.size(); i < size; i++)
            {
                IData disTradeData = disTradeDatas.getData(i);

                if (StringUtils.equals(discntCode, disTradeData.getString("DISCNT_CODE")) && StringUtils.equals(modifyTag, disTradeData.getString("MODIFY_TAG")))
                {
                    flag = true;
                    break;
                }
            }

        }

        return flag;
    }

    /**
     * 根据product_id和modify_tag匹配短信模板
     * 
     * @param tradeAllData
     * @param productId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkByPrdOper(IData tradeAllData, String productId, String modifyTag) throws Exception
    {
        boolean flag = false;

        IDataset prdTradeDatas = tradeAllData.getDataset("TF_B_TRADE_PRODUCT");

        if (IDataUtil.isNotEmpty(prdTradeDatas))
        {
            for (int i = 0, size = prdTradeDatas.size(); i < size; i++)
            {
                IData prdTradeData = prdTradeDatas.getData(i);

                if (StringUtils.equals(productId, prdTradeData.getString("PRODUCT_ID")) && StringUtils.equals(modifyTag, prdTradeData.getString("MODIFY_TAG")))
                {
                    flag = true;
                    break;
                }
            }

        }

        return flag;
    }

    /**
     * 根据service_id和modify_tag匹配短信模板
     * 
     * @param tradeAllData
     * @param serviceId
     * @param modifyTag
     * @return
     * @throws Exception
     */
    public static boolean chkBySvcOper(IData tradeAllData, String serviceId, String modifyTag) throws Exception
    {
        boolean flag = false;

        IDataset svcTradeDatas = tradeAllData.getDataset("TF_B_TRADE_SVC");

        if (IDataUtil.isNotEmpty(svcTradeDatas))
        {

            for (int i = 0, size = svcTradeDatas.size(); i < size; i++)
            {
                IData svcTradeData = svcTradeDatas.getData(i);

                if (StringUtils.equals(serviceId, svcTradeData.getString("SERVICE_ID")) && StringUtils.equals(modifyTag, svcTradeData.getString("MODIFY_TAG")))
                {
                    flag = true;
                    break;
                }
            }

        }

        return flag;
    }

    /**
     * 主台账的指定扩展字段配短信模版ID 匹配上发短信
     * 
     * @param tradeAllData
     * @param templateId
     * @param objCode
     * @return
     * @throws Exception
     */

    public static boolean chkByTemplateId(IData tradeAllData, String templateId, String objCode) throws Exception
    {
        boolean flag = false;

        IDataset tradeDatas = tradeAllData.getDataset("TF_B_TRADE");

        if (IDataUtil.isNotEmpty(tradeDatas))
        {
            IData tradeData = tradeDatas.getData(0);

            String rsrvStr = tradeData.getString(objCode, "");

            if (StringUtils.equals(templateId, rsrvStr))
            {
                return true;
            }

        }

        return flag;
    }
}
