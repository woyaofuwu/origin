
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.cancelchangeproduct.CancelChangeProductBean;

public class QueryUserCancelTradeSVC extends CSBizService
{
    /**
     * 预约业务查询接口---xuwb5
     * 
     * @param data
     *            SERIAL_NUMBER 手机号码 SUBSCRIBE_TYPE TRADE_EPARCHY_CODE 必传
     * @return
     * @throws Exception
     */
    public IData queryUserCancelTrade(IData data) throws Exception
    {

        IData result = new DataMap();

        if (!data.containsKey("SERIAL_NUMBER") || !data.containsKey("SUBSCRIBE_TYPE") || !data.containsKey("TRADE_EPARCHY_CODE"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1125);
        }
        String serialNumber = data.getString("SERIAL_NUMBER");
        String subscribeType = data.getString("SUBSCRIBE_TYPE");
        String tradeEparchyCode = data.getString("TRADE_EPARCHY_CODE");

        // 存在错单的情况
        IDataset ErrorInfos = TradeInfoQry.queryErrorTrade(serialNumber);
        if (IDataUtil.isNotEmpty(ErrorInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1123);
        }
        // 取消的预约业务
        /*
         * IDataset datas = TradeInfoQry.queryUserCancelTrade(serialNumber, subscribeType, tradeEparchyCode, null);
         * IDataset trades = new DatasetList(); for(int i=0;i<datas.size();i++){ IData param = datas.getData(i); String
         * tradeTypeCode=param.getString("TRADE_TYPE_CODE"); String subScribe_state=param.getString("SUBSCRIBE_STATE");
         * if("110".equals(tradeTypeCode)&&"0".equals(subScribe_state)){ trades.add(param); } }
         */
        IDataset trades = new DatasetList();
        CancelChangeProductBean bean = BeanManager.createBean(CancelChangeProductBean.class);
        trades = bean.queryChangeProductTrade(serialNumber);

        IDataset productList = new DatasetList();
        IDataset serviceList = new DatasetList();
        IDataset discntList = new DatasetList();
        if (IDataUtil.isNotEmpty(trades))
        {
            productList = TradeProductInfoQry.getTradeProductInfosByTradeId(trades.getData(0).getString("TRADE_ID"));
            serviceList = TradeSvcInfoQry.getTradeSvcInfosByTradeId(trades.getData(0).getString("TRADE_ID"));
            discntList = TradeDiscntInfoQry.getTradeDiscntInfosByTradeId(trades.getData(0).getString("TRADE_ID"));
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1124);
        }

        // 产品名称
        for (int i = 0; i < productList.size(); i++)
        {
            IData dataTmp = new DataMap();
            dataTmp = productList.getData(i);

            productList.getData(i).put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(dataTmp.getString("PRODUCT_ID")));
            productList.getData(i).put("OLD_PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(dataTmp.getString("OLD_PRODUCT_ID")));
        }

        // 服务名称
        for (int i = 0; i < serviceList.size(); i++)
        {
            IData dataTmp = new DataMap();
            dataTmp = serviceList.getData(i);
            serviceList.getData(i).put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(dataTmp.getString("SERVICE_ID")));
        }

        // 优惠名称
        for (int i = 0; i < discntList.size(); i++)
        {
            IData dataTmp = new DataMap();
            dataTmp = discntList.getData(i);
            discntList.getData(i).put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(dataTmp.getString("DISCNT_CODE")));
        }

        IData CancelTrade = new DataMap();
        CancelTrade.put("PRODUCTLIST", productList);
        CancelTrade.put("SERVICELIST", serviceList);
        CancelTrade.put("DISCNTLIST", discntList);
        CancelTrade.put("TRADES", trades);

        result.put("CANCELTRADE", CancelTrade);

        return result;

    }
}
