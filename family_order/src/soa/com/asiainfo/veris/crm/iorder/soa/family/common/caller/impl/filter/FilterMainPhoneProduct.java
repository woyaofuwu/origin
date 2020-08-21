package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IFilter;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * 
 * @author wych 处理下线优惠，处理默认勾选
 * 
 */
public class FilterMainPhoneProduct implements IFilter {
	@Override
	public IData trans(IData consData, IData transData) throws Exception {

		if (IDataUtil.isEmpty(transData) || IDataUtil.isEmpty(consData))
			return transData;

		String roleCode = consData.getString("ROLE_CODE");
		String sn = consData.getString("SERIAL_NUMBER", "");

		if (StringUtils.isBlank(roleCode) || StringUtils.isBlank(sn))
			return transData;

		IData user = UcaInfoQry.qryUserInfoBySn(sn);
		if (IDataUtil.isEmpty(user)) {
			return transData;
		}

		String userId = user.getString("USER_ID");
		String productId = getUserProductId(userId);
		if (StringUtils.isNotEmpty(productId)) {
			IDataset productSet = transData.getDataset(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			if (IDataUtil.isNotEmpty(productSet)) {
				setUpDefaultProduct(productSet, productId);
				transData.put(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productSet);
			}
		}
		return transData;
	}

	/**
	 * 设置默认选中
	 * 
	 * @param productSet
	 * @param productId
	 */
	private void setUpDefaultProduct(IDataset productSet, String productId) {
		if (IDataUtil.isNotEmpty(productSet)) {
			if (existsProductSet(productId, productSet)) {
				for (int i = 0; i < productSet.size(); i++) {
					IData product = productSet.getData(i);
					if (productId.equals(product.getString("OFFER_CODE"))) {
						product.put("SELECT_FLAG", "1");
					} else {
						if (!"2".equals(product.getString("SELECT_FLAG", ""))) {
							product.put("SELECT_FLAG", "2");
						}
					}
				}
			}
		}
	}

	/**
	 * 判断是否存在于集合中
	 * 
	 * @param productId
	 * @param productSet
	 * @return
	 */
	private boolean existsProductSet(String productId, IDataset productSet) {
		if (IDataUtil.isNotEmpty(productSet)) {
			for (int i = 0; i < productSet.size(); i++) {
				IData product = productSet.getData(i);
				if (productId.equals(product.getString("OFFER_CODE"))) {
					return true;
				}
			}
		}
		return false;
	}

	private String getUserProductId(String userId) throws Exception {
		String productId = null;
		IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
		String sysTime = SysDateMgr.getSysTime();
		if (IDataUtil.isNotEmpty(userMainProducts)) {
			int size = userMainProducts.size();
			for (int i = 0; i < size; i++) {
				IData userProduct = userMainProducts.getData(i);
				if (SysDateMgr.compareTo(userProduct.getString("START_DATE"), sysTime) < 0) {
					productId = userProduct.getString("PRODUCT_ID");

				} else {
					productId = userProduct.getString("PRODUCT_ID");
					break;
				}
			}
		}
		return productId;

	}

}
