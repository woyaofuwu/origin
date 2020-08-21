package com.asiainfo.veris.crm.order.soa.person.busi.intelligentnk;


import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAccessAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.enterpriseinternettv.EnterpriseInternetTVBean;



import org.apache.log4j.Logger;


public class IntelligentNetworKingBean  extends CSBizBean
{
	private static final Logger logger = Logger.getLogger(IntelligentNetworKingBean.class);
	
	/**
     * 6.5.1	宽带账号查询
     * 服务使用方（一级组网）向服务提供方（省CRM）发起宽带账号查询请求，服务提供方将查询结果反馈至服务使用方
     * @param param
     * @return
     * @throws Exception
     */
    public static IData wbandInq(IData param) throws Exception
    {
    	IData returnData = new DataMap();        
    	IData returnData1 = new DataMap();        
    	IDataset returnList = new DatasetList(); 
    	
        String oprNumb = IDataUtil.chkParam(param, "OprNumb"); //发起方操作流水号
        //String BizType = IDataUtil.chkParam(param, "BizType"); //业务类型代码    77一级家庭开放平台业务
        String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER"); //使用服务的手机号        
        //String BizVersion = IDataUtil.chkParam(param, "BizVersion"); //业务版本号  
        String psptId = null;

        IData param1 = new DataMap();
        param1.put("SERIAL_NUMBER", "KD_"+serialNumber);
        param1.put("REMOVE_TAG", "0");
    	IDataset kd_userInfo1 = EnterpriseInternetTVBean.qrySelAllBySn(param1);
    	
    	/*if(IDataUtil.isEmpty(kd_userInfo1)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"KD_"+serialNumber+"的用户信息查询为空。");
		}*/
    	
    	IData custInfo;
    	IDataset custInfos;
    	IData widenetInfo;
    	IDataset widenetInfos;
    	if(IDataUtil.isNotEmpty(kd_userInfo1)){//serialNumber 查询
    		String kd_UserId1 = kd_userInfo1.getData(0).getString("USER_ID");
    		custInfos = CustomerInfoQry.getCustInfoByCustIdPk(kd_userInfo1.getData(0).getString("CUST_ID"));
    		if(IDataUtil.isEmpty(custInfos)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"KD_"+serialNumber+"的客户信息查询为空。");
    		}
    		custInfo=custInfos.getData(0);
    		
    		widenetInfos = UserAccessAcctInfoQry.qryWidenetByUserId(kd_UserId1);
    		if(IDataUtil.isEmpty(widenetInfos)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"KD_"+serialNumber+"的宽带信息查询为空。");
    		}
    		widenetInfo=widenetInfos.getData(0);
		
    	}else{
    		
    		psptId = IDataUtil.chkParam(param, "ID_CARD"); //身份证； 手机号码查不到宽带时必填
	        IData para = new DataMap();
	        para.put("IDCARDNUM", psptId);
	        
	        SQLParser parser = new SQLParser(para);
	        parser.addSQL(" SELECT * FROM TF_F_CUSTOMER C  WHERE   C.PSPT_ID=:IDCARDNUM and remove_tag='0' ");
	        IDataset customers = Dao.qryByParse(parser);
	        if(IDataUtil.isNotEmpty(customers)){
	        	for(int j=0;j<customers.size();j++){
	        		IDataset userInfos = UserInfoQry.getAllNormalUserInfoByCustId(customers.getData(j).getString("CUST_ID"));
    	        	if(IDataUtil.isNotEmpty(userInfos)){
    	        		for(int i=0;i<userInfos.size();i++){
    	        			if(userInfos.getData(i).getString("SERIAL_NUMBER").contains("KD"))
    	        			{
    	        				IData widenet = UserAccessAcctInfoQry.qryWidenetByUserId(userInfos.getData(i).getString("USER_ID")).first();
    	        				if(IDataUtil.isNotEmpty(widenet)){
    	        					IDataset citys = StaticUtil.getList(null, "TD_S_STATIC", "PDATA_ID", "DATA_NAME", new String[] {"TYPE_ID", "DATA_ID"},
    	        							new String[] {"CITY_AREA_CODE", widenet.getString("RSRV_STR4")});
    	        					IDataset cityName = StaticUtil.getStaticList("LC_CITY_ID",widenet.getString("RSRV_STR4"));

    	        					
    	        					IData returnData2 = new DataMap(); 
    	        					returnData2.put("wbandAccount", userInfos.getData(i).getString("SERIAL_NUMBER"));//宽带账号
    	        					returnData2.put("wbandPhone", userInfos.getData(i).getString("SERIAL_NUMBER").replace("KD_", ""));//宽带账号绑定的手机号
    	        					returnData2.put("provinceName", "海南");//宽带账号对应的省份名称
    	        					returnData2.put("provID", "898");//省公司代码详见附录7.6	
    	        					returnData2.put("customerName", customers.getData(j).getString("CUST_NAME"));//宽带账号对应的姓名
    	        					returnData2.put("CITY", citys.getData(0).getString("DATA_NAME"));//宽带账号对应的城市名称
    	        					returnData2.put("CITY_CODE",  citys.getData(0).getString("PDATA_ID"));//见附录7.7城市/区域编码表
    	        					IData areInfo = area(citys.getData(0).getString("PDATA_ID"),widenet.getString("DETAIL_ADDRESS"));
    	        					if(IDataUtil.isNotEmpty(areInfo)){
    	        						returnData2.put("COUNTRY", areInfo.getString("COUNTRY"));
    	        						returnData2.put("COUNTRY_CODE",  areInfo.getString("COUNTRY_CODE"));
    	        					}else{
    	        						returnData2.put("COUNTRY", citys.getData(0).getString("DATA_NAME"));//宽带账号对应的区/县
    	        						returnData2.put("COUNTRY_CODE",  citys.getData(0).getString("PDATA_ID"));//见附录7.7城市/区域编码表
    	        						
    	        						if (citys.getData(0).getString("PDATA_ID").startsWith("469"))
    	        						{
    	        							returnData2.put("CITY", "海南省省直辖县级行政区划");
    	        							returnData2.put("CITY_CODE",  "469000");
    	        						}
    	        					}		
    	        					returnData2.put("districtAddr",  widenet.getString("DETAIL_ADDRESS"));//宽带账号对应的用户详细地址
    	        					
    	        					returnList.add(returnData2);
    	        					
    	        					
    	        				}
    	        			}
        	        		
        	        	}
    	        	}
	        		
	        	}
	        	returnData.put("wbandList", returnList);
            	returnData.put("OprNumb", oprNumb);
            	returnData.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            	return returnData;
	        	
	        }
    		
    		//IDCard 查询
    		/*String IDCard = IDataUtil.chkParam(param, "IDCard"); //用户身份证号
    		 custInfos = CustomerInfoQry.getCustInfoByCustIdPk(IDCard);
    		 if(IDataUtil.isEmpty(custInfos)){
     			CSAppException.apperr(CrmCommException.CRM_COMM_103,"IDCard"+IDCard+"的客户信息查询为空。");
     		}
     		custInfo=custInfos.getData(0);
    		if(IDataUtil.isNotEmpty(custInfo)){
	    		IData param2 = new DataMap();
	            param2.put("CUST_ID", custInfo.getString("CUST_ID"));
	            param2.put("REMOVE_TAG", "0");
	        	IDataset kd_userInfo2 = EnterpriseInternetTVBean.qrySelAllBySn(param2);
	        	if(IDataUtil.isEmpty(kd_userInfo2)){
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"IDCard"+IDCard+"的用户信息查询为空。");
	    		}
	        	String kd_UserId2 = kd_userInfo2.getData(0).getString("USER_ID");	        	
	        	widenetInfos = UserAccessAcctInfoQry.qryWidenetByUserId(kd_UserId2);
	        	if(IDataUtil.isEmpty(widenetInfos)){
	    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"IDCard"+IDCard+"的宽带信息查询为空。");
	    		}
	        	widenetInfo=widenetInfos.getData(0);
	        		    			    			        	
    		}else{*/
    			returnData1.put("X_RESULTCODE", "0000");
        		returnData1.put("X_RESULTINFO", "没有找到宽带信息!");
        		
        		returnList.add(returnData1);
            	returnData.put("wbandList", returnList);
            	returnData.put("OprNumb", oprNumb);
            	returnData.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            	return returnData;
    		//}    		   		
    	}
    	
    	IDataset citys = StaticUtil.getList(null, "TD_S_STATIC", "PDATA_ID", "DATA_NAME", new String[] {"TYPE_ID", "DATA_ID"},
    	        							new String[] {"CITY_AREA_CODE",widenetInfo.getString("RSRV_STR4")});
    	
    	returnData1.put("wbandAccount", "KD_"+serialNumber);//宽带账号
		returnData1.put("wbandPhone", serialNumber);//宽带账号绑定的手机号
		returnData1.put("provinceName", "海南");//宽带账号对应的省份名称
		returnData1.put("provID", "898");//省公司代码详见附录7.6	
		returnData1.put("customerName", custInfo.getString("CUST_NAME"));//宽带账号对应的姓名
		returnData1.put("CITY", citys.getData(0).getString("DATA_NAME"));//宽带账号对应的城市名称
		returnData1.put("CITY_CODE",  citys.getData(0).getString("PDATA_ID"));//见附录7.7城市/区域编码表
		IData areInfo = area(citys.getData(0).getString("PDATA_ID"),widenetInfo.getString("DETAIL_ADDRESS"));
		if(IDataUtil.isNotEmpty(areInfo)){
			returnData1.put("COUNTRY", areInfo.getString("COUNTRY"));
			returnData1.put("COUNTRY_CODE",  areInfo.getString("COUNTRY_CODE"));
		}else{
			returnData1.put("COUNTRY", citys.getData(0).getString("DATA_NAME"));//宽带账号对应的区/县
			returnData1.put("COUNTRY_CODE",  citys.getData(0).getString("PDATA_ID"));//见附录7.7城市/区域编码表
			
			if (citys.getData(0).getString("PDATA_ID").startsWith("469"))
			{
				returnData1.put("CITY", "海南省省直辖县级行政区划");
				returnData1.put("CITY_CODE",  "469000");
			}
		}		
		
		returnData1.put("districtAddr",  widenetInfo.getString("DETAIL_ADDRESS"));//宽带账号对应的用户详细地址
		
		returnList.add(returnData1);
    	returnData.put("wbandList", returnList);
    	returnData.put("OprNumb", oprNumb);
    	returnData.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	return returnData;
    }
    
    public static IData area(String pdataId,String detailAddress){
    	
    	IData returnData1 = new DataMap();  
    	if("460100".equals(pdataId)){
			if(detailAddress.contains("秀英区")){
				returnData1.put("COUNTRY", "海南省海口市秀英区");
				returnData1.put("COUNTRY_CODE",  "460105");
			}else if(detailAddress.contains("龙华区")){
				returnData1.put("COUNTRY", "海南省海口市龙华区");
				returnData1.put("COUNTRY_CODE",  "460106");
			}else if(detailAddress.contains("琼山区")){
				returnData1.put("COUNTRY", "海南省海口市琼山区");
				returnData1.put("COUNTRY_CODE",  "460107");
			}else if(detailAddress.contains("美兰区")){
				returnData1.put("COUNTRY", "海南省海口市美兰区");
				returnData1.put("COUNTRY_CODE",  "460108");
			}else{
				returnData1.put("COUNTRY", "海南省海口市市辖区");
				returnData1.put("COUNTRY_CODE",  "460101");
			}
			return returnData1;
		}else if("460200".equals(pdataId)){
			
			if(detailAddress.contains("海棠区")){
				returnData1.put("COUNTRY", "海南省三亚市海棠区");
				returnData1.put("COUNTRY_CODE",  "460202");
			}else if(detailAddress.contains("吉阳区")){
				returnData1.put("COUNTRY", "海南省三亚市吉阳区");
				returnData1.put("COUNTRY_CODE",  "460203");
			}else if(detailAddress.contains("天涯区")){
				returnData1.put("COUNTRY", "海南省三亚市天涯区");
				returnData1.put("COUNTRY_CODE",  "460204");
			}else if(detailAddress.contains("崖州区")){
				returnData1.put("COUNTRY", "海南省三亚市崖州区");
				returnData1.put("COUNTRY_CODE",  "460205");
			}else{
				returnData1.put("COUNTRY", "海南省三亚市市辖区");
				returnData1.put("COUNTRY_CODE",  "460201");
			}
			return returnData1;
	}else{
		return null;
	}
    	
    }
 
	
	/**
     * 6.5.2  智能组网预装订单信息受理
     * 服务使用方向服务提供方发起预装订单信息受理请求，服务提供方将受理结果反馈至服务使用方
     * @param data
     * @return  
     * @throws Exception
     */
    public IData orderBizOrder(IData data) throws Exception
    {
        String oprNumb = IDataUtil.chkParam(data, "OprNumb");
        String oprTime = IDataUtil.chkParam(data, "OprTime");
    	String bizType = IDataUtil.chkParam(data, "BizType");
    	String orderId = IDataUtil.chkParam(data, "orderId");
    	String bizVersion = data.getString("BizVersion");
    	
    	String   dataInfoStr  = IDataUtil.chkParam(data, "DATA_INFO");
    	String   orderDetailStr = IDataUtil.chkParam(data, "ORDER_DETAIL");
    	String SPID = "";
    	String bizCode = "";
    	String campaignId = "";
    	String IDV = "";
    	String city = "";
    	String cityCode = "";
    	String countryCode = "";
    	String country = "";
    	String districtAddr = "";
    	String reserveDate = "";
    	String houseType = "";
    	String houseTypeCode = "";
    	String areaSize = "";
    	String recommendNum = "";
    	String channel = "";
    	String wbandAccount = "";

    	IDataset dataInfo = new DatasetList(dataInfoStr);


		if(IDataUtil.isNotEmpty(dataInfo))
    	{
        	SPID = IDataUtil.chkParam(dataInfo.getData(0), "SPID");
        	bizCode = IDataUtil.chkParam(dataInfo.getData(0), "BizCode");
        	campaignId = IDataUtil.chkParam(dataInfo.getData(0), "CAMPAIGN_ID");
    	}
    	else
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"局数据信息不能为空。");
    	}
    	//IData orderDetail = new DataMap(orderDetailStr);
		IDataset orderDetail = new DatasetList(orderDetailStr);


		if(IDataUtil.isNotEmpty(orderDetail))
    	{
        	 IDV = IDataUtil.chkParam(orderDetail.getData(0), "IDV");
        	 city = IDataUtil.chkParam(orderDetail.getData(0),"city");
        	 cityCode = IDataUtil.chkParam(orderDetail.getData(0), "cityCode");
        	 countryCode =IDataUtil.chkParam(orderDetail.getData(0), "countryCode");
        	 country = IDataUtil.chkParam(orderDetail.getData(0),"country");
        	 districtAddr = orderDetail.getData(0).getString("districtAddr");
        	 reserveDate = orderDetail.getData(0).getString("reserveDate");
        	 houseType = orderDetail.getData(0).getString("houseType");
        	 houseTypeCode = orderDetail.getData(0).getString("houseTypeCode");
        	 areaSize = orderDetail.getData(0).getString("areaSize");
        	 recommendNum = orderDetail.getData(0).getString("recommendNum");
        	 channel = IDataUtil.chkParam(orderDetail.getData(0), "channel");
        	 wbandAccount = IDataUtil.chkParam(orderDetail.getData(0), "wbandAccount");

    	}
    	else
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"预装订单信息不能为空。");
    	}
    	
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", IDV);
    	param.put("BizType", bizType);
    	param.put("ORDER_ID", orderId);
    	param.put("OprNumb", oprNumb);
    	param.put("CONTACT_PHONE", IDV);
    	param.put("SPID", SPID);
    	param.put("BIZ_CODE", bizCode);
    	param.put("CAMPAIGN_ID", campaignId);
    	param.put("BizVersion", bizVersion);
    	param.put("CITY_CODE", cityCode);
    	param.put("COUNTY_CODE", countryCode);
    	param.put("RESERVE_DATE", reserveDate);
    	param.put("ADDRESS", districtAddr);
    	param.put("HOUSE_TYPE", houseType);
    	param.put("HOUSE_TYPE_CODE", houseTypeCode);
    	param.put("AREA_SIZE", areaSize);
    	param.put("RECOMMEND_NUM", recommendNum);
    	param.put("CHANNAL", channel);
    	   	
    	IDataset DataInfo = CommparaInfoQry.getCommparaByCode1to3("CSM","9387",SPID,campaignId,bizCode,"1","ZZZZ");
    	if(IDataUtil.isEmpty(DataInfo)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"SPID:"+SPID+",CAMPAIGN_ID:"+campaignId+",BIZ_CODE:"+bizCode+"的查询为空。");
		}
    	param.put("FIRST_TYPE",  DataInfo.getData(0).getString("PARA_CODE4"));
    	param.put("SECOND_TYPE", DataInfo.getData(0).getString("PARA_CODE5"));
    	param.put("THIRD_TYPE",  DataInfo.getData(0).getString("PARA_CODE6"));

    	IData returnData = new DataMap();
    	returnData.put("OPR_NUMB", oprNumb);
		returnData.put("OPR_TIME", oprTime);
		     		
		/*//登记台账前费用校验
		IData feeResult = CSAppCall.callOne("SS.DredgeSmartNetworkIntfSVC.checkFeeBeforeSubmit", param);
		if(feeResult.getString("X_RESULTCODE").equals("61314"))
    	{
			BigDecimal fee =new BigDecimal(0) ;
			IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",null,null);
        	if(IDataUtil.isNotEmpty(commparaInfos9211)){
        		
        		for(int i=0;i<commparaInfos9211.size();i++){
        			
        			if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(param.getString("FIRST_TYPE"))){
				    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4")))
				    	{
				    	   fee=new BigDecimal(commparaInfos9211.getData(i).getString("PARA_CODE4")).add(fee);				    	  
				    	}
				    }
				    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(param.getString("SECOND_TYPE"))){
				    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4")))
				    	{
				    		fee=new BigDecimal(commparaInfos9211.getData(i).getString("PARA_CODE4")).add(fee);				    	
				    	}
				    }
                    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(param.getString("THIRD_TYPE"))){
                    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4")))
				    	{
                    		fee=new BigDecimal(commparaInfos9211.getData(i).getString("PARA_CODE4")).add(fee);                    	
				    	}
				    }
        		}
        	}	
			 if (logger.isDebugEnabled()){
		         	logger.debug("FIRST_TYPE:"+param.getString("FIRST_TYPE"));
		         	logger.debug("SECOND_TYPE:"+param.getString("SECOND_TYPE"));
		         	logger.debug("THIRD_TYPE:"+param.getString("THIRD_TYPE"));
		         	logger.debug("SERIAL_NUMBER:"+param.getString("SERIAL_NUMBER"));
		         	logger.debug("leftFee:"+WideNetUtil.qryBalanceDepositBySn(param.getString("SERIAL_NUMBER")));
		         	logger.debug("fee:"+fee.toString());
		         }
    		returnData.put("BIZ_ORDER_RESULT", "61314");
    		returnData.put("RESULT_DESC", feeResult.getString("X_RESULTINFO"));
    		return returnData;
    	}*/
		
    	//IDataset ids = CSAppCall.call("SS.IntelligentNetworKingRegSVC.tradeReg", param);
		try{
			IDataset ids = CSAppCall.call("SS.DredgeSmartNetworkRegSVC.tradeReg", param);
			if(IDataUtil.isNotEmpty(ids))
	    	{
	    		returnData.put("listState", "1");
	    		returnData.put("BIZ_ORDER_RESULT", "0000");
	    		returnData.put("RESULT_DESC", "智能组网订单成功");
	    	}
	    	else
	    	{
	    		returnData.put("BIZ_ORDER_RESULT", "2000");
	    		returnData.put("RESULT_DESC", "智能组网订单失败");
	    	}
			
		}catch(Exception e){
			if(e.toString().contains("余额不足")){
				returnData.put("BIZ_ORDER_RESULT", "2999");
	    		returnData.put("RESULT_DESC", "用户余额不足,智能组网订单失败");
			}else{
				returnData.put("BIZ_ORDER_RESULT", "2999");
	    		returnData.put("RESULT_DESC", e.toString());
			}
		}
    	   	
    	
    	return returnData;
    }
    
    /**
     * 6.5.4    工单信息同步
     * 省CRM平台收到用户订购请求后生成工单并派给装维人员时，将工单信息实时同步给一级组网业务平台
     * @param data
     * @throws Exception
     */
    public IData listInfoSyn(IData data) throws Exception
    {
    	 String oprNumb = IDataUtil.chkParam(data, "OPER_NUMB"); 
         String oprTime = IDataUtil.chkParam(data, "OPER_TIME"); 
         String bizType = IDataUtil.chkParam(data, "BIZ_TYPE"); 
         String newListNo = IDataUtil.chkParam(data, "NEW_LIST_NO"); 
         String workListNo = IDataUtil.chkParam(data, "WORK_LIST_NO"); 
         String staffName = data.getString("STAFF_NAME");
         String staffPhone = data.getString("STAFF_PHONE"); 
         String listAccTime = data.getString("LIST_ACC_TIME"); 
         String goHomeTime = data.getString("GO_HOME_TIME"); 
         String listState = IDataUtil.chkParam(data, "LIST_STATE"); 
         String bizVersion =  data.getString("BIZ_VERSION");
         String routeId = getEparchyCodeByNo(newListNo);

         if(listAccTime!=null && listAccTime.length() >=10)
         {
        	 listAccTime = SysDateMgr.decodeTimestamp(listAccTime,SysDateMgr.PATTERN_STAND_SHORT);
         }
         
         if(goHomeTime!=null && goHomeTime.length() >=10)
         {
        	 goHomeTime = goHomeTime.substring(0, 10);
         }
         
     	IData param = new DataMap();
     	param.put("OPER_NUMB", oprNumb);
     	param.put("OPER_TIME", oprTime);
     	param.put("BIZ_TYPE", bizType);
     	param.put("NEW_LIST_NO", newListNo);
     	param.put("WORK_LIST_NO", workListNo);
     	param.put("STAFF_NAME", staffName);
     	param.put("STAFF_PHONE", staffPhone);
     	param.put("LIST_ACC_TIME", listAccTime);
     	param.put("GO_HOME_TIME", goHomeTime);
     	param.put("LIST_STATE", listState);
     	param.put("BIZ_VERSION", bizVersion);
     	param.put("WORK_LIST_UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

     	Dao.insert("TI_INTELLIGENTNET_WORK", param,Route.getJourDb(routeId));
     	
     	//2.修改智能组网业务状态变换表  状态
     	IData input = new DataMap();
     	input.put("NEW_LIST_NO", newListNo);
     	input.put("LIST_STATE", "2");
     	input.put("OPER_NUMB", oprNumb);
     	input.put("INTERFACE_TYPE", "0");
     	updateProcess(input);
     	
    	
     	IData returnData = new DataMap();

    	//IBossCall.工单信息同步
     	IDataset ids = IBossCall.listInfoSyn(oprNumb, bizType, newListNo, workListNo, staffName, staffPhone, SysDateMgr.getSysDateYYYYMMDDHHMMSS(), oprTime, listAccTime, goHomeTime, "2", bizVersion);

    	if(IDataUtil.isNotEmpty(ids))
    	{
    		returnData.put("BIZ_ORDER_RESULT", "0000");
    		returnData.put("RESULT_DESC", "工单信息同步成功");
    	}
    	else
    	{
    		returnData.put("BIZ_ORDER_RESULT", "3029");
    		returnData.put("RESULT_DESC", "工单信息同步失败");
    	}
    	
     	return returnData;
    }
    
    /**
     * 6.5.5     支付信息同步
     * 服务使用方向服务提供方进行支付信息同步
     * @param data
     * @return
     * @throws Exception
     */
    public IData payInfoSyn(IData data) throws Exception
    {
        String oprNumb = IDataUtil.chkParam(data, "OprNumb"); 
        String oprTime = IDataUtil.chkParam(data, "OprTime"); 
        String bizType = IDataUtil.chkParam(data, "BizType"); 
        String payAmount = IDataUtil.chkParam(data, "payAmount"); 

        String newListNo = IDataUtil.chkParam(data, "newListNo"); 
        String listState = IDataUtil.chkParam(data, "listState"); 
        String workListNo = data.getString("workListNo","");
        String payState = IDataUtil.chkParam(data, "payState"); 
        String payType = data.getString("payType");
        String payChannel = data.getString("payChannel"); 
        String payNo = data.getString("payNo"); 
        String payTime = data.getString("payTime"); 
        String SPID = data.getString("SPID"); 
        String bizCode = data.getString("BizCode");
        String campaignId = data.getString("CAMPAIGN_ID"); 
        String bizVersion = data.getString("BizVersion"); 
        String totalPrice = IDataUtil.chkParam(data,"totalPrice"); 
        String totalDiscount = IDataUtil.chkParam(data,"totalDiscount"); 
        String realTotalPrice = IDataUtil.chkParam(data,"realTotalPrice"); 

        String routeId = getEparchyCodeByNo(newListNo);
        
        String payDetailsStr = data.getString("PAY_DETAILS");//payDetails
        
        IData payDetails = null;
        
        if(StringUtils.isNotBlank(payDetailsStr))
        {
        	payDetails = new DataMap(payDetailsStr);
        }

    	//1.插智能组网支付信息表
    	IData param = new DataMap();
    	param.put("OPER_NUMB", oprNumb);
    	param.put("OPER_TIME", oprTime);
    	param.put("BIZ_TYPE", bizType);
    	param.put("NEW_LIST_NO", newListNo);
    	param.put("LIST_STATE", listState);
    	param.put("DATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	param.put("WORK_LIST_NO", workListNo);
    	param.put("PAY_AMOUNT", payAmount);
    	param.put("PAY_STATE", payState);
    	param.put("PAY_TYPE", payType);
    	param.put("PAY_CHANNEL", payChannel);
    	param.put("PAY_NO", payNo);
    	param.put("PAY_TIME", payTime);
    	param.put("BIZ_VERSION", bizVersion);
    	param.put("SP_ID", SPID);
    	param.put("BIZ_CODE",bizCode);
    	param.put("CAMPAIGN_ID", campaignId);
    	param.put("TOTAL_PRICE", totalPrice);
    	param.put("TOTAL_DISCOUNT", totalDiscount);
    	param.put("REAL_TOTAL_PRICE",realTotalPrice);
    	
    	StringBuilder sql = new StringBuilder();
    	sql.append(" UPDATE TI_INTELLIGENTNET_PAY T");
    	sql.append(" SET T.OPER_NUMB = :OPER_NUMB,");
    	sql.append("  T.OPER_TIME = :OPER_TIME,");
    	sql.append("  T.BIZ_TYPE = :BIZ_TYPE,");
    	sql.append("  T.LIST_STATE = :LIST_STATE,");
    	sql.append("  T.DATE_TIME = :DATE_TIME,");
    	sql.append("  T.WORK_LIST_NO = :WORK_LIST_NO,");
    	sql.append("  T.PAY_AMOUNT = :PAY_AMOUNT,");
    	sql.append("  T.PAY_STATE = :PAY_STATE,");
    	sql.append("  T.PAY_TYPE = :PAY_TYPE,");
    	sql.append("  T.PAY_CHANNEL = :PAY_CHANNEL,");
    	sql.append("  T.PAY_NO = :PAY_NO,");
    	sql.append("  T.PAY_TIME = :PAY_TIME,");
    	sql.append("  T.SP_ID = :SP_ID,");
    	sql.append("  T.BIZ_CODE = :BIZ_CODE,");
    	sql.append("  T.CAMPAIGN_ID = :CAMPAIGN_ID,");
    	sql.append("  T.TOTAL_PRICE = :TOTAL_PRICE,");
    	sql.append("  T.TOTAL_DISCOUNT = :TOTAL_DISCOUNT,");
    	sql.append("  T.REAL_TOTAL_PRICE = :REAL_TOTAL_PRICE,");
    	sql.append("  T.BIZ_VERSION = :BIZ_VERSION");
    	sql.append(" WHERE NEW_LIST_NO = :NEW_LIST_NO");
       
    	IData returnData = new DataMap();
    	returnData.put("OprNumb", oprNumb);
    	returnData.put("OprTime", oprTime);
    	returnData.put("BizOrderResult", "0000");
    	returnData.put("ResultDesc", "支付信息同步成功");
    	
    	try{
    	   Dao.executeUpdate(sql, param,Route.getJourDb(routeId));
        }catch(Exception e){
        	returnData.put("BizOrderResult", "2000");
    		returnData.put("ResultDesc", "支付信息同步失败");
        }
    	//2.修改智能组网业务状态变换表  状态
    	IData input = new DataMap();
    	input.put("NEW_LIST_NO", newListNo);
    	input.put("LIST_STATE", "3");
    	input.put("OPER_NUMB", oprNumb);
    	updateProcess(input);
    	       	   				
    	//IBossCall.支付信息同步
    	/*IDataset ids = IBossCall.payInfoSyn(oprNumb, bizType, oprTime, newListNo, listState, workListNo, payState, payType, payChannel, payNo, payTime, totalPrice, totalDiscount,
    			realTotalPrice, payAmount, data.getString("remark",""), SPID, bizCode, campaignId, bizVersion,null);
		
    	if(IDataUtil.isNotEmpty(ids))
    	{
    		returnData.put("BizOrderResult", "0000");
    		returnData.put("ResultDesc", "支付信息同步成功");
    	}
    	else
    	{
    		returnData.put("BizOrderResult", "2000");
    		returnData.put("ResultDesc", "支付信息同步失败");
    	}*/
    	
    	return returnData;
    	 	
    }
   
    private String getEparchyCodeByNo(String newListNo) throws Exception
    {
    	String eparchycode = BizRoute.getTradeEparchyCode();
//    	IData data = getOrderInfo(newListNo);
//    	
//    	if(IDataUtil.isNotEmpty(data))
//    	{
//    		eparchycode = data.getString("EPARCHY_CODE");
//    	}
//    	else
//    	{
//    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单编码【"+newListNo+"】查询不到有效订单信息！");
//    	}
    	
    	return eparchycode;
    }
    
    public IData getOrderInfo(String newListNo) throws Exception
    {
    	IData input = new DataMap();
    	input.put("NEW_LIST_NO", newListNo);
		IDataset ids = Dao.qryByCodeAllJour("TF_B_INTELLIGENTNET", "SEL_BY_ID", input, false);

		IData returnData = new DataMap();
		
		if(IDataUtil.isNotEmpty(ids))
		{
			returnData = ids.getData(0);
		}
		
		return returnData;
    }
        
    /**
     * 修改智能组网业务状态变换表  
     * @param data
     * @return
     * @throws Exception
     */
    public void updateProcess(IData data) throws Exception
    {
        String newListNo = IDataUtil.chkParam(data, "NEW_LIST_NO"); 
        String listState = IDataUtil.chkParam(data, "LIST_STATE"); 
        String operNumb = IDataUtil.chkParam(data, "OPER_NUMB");
        String routeId = BizRoute.getTradeEparchyCode();
        
    	IData param = new DataMap();
    	param.put("NEW_LIST_NO", newListNo);
    	param.put("OPER_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	param.put("LIST_STATE",listState);
    	param.put("OPER_NUMB",operNumb);
    	
    	StringBuilder sql = new StringBuilder();
    	sql.append(" UPDATE TI_INTELLIGENTNET_PROCESS T");
    	sql.append(" SET T.OPER_TIME = :OPER_TIME,");
    	sql.append("  T.LIST_STATE = :LIST_STATE,");
    	sql.append("  T.OPER_NUMB = :OPER_NUMB");
    	sql.append(" WHERE NEW_LIST_NO = :NEW_LIST_NO");
        Dao.executeUpdate(sql, param,Route.getJourDb(routeId));
    } 
    
    public IData checkSnIsKdAcct(IData param) throws Exception
    {
    	IData returnData = new DataMap();
    	String serialNumber = param.getString("SERIAL_NUMBER");
    	
    	IData param1 = new DataMap();
        param1.put("SERIAL_NUMBER", "KD_"+serialNumber);
        param1.put("REMOVE_TAG", "0");
    	IDataset kd_userInfo1 = EnterpriseInternetTVBean.qrySelAllBySn(param1);
    	   	
    	IDataset widenetInfos;
    	if(IDataUtil.isNotEmpty(kd_userInfo1)){//serialNumber 查询
    		String kd_UserId1 = kd_userInfo1.getData(0).getString("USER_ID");  		
    		widenetInfos = UserAccessAcctInfoQry.qryWidenetByUserId(kd_UserId1);
    		if(IDataUtil.isEmpty(widenetInfos)){
    			//CSAppException.apperr(CrmCommException.CRM_COMM_103,"KD_"+serialNumber+"的宽带信息查询为空。");
    			returnData.put("ACCESS_ACCT", "");
    		}  
    		returnData.put("ACCESS_ACCT", "KD_"+serialNumber);   	  	
    	}else{
    		//CSAppException.apperr(CrmCommException.CRM_COMM_103,"KD_"+serialNumber+"的用户信息查询为空。");
    		returnData.put("ACCESS_ACCT", "");
    	}
    	
    	return returnData;     	
    }
    
    /**
     * 报结信息同步（通过UIP调）
     * 装维管理平台完成组网安装后，生成报结信息（含评测报告）同步给省CRM，省CRM通过网状网同步报结信息给一级组网业务平台
     * @param data
     * @return
     * @throws Exception
     */
    public IData evaInfoSyn(IData data) throws Exception
    {
    	String oprNumb = IDataUtil.chkParam(data, "OPER_NUMB"); 
        String oprTime = IDataUtil.chkParam(data, "OPER_TIME"); 
        String bizType = IDataUtil.chkParam(data, "BIZ_TYPE"); 
        String workListNo = IDataUtil.chkParam(data, "WORK_LIST_NO"); 
        String newListNo = IDataUtil.chkParam(data, "NEW_LIST_NO"); 
        String preCoveragePercent = data.getString("PRECOVERAGE_PERCENT");
        String coveragePercent = data.getString("COVERAGE_PERCENT");
        String installEndTime = IDataUtil.chkParam(data, "INSTALL_END_TIME"); 
        String listState = IDataUtil.chkParam(data, "LIST_STATE"); 
        String bizVersion = data.getString("BIZ_VERSION"); 
        String networkingId = data.getString("NET_WORKING_ID"); 

        //String routeId = getEparchyCodeByNo(newListNo);
        
      //3.
    	IData returnData = new DataMap();
    	returnData.put("OPER_NUMB", oprNumb);
		returnData.put("OPER_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        String routeId =null;
        try{
            routeId = getEparchyCodeByNo(newListNo);
        }catch (Exception e) {
        	returnData.put("BIZ_ORDER_RESULT", "2001");
    		returnData.put("RESULT_DESC", e.toString());
    		return returnData;
		}
        
//        IDataset networkingPositionListBefore = data.getDataset("NETWORKING_POSITION_LIST_BEFORE"); 
//        IDataset networkingEquipmentListBefore = data.getDataset("NETWORKING_EQUIPMENT_LIST_BEFORE"); 
//        IDataset networkingPositionListAfter = data.getDataset("NETWORKING_POSITION_LIST_AFTER"); 
//        IDataset networkingEquipmentListAfter = data.getDataset("NETWORKING_EQUIPMENT_LIST_AFTER"); 
//
//        IDataset zwImageInfo = data.getDataset("ZW_IMAGE_INFO"); 
//        IDataset evascoreInfo = data.getDataset("EVA_SCORE_INFO"); 

    	
        IData param = new DataMap();
        param.put("OPER_NUMB", oprNumb);
    	param.put("OPER_TIME", oprTime);
    	param.put("BIZ_TYPE", bizType);
    	param.put("NEW_LIST_NO", newListNo);
    	param.put("WORK_LIST_NO", workListNo);
    	param.put("PRECOVERAGE_PERCENT", preCoveragePercent);
    	param.put("COVERAGE_PERCENT", coveragePercent);
    	param.put("INSTALL_END_TIME", installEndTime);
    	param.put("LIST_STATE", listState);
    	param.put("BIZ_VERSION", bizVersion);
    	param.put("NET_WORKING_ID", networkingId);
    	
    	Dao.insert("TI_INTELLIGENTNET_EVALUATE", param,Route.getJourDb(routeId));

    	
//    	if(IDataUtil.isNotEmpty(networkingPositionListBefore))
//    	{
//    		param.clear();
//			param.put("NEW_LIST_NO", newListNo);
//			param.put("POSITION_TYPE", "0");
//			
//    		for(int i = 0; i < networkingPositionListBefore.size(); i++)
//    		{
//    			param.put("DOWNLOADSPEED", networkingPositionListBefore.getData(i).getString("downloadSpeed"));
//    			param.put("XDCHANNEL", networkingPositionListBefore.getData(i).getString("xdchannel"));
//    			param.put("BANDWIDTH", networkingPositionListBefore.getData(i).getString("bandwidth"));
//    			param.put("FIELDSTRENGTH", networkingPositionListBefore.getData(i).getString("fieldStrength"));
//    			param.put("POSITION", networkingPositionListBefore.getData(i).getString("position"));
//    			param.put("FREQUENCY", networkingPositionListBefore.getData(i).getString("frequency"));
//    			param.put("UPDATETIME", networkingPositionListBefore.getData(i).getString("updateTime"));
//    			param.put("ACCESSPOINT", networkingPositionListBefore.getData(i).getString("accessPoint"));
//    			param.put("MAC", networkingPositionListBefore.getData(i).getString("mac"));
//    			param.put("FSJUDGE", networkingPositionListBefore.getData(i).getString("fsJudge"));
//    			param.put("PINGDELAY", networkingPositionListBefore.getData(i).getString("pingDelay"));
//    			param.put("PDJUDGE", networkingPositionListBefore.getData(i).getString("pdJudge"));
//    			param.put("PACKAGELOSS", networkingPositionListBefore.getData(i).getString("packageLoss"));
//    			param.put("PLJUDGE", networkingPositionListBefore.getData(i).getString("plJudge"));
//    			param.put("CIJUDGE", networkingPositionListBefore.getData(i).getString("ciJudge"));
//    			param.put("LINETYPE", networkingPositionListBefore.getData(i).getString("lineType"));
//
//    	    	Dao.insert("TI_INTELLIGENTNET_POSITION", param,Route.getJourDb(routeId));
//    		}
//    	}
//    	if(IDataUtil.isNotEmpty(networkingEquipmentListBefore))
//    	{
//    		param.clear();
//			param.put("NEW_LIST_NO", newListNo);
//			param.put("EQUIPMENT_TYPE", "0");
//			
//    		for(int i = 0; i < networkingEquipmentListBefore.size(); i++)
//    		{
//    			param.put("UPDATETIME", networkingEquipmentListBefore.getData(i).getString("updateTime"));
//    			param.put("POSITION", networkingEquipmentListBefore.getData(i).getString("position"));
//    			param.put("EQUIPMENTNAME", networkingEquipmentListBefore.getData(i).getString("equipmentName"));
//    			param.put("FREQUENCYRANGE", networkingEquipmentListBefore.getData(i).getString("frequencyRange"));
//    			param.put("SN", networkingEquipmentListBefore.getData(i).getString("sn"));
//    			param.put("DEVICETYPE", networkingEquipmentListBefore.getData(i).getString("deviceType"));
//    			param.put("MODEL", networkingEquipmentListBefore.getData(i).getString("model"));
//    			
//    	    	Dao.insert("TI_INTELLIGENTNET_EQUIPMENT", param,Route.getJourDb(routeId));
//    		}
//    	}
//    	if(IDataUtil.isNotEmpty(networkingPositionListAfter))
//    	{
//    		param.clear();
//			param.put("NEW_LIST_NO", newListNo);
//			param.put("POSITION_TYPE", "1");
//			
//    		for(int i = 0; i < networkingPositionListAfter.size(); i++)
//    		{
//    			param.put("DOWNLOADSPEED", networkingPositionListAfter.getData(i).getString("downloadSpeed"));
//    			param.put("XDCHANNEL", networkingPositionListAfter.getData(i).getString("xdchannel"));
//    			param.put("BANDWIDTH", networkingPositionListAfter.getData(i).getString("bandwidth"));
//    			param.put("FIELDSTRENGTH", networkingPositionListAfter.getData(i).getString("fieldStrength"));
//    			param.put("POSITION", networkingPositionListAfter.getData(i).getString("position"));
//    			param.put("FREQUENCY", networkingPositionListAfter.getData(i).getString("frequency"));
//    			param.put("UPDATETIME", networkingPositionListAfter.getData(i).getString("updateTime"));
//    			param.put("ACCESSPOINT", networkingPositionListAfter.getData(i).getString("accessPoint"));
//    			param.put("MAC", networkingPositionListAfter.getData(i).getString("mac"));
//    			param.put("FSJUDGE", networkingPositionListAfter.getData(i).getString("fsJudge"));
//    			param.put("PINGDELAY", networkingPositionListAfter.getData(i).getString("pingDelay"));
//    			param.put("PDJUDGE", networkingPositionListAfter.getData(i).getString("pdJudge"));
//    			param.put("PACKAGELOSS", networkingPositionListAfter.getData(i).getString("packageLoss"));
//    			param.put("PLJUDGE", networkingPositionListAfter.getData(i).getString("plJudge"));
//    			param.put("CIJUDGE", networkingPositionListAfter.getData(i).getString("ciJudge"));
//    			param.put("LINETYPE", networkingPositionListAfter.getData(i).getString("lineType"));
//
//    	    	Dao.insert("TI_INTELLIGENTNET_POSITION", param,Route.getJourDb(routeId));
//    		}
//    	
//    	}
//    	if(IDataUtil.isNotEmpty(networkingEquipmentListAfter))
//    	{
//    		param.clear();
//			param.put("NEW_LIST_NO", newListNo);
//			param.put("EQUIPMENT_TYPE", "1");
//			
//    		for(int i = 0; i < networkingEquipmentListAfter.size(); i++)
//    		{
//    			param.put("UPDATETIME", networkingEquipmentListAfter.getData(i).getString("updateTime"));
//    			param.put("POSITION", networkingEquipmentListAfter.getData(i).getString("position"));
//    			param.put("EQUIPMENTNAME", networkingEquipmentListAfter.getData(i).getString("equipmentName"));
//    			param.put("FREQUENCYRANGE", networkingEquipmentListAfter.getData(i).getString("frequencyRange"));
//    			param.put("SN", networkingEquipmentListAfter.getData(i).getString("sn"));
//    			param.put("DEVICETYPE", networkingEquipmentListAfter.getData(i).getString("deviceType"));
//    			param.put("MODEL", networkingEquipmentListAfter.getData(i).getString("model"));
//    			
//    	    	Dao.insert("TI_INTELLIGENTNET_EQUIPMENT", param,Route.getJourDb(routeId));
//    		}
//    	}
//    	if(IDataUtil.isNotEmpty(zwImageInfo))
//    	{
//    		param.clear();
//			param.put("NEW_LIST_NO", newListNo);
//    		for(int i = 0; i < zwImageInfo.size(); i++)
//    		{
//    			param.put("LAYOUTIMAGE_URL", zwImageInfo.getData(i).getString("layoutImageUrl"));
//    			param.put("PRENETWORKINGIMAGE_URL", zwImageInfo.getData(i).getString("preNetworkingImageUrl"));
//    			param.put("NETWORKINGIMAGE_URL", zwImageInfo.getData(i).getString("networkingImageUrl"));
//    			param.put("PRECOVERAGEIMAGE_URL", zwImageInfo.getData(i).getString("preCoverageImageUrl"));
//    			param.put("COVERAGEIMAGE_URL", zwImageInfo.getData(i).getString("coverageImageUrl"));
//    			
//    	    	Dao.insert("TI_INTELLIGENTNET_ZWIMAGE", param,Route.getJourDb(routeId));
//    		}
//    	
//    	}
//    	if(IDataUtil.isNotEmpty(evascoreInfo))
//    	{
//    		param.clear();
//			param.put("NEW_LIST_NO", newListNo);
//    		for(int i = 0; i < evascoreInfo.size(); i++)
//    		{
//    			param.put("PRE_SCORE", evascoreInfo.getData(i).getString("preScore"));
//    			param.put("SCORE", evascoreInfo.getData(i).getString("score"));
//    			param.put("PRECOVERAGE_SCORE", evascoreInfo.getData(i).getString("preCoverageScore"));
//    			param.put("COVERAGE_SCORE", evascoreInfo.getData(i).getString("coverageScore"));
//    			param.put("PREBANDWIDTH_SCORE", evascoreInfo.getData(i).getString("preBandwidthScore"));
//    			param.put("BANDWIDTH_SCORE", evascoreInfo.getData(i).getString("bandwidthScore"));
//    			param.put("PREINTERFERE_SCORE", evascoreInfo.getData(i).getString("preInterfereScore"));
//    			param.put("INTERFERE_SCORE", evascoreInfo.getData(i).getString("interfereScore"));
//    			param.put("PREPACKAGELOSS_SCORE", evascoreInfo.getData(i).getString("prePackagelossScore"));
//    			param.put("PACKAGELOSS_SCORE", evascoreInfo.getData(i).getString("packagelossScore"));
//    			param.put("PREDELAY_SCORE", evascoreInfo.getData(i).getString("preDelayScore"));
//    			param.put("DELAY_SCORE", evascoreInfo.getData(i).getString("delayScore"));
//    			param.put("PREFIELDSTRENGTH_SCORE", evascoreInfo.getData(i).getString("preFieldstrengthScore"));
//    			param.put("FIELDSTRENGTH_SCORE", evascoreInfo.getData(i).getString("fieldstrengthScore"));
//
//    	    	Dao.insert("TI_INTELLIGENTNET_EVASCORE", param,Route.getJourDb(routeId));
//    		}
//    	}
  
    	//2.修改智能组网业务状态变换表  状态
    	IData input = new DataMap();
    	input.put("NEW_LIST_NO", newListNo);
    	input.put("LIST_STATE", "4");
    	input.put("OPER_NUMB", oprNumb);
    	input.put("INTERFACE_TYPE", "1");
    	updateProcess(input);
    	
    	//3.
//    	IData returnData = new DataMap();
//    	returnData.put("OPER_NUMB", oprNumb);
//		returnData.put("OPER_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		String positionListBeforeStr = data.getString("NETWORKING_POSITION_LIST_BEFORE");
		IDataset positionListBefore = null;
		if(StringUtils.isNotBlank(positionListBeforeStr))
		{
			positionListBefore = new DatasetList(positionListBeforeStr);
		}
		
		String equipmentListBeforeStr = data.getString("NETWORKING_EQUIPMENT_LIST_BEFORE");
		IDataset equipmentListBefore = null;
		if(StringUtils.isNotBlank(equipmentListBeforeStr))
		{
			equipmentListBefore = new DatasetList(equipmentListBeforeStr);
		}
		
		String positionListAfterStr = data.getString("NETWORKING_POSITION_LIST_AFTER");
		IDataset positionListAfter = null;
		if(StringUtils.isNotBlank(positionListAfterStr))
		{
			positionListAfter = new DatasetList(positionListAfterStr);
		}
		
		String equipmentListAfterStr = data.getString("NETWORKING_EQUIPMENT_LIST_AFTER");
		IDataset equipmentListAfter = null;
		if(StringUtils.isNotBlank(equipmentListAfterStr))
		{
			equipmentListAfter = new DatasetList(equipmentListAfterStr);
		}
		
		String zwImageInfoStr = data.getString("ZW_IMAGE_INFO");
		IData zwImageInfo = null;
		if(StringUtils.isNotBlank(zwImageInfoStr))
		{
			zwImageInfo = new DataMap(zwImageInfoStr);
		}
		
		String evascoreInfoStr = data.getString("EVA_SCORE_INFO");
		IData evascoreInfo = null;
		if(StringUtils.isNotBlank(evascoreInfoStr))
		{
			evascoreInfo = new DataMap(evascoreInfoStr);
		}
    	//IBossCall.报结信息同步
    	IDataset ids = IBossCall.evaInfoSyn(oprNumb, oprTime, bizType, newListNo, workListNo, "898", listState, installEndTime, 
    			data.getString("NET_WORKING_ID",""), preCoveragePercent, coveragePercent, bizVersion,
    			positionListBefore, equipmentListBefore,positionListAfter, 
    			equipmentListAfter, zwImageInfo, evascoreInfo);

    	if(IDataUtil.isNotEmpty(ids))
    	{
    		returnData.put("BIZ_ORDER_RESULT", "0000");
    		returnData.put("RESULT_DESC", "评测信息同步成功");
    	}
    	else
    	{
    		returnData.put("BIZ_ORDER_RESULT", "2000");
    		returnData.put("RESULT_DESC", "评测信息同步失败");
    	}
    	
    	return returnData;
    }
    
    /**
     * 组网业务状态查询
     * 用户订购完成智能组网业务之后，能够在和家亲手机客户端上查看当前业务的状态
     * @param data
     * @throws Exception
     */
    public IData busiStatusQuery(IData data) throws Exception
    {
        String newListNo = IDataUtil.chkParam(data, "newListNo"); 
//       String routeId = "";
        IData returnData = new DataMap();
//        try
//        {
//        	routeId = getEparchyCodeByNo(newListNo);
//        }
//        catch(Exception e)
//        {
//        	returnData.put("BizOrderResult", "0016");
//			returnData.put("ResultDesc", "用户无数据");
//			return returnData;
//        }

        IData param = new DataMap();
		param.put("NEW_LIST_NO", newListNo);

		StringBuilder sql = new StringBuilder(2500);
		sql.append(" SELECT T.NEW_LIST_NO, ");
		sql.append(" T.OPER_NUMB, ");
		sql.append("  T.OPER_TIME, ");
		sql.append("  T.BIZ_TYPE, ");
		sql.append("  T.BIZ_VERSION, ");
		sql.append("  T.LIST_STATE ");
		sql.append(" FROM TI_INTELLIGENTNET_PROCESS T ");
		sql.append(" WHERE NEW_LIST_NO   =:NEW_LIST_NO ");

		IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
		
		
		returnData.put("OprNumb", data.getString("OprNumb"));
		returnData.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		if(IDataUtil.isNotEmpty(ids))
		{
			returnData.put("BizOrderResult", "0000");
			returnData.put("ResultDesc", "查询成功");
			returnData.put("listState", ids.getData(0).getString("LIST_STATE"));
		}
		else
		{
			returnData.put("BizOrderResult", "2001");
			returnData.put("ResultDesc", "订购关系不存在");
		}
		
    	return returnData;
    }
    /**
     * 用户评价信息同步
     * 组网业务完成后，用户对其进行评价，一级组网平台同步评价信息至省CRM
     * @param data
     * @throws Exception
     */
    public IData busiCommentSyn(IData data) throws Exception
    {
        String oprNumb = IDataUtil.chkParam(data, "OPR_NUMB");
        String oprTime = IDataUtil.chkParam(data, "OPR_TIME");
        String bizType = IDataUtil.chkParam(data, "BUSI_TYPE");
        String newListNo = IDataUtil.chkParam(data, "NEW_LIST_NO");
        String apprTime = IDataUtil.chkParam(data, "APP_TIME");
        String compreSatisfactionS = IDataUtil.chkParam(data, "COMPRE_SATISFACION_S");
        String compreSatisfaction = data.getString("CPMPRE_SATISFACTION");
        String bizVersion = data.getString("BIZ_VERSION");
        String routeId = getEparchyCodeByNo(newListNo);

        String scoreInfoStr = IDataUtil.chkParam(data, "SCORE_INFO");
        IDataset scoreInfos = new DatasetList(scoreInfoStr);
        StringBuffer buf = new StringBuffer();
        if(IDataUtil.isEmpty(scoreInfos))
        {
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"评分详细信息不能为空。");
        }
        else
        {
        	for(int i = 0; i < scoreInfos.size(); i++)
        	{
                IData scoreInfo = scoreInfos.getData(i);
                buf.append(scoreInfo.getString("WBAND_STATE")).append("|").append(scoreInfo.getString("SERVICE_ATTITUDE_S")).append("|");
                buf.append(scoreInfo.getString("BUSINESS_PRESENT_S")).append("|").append(scoreInfo.getString("WAIT_TIME_S")).append("|");
                buf.append(scoreInfo.getString("SUGGEST")).append(";");
        	}
        }
       
    	IData param = new DataMap();
    	param.put("OPER_NUMB", oprNumb);
    	param.put("OPER_TIME", oprTime);
    	param.put("BIZ_TYPE",bizType);
    	param.put("NEW_LIST_NO",newListNo);
    	param.put("APPR_TIME",apprTime);
    	param.put("COMPRE_SATISFACTIONS",compreSatisfactionS);
    	param.put("COMPRE_SATISFACTION",compreSatisfaction);
    	
    	if(StringUtils.isNotBlank(buf.toString()) && buf.toString().length() > 1024)
    	{
        	param.put("SCORE_INFO",buf.toString().substring(0, 1024));
    	}
    	else
    	{
        	param.put("SCORE_INFO",buf.toString());
    	}
    	param.put("SCORE_INFO",buf.toString().subSequence(0, buf.length()));
    	param.put("BIZ_VERSION",bizVersion);

    	Dao.insert("TI_SUBSCRIBER_EVALUATE", param,Route.getJourDb(routeId));
    	
		IData returnData = new DataMap();
		returnData.put("OprNumb", data.getString("OprNumb"));
		returnData.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		returnData.put("BizOrderResult", "0000");
		returnData.put("ResultDesc", "用户评价信息同步成功");
		
		return returnData;
    	
    }
    
    /**
     * 订单退订取消同步
     * 服务使用方向服务提供方发起退订取消同步请求，服务提供方反馈同步结果至服务使用方
     * @param data
     * @throws Exception
     */
    public IData tdCancelSyn(IData data) throws Exception
    {
    	 String oprNumb = IDataUtil.chkParam(data, "OprNumb");
         String newListNo = IDataUtil.chkParam(data, "newListNo");
    	 String oprTime = IDataUtil.chkParam(data, "OprTime");
    	 String bizVersion = data.getString("BizVersion");
    	 String bizType = data.getString("BizType");

         //1：订单信息同步 2：工单信息同步  3：支付信息同步  4：报结信息同步  5：退订取消信息同步
         data.put("listState", "5");      
        //订单退订取消同步
      	String bizOrderResult = "0000";
      	String resultDesc = "取消成功";
      	
        try
      	{
        	IData param = new DataMap();
        	param.put("OprNumb", oprNumb);
        	param.put("OprTime", oprTime);
        	param.put("BizType", bizType);
        	param.put("listState", "5");//5：退订取消信息同步
        	param.put("newListNo", newListNo);
        	param.put("BizVersion", bizVersion);
        	resend(param);
        	
        	IData input = new DataMap();
          	input.put("NEW_LIST_NO", newListNo);
          	input.put("LIST_STATE", "5");
          	input.put("OPER_NUMB", oprNumb);
          	updateProcess(input);
          	
      		
      	}catch(Exception e)
      	{
      		bizOrderResult = "3029";
      		resultDesc = "取消失败";
      	}

		IData orderInfo = getOrderInfo(newListNo);
		String serialNumber = "";
		if(IDataUtil.isNotEmpty(orderInfo))
		{
			serialNumber = orderInfo.getString("SERIAL_NUMBER");
		}
		
    	IData returnData = new DataMap();
    	returnData.put("IDType", "01");
    	returnData.put("IDV", serialNumber);
    	returnData.put("OprNumb", oprNumb);
    	returnData.put("BizOrderResult", bizOrderResult);
    	returnData.put("ResultDesc", resultDesc);
    	returnData.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	returnData.put("listState", "5");

    	return returnData;
    }
    /**
     * 请求重发
     * 当服务使用方和提供方对比业务状态不一致时，服务使用方向服务提供方发起请求重发请求，服务提供方反馈重发结果至服务使用方
     * @param data
     * @return
     * @throws Exception
     */
    public IData resend(IData data) throws Exception
    {
    	 IData returnData = new DataMap();
    	 returnData.put("OprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    	 returnData.put("BizOrderResult", "0000");
		 returnData.put("ResultDesc", "重发成功");
    	 String oprNumb = IDataUtil.chkParam(data, "OprNumb");
         String oprTime = IDataUtil.chkParam(data, "OprTime");
         String bizType = IDataUtil.chkParam(data, "BizType");
         
         //1：订单信息同步 2：工单信息同步  3：支付信息同步  4：报结信息同步  5：退订取消信息同步
         String listState = IDataUtil.chkParam(data, "listState");
         String newListNo = IDataUtil.chkParam(data, "newListNo");
         String workListNo = data.getString("workListNo"); 
         String bizVersion = data.getString("BizVersion"); 
         
         String provID= "898";
         String cancelType = "02";//01：装维侧取消（组网失败标识） 02：用户侧取消
         String refundAmount = "0";//退款金额
         String oprCode = "01";//默认订购   
         IData orderInfo = null;
         if("5".equals(listState))
         {
        	  orderInfo = getCancelOrderInfo(newListNo);
         }
         else
         {
        	 orderInfo = getOrderInfo(newListNo);
         }

         String routeId = orderInfo.getString("EPARCHY_CODE");
         IData payInfo = getPayInfoByNewListNo(newListNo,routeId);
         String SPID = orderInfo.getString("SP_ID");
         String bizCode = orderInfo.getString("BIZ_CODE");
         String campaignId = orderInfo.getString("CAMPAIGN_ID");
         if(IDataUtil.isNotEmpty(payInfo) && "1".equals(payInfo.getString("PAY_STATE")))
         {
        	 refundAmount = payInfo.getString("PAY_AMOUNT");
         }
         
         if("1".equals(listState))//订单信息同步
         {
        	 IBossCall.orderInfoSyn(oprNumb, bizType, oprTime, oprCode, newListNo, listState, orderInfo.getString("CREATE_TIME"), 
        			 bizVersion, orderInfo.getString("ORDER_ID"), orderInfo.getString("SERIAL_NUMBER"), orderInfo.getString("CITY"),
        			 orderInfo.getString("CITY_CODE"), orderInfo.getString("COUNTRY"), orderInfo.getString("COUNTRY_CODE"), 
        			 orderInfo.getString("DISTRICT_ADDR"), SPID, bizCode, campaignId, "", 
        			 orderInfo.getString("RESERVE_DATE"), orderInfo.getString("HOUSE_TYPE"),orderInfo.getString("HOUSE_TYPE_CODE"),
        			 orderInfo.getString("AREA_SIZE"), orderInfo.getString("CHANNAL"),orderInfo.getString("WBAND_ACCOUNT"));
         }
         else if("2".equals(listState))//工单信息同步
         {
        	 IData workInfo = getWorkInfoByNewListNo(newListNo,routeId);
        	 
        	 if(IDataUtil.isNotEmpty(workInfo))
        	 {
            	 IBossCall.listInfoSyn(oprNumb, bizType, newListNo, workInfo.getString("WORK_LIST_NO"), workInfo.getString("STAFF_NAME"), workInfo.getString("STAFF_PHONE"),  workInfo.getString("WORK_LIST_UPDATE_TIME"), oprTime, workInfo.getString("LIST_ACC_TIME"), workInfo.getString("GO_HOME_TIME"), listState, bizVersion);
        	 }
        	 else
        	 {
        		 returnData.put("BizOrderResult", "3029");
        		 returnData.put("ResultDesc", "根据订单编码【"+newListNo+"】找不到工单信息！");
        	 }
         }
         else if("3".equals(listState))//支付信息同步
         {
        	 if(IDataUtil.isNotEmpty(payInfo))
        	 {
            	 IBossCall.payInfoSyn(oprNumb, bizType, oprTime, newListNo, listState, workListNo,payInfo.getString("PAY_STATE"), 
            			 payInfo.getString("PAY_TYPE"),  payInfo.getString("PAY_CHANNEL"), payInfo.getString("PAY_NO"), payInfo.getString("PAY_TIME"), 
            			 payInfo.getString("TOTAL_PRICE"), payInfo.getString("TOTAL_DISCOUNT"), payInfo.getString("REAL_TOTAL_PRICE"),
            			 payInfo.getString("PAY_AMOUNT"), payInfo.getString("REMARK"), SPID, bizCode, campaignId, bizVersion, null);
        	 }
        	 else
        	 {
        		 returnData.put("BizOrderResult", "3029");
        		 returnData.put("ResultDesc", "根据订单编码【"+newListNo+"】查询不到有效支付订单信息！");
        	 }
         }
         else if("4".equals(listState))//报结信息同步
         {
        	 IData evaluateInfo = getEvaluateInfoByNewListNo(newListNo,routeId);
        	 
        	 if(IDataUtil.isNotEmpty(evaluateInfo))
        	 {
            	 IBossCall.evaInfoSyn(oprNumb, oprTime, bizType, newListNo, evaluateInfo.getString("WORK_LIST_NO"), provID, listState, evaluateInfo.getString("INSTALL_END_TIME"), evaluateInfo.getString("NET_WORKING_ID"), evaluateInfo.getString("PRECOVERAGE_PERCENT"), evaluateInfo.getString("COVERAGE_PERCENT"), bizVersion, null, null, null, null, null, null);
        	 }
        	 else
        	 {
        		 returnData.put("BizOrderResult", "3029");
        		 returnData.put("ResultDesc", "根据订单编码【"+newListNo+"】找不到报结信息！");
        	 }
         }
         else if("5".equals(listState))//退订取消信息同步
         {
        	 IBossCall.tdCancelSyn(oprNumb, bizType, oprTime, "02", provID, newListNo, listState, cancelType, refundAmount, SPID, bizCode, campaignId, bizVersion);
         }
         else
         {
        	 returnData.put("BizOrderResult", "3029");
    		 returnData.put("ResultDesc", "不能识别重复类型");
         }
    	return returnData;
    }
    public IData getCancelOrderInfo(String newListNo) throws Exception
    {
    	IData input = new DataMap();
    	input.put("NEW_LIST_NO", newListNo);

		IDataset ids = Dao.qryByCodeAllJour("TF_B_INTELLIGENTNET", "SEL_BY_NEWLISTNO", input, false);

		IData returnData = new DataMap();
		
		if(IDataUtil.isNotEmpty(ids))
		{
			returnData = ids.getData(0);
		}
		
		return returnData;
    }
    private IData getPayInfoByNewListNo(String newListNo,String routeId) throws Exception
    {
    	IData param = new DataMap();
		param.put("NEW_LIST_NO", newListNo);

		StringBuilder sql = new StringBuilder(2500);
		sql.append(" SELECT OPER_NUMB,                        ");
		sql.append("        OPER_TIME,                        ");
		sql.append("        BIZ_TYPE,                         ");
		sql.append("        NEW_LIST_NO,                      ");
		sql.append("        LIST_STATE,                       ");
		sql.append("        DATE_TIME,                        ");
		sql.append("        WORK_LIST_NO,                     ");
		sql.append("        PAY_AMOUNT,                       ");
		sql.append("        PAY_STATE,                        ");
		sql.append("        PAY_TYPE,                         ");
		sql.append("        PAY_CHANNEL,                      ");
		sql.append("        PAY_NO,                           ");
		sql.append("        PAY_TIME,                         ");
		sql.append("        SP_ID,                            ");
		sql.append("        BIZ_CODE,                         ");
		sql.append("        CAMPAIGN_ID,                      ");
		sql.append("        BIZ_VERSION,                      ");
		sql.append("        TOTAL_PRICE,                      ");
		sql.append("        TOTAL_DISCOUNT,                   ");
		sql.append("        REAL_TOTAL_PRICE,                 ");
		sql.append("        REMARK                            ");
		sql.append("   FROM TI_INTELLIGENTNET_PAY T           ");
		sql.append("  WHERE NEW_LIST_NO = :NEW_LIST_NO        ");

    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeId));
		IData returnData = new DataMap();
		
		if(IDataUtil.isNotEmpty(ids))
		{
			returnData = ids.getData(0);
		}
		
		return returnData;
    }
    private IData getWorkInfoByNewListNo(String newListNo,String routeId) throws Exception
    {
    	IData param = new DataMap();
		param.put("NEW_LIST_NO", newListNo);

		StringBuilder sql = new StringBuilder(2500);
		sql.append("  SELECT OPER_NUMB,                        ");
		sql.append("         OPER_TIME,                        ");
		sql.append("         BIZ_TYPE,                         ");
		sql.append("         NEW_LIST_NO,                      ");
		sql.append("         WORK_LIST_NO,                     ");
		sql.append("         STAFF_NAME,                       ");
		sql.append("         STAFF_PHONE,                      ");
		sql.append("         LIST_ACC_TIME,                    ");
		sql.append("         GO_HOME_TIME,                     ");
		sql.append("         LIST_STATE,                       ");
		sql.append("         BIZ_VERSION,                      ");
		sql.append("         WORK_LIST_UPDATE_TIME             ");
		sql.append("    FROM TI_INTELLIGENTNET_WORK           ");
		sql.append("  WHERE NEW_LIST_NO = :NEW_LIST_NO        ");

    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeId));
		IData returnData = new DataMap();
		
		if(IDataUtil.isNotEmpty(ids))
		{
			returnData = ids.getData(0);
		}
		
		return returnData;
    }
    private IData getEvaluateInfoByNewListNo(String newListNo,String routeId) throws Exception
    {
    	IData param = new DataMap();
		param.put("NEW_LIST_NO", newListNo);

		StringBuilder sql = new StringBuilder(2500);
		sql.append(" SELECT OPER_NUMB,                         ");
		sql.append("         OPER_TIME,                        ");
		sql.append("         BIZ_TYPE,                         ");
		sql.append("         WORK_LIST_NO,                     ");
		sql.append("         PRECOVERAGE_PERCENT,              ");
		sql.append("         COVERAGE_PERCENT,                 ");
		sql.append("         INSTALL_END_TIME,                 ");
		sql.append("         LIST_STATE,                       ");
		sql.append("         NEW_LIST_NO,                      ");
		sql.append("         BIZ_VERSION,                      ");
		sql.append("         NET_WORKING_ID                    ");
		sql.append("    FROM TI_INTELLIGENTNET_EVALUATE       ");
		sql.append("  WHERE NEW_LIST_NO = :NEW_LIST_NO        ");

    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb(routeId));
		IData returnData = new DataMap();
		
		if(IDataUtil.isNotEmpty(ids))
		{
			returnData = ids.getData(0);
		}
		
		return returnData;
    }
    
}
