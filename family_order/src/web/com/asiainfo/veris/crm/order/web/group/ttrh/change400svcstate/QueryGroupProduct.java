
package com.asiainfo.veris.crm.order.web.group.ttrh.change400svcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryGroupProduct extends GroupBasePage
{
    public abstract void setInfos(IDataset infos);

    public void queryGroupInfo(IRequestCycle cycle) throws Exception
    {
        String custId = getParameter("cond_CUST_ID");
        String productId = getParameter("cond_PRODUCT_ID");
        IDataset groupinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custId, productId);
        setInfos(groupinfo);
    }
}
