
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.usereasyprompt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserEasyPromptQry;

public class QueryUserEasyPromptBean extends CSBizBean
{

    /**
     * 功能：便捷服务意见查询 作者：GongGuang
     */
    public IDataset queryUserEasyPrompt(IData data, Pagination page) throws Exception
    {
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNumber1 = data.getString("SERIAL_NUMBER1", "");
        String serialNumber2 = data.getString("SERIAL_NUMBER2", "");
        String acceptDate1 = data.getString("ACCEPT_DATE1", "");
        String acceptDate2 = data.getString("ACCEPT_DATE2", "");
        String smsTypeCode = data.getString("SMS_TYPE_CODE", "");
        IDataset dataSet = QueryUserEasyPromptQry.queryUserEasyPrompt(serialNumber1, serialNumber2, acceptDate1, acceptDate2, smsTypeCode, routeEparchyCode, page);
        return dataSet;
    }
}
