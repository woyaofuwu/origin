package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;



import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.CrmDAO;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.busi.resale.InterForResaleIntfBean;


/**
 * @author yiyb
 * @create_date：2014-07
 */
public class InterforResalQry {

	private static final Logger logger = Logger.getLogger(InterforResalQry.class);

	/**
	 * 同步信息插入订单子产品表
	 * 
	 * @param input
	 * @throws Exception
	 */
	public static void saveOrderInfo(IData input) throws Exception{
		
		DBConnection conn =	new DBConnection("cen1",true,false);
		
		try{
			IDataUtil.chkParamNoStr(input, "MSISDN");  
			IDataUtil.chkParamNoStr(input, "IMSI");  
			IDataUtil.chkParamNoStr(input, "USER_ID");  
	
			IDataUtil.chkParamNoStr(input, "OPR_NUMB"); 
			IDataUtil.chkParamNoStr(input, "OP_TIME"); 
			IDataUtil.chkParamNoStr(input, "OPR_CODE"); 
			IDataUtil.chkParamNoStr(input, "MVNO_ID"); 
			
		
			input.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
			input.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
			input.put("STATUS", "1");
			
			InterforResalQry.insertIntoOrderPf(conn, input);
			
			String oprCode = input.getString("OPR_CODE");
			
			//订单参数信息
			IDataset newParams = input.getDataset("NEW_PARAMS");
			IDataset oldParams = input.getDataset("OLD_PARAMS");
			
//			if (!"1009".equals(oprCode) && (oldParams == null || oldParams.size() <1) && (newParams == null || newParams.size() <1)) {
//				
//				CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务变更前开通参数OLD_PARAMS和业务变更后开通参数NEW_PARAMS不能同时为空！");
//			}
			
			if(newParams != null && newParams.size() > 0){
				for(int i=0; i<newParams.size();i++){
						
					IData param = newParams.getData(i);
					if (param == null || param.isEmpty()) {
						
						/**
						 * REQ201703210004_关于下发转售业务优化支撑改造方案的通知_4032
						 * @author zhuoyingzhi
						 * @date 20170522
						 */
						InterForResaleIntfBean.updTradeLTEBFn(input, "4032", "业务变更后开通参数不能为空！","1");
						/*************************end*************************/
						CSAppException.apperr(CrmCommException.CRM_COMM_1178, "业务变更后开通参数不能为空！");
					}
					IDataUtil.chkParamNoStr(param, "INFO_CODE");  
					IDataUtil.chkParamNoStr(param, "INFO_VALUE"); 
					
					String infoCode = param.getString("INFO_CODE");
					//针对 CFU  CFB  CFNRY CFNRC 取它的第二属性 CFUNUM CFBNUM CFNRYNUM CFNRCNUM

					if ("CFUNUM".equals(infoCode) || "CFBNUM".equals(infoCode) 
							|| "CFNRYNUM".equals(infoCode) ||"CFNRCNUM".equals(infoCode)) {
						input.put("NEW_"+infoCode,param.getString("INFO_VALUE"));
					}
					
					if ("NEWIMSI".equals(infoCode)&& "1002".equals(oprCode)) {
						input.put("NEWIMSI", param.getString("INFO_VALUE"));
					}
					param.put("MODIFY_TAG", "0"); 
					param.put("OPR_NUMB",input.getString("OPR_NUMB"));
					param.put("INFO_TYPE","1");
					param.put("UPDATE_TIME",SysDateMgr.getSysTime());
					param.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
					param.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
					
					InterforResalQry.insertIntoOrderPFParams(conn, param);
				}
			}
			
			if(oldParams != null && oldParams.size() > 0){
				for(int i=0; i<oldParams.size();i++){
					
					IData param = oldParams.getData(i);
					if (param == null || param.isEmpty()) {
						/**
						 * REQ201703210004_关于下发转售业务优化支撑改造方案的通知_4032
						 * @author zhuoyingzhi
						 * @date 20170522
						 */
						InterForResaleIntfBean.updTradeLTEBFn(input, "4032", "业务变更前开通参数不能为空！", "1");
						/*************************end*************************/						
						CSAppException.apperr(CrmCommException.CRM_COMM_1179, "业务变更前开通参数不能为空！");
					}
					IDataUtil.chkParamNoStr(param, "INFO_CODE");  
					IDataUtil.chkParamNoStr(param, "INFO_VALUE"); 
					
					String infoCode = param.getString("INFO_CODE");
					//针对 CFU  CFB  CFNRY CFNRC 取它的第二属性 CFUNUM CFBNUM CFNRYNUM CFNRCNUM

					if ("CFUNUM".equals(infoCode) || "CFBNUM".equals(infoCode) 
							|| "CFNRYNUM".equals(infoCode) ||"CFNRCNUM".equals(infoCode)) {
						input.put("OLD_"+infoCode,param.getString("INFO_VALUE"));
					}
					param.put("MODIFY_TAG", "1"); 
					param.put("OPR_NUMB",input.getString("OPR_NUMB"));
					param.put("INFO_TYPE","0");
					param.put("UPDATE_TIME", SysDateMgr.getSysTime());
					param.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
					param.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
					
					InterforResalQry.insertIntoOrderPFParams(conn, param);
					
				}
			}
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			/**
		     * REQ201605270005 转售业务保障方案需求
		     * @CREATED by chenxy3@2016-5-31
		     */
			IData inparams=new DataMap();
			inparams.put("STATE", "1");
			inparams.put("RSPCODE", "5999");
			inparams.put("RSPDESC", "服务开通失败.");
			inparams.put("SERIAL_NUMBER", input.getString("MSISDN"));
			inparams.put("OPR_NUMB", input.getString("OPR_NUMB"));
			InterForResaleIntfBean.updTradeLTEB(inparams);
			//CSAppException.apperr(CrmCommException.CRM_COMM_1180, e.getMessage());
			logger.error(e);
			throw e;
		}finally{
			conn.close();
		}
	}
	/**
	 * @param pd
	 * @param data
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void insertIntoLtebUser(IData data, IDataset dataList,String userId ) throws Exception {
		
		IData param = new DataMap();
		param.put("PARTITION_ID", userId.substring(userId.length() -4));
		param.put("USER_ID", userId);
		param.put("LTEB_USER_ID",data.getString("USER_ID"));
		param.put("CUST_ID", "-1");
		param.put("ACCT_ID", "-1");
		param.put("SERIAL_NUMBER", data.getString("MSISDN"));
		param.put("IMSI", data.getString("IMSI"));
		param.put("OPEN_DATE", SysDateMgr.getSysTime());
		param.put("REMOVE_TAG", "0");
		Dao.insert("TF_F_LTEB_USER", param,Route.CONN_CRM_CEN);
		
		for(int i=0; i< dataList.size();i++){
			
			param.put("STATE_ATTR",dataList.getData(i).getString("INFO_ATTR"));
			param.put("STATE_ATTR_VALUE",dataList.getData(i).getString("INFO_ATTR_VALUE"));
			param.put("STATE_CODE", dataList.getData(i).getString("INFO_CODE"));
			param.put("STATE_VALUE",dataList.getData(i).getString("INFO_VALUE"));
			param.put("RSRV_STR1",data.getString("OPR_NUMB")); //保存此次操作流水
			param.put("START_DATE", data.getString("UPDATE_TIME"));
			param.put("UPDATE_TIME",data.getString("UPDATE_TIME"));
			param.put("END_DATE", SysDateMgr.getTheLastTime());
			Dao.insert("TF_F_LTEB_USER_STATE", param,Route.CONN_CRM_CEN);
		}
		
	}
	
	/**
	 * 根据info_code的值查询commpara表，取crm的服务id
	 * @param pd
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static void queryCommparaByBat(IData data,IDataset params) throws Exception{
		
		String spellParam = "";
		IData inData = new DataMap();
		inData.put("SUBSYS_CODE", "CSM");
		inData.put("PARAM_ATTR", "5432");
       
		for (int i = 0; i < params.size(); i++) {
			IData param = params.getData(i);
			spellParam += ",:INFO_CODE" + i;
			inData.put("INFO_CODE" + i, param.getString("INFO_CODE"));
		}
			
		String reGex = ":PARAM_CODE";
		String replaceMent = spellParam.substring(1);
		
		String sqlStmt = getSqlByCode("TD_S_COMMPARA","SEL_BY_IN_RESALE");
		StringBuilder sqlStmtNew = new StringBuilder(sqlStmt.replaceAll(reGex, replaceMent));
		
		IDataset  codeIDList = Dao.qryBySql(sqlStmtNew, inData, Route.CONN_CRM_CEN);
		
		
		Iterator retnLter = params.iterator();
		while (retnLter.hasNext()) {
			boolean flag = true;
			IData dealData = ((DataMap) retnLter.next());
			String infoCode = dealData.getString("INFO_CODE") ;
			
			for (int i = 0; i < codeIDList.size(); i++) {
				String codeStr = codeIDList.getData(i).getString("PARAM_CODE");
				if (codeStr != null && codeStr.equals(infoCode)) {
					flag = false;
					if ("CFU".equals(infoCode) || "CFB".equals(infoCode) 
						|| "CFNRY".equals(infoCode) ||"CFNRC".equals(infoCode)) {
						
						String cfnum = data.getString("NEW_"+infoCode+"NUM","");
						dealData.put("INFO_ATTR",infoCode+"NUM");
						dealData.put("INFO_ATTR_VALUE", "".equals(cfnum)?"111111":cfnum);
						if ("0".equals(data.getString("NEW_"+infoCode+"NUM"))) {
							dealData.put("MODIFY_TAG", "1");
						}
					}
					dealData.put("SERVICE_ID", codeIDList.getData(i).getString("PARA_CODE1"));
					dealData.put("MAIN_TAG", codeIDList.getData(i).getString("PARA_CODE2"));
					dealData.put("SVC_STATE_CODE", codeIDList.getData(i).getString("PARA_CODE3"));//服务状态编码
					dealData.put("COMMON_STATE_CODE", codeIDList.getData(i).getString("PARA_CODE4"));//服务正常状态编码
				}
			}
		   //如果在commpara中没有配置对应的服务，则直接删除，不处理	
			if (flag) {
				retnLter.remove();
			}
		}
	}
	
	/**
	 * 获取表中指定字段的值
	 * @param updateOrder
	 * @throws Exception
	 */
	public static String queryColumnValue(IData param,String colString) throws Exception{
		
		IDataset restList = Dao.qryByCode("TF_F_LTEB_USER", "SEL_BY_LTEUSER_ID", param,Route.CONN_CRM_CEN);
		if(restList != null && restList.size() > 0){
			return restList.getData(0).getString(colString);
		}
		return "";
	}
	
