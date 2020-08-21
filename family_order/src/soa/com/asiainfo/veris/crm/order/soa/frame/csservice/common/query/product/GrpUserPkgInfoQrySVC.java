
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GrpUserPkgInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询集团定制的产品
     * 
     * @author xunyl
     * @param data
     * @param eparchyCode
     * @return
     * @throws Exception
     * @date 2013-03-20
     */
    public static IDataset getGrpCustomizeProductByUserId(IData data) throws Exception
    {

        String userId = data.getString("USER_ID");
        return GrpUserPkgInfoQry.getGrpCustomizeProductByUserId(userId, null);
    }

    /**
     * 查询集团定制的包中相关优惠元素
     * 
     * @author weixb3s
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getGrpCustomizeDiscntByUserId(IData idata) throws Exception
    {
        String userId = idata.getString("USER_ID");
        return GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(userId, null);
    }

    /**
     * 查询集团定制的包中相关服务元素
     * 
     * @author weixb3
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getGrpCustomizeServByUserId(IData idata) throws Exception
    {
        String userId = idata.getString("USER_ID");
        return GrpUserPkgInfoQry.getGrpCustomizeServByUserId(userId, null);
    }

    /**
     * 查询集团定制的包中相关SP服务元素
     * 
     * @author weixb3
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getGrpCustomizeSpByUserId(IData idata) throws Exception
    {
        String userId = idata.getString("USER_ID");
        return GrpUserPkgInfoQry.getGrpCustomizeSpByUserId(userId, null);
    }
}
