
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.plat;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class PlatOrderAdd extends CSBizTempComponent
{
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setCond(null);
        this.setPlatSvcList(null);
        this.setShowAllCancel(null);
        this.setShowSwitch(null);
        this.setOnlyDisplayOrder(null);
        this.setShowKeyBusiness(null);
    }

    public abstract IDataset getInitPlatSvcs();

    public abstract String getShowSwitch();

    @Override
    public void renderComponent(StringBuilder stringbuilder, IMarkupWriter writer, IRequestCycle irequestcycle) throws Exception
    {
        this.getPage().addResBeforeBodyEnd("scripts/csserv/component/plat/platorderadd.js");
        IData param = this.getPage().getData();
        if (IDataUtil.isNotEmpty(this.getInitPlatSvcs()))
        {
            this.setPlatSvcList(this.getInitPlatSvcs());
            this.setOnlyDisplayOrder("true");
            return;
        }

        if (param.getString("TABSET", "").equals("ORDER_AREA"))
        {
            this.setCond(param);
            if ("".equals(param.getString("COND", "")))
            {
                return;
            }
            else
            {
                try
                {
                    SearchResponse resp = SearchClient.search("PM_OFFER_PLATSVC", param.getString("COND"), 0, 50);
                    // int numTotalHits = resp.getNumTotalHits(); // 鍖归厤鎬绘暟
                    IDataset datas = resp.getDatas(); // 褰撳墠杩斿洖缁撴灉锛屽垎椤靛悗鐨勭粨鏋�
                    this.setPlatSvcList(datas);
                }
                catch (Exception e)
                {

                }

            }
        }
        else if (param.getString("TABSET", "").equals("ALL_CANCEL_AREA"))
        {
            IDataset allCancels = StaticUtil.getStaticList("PLAT_ALL_CANCEL");
            this.getPage().setAjax(allCancels);
            this.setRenderContent(false);
        }
        else if (param.getString("TABSET", "").equals("SWITCH_AREA"))
        {
            IDataset staticList = StaticUtil.getStaticList("PLAT_SWITCH");
            IDataset switches = new DatasetList();
            IData map = new DataMap();
            map.put("USER_ID", param.getString("USER_ID"));
            map.put(Route.ROUTE_EPARCHY_CODE, param.getString(Route.ROUTE_EPARCHY_CODE));
            IDataset userSwitch = CSViewCall.call(this, "CS.PlatComponentSVC.getPlatSwitch", map);

            int size = staticList.size();
            for (int i = (size - 1); i >= 0; i--)
            {
                IData switchData = staticList.getData(i);
                int uSize = userSwitch.size();
                String isClose = "false";
                for (int j = 0; j < uSize; j++)
                {
                    IData userSwitchData = userSwitch.getData(j);
                    if (userSwitchData.getString("SERVICE_ID").equals(switchData.getString("DATA_ID")))
                    {
                        isClose = "true";
                        break;
                    }
                }
                switchData.put("IS_CLOSE", isClose);

                switches.add(switchData);
            }
            this.getPage().setAjax(switches);
            this.setRenderContent(false);
        }
        else if (param.getString("TABSET", "").equals("KEYBUSINESS_AREA"))
        {
        	IData map = new DataMap();
        	IDataset dataset = CSViewCall.call(this, "CS.PlatComponentSVC.getKeyBusiness", map);
        	this.setPlatSvcList(dataset);
            this.getPage().setAjax(dataset);
            this.setRenderContent(false);
        }
    }

    public abstract void setCond(IData cond);

    public abstract void setInitPlatSvcs(IDataset initPlatSvcs);

    public abstract void setOnlyDisplayOrder(String onlyDisplayOrder);

    public abstract void setPlatSvcList(IDataset platSvcList);

    public abstract void setShowAllCancel(String showAllCancel);

    public abstract void setShowSwitch(String showSwitch);
    
    public abstract void setShowKeyBusiness(String showKeyBusiness);

}
