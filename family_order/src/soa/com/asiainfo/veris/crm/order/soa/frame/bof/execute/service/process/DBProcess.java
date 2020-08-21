
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTaxTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayMoneyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradePf;

public class DBProcess
{
    public static IData buildInsertDatas(BusiTradeData btd) throws Exception
    {
        OrderDataBus dataBus = DataBusManager.getDataBus();
        Map<String, List<BaseTradeData>> tradeDatasMap = btd.getTradeDatasMap();

        if (tradeDatasMap != null && !tradeDatasMap.isEmpty())
        {
            IData tableData = new DataMap();
            Iterator lter = tradeDatasMap.keySet().iterator();

            while (lter.hasNext())
            {
                IDataset regTradeDataSet = new DatasetList();
                String key = (String) lter.next();
                List<BaseTradeData> tradeDataList = (tradeDatasMap.get(key));

                if (tradeDataList != null)
                {
                    for (int j = 0, size = tradeDataList.size(); j < size; j++)
                    {
                        BaseTradeData regTradeData = tradeDataList.get(j);
                        IData regData = regTradeData.toData();

                        regData.put("TRADE_ID", btd.getRD().getTradeId());
                        regData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(btd.getRD().getTradeId()));
                        regData.put("ACCEPT_DATE", dataBus.getAcceptTime());
                        regData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                        regData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                        regData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                        regData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                        regData.put("UPDATE_TIME", dataBus.getAcceptTime());
                        regData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                        regData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

                        String tableName = regTradeData.getTableName();

                        if (TradeTableEnum.TRADE_MAIN.getValue().equals(tableName))
                        {
                            if (StringUtils.isBlank(regData.getString("INTF_ID")))
                            {
                                regData.put("INTF_ID", btd.getAllRegTableName());
                            }

                            regData.put("DAY", btd.getRD().getTradeId().substring(6, 8));// 为了bh_trade_staff表用
                        }

                        regTradeDataSet.add(regData);
                    }

                    tableData.put(key, regTradeDataSet);
                }
            }

            boolean isPf = TradePf.isPf(btd.getTradeTypeCode(), tableData);

            if (isPf)
            {
                tableData.getDataset("TF_B_TRADE").getData(0).put("OLCOM_TAG", "1");
            }
            else
            {
                tableData.getDataset("TF_B_TRADE").getData(0).put("OLCOM_TAG", "0");
            }
            return tableData;
        }
        else
        {
            return null;
        }
    }

    public static void insertCenTradeForYD() throws Exception
    {
        OrderDataBus dataBus = DataBusManager.getDataBus();

        // 对于主工单是省内异地业务的，所有工单需往中心库插台账
        if (!CSBizBean.getTradeEparchyCode().equals(CSBizBean.getUserEparchyCode()))
        {
            List<BusiTradeData> btds = dataBus.getBtds();
            for (BusiTradeData busiTradeData : btds)
            {
                // 台账主表
                IData mainTradeData = busiTradeData.getMainTradeData().toData();
                mainTradeData.put("TRADE_ID", busiTradeData.getRD().getTradeId());
                mainTradeData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(busiTradeData.getRD().getTradeId()));
                mainTradeData.put("ACCEPT_DATE", dataBus.getAcceptTime());
                mainTradeData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                mainTradeData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                mainTradeData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                mainTradeData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                mainTradeData.put("UPDATE_TIME", dataBus.getAcceptTime());
                mainTradeData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                mainTradeData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                Dao.insert("TF_B_TRADE", mainTradeData, Route.CONN_CRM_CEN);

                // 费用台账表
                List<FeeTradeData> feeList = busiTradeData.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
                IDataset feeRegDataList = new DatasetList();
                for (int i = feeList.size() - 1; i >= 0; i--)
                {
                    IData feeData = feeList.get(i).toData();
                    feeData.put("TRADE_ID", busiTradeData.getRD().getTradeId());
                    feeData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(busiTradeData.getRD().getTradeId()));
                    feeData.put("ACCEPT_DATE", dataBus.getAcceptTime());
                    feeData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    feeData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    feeData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    feeData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    feeData.put("UPDATE_TIME", dataBus.getAcceptTime());
                    feeData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    feeData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    feeRegDataList.add(feeData);
                }
                if (IDataUtil.isNotEmpty(feeRegDataList))
                {
                    Dao.insert("TF_B_TRADEFEE_SUB", feeRegDataList, Route.CONN_CRM_CEN);
                }

                // 台账税表
                List<FeeTaxTradeData> feeTaxTDList = busiTradeData.getTradeDatas(TradeTableEnum.TRADE_FEETAX);
                IDataset feeTaxRegDataList = new DatasetList();
                for (FeeTaxTradeData feeTaxTD : feeTaxTDList)
                {
                    IData feeTaxData = feeTaxTD.toData();
                    feeTaxData.put("TRADE_ID", busiTradeData.getRD().getTradeId());
                    feeTaxData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(busiTradeData.getRD().getTradeId()));
                    feeTaxData.put("ACCEPT_DATE", dataBus.getAcceptTime());
                    feeTaxData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    feeTaxData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    feeTaxData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    feeTaxData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    feeTaxData.put("UPDATE_TIME", dataBus.getAcceptTime());
                    feeTaxData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    feeTaxData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    feeTaxRegDataList.add(feeTaxData);
                }
                if (IDataUtil.isNotEmpty(feeTaxRegDataList))
                {
                    Dao.insert("TF_B_TRADEFEE_TAX", feeTaxRegDataList, Route.CONN_CRM_CEN);
                }

                // 付款方式表
                List<PayMoneyTradeData> payMoneyTDList = busiTradeData.getTradeDatas(TradeTableEnum.TRADE_PAYMONEY);
                IDataset payMoneyDataList = new DatasetList();
                for (int i = 0, size = payMoneyTDList.size(); i < size; i++)
                {
                    IData payMoneyData = payMoneyTDList.get(i).toData();
                    payMoneyData.put("TRADE_ID", busiTradeData.getTradeId());
                    payMoneyData.put("ORDER_ID", DataBusManager.getDataBus().getOrderId());
                    payMoneyData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(busiTradeData.getTradeId()));
                    payMoneyData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    payMoneyData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    payMoneyData.put("UPDATE_TIME", DataBusManager.getDataBus().getAcceptTime());

                    payMoneyDataList.add(payMoneyData);
                }
                if (IDataUtil.isNotEmpty(payMoneyDataList))
                {
                    Dao.insert("TF_B_TRADEFEE_PAYMONEY", payMoneyDataList, Route.CONN_CRM_CEN);
                }
            }
        }
    }

    /**
     * 根据btd及表数据进行插表
     * 
     * @param input
     * @param mainBtd
     * @param insertDatas
     * @throws Exception
     */
    public static void insertOrder(MainOrderData mainOrderData) throws Exception
    {
        OrderDataBus dataBus = DataBusManager.getDataBus();
        String orderId = dataBus.getOrderId();
        List<BusiTradeData> btds = dataBus.getBtds();
        int btdSize = btds.size();
        IData orderData = mainOrderData.toData();

        orderData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(mainOrderData.getOrderId()));
        orderData.put("ACCEPT_DATE", dataBus.getAcceptTime());
        orderData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        orderData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        orderData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        orderData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        orderData.put("UPDATE_TIME", dataBus.getAcceptTime());
        orderData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        orderData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        if (btds.size() > 1)
        {
            orderData.put("ORDER_KIND_CODE", BofConst.ORDER_KIND_CODE_MUTIL_TRADE);
        }

        Dao.insert(mainOrderData.getTableName(), orderData, Route.getJourDb());
    }

    public static void insertOrderSub(List<BusiTradeData> btds, String orderId, BusiTradeData mainBtd) throws Exception
    {
        int btdSize = btds.size();
        if (btdSize > 1)
        {
            // 如果工单大于1则往TF_B_ORDER_SUB插条记录
            for (BusiTradeData btd : btds)
            {
                MainTradeData mainTradeData = btd.getMainTradeData();
                IData orderSub = new DataMap();
                orderSub.put("ORDER_ID", orderId);
                orderSub.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(orderId));
                orderSub.put("TRADE_ID", btd.getTradeId());
                orderSub.put("ROUTE_ID", btd.getRoute());
                orderSub.put("EPARCHY_CODE", mainTradeData.getEparchyCode());
                orderSub.put("UPDATE_TIME", btd.getRD().getAcceptTime());
                orderSub.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                orderSub.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                if (btd.getTradeId().equals(mainBtd.getTradeId()))
                {
                    orderSub.put("MAIN_TAG", "1");
                }

                Dao.insert("TF_B_ORDER_SUB", orderSub, Route.getJourDb(Route.CONN_CRM_CG));
            }
        }
    }

    public static void insertTrades(IDataset insertDatas) throws Exception
    {
        for (int i = 0, size = insertDatas.size(); i < size; i++)
        {
            IData insertData = insertDatas.getData(i);
            String route = insertData.getString("ROUTE");
            IData tableData = insertData.getData("TABLE_DATA");

            if (tableData != null && !tableData.isEmpty())
            {
                Iterator lter = tableData.keySet().iterator();

                while (lter.hasNext())
                {
                    String tableName = (String) lter.next();
                    IDataset tableDatas = tableData.getDataset(tableName);

                    if (IDataUtil.isNotEmpty(tableDatas))
                    {
                        Dao.insert(tableName, tableDatas, Route.getJourDb(BizRoute.getRouteId()));

                        if (tableName.equals(TradeTableEnum.TRADE_MAIN.getValue()))
                        {
                            IData mainData = tableDatas.getData(0);
                            String tradeTypeCode = mainData.getString("TRADE_TYPE_CODE");
                            // 是否搬迁到历史2表
                            boolean bSecond = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_FINISH_MOVE_SECOND, false);
                            // 如果是，则不插trade_bh_staff表
                            if (bSecond == false)
                            {
                                Dao.insert("TF_BH_TRADE_STAFF", mainData, Route.getJourDb(BizRoute.getTradeEparchyCode()));
                            }
                        }
                    }
                }
            }
        }
    }
}
