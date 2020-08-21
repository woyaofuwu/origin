
package com.asiainfo.veris.crm.order.soa.frame.bof.rule;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

/**
 * @author Administrator
 */
public interface IProductModuleBeforeRule
{
    public void checkProductModuleBeforeRule(ProductModuleData pmd, List<ProductModuleData> pmtds, BaseReqData brd) throws Exception;
}
