
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.dreamnet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class WithinSetDreamNetQry
{

    public static IDataset selOrderInfo(IData param) throws Exception
    {
        IDataset result = Dao.qryByCode("TF_F_USER_SVC", "SEL_PLATORDERINFO_BY_SN11", param);

        return result;
    }

    public static IDataset selPlatAttrInfo(IData param) throws Exception
    {
        IDataset musicattrs = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_PLATATTR_BY_USERID", param);
        return musicattrs;
    }


    
    public static IDataset queryByIdTypeAndId(String idType, String id, String eparchyCode) throws Exception
    {
    	//TD_B_QRY_RULE_CONFIG\SEL_BY_ID_TYPE_AND_ID
    	IData param = new DataMap();
    	param.put("ID_TYPE", idType);
    	param.put("ID", id);
    	param.put("EPARCHY_CODE", eparchyCode);
    	IDataset result = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_BY_ID_TYPE_AND_ID", param);
    	
    	return result;
    }
    
    public static IDataset queryByDiscntCode(String idType, String userId, String eparchyCode) throws Exception
    {
    	IData param = new DataMap();
    	param.put("ID_TYPE", idType);
    	param.put("USER_ID", userId);
    	param.put("EPARCHY_CODE", eparchyCode);
    	IDataset result = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_BY_DISCNT_CODE", param);
    	
    	return result;
    }
    
    public static IDataset queryBySaleactivePackageId(String userId, String eparchyCode) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("EPARCHY_CODE", eparchyCode);
    	IDataset result = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_BY_SALEACTIVE_PACKAGEID", param);
    	
    	return result;
    }
    
    public static IDataset queryPlatSvcByUserId(String userId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	IDataset result = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_PLATSVC_BY_USER_ID", param);
    	
    	return result;
    }
    
    public static IDataset querySvcByUserId(String userId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	IDataset result = Dao.qryByCode("TD_B_QRY_RULE_CONFIG", "SEL_SVC_BY_USER_ID", param);
    	
    	return result;
    }
}
