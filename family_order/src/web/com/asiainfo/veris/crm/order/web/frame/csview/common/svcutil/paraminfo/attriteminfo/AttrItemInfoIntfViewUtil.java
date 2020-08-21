
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.attriteminfo.AttrItemInfoIntf;

public class AttrItemInfoIntfViewUtil
{
    /**
     * 通过ID,IDTYPE,ATTROBJ,EPARCHYCODE查询AttrItemA表信息
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(IBizCommon bc, String id, String idType, String attrObj, String eparchyCode) throws Exception
    {
        IDataset attrItems = AttrItemInfoIntf.qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bc, id, idType, attrObj, eparchyCode);
        return attrItems;
    }

    /**
     * 通过产品ID和地州编码查询集团的产品参数配置AttrItemA信息列表
     * 
     * @param bc
     * @param grpProductId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductItemAInfosByGrpProductIdAndEparchyCode(IBizCommon bc, String grpProductId, String eparchyCode) throws Exception
    {
        IDataset attrItems = qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bc, grpProductId, "P", "0", eparchyCode);
        return attrItems;
    }

    /**
     * 通过产品ID和地州编码查询成员的产品参数配置AttrItemA信息列表
     * 
     * @param bc
     * @param grpProductId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryMebProductItemAInfosByGrpProductIdAndEparchyCode(IBizCommon bc, String grpProductId, String eparchyCode) throws Exception
    {
        IDataset attrItems = qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bc, grpProductId, "P", "1", eparchyCode);
        return attrItems;
    }
    
    /**
     * 查询TD_B_ATTR_ITEMB信息
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrItemBByIdAndIdtypeAttrCode(IBizCommon bc, String id, String idType, String attrCode, String eparchyCode) throws Exception
    {
    	return AttrItemInfoIntf.qryAttrItemBInfoByIdAndIdTypeAttrCode(bc, id, idType, attrCode, eparchyCode);
    }
}
