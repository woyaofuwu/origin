
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class MebPayRelaDestroyBean extends MemberBean
{
    private String actionFlag = null;

    private String acctId = null;

    private String payItemCode = null;

    /**
     * 处理付费关系
     * 
     * @throws Exception
     */
    public void actTradePayRela() throws Exception
    {
        // 查询成员非默认付费信息
        IDataset userPayList = PayRelaInfoQry.qryPayRelaByUserAcctIdDefTag(reqData.getUca().getUserId(), acctId, "0");
        // 查询成员特殊付费信息
        IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), acctId, payItemCode);

        if (IDataUtil.isEmpty(userPayList))
        {
            return;
        }

        IDataset payRelaSet = new DatasetList();
        IDataset specPayRelaSet = new DatasetList();
        for (int i = 0, iRow = userPayList.size(); i < iRow; i++)
        {
            IData userPayData = userPayList.getData(i);
            String actTag = userPayData.getString("ACT_TAG");
            if (!payItemCode.equals(userPayData.getString("PAYITEM_CODE", "")) || !"1".equals(actTag))
            {
                continue;
            }
            for (int j = 0, jRow = userSpecialPayList.size(); j < jRow; j++)
            {
                IData userSpecialPyaData = userSpecialPayList.getData(j);

                if (!userPayData.getString("LIMIT", "").equals(userSpecialPyaData.getString("LIMIT")) || !userPayData.getString("LIMIT_TYPE", "").equals(userSpecialPyaData.getString("LIMIT_TYPE")))
                {
                    continue;
                }

                // 状态属性：0-增加，1-删除，2-变更
                userPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                userSpecialPyaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                if (reqData.isNeedSms())
                {
                    userPayData.put("RSRV_STR4", "2");
                    userPayData.put("RSRV_STR5", reqData.getUca().getSerialNumber());
                }
                // 注销(上月底)
                if ("1".equals(actionFlag))
                {

                    String lastCycleLastAcct = SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getAddMonthsLastDay(-1));
                    userPayData.put("END_CYCLE_ID", lastCycleLastAcct);
                    userPayData.put("ACT_TAG", "0");// 设为无效(0)
                    userPayData.put("REMARK", "注销(上月底)"); // 注销(上月底)

                    userSpecialPyaData.put("END_CYCLE_ID", lastCycleLastAcct);
                    userSpecialPyaData.put("REMARK", "注销(上月底)"); // 注销(上月底)
                }
                else if ("2".equals(actionFlag))
                {// 注销(本月底)
                    String lastCycleThisAcct = SysDateMgr.getLastCycleThisMonth();
                    userPayData.put("END_CYCLE_ID", lastCycleThisAcct);
                    // 状态属性：0-增加，1-删除，2-变更
                    userPayData.put("REMARK", "注销(本月底)"); // 注销(上月底)

                    userSpecialPyaData.put("END_CYCLE_ID", lastCycleThisAcct);
                    userSpecialPyaData.put("REMARK", "注销(本月底)"); // 注销(上月底)

                }
                payRelaSet.add(userPayData);
                specPayRelaSet.add(userSpecialPyaData);
            }

        }
        super.addTradePayrelation(payRelaSet);
        super.addTradeUserSpecialepay(specPayRelaSet);

    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        actTradePayRela();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        actionFlag = map.getString("ACTION_FLAG", "");
        acctId = map.getString("ACCT_ID", "");
        payItemCode = map.getString("PAY_ITEM_CODE", "");
        reqData.setNeedSms(map.getBoolean("IF_SMS", false)); // 是否发短信
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);

        super.makUcaForMebNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "9982";
    }
}
