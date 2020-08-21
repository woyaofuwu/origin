
package com.asiainfo.veris.crm.order.soa.group.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

/**
 * 物联网相关信息查询服务
 * 
 * @author 
 */
public final class WlwGrpConstants
{
	
	/** 物联网测试套餐的资费 */
    public static IData GRP_TEST_DISCNT_CONFIG = new DataMap();
    
    static
    {
        try
        {
        	GRP_TEST_DISCNT_CONFIG = WlwGrpBusiUtils.loadConfigData();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
