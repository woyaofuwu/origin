
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.fetion;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class FetionMonthlyAction implements IProductModuleAction
{

    /**
     * 处理飞信大包月业务 修改结束时间
     * 
     * @author xiekl
     */
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
    	//将不需要调用服务的判断提前@tanzheng@20190611
    	if (btd.getTradeTypeCode().equals("3800"))
    	{
    		return;
    	}
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());

        // 如果是飞信业务,且是订购续订
        if ("23".equals(officeData.getBizTypeCode()) && "06_26".indexOf(pstd.getOperCode()) >= 0)
        {

            if ("FXHY03".equals(officeData.getBizCode()))
            {
                pstd.setEndDate(SysDateMgr.endDateOffset(pstd.getStartDate(), "3", "3"));
            }
            if ("FXHY06".equals(officeData.getBizCode()))
            {
                pstd.setEndDate(SysDateMgr.endDateOffset(pstd.getStartDate(), "6", "3"));
            }
            if ("FXHY12".equals(officeData.getBizCode()))
            {
                pstd.setEndDate(SysDateMgr.endDateOffset(pstd.getStartDate(), "12", "3"));

            }

        }
    }

}
