
package com.asiainfo.veris.crm.iorder.soa.family.busi.accept.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.busi.accept.order.requestdata.FamilyAcceptReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.buildrequest.BuildBaseFamilyBusiReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.data.CustFamilyData;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

/**
 * @Description 家庭资料登记类
 * @Auther: zhenggang
 * @Date: 2020/7/30 10:24
 * @version: V1.0
 */
public class BuildFamilyAccept extends BuildBaseFamilyBusiReqData implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FamilyAcceptReqData reqData = (FamilyAcceptReqData) brd;
        String fmyProductMode = param.getString("FAMILY_PRODUCT_MODE");
        String fmyBrandCode = param.getString("FAMILY_BRAND_CODE");
        String isEffectNow = param.getString("IS_EFFECT_NOW");

        reqData.setFmyProductMode(fmyProductMode);
        reqData.setFmyBrandCode(fmyBrandCode);
        reqData.setIsEffectNow(isEffectNow);
        reqData.setCustFamilyData(new CustFamilyData(param));
        // 家庭用户模拟角色对象
        param.put(KeyConstants.ROLE_CODE, FamilyRolesEnum.FAMILY.getRoleCode());
        param.put(KeyConstants.ROLE_TYPE, FamilyConstants.TYPE_NEW);
        super.buildBusiRequestData(param, brd);
        // 设置单加成员接口必传字段
        reqData.setFamilySn(reqData.getUca().getSerialNumber());
        reqData.setFamilyUserId(reqData.getUca().getUserId());
        reqData.setFmyAcctId(reqData.getUca().getAcctId());
        reqData.getUca().setProductId(reqData.getFmyProductId());
        reqData.getUca().setBrandCode(reqData.getFmyBrandCode());
        reqData.getUca().setAcctDay(param.getString("ACCT_DAY", "1"));
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        UcaData uca = new UcaData();
        String mainSn = param.getString("MANAGER_SN");
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取主卡用户信息无数据！");
        }

        String userIdA = SeqMgr.getUserId();
        String serialNumberA = getSerialNumberA(userIdA);
        String acctId = SeqMgr.getAcctId();

        // 设置三户资料对象
        uca.setUser(buildFamilyUserInfo(userInfo, userIdA, serialNumberA));

        String custId = userInfo.getString("CUST_ID");

        IData customerInfo = CustomerInfoQry.qryCustInfo(custId);
        customerInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_USER);

        IData custPersonInfo = UcaInfoQry.qryPerInfoByCustId(custId);
        custPersonInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_USER);

        uca.setCustomer(new CustomerTradeData(customerInfo));
        uca.setCustPerson(new CustPersonTradeData(custPersonInfo));

        uca.setCustFamily(buildCustFamilyInfo(uca.getCustPerson(), param));
        uca.setAccount(buildFamilyAcctInfo(userInfo, acctId));

        uca.setAcctBlance("0");
        uca.setLastOweFee("0");
        uca.setRealFee("0");

        uca.setAcctDay("1");
        uca.setFirstDate("");
        uca.setNextAcctDay("");
        uca.setNextFirstDate("");
        AcctTimeEnv env = new AcctTimeEnv("1", "", "", "");
        AcctTimeEnvManager.setAcctTimeEnv(env);
        return uca;
    }

    private String getSerialNumberA(String userIdA) throws Exception
    {
        StringBuffer snA = new StringBuffer();
        String eparchyCode = CSBizBean.getTradeEparchyCode().substring(2);
        String yyyymm = SysDateMgr.getNowCyc();
        String yymm = yyyymm.substring(yyyymm.length() - 4);
        snA.append("FM").append(eparchyCode).append(yymm);
        if (userIdA.length() < 8)
        {
            snA.append(userIdA);
        }
        else
        {
            snA.append(userIdA.substring(userIdA.length() - 7));
        }
        return snA.toString();
    }

    private UserTradeData buildFamilyUserInfo(IData userInfo, String userIdA, String serialNumberA) throws Exception
    {
        UserTradeData utd = new UserTradeData();
        utd.setUserId(userIdA);
        utd.setCustId(userInfo.getString("CUST_ID")); // 与主卡一致
        utd.setUsecustId(userInfo.getString("CUST_ID"));
        utd.setEparchyCode(userInfo.getString("EPARCHY_CODE")); // 与主卡一致
        utd.setCityCode(userInfo.getString("CITY_CODE")); // 与主卡一致
        utd.setUserTypeCode("0");
        utd.setSerialNumber(serialNumberA);
        utd.setUserStateCodeset("0");
        utd.setPrepayTag("1"); // 预付费标记：0：后付费，1：预付费
        utd.setMputeMonthFee("0");
        utd.setOpenDate(SysDateMgr.getSysTime());
        utd.setInDate(SysDateMgr.getSysTime()); // 建档时间
        utd.setInStaffId(CSBizBean.getVisit().getStaffId()); // 建档员工
        utd.setInDepartId(CSBizBean.getVisit().getDepartId()); // 建档渠道
        utd.setOpenStaffId(CSBizBean.getVisit().getStaffId()); // 开户员工
        utd.setOpenDepartId(CSBizBean.getVisit().getDepartId()); // 开户渠道
        utd.setDevelopStaffId(CSBizBean.getVisit().getStaffId());// 发展员工
        utd.setDevelopDate(SysDateMgr.getSysTime());// 发展日期
        utd.setDevelopDepartId(CSBizBean.getVisit().getDepartId()); // 发展渠道
        utd.setDevelopCityCode(CSBizBean.getVisit().getCityCode()); // 发展县市
        utd.setDevelopEparchyCode(CSBizBean.getTradeEparchyCode()); // 发展地市
        utd.setRemoveTag("0");
        utd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        utd.setAcctTag("Z"); // 不出账
        utd.setOpenMode("0");
        utd.setNetTypeCode("09");
        utd.setRemark("家庭用户资料");
        return utd;
    }

    private AccountTradeData buildFamilyAcctInfo(IData userInfo, String acctId) throws Exception
    {
        AccountTradeData accountTradeData = new AccountTradeData();
        accountTradeData.setAcctId(acctId);
        accountTradeData.setCustId(userInfo.getString("CUST_ID"));
        accountTradeData.setPayName("虚拟");
        accountTradeData.setPayModeCode("0");
        accountTradeData.setScoreValue("0");
        accountTradeData.setBasicCreditValue("0");
        accountTradeData.setCreditValue("0");
        accountTradeData.setOpenDate(SysDateMgr.getFirstDayOfThisMonth() + SysDateMgr.getFirstTime00000());
        accountTradeData.setRemoveTag("0");
        accountTradeData.setEparchyCode(userInfo.getString("EPARCHY_CODE"));
        accountTradeData.setCityCode(userInfo.getString("CITY_CODE"));
        accountTradeData.setAcctDiffCode("0");
        accountTradeData.setNetTypeCode(userInfo.getString("NET_TYPE_CODE", "00"));
        accountTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        accountTradeData.setRemark("家庭用户账户");
        return accountTradeData;
    }

    private CustFamilyTradeData buildCustFamilyInfo(CustPersonTradeData custPerson, IData input) throws Exception
    {
        CustFamilyData cfd = new CustFamilyData(input);
        CustFamilyTradeData custFamilyTradeData = new CustFamilyTradeData();
        custFamilyTradeData.setHomeCustId(custPerson.getCustId());
        custFamilyTradeData.setHomeId(SeqMgr.getHomeId());
        custFamilyTradeData.setHomeName(cfd.getHomeName());
        custFamilyTradeData.setHomePhone(cfd.getHomePhone());
        custFamilyTradeData.setHomeAddress(cfd.getHomeAddress());
        custFamilyTradeData.setHomeRegion(cfd.getHoneRegion());
        custFamilyTradeData.setHeadCustId(custPerson.getCustId());
        custFamilyTradeData.setHeadPsptId(custPerson.getPsptId());
        custFamilyTradeData.setHeadTypeCode(custPerson.getPsptTypeCode());
        custFamilyTradeData.setSerialNumber(cfd.getHeadSerialNumber());
        custFamilyTradeData.setHomeState("0");
        custFamilyTradeData.setCityCode(custPerson.getCityCode());
        custFamilyTradeData.setCustName(custPerson.getCustName());
        custFamilyTradeData.setEmail(custPerson.getEmail());
        custFamilyTradeData.setEparchyCode(custPerson.getEparchyCode());
        custFamilyTradeData.setFaxNbr(custPerson.getFaxNbr());
        custFamilyTradeData.setInDate(SysDateMgr.getSysTime()); // 建档时间
        custFamilyTradeData.setInStaffId(CSBizBean.getVisit().getStaffId()); // 建档员工
        custFamilyTradeData.setInDepartId(CSBizBean.getVisit().getDepartId()); // 建档渠道
        custFamilyTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        custFamilyTradeData.setPhone(custPerson.getPhone());
        custFamilyTradeData.setRemoveTag("0");
        return custFamilyTradeData;
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyAcceptReqData();
    }

}
