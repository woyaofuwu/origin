
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMebShortCodeData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyShortCodeBusiReqData;

public class FamilyShortCodeBusiTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        FamilyShortCodeBusiReqData reqData = (FamilyShortCodeBusiReqData) bd.getRD();
        List<FamilyMebShortCodeData> shortCodeDataList = reqData.getShortCodeDataList();
        String sysdate = reqData.getAcceptTime();
        UcaData uca = reqData.getUca();
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();

        IDataset relaList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("45", uca.getUserId(), "1");
        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_67);
        }

        String virtualSn = relaList.getData(0).getString("SERIAL_NUMBER_A");
        UcaData virtualUca = UcaDataFactory.getNormalUca(virtualSn);

        for (int i = 0, size = shortCodeDataList.size(); i < size; i++)
        {
            FamilyMebShortCodeData shortCodeData = shortCodeDataList.get(i);
            UcaData mebUca = shortCodeData.getUca();
            String newShortCode = shortCodeData.getNewShortCode();
            String oldShortCode = shortCodeData.getOldShortCode();

            // 校验成员未完工工单限制 ----start----
            IData data = new DataMap();
            data.put("TRADE_TYPE_CODE", tradeTypeCode);
            data.put("USER_ID", mebUca.getUserId());
            data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
            data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
            data.put("BRAND_CODE", "");
            FamilyTradeHelper.checkMemberUnfinishTrade(data);
            // 校验成员未完工工单限制 ----end----

            List<SvcTradeData> mebSvcs = mebUca.getUserSvcBySvcId("831");
            if (mebSvcs == null || mebSvcs.size() == 0)
            {
                SvcTradeData addSvcTD = new SvcTradeData();
                addSvcTD.setUserId(mebUca.getUserId());
                addSvcTD.setUserIdA(virtualUca.getUserId());
                addSvcTD.setProductId("99000001");
                addSvcTD.setPackageId("99850001");
                addSvcTD.setElementId("831");
                addSvcTD.setStartDate(sysdate);
                addSvcTD.setEndDate(SysDateMgr.getTheLastTime());
                addSvcTD.setMainTag("0");
                addSvcTD.setInstId(SeqMgr.getInstId());
                addSvcTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                addSvcTD.setRsrvStr1(mebUca.getSerialNumber());
                addSvcTD.setRsrvStr2(newShortCode);
                addSvcTD.setRsrvStr3(virtualUca.getSerialNumber());
                addSvcTD.setRsrvStr5(mebUca.getUserId());
                addSvcTD.setRsrvStr6(mebUca.getCustomer().getCustName());

                bd.add(mebUca.getSerialNumber(), addSvcTD);
            }
            else
            {
                SvcTradeData updSvcTd = mebSvcs.get(0).clone();
                updSvcTd.setRsrvStr4(updSvcTd.getRsrvStr2());
                updSvcTd.setRsrvStr2(newShortCode);
                updSvcTd.setModifyTag(BofConst.MODIFY_TAG_UPD);

                bd.add(mebUca.getSerialNumber(), updSvcTd);
            }

            IData relaData = RelaUUInfoQry.getRelaByPK(virtualUca.getUserId(), mebUca.getUserId(), "45");
            if (IDataUtil.isEmpty(relaData))
            {
                // 报错
                CSAppException.apperr(FamilyException.CRM_FAMILY_85);
            }
            RelationTradeData relaTD = new RelationTradeData(relaData);
            relaTD.setModifyTag(BofConst.MODIFY_TAG_UPD);
            relaTD.setShortCode(newShortCode);
            relaTD.setRsrvDate1(sysdate);
            relaTD.setRsrvStr1(oldShortCode);// 存放老短号 用于免填单 add by zhouwu 2014-08-28 10:02:55

            bd.add(uca.getSerialNumber(), relaTD);
        }

        List<SvcTradeData> userSvcs = virtualUca.getUserSvcBySvcId("830");
        if (userSvcs == null || userSvcs.size() == 0)
        {
            SvcTradeData addSvcTD = new SvcTradeData();
            addSvcTD.setUserId(virtualUca.getUserId());
            addSvcTD.setUserIdA(virtualUca.getUserId());
            addSvcTD.setProductId("99000001");
            addSvcTD.setPackageId("99850001");
            addSvcTD.setElementId("830");
            addSvcTD.setStartDate(sysdate);
            addSvcTD.setEndDate(SysDateMgr.getTheLastTime());
            addSvcTD.setMainTag("0");
            addSvcTD.setInstId(SeqMgr.getInstId());
            addSvcTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            addSvcTD.setRsrvStr1(virtualUca.getSerialNumber());

            bd.add(virtualUca.getSerialNumber(), addSvcTD);
        }
    }

}
