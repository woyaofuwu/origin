
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.netnp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: UserNetNpSVC.java
 * @Description: 携转用户资料处理
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午04:18:06 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-10-21 liuke v1.0.0 修改原因
 */
public class UserNetNpMgrSVC extends CSBizService
{

    /**
     * 终止用户携转资料：给资源号码回收时使用
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset endUserNetNpInfo(IData input) throws Exception
    {
        return UserNetNpMgrBean.endUserNetNpInfo(input.getString("SERIAL_NUMBER"));
    }
}
