
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.trade;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.encrypt.CrmEncrypt;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctDiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TelephoneTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order.requestdata.CreateFixTelUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.trade.BaseCreateUserTrade;

public class CreateFixTelUserTrade extends BaseCreateUserTrade implements ITrade
{
    /**
     * 密码为空时，默认密码，明文
     * 
     * @author dengyong3
     * @param btd
     * @param strProductId
     * @param strUserPwd
     * @return String
     * @throws Exception
     */
    public static String creatUserPwdForDefault(BusiTradeData btd, String strProductId, String strUserPwd) throws Exception
    {
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        int iLen = 0;
        int pFlag = createFixTelUserRD.getDefaultPwdMode();
        // 根据参数生成用户服务密码
        strUserPwd = dealUserPwd(btd, pFlag, strUserPwd);

        // 根据产品生成用户服务密码
        strUserPwd = creatUserPwdForProduct(btd, strProductId, strUserPwd);
        return strUserPwd;
    }

    /**
     * 根据产品生成用户服务密码
     * 
     * @author dengyong3
     * @param btd
     * @param strProductId
     * @param pt_str_DefaultUserPwd
     * @return String
     * @throws Exception
     */
    public static String creatUserPwdForProduct(BusiTradeData btd, String strProductId, String pt_str_DefaultUserPwd) throws Exception
    {

        if (!StringUtils.isBlank(strProductId))
        {
            IDataset pPwdList = CommparaInfoQry.getCommparaCode1("CSM", "1021", strProductId, BizRoute.getRouteId());
            if (pPwdList != null && pPwdList.size() > 0)
            {
                IData pPwdData = pPwdList.getData(0);
                int pFlag = pPwdData.getInt("PARA_CODE1");
                pt_str_DefaultUserPwd = dealUserPwd(btd, pFlag, pt_str_DefaultUserPwd);
            }
        }

        return pt_str_DefaultUserPwd;
    }

    /**
     * 处理用户服务密码
     * 
     * @author dengyong3
     * @param btd
     * @param pFlag
     * @param pt_str_DefaultUserPwd
     * @return String
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String dealUserPwd(BusiTradeData btd, int pFlag, String pt_str_DefaultUserPwd) throws Exception
    {

        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        int iLen = 0;
        switch (pFlag)
        {
            case 1:// 号码的后6位
                String strSerialNumber = createFixTelUserRD.getUca().getSerialNumber();
                iLen = strSerialNumber.length();
                pt_str_DefaultUserPwd = strSerialNumber.substring(iLen - 6, 6);
                break;
            case 2:// 业务流水号后6位
                String strTradeId = createFixTelUserRD.getTradeId();
                iLen = strTradeId.length();
                pt_str_DefaultUserPwd = strTradeId.substring(iLen - 6, 6);
                break;
            /*
             * case 3:// 取sim卡13-18位 String strSimCardNo = createFixTelUserRD.getSimCardNo(); iLen =
             * strSimCardNo.length(); if (iLen >= 18) { pt_str_DefaultUserPwd = strSimCardNo.substring(12, 6); } break;
             */
            case 4:// 随机6位数密码
                iLen = createFixTelUserRD.getDefaultPwdLength();
                if (iLen < 1 || iLen >= 7)
                {
                    iLen = 6;
                }

