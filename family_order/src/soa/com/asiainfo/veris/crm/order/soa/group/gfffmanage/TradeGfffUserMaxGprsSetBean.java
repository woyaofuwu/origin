
package com.asiainfo.veris.crm.order.soa.group.gfffmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;



public class TradeGfffUserMaxGprsSetBean extends GroupBean
{
    
    protected GfffUserMaxGprsSetReqData reqData = null;
    
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataOtherAdd();

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new GfffUserMaxGprsSetReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (GfffUserMaxGprsSetReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        reqData.setGprsMax(map.getString("GPRS_MAX"));
        reqData.setOtherInstId(map.getString("INST_ID"));
        
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }
    
    /**
     * 处理OTHER表
     * @throws Exception
     */
    private void infoRegDataOtherAdd() throws Exception
    {
        String otherInst = reqData.getOtherInstId();
        String gprsMax = reqData.getGprsMax();
        String userId = reqData.getUca().getUserId();
        String productId = reqData.getUca().getProductId();
        
        IDataset otherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"GFFF_MAX");
        
        //已存在则修改
        if(IDataUtil.isNotEmpty(otherInfos))
        {
            IData tradeInfo = otherInfos.getData(0);
            tradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            tradeInfo.put("RSRV_STR1", tradeInfo.getString("RSRV_VALUE"));	//旧流量上限值
            tradeInfo.put("RSRV_VALUE", gprsMax);				//新流量上限值
            tradeInfo.put("START_DATE", SysDateMgr.getSysTime());
            tradeInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            super.addTradeOther(tradeInfo);
        }
        //不存在则新增
        else{
        	IData tradeInfo = new DataMap();
            tradeInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            tradeInfo.put("USER_ID", userId);
            tradeInfo.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
            tradeInfo.put("START_DATE", SysDateMgr.getSysTime());
            tradeInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            tradeInfo.put("RSRV_VALUE_CODE", "GFFF_MAX");
            tradeInfo.put("RSRV_VALUE", gprsMax);
            tradeInfo.put("INST_ID", SeqMgr.getInstId());
            super.addTradeOther(tradeInfo);
        }

    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3908";	//流量自由充统付总流量上限设置
    }

}
