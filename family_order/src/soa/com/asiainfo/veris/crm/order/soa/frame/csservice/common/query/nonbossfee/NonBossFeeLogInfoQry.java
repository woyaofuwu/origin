
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.nonbossfee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class NonBossFeeLogInfoQry
{

    /**
     * 根据LogID获取非BOSS收款补录信息
     * 
     * @param logId
     * @return
     * @throws Exception
     */
    public static IDataset queryNonBossFeeByLog(String logId) throws Exception
    {
        IData inData = new DataMap();
        inData.put("LOG_ID", logId);
        return Dao.qryByCodeParser("TF_F_NONBOSSFEE_LOG", "SEL_FEELIST_BY_LOGID", inData);
    }

    /**
     * 查询非boss费用补录记录
     * REQ201409250007201409非出账业务收款及发票管理需求
     * 用于“非出账业务查询及发票管理”查询
     * chenxy3 2015-3-4
     */
    public static IDataset queryNonBossFeeLog(IData inData, Pagination pagination, String routeId) throws Exception
    {
        /*IData inData = new DataMap();
        inData.put("STAFF_ID", staffId);
        inData.put("TRADE_CITY_CODE", cityCode);
        inData.put("LOG_ID", logId);
        inData.put("PAY_NAME", payName);
        inData.put("FEE_NAME", feeName);
        inData.put("START_DATE", startDate);
        inData.put("END_DATE", endDate);
        inData.put("RSRV_STR4", nonBossFeePrint);*/ 
        if (StringUtils.isBlank(routeId))
        {
            routeId = CSBizBean.getTradeEparchyCode();
        }
        return Dao.qryByCodeParser("TF_F_NONBOSSFEE_LOG", "SEL_NONBOSSFEE_LOG", inData, pagination, routeId);
    }
    
    /**
     * 根据条件查询业务客户参数
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150206  
     */
    public static IDataset queryNonBossFeeUserItems(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams); 
  
        parser.addSQL(" select T.TYPE_ID, T.DATA_ID, T.DATA_NAME, T.PARAM_NAME, T.PARA_CODE1, T.PARA_CODE2, T.PARA_CODE3, T.PARA_CODE4, ");
        parser.addSQL(" to_char(trunc(T.START_DATE,'dd'),'yyyy-mm-dd') START_DATE, to_char(trunc(T.END_DATE,'dd'),'yyyy-mm-dd') END_DATE,  T.UPDATE_STAFF_ID, T.UPDATE_TIME, T.REMARK "); 
        parser.addSQL(" from td_s_nonbosspara t ");
        parser.addSQL(" where t.type_id = 'PAY_USER_NAME_ALL' ");
        parser.addSQL(" and t.DATA_NAME=:DATA_NAME  ");  

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 根据条件查询业务客户参数2 根据代码取中文
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150206  
     */
    public static IDataset queryNonBossFeeUserItems2(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams); 
  
        parser.addSQL(" select T.TYPE_ID, T.DATA_ID, T.DATA_NAME, T.PARAM_NAME, T.PARA_CODE1, T.PARA_CODE2, T.PARA_CODE3, T.PARA_CODE4, ");
        parser.addSQL(" to_char(trunc(T.START_DATE,'dd'),'yyyy-mm-dd') START_DATE, to_char(trunc(T.END_DATE,'dd'),'yyyy-mm-dd') END_DATE,  T.UPDATE_STAFF_ID, T.UPDATE_TIME, T.REMARK "); 
        parser.addSQL(" from td_s_nonbosspara t ");
        parser.addSQL(" where t.type_id = 'PAY_USER_NAME_ALL' ");
        parser.addSQL(" and T.DATA_ID=:DATA_ID  "); 

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 查询冲红重打发票需要的内容
     * @param inparams
     * @return
     * @throws Exception
     * @chenxy3 20150206  
     */
    public static IDataset queryNonBossLogByTradeid(IData inparams) throws Exception
    {

        SQLParser parser = new SQLParser(inparams); 
  
        parser.addSQL(" select t.LOG_ID,t.TRADE_STAFF_ID,t.PAY_NAME_REMARK PAY_NAME_DES,:O_TAX_NO O_TAX_NO,:O_TICKET_ID O_TICKET_ID,:INVOICE_TYPE INVOICE_TYPE");
        parser.addSQL(" from TF_F_NONBOSSFEE_LOG t where t.TRADE_ID=:TRADE_ID");  

        return Dao.qryByParse(parser);
    }
    
    /**
     * 根据业务流水号获取非BOSS收款补录信息
     * 
     * @param tradeId
     * @param refundTag
     * @return
     * @throws Exception
     */
    public static IDataset queryNonBossFeeLogByTrade(String tradeId, String refundTag) throws Exception
    {
        IData inData = new DataMap();
        inData.put("TRADE_ID", tradeId);
        inData.put("REFUND_TAG", refundTag);
        return Dao.qryByCodeParser("TF_F_NONBOSSFEE_LOG", "SEL_BY_TRADEID", inData);
    }
}
