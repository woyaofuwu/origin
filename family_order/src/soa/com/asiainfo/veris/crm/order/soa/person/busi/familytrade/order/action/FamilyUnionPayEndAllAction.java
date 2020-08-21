/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * @CREATED by gongp@2014-8-7 修改历史 Revision 2014-8-7 下午04:49:37
 */
public class FamilyUnionPayEndAllAction implements ITradeAction
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        String userId = btd.getRD().getUca().getUserId();

        IDataset dataset = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");

        if (IDataUtil.isNotEmpty(dataset))
        {

            IData temp = dataset.getData(0);

            String userIdA = temp.getString("USER_ID_A");

            IDataset allRelations = RelaUUInfoQry.getUserRelationAll(userIdA, "56");

            IData mainPayRelation = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
            if (IDataUtil.isEmpty(mainPayRelation))
            {
                // common.error("主卡号码无默认付费帐户！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "主卡号码无默认付费帐户！");
            }
            // ----------组织成员号码付费关系台账----------------
            String mainAcctId = mainPayRelation.getString("ACCT_ID", "-1");

            for (int i = 0, size = allRelations.size(); i < size; i++)
            {

                IData relations = allRelations.getData(i);

                RelationTradeData ralationTd = new RelationTradeData(relations);

                ralationTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                ralationTd.setEndDate(SysDateMgr.getLastDateThisMonth());

                btd.add(btd.getRD().getUca().getSerialNumber(), ralationTd);

                IData userPayRelation = this.getMemberPayRela(ralationTd.getUserIdB(), mainAcctId).getData(0);

                PayRelationTradeData payTd = new PayRelationTradeData(userPayRelation);

                payTd.setEndCycleId(DiversifyAcctUtil.getLastDayThisAcct(ralationTd.getUserIdB()).substring(0, 10).replace("-", ""));
                payTd.setRemark("统付成员付费关系");
                payTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                btd.add(ralationTd.getSerialNumberB(), payTd);

            }
        }
    }

    private IDataset getMemberPayRela(String userId, String acctId) throws Exception
    {

        IDataset dataset = PayRelaInfoQry.queryNormalPayre(userId, acctId, "41000", "0");

        if (IDataUtil.isEmpty(dataset))
        {
            // common.error("770070", "获取统一付费成员["+userId+"]付费关系无数据！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_821, userId);
        }

        if (dataset.size() > 1)
        {
            // common.error("770071", "获取统一付费成员["+userId+"]付费关系存在多条数据！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_822, userId);
        }
        return dataset;
    }
}
