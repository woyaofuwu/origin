
package com.asiainfo.veris.crm.order.web.person.changeuserinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 用户数据修改前台控制处理
 * 
 * @author liutt
 */
public abstract class ModifyUserInfo extends PersonBasePage
{
    /**
     * 根据证件类型和证件号码判断担保人是否为黑名单客户
     * 
     * @param clcle
     * @throws Exception
     */
    public void isBlackUser(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList();
        IData info = new DataMap();

        IData param = new DataMap();
        String assure_name = "";
        String count = "";
        param.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        param.put("PSPT_TYPE_CODE", data.getString("ASSURE_PSPT_TYPE_CODE"));
        param.put("PSPT_ID", data.getString("ASSURE_PSPT_ID"));

        IDataset blackInfo = CSViewCall.call(this, "CS.CustBlackInfoQrySVC.qryBlackCustInfo", param);// 判断是否是黑名单客户

        if (IDataUtil.isEmpty(blackInfo))
        {
            info.put("IS_BLACK_USER", "false");

            param.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

            IDataset modifyDataset = CSViewCall.call(this, "CS.CustomerInfoQrySVC.getCustInfoByPspt", param);// 根据证件号码和证件类型获取客户信息

            if (modifyDataset != null && modifyDataset.size() == 1)
            {
                assure_name = modifyDataset.getData(0).getString("CUST_NAME", "");
                count = "1";
            }
            else if (modifyDataset != null && modifyDataset.size() > 1)
            {
                assure_name = "";
                count = "" + modifyDataset.size();
            }
            else
            {
                assure_name = "";
                count = "0";
            }

        }
        else
        {
            info.put("IS_BLACK_USER", "true");
            CSViewException.apperr(CrmUserException.CRM_USER_193);
        }

        info.put("ASSURE_NAME", assure_name);
        info.put("PSPT_COUNT", count);

        dataset.add(info);
        setAjax(dataset);
    }

    /**
     * 查询完后，获取业务个性数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        IData editInfo = new DataMap();
        editInfo.put("USER_TYPE_CODE", userInfo.getString("USER_TYPE_CODE"));

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));

        // 获取担保人资料
        if ("".equals(userInfo.getString("ASSURE_CUST_ID", "")) || "0".equals(userInfo.getString("ASSURE_CUST_ID", "")))
        {
            editInfo.put("ASSURE_CUST_NAME", "");
            editInfo.put("ASSURE_PSPT_TYPE_CODE", "");
            editInfo.put("ASSURE_PSPT_ID", "");
            this.setAjax("FLAG", "true");
        }
        else
        {
            param.put("CUST_ID", userInfo.getString("ASSURE_CUST_ID"));
            param.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));         
            IDataset assureCustDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryCustInfoByCustId", param);         
            if (assureCustDataset.isEmpty())
            {
                CSViewException.apperr(CustException.CRM_CUST_144);
            }

            editInfo.put("ASSURE_CUST_NAME", assureCustDataset.get(0, "CUST_NAME"));
            editInfo.put("ASSURE_PSPT_TYPE_CODE", assureCustDataset.get(0, "PSPT_TYPE_CODE"));
            editInfo.put("ASSURE_PSPT_ID", assureCustDataset.get(0, "PSPT_ID"));

        }
        editInfo.put("ASSURE_TYPE_CODE", userInfo.getString("ASSURE_TYPE_CODE", ""));
        editInfo.put("ASSURE_DATE", userInfo.getString("ASSURE_DATE", ""));

        // 获取使用者资料
        param.put("CUST_ID", userInfo.getString("USECUST_ID"));
        param.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
        IDataset userCustDataset = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryCustInfoByCustId", param);

        if (userCustDataset.isEmpty())
        {
            CSViewException.apperr(CustException.CRM_CUST_98, userInfo.getString("SERIAL_NUMBER"));// 用户【%s】的客户信息不存在，业务不能继续！
        }

        IData userCustData = userCustDataset.getData(0);

        // 获取使用者客户档案资料
        IDataset usecustPersonSet = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryPerInfoByCustId", param);

        if (usecustPersonSet.isEmpty())
        {
            CSViewException.apperr(CustException.CRM_CUST_75);// 获取总机客户信息无数据！
        }

        IData useCustPersondata = usecustPersonSet.getData(0);
        userCustData.putAll(useCustPersondata);

        // 设置页面数据
        editInfo.put("USE_CUST_NAME", userCustData.getString("CUST_NAME", ""));
        editInfo.put("USE_PSPT_ID", userCustData.getString("PSPT_ID", ""));
        editInfo.put("USE_HOME_ADDRESS", userCustData.getString("HOME_ADDRESS", ""));
        editInfo.put("USE_PSPT_ADDR", userCustData.getString("PSPT_ADDR", ""));

        editInfo.put("USE_PSPT_TYPE_CODE", userCustData.getString("PSPT_TYPE_CODE", ""));
        editInfo.put("USE_PSPT_END_DATE", userCustData.getString("PSPT_END_DATE", ""));
        editInfo.put("USE_SEX", userCustData.getString("SEX", ""));

        editInfo.put("DEVELOP_DEPART_ID", userInfo.getString("DEVELOP_DEPART_ID", ""));
        editInfo.put("DEVELOP_STAFF_ID", userInfo.getString("DEVELOP_STAFF_ID", ""));
        editInfo.put("DEVELOP_NO", userInfo.getString("DEVELOP_NO", ""));

        setEditInfo(editInfo);

        this.writeLanuchLog(userInfo);
    }

    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.ModifyUserInfoRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setEditInfo(IData editInfo);

    /**
     * 记录特殊权限工号查询实名制用户资料
     * 
     * @param allInfo
     * @throws Exception
     */
    public void writeLanuchLog(IData userInfo) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userInfo.getString("USER_ID"));
        params.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        params.put("REMARK", "用户资料变更界面：记录员工查询用户资料！");
        CSViewCall.call(this, "SS.ModifyCustInfoSVC.writeLanuchLog", params);
    }

}
