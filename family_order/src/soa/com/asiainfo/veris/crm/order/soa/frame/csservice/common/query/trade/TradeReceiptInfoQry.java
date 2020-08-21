 
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

public class TradeReceiptInfoQry
{
    public static IDataset getCnoteInfoByTradeId(String tradeId) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("TRADE_ID", tradeId);
        IDataset results = Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID_PLUS", iParam, Route.getJourDb(BizRoute.getRouteId()));
        if (IDataUtil.isNotEmpty(results))
        {
            for (int i = 0, size = results.size(); i < size; i++)
            {
                // 翻译
                IData resultData = results.getData(i);
                resultData.put("BRAND_NAME", UpcCall.queryBrandNameByChaVal(resultData.getString("BRAND_CODE")));
                resultData.put("ORG_NAME", UDepartInfoQry.getDepartFrameByDepartId(resultData.getString("ORG_INFO")));
                resultData.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(resultData.getString("TRADE_STAFF_ID")));
                String custId = resultData.getString("CUST_ID");
                IDataset customerDataset = CustomerInfoQry.getCustomerByCustID(custId);
                String tradeTypeCode = resultData.getString("TRADE_TYPE_CODE");  
                if (IDataUtil.isEmpty(customerDataset))
                {
                    customerDataset = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
                }
                
                //REQ202004210009【实名制】关于NGBOSS实名登记受理单身份证号码
                IDataset tradeOther = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId,"CHRN");
                if (IDataUtil.isNotEmpty(tradeOther)&&"60".equals(tradeTypeCode))
                {
                	if("实名制办理".equals(tradeOther.first().getString("RSRV_VALUE"))){
                		customerDataset = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
                	}
                }
                
                if (IDataUtil.isNotEmpty(customerDataset))
                {
                    resultData.put("ID_CARD", customerDataset.getData(0).getString("PSPT_ID"));
                }
               
            }
        }
        
        return results;
    }
    
    public static IDataset getCnoteInfoByTradeId2016(String tradeId) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID_PLUS2016", iParam);
    }
    
    public static IDataset getCnoteInfoByTradeId2016_1(String tradeId) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID_PLUS2016_1", iParam,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset getCnoteInfoByTradeId2016_2(String cust_id) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("CUST_ID", cust_id);
        return Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID_PLUS2016_2", iParam);
    }
    
    public static IDataset getCnoteInfoByTradeId2016_3(String tradeId,String cust_id) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("TRADE_ID", tradeId);
        iParam.put("CUST_ID", cust_id);
        return Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID_PLUS2016_3", iParam,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    

    public static IDataset getNoteInfoByPk(String tradeId, String acceptMonth, String noteType) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", acceptMonth);
        param.put("NOTE_TYPE", noteType);
        return Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_NOTEINFO_PK", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 获取打印信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getPrintNoteInfoByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset getReceiptInfoByPk(String trade_type_code, String brand_code, String product_id, String trade_attr, String eparchy_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", trade_type_code);
        param.put("BRAND_CODE", brand_code);
        param.put("PRODUCT_ID", product_id);
        param.put("TRADE_ATTR", trade_attr);
        param.put("EPARCHY_CODE", eparchy_code);

        return Dao.qryByCode("TD_B_TRADE_RECEIPT", "SEL_RECEIPTPARA_BY_PK", param, pagination, Route.CONN_CRM_CEN);
    }

    /**
     * 批量打印数据查询
     * 
     * @param sn
     * @param staffId
     * @param startDate
     * @param endDate
     * @param cancelTag
     * @return
     * @throws Exception
     */
    public static IDataset queryPrintNoteInfo(String sn, String tradeId, String staffId, String startDate, String endDate, String cancelTag, Pagination pagination, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("TRADE_ID", tradeId);
        param.put("TRADE_STAFF_ID", staffId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("CANCEL_TAG", cancelTag);

        return Dao.qryByCodeParser("TF_B_TRADE_CNOTE_INFO", "SEL_BY_SERIAL_NUMBER", param, pagination,Route.getJourDb(routeId));
    }
    
    /**
     * 查询异地补卡结果反馈信息
     * 
     * @param sn
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset queryReCardResult(String sn, String startDate, String endDate,Pagination pagination, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);

        return Dao.qryByCodeParser("TF_F_RECARD_INFO", "SEL_BY_SERIAL_NUMBER_DATE", param, pagination, routeId);
    }
    /**
     * 查询异地补卡结果反馈信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryReCardResult(String tradeId, Pagination pagination,String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
 
        return Dao.qryByCodeParser("TF_F_RECARD_INFO", "SEL_BY_TRADE", param, pagination, routeId);
    }
    public static IDataset queryPrintNoteInfoCt(String sn, String tradeId, String staffId, String startDate, String endDate, String cancelTag, Pagination pagination, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("TRADE_ID", tradeId);
        param.put("TRADE_STAFF_ID", staffId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("CANCEL_TAG", cancelTag);

        return Dao.qryByCodeParser("TF_B_TRADE_CNOTE_INFO", "SEL_BY_SERIAL_NUMBER_CT", param, pagination, Route.getJourDb(routeId));
    }
    /**
     * 纸质单据电子化信息查询
     * wangsc10-20190603-REQ201904300014 关于开发纸质受理单转存功能界面的需求
     * @param cycle
     * @throws Exception
     */
    public static IDataset queryElectronicworkorderbulu(String sn, String startDate, String endDate, String tradeTypeCode, Pagination pagination, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        return Dao.qryByCodeParser("TF_B_TRADE_CNOTE_INFO", "SEL_BY_SERIAL_NUMBER_BULU", param, pagination, Route.getJourDb(routeId));
    }
    /**
     * 纸质单据电子化，传到东软
     * wangsc10-20190603-REQ201904300014 关于开发纸质受理单转存功能界面的需求
     * @param cycle
     * @throws Exception
     */
    public static IDataset toElectronicworkorderbulu(String tradeId,Pagination pagination, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_CNOTE_INFO", "SEL_BY_SERIAL_NUMBER_BULU_TO_DZH", param, pagination, Route.getJourDb(routeId));
    }
    
    public static IDataset queryPrintNoteInfoByuid(String userid, String tradeId, String staffId, String startDate, String endDate, String cancelTag, Pagination pagination, String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userid);
        param.put("TRADE_ID", tradeId);
        param.put("TRADE_STAFF_ID", staffId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        param.put("CANCEL_TAG", cancelTag);

        return Dao.qryByCodeParser("TF_B_TRADE_CNOTE_INFO", "SEL_BY_USER_ID", param, pagination, routeId);
    }
    
    public static IDataset queryPrintNotByTradeId(String tradeId)throws Exception{
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        
        return Dao.qryByCodeParser("TF_B_TRADE_CNOTE_INFO", "QRY_CNOTE_BY_TRADE_ID", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    public static void updatePrintNotByTradeId(String tradeId, String rsrvTag2)throws Exception{
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("RSRV_TAG2", rsrvTag2);
        
        Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", "UPD_CNOTE_RSRV_TAG2_BY_TRADE_ID", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    
    // 2016111610405400202251_REQ201611070014电子化存储新版客户兼容性优化 liquan
    public static IDataset getCnoteOtherTradeInfoByTradeId(String tradeId) throws Exception
    {
        IData iParam = new DataMap();
        iParam.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_CNOTE_INFO", "SEL_BY_TRADEID_PLUS_1", iParam,Route.getJourDb(BizRoute.getRouteId()));
    }

}
