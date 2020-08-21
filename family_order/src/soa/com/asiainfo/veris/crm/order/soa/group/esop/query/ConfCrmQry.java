package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ConfCrmQry extends CSBizBean {
    public static IDataset qryIbsysid(String productId, String groupId,String bpmTempletId) throws Exception
    {
		SQLParser parser = new SQLParser(null);
		parser.addSQL("select * from tf_bh_eop_subscribe ");
		parser.addSQL("where 1=1");
		parser.addSQL("and BUSI_CODE = '"+productId+"'");
		parser.addSQL("and GROUP_ID = '"+groupId+"'");
		parser.addSQL("and BPM_TEMPLET_ID = '"+bpmTempletId+"'");
		IDataset dataset =  Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
		return dataset;
    }
    
    public static IDataset qryLineNo(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);
		parser.addSQL("select * from tf_b_eop_subscribe_pool ");
		parser.addSQL(" where 1=1");
		parser.addSQL(" and rel_ibsysid = :REL_IBSYSID");
		parser.addSQL(" and state = :STATE");
		return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qrySubscribe(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);
		parser.addSQL("select * from tf_bh_eop_subscribe ");
		parser.addSQL(" where 1=1");
		parser.addSQL(" and IBSYSID = :IBSYSID");
		return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
    }
    
	public static IDataset qryStateByRelIbsysidPoolValue(String relIbsysid, String poolValue) throws Exception {
		IData param = new DataMap();
		param.put("REL_IBSYSID", relIbsysid);
		param.put("POOL_VALUE", poolValue);

		return Dao.qryByCodeParser("TF_B_EOP_SUBSCRIBE_POOL", "SEL_BY_POOLVALUE_RELIBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	
	public static IDataset qrySubscribePool(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);
		parser.addSQL("select POOL_ID, POOL_NAME, POOL_CODE, POOL_VALUE, POOL_LEVEL, STATE, REL_IBSYSID, CREATE_DATE, UPDATE_DATE, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3 ");
		parser.addSQL("from tf_b_eop_subscribe_pool ");
		parser.addSQL(" where 1=1");
		parser.addSQL(" and POOL_NAME = :POOL_NAME");
		parser.addSQL(" and POOL_VALUE = :POOL_VALUE");
		parser.addSQL(" and POOL_CODE = :POOL_CODE");
		parser.addSQL(" and STATE = :STATE");
		parser.addSQL(" and REL_IBSYSID = :REL_IBSYSID");
		return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
    }
}
