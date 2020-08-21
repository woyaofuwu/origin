
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.requestdata.ModifyCustInfoReqData;

public class BuildModifyCustInfoReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData inParam, BaseReqData brd) throws Exception
    {
        ModifyCustInfoReqData reqData = (ModifyCustInfoReqData) brd;
        reqData.setIsRealName(inParam.getString("IS_REAL_NAME"));
        reqData.setCustName(inParam.getString("CUST_NAME").contains("*") ? reqData.getUca().getCustomer().getCustName() : inParam.getString("CUST_NAME"));// 模糊化处理
        reqData.setPsptId(inParam.getString("PSPT_ID").contains("*") ? reqData.getUca().getCustomer().getPsptId() : inParam.getString("PSPT_ID"));
        reqData.setPsptAddr(inParam.getString("PSPT_ADDR", "").contains("*") ? reqData.getUca().getCustPerson().getPsptAddr() : inParam.getString("PSPT_ADDR"));
        reqData.setPostAddress(inParam.getString("POST_ADDRESS", "").contains("*") ? reqData.getUca().getCustPerson().getPostAddress() : inParam.getString("POST_ADDRESS"));
        reqData.setHomeAddress(inParam.getString("HOME_ADDRESS", "").contains("*") ? reqData.getUca().getCustPerson().getHomeAddress() : inParam.getString("HOME_ADDRESS"));
        reqData.setCityCodeA(inParam.getString("CITY_CODE_A"));
        reqData.setPsptTypeCode(inParam.getString("PSPT_TYPE_CODE", "").contains("*") ? reqData.getUca().getCustomer().getPsptTypeCode() : inParam.getString("PSPT_TYPE_CODE"));
        reqData.setSex(inParam.getString("SEX"));
        reqData.setPsptEndDate(inParam.getString("PSPT_END_DATE"));
        reqData.setPhone(inParam.getString("PHONE", "").contains("*") ? reqData.getUca().getCustPerson().getPhone() : inParam.getString("PHONE"));
        reqData.setContact(inParam.getString("CONTACT"));
        reqData.setContactPhone(inParam.getString("CONTACT_PHONE"));
        reqData.setPostCode(inParam.getString("POST_CODE"));
        reqData.setContactTypeCode(inParam.getString("CONTACT_TYPE_CODE"));
        reqData.setWorkName(inParam.getString("WORK_NAME","").contains("*") ? reqData.getUca().getCustPerson().getWorkName(): inParam.getString("WORK_NAME"));
        reqData.setWorkDepart(inParam.getString("WORK_DEPART"));
        reqData.setBirthday(inParam.getString("BIRTHDAY"));
        reqData.setJobTypeCode(inParam.getString("JOB_TYPE_CODE"));
        reqData.setJob(inParam.getString("JOB"));
        reqData.setEducateDegreeCode(inParam.getString("EDUCATE_DEGREE_CODE"));
        reqData.setEmail(inParam.getString("EMAIL","").contains("*")?reqData.getUca().getCustPerson().getEmail():inParam.getString("EMAIL"));
        reqData.setFaxNbr(inParam.getString("FAX_NBR"));
        reqData.setMarriage(inParam.getString("MARRIAGE"));
        reqData.setNationalityCode(inParam.getString("NATIONALITY_CODE"));
        reqData.setCharacterTypeCode(inParam.getString("CHARACTER_TYPE_CODE"));
        reqData.setWebuserId(inParam.getString("WEBUSER_ID"));
        reqData.setLanguageCode(inParam.getString("LANGUAGE_CODE"));
        reqData.setLocalNativeCode(inParam.getString("LOCAL_NATIVE_CODE"));
        reqData.setCommunityId(inParam.getString("COMMUNITY_ID"));
        reqData.setReligionCode(inParam.getString("RELIGION_CODE"));
        reqData.setFolkCode(inParam.getString("FOLK_CODE"));
        reqData.setRevenueLevelCode(inParam.getString("REVENUE_LEVEL_CODE"));
        reqData.setAgentCustName(inParam.getString("AGENT_CUST_NAME","").contains("*") ? reqData.getUca().getCustomer().getRsrvStr7():inParam.getString("AGENT_CUST_NAME"));
        reqData.setAgentPsptTypeCode(inParam.getString("AGENT_PSPT_TYPE_CODE"));
        reqData.setAgentPsptId(inParam.getString("AGENT_PSPT_ID","").contains("*") ? reqData.getUca().getCustomer().getRsrvStr9():inParam.getString("AGENT_PSPT_ID"));
        reqData.setAgentPsptAddr(inParam.getString("AGENT_PSPT_ADDR","").contains("*") ? reqData.getUca().getCustomer().getRsrvStr10():inParam.getString("AGENT_PSPT_ADDR"));
        reqData.setUse(inParam.getString("USE"));
        reqData.setUsePsptTypeCode(inParam.getString("USE_PSPT_TYPE_CODE"));
        reqData.setUsePsptId(inParam.getString("USE_PSPT_ID","").contains("*") ? reqData.getUca().getCustPerson().getRsrvStr7():inParam.getString("USE_PSPT_ID"));
        reqData.setUsePsptAddr(inParam.getString("USE_PSPT_ADDR","").contains("*") ? reqData.getUca().getCustPerson().getRsrvStr8():inParam.getString("USE_PSPT_ADDR"));
        reqData.setRemark(inParam.getString("REMARK"));
        //微信补登记新增
        reqData.setChannelId(inParam.getString("CHANNEL_ID", ""));
        reqData.setIssusingAuthority(inParam.getString("ISSUING_AUTHORITY", ""));
        reqData.setTransactionId(inParam.getString("TRANSACTION_ID", ""));
        reqData.setPsptStartDate(inParam.getString("PSPT_START_DATE", ""));
        
        reqData.setPassNumber(inParam.getString("PASS_NUMBER", ""));//港澳居住证通行证号码
        reqData.setLssueNumber(inParam.getString("LSSUE_NUMBER", ""));//港澳居住证签证次数
       
        //REQ201911080010 关于实名入网办理日志留存的改造通知 - add by guonj -20200305 
        reqData.setDevRead(inParam.getString("DEV_READ", ""));
        reqData.setReadRuslt(inParam.getString("READ_RUSLT", ""));
        reqData.setComparisonIs(inParam.getString("COMPARISON_IS", ""));
        reqData.setComparisonRuslt(inParam.getString("COMPARISON_RUSLT", ""));
        reqData.setComparisonSeq(inParam.getString("COMPARISON_SEQ", ""));
        reqData.setAuthenticityIs(inParam.getString("AUTHENTICITY_IS", ""));
        reqData.setAuthenticityRuslt(inParam.getString("AUTHENTICITY_RUSLT", ""));
        reqData.setAuthenticitySeq(inParam.getString("AUTHENTICITY_SEQ", ""));
        reqData.setProvenumIs(inParam.getString("PROVENUM_IS", ""));
        reqData.setProvenumRuslt(inParam.getString("PROVENUM_RUSLT", ""));
        reqData.setProvenumSeq(inParam.getString("PROVENUM_SEQ", ""));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ModifyCustInfoReqData();
    }

}
