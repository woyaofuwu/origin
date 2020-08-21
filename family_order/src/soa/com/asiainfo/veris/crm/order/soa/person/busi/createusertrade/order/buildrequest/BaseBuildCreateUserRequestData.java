
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;

public class BaseBuildCreateUserRequestData extends BaseBuilder implements IBuilder
{

    /**
     * 组织其他数据
     * 
     * @author sunxin
     * @param data
     * @return
     * @throws Exception
     */
    public static IData genOtherInfo(IData data) throws Exception
    {

        IDataset otherList = new DatasetList();
        IData otherData = new DataMap();
        IData toTdData = new DataMap();
        // 号资源
        otherData.put("ELEMENT_ID", data.getString("ELEMENT_ID", ""));
        otherList.add(otherData);
        toTdData.put("X_TRADE_OTHER_ELEMENT", otherList);
        return toTdData;
    }

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) brd;

        // 基本信息

        // baseCreatePersonUserRD.setSerialNumber(param.getString("SERIAL_NUMBER"));
        // baseCreatePersonUserRD.setDevelopNo(param.getString("DEVELOP_NO", ""));
        // baseCreatePersonUserRD.setNoteType(param.getString("NOTE_TYPE", "0"));

        // baseCreatePersonUserRD.setXTradeRes(genResInfo(param).getString("X_TRADE_RES"));

        buildElems(param, baseCreatePersonUserRD);
        baseCreatePersonUserRD.setMainProduct(param.getString("PRODUCT_ID"));// 设置产品信息
        baseCreatePersonUserRD.getUca().setProductId(param.getString("PRODUCT_ID"));
        baseCreatePersonUserRD.getUca().setBrandCode(baseCreatePersonUserRD.getMainProduct().getBrandCode());
        baseCreatePersonUserRD.getUca().setAcctDay(param.getString("ACCT_DAY", "1"));
    }

    // 处理产品
    public void buildElems(IData param, BaseCreateUserRequestData brd) throws Exception
    {
        /* 拼装子元素 */
        IDataset elems = new DatasetList();

        elems = new DatasetList(param.getString("SELECTED_ELEMENTS", ""));
        for (int i = 0; i < elems.size(); i++)
        {
            IData elem = elems.getData(i);
            String elemTypeCode = elem.getString("ELEMENT_TYPE_CODE", "");

            if (elemTypeCode.equals("D"))
            {
                brd.addPmd(new DiscntData(elem));
            }
            else if (elemTypeCode.equals("S"))
            {
                // 如果用户有这个服务，则不拼到requestData中
                if (brd.getUca().checkUserIsExistSvcId(elem.getString("ELEMENT_ID", "")))
                {
                    continue;
                }
                brd.addPmd(new SvcData(elem));
            }
            else if (elemTypeCode.equals("Z"))
            {
                // 如果用户有这个服务，则不拼到requestData中
                // List<PlatSvcTradeData> userPlatSvcs = brd.getUca().getPlatSvcs();
                // for (int j = 0; j < userPlatSvcs.size(); j++)
                // {
                // PlatSvcTradeData userPlatSvcTradeData = userPlatSvcs.get(j);
                // if (userPlatSvcTradeData.getElementId().equals(elem.getString("ELEMENT_ID", "")))
                // {
                // continue;
                // }
                // }
                // 暂时屏蔽brd.addPmd(new PlatSvcData(elem));
            }
            else if (elemTypeCode.equals("O"))
            {
                // brd.setOtherList(genOtherInfo(param).getString("X_TRADE_OTHER_ELEMENT"));// 其他元素类，如手机单双模
                // 不知道产品变更会不会处理，这里先自己处理
            }
        }
    }

    // 新增三户信息
    public UcaData buildUcaData(IData param) throws Exception
    {

        String userId = "";
        String custId = "";
        String acctId = "";
        UcaData uca = new UcaData();
        /*
         * 先处理全部新增情况 ，不包含已经存在情况 sunxin if (!param.getString("B_REOPEN_TAG", "").equals("1")) {
         */

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
        userInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        userInfo.put("NET_TYPE_CODE", "00");
        userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        userInfo.put("CITY_CODE", cityCode);
        userInfo.put("MODIFY_TAG", "0");
        userInfo.put("USER_STATE_CODESET", "0");
        userInfo.put("USER_DIFF_CODE", "0");
        userInfo.put("USER_PASSWD", param.getString("USER_PASSWD"));
        userInfo.put("USER_TYPE_CODE", param.getString("USER_TYPE_CODE"));
        userInfo.put("DEVELOP_DEPART_ID", param.getString("DEVELOP_DEPART_ID"));
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
        customerInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
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
        custPersonInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE"));
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

        AcctTimeEnv env = new AcctTimeEnv(param.getString("ACCT_DAY", "1"), "", "", "");
        AcctTimeEnvManager.setAcctTimeEnv(env);
        /*
         * } else { uca = super.buildUcaData(param); uca.getUser().setOpenDate(SysDateMgr.getSysTime()); }
         */
        return uca;
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new BaseCreateUserRequestData();
    }

}
