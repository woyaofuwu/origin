
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateTISmsGroupUser extends CreateGroupUser
{
    private IData productParam;

    public CreateTISmsGroupUser()
    {

    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataTISmsPlatsvc();
    }

    private void infoRegDataTISmsPlatsvc() throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
        data.put("SERVICE_ID", "755"); // 服务编码
        data.put("SERV_CODE", productParam.getString("SERV_CODE", "1")); // 服务代码
        data.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId()); // 集团编码
        data.put("BIZ_TYPE_CODE", productParam.getString("BIZ_TYPE_CODE", "")); // 业务类型编码
        data.put("BIZ_CODE", productParam.getString("BIZ_CODE", "")); // 业务代码
        data.put("BIZ_ATTR", productParam.getString("BIZ_ATTR", "")); // 业务属性
        data.put("BIZ_NAME", productParam.getString("BIZ_NAME", "")); // 业务名称
        data.put("BIZ_IN_CODE", productParam.getString("SERV_CODE", "")); // 业务接入号
        data.put("BIZ_STATUS", productParam.getString("BIZ_STATUS", "")); // 业务状态
        data.put("BIZ_STATE_CODE", "A"); // 接入状态
        data.put("BIZ_PRI", productParam.getString("BIZ_PRI", "")); // 业务优先级
        data.put("AUTH_CODE", productParam.getString("AUTH_CODE", "")); // 业务接入鉴权方式
        data.put("USAGE_DESC", productParam.getString("USAGE_DESC", "")); // 业务使用方法描述
        data.put("INTRO_URL", productParam.getString("INTRO_URL", "")); // 业务介绍网址
        data.put("BILLING_TYPE", productParam.getString("BILLING_TYPE", "")); // 计费类型
        data.put("BILLING_MODE", productParam.getString("BILLING_MODE", "")); // 计费模式
        data.put("PRICE", productParam.getString("PRICE", "")); // 单价
        data.put("PRE_CHARGE", productParam.getString("PRE_CHARGE", "")); // 预付费标记
        data.put("CS_TEL", productParam.getString("CS_TEL", "")); // 客服电话
        data.put("CS_URL", productParam.getString("CS_URL", "")); // 客服URL
        data.put("ACCESS_MODE", productParam.getString("ACCESS_MODE", "")); // 访问模式
        data.put("ACCESS_NUMBER", productParam.getString("SERV_CODE", "")); // 访问号码
        data.put("SI_BASE_IN_CODE", productParam.getString("SIBASE_INCODE", "")); // SI基本接入号
        data.put("SI_BASE_IN_CODE_A", productParam.getString("SIBASE_INCODE_A", "")); // SI基本接入号属性
        data.put("EC_BASE_IN_CODE", productParam.getString("EC_BASE_IN_CODE", "")); // EC基本接入号
        data.put("EC_BASE_IN_CODE_A", data.getString("SI_BASE_IN_CODE_A", "")); // EC基本接入号属性
        data.put("MAX_ITEM_PRE_DAY", productParam.getString("MAX_ITEM_PRE_DAY", "")); // 每日下发最大条数
        data.put("MAX_ITEM_PRE_MON", productParam.getString("MAX_ITEM_PRE_MON", "")); // 每月下发最大条数
        data.put("DELIVER_NUM", productParam.getString("DELIVER_NUM", "")); // 限制下发次数

        data.put("FORBID_START_TIME_A", productParam.getString("FORBID_START_TIME_A", "")); // 不允许下发开始时间A
        data.put("FORBID_END_TIME_A", productParam.getString("FORBID_END_TIME_A", "")); // 不允许下发终止时间A
        data.put("FORBID_START_TIME_B", productParam.getString("FORBID_START_TIME_B", "")); // 不允许下发开始时间B
        data.put("FORBID_END_TIME_B", productParam.getString("FORBID_END_TIME_B", "")); // 不允许下发终止时间B
        data.put("FORBID_START_TIME_C", productParam.getString("FORBID_START_TIME_C", "")); // 不允许下发开始时间C
        data.put("FORBID_END_TIME_C", productParam.getString("FORBID_END_TIME_C", "")); // 不允许下发终止时间C
        data.put("FORBID_START_TIME_D", productParam.getString("FORBID_START_TIME_D", "")); // 不允许下发开始时间D
        data.put("FORBID_END_TIME_D", productParam.getString("FORBID_END_TIME_D", "")); // 不允许下发终止时间D
        data.put("IS_TEXT_ECGN", productParam.getString("IS_TEXT_ECGN", "")); // 是否支持短信正文签名
        data.put("DEFAULT_ECGN_LANG", productParam.getString("DEFAULT_ECGN_LANG", "")); // 缺省签名语言
        data.put("TEXT_ECGN_EN", productParam.getString("TEXT_ECGN_EN", "")); // 英文短信正文签名
        data.put("TEXT_ECGN_ZH", productParam.getString("TEXT_ECGN_ZH", "")); // 中文短信正文签名
        data.put("OPR_EFF_TIME", productParam.getString("OPR_EFF_TIME", "")); // 操作生效时间
        data.put("OPR_SEQ_ID", productParam.getString("OPR_SEQ_ID", "")); // 操作流水号
        data.put("OPER_STATE", "0"); // 操作状态
        data.put("ADMIN_NUM", productParam.getString("ADMIN_NUM", "")); // 管理员手机号码
        data.put("MAS_ID", productParam.getString("MAS_ID", "")); // MAS服务器标识
        data.put("FIRST_DATE", getAcceptTime()); // 首次同步时间
        data.put("PLAT_SYNC_STATE", productParam.getString("PLAT_SYNC_STATE", "")); // 平台同步状态

        data.put("START_DATE", getAcceptTime()); // 开始时间
        data.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
        data.put("REMARK", productParam.getString("REMARK", "")); // 备注
        data.put("RSRV_NUM1", productParam.getString("RSRV_NUM1", "")); // 预留数值1
        data.put("RSRV_NUM2", productParam.getString("RSRV_NUM2", "")); // 预留数值2
        data.put("RSRV_NUM3", productParam.getString("RSRV_NUM3", "")); // 预留数值3
        data.put("RSRV_NUM4", productParam.getString("RSRV_NUM4", "")); // 预留数值4
        data.put("RSRV_NUM5", productParam.getString("RSRV_NUM5", "")); // 预留数值5

        IDataset discnts = reqData.cd.getDiscnt();
        String spCode = "";
        if (IDataUtil.isNotEmpty(discnts))
        {
            for (int i = 0, size = discnts.size(); i < size; i++)
            {
                IData discnt = discnts.getData(i);
                spCode = spCode + "|" + TRADE_MODIFY_TAG.Add.getValue() + "~" + discnt.getString("ELEMENT_ID", "");
            }
        }
        data.put("RSRV_STR1", spCode); // SP_CODE

        data.put("RSRV_STR2", productParam.getString("RSRV_STR2", "")); // 预留字段2
        data.put("RSRV_STR3", productParam.getString("RSRV_STR3", "")); // 预留字段3
        data.put("RSRV_STR4", productParam.getString("RSRV_STR4", "")); // 预留字段4
        data.put("RSRV_STR5", productParam.getString("RSRV_STR5", "")); // 预留字段5
        data.put("RSRV_DATE1", productParam.getString("RSRV_DATE1", "")); // 预留日期1
        data.put("RSRV_DATE2", productParam.getString("RSRV_DATE2", "")); // 预留日期2
        data.put("RSRV_DATE3", productParam.getString("RSRV_DATE3", "")); // 预留日期3
        data.put("RSRV_TAG1", productParam.getString("PRODUCT_TYPE", "")); // 产品类型 0-旅游局；1-旅行团；2-景点；3-酒店

        data.put("RSRV_TAG2", productParam.getString("PRODUCT_TYPE", "")); // 专用发往旅信通平台报文的，产品变更的时候要传空值，为了不改变原有的值，增加该字段来发报文；

        data.put("RSRV_TAG3", productParam.getString("RSRV_TAG3", "0")); // 标识是否走服务开通 0-正常走服务开通模式 1-ADC平台 2-行业网关

        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 标识为新增
        data.put("INST_ID", SeqMgr.getInstId());

        addTradeGrpPlatsvc(data);
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String productId = reqData.getUca().getProductId();

        // 处理产品参数信息
        productParam = reqData.cd.getProductParamMap(productId);

        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        reqData.cd.putProductParamList(productId, new DatasetList()); // 不插user_attr表

        String userPasswd = productParam.getString("TEMP_PWD", "");
        String userId = reqData.getUca().getUserId();
        if (StringUtils.isNotEmpty(userPasswd))
        {
            userPasswd = Encryptor.fnEncrypt(userPasswd, userId.substring(userId.length() - 9));
        }

        productParam.put("RSRV_STR10", userPasswd);
        productParam.put("USER_PASSWD", userPasswd);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        tradeData.put("RSRV_STR1", productParam.getString("BIZ_CODE", "")); // 业务代码
        tradeData.put("RSRV_STR2", reqData.getUca().getCustGroup().getGroupId()); // 集团编码
        tradeData.put("RSRV_STR3", productParam.getString("SERV_CODE", "")); // 服务代码
        tradeData.put("RSRV_STR4", productParam.getString("SERV_CODE", "")); // 基本接入号（海南无）
        tradeData.put("RSRV_STR6", productParam.getString("ADMIN_PHONE", "")); // 联系电话
        tradeData.put("RSRV_STR7", productParam.getString("ADMIN_EMAIL", "")); // email
        tradeData.put("RSRV_STR8", productParam.getString("ADMIN_ADDRESS", "")); // 联系地址
        tradeData.put("RSRV_STR9", productParam.getString("LOGIN_NAME", "")); // 登陆名
        tradeData.put("RSRV_STR10", productParam.getString("RSRV_STR10", "")); // 密码
    }

    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR1", productParam.getString("BIZ_CODE", "")); // 业务代码
        map.put("RSRV_STR2", reqData.getUca().getCustGroup().getGroupId()); // 集团编码
        map.put("RSRV_STR3", productParam.getString("SERV_CODE", "")); // 服务代码
        map.put("RSRV_STR4", productParam.getString("SERV_CODE", "")); // 基本接入号（海南无）
        map.put("RSRV_STR6", productParam.getString("ADMIN_PHONE", "")); // 联系电话
        map.put("RSRV_STR7", productParam.getString("ADMIN_EMAIL", "")); // email
        map.put("RSRV_STR8", productParam.getString("ADMIN_ADDRESS", "")); // 联系地址
        map.put("RSRV_STR9", productParam.getString("LOGIN_NAME", "")); // 登陆名
        map.put("RSRV_STR10", productParam.getString("RSRV_STR10", "")); // 密码
        map.put("USER_PASSWD", productParam.getString("USER_PASSWD", "")); // 密码
    }
}
