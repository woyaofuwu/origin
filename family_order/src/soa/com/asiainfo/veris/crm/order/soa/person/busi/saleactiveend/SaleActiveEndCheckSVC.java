
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SaleActiveEndCheckSVC extends CSBizService
{

    private static final long serialVersionUID = 8989405331390912792L;

    public IData checkSaleActiveEnd(IData params) throws Exception
    {
        String serialNumber = params.getString("SERIAL_NUMBER");
        String tradeId = params.getString("TRADE_ID");
        String productId = params.getString("PRODUCT_ID");
        String packageId = params.getString("PACKAGE_ID");
        SaleActiveEndBean saleActiveEndBean = BeanManager.createBean(SaleActiveEndBean.class);
        return saleActiveEndBean.checkSaleActiveEnd(serialNumber, productId, packageId, tradeId);
    }
    
    
    public IData checkIsNeedWarm(IData param) throws Exception
    {
    	String productId=param.getString("PRODUCT_ID","");
    	String packageId=param.getString("PACKAGE_ID","");
    	
    	
    	IDataset checkResult=CommparaInfoQry.getCommParas("CSM", 
    			"4120", productId, packageId, "0898");
    	
    	IData result=new DataMap();
    	
    	if(IDataUtil.isNotEmpty(checkResult)){
    		result.put("IS_NEED_WARM", "1");
    	}else{
    		result.put("IS_NEED_WARM", "0");
    	}
    	
    	return result;
    	
    }

}
