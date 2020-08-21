package com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;

public class CreateHYTPersonUserSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @Description：校验用户是否可以办理该套餐
	 * @param:@param input
	 * @param:@return
	 * @param:@throws Exception
	 * @return IDataset
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-28下午05:11:13
	 */
	public IData checkValidDiscnt(IData input) throws Exception {
		IData result = new DataMap();
		CreateHYTPersonUserBean bean = BeanManager.createBean(CreateHYTPersonUserBean.class);
		String isOwner = input.getString("IS_SHIP_OWNER");
		IDataset dataset = CommparaInfoQry.getCommparaInfoByCode2("CSM", "313", input.getString("DISCNT_CODE"),"1","ZZZZ");
		if(IDataUtil.isNotEmpty(dataset)){
			result.put("IS_OWNER_DISCNT", "1");
		}else{
			result.put("IS_OWNER_DISCNT", "0");
			if("1".equals(isOwner)&&"1".equals(input.getString("IS_OPEN"))){
				result.put("CODE", "0004");
				result.put("MSG", "船东开户必须办理船东套餐");
				return result;
			}
		}
		
		
		if(IDataUtil.isNotEmpty(dataset)&&"0".equals(isOwner)){
			result.put("CODE", "0002");
			result.put("MSG", "船员不可以办理船东套餐");
			return result;
		}
		IDataset shipInfo = bean.queryShipInfo(input);
		
		if(IDataUtil.isNotEmpty(shipInfo)){
			if("1".equals(isOwner)&&"1".equals(result.getString("IS_OWNER_DISCNT"))){
					result.put("CODE", "0001");
					result.put("MSG", "该船只已经办理船东套餐，不可重复办理");
					return result;
			}
		}else{
			if("0".equals(isOwner)){
				result.put("CODE", "0003");
				result.put("MSG", "该船只尚未开通船东套餐");
				return result;
			}
		}
		
		
		result.put("CODE", "0000");
		result.put("MSG", "可以办理");
		return result;
      
	}
}
