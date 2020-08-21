package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class SelfTerminalBean extends CSBizBean{
	/**
	 * 保存支付订单数据
	 * @param data
	 * @throws Exception
	 */
	public void addPayOrder(IData data)throws Exception{
		 StringBuilder insSql = new StringBuilder();
		 insSql.append(" INSERT INTO TF_B_PAYORDER_INFO( ");
		 insSql.append(" UUID,TRANS_ACTION_ID,SERIAL_NUMBER,ORDER_NO,ACCOUNT_MONEY,PAYMENT,ORDER_MONNEY,GIFT ");
		 insSql.append(" ,BUSI_TYPE,CHARGE_MONEY,PAYMENT_TYPE,PAY_TRANS,PAY_STATUS,ORDER_STATUS_CODE ");
		 insSql.append(" ,ORDER_STATUS_DESC,REFUND_ORDER_NO,REFUND_FEE,SETTLE_DATE,PRODUCT_NAME ");
		 insSql.append(" ,HOME_PROV,IS_REFUND,HALL_CODE,OP_ID,ACCEPT_MONTH) ");
		 
		 insSql.append(" VALUES(SEQ_PAYORDER_INFO.NEXTVAL,:TRANS_ACTION_ID,:SERIAL_NUMBER,:ORDER_NO,:ACCOUNT_MONEY,:PAYMENT,:ORDER_MONNEY ");
		 insSql.append(",:GIFT,:BUSI_TYPE,:CHARGE_MONEY,:PAYMENT_TYPE,:PAY_TRANS,:PAY_STATUS,:ORDER_STATUS_CODE,:ORDER_STATUS_DESC");
		 insSql.append(",:REFUND_ORDER_NO,:REFUND_FEE,:SETTLE_DATE,:PRODUCT_NAME,:HOME_PROV,:IS_REFUND,:HALL_CODE");
		 insSql.append(",:OP_ID,TO_NUMBER(TO_CHAR(SYSDATE,'mm')))");
		 
		 IData inParam=new DataMap();
		 inParam.put("TRANS_ACTION_ID", data.getString("transactionId",""));
		 inParam.put("SERIAL_NUMBER", data.getString("serviceNumber",""));
		 inParam.put("ORDER_NO", data.getString("orderNo",""));
		 inParam.put("ACCOUNT_MONEY", data.getString("accountMoney",""));
		 inParam.put("PAYMENT", data.getString("payMent",""));
		 inParam.put("ORDER_MONNEY", data.getString("orderMoney",""));
		 inParam.put("GIFT", data.getString("gift",""));
		 inParam.put("BUSI_TYPE", data.getString("busiType",""));
		 inParam.put("CHARGE_MONEY", data.getString("chargeMoney",""));
		 inParam.put("PAYMENT_TYPE", data.getString("payMentType",""));
		 inParam.put("PAY_TRANS", data.getString("paytrans",""));
		 inParam.put("PAY_STATUS", data.getString("payStatus",""));
		 inParam.put("ORDER_STATUS_CODE", data.getString("orderStatusCode",""));
		 inParam.put("ORDER_STATUS_DESC", data.getString("orderStatusDesc",""));
		 inParam.put("REFUND_ORDER_NO", data.getString("refundOrderNo",""));
		 inParam.put("REFUND_FEE", data.getString("refundFee",""));
		 inParam.put("SETTLE_DATE", data.getString("settleDate",""));
		 inParam.put("PRODUCT_NAME", data.getString("productName",""));
		 inParam.put("HOME_PROV", data.getString("homeProv",""));
		 inParam.put("IS_REFUND", data.getString("isRefund",""));
		 inParam.put("HALL_CODE", data.getString("hallCode",""));
		 inParam.put("OP_ID", data.getString("OP_ID",""));
	     
		 Dao.executeUpdate(insSql, inParam);
	}
	
	/**
	 * 记录流水关系
	 * @param inParams
	 * @throws Exception
	 */
	public void addSelfLog(IData inParams) throws Exception{
		StringBuilder sql=new StringBuilder();
		IData params=new DataMap();
		sql.append("INSERT INTO TF_B_SELFTERM_LOG(TRANS_ACTION_ID,CHANNEL_ID,ORDER_ID,OSP_ORDER_ID,");
		sql.append("IDENTITY_SN,CRM_ORDER_ID,CRM_TRADE_ID,OP_ID,OP_ORG,ACCEPT_MONTH,TRADE_TYPE_CODE");
		sql.append(",SERIAL_NUMBER,ORDER_MONEY,PAYMENT_MONEY) VALUES(:TRANS_ACTION_ID,:CHANNEL_ID,:ORDER_ID,:OSP_ORDER_ID,");
		sql.append(":IDENTITY_SN,:CRM_ORDER_ID,:CRM_TRADE_ID,:OP_ID,:OP_ORG,TO_NUMBER(TO_CHAR(SYSDATE,'mm')),");
		sql.append(":TRADE_TYPE_CODE,:SERIAL_NUMBER,:ORDER_MONEY,:PAYMENT_MONEY)");
		
		params.put("TRANS_ACTION_ID", inParams.getString("TRANS_ACTION_ID",""));
		params.put("CHANNEL_ID", inParams.getString("CHANNEL_ID",""));
		params.put("ORDER_ID", inParams.getString("ORDER_ID",""));
		params.put("OSP_ORDER_ID", inParams.getString("OSP_ORDER_ID",""));
		params.put("IDENTITY_SN", inParams.getString("IDENTITY_SN",""));
		params.put("CRM_ORDER_ID", inParams.getString("CRM_ORDER_ID",""));
		params.put("CRM_TRADE_ID", inParams.getString("CRM_TRADE_ID",""));
		params.put("OP_ID", inParams.getString("OP_ID",""));
		params.put("OP_ORG", inParams.getString("OP_ORG",""));
		params.put("TRADE_TYPE_CODE", inParams.getString("TRADE_TYPE_CODE",""));
		params.put("SERIAL_NUMBER", inParams.getString("SERIAL_NUMBER",""));
		params.put("ORDER_MONEY", inParams.getString("ORDER_MONEY",""));
		params.put("PAYMENT_MONEY", inParams.getString("PAYMENT_MONEY",""));
		
		Dao.executeUpdate(sql, params);
	}
	/**
	 * 查询日志记录
	 * @param inParams
	 * @return
	 * @throws Exception
	 */
	public IData qrySelfLog(IData inParams) throws Exception{
		StringBuilder sql=new StringBuilder();
		IData params=new DataMap();
		sql.append("SELECT CRM_ORDER_ID,CRM_TRADE_ID FROM TF_B_SELFTERM_LOG T WHERE T.TRANS_ACTION_ID=:TRANS_ACTION_ID");
		params.put("TRANS_ACTION_ID", inParams.getString("transactionId"));
		IDataset results= Dao.qryBySql(sql, params);
		if(IDataUtil.isNotEmpty(results)){
			return results.getData(0);
		}
		return null;
	}
	/**
	 * 保存用户身份校验结果数据
	 * @param data
	 * @throws Exception
	 */
	public void addUserSyncResult(IData data)throws Exception{
		 StringBuilder insSql = new StringBuilder();
		 insSql.append(" INSERT INTO TF_B_USERCHECK_INFO( ");
		 insSql.append(" TRANS_ACTION_ID,SERIAL_NUMBER,IDENTITY_SN,CHECK_RESULT,CHECK_VALUE,ELEC_WORK_ORDER,IDCARD_IMG ");
		 insSql.append(" ,USERGAY_PIC,USERCOLOR_PIC,IDCARD_TYPE,IDCARD_NO,CUST_NAME,CUST_SEX ");
		 insSql.append(" ,NATION,BIRTHDAY,CERTIFICATE_ADDR,OP_ID,OP_ORG,ACCEPT_MONTH) ");
		 
		 insSql.append(" VALUES(:TRANS_ACTION_ID,:SERIAL_NUMBER,:IDENTITY_SN,:CHECK_RESULT,:CHECK_VALUE,:ELEC_WORK_ORDER ");
		 insSql.append(",:IDCARD_IMG,:USERGAY_PIC,:USERCOLOR_PIC,:IDCARD_TYPE,:IDCARD_NO,:CUST_NAME,:CUST_SEX,:NATION");
		 insSql.append(",:BIRTHDAY,:CERTIFICATE_ADDR,:OP_ID,:OP_ORG,TO_NUMBER(TO_CHAR(SYSDATE,'mm')))");
		 
		 IData inParam=new DataMap();
		 inParam.put("TRANS_ACTION_ID", data.getString("transactionId",""));
		 inParam.put("SERIAL_NUMBER", data.getString("serviceNum",""));
		 inParam.put("IDENTITY_SN", data.getString("identitySN",""));
		 inParam.put("CHECK_RESULT", data.getString("checkResult",""));
		 inParam.put("CHECK_VALUE", data.getString("checkValue",""));
		 inParam.put("ELEC_WORK_ORDER", data.getString("elecWorkOrder",""));
		 inParam.put("IDCARD_IMG", data.getString("idCardImg",""));
		 inParam.put("USERGAY_PIC", data.getString("userGayPic",""));
		 inParam.put("USERCOLOR_PIC", data.getString("userColorPic",""));
		 
		 if(IDataUtil.isNotEmpty(data.getData("certificateInfo"))){
			 inParam.put("IDCARD_TYPE", data.getData("certificateInfo").getString("certificateType",""));
			 inParam.put("IDCARD_NO", data.getData("certificateInfo").getString("certificateNo",""));
			 inParam.put("CUST_NAME", data.getData("certificateInfo").getString("legalName",""));
			 inParam.put("CUST_SEX", data.getData("certificateInfo").getString("gender",""));
			 inParam.put("NATION", data.getData("certificateInfo").getString("nation",""));
			 inParam.put("BIRTHDAY", data.getData("certificateInfo").getString("birthday",""));
			 inParam.put("CERTIFICATE_ADDR", data.getData("certificateInfo").getString("certificateAddr",""));
		 }
		 
		 inParam.put("OP_ID", data.getString("OP_ID",""));
		 inParam.put("OP_ORG", data.getString("OP_ORG",""));
	     
		 Dao.executeUpdate(insSql, inParam);
	}
	/**
	 * 查询用户校验结果同步信息
	 * @param inParams
	 * @return
	 * @throws Exception
	 */
	public IData qryUserCheckInfo(IData inParams) throws Exception{
		StringBuilder sql=new StringBuilder();
		IData params=new DataMap();
		sql.append("SELECT * FROM TF_B_USERCHECK_INFO T WHERE T.TRANS_ACTION_ID=:TRANS_ACTION_ID");
		params.put("TRANS_ACTION_ID", inParams.getString("transactionId"));
		IDataset results= Dao.qryBySql(sql, params);
		if(IDataUtil.isNotEmpty(results)){
			return results.getData(0);
		}
		return null;
	} 
	/**
	 * 查询工单信息
	 * @param inParams
	 * @return
	 * @throws Exception
	 */
	public IData qryTradeInfo(String sn,String tradeTypeCode,boolean isHis) throws Exception{
		StringBuilder sql=new StringBuilder();
		
		IData params=new DataMap();
		params.put("SERIAL_NUMBER", sn);
		params.put("TRADE_TYPE_CODE", tradeTypeCode);
		
		sql.append("SELECT TO_CHAR(B.TRADE_ID) TRADE_ID,");
		sql.append("   B.TRADE_TYPE_CODE,");
		sql.append("   TO_CHAR(B.ACCEPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ACCEPT_DATE,");
		sql.append("   B.USER_ID,");
		sql.append("   B.SERIAL_NUMBER,");
		sql.append("   TO_CHAR(B.CUST_NAME) CUST_NAME,");
		sql.append("   B.BRAND_CODE,");
		sql.append("   B.CUST_ID,");
		sql.append("   DECODE(SIGN(LENGTH(B.PROCESS_TAG_SET) - 20),-1,'Z',SUBSTR(B.PROCESS_TAG_SET, 20, 1)) VERIFY_MODE,");
		sql.append("   B.TRADE_STAFF_ID,");
		sql.append("   B.TRADE_DEPART_ID ORG_INFO,");
		sql.append("   B.ORDER_ID");
		if(isHis){
			sql.append(" FROM  TF_BH_TRADE B");
		}else{
			sql.append(" FROM  TF_B_TRADE B");
		}
		sql.append(" WHERE ");
		sql.append(" B.ACCEPT_MONTH = TO_NUMBER(TO_CHAR(SYSDATE,'MM'))");
		sql.append(" AND B.SERIAL_NUMBER=:SERIAL_NUMBER");
		sql.append(" AND B.TRADE_TYPE_CODE=:TRADE_TYPE_CODE");
		sql.append(" ORDER BY B.ACCEPT_DATE DESC");
		
		IDataset results= Dao.qryBySql(sql, params);
		if(IDataUtil.isNotEmpty(results)){
			return results.getData(0);
		}
		return null;
	} 
	
	/**
	 * 更新对账结果表数据
	 * @param data
	 * @throws Exception
	 */
	public void updateSettleResult(IData data,String type)throws Exception{
		 StringBuilder updSql = new StringBuilder();
		 IData inParam=new DataMap();
		 updSql.append(" UPDATE TF_B_PAYORDER_RECRESULT SET DEAL_STATE=:DEAL_STATE");
		 //退款同步更新
		 if("0".equals(type)){
			 if("0".equals(data.getString("RESULT_CODE"))){
				 inParam.put("DEAL_STATE", "3");//退款中
				
				 updSql.append(",REFUND_DATE=:REFUND_DATE");
				 inParam.put("REFUND_DATE", data.getString("reqDate"));
				 updSql.append(",REFUND_ORDER_NO=:REFUND_ORDER_NO");
				 inParam.put("REFUND_ORDER_NO", data.getString("refundOrderNo"));
				 updSql.append(",DEAL_INFO=:DEAL_INFO");
				 inParam.put("DEAL_INFO", "调用退款接口成功，平台退款处理中");
			 }else{
				 inParam.put("DEAL_STATE", "2");//退款失败
				 updSql.append(",DEAL_INFO=:DEAL_INFO");
				 inParam.put("DEAL_INFO", data.getString("RESULT_INFO"));
			 }
		 //退款异步更新
		 }else{
			 if("010A00".equals(data.getString("orderStatusCode"))){//退款成功
				 inParam.put("DEAL_STATE", "1");//平台退款成功
				 
				 updSql.append(",IS_REFUND=:IS_REFUND");
				 inParam.put("IS_REFUND", "1");
			 }else{
				 inParam.put("DEAL_STATE", "4");//平台退款失败
			 }
			 updSql.append(",DEAL_INFO=:DEAL_INFO");
			 inParam.put("DEAL_INFO", "退款异步回调完成");
			 updSql.append(",ORDER_STATUS_DESC=:ORDER_STATUS_DESC");
			 inParam.put("ORDER_STATUS_DESC", data.getString("orderStatusCode","")+data.getString("orderStatusDesc",""));
		 }
		 updSql.append(" WHERE TRANS_ACTION_ID=:TRANS_ACTION_ID");
		 inParam.put("TRANS_ACTION_ID", data.getString("transactionId"));
		
		 Dao.executeUpdate(updSql, inParam);
	}
	
	//集中写卡业务开户==================================================================================
	/**
     * 查询集中写卡预开户订单
     * @param orderId
     * @param subOrderID
     * @return
     * @throws Exception
     */
    public static IDataset queryPreOrderInfo(String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT * FROM TF_F_PREORDER_INFO WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        return Dao.qryBySql(sql, param,  Route.CONN_CRM_CEN);
    }
    public static IDataset queryPreOrderInfoBySn(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("BILL_ID", serialNumber);
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT * FROM TF_F_PREORDER_INFO WHERE BILL_ID=:BILL_ID ORDER BY ACCEPT_DATE DESC");
        return Dao.qryBySql(sql, param,  Route.CONN_CRM_CEN);
    }
    
    public static int  updatePreOrderInfo(String state,String executeState,String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("EXEC_STATE", executeState);
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        if(state==null){
        	sql.append("UPDATE TF_F_PREORDER_INFO SET EXEC_STATE=:EXEC_STATE ");
        	if("2".equals(executeState)){
        		sql.append(",RSRV_STR5=to_char(sysdate,'yyyymmddhh24miss')");
        		sql.append(",RSRV_STR6=to_char(trunc(last_day(sysdate)+1),'yyyymmddhh24miss')");
        	}
        	sql.append(" WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        }else{
        	sql.append("UPDATE TF_F_PREORDER_INFO SET STATE=:STATE WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        }
        return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }
    //获取预开户成功或者激活失败的订单
    public static IDataset querySubOrderInfo(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERVICENO", serialNumber);
        StringBuilder sql=new StringBuilder();
        //AND STATE IN('OC','AF','SE','RE')
        sql.append("SELECT * FROM TF_B_CTRM_GERLSUBORDER WHERE SERVICENO=:SERVICENO  AND NUMBER_OPRTYPE IN('08','09') ORDER BY CREATE_TIME DESC");
        return Dao.qryBySql(sql, param,  Route.CONN_CRM_CEN);
    }
    public static int  updateSubOrderInfo(String state,String statusDesc,String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("STATUS_DESC", statusDesc);
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        sql.append("UPDATE TF_B_CTRM_GERLSUBORDER SET STATE=:STATE,STATUS_DESC=:STATUS_DESC,RSRV_STR1='' WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }
}
