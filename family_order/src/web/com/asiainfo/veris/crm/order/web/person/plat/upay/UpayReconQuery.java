
package com.asiainfo.veris.crm.order.web.person.plat.upay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import com.ailk.org.apache.commons.lang3.StringUtils;

public abstract class UpayReconQuery extends PersonBasePage
{

    /**
     * 和包电子券对账查询
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public void queryUpayRecon(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData("cond", true);
        IDataOutput result = CSViewCall.callPage(this, "SS.UpayReconQuerySVC.queryUpayRecon", param, this.getPagination("PaginBar"));

        this.setInfos(result.getData());
        this.setPaginCount(result.getDataCount());
        this.setCond(this.getData("cond", true));
    }
    
    /**
     * 批量返销
     * 
     * @param cycle
     * @throws Exception
     */
    public void runCancelTrade(IRequestCycle cycle) throws Exception
    {
        String tradeIds = getParameter("TRADE_IDS");
        String[] tradeIdSet = StringUtils.split(tradeIds, ",");
        for (int i = 0, size = tradeIdSet.length; i < size; i++)
        {
            String tradeId = tradeIdSet[i];
            IData data = new DataMap();
            data.put("TRADE_ID", tradeId);
            data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            data.put("UPAY_CANCEL_SCORE", "1");

            try{
                IDataset resultset = CSViewCall.call(this, "SS.CancelTradeSVC.cancelTradeReg", data);
        		IData result = (resultset != null && resultset.size() > 0) ? resultset.getData(0):new DataMap() ;
        	    if (StringUtils.isNotBlank(result.getString("ORDER_ID")) && !"-1".equals(result.getString("ORDER_ID"))){
        	    	data.put("DEAL_TAG", "1");
        	    	data.put("RSRV_STR1", "返销成功");
        	    	data.put("RSRV_STR2", SysDateMgr.getSysDate("yyyy-MM-dd"));
        	    }
			}catch(Exception ex){
				data.put("DEAL_TAG", "2");
				String errInfo = ex.getMessage();
    	    	if(errInfo.length()>500){
					errInfo=errInfo.substring(0,500);
				}
    	    	data.put("RSRV_STR1", errInfo);
    	    	data.put("RSRV_STR2", SysDateMgr.getSysDate("yyyy-MM-dd"));
			}finally{
				CSViewCall.call(this, "SS.UpayReconQuerySVC.updateUpayRecon", data);
			}
        }
        String showInfo = "启动返销成功!";
        
        IData idata = new DataMap();
        idata.put("result", showInfo);
        setAjax(idata);
        queryUpayRecon(cycle);// 初始化页面
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long paginCount);
}
