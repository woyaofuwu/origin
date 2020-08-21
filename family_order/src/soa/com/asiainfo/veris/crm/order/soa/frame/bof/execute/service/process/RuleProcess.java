
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.FillUserElementInfoUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class RuleProcess
{

    public static void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {
        if (!"0".equals(btd.getMainTradeData().getCancelTag()))
        {
            // 非正向受理业务不调用checkafter
            return;
        }
        IData ruleParam = new DataMap();
        IData tableDataClone = (IData) Clone.deepClone(tableData);
        ruleParam.putAll(tableDataClone);
        if (tableData.containsKey(TradeTableEnum.TRADE_SVC.getValue()) || tableData.containsKey(TradeTableEnum.TRADE_DISCNT.getValue()) || tableData.containsKey(TradeTableEnum.TRADE_PRODUCT.getValue()))
        {
            UserTradeData user = btd.getRD().getUca().getUser();
            IDataset userList = new DatasetList();
            userList.add(user.toData());
            if (BofConst.MODIFY_TAG_ADD.equals(user.getModifyTag()))
            {
                ruleParam.put("TF_F_USER", userList);
            }
            else
            {
                ruleParam.put("TF_F_USER_AFTER", userList);
            }

            AccountTradeData accountTradeData = btd.getRD().getUca().getAccount();
            IDataset accountList = new DatasetList();
            accountList.add(accountTradeData.toData());
            if (BofConst.MODIFY_TAG_ADD.equals(accountTradeData.getModifyTag()))
            {
                ruleParam.put("TF_F_ACCOUNT", accountList);
            }
            else
            {
                ruleParam.put("TF_F_ACCOUNT_AFTER", accountList);
            }

            CustomerTradeData customerTradeData = btd.getRD().getUca().getCustomer();
            IDataset customerList = new DatasetList();
            customerList.add(customerTradeData.toData());
            if (BofConst.MODIFY_TAG_ADD.equals(customerTradeData.getModifyTag()))
            {
                ruleParam.put("TF_F_CUSTOMER", customerList);
            }
            else
            {
                ruleParam.put("TF_F_CUSTOMER_AFTER", customerList);
            }

            IDataset userSvcsList = new DatasetList();
            List<SvcTradeData> svcList = btd.getRD().getUca().getUserSvcs();
            int size = svcList.size();
            for (int j = 0; j < size; j++)
            {
                userSvcsList.add(svcList.get(j).toData());
            }
            ruleParam.put("TF_F_USER_SVC_AFTER", userSvcsList);

            IDataset userDiscntsList = new DatasetList();
            List<DiscntTradeData> discntList = btd.getRD().getUca().getUserDiscnts();
            size = discntList.size();
            for (int j = 0; j < size; j++)
            {
                userDiscntsList.add(discntList.get(j).toData());
            }
            ruleParam.put("TF_F_USER_DISCNT_AFTER", userDiscntsList);

            IDataset userAttrList = new DatasetList();
            List<AttrTradeData> attrList = btd.getRD().getUca().getUserAttrs();
            size = attrList.size();
            for (int j = 0; j < size; j++)
            {
                userAttrList.add(attrList.get(j).toData());
            }
            ruleParam.put("TF_F_USER_ATTR_AFTER", userAttrList);

            IDataset userProductsList = new DatasetList();
            List<ProductTradeData> productsList = btd.getRD().getUca().getUserProducts();
            size = productsList.size();
            for (int j = 0; j < size; j++)
            {
                userProductsList.add(productsList.get(j).toData());
            }
            ruleParam.put("TF_F_USER_PRODUCT_AFTER", userProductsList);
        }
        // call规则
        ruleParam.put("TIPS_TYPE", "0|4");
        ruleParam.put("ACTION_TYPE", "TRADEALL.TradeCheckAfter");// modify by xiaocl
        ruleParam.put("TRADE_TYPE_CODE", btd.getTradeTypeCode());
        ruleParam.put("EPARCHY_CODE", btd.getRD().getUca().getUserEparchyCode());
        ruleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        ruleParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        ruleParam.put("LAST_OWE_FEE", btd.getRD().getUca().getLastOweFee());
        ruleParam.put("REAL_FEE", btd.getRD().getUca().getRealFee());
        ruleParam.put("LEAVE_REAL_FEE", btd.getRD().getUca().getAcctBlance());
        ruleParam.put("ORDER_TYPE_CODE", DataBusManager.getDataBus().getOrderTypeCode());
        ruleParam.put("PROCESS_TAG", btd.getRD().getTradeType().getIntfTagSet());// add by chenxy3 2015-07-14
        IData ruleResult = BizRule.bre4UniteInterface(ruleParam);
        CSAppException.breerr(ruleResult);
    }
}
