/**
 * 
 */

package com.asiainfo.veris.crm.order.pub.frame.bcf.set.util;

import com.ailk.common.data.IDataset;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DataSetUtils.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-1-20 下午03:34:25 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
 */
public final class DataSetUtils
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 下午03:34:33 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public static boolean isBlank(IDataset dataSet)
    {

        if (dataSet == null || dataSet.size() <= 0)
        {

            return true;

        }

        return false;

    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-1-20 下午03:34:40 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-1-20 chengxf2 v1.0.0 修改原因
     */
    public static boolean isNotBlank(IDataset dataSet)
    {

        return (!isBlank(dataSet));

    }
}
