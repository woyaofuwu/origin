package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;


/**
 * BOSS主动下单信用购机接口
 */
public class ApplyCreditPurchasesAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
		List<SaleActiveTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        SaleActiveTradeData saleActiveTD = list.get(0);

        //信用购机配置
		/*IDataset commparaInfos = CommparaInfoQry.getCommNetInfo("CSM","3119",saleActiveTD.getProductId()) ;
		if(DataUtils.isEmpty(commparaInfos)){
			return;
		}*/
        IDataset commparaInfos =new DatasetList(); 
        IDataset commparas=CommparaInfoQry.getCommNetInfo("CSM","3119",saleActiveTD.getProductId()) ;
		if(DataUtils.isEmpty(commparas)){
			return;
		}
		for(int i=0;i<commparas.size();i++){
			IData data=commparas.getData(i);
			if(StringUtils.isNotEmpty(data.getString("PARA_CODE4"))
					&&data.getString("PARA_CODE4").equals(saleActiveTD.getPackageId())){
				commparaInfos.add(data);
				break;
			}
		}
		if(DataUtils.isEmpty(commparaInfos)){
			return;
		}
        
        SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
    	//对于可以办理信用购机的活动   1:勾选了信用购机.0:没勾选信用购机,正常购机活动
        if(!"1".equals(req.getCreditPurchases())){
        	return ;
        }
        
        
        String seq = "898"+"BIP2B191"+ SysDateMgr.getSysDateYYYYMMDDHHMMSS()+btd.getTradeId().substring(btd.getTradeId().length()-6);
        regOtherTrade(btd,seq);
        btd.getMainTradeData().setExecTime(SysDateMgr.END_DATE_FOREVER);
		/*IData param = new DataMap();
        param.put("CUS_MBL_NO", btd.getRD().getUca().getSerialNumber());
        String seq = "898"+"BIP2B191"+ SysDateMgr.getSysDateYYYYMMDDHHMMSS()+btd.getTradeId().substring(btd.getTradeId().length()-6);
        param.put("SEQ", seq);
        param.put("BUSINESS_TYPE", "1");//0：非终端类 1：终端类

        param.put("PRODUCT_ID",  btd.getRD().getUca().getProductId());
        param.put("PRODUCT_NM", UProductInfoQry.getProductNameByProductId(btd.getRD().getUca().getProductId()));
        
 
        param.put("PRODUCT_AMT", productAmt(btd)); //套餐金额,暂无法获取.存放0  
        
        param.put("PKG_MONTH", saleActiveTD.getMonths()); // 套餐合约期数
        param.put("BONUS_AMT", commparaInfos.getData(0).getString("PARA_CODE2","0.00"));//期返红包金额    
        param.put("BONUS_MONTH", commparaInfos.getData(0).getString("PARA_CODE3",saleActiveTD.getMonths()));//红包返还总期数
        
        List<SaleGoodsTradeData> saleGoodsTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);
		if (null != saleGoodsTradeDatas && saleGoodsTradeDatas.size() > 0) {
			   SaleGoodsTradeData  goodData = saleGoodsTradeDatas.get(0);
		       param.put("GOODS_TYPE_ID", goodData.getDeviceBrandCode());//商品类型编号
               param.put("GOODS_BRAND", goodData.getDeviceBrand());//商品品牌
               param.put("GOODS_NM", goodData.getDeviceModel());
               
               //购机款分转成元
               double deviceCost=0.00;
               if(StringUtils.isNotEmpty(goodData.getDeviceCost())){
            	   deviceCost=Double.parseDouble(goodData.getDeviceCost())/100;
               }
               param.put("GOODS_PRICE", String.format("%.2f", deviceCost));
               
               param.put("GOODS_CODE", goodData.getResCode());//商品唯一标识
               param.put("GOODS_DESC", goodData.getGoodsName());
               param.put("BUSINESS_TYPE", "4".equals(goodData.getResTypeCode())?"1":"0");//0：非终端类 1：终端类

		}
 
        param.put("OPR_ID", CSBizBean.getVisit().getStaffId());
        param.put("OPR_MBL_NO", CSBizBean.getVisit().getSerialNumber());
        param.put("DEP_ID", CSBizBean.getVisit().getDepartCode());
        param.put("DEP_NM", CSBizBean.getVisit().getDepartName());
        IDataset ds=IBossCall.applyCreditPurchases(param);
        if(IDataUtil.isNotEmpty(ds)){
        	IDataset responseInfos=ds.getData(0).getDataset("REG_ORD_RSP");
        	String rspCode = responseInfos.getData(0).getString("RSP_CODE");
        	String rspInfo = responseInfos.getData(0).getString("RSP_INFO");
        	if(!StringUtils.equals(rspCode, "0000")){
        		if(StringUtils.equals(rspCode, "3037")){
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "营业厅或者操作员没有权限");
            	}else{
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, rspInfo);
            	}
        	}else{
            	regOtherTrade(btd, responseInfos,seq);
                btd.getMainTradeData().setExecTime(SysDateMgr.END_DATE_FOREVER);
        	}
        }else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "主动下单信用购机错误");
        }*/
	}

	
	private void regOtherTrade(BusiTradeData btd,String seq ) throws Exception{
        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setRsrvValueCode("CREDIT_PURCHASES");
        otherTD.setRsrvValue("信用购机");
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setUserId(btd.getRD().getUca().getUserId());
        otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
        otherTD.setModifyTag("0");
        otherTD.setInstId(SeqMgr.getInstId());
        otherTD.setRsrvStr9(seq);
        otherTD.setRsrvStr15("0");//BOSS主动下单信用购机接口标识    0：初始化 1：接口调用成功  2：接口调用失败
        otherTD.setRsrvStr16("CREDIT_PURCHASES_FINISH_FLAG");//信用购机完工标识
        otherTD.setRsrvStr20(CSBizBean.getVisit().getStaffId());
        otherTD.setRsrvStr21(CSBizBean.getVisit().getSerialNumber());
        otherTD.setRsrvStr22(CSBizBean.getVisit().getDepartCode());
        otherTD.setRsrvStr23(CSBizBean.getVisit().getDepartName());
        
		btd.add(btd.getRD().getUca().getSerialNumber(), otherTD);
	}
	
	public String productAmt(BusiTradeData btd)throws Exception{
		String amt = "0";
		IData params = new DataMap();
		params.put(Route.ROUTE_EPARCHY_CODE, "0898");
		params.put("PRODUCT_ID", btd.getRD().getUca().getProductId());
	    IDataset productAmts=CSAppCall.call("SS.CancelWholeNetCreditPurchasesSVC.getProductAmt", params);
	    if(IDataUtil.isNotEmpty(productAmts)){
	    	amt = productAmts.first().getString("AMT");
	    }
		return amt;
	}
	
}
