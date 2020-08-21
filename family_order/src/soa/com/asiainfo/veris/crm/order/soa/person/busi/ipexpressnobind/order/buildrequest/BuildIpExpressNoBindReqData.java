
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpressnobind.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressNoBindException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.ipexpressnobind.order.requestdata.IpExpressNoBindRequestData;

public class BuildIpExpressNoBindReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        IpExpressNoBindRequestData ienbrd = (IpExpressNoBindRequestData) brd;
        List<UserTradeData> ipUserDatas = new ArrayList<UserTradeData>();
        IDataset ipInfos = new DatasetList(param.getString("X_CODING_STR"));
        if (ipInfos == null || ipInfos.size() == 0)
        {
            CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_11);
        }
        for (int i = 0, size = ipInfos.size(); i < size; i++)
        {
            IData ipInfo = ipInfos.getData(i);
            UserTradeData ipUserData = new UserTradeData();
            String productId = ipInfo.getString("col_PRODUCT_ID");
            String newPwd = ipInfo.getString("col_TEMP_PWD");
            String oldPwd = ipInfo.getString("col_OLD_PWD");
            String userIdB = ipInfo.getString("col_USER_ID_B");
            String serialNumberG = ipInfo.getString("col_SERIAL_NUMBER_G");
            String dealTag = ipInfo.getString("col_M_DEAL_TAG");
            String packageSvcInfo = ipInfo.getString("col_PACKAGESVC");
            ipUserData.setUserId(userIdB);
            ipUserData.setUserPasswd(newPwd);
            ipUserData.setRsrvStr1(productId);
            ipUserData.setSerialNumber(serialNumberG);
            ipUserData.setModifyTag(dealTag);
            ipUserData.setRsrvStr2(packageSvcInfo);
            ipUserData.setRsrvStr3(oldPwd);
            ipUserDatas.add(ipUserData);
        }
        ienbrd.setIpUserDatas(ipUserDatas);
    }

    public UcaData buildUcaData(IData param) throws Exception
    {
        String serialNumber = param.getString("AUTH_SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        UcaData uca = new UcaData();
        if (userInfo == null)
        {
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", serialNumber);

            IDataset ipInfos = new DatasetList(param.getString("X_CODING_STR"));
            if (ipInfos == null || ipInfos.size() == 0)
            {
                CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_11);
            }
            for (int i = 0, size = ipInfos.size(); i < size; i++)
            {
                IData ipInfo = ipInfos.getData(i);
                String serialNumberG = ipInfo.getString("col_SERIAL_NUMBER_G");

                if (serialNumber.equals(serialNumberG))
                {
                    String productId = ipInfo.getString("col_PRODUCT_ID");
                    String newPwd = ipInfo.getString("col_TEMP_PWD");
                    String oldPwd = ipInfo.getString("col_OLD_PWD");
                    String userIdB = ipInfo.getString("col_USER_ID_B");
                    String dealTag = ipInfo.getString("col_M_DEAL_TAG");
                    String packageSvcInfo = ipInfo.getString("col_PACKAGESVC");
                    String openDate = ipInfo.getString("col_OPEN_DATE");
                    UserTradeData ipUserData = new UserTradeData();
                    ipUserData.setOpenDate(openDate);
                    ipUserData.setUserId(userIdB);
                    ipUserData.setUserPasswd(newPwd);
                    ipUserData.setRsrvStr1(productId);
                    ipUserData.setSerialNumber(serialNumberG);
                    ipUserData.setModifyTag(dealTag);
                    ipUserData.setRsrvStr2(packageSvcInfo);
                    ipUserData.setRsrvStr3(oldPwd);
                    data = ipUserData.toData();
                    break;
                }
            }
            if (data == null)
                CSAppException.apperr(IpExpressNoBindException.CRM_IPEXPRESSNOBIND_4);
            // 生成客户标识
            String custId = SeqMgr.getCustId();
            // 生成帐户标识
            String acctId = SeqMgr.getAcctId();
            String strCustCityCode = param.getString("CITY_CODE", "");
            String cityCode = "".equals(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode;
            param.put("CITY_CODE", cityCode);
            param.put("OPEN_DATE", SysDateMgr.getSysTime());
            // 构建用户资料
            userInfo = new DataMap();
            userInfo.putAll(data);
            userInfo.put("CUST_ID", custId);
            userInfo.put("USECUST_ID", custId);
            userInfo.put("NET_TYPE_CODE", BofConst.NET_TYPE_CODE);
            userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            userInfo.put("CITY_CODE", cityCode);
            userInfo.put("USER_TYPE_CODE", "0");
            userInfo.put("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId());
            userInfo.put("DEVELOP_DATE", SysDateMgr.getSysTime());
            userInfo.put("USER_STATE_CODESET", "0");
            userInfo.put("USER_TYPE_CODE", "0");
            userInfo.put("ACCT_TAG", "0");
            userInfo.put("PREPAY_TAG", "0");
            userInfo.put("MPUTE_MONTH_FEE", "0");
            userInfo.put("IN_DATE", userInfo.getString("OPEN_DATE"));
            userInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
            userInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
            userInfo.put("OPEN_MODE", "0");
            userInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            userInfo.put("REMOVE_TAG", "0");

            // 构建客户核心资料
            IData customerInfo = new DataMap();
            customerInfo.put("CUST_ID", custId);
            customerInfo.put("CUST_NAME", param.getString("CUST_NAME"));
            customerInfo.put("CUST_STATE", "0");
            customerInfo.put("CUST_TYPE", "0");
            customerInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE", "0"));
            customerInfo.put("PSPT_ID", param.getString("PSPT_ID", ""));
            customerInfo.put("OPEN_LIMIT", "0");
            customerInfo.put("IN_DATE", SysDateMgr.getSysTime());
            customerInfo.put("REMOVE_TAG", "0");
            customerInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            customerInfo.put("CITY_CODE", cityCode);
            customerInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            // 构建个人客户资料
            IData custPersonInfo = new DataMap();
            custPersonInfo.put("CUST_ID", custId);
            custPersonInfo.put("CUST_NAME", param.getString("CUST_NAME"));
            custPersonInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
            custPersonInfo.put("PSPT_ID", param.getString("PSPT_ID", ""));
            custPersonInfo.put("PSPT_END_DATE", param.getString("PSPT_END_DATE", ""));
            custPersonInfo.put("PSPT_ADDR", param.getString("PSPT_ADDR"));
            custPersonInfo.put("SEX", param.getString("SEX"));
            custPersonInfo.put("CITY_CODE", cityCode);
            custPersonInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            custPersonInfo.put("BIRTHDAY", param.getString("BIRTHDAY", ""));
            custPersonInfo.put("POST_ADDRESS", param.getString("POST_ADDRESS", ""));
            custPersonInfo.put("POST_CODE", param.getString("POST_CODE", ""));
            custPersonInfo.put("POST_PERSON", param.getString("POST_PERSON", ""));
            custPersonInfo.put("PHONE", param.getString("PHONE", ""));
            custPersonInfo.put("FAX_NBR", param.getString("FAX_NBR", ""));
            custPersonInfo.put("EMAIL", param.getString("EMAIL", ""));
            custPersonInfo.put("HOME_ADDRESS", param.getString("HOME_ADDRESS", ""));
            custPersonInfo.put("WORK_NAME", param.getString("WORK_NAME", ""));
            custPersonInfo.put("WORK_DEPART", param.getString("WORK_DEPART", ""));
            custPersonInfo.put("JOB_TYPE_CODE", param.getString("JOB_TYPE_CODE", ""));
            custPersonInfo.put("CONTACT", param.getString("CONTACT", ""));
            custPersonInfo.put("CONTACT_PHONE", param.getString("CONTACT_PHONE", ""));
            custPersonInfo.put("CONTACT_TYPE_CODE", param.getString("CONTACT_TYPE_CODE", ""));
            custPersonInfo.put("NATIONALITY_CODE", param.getString("NATIONALITY_CODE", ""));
            custPersonInfo.put("FOLK_CODE", param.getString("FOLK_CODE", ""));
            custPersonInfo.put("RELIGION_CODE", param.getString("RELIGION_CODE", ""));
            custPersonInfo.put("LANGUAGE_CODE", param.getString("LANGUAGE_CODE", ""));
            custPersonInfo.put("EDUCATE_DEGREE_CODE", param.getString("EDUCATE_DEGREE_CODE", ""));
            custPersonInfo.put("MARRIAGE", param.getString("MARRIAGE", ""));
            custPersonInfo.put("MODIFY_TAG", "0");
            // 构建账户资料
            IData acctInfo = new DataMap();
            acctInfo.put("ACCT_ID", acctId);
            acctInfo.put("CUST_ID", custId);
            acctInfo.put("ACCT_TAG", "0");
            acctInfo.put("NET_TYPE_CODE", "00");

            acctInfo.put("PAY_NAME", param.getString("PAY_NAME", ""));
            acctInfo.put("PAY_MODE_CODE", param.getString("PAY_MODE_CODE", "1"));
            acctInfo.put("BANK_CODE", param.getString("BANK_CODE", ""));
            acctInfo.put("BANK_ACCT_NO", param.getString("BANK_ACCT_NO", ""));
            acctInfo.put("CITY_CODE", cityCode);
            acctInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            acctInfo.put("SCORE_VALUE", "0");
            acctInfo.put("REMOVE_TAG", "0");
            acctInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            acctInfo.put("BASIC_CREDIT_VALUE", "0");
            acctInfo.put("CREDIT_VALUE", "0");
            acctInfo.put("OPEN_DATE", SysDateMgr.getSysTime());
            // 托收信息需要处理super_bank_code
            acctInfo.put("RSRV_STR1", param.getString("SUPER_BANK_CODE"));

            uca.setUser(new UserTradeData(userInfo));
            uca.setCustomer(new CustomerTradeData(customerInfo));
            uca.setCustPerson(new CustPersonTradeData(custPersonInfo));
            uca.setAccount(new AccountTradeData(acctInfo));
            uca.setAcctBlance("0");
            uca.setLastOweFee("0");
            uca.setRealFee("0");

            AcctTimeEnv env = new AcctTimeEnv(param.getString("ACCT_DAY", "1"), "", "", "");
            AcctTimeEnvManager.setAcctTimeEnv(env);
            uca.getUser().setOpenDate(SysDateMgr.getSysTime());
        }
        else
        {
            uca = UcaDataFactory.getNormalUca(serialNumber);
        }
        return uca;
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new IpExpressNoBindRequestData();
    }
}
