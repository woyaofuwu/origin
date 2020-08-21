
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.switches;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class SwitchAction implements IProductModuleAction
{

    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatSvcData psd = (PlatSvcData) pstd.getPmd();
        PlatOfficeData officeData = psd.getOfficeData();
        String bizTypeCode = officeData.getBizTypeCode();
        List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcs();
        int size = userPlatSvcs.size();

        if (PlatConstants.OPER_SERVICE_OPEN.equals(pstd.getOperCode()))
        {
            return;
        }
        else if (PlatConstants.OPER_SERVICE_CLOSE.equals(pstd.getOperCode()))
        {
            for (int i = 0; i < size; i++)
            {
                PlatSvcTradeData userPlatSvc = userPlatSvcs.get(i);
                PlatOfficeData userPlatOffice = null;
                try
                {
                	  userPlatOffice = PlatOfficeData.getInstance(userPlatSvc.getElementId());
                }catch(Exception e)
                {
                	
                }
                
                if(userPlatOffice==null)
                {
                	continue;
                }
                
               
                if (bizTypeCode.equals(userPlatOffice.getBizTypeCode()) && userPlatOffice.getServType().equals("1") && BofConst.MODIFY_TAG_USER.equals(userPlatSvc.getModifyTag()))
                {
                    PlatSvcTradeData temp = userPlatSvc.clone();
                    temp.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
                    temp.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    temp.setBizStateCode(PlatConstants.STATE_CANCEL);
                    temp.setEndDate(btd.getRD().getAcceptTime());
                    temp.setOprSource(pstd.getOprSource());
                    temp.setIsNeedPf("0"); // 分开关关时，退订的业务也不需要发报文
                    temp.setOperTime(pstd.getOperTime());
                    btd.add(uca.getSerialNumber(), temp);
                }
            }
        }
    }

}
