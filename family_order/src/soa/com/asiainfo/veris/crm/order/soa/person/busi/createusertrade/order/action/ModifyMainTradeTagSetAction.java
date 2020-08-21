
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

public class ModifyMainTradeTagSetAction implements ITradeAction
{

    // @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();
        String processTagSet = btd.getMainTradeData().getProcessTagSet();
        // 第1位： 是否二次开户(0-非，1-是)
        /*
         * if ("1".equals(createPersonUserRD.getBReopenTag())) { processTagSet = "1" + processTagSet.substring(1, 20); }
         * // 同帐户 if ("1".equals(createPersonUserRD.getBReopenTag())) { processTagSet = processTagSet.substring(0, 1) +
         * "1" + processTagSet.substring(2, 20); } // 同客户 if ("1".equals(createPersonUserRD.getBReopenTag())) {
         * processTagSet = processTagSet.substring(0, 2) + "1" + processTagSet.substring(3, 20); }
         */
        // 上述两个没找到 sunxin
        // 前台选择了部门(代理商开户)
        if ("AGENT_OPEN".equals(createPersonUserRD.getOpenType()))
        {
            // processTagSet = processTagSet.substring(0, 3) + "1" + processTagSet.substring(4, 20);
            btd.setMainTradeProcessTagSet(4, "1");
        }

        // 第8位：是否预约开户(0-非，1-是)
        /*
         * if ("1".equals(createPersonUserRD.getPreOpenTag())) { processTagSet = processTagSet.substring(0, 7) + "1" +
         * processTagSet.substring(8, 20); }
         */

        // 第10位：是否延迟回缴(海南，针对代理商开户的回缴方式)
        /*
         * if ("1".equals(createPersonUserRD.getOpenType()) && "1".equals(createPersonUserRD.getDelayPay())) { //
         * processTagSet = processTagSet.substring(0, 9) + "1" + processTagSet.substring(10, 20);
         * btd.setMainTradeProcessTagSet(10, "1"); }
         */
        // 第16位是否开户绑定预存营销 1-是 0-否
        if ("1".equals(createPersonUserRD.getBindSaleTag()))
        {
            // processTagSet = processTagSet.substring(0, 15) + "1" + processTagSet.substring(16, 20);
            btd.setMainTradeProcessTagSet(16, "1");

        }
        // 第17位 是否网上选号开户用户 1-是 0-否
        if ("1".equals(createPersonUserRD.getNetChoose()))
        {
            // processTagSet = processTagSet.substring(0, 16) + "1" + processTagSet.substring(17, 20);
            btd.setMainTradeProcessTagSet(17, "1");
        }
        // btd.getMainTradeData().setProcessTagSet(processTagSet);

    }
}
