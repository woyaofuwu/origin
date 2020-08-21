
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;

public final class BreQry
{
	public static IDataset getGropMemberInfoByUserId(String userId) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select D.USER_ID, D.CUST_ID, D.MEMBER_KIND from TF_F_CUST_GROUPMEMBER D ");
		parser.addSQL(" where PARTITION_ID = mod(TO_NUMBER(:USER_ID), 10000) and USER_ID = :USER_ID and REMOVE_TAG = '0' ");

		return Dao.qryByParse(parser);

	}

	public static IDataset getOtherNetSnTroopMember(String phoneCodeB) throws Exception
	{
		IData param = new DataMap();

		param.put("PHONE_CODE_B", phoneCodeB);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select /*+index(a IDX_TF_F_USER_SALE_ACTIVE_PROD)*/ COUNT(1) CNT from tf_f_user_sale_active a,td_s_commpara b ");
		parser.addSQL(" WHERE a.product_id=b.param_code AND b.subsys_code='CSM' AND b.param_attr=83 AND a.process_tag='0' AND a.rsrv_str2=:PHONE_CODE_B AND SYSDATE BETWEEN b.start_date AND b.end_Date ");

		return Dao.qryByParse(parser);

	}

	/**
	 * TODO SQL待优化
	 * 
	 * @param userId
	 * @param paramProductId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getPayMainActives(String userId, String paramProductId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_PRODUCT_ID", paramProductId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select c.user_id,c.product_id,c.package_id  ");
		parser.addSQL(" from TF_F_USER_SALE_ACTIVE C, ");
		parser.addSQL(" (select B.USER_ID_B, B.SERIAL_NUMBER_B ");
		parser.addSQL(" from TF_F_RELATION_UU A, TF_F_RELATION_UU B ");
		parser.addSQL("  where A.USER_ID_A = B.USER_ID_A ");
		parser.addSQL(" and A.RELATION_TYPE_CODE = '56'  ");
		parser.addSQL(" and A.USER_ID_B = :USER_ID ");
		parser.addSQL(" and A.ROLE_CODE_B = '2' and B.ROLE_CODE_B = '1' ");
		parser.addSQL(" and A.END_DATE > sysdate and B.END_DATE > sysdate) D ");
		parser.addSQL(" where C.USER_ID = D.USER_ID_B  and C.PRODUCT_ID = :PARAM_PRODUCT_ID and C.END_DATE > sysdate");
		return Dao.qryByParse(parser);
	}

	/**
	 * TODO SQL待优化
	 * 
	 * @param userId
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getPayMemberActives(String userId, String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select c.user_id,c.product_id,c.package_id  ");
		parser.addSQL(" from TF_F_USER_SALE_ACTIVE C, ");
		parser.addSQL(" (select B.USER_ID_B, B.SERIAL_NUMBER_B ");
		parser.addSQL(" from TF_F_RELATION_UU A, TF_F_RELATION_UU B ");
		parser.addSQL("  where A.USER_ID_A = B.USER_ID_A ");
		parser.addSQL(" and A.RELATION_TYPE_CODE = '56'  ");
		parser.addSQL(" and A.USER_ID_B = :USER_ID ");
		parser.addSQL(" and A.ROLE_CODE_B = '2' and B.ROLE_CODE_B = '2' ");
		parser.addSQL(" and A.END_DATE > sysdate and B.END_DATE > sysdate) D ");
		parser.addSQL(" where C.USER_ID = D.USER_ID_B  and C.PRODUCT_ID = :PRODUCT_ID and C.END_DATE > sysdate");
		return Dao.qryByParse(parser);
	}

	/**
	 * TODO SQL待优化
	 * 
	 * @param userId
	 * @param paramProductId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getPayMemberBActives(String userId, String paramProductId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PARAM_PRODUCT_ID", paramProductId);

		SQLParser parser = new SQLParser(param);

		parser.addSQL("SELECT C.USER_ID, C.PRODUCT_ID, C.PACKAGE_ID ");
		parser.addSQL("FROM TF_F_USER_SALE_ACTIVE C, ");
		parser.addSQL("(SELECT B.USER_ID_B, B.SERIAL_NUMBER_B ");
		parser.addSQL("FROM TF_F_RELATION_UU A, TF_F_RELATION_UU B ");
		parser.addSQL("WHERE A.USER_ID_A = B.USER_ID_A ");
		parser.addSQL("AND A.RELATION_TYPE_CODE = '56' ");
		parser.addSQL("AND A.USER_ID_B = :USER_ID ");
		parser.addSQL("AND A.PARTITION_ID = MOD(:USER_ID, 10000) ");
		parser.addSQL("AND A.ROLE_CODE_B = '1' ");
		parser.addSQL("AND B.ROLE_CODE_B = '2' ");
		parser.addSQL("AND A.END_DATE > SYSDATE ");
		parser.addSQL("AND B.END_DATE > SYSDATE) D ");
		parser.addSQL("WHERE C.USER_ID = D.USER_ID_B ");
		parser.addSQL("AND C.PARTITION_ID = MOD(D.USER_ID_B, 10000) ");
		parser.addSQL("AND C.PRODUCT_ID = :PARAM_PRODUCT_ID ");
		parser.addSQL("AND C.END_DATE > SYSDATE ");

		return Dao.qryByParse(parser);
	}

	public static IDataset getSimcardUseDataset(String resCode) throws Exception
	{
		IData param = new DataMap();
		param.put("RES_CODE", resCode);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select A.Card_Mode_Type from tf_r_simcard_idle a  ");
		parser.addSQL(" where a.SIM_CARD_NO = :RES_CODE ");
		parser.addSQL(" UNION ALL ");
		parser.addSQL(" SELECT B.card_mode_type fROM TF_R_SIMCARD_USE B ");
		parser.addSQL("  WHERE B.SIM_CARD_NO = :RES_CODE ");
		return Dao.qryByParse(parser);
	}

	public static IDataset getTerminalOrder(String serialNumber, String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("PRODUCT_ID", productId);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select A.ORDER_ID, A.SERIAL_NUMBER ");
		parser.addSQL(" from TF_F_TERMINALORDER A ");
		parser.addSQL(" where A.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and A.PRODUCT_ID = :PRODUCT_ID ");
		parser.addSQL("  and A.END_TIME > sysdate ");
		parser.addSQL(" and A.RSRV_STR2 = '0'  ");
		return Dao.qryByParse(parser);
	}

	public static IDataset getTroopMemberByTroopIdSn(String troopId, String phoneCodeB) throws Exception
	{
		IData param = new DataMap();
		param.put("TROOP_ID", troopId);
		param.put("PHONE_CODE_B", phoneCodeB);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select A.USER_ID, A.CUST_ID, A.CUST_CODE  ");
		parser.addSQL(" from TF_SM_TROOP_MEMBER A ");
		parser.addSQL(" where A.TROOP_ID = :TROOP_ID ");
		parser.addSQL(" and A.MEMBER_STATUS = '1' ");
		parser.addSQL(" and A.CUST_CODE = :PHONE_CODE_B ");
		return Dao.qryByParse(parser);
	}

	public static IDataset getTroopMemberByTroopIdUserId(String troopId, String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("TROOP_ID", troopId);
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select A.USER_ID, A.CUST_ID, A.CUST_CODE  ");
		parser.addSQL(" from TF_SM_TROOP_MEMBER A ");
		parser.addSQL(" where A.TROOP_ID = :TROOP_ID ");
		parser.addSQL(" and A.MEMBER_STATUS = '1' ");
		parser.addSQL(" and A.user_Id = :USER_ID ");
		return Dao.qryByParse(parser);
	}

	public static IDataset getUserTransPhoneInfos(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select COUNT(1) CNT, max(A.PHONE_CODE_B) PHONE_CODE_B");
		parser.addSQL(" from TF_F_TRANS_PHONE A ");
		parser.addSQL(" where A.PHONE_CODE_A = :SERIAL_NUMBER ");
		parser.addSQL(" and A.END_DATE > sysdate ");
		return Dao.qryByParse(parser);
	}

	public static boolean hasBizeTypeCodePlatSvc(IDataset userPlatSvcDataset, String bizeTypeCode) throws Exception
	{
		if (IDataUtil.isEmpty(userPlatSvcDataset))
		{
			return false;
		}

		boolean hasPlatSvc = false;

		for (int j = 0, s = userPlatSvcDataset.size(); j < s; j++)
		{
			IData userPlatSvc = userPlatSvcDataset.getData(j);
			String serviceId = userPlatSvc.getString("SERVICE_ID");
            IDataset svcData = UpcCall.qrySpServiceSpInfo(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC);
            if(IDataUtil.isNotEmpty(svcData))
            {
				String _bizTypeCode = svcData.getData(0).getString("BIZ_TYPE_CODE");
				String bizSateCode = userPlatSvc.getString("BIZ_STATE_CODE");
				if (bizeTypeCode.equals(_bizTypeCode) && "A".equals(bizSateCode))
				{
					hasPlatSvc = true;
					break;
				}
            }
		}

		return hasPlatSvc;
	}

	public static boolean qryUserProductTrade(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset userInfo = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_PRODUCT_TRADE", param);

		return IDataUtil.isEmpty(userInfo) ? false : true;
	}

	public static IDataset qryUserProductInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset userInfo = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_PRODUCT_INFO", param);

		return userInfo;
	}

	public static IDataset qryUserDiscntInfo(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		IDataset userInfo = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_DISCNT_INFO", param);

		return userInfo;
	}

	public static IDataset qryUserSaleActiveInfo(String productID,String userID) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productID);
		param.put("USER_ID", userID);

		IDataset userInfo = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USERID_PRODUCTID2", param);

		return userInfo;
	}

	public static boolean qryUserNextMianProductInfo(String productID,String userID) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productID);
		param.put("USER_ID", userID);

		IDataset userInfo = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_USERID_NEXT_PRODUCTID", param);

		return IDataUtil.isEmpty(userInfo) ? false : true;
	}

	public static boolean qryKDUserForTrade(String serialNumber,String tradeTypeCode, String acceptDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);
		param.put("ACCEPT_DATE",acceptDate);

//		SQLParser parser = new SQLParser(param);
//		parser.addSQL(" SELECT a.user_id FROM tf_bh_trade a WHERE  a.serial_number = :SERIAL_NUMBER " +
//				"AND a.trade_type_code= :TRADE_TYPE_CODE AND a.accept_date >= to_date( :ACCEPT_DATE,'yyyy-MM-dd HH24:mi:ss') ");
//		IDataset userInfo = Dao.qryByParse(parser);
		IDataset userInfo = Dao.qryByCode("TF_BH_TRADE", "SEL_KD_FOR_TRADE", param);


		return IDataUtil.isEmpty(userInfo) ? false : true;
	}



	public static boolean qryKDTradeforThreeMonth(String serialNumber,String tradeTypeCode) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);
		param.put("TRADE_TYPE_CODE", tradeTypeCode);

//		SQLParser parser = new SQLParser(param);
//		parser.addSQL(" SELECT a.user_id FROM tf_bh_trade a WHERE  a.serial_number = :SERIAL_NUMBER AND a.trade_type_code=:TRADE_TYPE_CODE AND add_months(trunc(sysdate,'MM'),-2) <= a.accept_date ");
//		IDataset userInfo = Dao.qryByParse(parser);
		IDataset userInfo = Dao.qryByCode("TF_BH_TRADE", "SEL_KD_TRADE_FOR_3MONTH", param);

		return IDataUtil.isEmpty(userInfo) ? false : true;
	}

	public static boolean queryRemoval(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("KD_SERIAL_NUMBER", "KD_" + serialNumber);

		IDataset userInfo = Dao.qryByCode("TF_F_USER_GPON_DESTROY", "SEL_DESTORY_ORDER_BY_KD_SERIAL_NUMBER", param);

		return IDataUtil.isEmpty(userInfo) ? false : true;
	}

	public static boolean isBookSaleActive(String serailNumber, String productId, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serailNumber);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select A.USER_ID, A.PRODUCT_ID, A.PACKAGE_ID from TF_F_USER_SALEACTIVE_BOOK A  ");
		parser.addSQL(" where A.SERIAL_NUMBER = :SERIAL_NUMBER and A.PRODUCT_ID_B = :PRODUCT_ID ");
		parser.addSQL(" and A.PACKAGE_ID_B = :PACKAGE_ID and A.DEAL_STATE_CODE = '0' ");
		parser.addSQL(" and A.PROCESS_TAG = '0' and A.END_DATE > sysdate ");

		IDataset bookSaleActiveSet = Dao.qryByParse(parser);

		return IDataUtil.isEmpty(bookSaleActiveSet) ? false : true;
	}

	public static boolean isFinishUserSvc(String serialNumber, String rsrvStr24) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);
		param.put("SERVICE_ID", rsrvStr24);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from TF_F_USER A, TF_F_USER_SVC B  where A.USER_ID = B.USER_ID  ");
		parser.addSQL(" and A.PARTITION_ID = B.PARTITION_ID and INSTR(:SERVICE_ID, '|' || B.SERVICE_ID || '|') > 0  ");
		parser.addSQL(" and A.REMOVE_TAG = '0' and B.END_DATE > sysdate and A.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and B.START_DATE = (select max(B1.START_DATE)  from TF_F_USER A1, TF_F_USER_SVC B1  ");
		parser.addSQL(" where A1.USER_ID = B1.USER_ID and A1.PARTITION_ID = B1.PARTITION_ID and A1.REMOVE_TAG = '0'  ");
		parser.addSQL(" and B1.SERVICE_ID in (SELECT A.PARAM_CODE FROM td_s_commpara a WHERE a.param_attr = '560' AND a.end_date > SYSDATE)  ");
		parser.addSQL(" and B1.END_DATE > sysdate and A1.SERIAL_NUMBER = :SERIAL_NUMBER)  ");
		IDataset serviceDataset = Dao.qryByParse(parser);

		return Integer.parseInt(serviceDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}

	public static boolean isNoFinishTrade(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select a.user_id,a.serial_number from TF_B_TRADE A where A.SERIAL_NUMBER = :SERIAL_NUMBER and A.TRADE_TYPE_CODE in (600, 612, 613, 611) and A.CANCEL_TAG = '0'  ");
		IDataset noFinishTrade = Dao.qryByParse(parser, Route.getJourDb());

		return IDataUtil.isEmpty(noFinishTrade) ? false : true;
	}
	
	public static IDataset noFinishTradeInfos(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select a.USER_ID,a.SERIAL_NUMBER,a.TRADE_ID from TF_B_TRADE A where A.SERIAL_NUMBER = :SERIAL_NUMBER and A.TRADE_TYPE_CODE in (600, 612, 613, 611) and A.CANCEL_TAG = '0' order by accept_date desc ");
		IDataset noFinishTrade = Dao.qryByParse(parser,Route.getJourDb(BizRoute.getTradeEparchyCode()));

		return noFinishTrade;
	}

	public static boolean isNoFinishUserSvc(String serialNumber, String rsrvStr24) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);
		param.put("SERVICE_ID", rsrvStr24);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from TF_B_TRADE_SVC B where INSTR(:SERVICE_ID, '|' || B.SERVICE_ID || '|') > 0  ");
		parser.addSQL(" and B.END_DATE > sysdate and B.MODIFY_TAG = '0'  ");
		parser.addSQL(" and exists (select 1 from TF_B_TRADE A where A.TRADE_ID = B.TRADE_ID ");
		parser.addSQL(" and A.ACCEPT_MONTH = B.ACCEPT_MONTH and A.USER_ID = B.USER_ID  ");
		parser.addSQL(" and A.TRADE_TYPE_CODE in (600, 612, 613, 611) and A.CANCEL_TAG = '0' and A.SERIAL_NUMBER = :SERIAL_NUMBER)  ");
		IDataset serviceDataset = Dao.qryByParse(parser, Route.getJourDb());

		return Integer.parseInt(serviceDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	/**
	 * 判断用户当前选择的服务是否符合营销活动规则
	 * @param wideUserSelectedServiceIds
	 * @param rsrvStr24
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public static boolean isNoOrderUserSvc(String wideUserSelectedServiceIds, String rsrvStr24) throws Exception
    {
	    boolean returnResult = false;
	    
	    String [] wideUserSelectedServiceIdArray = wideUserSelectedServiceIds.split("\\|");
	    String [] rsrvStr24Array = rsrvStr24.split("\\|");
	    
	    for (int i = 0; i < wideUserSelectedServiceIdArray.length; i++ )
	    {
	        //如果已经匹配到对应的服务ID，则不需要继续循环判断
	        if (returnResult)
	        {
	            break;
	        }
	        
	        for (int j = 0; j < rsrvStr24Array.length; j++)
	        {
	            if (StringUtils.equals(wideUserSelectedServiceIdArray[i], rsrvStr24Array[j]) )
	            {
	                returnResult = true;
	                break;
	            }
	        }
	    }
	    
        return returnResult;
    }

	public static boolean isOpenCycle(String userId, String endDate) throws Exception
	{
		IData param = new DataMap();

		param.put("USER_ID", userId);
		param.put("END_DATE", endDate);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT cycle_id,use_tag FROM td_b_cycle  ");
		parser.addSQL(" WHERE cyc_start_time <= to_date(:END_DATE, 'yyyy-mm-dd') ");
		parser.addSQL(" AND to_date(:END_DATE, 'yyyy-mm-dd')  <= cyc_end_time order by acct_day asc");

		IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);

		if (IDataUtil.isNotEmpty(dataset))
		{
			String useTag = dataset.getData(0).getString("USE_TAG", "").trim();
			if ("0".equals(useTag))
				return false;
		}

		return true;
	}

	public static boolean isTermianlOrderActive(String userId, String tradeId, String productId, String packageId) throws Exception
	{
		IData param = new DataMap();

		param.put("TRADE_ID", tradeId);
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("PACKAGE_ID", packageId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select D.TRADE_ID,D.USER_ID,D.PRODUCT_ID,D.PACKAGE_ID from tf_f_terminalorder d where d.trade_id = :TRADE_ID  ");
		parser.addSQL(" AND D.USER_ID = :USER_ID AND D.PRODUCT_ID = :PRODUCT_ID AND D.PACKAGE_ID=:PACKAGE_ID AND D.RSRV_STR2='1' ");

		IDataset terminalDataset = Dao.qryByParse(parser);
		return IDataUtil.isEmpty(terminalDataset) ? false : true;
	}

	public static boolean isWideNetUser(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", "KD_" + serialNumber);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select  A.USER_ID,A.SERIAL_NUMBER  from TF_F_USER A where A.SERIAL_NUMBER = :SERIAL_NUMBER and A.REMOVE_TAG = '0' ");
		IDataset userInfo = Dao.qryByParse(parser);

		return IDataUtil.isEmpty(userInfo) ? false : true;
	}
	
	public static boolean isUserDiscntByParamArrt1549(String userId, String packageId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PACKAGE_ID", packageId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT TD_S_COMMPARA A, TF_F_USER_DISCNT B where A.PARAM_ATTR = '1549'  ");
		parser.addSQL(" AND A.PARA_CODE1 = :PACKAGE_ID ");
		parser.addSQL(" AND B.DISCNT_CODE = A.PARAM_CODE ");
		parser.addSQL(" AND B.USER_ID = :USER_ID ");
		parser.addSQL(" AND B.PARTITION_ID = MOD(:USER_ID, 10000) ");
		parser.addSQL(" AND B.END_DATE > TRUNC(LAST_DAY(SYSDATE) + 1) ");
		
		IDataset discntDataset = Dao.qryByParse(parser);

		return Integer.parseInt(discntDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static boolean isGroupMember(String userId, String groupId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("GROUP_ID", groupId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from tf_f_cust_groupmember a,tf_f_cust_group b  ");
		parser.addSQL(" where a.user_id = :USER_ID ");
		parser.addSQL(" and a.remove_tag = '0' ");
		parser.addSQL(" and a.group_id = b.group_id ");
		parser.addSQL(" and b.remove_tag = '0' ");
		parser.addSQL(" and b.group_id = :GROUP_ID ");
		
		IDataset groupMember = Dao.qryByParse(parser);

		return Integer.parseInt(groupMember.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static String getAcctIdFromPayRelation(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select acct_id from tf_a_payrelation a ");
		parser.addSQL(" where a.user_id = :USER_ID ");
		parser.addSQL(" and a.payitem_code=-1 ");
		parser.addSQL(" and a.default_tag='1' ");
		parser.addSQL(" and a.act_tag='1' ");
		parser.addSQL(" and to_char(sysdate,'yyyymmdd') between start_cycle_id and end_cycle_id ");
		
		IDataset payrelation = Dao.qryByParse(parser);

		if(IDataUtil.isNotEmpty(payrelation))
		{
			return payrelation.getData(0).getString("ACCT_ID");
		}else
		{
			return null;
		}
	}
	
	public static String getPayModeCode(String acctId) throws Exception
	{
		IData param = new DataMap();
		param.put("ACCT_ID", acctId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select pay_mode_code from tf_f_account where acct_id = :ACCT_ID");
		
		IDataset payrelation = Dao.qryByParse(parser);

		if(IDataUtil.isNotEmpty(payrelation))
		{
			return payrelation.getData(0).getString("ACCT_ID");
		}else
		{
			return null;
		}
	}
	
	public static boolean isSpecialePay(String userId, String groupId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from tf_f_user_specialepay a  ");
		parser.addSQL(" where a.user_id = :USER_ID ");
		if("8981100167".equals(groupId))
		{
			parser.addSQL(" and user_id_a=1104030901883445 ");
			parser.addSQL(" and ((limit_type='1' and limit >=5000) or limit_type='0') ");
		}
		parser.addSQL(" and to_char(sysdate,'yyyymmdd') between  start_cycle_id and end_cycle_id ");
		
		IDataset groupMember = Dao.qryByParse(parser);

		return Integer.parseInt(groupMember.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static boolean is898GroupMember(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from tf_f_cust_groupmember a  ");
		parser.addSQL(" where a.user_id = :USER_ID and remove_tag='0' ");
		parser.addSQL(" and group_id in (select para_code1 from td_s_commpara where param_attr=1907 and subsys_code='CSM' and param_code='0' and end_date > sysdate) ");
		
		IDataset groupMember = Dao.qryByParse(parser);

		return Integer.parseInt(groupMember.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static boolean isVpmnGroupMember(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from tf_f_relation_uu   ");
		parser.addSQL(" where user_id_b=to_number(:USER_ID) and relation_type_code='20' ");
		parser.addSQL(" and end_date > sysdate and user_id_a in ");
		parser.addSQL(" (select user_id from tf_f_user where serial_number in (select para_code1 from td_s_commpara where param_attr=1907 and subsys_code='CSM' and param_code='1' and end_date > sysdate) and remove_tag='0') ");
		
		IDataset groupMember = Dao.qryByParse(parser);

		return Integer.parseInt(groupMember.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static IDataset getValidSaleActiveRsrvDate2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from tf_f_user_sale_active   ");
		parser.addSQL(" where  partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    process_tag = '0' and    nvl(rsrv_date2,end_date) > sysdate ");
		
		return Dao.qryByParse(parser);
	}
	
	public static IDataset getValidSaleActive(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from tf_f_user_sale_active   ");
		parser.addSQL(" where  partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    process_tag = '0' and    end_date > sysdate ");
		
		return Dao.qryByParse(parser);
	}
	
	public static IDataset getSaleNewRsrvDate2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from tf_f_user_sale_new   ");
		parser.addSQL(" where  partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    process_tag = '0' and    nvl(rsrv_date2,end_date) > sysdate ");
		
		return Dao.qryByParse(parser);
	}
	
	public static IDataset getSaleNew(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from tf_f_user_sale_new   ");
		parser.addSQL(" where  partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    process_tag = '0' and    end_date > sysdate ");
		
		return Dao.qryByParse(parser);
	}
	
	public static IDataset getSaleActiveBookRsrvDate2(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from tf_f_user_saleactive_book   ");
		parser.addSQL(" where  partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    process_tag = '0' and    nvl(rsrv_date2,end_date) > sysdate ");
		
		return Dao.qryByParse(parser);
	}
	
	public static int getMaxMemberNumParamAttr1526(String productId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select max(nvl(c.para_code2,'0')) PARA_CODE2 from   td_s_commpara c  ");
		parser.addSQL(" where  c.subsys_code = 'CSM' and    c.param_attr = 1526 and    c.para_code1 = :PRODUCT_ID ");
		parser.addSQL(" and    sysdate between c.start_date and c.end_date and    (c.eparchy_code = :EPARCHY_CODE or c.eparchy_code = 'ZZZZ') ");
		
		IDataset num = Dao.qryByParse(parser);
		return num.getData(0).getInt("PARA_CODE2");
	}
	
	public static IDataset getUserPersonDiscnt(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from   tf_f_user_discnt ");
		parser.addSQL(" partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    end_date > sysdate ");
		parser.addSQL(" and    discnt_code in (1310, 1311, 1316, 1317, 1318, 1319, 1339, 2115, 2116, 1139) ");
		
		return Dao.qryByParse(parser);
	}
	
	public static IDataset getUserGroupDiscnt(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from   tf_f_user_discnt ");
		parser.addSQL(" WHERE partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    end_date > sysdate ");
		parser.addSQL(" and    discnt_code in (1285, 1286, 1391, 662, 493) ");
		
		return Dao.qryByParse(parser);
	}
	
	public static boolean existsUserSvc(String userId, String serviceId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("SERVICE_ID", serviceId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from   tf_f_user_svc ");
		parser.addSQL(" WHERE partition_id = mod(to_number(:USER_ID), 10000) and    user_id = :USER_ID ");
		parser.addSQL(" and    end_date > sysdate ");
		parser.addSQL(" and    service_id = :SERVICE_ID ");
		
		IDataset serviceDataset = Dao.qryByParse(parser);

		return Integer.parseInt(serviceDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static String get898GroupCustId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select /*+ first_rows(1)*/ nvl(max(cust_id), -1) CUST_ID from   tf_f_cust_groupmember ");
		parser.addSQL(" where  user_id = :USER_ID and    remove_tag = '0' and    group_id like '898%' and    rownum < 2 ");
		
		IDataset groupMember = Dao.qryByParse(parser);
		
		return groupMember.getData(0).getString("CUST_ID");
	}
	
	public static boolean is8000Or7400UserProduct(String custId) throws Exception
	{
		IData param = new DataMap();
		param.put("CUST_ID", custId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from   tf_f_user where  cust_id = :CUST_ID and remove_tag = '0' and    product_id in (8000, 7400) ");
		
		IDataset serviceDataset = Dao.qryByParse(parser);

		return Integer.parseInt(serviceDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static boolean isNetBookSaleActive(String userId, String productId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from   tf_f_terminalorder a,tf_f_user b where  b.user_id = :USER_ID ");
		parser.addSQL(" and    b.remove_tag = '0' ");
		parser.addSQL(" and    a.serial_number = b.serial_number ");
		parser.addSQL(" and    a.product_id = :PRODUCT_ID ");
		parser.addSQL(" and    a.end_time>sysdate ");
		parser.addSQL(" and    a.rsrv_str2 = '0' ");
		
		IDataset serviceDataset = Dao.qryByParse(parser);

		return Integer.parseInt(serviceDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static IData getConfigParamAttr968(String productId, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(1) CNT,max(nvl(c.param_code,'0')) PARAM_CODE,max(nvl(c.para_code6,'0')) PARA_CODE6 ");
		parser.addSQL(" from   td_s_commpara c ");
		parser.addSQL(" where  c.subsys_code = 'CSM' ");
		parser.addSQL(" and    c.param_attr = 968 ");
		parser.addSQL(" and    c.para_code1 = :PRODUCT_ID ");
		parser.addSQL(" and    sysdate between c.start_date and c.end_date ");
		parser.addSQL(" and    (c.eparchy_code = :EPARCHY_CODE or c.eparchy_code = 'ZZZZ')");
		
		IDataset configDataset = Dao.qryByParse(parser);
		return configDataset.getData(0);
	}
	
	public static IData qrySaleActiveRsrvDate2ParamAttr155And968(String userId, String productId, String eparchyCode, String paramCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("PARAM_CODE", paramCode);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT /*+ ordered use_nl(s,a)*/ count(*) CNT,max(months_between(nvl(s.rsrv_date2,s.end_date),SYSDATE)) CNT2,max(nvl(a.para_code5,0)) CNT3 ");
		parser.addSQL(" FROM   (select * ");
		parser.addSQL(" from   (select * ");
		parser.addSQL(" from   tf_f_user_sale_active ");
		parser.addSQL(" where  partition_id = mod(to_number(:USER_ID), 10000) ");
		parser.addSQL(" and    user_id = :USER_ID ");
		parser.addSQL(" and    process_tag = '0' ");
		parser.addSQL(" and    ((:PARAM_CODE='0' and not exists( ");//话费返还/约定消费类活动
		parser.addSQL(" select 1 from td_s_commpara c1 where c1.param_attr=155 and c1. ");
		parser.addSQL(" subsys_code='CSM' ");
		parser.addSQL(" and c1.para_code1=product_id ");
		parser.addSQL(" and sysdate BETWEEN c1.start_date AND c1.end_date)) ");
		parser.addSQL(" or ");
		parser.addSQL(" (:PARAM_CODE='1' and exists( ");//约定在网类活动
		parser.addSQL(" select 1 from td_s_commpara c1 where c1.param_attr=155 and c1. ");
		parser.addSQL(" subsys_code='CSM' ");
		parser.addSQL(" and c1.para_code1=product_id ");
		parser.addSQL(" and sysdate BETWEEN c1.start_date AND c1.end_date))) order  by nvl(rsrv_date2,end_date) desc) ");
		parser.addSQL(" where  rownum < 2) s, td_s_commpara a ");
		parser.addSQL(" WHERE  s.user_id = to_number(:USER_ID) ");
		parser.addSQL(" AND    s.partition_id = mod(to_number(:USER_ID), 10000) ");
		parser.addSQL(" AND    nvl(s.rsrv_date2,s.end_date) > SYSDATE ");
		parser.addSQL(" AND    s.product_id = nvl(to_number(a.para_code2), s.product_id) ");
		parser.addSQL(" AND    a.param_attr = 968 ");
		parser.addSQL(" AND    a.subsys_code = 'CSM' ");
		parser.addSQL(" AND    sysdate BETWEEN a.start_date AND a.end_date ");
		parser.addSQL(" AND    (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ') ");
		parser.addSQL(" AND    (a.para_code1 = :PRODUCT_ID) ");
		
		IDataset configDataset = Dao.qryByParse(parser);
		return configDataset.getData(0);
	}
	
	public static IData qrySaleActiveEndDateParamAttr155And968(String userId, String productId, String eparchyCode, String paramCode) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("PRODUCT_ID", productId);
		param.put("EPARCHY_CODE", eparchyCode);
		param.put("PARAM_CODE", paramCode);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT /*+ ordered use_nl(s,a)*/ count(*) CNT,max(months_between(nvl(s.end_date,s.end_date),SYSDATE)) CNT2,max(nvl(a.para_code5,0)) CNT3 ");
		parser.addSQL(" FROM   (select * ");
		parser.addSQL(" from   (select * ");
		parser.addSQL(" from   tf_f_user_sale_active ");
		parser.addSQL(" where  partition_id = mod(to_number(:USER_ID), 10000) ");
		parser.addSQL(" and    user_id = :USER_ID ");
		parser.addSQL(" and    process_tag = '0' ");
		parser.addSQL(" and    ((:PARAM_CODE='0' and not exists( ");//话费返还/约定消费类活动
		parser.addSQL(" select 1 from td_s_commpara c1 where c1.param_attr=155 and c1. ");
		parser.addSQL(" subsys_code='CSM' ");
		parser.addSQL(" and c1.para_code1=product_id ");
		parser.addSQL(" and sysdate BETWEEN c1.start_date AND c1.end_date)) ");
		parser.addSQL(" or ");
		parser.addSQL(" (:PARAM_CODE='1' and exists( ");//约定在网类活动
		parser.addSQL(" select 1 from td_s_commpara c1 where c1.param_attr=155 and c1. ");
		parser.addSQL(" subsys_code='CSM' ");
		parser.addSQL(" and c1.para_code1=product_id ");
		parser.addSQL(" and sysdate BETWEEN c1.start_date AND c1.end_date))) order  by nvl(rsrv_date2,end_date) desc) ");
		parser.addSQL(" where  rownum < 2) s, td_s_commpara a ");
		parser.addSQL(" WHERE  s.user_id = to_number(:USER_ID) ");
		parser.addSQL(" AND    s.partition_id = mod(to_number(:USER_ID), 10000) ");
		parser.addSQL(" AND    nvl(s.rsrv_date2,s.end_date) > SYSDATE ");
		parser.addSQL(" AND    s.product_id = nvl(to_number(a.para_code2), s.product_id) ");
		parser.addSQL(" AND    a.param_attr = 968 ");
		parser.addSQL(" AND    a.subsys_code = 'CSM' ");
		parser.addSQL(" AND    sysdate BETWEEN a.start_date AND a.end_date ");
		parser.addSQL(" AND    (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ') ");
		parser.addSQL(" AND    (a.para_code1 = :PRODUCT_ID) ");
		
		IDataset configDataset = Dao.qryByParse(parser);
		return configDataset.getData(0);
	}
	
	public static IDataset getSaleActiveLimitParaCode1Config(String paramAttr, String paramCode, String paraCode1, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PARA_CODE1", paraCode1);
		param.put("PARAM_CODE", paramCode);
		param.put("PARAM_ATTR", paramAttr);
		param.put("EPARCHY_CODE", eparchyCode);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from   td_s_commpara c ");
		parser.addSQL(" where  c.subsys_code = 'CSM' ");
		parser.addSQL(" and    c.param_attr = :PARAM_ATTR ");
		parser.addSQL(" and    c.param_code = :PARAM_CODE ");
		parser.addSQL(" and    c.para_code1 = :PARA_CODE1 ");
		parser.addSQL(" and    sysdate between start_date and end_date ");
		parser.addSQL(" and    (c.eparchy_code = :EPARCHY_CODE or c.eparchy_code = 'ZZZZ')");
		
		IDataset configDataset = Dao.qryByParse(parser);
		
		return configDataset;
	}
	
	public static IDataset getSaleActiveLimitParamCodeConfig(String paramAttr, String paramCode, String eparchyCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PARAM_CODE", paramCode);
		param.put("PARAM_ATTR", paramAttr);
		param.put("EPARCHY_CODE", eparchyCode);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from   td_s_commpara c ");
		parser.addSQL(" where  c.subsys_code = 'CSM' ");
		parser.addSQL(" and    c.param_attr = :PARAM_ATTR ");
		parser.addSQL(" and    c.param_code = :PARAM_CODE ");
		parser.addSQL(" and    c.para_code1 = :PARA_CODE1 ");
		parser.addSQL(" and    sysdate between start_date and end_date ");
		parser.addSQL(" and    (c.eparchy_code = :EPARCHY_CODE or c.eparchy_code = 'ZZZZ')");
		
		IDataset configDataset = Dao.qryByParse(parser);
		
		return configDataset;
	}
	
	public static IDataset getDiscntDateLimitConfig(String paramAttr, String paramCode, String discntCode) throws Exception
	{
		IData param = new DataMap();
		param.put("PARAM_CODE", paramCode);
		param.put("PARAM_ATTR", paramAttr);
		param.put("DISCNT_CODE", discntCode);
		
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from   td_s_commpara c ");
		parser.addSQL(" where  c.subsys_code = 'CSM' ");
		parser.addSQL(" and    c.param_attr = :PARAM_ATTR ");
		parser.addSQL(" and (c.param_code = -1 or c.param_code = :PARAM_CODE) ");
		parser.addSQL(" and    sysdate between start_date and end_date ");
		parser.addSQL(" and instr(c.param_name, '|' || :DISCNT_CODE || '|') > 0 ");
		
		IDataset configDataset = Dao.qryByParse(parser);
		
		return configDataset;
	}
	
	public static boolean isNoIMSFinishUserSvc(String serialNumber, String rsrvStr24) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("SERVICE_ID", rsrvStr24);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from TF_B_TRADE_SVC B where INSTR(:SERVICE_ID, '|' || B.SERVICE_ID || '|') > 0  ");
		parser.addSQL(" and B.END_DATE > sysdate and B.MODIFY_TAG = '0'  ");
		parser.addSQL(" and exists (select 1 from TF_B_TRADE A where A.TRADE_ID = B.TRADE_ID ");
		parser.addSQL(" and A.ACCEPT_MONTH = B.ACCEPT_MONTH and A.USER_ID = B.USER_ID  ");
		parser.addSQL(" and A.TRADE_TYPE_CODE = 6800 and A.CANCEL_TAG = '0' and A.SERIAL_NUMBER = :SERIAL_NUMBER)  ");
		IDataset serviceDataset = Dao.qryByParse(parser, Route.getJourDb());

		return Integer.parseInt(serviceDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}
	
	public static boolean isIMSFinishUserSvc(String serialNumber, String rsrvStr24) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("SERVICE_ID", rsrvStr24);

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select count(*) CNT from TF_F_USER A, TF_F_USER_SVC B  where A.USER_ID = B.USER_ID  ");
		parser.addSQL(" and A.PARTITION_ID = B.PARTITION_ID and INSTR(:SERVICE_ID, '|' || B.SERVICE_ID || '|') > 0  ");
		parser.addSQL(" and A.REMOVE_TAG = '0' and B.END_DATE > sysdate and A.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and B.START_DATE = (select max(B1.START_DATE)  from TF_F_USER A1, TF_F_USER_SVC B1  ");
		parser.addSQL(" where A1.USER_ID = B1.USER_ID and A1.PARTITION_ID = B1.PARTITION_ID and A1.REMOVE_TAG = '0'  ");
		parser.addSQL(" and B1.SERVICE_ID in (SELECT A.PARAM_CODE FROM td_s_commpara a WHERE a.param_attr = '560' AND a.end_date > SYSDATE)  ");
		parser.addSQL(" and B1.END_DATE > sysdate and A1.SERIAL_NUMBER = :SERIAL_NUMBER)  ");
		IDataset serviceDataset = Dao.qryByParse(parser);

		return Integer.parseInt(serviceDataset.getData(0).getString("CNT")) <= 0 ? false : true;
	}
}
