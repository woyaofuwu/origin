
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.cibp;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.PlatUtils;

/**
 * 互联网电视的资源处理
 * 
 * @author xiekl 分为四种情况 1.订购互联网电视； 2.更新牌照 3.更换机顶盒 4.退订
 */
public class CibpResAction implements IProductModuleAction
{
	
    public static String basicService = "40227758,40227759,40227760,40227761,40227762,40227763,40227764";

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
    	String tradeTypeCode = btd.getTradeTypeCode();
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        //将不需要调用服务的判断提到前边@tanzheng@20190611
        if( "3800".equals(tradeTypeCode)|| "3902".equals(tradeTypeCode)
        		|| "80006737".equals(pstd.getElementId())){
        	return;
        }
        List<AttrTradeData> platSvcAttrList = pstd.getAttrTradeDatas();


        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());
        if (!"51".equals(officeData.getBizTypeCode()))
        {
            return;
        }

        if (PlatConstants.OPER_ORDER.equals(pstd.getOperCode()))
        {
            String stbId = PlatUtils.getAttrValue("STB_ID", platSvcAttrList);

            String[] basicServices = basicService.split(",");
            boolean flag = false;
            for (int i = 0; i < basicServices.length; i++)
            {
                List<PlatSvcTradeData> userPlatSvcList = uca.getUserPlatSvcByServiceId(basicServices[i]);
                if (userPlatSvcList != null && userPlatSvcList.size() > 0)
                {
                    flag = true;
                    break;
                }
            }
            // 作为更换牌照处理
            if (flag)
            {
                List<ResTradeData> resList = uca.getUserAllRes();
                for (int i = 0; i < resList.size(); i++)
                {
                    ResTradeData res = resList.get(i);
                    if ("J".equals(res.getRsrvTag1()))
                    {
                        res.setRsrvStr2(pstd.getElementId());
                        res.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        btd.add(uca.getSerialNumber(), res);
                    }
                }
            }
            else
            {
                ResTradeData resTrade = new ResTradeData();
                resTrade.setUserId(uca.getUserId());
                resTrade.setEndDate(SysDateMgr.END_DATE_FOREVER);
                resTrade.setUserIdA("-1");
                resTrade.setResTypeCode("4");
                resTrade.setImsi(stbId);
                resTrade.setResCode("N");
                resTrade.setKi("N");
                resTrade.setInstId(SeqMgr.getInstId());
                resTrade.setStartDate(SysDateMgr.getYesterdayTime());
                resTrade.setRemark("平台业务插入");
                resTrade.setRsrvStr2(pstd.getElementId());
                resTrade.setRsrvStr3("50000000");
                resTrade.setRsrvStr4("1,平台提供机顶盒");
                resTrade.setRsrvTag1("J");
                resTrade.setModifyTag(BofConst.MODIFY_TAG_ADD);
                btd.add(uca.getSerialNumber(), resTrade);
            }

        }
        else if (PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode()))
        {
//            List<ResTradeData> resList = uca.getUserAllRes();
//            for (int i = 0; i < resList.size(); i++)
//            {
//                ResTradeData res = resList.get(i);
//                if ("J".equals(res.getRsrvTag1()))
//                {
//                    res.setEndDate(SysDateMgr.getSysTime());
//                    res.setModifyTag(BofConst.MODIFY_TAG_DEL);
//                    btd.add(uca.getSerialNumber(), res);
//                }
//            }
        }
        else if (PlatConstants.OPER_USER_DATA_MODIFY.equals(pstd.getOperCode()))
        {
            List<ResTradeData> resList = uca.getUserAllRes();
            for (int i = 0; i < resList.size(); i++)
            {
                ResTradeData res = resList.get(i);
                if ("J".equals(res.getRsrvTag1()))
                {
                    String stbId = PlatUtils.getAttrValue("STB_ID", platSvcAttrList);
                    res.setImsi(stbId);
                    res.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    btd.add(uca.getSerialNumber(), res);
                }
            }
        }
    }

}
