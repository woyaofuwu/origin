
package com.asiainfo.veris.crm.order.soa.person.busi.rechargedcard;

import java.net.URL;
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
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CardSaleException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSInputData;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSServNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSVarNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.NeaSoapBindingStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.SoapInputXml;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOPStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOP_ServiceLocator;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.holders.CWSVarNodeHolder;

public class RechargedCardBean extends CSBizBean
{

    private IData callService(IData data) throws Exception
    {
        IData result = new DataMap();
        String url = "http://10.200.141.52:9989";
        String switchCode = "SMPVC";
        String srvCode = "TP001";
        String paramCode0 = "V290"; // CardPin
        String paramCode1 = "G004"; // MSISDN
        String paramCode2 = "V291"; // SerialNumber
        String paramCode3 = "V940";

        int timeOut = 60000; // 调用SMPVC超时时间
        
        String checktype="0";	//默认不使用新调用方式

        String sms4own = "尊敬的客户，您于%chargeTime%成功充值%money%。中国移动";
        String sms4other1 = "尊敬的客户，您于%chargeTime%为%msisdn%成功充值%money%。中国移动";
        String sms4other2 = "尊敬的客户，%serialNumber%于%chargeTime%为您成功充值%money%。中国移动";

        String paramValue;

        try
        {
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "URL");
            if (StringUtils.isNotBlank(paramValue))
                url = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SWITCH_CODE");
            if (StringUtils.isNotBlank(paramValue))
                switchCode = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SRV_CODE");
            if (StringUtils.isNotBlank(paramValue))
                srvCode = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CARDPIN_PARAM_CODE");
            if (StringUtils.isNotBlank(paramValue))
                paramCode0 = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "MSISDN_PARAM_CODE");
            if (StringUtils.isNotBlank(paramValue))
                paramCode1 = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SERIALNUMBER_PARAM_CODE");
            if (StringUtils.isNotBlank(paramValue))
                paramCode2 = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TRADETYPE_PARAM_CODE");
            if (StringUtils.isNotBlank(paramValue))
                paramCode3 = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SMS_FOR_OWN");
            if (StringUtils.isNotBlank(paramValue))
                sms4own = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SMS_FOR_OTHER1");
            if (StringUtils.isNotBlank(paramValue))
                sms4other1 = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SMS_FOR_OTHER2");
            if (StringUtils.isNotBlank(paramValue))
                sms4other2 = paramValue;

            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TIME_OUT");
            if (StringUtils.isNotBlank(paramValue))
                timeOut = Integer.parseInt(paramValue);
            
            paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CHECK_TYPE");
            if (StringUtils.isNotBlank(paramValue))
                {checktype = paramValue;}
            

        }
        catch (Exception e)
        {
            CSAppException.apperr(CardSaleException.CRM_CardSaleInfo_22);
        }

        CWSServNode servNode = new CWSServNode();

        servNode.setMStrServName(srvCode);
        CWSServNode mVServList[] = new CWSServNode[1];
        mVServList[0] = servNode;

        CWSVarNode mVVarList[] = new CWSVarNode[4];

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

        String tradeType = getVisit().getInModeCode();
        if ("5".equals(tradeType))
        {
            tradeType = "10";
        }
        else if ("2".equals(tradeType))
        {
            tradeType = "11";
        }
        else if ("0".equals(tradeType))
        {
            tradeType = "12";
        }
        else if ("L".equals(tradeType))
        {
            tradeType = "13";
        }
        else if ("X".equals(tradeType))
        {
            tradeType = "14";
        }
        else if ("1".equals(tradeType))
        {
            tradeType = "15";
        }
        else
        {
            tradeType = "11";// 如匹配不上，按网上营业厅处理
        }
        CWSVarNode varNode3 = new CWSVarNode();
        varNode3.setMStrName(paramCode3);
        varNode3.setMStrValue(tradeType);
        mVVarList[3] = varNode3;

        CWSInputData inputData = new CWSInputData();
        inputData.setMStrOrderID(getStrOrderId());
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
        
        //新联指调用
        String operationResult = null;
        String errorDescription = null;
        String cmdList = null;
        java.util.List<Element> queryResultList = null;

        IData sopQueryResult = new DataMap();
        sopQueryResult.put("ORDER_ID", inputData.getMStrOrderID());
        String smsId = "";
        try
        {
     		if(checktype.equals("1")){
    			SoapInputXml soapinput = new SoapInputXml();
    			URL u=new URL(url);
    			NeaSoapBindingStub neasop=new NeaSoapBindingStub(u,new Service());
    			String xmlParamStr = null;
    			
    			String serlial_number = data.getString("SERIAL_NUMBER");
				String priority = "50";
				xmlParamStr = soapinput.receiveMobileRecharge(getStrOrderId(),serlial_number,priority,switchCode,mVServList,mVVarList);
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
            // 模拟服务器返回值进行测试
            // TestCallWSSOP(inputData, m_strOrderID, m_nOperationResult, m_strFinishTime, m_strErrorDescription,
            // m_nCMDCount, m_strAutoCMDList, m_vQueryResult);

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
                String notice_content;
                if (data.getString("SERIAL_NUMBER").equals(data.getString("MSISDN")))
                {
                    // 为自己充值
                    notice_content = sms4own;
                    notice_content = notice_content.replace("%chargeTime%", getChargeTime());
                    notice_content = notice_content.replace("%money%", converYuan(cardPinAmount));
                    smsId = sendSMS(data, data.getString("SERIAL_NUMBER"), data.getString("USER_ID"), notice_content);
                }
                else
                {
                    // 为他人充值
                    String chargeTime = getChargeTime();
                    String money = converYuan(cardPinAmount);

                    // 为发起号码下发短信
                    notice_content = sms4other1;
                    notice_content = notice_content.replace("%chargeTime%", chargeTime);
                    notice_content = notice_content.replace("%money%", money);
                    notice_content = notice_content.replace("%msisdn%", data.getString("MSISDN"));
                    smsId = sendSMS(data, data.getString("SERIAL_NUMBER"), data.getString("USER_ID"), notice_content);

                    // 为被充值号码下发短信
                    notice_content = sms4other2;
                    notice_content = notice_content.replace("%chargeTime%", chargeTime);
                    notice_content = notice_content.replace("%money%", money);
                    notice_content = notice_content.replace("%serialNumber%", data.getString("SERIAL_NUMBER"));
                    smsId = smsId + "," + sendSMS(data, data.getString("MSISDN"), data.getString("USER_ID_B"), notice_content);
                }
                result.put("X_RESULTCODE", "0");
                result.put("X_RESULTINFO", "充值成功");
            }
            else
            {
                // 充值失败
                String regex = "RETN=[\\d]+,DESC=[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]+";// 匹配[RETN=1001,DESC=充值卡不存在]类似字符串
                String str = new String(m_strErrorDescription.value.getBytes("ISO-8859-1"), "GB2312");
                Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = p.matcher(str);
                if (matcher.find())
                {
                    String tmpStr = matcher.group(0);
                    String[] tmpStrs = tmpStr.split(",");

                    if (tmpStrs.length == 2)
                    {
                        result.put("X_RESULTCODE", "60" + tmpStrs[0].substring(tmpStrs[0].indexOf("=") + 1));
                        result.put("X_RESULTINFO", tmpStrs[1].substring(tmpStrs[1].indexOf("=") + 1));
                        result.put("X_RSPTYPE", "2");// add by ouyk
                        result.put("X_RSPCODE", "2998");// add by ouyk
                    }
                    else
                    {
                        result.put("X_RESULTCODE", "800003");
                        result.put("X_RESULTINFO", "返回信息解析错误：" + tmpStr);
                        result.put("X_RSPTYPE", "2");// add by ouyk
                        result.put("X_RSPCODE", "2998");// add by ouyk
                    }
                }
                else
                {
                    result.put("X_RESULTCODE", "800003");
                    result.put("X_RESULTINFO", "返回信息解析错误：" + str);
                    result.put("X_RSPTYPE", "2");// add by ouyk
                    result.put("X_RSPCODE", "2998");// add by ouyk
                }
            }

        }
        catch (Exception e)
        {
            // 调用智能网接口失败
            e.printStackTrace();
            result.put("X_RESULTCODE", "800004");
            result.put("X_RESULTINFO", "调用智能网接口错误：" + e.getMessage());
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
        }
        result.put("CHARGE_TIME", getChargeTime());
        data.put("SMS_NOTICE_ID", smsId);

        // 记录日志
        recordCallLog(data, sopQueryResult, result);
        return result;
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
        return yuan + "元";
    }

    private String getChargeTime()
    {
        return DateFormatUtils.format(new java.util.Date(), "yyyy年MM月dd日HH时mm分ss秒");
    }

    private String getStrOrderId()throws Exception
    {
//        return DateFormatUtils.format(new java.util.Date(), "yyyyMMddHHmmssSSS");
    	return SeqMgr.getOrderId();
    }

    public IData recharge(IData input) throws Exception
    {
        /*
         * 入参检查 IDataUtil.chkParam(input, "IN_MODE_CODE"); IDataUtil.chkParam(input, "TRADE_CITY_CODE");
         * IDataUtil.chkParam(input, "TRADE_DEPART_ID"); IDataUtil.chkParam(input, "TRADE_STAFF_ID");
         * IDataUtil.chkParam(input, "TRADE_EPARCHY_CODE");
         */
        IDataUtil.chkParam(input, "CARDPIN");
        IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 发起方手机号码
        IDataUtil.chkParam(input, "MSISDN"); // 被充值手机号码
        
        IData result = new DataMap();
        
        int index = Integer.parseInt(input.getString("CARDPIN").substring(0,2));
    	if(43<index && index<77){//符合此条件的是电子有价卡，走电子有价卡充值流程 lihb3
    		return evaluecardRecharge(input);
    	}

        // 判断被充值号码是否可充值
        IData param = new DataMap();
        param.put("REMOVE_TAG", "0");
        param.put("SERIAL_NUMBER", input.get("MSISDN"));

        IData msisdn = UcaInfoQry.qryUserInfoBySn(input.getString("MSISDN"));
        
        // 无法找到被充值号码资料
        if (IDataUtil.isEmpty(msisdn))
        {
            result.put("X_RESULTCODE", "800001");
            result.put("X_RESULTINFO", "无法找到被充值号码资料！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        if (StringUtils.equals("06", msisdn.getString("NET_TYPE_CODE")))
        {
            result.put("X_RESULTCODE", "800002");
            result.put("X_RESULTINFO", "被充值号码非本省用户！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        input.put("USER_ID_B", msisdn.getString("USER_ID"));

        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            result.put("X_RESULTCODE", "800001");
            result.put("X_RESULTINFO", "无法找到发起充值号码资料！");
            result.put("X_RSPTYPE", "2");// add by ouyk
            result.put("X_RSPCODE", "2998");// add by ouyk
            return result;
        }
        input.put("USER_ID", userInfo.getString("USER_ID"));

        return callService(input);
    }

    private void recordCallLog(IData data, IData sopQueryResult, IData result) throws Exception
    {
        try
        {
            IData param = new DataMap();
            param.put("ORDER_ID", sopQueryResult.getString("ORDER_ID", ""));
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
            param.put("USER_ID", data.getString("USER_ID", ""));
            param.put("SERIAL_NUMBER_B", data.getString("MSISDN", ""));
            param.put("USER_ID_B", data.getString("USER_ID_B", ""));
            param.put("CARD_PIN", data.getString("CARDPIN", ""));
            param.put("ACCEPT_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
            param.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
            param.put("SEND_INFO", sopQueryResult.getString("SEND_INFO", ""));
            param.put("OPERATION_CODE", sopQueryResult.getString("OPERATION_CODE", ""));
            param.put("USER_AMOUNT", sopQueryResult.getString("AMOUNT", ""));
            param.put("CARD_PIN_AMOUNT", sopQueryResult.getString("COUNTTOTAL", ""));
            param.put("ACTIVEDAYS", sopQueryResult.getString("ACTIVEDAYS", ""));
            param.put("SEQUENCE", sopQueryResult.getString("SEQUENCE", ""));
            param.put("ERROR_DESCRIPTION", sopQueryResult.getString("ERROR_DESCRIPTION", ""));
            param.put("X_RESULTCODE", result.getString("X_RESULTCODE", ""));
            param.put("X_RESULTINFO", result.getString("X_RESULTINFO", ""));
            param.put("SMS_NOTICE_ID", data.getString("SMS_NOTICE_ID", ""));
            param.put("IN_MODE_CODE", getVisit().getInModeCode());

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO PREPAIDCARD_CALL_LOG(" + "ORDER_ID, SERIAL_NUMBER, USER_ID, SERIAL_NUMBER_B, USER_ID_B, CARD_PIN, ACCEPT_DATE," + " ACCEPT_MONTH, SEND_INFO, OPERATION_CODE, USER_AMOUNT, CARD_PIN_AMOUNT, ACTIVEDAYS, "
                    + "SEQUENCE, ERROR_DESCRIPTION, X_RESULTCODE, X_RESULTINFO, SMS_NOTICE_ID, IN_MODE_CODE) " + "VALUES (" + ":ORDER_ID, :SERIAL_NUMBER, :USER_ID, :SERIAL_NUMBER_B, :USER_ID_B, :CARD_PIN, "
                    + "TO_DATE(:ACCEPT_DATE,'yyyyMMddHH24miss'), :ACCEPT_MONTH, :SEND_INFO, :OPERATION_CODE," + " :USER_AMOUNT, :CARD_PIN_AMOUNT, :ACTIVEDAYS, :SEQUENCE, :ERROR_DESCRIPTION, :X_RESULTCODE, "
                    + ":X_RESULTINFO, :SMS_NOTICE_ID, :IN_MODE_CODE)");
            Dao.executeUpdate(sql, param);

        }
        catch (Exception e)
        {
            CSAppException.apperr(CardSaleException.CRM_CardSaleInfo_24);
        }
    }

    private String sendSMS(IData data, String serialNumber, String userID, String notice_content) throws Exception
    {
        IData smsParam = new DataMap();
        try
        {

            // 插入短信表
            String seq = SeqMgr.getSmsSendId();
            long seq_id = Long.parseLong(seq);
            long partition_id = seq_id % 1000;

            smsParam.put("SEQ_ID", seq_id);
            smsParam.put("PARTITION_ID", partition_id);
            smsParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            smsParam.put("IN_MODE_CODE", getVisit().getInModeCode());
            smsParam.put("SERIAL_NUMBER", serialNumber);
            smsParam.put("USER_ID", userID);
            smsParam.put("NOTICE_CONTENT", notice_content);
            smsParam.put("PRIORITY", "50");
            smsParam.put("STAFF_ID", getVisit().getStaffId());
            smsParam.put("DEPART_ID", getVisit().getDepartId());
            smsParam.put("REMARK", "充值卡充值");

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO TI_O_SMS(" + "SMS_NOTICE_ID,PARTITION_ID,SEND_COUNT_CODE,REFERED_COUNT,EPARCHY_CODE," + "IN_MODE_CODE,CHAN_ID,RECV_OBJECT_TYPE,RECV_OBJECT,RECV_ID,SMS_TYPE_CODE,SMS_KIND_CODE,"
                    + "NOTICE_CONTENT_TYPE,NOTICE_CONTENT,FORCE_REFER_COUNT,SMS_PRIORITY,REFER_TIME," + "REFER_STAFF_ID,REFER_DEPART_ID,DEAL_TIME,DEAL_STATE,REMARK,SEND_TIME_CODE,SEND_OBJECT_CODE) " + "VALUES ("
                    + ":SEQ_ID,:PARTITION_ID,'1','0',:EPARCHY_CODE,:IN_MODE_CODE,'11','00',:SERIAL_NUMBER,:USER_ID,'20'," + "'02','0',:NOTICE_CONTENT,1,:PRIORITY,SYSDATE,:STAFF_ID,:DEPART_ID,SYSDATE,'15',:REMARK,1,6)");
            Dao.executeUpdate(sql, smsParam);
        }
        catch (Exception e)
        {
            CSAppException.apperr(CardSaleException.CRM_CardSaleInfo_23);
        }
        return smsParam.getString("SEQ_ID", "");
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
		String cardPwd = data.getString("CARDPIN");
		String serial_number = data.getString("MSISDN");//被充值号码		
				       
		if ( !TassHnydAPI.openHsm() ) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "无法连接到加密机！");
		}
		//将充值密码调用加密机加密再传给有价卡平台 2为有价卡平台公钥索引 lihb3 20161109
   	cardPwd = TassHnydAPI.encryptByCenterPK("2", cardPwd);
      		
   	String inMode = data.getString("IN_MODE_CODE","0");
   	String channlType = "01";
   	if("5".equals(inMode)){
   		channlType = "04";
   	}else if("2".equals(inMode)){
   		channlType = "08";
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
