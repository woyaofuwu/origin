package com.asiainfo.veris.crm.iorder.web.person.family.compproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;


public abstract class AccessProductLibrary extends CSBasePage
{
	public abstract void setCondition(IData condition);
	
	/*private static StringBuilder getInterFaceSQL;
	private static final Logger log=Logger.getLogger(SingleLoginESPSVC.class);
	
	static
	{
		getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
	}*/
    
    public void initial(IRequestCycle cycle) throws Exception
    {
    	
    	IData data = getData();
    	//String result = "/dh-admin/prodRepoPortal/#/212092/";
    	
    	/*log.debug("---------input params---------"+data);
		IData result =new DataMap();
		IData param1 = new DataMap();
		param1.put("PARAM_NAME", "accessProductLibraryUrl");
    	IDataset bizUrls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
    	String bizUrl = "";
    	if (bizUrls != null && bizUrls.size() > 0)
    	{
    		bizUrl = bizUrls.getData(0).getString("PARAM_VALUE", "");
    	}else{
    		data.put("resultCode", "-1");
    		data.put("resultInfo", "accessProductLibraryUrl地址在TD_S_BIZENV表未配置");
    	}
    	data.put("ESP_URL", bizUrl);*/
    	IData returnData = CSViewCall.callone(this, "SS.AccessProductLibrarySVC.getStandardProductUrl",data);
    	data.put("resultUrl",returnData.getString("resultUrl"));
    	data.put("resultCode","00" );
    	data.put("resultInfo", "Successful access.");
        setCondition(data);
    }
}
