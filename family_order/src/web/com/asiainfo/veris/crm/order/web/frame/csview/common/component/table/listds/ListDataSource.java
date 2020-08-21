/**
 * 
 */

package com.asiainfo.veris.crm.order.web.frame.csview.common.component.table.listds;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizComponent;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeDBImpl;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.ListDataSourceDB;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.ListDataSourceDBImpl;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ListDataSource.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-2 上午10:47:54 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-2 chengxf2 v1.0.0 修改原因
 */

public abstract class ListDataSource extends BizComponent
{

    public abstract String getListener();

    public abstract String getTextAttr();

    public abstract String getValueAttr();

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-3 上午08:25:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 chengxf2 v1.0.0 修改原因
     */
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {
        FieldTypeDBImpl tmpFieldDB = (FieldTypeDBImpl) cycle.getAttribute("FieldTypeDB");
        cycle.setAttribute("DataSourceType", "List");
        ListDataSourceDB listDataSource = new ListDataSourceDBImpl();
        listDataSource.setListener(this.getListener());
        listDataSource.setValueAttr(this.getValueAttr());
        listDataSource.setTextAttr(this.getTextAttr());
        tmpFieldDB.setListDataSource(listDataSource);
        renderBody(writer, cycle);
    }
}
