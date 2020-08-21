
package com.asiainfo.veris.crm.order.web.group.bat.batchgvpmnusersvc;

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
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public abstract class BatChgVpmnUserSvc extends CSBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
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

        // 非VPMN集团不能办理此业务
        if (!"VPMN".equals(userData.getString("BRAND_CODE")) || !"8000".equals(userData.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_15, serialNumber);
        }

        String userId = userData.getString("USER_ID", "");
        String custId = userData.getString("CUST_ID", "");

        // 查询客户信息
        IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);

        // 查询用户VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userId);

        // 判断是否为子母VPMN集团
        IData svcData = new DataMap();
        svcData.put("USER_ID_B", userId);
        svcData.put("RELATION_TYPE_CODE", "40");

        IDataset relaDataset = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(this, userId, "40", false);

        if (IDataUtil.isEmpty(relaDataset))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_48);
        }

        // 设置返回值
        IData vpmnData = new DataMap();
        vpmnData.put("GROUP_ID", custData.getString("GROUP_ID"));
        vpmnData.put("USER_ID", userId);
        vpmnData.put("SERIAL_NUMBER", serialNumber);
        vpmnData.put("VPN_NAME", userVpnData.getString("VPN_NAME"));
        vpmnData.put("GROUP_ADDR", custData.getString("GROUP_ADDR"));
        vpmnData.put("GROUP_CONTACT_PHONE", custData.getString("GROUP_CONTACT_PHONE"));

        setVpmn(vpmnData);

        setCondition(getData());
    }

    public abstract void setCondition(IData condition);

    public abstract void setVpmn(IData vpmnData);
}
