
package com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctConsignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeacctinfo.order.requestdata.ModifyAcctInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

/**
 * 账户信息修改trade写台账类
 * 
 * @author liutt
 */
public class ModifyAcctInfoTrade extends BaseTrade implements ITrade
{

    /**
     * 设置新增业务台帐帐户托收信息子表记录
     * 
     * @param btd
     * @throws Exception
     */
    private void createAccountConsignTradeInfo(BusiTradeData btd, String startCycleId) throws Exception
    {
        ModifyAcctInfoReqData reqData = (ModifyAcctInfoReqData) btd.getRD();
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
            acctConsignTradeData.setRsrvStr5("");

        }
        else
        {
            acctConsignTradeData.setSuperBankCode(reqData.getSuperbankCode());
            acctConsignTradeData.setBankCode(reqData.getBankCode());
            acctConsignTradeData.setBankAcctNo(reqData.getBankAcctNo());
            String bankName = UBankInfoQry.getBankNameByBankCode(reqData.getBankCode());
            acctConsignTradeData.setBankAcctName(bankName);
            acctConsignTradeData.setRsrvStr5(reqData.getRsrvStr5());	//欠费周期
        }

        acctConsignTradeData.setInstId(SeqMgr.getInstId());
        acctConsignTradeData.setContractId(reqData.getContractNo());
        acctConsignTradeData.setConsignMode("1");
        acctConsignTradeData.setPriority("0");
        acctConsignTradeData.setActTag("1");
        acctConsignTradeData.setStartCycleId(startCycleId);
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
        ModifyAcctInfoReqData reqData = (ModifyAcctInfoReqData) btd.getRD();
        AccountTradeData accountTradeData = reqData.getUca().getAccount().clone();
        accountTradeData.setEparchyCode(CSBizBean.getUserEparchyCode());
        accountTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        accountTradeData.setScoreValue("0");
        accountTradeData.setBasicCreditValue("0");
        accountTradeData.setCreditValue("0");
        accountTradeData.setRemoveTag("0");
        accountTradeData.setPayName(reqData.getPayName());
        accountTradeData.setBankCode(reqData.getBankCode());
        //accountTradeData.setRsrvStr4(reqData.getRsrvStr4());	//欠费周期
        accountTradeData.setRsrvStr6(reqData.getRsrvStr6());
        accountTradeData.setRsrvStr3(reqData.getPostCode());
        accountTradeData.setRsrvStr10(reqData.getPostAddress());
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
        ModifyAcctInfoReqData reqData = (ModifyAcctInfoReqData) btd.getRD();
        AccountTradeData oldAccountTradeData = reqData.getUca().getUserOriginalData().getAccount();
       
        String startCycleId = reqData.getAcceptTime().substring(0, 4) + reqData.getAcceptTime().substring(5, 7);
        // 非自然日，开始账期为下下个月
        if (!StringUtils.equals("1", reqData.getUca().getAcctDay())) {
            startCycleId = SysDateMgr.getAddMonthsNowday(2, startCycleId);
        }
        
        String oldRsrvStr5 = "";
        String newRsrvStr5 = reqData.getRsrvStr5() == null ? "" : reqData.getRsrvStr5();
        if ( "0".equals(reqData.getPayModeCode()) ){
        	newRsrvStr5 = "";
        }else{
        	
            IDataset ids = AcctConsignInfoQry.getConsignInfoByAcctIdAndCycle(reqData.getUca().getAccount().getAcctId(), startCycleId);
            if( IDataUtil.isNotEmpty(ids) ){
            	AcctConsignTradeData acctConsign = new AcctConsignTradeData(ids.getData(0));
            	oldRsrvStr5 = acctConsign.getRsrvStr5() == null ? "" : acctConsign.getRsrvStr5();
            	newRsrvStr5 = reqData.getRsrvStr5() == null ? oldRsrvStr5 : reqData.getRsrvStr5();	//考虑前台赋空值
            	reqData.setRsrvStr5(newRsrvStr5);
            }
        }
        
        String oldBankCode = oldAccountTradeData.getBankCode() == null ? "" : oldAccountTradeData.getBankCode();
        String oldBankAcctNo = oldAccountTradeData.getBankAcctNo() == null ? "" : oldAccountTradeData.getBankAcctNo();
        String oldContractNo = oldAccountTradeData.getContractNo() == null ? "" : oldAccountTradeData.getContractNo();
        
