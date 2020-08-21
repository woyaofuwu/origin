/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 */
public class CancelSaleActiveAction implements ITradeFinishAction
{
    /* (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        IDataset platSvcTradeDatas1 = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
        IDataset platSvcTradeDatas2 =  DataHelper.filter(platSvcTradeDatas1, "OPER_CODE="+ PlatConstants.OPER_CANCEL_ORDER);

        // 捞取TD_S_COMMPARA表1220数据，并筛选出满足的配置
        IDataset commparaDataset1 = CommparaInfoQry.getCommpara("CSM", "1220", "1220", CSBizBean.getTradeEparchyCode());
        IDataset commparaDataset2 = IDataUtil.filterByEqualsCol(commparaDataset1, "PARA_CODE1", platSvcTradeDatas2, "SERVICE_ID");
        IDataset commparaDataset3 = DataHelper.filter(commparaDataset2, "PARA_CODE8=" + "2");

        // 查询用户是否订购过购机营销活动, 并筛选出满足的营销活动
        IDataset saleActiveList = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
        IDataset commparaSaleActives1 = IDataUtil.filterByEqualsCol(saleActiveList, "PRODUCT_ID", commparaDataset3, "PARA_CODE2");
        IDataset commparaSaleActives2 = DataHelper.distinct(commparaSaleActives1, "PRODUCT_ID", "");

        for (int i = 0; i < commparaSaleActives2.size(); i++) {
            IData commparaSaleActive = commparaSaleActives2.getData(i);

            IData cancelParam = new DataMap();
            cancelParam.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
            cancelParam.put("PRODUCT_ID", commparaSaleActive.getString("PRODUCT_ID"));
            cancelParam.put("PACKAGE_ID", commparaSaleActive.getString("PACKAGE_ID"));
            cancelParam.put("RELATION_TRADE_ID", commparaSaleActive.getString("RELATION_TRADE_ID"));
            cancelParam.put("FORCE_END_DATE", SysDateMgr.getSysDate());
            cancelParam.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
            cancelParam.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
            cancelParam.put("TRADE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
            cancelParam.put("TRADE_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
            cancelParam.put("REMARK", "营销活动取消");

            IData callData = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam).getData(0);
        }
    }
}
