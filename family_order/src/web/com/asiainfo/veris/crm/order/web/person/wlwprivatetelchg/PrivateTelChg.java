package com.asiainfo.veris.crm.order.web.person.wlwprivatetelchg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class PrivateTelChg extends PersonBasePage{

	public abstract void setInfos(IDataset infos);
	public abstract void setInfo(IData info);
	public abstract void setPrivateTEL(IData privateTEL);
	public abstract void setElement_id(String element_id);
	public abstract void setElement_type_code(String element_type_code);
	public abstract void setUserId(String USER_ID);
	public abstract void setInstId(String INST_ID);
	public abstract void setProduct_id(String product_id);
	public abstract void setAreainfos(IDataset infos);
	public abstract void setAreainfo(IData info);
	
	public void getElements(IRequestCycle cycle) throws Exception{
		IData param = getData();
		String number = param.getString("SERIAL_NUMBER");
		//通过SERIAL_NUMBER来搜已订购的定向语音属性信息
		IData paraminfo = new DataMap();
		paraminfo.put("SERIAL_NUMBER",number);
		IDataset params = CSViewCall.call(this, "SS.PrivateTelChgSVC.getElements", paraminfo);
		IData privatetel = new DataMap();
		IDataset privatetels = new DatasetList();
		IDataset areainfos = new DatasetList();
		String service_id = "";
		String user_id = "";
		String product_id = "";
		String package_id = "";
		if(IDataUtil.isNotEmpty(params)){
			privatetel = params.getData(0);
			String inst_id = privatetel.getString("INST_ID");
			service_id = params.getData(1).getString("SERVICE_ID");
			user_id = params.getData(1).getString("USER_ID");
			product_id = params.getData(1).getString("PRODUCT_ID")+","+params.getData(1).getString("PACKAGE_ID");
			package_id = params.getData(1).getString("PACKAGE_ID");
			IData real_info = new DataMap();
			real_info.put("USER_ID", user_id);
			real_info.put("RELA_INST_ID", inst_id);
			setUserId(user_id);
			setInstId(inst_id);
			IDataset privatetel_value = CSViewCall.call(this,"SS.PrivateTelChgSVC.getattrs",real_info);
			boolean flag1 = true;
			boolean flag2 = true;
			if(IDataUtil.isNotEmpty(privatetel_value)){
				
				for(int i = 0 ; i<privatetel_value.size();i++){
					
					String attr_code = privatetel_value.getData(i).getString("ATTR_CODE");
					String attr_value = privatetel_value.getData(i).getString("ATTR_VALUE");
					String rsrv_num2 = privatetel_value.getData(i).getString("RSRV_STR2");
					if("LockFlag".equals(attr_code)){
						flag1 = false;
						if("".equals(attr_value)){
							attr_value = "0";
						}
					}else if("userShutFlag".equals(attr_code)){
						flag2 = false;
						if("".equals(attr_value)){
							attr_value = "0";
						}
					}else if("UserFlag".equals(attr_code)){
						if("1".equals(attr_value)){
							privatetel.put("GroupType", params.getData(1).getString("GroupType",""));
						}
					}
					privatetel.put(attr_code, attr_value);
					
					if("userWhiteNum_ADD".equals(rsrv_num2)&&"userWhiteNum".equals(attr_code)){
						IData value = new DataMap();
						value.put(attr_code, attr_value);
						privatetels.add(value);
					}else if("userArea_ADD".equals(rsrv_num2)&&"userArea".equals(attr_code)){
						IData value = new DataMap();
						value.put(attr_code, attr_value);
						areainfos.add(value);
					}
				}
				
			}
			if(flag1){
				privatetel.put("LockFlag", "0");
			}
			if(flag2){
				privatetel.put("userShutFlag", "0");
			}
		}
		

		privatetel.put("USER_ID", user_id);
		privatetel.put("SERIAL_NUMBER", number);
		setElement_id(service_id);
		setElement_type_code("S");
		setProduct_id(product_id);
		setPrivateTEL(privatetel);
		setInfos(privatetels);
		setAreainfos(areainfos);
		setAjax(privatetel);
		
	}
	
	public void submit(IRequestCycle cycle) throws Exception{
		IData param = getData();
		IData inparam = new DataMap();
		inparam.put("ELEMENT_ID", param.getString("ELEMENT_ID"));
		inparam.put("ELEMENT_TYPE_CODE", "S");
		
		inparam.put("INST_ID", param.getString("INST_ID"));
		
		String user_id = param.getString("USER_ID");
		String inst_id = param.getString("INST_ID");
		
		String attrString = param.getString("ATTRS");
		IDataset attrs = new DatasetList(attrString);
		
		
		IData real_info = new DataMap();
		real_info.put("USER_ID", user_id);
		real_info.put("RELA_INST_ID", inst_id);
		IDataset privatetel_value = CSViewCall.call(this,"SS.PrivateTelChgSVC.getattrs",real_info);
		if(IDataUtil.isEmpty(privatetel_value)){
			
			if(IDataUtil.isNotEmpty(attrs)){
				for(int i = 0 ; i<attrs.size() ; i++){
					if(StringUtils.isEmpty(attrs.getData(i).getString("ATTR_VALUE"))){
						attrs.remove(i);
					}
				}
			}
		}
		String pro_pac =  param.getString("PRODUCT_ID");
		String pro = pro_pac.split(",")[0];
		String pac = pro_pac.split(",")[1];
		
		
		inparam.put("ATTR_PARAM", attrs);
		inparam.put("MODIFY_TAG", "2");
		inparam.put("PRODUCT_ID", pro);
		inparam.put("PACKAGE_ID", pac);
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		data.put("USER_ID", param.getString("USER_ID"));
		
//		data.put("IF_BOOKING", "false");
		IDataset param_set = new DatasetList();
		param_set.add(inparam);
		data.put("SELECTED_ELEMENTS", param_set);
		data.put("AUTH_SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		data.put("TRADE_TYPE_CODE", "110");
		
		data.put("SUBMIT_TYPE","submit");
		
		IDataset result = CSViewCall.call(this, "SS.ChangeProductRegSVC.tradeReg", data);
		setAjax(result.getData(0));
		
		
	}
}
