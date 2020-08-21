
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateSuperTeleGroupUser extends CreateGroupUser
{
    private IData superTeleParam = new DataMap();// 总机参数

    private IData mebSvcData = new DataMap();

    /**
     * 生成其它台帐数据
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 登记VPN用户信息
        infoRegDataSuperTeleVpn();

        mebSvcData.put("TRADE_ID", getTradeId());
    }

    /**
     * 登记VPN用户信息
     * 
     * @throws Exception
     */
    public void infoRegDataSuperTeleVpn() throws Exception
    {
        IData vpnData = super.infoRegDataVpn();

        vpnData.put("SCP_CODE", "111"); // SCP代码-固定值
        vpnData.put("MAX_USERS", "1000"); // 集团可拥有的最大用户数
        vpnData.put("VPN_SCARE_CODE", ""); // 集团范围属性-通过数据库配置获取
        vpnData.put("VPMN_TYPE", "3");// VPN集团类型 0:本地集团 1:全省集团 2:全国集团 3:本地化省级集团 [特殊处理]
        vpnData.put("VPN_NO", reqData.getUca().getSerialNumber());// VPN集团号

        super.addTradeVpn(vpnData);
    }

    /**
     * 构建调用成员服务的数据
     * 
     * @param map
     * @throws Exception
     */
    public void makMebSvcData(IData map) throws Exception
    {
        mebSvcData.put("USER_ID", reqData.getUca().getUser().getUserId()); // 集团用户ID
        mebSvcData.put("GRP_SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber()); // 集团服务号码
        mebSvcData.put("SERIAL_NUMBER", superTeleParam.getString("E_SERIAL_NUMBER")); // 总机服务号码
        mebSvcData.put("MEM_ROLE_B", "2"); // 关系代码2-总机
        mebSvcData.put("SHORT_CODE", superTeleParam.getString("EXCHANGE_SHORT_SN")); // 总机短号
        mebSvcData.put("PRODUCT_ID", superTeleParam.getString("E_PRODUCT_ID")); // 总机产品
        mebSvcData.put("MEB_MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        mebSvcData.put("TRADE_ID", getTradeId());

        map.put("MEB_SVC_DATA", mebSvcData);
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // 构建总机参数数据
        makSuperTeleParam();

        // 构建总机号码数据,为调用成员服务准备数据
        makMebSvcData(map);
    }

    /**
     * 设置总机参数信息
     * 
     * @throws Exception
     */
    public void makSuperTeleParam() throws Exception
    {

        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        superTeleParam.put("E_SERIAL_NUMBER", productParam.getString("EXCHANGETELE_SN", "")); // 总机服务号码
        superTeleParam.put("E_USER_ID", productParam.getString("E_USER_ID", "")); // 总机用户标识
        superTeleParam.put("EXCHANGE_SHORT_SN", productParam.getString("EXCHANGE_SHORT_SN", "")); // 总机短号
        superTeleParam.put("E_CUST_ID", productParam.getString("E_CUST_ID", "")); // 总机客户标识
        superTeleParam.put("E_CUST_NAME", productParam.getString("E_CUST_NAME", "")); // 总机客户名称
        superTeleParam.put("E_EPARCHY_CODE", productParam.getString("E_EPARCHY_CODE", "")); // 总机归属地州
        superTeleParam.put("E_CITY_CODE", productParam.getString("E_CITY_CODE", "")); // 总机归属地州
        superTeleParam.put("E_PRODUCT_ID", productParam.getString("E_PRODUCT_ID", "")); // 总机产品标识
        superTeleParam.put("E_BRAND_CODE", productParam.getString("E_BRAND_CODE", "")); // 总机品牌编码

        superTeleParam.put("E_AdminInfo", productParam.getString("E_AdminInfo", "")); // 管理员信息
        superTeleParam.put("E_AdminPhone", productParam.getString("E_AdminPhone", "")); // 管理员电话
        superTeleParam.put("E_AdminAddr", productParam.getString("E_AdminAddr", "")); // 联系地址
        superTeleParam.put("U_VPMN", productParam.getString("VPMN_SN", "")); // 对应VPMN
        superTeleParam.put("REMARK", productParam.getString("REMARK", "")); // 备注

        // 不能为空的校验
        /*
         * for (Iterator<String> iterator = superTeleParam.keySet().iterator(); iterator.hasNext();) { String key =
         * iterator.next(); if (StringUtils.isEmpty(superTeleParam.getString(key))) {
         * CSAppException.apperr(ParamException.CRM_PARAM_335); } }
         */
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        tradeData.put("RSRV_STR3", superTeleParam.getString("E_SERIAL_NUMBER", "")); // 总机号码
        tradeData.put("RSRV_STR4", superTeleParam.getString("E_USER_ID", "")); // 总机用户标识
        tradeData.put("RSRV_STR5", superTeleParam.getString("EXCHANGE_SHORT_SN", "")); // 总机短号码
        tradeData.put("RSRV_STR6", superTeleParam.getString("E_AdminAddr", "")); // 联系地址
        tradeData.put("RSRV_STR7", superTeleParam.getString("E_AdminInfo", "")); // 海南管理员信息
        tradeData.put("RSRV_STR8", superTeleParam.getString("E_AdminPhone", "")); // 海南管理员电话
        tradeData.put("RSRV_STR9", getTradeId()); // trade_id
        tradeData.put("RSRV_STR10", superTeleParam.getString("U_VPMN", "")); // 对应VPMN
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR3", superTeleParam.getString("E_SERIAL_NUMBER", "")); // 总机号码
        map.put("RSRV_STR4", superTeleParam.getString("E_USER_ID", "")); // 总机用户标识
        map.put("RSRV_STR5", superTeleParam.getString("EXCHANGE_SHORT_SN", "")); // 总机短号码
        map.put("RSRV_STR6", superTeleParam.getString("E_AdminAddr", "")); // 联系地址
        map.put("RSRV_STR7", superTeleParam.getString("E_AdminInfo", "")); // 海南管理员信息
        map.put("RSRV_STR8", superTeleParam.getString("E_AdminPhone", "")); // 海南管理员电话
        map.put("RSRV_STR9", getTradeId()); // trade_id
        map.put("RSRV_STR10", superTeleParam.getString("U_VPMN", "")); // 对应VPMN
    }

}