	/**
	 * 4G BOSS开通工单表同步
	 * @param conn
	 * @param data
	 * @throws SQLException
	 */
	
	public static void insertIntoOrderPf(DBConnection conn,IData input) throws Exception{
		CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
		dao.insert(conn,"TF_B_LTEB_ORDER_PF", input);
	}
	
	/**
	 * 4G BOSS开通订单参数表同步
	 * @param conn
	 * @param data
	 * @throws SQLException
	 */
	public static void insertIntoOrderPFParams(DBConnection conn,IData input) throws Exception{
		
		CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
		dao.insert(conn,"TF_B_LTEB_ORDER_PF_PARAMS", input);
		
	}

	/**
	 * 更新订单表中的状态
	 * @param updateOrder
	 * @throws Exception
	 */
	public static void updateInfo(IData param) throws Exception{
		
        Dao.executeUpdateByCodeCode(param.getString("TNAME"), param.getString("SREF"), param,Route.CONN_CRM_CEN);	
	}
	

	/**
	 * 更新订单表中的状态
	 * @param updateOrder
	 * @throws Exception
	 */
	public static String getSqlByCode(String TNAME,String SREF) throws Exception{
		
		IData param = new DataMap();
		param.put("TNAME", TNAME);
		param.put("SREF", SREF);
		IDataset sqlData = Dao.qryByCode("CODE_CODE", "SEL_BY_TAB_REF", param, Route.CONN_CRM_CEN);
		
    	String sqlstmt = sqlData.size() == 1 ? ((String) sqlData.get(0,"SQL_STMT")) : null;
		if (sqlstmt == null)
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "CODE_CODE not exist " + TNAME
					+ " and " + SREF + " recode");

		return sqlstmt;
	}

	
	public static IDataset queryAllUserState(IData param) throws Exception{
		
		return Dao.qryByCode("TF_F_LTEB_USER_STATE","SEL_ALL_USERSTATE",param,Route.CONN_CRM_CEN);
		
	}
	
	public static IData queryUserIMEI(IData param) throws Exception{
		
		IDataset qryInfo =  Dao.qryByCode("TF_F_USER_RES","SEL_BY_USERID_TYPE_RESALE",param);
		return (qryInfo == null || qryInfo.size() <=0) ? new DataMap():qryInfo.getData(0);
	}
	
}