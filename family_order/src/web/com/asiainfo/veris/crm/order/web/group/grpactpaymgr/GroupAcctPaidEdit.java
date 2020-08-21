
package com.asiainfo.veris.crm.order.web.group.grpactpaymgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class GroupAcctPaidEdit extends CSBasePage
{
    public abstract void setInfo(IData info);

    public abstract void setAcctInfo(IData info);

    public abstract void setCondition(IData condition);

    public abstract void setInfos(IDataset infos);

    public abstract void setAcctInfos(IDataset infos);

    public abstract void setNoteItemList(IDataset infos);

    public abstract void setProductInfo(IDataset product);

    public abstract void setGroupAccountList(IDataset product);

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String acctIdA = param.getString("ACCT_ID_A");
        String custId = param.getString("CUST_ID");

        IData cond = new DataMap();
        cond.put("ACCT_ID_A", acctIdA);
        cond.put("CUST_ID", custId);
        setCondition(cond);
    }

    public void getAcctInfoBySn(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();
        IData paramCon = new DataMap();
        paramCon.put("USER_TYPE", "1");// 0-集团 1-个人
        setCondition(paramCon);

        IDataset tradeInfos = new DatasetList();
        String serialNumber = param.getString("SERIAL_NUMBER");
        tradeInfos = getAccountInfoBySN(serialNumber);
        setGroupAccountList(tradeInfos);
    }

    /**
     * @description:根据手机号码查询其默认付费账户信息
     * @author liujy
     * @param pd
     * @param serialNumber
     * @throws Exception
     */
    public IDataset getAccountInfoBySN(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        String usereaprchy_code = "";
        param.put("SERIAL_NUMBER", serialNumber);

        // 查询是否是本地号码
        IDataset mofficeInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.getMofficeBySN", param);
        if (mofficeInfos != null && mofficeInfos.size() > 0)
        {
            IData mofficeInfo = (IData) mofficeInfos.get(0);
            usereaprchy_code = mofficeInfo.getString("EPARCHY_CODE");
        }
        else
        {
            CSViewException.apperr(GrpException.CRM_GRP_742, serialNumber);
        }

        // 查询用户信息
        IData userInfos = UCAInfoIntfViewUtil.qryUserInfoBySn(this, serialNumber);
        if (null == userInfos || userInfos.size() == 0)
        {
            CSViewException.apperr(GrpException.CRM_GRP_715);
        }
        String userId = userInfos.getString("USER_ID");
        IData payParam = new DataMap();
        payParam.put("USER_ID", userId);

        // 根据USER_ID查询客户资料
        IData custInfo = UCAInfoIntfViewUtil.qryMebUCAInfoBySn(this, serialNumber);
        // 查询默认付费账户
        IDataset defPayRelaInfos = CSViewCall.call(this, "SS.BookTradeSVC.getDefaultPayRelationByUserID", payParam);
        if (defPayRelaInfos.size() == 0)
        {
            payParam.put("DEFAULT_TAG", "1");
            IDataset lastPayRelatons = CSViewCall.call(this, "SS.BookTradeSVC.getLastDefaultPayRelationByUserID", payParam);
            if (lastPayRelatons.size() == 0)
            {
                CSViewException.apperr(GrpException.CRM_GRP_743);
            }
        }

        // 分散账期修改 增加成员账期判断 add start
        IDataset judeAcctDayTag = CSViewCall.call(this, "SS.BookTradeSVC.getJudeAcctDayTag", payParam);
        String ifuseraccttag = "";
        if (null != judeAcctDayTag && judeAcctDayTag.size() > 0)
        {
            IData data = judeAcctDayTag.getData(0);
            ifuseraccttag = data.getString("IFUSERACCTTAG");
        }

        if ("true".equals(ifuseraccttag))
        {
            IDataset userAcctDayList = CSViewCall.call(this, "SS.BookTradeSVC.getUserAcctDay", payParam);

            if (null != userAcctDayList && userAcctDayList.size() > 0)
            {
                IData userAcctDay = userAcctDayList.getData(0);
                if (userAcctDay == null || userAcctDay.size() == 0)
                {
                    CSViewException.apperr(GrpException.CRM_GRP_743, serialNumber);
                }
                else
                {
                    // 判断默认付费账户是否是自然月结账成员
                    IData warn = new DataMap();
                    warn.put("USERACCTDAY", userAcctDay);
                    warn.put("SERIALNUMBER", userAcctDay);
                    CSViewCall.call(this, "SS.BookTradeSVC.checkUserAcctDayWithWarn", warn);
                }
            }
        }

        String acctId1 = defPayRelaInfos.getData(0).getString("ACCT_ID");
        IData acctParam = new DataMap();
        acctParam.put("ACCT_ID", acctId1);

        IDataset acctInfos = CSViewCall.call(this, "SS.BookTradeSVC.getAcctInfoByAcctId", acctParam);
        return acctInfos;
    }

    public void queryGroupAccountInfo(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();
        String custId = param.getString("CUST_ID");
        IDataset tradeInfos = new DatasetList();
        IData inputparam = new DataMap();
        inputparam.put("CUST_ID", custId);

        IDataset acccountInfo = CSViewCall.call(this, "SS.BookTradeSVC.queryGroupAccountInfo", inputparam);
        if (null != acccountInfo && acccountInfo.size() > 0)
        {
            for (int i = 0; i < acccountInfo.size(); i++)
            {
                IData account = acccountInfo.getData(i);
                IData data = new DataMap();
                data.put("ACCT_ID", account.getString("ACCT_ID"));
                data.put("PAY_NAME", account.getString("PAY_NAME"));
                data.put("PAY_MODE_CODE", account.getString("PAY_MODE_CODE"));
                data.put("PRODUCT_ID", account.getString("PRODUCT_ID"));
                data.put("PRODUCT_NAME", account.getString("PRODUCT_NAME"));
                data.put("EPARCHY_CODE", account.getString("EPARCHY_CODE"));
                tradeInfos.add(data);
            }
        }
        setGroupAccountList(tradeInfos);
    }

    public void getNoteItemList(IRequestCycle cycle) throws Throwable
    {
        IData inputparam = new DataMap();
        IDataset acccountInfo = CSViewCall.call(this, "SS.BookTradeSVC.getNoteItemList", inputparam);

        setNoteItemList(acccountInfo);
    }

    public void filterNoteItems(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();
        IData inputparam = new DataMap();
        inputparam.put("NOTE_ITEM", param.getString("NOTE_ITEM"));
        IDataset iDataset = CSViewCall.call(this, "SS.BookTradeSVC.filterNoteItems", inputparam);
        setNoteItemList(iDataset);
    }

    public void createRelationAA(IRequestCycle cycle) throws Throwable
    {
        IData param = getData();

        String payNoteItem = "";
        String payNoteName = "";

        String limitType = param.getString("LIMIT_TYPE");
        String limitValue = param.getString("LIMIT");
        String checkAll = param.getString("FLAG");
        String itemcodes = param.getString("ITEM_CODES");
        String custId = param.getString("CUST_ID");

        // 勾选全部账目，付费账目编码填"-1"
        if ("true".equals(checkAll))
        {
            payNoteItem = "-1";
            payNoteName = "全部账目";
        }
        else
        {
            itemcodes = itemcodes.replaceAll(",", "|");
            IData itemcode = new DataMap();
            itemcode.put("DETAIL_ITEMSET", itemcodes);

            // 获取付费账目编码
            IDataset itemList = CSViewCall.call(this, "SS.BookTradeSVC.qryPayItemCode", itemcode);
            if (null != itemList && itemList.size() > 0)
            {
                IData item = itemList.getData(0);
                payNoteName = item.getString("PAY_ITEM");
                payNoteItem = item.getString("PAYITEM_CODE");
            }
        }

        IData inData = new DataMap();
        inData.put("STATE", "ADD");// 新增
        inData.put("ACCT_ID", param.getString("ACCT_ID_A"));// 支付账户
        inData.put("EPARCHY_CODE_A", param.getString("PAY_EPARCHY_CODE_A"));// 支付账户归属地州
        inData.put("USER_ID_A", "-1");
        inData.put("ACCT_ID_B", param.getString("ACCT_ID_B"));// 被支付账户
        inData.put("EPARCHY_CODE_B", param.getString("PAY_EPARCHY_CODE_B"));// 被支付账户归属地州
        String eparchyCode = getTradeEparchyCode();
        inData.put("USER_EPARCHY_CODE", eparchyCode);
        inData.put("EPARCHY_CODE", eparchyCode);
        // inData.put("USER_ID_B", td.getUserInfo().getString("USER_ID","-1") );
        inData.put("CUST_ID", custId);
        // inData.put("SERIAL_NUMBER", td.getUserInfo().getString("SERIAL_NUMBER","-1"));
        inData.put("PAYITEM_CODE", payNoteItem);// 付费账目编码
        inData.put("RSRV_STR1", payNoteName);// 付费账目名称
        inData.put("ROLE_CODE_A", "0");// 0-代付账户 1-被代付账户
        inData.put("ROLE_CODE_B", "1");
        inData.put("ORDERNO", "0");
        inData.put("LIMIT_TYPE", limitType);
        inData.put("LIMIT_VALUE", limitValue);
        inData.put("RELATION_TYPE_CODE", "98");// 代付
        inData.put("TRADE_TYPE_CODE", "4610");// 代付

        IData inputParam = new DataMap();
        inputParam.put("ACCT_ID_B", param.getString("ACCT_ID_B"));
        inputParam.put("RELATION_TYPE_CODE", "98");
        inputParam.put("ACT_TAG", "1");

        IDataset relationaa = CSViewCall.call(this, "SS.BookTradeSVC.getRelationAAByActIdB", inputParam);
        if (null != relationaa && relationaa.size() > 0)
        {
            CSViewException.apperr(GrpException.CRM_GRP_739);
        }

        IDataset resultData = CSViewCall.call(this, "SS.PayrelaAdvChgSVC.createRelaTrade", inData);
        setAjax(resultData);
    }
}
