
package com.asiainfo.veris.crm.order.soa.group.outgroupsyn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

/**
 * 
 * 
 * @author
 */
public class ManageOutGroupSynBean extends GroupBean
{

    /**
     * 新增虚拟集团客户资料
     * @param data
     * @return
     * @throws Exception
     */
    public boolean addOutGroupInfos(IData data) throws Exception{
        return  ManageOutGroupSynQry.addOutGroupInfos(data);
    }
    
    /**
     * 查询虚拟集团客户资料
     * @param dt
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryOutGroupSynInfo(IData data, Pagination pagination) throws Exception
    {
        IDataset resultInfos = ManageOutGroupSynQry.qryOutGroupSynInfo(data, pagination);
        return resultInfos;
    }
    
    /**
     * 通过证件类型和证件号查询集团客户资料
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryCustInfoByLicenceTypeNo(IData data) throws Exception
    {
        IDataset resultInfos = ManageOutGroupSynQry.qryCustInfoByLicenceTypeNo(data);
        return resultInfos;
    }
 
    /**
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryOutGrpByLicenceTypeNoName(IData data) throws Exception
    {
        IDataset resultInfos = ManageOutGroupSynQry.qryOutGrpByLicenceTypeNoName(data);
        return resultInfos;
    }
    
    /**
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public int saveOutGrpByLicenceTypeNoName(IData data) throws Exception
    {
        return ManageOutGroupSynQry.saveOutGrpByLicenceTypeNoName(data);
    }
    
    /**
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryOutGrpByCustName(IData data) throws Exception
    {
        IDataset resultInfos = ManageOutGroupSynQry.qryOutGrpByCustName(data);
        return resultInfos;
    }
    
}
