
package com.asiainfo.veris.crm.order.soa.group.bindbroadbandmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupTradeBaseBean;

public class BindBroadBandMgrBean extends GroupTradeBaseBean
{
	protected BindBroadBandReqData reqData = null;
	
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
        relaData.put("RELATION_TYPE_CODE", "T9");
        relaData.put("ROLE_TYPE_CODE", "0");
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", "0");
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", SysDateMgr.getSysTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        super.addTradeRelation(relaData);
    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        actTradeRelation();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new BindBroadBandReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (BindBroadBandReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        reqData.setSerialNumber(map.getString("SERIAL_NUMBER"));
        reqData.setKdSerialNumber(map.getString("KD_SERIAL_NUMBER"));
        reqData.setBindTag(map.getString("BIND_TAG"));
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);

        //由于号码是IMS的固话号码,只是用于绑定UU关系
        String serialNumber = reqData.getSerialNumber();
        if(StringUtils.isBlank(serialNumber))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "该IMS固话号码为空!");
        }
        //UcaData grpUca = UcaDataFactory.getNormalUcaByQry(serialNumber,true,true);
        UcaData grpUca = UcaDataFactory.getNormalUca(serialNumber,true);
        reqData.setGrpUca(grpUca);

        String kdSerialNumber = reqData.getKdSerialNumber();
        if(StringUtils.isBlank(kdSerialNumber))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "需要绑定的宽带账号为空!");
        }
        
        UcaData ucaData = UcaDataFactory.getNormalUcaByQry(kdSerialNumber,true,false);
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

        tradeData.put("CUST_ID_B", reqData.getUca().getCustId());
        tradeData.put("USER_ID_B", reqData.getUca().getUserId());
        tradeData.put("ACCT_ID_B", "-1");
        tradeData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());

        tradeData.put("RSRV_STR1", reqData.getKdSerialNumber());
        
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "3754";
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3754";
    }
    
}
