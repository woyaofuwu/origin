
package com.asiainfo.veris.crm.order.web.person.sundryquery.greencell;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryGreenCellInfo extends PersonBasePage
{

    /**
     * 初始化界面提示信息
     * 
     * @param cycle
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // setHintInfo("要求【起始、终止】日期时间段不能超过31天~");
        IData inputData = this.getData();
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.QueryGreenCellInfoSVC.queryInitInfo", inputData);
        if (result != null && result.size() > 1)
        {
            this.setGreenCards(result.getDataset(0));
            this.setAreas(result.getDataset(1));
        }
    }

    /**
     * 执行绿色田野卡基站信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryGreenCellInfo(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        // getMofficeBySN(pd,inparam.getString("SERIAL_NUMBER",""));
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.QueryGreenCellInfoSVC.queryGreenCellInfo", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        setPageCount(result.getDataCount());
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(this.getData("cond"));
    }

    public abstract void setAreas(IDataset areas);

    public abstract void setCondition(IData cond);

    public abstract void setGreenCards(IDataset greenCards);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

}
