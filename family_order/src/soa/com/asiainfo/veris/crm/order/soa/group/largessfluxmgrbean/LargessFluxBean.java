
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

/**
 * 畅享流量
 * 
 * @author
 */
public class LargessFluxBean extends GroupBean
{

    /**
     * 查询需要分配总流量
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUserGrpGfffInfo(IData data) throws Exception{
        IDataset infos = LargessFluxQry.queryUserGrpGfffInfo(data);
        return infos;
    }
    
    /**
     * 分页查询需要分配流量
     * @param dt
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserGrpGfffInfo(IData data, Pagination pagination) throws Exception
    {
        IDataset resultInfos = LargessFluxQry.qryUserGrpGfffInfo(data, pagination);
        return resultInfos;
    }
    
    /**
     * 分页查询需要分配流量
     * @param dt
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserGrpSubGfffInfo(IData data, Pagination pagination) throws Exception
    {
        IDataset resultInfos = LargessFluxQry.qryUserGrpSubGfffInfo(data, pagination);
        return resultInfos;
    }
    
}
