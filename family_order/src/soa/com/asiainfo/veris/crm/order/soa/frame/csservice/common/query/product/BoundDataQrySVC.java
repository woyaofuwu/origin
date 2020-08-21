
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BoundDataQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据paramcode查询bounddata表信息
     * 
     * @author fanti3
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryBoundDataByParamcode(IData param) throws Exception
    {
        String paramCode = param.getString("PARAMCODE");
        return BoundDataQry.qryBoundDataByParamcode(paramCode);
    }

    /**
     * 根据省属性编号，省编码和市属性编号查询相应的城市
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset qryBoundDataByProValueCityCode(IData input) throws Exception
    {
        String provinceAttrCode = input.getString("PROVINCE_ATTR_CODE");
        String provinceAttrValue = input.getString("PROVINCE_ATTR_VALUE");
        String cityAttrCode = input.getString("CITY_ATTR_CODE");

        return BoundDataQry.qryBoundDataByProValueCityCode(provinceAttrCode, provinceAttrValue, cityAttrCode);
    }
}
