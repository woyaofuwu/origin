
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoChgQry;

public class CrmAMInfoBean extends CSBizBean
{
	private static int SPLIT = 0x19 ;
	
	public IDataset infostr(IDataset list,String userId,String infotype,String sqlRef) throws Exception
	{
		IDataset commparaInfos5152 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","5152","BOF_SYNC",sqlRef);
		String [] stmts = commparaInfos5152.first().getString("PARA_CODE20").split(";");
		IDataset dtrade = new  DatasetList();	
		for(int j=0;j<list.size();j++){
			String infostr = "";
			IData params = new DataMap();
			params.put("subno", j+1);
			params.put("infotype", infotype);
			params.put("opermethod", "2");
			for(int i=0;i<stmts.length;i++){
				String str = stmts[i];
				str = str.trim();
				str = str.toUpperCase();
				String str2 = StringUtils.isBlank(list.getData(j).getString(str)) ?"":list.getData(j).getString(str);
				if(i==stmts.length-1){
					infostr=infostr+str2;
				}else{
					infostr=infostr+str2+String.valueOf((char)SPLIT);
				}
			}	
			params.put("infostr", infostr.toString());
			dtrade.add(params);
		}
		return dtrade;
	}
	
	public IData queryCrmInfoList(IData inparam) throws Exception
	{
		IData result = new DataMap();
		result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "操作成功");
		
		IDataUtil.chkParam(inparam, "TABLE_CODE"); 
		String tableCode = inparam.getString("TABLE_CODE");
		if(!"301".equals(tableCode)){
			IDataUtil.chkParam(inparam, "USER_ID"); 
		}else{
			IDataUtil.chkParam(inparam, "USER_ID_B"); 
		}
		String userId = inparam.getString("USER_ID");
		String userIdB = inparam.getString("USER_ID_B");
		inparam.put("ROUTE_EPARCHY_CODE", "0898");
		
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("USER_ID_B", userIdB);
		
		result.putAll(doNewDbQueryForCTB(inparam,StringUtils.isNotBlank(userId)?userId:userIdB)) ;
		
		String serialNum ="";
		IData userInfo = UcaInfoQry.qryUserInfoByUserId(StringUtils.isNotBlank(userId)?userId:userIdB);
		if(IDataUtil.isNotEmpty(userInfo)){
			 serialNum = userInfo.getString("SERIAL_NUMBER");
		}
		
