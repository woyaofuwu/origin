
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyMebTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustFamilyMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.DestroyFamilyReqData;

public class DestroyFamilyTrade extends BaseTrade implements ITrade
{

    public static void delVirtualUser(BusiTradeData bd, String virtualSn) throws Exception
    {
        // 这里用UcaDataFactory会存在潜在问题，因为这些虚拟用户的号码都是一样的，如果有多个USER
        // 则只会构造一个UCA
        // UcaData virtualUca = UcaDataFactory.getUcaByUserId(userIdA);
        String sysdate = bd.getRD().getAcceptTime();
        UcaData uca = bd.getRD().getUca();

        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);

        // 终止用户资料
        UserTradeData delUser = virtualUca.getUser().clone();
        delUser.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delUser.setDestroyTime(sysdate);
        delUser.setRemoveTag("1");
        bd.add(virtualUca.getSerialNumber(), delUser);

        // 终止客户资料
        CustomerTradeData delCustomer = virtualUca.getCustomer().clone();
        delCustomer.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delCustomer.setRemoveTag("1");
        delCustomer.setRemoveDate(sysdate);
        bd.add(virtualUca.getSerialNumber(), delCustomer);

        if (null != virtualUca.getCustFamily())
        {
            CustFamilyTradeData delCustFamily = virtualUca.getCustFamily().clone();
            delCustFamily.setModifyTag(BofConst.MODIFY_TAG_DEL);
            delCustFamily.setRemoveTag("1");
            delCustFamily.setRemoveDate(sysdate);
            bd.add(virtualUca.getSerialNumber(), delCustFamily);
        }

        // 终止帐户信息
        AccountTradeData delAcct = virtualUca.getAccount().clone();
        delAcct.setModifyTag(BofConst.MODIFY_TAG_DEL);
        delAcct.setRemoveTag("1");
        delAcct.setRemoveDate(sysdate);
        bd.add(virtualUca.getSerialNumber(), delAcct);

        // 终止付费关系
        IData payRela = UcaInfoQry.qryDefaultPayRelaByUserId(virtualUca.getUserId());
        if (IDataUtil.isNotEmpty(payRela))
        {
            PayRelationTradeData delPayRela = new PayRelationTradeData(payRela);
            // UserFamilyMgr.fjgetFirstDayThisAcct
            // 这里获取账期还要看下原CPP方法的逻辑
            String acctDay = uca.getAcctDay();
            delPayRela.setEndCycleId(SysDateMgr.decodeTimestamp(SysDateMgr.getFirstDayOfThisMonth(), SysDateMgr.PATTERN_TIME_YYYYMMDD));
            delPayRela.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(virtualUca.getSerialNumber(), delPayRela);
        }

        // 终止用户主产品信息
        ProductTradeData delPrdTD = virtualUca.getUserMainProduct();
        delPrdTD.setEndDate(sysdate);
        delPrdTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        bd.add(virtualUca.getSerialNumber(), delPrdTD);

        // 终止用户服务
        List<SvcTradeData> userSvcList = virtualUca.getUserSvcs();
        for (int i = 0, size = userSvcList.size(); i < size; i++)
        {
            SvcTradeData userSvc = userSvcList.get(i);
            SvcTradeData delSvcTD = userSvc.clone();
            delSvcTD.setEndDate(sysdate);
            delSvcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(virtualUca.getSerialNumber(), delSvcTD);
        }

        // 终止用户优惠
        List<DiscntTradeData> userDiscntList = virtualUca.getUserDiscnts();
        for (int i = 0, size = userDiscntList.size(); i < size; i++)
        {
            DiscntTradeData userDiscnt = userDiscntList.get(i);
            DiscntTradeData delDiscntTD = userDiscnt.clone();
            delDiscntTD.setEndDate(sysdate);
            delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(virtualUca.getSerialNumber(), delDiscntTD);
        }
    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        DestroyFamilyReqData reqData = (DestroyFamilyReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        String ucaSn = uca.getSerialNumber();
        String sysdate = bd.getRD().getAcceptTime();
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        String strRemark = "亲亲网一键注销";
        
        IDataset relaList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("45", uca.getUserId(), "1");
        if (IDataUtil.isEmpty(relaList))
        {
            // 查询不到主卡的亲亲关系
            CSAppException.apperr(FamilyException.CRM_FAMILY_84);
        }

        String virtualSn = relaList.getData(0).getString("SERIAL_NUMBER_A");
        String userIdA = relaList.getData(0).getString("USER_ID_A");
        IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);

        if (IDataUtil.isEmpty(virtualUser))
        {
            // 没有找到虚拟用户
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }

