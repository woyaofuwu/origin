
package com.asiainfo.veris.crm.order.web.group.param.gfff;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;



public class UserParamInfoDingE extends IProductParamDynamic
{
	private Logger logger = Logger.getLogger(UserParamInfoDingE.class);
	
    @Override
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        
        //从EOSP获取专线数据
        IDataset eos = data.getDataset("EOS");

        if(IDataUtil.isEmpty(eos))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_909);
        }
        return result;
    }
    
    @Override
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        
        //从EOSP获取专线数据
        String eosStr = data.getString("EOS");
        
        if (StringUtils.isNotEmpty(eosStr) && !"{}".equals(eosStr))
        {
        }
        else 
        {
        	CSViewException.apperr(GrpException.CRM_GRP_909);
        }
        return result;
    }
}
