
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.bbossattrinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.bbossattrinfo.BBossAttrInfoIntf;

public class BBossAttrInfoIntfViewUtil
{

    /**
     * 通过productId operType bizType 参数，查询BBOSS_ATTR表的参数信息
     * 
     * @param bc
     * @param productId
     * @param operType
     * @param bizType
     * @return
     * @throws Exception
     */
    public static IDataset qryBBossAttrInfosByProductIdAndOperTypeBizType(IBizCommon bc, String productId, String operType, String bizType) throws Exception
    {
        IDataset infosDataset = BBossAttrInfoIntf.qryBBossAttrInfosByProductIdAndOperTypeBizType(bc, productId, operType, bizType);
        return infosDataset;
    }

}
