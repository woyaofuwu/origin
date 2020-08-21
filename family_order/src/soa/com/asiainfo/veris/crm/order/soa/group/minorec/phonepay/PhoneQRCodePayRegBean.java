package com.asiainfo.veris.crm.order.soa.group.minorec.phonepay;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class PhoneQRCodePayRegBean extends GroupBean {

    private int payMoney;

    /**
     * 费用类型暂时写死为预存
     */
    private String feeMode = "2";

    /**
     * 生成登记信息
     *
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 其它台帐处理-重点
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegTradeFeeSub();

    }

    /**
     * 处理费用表
     * @throws Exception
     */
    public void infoRegTradeFeeSub() throws Exception{
        IData data = new DataMap();
        data.put("USER_ID",reqData.getUca().getUserId());
        data.put("FEE_MODE",feeMode);
        data.put("FEE_TYPE_CODE","0");
        data.put("OLDFEE",payMoney);
        data.put("FEE",payMoney);
        IDataset tradeFeeSubList = new DatasetList();
        tradeFeeSubList.add(data);

        super.addTradefeeSub(tradeFeeSubList);
    }

    @Override
    protected void makInit(IData data) throws Exception {
        super.makInit(data);

        payMoney = data.getInt("PAY_NUM");
    }

    @Override
    protected void makUca(IData data) throws Exception
    {
        makUcaForGrpNormal(data);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "1604";
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        //设置订单为未支付状态  暂定为H
        tradeData.put("SUBSCRIBE_STATE","H");

    }

    @Override
    protected void regOrder() throws Exception
    {
        super.regOrder();
        IData orderData = bizData.getOrder();
        orderData.put("ORDER_STATE","H");
    }
}
