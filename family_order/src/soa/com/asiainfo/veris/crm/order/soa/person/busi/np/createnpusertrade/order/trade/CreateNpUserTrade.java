
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.trade;

import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UBankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.NpTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PostTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.CreateNpUserReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.PostInfo;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.ResInfo;

public class CreateNpUserTrade extends BaseTrade implements ITrade
{
    private void createAcctConsignTradeData(BusiTradeData btd) throws Exception
    {

        AcctConsignTradeData acctConsignTD = new AcctConsignTradeData();
        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        acctConsignTD.setAcctId(reqData.getUca().getAcctId());
        acctConsignTD.setPayModeCode(reqData.getUca().getAccount().getPayModeCode()); // 帐户付费类型：0-现金，1-托收，2-代扣

        acctConsignTD.setEparchyCode(BizRoute.getRouteId());
        acctConsignTD.setCityCode(reqData.getUca().getAccount().getCityCode());
        acctConsignTD.setSuperBankCode(reqData.getSuperBankCode());
        acctConsignTD.setBankCode(reqData.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctNo(reqData.getUca().getAccount().getBankAcctNo());

        String bankName = UBankInfoQry.getBankNameByBankCode(reqData.getUca().getAccount().getBankCode());
        acctConsignTD.setBankAcctName(bankName);
        acctConsignTD.setConsignMode("1"); // 托收方式：默认为1
        acctConsignTD.setPaymentId("4"); // 储值方式：默认为4
        acctConsignTD.setPayFeeModeCode("4");
        acctConsignTD.setActTag("1");
        acctConsignTD.setInstId(SeqMgr.getInstId());
        acctConsignTD.setStartCycleId(SysDateMgr.getNowCyc());
        acctConsignTD.setEndCycleId(SysDateMgr.getEndCycle205012());
        acctConsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(serialNumber, acctConsignTD);
    }

    public void createAcctTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();

        AccountTradeData atd = reqData.getUca().getAccount();
        atd.setAcctDiffCode("0");
        atd.setNetTypeCode(reqData.getUca().getUser().getNetTypeCode());
        atd.setScoreValue("0");
        atd.setBasicCreditValue("0");
        atd.setCreditValue("0");
        atd.setDebutyUserId(reqData.getUca().getUserId());
        atd.setDebutyCode(reqData.getUca().getSerialNumber());
        atd.setContractNo(reqData.getUca().getAccount().getContractNo());
        atd.setDepositPriorRuleId(reqData.getUca().getAccount().getDepositPriorRuleId());
        atd.setItemPriorRuleId(reqData.getUca().getAccount().getItemPriorRuleId());
        atd.setOpenDate(reqData.getUca().getUser().getOpenDate());
        atd.setRemoveTag("0");
        atd.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(serialNumber, atd);
    }

    /**
     * @Function: createAttrTradeData
     * @Description: 处理USIM卡的OPC值
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月6日 下午4:45:31
     */
    private void createAttrTradeData(BusiTradeData btd) throws Exception
    {
        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        List<ResInfo> resInfos = reqData.getResInfos();
        for (ResInfo resInfo : resInfos)
        {
            if ("1".equals(resInfo.getResTypeCode()) && StringUtils.isNotBlank(resInfo.getOpc()))
            {
                AttrTradeData atd = new AttrTradeData();
                atd.setAttrCode("OPC_VALUE");
                atd.setAttrValue(resInfo.getOpc());
                atd.setInstType("R");
                atd.setInstId(SeqMgr.getInstId());
                atd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                atd.setStartDate(reqData.getAcceptTime());
                atd.setEndDate(SysDateMgr.END_DATE_FOREVER);
                atd.setRelaInstId(SeqMgr.getInstId());
                atd.setUserId(reqData.getUca().getUserId());
                btd.add(reqData.getUca().getSerialNumber(), atd);
            }
        }

    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();

        createUserTradeData(btd);

        createCustPersonTradeData(btd);

        createCustomerTradeData(btd);

        createAcctTradeData(btd);

        createNpTradeData(btd);

        createPayrelationTradeData(btd);

        createResTradeData(btd);

        createProductTradeData(btd);

        createCreditTradeData(btd);

        // 如果是实名制开户，需要拼用户其他表
        if ("1".equals(reqData.getIsRealName()))
        {
            createOtherTradeData(btd);//

        }

        ProductModuleCreator.createProductModuleTradeData(reqData.getPmds(), btd);

        btd.addOpenUserAcctDayData(reqData.getUca().getUserId(), reqData.getUca().getAcctDay());

        btd.addOpenAccountAcctDayData(reqData.getUca().getAcctId(), reqData.getUca().getAcctDay());

        if ("1".equals(reqData.getUca().getAccount().getPayModeCode()))
        {
            createAcctConsignTradeData(btd);
        }

        createPostTradeData(btd);

        if (StringUtils.isNotBlank(reqData.getInvoiceNo()))
        {
            btd.getMainTradeData().setInvoiceNo(reqData.getInvoiceNo());
        }

        createAttrTradeData(btd);
        
        this.createCustPersonOtherData(btd);

        btd.getMainTradeData().setRsrvStr2(CSBizBean.getVisit().getStaffName());
        btd.getMainTradeData().setExecTime(SysDateMgr.END_DATE_FOREVER);
    }

