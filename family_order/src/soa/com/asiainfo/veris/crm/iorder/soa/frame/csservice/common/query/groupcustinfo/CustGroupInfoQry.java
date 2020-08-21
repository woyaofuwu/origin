package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.groupcustinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CustGroupInfoQry extends CSBizBean
{
	  /**
		 * 根据Cust_id查询集团客户资料（包括tf_f_customer信息）
		 * used
		 * @param data
		 * @param pagination
		 * @return
		 * @throws Exception
		 */
		public  static IDataset qryGroupByCustID(IData data) throws Exception
		{
			SQLParser parser = new SQLParser(data);
			parser.addSQL(" select cg.Group_Valid_Score,cg.GROUP_SUM_SCORE,cg.CUST_ID,cg.GROUP_ID,cg.CUST_NAME,cg.GROUP_TYPE,cg.CLASS_ID,cg.CLASS_ID2, ");

			parser.addSQL(" cg.LAST_CLASS_ID,cg.CLASS_CHANGE_DATE,cg.CUST_CLASS_TYPE,cg.GROUP_ATTR,cg.GROUP_STATUS,cg.IN_DATE,cg.IN_STAFF_ID,cg.IN_DEPART_ID,  ");  
			parser.addSQL(" cg.GROUP_ADDR,cg.EPARCHY_CODE,cg.CITY_CODE,cg.SUPER_GROUP_ID,cg.SUPER_GROUP_NAME,cg.MP_GROUP_CUST_CODE, ");
			parser.addSQL(" cg.UNIFY_PAY_CODE,cg.ORG_STRUCT_CODE,cg.CUST_MANAGER_ID,cg.CUST_MANAGER_APPR,cg.ASSIGN_DATE, ");
			parser.addSQL(" cg.ASSIGN_STAFF_ID,cg.CALLING_TYPE_CODE,cg.SUB_CALLING_TYPE_CODE,cg.CALLING_AREA_CODE, ");
			parser.addSQL(" cg.CALLING_TYPE_CODE AS HY_GRADE,cg.SUB_CALLING_TYPE_CODE AS HY_SUPER,SUBSTR(cg.CALL_TYPE,0,3) AS HY_MIDDEL, SUBSTR(cg.CALL_TYPE,0,4) AS HY_SUB, ");
			parser.addSQL(" cg.CALL_TYPE,cg.ACCEPT_CHANNEL,cg.AGREEMENT,cg.BUSI_TYPE,cg.GROUP_CONTACT_PHONE,cg.ENTERPRISE_TYPE_CODE, ");
			parser.addSQL(" cg.ENTERPRISE_SIZE_CODE,cg.ENTERPRISE_SCOPE,cg.JURISTIC_TYPE_CODE,cg.JURISTIC_CUST_ID,cg.JURISTIC_NAME, ");
			parser.addSQL(" cg.BUSI_LICENCE_TYPE,cg.BUSI_LICENCE_NO,cg.BUSI_LICENCE_VALID_DATE,cg.GROUP_MEMO,cg.BANK_ACCT, ");
			parser.addSQL(" cg.BANK_NAME,cg.REG_MONEY,cg.REG_DATE,cg.CUST_AIM,cg.SCOPE,cg.MAIN_BUSI,cg.MAIN_TRADE,cg.EMP_LSAVE, ");
			parser.addSQL(" cg.LATENCY_FEE_SUM,cg.YEAR_GAIN,cg.TURNOVER,cg.CONSUME,cg.COMM_BUDGET,cg.GTEL_BUDGET,cg.LTEL_BUDGET, ");
			parser.addSQL(" cg.GROUP_ADVERSARY,cg.VPMN_GROUP_ID,cg.VPMN_NUM,cg.USER_NUM,cg.EMP_NUM_LOCAL,cg.EMP_NUM_CHINA,cg.EMP_NUM_ALL, ");
			parser.addSQL(" cg.TELECOM_NUM_GH,cg.TELECOM_NUM_XLT,cg.MOBILE_NUM_CHINAGO,cg.MOBILE_NUM_GLOBAL,cg.MOBILE_NUM_MZONE, ");
			parser.addSQL(" cg.MOBILE_NUM_LOCAL,cg.UNICOM_NUM_G,cg.UNICOM_NUM_C,cg.UNICOM_NUM_GC,cg.PRODUCT_NUM_LOCAL, ");
			parser.addSQL(" cg.PRODUCT_NUM_OTHER,cg.PRODUCT_NUM_USE,cg.EMPLOYEE_ARPU,cg.NETRENT_PAYOUT,cg.MOBILE_PAYOUT,cg.UNICOM_PAYOUT, ");
			parser.addSQL(" cg.TELECOM_PAYOUT_XLT,cg.GROUP_PAY_MODE,cg.PAYFOR_WAY_CODE,cg.CALLING_POLICY_FORCE,cg.WRITEFEE_COUNT,cg.WRITEFEE_SUM,cg.USER_NUM_FULLFREE, ");
			parser.addSQL(" cg.USER_NUM_WRITEOFF,cg.BOSS_FEE_SUM,cg.DOYEN_STAFF_ID,cg.NEWTRADE_COMMENT,cg.LIKE_MOBILE_TRADE,cg.LIKE_DISCNT_MODE, ");
			parser.addSQL(" cg.FINANCE_EARNING,cg.SUBCLASS_ID,cg.WEBSITE,cg.FAX_NBR,cg.EMAIL,cg.POST_CODE,cg.AUDIT_STATE,cg.AUDIT_DATE,cg.AUDIT_STAFF_ID, ");
			parser.addSQL(" cg.AUDIT_NOTE,cg.REMOVE_FLAG,cg.REMOVE_METHOD,cg.REMOVE_REASON_CODE,cg.REMOVE_TAG,cg.REMOVE_DATE,cg.REMOVE_STAFF_ID, ");
			parser.addSQL(" cg.REMOVE_CHANGE,cg.UPDATE_TIME,cg.UPDATE_STAFF_ID,cg.UPDATE_DEPART_ID,cg.REMARK,cg.RSRV_NUM1,cg.RSRV_NUM2,cg.RSRV_NUM3, ");
			parser.addSQL(" cg.RSRV_STR1 RSRV_STR1_BM,cg.RSRV_STR2,cg.RSRV_STR3,cg.RSRV_STR4,cg.RSRV_STR5,cg.RSRV_STR6,cg.RSRV_STR7,cg.RSRV_STR8,cg.RSRV_DATE1, ");
			parser.addSQL(" cg.RSRV_DATE2,cg.RSRV_DATE3,cg.RSRV_TAG1,cg.RSRV_TAG2,cg.RSRV_TAG3,cg.PROVINCE_CODE,cg.CUST_SERV_NBR, ");
			parser.addSQL(" cg.PNATIONAL_GROUP_ID,cg.PNATIONAL_GROUP_NAME,cg.GROUP_MGR_SN,cg.GROUP_MGR_USER_ID,cg.GROUP_MGR_CUST_NAME, ");//jiudian 添加这一行
			//7省跨省数据专线业务优化需求新增客户服务等级字段
			parser.addSQL(" cg.SERV_LEVEL");
			parser.addSQL(" from  tf_f_cust_group cg where 1=1 ");
			parser.addSQL(" and cg.REMOVE_TAG = '0'  ");
			parser.addSQL(" and cg.CUST_ID = :CUST_ID ");

			return Dao.qryByParse(parser, Route.CONN_CRM_CG);
		}
		
		
		/**
	     * 通过custid查询一年合同信息
	     *
	     * @param custId
	     * @return
	     * @throws Exception
	     */
	    public static IDataset qryValidContractByCustId(String custId) throws Exception
	    {
	        IData param = new DataMap();
	        param.put("CUST_ID", custId);

	        return Dao.qryByCode("TF_F_CUST_CONTRACT", "SEL_BY_VALIDCONTRACTID", param);
	    }
	    
	    public static IDataset qryGroupInfoByCustId(String userId) throws Exception
	    {
	        IData param = new DataMap();
	        param.put("USER_ID", userId);

	        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_GROUP_INFO_BY_CUSTID", param);
	    }

		public static IDataset qryImportCustomerByNum(String serialNumber) throws Exception 
		{
			IData param = new DataMap();
	        param.put("SERIAL_NUMBER", serialNumber);

	        return Dao.qryByCode("TF_F_IMPORT_CUSTOMER", "SEL_CUST_BY_SERIAL_NUMBER", param);
		}

}
