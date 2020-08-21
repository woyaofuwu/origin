
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changepayrel.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.AcctDateUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changepayrel.order.requestdata.PayRelationInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

public class PayRelationInfoTrade extends BaseTrade implements ITrade
{

    /**
     * 分散账期处理 1、给新账户加账期 2、如果用户新账户是托收、且用户原账期不是1号。给修改用户对应的账期日为1号。
     * 
     * @param btd
     * @throws Exception
     */
    public void acctDayDeal(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        if (StringUtils.equals("1", reqData.getPayModeCode()))// 用户为托收时,新账户账期日为1号
        {
            btd.addOpenAccountAcctDayData(reqData.getNewAcctId(), "1");// 托收新账户的账期日必须为1号
            if (!StringUtils.equals("1", reqData.getUca().getAcctDay()))// 用户原来的账期日不是1号，则改成1号
            {
                // 此处只需要修改当前这个用户的账期，原账户下的其他用户不需要改变
                btd.addChangeAcctDayData(reqData.getUca().getUserId(), "1", false);// 第三个参数代表不同时处理账户下所有用户
            }
        }
        else
        { // 否则继承原来的账期日
            btd.addOpenAccountAcctDayData(reqData.getNewAcctId(), reqData.getUca().getAcctDay());
        }
    }

    /**
     * 设置业务台帐帐户托收信息子表
     * 
     * @param btd
     * @param baseParams
     * @throws Exception
     */
    private void createAccountConsignTrade(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        AcctConsignTradeData acctConsignTradeData = new AcctConsignTradeData();
        String startCycleId = "";// 新的托收开始账期
        if (StringUtils.equals("1", reqData.getUca().getAcctDay()))// 用户账期日为自然月账期1号
        {
            startCycleId = SysDateMgr.getNowCyc();// 当前月份第一天
        }
        else
        {
            // 取用户下个账期日所在月的下个月
            String firstDayNextAcct = AcctDateUtils.getFirstDayNextAcct(reqData.getAcceptTime(), reqData.getUca().getAcctDay(), reqData.getUca().getFirstDate());
            startCycleId = StringUtils.replace(SysDateMgr.getDateNextMonthFirstDay(firstDayNextAcct).substring(0, 6), "-", "");
        }
        acctConsignTradeData.setAcctId(reqData.getNewAcctId());// 新账户ID
        acctConsignTradeData.setPayModeCode(reqData.getPayModeCode());
        acctConsignTradeData.setEparchyCode(CSBizBean.getUserEparchyCode());
        acctConsignTradeData.setCityCode(reqData.getUca().getUser().getCityCode());
        if ("0".equals(reqData.getPayModeCode()))
        {
            acctConsignTradeData.setSuperBankCode("-1");
            acctConsignTradeData.setBankCode("-1");
            acctConsignTradeData.setBankAcctNo("-1");
            acctConsignTradeData.setBankAcctName("-1");
        }
        else
        {
            acctConsignTradeData.setSuperBankCode(reqData.getSuperBankCode());
            acctConsignTradeData.setBankCode(reqData.getBankCode());
            acctConsignTradeData.setBankAcctNo(reqData.getBankAcctNo());
            String bankName = UBankInfoQry.getBankNameByBankCode(reqData.getBankCode());
            acctConsignTradeData.setBankAcctName(bankName);
        }

        acctConsignTradeData.setConsignMode("1");
        acctConsignTradeData.setPriority("0");
        acctConsignTradeData.setActTag("1");
        acctConsignTradeData.setAssistantTag("0");
        acctConsignTradeData.setPaymentId("4");
        acctConsignTradeData.setPayFeeModeCode("4");
        acctConsignTradeData.setStartCycleId(startCycleId);
        acctConsignTradeData.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTradeData.setInstId(SeqMgr.getInstId());
        acctConsignTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        acctConsignTradeData.setRemark(reqData.getRemark());
        btd.add(btd.getRD().getUca().getSerialNumber(), acctConsignTradeData);

    }

