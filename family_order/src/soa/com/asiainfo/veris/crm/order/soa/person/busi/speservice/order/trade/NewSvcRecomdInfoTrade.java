
package com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata.NewSvcRecomdInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata.NewSvcRecomdInfoRequestData;

public class NewSvcRecomdInfoTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 插入other表动作
        NewSvcRecomdInfoRequestData reqData = (NewSvcRecomdInfoRequestData) btd.getRD();
        List<NewSvcRecomdInfoData> recomdInfos = reqData.getRecomdInfo();
        for (NewSvcRecomdInfoData recomdInfo : recomdInfos)
        {
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setUserId(btd.getRD().getUca().getUserId());
            otherTradeData.setRsrvValueCode("NEWSVCREC");
            otherTradeData.setRsrvValue("新业务推荐受理");

            otherTradeData.setRsrvStr1(btd.getRD().getUca().getSerialNumber());
            otherTradeData.setRsrvStr2(btd.getRD().getUca().getUser().getEparchyCode());
            otherTradeData.setRsrvStr3(recomdInfo.getRecomd_campn_id());
            otherTradeData.setRsrvStr4(recomdInfo.getRecomd_campn_name());
            otherTradeData.setRsrvStr5(recomdInfo.getRecomd_reply());

            otherTradeData.setRsrvStr6(recomdInfo.getRecomd_object_type_desc());
            otherTradeData.setRsrvStr7(recomdInfo.getRecomd_object_id());
            otherTradeData.setRsrvStr8("T000");
            otherTradeData.setRsrvStr9("");
            otherTradeData.setRsrvStr10(recomdInfo.getRecomd_refuse_remark());

            otherTradeData.setRsrvStr11(recomdInfo.getRecomd_refuse_reason_code());
            otherTradeData.setRsrvStr12(recomdInfo.getRecomd_object_type());
            otherTradeData.setRsrvStr13(recomdInfo.getRecomd_other_refuse_reason());
            otherTradeData.setRsrvStr14(recomdInfo.getRecomd_source_id());

            otherTradeData.setStartDate(btd.getRD().getAcceptTime());
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setStaffId(this.getVisit().getStaffId());
            otherTradeData.setDepartId(this.getVisit().getDepartId());

            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }

    }

}
