package com.asiainfo.veris.crm.order.soa.person.busi.evaluecard;


import org.apache.commons.lang.StringUtils;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizService;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.EValueCardInfoQry;

public class EValueCardSVC extends CSBizService {

	
	/**
	 * 有价卡销售接口
	 * SS.TelValueCardSVC.SellValueCard
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData sellEValueCard(IData input) throws Exception{

		IDataUtil.chkParamNoStr(input, "IDVALUE"); 
		IDataUtil.chkParamNoStr(input, "CARD_MONEY"); 
		IDataUtil.chkParamNoStr(input, "BUY_COUNT"); 
		IDataUtil.chkParamNoStr(input, "CHANNEL_TYPE"); 
		IDataUtil.chkParamNoStr(input, "CARD_TYPE"); 
		IDataUtil.chkParamNoStr(input, "HOME_PRO"); 
		IDataUtil.chkParamNoStr(input, "PAYMENT"); 
		IDataUtil.chkParamNoStr(input, "ACTION_TIME"); 
		IDataUtil.chkParamNoStr(input, "SETTLE_DATE"); 
		
		input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
		if ("".equals(input.getString("TRANSACTIONID",""))) {
			input.put("TRANSACTIONID", getTransactionID());
		}
//		//判断购卡用户是不是正常用户
//		IDataset set = UserInfoQry.getUserInfoBySn(input.getString("IDVALUE"), "0", "00");
//        if (IDataUtil.isEmpty(set))
//            CSAppException.apperr(CrmUserException.CRM_USER_1);// 无用户资料
		
        //不是批量购卡一次最多够10张
		if (input.getInt("BUY_COUNT",0) >10 || input.getInt("BUY_COUNT",0) == 0) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "BUY_COUNT购卡数量1-10之间！");
		}
		
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
        IData ibossData = bean.sellEValueCardIntf(input);
		
		int payMoney = input.getInt("CARD_MONEY",0)*input.getInt("BUY_COUNT",0);
    	input.put("PAY_MONEY", String.valueOf(payMoney));
		//登记台账
		IData result =  CSAppCall.call("SS.EValueCardRegSVC.tradeReg", input).getData(0);
		result.putAll(ibossData);
		return result ;
		
	}
	
	
	/**电子有价卡补发接口
	 * SS.TelValueCardSVC.ReissueValueCard
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData reSendCardInfo(IData input) throws Exception{
		
		if ("3".equals(input.getString("IN_MODE_CODE"))) {
			IDataUtil.chkParamNoStr(input, "IDVALUE"); 
		}
		IDataUtil.chkParamNoStr(input, "REISSUE_TYPE"); 
		if ("1".equals(input.getString("REISSUE_TYPE"))) {
			IDataUtil.chkParamNoStr(input, "ORI_TRANSACTIONID"); 
		}
		if ("2".equals(input.getString("REISSUE_TYPE"))) {
			IDataUtil.chkParamNoStr(input, "CARD_NO"); 
		}
		                                
		if ("".equals(input.getString("TRANSACTIONID",""))) {
			input.put("TRANSACTIONID", getTransactionID());
		}
		
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		return bean.reSendCardInfo(input);
	}
	
	
	/**
	 * SS.TelValueCardSVC.QuerySellValueCard
	 * 根据手机号码查询一个月中购卡记录
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryCardRecord(IData input) throws Exception {
		
		IDataUtil.chkParamNoStr(input, "IDVALUE"); 
		IData retnData = new DataMap();
		IDataset  cardInfo =  EValueCardInfoQry.getCardSaleInfo(input.getString("IDVALUE"));
		if(cardInfo!=null && cardInfo.size() > 0){   //如果不存在记录，改字段不返回  modify tanjl 2014-12-01
			retnData.put("CARD_INFO", cardInfo);
		}
		return retnData;
	}
	
	/**
	 * SS.TelValueCardSVC.QueryBatchSellInfoReq
	 * 查询有价卡批量销售请求信息
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryBatchSellReqInfo(IData input) throws Exception {
		
		IDataUtil.chkParamNoStr(input, "SERIAL_NUMBER");
		String transactionId = input.getString("TRANSACTION_ID");
		if (StringUtils.isEmpty(transactionId)) {
			IDataUtil.chkParamNoStr(input, "START_DATE");
			IDataUtil.chkParamNoStr(input, "END_DATE");
		} 
		IData retnData = new DataMap();
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		param.put("TRANSACTION_ID", transactionId);
		param.put("START_DATE", input.getString("START_DATE"));
		param.put("END_DATE", input.getString("END_DATE"));
		IDataset  cardInfo =  EValueCardInfoQry.queryBatchReqInfo(param,getPagination());
		if(cardInfo!=null && cardInfo.size() > 0){   //如果不存在记录，改字段不返回  modify tanjl 2014-12-01
			retnData.put("REQ_INFO", cardInfo);
		}
		return retnData;
	}
	/**
	 * SS.TelValueCardSVC.queryBatchRecReqInfo
	 * 查询有价卡批量充值请求信息
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryBatchRecReqInfo(IData input) throws Exception {
		
		IDataUtil.chkParamNoStr(input, "SERIAL_NUMBER");
		String tradeId = input.getString("TRADE_ID");
		if (StringUtils.isEmpty(tradeId)) {
			IDataUtil.chkParamNoStr(input, "START_DATE");
			IDataUtil.chkParamNoStr(input, "END_DATE");
		} 
		IData retnData = new DataMap();
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		param.put("TRADE_ID", tradeId);
		param.put("START_DATE", input.getString("START_DATE"));
		param.put("END_DATE", input.getString("END_DATE"));
		IDataset  cardInfo =  EValueCardInfoQry.queryBatchRecReqInfo(param,getPagination());
		if(cardInfo!=null && cardInfo.size() > 0){
			retnData.put("REQ_INFO", cardInfo);
		}
		return retnData;
	}
	 /**
     * 页面受理电子有价卡批量销售
     * @param input
     * @throws Exception
     */
	public IData batchSellStoreEValueCard(IData input) throws Exception {
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		input.put("TRANSACTION_ID", this.getTransactionID());
		return bean.batchSellStoreEValueCard(input);
	}
	
