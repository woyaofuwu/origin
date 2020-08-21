
package com.asiainfo.veris.crm.order.pub.frame.bcf.priv;

import java.util.LinkedList;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class ProductPrivUtil
{
    /**
     * 根据员工工号过滤产品列表并获取有权限的产品
     * 
     * @param staffId
     * @param productList
     * @throws Exception
     */
    public static void filterProductListByPriv(String staffId, IDataset productList) throws Exception
    {

        if (IDataUtil.isEmpty(productList))
        {
            return;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return;
        }

        LinkedList<String> productStrList = new LinkedList<String>();

        for (int i = 0, size = productList.size(); i < size; i++)
        {
            String productId = productList.getData(i).getString("PRODUCT_ID");
            productStrList.add(productId);
        }

        Set<String> privProductSet = StaffPrivUtil.hasProdPrivList(staffId, productStrList);

        if (privProductSet == null || privProductSet.size() == 0)
        {
            productList.clear();
            return;
        }

        int row = productList.size();

        for (int i = row - 1; i >= 0; i--)
        {
            String productId = productList.getData(i).getString("PRODUCT_ID");

            if (!privProductSet.contains(productId))// 没有权限
            {
                productList.remove(i);
            }
        }
    }

    /**
     * 根据员工工号判断产品列表中是否有可办理的产品
     * 
     * @param productList
     *            产品列表
     * @param staffId
     *            员工工号
     * @return
     * @throws Exception
     */
    public static boolean isProudctPriv(String staffId, IDataset productList) throws Exception
    {

        if (IDataUtil.isEmpty(productList))
        {
            return false;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return true;
        }

        LinkedList<String> productStrList = new LinkedList<String>();

        for (int i = 0; i < productList.size(); i++)
        {
            IData productInfo = productList.getData(i);

            productStrList.add(productInfo.getString("PRODUCT_ID", ""));
        }

        Set<String> privProductSet = StaffPrivUtil.hasProdPrivList(staffId, productStrList);

        if (privProductSet == null || privProductSet.size() == 0)
        {
            return false;
        }

        return true;
    }

}
