
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.build;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class BuildPlatForWlanSuspendAndResume extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        PlatReqData req = (PlatReqData) brd;

        List<PlatSvcData> platSvcDatas = new ArrayList<PlatSvcData>();
        String services = param.getString("SERVICE_ID", param.getString("ELEMENT_ID", ""));
        String operCodes = param.getString("OPER_CODE", "");
        String wlanDealTypes = param.getString("WLAN_DEAL_TYPE",""); //1--15G，2--50G

        String[] serviceArray = services.split("\\|");
        String[] operCodeArray = operCodes.split("\\|");

        for (int i = 0; i < serviceArray.length; i++)
        {
            if (operCodeArray == null)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "输入操作码OPER_CODE不能为空");
            }

            if (serviceArray.length != operCodeArray.length)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "输入操作码OPER_CODE的个数与SERVICE_ID个数需相同");
            }

            IData svcData = new DataMap();
            svcData.put("SERVICE_ID", serviceArray[i]);
            svcData.put("OPER_CODE", operCodeArray[i]);
            PlatSvcData psd = new PlatSvcData(svcData);

            if (operCodeArray[i].equals("04"))
            {
            	if("2".equals(wlanDealTypes)){   //计费传递50G封顶的时候记录该字段的值为F，在后面的规则里面会判断如果是这个值不允许再开通操作
            		psd.setRemark("F"); // 记录封顶暂停标识,50G封顶的时候使用
            	}
                psd.setRsrvStr8("14");
                psd.setRsrvStr9("1");  //流量封顶需要设置成1，提供给次月初自动恢复使用
            }else if(operCodeArray[i].equals("05")){    //暂停处理，需要更新RSRV_STR8为15的操作
            	psd.setRsrvStr8("15");
            }

            platSvcDatas.add(psd);
        }
        req.setPlatSvcDatas(platSvcDatas);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new PlatReqData();
    }

}
