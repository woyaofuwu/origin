
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserChangeCardFlowInfoQry;

public class SelfChangeCardFlowBean extends CSBizBean
{
    public IDataset delFlowInfo(IData input) throws Exception
    {
        String transId = input.getString("TRANS_ID");
        String startDate = input.getString("START_DATE");
        String dealReason = input.getString("DEAL_REASON");
        int ins = UserChangeCardFlowInfoQry.updByTransId(dealReason, getVisit().getStaffId(), transId, startDate);
        int del = UserChangeCardFlowInfoQry.delByTransId(transId, startDate);
        IDataset set = IDataUtil.idToIds(input);
        return set;
    }

    public IDataset querySelfCard(IData input, Pagination page) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String serialNumberTemp = input.getString("SERIAL_NUMBER_TEMP");

        IDataset set = UserChangeCardFlowInfoQry.qryUserCardFlowInfoBySnAndTempSn(serialNumber, serialNumberTemp, page);
        return set;
    }

}