		if("300".equals(tableCode))
		{
			IDataset userInfos =  Dao.qryByCode("BOF_SYNC", "SEL_TF_F_USER_BY_USERID", param);
			IDataset dtrade = infostr(userInfos,userId,tableCode,"SEL_TF_F_USER_BY_USERID");
			
//			String infostr = "";
//			IDataset commparaInfos5152 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","5152","BOF_SYNC","SEL_TF_F_USER_BY_USERID");
//			String [] stmts = commparaInfos5152.first().getString("PARA_CODE20").split(";");
//			IDataset dtrade = new  DatasetList();	
//			for(int j=0;j<userInfos.size();j++){
//				IData params = new DataMap();
//				params.put("subno", j+1);
//				params.put("infotype", "300");
//				params.put("opermethod", "2");
//				for(int i=0;i<stmts.length;i++){
//					String str = stmts[i];
//					str = str.trim();
//					str = str.toUpperCase();
//					String str2 = StringUtils.isBlank(userInfos.getData(j).getString(str)) ?"":userInfos.getData(j).getString(str);
//					if(i==stmts.length-1){
//						infostr=infostr+str2;
//					}else{
//						infostr=infostr+str2+String.valueOf((char)SPLIT);
//					}
//				}	
//				params.put("infostr", infostr.toString());
//				dtrade.add(params);
//			}
			
			result.put("dtrade", dtrade);
			
			
		}else if("301".equals(tableCode)){
			
			//IDataset userRelaUUInfo = RelaUUInfoQry.getRelaUUInfoByUseridB(userIdB,Route.CONN_CRM_CG,null);
			IDataset userRelaUUInfo =  Dao.qryByCode("BOF_SYNC", "SEL_RELATION_UU_BY_USERID_SYNC", param,Route.CONN_CRM_CG);
			IDataset dtrade = infostr(userRelaUUInfo,userId,tableCode,"SEL_RELATION_UU_BY_USERID_SYNC");
			result.put("dtrade", dtrade);
			
		}else if("302".equals(tableCode)){
			
			IDataset userInfoChanges = Dao.qryByCode("BOF_SYNC", "SEL_BY_USERID_INFOCHANGE", param);
			IDataset dtrade = infostr(userInfoChanges,userId,tableCode,"SEL_BY_USERID_INFOCHANGE");
			result.put("dtrade", dtrade);
			
		}else if("304".equals(tableCode)){
			
			//IDataset userSvcSet = UserSvcInfoQry.qryUserSvcByUserId(userId);
			IDataset userSvcSet = Dao.qryByCode("BOF_SYNC", "SEL_BY_USERID_SVC_SYNC", param);
			IDataset dtrade = infostr(userSvcSet,userId,tableCode,"SEL_BY_USERID_SVC_SYNC");
			result.put("dtrade", dtrade);
			
		}else if("306".equals(tableCode)){
			
			//IDataset userSvcStates = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, "0898");
			IDataset userSvcStates = Dao.qryByCode("BOF_SYNC", "SEL_BY_USERID_SVCSTATE_SYNC", param);
			IDataset dtrade = infostr(userSvcStates,userId,tableCode,"SEL_BY_USERID_SVCSTATE_SYNC");
			result.put("dtrade", dtrade);
			
		}else if("309".equals(tableCode)){
			
			IDataset platsvcOrderInfo = new DatasetList();
			//IDataset traces = Dao.qryByCodeParser("TF_F_USER_PLATSVC_TRACE", "SEL_BY_USER_ID", param);
			IDataset traces = Dao.qryByCode("BOF_SYNC", "SEL_BY_USERID_PLATSVC_ORDER", param);
			for (Object obj : traces)
			{
				IData trace = (IData) obj;
				String billType = trace.getString("BILL_TYPE");
				if("-1".equals(billType)){
					trace.put("BILL_TYPE", "9");
				}
				trace.put("SERIAL_NUMBER", serialNum);
				String svcId = trace.getString("SERVICE_ID");
				IDataset ids = UpcCall.qryByServiceIdBillType(BofConst.ELEMENT_TYPE_CODE_PLATSVC, svcId);
				if (IDataUtil.isNotEmpty(ids))
				{
					trace.putAll(ids.getData(0));
					platsvcOrderInfo.add(trace);
				}
			}
			IDataset dtrade = infostr(platsvcOrderInfo,userId,tableCode,"SEL_BY_USERID_PLATSVC_ORDER");
			result.put("dtrade", dtrade);
			
		}else if("313".equals(tableCode)){
			
			IDataset payrelationInfos = Dao.qryByCode("BOF_SYNC", "SEL_PAYRELATION_BY_USERID_SYNC", param);
//			if (IDataUtil.isNotEmpty(payrelationInfos))
//			{
//				for(int i=0;i<payrelationInfos.size();i++){
//					IData payrelationInfo = payrelationInfos.getData(i);
//					Date now = new Date();
//					String nowDate=SysDateMgr.date2String(now,SysDateMgr.PATTERN_TIME_YYYYMMDD);
//					if(nowDate.compareTo(payrelationInfo.getString("END_CYCLE_ID"))<0){
//						payrelation.add(payrelationInfo);
//					}
//				}
//			}
			IDataset dtrade = infostr(payrelationInfos,userId,tableCode,"SEL_PAYRELATION_BY_USERID_SYNC");
			result.put("dtrade", dtrade);
			
		}else if("401".equals(tableCode)){
			
			//IDataset userDiscntInfos = UserDiscntInfoQry.getUserNormalDiscntByUserId(userId);
			IDataset userDiscntInfos = Dao.qryByCode("BOF_SYNC", "SEL_DISCNT_BY_USERID_SYNC", param);
			IDataset dtrade = infostr(userDiscntInfos,userId,tableCode,"SEL_DISCNT_BY_USERID_SYNC");
			result.put("dtrade", dtrade);
			
		}else if("402".equals(tableCode)){
			
			IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_DISCNTATTR_BY_USERID_SYNC", param);
			IDataset dtrade = infostr(ids,userId,tableCode,"SEL_DISCNTATTR_BY_USERID_SYNC");
			result.put("dtrade", dtrade);
			
		}
		
		
		
