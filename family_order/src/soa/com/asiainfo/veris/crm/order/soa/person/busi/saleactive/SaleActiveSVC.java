
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;
import com.umpay.mpay.SignUtil; 

public class SaleActiveSVC extends CSBizService
{
    private static final long serialVersionUID = 2439362128137845255L;
    protected static Logger log = Logger.getLogger(SaleActiveSVC.class);

    public IData bookTerminalActive(IData params) throws Exception
    {
        SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        IData checkResult = saleActiveBean.checkTerminalInfoByPk(params);
        params.put("PRODUCT_NAME", checkResult.getString("PRODUCT_NAME"));
        params.put("PACKAGE_NAME", checkResult.getString("PACKAGE_NAME"));
        saleActiveBean.bookTerminalActive(params);

        IData returnData = new DataMap();
        returnData.put("X_RESULTINFO", "活动预约成功！");
        returnData.put("X_RECORDNUM", "1");
        returnData.put("X_RESULTCODE", "0");
        return returnData;
    }

    public IData cancelBookTerminalActive(IData params) throws Exception
    {
        SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        saleActiveBean.cancelBookTerminalActive(params);
        IData returnData = new DataMap();
        returnData.put("X_RESULTINFO", "活动预约取消成功！");
        returnData.put("X_RECORDNUM", "1");
        returnData.put("X_RESULTCODE", "0");
        return returnData;
    }

    public IDataset checkSaleBook(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        return saleActiveBean.checkSaleBook(serialNumber);
    }

    public IDataset querySaleBook(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        return saleActiveBean.querySaleBookList(serialNumber);
    }

