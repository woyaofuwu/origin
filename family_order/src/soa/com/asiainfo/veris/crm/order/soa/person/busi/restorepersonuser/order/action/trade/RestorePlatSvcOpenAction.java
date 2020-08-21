
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.trade;


import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend.PlatSvcTrade;

public class RestorePlatSvcOpenAction implements ITradeAction
{
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	System.out.println("复机RestorePlatSvcOpenAction"+btd.getRD().getPageRequestData());
        UcaData uca = btd.getRD().getUca();

        if ("PWLW".equals(uca.getBrandCode()))
        {
            return;
        }
        
        if ("MOSP".equals(uca.getBrandCode()))
        {
            return;
        }
        
        if ("CPE1".equals(uca.getBrandCode()))
        {
            return;
        }
        
        /* 拼装子元素 */
        String str = btd.getRD().getPageRequestData().getString("RES_PLATSVCS","");     
        if(StringUtils.isNotEmpty(str)){
        	 IDataset elems = new DatasetList(str);
             this.platSvcsOpen(elems, uca, btd);
        }
        this.other(uca, btd);

    }
    
    private void platSvcsOpen(IDataset serviceList, UcaData uca, BusiTradeData btd) throws Exception
    {
    	String tradeTypeCode = btd.getTradeTypeCode() ;
    	if(!"310".equals(tradeTypeCode))
    	{
    		return ;
    	}

        for (int i = 0; i < serviceList.size(); i++)
        {
            String serviceId = serviceList.getData(i).getString("ELEMENT_ID", "");
            String serviceName = serviceList.getData(i).getString("ELEMENT_NAME", "");
            if (StringUtils.isBlank(serviceId))
            {
                continue;
            }

            PlatSvcTradeData newPstd = new PlatSvcTradeData();
            newPstd.setElementId(serviceId);
            newPstd.setModifyTag(BofConst.MODIFY_TAG_ADD);
            newPstd.setUserId(uca.getUserId());
            newPstd.setBizStateCode(PlatConstants.STATE_OK);
            newPstd.setProductId(PlatConstants.PRODUCT_ID);
            newPstd.setPackageId(PlatConstants.PACKAGE_ID);
            newPstd.setStartDate(btd.getRD().getAcceptTime());
            newPstd.setEndDate(SysDateMgr.END_DATE_FOREVER);
            String instId = SeqMgr.getInstId();
            newPstd.setInstId(instId);
            newPstd.setActiveTag("");// 主被动标记
            newPstd.setOperTime(SysDateMgr.getSysTime());
            newPstd.setPkgSeq("");// 批次号，批量业务时传值
            newPstd.setUdsum("");// 批次数量
            newPstd.setIntfTradeId("");
            newPstd.setOperCode(PlatConstants.OPER_ORDER);
            newPstd.setOprSource("08");
            newPstd.setIsNeedPf("1");
            PlatSvcTrade.dealFirstTime(newPstd, uca); // 处理首次订购时间，连带开的可能首次订购时间为空
            btd.add(uca.getSerialNumber(), newPstd);
            
            //创建other台账
            OtherTradeData otherTD = new OtherTradeData();
            otherTD.setRsrvValueCode("RES_PLATSVC");
            
            otherTD.setRsrvStr1(serviceId);
            otherTD.setRsrvStr2(serviceName);
            otherTD.setStartDate(SysDateMgr.getSysTime());
            otherTD.setUserId(btd.getRD().getUca().getUserId());
            otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
            otherTD.setRemark("复机恢复之前平台服务other数据");
            otherTD.setModifyTag("0");
            otherTD.setInstId(SeqMgr.getInstId());
            btd.add(uca.getSerialNumber(), otherTD);
        }

    }
    
    //把复机恢复优惠和服务记录入other表
    private void other(UcaData uca, BusiTradeData btd) throws Exception
    {
        String str = btd.getRD().getPageRequestData().getString("RES_DISCNTS","");
        if(StringUtils.isNotEmpty(str)){
        	IDataset elems = new DatasetList(str);
            this.otherTrade(elems, btd, "RES_DISCNTS");
        }
        String svcStr = btd.getRD().getPageRequestData().getString("RES_SVCS","");
        if(StringUtils.isNotEmpty(svcStr)){
        	IDataset elems = new DatasetList(svcStr);
            this.otherTrade(elems, btd, "RES_SVCS");
        }
        
    }
    
    private void otherTrade(IDataset serviceList,BusiTradeData btd, String  rsrvValue) throws Exception
    {
    	
    	 for (int i = 0; i < serviceList.size(); i++)
         {
             String code = serviceList.getData(i).getString("ELEMENT_ID", "");
             String codeName = serviceList.getData(i).getString("ELEMENT_NAME", "");
             if (StringUtils.isBlank(code))
             {
                 continue;
             }
             
             //创建other台账
             OtherTradeData otherTD = new OtherTradeData();
             otherTD.setRsrvValueCode(rsrvValue);
             
             otherTD.setRsrvStr1(code);
             otherTD.setRsrvStr2(codeName);
             otherTD.setStartDate(SysDateMgr.getSysTime());
             otherTD.setUserId(btd.getRD().getUca().getUserId());
             otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
             otherTD.setRemark("复机恢复之前元素记录other数据");
             otherTD.setModifyTag("0");
             otherTD.setInstId(SeqMgr.getInstId());
             btd.add(btd.getMainTradeData().getSerialNumber(), otherTD);
         }
    	
    }

}
