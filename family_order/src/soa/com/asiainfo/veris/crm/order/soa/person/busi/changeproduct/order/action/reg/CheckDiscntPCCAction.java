package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;



/**
 *多业务共享主卡不允许办理流量冲浪包、4G流量套餐的实现在套餐互斥中配置
 *多业务共享副卡不允许办理流量冲浪包、4G流量套餐。
 */
public class CheckDiscntPCCAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐
        String user_id = uca.getUserId();
        //判断副号来办理流量冲浪包、4G流量套餐
        IDataset ds = ShareInfoQry.queryMembera(user_id);
        if(IDataUtil.isNotEmpty(ds) && discntTrades != null && discntTrades.size() > 0)
        {
        	for(int i=0;i<ds.size();i++)
        	{
        		IData data = ds.getData(i);
        		IDataset productInfo = UserProductInfoQry.queryMainProduct(data.getString("USER_ID_B"));
        		
        		if(IDataUtil.isNotEmpty(productInfo))
        		{
        			IData commpara = new DataMap();
                    commpara.put("SUBSYS_CODE", "CSM");
                    commpara.put("PARAM_ATTR", "5544");
                    commpara.put("PARAM_CODE", "SHARE");
                    commpara.put("PARA_CODE1", productInfo.getData(0).getString("PRODUCT_ID"));
                    IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                    
                    if(IDataUtil.isNotEmpty(commparaDs))
                    {
                    	for (DiscntTradeData discntTrade : discntTrades) 
                    	{
                    		if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) 
                    		{
                    			String discntCode = discntTrade.getDiscntCode();
                    			commpara.put("PARAM_ATTR", "5545");
                                commpara.put("PARAM_CODE", "SHARE");
                                commpara.put("PARA_CODE1", discntCode);
                                IDataset dscnt = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                                if (IDataUtil.isNotEmpty(dscnt))
								{
                                    String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);

                        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "多终端共享副号不允许办理此"+discntName+"套餐！");
								}
                            }
                        }
                    }
                 }
        	}
        }
        
        //判断主号办理流量不限量套餐，副号已经存在流量冲浪包、4G流量套餐
        List<ProductTradeData> ProductTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);//获取优惠子台帐
        ds = ShareInfoQry.queryMember(user_id);
        if(IDataUtil.isNotEmpty(ds) && ProductTrades != null && ProductTrades.size() > 0)
        {
        	for (ProductTradeData ProductTrade : ProductTrades) 
        	{
        		if (BofConst.MODIFY_TAG_ADD.equals(ProductTrade.getModifyTag()) || "1".equals(ProductTrade.getMainTag()))
        		{
        			IData commpara = new DataMap();
                    commpara.put("SUBSYS_CODE", "CSM");
                    commpara.put("PARAM_ATTR", "5544");
                    commpara.put("PARAM_CODE", "SHARE");
                    commpara.put("PARA_CODE1", ProductTrade.getProductId());
                    IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
                    if(IDataUtil.isNotEmpty(commparaDs))
                    {
                    	for(int i=0;i<ds.size();i++)
                    	{
                        	IData param = new DataMap();
                            param.put("USER_ID", ds.getData(i).getString("USER_ID_B"));
                            param.put("PARAM_ATTR", "5545");
                            param.put("PARAM_CODE", "SHARE");
                        	IDataset dscnt = UserDiscntInfoQry.queryUserAllDiscntsByUserIdPCC(param);
                        	if (IDataUtil.isNotEmpty(dscnt))
                        	{
                                CSAppException.apperr(CrmCommException.CRM_COMM_103, "多终端共享副号当前有流量冲浪包、4G流量套餐等，不满足办理条件！");
                        	}
                    	}
                    }                    
        		}
        	}	
        }
        
        
    }      
}