    public void trade4SchoolActive(IData params) throws Exception
    {
        SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        saleActiveBean.trade4SchoolActive(params);
    }
/*    public IDataset queryAddrInfo(IData input) throws Exception
    {
    	  String userId = input.getString("USER_ID");
          SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
          return saleActiveBean.queryAddrInfoByUserId(userId);
    }
    
    public IDataset addAddrInfo(IData input) throws Exception
    {
    	input.put("ADDR_ID", SeqMgr.getLogId());
    	input.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	input.put("UPDATE_STAFF_ID", getVisit().getStaffId());
    	input.put("UPDATE_DEPART_ID", getVisit().getDepartId());
    	Dao.insert("TF_F_USER_LOGISTICS", input);
    	IDataset set = new DatasetList();
    	set.add(input);
    	return set;
    }
    
    public IDataset delAddrInfo(IData input) throws Exception
    {
    	Dao.delete("TF_F_USER_LOGISTICS", input);
    	IDataset set = new DatasetList();
        set.add(input);
        return set;
    }
    
    public IDataset updAddrInfo(IData input) throws Exception
    {
    	input.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	input.put("UPDATE_STAFF_ID", getVisit().getStaffId());
    	input.put("UPDATE_DEPART_ID", getVisit().getDepartId());
    	Dao.update("TF_F_USER_LOGISTICS", input);
    	IDataset set = new DatasetList();
        set.add(input);
        return set;
    }
    
    public IDataset queryTrackDetailInfo(IData input) throws Exception
    {
    	String trackId = input.getString("TRACK_ID");
    	WebServiceClient client = new WebServiceClient();
    	SearchOrderBean  bean  = new SearchOrderBean();
    	bean.setLogisticProviderID("HN_CHINAMOBILOE");
    	OrderBean[] orders = new OrderBean[1];
    	for(int i=0;i<orders.length;i++){
    		OrderBean b = new OrderBean();
    		b.setMainNo(trackId);
    		b.setOrderNo(trackId);
    		b.setSubOrderNo("");
    		orders[i] = b;
    	}
    	bean.setOrders(orders);
    	try
    	{
	    	SearchOrderBody body = client.searchOrder(bean);
	    	TSearchOrder[] qorders = body.getOrders();
	    	if(qorders.length <=0){
	    		return null;
	    	}
	    	IDataset trackList = new DatasetList();
	    	for(int a=0;a<qorders.length;a++){
	    		IData tradeData = new DataMap();
	    		tradeData.put("mailNo",qorders[a].getMailNo());
	    		tradeData.put("orderNo",qorders[a].getOrderNo());
	    		tradeData.put("orderStatus",qorders[a].getOrderStatus());
	    		tradeData.put("statusTime",qorders[a].getStatusTime());
	    		tradeData.put("remark",qorders[a].getRemark());
	    		IDataset stepInfos = new DatasetList();
	    		for(int b=0;b<qorders[a].getSteps().length;b++){
	    			IData  sData = new DataMap();
	    			sData.put("acceptTime", qorders[a].getSteps()[b].getAcceptTime());
	    			sData.put("acceptAddress", qorders[a].getSteps()[b].getAcceptAddress());
	    			stepInfos.add(sData);
	    		}
	    		tradeData.put("steps", stepInfos);
	    		trackList.add(tradeData);
	    	}
	    	return trackList;
    	}
    	catch(Exception e)
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "物流信息不存在！");
    		return null ;
    	}
    	/*
    	IDataset trackList = new DatasetList();
    	IData tradeData = new DataMap();
		tradeData.put("mailNo","12332121321213");
		tradeData.put("orderNo","123321213212321");
		tradeData.put("orderStatus","1");
		tradeData.put("statusTime","2014-12-11 11:11:11");
		tradeData.put("remark","ceshifdsfds");
		IDataset stepInfos = new DatasetList();
		for(int b=0;b<3;b++){
			IData  sData = new DataMap();
			sData.put("acceptTime", "2014-12-11 11:11:11");
			sData.put("acceptAddress", "CESHISAAAA");
			stepInfos.add(sData);
		}
		tradeData.put("steps", stepInfos);
    	trackList.add(tradeData);
    	
    }
    
    public IDataset queryTrackInfoByCond(IData input) throws Exception{
    	SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        return saleActiveBean.queryTrackInfoByCond(input,getPagination());
    }
    
    public void delTrackInfo(IData param) throws Exception{
    	String trackIds = param.getString("TRACK_IDS");
    	String[] track  =trackIds.split(",");
    	for(int i=0;i<track.length;i++){
    		String trackId = track[i];

    		IDataset tset = AddrInfoQry.qryTrackInfoByTrackId(trackId);
    		if(IDataUtil.isEmpty(tset)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"物流配送订单错误");
    		}
    		String state = tset.getData(0).getString("STATE");
    		if("2".equals(state)){
    			WebServiceClient client = new WebServiceClient();
            	CancelOrderBean bean = new CancelOrderBean();
            	bean.setLogisticProviderID("HN_CHINAMOBILOE");
            	bean.setOrderNo(trackId);
            	bean.setInfoType("INSTRUCTION");
            	bean.setInfoContent("WITHDRAW");
            	bean.setRemark("营销活动宅急送取消");
            	boolean canOrder = client.cancelOrder(bean);
            	if(!canOrder){
            		CSAppException.apperr(CrmCommException.CRM_COMM_103,"删除物流配送订单错误");
            	}
    		}
    		
    		IData params = new DataMap();
    		params.put("STATE", "4");
    		params.put("TRACK_ID", trackId);
    		Dao.executeUpdateByCodeCode("TF_F_USER_TRACKINFO", "UPD_TRACK_STATE", params);
    	}
    }
    
    public void endTrackInfo(IData param) throws Exception{//已配送
    	String trackIds = param.getString("TRACK_IDS");
    	String[] track  =trackIds.split(",");
    	for(int i=0;i<track.length;i++){
    		String trackId = track[i];
    		IData params = new DataMap();
    		params.put("STATE", "3");
    		params.put("TRACK_ID", trackId);
    		Dao.executeUpdateByCodeCode("TF_F_USER_TRACKINFO", "UPD_TRACK_STATE", params);
    	}
    }
    public void subTrackInfo(IData param) throws Exception{//安排配送
    	//调用接口，产生物流订单
    	String trackIds = param.getString("TRACK_IDS");
    	String[] track  =trackIds.split(",");
    	for(int i=0;i<track.length;i++){
    		String trackId = track[i];
    		
    		IDataset tset = AddrInfoQry.qryTrackInfoByTrackId(trackId);
    		if(IDataUtil.isEmpty(tset)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"物流配送订单错误");
    		}
    		String addrId = tset.getData(0).getString("ADDR_ID");
    		
    		CreateOrderBean bean = new CreateOrderBean();
    		bean.setLogisticProviderID("HN_CHINAMOBILOE");
    		bean.setOrderNo(trackId);
    		bean.setSubOrderNo("");
    		bean.setTradeNo(trackId);
    		bean.setMailNo(trackId);
    		bean.setType("0");
    		bean.setFlag("");
    		AddressBean sender = new AddressBean();
    		sender.setName(tset.getData(0).getString("SEND_NAME"));
    		sender.setPostCode("");
    		sender.setPhone("");
    		sender.setMobile(tset.getData(0).getString("SEND_SN"));
    		sender.setProv("海南");
    		sender.setCity("");
    		sender.setDistrict("");
    		sender.setAddress("寄件人");
    		bean.setSender(sender);
    		
    		IDataset set = AddrInfoQry.queryAddrInfoById(addrId);
    		if(IDataUtil.isEmpty(set)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"收件人地址数据无效!");
    		}
    		AddressBean receiver = new AddressBean();
    		receiver.setName(set.getData(0).getString("NAME"));
    		receiver.setPostCode(set.getData(0).getString("POST_CODE"));
    		receiver.setPhone(set.getData(0).getString("PHONE"));
    		receiver.setMobile(set.getData(0).getString("SERIAL_NUMBER"));
    		receiver.setProv(set.getData(0).getString("PROVINCE"));
    		receiver.setCity(set.getData(0).getString("CITY"));
    		receiver.setDistrict(set.getData(0).getString("DISTRICT"));
    		receiver.setAddress(set.getData(0).getString("ADDRESS"));
    		bean.setReceiver(receiver);
    		bean.setSendStartTime(SysDateMgr.getSysTime());
    		bean.setSendEndTime(SysDateMgr.addDays(5));
    		IDataset pset = TradeFeeRegQry.selTradeFeePayMoney(trackId);
    		bean.setCodAmount("1");
    		if(IDataUtil.isNotEmpty(pset)){
    			bean.setCodAmount(String.valueOf(pset.getData(0).getInt("MONEY")/100));
    		}
    		
    		IDataset goods = UserSaleActiveInfoQry.getGoodsInfoByMobPhoneService(tset.getData(0).getString("USER_ID"),trackId, "4");
    		ItemBean[] items = new ItemBean[goods.size()];
    		for(int a=0;a<items.length;a++){
    			ItemBean temp = new ItemBean();
    			temp.setItemName(goods.getData(a).getString("GOODS_NAME"));
    			temp.setItemNumber(goods.getData(a).getString("GOODS_NUM"));
    			temp.setItemValue(String.valueOf(goods.getData(a).getInt("GOODS_VALUE")/100));
    			temp.setItemVolume("");
    			temp.setItemWeight("");
    			items[a] = temp;
    		}
    		bean.setItems(items);
    		bean.setItemsName("");
    		bean.setItemsNumber("1");
    		bean.setItemsWeight("");
    		bean.setItemsVolume("");
    		bean.setItemsValue("");
    		bean.setInsuranceValue("");
    		bean.setRemark("");
    		bean.setDataFlag("");
    		
    		WebServiceClient client = new WebServiceClient();
    		boolean flag = client.createOrder(bean);
    		if(!flag){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"物流信息创建错误！");
    		}
    		
    		
    		IData params = new DataMap();
    		params.put("STATE", "2");
    		params.put("TRACK_ID", trackId);
    		Dao.executeUpdateByCodeCode("TF_F_USER_TRACKINFO", "UPD_TRACK_STATE", params);
    	}
		
    }
    
 	//查询业务区信息
    public IDataset queryInitInfo(IData input) throws Exception
    {	
        IDataset result = SundryQry.initCityArea(true);
        return result;
    }
    public IDataset queryTrackStatus(IData input) throws Exception
    {
         String csm = input.getString("SUBSYS_CODE");
         String parattr = input.getString("PARAM_ATTR");
         String epcode = input.getString("EPARCHY_CODE");
      // String paracode= input.getString("");
         String paracode= "0";
    	 IDataset result = CommparaInfoQry.getCommparaAllCol(csm,parattr,paracode, epcode);
    	 return result;
    }*/
    