    /**
     * @Function: createCreditTradeData
     * @Description: 信用度子表
     * @param btd
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月15日 上午11:10:01
     */
    public void createCreditTradeData(BusiTradeData btd) throws Exception
    {
        CreateNpUserReqData rd = (CreateNpUserReqData) btd.getRD();
        String serialNumber = rd.getUca().getUser().getSerialNumber();

        CreditTradeData ctd = new CreditTradeData();
        ctd.setUserId(rd.getUca().getUserId());
        ctd.setCreditValue("2000");
        ctd.setCreditMode("addCredit");
        ctd.setCreditGiftMonths("0");
        ctd.setStartDate(rd.getAcceptTime());
        ctd.setEndDate(SysDateMgr.addDays(rd.getAcceptTime(), 3));
        ctd.setModifyTag("0");
        btd.add(serialNumber, ctd);
    }

    public void createCustomerTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();
        CustomerTradeData ctd = reqData.getUca().getCustomer();
        ctd.setCustType("0");
        ctd.setModifyTag(BofConst.MODIFY_TAG_ADD);

        ctd.setOpenLimit("0"); // 默认为0
        ctd.setInDate(reqData.getUca().getUser().getOpenDate());
        ctd.setInStaffId(CSBizBean.getVisit().getStaffId());
        ctd.setInDepartId(CSBizBean.getVisit().getDepartId());
        ctd.setRemoveTag("0");
        ctd.setCustState("0");

