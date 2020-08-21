
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.stmm;

import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class StmmAction implements IProductModuleAction
{

    /**
     * 处理精彩奥运专享包 修改结束时间
     * 
     * @author xiekl
     */
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());

        // 如果是飞信业务,且是订购续订
        if ("30828088".equals(officeData.getBizCode()) && "06".indexOf(pstd.getOperCode()) >= 0)
        {

        
            pstd.setEndDate(SysDateMgr.getLastDateThisMonth());

        }
        else if("30828088".equals(officeData.getBizCode()) && "07".indexOf(pstd.getOperCode()) >= 0)
        {
        	 CSAppException.apperr(PlatException.CRM_PLAT_0904);
        }
    }

}
