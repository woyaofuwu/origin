package com.asiainfo.veris.crm.order.soa.person.busi.intf.ordercrbt;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.CrmDAO;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.query.RouteInfoQry;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.iupc.rule.dao.DAO;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

/**
 * 不忘初心，传递梦想，彩铃传播活动需求
 * @author 谢志文
 * @create_date：2017-06
 */
public class CRBTIntfBean extends CSBizBean
{

    public IData tradeRegItem(IData input) throws Exception
    {
        IData result = new DataMap(); 
        
        String strSerialNumber = input.getString("SERIAL_NUMBER");
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }
        
        String strUserID = userInfo.getString("USER_ID");
        
        if ("0".equals(input.getString("MODIFY_TAG", "0")))
        {
            if(UserSvcInfoQry.getUserSvcInfoByUserSvcId(strUserID, "20") > 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户已开通彩铃服务");
            }  
        } 
        
        
        IDataset configData =  CommparaInfoQry.getCommByParaAttr("CRM", "2540", "0898"); 

        if (IDataUtil.isEmpty(configData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "免费彩铃元素配置TD_S_COMMPARA.2540不存在");
        }
        
        IDataset mainProd = UserProductInfoQry.queryUserMainProduct(strUserID);
        
        if (IDataUtil.isEmpty(mainProd))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户主产品信息不存在");
        }

        IDataset selectedElements = new DatasetList(); 

        for (Iterator iterator = configData.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next();

            String elementId = item.getString("PARAM_CODE");
            
            String elementTypeCode = item.getString("PARA_CODE2");
            
            if ("D".equals(elementTypeCode))
            {
                IData discntInfo = UDiscntInfoQry.getDiscntInfoByPk(elementId);
                
                if (IDataUtil.isEmpty(discntInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据优惠编码[" + elementId + "]查询优惠配置不存在");
                }
            }
            else if ("S".equals(elementTypeCode))
            {
                IData svcInfo = USvcInfoQry.qryServInfoBySvcId(elementId);
                
                if (IDataUtil.isEmpty(svcInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据服务编码[" + elementId + "]查询服务配置不存在");
                }
            }
            else
            {
              continue;
            }
            
            IDataset orderElement = UProductElementInfoQry.getElementInfosByProductId(mainProd.getData(0).getString("PRODUCT_ID")); 
          
            
            IDataset  elementDataset = DataHelper.filter(orderElement, "ELEMENT_TYPE_CODE="+elementTypeCode+",ELEMENT_ID="+elementId);
            
            if (IDataUtil.isEmpty(elementDataset))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户主产品编码["+mainProd.getData(0).getString("PRODUCT_ID")+"],元素类型["+elementTypeCode+"],查询可订购关系不存在");
            }
             
            
           
            IData elementData = new DataMap();
            elementData.put("PRODUCT_ID", mainProd.getData(0).getString("PRODUCT_ID"));
            elementData.put("PACKAGE_ID",elementDataset.getData(0).getString("PACKAGE_ID"));
            elementData.put("ELEMENT_ID", elementId.trim());
          
            elementData.put("MODIFY_TAG", input.getString("MODIFY_TAG", "0"));
            elementData.put("ELEMENT_TYPE_CODE", elementTypeCode.trim());
             
            selectedElements.add(elementData);

        }

        IData inParam = new DataMap();
        inParam.put("SELECTED_ELEMENTS", selectedElements);
        inParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        inParam.put("TRADE_TYPE_CODE", "110");

        IDataset idsRet = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", inParam);
        
        if(IDataUtil.isNotEmpty(idsRet))
        {
        	IData idRet = idsRet.first();
        	String strTradeID = idRet.getString("TRADE_ID");
        	result.putAll(idsRet.first());
        	result.put("X_RSPDESC", "OK");
            result.put("X_RESULTINFO", "OK");
            result.put("X_RSPCODE", "0000");
            result.put("X_RESULTCODE", "0000");
           
            String strAcceptMonth = SysDateMgr.getCurMonth();
            String strSysTime = SysDateMgr.getSysTime();
            IData e = idsRet.first();
            e.put("ACCEPT_MONTH", strAcceptMonth);
            e.put("SERIAL_NUMBER", strSerialNumber);
            IDataset crbtInfo = queryInfo(e);
            if (IDataUtil.isEmpty(crbtInfo))
            {
                IData paramsAll = new DataMap(); 
                String id = UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0,30); 
                paramsAll.put("OPR_NUM", id); 
                paramsAll.put("ACCEPT_MONTH", strAcceptMonth);
                paramsAll.put("MODIFY_TAG", "0");
                paramsAll.put("SERIAL_NUMBER", strSerialNumber);
                paramsAll.put("ACCEPT_DATE", strSysTime); 
                paramsAll.put("STATUS", "1");
                paramsAll.put("UPDATE_TIME", strSysTime); 
                paramsAll.put("RSRV_STR1", strTradeID);
                paramsAll.put("RSRV_STR2", CSBizBean.getVisit().getStaffId());
                paramsAll.put("RSRV_STR3", CSBizBean.getVisit().getDepartId());
                paramsAll.put("RSRV_STR4", "");
                paramsAll.put("RSRV_STR5", "");
                paramsAll.put("REMARK", "");
                Dao.insert("TF_B_ORDER_CRBT", paramsAll,Route.CONN_CRM_CEN);
            } 
            else
            {
            	IData inData = new DataMap();
             
            	if ("0".equals(input.getString("MODIFY_TAG", "0")))
            	{
            	    inData.put("RSRV_STR1", strTradeID);
            	    inData.put("STATUS", "1");
            	}
            	else
            	{
            	    //退订将订单ID放到预留字段5，否则放到预留字段1
            	    inData.put("RSRV_STR5", strTradeID);
            	    inData.put("STATUS", "7");
            	}

                //更新下发表中执行的工单id 
                inData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                inData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                
                inData.put("ROWID", crbtInfo.first().getString("ROWID"));

                updateInfo(inData);
            }
        }
        else
        {
        	result.put("X_RSPDESC", "失败");
            result.put("X_RESULTINFO", "失败");
            result.put("X_RSPCODE", "2998");
            result.put("X_RESULTCODE", "2998");
		}
        
        return result;
    } 

    private void updateInfo(IData inData) throws Exception
    {

        DBConnection conn = new DBConnection("cen1", true, false);
        try
        {
            SQLParser parser = new SQLParser(inData);
            parser.addSQL(" UPDATE TF_B_ORDER_CRBT SET ");
            parser.addSQL(" STATUS      = :STATUS, ");
            parser.addSQL(" RSRV_STR1   = :RSRV_STR1, ");
            parser.addSQL(" RSRV_STR2   = :UPDATE_STAFF_ID, ");
            parser.addSQL(" RSRV_STR3   = :UPDATE_DEPART_ID, ");
            parser.addSQL(" RSRV_STR4   = :REQUEST_ID, ");
            parser.addSQL(" RSRV_STR5   = :RSRV_STR5, ");
            parser.addSQL(" REMARK   = :REMARK, "); 
            parser.addSQL(" UPDATE_TIME = SYSDATE ");
            parser.addSQL(" WHERE ROWID = :ROWID ");

            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
            dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

            conn.commit();
        }
        catch (Exception e)
        {
            conn.rollback();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
        }
        finally
        {
            conn.close();
        }
    }

    public IData processOrder(IData input) throws Exception
    {

        StringBuilder selectSQL = new StringBuilder()
                .append(" SELECT ROW_.*,ROWID, ROWNUM ROWNUM_ FROM ")
                .append(" ( select OPR_NUM,ACCEPT_MONTH,MODIFY_TAG,SERIAL_NUMBER,STATUS,ACCEPT_DATE from TF_B_ORDER_CRBT where STATUS = '0' order by ACCEPT_DATE) ROW_  ")
                .append(" WHERE ROWNUM <= 10000 ");

        IData input1 = new DataMap();
        IDataset list = Dao.qryBySql(selectSQL, input1, Route.CONN_CRM_CEN);

        if (IDataUtil.isNotEmpty(list))
        {
            for (Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                IData item = (IData) iterator.next();
                 
                
                IData resultItem = new DataMap();
                resultItem.put("RESULT_INFO", "处理成功");
                resultItem.put("RESULT_CODE", "0");
                resultItem.put("RESULT_DETAIL", "");
                
                
                IDataset ret =null;
                try
                {
                      ret = CSAppCall.call("SS.CRBTIntfSVC.tradeRegItem", item);
                   
                } 
                catch (Exception e)
                {
                    resultItem.putAll(processException(e)); 
                }

                IData inData = new DataMap();

                //更新下发表中执行的工单id 
                inData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                inData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                
                if (IDataUtil.isNotEmpty(ret))
                {
                    inData.put("RSRV_STR1", ret.getData(0).getString("TRADE_ID"));
                   
                }
                String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(item.getString("SERIAL_NUMBER")); 
                
                inData.put("EPARCHY_CODE", StringUtils.isNotEmpty(strEparchyCode)?strEparchyCode:"");
               

                inData.putAll(resultItem);
                inData.put("STATUS", "0".equals(inData.getString("RESULT_CODE")) ? "1" : "2");
                inData.put("ROWID", input.getString("ROWID"));

                updateInfo(inData);

            }
        }
        IData returnData = new DataMap();

        String rspCode = "0";
        String rspDesc = "调用成功！";
        returnData.put("RESULT_CODE", rspCode);
        returnData.put("RESULT_INFO", rspDesc);
        returnData.put("RSP_CODE", rspCode);
        returnData.put("RSP_DESC", rspDesc);
        return returnData;
    }

    private IData processException(Exception e) throws Exception
    {
        IData exceptionRet = new DataMap(); 
        
        getExceptionInfo(e,exceptionRet); 
               
        SessionManager.getInstance().rollback();
         
        exceptionRet.put("RESULT_CODE", "-1"); 
        
        return exceptionRet;
    }

    private void getExceptionInfo(Throwable e,IData exceptionRet)
    {
     
        if (e instanceof InvocationTargetException)
        {
           getExceptionInfo(((InvocationTargetException) e).getTargetException(), exceptionRet);
        }
        else
        {
            String msg = e.getMessage();
            msg = (msg == null) ? "处理失败" : msg;
            String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
            
            StringBuffer sb = new StringBuffer();
            for (StackTraceElement stackTraceElement : e.getStackTrace())
            {
                sb.append(stackTraceElement + "\n");
            }
            String tStr = sb.toString();

            String resultDetail = ((tStr.length() > 4000) ? tStr.substring(0, 4000) : tStr); 
            
            exceptionRet.put("RESULT_INFO", rspDesc); 
            exceptionRet.put("RESULT_DETAIL", resultDetail);
        }
      
    }
    
    public static void buildField(IData item,String fieldName) throws Exception
    { 
        String fieldValue = StaticUtil.getStaticValue(BizBean.getVisit(), "TD_B_PACKAGE_ELEMENT", new String[] {"ELEMENT_ID", "PACKAGE_ID"},
        					fieldName, new String[] {IDataUtil.chkParam(item, "ELEMENT_ID"),IDataUtil.chkParam(item,"PACKAGE_ID")});
        item.put(fieldName, fieldValue);
    }
    
    /**
     * 活动到期下发提醒短信接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData sendEndSMS(IData input) throws Exception
    {

        //锁定短信发送范围
        StringBuilder selectSQL = new StringBuilder()
                .append(" SELECT ROW_.*,ROWID, ROWNUM ROWNUM_ FROM ")
                .append(" ( select OPR_NUM,ACCEPT_MONTH,MODIFY_TAG,SERIAL_NUMBER,STATUS,ACCEPT_DATE,RSRV_STR4 REQUEST_ID from TF_B_ORDER_CRBT where STATUS in ('1','4') order by ACCEPT_DATE) ROW_  ")
                .append(" WHERE ROWNUM <= 10000 ");

        IData input1 = new DataMap();
        IDataset list = Dao.qryBySql(selectSQL, input1, Route.CONN_CRM_CEN);

        if (IDataUtil.isNotEmpty(list))
        {
            for (Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                IData item = (IData) iterator.next();
                 
                
                IData resultItem = new DataMap();
                resultItem.put("RESULT_INFO", "短信发送成功");
                resultItem.put("RESULT_CODE", "0");
                resultItem.put("RESULT_DETAIL", "");
                
                Boolean sendFlag = Boolean.FALSE;
                
                IDataset ret = null;
                 
                try
                { 
                    ret = CSAppCall.call("SS.CRBTIntfSVC.processSmsItem", item);
                    sendFlag = Boolean.TRUE;//ret.getData(0).getBoolean("SEND_FLAG", Boolean.FALSE);
                    //resultItem.put("REMARK", ret.getData(0).getString("REMARK", ""));
                } 
                catch (Exception e)
                {
                    resultItem.putAll(processException(e));
                }
                
                //短信实际发送了，或者发送抛异常则更新
                if (sendFlag || "-1".equals(resultItem.getString("RESULT_CODE")))
                {
                    IData inData = new DataMap();

                    //更新下发表中执行的工单id 
                    inData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    inData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());  
                    inData.putAll(resultItem);
                  
                    inData.put("ROWID", item.getString("ROWID"));
                    if (IDataUtil.isNotEmpty(ret))
                    {
                    	IData idRet = ret.first();
                    	String strResultCode = inData.getString("RESULT_CODE", "");
                    	
                    	String strRequestID = idRet.getString("REQUEST_ID", "-1");
                        inData.put("REQUEST_ID", strRequestID);
                        
                        String strFlowID = idRet.getString("FLOW_ID", "-1");
                        inData.put("RSRV_STR5", strFlowID);
                        
                        String strRemark = idRet.getString("REMARK", "短信处理失败1");
                        inData.put("REMARK", strRemark);
                        
                        String strSMS = idRet.getString("SMS_TYPE", "");
                        if ("SMS".equals(strSMS) || "-1".equals(strRequestID))
                        {
                            inData.put("STATUS", "0".equals(strResultCode) ? "9" : "4");     
                        }
                        else
                        {
                            inData.put("STATUS", "0".equals(strResultCode) ? "3" : "4");     
                        }
                    }
                    else
                    {
                    	inData.put("STATUS", "4");
                    	inData.put("REQUEST_ID", resultItem.getString("REQUEST_ID", "-1"));
                    	inData.put("RSRV_STR5", resultItem.getString("FLOW_ID", "-1"));
                        inData.put("REMARK", resultItem.getString("RESULT_INFO", "短信处理失败2"));
					}
                    updateInfo(inData);
                }
            }
        }
        IData returnData = new DataMap();

        String rspCode = "0";
        String rspDesc = "调用成功！";
        returnData.put("RESULT_CODE", rspCode);
        returnData.put("RESULT_INFO", rspDesc);
        returnData.put("RSP_CODE", rspCode);
        returnData.put("RSP_DESC", rspDesc);
        return returnData;
    }

    /**
     * 活动到期下发提醒短信子函数
     * @param input
     * @return
     * @throws Exception
     */
    public IData processSmsItem(IData item) throws Exception
    {
        IData result = new DataMap(); 
        result.put("SEND_FLAG", Boolean.FALSE);
        result.put("REMARK", "用户未下发提醒短信");
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(item.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }
       
        IDataset configData =  CommparaInfoQry.getCommByParaAttr("CRM", "2540", userInfo.getString("EPARCHY_CODE")); 

        if (IDataUtil.isEmpty(configData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "免费彩铃元素配置TD_S_COMMPARA.2540不存在");
        }
        
        IDataset mainProd = UserProductInfoQry.queryUserMainProduct(userInfo.getString("USER_ID"));
        
        if (IDataUtil.isEmpty(mainProd))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户主产品信息不存在");
        }
        
        String productName =  StaticUtil.getStaticValue(BizBean.getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "PRODUCT_NAME",mainProd.getData(0).getString("PRODUCT_ID"));
        
        filterSmsTypes(configData);
        //排序控制，优先级高的先发
        DataHelper.sort(configData, "PARA_CODE5", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        
        for (Iterator iterator = configData.iterator(); iterator.hasNext();)
        {
            IData configItem = (IData) iterator.next();

            //元素类型
            String elementType = configItem.getString("PARA_CODE1");

            //配置类型
            String elementTypeCode = configItem.getString("PARA_CODE2");

            //元素编码
            String elementId = configItem.getString("PARA_CODE3");

            //短信发送内容
            String endSmsContent = configItem.getString("PARA_CODE24");

            //短信发送时间
            String smsSendTime = configItem.getString("PARA_CODE4");

            //指定发送时间没有达到则跳过
            if (StringUtils.isNotBlank(smsSendTime))
            {
                Date date1 = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND);
                Date date2 = SysDateMgr.string2Date(smsSendTime, SysDateMgr.PATTERN_STAND);

                //时间没到跳过
                if (date1.compareTo(date2) < 0)
                {
                	continue;
                }

                if ("D".equals(elementType))
                {
                    //用户没有订购服务或资费，则跳过
                    if (IDataUtil.isEmpty(UserDiscntInfoQry.getAllDiscntByUser(userInfo.getString("USER_ID"), elementId)))
                    {
                        continue;
                    }
                }
                else if ("S".equals(elementType))
                {
                    if (UserSvcInfoQry.getUserSvcInfoByUserSvcId(userInfo.getString("USER_ID"), elementId) == 0)
                    {
                        continue;
                    }
                }
                else if ("P".equals(elementType))
                {
                    //查询用户是否有配置的平台服务
                    if (IDataUtil.isEmpty(UserPlatSvcInfoQry.getUserPlatSvc(userInfo.getString("USER_ID"), elementId))) 
                    {
                        continue;
                    }
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "免费彩铃TD_S_COMMPARA.2540,PARA_CODE1只能配置'S','D','P'");
                }
                result.put("SMS_TYPE", elementTypeCode);

                //普通的短信到期提醒
                if ("SMS".equals(elementTypeCode))
                {
                    IData smsData = new DataMap();
                    String smsNoticeId = SeqMgr.getSmsSendId();
                    smsData.put("SMS_NOTICE_ID", smsNoticeId);
                    smsData.put("RECV_OBJECT", item.getString("SERIAL_NUMBER"));
                    smsData.put("NOTICE_CONTENT", String.format(endSmsContent, productName));
                    SmsSend.insSms(smsData);
                    result.put("REQUEST_ID", smsNoticeId);
                    result.put("FLOW_ID", "-1");
                    result.put("SEND_FLAG", Boolean.TRUE);
                    result.put("REMARK", "用户下发普通提醒短信");
                    //只发送一条
                    break;
                }
                //需要二次确认的短信提醒
                else if ("TWOCHECK_SMS".equals(elementTypeCode) || "TWOCHECK_SMS_NOREP".equals(elementTypeCode))
                {
                    //二次确认短信重发送，只给二次短信发送后，没有回复的用户发送
                    if ("TWOCHECK_SMS_NOREP".equals(elementTypeCode))
                    {
                        //发送了二次确认短信，并且回复了的则不再发送二次确认短信
                    	//状态修改为5，或者是6，则代表用户已经回复过了       
                        if (StringUtils.isNotBlank(item.getString("REQUEST_ID")) && !("5".equals(item.getString("STATUS")) || "6".equals(item.getString("STATUS"))))
                        {
                            continue;
                        }
                    }

                    IData sendInfo = new DataMap();
                    String smsContent = String.format(endSmsContent, productName);
                    sendInfo.put("REMARK", "二次确认短信");
                    sendInfo.put("SERIAL_NUMBER", item.getString("SERIAL_NUMBER"));
                    sendInfo.put("SMS_CONTENT", smsContent);
                    sendInfo.put("SMS_TYPE", BofConst.SPEC_SVC_SEC);
                    sendInfo.put("OPR_SOURCE", "1");

                    // 插二次短信表
                    IData preOderData = new DataMap();
                    //  preOderData.put("SVC_NAME", "SS.BatSaleActiveSVC.replyTradeInft");

                    //插入预约工单,在二次短信确认的时候会调用此接口进行处理
                    preOderData.put("SVC_NAME", "SS.CRBTIntfSVC.twoCheckReply");
                    preOderData.put("PRE_TYPE", BofConst.SPEC_SVC_SEC);
                    preOderData.put("SERIAL_NUMBER", item.getString("SERIAL_NUMBER"));
                    IData twoCheckData = TwoCheckSms.twoCheck("-1", 24, preOderData, sendInfo);// 插入2次短信表

                    //将request_id返回便于保存到表中，进行后续的业务处理
                    String request_id = twoCheckData.getString("REQUEST_ID", "");
                    if(StringUtils.isNotBlank(request_id))
                    {
                    	result.put("REQUEST_ID", request_id);
                        result.put("REMARK", "用户下发二次确认短信");
                        String acceptMonth = StrUtil.getAcceptMonthById(request_id);
                    	String flow_id = request_id.substring(request_id.length() - 8);
                    	String twoCheck3 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                                           { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "BMC_TWOCHECK_CODE3", "SS.CRBTIntfSVC.twoCheckReply" });
                    	flow_id = twoCheck3 + flow_id + acceptMonth; 
                    	result.put("FLOW_ID", flow_id);
                    }
                    else
                    {
                    	result.put("REQUEST_ID", "-1");
                    	result.put("FLOW_ID", "-1");
                        result.put("REMARK", "用户未下发提醒短信。");
					}
                    
                    result.put("SEND_FLAG", Boolean.TRUE);
                    //只发送一条
                    break;
                }
            }
        }
        return result; 
    }

    private void filterSmsTypes(IDataset configData) throws Exception
    {
        for (Iterator iterator = configData.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next();
            String elementTypeCode = item.getString("PARA_CODE2");
            if (!("SMS".equals(elementTypeCode) || "TWOCHECK_SMS".equals(elementTypeCode) || "TWOCHECK_SMS_NOREP".equals(elementTypeCode)))
            {
                iterator.remove();
            } 
        }
    }
    /**
     * 二次确认短信的回复处理
     * @param input
     * @return
     * @throws Exception
     */
    public IData twoCheckReply(IData input) throws Exception
    { 
        String serialNumber = input.getString("SERIAL_NUMBER");//用户手机号码
        //二次短信确认的标示
        String requestId   = input.getString("REQUEST_ID");//回复 短信的号码 
        //回复的结果  "true"代表回复"是" false 代表回复的"否"
        String isConfirm = input.getString("IS_CONFIRM");
         
    
        IData inData = new DataMap(); 
        inData.put("SERIAL_NUMBER", serialNumber);
        
       IDataset resultInfo = queryInfo(inData);
       resultInfo = DataHelper.filter(resultInfo, "RSRV_STR5="+requestId);
       
       if (IDataUtil.isEmpty(resultInfo))
       {
           CSAppException.apperr(CrmCommException.CRM_COMM_103, "此用户没有发送二次确认短信,无法处理回复");
       }
       
       IData resultItem = resultInfo.getData(0);
       
       //回复“是”则修改状态为5，此类号码将不进行退订
       if ("true".equals(isConfirm))
       {  
           //回复是则修改为5
           resultItem.put("STATUS", "5"); 
       }
       else
       {
           //回复否则修改为6
           resultItem.put("STATUS", "6"); 
       }
       resultItem.put("REMARK", "二次短信确认回复成功");
       
       updateInfo(resultItem);
       
        
        IData returnData = new DataMap(); 
        String rspCode = "0";
        String rspDesc = "调用成功！";
        returnData.put("RSP_CODE", rspCode);
        returnData.put("RSP_DESC", rspDesc);
        return returnData;
    }
    
    public IData processCancelProductOrder(IData input) throws Exception
    {

        StringBuilder selectSQL = new StringBuilder()
                .append(" SELECT ROW_.*,ROWID, ROWNUM ROWNUM_ FROM ")
                .append(" ( select OPR_NUM,ACCEPT_MONTH,'1' MODIFY_TAG,SERIAL_NUMBER,STATUS,ACCEPT_DATE from TF_B_ORDER_CRBT where STATUS in('3','6','8') and RSRV_STR4 IS NOT NULL order by ACCEPT_DATE) ROW_  ")
                .append(" WHERE ROWNUM <= 10000 ");

        IData input1 = new DataMap();
        IDataset list = Dao.qryBySql(selectSQL, input1, Route.CONN_CRM_CEN);

        if (IDataUtil.isNotEmpty(list))
        {
        	for (int i = 0; i < list.size(); i++) 
        	{
                IData item = list.getData(i);
                
                IData resultItem = new DataMap();
                //resultItem.put("RESULT_INFO", "处理成功");
                resultItem.put("RESULT_CODE", "0");
                resultItem.put("RESULT_DETAIL", "");
                
                
                IDataset ret =null;
                try
                {
                    String executeTime = getCancelProductExecuteTime();
                    if (StringUtils.isNotBlank(executeTime))
                    {
                        Date date1 = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND);
                        Date date2 = SysDateMgr.string2Date(executeTime, SysDateMgr.PATTERN_STAND);
                        
                         //时间没到跳过
                         if (date1.compareTo(date2) < 0)
                         {
                            continue;
                         }
                    }
                    else
                    {
                    	CSAppException.apperr(CrmCommException.CRM_COMM_103, "2540配置中没有发现PARA_CODE2=CANCEL_PRODUCT_TIME的配置");
					}
                    ret = CSAppCall.call("SS.CRBTIntfSVC.tradeRegCancel", item);
                    if(IDataUtil.isNotEmpty(ret))
                    {
                    	item.put("UPDATE_DEPART_ID", ret.getData(0).getString("TRADE_ID", "-1"));
                    	resultItem.put("RESULT_CODE", ret.getData(0).getString("RESULT_CODE"));
                    	resultItem.put("REMARK", ret.getData(0).getString("REMARK"));
                    }
                    else
                    {
                    	item.put("UPDATE_DEPART_ID", "-1");
                    	resultItem.put("RESULT_CODE", "-1");
    					resultItem.put("REMARK", "免费彩铃到期自动取消失败。。");
					}
                } 
                catch (Exception e)
                {
                	IData ide = processException(e);
                	item.put("UPDATE_DEPART_ID", "-1");
                	resultItem.put("RESULT_CODE", "-1");
					resultItem.put("REMARK", ide.getString("RESULT_INFO", "免费彩铃到期自动取消失败。。。"));
                }

                //更新下发表中执行的工单id 
                //item.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                //item.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                
                /*if (IDataUtil.isNotEmpty(ret))
                {
                    item.put("UPDATE_DEPART_ID", ret.getData(0).getString("TRADE_ID"));
                   
                }
                else
                {
                	item.put("UPDATE_DEPART_ID", "-1");
				}*/
                //String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(item.getString("SERIAL_NUMBER")); 
                //StringUtils.isNotEmpty(strEparchyCode)?strEparchyCode:""
                
                item.put("EPARCHY_CODE", "0898");
               
                item.putAll(resultItem);
                item.put("STATUS", "0".equals(item.getString("RESULT_CODE")) ? "7" : "8"); 
                updateInfo(item);
            } 
        } 
        IData returnData = new DataMap(); 
        String rspCode = "0";
        String rspDesc = "调用成功！";
        returnData.put("RESULT_CODE", rspCode);
        returnData.put("RESULT_INFO", rspDesc);
        returnData.put("RSP_CODE", rspCode);
        returnData.put("RSP_DESC", rspDesc);
        return returnData; 
    }
    
    public IData tradeRegCancel(IData item) throws Exception
    {
        IData result = new DataMap();
        String strSn = item.getString("SERIAL_NUMBER");
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(strSn);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }
        
        String strUserID = userInfo.getString("USER_ID");
        
        IData param = new DataMap();
        param.put("USER_ID", strUserID);
        param.put("SERVICE_ID", "20");
        IDataset idsUserSvc = UserSvcInfoQry.getUserSvcInfoByUserSvcId(param);
        
        if (IDataUtil.isEmpty(idsUserSvc))
        {
        	result.put("RESULT_CODE", "-1");
        	result.put("REMARK", "用户免费彩铃不存在或者已经取消。");
        	return result;
        }
        else
        {
        	try 
        	{
        		IData idUserSvc = idsUserSvc.first();
            	String strInstID = idUserSvc.getString("INST_ID", "");
            	IData inParam = new DataMap();
                inParam.put("SERIAL_NUMBER", strSn);
                inParam.put("TRADE_TYPE_CODE", "110");
                inParam.put("ELEMENT_ID", "20");
                inParam.put("MODIFY_TAG", "1");
                inParam.put("ELEMENT_TYPE_CODE", "S");
                //inParam.put("INST_ID", strInstID);
                //inParam.put("END_DATE", SysDateMgr.getSysTime());
                inParam.put("BOOKING_TAG", "0");
                inParam.put("REMARK", "免费彩铃到期自动取消");
                IDataset ret = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", inParam);
                if(IDataUtil.isNotEmpty(ret))
                {
                	result.put("TRADE_ID", ret.getData(0).getString("TRADE_ID"));
                	result.put("RESULT_CODE", "0");
                	result.put("REMARK", "免费彩铃到期自动取消成功。");
                }
			} 
        	catch (Exception e) 
			{
				String error =  Utility.parseExceptionMessage(e); 
				String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					//String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					result.put("RESULT_CODE", "-1");
					result.put("REMARK", strExceptionMessage);
				}
				else
				{
					result.put("RESULT_CODE", "-1");
					result.put("REMARK", error);
				}
			}
		}
        return result;
    }
    
    private String getCancelProductExecuteTime() throws Exception
    {
        IDataset configData =  CommparaInfoQry.getCommByParaAttr("CRM", "2540", "0898"); 

        if (IDataUtil.isEmpty(configData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "免费彩铃配置TD_S_COMMPARA.2540不存在");
        }
        
        configData =  DataHelper.filter(configData, "PARA_CODE2=CANCEL_PRODUCT_TIME");
        
        if (IDataUtil.isEmpty(configData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "2540配置中没有发现PARA_CODE2=CANCEL_PRODUCT_TIME的配置");
        }
        
        String executeTime = configData.getData(0).getString("PARA_CODE1");
        
        if (StringUtils.isEmpty(executeTime))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "PARA_CODE2=CANCEL_PRODUCT_TIME的配置中没有发现PARA_CODE1中配置指定的取消时间");
        }
        
       return executeTime;
        
    }
    
    public IDataset queryInfo(IData inData) throws Exception
    {
    	SQLParser parser = new SQLParser(inData);
	    parser.addSQL(" SELECT ROWID, T.* ");
	    parser.addSQL("   FROM TF_B_ORDER_CRBT T ");
	    parser.addSQL("  WHERE 1 = 1 ");
	    parser.addSQL("    AND SERIAL_NUMBER = :SERIAL_NUMBER ");
	    parser.addSQL("    AND T.ACCEPT_MONTH = :ACCEPT_MONTH ");
        return DAO.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    public static void main(String []args) throws Exception
    {
     
        Date date1 = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND);
        Date date2 = SysDateMgr.string2Date("2017-06-23 21:20:10", SysDateMgr.PATTERN_STAND);
        
        System.out.println(date1.compareTo(date2) < 0);
        
    }

}