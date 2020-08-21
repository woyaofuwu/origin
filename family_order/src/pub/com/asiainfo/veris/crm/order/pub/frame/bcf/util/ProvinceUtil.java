
package com.asiainfo.veris.crm.order.pub.frame.bcf.util;

import com.ailk.biz.BizEnv;
import com.ailk.common.config.SystemCfg;
import com.ailk.org.apache.commons.lang3.StringUtils;

public final class ProvinceUtil
{
    public final static String HAIN = "HAIN"; // 海南

    public final static String HNAN = "HNAN"; // 湖南

    public final static String QHAI = "QHAI"; // 青海

    public final static String SHXI = "SHXI"; // 陕西

    public final static String TJIN = "TJIN"; // 天津

    public final static String XINJ = "XINJ"; // 新疆

    public final static String YUNN = "YUNN"; // 云南

    /**
     * 得到本省省编码
     * 
     * @return
     * @throws Exception
     */
    public final static String getProvinceCode()
    {
        String provinceCode = SystemCfg.provinceCode;

        if (StringUtils.isBlank(provinceCode))
        {
            provinceCode = "";
        }

        return provinceCode;
    }

    /**
     * 得到集团公司省编码
     * 
     * @return
     * @throws Exception
     */
    public final static String getProvinceCodeGrpCorp() throws Exception
    {
        String provcode = BizEnv.getEnvString("crm.grpcorp.provincecode");
        return provcode;
    }

    /**
     * 判断是否本省
     * 
     * @return
     * @throws Exception
     */
    public final static Boolean isProvince(String userProvinceCode) throws Exception
    {
        String provinceCode = getProvinceCode();

        return userProvinceCode.equals(provinceCode);
    }
}
