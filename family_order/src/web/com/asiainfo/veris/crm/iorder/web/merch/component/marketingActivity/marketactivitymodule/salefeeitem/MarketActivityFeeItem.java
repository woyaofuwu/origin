
package com.asiainfo.veris.crm.iorder.web.merch.component.marketingActivity.marketactivitymodule.salefeeitem;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class MarketActivityFeeItem extends CSBizTempComponent
{

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
            setInfos(null);
    }

    public abstract String getEparchyCode();

    public abstract String getPackageId();

    public abstract String getProductId();

    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        addResourceFile(writer, "scripts/merch/marketingActivity/saleactivefeeitem/marketactivityfeeitem.js");
        renderComponentAction(informalParametersBuilder, writer, cycle);

    }

    private void renderComponentAction(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        IData retData = new DataMap();
        IData param = this.getPage().getData();
        setItemIndex(param.getString("ITEM_INDEX"));
        IDataset feeDataSet = new DatasetList();//duhj暂时屏蔽 CSViewCall.call(this, "SS.SaleActiveQuerySVC.getSaleFeeItemList", param);
        if (IDataUtil.isNotEmpty(feeDataSet))
        {

            IDataset FeeList = feeDataSet.getData(0).getDataset("FEE_LIST");
            IDataset giftFeeList = feeDataSet.getData(0).getDataset("GIFTFEE_LIST");
            FeeList.addAll(giftFeeList);
            if (FeeList.size() > 0)
            {
                retData.put("IS_FEE", "1");
                setFeeList(FeeList);
            }

            IDataset attrList = feeDataSet.getData(0).getDataset("ATTR_LIST");
            if (attrList.size() > 0)
            {
                retData.put("IS_ATTR", "1");
                // IDataset attrs = new DatasetList(attrList.getData(0));
                setAttrList(attrList);
            }
        }
        setCond(retData);
    }

    public abstract void setInfos(IDataset infos);

    public abstract void setCond(IData cond);

    public abstract void setItemIndex(String itemIndex);

    public abstract void setFeeList(IDataset feeList);

    public abstract void setAttrList(IDataset attrList);
}
