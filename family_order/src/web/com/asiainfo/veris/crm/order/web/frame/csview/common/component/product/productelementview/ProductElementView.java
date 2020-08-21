
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.productelementview;

import java.util.Iterator;
import java.util.Map;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;

public abstract class ProductElementView extends BizComponent
{

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setModel(null);
    }

    public abstract String getClassName();

    public abstract String getHeadField();

    public abstract String getHeadText();

    public abstract Object getModel();

    public abstract String getShowFields();

    public abstract String getShowHeads();

    public abstract boolean isDisabled();

    public abstract boolean isInitHead();

    private String outElementTable(IDataset dataset, String productName, String[] headNames, String[] showFlds, String bodyName)
    {

        StringBuilder buffer = new StringBuilder();
        buffer.append("   <div class=\"c_box\">\n");
        buffer.append("   <div class=\"c_title\">\n");
        buffer.append("      <div class=\"text\">" + (productName == null || productName.equals("") ? "" : (productName + "-")) + getHeadText() + "</div>\n");
        buffer.append("   </div>\n");
        buffer.append("   <div class=\"c_table c_table-row-5\">\n");
        buffer.append("      <table>\n");
        buffer.append("         <thead>\n");
        buffer.append("	         <tr>\n");
        for (int col = 0; col < headNames.length; col++)
        {
            buffer.append("	        <th>" + headNames[col].trim() + "</th>\n");
        }
        buffer.append("	         </tr>\n");
        buffer.append("	      </thead>\n");
        buffer.append("	      <tbody id=\"" + bodyName + "_TBODY\"" + " name=\"" + bodyName + "_TBODY\"" + ">\n");
        for (int row = 0; row < dataset.size(); row++)
        {
            if (row % 2 == 0)
                buffer.append("	         <tr>\n");
            else
                buffer.append("	         <tr class=\"odd\">\n");
            IData map = (IData) dataset.get(row);
            for (int col = 0; col < showFlds.length; col++)
            {
                buffer.append("	    <td class=\"e_center\">" + map.getString(showFlds[col], "").trim() + "</td>\n");
            }
            buffer.append("	         </tr>\n");
        }
        buffer.append("	      </tbody>\n");
        buffer.append("      </table>\n");
        buffer.append("   </div>\n");
        buffer.append("   </div>\n");
        buffer.append("   <input type=\"text\" name=\"PRODUCTELEMENTS_VIEW_SHOW_FILEDS\"  id=\"PRODUCTELEMENTS_VIEW_SHOW_FILEDS\" style=\"display:none\" value=\"" + getShowFields() + "\"/>\n");
        return buffer.toString();
    }

    /**
     * render component
     * 
     * @param writer
     * @param cycle
     */
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle)
    {

        if (cycle.isRewinding())
            return;
        cycle.getPage().addResAfterBodyBegin("scripts/csserv/component/group/productelementview/productelementview.js");

        Object model = getModel();
        IDataset dataModel = null;
        String showHeads = getShowHeads();
        String showFields = getShowFields();
        String bodyName = getId();

        if (StringUtils.isBlank(showHeads))
            return;
        String[] headNames = showHeads.split(",");

        if (StringUtils.isBlank(showFields))
            return;
        String[] showFlds = showFields.split(",");

        if (StringUtils.isBlank(bodyName))
            bodyName = "PRODUCTELEMENTS_VIEW";

        if (model == null)
        {
            if (isInitHead())
            {
                model = new DatasetList();
            }
            else
            {
                return;
            }
        }

        if (model instanceof IDataset)
        {
            dataModel = (IDataset) model;
            writer.printRaw(outElementTable(dataModel, "", headNames, showFlds, bodyName));
        }
        else if (model instanceof IData)
        {
            IData map = (IData) model;
            Iterator it = map.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry entry = (Map.Entry) it.next();
                Object value = entry.getValue();
                IDataset dataset = (IDataset) value;
                String title = "";
                if (dataset != null && dataset.size() > 0)
                    title = ((IData) dataset.get(0)).getString(getHeadField(), "");
                writer.printRaw(outElementTable((IDataset) value, title, headNames, showFlds, bodyName));
            }
        }

        super.renderComponent(writer, cycle);
    }

    public abstract void setModel(Object object);
}
