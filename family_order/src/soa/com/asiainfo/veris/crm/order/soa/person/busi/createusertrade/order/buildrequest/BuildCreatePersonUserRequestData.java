
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

public class BuildCreatePersonUserRequestData extends BuildCreateUserRequestData implements IBuilder
{

    /**
     * 构建登记流程 业务数据输入，后续逻辑处理从RequestData中获取数据，即这里的brd
     */

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        super.buildBusiRequestData(param, brd);
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) brd;
        
        //REQ201911080010 关于实名入网办理日志留存的改造通知 - add by guonj -20200305 
        createPersonUserRD.setDevRead(param.getString("custInfo_DEV_READ", ""));
        createPersonUserRD.setReadRuslt(param.getString("custInfo_READ_RUSLT", ""));
        createPersonUserRD.setComparisonIs(param.getString("custInfo_COMPARISON_IS", ""));
        createPersonUserRD.setComparisonRuslt(param.getString("custInfo_COMPARISON_RUSLT", ""));
        createPersonUserRD.setComparisonSeq(param.getString("custInfo_COMPARISON_SEQ", ""));
        createPersonUserRD.setAuthenticityIs(param.getString("custInfo_AUTHENTICITY_IS", ""));
        createPersonUserRD.setAuthenticityRuslt(param.getString("custInfo_AUTHENTICITY_RUSLT", ""));
        createPersonUserRD.setAuthenticitySeq(param.getString("custInfo_AUTHENTICITY_SEQ", ""));
        createPersonUserRD.setProvenumIs(param.getString("custInfo_PROVENUM_IS", ""));
        createPersonUserRD.setProvenumRuslt(param.getString("custInfo_PROVENUM_RUSLT", ""));
        createPersonUserRD.setProvenumSeq(param.getString("custInfo_PROVENUM_SEQ", ""));
        
        createPersonUserRD.setMsisdn_type(param.getString("MSISDN_TYPE", ""));
        // 海南特殊
        // 用于接口处理
        createPersonUserRD.setCityCode(param.getString("CITY_CODE", ""));// 用于接口处理
        createPersonUserRD.setOperateId(param.getString("OPERATE_ID", ""));// 用于接口处理
        createPersonUserRD.setNetTypeCode(param.getString("NET_TYPE_CODE"));

        /*
         * createPersonUserRD.setPreOpenTag(param.getString("PRE_OPEN_TAG", "0")); //
         * createPersonUserRD.setPreOpenTime(param.getString("PRE_OPEN_DATE","0")); String sysDate =
         * "1".equals(createPersonUserRD.getPreOpenTag()) ? param.getString("PRE_OPEN_DATE") :
         * SysDateMgr.getSysTime();// 系统时间 createPersonUserRD.setSysDate(sysDate);
         */
        String agentId = param.getString("AGENT_DEPART_ID", "");
        if (agentId.equals(""))
            agentId = param.getString("AGENT_DEPART_ID1", "");
        if (!agentId.equals(""))
        {
            if (agentId.indexOf(" ") != -1)
                agentId = (agentId.split(" "))[0];
        }
        else
            agentId = CSBizBean.getVisit().getDepartId();
        createPersonUserRD.setAgentDepartId(agentId);// 特殊处理 sunxin
        createPersonUserRD.setBindSaleTag(param.getString("BIND_SALE_TAG", "0"));// 绑定营销活动
        createPersonUserRD.setAgentCustName(param.getString("AGENT_CUST_NAME", ""));// 经办人名称
        createPersonUserRD.setAgentPsptTypeCode(param.getString("AGENT_PSPT_TYPE_CODE", ""));// 经办人证件类型
        createPersonUserRD.setAgentPsptId(param.getString("AGENT_PSPT_ID", ""));// 经办人证件号码
        createPersonUserRD.setAgentPsptAddr(param.getString("AGENT_PSPT_ADDR", ""));// 经办人证件地址

        createPersonUserRD.setActiveTag(param.getString("ACTIVE_TAG", "0"));
        createPersonUserRD.setRealName(param.getString("REAL_NAME", "0"));// 页面需要根据选择赋值
        createPersonUserRD.setSaleProductId(param.getString("SALE_PRODUCT_ID"));// 绑定的营销活动ID 页面赋值
        createPersonUserRD.setSalePackageId(param.getString("SALE_PACKAGE_ID"));// 绑定的营销包ID 页面赋值
        createPersonUserRD.setInvoiceNo(param.getString("_INVOICE_CODE", "0"));// 发票号码 页面赋值
        createPersonUserRD.setCardPasswd(param.getString("CARD_PASSWD", ""));// 初始化密码
        createPersonUserRD.setPassCode(param.getString("PASSCODE", ""));// 密码因子
        createPersonUserRD.setDefaultPwdFlag(param.getString("DEFAULT_PWD_FLAG", "0"));// 是否使用初始密码

        // 初始化参数，隐藏信息

        createPersonUserRD.setCallingTypeCode(param.getString("CALLING_TYPE_CODE", ""));// 物联网新增应用行业类型
        createPersonUserRD.setPayModeCode(param.getString("PAY_MODE_CODE", "0"));
        createPersonUserRD.setBindSaleTag(param.getString("BIND_SALE_TAG", "0"));// 是否绑定营销活动 页面赋值
        createPersonUserRD.setBindDefaultDiscnt(param.getString("X_BIND_DEFAULT_DISCNT", ""));// "优惠编码|绑定月份"
        // 资源返回的吉祥号码开户默认绑定的优惠档次
        // 页面赋值

        // 初始化参数，隐藏信息

        createPersonUserRD.setAcctTag(param.getString("ACCT_TAG", "0"));
        createPersonUserRD.setOpenMode(param.getString("OPEN_MODE", "0"));
        createPersonUserRD.setSuperBankCode(param.getString("SUPER_BANK_CODE"));
        createPersonUserRD.setOpenType(param.getString("OPEN_TYPE", ""));
        createPersonUserRD.setOpenLimitTag(param.getString("OPEN_LIMIT_TAG"));
        createPersonUserRD.setOpenLimitCount(param.getString("OPEN_LIMIT_COUNT"));
        createPersonUserRD.setCustNameLimit(param.getString("CUSTNAME_LIMIT"));
        createPersonUserRD.setDefaultPwdMode(param.getInt("DEFAULT_PWD_MODE", 4));
        createPersonUserRD.setDefaultPwd(param.getString("DEFAULT_PWD", "123456"));
        createPersonUserRD.setDefaultPwdLength(param.getInt("DEFAULT_PWD_LENGTH", 6));
        createPersonUserRD.setDefaultUserType(param.getString("DEFAULT_USER_TYPE"));
        createPersonUserRD.setDefaultPsptType(param.getString("DEFAULT_PSPT_TYPE"));
        createPersonUserRD.setDefaultPayMode(param.getString("DEFAULT_PAY_MODE"));
        createPersonUserRD.setChrBlackCheckMode(param.getString("CHR_BLACKCHECKMODE"));
        createPersonUserRD.setChrCheckOweFeeByPspt(param.getString("CHR_CHECKOWEFEEBYPSPT"));
        createPersonUserRD.setChrCheckOweFeeByPsptAllUser(param.getString("CHR_CHECKOWEFEEBYPSPT_ALLUSER"));
        createPersonUserRD.setResCheckByDepart(param.getString("RES_CHECK_BY_DEPART"));
        createPersonUserRD.setChrAutoPasswd(param.getString("CHR_AUTO_PASSWD"));
        // createPersonUserRD.setProvOpenAdvancePayFlag(param.getString("PROV_OPEN_ADVANCE_PAY_FLAG"));
        // createPersonUserRD.setProvOpenAdvancePay(param.getString("PROV_OPEN_ADVANCE_PAY"));
        // createPersonUserRD.setProvOpenOperFeeFlag(param.getString("PROV_OPEN_OPERFEE_FLAG"));
        // createPersonUserRD.setProvOpenOperFee(param.getString("PROV_OPEN_OPERFEE"));
        // createPersonUserRD.setChrUserGgCard(param.getString("CHR_USEGGCARD", ""));

        //行业应用卡批量开户标记,是否M2M类型，0不是，1是
        createPersonUserRD.setM2mTag(param.getString("M2M_TAG", "0"));
        
        createPersonUserRD.setChrCheckSeleNum(param.getString("CHR_CHECKSELENUM"));

        // 其他隐藏信息
        // createPersonUserRD.setOldSerialNumber(param.getString("OLD_SERIAL_NUMBER"));
        // createPersonUserRD.setOldSimCardNo(param.getString("OLD_SIM_CARD_NO"));
        // createPersonUserRD.setOldPsptTypeCode(param.getString("OLD_PSPT_TYPE_CODE"));
        // createPersonUserRD.setOldPsptId(param.getString("OLD_PSPT_ID"));
        createPersonUserRD.setCheckResultCode(param.getString("CHECK_RESULT_CODE"));
        createPersonUserRD.setCheckPsptCode(param.getString("CHECK_PSPT_CODE"));

        // 如果是2次开户，需要将三户资料重新取值，防止页面进行修改，数据不正确 sunxin
        /*
         * if ("1".equals(param.getString("B_REOPEN_TAG", ""))) { // 设置三户资料对象 UcaData uca =
         * DataBusManager.getDataBus().getUca(param.getString("SERIAL_NUMBER")); if (uca == null) { uca =
         * UcaDataFactory.getNormalUca(param.getString("SERIAL_NUMBER")); } // 将页面修改的值重新put进uca
         * uca.getUser().setUserPasswd(param.getString("USER_PASSWD"));
         * uca.getUser().setOpenDate(SysDateMgr.getSysTime()); uca.getUser().setModifyTag(BofConst.MODIFY_TAG_UPD);
         * uca.getUser().setDevelopDepartId(agentId); uca.getCustomer().setCustName(param.getString("CUST_NAME"));
         * uca.getCustomer().setPsptId(param.getString("PSPT_ID"));
         * uca.getCustomer().setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
         * uca.getCustomer().setModifyTag(BofConst.MODIFY_TAG_UPD);
         * uca.getCustPerson().setCustName(param.getString("CUST_NAME"));
         * uca.getCustPerson().setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
         * uca.getCustPerson().setPsptId(param.getString("PSPT_ID"));
         * uca.getCustPerson().setPsptEndDate(param.getString("PSPT_END_DATE"));
         * uca.getCustPerson().setPsptAddr(param.getString("PSPT_ADDR"));
         * uca.getCustPerson().setSex(param.getString("SEX"));
         * uca.getCustPerson().setBirthday(param.getString("BIRTHDAY"));
         * uca.getCustPerson().setPostAddress(param.getString("POST_ADDRESS"));
         * uca.getCustPerson().setPostCode(param.getString("POST_CODE"));
         * uca.getCustPerson().setPhone(param.getString("PHONE"));
         * uca.getCustPerson().setFaxNbr(param.getString("FAX_NBR"));
         * uca.getCustPerson().setEmail(param.getString("EMAIL"));
         * uca.getCustPerson().setHomeAddress(param.getString("HOME_ADDRESS"));
         * uca.getCustPerson().setWorkName(param.getString("WORK_NAME"));
         * uca.getCustPerson().setWorkDepart(param.getString("WORK_DEPART"));
         * uca.getCustPerson().setJobTypeCode(param.getString("JOB_TYPE_CODE"));
         * uca.getCustPerson().setContact(param.getString("CONTACT"));
         * uca.getCustPerson().setContactPhone(param.getString("CONTACT_PHONE"));
         * uca.getCustPerson().setContactTypeCode(param.getString("CONTACT_TYPE_CODE"));
         * uca.getCustPerson().setNationalityCode(param.getString("NATIONALITY_CODE"));
         * uca.getCustPerson().setFolkCode(param.getString("FOLK_CODE"));
         * uca.getCustPerson().setReligionCode(param.getString("RELIGION_CODE"));
         * uca.getCustPerson().setLanguageCode(param.getString("LANGUAGE_CODE"));
         * uca.getCustPerson().setEducateDegreeCode(param.getString("EDUCATE_DEGREE_CODE"));
         * uca.getCustPerson().setMarriage(param.getString("MARRIAGE"));
         * uca.getCustPerson().setCallingTypeCode(param.getString("CALLING_TYPE_CODE"));
         * uca.getCustPerson().setModifyTag(BofConst.MODIFY_TAG_UPD);
         * uca.getAccount().setPayName(param.getString("PAY_NAME"));
         * uca.getAccount().setPayModeCode(param.getString("PAY_MODE_CODE", ""));
         * uca.getAccount().setBankCode(param.getString("BANK_CODE"));
         * uca.getAccount().setBankAcctNo(param.getString("BANK_ACCT_NO"));
         * uca.getAccount().setModifyTag(BofConst.MODIFY_TAG_UPD); }
         */
        // 处理批量开户费用
        if ("500".equals(param.getString("TRADE_TYPE_CODE", "500")))
        {
            createPersonUserRD.setXTradeGiftFee(param.getString("X_TRADE_GIFTFEE", ""));
            if (StringUtils.isNotBlank(param.getString("X_TRADE_FEESUB", "")))
            {
                makeFeeData(param, createPersonUserRD);
            }
        }
        
        if(StringUtils.isNotBlank(param.getString("EID",""))){//一号一终端开户登记EID

        	createPersonUserRD.setEid(param.getString("EID")+"@"+param.getString("IMEI"));
            createPersonUserRD.setPrimarySerialNumber(param.getString("PRIMARY_SERIAL_NUMBER"));
            createPersonUserRD.setNewImei(param.getString("IMEI"));
        }


        // 处理批量开户费用
        if ("700".equals(param.getString("TRADE_TYPE_CODE", "700")))
        {
            createPersonUserRD.setAgentFee(param.getString("AGENT_FEE", ""));
            if (StringUtils.isNotBlank(param.getString("ADVANCE_FEE", "")))
            {
                makeFeeDataForAgent(param, createPersonUserRD);
            }
        }
        
        //REQ201502050013放号政策调整需求 by songlm
        if (StringUtils.isNotBlank(param.getString("AGENT_PRESENT_FEE", "")))
        {
        	String agentPresentFee = param.getString("AGENT_PRESENT_FEE", "");
        	String commType = "";
        	if ("500".equals(param.getString("TRADE_TYPE_CODE", "500")))
            {
        		commType = "519";//如果是批量预开户，则对应td_s_commpara中的519 未激活用户配置的产品包            
        	}
        	else
        	{
        		commType = "518";
        	}
        	createPersonUserRD.setAgentPresentFee(commType + "|" + agentPresentFee);
        }

    }

    /**
     * 定义requestData对象
     */

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CreatePersonUserRequestData();
    }

    /*
     * public UcaData buildUcaData(IData param) throws Exception { // 设置三户资料对象 String sn =
     * param.getString("SERIAL_NUMBER"); if (param.getString("B_REOPEN_TAG", "").equals("1")) { UcaData uca = new
     * UcaData(); uca = DataBusManager.getDataBus().getUca(sn); if (uca == null) { uca =
     * UcaDataFactory.getNormalUca(sn); } return uca; } else { } }
     */
    public void makeFeeData(IData param, CreatePersonUserRequestData brd) throws Exception
    {
        // 预存时，插0预存费用存折
        FeeData feeData = new FeeData();
        feeData.setFeeMode("2");
        feeData.setFeeTypeCode("0");
        feeData.setFee("0");
        feeData.setOldFee("0");
        brd.addFeeData(feeData);
    }

    public void makeFeeDataForAgent(IData param, CreatePersonUserRequestData brd) throws Exception
    {

        // 有预存时，插预存费用存折
        FeeData feeData = new FeeData();
        feeData.setFeeMode("2");
        feeData.setFeeTypeCode("0");
        feeData.setFee(param.getString("ADVANCE_FEE", ""));
        feeData.setOldFee(param.getString("ADVANCE_FEE", ""));
        brd.addFeeData(feeData);
    }
}
