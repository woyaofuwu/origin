/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.axis.client.Service;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CardSaleException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.userident.UserIdentBean;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSInputData;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSServNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.CWSVarNode;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.NeaSoapBindingStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.SoapInputXml;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOPStub;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.WSSOP_ServiceLocator;
import com.asiainfo.veris.crm.order.soa.person.wssoap.rechargedcard.holders.CWSVarNodeHolder;

/**
 * @CREATED by gongp@2014-7-30 修改历史 Revision 2014-7-30 上午10:32:49
 */
public class ValueCardMgrIntfBean extends CSBizBean
{
    private static final transient Logger log = Logger.getLogger(ValueCardMgrIntfBean.class);

    @SuppressWarnings("unchecked")
    private IData callService(IData data) throws Exception
    {
        IData result = new DataMap();
        String url = "http://10.200.141.52:9989";
        String switchCode = "SMPVC";
        String srvCode = "TP001";
        String paramCode0 = "V290";// CardPin
        String paramCode1 = "G004";// MSISDN
        String paramCode2 = "V291";// SerialNumber
        String paramCode3 = "V940";// TRADETYPE

        int timeOut = 60000;// 调用SMPVC超时时间
        
        String checktype="0";	//默认不使用新调用方式

        String sms4own = "尊敬的客户，您于%chargeTime%成功充值%money%。中国移动";
        String sms4other1 = "尊敬的客户，您于%chargeTime%为%msisdn%成功充值%money%。中国移动";
        String sms4other2 = "尊敬的客户，%serialNumber%于%chargeTime%为您成功充值%money%。中国移动";

        String paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "URL");
        if (paramValue != null && !"".equals(paramValue))
        {
            url = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SWITCH_CODE");
        if (paramValue != null && !"".equals(paramValue))
        {
            switchCode = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SRV_CODE");
        if (paramValue != null && !"".equals(paramValue))
        {
            srvCode = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CARDPIN_PARAM_CODE");
        if (paramValue != null && !"".equals(paramValue))
        {
            paramCode0 = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "MSISDN_PARAM_CODE");
        if (paramValue != null && !"".equals(paramValue))
        {
            paramCode1 = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SERIALNUMBER_PARAM_CODE");
        if (paramValue != null && !"".equals(paramValue))
        {
            paramCode2 = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TRADETYPE_PARAM_CODE");
        if (paramValue != null && !"".equals(paramValue))
        {
            paramCode3 = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SMS_FOR_OWN");
        if (paramValue != null && !"".equals(paramValue))
        {
            sms4own = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SMS_FOR_OTHER1");
        if (paramValue != null && !"".equals(paramValue))
        {
            sms4other1 = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "SMS_FOR_OTHER2");
        if (paramValue != null && !"".equals(paramValue))
        {
            sms4other2 = paramValue;
        }

        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "CHECK_TYPE");
        if (StringUtils.isNotBlank(paramValue))
            {checktype = paramValue;}
        
        paramValue = StaticUtil.getStaticValue("PREPAID_CARD_SRV", "TIME_OUT");

        timeOut = Integer.parseInt(paramValue);

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

        // REQ201309110034 关于部署10086热线充值融合改造工作的通知
        /*
         * 业务类型，可定义各种充值渠道，取值如下： 10：短信营业厅 IN_MODE_CODE:5 11：网上营业厅 IN_MODE_CODE:2 12：实体营业厅 IN_MODE_CODE:0 13：WAP营业厅
         * IN_MODE_CODE:L 14：自助终端 IN_MODE_CODE:X 15：10086热线 IN_MODE_CODE:1
         */
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
        // REQ201309110034 关于部署10086热线充值融合改造工作的通知

        CWSInputData inputData = new CWSInputData();
        inputData.setMStrOrderID(SysDateMgr.getSysDate("yyyyMMddHHmmssSSS"));
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
				xmlParamStr = soapinput.receiveMobileRecharge(inputData.getMStrOrderID(),serlial_number,priority,switchCode,mVServList,mVVarList);
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
                    notice_content = notice_content.replace("%chargeTime%", SysDateMgr.getSysDate("yyyy年MM月dd日HH时mm分ss秒"));
                    notice_content = notice_content.replace("%money%", FeeUtils.Fen2Yuan(cardPinAmount));
                    smsId = sendSMS(data, data.getString("SERIAL_NUMBER"), data.getString("USER_ID"), notice_content);
                }
                else
                {
                    // 为他人充值
                    String chargeTime = SysDateMgr.getSysDate("yyyy年MM月dd日HH时mm分ss秒");
                    String money = FeeUtils.Fen2Yuan(cardPinAmount);

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
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                    }
                    else
                    {
                        result.put("X_RESULTCODE", "800003");
                        result.put("X_RESULTINFO", "返回信息解析错误：" + tmpStr);
                        result.put("X_RSPTYPE", "2");
                        result.put("X_RSPCODE", "2998");
                    }
                }
                else
                {
                    result.put("X_RESULTCODE", "800003");
                    result.put("X_RESULTINFO", "返回信息解析错误：" + str);
                    result.put("X_RSPTYPE", "2");
                    result.put("X_RSPCODE", "2998");

                }
            }

        }
        catch (Exception e)
        {
            // 调用智能网接口失败
            e.printStackTrace();
            result.put("X_RESULTCODE", "800004");
            result.put("X_RESULTINFO", "调用智能网接口错误：" + e.getMessage());
            result.put("X_RSPTYPE", "2");
            result.put("X_RSPCODE", "2998");
        }
        result.put("CHARGE_TIME", SysDateMgr.getSysDate("yyyy年MM月dd日HH时mm分ss秒"));
        data.put("SMS_NOTICE_ID", smsId);

        // 记录日志
        recordCallLog(data, sopQueryResult, result);
        return result;
    }

    private UcaData getNormalUca(String sn, String errInfo) throws Exception
    {

        UcaData ucaData = null;
        try
        {
            ucaData = UcaDataFactory.getNormalUca(sn);
        }
        catch (Exception e)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, errInfo);
        }

