package com.asiainfo.veris.crm.order.soa.person.busi.recharge;

import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.axis.client.Service;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.com.tass.hnyd.hsm.TassHnydAPI;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.alibaba.fastjson.JSON;
import com.asiainfo.veris.crm.order.pub.exception.CardSaleException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.RechargeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.RechargeBy10086Info.QueryRechargeBy10086Info;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSInputData;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSServNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSVarNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.NeaSoapBindingStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.SoapInputXml;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOPStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOP_ServiceLocator;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.holders.CWSVarNodeHolder;
import com.itextpdf.text.List;


public class ReceiveMobileRechargeBean extends CSBizBean
{
	
	/**
	 * 调用渠道接口记录接触信息
	 * inData 被充值手机号、接入手机号、卡密等
	 * vcData 调用智能网充值接口后的处理结果
	 */
    public IData callCHNLChargeTouchITF(IData inData, IData vcData) throws Exception
    {
        // 调用渠道接口记录接触信息
        IData httpParams = new DataMap();
        int touchState = 0;//初始touchState
        
        //如果充值失败，将touchState置为1
        if (!"0000".equals(vcData.getString("X_RESULTCODE")))
        {
            touchState = 1;
        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(inData.getString("X_CALL_EDMPHONECODE"));//被充值手机号，如果是非本省的这里会找不到用户信息
        
        String custId = "";
        if (IDataUtil.isNotEmpty(userInfo))
        {
            custId = userInfo.getString("CUST_ID");
        }

        //对应页面“渠道管理--渠道信息管理--热线充值渠道接触查询”  对应数据表UCR_CRM1.TF_CHL_TOUCH_INFO
        httpParams.put("TRADE_EPARCHY_CODE", inData.getString("TRADE_EPARCHY_CODE"));
        httpParams.put("TRADE_CITY_CODE", inData.getString("TRADE_CITY_CODE"));
        httpParams.put("TRADE_DEPART_ID", inData.getString("TRADE_DEPART_ID"));
        httpParams.put("TRADE_STAFF_ID", inData.getString("TRADE_STAFF_ID"));

        httpParams.put("BUSI_ACCEPT_ID", "");//页面展示的2[业务受理标识]
        httpParams.put("CHNL_ID", inData.getString("IN_MODE_CODE", "1"));//页面展示的3[渠道标识]，目前采用了入参的IN_MODE_CODE，之前采用的0898
        httpParams.put("CHNL_TYPE", inData.getString("CHNL_TYPE","1"));//页面展示的4[渠道类型]对应的编码 1：客服
        httpParams.put("TOUCH_REASON", vcData.getString("X_RESULTINFO", ""));//页面展示的5[接触原因]，来源智能网返回的结果描述
        httpParams.put("TOUCH_TYPE", "CHARGE");
        httpParams.put("TOUCH_CONTENT", "充值");//页面展示的6[接触内容]
        httpParams.put("TOUCH_STATE", touchState);//页面展示的7[接触状态]编码
        httpParams.put("TOUCH_RESULT", touchState == 0 ? "成功" : "失败");//页面展示的9[接触结果]
        httpParams.put("TOUCH_TIME", SysDateMgr.getSysTime());//页面展示的8[接触时间]
        httpParams.put("ELEMENT11", custId);//页面展示的1[客户标识]
        httpParams.put("ELEMENT12", inData.getString("SERIAL_NUMBER"));//页面展示的10[主叫号码]
        httpParams.put("ELEMENT13", inData.getString("X_CALL_EDMPHONECODE"));//页面展示的11[被充值号码]
        httpParams.put("ELEMENT14", inData.getString("EXPORT_PARA_TYPE"));//页面展示的12[10086自助服务按键轨迹]
        httpParams.put(Route.ROUTE_EPARCHY_CODE, inData.getString("TRADE_EPARCHY_CODE"));

        IData saveResult = SccCall.saveChargeTouchInfo(httpParams).getData(0);

        return saveResult;
    }

    /**
     * 调用智能网充值
     * */
    private IData callService(IData data) throws Exception
    {
        IData result = new DataMap();//返回结果
    	IData logResult = new DataMap();//日志结果内容
        String url = "http://10.200.179.177:20004/service/SMPCVC2";
        String backupUrl="http://10.200.179.177:20005/service/SMPCVC3";
        String switchCode = "SMPCVC2";
        String switchCodeNew="SMPCVC3";
        String srvCode = "TP003";
        String paramCode0 = "V290"; //CardPin
        String paramCode1 = "G004"; //MSISDN
        String paramCode2 = "V291"; //SerialNumber
        String paramCode3 = "V940";//TRADETYPE
        int timeOut = 60000; //调用SMPVC超时时间

        String paramValue = "";
        
        /*
         * 下面的三个值是用来做测试使用
         */
        String testSwitch="0";	//默认是关闭的，0是关闭，1是打开，打开就会使用备用的地址
        String testUrl="";			//测试用的原url
        String testBackupUrl="";	//测试用的备用url
        
        String smsSwith="0";	//默认不下发短信
        
        String checktype="0";	//默认不使用新调用方式

        //如果数据库静态参数表中存在PREPAID_CARD_SRV配置，则采用数据库的配置
        try
        {
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "NEW_URL");
            if (StringUtils.isNotBlank(paramValue))
                {url = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "BACKUP_URL");
            if (StringUtils.isNotBlank(paramValue))
                {backupUrl = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TEST_SWITCH");
            if (StringUtils.isNotBlank(paramValue))
                {testSwitch = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TEST_BACKUP_URL");
            if (StringUtils.isNotBlank(paramValue))
                {testBackupUrl = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TEST_URL");
            if (StringUtils.isNotBlank(paramValue))
                {testUrl = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "10086_SMS_SWITH");
            if (StringUtils.isNotBlank(paramValue))
                {smsSwith = paramValue;}

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SWITCH_CODE1");
            if (StringUtils.isNotBlank(paramValue))
                {switchCode = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SWITCH_CODE2");
            if (StringUtils.isNotBlank(paramValue))
                {switchCodeNew = paramValue;}

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SRV_CODE");
            if (StringUtils.isNotBlank(paramValue))
                {srvCode = paramValue;}

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CARDPIN_PARAM_CODE");
            if (StringUtils.isNotBlank(paramValue))
                {paramCode0 = paramValue;}

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "MSISDN_PARAM_CODE");
            if (StringUtils.isNotBlank(paramValue))
                {paramCode1 = paramValue;}

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SERIALNUMBER_PARAM_CODE");
            if (StringUtils.isNotBlank(paramValue))
                {paramCode2 = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CHECK_TYPE");
            if (StringUtils.isNotBlank(paramValue))
                {checktype = paramValue;}
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TIME_OUT");
            if (StringUtils.isNotBlank(paramValue))
                {timeOut = Integer.parseInt(paramValue);}
        }
        catch (Exception e)
        {
            CSAppException.apperr(CardSaleException.CRM_CardSaleInfo_22);
        }

        CWSServNode servNode = new CWSServNode();

        servNode.setMStrServName(srvCode);
        CWSServNode mVServList[] = new CWSServNode[1];
        mVServList[0] = servNode;

        CWSVarNode mVVarList[] = new CWSVarNode[4];//REQ201309110034 关于部署10086热线充值融合改造工作的通知 Size 3->4

        CWSVarNode varNode0 = new CWSVarNode();
        varNode0.setMStrName(paramCode0);
        varNode0.setMStrValue(data.getString("CARDPIN"));
        mVVarList[0] = varNode0;

        CWSVarNode varNode1 = new CWSVarNode();
        varNode1.setMStrName(paramCode1);
        varNode1.setMStrValue(data.getString("MSISDN"));
        mVVarList[1] = varNode1;

        CWSVarNode varNode2 = new CWSVarNode();
        varNode2.setMStrName(paramCode2);
        varNode2.setMStrValue(data.getString("SERIAL_NUMBER"));
        mVVarList[2] = varNode2;

        //REQ201309110034 关于部署10086热线充值融合改造工作的通知 
	    //  业务类型，可定义各种充值渠道，取值如下：
	    //  10：短信营业厅 IN_MODE_CODE:5
	    //  11：网上营业厅 IN_MODE_CODE:2
	    //  12：实体营业厅 IN_MODE_CODE:0
	    //  13：WAP营业厅 IN_MODE_CODE:L
	    //  14：自助终端  IN_MODE_CODE:X
	    //  15：10086热线 IN_MODE_CODE:1
        String tradeType = data.getString("IN_MODE_CODE");
        if("5".equals(tradeType))
        {
        	tradeType = "10";
        }else if("2".equals(tradeType))
        {
        	tradeType = "11";
        }else if("0".equals(tradeType))
        {
        	tradeType = "12";
        }else if("L".equals(tradeType))
        {
        	tradeType = "13";
        }else if("X".equals(tradeType))
        {
        	tradeType = "14";
        }else if("1".equals(tradeType))
        {
    	  tradeType = "15";
        }
      
        CWSVarNode varNode3 = new CWSVarNode();
        varNode3.setMStrName(paramCode3);
        varNode3.setMStrValue(tradeType);
        mVVarList[3] = varNode3;
        
        
        String orderId=getStrOrderId();
        
        //如果开启了测试，使用测试地址来做测试
    	if(testSwitch.equals("1")){
    		url=testUrl;
    		backupUrl=testBackupUrl;
    	}
    	
    	/*
    	 * 通过数据库的序列来分配每个请求分配的地址
    	 * 双数就是备份地址
    	 */
    	String urlType="0";
    	long restNum=Long.parseLong(orderId.substring(8))%2;
    	if(restNum>0){
    		url=backupUrl;
    		switchCode=switchCodeNew;
    		urlType="1";
    	}
    	
    	
        CWSInputData inputData = new CWSInputData();
        inputData.setMStrOrderID(orderId);
        inputData.setMStrSerialNumber(data.getString("SERIAL_NUMBER"));
        inputData.setMStrSwitchid(switchCode);
        inputData.setNPriority(50);
        inputData.setMVServList(mVServList);
        inputData.setMVVarList(mVVarList);

        StringHolder m_strOrderID = new StringHolder();
        IntHolder m_nOperationResult = new IntHolder();
        StringHolder m_strFinishTime = new StringHolder();
        StringHolder m_strErrorDescription = new StringHolder();
        IntHolder m_nCMDCount = new IntHolder();
        StringHolder m_strAutoCMDList = new StringHolder();
        CWSVarNodeHolder m_vQueryResult = new CWSVarNodeHolder();
        
        
        IData sopQueryResult = new DataMap();
        sopQueryResult.put("ORDER_ID", inputData.getMStrOrderID());
        
        boolean isCallError=false;
        
        //新联指调用
        String operationResult = null;
        String errorDescription = null;
        String cmdList = null;
        java.util.List<Element> queryResultList = null;
        
        try
        {
        	//用来监控是否会有超时的情况发生
        	try {
        		if(checktype.equals("1")){
        			SoapInputXml soapinput = new SoapInputXml();
        			URL u=new URL(url);
        			NeaSoapBindingStub neasop=new NeaSoapBindingStub(u,new Service());
        			String xmlParamStr = null;
        			
        			String serlial_number = data.getString("SERIAL_NUMBER");
					String priority = "50";
					xmlParamStr = soapinput.receiveMobileRecharge(orderId,serlial_number,priority,switchCode,mVServList,mVVarList);
					String res =neasop.callWSSOP(xmlParamStr); //新联指调用
					 Document dom=DocumentHelper.parseText(res);
					  Element root=dom.getRootElement();
					  operationResult=root.element("operationResult").getText();
					  errorDescription=root.element("description").getText();
					  cmdList=root.element("cmdList").getText();
					  queryResultList = root.elements("queryResultList");
        		}else{
        			WSSOP_ServiceLocator wssopLocator = new WSSOP_ServiceLocator(url);
                    WSSOPStub binding = (WSSOPStub) wssopLocator.getWSSOP();
                    binding.setTimeout(timeOut);
                    binding.callWSSOP(inputData, m_strOrderID, m_nOperationResult, m_strFinishTime, m_strErrorDescription, m_nCMDCount, m_strAutoCMDList, m_vQueryResult);
                    
        		}
        	
			} catch (Exception e) {
				
				isCallError=true;
				
				CSAppException.apperr(RechargeException.CRM_RECHARGE_800004,e.getMessage());
				
			}
        	
        	int operationResultCode = -999 ;
        	
        	if(checktype.equals("1")){
        		sopQueryResult.put("SEND_INFO",cmdList );
                sopQueryResult.put("OPERATION_CODE", operationResult);
                sopQueryResult.put("ERROR_DESCRIPTION",errorDescription);
                
        	}else{
        		sopQueryResult.put("SEND_INFO", new String(m_strAutoCMDList.value.getBytes("ISO-8859-1"), "GB2312"));
                sopQueryResult.put("OPERATION_CODE", m_nOperationResult.value);
                sopQueryResult.put("ERROR_DESCRIPTION", new String(m_strErrorDescription.value.getBytes("ISO-8859-1"), "GB2312"));
                
                
                 operationResultCode = m_nOperationResult.value;
        	}
        	
            
            if (operationResultCode == 0 || operationResult.equals("0"))
            {
                // 充值成功
//				IData sopQueryResult = new DataMap();
            	if(queryResultList != null){
            		String strName= null;
            		String strValue= null;
            		for(int i =0; i<queryResultList.size();i++){
            		   strName = ((Element) queryResultList.get(i)).element("strName").getText();
            		   strValue = ((Element) queryResultList.get(i)).element("strValue").getText();
            		   sopQueryResult.put(strName, strValue);

            		}
            	}
                if (m_vQueryResult.value != null)
                {
                    for (Object obj : m_vQueryResult.value)
                    {
                        CWSVarNode node = (CWSVarNode) obj;
                        sopQueryResult.put(node.getMStrName(), node.getMStrValue());
                    }
                }
                String cardPinAmount = sopQueryResult.getString("COUNTTOTAL", "");
                
                /*
                 * 发送短信
                 */
                if(smsSwith.equals("1")){		//下发短信
                	if (data.getString("SERIAL_NUMBER").equals(data.getString("MSISDN")))
                    {
                    	
                    	/*
                    	 * 获取短信模板
                    	 */
                    	String smsContentTemplate="";
                    	IDataset smsTemplates=CommparaInfoQry.getCommNetInfo("CSM", "1602", "10086_CHARGE_SELF_SMS");
                    	if(IDataUtil.isEmpty(smsTemplates)){
                    		smsContentTemplate="您已为@SERIAL_NUMBER@成功充值@FEE@元，充值时间为@ACCEPT_DATE@。即日起，拨打10086热线，根据语音提示操作，可实现手机充值卡充值。";
                    	}else{
                    		IData smsTemplate=smsTemplates.getData(0);
                    		smsContentTemplate=smsTemplate.getString("PARA_CODE2","")+smsTemplate.getString("PARA_CODE3","")+
                    						smsTemplate.getString("PARA_CODE4","")+smsTemplate.getString("PARA_CODE5","")+
                    						smsTemplate.getString("PARA_CODE6","")+smsTemplate.getString("PARA_CODE7","")+
                    						smsTemplate.getString("PARA_CODE8","")+smsTemplate.getString("PARA_CODE9","")+
                    						smsTemplate.getString("PARA_CODE10","");
                    		
                    	}
                    	
                    	String fee=converYuan(cardPinAmount);
                    	String smsContent=smsContentTemplate.replaceAll("@SERIAL_NUMBER@", data.getString("SERIAL_NUMBER")).
                    				replaceAll("@FEE@", fee).replaceAll("@ACCEPT_DATE@", getChargeTime());
                    	
                        /*
                         * 为自己充值
                         */
                    	IData smsData = new DataMap();
                    	smsData.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
                    	smsData.put("NOTICE_CONTENT", smsContent);
                    	smsData.put("RECV_ID", data.getString("SERIAL_NUMBER"));
                    	smsData.put("PRIORITY", "50");
                    	smsData.put("REMARK", "10086热线充值成功短信");
                    	SmsSend.insSms(smsData);
                    	
                    }
                    else
                    {
                    	/*
                    	 * 获取短信模板
                    	 */
                    	String smsContentTemplateSelf="";
                    	String smsContentTemplateOther="";
                    	IDataset smsTemplates=CommparaInfoQry.getCommNetInfo("CSM", "1602", "10086_CHARGE_OTHERS_SMS");
                    	if(IDataUtil.isEmpty(smsTemplates)){
                    		smsContentTemplateSelf="您已为@SERIAL_NUMBER@成功充值@FEE@元，充值时间为@ACCEPT_DATE@。即日起，拨打10086热线，根据语音提示操作，可实现手机充值卡充值。";
                    		smsContentTemplateOther="@SERIAL_NUMBER@已为您成功充值@FEE@元，充值时间为@ACCEPT_DATE@。即日起，拨打10086热线，根据语音提示操作，可实现手机充值卡充值。";
                    	}else{
                    		for(int i=0,size=smsTemplates.size();i<size;i++){
                    			IData smsTemplate=smsTemplates.getData(i);
                    			String paraCode1=smsTemplate.getString("PARA_CODE1","");
                    			
                    			String smsContentTemplateTempl=smsTemplate.getString("PARA_CODE2","")+smsTemplate.getString("PARA_CODE3","")+
                						smsTemplate.getString("PARA_CODE4","")+smsTemplate.getString("PARA_CODE5","")+
                						smsTemplate.getString("PARA_CODE6","")+smsTemplate.getString("PARA_CODE7","")+
                						smsTemplate.getString("PARA_CODE8","")+smsTemplate.getString("PARA_CODE9","")+
                						smsTemplate.getString("PARA_CODE10","");
                    			
                    			if(paraCode1.equals("1")){
                    				smsContentTemplateSelf=smsContentTemplateTempl;
                        		}else if(paraCode1.equals("2")){
                        			smsContentTemplateOther=smsContentTemplateTempl;
                        		}
                    		}
                    	}
                    	
                        // 为他人充值
                        String fee = converYuan(cardPinAmount);
                        String chargeTime=getChargeTime();
                        
                        String smsContentSelf=smsContentTemplateSelf.replaceAll("@SERIAL_NUMBER@", data.getString("MSISDN")).
                				replaceAll("@FEE@", fee).replaceAll("@ACCEPT_DATE@", chargeTime);
                    	String smsContentOther=smsContentTemplateOther.replaceAll("@SERIAL_NUMBER@", data.getString("SERIAL_NUMBER")).
                				replaceAll("@FEE@", fee).replaceAll("@ACCEPT_DATE@", chargeTime);
                        
                        /*
                         * 为发起号码下发短信
                         */
                    	IData smsDataSelf = new DataMap();
                    	smsDataSelf.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
                    	smsDataSelf.put("NOTICE_CONTENT", smsContentSelf);
                    	smsDataSelf.put("RECV_ID", data.getString("SERIAL_NUMBER"));
                    	smsDataSelf.put("PRIORITY", "50");
                    	smsDataSelf.put("REMARK", "10086热线充值成功短信");
                    	SmsSend.insSms(smsDataSelf);
                    	
                    	
                        /*
                         * 为被充值号码下发短信
                         */
                    	IData smsDataOther = new DataMap();
                    	smsDataOther.put("RECV_OBJECT", data.getString("MSISDN"));
                    	smsDataOther.put("NOTICE_CONTENT", smsContentOther);
                    	smsDataOther.put("RECV_ID", data.getString("MSISDN"));
                    	smsDataOther.put("PRIORITY", "50");
                    	smsDataOther.put("REMARK", "10086热线充值成功短信");
                    	SmsSend.insSms(smsDataOther);
                    	
                    }
                }
                
                
                logResult.put("X_RESULTCODE", "0");
                logResult.put("X_RESULTINFO", "充值成功");
                logResult.put("IN_FEE", cardPinAmount);
                
                
                result.put("X_RESULTCODE", "0000");
                result.put("X_RESULTINFO", "充值成功");
				result.put("IN_FEE", cardPinAmount);
            }
            else
            {
            	
                // 充值失败
                String regex = "RETN=[\\d]+,DESC=[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]+";// 匹配[RETN=1001,DESC=充值卡不存在]类似字符串
                String str =new String(m_strErrorDescription.value.getBytes("ISO-8859-1"), "GB2312");
                Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(str);
                if (matcher.find())
                {
                    String tmpStr = matcher.group(0);
                    String[] tmpStrs = tmpStr.split(",");
                	
                    if (tmpStrs.length == 2)
                    {
                    	String xResultCode=tmpStrs[0].substring(tmpStrs[0].indexOf("=") + 1);
                    	
                        result.put("X_RESULTCODE", xResultCode);
                        result.put("X_RESULTINFO", tmpStrs[1].substring(tmpStrs[1].indexOf("=") + 1));
                        result.put("X_RSPTYPE", "2");// add by ouyk
                        result.put("X_RSPCODE", "2998");// add by ouyk
                        
                        
                        logResult.put("X_RESULTCODE", tmpStrs[0].substring(tmpStrs[0].indexOf("=") + 1));
                        logResult.put("X_RESULTINFO", tmpStrs[1].substring(tmpStrs[1].indexOf("=") + 1));
                        logResult.put("X_RSPTYPE", "2");// add by ouyk
                        logResult.put("X_RSPCODE", "2998");// add by ouyk
                        
                       
                        if(xResultCode!=null){
                        	if(xResultCode.equals("1001")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1001);
                        	}else if(xResultCode.equals("1002")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1002);
                        	}else if(xResultCode.equals("1003")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1003);
                        	}else if(xResultCode.equals("1004")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1004);
                        	}else if(xResultCode.equals("1005")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1005);
                        	}else if(xResultCode.equals("1006")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1006);
                        	}else if(xResultCode.equals("1007")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1007);
                        	}else if(xResultCode.equals("1008")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1008);
                        	}else if(xResultCode.equals("1009")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1009);
                        	}else if(xResultCode.equals("1010")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1010);
                        	}else if(xResultCode.equals("1011")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1011);
                        	}else if(xResultCode.equals("1012")){
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_1012);
                        	}else{
                        		CSAppException.apperr(RechargeException.CRM_RECHARGE_800004);
                        	}
                        }else{
                    		CSAppException.apperr(RechargeException.CRM_RECHARGE_800004);
                    	}
                        
                    }
                    else
                    {
                    	logResult.put("X_RESULTCODE", "800003");
                    	logResult.put("X_RESULTINFO", "返回信息解析错误：" + tmpStr);
                        
                        CSAppException.apperr(RechargeException.CRM_RECHARGE_800003, "返回信息解析错误：" + str);
                    }
                }
                else
                {
                	logResult.put("X_RESULTCODE", "800003");
                	logResult.put("X_RESULTINFO", "返回信息解析错误：" + str);

                    CSAppException.apperr(RechargeException.CRM_RECHARGE_800003, "返回信息解析错误：" + str);
                }
                
            	//给主卡用户下发失败提示短信
            	IDataset ids = CommparaInfoQry.getCommpara("CSM", "3792", result.getString("X_RESULTCODE"), "ZZZZ");
            	if (IDataUtil.isEmpty(ids))
            	{
            		logResult.put("X_RESULTCODE", "549118");
            		logResult.put("X_RESULTINFO", "没有配置充值失败下发短信内容");
            		
            		CSAppException.apperr(RechargeException.CRM_RECHARGE_549118);
            	}
            	
            	IData commpara = ids.getData(0);
            	String Err_SmsInfo = commpara.getString("PARA_CODE1","") + commpara.getString("PARA_CODE2","") + commpara.getString("PARA_CODE3","");
            	IData smsData = new DataMap();
                smsData.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
                smsData.put("NOTICE_CONTENT", Err_SmsInfo);
                smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER")).getString("USER_ID"));
                smsData.put("PRIORITY", "50");
                smsData.put("REMARK", "调智能网充值接口失败");
                SmsSend.insSms(smsData);
            }
            

            // 记录日志
            logResult.put("CHARGE_TIME", getChargeTime());
            data.put("SMS_NOTICE_ID", "0");
            data.put("USER_ID", "0");
            data.put("USER_ID_B", "0");
            data.put("RSRV_STR1", urlType);
            data.put("RSRV_STR2", "0");	//未发生调用url错误
            recordCallLog(data, sopQueryResult, logResult);
            
            
        }
        catch (Exception e)
        {
        	
        	String xresultCode=logResult.getString("X_RESULTCODE","");
        	if(!(xresultCode!=null&&!xresultCode.trim().equals(""))){
        		logResult.put("X_RESULTCODE", "800004");
        		logResult.put("X_RESULTINFO", "调用智能网接口错误：" + e.getMessage());
        	}
        	
        	if(isCallError){		//调用出错特殊处理
        		data.put("RSRV_STR2", "1");	//发生调用url错误
            }else{
            	data.put("RSRV_STR2", "0");	//发生调用url错误
            }
            
            // 记录日志
        	logResult.put("CHARGE_TIME", getChargeTime());
            data.put("SMS_NOTICE_ID", "0");
            data.put("USER_ID", "0");
            data.put("USER_ID_B", "0");
            data.put("RSRV_STR1", urlType);
            recordCallLog(data, sopQueryResult, logResult);
        	
        	
        	//给主卡用户下发失败提示短信
        	IDataset ids = CommparaInfoQry.getCommpara("CSM", "3792", "ERR_SMSINFO", "ZZZZ");
        	if (IDataUtil.isEmpty(ids))
        	{
        		CSAppException.apperr(RechargeException.CRM_RECHARGE_549118);
        	}
        	
        	IData commpara = ids.getData(0);
        	String Err_SmsInfo = commpara.getString("PARA_CODE1","") + commpara.getString("PARA_CODE2","") + commpara.getString("PARA_CODE3","");
        	//异步下发短信
        	AsyncSendSms(data.getString("SERIAL_NUMBER"), Err_SmsInfo, "调用智能网接口错误");
            // 调用智能网接口失败
//            e.printStackTrace();

            //CSAppException.apperr(RechargeException.CRM_RECHARGE_800004, "调用智能网接口错误：" + e.getMessage());
            throw e;
        }
        
        return result;
    }

    private String getStrOrderId() throws Exception
    {
//        return SysDateMgr.getSysDate("yyyyMMddHHmmssSSS");
    	return SeqMgr.getOrderId();
    }
    
    private String getChargeTime()
    {
        return DateFormatUtils.format(new java.util.Date(), "yyyy年MM月dd日HH时mm分ss秒");
    }
    
    private String converYuan(String fen)
    {
        String yuan = "";
        if (fen == null || "".equals(fen))
            return "0.0";
        if (fen.length() == 1)
        {
            yuan = "0.0" + fen;
        }
        else if (fen.length() == 2)
        {
            yuan = "0." + fen;
        }
        else
        {
            yuan = fen.substring(0, fen.length() - 2) + "." + fen.substring(fen.length() - 2);
        }
        return yuan;
    }

    private boolean isNumeric(String str)
    {

        Pattern pattern = Pattern.compile("[0-9]*");

        Matcher isNum = pattern.matcher(str);

        if (!isNum.matches())
        {

            return false;
        }

        return true;

    }

    public IData receiveMobileRecharge(IData param) throws Exception
    {
    	//针对热线渠道的情况，热线渠道的标识为：in_mode_code=1,staffId=ITFCC000
    	String staffId=CSBizBean.getVisit().getStaffId();
    	String inModeCode=CSBizBean.getVisit().getInModeCode();
    	if(inModeCode.equals("1")&&staffId.equals("ITFCC000")){
    		CSBizBean.getVisit().setInModeCode("2");
    	}
    	
    	//校验入参
        IDataUtil.chkParam(param, "SERIAL_NUMBER");//接入手机号
        IDataUtil.chkParam(param, "X_CALL_EDMPHONECODE");//被充值手机号
        IDataUtil.chkParam(param, "CARD_PASSWD");//卡密

        IDataUtil.chkParam(param, "IN_MODE_CODE");
        IDataUtil.chkParam(param, "TRADE_CITY_CODE");
        IDataUtil.chkParam(param, "TRADE_DEPART_ID");
        IDataUtil.chkParam(param, "TRADE_STAFF_ID");
        IDataUtil.chkParam(param, "TRADE_EPARCHY_CODE");

        String x_call_edmphonecode = param.getString("X_CALL_EDMPHONECODE", "");//被充值手机号
        String serialNumber = param.getString("SERIAL_NUMBER", "");//接入手机号
        String cardPwd = param.getString("CARD_PASSWD", "");//卡密
        
        IData result = new DataMap();
        
        //如果被充值手机号不足11位，则报错：手机号码长度必须为11位数字!
        if (x_call_edmphonecode.length() != 11)
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_549112);
        }

        //如果卡密不足15位或超过20位，则报错：充值卡密码只能为15-20位数字!
        if (cardPwd.length() < 15 || cardPwd.length() > 20)
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_549113);
        }

        //如果接入手机号不全是数字，则报错：手机号码长度必须为11位数字!
        if (!isNumeric(serialNumber))
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_549114);
        }

        //如果卡密不全是数字，则报错：充值卡密码只能为15-20位数字!
        if (!isNumeric(cardPwd))
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_549115);
        }

        //充值管控功能
        //获取3791 PWDERRORS 10086热线热值密码输入错误次数
        IDataset ids = CommparaInfoQry.getCommpara("CSM", "3791", "PWDERRORS", "ZZZZ");
        //如果未配置则报错：没有配置密码错误限制参数!
        if (IDataUtil.isEmpty(ids))
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_549116);
        }
        //取出充值管控充值信息，充值管控记录表TF_F_PAYMEMNT_SURELY
        IDataset list = QueryRechargeBy10086Info.getPaymemntSurelyList(param.getString("X_CALL_EDMPHONECODE"), param.getString("SERIAL_NUMBER"), ids.getData(0).getInt("PARA_CODE1", 0));
        if (IDataUtil.isNotEmpty(list) && list.getData(0).getInt("DISTIMES") >= 0)//如果超过3791 PWDERRORS配置的密码输入错误次数PARA_CODE1，则处理
        {
            IData commpara = ids.getData(0);
            String paraCode3 = commpara.getString("PARA_CODE3");//提示方式：1短信提示   2：报错
            String paraCode5 = commpara.getString("PARA_CODE5");//发送短信的号码
            String paraCode4 = commpara.getString("PARA_CODE4","");//提示信息内容
            if ("1".equals(paraCode3))
            {
                sendSms(paraCode5, param.getString("SERIAL_NUMBER")+paraCode4);//发送短信
            }
            else if ("2".equals(paraCode3))
            {
                CSAppException.apperr(RechargeException.CRM_RECHARGE_1013);//周期内密码输入次数超过限制！
            }
        }

        //代充值监控功能
        IDataset csIds = new DatasetList();
        IDataset cslist = new DatasetList();
        
        //当接入手机号不同于被充值手机号，即代充的情况
        if (!serialNumber.equals(x_call_edmphonecode))
        {
        	//获取3791 INSPECTTIMES 10086热线热值代充值次数限制
            csIds = CommparaInfoQry.getCommpara("CSM", "3791", "INSPECTTIMES", "ZZZZ");
            //如果不存在配置则报错：没有配置代充值次数限制参数！
            if (IDataUtil.isEmpty(csIds))
            {
                CSAppException.apperr(RechargeException.CRM_RECHARGE_549117);
            }
            
            //取出当前号码的代充值监控信息，代充值管控记录表TF_F_SUPPLYPAY_INSPECT
            cslist = QueryRechargeBy10086Info.getSupplypayInspectList(param.getString("X_CALL_EDMPHONECODE"), param.getString("SERIAL_NUMBER"), param.getString("TRADE_EPARCHY_CODE"), csIds.getData(0).getInt("PARA_CODE1", 0));
            if (IDataUtil.isNotEmpty(cslist) && cslist.getData(0).getInt("DISTIMES") >= 0)//如果超过3791 INSPECTTIMES配置的密码输入错误次数PARA_CODE1，则处理
            {
                IData commpara = csIds.getData(0);
                String paraCode3 = commpara.getString("PARA_CODE3");//提示方式：1短信提示   2：报错
                String paraCode5 = commpara.getString("PARA_CODE5");//发送短信的号码
                String paraCode4 = commpara.getString("PARA_CODE4","");//提示信息内容
                if ("1".equals(paraCode3))
                {
                    sendSms(paraCode5, paraCode4);//发送短信
                }
                else if("2".equals(paraCode3))
                {
                	CSAppException.apperr(RechargeException.CRM_RECHARGE_1006);//周期内代充值次数超过限制！
                }
            }
        }
        
        int index = Integer.parseInt(cardPwd.substring(0,2));
    	if(43<index && index<77){//符合此条件的是电子有价卡，走电子有价卡充值流程 lihb3
    		result = evaluecardRecharge(param);
    		if("0000".equals(result.getString("X_RSPCODE"))){
    			result.put("X_RESULTCODE", "0000");
        		result.put("X_RESULTINFO", "受理成功");
    		}
    	}else{
            result = rechargeByPhoneVC(param);//调用webservice，并返回充值结果
    	}

        IData chnlResutl = callCHNLChargeTouchITF(param, result);//调用渠道接口记录接触信息

        result.put("X_RESULTCODE_CHNL", chnlResutl.getString("X_RESULTCODE"));//记录渠道接口记录接触信息结果
        result.put("X_RESULT_INFO_CHNL", chnlResutl.getString("X_RESULT_INFO"));//记录渠道接口记录接触信息结果
        
        //0000，充值成功
        if ("0000".equals(result.getString("X_RESULTCODE")) || "0".equals(result.getString("X_RESULTCODE")))
        {
            if (!param.getString("SERIAL_NUMBER").equals(param.getString("X_CALL_EDMPHONECODE")))
            {
                // 记录代充值记录 【cslist取出当前号码的代充值监控信息，代充值管控记录表TF_F_SUPPLYPAY_INSPECT】【csIds 3791 INSPECTTIMES 10086热线热值代充值次数限制 数据】 
                recordRechargeResult(param, "0", cslist, csIds);
            }

        }
        else if ("2005".equals(result.getString("X_RESULTCODE")) || "1001".equals(result.getString("X_RESULTCODE")))
        {
            // 记录充值卡密码错误【list 充值管控充值信息，充值管控记录表TF_F_PAYMEMNT_SURELY】【ids 3791 PWDERRORS 10086热线热值密码输入错误次数】
            recordRechargeResult(param, "1", list, ids);
        }

        //返回结果中包含
        //1：调用智能网充值结果 X_RESULTCODE、X_RESULTINFO、IN_FEE等     
        //2：调用渠道接口记录接触信息X_RESULTCODE_CHNL  X_RESULT_INFO_CHNL
        return result;
    }

    /**
     * 调用智能网充值前的过滤判断
     * */
    private IData rechargeByPhoneVC(IData data) throws Exception
    {
        IData result = new DataMap();
        IData inData = new DataMap();
        
        inData.put("IN_MODE_CODE", data.getString("IN_MODE_CODE"));
        inData.put("TRADE_CITY_CODE", data.getString("TRADE_CITY_CODE"));
        inData.put("TRADE_DEPART_ID", data.getString("TRADE_DEPART_ID"));
        inData.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
        inData.put("TRADE_EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
        inData.put("CARDPIN", data.getString("CARD_PASSWD"));//卡密
        inData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));//接入手机号
        inData.put("MSISDN", data.getString("X_CALL_EDMPHONECODE"));//被充值手机号
        
        //用来区分是10086或者1008633
        inData.put("CUST_CONTACT_ID", data.getString("CUST_CONTACT_ID","")); 
        
        //屏蔽下面代码原因：被充值用户可以是非本省用户，非本省用户没有资料；他网用户如电信联通手机号可以作为接入手机号，即发起用户也无资料
        /**
        //判断被充值号码是否可充值
        IData queryResult = UcaInfoQry.qryUserInfoBySn(inData.getString("MSISDN"));
		
        //无法找到被充值号码资料
        if (IDataUtil.isEmpty(queryResult))
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_800001);
        }
        
        //被充值号码非本省用户
        if ("06".equals(queryResult.getString("NET_TYPE_CODE")))
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_800002);
        }
        
        inData.put("USER_ID_B", queryResult.getString("USER_ID"));

        //获取发起用户USER_ID
        queryResult = UcaInfoQry.qryUserInfoBySn(inData.getString("SERIAL_NUMBER"));
        if (queryResult == null || queryResult.size() < 1)
        {
            CSAppException.apperr(RechargeException.CRM_RECHARGE_800001);
        }
        inData.put("USER_ID", queryResult.getString("USER_ID"));
        **/
        
        //调用WebService进行充值
        result = callService(inData);

        return result;

    }

    /**
     * 记录充值结果
     * data 被充值手机号、接入手机号、卡密等
     * type 0-代充值成功记录    1-充值卡密码错误记录
     * 当type=0时  【recordList取出当前号码的代充值监控信息，代充值管控记录表TF_F_SUPPLYPAY_INSPECT】【paramList 3791 INSPECTTIMES 10086热线热值代充值次数限制 数据】
     * 当type=1时  【recordList 充值管控充值信息，充值管控记录表TF_F_PAYMEMNT_SURELY】【paramList 3791 PWDERRORS 10086热线热值密码输入错误次数】
     * */
    public void recordRechargeResult(IData data, String type, IDataset recordList, IDataset paramList) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));//接入手机号
        param.put("X_CALL_EDMPHONECODE", data.getString("X_CALL_EDMPHONECODE"));//被充值手机号
        param.put("MAIN_SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));//接入手机号
        param.put("SURELY_SERIAL_NUMBER", data.getString("X_CALL_EDMPHONECODE"));//被充值手机号
        param.put("START_DATE", SysDateMgr.getSysTime());//系统当前时间
        param.put("END_DATE", SysDateMgr.addDays(paramList.getData(0).getInt("PARA_CODE2")));//当前时间加[3791 INSPECTTIMES 10086热线热值代充值次数限制数据的PARA_CODE2值]

        String tabName = null;

        if (type.equals("1"))//充值卡密码错误记录
        {
            tabName = "TF_F_PAYMEMNT_SURELY";
            param.put("CHANL_ID", data.getString("IN_MODE_CODE"));
            param.put("CARD_PWD", data.getString("CARD_PASSWD"));//错误的卡密
            param.put("ERROR_NUM", 1);
        }
        else if (type.equals("0"))//代充值成功记录
        {
            tabName = "TF_F_SUPPLYPAY_INSPECT";
            param.put("SUPPLY_NUM", 1);
            param.put("REMIND_TYPE", paramList.getData(0).getString("PARA_CODE3"));
            param.put("REMIND_CONTENT", paramList.getData(0).getString("PARA_CODE4"));
            param.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
            param.put("REMIND_NUMBER", data.getString("X_CALL_EDMPHONECODE"));//提醒对象是提醒被充值的号码？？？貌似不对吧
        }

        // 如果当前充值主体已有充值记录则修改，否则新增
        if (recordList != null && recordList.size() > 0)
        {
            param.put("LOG_ID", recordList.getData(0).getString("LOG_ID"));
            Dao.executeUpdateByCodeCode(tabName, "UPD_BY_LOG_ID", param);
        }
        else
        {
            param.put("LOG_ID", SeqMgr.getLogId());
            Dao.insert(tabName, param);
        }

    }

    //发送短信，如果是省外用户或他网手机号，则存在问题
    private void sendSms(String serialNumber, String noticeContent) throws Exception
    {
        IData smsData = new DataMap();
        smsData.put("RECV_OBJECT", serialNumber);
        smsData.put("NOTICE_CONTENT", noticeContent);
        smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(serialNumber).getString("USER_ID"));//如果是省外用户或他网手机号，则存在问题
        smsData.put("PRIORITY", "50");
        smsData.put("REMARK", "代充值周期内超过限制次数通知");
        SmsSend.insSms(smsData);
    }
    
    private void AsyncSendSms(String serialNumber, String noticeContent, String StrRemark)
    {
    	 Connection conn = null;
         try
         {
             conn = ConnectionManagerFactory.getConnectionManager().getConnection("crm1");//SessionManager.getInstance().getAsyncConnection("crm1");
             IData smsData = new DataMap();
             smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
             smsData.put("RECV_OBJECT", serialNumber);
             smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(serialNumber).getString("USER_ID"));
             smsData.put("NOTICE_CONTENT", noticeContent);
             smsData.put("REMARK", StrRemark);
             IData SmsData = SmsSend.prepareSmsData(smsData);
             String InSql = " INSERT INTO TI_O_SMS(SMS_NOTICE_ID,PARTITION_ID, " +
			          " EPARCHY_CODE,BRAND_CODE,IN_MODE_CODE,SMS_NET_TAG, " +
			          " CHAN_ID,SEND_OBJECT_CODE,SEND_TIME_CODE,SEND_COUNT_CODE, " +
			          " RECV_OBJECT_TYPE,RECV_OBJECT,RECV_ID,SMS_TYPE_CODE, " +
			          " SMS_KIND_CODE,NOTICE_CONTENT_TYPE,NOTICE_CONTENT,REFERED_COUNT, " +
			          " FORCE_REFER_COUNT,FORCE_OBJECT, " +
			          " FORCE_START_TIME,FORCE_END_TIME, " +
			          " SMS_PRIORITY, " +
			          " REFER_TIME, " +
			          " REFER_STAFF_ID,REFER_DEPART_ID," +
			          " DEAL_TIME," +
			          " DEAL_STAFFID,DEAL_DEPARTID," +
			          " DEAL_STATE, " +
			          " REMARK,REVC1,REVC2,REVC3,REVC4,MONTH,DAY) ";
             InSql += "VALUES('"  + SmsData.getString("SMS_NOTICE_ID") + "','" + SmsData.getString("PARTITION_ID");
             InSql +=   "','" + SmsData.getString("EPARCHY_CODE") + "','" + SmsData.getString("BRAND_CODE", "") + "','"  + SmsData.getString("IN_MODE_CODE") + "','" + SmsData.getString("SMS_NET_TAG");
             InSql +=   "','" + SmsData.getString("CHAN_ID") + "','" + SmsData.getString("SEND_OBJECT_CODE") + "','"  + SmsData.getString("SEND_TIME_CODE") + "','" + SmsData.getString("SEND_COUNT_CODE");
             InSql +=   "','" + SmsData.getString("RECV_OBJECT_TYPE") + "','" + SmsData.getString("RECV_OBJECT") + "','"  + SmsData.getString("RECV_ID") + "','" + SmsData.getString("SMS_TYPE_CODE");
             InSql +=   "','" + SmsData.getString("SMS_KIND_CODE") + "','" + SmsData.getString("NOTICE_CONTENT_TYPE") + "','"  + SmsData.getString("NOTICE_CONTENT") + "','" + SmsData.getString("REFERED_COUNT");
             InSql +=   "','" + SmsData.getString("FORCE_REFER_COUNT") + "','" + SmsData.getString("FORCE_OBJECT") + "','"  + SmsData.getString("FORCE_START_TIME", "") + "','" + SmsData.getString("FORCE_END_TIME", "");
             InSql +=   "','" + SmsData.getString("SMS_PRIORITY") + "'," + "to_date('" + SmsData.getString("REFER_TIME") + "', 'yyyy-mm-dd hh24:mi:ss')" + ",'"  + SmsData.getString("REFER_STAFF_ID") + "','" + SmsData.getString("REFER_DEPART_ID");
             InSql +=   "'," + "to_date('"+ SmsData.getString("DEAL_TIME") + "', 'yyyy-mm-dd hh24:mi:ss')" + ",'" + SmsData.getString("DEAL_STAFFID", "") + "','"  + SmsData.getString("DEAL_DEPARTID", "") + "','" + SmsData.getString("DEAL_STATE");
             InSql +=   "','" + SmsData.getString("REMARK", "") + "','" + SmsData.getString("REVC1", "") + "','"  + SmsData.getString("REVC2", "") + "','" + SmsData.getString("REVC3", "");
             InSql +=   "','" + SmsData.getString("REVC4", "") + "','" + SmsData.getString("MONTH") + "','"  + SmsData.getString("DAY") + "')" ;
             Statement st = conn.createStatement();
             boolean bRes = st.execute(InSql);
             if(!bRes)
             {
            	 CSAppException.apperr(RechargeException.CRM_RECHARGE_800004, "插下发短信失败!");
             }
             conn.commit();
             st.close();
         }
         catch (Exception ex)
         {
             ex.printStackTrace();
             try
             {
                 if(conn!=null){
                     conn.rollback();
                 }
             }
             catch (Exception ex1)
             {
                 ex1.printStackTrace();
             }
         }
         finally
         {
             try
             {
                 if(conn!=null){
                     conn.close();
                 }
             }
             catch (Exception ex)
             {
                 ex.printStackTrace();
             }
         }
    }
    
    
    private void recordCallLog(IData data, IData sopQueryResult, IData result) throws Exception
    {
    	
    	Connection conn = null;
    	
        try
        {
        	conn = ConnectionManagerFactory.getConnectionManager().getConnection("crm1");//SessionManager.getInstance().getAsyncConnection("crm1");
        	
            String inModeCode=data.getString("IN_MODE_CODE","");
            if(inModeCode==null||inModeCode.trim().equals("")){
            	inModeCode="1";
            }
            
            StringBuilder sql = new StringBuilder();
            sql.append(" INSERT INTO PREPAIDCARD_CALL_LOG( ");
            sql.append(" ORDER_ID, SERIAL_NUMBER, USER_ID, SERIAL_NUMBER_B, USER_ID_B, CARD_PIN, ACCEPT_DATE, ");
            sql.append(" ACCEPT_MONTH, SEND_INFO, OPERATION_CODE, USER_AMOUNT, CARD_PIN_AMOUNT, ACTIVEDAYS, ");
            sql.append(" SEQUENCE, ERROR_DESCRIPTION, X_RESULTCODE, X_RESULTINFO, SMS_NOTICE_ID, IN_MODE_CODE, RSRV_STR1,RSRV_STR2,RSRV_STR3) ");
            sql.append(" VALUES ( ");
            sql.append(sopQueryResult.getString("ORDER_ID", ""));
            sql.append(", '");
            sql.append(data.getString("SERIAL_NUMBER", ""));
            sql.append("', ");
            sql.append(data.getString("USER_ID", "0"));
            sql.append(", '");
            sql.append(data.getString("MSISDN", ""));
            sql.append("', ");
            sql.append(data.getString("USER_ID_B", ""));
            sql.append(", '");
            sql.append(data.getString("CARDPIN", ""));
            sql.append("', TO_DATE('");
            sql.append(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
            sql.append("', 'yyyyMMddHH24miss'), ");
            sql.append(SysDateMgr.getCurMonth());
            sql.append(", '");
            sql.append(sopQueryResult.getString("SEND_INFO", ""));
            sql.append("', '");
            sql.append(sopQueryResult.getString("OPERATION_CODE", ""));
            sql.append("', '");
            sql.append(sopQueryResult.getString("AMOUNT", ""));
            sql.append("', '");
            sql.append(sopQueryResult.getString("COUNTTOTAL", ""));
            sql.append("', '");
            sql.append(sopQueryResult.getString("ACTIVEDAYS", ""));
            sql.append("', '");
            sql.append(sopQueryResult.getString("SEQUENCE", ""));
            sql.append("', '");
            sql.append(sopQueryResult.getString("ERROR_DESCRIPTION", ""));
            sql.append("', '");
            sql.append(result.getString("X_RESULTCODE", ""));
            sql.append("', '");
            sql.append(result.getString("X_RESULTINFO", ""));
            sql.append("', '");
            sql.append(data.getString("SMS_NOTICE_ID", ""));
            sql.append("', '");
            sql.append(inModeCode);
            sql.append("', '");
            sql.append(data.getString("RSRV_STR1", "0"));
            sql.append("', '");
            sql.append(data.getString("RSRV_STR2", "0"));
            sql.append("', '");
            sql.append(data.getString("CUST_CONTACT_ID", "0"));
            sql.append("' ");
            sql.append(" ) ");
            
            Statement st = conn.createStatement();
            st.execute(sql.toString());
            conn.commit();
            st.close();
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                if(conn!=null){
                    conn.rollback();
                }
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();
            }
        }
        finally
        {
            try
            {
                if(conn!=null){
                    conn.close();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * 电子有价卡充值 lihb3 20161130
     * @param data
     * @return 
     * @throws Exception 
     */
    public IData evaluecardRecharge(IData data) throws Exception {
		
    	IData result = new DataMap(); //返回结果

		String main_serial_number = data.getString("SERIAL_NUMBER", "");//接入手机号
		String cardPwd = data.getString("CARD_PASSWD");
		String serial_number = data.getString("X_CALL_EDMPHONECODE");//被充值号码		
				       
		if ( !TassHnydAPI.openHsm() ) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "无法连接到加密机！");
		}
		//将充值密码调用加密机加密再传给有价卡平台 2为有价卡平台公钥索引 lihb3 20161109
    	cardPwd = TassHnydAPI.encryptByCenterPK("2", cardPwd);
       	
    	String inMode = data.getString("IN_MODE_CODE","0");
    	String channlType = "01";
    	if("1".equals(inMode)){
    		channlType = "03";
    	}
	    IData inData = new DataMap();
	    inData.put("SURELY_SERIAL_NUMBER", serial_number);// 被充值手机号
		inData.put("CARD_PASSWORD", 	cardPwd);// 卡密码
		inData.put("MAIN_SERIAL_NUMBER", 	main_serial_number);//主叫号
		inData.put("CHANNEL_TYPE", 			channlType);
		inData.put("CARD_TYPE", 			"00");
		
		String homePro = "898";
		inData.put("HOME_PRO", 			homePro);
		inData.put("TRANSACTIONID", 	homePro+SysDateMgr.getSysDate("yyyyMMddHHmmss")+"000001");
		inData.put("KIND_ID", 			"bPayment_BOSS_0_0");
		inData.put("ACTION_TIME", 		SysDateMgr.getSysDate("yyyyMMddHHmmss"));

		try{
			result = IBossCall.callHttpIBOSS4("IBOSS", inData).getData(0);
		}catch(Exception ex){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！"+ex.getMessage());
		}
		
		return result;
	}
    
}
