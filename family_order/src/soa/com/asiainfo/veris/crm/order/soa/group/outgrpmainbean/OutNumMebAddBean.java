
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * 成员网外号码新增
 * 
 * @author loyoveui
 */
public class OutNumMebAddBean extends MemberBean
{
    protected OutNumMebAddBeanReqData reqData = null;

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

        // 检查网外号码唯一性
        checkOutGrpNum();
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

    /**
     * 检查网外号码唯一性
     */
    public void checkOutGrpNum() throws Exception
    {
        String strGrpOutSnB = reqData.getOUT_GRP_NUM(); // 取网外号码
        // 成员用户USER_ID
        String user_id_a = reqData.getUca().getUserId();
        String eparch_code = reqData.getUca().getUserEparchyCode();

        // 检查网外号码唯一性
        IDataset outparams = RelaUUInfoQry.getGrpOutinfo(user_id_a, null, eparch_code);
        for (int i = 0; i < outparams.size(); i++)
        {
            IData uuinfoData = outparams.getData(i);
            String serial_number_b = uuinfoData.getString("SERIAL_NUMBER_B", "");
            if (serial_number_b.equals(strGrpOutSnB))
            {
                CSAppException.apperr(GrpException.CRM_GRP_41, strGrpOutSnB);
            }
        }
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new OutNumMebAddBeanReqData();
    }

    public void infoRegDataRelation() throws Exception
    {
        IData rela = new DataMap();
        rela.put("USER_ID_A", reqData.getUca().getUserId()); // 成员用户ID
        rela.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber()); // 成员sn
        rela.put("USER_ID_B", reqData.getUSER_ID_OUTPHONE()); // 网外号码user_id
        rela.put("SERIAL_NUMBER_B", reqData.getOUT_GRP_NUM()); // 网外号码
        rela.put("ROLE_CODE_A", "0");
        rela.put("ROLE_CODE_B", "1");
        rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        rela.put("START_DATE", SysDateMgr.getSysTime());
        rela.put("RSRV_STR1", "G");
        rela.put("END_DATE", SysDateMgr.getTheLastTime());
        rela.put("RELATION_TYPE_CODE", "MO"); // 成员网外号码业务
        rela.put("INST_ID", SeqMgr.getInstId());
        this.addTradeRelation(rela);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (OutNumMebAddBeanReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setOUT_GRP_NUM(map.getString("OUT_GRP_NUM")); // 网外号码
        String user_id_b_outphone = SeqMgr.getUserId();
        reqData.setUSER_ID_OUTPHONE(user_id_b_outphone); // 网外号码user_id
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

        data.put("USER_ID_B", reqData.getUSER_ID_OUTPHONE()); // 网外的
        data.put("SERIAL_NUMBER_B", reqData.getOUT_GRP_NUM());

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
