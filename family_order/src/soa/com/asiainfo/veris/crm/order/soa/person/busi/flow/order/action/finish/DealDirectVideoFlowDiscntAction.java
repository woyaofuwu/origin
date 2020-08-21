package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.action.finish;


import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


public class DealDirectVideoFlowDiscntAction implements ITradeAction
{
	private final Logger log = Logger.getLogger(DealDirectVideoFlowDiscntAction.class);
	/**
	 * lisw3
	 */
	
	public void executeAction(BusiTradeData btd) throws Exception
	{
		saveChannelInfo(btd);//保存渠道编码和流水号
		if (log.isDebugEnabled())
    	{
    		log.debug("=======正在执行DealDirectVideoFlowDiscnt=====================");
    	}

		String oid=btd.getRD().getPageRequestData().getString("OID","");
		String tid=btd.getRD().getPageRequestData().getString("TID","");
		String channelTag=btd.getRD().getPageRequestData().getString("NEED_CHANNEL_TAG","");
		if (log.isDebugEnabled())
    	{
    		log.debug("=======正在执行DealDirectVideoFlowDiscnt 《《oid》》=====================是"+oid);
    		log.debug("=======正在执行DealDirectVideoFlowDiscnt 《《tid》》=====================是"+tid);
    		log.debug("=======正在执行DealDirectVideoFlowDiscnt 《《channelTag》》=====================是"+channelTag);

    	}
		List<DiscntTradeData> tradeDiscnts =btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		
		if (log.isDebugEnabled())
    	{
    		log.debug("=======正在执行DealDirectVideoFlowDiscnt 《《tradeDiscnts》》=====================是"+tradeDiscnts);
    	}
		if(tradeDiscnts==null){
			return;
		}
		for (int i = 0; i < tradeDiscnts.size(); i++) {
			DiscntTradeData tradeDiscntItem = tradeDiscnts.get(i);
			if (log.isDebugEnabled())
	    	{
	    		log.debug("=======正在执行DealDirectVideoFlowDiscnt 《《tradeDiscntItem.getDiscntCode()》》=====================是"+tradeDiscntItem.getDiscntCode());
	    	}
			//如果是定向视频流量年包，半年包类的自动失效的
			IDataset paraList = CommparaInfoQry.getCommparaByCode1("CSM", "2017", tradeDiscntItem.getDiscntCode(), "IS_VIDEO_PKG",null);
			if(IDataUtil.isEmpty(paraList)){
				continue; 
			}
			String inModeCode=btd.getMainTradeData().getInModeCode();
			if (log.isDebugEnabled())
	    	{
	    		log.debug("=======正在执行DealDirectVideoFlowDiscnt 《《inModeCode》》=====================是"+inModeCode);
	    	}
			if("6".equals(inModeCode)){
				tradeDiscntItem.setRsrvStr3("03");//渠道来源：01能开，02营业厅，03其他渠道，04一级电渠 
				if("ABILITY".equals(channelTag)){//销售订单信息接口过来的及能开渠道
					tradeDiscntItem.setRsrvStr3("01");//渠道来源：01能开，02营业厅，03其他渠道，04一级电渠 
					tradeDiscntItem.setRsrvStr4(tid);
					tradeDiscntItem.setRsrvStr5(oid);
				}else if("UMMP".equals(channelTag)){//移动商城业务办理接口过来的
					tradeDiscntItem.setRsrvStr3("04");//渠道来源：01能开，02营业厅，03其他渠道，04一级电渠 
				}
			}else if("0".equals(inModeCode)){
				tradeDiscntItem.setRsrvStr3("02");//渠道来源：01能开，02营业厅，03其他渠道，04一级电渠 
			}
		}
	}
	public static void saveChannelInfo(BusiTradeData btd) throws Exception{
		List<SvcTradeData> tradeSvcs =btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		if(tradeSvcs==null){
			return;
		}
		boolean flag=false;
		String xjldTag=StaticUtil.getStaticValue("HARASSMENT_CALL_SVC", "1003");//配置了则为谢绝来电服务
		for (int i = 0; i < tradeSvcs.size(); i++) {
			SvcTradeData tradeSvcItem = tradeSvcs.get(i);
			if(StringUtils.equals(xjldTag, tradeSvcItem.getElementId())){//如果是谢绝来电的服务
				flag=true;
				break;
			}
		}
		if(flag){//走产品变更接口过来的，如果是谢绝来电的服务则保存一下两个字段给服开
			String sqid=SeqMgr.getInstId();
			String inModeCode=btd.getMainTradeData().getInModeCode();
			String channelId=StaticUtil.getStaticValue("CHANNEL_RELATION",inModeCode);
			btd.getMainTradeData().setRsrvStr8(channelId);//渠道编码
			btd.getMainTradeData().setRsrvStr6("COP898"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+sqid.substring(sqid.length()-12));//操作流水
		}
		String channelTag=btd.getRD().getPageRequestData().getString("NEED_CHANNEL_TAG","");
		IDataset staticList=StaticUtil.getStaticList("NEED_CHANNEL_TAG",channelTag);
		if(IDataUtil.isNotEmpty(staticList)){//接口过来的，如果配置了需要保存渠道标识，则保存渠道编码
			btd.getMainTradeData().setRsrvStr8(btd.getRD().getPageRequestData().getString("CHANNEL_ID",""));//渠道编码
			btd.getMainTradeData().setRsrvStr6(btd.getRD().getPageRequestData().getString("OPR_NUMB",""));//操作流水
		}
	}
}