		return result;
	}
	public IData doNewDbQueryForCTB(IData inparam,String userId) throws Exception
	{
		IData params = new DataMap();
		
		String day = SysDateMgr.getCurDay();
        if(day.length()<2) 
        	day = "0" + day;
        params.put("partitionid", day);
        params.put("eventcode", "");//SeqMgr.getSyncIncreId()
        
        //查询三户资料
  		//IDataset userInfo = CSAppCall.call("SS.ModifyEPostInfoSVC.qryCustInfos", inparam);
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
  		if(IDataUtil.isNotEmpty(userInfo)){
  			String custId = userInfo.getString("CUST_ID");
  			params.put("idtype", "1,3");
  			params.put("idkey", custId+","+userId);
  			
  			IDataset acctInfo = AcctInfoQry.getAcctInfoByCustIdForGrp(custId);
  			if(IDataUtil.isNotEmpty(acctInfo)){
  				String acctId = acctInfo.first().getString("ACCT_ID");
  			    params.put("idtype", "1,2,3");
  			    params.put("idkey", custId+","+acctId+","+userId);
  			}
  		}
  		
  		params.put("deal_type", "");
  		String dealTime = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
  		params.put("deal_time", dealTime);
  		params.put("discntkey", "");
  		params.put("donetag", "0");
  		params.put("updatetime", "");
  		
		return params;
	}
	
	 public String  sqlStmt(IData params) throws Exception{
	    
		String sqlStmt = null;
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL("SELECT * FROM TD_B_CRMTOBILLING WHERE STATE = '3' and t.c_table =:c_table"); 
        IDataset infos=  Dao.qryByParse(parser); 
        if(IDataUtil.isNotEmpty(infos)){
        	sqlStmt = infos.first().getString("SQL_DEFINE");
        }
    	
    	return sqlStmt;
	 }
	    
	
	public IData syncCrmInfoList(IData inparam) throws Exception
	{
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "操作成功");
		
		IDataUtil.chkParam(inparam, "TABLE_CODE"); 
		String tableCode = inparam.getString("TABLE_CODE");
		if(!"301".equals(tableCode)){
			IDataUtil.chkParam(inparam, "USER_ID"); 
		}else{
			IDataUtil.chkParam(inparam, "USER_ID_B"); 
		}
		String userId = inparam.getString("USER_ID");
		String userIdB = inparam.getString("USER_ID_B");
		String syncId = SeqMgr.getSyncIncreId();
		String syncDay = SysDateMgr.getCurDay();
		String syncMonth = SysDateMgr.getCurMonth();
		int num = 0;
		boolean flg = false;
	    result.put("SYNC_SEQUENCE", syncId);
		
		IData param = new DataMap();
		param.put("USER_ID", userId);
		
		if("300".equals(tableCode))
		{
			//IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
			IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserId(userId, BizRoute.getRouteId());

			userInfo.put("MODIFY_TAG", "8");
			userInfo.put("SYNC_SEQUENCE",syncId);
			userInfo.put("SYNC_DAY",syncDay);
			
			userInfo.put("SCORE_VALUE", "0");
			userInfo.put("CREDIT_CLASS", "0");
			userInfo.put("BASIC_CREDIT_VALUE", "0");
			userInfo.put("CREDIT_VALUE", "0");

		    flg = Dao.insert("TI_B_USER", userInfo, Route.getJourDbDefault());
			
		}else if("301".equals(tableCode)){
			
			param.put("USER_ID_B", userIdB);
			IDataset userRelaUUInfos = Dao.qryByCode("BOF_SYNC", "SEL_RELATION_UU_BY_USERID", param);
			IDataset userRelaBBInfos = RelaBBInfoQry.getBBByUserIdAB(null,userIdB,null,null);
			userRelaUUInfos.addAll(userRelaBBInfos);
			if (IDataUtil.isNotEmpty(userRelaUUInfos))
			{
				syncParams(userRelaUUInfos,syncId,syncDay);
				
				int[] rowArray = Dao.insert("TI_B_RELATION_UU", userRelaUUInfos, Route.getJourDbDefault());
				num = getRowArrayCount(rowArray);
			}
			
		}else if("302".equals(tableCode)){
			
			IDataset userInfoChanges = UserInfoChgQry.getUserInfoChgByUserIdCurvalid(userId);
			if (IDataUtil.isNotEmpty(userInfoChanges))
			{
				syncParams(userInfoChanges,syncId,syncDay);
				int[] rowArray = Dao.insert("TI_B_USER_INFOCHANGE", userInfoChanges, Route.getJourDbDefault());
				num = getRowArrayCount(rowArray);
			}
			
		}else if("304".equals(tableCode)){
			
			IDataset userSvcSet = Dao.qryByCode("BOF_SYNC", "SEL_SVC_BY_USERID", param);
			if (IDataUtil.isNotEmpty(userSvcSet))
			{
				syncParams(userSvcSet,syncId,syncDay);
				for(int i=0;i<userSvcSet.size();i++){
					IData userSvc = userSvcSet.getData(i);
					userSvc.put("PRODUCT_ID", "-1");
					userSvc.put("PACKAGE_ID", "-1");
				}
				int[] rowArray = Dao.insert("TI_B_USER_SVC", userSvcSet, Route.getJourDbDefault());
				num = getRowArrayCount(rowArray);
			}
			
		}else if("306".equals(tableCode)){
			
			IDataset userSvcStates = Dao.qryByCode("BOF_SYNC", "SEL_SVCSTATE_BY_USERID", param);
			if (IDataUtil.isNotEmpty(userSvcStates))
			{
				syncParams(userSvcStates,syncId,syncDay);
				int[] rowArray = Dao.insert("TI_B_USER_SVCSTATE", userSvcStates, Route.getJourDbDefault());
				num = getRowArrayCount(rowArray);
			}
			
		}else if("309".equals(tableCode)){//!!!!
			
			IDataset traces = Dao.qryByCodeParser("BOF_SYNC", "SEL_USERPLATSVCTRACE_BY_UIDTID2", param);
			if (IDataUtil.isNotEmpty(traces))
			{
				syncParams(traces,syncId,syncDay);
				for (Object obj : traces)
				{
					IData trace = (IData) obj;
					String svcId = trace.getString("SERVICE_ID");
					IDataset ids = UpcCall.qryByServiceIdBillType(BofConst.ELEMENT_TYPE_CODE_PLATSVC, svcId);
					if (IDataUtil.isNotEmpty(ids))
					{
						trace.putAll(ids.getData(0));
						Dao.insert("TI_B_PLATSVC_ORDER", trace, Route.getJourDbDefault());
					}
				}
				syncParams(traces,syncId,syncDay);
				//int[] rowArray = Dao.insert("TI_B_PLATSVC_ORDER", traces, Route.getJourDbDefault());
				//num = getRowArrayCount(rowArray);
				
			}
			
		}else if("313".equals(tableCode)){
			
			IDataset payrelation = new DatasetList();
			IDataset payrelationInfos = Dao.qryByCode("BOF_SYNC", "SEL_PAYRELATION_BY_USERID", param);
			if (IDataUtil.isNotEmpty(payrelationInfos))
			{
				for(int i=0;i<payrelationInfos.size();i++){
					IData payrelationInfo = payrelationInfos.getData(i);
					Date now = new Date();
					String nowDate=SysDateMgr.date2String(now,SysDateMgr.PATTERN_TIME_YYYYMMDD);
					if(nowDate.compareTo(payrelationInfo.getString("END_CYCLE_ID"))<0){
						payrelation.add(payrelationInfo);
					}
				}
				syncParams(payrelation,syncId,syncDay);
				int[] rowArray = Dao.insert("TI_B_PAYRELATION", payrelation, Route.getJourDbDefault());
				num = getRowArrayCount(rowArray);
			}
			
		}else if("401".equals(tableCode)){
			
			IDataset userDiscntInfos = UserDiscntInfoQry.getUserNormalDiscntByUserId2(userId);
			if (IDataUtil.isNotEmpty(userDiscntInfos))
			{
				for(int i=0;i<userDiscntInfos.size();i++){
					IData userSvc = userDiscntInfos.getData(i);
					userSvc.put("PRODUCT_ID", "-1");
					userSvc.put("PACKAGE_ID", "-1");
					userSvc.put("ID_TYPE", "0");
				}
				syncParams(userDiscntInfos,syncId,syncDay);
				int[] rowArray = Dao.insert("TI_B_USER_DISCNT", userDiscntInfos, Route.getJourDbDefault());
				num = getRowArrayCount(rowArray);
			}
			
		}else if("402".equals(tableCode)){
			
			IDataset ids = Dao.qryByCode("BOF_SYNC", "SEL_DISCNTATTR_BY_USERID", param);
			if (IDataUtil.isNotEmpty(ids))
			{
				syncParams(ids,syncId,syncDay);
				int[] rowArray = Dao.insert("TI_B_USER_DISCNT_ATTR", ids, Route.getJourDbDefault());
				num = getRowArrayCount(rowArray);
			}
			
		}
		
		// 插同步主表 begin
	
		IData syncInfo = new DataMap();
		syncInfo.put("SYNC_SEQUENCE", syncId);
		syncInfo.put("SYNC_DAY", syncDay);
		syncInfo.put("SYNC_TYPE", "0");
		syncInfo.put("TRADE_ID", "");
		syncInfo.put("STATE", "0");
		syncInfo.put("USER_ID", userId);
		syncInfo.put("EPARCHY_CODE", "0898");
		syncInfo.put("TRADE_TYPE_CODE","0898");

		Dao.insert("TI_B_SYNCHINFO", syncInfo, Route.getJourDbDefault());
	
		// 插同步主表 end
		
		
		
		return result;
	}

	private void syncParams(IDataset datas,String syncId,String syncDay ) throws Exception {
		String sn =null;
		if(IDataUtil.isNotEmpty(datas)){
			IData userInfo = UcaInfoQry.qryUserInfoByUserId(datas.first().getString("USER_ID"));
		    if(IDataUtil.isNotEmpty(userInfo)){
		    	sn = userInfo.getString("SERIAL_NUMBER");
		    }
		}
		for (int i = 0, size = datas.size(); i < size; i++)
		{
			IData data = datas.getData(i);
			data.put("SERIAL_NUMBER", sn);
			data.put("MODIFY_TAG", "8");
			data.put("SYNC_SEQUENCE",syncId);
			data.put("SYNC_DAY",syncDay);
		}
	}
	
	private static int getRowArrayCount(int[] rowArray) throws Exception
	{
		if (rowArray == null || rowArray.length == 0)
		{
			return 0;
		}

		int result = 0;
		for (int rowCount : rowArray)
		{
			result = result + rowCount;
		}
		return result;
	}
	
	/**
	 * 返回指定字符串中指定字符第N次出现在的位置
	 * @author  
	 * @param string
	 * @return 
	 */
	public static int getCharacterPosition(String string, String c, int num)
	{

		//这里是获取c符号的位置
		Matcher slashMatcher = Pattern.compile(c).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find())
		{
			mIdx++;
			//当c符号第二次出现的位置
			if (mIdx == num)
			{
				break;
			}
		}
		return slashMatcher.start();
	}
	
}
