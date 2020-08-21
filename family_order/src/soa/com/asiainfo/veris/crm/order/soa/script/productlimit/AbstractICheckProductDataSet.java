
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 抽象公用集合
 * @author: xiaocl
 */
public abstract class AbstractICheckProductDataSet extends DataMap
{

    enum CHECKPRODUCTDATASET
    {
        B_DISCNT_LIMIT("B_DISCNT_LIMITE"), // 是否走资费依赖互斥校验
        B_IS_CHECK_LIMIT("B_IS_CHECK_LIMIT"), // 是否走产品依赖互斥校验, 否者直接退出
        B_IS_ONLY_CHECK_SVC_STATE("B_IS_ONLY_CHECK_SVC_STATE"), // 是否只校验服务状态
        B_SERVICE("B_SERVICE"), // 是否走服务依赖互斥校验
        B_PACKAGE("B_PACKAGE"), // 是否走包依赖互斥校验
        B_IS_PKGINSIDE_ELMENETLIMIT("B_IS_PKGINSIDE_ELMENETLIMIT"), // 是否走全局元素校验

        LIST_USERALLPRODUCT("LIST_USERALLPRODUCT"), // 用户当前所有产品信息
        LIST_USERALLSERVICE("LIST_USERALLSERVICE"), // 用户当前所有服务信息
        LIST_USERALLDISCNT("LIST_USERALLDISCNT"), // 用户当前所有优惠信息
        LIST_USERALLPACKAGE("LIST_USERALLPACKAGE"), // 用户当前所有包信息
        LIST_USERALLELEMENT("LIST_USERALLELEMENT"), // 用户当前所有元素合集 Service + Discnt
        LIST_USERALLATTR("LIST_USERALLATTR"), // 用户当前所有属性
        LIST_USERALLSVCSTATE("LIST_USERALLSVCSTATE"), // 用户当前所有服务状态

        LIST_TRADESVC("LIST_TRADESVC"), LIST_TRADEDISCNT("LIST_TRADEDISCNT"), LIST_TRADEPRODUCT("LIST_TRADEPRODUCT"), LIST_TRADEPACKAGE("LIST_TRADEPACKAGE"), LIST_TRADEATTR("LIST_TRADEATTR"), LIST_TRADEELEMENT("LIST_TRADEELEMENT"),
        LIST_TRADESVCSTATE("LIST_TRADESVCSTATE");

        final String value;

        private CHECKPRODUCTDATASET(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    public AbstractICheckProductDataSet(Map<String, Object> map)
    {
    }

    public void init(IData databus, CheckProductDecision checkProductDecision) throws Exception
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLPRODUCT.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_USERALLSERVICE.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_USERALLDISCNT.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_USERALLPACKAGE.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_USERALLELEMENT.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_USERALLATTR.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_USERALLSVCSTATE.value, new DatasetList());

        put(CHECKPRODUCTDATASET.LIST_TRADESVC.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_TRADEDISCNT.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_TRADEPRODUCT.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_TRADEPACKAGE.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_TRADEATTR.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_TRADEELEMENT.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_TRADESVCSTATE.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_TRADEELEMENT.value, new DatasetList());
        put(CHECKPRODUCTDATASET.LIST_USERALLELEMENT.value, new DatasetList());

        if (databus.containsKey("TF_B_TRADE_SVC"))
        {                
        	IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
            BreQueryHelp.fillTradeProductIdAndPackageId(databus,listTradeSvc);
            put(CHECKPRODUCTDATASET.LIST_TRADESVC.value, listTradeSvc);
            if (listTradeSvc.size() > 0)
            {
                checkProductDecision.setBHasTradesvc(true);
            }
        }
        if (databus.containsKey("TF_B_TRADE_DISCNT"))
        {
        	IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
            BreQueryHelp.fillTradeProductIdAndPackageId(databus,listTradeDiscnt);
            put(CHECKPRODUCTDATASET.LIST_TRADEDISCNT.value, listTradeDiscnt);
            if (listTradeDiscnt.size() > 0)
            {
                checkProductDecision.setBHasTradeDiscnt(true);
            }
        }
        if (databus.containsKey("TF_B_TRADE_PRODUCT"))
        {
            put(CHECKPRODUCTDATASET.LIST_TRADEPRODUCT.value, databus.getDataset("TF_B_TRADE_PRODUCT"));
            if (databus.getDataset("TF_B_TRADE_PRODUCT").size() > 0)
            {
                checkProductDecision.setBHasTradeProduct(true);
            }
        }
        if (databus.containsKey("TF_B_TRADE_ATTR"))
        {
            put(CHECKPRODUCTDATASET.LIST_TRADEATTR.value, databus.getDataset("TF_B_TRADE_ATTR"));
        }
        if (databus.containsKey("TF_B_TRADE_PACKAGE"))
        {
            put(CHECKPRODUCTDATASET.LIST_TRADEPACKAGE.value, databus.getDataset("TF_B_TRADE_PACKAGE"));

        }
        if (databus.containsKey("TF_B_TRADE_SVCSTATE"))
        {
            put(CHECKPRODUCTDATASET.LIST_TRADESVCSTATE.value, databus.getDataset("TF_B_TRADE_SVCSTATE"));
        }
        if (databus.containsKey("TF_F_USER_PRODUCT_AFTER"))
        {
            put(CHECKPRODUCTDATASET.LIST_USERALLPRODUCT.value, databus.getDataset("TF_F_USER_PRODUCT_AFTER"));
        }
        if (databus.containsKey("TF_F_USER_SVC_AFTER"))
        {
            put(CHECKPRODUCTDATASET.LIST_USERALLSERVICE.value, databus.getDataset("TF_F_USER_SVC_AFTER"));
        }
        if (databus.containsKey("TF_F_USER_DISCNT_AFTER"))
        {
            put(CHECKPRODUCTDATASET.LIST_USERALLDISCNT.value, databus.getDataset("TF_F_USER_DISCNT_AFTER"));
        }
        if (databus.containsKey("TF_F_USER_ATTR_AFTER"))
        {
            put(CHECKPRODUCTDATASET.LIST_USERALLATTR.value, databus.getDataset("TF_F_USER_ATTR_AFTER"));
        }
        if (databus.containsKey("TF_F_USER_PACKAGE_AFTER"))
        {
            put(CHECKPRODUCTDATASET.LIST_USERALLPACKAGE.value, databus.getDataset("TF_F_USER_PACKAGE_AFTER"));
        }
        if (databus.containsKey("TF_F_USER_SVCSTATE_AFTER"))
        {
            put(CHECKPRODUCTDATASET.LIST_USERALLSVCSTATE.value, databus.getDataset("TF_F_USER_SVCSTATE_AFTER"));
        }
    }
}
