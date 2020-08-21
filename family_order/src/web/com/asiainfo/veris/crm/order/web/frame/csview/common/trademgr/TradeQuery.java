
package com.asiainfo.veris.crm.order.web.frame.csview.common.trademgr;

import java.util.StringTokenizer;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TradeQuery extends PersonBasePage
{
    public void initPage(IRequestCycle cycle) throws Exception
    {
        String routeIds = "0898,cen";

        StringTokenizer st = new StringTokenizer(routeIds, ",", false);
        IDataset ids = new DatasetList();

        while (st.hasMoreElements())
        {
            String routeId = st.nextToken();
            IData route = new DataMap();
            route.put("ROUTE_ID", routeId);
            ids.add(route);
        }

        this.setRouteList(ids);
    }

    public void qryErrorMsg(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();

        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataset error = CSViewCall.call(this, "CS.TradeQuerySVC.qryErrorMsg", data);

        String errorMsg = "未找到错误日志!";

        if (IDataUtil.isNotEmpty(error))
        {
            String msg = error.getData(0).getString("ERR");

            errorMsg = msg;
        }

        this.setErrInfos(errorMsg);
    }

    public void queryTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond");

        IDataset tradeinfos = CSViewCall.call(this, "CS.TradeQuerySVC.queryTradeInfo", data);
        if (IDataUtil.isNotEmpty(tradeinfos))
        {

            this.setTradeinfos(tradeinfos);

            this.setErrInfos(null);
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setErrInfos(String errInfos);

    public abstract void setRouteList(IDataset list);

    public abstract void setRowIndex(int index);

    public abstract void setTradeinfo(IData tradeInfo);

    public abstract void setTradeinfos(IDataset tradeInfos);

    public void tradePfAgain(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();

        CSViewCall.call(this, "CS.TradeQuerySVC.tradePfAgain", data);

        String msg = "工单状态修改成功,等待AEE完工!";

        this.setErrInfos(msg);
    }
    
    public void checkSubscribeType(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	String resultCode = "0";
    	IData info = new DataMap();
    	info.put("TYPE_ID", "PBOSS_SUBSCRIBE_TYPE");
    	info.put("PDATA_ID", data.getString("SUBSCRIBE_TYPE"));
    	IDataset pbossFlags = CSViewCall.call(this, "CS.StaticInfoQrySVC.getStaticListByTypeIdPDataId", info);
    	if(IDataUtil.isNotEmpty(pbossFlags)){
    		resultCode = pbossFlags.getData(0).getString("DATA_ID");
    		if(StringUtils.isEmpty(resultCode)){
    			resultCode = "0";
    		}
    	}
    	info.put("RESULT_CODE", resultCode);
    	setAjax(info);
    }
}
