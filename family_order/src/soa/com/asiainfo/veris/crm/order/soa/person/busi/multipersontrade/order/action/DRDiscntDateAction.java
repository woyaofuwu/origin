package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.requestdata.GroupMemberData;

public class DRDiscntDateAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{	
		
		List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐 
		System.out.println("discntTrades111===="+discntTrades);
        if (discntTrades != null && discntTrades.size() > 0) {
            for (DiscntTradeData discntTrade : discntTrades) {
                if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                	IDataset memberList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", discntTrade.getUserId(), "2");
                	System.out.println("memberList111===="+memberList);
                    String prodId="";
                    List<ProductTradeData> productTrade = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
                    ProductTradeData prodTradeData = new ProductTradeData();
                    System.out.println("productTrade111===="+productTrade);
                    if(productTrade != null && productTrade.size() > 0)
                    {
                        for(ProductTradeData product : productTrade)
                        {
                            if(BofConst.MODIFY_TAG_ADD.equals(product.getModifyTag()) && "1".equals(product.getMainTag()))
                            {
                            	prodId = product.getProductId();
                            	prodTradeData = product;
                            	System.out.println(">>>>product>>>>product==="+product);
                                //新增OfferRel
                                OfferRelTradeData offerRel = new OfferRelTradeData();
                         		offerRel.setInstId(SeqMgr.getInstId());
                         		offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
                         		offerRel.setOfferInsId(prodTradeData.getInstId());
                         		offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                         		offerRel.setOfferCode("99000211");
                         		offerRel.setUserId(discntTrade.getUserId());
                         		offerRel.setRelOfferInsId(discntTrade.getInstId());
                         		offerRel.setRelOfferCode(discntTrade.getElementId());
                         		offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
                         		offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);//连带关系
                         		offerRel.setStartDate(discntTrade.getStartDate());
                         		offerRel.setEndDate(discntTrade.getEndDate());
                         		offerRel.setRelUserId(discntTrade.getUserId());
                         		offerRel.setGroupId("-1");
                         		btd.add(btd.getRD().getUca().getSerialNumber(), offerRel);
                            }
                        }
                    }else 
                    {
                    	String serialNumber = btd.getRD().getUca().getSerialNumber();
                    	if(!(serialNumber.startsWith("DR"))){
                			serialNumber="DR"+serialNumber;
                		}
                    	System.out.println("serialNumber===="+serialNumber);
                    	String drUserId = "";
                    	String drProIsntId = "";
                    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
                    	if (IDataUtil.isNotEmpty(userInfo)) {
							drUserId = userInfo.getString("USER_ID");
						}
                    	
                    	IDataset productList =RelaUUInfoQry.qryDrProductId(drUserId);
                    	if (IDataUtil.isNotEmpty(productList)) {
                    		drProIsntId = productList.getData(0).getString("INST_ID");
						}
                    	System.out.println("drProIsntId===="+drProIsntId);
                    	OfferRelTradeData offerRel = new OfferRelTradeData();
                 		offerRel.setInstId(SeqMgr.getInstId());
                 		offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
                 		offerRel.setOfferInsId(drProIsntId);
                 		offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                 		offerRel.setOfferCode("99000211");
                 		offerRel.setUserId(discntTrade.getUserId());
                 		offerRel.setRelOfferInsId(discntTrade.getInstId());
                 		offerRel.setRelOfferCode(discntTrade.getElementId());
                 		offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
                 		offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);//连带关系
                 		offerRel.setStartDate(discntTrade.getStartDate());
                 		offerRel.setEndDate(discntTrade.getEndDate());
                 		offerRel.setRelUserId(discntTrade.getUserId());
                 		offerRel.setGroupId("-1");
                 		btd.add(btd.getRD().getUca().getSerialNumber(), offerRel);
					}

                }
            }
        }
	
		
        
	}

}
