package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class GroupProjectNameBean extends CSBizBean {
    public static IDataset qryProjectName(IData param) throws Exception
    {
		SQLParser parser = new SQLParser(param);
		parser.addSQL("SELECT distinct(t.PROJECT_NAME) FROM TF_B_EOP_GROUP_PROJECTNAME t");
		parser.addSQL(" where 1=1");
		parser.addSQL(" and t.CUST_ID = :CUST_ID");
		parser.addSQL(" and t.GROUP_ID =:GROUP_ID ");
		IDataset dataset =  Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
		return dataset;
    }
    
    public static void insertProjectName(IData param) throws Exception
    {
    	String sysdateStr = TimeUtil.getSysTime();
    	param.put("INST_ID", SeqMgr.getInstId(getVisit().getLoginEparchyCode()));
    	param.put("INSERT_STAFFID", getVisit().getStaffId());
    	param.put("INSERT_TIME", sysdateStr);
        Dao.insert("TF_B_EOP_GROUP_PROJECTNAME", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
}
