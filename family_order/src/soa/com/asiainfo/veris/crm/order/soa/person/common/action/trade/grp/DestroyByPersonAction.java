
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.grp;

import java.util.Iterator;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.DataFactory;

public class DestroyByPersonAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", btd.getRD().getUca().getUserId());
        param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
        param.put("TRADE_TYPE_CODE", btd.getTradeTypeCode());
        param.put("ACCEPT_TIME", btd.getRD().getAcceptTime());

        IDataset grpTrade = CSAppCall.call("SS.GrpCreditSVC.destroyByPerson", param);
        // IDataset grpTrade = CSAppCall.call("http://10.200.130.83:10000/service", "SS.GrpCreditSVC.destroyByPerson",
        // param, true);
        if (IDataUtil.isNotEmpty(grpTrade))
        {
            IData data = grpTrade.getData(0);
            String tradeStr = data.getString("GRP_TRADE_DATA");
            IDataset tradeDataset = new DatasetList(tradeStr);
            if (IDataUtil.isNotEmpty(tradeDataset))
            {
                for (int i = 0, size = tradeDataset.size(); i < size; i++)
                {
                    IData tradeData = tradeDataset.getData(i);
                    Set<String> set = tradeData.keySet();
                    Iterator<String> iter = set.iterator();
                    if (iter.hasNext())
                    {
                        String key = iter.next();
                        IData idata = new DataMap(tradeData.getString(key));
                        if (IDataUtil.isNotEmpty(idata))
                        {
                            BaseTradeData tData = DataFactory.getInstance().getData(key, idata);
                            if (tData != null)
                            {
                                btd.add(btd.getRD().getUca().getSerialNumber(), tData);// 将集团返回的数据添加到busiTradeData中
                            }
                            else
                            {
                                CSAppException.apperr(TradeException.CRM_TRADE_95, key + "获取台账信息错误!");
                            }
                        }
                    }
                }
            }
        }
    }

}
