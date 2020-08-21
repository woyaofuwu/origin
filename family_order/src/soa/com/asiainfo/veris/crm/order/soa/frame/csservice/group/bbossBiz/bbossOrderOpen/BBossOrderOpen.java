
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderOpen;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.PoTradePlusInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/*
 * 工单开通，如果成员业务，本省不保存报文中的信息，直接返回开通成功，否则会将报文信息解析入表，入表后返回开通成功，此时返回的是瞬时响应报文， 用户需要在工单查询页面处理后才会将工单开通的结果归档给集团公司，此时返回的是归档报文
 */
public class BBossOrderOpen
{

    /*
     * @description 处理BBOSS向省BOSS发的工单开通业务
     * @author xunyl
     * @date 2013-07-25
     */
    public static IDataset dealOrderOpen(IData map) throws Exception
    {
        // 1- 定义返回对象
        IDataset result = new DatasetList();

        // 2- 获取业务操作类型(业务操作类型为变更成员时，不入工单表，直接返回成功)
        String operaType = map.getString("OPERA_TYPE");
        if ("6".equals(operaType))
        {
            result = orderOpenForChgMeb(map);
            return result;
        }

        // 3- 订单信息入表
        result = saveOrderInfo(map);

        // 4- 业务开通和业务取消需要反馈EC竣工通知
        rspEcFinishMess(map);

        // 5- 返回工单开通结果
        return result;
    }

    /*
     * @description 产品参数信息的特殊情况处理
     * @author xunyl
     * @date 2013-07-25
     * @remark 典型场景：企业飞信下发开通工单时会将所有的配合省信息一起发送下来，落地方只要接收本省的信息即可，因此它省信息需要过滤
     */
    protected static void dealSpecialCharacter(IDataset ProductSpecCharacterNumber, IDataset CharacterValue, IDataset CGroup) throws Exception
    {
        for (int i = 0; i < ProductSpecCharacterNumber.size(); i++)
        {
            if (null == ProductSpecCharacterNumber.get(i))
            {
                continue;
            }
            String curNumber = ProductSpecCharacterNumber.get(i).toString();
            String curValue = CharacterValue.get(i).toString();
            String curCGroup = CGroup.get(i).toString();
            if ("910602001".equals(curNumber) && !ProvinceUtil.getProvinceCodeGrpCorp().equals(curValue))
            {
                if (!"".equals(curCGroup))
                {
                    for (int j = i + 1; j < ProductSpecCharacterNumber.size(); j++)
                    {
                        if (curCGroup.equals(CGroup.get(j)))
                        {
                            ProductSpecCharacterNumber.set(j, null);
                        }
                    }
                }
                ProductSpecCharacterNumber.set(i, null);
            }
        }
    }

    /*
     * @descripiton 处理操作类型为变更成员的工单开通业务
     * @author xunyl
     * @date 2013-07-25
     */
    protected static IDataset orderOpenForChgMeb(IData map) throws Exception
    {
        // 1- 拼装开通成功报文
        IData message = new DataMap();
        String provinceCode = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_INFO", "HNAN", CSBizBean.getTradeEparchyCode());
        message.put("PROVINCE_CODE", provinceCode);
        message.put("IN_MODE_CODE", "0");
        message.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        message.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        message.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        message.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        message.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码此密码由BOSS制定，接入渠道必填
        message.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        message.put("ROUTETYPE", "00");
        message.put("ROUTEVALUE", "000");
        message.put("KIND_ID", "BIP4B256_T4101033_0_0");
        message.put("ORDER_NO", map.getString("ORDER_NO"));
        message.put("RSPCODE", "00");
        message.put("RSPDESC", "00");
        message.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

        // 2- 往一级BOSS接口发送报文
        IDataset callResult = IBossCall.dealInvokeUrl("BIP4B256_T4101033_0_0", "IBOSS", message);
        if (IDataUtil.isEmpty(callResult))
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        }
        if (!"0000".equals(callResult.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_349, callResult.getData(0).getString("X_RSPCODE", ""), callResult.getData(0).getString("X_RSPDESC", ""));
        }

