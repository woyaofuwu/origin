
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AttrItemInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset CanInserUserName(IData input) throws Exception
    {
        String id = input.getString("ID");
        IDataset data = AttrItemInfoQry.CanInserUserName(id, null);

        return data;
    }

    public IDataset CanSameAcct(IData input) throws Exception
    {
        String id = input.getString("ID");
        IDataset data = AttrItemInfoQry.CanSameAcct(id, null);

        return data;
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ查询属性信息
     * 
     * @param param
     *            查询参数
     * @return 属性信息
     * @throws Exception
     */
    public IDataset getAttrItemAByIDTO(IData input) throws Exception
    {
        String id = input.getString("ID");
        String idType = input.getString("ID_TYPE");
        String attrObj = input.getString("ATTR_OBJ");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset output = AttrItemInfoQry.getAttrItemAByIDTO(id, idType, attrObj, eparchyCode, getPagination());
        return output;
    }

    /**
     * 查询itemb表的参数翻译信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAttrItemBInfoByFieldCode(IData input) throws Exception
    {
        String id = input.getString("ID");
        String idType = input.getString("ID_TYPE");
        String attrCode = input.getString("ATTR_CODE");
        String attrFieldCode = input.getString("ATTR_FIELD_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset output = AttrItemInfoQry.getAttrItemBInfoByFieldCode(id, idType, attrCode, attrFieldCode, eparchyCode);
        return output;
    }

    /**
     * 根据产品ID 元素类型 参数显示类型 查询itema表元素配置情况
     * 
     * @param param
     *            查询参数
     * @author weixb3
     * @return 属性信息
     * @throws Exception
     */
    public IDataset getelementItemaByProductId(IData input) throws Exception
    {
        String elementTypeCode = input.getString("ELEMENT_TYPE_CODE");
        String attrTypeCode = input.getString("ATTR_TYPE_CODE");
        String productId = input.getString("PRODUCT_ID");
        IDataset output = AttrItemInfoQry.getelementItemaByProductId(elementTypeCode, attrTypeCode, productId, getPagination());
        return output;
    }

    /**
     * 根据RATEPLANID，查ICB参数
     * 
     * @author weixb3 2013-04-17
     * @param input
     *            查询参数
     * @return
     * @throws Exception
     */
    public IDataset getIcbsByRatePlan(IData input) throws Exception
    {
        String rateplanid = input.getString("RATEPLANID");
        IDataset output = AttrItemInfoQry.getIcbsByRatePlan(rateplanid);
        return output;
    }

    /**
     * 根据产品ID 元素类型 参数显示类型 查询itemb表元素配置情况
     * 
     * @author liaolc 2013-04-02 11:28
     * @param pd
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     */
    public IDataset getItembByIdAndType(IData input) throws Exception
    {
        String id = input.getString("ID");
        String idType = input.getString("ID_TYPE");
        String attrCode = input.getString("ATTR_CODE");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset output = AttrItemInfoQry.getItembByIdAndType(id, idType, attrCode, eparchyCode);
        return output;
    }
    
    /**
     * 查询TD_B_ATTR_ITEMB表信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAttrItemBByIdAndIdtypeAttrCode(IData input) throws Exception
    {
    	String id = input.getString("ID");
    	String idType = input.getString("ID_TYPE");
    	String attrCode = input.getString("ATTR_CODE");
    	String eparchyCode = input.getString("EPARCHY_CODE");
    	
    	return AttrItemInfoQry.qryAttrItemBByIdAndIdtypeAttrCode(id, idType, attrCode, eparchyCode);
    }

}
