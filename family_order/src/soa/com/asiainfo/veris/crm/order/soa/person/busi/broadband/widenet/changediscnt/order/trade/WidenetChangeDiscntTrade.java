
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt.order.requestdata.WidenetChangeDiscntRequestData;

public class WidenetChangeDiscntTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        WidenetChangeDiscntRequestData reqData = (WidenetChangeDiscntRequestData) btd.getRD();
        createTradeDiscnt(btd, reqData);
        if (StringUtils.isNotBlank(reqData.getSpcDelDiscntFee()) && StringUtils.isNotBlank(reqData.getSpcDelDiscntPaymentId()))
        {
            String[] delDiscntsFee = reqData.getSpcDelDiscntFee().split(",");
            String[] delDiscntsPayId = reqData.getSpcDelDiscntPaymentId().split(",");
            for (int j = 0; j < delDiscntsFee.length && StringUtils.isNotBlank(delDiscntsFee[j]) && !"0".equals(delDiscntsFee[j]); j++)
            {
                OtherFeeTradeData otherFeeTD = new OtherFeeTradeData();
                otherFeeTD.setUserId(reqData.getUca().getUserId());
                otherFeeTD.setAcctId(reqData.getUca().getAcctId());
                otherFeeTD.setUserId2(reqData.getUca().getUserId());
                otherFeeTD.setAcctId2(reqData.getUca().getAcctId());
                otherFeeTD.setInDepositCode("0");
                otherFeeTD.setOutDepositCode(delDiscntsPayId[j]);
                otherFeeTD.setOperFee(delDiscntsFee[j]);
                otherFeeTD.setPaymentId("0");
                otherFeeTD.setOperType(BofConst.OTHERFEE_CAMPUS_TRANS);
                otherFeeTD.setStartDate(SysDateMgr.getSysDate());
                otherFeeTD.setEndDate(SysDateMgr.getTheLastTime());
                btd.add(reqData.getUca().getSerialNumber(), otherFeeTD);

                OtherTradeData otherTradeData = new OtherTradeData();
                otherTradeData.setUserId(reqData.getUca().getUserId());
                otherTradeData.setRsrvValueCode("CAMPUS_TRANS_FEE");
                if ("1".equals(reqData.getCampusTag()))
                {
                    otherTradeData.setRsrvValue("1");
                    otherTradeData.setRemark("校园宽带退订后费用转移");
                }
                else if ("2".equals(reqData.getCampusTag()))
                {
                    otherTradeData.setRsrvValue("2");
                    otherTradeData.setRemark("校园宽带变更后费用转移");
                }
                otherTradeData.setInstId(SeqMgr.getInstId());
                otherTradeData.setStartDate(SysDateMgr.getSysDate());
                otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
                otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                otherTradeData.setRsrvStr1(reqData.getUca().getUserId());
                otherTradeData.setRsrvStr2(reqData.getUca().getUserId());
                otherTradeData.setRsrvStr3(reqData.getUca().getAcctId());
                otherTradeData.setRsrvStr4(reqData.getUca().getAcctId());
                otherTradeData.setRsrvStr7(delDiscntsPayId[j]);
                otherTradeData.setRsrvStr8("0");
                otherTradeData.setRsrvStr9(delDiscntsFee[j]);
                otherTradeData.setRsrvStr10(delDiscntsFee[j]);
                otherTradeData.setRsrvStr11(getVisit().getStaffEparchyCode());
                otherTradeData.setRsrvStr12(getVisit().getCityCode());
                otherTradeData.setRsrvStr13(getVisit().getStaffId());
                otherTradeData.setRsrvStr14(getVisit().getDepartId());
                otherTradeData.setRsrvStr15(getVisit().getProvinceCode());
                btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
            }
        }
    }

    private void createTradeDiscnt(BusiTradeData<BaseTradeData> btd, WidenetChangeDiscntRequestData reqData) throws Exception
    {

        IDataset spcUserDiscnts = reqData.getSpcUserDiscnts();
        for (int i = 0; i < spcUserDiscnts.size(); i++)
        {
            IData spcUserDiscnt = spcUserDiscnts.getData(i);
            String deferTag = spcUserDiscnt.getString("DEFER_TAG");
            DiscntTradeData newDiscnt = new DiscntTradeData();
            newDiscnt.setUserId(reqData.getUca().getUserId());
            newDiscnt.setUserIdA("-1");
            newDiscnt.setProductId("-1");
            newDiscnt.setPackageId("-1");
            newDiscnt.setElementId(spcUserDiscnt.getString("DISCNT_CODE", ""));
            // 新增优惠
            if ("A".equals(deferTag))
            {
                newDiscnt.setInstId(SeqMgr.getInstId());
                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
            }
            // 延期优惠
            else if ("B".equals(deferTag))
            {
                newDiscnt.setInstId(spcUserDiscnt.getString("INST_ID"));
                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
            }
            // 删除优惠
            else if ("D".equals(deferTag))
            {
                newDiscnt.setInstId(spcUserDiscnt.getString("INST_ID"));
                newDiscnt.setModifyTag(BofConst.MODIFY_TAG_DEL);
            }

            newDiscnt.setSpecTag("0");
            newDiscnt.setStartDate(spcUserDiscnt.getString("START_DATE"));
            newDiscnt.setEndDate(spcUserDiscnt.getString("END_DATE"));
            btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
        }

    }

}
