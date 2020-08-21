
package com.asiainfo.veris.crm.order.soa.group.groupintf.gfffgrpmembatmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class GfffGrpMemBatMgrSVC extends CSBizService
{
    
    private static final long serialVersionUID = 1L;

    /**
     * 流量自由充(限量统付)产品成员批量新增
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatGfffLimitationMember(IData inParam) throws Exception
    {
        GfffGrpMemBatMgrBean bean = new GfffGrpMemBatMgrBean();

        return bean.createBatGfffLimitationMember(inParam);
    }
    
    /**
     * 流量自由充(全量统付)产品成员批量新增
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatGfffQuanLiangMember(IData inParam) throws Exception
    {
        GfffGrpMemBatMgrBean bean = new GfffGrpMemBatMgrBean();

        return bean.createBatGfffQuanLiangMember(inParam);
    }
    
    /**
     * 流量自由充(定额统付)产品成员批量新增
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatGfffDingEMember(IData inParam) throws Exception
    {
        GfffGrpMemBatMgrBean bean = new GfffGrpMemBatMgrBean();

        return bean.createBatGfffDingEMember(inParam);
    }
    
    /**
     * 流量自由充(限量统付)产品成员变更
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset changeGrpGfffLimitationMember(IData inParam) throws Exception
    {
        GfffGrpMemBatMgrBean bean = new GfffGrpMemBatMgrBean();

        return bean.changeGrpGfffLimitationMember(inParam);
    }
    
    /**
     * 流量自由充(定额统付)产品成员变更
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset changeGrpGfffDingEMember(IData inParam) throws Exception
    {
        GfffGrpMemBatMgrBean bean = new GfffGrpMemBatMgrBean();

        return bean.changeGrpGfffDingEMember(inParam);
    }
    
}