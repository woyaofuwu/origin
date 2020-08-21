package com.asiainfo.veris.crm.iorder.soa.family.busi.cancel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.TradeCancelReqData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.cancel.data.builder.TradeCancelReqDataBuilder;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetActInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class TradeCancelSVC extends CSBizService {

	private static final long serialVersionUID = 1L;

	private static final transient Logger logger = Logger.getLogger(TradeCancelSVC.class);
	
	public IData getIsEC(IData param) throws Exception{
		IData isEcFlag = new DataMap();
		String serialNum = IDataUtil.chkParam(param, "SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNum);
		if(IDataUtil.isEmpty(userInfo)){
//			IDataset snTrade= TradeInfoQry.getTradeInfoBySn(serialNum);
//			if(IDataUtil.isNotEmpty(snTrade)){
			//查询WIDENET_ACT预留字段RSRV_STR4是否是集团EC
			IDataset wideNetECTrade= TradeWideNetActInfoQry.qeyWideNetActInfoBTradeByAcctId(serialNum);
			if(IDataUtil.isNotEmpty(wideNetECTrade)){
				String snEC = wideNetECTrade.getData(0).getString("RSRV_STR4");
				if(StringUtils.isNotEmpty(snEC) && snEC.startsWith("29")){
					isEcFlag.put("IS_EC_FLAG", "1");
				}
			}
//			}
		}
		return isEcFlag;
	}
	//查询EC撤单
	public IDataset getECUserUnfinishedTradeList(IData param) throws Exception{
		TradeCancelBean tradeCancelBean = BeanManager.createBean(TradeCancelBean.class);
		IDataset unfinishedTradeList = tradeCancelBean.getECUserUnfinishedTradeList(param);
		
		for (int i = 0, s = unfinishedTradeList.size(); i < s; i++) {
			IData tradeData = unfinishedTradeList.getData(i);
			tradeData.put("TRADE_TYPE_NAME", UTradeTypeInfoQry.getTradeTypeName(tradeData.getString("TRADE_TYPE_CODE")));
			tradeData.put("TRADE_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tradeData.getString("TRADE_DEPART_ID")));
			tradeData.put("TRADE_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tradeData.getString("TRADE_EPARCHY_CODE")));
			tradeData.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tradeData.getString("TRADE_STAFF_ID")));
			tradeData.put("WORKER_REJECTION", false);

			String tradeId = tradeData.getString("TRADE_ID");
			if (tradeId.equals(tradeData.getString("RSRV_STR8"))
					&& StringUtils.isNotBlank(tradeData.getString("RSRV_STR9"))) {
				tradeData.put("WORKER_REJECTION", true);
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("用户的可撤销订单信息：" + unfinishedTradeList.toString());
		}
		
		return unfinishedTradeList;
	}
	public IDataset getUserUnfinishedTradeList(IData param) throws Exception {
		TradeCancelBean tradeCancelBean = BeanManager.createBean(TradeCancelBean.class);
		IDataset unfinishedTradeList = tradeCancelBean.getUserUnfinishedTradeList(param);
		
		for (int i = 0, s = unfinishedTradeList.size(); i < s; i++) {
			IData tradeData = unfinishedTradeList.getData(i);
			tradeData.put("TRADE_TYPE_NAME", UTradeTypeInfoQry.getTradeTypeName(tradeData.getString("TRADE_TYPE_CODE")));
			tradeData.put("TRADE_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tradeData.getString("TRADE_DEPART_ID")));
			tradeData.put("TRADE_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tradeData.getString("TRADE_EPARCHY_CODE")));
			tradeData.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tradeData.getString("TRADE_STAFF_ID")));
			tradeData.put("WORKER_REJECTION", false);

			String tradeId = tradeData.getString("TRADE_ID");
			if (tradeId.equals(tradeData.getString("RSRV_STR8"))
					&& StringUtils.isNotBlank(tradeData.getString("RSRV_STR9"))) {
				tradeData.put("WORKER_REJECTION", true);
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("用户的可撤销订单信息：" + unfinishedTradeList.toString());
		}
		
		return unfinishedTradeList;
	}
	
	
	public IData getCancelTradeDetails(IData param) throws Exception {
		IData resultData = new DataMap();
		IDataset feeList = new DatasetList();
		
		TradeCancelBean tradeCancelBean = BeanManager.createBean(TradeCancelBean.class);
		IDataset cancelTradeList = tradeCancelBean.getCancelTradeDetails(param);
		resultData.put("IS_OVERMONTH", "0");
		for (int i = 0, s = cancelTradeList.size(); i < s; i++) {
			IData tradeData = cancelTradeList.getData(i);
			tradeData.put("FINISH_FLAG", tradeData.getInt("FINISH_FLAG", 0));
			tradeData.put("FINISH_FLAG_NAME", tradeData.getInt("FINISH_FLAG") == 0 ? "未完工" : "已完工");
			tradeData.put("TRADE_TYPE_NAME", UTradeTypeInfoQry.getTradeTypeName(tradeData.getString("TRADE_TYPE_CODE")));
			tradeData.put("TRADE_DEPART_NAME", UDepartInfoQry.getDepartNameByDepartId(tradeData.getString("TRADE_DEPART_ID")));
			tradeData.put("TRADE_EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tradeData.getString("TRADE_EPARCHY_CODE")));
			tradeData.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tradeData.getString("TRADE_STAFF_ID")));
			tradeData.put("WORKER_REJECTION", false);

			String tradeId = tradeData.getString("TRADE_ID");
			String tradeTypeCode = tradeData.getString("TRADE_TYPE_CODE");
			if (tradeId.equals(tradeData.getString("RSRV_STR8"))
					&& StringUtils.isNotBlank(tradeData.getString("RSRV_STR9"))) {
				tradeData.put("WORKER_REJECTION", true);
			}

			feeList.addAll(tradeCancelBean.getTradeFeeList(tradeId, tradeTypeCode));
			//查询手机产品变更是否跨月
			if("110".equals(tradeData.getString("TRADE_TYPE_CODE"))){
				IDataset mainProductInfos = UserProductInfoQry.queryUserMainProduct(tradeData.getString("USER_ID"));
				if(IDataUtil.isNotEmpty(mainProductInfos) && 1==mainProductInfos.size()){
					resultData.put("IS_OVERMONTH", "1");
				}
			}
		}
		for(int i = 0; i < cancelTradeList.size(); i++){
			IData tradeData = cancelTradeList.getData(i);
			//剔除252工单
			if("252".equals(tradeData.getString("TRADE_TYPE_CODE"))){
				//IDataset familyInfos = FamilyPackageTradeViewQry.queryFamilyVNetInfo(tradeData.getString("USER_ID"),"1V"); 暂时注销
				IDataset familyInfos = null;
				if(IDataUtil.isNotEmpty(familyInfos)){
					CSAppException.apperr(TradeException.CRM_TRADE_95, "该笔订单中有家庭V网开户工单,请先去家庭V网销户界面注销家庭V网,再做撤销!");
				}else{
					cancelTradeList.remove(i);
					i--;
				}
			}
			if("110".equals(tradeData.getString("TRADE_TYPE_CODE"))&&"1".equals(resultData.getString("IS_OVERMONTH"))){
				    cancelTradeList.remove(i);
				    i--;
			}
		}
		
		resultData.put("FEE_LIST", feeList);
		resultData.put("PAGE_DATA", tradeCancelBean.getPageData(param));
		resultData.put("CANCEL_TRADE_LIST", cancelTradeList);
		
		if (logger.isDebugEnabled()) {
			logger.debug("用户选择的工单所在订单的详细信息：" + resultData.toString());
		}
		
		return resultData;
	}
	
	
	/**
	 * @desc 融合业务撤销受理
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData cancelTradeSubmit(IData param) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("撤单提交数据：" + param.toString());
		}
		
		DatasetList cancelTradeList = new DatasetList(param.getString("CANCEL_TRADE_LIST"));
		for (int i = 0; null != cancelTradeList && i < cancelTradeList.size(); i++) {
			IData data = cancelTradeList.getData(i);
			String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
			if (StringUtils.equals("109",tradeTypeCode))
			{
				CSAppException.apperr(TradeException.CRM_TRADE_95, "您所选的撤单工单中存在套餐变更工单，不允许撤单");
			}
		}
		
		/*IDataset tradeCancel301 = CommonUtil.select(cancelTradeList, new String[] {"TRADE_TYPE_CODE"}, new String[] {"301"});
		if(DataUtils.isEmpty(tradeCancel301)){
			IDataset tradeCancel309 = CommonUtil.select(cancelTradeList, new String[] {"TRADE_TYPE_CODE"}, new String[] {"309"});
			if(DataUtils.isNotEmpty(tradeCancel309)) {
				if(DataUtils.isNotEmpty(CommonUtil.select(cancelTradeList, new String[] {"TRADE_TYPE_CODE"}, new String[] {"64"}))) {
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"办理影视权益包的用户，不可单独撤单魔百盒！");
				}
			}
		}*/
		
		TradeCancelReqData reqData = TradeCancelReqDataBuilder.buildReqData(param);
		//TradeCancelRule.checkAbnormalUnfinishedTrade(reqData);
		
		TradeCancelBean tradeCancelBean = BeanManager.createBean(TradeCancelBean.class);
		//tradeCancelBean.cancelTradeSubmit(reqData);

		IData result = new DataMap();
		result.put("CANCEL_ORDER_ID", reqData.getCancelOrderId());
		if (StringUtils.isNotEmpty(reqData.getUndoOrderId())) {
			result.put("UNDO_ORDER_ID", reqData.getUndoOrderId());
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("订单撤销提交返回信息：" + result.toString());
		}
		
		return result;
	}
}

