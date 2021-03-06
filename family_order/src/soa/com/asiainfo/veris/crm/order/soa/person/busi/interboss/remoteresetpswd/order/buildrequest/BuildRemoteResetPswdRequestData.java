package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd.order.buildrequest;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteresetpswd.order.requestdata.RemoteResetPswdRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.RealNameIntfBean;

public class BuildRemoteResetPswdRequestData extends BaseBuilder implements IBuilder{

	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public BaseReqData getBlankRequestDataInstance() {
		// TODO Auto-generated method stub
        return new RemoteResetPswdRequestData();
	}
	// 新增三户信息
	public UcaData buildUcaData(IData input) throws Exception
	{
		IData param = new DataMap();
		param.putAll(input);
		
		param.put("PSPT_TYPE_CODE", "1");
		param.put("PSPT_ID", input.getString("PSPT_ID"));
		param.put("CUST_NAME", input.getString("CUST_NAME"));
		param.put("PAY_NAME", input.getString("CUST_NAME"));
		param.put("PAY_MODE_CODE", "0");
		UcaData uca = new UcaData();

		String userId =  "-1";
		String custId = "-1";
		String acctId = "-1";
		String strCustCityCode = param.getString("CITY_CODE", "");
		String cityCode = "".equals(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode;
		// 1.构建用户资料
		UserTradeData userData = new UserTradeData();
		userData.setUserId(userId);
		userData.setModifyTag(BofConst.MODIFY_TAG_UPD);//虚拟用户，只造子台账，不归档。区别于真实开户业务。
		userData.setSerialNumber(param.getString("SERIAL_NUMBER"));
		userData.setNetTypeCode("00");
		userData.setUserStateCodeset("0");
		userData.setUserDiffCode("0");
		userData.setOpenMode(param.getString("OPEN_MODE", "0"));
		userData.setAcctTag(param.getString("ACCT_TAG", "0"));
		userData.setPrepayTag("1");
		userData.setMputeMonthFee("0");
		userData.setRemoveTag("0");
		
		userData.setCustId(custId);
		userData.setUsecustId(custId);
		userData.setEparchyCode(CSBizBean.getUserEparchyCode());
		userData.setCityCode(cityCode);
		if (StringUtils.equals("402", param.getString("PRODUCT_ID", "")))
		{
			userData.setUserPasswd(StringUtils.substring(userData.getSerialNumber(), 5));
		}
		else
		{
			userData.setUserPasswd(param.getString("USER_PASSWD", ""));
		}
		userData.setUserTypeCode(param.getString("USER_TYPE_CODE", "0"));
		userData.setDevelopDepartId(param.getString("DEVELOP_DEPART_ID",""));
		userData.setDevelopDate(SysDateMgr.getSysTime());
		userData.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
		userData.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
		userData.setDevelopCityCode(cityCode);
		userData.setDevelopEparchyCode(CSBizBean.getTradeEparchyCode());
		userData.setInDate(SysDateMgr.getSysTime());
		userData.setInStaffId(CSBizBean.getVisit().getStaffId());
		userData.setInDepartId(CSBizBean.getVisit().getDepartId());
		userData.setOpenDate(SysDateMgr.getSysTime());
		userData.setOpenStaffId(CSBizBean.getVisit().getStaffId());
		userData.setOpenDepartId(CSBizBean.getVisit().getDepartId());
		userData.setAssureTypeCode(param.getString("ASSURE_TYPE_CODE", ""));
		userData.setAssureDate(param.getString("ASSURE_DATE", ""));
		userData.setDevelopNo(param.getString("DEVELOP_NO", ""));

		// 2.构建客户核心资料
		CustomerTradeData customerData = new CustomerTradeData();
		customerData.setCustId(custId);
		customerData.setCustName(param.getString("CUST_NAME", "").trim());
		customerData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE", ""));
		customerData.setPsptId(param.getString("PSPT_ID", ""));
		customerData.setCityCode(cityCode);
		customerData.setModifyTag(BofConst.MODIFY_TAG_UPD);
		customerData.setCustType("0");
		customerData.setCustState("0");
		customerData.setOpenLimit(param.getString("OPEN_LIMIT", "0"));
		customerData.setInDate(SysDateMgr.getSysTime());
		customerData.setInStaffId(CSBizBean.getVisit().getStaffId());
		customerData.setInDepartId(CSBizBean.getVisit().getDepartId());
		customerData.setRemoveTag("0");
		customerData.setEparchyCode(CSBizBean.getUserEparchyCode());
		customerData.setCustPasswd(param.getString("CUST_PASSWD", "0"));
		customerData.setScoreValue(param.getString("SCORE_VALUE", "0"));
		customerData.setCreditClass(param.getString("CREDIT_CLASS", "0"));
		customerData.setBasicCreditValue(param.getString("BASIC_CREDIT_VALUE", "0"));
		customerData.setCreditValue(param.getString("CREDIT_VALUE", "0"));
		customerData.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
		customerData.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
		// 3.构建个人客户资料
		CustPersonTradeData custPersonData = new CustPersonTradeData();
		custPersonData.setCustId(custId);
		custPersonData.setCustName(param.getString("CUST_NAME", "").trim());
		custPersonData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
		custPersonData.setPsptId(param.getString("PSPT_ID"));
		custPersonData.setPsptEndDate(param.getString("PSPT_END_DATE", ""));
		custPersonData.setPsptAddr(param.getString("PSPT_ADDR", ""));
		custPersonData.setSex(param.getString("SEX", ""));
		custPersonData.setCityCode(cityCode);
		custPersonData.setModifyTag(BofConst.MODIFY_TAG_UPD);
		custPersonData.setEparchyCode(CSBizBean.getUserEparchyCode());
		custPersonData.setBirthday(param.getString("BIRTHDAY", ""));
		custPersonData.setPostAddress(param.getString("CONTACT_ADDRESS", ""));
		custPersonData.setPostCode(param.getString("POST_CODE", ""));
		custPersonData.setPhone(param.getString("PHONE", ""));
		custPersonData.setFaxNbr(param.getString("FAX_NBR", ""));
		custPersonData.setEmail(param.getString("EMAIL", ""));
		custPersonData.setHomeAddress(param.getString("HOME_ADDRESS", ""));
		custPersonData.setWorkName(param.getString("WORK_NAME", ""));
		custPersonData.setWorkDepart(param.getString("WORK_DEPART", ""));
		custPersonData.setJobTypeCode(param.getString("JOB_TYPE_CODE", ""));
		custPersonData.setContact(param.getString("CONTACT", ""));
		custPersonData.setContactPhone(param.getString("CONTACT_PHONE", ""));
		custPersonData.setContactTypeCode(param.getString("CONTACT_TYPE_CODE", ""));
		custPersonData.setNationalityCode(param.getString("NATIONALITY_CODE", ""));
		custPersonData.setFolkCode(param.getString("FOLK_CODE", ""));
		custPersonData.setReligionCode(param.getString("RELIGION_CODE", ""));
		custPersonData.setLanguageCode(param.getString("LANGUAGE_CODE", ""));
		custPersonData.setEducateDegreeCode(param.getString("EDUCATE_DEGREE_CODE", ""));
		custPersonData.setMarriage(param.getString("MARRIAGE", ""));
		custPersonData.setRemoveTag("0");
		custPersonData.setCallingTypeCode(param.getString("CALLING_TYPE_CODE", ""));
		// 4.构建账户资料
		AccountTradeData accountData = new AccountTradeData();
		accountData.setAcctId(acctId);
		accountData.setCustId(custId);
		accountData.setModifyTag(BofConst.MODIFY_TAG_UPD);
		accountData.setNetTypeCode("00");
		accountData.setPayName(param.getString("PAY_NAME", "").trim());
		accountData.setPayModeCode(param.getString("PAY_MODE_CODE", ""));
		accountData.setBankCode(param.getString("BANK_CODE", ""));
		accountData.setBankAcctNo(param.getString("BANK_ACCT_NO", ""));
		accountData.setCityCode(cityCode);
		accountData.setEparchyCode(CSBizBean.getUserEparchyCode());
		accountData.setScoreValue("0");
		accountData.setBasicCreditValue("0");
		accountData.setCreditValue("0");
		accountData.setOpenDate(SysDateMgr.getSysTime());
		accountData.setRemoveTag("0");
		accountData.setRsrvStr6(param.getString("BANK_AGREEMENT_NO", "")); // 银行协议号
		accountData.setRsrvStr7(param.getString("BANK_DEPOSIT_TYPE", ""));// 银行业务类型
		accountData.setAcctDiffCode(param.getString("ACCT_DIFF_CODE", "0"));
		accountData.setDebutyCode(param.getString("DEBUTY_CODE", userData.getSerialNumber()));
		accountData.setDebutyUserId(param.getString("DEBUTY_USER_ID", userData.getUserId()));
		accountData.setCreditClassId(param.getString("CREDIT_CLASS_ID", ""));

		uca.setUser(userData);
		uca.setCustomer(customerData);
		uca.setCustPerson(custPersonData);
		uca.setAccount(accountData);
		uca.setAcctBlance("0");
		uca.setLastOweFee("0");
		uca.setRealFee("0");

		AcctTimeEnv env = new AcctTimeEnv(param.getString("ACCT_DAY", "1"), "", "", "");
		AcctTimeEnvManager.setAcctTimeEnv(env);
		return uca;
	}
	@Override
	protected void checkBefore(IData input, BaseReqData reqData)
			throws Exception {
		// TODO Auto-generated method stub
	}
    
}
