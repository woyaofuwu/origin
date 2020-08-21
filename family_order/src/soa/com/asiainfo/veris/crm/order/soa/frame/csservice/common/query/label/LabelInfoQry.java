
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class LabelInfoQry extends CSBizBean
{
    public static IDataset getLabelsByLabelId(String labelId) throws Exception
    {
        IData data = new DataMap();
        data.put("LABEL_ID", labelId);
        
//      IDataset result = UpcCall.qryCataLogsByTypeRootLevel("K", "PERSON", "2");
        IDataset result = UpcCall.qryChildrenCatalogsByIdLevel("4", labelId);
        for(int i=0; i<result.size(); i++)
        {
            IData info = result.getData(i);
            info.put("LABEL_ID", info.getString("CATALOG_ID"));
            info.put("LABEL_NAME", info.getString("CATALOG_NAME"));
        }
        return result;
//        return Dao.qryByCode("TD_B_LABEL", "SEL_BY_LABEL_ID", data, Route.CONN_CRM_CEN);
    }

    public static IDataset getLabelsByParentLabelId(String parentLabelId, String labelId) throws Exception
    {
        IData data = new DataMap();
        data.put("PARENT_LABEL_ID", parentLabelId);
        data.put("LABEL_ID", labelId);
        
        //TODO 产商品倒换数据有问题，暂时用老数据查询
      IData catalog = UpcCall.qryCatalogByCatalogIdAndUpCatalogId(labelId, parentLabelId);
      return IDataUtil.idToIds(catalog);
//        return Dao.qryByCode("TD_B_LABEL", "SEL_BY_PARENT_LABEL_ID", data, Route.CONN_CRM_CEN);
    }

    public static String getLogicLabelIdByElementId(String elementId) throws Exception
    {
        IData data = new DataMap();
        data.put("ELEMENT_ID", elementId);
        data.put("ELEMENT_TYPE_CODE", "P");
        data.put("LABEL_ID", SaleActiveConst.SALE_ACTIVE_LABEL_LOGIC);
        
        IDataset labelSet = UpcCall.qryCatalogsByCatalogIdCatalogLevel(elementId, SaleActiveConst.SALE_ACTIVE_LABEL_LOGIC, "4");
        if(IDataUtil.isEmpty(labelSet))
        {
            labelSet = UpcCall.qryCatalogByCatalogId(elementId);
        }
        return labelSet.getData(0).getString("UP_CATALOG_ID");
        
//      IDataset labelSet = Dao.qryByCode("TD_B_LABEL", "SEL_YXLX_BY_PID", data, Route.CONN_CRM_CEN);
//        if (IDataUtil.isEmpty(labelSet))
//        {
//            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据传入的产品ID未找到活动类型信息！PRODUCT_ID=" + elementId);
//        }
//        return labelSet.getData(0).getString("LABEL_ID");
    }
    
    public static IDataset queryProductLabelId(String productId)throws Exception{
        return UpcCall.qryCatalogByCatalogId(productId);
//        return Dao.qryByCode("TD_B_LABEL", "QRY_PRODUCT_LABEL_ID", data, Route.CONN_CRM_CEN);
    }
}
