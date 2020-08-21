
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;

public class AttrBizInfoQrySVC extends CSBizService
{

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    public IDataset getBizAttr(IData idata) throws Exception
    {
        String id = idata.getString("ID");
        String idType = idata.getString("ID_TYPE");
        String attrObj = idata.getString("ATTR_OBJ");
        String attrCode = idata.getString("ATTR_CODE");

        return AttrBizInfoQry.getBizAttr(id, idType, attrObj, attrCode, null);
    }

    /**
     * 根据ID、ID_TYPE、ATTR_OBJ、ATTR_VALUE查询，用于根据BBOSS数据查询产品模型数据
     * 
     * @param param
     *            查询参数
     * @return 产品信息
     * @throws Exception
     * @author weixb3
     */
    public IDataset getBizAttrByAttrValue(IData idata) throws Exception
    {
        String id = idata.getString("ID");
        String idType = idata.getString("ID_TYPE");
        String attrObj = idata.getString("ATTR_OBJ");
        String attrValue = idata.getString("ATTR_VALUE");

        IDataset data = AttrBizInfoQry.getBizAttrByAttrValue(id, idType, attrObj, attrValue, null);
        return data;
    }

    public IDataset getBizAttrByIdTypeObjCodeEparchy(IData input) throws Exception
    {
        String id = input.getString("ID");
        String idType = input.getString("ID_TYPE");
        String attrCode = input.getString("ATTR_CODE");
        String attrObj = input.getString("ATTR_OBJ");
        String eparchyCode = input.getString("EPARCHY_CODE");
        IDataset data = AttrBizInfoQry.getBizAttrByIdTypeObjCodeEparchy(id, idType, attrObj, attrCode, eparchyCode, null);

        return data;
    }

    public IDataset queryProductCtrlInfo(IData input) throws Exception
    {
        String id = input.getString("ID");
        String attrObj = input.getString("ATTR_OBJ");
        String idType = input.getString("ID_TYPE", "P");
        BizCtrlInfo ctrlInfo = BizCtrlBean.getBizCtrlInfo(id, idType, attrObj);

        IData data = ctrlInfo.getCtrlInfo();
        return IDataUtil.idToIds(data);
    }

    public IDataset showTemplate(IData input) throws Exception
    {
        String id = input.getString("ID");
        IDataset data = AttrItemInfoQry.showTemplate(id, null);

        return data;
    }
}
