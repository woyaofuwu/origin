
package com.asiainfo.veris.crm.order.web.group.param.ggci;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class UserParamInfo extends IProductParamDynamic
{

    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        String productNo = parainfo.getString("PRODUCT_ID");

       
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {

        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        String productNo = parainfo.getString("PRODUCT_ID");

        parainfo.put("VISP_INFO", new DatasetList());
        parainfo.put("SYS_DATE_NOW", SysDateMgr.getSysDate());
        result.put("PARAM_INFO", parainfo);

        return result;
    }
}
