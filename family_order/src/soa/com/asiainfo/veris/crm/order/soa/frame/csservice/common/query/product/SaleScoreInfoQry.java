
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SaleScoreInfoQry extends CSBizBean
{

    public static IDataset queryById(String scoreDeductId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SCORE_DEDUCT_ID", scoreDeductId);
        return Dao.qryByCode("TD_B_SALE_SCORE", "SEL_BY_SCORE_DEDUCT_ID", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByIdEparchy(String scoreDeductId, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("SCORE_DEDUCT_ID", scoreDeductId);
        cond.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_B_SALE_SCORE", "SEL_BY_SCORE_DEDUCT_ID_EPARCHY", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByPkgId(String packageId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        return Dao.qryByCode("TD_B_SALE_SCORE", "SEL_BY_PACKID", cond, Route.CONN_CRM_CEN);
    }

    public static IDataset queryByPkgIdEparchy(String packageId, String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PACKAGE_ID", packageId);
        cond.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TD_B_SALE_SCORE", "SEL_BY_PACKID_EPARCHY", cond, Route.CONN_CRM_CEN);
    }
    
    public static IDataset queryValidScorebyPackageId(String packageId)throws Exception{
    	IData param=new DataMap();
    	param.put("PACKAGE_ID", packageId);
    	
    	return Dao.qryByCode("TD_B_SALE_SCORE", "QRY_SCORE_BY_PACKAGE_ID", param, Route.CONN_CRM_CEN);
    }
}
