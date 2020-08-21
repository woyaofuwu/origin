
package com.asiainfo.veris.crm.order.web.person.sundryquery.specqry;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryUserPostInfo extends PersonBasePage
{

    /**
     * 邮寄内容处理
     * 
     * @param postContent
     * @return
     * @throws Exception
     */
    private String processPostContent(String postContent) throws Exception
    {
        StringBuilder sb = new StringBuilder("");
        if (postContent != null && postContent.length() >= 1)
        {
            String str1 = pageutil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(0, 1));
            sb.append(str1 != null && sb.length() == 0 ? str1 : "");
        }
        if (postContent != null && postContent.length() >= 2)
        {
            String str2 = pageutil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(1, 2));
            sb.append(str2 != null && sb.length() != 0 ? "," + str2 : "");
        }
        if (postContent != null && postContent.length() >= 3)
        {
            String str3 = pageutil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(2, 3));
            sb.append(str3 != null && sb.length() != 0 ? "," + str3 : "");
        }
        if (postContent != null && postContent.length() >= 4)
        {
            String str4 = pageutil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(3));
            sb.append(str4 != null && sb.length() != 0 ? "," + str4 : "");
        }
        return sb.toString();
    }

    /**
     * 邮寄方式处理
     * 
     * @param postTypeSet
     * @return
     * @throws Exception
     */
    private String processPostTypeSet(String postTypeSet) throws Exception
    {
        StringBuilder sb = new StringBuilder("");
        if (postTypeSet != null && postTypeSet.length() >= 1)
        {
            String str1 = pageutil.getStaticValue("POSTINFO_POSTTYPESET", postTypeSet.substring(0, 1));
            sb.append(str1 != null && sb.length() == 0 ? str1 : "");
        }
        if (postTypeSet != null && postTypeSet.length() >= 2)
        {
            String str2 = pageutil.getStaticValue("POSTINFO_POSTTYPESET", postTypeSet.substring(1, 2));
            sb.append(str2 != null && sb.length() != 0 ? "," + str2 : "");
        }
        if (postTypeSet != null && postTypeSet.length() >= 3)
        {
            String str3 = pageutil.getStaticValue("POSTINFO_POSTTYPESET", postTypeSet.substring(2));
            sb.append(str3 != null && sb.length() != 0 ? "," + str3 : "");
        }
        return sb.toString();
    }

    /**
     * 执行绿色田野卡基站信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserPostInfo(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.QueryUserPostInfoSVC.queryUserPostInfo", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }

        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            // 邮寄内容处理
            String postContent = processPostContent(data.getString("POST_CONTENT", ""));
            data.put("POST_CONTENT_TEXT", postContent);
            // 邮寄方式处理
            String postTypeset = processPostTypeSet(data.getString("POST_TYPESET", ""));
            data.put("POST_TYPESET_TEXT", postTypeset);

            if (!this.hasPriv("SYS012"))
            {
                data.put("POST_ADDRESS", "***");
                data.put("EMAIL", "***");
            }
        }

        setPageCount(result.getDataCount());
        // 设置页面返回数据
        setInfos(dataset);
    }

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

}
