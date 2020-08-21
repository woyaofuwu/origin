package com.asiainfo.veris.crm.order.soa.person.common.action.trade.relation;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.GRPValueCardMgrSVC;

public class SPECIALUUDiscntUIAAction implements ITradeAction {

	private static transient Logger logger = Logger.getLogger(SPECIALUUDiscntUIAAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		int flagDelNum = 0;//计算取消套餐个数
		int flagAddNum = 0;//计算取消套餐个数
		String userIdA= "";
		String relationTypeCode= "";
		String endTime = SysDateMgr.getSysTime();
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		if (discntTrades != null && discntTrades.size() > 0) {			
			for (DiscntTradeData discntTrade : discntTrades) {
				String elementId = discntTrade.getElementId();
				String userId = discntTrade.getUserId();
				
				//查询是否配置的特殊套餐
	            IDataset commparas = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "1788", "SPECIALUU", elementId, elementId, "0898");
	            //不存在继续下一条
	            if(IDataUtil.isEmpty(commparas))
	            {
	            	continue;
	            }
	            
	            //为了代码公用，都是读取配置，以防万一给默认值
	            userIdA = commparas.getData(0).getString("PARA_CODE3","9999999920180926");
	            relationTypeCode = commparas.getData(0).getString("PARA_CODE4","XJ");
	            String timeFlag = commparas.getData(0).getString("PARA_CODE5","");
	            //存在，判断是否已经有关系
	            IDataset relaUU = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(userId, relationTypeCode);
	            
				if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
					
					if(IDataUtil.isEmpty(relaUU) && flagAddNum == 0)
					{
						RelationTradeData rtd = new RelationTradeData();

				        rtd.setUserIdA(userIdA);
				        rtd.setUserIdB(userId);
				        rtd.setSerialNumberA(relationTypeCode+"20180926");
				        rtd.setInstId(SeqMgr.getInstId());
				        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
				        rtd.setRelationTypeCode(relationTypeCode);
				        rtd.setSerialNumberB(btd.getRD().getUca().getSerialNumber());
				        rtd.setRoleCodeA("0");
				        rtd.setRoleCodeB("2");// 2表示副卡
				        rtd.setOrderno("0");
				        rtd.setStartDate(discntTrade.getStartDate());
				        rtd.setEndDate(SysDateMgr.getTheLastTime());
				        btd.add(btd.getRD().getUca().getSerialNumber(), rtd);
				        flagAddNum = 1;//始终保持一条
					}else{
						flagAddNum = 1;//始终保持一条
					}
					
					discntTrade.setUserIdA(userIdA);
				}
				
				if (BofConst.MODIFY_TAG_DEL.equals(discntTrade.getModifyTag())) {
					//可能会存在多个套餐都是同学网的套餐，关系只有一条
					flagDelNum = flagDelNum + 1;  
					if("1".equals(timeFlag))
					{
						endTime = SysDateMgr.getAddMonthsLastDay(1);
					}
				}
			}
			
			//下面这段主要是删除，所以根据计数标志处理
			//如果已经存在关系，预约的也是需要新增的,需要减去原有数
			if(0 != flagDelNum - flagAddNum)
			{
				//判断同学网套餐个数都取消才截止关系
				IDataset relaUUALL = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(btd.getRD().getUca().getUserId(), relationTypeCode);
				IDataset userDiscnt = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(btd.getRD().getUca().getUserId(), userIdA);
				if(IDataUtil.isNotEmpty(relaUUALL) && flagDelNum == userDiscnt.size())
				{
					RelationTradeData oldRelationUU = new RelationTradeData(relaUUALL.getData(0));
			        oldRelationUU.setModifyTag(BofConst.MODIFY_TAG_DEL);
			        oldRelationUU.setEndDate(endTime);
			        btd.add(btd.getRD().getUca().getSerialNumber(), oldRelationUU);
				}
			}
			
		}
	}
}
