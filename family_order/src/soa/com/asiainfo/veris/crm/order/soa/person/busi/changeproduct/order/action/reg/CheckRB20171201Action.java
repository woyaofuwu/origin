package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import org.apache.log4j.Logger;


/**
 * 视频彩铃处理ACTION
 * @author MSI-
 *
 */
public class CheckRB20171201Action implements ITradeAction {
	private static transient Logger logger = Logger.getLogger(CheckRB20171201Action.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		Boolean isNeedAction = btd.getRD().isNeedAction();
		if(!isNeedAction){
			logger.debug(">>>>>>>>>>>>>VoLteAndColorRingAction 不需要执行action>>>>>>>");
			return;
		}
		//判断是否新开190服务 取消190服务
 		boolean isNewVoLte = false;
 		boolean isCancelVoLte = false;
 		
 		//判断是否新开20服务 取消20服务
 		//boolean isNew20 = false;
 		//boolean isCancel20 = false;
 		
		//判断是否新开20171201服务 取消20171201服务
 		boolean isNew20171201 = false;
 		boolean isCancel20171201 = false;
 		
 		List<SvcTradeData> svcTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		if (CollectionUtils.isNotEmpty(svcTrades) && svcTrades.size() > 0)
 		{
 			for (int i = 0; i < svcTrades.size(); i++) 
 			{
 				SvcTradeData svcTradeData = svcTrades.get(i);
 				if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && 
 					"190".equals(svcTradeData.getElementId()) && 
 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
				{
 					isNewVoLte = true;
 					continue;
				}
 				
 				if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && 
 					"190".equals(svcTradeData.getElementId()) && 
 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
				{
 					isCancelVoLte = true;
 					continue;
				}
 				
 				/*if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && 
 	 				"20".equals(svcTradeData.getElementId()) && 
 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
				{
 					isNew20 = true;
 					continue;
				}*/
 				
 				/*if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && 
 					"20".equals(svcTradeData.getElementId()) && 
 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
				{
 					isCancel20 = true;
 					continue;
				}*/
 				
 				if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && 
 					"20171201".equals(svcTradeData.getElementId()) && 
 					"S".equals(svcTradeData.getElementType()))
				{
 					isNew20171201 = true;
 					continue;
				}
 				
 				if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && 
 					"20171201".equals(svcTradeData.getElementId()) && 
 					"S".equals(svcTradeData.getElementType()))
				{
 					isCancel20171201 = true;
 					continue;
				}
 			}
 		}
		
		UcaData uca = btd.getRD().getUca();
		
		/*if(isNew20171201)
		{
			if(isNewVoLte)
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购视频彩铃，要先订购VOLTE。");
			}
			else if(isCancelVoLte)
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购视频彩铃，要含有VOLTE。。");
			}
			else 
			{
				boolean isHave190 = true;
		 		//boolean isHave20 = true;
		 		String strInfo190_20 = "订购视频彩铃，要先订购";
				List<SvcTradeData> userSvc190 = uca.getUserSvcBySvcId("190");
				if(CollectionUtils.isEmpty(userSvc190))
	 	 		{
					strInfo190_20 = strInfo190_20 + "VOLTE";
					isHave190 = false;
	 	 		}
				
				List<SvcTradeData> userSvc20 = uca.getUserSvcBySvcId("20");
				if(CollectionUtils.isEmpty(userSvc20))
	 	 		{
					if(!isHave190)
					{
						strInfo190_20 = strInfo190_20 + "和彩铃";
					}
					else
					{
						strInfo190_20 = strInfo190_20 + "彩铃";
					}
					isHave20 = false;
	 	 		}
				
				if(!isHave190)
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, strInfo190_20);
				}
			}
		}*/
		
		if((isCancelVoLte) && !isCancel20171201)
		{
			List<SvcTradeData> userSvc20171201 = uca.getUserSvcBySvcId("20171201");
			if(CollectionUtils.isNotEmpty(userSvc20171201))
 	 		{
				SvcTradeData SvcTrade = userSvc20171201.get(0).clone();
				SvcTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
				SvcTrade.setEndDate(SysDateMgr.getSysTime());
				SvcTrade.setRemark("用户取消VOLTE,同时取消视频彩铃");
				btd.add(uca.getSerialNumber(), SvcTrade);
 	 		}
		}
	}
}
