
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.query;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class QueryGroupUser extends CSBasePage
{

    public void qryGroupUserInfoByCustId(IRequestCycle cycle) throws Exception
    {
        String custId = getData().getString("CUST_ID");

        IDataset grpUserList = UCAInfoIntfViewUtil.qryGrpUserInfoByCustId(this, custId, false);

        setInfoList(grpUserList);
    }

    public abstract void setInfoList(IDataset grpUserlist);
}
