package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.checknpoutresultquery;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class CheckNpOutResultQueryBean extends CSBizBean {

	/**
	 * 功能：携出预审核查询 作者：mengqx
	 */
	public IDataset queryCheckNpOut(IData data, Pagination page)
			throws Exception {
		// String tradeEparchyCode = this.getTradeEparchyCode();//
		// BizRoute.getRouteId();
		String startDate = data.getString("START_DATE", "");
		String finishDate = data.getString("FINISH_DATE", "");
		String serialNumber = data.getString("SERIAL_NUMBER", "");
		String cityCode = data.getString("CITY_CODE", "");
		String smsResult = data.getString("IS_RESULT_NP_OUT", "");
		
		//查询号码仅提供在网号码查询  
		if(serialNumber != null && !serialNumber.isEmpty()){
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			if(IDataUtil.isEmpty(userInfo)){
				IData resultData = new DataMap();
				resultData.put("RESULT_INFO", "号码已注销或不存在");
				return IDataUtil.idToIds(resultData);//IData转换成IDataset
			}
		}
		IData params = new DataMap();
		params.put("CITY_CODE", cityCode);

		String staffId = getVisit().getStaffId();
        boolean flag = StaffPrivUtil.isFuncDataPriv(staffId, "CHEKENPOUTALL");
    	if(!flag){
    		params.put("CITY_CODE", getVisit().getCityCode());
    	}
    	if(flag&&"HNAL".equals(cityCode)){
    		params.put("CITY_CODE", "");
		}
    	if(flag&&"HNSJ".equals(cityCode)){
    		params.put("CITY_CODE", "");
    	}

		params.put("START_DATE", startDate);
		params.put("FINISH_DATE", finishDate);
		params.put("SERIAL_NUMBER", serialNumber);
		params.put("SMS_RESULT", smsResult);
		// IDataset dataSet = QueryCheckNpOutQry.queryCheckNpOut(params, page);
		return Dao.qryByCode("TL_B_CHECKNPOUT_LOG", "SEL_BY_SN", params, page);
		// return dataSet;
	}
	
	/**
     * 功能：新增   by mengqx
     */
    public int insertNonBossFeeItem(IData inparams) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TL_B_CHECKNPOUT_LOG", "INS_LOG", inparams);
    }
}
