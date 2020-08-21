package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.gponwidenetorderdestory.order.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class OrderDestroyGponTrade extends BaseTrade implements ITrade
{
    /**
     * 实现父类抽象方法
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	String serialNumber = btd.getRD().getUca().getSerialNumber();
    	
    	String IMSTag = btd.getRD().getPageRequestData().getString("IMS_TAG","");
    	btd.getMainTradeData().setRsrvStr4(IMSTag);
    	
    	//add by kangyt begin
    	//获取预约时间
    	String ordertime = btd.getRD().getPageRequestData().getString("DESTORYTIME","");
    	String ordertime2 = "";
    	String widetype = "";
    	if(!"".equals(ordertime))
    	{
    		ordertime2 =SysDateMgr.getLastSecond(ordertime);//优惠截止时间
        	btd.getMainTradeData().setRsrvStr10(ordertime);//预约拆机日期记录在台账信息中
        	//获取宽带类型
        	String userId = btd.getRD().getUca().getUserId();
            IDataset dataset = WidenetInfoQry.getUserWidenetInfo(userId);
            widetype = dataset.getData(0).getString("RSRV_STR2");
            btd.getMainTradeData().setRsrvStr9(widetype);
            btd.getMainTradeData().setRsrvStr6("0");
            
            /**
             *  REQ201609280002 宽带功能优化 chenxy3 2016-11-29 
             * 记录销户原因
             * */
            String reasonCode= btd.getRD().getPageRequestData().getString("DESTORYREASON","");
            String reasonElse=btd.getRD().getPageRequestData().getString("REASONELSE","");
            btd.getMainTradeData().setRsrvStr5(reasonCode+"|"+reasonElse); 
            
            //如果是FTTH宽带，需要记录光猫串号，和是否退订标志
            if ("3".equals(widetype) || "5".equals(widetype))
            {
            	String modemode = btd.getRD().getPageRequestData().getString("MODEM_MODE","");
            	String modereturn = btd.getRD().getPageRequestData().getString("MODEM_RETUAN","");
            	String modefee = btd.getRD().getPageRequestData().getString("MODEM_FEE","");
            	
            	btd.getMainTradeData().setRsrvStr8(modemode);
            	btd.getMainTradeData().setRsrvStr7(modefee);
            	btd.getMainTradeData().setRsrvStr6(modereturn);
            }
    	}
        //add by kangyt end
        
        List<DiscntTradeData> discntList = btd.getRD().getUca().getUserDiscnts();
        if (discntList.size() > 0)
        {
            for (int i = 0, size = discntList.size(); i < size; i++)
            {
            	DiscntTradeData disctnData = new DiscntTradeData(discntList.get(i).toData());
            	if(!"".equals(ordertime2))
            	{
            		disctnData.setRsrvStr1(disctnData.getEndDate());//记录原优惠end_date
                    disctnData.setRsrvStr2(widetype);//记录宽带类型
                    disctnData.setEndDate(ordertime2);//截止到月底，如果预约的是7月1日，则截止到6月30日
                    disctnData.setRemark("宽带预约拆机，截止优惠到预约月份的月底");
            	}
            	else
            	{
            		disctnData.setRsrvStr1(disctnData.getEndDate());//记录原优惠end_date
            		disctnData.setEndDate(SysDateMgr.getLastDateThisMonth());//截止到月底
                    disctnData.setRemark("GPON宽带预约拆机，截止优惠到月底");
            	}
                disctnData.setModifyTag(BofConst.MODIFY_TAG_UPD);

                btd.add(serialNumber, disctnData);
            }
        }
    }
}
