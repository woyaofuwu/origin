package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.payment;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.rpc.org.jboss.netty.util.internal.StringUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;

public class PaymentBean extends  CSBizBean
{
	private final String insPayMoney ="INSERT INTO TF_B_TRADEFEE_PAYMONEY(ACCEPT_MONTH,PAY_MONEY_CODE,REMARK,UPDATE_TIME,ORDER_ID,MONEY,UPDATE_DEPART_ID,TRADE_ID,UPDATE_STAFF_ID) VALUES(:ACCEPT_MONTH,:PAY_MONEY_CODE,:REMARK,:UPDATE_TIME,:ORDER_ID,:MONEY,:UPDATE_DEPART_ID,:TRADE_ID,:UPDATE_STAFF_ID)";
	
	public IData payOrder(IData inParam) throws Exception
	{	
		IDataset payMoneyDatas = this.getPayMoneyDatas(inParam);
		if(IDataUtil.isNotEmpty(payMoneyDatas))
		{		
			/**
			 * REQ201704270011_关于购买大额有价卡的业务流程优化
			 * <br/>
			 * 未超过5000元,则不需要打印电子工单
			 * @author zhuoyingzhi
			 * @date 21080606
			 */
			String tradeTypeCode=inParam.getString("TRADE_TYPE_CODE");
			String tradeId = inParam.getString("TRADE_ID");
			boolean is5000=false;
			/*System.out.println("----PaymentBean-----tradeTypeCode:"+tradeTypeCode);*/
			if("416".equals(tradeTypeCode)){
				IDataset trade416=TradeInfoQry.getTradeInfobyTradeId(tradeId);
				if(IDataUtil.isNotEmpty(trade416)){
					IData tradeInfo=trade416.getData(0);
					//营业费用(单位是 分)
					double  operFee=tradeInfo.getDouble("OPER_FEE");
					IDataset  dataset=CommparaInfoQry.getCommparaByCode1("CSM", "2019", "201707171648", CSBizBean.getTradeEparchyCode());
					/*System.out.println("----PaymentBean-----dataset:"+dataset);*/
					if(IDataUtil.isNotEmpty(dataset)){
						double  paramCode=dataset.getData(0).getDouble("PARAM_CODE");
						/*System.out.println("----PaymentBean-----paramCode:"+paramCode);*/
						//500000
						Double operFeeDouble=Double.valueOf(operFee);
						if(operFeeDouble < paramCode){
							//未超过5000元,则不需要打印电子工单
							is5000=true;
						}
					}					
				}
			}
			for(int i=0;i<payMoneyDatas.size();i++)
			{
				IData paymoneyData = payMoneyDatas.getData(i);
				//Dao.executeUpdate(new StringBuilder(insPayMoney), paymoneyData, Route.getJourDb());
				//有价卡销售额度是否超过5000标识
				paymoneyData.put("VALUE_CARD_IS5000_FLAG", is5000);
				Dao.executeUpdateByCodeCode("TF_B_TRADEFEE_PAYMONEY", "INSERT_TRADEFEE_PAYMONEY2", paymoneyData, Route.getJourDb());
				this.updateTradeState(paymoneyData);
			}
			//有价卡销售额度是否超过5000标识
			inParam.put("VALUE_CARD_IS5000_FLAG", is5000);
			this.updateOrderState(inParam);
		}
		IData outData = new DataMap();
		outData.put("RESULT", "SUCCESS");
		
		return outData;
	}
	public IData payTrade(IData inParam) throws Exception
	{
		
		String tradeId = inParam.getString("TRADE_ID");
		String orderId = inParam.getString("ORDER_ID");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
		
		IDataset payFeeDatas = new DatasetList(inParam.getString("PAY_DETAIL"));
		
		if(IDataUtil.isNotEmpty(payFeeDatas))
		{		
			for(int i=0;i<payFeeDatas.size();i++)
			{
				IData temp = payFeeDatas.getData(i);
				temp.put("TRADE_ID", tradeId);
				temp.put("ORDER_ID", orderId);
				temp.put("MONEY", temp.getString("AMOUNT"));
				temp.put("PAY_MONEY_CODE", temp.getString("PAY_TYPE"));
				temp.put("UPDATE_TIME", SysDateMgr.getSysTime());
				temp.put("ACCEPT_MONTH", acceptMonth);
				temp.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
				temp.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
				temp.put("REMARK", temp.getString("REMARK", "pay sucess!"));
				//Dao.executeUpdate(new StringBuilder(insPayMoney), temp, Route.getJourDb());
				Dao.executeUpdateByCodeCode("TF_B_TRADEFEE_PAYMONEY", "INSERT_TRADEFEE_PAYMONEY2", temp, Route.getJourDb());
			}
			
			this.updateTradeState(inParam);
			this.updateOrderState(inParam);
		}
		
		IData outData = new DataMap();
		outData.put("RESULT", "SUCCESS");
		
		return outData;
	}
	
