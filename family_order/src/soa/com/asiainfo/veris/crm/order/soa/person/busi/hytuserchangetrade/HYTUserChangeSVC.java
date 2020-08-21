package com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade;



import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class HYTUserChangeSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 * @Description：校验用户是否为海洋通用户
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IData
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-6-4下午04:18:31
	 */
	public IData checkIsHYTUser(IData input) throws Exception {
		IData result = new DataMap();
		HYTUserChangeBean bean = BeanManager.createBean(HYTUserChangeBean.class);
		IDataset res = bean.checkIsHYTUser(input.getString("USER_ID"));
		if(IDataUtil.isNotEmpty(res)){
			result.put("CODE", "0000");
			result.put("MSG", "海洋通用户");
			result.put("IS_SHIP_OWNER", res.first().getString("RSRV_STR2"));
			result.put("SHIP_ID",res.first().getString("RSRV_STR1"));
			IDataset discntList = bean.getDiscnt(input.getString("USER_ID"));
			result.put("DISCNT_LIST",discntList);
		}else{
			result.put("CODE", "0001");
			result.put("MSG", "非海洋通用户");
		}
		return result;
	}
	
}
