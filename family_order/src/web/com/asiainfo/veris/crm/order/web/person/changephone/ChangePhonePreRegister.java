
package com.asiainfo.veris.crm.order.web.person.changephone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ChangePhonePreRegister extends PersonBasePage
{

    public void checkBeforeTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        IDataset dataset = CSViewCall.call(this, "SS.ChangePhonePreRegisterSVC.checkSerialOldNew", data);

        setAjax(dataset);
    }

    private void dealMoveInfo(IData data)
    {
        // TODO Auto-generated method stub
        String spInfo = data.getString("custInfo_SEL_BUSI_IMPS", "") + "," + data.getString("custInfo_SEL_BUSI_M139", "");
        String[] strParam = spInfo.split(",");

        IDataset item = new DatasetList();

        for (int i = 0; i < strParam.length; i++)
        {
            IData itemsa = new DataMap();
            String str = strParam[i];
            if (!"".equals(str))
            {
                itemsa.put("BIZ_INFO", str);
                itemsa.put("MOVED", "0");
            }
            item.add(itemsa);
        }

        data.put("MOVE_INFO", item);
    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        setUserInfo(userInfo);
        setCustInfo(custInfo);
        setAjax(userInfo);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        dealMoveInfo(data);

        IDataset checkdata = CSViewCall.call(this, "SS.ChangePhonePreRegisterSVC.checkSerialOldNew", data);

        data.put("SYNC_INFO", (IData) checkdata.getData(0).get("SYNC_INFO"));
        data.put("CHANNEL", "08"); // 1 -> 08 2013-06-27 01-WEB，02-网上营业厅，03-WAP，04-SMS，07-10086语音，08-营业厅
        data.put("RSRV_STR6", "BEGIN");
        data.put("REMARK", "BEGIN");
        //人像比对受理单编号与照片编号同步接口调用
        IDataset dataset = CSViewCall.call(this, "SS.ChangePhonePreRegisterRegSVC.tradeReg", data);
        data.put("TRADE_ID", dataset.getData(0).getString("TRADE_ID"));
        CSViewCall.call(this, "SS.ChangePhonePreRegisterSVC.SynPicId", data);
        setAjax(dataset);

    }
    
    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }
    
    /**
     * 是否免人像比对和身份证可手动输入权限
     * 
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void kqbkDataRight(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.kqbkDataRight", param);
        setAjax(dataset.getData(0));
    }
    
    public abstract void setCustInfo(IData custInfo);

    public abstract void setUserInfo(IData userInfo);
    
}
