/**
 * 
 */

package com.asiainfo.veris.crm.order.web.frame.csview.common.component.table.tablegrid;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeDB;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetDB;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.AIResult;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DBGridBean.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 上午11:32:54 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public abstract class BaseTable extends BizTempComponent
{

    protected FieldTypeSetDB fieldTypeSet;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:37:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        if (!cycle.isRewinding())
        {
            fieldTypeSet = null;
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:35:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public int getColCount()
    {
        int showColCount = 0;
        FieldTypeDB[] cols = fieldTypeSet.getFieldList();
        for (int i = 0; i < cols.length; i++)
        {
            if (cols[i].isGridVisible())
            {
                showColCount += 1;
            }
        }
        if (this.isMultiSelect())
        {
            showColCount += 1;
        }
        if (this.isRowSequence())
        {
            showColCount += 1;
        }
        return showColCount;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-23 下午08:28:19 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-23 chengxf2 v1.0.0 修改原因
     */
    public String getDBLinkHTMLString(FieldTypeDB tmpField, String sIDValue)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<td ");
        buffer.append("I='").append(sIDValue).append("' ");
        buffer.append(">");
        buffer.append("<a href=\"javascript:void(0);\" onclick=\"TableGrid_OnDBLink('");
        buffer.append(getTableId()).append("','");
        buffer.append(tmpField.getName()).append("','");
        buffer.append(sIDValue).append("');\">");
        buffer.append(sIDValue).append("</a>");
        buffer.append("</td>");
        return buffer.toString();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:35:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public String getDBTreeColName() throws Exception
    {
        FieldTypeDB[] cols = fieldTypeSet.getFieldList();
        for (int j = 0; j < cols.length; j++)
        {
            FieldTypeDB tmpField = cols[j];
            if (isDBTreeField(tmpField))
            {
                return tmpField.getName();
            }
        }
        return "";
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:36:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public String getDBTreeHTMLString(AIResult aiResult, String sIDValue, String sDisValue)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<td ");
        buffer.append("I='").append(sIDValue).append("' ");
        buffer.append("level='").append(aiResult.getLevel()).append("' ");
        buffer.append("class='").append("level level-").append(aiResult.getLevel()).append(aiResult.isFold() ? " unfold" : " file").append("' ");
        buffer.append(">");
        buffer.append("<a class='ico' href='#nogo'></a>");
        buffer.append("<a class='text' href='#nogo'>").append(sDisValue).append("</a>");
        buffer.append("</td>");
        return buffer.toString();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 上午09:37:09 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    public String getHideFieldStr()
    {
        StringBuilder hideFieldStr = new StringBuilder();
        FieldTypeDB[] cols = fieldTypeSet.getFieldList();
        for (int j = 0; j < cols.length; j++)
        {
            FieldTypeDB tmpField = cols[j];
            if (!tmpField.isGridVisible())
            {
                if (hideFieldStr.length() > 0)
                {
                    hideFieldStr.append(",");
                }
                hideFieldStr.append(tmpField.getName());
            }
        }
        return hideFieldStr.toString();
    }

    public abstract String getNavId();

    public abstract String getOncellchange();

    public abstract String getOncontextmenu();

    public abstract String getOndbclick();

    public abstract String getOndblink();

    public abstract String getOnfocusout();

    public abstract String getOnInit();

    public abstract String getOnresize();

    public abstract String getOnrowchange();

    public abstract String getOnrowclick();

    public abstract String getOnrowselected();

    public abstract String getOntitledbclick();

    public abstract String getOnvalchange();

    public abstract String getPageSize();

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:37:11 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public String getPKValue(AIResult aiResult) throws Exception
    {
        FieldTypeDB[] pkFields = fieldTypeSet.getPkField();
        if ((pkFields != null) && (pkFields.length > 0))
        {
            FieldTypeDB pkField = pkFields[0];
            if (pkField != null)
            {
                return aiResult.getValue(pkField.getName());
            }
        }
        return "";
    }

    public abstract String getScroll();

    public abstract String getServiceName();

    public abstract String getTableId();

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:36:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public boolean isDBLink(FieldTypeDB tmpField)
    {
        String colEditType = tmpField.getDefaultEditType().getName();
        if (StringUtils.isNotEmpty(colEditType) && "DBLink".equals(colEditType))
        {
            return true;
        }
        return false;
    }

    public boolean isDBLinkField(FieldTypeDB tmpField)
    {
        String colEditType = tmpField.getDefaultEditType().getName();
        if (StringUtils.isNotEmpty(colEditType) && "DBLink".equals(colEditType))
        {
            return true;
        }
        return false;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:36:37 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public boolean isDBTreeField(FieldTypeDB tmpField)
    {
        String colEditType = tmpField.getDefaultEditType().getName();
        if (StringUtils.isNotEmpty(colEditType) && "DBTree".equals(colEditType))
        {
            return true;
        }
        return false;
    }

    public abstract boolean isEditable();

    public abstract boolean isFootDisplay();

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:35:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public boolean isHasDBTreeField()
    {
        FieldTypeDB[] cols = fieldTypeSet.getFieldList();
        for (int j = 0; j < cols.length; j++)
        {
            FieldTypeDB tmpField = cols[j];
            if (isDBTreeField(tmpField))
            {
                return true;
            }
        }
        return false;
    }

    public abstract boolean isInitial();

    public abstract boolean isMultiSelect();

    public abstract boolean isRowSequence();

    public abstract void setDivElement(String divElement);

    public abstract void setNavId(String navId);

    public abstract void setPagin(Pagination pagin);

}
