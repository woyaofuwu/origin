
package com.asiainfo.veris.crm.iorder.soa.merch.plat;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.merch.merchShoppingCart.MerchShoppingCartBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class MerchSinglePlatSVC extends CSBizService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset tradeReg(IData param) throws Exception
	{
        IDataset selectedElements = null;
        String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
        if (param.getString("SELECTED_ELEMENTS") != null) {
            selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        }

        if(IDataUtil.isNotEmpty(selectedElements)){
          int size = selectedElements.size();
          for (int i = 0; i < size; i++) {
            IData element = selectedElements.getData(i);
            element.put("SERVICE_ID", element.getString("OFFER_CODE"));
            element.put("BIZ_STATE_CODE", "A");
            element.put("MODIFY_TAG", "0");
            element.put("OPER_CODE", PlatConstants.OPER_ORDER);
            //有需要赠送的话，需要调整参数  赠送号码变为受理号码
            if(StringUtils.isNotBlank(element.getString("GIFT_SERIAL_NUMBER"))){
            	param.put("SERIAL_NUMBER", element.getString("GIFT_SERIAL_NUMBER"));
            	element.put("GIFT_SERIAL_NUMBER", serialNumber);
            }
          }

        }
      param.put("SELECTED_ELEMENTS", selectedElements);
        IDataset result = CSAppCall.call("SS.PlatRegSVC.tradeReg", param);
        return  result;
	}

	public IData addShoppingCartConfirm(IData param) throws Exception
	{
		IData result = new DataMap();

		IData element = new DataMap(param.getString("OFFER"));
		String elementType = element.getString("OFFER_TYPE");
		String elementId = element.getString("OFFER_CODE");
		String endDate = element.getString("END_DATE");

		String nextProductStartDate = "";

		if (!"P".equals(elementType))
		{
			MerchShoppingCartBean shoppingCartBean = BeanManager.createBean(MerchShoppingCartBean.class);
			IDataset cartOffers = shoppingCartBean.getShoppingCartAllElements(param);
			if (IDataUtil.isNotEmpty(cartOffers))
			{
				for (int i = 0; i < cartOffers.size(); i++)
				{
					IData offer = cartOffers.getData(i);
					if ("P".equals(offer.getString("OFFER_TYPE")) && BofConst.MODIFY_TAG_ADD.equals(offer.getString("MODIFY_TAG")) && "1".equals(offer.getString("MAIN_TAG")))
					{
						nextProductStartDate = offer.getString("START_DATE");
						if (SysDateMgr.getDayIntervalNoAbs(nextProductStartDate, endDate) > 0)
						{
//							IData data = UpcCallIntf.qryROfferBaseByOfferCond(offer.getString("OFFER_CODE"), "P", elementId, elementType, getRouteId());
                            IData data = null;//上面的方法编译不通过，写死null
							if (IDataUtil.isEmpty(data))
							{
								if ("D".equals(elementType))
								{
									IDataset commparaFlowDiscntList = CommparaInfoQry.getCommparaInfoByCode("CSM", "9991", "DATA_PACK_HOUR_DISCNT", elementId, CSBizBean.getUserEparchyCode());
									if (IDataUtil.isNotEmpty(commparaFlowDiscntList))
									{
										CSAppException.appError("-1", "购物车中存在预约产品变更，日套餐/流量小时套餐,不能加入购物车！");
									}
								}

								result.put("IS_CONFIRM", true);
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	public IDataset getOrderAntinomyOffers(IData data) throws Exception
	{
		String offerCode = data.getString("OFFER_CODE");
		String offerType = data.getString("OFFER_TYPE");
		String sn = data.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(sn);
		IDataset result = new DatasetList();

		IDataset antinomyOffers = CommparaInfoQry.getCommparaInfoByCode("CSM", "26", offerCode, offerType, CSBizBean.getUserEparchyCode());
		if (IDataUtil.isNotEmpty(antinomyOffers))
		{
			for (int i = 0; i < antinomyOffers.size(); i++)
			{
				IData antinomyOffer = antinomyOffers.getData(i);
				String relOfferCode = antinomyOffer.getString("PARA_CODE2");
				String relOfferType = antinomyOffer.getString("PARA_CODE3", "");
				String tips = antinomyOffer.getString("PARA_CODE4", "");
				if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
				{
					List<DiscntTradeData> userDiscnts = uca.getUserDiscntByDiscntId(relOfferCode);
					if (userDiscnts.size() > 0)
					{
						result.addAll(getDelElement(userDiscnts, BofConst.ELEMENT_TYPE_CODE_DISCNT, tips));
					}
				}
				else if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_SVC))
				{
					List<SvcTradeData> userSvcs = uca.getUserSvcBySvcId(relOfferCode);
					if (userSvcs.size() > 0)
					{
						result.addAll(getDelElement(userSvcs, BofConst.ELEMENT_TYPE_CODE_SVC, tips));
					}
				}
				else if (relOfferType.equals(BofConst.ELEMENT_TYPE_CODE_PLATSVC))
				{
					List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(relOfferCode);
					if (userPlatSvcs.size() > 0)
					{
						result.addAll(getDelElement(userPlatSvcs, BofConst.ELEMENT_TYPE_CODE_PLATSVC, tips));
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder("订购" + UpcCall.qryOfferNameByOfferTypeOfferCode(offerCode, offerType) + "\n");
		if (IDataUtil.isNotEmpty(result))
		{
			for (int i = 0; i < result.size(); i++)
			{
				IData param = result.getData(i);
				sb.append(param.getString("DEL_TIPS") + "\n");
				param.remove("DEL_TIPS");
			}
			result = dealSelectedForChg(data, result, uca);
			result.getData(0).put("DEL_TIPS", sb.toString());
		}

		return result;
	}

	private IDataset dealSelectedForChg(IData data, IDataset elements, UcaData uca) throws Exception
	{
		String acceptTime = SysDateMgr.getSysTime();
		DataBusManager.getDataBus().setAcceptTime(acceptTime);
		IData input = new DataMap();
		ProductTradeData userProduct = uca.getUserMainProduct();
		if (userProduct != null)
		{
			input.put("USER_PRODUCT_ID", userProduct.getProductId());
		}
		ProductTradeData nextProduct = uca.getUserNextMainProduct();
		if (nextProduct != null)
		{
			input.put("NEXT_PRODUCT_ID", nextProduct.getProductId());
		}

		input.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		input.put("ROUTE_EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		input.put("TRADE_TYPE_CODE", "110");
		input.put("USER_ID", data.getString("USER_ID"));
		input.put("ELEMENTS", elements);
		input.put("CHECK_RULE", false);
		IDataset delElements = CSAppCall.call("CS.SelectedElementSVC.dealSelectedElementsForChg", input);
		return delElements;
	}

	private IDataset getDelElement(List<? extends ProductModuleTradeData> userEles, String type, String tips)
	{
		IDataset result = new DatasetList();
		for (ProductModuleTradeData productModuleTradeData : userEles)
		{
			IData param = new DataMap();
			param.put("PRODUCT_ID", productModuleTradeData.getProductId());
			param.put("PACKAGE_ID", productModuleTradeData.getPackageId());
			param.put("ELEMENT_ID", productModuleTradeData.getElementId());
			param.put("ELEMENT_TYPE_CODE", type);
			param.put("INST_ID", productModuleTradeData.getInstId());
			param.put("START_DATE", productModuleTradeData.getStartDate());
			param.put("END_DATE", productModuleTradeData.getEndDate());
			param.put("DEL_TIPS", tips);
			param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
			param.put("CHECK_RULE", false);

			result.add(param);
		}
		return result;
	}

}
