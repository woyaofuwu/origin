package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.PackageElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class OfferRelHandler {

	public static void runn(ProductModuleTradeData pmtd, BusiTradeData btd, UcaData uca) throws Exception{
		String modifyTag = pmtd.getModifyTag();
		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
			//新增时，看是否需要新增商品关系数据
			String productId = pmtd.getProductId();//主商品ID
			String packageId = pmtd.getPackageId();//组ID
			if("-1".equals(productId)){
				return;
			}
			
			List<ProductTradeData> products = uca.getUserProduct(productId);
			if(ArrayUtil.isEmpty(products)){
				//主商品不存在，可能是营销活动
			    IData product = UProductInfoQry.qrySaleActiveProductByPK(productId);
			    if(IDataUtil.isNotEmpty(product))
			    {
			        List<SaleActiveTradeData> saleActives = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);//uca.getUserSaleActives();
			        createSaleActiveOfferComRel(saleActives.get(0), btd, pmtd, uca);
			    }
			}
			else{
				ProductTradeData product = products.get(0);
				createOfferComRel(product, btd, pmtd, uca);
			}
		}
		else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
			deleteAllOfferRel(btd, pmtd, uca);
		}
		else if(BofConst.MODIFY_TAG_INHERIT.equals(modifyTag)){
			//只有主商品有继承
			String productId = pmtd.getProductId();//主商品ID
			ProductTradeData addProduct = null;
			List<ProductTradeData> products = uca.getUserProduct(productId);//新增商品
			if(ArrayUtil.isNotEmpty(products)){
				addProduct = products.get(0);
			}
			ProductTradeData delProduct = null;
			List<ProductTradeData> mainProducts = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
			if(ArrayUtil.isNotEmpty(mainProducts)){
				for(ProductTradeData temp : mainProducts){
					if("1".equals(temp.getMainTag()) && BofConst.MODIFY_TAG_DEL.equals(temp.getModifyTag())){
						delProduct = temp;
						break;
					}
				}
			}
			
			if(delProduct != null){
				deleteOfferRel(delProduct, btd, pmtd, uca);
			}
			if(addProduct != null){
				createOfferComRel(addProduct, btd, pmtd, uca);
			}
		}
	}
	
	private static void createOfferComRel(ProductTradeData product, BusiTradeData btd, ProductModuleTradeData pmtd, UcaData uca) throws Exception{
		String offerInsId = product.getInstId();
		String productId = product.getProductId();
		IData elementCfg = ProductElementsCache.getElement(productId, pmtd.getElementId(), pmtd.getElementType());
		if(null == elementCfg || elementCfg.size() == 0)
		{
			return;
		}
		String flag = elementCfg.getString("FLAG");
		if("PM_OFFER_JOIN_REL".equals(flag)){
			//join_rel关系为弱关系，不实例化
		}
		else if("PM_OFFER_COM_REL".equals(flag) || "PM_OFFER_GROUP_REL".equals(flag)){
			//构成关系和组商品关系，实例化
			OfferRelTradeData offerRel = new OfferRelTradeData();
			offerRel.setInstId(SeqMgr.getInstId());
			offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
			offerRel.setOfferInsId(offerInsId);
			offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			offerRel.setOfferCode(productId);
			offerRel.setUserId(product.getUserId());
			offerRel.setRelOfferInsId(pmtd.getInstId());
			offerRel.setRelOfferType(pmtd.getElementType());
			offerRel.setRelOfferCode(pmtd.getElementId());
			offerRel.setRelUserId(pmtd.getUserId());
			offerRel.setGroupId(StringUtils.isNotBlank(elementCfg.getString("GROUP_ID")) ? elementCfg.getString("GROUP_ID") : "0");
			offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);//构成关系
			if(SysDateMgr.compareTo(pmtd.getStartDate(), product.getStartDate()) > 0){
				offerRel.setStartDate(pmtd.getStartDate());
			}
			else{
				offerRel.setStartDate(product.getStartDate());
			}
			if(SysDateMgr.compareTo(pmtd.getEndDate(), product.getEndDate()) > 0){
				offerRel.setEndDate(product.getEndDate());
			}
			else{
				offerRel.setEndDate(pmtd.getEndDate());
			}
			btd.add(uca.getSerialNumber(), offerRel);
		}
	}
	
	public static void deleteAllOfferRel(BusiTradeData btd, ProductModuleTradeData pmtd, UcaData uca) throws Exception{
		List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndRelOfferInsId(pmtd.getInstId());
		if(ArrayUtil.isEmpty(offerRels)){
			return;
		}
		for(OfferRelTradeData offerRel : offerRels){
			offerRel.setModifyTag(BofConst.MODIFY_TAG_DEL);
			offerRel.setEndDate(pmtd.getEndDate());
			btd.add(uca.getSerialNumber(), offerRel);
		}
	}
	
	public static void deleteOfferRel(ProductTradeData product, BusiTradeData btd, ProductModuleTradeData pmtd, UcaData uca) throws Exception{
		List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndTwoInsId(product.getInstId(), pmtd.getInstId());
		if(ArrayUtil.isEmpty(offerRels)){
			return;
		}
		for(OfferRelTradeData offerRel : offerRels){
			offerRel.setModifyTag(BofConst.MODIFY_TAG_DEL);
			offerRel.setEndDate(product.getEndDate());
			btd.add(uca.getSerialNumber(), offerRel);
		}
	}
	
	private static void createSaleActiveOfferComRel(SaleActiveTradeData saleActive, BusiTradeData btd, ProductModuleTradeData pmtd, UcaData uca) throws Exception{
	    String packageId = saleActive.getPackageId();
	    String offerCode = pmtd.getElementId();
        String offerType = pmtd.getElementType();
        
        IData elementCfg = PackageElementsCache.getElement(packageId, offerCode, offerType);
        if(IDataUtil.isEmpty(elementCfg))
        {
            return;
        }
        
        String flag = elementCfg.getString("FLAG");
        if("PM_OFFER_JOIN_REL".equals(flag)){
            //join_rel关系为弱关系，不实例化
        }
        else if("PM_OFFER_COM_REL".equals(flag) || "PM_OFFER_GROUP_REL".equals(flag)){
            //构成关系和组商品关系，实例化
            OfferRelTradeData offerRel = new OfferRelTradeData();
            offerRel.setInstId(SeqMgr.getInstId());
            offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
            offerRel.setOfferInsId(saleActive.getInstId());
            offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PACKAGE);
            offerRel.setOfferCode(packageId);
            offerRel.setUserId(btd.getRD().getUca().getUserId());
            offerRel.setRelOfferInsId(pmtd.getInstId());
            offerRel.setRelOfferType(pmtd.getElementType());
            offerRel.setRelOfferCode(pmtd.getElementId());
            offerRel.setRelUserId(pmtd.getUserId());
            offerRel.setGroupId(StringUtils.isNotBlank(elementCfg.getString("GROUP_ID")) ? elementCfg.getString("GROUP_ID") : "0");
            offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);//构成关系
            if(SysDateMgr.compareTo(pmtd.getStartDate(), saleActive.getStartDate()) > 0){
                offerRel.setStartDate(pmtd.getStartDate());
            }
            else{
                offerRel.setStartDate(saleActive.getStartDate());
            }
            if(SysDateMgr.compareTo(pmtd.getEndDate(), saleActive.getEndDate()) > 0){
                offerRel.setEndDate(saleActive.getEndDate());
            }
            else{
                offerRel.setEndDate(pmtd.getEndDate());
            }
            
            btd.add(uca.getSerialNumber(), offerRel);
        }
    }
}
