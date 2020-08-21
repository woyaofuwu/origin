
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changepayrel.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.changepayrel.order.requestdata.PayRelationInfoReqData;

/**
 * 终止指定帐户下的高级付费关系
 * 
 * @author yuezy
 */
public class StopAcctAdvPayrelaAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        PayRelationInfoReqData reqData = (PayRelationInfoReqData) btd.getRD();
        String oldAcctId = reqData.getUca().getUserOriginalData().getAccount().getAcctId();// 原账户ID

        if (PersonConst.PAYRELANORCHG_CHANGE_ALL.equals(reqData.getChangeAll()) || !existValidPayRelation(reqData, oldAcctId))
        {

            IDataset dataset = PayRelaInfoQry.qryPayRelaByAcctID(oldAcctId, "0", "1", reqData.getStartCycleId());
            if (IDataUtil.isNotEmpty(dataset))
            {

                String endCycleId = SysDateMgr.decodeTimestamp(reqData.getStartCycleId(), "yyyy-MM-dd");
                endCycleId = SysDateMgr.addDays(endCycleId, -1);
                endCycleId = SysDateMgr.decodeTimestamp(endCycleId, "yyyyMMdd");

                for (int i = 0, size = dataset.size(); i < size; i++)
                {
                    PayRelationTradeData modPayRelTrade = new PayRelationTradeData(dataset.getData(i));
                    modPayRelTrade.setEndCycleId(endCycleId);
                    modPayRelTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(btd.getRD().getUca().getSerialNumber(), modPayRelTrade);
                }
            }

            // 账户转账
            this.transAcctFee(btd, reqData.getNewAcctId(), oldAcctId);
        }
    }

    /**
     * 检查当前账户是否还存在有效的默认付费关系
     * 
     * @param btd
     * @return
     * @throws Exception
     */
    private boolean existValidPayRelation(PayRelationInfoReqData reqData, String oldAcctId) throws Exception
    {
        IDataset dataset = PayRelaInfoQry.getAcctPayReltionNow(oldAcctId, "1", "1", null);
        if (IDataUtil.isNotEmpty(dataset))
        {
            String userId = reqData.getUca().getUserId();
            String newStartCycleId = reqData.getStartCycleId();// 新账户的开始账期
            String kdUserId = "";
            String sysDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_TIME_YYYYMMDD);
            String KDserialNumber = "KD_" + reqData.getUca().getSerialNumber();
            IData kdUsers = UcaInfoQry.qryUserInfoBySn(KDserialNumber);
            if (IDataUtil.isNotEmpty(kdUsers))
            {
                kdUserId = kdUsers.getString("USER_ID");// 宽带用户ID
            }

            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                String tempUserId = dataset.getData(i).getString("USER_ID");
                IData temp = UcaInfoQry.qryUserInfoByUserId(tempUserId);
                if (IDataUtil.isNotEmpty(temp) && StringUtils.equals("0", temp.getString("REMOVE_TAG")))
                {
                    // 判断用户ID等于当前用户或者关联的宽带用户时
                    if (StringUtils.equals(tempUserId, userId) || StringUtils.equals(tempUserId, kdUserId))
                    {
                        // 如果新账期的开始时间时大于系统时间的，则认为当前账户还存在有效的付费关系[]
                        if (SysDateMgr.getTimeDiff(sysDate, newStartCycleId, SysDateMgr.PATTERN_TIME_YYYYMMDD) > 0)
                        {
                            return true;
                        }
                    }
                    else
                    // 还存在给其他用户付费
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 转账
     * 
     * @throws Exception
     */
    private void transAcctFee(BusiTradeData btd, String newAcctId, String oldAcctId) throws Exception
    {
        OtherFeeTradeData feeTrade = new OtherFeeTradeData();
        feeTrade.setOperType(BofConst.OTHERFEE_DIFF_TRANS);
        feeTrade.setUserId(btd.getRD().getUca().getUserId());
        feeTrade.setAcctId(oldAcctId);// ???
        feeTrade.setUserId2(btd.getRD().getUca().getUserId());
        feeTrade.setAcctId2(newAcctId);
        feeTrade.setPaymentId("0");
        feeTrade.setStartDate(btd.getRD().getAcceptTime());
        feeTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
        btd.add(btd.getRD().getUca().getSerialNumber(), feeTrade);
    }
}
