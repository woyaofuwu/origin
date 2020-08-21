package com.asiainfo.veris.crm.iorder.soa.family.common.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class FamilyTvOpenSVC extends CSBizService {

	/**
	 * @author zhangxi
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData familyTvOpenCheck(IData input) throws Exception {

		String roleType = input.getString("ROLE_TYPE");
		String serialNumber = input.getString("SERIAL_NUMBER");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = userInfo.getString("USER_ID");

		//家庭新开魔百和校验
		if(FamilyConstants.TYPE_NEW.equals(roleType)){

			//判断用户是否含有有效的平台业务
			IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");// biz_type_code=51未来电视
			if (IDataUtil.isNotEmpty(platSvcInfos)) {
				CSAppException.apperr(TradeException.CRM_TRADE_333, "用户当前存在生效的魔百和平台业务，不能再办理。");
			}
			IDataset platSvcInfostow = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "86");// biz_type_code=51IPTV
			if (IDataUtil.isNotEmpty(platSvcInfostow)) {
				CSAppException.apperr(TradeException.CRM_TRADE_333, "用户当前存在生效的魔百和平台业务，不能再办理。");
			}

			// 魔百和产品信息
			IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");
			userInfo.put("PRODUCT_INFO_SET", topSetBoxProducts);
			return userInfo;
		}
		//存量魔百和
		else if(FamilyConstants.TYPE_OLD.equals(roleType)){

			// 魔百和产品信息
			IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");

			//查询用户有效的平台服务
			IDataset platSvcInfosOtt = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");// biz_type_code=51未来电视
			IDataset platSvcInfosIptv = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "86");// biz_type_code=51IPTV

			if(IDataUtil.isEmpty(platSvcInfosOtt) && IDataUtil.isEmpty(platSvcInfosIptv)){
				CSAppException.apperr(TradeException.CRM_TRADE_333, "用户当前不存在生效的魔百和平台业务，不能添加存量魔百和。");
			}

			IDataset topSetBoxProductsValid = new DatasetList();
			for(Object obj:topSetBoxProducts){
				DataMap topSetBoxProduct = (DataMap)obj;

				if(IDataUtil.isNotEmpty(platSvcInfosOtt)){
					if("19091909".equals(topSetBoxProduct.getString("PRODUCT_ID"))){
						topSetBoxProductsValid.add(topSetBoxProduct);
					}
				}

				if(IDataUtil.isNotEmpty(platSvcInfosIptv)){
					if("84019842".equals(topSetBoxProduct.getString("PRODUCT_ID"))){
						topSetBoxProductsValid.add(topSetBoxProduct);
					}
				}
			}

			userInfo.put("OLD_PRODUCT_INFO_SET", topSetBoxProductsValid);
			return userInfo;
		}

		return userInfo;
	}

	public IDataset queryTopsetBoxInfo(IData input) throws Exception {

		IDataset resInfoList = new DatasetList();

		IDataUtil.chkParam(input, "PRODUCT_ID");

		String serialNumber = input.getString("SERIAL_NUMBER");
		String productId = input.getString("PRODUCT_ID");


		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		String userId = userInfo.getString("USER_ID");

		IDataset topsetBoxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
		if(IDataUtil.isEmpty(topsetBoxInfos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理办理家庭存量魔百和！");
        }

		IData topsetBoxInfo = new DataMap();

		for(Object obj:topsetBoxInfos){
			topsetBoxInfo = (DataMap)obj;
			if(productId.equals(topsetBoxInfo.getString("RSRV_STR1"))){

				IData resInfo = new DataMap();

				resInfo.put("SERIAL_NUMBER", serialNumber);
				resInfo.put("USER_ID", userId);

				resInfo.put("OLD_RES_ID", topsetBoxInfo.getString("IMSI"));
		        resInfo.put("OLD_RES_NO", topsetBoxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
		        resInfo.put("OLD_RES_BRAND_NAME", topsetBoxInfo.getString("RSRV_STR4").split(",")[0]);
		        resInfo.put("OLD_RES_KIND_NAME", topsetBoxInfo.getString("RSRV_STR4").split(",")[1]);
		        resInfo.put("OLD_RES_STATE_NAME", "已销售");
		        resInfo.put("OLD_RES_FEE", topsetBoxInfo.getString("RSRV_NUM5"));
		        resInfo.put("OLD_RES_SUPPLY_COOPID", topsetBoxInfo.getString("KI"));
		        resInfo.put("OLD_RES_TYPE_CODE", topsetBoxInfo.getString("RES_TYPE_CODE"));
		        resInfo.put("OLD_RES_KIND_CODE", topsetBoxInfo.getString("RES_CODE"));

		        String basePackageInfo=topsetBoxInfo.getString("RSRV_STR2");

		        if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
		        	String[] basePackages=basePackageInfo.split(",");
		        	if(basePackages!=null&&basePackages.length>0){
		        		String serviceId=basePackages[0];
		        		resInfo.put("OLD_BASEPACKAGE", serviceId);

		        		if(serviceId!=null&&!serviceId.trim().equals("")&&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null")){
		        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);

		        			if(IDataUtil.isNotEmpty(svcInfo)){
		        				resInfo.put("OLD_BASEPACKAGE_NAME", svcInfo.getString("SERVICE_NAME",""));
		        			}else{
		        				resInfo.put("OLD_BASEPACKAGE_NAME", "");
		        			}
		        		}else{
		        			resInfo.put("OLD_BASEPACKAGE_NAME", "");
		        		}
		        	}else{
		        		resInfo.put("OLD_BASEPACKAGE_NAME", "");
		        	}
		        }else{
		        	 resInfo.put("OLD_BASEPACKAGE_NAME", "");
		        }

		        resInfoList.add(resInfo);

			}

		}


		return resInfoList;

	}

	/**
	 *
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData qryPackagesByPID(IData input) throws Exception {
		IData retData = new DataMap();
		String productId = input.getString("PRODUCT_ID");

		if (StringUtils.isNotBlank(productId)) {

			IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(productId);

			retData.put("B_P", topSetBoxPlatSvcPackages.getDataset("B_P"));// 基础服务包
			retData.put("O_P", topSetBoxPlatSvcPackages.getDataset("O_P")); // 可选服务包

			input.put("TOPSET_TYPE", "1");// 必选营销包TOPSET_TYPE
			IDataset platSvcPackages = CSAppCall.call("SS.InternetTvOpenIntfSVC.queryPlatSvc", input);
			if (IDataUtil.isNotEmpty(platSvcPackages)) {
				retData.put("P_P", platSvcPackages.first().getDataset("PLATSVC_INFO_LIST"));
			}

			//魔百和调测费活动
            IDataset topSetBoxSaleActiveList2 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "3800", "TOPSETBOX");
            topSetBoxSaleActiveList2 = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), topSetBoxSaleActiveList2);

            //过滤扶贫专项包
            if(IDataUtil.isNotEmpty(topSetBoxSaleActiveList2)){

            	for(int i=0; i<topSetBoxSaleActiveList2.size(); i++){
            		DataMap temp = (DataMap) topSetBoxSaleActiveList2.get(i);
            		if("2918".equals(temp.get("PARA_CODE2"))){
            			topSetBoxSaleActiveList2.remove(i);
            		}
            	}
            }

            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST2", topSetBoxSaleActiveList2);

            //魔百和押金
            retData.put("TOP_SET_BOX_DEPOSIT", "0");

            IDataset isIPTV = CommparaInfoQry.getCommParas("CSM", "182", "IS_IPTV_TIP", productId, "0898");
            if (IDataUtil.isNotEmpty(isIPTV)){
            	String wideProductType = input.getString("WIDE_PRODUCT_TYPE", "");// 宽带类型
                String wideProductId = input.getString("WIDE_PRODUCT_ID", "");// 宽带产品档次

                if (null != wideProductType && !"".equals(wideProductType)) {
                    if (StringUtils.equals("1", wideProductType) || StringUtils.equals("6", wideProductType)) {
                        retData.put("resultIPTVCode", "-1");
                        retData.put("resultIPTVInfo", "您的宽带制式所限，目前无法办理魔百和直播电视业务，建议办理魔百和互联网电视业务！");
                        return retData;
                    }
                } else {
                    retData.put("resultIPTVCode", "-1");
                    retData.put("resultIPTVInfo", "选择魔百和IPTV业务，请先选择宽带产品类型！");
                    return retData;
                }
                if (null != wideProductId && !"".equals(wideProductId)) {

                    IDataset ftthkddc = CommparaInfoQry.getCommParas("CSM", "182", "KD_DC_50M", wideProductId, "0898");// 查询宽带FTTH档次是不是50M以下
                    if (IDataUtil.isNotEmpty(ftthkddc)) {
                        retData.put("resultIPTVCode", "-1");
                        retData.put("resultIPTVInfo", "您所办理的宽带业务网速太低，无法办理魔百和直播电视业务，请将宽带升档至50M及以上再办理！");
                        return retData;
                    }
                } else {
                    retData.put("resultIPTVCode", "-1");
                    retData.put("resultIPTVInfo", "选择魔百和IPTV业务，请先选择宽带产品！");
                    return retData;
                }
            }


		}else{
            retData.put("B_P", new DataMap());
            retData.put("O_P", new DataMap());
            retData.put("TOP_SET_BOX_DEPOSIT", "0");
        }

		return retData;
	}

}
