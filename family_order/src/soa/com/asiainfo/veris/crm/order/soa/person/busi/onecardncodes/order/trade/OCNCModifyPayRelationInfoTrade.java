
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.requestdata.ModifyPayRelationInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;

public class OCNCModifyPayRelationInfoTrade extends BaseTrade implements ITrade
{

    /**
     * 
     * 
     * @param btd
     * @throws Exception
     */
    public void acctDayDeal(BusiTradeData btd) throws Exception
    {
        ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
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
        ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
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
            startCycleId = StringUtils.replace(SysDateMgr.getDateNextMonthFirstDay(firstDayNextAcct).substring(0, 8), "-", "");
        }
//        acctConsignTradeData.setAcctId(reqData.getNewAcctId());// 新账户ID
        if("1".equals(reqData.getChgAcctType())){
        	acctConsignTradeData.setAcctId(reqData.getUca().getAcctId());//取消绑定时，构造托收台账取副号的ACCT_ID。
        }else{
        	acctConsignTradeData.setAcctId(reqData.getNewAcctId());
        }
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
     * 如果是分帐，则插入帐户子台帐表
     * 
     * @param btd
     * @throws Exception
     */
    private void createAccountTrade(BusiTradeData btd) throws Exception
    {
        ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
        AccountTradeData acctTradeData = new AccountTradeData();
        //0 绑定付费  副号码pay表生成一条acctID 为主号码的信息 删除原来的  
        //分账则生成一条新的数据
        if("1".equals(reqData.getChgAcctType())){
        	 acctTradeData.setAcctId(reqData.getNewAcctId());// 生成新的账户ID
             acctTradeData.setCustId(reqData.getUca().getCustId());
             acctTradeData.setPayModeCode(reqData.getPayModeCode());
             acctTradeData.setPayName(reqData.getPayName());
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
             acctTradeData.setNetTypeCode(reqData.getUca().getUser().getNetTypeCode());
             acctTradeData.setCreditClassId("-1");
             acctTradeData.setBasicCreditValue("0");
             acctTradeData.setCreditValue("0");
             acctTradeData.setDepositPriorRuleId("0");
             acctTradeData.setOpenDate(reqData.getAcceptTime());
             acctTradeData.setRemoveTag("0");
             acctTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
             btd.add(reqData.getUca().getSerialNumber(), acctTradeData);
        }
        if("0".equals(reqData.getChgAcctType())){
            AccountTradeData acctTradeDataDel = reqData.getUca().getAccount();
            acctTradeDataDel.setModifyTag(BofConst.MODIFY_TAG_DEL);
            acctTradeDataDel.setRemoveTag("1");
            acctTradeDataDel.setRemoveDate(reqData.getAcceptTime());
            btd.add(reqData.getUca().getSerialNumber(), acctTradeDataDel);
        }
       
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
        
        String osn = reqData.getOtherSn();
        IData userinfo = UcaInfoQry.qryUserInfoBySn(osn);
        IData acctinfo = UcaInfoQry.qryAcctInfoByUserId(userinfo.getString("USER_ID"));
        
        this.dealBaseParam(btd);// 处理公共参数(生成新的账户ID与计算新账户的开始账期)
        acctDayDeal(btd);// 分散账期
        createAccountTrade(btd);// 账户台账登记
        createTradePayrelation(btd,acctinfo);
//        dealPayRelationTrade(btd, reqData.getUca().getUserId(), "");// 付费关系台账登记
        createDiscntTradeData(btd,userinfo);//优惠
        createMainTradeInfo(btd);// 主台账登记
        reqData.setNewAcctId(acctinfo.getString("ACCT_ID"));
        
        if (!StringUtils.equals("0", reqData.getPayModeCode()) && !StringUtils.isBlank(reqData.getBankCode()) && !StringUtils.isBlank(reqData.getBankAcctNo()) && !StringUtils.isBlank(reqData.getSuperBankCode()))
        {
            createAccountConsignTrade(btd);// 台帐帐户托收信息子表登记
        }

    }

