
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;

/**
 * 统一付费台账登记完校验
 * @author chenzg
 *
 */
public class PersonGrpUserUnionPayCheckAfterTradeAction implements ITradeAction
{
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        try
        {
            List<RelationTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
            String tradeId = btd.getTradeId();
            IDataset members = new DatasetList();

            for (int i = 0, size = list.size(); i < size; i++)
            {
                RelationTradeData relaTd = list.get(i);

                IData member = new DataMap();
                member.put("TRADE_ID", tradeId);
                member.put("IN_TIME", SysDateMgr.getSysTime());

                member.put("SERIAL_NUMBER", relaTd.getSerialNumberB());
                member.put("RELATION_TYPE_CODE", "57");
                members.add(member);
            }

            /*
             * IData member = new DataMap(); member.put("TRADE_ID", tradeId); member.put("SERIAL_NUMBER",
             * btd.getRD().getUca().getSerialNumber()); member.put("IN_TIME",SysDateMgr.getSysTime());
             * member.put("RELATION_TYPE_CODE", "56"); members.add(member);
             */

            Dao.insert("TP_F_UNIONPAY_MEMBER", members);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "14221001:本次办理和商务融合产品统付业务的成员已办理该业务，请耐心等待工单完工！");
        }
    }

}
