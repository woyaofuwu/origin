
package com.asiainfo.veris.crm.order.web.group.destroygroupspecialpay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SpecialPayProductInfo extends GroupBasePage
{
    /**
     * 查询统付产品成员信息查询统付账号的产品信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryAcctProductInfo(IRequestCycle cycle) throws Exception
    {

        IData condData = getData("cond", true);

        // 查询集团客户信息
        IData grpCustData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, condData.getString("GROUP_ID"));

        IData svcData = new DataMap();

        String acctId = condData.getString("ACCT_ID");

        svcData.put("CUST_ID", grpCustData.getString("CUST_ID"));
        svcData.put("ACCT_ID", condData.getString("ACCT_ID"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset productList = CSViewCall.call(this, "CS.PayRelaInfoQrySVC.qryGrpSpecialPayByCustIdAcctId", svcData);

        if (IDataUtil.isEmpty(productList))
        {
            setMessage("集团账户" + acctId + "无统付产品!");
        }

        setGroupInfo(grpCustData);

        setProductList(productList);

    }

    public abstract void setMessage(String message);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setProductList(IDataset productList);
}
