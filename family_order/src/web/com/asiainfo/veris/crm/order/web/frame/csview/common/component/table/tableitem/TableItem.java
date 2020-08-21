
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.table.tableitem;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizComponent;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeDBImpl;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetDB;

public abstract class TableItem extends BizComponent
{

    public abstract String getDisplayValue();

    public abstract String getEditType();

    public abstract String getName();

    public abstract String getTitle();

    public abstract String getWidth();

    public abstract boolean isEditable();

    public abstract boolean isVisible();

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-2-17 下午04:26:22 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-2-17 chengxf2 v1.0.0 修改原因
     */
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {
        FieldTypeDBImpl tmpFieldDB = new FieldTypeDBImpl();
        tmpFieldDB.setName(this.getName()); // 列名
        tmpFieldDB.setTitle(this.getTitle()); // 列头
        tmpFieldDB.setIsEnabled(isEditable()); // 是否可编辑

        String editType = this.getEditType();
        if (StringUtils.isEmpty(editType))
        {
            editType = "DBEdit"; // 默认为DBEdit
        }
        tmpFieldDB.setDefaultEditType(editType);

        boolean isVisible = this.isVisible();
        if (!isVisible)
        {
            tmpFieldDB.setGridVisible(isVisible);
        }

        tmpFieldDB.setDisplayValue(this.getDisplayValue());

        FieldTypeSetDB fieldTypeSetDB = (FieldTypeSetDB) cycle.getAttribute("FieldTypeSetDB");
        fieldTypeSetDB.addField(tmpFieldDB);

        cycle.setAttribute("FieldTypeDB", tmpFieldDB);
        renderBody(writer, cycle); // 渲染子组件
    }
}
