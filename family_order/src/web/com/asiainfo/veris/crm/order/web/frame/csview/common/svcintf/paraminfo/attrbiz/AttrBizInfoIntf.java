
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.paraminfo.attrbiz;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class AttrBizInfoIntf
{

    /**
     * 查询attrbiz表的参数【ATTR_OBJ&ATTR_CODE】
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param attrCode
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrBizInfosByIdAndIdTypeAttrObjAttrCode(IBizCommon bc, String id, String idType, String attrObj, String attrCode) throws Exception
    {
        IData inparam = new DataMap();
        if (id != null)
        {
            inparam.put("ID", id);
        }
        if (idType != null)
        {
            inparam.put("ID_TYPE", idType);
        }
        if (attrObj != null)
        {
            inparam.put("ATTR_OBJ", attrObj);
        }
        if (attrCode != null)
        {
            inparam.put("ATTR_CODE", attrCode);
        }
        return CSViewCall.call(bc, "CS.AttrBizInfoQrySVC.getBizAttr", inparam);
    }

    /**
     * 查询ATTRBIZ表的参数【ATTR_OBJ&ATTR_VALUE】
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @param attrValue
     * @return
     * @throws Exception
     */
    public static IDataset qryAttrBizInfosByIdAndIdTypeAttrObjAttrValue(IBizCommon bc, String id, String idType, String attrObj, String attrValue) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ID", id);
        inparam.put("ID_TYPE", idType);
        inparam.put("ATTR_OBJ", attrObj);
        inparam.put("ATTR_VALUE", attrValue);

        return CSViewCall.call(bc, "CS.AttrBizInfoQrySVC.getBizAttrByAttrValue", inparam);
    }

    /**
     * 查询集团产品控制信息
     * 
     * @param bc
     * @param id
     * @param idType
     * @param attrObj
     * @return
     * @throws Exception
     */
    public static IDataset qryGrpProductCtrlInfoByIdAndIdTypeAndAttrObj(IBizCommon bc, String id, String idType, String attrObj) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("ID", id);
        inparam.put("ID_TYPE", idType);
        inparam.put("ATTR_OBJ", attrObj);
        return CSViewCall.call(bc, "CS.AttrBizInfoQrySVC.queryProductCtrlInfo", inparam);
    }

}
