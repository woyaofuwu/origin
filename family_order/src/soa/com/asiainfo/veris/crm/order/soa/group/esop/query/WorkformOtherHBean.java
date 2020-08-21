package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class WorkformOtherHBean
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformOtherH(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_EOP_OTHER", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryOtherBySubIbsysidRecordNum(String subIbsysid, String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SUB_IBSYSID", subIbsysid);
    	param.put("RECORD_NUM", recordNum);
    	return Dao.qryByCode("TF_BH_EOP_OTHER", "SEL_BY_SUBIBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryOtherBySubIbsysidAttrCode(String subIbsysid, String attrCode) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SUB_IBSYSID", subIbsysid);
    	param.put("ATTR_CODE", attrCode);
    	return Dao.qryByCode("TF_BH_EOP_OTHER", "SEL_BY_SUBIBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryOtherByIbsysidAttrCode(String ibsysid, String attrCode) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("ATTR_CODE", attrCode);
    	return Dao.qryByCode("TF_BH_EOP_OTHER", "SEL_BY_IBSYSID_ATTRCODE", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryHOtherByIbsysidNodeId(String ibsysid, String nodeId) throws Exception {
        if(StringUtils.isBlank(ibsysid)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "传入IBSYSID不能为空！");
        }
        if(StringUtils.isBlank(nodeId)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "传入NODE_ID不能为空！");
        }
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", nodeId);

        StringBuilder strSql = new StringBuilder(1000);
        strSql.append(" SELECT * FROM TF_BH_EOP_OTHER T ");
        strSql.append(" WHERE T.IBSYSID =:IBSYSID ");
        strSql.append(" AND T.NODE_ID =:NODE_ID ");
        strSql.append(" and t.sub_ibsysid =( ");
        strSql.append(" SELECT max(a.sub_ibsysid) ");
        strSql.append(" FROM TF_BH_EOP_OTHER a ");
        strSql.append(" WHERE a.IBSYSID =T.IBSYSID ");
        strSql.append(" AND a.NODE_ID =:NODE_ID) ");

        return Dao.qryBySql(strSql, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryOtherByIbsysidAttrCodeRecordNum(String ibsysid, String attrCode,String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("ATTR_CODE", attrCode);
    	param.put("RECORD_NUM", recordNum);
    	return Dao.qryByCode("TF_BH_EOP_OTHER", "SEL_BY_IBSYSID_ATTRCODE_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
