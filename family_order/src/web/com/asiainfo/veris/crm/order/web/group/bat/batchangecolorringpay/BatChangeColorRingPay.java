
package com.asiainfo.veris.crm.order.web.group.bat.batchangecolorringpay;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class BatChangeColorRingPay extends CSBasePage
{
    
	/**
	 * 页面初始化
	 * @param cycle
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-7-13
	 */
    public void init(IRequestCycle cycle) throws Exception
    {
    	String batchOperType = getData().getString("BATCH_OPER_TYPE", "NOXXXX");
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
        setCondition(getData());
    }
	/**
     * 查询集团彩铃用户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryColorRingUser(IRequestCycle cycle) throws Exception
    {
        String serialNumber = getData().getString("cond_SERIAL_NUMBER");

        // 查询集团用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber);

        // 判断是否为彩铃用户
        if (!"6200".equals(userData.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(GrpException.CRM_GRP_653, serialNumber);
        }

        // 查询集团客户信息
        String custId = userData.getString("CUST_ID", "");

        IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);

        // 查询用户付费计划信息
        IData svcData = new DataMap();
        svcData.put("USER_ID", userData.getString("USER_ID", ""));
        svcData.put("USER_ID_A", "-1");
        IDataset payPlanList = CSViewCall.call(this, "CS.UserPayPlanInfoQrySVC.getPayPlanInfosByUserIdForGrp", svcData);

        setPayPlanList(payPlanList);

        setAjax(custData);
    }

    public abstract void setCondition(IData condition);

    public abstract void setPayPlanList(IDataset payPlanList);
}
