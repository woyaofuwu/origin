package com.asiainfo.veris.crm.order.soa.group.modifylinepayrelation;

import com.ailk.bizcommon.util.Clone;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ModifyLinePayRelation extends GroupBean {

    private String acctId = "";

    private String operType = "";

    private IData eosInfo;

    @Override
    public void actTradeBefore() throws Exception {

        super.actTradeBefore();
    }

    @Override
    public void actTradeSub() throws Exception {

        super.actTradeSub();

        actTradePayRela();

        infoRegDataTradeExt();

    }

    protected void actTradePayRela() throws Exception {
        IDataset payRelaInfos = new DatasetList();
        IData payRelaData = UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(reqData.getUca().getUserId());
        if(DataUtils.isEmpty(payRelaData)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID=" + reqData.getUca().getUserId() + "未查询到默认付费关系！");
        }
        IData newPayRelaData = (IData) Clone.deepClone(payRelaData);
        //新加付费关系
        newPayRelaData.put("ACCT_ID", acctId);
        newPayRelaData.put("START_CYCLE_ID", SysDateMgr.getNowCycle());
        newPayRelaData.put("INST_ID", SeqMgr.getInstId());
        newPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        //删除旧的付费关系
        payRelaData.put("DEFAULT_TAG", "0");//默认付费标记
        payRelaData.put("ACT_TAG", "0");//作用标志：0-不作用，1-作用
        payRelaData.put("END_CYCLE_ID", SysDateMgr.getNowCycle());
        payRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

        payRelaInfos.add(payRelaData);
        payRelaInfos.add(newPayRelaData);

        super.addTradePayrelation(payRelaInfos);
    }

    public void infoRegDataTradeExt() throws Exception {
        if(DataUtils.isEmpty(eosInfo)) {
            return;
        }
        IDataset extDatas = new DatasetList();
        IData data = new DataMap();
        data.put("ATTR_CODE", "ESOP");
        data.put("ATTR_VALUE", eosInfo.getString("IBSYSID"));
        data.put("RSRV_STR1", eosInfo.getString("NODE_ID"));
        data.put("RSRV_STR10", "EOS");
        data.put("RSRV_STR5", "NEWFLAG");
        data.put("RSRV_STR6", eosInfo.getString("RECORD_NUM"));
        extDatas.add(data);
        addTradeExt(extDatas);

    }

    @Override
    protected void makInit(IData map) throws Exception {
        super.makInit(map);
        acctId = map.getString("ACCT_ID");
        operType = map.getString("OPERTYPE");
        eosInfo = map.getData("ESOP");
    }

    @Override
    protected void makUca(IData map) throws Exception {
        makUcaForGrpNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception {
        return "1602";
    }

    /*@Override
    protected void setTradePayrelation(IData map) throws Exception {
        super.setTradePayrelation(map);
    
        map.put("USER_ID", reqData.getUca().getUserId());
        map.put("INST_ID", SeqMgr.getInstId()); // 实例标识
    }*/
}
