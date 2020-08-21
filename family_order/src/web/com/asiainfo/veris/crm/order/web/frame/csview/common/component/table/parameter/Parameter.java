/**
 * 
 */

package com.asiainfo.veris.crm.order.web.frame.csview.common.component.table.parameter;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizComponent;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeDBImpl;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.ListDataSourceDB;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.TextDataSourceDB;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: Parameter.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-4-2 上午10:40:13 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-4-2 chengxf2 v1.0.0 修改原因
 */

public abstract class Parameter extends BizComponent
{

    public abstract String getName();

    public abstract String getType();

    public abstract String getValue();

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
        String dataSourceType = cycle.getAttribute("DataSourceType").toString();
        if (StringUtils.equals("Text", dataSourceType))
        {
            TextDataSourceDB textDataSource = tmpFieldDB.getTextDataSource();
            textDataSource.addPara(getName(), getType(), getValue());
        }
        else
        {
            ListDataSourceDB listDataSource = tmpFieldDB.getListDataSource();
            listDataSource.addPara(getName(), getType(), getValue());
        }
    }
}