    /**
     * 页面受理电子有价卡返销
     * @param input
     * @throws Exception
     */
	public void cancelEValueCard(IData input) throws Exception {
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		bean.cancelEValueCard(input);
	}
	
	 /**
     * 营业厅受理电子有价卡销售
     * @param input
     * @throws Exception
     */
	public void sellStoreEValueCard(IData input) throws Exception {
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		bean.sellStoreEValueCard(input);
	}
	
	 /**
     * 获取可以返销的有价卡销售数据
     * @param input
     * @throws Exception
     */
	public IDataset getCanCancelCardInfo(IData input) throws Exception {
		
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		return bean.getCanCancelCardInfo(input);
	}
	
	/**
	 * SS.TelValueCardSVC.QueryValueCardInfo
	 * 根据手机号码查询一个月中购卡记录
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryValueCardInfo(IData input) throws Exception {
		
		IDataUtil.chkParamNoStr(input, "CARD_NO"); 
		if ("".equals(input.getString("TRANSACTIONID",""))) {
			input.put("TRANSACTIONID", getTransactionID());
		}
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		return bean.queryValueCardInfo(input);
	}
	
	
	/**
	 * 电子卡锁定、解锁、延期操作  add by huping20161009
	 * @return
	 */
	public IDataset lockOrUnlockEValueCard(IData input) throws Exception { 
		if ("".equals(input.getString("TRANSACTIONID",""))) {
			input.put("TRANSACTIONID", getTransactionID());
		}
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		return bean.lockOrUnlockEValueCard(input);
	}
	
	/**
	 * 安全密钥上行更新  add by huping20161009
	 * @return
	 */
	public IDataset upRsaPublicKey(IData input) throws Exception { 
		if ("".equals(input.getString("TRANSACTIONID",""))) {
			input.put("TRANSACTIONID", getTransactionID());
		}
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		return bean.upRsaPublicKey(input);
	}
	
	/**
	 * 安全密钥查询  add by huping20161009
	 * @return
	 */
	public IDataset qryRsaPublicKey(IData input) throws Exception { 
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		input.put("STAFF_ID", getVisit().getStaffId());
		return bean.qryRsaPublicKey(input,getPagination());
	}
	
	/**
	 * 安全密钥下行更新接口  add by huping20161009
	 * @return
	 */
	public IData downRsaPublicKey(IData input) throws Exception { 
		if ("".equals(input.getString("TRANSACTIONID",""))) {
			input.put("TRANSACTIONID", getTransactionID());
		}
		EValueCardOrderBean bean = BeanManager.createBean(EValueCardOrderBean.class);
		return bean.downRsaPublicKey(input);
	}
	
	public IData getECRechargeID(IData input) throws Exception{
		String tradeId = SeqMgr.getNewTradeIdFromDb(SysDateMgr.getSysTime());
		IData result = new DataMap();
		result.put("INIT_TRADE_ID",tradeId);
		return result; 
	}
	
	public String getTransactionID() throws Exception{
		return "898"+SysDateMgr.getSysDate("yyyyMMddHHmmss")+"000001"; 
	}
}
