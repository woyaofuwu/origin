
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupTradeBaseBean;

public class AddSubGrpVpmnBean extends GroupTradeBaseBean
{
    protected SubGrpVpmnReqData reqData = null;

    /**
     * 登记Relation信息
     * 
     * @throws Exception
     */
    public void actTradeRelation() throws Exception
    {
        IData relaData = new DataMap();

        relaData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        relaData.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
        relaData.put("USER_ID_B", reqData.getUca().getUserId());
        relaData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
        relaData.put("RELATION_TYPE_CODE", "40");
        relaData.put("ROLE_TYPE_CODE", "0");
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", "0");
        relaData.put("SHORT_CODE", "");
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", SysDateMgr.getSysTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        relaData.put("REMARK", reqData.getRemark());

        super.addTradeRelation(relaData);
    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // Relation信息
        actTradeRelation();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new SubGrpVpmnReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (SubGrpVpmnReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        reqData.setParentSerialNumber(map.getString("PARENT_SERIAL_NUMBER"));
        reqData.setSerialNumber(map.getString("SERIAL_NUMBER"));
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);

        IData param = new DataMap();

        // 子VPMN信息
        param.put("SERIAL_NUMBER", reqData.getSerialNumber());
        UcaData uca = UcaDataFactory.getNormalUcaBySnForGrp(param);

        reqData.setUca(uca);

        // 母VPMN信息
        param.clear();
        param.put("SERIAL_NUMBER", reqData.getParentSerialNumber());
        UcaData grpUca = UcaDataFactory.getNormalUcaBySnForGrp(param);

        reqData.setGrpUca(grpUca);
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        tradeData.put("CUST_ID", reqData.getGrpUca().getCustId());
        tradeData.put("CUST_NAME", reqData.getGrpUca().getCustomer().getCustName());
        tradeData.put("USER_ID", reqData.getGrpUca().getUserId());
        tradeData.put("ACCT_ID", reqData.getGrpUca().getAccount().getAcctId());

        tradeData.put("SERIAL_NUMBER", reqData.getGrpUca().getSerialNumber());
        tradeData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        tradeData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        tradeData.put("PRODUCT_ID", reqData.getGrpUca().getProductId());
        tradeData.put("BRAND_CODE", reqData.getGrpUca().getBrandCode());

        tradeData.put("CUST_ID_B", "-1");
        tradeData.put("USER_ID_B", reqData.getUca().getUserId());
        tradeData.put("ACCT_ID_B", "-1");
        tradeData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());

        tradeData.put("RSRV_STR1", reqData.getUca().getSerialNumber());
        tradeData.put("RSRV_STR2", reqData.getUca().getUserId());
        tradeData.put("RSRV_STR3", reqData.getUca().getCustId());
        tradeData.put("RSRV_STR4", SysDateMgr.getSysTime());
        tradeData.put("RSRV_STR5", SysDateMgr.getTheLastTime());
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "3580";
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3580";
    }
}
