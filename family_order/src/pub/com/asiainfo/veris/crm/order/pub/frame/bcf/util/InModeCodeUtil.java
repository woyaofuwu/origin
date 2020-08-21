
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 接入渠道判断核心类
 * 
 * @author huyong
 */
public final class InModeCodeUtil
{

    /**
     * 判断是否接口
     * 
     * @param inModeCode
     *            接入编码
     * @param xSubTransCode
     *            子交易编码
     * @param batchId
     *            批次号
     * @return
     */
    public static boolean isIntf(String inModeCode, String xSubTransCode, String batchId)
    {
        boolean isIntf = false;

        if (isNotSale(inModeCode) || StringUtils.isNotBlank(xSubTransCode) || StringUtils.isNotBlank(batchId))
        {
            isIntf = true;
        }

        return isIntf;
    }

    /**
     * 非营业前台(客服)判断
     * 
     * @param inModeCode
     * @return
     */
    public static boolean isNotSale(String inModeCode)
    {
        if (StringUtils.isBlank(inModeCode))
        {
            Utility.error("-1", null, "inModeCode不能为空!");
        }
        boolean result = true;

        if ("0".equals(inModeCode) || "1".equals(inModeCode))
        {
            result = false;
        }
        return result;
    }

    /**
     * 不是营业前台,不是批量
     * 
     * @param inModeCode
     * @param batchId
     * @return
     */
    public static boolean isNotSaleAndBatch(String inModeCode, String batchId)
    {
        boolean reulst = isNotSale(inModeCode);
        if (reulst && StringUtils.isBlank(batchId))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 是否重新计算时间
     * 
     * @param inModeCode
     * @param batchId
     * @return
     */
    public static boolean isReDate(String inModeCode, String batchId)
    {
        boolean reulst = isNotSale(inModeCode);
        if (reulst && StringUtils.isNotBlank(batchId))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static void main(String[] args) throws Exception
    {
        String inModeCode = "3";
        String batchId = "";
        boolean reuslt = isNotSaleAndBatch(inModeCode, batchId);
    }
}
