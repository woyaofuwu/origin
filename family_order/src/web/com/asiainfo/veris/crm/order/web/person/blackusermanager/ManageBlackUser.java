/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.blackusermanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-6-19 修改历史 Revision 2014-6-19 下午05:41:41
 */
public abstract class ManageBlackUser extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        // 查询出黑名单信息 显示页面
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset blackUserList = CSViewCall.call(this, "SS.BlackUserManageSVC.qryBlackUserList", data);

        this.setInfos(blackUserList);

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.BlackUserManageSVC.blackUserManagement", data);

        // 成功
        if ("0".equals(dataset.getData(0).getString("RESULT")))
        {
            String faildNumber = dataset.getData(0).getString("FAILD_NUMBER", "");
            if ("".equals(faildNumber))
            {
            }
            else
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "号码：" + faildNumber + "因为没有用户资料，设置黑名单失败！");
            }
        }
        // 失败
        else
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "黑名单设置失败，请重试！");
        }
        setAjax(dataset);
    }

    public abstract void setInfos(IDataset infos);

}
