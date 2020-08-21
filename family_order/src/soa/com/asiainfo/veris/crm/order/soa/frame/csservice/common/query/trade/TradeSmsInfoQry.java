
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeSmsInfoQry
{
    public static IDataset getElementTwoCheckConfig(String tradeTypeCode, String brandCode, String productId, String eparchyCode, String inModeCode, String objTypeCode, String objCode, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", productId);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("IN_MODE_CODE", inModeCode);
        param.put("OBJ_TYPE_CODE", objTypeCode);
        param.put("OBJ_CODE", objCode);
        param.put("MODIFY_TAG", modifyTag);

        IDataset result = Dao.qryByCodeParser("TD_B_TRADE_SMS", "SEL_ELEMENT_TWOCHECK", param, Route.CONN_CRM_CEN);

        return result;
    }

    /**
     * 根据trade_type_code查询短信内容参数
     * 
     * @param inParams
     * @return
     * @throws Exception
     */
    public static IDataset getTradeSmsInfo(String trade_type_code, String brand_code, String product_id, String cancel_tag, String in_mode_code, String eparchy_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", trade_type_code);
        param.put("BRAND_CODE", brand_code);
        param.put("PRODUCT_ID", product_id);
        param.put("CANCEL_TAG", cancel_tag);
        param.put("IN_MODE_CODE", in_mode_code);
        param.put("EPARCHY_CODE", eparchy_code);

        IDataset infos = Dao.qryByCodeParser("TD_B_TRADE_SMS", "SEL_TRADESMS_PARA", param, pagination, Route.CONN_CRM_CEN);
        return infos;
    }

    /**
     * 查询模板标识
     * 
     * @param tradeTypeCode
     * @param brandCode
     * @param productId
     * @param cancelTag
     * @param epachyCode
     * @param inModeCode
     * @return
     * @throws Exception
     */
    public static IDataset getTradeSmsInfos(String tradeTypeCode, String brandCode, String productId, String cancelTag, String epachyCode, String inModeCode, String eventType) throws Exception
    {

        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", productId);
        param.put("CANCEL_TAG", cancelTag);
        param.put("EPARCHY_CODE", epachyCode);
        param.put("EVENT_TYPE", eventType);
        param.put("IN_MODE_CODE", inModeCode);

        IDataset result = Dao.qryByCodeParser("TD_B_TRADE_SMS", "SEL_TRADE_SMS_BY_KYES", param, Route.CONN_CRM_CEN);

        return result;
    }
}
