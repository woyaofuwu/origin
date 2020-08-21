
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: PopuCommon.java
 * @Description: 批量业务平台业务-平台代码查询
 * @version: v1.0.0
 * @author: xiangyc
 * @date: 2014-3-6 上午9:56:49 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0 修改原因
 */

public abstract class PopuCommon extends PersonBasePage
{

    /**
     * @Function: initPlatQryPopup
     * @Description: 平台业务代码查询页面初始化方法
     * @param： IRequestCycle
     * @return：void
     * @throws：Exception
     * @version: v1.0.0
     * @author: xiangyc@asiainfo-linkage.com
     * @date: 上午10:00:29 2014-3-6 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0
     */
    public void initPlatQryPopup(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        String sIsPop = cond.getString("IS_POP", "");
        String paramBizTypeCode = cond.getString("PPP");
        if (StringUtils.isNotBlank(paramBizTypeCode))
        {
            setParamsA(paramBizTypeCode);
        }
        String tipInfo = "";
        if (StringUtils.isNotBlank(sIsPop) && "BATTASK_PLAT".equals(sIsPop))
        {
            setParams("YES");
            tipInfo = "请输入查询条件，然后双击选定的查询结果回到主界面！";
            setTipInfo(tipInfo);
        }
        queryPlatInfo(cycle);
    }

    /**
     * @Function: queryPlatInfo
     * @Description: 平台业务代码查询方法
     * @param： IRequestCycle
     * @return：void
     * @throws：Exception
     * @version: v1.0.0
     * @author: xiangyc@asiainfo-linkage.com
     * @date: 上午10:03:51 2014-3-6 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0
     */
    public void queryPlatInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        long getDataCount = 0;
        IData cond = new DataMap();
        String spCode = data.getString("SP_CODE");
        String spName = data.getString("SP_NAME");
        String bizTypeCode = data.getString("PARAM_BIZ_TYPE_CODE");
        if (StringUtils.isNotBlank(spCode))
        {
            cond.put("SP_CODE", spCode);
        }
        if (StringUtils.isNotBlank(spName))
        {
            cond.put("SP_NAME", spName);
        }
        if (StringUtils.isNotBlank(bizTypeCode))
        {
            cond.put("BIZ_TYPE_CODE", bizTypeCode);
        }else{
        	cond.put("BIZ_TYPE_CODE", data.getString("PPP",""));
        }
        IDataOutput output = CSViewCall.callPage(this, "CS.BatDealSVC.queryPlatInfo", cond, getPagination("taskNav"));
        if (output.getData() == null || output.getData().size() == 0)
        {
            setTipInfo("没有符合查询条件的数据！");
            return;
        }else
        {
            setTipInfo("双击任意一条查询结果返回！");
            getDataCount = Long.parseLong(output.getData().getData(0).getString("TOTAL"));
        }
        setSpInfos(output.getData());
        setQueryPlatListCount(getDataCount);
    }

    public abstract void setBatchOperTypes(IDataset set);

    public abstract void setCondition(IData info);

    public abstract void setParams(String params);

    public abstract void setParamsA(String params);

    public abstract void setParamsB(String params);

    public abstract void setQueryPlatListCount(long queryPlatListCount);

    public abstract void setSpInfo(IData spInfo);

    public abstract void setSpInfos(IDataset spInfos);

    public abstract void setTipInfo(String tipInfo);

}
