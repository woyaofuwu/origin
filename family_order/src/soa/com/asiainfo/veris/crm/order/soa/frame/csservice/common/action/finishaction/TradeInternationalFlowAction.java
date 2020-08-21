package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMbDisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/**
 * @author 	chenmw
 * @date	2016-11-23
 * 完工之前处理国际流量统付资费绑定
 */
public class TradeInternationalFlowAction implements ITradeFinishAction{
	private static Logger logger = Logger.getLogger(TradeInternationalFlowAction.class);
	
	public void executeAction(IData mainTrade) throws Exception {
		String discntTradeTableName = "TF_B_TRADE_DISCNT";
		String intfId = mainTrade.getString("INTF_ID","");
		String tradeId = mainTrade.getString("TRADE_ID");
		
		//1、如果台账有登记 TF_B_TRADE_DISCNT 表
		if(intfId.indexOf(discntTradeTableName)!=-1){
			return;
		}
		if (logger.isDebugEnabled()){
			logger.debug(">>>>>>>国际流量统付日志 mainTrade="+mainTrade);
		}

		//2、获取台账 TF_B_TRADE_GRP_MERCH_MB_DIS 数据 
		IDataset tradeGrpMerchMbDisInfos = TradeGrpMerchMbDisInfoQry.getTradeGrpMerchMbDisByTradeId(tradeId);
		if(IDataUtil.isEmpty(tradeGrpMerchMbDisInfos)){
			return;
		}
		if (logger.isDebugEnabled()){
			logger.debug(">>>>>>>国际流量统付日志 tradeGrpMerchMbDisInfos="+tradeGrpMerchMbDisInfos);
		}
		IData tradeGrpMerchMbDisInfo = tradeGrpMerchMbDisInfos.getData(0);
		
		//3、获取集团产品编码，判断是否是国际流量统付
		String product_spec_code = tradeGrpMerchMbDisInfo.getString("PRODUCT_SPEC_CODE");
    	if (!"99910".equals(product_spec_code)){
    		return;
    	}

    	//4、获取国际流量统付配置的包编码、优惠编码信息
		IDataset staticList = StaticUtil.getStaticList("BBOSS_INTERNATIONALFLOW_PRO");
		if(IDataUtil.isEmpty(staticList)){
			return;
		}
		if (logger.isDebugEnabled()){
			logger.debug(">>>>>>>国际流量统付日志 staticList="+staticList);
		}

		//5、获取本地产品编码
		String productId = GrpCommonBean.merchToProduct(product_spec_code, 2, null);
		if(StringUtils.isBlank(productId))
		{
			IDataset pStaticList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID", new String[]{ "TYPE_ID", "DATA_ID" }, new String[]{ "BBOSS_INTERNATIONALFLOW_PRO", staticList.getData(0).getString("DATA_ID")});
			if(IDataUtil.isEmpty(pStaticList)){
				return;
			}
			if (logger.isDebugEnabled()){
				logger.debug(">>>>>>>国际流量统付日志 pStaticList="+pStaticList);
			}
			productId = pStaticList.getData(0).getString("PDATA_ID");
		}
		//获取本地资费编码
		String discntCode= GrpCommonBean.merchToProduct(tradeGrpMerchMbDisInfo.getString("PRODUCT_DISCNT_CODE"), 1, productId);
		if(StringUtils.isBlank(discntCode))
		{
			discntCode = staticList.getData(0).getString("DATA_NAME");
		}
		//获取本地包编码
		String packageId = staticList.getData(0).getString("DATA_ID");
		IDataset packageInfos = ProductInfoQry.getElementByProductIdElemId(productId, discntCode);
		if (IDataUtil.isNotEmpty(packageInfos))
		{
			packageId = packageInfos.getData(0).getString("PACKAGE_ID");
		}
		
		//6、组装数据
		IData discntTradeData = new DataMap();
		discntTradeData.put("TRADE_ID", tradeId);
		discntTradeData.put("ACCEPT_MONTH", tradeId.substring(4, 6));
		discntTradeData.put("USER_ID", mainTrade.getString("USER_ID"));
		discntTradeData.put("USER_ID_A", tradeGrpMerchMbDisInfo.getString("RSRV_STR1"));
		discntTradeData.put("PRODUCT_ID", productId);
		discntTradeData.put("PACKAGE_ID", packageId);
		discntTradeData.put("DISCNT_CODE", discntCode);
		//特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
		discntTradeData.put("SPEC_TAG", "0");
		discntTradeData.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(productId));
		discntTradeData.put("INST_ID", SeqMgr.getInstId());
		discntTradeData.put("CAMPN_ID", mainTrade.getString("CAMPN_ID"));
		discntTradeData.put("START_DATE", tradeGrpMerchMbDisInfo.getString("START_DATE"));
        discntTradeData.put("END_DATE", tradeGrpMerchMbDisInfo.getString("END_DATE"));
        discntTradeData.put("MODIFY_TAG", "0");
		discntTradeData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		discntTradeData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
		discntTradeData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
		discntTradeData.put("REMARK", mainTrade.getString("REMARK",tradeGrpMerchMbDisInfo.getString("PRODUCT_DISCNT_CODE")));
		discntTradeData.put("OPER_CODE", "");
		//是否需要发服开 0为不发，1或"" 是要发服开
		discntTradeData.put("IS_NEED_PF", "0");
		if (logger.isDebugEnabled()){
			logger.debug(">>>>>>>国际流量统付日志 discntTradeData="+discntTradeData);
		}
		
		//7、数据入 TF_B_TRADE_DISCNT 表
		Dao.insert(discntTradeTableName, discntTradeData,Route.getJourDb());
		
		//8、更新主台账TF_B_TRADE表的INTF_ID字段
		intfId = intfId + discntTradeTableName + ",";
		mainTrade.put("INTF_ID", intfId);
		String sql = "UPDATE TF_B_TRADE T SET T.INTF_ID=:INTF_ID WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))";
		Dao.executeUpdate(new StringBuilder(sql),mainTrade,Route.getJourDb());
	}

}
