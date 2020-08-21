
package com.asiainfo.veris.crm.order.web.group.bat.batpayrelaadvchg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class BatPayrelaAdvChg extends GroupBasePage
{

    /**
     * 过滤综合帐目
     * 
     * @param cycle
     * @throws Exception
     */
    public void filterNoteItems(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        data.put("NOTE_ITEM", getParameter("NOTE_ITEM"));

        IDataset iDataset = CSViewCall.call(this, "CS.NoteItemInfoQrySVC.filterNoteItems", data);

        setNoteItemList(iDataset);
    }

    public void getGroupBaseInfo(IRequestCycle cycle) throws Throwable
    {
        IData result = queryGroupCustInfo(cycle);
        String custId = result.getString("CUST_ID");

        if (StringUtils.isEmpty(custId))
            return;

        // 查询集团账户信息
        IDataset acctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", result);

        if (IDataUtil.isNotEmpty(acctInfos))
        {
            for (int i = 0, len = acctInfos.size(); i < len; i++)
            {
                IData acctInfoData = acctInfos.getData(i);
                String acctId = acctInfoData.getString("ACCT_ID", "");
                String payName = acctInfoData.getString("PAY_NAME", "");
                String productId = acctInfoData.getString("PRODUCT_ID", "");
                String productName = acctInfoData.getString("PRODUCT_NAME", "");

                // String payModeName = acctInfoData.getString("PAY_MODE_NAME", "");
                // String eparchyName = acctInfoData.getString("EPARCHY_NAME", "");
                String userId = acctInfoData.getString("USER_ID", "");
                String epachyCode = acctInfoData.getString("EPARCHY_CODE", "");
                acctInfoData.put("DISPLAY_NAME", acctId + "|" + payName + "|" + productId + "|" + productName + "|" + userId + "|" + epachyCode);
            }
        }

        setAcctInfos(acctInfos);

        // 其它信息
        IData acctotherInfo = new DataMap();
        /*
         * IDataset otherInfos=CSViewCall.call(this,"SYS_Static_GetSysTime",new DataMap()); String
         * endCycleId=(String)otherInfos.get(0);
         */
        acctotherInfo.put("START_CYCLE_ID", SysDateMgr.getNowCyc());
        acctotherInfo.put("END_CYCLE_ID", "205001");
        setAcctotherInfo(acctotherInfo);

        // 设置综合账目列表
        IData itemData = new DataMap();

        IDataset ItemList = CSViewCall.call(this, "CS.NoteItemInfoQrySVC.queryNoteItems", itemData); // 综合帐目列表

        setNoteItemList(ItemList);

        setGroupInfo(result);
    }

    /**
     * 取付费账目编码
     * 
     * @param cycle
     * @throws Exception
     */
    public void GetPayItemCode(IRequestCycle cycle) throws Exception
    {
        String check_all = getParameter("CheckAll");
        String payitem_code = "";

        // 处理付费帐目编码
        if (StringUtils.isNotBlank(check_all)) // 如果前台勾选了“综合帐目全选”，编码为“-1”
        {
            payitem_code = "-1";
        }
        else
        // 如果前台没有勾选了“综合帐目全选”，则由账务流程返回编码
        {
            String itemcodes = getParameter("itemcodes");

            if (StringUtils.isBlank(itemcodes))
            {
                // 没有选择综合帐目，无法完成拼串
            }

            String[] itemStr = StringUtils.split(itemcodes, ",");

            IData input = new DataMap();
            input.put("ITEMCODES", itemStr);

            IData iData = CSViewCall.callone(this, "CS.AcctInfoQrySVC.callAccountSvc", input);

            payitem_code = iData.getString("PAYITEM_CODE");
        }

        setAjax("PAYITEM_CODE", payitem_code);

    }

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        getData().put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        getData().put("EPARCHY_CODE", getTradeEparchyCode());

        setCondition(getData());
    }

    public void queryAcctInfo(IRequestCycle cycle) throws Exception
    {
        String acctId = getParameter("ACCT_ID");

        IData idAcctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, acctId);
        String strPayModeCode = idAcctInfo.getString("PAY_MODE_CODE", "0");
        // 如果不是现金帐户，即托收帐户，才查托收表
        if (!"0".equals(strPayModeCode))
        {
            IData idParam = new DataMap();
            idParam.put("ACCT_ID", getParameter("ACCT_ID"));
            IDataset idsAcctConsInfo = CSViewCall.call(this, "CS.AcctConsignInfoQrySVC.getConsignInfoByAcctIdForGrp", idParam);
            IData idAcctConsInfo = (idsAcctConsInfo.size() > 0 ? idsAcctConsInfo.getData(0) : null);
            if (idAcctConsInfo != null)
            {
                idAcctInfo.put("SUPER_BANK_CODE", idAcctConsInfo.getString("SUPER_BANK_CODE", ""));
            }
        }

        setCondition(idAcctInfo);
    }

    public abstract void setAcctInfo(IData idata);

    public abstract void setAcctInfoDesc(IData acctInfoDesc);

    public abstract void setAcctInfos(IDataset idataset);

    public abstract void setCondition(IData idAcctInfo);

    public abstract void setConsignDesc(IData consignDesc);

    public abstract void setGroupInfo(IData groupInfo);

    public abstract void setMemacctinfo(IData idAcctConsInfo);

    public abstract void setNoteItem(IData noteItem);

    public abstract void setNoteItemList(IDataset noteItemList);

    public abstract void setAcctotherInfo(IData acctotherInfo);

}
