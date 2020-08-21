
package com.asiainfo.veris.crm.order.web.person.sundryquery.plat.mdo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class MdoSvcQuery extends PersonQueryPage
{
    /**
     * @param cycle
     * @throws Exception
     */
    public void queryMdoSvcInfo(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        IData params = new DataMap();
        params.put("REMOVE_TAG", "0");// 根据号码查询正常用户信息
        params.put("SERIAL_NUMBER", condParams.getString("SERIAL_NUMBER", ""));
        IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySnNoProduct", params);
        if (null != userInfos && userInfos.size() > 0)
        {
            // 查询客户信息
            params.clear();
            String custName = "";
            params.put("CUST_ID", userInfos.getData(0).getString("CUST_ID"));// 根据号码查询正常用户信息
            params.put("SERIAL_NUMBER", userInfos.getData(0).getString("SERIAL_NUMBER"));
            // params.put(Route.ROUTE_EPARCHY_CODE,userInfos.getData(0).getString("EPARCHY_CODE"));
            IDataset custInfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryPerInfoByCustId", params);
            if (null != custInfos && custInfos.size() > 0)
            {
                setCustInfo(custInfos.getData(0));
                custName = custInfos.getData(0).getString("CUST_NAME", "");
            }

            // 查询MDO订购信息
            condParams.put("USER_ID", userInfos.getData(0).getString("USER_ID"));
            condParams.put("CUST_NAME", custName);
            IDataOutput output = CSViewCall.callPage(this, "SS.QueryMdoInfoSVC.queryUserMdoSvcInfo", condParams, getPagination("mdoSvcInfoNav"));
            setInfos(output.getData());
            setMdoSvcCount(output.getDataCount());
        }
        setCondition(getData("cond", true));
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData info);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setMdoSvcCount(long mdoSvcCount);
}
