package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.buildrequest;


import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.intelligentnk.IntelligentNetworKingBean;
import com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.requestdata.DredgeSmartNetworkReqData;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class BuildDredgeSmartNetworkReqData extends BaseBuilder implements IBuilder{
	
	@Override
	public void buildBusiRequestData(IData param, BaseReqData brd)
			throws Exception {
		
		DredgeSmartNetworkReqData ReqData = (DredgeSmartNetworkReqData) brd;
		ReqData.setFiistType(param.getString("FIRST_TYPE"));
		ReqData.setSecondType(param.getString("SECOND_TYPE"));
		ReqData.setThirdType(param.getString("THIRD_TYPE"));
		String SPID = null;
		String BIZ_CODE = null;
		String CAMPAIGN_ID = null;
		
		IDataset commparaInfos9387 = CommparaInfoQry.getCommparaByCode4to6("CSM","9387",null,param.getString("FIRST_TYPE"),param.getString("SECOND_TYPE"),param.getString("THIRD_TYPE"),"ZZZZ");
		if(IDataUtil.isNotEmpty(commparaInfos9387)){
			for(int i=0;i<commparaInfos9387.size();i++){
				String paraCode4 = commparaInfos9387.getData(i).getString("PARA_CODE4","0");
				String paraCode5 = commparaInfos9387.getData(i).getString("PARA_CODE5","0");
				String paraCode6 = commparaInfos9387.getData(i).getString("PARA_CODE6","0");
				if(paraCode4.equals(StringUtils.isBlank(param.getString("FIRST_TYPE"))?"0":param.getString("FIRST_TYPE"))&&
				   paraCode5.equals(StringUtils.isBlank(param.getString("SECOND_TYPE"))?"0":param.getString("SECOND_TYPE"))&&
				   paraCode6.equals(StringUtils.isBlank(param.getString("THIRD_TYPE"))?"0":param.getString("THIRD_TYPE"))){
					SPID = commparaInfos9387.getData(i).getString("PARAM_CODE");
					CAMPAIGN_ID = commparaInfos9387.getData(i).getString("PARA_CODE1");
					BIZ_CODE = commparaInfos9387.getData(i).getString("PARA_CODE2");
				}
		
			}
		}
		System.out.println("坐标参数"+SPID+";"+BIZ_CODE+";"+CAMPAIGN_ID);
		
		//guozhao
		String serialNumber=param.getString("SERIAL_NUMBER");
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		data.put("OprNumb", serialNumber);//只是为了查宽带信息，但oprNumb不能为空 
		IDataset returnList = new DatasetList(IntelligentNetworKingBean.wbandInq(data).getString("wbandList"));
		IData wbandList = returnList.getData(0);
		String CITY_CODE=wbandList.getString("CITY_CODE");
		String COUNTRY_CODE=wbandList.getString("COUNTRY_CODE");
		String districtAddr=wbandList.getString("districtAddr");
	
						
		ReqData.setCust_name(param.getString("CUST_NAME",""));
		ReqData.setContact_phone(param.getString("CONTACT_PHONE",""));
		ReqData.setCity_code(param.getString("CITY_CODE",CITY_CODE));//CITY是DredgeSmartNetworkTrade里面根据CITY_CODE查的
		ReqData.setCity(wbandList.getString("CITY",""));
		ReqData.setCounty_code(param.getString("COUNTY_CODE",COUNTRY_CODE));//COUNTY是DredgeSmartNetworkTrade里面根据COUNTY_CODE查的
		ReqData.setResidential_zone(param.getString("RESIDENTIAL_ZONE",""));
		ReqData.setReserve_date(param.getString("RESERVE_DATE",""));
		ReqData.setAddress(param.getString("ADDRESS",districtAddr));
		ReqData.setArea_size(param.getString("AREA_SIZE",""));
    	ReqData.setRecommend_num(param.getString("RECOMMEND_NUM",""));
    	ReqData.setChannal(param.getString("CHANNAL",""));
    	
    	ReqData.setOprNumb(param.getString("OprNumb",""));
    	ReqData.setSPID(param.getString("SPID",SPID));
    	ReqData.setBizCode(param.getString("BIZ_CODE",BIZ_CODE));
    	ReqData.setCampaign_id(param.getString("CAMPAIGN_ID",CAMPAIGN_ID));
    	
    	if(StringUtils.isNotBlank(param.getString("BizVersion")))
    	{
    		ReqData.setBizVersion(param.getString("BizVersion"));
    	}
    	else
    	{
        	ReqData.setBizVersion("1.0.0");
    	}	
    	ReqData.setHouse_type(param.getString("HOUSE_TYPE",""));
    	ReqData.setHouse_type_code(param.getString("HOUSE_TYPE_CODE",""));
    	ReqData.setAppOrderId(param.getString("ORDER_ID",""));
    	ReqData.setBizType(param.getString("BizType",""));
    	
    	String frameNetType = "0";
    	if(StringUtils.isNotBlank(param.getString("FRAME_NET_TYPE")))
    	{
    		frameNetType = param.getString("FRAME_NET_TYPE");
    	}
    	/*else
    	{
    		IDataset commparas = CommparaInfoQry.getCommpara("CSM","719","NEW_NETFEE_PRESENT","ZZZZ");
    		if(IDataUtil.isNotEmpty(commparas))
    		{
    			for(int i = 0; i < commparas.size(); i++)
    			{
    				if(param.getString("SPID").equals(commparas.getData(i).getString("PARA_CODE7"))
    						&& param.getString("BIZ_CODE").equals(commparas.getData(i).getString("PARA_CODE8"))
    								&& param.getString("CAMPAIGN_ID").equals(commparas.getData(i).getString("PARA_CODE9")))
    				{
    					frameNetType = commparas.getData(i).getString("PARA_CODE1");
			        	break;
    				}
    			}
    		}
    	}*/
    	ReqData.setFrameNetType(frameNetType);
		
	}

	@Override
	public BaseReqData getBlankRequestDataInstance() {
		
		return new DredgeSmartNetworkReqData() ;
	}

}
