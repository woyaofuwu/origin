
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.pageset;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BmFrameException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.query.bm.BmServiceFactory;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PageSet.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-14 下午02:41:46 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-14 chengxf2 v1.0.0 修改原因
 */
public abstract class PageSet extends BizTempComponent
{

    public abstract String getFrameId();

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 下午03:30:27 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    private String getInitScript(IData pageInfo) throws Exception
    {
        StringBuilder initScript = new StringBuilder(200);
        String pageId = pageInfo.getString("PAGE_ID");
        initScript.append("<script language=\"javascript\" type=\"text/javascript\">\r\n");
        initScript.append("$(function(){\r\n debugger;\r\n");
        initScript.append("gPageIdList[gPageIdList.length]='").append(pageId).append("';\r\n");
        IDataset pageRowSetList = BmServiceFactory.getPageDataSetList(this, pageId);
        for (int i = 0; i < pageRowSetList.size(); i++)
        {
            IData pageRowSet = pageRowSetList.getData(i);
            String datasetType = pageRowSet.getString("DATASET_TYPE");
            String datasetKey = pageRowSet.getString("DATASET_KEY");
            String datasetMethod = pageRowSet.getString("DATASET_METHOD");
            initScript.append("var objPageSet = new PageDataSet();\r\n");
            initScript.append("objPageSet.dataSetType='").append(datasetType).append("';\r\n");
            initScript.append("objPageSet.dataSetKey='").append(datasetKey).append("';\r\n");
            initScript.append("objPageSet.dataSetMethod='").append(datasetMethod).append("';\r\n");
            if (i == 0)
            {
                initScript.append("g_SoFrame_objPageSetsArray['").append(pageId).append("'] = new PageDataSets();\r\n");
            }
            initScript.append("g_SoFrame_objPageSetsArray['").append(pageId).append("'].addPageDataSet(objPageSet);\r\n");
        }
        initScript.append("});\r\n");
        initScript.append("\r\n</script>");
        return initScript.toString();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 下午03:24:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    private String getPageFrameId(IData pageInfo)
    {
        return "FRAME_" + pageInfo.getString("PAGE_ID");
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 下午03:26:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    private String getPageParams(IData pageInfo)
    {
        StringBuilder paramsBuf = new StringBuilder(100);
        String pageRuleSetId = pageInfo.getString("PAGE_RULESET_ID");
        if (StringUtils.isNotBlank(pageRuleSetId))
        {
            paramsBuf.append("&PAGE_RULESET_ID=").append(pageRuleSetId);
        }
        paramsBuf.append("&PAGE_FRAME_ID=").append(pageInfo.getString("PAGE_FRAME_ID"));
        paramsBuf.append("&FRAME_ID=").append(getFrameId());
        return paramsBuf.toString();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-19 下午03:24:09 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-19 chengxf2 v1.0.0 修改原因
     */
    private String getPageTemplateId(IData pageInfo)
    {
        return "TEMPLATE_" + pageInfo.getString("PAGE_ID");
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-13 下午03:29:57 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-13 chengxf2 v1.0.0 修改原因
     */
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle)
    {
        try
        {
            String frameId = getFrameId();
            IData frameInfo = BmServiceFactory.getFrameInfo(this, frameId);
            if (frameInfo.isEmpty())
            {
                CSViewException.apperr(BmFrameException.CRM_BM_1, frameId);
            }
            String pageSetId = frameInfo.getString("PAGESET_ID");
            IDataset pageSetPageList = BmServiceFactory.getPageSetPageList(this, pageSetId);
            for (int i = 0; i < pageSetPageList.size(); i++)
            {
                IData pageInfo = pageSetPageList.getData(i);
                pageInfo.put("PAGE_TEMPLATE_ID", getPageTemplateId(pageInfo));
                pageInfo.put("PAGE_FRAME_ID", getPageFrameId(pageInfo));
                pageInfo.put("PARAMS", getPageParams(pageInfo));
                pageInfo.put("INIT_SCRIPT", getInitScript(pageInfo));
            }
            setPageList(pageSetPageList);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public abstract void setPageInfo(IData rolePageInfo);

    public abstract void setPageList(IDataset rolePageList);

}