        if( !StringUtils.equals(oldAccountTradeData.getPayModeCode(), reqData.getPayModeCode()) 
         || !StringUtils.equals(oldBankCode, reqData.getBankCode()) 
         || !StringUtils.equals(oldBankAcctNo, reqData.getBankAcctNo())
         || !StringUtils.equals(oldContractNo, reqData.getContractNo()) 
         || !StringUtils.equals(oldRsrvStr5, newRsrvStr5) ) //增加欠费周期判断
        {
            createAccountConsignTradeInfo(btd, startCycleId);
            delAccountConsignTradeInfo(btd, startCycleId);
            createDecodeCreditTradeInfo(btd);
            // 分散账期
            if (!StringUtils.equals("1", reqData.getUca().getAcctDay()))
            {// 非自然月账期变更为自然月账期
                // 处理因账期变更,归属某账户下所有用户资料台账
                btd.addChangeAcctDayData(reqData.getUca().getUserId(), "1");// 放到btd中，待通用action统一处理
            }
        }
        
          
        String addRemark = reqData.getmainTradeRemark();         
        if (addRemark != null && addRemark.length() > 0) {
            String remark = btd.getMainTradeData().getRemark();
            if (remark != null && !remark.trim().equals("")) {
                String allRemark = addRemark + remark;
                int allLength = allRemark.getBytes().length;
                String insertRemark = "";
                if (allLength > 148) {
                    int i = 1;
                    while (allLength > 148) {
                        allLength = allRemark.substring(0, allRemark.length() - i).getBytes().length;
                        insertRemark = allRemark.substring(0, allRemark.length() - i);
                        i++;
                    }
                    btd.getMainTradeData().setRemark(insertRemark);
                } else {
                    btd.getMainTradeData().setRemark(allRemark);
                }

            } else {
                btd.getMainTradeData().setRemark(addRemark);
            }
        }
        
     
        
    }

    /**
     * 创建新增信用度台账记录
     * 
     * @param btd
     * @throws Exception
     */
    private void createDecodeCreditTradeInfo(BusiTradeData btd) throws Exception
    {
        ModifyAcctInfoReqData reqData = (ModifyAcctInfoReqData) btd.getRD();
        // 通过老的付费方式获取变化的信用度值
        IDataset olddataset = CommparaInfoQry.getCommparaCode1("CSM", "889", btd.getRD().getUca().getUserOriginalData().getAccount().getPayModeCode(), CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(olddataset))
        {
            CreditTradeData creditTradeData = new CreditTradeData();
            int creditValue = -Integer.parseInt(olddataset.getData(0).getString("PARA_CODE1"));
            creditTradeData.setUserId(btd.getRD().getUca().getUserId());
            creditTradeData.setCreditValue("" + creditValue);
            creditTradeData.setCreditMode("addCredit");// 信用度类型标识'addCredit-增加扣减信用度;callCredit-触发信控';
            creditTradeData.setCreditGiftMonths("1");// 赠送信用度月份数
            creditTradeData.setStartDate(reqData.getAcceptTime());
            creditTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            creditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), creditTradeData);
        }
        // 通过新的付费方式获取变化的信用度值
        IDataset newdataset = CommparaInfoQry.getCommparaCode1("CSM", "889", reqData.getPayModeCode(), CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(newdataset))
        {
            CreditTradeData creditTradeData = new CreditTradeData();
            int score = Integer.parseInt(newdataset.getData(0).getString("PARA_CODE1"));
            creditTradeData.setUserId(btd.getRD().getUca().getUserId());
            creditTradeData.setCreditValue("" + score);
            creditTradeData.setCreditMode("addCredit");// 信用度类型标识'addCredit-增加扣减信用度;callCredit-触发信控';
            creditTradeData.setCreditGiftMonths("1");// 赠送信用度月份数
            creditTradeData.setStartDate(reqData.getAcceptTime());
            creditTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            creditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), creditTradeData);
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
        ModifyAcctInfoReqData reqData = (ModifyAcctInfoReqData) btd.getRD();
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
