package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformProductSubHBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformProductH(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_EOP_PRODUCT_SUB", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryProductByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("VALID_TAG", EcEsopConstants.STATE_VALID);
        return Dao.qryByCode("TF_BH_EOP_PRODUCT_SUB", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IData qryProductByIbsysidRecordNum(String ibsysid,String recordeNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    //	param.put("VALID_TAG", EcEsopConstants.STATE_VALID);
    	param.put("RECORD_NUM", recordeNum);
        IDataset productInfos = Dao.qryByCode("TF_BH_EOP_PRODUCT_SUB", "SEL_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
        if(DataUtils.isNotEmpty(productInfos))
        {
        	return productInfos.first();
        }
        return new DataMap();
    }
}
