
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.attriteminfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class AttrItemInfoIntf
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
        IData inparam = new DataMap();
        inparam.put("ID", id);
        inparam.put("ID_TYPE", idType);
        inparam.put("ATTR_OBJ", attrObj);
        inparam.put("EPARCHY_CODE", eparchyCode);
        return CSViewCall.call(bc, "CS.AttrItemInfoQrySVC.getAttrItemAByIDTO", inparam);
    }
    
    /**
     * 通过ID,IDTYPE,ATTR_CODE,EPARCHY_CODE查询AttrItemB表信息
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrItemBInfoByIdAndIdTypeAttrCode(IBizCommon bc, String id, String idType, String attrCode, String eparchyCode) throws Exception
    {
    	IData param = new DataMap();
    	
    	param.put("ID", id);
    	param.put("ID_TYPE", idType);
    	param.put("ATTR_CODE", attrCode);
    	param.put("EPARCHY_CODE", eparchyCode);
    	
    	return CSViewCall.call(bc, "CS.AttrItemInfoQrySVC.qryAttrItemBByIdAndIdtypeAttrCode", param);
    }

}
