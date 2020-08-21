package com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.upcinfo;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class UpcInfoIntf
{
    public static IDataset qryUpcInfosByServiceNameAndParamData(IBizCommon bc, String serviceName, IData paramData) throws Exception
    {
        paramData.put("REAL_SERVICE_NAME", serviceName);
        
        return CSViewCall.call(bc, "CS.UpcInfoSVC.qryUpcInfosByServiceNameAndParamData", paramData);
    }
    
    /**
     * 特征相关接口
     * 根据特牌查询品牌名称
     * @param brandCode
     * @return
     * @throws Exception
     */
    public static String queryBrandNameByChaVal(IBizCommon bc, String brandCode) throws Exception
    {
        IData input = new DataMap();
        
        input.put("BRAND_CODE",brandCode);
        IDataset result = CSViewCall.call(bc,"UPC.Out.ChaQueryFSV.queryBrandNameByChaVal", input);
        return  result.getData(0).getString("BRAND_NAME");
        
    }
}
