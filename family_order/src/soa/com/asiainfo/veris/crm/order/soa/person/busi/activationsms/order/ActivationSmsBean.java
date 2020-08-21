package com.asiainfo.veris.crm.order.soa.person.busi.activationsms.order;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealBbossSmsInfoBean;

/**
 * 系统自动触发号卡激活提醒短信bean
 * @author zlx
 * 2018.9.13
 */
public class ActivationSmsBean extends CSBizBean {

    static Logger log = Logger.getLogger(ActivationSmsBean.class);
    //未激活状态
    static final String USER_ACCT_TAG = "2";
    //静态参数配置
    static final String COMM_SUBSYS_CODE = "CSM";
    static final String COMM_PARAM_ATTR = "90";
    static final String COMM_SEND_SMS_DAYS = "SMS_DAYS";
    static final String COMM_SEND_SMS_INFO = "SMS_INFO";


    /**
     * 给所有指定天数未激活用户发送短信
     * 在预开户的T+8、T+11、T+14、T+17、T+20、T+23、T+26 当天，分别发送短信提醒客户激活号码
     * @param data
     * @return
     * @throws Exception
     */
    public IData execute(IData data) throws Exception
    {
        if(log.isDebugEnabled()){
            log.debug("-----------进入 ActivationSmsBean.sendSMS-------");
        }
        data.put("RESULT_CODE","SUCCESS");
        data.put("X_RESULTCODE","0");
        data.put("X_RESULTINFO","OK");
        
        //需发信息的天数list
        String commDays = getCommParamName(COMM_SEND_SMS_DAYS);
        //得到短信内容
        String msgStr = getCommParamName(COMM_SEND_SMS_INFO);

        if (StringUtils.isNotBlank(commDays) && StringUtils.isNotBlank(msgStr)) {
            String[] split = commDays.split(",");
            List<String> dayList = Arrays.asList(split);
            String day=data.getString("DAYS","");
            //判断是否在须发信息的天数内
            if (dayList.contains(day))
            {
            	String serialNumber=data.getString("SERIAL_NUMBER","");
                IData params = new DataMap();
                //依据开户号码查询用户预留的联系电话
                params.put("SERIAL_NUMBER", serialNumber);
                IDataset postcardInfos = Dao.qryByCode("TD_B_POSTCARD_INFO", "SEL_BY_SERIAL_NUMBER", params, Route.getCrmDefaultDb());
                if(IDataUtil.isNotEmpty(postcardInfos)) {
                    IData returnData=  postcardInfos.getData(0);
                    String phone_number = returnData.getString("POST_PHONE");
                    String state = returnData.getString("STATE");//开户状态 0初始化，1预开，2激活，4销户
                    if ("1".equals(state)) {
                    	//发短信操作
                        sendSms(msgStr, serialNumber, phone_number);
                    }
                } else {
                    data.put("X_RESULTINFO", "未获取到用户预留的联系电话，用户开户号码为："+serialNumber);
                }
            }
        } else {
            data.put("RESULT_CODE", "ERROR");
            data.put("X_RESULTCODE", "-1");
            data.put("X_RESULTINFO", "未获取到天数或短信具体内容");
        }
        if(log.isDebugEnabled()){
            log.debug("-----------退出 ActivationSmsBean.sendSMS-------");
        }
        return data;
    }

    /****
     * 发短信处理(发短信前必须判断用户状态是否已激活)
     * @param msgStr   短信内容
     * @param serialNumber   开户手机号码
     * @param phoneNumber    预留联系方式
     * @throws Exception
     */
    public void sendSms(String msgStr,String serialNumber,String phoneNumber) throws Exception
    {
        //发短信之前验证用户是否已经激活
        DataMap pNumber = new DataMap();
        pNumber.put("SERIAL_NUMBER", serialNumber);
        pNumber.put("REMOVE_TAG", "0");
        IDataset acctTagData = Dao.qryByCode("TF_F_USER", "SEL_USER_BY_SN", pNumber);
        if (IDataUtil.isNotEmpty(acctTagData) &&
                IDataUtil.isNotEmpty(acctTagData.getData(0)))
        {
            String acctag = acctTagData.getData(0).getString("ACCT_TAG");
            //如果未激活调用发短信接口给预留号码发短信
            if (USER_ACCT_TAG.equals(acctag))
            {
                DealBbossSmsInfoBean.combinCommonDate(msgStr, phoneNumber);
                if (log.isDebugEnabled())
                {
                    log.debug("-----------send sms success-----"+phoneNumber);
                }
            }
        }
    }

    /***
     * 获取静态参数配置
     * @param param_code 静态参数编码
     * @return
     * @throws Exception
     */
    public String getCommParamName(String param_code) throws Exception
    {
        //参数
        IData param1 = new DataMap();
        param1.put("SUBSYS_CODE", COMM_SUBSYS_CODE);
        param1.put("PARAM_ATTR", COMM_PARAM_ATTR);
        param1.put("PARAM_CODE", param_code);
        //查询td_s_commpara表获得需发短信天数字符串和短信内容
        IDataset commDataset = Dao.qryByCode("TD_S_COMMPARA", "SEL_BY_SUBATTRPARAMCODE", param1, Route.CONN_CRM_CEN);
        if(IDataUtil.isNotEmpty(commDataset) &&
                IDataUtil.isNotEmpty(commDataset.getData(0)))
        {
            if(log.isDebugEnabled()){
                log.debug("-----------get commonParam success-------"+commDataset.getData(0).getString("PARAM_NAME"));
            }

            return commDataset.getData(0).getString("PARAM_NAME");
        }
        return null;
    }
}
