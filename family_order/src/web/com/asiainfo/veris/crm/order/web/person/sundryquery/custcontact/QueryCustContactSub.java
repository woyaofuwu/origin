
package com.asiainfo.veris.crm.order.web.person.sundryquery.custcontact;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryCustContactSub extends PersonBasePage
{

    /**
     * 查询服务请求/缴退费等信息
     * 
     * @param cycle
     * @throws Exception
     * @author chenhao 2009-3-13
     */
    public void queryCustContactSub(IRequestCycle cycle) throws Exception
    {

        IData inparam = getData();

        IDataset res = CSViewCall.call(this, "SS.QueryIntegrateCustContactSVC.getCustContactSubInfos", inparam);
        if (res == null || res.size() == 0)
        {
            // TODO
            // CSViewException.apperr(CrmCommException.CRM_COMM_685);
            // error("获取接触信息详细无数据");
        }
        else
        {
            setSubinfos(res);
            setCustcontactsub((IData) res.get(0));
        }
    }

    public abstract void setCustcontactsub(IData info);

    public abstract void setInfo(IData info);

    public abstract void setSubinfos(IDataset subinfos);
}
