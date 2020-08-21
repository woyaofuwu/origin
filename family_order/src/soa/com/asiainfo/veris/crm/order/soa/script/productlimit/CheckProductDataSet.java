
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 強弱對象轉化和統一管理
 * @author: xiaocl
 */
public class CheckProductDataSet extends AbstractICheckProductDataSet implements ICheckProductDataSet
{

    private static Logger logger = Logger.getLogger(CheckProductDataSet.class);

    private IData databus = new DataMap();

    public CheckProductDataSet(Map<String, Object> map, CheckProductDecision checkProductDecision) throws Exception
    {
        super(map);
        databus = (IData) map;
        super.init(databus, checkProductDecision);
    }

    @SuppressWarnings("unchecked")
    public IDataset getUserAllAttrList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_USERALLATTR.value);
    }

    @SuppressWarnings("unchecked")
    public IDataset getUserAllDiscntList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_USERALLDISCNT.value);
    }

    @SuppressWarnings("unchecked")
    public IDataset getUserAllElementList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_USERALLELEMENT.value);
    }

    @SuppressWarnings("unchecked")
    public IDataset getUserAllPackageList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_USERALLPACKAGE.value);
    }

    @SuppressWarnings("unchecked")
    public IDataset getUserAllProductList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_USERALLPRODUCT.value);
    }

    @SuppressWarnings("unchecked")
    public IDataset getUserAllServiceList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_USERALLSERVICE.value);
    }

    @SuppressWarnings("unchecked")
    public IDataset getUserAllSvcstateList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_USERALLSVCSTATE.value);
    }

    public IDataset getUserTradeAttrList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_TRADEATTR.value);
    }

    public IDataset getUserTradeDiscntList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_TRADEDISCNT.value);
    }

    public IDataset getUserTradeElementList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_TRADEELEMENT.value);
    }

    public IDataset getUserTradePackageList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_TRADEPACKAGE.value);
    }

    public IDataset getUserTradeProductList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_TRADEPRODUCT.value);
    }

    public IDataset getUserTradeSvcList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_TRADESVC.value);
    }

    public IDataset getUserTradeSvcstateList()
    {
        return getDataset(CHECKPRODUCTDATASET.LIST_TRADESVCSTATE.value);
    }

    @SuppressWarnings("unchecked")
    public void setTradeAttrList(IDataset list)
    {
        put(CHECKPRODUCTDATASET.LIST_TRADEATTR.value, list);
    }

    @SuppressWarnings("unchecked")
    public void setTradeDiscntList(IDataset list)
    {
        put(CHECKPRODUCTDATASET.LIST_TRADEDISCNT.value, list);
    }

    public void setTradeElementList(IDataset list)
    {
        put(CHECKPRODUCTDATASET.LIST_TRADEELEMENT.value, list);
    }

    @SuppressWarnings("unchecked")
    public void setTradePackageList(IDataset list)
    {
        put(CHECKPRODUCTDATASET.LIST_TRADEPACKAGE.value, list);
    }

    @SuppressWarnings("unchecked")
    public void setTradeProductList(IDataset list)
    {
        put(CHECKPRODUCTDATASET.LIST_TRADEPRODUCT.value, list);
    }

    @SuppressWarnings("unchecked")
    public void setTradeSvcList(IDataset list)
    {
        put(CHECKPRODUCTDATASET.LIST_TRADESVC.value, list);
    }

    @SuppressWarnings("unchecked")
    public void setTradeSvcstateList(IDataset list)
    {
        put(CHECKPRODUCTDATASET.LIST_TRADESVCSTATE.value, list);
    }

    @SuppressWarnings("unchecked")
    public void setUserAllAttrList(IDataset userAllAttrList)
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLATTR.value, userAllAttrList);
    }

    @SuppressWarnings("unchecked")
    public void setUserAllDiscntList(IDataset userAllDiscntList)
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLDISCNT.value, userAllDiscntList);
    }

    @SuppressWarnings("unchecked")
    public void setUserAllElementList(IDataset userAllElementList)
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLELEMENT.value, userAllElementList);
    }

    @SuppressWarnings("unchecked")
    public void setUserAllPackageList(IDataset userAllPackageList)
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLPACKAGE.value, userAllPackageList);
    }

    @SuppressWarnings("unchecked")
    public void setUserAllProductList(IDataset userAllProductlist)
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLPRODUCT.value, userAllProductlist);
    }

    @SuppressWarnings("unchecked")
    public void setUserAllServiceList(IDataset userAllServiceList)
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLSERVICE.value, userAllServiceList);
    }

    @SuppressWarnings("unchecked")
    public void setUserAllSvcstateList(IDataset userAllSvcstateList)
    {
        put(CHECKPRODUCTDATASET.LIST_USERALLSVCSTATE.value, userAllSvcstateList);
    }

}
