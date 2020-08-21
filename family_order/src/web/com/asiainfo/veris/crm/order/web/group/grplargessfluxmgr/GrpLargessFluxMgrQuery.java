
package com.asiainfo.veris.crm.order.web.group.grplargessfluxmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpLargessFluxMgrQuery extends GroupBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract void setHintInfo(String infos);

    public abstract void setInfoCount(long infoCount);

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);
        this.setCondition(param);
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * 分页查询需要分配总流量
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData inputParam = getData("cond", true);
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", inputParam.getString("GRP_SERIAL_NUMBER"));
        //param.put("START_DATE", inputParam.getString("START_DATE"));
        //param.put("END_DATE", inputParam.getString("END_DATE"));
        
        IDataOutput dataOutput = CSViewCall.callPage(this, 
                   "SS.LargessFluxGrpMainSVC.qryUserGrpGfffInfo", param, getPagination("PageNav"));

        if (null != dataOutput && dataOutput.getData().size() > 0)
        {
            setHintInfo("查询成功~~！");
        }
        else
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        
        setCondition(inputParam);
        setInfos(dataOutput.getData());
        setInfoCount(dataOutput.getDataCount());
        
    }
    
}
