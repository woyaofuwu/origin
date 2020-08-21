/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 */
public class PlatRelaSaleAction implements ITradeAction
{
    /* (non-Javadoc)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception {
    	MainTradeData mainTrade = btd.getMainTradeData();
        String userId = mainTrade.getUserId();
        List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        //System.out.println("-------------PlatRelaSaleAction---------------platSvcTradeDatas:"+platSvcTradeDatas);
        if (platSvcTradeDatas == null || platSvcTradeDatas.size() <= 0)
        {
            return;
        }
        for (int i = 0; i < platSvcTradeDatas.size(); i++)
        {
        	PlatSvcTradeData platSvcTradeData = platSvcTradeDatas.get(i);
        	if(BofConst.MODIFY_TAG_DEL.equals(platSvcTradeData.getModifyTag()))
        	{
                String serviceId = platSvcTradeData.getElementId();
        		IDataset commparaDataset1 = CommparaInfoQry.getCommparaByCode1("CSM", "1220", "1221", serviceId,null);
                //System.out.println("-------------PlatRelaSaleAction---------------commparaDataset1:"+commparaDataset1);

        		// 查询用户是否订购过购机营销活动, 并筛选出满足的营销活动
                IDataset saleActiveList = UserSaleActiveInfoQry.getUserSaleActivesBySelbyUserId(userId);
                IDataset commparaSaleActives1 = IDataUtil.filterByEqualsCol(saleActiveList, "PACKAGE_ID", commparaDataset1, "PARA_CODE3");
                IDataset commparaSaleActives2 = DataHelper.distinct(commparaSaleActives1, "PACKAGE_ID", "");
                //System.out.println("-------------PlatRelaSaleAction---------------commparaSaleActives2:"+commparaSaleActives2);
                for (int j = 0; j < commparaSaleActives2.size(); j++) {
                    IData commparaSaleActive = commparaSaleActives2.getData(j);

                    IData cancelParam = new DataMap();
                    cancelParam.put("SERIAL_NUMBER", mainTrade.getSerialNumber());
                    cancelParam.put("PRODUCT_ID", commparaSaleActive.getString("PRODUCT_ID"));
                    cancelParam.put("PACKAGE_ID", commparaSaleActive.getString("PACKAGE_ID"));
                    cancelParam.put("RELATION_TRADE_ID", commparaSaleActive.getString("RELATION_TRADE_ID"));
                    cancelParam.put("FORCE_END_DATE", SysDateMgr.getSysDate());
                    cancelParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    cancelParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    cancelParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    cancelParam.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getLoginEparchyCode());
                    cancelParam.put("REMARK", "退订平台业务，关联终止相应的营销活动");

                    IData callData = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam).getData(0);
                }
        	}
        }
       
    }
}
