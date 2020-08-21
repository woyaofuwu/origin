
package com.asiainfo.veris.crm.order.web.person.score.scoreconvert;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ScoreConvert extends PersonBasePage
{
    /**
     * 调用IBOSS接口撤销订单
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public void cancelConvertGiftOrder(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        data.put("ORDER_ID", getData().getString("ORDER_ID"));
        data.put("PROC_STATE", getData().getString("PROC_STATE"));
        data.put("ORDER_SUB_ID", getData().getString("ORDER_SUB_ID"));
        data.put("ORDER_SEQ", getData().getString("ORDER_SEQ"));
        String alertInfo = "";
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataset infos = CSViewCall.call(this, "SS.ScoreConvertSVC.cancelConvertGiftOrder", data);

        alertInfo = "流水号【" + data.getString("ORDER_SEQ") + "】撤销结果:" + infos.getData(0).getString("MESSAGE");

        queryConvertRecord(cycle);

        this.setAjax("RESULT_MESSAGE", alertInfo);// 传给页面提示
    }

    private void getCommInfo(IData allInfo) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", allInfo.getData("USER_INFO").getString("SERIAL_NUMBER"));
        inparam.put("USER_ID", allInfo.getData("USER_INFO").getString("USER_ID"));
        inparam.put("BRAND_CODE", allInfo.getData("USER_INFO").getString("BRAND_CODE"));
        IDataset data = CSViewCall.call(this, "SS.ScoreConvertSVC.getCommInfo", inparam);

        setCommInfo(data.getData(0));
        setProvinceList(data.getData(0).getDataset("PROVINCE_LIST"));// 省份信息
    }

    /**
     * BOSS业务 初始化页面
     * 
     * @author xiaolu
     * @param cycle
     * @throws Exception
     */
    public void initServicePage(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        IData data = new DataMap();
        // BOSS业务 接口接入数据处理
        data.put("cond_SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
        data.put("IN_MODE_CODE", param.getString("IN_MODE_CODE", ""));
        data.put("cond_ORDER_SEQ", param.getString("ORDER_SEQ", ""));

        setCondition(data);

    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData dataset = new DataMap();

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));
        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        setCustInfo(custInfo);
        setUserInfo(userInfo);

        dataset.put("USER_INFO", userInfo);

        // 获取子业务资料
        getCommInfo(dataset);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.putAll(data);
        IDataset dataset = CSViewCall.call(this, "SS.ScoreConvertRegSVC.tradeReg", param);
        setAjax(dataset);

    }

    public void queryCity(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataset infos = CSViewCall.call(this, "SS.ScoreConvertSVC.queryCity", data);

        setCityList(infos);
    }

    /**
     * IBOSS订单查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryConvertGiftOrder(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        data.put("ORDER_ID", getData().getString("ORDER_ID"));
        data.put("PROC_STATE", getData().getString("PROC_STATE"));
        data.put("ORDER_SUB_ID", getData().getString("ORDER_SUB_ID"));
        data.put("ORDER_SEQ", getData().getString("ORDER_SEQ"));
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        String alertInfo = "";
        IData resultData = new DataMap();
        // queryConvertGiftOrder
        IDataset infos = CSViewCall.call(this, "SS.ScoreConvertSVC.queryConvertGiftOrder", data);
        if (IDataUtil.isNotEmpty(data))
        {
            resultData = infos.getData(0);
        }
        String resultMessage = resultData.getString("TRADE_RES_DESC");
        String state = resultData.getString("SUB_ORDER_STATUS", "");
        if (StringUtils.isBlank(state))
        {
            state = data.getString("ORDER_STATE", "");
        }
        String stateName = this.getPageUtil().getStaticValue("UPMS_ORDER_STATE", state);
        alertInfo = "订单流水号【" + data.getString("ORDER_SEQ") + "】的记录查询结果：" + resultMessage ;
        queryConvertRecord(cycle);
        this.setAjax("RESULT_MESSAGE", alertInfo);// 传给页面提示
    }

    /**
     * 查询积分兑换记录
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryConvertRecord(IRequestCycle cycle) throws Exception
    {

        IData data = getData("cond", true);
        String alertInfo = "";
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataOutput infos = CSViewCall.callPage(this, "SS.ScoreConvertSVC.queryConvertRecord", data, getPagination("pagin"));

        if (IDataUtil.isEmpty(infos.getData()))
        {
            alertInfo = "获取兑换记录无数据!";
        }

        setInfos(infos.getData());
        setCount(infos.getDataCount());

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        IData param = getData();
        param.put("IN_MODE_CODE", getVisit().getInModeCode());
        setCondition(param);// 设置页面IN_MODE_CODE值
    }

    public void queryDistrict(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());
        IDataset infos = CSViewCall.call(this, "SS.ScoreConvertSVC.queryDistrict", data);
        setDistrictList(infos);
    }

    public void queryGiftCount(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset info = CSViewCall.call(this, "SS.ScoreConvertSVC.queryGiftCount", data);

        if (IDataUtil.isNotEmpty(info))
        {
            String tradeResCode = info.getData(0).getString("TRADE_RES_CODE", "");
            String tradeResDesc = info.getData(0).getString("TRADE_RES_DESC", "");
            if ("01".equals(tradeResCode))// 01-查询成功
            {
                int itemNum = data.getInt("ITEM_NUM");
                int num = info.getData(0).getInt("STORE_NUM");
                if (itemNum > num)// 库存数量不够
                {
                    this.setAjax("ALERT_INFO", "兑换物品数量[" + itemNum + "],库存数量[" + num + "],请重新选择!");
                }
                else
                {
                    this.setAjax("ALERT_INFO", "0");// 0 成功
                }

            }
            else
            {
                this.setAjax("ALERT_INFO", "库存查询失败! " + tradeResDesc);
            }

        }
        else
        {
            this.setAjax("ALERT_INFO", "调用IBOSS接口查询礼品库存发生异常!");
        }

    }

    /**
     * CRM礼品查询方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryGifts(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);

        String alertInfo = "";
        IDataOutput infos = CSViewCall.callPage(this, "SS.ScoreConvertSVC.queryGifts", data, getPagination("pagin"));

        if (IDataUtil.isEmpty(infos.getData()))
        {
            alertInfo = "查询无数据!";
        }

        setInfos(infos.getData());
        setCount(infos.getDataCount());

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示

    }

    public abstract void setCityList(IDataset cityList);

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCondition(IData data);

    public abstract void setCount(long count);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setDistrictList(IDataset districtList);

    public abstract void setExchangeTag(String tag);

    public abstract void setInfos(IDataset dataset);

    public abstract void setProvinceList(IDataset provinceList);

    public abstract void setTypeTag(String tag);

    public abstract void setUserInfo(IData userInfo);
}
