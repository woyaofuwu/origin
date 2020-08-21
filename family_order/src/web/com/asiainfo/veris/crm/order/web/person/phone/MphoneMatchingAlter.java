
package com.asiainfo.veris.crm.order.web.person.phone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MphoneMatchingAlter extends PersonBasePage
{
    public void alterClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MphoneMatchingSVC.alterMphone", data);

        int tag = result.getInt("TAG");
        if (tag != 1)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "号码匹配出现异常！");
        }

        setAjax(data);
    }

    public void checkClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        // IData inparam = new DataMap();
        String serialNumberB = data.getString("SERIAL_NUMBER_B");
        data.put("SERIAL_NUMBER", serialNumberB);

        IDataset result = CSViewCall.call(this, "SS.MphoneMatchingSVC.checkMphone", data);

        this.setInfos(result);
    }

    public void queryClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.MphoneMatchingSVC.queryMphoneMatch", data, getPagination());
        IData result = new DataMap();
        if (dataset == null || dataset.size() == 0)
        {
            result.put("RESULT_INFO", "查询不到可以进行匹配的新号码！请联系信息技术中心！");
            result.put("QUERY_TAG", "false");
        }
        else
        {
            this.setInfos(dataset);
            result.put("QUERY_TAG", "true");
        }
        setAjax(result);
    }

    public void refreshClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data = CSViewCall.callone(this, "SS.MphoneMatchingSVC.queryMphoneBySerialNumberA", data);

        this.setCondition(data);

    }

    public abstract void setCondition(IData idata);

    public abstract void setEditList(IDataset list);

    public abstract void setInfos(IDataset serviceList);

    public abstract void setSelectList(IDataset list);

}
