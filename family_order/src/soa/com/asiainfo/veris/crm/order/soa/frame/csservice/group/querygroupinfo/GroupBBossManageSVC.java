
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.querygroupinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * @author jch
 */
public class GroupBBossManageSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 取消专线订单
     * 
     * @param param
     * @throws Exception
     */
    public IDataset cancelSend(IData param) throws Exception
    {
        GroupBBossManage manage = new GroupBBossManage();

        manage.cancelSend(param);

        return null;

    }

    /**
     * 发送受理报文前，先处理相关的台账信息
     * 
     * @param param
     * @throws Exception
     */
    public IDataset updataCreateBefor(IData param) throws Exception
    {
        GroupBBossManage manage = new GroupBBossManage();

        manage.updataCreateBefor(param);

        return null;

    }
}