        // 3- 处理返回结果
        IData result = new DataMap();
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result.put("RSP_CODE", "00");
        result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        return IDataUtil.idToIds(result);
    }

    protected static void rspEcFinishMess(IData map) throws Exception
    {
        // 1- 拼装开通成功报文
        IData message = new DataMap();
        String provinceCode = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_INFO", "HNAN", CSBizBean.getTradeEparchyCode());
        message.put("PROVINCE_CODE", provinceCode);
        message.put("IN_MODE_CODE", "0");
        message.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        message.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        message.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        message.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        message.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码此密码由BOSS制定，接入渠道必填
        message.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        message.put("ROUTETYPE", "00");
        message.put("ROUTEVALUE", "000");
        message.put("KIND_ID", "BIP4B256_T4101033_0_0");
        message.put("ORDER_NO", map.getString("ORDER_NO"));
        message.put("RSPCODE", "00");
        message.put("RSPDESC", "00");
        message.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));

        // 开通工单中拼写产品参数
        setProductParam(map, message);

        // 2- 往一级BOSS接口发送报文
        IDataset callResult = IBossCall.dealInvokeUrl("BIP4B256_T4101033_0_0", "IBOSS", message);
        if (callResult == null)
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        }
        if (!"0000".equals(callResult.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_349, callResult.getData(0).getString("X_RSPCODE", ""), callResult.getData(0).getString("X_RSPDESC", ""));
        }

    }

    /*
     * @description 订单信息入表
     * @author xunyl
     * @date 2013-07-25
     */
    protected static IDataset saveOrderInfo(IData map) throws Exception
    {
        // 1- 基本订购信息入表
        IData baseInfo = new DataMap();
        baseInfo.put("TRADE_ID", SeqMgr.getTradeId()); // 业务编码
        baseInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); // 受理月份
        baseInfo.put("ACCEPT_DATE", SysDateMgr.getSysDate()); // 受理时间
        baseInfo.put("PRODUCTORDERNUMBER", map.getString("ORDER_NO")); // 产品订单号
        baseInfo.put("PRODUCTSPECNUMBER", map.getString("RSRV_STR10")); // 产品规格编号
        baseInfo.put("ACCESSNUMBER", map.getString("RSRV_STR5")); // 产品关键号码
        baseInfo.put("PRIACCESSNUMBER", map.getString("RSRV_STR6")); // 产品附件号码
        baseInfo.put("LINKMAN", map.getString("CONTACTNAME")); // 联系人
        baseInfo.put("CONTACTPHONE", map.getString("CONTACT_PHONE")); // 联系电话
        baseInfo.put("DESCRIPTION", map.getString("DESCRIPTION")); // 产品描述
        baseInfo.put("SERVICELEVELID", map.getString("RSRV_STR7")); // 服务开通等级ID
        baseInfo.put("TERMINALCONFIRM", map.getString("RSRV_STR8")); // 是否需要二次确认
        baseInfo.put("OPERATIONSUBTYPEID", map.getString("OPERA_TYPE")); // 产品级业务操作
        baseInfo.put("TRADE_STATE", "0"); // 操作类型
        baseInfo.put("RSRV_STR5", map.getString("CUSTOMER_NUMBER")); // 集团客户编码 v1.1.6添加
        Dao.insert("TF_B_POTRADE", baseInfo,Route.getJourDb(BizRoute.getRouteId()));

        // 2- 产品参数信息入表
        IDataset ProductSpecCharacterNumber = IDataUtil.getDataset("RSRV_STR1", map);// 属性编码
        IDataset CharacterValue = IDataUtil.getDataset("RSRV_STR2", map);// 属性值
        IDataset Name = IDataUtil.getDataset("PROPERTY_NAME", map);// 属性名称
        IDataset Action = IDataUtil.getDataset("ACTION", map);// 操作类型
        IDataset CGroup = IDataUtil.getDataset("CHARACTER_GROUP", map);// 属性组
        // 2-1 特殊情况处理
        dealSpecialCharacter(ProductSpecCharacterNumber, CharacterValue, CGroup);
        // 2-2 参数信息入表
        for (int i = 0; i < ProductSpecCharacterNumber.size(); i++)
        {
            if (null == ProductSpecCharacterNumber.get(i))
            {
                continue;
            }
            String curNumber = ProductSpecCharacterNumber.get(i).toString();
            String curName = Name.get(i).toString();
            String curValue = CharacterValue.get(i).toString();
            String curAction = Action.get(i).toString();
            String curCGroup = CGroup.get(i).toString();

            IData productPlusInfo = new DataMap();
            productPlusInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); // 受理月
            productPlusInfo.put("PRODUCTORDERNUMBER", map.getString("ORDER_NO")); // 定单号
            productPlusInfo.put("PRODUCTSPECCHARACTERNUMBER", curNumber); // 产品属性代码
            productPlusInfo.put("CHARACTERVALUE", curValue); // 属性值
            productPlusInfo.put("NAME", curName); // 属性名
            productPlusInfo.put("ACTION", curAction);// 属性操作
            productPlusInfo.put("CHARACTERGROUP", curCGroup);// 属性组
            Dao.insert("TF_B_POTRADEPLUS", productPlusInfo,Route.getJourDb(BizRoute.getRouteId()));
        }

        // 3- 处理返回结果
        IData result = new DataMap();
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result.put("RSP_CODE", "00");
        result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        return IDataUtil.idToIds(result);
    }

    /*
     * @description 帅选工单阶段反馈的属性
     * @author xunyl
     * @date 2014-04-14
     */
    protected static IDataset selOrderOpenRspParam(IDataset bbossAttrInfoList) throws Exception
    {
        // 1- 定义返回变量
        IDataset rspParamInfoList = new DatasetList();

        // 2- 如果产品对应的参数为空则直接退出
        if (IDataUtil.isEmpty(bbossAttrInfoList))
        {
            return rspParamInfoList;
        }

        // 3- 帅选VISIBLE为工单开通阶段的产品参数,自定义工单开通阶段的操作类型为98
        for (int i = 0; i < bbossAttrInfoList.size(); i++)
        {
            IData bbossAttrInfo = bbossAttrInfoList.getData(i);
            if (GrpCommonBean.nullToString(bbossAttrInfo.getString("VISIBLE")).contains("98"))
            {
                rspParamInfoList.add(bbossAttrInfo);
            }

        }

        // 4- 返回工单阶段反馈属性
        return rspParamInfoList;
    }

    /*
     * @description 开通工单中拼写产品参数
     * @author xunyl
     * @date 2014-04-14
     */
    protected static void setProductParam(IData map, IData message) throws Exception
    {
        // 1- 获取产品规格编号
        String productSpecNumber = map.getString("RSRV_STR10");

        // 2- 根据产品规格编号和操作编号查询是否有对应的产品参数需要在开通工单环节反馈
        IDataset bbossAttrInfoList = BBossAttrQry.qryBBossAttrByPospecBiztype(productSpecNumber, "1");
        IDataset rspParamInfoList = selOrderOpenRspParam(bbossAttrInfoList);

        // 3- 没有对应的产品参数需要反馈则直接退出
        if (IDataUtil.isEmpty(rspParamInfoList))
        {
            return;
        }

        // 4- 遍历需要反馈的产品参数，如果为属性组则意味着需要根据工单中下发的属性组来决定反馈多少组属性
        IDataset paramCodeList = new DatasetList();
        IDataset paramNameList = new DatasetList();
        IDataset paramValueList = new DatasetList();
        IDataset cgroupList = new DatasetList();
        for (int i = 0; i < rspParamInfoList.size(); i++)
        {
            IData rspParamInfo = rspParamInfoList.getData(i);
            if (StringUtils.isEmpty(rspParamInfo.getString("GROUPATTR")))
            {
                paramCodeList.add(rspParamInfo.getString("ATTR_CODE"));
                paramNameList.add(rspParamInfo.getString("ATTR_NAME"));
                paramValueList.add(rspParamInfo.getString("DEFAULT_VALUE"));
                cgroupList.add("");
            }
            else
            {
                String productSpecCharacterNum = rspParamInfo.getString("GROUPATTR").split("_")[0];
                String productOrderNum = map.getString("ORDER_NO");
                IDataset poTradePlusInfoList = PoTradePlusInfoQry.qryPoTradePlus(productOrderNum, productSpecCharacterNum);
                if (IDataUtil.isEmpty(poTradePlusInfoList))
                {
                    return;
                }
                for (int j = 0; j < poTradePlusInfoList.size(); j++)
                {
                    paramCodeList.add(rspParamInfo.getString("ATTR_CODE"));
                    paramNameList.add(rspParamInfo.getString("ATTR_NAME"));
                    paramValueList.add(rspParamInfo.getString("DEFAULT_VALUE"));
                    cgroupList.add(poTradePlusInfoList.getData(j).getString("CHARACTERGROUP"));
                }
            }
        }

        // 5- 拼写返回的参数
        message.put("CHARACTER_ID", paramCodeList);
        message.put("CHARACTER_VALUE", paramValueList);
        message.put("CHARACTER_NAME", paramNameList);
        message.put("CHARACTER_GROUP", cgroupList);

    }
}
