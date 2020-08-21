package com.asiainfo.veris.crm.order.soa.person.busi.wlwprivatetelchg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class PrivateTelChgBean extends CSBizBean{

	public PrivateTelChgBean(){
		
	}
	
	public IDataset getElements(IData param)throws Exception{
		
		String number = param.getString("SERIAL_NUMBER");
		IData user = UcaInfoQry.qryUserInfoBySn(number);
		if(IDataUtil.isEmpty(user)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户资料无数据!");
		}
		
		UcaData uca = UcaDataFactory.getNormalUca(number);
		String strBrandCode = uca.getBrandCode();
		if(!"PWLW".equals(strBrandCode)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,number + "该号码不是物联网号码");
		}
		
		String user_id = user.getString("USER_ID");
		IDataset service = CommparaInfoQry.getCommNetInfo("CSM", "9014", "99011019");
		if(IDataUtil.isEmpty(service)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"业务配置出错，请管理员修复");
		}
		String service_id = service.getData(0).getString("PARAM_CODE");
		
		IDataset params = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(user_id,service_id);
		if(IDataUtil.isEmpty(params)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未订购该个人智能网语音通信服务!");
		}
		
		String userIdA = params.getData(0).getString("USER_ID_A","");
		if(!"-1".equals(userIdA))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户未订购该个人智能网语音通信服务!!");
		}
		
		IData user_info = new DataMap();
		user_info.put("USER_ID", user_id);
		user_info.put("SERVICE_ID",service_id);
		user_info.put("PRODUCT_ID",params.getData(0).getString("PRODUCT_ID"));
		user_info.put("PACKAGE_ID", params.getData(0).getString("PACKAGE_ID"));
		
		IDataset UU = RelaUUInfoQry.getRelaByUserIdBForSqlff(user_id, null);
		if(IDataUtil.isNotEmpty(UU)){
			String user_id_a = UU.getData(0).getString("USER_ID_A");
			
			IDataset infos = UserAttrInfoQry.getUserAttrByUserIda(user_id_a);
			
			if(IDataUtil.isNotEmpty(infos)){
				for(int i=0;i<infos.size();i++){
					String code = infos.getData(i).getString("ATTR_CODE");
					if("GroupType".equals(code)){
						user_info.put("GroupType", infos.getData(i).getString("ATTR_VALUE"));
						break;
					}
				}
			}
		}
		
		params.add(user_info);
		return params;
	}
	
	
	public IDataset getattrs(IData param)throws Exception{
		
		param.put("INST_TYPE", "S");
		IDataset params = UserAttrInfoQrySVC.getUserAttrByUserIdInstid(param);
		if(IDataUtil.isEmpty(params)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未订购该服务");
		}
		return params;
	}
}
