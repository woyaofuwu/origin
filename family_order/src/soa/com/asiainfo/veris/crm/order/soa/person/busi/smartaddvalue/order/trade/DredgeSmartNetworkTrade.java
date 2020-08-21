package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.trade;



import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.intelligentnk.IntelligentNetworKingBean;
import com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.requestdata.DredgeSmartNetworkReqData;

public class DredgeSmartNetworkTrade extends BaseTrade implements ITrade{

	@Override
	public void createBusiTradeData(BusiTradeData bd) throws Exception {
					
    	createDredgeSmartNetwork(bd);
		
		//REQ201812070007  智能组网业务全网推广
		DredgeSmartNetworkReqData reqData = (DredgeSmartNetworkReqData) bd.getRD();

		String payNo = bd.getTradeId();//支付流水
    	   	
    	//1.新增智能组网预受理订单表
    	IData data = insertIntelligentnet(bd);
    	
    	//2.支付信息同步放在完工时 PayDredgeSmartNetworkAction
    	//syncPayInfo(bd,data,payNo);

    	//3.新增智能组网业务状态变换表数据
    	insertProcess(data);
    	
    	bd.getMainTradeData().setRsrvStr2(reqData.getFrameNetType());
    	bd.getMainTradeData().setRsrvStr3(StaticUtil.getStaticValue("FRAME_NET_TYPE", reqData.getFrameNetType()));
    	
    	
    	bd.getMainTradeData().setRsrvStr4(data.getString("NEW_LIST_NO"));

        	//01：CRM/BOSS  02：和家亲APP 03：网上营业厅 04：掌上营业厅 05：营业前台 09：其他
        	bd.getMainTradeData().setRsrvStr5("05");
        	//0：支付宝 1：微信 2:现金 3：其他 4：话费（只有省CRM侧支持话费支付）支付状态payState为1时必填
        	bd.getMainTradeData().setRsrvStr6("2");
        	bd.getMainTradeData().setRsrvStr7(payNo);

   	
    	bd.getMainTradeData().setRsrvStr8(bd.getMainTradeData().getOperFee());
    	bd.getMainTradeData().setRsrvStr9("100");
	}
	
	 private void createDredgeSmartNetwork(BusiTradeData btd) throws Exception{
		 
		 DredgeSmartNetworkReqData reqData = (DredgeSmartNetworkReqData) btd.getRD();
		 String UserId = btd.getRD().getUca().getUserId();
		 
		 if(StringUtils.isNotBlank(reqData.getFiistType())){ 	
			 IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",reqData.getFiistType(),null);
			 DiscntTradeData newDiscnt = new DiscntTradeData();
			 String discntCode = reqData.getFiistType();
			 
	         newDiscnt.setUserId(UserId);
	         newDiscnt.setProductId("-1");
	         newDiscnt.setPackageId("-1");
	         newDiscnt.setElementId(discntCode);
	         newDiscnt.setInstId(SeqMgr.getInstId());
	         newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
	         newDiscnt.setStartDate(SysDateMgr.getSysTime());
	         newDiscnt.setEndDate(SysDateMgr.END_DATE_FOREVER);
	         newDiscnt.setRsrvStr1(commparaInfos9211.getData(0).getString("PARA_CODE17"));//赠送的设备
			 newDiscnt.setRsrvStr2(btd.getTradeId());
	         newDiscnt.setRemark("智能组网增值产品开通绑定优惠");
	         System.out.println("createDredgeSmartNetwork--FIRST "+newDiscnt);
	         btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
	         
	         
	         if(StringUtils.isNotBlank(reqData.getSecondType())){
	        	 IDataset commparaInfo = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",reqData.getFiistType(),null);
				 DiscntTradeData newSecondDiscnt = new DiscntTradeData();
				 String secondDiscntCode = reqData.getSecondType();
				 
				 newSecondDiscnt.setUserId(UserId);
				 newSecondDiscnt.setProductId("-1");
				 newSecondDiscnt.setPackageId("-1");
				 newSecondDiscnt.setElementId(secondDiscntCode);
				 newSecondDiscnt.setInstId(SeqMgr.getInstId());
				 newSecondDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
				 newSecondDiscnt.setRsrvStr1(commparaInfo.getData(0).getString("PARA_CODE17"));//赠送的设备
				 newSecondDiscnt.setRsrvStr2(btd.getTradeId());
				 newSecondDiscnt.setStartDate(SysDateMgr.getSysTime());
				 newSecondDiscnt.setEndDate(SysDateMgr.END_DATE_FOREVER);
				 newSecondDiscnt.setRemark("智能组网增值产品开通绑定优惠");
		         System.out.println("createDredgeSmartNetwork--SECOND "+newDiscnt);
		         btd.add(btd.getRD().getUca().getSerialNumber(), newSecondDiscnt);
		         
			 }
	         
		 }
		 
		 if(StringUtils.isNotBlank(reqData.getThirdType())){
			 IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",reqData.getFiistType(),null);
			 DiscntTradeData newDiscnt = new DiscntTradeData();
			 String discntCode = reqData.getThirdType();
			 
	         newDiscnt.setUserId(UserId);
	         newDiscnt.setProductId("-1");
	         newDiscnt.setPackageId("-1");
	         newDiscnt.setElementId(discntCode);
	         newDiscnt.setInstId(SeqMgr.getInstId());
	         newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
	         newDiscnt.setRsrvStr1(commparaInfos9211.getData(0).getString("PARA_CODE17"));//赠送的设备
			 newDiscnt.setRsrvStr2(btd.getTradeId());
	         newDiscnt.setStartDate(SysDateMgr.getSysTime());
	         newDiscnt.setEndDate(SysDateMgr.END_DATE_FOREVER);
	         newDiscnt.setRemark("智能组网增值产品开通绑定优惠");
	         System.out.println("createDredgeSmartNetwork--Third "+newDiscnt);
	         btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
	         
	         
		 }
		 
	 }
	 	
