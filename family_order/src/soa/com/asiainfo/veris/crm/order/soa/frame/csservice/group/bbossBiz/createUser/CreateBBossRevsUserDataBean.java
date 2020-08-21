
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpGenSn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/*
 * @description 拼装一级BOSS过来的集团产品受理数据
 * @author xunyl
 * @date 203-06-21
 */
public class CreateBBossRevsUserDataBean extends GroupBean
{
    // 客户编号定义
    static String custId = "";

    // 定义手机虚拟号
    static String serialNumber = "";

    // 定义中断类型(程序处理时遇到某些情况会自动中断后面的进程，例如在集团产品受理过程中，需要新增的产品信息为空，则不需要继续进行受理)
    static String breakType = "0";// 0表示受理过程正常进行，否则中断受理过程

    /*
     * @description 校验是否存在有程序中断情况，如果有重新定义返回数据
     * @author xunyl
     * @date 2013-06-28
     */
    protected static void checkOperResult(IData map, IData returnVal) throws Exception
    {
        // 1- 检查中断类型
        if ("0".equals(breakType))
        {
            return;
        }

        // 2- 清空返回数据
        returnVal.clear();

        // 3- 定义应答报文
        IData rep_data = new DataMap();
        if ("1".equals(breakType))
        {// 集团受理过程中没有产品信息
            rep_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            rep_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
            rep_data.put("RSPCODE".toUpperCase(), "00");
            rep_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
            rep_data.put("TRADE_ID", "-1");
            rep_data.put("EC_SERIAL_NUMBER", map.getString("EC_SERIAL_NUMBER", ""));
            rep_data.put("SUBSCRIBE_ID", map.getString("SUBSCRIBE_ID", ""));
            rep_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        }

        // 4- 重新定义返回数据
        returnVal.put("BREAK_TYPE", breakType);
        returnVal.put("REP_DATA", IDataUtil.idToIds(rep_data));
    }

    /*
     * @description 根据EC客户编号获取本省客户编号和集团编号
     * @author xunyl
     * @date 2013-06-21
     */
    protected static IData geCustInfoByEcNumber(IData map) throws Exception
    {
        // 1- 定义客户信息对象
        IData custInfo = new DataMap();

        // 2- 获取EC客户编号
        String ecCustNumber = map.getString("EC_SERIAL_NUMBER", "");

        // 3- 根据EC客户编号获取集团客户信息
        IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(ecCustNumber, null);

        // 4- 获取集团客户信息为空的情况，抛出异常
        if (groupInf == null || groupInf.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_899, ecCustNumber);
        }

