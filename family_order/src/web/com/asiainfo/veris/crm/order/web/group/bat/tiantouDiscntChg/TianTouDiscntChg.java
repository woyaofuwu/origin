
package com.asiainfo.veris.crm.order.web.group.bat.tiantouDiscntChg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class TianTouDiscntChg extends GroupBasePage
{

    /**
     * 初始化批量弹出窗口页面
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
    	String batchOperType = getData().getString("BATCH_OPER_TYPE", "NOXXXX");
        IData data = new DataMap();
        data.put("SYBSYS_CODE", "CSM");
        data.put("PARAM_ATTR", "236");
        data.put("PARAM_CODE", "0");

        IDataset discntLists = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaInfoByAttrAndParam", data);

        setDiscntLists(discntLists);

        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", batchOperType);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	getData().put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	getData().put("MEB_VOUCHER_FILE_SHOW","true");
        }
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
        setInfo(getData());

    }

    public abstract void setDiscntLists(IDataset dataset);

    public abstract void setInfo(IData data);
}
