
package com.asiainfo.veris.crm.order.web.person.broadband.cttnet.cttpresentdiscnt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CttAddPresentDiscnt extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void querySendBackDiscnt(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataOutput result = null;
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        result = CSViewCall.callPage(this, "SS.CttPresentDiscntSVC.querySendBackDiscnt", data, getPagination("pageinfo"));

        setSendBackDiscnts(result.getData());
        setCount(result.getDataCount());
    }

    /**
     * 查询用户赠送优惠记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryUserPresentDiscnts(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset result = CSViewCall.call(this, "SS.CttPresentDiscntSVC.queryUserPresentDiscnts", data);

        this.setPresentDiscnts(result);
    }

    /**
     * 保存优惠记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void savePresentDiscnt(IRequestCycle cycle) throws Exception
    {

        IData param = getData();
        String temp = param.getString("presentInfos");
        IDataset presentInfos = new DatasetList(temp);

        if (presentInfos != null && !presentInfos.isEmpty())
        {
            for (int i = 0; i < presentInfos.size(); i++)
            {
                IData data = presentInfos.getData(i);
                data.putAll(param);
                data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
                data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                data.put("UPDATE_DEPART_ID", getVisit().getDepartId());

                String phoneType = data.getString("PHONE_TYPE", "");
                if ("固话号码".equals(phoneType)) // 转换赠送号码类型
                {
                    data.put("RSRV_STR1", "0");
                }
                else if ("移动号码".equals(phoneType))
                {
                    data.put("RSRV_STR1", "1");
                }

                if ("0".equals(data.getString("X_TAG", ""))) // 新增
                {
                    CSViewCall.call(this, "SS.CttPresentDiscntSVC.savePresentDiscnt", data);
                }
                if ("2".equals(data.getString("X_TAG", "")))// 修改
                {
                    CSViewCall.call(this, "SS.CttPresentDiscntSVC.updatePresentDiscnt", data);
                }
            }
        }
    }

    public abstract void setCond(IData cond);

    public abstract void setCount(long count);

    public abstract void setPresentDiscnts(IDataset dataset);

    public abstract void setSendBackDiscnts(IDataset dataset);
}
