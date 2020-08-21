
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.table.tablegrid;

import ognl.Ognl;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeDB;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.FieldTypeSetDBImpl;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.ListDataSourceDB;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.AIResult;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.AIResultFactory;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class TableGrid extends BaseTable
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-20 上午11:43:11 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-20 chengxf2 v1.0.0 修改原因
     */
    private String getDisplayValue(FieldTypeDB tmpField, AIResult aiResult) throws Exception
    {
        String expression = tmpField.getDisplayValue();
        return (String) Ognl.getValue(expression, aiResult, String.class);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-14 下午05:30:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
     */
    private String getFieldTypeSet() throws Exception
    {
        IData fieldTypeSetMap = new DataMap();
        IDataset fieldList = new DatasetList();
        fieldTypeSetMap.put("Name", fieldTypeSet.getName());
        fieldTypeSetMap.put("MainField", fieldTypeSet.getMainField());
        fieldTypeSetMap.put("FieldList", fieldList);
        IData tmpField = null;
        FieldTypeDB[] cols = this.fieldTypeSet.getFieldList();
        for (int i = 0; i < cols.length; i++)
        {
            tmpField = new DataMap();
            tmpField.put("N", cols[i].getName());
            tmpField.put("T", cols[i].getTitle());
            tmpField.put("Ed", cols[i].getDefaultEditType().getName());
            if (cols[i].getListDataSource() != null)
            {
                tmpField.put("DN", cols[i].getName()); // 设置数据源名称
            }
            fieldList.add(tmpField);
        }
        return fieldTypeSetMap.toString();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-3 上午09:01:02 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 chengxf2 v1.0.0 修改原因
     */
    private String getTableDataSource() throws Exception
    {
        IDataset dataSourceList = new DatasetList();
        FieldTypeDB[] cols = this.fieldTypeSet.getFieldList();
        for (int i = 0; i < cols.length; i++)
        {
            ListDataSourceDB listDataSource = cols[i].getListDataSource();
            if (listDataSource != null)
            {
                IData dataSource = new DataMap();
                IDataset paramList = new DatasetList();
                dataSource.put("Name", cols[i].getName());
                dataSource.put("Title", cols[i].getTitle());
                dataSource.put("Listener", listDataSource.getListener());
                dataSource.put("ValueAttr", listDataSource.getValueAttr());
                dataSource.put("TextAttr", listDataSource.getTextAttr());
                dataSource.put("ListParameter", paramList);
                int paraCount = listDataSource.getParaCount();
                for (int j = 0; j < paraCount; j++)
                {
                    IData paraMap = new DataMap();
                    paraMap.put("Name", listDataSource.getParaName(j));
                    paraMap.put("Value", listDataSource.getParaValue(j));
                    paramList.add(paraMap);
                }
                dataSourceList.add(dataSource);
            }
        }
        return dataSourceList.toString();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-3-31 上午11:52:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
     */
    private IDataset getTableGridDataList(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        IDataset tableDataList = new DatasetList();
        Pagination pagin = getContext().getPagination();
        pagin.setPageSize(Integer.parseInt(this.getPageSize()));
        if (isAjax || isInitial())
        { // ajax请求的时候调用服务获取数据
            IData data = this.getRequestData();
            IDataOutput output = CSViewCall.callPage(this, getServiceName(), data, pagin);
            pagin.setCount(output.getDataCount());// 设置总数量
            tableDataList = output.getData();
        }
        else
        {
            pagin.setCount(0);// 设置总数量
        }
        this.setPagin(pagin);
        return tableDataList;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:35:06 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderBodyElement(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        StringBuilder bodyElement = new StringBuilder();
        bodyElement.append("<tbody>");
        IDataset tableDataList = getTableGridDataList(writer, cycle); // 获得表格数据
        AIResult aiResult = AIResultFactory.getInstance(tableDataList, this.getPage());
        boolean hasDBTreeField = isHasDBTreeField();
        int dataRowCount = 0;
        while (aiResult.next())
        {
            dataRowCount += 1;
            bodyElement.append(renderTRElement(dataRowCount, hasDBTreeField, aiResult));
        }
        bodyElement.append("</tbody>");
        return bodyElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:33:43 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        this.fieldTypeSet = new FieldTypeSetDBImpl();
        cycle.setAttribute("FieldTypeSetDB", this.fieldTypeSet);
        renderBody(writer, cycle); // 渲染子组件
        if (this.fieldTypeSet.getFieldCount() > 0)
        {
            String script = "$.tableManager.get('" + this.getTableId() + "');";
            if (isAjax)
            {
                includeScript(writer, "scripts/csserv/component/table/TableGrid.js");
                includeScript(writer, "scripts/csserv/component/table/FieldType.js");
                includeScript(writer, "scripts/csserv/component/table/DataSource.js");
                addScriptContent(writer, script);
            }
            else
            {
                getPage().addResAfterBodyBegin("scripts/csserv/component/table/TableGrid.js");
                getPage().addResAfterBodyBegin("scripts/csserv/component/table/FieldType.js");
                getPage().addResAfterBodyBegin("scripts/csserv/component/table/DataSource.js");
                getPage().addScriptBeforeBodyEnd(getTableId() + "_init", "$(function(){\r\n" + script + "\r\n});");
            }
            StringBuilder divElement = renderScrollElement(writer, cycle);
            this.setDivElement(divElement.toString());
            if (isFootDisplay())
            { // 如果需要分页Bar
                renderNavBar(writer, cycle);
            }
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
     * @throws Exception
     * @date: 2014-4-3 上午08:59:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderDataSourceElement(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        StringBuilder dataSourceElement = new StringBuilder();
        dataSourceElement.append("<div id='TableGrid_DataSource_" + this.getTableId() + "'");
        dataSourceElement.append(" dataSource='" + getTableDataSource() + "'>\n");
        dataSourceElement.append("</div>");
        return dataSourceElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:33:56 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderDivElement(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        StringBuilder divElement = new StringBuilder();
        divElement.append("<div DBGridPK='");
        divElement.append(this.getTableId());
        divElement.append("' Name = 'TableGrid_" + this.getTableId() + "' id = 'TableGrid_" + this.getTableId() + "'");
        divElement.append(" class='c_table c_table-hover c_table-row-").append(this.getPageSize()).append("'");
        divElement.append(" DBTreeColName='" + getDBTreeColName() + "'");
        divElement.append(" FieldIDs='" + getHideFieldStr() + "'");
        if (this.isEditable())
        {
            divElement.append(" Edit='true'");
        }
        if (this.isRowSequence())
        {
            divElement.append(" isRowSequence=\"true\"");
        }
        if (this.isMultiSelect())
        {
            divElement.append(" isMutilSelect=\"true\"");
        }
        if (StringUtils.isNotEmpty(getOnInit()))
        {
            divElement.append(" S_OnInit ='" + this.getOnInit() + "'");
        }
        if (this.getOncellchange() != null)
        {
            divElement.append(" S_OnCellFocusChange =\"" + this.getOncellchange() + "\"");
        }
        if (this.getOnrowchange() != null)
        {
            divElement.append(" S_OnRowFocusChange =\"" + this.getOnrowchange() + "\"");
        }
        if (this.getOnfocusout() != null)
        {
            divElement.append(" S_OnFocusOut =\"" + this.getOnfocusout() + "\"");
        }
        if (this.getOnvalchange() != null)
        {
            divElement.append(" S_OnValueChange =\"" + this.getOnvalchange() + "\"");
        }
        if (this.getOncontextmenu() != null)
        {
            divElement.append(" S_OnContextMenu =\"" + this.getOncontextmenu() + "\"");
        }
        if (this.getOnrowselected() != null)
        {
            divElement.append(" S_OnRowSelected =\"" + this.getOnrowselected() + "\"");
        }
        if ((this instanceof BaseTable) && (((BaseTable) this).getOnrowclick() != null))
        {
            divElement.append(" S_OnRowClick =\"" + ((BaseTable) this).getOnrowclick() + "\"");
        }
        if (this.getOndblink() != null)
        {
            divElement.append(" S_OnDBLink =\"" + this.getOndblink() + "\"");
        }
        if (this.getOndbclick() != null)
        {
            divElement.append(" S_OnGridDbClick =\"" + this.getOndbclick() + "\"");
        }
        if (this.getOnresize() != null)
        {
            divElement.append(" S_OnResize =\"" + this.getOnresize() + "\"");
        }
        if ((this.getOntitledbclick() != null) && (!(this.getOntitledbclick().equals(""))))
        {
            divElement.append(" S_OnTitleDbClick =\"" + this.getOntitledbclick() + "\"");
        }
        divElement.append(">");
        divElement.append(renderDataSourceElement(writer, cycle));
        divElement.append(renderTableSetElement(writer, cycle));
        divElement.append(renderTableElement(writer, cycle));
        divElement.append("</div>");
        return divElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:34:34 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderHeadElement(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        StringBuilder headElement = new StringBuilder();
        headElement.append("<thead>");
        headElement.append("<tr>");
        if (this.isRowSequence())
        {
            headElement.append("<th width='24' class='e_center' >");
            headElement.append("序号");
            headElement.append("</th>");
        }
        if (this.isMultiSelect())
        {
            headElement.append("<th width='20' class='e_center' >");
            headElement.append("<input type='checkbox' onclick=\"TableGrid_allSelectChange('");
            headElement.append(this.getTableId());
            headElement.append("');\" />");
            headElement.append("</th>");
        }
        FieldTypeDB[] cols = fieldTypeSet.getFieldList();
        for (int i = 0; i < cols.length; i++)
        {
            FieldTypeDB fieldTypeDb = cols[i];
            if (fieldTypeDb.isGridVisible())
            {
                headElement.append("<th ");
                // headElement.append(fieldTypeDb.getTitle());
                headElement.append(" class='e_center' ");
                headElement.append(" FieldID='");
                headElement.append(fieldTypeDb.getName());
                headElement.append("' CanModify='");
                headElement.append(fieldTypeDb.getIsEnabled());
                headElement.append("' >");
                headElement.append("<span>").append(fieldTypeDb.getTitle()).append("</span>");
                headElement.append("</th>");
            }
        }
        headElement.append("</tr>");
        headElement.append("</thead>");
        return headElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-3-13 上午10:11:53 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderHiddenField(AIResult aiResult) throws Exception
    {
        StringBuilder hideColBuffer = new StringBuilder();
        FieldTypeDB[] cols = fieldTypeSet.getFieldList();
        for (int j = 0; j < cols.length; j++)
        {
            FieldTypeDB tmpField = cols[j];
            String name = tmpField.getName();
            String sIDValue = aiResult.getValue(name);
            if (!tmpField.isGridVisible())
            {
                hideColBuffer.append(" ").append(tmpField.getName()).append("='").append(sIDValue).append("'");
                String displayName = tmpField.getDisplayName();
                if (StringUtils.isNotEmpty(displayName))
                {
                    String sDisValue = aiResult.getValue(displayName);
                    if (StringUtils.isNotEmpty(sDisValue))
                    {
                        hideColBuffer.append(" ").append(tmpField.getName() + "_DISPLAY").append("='").append(sDisValue).append("'");
                    }
                }
            }
        }
        return hideColBuffer;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-3-28 下午10:25:04 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-28 chengxf2 v1.0.0 修改原因
     */
    private void renderNavBar(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String navId = "navbar_" + this.getTableId();
        this.setNavId(navId);
        String script = "$.initNavPage('" + navId + "','" + navId + "');";
        if (isAjax)
        {
            includeScript(writer, "scripts/csserv/component/table/NavBar.js");
            addScriptContent(writer, script);
        }
        else
        {
            getPage().addResAfterBodyBegin("scripts/csserv/component/table/NavBar.js");
            getPage().addScriptBeforeBodyEnd(navId + "_init", "$(function(){\r\n" + script + "\r\n});");
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
     * @date: 2014-3-28 上午11:44:54 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-28 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderScrollElement(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        StringBuilder scrollElement = new StringBuilder();
        scrollElement.append("<div id='Scroll_" + this.getTableId() + "'");
        String scroll = this.getScroll();
        if (StringUtils.isNotEmpty(scroll))
        {
            if ("X".equals(StringUtils.upperCase(scroll)))
            {
                scrollElement.append(" class='c_scroll c_scroll-x");
            }
            else
            {
                scrollElement.append(" class='c_scroll");
            }
            if (Integer.parseInt(this.getPageSize()) > 0)
            {
                scrollElement.append(" c_scroll-table-").append(this.getPageSize());
            }
            scrollElement.append("'");
        }
        scrollElement.append(">");
        scrollElement.append(renderDivElement(writer, cycle));
        scrollElement.append("</div>");
        return scrollElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 上午11:34:24 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderTableElement(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        StringBuilder tableElement = new StringBuilder();
        tableElement.append("<table id=");
        tableElement.append(this.getTableId());
        tableElement.append(" onclick=\"TableGrid_OnClick('");
        tableElement.append(this.getTableId());
        tableElement.append("')\" ondblclick=\"TableGrid_OnDbClick('");
        tableElement.append(this.getTableId());
        tableElement.append("')\" onfocusout=\"TableGrid_OnFocusOut('");
        tableElement.append(this.getTableId());
        tableElement.append("')\" >");
        tableElement.append(renderHeadElement(writer, cycle));
        tableElement.append(renderBodyElement(writer, cycle));
        tableElement.append("</table>");
        return tableElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-14 下午05:30:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderTableSetElement(IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        StringBuilder tableSetElement = new StringBuilder();
        tableSetElement.append("<div id='TableGrid_FieldTypeSet_" + this.getTableId() + "'");
        tableSetElement.append(" fieldTypeSet='" + getFieldTypeSet() + "'>\n");
        tableSetElement.append("</div>");
        return tableSetElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-3-13 上午10:18:12 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderTDElement(FieldTypeDB tmpField, AIResult aiResult) throws Exception
    {
        StringBuilder TDElement = new StringBuilder();
        String name = tmpField.getName(); // 字段名
        String sIDValue = aiResult.getValue(name);
        String sDisValue = aiResult.getValue(name);
        if (StringUtils.isEmpty(sIDValue))
        {
            TDElement.append("<td class='e_center' ");
            // TDElement.append(" title='" + name + "' ");
            TDElement.append(">");
            TDElement.append("</td>");
            return TDElement;
        }
        if (StringUtils.isNotBlank(tmpField.getDisplayValue()))
        {
            sDisValue = getDisplayValue(tmpField, aiResult);
        }
        if (isDBTreeField(tmpField))
        { // 树列
            TDElement.append(getDBTreeHTMLString(aiResult, sIDValue, sDisValue));
        }
        else if (isDBLinkField(tmpField))
        { // 链接列
            TDElement.append(getDBLinkHTMLString(tmpField, sIDValue));
        }
        else
        {
            TDElement.append("<td class='e_center'");
            // TDElement.append(" title='" + name + "' ");
            TDElement.append(" I='").append(sIDValue).append("'>");
            TDElement.append(sDisValue);
            TDElement.append("</td>");
        }
        return TDElement;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-3-13 上午10:01:08 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    private StringBuilder renderTRElement(int dataRowCount, boolean hasDBTreeField, AIResult aiResult) throws Exception
    {
        StringBuilder TRElement = new StringBuilder();
        if ((dataRowCount + 1) % 2 == 0)
        {
            TRElement.append("<tr class='' I='" + getPKValue(aiResult) + "'");
        }
        else
        {
            TRElement.append("<tr class='even' I='" + getPKValue(aiResult) + "'");
        }
        TRElement.append(" id='" + this.getTableId() + "_" + dataRowCount + "'");
        TRElement.append(renderHiddenField(aiResult)); // 隐藏字段存放方式
        if (hasDBTreeField)
        {
            TRElement.append(" isopen='true' child_list='" + aiResult.getChildRowIndexs() + "' level='" + aiResult.getLevel() + "' ");
        }
        TRElement.append(">");
        if (this.isRowSequence())
        {
            TRElement.append("<td class='e_center'>" + dataRowCount + "</td>");
        }
        if (this.isMultiSelect())
        {
            TRElement.append(" <td class=\"e_center\"><input AG=\"true\" class=\"GD-S-C\" type=\"checkbox\"/></td>");
        }
        FieldTypeDB[] cols = fieldTypeSet.getFieldList();
        for (int j = 0; j < cols.length; j++)
        {
            FieldTypeDB tmpField = cols[j];
            if (tmpField.isGridVisible())
            {
                TRElement.append(renderTDElement(tmpField, aiResult));
            }
        }
        TRElement.append("</tr>\n");
        return TRElement;
    }

}
