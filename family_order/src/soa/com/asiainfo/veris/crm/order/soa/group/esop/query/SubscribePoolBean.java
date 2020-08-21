package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.group.esop.esopdesktop.EsopDeskTopConst;

public class SubscribePoolBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertSubscribePool(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_SUBSCRIBE_POOL", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updByRelIbsysidAndState(IData param) throws Exception
    {
    	IData input = new DataMap();
    	input.put("REL_IBSYSID", param.getString("RSRV_STR5"));

    	Dao.executeUpdateByCodeCode("TF_B_EOP_SUBSCRIBE_POOL", "UPD_BY_IBSYSID", input, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updateSubscribePoolStateByApply(IData param) throws Exception
    {
    	String nodeId = param.getString("NODE_ID");
        StringBuilder sbf = new StringBuilder();
        sbf.append("UPDATE TF_B_EOP_SUBSCRIBE_POOL SET ");
        if(nodeId.equals("apply")) {//申请节点后将状态改为A
        	sbf.append(" STATE = 'A'");
        }else if(nodeId.equals("End")) {//结束节点前将状态改为9
        	sbf.append(" STATE = '9'");
        }
        sbf.append(" WHERE REL_IBSYSID = :REL_IBSYSID ");
        sbf.append(" AND POOL_VALUE = :POOL_VALUE ");
        if(nodeId.equals("End")) {//只修改A状态的数据
        	sbf.append("AND STATE = 'A'");
        }

        Dao.executeUpdate(sbf, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static void updateSubscribePoolState(IData param) throws Exception
    {
        StringBuilder sbf = new StringBuilder();
        sbf.append("UPDATE TF_B_EOP_SUBSCRIBE_POOL SET ");
        sbf.append(" STATE = :STATE");
        sbf.append(" WHERE 1=1 ");
        sbf.append(" and (POOL_NAME = :POOL_NAME or :POOL_NAME is null)");
        sbf.append(" and (POOL_VALUE = :POOL_VALUE or :POOL_VALUE is null)");
        sbf.append(" and (POOL_CODE = :POOL_CODE or :POOL_CODE is null)");
        sbf.append(" and (STATE = :STATEOLD or :STATEOLD is null)");
        sbf.append(" and (REL_IBSYSID = :REL_IBSYSID or :REL_IBSYSID is null)");

        Dao.executeUpdate(sbf, param, Route.getJourDb(BizRoute.getRouteId()));
    }
	
}