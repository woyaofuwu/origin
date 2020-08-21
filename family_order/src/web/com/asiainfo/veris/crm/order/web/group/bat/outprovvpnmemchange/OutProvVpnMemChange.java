
package com.asiainfo.veris.crm.order.web.group.bat.outprovvpnmemchange;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class OutProvVpnMemChange extends GroupBasePage
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
     * 查询VPMN信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void qryVpmnInfo(IRequestCycle cycle) throws Throwable
    {

        String serialNumber = getData().getString("cond_SERIAL_NUMBER");

        // 查询VPMN用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber);

        // 判断是否VPMN用户
        if (!"VPMN".equals(userData.getString("BRAND_CODE")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_15, serialNumber);
        }

        if ("8050".equals(userData.getString("PRODUCT_ID")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_43);
        }

        // 查询VPMN客户信息
        IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, userData.getString("CUST_ID", ""));

        // 查询VPN信息
        String userId = userData.getString("USER_ID");

        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userId);

        if (!"2".equals(userVpnData.getString("VPN_SCARE_CODE")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_44);
        }

        IData vpmnData = new DataMap();

        vpmnData.put("USER_ID", userId);
        vpmnData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
        vpmnData.put("VPN_NAME", userVpnData.getString("VPN_NAME"));
        vpmnData.put("GROUP_ADDR", custData.getString("GROUP_ADDR"));
        vpmnData.put("GROUP_CONTACT_PHONE", custData.getString("GROUP_CONTACT_PHONE"));
        vpmnData.put("GROUP_ID", custData.getString("GROUP_ID"));
        vpmnData.put("PRODUCT_ID", userData.getString("PRODUCT_ID"));
        setInfo(vpmnData);

        IData data = new DataMap();
        data.put("SYBSYS_CODE", "CGM");
        data.put("PARAM_ATTR", "80");
        data.put("PARAM_CODE", "0");

        IDataset discntLists = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaInfoByAttrAndParam", data);

        setDiscntLists(discntLists);

        setCondition(getData());

    }

    public abstract void setCondition(IData data);

    public abstract void setDiscntList(IData data);

    public abstract void setDiscntLists(IDataset dataset);

    public abstract void setInfo(IData data);

    /**
     * 验证跨省VPMN是否有权限办理安邦跨省套餐
     * 
     * @param cycle
     * @throws Throwable
     */
    public void validchkVpmn(IRequestCycle cycle) throws Throwable
    {
        String discntCode = getParameter("DISCNT_CODE");
        String serialNumber = getParameter("SERIAL_NUMBER");

        // modify by lixiuyu@20100719 安利（中国）日用品有限公司信息化合作需求新增安利跨省V网0元套餐（99720504）和安利跨省V网15元套餐（99720505）
        if (discntCode.equals("99720501") || discntCode.equals("99720502") || discntCode.equals("99720503") || discntCode.equals("99720504") || discntCode.equals("99720505"))
        {
            IDataset commParams = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCode(this, "CSM", "119", "0", serialNumber, getTradeEparchyCode());
            if (IDataUtil.isEmpty(commParams))
            {
                CSViewException.apperr(VpmnUserException.VPMN_USER_45, discntCode);
            }
        }
    }
}
