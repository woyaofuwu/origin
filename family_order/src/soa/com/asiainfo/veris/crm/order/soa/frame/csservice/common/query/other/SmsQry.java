
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class SmsQry
{

    public static IDataset queryDISCNTInfoByUserid(String userId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        return Dao.qryByCode("SMS", "SEL_FOR_WELCOME_DISCNT_HAIN", inParam);
    }
    
    //第三代订单中心改造
    public static IDataset queryDISCNTInfoByUseridNow(String userId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        IDataset userDiscntInfos =  Dao.qryByCode("SMS", "SEL_FOR_WELCOME_DISCNT_HAIN_NOW", inParam);
        IDataset result = new DatasetList();
        if(IDataUtil.isNotEmpty(userDiscntInfos)){
        	for(int i = 0 ; i < userDiscntInfos.size() ; i++){
        		IData userDiscntInfo = userDiscntInfos.getData(i);
        		IData offerInfos = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_DISCNT, userDiscntInfo.getString("SERVICE_ID",""));
        		if(IDataUtil.isNotEmpty(offerInfos)){
        			userDiscntInfo.put("BIZ_NAME", offerInfos.getString("OFFER_NAME",""));
        		}
        		result.add(userDiscntInfo);
        	}
        }
        return result;
    }

    public static IDataset queryLotteryWinners(String dealflag, String activityNumber, String beginDate, String endDate, String rowNumber, String userId, String execFlag, String prizeTypeCode, String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("DEAL_FLAG", dealflag);
        param.put("ACTIVITY_NUMBER", activityNumber);
        param.put("BEGIN_DATE", SysDateMgr.suffixDate(beginDate, 0));
        param.put("END_DATE", SysDateMgr.suffixDate(endDate, 1));
        param.put("ROW_NUMBER", String.valueOf(rowNumber));

        if (StringUtils.isNotBlank(userId))
        {
            param.put("USER_ID", userId);
        }

        if (StringUtils.isNotBlank(execFlag))
        {
            param.put("EXEC_FLAG", execFlag);
        }

        if (StringUtils.isNotBlank(prizeTypeCode))
        {
            param.put("PRIZE_TYPE_CODE", prizeTypeCode);
        }

        if (StringUtils.isNotBlank(serialNumber))
        {
            param.put("SERIAL_NUMBER", serialNumber);
        }
        return Dao.qryByCodeParser("SMS", "SEL_UEC_LOTTERY_FOR_BOSS", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset queryLotteryWinnersWithPage(String dealflag, String activityNumber, String beginDate, String endDate, String userId, String execFlag, String prizeTypeCode, String serialNumber, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("DEAL_FLAG", dealflag);
        param.put("ACTIVITY_NUMBER", activityNumber);
        param.put("BEGIN_DATE", SysDateMgr.suffixDate(beginDate, 0));
        param.put("END_DATE", SysDateMgr.suffixDate(endDate, 1));

        if (StringUtils.isNotBlank(userId))
        {
            param.put("USER_ID", userId);
        }

        if (StringUtils.isNotBlank(execFlag))
        {
            param.put("EXEC_FLAG", execFlag);
        }

        if (StringUtils.isNotBlank(prizeTypeCode))
        {
            param.put("PRIZE_TYPE_CODE", prizeTypeCode);
        }

        if (StringUtils.isNotBlank(serialNumber))
        {
            param.put("SERIAL_NUMBER", serialNumber);
        }
        return Dao.qryByCodeParser("SMS", "SEL_UEC_LOTTERY_FOR_BOSS_PAGE", param, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset queryPlatSVCInfoByUserid(String userId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        return Dao.qryByCode("SMS", "SEL_FOR_WELCOME_PLATSVC_HAIN", inParam);
    }
    
    //海南第三代订单中心改造
    public static IDataset queryPlatSVCInfoByUseridNow(String userId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        IDataset userPlatSvcInfos = Dao.qryByCode("SMS", "SEL_FOR_WELCOME_PLATSVC_HAIN_NOW", inParam);
        IDataset result = new DatasetList();
        if(IDataUtil.isNotEmpty(userPlatSvcInfos)){
        	int userPlatSvcInfoSize = userPlatSvcInfos.size();
        	for(int i = 0 ; i < userPlatSvcInfoSize ; i++){
        		IData userPlatSvcInfo = userPlatSvcInfos.getData(i);
        		IDataset offerInfos = UpcCall.qrySpServiceSpInfo(userPlatSvcInfo.getString("SERVICE_ID",""), BofConst.ELEMENT_TYPE_CODE_PLATSVC);
        		if(IDataUtil.isNotEmpty(offerInfos)){
        			IData offerInfo = offerInfos.getData(0);
        			if(userPlatSvcInfo.getString("SP_ID","").equals(offerInfo.getString("SP_ID",""))
        			&& userPlatSvcInfo.getString("BIZ_CODE","").equals(offerInfo.getString("BIZ_CODE",""))	
        			&& userPlatSvcInfo.getString("BIZ_TYPE_CODE","").equals(offerInfo.getString("BIZ_TYPE_CODE",""))	
        					){
        				String spName = "注册类业务".equals(offerInfo.getString("SP_NAME","")) ? "中国移动" : offerInfo.getString("SP_NAME","");
        				userPlatSvcInfo.put("SP_NAME", spName);
        				userPlatSvcInfo.putAll(offerInfo);
        				result.add(userPlatSvcInfo);
        			}
        		}
        	}
        }
        return result;
    }

    public static IDataset querySVCInfoByUserid(String userId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        return Dao.qryByCode("SMS", "SEL_FOR_WELCOME_SVC_HAIN", inParam);
    }
    
    //海南第三代订单中心改造
    public static IDataset querySVCInfoByUseridNow(String userId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("USER_ID", userId);
        IDataset svcInfos =  Dao.qryByCode("SMS", "SEL_FOR_WELCOME_SVC_HAIN_NOW", inParam);
        IDataset result = new DatasetList();
        if(IDataUtil.isNotEmpty(svcInfos)){
        	for(int i = 0 ; i < svcInfos.size() ; i++){
        		IData svcInfo = svcInfos.getData(i);
        		IData offerInfo = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_SVC, svcInfo.getString("SERVICE_ID",""), "Y");
        		if(IDataUtil.isNotEmpty(offerInfo)){
        			svcInfo.put("BIZ_NAME", offerInfo.getString("OFFER_NAME",""));
        			svcInfo.put("BILLFLG", offerInfo.getString("RSRV_STR4","2"));
        			svcInfo.put("PRICE", offerInfo.getString("RSRV_STR3",""));
        		}
        		result.add(svcInfo);
        	}
        }
        return result;
    }

}