    /**
     * 生成修改账户资料台账
     * 
     * @param btd
     * @throws Exception
     */
    private void createAccountTrade(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        String newSerNum = reqData.getNewSeriNum();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(newSerNum);
        // 帐户资料
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID"));
        AccountTradeData acctTradeData = new AccountTradeData();
        acctTradeData.setAcctId(reqData.getNewAcctId());// 生成新的账户ID
        acctTradeData.setPayName(reqData.getPayName());
        acctTradeData.setPayModeCode(acctInfo.getString("PAY_MODE_CODE"));
        acctTradeData.setAcctDiffCode("0");
        acctTradeData.setCustId(reqData.getUca().getCustId());
        acctTradeData.setBankCode(reqData.getBankCode());
        if (StringUtils.equals("0", reqData.getPayModeCode()))// 现金账户
        {
            acctTradeData.setBankAcctNo("");
            acctTradeData.setContractNo("");
        }
        else
        {
            acctTradeData.setBankAcctNo(reqData.getBankAcctNo());
            // 选择托收，则自动生成代扣号
            if (StringUtils.isBlank(acctTradeData.getContractNo()) && StringUtils.equals("1", reqData.getPayModeCode()) && acctTradeData.getBankCode().length() > 3)
            {
                acctTradeData.setContractNo(PersonUtil.createContractNo(acctTradeData.getAcctId(), acctTradeData.getBankCode()));
            }
        }
        acctTradeData.setRsrvStr6(reqData.getBankAgreementNo());// 银行协议号
        acctTradeData.setAcctTag("1");// 分账
        acctTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        acctTradeData.setCityCode(reqData.getUca().getCustomer().getCityCode());// 考虑和客户的一致
        acctTradeData.setScoreValue("0");
        acctTradeData.setAcctDiffCode("0");
        //acctTradeData.setNetTypeCode("12");
        acctTradeData.setNetTypeCode("00");
        acctTradeData.setCreditClassId("-1");
        acctTradeData.setBasicCreditValue("0");
        acctTradeData.setCreditValue("0");
        acctTradeData.setDepositPriorRuleId("0");
        acctTradeData.setOpenDate(reqData.getAcceptTime());
        acctTradeData.setRemoveTag("0");
        acctTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(reqData.getUca().getSerialNumber(), acctTradeData);
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        if ("1".equals(reqData.getChgAcctType()))
        {
            this.dealBaseParam(btd);// 处理公共参数(生成新的账户ID与计算新账户的开始账期)
            acctDayDeal(btd);// 分散账期
            createAccountTrade(btd);// 账户台账登记
        }
        else
        {
        	reqData.setStartCycleId(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD).replace("-", ""));
            reqData.setNewAcctId(reqData.getChangeAcctId());
        }
        createTradePayrelation(btd);// 付费关系台账登记
        createMainTradeInfo(btd);// 主台账登记

        if (!StringUtils.equals("0", reqData.getPayModeCode()) && !StringUtils.isBlank(reqData.getBankCode()) && !StringUtils.isBlank(reqData.getBankAcctNo()) && !StringUtils.isBlank(reqData.getSuperBankCode()))
        {
            createAccountConsignTrade(btd);// 台帐帐户托收信息子表登记
        }

    }

    /**
     *主台账扩展字段赋值
     * 
     * @param btd
     * @throws Exception
     */
    public void createMainTradeInfo(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();

        btd.getMainTradeData().setAcctId(reqData.getNewAcctId());
        btd.getMainTradeData().setRsrvStr1(reqData.getUca().getUserOriginalData().getAccount().getAcctId());// 原号码acct_id，置主台帐表
        btd.getMainTradeData().setRsrvStr2(reqData.getStartCycleId());// 起始帐期，置主台帐表
        btd.getMainTradeData().setRsrvStr3(btd.getRD().getUca().getUserId());
        btd.getMainTradeData().setRsrvStr6(reqData.getBankAgreementNo());// 银行协议号
        //btd.getMainTradeData().setNetTypeCode("12");
        btd.getMainTradeData().setNetTypeCode("00");
        btd.getMainTradeData().setSubscribeType("300");
        btd.getMainTradeData().setPfType("300");
        String chg_acct_type = reqData.getChgAcctType();
        if ("1".equals(chg_acct_type))
        {
            btd.getMainTradeData().setRsrvStr9("S");
        }
        else if ("0".equals(chg_acct_type))
        {
            btd.getMainTradeData().setRsrvStr9("U");
        }
    }

    /**
     * 生成付费关系台账
     * 
     * @param btd
     * @param baseParams
     * @throws Exception
     */
    public void createTradePayrelation(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        if (PersonConst.PAYRELANORCHG_CHANGE_ALL.equals(reqData.getChangeAll()))
        {// 将原账户下所有用户转移
            String oldAcctId = reqData.getUca().getUserOriginalData().getAccount().getAcctId();
            IDataset dataset = PayRelaInfoQry.getAcctPayReltionNow(oldAcctId, "1", "1", null);
            if (IDataUtil.isNotEmpty(dataset))
            {
                for (int i = 0, size = dataset.size(); i < size; i++)
                {
                    IData data = dataset.getData(i);
                    String userId = data.getString("USER_ID");
                    dealPayRelationTrade(btd, userId, "");
                }
            }
        }
        else
        {
            dealPayRelationTrade(btd, reqData.getUca().getUserId(), "");// 当前 用户
        }
    }

    private void dealBaseParam(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        reqData.setNewAcctId(SeqMgr.getAcctId());// 生成新的账户ID
        String startCycleId = "";// 新账户的开始账期
        if (!StringUtils.equals("1", reqData.getUca().getAcctDay()) && StringUtils.equals("1", reqData.getPayModeCode()))// 用户账期日不为自然月且为托收时
        {
            // 取用户下个账期日的第一天
            startCycleId = SysDateMgr.getFirstDayNextAcct(reqData.getAcceptTime(), reqData.getUca().getAcctDay(), reqData.getUca().getFirstDate());
        }
        else
        {
            // 其它情况取当前账期的第一天
            startCycleId = SysDateMgr.getFirstCycleDayThisAcct(reqData.getAcceptTime(), reqData.getUca().getAcctDay(), reqData.getUca().getFirstDate(), reqData.getUca().getAcctDayStartDate());
        }
//        reqData.setStartCycleId(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD).replace("-", ""));
        startCycleId = StringUtils.replace(startCycleId, "-", "");
        startCycleId = StringUtils.substring(startCycleId, 0, 8);
        reqData.setStartCycleId(startCycleId);
    }

    /**
     * 付费关系台账处理
     * 
     * @param btd
     * @param userId
     * @param remark
     *            备注
     * @throws Exception
     */
    private void dealPayRelationTrade(BusiTradeData btd, String userId, String remark) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
