package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.query.param.UTradeTypeInfoQry;
import com.ailk.bizservice.query.sysorg.UAreaInfoQry;
import com.ailk.bizservice.query.sysorg.UStaffInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryChangeSvcRecondsSVC extends CSBizService{
	static transient final Logger logger = Logger.getLogger(QueryChangeSvcRecondsSVC.class);
	private static final long serialVersionUID = 5120424938777575405L;
	
	public IDataset getChangeRecondsInfo(IData input) throws Exception{
		QueryChangeSvcRecondsBean result = (QueryChangeSvcRecondsBean) BeanManager.createBean(QueryChangeSvcRecondsBean.class);
        IDataset infos = result.getRecondsInfo(input, getPagination());
        if(null!=infos && infos.size()>0){
        	setNameInfoByIds(infos);
        }
        return infos;
	}
	/**
	 * 
	 * TODO
	 * @author chenfeng9
	 * @date 2017年12月19日
	 * @param infos
	 * @return void
	 * @throws Exception 
	 */
	private void setNameInfoByIds(IDataset infos) throws Exception{
		//转换ID为名称
		for(int i=0,size=infos.size();i<size;i++){
			IData info = infos.getData(i);
			String cityCode = info.getString("CITY_CODE");
			if(StringUtils.isNotBlank(cityCode)){
				info.put("AREA_NAME", UAreaInfoQry.getAreaNameByAreaCode(cityCode));
			}
			if(StringUtils.isNotBlank(info.getString("BUSI_TYPE_CODE"))){
				info.put("BUSI_TYPE_NAME", UTradeTypeInfoQry.getTradeTypeName(info.getString("BUSI_TYPE_CODE")));
			}
			if(StringUtils.isNotBlank(info.getString("STAFF_ID"))){
				info.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(info.getString("STAFF_ID")));
			}
			if(StringUtils.isNotBlank(info.getString("CUST_STAR_LEAVE"))){
				info.put("CUST_STAR_LEAVE", exchangeStarLeave(info.getString("CUST_STAR_LEAVE")));
			}
			if(StringUtils.isNotBlank(info.getString("ASSURE_STAR_LEAVE"))){
				info.put("ASSURE_STAR_LEAVE", exchangeStarLeave(info.getString("ASSURE_STAR_LEAVE")));
			}
		}
	}
	/**
	 * 
	 * TODO
	 * @author chenfeng9
	 * @date 2017年12月19日
	 * @param infos
	 * @return void
	 * @throws Exception 
	 */
	private String exchangeStarLeave(String starLeave){
		try {
			return StaticUtil.getStaticValue("ACCT_CREDIT_CLASS", starLeave);
		} catch (Exception e) {
			return "starLeave";
		}
	}
}
