
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupTradeBaseBean;

public class AddParentVpmnOutNetBean extends GroupTradeBaseBean
{
    protected ParentVpmnOutNetReqData reqData = null;

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
        relaData.put("RELATION_TYPE_CODE", "41");
        relaData.put("ROLE_TYPE_CODE", "1");
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", "0");
        relaData.put("SHORT_CODE", reqData.getOutShortCode());
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
        return new ParentVpmnOutNetReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ParentVpmnOutNetReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        reqData.setSerialNumber(map.getString("SERIAL_NUMBER"));
        reqData.setOutSerialNumber(map.getString("OUT_SERIAL_NUMBER"));
        reqData.setOutShortCode(map.getString("OUT_SHORT_CODE"));
        reqData.setRemark(map.getString("REMARK"));
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);

        UcaData grpUca = UcaDataFactory.getNormalUcaBySnForGrp(map);

        reqData.setGrpUca(grpUca);

        IData userData = new DataMap();
        userData.put("USER_ID", SeqMgr.getUserId());
        userData.put("SERIAL_NUMBER", reqData.getOutSerialNumber());

        UserTradeData userTradeData = new UserTradeData(userData);
        UcaData ucaData = new UcaData();
        ucaData.setUser(userTradeData);

        reqData.setUca(ucaData);
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

        tradeData.put("RSRV_STR1", reqData.getOutSerialNumber());
        tradeData.put("RSRV_STR2", reqData.getOutShortCode());
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "3584";
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3584";
    }
}
