
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifycustinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifycustinfo.order.requestdata.CttModifyCustInfoReqData;

public class BuildCttModifyCustInfoReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData inParam, BaseReqData brd) throws Exception
    {
        CttModifyCustInfoReqData reqData = (CttModifyCustInfoReqData) brd;
        // reqData.setRemark(inParam.getString("REMARK"));//bof里面已经存在，不需要再设置了
        reqData.setIsRealName(inParam.getString("IS_REAL_NAME"));
        reqData.setCustName(inParam.getString("CUST_NAME").contains("*") ? reqData.getUca().getCustomer().getCustName() : inParam.getString("CUST_NAME"));// 模糊化处理
        reqData.setPsptId(inParam.getString("PSPT_ID").contains("*") ? reqData.getUca().getCustomer().getPsptId() : inParam.getString("PSPT_ID"));
        reqData.setPsptAddr(inParam.getString("PSPT_ADDR").contains("*") ? reqData.getUca().getCustPerson().getPsptAddr() : inParam.getString("PSPT_ADDR"));
        reqData.setPostAddress(inParam.getString("POST_ADDRESS").contains("*") ? reqData.getUca().getCustPerson().getPostAddress() : inParam.getString("POST_ADDRESS"));
        reqData.setHomeAddress(inParam.getString("HOME_ADDRESS").contains("*") ? reqData.getUca().getCustPerson().getHomeAddress() : inParam.getString("HOME_ADDRESS"));
        reqData.setCityCodeA(inParam.getString("CITY_CODE_A"));
        reqData.setPsptTypeCode(inParam.getString("PSPT_TYPE_CODE").contains("*") ? reqData.getUca().getCustomer().getPsptTypeCode() : inParam.getString("PSPT_TYPE_CODE"));
        reqData.setSex(inParam.getString("SEX"));
        reqData.setPsptEndDate(inParam.getString("PSPT_END_DATE"));
        reqData.setPhone(inParam.getString("PHONE").contains("*") ? reqData.getUca().getCustPerson().getPhone() : inParam.getString("PHONE"));
        reqData.setContact(inParam.getString("CONTACT"));
        reqData.setContactPhone(inParam.getString("CONTACT_PHONE"));
        reqData.setPostCode(inParam.getString("POST_CODE"));
        reqData.setContactTypeCode(inParam.getString("CONTACT_TYPE_CODE"));
        reqData.setWorkName(inParam.getString("WORK_NAME"));
        reqData.setWorkDepart(inParam.getString("WORK_DEPART"));
        reqData.setBirthday(inParam.getString("BIRTHDAY"));
        reqData.setJobTypeCode(inParam.getString("JOB_TYPE_CODE"));
        reqData.setJob(inParam.getString("JOB"));
        reqData.setEducateDegreeCode(inParam.getString("EDUCATE_DEGREE_CODE"));
        reqData.setEmail(inParam.getString("EMAIL"));
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
        
        //REQ201908310001关于优化铁通模块客户资料变更界面的需求by liangdg3 start 
        IDataset idCust=CustPersonInfoQry.qryPerInfoByPsptId_2(reqData.getUca().getCustomer().getCustId());
        String oldRespCustName="";
        String oldRespPsptId="";
        String oldRespPsptAddr="";
        if(idCust!=null&&idCust.size()>0){
        	 oldRespCustName=idCust.getData(0).getString("RSRV_STR2","");
             oldRespPsptId=idCust.getData(0).getString("RSRV_STR4","");
             oldRespPsptAddr=idCust.getData(0).getString("RSRV_STR5","");
        }
        reqData.setRespCustName(inParam.getString("RESP_CUST_NAME","").contains("*") ? oldRespCustName:inParam.getString("RESP_CUST_NAME"));
        reqData.setRespPsptTypeCode(inParam.getString("RESP_PSPT_TYPE_CODE"));
        reqData.setRespPsptId(inParam.getString("RESP_PSPT_ID","").contains("*") ? oldRespPsptId:inParam.getString("RESP_PSPT_ID"));
        reqData.setRespPsptAddr(inParam.getString("RESP_PSPT_ADDR","").contains("*") ? oldRespPsptAddr:inParam.getString("RESP_PSPT_ADDR"));
        
        //REQ201908310001关于优化铁通模块客户资料变更界面的需求by liangdg3 end 
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CttModifyCustInfoReqData();
    }

}