		private void insertProcess(IData data) throws Exception
		 {
		    	IData processData = new DataMap();
		    	processData.put("NEW_LIST_NO", data.getString("NEW_LIST_NO"));
		    	processData.put("OPER_NUMB", data.getString("OPER_NUMB"));
		    	processData.put("OPER_TIME", data.getString("SYSDATE"));
		    	processData.put("BIZ_TYPE", data.getString("BIZ_TYPE"));
		    	//1：订单信息 2：工单信息 4：报结信息
		    	processData.put("LIST_STATE", "1");
		    	processData.put("BIZ_VERSION", data.getString("BIZ_VERSION"));
		    	
		    	Dao.insert("TI_INTELLIGENTNET_PROCESS", processData,Route.getJourDbDefault());			
		 }
	    
	    
		private IData insertIntelligentnet(BusiTradeData btd) throws Exception
		{
			DredgeSmartNetworkReqData reqData = (DredgeSmartNetworkReqData) btd.getRD();

			String sysDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
	    	IntelligentNetworKingBean bean = new IntelligentNetworKingBean();
	    	IData param = new DataMap();
	    	param.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
	    	param.put("SYSDATE", sysDate);
	    	
	    	IData data = bean.checkSnIsKdAcct(param);//校验手机号码
	    	
	    	String instId = SeqMgr.getInstId();
//	    	订单编码
//	    	格式为：3位省份代码(海南898)+时间（YYYYMMDDHHMMSS）+4位流水号
//	    	传给装维系统的
	    	//String newListNo = "898" + sysDate + instId.substring(instId.length() - 4, instId.length());
	    	String newListNo = reqData.getTradeId();
	    	
	    	if(StringUtils.isBlank(newListNo)){
	    		newListNo = btd.getTradeId();
	    	}
	    	
	    	param.put("TRADE_ID", reqData.getTradeId());
	    	
	    	if(StringUtils.isNotBlank(reqData.getAppOrderId()))
	    	{
	        	param.put("ORDER_ID", reqData.getAppOrderId());
	    	}
	    	else
	    	{
	    		param.put("ORDER_ID", newListNo);
	    	}
	    	
	    	if(StringUtils.isNotBlank(reqData.getOprNumb()))
	    	{
	        	param.put("OPER_NUMB", reqData.getOprNumb());
	    	}
	    	else
	    	{
	        	param.put("OPER_NUMB", "0214" + newListNo);
	    	}
	    	if(StringUtils.isNotBlank(reqData.getBizType()))
	    	{
	        	param.put("BIZ_TYPE", reqData.getBizType());
	    	}
	    	else
	    	{
	        	param.put("BIZ_TYPE", "77");
	    	}
	    	System.out.println("CUSTOMER_NAME标志"+btd.getRD().getUca().getCustomer().getCustName());
	    	if(StringUtils.isBlank(reqData.getUca().getCustomer().getCustName()))
	    	{
	    		param.put("CUSTOMER_NAME", btd.getRD().getUca().getCustId());
	    	}
	    	else
	    	{
	    		param.put("CUSTOMER_NAME", reqData.getUca().getCustomer().getCustName());
	    	}
	    	if(StringUtils.isNotEmpty(reqData.getContact_phone()))
	    	{
	    		param.put("ACCESS_NUM", reqData.getContact_phone());
	    	}
	    	else
	    	{
	    		param.put("ACCESS_NUM", btd.getRD().getUca().getSerialNumber());
	    	}
	    	param.put("CITY_CODE", reqData.getCity_code());
			String cityName = StringUtils.isBlank(reqData.getCity())?StaticUtil.getStaticValue("CITY", reqData.getCity_code()):reqData.getCity();
	    	param.put("CITY",cityName);
	    	IDataset commpara = CommparaInfoQry.getCommparaByAttrCode1("CSM","5774",reqData.getCounty_code(),"ZZZZ",null);
	    	String country = "-1";
	    	if(IDataUtil.isNotEmpty(commpara))
	    	{
	    		country = commpara.getData(0).getString("PARAM_NAME");
	    	}
	    	param.put("COUNTRY",country);
	    	param.put("COUNTRY_CODE", reqData.getCounty_code());
	    	param.put("DISTRICT_ADDR", reqData.getAddress());
	    	param.put("RESIDENTIAL_ZONE", reqData.getResidential_zone());

	    	String reserveDate = "";
	    	if(StringUtils.isNotBlank(reqData.getReserve_date()) )
	    	{
	    		if(reqData.getReserve_date().indexOf("-") >= 0)
	    		{
	    			reserveDate = SysDateMgr.decodeTimestamp(reqData.getReserve_date(),SysDateMgr.PATTERN_STAND_SHORT);
	    		}
	    		else
	    		{
	    			reserveDate =  reqData.getReserve_date();
	    		}
	    		
	    		//只取10位
	    		if(StringUtils.isNotBlank(reserveDate) && reserveDate.length() > 10)
	    		{
	    			reserveDate = reserveDate.substring(0, 10);
	    		}
	    	}

	    	param.put("RESERVE_DATE",reserveDate);

			if(StringUtils.isNotBlank(reqData.getHouse_type()))
			{
		    	param.put("HOUSE_TYPE", reqData.getHouse_type());
			}
			else
			{
		    	param.put("HOUSE_TYPE", StaticUtil.getStaticValue("HOUSE_TYPE", reqData.getHouse_type_code()));
			}

	    	param.put("HOUSE_TYPE_CODE", reqData.getHouse_type_code());
	    	param.put("AREA_SIZE", reqData.getArea_size());
	    	param.put("RECOMMEND_NUM", reqData.getRecommend_num());
	    	
	    	String channal = "";
	    	
	    	//接口由传值不做转换
//	    	01	CRM/BOSS （由系统发起）
//	    	05	手机客户端
//	    	06	网上营业厅
//	    	07	掌上营业厅
//	    	09	10086人工
//	    	10	10086IVR
//	    	11	营业前台

	    	if(StringUtils.isNotBlank(reqData.getChannal()))
	    	{
	    		channal = reqData.getChannal();
	    	}
	    	else
	    	{
	    		if("0".equals(CSBizBean.getVisit().getInModeCode()))
	    		{
	    			channal = "11";
	    		}
	    		else if("1".equals(CSBizBean.getVisit().getInModeCode()))
	    		{
	    			channal = "09";
	    		}else if("SD".equals(CSBizBean.getVisit().getInModeCode()))
	    		{
	    			channal = "11";
	    		}
	    	}
	    	param.put("CHANNAL", channal);
	    	param.put("WBAND_ACCOUNT", data.getString("ACCESS_ACCT"));
	    	param.put("NEW_LIST_NO", newListNo);
	    	
	    	param.put("SP_ID", reqData.getSPID());
	    	param.put("BIZ_CODE", reqData.getBizCode());
	    	param.put("CAMPAIGN_ID", reqData.getCampaign_id());
	    		    	
	    	param.put("BIZ_VERSION", reqData.getBizVersion());
	    	param.put("EPARCHY_CODE",reqData.getUca().getUserEparchyCode());
	    	param.put("CREATE_TIME",sysDate);

	    	Dao.insert("TF_B_INTELLIGENTNET", param,Route.getJourDbDefault());
	    	
	    	String  customerName = null;
	    	if(StringUtils.isBlank(reqData.getCust_name())){
	    		customerName = btd.getRD().getUca().getCustomer().getCustName();
	    	}else{
	    		customerName = reqData.getCust_name();
	    	}
			
	    	//IBossCall.订单信息同步
	      IBossCall.orderInfoSyn(param.getString("OPER_NUMB"), param.getString("BIZ_TYPE"), sysDate, "01", newListNo, 
	    			"1", sysDate, reqData.getBizVersion(), reqData.getAppOrderId(), reqData.getUca().getSerialNumber(), 
	    			cityName, reqData.getCity_code(), country, reqData.getCounty_code(), reqData.getAddress(), 
	    			reqData.getSPID(),reqData.getBizCode(), reqData.getCampaign_id(),  customerName, 
	    			param.getString("RESERVE_DATE"), param.getString("HOUSE_TYPE"), reqData.getHouse_type_code(), 
	    			reqData.getArea_size(), channal, param.getString("WBAND_ACCOUNT"));
	    	
	      return param;
		}
				 
}
