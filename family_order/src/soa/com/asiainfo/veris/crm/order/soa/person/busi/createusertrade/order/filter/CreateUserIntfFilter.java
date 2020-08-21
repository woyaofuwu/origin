
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.RandomStringUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateUserIntfFilter implements IFilterIn
{

    /**
     * 开户接口入参检查
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "TRADE_DEPART_PASSWD");
        IDataUtil.chkParam(param, "TRADE_TYPE_CODE");
        IDataUtil.chkParam(param, "PRODUCT_ID");
        IDataUtil.chkParam(param, "BRAND_CODE");
        IDataUtil.chkParam(param, "USER_TYPE_CODE");
        IDataUtil.chkParam(param, "PAY_NAME");
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "SIM_CARD_NO");
        IDataUtil.chkParam(param, "CUST_NAME");
        IDataUtil.chkParam(param, "PSPT_TYPE_CODE");
        IDataUtil.chkParam(param, "PSPT_ID");
        IDataUtil.chkParam(param, "PAY_MODE_CODE");
        IDataUtil.chkParam(param, "PAY_MONEY_CODE");
        // IDataUtil.chkParam(param, "RES_FEE");
        // IDataUtil.chkParam(param, "X_FPAY_FEE");
        IDataUtil.chkParam(param, "ADVANCE_PAY");
        IDataUtil.chkParam(param, "OPER_FEE");
        IDataUtil.chkParam(param, "FOREGIFT");

    }

    /**
     * 检验代理商登录密码
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkLoginPasswd(IData param) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("IN_IP", "1.2.3.4");
        inparam.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("X_GETMODE", "0");
        inparam.put("PASSWD", param.getString("LOGIN_PASSWD"));
        CSAppCall.call("QSM_ChkSysOrgInfo", inparam);
    }

    /**
     * 获取代理商余额
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public IData getAgentLeavefee(IData param) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        inparam.put("USER_ID", param.getString("USER_ID"));
        inparam.put("OPER_TYPE_CODE", "6");
        IDataset dataset = CSAppCall.call("CHL_ChnlSkyIn", inparam);
        return dataset.getData(0);
    }

    /**
     * 查询代理商用户信息
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public IData getAgentUserInfo(IData param) throws Exception
    {

        IData userInfos = UcaInfoQry.qryUserInfoBySn(param.getString("IN_PHONE"));
        if (userInfos == null || userInfos.isEmpty())
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        return userInfos;
    }

    public void transferDataInput(IData input) throws Exception
    {
        checkInparam(input);
        int moneySub = Integer.parseInt(input.getString("ADVANCE_PAY", "0")) + Integer.parseInt(input.getString("FOREGIFT", "0")) + Integer.parseInt(input.getString("OPER_FEE", "0"));
        if (moneySub != 0)
            input.put("FEE_STATE", input.getString("FEE_STATE", "1"));
        else
            input.put("FEE_STATE", input.getString("FEE_STATE", "0"));
        /*
         * checkLoginPasswd(input); / IData userInfo = getAgentUserInfo(input); / IData leaveFee =
         * getAgentLeavefee(userInfo); String info_type = leaveFee.getString("INFO_TYPE"); if ("1".equals(info_type)) {
         * String trans_money = leaveFee.getString("TRANS_MONEY"); String x_fpay_money = input.getString("X_FPAY_FEE");
         * if (Integer.parseInt(trans_money) < Integer.parseInt(x_fpay_money)) {
         * CSAppException.apperr(AgentsException.CRM_AGENTS_16); } } else if ("2".equals(info_type)) {
         * CSAppException.apperr(AgentsException.CRM_AGENTS_17); } else if ("3".equals(info_type)) {
         * CSAppException.apperr(AgentsException.CRM_AGENTS_16); } else {
         * CSAppException.apperr(AgentsException.CRM_AGENTS_18); }
         */

        String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        IDataset checkMphoneDatas = new DatasetList();
        IDataset simCardInfo = new DatasetList();
        String openType = input.getString("OPEN_TYPE");
        String remark = input.getString("REMARK");
        if(StringUtils.isNotBlank(remark) && remark.endsWith("mobileuseful"))
        {
          //最好用手机开户无需选占
            return;
        }
        if (openType != null && "AGENT_OPEN".equals(openType))
        {
            //代理商开户不重新校验
        }
        else
        {
            checkMphoneDatas = ResCall.checkResourceForMphone("0", serialNumber, "0", "", "2");// 修改
        }
        simCardInfo = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "");
        String imsi = simCardInfo.getData(0).getString("IMSI", "-1");
        String ki = simCardInfo.getData(0).getString("KI", "");
        String res_kind_code = simCardInfo.getData(0).getString("RES_KIND_CODE", "");
        String res_kind_name = simCardInfo.getData(0).getString("RES_KIND_NAME", "");
        String sim_type_code = simCardInfo.getData(0).getString("SIM_TYPE_CODE", "");
        String strResTypeCode = simCardInfo.getData(0).getString("RES_TYPE_CODE", "0"); 
        String opc_value = simCardInfo.getData(0).getString("OPC", "");
        input.put("IMSI", imsi);
        input.put("KI", ki);
        
        input.put("RES_KIND_CODE", res_kind_code);
        input.put("RES_KIND_NAME", res_kind_name);
        input.put("SIM_TYPE_CODE", sim_type_code);
        input.put("RES_TYPE_CODE", strResTypeCode);
        input.put("OPC_VALUE", opc_value);
        input.put("SIM_FEE_TAG", simCardInfo.getData(0).getString("FEE_TAG", ""));

        // add by xieyf5 20180918 start
        IData checkSimData = simCardInfo.getData(0);
        String strSimTypeCode = checkSimData.getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode
        if (!"".equals(checkSimData.getString("EMPTY_CARD_ID", "")) && !"U".equals(strSimTypeCode) && !"X".equals(strSimTypeCode))
        {
            // 如果SIM卡表中EMPTY_CARD_ID字段不为空，标明该卡由白卡写成，到白卡表中取卡类型 IData
            IDataset emptyCardInfo = ResCall.getEmptycardInfo(checkSimData.getString("EMPTY_CARD_ID"), "", "");
            if(IDataUtil.isEmpty(emptyCardInfo)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该SIM卡的白卡信息为空");
            }
            IData newEmptyCardInfo = emptyCardInfo.getData(0);// 资源接口
            String rsrvTag = newEmptyCardInfo.getString("RSRV_TAG1");
            if ("3".equals(rsrvTag)) {
                input.put("SIM_FEE_TAG", "1");
            }
        }
        // add by xieyf5 20180918 end

        String userPwd = input.getString("USER_PASSWD", "");
        if ("".equals(userPwd))
        {
            userPwd = RandomStringUtils.randomNumeric(6);
        }
        input.put("USER_PASSWD", userPwd);
        input.put("ACCT_TAG", "0");
        input.put("OPEN_MODE", "0");
        input.put("USER_TYPE_CODE", "0");

        input.put("CUST_NAME", input.getString("CUST_NAME", "无档户"));
        input.put("PAY_NAME", input.getString("CUST_NAME", "无档户"));
        input.put("PAY_MODE_CODE", input.getString("PAY_MODE_CODE", "0"));
        input.put("PSPT_TYPE_CODE", input.getString("PSPT_TYPE_CODE", "Z"));
        input.put("PSPT_ID", input.getString("PSPT_ID", "11111111111111111111"));
        input.put("PHONE", input.getString("PHONE", "10086"));
        input.put("POST_CODE", input.getString("POST_CODE", "000000"));
        input.put("CONTACT", input.getString("CUST_NAME", "无档户"));
        input.put("CONTACT_PHONE", input.getString("CONTACT_PHONE", "10086"));
        input.put("REAL_NAME", "1");
        input.put("CARD_PASSWD", simCardInfo.getData(0).getString("PASSWORD", ""));// 密码密文 ceshi
        input.put("PASSCODE", simCardInfo.getData(0).getString("KIND", ""));// 密码加密因子 ceshi
        if (StringUtils.isNotBlank(simCardInfo.getData(0).getString("PASSWORD", "")) && StringUtils.isNotBlank(simCardInfo.getData(0).getString("KIND", "")))
            input.put("DEFAULT_PWD_FLAG", "1");
        // 接口ITF_CRM_CreatePersonUser来的开户，city_code强制为HNHK
        if ("1".equals(input.getString("FLAG", "")))
        {
            input.put("CITY_CODE", "HNHK");
        }
        // 外围接口只传递一个productId，需要自己调用拼SELECTED_ELEMENTS 在构建对象里处理

        String netTypeCode = input.getString("NET_TYPE_CODE");
        if (!"".equals(netTypeCode))
        {
            if ("07".equals(netTypeCode))
            {
                input.put("M2M_FLAG", "1");
            }
        }
        
        IDataset uimInfo =ResCall.qrySimCardTypeByTypeCode(strResTypeCode);
        if("01".equals(uimInfo.getData(0).getString("NET_TYPE_CODE")))
        	input.put("FLAG_4G", "1");
        
        /**
         * REQ201608300005 开户、客户资料变更优化需求
         * 20160901
         * @zhuoyingzhi
         */
        //证件类型
        String psptTypeCode=input.getString("PSPT_TYPE_CODE");
        //身份证号码
        String psptId=input.getString("PSPT_ID");
        if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)||"2".equals(psptTypeCode)){
            if (psptId.length() == 15 || psptId.length() == 18)
            {

                String reStr = checkPspt(psptId, "", psptTypeCode);
                if (StringUtils.isNotBlank(reStr))
                {
                    CSAppException.apperr(CustException.CRM_CUST_116);// 证件号码不正确
                }
            }
            else
            {
                CSAppException.apperr(CustException.CRM_CUST_116);// 证件号码不正确
            }
            
        	input.put("BIRTHDAY", getBirthdayByPspt(psptId));
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
			// TODO: handle exception
			throw e;
		}
    }
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
}
