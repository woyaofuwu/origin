
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.mcld;

import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 彩云平台处理
 * 
 * @author xiekl 1.如果是彩云包年业务，结束时间是下一年的这个月 2.处理通行证 3.如果是包年业务不能退订
 */
public class McldAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
    	//将不需要调用服务的判断提前@tanzheng@20190611
    	if (btd.getTradeTypeCode().equals("3800"))
    	{
    		return;
    	}
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());

        if (!officeData.getBizTypeCode().equals("72"))
        {
            return;
        }

        // 针对包年业务的处理
        if (officeData.getSpCode().indexOf("BN") > 0)
        {
            if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
            {
                // 包年业务，设置到期时间为一年后
                pstd.setEndDate(SysDateMgr.endDateOffset(pstd.getStartDate(), "12", "2"));
            }

            if (PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "彩云平台包年业务不能退订");
            }
        }

        // // 如果有互联网通行证号
        // if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()) && pstd.getRsrvStr10() != null &&
        // pstd.getRsrvStr10().indexOf("PASS_ID:") > 0)
        // {
        // IDataset netUserInfos = UserOtherInfoQry.getUserOther(uca.getUserId(), "NET_USER_INFO");
        //
        // OtherTradeData otherTD = new OtherTradeData();
        // if (netUserInfos != null && !netUserInfos.isEmpty())
        // {
        // otherTD.setRsrvValueCode("NET_USER_INFO");
        // otherTD.setRsrvValue(pstd.getRsrvStr10().substring(8));
        // otherTD.setRsrvStr1(btd.getTradeId());
        //
        // otherTD.setStartDate(SysDateMgr.getSysTime());
        // otherTD.setUserId(pstd.getUserId());
        // otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
        // otherTD.setRemark("互联网通行证号");
        // otherTD.setModifyTag("0");
        // otherTD.setInstId(SeqMgr.getInstId());
        // btd.add(uca.getSerialNumber(), otherTD);
        // }
        // else
        // {
        // otherTD.setRsrvValueCode("NET_USER_INFO");
        // otherTD.setRsrvValue(pstd.getRsrvStr10().substring(8));
        // otherTD.setRsrvStr1(btd.getTradeId());
        //
        // otherTD.setStartDate(netUserInfos.getData(0).getString("START_DATE"));
        // otherTD.setUserId(pstd.getUserId());
        // otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
        // otherTD.setRemark("互联网通行证号");
        // otherTD.setModifyTag("2");
        // otherTD.setInstId(SeqMgr.getInstId());
        // btd.add(uca.getSerialNumber(), otherTD);
        // }

        // }

    }

}
