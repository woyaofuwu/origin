
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.product.pkgelementlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SelectedElements extends CSBizTempComponent
{
    private void buildSelectedElements(IDataset result)
    {
        if (result == null || result.size() <= 0)
        {
            return;
        }
        IDataset selectedElements = result.getData(0).getDataset("SELECTED_ELEMENTS");
        if (selectedElements == null || selectedElements.size() <= 0)
        {
            return;
        }
        IDataset svcList = new DatasetList();
        IDataset discntList = new DatasetList();
        int size = selectedElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = selectedElements.getData(i);
            element.put("ITEM_INDEX", i);
            if ("S".equals(element.getString("ELEMENT_TYPE_CODE")) || "Z".equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                svcList.add(element);
            }
            else if ("D".equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                discntList.add(element);
            }
        }
        this.setSvcList(svcList);
        this.setDiscntList(discntList);
    }

    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setDiscntList(null);
        this.setSvcList(null);
    }

    public abstract String getBasicCancelDateControlId();

    public abstract String getBasicStartDateControlId();

    public abstract String getCallAddElementSvc();

    public abstract String getInitCallSvc();

    public abstract IData getInitParam();

    public abstract String getRenderCallSvc();

    public abstract String getTradeTypeCode();

    @Override
    public void renderComponent(StringBuilder stringbuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/product/pkgelementlist/selectedelements.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/iorder/icsserv/component/product/pkgelementlist/selectedelements.js");
        }

        IData param = this.getPage().getData();
        if (param.getString("IS_RENDER", "").equals("true"))
        {
            if (this.getRenderCallSvc() == null || this.getRenderCallSvc().equals(""))
            {
                return;
            }
            param.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE"));
            param.put("SVC_NAME", this.getRenderCallSvc());
            IDataset result = CSViewCall.call(this, "CS.ProductComponentSVC.transmit", param);
            this.buildSelectedElements(result);
            this.setAcctDay(param.getString("ACCT_DAY", ""));
            this.setFirstDate(param.getString("FIRST_DATE", ""));
            this.setNextAcctDay(param.getString("NEXT_ACCT_DAY", ""));
            this.setNextFirstDate(param.getString("NEXT_FIRST_DATE", ""));
            if (StringUtils.isNotBlank(param.getString("TRADE_TYPE_CODE")))
            {
                this.setTradeTypeCode(param.getString("TRADE_TYPE_CODE"));
            }
            this.getPage().setAjax(result);
        }
        else
        {
            // 初始化方式
            if (this.getInitCallSvc() == null || this.getInitCallSvc().equals(""))
            {
                return;
            }
            if (IDataUtil.isNotEmpty(this.getInitParam()))
            {
                param.putAll(this.getInitParam());
            }
            param.put("SVC_NAME", this.getInitCallSvc());
            param.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE"));
            IDataset result = CSViewCall.call(this, "CS.ProductComponentSVC.transmit", param);
            this.setAcctDay(param.getString("ACCT_DAY", ""));
            this.setFirstDate(param.getString("FIRST_DATE", ""));
            this.setNextAcctDay(param.getString("NEXT_ACCT_DAY", ""));
            this.setNextFirstDate(param.getString("NEXT_FIRST_DATE", ""));
            this.buildSelectedElements(result);
            writer.printRaw("<script language=\"javascript\">\n");
            writer.printRaw("$(function(){\n");
            writer.printRaw("selectedElements.initSelectedElements('" + result + "');\n");
            writer.printRaw("});\n");
            writer.printRaw("</script>\n");
        }
        writer.printRaw("<script language=\"javascript\">\n");
        writer.printRaw("$(function(){\n");
        writer.printRaw("productEnv.setEnv('" + param.getString("EPARCHY_CODE", "") + "','" + param.getString("USER_ID", "-1") + "');\n");
        writer.printRaw("});\n");
        writer.printRaw("</script>\n");
    }

    public abstract void setAcctDay(String acctDay);

    public abstract void setBasicCancelDateControlId(String basicCancelDateControlId);

    public abstract void setBasicStartDateControlId(String basicStartDateControlId);

    public abstract void setElementExtendFiled(String elementExtendFiled);
    
    public abstract void setCallAddElementSvc(String callAddElementSvc);

    public abstract void setDiscntList(IDataset discntList);

    public abstract void setFirstDate(String firstDate);

    public abstract void setInitCallSvc(String initCallSvc);

    public abstract void setInitParam(IData initParam);

    public abstract void setNextAcctDay(String nextAcctDay);

    public abstract void setNextFirstDate(String nextFirstDate);

    public abstract void setRenderCallSvc(String renderCallSvc);

    public abstract void setSvcList(IDataset svcList);

    public abstract void setTradeTypeCode(String tradeTypeCode);
}
