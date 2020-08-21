
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class ExtractNoActivationQry extends CSBizBean
{
    /**
     * 执行号码激活情况信息查询
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryExtractnoactivationInfo(IData inparam, Pagination pagination) throws Exception
    {
        IDataset resutls=new DatasetList(); 
        resutls = Dao.qryByCodeParser("TF_F_USER", "SEL_USER_EXTRACTNOINFO", inparam,pagination,Route.CONN_CRM_CG);
        for(int i=0,size=resutls.size();i<size;i++){
			IData data=resutls.getData(i);
			if(!data.getString("PRODUCT_ID","").equals("")){
				data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(data.getString("PRODUCT_ID","")));
			}
        }    	
    	return resutls;
    }
}
