
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.undo;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

public class UndoDiscntStock implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {

		// TODO Auto-generated method stub
		int k = 0;
        String condFactor3 = "MFTY";
        String intfId = mainTrade.getString("INTF_ID","");
        if(StringUtils.isEmpty(intfId)){
        	return;
        }
        if(!(intfId.indexOf("TF_B_TRADE_DISCNT") >= 0)){//没有TF_B_TRADE_DISCNT台账 不处理
        	return;
        }
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        if( IDataUtil.isNotEmpty(discntTrades) ) {
            for (int i = 0; i < discntTrades.size(); i++) {
            	
            	IData discnt = discntTrades.getData(i);
            	DiscntTradeData discntTrade = new DiscntTradeData(discnt);
                            
                if("84010038".equals(discntTrade.getDiscntCode()) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                    k = k +1;
                }
                if("84010039".equals(discntTrade.getDiscntCode()) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                    k = k +2;
                }
                if("84010040".equals(discntTrade.getDiscntCode()) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                    k = k +3;
                }
            }
        }
        if ( k <= 0)
        {
            return;
        }
        else
        {
        	IDataset result = ActiveStockInfoQry.queryByResKind(condFactor3, CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isEmpty(result))
            {
            	return;
            }

            IData cond = new DataMap();
            cond.put("RES_KIND_CODE", condFactor3);
            cond.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            cond.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            cond.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
            StringBuilder sql = new StringBuilder(200);
            sql.append(" UPDATE TF_F_ACTIVE_STOCK");
            if (k==2){
            	sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U - 2");
            }
            else if (k==3)
            {
            	sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U - 3");
            }
            else
            {
            	sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U - 1");

            }
            sql.append(" WHERE STAFF_ID = :STAFF_ID");
            sql.append(" AND EPARCHY_CODE = :EPARCHY_CODE");
            sql.append(" AND RES_KIND_CODE = :RES_KIND_CODE");
            sql.append(" AND CITY_CODE = :CITY_CODE");
            Dao.executeUpdate(sql, cond);
        }

    }

}
