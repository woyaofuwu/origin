
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * 成员网外号码删除
 * 
 * @author loyoveui
 */
public class OutNumMebDelBean extends MemberBean
{
    protected OutNumMebDelBeanReqData reqData = null;

    /**
     * 生成台帐表其它数据（拼台帐前）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataRelation(); // 湖南网外都走UU
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new OutNumMebDelBeanReqData();
    }

    public void infoRegDataRelation() throws Exception
    {
        String user_id_a = reqData.getUca().getUserId();
        String user_id_b = reqData.getUSER_ID_B();

        IDataset relationUUs = RelaUUInfoQry.qryUU(user_id_a, user_id_b, "MO", null);
        IData relationUU = new DataMap();
        for (int i = 0; i < relationUUs.size(); i++)
        {
            relationUU = relationUUs.getData(i);

            relationUU.put("END_DATE", getAcceptTime());
            relationUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }

        this.addTradeRelation(relationUU);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (OutNumMebDelBeanReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setUSER_ID_B(map.getString("USER_ID_B")); // 网外号码user_id
        reqData.setSERIAL_NUMBER_B(map.getString("SERIAL_NUMBER_B")); // 网外号码sn
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        makUcaForMebNormal(map);
    }

    /**
     * 处理台帐主表的数据
     */
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("CUST_NAME", ""); // 客户名称
        data.put("CUST_ID", reqData.getUca().getCustId());
        data.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识
        data.put("NET_TYPE_CODE", "G"); // 网别编码
        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", "-1"); // 产品标识
        data.put("CUST_ID_B", "-1"); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
        data.put("ACCT_ID_B", "-1"); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
        data.put("BRAND_CODE", reqData.getUca().getBrandCode());// 品牌编码

        data.put("USER_ID_B", reqData.getUSER_ID_B()); // 网外的
        data.put("SERIAL_NUMBER_B", reqData.getSERIAL_NUMBER_B());

        data.put("USER_ID", reqData.getUca().getUserId()); // 成员的
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

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

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "4000"; // 成员网外号码维护
    }
}
