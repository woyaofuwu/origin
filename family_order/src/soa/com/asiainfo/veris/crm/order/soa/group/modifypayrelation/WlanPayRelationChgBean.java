
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

public class WlanPayRelationChgBean extends VgpoPayRelationChgBean
{

    /**
     * 生成其他台帐信息
     */
    public void batPayRela() throws Exception
    {

        String startcycleid = baseCommInfo.getString("START_CYCLE_ID");
        String payitemcode = baseCommInfo.getString("PAYITEM_CODE");
        String operType = baseCommInfo.getString("OPER_TYPE");

        boolean ifaddPay = true;

        String acctId = reqData.getGrpUca().getAcctId();// 集团账户id //baseCommInfo.getString("ACCT_ID");
        String userId = reqData.getUca().getUserId();// 成员用户标识
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IData param = new DataMap();
        param.put("USER_ID", userId);

        IDataset dataset = new DatasetList();

        IDataset payRelations = AcctInfoQry.getPayRelaByUserEffected(param, eparchyCode);
        if ("2".equals(operType))
        {
            if (IDataUtil.isNotEmpty(payRelations))
            {
                for (int i = 0, sizeI = payRelations.size(); i < sizeI; i++)
                {
                    IData payRelationInfo = payRelations.getData(i);
                    if (acctId.equals(payRelationInfo.getString("ACCT_ID")))
                    {
                        // 分散账期修改
                        payRelationInfo.put("END_CYCLE_ID", baseCommInfo.getString("DEL_CYCLE_ID"));
                        payRelationInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                        dataset.add(payRelationInfo);
                    }
                }
            }
        }
        else
        {

            if (null != payRelations && payRelations.size() > 0)
            {
                IDataset wlanpayitems = StaticUtil.getStaticList("GROUP_BAT_WLANPAYRELA");
                for (int i = 0; i < payRelations.size(); i++)
                {
                    // add by qiand 20100720
                    boolean isvpnitem = false; // 账目是否是WLAN账目
                    IData payRelationInfo = payRelations.getData(i);
                    if (acctId.equals(payRelationInfo.getString("ACCT_ID")))
                    {

                        for (int j = 0, sizeJ = wlanpayitems.size(); j < sizeJ; j++)
                        {
                            IData dt = wlanpayitems.getData(j);
                            if (payRelationInfo.getString("PAYITEM_CODE").equals(dt.getString("KEY")))
                            {
                                isvpnitem = true;
                                break;
                            }
                        }
                        if (isvpnitem)
                        {
                            if (!payRelationInfo.getString("START_CYCLE_ID", "").equals(startcycleid) || !payRelationInfo.getString("PAYITEM_CODE", "").equals(payitemcode))
                            {
                                // 现有有效的开始账期和当前账期不同时，插入两条记录
                                // 分散账期修改
                                payRelationInfo.put("END_CYCLE_ID", baseCommInfo.getString("DEL_CYCLE_ID"));
                                payRelationInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                dataset.add(payRelationInfo);
                            }
                            else
                            {
                                ifaddPay = false;
                                // 现有有效账期和当前账期相同时，为防止互斥，插入一条更新记录
                                payRelationInfo.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID"));
                                payRelationInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                payRelationInfo.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG"));
                                payRelationInfo.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE"));
                                payRelationInfo.put("LIMIT", baseCommInfo.getString("LIMIT"));
                                dataset.add(payRelationInfo);
                            }
                        }

                    }
                }
            }
            if (ifaddPay)
            {
                IData info = new DataMap();

                info.put("USER_ID", userId);
                info.put("ACCT_ID", acctId);
                info.put("PAYITEM_CODE", baseCommInfo.getString("PAYITEM_CODE"));
                info.put("ACCT_PRIORITY", "0");
                info.put("USER_PRIORITY", "0");
                info.put("BIND_TYPE", "0");
                info.put("ACT_TAG", "1");
                info.put("DEFAULT_TAG", "0");
                info.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE"));
                info.put("LIMIT", baseCommInfo.getString("LIMIT"));
                info.put("STATE", "ADD");
                info.put("START_CYCLE_ID", baseCommInfo.getString("START_CYCLE_ID"));
                info.put("END_CYCLE_ID", baseCommInfo.getString("END_CYCLE_ID"));
                info.put("COMPLEMENT_TAG", baseCommInfo.getString("COMPLEMENT_TAG"));
                info.put("INST_ID", SeqMgr.getInstId());

                dataset.add(info);
            }
        }

        this.addTradePayrelation(dataset);
    }

}
