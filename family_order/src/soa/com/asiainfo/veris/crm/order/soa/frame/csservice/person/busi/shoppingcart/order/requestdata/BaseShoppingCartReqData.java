
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.shoppingcart.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.shoppingcart.BusiOrderModuleData;

public class BaseShoppingCartReqData extends BaseReqData
{
    private List<BusiOrderModuleData> busiOrderList;

    public List<BusiOrderModuleData> getBusiOrderList()
    {
        if (CollectionUtils.isEmpty(this.busiOrderList))
        {
            this.busiOrderList = new ArrayList<BusiOrderModuleData>();
        }
        return this.busiOrderList;
    }

    public void setBusiOrderList(List<BusiOrderModuleData> busiOrderList)
    {
        this.busiOrderList = busiOrderList;
    }

}
