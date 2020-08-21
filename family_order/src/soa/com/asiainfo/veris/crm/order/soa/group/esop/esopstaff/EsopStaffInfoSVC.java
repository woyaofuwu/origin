package com.asiainfo.veris.crm.order.soa.group.esop.esopstaff;

import com.ailk.biz.bean.security.SecurityDAO;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dao.DAOManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class EsopStaffInfoSVC extends CSBizService {
	private static final long serialVersionUID = 1L;
	
	public IData qryStaffInfo(IData param) throws Exception {
		IData result = new DataMap();
		result.put("VALID","false");
		result.put("subStaffIds", "");
		result.put("jobIds", "");	
		String loginLogId = param.getString("LOGIN_LOG_ID","");
		if("".equals(loginLogId)){
			result.put("X_RESULTCODE","-1");
			result.put("X_RESULTINFO","输入参数[LOGIN_LOG_ID]不存在！");
			return result;
		}
		String sStaffId =  param.getString("STAFF_ID","");//员工号
		if("".equals(sStaffId)){
			result.put("X_RESULTCODE","-1");
			result.put("X_RESULTINFO","输入参数[STAFF_ID]不存在！");
			return result;
		}
		String lvc =  param.getString("LOG_VERIFY_CODE","");//员工号
		if("".equals(lvc)){
			result.put("X_RESULTCODE","-1");
			result.put("X_RESULTINFO","输入参数[LOG_VERIFY_CODE]不存在！");
			return result;
		}

		SecurityDAO dao = (SecurityDAO) DAOManager.createDAO(SecurityDAO.class, "sys");
		IData staffinfo = dao.queryStaff(sStaffId, null);
		
		if(staffinfo==null){
			result.put("X_RESULTCODE","-1");
			result.put("X_RESULTINFO","员工信息不存在！");
			return result;
		}
		
		result.put("staffId", staffinfo.get("STAFF_ID"));
		result.put("staffName", staffinfo.get("STAFF_NAME"));
		result.put("cityCode", staffinfo.get("AREA_CODE"));
		
		IData info = new DataMap();
		info.put("STAFF_ID", sStaffId);
		info.put("LOGIN_LOG_ID", loginLogId);
		info.put("RID", lvc);
		boolean canPass= EsopStaffInfoBean.qryStaffOtherInfos(param,result);	
		/*if(canPass){			
			IDataset rightinfos = secbean.getPrivs(pd, "1", sStaffId, "", "1234");
			if(rightinfos.size()>0){
				result.put("X_RESULTINFO","员工无查看此菜单权限！");
				for(int i=0;i<rightinfos.size();i++){
					IData info = rightinfos.getData(i);
					if(info.getString("RIGHT_CODE","").equals(rightCode)&& info.getString("MOD_NAME","").indexOf(funcId)!=-1){
						result.put("X_RESULTINFO","OK");
						result.put("HASRIGHT", "true");
						break;
					}					
				}
			}
		}*/
		return result;
	}

    public IDataset queryStaffLoginInfos(IData param) throws Exception {
        return EsopStaffInfoDAO.qryStaffLoginInfos(param, getPagination());
    }
}
