
package com.asiainfo.veris.crm.iorder.soa.family.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

/**
 * @Description 参数转换工具类
 * @Auther: zhenggang
 * @Date: 2020/8/3 11:15
 * @version: V1.0
 */
public class ParamTransUtil
{
    /**
     * @Description: 找到角色商品列表中的主商品元素
     * @Param: [role]
     * @return: java.lang.String
     * @Author: zhenggang
     * @Date: 2020/8/3 11:21
     */
    public static String findRoleMainOffer(IData role) throws Exception
    {
        String offerStr = role.getString("OFFERS", "[]");
        IDataset offers = new DatasetList(offerStr);
        if (IDataUtil.isNotEmpty(offers))
        {
            for (Object obj : offers)
            {
                IData offer = (IData) obj;
                String elementTypeCode = offer.getString("ELEMENT_TYPE_CODE");
                if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
                {
                    return offer.getString("ELEMENT_ID");
                }
            }
        }

        return null;
    }
}
