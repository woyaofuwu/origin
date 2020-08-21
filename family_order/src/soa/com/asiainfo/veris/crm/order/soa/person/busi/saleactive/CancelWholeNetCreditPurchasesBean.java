package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import java.math.BigDecimal;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeStaffInfoQry;

public class CancelWholeNetCreditPurchasesBean extends CSBizBean{
	public IDataset getCancelWholeNetCreditPurchasesInfo(IData input) throws Exception{
		String serialNumber = input.getString("cusMblNo");
		String procTyp=input.getString("procTyp");
		IData inparam = queryTradeInfo(input, serialNumber);
		IDataset CancelInfos=new DatasetList();
		if(procTyp.equals("1")){//撤单
			IData REG_REJ_RSP = new DataMap();
			String RspCode= "";
			
			String orderId = inparam.getString("orderId");
			String routeId = inparam.getString("routeId");
			String inFlag = inparam.getString("inFlag");
			IDataset tradeList = TradeInfoQry.getMainTradeByOrderId(orderId, "0", routeId);
			if(IDataUtil.isNotEmpty(tradeList)){
				if(inFlag.equals("0") || inFlag.equals("2")){
					RspCode="查询成功";
				}else if(inFlag.equals("1")){
					CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0, "订单已成功发送到和包平台，不能撤单");
				}else{
					CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0, "未查询到订单");
				}
				
			}else{
				CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0, "未查询到订单");
			}
			REG_REJ_RSP.put("RSP_CODE", RspCode);
			CancelInfos.add(REG_REJ_RSP);
			
		}else if(procTyp.equals("2")){//退货
			IDataset cancelInfos = IBossCall.getCancelWholeNetCreditPurchasesInfo(inparam);
			
			IData cancelInfo=cancelInfos.getData(0);
			
			IData REG_REJ_RSP=cancelInfo.getDataset("REG_REJ_RSP").first();
			String mpl_ord_dt=cancelInfo.getString("MPL_ORD_DT");

			String RspCode=REG_REJ_RSP.getString("RSP_CODE");
			if("0000".equals(RspCode)){
				RspCode="成功";
			}else{
				RspCode="失败";
			}
			REG_REJ_RSP.put("RSP_CODE", RspCode);
			REG_REJ_RSP.put("MPL_ORD_DT", mpl_ord_dt);//全网信用购机平台订单日期

			CancelInfos.add(REG_REJ_RSP);
		}
		
		return CancelInfos;
	}

	private IData queryTradeInfo(IData input, String serialNumber) throws Exception {
		IDataset tradeDatas = TradeInfoQry.getMainTradeBySN(serialNumber, "240");
		String finish = "";
		if(DataUtils.isEmpty(tradeDatas)){
			tradeDatas = TradeHistoryInfoQry.getInfosBySnTradeTypeCode("240", serialNumber, "") ; 
			if(DataUtils.isEmpty(tradeDatas)){
	        	CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0, "未查询到订单");
			}else{
				finish="1";
			}
		}else{
			finish="0";
		}
		IDataset otherDatas = new DatasetList();
		IDataset otherDatas2 = new DatasetList();
		String tradeId = "";
		String orderId = "";
		String routeId = "";
		long tradeIdL = 0;
		for (Object object : tradeDatas) {
			IData tradeData =(IData) object ;
			tradeId =  tradeData.getString("TRADE_ID");
			Long selTradeIdL =  tradeData.getLong("TRADE_ID");
			otherDatas2 = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(tradeId, "CREDIT_PURCHASES");
			if(DataUtils.isNotEmpty(otherDatas2)){
				if(selTradeIdL>tradeIdL){//取最近一笔信用购机购机
					otherDatas=otherDatas2;
					tradeIdL = selTradeIdL;
					
					orderId = tradeData.getString("ORDER_ID");
			        routeId = tradeData.getString("EPARCHY_CODE");
				}
				
			}
		}
		
		if(DataUtils.isNotEmpty(otherDatas)){//otherDatas 最近一笔信用购机
			String seq = otherDatas.getData(0).getString("RSRV_STR9");//比较seq
			String mplOrdNo = otherDatas.getData(0).getString("RSRV_STR1");
			String mplOrdDt = otherDatas.getData(0).getString("RSRV_STR2");
			String inFlag = otherDatas.getData(0).getString("RSRV_STR15");//BOSS主动下单信用购机接口标识 0：初始化， 1：接口调用成功  2：接口调用失败
			
			int tradeId1 = Integer.valueOf(tradeId.substring(tradeId.length()-6))+1;
	        String rejSeq = "898"+"BIP2B191"+ SysDateMgr.getSysDateYYYYMMDDHHMMSS()+tradeId1;

			IData  inparam = new DataMap();
	   	 	inparam.put("procTyp", input.getString("procTyp"));
	   	 	inparam.put("cusMblNo", serialNumber);
	   	 	inparam.put("ExSeq", seq);
	   	 	inparam.put("Seq", seq);
	   	 	inparam.put("REJ_SEQ", rejSeq);
	   	 	inparam.put("mplOrdNo", mplOrdNo);
	   	 	inparam.put("mplOrdDt", mplOrdDt);
	   	 	inparam.put("TRADE_ID", String.valueOf(tradeIdL));
	   	 	inparam.put("HAX_HIS", finish);
	   	 	inparam.put("inFlag", inFlag);
	   	 	inparam.put("orderId", orderId);
	   	 	inparam.put("routeId", routeId);
			return inparam;
		}
		return new DataMap();
	}
	
	public IDataset getCancelRequestInfo(IData input) throws Exception{
		String serialNumber = input.getString("cusMblNo");
		String  procTyp = input.getString("procTyp");
		IData inparam = queryTradeInfo(input, serialNumber);
		if(DataUtils.isEmpty(inparam)){
			return new DatasetList();
		}
		input.putAll(inparam);
   	 	
		IDataset CancelReqInfos=new DatasetList();
		if(!StringUtils.isEmpty(procTyp) ){
			
			if(procTyp.equals("1")){//撤单
				
				String inFlag = input.getString("inFlag"); //BOSS主动下单信用购机接口标识 0：初始化， 1：接口调用成功  2：接口调用失败
				
				if(!StringUtils.isEmpty(inFlag)  ){
					if(inFlag.equals("0") || inFlag.equals("2")){
						
						String orderId = input.getString("orderId");
						String routeId = input.getString("routeId");
						String tradeId = input.getString("TRADE_ID");
						IDataset tradeList = TradeInfoQry.getMainTradeByOrderId(orderId, "0", routeId);
						IData orderInfo=  TradeInfoQry.getOrderByOrderId(orderId);
						if(IDataUtil.isNotEmpty(tradeList)){
							for(int i=0; i<tradeList.size(); i++){
								IData tradeInfo = tradeList.getData(i);
								IData pubData = this.getPublicData(tradeId);
								createCancelTrade(tradeInfo,pubData);
								TradeInfoQry.moveBhTrade(tradeInfo);
							}
							if(IDataUtil.isNotEmpty(orderInfo)){
								TradeInfoQry.moveBhOrder(orderInfo);
							}
						}
			              
						releaseIMEI(tradeId,serialNumber);//释放终端
						
						IData REG_REJ_RSP=new DataMap();
						String RspCode= "成功";
						REG_REJ_RSP.put("RSP_CODE", RspCode);
						CancelReqInfos.add(REG_REJ_RSP);
					}
					
					
				}
				
			}else if(procTyp.equals("2")){//退货
				IDataset cancelReqInfos= IBossCall.getCancelRequestInfo(input);
				IData cancelReqInfo=cancelReqInfos.getData(0);
				IData REG_REJ_RSP=cancelReqInfo.getDataset("REG_REJ_RSP").first();
				String RspCode=REG_REJ_RSP.getString("RSP_CODE");
				if("0000".equals(RspCode)){
					RspCode="成功";
					IData param=new DataMap();
					param.put("RSRV_STR10", input.getString("procTyp"));
					param.put("RSRV_STR11", input.getString("REJ_SEQ"));
					param.put("TRADE_ID", input.getString("TRADE_ID"));
					param.put("RSRV_VALUE_CODE", "CREDIT_PURCHASES");
					Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER","UPD_RSRV_STR10_11_BY_TRADE_ID", param,Route.getJourDb(this.getTradeEparchyCode()));
					CancelTradeReg(input);
				}else{
					RspCode="失败";
				}
				REG_REJ_RSP.put("RSP_CODE", RspCode);
				CancelReqInfos.add(REG_REJ_RSP);
				return CancelReqInfos;
			}
		}
		
		return CancelReqInfos;
	}
	
	
	private void releaseIMEI(String  saletradeId,String usersn) throws Exception {
		IDataset ds = TradeSaleGoodsInfoQry.getTradeSaleGoodsByTradeId(saletradeId);
		if(IDataUtil.isNotEmpty(ds))
		{
			IData data = ds.getData(0);
			data.put("RES_NO", data.getString("RES_CODE",""));
    		data.put("SERIAL_NUMBER", usersn);
    		//释放终端预占
    		IDataset retDataset =HwTerminalCall.releaseResTempOccupy(data);
    		
		}
	}
	
	
	public void CancelTradeReg(IData PageData) throws Exception{
		String haxHis = PageData.getString("HAX_HIS");//是否已完工搬历史
		if("0".equals(haxHis)){
			unfinishTradeReg(PageData);
		}else{
			finishTradeReg(PageData);
		}
	}
	
	/**
	 * 已完工撤单,走返销流程
	 * @param PageData
	 * @throws Exception
	 */
	public void finishTradeReg(IData PageData) throws Exception{
		PageData.put("TRADE_ID", PageData.getString("TRADE_ID"));
		PageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
		PageData.put("IS_CHECKRULE", false);
		CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", PageData);
	}
	
	/**
	 * 未完工撤单.直接搬历史
	 * @param PageData
	 * @throws Exception
	 */
	 
	public void unfinishTradeReg(IData PageData) throws Exception{
		String tradeId=PageData.getString("TRADE_ID");
		IDataset hisTradeInfos = TradeInfoQry.queryByTradeIdCancelTag(tradeId, "0",  Route.getJourDb(BizRoute.getRouteId()));
		if (IDataUtil.isEmpty(hisTradeInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_70, tradeId);// 获取台帐历史表资料:没有该笔业务!%s
        }
		
		for (int i=0;i<hisTradeInfos.size();i++) {
			IData hisTrade=hisTradeInfos.getData(i);
			IData pubData = this.getPublicData(tradeId);// 操作员/trade_id/cancel_tag等相关信息
			String newOrderId=createCancelOrder(hisTrade,pubData);
			/*		IData newCancelTrade=createCancelTrade(hisTrade,PageData,pubData,newOrderId);
			if("1".equals(newCancelTrade.getString("FEE_STATE")) && StringUtils.isBlank(hisTrade.getString("FEE_TIME")))
	        {
	        	this.modifyHisTradeFeeTime(pubData);// 修改原台账状态
	        }else
	        {
	        	this.modifyHisTrade(pubData);// 修改原台账状态
	        }*/
			
    		createCancelTrade(hisTrade,pubData);
    		TradeInfoQry.moveBhTrade(hisTrade);
			createCancelStaffTrade(newOrderId, pubData);
		}
	}
	
	
	
	  public IData createCancelTrade(IData tradeData, IData pubData) throws Exception
	    {
	        IData newTradeData = new DataMap();
	        newTradeData.putAll(tradeData);

	        /********* 费用 *******************************/

	        long lOperFee = -tradeData.getLong("OPER_FEE", 0);
	        long lAdvancePay = -tradeData.getLong("ADVANCE_PAY", 0);
	        long lforegift = -tradeData.getLong("FOREGIFT", 0);
	        String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
	        newTradeData.put("CANCEL_TRADE_ID", tradeData.getString("TRADE_ID"));
	        
	        newTradeData.put("OPER_FEE", String.valueOf(lOperFee));
	        newTradeData.put("ADVANCE_PAY", String.valueOf(lAdvancePay));
	        newTradeData.put("FOREGIFT", String.valueOf(lforegift));
	        newTradeData.put("FEE_STATE", strFeeState);
	        newTradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
	        newTradeData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("STAFF_ID"));

	        newTradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
	        newTradeData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
	      
	        newTradeData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
	        newTradeData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
	        newTradeData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
	        newTradeData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
	        newTradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
	        newTradeData.put("CANCEL_TAG", "3");// 2=返销 3=取消
	        newTradeData.put("CANCEL_DATE", tradeData.getString("ACCEPT_DATE"));
	        newTradeData.put("CANCEL_STAFF_ID", tradeData.getString("TRADE_STAFF_ID"));
	        newTradeData.put("CANCEL_DEPART_ID", tradeData.getString("TRADE_DEPART_ID"));
	        newTradeData.put("CANCEL_CITY_CODE", tradeData.getString("TRADE_CITY_CODE"));
	        newTradeData.put("CANCEL_EPARCHY_CODE", tradeData.getString("TRADE_EPARCHY_CODE"));
	        newTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
	        newTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
	        newTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
	        newTradeData.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));


	        if (!Dao.insert("TF_BH_TRADE", newTradeData, Route.getJourDb(BizRoute.getRouteId())))
	        {
	            CSAppException.apperr(TradeException.CRM_TRADE_304, pubData.getString("TRADE_ID"));
	        }

	        return newTradeData;
	    }
	
	 /**
     * 获取一些公共信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    private IData getPublicData(String tradeId) throws Exception
    {
        IData pubData = new DataMap();
        pubData.put("CANCEL_TAG", "2");// 2=返销
        pubData.put("TRADE_ID", tradeId);
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("LOGIN_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return pubData;
    }
    
    /**
     * 生成新的返销订单
     * 
     * @throws Exception
     */
    public String createCancelOrder(IData hisTradeData, IData pubData) throws Exception
    {
        IData newOrder = new DataMap();

        String  newOrderId =  SeqMgr.getOrderId(); // 生成新的order_id
        
        newOrder.put("ORDER_ID", newOrderId);
        newOrder.put("ACCEPT_MONTH", newOrderId.substring(4, 6)); 
        
        newOrder.put("ORDER_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("TRADE_TYPE_CODE", hisTradeData.getString("TRADE_TYPE_CODE"));
        newOrder.put("PRIORITY", hisTradeData.getString("PRIORITY"));
        newOrder.put("ORDER_STATE", "0");
        newOrder.put("NEXT_DEAL_TAG", "0");
        newOrder.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        String custId = hisTradeData.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        newOrder.put("CUST_ID", custId);
        newOrder.put("CUST_NAME", hisTradeData.getString("CUST_NAME"));
        newOrder.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE", ""));
        newOrder.put("PSPT_ID", custInfo.getString("PSPT_ID", ""));
        newOrder.put("EPARCHY_CODE", hisTradeData.getString("EPARCHY_CODE"));
        newOrder.put("CITY_CODE", hisTradeData.getString("CITY_CODE"));

        newOrder.put("OPER_FEE", hisTradeData.getString("OPER_FEE"));
        newOrder.put("FOREGIFT", hisTradeData.getString("FOREGIFT"));
        newOrder.put("ADVANCE_PAY", hisTradeData.getString("ADVANCE_PAY"));
        newOrder.put("FEE_STATE", hisTradeData.getString("FEE_STATE"));

        newOrder.put("CANCEL_TAG", "2");
        newOrder.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newOrder.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newOrder.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newOrder.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newOrder.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));

        newOrder.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("FINISH_DATE", "");
        newOrder.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));

        newOrder.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newOrder.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));

        newOrder.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newOrder.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newOrder.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newOrder.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));
        newOrder.put("SUBSCRIBE_TYPE", "0");// 默认写个0吧
        Dao.insert("TF_B_ORDER", newOrder,Route.getJourDbDefault());
        return newOrderId;
    }

    /**
     * 生成新订单数据
     * 
     * @pageData 页面传过来的数据；
     * @pubData 一些公共信息如：系统当前时间，工号、登陆地市等。
     * @throws Exception
     */
    public IData createCancelTrade(IData hisTradeData, IData pageData, IData pubData, String newOrderId) throws Exception
    {
        IData newTradeData = new DataMap();
        newTradeData.putAll(hisTradeData);

        /********* 费用 *******************************/
        String strForegift = pageData.getString("FOREGIFT", "");
        if (strForegift.length() == 0)
        {
            strForegift = hisTradeData.getString("FOREGIFT");
        }
        long lSubscribeType = hisTradeData.getLong("SUBSCRIBE_TYPE", 0);
        long lOperFee = -hisTradeData.getLong("OPER_FEE", 0);
        long lAdvancePay = -hisTradeData.getLong("ADVANCE_PAY", 0);
        long lforegift = -hisTradeData.getLong("strForegift", 0);
        String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
        String cancelTag = pubData.getString("CANCEL_TAG");
        String oldFeeState = hisTradeData.getString("FEE_STATE");
        if("2".equals(cancelTag))//put CANCEL_TAG=2的订单FEE_STATE值时，
        {
        	if("0".equals(oldFeeState)&&"1".equals(strFeeState))//如果原订单FEE_STATE=0,且费用大于0
        	{
        		strFeeState = "0";
        	}
        }
        
        newTradeData.put("SUBSCRIBE_TYPE", String.valueOf((lSubscribeType / 10) * 10)); // 原订单类型转为相应的立即执行类型
        newTradeData.put("OPER_FEE", String.valueOf(lOperFee));
        newTradeData.put("ADVANCE_PAY", String.valueOf(lAdvancePay));
        newTradeData.put("FOREGIFT", String.valueOf(lforegift));
        newTradeData.put("FEE_STATE", strFeeState);
        newTradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
        newTradeData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("STAFF_ID"));

        newTradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        
        newTradeData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
        newTradeData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newTradeData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        newTradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));

        newTradeData.put("CANCEL_TAG", cancelTag);// 2=返销 3=取消
        newTradeData.put("CANCEL_DATE", hisTradeData.getString("ACCEPT_DATE"));
        newTradeData.put("CANCEL_STAFF_ID", hisTradeData.getString("TRADE_STAFF_ID"));
        newTradeData.put("CANCEL_DEPART_ID", hisTradeData.getString("TRADE_DEPART_ID"));
        newTradeData.put("CANCEL_CITY_CODE", hisTradeData.getString("TRADE_CITY_CODE"));
        newTradeData.put("CANCEL_EPARCHY_CODE", hisTradeData.getString("TRADE_EPARCHY_CODE"));
        // 以下字段取默认值
        newTradeData.put("BPM_ID", "");
        newTradeData.put("SUBSCRIBE_STATE", "0");
        newTradeData.put("NEXT_DEAL_TAG", "0");
        newTradeData.put("OLCOM_TAG", hisTradeData.getString("OLCOM_TAG"));
        newTradeData.put("FINISH_DATE", "");
        newTradeData.put("EXEC_ACTION", "");
        newTradeData.put("EXEC_RESULT", "");
        newTradeData.put("EXEC_DESC", "");

        newTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
        newTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
        newTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
        newTradeData.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));

        newTradeData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        newTradeData.put("TERM_IP", CSBizBean.getVisit().getRemoteAddr());
        newTradeData.put("INVOICE_NO", pageData.getString("INVOICE_NO", ""));
        newTradeData.put("REMARK", pageData.getString("REMARKS", ""));

        // 待确认
        newTradeData.put("CHANNEL_TRADE_ID", "");
        newTradeData.put("CHANNEL_ACCEPT_TIME", "");
        newTradeData.put("CANCEL_TYPE_CODE", "");
        newTradeData.put("RSRV_TAG1", "");

        // 如果取出来的老trade没有pfwait值(老系统倒换过来的数据)，如果olcom_tag=1,则PFwait写为1，否则写为0，兼容一下
        if (StringUtils.isBlank(newTradeData.getString("PF_WAIT")))
        {
            if (StringUtils.equals("1", newTradeData.getString("OLCOM_TAG")))// 如果原单据是要发指令的
            {
                newTradeData.put("PF_WAIT", "1");
            }
            else
            {
                newTradeData.put("PF_WAIT", "0");
            }
        }

        newTradeData.put("ORDER_ID", newOrderId);// 新的订单号
        if (!Dao.insert("TF_B_TRADE", newTradeData, Route.getJourDb(BizRoute.getRouteId())))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_304, pubData.getString("TRADE_ID"));
        }

        return newTradeData;
    }
    
    /**
     * 修改原历史订单
     * 
     * @param pubData
     * @throws Exception
     */
    public void modifyHisTrade(IData pubData) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", pubData.getString("TRADE_ID"));
        param.put("CANCEL_TAG", "1");// 被返销
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        int cnt = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPD_HISTRADE_CANCEL_TAG", param, Route.getJourDb(BizRoute.getRouteId()));
        if (cnt < 1)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_305, pubData.getString("TRADE_ID"));
        }
    }
    
    /**
     * 修改原历史订单
     * 
     * @param pubData
     * @throws Exception
     */
    public void modifyHisTradeFeeTime(IData pubData) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", pubData.getString("TRADE_ID"));
        param.put("CANCEL_TAG", "1");// 被返销
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        param.put("FEE_TIME", pubData.getString("SYS_TIME"));
        param.put("FEE_STAFF_ID", pubData.getString("STAFF_ID"));
        int cnt = Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPD_HISTRADE_FEE_TIME", param, Route.getJourDb(BizRoute.getRouteId()));
        if (cnt < 1)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_305, pubData.getString("TRADE_ID"));
        }
    }
     
    /**
     * 处理trade_staff表
     * 
     * @param newOrderId
     * @param pubData
     * @throws Exception
     */
    public void createCancelStaffTrade(String newOrderId, IData pubData) throws Exception
    {
        String tradeId = pubData.getString("TRADE_ID");
        IDataset tradeStaffInfos = TradeStaffInfoQry.queryTradeStaffByTradeId(tradeId, "0");

        if (IDataUtil.isEmpty(tradeStaffInfos))
        {
            return;
        }

        // 修改原单据的状态
        IData param = new DataMap();

        param.put("TRADE_ID", tradeId);
        param.put("DAY", tradeStaffInfos.getData(0).getString("DAY"));
        param.put("CANCEL_TAG", "1");// 被返销
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));

        StringBuilder sb = new StringBuilder(200);
        sb.append(" UPDATE  TF_BH_TRADE_STAFF ");
        sb.append(" SET   CANCEL_TAG=:CANCEL_TAG, ");
        sb.append(" CANCEL_DATE=to_date(:CANCEL_DATE,'YYYY-MM-DD HH24:MI:SS'), ");
        sb.append(" CANCEL_STAFF_ID=:CANCEL_STAFF_ID, ");
        sb.append(" CANCEL_DEPART_ID=:CANCEL_DEPART_ID, ");
        sb.append(" CANCEL_CITY_CODE=:CANCEL_CITY_CODE, ");
        sb.append(" CANCEL_EPARCHY_CODE=:CANCEL_EPARCHY_CODE ");
        sb.append(" WHERE TRADE_ID =:TRADE_ID");
        sb.append(" AND DAY=:DAY");
        sb.append(" AND CANCEL_TAG='0' ");

        Dao.executeUpdate(sb, param,Route.getJourDbDefault());

        // 新增一条cancel_tag=2的单据
        IData newData = tradeStaffInfos.getData(0);

        if (newData.getLong("OPER_FEE", 0) > 0)
        {
            newData.put("OPER_FEE", (-1) * newData.getLong("OPER_FEE", 0));
        }

        if (newData.getLong("FOREGIFT", 0) > 0)
        {
            newData.put("FOREGIFT", (-1) * newData.getLong("FOREGIFT", 0));
        }

        if (newData.getLong("ADVANCE_PAY", 0) > 0)
        {
            newData.put("ADVANCE_PAY", (-1) * newData.getLong("ADVANCE_PAY", 0));
        }

        newData.put("ORDER_ID", newOrderId);
        newData.put("CANCEL_TAG", "2");
        newData.put("EXEC_TIME", pubData.getString("SYS_TIME"));

        // 新单据cancel字段记录的是原单据的 受理相关信息
        newData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        newData.put("CANCEL_DATE", newData.getString("ACCEPT_DATE"));
        newData.put("CANCEL_STAFF_ID", newData.getString("TRADE_STAFF_ID"));
        newData.put("CANCEL_DEPART_ID", newData.getString("TRADE_DEPART_ID"));
        newData.put("CANCEL_CITY_CODE", newData.getString("TRADE_CITY_CODE"));
        newData.put("CANCEL_EPARCHY_CODE", newData.getString("TRADE_EPARCHY_CODE"));
        newData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        newData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        newData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        newData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        
        if(StringUtils.isNotBlank(newData.getString("FEE_TIME")))
        {
            newData.put("FEE_STATE", newData.getString("FEE_STATE"));
        	newData.put("FEE_TIME", pubData.getString("SYS_TIME"));
            newData.put("FEE_STAFF_ID", pubData.getString("STAFF_ID"));
        }

        // 分区号
        newData.put("DAY", SysDateMgr.getSysDate("dd"));
 
        Dao.insert("TF_BH_TRADE_STAFF", newData,Route.getJourDbDefault());
    }
    
    public IData getProductAmt(IData input) throws Exception{
    	
    	IDataUtil.chkParam(input, "PRODUCT_ID"); 
    	
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "请求成功");
    	
        BigDecimal amtAll = new BigDecimal("0");
    	String iv_product_id = input.getString("PRODUCT_ID");
    	IDataset newProductElements = ProductElementsCache.getProductElements(iv_product_id);
        if(IDataUtil.isNotEmpty(newProductElements))
    	{
        	for (int i = 0; i < newProductElements.size(); i++)
    		{
        		IData ProductElement = newProductElements.getData(i);
        		String strElementID = ProductElement.getString("ELEMENT_ID", "");
    			String strElementTypeCode = ProductElement.getString("ELEMENT_TYPE_CODE", "");
    			String strElementForceTag = ProductElement.getString("ELEMENT_FORCE_TAG", "");
    			String strGroupForceTag = ProductElement.getString("PACKAGE_FORCE_TAG", "");
    			//取主产品下的构成必选优惠，或者主产品下必选组的优惠
		        if("D".equals(strElementTypeCode) && ("1".equals(strElementForceTag) || "1".equals(strGroupForceTag)))
				{
		        
		        	IData map = new DataMap();
		        	map.put("PRODUCT_OFFERING_ID", strElementID);  
		        	IDataset returnList = AcctCall.productOfferingConfig(map);
		            if(IDataUtil.isNotEmpty(returnList)){
		            	for(int j = 0; j < returnList.size(); j++){
		            		String type = returnList.getData(j).getString("type");
		            		if("Z".equals(type)){
		            			String discntAmt =returnList.getData(j).getString("busiprice","0");
				            	amtAll = amtAll.add(new BigDecimal(discntAmt));
		            		}
		            	}
		        	}
		        	
				}
    		}
    	}
        result.put("AMT", amtAll.toString());
    	return result;
    }
}
