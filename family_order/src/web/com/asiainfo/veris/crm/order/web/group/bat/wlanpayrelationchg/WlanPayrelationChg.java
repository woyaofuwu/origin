
package com.asiainfo.veris.crm.order.web.group.bat.wlanpayrelationchg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class WlanPayrelationChg extends GroupBasePage
{

    private IDataset getNowAndNextCycle() throws Throwable
    {
        IDataset startCycleList = new DatasetList();
        // 获取本账期生效和下账期生效
        String now_bcyc_id = SysDateMgr.getNowCyc();
        String next_bcyc_id = SysDateMgr.getNextCycle();

        IData nowCycle = new DataMap();
        nowCycle.put("BCYC_ID", now_bcyc_id);
        nowCycle.put("BCYC_NAME", "本账期生效");
        startCycleList.add(nowCycle);

        IData nextCycle = new DataMap();
        nextCycle.put("BCYC_ID", next_bcyc_id);
        nextCycle.put("BCYC_NAME", "下账期生效");
        startCycleList.add(nextCycle);

        return startCycleList;
    }

    /**
     * 初始化批量弹出窗口页面
     * 
     * @param cycle
     * @throws Throwable
     */
    public void initial(IRequestCycle cycle) throws Throwable
    {
        String batchOperType = getData().getString("BATCH_OPER_TYPE", "NOXXXX");
        IDataset startCycleList = getNowAndNextCycle();
        setMyStartBcyc(startCycleList);

        IData initData = getData();
        initData.put("DATA4", SysDateMgr.getEndCycle205012());// 默认结束帐期为
        initData.put("FEE_TYPE", "1");// 默认费用类别为"部分费用"
        initData.put("DATA6", "1");// 默认限定方式为"金额"
        initData.put("DATA7", "0");// 默认限定值为"0"
        initData.put("CURRENT_CYCLE", SysDateMgr.getSysDate("yyyyMM"));
    	//add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "839");
        iparam.put("PARAM_CODE", batchOperType);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
        	initData.put("MEB_VOUCHER_FILE_SHOW","false");
        }else{
        	initData.put("MEB_VOUCHER_FILE_SHOW","true");
        }
        initData.put("BATCH_OPER_TYPE", batchOperType);
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----

        setInfo(initData);

        String allow_brank_code = initData.getString("ALLOW_BRANK_CODE", "");

        IDataset payItems = new DatasetList();

        if ("WLAN".equals(allow_brank_code))
        {
            payItems = StaticUtil.getStaticList("GROUP_BAT_WLANPAYRELA");
        }
        else if ("VGPO".equals(allow_brank_code))
        {
            payItems = StaticUtil.getStaticList("GROUP_GPON_BAT_PAYPARAMCODE");
        }
        else if ("VPMN".equals(allow_brank_code))
        {
            payItems = StaticUtil.getStaticList("GROUP_BAT_PAYPARAMCODE");
        }

        setPayItems(payItems);
    }

    /**
     * 查询付费关系信息
     * 
     * @param cycle
     * @throws Throwable
     */
    public void queryInfos(IRequestCycle cycle) throws Throwable
    {
        String serialNumber = getParameter("SERIAL_NUMBER", "");
        String allow_brank_code = getParameter("ALLOW_BRANK_CODE", "");

        // VPMN统一付费关系 需要判断权限
        if ("VPMN".equals(allow_brank_code))
        {
            if ("V0HN001010".equals(serialNumber) || "V0SJ004001".equals(serialNumber))
            {
                IData paramData = new DataMap();
                paramData.put("STAFF_ID", getVisit().getStaffId());
                paramData.put("RIGHT_CODE", "CREATE_HAINMOBILE_MEB_RIGHT");
                IDataset rightDataset = CSViewCall.call(this, "CS.StaffInfoQrySVC.queryGrpRightByIdCode", paramData);

                if (IDataUtil.isEmpty(rightDataset))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_1137);
                }
            }
        }

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("REMOVE_TAG", "0");
        inparam.put("NET_TYPE_CODE", "00");

        // 根据serial_number,remove_tag值取得cust_id,user_id值
        IData iData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber);

        if (!"00".equals(iData.getString("NET_TYPE_CODE")))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_573, serialNumber); // 没有找到该付费号码的用户资料,请确认!
        }

        String brandCode = iData.getString("BRAND_CODE");
        String productId = iData.getString("PRODUCT_ID");

        if ("WLAN".equals(allow_brank_code))
        {

            if (!"WLAN".equals(brandCode))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_145); // 该付费号码不是集团WLAN产品编码!
            }
        }
        else if ("VGPO".equals(allow_brank_code))
        {

            if (!"VGPO".equals(brandCode) && !"7140".equals(productId))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_1065); // 该付费号码不是集团VGPO产品编码!
            }
        }
        else if ("VPMN".equals(allow_brank_code))
        {

            if (!"VPMN".equals(brandCode) && !"7029".equals(productId))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_1066); // 该付费号码不属于VPMN集团或农信通!
            }
        }

        IData custparam = new DataMap();
        custparam.put("CUST_ID", iData.getString("CUST_ID", ""));
        custparam.put("USER_ID", iData.getString("USER_ID", ""));
        custparam.put("REMOVE_TAG", "0");

        IData custInfo = UCAInfoIntfViewUtil.qryGrpCustomerInfoByCustId(this, iData.getString("CUST_ID"));

        // 从付费关系表中根据USER_ID获得ACCT_ID

        IData payrela = UCAInfoIntfViewUtil.qryGrpDefaultPayRelaInfoByUserId(this, iData.getString("USER_ID"));// AcctInfoQry.getDefaultAcctInfoByUserId(pd,
        // inparam);

        custparam.put("ACCT_ID", payrela.getString("ACCT_ID", ""));

        // 根据ACCT_ID从帐户表中得到PAY_MODE_CODE,PAY_NAME
        inparam.clear();
        inparam.put("ACCT_ID", payrela.getString("ACCT_ID", ""));

        IData acctinfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, payrela.getString("ACCT_ID"));

        // 把值设置到页面
        IData infoData = new DataMap();
        infoData.put("MEM_CUST_NAME", custInfo.getString("CUST_NAME", ""));
        infoData.put("PAY_NAME", acctinfo.getString("PAY_NAME", ""));
        infoData.put("PAY_MODE_CODE", acctinfo.getString("PAY_MODE_CODE", ""));
        infoData.put("BANK_CODE", acctinfo.getString("BANK_CODE", ""));

        // 取当前帐期
        String currentCycle = SysDateMgr.getSysDate("yyyyMM");
        infoData.put("CURRENT_CYCLE", currentCycle);
        infoData.put("ACCT_ID", custparam.getString("ACCT_ID", ""));
        infoData.put("USER_ID", custparam.getString("USER_ID", ""));
        infoData.put("CUST_ID", custparam.getString("CUST_ID", ""));
        infoData.put("SERIAL_NUMBER2", inparam.getString("SERIAL_NUMBER", ""));
        infoData.put("ALLOW_BRANK_CODE", allow_brank_code);	//add by chenzg@20180711

        setInfo(infoData);

        IDataset startCycleList = getNowAndNextCycle();
        setMyStartBcyc(startCycleList);

    }

    public abstract void setInfo(IData data);

    public abstract void setMyStartBcyc(IDataset dataset);

    public abstract void setPayItems(IDataset dataset);
}
