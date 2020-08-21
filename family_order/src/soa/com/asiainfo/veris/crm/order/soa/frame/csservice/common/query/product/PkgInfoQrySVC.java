
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PkgInfoQrySVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 查询包名称
     */
    public IDataset getPackageNameByPackageId(IData param) throws Exception
    {
        IData map = new DataMap();
        String pkgName = PkgInfoQry.getPackageNameByPackageId(param.getString("PACKAGE_ID", ""));
        map.put("PACKAGE_NAME", pkgName);
        IDataset dataset = new DatasetList();
        dataset.add(map);
        return dataset;
    }

}
