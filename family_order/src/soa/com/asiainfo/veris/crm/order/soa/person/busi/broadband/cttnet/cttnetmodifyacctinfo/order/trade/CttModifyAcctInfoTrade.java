
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyacctinfo.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctConsignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetmodifyacctinfo.order.requestdata.CttModifyAcctInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

/**
 * 账户信息修改trade写台账类
 */
public class CttModifyAcctInfoTrade extends BaseTrade implements ITrade
{

    /**
     * 设置新增业务台帐帐户托收信息子表记录
     * 
     * @param btd
     * @throws Exception
     */
    private void createAccountConsignTradeInfo(BusiTradeData btd) throws Exception
    {
        CttModifyAcctInfoReqData reqData = (CttModifyAcctInfoReqData) btd.getRD();
        AcctConsignTradeData acctConsignTradeData = new AcctConsignTradeData();

        acctConsignTradeData.setAcctId(reqData.getUca().getAccount().getAcctId());
        acctConsignTradeData.setPayModeCode(reqData.getPayModeCode());
        acctConsignTradeData.setEparchyCode(CSBizBean.getUserEparchyCode());
        acctConsignTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
        if ("0".equals(reqData.getPayModeCode()))
        {
            acctConsignTradeData.setSuperBankCode("-1");
            acctConsignTradeData.setBankCode("-1");
            acctConsignTradeData.setBankAcctNo("-1");
            acctConsignTradeData.setBankAcctName("-1");

        }
        else
        {
            acctConsignTradeData.setSuperBankCode(reqData.getSuperbankCode());
            acctConsignTradeData.setBankCode(reqData.getBankCode());
            acctConsignTradeData.setBankAcctNo(reqData.getBankAcctNo());
            String bankName = BankInfoQry.getCttBankNameByBankCode(reqData.getBankCode());
            acctConsignTradeData.setBankAcctName(bankName);
        }

        acctConsignTradeData.setInstId(SeqMgr.getInstId());
        acctConsignTradeData.setContractId(reqData.getContractNo());
        acctConsignTradeData.setConsignMode("1");
        acctConsignTradeData.setPriority("0");
        acctConsignTradeData.setActTag("1");
        acctConsignTradeData.setStartCycleId(reqData.getAcceptTime().substring(0, 4) + reqData.getAcceptTime().substring(5, 7));
        acctConsignTradeData.setAssistantTag("0");
        acctConsignTradeData.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        acctConsignTradeData.setPaymentId("4");
        acctConsignTradeData.setPayFeeModeCode("4");
        acctConsignTradeData.setRemark(reqData.getRemark());
        btd.add(btd.getRD().getUca().getSerialNumber(), acctConsignTradeData);
    }

    /**
     * 设置账户台帐表的数据
     * 
     * @param btd
     * @throws Exception
     */
    private void createAccountTradeInfo(BusiTradeData btd) throws Exception
    {
        CttModifyAcctInfoReqData reqData = (CttModifyAcctInfoReqData) btd.getRD();
        AccountTradeData accountTradeData = reqData.getUca().getAccount().clone();
        accountTradeData.setEparchyCode(CSBizBean.getUserEparchyCode());
        accountTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        accountTradeData.setScoreValue("0");
        accountTradeData.setBasicCreditValue("0");
        accountTradeData.setCreditValue("0");
        accountTradeData.setRemoveTag("0");
        accountTradeData.setPayName(reqData.getPayName());
        accountTradeData.setBankCode(reqData.getBankCode());
        accountTradeData.setRsrvStr3(reqData.getRsrvStr3());// 账单信息
        accountTradeData.setRsrvStr6(reqData.getRsrvStr6());
        accountTradeData.setRemark(reqData.getRemark());
        String payModeCode = reqData.getPayModeCode();
        accountTradeData.setPayModeCode(payModeCode);
        if (payModeCode.equals("0"))
        { // 现金账户
            accountTradeData.setBankAcctNo("");
            accountTradeData.setContractNo("");
        }
        else
        {
            accountTradeData.setBankAcctNo(reqData.getBankAcctNo());
            accountTradeData.setContractNo(reqData.getContractNo());
            // 选择托收，则自动生成代扣号
            if (StringUtils.isBlank(reqData.getContractNo()) && StringUtils.equals("1", payModeCode) && reqData.getBankCode().length() > 3)
            {
                accountTradeData.setContractNo(PersonUtil.createContractNo(accountTradeData.getAcctId(), accountTradeData.getBankCode()));
            }
        }
        accountTradeData.setCityCode(btd.getRD().getUca().getUser().getCityCode());
        btd.add(reqData.getUca().getUser().getSerialNumber(), accountTradeData);

    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createAccountTradeInfo(btd);
        CttModifyAcctInfoReqData reqData = (CttModifyAcctInfoReqData) btd.getRD();
        AccountTradeData oldAccountTradeData = reqData.getUca().getUserOriginalData().getAccount();
        String oldBankCode = oldAccountTradeData.getBankCode() == null ? "" : oldAccountTradeData.getBankCode();
        String oldBankAcctNo = oldAccountTradeData.getBankAcctNo() == null ? "" : oldAccountTradeData.getBankAcctNo();
        String oldContractNo = oldAccountTradeData.getContractNo() == null ? "" : oldAccountTradeData.getContractNo();

        if (!oldAccountTradeData.getPayModeCode().equals(reqData.getPayModeCode()) || !oldBankCode.equals(reqData.getBankCode()) || !oldBankAcctNo.equals(reqData.getBankAcctNo()) || !oldContractNo.equals(reqData.getContractNo()))
        {
            createAccountConsignTradeInfo(btd);
            String startCycleId = reqData.getAcceptTime().substring(0, 4) + reqData.getAcceptTime().substring(5, 7);
            delAccountConsignTradeInfo(btd, startCycleId);
        }
    }
    
    /**
     * 删除业务台账帐户托收表记录
     * 
     * @param btd
     * @param startCycleId
     * @throws Exception
     */
    private void delAccountConsignTradeInfo(BusiTradeData btd, String startCycleId) throws Exception
    {
    	CttModifyAcctInfoReqData reqData = (CttModifyAcctInfoReqData) btd.getRD();
        IDataset ids = AcctConsignInfoQry.getConsignInfoByAcctIdAndCycle(reqData.getUca().getAccount().getAcctId(), startCycleId);
        for (int i = 0, size = ids.size(); i < size; i++)
        {
            AcctConsignTradeData acctConsign = new AcctConsignTradeData(ids.getData(i));
            acctConsign.setModifyTag(BofConst.MODIFY_TAG_DEL);
            acctConsign.setEndCycleId(SysDateMgr.getAddMonthsNowday(-1, startCycleId));
            btd.add(btd.getRD().getUca().getSerialNumber(), acctConsign);
        }

    }

}
