
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;

public class CreateDatalineGroupUser extends CreateGroupUser
{
    protected CreateInternetGroupUserReqData reqData = null;

    /**
     * 构造函数
     */
    public CreateDatalineGroupUser()
    {

    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateInternetGroupUserReqData();
    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();
    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        actTradeDataline();
        actTradeDatalineAttr();
        setRegOtherInfo();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateInternetGroupUserReqData) getBaseReqData();

    }
    
    protected void setTradeBase() throws Exception
    {

        super.setTradeBase();

        IData map = bizData.getTrade();

        map.put("PF_WAIT", reqData.getPfWait());

    }

    protected void makReqData(IData map) throws Exception
    {

        super.makReqData(map);

        reqData.setInterData(map.getData("ATTRINTERNET"));

        reqData.setDataline(map.getData("DATALINE"));

        reqData.setCommonData(map.getDataset("COMMON_DATA"));
        
        reqData.setPfWait(map.getInt("PF_WAIT"));
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

    private void actTradeDatalineAttr() throws Exception
    {
        IData dataline = reqData.getDataline();
        IDataset attrDataList = reqData.getCommonData();
        IDataset dataset = new DatasetList();

        IData userData = new DataMap();
        userData.put("USER_ID", reqData.getUca().getUserId());
        userData.put("START_DATE", getAcceptTime());
        userData.put("SHEET_TYPE", "4");

        dataset = DatalineUtil.addTradeUserDataLineAttr(attrDataList, dataline, userData);

        super.addTradeDataLineAttr(dataset);
    }

    private void actTradeDataline() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData dataline = reqData.getDataline();
        IData internet = reqData.getInterData();

        IData userData = new DataMap();
        userData.put("USER_ID", reqData.getUca().getUserId());
        userData.put("START_DATE", getAcceptTime());
        userData.put("SHEET_TYPE", "4");
        
        //add by REQ201802260030 关于集客业务支撑流程式快速开通功能需求
        if(dataline!=null&&dataline.getString("LINEOPENTAG","").equals("1")){
        	dataline.put("RSRV_NUM1", dataline.getString("LINEOPENTAG",""));
        }
        dataset = DatalineUtil.addTradeUserDataLine(dataline, internet, userData);

        super.addTradeDataLine(dataset);
    }

    /**
     * 其它台帐处理
     */
    public void setRegOtherInfo() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        IData internet = reqData.getInterData();

        if (null != internet && internet.size() > 0)
        {
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("RSRV_VALUE_CODE", "N001");
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            data.put("START_DATE", getAcceptTime());
            data.put("END_DATE", SysDateMgr.getTheLastTime());

            data.put("RSRV_VALUE", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")) + 1);
            // 专线
            data.put("RSRV_STR1", internet.getString("pam_NOTIN_LINE_NUMBER"));
            // 专线带宽
            data.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND"));
            // 专线价格
            data.put("RSRV_STR3", internet.getString("pam_NOTIN_LINE_PRICE"));
            // 安装调试费
            data.put("RSRV_STR4", internet.getString("pam_NOTIN_INSTALLATION_COST"));
            // 专线一次性通信服务费
            data.put("RSRV_STR5", internet.getString("pam_NOTIN_ONE_COST"));
            // 业务标识
            data.put("RSRV_STR7", internet.getString("pam_NOTIN_PRODUCT_NUMBER"));
            //软件应用服务费 add by chenzg@20180620
            data.put("RSRV_STR8", internet.getString("pam_NOTIN_SOFTWARE_PRICE"));	
            // 专线实例号
            data.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));
            //网络技术支持服务费 add by chenzg@20180620
            data.put("RSRV_STR10", internet.getString("pam_NOTIN_NET_PRICE"));	

            String groupCity = UAreaInfoQry.getAreaNameByAreaCode(internet.getString("pam_NOTIN_GROUP_CITY"));
            // 集团客户所在市县
            data.put("RSRV_STR11", groupCity);

            String aCity = UAreaInfoQry.getAreaNameByAreaCode(internet.getString("pam_NOTIN_A_CITY"));
            // A端所在市县
            data.put("RSRV_STR12", aCity);

            String zCity = UAreaInfoQry.getAreaNameByAreaCode(internet.getString("pam_NOTIN_Z_CITY"));
            // Z端所在市县
            data.put("RSRV_STR13", zCity);

            // 集团客户所在市县
            data.put("RSRV_STR14", internet.getString("pam_NOTIN_GROUP_CITY"));
            // A端所在市县
            data.put("RSRV_STR15", internet.getString("pam_NOTIN_A_CITY"));
            // Z端所在市县
            data.put("RSRV_STR16", internet.getString("pam_NOTIN_Z_CITY"));

            // 集团所在市县分成比例
            data.put("RSRV_STR17", internet.getString("pam_NOTIN_GROUP_PERCENT"));
            // A端所在市县分成比例
            data.put("RSRV_STR18", internet.getString("pam_NOTIN_A_PERCENT"));
            // Z端所在市县分成比例
            data.put("RSRV_STR19", internet.getString("pam_NOTIN_Z_PERCENT"));
            
            // 服务开通
            data.put("IS_NEED_PF", "1");

            dataset.add(data);

            addTradeOther(dataset);
        }
    }

    protected void makUca(IData map) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUcaByCustIdForGrp(map);

        IData baseUserInfo = map;

        if (IDataUtil.isNull(baseUserInfo))
        {
            baseUserInfo = new DataMap();
        }

        // 得到数据
        String productId = baseUserInfo.getString("PRODUCT_ID");

        IData userInfo = new DataMap();
        userInfo.put("USER_ID", map.getString("USER_ID"));// 用户标识
        userInfo.put("CUST_ID", map.getString("CUST_ID")); // 归属客户标识
        userInfo.put("USECUST_ID", baseUserInfo.getString("USECUST_ID", map.getString("CUST_ID"))); // 使用客户标识：如果不指定，默认为归属客户标识

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

        userInfo.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER"));// 必须由前台传,对于第3放接口,需要根据in_mode_code后台构造sn

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

}
