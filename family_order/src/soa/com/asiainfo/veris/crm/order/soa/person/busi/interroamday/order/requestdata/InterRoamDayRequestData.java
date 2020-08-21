
package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

/**
 * @author think
 */
public class InterRoamDayRequestData extends BaseReqData
{
    List<ProductModuleData> discntDatas = new ArrayList<ProductModuleData>();

    public List<ProductModuleData> getDiscntDatas()
    {
        return discntDatas;
    }

    public void setDiscntDatas(List<ProductModuleData> discntDatas)
    {
        this.discntDatas = discntDatas;
    }

}
