
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ElemInfoQry;

public class TradeSvcInfoQry extends CSBizBean
{
    /**
     * 根据tradeId查询所有的用户服务备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakSvcByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_SVC_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * 办理国漫调用营销管理接口数据准备
     * 
     * @param userId
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getDataForSalemanmByRoming(String userId, String tradeId) throws Exception
    {
        IData param = new DataMap();

        param.put("USER_ID_A", userId);
        param.put("USER_ID_B", userId);
        param.put("USER_ID_E", userId);
        param.put("TRADE_IDA", tradeId);
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("TRADE_IDA", tradeId);
        IDataset  res=Dao.qryByCode("TF_B_TRADE_SVC", "SEL_RN_IMPORTANT_BY_USERID_NEW1", param);
        if(IDataUtil.isNotEmpty(res)){
        	for(int i=0;i<res.size();i++){
        		IData temp=res.getData(i);
                IDataset  res2=Dao.qryByCode("TF_B_TRADE_SVC", "SEL_RN_IMPORTANT_BY_USERID_NEW2", data,Route.getJourDb(BizRoute.getRouteId()));
                if(IDataUtil.isNotEmpty(res2)){
                temp.put("FEE", res2.getData(0).getString("FEE",""));
                }
        	}
        }
        
        
        return res;
    }

    public static IDataset getElementFromPackageByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        IDataset userElement = Dao.qryByCode("TF_B_TRADE_SVC", "SEL_SVC_DISCNT_ELE_BY_TRADEID", param);
        if (IDataUtil.isEmpty(userElement))
            return userElement;

        for (int i = 0; i < userElement.size(); i++)
        {
            IData map = userElement.getData(i);
            map.put("MODIFY_TAG", "exist");
        }
        ElemInfoQry.fillElementName(userElement);
        
        return userElement;
    }

    public static IDataset getElementFromPackageTrade(String trade_id, String product_id, String user_id, String bboss_flag, Pagination pagination) throws Exception
    {

        IDataset dataset = new DatasetList();
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("PRODUCT_ID", product_id);
        param.put("USER_ID", user_id);
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        if ("1".equals(bboss_flag))// 读取注销台帐
        {
            dataset = Dao.qryByCode("TF_B_TRADE_SVC", "SEL_SVC_DISCNT_ELE_BY_TRADEID_PRODID_ZXDAATA", param, pagination);
        }
        else
        {
            dataset = Dao.qryByCode("TF_B_TRADE_SVC", "SEL_SVC_DISCNT_ELE_BY_TRADEID_PRODID", param, pagination);
        }

        return dataset;
    }

    /**
     * 根据tradeId查询所有的用户服务台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getTradeSvcByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_SVC", "SEL_BY_TRADEID", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    // 取消预约产品变更获取服务变更信息
    public static IDataset getTradeSvcInfosByTradeId(String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_SVC", "SEL_CANCLE_BY_TRADEID", inparams, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    public static IDataset getUserSvcomTrade(IData paramSvc) throws Exception
    {

        return Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BY_TRADESVC_SERVICE_ID", paramSvc);
    }

    /**
     * 查询备份的服务属性
     * 
     * @return
     * @throws Exception
     */
    public static IDataset querySvcAttrFromBakByTradeId(String tradeId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SVC_BAK", "SEL_TRADEATTR_SVC_FROM_BAK", param);
    }

    /**
     * 根据tradeId获取备份的服务
     * 
     * @param tradeId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset querySvcFromBakByTradeId(String tradeId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_B_TRADE_SVC", "SEL_TRADESVC_FROM_BAK", param);

    }

    /**
     * @param tradeId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeSvc(String tradeId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_SVC", "TRADE_SVC_SEL", param, pagination);
    }

    public static IDataset queryTradeSvcByTradeId(String tradeId, String modifyTag, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MODIFY_TAG", modifyTag);
        return Dao.qryByCode("TF_B_TRADE_SVC", "SEL_SVC_BY_TRADEID", param, eparchyCode);
    }

    public static IDataset queryTradeSvcByTradeIdAndUserId(String tradeId, String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BYUSER_SERVICE_ATTR", param);
        return dataset;
    }

    /**
     * @param userId
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeSvcsByUserId(String userId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_TRADE_SVC", "SEL_BY_USER", param, pagination);
    }

    /**
     * 更新服务开始时间
     * 
     * @author chenzm
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updateStartDate(String trade_id, String start_date) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("START_DATE", start_date);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_SVC", "UPD_STARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 获取台台帐服务表
     * 
     * @param iData
     * @return
     */
    public IDataset getTradeSvc(String tradeId, Pagination pagination) throws Exception
    {

        if (tradeId == null || "".equals(tradeId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103);
        }
        IData params = new DataMap();
        params.put("VTRADE_ID", tradeId);
        try
        {
            IDataset iDataset = Dao.qryByCodeParser("TF_B_TRADE_SVC", "SEL_BY_TRADEID", params, pagination, Route.CONN_CRM_CEN);
            if (!IDataUtil.isNotEmpty(iDataset))
            {
                ((IData) iDataset.get(0)).put("X_RECORDNUM", iDataset.size());
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103);
            }
            return iDataset;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(TradeException.CRM_TRADE_32);
            return null;
        }
    }
    
    public static IDataset getValidTradeBakSvcByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_SVC_BAK", "SEL_VALID_BAK_BY_TRADE", params);
    }
}
