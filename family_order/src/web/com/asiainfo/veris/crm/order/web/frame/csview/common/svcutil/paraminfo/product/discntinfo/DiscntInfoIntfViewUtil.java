
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.discntinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.product.discntinfo.DiscntInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class DiscntInfoIntfViewUtil
{
    /**
     * 通过产品ID查询产品信息返回dataset
     * 
     * @param bc
     * @param productId
     * @return
     * @throws Exception
     */
    public static IDataset qryDiscntInfoByDisCode(IBizCommon bc, String discntCode) throws Exception
    {
        IDataset infosDataset = DiscntInfoIntf.qryDiscntInfoByDisCode(bc, discntCode);

        return infosDataset;
    }

    /**
     * 通过产品ID查询产品信息返回data
     * 
     * @param bc
     * @param productId
     * @param isThrowException
     *            ,true 查询不到数据抛出异常, false 查询不到数据返回null
     * @return
     * @throws Exception
     */
    public static IData qryDiscntInfoByDisCode(IBizCommon bc, String discntCode, boolean isThrowException) throws Exception
    {
        IData resultData = null;
        IDataset infosDataset = qryDiscntInfoByDisCode(bc, discntCode);
        if (IDataUtil.isNotEmpty(infosDataset))
        {
            resultData = infosDataset.getData(0);
        }
        if (IDataUtil.isEmpty(resultData))
        {
            if (isThrowException)
            {
                CSViewException.apperr(ProductException.CRM_PRODUCT_503, discntCode);
                return null;
            }
        }
        return resultData;
    }

    /**
     * 通过资费编码查询资费名称
     * 
     * @param bc
     * @param discntCode
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static String qryDiscntNameStrByDiscntCode(IBizCommon bc, String discntCode) throws Exception
    {
        return UpcViewCall.queryOfferNameByOfferId(bc, "D", discntCode); 
        
    }

}
