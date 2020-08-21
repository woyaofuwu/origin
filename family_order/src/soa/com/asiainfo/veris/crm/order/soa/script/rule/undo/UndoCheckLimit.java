
package com.asiainfo.veris.crm.order.soa.script.rule.undo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: UndoCheckLimit.java
 * @Description: 业务返销校验[限制]
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-29 下午4:17:02
 */
public class UndoCheckLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UndoCheckLimit.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UndoCheckLimit() >>>>>>>>>>>>>>>>>>");

        IData hisTradeData = databus.getData("TRADE_INFO");// 历史订单信息
        String tradeId = hisTradeData.getString("TRADE_ID");
        String tradeTypeCode = hisTradeData.getString("TRADE_TYPE_CODE");
        String sUserId = hisTradeData.getString("USER_ID");
        String sAcceptDate = hisTradeData.getString("ACCEPT_DATE");
        String serialNumber = hisTradeData.getString("SERIAL_NUMBER");
        String tradeEparchyCode = hisTradeData.getString("EPARCHY_CODE");//
        String cancelEparchyCode = databus.getString("TRADE_EPARCHY_CODE");

        IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(tradeTypeCode, tradeEparchyCode);
        // 不检查返销标志了，返销业务有从前台和接口发起的
        String sBackTag = tradeTypeInfo.getString("BACK_TAG");
        if (sBackTag.equals("1") || sBackTag.equals("2") || sBackTag.equals("3"))
        {
            // 允许返销：
            // 1的允许返销时间由td_s_commpara表中param_attr=1002的para_code1配置
            // 2只允许当日返销
            // 3只允许当月返销
        }
        else
        {
            return false;
        }

        // 异地受理标志检查
        if (tradeTypeInfo.getString("EXTEND_TAG").equals("0"))
        {
            if (!StringUtils.equals(tradeEparchyCode, cancelEparchyCode))
            {
                String errInfo = "此类型业务不允许异地返销！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751121, errInfo);
                return true;
            }
        }

        // 3.隔笔返销检查
        // 有未完工的业务校验
        IDataset unFinishTrade = TradeInfoQry.getMainTradeByUserId(sUserId);
        if (IDataUtil.isNotEmpty(unFinishTrade))
        {
            String errInfo = "业务返销校验-此用户有尚未完工的业务，此笔业务无法返销！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751122, errInfo);
            return true;
        }
        
        /**
         * REQ201602140004下岛问题优化--资源管理相关优化
         * 批量预开户不对隔笔返销进行校验，但要求必须是未激活用户
         * */
        IData userInfoData = UcaInfoQry.qryUserInfoByUserId(sUserId);
        String removeTag=userInfoData.getString("REMOVE_TAG");//要求正常REMOVE_TAG=0
        String openMode=userInfoData.getString("OPEN_MODE");//要求预开未返单=1
        String acctTag=userInfoData.getString("ACCT_TAG");//要求待激活=2
        if("500".equals(tradeTypeCode)&&"0".equals(removeTag) && "1".equals(openMode) && "2".equals(acctTag)){
        	return false;
        }else{
        
        /**
         * 批量预开户--end
         * */
        
	        IData tempParam = new DataMap();
	        // 同类业务隔笔返销
	        tempParam.put("SERIAL_NUMBER", serialNumber);
	        tempParam.put("TRADE_TYPE_CODE", tradeTypeCode);
	        tempParam.put("TRADE_EPARCHY_CODE", "");
	        IDataset sameTradeTypeDataset = Dao.qryByCode("TF_BH_TRADE", "SEL_BACKINFO_BY_SN", tempParam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	        if (IDataUtil.isNotEmpty(sameTradeTypeDataset))
	        {
	            if (!StringUtils.equals(tradeTypeCode, "240"))
	            {
	                if (!StringUtils.equals(tradeId, sameTradeTypeDataset.getData(0).getString("TRADE_ID")))
	                {
	                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "", "指定返销的不是此用户同种业务的最后一笔，不能隔笔返销！");
	                    return true;
	                }
	            }
	        }
	        else
	        {
	            CSAppException.apperr(TradeException.CRM_TRADE_95, "获取此用户最后一笔业务数据失败！");
	        }

	        // 不同业务隔笔返销
	        IDataset dataSet = new DatasetList();
	        IData param = new DataMap();
	        param.put("SERIAL_NUMBER", serialNumber);
	        param.put("ACCEPT_DATE", sAcceptDate);
	        dataSet = Dao.qryByCode("TF_BH_TRADE", "SEL_CHECK_TRADE_ATTR", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	        if (IDataUtil.isNotEmpty(dataSet))
	        {
	            int iTradeTypeCodeNew = dataSet.getData(0).getInt("TRADE_TYPE_CODE");
	            //if (!(iTradeTypeCodeNew == 431 || iTradeTypeCodeNew == 432 || iTradeTypeCodeNew == 433 || iTradeTypeCodeNew == 110 || iTradeTypeCodeNew == 141))
	            if(iTradeTypeCodeNew == 100)
	            {
	                StringBuilder errInfo = new StringBuilder(300);
	                errInfo.append("此业务之后存在办理时间为[");
	                errInfo.append(dataSet.getData(0).getString("ACCEPT_DATE", ""));
	                errInfo.append("]，流水号为[");
	                errInfo.append(dataSet.getData(0).getString("TRADE_ID", ""));
	                errInfo.append("]的");
	                errInfo.append(dataSet.getData(0).getString("TRADE_TYPE", ""));
	                errInfo.append("业务，此业务不能返销！");
	                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751123, errInfo.toString());
	                return true;
	            }
	        }

	        // 业务类型限制
	        dataSet.clear();
	        param.clear();
	        param.put("SERIAL_NUMBER", serialNumber);
	        param.put("ACCEPT_DATE", sAcceptDate);
	        param.put("TRADE_TYPE_CODE", tradeTypeCode);
	        param.put("TRADE_ID", tradeId);
	        dataSet = Dao.qryByCode("TF_BH_TRADE", "SEL_CHECK_TRADE_CANCEL_LIMIT2", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	        if (IDataUtil.isNotEmpty(dataSet))
	        { 
	            StringBuilder errInfo = new StringBuilder(300);
	            errInfo.append("此业务之后存在办理时间为[");
	            errInfo.append(dataSet.getData(0).getString("ACCEPT_DATE", ""));
	            errInfo.append("]，流水号为[");
	            errInfo.append(dataSet.getData(0).getString("TRADE_ID", ""));
	            errInfo.append("]的");
	            errInfo.append(dataSet.getData(0).getString("TRADE_TYPE", ""));
	            errInfo.append("业务，此业务不能返销！");
	            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751124, errInfo.toString());
	            return true;
	        }
	        
	        /**
	         * 240营销活动 之后存在110，153的业务要判断是否与当前用户的优惠有依赖
	         * 1、查询240业务之后是否存在110，153业务。
	         * 2、如果存在，则获取该240办理的优惠、服务信息（可能是新增、修改、删除的）
	         * 3、对比当前的用户服务、优惠信息，传给PM_OFFER_REL表看是否依赖。这点要确认怎么匹配依赖，如果是删除了某个优惠，与现有的有互斥关系怎么办？
	         * */
	        IData params = new DataMap();
	        params.put("USER_ID", sUserId);
	        params.put("SERIAL_NUMBER", serialNumber);
	        params.put("ACCEPT_DATE", sAcceptDate);
	        params.put("TRADE_ID", tradeId);
	        IDataset dataSets240 = Dao.qryByCode("TF_BH_TRADE", "SEL_BRE_TRADE_BY_SN_ID", params, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
	        //当前要返销240业务，且该业务之后存在110，153等业务才进行处理
	        if("240".equals(tradeTypeCode) && dataSets240!=null && dataSets240.size()>0){
	        	//查询要返销的活动的优惠
	        	IDataset tradeDiscnts=TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
	        	for(int j=0; j < tradeDiscnts.size(); j++){
	        		String tradeDiscntCode=tradeDiscnts.getData(j).getString("DISCNT_CODE");
	        		String modifyTag=tradeDiscnts.getData(j).getString("MODIFY_TAG");
	        		if("0".equals(modifyTag)){
	        			
	        			params.put("DISCNT_CODE", tradeDiscntCode);
	        			//查询用户有效的优惠
	        			IDataset userDiscs = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_ALL_DISCNT_BY_ID", params );
	        			for(int v=0; v<userDiscs.size(); v++){
	        				String userDiscntCode=userDiscs.getData(v).getString("DISCNT_CODE");
	        				IDataset relSet=UpcCall.qryOfferRelsByOfferCode1Code2(userDiscntCode,tradeDiscntCode,"D","1");
	        				if(relSet!=null && relSet.size()>0){
	        					StringBuilder errInfo = new StringBuilder(300);
	        			        errInfo.append("营销活动业务，该业务不能返销！[用户当前优惠<"+userDiscntCode+">依赖于要返销的优惠<"+tradeDiscntCode+">]");
	        			        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751125, errInfo.toString());
	        			        return true;   
	        				}
	        			}
	        			//查询要返销的活动的服务SVC 必须去掉本次要返销的营销活动新增的优惠 否则有可能自己跟自己依赖被拦截
	    	        	IDataset tradeSvcs=TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
	    	        	for (int u=0; u < tradeSvcs.size(); u++){
	    	        		String tradeSvcId=tradeSvcs.getData(u).getString("SERVICE_ID");
	    	        		String modTag=tradeSvcs.getData(u).getString("MODIFY_TAG");
	    	        		if("0".equals(modTag)){
			        			//查询用户有效的优惠 当前用户的服务有可能依赖于要返销的优惠
	    	        			IData svcInput=new DataMap();
	    	        			svcInput.put("USER_ID", sUserId);
	    	        			svcInput.put("SERVICE_ID", tradeSvcId);
			        			IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_ALL_SVC_BY_ID", svcInput );
			        			for(int p=0; p<userSvcs.size(); p++){
			        				String userSvcId=userSvcs.getData(p).getString("SERVICE_ID");
			        				IDataset relSet=UpcCall.qryOfferRelsByOfferCode1Code2(userSvcId,tradeDiscntCode,"S","1");
			        				if(relSet!=null && relSet.size()>0){
			        					StringBuilder errInfo = new StringBuilder(300);
			        			        errInfo.append("该营销活动业务不能返销！[用户当前服务<"+userSvcId+">依赖于要返销的优惠<"+tradeDiscntCode+">]");
			        			        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751125, errInfo.toString());
			        			        return true; 
			        				}
			        			}
		    	        	}
	    	        	}
	    	        	//如果用户只有优惠返销，没有服务返销，那么就只判断用户当前的服务与要返销的优惠之间的依赖关系
	    	        	if(tradeSvcs==null || tradeSvcs.size()==0){
	    	        		//查询用户有效的优惠 当前用户的服务有可能依赖于要返销的优惠
    	        			IData svcInput=new DataMap();
    	        			svcInput.put("USER_ID", sUserId);
    	        			svcInput.put("SERVICE_ID", "-1");
		        			IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_ALL_SVC_BY_ID", svcInput );
		        			for(int p=0; p<userSvcs.size(); p++){
		        				String userSvcId=userSvcs.getData(p).getString("SERVICE_ID");
		        				IDataset relSet=UpcCall.qryOfferRelsByOfferCode1Code2(userSvcId,tradeDiscntCode,"S","1");
		        				if(relSet!=null && relSet.size()>0){
		        					StringBuilder errInfo = new StringBuilder(300); 
		        			        errInfo.append("此业务不能返销！[用户当前服务<"+userSvcId+">依赖于要返销的优惠<"+tradeDiscntCode+">]");
		        			        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751125, errInfo.toString());
		        			        return true; 
		        				}
		        			}
	    	        	}
	        		}
	        	}
	        	//查询要返销的活动的服务SVC
	        	IDataset tradeSvcs=TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
	        	for (int j=0; j < tradeSvcs.size(); j++){
	        		String tradeSvcId=tradeSvcs.getData(j).getString("SERVICE_ID");
	        		String modifyTag=tradeSvcs.getData(j).getString("MODIFY_TAG");
	        		if("0".equals(modifyTag)){
	        			params.put("SERVICE_ID", tradeSvcId);
	        			//查询用户有效的优惠
	        			IDataset userSvcs = Dao.qryByCode("TF_F_USER_SVC", "SEL_USER_ALL_SVC_BY_ID", params );
	        			for(int v=0; v<userSvcs.size(); v++){
	        				String userSvcId=userSvcs.getData(v).getString("SERVICE_ID");
	        				IDataset relSet=UpcCall.qryOfferRelsByOfferCode1Code2(userSvcId,tradeSvcId,"S","1");
	        				if(relSet!=null && relSet.size()>0){
	        					StringBuilder errInfo = new StringBuilder(300); 
	        			        errInfo.append("营销活动业务不能返销！[用户当前服务<"+userSvcId+">依赖于要返销的服务<"+tradeSvcId+">]");
	        			        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751125, errInfo.toString());
	        			        return true; 
	        				}
	        			}
	        			//查询返销的活动的优惠,去掉这些优惠，否则存在依赖会造成自己拦截自己
	    	        	IDataset tradeDiscs=TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
	    	        	for(int r=0; r < tradeDiscs.size(); r++){
	    	        		String tradeDiscCode=tradeDiscs.getData(r).getString("DISCNT_CODE");
	    	        		String modTag=tradeDiscs.getData(r).getString("MODIFY_TAG");
	    	        		if("0".equals(modTag)){
	    	        			IData discInput=new DataMap();
	    	        			discInput.put("USER_ID", sUserId);
	    	        			discInput.put("DISCNT_CODE", tradeDiscCode);
			        			//查询用户有效的优惠 当前用户的优惠可能会依赖于要返销的服务
			        			IDataset userDiscs = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_ALL_DISCNT_BY_ID", discInput );
			        			for(int v=0; v<userDiscs.size(); v++){
			        				String userDiscntCode=userDiscs.getData(v).getString("DISCNT_CODE");
			        				IDataset relSet=UpcCall.qryOfferRelsByOfferCode1Code2(userDiscntCode,tradeSvcId,"D","1");
			        				if(relSet!=null && relSet.size()>0){
			        					StringBuilder errInfo = new StringBuilder(300); 
			        			        errInfo.append("营销活动业务不能返销！[用户当前优惠<"+userDiscntCode+">依赖于要返销的服务<"+tradeSvcId+">]");
			        			        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751125, errInfo.toString());
			        			        return true;   
			        				}
			        			}
	    	        		}
	    	        	}
	    	        	//如果用户只有服务返销，没有优惠返销，那么就只判断用户当前的优惠与要返销的服务之间的依赖关系
	    	        	if(tradeDiscs== null || tradeDiscs.size()==0){
	    	        		IData discInput=new DataMap();
    	        			discInput.put("USER_ID", sUserId);
    	        			discInput.put("DISCNT_CODE", "-1");
		        			//查询用户有效的优惠 当前用户的优惠可能会依赖于要返销的服务
		        			IDataset userDiscs = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_USER_ALL_DISCNT_BY_ID", discInput );
		        			for(int v=0; v<userDiscs.size(); v++){
		        				String userDiscntCode=userDiscs.getData(v).getString("DISCNT_CODE");
		        				IDataset relSet=UpcCall.qryOfferRelsByOfferCode1Code2(userDiscntCode,tradeSvcId,"D","1");
		        				if(relSet!=null && relSet.size()>0){
		        					StringBuilder errInfo = new StringBuilder(300); 
		        			        errInfo.append("营销活动业务不能返销！[用户当前优惠<"+userDiscntCode+">依赖于要返销的服务<"+tradeSvcId+">]");
		        			        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751125, errInfo.toString());
		        			        return true;   
		        				}
		        			}
	    	        	}
	        		}
	        	}
	        }
	    }
        return false;
    } 
}
