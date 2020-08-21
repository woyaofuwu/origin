package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreexchange.order.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ScoreTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class DealGoodsAction2 implements ITradeAction {
	private static Logger log = Logger.getLogger(DealGoodsAction2.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		//TF_B_TRADE_SCORE":{"ACTION_COUNT":"1","SCORE":"2977908","USER_ID":"1119031239692653","ID_TYPE":"0","RSRV_STR9":null,"RSRV_STR7":null,"RSRV_STR8":null,"START_CYCLE_ID":"-1","SERIAL_NUMBER":"13876620987","GOODS_NAME":"流量解速包","VALUE_CHANGED":"0","RES_ID":"84025852","REMARK":"积分兑换","YEAR_ID":"ZZZZ","RULE_ID":"84025852","SCORE_CHANGED":"-1667","END_CYCLE_ID":"-1","RSRV_STR5":null,"RSRV_STR6":null,"RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":"3","RSRV_STR10":null,"RSRV_STR2":null,"SCORE_TYPE_CODE":"ZZ","CANCEL_TAG":"0","SCORE_TAG":"1"}
		//TF_B_TRADE_DISCNT":{"USER_ID_A":"-1","MODIFY_TAG":"2","RSRV_NUM4":null,"RSRV_NUM5":null,"END_DATE":"2019-12-31 23:59:59","RSRV_NUM1":null,"USER_ID":"1119031239692653","RSRV_NUM2":null,"RSRV_NUM3":null,"INST_ID":"1119050713700996","REMARK":null,"IS_NEED_PF":"","START_DATE":"2019-05-07 10:21:03","PRODUCT_ID":null,"RSRV_DATE3":null,"OPER_CODE":"","RSRV_DATE2":null,"RSRV_DATE1":null,"RSRV_STR5":null,"RSRV_STR3":null,"RSRV_STR4":null,"RSRV_STR1":null,"RSRV_STR2":null,"DISCNT_CODE":"84025852","CAMPN_ID":null,"SPEC_TAG":"0","RELATION_TYPE_CODE":"0","RSRV_TAG2":null,"PACKAGE_ID":null,"RSRV_TAG3":null,"RSRV_TAG1":null}

		/*
		 * 80130483 语音叠加包(50分钟国内主叫) 80130482 语音叠加包(100分钟国内主叫) 80130481
		 * 流量解速包(积分兑换)
		 */
		UcaData uca = btd.getRD().getUca();
		log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx29 " + btd);

		List<ScoreTradeData> scoreTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SCORE);

		log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx33 " + scoreTDList);

		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);// 获取优惠子台帐
		log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx41 " + discntTrades);
		String cursystime = SysDateMgr.getSysTime();
		List<DiscntTradeData> addCloneList = new ArrayList<DiscntTradeData>();
		
		for (int i = 0; i < scoreTDList.size(); i++) {
			ScoreTradeData std = scoreTDList.get(i);
			String res_id = std.getResId();// 优惠编码
			int actionCount = Integer.parseInt(std.getActionCount());
			log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx40 " + res_id);

			// String modifytag = std.getModifyTag();// 优惠编码

			if ("80130481".equals(res_id)) {// 生产环境
			// if ("84025852".equals(res_id) ) {//测试环境
				
				// 流量解速包（积分兑换）未限速问题  start
				
				IData inParamNew = new DataMap();
				inParamNew.put("USER_ID", uca.getUserId());
				inParamNew.put("USRIDENTIFIER", "86"+uca.getSerialNumber());
				inParamNew.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
				IDataset result= PCCBusinessQry.qryPccOperationTypeForSubscriber(inParamNew);
				if(IDataUtil.isEmpty(result)){
					result= PCCBusinessQry.qryPccHOperationTypeForSubscriber(inParamNew);
				}
				if (IDataUtil.isNotEmpty(result)) {
					String usrStatus = result.getData(0).getString("USR_STATUS", "");
					String execState = result.getData(0).getString("EXEC_STATE", "");
					//usrStatus 1为解速标识；2、3、4、6、8为限速标识
					//execState 处理状态 0-入库、1-处理中、2-处理完成、9-处理失败
					
					if ("1".equals(usrStatus) && "2".equals(execState)) {
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务检查：此用户不是限速状态，不能订购解速包！");
					}
				}else {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务检查：此用户不是限速状态，不能订购解速包！");
				}
				
				// 流量解速包（积分兑换）未限速问题  end

				// 如果没有GPRS服务，则不允许办理
				String userId = btd.getRD().getUca().getUserId();
				IDataset ds = UserSvcInfoQry.qryUserSvcByUserSvcId(userId, "22");// 22
				log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx50 " + ds);

				// GPRS服务
				if (ds == null || ds.size() == 0) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "未开通GPRS服务，不能办理！");
				}
			}

			if ("80130481,80130482,80130483,".indexOf(res_id + ",") != -1) {// 生产环境
			// if ("84025852,84025856,84025855,".indexOf(res_id) != -1) {//测试环境

				String lastday = SysDateMgr.getLastDateThisMonth().trim();// 2019-05-31// 23:59:59
				log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx66 " + lastday);

				String curday = SysDateMgr.getSysDate().trim();// 2019-05-08
				log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx70 " + curday);

				if (curday.equals(lastday.substring(0, 10))) {// 今天刚好是本月最后一天
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "当月最后一天，不能办理！");
				}

				for (int j = 0; j < discntTrades.size(); j++) {
					DiscntTradeData discntTrade = discntTrades.get(j);
					String discntcode = discntTrade.getDiscntCode();
					if (res_id.equals(discntcode)) {
						discntTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
						discntTrade.setInstId(SeqMgr.getInstId());
						discntTrade.setStartDate(cursystime);
						discntTrade.setEndDate(lastday);
						log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx86 " + discntTrade);

						if (actionCount > 1) {// 领取多份
							for (int k = 0; k < actionCount - 1; k++) {
								DiscntTradeData discntTradeClone = discntTrade.clone();
								discntTradeClone.setInstId(SeqMgr.getInstId());
								log.error("DealGoodsAction2xxxxxxxxxxxxxxxxxxxxxxx102 " + discntTradeClone);
								addCloneList.add(discntTradeClone);							
							}
						}
						break;
					}
				}
			}
		}

		if (addCloneList.size() > 0) {
			for (int i = 0; i < addCloneList.size(); i++) {
				btd.add(uca.getSerialNumber(), addCloneList.get(i));
			}
		}
	}

}
