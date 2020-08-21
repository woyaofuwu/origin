
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class ADCMemberQuery extends CSBasePage
{

    /**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description: ADC集团成员查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {

        IData condData = getData();
        String sn = condData.getString("cond_SERIAL_NUMBER");
        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("SERIAL_NUMBER", sn);
        param.put("REMOVE_TAG", "0");

        IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", param);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_573, sn);
        }

        param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("USER_ID", userInfo.getString("USER_ID"));
        IDataOutput dataOutput = CSViewCall.callPage(this, "SS.GroupInfoQuerySVC.qryADCMembersbyuser", param, getPagination());

        IDataset resultSet = dataOutput.getData();
        if (IDataUtil.isEmpty(resultSet))
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }

        setInfos(resultSet);
        setCondition(condData);
        setInfoCount(dataOutput.getDataCount());

    }

    public abstract void setCondition(IData condition);
    public abstract void setHintInfo(String hintInfo);
    public abstract void setInfoCount(long infoCount);
    public abstract void setInfos(IDataset infos);

}
