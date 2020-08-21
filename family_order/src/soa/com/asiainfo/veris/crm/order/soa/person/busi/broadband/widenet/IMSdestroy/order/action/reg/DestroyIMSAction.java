package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.IMSdestroy.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;


/**
 * 
 * 受理以下业务时  同时进行 IMS固话拆机
 * 宽带拆机 605，
 * 宽带预约拆机 635，
 * 宽带移机 606，
 * 手机号码欠费停机  7240
 * @author  zhengkai5 
 * */

public class DestroyIMSAction implements ITradeAction
{

	private static final Logger logger = Logger.getLogger(DestroyIMSAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		
		/*
		 * 手机号码欠费停机为自动化任务，不需要权限判断
		 * */
		if(!"7240".equals(btd.getRD().getOrderTypeCode()))
		{
			String staffId = CSBizBean.getVisit().getStaffId();
			
			boolean staffPriv = StaffPrivUtil.isPriv(staffId, "crm9D97",StaffPrivUtil.PRIV_TYPE_FUNCTION);  //IMS固话拆机功能权限
			if(!staffPriv)
			{	//BUG20190102090436  IMS固话拆机问题优化   by xuzh5 2019-1-3 11:09:40
				CSAppException.appError("9997", "没有操作IMS固话拆机权限，权限code为【crm9D97】 ，请申请权限后再试！");
			}
		}
		
		IData ims = new DataMap();
		String serialNumber=btd.getRD().getUca().getSerialNumber();
		String orderId= btd.getRD().getOrderId();
		
		String kdSerialNumber = "";
		if(serialNumber.startsWith("KD_"))
		{
			kdSerialNumber = serialNumber;
			serialNumber=serialNumber.replaceAll("KD_", "");
		}else {
			kdSerialNumber = "KD_"+serialNumber;
		}
		
		ims.put("SERIAL_NUMBER", serialNumber);
        ims.put(Route.ROUTE_EPARCHY_CODE, "0898");
        ims.put("EPARCHY_CODE", "0898");
        ims.put("ORDER_ID", orderId);
        ims.put("ORDER_TYPE_CODE", btd.getRD().getOrderTypeCode());
        
        IDataset ImsInfos = null;
        try {
			ImsInfos = CSAppCall.call("SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", ims);
        } catch (Exception e) {
			logger.error("no  user! "+e.toString());
		}
			
		if(IDataUtil.isNotEmpty(ImsInfos))
		{
			IData ImsInfo = ImsInfos.first();
			String userId = ImsInfo.getString("USER_ID_B");
			ims.put("USER_ID", userId);
			IData userProductInfo = CSAppCall.callOne("SS.GetUser360ViewSVC.qryUserProductInfoByUserId",ims);
			if (IDataUtil.isNotEmpty(userProductInfo)) {
				String brandCode = userProductInfo.getString("BRAND_CODE");
				String productName = userProductInfo.getString("RSRV_STR5");
				ims.put("IMS_BRAND", brandCode);
				ims.put("IMS_PRODUCT", productName);
				ims.put("IMS_SERIAL_NUMBER", serialNumber);
			}
			
			ims.put("SERIAL_NUMBER", ImsInfo.getString("SERIAL_NUMBER_B"));
			ims.put("WIDE_SERIAL_NUMBER", kdSerialNumber);
			IDataset dataset = CSAppCall.call("SS.IMSDestroyRegSVC.tradeReg", ims);
			
			/* 所有宽带拆机工单中添加IMS固话信息
			 * 605:  立即拆机
			 * 1605：预约拆机
			 * 615：  特殊拆机
			 * 635:  校园宽带销户
			 * */
			if("605".equals(btd.getRD().getOrderTypeCode())||"615".equals(btd.getRD().getOrderTypeCode())){
				 //修改主台账表中预留字段
	            List<MainTradeData> MainTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
	           
	            if (null != MainTradeDatas)
	            {
	            	MainTradeDatas.get(0).setRsrvStr4("1"); 
	            	MainTradeDatas.get(0).setRsrvStr5(ims.getString("SERIAL_NUMBER","")); 
	            }
			}
			logger.error("DestroyIMS succeess!"+dataset.toString());
		}
	}
}
