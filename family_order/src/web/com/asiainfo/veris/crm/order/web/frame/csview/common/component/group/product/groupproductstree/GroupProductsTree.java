
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.groupproductstree;

import org.apache.log4j.Logger;
import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.web.BaseTempComponent;

public abstract class GroupProductsTree extends BaseTempComponent
{

    private static Logger logger = Logger.getLogger(GroupProductsTree.class);

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            setTreeParams(null);
        }
    }

    public abstract String getCheckboxAction();

    public abstract IDataset getDisabedProducts();

    public abstract String getEparchyCode();

    public abstract String getGroupId();

    public abstract String getGrpUserId();

    public abstract String getLimitProducts();

    public abstract String getLimitProductTypes();

    public abstract String getLimitType();

    public abstract String getMebUserId();

    public abstract String getMethod();

    public abstract String getNeedOrderedNode();

    public abstract String getParentTypeCode();

    public abstract String getRootName();

    public abstract String getTextAction();

    public abstract IData getTreeParams();

    public abstract boolean isAsync();

    public abstract boolean isInit();

    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
        {
            return;
        }
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/product/groupproductstree/GroupProductsTree.js");
        try
        {
            IData params = new DataMap();
            params.put("ROOT_NAME", getRootName() == null ? "" : getRootName());
            params.put("PARENT_TYPE_CODE", this.getParentTypeCode());
            params.put("MEB_USER_ID", getMebUserId() == null ? "" : getMebUserId());
            params.put("GROUP_ID", this.getGroupId() == null ? "" : getGroupId());
            params.put("EPARCHY_CODE", this.getEparchyCode() == null ? "" : getEparchyCode());
            params.put("CHECKBOX_ACTION", this.getCheckboxAction() == null ? "" : getCheckboxAction());
            params.put("TEXT_ACTION", this.getTextAction() == null ? "" : getTextAction());
            params.put("NEED_ORDERED_NODE", this.getNeedOrderedNode() == null ? "true" : getNeedOrderedNode());
            params.put("DISABLED_PRODUCTS", getDisabedProducts() == null ? new DatasetList() : getDisabedProducts());
            params.put("GRP_USER_ID", getGrpUserId() == null ? "" : getGrpUserId());
            params.put("METHOD_NAME", this.getMethod());
            params.put("ASYNC_TAG", this.isAsync());
            params.put("LIMIT_TYPE", this.getLimitType() == null ? "" : getLimitType()); // 当limit_type=0为限制limit_products不展示。limit_type=1为只显示limit_products中的产品
            params.put("LIMIT_PRODUCTS", this.getLimitProducts() == null ? "" : getLimitProducts());
            params.put("LIMIT_PRODUCT_TYPES", this.getLimitProductTypes() == null ? "" : getLimitProductTypes());// 产品类型
            // 比如ADCG、MASG等

            IData treeparams = getTreeParams();
            if (treeparams != null)
                params.putAll(treeparams);
            setTreeParams(params);

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        super.renderComponent(writer, cycle);
    }

    public abstract void setLimitProducts(String limitProducts);

    public abstract void setLimitProductTypes(String limitProducts);

    public abstract void setLimitType(String limitTypes);

    public abstract void setTextAction(String textAction);

    public abstract void setTreeParams(IData treeparams);
}
