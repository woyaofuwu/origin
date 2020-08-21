package com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen;

import java.util.Calendar;

import com.ailk.biz.BizVisit;
import com.ailk.biz.util.Encryptor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.service.bean.BaseBean;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoBean;

public class CustServiceHelper
{
    /**
     * 一级客服凭证校验
     * @throws Exception 
     *
     *
     */
    public static IData checkCertificate(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "IDENT_CODE");

        IData userInfo = checkUserInfo(data); 

        //校验客户是否已经绑定了手机号码 
        IData queryCondition = new DataMap();
        queryCondition.put("USER_ID", userInfo.getString("USER_ID"));  
      //  IData relations = checkBindingRelation(queryCondition);

        data.put("USER_ID", userInfo.getString("USER_ID"));  
        IDataset dataSet = CustServiceHelper.queryCertificate(data);
        if (IDataUtil.isEmpty(dataSet))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_938);
        }
        IData result = new DataMap();
        result.put("USER_INFO", userInfo);//用户信息
        result.put("IDENT_INFO", dataSet.getData(0));//凭证信息
    //    result.put("ACCOUNT_IDENT_INFO", relations);//用户绑定关系信息
        return result;
    }

    public static IData checkUserInfo(IData data) throws Exception
    { 
        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);// 用户资料不存在
        }
        return userInfo;
    }

    public static void checkUserType(IData data) throws Exception
    {
        String type = IDataUtil.chkParam(data, "USER_TYPE");
        if (type == null || type.equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_606);
        }
        if (!"01".equals(type))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "标识类型错误");
        }
    }
    /**
     * 是否是一级客服接入
     *
     *
     * @param channelId
     * @return
     * @throws Exception
     */
    public static boolean isCustomerServiceChannel(String channelId) throws Exception
    {
        if (channelId == null || channelId.equals(""))
        {
            return false;
        }
        return null != StaticUtil.getStaticValue("CUSTSERVICE_CHANNEL_ID", channelId);
    }
    
    public static String getCustomerServiceChannel(String channelId) throws Exception
    {
        if (channelId == null || channelId.equals(""))
        {
            return "";
        }
       return StaticUtil.getStaticValue("CUSTSERVICE_CHANNEL_ID", channelId);               
    }
    
    // 产生用户id后九位的
    public static String genUserId(String userId) throws Exception
    {
        String userIdTemp = "";
        if (userId.length() >= 9) // 加密那里是userid的后九位，不足前面补零
            userIdTemp = userId.substring(userId.length() - 9, userId.length());
        else
        {
            for (int i = 0; i < 9 - userId.length(); i++)
            {
                userIdTemp += "0";
            }
            userIdTemp += userId;
        }
        return userIdTemp;
    }
    
    public static IData checkServicePassword(IData data) throws Exception
    {
        IData info = new DataMap();
        if ("01".equals(data.getString("PWD_TYPE")))
        {// 服务密码,校验密码是否正确
            //服务密码凭证申请不传密码 直接申请
        	if("ABILITY_PLAT".equals(data.getString("CHECK_TYPE"))){//只有一二级能开的才进行密码校验
	            boolean res2 = UserInfoQry.checkUserPassWd(data.getString("USER_ID"), data.getString("PASSWORD"));
	 
	            if (res2 == false)// 密码错误
	            {   
	                info.put("X_RESULTCODE", "-1");
	                info.put("X_RESULTINFO", "服务密码错误!"); 
	            }
        	}
        }
        else if ("02".equals(data.getString("PWD_TYPE")))
        {// 短信随机密码，校验密码是否正确 
            IDataUtil.chkParam(data, "PASSWORD");
            String newpasswd = Encryptor.fnEncrypt(data.getString("PASSWORD"), genUserId(data.getString("USER_ID")));// 密文密码
            
            IDataset dataset = UserIdentInfoQry.queryPWD(data.getString("USER_ID"), newpasswd, data.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(dataset))
            { 
                info.put("X_RESULTCODE", "22036");//短信随机密码输入错误
                info.put("X_RESULTINFO", "随机密码不正确！"); 
            }
            else
            {
                //被验证过
                if ("1".equals(dataset.getData(0).getString("PWD_FLAG", "")))
                {
                    info.put("X_RESULTCODE", "22036");//短信随机密码输入错误
                    info.put("X_RESULTINFO", "随机密码不能重复验证！");
                }
                else
                {
                    UserIdentInfoQry.updatePWDLog(data);// 更新为已验证标识
                }

            }
        }
        return info;
    }

    /**
     * 
     *校验用户是否存在绑定关系
     *
     * @param userInfo
     * @throws Exception 
     */
    public static IData checkBindingRelation(IData queryCondition) throws Exception
    {
        //校验客户是否已经绑定了手机号码
        IDataset dataset = CustServiceHelper.queryUserAccount(queryCondition); 

        if (IDataUtil.isEmpty(dataset))
        {
            IData info =  new DataMap();
            info.put("X_RESULTCODE", "22012");//短信随机密码输入错误
            info.put("X_RESULTINFO", "用户未绑定或已去绑定"); 
            return info;
//            CSAppException.apperr(CrmCommException.CRM_COMM_103, "此用户绑定关系不存在");
        }
        return dataset.getData(0); 
    }
    
    public static String buildIdentInfo(IData data) throws Exception
    {
        String strDate = DateFormatUtils.format(Calendar.getInstance().getTime(),"yyyyMMdd");
        IDataset out = UserIdentInfoQry.getseqString();
        String seqId = ((IData) out.get(0)).getString("OUTSTR", "");
        String identCode = "IDENTCODE"+strDate+((BizVisit)BaseBean.getVisit()).getStaffEparchyCode()+seqId;
        
      
        String EFFECTIVE_TIME = data.getString("EFFECTIVE_TIME", "");
        int effctTime = 300;
        if (EFFECTIVE_TIME != null && !"".equals(EFFECTIVE_TIME))
        {
            effctTime = Integer.parseInt(EFFECTIVE_TIME);
        }
        String registTime = SysDateMgr.getSysTime();
        String endDate = SysDateMgr.date2String(DateUtils.addSeconds(SysDateMgr.string2Date(registTime,
                                                                                            "yyyy-MM-dd HH:mm:ss"),
                                                                     effctTime), "yyyy-MM-dd HH:mm:ss");

        data.put("IDENT_START_TIME", registTime);
        data.put("IDENT_END_TIME", endDate);
        data.put("IDENT_CODE", identCode);
        
        return identCode;
    }

    /*
    * 品牌代码转换 输入值： G001：全球通 G002：神州行 G010：动感地带 返回值： 01：全球通；02：神州行；03：动感地带；09：其他品牌
    */
    public static String convertBrandCode(String brandCode) throws Exception
    {
        String group_brand = StaticUtil.getStaticValue((BizVisit) BaseBean.getVisit(), "TD_S_COMMPARA",
                                                       new String[] {"PARAM_ATTR", "PARAM_CODE"}, "PARA_CODE1",
                                                       new String[] {"998", brandCode});
        String result = "";
        if (StringUtils.isBlank(group_brand))
        {
            return "";
        }

        if ("0".equals(group_brand))
        {
            result = "01";// 全球通
        }
        else if ("1".equals(group_brand))
        {
            result = "02";// 神州行
        }
        else if ("2".equals(group_brand))
        {
            result = "03";// 动感地带
        }
        else if ("3".equals(group_brand))
        {
            result = "02";// 神州行
        }
        else
        {
            result = "09";// 其它品牌
        }

        return result;
    }

    /**
    * 获取用户状态编码00  正常
    01  单向停机
    02  停机
    03  预销户
    04  销户
    05  过户
    06  改号
    99  此号码不存在
    */
    public static String getUserStateParam(String param)
    {
        String result = "";

        if ("0".equals(param))
            result = "00";
        else if ("1".equals(param))
            result = "02";
        else if ("2".equals(param))
            result = "02";
        else if ("3".equals(param))
            result = "02";
        else if ("4".equals(param))
            result = "02";
        else if ("5".equals(param))
            result = "02";
        else if ("6".equals(param))
            result = "04";
        else if ("7".equals(param))
            result = "02";
        else if ("8".equals(param))
            result = "03";
        else if ("9".equals(param))
            result = "03";
        else if ("A".equals(param))
            result = "01";
        else if ("B".equals(param))
            result = "01";
        else if ("C".equals(param))
            result = "02";
        else if ("D".equals(param))
            result = "02";
        else if ("E".equals(param))
            result = "04";
        else if ("F".equals(param))
            result = "03";
        else if ("G".equals(param))
            result = "01";
        else if ("H".equals(param))
            result = "03";
        else if ("I".equals(param))
            result = "02";
        else if ("J".equals(param))
            result = "02";
        else if ("K".equals(param))
            result = "02";
        else if ("L".equals(param))
            result = "02";
        else if ("M".equals(param))
            result = "02";
        else if ("N".equals(param))
            result = "00";
        else if ("O".equals(param))
            result = "02";
        else if ("Q".equals(param))
            result = "02";
        else
            result = "00";

        return result;
    }
    
    public static IData buildBrandAndStatus(IData data) throws Exception
    {
        IData result = new DataMap();
        QueryInfoBean queryInfoBean = BeanManager.createBean(QueryInfoBean.class);
        data.put("X_GETMODE", 1);// 1-输入用户标识
        IDataset cust = queryInfoBean.getUserCustAcct(data);
        String userBrand = cust.getData(0).getString("BRAND_CODE", "");//用户品牌        
        String userStatus = cust.getData(0).getString("USER_STATE_CODESET", "");// 用户状态
        userBrand = CustServiceHelper.convertBrandCode(userBrand);
        userStatus = CustServiceHelper.getUserStateParam(userStatus);
        result.put("BRAND", userBrand);
        result.put("STATUS", userStatus);//用户状态
        return result;
    }
    
    /**
     * 根据USER_ID 查询用户账号表
     * @param userId
     * @param microAccount
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAccount(IData param) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_ACCOUNT", "SEL_USERACCOUNT_BY_USERID", param);
    }
    /**
     * 查询自助服务密码请求
     *
     *
     * @param param
     * @return
     * @throws Exception
     */
    public static IData querySelfServiceCertificateRequest(IData param) throws Exception
    {   
        IData result = new DataMap();
        
        IDataset reqRes = Dao.qryByCodeParser("TF_F_USER_ACCOUNT", "SEL_OPR_NUMB_BY_SERIALNUMBER", param);
        

        if (null == reqRes || reqRes.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "此用户自助服务密码请求不存在");
        } 
        result = reqRes.getData(0);
        
        return result;
    }
    /**
     * 更新绑定关系表
     *
     *
     * @param params
     * @throws Exception
     */
    public static void updateIdentInfo(IData params) throws Exception
    {
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" UPDATE TF_F_USER_ACCOUNT SET "); 
        parser.addSQL(" BIND_TIME =to_date(:BIND_TIME,'YYYY-MM-DD HH24:MI:SS')  ,"); 
        parser.addSQL(" REMOVE_TAG=:REMOVE_TAG ,");
        parser.addSQL(" REMOVE_TIME=to_date(:REMOVE_TIME,'YYYY-MM-DD HH24:MI:SS')  ,"); 
        parser.addSQL(" ERROR_NUMB=:ERROR_NUMB ,");
        parser.addSQL(" UPDATE_STAFF_ID=:UPDATE_STAFF_ID,");
        parser.addSQL(" UPDATE_DEPART_ID=:UPDATE_DEPART_ID, ");
        parser.addSQL(" UPDATE_TIME=sysdate ");
        parser.addSQL(" WHERE 1=1   ");
        parser.addSQL(" AND USER_ID= :USER_ID ");
        parser.addSQL(" AND PARTITION_ID=MOD(:USER_ID, 10000)");
        parser.addSQL(" AND ACCOUNT = :ACCOUNT");  
        parser.addSQL(" AND ROWID = :ROWID");  
        Dao.executeUpdate(parser);
    }
    
    /**
     * 更新绑定关系表
     *
     *
     * @param params
     * @throws Exception
     */
    public static void updateCertificateInfo(IData params) throws Exception
    {
        SQLParser parser = new SQLParser(params);
        parser.addSQL(" UPDATE TF_F_USER_CERTIFICATE SET ");  
        parser.addSQL(" IDENT_END_TIME=to_date(:IDENT_END_TIME,'YYYY-MM-DD HH24:MI:SS') ,");  
        parser.addSQL(" UPDATE_STAFF_ID=:UPDATE_STAFF_ID,");
        parser.addSQL(" UPDATE_DEPART_ID=:UPDATE_DEPART_ID, ");
        parser.addSQL(" RSRV_STR2=:RSRV_STR2, "); 
        parser.addSQL(" UPDATE_TIME=sysdate ");
        parser.addSQL(" WHERE 1=1   ");
        parser.addSQL(" AND USER_ID= :USER_ID ");
        parser.addSQL(" AND PARTITION_ID=MOD(:USER_ID, 10000)"); 
        parser.addSQL(" AND ROWID=:ROWID"); 
        Dao.executeUpdate(parser);
    }
    
    public static IDataset queryCertificate(IData params) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_USER_CERTIFICATE", "SEL_CERTIFICATE_BY_USERID", params);
        
    }
    
    public static IData buildUserCreditInfo(IData input) throws Exception
    {
        IData userInfo = new DataMap();
        //2.添加用户信誉度信息
        IData queryData = AcctCall.getUserCreditInfos("0",
                                                      input.getString("USER_UNIQUE", input.getString("USER_ID")))
                .getData(0);
        
        
        IData transMap = new DataMap();
        transMap.put("-1", "13");//未评级客户   
        transMap.put("0", "12");//0星级客户   
        transMap.put("1", "11");//1星客户    
        transMap.put("2", "10");//2星客户    
        transMap.put("3", "09");//3星客户    
        transMap.put("4", "08");//4星客户    
        transMap.put("5", "07");//5星普通客户  
        transMap.put("6", "06");// 5星金客户   
        transMap.put("7", "05");// 5星钻客户   
        
        userInfo.put("STAR_LEVEL", transMap.getString(queryData.getString("CREDIT_CLASS")));
        
        
        String starScore = queryData.getString("STAR_SCORE", "0");
        if ("".equals(starScore) || starScore == null)
        {
            starScore = "0";
        }
        userInfo.put("STAR_SCORE", starScore);
        if (!"0".equals(userInfo.getString("STAR_LEVEL")))
        {
            String starTime = queryData.getString("STAR_TIME");
            userInfo.put("STAR_TIME", starTime);
        }
        return userInfo;
    }
}
