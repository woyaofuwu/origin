package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend.PlatSvcTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;


public class BindPlatSvcAction implements ITradeAction{

	public void executeAction(BusiTradeData btd) throws Exception
    {
		    UcaData uca = btd.getRD().getUca();
			String serialNumber=uca.getSerialNumber();
			String userId=uca.getUserId();
			String tradeId =btd.getMainTradeData().getRsrvStr10();
			IDataset platSvcTrades = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId,"TOPSET_PLATSVC");
			if (IDataUtil.isNotEmpty(platSvcTrades))
	        {
				for(int i=0;i<platSvcTrades.size();i++){
					String serviceId = platSvcTrades.getData(i).getString("RSRV_STR3");
					if(StringUtils.isBlank(serviceId)){
						break;
					}
					
					PlatSvcTradeData newPstd = new PlatSvcTradeData();
		            newPstd.setElementId(serviceId);
		            newPstd.setModifyTag(BofConst.MODIFY_TAG_ADD);
		            newPstd.setUserId(userId);
		            newPstd.setBizStateCode(PlatConstants.STATE_OK);
		            newPstd.setProductId(PlatConstants.PRODUCT_ID);
		            newPstd.setPackageId(PlatConstants.PACKAGE_ID);
		            newPstd.setStartDate(btd.getRD().getAcceptTime());
		            newPstd.setEndDate(SysDateMgr.END_DATE_FOREVER);
		            String instId = SeqMgr.getInstId();
		            newPstd.setInstId(instId);
		            newPstd.setActiveTag("");// 主被动标记
		            newPstd.setOperTime(SysDateMgr.getSysTime());
		            newPstd.setPkgSeq("");// 批次号，批量业务时传值
		            newPstd.setUdsum("");// 批次数量
		            newPstd.setIntfTradeId("");
		            newPstd.setOperCode(PlatConstants.OPER_ORDER);
		            newPstd.setOprSource("08");
		            newPstd.setIsNeedPf("1");
		            PlatSvcTrade.dealFirstTime(newPstd, uca); // 处理首次订购时间，连带开的可能首次订购时间为空
		            newPstd.setRemark("根据ohter办理");
		            btd.add(serialNumber, newPstd);
		            
		            //根据服务绑定减免套餐
		            IDataset commparaInfos2509 = CommparaInfoQry.getCommparaByCode4to6("CSM","2509","TOPSETBOX",serviceId,null,null,"0898");
		            if (IDataUtil.isNotEmpty(commparaInfos2509))
			        {
		            	String discntCode = commparaInfos2509.first().getString("PARA_CODE5");
		            	int months = Integer.parseInt(commparaInfos2509.first().getString("PARA_CODE6","18"));//优惠偏移时间（单位月）
		            	if(StringUtils.isNotBlank(discntCode)){
		            		DiscntTradeData newDiscnt = new DiscntTradeData();
				            newDiscnt.setUserId(userId);
				            newDiscnt.setProductId("-1");
				            newDiscnt.setPackageId("-1");
				            newDiscnt.setElementId(discntCode);
				            newDiscnt.setInstId(SeqMgr.getInstId());
				            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
				            newDiscnt.setStartDate(SysDateMgr.getSysTime());
				            newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDay(months,SysDateMgr.getSysTime()));
				            newDiscnt.setRemark("根据配置后台绑定优惠,配置类型为2509");
				            btd.add(uca.getSerialNumber(), newDiscnt);	
		            	}
		            	
			        }
		            
				}
				
	        }
			
			
		
    }
	
	
}