                pt_str_DefaultUserPwd = RandomStringUtils.randomNumeric(6);
                break;
            case 5:// 身份证取后6位，其它证件置为000000
                pt_str_DefaultUserPwd = "000000";
                String strPsptTypeCode = createFixTelUserRD.getUca().getCustomer().getPsptTypeCode();
                String strPsptId = createFixTelUserRD.getUca().getCustomer().getPsptId();
                if (!"".equals(strPsptTypeCode))
                {
                    if ("0".equals(strPsptTypeCode))
                    {
                        iLen = strPsptId.length();
                        if (iLen > 6)
                        {
                            pt_str_DefaultUserPwd = strPsptId.substring(iLen - 6, 6);
                        }
                    }
                }
                break;
        }

        return pt_str_DefaultUserPwd;
    }

    /**
     * 工单记录追加
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void appendOrderData(BusiTradeData btd) throws Exception
    {

    }

    /**
     * 生成台帐账户优惠子台账
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void appendTradeAcctDiscontData(BusiTradeData btd) throws Exception
    {
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        AcctDiscntTradeData acctDiscntTradeData = new AcctDiscntTradeData();
        acctDiscntTradeData.setPartitionId(createFixTelUserRD.getUca().getAcctId().substring(0, 4));
        acctDiscntTradeData.setAcctId(btd.getRD().getUca().getAcctId());
        acctDiscntTradeData.setDiscntCode(createFixTelUserRD.getAcctDiscount());
        acctDiscntTradeData.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
        acctDiscntTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        acctDiscntTradeData.setInstId(SeqMgr.getInstId());
        acctDiscntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        acctDiscntTradeData.setUpdateTime("");
        acctDiscntTradeData.setUpdateStaffId(getVisit().getStaffId());
        acctDiscntTradeData.setUpdateDepartId(getVisit().getDepartId());
        acctDiscntTradeData.setRemark("");
        acctDiscntTradeData.setRsrNum1("0");
        btd.add(btd.getRD().getUca().getSerialNumber(), acctDiscntTradeData);
    }

    /**
     * 修改主台帐字段
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void appendTradeMainData(BusiTradeData btd) throws Exception
    {
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();

        // btd.getMainTradeData().setRsrvStr1(createFixTelUserRD.getSimCardNo());// sim卡号
        btd.getMainTradeData().setSubscribeType("300");
        btd.getMainTradeData().setPfType("300");
        btd.getMainTradeData().setOlcomTag("1");
        // 处理特殊数据：PROCESS_TAG_SET
        // btd.getMainTradeData().setProcessTagSet("00000000000000000000");
        // 先写死，要判断是否是 同帐户（改第二位），同客户,改第三位
        btd.setMainTradeProcessTagSet(2, "1");
        btd.setMainTradeProcessTagSet(3, "1");
        btd.getMainTradeData().setExecTime(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
        btd.getMainTradeData().setFinishDate("");
        btd.getMainTradeData().setRsrvStr2(CSBizBean.getVisit().getStaffName());
        // INVOICE_NO\DEFAULT_PWD_FLAG 老版界面上和后台处理都没有,此处忽略
        btd.getMainTradeData().setRsrvStr8(createFixTelUserRD.getResKindName());
        String userPasswd = CrmEncrypt.EncryptPasswd(btd.getMainTradeData().getRsrvStr9());
        btd.getMainTradeData().setRsrvStr9(userPasswd);// 用户密码
        // OPEN_DATE先忽略

        // td.setChildOrderInfo(X_TRADE_ORDER.X_CUST_ORDER, "APP_TYPE", "300"); TODO:该条处理暂定
    }

    /**
     * 生成台帐用户子表
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void BaseCreateUserTradeData(BusiTradeData btd) throws Exception
    {
        super.BaseCreateUserTradeData(btd);
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        UserTradeData userTD = (UserTradeData) btd.getTradeDatas(TradeTableEnum.TRADE_USER).get(0);
        // TODO:品牌和产品id字段已经从user表中拆分,此处用户台账不再记录,不过老版会从user台账中取产品信息去更新用户产品表这个细节要确认下
        /*
         * tradeUserData.put("BRAND_CODE" , userInfo.getString("BRAND_CODE")); tradeUserData.put("PRODUCT_ID" ,
         * userInfo.getString("PRODUCT_ID"));
         */

        // TODO:有很多字段是可以为空的,而且页面上也没有该字段对应的元素,所以此处都忽略掉
        userTD.setUserStateCodeset("0");
        userTD.setUserTypeCode("0");// 用户类型默认为0:普通用户
        userTD.setUserDiffCode("0");
        userTD.setCityCodeA("0");
        userTD.setNetTypeCode(PersonConst.FIX_TEL_NET_TYPE_CODE);
        String isBat = createFixTelUserRD.getIsBat();
        if (StringUtils.equals("1", isBat))
        {
            userTD.setOpenMode("5");
        }
        userTD.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setDevelopStaffId(CSBizBean.getVisit().getStaffId());

        userTD.setDevelopDate(SysDateMgr.getSysTime());
        userTD.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setDevelopCityCode(CSBizBean.getVisit().getCityCode());
        userTD.setDevelopEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        userTD.setRemark(createFixTelUserRD.getRemark());
        userTD.setRsrvTag1(createFixTelUserRD.getAreaType());// 地域：0-农村；1-城市
        userTD.setRsrvTag2(createFixTelUserRD.getClearAccount());
        userTD.setRsrvStr8(createFixTelUserRD.getReversePolarity());
        // TODO:SEQ_TRANS_USER_ID该seq不知道现在叫啥名字,此处先不设置,待定
        // tradeUserData.put("RSRV_STR5" , dao.getSequence("SEQ_TRANS_USER_ID"));
    }

    /**
     * 生成台帐帐户托收子表
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void createAcctConsignTradeData(BusiTradeData btd) throws Exception
    {
        AcctConsignTradeData acctConsignTD = new AcctConsignTradeData();
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        String serialNumber = createFixTelUserRD.getUca().getUser().getSerialNumber();
        acctConsignTD.setAcctId(createFixTelUserRD.getUca().getAcctId());
        acctConsignTD.setPayModeCode(createFixTelUserRD.getUca().getAccount().getPayModeCode());// 帐户付费类型：0-现金，1-托收，2-代扣
        acctConsignTD.setEparchyCode(BizRoute.getRouteId());
        acctConsignTD.setCityCode(createFixTelUserRD.getCityCode());
        acctConsignTD.setSuperBankCode(createFixTelUserRD.getSuperBankCode());
        acctConsignTD.setBankCode(createFixTelUserRD.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctNo(createFixTelUserRD.getUca().getAccount().getBankAcctNo());
        String bankName = UBankInfoQry.getBankNameByBankCode(createFixTelUserRD.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctName(bankName);
        acctConsignTD.setConsignMode("1");// 托收方式：默认为1
        acctConsignTD.setPaymentId("4");// 储值方式：默认为4
        acctConsignTD.setPayFeeModeCode("4");
        acctConsignTD.setActTag("1");
        acctConsignTD.setInstId(SeqMgr.getInstId());
        acctConsignTD.setStartCycleId(SysDateMgr.getNowCyc());
        acctConsignTD.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, acctConsignTD);
    }

    /**
     * 生成台帐帐户子表
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createAcctTradeData(BusiTradeData btd) throws Exception
    {
        super.BaseCreateAcctTradeData(btd);
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        AccountTradeData acctTD = (AccountTradeData) btd.getTradeDatas(TradeTableEnum.TRADE_ACCOUNT).get(0);
        acctTD.setAcctDiffCode("0");
        acctTD.setNetTypeCode(createFixTelUserRD.getUca().getUser().getNetTypeCode());
        acctTD.setScoreValue("0");
        acctTD.setBasicCreditValue("0");
        acctTD.setCreditValue("0");
        String isBat = createFixTelUserRD.getIsBat();

        if (StringUtils.equals("1", isBat))
        {
            acctTD.setPayName("无档户");
            acctTD.setPayModeCode("0");
        }
        acctTD.setDebutyUserId(createFixTelUserRD.getUca().getUserId());
        acctTD.setDebutyCode(createFixTelUserRD.getUca().getSerialNumber());
        acctTD.setContractNo(createFixTelUserRD.getUca().getAccount().getContractNo());
        acctTD.setDepositPriorRuleId(createFixTelUserRD.getUca().getAccount().getDepositPriorRuleId());
        acctTD.setItemPriorRuleId(createFixTelUserRD.getUca().getAccount().getItemPriorRuleId());
        acctTD.setOpenDate(createFixTelUserRD.getUca().getUser().getOpenDate());
    }

    /**
     * 创建开户具体业务台账
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 预占号码资源

        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        String isBat = createFixTelUserRD.getIsBat();
        String accountCode = createFixTelUserRD.getAcctDiscount();
        if (!StringUtils.equals("1", isBat))
        {
            PBossCall.resPreOccupy(btd.getRD().getUca().getSerialNumber(), "");
            if (accountCode != null && !"-1".equals(accountCode) && !"".endsWith(accountCode))
            {
                appendTradeAcctDiscontData(btd);
            }
            // 用户归属与客户业务归属处理:前台选择了客户业务区，则记录选择值，否则记录操作业务区 add by yinhq 2009-11-26
            String strCustCityCode = createFixTelUserRD.getCityCode();
            createFixTelUserRD.setCityCode(StringUtils.isBlank(strCustCityCode) ? CSBizBean.getVisit().getCityCode() : strCustCityCode);

            super.createBusiTradeData(btd);// 基类处理了三户、资源台账
            
            // 处理客户核心资料
            createCustomerTradeDataA(btd);
            
            appendInfo(btd);
            String mainProductId = createFixTelUserRD.getMainProduct().getProductId();// 基本产品
            // 用户开户明文
            String userPassword = createFixTelUserRD.getUca().getUser().getUserPasswd();// 用户开户密码明文
            boolean bDefaultPwd = false;
            if (StringUtils.isBlank(userPassword))
            {
                userPassword = createFixTelUserRD.getDefaultPwd();// 默认密码123456
                userPassword = creatUserPwdForDefault(btd, mainProductId, userPassword);
                createFixTelUserRD.getUca().getUser().setUserPasswd(userPassword);
                bDefaultPwd = true;
            }

            btd.getMainTradeData().setRsrvStr9(userPassword);// 用户密码明文
            appendTradeMainData(btd);// 处理主台账
            appendOrderData(btd);// 处理工单记录

            // btd.addOpenUserAcctDayData(createFixTelUserRD.getUca().getUserId(), "1");//第三个参数代表不同时处理账户下所有用户
            // btd.addOpenAccountAcctDayData(createFixTelUserRD.getUca().getAcctId(),createFixTelUserRD.getUca().getAcctDay());
            if (createFixTelUserRD.getUca().getAccount().getPayModeCode().equals("1"))
                createAcctConsignTradeData(btd);

            createPayrelationTradeData(btd);

            createResTradeData(btd);// 固话号码资源台账
            createDeviceTradeData(btd);// 设备台账

            createTradeTelephoneData(btd);// 固话台账
        }
        else
        {
            int isMainserString = createFixTelUserRD.getMainServNumFlg();
            String batOperType = btd.getRD().getBatchDealType();
            if (StringUtils.equals(batOperType, "BATCREATEFIXEDUSER"))
            {
                btd.getMainTradeData().setOlcomTag("1");
            }
            else if (StringUtils.equals(batOperType, "BATCREATETRUNKUSER") || StringUtils.equals(batOperType, "BATAPPENDTRUNKUSER"))
            {
                if (isMainserString == 1)
                {
                    btd.getMainTradeData().setOlcomTag("1");
                }
                else
                {
                    btd.getMainTradeData().setOlcomTag("0");
                    btd.setMainTradeProcessTagSet(1, "1");
                    btd.setMainTradeProcessTagSet(2, "1");

                }
            }
            if (!StringUtils.equals(btd.getTradeTypeCode(), "9752"))
            {
                String signpath = createFixTelUserRD.getSignPath();
                PBossCall.batResPreOccupy(btd.getRD().getUca().getSerialNumber(), signpath);
                // super.createBusiTradeData(btd);// 基类处理了三户、资源台账
                BaseCreateUserTradeData(btd);
                super.createProductTradeData(btd);
                BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
                // 处理元素
                ProductModuleCreator.createProductModuleTradeData(baseCreatePersonUserRD.getPmds(), btd);
                createResTradeData(btd);// 固话号码资源台账
                btd.addOpenUserAcctDayData(baseCreatePersonUserRD.getUca().getUserId(), baseCreatePersonUserRD.getUca().getAcctDay());

                createPayrelationTradeData(btd);

                if (isMainserString == 1)
                {
                    BaseCreateCustomerTradeData(btd);// 处理客户核心资料
                    BaseCreateCustPersonTradeData(btd);// 处理客户个人资料
                    createAcctTradeData(btd);// 处理账户资料
                    btd.addOpenAccountAcctDayData(baseCreatePersonUserRD.getUca().getAcctId(), baseCreatePersonUserRD.getUca().getAcctDay());
                    createTradeTelephoneData(btd);// 固话台账
                }
                else
                {
                    createRelationData(btd);

                }
            }
            else
            {

            }
        }

        btd.getMainTradeData().setPriority("40");
    }

    public void appendInfo(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
    	CreateFixTelUserRequestData rd = (CreateFixTelUserRequestData) btd.getRD();
    	UserTradeData userTD = baseCreatePersonUserRD.getUca().getUser();
    	List<UserTradeData> svcList = btd.get(TradeTableEnum.TRADE_USER.getValue());
        for (UserTradeData uTD : svcList)
        {
//!!!fengxx            String prepayTag = StaticUtil.getStaticValue(getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "PREPAY_TAG", rd.getMainProduct().getProductId());
//            userTD.setPrepayTag(prepayTag);
        	String prepayTag = "";
        	IData prepayTagDataset = UpcCall.queryOfferByOfferId("P", rd.getMainProduct().getProductId(), "Y");
			if(IDataUtil.isNotEmpty(prepayTagDataset)){
				prepayTag=prepayTagDataset.getString("PREPAY_TAG");
			}
			uTD.setPrepayTag(prepayTag);
        	
        }
    	userTD.setRsrvTag1(rd.getAreaType());
    	userTD.setRsrvTag2(rd.getClearAccount());
    	userTD.setRsrvStr5(SeqMgr.getTransUserId());
    	AccountTradeData acctTD = rd.getUca().getAccount();
    	acctTD.setRsrvStr3(rd.getPaperType());
    	acctTD.setNetTypeCode(PersonConst.FIX_TEL_NET_TYPE_CODE);
    	acctTD.setRsrvStr1(SeqMgr.getTransAcctId());
    	//btd.add(rd.getUca().getSerialNumber(), userTD);

    }
    
    public void createBusiTradeDataFoeThis(BusiTradeData btd) throws Exception
    {
        BaseCreateUserRequestData baseCreatePersonUserRD = (BaseCreateUserRequestData) btd.getRD();
        BaseCreateUserTradeData(btd);// 处理用户台账
        super.BaseCreateCustomerTradeData(btd);// 处理客户核心资料
        super.BaseCreateCustPersonTradeData(btd);// 处理客户个人资料
        super.BaseCreateAcctTradeData(btd);// 处理账户资料

        super.createProductTradeData(btd);

        // 处理元素
        ProductModuleCreator.createProductModuleTradeData(baseCreatePersonUserRD.getPmds(), btd);

        // 处理分散账期 因为有预约的概念，需要修改 后续处理 sunxin
        btd.addOpenUserAcctDayData(baseCreatePersonUserRD.getUca().getUserId(), baseCreatePersonUserRD.getUca().getAcctDay());

        btd.addOpenAccountAcctDayData(baseCreatePersonUserRD.getUca().getAcctId(), baseCreatePersonUserRD.getUca().getAcctDay());

    }
    
    /**
     * 生成台帐核心客户表
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeDataA(BusiTradeData btd) throws Exception
    {
    	CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        List<CustomerTradeData> customerTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER);
        for (CustomerTradeData customerTradeData : customerTradeDatas)
        {
            customerTradeData.setRsrvStr7(createFixTelUserRD.getAgentCustName());// 经办人名称
            customerTradeData.setRsrvStr8(createFixTelUserRD.getAgentPsptTypeCode());// 经办人证件类型
            customerTradeData.setRsrvStr9(createFixTelUserRD.getAgentPsptId());// 经办人证件号码
            customerTradeData.setRsrvStr10(createFixTelUserRD.getAgentPsptAddr());// 经办人证件地址
        }
    }

    /**
     * 生成台帐核心客户表
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {
        super.BaseCreateCustomerTradeData(btd);
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        CustomerTradeData customerTD = (CustomerTradeData) btd.getTradeDatas(TradeTableEnum.TRADE_CUSTOMER).get(0);
        customerTD.setCustType("0");
        customerTD.setCustState("0");
        customerTD.setOpenLimit("0");// 默认为0
        customerTD.setInDate(createFixTelUserRD.getUca().getUser().getOpenDate());
        customerTD.setInStaffId(CSBizBean.getVisit().getStaffId());
        customerTD.setInDepartId(CSBizBean.getVisit().getDepartId());
        customerTD.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        customerTD.setDevelopStaffId(CSBizBean.getVisit().getStaffId());

    }

    /**
     * 生成设备资源子台账
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createDeviceTradeData(BusiTradeData btd) throws Exception
    {
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        IDataset deviceDataset = createFixTelUserRD.getDeviceRes();

        if (null == deviceDataset)
            return;

        for (int i = 0; i < deviceDataset.size(); i++)
        {
            ResTradeData resTradeData = new ResTradeData();
            IData deviceData = deviceDataset.getData(i);
            resTradeData.setUserId(createFixTelUserRD.getUca().getUserId());
            resTradeData.setUserIdA("-1");// TODO:?uca对象里面没有userida属性
            resTradeData.setResTypeCode(deviceData.getString("DEVICE_TYPE_CODE"));
            resTradeData.setResCode(deviceData.getString("DEVICE_CODE", "-" + i));
            resTradeData.setInstId(SeqMgr.getInstId());
            resTradeData.setCampnId("");
            resTradeData.setStartDate(SysDateMgr.getSysTime());
            resTradeData.setEndDate(SysDateMgr.getTheLastTime());
            resTradeData.setModifyTag("0");
            resTradeData.setRemark("固话用户开户");
            resTradeData.setRsrvStr4(deviceData.getString("DEVICE_KIND_CODE"));// 资源小类
            resTradeData.setRsrvTag1(deviceData.getString("USE_TYPE_CODE"));// 使用方式：0-购买 ；1-赠送
            // resTradeData.setRsrvNum1(deviceData.getString("DEVICE_PRICE"));// 设备价格(资源价格)

            btd.add(createFixTelUserRD.getUca().getSerialNumber(), resTradeData);

            // 老版本此处对于多个设备,主台账只能记录最后设备信息,此处采用追加的方式
            if (!(deviceData.getString("DEVICE_CODE").substring(0, 1).equals("-")))
            {
                if (deviceData.getString("DEVICE_KIND_CODE").equals("01"))
                {// 固话座机
                    String rsrvstr3 = btd.getMainTradeData().getRsrvStr3() + "," + deviceData.getString("DEVICE_CODE");
                    btd.getMainTradeData().setRsrvStr3(rsrvstr3);
                    resTradeData.setRsrvTag2("1");// 使用方式：1表示座机。2表示计费器
                }
                else if (deviceData.getString("DEVICE_KIND_CODE").equals("04"))
                {// 计费器
                    String rsrvstr4 = btd.getMainTradeData().getRsrvStr4() + "," + deviceData.getString("DEVICE_CODE");
                    resTradeData.setRsrvTag2("2");// 使用方式：1表示座机。2表示计费器
                    btd.getMainTradeData().setRsrvStr4(rsrvstr4);
                }
                resTradeData.setRsrvStr4(deviceData.getString("DEVICE_CODE"));
            }
        }
    }

    /**
     * 生成台帐付费表
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    private void createPayrelationTradeData(BusiTradeData btd) throws Exception
    {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        String serialNumber = createFixTelUserRD.getUca().getUser().getSerialNumber();
        payrelationTD.setUserId(createFixTelUserRD.getUca().getUserId());
        payrelationTD.setAcctId(createFixTelUserRD.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setRemark(createFixTelUserRD.getRemark());
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(serialNumber, payrelationTD);
    }

    private void createRelationData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        CreateFixTelUserRequestData crateTelUserData = (CreateFixTelUserRequestData) btd.getRD();
        RelationTradeData relaTionData = new RelationTradeData();
        relaTionData.setUserIdA(crateTelUserData.getMainUserId());
        relaTionData.setSerialNumberA(crateTelUserData.getMainSerNum());
        relaTionData.setUserIdB(btd.getRD().getUca().getUserId());
        relaTionData.setSerialNumberB(btd.getRD().getUca().getSerialNumber());
        relaTionData.setRelationTypeCode("T1");
        relaTionData.setRoleCodeA("0");
        relaTionData.setRoleCodeB("2");
        relaTionData.setOrderno("-1");
        relaTionData.setStartDate(SysDateMgr.getSysDate());
        relaTionData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        relaTionData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        relaTionData.setInstId(SeqMgr.getInstId());
        btd.add(btd.getRD().getUca().getSerialNumber(), relaTionData);
    }

    /**
     * 生成固话号码资源子台账
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createResTradeData(BusiTradeData btd) throws Exception
    {
        ResTradeData resTradeData = new ResTradeData();
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        resTradeData.setUserId(createFixTelUserRD.getUca().getUserId());
        resTradeData.setUserIdA("-1");
        resTradeData.setResTypeCode("N");
        resTradeData.setResCode(createFixTelUserRD.getUca().getSerialNumber());
        resTradeData.setImsi("");
        resTradeData.setKi("");
        resTradeData.setInstId(SeqMgr.getInstId());
        resTradeData.setCampnId("");
        resTradeData.setStartDate(SysDateMgr.getSysTime());
        resTradeData.setEndDate(SysDateMgr.getTheLastTime());
        resTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTradeData.setRsrvStr1(createFixTelUserRD.getSwitchId());// 交换机编码
        resTradeData.setRsrvStr2(createFixTelUserRD.getSwitchType());// 交换机类型
        resTradeData.setRsrvStr4(createFixTelUserRD.getResKindCode());// 资源小类

        btd.add(createFixTelUserRD.getUca().getSerialNumber(), resTradeData);
    }

    /**
     * 生成固话台账
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createTradeTelephoneData(BusiTradeData btd) throws Exception
    {
        CreateFixTelUserRequestData createFixTelUserRD = (CreateFixTelUserRequestData) btd.getRD();
        TelephoneTradeData telTradeData = new TelephoneTradeData();

        telTradeData.setUserId(createFixTelUserRD.getUca().getUserId());
        telTradeData.setCancelTag("0");
        telTradeData.setStandAddress(createFixTelUserRD.getStandAddress());
        telTradeData.setdetailAddress(createFixTelUserRD.getDetailAddress());
        telTradeData.setSignPath(createFixTelUserRD.getSignPath());// 老版本PORT_TYPE在界面上没有找到设置地方,此处忽略
        telTradeData.setSecret(createFixTelUserRD.getSecret());
        telTradeData.setStandAddressCode(createFixTelUserRD.getStandAddressCode());
        telTradeData.setModifyTag("0");
        telTradeData.setInstId(SeqMgr.getInstId());
        telTradeData.setStartDate(SysDateMgr.getSysTime());
        telTradeData.setEndDate(SysDateMgr.getTheLastTime());
        telTradeData.setRemark("固话用户开户");

        btd.add(createFixTelUserRD.getUca().getSerialNumber(), telTradeData);
    }
}
