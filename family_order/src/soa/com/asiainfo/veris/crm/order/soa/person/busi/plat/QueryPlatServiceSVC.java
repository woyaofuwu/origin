
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * 平台业务查询服务
 * 
 * @author xiekl
 */
public class QueryPlatServiceSVC extends CSBizService
{

    /**
     * 查询用户当前订购的平台服务
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryUserPlatSvcs(IData param) throws Exception
    {
        return QueryPlatServiceBean.qryUserPlatSvcs(param, this.getPagination());
    }
}
