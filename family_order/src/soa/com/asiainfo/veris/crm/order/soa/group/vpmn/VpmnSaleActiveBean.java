
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class VpmnSaleActiveBean extends MemberBean
{
    protected VpmnSaleActiveReqData reqData = null;

    /**
     * 生成台帐表其它数据（拼台帐前）
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        String activeType = reqData.getRsrvStr4();
        if ("2".equals(activeType))
        {
            infoRegSecMonthFeeDiscnt();
        }
        else if ("9".equals(activeType))
        {
            infoRegBothFeeDiscnt();
        }
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        String activeType = reqData.getRsrvStr4();// 活动类型 1--V网分享有礼活动，2--V网体验活动,9--双网有礼活动
        if ("9".equals(activeType))
        {
            infoBothActiveRelation();
        }
        else if ("2".equals(activeType))
        {
            infoVpmnActiveRelation();
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new VpmnSaleActiveReqData();
    }

    /**
     * 记录双网有礼活动
     * 
     * @author add by wangyf6
     * @throws Exception
     */
    public void infoBothActiveRelation() throws Exception
    {
        IData rela = new DataMap();
        String userId = reqData.getUserId();
        rela.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
        rela.put("USER_ID_A", reqData.getUserIdA());
        rela.put("SERIAL_NUMBER_A", reqData.getSerialNumberA());
        rela.put("USER_ID_B", userId);
        rela.put("SERIAL_NUMBER_B", reqData.getSerialNumber());
        rela.put("ACTIVE_TYPE", reqData.getRsrvStr4());
        rela.put("START_DATE", reqData.getSysDate());
        // 绑定时间是12个月,立即生效
        rela.put("END_DATE", SysDateMgr.getAddMonthsLastDay(12, reqData.getSysDate()));
        rela.put("UPDATE_TIME", reqData.getSysDate());
        rela.put("UPDATE_STAFF_ID", reqData.getUpdateStaffId());
        rela.put("UPDATE_DEPART_ID", reqData.getUpdateDeptId());

        rela.put("GIVE_TAG", "0");// 字段GIVE_TAG记录话费赠送标记（0未赠送，1已赠送）
        rela.put("GIVE_DATE", "");// 字段GIVE_DATE记录话费赠送时间
        rela.put("RSRV_TAG1", "");
        rela.put("RSRV_TAG2", "");
        rela.put("RSRV_TAG3", "");
        rela.put("RSRV_STR1", reqData.getRsrvStr5());// 字段RSRV_STR5记录V网集团编码
        rela.put("RSRV_STR2", reqData.getRsrvStr6());// 字段RSRV_STR6记录V网集团名称
        rela.put("RSRV_STR3", "");// 字段RSRV_STR3记录受理员工
        rela.put("RSRV_DATE1", "");
        rela.put("RSRV_DATE2", reqData.getRsrvStr7());// 字段RSRV_DATE2记录加入V网时间
        rela.put("RSRV_DATE3", "");
        rela.put("REMARK", "");
        rela.put("INST_ID", SeqMgr.getInstId());
        boolean res = Dao.insert("TF_F_VPMNACTIVE_RELATION", rela, Route.CONN_CRM_CG);
        if (!res)
        {
            // 新增集团V网营销活动失败！
            CSAppException.apperr(VpmnUserException.VPMN_USER_121);
        }
    }

    /**
     * 特殊处理双网有礼活动套餐
     * 
     * @param pd
     * @throws Exception
     * @author wangyf6
     */
    public void infoRegBothFeeDiscnt() throws Exception
    {
        IDataset discnts = new DatasetList();
        IDataset datas = ParamInfoQry.getCommparaByParamattr("CSM", "6023", null, reqData.getEparchyCode());
        if (IDataUtil.isNotEmpty(datas))
        {
            for (int i = 0, size = datas.size(); i < size; i++)
            {
                IData discnt = new DataMap();
                discnt.put("USER_ID", reqData.getUca().getUserId());
                discnt.put("USER_ID_A", reqData.getGrpUca().getUserId());
                discnt.put("PRODUCT_ID", "-1");
                discnt.put("PACKAGE_ID", "-1");
                discnt.put("DISCNT_CODE", datas.getData(i).getString("PARAM_CODE"));
                discnt.put("ELEMENT_ID", datas.getData(i).getString("PARAM_CODE"));
                discnt.put("SPEC_TAG", "2");
                discnt.put("RELATION_TYPE_CODE", "20");

                discnt.put("INST_ID", SeqMgr.getInstId());
                discnt.put("START_DATE", SysDateMgr.getSysDate());
                discnt.put("END_DATE", SysDateMgr.getAddMonthsLastDay(12, reqData.getSysDate()));
                discnt.put("DIVERSIFY_ACCT_TAG", "1");
                discnt.put("STATE", "ADD");
                discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                discnts.add(discnt);
            }
        }
        reqData.setDiscnts(discnts);
        this.addTradeDiscnt(discnts);
    }

    /**
     * 特殊处理集团V网3元免费体验套餐
     * 
     * @param pd
     * @throws Exception
     * @author lixiuyu
     */
    public void infoRegSecMonthFeeDiscnt() throws Exception
    {
        IDataset discnts = new DatasetList();
        IDataset datas = ParamInfoQry.getCommparaByParamattr("CSM", "6022", "3319", reqData.getEparchyCode());
        if (IDataUtil.isNotEmpty(datas))
        {
            for (int i = 0, size = datas.size(); i < size; i++)
            {
                IData discnt = new DataMap();
                discnt.put("USER_ID", reqData.getUca().getUserId());
                discnt.put("USER_ID_A", reqData.getGrpUca().getUserId());
                discnt.put("PRODUCT_ID", "-1");
                discnt.put("PACKAGE_ID", "-1");
                discnt.put("DISCNT_CODE", datas.getData(i).getString("PARAM_CODE"));
                discnt.put("ELEMENT_ID", datas.getData(i).getString("PARAM_CODE"));
                discnt.put("SPEC_TAG", "2");
                discnt.put("RELATION_TYPE_CODE", "20");

                discnt.put("INST_ID", SeqMgr.getInstId());
                discnt.put("START_DATE", SysDateMgr.getSysDate());
                discnt.put("END_DATE", SysDateMgr.getNextMonthLastDate());
                discnt.put("DIVERSIFY_ACCT_TAG", "1");
                discnt.put("STATE", "ADD");
                discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                discnts.add(discnt);
            }
        }
        reqData.setDiscnts(discnts);
        this.addTradeDiscnt(discnts);
    }

    /**
     * 记录V网营销活动关系
     * 
     * @throws Exception
     */
    public void infoVpmnActiveRelation() throws Exception
    {
        IData rela = new DataMap();
        String userId = reqData.getUserId();
        rela.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
        rela.put("USER_ID_A", reqData.getUserIdA());
        rela.put("SERIAL_NUMBER_A", reqData.getSerialNumberA());
        rela.put("USER_ID_B", userId);
        rela.put("SERIAL_NUMBER_B", reqData.getSerialNumber());
        rela.put("ACTIVE_TYPE", reqData.getRsrvStr4());
        rela.put("START_DATE", reqData.getSysDate());
        rela.put("END_DATE", reqData.getEndDate());

        rela.put("UPDATE_TIME", reqData.getSysDate());
        rela.put("UPDATE_STAFF_ID", reqData.getUpdateStaffId());
        rela.put("UPDATE_DEPART_ID", reqData.getUpdateDeptId());

        rela.put("GIVE_TAG", "0");// 字段GIVE_TAG记录话费赠送标记（0未赠送，1已赠送）
        rela.put("GIVE_DATE", "");// 字段GIVE_DATE记录话费赠送时间
        rela.put("RSRV_TAG1", "");
        rela.put("RSRV_TAG2", "");
        rela.put("RSRV_TAG3", "");
        rela.put("RSRV_STR1", reqData.getRsrvStr5());// 字段RSRV_STR5记录V网集团编码
        rela.put("RSRV_STR2", reqData.getRsrvStr6());// 字段RSRV_STR6记录V网集团名称
        rela.put("RSRV_STR3", "");// 字段RSRV_STR3记录受理员工
        rela.put("RSRV_DATE1", "");
        rela.put("RSRV_DATE2", reqData.getRsrvStr7());// 字段RSRV_DATE2记录加入V网时间
        rela.put("RSRV_DATE3", "");
        rela.put("REMARK", "");
        rela.put("INST_ID", SeqMgr.getInstId());

        boolean res = Dao.insert("TF_F_VPMNACTIVE_RELATION", rela, Route.CONN_CRM_CG);
        if (!res)
        {
            // 新增集团V网营销活动失败！
            CSAppException.apperr(VpmnUserException.VPMN_USER_121);
        }
    }

    /**
     * 初始化reqData
     */
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (VpmnSaleActiveReqData) getBaseReqData();
    }

    /**
     * 设置reqData
     */
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        IData userInfo = map.getData("MEM_USER_INFO");
        IData custInfo = map.getData("MEM_CUST_INFO");
        IData acctInfo = map.getData("MEM_ACCT_INFO");
        // 业务流水号
        String tradeId = SeqMgr.getTradeId();
        reqData.setTradeId(tradeId);
        // 订单流水号
        String orderId = SeqMgr.getOrderId();
        reqData.setOrderId(orderId);
        reqData.setBatchId(map.getString("BATCH_ID"));
        // 受理月份
        String acceptMonth = SysDateMgr.getCurMonth();
        reqData.setAcceptMonth(acceptMonth);
        // 客户信息
        reqData.setCustId(custInfo.getString("CUST_ID", ""));
        reqData.setCustName(custInfo.getString("CUST_NAME", ""));
        reqData.setOrderCustId(custInfo.getString("CUST_ID", ""));
        reqData.setOrderCustName(custInfo.getString("CUST_NAME", ""));
        reqData.setPsptId(custInfo.getString("PSPT_ID", ""));
        reqData.setPsptTypeCode(custInfo.getString("PSPT_TYPE_CODE", ""));
        reqData.setGroupId(custInfo.getString("GROUP_ID", ""));
        // 被推荐号码用户信息
        reqData.setUserId(userInfo.getString("USER_ID", "")); // 被推荐号码USER_ID
        reqData.setSerialNumber(userInfo.getString("SERIAL_NUMBER", "")); // 被推荐号码

        // 推荐号码用户信息
        reqData.setUserIdA(map.getString("USER_ID_A"));
        reqData.setSerialNumberA(map.getString("SERIAL_NUMBER_A"));

        reqData.setUserIdB(map.getString("USER_ID_A"));
        reqData.setSerialNumberB(map.getString("SERIAL_NUMBER_A"));

        // 活动类型
        reqData.setRsrvStr4(map.getString("ACTIVE_TYPE", ""));// 记录活动类型字段
        reqData.setRsrvStr5(map.getString("GRP_SERIAL_NUMBER"));// V网集团编码
        reqData.setRsrvStr6(reqData.getGrpUca().getCustGroup().getCustName());// V网集团名称
        reqData.setRsrvStr7(reqData.getGrpUca().getUser().getOpenDate());// 加入V网的时间

        reqData.setAcctId(acctInfo.getString("ACCT_ID"));
        reqData.setNetTypeCode(userInfo.getString("NET_TYPE_CODE"));

        reqData.setProductId(userInfo.getString("PRODUCT_ID", ""));
        reqData.setBrandCode(userInfo.getString("BRAND_CODE", ""));

        // 业务类型
        reqData.setTradeTypeCode(map.getString("TRADE_TYPE_CODE"));
        reqData.setPriority("290"); // 优先级
        // 地州编码
        reqData.setEparchyCode(map.getString(Route.ROUTE_EPARCHY_CODE));
        reqData.setTradeEparchCode(map.getString("TRADE_EPARCHY_CODE"));

        reqData.setUpdateStaffId(map.getString("UPDATE_STAFF_ID"));
        reqData.setUpdateDeptId(map.getString("UPDATE_DEPART_ID"));
        // 系统时间
        reqData.setSysDate(SysDateMgr.getSysDate());
        reqData.setEndDate(SysDateMgr.getTheLastTime());
        reqData.setExecTime(SysDateMgr.getSysDate());
    }

    /**
     * 构建uca
     */
    protected void makUca(IData map) throws Exception
    {
        makUcaForVpmnSaleActive(map);
    }

    protected void makUcaForVpmnSaleActive(IData map) throws Exception
    {
        UcaData ucaData = new UcaData();
        IData memUserInfo = map.getData("MEM_USER_INFO");
        IData memCustInfo = map.getData("MEM_CUST_INFO");
        IData memAcctInfo = map.getData("MEM_ACCT_INFO");

        ucaData.setUser(new UserTradeData(memUserInfo));
        ucaData.setCustomer(new CustomerTradeData(memCustInfo));
        ucaData.setAccount(new AccountTradeData(memAcctInfo));
        reqData.setUca(ucaData);

        // GRP_SERIAL_NUMBER
        String grpSerialNumber = map.getString("GRP_SERIAL_NUMBER");
        String grpUserId = map.getString("GRP_USER_ID");

        UcaData grpUCA = DataBusManager.getDataBus().getUca(grpSerialNumber);

        if (grpUCA == null)
        {
            IData d = new DataMap();
            d.put("USER_ID", grpUserId);
            grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(d);
        }
        reqData.setGrpUca(grpUCA);
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "3603";
    }
}
