
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.build;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.PlatReload;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class BuildPlatFor139Mail extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        PlatReqData req = (PlatReqData) brd;
        UcaData uca = req.getUca();
        List<PlatSvcData> platSvcs = new ArrayList<PlatSvcData>();
        List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(PlatReload.mail139Free);
        if (pstds == null || pstds.size() <= 0)
        {
            pstds = uca.getUserPlatSvcByServiceId(PlatReload.mail139Standard);
        }
        if (pstds == null || pstds.size() <= 0)
        {
            pstds = uca.getUserPlatSvcByServiceId(PlatReload.mail139Vip);
        }
        if (pstds == null || pstds.size() <= 0)
        {
            // 用户没有139邮箱服务，则订购5元版
            PlatSvcData psd = new PlatSvcData();
            psd.setElementId(PlatReload.mail139Standard);
            psd.setOfficeData(PlatOfficeData.getInstance(psd.getElementId()));
            psd.setOperCode(PlatConstants.OPER_ORDER);
            psd.setOprSource("14");
            platSvcs.add(psd);
        }
        else
        {
            PlatSvcTradeData pstd = pstds.get(0);
            PlatSvcData psd = new PlatSvcData();
            psd.setElementId(pstd.getElementId());
            psd.setOfficeData(PlatOfficeData.getInstance(psd.getElementId()));
            psd.setOperCode(PlatConstants.OPER_PE_ACTIVATE);
            psd.setOprSource("14");
            psd.setInstId(pstd.getInstId());
            platSvcs.add(psd);
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
