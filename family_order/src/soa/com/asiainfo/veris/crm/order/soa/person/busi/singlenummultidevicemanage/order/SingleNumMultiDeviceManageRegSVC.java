
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.requestdata.SingleNumMultiDeviceManageRequestData;

public class SingleNumMultiDeviceManageRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
    	if (this.input.getString("ORDER_TYPE_CODE", "").equals(""))
        {
        	input.put("ORDER_TYPE_CODE", "396");
        }
    	
        return this.input.getString("ORDER_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
    	if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
	    	input.put("TRADE_TYPE_CODE", "396");
        }
    	
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        orderData.setSubscribeType("300");
    }
    
    
    @Override
    public void otherTradeDeal(IData idata, BusiTradeData btd) throws Exception {
		SingleNumMultiDeviceManageRequestData singleNumMultiDevicRD = (SingleNumMultiDeviceManageRequestData) btd.getRD();

		//01 新增的时候才需要开户
		if ("01".equals(singleNumMultiDevicRD.getOperCode())) {
			IData createPersonUserData = new DataMap();

			createPersonUserData.putAll(idata);
			createPersonUserData.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER_B"));
			createPersonUserData.put("SIM_CARD_NO", idata.getString("SIM_CARD_NO_B"));
			createPersonUserData.put("USER_TYPE_CODE", "0");

			createPersonUserData.put("PAY_NAME", idata.getString("CUST_NAME").trim());
			createPersonUserData.put("PAY_MODE_CODE", "0");
			//主号码账户ID
//        	createPersonUserData.put("PRI_ACCT_ID", singleNumMultiDevicRD.getUca().getAcctId());

			createPersonUserData.put("TRADE_TYPE_CODE", "10");
			createPersonUserData.put("ORDER_TYPE_CODE", "10");

			IDataset selectedelements = new DatasetList();

			//一号多终端副号产品ID
			String productId = "20170998";

			createPersonUserData.put("PRODUCT_ID", productId);

			// 必选或者默认的元素
			IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, "1", "1");
			forceElements = ProductUtils.offerToElement(forceElements, productId);

			if (IDataUtil.isNotEmpty(forceElements)) {
				String sysDate = SysDateMgr.getSysTime();
				for (int i = 0; i < forceElements.size(); i++) {
					IData element = new DataMap();
					element.put("ELEMENT_ID", forceElements.getData(i).getString("ELEMENT_ID"));
					element.put("ELEMENT_TYPE_CODE", forceElements.getData(i).getString("ELEMENT_TYPE_CODE"));
					element.put("PRODUCT_ID", createPersonUserData.getString("PRODUCT_ID"));
					element.put("PACKAGE_ID", forceElements.getData(i).getString("PACKAGE_ID"));
					element.put("MODIFY_TAG", "0");
					element.put("START_DATE", sysDate);
					element.put("END_DATE", "2050-12-31");
					selectedelements.add(element);
				}
			} else {
				CSAppException.appError("-1", "一号多终端产品[20170998]没有配置,请联系管理员!");
			}

			createPersonUserData.put("SELECTED_ELEMENTS", selectedelements.toString());

			//认证方式
			String checkMode = btd.getRD().getCheckMode();
			createPersonUserData.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z");
			
	        createPersonUserData.put("FLAG_4G", "1");

			IDataset result = CSAppCall.call("SS.CreatePersonUserRegSVC.tradeReg", createPersonUserData);
			String userId = result.getData(0).getString("USER_ID");

			List<RelationTradeData> relationTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);

			if (null != relationTradeDatas) {
				relationTradeDatas.get(0).setUserIdB(userId);
			}

			//修改副号码的付费关系
			List<BusiTradeData> btds = DataBusManager.getDataBus().getBtds();

			for (BusiTradeData btdTemp : btds) {
				String tradeTypeCode = btdTemp.getTradeTypeCode();

				if ("10".equals(tradeTypeCode)) {
					List<PayRelationTradeData> payRelationDatas = btdTemp.getTradeDatas(TradeTableEnum.TRADE_PAYRELATION);

					if (null != relationTradeDatas) {
						payRelationDatas.get(0).setAcctId(singleNumMultiDevicRD.getUca().getAcctId());
					}
				}
			}
			//关于做好一号双终端业务相关问题优化改造的通知 第一个改造点：增加PriIMSI和AuxIMSI两个必填字段
			if (result != null && result.size() > 0) {
				String createUserTradeId = result.first().getString("TRADE_ID");
				//TODO 同一笔订单下多笔工单的情况，所有的拼串数据全部放在对应的数据总线中，所以只能在数据总线中获取其他的工单的字表信息
		        if (btds.size() > 1){
		            // 如果工单大于1则往TF_B_ORDER_SUB插条记录
		            for (BusiTradeData btdOpen : btds){
		            	if(createUserTradeId.equals(btdOpen.getTradeId())){    //代表开户工单
		            		String priIMSI = "";
		    				String auxIMSI = "";
		    				//查trade_res台账，获取副号码的imsi
		    				List<ResTradeData> tradeResList = btdOpen.getTradeDatas(TradeTableEnum.TRADE_RES);//RES_TYPE_CODE="1"为sim卡
		    				if (tradeResList != null && tradeResList.size() > 0) {
		    					for(ResTradeData resTradeData : tradeResList){
		    						if("1".equals(resTradeData.getResTypeCode())){
		    							auxIMSI = resTradeData.getImsi();
		    							break;
		    						}
		    					}
		    				}
		    				//主号的根据号码查找user_res查
		    				String serialNumber = btd.getRD().getUca().getSerialNumber();//主号码
		    				IDataset userResList = UserResInfoQry.getUserResBySelbySerialnremove(serialNumber,"1");//RES_TYPE_CODE="1"为sim卡
		    				if (userResList != null && userResList.size() > 0) {
		    					 priIMSI = userResList.getData(0).getString("IMSI");
		    				}
		    				btd.getMainTradeData().setRsrvStr1(priIMSI);//主号的imsi
		    				btd.getMainTradeData().setRsrvStr2(auxIMSI);//副号的imsi
		    				break;
		            	}
		            }
		        }
			}
		}
		//02 删除需要调用销户
		else if ("02".equals(singleNumMultiDevicRD.getOperCode())) {
			//主号欠费销户、欠费预销户不需要调用
			if ("7240".equals(btd.getRD().getOrderTypeCode()) || "7230".equals(btd.getRD().getOrderTypeCode())) {
				return;
			}
			//EXEC_TYPE为BACK的情况下代表有未完工的10工单，不调用销户流程  add by tanjl
			if ("BACK".equals(input.getString("EXEC_TYPE", ""))) {
				return;
			}

			// 调用手机销户登记流程
			IData data = new DataMap();
			data.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER_B"));
			data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
			data.put("ORDER_TYPE_CODE", "192");
			data.put("TRADE_TYPE_CODE", "192");
			CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", data);
		}
	}
}
