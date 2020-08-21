
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryDept extends GroupBasePage
{

    public abstract IData getInfo();

    /**
     * 作用：查询集团客户信息
     * 
     * @author luoy
     * @param cycle
     * @throws Throwable
     */
    public void queryDepts(IRequestCycle cycle) throws Throwable
    {

        IData params = new DataMap();
        IDataset result = null;
        IData conParams = getData("cond", true);
        String strQueryType = getParameter("QueryType");
        if (strQueryType.equals("0")) // 按渠道编码查询
        {
            params.put("DEPART_CODE", conParams.get("deptCode"));
        }
        else if (strQueryType.equals("1"))// 按用户渠道编码查询
        {
            params.put("USER_DEPART_CODE", conParams.get("userDeptCode"));
        }
        else if (strQueryType.equals("2"))// 按渠道名称模糊查询
        {
            params.put("DEPART_NAME", conParams.get("deptName"));
        }
        setInfos(result);
        setCondition(getData("cond", true));
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

}
