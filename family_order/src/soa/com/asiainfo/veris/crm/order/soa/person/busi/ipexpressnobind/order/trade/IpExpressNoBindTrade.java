
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpressnobind.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressNoBindException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AcctConsignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctConsignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ipexpressnobind.order.requestdata.IpExpressNoBindRequestData;

public class IpExpressNoBindTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        // 判断手机号码用户是否已经绑定过其他号码
        IpExpressNoBindRequestData ienbrd = (IpExpressNoBindRequestData) bd.getRD();
        String userId = ienbrd.getUca().getUserId();
        String serialNumber = ienbrd.getUca().getSerialNumber();
        String serialNumberA = "52" + serialNumber;
        IDataset bindInfos = RelaUUInfoQry.getUserRelationByUR(userId, "52");
        String userIdA = "";
        if (bindInfos.size() > 0)
        {// 已绑定过
            userIdA = bindInfos.getData(0).getString("USER_ID_A");
            serialNumberA = bindInfos.getData(0).getString("SERIAL_NUMBER_A");
        }
        else
        {// 需要新建虚拟用户
            userIdA = SeqMgr.getUserId();
            geneTradeUser(userIdA, serialNumberA, "111111", bd);
            geneTradeRelationIdA(serialNumber, userId, userIdA, bd);
            bd.addOpenUserAcctDayData(userIdA, "1");
            geneTradePayrelation(serialNumberA, userIdA, ienbrd.getUca(), bd);
            geneTradeProductIdA(serialNumberA, userIdA, bd);
            geneTradeResIdA(serialNumberA, userIdA, bd);
            geneTradeDiscnt(serialNumberA, userIdA, bd, "5901", "-1");
        }

        // 处理绑定的号码
        List<UserTradeData> ipUserInfos = ienbrd.getIpUserDatas();
        int delCount = 0, addCount = 0, mainDelFlag = 0, count = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "52").size(), updCount = 0;
        for (int i = 0, size = ipUserInfos.size(); i < size; i++)
        {
            UserTradeData ipUserInfo = ipUserInfos.get(i);
            // 关联inst_id
            String inst_id = SeqMgr.getInstId();
            String dealTag = ipUserInfo.getModifyTag();
            String userIdB = ipUserInfo.getUserId();
            String serialNumberB = ipUserInfo.getSerialNumber();
            if ("0".equals(dealTag))
            {
                if (serialNumber.equals(serialNumberB))
                {// 主号码
                    geneTradeAcctConsign(ienbrd.getUca(), bd);
                    geneTradeAttr(ipUserInfo, bd, inst_id);
                    bd.addOpenUserAcctDayData(userIdB, "1");
                    bd.addOpenAccountAcctDayData(ienbrd.getUca().getAcctId(), "1");
                    geneTradeDiscnt(serialNumberB, userIdB, bd, "487", ipUserInfo.getRsrvStr1());
                    geneTradeProductIdB(userIdA, ipUserInfo, bd);
                    geneTradeResIdB(userIdA, ipUserInfo, bd);
                    geneTradeSvc(userIdA, ipUserInfo, bd, inst_id);
                    bd.add(serialNumberB, ienbrd.getUca().getUser());
                    bd.add(serialNumberB, ienbrd.getUca().getAccount());
                    bd.add(serialNumberB, ienbrd.getUca().getCustomer());
                    bd.add(serialNumberB, ienbrd.getUca().getCustPerson());
                    geneTradePayrelation(serialNumberB, userIdB, ienbrd.getUca(), bd);
                    addCount++;
                    continue;
                }
                // 新增绑定号码
                geneTradeUser(userIdB, serialNumberB, ipUserInfo.getUserPasswd(), bd);
                geneTradeAttr(ipUserInfo, bd, inst_id);
                geneTradeRelationIdB(serialNumberA, userIdA, ipUserInfo, bd);
                bd.addOpenUserAcctDayData(userIdB, "1");
                geneTradePayrelation(serialNumberB, userIdB, ienbrd.getUca(), bd);
                geneTradeDiscnt(serialNumberB, userIdB, bd, "487", ipUserInfo.getRsrvStr1());
                geneTradeProductIdB(userIdA, ipUserInfo, bd);
                geneTradeResIdB(userIdA, ipUserInfo, bd);
                geneTradeSvc(userIdA, ipUserInfo, bd, inst_id);
                addCount++;
            }
            else if ("1".equals(dealTag))
            {
                // 删除绑定号码
                if (serialNumber.equals(serialNumberB))
                {// 删除主号码需要标记
                    mainDelFlag = 1;
                    // 处理托收信息
                    delTradeAcctConsign(ienbrd.getUca(), bd);
                }
                delTradeSvc(ipUserInfo, bd);
                delTradeRes(ipUserInfo, bd);
                delTradeProduct(ipUserInfo, bd);
                delTradeDiscnt(ipUserInfo, bd);
                delTradePayrelation(ipUserInfo, bd);
                delTradeRelationIdB(ipUserInfo, bd);
                delTradeAttr(ipUserInfo, bd);
                delTradeUser(ipUserInfo, bd);

                delCount++;
            }
            else if ("2".equals(dealTag))
            {
                // 修改绑定号码信息，修改也只能改产品、密码、服务信息
                UcaData ipUca = UcaDataFactory.getUcaByUserId(userIdB);
                String newProductId = ipUserInfo.getRsrvStr1();
                // 密码
                if (!ipUserInfo.getUserPasswd().equals(ipUca.getUser().getUserPasswd()))
                {
                    updTradeUser(ipUserInfo, bd);
                    delTradeAttr(ipUserInfo, bd);
                    geneTradeAttr(ipUserInfo, bd, inst_id);
                    updCount++;
                }
                // 产品
                if (!ipUca.getProductId().equals(newProductId))
                {
                    updTradeProduct(ipUserInfo, bd);
                    updCount++;
                }

                // 服务
                updCount = updCount + updTradeSvc(ipUserInfo, bd);
            }
        }

        // 若该手机号码下已无绑定固定号码，删除虚拟用户信息
        if (delCount == count && count != 0 && addCount == 0)
        {
            UserTradeData userInfoIdA = UcaDataFactory.getUcaByUserId(userIdA).getUser();
            delTradeRelationIdA(userIdA, bd);
            delTradeDiscnt(userInfoIdA, bd);
            delTradePayrelation(userInfoIdA, bd);
            delTradeRes(userInfoIdA, bd);
            delTradeProduct(userInfoIdA, bd);
            delTradeUser(userInfoIdA, bd);

        }
        else if (mainDelFlag == 1)
        {
            CSAppException.apperr(IpExpressNoBindException.CRM_IPEXPRESSNOBIND_5);
        }
        if (updCount == 0 && addCount == 0 && delCount == 0)
        {
            CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_12);
        }
    }

    private void delTradeAcctConsign(UcaData uca, BusiTradeData bd) throws Exception
    {
        IDataset acctConsignInfos = AcctConsignInfoQry.getConsignInfoByAcctId(uca.getAcctId());
        if (DataSetUtils.isNotBlank(acctConsignInfos))
        {
            for (int i = 0, size = acctConsignInfos.size(); i < size; i++)
            {
                AcctConsignTradeData actd = new AcctConsignTradeData(acctConsignInfos.getData(i));
                actd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                actd.setEndCycleId(SysDateMgr.getNowCyc());
                bd.add(uca.getSerialNumber(), actd);
            }
        }
    }

    private void delTradeAttr(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        List<AttrTradeData> attrInfos = ucaInfo.getUserAttrs();
        for (int i = 0, size = attrInfos.size(); i < size; i++)
        {
            AttrTradeData attrInfo = attrInfos.get(i).clone();
            attrInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
            attrInfo.setEndDate(SysDateMgr.getSysTime());
            bd.add(utd.getSerialNumber(), attrInfo);
        }
    }

    private void delTradeDiscnt(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        List<DiscntTradeData> discntInfos = ucaInfo.getUserDiscnts();
        for (int i = 0, size = discntInfos.size(); i < size; i++)
        {
            DiscntTradeData discntInfo = discntInfos.get(i).clone();
            discntInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
            discntInfo.setEndDate(SysDateMgr.getSysTime());
            bd.add(utd.getSerialNumber(), discntInfo);
        }
    }

    private void delTradePayrelation(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        IData payrelationInfos = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
        PayRelationTradeData payrelationInfo = new PayRelationTradeData(payrelationInfos);
        payrelationInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
        payrelationInfo.setActTag("0");
        payrelationInfo.setEndCycleId(SysDateMgr.getLastCycle());
        bd.add(utd.getSerialNumber(), payrelationInfo);
    }

    private void delTradeProduct(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        List<ProductTradeData> productInfos = ucaInfo.getUserProducts();
        for (int i = 0, size = productInfos.size(); i < size; i++)
        {
            ProductTradeData productInfo = productInfos.get(i).clone();
            productInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
            productInfo.setEndDate(SysDateMgr.getSysTime());
            bd.add(utd.getSerialNumber(), productInfo);
        }
    }

    private void delTradeRelationIdA(String userIdA, BusiTradeData bd) throws Exception
    {
        IDataset relationInfos = RelaUUInfoQry.getUserRelationsByUserId(userIdA, bd.getRD().getUca().getUserId());
        RelationTradeData rtd = new RelationTradeData((IData) relationInfos.getData(0));
        rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        rtd.setEndDate(SysDateMgr.getSysTime());
        bd.add(null, rtd);
    }

    private void delTradeRelationIdB(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        IDataset relationInfos = RelaUUInfoQry.qryByRelaUserIdB(userId, "52", null);
        RelationTradeData rtd = new RelationTradeData((IData) relationInfos.getData(0));
        rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        rtd.setEndDate(SysDateMgr.getSysTime());
        bd.add(null, rtd);
    }

    private void delTradeRes(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        IDataset resInfos = UserResInfoQry.getUserResInfoByUserId(userId);
        for (int i = 0, size = resInfos.size(); i < size; i++)
        {
            ResTradeData resInfo = new ResTradeData((IData) resInfos.get(i));
            resInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
            resInfo.setEndDate(SysDateMgr.getSysTime());
            bd.add(utd.getSerialNumber(), resInfo);
        }
    }

    private void delTradeSvc(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        List<SvcTradeData> svcInfos = ucaInfo.getUserSvcs();
        for (int i = 0, size = svcInfos.size(); i < size; i++)
        {
            SvcTradeData svcInfo = svcInfos.get(i).clone();
            svcInfo.setModifyTag(BofConst.MODIFY_TAG_DEL);
            svcInfo.setEndDate(SysDateMgr.getSysTime());
            bd.add(utd.getSerialNumber(), svcInfo);
        }
    }

    private void delTradeUser(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        UserTradeData delUtd = ucaInfo.getUser();
        delUtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delUtd.setRemoveTag("2");
        delUtd.setDestroyTime(SysDateMgr.getSysTime());
        bd.add(utd.getSerialNumber(), delUtd);
    }

    private void geneTradeAcctConsign(UcaData uca, BusiTradeData bd) throws Exception
    {
        AcctConsignTradeData actd = new AcctConsignTradeData();
        String sn = uca.getSerialNumber();
        String payModeCode = uca.getAccount().getPayModeCode();
        actd.setAcctId(uca.getAcctId());
        actd.setPayModeCode(payModeCode);
        actd.setEparchyCode(uca.getUserEparchyCode());
        actd.setCityCode(uca.getUser().getCityCode());
        if ("0".equals(payModeCode))
        {
            actd.setSuperBankCode("-1");
            actd.setBankCode("-1");
            actd.setBankAcctNo("-1");
            actd.setBankAcctName("-1");
        }
        else
        {
            actd.setSuperBankCode(uca.getAccount().getRsrvStr1());
            actd.setBankCode(uca.getAccount().getBankCode());
            actd.setBankAcctNo(uca.getAccount().getBankAcctNo());
            actd.setBankAcctName(uca.getAccount().getPayName());
        }
        actd.setContractName(uca.getCustPerson().getContact());
        actd.setContactPhone(uca.getCustPerson().getContactPhone());
        actd.setPostAddress(uca.getCustPerson().getPostAddress());
        actd.setConsignMode("1");
        actd.setPriority("0");
        actd.setActTag("1");
        actd.setStartCycleId(SysDateMgr.getNowCyc());
        actd.setEndCycleId(SysDateMgr.getEndCycle205012());
        actd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        actd.setPaymentId("4");
        actd.setPayFeeModeCode("4");
        actd.setInstId(SeqMgr.getInstId());
        bd.add(sn, actd);
    }

    private void geneTradeAttr(UserTradeData utd, BusiTradeData bd, String inst_id) throws Exception
    {
        AttrTradeData atd = new AttrTradeData();
        atd.setUserId(utd.getUserId());
        atd.setInstType("S");
        atd.setInstId(SeqMgr.getInstId());
        atd.setAttrCode("PASSWD");
        atd.setAttrValue(utd.getUserPasswd());
        atd.setStartDate(SysDateMgr.getSysTime());
        atd.setEndDate(SysDateMgr.getTheLastTime());
        atd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        atd.setRelaInstId(inst_id);
        bd.add(utd.getSerialNumber(), atd);
    }

    private void geneTradeDiscnt(String serialNumber, String userId, BusiTradeData bd, String discntCode, String productId) throws Exception
    {
        DiscntTradeData dtd = new DiscntTradeData();
        dtd.setUserIdA(bd.getRD().getUca().getUserId());
        dtd.setUserId(userId);
        dtd.setPackageId("-1");
        dtd.setProductId(productId);
        dtd.setElementId(discntCode);
        dtd.setSpecTag("0");
        dtd.setRelationTypeCode("52");
        dtd.setCampnId("0");
        dtd.setInstId(SeqMgr.getInstId());
        dtd.setStartDate(SysDateMgr.getSysTime());
        dtd.setEndDate(SysDateMgr.getTheLastTime());
        dtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        dtd.setRemark(bd.getRD().getRemark());
        bd.add(serialNumber, dtd);
    }

    private void geneTradePayrelation(String serialNumber, String userId, UcaData ucaInfo, BusiTradeData bd) throws Exception
    {
        PayRelationTradeData ptd = new PayRelationTradeData();
        ptd.setUserId(userId);
        ptd.setAcctId(ucaInfo.getAcctId());
        ptd.setPayitemCode("-1");
        ptd.setAcctPriority("0");
        ptd.setUserPriority("0");
        ptd.setBindType("1");
        ptd.setActTag("1");
        ptd.setDefaultTag("1");
        ptd.setLimitType("0");
        ptd.setLimit("0");
        ptd.setComplementTag("0");
        ptd.setInstId(SeqMgr.getInstId());
        ptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        ptd.setStartCycleId(SysDateMgr.getNowCycle());
        ptd.setEndCycleId(SysDateMgr.getEndCycle20501231());
        ptd.setRemark(bd.getRD().getRemark());
        bd.add(serialNumber, ptd);
    }

    private void geneTradeProductIdA(String serialNumber, String userIdA, BusiTradeData bd) throws Exception
    {
        ProductTradeData ptd = new ProductTradeData();
        ptd.setUserId(userIdA);
        ptd.setUserIdA("-1");
        ptd.setProductId("6010");
        ptd.setProductMode("00");
        ptd.setBrandCode("IP00");
        ptd.setMainTag("1");
        ptd.setInstId(SeqMgr.getInstId());
        ptd.setStartDate(SysDateMgr.getSysTime());
        ptd.setEndDate(SysDateMgr.getTheLastTime());
        ptd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        ptd.setRemark(bd.getRD().getRemark());
        bd.add(serialNumber, ptd);
    }

    private void geneTradeProductIdB(String userIdA, UserTradeData utd, BusiTradeData bd) throws Exception
    {
        ProductTradeData ptd = new ProductTradeData();
        ptd.setUserId(utd.getUserId());
        ptd.setUserIdA(userIdA);
        ptd.setProductId(utd.getRsrvStr1());
        ptd.setProductMode("00");
        ptd.setBrandCode("IP02");
        ptd.setMainTag("1");
        ptd.setInstId(SeqMgr.getInstId());
        ptd.setStartDate(SysDateMgr.getSysTime());
        ptd.setEndDate(SysDateMgr.getTheLastTime());
        ptd.setModifyTag(utd.getModifyTag());
        ptd.setRemark(bd.getRD().getRemark());
        bd.add(utd.getSerialNumber(), ptd);
    }

    private void geneTradeRelationIdA(String serialNumber, String userId, String userIdA, BusiTradeData bd) throws Exception
    {
        RelationTradeData rtd = new RelationTradeData();
        String serialNumberA = "52" + serialNumber;
        rtd.setUserIdA(userIdA);
        rtd.setUserIdB(userId);
        rtd.setSerialNumberA(serialNumberA);
        rtd.setSerialNumberB(serialNumber);
        rtd.setRelationTypeCode("52");
        rtd.setRoleTypeCode("0");
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB("1");
        rtd.setShortCode(serialNumberA);
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setStartDate(SysDateMgr.getSysTime());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(null, rtd);
    }

    private void geneTradeRelationIdB(String serialNumberA, String userIdA, UserTradeData utd, BusiTradeData bd) throws Exception
    {
        RelationTradeData rtd = new RelationTradeData();
        rtd.setUserIdA(userIdA);
        rtd.setUserIdB(utd.getUserId());
        rtd.setSerialNumberA(serialNumberA);
        rtd.setSerialNumberB(utd.getSerialNumber());
        rtd.setRelationTypeCode("52");
        rtd.setRoleTypeCode("0");
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB("2");
        rtd.setShortCode(utd.getSerialNumber());
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setStartDate(SysDateMgr.getSysTime());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        rtd.setModifyTag(utd.getModifyTag());
        bd.add(null, rtd);
    }

    private void geneTradeResIdA(String serialNumber, String userIdA, BusiTradeData bd) throws Exception
    {
        ResTradeData rtd = new ResTradeData();
        rtd.setUserId(bd.getRD().getUca().getUserId());
        rtd.setUserIdA(userIdA);
        rtd.setResTypeCode("G");
        rtd.setResCode("52" + serialNumber);
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setStartDate(SysDateMgr.getSysTime());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtd.setRemark(bd.getRD().getRemark());
        bd.add(serialNumber, rtd);
    }

    private void geneTradeResIdB(String userIdA, UserTradeData utd, BusiTradeData bd) throws Exception
    {
        ResTradeData rtd = new ResTradeData();
        rtd.setUserId(utd.getUserId());
        rtd.setUserIdA(userIdA);
        rtd.setResTypeCode("T");
        rtd.setResCode(utd.getSerialNumber());
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setStartDate(SysDateMgr.getSysTime());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        rtd.setModifyTag(utd.getModifyTag());
        rtd.setRemark(bd.getRD().getRemark());
        bd.add(utd.getSerialNumber(), rtd);
    }

    private void geneTradeSvc(String userIdA, UserTradeData utd, BusiTradeData bd, String inst_id) throws Exception
    {
        String[] tempPackageSvcs = utd.getRsrvStr2().split("~");
        String productId = utd.getRsrvStr1();
        for (int j = 0, tempSize = tempPackageSvcs.length; j < tempSize; j++)
        {
        	inst_id = SeqMgr.getInstId();
            String[] tempPackageSvc = tempPackageSvcs[j].split("@");
            String packageId = tempPackageSvc[0];
            String svcId = tempPackageSvc[1];
            SvcTradeData std = new SvcTradeData();
            std.setUserIdA(userIdA);
            std.setUserId(utd.getUserId());
            std.setProductId(productId);
            std.setPackageId(packageId);
            std.setElementId(svcId);
//            IData packageInfo = PkgElemInfoQry.getElementByElementId(packageId, svcId);
              IData packageInfo= UPackageElementInfoQry.getPackageElementInfoByPorductIdElementId(productId,"P",svcId,"S").getData(0);
            if(IDataUtil.isEmpty(packageInfo)){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到订购的服务包信息！");
            }
            std.setMainTag(packageInfo.getString("MAIN_TAG"));
            std.setInstId(inst_id);
            std.setStartDate(SysDateMgr.getSysTime());
            std.setEndDate(SysDateMgr.getTheLastTime());
            std.setModifyTag(utd.getModifyTag());
            bd.add(utd.getSerialNumber(), std);
        }
    }

    private void geneTradeUser(String userId, String serialNumber, String pwd, BusiTradeData bd) throws Exception
    {
        UserTradeData utd = bd.getRD().getUca().getUser().clone();
        UcaData ucaInfo = bd.getRD().getUca();
        utd.setUserId(userId);
        utd.setEparchyCode(CSBizBean.getTradeEparchyCode());
        utd.setCityCode(CSBizBean.getVisit().getCityCode());
        utd.setUserPasswd(pwd);
        utd.setUserTypeCode("0");
        utd.setUserStateCodeset("0");
        utd.setNetTypeCode("00");
        utd.setMputeMonthFee("0");
        utd.setInStaffId(CSBizBean.getVisit().getStaffId());
        utd.setInDepartId(CSBizBean.getVisit().getDepartId());
        utd.setOpenMode("0");
        utd.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        utd.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        utd.setSerialNumber(serialNumber);
        utd.setPrepayTag("0");
        utd.setOpenDate(SysDateMgr.getSysTime());
        utd.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        utd.setDevelopCityCode(CSBizBean.getVisit().getCityCode());
        utd.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        utd.setDevelopEparchyCode(CSBizBean.getTradeEparchyCode());
        utd.setDevelopDate(SysDateMgr.getSysTime());
        utd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        utd.setRemoveTag("0");
        bd.add(serialNumber, utd);
    }

    private void updTradeProduct(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        List<ProductTradeData> ptds = ucaInfo.getUserProducts();
        ProductTradeData ptdOld = ptds.get(0).clone();
        ProductTradeData ptdNew = ptds.get(0).clone();
        ptdOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
        ptdOld.setEndDate(SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));
        bd.add(utd.getSerialNumber(), ptdOld);
        ptdNew.setProductId(utd.getRsrvStr1());
        ptdNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        ptdNew.setStartDate(SysDateMgr.getSysTime());
        bd.add(utd.getSerialNumber(), ptdNew);
    }

    private int updTradeSvc(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        int updCount = 0;
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        List<SvcTradeData> oldstds = ucaInfo.getUserSvcs();
        String[] tempPackageSvcs = utd.getRsrvStr2().split("~");
        String productId = utd.getRsrvStr1();

        // 老服务不在新增服务中，则删除
        for (int i = 0, size = oldstds.size(); i < size; i++)
        {
            SvcTradeData delstd = oldstds.get(i).clone();
            int delTag = 1;
            for (int j = 0, newSize = tempPackageSvcs.length; j < newSize; j++)
            {
                String[] tempPackageSvc = tempPackageSvcs[j].split("@");
                String packageId = tempPackageSvc[0];
                String svcId = tempPackageSvc[1];
                if (svcId.equals(delstd.getElementId()))
                {
                    delTag = 0;
                    break;
                }
            }
            if (delTag == 1)
            {
                delstd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                delstd.setEndDate(SysDateMgr.getLastSecond(SysDateMgr.getSysTime()));
                bd.add(utd.getSerialNumber(), delstd);
                updCount++;
            }
        }

        // 新增服务不在老服务中，则新增
        for (int i = 0, size = tempPackageSvcs.length; i < size; i++)
        {
            String[] tempPackageSvc = tempPackageSvcs[i].split("@");
            String packageId = tempPackageSvc[0];
            String svcId = tempPackageSvc[1];
            int addTag = 1;
            for (int j = 0, sizeOld = oldstds.size(); j < sizeOld; j++)
            {
                SvcTradeData delstd = oldstds.get(j).clone();
                if (svcId.equals(delstd.getElementId()))
                {
                    addTag = 0;
                    break;
                }
            }
            if (addTag == 1)
            {
                SvcTradeData addstd = oldstds.get(0).clone();
                addstd.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addstd.setStartDate(SysDateMgr.getSysTime());
                addstd.setProductId(productId);
                addstd.setPackageId(packageId);
                addstd.setElementId(svcId);
                bd.add(utd.getSerialNumber(), addstd);
                updCount++;
            }
        }
        return updCount;
    }

    private void updTradeUser(UserTradeData utd, BusiTradeData bd) throws Exception
    {
        String userId = utd.getUserId();
        UcaData ucaInfo = UcaDataFactory.getUcaByUserId(userId);
        UserTradeData updUtd = ucaInfo.getUser();
        updUtd.setModifyTag(BofConst.MODIFY_TAG_UPD);
        updUtd.setUserPasswd(utd.getRsrvStr3());
        bd.add(utd.getSerialNumber(), updUtd);
    }
}
