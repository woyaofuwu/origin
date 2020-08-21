
package com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UAttrBizInfoQry;

public class BizCtrlBean
{
    /**
     * 获取产品控制信息
     * 
     * @param id
     * @param idType
     * @param attrObj
     * @return
     * @throws Exception
     */
    public static BizCtrlInfo getBizCtrlInfo(String id, String idType, String attrObj) throws Exception
    {
        // 构造
        BizCtrlInfo ctrlInfo = new BizCtrlInfo();

        IDataset ids = UAttrBizInfoQry.getBizAttrByIdTypeObj(id, idType, attrObj, null);

        // 没有配置
        if (IDataUtil.isEmpty(ids))
        {
            return ctrlInfo;
        }

        Iterator<Object> iterator = ids.iterator();

        if (!iterator.hasNext())
        {
            return ctrlInfo;
        }

        IData data = null;
        String key = "";
        IData crt = ctrlInfo.getCtrlInfo();

        while (iterator.hasNext())
        {
            // key value
            data = (IData) iterator.next();
            key = data.getString("ATTR_CODE", "");

            crt.put(key, data);
        }

        return ctrlInfo;
    }

    /**
     * 获取产品控制信息
     * 
     * @param productId
     * @param attrObj
     * @return
     * @throws Exception
     */
    public static BizCtrlInfo qryProductCtrlInfo(String productId, String attrObj) throws Exception
    {
        BizCtrlInfo ctrlInfo = getBizCtrlInfo(productId, "P", attrObj);

        return ctrlInfo;
    }

}
