
package com.asiainfo.veris.crm.order.web.person.bat.batextractnoactivation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatExtractNoActivationInfo extends PersonBasePage
{
    /**
     * 执行号码激活情况信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryExtractnoactivationInfo(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        Pagination page = getPagination("pageNav");
        // getMofficeBySN(pd,inparam.getString("SERIAL_NUMBER",""));
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.QueryExtractNoActivationInfoSVC.queryExtractnoactivationInfo", inputData, page);

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
