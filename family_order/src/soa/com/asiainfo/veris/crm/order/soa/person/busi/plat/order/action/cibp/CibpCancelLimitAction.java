
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.cibp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

/**
 * Copyright: Copyright 2017 alik
 * 
 * @ClassName: CibpCancelLimitAction.java
 * @Description: 魔百盒业务对于通过一级BOSS渠道发起的退订返回退订失败
 * @version: v1.0.0
 * @author: songxw
 */
public class CibpCancelLimitAction implements IProductModuleAction
{
    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub

        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());
        
        /*查出配置的可退订魔百合业务SELECT a.*,rowid FROM ucr_cen1.td_s_static a where a.type_id='CIBP_CANCEL';*/
        IDataset staticConfigs = StaticInfoQry.getStaticValueByTypeId("CIBP_CANCEL");
        int size = staticConfigs.size();
        String bizcodeAll = "";
        for (int i = 0; i < size; i++)
        {
            IData data = staticConfigs.getData(i);
            String bizCode = data.getString("DATA_ID");
            bizcodeAll = bizcodeAll + "," + bizCode;
        }

        if (btd.getTradeTypeCode().equals("3700")&&"51".equals(officeData.getBizTypeCode())&&!bizcodeAll.contains((officeData.getSpCode()+"_"+officeData.getBizCode()))
        		&&PlatConstants.OPER_CANCEL_ORDER.equals(pstd.getOperCode())
        		)
        {
        	CSAppException.apperr(PlatException.CRM_PLAT_3021);
        }
    }
}