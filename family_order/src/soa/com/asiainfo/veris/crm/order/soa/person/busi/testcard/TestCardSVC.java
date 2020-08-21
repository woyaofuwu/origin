
package com.asiainfo.veris.crm.order.soa.person.busi.testcard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.log.LogBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class TestCardSVC extends CSBizService
{

    private static final long serialVersionUID = -1756769134328846009L;

    public void baseTradeCheck(IData data, IData custInfo) throws Exception
    {
        if ("1".equals(data.getString("IS_IN_PURCHASE", "")) && !data.getString("CUST_NAME", "").equals(custInfo.getString("CUST_NAME")))
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "802001:用户处在购机期间，不能修改用户名称！");
    }

    private void checkBefore(IData data) throws Exception
    {
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "802002:输入的参数中没有用户号码");
        }
        if ("".equals(data.getString("CUST_NAME", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "802003:输入的参数中没有客户名称");
        }
    }

    public void modifyCustInfo(IData data) throws Exception
    {
        TestCardBean bean = (TestCardBean) BeanManager.createBean(TestCardBean.class);
        String serialNumber = data.getString("SERIAL_NUMBER");
        checkBefore(data);
        IDataset custIds = UserInfoQry.getTestCardCustId(serialNumber);
        if (IDataUtil.isNotEmpty(custIds) && IDataUtil.isNotEmpty(custIds.getData(0)))
        {
            data.putAll(custIds.getData(0));
            bean.updateCustName(data);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该服务号码对应的客户资料信息");
        }
    }

    public IDataset testCardDeal(IData data) throws Exception
    {

        IData logData = new DataMap();
        IData result = new DataMap();
        result.put("ORDER_ID", SeqMgr.getOrderId());

        IDataset returnData = new DatasetList();

        String SERIAL_NUMBER = data.getString("SERIAL_NUMBER", "");
        if (StringUtils.isEmpty(SERIAL_NUMBER))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "服务号码不能为空！");
        }

        String CUST_NAME = data.getString("DATA1", "");
        String END_DATE = data.getString("DATA2", "");
        String isStop = "";
        String batchOperType = data.getString("BATCH_OPER_TYPE");
        if ("STOPTESTCARD".equals(batchOperType))
        {
            isStop = "1";
        }
        String UPDATE_STAFF_ID = data.getString("TRADE_STAFF_ID", "SUPERUSR");
        String UPDATE_DEPART_ID = data.getString("TRADE_DEPART_ID", "36601");

        // 只有测试卡用户可以办理
        IData user = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER);
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该服务号码对应的用户资料信息");
        }
        String city_code = user.getString("CITY_CODE");
        if (!"HNSJ".equals(city_code) && !"HNHN".equals(city_code))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "非测试卡用户！");
        }

        if (StringUtils.isNotEmpty(CUST_NAME))
        {

            data.put("CUST_NAME", CUST_NAME);
            data.put("SERIAL_NUMBER", SERIAL_NUMBER);
            data.put("HOME_ADDRESS", "hn");
            data.put("PHONE", SERIAL_NUMBER);

            data.put("WORK_NAME", "a");
            data.put("EMAIL", "xx@xx.com");
            data.put("PSPT_ADDR", "xxx");

            modifyCustInfo(data);

            logData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            logData.put("OPER_CODE", "4");
            logData.put("OPER_DESC", "更新测试号码客户姓名");
            logData.put("OPER_MOD", "更新测试号码客户姓名");
            logData.put("OPER_TYPE", "UPD");
            LogBean.insertOperLog(logData);
        }

        if (StringUtils.isNotEmpty(END_DATE))
        {
            TestCardBean bean = (TestCardBean) BeanManager.createBean(TestCardBean.class);
            data.put("END_DATE", END_DATE);
            bean.updateTestCardEndDate(data);
            logData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            logData.put("OPER_CODE", "4");
            logData.put("OPER_DESC", "更新测试号码有效期");
            logData.put("OPER_MOD", "更新测试号码有效期");
            logData.put("OPER_TYPE", "UPD");
            LogBean.insertOperLog(logData);
        }

        // 判断是否需要办理局方停机

        if ("1".equals(isStop))
        {

            data.put("SERIAL_NUMBER", SERIAL_NUMBER);
            IData param = new DataMap();
            param.putAll(data);
            param.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE, "0898"));
            param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            param.put("TRADE_STAFF_ID", UPDATE_STAFF_ID);
            param.put("TRADE_DEPART_ID", UPDATE_DEPART_ID);
            param.put("TRADE_CITY_CODE", data.getString("CITY_CODE", "0898"));
            param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", "0"));

            try
            {
                param.put("TRADE_TYPE_CODE", "136");
                param.put("ORDER_TYPE_CODE", "136");
                returnData = CSAppCall.call("SS.ChangeSvcStateIntfSVC.createOfficeStopOpenReg", param);

                logData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                logData.put("OPER_CODE", "6");
                logData.put("OPER_DESC", "测试号码局方停机");
                logData.put("OPER_MOD", "测试号码局方停机");
                logData.put("OPER_TYPE", "UPD");
                LogBean.insertOperLog(logData);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        if (IDataUtil.isEmpty(returnData))
        {
            returnData.add(result);
        }
        return returnData;
    }

}
