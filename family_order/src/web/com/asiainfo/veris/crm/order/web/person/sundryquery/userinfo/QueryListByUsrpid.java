
package com.asiainfo.veris.crm.order.web.person.sundryquery.userinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryListByUsrpid extends PersonBasePage
{
    /**
     * 客户号码清单查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryListByUsrpid(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset userset = CSViewCall.call(this, "SS.QueryListByUsrpidSVC.qryAllUserAndBrandInfoByPsptId", data);
        if (IDataUtil.isEmpty(userset))
        {
            setAjax("DATA_COUNT", "0");
            return;
        }
        DataHelper.sort(userset, "OPEN_DATE", 2, 1);
        setCustInfos(userset);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfos(IDataset custInfos);

    public abstract void setPageCount(long pageCount);

}
