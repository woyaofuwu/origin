
package com.asiainfo.veris.crm.order.soa.person.busi.plat.platsyn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PlatOrderRelationSVC extends CSBizService
{
	private static final long serialVersionUID = 175290490258472162L;

	/**
     * 批量导入平台业务订购关系
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData importPlatOrderRelation(IData param) throws Exception
    {
        PlatOrderRelationBean.importPlatOrderRelation(param.getString("FILE_NAME"), param.getDataset("PLAT_SYN"));
        return new DataMap();
    }
    
    /**
     * 平台业务订购关系同步批量启动
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData startBatDeal(IData param) throws Exception
    {
    	PlatOrderRelationBean.startBatDeal(param);
    	return new DataMap();
    }

    /**
     * 查询平台业务订购关系同步批量信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryPlatOrderRelationBat(IData param) throws Exception
    {
        return PlatOrderRelationBean.queryPlatOrderRelationBat(param, this.getPagination());
    }
    
    /**
     * 查询平台业务订购关系同步导入详情
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryPlatOrderRelationBatDtl(IData param) throws Exception
    {
        return PlatOrderRelationBean.queryPlatOrderRelationBatDtl(param, this.getPagination());
    }
    
    /**
     * 根据主键查询批次信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData qryPlatDataBatByPK(IData data) throws Exception
    {
        return PlatOrderRelationBean.qryPlatDataBatByPK(data);
    }
}
