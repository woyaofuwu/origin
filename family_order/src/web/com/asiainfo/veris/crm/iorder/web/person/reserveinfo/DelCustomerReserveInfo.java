package com.asiainfo.veris.crm.iorder.web.person.reserveinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DelCustomerReserveInfo extends PersonBasePage {
	
	/**
     * 查询方法
     *
     * @param cycle
     * @throws Exception
     */
    public void queryCustomerReserveInfo(IRequestCycle cycle) throws Exception {
        IData data = getData("cond");
        IDataOutput dataset = CSViewCall.callPage(this, "SS.CustomerReserveInfoSVC.queryCustomerReserveInfo", data, getPagination("pagin"));
        IDataset customerinfolist = dataset.getData();
        data.put("NUM", customerinfolist.size());
        setAjax(data);
        setCount(dataset.getDataCount());
        setCustomerInfos(customerinfolist);
    }

    /**
     * 删除方法
     *
     * @param cycle
     * @throws Exception
     */
    public void deleteCustomerReserveInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.UopInterface.delPostInfo", data);
        setAjax(dataset.getData(0));
    }
    
    //public abstract void setCondition(IData condition);

    public abstract void setCustomerInfos(IDataset infos);

    public abstract void setCount(long count);

    //public abstract void setRowIndex(int rowIndex);

    //public abstract void setOperTagSet(IDataset operTagSet);
}
