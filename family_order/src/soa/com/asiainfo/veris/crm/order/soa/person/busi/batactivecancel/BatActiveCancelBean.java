package com.asiainfo.veris.crm.order.soa.person.busi.batactivecancel;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;  
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 批量返销营销活动
 * 2016-01-25
 * chenxy3
 * */
public class BatActiveCancelBean extends CSBizBean
{
	/**
     *  获取营销活动
     * */
	public static IDataset queryCampnTypes(IData inParam) throws Exception
    {
		IData param = new DataMap(); 
		param.put("LABEL_ID", "S1000");
//        return Dao.qryByCode("TD_B_LABEL", "SEL_BY_LABEL_ID", param, Route.CONN_CRM_CEN);
		IDataset result = UpcCall.qryChildrenCatalogsByIdLevel("4", "S1000");//UpcCall.qryCataLogsByTypeRootLevel("K", "PERSON", "2");
        for(int i=0; i<result.size(); i++)
        {
            IData info = result.getData(i);
            info.put("LABEL_ID", info.getString("CATALOG_ID"));
            info.put("LABEL_NAME", info.getString("CATALOG_NAME"));
        }
        return result;
    }
	
	/**
	 * 根据活动类型获取产品* 
	 * */
	public static IDataset querySaleActiveProductByLabel(IData inParam) throws Exception
    {
		String labelId = inParam.getString("CAMPN_TYPE");
        String eparchyCode = inParam.getString("EPARCHY_CODE");
		IData cond = new DataMap();
        cond.put("LABEL_ID", labelId);
        cond.put("EPARCHY_CODE", eparchyCode);
        
        IDataset productInfos = new DatasetList();
        IDataset results = UpcCall.qryCatalogsByUpCatalogId(labelId);
        for(int i=0;i<results.size();i++)
        {
            IData result = results.getData(i);
            IDataset paraInfos = CommparaInfoQry.getCommNetInfo("CSM", "522", result.getString("CATALOG_ID"));
            if(IDataUtil.isNotEmpty(paraInfos))
            {
                continue;
            }
            result.put("PRODUCT_ID", result.getString("CATALOG_ID"));
            result.put("PRODUCT_NAME", result.getString("CATALOG_NAME"));
            productInfos.add(result);
        }
        return productInfos;
        
//        return Dao.qryByCode("TD_B_PRODUCT", "SEL_BY_LABEL_ID", cond, Route.CONN_CRM_CEN);
    }
	
	/**
	 * 根据产品ID获取包* 
	 * */
	public static IDataset queryPackageByProdID(IData inParam) throws Exception
    {
		String prodId = inParam.getString("PRODUCT_ID");
        String eparchyCode = inParam.getString("EPARCHY_CODE");
		IData cond = new DataMap();
        cond.put("PRODUCT_ID", prodId);
        cond.put("EPARCHY_CODE", eparchyCode);
        
        IDataset saleActives = UpcCall.qryOffersByCatalogId(prodId);
        for(int i=0;i<saleActives.size();i++)
        {
            IData saleActive = saleActives.getData(i);
            saleActive.put("PACKAGE_ID", saleActive.getString("OFFER_CODE"));
            saleActive.put("PACKAGE_NAME", saleActive.getString("OFFER_CODE") + "|" + saleActive.getString("OFFER_NAME"));
            saleActive.put("PRODUCT_ID", prodId);
        }
        return saleActives;
//        return Dao.qryByCode("TD_B_PACKAGE", "SEL_PACKAGE_BY_PRODUCTID_BAT", cond, Route.CONN_CRM_CEN);
    }
	
	/**
	 * 根据task_id获取TF_B_TRADE_BAT_TASK数据* 
	 * */
	public static IDataset queryBatTaskInfo(IData inParam) throws Exception
    { 
        String batchTaskId = inParam.getString("BATCH_TASK_ID");
        String acceptMon=inParam.getString("ACCEPT_MONTH");
		IData cond = new DataMap();
        cond.put("BATCH_TASK_ID", batchTaskId); 
        cond.put("ACCEPT_MONTH", acceptMon); 
        return Dao.qryByCode("TF_B_TRADE_BAT_TASK", "SEL_BAT_TASK_BY_TASKID", cond, Route.getJourDb(Route.CONN_CRM_CG));
    }
	
	
	/**
	 * 取返销用户营销活动信息，先要查得到才行
	 * */
	public static IDataset queryUserSaleActiveInfo(IData inParam) throws Exception
    {  
		IData cond = new DataMap();
        cond.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER","")); 
        cond.put("CAMPN_TYPE", inParam.getString("CAMPN_TYPE","")); 
        cond.put("PRODUCT_ID", inParam.getString("PRODUCT_ID","")); 
        cond.put("PACKAGE_ID", inParam.getString("PACKAGE_ID","")); 
        return Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_TRADE_SALE_ACTIVE_INFO", cond); 
    }
}