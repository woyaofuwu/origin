package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.common;


import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * @处理平台服务退订的结束时间
  有配置的才特殊处理
 */
public class DealPlatCancelEndDate implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());
        String bizTypeCode = officeData.getBizTypeCode();
        if (!PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()))
        {
            return ;
        }
       
        IDataset endDateConfigs=CommparaInfoQry.getCommparaByCodeCode1("CSM", "1911", bizTypeCode, pstd.getElementId());
        if (IDataUtil.isEmpty(endDateConfigs))
        {
            return;
        }
        //失效方式 0-立即取消（当前时间）3 -本账期末取消（本月最后一天的23:59:59）  9-2050-12-31 23:59:59
        String cancelMode = endDateConfigs.getData(0).getString("PARA_CODE10");
      
        if(!"".equals(cancelMode) && null!=cancelMode){
        	if("0".equals(cancelMode)){
        		 pstd.setEndDate(SysDateMgr.getSysTime());
        	}else if("3".equals(cancelMode)){
        		 pstd.setEndDate(SysDateMgr.getLastDateThisMonth());
        	}else if("9".equals(cancelMode)){
        		 pstd.setEndDate(SysDateMgr.END_DATE_FOREVER);
        	}
        }     
    }
}