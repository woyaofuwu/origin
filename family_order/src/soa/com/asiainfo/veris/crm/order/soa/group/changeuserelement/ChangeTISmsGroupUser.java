
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeTISmsGroupUser extends ChangeUserElement
{
    private IData productParam; // 产品个性化参数

    private String operState;// 操作类型

    private IData modifyParamMap = new DataMap();// 要修改的参数

    private boolean modifyUser;

    public ChangeTISmsGroupUser()
    {

    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        // 处理产品参数
        dealProductParam();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        if (StringUtils.isNotBlank(operState))
        {
            // 查询TF_F_USER_GRP_PLATSVC表
            String userId = reqData.getUca().getUserId();
            IData inparam = new DataMap();
            inparam.put("USER_ID", reqData.getUca().getUserId());
            IData data = UserGrpPlatSvcInfoQry.getUserAttrByUserId(userId);
            if (IDataUtil.isEmpty(data))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "旅信（游）通产品变更：查询用户平台服务信息表失败！");
            }

            if (operState.equals("71")) // 暂停
            {
                data.put("BIZ_STATE_CODE", "N");
                data.put("OPER_STATE", "04");
                data.put("RSRV_STR2", "71");
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                data.put("RSRV_TAG3", "0");// 服务开通标识 0 正常走服务开通模式 1 ADC平台 2 行业网关
                addTradeGrpPlatsvc(data);
            }
            else if (operState.equals("72")) // 恢复
            {
                data.put("BIZ_STATE_CODE", "A");
                data.put("OPER_STATE", "05");
                data.put("RSRV_STR2", "72");
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                data.put("RSRV_TAG3", "0");// 服务开通标识 0 正常走服务开通模式 1 ADC平台 2 行业网关
                addTradeGrpPlatsvc(data);
            }
            else if (operState.equals("05")) // 修改
            {

                if (IDataUtil.isNotEmpty(modifyParamMap))
                {
                    // 修改platsvc表
                    data.put("OPER_STATE", "08");
                    data.put("RSRV_STR2", "05");
                    infoRegDataTISmsPlatSvc(data);
                }
            }
        }
    }

    private void dealProductParam() throws Exception
    {
        if (operState.equals("05"))
        {
            String old_product_param = productParam.getString("OLD_PRODUCT_PARAM");
            IDataset params = new DatasetList(old_product_param); // IDataUtil.getDataset(productParam,
            // "OLD_PRODUCT_PARAM");

            Iterator<String> iterator = productParam.keySet().iterator();
            while (iterator.hasNext())
            {
                String key = iterator.next();
                boolean isExist = false;
                for (int i = 0, size = params.size(); i < size; i++)
                {
                    IData param = params.getData(i);
                    if (param.getString("ATTR_CODE").equals(key))
                    {
                        if (!param.getString("ATTR_VALUE", "").equals(productParam.getString(key)))
                        {
                            modifyParamMap.put(key, productParam.getString(key, ""));
                        }
                        isExist = true;
                        break;
                    }
                }
                if (!isExist && !key.equals("OLD_PRODUCT_PARAM"))
                {
                    String value = productParam.getString(key, "");
                    modifyParamMap.put(key, value);
                }
            }
        }

        // 联系电话
        if (StringUtils.isNotBlank(modifyParamMap.getString("ADMIN_PHONE", "")))
        {
            productParam.put("RSRV_STR6", IDataUtil.getAndDelColValueFormIData(modifyParamMap, "ADMIN_PHONE"));
            modifyUser = true;
        }
        // email
        if (StringUtils.isNotBlank(modifyParamMap.getString("ADMIN_EMAIL", "")))
        {
            productParam.put("RSRV_STR7", IDataUtil.getAndDelColValueFormIData(modifyParamMap, "ADMIN_EMAIL"));
            modifyUser = true;
        }
        // 联系地址
        if (StringUtils.isNotBlank(modifyParamMap.getString("ADMIN_ADDRESS", "")))
        {
            productParam.put("RSRV_STR8", IDataUtil.getAndDelColValueFormIData(modifyParamMap, "ADMIN_ADDRESS"));
            modifyUser = true;
        }
    }

    private void infoRegDataTISmsPlatSvc(IData data) throws Exception
    {
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); // 标识为修改

        data.put("SERV_CODE", productParam.getString("SERV_CODE", "1")); // 服务代码
        data.put("BIZ_TYPE_CODE", productParam.getString("BIZ_TYPE_CODE", "")); // 业务类型编码
        data.put("BIZ_CODE", productParam.getString("BIZ_CODE", "")); // 业务代码
        data.put("BIZ_ATTR", productParam.getString("BIZ_ATTR", "")); // 业务属性
        data.put("BIZ_NAME", productParam.getString("BIZ_NAME", "")); // 业务名称
        data.put("BIZ_IN_CODE", productParam.getString("SERV_CODE", "")); // 业务接入号
        data.put("BIZ_STATUS", productParam.getString("BIZ_STATUS", "")); // 业务状态
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
        data.put("OPER_STATE", data.getString("OPER_STATE", "08")); // 传给服务开通的操作代码
        data.put("ADMIN_NUM", productParam.getString("ADMIN_NUM", "")); // 管理员手机号码
        data.put("MAS_ID", productParam.getString("MAS_ID", "")); // MAS服务器标识
        data.put("PLAT_SYNC_STATE", productParam.getString("PLAT_SYNC_STATE", "")); // 平台同步状态

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

        data.put("RSRV_STR2", productParam.getString("OPER_CODE", "")); // 页面上的 OPER_CODE 05 修改，71 暂停 ，72 恢复
        data.put("RSRV_STR3", productParam.getString("RSRV_STR3", "")); // 预留字段3
        data.put("RSRV_STR4", productParam.getString("RSRV_STR4", "")); // 预留字段4
        data.put("RSRV_STR5", productParam.getString("RSRV_STR5", "")); // 预留字段5
        data.put("RSRV_DATE1", productParam.getString("RSRV_DATE1", "")); // 预留日期1
        data.put("RSRV_DATE2", productParam.getString("RSRV_DATE2", "")); // 预留日期2
        data.put("RSRV_DATE3", productParam.getString("RSRV_DATE3", "")); // 预留日期3

        // data.put("RSRV_TAG1", productParam.getString("PRODUCT_TYPE", "")); //产品类型 0-旅游局；1-旅行团；2-景点；3-酒店

        data.put("RSRV_TAG2", ""); // PRODUCT_TYPE 专用发往旅信通平台报文的，产品变更的时候要传空值，为了不改变原有的值，增加该字段来发报文；
        data.put("RSRV_TAG3", productParam.getString("RSRV_TAG3", "0")); // 标识是否走服务开通 0 正常走服务开通模式 1 ADC平台 2 行业网关

        addTradeGrpPlatsvc(data);
    }

    public IData getTradeUserExtendData() throws Exception
    {
        IData userInfo = super.getTradeUserExtendData();

        if (modifyUser)
        {
            userInfo.put("RSRV_STR6", productParam.getString("RSRV_STR6", ""));
            userInfo.put("RSRV_STR7", productParam.getString("RSRV_STR7", ""));
            userInfo.put("RSRV_STR8", productParam.getString("RSRV_STR8", ""));
            userInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }

        return userInfo;
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

        operState = productParam.getString("OPER_CODE", "");
        productParam.put("RSRV_STR9", reqData.getUca().getSerialNumber()); // 发往旅信通平台的报文字段:login_name, 不插user表
    }

}
