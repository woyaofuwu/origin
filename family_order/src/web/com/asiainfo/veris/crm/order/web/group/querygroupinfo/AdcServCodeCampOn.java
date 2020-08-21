
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class AdcServCodeCampOn extends CSBasePage
{

    /**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * @Description: ADC集团成员查询
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void insertCamponServCode(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String flag = "true";
        String message = "";

        IData searchCond = new DataMap();
        searchCond.put("SVR_CODE", param.getString("SVR_CODE", " "));

        IDataset searchRst = CSViewCall.call(this, "SS.AdcCamponSvrCodeSvc.getAdcCamponSvrcodeInfo", searchCond);
        if (!IDataUtil.isEmpty(searchRst)) {
            flag = "false";
            message = "服务号码已经被预占！";
        }

        if ("true".equals(flag)) {
            CSViewCall.call(this, "SS.AdcCamponSvrCodeSvc.insertCamponServCode", param);
        }

        IData result = new DataMap();
        result.put("result", flag);
        result.put("message", message);
        setAjax(result);
    }

    public abstract void setInfo(IData condition);

}
