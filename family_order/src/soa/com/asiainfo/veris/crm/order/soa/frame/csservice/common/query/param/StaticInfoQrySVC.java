
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.URelaRoleInfoQry;

public class StaticInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public static IDataset getRoleCodeList(IData input) throws Exception
    {
        String relationType = input.getString("RELATION_TYPE_CODE", "");

        IDataset data = URelaRoleInfoQry.qryRelaRoleInfoByRelaTypeCode(relationType);

        return data;
    }

    /**
     * 根据type_id、eparchy_code获取静态参数表数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getStaticListByTypeIdEparchy(IData input) throws Exception
    {
        String eparchyCode = getVisit().getStaffEparchyCode();
        String typeid = input.getString("TYPE_ID", "");
        Pagination page = new Pagination();
        return StaticInfoQry.getStaticListByTypeIdEparchy(eparchyCode, typeid, page);
    }

    /**
     * @Description: 根据类型和副类型查询静态信息
     * @param typeId
     * @param pdataId
     * @return
     * @throws Exception
     * @author: tangxy
     * @date: Jun 26, 2014 9:50:04 AM
     */
    public IDataset getStaticListByTypeIdPDataId(IData input) throws Exception
    {
        String typeId = input.getString("TYPE_ID");
        String pdataId = input.getString("PDATA_ID");
        return StaticInfoQry.queryStaticValuesByPdataId(pdataId, typeId);
    }

    /**
     * 根据TYPE_ID查静态数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getStaticValueByTypeId(IData input) throws Exception
    {
        String typeid = input.getString("TYPE_ID", "");

        IDataset data = StaticInfoQry.getStaticValueByTypeId(typeid);

        return data;
    }
    
    /**
     * 根据TYPE_ID,P_DATA_ID DATA_ID 查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    
    public IDataset getStaticValueByPDType(IData param) throws Exception{
    
    	return StaticInfoQry.getStaticValueByPDType(param);
    	
    }
    
    public static IDataset getStaticValueByPDTypeMEM(IData param) throws Exception{
    	return StaticInfoQry.getStaticValueByPDTypeMEM(param);
    }
    
    
    
}
