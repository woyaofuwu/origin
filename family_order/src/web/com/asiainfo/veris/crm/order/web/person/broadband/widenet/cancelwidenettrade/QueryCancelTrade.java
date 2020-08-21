package com.asiainfo.veris.crm.order.web.person.broadband.widenet.cancelwidenettrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryCancelTrade extends PersonBasePage{
	
	public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);
    
    
    /**
     * 初始化界面提示信息
     * 
     * @param cycle
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // setHintInfo("要求【起始、终止】日期时间段不能超过31天~");
        IData inputData = this.getData();
        
        inputData.put("cond_START_DATE", SysDateMgr.getFirstDayOfThisMonth4WEB());
        inputData.put("cond_END_DATE", SysDateMgr.getSysDate());
        this.setCondition(inputData);
    }
	
    
    /**
     * 
     * 查询撤单数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCancelTradeInfoInfo(IRequestCycle cycle) throws Exception
    {

        IData inputData = this.getData("cond");
        inputData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        
        String startDate = inputData.getString("START_DATE", "");
        if (StringUtils.isNotBlank(startDate))
        {
            inputData.put("ACCEPT_MONTH", startDate.substring(5, 7));
        }
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.CancelWidenetTradeService.queryWidenetCancelTradeInfo", inputData, this.getPagination("pageNav"));
        
        IDataset dataset = result.getData();
        setPageCount(result.getDataCount());
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(this.getData("cond"));
    }
    
    

}
