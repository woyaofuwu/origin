package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;


import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


public class VirtualDataInvalidSVC extends CSBizService {
	private static final long serialVersionUID = 1L;
	private static final String TABLE_VIRTUAL_CHARGE="TD_S_VIRTUAL_CHARGE";
	private static final String TABLE_VIRTUAL_DATA="TD_S_VIRTUAL_DATA";
	private static final String TABLE_DISCNT_INVALID="TD_S_DISCNT_INVALID";
	private static final String TABLE_CASH_INVALID="TD_S_CASH_INVALID";
     /**
      * 7.6.1虚拟流量中的现金账户流量失效通知接口
      * @param data
      * @return
      * @throws Exception
      */
//	public IData cashInvalid(IData data) throws Exception {
//		// 接收并校验必传入参
//		IData inParam = new DataMap();
//		inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
//		inParam.put("CASH_FLOW", data.getString("CASH_FLOW"));
//		inParam.put("EXPIRE_TIME", data.getString("EXPIRE_TIME"));
//		inParam.put("SETTLE_DATE", data.getString("SETTLE_DATE"));
//		inParam.put("EXPIRE_ID", data.getString("EXPIRE_ID"));
//		//chkParams(inParam);
//		inParam.put("REMARK", data.getString("REMARK"));
//		// 数据入库
//		VirtualDataInvalidBean virtualbean = new VirtualDataInvalidBean();
//		virtualbean.insertTable(inParam,TABLE_CASH_INVALID,Route.CONN_CRM_CEN);	
//		
//		// 构造返回参数
//		IData returnParam = new DataMap();
//		returnParam.put("RSP_CODE", "0000");
//		returnParam.put("RSP_DESC", "虚拟流量中的现金账户流量失效通知接口数据库入库成功");
//		returnParam.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
//
//		// 返回相应参数
//		return returnParam;
//	}
    /**
     * 7.6.2虚拟流量中的优惠账户流量失效通知接口
     * @param data
     * @return
     * @throws Exception
     */
//	public IData discntInvalid(IData data) throws Exception {
//		// 接收并校验必传入参
//		IData inParam = new DataMap();
//		inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
//		inParam.put("GIVE_FLOW", data.getString("GIVE_FLOW"));
//		inParam.put("EXPIRE_TIME", data.getString("EXPIRE_TIME"));
//		inParam.put("SETTLE_DATE", data.getString("SETTLE_DATE"));
//		inParam.put("EXPIRE_ID", data.getString("EXPIRE_ID"));
//		//chkParams(inParam);
//		inParam.put("REMARK", data.getString("REMARK"));
//		// 数据入库
//		VirtualDataInvalidBean virtualbean = new VirtualDataInvalidBean();
//		virtualbean.insertTable(inParam,TABLE_DISCNT_INVALID,Route.CONN_CRM_CEN);	
//
//		// 构造返回参数
//		IData returnParam = new DataMap();
//		returnParam.put("RSP_CODE", "0000");
//		returnParam.put("RSP_DESC", "虚拟流量中的优惠账户流量失效通知接口数据库入库成功");
//		returnParam.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
//
//		// 返回相应参数
//		return returnParam;
//	}
	/**
	 * 7.5虚拟流量购买通知文件接口
	 */
	public IData buyData(IData data) throws Exception {
		IData returnParam = new DataMap();
		returnParam.put("RSP_CODE", "0000");
		returnParam.put("RSP_DESC", "虚拟流量购买数据库入库成功");
//		IData inParam = new DataMap();
		data.put("ID_TYPE",IDataUtil.chkParam(data, "ID_TYPE")); 
		data.put("SERIAL_NUMBER", IDataUtil.chkParam(data, "SERIAL_NUMBER"));
		data.put("TRANSACTION_ID", IDataUtil.chkParam(data, "TRANSACTION_ID"));
		data.put("ACTION_DATE", IDataUtil.chkParam(data, "ACTION_DATE"));
		data.put("ACTION_TIME", IDataUtil.chkParam(data, "ACTION_TIME"));
		int virtFlow=data.getInt("VIRT_FLOW");
		if(virtFlow<0){
			returnParam.put("RSP_CODE", "9999");
			returnParam.put("RSP_DESC", "虚拟流量值必须为非负整数");
			return returnParam;
		}
		data.put("VIRT_FLOW", virtFlow);//要求必须为非负整数，流量单位：M（兆）
		data.put("ORGAN_ID", "SHOP");//固定为：SHOP 表示移动商城
		data.put("CNL_TYPE", IDataUtil.chkParam(data, "CNL_TYPE"));
		data.put("PAYED_TYPE","01");//固定填写01
		data.put("SETTLE_DATE", data.getString("SETTLE_DATE"));
		data.put("PAYMENT", data.getInt("PAYMENT"));
		// 数据入库
		VirtualDataInvalidBean virtualbean = new VirtualDataInvalidBean();
		virtualbean.insertTable(data,TABLE_VIRTUAL_DATA,Route.CONN_CRM_CEN);		    	
     	if("0000".equals(returnParam.getString("RSP_CODE"))){
		returnParam.put("ID_TYPE", data.getString("ID_TYPE"));
		returnParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		returnParam.put("TRANSACTION_ID", data.getString("TRANSACTION_ID"));
		returnParam.put("ACTION_DATE", data.getString("ACTION_DATE"));
     	}
     	return returnParam;
	}
	  /**
     * @Description: 移动商城接口2.0-流量直充接口(虚拟流量)
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160523
     */
	public IData flowPaymentForXN(IData input) throws Exception{
		
		IDataUtil.chkParam(input, "ORGAN_ID");           // 机构编码
		IDataUtil.chkParam(input, "TRANSACTION_ID");      //操作流水号
		IDataUtil.chkParam(input, "VIRT_FLOW");       // 虚拟流量
		IDataUtil.chkParam(input, "CASH_FLOW");        // 现金流量
		IDataUtil.chkParam(input, "PRESENT_FLOW");       // 优惠流量
		IDataUtil.chkParam(input, "CNL_TYPE");//渠道标识
		IDataUtil.chkParam(input, "PAY_TYPE");//缴费类型
		IDataUtil.chkParam(input, "SETTLE_DATE");//交易帐期		
//		IDataUtil.chkParam(input, "PAYMENT");         // 订单总金额
				
		IData paramer = new DataMap();  
		int counts = Integer.parseInt(input.getString("ORDER_CNT"));//订单数量
		String discntId =IDataUtil.chkParam(input, "PRODUCT_NO");//产品编码
		String elementTypeCode = "D";
		String modifyTag = "0";
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");         // 手机号
		//返回参数
		IData result = new DataMap(); 
		result.put("ID_TYPE", input.getString("ID_TYPE"));
		result.put("SERIAL_NUMBER", serialNumber);
		result.put("TRANSACTION_ID", input.getString("TRANSACTION_ID"));
		result.put("ACTION_DATE", input.getString("ACTION_DATE"));
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
		//返回参数
		
		input.put("IDVALUE", serialNumber);
		input.put("TRANS_ID", input.getString("TRANSACTION_ID"));
		ChangeProductBean bean =  new ChangeProductBean();
		IDataset queryResult = bean.queryFlowPayment(input);
		if(IDataUtil.isNotEmpty(queryResult)){
			result.put("X_RSPTYPE", "2");
			result.put("X_RESULTCODE", "3A34");
			result.put("X_RESULTINFO","重复交易，该交易已处理成功");
			result.put("RSP_DESC", "重复交易，该交易已处理成功");
	      	result.put("RSP_CODE", "3A34");
	      	//将CRM处理结果回写给一级BOSS对账用UOP_CEN1.TO_O_FLOWDIRECT_MOBILEMALL
//	      	paramer.put("BUSYID",input.getString("UIPBUSIID"));
//	      	paramer.put("CRM_RESULT","2998");
//			bean.updateIbossCrmResult(paramer);
			return result;
		}		
		IDataset dataset = CommparaInfoQry.getCommparaInfos("CSM","2788",discntId);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据流量直充产品编码没有查询到对应的资费");
		}else{
			discntId = dataset.getData(0).getString("PARA_CODE1");
		}
		//订购数量限制,各档位流量包每月累计限购/叠加/兑换次数为10次
//		if(StringUtils.isNotEmpty(flowType) && "PAY_GIFT_SCORE".contains(flowType)){
//			int discntCounts = 0;
//			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
//			List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
//			IDataset commparaResults = CommparaInfoQry.getCommparaInfoByParacode4AndAttr("CSM","2788", flowType, null);
//			for (DiscntTradeData userDiscnt : userDiscnts ){
//	            String discntCode  = userDiscnt.getElementId();
//	            for(int i = 0,j = commparaResults.size(); i < j; i++){
//	            	if(discntCode.equals(discntId)&&discntCode.equals(commparaResults.getData(i).getString("PARA_CODE1"))){
//	            		discntCounts++;
//	            		break;
//	            	}
//	            }	            
//			}
//			if(discntCounts>=10){
//				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户本月拥有的相同档次流量包产品已达到上限10次！");
//			}
//		}
		
