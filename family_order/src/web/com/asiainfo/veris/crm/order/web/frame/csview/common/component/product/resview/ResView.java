
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.resview;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;

public abstract class ResView extends CSBizTempComponent
{

    private IData resData;

    private String resRecord = "[]";

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {

        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            resData = null;
            resRecord = null;
        }
    }

    public IData getResData()
    {

        return resData;
    }

    public abstract IDataset getResList();

    public String getResRecord()
    {

        return resRecord;
    }

    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        if (cycle.isRewinding())
            return;

        IDataset resList = getResList();
        if (IDataUtil.isNotEmpty(resList))
        {
            for (int i = 0, ilen = resList.size(); i < ilen; i++)
            {
                IData item = resList.getData(i);
                String resTypeCode = item.getString("RES_TYPE_CODE");
                if (StringUtils.isNotEmpty(resTypeCode))
                {
                    item.put("RES_TYPE", StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", resTypeCode));
                }
            }
        }

        this.setResRecord(resList == null ? "[]" : resList.toString());
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/product/resview/ResView.js");
    }

    public void setResData(IData resData)
    {

        this.resData = resData;
    }

    public void setResRecord(String resRecord)
    {

        this.resRecord = resRecord;
    }

}
