
package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order.requestdata.ChangeCustOwnerReqData;

public class BuildChangeCustOwnerReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData input, BaseReqData brd) throws Exception
    {
        ChangeCustOwnerReqData reqData = (ChangeCustOwnerReqData) brd;
        /************** 客户资料 *************/
        reqData.setNewCustName(input.getString("CUST_NAME", ""));// 客户名称
        reqData.setNewPsptTypeCode(input.getString("PSPT_TYPE_CODE", ""));// 证件类型
        reqData.setNewPsptId(input.getString("PSPT_ID", ""));// 证件号码
        reqData.setNewPsptAddr(input.getString("PSPT_ADDR", ""));// 证件地址
        reqData.setNewPsptEndDate(input.getString("PSPT_END_DATE", ""));// 证件有效期
        reqData.setNewBirthday(input.getString("BIRTHDAY", ""));// 出生日期
        reqData.setNewPhone(input.getString("PHONE", ""));// 联系电话
        reqData.setNewPostCode(input.getString("POST_CODE", ""));// 通信邮编
        reqData.setNewPostAddress(input.getString("CONTACT_ADDRESS", ""));// 通信地址 --检查key是否正确
        reqData.setNewContact(input.getString("CONTACT", ""));// 联系人姓名
        reqData.setNewContactPhone(input.getString("CONTACT_PHONE", ""));// 联系人电话
        reqData.setNewContactTypeCode(input.getString("CONTACT_TYPE_CODE", ""));// 优先联系方式
        reqData.setNewWorkName(input.getString("WORK_NAME", ""));// 工作单位
        reqData.setNewWorkDepart(input.getString("WORK_DEPART", ""));// 工作部门
        reqData.setNewEmail(input.getString("EMAIL", ""));// Email地址
        reqData.setNewFaxNbr(input.getString("FAX_NBR", ""));// 传真电话
        reqData.setNewHomeAddr(input.getString("HOME_ADDRESS", ""));// 家庭地址
        reqData.setNewJobTypeCode(input.getString("JOB_TYPE_CODE", ""));// 职业类型
        reqData.setNewSex(input.getString("SEX", ""));// 性别
        reqData.setNewMarriage(input.getString("MARRIAGE", ""));// 婚姻状况
        reqData.setNewEducateDegreeCode(input.getString("EDUCATE_DEGREE_CODE", ""));// 教育程度
        reqData.setNewNationalityCode(input.getString("NATIONALITY_CODE", ""));// 国籍
        reqData.setNewFolkCode(input.getString("FOLK_CODE", ""));// 民族
        reqData.setNewReligionCode(input.getString("RELIGION_CODE", ""));// 信仰
        reqData.setNewLanguageCode(input.getString("LANGUAGE_CODE", ""));// 语言
        reqData.setNewIsRealName(input.getString("IS_REAL_NAME", ""));// 是否实名制
        reqData.setDestroyIMSTag(input.getString("DESTORY_IMS_TAG", ""));// 拆机标识
        reqData.setIMSTag(input.getString("IMS_TAG",""));           
        reqData.setIMSSerialNumber(input.getString("IMS_SERIAL_NUMBER",""));
        reqData.setIMSBrand(input.getString("IMS_BRAND",""));
        reqData.setIMSProduct(input.getString("IMS_PRODUCT",""));

        if (StringUtils.isNotBlank(input.getString("CITY_CODE")))// 客户业务区
        {
            reqData.setNewCityCode(input.getString("CITY_CODE"));
        }
        else
        {
            reqData.setNewCityCode(reqData.getUca().getUser().getCityCode());
        }

        /*************** 账户信息 *************************/
        
        //对模糊化的字段进行处理
        if(input.getString("PAY_NAME","").contains("*") )
        {
        	input.put("PAY_NAME", reqData.getUca().getUserOriginalData().getAccount().getPayName());
        }
        
        if(input.getString("BANK_ACCT_NO","").contains("*") )
        {
        	input.put("BANK_ACCT_NO", reqData.getUca().getUserOriginalData().getAccount().getBankAcctNo());
        }
        
        if(input.getString("CONTRACT_NO","").contains("*") )
        {
        	input.put("CONTRACT_NO", reqData.getUca().getUserOriginalData().getAccount().getContractNo());
        }
		
        reqData.setNewPayName(input.getString("PAY_NAME", ""));// 账户名称
        reqData.setNewPayModeCode(input.getString("PAY_MODE_CODE", ""));// 账户类型
        reqData.setNewAcctDay(input.getString("ACCT_DAY", ""));// 用户账期
        reqData.setNewSuperBankCode(input.getString("SUPER_BANK_CODE", ""));
        reqData.setNewBankCode(input.getString("BANK_CODE", ""));// 银行名称
        reqData.setNewBankAcctNo(input.getString("BANK_ACCT_NO", ""));// 账户名称
        reqData.setNewBankAgreementNo(input.getString("BANK_AGREEMENT_NO", ""));// 银行协议号
        reqData.setNewContractNo(input.getString("CONTRACT_NO", ""));

        //密码
        reqData.setNewPassword(input.getString("NEW_PASSWD"));

        // 经办人信息
        reqData.setAgentCustName(input.getString("AGENT_CUST_NAME"));
        reqData.setAgentPsptTypeCode(input.getString("AGENT_PSPT_TYPE_CODE"));
        reqData.setAgentPsptId(input.getString("AGENT_PSPT_ID"));
        reqData.setAgentPsptAddr(input.getString("AGENT_PSPT_ADDR"));
        
        /********************使用人信息********************/
        //使用人姓名
        reqData.setUseName(input.getString("USE"));
        //用人证件类型
        reqData.setUsePsptTypeCode(input.getString("USE_PSPT_TYPE_CODE"));
        //使用人证件号码
        reqData.setUsePsptId(input.getString("USE_PSPT_ID"));
        //使用人证件地址
        reqData.setUsePsptAddr(input.getString("USE_PSPT_ADDR"));
        
        //REQ201911080010 关于实名入网办理日志留存的改造通知 - add by guonj -20200305 
        reqData.setDevRead(input.getString("custInfo_DEV_READ", ""));
        reqData.setReadRuslt(input.getString("custInfo_READ_RUSLT", ""));
        reqData.setComparisonIs(input.getString("custInfo_COMPARISON_IS", ""));
        reqData.setComparisonRuslt(input.getString("custInfo_COMPARISON_RUSLT", ""));
        reqData.setComparisonSeq(input.getString("custInfo_COMPARISON_SEQ", ""));
        reqData.setAuthenticityIs(input.getString("custInfo_AUTHENTICITY_IS", ""));
        reqData.setAuthenticityRuslt(input.getString("custInfo_AUTHENTICITY_RUSLT", ""));
        reqData.setAuthenticitySeq(input.getString("custInfo_AUTHENTICITY_SEQ", ""));
        reqData.setProvenumIs(input.getString("custInfo_PROVENUM_IS", ""));
        reqData.setProvenumRuslt(input.getString("custInfo_PROVENUM_RUSLT", ""));
        reqData.setProvenumSeq(input.getString("custInfo_PROVENUM_SEQ", ""));
        
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ChangeCustOwnerReqData();
    }

}
