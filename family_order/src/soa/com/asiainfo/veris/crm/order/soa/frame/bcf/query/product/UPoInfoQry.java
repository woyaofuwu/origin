
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public final class UPoInfoQry
{
    /**
     * 通过商品编号查询全网的商品信息
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static IData queryPoDataBySpecNumber(String specNumber) throws Exception
    {
        IDataset poDatas = UpcCall.queryPoByPospecNumber(specNumber);
        if(IDataUtil.isNotEmpty(poDatas))
        {
            return poDatas.getData(0);
        }
        else
        {
            return new DataMap();
        }
    }
    
    /**
     * 通过商品编号查询全网的商品名称
     * @param specNumber
     * @return
     * @throws Exception
     */
    public static String queryPospecNameBySpecNumber(String specNumber) throws Exception
    {
        String pospecName = "";
        IData po = queryPoDataBySpecNumber(specNumber);
        if(IDataUtil.isNotEmpty(po))
        {
            pospecName = po.getString("POSPECNAME");
        }
        
        return pospecName;
    }
}
