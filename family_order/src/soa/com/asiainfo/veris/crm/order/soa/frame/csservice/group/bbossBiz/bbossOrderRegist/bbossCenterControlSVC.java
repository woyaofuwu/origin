package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderRegist;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class bbossCenterControlSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    public static void rigisitXmlInfo(IData inpram)throws Exception{
        bbossCenterControl.rigistXmlData(inpram, inpram.getString("DEAL_STATE"));//0代表等待处理
    }

}