        btd.add(serialNumber, ctd);
    }

    public void createCustPersonTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();
        CustPersonTradeData cptd = reqData.getUca().getCustPerson();
        cptd.setRemoveTag("0");
        cptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        
        if(cptd.getBirthday()==null||cptd.getBirthday().trim().length()==0){
            cptd.setBirthday("1900-01-01");
        }

        btd.add(serialNumber, cptd);
    }

    public void createNpTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();

        NpTradeData nptd = new NpTradeData();
        nptd.setCredType(reqData.getUca().getCustPerson().getPsptTypeCode());
        nptd.setPsptId(reqData.getUca().getCustPerson().getPsptId());
        nptd.setCustName(reqData.getUca().getCustPerson().getCustName());

        nptd.setActorCustName(reqData.getOther().getAssureName());
        nptd.setActorCredType(reqData.getOther().getAssurePsptTypeCode());
        nptd.setActorPsptId(reqData.getOther().getAssurePsptId());
        nptd.setUserId(reqData.getUca().getUserId());
        nptd.setTradeTypeCode("40");
        nptd.setNpServiceType("MOBILE");

        nptd.setSerialNumber(reqData.getUca().getSerialNumber());
        nptd.setCancelTag("0");
        nptd.setState("000");
        nptd.setANpCardType("10000000");
        if(StringUtils.isNotEmpty(reqData.getAuthCode())){        	
        	nptd.setAuthCode(reqData.getAuthCode());
        	nptd.setAuthCodeExpired(reqData.getAuthCodeExpired());
        	nptd.setState("040");
        	nptd.setMsgCmdCode("ACT_REQ_NEW");
        }
        

        
        IDataset aps = TradeNpQry.getValidTradeNpBySn(reqData.getUca().getSerialNumber());
        String home_operator = "";
        
        String netWorkType = reqData.getNetWorkType();
        if(IDataUtil.isNotEmpty(aps)){
            String asp = aps.getData(0).getString("ASP", "").trim();

            if ("2".equals(asp))
            {
                home_operator = "003";
                netWorkType = "3";
            }
            if ("3".equals(asp))
            {
                home_operator = "001";
                netWorkType = "1";
            }
            if ("1".equals(asp))
            {
                home_operator = "002";
                netWorkType = "4";

            }
        }
       
        if(StringUtils.isBlank(netWorkType)){
            netWorkType = "0";
        }
        String homeNetid = home_operator + netWorkType;
        String prov_code = reqData.getProvCode();
        homeNetid = homeNetid + prov_code + "0";

        String inNetId = "00248980";// 其中前三位为运营商代码（现有三个运营商，中国电信001，中国移动002，中国联通003）；第四位为运营商网络标识符（1为CDMA，2为CDMA2000，3为GSM，4为TD-SCDMA，5为WCDMA）；5-7位为本地网标识码；第8位为拓展位，默认填为0
        String netId = reqData.getHomeOperator() + reqData.getNetWorkType() + "8980";

        
        // tf_f_user_np_all 视图从TD_NP来
        // 20100419 如果 tf_f_user_np_all视图有记录，则用里面的home_netid 替换 HOME_NETID
        // SEL_NPALL_VIEW_BYS视图没有 暂时注释
      IDataset npall = TradeNpQry.getTradeNpAllBySn(reqData.getUca().getSerialNumber());
        if (IDataUtil.isNotEmpty(npall))
        {
            homeNetid = npall.getData(0).getString("HOME_NETID", "");
            if (homeNetid.length() == 6)
            {
                homeNetid = "00" + homeNetid;
            }
//            netId = npall.getData(0).getString("PORT_IN_NETID", "");
//            if (netId.length() == 6)
//            {
//                netId = "00" + netId;
//            }
        }
        nptd.setHomeNetid(homeNetid);
        nptd.setPortInNetid(inNetId);// 携 入运营商
        nptd.setPortOutNetid(netId);// 携出运营商
        nptd.setCreateTime(reqData.getAcceptTime());
        nptd.setBookSendTime(reqData.getAcceptTime());
        nptd.setRsrvStr3(reqData.getIsNpBack());
        nptd.setPhone(reqData.getUca().getCustPerson().getPhone());
        //新增受理员工信息 add by dengyi5
        nptd.setUpdateDepartId(CSBizBean.getVisit().getDepartId());
        nptd.setUpdateStaffId(CSBizBean.getVisit().getStaffId());
        nptd.setEparchyCode(CSBizBean.getTradeEparchyCode());
		
        // String np_tag = "1";
        // String str = homeNetid.substring(0, 3);
        // if ("002".equals(str))
        // {
        // np_tag = "6";// 移动携回
        // }
        // nptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        // nptd.setNpTag(np_tag);

        btd.add(serialNumber, nptd);
    }

    /**
     * 生成台帐其它资料表
     * 
     * @author sunxin
     * @param btd
     * @throws Exception
     */
    public void createOtherTradeData(BusiTradeData btd) throws Exception
    {
        CreateNpUserReqData createPersonUserRD = (CreateNpUserReqData) btd.getRD();
        String serialNumber = createPersonUserRD.getUca().getUser().getSerialNumber();

        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(createPersonUserRD.getUca().getUserId());
        otherTD.setRsrvValueCode("CHRN");
        otherTD.setRsrvValue("实名制");

        otherTD.setRsrvStr1(CSBizBean.getVisit().getStaffId());
        otherTD.setRsrvStr2(createPersonUserRD.getAcceptTime());
        otherTD.setRsrvStr3("1");
        otherTD.setRsrvStr4("10");
        otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
        otherTD.setStartDate(createPersonUserRD.getAcceptTime());
        otherTD.setEndDate(SysDateMgr.getTheLastTime());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(serialNumber, otherTD);
        // 手输身份证4A
		String handerInput4A = createPersonUserRD.getHanderInput4A();
		if (StringUtils.equals("1", handerInput4A))
		{
			OtherTradeData otherTD4 = new OtherTradeData();
			otherTD4.setUserId(createPersonUserRD.getUca().getUserId());
			otherTD4.setRsrvValueCode("HANDINPUTPSPTID");
			otherTD4.setRsrvValue("手工输入居民身份证");
			otherTD4.setRsrvStr1(btd.getTradeId());
			otherTD4.setStartDate(createPersonUserRD.getAcceptTime());
			otherTD4.setEndDate(SysDateMgr.END_DATE_FOREVER);
			otherTD4.setDepartId(CSBizBean.getVisit().getDepartId());
			otherTD4.setInstId(SeqMgr.getInstId());
			otherTD4.setModifyTag(BofConst.MODIFY_TAG_ADD);
			btd.add(createPersonUserRD.getUca().getSerialNumber(), otherTD4);
		}

		// 一证5号4A
		String more5Pspt4A = createPersonUserRD.getMore5Pspt4A();
		if (StringUtils.equals("1", more5Pspt4A))
		{
			OtherTradeData otherTD5 = new OtherTradeData();
			otherTD5.setUserId(createPersonUserRD.getUca().getUserId());
			otherTD5.setRsrvValueCode("MORE5PSPT4A");
			otherTD5.setRsrvValue("一证5号4A认证");
			otherTD5.setRsrvStr1(btd.getTradeId());
			otherTD5.setStartDate(createPersonUserRD.getAcceptTime());
			otherTD5.setEndDate(SysDateMgr.END_DATE_FOREVER);
			otherTD5.setDepartId(CSBizBean.getVisit().getDepartId());
			otherTD5.setInstId(SeqMgr.getInstId());
			otherTD5.setModifyTag(BofConst.MODIFY_TAG_ADD);
			btd.add(createPersonUserRD.getUca().getSerialNumber(), otherTD5);
		}
        
       
    }

    private void createPayrelationTradeData(BusiTradeData btd) throws Exception
    {

        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        payrelationTD.setUserId(reqData.getUca().getUserId());
        payrelationTD.setAcctId(reqData.getUca().getAcctId());

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
        payrelationTD.setRemark(reqData.getRemark());
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

        btd.add(serialNumber, payrelationTD);
    }

    private void createPostTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        List<PostInfo> posts = reqData.getPosts();
        if (posts != null)
        {
            for (PostInfo post : posts)
            {
                if ("1".equals(post.getPostTag()))
                {
                    PostTradeData postTD = new PostTradeData();
                    postTD.setId(reqData.getUca().getUserId()); // 标识:客户、用户或帐户标识
                    postTD.setIdType("1"); // 标识类型：0-客户，1-用户，2-帐户
                    postTD.setPostName(post.getPostName());
                    postTD.setPostTag(post.getPostTag()); // 邮寄标志：0-不邮寄，1-邮寄
                    postTD.setPostContent(post.getPostContent());
                    postTD.setPostTypeset(post.getPostTypeset()); // 邮寄方式：0-邮政，2-Email
                    postTD.setPostCyc(post.getPostCyc()); // 邮寄周期：0-按月，1-按季，2-按年
                    postTD.setPostAddress(post.getPostinfoAddress());
                    postTD.setPostCode(post.getPostinfoCode());
                    postTD.setEmail(post.getPostEmail());
                    postTD.setFaxNbr(post.getPostFaxNbr());
                    postTD.setCustType("0");
                    postTD.setStartDate(reqData.getUca().getUser().getOpenDate());
                    postTD.setEndDate(SysDateMgr.getTheLastTime());
                    postTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    postTD.setInstId(SeqMgr.getInstId());
                    btd.add(serialNumber, postTD);
                }
            }
        }

    }

    private void createProductTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(reqData.getUca().getUserId());
        productTD.setUserIdA("-1");

        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getUca().getUser().getOpenDate());

        productTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");

        btd.add(serialNumber, productTD);
    }

    private void createResTradeData(BusiTradeData btd) throws Exception
    {
        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();

        List<ResInfo> resInfos = reqData.getResInfos();

        if (resInfos != null)
        {
            for (ResInfo resInfo : resInfos)
            {

                ResTradeData resTD = new ResTradeData();
                String inst_id = SeqMgr.getInstId();
                resTD.setUserId(reqData.getUca().getUserId());
                resTD.setUserIdA("-1");
                resTD.setResTypeCode(resInfo.getResTypeCode());

                resTD.setResCode(resInfo.getResCode());
                resTD.setImsi(resInfo.getImsi());
                resTD.setKi(resInfo.getKi());
                resTD.setInstId(inst_id);
                resTD.setStartDate(reqData.getUca().getUser().getOpenDate());

                resTD.setEndDate(SysDateMgr.getTheLastTime());
                resTD.setModifyTag(resInfo.getModifyTag());
                resTD.setRsrvStr1(resInfo.getRsrvStr1()); // SIM卡的RESKIND|CAPACITY(资源类型|SIM卡容量)
          
                resTD.setRsrvStr3(resInfo.getRsrvStr3()); // 3G卡opc值
                resTD.setRsrvStr4(resInfo.getRsrvStr4()); // 区分物联网资源
                resTD.setRsrvStr5(resInfo.getRsrvStr5());
                resTD.setRsrvTag3(resInfo.getRsrvTag3());//表示4G卡
                if ("1".equals(resInfo.getResTypeCode()))
                {
                    resTD.setRsrvStr2(resInfo.getRsrvStr2());
                    String str2G = "";
                    if ("1".equals(resInfo.getSimCardType()))
                    {
                        str2G = "2";// callpf 的要求
                    }
                    if (StringUtils.isNotBlank(resInfo.getOpc()))
                    {// opc不为空就是3g卡
                        str2G = "2";
                    }
                    resTD.setRsrvTag1(str2G);
                    resTD.setRsrvTag2(resInfo.getRsrvTag2());//
                    resTD.setRsrvNum5(resInfo.getRsrvNum5());//
                    // String simCardType = resInfo.getSimCardType();
                    // String capacityTypeCode = resInfo.getCapacityTypeCode();
                    // String str1 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_RESKIND", new
                    // java.lang.String[]
                    // { "RES_TYPE_CODE", "RES_KIND_CODE" }, "KIND_NAME", new java.lang.String[]
                    // { "6", simCardType });
                    // String str2 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_SIMCAPACITY",
                    // "CAPACITY_TYPE_CODE", "CAPACITY_TYPE", capacityTypeCode);
                    // String rsrvStr8 = "";
                    // if (StringUtils.isNotBlank(str1))
                    // {
                    // rsrvStr8 += str1;
                    // }
                    // if (StringUtils.isNotBlank(str2))
                    // {
                    // rsrvStr8 += str2;
                    // }
                    btd.getMainTradeData().setRsrvStr1(resInfo.getResCode());
                    btd.getMainTradeData().setRsrvStr8(resInfo.getResKindName());// 修改主台账8

                }

                btd.add(serialNumber, resTD);
            }
        }
    }

    public void createUserTradeData(BusiTradeData btd) throws Exception
    {

        CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getSerialNumber();
        UserTradeData utd = reqData.getUca().getUser();

        String userPasswd = reqData.getUca().getUser().getUserPasswd();
        String userId = reqData.getUca().getUser().getUserId();

        btd.getMainTradeData().setRsrvStr9(DESUtil.encrypt(userPasswd));// 主台账放可逆密码
        // 密码加密
        if (StringUtils.isNotBlank(userPasswd))
        {
            userPasswd = PasswdMgr.encryptPassWD(userPasswd, userId);

        }
        IDataset snMoffice = TradeNpQry.getValidTradeNpBySn(reqData.getUca().getSerialNumber());
 

        utd.setCityCodeA("0");
        utd.setUserPasswd(userPasswd);
        utd.setUserDiffCode("0");
        utd.setUserTagSet("1");// 已携入 对应老完工流程ModiUserTagSet
        // utd.setOpenDate(SysDateMgr.decodeTimestamp(reqData.getAcceptTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD));
        utd.setUserStateCodeset("0");
        utd.setNetTypeCode(reqData.getUca().getUser().getNetTypeCode());

        utd.setPrepayTag("1"); // 预付费标记：0：后付费，1：预付费
        utd.setMputeMonthFee("0");
        utd.setMputeDate(reqData.getUca().getUser().getMputeDate());
        utd.setFirstCallTime(reqData.getUca().getUser().getFirstCallTime());
        utd.setLastStopTime(reqData.getUca().getUser().getLastStopTime());

        utd.setChangeuserDate(reqData.getUca().getUser().getChangeuserDate());
        utd.setInDate(reqData.getUca().getUser().getOpenDate()); // 建档时间
        utd.setInStaffId(CSBizBean.getVisit().getStaffId()); // 建档员工
        utd.setInDepartId(CSBizBean.getVisit().getDepartId()); // 建档渠道
        utd.setOpenStaffId(CSBizBean.getVisit().getStaffId()); // 开户员工
        utd.setOpenDepartId(CSBizBean.getVisit().getDepartId()); // 开户渠道
        utd.setDevelopDepartId(CSBizBean.getVisit().getDepartId()); // 发展渠道
        utd.setDevelopCityCode(CSBizBean.getVisit().getCityCode()); // 发展县市
        utd.setDevelopEparchyCode(CSBizBean.getTradeEparchyCode()); // 发展地市
        utd.setAssureCustId(reqData.getUca().getUser().getAssureCustId()); // 担保客户标识
        utd.setRemoveTag("0");
        utd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        utd.setAcctTag("0");
        utd.setOpenMode("0");

        btd.add(serialNumber, utd);
    }
    
    public void createCustPersonOtherData(BusiTradeData btd) throws Exception
    {
    	CreateNpUserReqData reqData = (CreateNpUserReqData) btd.getRD();
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        CustPersonTradeData custpersonTD = reqData.getUca().getCustPerson().clone();
        if (!custpersonTD.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
        {
        	//新增使用人证件类型录入
        	String strUseName = custpersonTD.getRsrvStr5();
        	String strUsePsptTypeCode = custpersonTD.getRsrvStr6();
        	String strUsePsptId = custpersonTD.getRsrvStr7();
        	String strUsePsptAddr = custpersonTD.getRsrvStr8();
        	
        	String strTradeTpyeCode = btd.getTradeTypeCode();
        	
        	if( StringUtils.isNotBlank(strUseName) || StringUtils.isNotBlank(strUsePsptTypeCode)
        	  ||StringUtils.isNotBlank(strUsePsptId) || StringUtils.isNotBlank(strUsePsptAddr) ){
        		
            	String strCustId = custpersonTD.getCustId();
            	String strPartition_id = strCustId.substring(strCustId.length() - 4);
            	      
    			IDataset list = CustPersonInfoQry.qryCustPersonOtherByCustId(strCustId);//Dao.qryByParse(parser, BizRoute.getRouteId());
    			if( IDataUtil.isNotEmpty(list) ){
    				IData custPersonOtherData = list.first();
    				custPersonOtherData.put("USE_NAME", custpersonTD.getRsrvStr5());
    	        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custpersonTD.getRsrvStr6());
    	        	custPersonOtherData.put("USE_PSPT_ID", custpersonTD.getRsrvStr7());
    	        	custPersonOtherData.put("USE_PSPT_ADDR", custpersonTD.getRsrvStr8());
    	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
    				Dao.update("TF_F_CUST_PERSON_OTHER", custPersonOtherData, new String[] { "PARTITION_ID", "CUST_ID" });
    			}else{
    				IData custPersonOtherData = new DataMap();
    	        	custPersonOtherData.put("PARTITION_ID", strPartition_id);
    	        	custPersonOtherData.put("CUST_ID", custpersonTD.getCustId());
    	        	custPersonOtherData.put("USE_NAME", custpersonTD.getRsrvStr5());
    	        	custPersonOtherData.put("USE_PSPT_TYPE_CODE", custpersonTD.getRsrvStr6());
    	        	custPersonOtherData.put("USE_PSPT_ID", custpersonTD.getRsrvStr7());
    	        	custPersonOtherData.put("USE_PSPT_ADDR", custpersonTD.getRsrvStr8());
    	        	custPersonOtherData.put("CREATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	        	custPersonOtherData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	        	custPersonOtherData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    	        	custPersonOtherData.put("REMARK", "个人开户-使用人证据录入");
    	        	custPersonOtherData.put("RSRV_STR1", strTradeTpyeCode);
    	        	custPersonOtherData.put("RSRV_STR2", "");
    	        	custPersonOtherData.put("RSRV_STR3", "");
    	        	custPersonOtherData.put("RSRV_STR4", "");
    	        	custPersonOtherData.put("RSRV_STR5", "");
    				Dao.insert("TF_F_CUST_PERSON_OTHER", custPersonOtherData);
    			}
        		
        	}
        	
        }
       
    }

}
