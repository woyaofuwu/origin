
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BrandInfoQrySVC extends CSBizService
{
	private static final long serialVersionUID = 8605141085458612718L;
	/**
	 * 根据品牌编码查询品牌名称
	 * @param input
	 * @return
	 * @throws Exception
	 * @Author:chenzg
	 * @Date:2017-4-13
	 */
	public static IDataset queryBrandByBrandCode(IData input) throws Exception
    {
		IDataUtil.chkParam(input, "BRAND_CODE");
        String brandCode = input.getString("BRAND_CODE");
        return BrandInfoQry.queryBrandByBrandCode(brandCode);
    }
}
