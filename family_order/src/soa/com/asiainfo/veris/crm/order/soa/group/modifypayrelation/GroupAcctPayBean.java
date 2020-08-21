
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.group.common.query.BookTradeSVC;

public class GroupAcctPayBean extends GroupBean
{
    private IData baseCommInfo = new DataMap();

    private String state = "";

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        dealRelationAA();
        // 短信发送
        if (!"MODI".equals(state))
        {
            // setRegSms();
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new GroupAcctPayReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (GroupAcctPayReqData) getBaseReqData();
    }

    public void dealRelationAA() throws Exception
    {
        IDataset idatas = new DatasetList();
        IData data = new DataMap();
        state = baseCommInfo.getString("STATE");
        if ("ADD".equals(state))
        {// 建立代付、统付关系
            data.put("ACCT_ID_A", baseCommInfo.getString("ACCT_ID_A", "")); // 代付账户
            data.put("USER_ID_A", baseCommInfo.getString("USER_ID_A", "")); // A用户标识
            data.put("ACCT_ID_B", baseCommInfo.getString("ACCT_ID_B", "")); // 被代付账户
            data.put("USER_ID_B", baseCommInfo.getString("USER_ID_B", "")); // B用户标识
            data.put("ACT_TAG", "1"); // 有效
            data.put("PAYITEM_CODE", baseCommInfo.getString("PAYITEM_CODE", "")); // 付费账目编码，默认-1，表示所有账目
            data.put("USER_ID_B", baseCommInfo.getString("USER_ID_B", "")); // B用户标识
            data.put("RELATION_TYPE_CODE", baseCommInfo.getString("RELATION_TYPE_CODE")); // 关系类型
            data.put("ROLE_CODE_A", baseCommInfo.getString("ROLE_CODE_A", "")); // 关系类型
            data.put("ROLE_CODE_B", baseCommInfo.getString("ROLE_CODE_B", "")); // 关系类型
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            data.put("START_DATE", SysDateMgr.getDateForYYYYMMDD(getAcceptTime()));
            data.put("END_DATE", SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getTheLastTime()));
            data.put("INST_ID", SeqMgr.getInstId());
            // 查询代付关系顺序号
            IData inparams = new DataMap();
            inparams.put("ACCT_ID", baseCommInfo.getString("ACCT_ID_A", ""));
            int orserNo = 1;
            // //查询cg库
            // IDataset cginfos = AcctInfoQry.getAllRelAAByActIdA(pd, inparams, "cg",pd.getPagination());
            // orserNo += cginfos.size();
            // //遍历地州库查询账务关系
            // String[] connNames = ConnectionManager.getInstance().getAllNames();
            // if (connNames != null) {
            // int len = connNames.length;
            // for (int j = 0; j < len; j++){
            // String connName = connNames[j];
            // if (connName.indexOf("crm") >= 0){
            // IDataset crminfos = AcctInfoQry.getAllRelAAByActIdA(pd, inparams, connName,pd.getPagination());
            // orserNo += crminfos.size();
            // }
            // }
            // }
            // setDbConCode(pd,"cg");
            data.put("ORDERNO", orserNo); // 顺序号：表示B用户在此关联关系中的顺序号
            data.put("LIMIT_TYPE", baseCommInfo.getString("LIMIT_TYPE", "")); // 限定类型：0-全部 1-按比例 2-按金额
            data.put("LIMIT_VALUE", baseCommInfo.getString("LIMIT_VALUE", "")); // 限定值 金额:分 比例:%
            data.put("RSRV_STR1", baseCommInfo.getString("RSRV_STR1", ""));// 付费账目名称
            data.put("RSRV_STR2", baseCommInfo.getString("EPARCHY_CODE_A"));// 支付账户归属地州
            data.put("RSRV_STR3", baseCommInfo.getString("EPARCHY_CODE_B"));// 被支付账户归属地州
            idatas.add(data);

        }
        else if ("DEL".equals(state))
        {// 删除代付、统付关系
            BookTradeSVC bookTrade = new BookTradeSVC();
            IData param = new DataMap();
            param.put("ACCT_ID_A", baseCommInfo.getString("ACCT_ID_A", ""));
            param.put("ACCT_ID_B", baseCommInfo.getString("ACCT_ID_B", ""));
            IDataset relationaa = bookTrade.qryRelationAAByAcctIdAAndB(param);

            if (null != relationaa && relationaa.size() > 0)
            {
                IData relation = relationaa.getData(0);
                relation.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                relation.put("ACT_TAG", "0");
                relation.put("UPDATE_TIME", getAcceptTime());
                relation.put("END_DATE", SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getAddMonthsLastDay(-1)));

                idatas.add(relation);
            }

        }
        else if ("MODI".equals(state))
        {// 修改统付、代付关系
            IDataset dataset = baseCommInfo.getDataset("DATA_SET");
            if (IDataUtil.isEmpty(dataset))
            {
                return;
            }

            for (int i = 0; i < dataset.size(); i++)
            {
                IData modiData = dataset.getData(i);
                IDataset relationAAs = AcctInfoQry.getRelationAAByActIdAB(modiData, null);
                IData relationAA = relationAAs.getData(0);
                relationAA.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                relationAA.put("ORDERNO", modiData.getString("ORDERNO"));
                relationAA.put("REMARK", modiData.getString("REMARK"));
                relationAA.put("ACT_TAG", "1"); // 有效
                idatas.add(relationAA);
            }
        }

        super.addTradeRelationAa(idatas);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        baseCommInfo.put("TRADE_TYPE_CODE", map.getString("TRADE_TYPE_CODE"));
        baseCommInfo.put("LIMIT_TYPE", map.getString("LIMIT_TYPE"));
        baseCommInfo.put("LIMIT", map.getString("LIMIT", "0"));
        baseCommInfo.put("COMPLEMENT_TAG", map.getString("COMPLEMENT_TAG"));

        baseCommInfo.put("ACCT_PRIORITY", map.getString("ACCT_PRIORITY"));
        baseCommInfo.put("newSnInfo_CheckAll", map.getString("newSnInfo_CheckAll"));

        baseCommInfo.put("STATE", map.getString("STATE"));//
        baseCommInfo.put("ACCT_ID_A", map.getString("ACCT_ID"));// 支付账户
        baseCommInfo.put("EPARCHY_CODE_A", map.getString("PAY_EPARCHY_CODE_A"));// 支付账户归属地州
        baseCommInfo.put("USER_ID_A", "-1");
        baseCommInfo.put("ACCT_ID_B", map.getString("ACCT_ID_B"));// 被支付账户
        baseCommInfo.put("EPARCHY_CODE_B", map.getString("PAY_EPARCHY_CODE_B"));// 被支付账户归属地州
        // 地州编码
        baseCommInfo.put("USER_EPARCHY_CODE", map.getString("USER_EPARCHY_CODE"));
        baseCommInfo.put("EPARCHY_CODE", map.getString("EPARCHY_CODE"));
        baseCommInfo.put("USER_ID_B", map.getString("USER_ID", "-1"));
        baseCommInfo.put("CUST_ID", map.getString("CUST_ID"));
        baseCommInfo.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", "-1"));
        baseCommInfo.put("PAYITEM_CODE", map.getString("PAYITEM_CODE"));// 付费账目编码
        baseCommInfo.put("RSRV_STR1", map.getString("RSRV_STR1"));// 付费账目名称
        baseCommInfo.put("ROLE_CODE_A", "0");// 0-代付账户 1-被代付账户
        baseCommInfo.put("ROLE_CODE_B", "1");
        baseCommInfo.put("ORDERNO", "0");
        baseCommInfo.put("LIMIT_TYPE", map.getString("LIMIT_TYPE"));
        baseCommInfo.put("LIMIT_VALUE", map.getString("LIMIT_VALUE"));
        baseCommInfo.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE"));// 统付

        if ("MODI".equals(map.getString("STATE")))
        {
            baseCommInfo.put("DATA_SET", map.getDataset("DATA_SET"));// 变更列表
        }
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUcaByCustIdForGrp(map);

        IData baseUserInfo = map.getData("USER_INFO");

        if (IDataUtil.isNull(baseUserInfo))
        {
            baseUserInfo = new DataMap();
        }

        // 生成用户id
        String userId = "-1";

        // 得到数据
        String productId = "-1";// 必须传
        String serialNumber = "-1";

        IData userInfo = new DataMap();
        userInfo.put("USER_ID", userId);// 用户标识
        userInfo.put("CUST_ID", uca.getCustGroup().getCustId()); // 归属客户标识
        userInfo.put("USECUST_ID", baseUserInfo.getString("USECUST_ID", uca.getCustGroup().getCustId())); // 使用客户标识：如果不指定，默认为归属客户标识

        userInfo.put("EPARCHY_CODE", baseUserInfo.getString("EPARCHY_CODE", CSBizBean.getTradeEparchyCode())); // 归属地市
        userInfo.put("CITY_CODE", baseUserInfo.getString("CITY_CODE", CSBizBean.getVisit().getCityCode())); // 归属业务区

        userInfo.put("CITY_CODE_A", baseUserInfo.getString("CITY_CODE_A", ""));
        userInfo.put("USER_PASSWD", baseUserInfo.getString("USER_PASSWD", "")); // 用户密码
        userInfo.put("USER_DIFF_CODE", baseUserInfo.getString("USER_DIFF_CODE", "")); // 用户类别
        userInfo.put("USER_TYPE_CODE", baseUserInfo.getString("USER_TYPE_CODE", "8")); // 用户类型
        userInfo.put("USER_TAG_SET", baseUserInfo.getString("USER_TAG_SET", ""));

        // 用户标志集：主要用来做某些信息的扩充，如：催缴标志、是否可停机标志，对于这个字段里面第几位表示什么含义在扩展的时候定义
        userInfo.put("USER_STATE_CODESET", baseUserInfo.getString("USER_STATE_CODESET", "0")); // 用户主体服务状态集：见服务状态参数表
        userInfo.put("NET_TYPE_CODE", baseUserInfo.getString("NET_TYPE_CODE", "00")); // 网别编码

        userInfo.put("SERIAL_NUMBER", serialNumber);// 必须由前台传,对于第3放接口,需要根据in_mode_code后台构造sn

        userInfo.put("SCORE_VALUE", baseUserInfo.getString("SCORE_VALUE", "0")); // 积分值
        userInfo.put("CONTRACT_ID", baseUserInfo.getString("CONTRACT_ID", "")); // 合同号

        userInfo.put("CREDIT_CLASS", baseUserInfo.getString("CREDIT_CLASS", "0")); // 信用等级
        userInfo.put("BASIC_CREDIT_VALUE", baseUserInfo.getString("BASIC_CREDIT_VALUE", "0")); // 基本信用度
        userInfo.put("CREDIT_VALUE", baseUserInfo.getString("CREDIT_VALUE", "0")); // 信用度
        userInfo.put("CREDIT_CONTROL_ID", baseUserInfo.getString("CREDIT_CONTROL_ID", "0")); // 信控规则标识
        userInfo.put("ACCT_TAG", baseUserInfo.getString("ACCT_TAG", "0")); // 出帐标志：0-正常处理，1-定时激活，2-待激活用户，Z-不出帐
        userInfo.put("PREPAY_TAG", baseUserInfo.getString("PREPAY_TAG", "0")); // 预付费标志：0-后付费，1-预付费。（省内标准）
        userInfo.put("MPUTE_MONTH_FEE", baseUserInfo.getString("MPUTE_MONTH_FEE", "0")); // 固定费用重算标志：0-不重算，1-重算，2-从月初开始重算
        userInfo.put("MPUTE_DATE", baseUserInfo.getString("MPUTE_DATE", "")); // 月租重算时间
        userInfo.put("FIRST_CALL_TIME", baseUserInfo.getString("FIRST_CALL_TIME", "")); // 首次通话时间
        userInfo.put("LAST_STOP_TIME", baseUserInfo.getString("LAST_STOP_TIME", "")); // 最后停机时间
        userInfo.put("CHANGEUSER_DATE", baseUserInfo.getString("CHANGEUSER_DATE", "")); // 过户时间
        userInfo.put("IN_NET_MODE", baseUserInfo.getString("IN_NET_MODE", "")); // 入网方式
        userInfo.put("IN_DATE", baseUserInfo.getString("IN_DATE", getAcceptTime())); // 建档时间
        userInfo.put("IN_STAFF_ID", baseUserInfo.getString("IN_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        userInfo.put("IN_DEPART_ID", baseUserInfo.getString("IN_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        userInfo.put("OPEN_MODE", baseUserInfo.getString("OPEN_MODE", "0")); // 开户方式：0-正常，1-预开未返单，2-预开已返单，3-过户新增，4-当日返单并过户
        userInfo.put("OPEN_DATE", baseUserInfo.getString("OPEN_DATE", getAcceptTime())); // 开户时间
        userInfo.put("OPEN_STAFF_ID", baseUserInfo.getString("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId())); // 开户员工
        userInfo.put("OPEN_DEPART_ID", baseUserInfo.getString("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 开户渠道
        userInfo.put("DEVELOP_STAFF_ID", baseUserInfo.getString("DEVELOP_STAFF_ID", "")); // 发展员工
        userInfo.put("DEVELOP_DATE", baseUserInfo.getString("DEVELOP_DATE", getAcceptTime())); // 发展时间
        userInfo.put("DEVELOP_DEPART_ID", baseUserInfo.getString("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId())); // 发展渠道
        userInfo.put("DEVELOP_CITY_CODE", baseUserInfo.getString("DEVELOP_CITY_CODE", CSBizBean.getVisit().getCityCode())); // 发展市县
        userInfo.put("DEVELOP_EPARCHY_CODE", baseUserInfo.getString("DEVELOP_EPARCHY_CODE", CSBizBean.getTradeEparchyCode())); // 发展地市
        userInfo.put("DEVELOP_NO", baseUserInfo.getString("DEVELOP_NO", "")); // 发展文号
        userInfo.put("ASSURE_CUST_ID", baseUserInfo.getString("ASSURE_CUST_ID", "")); // 担保客户标识
        userInfo.put("ASSURE_TYPE_CODE", baseUserInfo.getString("ASSURE_TYPE_CODE", "")); // 担保类型
        userInfo.put("ASSURE_DATE", baseUserInfo.getString("ASSURE_DATE", "")); // 担保期限

        // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        userInfo.put("REMOVE_TAG", baseUserInfo.getString("REMOVE_TAG", "0")); //

        userInfo.put("PRE_DESTROY_TIME", baseUserInfo.getString("PRE_DESTROY_TIME", "")); // 预销号时间
        userInfo.put("DESTROY_TIME", baseUserInfo.getString("DESTROY_TIME", "")); // 注销时间
        userInfo.put("REMOVE_EPARCHY_CODE", baseUserInfo.getString("REMOVE_EPARCHY_CODE", "")); // 注销地市
        userInfo.put("REMOVE_CITY_CODE", baseUserInfo.getString("REMOVE_CITY_CODE", "")); // 注销市县
        userInfo.put("REMOVE_DEPART_ID", baseUserInfo.getString("REMOVE_DEPART_ID", "")); // 注销渠道
        userInfo.put("REMOVE_REASON_CODE", baseUserInfo.getString("REMOVE_REASON_CODE", "")); // 注销原因
        userInfo.put("REMARK", baseUserInfo.getString("REMARK", "")); // 备注

        userInfo.put("RSRV_NUM1", baseUserInfo.getString("RSRV_NUM1", "")); // 预留数值1
        userInfo.put("RSRV_NUM2", baseUserInfo.getString("RSRV_NUM2", "")); // 预留数值2
        userInfo.put("RSRV_NUM3", baseUserInfo.getString("RSRV_NUM3", "")); // 预留数值3
        userInfo.put("RSRV_NUM4", baseUserInfo.getString("RSRV_NUM4", "")); // 预留数值4
        userInfo.put("RSRV_NUM5", baseUserInfo.getString("RSRV_NUM5", "")); // 预留数值5

        userInfo.put("RSRV_STR1", baseUserInfo.getString("RSRV_STR1", "")); // 预留字段1
        userInfo.put("RSRV_STR2", baseUserInfo.getString("RSRV_STR2", "")); // 预留字段2
        userInfo.put("RSRV_STR3", baseUserInfo.getString("RSRV_STR3", "")); // 预留字段3
        userInfo.put("RSRV_STR4", baseUserInfo.getString("RSRV_STR4", "")); // 预留字段4
        userInfo.put("RSRV_STR5", baseUserInfo.getString("RSRV_STR5", "")); // 预留字段5
        userInfo.put("RSRV_STR6", baseUserInfo.getString("RSRV_STR6", "")); // 预留字段6
        userInfo.put("RSRV_STR7", baseUserInfo.getString("RSRV_STR7", "")); // 预留字段7
        userInfo.put("RSRV_STR8", baseUserInfo.getString("RSRV_STR8", "")); // 预留字段8
        userInfo.put("RSRV_STR9", baseUserInfo.getString("RSRV_STR9", "")); // 预留字段9
        userInfo.put("RSRV_STR10", baseUserInfo.getString("RSRV_STR10", "")); // 预留字段10
        userInfo.put("RSRV_DATE1", baseUserInfo.getString("RSRV_DATE1", "")); // 预留日期1
        userInfo.put("RSRV_DATE2", baseUserInfo.getString("RSRV_DATE2", "")); // 预留日期2
        userInfo.put("RSRV_DATE3", baseUserInfo.getString("RSRV_DATE3", "")); // 预留日期3
        userInfo.put("RSRV_TAG1", baseUserInfo.getString("RSRV_TAG1", "")); // 预留标志1
        userInfo.put("RSRV_TAG2", baseUserInfo.getString("RSRV_TAG2", "")); // 预留标志2
        userInfo.put("RSRV_TAG3", baseUserInfo.getString("RSRV_TAG3", "")); // 预留标志3

        UserTradeData utd = new UserTradeData(userInfo);
        uca.setUser(utd);
        uca.setProductId(productId);
        uca.setBrandCode(baseUserInfo.getString("BRAND_CODE", "-1"));

        reqData.setUca(uca);
        // 把集团uca放到databus总线,用sn作为key取
        DataBusManager.getDataBus().setUca(uca);

    }

    protected void makUserAcctDay() throws Exception
    {

    }

    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();
        data.put("EPARCHY_CODE", baseCommInfo.getString("EPARCHY_CODE"));
    }

    protected void makInit(IData map) throws Exception
    {
        baseCommInfo.put("TRADE_TYPE_CODE", map.getString("TRADE_TYPE_CODE"));
        baseCommInfo.put("EPARCHY_CODE", map.getString("EPARCHY_CODE"));
    }

    protected String setTradeTypeCode() throws Exception
    {
        return baseCommInfo.getString("TRADE_TYPE_CODE");
    }

    public void chkTradeAfter() throws Exception
    {

    }

}
