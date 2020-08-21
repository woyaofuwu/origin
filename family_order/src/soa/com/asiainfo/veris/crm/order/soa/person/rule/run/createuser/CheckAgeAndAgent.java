package com.asiainfo.veris.crm.order.soa.person.rule.run.createuser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

/**
 * 检查年龄范围小于10岁大于120岁时，经办人信息是否填写，不填写则返回错误，提示必须填写
 * liquan5 20170103 
 * */
public class CheckAgeAndAgent extends BreBase implements IBREScript {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckAgeAndAgent.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled()){
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckAgeAndAgent() >>>>>>>>>>>>>>>>>>");
        }
        IDataset customer = databus.getDataset("TF_B_TRADE_CUSTOMER");
        if (customer != null && customer.size() > 0) {
            IData data = customer.getData(0);
            String pspt_type_code = data.getString("PSPT_TYPE_CODE", "").trim();//开户人证件类型
            if (pspt_type_code.equals("0") || pspt_type_code.equals("1") || pspt_type_code.equals("2")) {
                String pspt_id = data.getString("PSPT_ID", "");//开户人证件号码
                String birthday = pspt_id.substring(6, 14);
                int age = getExactAge(birthday);
                if (age < 16 || age > 120) {//经办人信息必须填写
//                    String rsrv7 = data.getString("RSRV_STR7", "").trim();//姓名
//                    String rsrv8 = data.getString("RSRV_STR8", "").trim();//证件类型
//                    String rsrv9 = data.getString("RSRV_STR9", "").trim();//证件号码
//                    String rsrv10 = data.getString("RSRV_STR10", "").trim();//地址
//                    if (rsrv7.length() == 0 || rsrv8.length() == 0 || rsrv9.length() == 0 || rsrv10.length() == 0) {
//                        logger.error("CheckAgeAndAgent.java 52");                        
//                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170103, "年龄小于10岁或大于120岁，经办人的各项信息必须填写。");
//                        return true;
//                    }
                	String staffId = CSBizBean.getVisit().getStaffId();	
                	boolean flag = StaffPrivUtil.isFuncDataPriv(staffId, "OP_AGENT_PRIV");//代办入网权限
                	
                	if(flag){
                		//有权限，需要经办人信息
                		String agentCustName = data.getString("RSRV_STR7", "").trim();//姓名
                        String agentPsptTypeCode = data.getString("RSRV_STR8", "").trim();//证件类型
                        String agentPsptID = data.getString("RSRV_STR9", "").trim();//证件号码
                        String agentPsptAddr = data.getString("RSRV_STR10", "").trim();//地址
                        if (agentCustName.length() == 0 || agentPsptTypeCode.length() == 0 || agentPsptID.length() == 0 || agentPsptAddr.length() == 0) {
                        	logger.error("CheckAgeAndAgent.java 52");                        
                        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170103, "年龄小于10岁或大于120岁，经办人的各项信息必须填写。");
                        	return true;
                        }

                        if(StringUtils.equals("0", agentPsptTypeCode) || StringUtils.equals("1", agentPsptTypeCode) || StringUtils.equals("2", agentPsptTypeCode))
                        {
                        	if (agentPsptID.length() == 15 || agentPsptID.length() == 18)
                            {
                                String tempAgentPsptId = agentPsptID;
                                if (tempAgentPsptId.length() == 15) {
                                	tempAgentPsptId = IdcardUtils.conver15CardTo18(tempAgentPsptId);
                                    if (tempAgentPsptId == null || tempAgentPsptId.equals("")) {
//                                        CSAppException.apperr(CustException.CRM_CUST_199);// 经办人证件号码错误
                                    	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170103, "经办人证件号码错误");
                                    	return true;
                                    }
                                }
                                
                                int agentAge = IdcardUtils.getExactAgeByIdCard(tempAgentPsptId);
                                if(agentAge < 16){
//                                	CSAppException.apperr(CustException.CRM_CUST_198);//经办人年龄不能小于16岁
                                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170103, "经办人年龄不能小于16岁");
                                	return true;
                                }
                            }else
                            {
//                                CSAppException.apperr(CustException.CRM_CUST_199);// 经办人证件号码错误
                            	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170103, "经办人证件号码错误");
                            	return true;
                            }
                        }else{
//                        	CSAppException.apperr(CustException.CRM_CUST_192);// 经办人只能选择个人证件
//                        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170103, "经办人只能选择个人证件");
//                        	return true;
                        	return false;
                        }
                	}else {
                		//无权限，直接报错
                		//该工号没有代办入网的权限！（仅限自办营业厅工号能申请该权限）
//                		CSAppException.apperr(CustException.CRM_CUST_197);
                		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20170103, "该工号没有代办入网的权限！（仅限自办营业厅工号能申请该权限）");
                    	return true;
                	}
                }
            }
        }

        return false;
    }

    private int getAge(String birthDateString)
    {
        int age = 0;
        Date now = new Date();

        SimpleDateFormat format_y = new SimpleDateFormat("yyyy");
        SimpleDateFormat format_M = new SimpleDateFormat("MM");
        SimpleDateFormat format_D = new SimpleDateFormat("dd");

        String birth_year = birthDateString.substring(0, 4);
        String this_year = format_y.format(now);

        String birth_month = birthDateString.substring(4, 6);
        String this_month = format_M.format(now);

        String birth_day = birthDateString.substring(6, 8);
        String this_day = format_D.format(now);

        age = Integer.parseInt(this_year) - Integer.parseInt(birth_year);

        if (age == 0) {
            return age;
        } else {
            /* if (this_month.compareTo(birth_month) == 0) {

                 if (this_day.compareTo(birth_day) < 0)
                     age -= 1;
                 if (age < 0)
                     age = 0;
                 return age;
             } else {*/
            // 如果未到出生月份，则age - 1
            if (this_month.compareTo(birth_month) <= 0)
                age -= 1;
            if (age < 0)
                age = 0;
            return age;
            // }
        }
    }
    
    /**
	 * REQ201808100006	关于调整实名制相关规则的需求 by MengQX 20181009
	 * 根据身份证号获取年龄，精确到天
	 * @param birthDateString 出生日期
	 * @return 年龄
	 */
    private int getExactAge(String birthDateString)
    {
    	int iAge = 0;
    	
        String birthYear = birthDateString.substring(0,4);
        String birthMonth = birthDateString.substring(4,6);
        String birthDay = birthDateString.substring(6,8);
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH) + 1;
        int nowDay = cal.get(Calendar.DAY_OF_MONTH);
        
        if(nowYear == Integer.valueOf(birthYear))
        {
        	iAge = 0;//同年 则为0岁
        }
        else
        {
            int ageDiff = nowYear - Integer.valueOf(birthYear) ; //年之差
            if(ageDiff > 0)
            {
                if(nowMonth == Integer.valueOf(birthMonth))
                {
                    int dayDiff = nowDay - Integer.valueOf(birthDay);//日之差
                    if(dayDiff < 0)
                    {
                    	iAge = ageDiff - 1;
                    }
                    else
                    {
                    	iAge = ageDiff ;
                    }
                }
                else
                { 
                    int monthDiff = nowMonth - Integer.valueOf(birthMonth);//月之差
                    if(monthDiff <= 0)
                    {
                    	iAge = ageDiff - 1;
                    }
                    else
                    {
                    	iAge = ageDiff ;
                    }
                }
            }
            else
            {
            	iAge = -1;//返回-1 表示出生日期输入错误 晚于今天
            }
        }
        return iAge;//返回周岁年龄		
    }
}
