package com.asiainfo.veris.crm.order.web.frame.csview.group.querygroupinfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.tapestry.IRequestCycle;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;


public abstract class SingleLoginESP extends CSBasePage
{
	public abstract void setCondition(IData condition);
	
	
    
    public void initial(IRequestCycle cycle) throws Exception
    {
    	
    	IData data = getData();
    	IData condData = new  DataMap();
    	
    	try{   		
    		
	    	condData.put("KIND_ID", "BIP5B051_T5001051_0_0");	    	
	    	condData.put("APP_ID", pageutil.getStaticValue("singleLoginESP", "AppID"));
	    	String staffId= this.getVisit().getStaffId();
	    	condData.put("UCODE",staffId);
	    	condData.put("COMPANY_ID", "898");
	    	String eparchyCode= this.getVisit().getStaffEparchyCode();
	    	condData.put("EPARCHY_CODE", eparchyCode);
	    	String countyId=this.getVisit().getCityCode();
	    	condData.put("CITY_CODE", countyId);
	    	condData.put("FUNC",  "0001");
	    	condData.put("TIME_STR",new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) );
	    	//SysDateMgr.string2Date(Long.toString(SysDateMgr.currentTimeMillis()), "yyyyMMddHHmmss")
	    	IData result=CSViewCall.callone(this, "CS.SingleLoginESPSVC.qeyESPSceret", condData);
	    	if(IDataUtil.isEmpty(result))
	    	{
	        	data.put("resultCode", "x1");
	        	data.put("resultInfo", "获取令牌失败");
	        }
	    	String resultCode=result.getString("X_RESULTCODE");
	    	if("00".equals(resultCode))
	    	{
		    	data.put("resultCode","00" );
		    	data.put("resultInfo", result.getString("X_RESULTINFO"));
		    	String token=result.getString("TOKEN");
		    	String CITY_ID=result.getString("CITY_ID");
		    	String COUNTY_ID=result.getString("COUNTY_ID");
		    	String grpESPUrl=result.getString("ESP_URL");
		    	//?Token=546ee43db2794125843f5308c92d0c00&+&Ucode=0001+&CompanyID=100+&CityID=1000+&CountyID=10000
		    	String url=grpESPUrl+"?Token="+token+"&Ucode="+staffId+"&CompanyID=898&CityID="+CITY_ID+"&CountyID="+COUNTY_ID;
		    	data.put("ESP_URL",url );
	    	}
	    	else {
	    		data.put("resultCode",resultCode );
		    	data.put("resultInfo", result.getString("X_RESULTINFO"));
			}
    	}
    	catch (Exception e) {
    		data.put("resultCode","x2" );
	    	data.put("resultInfo","未知错误！");
		}   	
    	
        setCondition(data);
    }
}