		if(counts > 1){
			//订购数量大于1，则进行参数转换
			discntId = paramConvert(discntId,counts);
			elementTypeCode = paramConvert(elementTypeCode,counts);
			modifyTag = paramConvert(modifyTag,counts);
		}
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("ELEMENT_ID", discntId);
		param.put("ELEMENT_TYPE_CODE", elementTypeCode);
		param.put("MODIFY_TAG", modifyTag);
		param.put("FLOW_PAYMENT_ID", input.getString("TRANSACTION_ID"));
		param.put("NUM", input.getString("ORDER_CNT"));
		param.put("UNI_CHANNEL", input.getString("RESERVE2",""));//全网渠道19位编码
		param.put("NET_EXPENSES_CODE", input.getString("RESERVE3",""));//全网资费编码
		param.put("OPRNUMB", input.getString("TRANSACTION_ID"));//流水号
//		if("PAY".equals(flowType)){//流量直充才有发票,赠送和积分兑换没有发票
			int payMent = input.getInt("PAYMENT",0);        //实付金额
			int discnt = input.getInt("PROD_DISCOUNT",0); //产品折减金额
			int relPayMent = payMent+discnt;              //应付金额
			param.put("PRINT_TICKET", "1");
			param.put("PAYMENT", payMent);
			param.put("REL_PAYMENT", relPayMent);
//		}		
		try{                          
			IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
			result = results.getData(0);
		} catch (Exception e){
			result.put("X_RSPTYPE", "2");
			//将CRM处理结果回写给一级BOSS对账用UOP_CEN1.TO_O_FLOWDIRECT_MOBILEMALL
//	      	paramer.put("BUSYID",input.getString("UIPBUSIID"));
//	      	paramer.put("CRM_RESULT","2998");
//			bean.updateIbossCrmResult(paramer);
	      	if(null != e.getMessage()){
				if(e.getMessage().contains("用户已订购该优惠")){
					result.put("X_RESULTCODE", "3A34");
					result.put("X_RESULTINFO","用户已订购该优惠");
					result.put("RSP_CODE", "3A34");
			      	result.put("RSP_DESC",  "用户已订购该优惠");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else if(e.getMessage().contains("用户有未完工的订单")){
					result.put("X_RESULTCODE", "998");
					result.put("X_RESULTINFO","用户有未完工的订单");
					result.put("RSP_CODE", "998");
			      	result.put("RSP_DESC",  "用户有未完工的订单");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else if(e.getMessage().contains("用户不存在")){
					result.put("X_RESULTCODE", "810");
					result.put("X_RESULTINFO","该用户不存在或已经销号");
					result.put("RSP_CODE", "810");
			      	result.put("RSP_DESC",  "该用户不存在或已经销号");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else if(e.getMessage().contains("停机")){
					result.put("X_RESULTCODE", "2005");
					result.put("X_RESULTINFO","用户已停机");
					result.put("RSP_CODE", "2005");
			      	result.put("RSP_DESC",  "用户已停机");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else{
					result.put("X_RESULTCODE", "2997");
					result.put("X_RESULTINFO",e.getMessage());
					result.put("RSP_CODE", "2997");
			      	result.put("RSP_DESC",  e.getMessage());
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}
	      	}else{	      		
	      		result.put("RSP_CODE", "4024");
	      		result.put("RSP_DESC", "产品编码非法");
	      		result.put("X_RESULTCODE", "4024");
		      	result.put("X_RESULTINFO",  "产品编码非法");
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
				return result;
	      	}
		}
		
		String VIRT_ACCOUNT_CUTFLOW=input.getString("VIRT_ACCOUNT_CUTFLOW");
		if(VIRT_ACCOUNT_CUTFLOW==null){
			VIRT_ACCOUNT_CUTFLOW="0";
		}
		VIRT_ACCOUNT_CUTFLOW=VIRT_ACCOUNT_CUTFLOW.trim();
		if("".equals(VIRT_ACCOUNT_CUTFLOW)){
			VIRT_ACCOUNT_CUTFLOW="0";
		}
		
		
		String CUT_PROPORTION=input.getString("CUT_PROPORTION");
		if(CUT_PROPORTION==null){
			CUT_PROPORTION="0";
		}
		CUT_PROPORTION=CUT_PROPORTION.trim();
		if("".equals(CUT_PROPORTION)){
			CUT_PROPORTION="0";
		}
				
		//移动商城2.8 支撑虚拟流量提取不同类型的换算，省份只需保存新增的字段，用于后续流量结算依据; 需修改表（TD_S_VIRTUAL_CHARGE）结构新增两个字段 add by huangyq
		input.put("VIRT_ACCOUNT_CUTFLOW", Integer.valueOf(VIRT_ACCOUNT_CUTFLOW));
		input.put("CUT_PROPORTION", Integer.valueOf(CUT_PROPORTION));
		
	    
		//将CRM处理结果回写给一级BOSS对账用UOP_CEN1.TO_O_FLOWDIRECT_MOBILEMALL
//      	paramer.put("BUSYID",input.getString("UIPBUSIID"));
//      	paramer.put("CRM_RESULT","0000");
//		bean.updateIbossCrmResult(paramer);	
		result.put("X_RSPTYPE", "0");
		result.put("RSP_CODE", "0000");
		result.put("RSP_DESC", "OK");
		VirtualDataInvalidBean virtualbean = new VirtualDataInvalidBean();
		virtualbean.insertTable(input,TABLE_VIRTUAL_CHARGE,Route.CONN_CRM_CEN);		
		return result;
	}
	public String paramConvert(String param,int counts){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<counts;i++){
			if(i == counts-1){
				sb.append(param);
			}else{
				sb.append(param).append(",");
			}			
		}		
		return sb.toString();
	}
	/**
	 * 电子发票记录查询接口
	 * 根据号码获取发票打印业务信息
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData getNotePrintTradeInfo(IData input) throws Exception {
		
		String[] paramKeys = { "SERIAL_NUMBER" , "START_DATE", "END_DATE"};
		checkParams(input, paramKeys);

		VirtualDataInvalidBean bean = new VirtualDataInvalidBean();
		return bean.getNotePrintTradeInfo(input);
	}
	/**
	 * 校验接口必填参数
	 * 
	 * @param input
	 * @param params
	 * @throws Exception
	 */
	public void checkParams(IData input, String[] params) throws Exception {
		for (String param : params) {
			IDataUtil.chkParam(input, param);
		}
	}
	/**
	 * 移动商城流量打印电子发票接口
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset printKJ4SC(IData input) throws Exception {
		String[] paramKeys = { "SERIAL_NUMBER", "RECEIPT_ID" };
		checkParams(input, paramKeys);
		input.put("TRADE_ID", input.getString("RECEIPT_ID"));
		VirtualDataInvalidBean bean = new VirtualDataInvalidBean();
		return bean.printKJ4SC(input);
	}
}
