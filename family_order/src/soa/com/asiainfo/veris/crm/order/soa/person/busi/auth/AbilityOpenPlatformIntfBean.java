package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryInfoUtil;

public class AbilityOpenPlatformIntfBean extends CSBizBean{
	
	Logger logger = Logger.getLogger(AbilityOpenPlatformIntfBean.class); 
	/**
	 * 终端产品同步
	 * @param pd
	 * @param data
	 * @throws Exception
	 */
	public boolean terminalSynchro(IData data) throws Exception
	{
		boolean isreturn=false;
		if("1".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.insert("TD_B_CTRM_TERMINAL", data,Route.CONN_CRM_CEN);
		}else if("2".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.save("TD_B_CTRM_TERMINAL", data,new String[]{"TERMINAL_ID"},Route.CONN_CRM_CEN);
			
		}else if("3".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.delete("TD_B_CTRM_TERMINAL", data,new String[]{"TERMINAL_ID"},Route.CONN_CRM_CEN);
		}
		return isreturn;
		
	}
	/**
	 * 合约同步
	 * @param pd
	 * @param data
	 * @throws Exception
	 */
	public boolean contractProductSynchro(IData data) throws Exception
	{
		boolean isreturn=false;
		if("1".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.insert("TD_B_CTRM_CONTRACT", data,Route.CONN_CRM_CEN);
		}else if("2".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.save("TD_B_CTRM_CONTRACT", data,new String[]{"CONTRACT_ID"},Route.CONN_CRM_CEN);
		}else if("3".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.delete("TD_B_CTRM_CONTRACT", data,new String[]{"CONTRACT_ID"},Route.CONN_CRM_CEN);
		}
		return isreturn;
		
	}
	/**
	 * 套餐产品同步
	 * @param pd
	 * @param data
	 * @throws Exception
	 */
	public boolean productInfoSynchro(IData data) throws Exception
	{
		boolean isreturn=false;
		if("1".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.insert("TD_B_CTRM_VAS", data,Route.CONN_CRM_CEN);
		}else if("2".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.save("TD_B_CTRM_VAS", data,new String[]{"VAS_ID"},Route.CONN_CRM_CEN);
		}else if("3".equals(data.getString("CHANGE_TYPE"))){
			isreturn=Dao.delete("TD_B_CTRM_VAS", data,new String[]{"VAS_ID"},Route.CONN_CRM_CEN);
		}
		return isreturn;
		
	}

	
	
	/**
	 * 商品信息同步
	 * @param pd
	 * @param data
	 * @throws Exception
	 */
	public boolean goodsInfoSynchro(IData data,String tag) throws Exception
	{
		boolean isreturn=false;
		if("g".equals(tag)){
			if("1".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.insert("TD_B_CTRM_GOODS", data,Route.CONN_CRM_CEN);
			}else if("2".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.save("TD_B_CTRM_GOODS", data,new String[]{"GOODS_ID"},Route.CONN_CRM_CEN);
			}else if("3".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.delete("TD_B_CTRM_GOODS", data,new String[]{"GOODS_ID"},Route.CONN_CRM_CEN);
			}
		}else if("ps".equals(tag)){
			if("1".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.insert("TD_B_CTRM_GOODS_GROUP", data,Route.CONN_CRM_CEN);
			}else if("2".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.save("TD_B_CTRM_GOODS_GROUP", data,new String[]{"GOODS_ID","GROUP_INDEX"},Route.CONN_CRM_CEN);
			}else if("3".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.delete("TD_B_CTRM_GOODS_GROUP", data,new String[]{"GOODS_ID","GROUP_INDEX"},Route.CONN_CRM_CEN);
			}
		}else if("p".equals(tag)){
			if("1".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.insert("TD_B_CTRM_GOODS_PRODUCT", data,Route.CONN_CRM_CEN);
			}else if("2".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.save("TD_B_CTRM_GOODS_PRODUCT", data,new String[]{"GOODS_ID","GROUP_INDEX","PRODUCT_ID"},Route.CONN_CRM_CEN);
			}else if("3".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.delete("TD_B_CTRM_GOODS_PRODUCT", data,new String[]{"GOODS_ID","GROUP_INDEX","PRODUCT_ID"},Route.CONN_CRM_CEN);
			}
		}else if("attr".equals(tag)){
			if("1".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.insert("TD_B_CTRM_GOODS_ATTR", data,Route.CONN_CRM_CEN);
			}else if("2".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.save("TD_B_CTRM_GOODS_ATTR", data,new String[]{"GOODS_ID","ADD_ID"},Route.CONN_CRM_CEN);
			}else if("3".equals(data.getString("CHANGE_TYPE"))){
				isreturn=Dao.delete("TD_B_CTRM_GOODS_ATTR", data,new String[]{"GOODS_ID","ADD_ID"},Route.CONN_CRM_CEN);
			}
		}
		return isreturn;
		
	}
	
