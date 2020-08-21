/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.person.common.action.trade.score;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
/***
 *
 */
public class TradeScoreDealAction implements ITradeAction
{
	static Logger log = Logger.getLogger(TradeScoreDealAction.class);
    /**
     * (non-Javadoc) 登记时处理积分台账
     *
     * @see com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction#executeAction(com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
        
        List<ScoreTradeData> scoreTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SCORE);
        List<SaleActiveTradeData> activeTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        BaseReqData req = btd.getRD();
        UcaData uca = btd.getRD().getUca();
        String tradeTypeCode = btd.getTradeTypeCode();
        String tradeId = btd.getTradeId();
        String userId = uca.getUserId();
        String yearId = "";
        String scoreTypeCode = "";
        int scoreChanged = 0;
        String preType = req.getPreType();
        String isConFirm = req.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;
        IDataset scoreInfos =new DatasetList();
        String rsrvStr3 = "0^0";
        IDataset scoreInfosPoint329=new DatasetList();
		IDataset scoreInfosTime329=new DatasetList();

		IData userScoreMoveITFInData = new DataMap();

        for (ScoreTradeData scoreTD : scoreTDList)
        {
            String scoreTag = scoreTD.getScoreTag();
            if ("0".equals(scoreTag))// 积分清零
            {
                AcctCall.cancelScoreValue(tradeTypeCode, tradeId, userId);
            }
            else if ("1".equals(scoreTag))// 积分变更
            {
                yearId = scoreTD.getYearId();
                scoreTypeCode = scoreTD.getScoreTypeCode();
                int myScore = Integer.parseInt(scoreTD.getScoreChanged());

                if ("340".equals(tradeTypeCode))
                {
                    String modUserId = scoreTD.getUserId();
                    if("6".equals(btd.getMainTradeData().getInModeCode())){
                    	if(myScore<0){
                    		 IDataset resultInfo= AcctCall.userScoreModifyIBoss(modUserId, yearId, scoreTypeCode, tradeTypeCode, myScore, tradeId);
							 scoreInfos = resultInfo.getData(0).getDataset("SCORE_INFO");
					         if(null!=scoreInfos&&!scoreInfos.isEmpty()){
						        btd.getMainTradeData().setRsrvStr9(resultInfo.getData(0).getString("OPERATE_TIME",""));
					         }
						 }else{
							 IDataset scoreInfosType=new DatasetList();
							 IDataset scoreInfosPoint=new DatasetList();
							 IDataset scoreInfosTime=new DatasetList();
							 for(int i=0;i<scoreInfos.size();i++){
				        		IData scoreInfo=scoreInfos.getData(i);
				        		scoreInfosType.add(scoreInfo.getString("SCORE_TYPE_CODE", ""));
				                scoreInfosPoint.add(scoreInfo.getString("SCORE_VALUE", ""));
				                scoreInfosTime.add(scoreInfo.getString("VALID_DATE", ""));
				                IData param = new DataMap();
				                param.put("END_DATE", scoreInfo.getString("VALID_DATE", ""));
				                scoreTypeCode=scoreInfo.getString("INTEGRAL_TYPE_CODE", "ZZ");
				                myScore=Integer.parseInt(scoreInfo.getString("SCORE_VALUE", "0"));
								 //调积分扣减转赠接口
				                AcctCall.userScoreModifyNew(modUserId, yearId, scoreTypeCode, tradeTypeCode, myScore, tradeId,param);
				        	 }
							 btd.getMainTradeData().setRsrvStr6(scoreInfosType.toString());
						     btd.getMainTradeData().setRsrvStr7(scoreInfosPoint.toString());
						     btd.getMainTradeData().setRsrvStr8(scoreInfosTime.toString());
						 }
					} else if ("5".equals(btd.getMainTradeData().getInModeCode())) {
						// 积分转赠-外围接口
						if ("ZS".equals(scoreTD.getRsrvStr6())) {
							userScoreMoveITFInData.put("SERIAL_NUMBER", scoreTD.getSerialNumber());
						} else if ("BZS".equals(scoreTD.getRsrvStr6())) {
							userScoreMoveITFInData.put("OBJECT_SERIAL_NUMBER", scoreTD.getSerialNumber());
						}
						userScoreMoveITFInData.put("DONATE_SCORE", scoreTD.getScoreChanged());
					 } else {
						// 调积分扣减转赠接口
	                    AcctCall.userScoreModify(modUserId, yearId, scoreTypeCode, tradeTypeCode, myScore, tradeId);
					 }
                }else if("341".equals(tradeTypeCode)){
                	String serialNumber = btd.getMainTradeData().getSerialNumber();
                	String objSerialNumber = btd.getMainTradeData().getRsrvStr5();
                	String donateScore = btd.getMainTradeData().getRsrvStr6();
                	String validDate = btd.getMainTradeData().getRsrvStr7();
                	String aTradeTypeCode = btd.getMainTradeData().getRsrvStr9();
                	String bTradeTypeCode = btd.getMainTradeData().getRsrvStr10();
                	AcctCall.userScoreModifyZC(userId, tradeId, scoreTypeCode, serialNumber, objSerialNumber, donateScore, validDate,aTradeTypeCode,bTradeTypeCode);
                }
                else if("329".equals(tradeTypeCode))
				 {
					 if("6".equals(btd.getMainTradeData().getInModeCode())){
						 if("032".equals(scoreTD.getRsrvStr6())){
							 //调积分回退冲正接口
							 IData param = new DataMap();
							 tradeId=btd.getMainTradeData().getRsrvStr9();
							 IDataset resultInfo=AcctCall.userScoreModifyRollback(userId, yearId, scoreTypeCode, tradeTypeCode, myScore, tradeId,param);
							 String COMSUME_SCORE = resultInfo.getData(0).getString("CONSUME_POINT","");
							 String PROMOTION_SCORE = resultInfo.getData(0).getString("PROMOTION_POINT","");
							 scoreInfos = resultInfo.getData(0).getDataset("SCORE_INFO");
							 IDataset scoreInfosPoint=new DatasetList();
							 IDataset scoreInfosTime=new DatasetList();
							 if(IDataUtil.isNotEmpty(scoreInfos) && scoreInfos.size()>0){
								 for(int i=0;i<scoreInfos.size();i++){
					        		IData scoreInfo=scoreInfos.getData(i);
					        		if(!"01".equals(scoreInfo.getString("SCORE_TYPE_CODE", ""))){
					                scoreInfosPoint.add(scoreInfo.getString("SCORE_VALUE", ""));
					                scoreInfosTime.add(scoreInfo.getString("VALID_DATE", ""));
					        		}
					        	 }
							 }
							 btd.getMainTradeData().setRsrvStr3(COMSUME_SCORE+"^"+PROMOTION_SCORE);
							 btd.getMainTradeData().setRsrvStr6(scoreInfosTime.toString());
							 btd.getMainTradeData().setRsrvStr7(scoreInfosPoint.toString());
						 }else if("033".equals(scoreTD.getRsrvStr6())){
							 //调积分冲正接口
							 AcctCall.scoreCancel(btd.getMainTradeData().getRsrvStr9());
						 }else if("040".equals(scoreTD.getRsrvStr6())){
							 //调积分转赠回退接口
							 AcctCall.scoreCancel(btd.getMainTradeData().getRsrvStr9());
						 }else{
							 IData param = new DataMap();
							 param.put("END_DATE", scoreTD.getRsrvStr3());
							 //调积分扣减转赠接口
							 IDataset resultInfo = new DatasetList();
							 if ("外部积分商城".equals(scoreTD.getGoodsName()))
							 {
								 resultInfo = AcctCall.userScoreModifyOutter(btd.getRD().getUca().getSerialNumber(), userId, yearId,scoreTypeCode, tradeTypeCode, myScore, tradeId,param);
							 }else{

								 resultInfo = AcctCall.userScoreModifyNew(userId, yearId, scoreTypeCode, tradeTypeCode, myScore, tradeId,param);
							 }
							 //报错抛出异常
							 if(IDataUtil.isNotEmpty(resultInfo) && resultInfo.size()>0){
								 String resultCode=resultInfo.getData(0).getString("X_RESULTCODE","");
								 if(!"0".equals(resultCode)){
									 CSAppException.apperr(CrmCommException.CRM_COMM_103,resultInfo.getData(0).getString("X_RESULTINFO",""));
								 }
							 }
							 if("031".equals(scoreTD.getRsrvStr6()) && IDataUtil.isNotEmpty(resultInfo)){
								 String COMSUME_SCORE = resultInfo.getData(0).getString("CONSUME_POINT","");
								 String PROMOTION_SCORE = resultInfo.getData(0).getString("PROMOTION_POINT","");
								 scoreInfos = resultInfo.getData(0).getDataset("SCORE_INFO");
								 //IDataset scoreInfosPoint=new DatasetList();
								 //IDataset scoreInfosTime=new DatasetList();
								 if(IDataUtil.isNotEmpty(scoreInfos) && scoreInfos.size()>0){
									 for(int i=0;i<scoreInfos.size();i++){
										 IData scoreInfo=scoreInfos.getData(i);
										 if(!"01".equals(scoreInfo.getString("SCORE_TYPE_CODE", ""))){
											 scoreInfosPoint329.add(scoreInfo.getString("SCORE_VALUE", ""));
											 scoreInfosTime329.add(scoreInfo.getString("VALID_DATE", ""));
										 }
									 }
								 }

								 if ("外部积分商城".equals(scoreTD.getGoodsName()))
								 {
									 //String rsrvStr3 = btd.getMainTradeData().getRsrvStr3();
									 String consume = rsrvStr3.split("\\^").length > 0 ? rsrvStr3.split("\\^")[0] : "0";
									 String promotin = rsrvStr3.split("\\^").length > 1 ? rsrvStr3.split("\\^")[1] : "0";
									 if("01".equals(scoreTD.getScoreTypeCode()))
									 {
										 consume = COMSUME_SCORE;
									 }
									 else if("02".equals(scoreTD.getScoreTypeCode()))
									 {
										 promotin = PROMOTION_SCORE;
									 }
									 else
									 {
										 consume = COMSUME_SCORE;
										 promotin = PROMOTION_SCORE;
									 }

									 rsrvStr3 = consume+"^"+promotin;
									 btd.getMainTradeData().setRsrvStr3(rsrvStr3);
								 }
								 else
								 {
									 btd.getMainTradeData().setRsrvStr3(COMSUME_SCORE+"^"+PROMOTION_SCORE);
								 }
								 btd.getMainTradeData().setRsrvStr6(scoreInfosTime329.toString());
								 btd.getMainTradeData().setRsrvStr7(scoreInfosPoint329.toString());
							 }
						 }
					 }
				 }
                //BUG20160114180406 关于营销活动赠送积分无法根据配置来设定有效期的BUG by songlm 20160201
                else if("240".equals(tradeTypeCode))
				 {
					 IData param = new DataMap();
					 param.put("END_DATE", scoreTD.getRsrvStr2());
					 //调积分扣减转赠new接口
					 /**
			          * REQ201603090003 关于新增集团客户回馈购机活动的需求（积分）  chenxy3 20160325
			          * */
					 IDataset callset=AcctCall.userScoreModifyNew(userId, yearId, scoreTypeCode, tradeTypeCode, myScore, tradeId,param);
					 if(callset!=null && callset.size()>0){
						 String resultCode=callset.getData(0).getString("X_RESULTCODE","");
						 if(!"0".equals(resultCode)){
							 CSAppException.apperr(CrmCommException.CRM_COMM_103,callset.getData(0).getString("X_RESULTINFO",""));
						 }
					}
				 }
                else
                {
                    scoreChanged = scoreChanged + myScore;
                }
            }
        }

        if ("340".equals(tradeTypeCode))
        {
        	if ("5".equals(btd.getMainTradeData().getInModeCode()))
        	{
        		if(!userScoreMoveITFInData.isEmpty())
        		{
        			// 积分转赠-外围接口
        			AcctCall.userScoreMoveITF(userScoreMoveITFInData);
        		}
        	}
        }

        if (scoreChanged != 0)
        {
            // 调积分扣减转赠接口
        	/**
	          * REQ201603090003 关于新增集团客户回馈购机活动的需求（积分）  chenxy3 20160325 
	          * */
        	IDataset callset=AcctCall.userScoreModify2(userId, yearId, scoreTypeCode, tradeTypeCode, scoreChanged, tradeId);
            if(callset!=null && callset.size()>0){
				 String resultCode=callset.getData(0).getString("X_RESULTCODE","");
				 if(!"0".equals(resultCode)){
					 CSAppException.apperr(CrmCommException.CRM_COMM_103,callset.getData(0).getString("X_RESULTINFO",""));
				 }
			}
        }
    }
}
