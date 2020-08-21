package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.interboss;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class IbTradeQry
{
	private static final long serialVersionUID = 1L;
	
	public static IDataset qryIbTradeBySNAndReqTime(String iditemrange, String reqtime, Pagination page, String activitycode, String bipcode, boolean istoday) throws Exception
    {
        IData param = new DataMap();
       
        if(istoday){
        	 param.put("IDITEMRANGE", iditemrange);
             param.put("ACTIVITYCODE", activitycode);
             param.put("BIPCODE", bipcode);
        	return Dao.qryByCode("TL_B_IBTRADE", "SEL_BTRADE_BY_SNANDACANDBC", param, page, Route.CONN_CRM_CEN);
        }else{
        	param.put("IDITEMRANGE", iditemrange);
            param.put("REQTIME", reqtime);
            IDataset dataset = Dao.qryByCode("TL_BH_IBTRADE", "SEL_IBTRADE_BY_SNANDMONTH", param, page, Route.CONN_CRM_CEN);
            IDataset result = new DatasetList();
            for(int i=0; i<dataset.size(); i++){
            	IData data = dataset.getData(i);
            	String dbReqTime = data.getString("REQTIME");
            	String dbActivitycode = data.getString("ACTIVITYCODE");
            	String dbBipcode = data.getString("BIPCODE");

            	//1.满足请求时间
            	if(StringUtils.isNotEmpty(dbReqTime) && dbReqTime.contains(reqtime)){
            		//2.两种情况：一种是业务类型为空时，一种是业务类型不为空时
					boolean isMatch = (StringUtils.isEmpty(activitycode) && StringUtils.isEmpty(bipcode))
							|| (dbActivitycode.equals(activitycode) && dbBipcode.equals(bipcode));
					if(isMatch){
						result.add(data);
					}
				}

            }
        	return result;
        }
       
    }
	public static IDataset qryTradeTypeList(String Rsrv_str6, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("RSRV_STR6", Rsrv_str6);
		return Dao.qryByCode("TD_B_IBBUSI_SIGN", "SEL_TRADETYPELIST", param, page, Route.CONN_CRM_CEN);
	}
	
	public static IDataset qryTradelogbyTRANSIDO(String transido, Pagination page) throws Exception
	{
		IData param = new DataMap();
		param.put("TRANSIDO", transido);
		IDataset dataset = Dao.qryByCode("TL_B_IBTRADE_LOG", "SEL_LOGBYTRANSIDO", param, page, Route.CONN_LOG);	
		return dataset;
	}	

}
