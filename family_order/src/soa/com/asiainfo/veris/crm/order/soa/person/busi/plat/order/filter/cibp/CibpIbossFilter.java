
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cibp;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class CibpIbossFilter implements IFilterIn
{

    public static String basicService = "40227758,40227759,40227760,40227761,40227762,40227763,40227764";

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        if (StringUtils.isNotBlank(input.getString("OPR_NUMB")))
        {
            input.put("INTF_TRADE_ID", input.getString("OPR_NUMB"));
        }
        if (StringUtils.isNotBlank(input.getString("TRANS_ID")))
        {
            input.put("INTF_TRADE_ID", input.getString("TRANS_ID"));
        }

        String infoCode = input.getString("INFO_CODE");
        if ("STB_ID".equals(infoCode))
        {
            input.put("INFO_CODE", "STBID");
        }

        if ("09".equals(input.getString("OPER_CODE")))
        {
            input.put("OPER_CODE", "06");
            input.put("RSRV_STR1", input.getString("OLD_SP_CODE", ""));
            input.put("RSRV_STR2", input.getString("OLD_BIZ_CODE", ""));
        }

        if ("08".equals(input.getString("OPER_CODE")))
        {
            UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER", input.getString("ID_VALUE")));
            String[] basicServices = basicService.split(",");
            boolean flag = false;
            String oldStdId = "";
            for (int i = 0; i < basicServices.length; i++)
            {
                List<PlatSvcTradeData> userPlatSvcList = uca.getUserPlatSvcByServiceId(basicServices[i]);
                if (userPlatSvcList != null && userPlatSvcList.size() > 0)
                {
                    flag = true;
                    AttrTradeData userAttr = uca.getUserAttrsByRelaInstIdAttrCode(userPlatSvcList.get(0).getInstId(), "STBID");
                    if (userAttr != null)
                    {
                        oldStdId = userAttr.getAttrValue();
                    }
                    break;
                }
            }

            if (!flag)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "用户没有订购互联网电视服务，不能更换机顶盒");
            }

            String ibossOldStd = input.getString("OLDSTBID"); // iboss传入的旧的机顶盒ID
            if (StringUtils.isBlank(ibossOldStd))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "旧机顶盒ID不能为空");
            }

            if (!StringUtils.equals(ibossOldStd, oldStdId))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "传入旧机顶盒ID与数据库中的保存的不一致");
            }

        }
    }

}
