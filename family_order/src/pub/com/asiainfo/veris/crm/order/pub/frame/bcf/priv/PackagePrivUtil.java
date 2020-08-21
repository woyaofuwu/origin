
package com.asiainfo.veris.crm.order.pub.frame.bcf.priv;

import java.util.LinkedList;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class PackagePrivUtil
{
    /**
     * 根据员工工号过滤包列表并获取有权限的包
     * 
     * @param staffId
     * @param packageList
     * @throws Exception
     */
    public static void filterPackageListByPriv(String staffId, IDataset packageList) throws Exception
    {
        if (IDataUtil.isEmpty(packageList))
        {
            return;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return;
        }

        LinkedList<String> packageStrList = new LinkedList<String>();

        for (int i = 0, size = packageList.size(); i < size; i++)
        {
            IData packData = packageList.getData(i);
            String packageId = packData.getString("PACKAGE_ID");

            packageStrList.add(packageId);
        }

        Set<String> privPackSet = StaffPrivUtil.hasPkgPrivList(staffId, packageStrList);

        if (privPackSet == null || privPackSet.size() == 0)
        {
            packageList.clear();
            return;
        }

        int row = packageList.size();

        for (int i = row - 1; i >= 0; i--)
        {
            IData packData = packageList.getData(i);
            String packageId = packData.getString("PACKAGE_ID");

            if (!privPackSet.contains(packageId))// 没有权限
            {
                packageList.remove(i);
            }
        }

    }

}
