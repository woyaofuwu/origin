
package com.asiainfo.veris.crm.order.web.group.bat.batchangevpmnshortcode;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class BatChangeVPMNShortCode extends CSBasePage
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
     * 查询VPMN用户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryVpmnUser(IRequestCycle cycle) throws Exception
    {
        String serialNumber = getData().getString("cond_SERIAL_NUMBER");

        // 查询用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber);

        if (!"VPMN".equals(userData.getString("BRAND_CODE")) || !"8000".equals(userData.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_15, serialNumber);
        }

        // 查询客户信息
        IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, userData.getString("CUST_ID", ""));

        String userId = userData.getString("USER_ID", "");

        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userId);

        // 设置返回值
        IData vpmnData = new DataMap();

        vpmnData.put("GROUP_ID", custData.getString("GROUP_ID"));
        vpmnData.put("USER_ID", userId);
        vpmnData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
        vpmnData.put("VPN_NAME", userVpnData.getString("VPN_NAME"));

        setVpmn(vpmnData);
    }

    public abstract void setCondition(IData condition);

    public abstract void setVpmn(IData vpmnData);
}
