
package com.asiainfo.veris.crm.order.web.group.vpmnmanagermgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class VpmnManagerList extends CSBasePage
{
    public abstract IData getCondition();

    public abstract void setCondition(IData condition);

    public abstract IDataset getInfos();

    public abstract void setInfos(IDataset infos);

    public abstract String getStaffId();

    public abstract void setStaffId(String staffId);

    public abstract IData getStaffData();

    public abstract void setStaffData(IData staffData);

    public abstract void setPageCounts(long pageCounts);

    public abstract void setHintInfo(String hintInfo);

    /**
     * @Description: 初始化页面方法
     * @author sungq3
     * @date 2014-05-15
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("VPMN客户经理维护~~!");
        IData staffData = new DataMap();
        staffData.put("START_DATE", SysDateMgr.getSysDate() + SysDateMgr.START_DATE_FOREVER);
        staffData.put("END_DATE", SysDateMgr.getTheLastTime());
        staffData.put("AREA_CODE", getVisit().getCityCode());
        staffData.put("con_STAFF_ID", getVisit().getStaffId());
        staffData.put("cond_STAFF_ID", getVisit().getStaffId());
        setStaffData(staffData);
    }

    /**
     * @Description:查询客户经理权限信息列表
     * @author sungq3
     * @date 2014-05-15
     * @param cycle
     * @throws Exception
     */
    public void qryManagerRightsList(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String custManagerId = param.getString("cond_STAFF_ID");
        String userProdCode = param.getString("cond_USER_PRODUCT_CODE");
        IData inparam = new DataMap();
        inparam.put("STAFF_ID", custManagerId);
        inparam.put("USER_PRODUCT_CODE", userProdCode);
        IDataOutput mgrListOutput = CSViewCall.callPage(this, "CS.CustManagerInfoQrySVC.qryVpmnStaffList", inparam, getPagination("pageNav"));
        IDataset managerRights = mgrListOutput.getData();
        if (IDataUtil.isEmpty(managerRights))
        {
            setHintInfo("没有查询到VPMN客户经理信息~~!");
        }
        else
        {
            if (StringUtils.isNotEmpty(custManagerId))
            {
                IDataset staffDataset = CSViewCall.call(this, "CS.StaffInfoQrySVC.qryStaffInfoByStaffId4Vpmn", inparam);
                if (IDataUtil.isEmpty(staffDataset))
                {
                    CSViewException.apperr(GrpException.CRM_GRP_772);
                }
                String link_phone = staffDataset.getData(0).getString("SERIAL_NUMBER");
                String area_code = staffDataset.getData(0).getString("CITY_CODE");
                for (int i = 0, size = managerRights.size(); i < size; i++)
                {
                    IData managerRight = managerRights.getData(i);
                    managerRight.put("LINK_PHONE", link_phone);
                    managerRight.put("AREA_CODE", area_code);
                }
            }
            else
            {
                for (int i = 0, sizeI = managerRights.size(); i < sizeI; i++)
                {
                    inparam.clear();
                    inparam.put("STAFF_ID", managerRights.getData(i).getString("STAFF_ID"));
                    IDataset staffDataset = CSViewCall.call(this, "CS.StaffInfoQrySVC.qryStaffInfoByStaffId4Vpmn", inparam);
                    if (IDataUtil.isEmpty(staffDataset))
                    {
                        CSViewException.apperr(GrpException.CRM_GRP_772);
                    }
                    String link_phone = staffDataset.getData(0).getString("SERIAL_NUMBER");
                    String area_code = staffDataset.getData(0).getString("CITY_CODE");
                    String staff_id = staffDataset.getData(0).getString("STAFF_ID");
                    for (int j = 0, size = managerRights.size(); j < size; j++)
                    {
                        IData managerRight = managerRights.getData(j);
                        if (managerRight.getString("STAFF_ID").equals(staff_id))
                        {
                            managerRight.put("LINK_PHONE", link_phone);
                            managerRight.put("AREA_CODE", area_code);
                        }
                    }
                }
            }
            setStaffId(custManagerId);
            setInfos(managerRights);
            setCondition(param);
            setPageCounts(mgrListOutput.getDataCount());
            // 记录客户经理操作日志
            insOperLog("VPMN客户经理权限查询", "QRY", "输入参数为:" + inparam, custManagerId, getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
        }
    }

    /**
     * @Description:链接时数据的传递
     * @author sungq3
     * @date 2014-05-15
     * @param cycle
     * @throws Exception
     */
    public void redirectToEdit(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        setStaffId(param.getString("con_STAFF_ID"));
        param.put("AREA_CODE", getVisit().getCityCode());
        setStaffData(param);
    }

    /**
     * @Description:VPMN客户经理分配
     * @author sungq3
     * @date 2014-05-15
     * @param cycle
     * @throws Exception
     */
    public void doDispatch(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        // String curStaffId = param.getString("STAFF_ID");
        String conStaffId = param.getString("staffId");// staffId
        String userProdCode = param.getString("POP_USER_PRODUCT_CODE");
        String rightCode = param.getString("con_RIGHT_CODE");
        String startDate = param.getString("con_START_DATE");
        String endDate = param.getString("con_END_DATE");
        String remark = param.getString("con_REMARK");
        String areaCode = param.getString("con_AREA_CODE");
        String cityCode = getVisit().getCityCode();
        if (cityCode.equals(areaCode) || "HNSJ".equals(cityCode) || StringUtils.isEmpty(cityCode))
        { // 不是海南省局的工号，要进行业务区判断
            IData inparam = new DataMap();
            inparam.put("STAFF_ID", conStaffId);
            inparam.put("USER_PRODUCT_CODE", userProdCode);
            inparam.put("RIGHT_CODE", rightCode);
            IDataset rights = CSViewCall.call(this, "CS.CustManagerInfoQrySVC.checkVpmnRight", inparam);
            if (rights.size() > 0)
            {
                // 该客户经理已经存在该权限，不能重复分配！
                CSViewException.apperr(VpmnUserException.VPMN_USER_125);
            }
            else
            {
                inparam.put("START_DATE", startDate.substring(0, 10));
                // 当天是否进行过权限分配
                IDataset right = CSViewCall.call(this, "CS.CustManagerInfoQrySVC.checkVpmnRightAll", inparam);
                IDataset dsBool = new DatasetList("[false]");
                if (right.size() > 0)
                { // 变更
                    IData rightData = right.getData(0);
                    rightData.put("START_DATE", startDate);
                    rightData.put("END_DATE", endDate);
                    rightData.put("REMARK", remark);
                    rightData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    rightData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                    rightData.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    dsBool = CSViewCall.call(this, "CS.CustManagerInfoQrySVC.updVpnRight", rightData);
                }
                else
                { // 新增
                    inparam.put("START_DATE", startDate);
                    inparam.put("END_DATE", endDate);
                    inparam.put("REMARK", remark);
                    inparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    inparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                    inparam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    dsBool = CSViewCall.call(this, "CS.CustManagerInfoQrySVC.insertVpnRight", inparam);

                }
                if (!(Boolean) dsBool.get(0))
                {
                    // 新增员工集团权限表出错
                    CSViewException.apperr(VpmnUserException.VPMN_USER_126);
                }
            }
            // 记录客户经理操作日志
            insOperLog("VPMN客户经理权限新增", "INS", "输入参数为:" + inparam, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());

            IData staffData = new DataMap();
            staffData.put("START_DATE", SysDateMgr.getSysDate() + SysDateMgr.START_DATE_FOREVER);
            staffData.put("END_DATE", SysDateMgr.getTheLastTime());
            staffData.put("AREA_CODE", getVisit().getCityCode());
            setStaffData(staffData);
        }
        else
        {
            // 您无权限操作别的业务区权限！
            CSViewException.apperr(VpmnUserException.VPMN_USER_127);
        }
    }

    /**
     * @Description:删除客户经理VPMN权限
     * @author sungq3
     * @date 2014-05-15
     * @param cycle
     * @throws Exception
     */
    public void delStaffVpmnRight(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String staffId = param.getString("staffId");
        String userProdCode = param.getString("POP_USER_PRODUCT_CODE");
        String rightCode = param.getString("con_RIGHT_CODE");
        String startDate = param.getString("con_START_DATE");
        String areaCode = param.getString("con_AREA_CODE");
        String cityCode = getVisit().getCityCode();
        if (cityCode.equals(areaCode) || "HNSJ".equals(cityCode) || StringUtils.isEmpty(cityCode))
        { // 不是海南省局的工号，要进行业务区判断
            IData inparam = new DataMap();
            inparam.put("STAFF_ID", staffId);
            inparam.put("USER_PRODUCT_CODE", userProdCode);
            inparam.put("RIGHT_CODE", rightCode);
            inparam.put("START_DATE", startDate.substring(0, 10));
            // 当天是否进行过权限分配
            IDataset rightDataset = CSViewCall.call(this, "CS.CustManagerInfoQrySVC.checkVpmnRightAll", inparam);
            if (IDataUtil.isEmpty(rightDataset))
            {
                CSViewException.apperr(VpmnUserException.VPMN_USER_137);
            }
            IData rightData = rightDataset.getData(0);
            rightData.put("START_DATE", startDate);
            rightData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            rightData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            rightData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            rightData.put("END_DATE", SysDateMgr.getSysTime());
            IDataset dsBool = CSViewCall.call(this, "CS.CustManagerInfoQrySVC.updVpnRight", rightData);
            if ((Boolean) dsBool.get(0))
            {
                // 记录客户经理操作日志
                insOperLog("VPMN客户经理权限删除", "DEL", "输入参数为:" + inparam, getVisit().getStaffId(), getVisit().getDepartId(), getVisit().getCityCode(), getVisit().getRemoteAddr());
                
                IData staffData = new DataMap();
                staffData.put("START_DATE", SysDateMgr.getSysDate() + SysDateMgr.START_DATE_FOREVER);
                staffData.put("END_DATE", SysDateMgr.getTheLastTime());
                staffData.put("AREA_CODE", getVisit().getCityCode());
                setStaffData(staffData);
            }
            else
            {
                // 删除失败
                CSViewException.apperr(VpmnUserException.VPMN_USER_128);
            }
        }
        else
        {
            // 您无权限操作别的业务区权限！
            CSViewException.apperr(VpmnUserException.VPMN_USER_127);
        }
    }

    private void insOperLog(String oper_mod, String oper_type, String oper_desc, String staff_id, String depart_id, String city_id, String ip_addr) throws Exception
    {
        IData logData = new DataMap();
        logData.put("OPER_MOD", oper_mod);
        logData.put("OPER_TYPE", oper_type);
        logData.put("OPER_DESC", oper_desc);
        logData.put("STAFF_ID", staff_id);
        logData.put("DEPART_ID", depart_id);
        logData.put("CITY_ID", city_id);
        logData.put("IP_ADDR", ip_addr);
        // CSViewCall.call(this, "CS.CustManagerInfoQrySVC.insertOperLog", logData);
    }
}
