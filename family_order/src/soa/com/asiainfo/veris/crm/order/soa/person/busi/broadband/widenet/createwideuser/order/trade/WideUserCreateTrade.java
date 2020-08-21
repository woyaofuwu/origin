
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.trade;

import java.util.List;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccessAcctTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AddrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WidenetOtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.requestdata.WideUserCreateRequestData;

public class WideUserCreateTrade extends BaseTrade implements ITrade
{

    /**
     * 修改主台帐字段
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {

        btd.getMainTradeData().setUserIdB(reqData.getGponUserId());
        btd.getMainTradeData().setSubscribeType("300");
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        WideUserCreateRequestData reqData = (WideUserCreateRequestData) btd.getRD();
        List<ProductModuleData> selectedElements = reqData.getProductElements();

        createTradeWidenet(btd, reqData);

        if ("630".equals(btd.getTradeTypeCode()))
        {

            createTradeWidenetOther(btd, reqData);

        }
        // 生成宽带用户
        createTradeUser(btd, reqData);

        // 生成虚拟用户
        createTradeVirtualUser(btd, reqData);

        // 生成虚拟产品台账
        createTradeProductVirtualUser(btd, reqData, reqData.getVirtualUserId());

        // 生成用户关系
        createTradeRelationUU(btd, reqData);

        // 宽带用户产品台账
        createTradeProduct(btd, reqData);

        // 虚拟用户绑定资费
        createTradeVirtualDiscnt(btd, reqData);

        // 生成资源台帐
        if (StringUtils.isNotBlank(reqData.getModemNumeric()))
        {
            createResTradeData(btd, reqData);
        }

        // 宽带子账户开户
        if ("611".equals(btd.getTradeTypeCode()))
        {
            Boolean tag = true;
            String userIdA = reqData.getUserIdA();
            if (StringUtils.isBlank(userIdA))
            {
                userIdA = SeqMgr.getUserId();
                tag = false;
                btd.addOpenUserAcctDayData(userIdA, "1");
            }
            String gponUserId = reqData.getGponUserId();// 主账号user_id
            String gponSerialNumber = reqData.getGponSerialNumber();// 主账号用户
            String relationtypecode = "";
            if ("2".equals(reqData.getPreWideType()))// 根据不同的宽带类型取不同的relationtypecode
                relationtypecode = "77";// 平行账号
            else
                relationtypecode = "78"; // 家庭账号
            if (!tag)
            {
                genTradeRelaInfoWide(userIdA, gponUserId, gponSerialNumber, "1", relationtypecode, btd, reqData);
            }
            genTradeRelaInfoWide(userIdA, reqData.getUca().getUserId(), reqData.getUca().getSerialNumber(), "2", relationtypecode, btd, reqData);
        }
        // 生成付费关系
        createTradePayRelation(btd, reqData);

        if (StringUtils.equals("2", reqData.getModemStyle()) && StringUtils.isNotBlank(reqData.getModemNumeric()))
        {

            // 当MODEM使用方式为“租用”时,需要绑定一个帐务指定编码的资费
            createTradeBindDiscnt(btd, reqData, selectedElements.get(0).getStartDate(), selectedElements.get(0).getEndDate());

            // 当MODEM使用方式为“租用”时,需要绑定一个帐务指定编码的服务
            createTradeBindSvc(btd, reqData);
        }
        // 构建产品
        ProductModuleCreator.createProductModuleTradeData(selectedElements, btd);

        appendTradeMainData(btd, reqData);
        btd.addOpenUserAcctDayData(reqData.getUca().getUserId(), "1");
        btd.addOpenUserAcctDayData(reqData.getVirtualUserId(), "1");
    }

    /**
     * 生成台帐资源子表
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void createResTradeData(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        ResTradeData resTD = new ResTradeData();
        resTD.setUserId(reqData.getUca().getUserId());
        resTD.setUserIdA("-1");
        resTD.setResTypeCode("W");// 宽带资源
        resTD.setResCode("-1");
        resTD.setImsi("");
        resTD.setKi("");
        resTD.setInstId(SeqMgr.getInstId());
        resTD.setStartDate(reqData.getOpenDate());
        resTD.setEndDate(SysDateMgr.getTheLastTime());
        resTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTD.setRemark("宽带装机MODEM资源");
        resTD.setRsrvStr1(reqData.getModemNumeric());// MODEM型号
        resTD.setRsrvStr3("TT");// 铁通标识符
        resTD.setRsrvStr4("03");// modem的小类编码
        resTD.setRsrvTag1(reqData.getModemStyle());// MODEM方式
        btd.add(btd.getRD().getUca().getSerialNumber(), resTD);
    }

    private void createTradeAccessAcct(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        AccessAcctTradeData accessAcctTD = new AccessAcctTradeData();
        String ori = "000000000" + reqData.getUca().getUserId();
        String pwd = Encryptor.fnEncrypt("kd123456", ori);
        accessAcctTD.setSerialNumber(reqData.getNormalSerialNumber());
        accessAcctTD.setUserId(reqData.getUca().getUserId());
        accessAcctTD.setInstId(SeqMgr.getInstId());
        accessAcctTD.setAccessAcct(reqData.getUca().getSerialNumber());
        accessAcctTD.setAccessType("0");
        accessAcctTD.setAccessPwd(pwd);

        accessAcctTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        accessAcctTD.setStartDate(reqData.getOpenDate());
        accessAcctTD.setEndDate(SysDateMgr.getTheLastTime());
        accessAcctTD.setRemark("移动宽带创建");
        accessAcctTD.setRsrvStr1("18");// 服开使用：0-不绑定手机号；18-绑定手机号
        accessAcctTD.setRsrvTag1("");

        btd.add(btd.getRD().getUca().getSerialNumber(), accessAcctTD);
    }

    private void createTradeAddr(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        AddrTradeData addrTD = new AddrTradeData();
        addrTD.setUserId(reqData.getUca().getUserId());
        addrTD.setInstId(SeqMgr.getInstId());
        addrTD.setMofficeId("0");
        addrTD.setAddrName(reqData.getStandAddress());
        addrTD.setAddrId(reqData.getStandAddressCode());
        addrTD.setAddrDesc(reqData.getDetailAddress());
        addrTD.setStartDate(reqData.getOpenDate());
        addrTD.setEndDate(SysDateMgr.getTheLastTime());
        addrTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addrTD.setRemark("移动宽带创建");
        btd.add(btd.getRD().getUca().getSerialNumber(), addrTD);
    }

    private void createTradeBindDiscnt(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData, String startdate, String endDate) throws Exception
    {

        IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "1129", reqData.getModemNumeric(), reqData.getUca().getUserEparchyCode());
        if (IDataUtil.isEmpty(commparaInfos))
        {
            commparaInfos = CommparaInfoQry.getCommpara("CSM", "1128", "D", reqData.getUca().getUserEparchyCode());
            if (IDataUtil.isEmpty(commparaInfos))
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_5);
            }
        }
        String specDiscntCode = commparaInfos.getData(0).getString("PARA_CODE1", "-1");
        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(reqData.getUca().getUserId());
        newDiscnt.setUserIdA("-1");
        newDiscnt.setProductId("-1");
        newDiscnt.setPackageId("-1");
        newDiscnt.setElementId(specDiscntCode);
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setStartDate(startdate);
        newDiscnt.setEndDate(endDate);
        btd.add(reqData.getUca().getSerialNumber(), newDiscnt);
    }

    private void createTradeBindSvc(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "1128", "S", reqData.getUca().getUserEparchyCode());
        if (IDataUtil.isEmpty(commparaInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_6);
        }
        String serviceId = commparaInfos.getData(0).getString("PARA_CODE1", "-1");
        SvcTradeData svcData = new SvcTradeData();
        svcData.setUserId(reqData.getUca().getUserId());
        svcData.setUserIdA("-1");
        svcData.setProductId("-1");
        svcData.setPackageId("-1");
        svcData.setElementId(serviceId);
        svcData.setInstId(SeqMgr.getInstId());
        svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        svcData.setStartDate(reqData.getOpenDate());
        svcData.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(reqData.getUca().getSerialNumber(), svcData);
    }

    /**
     * 生成台帐付费表
     * 
     * @author chenzm
     * @param btd
     * @throws Exception
     */
    private void createTradePayRelation(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
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
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(btd.getRD().getUca().getSerialNumber(), payrelationTD);
    }

