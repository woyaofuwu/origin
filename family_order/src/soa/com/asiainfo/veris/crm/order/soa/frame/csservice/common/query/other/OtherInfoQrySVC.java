
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

public class OtherInfoQrySVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 根据用户编码查询Other表资料
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getOtherInfoByUserId(IData input) throws Exception
    {
        IDataset data = GrpInfoQry.getOutGrpInfos(input);

        return data;
    }
    /**
     * 根据trade_id查询Other表资料
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryOtherByTradeId(IData input) throws Exception
    {
        IDataset data = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_TRADEID",input,Route.CONN_CRM_CG);

        return data;
    }
    public static IDataset qryOtherProductId(String product_offering_id) throws Exception{
    	IData map=new DataMap();
    	map.put("RSRV_STR4", product_offering_id);
    	
    	IDataset data = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_PRODUCTID",map,Route.CONN_CRM_CG);

        return data;
    }
    

}
