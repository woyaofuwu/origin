
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.trade;
 
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidenetchangeprod.order.requestdata.NoPhoneWideRenewRequestData;
 
public class NoPhoneWideRenewTrade extends ChangeProductTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
    	NoPhoneWideRenewRequestData rq = (NoPhoneWideRenewRequestData) btd.getRD();
        
        btd.getMainTradeData().setSubscribeType("300");
        
        btd.getMainTradeData().setRsrvStr2(rq.getProductId());
        btd.getMainTradeData().setRsrvStr3(rq.getPackageId());
        btd.getMainTradeData().setRsrvStr4(rq.getDiscntId());
        btd.getMainTradeData().setRsrvStr5(rq.getStartDate());
        btd.getMainTradeData().setRsrvStr6(rq.getendDate());
        btd.getMainTradeData().setRsrvStr7(rq.getWideYearFee());
        btd.getMainTradeData().setRsrvStr8(rq.getWideYearFee()); 
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	 super.createBusiTradeData(btd);
    	NoPhoneWideRenewRequestData rq=(NoPhoneWideRenewRequestData)btd.getRD();
    	String productId= rq.getProductId();
    	String packageId= rq.getPackageId();
    	String discntId= rq.getDiscntId();
    	String startDate =rq.getStartDate();
    	String endDate=rq.getendDate();
    	String stopOpenTag=rq.getStopOpenTag();
    	createDiscntTrade(productId,packageId,discntId,startDate,endDate,stopOpenTag,btd);
        appendTradeMainData(btd);
        
    }
    
    public void createDiscntTrade(String productId,String packageId,String discntId ,String startDate,String endDate, String stopOpenTag, BusiTradeData btd) throws Exception
	{
		 
		DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(btd.getRD().getUca().getUserId());
        newDiscnt.setUserIdA("-1");
        newDiscnt.setProductId(productId);
        newDiscnt.setPackageId(packageId);
        newDiscnt.setElementId(discntId);
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("0");
        newDiscnt.setStartDate(startDate);
        newDiscnt.setEndDate(endDate);
        newDiscnt.setRemark("无手机宽带产品续费");
        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
         
        if(stopOpenTag!=null && "1".equals(stopOpenTag)){
	        DiscntTradeData newDiscnt2 = new DiscntTradeData();
	        newDiscnt2.setUserId(btd.getRD().getUca().getUserId());
	        newDiscnt2.setUserIdA("-1");
	        newDiscnt2.setProductId("-1");
	        newDiscnt2.setPackageId("-1");
	        //System.out.println("===============NoPhoneWideRenewTrade================discntId:"+discntId);
	        if("84014240".equals(discntId)){
	        	newDiscnt2.setElementId("84014640");
	        }
	        else if("84014241".equals(discntId)){
	        	newDiscnt2.setElementId("84014641");
	        }
	        else if("84014242".equals(discntId)){
	        	newDiscnt2.setElementId("84014642");
	        }
	        //BUS201907300031新增度假宽带季度半年套餐开发需求
	        else if("84071448".equals(discntId) || "84071449".equals(discntId) || "84074442".equals(discntId)){
	        	newDiscnt2.setElementId("84071447");
	        }
	        //BUS201907300031新增度假宽带季度半年套餐开发需求
	        else{        	
	        	newDiscnt2.setElementId("20170313");
	        } 
	        //System.out.println("===============NoPhoneWideRenewTrade================newDiscnt2:"+newDiscnt2);
	        newDiscnt2.setInstId(SeqMgr.getInstId());
	        newDiscnt2.setModifyTag(BofConst.MODIFY_TAG_ADD);
	        newDiscnt2.setSpecTag("0");
	        String sysdate=SysDateMgr.getSysTime();
	        String endate=SysDateMgr.getDateLastMonthSec(sysdate);//月底
	        newDiscnt2.setStartDate(sysdate);
	        newDiscnt2.setEndDate(endate);
	        newDiscnt2.setRemark("无手机宽带产品续费");
	        btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt2);
        }
	}
}
