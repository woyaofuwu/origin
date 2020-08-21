
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.synBBossOrderState;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss.OrderOpenDao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.SendDataToEsopBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.GrpIntf;

/**
 * 输入数据处理类
 * 
 * @author liuzz
 * @date 2012-1-9
 */
public class SynBbossOrderState
{
    /*
     * @descripiton 配合省协助工单处理
     * @author xunyl date 2013-11-01
     */
    public static void dealAssistOrder(IData map) throws Exception
    {

        // 查询用户的存在状态
        String merchOfferId = map.getString("RSRV_STR2", "");// 商品订购关系ID

        boolean isTradeExist = TradeGrpMerchInfoQry.qryMerchOnlineInfoByMerchOfferId(merchOfferId, null).size() > 0 ? true : false;

        boolean isUserExist = GrpIntf.isUserExistBossGrp(merchOfferId);

        if (!isTradeExist || !isUserExist)
        {
            // 先注释测试
            // // 设置反向标记
            // map.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记 1代表是反向接口
            //
            // // 添加用户路由(后台生成虚拟号码用)
            // map.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            //
            // // 根据商品操作编号和用户的存在状态分别调用不同的服务进行处理
            // CreateBBossUserSVC crtBboss = new CreateBBossUserSVC();
            // crtBboss.crtOrder(map);
        }

        String productOperType = "";
        IDataset productOperList = map.getDataset("RSRV_STR14");// 产品操作类型
        if (IDataUtil.isNotEmpty(productOperList))
        {
            IDataset operTypeList = IDataUtil.modiIDataset(productOperList.get(0), "RSRV_STR14");
            productOperType = operTypeList.get(0).toString();
        }

        if ("14".equals(productOperType))
        {
            map.put("STATE_TYPE", "1000");// 同步状态(配合省协助预受理工单落地)
            map.put("PROVCPRT_TYPE", "1");// 协助工单类型
            map.put("NOTES", "配合省协助预受理工单落地");// 备注
        }
        else if ("15".equals(productOperType))
        {
            map.put("STATE_TYPE", "1001");// 同步状态(配合省协助受理工单落地)
            map.put("PROVCPRT_TYPE", "2");
            map.put("NOTES", "配合省协助受理工单落地");// 备注
        }
        else
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_40);
        }

        map.put("IF_PROVCPRT", "1");// 是否协助工单
        map.put("POORDER_NUMBER", map.getString("SUBSCRIBE_ID", ""));// 商品订单号
        map.put("SYN_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 同步时间
        map.put("PRODUCT_ID", map.getString("PSUBSCRIBE_ID", ""));// BBOSS生成的产品订单号
        map.put("CUSTOMER_NUMBER", map.getString("EC_SERIAL_NUMBER", ""));
    }

    public static IData dealAttr(String SeqId, String infoTag, String infoType, String offeringId, int subInfoType, String attrCode, String attrValue, String attrName) throws Exception
    {
        IData map = new DataMap();
        map.put("SYNC_SEQUENCE", SeqId);
        map.put("INFO_TAG", infoTag);
        map.put("INFO_TYPE", infoType);
        map.put("ORDERING_ID", offeringId);
        map.put("SUB_INFO_TYPE", subInfoType);
        map.put("ATTR_CODE", attrCode);
        map.put("ATTR_VALUE", attrValue);
        map.put("ATTR_OPER", "0");
        map.put("ATTR_NAME", attrName);
        return map;
    }

    /**
     * @version 20120109
     * @modify weixb3
     * @date 2013/10/18
     */
    public static IDataset insetBbossOrderState(IData data) throws Exception
    {

        IData result = new DataMap();
        IDataset resultSet = new DatasetList();

        String poOrderNumber = data.getString("POORDER_NUMBER");
        IDataset tradeInfos = TradeGrpMerchInfoQry.qryGrpMerchByMerchOrderIdUnionTrade(poOrderNumber);

        IData map = new DataMap();
        String SeqId = SeqMgr.getSYnTradeIdForGrp();// 工单同步序列
        data.put("SYNC_SEQUENCE", SeqId);
        map.put("SYNC_SEQUENCE", SeqId);

        // 跨省工单状态信息
        if (null != tradeInfos && IDataUtil.isNotEmpty(tradeInfos))
        {
            IData trade_temp = tradeInfos.getData(0);
            map.put("TRADE_ID", trade_temp.getString("TRADE_ID", ""));
            map.put("EC_NAME", trade_temp.getString("CUST_NAME", ""));
            map.put("MERCH_SPEC_CODE", trade_temp.getString("MERCH_SPEC_CODE", ""));
        }
        map.put("PO_ORDER_ID", poOrderNumber);
        map.put("EC_SERIAL_NUMBER", data.getString("CUSTOMER_NUMBER"));
        map.put("SYNC_STATE", data.getString("STATE_TYPE"));
        map.put("SYN_TIME", data.getString("SYN_TIME"));
        map.put("SEND", "BBSS");
        map.put("RECEIVE", "BOSS");
        map.put("IF_PROVCPRT", data.getString("IF_PROVCPRT", "0"));
        map.put("PROVCPRT_TYPE", data.getString("PROVCPRT_TYPE", ""));
        map.put("IF_ANS", data.getString("IF_ANS", "0"));
        map.put("REMARK", data.getString("NOTES"));
        map.put("RSRV_STR2", map.getString("IF_PROVCPRT"));// 0不需要反馈 1需要反馈(协助工单默认反馈)
        if (!"1".equals(map.getString("RSRV_STR2")))
        {// 如果不是协助工单 查询配置看是否需要反馈
            IDataset dataset = queryCommpInfoByAttrAndParam(map.getString("SYNC_STATE"));
            if (null != dataset && IDataUtil.isNotEmpty(dataset))
            {
                map.put("RSRV_STR2", "1");
            }
        }
        // 跨省工单状态产品信息
        IDataset productOrders = IDataUtil.getDatasetSpecl("PRODUCT_ID", data);// BBOSS生成的产品订单号
        IDataset circuitCodes = IDataUtil.getDatasetSpecl("CIRCUIT_CODE", data);// 电路代号
        IDataset results = IDataUtil.getDatasetSpecl("RESULT", data);// 处理结果
        IDataset reasons = IDataUtil.getDatasetSpecl("REASON", data);// 处理原因描述
        IDataset contacts = IDataUtil.getDatasetSpecl("CUSTOM_CONTACT", data);// 客户联系人
        IDataset contactPhones = IDataUtil.getDatasetSpecl("CUSTOM_CONTACT_PHONE", data);// 客户联系方式
        IDataset planComptime = IDataUtil.getDatasetSpecl("PLAN_COMPTIME", data);// 完成或者预计完成此状态的时间

        // 商品属性当前状态负责人信息
        IDataset phones = IDataUtil.getDataset("PHONE", data);// 当前状态负责人电话
        IDataset names = IDataUtil.getDataset("NAME", data);// 当前状态负责人姓名
        IDataset departments = IDataUtil.getDataset("DEPARTMENT", data);// 当前状态负责人所在单位及部门

        // 跨省工单产品属性信息
        IDataset magTypes = IDataUtil.getDataset("MAG_TYPE", data);// 客户经理类型
        IDataset magNames = IDataUtil.getDataset("MAG_NAME", data);// 客户经理姓名
        IDataset magPhones = IDataUtil.getDataset("MAG_PHONE", data);// 客户经理电话

        IDataset custTypes = IDataUtil.getDataset("CUST_TYPE", data);// 客户类型
        IDataset custNames = IDataUtil.getDataset("CUST_NAME", data);// 客户名称
        IDataset custCodes = IDataUtil.getDataset("CUST_CODE", data);// 客户编码

        IDataset dealSides = IDataUtil.getDataset("DEAL_SIDE", data);// 产品落实侧
        IDataset dealDetails = IDataUtil.getDataset("DEAL_DETAIL", data);// 当前状态处理说明
        IDataset attachments = IDataUtil.getDataset("ATTACHMENT", data);// 当前状态涉及附件

        IDataset modiTypes = IDataUtil.getDataset("MODI_TYPE", data);// 修正的产品信息类别
        IDataset modiValues = IDataUtil.getDataset("MODI_VALUE", data);// 修正后的值

        // 跨省工单状态信息
        IDataset attrs = new DatasetList();

        // 商品属性当前状态负责人信息
        if (null != phones && phones.size() > 0)
        {
            if (phones.size() != names.size() || names.size() != departments.size())
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_210);
            }
            for (int n = 0; n < phones.size(); n++)
            {
                attrs.add(dealAttr(SeqId, "O", "RespPerson", poOrderNumber, n, "PHONE", phones.get(n).toString(), "电话"));
                attrs.add(dealAttr(SeqId, "O", "RespPerson", poOrderNumber, n, "NAME", names.get(n).toString(), "姓名"));
                attrs.add(dealAttr(SeqId, "O", "RespPerson", poOrderNumber, n, "DEPARTMENT", departments.get(n).toString(), "所在单位及部门"));

            }
        }
        // 插入跨省工单状态表
        OrderOpenDao.inserPotradeState(map);

        if (productOrders != null && productOrders.size() > 0)
        {
            for (int i = 0; i < productOrders.size(); i++)
            {
                String productOrder = productOrders.get(i).toString();

                map.put("ORDER_NUMBER", productOrder);
                if (null != circuitCodes && circuitCodes.size() > 0)
                {
                    map.put("CIRCUIT_CODE", circuitCodes.get(i));
                }
                if (null != results && results.size() > 0)
                {
                    map.put("RESULT_CODE", results.get(i));
                }
                if (null != reasons && reasons.size() > 0)
                {
                    map.put("RESULT_INFO", reasons.get(i));
                }
                if (null != contacts && contacts.size() > 0)
                {
                    map.put("CONTACT", contacts.get(i));
                }
                if (null != contactPhones && contactPhones.size() > 0)
                {
                    map.put("CONTACT_PHONE", contactPhones.get(i));
                }
                if (null != planComptime && planComptime.size() > 0)
                {
                    map.put("PLAN_COMPTIME", planComptime.get(i));
                }
                map.put("UPDATE_TIME", SysDateMgr.getSysTime());
                map.put("REMARK", data.getString("NOTES"));

                // 插入跨省工单状态产品表
                OrderOpenDao.inserProductTrade(map);

                if (null != magTypes && magTypes.size() > 0)
                {

                    if (magTypes.size() != magPhones.size() || magPhones.size() != magNames.size())
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_211);
                    }
                    IDataset magType = magTypes.getDataset(i);
                    IDataset magName = magNames.getDataset(i);
                    IDataset magPhone = magPhones.getDataset(i);
                    for (int j = 0; j < magType.size(); j++)
                    {
                        // 产品对应的客户经理信息
                        attrs.add(dealAttr(SeqId, "P", "ProvCustMag", productOrder, j, "MAG_TYPE", magType.get(j).toString(), "客户经理类型"));
                        attrs.add(dealAttr(SeqId, "P", "ProvCustMag", productOrder, j, "MAG_NAME", magName.get(j).toString(), "姓名"));
                        attrs.add(dealAttr(SeqId, "P", "ProvCustMag", productOrder, j, "MAG_PHONE", magPhone.get(j).toString(), "电话"));
                    }
                }

                if (null != custTypes && custTypes.size() > 0)
                {
                    if (custTypes.size() != custCodes.size())
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_212);
                    }
                    IDataset custType = custTypes.getDataset(i);
                    IDataset custCode = custCodes.getDataset(i);
                    IDataset custName = new DatasetList();
                    if (null != custNames && custNames.size() > 0)
                    {
                        custName = custNames.getDataset(i);
                    }
                    for (int k = 0; k < custType.size(); k++)
                    {
                        // 产品受理涉及的客户信息
                        attrs.add(dealAttr(SeqId, "P", "CustInfo", productOrder, k, "CUST_TYPE", custType.get(k).toString(), "客户类型"));
                        attrs.add(dealAttr(SeqId, "P", "CustInfo", productOrder, k, "CUST_CODE", custCode.get(k).toString(), "客户编码"));
                        if (null != custName.get(k).toString() && !"".equals(custName.get(k).toString()))
                        {
                            attrs.add(dealAttr(SeqId, "P", "CustInfo", productOrder, k, "CUST_NAME", custName.get(k).toString(), "客户名称"));
                        }
                    }
                }

                if (null != dealSides && dealSides.size() > 0)
                {
                    if (dealSides.size() != dealDetails.size())
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_213);
                    }
                    IDataset dealSide = dealSides.getDataset(i);
                    IDataset dealDetail = dealDetails.getDataset(i);
                    IDataset attachment = new DatasetList();
                    if (null != attachments && attachments.size() > 0)
                    {
                        attachment = attachments.getDataset(i);
                    }
                    for (int l = 0; l < dealSide.size(); l++)
                    {
                        // 产品落实情况
                        attrs.add(dealAttr(SeqId, "P", "DealInfo", productOrder, l, "DEAL_SIDE", dealSide.get(l).toString(), "产品落实侧"));
                        attrs.add(dealAttr(SeqId, "P", "DealInfo", productOrder, l, "DEAL_DETAIL", dealDetail.get(l).toString(), "当前状态处理说明"));
                        if (null != attachment && attachment.size() > 0)
                        {
                            attrs.add(dealAttr(SeqId, "P", "DealInfo", productOrder, l, "ATTACHMENT", attachment.get(l).toString(), "当前状态涉及附件"));
                        }

                    }
                }

                if (null != modiTypes && modiTypes.size() > 0)
                {
                    IDataset modiType = modiTypes.getDataset(i);
                    IDataset modiValue = modiValues.getDataset(i);
                    for (int m = 0; m < modiType.size(); m++)
                    {
                        // 产品信息修正
                        attrs.add(dealAttr(SeqId, "P", "ProdInfoModify", productOrder, m, "MODI_TYPE", modiType.get(m).toString(), "修正的产品信息类别"));
                        attrs.add(dealAttr(SeqId, "P", "ProdInfoModify", productOrder, m, "MODI_VALUE", modiValue.get(m).toString(), "修正后的值"));
                    }
                }
            }
        }

        if (null != attrs && attrs.size() > 0)
        {
            OrderOpenDao.inserPotradeStateAttr(attrs);
        }

        // 台账登记成功后调用ESOP接口
        if (BizEnv.getEnvBoolean("isesop", false))
        {
            if (IDataUtil.isNotEmpty(tradeInfos))
            {
                String tradeId = tradeInfos.getData(0).getString("TRADE_ID", "");
                // 获取主台账数据
                IDataset infos = TradeInfoQry.getMainTradeByTradeIdForGrp(tradeId);
                if (IDataUtil.isNotEmpty(infos))
                {
                    IData httpResult = SendDataToEsopBean.synEsopData(tradeId, data);
                    // ESOP接口调用成功
                    if ("0".equals(httpResult.getString("X_RESULTCODE")))
                    {
                        String resultIbsysId = httpResult.getString("IBSYSID");
                        SendDataToEsopBean.updateIbSysIdForTradeExt(resultIbsysId, tradeId, data);
                    }
                }
            }
        }

        result.put("POORDER_NUMBER", poOrderNumber);
        result.put("STATE_TYPE", data.getString("STATE_TYPE"));
        result.put("PRODUCT_ORDER_NUMBER", IDataUtil.getDatasetSpecl("PRODUCT_ID", data));
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result.put("RSP_CODE", "00");
        result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        resultSet.add(result);

        return resultSet;
    }

    /**
     * 查询工单反馈配置
     * 
     * @param pd
     * @param sync_state
     * @author liaoyi
     */
    public static IDataset queryCommpInfoByAttrAndParam(String sync_state) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SYBSYS_CODE", "CSM");
        inparams.put("PARAM_ATTR", "5001");
        inparams.put("EPARCHY_CODE", "ZZZZ");
        inparams.put("PARAM_CODE", sync_state);
        IDataset comparDatas = ParamInfoQry.getCommparaInfoByAttrAndParam(inparams);

        return comparDatas;
    }

    /*
     * @description 工单反馈
     * @author xunyl
     * @date 2013-10-31
     */
    public static IDataset rspDealOrderResult(IData map) throws Exception
    {
        // 1- 将反馈信息发送至IBOSS
        IData ibossInfo = map.getData("IBOSS_INFO");
        String kindId = ibossInfo.getString("KIND_ID");
        IDataset result = IBossCall.dealInvokeUrl(kindId, "IBOSS", ibossInfo);
        if (null == result)
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_6);
        }

        String rspCode = result.getData(0).getString("RSP_CODE", "");
        if (!"00".equals(rspCode))
        {
            String xRspCode = result.getData(0).getString("X_RSPCODE", "");
            String xRspDesc = result.getData(0).getString("X_RSPDESC", "");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, xRspCode, xRspDesc);
        }

        // 2- 保存商品信息处理结果
        IData poTradeStateInfo = map.getData("PO_TRADE_STATE_INFO");
        OrderOpenDao.inserPotradeState(poTradeStateInfo);

        // 3- 保存产品信息处理结果
        IDataset productTradeInfoList = map.getDataset("PRODUCT_TRADE_INFO_LIST");
        if (IDataUtil.isNotEmpty(productTradeInfoList))
        {
            for (int i = 0; i < productTradeInfoList.size(); i++)
            {
                OrderOpenDao.inserProductTrade(productTradeInfoList.getData(i));
            }
        }

        // 4- 保存其它信息处理结果
        IDataset poStateTradeStateAttrInfoList = map.getDataset("PO_TRADE_STATE_ATTR_INFO_LIST");
        OrderOpenDao.inserPotradeStateAttr(poStateTradeStateAttrInfoList);

        // 5 发送esop
        boolean sendpf = BizEnv.getEnvBoolean("isesop", false);

        if (sendpf)
        {
            IData iesopData = map.getData("EOS");
            if (IDataUtil.isNotEmpty(iesopData))
            {
                IDataset httpResultSet = ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", iesopData);
                return httpResultSet;
            }
        }

        return new DatasetList();

    }
}