	public IDataset getPayMoneyDatas(IData param)throws Exception
	{
		IDataset payMoneyDatas = new DatasetList();
		String orderId = this.checkParam(param, "ORDER_ID");
		
		IDataset payFeeDatas = new DatasetList(param.getString("PAY_DETAIL"));
		if(IDataUtil.isEmpty(payFeeDatas))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"支付明细不能为空！");
		}
		IData payFeeData = payFeeDatas.getData(0);//目前支付中心不支持混合支付，所以同一笔支付只有一种方式
		
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT TRADE_ID FROM TF_B_TRADE A WHERE ORDER_ID =:ORDER_ID AND SUBSCRIBE_STATE ='X' ");
		IDataset tradeDatas = Dao.qryBySql(sql, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(tradeDatas))
		{
			for(int i=0;i<tradeDatas.size();i++)
			{
				IData temp = tradeDatas.getData(i);
				String tradeId = temp.getString("TRADE_ID");
				String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
				IDataset tradeFees = TradefeeSubInfoQry.qryTradeFeeSubByTradeId(tradeId, this.getTradeEparchyCode());
				if(IDataUtil.isNotEmpty(tradeFees))
				{
					long tradeFee = 0;
					for(int j=0;j<tradeFees.size();j++)
					{
						IData data = tradeFees.getData(j);
						tradeFee += data.getLong("FEE");
					}
					IData payMoneyData = new DataMap();
					payMoneyData.put("TRADE_ID", tradeId);
					payMoneyData.put("ORDER_ID", orderId);
					payMoneyData.put("MONEY", String.valueOf(tradeFee));
					payMoneyData.put("PAY_MONEY_CODE", payFeeData.getString("PAY_TYPE"));
					payMoneyData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					payMoneyData.put("ACCEPT_MONTH", acceptMonth);
					payMoneyData.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
					payMoneyData.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
					payMoneyData.put("REMARK", temp.getString("REMARK", "pay sucess!"));
					payMoneyDatas.add(payMoneyData);
				}
			}
		}
		return payMoneyDatas;
	}
	
	public String transToMoneyCode(String payType)throws Exception
	{
		String payMoneyCode ="0";
		if(StringUtils.equals("CASH", payType))
		{
			payMoneyCode = "0";
		}
		else if(StringUtils.equals("POS", payType))
		{
			payMoneyCode = "2";
		}
		else if(StringUtils.equals("WX", payType))
		{
			payMoneyCode = "w";
		}
		else if(StringUtils.equals("AP", payType))
		{
			payMoneyCode = "9";
		}
		else
		{
			payMoneyCode = "0";
		}
		return payMoneyCode;
	}
	
	public int updateTradeState(IData inParam)throws Exception
	{
		String tradeId = inParam.getString("TRADE_ID");
		String tradeTypeCode =inParam.getString("TRADE_TYPE_CODE");
		String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
		String isGroup = inParam.getString("IS_GROUP");
		boolean isPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CLOSE_NO_PRINT");
		String mustPrintTag = StaticUtil.getStaticValue("MUST_PRINT_SWITCH", "0");
		boolean isPrint = CommparaInfoQry.isTradeMustPrint(tradeTypeCode);
		
		StringBuilder sql = new StringBuilder(300);
		sql.append("UPDATE TF_B_TRADE  ");
		sql.append("SET SUBSCRIBE_STATE =:SUBSCRIBE_STATE, ");
		sql.append("INTF_ID = INTF_ID||'TF_B_TRADEFEE_PAYMONEY,' ");
		sql.append("WHERE TRADE_ID = :TRADE_ID ");
		sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
		
		
		StringBuilder sql2 = new StringBuilder(300);
		sql2.append("UPDATE TF_BH_TRADE_STAFF  ");
		sql2.append("SET SUBSCRIBE_STATE ='0' ");
		sql2.append("WHERE TRADE_ID = :TRADE_ID ");
		sql2.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
		
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("ACCEPT_MONTH", acceptMonth);
		/**
		 * REQ201704270011_关于购买大额有价卡的业务流程优化
		 * <br/>
		 * 未超过5000元,则不需要打印电子工单
		 * @author zhuoyingzhi
		 * @date 21080606
		 */
		//如果有价卡销售额度未超过5000元，则不需要打印电子工单
		boolean  valueCardIsFlag=inParam.getBoolean("VALUE_CARD_IS5000_FLAG",false);
		/*****************************************/
		if("X".equals(this.getInModeCode(inParam)) || valueCardIsFlag){//自助终端过来的直接改为0
			param.put("SUBSCRIBE_STATE","0");
		}else{
			param.put("SUBSCRIBE_STATE", (!StringUtils.equals("on", mustPrintTag)||StringUtils.equals("TRUE", isGroup)||isPriv||isPrint)?"0":"Y");//集团业务,或者有权限直接改为0
		}
		
		Dao.executeUpdate(sql2, param,Route.getJourDb());
		
		return  Dao.executeUpdate(sql, param,Route.getJourDb());
		
	}
	
	public int updateOrderState(IData inParam)throws Exception
	{
		String orderId = inParam.getString("ORDER_ID");
		String tradeTypeCode =inParam.getString("TRADE_TYPE_CODE");
		String acceptMonth = StrUtil.getAcceptMonthById(orderId);
		String isGroup = inParam.getString("IS_GROUP");
		boolean isPriv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CLOSE_NO_PRINT");
		String mustPrintTag = StaticUtil.getStaticValue("MUST_PRINT_SWITCH", "0");
		boolean isPrint = CommparaInfoQry.isTradeMustPrint(tradeTypeCode);
		
		StringBuilder sql = new StringBuilder(300);
		sql.append("UPDATE TF_B_ORDER  ");
		sql.append("SET ORDER_STATE =:ORDER_STATE ");
		sql.append("WHERE ORDER_ID = :ORDER_ID ");
		sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");
		
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		param.put("ACCEPT_MONTH", acceptMonth);
		
		if("X".equals(this.getInModeCode(inParam))){//自助终端过来的直接改为0
			param.put("ORDER_STATE","0");
		}else{
			param.put("ORDER_STATE", (!StringUtils.equals("on", mustPrintTag)||StringUtils.equals("TRUE", isGroup)||isPriv||isPrint)?"0":"Y");//集团业务,或者有权限直接改为0
		}
		
		return  Dao.executeUpdate(sql, param,Route.getJourDb());
		
	}
	
	public IDataset getTradeFees(IData param) throws Exception
	{
		String tradeId = this.checkParam(param, "TRADE_ID");
		
		String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
		String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
		if(StringUtils.isBlank(tradeTypeName))tradeTypeName="业务费用";
		
		StringBuilder sql2 = new StringBuilder(500);
		sql2.append("SELECT TRADE_ID,SUM(FEE) FEE  ");
		sql2.append("FROM TF_B_TRADEFEE_SUB ");
		sql2.append(" WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)  AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) GROUP BY TRADE_ID  ");

		IDataset tradeFees = Dao.qryBySql(sql2, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(tradeFees))
		{
			tradeFees.getData(0).put("TRADE_NAME", tradeTypeName);
		}
		return tradeFees;
	}
	
	public IDataset getOrderFees(IData param) throws Exception
	{
		IDataset orderFees = new DatasetList();
		String orderId = this.checkParam(param, "ORDER_ID");
		if(!StringUtils.isNumeric(orderId)){
			return orderFees;
		}
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT TRADE_ID,TRADE_TYPE_CODE FROM TF_B_TRADE A WHERE ORDER_ID =:ORDER_ID AND SUBSCRIBE_STATE ='X' AND CANCEL_TAG='0' ");
		//支付中心要求合并费用明细
		StringBuilder sql2 = new StringBuilder(500);
		sql2.append("SELECT TRADE_ID,SUM(FEE) FEE  ");
		sql2.append("FROM TF_B_TRADEFEE_SUB ");
		sql2.append(" WHERE TRADE_ID = TO_NUMBER(:TRADE_ID)  AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) GROUP BY TRADE_ID  ");
		
		IDataset tradeDatas = Dao.qryBySql(sql, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(tradeDatas))
		{
			for(int i=0;i<tradeDatas.size();i++)
			{
				IData temp = tradeDatas.getData(i);
				String tradeId = temp.getString("TRADE_ID");
				String tradeTypeCode = temp.getString("TRADE_TYPE_CODE");
				String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
				if(StringUtils.isBlank(tradeTypeName))tradeTypeName="业务费用";
				IDataset tradeFees =Dao.qryBySql(sql2, temp, Route.getJourDb());
				if(IDataUtil.isNotEmpty(tradeFees))
				{
					tradeFees.getData(0).put("TRADE_NAME", tradeTypeName);
					orderFees.addAll(tradeFees);
				}
			}
		}
		return orderFees;
	}
	
	public IDataset getLoginMessage(IData param) throws Exception
	{
		IDataset messages = new DatasetList();
		String staffId = this.checkParam(param, "STAFF_ID");
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT * FROM TF_B_TRADE A WHERE TRADE_STAFF_ID =:STAFF_ID AND SUBSCRIBE_STATE in('X','Y') AND CANCEL_TAG='0' ");
		IDataset tradeDatas = Dao.qryBySql(sql, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(tradeDatas))
		{
			IData message = new DataMap();
			message.put("INFO_TOPIC", "您有待支付/打印的工单");
			message.put("INFO_ID", "UN_PAID_PRINT");
			message.put("JUMP_URL", "/order/order?service=page/unpaidorderdeal.UnpaidOrderDeal&listener=init");
			messages.add(message);
		}
		return messages;
	}

	public IDataset getLoginMessageNew(IData param) throws Exception
	{
		IDataset messages = new DatasetList();
		String staffId = this.checkParam(param, "STAFF_ID");
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT * FROM TF_B_TRADE A WHERE TRADE_STAFF_ID =:STAFF_ID AND SUBSCRIBE_STATE in('X','Y') AND CANCEL_TAG='0' ");
		IDataset tradeDatas = Dao.qryBySql(sql, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(tradeDatas))
		{
			IData message = new DataMap();
			message.put("INFO_TOPIC", "您有待支付/打印的工单");
			message.put("INFO_ID", "UN_PAID_PRINT");
			message.put("JUMP_URL", "/order/iorder?service=page/unpaidorderdeal.UnpaidOrderDealNew&listener=init");
			messages.add(message);
		}
		return messages;
	}
	
	public String checkParam(IData param ,String key) throws Exception
	{
		String value = param.getString(key);
		if(StringUtils.isBlank(value))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "参数【"+key+"】必须传入");
		}
		return value;
	}
	
	public IDataset getTradePayMoney(IData param) throws Exception
	{
		String startDate = this.checkParam(param, "START_DATE");
		String endDate = this.checkParam(param, "END_DATE");
		
		IData data = new DataMap();
		data.put("START_DATE", startDate);
		data.put("END_DATE", endDate);
		data.put("ACCEPT_MONTH", startDate.substring(5,7));
		
		StringBuilder sql = new StringBuilder(500);
		sql.append("SELECT B.ORDER_ID,A.TRADE_ID,A.PAY_MONEY_CODE,A.MONEY,A.UPDATE_TIME,DECODE(B.CANCEL_TAG,'0','U','1','C','2','R') CANCEL_TAG  ");
		sql.append("FROM TF_B_TRADEFEE_PAYMONEY A,TF_B_TRADE B ");
		sql.append("WHERE A.TRADE_ID = B.TRADE_ID  ");
		sql.append("AND A.ACCEPT_MONTH =:ACCEPT_MONTH ");
		sql.append("AND B.ACCEPT_MONTH =:ACCEPT_MONTH ");
		sql.append("AND A.PAY_MONEY_CODE IN ('w','9') ");
		sql.append("AND A.UPDATE_TIME BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') ");
		sql.append("UNION ALL ");
		sql.append("SELECT B.ORDER_ID,A.TRADE_ID,A.PAY_MONEY_CODE,A.MONEY,A.UPDATE_TIME,DECODE(B.CANCEL_TAG,'0','U','1','C','2','R') CANCEL_TAG  ");
		sql.append("FROM TF_B_TRADEFEE_PAYMONEY A,TF_BH_TRADE B ");
		sql.append("WHERE A.TRADE_ID = B.TRADE_ID  ");
		sql.append("AND A.ACCEPT_MONTH =:ACCEPT_MONTH ");
		sql.append("AND B.ACCEPT_MONTH =:ACCEPT_MONTH ");
		sql.append("AND A.PAY_MONEY_CODE IN ('w','9') ");
		sql.append("AND A.UPDATE_TIME BETWEEN TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS') ");
		
		return Dao.qryBySql(sql, data, Route.getJourDb());
	}
	/**
	 * 获取IN_MODE_CODE
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String getInModeCode(IData param)throws Exception
	{
		if(StringUtils.isEmpty(param.getString("ORDER_ID"))){
			return "";
		}
		String inModeCode="";
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT IN_MODE_CODE FROM TF_B_TRADE A WHERE ORDER_ID =:ORDER_ID");
		IDataset tradeDatas = Dao.qryBySql(sql, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(tradeDatas)){
			inModeCode=tradeDatas.getData(0).getString("IN_MODE_CODE", "");
		}
		return inModeCode;
	}
}