    private void createTradeProduct(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(reqData.getUca().getUserId());
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");

        btd.add(btd.getRD().getUca().getSerialNumber(), productTD);
    }

    /**
     * 虚拟用户产品台账拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeProductVirtualUser(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData, String userId) throws Exception
    {
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(userId);
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");

        btd.add(btd.getRD().getUca().getSerialNumber(), productTD);
    }

    private void createTradeRate(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        RateTradeData rateTD = new RateTradeData();
        rateTD.setUserId(reqData.getUca().getUserId());
        rateTD.setInstId(SeqMgr.getInstId());
        rateTD.setStartDate(reqData.getOpenDate());
        rateTD.setEndDate(SysDateMgr.getTheLastTime());
        rateTD.setRate("1024");
        rateTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rateTD.setRemark("移动宽带创建");
        // rateTD.setRsrvStr1(reqData.getMainSvc());//服务开通需要主体服务

        btd.add(btd.getRD().getUca().getSerialNumber(), rateTD);
    }

    /**
     * 用户关系台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeRelationUU(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        genTradeRelaInfoWide(reqData.getVirtualUserId(), reqData.getNormalUserId(), reqData.getNormalSerialNumber(), "1", "47", btd, reqData);
        genTradeRelaInfoWide(reqData.getVirtualUserId(), reqData.getUca().getUserId(), reqData.getUca().getSerialNumber(), "2", "47", btd, reqData);
    }

    /**
     * 用户子台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeUser(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        UserTradeData userTD = reqData.getUca().getUser();
        String ori = "000000000" + reqData.getUca().getUserId();
        String ori1 = ori.substring(ori.length() - 9, ori.length());
        String pwd = Encryptor.fnEncrypt(reqData.getUserPasswd(), ori1);
        userTD.setUserPasswd(pwd);
        userTD.setOpenDate(reqData.getOpenDate());
        userTD.setInDate(reqData.getOpenDate());
        btd.add(btd.getRD().getUca().getSerialNumber(), userTD);
    }

    private void createTradeVirtualDiscnt(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {

        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(reqData.getVirtualUserId());
        newDiscnt.setUserIdA("-1");
        newDiscnt.setProductId("-1");
        newDiscnt.setPackageId("-1");
        newDiscnt.setElementId(reqData.getLowDiscntCode());
        newDiscnt.setRelationTypeCode("47");
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("2");
        newDiscnt.setStartDate(reqData.getOpenDate());
        newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
        newDiscnt.setRemark("宽带保底优惠");
        btd.add("KV_" + reqData.getNormalSerialNumber(), newDiscnt);
    }

    /**
     * 虚拟用户子台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeVirtualUser(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        UserTradeData userTD = new UserTradeData();
        String ori = "000000000" + reqData.getVirtualUserId();
        String ori1 = ori.substring(ori.length() - 9, ori.length());
        String pwd = Encryptor.fnEncrypt(reqData.getUserPasswd(), ori1);

        userTD.setUserId(reqData.getVirtualUserId());
        userTD.setCustId(reqData.getUca().getCustomer().getCustId());
        userTD.setUsecustId(reqData.getUca().getCustomer().getCustId());
        userTD.setSerialNumber("KV_" + reqData.getNormalSerialNumber());
        userTD.setNetTypeCode(reqData.getUca().getUser().getNetTypeCode());
        userTD.setUserTypeCode(reqData.getUca().getUser().getUserTypeCode());
        userTD.setUserStateCodeset("0");
        userTD.setUserPasswd(pwd);
        userTD.setPrepayTag("0");
        userTD.setMputeMonthFee("0");
        userTD.setAcctTag("0");
        userTD.setDevelopCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setDevelopDate(reqData.getOpenDate());
        userTD.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setDevelopEparchyCode(CSBizBean.getUserEparchyCode());
        userTD.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setInDate(reqData.getOpenDate());
        userTD.setInStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setInDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setOpenDate(reqData.getOpenDate());
        userTD.setOpenMode("0");
        userTD.setEparchyCode(CSBizBean.getUserEparchyCode());
        userTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        userTD.setCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setRemoveTag("0");
        btd.add("KV_" + reqData.getNormalSerialNumber(), userTD);
    }

    /**
     * 用户宽带台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeWidenet(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        WideNetTradeData wtd = new WideNetTradeData();
        String ori = "000000000" + reqData.getUca().getUserId();
        String ori1 = ori.substring(ori.length() - 9, ori.length());
        String pwd = Encryptor.fnEncrypt(reqData.getUserPasswd(), ori1);
        wtd.setUserId(reqData.getUca().getUserId());
        wtd.setStandAddress(reqData.getStandAddress());
        wtd.setDetailAddress(reqData.getDetailAddress());
        wtd.setStandAddressCode(reqData.getStandAddressCode());
        wtd.setAcctPasswd(pwd);
        wtd.setContact(reqData.getContact());
        wtd.setContactPhone(reqData.getContactPhone());
        wtd.setPhone(reqData.getPhone());
        wtd.setInstId(SeqMgr.getInstId());
        wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        wtd.setStartDate(reqData.getOpenDate());
        wtd.setEndDate(SysDateMgr.getTheLastTime());
        wtd.setSuggestDate(reqData.getOpenDate());
        wtd.setRsrvStr1(DESUtil.encrypt(reqData.getUserPasswd()));
        wtd.setRsrvStr4(reqData.getAreaCode());
        wtd.setRsrvStr5(reqData.getPsptId());
        wtd.setRsrvStr2(reqData.getWideType());
        wtd.setRsrvStr3(reqData.getPreWideType());
        if (StringUtils.isNotBlank(reqData.getModemStyle()))
        {
            wtd.setRsrvTag1("7");// 7代表铁通;
        }
        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }

    /**
     * 用户宽带其它台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author chenzm
     */
    private void createTradeWidenetOther(BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {
        IDataset widenerOtherInfos = WidenetOtherInfoQry.getUserWidenetOtherByNoteId(reqData.getStudentNumber());
        if (IDataUtil.isNotEmpty(widenerOtherInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_18);

        }
        WidenetOtherTradeData widenetOtherTradeData = new WidenetOtherTradeData();
        widenetOtherTradeData.setUserId(reqData.getUca().getUserId());
        widenetOtherTradeData.setSerialNumber(reqData.getUca().getSerialNumber().substring(3));
        widenetOtherTradeData.setNoteId(reqData.getStudentNumber());
        widenetOtherTradeData.setInstId(SeqMgr.getInstId());
        widenetOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        widenetOtherTradeData.setStartDate(reqData.getOpenDate());
        widenetOtherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(btd.getRD().getUca().getSerialNumber(), widenetOtherTradeData);
    }

    private void genTradeRelaInfoWide(String userIdVirtual, String userId, String serialNumber, String roleCodeB, String relationTypeCode, BusiTradeData<BaseTradeData> btd, WideUserCreateRequestData reqData) throws Exception
    {

        RelationTradeData rtd = new RelationTradeData();

        rtd.setUserIdA(userIdVirtual);
        rtd.setUserIdB(userId);
        rtd.setSerialNumberA("-1");
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtd.setRelationTypeCode(relationTypeCode);
        rtd.setSerialNumberB(serialNumber);
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB(roleCodeB);// 2表示副卡
        rtd.setOrderno("0");
        rtd.setStartDate(reqData.getOpenDate());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);
    }

}
