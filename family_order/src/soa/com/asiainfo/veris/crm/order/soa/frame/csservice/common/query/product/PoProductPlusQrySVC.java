
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PoProductPlusQrySVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 根据产品编码查询poproductplus表的集团产品属性
     * 
     * @author ft
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryPoProductPlusByPospec(IData param) throws Exception
    {
        String productNpecNumber = param.getString("PRODUCTSPECNUMBER", "");
        return PoProductPlusQry.qryPoProductPlusByPospec(productNpecNumber);
    }
}
