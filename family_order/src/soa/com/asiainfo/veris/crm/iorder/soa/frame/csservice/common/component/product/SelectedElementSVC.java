package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.component.product;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.product.ProductInfoNewQry;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class SelectedElementSVC extends CSBizService
{
	private static final Logger log = Logger.getLogger(SelectedElementSVC.class);

	public IDataset getGrpChildOffers(IData param) throws Exception
	{

		String userId = param.getString("USER_ID");
		String productId = param.getString("PRODUCT_ID");
		String userEparchyCode = CSBizBean.getUserEparchyCode();
		if (userId == null || "".equals(userId))
		{
			return null;
		}

		IData productInfo = UProductInfoQry.qryProductByPK(productId);
		if (IDataUtil.isEmpty(productInfo))
			return null;
		String brandCode =productInfo.getString("BRAND_CODE", "");
		IDataset insChildOffers = new DatasetList();
		IDataset userElement = new DatasetList();
		
		IDataset bbInfos = new DatasetList();
		//构造子产品信息
		if (brandCode.equals("DLBG") || brandCode.equals("BOSG") )
		{
			String relationTypeCode = UProductCompInfoQry.getRelationTypeCodeByProductId(productId);
			bbInfos = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(userId, relationTypeCode, "0");//RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(userId, relationTypeCode, null);
			if (IDataUtil.isNotEmpty(bbInfos))
			{
				for (int i = 0; i < bbInfos.size(); i++)
				{
					IDataset mainProds = UserProductInfoQry.queryMainProduct(bbInfos.getData(i).getString("USER_ID_B"));
					if(IDataUtil.isEmpty(mainProds)){
						continue;
					}

					IData mainProd = mainProds.first();
					IData insOffer = new DataMap();
					IData offer = UpcCall.queryOfferByOfferId("P", mainProd.getString("PRODUCT_ID"));
	                insOffer.put("OFFER_ID", offer.getString("OFFER_ID"));
					insOffer.put("OFFER_CODE", mainProd.getString("PRODUCT_ID"));
	                insOffer.put("OFFER_TYPE", "P");
	                insOffer.put("OFFER_INS_ID", mainProd.getString("INST_ID"));
	                insOffer.put("START_DATE", mainProd.getString("START_DATE"));
	                insOffer.put("END_DATE", mainProd.getString("END_DATE"));
	                insOffer.put("GROUP_ID", "-1");
	                insOffer.put("OFFER_NAME", offer.getString("OFFER_NAME"));
	                insOffer.put("USER_ID", mainProd.getString("USER_ID"));
	                insChildOffers.add(insOffer);
				}
			}
		}
		
		if (brandCode.equals("DLBG"))
		{
			if (IDataUtil.isNotEmpty(bbInfos))
			{
				for (int i = 0; i < bbInfos.size(); i++)
				{
					userElement = UserSvcInfoQry.getValidElementFromPackageByUserA(bbInfos.getData(i).getString("USER_ID_B", ""), userId);
					if (IDataUtil.isNotEmpty(userElement))
					{
						break;
					}
				}

				for (int i = 0; i < userElement.size(); i++)
				{
					IData map = userElement.getData(i);
					
					IData insOffer = new DataMap();
					insOffer.put("OFFER_CODE", map.getString("ELEMENT_ID"));
					// 动力100资费转换
					IData meberUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(map.getString("USER_ID"), "0");
					if (IDataUtil.isNotEmpty(meberUserInfo))
					{
						IDataset datas = AttrBizInfoQry.getBizAttrByDynamic(meberUserInfo.getString("PRODUCT_ID"), "D", "DIS", map.getString("ELEMENT_ID"), null);
						if (datas != null && datas.size() > 0)
						{
							insOffer.put("OFFER_CODE", datas.getData(0).getString("ATTR_VALUE"));
						}
					}
					IData offer = UpcCall.queryOfferByOfferId(map.getString("ELEMENT_TYPE_CODE"), insOffer.getString("OFFER_CODE"));
	                insOffer.put("OFFER_ID", offer.getString("OFFER_ID"));
	                insOffer.put("OFFER_TYPE", map.getString("ELEMENT_TYPE_CODE"));
	                insOffer.put("OFFER_INS_ID", map.getString("INST_ID"));
	                insOffer.put("START_DATE", map.getString("START_DATE"));
	                insOffer.put("END_DATE", map.getString("END_DATE"));
	                insOffer.put("GROUP_ID", map.getString("PACKAGE_ID"));
	                insOffer.put("OFFER_NAME", map.getString("ELEMENT_NAME"));
	                insOffer.put("USER_ID", userId);
	                insChildOffers.add(insOffer);
				}

			}
		}
		else
		{
			IData temp = new DataMap();
			temp.put("PRODUCT_ID", productId);
			temp.put("USER_ID", userId);
			userElement = UserSvcInfoQry.getElementFromPackageByUser(userId, productId, null);
			IDataset plusProducts = ProductInfoNewQry.getPlusProductByProdId(userEparchyCode, productId);
			if (IDataUtil.isNotEmpty(plusProducts))
			{
				for (int k = 0; k < plusProducts.size(); k++)
				{
					IDataset tempUserElement = UserSvcInfoQry.getElementFromPackageByUser(userId, plusProducts.getData(k).getString("PRODUCT_ID"), null);
					if (IDataUtil.isNotEmpty(tempUserElement))
						userElement.addAll(tempUserElement);
				}
			}

			for (int i = 0; i < userElement.size(); i++)
			{
				IData map = userElement.getData(i);
				IData insOffer = new DataMap();
				//QR-20191009-01   商品业务受理界面办理变更报错  产商品下线了怎么办，头疼，营业没有数据记录前缀
				IData offer = UpcCall.queryOfferByOfferId(map.getString("ELEMENT_TYPE_CODE"), map.getString("ELEMENT_ID"));
				if(IDataUtil.isEmpty(offer)) {
					continue;
				}

				insOffer.put("OFFER_ID", offer.getString("OFFER_ID"));
				insOffer.put("OFFER_CODE", map.getString("ELEMENT_ID"));
                insOffer.put("OFFER_TYPE", map.getString("ELEMENT_TYPE_CODE"));
                insOffer.put("OFFER_INS_ID", map.getString("INST_ID"));
                insOffer.put("START_DATE", map.getString("START_DATE"));
                insOffer.put("END_DATE", map.getString("END_DATE"));
                insOffer.put("GROUP_ID", map.getString("PACKAGE_ID"));
                insOffer.put("OFFER_NAME", map.getString("ELEMENT_NAME"));
                insOffer.put("USER_ID", userId);
                insChildOffers.add(insOffer);
			}

		}
		return insChildOffers;

	}
}
