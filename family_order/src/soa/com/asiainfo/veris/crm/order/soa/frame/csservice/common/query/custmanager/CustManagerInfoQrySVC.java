
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.log.LogBean;

public class CustManagerInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 获取VPMN客户经理权限
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset checkVpmnRight(IData inparam) throws Exception
    {
        String staffId = inparam.getString("STAFF_ID");
        String userProdCode = inparam.getString("USER_PRODUCT_CODE");
        String rightCode = inparam.getString("RIGHT_CODE");
        return CustManagerInfoQry.checkVpmnRight(staffId, userProdCode, rightCode);
    }

    /**
     * 获取VPMN客户经理权限（包括注销的）
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset checkVpmnRightAll(IData inparam) throws Exception
    {
        String staffId = inparam.getString("STAFF_ID");
        String userProdCode = inparam.getString("USER_PRODUCT_CODE");
        String rightCode = inparam.getString("RIGHT_CODE");
        String startDate = inparam.getString("START_DATE");
        return CustManagerInfoQry.checkVpmnRightAll(staffId, userProdCode, rightCode, startDate);
    }

    /**
     * 记录客户经理操作日志
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public boolean insertOperLog(IData inparam) throws Exception
    {
        return LogBean.insertOperLog(inparam);
    }

    /**
     * 新增VPMN客户经理权限
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public boolean insertVpnRight(IData inparam) throws Exception
    {
        return CustManagerInfoQry.insertVpnRight(inparam);
    }

    /**
     * 查询集团客户经理信息
     * 
     * @param custManagerId
     * @return
     * @throws Exception
     */
    public IDataset qryCustManagerInfoById(IData inparam) throws Exception
    {
        String custManagerId = inparam.getString("CUST_MANAGER_ID");
        return UStaffInfoQry.qryCustManagerStaffById(custManagerId);
    }

    /**
     * 查询客户经理权限信息列表
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryVpmnCustMgrStaffList(IData inparam) throws Exception
    {
        String custMgrId = inparam.getString("CUST_MANAGER_ID");
        String userProdCode = inparam.getString("USER_PRODUCT_CODE");
        return CustManagerInfoQry.qryVpmnCustMgrStaffList(custMgrId, userProdCode, this.getPagination());
    }

    /**
     * @Description:查询VPMN产品客户经理分配批量导入批次信息
     * @author sungq3
     * @date 2014-05-19
     * @return
     * @throws Exception
     */
    public IDataset qryVpmnDisInfo(IData inparam) throws Exception
    {
        String importId = inparam.getString("IMPORT_ID");
        String dealState = inparam.getString("DEAL_STATE");
        String impFileName = inparam.getString("IMPORT_FILENAME");
        String impType = inparam.getString("IMPORT_TYPE");
        String startDate = inparam.getString("START_DATE");
        String endDate = inparam.getString("END_DATE");
        return CustManagerInfoQry.qryVpmnDisInfo(importId, dealState, impFileName, impType, startDate, endDate, this.getPagination());
    }

    /**
     * 查询VPMN集团用户信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryVpmnGroupInfo(IData inparam) throws Exception
    {
        String groupId = inparam.getString("GROUP_ID");
        String vpnNo = inparam.getString("VPN_NO");
        String vpnName = inparam.getString("VPN_NAME");
        String cityCode = inparam.getString("CITY_CODE");
        return CustManagerInfoQry.qryVpmnGroupInfo(groupId, vpnNo, vpnName, cityCode, this.getPagination());
    }

    /**
     * 查询员工权限信息列表
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryVpmnStaffList(IData inparam) throws Exception
    {
        String staffId = inparam.getString("STAFF_ID");
        String userProdCode = inparam.getString("USER_PRODUCT_CODE");
        return CustManagerInfoQry.qryVpmnStaffList(staffId, userProdCode, this.getPagination());
    }

    /**
     * @Description:查询此次导入明细
     * @author sungq3
     * @date 2014-05-20
     * @return
     * @throws Exception
     */
    public IDataset queryThisVpmnManagerInfo(IData inparam) throws Exception
    {
        String importId = inparam.getString("IMPORT_ID");
        String dealState = inparam.getString("DEAL_STATE");
        return CustManagerInfoQry.queryThisVpmnManagerInfo(importId, dealState, this.getPagination());
    }

    /**
     * 查询VPMN集团所在地市
     * 
     * @return
     * @throws Exception
     */
    public IDataset queryVpmnCityCode(IData inparam) throws Exception
    {
        String vpnNo = inparam.getString("VPN_NO");
        return CustManagerInfoQry.queryVpmnCityCode(vpnNo);
    }

    /**
     * @Description:保存VpnInfo
     * @author sungq3
     * @date 2014-05-22
     * @return
     * @throws Exception
     */
    public boolean saveVpnInfo(IData inparam) throws Exception
    {
        return CustManagerInfoQry.updateVpnInfo(inparam);
    }

    /**
     * 更新VPMN客户经理权限
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public boolean updVpnRight(IData inparam) throws Exception
    {
        return CustManagerInfoQry.updVpnRight(inparam);
    }
    
    public IDataset qryVpmnStaffInfo(IData input) throws Exception {
    	 return CustManagerInfoQry.qryVpmnStaffInfo(input,this.getPagination());
    }
}
