
package com.asiainfo.veris.crm.order.soa.person.busi.electricBusiness;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryInfoUtil;

public class ShoppingOrderBean extends CSBizBean
{
    private static transient final Logger logger = Logger.getLogger(ShoppingOrderBean.class);

    /**
     * 提交反馈信息
     */
    public IDataset submitFeedBack(IData data) throws Exception
    {

        String[] allIds = data.getString("comminfo_PIDINFO", "").split(";");

        // String partitionFlag = pageParam.getString("chkPartition","0");
        String splitFlag = data.get("IS_SPLIT") == null ? "0" : "1";
        IData param = new DataMap();
        param.put("KIND_ID", "BIP3B508_T3000511_0_0");
        param.put("OPR_NUMB", SysDateMgr.getSysTime());
        param.put("TID", allIds[0].split(",")[0]);
        param.put("IS_SPLIT", splitFlag);
        param.put("RSP_TIME", SysDateMgr.getSysTime());
        param.put("SHIPPING_COMPANY", data.getString("SHIPPING_COMPANY", ""));
        param.put("SHIPPING_COMPANY_NAME", StaticUtil.getStaticValue("COMPANY_ID", data.getString("SHIPPING_COMPANY", "")));
        param.put("SHIPPING_ID", data.getString("SHIPPING_ID", ""));
        param.put("SHIPPING_MEMO", data.getString("REMARK", ""));

        IDataset paramList = new DatasetList();
        IDataset oidList = new DatasetList();

    	//查询子订单数据
		IData daoParam = new DataMap();
		daoParam.put("TID",allIds[0].split(",")[0]);
		IDataset orderList = QueryInfoUtil.queryListByTid(daoParam);
		
		if ( orderList!= null && orderList.size() > 0) {
			
			for (int i = 0; i < orderList.size(); i++) {
				IData updateParam = new DataMap();
			    updateParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                updateParam.put("UPDATE_STAFF_ID", param.getString("TRADE_STAFF_ID"));
                updateParam.put("UPDATE_DEPART_ID", param.getString("TRADE_DEPART_ID"));
                
                IData orderInfo = orderList.getData(i);
				String newIMEI = "";
				boolean flag = true;
				
			    for (int j = 0; j < allIds.length; j++)
		        {
					String[] orderIds = allIds[j].split(",");
					if (orderInfo.getString("OID").equals(orderIds[1])) {
						newIMEI = orderIds[2];
						flag = false;
					}
		    	}
				if (flag) {
					continue;
				}
				
				if ("2".equals(orderInfo.getString("CTRM_PRODUCT_TYPE"))){
					IData param1 = new DataMap();
		            param1.put("TID", orderInfo.getString("TID"));
		            param1.put("IS_SPLIT", splitFlag);
		            param1.put("SHIPPING_COMPANY", data.getString("SHIPPING_COMPANY", ""));
		            param1.put("SHIPPING_COMPANYNAME", StaticUtil.getStaticValue("COMPANY_ID", data.getString("SHIPPING_COMPANY", "")));
		            param1.put("SHIPPING_ID", data.getString("SHIPPING_ID", ""));
		            param1.put("REMARK", data.getString("REMARK", ""));
		            param1.put("OID", orderInfo.getString("OID"));
		            param1.put("PID", orderInfo.getString("PID"));
		            param1.put("UPDATE_TIME", SysDateMgr.getSysTime());
		            param1.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		            param1.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		            
		            paramList.add(param1);
		            IData oidMap = new DataMap();
		            oidMap.put("OID", orderInfo.getString("OID"));
		            oidList.add(oidMap);
				}
	            
	        	String route = RouteInfoQry.getEparchyCodeBySnForCrm(orderInfo.getString("PHONE"));
	
	            //此产品为不是号卡类的合约且状态为0，则执行该合约产品	
		    	if ("2".equals(orderInfo.getString("CTRM_PRODUCT_TYPE")) && "0".equals(orderInfo.getString("STATUS")) 
		    			&& !"3".equals(orderInfo.getString("RSRV_STR2"))) {
		    		// 为合约订单且有IMEI，则执行合约计划
	                IData retnData = new DataMap();
	                // 调合约计划的接口
	                IData inData = new DataMap();
	                inData.put("SERIAL_NUMBER",orderInfo.getString("PHONE"));
					inData.put("PRODUCT_ID", orderInfo.getString("PRODUCT_ID"));
					inData.put("PACKAGE_ID", orderInfo.getString("PACKAGE_ID"));
					
//	                if (!"".equals(newIMEI))
//	                {
//	                    inData.put("TERMINAL_ID", newIMEI);
//	                    inData.put("ACTION_TYPE", "2");
//	                }
//	                else
//	                {
	                    inData.put("ACTION_TYPE", "0");
//	                }
	                inData.put("NO_TRADE_LIMIT", "TRUE");
	                inData.put("IN_MODE_CODE", "6");
	                inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	                inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	                inData.put("TRADE_EPARCHY_CODE", route);
	                inData.put(Route.ROUTE_EPARCHY_CODE, route);
	                inData.put("KIND_ID", "BIP3B507_T3000510_0_0");
	
	                updateParam.put("STATUS", "1");
	                try
	                {
	                    IDataset retn = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", inData);
	                    retnData = (retn != null && retn.size() > 0 )?retn.getData(0):new DataMap();
	
	                    if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) 
								&& !"-1".equals(retnData.getString("ORDER_ID")))
	                    {
	                        updateParam.put("STATUS", "1");
	                        updateParam.put("ACCEPT_RESULT", "1");
	                        
	                    	inData.put("PARAM_CODE","ABILITY_CONTRACT");
	                        inData.put("EPARCHY_CODE", route);
							inData.put("%101!",data.getString("SHIPPING_ID", "")); //业务订购渠道
							inData.put("%103!",orderInfo.getString("PRODUCT_ID"));  //本地合约编码
							inData.put("%107!",orderInfo.getString("CTRM_PRODUCT_ID")); //集团的合约编码
							inData.put("%105!",orderInfo.getString("CREATE_TIME")); //订购时间
						    inData.put("PARA_CODE1",orderInfo.getString("PACKAGE_ID"));
						    inData.put("TRADE_ID",retnData.getString("TRADE_ID"));
							QueryInfoUtil.sendSMS(inData);
	                    }
	                    else
	                    {
	                        updateParam.put("STATUS", "2");
	                        updateParam.put("ACCEPT_RESULT", "2");
	                    }
	                }
	                catch (Exception e)
	                {
	                    updateParam.put("STATUS", "2");
	                    retnData.put("X_RESULTINFO",e.getMessage());
	                }
	                String msg = retnData.getString("X_RESULTINFO", "");
	                msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
	
	                updateParam.put("TRADE_ID", retnData.getString("TRADE_ID", "-1"));
	                updateParam.put("PID", orderInfo.getString("PID"));
	                updateParam.put("REMARK", msg);
	                updateParam.put("ACCEPT_DATE", SysDateMgr.getSysTime());
	                updateInfo(updateParam);
	                
	              //保存界面输入的IMEI到order_product表
			    	if (newIMEI != null && !"".equals(newIMEI)) {
			    		//----暂时这样
			    		param.clear();
						IData userinfo = UcaInfoQry.qryUserInfoBySn(orderInfo.getString("PHONE"),route);
						//把用户旧的IMEI终止
						param.put("USER_ID", userinfo.getString("USER_ID"));
						param.put("END_DATE", SysDateMgr.getSysTime());
						QueryInfoUtil.updateUserIMEI(param,route);
						
						//插入新的IMEI
						param.clear();
						param.put("USER_ID", userinfo.getString("USER_ID"));
						param.put("SERIAL_NUMBER", orderInfo.getString("PHONE"));
						param.put("IMEI", newIMEI);
						param.put("START_DATE", SysDateMgr.getSysTime());
						param.put("END_DATE", SysDateMgr.getTheLastTime());
						QueryInfoUtil.insertUserIMEI(param,route);
			    		//----购机的合约还是要配置购机才行
			    		updateParam.put("ORDER_STATUS", orderInfo.getString("STATUS"));
			    		updateParam.put("OID", orderInfo.getString("OID"));
						updateParam.put("TID", orderInfo.getString("TID"));
						updateParam.put("RSRV_STR1", newIMEI);       //RSRV_STR1保存最新的IMEI号  
						QueryInfoUtil.updateOrderInfo(updateParam);
					}
	            }
	        }
		}
		
        IDataset disList = DataHelper.distinct(oidList, "OID", "");
        param.putAll(disList.toData());
        param.remove("X_RECORDNUM");
        // 调接口反馈
        if (!disList.isEmpty()) {
        	 IBossCall.dealInvokeUrl("BIP3B508_T3000511_0_0", "IBOSS2", param);
		}
        saveDistitionInfo(paramList);

        IData returnMap = new DataMap();
        returnMap.put("0000", "配送反馈成功!");
        IDataset returnList = new DatasetList();
        returnList.add(returnMap);

        return returnList;
    }
    /**
     * 提交反馈信息
     */
    public void feedbackReturnOrder(IData data) throws Exception
    {
    	String[] allIds = data.getString("inforecvid").split(",");
		IDataset paramList = new DatasetList();
		
		for (int i = 0; i < allIds.length; i++) {
			String[] orderIds = allIds[i].split("_");
			IData info = new DataMap();
			info.put("REFUND_ID", orderIds[0]);
			info.put("TID", orderIds[1]);
			info.put("OID", orderIds[2]);
			paramList.add(info);
			
			IData qryProData = new DataMap();
			qryProData.put("OID", orderIds[2]);
			qryProData.put("TID", orderIds[1]);
			qryProData.put("CTRM_PRODUCT_TYPE", "2");
			IDataset orderInfos = CSAppCall.call("SS.ShoppingOrderSVC.queryProOrderByCtrmType", qryProData);
			
			if (orderIds != null && orderInfos.size() > 0) {
				for (int j = 0; j < orderInfos.size(); j++) {
					IData orderData = orderInfos.getData(j);
					String status = orderData.getString("STATUS");
					if (!"0".equals(status) && !"2".equals(status)) {
						//调返销流程
						IData input = new DataMap();
						input.put("TRADE_ID", orderData.getString("TRADE_ID"));
						input.put(Route.ROUTE_EPARCHY_CODE, getVisit().getLoginEparchyCode());
						CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", input);
						
					}else if ("0".equals(status)) {//如果合约没执行，则修改其状态
						IData inputData = new DataMap();
						inputData.put("PID", orderData.getString("PID"));
						inputData.put("TRADE_ID", "-1");
						inputData.put("ACCEPT_DATE", SysDateMgr.getSysDate());
						inputData.put("STATUS", "3");
						inputData.put("ACCEPT_RESULT", "1");
						inputData.put("UPDATE_TIME", SysDateMgr.getSysDate());
						inputData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
						inputData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
						CSAppCall.call("SS.ShoppingOrderSVC.updCtrmOrderProduct", inputData);
					}
					
					//发短信
					IData param = new DataMap();
					param.put("PARAM_CODE", "RETURN_ORDER");
					param.put("%101!", data.getString("CHANNEL_ID"));
					param.put("%102!", orderData.getString("RSRV_STR4"));
					param.put("PARA_CODE1",orderData.getString("PACKAGE_ID"));
					param.put("EPARCHY_CODE", getVisit().getLoginEparchyCode());
					param.put("TRADE_STAFF_ID", getVisit().getStaffId());
			        param.put("TRADE_DEPART_ID", getVisit().getDepartId());
					QueryInfoUtil.sendSMS(param);//发送短信
					
					if (!"".equals(orderData.getString("NEW_IMEI",""))) {
						//把用户营销活动绑定的IMEI终止
						IData userInfo = UcaInfoQry.qryUserInfoBySn(orderData.getString("PHONE"));
						param.clear();
						param.put("USER_ID", userInfo.getString("USER_ID"));
						param.put("IMEI", orderData.getString("NEW_IMEI"));
						QueryInfoUtil.updUserImei(param,getVisit().getLoginEparchyCode());
					}
				}
			}
			//调iBoss退款订单反馈接口
	    	IData ibossData = new  DataMap();
	    	ibossData.put("KIND_ID", "BIP3B511_T3000514_0_0");
	    	ibossData.put("OPR_NUMB", SysDateMgr.getSysDate());
	    	ibossData.put("CHANNEL_ID", data.getString("CHANNEL_ID"));
	    	ibossData.put("REFUND_TYPE", data.getString("REFUND_TYPE"));
	    	ibossData.put("REFUND_ID",  orderIds[0]);
	    	ibossData.put("TID", orderIds[1]);
	    	ibossData.put("OID", orderIds[2]);
	    	ibossData.put("RSP_TIME", SysDateMgr.getSysDate());
	    	ibossData.put("RSP_RESULT", data.getString("RSP_RESULT"));
	    	ibossData.put("REFUND_FEE", data.getString("REFUND_FEE"));
	    	ibossData.put("MEMO", data.getString("REMARK"));
	    	CSAppCall.call("SS.ShoppingOrderSVC.returnOrder2IBoss", ibossData);
	    	//更新本地订单表状态
	    	CSAppCall.call("SS.ShoppingOrderSVC.updCtrmRefundSubById", info);
		}
    }
    
    public void saveDistitionInfo(IDataset params) throws Exception
    {

        Dao.executeBatchByCodeCode("TF_B_CTRM_TLIST", "INS_CTRM_SHIPPING", params);
    }

    /**
     * 更新订单表中的状态
     * 
     * @param updateOrder
     * @throws Exception
     */
    public void updateInfo(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER_PRODUCT", param);

    }

    /** ************************add by ouyk start********************************************************* */
    /**
     * 查询销售订单
     * 
     * @param input
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryB2COrder(IData input, Pagination pagination) throws Exception
    {
        String beginTime = input.getString("cond_BEGIN_TIME");
        String endTime = input.getString("cond_END_TIME");
        if ("".equals(beginTime))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1048);
        }
        if ("".equals(endTime))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1049);
        }
        IData param = new DataMap();
        String serialNum = input.getString("cond_SERIAL_NUMBER");
        String tId = input.getString("cond_TID");
        if (!"".equals(serialNum))
        {
            param.put("SERIAL_NUMBER", serialNum);
        }
        if (!"".equals(tId))
        {
            param.put("TID", tId);
        }
        param.put("START_DATE", beginTime);
        param.put("END_DATE", endTime);
        return QueryInfoUtil.queryB2COrder(param, pagination);
    }

    /**
     * 查询销售子订单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySubOrderInfo(IData input) throws Exception
    {
        String TId = input.getString("TID");
        return QueryInfoUtil.querySubOrderByTId(TId);
    }

    /**
     * 查询子订单关联产品
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryOrderProductInfo(IData input) throws Exception
    {
        String OId = input.getString("OID");
        return QueryInfoUtil.queryProductInfoByOID(OId);
    }

    /**
     * 查询退款订单
     * 
     * @param input
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryReturnOrderInfo(IData input, Pagination pagination) throws Exception
    {
        String beginTime = IDataUtil.chkParam(input, "BEGIN_TIME");
        String endTime = IDataUtil.chkParam(input, "END_TIME");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String tradeId = input.getString("TID");

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TID", tradeId);
        param.put("START_DATE", beginTime);
        param.put("END_DATE", endTime);
        return QueryInfoUtil.queryReturnOrder(param, pagination);
    }

    /**
     * 查询退款子订单
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryReturnSubOrder(IData input) throws Exception
    {
        String refundId = IDataUtil.chkParam(input, "REFUND_ID");
        return QueryInfoUtil.queryReturnSubOrderByRefundId(refundId);
    }

    public IDataset queryProOrderByCtrmType(IData input) throws Exception
    {
        return QueryInfoUtil.queryProOrderByCtrmType(input.getString("OID"), input.getString("TID"), input.getString("CTRM_PRODUCT_TYPE"));
    }

    public IDataset updCtrmOrderProduct(IData input) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER_PRODUCT", input);
        return null;
    }

    public IDataset updCtrmRefundSubById(IData inputs) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_REFUND_SUB_BYID", inputs);
        return null;
    }
    /**************************add by ouyk end**********************************************************/

    
}
