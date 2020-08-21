package com.asiainfo.veris.crm.order.soa.group.speclist;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class SpecListBean extends CSBizBean
{

	private static final long serialVersionUID = 1L;

	/**
	 * 查询特殊名单信息
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public IDataset queryUserInfo(IData inparams, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	parser.addSQL(" Select a.* ");
    	parser.addSQL(" From TF_F_USER a");
    	parser.addSQL(" Where 1 = 1");
    	parser.addSQL(" AND a.SERIAL_NUMBER = :USER_MOBILE ");
    	parser.addSQL(" AND a.REMOVE_TAG = '0' ");
    	return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
	
	/**
	 * 查询特殊名单信息
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public IDataset querySpeclist(IData inparams, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	parser.addSQL(" Select a.USER_MOBILE, a.SPECIAL_TYPE, a.TYPE_NAME, a.PROMPT_MESSAGE, a.ISBOLD, a.COLOR, a.REMOVE_TAG, a.OPER_STAFF_ID, ");
    	parser.addSQL(" TO_CHAR(a.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
    	parser.addSQL(" TO_CHAR(a.OPER_TIME, 'yyyy-mm-dd hh24:mi:ss') OPER_TIME ");
    	parser.addSQL(" From TF_F_CUST_SPEC_LIST a");
    	parser.addSQL(" Where 1 = 1");
    	parser.addSQL(" And a.USER_MOBILE = :USER_MOBILE ");
    	parser.addSQL(" And a.SPECIAL_TYPE = :SPECIAL_TYPE ");
    	parser.addSQL(" And a.TYPE_NAME = :TYPE_NAME ");
    	parser.addSQL(" And a.REMOVE_TAG = :REMOVE_TAG ");
    	parser.addSQL(" And a.REMOVE_TAG in ('0','1') ");
    	parser.addSQL(" And a.INSERT_TIME Between TO_DATE(:START_DATE, 'yyyy-mm-dd') And TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')+1");
    	parser.addSQL(" Order By a.SPECIAL_TYPE DESC");
    	return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
	/**
	 * 查询特殊名单信息(客服用)
	 * @param params
	 * @return
	 * @throws Exception
	 */
    public IDataset querySpeclistForKF(IData inparams, Pagination pagination) throws Exception
    {
    	SQLParser parser = new SQLParser(inparams);
    	parser.addSQL(" Select a.USER_MOBILE, a.SPECIAL_TYPE, a.TYPE_NAME, a.PROMPT_MESSAGE, a.ISBOLD, a.COLOR, a.REMOVE_TAG, a.OPER_STAFF_ID, ");
    	parser.addSQL(" TO_CHAR(a.INSERT_TIME, 'yyyy-mm-dd hh24:mi:ss') INSERT_TIME, ");
    	parser.addSQL(" TO_CHAR(a.OPER_TIME, 'yyyy-mm-dd hh24:mi:ss') OPER_TIME ");
    	parser.addSQL(" From TF_F_CUST_SPEC_LIST a");
    	parser.addSQL(" Where 1 = 1");
    	parser.addSQL(" And a.USER_MOBILE = :USER_MOBILE ");
    	parser.addSQL(" And a.REMOVE_TAG in ('0') ");
    	parser.addSQL(" And a.INSERT_TIME Between TO_DATE(:START_DATE, 'yyyy-mm-dd') And TO_DATE(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')+1");
    	parser.addSQL(" Order By a.SPECIAL_TYPE DESC");
    	return Dao.qryByParse(parser, pagination, Route.CONN_CRM_CG);
    }
    
    /**
     * 新增特殊名单
     * @param inparams
     * @return
     * @throws Exception
     * @author wuhao
     * @date 2019-7-16
     */
    public IDataset insertSpeclist(IData params) throws Exception
    {
    	IDataset dataset = new DatasetList();
        params.put("OPER_STAFF_ID", getVisit().getStaffId());
        params.put("INSERT_TIME", SysDateMgr.getSysTime());
        params.put("REMOVE_TAG", "0");
        Dao.insert("TF_F_CUST_SPEC_LIST", params, Route.CONN_CRM_CG);
        return dataset;
    }
    
    /**
     * 删除特殊名单
     * @param inparams
     * @return
     * @throws Exception
     * @author wuhao
     * @date 2019-7-16
     */
    public IDataset deleteSpeclist(IData params) throws Exception
    {
    	IDataset dataset = new DatasetList();
        params.put("OPER_STAFF_ID", getVisit().getStaffId());
        params.put("OPER_TIME", SysDateMgr.getSysTime());
        params.put("REMOVE_TAG", "1");
        Dao.save("TF_F_CUST_SPEC_LIST", params, new String[] { "USER_MOBILE","INSERT_TIME" }, Route.CONN_CRM_CG);
        return dataset;
    }
    
    /**
     * 修改特殊名单
     * @param inparams
     * @return
     * @throws Exception
     * @author wuhao
     * @date 2019-7-16
     */
    public IDataset updateSpeclist(IData params) throws Exception
    {
    	IDataset dataset = new DatasetList();
        params.put("OPER_STAFF_ID", getVisit().getStaffId());
        params.put("OPER_TIME", SysDateMgr.getSysTime());
        params.put("REMOVE_TAG", "0");
        Dao.save("TF_F_CUST_SPEC_LIST", params, new String[] { "USER_MOBILE","INSERT_TIME" }, Route.CONN_CRM_CG);
        return dataset;
    }
}
