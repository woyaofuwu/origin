
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.build;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.OperCodeTrans;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class BuildPlatForRinpSyncIntf extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        PlatReqData req = (PlatReqData) brd;
        UcaData uca = req.getUca();
        req.setSync(true);
        List<PlatSvcData> platSvcs = new ArrayList<PlatSvcData>();
        String operCode = param.getString("OPER_CODE");
        OperCodeTrans.operCodeTrans(param);
        if (PlatConstants.OPER_MODIFY_PASSWORD.equals(operCode))
        {
            List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcs();
            if (userPlatSvcs != null && userPlatSvcs.size() > 0)
            {
                int size = userPlatSvcs.size();
                for (int i = 0; i < size; i++)
                {
                    PlatSvcTradeData userPstd = userPlatSvcs.get(i);
                    PlatOfficeData officeData = PlatOfficeData.getInstance(userPstd.getElementId());
                    if (!officeData.getBizTypeCode().equals(PlatConstants.PLAT_FARMING_CREDIT_ALL))
                    {
                        continue;
                    }
                    else
                    {
                        String bizCode = officeData.getBizCode();
                        int index = bizCode.indexOf("-");
                        if (index > 0 && bizCode.indexOf("-", index + 1) > 0)
                        {
                            PlatSvcData platSvc = new PlatSvcData();
                            platSvc.setElementId(userPstd.getElementId());
                            platSvc.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
                            platSvc.setOprSource(param.getString("OPR_SOURCE", "08"));
                            platSvc.setOfficeData(officeData);
                            platSvcs.add(platSvc);
                        }
                    }
                }
            }
        }
        req.setPlatSvcDatas(platSvcs);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new PlatReqData();
    }

}
