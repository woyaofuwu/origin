
package com.asiainfo.veris.crm.order.web.person.onecardncodes;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class OneCardNCodesChangeCardRemote extends PersonBasePage
{

    /**
     * 查询副号码信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getOtherSNInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER_O"));

        IDataset dataset = CSViewCall.call(this, "SS.OneCardNCodesChangeCardRemoteSVC.getOtherSNInfo", data);
        if(!IDataUtil.isEmpty(dataset)){
        	IData oUserInfo = dataset.getData(0).getData("OUSERINFO");
        	oUserInfo.put("STATE_NAME", UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", oUserInfo.getString("USER_STATE_CODESET","")));
        	setCondition(getData("cond"));
            setOuser_info(dataset.getData(0).getData("OUSERINFO"));
            setOcust_info(dataset.getData(0).getData("OUSERINFO"));
            setFresinfo(dataset.getData(0).getData("ORESINFO"));

            // setOuser_info(dataset.getData(0).getData("OUSERINFO"));
            // setOcust_info(dataset.getData(0).getData("OCUSTINFO"));
            // setFresinfo(dataset.getData(0).getData("OUSERRESINFO"));

            setAjax(dataset.getData(0));
        }
        

    }

    
    /**
     * 查询副号码信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void getOtherSNInfoM(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));

        IDataset dataset = CSViewCall.call(this, "SS.OneCardNCodesChangeCardRemoteSVC.getOtherSNInfo", data);
        if(!IDataUtil.isEmpty(dataset)){
        	IData oUserInfo = dataset.getData(0).getData("OUSERINFO");
        	oUserInfo.put("STATE_NAME", UpcViewCall.qryOfferFuncStaByAnyOfferIdStatus(this, "0", "S", oUserInfo.getString("USER_STATE_CODESET","")));
        	setCondition(getData("cond"));
            setUserInfo(dataset.getData(0).getData("OUSERINFO"));
            setCustInfo(dataset.getData(0).getData("OUSERINFO"));
            setResInfo(dataset.getData(0).getData("ORESINFO"));

            // setOuser_info(dataset.getData(0).getData("OUSERINFO"));
            // setOcust_info(dataset.getData(0).getData("OCUSTINFO"));
            // setFresinfo(dataset.getData(0).getData("OUSERRESINFO"));

            setAjax(dataset.getData(0));
        }
        

    }
    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        data.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));

        // 用户资料
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));
        IDataset output = CSViewCall.call(this, "SS.OneCardNCodesChangeCardRemoteSVC.getResInfo", userInfo);
        
        output.getData(0).put("SIM_CARD_NO", output.getData(0).getString("RES_NO"));
        setCustInfo(custInfo);
        setUserInfo(userInfo);
        setResInfo(output.getData(0));
        // setOuser_info(output.getData(0).getData("OUSERINFO"));
        // setFresinfo(output.getData(0).getData("OUSERRESINFO"));
        // setOldCard(output.getData(0).getData("USERRESINFO"));
        // setOcust_info(output.getData(0).getData("OCUSTINFO"));
        // setAjax(output.getData(0));
    }

    public abstract void setCondition(IData cond);

    public abstract void setCustInfo(IData data);

    public abstract void setEditInfo(IData data);

    public abstract void setFresinfo(IData data);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset datas);

    public abstract void setOcust_info(IData data);

    public abstract void setOuser_info(IData data);

    public abstract void setResInfo(IData data);

    public abstract void setUserInfo(IData data);

    public void writeCard(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        inparam.put("ICCID", data.getString("ICCID1"));
        inparam.put("ICCID2", data.getString("ICCID2"));

        IDataOutput output = CSViewCall.callPage(this, "SS.OneCardNCodesChangeCardRemoteSVC.writeCard", inparam, getPagination("relatfoNav"));

        // setCount(output.getDataCount());
        // pageutil.getStaticValue(type_id, data_id)
        setAjax(output.getData());
        // setInfos(output.getData());
        // setEditInfo(inparam);

    }
}
