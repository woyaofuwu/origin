
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophoneschoolcreatewideuser.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.Des;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophoneschoolcreatewideuser.order.requestdata.NoPhoneSchoolWideUserCreateRequestData;

public class BuildNoPhoneSchoolWideUserCreateReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	NoPhoneSchoolWideUserCreateRequestData reqData = (NoPhoneSchoolWideUserCreateRequestData) brd;
        String tradeTypeCode = reqData.getTradeType().getTradeTypeCode();
        reqData.setUserPasswd(param.getString("USER_PASSWD", "kd123456"));
        reqData.setPsptId(param.getString("WIDE_PSPT_ID"));
        reqData.setPhone(param.getString("PHONE"));
        reqData.setContact(param.getString("CONTACT"));
        reqData.setContactPhone(param.getString("CONTACT_PHONE"));
        reqData.setStandAddress(param.getString("STAND_ADDRESS"));
        reqData.setStandAddressCode(param.getString("STAND_ADDRESS_CODE"));
        reqData.setDetailAddress(param.getString("DETAIL_ADDRESS"));
        reqData.setRealName(param.getString("REAL_NAME"));
        if("600".equals(tradeTypeCode) || "612".equals(tradeTypeCode) || "613".equals(tradeTypeCode)){
        	reqData.setDetailAddress(param.getString("DETAIL_ADDRESS") + "(" + param.getString("DETAIL_ROOM_NUM","") + ")");
        }
        
        
        reqData.setAreaCode(param.getString("AREA_CODE"));
        reqData.setMainProduct(param.getString("WIDE_PRODUCT_ID"));
        reqData.setOpenDate(SysDateMgr.getSysTime());
        reqData.setNormalUserId(param.getString("NORMAL_USER_ID"));
        reqData.setNormalSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setVirtualUserId(SeqMgr.getUserId());
        reqData.setPreWideType(param.getString("PREWIDE_TYPE", ""));
        reqData.setModemStyle(param.getString("MODEM_STYLE", ""));
        reqData.setModemNumeric(param.getString("MODEM_NUMERIC_CODE", ""));
        reqData.setStudentNumber(param.getString("STUDENT_NUMBER", ""));
        brd.getUca().setProductId(param.getString("WIDE_PRODUCT_ID"));
        brd.getUca().setBrandCode(reqData.getMainProduct().getBrandCode());
        
        String serialNumberGrp = param.getString("SERIAL_NUMBER");
        if(StringUtils.isNotBlank(serialNumberGrp)){
        	IData productInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberGrp);
            if (IDataUtil.isNotEmpty(productInfo))
            {
            	// 如果是集团商务宽带用户，做一个标识
                if ("7341".equals(productInfo.getString("PRODUCT_ID")))
                {
                	reqData.setUserPasswd("123456");
                	reqData.setRsrvStr10("BNBD");
                }
            }
        }
        
        param.put("SERIAL_NUMBER", "KD_" + param.getString("WIDE_SERIAL_NUMBER"));
        
        // 针对信控统计，区分宽带类型
        // 虚拟优惠：GPON-5906；ADSL-5907；FTTH-5908; 校园宽带-5909
        
        if (tradeTypeCode.equals("612"))
        {
            reqData.setWideType("2");// adsl
            reqData.setLowDiscntCode("5907");
        }
        else if (tradeTypeCode.equals("613"))
        {
            reqData.setWideType("3");// ftth
            reqData.setLowDiscntCode("5908");
        }
        else if (tradeTypeCode.equals("630"))
        {
            reqData.setWideType("4");// 校园
            reqData.setLowDiscntCode("5909");
        }
        else
        {
            reqData.setWideType("1");// GPON
            reqData.setLowDiscntCode("5906");
        }

        if (tradeTypeCode.equals("611"))
        {
            reqData.setUserIdA(param.getString("USER_ID_A"));
            reqData.setGponUserId(param.getString("GPON_USER_ID"));
            reqData.setGponSerialNumber(param.getString("GPON_SERIAL_NUMBER"));
        }
        IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        if (IDataUtil.isNotEmpty(selectedElements))
        {
            List<ProductModuleData> elements = new ArrayList<ProductModuleData>();
            int size = selectedElements.size();
            for (int i = 0; i < size; i++)
            {
                IData element = selectedElements.getData(i);
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    DiscntData discntData = new DiscntData(element);
                    discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    elements.add(discntData);

                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
                {
                    SvcData svcData = new SvcData(element);
                    svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    elements.add(svcData);

                }
            }

            reqData.setProductElements(elements);
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1036);
        }

    }

    public UcaData buildUcaData(IData param) throws Exception
    {
    	String userId = "";
        String custId = "";
        String acctId = "";
        UcaData uca = new UcaData();

        // 生成用户标识
        userId = SeqMgr.getUserId();
        // 生成客户标识
        custId = SeqMgr.getCustId();
        // 生成帐户标识
        acctId = SeqMgr.getAcctId();

        String strCustCityCode = param.getString("CITY_CODE", "");
        String cityCode = "".equals(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode;
        param.put("CITY_CODE", cityCode);
        param.put("OPEN_DATE", SysDateMgr.getSysTime());
        // 设置三户资料对象
        // 构建用户资料
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", userId);
        userInfo.put("CUST_ID", custId);
        userInfo.put("USECUST_ID", custId);
        userInfo.put("SERIAL_NUMBER", "KD_" + param.getString("WIDE_SERIAL_NUMBER"));
        userInfo.put("NET_TYPE_CODE", "00");
        userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        userInfo.put("CITY_CODE", cityCode);
        userInfo.put("MODIFY_TAG", "0");
        userInfo.put("USER_STATE_CODESET", "0");
        userInfo.put("USER_DIFF_CODE", "0");
        
        String userPasswd = param.getString("USER_PASSWD");
        
        if (userPasswd.indexOf("xxyy") > 0)
        {
            userPasswd = userPasswd.substring(0, userPasswd.indexOf("xxyy"));
            
            Des desObj = new Des();
            String key1 = "c";
            String key2 = "x";
            String key3 = "y";
            
            userPasswd = desObj.strDec(userPasswd, key1, key2, key3);
        }
        userInfo.put("USER_PASSWD", userPasswd);
        userInfo.put("USER_TYPE_CODE", "0");
        userInfo.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("IN_DATE", SysDateMgr.getSysTime());
        userInfo.put("DEVELOP_STAFF_ID", param.getString("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        userInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("OPEN_MODE", param.getString("OPEN_MODE", "0"));
        userInfo.put("OPEN_DATE", SysDateMgr.getSysTime());
        userInfo.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("ACCT_TAG", param.getString("ACCT_TAG", "0"));
        userInfo.put("PREPAY_TAG", "1");
        userInfo.put("MPUTE_MONTH_FEE", "0");
        userInfo.put("REMOVE_TAG", "0");
        userInfo.put("ASSURE_TYPE_CODE", param.getString("ASSURE_TYPE_CODE", ""));
        userInfo.put("ASSURE_DATE", param.getString("ASSURE_DATE", ""));

        // 构建客户核心资料
        IData customerInfo = new DataMap();
        customerInfo.put("CUST_ID", custId);
        customerInfo.put("CUST_NAME", param.getString("CUST_NAME").trim());
        customerInfo.put("PSPT_TYPE_CODE", StringUtils.isEmpty(param.getString("PSPT_TYPE_CODE"))?"0":param.getString("PSPT_TYPE_CODE"));
        customerInfo.put("PSPT_ID", param.getString("PSPT_ID"));
        customerInfo.put("CITY_CODE", cityCode);
        customerInfo.put("MODIFY_TAG", "0");
        customerInfo.put("CUST_TYPE", "0");
        customerInfo.put("CUST_STATE", "0");
        customerInfo.put("OPEN_LIMIT", "0");
        customerInfo.put("IN_DATE", SysDateMgr.getSysTime());
        customerInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        customerInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        customerInfo.put("REMOVE_TAG", "0");
        customerInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        // 构建个人客户资料
        IData custPersonInfo = new DataMap();
        custPersonInfo.put("CUST_ID", custId);
        custPersonInfo.put("CUST_NAME", param.getString("CUST_NAME").trim());
        custPersonInfo.put("PSPT_TYPE_CODE", StringUtils.isEmpty(param.getString("PSPT_TYPE_CODE"))?"0":param.getString("PSPT_TYPE_CODE"));
        custPersonInfo.put("PSPT_ID", param.getString("PSPT_ID"));
        custPersonInfo.put("PSPT_END_DATE", param.getString("PSPT_END_DATE", ""));
        custPersonInfo.put("PSPT_ADDR", param.getString("PSPT_ADDR", ""));
        custPersonInfo.put("SEX", param.getString("SEX", ""));
        custPersonInfo.put("CITY_CODE", cityCode);
        custPersonInfo.put("MODIFY_TAG", "0");
        custPersonInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        custPersonInfo.put("BIRTHDAY", param.getString("BIRTHDAY", ""));
        custPersonInfo.put("POST_ADDRESS", param.getString("POST_ADDRESS"));
        custPersonInfo.put("POST_CODE", param.getString("POST_CODE"));
        custPersonInfo.put("PHONE", param.getString("PHONE"));
        custPersonInfo.put("FAX_NBR", param.getString("FAX_NBR"));
        custPersonInfo.put("EMAIL", param.getString("EMAIL"));
        custPersonInfo.put("HOME_ADDRESS", param.getString("HOME_ADDRESS"));
        custPersonInfo.put("WORK_NAME", param.getString("WORK_NAME"));
        custPersonInfo.put("WORK_DEPART", param.getString("WORK_DEPART"));
        custPersonInfo.put("JOB_TYPE_CODE", param.getString("JOB_TYPE_CODE"));
        custPersonInfo.put("CONTACT", param.getString("CONTACT"));
        custPersonInfo.put("CONTACT_PHONE", param.getString("CONTACT_PHONE"));
        custPersonInfo.put("CONTACT_TYPE_CODE", param.getString("CONTACT_TYPE_CODE"));
        custPersonInfo.put("NATIONALITY_CODE", param.getString("NATIONALITY_CODE"));
        custPersonInfo.put("FOLK_CODE", param.getString("FOLK_CODE"));
        custPersonInfo.put("RELIGION_CODE", param.getString("RELIGION_CODE"));
        custPersonInfo.put("LANGUAGE_CODE", param.getString("LANGUAGE_CODE"));
        custPersonInfo.put("EDUCATE_DEGREE_CODE", param.getString("EDUCATE_DEGREE_CODE"));
        custPersonInfo.put("MARRIAGE", param.getString("MARRIAGE"));
        custPersonInfo.put("REMOVE_TAG", "0");
        custPersonInfo.put("CALLING_TYPE_CODE", param.getString("CALLING_TYPE_CODE"));
        custPersonInfo.put("RSRV_STR5", param.getString("USE"));
        custPersonInfo.put("RSRV_STR6", param.getString("USE_PSPT_TYPE_CODE"));
        custPersonInfo.put("RSRV_STR7", param.getString("USE_PSPT_ID"));
        custPersonInfo.put("RSRV_STR8", param.getString("USE_PSPT_ADDR"));
        // 构建账户资料
        IData acctInfo = new DataMap();
        acctInfo.put("ACCT_ID", acctId);
        acctInfo.put("CUST_ID", custId);
        acctInfo.put("MODIFY_TAG", "0");
        acctInfo.put("PAY_NAME", param.getString("PAY_NAME").trim());
        acctInfo.put("PAY_MODE_CODE", param.getString("PAY_MODE_CODE", ""));
        acctInfo.put("BANK_CODE", param.getString("BANK_CODE"));
        acctInfo.put("BANK_ACCT_NO", param.getString("BANK_ACCT_NO"));
        acctInfo.put("CITY_CODE", cityCode);
        acctInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        acctInfo.put("SCORE_VALUE", "0");
        acctInfo.put("BASIC_CREDIT_VALUE", "0");
        acctInfo.put("CREDIT_VALUE", "0");
        acctInfo.put("OPEN_DATE", SysDateMgr.getSysTime());
        acctInfo.put("REMOVE_TAG", "0");

        uca.setUser(new UserTradeData(userInfo));
        uca.setCustomer(new CustomerTradeData(customerInfo));
        uca.setCustPerson(new CustPersonTradeData(custPersonInfo));
        uca.setAccount(new AccountTradeData(acctInfo));
        uca.setAcctBlance("0");
        uca.setLastOweFee("0");
        uca.setRealFee("0");

        AcctTimeEnv env = new AcctTimeEnv(param.getString("ACCT_DAY", "1"), "1995-02-01", "1995-01-01", "1995-01-01");
        AcctTimeEnvManager.setAcctTimeEnv(env);
        
        return uca;
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneSchoolWideUserCreateRequestData();
    }

}
