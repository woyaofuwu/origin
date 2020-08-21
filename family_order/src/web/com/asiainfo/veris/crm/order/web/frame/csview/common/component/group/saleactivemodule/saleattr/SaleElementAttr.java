
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.saleactivemodule.saleattr;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SaleElementAttr extends CSBizTempComponent
{
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setAttr(null);
        this.setAttrs(null);
        this.setElementId(null);
        this.setItemIndex(null);
        this.setDisplayCondition(null);
        this.setConfirmHandler(null);
    }

    public abstract String getConfirmHandler();

    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/csserv/component/group/saleactivemodule/saleattr/saleelementattr.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/csserv/component/group/saleactivemodule/saleattr/saleelementattr.js");
        }
        IData param = this.getPage().getData();
        if (param.getString("ELEMENT_ID", "").equals("") || param.getString("ELEMENT_TYPE_CODE", "").equals(""))
        {
            return;
        }
        String dateStr = param.getString("DATES");
        IData dates = new DataMap(dateStr);
        String endEnableTag = param.getString("END_ENABLE_TAG");
        dates.put("END_ENABLE_TAG", endEnableTag);
        this.setInfo(dates);
        this.setElementId(param.getString("ELEMENT_ID"));
        this.setItemIndex(param.getString("ITEM_INDEX"));
        this.setDisplayCondition(param.getString("DISPLAY_CONDITION"));
        IDataset attrs = CSViewCall.call(this, "CS.SelectedElementSVC.getElementAttrs", param);
        if (attrs != null && attrs.size() > 0)
        {
            IDataset attrsResult = new DatasetList();
            IData tempAttr = null;
            int size = attrs.size();
            String attrCode = "";
            for (int i = 0; i < size; i++)
            {
                IData attr = attrs.getData(i);
                if (!attrCode.equals(attr.getString("ATTR_CODE")))
                {
                    if (attr.getString("ATTR_TYPE_CODE").equals("1") || attr.getString("ATTR_TYPE_CODE").equals("3") || attr.getString("ATTR_TYPE_CODE").equals("9") || attr.getString("ATTR_TYPE_CODE").equals("5"))
                    {
                        if (tempAttr != null)
                        {
                            attrsResult.add(tempAttr);
                        }
                        tempAttr = attr;
                        tempAttr.put("PARAMS", new DatasetList());
                        IData attrParam = new DataMap();
                        attrParam.put("ATTR_FIELD_NAME", attr.getString("ATTR_FIELD_NAME"));
                        attrParam.put("ATTR_FIELD_CODE", attr.getString("ATTR_FIELD_CODE"));
                        tempAttr.getDataset("PARAMS").add(attrParam);
                        attrCode = attr.getString("ATTR_CODE");
                        if (i == size - 1)
                        {
                            attrsResult.add(tempAttr);
                        }
                    }
                    else if (attr.getString("ATTR_TYPE_CODE").equals("8"))
                    {// 自定义弹出框提交的验证JS方法
                        String attrScript = attr.getString("SELFFUNC", "");
                        if (StringUtils.isNotBlank(attrScript))
                        {
                            setConfirmHandler(attrScript);
                        }
                    }
                    else
                    {
                        if (tempAttr != null)
                        {
                            attrsResult.add(tempAttr);
                            tempAttr = null;
                        }
                        attrsResult.add(attr);
                    }
                }
                else
                {
                    if (attr.getString("ATTR_TYPE_CODE").equals("1") || attr.getString("ATTR_TYPE_CODE").equals("3") || attr.getString("ATTR_TYPE_CODE").equals("9") || attr.getString("ATTR_TYPE_CODE").equals("5"))
                    {
                        IData attrParam = new DataMap();
                        attrParam.put("ATTR_FIELD_NAME", attr.getString("ATTR_FIELD_NAME"));
                        attrParam.put("ATTR_FIELD_CODE", attr.getString("ATTR_FIELD_CODE"));
                        tempAttr.getDataset("PARAMS").add(attrParam);
                        if (i == size - 1)
                        {
                            attrsResult.add(tempAttr);
                        }
                    }
                }
                attrCode = attr.getString("ATTR_CODE");
            }

            this.setAttrs(attrsResult);
            if (StringUtils.isBlank(getConfirmHandler()))
            {
                setConfirmHandler("saleactiveModule.confirmAttr(this.getAttribute('itemIndex'));");
            }
        }
    }

    public abstract void setAttr(IData attr);

    public abstract void setAttrs(IDataset attrs);

    public abstract void setConfirmHandler(String confirmHandler);

    public abstract void setDisplayCondition(String displayCondition);

    public abstract void setElementId(String elementId);

    public abstract void setInfo(IData info);

    public abstract void setItemIndex(String itemIndex);

}
