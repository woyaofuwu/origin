package com.asiainfo.veris.crm.order.soa.person.busi.heeduforschools.order.action;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TopSetBoxOccupyAction.java
 * @Description: 机顶盒占用或换机或退还
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-5 下午9:31:30 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-5 yxd v1.0.0 修改原因
 */
public class HeEduForSchoolAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
    	String actionType = mainTrade.getString("RSRV_STR1"); // 0:购买 1:换机  2:退还 3:丢失
    	if("0".equals(actionType) || "1".equals(actionType)){
    		//add by zhangxing3 for REQ201812180020家宽终端库存管理需求
    		String switchTag = BizEnv.getEnvString("lc.terminal.switch");//浪潮开关  1开，0关
    		if("1".equals(switchTag))
    		{
    			if("0".equals(actionType)){
    				IData returnParam=new DataMap();
    	            IDataset topSetBoxDataset = TradeResInfoQry.qryTradeResInfosByType(mainTrade.getString("TRADE_ID"), "4", null);
    	            if (IDataUtil.isNotEmpty(topSetBoxDataset))
    	            {
						IData topSetBoxData = topSetBoxDataset.getData(0);
						returnParam.put("sn", topSetBoxData.getString("IMSI",""));//终端串码
						returnParam.put("broadband", mainTrade.getString("SERIAL_NUMBER", ""));//宽带账号
						String cityid = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", 
				        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"PDATA_ID", 
				        		new java.lang.String[]{"LC_CITY_ID", mainTrade.getString("CITY_CODE", "")});
						returnParam.put("userid", mainTrade.getString("USER_ID",""));//用户id
						returnParam.put("username", mainTrade.getString("CUST_NAME",""));//用户名称
						returnParam.put("cityid", cityid);//市县编码，必填，参考附录3.1 市县编码
						returnParam.put("terminal_type", "3");//终端类型:1-光猫,3-魔百盒,5-LAN
						returnParam.put("opersource", "CRM");//来源系统，参见3.5来源系统 pboss或crm    					
						returnParam.put("operid", CSBizBean.getVisit().getStaffId());//操作员工号 装维人员或营业厅人员
						returnParam.put("opername", CSBizBean.getVisit().getStaffName());//操作员名称
						returnParam.put("operorg", CSBizBean.getVisit().getDepartId());//操作员组织id，营业厅需对应营业厅id
						returnParam.put("operorgname", CSBizBean.getVisit().getDepartName());//操作员名称，营业厅对应营业厅名称
						returnParam.put("crm_order", mainTrade.getString("TRADE_ID", ""));//crm工单号    					
						returnParam.put("order_no", "");//开通系统工单号
					

    	            }
    			}
    			if("1".equals(actionType)){
	    			IData returnParam=new DataMap();				
					returnParam.put("terminal_type", "3");//终端类型:1-光猫,3-魔百盒,5-LAN
					returnParam.put("broadband", mainTrade.getString("SERIAL_NUMBER"));//宽带账号
					returnParam.put("oldsn", mainTrade.getString("RSRV_STR8", ""));//旧终端串码
					returnParam.put("newsn", mainTrade.getString("RSRV_STR6", ""));//新终端串码
					returnParam.put("operid", CSBizBean.getVisit().getStaffId());//装维人员工号
					returnParam.put("model", "");//终端型号
	

    			}
    		}
    		else
    		{
	    		IDataset returnResult = HwTerminalCall.saleOrChange4SetTopBox(mainTrade);
	    		if(IDataUtil.isEmpty(returnResult)){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
				}else{
					String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
					if(!resultCode.equals("0")){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
								getData(0).getString("X_RESULTINFO",""));
					}
				}
    		}
    	}else if("2".equals(actionType)){
    		//add by zhangxing3 for REQ201812180020家宽终端库存管理需求
    		String switchTag = BizEnv.getEnvString("lc.terminal.switch");//浪潮开关  1开，0关
    		if("1".equals(switchTag))
    		{
    			IData returnParam=new DataMap();
    			IDataset topSetBoxDataset = TradeResInfoQry.qryTradeResInfosByType(mainTrade.getString("TRADE_ID"), "4", null);
	            if (IDataUtil.isNotEmpty(topSetBoxDataset))
	            {
					IData boxInfo = topSetBoxDataset.getData(0);
	            	returnParam.put("sn", boxInfo.getString("IMSI"));//终端串码
					returnParam.put("broadband", mainTrade.getString("SERIAL_NUMBER"));//宽带账号
					String cityid = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", 
			        		new java.lang.String[]{"TYPE_ID", "DATA_ID"},"PDATA_ID", 
			        		new java.lang.String[]{"LC_CITY_ID", CSBizBean.getVisit().getCityCode()});
					returnParam.put("cityid", cityid);//市县编码，必填，参考附录3.1 市县编码
					returnParam.put("terminal_type", "3");//终端类型:1-光猫,3-魔百盒,5-LAN
					returnParam.put("opersource", "CRM");//来源系统，参见3.5来源系统 pboss或crm
					returnParam.put("operid", CSBizBean.getVisit().getStaffId());//操作员工号 装维人员或营业厅人员
					returnParam.put("opername", CSBizBean.getVisit().getStaffName());//操作员名称
					returnParam.put("operorg", CSBizBean.getVisit().getDepartId());//操作员组织id，营业厅需对应营业厅id
					returnParam.put("operorgname", CSBizBean.getVisit().getDepartName());//操作员名称，营业厅对应营业厅名称
					returnParam.put("crm_order", mainTrade.getString("TRADE_ID"));//crm工单号
					returnParam.put("recover_type", "拆机");//回退类型 拆机、撤单，参考3.7终端回退类型
					returnParam.put("userid", mainTrade.getString("USER_ID"));//用户id
					returnParam.put("username", mainTrade.getString("CUST_NAME"));//用户名称
					returnParam.put("order_no", "");//开通系统工单号
					returnParam.put("dell_type", "");//拆机原因 故障、离网，参考3.8终端回退类型
				

	            }
    		}
    		else
    		{
	    		IData returnParam = new DataMap();
	    		String trade_id = mainTrade.getString("TRADE_ID");
	    		IDataset boxInfos = TradeResInfoQry.queryAllTradeResByTradeId(trade_id);
	    		if(DataSetUtils.isNotBlank(boxInfos)){
	    			IData boxInfo = boxInfos.first();
	    			String serialNumber = mainTrade.getString("SERIAL_NUMBER");
	    			returnParam.put("RES_NO", boxInfo.getString("IMSI"));
					returnParam.put("PARA_VALUE1", serialNumber);
					returnParam.put("SALE_FEE", boxInfo.getString("RSRV_NUM5",""));
					returnParam.put("PARA_VALUE7", "0");
					returnParam.put("DEVICE_COST", boxInfo.getString("RSRV_NUM4","0"));
					returnParam.put("X_CHOICE_TAG", "1");
					returnParam.put("RES_TYPE_CODE", "4");
					returnParam.put("PARA_VALUE11", boxInfo.getString("UPDATE_TIME"));
					returnParam.put("PARA_VALUE14", boxInfo.getString("RSRV_NUM5","0"));
					returnParam.put("PARA_VALUE17", boxInfo.getString("RSRV_NUM5","0"));
					returnParam.put("PARA_VALUE1", serialNumber);
					returnParam.put("USER_NAME", mainTrade.getString("CUST_NAME"));
					returnParam.put("STAFF_ID", boxInfo.getString("UPDATE_STAFF_ID"));
					returnParam.put("TRADE_ID", boxInfo.getString("INST_ID"));
	    			IDataset returnResult=HwTerminalCall.returnTopSetBoxTerminal(returnParam);
					if(IDataUtil.isEmpty(returnResult)){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
					}else{
						String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
						if(!resultCode.equals("0")){
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
									getData(0).getString("X_RESULTINFO",""));
						}
					}
	    		}
    		}
    	}
    }
}