        // 如果不是正常用户则报错
        String removeTag = virtualUser.getString("REMOVE_TAG");
        if (!StringUtils.equals(removeTag, "0"))
        {
            // 亲亲网已被注销，请确认再办理
            CSAppException.apperr(FamilyException.CRM_FAMILY_98);
        }

        // 终止虚拟用户资料
        delVirtualUser(bd, virtualSn);// 这个方法后续看能否公用

        // 处理UU关系
        IDataset mebList = RelaUUInfoQry.getAllMebByUSERIDA(userIdA, "45");
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData mebData = mebList.getData(i);
            String sn = mebData.getString("SERIAL_NUMBER_B");
            
            //如果成员不存在则继续
            IData user = UcaInfoQry.qryUserInfoBySn(sn);
            if (IDataUtil.isEmpty(user))
				continue;
            
            UcaData mebUca = UcaDataFactory.getNormalUca(sn);
            mebUca.setAcctTimeEnv();// 分散账期

            // 校验成员未完工工单限制 ----start----
            IData data = new DataMap();
            data.put("TRADE_TYPE_CODE", tradeTypeCode);
            data.put("USER_ID", mebUca.getUserId());
            data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
            data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
            data.put("BRAND_CODE", "");
            FamilyTradeHelper.checkMemberUnfinishTrade(data);
            // 校验成员未完工工单限制 ----end----

            RelationTradeData delMebRelTradeData = new RelationTradeData(mebData);
            delMebRelTradeData.setRsrvStr2(delMebRelTradeData.getShortCode());
            delMebRelTradeData.setShortCode("");
            delMebRelTradeData.setEndDate(sysdate);// 改为立即失效 by yanwu
            delMebRelTradeData.setRsrvTag1("1");
            delMebRelTradeData.setRsrvStr1(mebUca.getCustomer().getCustName());
            delMebRelTradeData.setRemark(strRemark);
            delMebRelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            bd.add(ucaSn, delMebRelTradeData);

            // 处理服务
            List<SvcTradeData> userSvcList = mebUca.getUserSvcs();
            for (int j = 0, listSize = userSvcList.size(); j < listSize; j++)
            {
                SvcTradeData userSvc = userSvcList.get(j);
                if (userIdA.equals(userSvc.getUserIdA()))
                {
                    SvcTradeData delSvcTD = userSvc.clone();
                    delSvcTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    delSvcTD.setEndDate(sysdate);
                    delSvcTD.setRemark(strRemark);
                    bd.add(mebUca.getSerialNumber(), delSvcTD);
                }
            }

            // 处理优惠
            List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
            for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
            {
                DiscntTradeData userDiscnt = userDiscntList.get(j);
                if (userIdA.equals(userDiscnt.getUserIdA()))
                {
                    DiscntTradeData delDiscntTD = userDiscnt.clone();
                    delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    //delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());// 改为下账期失效 by zhouwu
                    delDiscntTD.setEndDate(sysdate);// 改为立即失效 by yanwu
                    delDiscntTD.setRemark(strRemark);
                    bd.add(mebUca.getSerialNumber(), delDiscntTD);
                }
            }

            // 处理可选优惠
            IDataset commparaList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", mebUca.getUserEparchyCode());
            for (int j = 0, listSize = userDiscntList.size(); j < listSize; j++)
            {
                DiscntTradeData userDiscnt = userDiscntList.get(j);
                for (int k = 0, commparaSize = commparaList.size(); k < commparaSize; k++)
                {
                    IData commpara = commparaList.getData(k);
                    if (commpara.getString("PARAM_CODE", "").equals(userDiscnt.getElementId()))
                    {
                        DiscntTradeData delDiscntTD = userDiscnt.clone();
                        delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        //delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());// 改为下账期失效 by zhouwu
                        delDiscntTD.setEndDate(sysdate);// 改为立即失效 by yanwu
                        delDiscntTD.setRemark(strRemark);
                        bd.add(mebUca.getSerialNumber(), delDiscntTD);
                        break;
                    }
                }

            }
        }

        // 处理CUST_FAMILYMEB
        String homeCustId = virtualUser.getString("CUST_ID");
        IDataset custFamilyMebList = CustFamilyMebInfoQry.getAllFamilyMember(homeCustId);
        if (IDataUtil.isNotEmpty(custFamilyMebList))
        {
            for (int i = 0, size = custFamilyMebList.size(); i < size; i++)
            {
                IData custFamilyMeb = custFamilyMebList.getData(i);
                CustFamilyMebTradeData delCustFamilyMebTD = new CustFamilyMebTradeData(custFamilyMeb);
                delCustFamilyMebTD.setRemoveDate(sysdate);
                delCustFamilyMebTD.setRemoveTag("1");
                delCustFamilyMebTD.setModifyTag(BofConst.MODIFY_TAG_DEL);

                bd.add(ucaSn, delCustFamilyMebTD);
            }
        }
    }

}