	public void insertIntoCtrmTList(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_TLIST", data);
	}
	
	public void insertIntoCtrmOrder(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_ORDER", data);
	}
	
	public void insertIntoCtrmOrderProduct(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_ORDER_PRODUCT", data);
	}
	
	public void insertIntoCtrmOrderAttr(DBConnection conn,IData data) throws Exception{
		BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
		
		dao.insert(conn, "TF_B_CTRM_ORDER_ATTR", data);
	}
	
	
	
	/**
	 * 批量更新表数据
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateBatchInfo(String tabName,String sqlRef,IDataset params) throws Exception{
		Dao.executeBatchByCodeCode(tabName, sqlRef, params,Route.CONN_CRM_CEN);
	}

	
	
	
	/**
	 * 更新订单表中的状态
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateInfoById(IData param) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER_BYID", param,Route.CONN_CRM_CEN);
		
	}
	
	/**
	 * 更新订单表中的状态
	 * @param updateOrder
	 * @throws Exception
	 */
	public void updateInfoForTid(IData param) throws Exception{
		Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST","UPD_CTRM_ORDER_TID_OID", param,Route.CONN_CRM_CEN);
		
	}
	/**
	 * 填充字符串 例：数据库生成了5位的数字sequence，但构成流水号需要8位，那么可通过此方法，在左边补零
	 * 
	 * @param srcStr 原始字符串
	 * @param fillStr  要填充到原始字符串中的字符串
	 * @param totalLen 填充后的总长度
	 * @param leftFlag   左填充标志 为true在srcStr左边填充fillStr 为false在srcStr右边填充fillStr
	 * @return
	 */
	public  String fillStr(String srcStr, String fillStr, int totalLen,boolean leftFlag){
		
		if (srcStr == null)
			return null;
		if (srcStr.length() > totalLen || fillStr == null
				|| fillStr.length() == 0)
			return srcStr;
		if (((totalLen - srcStr.length()) % fillStr.length()) != 0)
			return srcStr;
		String result = srcStr;
		
		int i = totalLen - srcStr.length();
		while (i > 0)
		{
			result = leftFlag ? (fillStr + result) : (result + fillStr);
			i = i - fillStr.length();
		}
		return result;
	}
	

	
	/**
	 * 订单退款同步
	 * @param input
	 * @throws Exception
	 */
	public void refundOrderSynchro(IData input) throws Exception{
		
		boolean contractFlag = false;
		String serialNumber = "";
		
		//查询合约信息
		IData param = new DataMap();
		param.put("TID", input.getString("TID"));
		param.put("OID", input.getString("OID"));
		param.put("CTRM_PRODUCT_TYPE", "2");
		IDataset orderContractInfos = CSAppCall.call("SS.ShoppingOrderSVC.queryProOrderByCtrmType", param);
		IData orderContractInfo = new DataMap();
		if (IDataUtil.isNotEmpty(input) && IDataUtil.isNotEmpty(orderContractInfos)) {
			orderContractInfo = orderContractInfos.getData(0);
			serialNumber = orderContractInfo.getString("PHONE");
			IData inputData = new DataMap();
			input.put("SERIAL_NUMBER", 	orderContractInfo.getString("PHONE"));
			inputData.put("PID", orderContractInfo.getString("PID"));
			inputData.put("TRADE_ID", "-1");
			inputData.put("ACCEPT_DATE", SysDateMgr.getSysDate());
			inputData.put("STATUS", "5");
			inputData.put("ACCEPT_RESULT", "1");
			inputData.put("UPDATE_TIME", SysDateMgr.getSysDate());
			inputData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
			inputData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
			
			if("1".equals(orderContractInfo.getString("RSRV_STR2"))){      //1--预存购机直接无条件返回成功
				//同步信息插入订单子产品表
				contractFlag = true;
				input.put("RSP_TIME", SysDateMgr.getSysTime());
				inputData.put("STATUS", "4");
				CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
				
			}else if("2".equals(orderContractInfo.getString("RSRV_STR2"))){   //2--购机赠送费用
				//调返销流程,确定TRADE_ID不为空的时候才调用合约返销流程，否则认为合约还未执行.
				contractFlag = true;
				input.put("RSP_TIME", SysDateMgr.getSysTime());
			
				if(!"".equals(orderContractInfo.getString("TRADE_ID")) && !"-1".equals(orderContractInfo.getString("TRADE_ID"))){
					IData inParam = new DataMap();
					inParam.put("SERIAL_NUMBER", 	orderContractInfo.getString("PHONE"));
					inParam.put("TRADE_ID", 		orderContractInfo.getString("TRADE_ID"));
					inParam.put(Route.ROUTE_EPARCHY_CODE, input.getString(Route.ROUTE_EPARCHY_CODE,"0898"));
	            	try{
	            		IDataset retnList = CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", inParam);
	            		IData contractData = (retnList != null && retnList.size() > 0) ? retnList.getData(0):new DataMap() ;
	            	    if (StringUtils.isNotBlank(contractData.getString("ORDER_ID")) 
								&& !"-1".equals(contractData.getString("ORDER_ID"))){
	            	    	inputData.put("STATUS", "4");
	            	    }
	    			}catch(Exception ex){
	    				SessionManager.getInstance().rollback();
	    				logger.error("lixm6refundOrderSynchro= "+ex.getMessage());
	    			}
				}

				CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
				
				//查询产品信息
				param.put("CTRM_PRODUCT_TYPE", "3");
				IDataset orderProductInfos = CSAppCall.call("SS.ShoppingOrderSVC.queryProOrderByCtrmType", param);
				
				if (IDataUtil.isNotEmpty(orderProductInfos)) {
					inputData.put("STATUS", "5");
					IData orderProductInfo = orderProductInfos.getData(0);
					if(!"".equals(orderProductInfo.getString("TRADE_ID")) && !"-1".equals(orderProductInfo.getString("TRADE_ID"))){
						IData inparam = new DataMap();
						inparam.put("SERIAL_NUMBER", orderProductInfo.getString("PHONE"));
				        IDataset errTradeInfos = CSAppCall.call("SS.CancelChangeProductSVC.queryErrorInfoTrade", inparam);
				        if (IDataUtil.isEmpty(errTradeInfos)){
				            IDataset cancelTradeInfos = CSAppCall.call("SS.CancelChangeProductSVC.queryChangeProductTrade", inparam);
				            //存在预约产品变更，需要进行取消处理
				            if (IDataUtil.isNotEmpty(cancelTradeInfos)){
				            	inparam.put("TRADE_ID", orderProductInfo.getString("TRADE_ID"));
				            	try{
				            		IDataset retnList =  CSAppCall.call("SS.CancelChangeProductSVC.cancelChangeProductTrade", inparam);
				            		IData proData = (retnList != null && retnList.size() > 0) ? retnList.getData(0):new DataMap() ;
				            	    if (StringUtils.isNotBlank(proData.getString("ORDER_ID")) 
											&& !"-1".equals(proData.getString("ORDER_ID"))){
				            	    	inputData.put("STATUS", "4");
				            	    }
				            	}catch(Exception ex){
									logger.error("lixm6refundOrderSynchro产品取消= "+ex.getMessage());
				    			}
				            	
				            	CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
				            }
				        }
					}
				}
			}
			
			
			if (contractFlag) {
				//发短信
				input.put("PARAM_CODE", "RETURN_ORDER");
				input.put("%101!", input.getString("CHANNEL_ID"));
				input.put("%103!", orderContractInfo.getString("PRODUCT_ID"));
				input.put("%105!", SysDateMgr.getSysTime());
				input.put("%107!", orderContractInfo.getString("CTRM_PRODUCT_ID"));
				input.put("PARAM_CODE1", orderContractInfo.getString("PACKAGE_ID"));
				input.put("EPARCHY_CODE", input.getString(Route.ROUTE_EPARCHY_CODE));
				QueryInfoUtil.sendSMS(input);//发送短信
				
				if (!"".equals(orderContractInfo.getString("NEW_IMEI",""))) {
					//把用户营销活动绑定的IMEI终止
					IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
					param.put("USER_ID", userInfo.getString("USER_ID"));
					param.put("IMEI", orderContractInfo.getString("NEW_IMEI"));
					QueryInfoUtil.updUserImei(param, userInfo.getString("EPARCHY_CODE"));
				}
			}
		}
		
		input.put("UPDATE_TIME", SysDateMgr.getSysTime());
		input.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
		input.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
		Dao.insert("TF_B_CTRM_REFUND", input,Route.CONN_CRM_CEN);
		
		Dao.insert("TF_B_CTRM_REFUND_SUB", input,Route.CONN_CRM_CEN);
		
	}
	
	private String getMessage(Exception e) {
    	Throwable t = Utility.getBottomException(e);
    	String s = "";
    	if(t != null){
    		s = t.getMessage();
    	}

    	if(StringUtils.isNotBlank(s)){
    		if(s.length() > 120){
    			s = s.substring(0, 120);
    		}
    	}
    	return s;
    }
	
}
