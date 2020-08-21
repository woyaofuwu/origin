package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class MyContactTab extends PersonBasePage {

    /**
     * 客户资料综合查询 - 接触信息查询
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData inParam = getData();
        IDataset svcRequestInfos = new DatasetList();
        IDataset custCustomerInfos = new DatasetList();
        IDataset relaTradeInfos = new DatasetList();

        String serialNumber = inParam.getString("SERIAL_NUMBER", "");
        if (StringUtils.isNotBlank(serialNumber)) {
            // 查询服务请求
            svcRequestInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryThServerInfos", inParam);
            // 查询客户接触
            custCustomerInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryTHCustomerContactInfo", inParam);
            // 查询相关工单
            relaTradeInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryTHRelaTradeInfo", inParam);
        }
        setSrInfos(svcRequestInfos);
        setCcInfos(custCustomerInfos);
        setRtInfos(relaTradeInfos);
    }

    public abstract void setSrInfos(IDataset srInfos);

    public abstract void setCcInfos(IDataset ccInfos);

    public abstract void setRtInfos(IDataset rtInfos);
}