        return ucaData;
    }

    public IData recharge(IData data) throws Exception
    {

        // 入参检查
        /*
         * IDataUtil.chkParam(data, "IN_MODE_CODE"); IDataUtil.chkParam(data, "TRADE_CITY_CODE");
         * IDataUtil.chkParam(data, "TRADE_DEPART_ID"); IDataUtil.chkParam(data, "TRADE_STAFF_ID");
         * IDataUtil.chkParam(data, "TRADE_EPARCHY_CODE");
         */

        IDataUtil.chkParam(data, "CARDPIN");
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "MSISDN");

        // 判断被充值号码是否可充值
        UcaData ucaData = getNormalUca(data.getString("MSISDN"), "800001:无法找到被充值号码资料！");

        if ("06".equals(ucaData.getUser().getNetTypeCode()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1176);
        }

        data.put("USER_ID_B", ucaData.getUserId());

        // 获取发起用户USER_ID
        UcaData ucaDataMain = getNormalUca(data.getString("SERIAL_NUMBER"), "800001:无法找到发起充值号码资料！");

        data.put("USER_ID", ucaDataMain.getUserId());

        return this.callService(data);
    }

    @SuppressWarnings("unchecked")
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
            param.put("ACCEPT_DATE", sopQueryResult.getString("ORDER_ID", "").substring(0, 14));
            param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(sopQueryResult.getString("ORDER_ID")));
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

            String sql = "INSERT INTO PREPAIDCARD_CALL_LOG(ORDER_ID, SERIAL_NUMBER, USER_ID, SERIAL_NUMBER_B, USER_ID_B, CARD_PIN, ACCEPT_DATE, ACCEPT_MONTH, SEND_INFO, OPERATION_CODE, USER_AMOUNT, CARD_PIN_AMOUNT, ACTIVEDAYS, SEQUENCE, ERROR_DESCRIPTION, X_RESULTCODE, X_RESULTINFO, SMS_NOTICE_ID, IN_MODE_CODE) VALUES (:ORDER_ID, :SERIAL_NUMBER, :USER_ID, :SERIAL_NUMBER_B, :USER_ID_B, :CARD_PIN, TO_DATE(:ACCEPT_DATE,'yyyyMMddHH24miss'), :ACCEPT_MONTH, :SEND_INFO, :OPERATION_CODE, :USER_AMOUNT, :CARD_PIN_AMOUNT, :ACTIVEDAYS, :SEQUENCE, :ERROR_DESCRIPTION, :X_RESULTCODE, :X_RESULTINFO, :SMS_NOTICE_ID, :IN_MODE_CODE)";
            Dao.executeUpdate(new StringBuilder(sql), param);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(CardSaleException.CRM_CardSaleInfo_24);
        }
    }

    @SuppressWarnings("unchecked")
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
            smsParam.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE"));
            smsParam.put("IN_MODE_CODE", getVisit().getInModeCode());
            smsParam.put("SERIAL_NUMBER", serialNumber);
            smsParam.put("USER_ID", userID);
            smsParam.put("NOTICE_CONTENT", notice_content);
            smsParam.put("PRIORITY", "50");
            smsParam.put("STAFF_ID", getVisit().getStaffId());
            smsParam.put("DEPART_ID", getVisit().getDepartId());
            smsParam.put("REMARK", "充值卡充值");

            String sql = "INSERT INTO TI_O_SMS(SMS_NOTICE_ID,PARTITION_ID,SEND_COUNT_CODE,REFERED_COUNT,EPARCHY_CODE,IN_MODE_CODE,CHAN_ID,RECV_OBJECT_TYPE,RECV_OBJECT,RECV_ID,SMS_TYPE_CODE,SMS_KIND_CODE,NOTICE_CONTENT_TYPE,NOTICE_CONTENT,FORCE_REFER_COUNT,SMS_PRIORITY,REFER_TIME,REFER_STAFF_ID,REFER_DEPART_ID,DEAL_TIME,DEAL_STATE,REMARK,SEND_TIME_CODE,SEND_OBJECT_CODE) VALUES (:SEQ_ID,:PARTITION_ID,'1','0',:EPARCHY_CODE,:IN_MODE_CODE,'11','00',:SERIAL_NUMBER,:USER_ID,'20','02','0',:NOTICE_CONTENT,1,:PRIORITY,SYSDATE,:STAFF_ID,:DEPART_ID,SYSDATE,'15',:REMARK,1,6)";
            Dao.executeUpdate(new StringBuilder(sql), smsParam);
        }
        catch (Exception e)
        {
            CSAppException.apperr(CardSaleException.CRM_CardSaleInfo_23);
        }
        return smsParam.getString("SEQ_ID", "");
    }

    public IData valuecardRechargeRegByPhone(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "CARD_PWD");
        IDataUtil.chkParam(data, "PHONE_NUM");

        String serialNumber = data.getString("PHONE_NUM", "");

        data.put("MSISDN", serialNumber);
        data.put("CARDPIN", data.getString("CARD_PWD"));
        data.put("SERIAL_NUMBER", data.getString("PAY_NUM", ""));

        if (StringUtils.isNotBlank(data.getString("IDENT_CODE", "")))
        {// 身份凭证传入时则开始校验
        	String bizCodeType = data.getString("BIZ_TYPE_CODE");//渠道编码
			if(CustServiceHelper.isCustomerServiceChannel(bizCodeType)){//一级客服升级业务能力开放平台身份鉴权
	        	IData identPara =  new DataMap();
	        	identPara.put("SERIAL_NUMBER", serialNumber);
	        	identPara.put("IDENT_CODE", data.getString("IDENT_CODE",""));
	        	identPara.put("BIZ_TYPE_CODE", bizCodeType);
	        	CustServiceHelper.checkCertificate(identPara);
			}else{
				IDataset paramDatas = CommparaInfoQry.queryCommInfos("7777", "IDENT_AUTH_CONFG", data.getString("KIND_ID", ""), data.getString("BIZ_TYPE_CODE", ""));

	            if (IDataUtil.isNotEmpty(paramDatas))
	            {
	                UserIdentBean bean = new UserIdentBean();
	                data.put("SERIAL_NUMBER", data.getString("IDVALUE", ""));
	                bean.identAuth(data);
	            }
			}            
        }

        IData outParam = this.recharge(data);

        outParam.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        return outParam;
    }
}
