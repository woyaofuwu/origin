
package com.asiainfo.veris.crm.order.soa.person.busi.onnetsaleactiveend.order.trade;

import java.math.BigDecimal;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onnetsaleactiveend.order.requestdata.SaleActiveOnNetEndReqData;


public class SaleActiveOnNetEndTradeDataBuilder extends BaseTrade implements ITrade
{

    @SuppressWarnings("unchecked")
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        SaleActiveOnNetEndReqData SaleActiveOnNetEndReqData = (SaleActiveOnNetEndReqData) btd.getRD();
		
        createThisSaleActiveTradeData(btd);
                
        ProductModuleCreator.createProductModuleTradeData(SaleActiveOnNetEndReqData.getPmds(), btd);

        MainTradeData mainTradeData = btd.getMainTradeData();

        mainTradeData.setRemark(SaleActiveOnNetEndReqData.getRemark());
        mainTradeData.setRsrvStr1(SaleActiveOnNetEndReqData.getProductId());
        mainTradeData.setRsrvStr2(SaleActiveOnNetEndReqData.getPackageId());
        
        /*
         * 打印使用字段
         * 
         */
    	StringBuilder rsrvStr8=new StringBuilder();
        IData productInfo=UProductInfoQry.qrySaleActiveProductByPK(SaleActiveOnNetEndReqData.getProductId());//ProductInfoQry.queryAllProductInfo(SaleActiveOnNetEndReqData.getProductId());
        if(IDataUtil.isNotEmpty(productInfo)){
        	rsrvStr8.append(productInfo.getString("PRODUCT_NAME")); //终止活动名称
        }else{
        	rsrvStr8.append("");
        }
        rsrvStr8.append("#null#");
        
        
        //获取违约终止时间
        boolean isGetValue=false;
        List<SaleActiveTradeData>  SaleActiveTradeDatas=btd.get("TF_B_TRADE_SALE_ACTIVE");
        for (SaleActiveTradeData saleActiveTradeData : SaleActiveTradeDatas) {
			if(saleActiveTradeData.getModifyTag().equals(BofConst.MODIFY_TAG_DEL)
					&&saleActiveTradeData.getProductId().equals(SaleActiveOnNetEndReqData.getProductId())){
				//计算已经使用的月份
				String startDate=saleActiveTradeData.getRsrvDate1();
				//String curDate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_MS);
				String curDate=saleActiveTradeData.getRsrvDate2();
				
				int usedMonths=0;
				
				if(startDate.compareTo(curDate)<=0){
					usedMonths=SysDateMgr.monthInterval(startDate, curDate);
				}
				
				rsrvStr8.append(String.valueOf(usedMonths));  //活动正常履约月份
				rsrvStr8.append("#null#");
				rsrvStr8.append(saleActiveTradeData.getEndDate()); //违约终止时间
				rsrvStr8.append("#null#");
				
				isGetValue=true;
				
				break;
			}
		}
        if(!isGetValue){
        	rsrvStr8.append("");  //活动正常履约月份
			rsrvStr8.append("#null#");
			rsrvStr8.append(""); //违约终止时间
			rsrvStr8.append("#null#");
        }
        
        IData pageParam=btd.getRD().getPageRequestData(); 
        if(IDataUtil.isNotEmpty(pageParam)){      
        	String returnFee=pageParam.getString("RETURNFEE","0");
        	if(returnFee!=null&&!returnFee.trim().equals("")){
        		BigDecimal returnFeeD=new BigDecimal(returnFee);
        		double finalReturnFee=returnFeeD.divide(new BigDecimal(100)).doubleValue();
        		rsrvStr8.append(finalReturnFee);  //实收金额
        	}else{
        		rsrvStr8.append("");  //实收金额
        	}
        }else{
        	rsrvStr8.append("");  //实收金额
        }
        
        mainTradeData.setRsrvStr8(rsrvStr8.toString());
    }


    @SuppressWarnings("unchecked")
    private void createThisSaleActiveTradeData(BusiTradeData btd) throws Exception
    {
        SaleActiveOnNetEndReqData SaleActiveOnNetEndReqData = (SaleActiveOnNetEndReqData) btd.getRD();
        MainTradeData mainTradeData = btd.getMainTradeData();
        //UcaData uca = SaleActiveOnNetEndReqData.getUca();
        //UserSaleActiveInfoQry.queryOnNetSaleActiveByPPIDuserId(userId, productId, packageId, relationtradeId);
        //SaleActiveTradeData userSaleActiveTradeData = uca.getUserSaleActiveByRelaTradeId(SaleActiveOnNetEndReqData.getRelationTradeId());
        IDataset ds = UserSaleActiveInfoQry.queryOnNetSaleActiveByPPIDuserId(mainTradeData.getUserId(), SaleActiveOnNetEndReqData.getProductId(), SaleActiveOnNetEndReqData.getPackageId(), SaleActiveOnNetEndReqData.getRelationTradeId());
        
        if(IDataUtil.isNotEmpty(ds))
        {
        	SaleActiveTradeData userSaleActiveTradeData = new SaleActiveTradeData(ds.getData(0));
            SaleActiveTradeData saleActiveTradeData = userSaleActiveTradeData.clone();        
            String endDate = SaleActiveOnNetEndReqData.getAcceptTime();//默认终止时间为当前
            saleActiveTradeData.setRsrvDate2(endDate);
            saleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            saleActiveTradeData.setRsrvStr22(SaleActiveOnNetEndReqData.getRemark());
            saleActiveTradeData.setRsrvStr7(SaleActiveOnNetEndReqData.getIntface());        	
            saleActiveTradeData.setRsrvStr6(SaleActiveOnNetEndReqData.getReturnfee());
            saleActiveTradeData.setRsrvStr8(CSBizBean.getVisit().getCityCode());
            btd.add(btd.getRD().getUca().getSerialNumber(), saleActiveTradeData);
        }
        
    }
}

