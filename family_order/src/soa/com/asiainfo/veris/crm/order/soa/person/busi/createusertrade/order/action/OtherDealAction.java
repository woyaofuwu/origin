
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

/**
 * 如果其他元素不为空则拼其他元素串 记录到other表
 * 
 * @author sunxin
 */
public class OtherDealAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        IDataset otherList = new DatasetList();
        otherList.add(createPersonUserRD.getOtherList());
        UcaData uca = btd.getRD().getUca();
        if (IDataUtil.isEmpty(otherList))
        {
            return;
        }
        for (int i = 0; i < otherList.size(); i++)
        {
            IData otherData = otherList.getData(i);
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setUserId(uca.getUserId());
            otherTradeData.setRsrvValueCode("SB");
            otherTradeData.setRsrvValue(otherData.getString("ELEMENT_ID"));
            otherTradeData.setStartDate(SysDateMgr.getSysTime());
            otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());

            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }

    }

}
