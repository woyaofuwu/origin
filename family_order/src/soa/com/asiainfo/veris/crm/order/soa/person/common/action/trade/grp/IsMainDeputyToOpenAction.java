package com.asiainfo.veris.crm.order.soa.person.common.action.trade.grp;


import org.apache.log4j.Logger;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 *  针对一号双终端副设备判断为：判断用户品牌编码为：MDRP
          判断是否欠费停机和欠费开机：业务类型欠费半停机7210、欠费停机7220，缴费开机7301
          主台账RSRV_STR5字段填充品牌的值MDRP,服开侧根据这个进行判断
 * @author zhouxin7
 *
 */
public class IsMainDeputyToOpenAction implements ITradeAction{

	private static transient final Logger logger = Logger.getLogger(IsMainDeputyToOpenAction.class);
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		String tradeTypeCode = btd.getTradeTypeCode();
		logger.debug("BUSI_ITEM_CODE+++++++++++++++"+tradeTypeCode);
		//欠费半停机7210、欠费停机7220，缴费开机7301
		if(StringUtils.isNotEmpty(tradeTypeCode)&&StringUtils.equals("7210",tradeTypeCode)||StringUtils.equals("7220",tradeTypeCode)||StringUtils.equals("7301",tradeTypeCode)){
			if(StringUtils.equals(btd.getRD().getUca().getBrandCode(),"MDRP")){//一号双终端副设备
				//主台账数据  RSRV_STR5字段填充品牌的值MDRP
				MainTradeData mainTrade = btd.getMainTradeData();
				mainTrade.setRsrvStr5("MDRP");
			}
		}
	}

}
