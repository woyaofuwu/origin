
package com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.villagework.VillageWorkBean;
import com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.requestdata.VillageWorkData;
import com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.requestdata.VillageWorkRequestData;

public class VillageWorkTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 插入other表动作
        VillageWorkRequestData reqData = (VillageWorkRequestData) btd.getRD();
        List<VillageWorkData> vInfos = reqData.getSerNumInfo();
        for (VillageWorkData vInfo : vInfos)
        {
            insertOther(btd, vInfo);
            insertOtherServ(btd, vInfo);
        }

    }

    public void insertOther(BusiTradeData btd, VillageWorkData vInfo) throws Exception
    {
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setUserId(btd.getRD().getUca().getUserId());
        otherTradeData.setRsrvValueCode("A7");
        otherTradeData.setRsrvValue(btd.getRD().getUca().getUserId());

        otherTradeData.setRsrvStr1(vInfo.getSERIAL_NUMBER());

        otherTradeData.setStartDate(btd.getRD().getAcceptTime());
        String tag = vInfo.getFlag();
        if ("0".equals(tag))
        {
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        }
        else if ("1".equals(tag))
        {
            otherTradeData.setEndDate(btd.getRD().getAcceptTime());
        }
        otherTradeData.setModifyTag(tag);
        otherTradeData.setInstId(SeqMgr.getInstId());

        btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
    }

    public void insertOtherServ(BusiTradeData btd, VillageWorkData vInfo) throws Exception
    {
        IDataset otherList = new DatasetList();
        IData otherServTradeData = new DataMap();
        String tag = vInfo.getFlag();
        VillageWorkBean bean = BeanManager.createBean(VillageWorkBean.class);
        if ("0".equals(tag))
        {
            otherServTradeData.put("USER_ID", btd.getRD().getUca().getUserId());
            otherServTradeData.put("SERVICE_MODE", "A7");
            otherServTradeData.put("SERIAL_NUMBER", vInfo.getSERIAL_NUMBER());
            otherServTradeData.put("PROCESS_INFO", "村通工程特殊号码");
            otherServTradeData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            otherServTradeData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
            otherServTradeData.put("PROCESS_TAG", "0");
            otherServTradeData.put("START_DATE", btd.getRD().getAcceptTime());
            otherServTradeData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            otherServTradeData.put("PARTITION_ID", btd.getRD().getUca().getUserId().substring(btd.getRD().getUca().getUserId().length() - 4));
            otherServTradeData.put("INST_ID", SeqMgr.getInstId());
            otherList.add(otherServTradeData);
            bean.insertUserOtherServInfo(otherList);
        }
        else if ("1".equals(tag))
        {
            otherServTradeData.put("PROCESS_TAG", "1");
            otherServTradeData.put("END_DATE", btd.getRD().getAcceptTime());
            otherServTradeData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            otherServTradeData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
            otherServTradeData.put("USER_ID", btd.getRD().getUca().getUserId());
            otherServTradeData.put("SERVICE_MODE", "A7");
            otherServTradeData.put("SERIAL_NUMBER", vInfo.getSERIAL_NUMBER());
            Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "UPD_BY_USERID_SERIAL", otherServTradeData);
        }

    }

}
