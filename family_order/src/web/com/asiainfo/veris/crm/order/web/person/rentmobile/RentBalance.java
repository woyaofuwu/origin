
package com.asiainfo.veris.crm.order.web.person.rentmobile;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RentBalance extends PersonBasePage
{

    /**
     * 认证加载用户信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String userId = data.getString("USER_ID");
        String serialNumber = data.getString("SERIAL_NUMBER");
        IData info = new DataMap();

        /** 获取资源信息 */
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset resInfo = CSViewCall.call(this, "SS.RentMobileSVC.getUserResInfo", param);
        if (IDataUtil.isNotEmpty(resInfo))
        {
            info.put("SIM_CARD_NO", resInfo.getData(0).getString("RES_CODE"));
            info.put("IMSI_NO", resInfo.getData(0).getString("IMSI"));
        }

        /** 查询用户租机信息 */
        IDataset rentInfo = CSViewCall.call(this, "SS.RentMobileSVC.getRentMobile", param);
        if (IDataUtil.isNotEmpty(rentInfo))
        {
            info.put("RENT_SERIAL_NUMBER", rentInfo.getData(0).getString("RENT_SERIAL_NUMBER"));// 租机号码
            info.put("RENT_START_DATE", rentInfo.getData(0).getString("RSRV_DATE"));// 结算时间
            info.put("RENT_TYPE_NAME", rentInfo.getData(0).getString("RENT_TYPE_NAME"));
            info.put("RENT_MODE_CODE", rentInfo.getData(0).getString("RENT_MODE_CODE"));
        }

        /** 获取押金 */
        IDataset foregiftInfo = CSViewCall.call(this, "SS.RentMobileSVC.queryForegiftInfo", param);
        if (IDataUtil.isNotEmpty(foregiftInfo))
        {
            int money = foregiftInfo.getData(0).getInt("MONEY", 0);
            info.put("MONEY", money / 100);
        }

        setInfo(info);
    }

    /**
     * 初始化界面
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        cond.put("PARAM_ATTR", "743");
        cond.put("SUBSYS_CODE", "CSM");
        cond.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        IDataset results = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommparaInfoByAttr", cond);
        setRentTypeCodes(results);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData();
        IDataset result = CSViewCall.call(this, "SS.RentBalanceSVC.tradeReg", inparam);
        setAjax(result);
    }

    public abstract void setInfo(IData info);

    public abstract void setRentTypeCodes(IDataset idataset);
}
