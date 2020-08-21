
package com.asiainfo.veris.crm.order.web.group.custmanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public abstract class CustManagerTJNum extends CSBasePage
{
    /**
     * 执行订单信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void addCustMgrTJNum(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        IData param = new DataMap();
        String tjNumber = cond.getString("cond_TJNUMBER");
        String stdffId = cond.getString("cond_MANAGER_STAFF_ID");

        // 验证客户经理
        param.put("MANAGER_STAFF_ID", stdffId);
        IDataset managerInfos = CSViewCall.call(this, "SS.CustManagerTJNumSVC.checkCustManagerStaff", param);

        String staffName = managerInfos.getData(0).getString("MANGER_NAME");
        param.put("MANGER_NAME", staffName);
        param.put("TJNUMBER", tjNumber);

        // 验证号码是否为某一集团成员
        IDataset chekData = CSViewCall.call(this, "SS.CustManagerTJNumSVC.checkGbmBySerialNumber", param);
        if (IDataUtil.isEmpty(chekData))
        {
            CSViewException.apperr(CustException.CRM_CUST_999, tjNumber);
        }

        // 当月同一号码系统限制只能录入2次机会
        IDataset ContInfo = CSViewCall.call(this, "SS.CustManagerTJNumSVC.checkSerialNumberCont", param);

        int size = ContInfo.size();
        if (size >= 2)
        {
            CSViewException.apperr(CustException.CRM_CUST_1000, tjNumber);
        }

        IDataset productinfos = CSViewCall.call(this, "SS.CustManagerTJNumSVC.queryProductsByType", param);
        setActivities(productinfos);

        String active = getData().getString("ACTIVE_ID");
        String str[]=active.split("\\|");
        String productId = str[0];
        param.put("ACTIVE_ID", productId);
        if ("00000000".equals(productId))
        {
            param.put("ACTIVE_NAME", "常态终端销售");
        }
        else
        {
            param.put("ACTIVE_NAME", str[1]);
        }
        param.put("REMARK", getData().getString("REMARK"));

        IDataset comfiDataset = CSViewCall.call(this, "SS.CustManagerTJNumSVC.insertCustManagerTJNum", param);
        if (IDataUtil.isNotEmpty(comfiDataset))
        {
            IData ajax = new DataMap();
            ajax.put("X_RESULTINFO", "新增成功");
            setAjax(ajax);
        }
    }

    /**
     * 初始化界面信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        String manageStaffId = condData.getString("MANAGER_STAFF_ID");

        IData param = new DataMap();
        IDataset productinfos = CSViewCall.call(this, "SS.CustManagerTJNumSVC.queryProductsByType", param);
        for(int i=0;i<productinfos.size();i++){
        	IData productinfo = productinfos.getData(i);
        	String productId = productinfo.getString("PRODUCT_ID")+"|"+productinfo.getString("PRODUCT_NAME");
        	productinfo.put("PRODUCT_ID",productId);
        }

        setActivities(productinfos);
        param.put("MANAGER_STAFF_ID", manageStaffId);
        param.put("IN_DATE_START", SysDateMgr.getSysDate());
        param.put("IN_DATE_END", SysDateMgr.getTomorrowDate());
        setCondition(param);
    }

    /**
     * 查询客户经理信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustManagerStaff(IRequestCycle cycle) throws Exception
    {
        // 判断逻辑
        String manageStaffId = getData().getString("cond_MANAGER_STAFF_ID");
        IData param = new DataMap();
        param.put("MANAGER_STAFF_ID", manageStaffId);

        IDataset dataset = CSViewCall.call(this, "SS.CustManagerTJNumSVC.checkCustManagerStaff", param);
        IData managerInfos = new DataMap();
        managerInfos.put("MANAGER_STAFF_ID", manageStaffId);
        managerInfos.put("MANGER_NAME", dataset.getData(0).getString("MANGER_NAME"));
        IDataset productinfos = CSViewCall.call(this, "SS.CustManagerTJNumSVC.queryProductsByType", param);

        setActivities(productinfos);
        setCondition(managerInfos);
    }

    /**
     * 执行查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustMgrTJNums(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);
        String mgrStraffId = condData.getString("MANAGER_STAFF_ID");
        String startData = condData.getString("IN_DATE_START");
        String endData = condData.getString("IN_DATE_END");
        String activcId = condData.getString("ACTIVE_ID");
        String tjNunber = condData.getString("TJNUMBER");

        IData param = new DataMap();
        param.put("MANAGER_STAFF_ID", mgrStraffId);
        param.put("IN_DATE_START", startData);
        param.put("IN_DATE_END", endData);
        param.put("ACTIVE_ID", activcId);
        param.put("TJNUMBER", tjNunber);
        setCondition(param);
        IDataset productinfos = CSViewCall.call(this, "SS.CustManagerTJNumSVC.queryProductsByType", param);
        setActivities(productinfos);
        IDataset result = CSViewCall.call(this, "SS.CustManagerTJNumSVC.queryCustManagerTjNums", param);

        param.put("EXPORT", "false");
        if (result.size() > 0)
        {
            param.put("EXPORT", "true");
        }
        setInfos(result);
    }

    public void reset(IRequestCycle cycle) throws Exception
    {
        setCondition(new DataMap());
        setCustMgrData(new DataMap());
        setActivities(new DatasetList());
        setInfos(new DatasetList());
    }

    public abstract void setActivities(IDataset activities);

    public abstract void setCondition(IData condition);

    public abstract void setCustMgrData(IData custMgrData);

    public abstract void setInfos(IDataset infos);

}
