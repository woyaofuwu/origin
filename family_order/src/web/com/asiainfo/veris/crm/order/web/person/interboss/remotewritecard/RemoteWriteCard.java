
package com.asiainfo.veris.crm.order.web.person.interboss.remotewritecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RemoteWriteCard extends PersonBasePage
{

    // 获取卡费
    public void getDevicePrice(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData resultInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.getDevicePrice", data).getData(0);
        setAjax(resultInfo);
    }

    public void getSimCardInfo(IRequestCycle cycle) throws Exception
    {
        IData params = getData("cond", true);
        IData simCardInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.getSimCardInfo", params).getData(0);
        setAjax(simCardInfo);
    }

    public abstract IData getUserInfo();

    /**
     * 查询后设置页面信息
     */
    public void queryCustInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IDataset custInfos = CSViewCall.call(this, "SS.RemoteWriteCardSVC.queryCustInfo", data);
        if (IDataUtil.isEmpty(custInfos))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询用户资料（异地）为空！");
        }
        IData custInfo = custInfos.getData(0);
        /*
        if (!"00".equals(custInfo.getString("USER_STATE_CODESET")))
        {
            if ("02".equals(custInfo.getString("USER_STATE_CODESET")))
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "该用户处于停机状态，不能办理此业务！");
            }
            else
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "530000:非正常在网用户不能办理远程写卡业务！");
            }
        }*/
        IData resMPayInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.queryMPayInfo", data).getData(0);
        if ("0".equals(resMPayInfo.getString("BIZ_STATE_CODE")) || "1".equals(resMPayInfo.getString("BIZ_STATE_CODE")))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "530042:该客户已经订购了手机支付业务，请客户直接联系归属地10086，由归属地客服接手写卡服务！");
        } 
        setInfo(custInfo);
    }
    
    /**
     * REQ201501050004关于补换卡身份证件校验的需求
     * chenxy3 2015-01-21
     * 获取用户DATARIGHT权限，用以判定是否允许手动录入身份证号码
     * */
    public void getPriv(IRequestCycle cycle) throws Exception
    {
    	//
    	IData id=new DataMap();
        String staff=getVisit().getStaffId();
    	boolean highpriv = StaffPrivUtil.isFuncDataPriv(staff, "HIGH_PRIV");
        if(highpriv){
        	id.put("HIGH_PRIV", "1");
        }else{
        	id.put("HIGH_PRIV", "0");
        };
        setAjax(id);
        
    }
    
    public abstract void setCustInfo(IData editInfo);

    public abstract void setInfo(IData info);

    public abstract void setNewCard(IData a);

    public abstract void setOldCard(IData a);

    public abstract void setUserInfo(IData userInfo);

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void writeCardActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData resultInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.writeCardActive", data).getData(0);
        setAjax(resultInfo);
    }
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset resultInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.writeCardActive", data);
        setAjax(resultInfo);
    }
    // 写卡结果回传
    public void writeCardResultback(IRequestCycle cycle) throws Exception
    {
        IData params = getData("cond", true);
        IData resultInfo = CSViewCall.call(this, "SS.RemoteWriteCardSVC.writeCardResultback", params).getData(0);
        setAjax(resultInfo);
    }
    //加载打印
    public void loadPrintData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData returnData = new DataMap();
        IDataset rePrintDatas = CSViewCall.call(this, "SS.RemoteWriteCardSVC.loadPrintData", data);
        returnData.put("PRINT_DATA", rePrintDatas);
        setAjax(returnData);
    }
    
    /**
     * @Description 构建打印发票数据
     * @param cycle
     */
    public void printTrade(IRequestCycle cycle) throws Exception{
        IData inputData = this.getData();
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        inputData.put("STAFF_NAME", getVisit().getStaffName());
        inputData.put("DEPART_NAME", getVisit().getDepartName());
        // 服务返回结果集
        IDataset result = CSViewCall.call(this, "SS.RemoteWriteCardSVC.printTrade", inputData);
        // 设置页面返回数据
        setAjax(result);
    }
}
