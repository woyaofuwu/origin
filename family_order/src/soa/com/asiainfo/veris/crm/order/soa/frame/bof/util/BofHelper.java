
package com.asiainfo.veris.crm.order.soa.frame.bof.util;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BofHelper
{

    /**
     * * 判断是否预受理业务
     * 
     * @param req
     * @return
     * @throws Exception
     */
    public static boolean isNotPreTrade(BaseReqData req) throws Exception
    {
        return !isPreTrade(req);
    }

    /**
     * * 判断是否预受理业务
     * 
     * @param req
     * @return
     * @throws Exception
     */
    public static boolean isPreTrade(BaseReqData req) throws Exception
    {
        boolean isPreFlag = false;
        if (StringUtils.isBlank(req.getPreType()) || "true".equals(req.getIsConfirm()))
        {
            isPreFlag = false;
        }
        else
        {
            isPreFlag = true;
        }

        return isPreFlag;
    }
}
