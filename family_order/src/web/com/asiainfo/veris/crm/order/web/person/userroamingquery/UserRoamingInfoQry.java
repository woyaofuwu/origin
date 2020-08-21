package com.asiainfo.veris.crm.order.web.person.userroamingquery;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UserRoamingInfoQry extends PersonBasePage
{
	public abstract void setUserRoamingInfos(IDataset userRoamingInfos);
	
	public abstract void setUserRoamingInfo(IData userRoamingInfo);
	
	public abstract void setSerialNumber(String serialNumber);
	
	public abstract void setRowIndex(int rowIndex);
	
	public abstract void setOprTIMSI(String oprTIMSI);

	public abstract void setProvCode(String provCode);
	
	public void queryUserRoamingInfo(IRequestCycle cycle) throws Exception
	{
		String serialNumber = this.getParameter("SERIAL_NUMBER");
		
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		
		IDataset dataOut = CSViewCall.call(this, "SS.UserRoamingQuerySVC.queryUserRoamingInfo", data);
		
		IData result = dataOut.getData(0);
		if (null != result) {
			IDataset userRoamingInfoQryRsps = result.getDataset("USER_ROAMING_INFO_QRY_RSP");
			if (null != userRoamingInfoQryRsps) {
				IData userRoamingInfoQryRsp = userRoamingInfoQryRsps.getData(0);
				IDataset userRoamingInfos = userRoamingInfoQryRsp.getDataset("USER_ROAMING_INFO");
				if (null != userRoamingInfos) {
					IData userRoamingInfo = userRoamingInfos.getData(0);
					
					IDataset showRoamingInfos = new DatasetList();
					IDataset roamingInfos = userRoamingInfo.getDataset("ROAMING_INFO");
					//如果有查询为空，那么用户应该没有漫游，数据库中就插入空的
					if (null != roamingInfos && roamingInfos.size() > 0) {
						for(int i = 0 ; i<roamingInfos.size(); i++){
							IData roamingInfo = roamingInfos.getData(i);
							IDataset userRoamingCountry = roamingInfo.getDataset("USER_ROAMING_COUNTRY");
							if (null != userRoamingCountry && userRoamingCountry.size() > 0) {
								for(int j = 0 ; j<userRoamingCountry.size(); j++){
									//由于漫游信息是很多条，所以先要把一样的参数复制进来，再设置漫游信息，否则不可以批量新增
									IData showRoamingInfo = new DataMap();
									
									showRoamingInfo.put("USER_ROAMING_DATE", IDataUtil.chkParamNoStr(roamingInfo ,"USER_ROAMING_DATE"));
									showRoamingInfo.put("COUNTRY_NAME", IDataUtil.chkParamNoStr(userRoamingCountry.getData(j) ,"COUNTRY_NAME"));
									showRoamingInfos.add(showRoamingInfo);
								}
							}
						}
					}
					setUserRoamingInfos(showRoamingInfos);
					
					String MSISDN = userRoamingInfo.getString("MSISDN");
					setSerialNumber(MSISDN);
					
					String provCode = userRoamingInfo.getString("PROV_CODE");
					setProvCode(provCode); 
					
					
					String oprTIMSI = userRoamingInfo.getString("OPR_TIMSI");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmSS");
					formatter.setLenient(false);
					Date newDate= formatter.parse(oprTIMSI);
					formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
					oprTIMSI = formatter.format(newDate);
					setOprTIMSI(oprTIMSI);
				}
			}
		}
	}
}

