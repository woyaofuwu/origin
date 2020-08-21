package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.npreturnvisit;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSExportTaskExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class NpReturnVisitExport extends CSExportTaskExecutor
{
    public IDataset executeExport(IData param, Pagination arg1) throws Exception
    {

        IDataset ids =  CSAppCall.call("SS.NpReturnVisitSVC.getNpOutInfos", param);
        if(IDataUtil.isNotEmpty(ids)){
            for(int i=0,len=ids.size();i<len;i++){
                IData data = ids.getData(i);
                
       
                String protInNetId = data.getString("PORT_IN_NETID");
                String brandCode =  data.getString("BRAND_CODE");
                String vipClassId = data.getString("VIP_CLASS_ID");
                String vipTypeCode = data.getString("VIP_TYPE_CODE");
                String custManagerId = data.getString("CUST_MANAGER_ID");
                String productId = data.getString("PRODUCT_ID");
                String cityCode =  data.getString("CITY_CODE");
                String str0 = protInNetId.substring(0, 3);
                String str1 = protInNetId.substring(3,4);
                
                data.put("CITY_CODE",StaticUtil.getStaticValue(getVisit(),"TD_M_AREA","AREA_CODE","AREA_NAME", cityCode));
                protInNetId = StaticUtil.getStaticValue("PORT_CARRIER", str0)+"["+StaticUtil.getStaticValue("CARRIER_NET", str1)+"]";
                data.put("PORT_IN_NETID", protInNetId);
                //data.put("BRAND_CODE", StaticUtil.getStaticValue(getVisit(),"TD_S_BRAND", "BRAND_CODE","BRAND",brandCode));
                String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);
                data.put("BRAND_CODE", brandName);
                if(StringUtils.isNotBlank(vipClassId) && StringUtils.isNotBlank(vipTypeCode)){
                  data.put("VIP_CLASS_ID",StaticUtil.getStaticValue(getVisit(),"TD_M_VIPCLASS",new java.lang.String[]{"VIP_TYPE_CODE","CLASS_ID"}, "CLASS_NAME", new java.lang.String[]{vipTypeCode,vipClassId}));
                }
                
                if(StringUtils.isNotBlank(custManagerId)){
                    data.put("CUST_MANAGER_ID",StaticUtil.getStaticValue(getVisit(),"TD_M_STAFF","STAFF_ID","STAFF_NAME", custManagerId));
                }
                
                data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                
            }
        }
        
        return ids;

    }
}
