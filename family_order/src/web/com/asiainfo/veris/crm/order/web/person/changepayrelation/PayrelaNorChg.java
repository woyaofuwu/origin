
package com.asiainfo.veris.crm.order.web.person.changepayrelation;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 普通付费关系变更
 */
public abstract class PayrelaNorChg extends PersonBasePage
{
    /**
     * 普通付费关系,点Auth组件后的方法 子类重写
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        IData result = CSViewCall.callone(this, "SS.ModifyPayRelationSVC.getBusiParam", pgData);
        String startCycleId = result.getString("START_CYCLE_ID");
        startCycleId = startCycleId.substring(0, startCycleId.length() - 2);// 账期为年月格式

        IData baseCommInfo = new DataMap();
        baseCommInfo.put("START_CYCLE_ID", startCycleId);
        setBaseCommInfo(baseCommInfo);

        // 查询账户信息
        IData param = new DataMap();
        param.put("ACCT_ID", pgData.getString("ACCT_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, pgData.getString("EPARCHY_CODE"));
        param.put(StrUtil.getNotFuzzyKey(), true);// 强制不模糊化
        IDataset acctDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryAcctInfoByAcctId", param);
        setAcctInfo(acctDataset.getData(0));
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.PayrelaNormalRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setAcctInfo(IData acctInfo);

    public abstract void setBaseCommInfo(IData baseCommInfo);

}
