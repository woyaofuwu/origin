package com.asiainfo.veris.crm.order.web.group.wlwprivatetelchg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class PrivateTelGrpChg extends CSBasePage{

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
	
	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void getElements(IRequestCycle cycle) throws Exception{
		IData param = getData();
		String number = param.getString("SERIAL_NUMBER");
		//通过SERIAL_NUMBER来搜已订购的定向语音属性信息
		IData paraminfo = new DataMap();
		paraminfo.put("SERIAL_NUMBER",number);
		IDataset params = CSViewCall.call(this, "SS.PrivateTelGrpChgSVC.getElements", paraminfo);
		IData privatetel = new DataMap();
		IDataset privatetels = new DatasetList();
		IDataset areainfos = new DatasetList();
		String serviceId = "";
		String userId = "";
		String grpProductId = "";
		if(IDataUtil.isNotEmpty(params)){
			privatetel = params.getData(0);
			String instId = privatetel.getString("INST_ID");
			serviceId = privatetel.getString("SERVICE_ID");
			userId = privatetel.getString("USER_ID");
			grpProductId = privatetel.getString("PRODUCT_ID");
			setUserId(userId);
			setInstId(instId);
			
			
			IData realInfo = new DataMap();
			realInfo.put("USER_ID", userId);
			realInfo.put("RELA_INST_ID", instId);
			realInfo.put("INST_TYPE", "S");
			IDataset privateTelValues = CSViewCall.call(this,"SS.PrivateTelGrpChgSVC.getattrs",realInfo);
			
			boolean flag1 = true;
			boolean flag2 = true;
			if(IDataUtil.isNotEmpty(privateTelValues)){
				for(int i = 0 ; i<privateTelValues.size();i++){
					IData attrValues = privateTelValues.getData(i);
					if(IDataUtil.isNotEmpty(attrValues)){
						String attrCode = attrValues.getString("ATTR_CODE");
						String attrValue = attrValues.getString("ATTR_VALUE");
						if("LockFlag".equals(attrCode)){
							flag1 = false;
							if("".equals(attrValue)){
								attrValue = "0";
							}
						}else if("userShutFlag".equals(attrCode)){
							flag2 = false;
							if("".equals(attrValue)){
								attrValue = "0";
							}
						}else if("UserFlag".equals(attrCode)){
							if("1".equals(attrValue)){
								privatetel.put("GroupType", privatetel.getString("GroupType",""));
							}
						}
						privatetel.put(attrCode, attrValue);
						
						if("userWhiteNum".equals(attrCode)){
							IData value = new DataMap();
							value.put(attrCode, attrValue);
							privatetels.add(value);
						}else if("userArea".equals(attrCode)){
							IData value = new DataMap();
							value.put(attrCode, attrValue);
							areainfos.add(value);
						}
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
		
		privatetel.put("SERIAL_NUMBER", number);
		setElement_id(serviceId);
		setElement_type_code("S");
		setProduct_id(grpProductId);
		setPrivateTEL(privatetel);
		setInfos(privatetels);
		setAreainfos(areainfos);
		setAjax(privatetel);
	}
	
	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void submit(IRequestCycle cycle) throws Exception{
		IData param = getData();
		IData inparam = new DataMap();
		inparam.put("ELEMENT_ID", param.getString("ELEMENT_ID"));
		inparam.put("ELEMENT_TYPE_CODE", "S");
		inparam.put("INST_ID", param.getString("INST_ID"));
		
		String userId = param.getString("USER_ID");
		String instId = param.getString("INST_ID");
		
		IData paramInfo = new DataMap();
		paramInfo.put("USER_ID", userId);
		paramInfo.put("INST_ID", instId);
		paramInfo.put("SERVICE_ID", param.getString("ELEMENT_ID"));
		paramInfo.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset svcInfos = CSViewCall.call(this,"CS.UserSvcInfoQrySVC.queryUserSvcByUserIdAndInstId",paramInfo);
		if(IDataUtil.isEmpty(svcInfos)){
			CSViewException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户[" + userId +"]的服务!");
		}
		IData svcInfo = svcInfos.getData(0);
		if(IDataUtil.isNotEmpty(svcInfo)){
			inparam.put("START_DATE", svcInfo.getString("START_DATE",""));
			inparam.put("END_DATE", svcInfo.getString("END_DATE",""));
		}
		
		String attrString = param.getString("ATTRS");
		IDataset attrs = new DatasetList(attrString);
		IData realInfo = new DataMap();
		realInfo.put("USER_ID", userId);
		realInfo.put("RELA_INST_ID", instId);
		realInfo.put("INST_TYPE", "S");
		IDataset privatetel_value = CSViewCall.call(this,"SS.PrivateTelGrpChgSVC.getattrs",realInfo);
		if(IDataUtil.isEmpty(privatetel_value)){
			if(IDataUtil.isNotEmpty(attrs)){
				for(int i = 0 ; i<attrs.size() ; i++){
					if(StringUtils.isEmpty(attrs.getData(i).getString("ATTR_VALUE"))){
						attrs.remove(i);
					}
				}
			}
		}
		
		String grpProductId =  param.getString("PRODUCT_ID");
		inparam.put("ATTR_PARAM", attrs);
		inparam.put("MODIFY_TAG", "2");
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));//手机号码
		data.put("USER_ID", param.getString("USER_ID_A"));//集团产品的userID
		
		IDataset param_set = new DatasetList();
		param_set.add(inparam);
		data.put("ELEMENT_INFO", param_set);
		data.put("AUTH_SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
		data.put("TRADE_TYPE_CODE", "6115");
		data.put("PRODUCT_ID", grpProductId);
		
		data.put("SUBMIT_TYPE","submit");
		
		IDataset result = CSViewCall.call(this, "CS.ChangeMemElementSvc.changeMemElement", data);
		setAjax(result.getData(0));
		
	}
}
