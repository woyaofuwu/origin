
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

/**
 * 子母集团新增
 * 
 * @author loyoveui
 */
public class ZmGrpAddBean extends GroupBean
{
    protected ZmGrpAddBeanReqData reqData = null;

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataRelation(); // 湖南网外都走UU
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new ZmGrpAddBeanReqData();
    }

    public void infoRegDataRelation() throws Exception
    {
        IData rela = new DataMap();
        rela.put("RELATION_TYPE_CODE", "ZM");
        rela.put("USER_ID_A", reqData.getUca().getUserId()); // 母集团USER_ID
        rela.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber()); // 母集团SERIAL_NUMBER
        rela.put("USER_ID_B", reqData.getUSER_ID_B()); // 子集团user_id
        rela.put("SERIAL_NUMBER_B", reqData.getSERIAL_NUMBER_B()); // 子集团sn
        rela.put("ROLE_CODE_A", "0");
        rela.put("ROLE_CODE_B", "1");
        rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        rela.put("START_DATE", getAcceptTime());
        rela.put("REMARK", reqData.getRemark()); // 备注字段
        rela.put("END_DATE", SysDateMgr.getTheLastTime());

        String ind_id = SeqMgr.getInstId();
        rela.put("INST_ID", ind_id);

        this.addTradeRelation(rela);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ZmGrpAddBeanReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setUSER_ID_B(map.getString("USER_ID_B")); // 子集团user_id
        reqData.setSERIAL_NUMBER_B(map.getString("SERIAL_NUMBER_B")); // 子集团sn
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }

    /**
     * 处理台帐主表的数据
     */
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("CUST_NAME", reqData.getUca().getCustGroup().getCustName()); // 客户名称
        data.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识
        data.put("NET_TYPE_CODE", "G"); // 网别编码
        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", "-1"); // 产品标识
        data.put("CUST_ID_B", "-1"); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
        data.put("ACCT_ID_B", "-1"); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
        data.put("BRAND_CODE", "");// 品牌编码

        data.put("USER_ID", reqData.getUca().getUserId()); // 母集团
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        data.put("CUST_ID", reqData.getUca().getCustId());

        data.put("USER_ID_B", reqData.getUSER_ID_B()); // 子集团
        data.put("SERIAL_NUMBER_B", reqData.getSERIAL_NUMBER_B());

        data.put("RSRV_STR1", ""); // 预留字段1
        data.put("RSRV_STR2", ""); // 预留字段2
        data.put("RSRV_STR3", ""); // 预留字段3
        data.put("RSRV_STR4", ""); // 预留字段4
        data.put("RSRV_STR5", ""); // 预留字段5
        data.put("RSRV_STR6", ""); // 预留字段6
        data.put("RSRV_STR7", ""); // 预留字段7
        data.put("RSRV_STR8", ""); // 预留字段8
        data.put("RSRV_STR9", ""); // 预留字段9
        data.put("RSRV_STR10", ""); // 预留字段10
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "1061"; // 子母集团新增
    }
}
