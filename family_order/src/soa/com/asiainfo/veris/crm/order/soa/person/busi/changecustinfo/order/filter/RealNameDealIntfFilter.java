
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.filter;  

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.person.busi.changepassword.UserPasswordInfoComm;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

/**
 * 手机实名制办理 接口 入参过滤类
 * 
 * @author Administrator
 */
public class RealNameDealIntfFilter implements IFilterIn
{

	static Logger logger=Logger.getLogger(RealNameDealIntfFilter.class);
    /**
     * check pspt
     * 
     * @param value
     * @param desc
     * @param psptTypeCode
     *            ：证件类型
     * @return 成功返回空字符串，失败则返回错误信息
     */
    private String checkPspt(String value, String desc, String psptTypeCode)
    {
        String[] errors =
        { "验证通过", "身份证号码位数不对", "身份证号码不合法", "身份证号码校验错误", "身份证地区非法", "身份证出生日期不符合要求", "身份证为空" };
        if (value == null || "".equals(value))
            return desc + "(" + value + ")" + errors[4] + ";";
        IData area = new DataMap();
        area.put("11", "\u5317\u4EAC");
        area.put("12", "\u5929\u6D25");
        area.put("13", "\u6CB3\u5317");
        area.put("14", "\u5C71\u897F");
        area.put("15", "\u5185\u8499\u53E4");
        area.put("21", "\u8FBD\u5B81");
        area.put("22", "\u5409\u6797");
        area.put("23", "\u9ED1\u9F99\u6C5F");
        area.put("31", "\u4E0A\u6D77");
        area.put("32", "\u6C5F\u82CF");
        area.put("33", "\u6D59\u6C5F");
        area.put("34", "\u5B89\u5FBD");
        area.put("35", "\u798F\u5EFA");
        area.put("36", "\u6C5F\u897F");
        area.put("37", "\u5C71\u4E1C");
        area.put("41", "\u6CB3\u5357");
        area.put("42", "\u6E56\u5317");
        area.put("43", "\u6E56\u5357");
        area.put("44", "\u5E7F\u4E1C");
        area.put("45", "\u5E7F\u897F");
        area.put("46", "\u6D77\u5357");
        area.put("50", "\u91CD\u5E86");
        area.put("51", "\u56DB\u5DDD");
        area.put("52", "\u8D35\u5DDE");
        area.put("53", "\u4E91\u5357");
        area.put("54", "\u897F\u85CF");
        area.put("61", "\u9655\u897F");
        area.put("62", "\u7518\u8083");
        area.put("63", "\u9752\u6D77");
        area.put("64", "\u5B81\u590F");
        area.put("65", "\u65B0\u7586");
        area.put("71", "\u53F0\u6E7E");
        area.put("81", "\u9999\u6E2F");
        area.put("82", "\u6FB3\u95E8");
        area.put("91", "\u56FD\u5916");

        String idcard = value, Y, JYM;
        String S, M, ereg;
        Calendar c = Calendar.getInstance();
        if (idcard.charAt(idcard.length() - 1) == '*')
            idcard = idcard.substring(0, idcard.length() - 1) + 'X';

        if (!area.containsKey(idcard.substring(0, 2)))
        {
            return desc + "(" + value + ")" + errors[4] + ";";
        }
        switch (idcard.length())
        {
            case 15:
                if ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0 || ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 100 == 0 && (Integer.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0))
                {
                    ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";
                }
                else
                {
                    ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}$";
                }
                boolean bTemp = Pattern.compile(ereg).matcher(idcard).find();
                if (bTemp)
                {
                    Matcher matches = Pattern.compile(ereg).matcher(idcard);
                    c.setTime(new java.util.Date());
                    int nowY = c.get(Calendar.YEAR);
                    if (matches.groupCount() > 0)
                    {
                        if (Integer.parseInt(("19" + idcard.substring(6, 8))) + 100 < nowY)
                        {
                            return desc + "(" + value + ")" + errors[5] + ";";
                        }
                    }
                    return "";
                }
                else
                {
                    return desc + "(" + value + ")" + errors[2] + ";";
                }
            case 18:
                if (Integer.parseInt(idcard.substring(6, 10)) % 4 == 0 || (Integer.parseInt(idcard.substring(6, 10)) % 100 == 0 && Integer.parseInt(idcard.substring(6, 10)) % 4 == 0))
                {
                    ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";
                }
                else
                {
                    ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}[0-9Xx]$";
                }
                boolean bTemp18 = Pattern.compile(ereg).matcher(idcard).find();
                if (bTemp18)
                {
                    Pattern pattern = Pattern.compile(ereg);
                    Matcher matches = pattern.matcher(idcard);
                    c.setTime(new java.util.Date());
                    int nowY = c.get(Calendar.YEAR);
                    if (matches.groupCount() > 0)
                    {
                        int iYear = Integer.parseInt(idcard.substring(6, 10));
                        /*------modify by chenzg@20131122--身份证件类型新增户口本(REQ201311080002)---begin----*/
                        /* 户口本证件类型，用户的也是身份证，但是校验身份证号码时，不限制必须大于15岁 */
                        if ("2".equals(psptTypeCode))
                        {
                            if ((iYear + 100) < nowY)
                            {
                                return desc + "(" + value + ")" + errors[5] + ";";
                            }
                        }
                        else
                        {
                           // if ((iYear + 15) > nowY || (iYear + 100) < nowY)
                        	if ((iYear + 100) < nowY)
                            {
                                return desc + "(" + value + ")" + errors[5] + ";";
                            }
                        }
                        /*------modify by chenzg@20131122--身份证件类型新增户口本(REQ201311080002)---end------*/
                    }
                    return "";
                }
                else
                {
                    return desc + "(" + value + ")" + errors[2] + ";";
                }
            default:
                return desc + "(" + value + ")" + errors[2] + ";";
        }
    }

    @Override
    public void transferDataInput(IData inparam) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
        String custName = IDataUtil.chkParam(inparam, "CUST_NAME");
        String psptTypeCode = IDataUtil.chkParam(inparam, "PSPT_TYPE_CODE");
        String psptId = IDataUtil.chkParam(inparam, "PSPT_ID");
        String serialMain = IDataUtil.chkParam(inparam, "SERIAL_MAIN");
        
        String passNumber = "";
        String lssueNumber = "";
        
        if("Q".equals(psptTypeCode))
        {
        	passNumber = IDataUtil.chkParam(inparam, "PASS_NUMBER");//通行证号码
        	lssueNumber = IDataUtil.chkParam(inparam, "LSSUE_NUMBER");//签证次数
        }

        if (!StringUtils.equals(serialMain, serialNumber))
        {
            String userPwd = inparam.getString("USER_PASSWD");
            if (StringUtils.isBlank(userPwd))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1195);// 主叫号码 与受理号码不相等，受理用户密码不能为空！
            }

            IData checkRlt = UserPasswordInfoComm.checkUserPWD(inparam);
            String resCode = checkRlt.getString("X_RESULTCODE", "");

            if (StringUtils.isNotBlank(resCode))
            {
                if (!StringUtils.equals("0", resCode))
                {
                    String resDesc = checkRlt.getString("X_RESULTINFO", "");
                    CSAppException.apperr(CrmCommException.CRM_COMM_13, resCode, resDesc);
                }
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1114);// 802011:用户服务密码不合法！
            }
        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_544, serialNumber);// 根据服务号码[%s]查询不到用户信息！
        }

        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_165);// 获取客户资料无数据!
        }

        // 允许非实名制的报停、挂失状态客户登记实名制
        boolean stateFlag = false;
        String stateCode = userInfo.getString("USER_STATE_CODESET", "");
        String isRealNmae = custInfo.getString("IS_REAL_NAME", "");
        if (!"1".equals(isRealNmae))
        {// 非实名客户
            if ("1".equals(stateCode) || "2".equals(stateCode))
            {
                stateFlag = true; // 报停、挂失状态客户时，不做拦截
            }
        }
        if (!stateFlag)
        { // 允许非实名制的报停、挂失状态客户登记实名制
            String acctTag = userInfo.getString("ACCT_TAG", "");
            String openDate = userInfo.getString("OPEN_DATE", "");
            int daysBeforeTd = SysDateMgr.dayInterval(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD), openDate);
            if (!StringUtils.equals("2", acctTag) && daysBeforeTd != 0)
            {// 智能CRM客户资料变更（实名制登记），拦截已激活用户的登记 20160815
                //CSAppException.apperr(CrmUserException.CRM_USER_1113);// 802007:必须是待激活状态的用户或当天激活的用户方可办理实名制业务！

            }
        }

        if (StringUtils.equals("1", custInfo.getString("IS_REAL_NAME", "")))
        {
            CSAppException.apperr(CustException.CRM_CUST_1006);// 该用户已经办理了实名制业务
        }

        // 证件类型合法性校验
        if (StringUtils.equals("SD", CSBizBean.getVisit().getInModeCode()))
        {
            IDataset typeSet = StaticUtil.getStaticList("TD_S_PASSPORTTYPE", psptTypeCode);
            if (IDataUtil.isEmpty(typeSet))
            {
                CSAppException.apperr(CustException.CRM_CUST_130);// 证件类型错误

            }
            else
            {
                if (StringUtils.equals("B", psptTypeCode) || StringUtils.equals("F", psptTypeCode) || StringUtils.equals("K", psptTypeCode))
                {
                    CSAppException.apperr(CustException.CRM_CUST_1007);// 此证件类别无法通过短信办理实名制登记
                }
            }
        }
        else
        {
            if (StringUtils.equals("C", psptTypeCode))
            {
                CSAppException.apperr(CustException.CRM_CUST_1007);// 此证件类别无法通过短信办理实名制登记
            }
        }

        // 身份证号码校验
        if (StringUtils.equals("0", psptTypeCode) || StringUtils.equals("1", psptTypeCode) || StringUtils.equals("2", psptTypeCode))
        {
            if (psptId.length() == 15 || psptId.length() == 18)
            {
 
                String reStr = checkPspt(psptId, "", psptTypeCode);
                if (StringUtils.isNotBlank(reStr))
                {
                    CSAppException.apperr(CustException.CRM_CUST_116);// 证件号码不正确
                }
                
              //检查身份证出生日期是否在12(含)-120岁(含)之间
//                try {
                    String tempPsptId = psptId;
                    if (tempPsptId.length() == 15) {
                        tempPsptId = IdcardUtils.conver15CardTo18(tempPsptId);
                        if (tempPsptId == null || tempPsptId.equals("")) {
                            CSAppException.apperr(CustException.CRM_CUST_116);// 证件号码不正确
                        }
                    }
                    
                    /**
        			 * REQ201808100006	关于调整实名制相关规则的需求 by MengQX 20181009
        			 * 办理人16岁以下，无代办入网权限不给办理，有权限则需要经办人信息，经办人必须满16岁
        			 * @param idCard
        			 * @return
        			 */
                    int age = IdcardUtils.getExactAgeByIdCard(tempPsptId);
                    if (age < 16) {
                    	String staffId = CSBizBean.getVisit().getStaffId();	
                    	boolean flag = StaffPrivUtil.isFuncDataPriv(staffId, "OP_AGENT_PRIV");//代办入网权限
                    	
                    	if(flag){
                    		//有权限，需要经办人信息
                    		String agentCustName = IDataUtil.chkParam(inparam, "AGENT_CUST_NAME");
                            String agentPsptTypeCode = IDataUtil.chkParam(inparam, "AGENT_PSPT_TYPE_CODE");
                            String agentPsptID = IDataUtil.chkParam(inparam, "AGENT_PSPT_ID");
                            String agentPsptAddr = IDataUtil.chkParam(inparam, "AGENT_PSPT_ADDR");

                            if(StringUtils.equals("0", agentPsptTypeCode) || StringUtils.equals("1", agentPsptTypeCode) || StringUtils.equals("2", agentPsptTypeCode))
                            {
                            	if (agentPsptID.length() == 15 || agentPsptID.length() == 18)
                                {
                            		String returnStr = checkPspt(agentPsptID, "", agentPsptTypeCode);
                                    if (StringUtils.isNotBlank(returnStr))
                                    {
                                        CSAppException.apperr(CustException.CRM_CUST_199);// 经办人证件号码错误
                                    }
                                    
                                    String tempAgentPsptId = agentPsptID;
                                    if (tempAgentPsptId.length() == 15) {
                                    	tempAgentPsptId = IdcardUtils.conver15CardTo18(tempAgentPsptId);
                                        if (tempAgentPsptId == null || tempAgentPsptId.equals("")) {
                                            CSAppException.apperr(CustException.CRM_CUST_199);// 经办人证件号码错误
                                        }
                                    }
                                    
                                    int agentAge = IdcardUtils.getExactAgeByIdCard(tempAgentPsptId);
                                    if(agentAge < 16){
                                    	CSAppException.apperr(CustException.CRM_CUST_198);//经办人年龄不能小于16岁
                                    }
                                }else
                                {
                                    CSAppException.apperr(CustException.CRM_CUST_199);// 经办人证件号码错误
                                }
                            }
//                            else{
//                            	CSAppException.apperr(CustException.CRM_CUST_192);// 经办人只能选择个人证件
//                            }
                    	}else {
                    		//无权限，直接报错
                    		//该工号没有代办入网的权限！（仅限自办营业厅工号能申请该权限）
                    		CSAppException.apperr(CustException.CRM_CUST_197);
                    	}
                    } else if (age > 120) {
                        CSAppException.apperr(CustException.CRM_CUST_196);// 身份证年龄大于120岁，不能办理该业务！
                    }

//                } catch (Exception e) {
//                    CSAppException.apperr(CustException.CRM_CUST_116);//  
//                }

            }
            else
            {
                CSAppException.apperr(CustException.CRM_CUST_116);// 证件号码不正确
            }
        }

        // 查询个人客户
        IData custPerson = UcaInfoQry.qryPerInfoByCustId(userInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(custPerson))
        {
            CSAppException.apperr(CustException.CRM_CUST_69);// 获取个人客户资料无数据!
        }

        // 如下几个key清除，避免覆盖传入进来的参数
        custPerson.remove("CUST_NAME");
        custPerson.remove("PSPT_TYPE_CODE");
        custPerson.remove("PSPT_ID");
        custPerson.remove("PSPT_ADDR");

        inparam.putAll(custPerson);// 加入原个人客户其他字段信息，build里面会使用到。
        inparam.put("IS_REAL_NAME", "1");// 写死是实名制
        
        /**
         * REQ201608300005 开户、客户资料变更优化需求
         * 20160901
         * @zhuoyingzhi
         */
         if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)||"2".equals(psptTypeCode)){
         	inparam.put("BIRTHDAY", getBirthdayByPspt(psptId));
         }
    }
    
    /**
     * 通过身份证号码过去  年月日
     * @param pspt
     * @return
     * @throws Exception
     */
    public static  String  getBirthdayByPspt(String psptId)throws Exception{
    	try {
    		String birthday="";
    		//460006 19890217 3112
    		if(psptId.length() == 18){
    			 /*18位身份证号码形成 结构
    			 *前俩字是代表省份或直辖市、自治区。
    			 *3.4位是地级市的代码。
    			 *5.6是县级市的代码。
    			 *7-14位是你的生日编号。
    			 *15-17为了区分同日期生的人和男女性别，男单女双。
    			 *最后以为是检验码，由数字或大写X组成。
    			 */
    			//410184 19880513 183X
    			birthday=psptId.substring(6, 14);
    		}
    		if(psptId.length() == 15){
    			//130503 670401 001
    			 /*15位身份证号码各位的含义: 
    				 *1-2位省、自治区、直辖市代码； 
    				 *3-4位地级市、盟、自治州代码； 
    				 *5-6位县、县级市、区代码； 
    				 *7-12位出生年月日,比如670401代表1967年4月1日,与18位的第一个区别； 
    				 *13-15位为顺序号，其中15位男为单数，女为双数； 
    				 *与18位身份证号的第二个区别：没有最后一位的验证码。    
    		      */
    			birthday=psptId.substring(6, 12);
    			birthday="19"+birthday;
    		}
    		return birthday;
		} catch (Exception e) {
			if(logger.isInfoEnabled())  logger.info(e);
			throw e;
		}
    }
}
