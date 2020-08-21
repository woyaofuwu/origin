
package com.asiainfo.veris.crm.order.soa.person.busi.checkBeforeNPOut;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.checknpoutresultquery.CheckNpOutResultQueryBean;

public class CheckBeforeNPOutSVC extends CSBizService
{
	private static Logger logger = Logger.getLogger(CheckBeforeNPOutSVC.class);
    public IData checkNpOut(IData input) throws Exception
    {
    	IData reqData = new DataMap();
    	IData retData = new DataMap();
    	
    	//入参效验
    	if("".equals(input.getString("SERIAL_NUMBER", "")))
    	{
    		retData.put("X_RESULTCODE", "-1");
 			retData.put("X_RESULTINFO", "手机号码不能为空!");
 			return retData;
    	}
    	
    	 IDataset userList =  UserInfoQry.getUserInfoBySn(input.getString("SERIAL_NUMBER"), "0");
 		
 		if (userList == null || userList.size() < 1) {
 			retData.put("X_RESULTCODE", "-1");
 			retData.put("X_RESULTINFO", "获取不到用户资料信息!");
 			return retData;
 		}
 		
 		 UcaData uca = null;
         uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
         
         if(!"0".equals(input.getString("IN_MODE_CODE", "")))
         {
        	 if("".equals(input.getString("USER_NAME", "")))
         	{
         		retData.put("X_RESULTCODE", "-1");
      			retData.put("X_RESULTINFO", "用户名称不能为空!");
      			return retData;
         	}
         	
         	if("".equals(input.getString("CRED_NUMBER", "")))
         	{
         		retData.put("X_RESULTCODE", "-1");
      			retData.put("X_RESULTINFO", "身份证号码不能为空!");
      			return retData;
         	}
         	
        	 if(!input.getString("USER_NAME").equals(uca.getCustomer().getCustName()))
             {
            	retData.put("X_RESULTCODE", "-1");
      			retData.put("X_RESULTINFO", "用户名输入不正确!");
      			return retData;
             }
             
             if(!input.getString("CRED_NUMBER").equals(uca.getCustomer().getPsptId()))
             {
            	retData.put("X_RESULTCODE", "-1");
      			retData.put("X_RESULTINFO", "身份证号码输入不正确!");
      			return retData;
             }
         }
        
         
       
         
         //开始拼装接口调用入参
         String messageId= "000";
         IDataset ids = MsisdnInfoQry.getMsisDns(input.getString("SERIAL_NUMBER"));
         
         if (IDataUtil.isNotEmpty(ids))
         {
             String areaCode = ids.getData(0).getString("AREA_CODE", "");
             String strAreaCode = "";
             if (4 == areaCode.length())
             {
                 strAreaCode = areaCode.substring(1, 4);
             }
             else
             {
                 strAreaCode = areaCode;
             }
             
             messageId += strAreaCode;
         }else
         {
        	 messageId += "898";
         }
         messageId += "1115145116";
         
         reqData.put("BACK_TAG", "");
         reqData.put("PORT_OUT_NETID", "00238980");
         reqData.put("CREDTYPE", uca.getCustomer().getPsptTypeCode());
         reqData.put("CREDNUMBER", uca.getCustomer().getPsptId());
         reqData.put("CARDTYPE", "");
         reqData.put("PORT_IN_NETID", "00118980");
         reqData.put("TRADE_DEPART_ID", "NGPF0");
         reqData.put("ACTOR_PHONE", "");
         reqData.put("CREDNUMBER2", "");
         reqData.put("NPCODE", input.getString("SERIAL_NUMBER"));
         reqData.put("CREDTYPE2", "Z");
         reqData.put("REQTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
         reqData.put("HOME_NETID", "00238980");
         reqData.put("SERVICETYPE", "MOBILE");
         reqData.put("TRADE_EPARCHY_CODE", "0898");
         reqData.put("TRADE_CITY_CODE", "NGPF");
         reqData.put("TELEPHONE", input.getString("SERIAL_NUMBER"));
         reqData.put("EPARCHY_CODE", "0898");
         reqData.put("COMMANDCODE", "APPLY_REQ");
         reqData.put("NAME2", "");
         reqData.put("FLOWID", "8981150414000001");
         reqData.put("USERNAME", input.getString("USERNAME"));
         reqData.put("TRADE_STAFF_ID", "SUPERUSR");
         reqData.put("MESSAGEID", messageId);
         reqData.put("USERNAME", uca.getCustomer().getCustName());
         reqData.put("PRE_TYPE", "1");
         
         IData outparam = CSAppCall.callOne("SS.DispatcherNpRequestSVC.dispatcherNpRequest", reqData);
         
         //发送通知客户短信
         String in_mode_code= input.getString("IN_MODE_CODE", "");
         if(in_mode_code==null||"".equals(in_mode_code)){
        	 in_mode_code=getVisit().getInModeCode();
         }
         if(!"0".equals(in_mode_code)){
            IData templateInfo = TemplateQry.qryTemplateContentByTempateId("CRM_SMS_NP_OUT_20180514001");
            IData data = new DataMap();
              data.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
              data.put("NOTICE_CONTENT", templateInfo.getString("TEMPLATE_CONTENT1"));
              data.put("BRAND_CODE", "");
              data.put("RECV_ID", uca.getUserId());
              data.put("REMARK", "自动给客户发送挽留营销短信");
              SmsSend.insSms(data);
              
              templateInfo = TemplateQry.qryTemplateContentByTempateId("CRM_SMS_NP_OUT_20180514002");
              data = new DataMap();
              data.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
              data.put("NOTICE_CONTENT", templateInfo.getString("TEMPLATE_CONTENT1"));
              data.put("BRAND_CODE", "");
              data.put("RECV_ID", uca.getUserId());
              data.put("REMARK", "自动给客户发送挽留营销短信");
              SmsSend.insSms(data);
              
              
            //REQ201804120024-关于新增携转预审核清单查询界面的需求 新增携转预审核记录 by mengqx
  			IData insertData = new DataMap();
  			if ("".equals(outparam.getString("X_RESULTINFO", ""))) {
  				//用户可以办理携出
  				insertData.put("SMS_RESULT", 1);//通过
  			} else {
  				insertData.put("SMS_RESULT", 0);//未通过
  			}
  			
  			CheckNpOutResultQueryBean bean = (CheckNpOutResultQueryBean) BeanManager
  					.createBean(CheckNpOutResultQueryBean.class);
  			insertData.put("USER_ID", uca.getUserId());
  			insertData.put("SERIAL_NUMBER",  input.getString("SERIAL_NUMBER"));
  			insertData.put("CITY_CODE", uca.getUser().getCityCode());
  			insertData.put("SMS_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
  			
  			insertData.put("CUST_NAME", uca.getCustPerson().getCustName());
  			IData productInfo = UcaInfoQry.qryUserMainProdInfoByUserId(uca.getUserId());
  			insertData.put("PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
  			
  			//调用接口获取近三个月消费平均值数据
  			IData inParam = new DataMap();
  			inParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
  			inParam.put("THREE_MONFEE_TAG", 1);
  			IDataset results = CSAppCall.call("AM_CRM_AverageFee", inParam);// 账务接口
  			if(IDataUtil.isNotEmpty(results)){
  				IData result = results.getData(0);
  				insertData.put("FEE", result.getString("AVERAGE_FEE"));
  			} else {
  				insertData.put("FEE", "");
  			}

  			bean.insertNonBossFeeItem(insertData);
  			//end REQ201804120024-关于新增携转预审核清单查询界面的需求 新增携转预审核记录 by mengqx 
         }
         
         if("".equals(outparam.getString("X_RESULTINFO", "")))
         {
        	 retData.put("X_RESULTCODE", "0");
        	 retData.put("X_RESULTINFO", "用户可以办理携出！");
         }else
         {
        	 retData.put("X_RESULTCODE", "-1");
        	 String resultInfo = outparam.getString("X_RESULTINFO");
        	 if(resultInfo.contains("<Key>") && resultInfo.contains("@@") && resultInfo.contains("</Key>"))
        	 {
        		 String code = resultInfo.substring( resultInfo.indexOf("<Key>")+5, resultInfo.indexOf("@@"));
            	 String reason = resultInfo.substring( resultInfo.indexOf("@@")+2, resultInfo.lastIndexOf("</Key>"));
            	 
            	 
            	 
            	 if("0".equals(input.getString("IN_MODE_CODE", "")))
            	 {
            		String[] codeArray = code.split("\\|");
            		String[] reasonArray = reason.split("\\|");
            		retData.put("X_RESULTCODE", "0001");
            	
            		if(codeArray.length != reasonArray.length)
            		{
            			retData.put("X_RESULTINFO", "限制编码和限制说明长度不等：" + code + reason);

            			
            		}else
            		{
            			IDataset dataset = new DatasetList();
            			
            			for(int i = 0; i < codeArray.length; i++)
            			{
            				IData    data = new DataMap();
            				data.put("FORBIDDEN_CODE", codeArray[i]);
            				data.put("FORBIDDEN_MSG", reasonArray[i]);
            				dataset.add(data);
            			}
            			retData.put("X_RESULTINFO", dataset);
            			
            		}	
            	 }else
            	 {
            		 retData.put("X_RESULTINFO", reason);
            	 }
            	 
        	 }else
        	 {
        		 retData.put("X_RESULTINFO", resultInfo);
        	 }
        	 
         }
    	
    	return retData;
    }
    
    //REQ201905200021关于开发携转号码限制查询接口的需求 wuhao5 2019-06-03
    public IData carryNumberLimitInfo(IData inputData) throws Exception
    { 	    	
    	if (logger.isDebugEnabled())
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBeforeNpOut() >>>>>>>>>>>>>>>>>>");
    	System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBeforeNpOut() >>>>>>>>>>>>>>>>>>");
    	String respDesc = "success";
    	String respCode = "0";
        //Pagination page = getPagination("pageNav");
        inputData.put("DEPART_ID", getVisit().getDepartId());
        inputData.put("EPARCHY_CODE", "0898");
        inputData.put(Route.ROUTE_EPARCHY_CODE, "0898");
        inputData.put("IN_MODE_CODE", "0");
        inputData.put("SERIAL_NUMBER", inputData.getData("params").getString("userMobile",""));
        String paging = inputData.getData("params").getData("crmpfPubInfo").getString("paging","");
		int irowsPerPage = 0;
        int ipageNum = 0;
        getVisit().setInModeCode("0");
        getVisit().setLoginEparchyCode("0898");
        IDataset limitInfo = new DatasetList();
        // 服务返回结果集
        IDataset retDatas = CSAppCall.call("SS.CheckBeforeNPOut.checkNpOut", inputData);
        IData retData = new DataMap();
        if (IDataUtil.isNotEmpty(retDatas))
        {
        	retData = retDatas.getData(0);
        	if(IDataUtil.isEmpty(retData))
        	{
                IData result = new DataMap();
                
                IData object = new DataMap();
                object.put("respDesc", "底层接口出错,无返回结果");
                object.put("respCode", "-9999");
                object.put("result", result);
                object.put("resultRows", "0");
                
                IData returnData = new DataMap();
                returnData.put("object", object);
            	return returnData;
        	}
        }else
        {                	
            IData result = new DataMap();
            
            IData object = new DataMap();
            object.put("respDesc", "底层接口出错,无返回结果");
            object.put("respCode", "-9999");
            object.put("result", result);
            object.put("resultRows", "0");
            
            IData returnData = new DataMap();
            returnData.put("object", object);
        	return returnData;
        }        
        String reason = retData.getString("X_RESULTINFO");
		String[] reasonArray = reason.split("\\|");
		int resultRows = reasonArray.length;//限制说明总条数
		int maxNum = resultRows;//查询终止条数,默认为限制业务条数
		int beginNum = 0;//查询开始条数,默认从0开始
        
        //分页功能
        if("1".equals(paging)){
        	String rowsPerPage = inputData.getData("params").getData("crmpfPubInfo").getString("rowsPerPage","0");
        	String pageNum = inputData.getData("params").getData("crmpfPubInfo").getString("pageNum","0");
        	//分页参数非空才取值,避免报错
        	if(!"".equals(rowsPerPage) && !"".equals(pageNum)){
        		irowsPerPage = Integer.parseInt(rowsPerPage);
                ipageNum = Integer.parseInt(pageNum);
        	}
        	//当分页参数正常时才按分页查询
        	if(irowsPerPage > 0 && ipageNum > 0){        		
	        	beginNum = (ipageNum - 1) * irowsPerPage;
	        	//超过最大条数,不返回值
	        	if(beginNum > resultRows)
	        	{
	        		beginNum = 0;
	        		maxNum = 0;
	        	}else
	        	{
	        		maxNum = (ipageNum * irowsPerPage) > resultRows ? resultRows : (ipageNum * irowsPerPage);//分页最大条数超过总条数取总条数        		
	        	}        		
        	}
        }
        
        if(resultRows > 0){
            for(int i = beginNum ; i < maxNum ; i++){
            	IData limitInfoData = new DataMap();
            	limitInfoData.put("limitId", "无此数据");//限制编码
            	limitInfoData.put("limitExplain", reasonArray[i].replace("\n", ""));//限制说明
            	limitInfoData.put("handleChannel", "无此数据");//限制业务受理方式
            	limitInfoData.put("authType", "无此数据");//限制业务验证方式
            	limitInfoData.put("isConform", "");//是否符合工信部要求 1是,0否
            	limitInfo.add(limitInfoData);
            }
        }

        IData result = new DataMap();
        if(IDataUtil.isNotEmpty(limitInfo))
        {
        	result.put("limitInfo", limitInfo);
        }
                
        IData object = new DataMap();
        object.put("respDesc", respDesc);
        object.put("respCode", respCode);
        object.put("result", result);
        object.put("resultRows", Integer.toString(resultRows));
        
        IData returnData = new DataMap();
        returnData.put("object", object);
        
        System.out.print("wuhao5>>carryNumberLimitInfo>>result" + object);
        logger.debug("wuhao5>>carryNumberLimitInfo>>result" + object);
        
    	return returnData;    	
    }
    
}
