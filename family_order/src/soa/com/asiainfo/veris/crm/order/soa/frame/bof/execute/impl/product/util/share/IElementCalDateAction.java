
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.share;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;

public interface IElementCalDateAction
{
    public String calElementDate(ProductModuleData pmd, ProductTimeEnv env, UcaData uca, List<ProductModuleData> pmds) throws Exception;
}
