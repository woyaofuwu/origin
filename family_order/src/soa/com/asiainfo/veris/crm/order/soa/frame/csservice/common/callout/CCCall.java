
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;


public class CCCall
{
	public static Logger logger = Logger.getLogger(CCCall.class);


	
    /**
     * 新增/修改个人客户信息时，新增或修改参与人信息接口
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData modifyIndividualCustomer(IData param) throws Exception
    {
        IData input = new DataMap();
        input.put("CUST_ID", param.getString("CUST_ID"));
        input.put("CUST_NAME", param.getString("CUST_NAME"));
        input.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
        input.put("PSPT_ID", param.getString("PSPT_ID"));
        input.put("PSPT_ADDR", param.getString("PSPT_ADDR"));
        input.put("PSPT_END_DATE", param.getString("PSPT_END_DATE"));
        input.put("SEX", param.getString("SEX"));
        input.put("BIRTHDAY", param.getString("BIRTHDAY"));
        input.put("BIRTHDAY_LUNAR", param.getString("BIRTHDAY_LUNAR"));
        input.put("IS_RELA_NAME", param.getString("IS_REAl_NAME"));
        input.put("PHONE", param.getString("PHONE"));
        input.put("POST_CODE", param.getString("POST_CODE"));
        input.put("POST_ADDRESS", param.getString("POST_ADDRESS"));
        input.put("MGMT_COUNTY", param.getString("CITY_CODE"));
        input.put("CONTACT", param.getString("CONTACT"));
        input.put("CONTACT_PHONE", param.getString("CONTACT_PHONE"));
        input.put("WORK_NAME", param.getString("WORK_NAME"));
        input.put("WORK_DEPART", param.getString("WORK_DEPART"));
        input.put("HOME_ADDRESS", param.getString("HOME_ADDRESS"));
        input.put("EMAIL", param.getString("EMAIL"));
        input.put("FAX_NBR", param.getString("FAX_NBR"));
        input.put("CONTACT_TYPE_CODE", param.getString("CONTACT_TYPE_CODE"));
        input.put("JOB", param.getString("JOB"));
        
        input.put("PROFESSION", param.getString("CALLING_TYPE_CODE"));
        input.put("MARRIAGE", param.getString("MARRIAGE"));
        input.put("EDUCATE_GRADE_CODE", param.getString("EDUCATE_DEGREE_CODE"));
        input.put("NATIONALITY_CODE", param.getString("NATIONALITY_CODE"));
        
        input.put("LANGUAGE_CODE", param.getString("LANGUAGE_CODE"));
        input.put("FOLK_CODE", param.getString("FOLK_CODE"));
        input.put("RELIGION_CODE", param.getString("RELIGION_CODE"));
        
        ServiceResponse response = BizServiceFactory.call("CCF.outsvc.ICCOutOperateSV.modifyIndividualCustomer", param, null);
        IData out = response.getBody();
        
        return out;
        
    }
    
    /**
     * 新增/修改经办人，责任人，使用人信息的时候，新增或修改参与人信息接口
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IData modifyAttnUserInfo(IData param) throws Exception
    {
        IData input = new DataMap();
        input.put("CUST_ID", param.getString("CUST_ID"));
        input.put("CUST_NAME", param.getString("CUST_NAME"));
        input.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
        input.put("PSPT_ID", param.getString("PSPT_ID"));
        input.put("PSPT_ADDR", param.getString("PSPT_ADDR"));
        input.put("PSPT_END_DATE", param.getString("PSPT_END_DATE"));
        input.put("PARTY_ROLE_SPEC_ID", param.getString("PARTY_ROLE_SPEC_ID"));
        
        ServiceResponse response = BizServiceFactory.call("CCF.outsvc.ICCOutOperateSV.modifyAttnUserInfo", param, null);
        IData out = response.getBody();
        
        return out;
        
    }
    
    /**
     * 开发校验接口，校验号码是否属于集团成员（判断的类型为：通讯录成员。BOSS路径为：CRM-集团业务-资料管理-集团客户资料管理-集团成员，“成员关系”一栏需为通讯录成员）
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryGroupMembersBySN(IData param) throws Exception
    {
       
        ServiceResponse response = BizServiceFactory.call("CC.groupmember.IGroupMemberQuerySV.queryGroupMembersBySN", param, null);
        IDataset out = response.getDataset("DATAS");
        
        return out;
        
    }
    
}

