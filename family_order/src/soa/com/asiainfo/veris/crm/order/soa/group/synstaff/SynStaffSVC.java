package com.asiainfo.veris.crm.order.soa.group.synstaff;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class SynStaffSVC extends CSBizService {

	private static final long serialVersionUID = 1L;

	 private static final Logger logger = Logger.getLogger(SynStaffSVC.class);
	/**
	 * 调能开接口同步电子工号
	 * @param input
	 * @return
	 * @throws Exception
	 * @author wuhao
	 * @date 2019-10-22
	 */
	public void synStaffInfoToCHANEL(IData input) throws Exception {
	
		IData organize = new DataMap();
		IData busiparams = new DataMap();
		IData request = new DataMap();
		logger.debug("SYNCHANNEL>>>>>>" + input);
		IData object = new DataMap();
		IDataset organizes = new DatasetList(); 
		object.put("organizeName", input.getString("STAFF_NAME"));
		object.put("code", input.getString("STAFF_ID"));
		object.put("state", input.getString("DIMISSION_TAG","1"));
		object.put("createDate", input.getString("START_DATE"));
		object.put("valid_date", input.getString("START_DATE"));
		object.put("expire_date", input.getString("END_DATE"));
		object.put("updateStaffId", input.getString("UPDATE_STAFF_ID"));
		object.put("updateDepartId", input.getString("UPDATE_DEPART_ID"));
		object.put("thirdOrgType", input.getString("DEPART_KIND_CODE"));		
		//organizes.add(object);
		
		organize.put("organize",object);
		organize.put("action",input.getString("SYN_ACTION"));
		/*busiparams.put("BUSIPARAMS", organize);
		request.put("REQUEST", busiparams);*/
		logger.debug("CHANNEL_REQUEST>>>>>>"+organize);
		IData abilityResult = WideNetUtil.buildChannelData(organize);
		logger.debug("abilityResult>>>>>>" + abilityResult);
		if ("00000".equals(abilityResult.getString("resCode"))) {
            IData BJRetInfo = new DataMap(abilityResult.getString("result"));
            if (!"0".equals(BJRetInfo.getString("responsecode"))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "员工管理调用渠道中心接口失败：" + BJRetInfo.getString("X_RESULTINFO"));
            }      
	  } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "员工管理调用能力开放平台失败：" + abilityResult.getString("resMsg"));
        }
	}
}