    /**
     * @Function: createDiscntTradeData()
     * @Description: 优惠子台帐
     * @param:
     * @return：
     */
    private void createDiscntTradeData(BusiTradeData btd,IData userinfo) throws Exception
    {
    	ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();

    	DiscntTradeData discntTradeData = new DiscntTradeData();
    	//新增 
    	if("0".equals(reqData.getChgAcctType())){
    		discntTradeData.setUserId(userinfo.getString("USER_ID"));
            discntTradeData.setUserIdA("-1");
            discntTradeData.setPackageId("-1");
            discntTradeData.setProductId("-1");
            discntTradeData.setSpecTag("2");
            discntTradeData.setRelationTypeCode("30");
            discntTradeData.setEndDate(SysDateMgr.END_TIME_FOREVER);
            discntTradeData.setStartDate(reqData.getAcceptTime());
            discntTradeData.setModifyTag("0");
            discntTradeData.setInstId(SeqMgr.getInstId());
            discntTradeData.setElementId("5903");
            discntTradeData.setElementType("D");
            btd.add(reqData.getUca().getSerialNumber(), discntTradeData);	
    	}
     	//删除
    	if("1".equals(reqData.getChgAcctType())){

//            UcaData uca = btd.getRD().getUca();
//            List<DiscntTradeData> disDatas = uca.getUserDiscntByPidPkid("-1", "-1");//uca.getUserDiscntByDiscntId("5903");//主卡userid挂848优惠
//            								 
//    	        for(DiscntTradeData disData:disDatas){
//	        		DiscntTradeData disTrade = disData.clone();
//    	        	if("5903".equals(disTrade.getDiscntCode())){
//        	        	disTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
//        	        	disTrade.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
//        	        	disTrade.setInstId(disTrade.getInstId());
//        	        	btd.add(reqData.getUca().getSerialNumber(), disTrade);	
//    	        	}
    	        	
    	        	 IDataset discnt = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userinfo.getString("USER_ID"), "5903", getTradeEparchyCode());
    	             if(IDataUtil.isNotEmpty(discnt)){
    	            	 DiscntTradeData disData =new DiscntTradeData(discnt.getData(0));
        	             disData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        	             disData.setEndDate(SysDateMgr.getLastSecond(btd.getRD().getAcceptTime()));
        	             btd.add(reqData.getUca().getSerialNumber(), disData);	 
    	             }
    	            
    	       
//    	        }
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
    	
    	
        ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
    	IDataset oUserResInfos =UserResInfoQry.getUserResInfosByUserIdResTypeCode(reqData.getUca().getUserId(), "1");
    	IData user = UcaInfoQry.qryUserInfoBySn(reqData.getOtherSn());
    	IDataset mUserResInfos =UserResInfoQry.getUserResInfosByUserIdResTypeCode(user.getString("USER_ID",""), "1");

    	String simCardNoO ="";
    	String simCardNoM ="";

    	if(IDataUtil.isNotEmpty(oUserResInfos)){
    		simCardNoO = oUserResInfos.getData(0).getString("RES_CODE","");
    	}
    	if(IDataUtil.isNotEmpty(mUserResInfos)){
    		simCardNoM = mUserResInfos.getData(0).getString("RES_CODE","");
    	}
        
        btd.getMainTradeData().setAcctId(reqData.getNewAcctId());
        btd.getMainTradeData().setCustId(reqData.getUca().getCustId());
        btd.getMainTradeData().setRsrvStr1(reqData.getUca().getAccount().getRsrvStr1());
        btd.getMainTradeData().setRsrvStr2(reqData.getUca().getAccount().getRsrvStr2());
        btd.getMainTradeData().setRsrvStr3(reqData.getUca().getAccount().getRsrvStr3());
        btd.getMainTradeData().setRsrvStr4(reqData.getOtherSn());//主号码
        btd.getMainTradeData().setRsrvStr5(simCardNoO);//副号码SIM卡
        btd.getMainTradeData().setRsrvStr6(reqData.getChgAcctType());//变更类型
        btd.getMainTradeData().setRsrvStr7(simCardNoM);//主SIM卡
//        btd.getMainTradeData().setRsrvStr6(reqData.getBankAgreementNo());// 银行协议号
    }

    /**
     * 生成付费关系台账
     * 
     * @param btd
     * @param baseParams
     * @throws Exception
     */
    public void createTradePayrelation(BusiTradeData btd,IData acctinfo) throws Exception
    {
    	ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
        dealPayRelationTrade(btd, reqData.getUca().getUserId(), "",acctinfo);// 当前 用户
    }

    private void dealBaseParam(BusiTradeData btd) throws Exception
    {
        ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
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
    private void dealPayRelationTrade(BusiTradeData btd, String userId, String remark,IData acctinfo) throws Exception
    {
    	ModifyPayRelationInfoReqData reqData = (ModifyPayRelationInfoReqData) btd.getRD();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String newStartCycleId = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD);

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

        // 用新的acctId增加新的付费关系
        //如果是绑定关系 则用主号码的ACCTID作为付费   如果是取消绑定则是用一个新增的acctid作为默认付费
        PayRelationTradeData newPayRelTrade = new PayRelationTradeData();
        //0 绑定付费 1 是取消绑定
        if("0".equals(reqData.getChgAcctType())){
            newPayRelTrade.setAcctId(acctinfo.getString("ACCT_ID"));//主号码的ACCTID
        }
        if("1".equals(reqData.getChgAcctType())){
            newPayRelTrade.setAcctId(reqData.getNewAcctId());//新增的ACCTID
        }
        newPayRelTrade.setUserId(userId);
        newPayRelTrade.setStartCycleId(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD).replace("-", ""));
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
