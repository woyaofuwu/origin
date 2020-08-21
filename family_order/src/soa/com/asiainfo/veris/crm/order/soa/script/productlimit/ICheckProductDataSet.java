
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IDataset;

public interface ICheckProductDataSet
{
    // public abstract String getValue();
    // public abstract String toString();

    public abstract IDataset getUserAllAttrList();

    public abstract IDataset getUserAllDiscntList();

    public abstract IDataset getUserAllElementList();

    public abstract IDataset getUserAllPackageList();

    public abstract IDataset getUserAllProductList();

    public abstract IDataset getUserAllServiceList();

    public abstract IDataset getUserAllSvcstateList();

    public abstract IDataset getUserTradeAttrList();

    public abstract IDataset getUserTradeDiscntList();

    public abstract IDataset getUserTradeElementList();

    public abstract IDataset getUserTradePackageList();

    public abstract IDataset getUserTradeProductList();

    public abstract IDataset getUserTradeSvcList();

    public abstract IDataset getUserTradeSvcstateList();

    public abstract void setTradeAttrList(IDataset list);

    public abstract void setTradeDiscntList(IDataset list);

    public abstract void setTradeElementList(IDataset list);

    public abstract void setTradePackageList(IDataset list);

    public abstract void setTradeProductList(IDataset list);

    public abstract void setTradeSvcList(IDataset list);

    public abstract void setTradeSvcstateList(IDataset list);

    public abstract void setUserAllAttrList(IDataset userAllAttrList);

    public abstract void setUserAllDiscntList(IDataset userAllDiscntList);

    public abstract void setUserAllElementList(IDataset userAllElementList);

    public abstract void setUserAllPackageList(IDataset userAllPackageList);

    public abstract void setUserAllProductList(IDataset userAllProductlist);

    public abstract void setUserAllServiceList(IDataset userAllServiceList);

    public abstract void setUserAllSvcstateList(IDataset userAllSvcstateList);

}
