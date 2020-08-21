/**
 * 
 */

package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.widechangeuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @ClassName:
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author:
 * @date:
 */

public abstract class ModifyUserWidenetAcInfoNew extends PersonBasePage
{

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData(); 
        IDataset dataset = CSViewCall.call(this, "SS.WideChangeUserCheckSVC.checkOldNumForChangeUser", data);
        if (IDataUtil.isEmpty(dataset))
        {
            CSViewException.apperr(WidenetException.CRM_WIDENET_4);
        }
        IData wideInfo = dataset.getData(0); 
        wideInfo.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        String widetype=dataset.getData(0).getString("RSRV_STR2");
        wideInfo.put("WIDE_TYPE_CODE",widetype);
        if ("1".equals(widetype))
        {
        	wideInfo.put("WIDE_TYPE_NAME", "移动FTTB");
        }else if ("2".equals(widetype))
        {
        	wideInfo.put("WIDE_TYPE_NAME", "铁通ADSL");
        }else if ("3".equals(widetype))
        {
        	wideInfo.put("WIDE_TYPE_NAME", "移动FTTH");
        }else if ("5".equals(widetype))
        {
        	wideInfo.put("WIDE_TYPE_NAME", "铁通FTTH");
        }else if ("6".equals(widetype))
        {
        	wideInfo.put("WIDE_TYPE_NAME", "铁通FTTB");
        } 
        
        //有IMS固话信息，需先做IMS固话拆机
        IData param = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put(Route.ROUTE_EPARCHY_CODE, "0898");
        param.put("EPARCHY_CODE", "0898");
        IData imsInfo = null;
        try {
        	imsInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", param);
		} catch (Exception e) {
		}
        if (IDataUtil.isNotEmpty(imsInfo)) {
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "用户取消家庭IMS固话后才可办理宽带过户！");
		}
        
        setWideInfo(wideInfo);
        setInfo(wideInfo); 
    }

    /**
     * 重写提交方法
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.WideChangeUserRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    /*
     * 针对变更帐号，查询变更帐号信息
     */
    public void qureyMphone(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER_A", ""));
        IDataset dataset = CSViewCall.call(this, "SS.WideChangeUserCheckSVC.checkNewNumForChangeUser", data);
        setAjax(dataset);
    }

    public abstract void setWideInfo(IData wideInfo);
    public abstract void setInfo(IData info);
}