    public IData EndSaleActive(String months , String Fee , IData saleactive) throws Exception
    {
    	IData svcParam = new DataMap();
    	IData result = new DataMap();
        svcParam.put("SERIAL_NUMBER", saleactive.getString("SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", saleactive.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", saleactive.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", saleactive.getString("RELATION_TRADE_ID"));
        svcParam.put("CAMPN_TYPE", saleactive.getString("CAMPN_TYPE"));
        svcParam.put("REMARK", "机惠专享购机活动B消费总额大于约定消费额度");
        svcParam.put("RETURNFEE","0");
        svcParam.put("INTERFACE", "1");
        svcParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
        svcParam.put("END_DATE_VALUE",SysDateMgr.getLastMonthLastDate()); 
        
        svcParam.put("TRADE_EPARCHY_CODE", "0898");// 受理地州
		svcParam.put("PROVINCE_CODE", "0898");
		svcParam.put("EPARCHY_CODE", "0898");
		svcParam.put("CITY_CODE", "HNSJ");
		svcParam.put("TRADE_CITY_CODE","HNSJ");
		svcParam.put("TRADE_DEPART_ID","36601");
		svcParam.put("TRADE_STAFF_ID","SUPERUSR");
		svcParam.put("IN_MODE_CODE","0");
		
		svcParam.put("STAFF_ID","SUPERUSR");
		svcParam.put("STAFF_NAME","AEE调用");
		svcParam.put("LOGIN_EPARCHY_CODE","0898");
		svcParam.put("STAFF_EPARCHY_CODE","0898");
		svcParam.put("DEPART_ID","36601");
		svcParam.put("DEPART_CODE","HNSJ0000");
		
		CSBizBean.getVisit().setStaffId("SUPERUSR");
        CSBizBean.getVisit().setCityCode("0898");
        CSBizBean.getVisit().setDepartId("36601");
        CSBizBean.getVisit().setInModeCode("0");
		
        try
        {
        	CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", svcParam);
        	saleactive.put("MONTHS", months);
     		saleactive.put("TOTAL_CONSUME", Fee);
     		saleactive.put("UPDATE_TIME",SysDateMgr.getSysTime());
     		saleactive.put("END_DATE",SysDateMgr.getLastMonthLastDate());
     		saleactive.put("RSRV_STR1","1");
     		Dao.update("TF_F_USER_SALEACTIVE_CONSUME", saleactive);
     		result.put("X_RESULTCODE", "0");
    		result.put("X_RESULEINFO", "用户办理活动生效起"+months+"个月总消费(分)："+Fee);
        }
        catch(Exception ex)
        {       	
        	/*int len = 0 ;
        	int lentmp = 0 ;
        	result.put("X_RESULTCODE", "-1");
			result.put("X_RESULEINFO", "机惠专享购机活动B终止异常："+saleactive.getString("SERIAL_NUMBER")+" "+saleactive.getString("PACKAGE_ID")+" "+ex.getMessage());
			
			lentmp = result.getString("X_RESULEINFO").length();
			len = lentmp>2000?2000:lentmp;
			saleactive.put("UPDATE_TIME",SysDateMgr.getSysTime());
			saleactive.put("RSRV_STR1","0");
			saleactive.put("RSRV_STR5",result.getString("X_RESULEINFO").substring(0, len));
			Dao.update("TF_F_USER_SALEACTIVE_CONSUME", saleactive);*/
			throw ex ;
        }        	
		return result ;
    }
    
    public IData EndSaleActiveByPID(IData saleactive) throws Exception
    {
    	IData svcParam = new DataMap();
    	IData result = new DataMap();
    	String packageId = "";
        svcParam.put("SERIAL_NUMBER", saleactive.getString("SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", saleactive.getString("PRODUCT_ID"));
        //svcParam.put("PACKAGE_ID", saleactive.getString("PACKAGE_ID"));
        
        IData param1 = new DataMap();
        String serialNumber = saleactive.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        param1.put("USER_ID", userId);
        param1.put("PRODUCT_ID",saleactive.getString("PRODUCT_ID"));
        //param1.put("PACKAGE_ID", saleactive.getString("PACKAGE_ID"));
        param1.put("PROCESS_TAG", "0");
        IDataset dataset1 = Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PPID", param1);
        if(IDataUtil.isNotEmpty(dataset1)&&dataset1.size() > 1){
        	
        	result.put("X_RESULTCODE", "1196");
    		result.put("X_RESULEINFO", "用户有多个生效的营销包，不允许终止");
    		return result ;
        	
        }
        String relationTradeId = "";
        String campnType = "";
        if(IDataUtil.isNotEmpty(dataset1)&&dataset1.size() == 1){
        	relationTradeId = dataset1.getData(0).getString("RELATION_TRADE_ID");
        	campnType = dataset1.getData(0).getString("CAMPN_TYPE");
        	packageId = dataset1.getData(0).getString("PACKAGE_ID");
        }
        
        svcParam.put("PACKAGE_ID", packageId);
        svcParam.put("RELATION_TRADE_ID", relationTradeId);
        svcParam.put("CAMPN_TYPE", campnType);
        svcParam.put("REMARK", "校园营销活动终止");
        svcParam.put("RETURNFEE","0");
        svcParam.put("INTERFACE", "1");
        svcParam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        //svcParam.put("END_DATE_VALUE",SysDateMgr.getLastDateThisMonth()); 
        svcParam.put("FORCE_END_DATE",SysDateMgr.getLastDateThisMonth());
        svcParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 受理地州
		svcParam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
		svcParam.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
		svcParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		svcParam.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
		svcParam.put("TRADE_CITY_CODE",CSBizBean.getVisit().getCityCode());
		svcParam.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
		svcParam.put("TRADE_STAFF_ID",CSBizBean.getVisit().getStaffId());
		svcParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		svcParam.put("IN_MODE_CODE", saleactive.getString("IN_MODE_CODE"));
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULEINFO", "受理成功");
        try
        {
        	IDataset SaleActives = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", svcParam);
        }
        catch(Exception ex)
        {
        	result.put("X_RESULTCODE", "-1");
			result.put("X_RESULEINFO", "校园营销活动终止异常："+ex.getMessage());
        }        	
		return result ;
    }
    
    public IDataset CalConsumeAndEndSaleActive(IData input) throws Exception
    {
    	IDataset results=new DatasetList();
    	SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
    	//获取用户有效的 营销活动： 机惠专享购机活动B
    	IDataset saleactives =saleActiveBean.querySaleActiveByPid();
    	String date1 = SysDateMgr.getlastMonthFirstDate();
    	date1 = SysDateMgr.decodeTimestamp(date1,SysDateMgr.PATTERN_TIME_YYYYMM);
		if(IDataUtil.isNotEmpty(saleactives))
		{
			for(int k=0;k<saleactives.size();k++)
			{								
			    //用户不能重复办理 购机活动B
				IData saleactive = saleactives.getData(k);
	    		IData result =new DataMap();
				IDataset ConsumeRecs = saleActiveBean.queryConsumeByCurMonths(saleactive);
				if(IDataUtil.isEmpty(ConsumeRecs))
				{
					//调用账务接口 ，获取上月消费水平
					IData param1 = new DataMap();
			    	param1.put("X_PAY_USER_ID",saleactive.getString("USER_ID"));
			    	param1.put("END_CYCLE_ID",date1);
			    	param1.put("START_CYCLE_ID",date1); 
			    	param1.put("TRADE_EPARCHY_CODE", "0898");// 受理地州
			    	param1.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
			    	param1.put(Route.ROUTE_EPARCHY_CODE, "0898");// 路由地州
                    param1.put("EPARCHY_CODE", "0898");
	   				param1.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
	   				param1.put("TRADE_CITY_CODE",CSBizBean.getVisit().getCityCode());
	   				param1.put("TRADE_DEPART_ID","36601");
	   				param1.put("TRADE_STAFF_ID","SUPERUSR");
			    	//获取消费记录
					IDataset ConsumeRecords = saleActiveBean.queryConsumeByPid(saleactive);
					try
					{	
						IDataOutput output = CSAppCall.callAcct("AM_QueryNewUserBill", param1, false);
				    	IData allInfo = output.getData().getData(0);
				        //IDataset allInfos=CSAppCall.call("http://10.200.130.83:10000/service","AM_QueryNewUserBill", param1, false);
				        //IData allInfo = allInfos.getData(0);				        
				    	IDataset billinfos = allInfo.getDataset("BILL_INFOS");
				    	String totelfee = "0" ;
				    	String agentfee = "0" ;
				    	int n = 0 ;
				    	for(int i=0,size=billinfos.size();i<size;i++)
				    	{
				    		IData billinfo =new DataMap();
				    		billinfo = billinfos.getData(i);
				    		
				    		if(billinfo.getString("INTEGRATE_ITEM","").contains("代收费业务费用"))
				    		{
				    			agentfee = billinfo.getString("FEE","");
				    			n++ ;
				    		}
				    		
				    		if(billinfo.getString("INTEGRATE_ITEM","").contains("小计"))
				    		{
				    			totelfee = billinfo.getString("FEE","");
				    			n++ ;
				    		}
				    		
				    		if(n==2)
				    			break ;
				    	}
				    	
				    	int Feetmp = Integer.parseInt(totelfee)-Integer.parseInt(agentfee);   	
				    	String Fee = String.valueOf(Feetmp);//用户上月消费水平
						
						//String Fee = "800000";
				    	//int Feetmp = Integer.parseInt(Fee);
						IDataset pkexts =  PkgExtInfoQry.queryPackageExtInfo(saleactive.getString("PACKAGE_ID"),"0898");				
						if(IDataUtil.isNotEmpty(pkexts))
						{
							String YDfee = pkexts.getData(0).getString("RSRV_STR7");
							int YDfeetmp = Integer.parseInt(YDfee);
							if(IDataUtil.isEmpty(ConsumeRecords))
							{					
								saleactive.put("MONTHS", "0");
								saleactive.put("TOTAL_CONSUME", "0");
								saleactive.put("UPDATE_TIME",SysDateMgr.getSysTime());
								saleactive.put("RSRV_STR1","0");
								Dao.insert("TF_F_USER_SALEACTIVE_CONSUME", saleactive);
								if(Feetmp>YDfeetmp||YDfeetmp==Feetmp)
								{							
									
									result=EndSaleActive("1",Fee,saleactive);
									results.add(result);
								}
								else
								{
									saleactive.put("MONTHS", "1");
						     		saleactive.put("TOTAL_CONSUME", Fee);
						     		saleactive.put("RSRV_STR1","1");
						     		Dao.update("TF_F_USER_SALEACTIVE_CONSUME", saleactive);
									result.put("X_RESULTCODE", "0");
									result.put("X_RESULEINFO", "用户办理活动生效的当月消费(分)："+Fee);
									results.add(result);
								}
							}
							else
							{
								//计算总消费额，消费额小于约定额度时更新消费记录，当总消费额大于等于约定额度时终止活动 
								IData ConsumeRecord = ConsumeRecords.getData(0);
								String months = ConsumeRecord.getString("MONTHS");
								String total_consume = ConsumeRecord.getString("TOTAL_CONSUME");
								int totalfeetem = Integer.parseInt(total_consume) + Integer.parseInt(Fee);	
								months = String.valueOf(Integer.parseInt(months)+1);						
								if(totalfeetem>YDfeetmp || YDfeetmp==totalfeetem)
								{	
									result=EndSaleActive(months,String.valueOf(totalfeetem),ConsumeRecord);
									results.add(result);
								}
								else
								{							
									ConsumeRecord.put("MONTHS", months);
									ConsumeRecord.put("TOTAL_CONSUME", String.valueOf(totalfeetem));
									ConsumeRecord.put("UPDATE_TIME",SysDateMgr.getSysTime());
									ConsumeRecord.put("RSRV_STR1","1");
									Dao.update("TF_F_USER_SALEACTIVE_CONSUME", ConsumeRecord);
									result.put("X_RESULTCODE", "0");
									result.put("X_RESULEINFO", "用户办理活动生效起"+months+"个月总消费(分)："+String.valueOf(totalfeetem));
									results.add(result);
								}															
							}
						}
						else
						{
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "扩展包不存在 ："+saleactive.getString("PACKAGE_ID"));
						}

				    }
					catch(Exception ex)
					{
					//	log.error("<<<<wuxd>>>>"+ex.getMessage());
						int len = 0 ;
			        	int lentmp = 0 ;
			        	
						if(IDataUtil.isEmpty(ConsumeRecords))
						{
							result.put("X_RESULTCODE", "-1");
							result.put("X_RESULEINFO", "用户第一月消费，调用账务接口异常："+saleactive.getString("SERIAL_NUMBER")+" "+saleactive.getString("PACKAGE_ID")+" "+ex.getMessage());							
							lentmp = result.getString("X_RESULEINFO").length();
				        	len = lentmp>200?200:lentmp;
							saleactive.put("MONTHS", "0");
							saleactive.put("TOTAL_CONSUME", "0");
							saleactive.put("UPDATE_TIME",SysDateMgr.getSysTime());
							saleactive.put("RSRV_STR1","0");
							saleactive.put("RSRV_STR5",result.getString("X_RESULEINFO").substring(0, len));
							Dao.insert("TF_F_USER_SALEACTIVE_CONSUME", saleactive);
							results.add(result);
						}
						else
						{
							result.put("X_RESULTCODE", "-1");
							result.put("X_RESULEINFO", "调用账务接口异常："+saleactive.getString("SERIAL_NUMBER")+" "+saleactive.getString("PACKAGE_ID")+" "+ex.getMessage());					
							lentmp = result.getString("X_RESULEINFO").length();
				        	len = lentmp>200?200:lentmp;
							IData ConsumeRecotmp = ConsumeRecords.getData(0);
							ConsumeRecotmp.put("UPDATE_TIME",SysDateMgr.getSysTime());
							ConsumeRecotmp.put("RSRV_STR1","0");
							saleactive.put("RSRV_STR5",result.getString("X_RESULEINFO").substring(0, len));
							Dao.update("TF_F_USER_SALEACTIVE_CONSUME", ConsumeRecotmp);
							results.add(result);
						}
					}
				}
			}		
		}
		else
		{  
			IData result =new DataMap();
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULEINFO", "用户未办理机惠专享购机活动B或活动已到期");
			results.add(result);
		}		
    	return results;
    } 
    
    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
     * 和包平台调用公用接口
     * */
    public IData redPackPlatCall(IData input) throws Exception
    {   
    	//调认证接口
        String requestDate = SysDateMgr.getSysDateYYYYMMDD(); //YYYYMMDD
        String requestTime = SysDateMgr.getSysTime();//2016-08-24 18:19:51
        String callType=input.getString("CALL_TYPE","");
        requestTime=requestTime.substring(requestTime.indexOf(":")-2).replaceAll(":", "");//格式：HHMISS
       
        String baseURL = BizEnv.getEnvString("crm.call.RegPackCallUrl");
//        String appKey = BizEnv.getEnvString("crm.callflowcard.appKey"); 
//        String appSecret = BizEnv.getEnvString("crm.callflowcard.appSecret");
        if(StringUtils.isBlank(baseURL))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "发送红包调用平台地址crm.call.RegPackCallUrl为空:");
        } 
          
