
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata.ProtectionInfoRequestData;

public class ProtectionInfoTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        // 插入SVC表动作
        dealSvcInfo(btd);

        // 插入Attr表动作
        dealAttrInfo(btd);

    }

    public void dealAttrInfo(BusiTradeData btd) throws Exception
    {
        ProtectionInfoRequestData reqData = (ProtectionInfoRequestData) btd.getRD();

        List<AttrTradeData> attrTradeDatas = null;
        // 处理开始结束时间
        List<SvcTradeData> newSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        for (SvcTradeData newSvcTradeData : newSvcTradeDatas)
        {
            if (BofConst.MODIFY_TAG_ADD.equals(newSvcTradeData.getModifyTag()))
            {
                AttrTradeData attrTradeData = new AttrTradeData();
                attrTradeData.setUserId(btd.getRD().getUca().getUserId());
                attrTradeData.setInstType("S");
                attrTradeData.setAttrCode("PROTECTION_INFO");// ATTR_CODE直接写死
                attrTradeData.setModifyTag(reqData.getFlag());
                attrTradeData.setInstId(SeqMgr.getInstId());
                attrTradeData.setAttrValue(reqData.getAttr_value()); // 详单密码，如果是取消需要怎么操作
                attrTradeData.setStartDate(SysDateMgr.getSysTime());
                attrTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                attrTradeData.setRelaInstId(newSvcTradeData.getInstId());

                btd.add(btd.getRD().getUca().getSerialNumber(), attrTradeData);
            }
            else
            {
                // 删除
                List<AttrTradeData> attrTradeDatalist = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
                int size = attrTradeDatalist.size();
                for (int i = 0; i < size; i++)
                {
                    AttrTradeData attrTradeData = attrTradeDatalist.get(i);
                    if (attrTradeData.getAttrCode().equals("PROTECTION_INFO"))
                    {
                        attrTradeData.setStartDate(reqData.getStart_date());
                        attrTradeData.setEndDate(SysDateMgr.getSysTime());
                        attrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    }
                }

            }

        }

    }

    public void dealSvcInfo(BusiTradeData btd) throws Exception
    {
        ProtectionInfoRequestData reqData = (ProtectionInfoRequestData) btd.getRD();
        SvcTradeData svcTradeData = new SvcTradeData();

        svcTradeData.setUserId(btd.getRD().getUca().getUserId());
        svcTradeData.setUserIdA("-1");
        svcTradeData.setProductId("-1");
        svcTradeData.setPackageId("-1");
        svcTradeData.setElementId("3313");
        svcTradeData.setMainTag("0");
        svcTradeData.setRsrvTag1("1");
        svcTradeData.setRemark("详单密码保护操作");
        svcTradeData.setModifyTag(reqData.getFlag());

        if ("0".equals(reqData.getFlag()))
        {
            svcTradeData.setInstId(SeqMgr.getInstId());
            svcTradeData.setStartDate(SysDateMgr.getSysTime());
            svcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        }
        else
        {
            svcTradeData.setInstId(reqData.getSvc_inst_id());
            svcTradeData.setStartDate(reqData.getStart_date());
            svcTradeData.setEndDate(SysDateMgr.getSysTime());
        }

        btd.add(btd.getRD().getUca().getSerialNumber(), svcTradeData);

    }

}
