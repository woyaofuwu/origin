
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.reg;

import java.util.List;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * 无手机一机多宽添加付费关系 将完工的action的搬迁至登记的action, 因为完工插资料表不会同步账务，账务不会统付
 * 
 * @author duhj_kd
 */
public class NophoneWideUnionPayAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
        if (ArrayUtil.isNotEmpty(otherTradeDatas))
        {
            for (int i = 0; i < otherTradeDatas.size(); i++)
            {
                OtherTradeData tempOtherTradeData = otherTradeDatas.get(i);
                if ("ONESN_MANYWIDE".equals(tempOtherTradeData.getRsrvValueCode()))
                {
                    String paySn = tempOtherTradeData.getRsrvStr1();
                    IDataset payUsers = UserInfoQry.getEffUserInfoBySn(paySn, "0", null);
                    if (IDataUtil.isEmpty(payUsers))
                    {
                        CSAppException.appError("-1", "付费号码" + paySn + "无有效用户资料！");
                    }
                    IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(payUsers.getData(0).getString("USER_ID"));

                    if (IDataUtil.isEmpty(mainPayRelations))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "付费号码无默认付费帐户！");
                    }

                    PayRelationTradeData payrelationTD = new PayRelationTradeData();
                    payrelationTD.setUserId(userId);
                    payrelationTD.setAcctId(mainPayRelations.getString("ACCT_ID"));
                    payrelationTD.setPayitemCode(BofConst.NO_PHONE_WIDE_UNION_PAY_CODE);
                    payrelationTD.setAcctPriority("0");
                    payrelationTD.setUserPriority("0");
                    payrelationTD.setBindType("1");
                    payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
                    payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
                    payrelationTD.setActTag("1");
                    payrelationTD.setDefaultTag("0");
                    payrelationTD.setLimitType("0");
                    payrelationTD.setLimit("0");
                    payrelationTD.setComplementTag("0");
                    payrelationTD.setInstId(SeqMgr.getInstId());
                    payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    btd.add(btd.getRD().getUca().getSerialNumber(), payrelationTD);
                }
            }
        }

    }
}
