
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changesvcstate;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * 宽带预约报停到期提醒处理服务
 * 
 * @author chenzm
 */
public class WidenetStopExpireSVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(WidenetStopExpireSVC.class);
    public void dealExpire(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        IDataset userSvcStateInfos = UserSvcStateInfoQry.getUserMainState(userId);
        if (IDataUtil.isNotEmpty(userSvcStateInfos))
        {
            String stateCode = userSvcStateInfos.getData(0).getString("STATE_CODE");
            if ("1".equals(stateCode) || "5".equals(stateCode))
            {
                StringBuilder bufferSQL2 = new StringBuilder();
                bufferSQL2.append("UPDATE TF_F_USER SET ");
                bufferSQL2.append(" USER_STATE_CODESET = '" + stateCode + "'");
                bufferSQL2.append(" WHERE USER_ID = '" + userId + "' ");
                bufferSQL2.append(" AND PARTITION_ID = MOD(USER_ID, 10000)");
                bufferSQL2.append(" AND REMOVE_TAG = '0' ");

                IData userParam = new DataMap();
                userParam.put("USER_ID", userId);
                Dao.executeUpdate(bufferSQL2, userParam);
                // 宽带预约停机TI_B_USER同步处理
                this.syncUserInfo(tradeId, userId);
            }
        }

    }

    /**
     * @Description: 同步用户状态给账务侧
     * @param tradeId
     * @param userId
     * @throws Exception
     */
    public void syncUserInfo(String tradeId, String userId) throws Exception
    {
        String syncSequence = SeqMgr.getSyncIncreId();
        String curDay = SysDateMgr.getCurDay();
        
        StringBuilder iuserSQL = new StringBuilder();
        
        iuserSQL.append("SELECT '" + syncSequence + "' SYNC_SEQUENCE,'" + curDay + "' SYNC_DAY,'8' MODIFY_TAG,'" + tradeId + "' TRADE_ID, MOD(B.USER_ID, 10000) PARTITION_ID, B.USER_ID, B.CUST_ID, B.USECUST_ID, ");
        iuserSQL.append("B.EPARCHY_CODE,B.CITY_CODE, B.CITY_CODE_A, B.USER_PASSWD, B.USER_DIFF_CODE, B.USER_TYPE_CODE, ");
        iuserSQL.append("B.USER_TAG_SET, B.USER_STATE_CODESET, B.NET_TYPE_CODE, B.SERIAL_NUMBER, B.CONTRACT_ID, B.ACCT_TAG, ");
        iuserSQL.append("B.PREPAY_TAG, B.MPUTE_MONTH_FEE, B.MPUTE_DATE, B.FIRST_CALL_TIME, B.LAST_STOP_TIME, B.CHANGEUSER_DATE,");
        iuserSQL.append("B.IN_NET_MODE, B.IN_DATE, B.IN_STAFF_ID,B.IN_DEPART_ID, B.OPEN_MODE, B.OPEN_DATE, B.OPEN_STAFF_ID, ");
        iuserSQL.append("B.OPEN_DEPART_ID, B.DEVELOP_STAFF_ID, B.DEVELOP_DATE, B.DEVELOP_DEPART_ID, B.DEVELOP_CITY_CODE, ");
        iuserSQL.append("B.DEVELOP_EPARCHY_CODE, B.DEVELOP_NO, B.ASSURE_CUST_ID, B.ASSURE_TYPE_CODE,B.ASSURE_DATE, B.REMOVE_TAG, ");
        iuserSQL.append("B.PRE_DESTROY_TIME, B.DESTROY_TIME,B.REMOVE_EPARCHY_CODE, B.REMOVE_CITY_CODE, B.REMOVE_DEPART_ID, ");
        iuserSQL.append("B.REMOVE_REASON_CODE,B.UPDATE_TIME, B.UPDATE_STAFF_ID, B.UPDATE_DEPART_ID, B.REMARK, B.RSRV_NUM1, ");
        iuserSQL.append("B.RSRV_NUM2, B.RSRV_NUM3, B.RSRV_NUM4, B.RSRV_NUM5, B.RSRV_STR1, B.RSRV_STR2,B.RSRV_STR3, B.RSRV_STR4, ");
        iuserSQL.append("B.RSRV_STR5, B.RSRV_STR6, B.RSRV_STR7, B.RSRV_STR8,B.RSRV_STR9, B.RSRV_STR10, B.RSRV_DATE1, B.RSRV_DATE2, ");
        iuserSQL.append("B.RSRV_DATE3, B.RSRV_TAG1,B.RSRV_TAG2, B.RSRV_TAG3, C.BRAND_CODE , C.PRODUCT_ID ");
        iuserSQL.append("FROM TF_F_USER B,  TF_F_USER_INFOCHANGE C ");
        iuserSQL.append("WHERE B.USER_ID = '" + userId + "' ");
        iuserSQL.append("AND B.USER_ID = C.USER_ID ");
        iuserSQL.append("AND C.PARTITION_ID = MOD(B.USER_ID, 10000)");
        iuserSQL.append("AND SYSDATE BETWEEN C.START_DATE AND C.END_DATE");

        IData userParam = new DataMap();
        userParam.put("USER_ID", userId);
        
        IDataset results =Dao.qryBySql(iuserSQL,userParam);
        
        if (IDataUtil.isNotEmpty(results))
        {
            for (int i = 0; i < results.size(); i++)
            {
                results.getData(i).put("SCORE_VALUE", "0");
                results.getData(i).put("BASIC_CREDIT_VALUE", "0");
                results.getData(i).put("CREDIT_VALUE", "0");
                results.getData(i).put("CREDIT_CLASS", "0");
            }
            
            Dao.insert("TI_B_USER", results, Route.getJourDbDefault());
            
            IData syncData = new DataMap();
            syncData.put("SYNC_SEQUENCE", syncSequence);
            syncData.put("SYNC_DAY", curDay);
            syncData.put("SYNC_TYPE", "0");
            syncData.put("TRADE_ID", tradeId);
            syncData.put("STATE", "0");
            syncData.put("REMARK", "宽带报停预约到期用户资料同步【" + userId + "】");

            Dao.insert("TI_B_SYNCHINFO", syncData, Route.getJourDbDefault());
         }
    }

    /**
     * AEE自动任务
     * 包年套餐结束自动停机
     * @param userInfo
     * @throws Exception
     */
    public void checkWidenetPackYearsExpire(IData inParam) throws Exception
    {
    	IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
		String KDserialNumber = "KD_" + dealParam.getString("SERIAL_NUMBER");
        IData wideInfo = UcaInfoQry.qryUserInfoBySn(KDserialNumber);
        if (IDataUtil.isNotEmpty(wideInfo))
        {
        	IData param = new DataMap();
			param.put("SERIAL_NUMBER", KDserialNumber);
            param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
            param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            param.put("VALID_FLAG", "1");// 必传，宽带规则判断标示，传1宽带不判断手机状态
            // 宽带业务类型
            IDataset userWidenetInfo = WidenetInfoQry.getUserWidenetInfo(wideInfo.getString("USER_ID"));
            if (IDataUtil.isNotEmpty(userWidenetInfo))
            {
            	 IData userWidenet = userWidenetInfo.getData(0);
                 String widenetType = userWidenet.getString("RSRV_STR2");
                 String tradeTypeCode = "7221";// GPON宽带欠费停机
                 if ("2".equals(widenetType)||"5".equals(widenetType)||"6".equals(widenetType))
                 {
                     tradeTypeCode = "7222";// ADSL宽带欠费停机
                 }
                 else if ("3".equals(widenetType))
                 {
                     tradeTypeCode = "7223";// 光纤宽带欠费停机
                 }
                 else if ("4".equals(widenetType))
                 {
                     tradeTypeCode = "7224";// 校园宽带欠费停机
                 }
                 param.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
            try{
            	IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", param);
            }catch (Exception e) {
            	CSAppException.apperr(CrmCommException.CRM_COMM_103,e.toString());
			}
        }
    }
    
    /**
     * AEE自动任务
     * 宽带候鸟活动到期自动绑定按天收费套餐
     * @param userInfo
     * @throws Exception
     */
	public void checkWidenetBirdsExpire(IData inParam) throws Exception {
		IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
		String tag = dealParam.getString("TAG", "");
		if("HOUNIAO_2019".equals(tag))
		{
			String KDserialNumber = "KD_" + dealParam.getString("SERIAL_NUMBER","");
	        IData wideUserInfo = UcaInfoQry.qryUserInfoBySn(KDserialNumber);
	        if (IDataUtil.isNotEmpty(wideUserInfo))
	        {
	        	IData param = new DataMap();
				param.put("SERIAL_NUMBER", KDserialNumber);
	            param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
	            param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
	            param.put("VALID_FLAG", "1");// 必传，宽带规则判断标示，传1宽带不判断手机状态
	            param.put("TRADE_TYPE_CODE", "603");	 
	            param.put("HOUNIAO_TAG", "1");
	            param.put("TV_FLAG", "1");

	            try{
	            	IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", param);
	            }catch (Exception e) {
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,e.toString());
				}
	            
	            //add by zhangxing3 for 度假宽带2019用户自动停机时判断如果有魔百和业务，需要关联魔百和报停
	            IData userInfo = UcaInfoQry.qryUserInfoBySn(dealParam.getString("SERIAL_NUMBER",""));
	            if(IDataUtil.isEmpty(userInfo)){
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
	            }
	            String userId = userInfo.getString("USER_ID","");
	    		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
	    		if(IDataUtil.isNotEmpty(boxInfos)){
	    			
	    			IData boxInfo=boxInfos.first();
	    			String stopSignal=boxInfo.getString("RSRV_TAG3","");
	    	        IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3900", userId, "0");
	    			if(!"1".equals(stopSignal) && IDataUtil.isEmpty(outDataset)){
	    				
			            IData acceptData = new DataMap();
			            String tradeId = SeqMgr.getTradeIdFromDb();
			            String serialNumber = dealParam.getString("SERIAL_NUMBER","");

			            acceptData.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
			            acceptData.put("USER_ID", userId);
			            acceptData.put("SERIAL_NUMBER", serialNumber);
			            acceptData.put("TRADE_TYPE_CODE", "3900");

			            IData insertData = new DataMap();
			            insertData.put("DEAL_ID", tradeId);
			            insertData.put("USER_ID", userId);
			            insertData.put("PARTITION_ID", userId.substring(userId.length() - 4));
			            insertData.put("SERIAL_NUMBER", serialNumber);
			            insertData.put("EPARCHY_CODE", "0898");
			            insertData.put("EXEC_TIME", SysDateMgr.getFirstDayOfNextMonth4WEB());// 先统一默认为下月1号，有特殊情况再处理
			            insertData.put("EXEC_MONTH", SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(1, SysDateMgr.getSysTime()), "MM"));
			            insertData.put("IN_TIME", SysDateMgr.getSysTime());
			            insertData.put("DEAL_STATE", "0");
			            insertData.put("DEAL_COND", acceptData.toString());
			            insertData.put("DEAL_TYPE", "StopTopsetboxExp");

			            Dao.insert("TF_F_EXPIRE_DEAL", insertData);
    				
	    	        }	    				    			
	            } 
	            //add by zhangxing3 for 度假宽带2019用户自动停机时判断如果有魔百和业务，需要关联魔百和报停

	        }
		}
		else
		{
			// 候鸟按天收费套餐
			String discntCodeDay = "84013242";
			UcaData mobileUserInfo = UcaDataFactory.getNormalUca(dealParam.getString("SERIAL_NUMBER"));
			String mobileUserId = mobileUserInfo.getUserId();
	
			try {
	
				IDataset UserDiscntInfos = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(mobileUserId, discntCodeDay,"0898");
				if (IDataUtil.isNotEmpty(UserDiscntInfos)) {
					return;// 已存在按天收费套餐，则不再绑定了。
				}
				if (1 == insUserDiscnt(mobileUserId, discntCodeDay)) {
					// 绑定按天收费套餐成功后，执行同步操作。
					String syncIncreId = SeqMgr.getSyncIncreId();
					insTiSync(syncIncreId);
					insTibDiscnt(syncIncreId, mobileUserId, discntCodeDay);
				} else {
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"宽带候鸟活动到期自动绑定按天收费套餐失败！");
				}
	
			} catch (Exception e) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, e.toString());
			}
		}
	}
    private int insUserDiscnt(String userId,String discntCode) throws Exception
    {
        String instId = SeqMgr.getInstId();
		IData cond = new DataMap();
		cond.put("USER_ID",userId);
		cond.put("USER_ID_A","-1");
		cond.put("PRODUCT_ID","-1");
		cond.put("PACKAGE_ID","-1");
		cond.put("DISCNT_CODE",discntCode);
		cond.put("SPEC_TAG","0");
		cond.put("RELATION_TYPE_CODE","");
		cond.put("INST_ID",instId);
		cond.put("CAMPN_ID","");
		cond.put("START_DATE",SysDateMgr.getSysDateYYYYMMDD());
		cond.put("END_DATE",SysDateMgr.getTheLastTime());
		cond.put("SPEC_TAG","0");
		cond.put("REMARK","候鸟活动到期自动绑定按天收费套餐");
		cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
		cond.put("UPDATE_STAFF_ID","SUPERUSR");
		cond.put("UPDATE_DEPART_ID","36601");
		return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);         
    }
    private void insTiSync(String iv_sync_sequence) throws Exception
    {
        IData synchInfoData = new DataMap();
        synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
        String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
        synchInfoData.put("SYNC_DAY", syncDay);
        synchInfoData.put("SYNC_TYPE", "0");
        synchInfoData.put("TRADE_ID", "0");
        synchInfoData.put("STATE", "0");
        synchInfoData.put("REMARK", "候鸟活动到期自动绑定按天收费套餐");
        synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
        synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        Dao.insert("TI_B_SYNCHINFO", synchInfoData,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    private void insTibDiscnt(String iv_sync_sequence, String userId,String discntCode) throws Exception
    {
        IDataset UserDiscntInfos=UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId,discntCode,"0898");
        if(IDataUtil.isNotEmpty(UserDiscntInfos)){
        	for(int i=0;i<UserDiscntInfos.size();i++){
        		IData param =UserDiscntInfos.getData(i);
                param.put("PRODUCT_ID", param.getString("PRODUCT_ID","-1"));
                param.put("PACKAGE_ID", param.getString("PACKAGE_ID","-1"));
                param.put("SYNC_SEQUENCE", iv_sync_sequence);
                param.put("USER_ID", userId);
        	    Dao.executeUpdateByCodeCode("TI_B_USER_DISCNT", "INS_TIBDISCNT_USERDISCNT_ID", param,Route.getJourDb(BizRoute.getRouteId()));
        	}
        } 
    }
    
//    public static IDataset getWidenetPackYearsExpire() throws Exception
//    {
//    	IData param = new DataMap();
//    	return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_PACLYEARS_EXPIRE", param);
//    }
//    
//    /**
//	 * 根据USER_ID获得正常用户的SERIAL_NUMBER
//	 * @param param
//	 * @return
//	 * @throws Exception
//	 */
//	public static String getSerialNumberByUserId(String userId) throws Exception
//	{
//		IData param = new DataMap();
//		param.put("USER_ID", userId);
//		SQLParser parser = new SQLParser(param);     
//		parser.addSQL("select serial_number from TF_F_USER where user_id = :USER_ID");
//		return Dao.qryByParse(parser).first().getString("SERIAL_NUMBER");
//	}
	
	 
}
