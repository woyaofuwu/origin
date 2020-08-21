package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.reg;



import java.util.List;

import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * 手机停开机连带无手机宽带。
 * @author xuejy
 *
 */
public class NophoneWideChangeSvcStateAction implements ITradeAction {
	
	private static transient Logger logger = Logger.getLogger(NophoneWideChangeSvcStateAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		if (logger.isDebugEnabled())
        {
            logger.debug("进入NophoneWideChangeSvcStateAction函数，手机停开机连带无手机宽带Action...Start!");
        }
		IDataset relaUUInfos = new DatasetList();
		//如果是宽带账号
		if(btd.getRD().getUca().getSerialNumber().startsWith("KD_")){
			//判断无手机or有手机宽带用户
			IData wideNetData = WideNetUtil.getWideNetTypeInfo(btd.getRD().getUca().getSerialNumber());

			//无手机宽带不能受理增值产品
			if ("Y".equals(wideNetData.getString("IS_NOPHONE_WIDENET")))
			{
				relaUUInfos = RelaUUInfoQry.getRelaUUInfoByUserIda(btd.getRD().getUca().getUserId(), "58",null);
			}else{
				relaUUInfos = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(btd.getRD().getUca().getUserId(), "58", "1");
			}
			//如果是手机号码
		}else{
			//判断手机号码有没有绑定宽带
			String serialNumber = btd.getRD().getUca().getSerialNumber();
			IDataset userInfo = UserInfoQry.getUserInfoBySn("KD_" + serialNumber,"0");
			if(IDataUtil.isEmpty(userInfo)){
				return;
			}
			//判断手机号码有没有绑定无手机宽带
			relaUUInfos = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(btd.getRD().getUca().getUserId(),"58");
			if(IDataUtil.isEmpty(relaUUInfos)){
				return;
			}
		}
		if(IDataUtil.isNotEmpty(relaUUInfos)){
			for(int i=0; i < relaUUInfos.size(); i++){
					IData relaUUInfo = relaUUInfos.getData(i);
					IData params = new DataMap();
					params.put("SERIAL_NUMBER", relaUUInfo.getString("SERIAL_NUMBER_A"));
					params.put("USER_ID", relaUUInfo.getString("USER_ID_A"));
					params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
					params.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
					//存在统一付费关系时 进行处理
					if(StringUtils.equals("131", btd.getTradeTypeCode()) || StringUtils.equals("136", btd.getTradeTypeCode()) || StringUtils.equals("132", btd.getTradeTypeCode()) || 
							StringUtils.equals("7101", btd.getTradeTypeCode()) || StringUtils.equals("7110", btd.getTradeTypeCode()) || 
							StringUtils.equals("7220", btd.getTradeTypeCode()) || StringUtils.equals("7240", btd.getTradeTypeCode()) || 
							StringUtils.equals("192", btd.getTradeTypeCode())){
						//停机 变更无手机宽带服务状态
						IDataset mainStates = UserSvcStateInfoQry.getUserMainState(relaUUInfo.getString("USER_ID_A"));
						String serviceId = "";
						if(IDataUtil.isNotEmpty(mainStates)){
							serviceId = mainStates.getData(0).getString("SERVICE_ID");
						}
						params.put("TRADE_TYPE_CODE", "683");
						if(StringUtils.equals("7220", btd.getTradeTypeCode())){
							//欠费停机
							params.put("STATE_CODE", "5");
						}else if(StringUtils.equals("136", btd.getTradeTypeCode()) && "2010".equals(serviceId)){
							//局方停机
							params.put("STATE_CODE", "4");
						}else if(StringUtils.equals("7110", btd.getTradeTypeCode()) && "2010".equals(serviceId)){
							//高额停机
							params.put("STATE_CODE", "7");
						}else{
							//申请停机
							params.put("STATE_CODE", "1");
						}
						IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", params);
					}else if(StringUtils.equals("133", btd.getTradeTypeCode()) || StringUtils.equals("126", btd.getTradeTypeCode()) || StringUtils.equals("492", btd.getTradeTypeCode()) || 
							StringUtils.equals("497", btd.getTradeTypeCode()) || StringUtils.equals("496", btd.getTradeTypeCode()) ||  
							StringUtils.equals("7301", btd.getTradeTypeCode()) ||  StringUtils.equals("310", btd.getTradeTypeCode()) || 
							StringUtils.equals("7303", btd.getTradeTypeCode())){
						//开机 变更无手机宽带服务状态
						params.put("TRADE_TYPE_CODE", "684");
						params.put("STATE_CODE", "0");
						IDataset result = CSAppCall.call("SS.ChangeWidenetSvcStateRegSVC.tradeReg", params);
					}else if(StringUtils.equals("685", btd.getTradeTypeCode()) || StringUtils.equals("687", btd.getTradeTypeCode())){
						//无手机宽带拆机
				    	RelationTradeData rtd = new RelationTradeData(relaUUInfo);
				        rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
				        rtd.setEndDate(btd.getRD().getAcceptTime());
				        
				        btd.add(btd.getRD().getUca().getSerialNumber(), rtd);
				        
				        IDataset payRelas = PayRelaInfoQry.getPayrelaByPayItemCode(btd.getRD().getUca().getUserId(),BofConst.NO_PHONE_WIDE_UNION_PAY_CODE);
						if (IDataUtil.isNotEmpty(payRelas)) {
							IData payRela = payRelas.getData(0);
							PayRelationTradeData payrelationTD = new PayRelationTradeData(payRela);
							payrelationTD.setStartCycleId(DiversifyAcctUtil.getFirstDayThisAcct(btd.getRD().getUca().getUserId()).substring(0, 10).replace("-", ""));
							payrelationTD.setEndCycleId(DiversifyAcctUtil.getLastDayThisAcct(btd.getRD().getUca().getUserId()).substring(0, 10).replace("-", ""));
							payrelationTD.setModifyTag(BofConst.MODIFY_TAG_DEL);

							btd.add(btd.getRD().getUca().getSerialNumber(), payrelationTD);
						}
					}else if(StringUtils.equals("689", btd.getTradeTypeCode())){
						List<PayRelationTradeData> payRelationTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PAYRELATION);
						if (payRelationTrades != null && payRelationTrades.size() > 0){
							 for(int j=0;j<payRelationTrades.size();j++){
								 PayRelationTradeData prtd=payRelationTrades.get(j);
								 if(BofConst.NO_PHONE_WIDE_UNION_PAY_CODE.equals(prtd.getPayitemCode())){
									 prtd.setEndCycleId(SysDateMgr.getNowCycle());
								 }
							 }
						}
					}
				}
			}
			
		if (logger.isDebugEnabled())
        {
            logger.debug("NophoneWideChangeSvcStateAction函数结束，手机停开机连带无手机宽带Action...End!");
        }
	}

}
