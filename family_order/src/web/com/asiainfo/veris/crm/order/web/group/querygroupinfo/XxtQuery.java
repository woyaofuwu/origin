
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class XxtQuery extends CSBasePage
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
     * @Description: 校讯通异网号码互查, 根据原来的 ChenTest.java文件写的
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();
        String snA = condData.getString("cond_SERIAL_NUMBER_A");
        String snB = condData.getString("cond_SERIAL_NUMBER_B");

        if (StringUtils.isNotEmpty(snB))
        {
            IData param = new DataMap();
            param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            param.put("SERIAL_NUMBER", snB);
            param.put("REMOVE_TAG", "0");

            IData userInfo = CSViewCall.callone(this, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", param);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_573, snB);
            }
        }

        IData param = new DataMap();
        param.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        param.put("SERIAL_NUMBER_A", snB);
        param.put("SERIAL_NUMBER_B", snA);
        IDataset relations = CSViewCall.call(this, "CS.RelaXXTInfoQrySVC.queryXxtInfoBySnaOrSnb", param);
        for (int i = 0; i < relations.size(); i++)
        {
            IData data = relations.getData(i);

            param.clear();
            param.put("USER_ID", data.getString("EC_USER_ID"));
            IData ecInfo = CSViewCall.callone(this, "CS.GrpInfoQrySVC.queryGrpProuctByUserId", param);

            if (IDataUtil.isNotEmpty(ecInfo))
            {
                data.put("GROUP_ID", ecInfo.getString("GROUP_ID"));
                data.put("CUST_NAME", ecInfo.getString("CUST_NAME"));
            }
        }

        setInfos(relations);
        setCondition(condData);
        setInfoCount(relations.size());

        if (IDataUtil.isEmpty(relations))
        {
            setHintInfo("没有符合条件的查询结果,请核对你要查询的内容！");
        }
        else
        {
            setHintInfo("查询成功！");
        }
    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);

}
