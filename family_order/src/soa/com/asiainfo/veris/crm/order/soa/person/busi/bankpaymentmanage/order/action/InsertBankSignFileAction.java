/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankMainSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankSubSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by gongp@2014-6-30 修改历史 Revision 2014-6-30 下午08:14:49
 */
public class InsertBankSignFileAction implements ITradeFinishAction
{

    /*
     * (non-Javadoc) 未使用
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        
        String tradeId = mainTrade.getString("TRADE_ID");
        
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        if ("1390".equals(tradeTypeCode) || "1392".equals(tradeTypeCode))
        {

            IDataset list = UserBankMainSignInfoQry.qryTradeMainsignInfoByPK(tradeId);

            if (!list.isEmpty())
            {
                for (int i = 0, size = list.size(); i < size; i++)
                {

                    BankMainSignTradeData bankMainTd = new BankMainSignTradeData(list.getData(i));

                    if (BofConst.MODIFY_TAG_UPD.equals(bankMainTd.getModifyTag()))
                        continue;

                    IData tiBSynchInfo = new DataMap();

                    tiBSynchInfo.put("RSRV_STR6", bankMainTd.getSignId());
                    tiBSynchInfo.put("RSRV_STR12", bankMainTd.getUserType());
                    tiBSynchInfo.put("RSRV_STR13", bankMainTd.getUserValue());
                    tiBSynchInfo.put("RSRV_STR16", "01");

                    Dao.executeUpdateByCodeCode("TI_O_BANK_SIGN_FILE", "END_TI_BANK_SIGN_FILE", tiBSynchInfo, Route.CONN_CRM_CEN);

                    // 插入总表
                    tiBSynchInfo.clear();

                    tiBSynchInfo.put("RSRV_STR6", bankMainTd.getSignId());
                    tiBSynchInfo.put("RSRV_STR12", bankMainTd.getUserType());
                    tiBSynchInfo.put("RSRV_STR13", bankMainTd.getUserValue());
                    tiBSynchInfo.put("RSRV_STR7", bankMainTd.getBankAcctId());
                    tiBSynchInfo.put("RSRV_STR8", bankMainTd.getBankAcctType());
                    tiBSynchInfo.put("RSRV_STR9", bankMainTd.getBankId());
                    tiBSynchInfo.put("PAY_TYPE", bankMainTd.getPayType());
                    tiBSynchInfo.put("RSRV_STR10", bankMainTd.getRechThreshold());
                    tiBSynchInfo.put("RSRV_STR11", bankMainTd.getRechAmount());
                    tiBSynchInfo.put("PREPAY_TAG", "");
                    tiBSynchInfo.put("RSRV_STR16", "01");
                    tiBSynchInfo.put("APPLY_DATE", bankMainTd.getApplyDate());
                    tiBSynchInfo.put("START_DATE", bankMainTd.getStartDate());
                    tiBSynchInfo.put("END_DATE", bankMainTd.getEndDate());
                    tiBSynchInfo.put("RSRV_STR14", bankMainTd.getIdType());
                    tiBSynchInfo.put("RSRV_STR15", bankMainTd.getIdValue());
                    tiBSynchInfo.put("DEAL_TYPE", "0".equals(bankMainTd.getModifyTag()) ? "01" : ("1".equals(bankMainTd.getModifyTag()) ? "02" : "03"));
                    tiBSynchInfo.put("RSRV_STR17", "0");
                    tiBSynchInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    tiBSynchInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    tiBSynchInfo.put("REMARK", bankMainTd.getRemark());
                    tiBSynchInfo.put("RSRV_STR1", bankMainTd.getRsrvStr1());
                    tiBSynchInfo.put("RSRV_STR2", bankMainTd.getRsrvStr2());
                    tiBSynchInfo.put("RSRV_STR3", bankMainTd.getRsrvStr3());
                    tiBSynchInfo.put("RSRV_STR4", bankMainTd.getRsrvStr4());
                    tiBSynchInfo.put("RSRV_STR5", bankMainTd.getRsrvStr5());

                    Dao.executeUpdateByCodeCode("TI_O_BANK_SIGN_FILE", "INC_TI_BANK_SIGN_FILE", tiBSynchInfo, Route.CONN_CRM_CEN);

                    // 插入签解约关系文件接口表 TI_O_BANK_SIGN_FILE
                }
            }

        }
        else if ("1393".equals(tradeTypeCode) || "1394".equals(tradeTypeCode))
        {

            IDataset list = UserBankMainSignInfoQry.qryTradeSubsignInfoByTradeId(tradeId);

            if (!list.isEmpty())
            {
                for (int i = 0, size = list.size(); i < size; i++)
                {
                    IData temp = list.getData(i);
                    
                    BankSubSignTradeData subSignTd = new BankSubSignTradeData(temp);
                    subSignTd.setSignId(temp.getString("RSRV_STR18"));

                    String modifyTag = "0".equals(subSignTd.getModifyTag()) ? "01" : ("1".equals(subSignTd.getModifyTag()) ? "02" : "03");

                    if (BofConst.MODIFY_TAG_UPD.equals(modifyTag))
                        continue;

                    IData tiBSynchInfo = new DataMap();

                    tiBSynchInfo.put("RSRV_STR6", subSignTd.getSignId());
                    tiBSynchInfo.put("RSRV_STR12", subSignTd.getSubUserType());
                    tiBSynchInfo.put("RSRV_STR13", subSignTd.getSubUserValue());
                    tiBSynchInfo.put("RSRV_STR16", "02");

                    Dao.executeUpdateByCodeCode("TI_O_BANK_SIGN_FILE", "END_TI_BANK_SIGN_FILE", tiBSynchInfo, Route.CONN_CRM_CEN);

                    // 插入总表
                    tiBSynchInfo.clear();

                    tiBSynchInfo.put("RSRV_STR6", subSignTd.getSignId());
                    tiBSynchInfo.put("RSRV_STR12", subSignTd.getSubUserType());
                    tiBSynchInfo.put("RSRV_STR13", subSignTd.getSubUserValue());
                    tiBSynchInfo.put("RSRV_STR7", "");
                    tiBSynchInfo.put("RSRV_STR8", "");
                    tiBSynchInfo.put("RSRV_STR9", "");
                    tiBSynchInfo.put("PAY_TYPE", "");
                    tiBSynchInfo.put("RSRV_STR10", "");
                    tiBSynchInfo.put("RSRV_STR11", "");
                    tiBSynchInfo.put("PREPAY_TAG", "");
                    tiBSynchInfo.put("RSRV_STR16", "02");
                    tiBSynchInfo.put("APPLY_DATE", subSignTd.getStartDate());
                    tiBSynchInfo.put("START_DATE", subSignTd.getStartDate());
                    tiBSynchInfo.put("END_DATE", subSignTd.getEndDate());
                    tiBSynchInfo.put("RSRV_STR14", "");
                    tiBSynchInfo.put("RSRV_STR15", "");
                    tiBSynchInfo.put("DEAL_TYPE", "0".equals(subSignTd.getModifyTag()) ? "01" : ("1".equals(subSignTd.getModifyTag()) ? "02" : "03"));
                    tiBSynchInfo.put("RSRV_STR17", "0");
                    tiBSynchInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    tiBSynchInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    tiBSynchInfo.put("REMARK", subSignTd.getRemark());
                    tiBSynchInfo.put("RSRV_STR1", subSignTd.getRsrvStr1());
                    tiBSynchInfo.put("RSRV_STR2", subSignTd.getRsrvStr2());
                    tiBSynchInfo.put("RSRV_STR3", subSignTd.getRsrvStr3());
                    tiBSynchInfo.put("RSRV_STR4", subSignTd.getRsrvStr4());
                    tiBSynchInfo.put("RSRV_STR5", subSignTd.getRsrvStr5());

                    Dao.executeUpdateByCodeCode("TI_O_BANK_SIGN_FILE", "INC_TI_BANK_SIGN_FILE", tiBSynchInfo, Route.CONN_CRM_CEN);

                    // 插入签解约关系文件接口表 TI_O_BANK_SIGN_FILE

                }
            }
        }

    }

}
