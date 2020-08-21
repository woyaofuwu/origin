
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;

public class BusiOrderModuleData
{
    private String shoppingCartId;

    private String orderId;

    private String busiTypeCode;

    private String orderTypeCode;

    private String shoppingStateCode;

    private String orderRegSvc;

    private IData pageInputData;

    private List<BusiElementModuleData> elementList;

    public BusiOrderModuleData()
    {

    }

    public BusiOrderModuleData(IData data)
    {
        this.shoppingCartId = data.getString("shoppingCartId");
        this.orderId = data.getString("busiOrderId");
        this.orderTypeCode = data.getString("busiOrderType");
        this.orderRegSvc = data.getString("orderRegSvc");
        this.busiTypeCode = data.getString("busiTypeCode");
        this.shoppingStateCode = data.getString("shoppingStateCode");
    }

    public IDataset elementListToDataset()
    {
        IDataset dataset = new DatasetList();
        if (CollectionUtils.isNotEmpty(this.elementList))
        {
            for (int index = 0, size = this.elementList.size(); index < size; index++)
            {
                BusiElementModuleData bemd = this.elementList.get(index);
                dataset.add(bemd.toData());
            }
        }
        return dataset;
    }

    public String getBusiTypeCode()
    {
        return busiTypeCode;
    }

    public List<BusiElementModuleData> getElementList()
    {
        if (CollectionUtils.isEmpty(this.elementList))
        {
            this.elementList = new ArrayList<BusiElementModuleData>();
        }
        return this.elementList;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public String getOrderRegSvc()
    {
        return orderRegSvc;
    }

    public String getOrderTypeCode()
    {
        return orderTypeCode;
    }

    public IData getPageInputData()
    {
        return pageInputData;
    }

    public String getShoppingCartId()
    {
        return shoppingCartId;
    }

    public String getShoppingStateCode()
    {
        return shoppingStateCode;
    }

    public void setBusiTypeCode(String busiTypeCode)
    {
        this.busiTypeCode = busiTypeCode;
    }

    public void setElementList(List<BusiElementModuleData> elementList)
    {
        this.elementList = elementList;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public void setOrderRegSvc(String orderRegSvc)
    {
        this.orderRegSvc = orderRegSvc;
    }

    public void setOrderTypeCode(String orderTypeCode)
    {
        this.orderTypeCode = orderTypeCode;
    }

    public void setPageInputData(IData pageInputData)
    {
        this.pageInputData = pageInputData;
    }

    public void setShoppingCartId(String shoppingCartId)
    {
        this.shoppingCartId = shoppingCartId;
    }

    public void setShoppingStateCode(String shoppingStateCode)
    {
        this.shoppingStateCode = shoppingStateCode;
    }

}