//        String newStartCycleId = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD);
        String newStartCycleId = reqData.getStartCycleId();
        boolean flag = true;

        // 查询用户还有效的付费关系信息
        IDataset userValidPayRela = PayRelaInfoQry.qryValidPayRelationByUserId(userId, "1", "1");
        if (IDataUtil.isNotEmpty(userValidPayRela))
        {
            for (int i = 0, size = userValidPayRela.size(); i < size; i++)
            {
                String tempStartCycleId = userValidPayRela.getData(i).getString("START_CYCLE_ID");
                // 新的开始时间大于原来的开始时间，则是正在生效的，需要终止
                if (SysDateMgr.getTimeDiff(tempStartCycleId, newStartCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD) >= 0)
                {
                    PayRelationTradeData delPayRelTrade = new PayRelationTradeData(userValidPayRela.getData(i));
                    delPayRelTrade.setEndCycleId(StringUtils.replace(SysDateMgr.addDays(SysDateMgr.decodeTimestamp(newStartCycleId, "yyyy-MM-dd"), -1), "-", ""));// 结束时间为新的开始时间减1天
                    delPayRelTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, delPayRelTrade);
                }
                else
                // 当前用户还未生效的付费关系
                {
                    PayRelationTradeData modPayRelTrade = new PayRelationTradeData(userValidPayRela.getData(i));
                    modPayRelTrade.setAcctId(reqData.getNewAcctId());// 改成新的账户ID--很奇怪，需要询问一下现场
                    modPayRelTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    btd.add(serialNumber, modPayRelTrade);
                }
            }
        }

        if (flag)// 用户不存在还未生效的付费关系 则新增
        {
	        // 用新的acctId增加新的付费关系
	        PayRelationTradeData newPayRelTrade = new PayRelationTradeData();
	        newPayRelTrade.setAcctId(reqData.getNewAcctId());
	        newPayRelTrade.setUserId(userId);
	//        newPayRelTrade.setStartCycleId(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD).replace("-", ""));
	        newPayRelTrade.setStartCycleId(reqData.getStartCycleId());
	        newPayRelTrade.setEndCycleId(SysDateMgr.getEndCycle20501231());
	        newPayRelTrade.setPayitemCode("-1");
	        newPayRelTrade.setAcctPriority("0");
	        newPayRelTrade.setUserPriority("0");
	        newPayRelTrade.setAddupMethod("0");
	        newPayRelTrade.setAddupMonths("0");
	        newPayRelTrade.setBindType("1");
	        newPayRelTrade.setActTag("1");
	        newPayRelTrade.setDefaultTag("1");
	        newPayRelTrade.setLimit("0");
	        newPayRelTrade.setLimitType("0");
	        newPayRelTrade.setComplementTag("0");
	        newPayRelTrade.setInstId(SeqMgr.getInstId());
	        newPayRelTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        if (StringUtils.isNotBlank(remark))
	        {
	            newPayRelTrade.setRemark(remark);
	        }
	        btd.add(serialNumber, newPayRelTrade);
        }
    }
}
