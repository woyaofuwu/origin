package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen;

import java.util.Calendar;

import org.apache.commons.lang.RandomStringUtils;

import com.ailk.biz.util.Encryptor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class CustServiceAuthBean extends CSBizBean
{

    /**
     * 生成随机6位密码
     * @param data
     * @throws Exception
     */
    public IData randomPassword(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "OPR_NUMB");
        IDataUtil.chkParam(data, "CHANNEL_ID");

        IData userInfo = CustServiceHelper.checkUserInfo(data);

        IData param = new DataMap();

        String userId = userInfo.getString("USER_ID");
        //String partition_id = SysDateMgr.getCurMonth();//分区改为月            
        String partition_id = userId.substring(userId.length() - 4, userId.length());          
        
        String passwd = RandomStringUtils.randomNumeric(6);

        String newpasswd = Encryptor.fnEncrypt(passwd, CustServiceHelper.genUserId(userId));// 密文密码
        String notice = "您本次登录的随机密码为" + passwd + ",请保密，并确认是本人操作[中国移动]";

        int activeSecends = 300;
        String registTime = SysDateMgr.getSysTime();
        String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime,
                                                                                            "yyyy-MM-dd HH:mm:ss"),
                                                                                            activeSecends), "yyyy-MM-dd HH:mm:ss");
        
        data.put("PWD_START_TIME", registTime);
        data.put("PWD_END_TIME", endDate);
       // data.put("PWD_ACTIVE_TIME", activeSecends);
        
        data.put("USER_ID", userId);
        data.put("PARTITION_ID", partition_id);
        data.put("PASSWORD", newpasswd);
        data.put("PWD_FLAG", "0");//2015-07-30 CHENXY3 修改 先默认给0
        if (!Dao.insert("TF_B_PWD_LOG", data))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "随机密码日志表生成出错！");
        }
        //发送短信通知
        IData inparam = new DataMap();
        inparam.put("NOTICE_CONTENT", notice);
        inparam.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
        inparam.put("RECV_ID", data.getString("SERIAL_NUMBER"));
        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
        inparam.put("REMARK", "移动业务办理校验随机验证短信通知");
        SmsSend.insSms(inparam);

        param.put("PASSWORD_RSLT", "0");//0 成功   1 失败
        param.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
          param.put("INTF_TYPE", "01");
        return param;
    }

    /**
     * 绑定手机号码
     *
     *
     * @param data
     * @return
     * @throws Exception
     */
    public IData bindMobilePhone(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "OPR_NUMB");
        IDataUtil.chkParam(data, "CHANNEL_ID");
        IDataUtil.chkParam(data, "IDENT_CODE_TYPE");
        IDataUtil.chkParam(data, "PASSWORD");
        IDataUtil.chkParam(data, "PWD_TYPE");
        String account = IDataUtil.chkParam(data, "MICRO_ACCOUNT");
  
        data.put("CHANNEL_TYPE", "01");
        CustServiceHelper.checkUserType(data);

        IData userInfo = CustServiceHelper.checkUserInfo(data);

        
        IData user = new DataMap();
        IDataset userInfos = new DatasetList();

        String userId = userInfo.getString("USER_ID");

        //校验客户是否已经绑定了手机号码 
 
        data.put("USER_ID", userId);
        data.put("REMOVE_TAG", "0");
        
        IDataset dataset = CustServiceHelper.queryUserAccount(data);

        if (IDataUtil.isNotEmpty(dataset))
        { 
            IData info = new DataMap();
            info.put("X_RESULTCODE", "22037");//该手机号在本渠道已经绑定
            info.put("X_RESULTINFO", "该号码已经绑定了微博/微信账号！"); 
            return info;
           //CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码已经绑定了微博/微信账号！"); 
        }
        
        data.put("USER_ID", userId); 
        data.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", ""));
        data.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));
        data.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID", ""));
        data.put("UPDATE_TIME", SysDateMgr.getSysTime()); 
        
        IData retinfo = CustServiceHelper.checkServicePassword(data);

        if (!"-1".equals(retinfo.getString("X_RESULTCODE")))
        {
            CustServiceHelper.buildIdentInfo(data); 
            data.put("REMOVE_TAG", "0");//账号失效标识 
            createCertificateInfo(data);
        }
        else
        {
            data.put("REMOVE_TAG", "1");//账号失效标识
        }
        
        if (null != data.getString("IDENT_CODE"))
        {
            data.put("BIND_TIME", SysDateMgr.getSysTime()); 
        }

       
        data.put("ERROR_NUMB", null == data.getString("IDENT_CODE") ? 1 : 0);
        data.put("ERROR_TIME", SysDateMgr.getSysTime()); 
        data.put("ACCOUNT", account);
        createIdentInfo(data);
       

        if ("-1".equals(retinfo.getString("X_RESULTCODE")))
        { 
           return retinfo;
        }

        IData info = new DataMap();
        info.put("IDENT_CODE_LEVEL", data.getString("IDENT_CODE_TYPE"));//用户身份凭证级别 01：一般凭证 03：服务密码凭证
        info.put("IDENT_CODE_TYPE", data.getString("IDENT_CODE_TYPE"));//用户身份凭证类型01：一般凭证（绑定不涉及）02：短信密码凭证03：服务密码凭证
        user.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        user.put("CUSTOM_ID", userInfo.getString("CUST_ID"));//客户标识
        user.put("PROVINCE", "898");//客户归属省

        IData ret = CustServiceHelper.buildBrandAndStatus(data);

        user.putAll(ret);
        user.putAll(CustServiceHelper.buildUserCreditInfo(data));

        userInfos.add(user);
        info.put("USER_INFO", userInfos);//用户信息
        info.put("X_RESULTCODE", "0");
        info.put("X_RESULTINFO", "OK!");
        info.put("IDENT_END_TIME", data.getString("IDENT_END_TIME"));
        info.put("IDENT_CODE", data.getString("IDENT_CODE"));//用户身份凭证 
          info.put("INTF_TYPE", "01");
        info.put("EFFECTIVE_TIME", "300");
        return info;
    }

    public IData unBindMobilePhone(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");//手机号码

        String account = IDataUtil.chkParam(data, "MICRO_ACCOUNT");//微博/微信账号 

        CustServiceHelper.checkUserType(data);

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }

        //校验客户是否已经绑定了手机号码 
 
        data.put("USER_ID", userInfo.getString("USER_ID")); 
        data.put("REMOVE_TAG", "0");
        data.put("ACCOUNT", account);
        IData retinfo = CustServiceHelper.checkBindingRelation(data); 
        if (!"".equals(retinfo.getString("X_RESULTCODE","")))
        {
            retinfo.put("X_RSPCODE", retinfo.getString("X_RESULTCODE"));
            retinfo.put("X_RSPDESC", retinfo.getString("X_RESULTINFO"));
            return retinfo;
        }
                
        data.put("REMOVE_TAG", "1"); 
        data.put("REMOVE_TIME", SysDateMgr.getSysDate()); 
        
        CustServiceHelper.updateIdentInfo(data);//解除绑定 
        
        IData info = new DataMap();
        info.put("SERIAL_NUMBER", serialNumber);
        info.put("OPR_NUMB", data.getString("OPR_NUMB")); 
        info.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        info.put("INTF_TYPE", "01");
        return info;
    }

    /**
     * 凭证申请
     * @param data
     * @throws Exception
     */
    public IData certificateRequest(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "OPR_NUMB");
        IDataUtil.chkParam(data, "CHANNEL_ID");
        String identCodeType = IDataUtil.chkParam(data, "IDENT_CODE_TYPE"); 
         
        
        if ("03".equals(data.getString("IDENT_CODE_TYPE")))
        {
            IDataUtil.chkParam(data, "PWD_TYPE");
        }

        CustServiceHelper.checkUserType(data); 
        
        IData info = new DataMap();
        IData user = new DataMap();

        IData userInfo = CustServiceHelper.checkUserInfo(data);

        String userId = userInfo.getString("USER_ID");
   
      

        data.put("USER_ID", userId);
     
       
        data.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", ""));
        data.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));
        data.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID", ""));
        data.put("UPDATE_TIME", SysDateMgr.getSysTime());
        
        data.put("IDENT_CODE_LEVEL", data.getString("IDENT_CODE_TYPE"));
 
        //01 自助，02.坐席
        String value = StaticUtil.getStaticValue("CUSTSERVICE_CHANNEL_ID", data.getString("CHANNEL_ID"));

        if ("01".equals(value)&& !"1CSVCVSCK200001".equals(data.getString("CHANNEL_ID")))
        { 
        //校验客户是否已经绑定了手机号码
        IData bindingRelation = CustServiceHelper.checkBindingRelation(data);

        data.put("USER_ID", userId);
        IData retinfo = CustServiceHelper.checkServicePassword(data);
    
            if (!"".equals(retinfo.getString("X_RESULTCODE","")))
            {
                int errorNum = bindingRelation.getInt("ERROR_NUMB", 0);
                errorNum = errorNum + 1;
                data.put("ERROR_NUMB", String.valueOf(errorNum));
                CustServiceHelper.updateIdentInfo(data);
                retinfo.put("X_RSPCODE", retinfo.getString("X_RESULTCODE"));
                retinfo.put("X_RSPDESC", retinfo.getString("X_RESULTINFO"));
                return retinfo;
            }
        }
        else
        {
            data.put("USER_ID", userId);
            IData retinfo = CustServiceHelper.checkServicePassword(data);
            if (!"".equals(retinfo.getString("X_RESULTCODE","")))
            {
                retinfo.put("X_RSPCODE", retinfo.getString("X_RESULTCODE"));
                retinfo.put("X_RSPDESC", retinfo.getString("X_RESULTINFO"));
               return retinfo;
            }
        }
   
        CustServiceHelper.buildIdentInfo(data);

        //凭证信息入库   TF_F_USER_CERTIFICATE
        createCertificateInfo(data);

        String customId = data.getString("CUSTOM_ID");

        if ("".equals(customId) || customId == null)
        {
            info.put("CUSTOM_ID_STATUS", "N");//客户标识是否变更   N 为未变更,Y为已变更
        }
        else
        {
            info.put("CUSTOM_ID_STATUS", "Y");
        }
        info.put("IDENT_CODE", data.getString("IDENT_CODE"));//用户身份凭证
        
        
        String retIdentCodeType = "03";
        if ("01".equals(identCodeType))
        {
            retIdentCodeType ="01";
        }
        else if ("03".equals(identCodeType))
        {
            if ("02".equals(data.getString("PWD_TYPE")))
            {
                retIdentCodeType ="02";
            }
            
        }
      
        info.put("IDENT_CODE_LEVEL", retIdentCodeType);//用户身份凭证级别 01：一般凭证 03：服务密码凭证
        info.put("IDENT_CODE_TYPE", retIdentCodeType);
        
        user.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        user.put("CUSTOM_ID", userInfo.getString("CUST_ID"));//客户标识
        user.put("PROVINCE", "898");//客户归属省

         IData ret = CustServiceHelper.buildBrandAndStatus(data);
         user.putAll(ret);
         user.putAll(CustServiceHelper.buildUserCreditInfo(userInfo));

        IDataset userInfos = new DatasetList();
        userInfos.add(user);
        info.put("USER_INFO", userInfos);//用户信息
        info.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        info.put("EFFECTIVE_TIME", "300");
        info.put("INTF_TYPE", "01");
        
        info.put("X_RSPCODE", "0");
        info.put("X_RSPDESC", "OK");
        return info;
    }
    /**
     * 凭证校验
     * @param data
     * @throws Exception
     */
    public IData checkCertificate(IData data) throws Exception
    {
        IData info = new DataMap();
        CustServiceHelper.checkCertificate(data);
        info.put("X_RESULTCODE", "0");
        info.put("X_RESULTINFO", "OK!");
        info.put("X_RSPCODE", "0");
        info.put("X_RSPINFO", "OK");
           info.put("INTF_TYPE", "01");
        return info;
    }

    /**
     * 客户有效期延期
     * @param data
     * @return
     * @throws Exception
     */
    public IData certificateDelay(IData data) throws Exception
    {
        
        CustServiceHelper.checkUserType(data);
        IDataUtil.chkParam(data, "OPR_NUMB");
        IDataUtil.chkParam(data, "CHANNEL_ID");
        IDataUtil.chkParam(data, "IDENT_CODE"); 

        IData retInfo = CustServiceHelper.checkCertificate(data);

        IData certInfo = retInfo.getData("IDENT_INFO");
        
        IData userInfo = retInfo.getData("USER_INFO");
        
        data.putAll(certInfo);
        
        data.put("USER_ID", userInfo.getString("USER_ID"));

        //-------失效旧的
        String sysdate = SysDateMgr.getSysTime();
        data.put("IDENT_END_TIME", sysdate);
        CustServiceHelper.updateCertificateInfo(data);//失效旧的

        //生成凭证
        CustServiceHelper.buildIdentInfo(data); 
        createCertificateInfo(data);

        IData result = new DataMap(); 
        result.put("IDENT_END_TIME", data.getString("IDENT_END_TIME"));//用户凭证失效时间
        result.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        result.put("OPR_NUMB", data.getString("OPR_NUMB"));
        result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
           result.put("INTF_TYPE", "01");
        return result;
    }

    /**
     * 自助服务密码凭证申请
     * @param data
     * @throws Exception
     */
    public IData selfServiceCertificateRequest(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "OPR_NUMB"); //本次操作的流水号 
        String channelID = IDataUtil.chkParam(data, "CHANNEL_ID");
        String identCodeType = IDataUtil.chkParam(data, "IDENT_CODE_TYPE"); //用户身份凭证类型 01：一般凭证  03：服务密码凭证
        IData userInfo = CustServiceHelper.checkUserInfo(data);
        CustServiceHelper.checkUserType(data);
        
        //01 自助，02.坐席
        //String value = StaticUtil.getStaticValue("CUSTSERVICE_CHANNEL_ID", data.getString("CHANNEL_ID"));
 
        if (!("03".equals(identCodeType)))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "只允许申请服务密码凭证");
        } 
        String userId = userInfo.getString("USER_ID");
      
        data.put("USER_ID", userId);
   
        data.put("RSRV_STR1", "selfServiceCertificateRequest");
    
        String registTime = SysDateMgr.getSysTime();

        data.put("EPARCHY_CODE", data.getString("TRADE_EPARCHY_CODE", ""));
        data.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));
        data.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID", ""));
        data.put("UPDATE_TIME", registTime); 
  
        //校验客户是否已经绑定了手机号码 
        CustServiceHelper.checkBindingRelation(data);

        //入表 
        createCertificateInfo(data);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(SysDateMgr.currentTimeMillis());

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String smsTemplate = "【尊敬的客户，您于今日" + hour + "点" + minute + "分在通过"
                             + StaticUtil.getStaticValue("CUSTSERVICE_CHANNEL_ID", channelID)
                             + "申请服务密码验证，请直接回复本短信，格式为MMYZ#6位服务密码。】";

        //发送短信通知
        IData inparam = new DataMap();
        inparam.put("NOTICE_CONTENT", smsTemplate);
        inparam.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
        inparam.put("RECV_ID", data.getString("SERIAL_NUMBER"));
        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
        inparam.put("REMARK", "移动自助服务密码申请业务办理短信通知");
        SmsSend.insSms(inparam);

        IData info = new DataMap(); 
        info.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
      info.put("INTF_TYPE", "01");
        return info;
    }

    /**
     * 自助服务密码凭证返回
     * @param data
     * @throws Exception
     */
    public IData selfServiceCertificateResponse(IData data) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "PASSWORD");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }
        IData authReq = CustServiceHelper.querySelfServiceCertificateRequest(data);

        data.putAll(authReq);

        String registTime = SysDateMgr.getSysTime();

        String userId = userInfo.getString("USER_ID"); 
        
        data.put("USER_ID", userId); 
        data.put("UPDATE_STAFF_ID", data.getString("TRADE_STAFF_ID", ""));
        data.put("UPDATE_DEPART_ID", data.getString("TRADE_DEPART_ID", ""));
    
        CustServiceHelper.checkBindingRelation(data);
    
        boolean res2 = UserInfoQry.checkUserPassWd(data.getString("USER_ID"), data.getString("PASSWORD"));
        if (res2) 
        {  
            //生成凭证
           
            data.put("UPDATE_TIME", registTime); 
    
           
            CustServiceHelper.buildIdentInfo(data);
       
            
            IData info = new DataMap();
            
            String customId = authReq.getString("CUSTOM_ID");
            
            if ("".equals(customId) || customId == null)
            {
                info.put("CUSTOM_ID_STATUS", "N");//客户标识是否变更   N 为未变更,Y为已变更
            }
            else
            {
                info.put("CUSTOM_ID_STATUS", "Y");
            }
            String curOprNumb = (data.getString("TRADE_EPARCHY_CODE", "") + SysDateMgr.currentTimeMillis());
            
            
            info.put("OPR_NUMB", curOprNumb);
            info.put("REQ_OPR_NUMB", authReq.getString("OPR_NUMB")); 
            info.put("CHANNEL_ID", data.getString("CHANNEL_ID"));
            
            info.put("IDENT_CODE", data.getString("IDENT_CODE"));//用户身份凭证
            info.put("IDENT_CODE_LEVEL", "03");//用户身份凭证级别 01：一般凭证 03：服务密码凭证
            info.put("IDENT_CODE_TYPE", "03"); 
          //合版本 duhj  2017/5/3 删除  info.put("PROVINCE", "731");//客户归属省
            
          
            
          
            info.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            info.put("CUSTOM_ID", userInfo.getString("CUST_ID"));//客户标识
            info.put("PROVINCE", "898");//客户归属省

             IData ret = CustServiceHelper.buildBrandAndStatus(data);
             info.putAll(ret); 
             info.putAll(CustServiceHelper.buildUserCreditInfo(userInfo)); 
       
 
            info.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            info.put("EFFECTIVE_TIME", "300");
            info.put("INTF_TYPE", "01");
            
            String kindId = "buffetloginAuthReturn_CSVC_0_0";
            info.put("KIND_ID", kindId); 
             //IDataset callResult  = IBossCall.callHttpIBOSS3("IBOSS", info);
             IDataset callResult  = IBossCall.callHttpIBOSS4("IBOSS", info); //合版本 duhj 2017/5/3 修改 

             if (IDataUtil.isEmpty(callResult))
            {
                CSAppException.apperr(IBossException.CRM_IBOSS_6);
             }
             if (!("0".equals(callResult.getData(0).getString("X_RSPTYPE", ""))))
            {
                 CSAppException.apperr(CrmCommException.CRM_COMM_103,
                                      "平台返回错误:" + callResult.getData(0).getString("X_RESULTINFO", ""));
            }
            
            authReq.put("RSRV_STR2", authReq.getString("OPR_NUMB"));
            CustServiceHelper.updateCertificateInfo(authReq);
            createCertificateInfo(data);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务密码错误!"); 
        }
        IData info = new DataMap();
        info.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        info.put("INTF_TYPE", "01");
        return info;
    }

    /**
     * 绑定关系表
     * @param data
     * @throws Exception
     */
    public void createIdentInfo(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String partition_id = userId.substring(userId.length() - 4, userId.length());  
        data.put("PARTITION_ID", partition_id); 
        if (!Dao.insert("TF_F_USER_ACCOUNT", data))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_2999);
        }
    }
    /**
     * 凭证申请
     * @param data
     * @throws Exception
     */
    public void createCertificateInfo(IData data) throws Exception
    {
        String userId = data.getString("USER_ID");
        String partition_id = userId.substring(userId.length() - 4, userId.length());  
        data.put("PARTITION_ID", partition_id); 
        if (!Dao.insert("TF_F_USER_CERTIFICATE", data))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_2999);
        }
    }
}
