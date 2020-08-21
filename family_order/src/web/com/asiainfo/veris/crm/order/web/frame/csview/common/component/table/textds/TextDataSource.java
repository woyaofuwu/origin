/**
 * 
 */

package com.asiainfo.veris.crm.order.web.frame.csview.common.component.table.textds;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizComponent;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeDBImpl;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.TextDataSourceDB;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.TextDataSourceDBImpl;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DisplayValue.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-2 上午10:39:21 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-2 chengxf2 v1.0.0 修改原因
 */

public abstract class TextDataSource extends BizComponent
{

    public abstract String getMethodName();

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-2 上午10:40:52 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-2 chengxf2 v1.0.0 修改原因
     */
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {
        FieldTypeDBImpl tmpFieldDB = (FieldTypeDBImpl) cycle.getAttribute("FieldTypeDB");
        cycle.setAttribute("DataSourceType", "Text");
        TextDataSourceDB textDataSource = new TextDataSourceDBImpl();
        textDataSource.setMethodName(getMethodName());
        tmpFieldDB.setTextDataSource(textDataSource);
        // cycle.setAttribute("DataSource", textDataSource);
        renderBody(writer, cycle); // 渲染子组件
    }
}
