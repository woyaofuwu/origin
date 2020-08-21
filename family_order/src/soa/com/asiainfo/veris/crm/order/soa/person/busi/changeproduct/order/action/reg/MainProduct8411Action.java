package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class MainProduct8411Action implements ITradeAction 
{
	// 准备优惠台帐数据 
    private void dealForDiscnt(BusiTradeData btd, UcaData uca, IData param, ProductTradeData prodTradeData) throws Exception
    {
    	
        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(param.getString("USER_ID"));
        newDiscnt.setProductId(param.getString("PRODUCT_ID"));
        newDiscnt.setPackageId(param.getString("PACKAGE_ID"));
        newDiscnt.setElementId(param.getString("DISCNT_CODE"));
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        newDiscnt.setStartDate(param.getString("START_DATE"));
        newDiscnt.setEndDate(param.getString("END_DATE"));
        newDiscnt.setRemark("根据业务类型主产品后台绑定优惠");
        btd.add(uca.getSerialNumber(), newDiscnt);
        
        //此处prodId与packId为-1，需要手动新增OfferRel
        OfferRelTradeData offerRel = new OfferRelTradeData();
		offerRel.setInstId(SeqMgr.getInstId());
		offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
		offerRel.setOfferInsId(prodTradeData.getInstId());
		offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_PRODUCT);
		offerRel.setOfferCode(prodTradeData.getProductId());
		offerRel.setUserId(prodTradeData.getUserId());
		offerRel.setRelOfferInsId(newDiscnt.getInstId());
		offerRel.setRelOfferCode(newDiscnt.getElementId());
		offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
		offerRel.setRelType(BofConst.OFFER_REL_TYPE_LINK);//连带关系
		offerRel.setStartDate(newDiscnt.getStartDate());
		offerRel.setEndDate(newDiscnt.getEndDate());
		offerRel.setRelUserId(newDiscnt.getUserId());
		offerRel.setGroupId("-1");
		//btd.add(uca.getSerialNumber(), offerRel);BUG20180208171355
    }
	
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		UcaData uca = btd.getRD().getUca();
		List<ProductTradeData> lsProductTrade = btd.get("TF_B_TRADE_PRODUCT");
		if(CollectionUtils.isEmpty(lsProductTrade))
		{
			return;
		}

		String UserId = uca.getUserId();
		for (int i = 0; i < lsProductTrade.size(); i++) 
		{
			ProductTradeData ptProduct = lsProductTrade.get(i);
			//String strOldProductId = ptProduct.getOldProductId();
			String strProductId = ptProduct.getProductId();
			String strModifyTag = ptProduct.getModifyTag();
			String strMainTag = ptProduct.getMainTag();
			
			if(BofConst.MODIFY_TAG_ADD.equals(strModifyTag) && "1".equals(strMainTag))
			{
				IDataset commparaInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "9227", strProductId, btd.getRD().getUca().getUserEparchyCode());
				if(IDataUtil.isNotEmpty(commparaInfos))
		    	{
					IData idCommparaPara8411 = commparaInfos.first();
					String strIsChange = idCommparaPara8411.getString("PARA_CODE10", "");
					if("1".equals(strIsChange))
					{
						String discntCode = idCommparaPara8411.getString("PARA_CODE1");//para_code1=后台绑定优惠
			        	String continuous = idCommparaPara8411.getString("PARA_CODE2", "");//para_code2=绑定期限(数字代表几个月，null则到2050）
			        	String effTime = idCommparaPara8411.getString("PARA_CODE3");//para_code3=0-立即生效 1-次月生效
			        	
			        	String discntNew = "";//本次新办的该种优惠 
			        	
			        	boolean flag=true;//允许办理条件
			        	
			        	//2、本次办理的优惠如果存在该优惠，则不再绑定。
			        	List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
			        	for (DiscntTradeData discntTradeData : discntTradeDatas)
			            {
			                // 判断R类型优惠 拼入attr
			                if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
			                {
			                    discntNew = discntTradeData.getElementId();
			                    if(discntNew.equals(discntCode))
			                    {
			                    	flag=false;
			                    	break;
			                    }
			                }
			            }
			        	//3、要求该用户原来没有有效的该优惠
			        	if(flag)
			        	{
				        	IDataset userDiscs=UserDiscntInfoQry.getAllDiscntByUser(UserId,discntCode);
				        	if(userDiscs!=null && userDiscs.size()>0)
				        	{
				        		flag=false;
				        	}
			        	}
			        	
			        	if(flag)
			        	{
				        	String startDate="";
				        	String endData="";
				        	if("0".equals(effTime))
				        	{
				        		startDate=SysDateMgr.getSysTime(); //0-立即生效 1-次月生效
				        		if(!"".equals(continuous) && !"null".equals(continuous))
				        		{
				            		endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous));//绑定期限(数字代表几个月，null则到2050）
				            	}
				        		else
				            	{
				            		endData= SysDateMgr.END_DATE_FOREVER;
				            	}
				        	}
				        	else
				        	{
				        		startDate=SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysDate());//  1-次月生效
				        		if(!"".equals(continuous) && !"null".equals(continuous))
				        		{
				            		endData= SysDateMgr.getAddMonthsLastDay(Integer.parseInt(continuous)+1);//绑定期限(数字代表几个月，null则到2050）
				            	}
				        		else
				            	{
				            		endData= SysDateMgr.END_DATE_FOREVER;
				            	}
				        	}
				        	IData discntData=new DataMap();
				            discntData.put("USER_ID", UserId);
				            discntData.put("PRODUCT_ID", "-1");
				            discntData.put("PACKAGE_ID", "-1");
				            discntData.put("DISCNT_CODE", discntCode);
				            discntData.put("START_DATE", startDate);
				            discntData.put("END_DATE", endData);
				            dealForDiscnt(btd, uca, discntData, ptProduct); 
			        	}
					}
		    	} 
			}
		}
	}
}
