
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * 配置在GoodsSynTradeAction之前执行
 * 
 * @author Mr.Z
 */
public class NetStoreTradeAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
        SaleActiveReqData saleActiveReq = (SaleActiveReqData) btd.getRD();
        
        if (saleActiveReq.isNetStoreActive())
        {
        	List<SaleGoodsTradeData> saleGoodsTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);

            for (SaleGoodsTradeData saleGoodsTradeData : saleGoodsTradeList)
            {
                saleGoodsTradeData.setResTag("0");
                saleGoodsTradeData.setResCode("0");
            }

            UcaData uca = saleActiveReq.getUca();

            OtherTradeData other = new OtherTradeData();
            other.setUserId(uca.getUserId());
            other.setRsrvValueCode("EMALL");
            other.setInstId(SeqMgr.getInstId());
            other.setStartDate(btd.getRD().getAcceptTime());
            other.setEndDate(SysDateMgr.getTheLastTime());
            other.setRsrvStr1(saleActiveReq.getProductId());
            other.setRsrvStr2(saleActiveReq.getPackageId());
            other.setRsrvStr3(saleActiveReq.getDeviceModelCode());
            other.setRsrvStr4(saleActiveReq.getDeviceModel());
            other.setRsrvStr5(saleActiveReq.getProdOrderId());
            other.setStaffId(CSBizBean.getVisit().getStaffId());
            other.setDepartId(CSBizBean.getVisit().getDepartId());
            other.setModifyTag(BofConst.MODIFY_TAG_ADD);

            btd.add(uca.getSerialNumber(), other);
        }

        if (StringUtils.isNotBlank(saleActiveReq.getNetOrderId()))
        {
        	 /*
      		  * 查询订购的产品是否是购机类产品
      		  */
      		 IDataset productLabel = LabelInfoQry.queryProductLabelId(saleActiveReq.getProductId());
      		 if(IDataUtil.isNotEmpty(productLabel)){
      			 String labelId=productLabel.getData(0).getString("UP_CATALOG_ID","");
      			 
      			 //确认是否为购机类营销活动
      			 if(labelId.equals("YX03")||labelId.equals("YX08")||labelId.equals("YX09")
      					 ||labelId.equals("YX07")){
      				 
      				 
      				String autoFlag="0";	//网厅审核标识
      	        	IData requestData=btd.getRD().getPageRequestData();
      	        	if(IDataUtil.isNotEmpty(requestData)){
      	        		autoFlag=requestData.getString("AUTO_FLAG","");
      	        		if(autoFlag==null){
      	        			autoFlag="0";
      	        		}
      	        	}
      	        	
      		       	 /*
      		       	  * 只有是非货到付款，并且产品类型为购机类的才会更改预约订单的状态
      		       	  */
      		       	 String payMoneyCode=saleActiveReq.getPayMoneyCode();
      		       	 if(payMoneyCode==null){
      		       		payMoneyCode="";
      		       	 }
      		       	 
      		       	 
      		       if(!(autoFlag.equals("1")&&payMoneyCode.equals(BofConst.PAY_MONEY_CODE_BY_HDFK))){
      		    	 IData updateData = new DataMap();
						
			          updateData.put("USE_PRODUCT_ID", saleActiveReq.getProductId());
			          updateData.put("USE_PACKAGE_ID", saleActiveReq.getPackageId());
			          updateData.put("RSRV_STR7",      "");
			          updateData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			          updateData.put("TERMINAL_ID", saleActiveReq.getSaleGoodsImei());
			          updateData.put("USER_ID", saleActiveReq.getUca().getUserId());
			          updateData.put("NET_ORDER_ID", saleActiveReq.getNetOrderId());
			          updateData.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());
			          updateData.put("RSRV_STR2", "1");
			          updateData.put("TRADE_ID", saleActiveReq.getTradeId());//add by sunxin
			          
			          Dao.executeUpdateByCodeCode("TF_F_TERMINALORDER", "UPDATE_BY_SN_STR2", updateData);
      		    	   
      		       }
      				 
      			 }
      		 }
       	
       }else{ //如果有预约记录受理后没有用，也要更新记录
    	   	 
    	     /*
    	      * 首先判断是否是购机类影响活动
    	      */
           IDataset productLabel = LabelInfoQry.queryProductLabelId(saleActiveReq.getProductId());
           if(IDataUtil.isNotEmpty(productLabel)){
               String labelId=productLabel.getData(0).getString("UP_CATALOG_ID","");
	 			 
	 			 //确认是否为购机类营销活动
	 			 if(labelId.equals("YX03")||labelId.equals("YX08")||labelId.equals("YX09")
	 					 ||labelId.equals("YX07")){
	 				
	 				//并且存在预处理工单
	 				IDataset saleBooks = TerminalOrderInfoQry.qryTerminalOrderInfo(btd.getMainTradeData().getSerialNumber());
	 				if (IDataUtil.isNotEmpty(saleBooks)&&saleBooks.size()>0){
	 					
	 					String autoFlag="0";	//网厅审核标识
	 			       	IData requestData=btd.getRD().getPageRequestData();
	 			       	if(IDataUtil.isNotEmpty(requestData)){
	 			       		autoFlag=requestData.getString("AUTO_FLAG","");
	 			       		if(autoFlag==null){
	 			       			autoFlag="0";
	 			       		}
	 			       	}
	 		       	
	 			       	 /*
	 			       	  * 只有是非货到付款，并且产品类型为购机类的才会更改预约订单的状态
	 			       	  */
	 			       	 String payMoneyCode=saleActiveReq.getPayMoneyCode();
	 			       	 if(payMoneyCode==null){
	 			       		payMoneyCode="";
	 			       	 }
	 			       	 
	 			       	if(!(autoFlag.equals("1")&&payMoneyCode.equals(BofConst.PAY_MONEY_CODE_BY_HDFK))){
	 			       	   IData updateData = new DataMap();
	       				
		                   updateData.put("USE_PRODUCT_ID", saleActiveReq.getProductId());
		                   updateData.put("USE_PACKAGE_ID", saleActiveReq.getPackageId());
		                   updateData.put("RSRV_STR7",      "没有用预约办理");
		                   updateData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		                   updateData.put("TERMINAL_ID", saleActiveReq.getSaleGoodsImei());
		                   updateData.put("USER_ID", saleActiveReq.getUca().getUserId());
		                   updateData.put("NET_ORDER_ID", saleBooks.getData(0).getString("ORDER_ID"));
		                   updateData.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());
		                   updateData.put("RSRV_STR2", "1");
		                   updateData.put("TRADE_ID", saleActiveReq.getTradeId());
		
		                   Dao.executeUpdateByCodeCode("TF_F_TERMINALORDER", "UPDATE_BY_SN_STR2", updateData);
	 			       	}
	 				}
	 				 
	 			 }
	 			 
	 		 }
       		
       }
        
    }

}
