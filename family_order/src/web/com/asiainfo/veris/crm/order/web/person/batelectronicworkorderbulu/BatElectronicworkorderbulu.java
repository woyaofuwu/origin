
package com.asiainfo.veris.crm.order.web.person.batelectronicworkorderbulu;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatElectronicworkorderbulu extends PersonBasePage
{
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String today = pageutil.getSysDate();
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        if (StringUtils.equals(startDate, ""))
        {
            startDate = today;
        }

        if (StringUtils.equals(endDate, ""))
        {
            endDate = today;
        }
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("SERIAL_NUMBER", serialNumber);
        setCond(data);
        
        String tradeTypeCode = "";
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        input.put("PRT_TRADEFF_TAG", "1");
        IDataset undoTradeTypes = CSViewCall.call(this, "CS.TradeTypeInfoQrySVC.qryTradeTypeByEpachyCodeAndprtTradeTeeTag", input);
        if (IDataUtil.isNotEmpty(undoTradeTypes))
        {
            for (int i = 0, size = undoTradeTypes.size(); i < size; i++)
            {
                IData tempData = undoTradeTypes.getData(i);
                String tradeType = tempData.getString("TRADE_TYPE");
                tradeTypeCode = tempData.getString("TRADE_TYPE_CODE");
                tempData.put("TRADE_TYPE", "[" + tradeTypeCode + "]" + tradeType);
            }
            setTradeTypeCodeList(undoTradeTypes);
        }
        
    }
    
    /**
     * 纸质单据电子化信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryElectronicworkorder(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        Pagination page = getPagination("recordNav");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataOutput result = CSViewCall.callPage(this, "SS.BatElectronicworkorderbuluSVC.queryElectronicworkorder", data, page);
        setRecordCount(result.getDataCount());
        setInfos(result.getData());
        setCond(data);
    }
    /**
     * 纸质单据电子化，传到东软
     * 
     * @param cycle
     * @throws Exception
     */
    public void electronicworkorderbuluToDzh(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.BatElectronicworkorderbuluSVC.electronicworkorderbuluToDzh", data);
		if (IDataUtil.isNotEmpty(results) && results.size() > 0)
        {
            setAjax(results.getData(0));
        }
    }

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount);
    
    public abstract void setTradeTypeCodeList(IDataset tradeTypeCodeList);

}
