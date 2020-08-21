
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupTradeBaseBean;

public class DelParentVpmnOutNetBean extends GroupTradeBaseBean
{
    protected ParentVpmnOutNetReqData reqData = null;

    /**
     * 登记Relation信息
     * 
     * @throws Exception
     */
    public void actTradeRelation() throws Exception
    {
        IDataset relaList = RelaUUInfoQry.qryUU(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId(), "41", null);

        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_8, reqData.getUca().getSerialNumber(), reqData.getGrpUca().getSerialNumber());
        }

        IData relaData = relaList.getData(0);
        relaData.put("END_DATE", SysDateMgr.getSysTime());
        relaData.put("REMARK", reqData.getRemark());
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

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
        reqData.setRemark(map.getString("REMARK"));
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);

        UcaData grpUca = UcaDataFactory.getNormalUcaBySnForGrp(map);

        reqData.setGrpUca(grpUca);

        IDataset relaList = RelaUUInfoQry.queryRelaUUBySnb(reqData.getOutSerialNumber(), "41");

        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_8, reqData.getOutSerialNumber(), reqData.getGrpUca().getSerialNumber());
        }

        IData userData = new DataMap();
        userData.put("USER_ID", relaList.getData(0).getString("USER_ID_B"));
        userData.put("SERIAL_NUMBER", reqData.getOutSerialNumber());

        UserTradeData userTradeData = new UserTradeData(userData);
        UcaData ucaData = new UcaData();
        ucaData.setUser(userTradeData);

        reqData.setUca(ucaData);

        reqData.setOutShortCode(relaList.getData(0).getString("SHORT_CODE"));
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
        return "3585";
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3585";
    }
}
