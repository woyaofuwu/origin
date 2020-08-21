
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MsisdnInfoQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 移动网内号码验证
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset getMsisonBySerialnumber(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        return IDataUtil.idToIds(MsisdnInfoQry.getMsisonBySerialnumber(serialNumber, null));
    }

    /**
     * 移动网内号码验证 ASP：运营商 1-移动 2-联通 3-电信 6-卫通
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset getMsisonBySerialnumberAsp(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String asp = input.getString("ASP");

        return IDataUtil.idToIds(MsisdnInfoQry.getMsisonBySerialnumberAsp(serialNumber, asp));
    }
    
    /**
     * 移动网内号码验证 ASP：运营商 1-移动 2-联通 3-电信 6-卫通
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IData getCrmMsisonBySerialnumber(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        return MsisdnInfoQry.getCrmMsisonBySerialnumber(serialNumber);
    }
    
    /**
     * ADC或MAS成员号段验证
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IData getCrmMsisonBySerialnumberlimit(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        return MsisdnInfoQry.getCrmMsisonBySerialnumberlimit(serialNumber);
    }
}
