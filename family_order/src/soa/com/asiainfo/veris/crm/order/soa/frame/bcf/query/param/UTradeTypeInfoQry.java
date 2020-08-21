
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class UTradeTypeInfoQry
{

    /**
     * 根据业务类型查询业务交易优先级
     * 
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    public static String getTradePri(String tradeTypeCode) throws Exception
    {
        String EPARCHY_CODE = CSBizBean.getTradeEparchyCode();

        IData data = getTradeType(tradeTypeCode, EPARCHY_CODE);

        String priority = "-1";

        if (IDataUtil.isNotEmpty(data))
        {
            priority = data.getString("PRIORITY");// 优先级
        }

        return priority;
    }

    /**
     * 根据业务类型编码查询业务类型名称,带地州编码
     * 
     * @param tradeTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IData getTradeType(String tradeTypeCode, String eparchyCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_TYPE_CODE", tradeTypeCode);
        inparams.put("EPARCHY_CODE", eparchyCode);

        IDataset ids = Dao.qryByCode("TD_S_TRADETYPE", "SEL_BY_PK", inparams, Route.CONN_CRM_CEN);

        if (IDataUtil.isEmpty(ids))
        {
            return null;
        }

        return ids.getData(0);
    }

    /**
     * 根据业务类型编码查询业务类型名称,不带地州编码
     * 
     * @param tradeTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static String getTradeTypeName(String tradeTypeCode) throws Exception
    {
        String eparchyCode = CSBizBean.getTradeEparchyCode();

        String tradeTypeName = getTradeTypeName(tradeTypeCode, eparchyCode);

        return tradeTypeName;
    }

    /**
     * 根据业务类型编码查询业务类型名称,带地州编码
     * 
     * @param tradeTypeCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static String getTradeTypeName(String tradeTypeCode, String eparchyCode) throws Exception
    {
        IData data = getTradeType(tradeTypeCode, eparchyCode);

        String tradeTypeName = "";

        if (IDataUtil.isNotEmpty(data))
        {
            tradeTypeName = data.getString("TRADE_TYPE");// 业务类型名称
        }

        return tradeTypeName;
    }

}
