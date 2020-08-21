package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderRegist;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * @description 该类用于判断管理节点工单是否需要延时处理，主要用于多个变更单的情况
 * @author xunyl
 * @date 2015-01-29
 */
public class bbossMgrOrderDelayBean {
	
	/**
	 * @description 该方法用于判断管理节点工单是否需要延时处理，延时处理的场合更改处理状态(从正常处理更改为延迟处理)
	 * @author xunyl
	 * @date 2015-01-29
	 */
	public static boolean isNeedDelay(IData map) throws Exception {
		//1- 定义返回值
		boolean result = false;		
		
		//2- 报文体中获取产品订购关系编号和管理节点编号
		IDataset rsrvstr4Set = IDataUtil.getDatasetSpecl("RSRV_STR4", map); // 产品订购关系ID
		String productOfferingId = rsrvstr4Set.get(0).toString();
		String manageNode = map.getString("PR_MN_OPERATE_CODE");
		
		//3- 根据集团BBOSS的产品订购编号查询trade_grp_merchp表，获取台帐编号
		String tradeId = "";
		IDataset merchpInfoList = TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferingId, null);
		if(IDataUtil.isNotEmpty(merchpInfoList)){
			IData merchpInfo= merchpInfoList.getData(0);
			tradeId = merchpInfo.getString("TRADE_ID");
		}				
				
		//4- 根据台帐编号和管理节点编号查看TRADE_OHTER表是否还有没回复的相同编号的管理节点
		if(StringUtils.isBlank(tradeId)){
			return result;
		}			
		IDataset tradeOtherInfoList = TradeOtherInfoQry.queryTradeOtherByTradeIdAndRsrvValueCode(tradeId,"BBOSS_" + manageNode);
		if(IDataUtil.isEmpty(tradeOtherInfoList)){
			return result;
		}
		IDataset tradeInfoList = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);		
		for(int i= 0;i<tradeOtherInfoList.size();i++){		    
			IData tradeOtherInfo = tradeOtherInfoList.getData(i);
			String isRsp = tradeOtherInfo.getString("RSRV_TAG1");//1代表已回复，其它代表未回复
			if(!StringUtils.endsWith("1", isRsp) && IDataUtil.isNotEmpty(tradeInfoList)){
				result = true;
				break;
			}
		}
		
		//5- 返回判断结果
		return result;
	}
	
	/**
	 * @description 获取管理节点延时处理的返回结果
	 * @author xunyl
	 * @date 2015-02-05
	 */
	public static IDataset getMgrRspInfoList(IData data)throws Exception {
		IDataset dealResult = new DatasetList();
		
		IData result_data = new DataMap();
        result_data.put("OPR_TIME",SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER",""));
        result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID",""));
        result_data.put("PRODUCT_ORDERNUMBER",  IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号			
		IDataset temp = new DatasetList();
		temp.add("00");
		result_data.put("PRODUCT_ORDER_RSP_CODE",temp);
		IDataset temp1 = new DatasetList();
		temp1.add(IntfField.SUUCESS_CODE[1]);
		result_data.put("PRODUCT_ORDER_RSP_DESC", temp1);			
		result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
		result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);	
		result_data.put("RSPCODE".toUpperCase(), "00");
	    result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
		dealResult.add(result_data);

        return dealResult;
	}
}
