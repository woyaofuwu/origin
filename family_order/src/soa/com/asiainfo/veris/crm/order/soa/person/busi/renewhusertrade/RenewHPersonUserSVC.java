package com.asiainfo.veris.crm.order.soa.person.busi.renewhusertrade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class RenewHPersonUserSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 校园异网号码产品信息查询
	 * @param param
	 * @return
	 * @throws Exception
	 * 
	 * @author zhaohj3
	 * @date 2018-3-6 10:50:51
	 */
    public IDataset getProductInfo(IData param) throws Exception{
		String userId = param.getString("USER_ID");
		IDataset userProductInfos = UserProductInfoQry.queryProductByUserId(userId);

		if (IDataUtil.isNotEmpty(userProductInfos)) {
			for (int i = 0; i < userProductInfos.size(); i++) {
				IData userProductInfo = userProductInfos.getData(i);
				String productId = userProductInfo.getString("PRODUCT_ID");
				
				IData productInfo = UProductInfoQry.qryProductByPK(productId);
				
				if (IDataUtil.isNotEmpty(productInfo)) {
					userProductInfo.putAll(productInfo);
				}
			}
		}
		return userProductInfos;
    }
    

	/**
	 * 和校园异网号码续费查询费用信息接口
	 * @param input
	 * @return
	 * @throws Exception
	 * @auth zhaohj3
	 * @date 2018-3-6 16:10:04
	 */
	public IDataset getTradeFee(IData input) throws Exception {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        // 0- 业务费用, 根据业务类型编码、产品标识、大客户等级标识、地市编码获取对应业务的通用费用，进入页面需要获取的费用
        String tradeFeeType = "0";
        IDataset results = ProductFeeInfoQry.getTradeFee(tradeTypeCode, tradeFeeType, BizRoute.getTradeEparchyCode());
        
        return results;
	}
}