        String signString=input.getString("SIGN_STRING","");
        
        String requestXML ="";
        String response="";
        try
        {
        	//签名
        	String key_cert_url=BizEnv.getEnvString("crm.call.RegPackSignFileUrl");
        	String key_name="888002130000009_HNCMCC";//测试用，这里要换
        	String cert_name="testUmpay"; //测试用，这里要换
        	//String cert_name="888002130000009_HNCMCC"; //测试用，这里要换
        	if("PLACE_ORDER".equals(callType)||"RESEND_CODE".equals(callType)||"PAY_ORDER".equals(callType)||"SEND_SMS".equals(callType)){
        		String merid=input.getString("MERID","");
        		IDataset comms = CommparaInfoQry.getCommparaByAttrCode2("CSM", "6897",merid, "0898",null); 
        		if(IDataUtil.isNotEmpty(comms)){
        			key_name=comms.getData(0).getString("PARA_CODE3");
        			cert_name=comms.getData(0).getString("PARA_CODE4");
        		}
        	}
        	
        	
        	String keyFile = key_cert_url+key_name+".key.p8";
        	String KeyCheck = key_cert_url+cert_name+".cert.crt";
        	log.info("(========cxy===========keyFile==="+keyFile); 
        	log.info("(========cxy===========KeyCheck==="+KeyCheck); 
        	SignUtil.setMerPrikeyPath(keyFile);
        	SignUtil.setPlatCertPath(KeyCheck);
        	
            String sign=SignUtil.doGenerateSign(signString);
            
            log.info("(========cxy===========sign=1=="+sign); 
            
            requestXML = input.getString("REQUEST_XML",""); 
            
            requestXML=requestXML+"<SIGN>"+sign+"</SIGN></MESSAGE>";
            log.info("(========cxy===========baseURL==="+baseURL);
            log.info("(========cxy===========signString==="+signString);
            log.info("(========cxy===========requestXML==="+requestXML);
            response = doPostAuth(baseURL,requestXML,"utf-8",false); 
            log.info("(========cxy======response=====red_pack==="+response);
        }
        catch(Exception e)
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包调用平台失败:"+e.toString());
        }
        
        if("".equals(response))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "和包调用平台失败！");
        }
        
        IData resultData=new DataMap();
        //解析返回报文，获取token
        Document doc = DocumentHelper.parseText(response);
        Element rootElement = doc.getRootElement();
        String rcode = rootElement.element("RCODE").getStringValue(); 
        String desc = rootElement.element("DESC").getStringValue(); 
        //log.info("("========cxy======response=====desc==="+desc);
        if("000000".equals(rcode)){
        	resultData.put("X_RESULTCODE", "1");
        	resultData.put("X_RESULTINFO", desc);
        	if("CHECK_VALUE".equals(callType)){
//        		String allBalance=rootElement.element("AVL_TOT_BAL").getStringValue(); //可用总余额
//        		String elecQuan=rootElement.element("MER_RED_BAL").getStringValue(); //用户在该商户的可用电子券余额
        		String allBalance=rootElement.element("DRW_TOT_BAL").getStringValue(); //可用总余额
        		String elecQuan=rootElement.element("MER_CUR_AMT").getStringValue(); //用户在该商户的可用电子券余额
    	        resultData.put("ALL_BALANCE", allBalance);
            	resultData.put("ELEC_QUAN", elecQuan);
            }
        	if("PAY_ORDER".equals(callType)){
        		/*
        		 * AMT	交易金额	M	15	以分为单位
					ORDERID	商户订单号	M	20	商户订单号
					COUAMT	电子券消费金额	M	15	以分为单位
					VCHAMT	代金券消费金额	M	15	以分为单位
					CASHAMT	现金消费金额	M	15	以分为单位
        		 * */
        		resultData.put("AMT", rootElement.element("AMT").getStringValue());
            	resultData.put("ORDERID", rootElement.element("ORDERID").getStringValue());
            	//resultData.put("COUAMT", rootElement.element("COUAMT").getStringValue());
            	resultData.put("COUAMT", rootElement.element("COUPAMT").getStringValue());
            	resultData.put("CASHAMT", rootElement.element("CASHAMT").getStringValue());
            	resultData.put("CASHAMT", rootElement.element("CASHAMT").getStringValue());
        	}
        	if("RESEND_CODE".equals(callType)){
        		/**
        		 * AMT	交易金额	M	15	以分为单位
				ORDERID	商户订单号	M	20	商户订单号
				USERTOKEN	用户标志	M	64	用户手机号
        		 * */
        		resultData.put("AMT", rootElement.element("AMT").getStringValue());
            	resultData.put("ORDERID", rootElement.element("ORDERID").getStringValue());
            	resultData.put("USERTOKEN", rootElement.element("USERTOKEN").getStringValue());
        	}
        	if("PAY_RETURN".equals(callType)){
        		resultData.put("PLAT_RESULT_CODE",rcode);
        		resultData.put("CALL_XML",requestXML);
        		resultData.put("CALL_RESPONSE",response);
        	}
        	if("BACK_REDPACK".equals(callType)){
        		resultData.put("CALL_XML",requestXML);
        		resultData.put("CALL_RESPONSE",response);
        	}
        	//下发短信wangsc10--20190311
        	if("SEND_SMS".equals(callType)){
        		resultData.put("CALL_XML",requestXML);
        		resultData.put("CALL_RESPONSE",response);
        	}
        	
        }else{
        	resultData.put("X_RESULTCODE", rcode);
        	resultData.put("X_RESULTINFO", desc);
        }
        return resultData;
    }
    
    /**
     * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
     * 接口调用
     * */
    public static String doPostAuth(String url, String reqStr,String charset, boolean pretty)
    {
        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();

        PostMethod method = new PostMethod(url);
        try {
            method.setRequestEntity(new StringRequestEntity(reqStr, "application/xml", "utf-8"));
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),charset));
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    if (pretty)
                    {
                        response.append(line).append(
                        System.getProperty("line.separator"));
                    }
                    else
                    {
                        response.append(line);
                    }
                }
                reader.close();
            }
        } catch (UnsupportedEncodingException e1) 
        {
            return "";
        }catch (IOException e) {
            
            return "";
        } 
        finally 
        {
            method.releaseConnection();
        } 
        return response.toString();
    }
    
    public IData dealWholeNetCreditPurchases(IData params) throws Exception
    {
        SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        return saleActiveBean.dealWholeNetCreditPurchases(params);
    }
    
    /**
     * 信用购机，调用iboss接口
     * by chenyw7
     */
    public IDataset applyCreditPurchases(IData params) throws Exception
    {
    	IData input = new DataMap();
    	String tradeId = params.getString("TRADE_ID");
    	String productId = params.getString("PRODUCT_ID");
    	String serialNumber = params.getString("SERIAL_NUMBER");
    	String seq = params.getString("SEQ");
    	String oprId = params.getString("OPR_ID");
    	String oprMblNo = params.getString("OPR_MBL_NO");
    	String depId = params.getString("DEP_ID");
    	String depNm = params.getString("DEP_NM");
    	//String depNm = StaticUtil.getStaticValue(null, "TD_M_DEPART", "DEPART_ID", "DEPART_NAME", depId);
    	
    	
    	IDataset saleActiveTradeDatas = TradeSaleActive.getTradeSaleActiveByTradeId(tradeId);
    	IData saleActiveTradeData = saleActiveTradeDatas.getData(0);
    	
    	IDataset commparaInfos =new DatasetList(); 
        IDataset commparas=CommparaInfoQry.getCommNetInfo("CSM","3119",saleActiveTradeData.getString("PRODUCT_ID")) ;
		for(int i=0;i<commparas.size();i++){
			IData data=commparas.getData(i);
			if(StringUtils.isNotEmpty(data.getString("PARA_CODE4"))
					&&data.getString("PARA_CODE4").equals(saleActiveTradeData.getString("PACKAGE_ID"))){
				commparaInfos.add(data);
				break;
			}
		}
    	
    	
    	input.put("CUS_MBL_NO", serialNumber);
    	input.put("SEQ", seq);
    	input.put("BUSINESS_TYPE", "1");//0：非终端类 1：终端类

    	input.put("PRODUCT_ID",  productId);
    	input.put("PRODUCT_NM", UProductInfoQry.getProductNameByProductId(productId));
    	
    	input.put("OPR_ID", oprId);
        input.put("OPR_MBL_NO", oprMblNo);
        input.put("DEP_ID", depId);
        input.put("DEP_NM", depNm);
        
 
        input.put("PRODUCT_AMT", productAmt(productId)); //套餐金额,暂无法获取.存放0  
        
        input.put("PKG_MONTH", saleActiveTradeData.getString("MONTHS")); // 套餐合约期数
        input.put("BONUS_AMT", commparaInfos.getData(0).getString("PARA_CODE2","0.00"));//期返红包金额    
        input.put("BONUS_MONTH", commparaInfos.getData(0).getString("PARA_CODE3",saleActiveTradeData.getString("MONTHS")));//红包返还总期数
        
        IDataset saleGoodsTradeDatas = TradeSaleGoodsInfoQry.getTradeSaleGoodsByTradeId(tradeId);
		if (null != saleGoodsTradeDatas && saleGoodsTradeDatas.size() > 0) {
			IData  goodData = saleGoodsTradeDatas.getData(0);
			input.put("GOODS_TYPE_ID", goodData.getString("DEVICE_BRAND_CODE"));//商品类型编号
			input.put("GOODS_BRAND", goodData.getString("DEVICE_BRAND"));//商品品牌
			input.put("GOODS_NM", goodData.getString("DEVICE_MODEL"));
   
			//购机款分转成元
			double deviceCost=0.00;
			if(StringUtils.isNotEmpty(goodData.getString("DEVICE_COST"))){
				deviceCost=Double.parseDouble(goodData.getString("DEVICE_COST"))/100;
			}
			input.put("GOODS_PRICE", String.format("%.2f", deviceCost));
   
			input.put("GOODS_CODE", goodData.getString("RES_CODE"));//商品唯一标识
			input.put("GOODS_DESC", goodData.getString("GOODS_NAME"));
			input.put("BUSINESS_TYPE", "4".equals(goodData.getString("RES_TYPE_CODE"))?"1":"0");//0：非终端类 1：终端类

		}
 
        
        
        
	        
        return IBossCall.applyCreditPurchases(input);
    }
    
    
    
    /**
     * 套餐办理结果通知，调用iboss接口
     * by chenyw7
     */
    public IDataset replyCreditPurchases(IData params) throws Exception
    {
    	IData input = new DataMap();
    	String tradeId = params.getString("TRADE_ID");
    	String cusMblNo = params.getString("CUS_MBL_NO");
    	String seq = params.getString("SEQ");
    	String mplOrdNo = params.getString("MPL_ORD_NO");
    	String mplOrdDt = params.getString("MPL_ORD_DT");
    	
    	
    	
    	input.put("SEQ", seq);
    	input.put("CUS_MBL_NO", cusMblNo);
		input.put("MPL_ORD_NO", mplOrdNo);
		input.put("MPL_ORD_DT", mplOrdDt);
		input.put("ACP_TM", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		input.put("ACP_TYP","P");//操作类型 P:办理 R:取消
		IDataset goods=TradeSaleGoodsInfoQry.getTradeSaleGoodsByTradeId(tradeId);
		if(IDataUtil.isNotEmpty(goods)){
			input.put("GOODS_CODE",goods.getData(0).getString("RES_CODE"));
		}
	        
        return IBossCall.replyCreditPurchases(input);
    }
    
    
    
    
    public String productAmt(String  productId)throws Exception{
		String amt = "0";
		IData params = new DataMap();
		params.put(Route.ROUTE_EPARCHY_CODE, "0898");
		params.put("PRODUCT_ID", productId);
	    IDataset productAmts=CSAppCall.call("SS.CancelWholeNetCreditPurchasesSVC.getProductAmt", params);
	    if(IDataUtil.isNotEmpty(productAmts)){
	    	amt = productAmts.first().getString("AMT");
	    }
		return amt;
	}
    
	/**
     * REQ202005260002_关于开展5G招募活动的开发需求
     * 赠送积分、兑换话费
     * @param data
     * @return
     * @throws Exception
     */	
    public void giftActiveScore(IData params) throws Exception
    {
        SaleActiveBean saleActiveBean = BeanManager.createBean(SaleActiveBean.class);
        saleActiveBean.giftActiveScore(params);
    }
    
}