        // 5- 返回客户信息
        custInfo.put("CUST_ID", groupInf.getData(0).getString("CUST_ID"));
        custInfo.put("GROUP_ID", groupInf.getData(0).getString("GROUP_ID"));
        custInfo.put("CUST_NAME", groupInf.getData(0).getString("CUST_NAME"));
        return custInfo;
    }

    /*
     * @description 根据客户编号查询帐户信息
     * @author xunyl
     * @date 2013-06-22
     */
    protected static IData getAcctByCustId(IData custInfo) throws Exception
    {
        // 1- 定义返回数据
        IData accoutResult = new DataMap();

        // 2- 根据客户编号查询帐户信息
        String custId = custInfo.getString("CUST_ID");
        IDataset acctInfos = UAcctInfoQry.qryAcctInfoByCustId(custId);

        // 3- 分情况处理查询结果，存在即用已有的帐户，不存在则新增帐户
        if (acctInfos == null || acctInfos.size() == 0)
        {// 帐户信息不存在，需要新增帐户
            accoutResult.put("ACCT_IS_ADD", true);

            // 拼装帐户信息
            IData acctInfo = new DataMap();
            acctInfo.put("SAME_ACCT", "0");
            acctInfo.put("PAY_MODE_CODE", "0");
            acctInfo.put("ACCT_ID", SeqMgr.getAcctId());
            acctInfo.put("PAY_NAME", custInfo.getString("CUST_NAME"));
            acctInfo.put("RSRV_STR8", "1");// 打印模式 分产品项展示费用
            acctInfo.put("RSRV_STR9", "1");// 发票模式：实收发票

            accoutResult.put("ACCT_INFO", acctInfo);
        }
        else
        {// 帐户信息存在，直接返回帐户编号
            accoutResult.put("ACCT_IS_ADD", false);

            accoutResult.put("ACCT_ID", acctInfos.getData(0).getString("ACCT_ID"));
        }

        // 4-返回帐户信息
        return accoutResult;
    }

    /*
     * @descripiton 拼装BBOSS特有的信息(业务保障等级,业务开展模式,主办省,集团编号，套餐生效规则)
     * @author xunyl 2013-06-24
     */
    protected static void getBbossMerchInfo(String groupId, IData map, IData bbossMerchInfo) throws Exception
    {
        // 1- 添加集团编号
        bbossMerchInfo.put("GROUP_ID", groupId);

        // 2- 添加主办省
        String hostCompany = map.getString("PROVINCE", "");
        bbossMerchInfo.put("HOST_COMPANY", hostCompany);

        // 3- 添加业务保障等级
        String busNeedDegree = map.getString("BUSNEED_DEGREE", "");
        bbossMerchInfo.put("BUS_NEED_DEGREE", busNeedDegree);

        // 4- 添加业务开展模式
        String bizMode = map.getString("SI_BIZ_MODE", "");
        bbossMerchInfo.put("BIZ_MODE", bizMode);

        // 5- 添加套餐生效规则
        String poRatePolicyEffRule = map.getString("RSRV_STR3", "");
        bbossMerchInfo.put("PAY_MODE", poRatePolicyEffRule);

        if (StringUtils.isNotEmpty(map.getString("RSRV_STR2")))
        {
            // 6- 商品订购关系ID BBOSS下发
            String offer_id = map.getString("RSRV_STR2");
            bbossMerchInfo.put("MERCH_OFFER_ID", offer_id);
        }

         if (StringUtils.isNotEmpty(map.getString("SUBSCRIBE_ID")))
        {
            // 6- 商品订单ID BBOSS下发
            String order_id = map.getString("SUBSCRIBE_ID");
            bbossMerchInfo.put("MERCH_ORDER_ID", order_id);
        }

        // 7- 添加管理节点受理标记
        String busiSign = map.getString("BUSI_SIGN", "");
        if (IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign) // BBOSS 业务流程管理接口
                || IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign + "_1_0"))
        {
            bbossMerchInfo.put("BBOSS_MANAGE_CREATE", true);
        }
        
        // 8- 反向落地时添加提单人员信息，对应规范中的ContactorInfo
        IDataset contactorInfos = new DatasetList();   
        getContactorInfo(map, contactorInfos);
        bbossMerchInfo.put("CONTACTOR_INFOS", contactorInfos);
        
      //REQ201911040021_(集团全网)关于更新集客大厅与省公司接口规范的通知--启用成员叠加包、增加订购渠道信息  add by huangzl3
        // 2-8 添加BBOSS渠道信息
        IData channelInfo = new DataMap();
        channelInfo.put("MAINTEANCE_CHANNELID", map.getString("MAINTEANCE_CHANNELID", ""));
        channelInfo.put("CHANNEL_TYPE", map.getString("CHANNEL_TYPE", ""));
        channelInfo.put("PROJECT_NAME", map.getString("PROJECT_NAME", ""));
        channelInfo.put("PROJECT_ID", map.getString("PROJECT_ID", ""));
        channelInfo.put("SETTLEMENT_RATIO", map.getString("SETTLEMENT_RATIO", ""));
        channelInfo.put("CHANNEL_ID", map.getString("CHANNEL_ID", ""));

        bbossMerchInfo.put("CHANNEL_INFO", channelInfo);
        //REQ201911040021_(集团全网)关于更新集客大厅与省公司接口规范的通知--启用成员叠加包、增加订购渠道信息  end
    }

    /*
     * @description 拼装必选元素信息
     * @auhtor xunyl
     * @date 2013-06-27
     */
    protected static void getElementInfo(IData forceElement, IData elementInfo) throws Exception
    {
        // 1- 添加资费实例ID，受理时默认为""
        elementInfo.put("INST_ID", "");

        // 2- 添加元素类型，资费类型对应为"D"，服务类型对应为"S"
        elementInfo.put("ELEMENT_TYPE_CODE", forceElement.getString("ELEMENT_TYPE_CODE"));

        // 3- 添加资费状态，受理时默认为"0"
        elementInfo.put("MODIFY_TAG", "0");

        // 4- 添加产品产品编号
        elementInfo.put("PRODUCT_ID", forceElement.getString("PRODUCT_ID"));

        // 5- 添加元素ID
        elementInfo.put("ELEMENT_ID", forceElement.getString("ELEMENT_ID"));

        // 6- 添加包信息
        elementInfo.put("PACKAGE_ID", forceElement.getString("PACKAGE_ID"));

        // 9- 添加开始时间
        elementInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间
        elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加参数信息
        IDataset attrItemAList = AttrItemInfoQry.getElementItemA(forceElement.getString("ELEMENT_TYPE_CODE"), forceElement.getString("ELEMENT_ID"), CSBizBean.getUserEparchyCode());
        IDataset attrParams = new DatasetList();
        for (int i = 0; i < attrItemAList.size(); i++)
        {
            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", attrItemAList.getData(i).getString("ATTR_CODE"));
            attrParam.put("ATTR_NAME", attrItemAList.getData(i).getString("ATTR_LABLE"));
            attrParam.put("ATTR_VALUE", attrItemAList.getData(i).getString("ATTR_INIT_VALUE"));
            attrParams.add(attrParam);
        }
        if (null == attrItemAList || attrItemAList.size() == 0)
        {
            elementInfo.put("ATTR_PARAM", "");
        }
        else
        {
            elementInfo.put("ATTR_PARAM", attrParams);
        }

    }

    /*
     * @description 拼装商品资费信息(套餐编号对应于本省的PACKAGE_ID,资费编号对应于ELEMENT_ID)
     * @author xunyl
     * @date 2013-06-23
     */
    protected static void getMerchDiscnt(IData map, IDataset merchDiscnt, String proPoNumber) throws Exception
    {
        // 1- 获取商品套餐、资费信息
        IDataset tempRatePolicyIdSet = IDataUtil.getDataset("PRSRV_STR1", map);// 商品套餐ID
        IDataset tempPlanIdSet = IDataUtil.getDataset("PLAN_ID", map);// 资费计划标识
        IDataset tempDescriptionSet = IDataUtil.getDataset("DESCRIPTION", map);// 资费描述
        IDataset tempActionSet = IDataUtil.getDataset("ACTION", map);// 资费操作代码
        
        // 2- 拼装资费信息
        for (int i = 0; i < tempRatePolicyIdSet.size(); i++)
        {
            // 2-1 根据商品套餐ID获取包ID
            String ratePolicyId = GrpCommonBean.nullToString(tempRatePolicyIdSet.get(i));
            String packageId = GrpCommonBean.merchToProduct(ratePolicyId, 1, proPoNumber);
            if(StringUtils.isEmpty(packageId)){
            	continue;
            }

            // 2-2 循环资费计划标志拼装商品资费信息
            IDataset planIdset = IDataUtil.modiIDataset(tempPlanIdSet.get(i), "PLAN_ID");
            IDataset descriptionSet = IDataUtil.modiIDataset(tempDescriptionSet.get(i), "DESCRIPTION");
            IDataset actionSet = IDataUtil.modiIDataset(tempActionSet.get(i), "ACTION");
            
            //2-3获取icb参数
            IDataset paraCodeList=IDataUtil.getDataset("PARA_CODE", map);
            IDataset paraNameList=IDataUtil.getDataset("PARANAME", map);
            IDataset paraValueList=IDataUtil.getDataset("VALUE", map);
            for (int j = 0; j < planIdset.size(); j++)
            {
                // 2-2-1 定义商品元素对象
                IData elementInfo = new DataMap();
                // 2-2-2 添加资费实例ID，受理时默认为""
                elementInfo.put("INST_ID", "");
                // 2-2-3 添加元素类型，资费类型对应为"D"
                elementInfo.put("ELEMENT_TYPE_CODE", "D");
                // 2-2-4 添加资费状态  删除状态资费直接返回
                String action = GrpCommonBean.nullToString(actionSet.get(j));
                if("0".equals(action)){
                    continue;
                }
                elementInfo.put("MODIFY_TAG","0");
                // 2-2-5 添加产品产品编号
                elementInfo.put("PRODUCT_ID", proPoNumber);
                // 2-2-6 添加元素ID
                String planId = GrpCommonBean.nullToString(planIdset.get(j));
                String elementId = GrpCommonBean.merchToProduct(planId, 1, proPoNumber);
                elementInfo.put("ELEMENT_ID", elementId);
                // 2-2-7 添加包信息
                elementInfo.put("PACKAGE_ID", packageId);
                // 2-2-8 添加元素名称
                String desc = "";
                if (descriptionSet.size() > j)
                {
                    desc = GrpCommonBean.nullToString(descriptionSet.get(j));
                }
                elementInfo.put("ELEMENT_NAME", desc);
                // 2-2-9 添加开始时间
                elementInfo.put("START_DATE", SysDateMgr.getSysTime());
                // 2-2-10 添加结束时间
                elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                // 2-2-11 添加资费参数
                // 12- 添加参数信息
                IDataset merchIcbParamInfo=new DatasetList();
                if(IDataUtil.isNotEmpty(paraCodeList)){
                     IDataset icbCodeList=paraCodeList.getDataset(j);
                     IDataset icbValueList=paraValueList.getDataset(j);
                     IDataset icbNameList=paraNameList.getDataset(j);
                     GrpCommonBean.getMerchIcbParamInfo(merchIcbParamInfo,elementId,icbCodeList,icbNameList,icbValueList);
                }
               
                if (IDataUtil.isEmpty(merchIcbParamInfo))
                {
                    elementInfo.put("ATTR_PARAM", "");
                }
                else
                {
                    elementInfo.put("ATTR_PARAM", merchIcbParamInfo);
                }

         //    elementInfo.put("ATTR_PARAM", "");
                // 2-2-12 将资费添加至资费信息中
                merchDiscnt.add(elementInfo);
            }
        }
    }

    /*
     * @description 获取产品的基本信息
     * @author xunyl
     * @date 2013-06-26
     */
    protected static void getProductBaseInfo(String productId, String productOperCode, String productOfferId, String productOrderId, IData bbossProductInfo) throws Exception
    {
        // 1- 添加用户信息
        bbossProductInfo.put("USER_ID", "");

        // 2- 添加产品编号
        bbossProductInfo.put("PRODUCT_ID", productId);

        // 3- 添加产品用户的存在状态
        bbossProductInfo.put("ISEXIST", "");

        // 4- 添加产品操作类型
        bbossProductInfo.put("PRODUCT_OPER_CODE", productOperCode);

        if (StringUtils.isNotEmpty(productOfferId))
        {
            // 5- 添加产品订购关系ID BBOSS下发
            bbossProductInfo.put("PRODUCT_OFFER_ID", productOfferId);
        }

        if (StringUtils.isNotEmpty(productOfferId))
        {
            // 6- 添加产品订单ID BBOSS下发
            bbossProductInfo.put("PRODUCT_ORDER_ID", productOrderId);
        }
    }

    /*
     * @description 获取元素信息
     * @author xunyl
     * @date 2013-06-27
     */
    protected static void getProductElementInfo(String productId, IData secondProductInfo, IDataset elementInfo) throws Exception
    {
        // 1- 添加必须服务
        IDataset forceSvcs = ProductInfoQry.getProductForceSvc(productId, CSBizBean.getUserEparchyCode());
        for (int i = 0; i < forceSvcs.size(); i++)
        {
            IData forceSvc = forceSvcs.getData(i);
            // 1-1 拼装服务数据
            IData svcElement = new DataMap();
            getElementInfo(forceSvc, svcElement);
            // 1-2 将拼好的服务元素放入元素集
            elementInfo.add(svcElement);
        }

        // 2- 定义必选元素编号数据集(一级BOSS发送过来的资费与本地获取到的必选资费也许会有重复，一级BOSS发送过来的资费编号放入到该List,后续添加必选资费时校验重复)
        IData elementIdMap = new DataMap();

        // 3- 添加一级BOSS传递的资费信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        IDataset ratePlanActionList = secondProductInfo.getDataset("RATE_PLAN_ACTION");
        for (int j = 0; j < ratePlanIdList.size(); j++)
        {
            // 3-1 没有与本省资费编码存在对应关系的资费不添加
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(j));
            if ("".equals(ratePlanId))
            {
                continue;
            }

            // 3-2 报文中被删除的资费不添加
            String ratePlanAction = GrpCommonBean.nullToString(ratePlanActionList.get(j));
            if("0".equals(ratePlanAction)){
                continue;
            }

            // 3-3 添加新增的资费信息
            String elementId = GrpCommonBean.merchToProduct(ratePlanId, 1, productId);// add by xuxf20140606
            if(StringUtils.isEmpty(elementId)){
            	continue;
            }
            elementIdMap.put(elementId, elementId);
            getProductRateInfo(j, productId, elementId, secondProductInfo, elementInfo);
        }

        // 4- 添加必选资费
        IDataset forceDiscnts = ProductInfoQry.getProductForceDiscnt(productId);
        for (int i = 0; i < forceDiscnts.size(); i++)
        {
            IData forceDct = forceDiscnts.getData(i);
            // 4-1 获取资费编号并判断资费编号在必选元素编号集中是否存在，存在说明重复，进行下一次循环
            String dctId = forceDct.getString("ELEMENT_ID");
            if (elementIdMap.containsKey(dctId))
            {
                continue;
            }
            // 4-2 拼装资费数据
            IData dctElement = new DataMap();
            getElementInfo(forceDct, dctElement);
            // 4-3 将拼好的服务元素放入元素集
            elementInfo.add(dctElement);
        }
    }

    /*
     * @description 拼装产品资费信息
     * @author xunyl
     * @date 2013-06-25           
     */
    protected static void getProductRateInfo(int j, String productId, String elementId, IData secondProductInfo, IDataset rateInfoList) throws Exception
    {
        // 1- 获取产品数据三级信息
        IData thirdProductInfo = new DataMap();
        GrpCommonBean.getThirdProductInfo(j, secondProductInfo, thirdProductInfo);

        // 2- 定义资费信息
        IData rateInfo = new DataMap();

        // 3- 添加资费实例ID，受理时默认为""
        rateInfo.put("INST_ID", "");

        // 4- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 5- 添加资费状态，受理时默认为"0"
        rateInfo.put("MODIFY_TAG", "0");

        // 6- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", productId);

        // 7- 添加元素ID(元素ID即省内的资费编号，与一级BOSS传递过来的RATE_PLAN_ID形成一一对应关系)
        rateInfo.put("ELEMENT_ID", elementId);

        // 8- 添加包信息
        IDataset packageElement = PkgElemInfoQry.getPackageElementByProductId(productId, "D", elementId);
        if (null == packageElement || packageElement.size() == 0)
        {
            // 根据元素编号%s获取不到对应的包信息
            CSAppException.apperr(CrmUserException.CRM_USER_907);
        }
        String packageId = packageElement.getData(0).getString("PACKAGE_ID");
        rateInfo.put("PACKAGE_ID", packageId);

        // 9- 添加开始时间 WAITE_TO_MODIFY 
        rateInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间 WAITE_TO_MODIFY
        rateInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加资费参数信息
        IDataset productIcbParamInfo = new DatasetList();
        GrpCommonBean.getProductIcbParamInfo(thirdProductInfo, productIcbParamInfo);       
        if (null == productIcbParamInfo || productIcbParamInfo.size() == 0)
        {
            rateInfo.put("ATTR_PARAM", "");
        }
        else
        {
            rateInfo.put("ATTR_PARAM", productIcbParamInfo);
        }

        // 12- 添加资费信息至资费列表
        rateInfoList.add(rateInfo);
    }
    
    /*
     * @description 拼装产品资费信息
     * @author daidl
     * @date 2019-04-9           
     */
    protected static void getJKDTProductRateInfo(int j, String productId, String elementId, IData secondProductInfo, IDataset rateInfoList) throws Exception
    {
        // 1- 获取产品数据三级信息
        IData thirdProductInfo = new DataMap();
        GrpCommonBean.getThirdProductInfo(j, secondProductInfo, thirdProductInfo);

        // 2- 定义资费信息
        IData rateInfo = new DataMap();

        // 3- 添加资费实例ID，受理时默认为""  
        rateInfo.put("INST_ID", "");

        // 4- 添加元素类型，资费类型对应为"D"
        rateInfo.put("ELEMENT_TYPE_CODE", "D");

        // 5- 添加资费状态，受理时默认为"0"
        rateInfo.put("MODIFY_TAG", "0");

        // 6- 添加产品产品编号
        rateInfo.put("PRODUCT_ID", productId);

        // 7- 添加元素ID(元素ID即省内的资费编号，与一级BOSS传递过来的RATE_PLAN_ID形成一一对应关系)
        rateInfo.put("ELEMENT_ID", elementId);

        // 8- 添加包信息
        IDataset packageElement = PkgElemInfoQry.getPackageElementByProductId(productId, "D", elementId);
        if (null == packageElement || packageElement.size() == 0)
        {
            // 根据元素编号%s获取不到对应的包信息
            CSAppException.apperr(CrmUserException.CRM_USER_907);
        }
        String packageId = packageElement.getData(0).getString("PACKAGE_ID");
        rateInfo.put("PACKAGE_ID", packageId);

        // 9- 添加开始时间 WAITE_TO_MODIFY 
        rateInfo.put("START_DATE", SysDateMgr.getSysTime());

        // 10- 添加结束时间 WAITE_TO_MODIFY
        rateInfo.put("END_DATE", SysDateMgr.getTheLastTime());

        // 11- 添加资费参数信息
        IDataset productIcbParamInfo = new DatasetList();
        GrpCommonBean.getProductIcbParamInfo(thirdProductInfo, productIcbParamInfo);
        
        //12- 其它参数信息转换处理
        GrpCommonBean.getProductOtherParamInfo(secondProductInfo, productIcbParamInfo, productId, elementId);//jkdt daidl
        
        if (null == productIcbParamInfo || productIcbParamInfo.size() == 0)
        {
            rateInfo.put("ATTR_PARAM", "");
        }
        else
        {
            rateInfo.put("ATTR_PARAM", productIcbParamInfo);
        }

        // 12- 添加资费信息至资费列表
        rateInfoList.add(rateInfo);
    }
    
    /*
     * @descripiton 拼装资源数据
     * @author xunyl
     * @date 2013-06-22
     */
    protected static void getResInfo(String serialNumber, IDataset resInfos) throws Exception
    {
        // 1- 拼装资源数据
        IData resInfo = new DataMap();
        resInfo.put("MODIFY_TAG", "0");// 默认为新增
        resInfo.put("RES_TYPE_CODE", "G");// 默认为集团
        resInfo.put("RES_TYPE", "集团编号");
        resInfo.put("RES_CODE", serialNumber);// 集团虚拟手机号
        resInfo.put("CHECKED", "true");
        resInfo.put("DISABLED", "true");

        // 2- 返回资源数据
        resInfos.add(resInfo);
    }

    /*
     * @description 校验是否需要中断本次循环
     * @author xunyl
     * @date 2013-06-28
     */
    protected static boolean isContinue(int i, IData firstProductInfo) throws Exception
    {
        // 1- 定义校验结果，默认为true,校验通过
        boolean checkResult = true;

        // 2- 获取产品订购关系编号
        IDataset productOfferingIdset = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        String productOfferingId = GrpCommonBean.nullToString(productOfferingIdset.get(i));

        // 3-根据产品订购关系编号查询产品用户
        IDataset merchPInfos = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferingId);
        if (null != merchPInfos && merchPInfos.size() > 0)
        {
            checkResult = false;
        }

        // 返回校验结果
        return checkResult;
    }

    /*
     * @description 将一级BOSS传递过来的数据拼装成符合集团产品受理基类处理的数据集
     * @author xunyl
     * @date 2013-06-21
     */
    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();

        map.put("CONTRACT_ID", SeqMgr.getContractId());
        // 2- 拼装商品数据
        makeMerchInfoData(map, returnVal);

        // 3- 拼装产品数据
        makeMerchPInfoData(map, returnVal);

        // 4- 特殊情况下，需要将拼好的标准结构进行特殊处理
        GrpCommonBean.modifyInparamsBySpecialBiz(returnVal, GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue());

        // 5- 校验是否有中断情况出现，如果有中断情况，重新定义返回数据
        checkOperResult(map, returnVal);

        // 6- 返回结果
        return returnVal;
    }

    /*
     * @description 拼装商品数据
     * @author xunyl
     * @date 2013-06-21
     */
    protected static void makeMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 添加商品数据
        // 2-1 添加客户编号
        IData custInfo = geCustInfoByEcNumber(map);
        custId = custInfo.getString("CUST_ID");
        merchInfo.put("CUST_ID", custId);

        // 2-2 添加商品编号
        String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0); // BBOSS商品规格编号
        String proPoNumber = GrpCommonBean.merchToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        merchInfo.put("PRODUCT_ID", proPoNumber);

        // 2-3 添加虚拟手机号码
        IData inparam = new DataMap();
        String groupId = custInfo.getString("GROUP_ID");
        inparam.put("GROUP_ID", groupId);
        inparam.put("PRODUCT_ID", proPoNumber);
        serialNumber = GrpGenSn.genGrpSn(inparam).getString("SERIAL_NUMBER");
        merchInfo.put("SERIAL_NUMBER", serialNumber);

        // 2-4 添加帐户信息
        IData acctInfo = getAcctByCustId(custInfo);
        if (acctInfo.getBoolean("ACCT_IS_ADD") == true)
        {
            merchInfo.put("ACCT_IS_ADD", true);
            merchInfo.put("ACCT_INFO", acctInfo.getData("ACCT_INFO"));
        }
        else
        {
            merchInfo.put("ACCT_IS_ADD", false);
            merchInfo.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        }

        // 2-5 添加用户类别，默认为省级集团
        IData userInfo = new DataMap();
        userInfo.put("USER_DIFF_CODE", "2");// 用户类别：0-普通个人，1-地市级集团，2-省级集团，3-跨省集团
        userInfo.put("CONTRACT_ID", map.getString("CONTRACT_ID", ""));
        IDataset pOAttTypeInfoList = IDataUtil.getDataset("POATT_TYPE", map);
        IDataset pOAttCodeInfoList = IDataUtil.getDataset("POATT_CODE", map);//合同编码
        if(IDataUtil.isNotEmpty(pOAttTypeInfoList)){      
            for(int i=0;i<pOAttTypeInfoList.size();i++){
                String pOAttType = pOAttTypeInfoList.get(i).toString();
                if(StringUtils.equals("1", pOAttType)){//合同附件
                    String contractId = pOAttCodeInfoList.get(i).toString();
                    userInfo.put("CONTRACT_ID",contractId);
                }           
            }  
        }
        merchInfo.put("USER_INFO", userInfo);

        // 2-6 添加资源信息
        IDataset resInfos = new DatasetList();
        getResInfo(serialNumber, resInfos);
        merchInfo.put("RES_INFO", resInfos);

        // 2-7 添加商品资费信息
        IDataset merchDiscnt = new DatasetList();
        getMerchDiscnt(map, merchDiscnt, proPoNumber);
        //BUG20181116093118跨省专线下账期生效未计入数据库产品生效时间未延期至次月生效问题start
        //解决用户选择下账期生效时资费生效时间为下月初始时间
        String poRatePolicyEffRule = map.getString("RSRV_STR3", "");
        merchDiscnt = CreateBBossUserDataBean.dealStartDate(merchDiscnt, poRatePolicyEffRule);
        //BUG20181116093118跨省专线下账期生效未计入数据库产品生效时间未延期至次月生效问题end
        merchInfo.put("ELEMENT_INFO", merchDiscnt);

        // 2-8 添加BBOSS特有商品信息
        IData bbossMerchInfo = new DataMap();
        getBbossMerchInfo(groupId, map, bbossMerchInfo);
        merchInfo.put("BBOSS_MERCH_INFO", bbossMerchInfo);

        // 3- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 4- 添加商品信息至返回结果中
        returnVal.put("MERCH_INFO", merchInfo);
        
        // 5- 商品附件信息发往客管侧保存
        GrpCommonBean.sendAttInfoListToCliMng(map,custId,proPoNumber);
    }

    /*
     * @description 拼装产品数据
     * @auhtor xunyl
     * @date 2013-06-25
     * @example 下面的例子可以解释一级BOSS传递的数据中产品，资费和ICB参数之间的关系 [a,b]---产品 [[a1,a2][b1,b2]]---资费
     * [[[a11,a12][a21,a22]][[b11,b12][b21,b22]]]---ICB参数
     */
    protected static void makeMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义符合基类处理的产品数据集
        IDataset productInfoset = new DatasetList();

        // 2- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 3- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");

        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 3-1 校验是否需要中断本次循环(例如产品用户资料已经存在的情况，产品操作类型依然为新增或者预受理等)
            boolean result = isContinue(i, firstProductInfo);
            if (result == false)
            {
                continue;
            }

            // 3-2 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 3-3 定义产品信息变量
            IData productInfo = new DataMap();

            // 3-4 添加客户编号
            productInfo.put("CUST_ID", custId);

            // 3-5 添加产品编号
            String proNumber = GrpCommonBean.nullToString(productNumberSet.get(i));
            String productId = GrpCommonBean.merchToProduct(proNumber, 2, null);// 产品编号转化为本地产品编号

            productInfo.put("PRODUCT_ID", productId);

            // 3-6 添加帐户信息
            productInfo.put("ACCT_IS_ADD", true);// 这里的值为true,既产品生成台帐时也会新增账户，但是账户编号会根据商品的返回值来定

            // 3-7 添加元素信息(包括服务与资费)
            IDataset elementInfo = new DatasetList();
            getProductElementInfo(productId, secondProductInfo, elementInfo);
            //BUG20181116093118跨省专线下账期生效未计入数据库产品生效时间未延期至次月生效问题start
            //解决用户选择下账期生效时资费生效时间为下月初始时间
            String poRatePolicyEffRule =map.getString("RSRV_STR3","1") ;// 套餐生效规则  daidl
            elementInfo = CreateBBossUserDataBean.dealStartDate(elementInfo, poRatePolicyEffRule);
            //BUG20181116093118跨省专线下账期生效未计入数据库产品生效时间未延期至次月生效问题end
            if (null == elementInfo || elementInfo.size() == 0)
            {
                productInfo.put("ELEMENT_INFO", "");
            }
            else
            {
                productInfo.put("ELEMENT_INFO", elementInfo);
            }

            // 3-8 添加产品属性信息
            IData productParam = new DataMap();
            IDataset productParamInfoList = GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            filterProductParam(productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 3-9 添加虚拟手机号
            IData idGen = new DataMap();
            idGen.put("GROUP_ID", returnVal.getData("MERCH_INFO").getData("BBOSS_MERCH_INFO").getString("GROUP_ID"));
            idGen.put("PRODUCT_ID", productId);
            String serialNumber = GrpGenSn.genGrpSn(idGen).getString("SERIAL_NUMBER");
            serialNumber = GrpCommonBean.dealSerialNumber(productId, serialNumber, productParamInfoList);
            productInfo.put("SERIAL_NUMBER", serialNumber);

            // 3-10 添加产品操作类型(仅供判断是否为归档报文用)
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));
            productInfo.put("PRODUCT_OPER_CODE", productOperCode);

            // 3-11 添加产品订购关系ID
            String productOfferId = GrpCommonBean.nullToString(productOfferIdSet.get(i));
            IDataset merchpUserInfoList =UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
            IDataset merchpTradeInfoList =TradeGrpMerchpInfoQry.getMerchpOnlineByProductofferId(productOfferId,null);
            if(IDataUtil.isNotEmpty(merchpUserInfoList) || IDataUtil.isNotEmpty(merchpTradeInfoList)){
            	return;
            }

            // 3-12 添加产品订单号
            String productOrderId = GrpCommonBean.nullToString(productOrderIdSet.get(i));

            // 3-13 添加BBOSS特有产品信息
            IData bbossProductInfo = new DataMap();
            getProductBaseInfo(productId, productOperCode, productOfferId, productOrderId, bbossProductInfo);
            String busiSign = map.getString("BUSI_SIGN", "");
            if (IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign) // BBOSS 业务流程管理接口
                    || IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign + "_1_0"))
            {
                bbossProductInfo.put("BBOSS_MANAGE_CREATE", true);
            }

            // 3-14 一些产品需要特殊标记
            specialDeal(bbossProductInfo, proNumber, map);

            productInfo.put("BBOSS_PRODUCT_INFO", bbossProductInfo);

            IData userInfo = new DataMap();
            userInfo.put("CONTRACT_ID", map.getString("CONTRACT_ID", ""));
            productInfo.put("USER_INFO", userInfo);

            // 3-15 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 3-16 将产品信息添加至产品数据集
            productInfoset.add(productInfo);
        }

        // 4- 校验集团产品受理程序是否继续进行
        if (null == productInfoset || productInfoset.size() == 0)
        {
            breakType = "1";
        }

        // 5- 添加产品信息至返回结果中
        returnVal.put("ORDER_INFO", productInfoset);
    }

    /**
     * @Function:
     * @Description:一些产品需要加入特殊标记
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2014-9-5
     */
    private static void specialDeal(IData bbossProductInfo, String proNumber, IData map)
    {
        // 统付业务需要把业务模式传给产品
        if ("99904".equals(proNumber) || "99905".equals(proNumber))
        {
            bbossProductInfo.put("BIZ_MODE", map.getString("SI_BIZ_MODE", ""));// 业务受理模式
        }
    }
    
    /*
     * @description 将一级BOSS传递过来的数据拼装成符合集团产品受理基类处理的数据集
     * @author xunyl
     * @date 2013-06-21
     */
    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义返回数据
        IData returnVal = new DataMap();

        map.put("CONTRACT_ID", SeqMgr.getContractId());
        // 2- 拼装商品数据
        makeJKDTMerchInfoData(map, returnVal);

        // 3- 拼装产品数据
        makeJKDTMerchPInfoData(map, returnVal);

        // 4- 特殊情况下，需要将拼好的标准结构进行特殊处理
        GrpCommonBean.modifyJKDTInparamsBySpecialBiz(returnVal, GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue());

        // 5- 校验是否有中断情况出现，如果有中断情况，重新定义返回数据
        checkOperResult(map, returnVal);

        // 6- 返回结果
        return returnVal;
    }

    /*
     * @description 集客大厅拼装商品数据
     * @author
     * @date
     */
    protected static void makeJKDTMerchInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义商品对象
        IData merchInfo = new DataMap();

        // 2- 添加商品数据
        // 2-1 添加客户编号
        IData custInfo = geCustInfoByEcNumber(map);
        custId = custInfo.getString("CUST_ID");
        merchInfo.put("CUST_ID", custId);

        // 2-2 添加商品编号
        String poNumber = (String) IDataUtil.getDatasetSpecl("RSRV_STR1", map).get(0); // BBOSS商品规格编号
        String proPoNumber = GrpCommonBean.merchJKDTToProduct(poNumber, 0, null);// 商品编号转化为本地产品编号
        merchInfo.put("PRODUCT_ID", proPoNumber);

        // 2-3 添加虚拟手机号码
        IData inparam = new DataMap();
        String groupId = custInfo.getString("GROUP_ID");
        inparam.put("GROUP_ID", groupId);
        inparam.put("PRODUCT_ID", proPoNumber);
        serialNumber = GrpGenSn.genGrpSn(inparam).getString("SERIAL_NUMBER");
        merchInfo.put("SERIAL_NUMBER", serialNumber);

        // 2-4 添加帐户信息
        IData acctInfo = new DataMap();

        // 2- 根据客户编号查询帐户信息
        String custId = custInfo.getString("CUST_ID");
        IDataset acctInfos = AcctInfoQry.getAcctInfoByCustId(custId);

        boolean ifAcct = false;
        if(StringUtils.isNotBlank(map.getString("ACCOUNT_NUMBER", "")))
        {
            String acctNumber = map.getString("ACCOUNT_NUMBER");
            if(acctInfos!=null){
                for(int acct1=0;acct1<acctInfos.size();acct1++)
                {
                    if(acctInfos.getData(acct1).getString("ACCT_ID").equals(acctNumber))
                    {
                        ifAcct = true;
                        break;
                    }
                }
            }
        }


        // 3- 分情况处理查询结果，存在即用已有的帐户，不存在则新增帐户
        if (acctInfos == null || acctInfos.size() == 0 || (IDataUtil.isNotEmpty(acctInfos)&&(!ifAcct)&&(StringUtils.isNotBlank(map.getString("ACCOUNT_NUMBER", "")))) )
        {// 帐户信息不存在，需要新增帐户
            acctInfo.put("ACCT_IS_ADD", true);

            // 拼装帐户信息
            IData acct = new DataMap();
            acct.put("SAME_ACCT", "0");
            acct.put("PAY_MODE_CODE", "0");
            acct.put("ACCT_ID", map.getString("ACCOUNT_NUMBER",SeqMgr.getAcctId()));
            acct.put("PAY_NAME", custInfo.getString("CUST_NAME"));
            acct.put("RSRV_STR8", "1");// 打印模式 分产品项展示费用
            acct.put("RSRV_STR9", "1");// 发票模式：实收发票

            acctInfo.put("ACCT_INFO", acct);
        }
        else
        {// 帐户信息存在，直接返回帐户编号
            acctInfo.put("ACCT_IS_ADD", false);

            acctInfo.put("ACCT_ID", map.getString("ACCOUNT_NUMBER",acctInfos.getData(0).getString("ACCT_ID")));
        }


        if (acctInfo.getBoolean("ACCT_IS_ADD") == true)
        {
            merchInfo.put("ACCT_IS_ADD", true);
            merchInfo.put("ACCT_INFO", acctInfo.getData("ACCT_INFO"));
        }
        else
        {
            merchInfo.put("ACCT_IS_ADD", false);
            merchInfo.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        }

        // 2-5 添加用户类别，默认为省级集团
        IData userInfo = new DataMap();
        userInfo.put("USER_DIFF_CODE", "2");// 用户类别：0-普通个人，1-地市级集团，2-省级集团，3-跨省集团
        userInfo.put("CONTRACT_ID", map.getString("CONTRACT_ID", ""));
        IDataset pOAttTypeInfoList = IDataUtil.getDataset("POATT_TYPE", map);
        IDataset pOAttCodeInfoList = IDataUtil.getDataset("POATT_CODE", map);//合同编码
        if(IDataUtil.isNotEmpty(pOAttTypeInfoList)){
            for(int i=0;i<pOAttTypeInfoList.size();i++){
                String pOAttType = pOAttTypeInfoList.get(i).toString();
                if(StringUtils.equals("1", pOAttType)){//合同附件
                    String contractId = pOAttCodeInfoList.get(i).toString();
                    userInfo.put("CONTRACT_ID",contractId);
                }
            }
        }
        merchInfo.put("USER_INFO", userInfo);

        // 2-6 添加资源信息
        IDataset resInfos = new DatasetList();
        getResInfo(serialNumber, resInfos);
        merchInfo.put("RES_INFO", resInfos);

        // 2-7 添加商品资费信息
        IDataset merchDiscnt = new DatasetList();
        getJKDTMerchDiscnt(map, merchDiscnt, proPoNumber);
        merchInfo.put("ELEMENT_INFO", merchDiscnt);

        // 2-8 添加BBOSS特有商品信息
        IData bbossMerchInfo = new DataMap();
        getBbossMerchInfo(groupId, map, bbossMerchInfo);
        merchInfo.put("BBOSS_MERCH_INFO", bbossMerchInfo);

        // 3- 添加反向受理标记(反向受理不发服务开通)
        merchInfo.put("IN_MODE_CODE", "6");

        // 4- 添加商品信息至返回结果中
        returnVal.put("MERCH_INFO", merchInfo);

        // 5- 商品附件信息发往客管侧保存
        GrpCommonBean.sendAttInfoListToCliMng(map,custId,proPoNumber);
    }

    /*
     * @description 集客大厅
     * @author
     * @date
     */
    protected static void getJKDTMerchDiscnt(IData map, IDataset merchDiscnt, String proPoNumber) throws Exception
    {
        // 1- 获取商品套餐、资费信息
        //IDataset tempRatePolicyIdSet = IDataUtil.getDataset("PRSRV_STR1", map);// 商品套餐ID
        IDataset tempPlanIdSet = IDataUtil.getDataset("PLAN_ID", map);// 资费计划标识
        IDataset tempDescriptionSet = IDataUtil.getDataset("DESCRIPTION", map);// 资费描述
        IDataset tempActionSet = IDataUtil.getDataset("ACTION", map);// 资费操作代码

        // 2- 拼装资费信息
        for (int i = 0; i < tempPlanIdSet.size(); i++)
        {

            // 2-2 循环资费计划标志拼装商品资费信息
            IDataset planIdset = IDataUtil.modiIDataset(tempPlanIdSet.get(i), "PLAN_ID");
            IDataset descriptionSet = IDataUtil.modiIDataset(tempDescriptionSet.get(i), "DESCRIPTION");
            IDataset actionSet = IDataUtil.modiIDataset(tempActionSet.get(i), "ACTION");

            //2-3获取icb参数
            IDataset paraCodeList=IDataUtil.getDataset("PARA_CODE", map);
            IDataset paraNameList=IDataUtil.getDataset("PARANAME", map);
            IDataset paraValueList=IDataUtil.getDataset("VALUE", map);
            for (int j = 0; j < planIdset.size(); j++)
            {
                // 2-2-1 定义商品元素对象
                IData elementInfo = new DataMap();
                // 2-2-2 添加资费实例ID，受理时默认为""
                elementInfo.put("INST_ID", "");
                // 2-2-3 添加元素类型，资费类型对应为"D"
                elementInfo.put("ELEMENT_TYPE_CODE", "D");
                // 2-2-4 添加资费状态  删除状态资费直接返回
                String action = GrpCommonBean.nullToString(actionSet.get(j));
                if("0".equals(action)){
                    continue;
                }
                elementInfo.put("MODIFY_TAG","0");
                // 2-2-5 添加产品产品编号
                elementInfo.put("PRODUCT_ID", proPoNumber);
                // 2-2-6 添加元素ID
                String planId = GrpCommonBean.nullToString(planIdset.get(j));
                String elementId = GrpCommonBean.merchToProduct(planId, 1, proPoNumber);
                if(StringUtils.isEmpty(elementId)){
                	continue;
                }
                elementInfo.put("ELEMENT_ID", elementId);
                // 2-2-7 添加包信息

                // 2-1 根据商品套餐ID获取包ID
                // 集客大厅现在只有商品ID和子商品ID,来查询子商品所属组的组ID
                IData packageinfo = PkgElemInfoQry.getDiscntsByDiscntCode(elementId,proPoNumber,"");

                elementInfo.put("PACKAGE_ID", packageinfo.getString("PACKAGE_ID","0"));
                // 2-2-8 添加元素名称
                String desc = "";
                if (descriptionSet.size() > j)
                {
                    desc = GrpCommonBean.nullToString(descriptionSet.get(j));
                }
                elementInfo.put("ELEMENT_NAME", desc);
                // 2-2-9 添加开始时间
                elementInfo.put("START_DATE", SysDateMgr.getSysTime());
                // 2-2-10 添加结束时间
                elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                // 2-2-11 添加资费参数
                // 12- 添加参数信息
                IDataset merchIcbParamInfo=new DatasetList();
                if(IDataUtil.isNotEmpty(paraCodeList)){
                    IDataset icbCodeList=paraCodeList.getDataset(j);
                    IDataset icbValueList=paraValueList.getDataset(j);
                    IDataset icbNameList=paraNameList.getDataset(j);
                    GrpCommonBean.getMerchIcbParamInfo(merchIcbParamInfo,elementId,icbCodeList,icbNameList,icbValueList);
                }

                if (IDataUtil.isEmpty(merchIcbParamInfo))
                {
                    elementInfo.put("ATTR_PARAM", "");
                }
                else
                {
                    elementInfo.put("ATTR_PARAM", merchIcbParamInfo);
                }

                //    elementInfo.put("ATTR_PARAM", "");
                // 2-2-12 将资费添加至资费信息中
                merchDiscnt.add(elementInfo);
            }
        }
    }

    //集客大厅
    protected static void makeJKDTMerchPInfoData(IData map, IData returnVal) throws Exception
    {
        // 1- 定义符合基类处理的产品数据集
        IDataset productInfoset = new DatasetList();

        // 2- 获取产品数据的一级信息（按照一级BOSS与CRM的接口规范，将黑色部分字段设置为一级信息，红色部分字段设置为二级信息，黄色部分字段设置为三级信息）
        IData firstProductInfo = new DataMap();
        GrpCommonBean.getFirstProductInfo(map, firstProductInfo);

        // 3- 循环处理每个产品，处理后的产品数据符合基类处理的数据结构
        IDataset productNumberSet = firstProductInfo.getDataset("PRO_NUMBER");
        IDataset productOfferIdSet = firstProductInfo.getDataset("PRO_ORDER_RELATION_NUMBER");
        IDataset productOrderIdSet = firstProductInfo.getDataset("PRO_ORDER_NUMBER");

        for (int i = 0; i < productNumberSet.size(); i++)
        {
            // 3-1 校验是否需要中断本次循环(例如产品用户资料已经存在的情况，产品操作类型依然为新增或者预受理等)
            boolean result = isContinue(i, firstProductInfo);
            if (result == false)
            {
                continue;
            }

            // 3-2 获取产品数据二级信息
            IData secondProductInfo = new DataMap();
            GrpCommonBean.getSecondProductInfo(i, firstProductInfo, secondProductInfo);

            // 3-3 定义产品信息变量
            IData productInfo = new DataMap();

            // 3-4 添加客户编号
            productInfo.put("CUST_ID", custId);

            // 3-5 添加产品编号
            String proNumber = GrpCommonBean.nullToString(productNumberSet.get(i)).replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
            String productId = GrpCommonBean.merchJKDTToProduct(proNumber, 2, null);// 产品编号转化为本地产品编号

            productInfo.put("PRODUCT_ID", productId);

            // 3-6 添加帐户信息
            productInfo.put("ACCT_IS_ADD", true);// 这里的值为true,既产品生成台帐时也会新增账户，但是账户编号会根据商品的返回值来定

            // 3-7 添加元素信息(包括服务与资费)
            IDataset elementInfo = new DatasetList();
            getJKDTProductElementInfo(productId, secondProductInfo, elementInfo);
            //(1立即生效，2下账期生效，4下一天生效)
            String poRatePolicyEffRule =map.getString("PO_RATE_POLICY_EFF_RULE","1") ;// 套餐生效规则  daidl
            elementInfo = CreateBBossUserDataBean.dealStartDate(elementInfo, poRatePolicyEffRule);
            if (null == elementInfo || elementInfo.size() == 0)
            {
                productInfo.put("ELEMENT_INFO", "");
            }
            else
            {
                productInfo.put("ELEMENT_INFO", elementInfo);
            }

            // 3-8 添加产品属性信息
            IData productParam = new DataMap();
            IDataset productParamInfoList = GrpCommonBean.getProductParamInfo(productId, secondProductInfo, productParam);
            filterProductParam(productParam);
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 3-9 添加虚拟手机号
            IData idGen = new DataMap();
            idGen.put("GROUP_ID", returnVal.getData("MERCH_INFO").getData("BBOSS_MERCH_INFO").getString("GROUP_ID"));
            idGen.put("PRODUCT_ID", productId);
            String serialNumber = GrpGenSn.genGrpSn(idGen).getString("SERIAL_NUMBER");
            serialNumber = GrpCommonBean.dealSerialNumber(productId, serialNumber, productParamInfoList);
            productInfo.put("SERIAL_NUMBER", serialNumber);

            // 3-10 添加产品操作类型(仅供判断是否为归档报文用)
            IDataset productOperTypes = secondProductInfo.getDataset("PRODUCT_OPER");
            String productOperCode = GrpCommonBean.nullToString(productOperTypes.get(0));
            productInfo.put("PRODUCT_OPER_CODE", productOperCode);

            // 3-11 添加产品订购关系ID
            String productOfferId = GrpCommonBean.nullToString(productOfferIdSet.get(i)).replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "");
            IDataset merchpUserInfoList =UserEcrecepProductInfoQry.qryEcrEceppInfosByPro(productOfferId,null);
            IDataset merchpTradeInfoList =TradeEcrecepProductInfoQry.getJKDTMerchpOnlineByProductofferId(productOfferId,null);
            if(IDataUtil.isNotEmpty(merchpUserInfoList) || IDataUtil.isNotEmpty(merchpTradeInfoList)){
                return;
            }

            // 3-12 添加产品订单号
            String productOrderId = GrpCommonBean.nullToString(productOrderIdSet.get(i)).replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "");

            // 3-13 添加BBOSS特有产品信息
            IData bbossProductInfo = new DataMap();
            getProductBaseInfo(productId, productOperCode, productOfferId, productOrderId, bbossProductInfo);
            String busiSign = map.getString("BUSI_SIGN", "");
            if (IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign) // BBOSS 业务流程管理接口
                    || IntfField.SubTransCode.BbossGrpManagerBiz.value.equals(busiSign + "_1_0"))
            {
                bbossProductInfo.put("BBOSS_MANAGE_CREATE", true);
            }

            productInfo.put("BBOSS_PRODUCT_INFO", bbossProductInfo);

            IData userInfo = new DataMap();
            userInfo.put("CONTRACT_ID", map.getString("CONTRACT_ID", ""));
            productInfo.put("USER_INFO", userInfo);

            // 3-15 添加反向受理标记(反向受理不发服务开通)
            productInfo.put("IN_MODE_CODE", "6");

            // 3-16 将产品信息添加至产品数据集
            productInfoset.add(productInfo);
        }

        // 4- 校验集团产品受理程序是否继续进行
        if (null == productInfoset || productInfoset.size() == 0)
        {
            breakType = "1";
        }

        // 5- 添加产品信息至返回结果中
        returnVal.put("ORDER_INFO", productInfoset);
    }
    
    
    
    protected static void getJKDTProductElementInfo(String productId, IData secondProductInfo, IDataset elementInfo) throws Exception
    {
        // 1- 添加必须服务
        IDataset forceSvcs = ProductInfoQry.getProductForceSvc(productId, CSBizBean.getUserEparchyCode());
        if(IDataUtil.isNotEmpty(forceSvcs))
        {
            for (int i = 0; i < forceSvcs.size(); i++)
            {
                IData forceSvc = forceSvcs.getData(i);
                // 1-1 拼装服务数据
                IData svcElement = new DataMap();
                getElementInfo(forceSvc, svcElement);
                // 1-2 将拼好的服务元素放入元素集
                elementInfo.add(svcElement);
            }
        }

        // 2- 定义必选元素编号数据集(一级BOSS发送过来的资费与本地获取到的必选资费也许会有重复，一级BOSS发送过来的资费编号放入到该List,后续添加必选资费时校验重复)
        IData elementIdMap = new DataMap();

        // 3- 添加一级BOSS传递的资费信息
        IDataset ratePlanIdList = secondProductInfo.getDataset("RATE_PLAN_ID");
        IDataset ratePlanActionList = secondProductInfo.getDataset("RATE_PLAN_ACTION");
        for (int j = 0; j < ratePlanIdList.size(); j++)
        {
            // 3-1 没有与本省资费编码存在对应关系的资费不添加
            String ratePlanId = GrpCommonBean.nullToString(ratePlanIdList.get(j));
            if ("".equals(ratePlanId))
            {
                continue;
            }
            // 3-2 报文中被删除的资费不添加
            String ratePlanAction = GrpCommonBean.nullToString(ratePlanActionList.get(j));
            if("0".equals(ratePlanAction)){
                continue;
            }

            // 3-3 添加新增的资费信息
            String elementId = GrpCommonBean.merchJKDTToProduct(ratePlanId, 1, productId);// add by xuxf20140606
            elementIdMap.put(elementId, elementId);
            getJKDTProductRateInfo(j, productId, elementId, secondProductInfo, elementInfo);//daidl
        }

        // 4- 添加必选资费
        IDataset forceDiscnts = ProductInfoQry.getProductForceDiscnt(productId);
        if(IDataUtil.isNotEmpty(forceDiscnts))
        {
            for (int i = 0; i < forceDiscnts.size(); i++)
            {
                IData forceDct = forceDiscnts.getData(i);
                // 4-1 获取资费编号并判断资费编号在必选元素编号集中是否存在，存在说明重复，进行下一次循环
                String dctId = forceDct.getString("ELEMENT_ID");
                if (elementIdMap.containsKey(dctId))
                {
                    continue;
                }
                // 4-2 拼装资费数据
                IData dctElement = new DataMap();
                getElementInfo(forceDct, dctElement);
                // 4-3 将拼好的服务元素放入元素集
                elementInfo.add(dctElement);
            }
        }

    }

    /**
     *@descriptin 反向受理时，需要过滤参数状态为删除的数据
     *@author xunyl
     *@date 2015-09-15
     */
    private static void filterProductParam(IData inparam)throws Exception{
        //1- 获取产品参数
        IDataset productparamList =inparam.getDataset("PRODUCT_PARAM");
        if(IDataUtil.isEmpty(productparamList)){
            return;
        }

        //2- 循环过滤状态为删除的数据
        for(int i=0;i<productparamList.size();i++){
            IData productParam = productparamList.getData(i);
            String state = productParam.getString("STATE");
            if(StringUtils.equals(GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_DEL.getValue(), state)){
                productparamList.remove(i);
                i=i-1;
            }
        }
    }
    
    /**
     *@descriptin 反向受理时，拼装商品订购的联系人信息
     *@author songxw
     *@date 2018-10-17
     */
    protected static void getContactorInfo(IData map, IDataset contactorInfos) throws Exception{
        IDataset contactorTypeList = IDataUtil.getDataset("CONTACTOR_TYPE", map);
        IDataset contactorNameList = IDataUtil.getDataset("CONTACTOR_NAME", map);
        IDataset contactorPhoneList = IDataUtil.getDataset("CONTACTOR_PHONE", map);
        if(IDataUtil.isNotEmpty(contactorTypeList)){      
            for(int i=0;i<contactorTypeList.size();i++){
            	IData contactorInfo = new DataMap();
                contactorInfo.put("CONTACTOR_TYPE_CODE", contactorTypeList.get(i).toString());
                contactorInfo.put("CONTACTOR_NAME", contactorNameList.get(i).toString());
                contactorInfo.put("CONTACTOR_PHONE", contactorPhoneList.get(i).toString());
                if("5".equals(contactorTypeList.get(i).toString())){//5-订单提交人员，此字段必填
                	String staffNumber = map.getString("STAFF_NUMBER", "");
                	if(!"".equals(staffNumber)){//根据总部用户名查出本省工号
	                    IDataset staffDataset = StaffInfoQry.qryStaffInfoByBboss(staffNumber);
	                    if (IDataUtil.isNotEmpty(staffDataset)){
	                    	staffNumber = staffDataset.getData(0).getString("STAFF_ID");
	                    }
                	}
                	contactorInfo.put("STAFF_NUMBER", staffNumber);
                }else{
                	contactorInfo.put("STAFF_NUMBER", "-");
                }
                contactorInfos.add(contactorInfo);
            }  
        }
    }
}
