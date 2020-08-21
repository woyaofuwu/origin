
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonechangeuser.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonechangeuser.order.requestdata.NoPhoneWideChangeUserRequestData;

public class NoPhoneWideChangeUserTrade extends BaseTrade implements ITrade
{
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	NoPhoneWideChangeUserRequestData reqData = (NoPhoneWideChangeUserRequestData) btd.getRD();
        
    	IData rData=reqData.getWideChangeInfo();
    	String newCustId=SeqMgr.getCustId();
    	rData.put("NEW_CUST_ID", newCustId);
    	
    	// 处理个人资料
    	createTradeUser(btd,rData);
        // 处理客户核心资料
    	createCustomerTradeInfo(btd,rData);
    	// 处理客户个人资料
    	createCustPersonTrade(btd,rData); 
        // 处理账户资料
        createAcctTradeData(btd,rData); 
        
        btd.addOpenUserAcctDayData(reqData.getUca().getUserId(), "1");
        btd.addOpenAccountAcctDayData(reqData.getUca().getAcctId(), "1");

        createWideData(btd, rData);// 手机号也要过户
    }

    private void createWideData(BusiTradeData btd, IData rData) throws Exception
    {
        String newCustId = SeqMgr.getCustId();
        rData.put("NEW_CUST_ID_PHONE", newCustId);
        String oldPhoneSn = rData.getString("TOPSETBOX_SERIAL_NUMBER");
        if (StringUtils.isBlank(oldPhoneSn))
        {
            oldPhoneSn=btd.getRD().getUca().getSerialNumber().substring(3);
            rData.put("TOPSETBOX_SERIAL_NUMBER", oldPhoneSn);
        }
        // 处理个人资料
        createTradeUserPhone(btd, rData);
        // 处理客户核心资料
        createCustomerTradeInfoPhone(btd, rData);
        // 处理客户个人资料
        createCustPersonTradPhone(btd, rData);
        // 处理账户资料
        createAcctTradeDataPhone(btd, rData);
    }

    private void createAcctTradeDataPhone(BusiTradeData btd, IData newCustInfo) throws Exception
    {
        String oldPhoneSn = newCustInfo.getString("TOPSETBOX_SERIAL_NUMBER");

        IDataset phoneAcctInfo = AcctInfoQry.queryAcctInfoBySn(oldPhoneSn);
        if (IDataUtil.isNotEmpty(phoneAcctInfo))
        {
            AccountTradeData acctTDPhone = new AccountTradeData(phoneAcctInfo.getData(0));
            acctTDPhone.setModifyTag(BofConst.MODIFY_TAG_UPD);// 修改
            acctTDPhone.setCustId(newCustInfo.getString("NEW_CUST_ID_PHONE"));
            acctTDPhone.setPayName(newCustInfo.getString("CUST_NAME"));
            btd.add(oldPhoneSn, acctTDPhone);
            
            btd.addOpenAccountAcctDayData(acctTDPhone.getAcctId(), "1");
        }else{
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_112);
        }

    }

    private void createCustPersonTradPhone(BusiTradeData btd, IData newCustInfo) throws Exception
    {
        String oldPhoneSn = newCustInfo.getString("TOPSETBOX_SERIAL_NUMBER");

        CustPersonTradeData custPersonTradeData = new CustPersonTradeData();
        custPersonTradeData.setCustId(newCustInfo.getString("NEW_CUST_ID_PHONE"));

        custPersonTradeData.setPsptTypeCode(newCustInfo.getString("PSPT_TYPE_CODE"));
        custPersonTradeData.setPsptId(newCustInfo.getString("PSPT_ID"));
        custPersonTradeData.setCustName(newCustInfo.getString("CUST_NAME"));
        custPersonTradeData.setPsptEndDate(newCustInfo.getString("PSPT_END_DATE"));
        custPersonTradeData.setPsptAddr(newCustInfo.getString("PSPT_ADDR"));
        custPersonTradeData.setSex(newCustInfo.getString("SEX"));
        custPersonTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
        custPersonTradeData.setBirthday(newCustInfo.getString("BIRTHDAY"));
        if (newCustInfo.getString("BIRTHDAY", "") == null || "".equals(newCustInfo.getString("BIRTHDAY", "")))
        {
            custPersonTradeData.setBirthday("1900-01-01");
        }
        else
        {
            custPersonTradeData.setBirthday(newCustInfo.getString("BIRTHDAY"));
        }
        custPersonTradeData.setPostAddress(newCustInfo.getString("CONTACT_ADDRESS"));
        custPersonTradeData.setPostCode(newCustInfo.getString("POST_CODE"));

        custPersonTradeData.setPhone(newCustInfo.getString("CONTACT_PHONE"));
        custPersonTradeData.setFaxNbr(newCustInfo.getString("FAX_NBR", ""));
        custPersonTradeData.setEmail(newCustInfo.getString("EMAIL", ""));
        custPersonTradeData.setHomeAddress(newCustInfo.getString("HOME_ADDRESS", ""));
        custPersonTradeData.setWorkName(newCustInfo.getString("WORK_NAME", ""));
        custPersonTradeData.setWorkDepart(newCustInfo.getString("WORK_DEPART", ""));

        custPersonTradeData.setContact(newCustInfo.getString("CONTACT", ""));
        custPersonTradeData.setContactPhone(newCustInfo.getString("CONTACT_PHONE"));
        custPersonTradeData.setContactTypeCode(newCustInfo.getString("CONTACT_TYPE_CODE", ""));// baseCommInfo.getString("PRIORITY")

        custPersonTradeData.setNationalityCode(newCustInfo.getString("NATIONALITY_CODE", ""));
        custPersonTradeData.setLanguageCode(newCustInfo.getString("LANGUAGE_CODE", ""));
        custPersonTradeData.setFolkCode(newCustInfo.getString("FOLK_CODE", ""));
        custPersonTradeData.setReligionCode(newCustInfo.getString("RELIGION_CODE", ""));
        custPersonTradeData.setJobTypeCode(newCustInfo.getString("JOB_TYPE_CODE", ""));
        custPersonTradeData.setEducateDegreeCode(newCustInfo.getString("EDUCATE_DEGREE_CODE", ""));
        custPersonTradeData.setMarriage(newCustInfo.getString("MARRIAGE", ""));

        custPersonTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        custPersonTradeData.setRemoveTag("0");
        custPersonTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        custPersonTradeData.setRemark(btd.getRD().getRemark());

        btd.add(oldPhoneSn, custPersonTradeData);

    }

    private void createCustomerTradeInfoPhone(BusiTradeData btd, IData rData) throws Exception
    {
        String oldPhoneSn = rData.getString("TOPSETBOX_SERIAL_NUMBER");
        CustomerTradeData customerTradeData = new CustomerTradeData();
        customerTradeData.setCustId(rData.getString("NEW_CUST_ID_PHONE"));
        customerTradeData.setCustName(rData.getString("CUST_NAME"));
        customerTradeData.setIsRealName("1");
        customerTradeData.setCustType("0");
        customerTradeData.setCustState("1");
        customerTradeData.setPsptTypeCode(rData.getString("PSPT_TYPE_CODE"));
        customerTradeData.setPsptId(rData.getString("PSPT_ID"));
        customerTradeData.setOpenLimit("0");
        customerTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        customerTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
        customerTradeData.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        customerTradeData.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        customerTradeData.setInDepartId(CSBizBean.getVisit().getDepartId());
        customerTradeData.setInStaffId(CSBizBean.getVisit().getStaffId());
        customerTradeData.setInDate(SysDateMgr.getSysTime());
        customerTradeData.setRemark(rData.getString("REMARK"));
        customerTradeData.setRemoveTag("0");
        customerTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(oldPhoneSn, customerTradeData);

    }

    // 用户资料
    private void createTradeUserPhone(BusiTradeData<BaseTradeData> btd, IData changeInfo) throws Exception
    {
        String oldPhoneSn = changeInfo.getString("TOPSETBOX_SERIAL_NUMBER");
        IData phoneData = UcaInfoQry.qryUserInfoBySn(oldPhoneSn, RouteInfoQry.getEparchyCodeBySn(oldPhoneSn));
        if (IDataUtil.isEmpty(phoneData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_126, oldPhoneSn);
        }
        UserTradeData userData = new UserTradeData(phoneData);
        userData.setCustId(changeInfo.getString("NEW_CUST_ID_PHONE")); // 新客户id
        userData.setUsecustId(changeInfo.getString("NEW_CUST_ID_PHONE"));
        userData.setRemark("无手机宽带过户");// 取值写死
        userData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        userData.setCityCode(CSBizBean.getVisit().getCityCode());
        userData.setModifyTag("2");
        userData.setRemoveTag("0");
        btd.add(oldPhoneSn, userData);// 加入busiTradeData中

        btd.addOpenUserAcctDayData(userData.getUserId(), "1");
    }

    // 用户资料
    private void createTradeUser(BusiTradeData<BaseTradeData> btd, IData changeInfo) throws Exception
    {
        String oldSn = btd.getRD().getUca().getSerialNumber();
        UserTradeData userData = btd.getRD().getUca().getUser().clone();
        userData.setCustId(changeInfo.getString("NEW_CUST_ID")); // 新客户id 
        userData.setUsecustId(changeInfo.getString("NEW_CUST_ID"));
        userData.setRemark("无手机宽带过户");// 取值写死 
        userData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        userData.setCityCode(CSBizBean.getVisit().getCityCode());
        userData.setModifyTag("2");
        userData.setRemoveTag("0"); 
        btd.add(oldSn, userData);// 加入busiTradeData中 
    }
  
    
    /**
     * 准备台帐客户资料子表的数据
     * 
     * @param btd
     * @param assusreCustInfo
     * @throws Exception
     */
    private void createCustomerTradeInfo(BusiTradeData<BaseTradeData>  btd, IData newCustInfo) throws Exception
    { 
        CustomerTradeData customerTradeData = new CustomerTradeData();
        customerTradeData.setCustId(newCustInfo.getString("NEW_CUST_ID"));
        customerTradeData.setCustName(newCustInfo.getString("CUST_NAME"));
        customerTradeData.setIsRealName("1");
        customerTradeData.setCustType("0");
        customerTradeData.setCustState("1");
        customerTradeData.setPsptTypeCode(newCustInfo.getString("PSPT_TYPE_CODE"));
        customerTradeData.setPsptId(newCustInfo.getString("PSPT_ID"));
        customerTradeData.setOpenLimit("0");
        customerTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        customerTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
        customerTradeData.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        customerTradeData.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        customerTradeData.setInDepartId(CSBizBean.getVisit().getDepartId());
        customerTradeData.setInStaffId(CSBizBean.getVisit().getStaffId());
        customerTradeData.setInDate(SysDateMgr.getSysTime());
        customerTradeData.setRemark(newCustInfo.getString("REMARK"));
        customerTradeData.setRemoveTag("0");
        customerTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(btd.getRD().getUca().getSerialNumber(), customerTradeData);
    }
    
    /**
     * 生成个人客户资料台账
     * /************** 客户资料 ************* 
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
     * @param btd
     * @throws Exception
     */
    private void createCustPersonTrade(BusiTradeData btd, IData newCustInfo) throws Exception
    {
        CustPersonTradeData custPersonTradeData = new CustPersonTradeData();
        custPersonTradeData.setCustId(newCustInfo.getString("NEW_CUST_ID"));

        custPersonTradeData.setPsptTypeCode(newCustInfo.getString("PSPT_TYPE_CODE"));
        custPersonTradeData.setPsptId(newCustInfo.getString("PSPT_ID"));
        custPersonTradeData.setCustName(newCustInfo.getString("CUST_NAME"));
        custPersonTradeData.setPsptEndDate(newCustInfo.getString("PSPT_END_DATE"));
        custPersonTradeData.setPsptAddr(newCustInfo.getString("PSPT_ADDR"));
        custPersonTradeData.setSex(newCustInfo.getString("SEX"));
        custPersonTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
        custPersonTradeData.setBirthday(newCustInfo.getString("BIRTHDAY"));
        if(newCustInfo.getString("BIRTHDAY","")==null||"".equals(newCustInfo.getString("BIRTHDAY",""))){
            custPersonTradeData.setBirthday("1900-01-01");
        }else{
            custPersonTradeData.setBirthday(newCustInfo.getString("BIRTHDAY"));
        }
        custPersonTradeData.setPostAddress(newCustInfo.getString("CONTACT_ADDRESS"));
        custPersonTradeData.setPostCode(newCustInfo.getString("POST_CODE")); 

        custPersonTradeData.setPhone(newCustInfo.getString("CONTACT_PHONE"));
        custPersonTradeData.setFaxNbr(newCustInfo.getString("FAX_NBR", ""));
        custPersonTradeData.setEmail(newCustInfo.getString("EMAIL", ""));
        custPersonTradeData.setHomeAddress(newCustInfo.getString("HOME_ADDRESS", ""));
        custPersonTradeData.setWorkName(newCustInfo.getString("WORK_NAME", ""));
        custPersonTradeData.setWorkDepart(newCustInfo.getString("WORK_DEPART", ""));

        custPersonTradeData.setContact(newCustInfo.getString("CONTACT", ""));
        custPersonTradeData.setContactPhone(newCustInfo.getString("CONTACT_PHONE"));
        custPersonTradeData.setContactTypeCode(newCustInfo.getString("CONTACT_TYPE_CODE", ""));// baseCommInfo.getString("PRIORITY")

        custPersonTradeData.setNationalityCode(newCustInfo.getString("NATIONALITY_CODE", ""));
        custPersonTradeData.setLanguageCode(newCustInfo.getString("LANGUAGE_CODE", ""));
        custPersonTradeData.setFolkCode(newCustInfo.getString("FOLK_CODE", ""));
        custPersonTradeData.setReligionCode(newCustInfo.getString("RELIGION_CODE", ""));
        custPersonTradeData.setJobTypeCode(newCustInfo.getString("JOB_TYPE_CODE", ""));
        custPersonTradeData.setEducateDegreeCode(newCustInfo.getString("EDUCATE_DEGREE_CODE", ""));
        custPersonTradeData.setMarriage(newCustInfo.getString("MARRIAGE", "")); 

        custPersonTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        custPersonTradeData.setRemoveTag("0");
        custPersonTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        custPersonTradeData.setRemark(btd.getRD().getRemark()); 

        btd.add(btd.getRD().getUca().getSerialNumber(), custPersonTradeData);
    }
    
    
    /**
     * 账户资料
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    public void createAcctTradeData(BusiTradeData btd, IData newCustInfo) throws Exception
    {
        NoPhoneWideChangeUserRequestData rd = (NoPhoneWideChangeUserRequestData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();
        AccountTradeData acctTD = rd.getUca().getAccount().clone();
        acctTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
        acctTD.setCustId(newCustInfo.getString("NEW_CUST_ID"));
        acctTD.setPayName(newCustInfo.getString("CUST_NAME"));
         
        btd.add(serialNumber, acctTD);
    } 
}
