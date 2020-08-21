package com.asiainfo.veris.crm.order.soa.group.groupintf.reverse.pbossorderdeal;

import java.util.Map;

import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot.InstancePfQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.WlwBusiHelper;
import com.asiainfo.veris.crm.order.soa.script.rule.iot.IotCheck;

public class PBossOrderDeal
{
    /*
     * @description pboss瞬时报文处理
     * @author liuzz
     * @date 2016-08-15
     */
    public static IData dealPbossOrder(String ecCustNumber, IData map) throws Exception
    {
        //一、查询集团客户信息
        IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(ecCustNumber, null);

        if (groupInf == null || groupInf.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_899, ecCustNumber);
        }
        String custId = groupInf.getData(0).getString("CUST_ID");
        String groupId = groupInf.getData(0).getString("GROUP_ID");
        String grpEparchyCode = groupInf.getData(0).getString("EPARCHY_CODE");
        String groupName = groupInf.getData(0).getString("GROUP_NAME");

        //二、查询集团用户信息（根据物联网平台用户ID查询本地用户id）
        String subsId = IDataUtil.chkParam(map,"SUBS_ID");
        String operCode = IDataUtil.chkParam(map,"OPR_CODE");
        String operSeq = map.getString("OPR_SEQ");
        IData userInfo = InstancePfQuery.queryUserBySubsIdAndInstType(subsId, "U", Route.CONN_CRM_CG);
        String userId = null;
        String serialNumber = "";
        String productId = "";
        if (IDataUtil.isNotEmpty(userInfo))
        {
            if ("01".equals(operCode))
            {
                //已订购，请勿重复订购
                CSAppException.apperr(GrpException.CRM_GRP_713, "已存在订购信息，请勿重复订购！");
            }
            userId = userInfo.getString("USER_ID");
            IData user = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
            if (IDataUtil.isEmpty(user))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "没有查询到有效的集团用户信息！");
            }
            serialNumber = user.getString("SERIAL_NUMBER");
            productId = user.getString("PRODUCT_ID");
        }
        else if (IDataUtil.isEmpty(userInfo) && !"01".equals(operCode))
        {
            //没有此业务无法变更或注销
            CSAppException.apperr(GrpException.CRM_GRP_713, "不存在订购关系，无法进行新增以外的其他操作！");
        }

        IData reqData = new DataMap();
        reqData.put("SERIAL_NUMBER", serialNumber);
        reqData.put("USER_ID", userId);
        reqData.put("USER_EPARCHY_CODE", grpEparchyCode);
        reqData.put("IS_REVERSE", true);
        reqData.put("REVERSE_OPER_SEQ", operSeq);
        if ("02".equals(operCode))
        {
            reqData.put("PRODUCT_ID", productId);
            reqData.put("REASON_CODE", "10");
            return reqData;
        }
        //三、获取产品、优惠、服务信息
        IDataset productParam = new DatasetList();
        IDataset serviceParamInfo = new DatasetList();
        IDataset elementInfo = new DatasetList();
        IData insMap = new DataMap();
         
        IDataset prodInfos = map.getDataset("PROD_INFO");
        for (int i = 0; i < prodInfos.size(); i++)
        {
            IData prodInfo = prodInfos.getData(i);
            String prodId = prodInfo.getString("PROD_ID");
            String operType = prodInfo.getString("OPER_TYPE");
            String prodInstId = prodInfo.getString("PROD_INST_ID");
            IDataset prodAttrs = prodInfo.getDataset("PROD_ATTR_INFO");
           

            //查询是否是产品
             Map prodCfg =  IotCheck.WLW_PRODUCT_CONFIG_MAP; 
            IData  prod = (IData) prodCfg.get(prodId); 
          
            if (IDataUtil.isNotEmpty(prod))
            {
                productId = prod.getString("PARAM_CODE");
                insMap.put(productId, prodInstId);
                productParam.addAll(prodAttrs);
                continue;
            }
            
            //查询是否是资费 
            Map discntCfg =  IotCheck.WLW_DISCNT_CONFIG_MAP; 
            IData  discnt = (IData) discntCfg.get(prodId); 
            
            if (IDataUtil.isNotEmpty(discnt))
            {
                IData discntInfo = dealElement(userId, operType, discnt, "D");
                insMap.put(discnt.getString("PARAM_CODE"), prodInstId);
                discntInfo.put("ATTR_PARAM", prodAttrs);
                elementInfo.add(discntInfo);
                continue;
            }
            
            //查询是否是服务
            Map servCfg =  IotCheck.WLW_SVC_CONFIG_MAP;
            IData  serv = (IData) servCfg.get(prodId); 
            if (IDataUtil.isNotEmpty(serv))
            {
                //处理服务
                IData servInfo = dealElement(userId, operType, serv, "S");
                insMap.put(serv.getString("PARAM_CODE"), prodInstId);
                //servInfo.put("ATTR_PARAM", prodAttrs);
                
                //服务属性 
                IData serviceParam = new DataMap();
                serviceParam.put("SERVICE_ID",serv.getString("PARAM_CODE"));
                serviceParam.put("SERVICE_PARAM", prodInfo.getDataset("SERVICE_ATTR_INFO"));   
                serviceParamInfo.add(serviceParam);
                
                //暂时写法
                IDataset attrParamSet = WlwBusiHelper.getServiceParamInfos(serviceParamInfo);
                servInfo.put("ATTR_PARAM", attrParamSet);
                
                elementInfo.add(servInfo);
                continue;
            }
        }
        
        //四、如果没有取到主产品ID，默认受理产品为车联网集团主体产品
        if (StringUtils.isEmpty(productId))
        { 
            Map prodCfg =  IotCheck.WLW_PRODUCT_CONFIG_MAP; 
            IData  defaultProd = (IData) prodCfg.get("I00010800001"); 
            if (IDataUtil.isEmpty(defaultProd))
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "缺少车联网主体产品配置，PARA_ATTR=9015！");
            }
            productId = defaultProd.getString("PARAM_CODE");
        }

        //五、查询集团服务号码，新增的情况生成服务号码
        if ("01".equals(operCode))
        {
            serialNumber = getSn(groupId, productId, grpEparchyCode);
            IDataset resInfos = new DatasetList();
            getResInfo(serialNumber, resInfos);
            reqData.put("RES_INFO", resInfos);
        }

        reqData.put("PRODUCT_ID", productId);
        reqData.put("ELEMENT_INFO", elementInfo);
        reqData.put("CUST_ID", custId);
        reqData.put("EFFECT_NOW", true);

        IData acctInfo = getAcctByCustId(custId, groupName);
        if (acctInfo.getBoolean("ACCT_IS_ADD") == true)
        {
            reqData.put("ACCT_IS_ADD", true);
            reqData.put("ACCT_INFO", acctInfo.getData("ACCT_INFO"));
        }
        else
        {
            reqData.put("ACCT_IS_ADD", false);
            reqData.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        } 
        
        if (IDataUtil.isNotEmpty(productParam))
        {
            IDataset productParamInfo = new DatasetList();
            IData prodParam = new DataMap();
            prodParam.put("PRODUCT_ID", productId);
            prodParam.put("PRODUCT_PARAM", productParam);
            reqData.put("PRODUCT_PARAM_INFO", productParamInfo);
        }
        
        //服务属性中包含黑白名单信息设置
//        if (IDataUtil.isNotEmpty(serviceParamInfo))
//        {
//            IDataset blackWhiteInfos =  WlwBusiHelper.getBlackParamFormServiceParamInfo(serviceParamInfo);
//            reqData.put("BLACK_WHITE_INFO", blackWhiteInfos);
//        }

        reqData.put("IS_REVERSE", true);//反向标记
        insMap.put("SUBS_ID", subsId);//全网用户ID
        reqData.put("PBOSS_INST", insMap);//全网实例ID
        reqData.put("IS_NEED_TRANS", false);
        return reqData;
    }

    public static IData dealElement(String userId, String operType, IData element, String elementType) throws Exception
    {
        String elementId = element.getString("PARAM_CODE");
        //根据userId和elementId查询实例
        IDataset userElement = new DatasetList();
        if (StringUtils.isNotEmpty(userId))
        {
            if ("D".equals(elementType))
            {
                userElement = UserDiscntInfoQry.getAllDiscntByUser(userId, elementId);
            }
            else if ("S".equals(elementType))
            {
                userElement = UserSvcInfoQry.getSvcUserId(userId, elementId);
            }
        }

        //处理资费
        if (!"01".equals(operType) && IDataUtil.isEmpty(userElement))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713,
                                  "未查询到【" + element.getString("PARA_CODE1") + "】" + element.getString("PARAM_NAME")
                                          + "对应的订购信息，无法进行新增以外的其他操作！");
        }
        else if ("01".equals(operType) && IDataUtil.isNotEmpty(userElement))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713,
                                  "集团已存在【" + element.getString("PARA_CODE1") + "】" + element.getString("PARAM_NAME")
                                          + "对应的订购信息，无法进行新增操作！");

        }
        String instId = "";
        String startDate = SysDateMgr.getSysDate();
        String endDate = SysDateMgr.getTheLastTime();
        if (IDataUtil.isNotEmpty(userElement))
        {
            instId = userElement.getData(0).getString("INST_ID");
            startDate = userElement.getData(0).getString("START_DATE");
            endDate = userElement.getData(0).getString("END_DATE");
        }
        String modifyTag = "0";
        String operState = "";
        if ("01".equals(operType))
        {
            modifyTag = "0";//新增
        }
        else if ("02".equals(operType))
        {
            modifyTag = "1";//删除
            endDate = SysDateMgr.getSysDate();
        }
        else if ("03".equals(operType))
        {
            modifyTag = "2";//变更
            operState = "08";
        }
        else if ("04".equals(operType))
        {//暂停恢复还要再处理
            modifyTag = "2";//暂停
            operState = "04";
        }
        else if ("05".equals(operType))
        {
            modifyTag = "2";//恢复
            operState = "05";
        }
        IData elementInfo = new DataMap();
        elementInfo.put("INST_ID", instId);
        elementInfo.put("ELEMENT_TYPE_CODE", elementType);
        elementInfo.put("MODIFY_TAG", modifyTag);
        elementInfo.put("OPER_STATE", operState);
        elementInfo.put("PACKAGE_ID", element.getString("PARA_CODE4"));
        elementInfo.put("PRODUCT_ID", element.getString("PARA_CODE5"));
        elementInfo.put("ELEMENT_ID", elementId);
        elementInfo.put("START_DATE", startDate);
        elementInfo.put("END_DATE", endDate);
        return elementInfo;
    }

    public static String getSn(String groupId, String productId, String grpEparchyCode) throws Exception
    {
        // 避免服务号码的重复 add begin
        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, grpEparchyCode);
        String serialNumber = "";
        for (int i = 0; i < 10; i++)
        {
            IData grpSnData = CSAppCall.call("SS.ProductInfoSVC.genGrpSn", param).getData(0);

            serialNumber = grpSnData.getString("SERIAL_NUMBER", "");

            if (StringUtils.isEmpty(serialNumber))
            {
                break;
            }

            IData userList = UserInfoQry.getUserInfoBySN(serialNumber);
            param.clear();
            param.put("SERIAL_NUMBER", serialNumber);
            param.put(Route.USER_EPARCHY_CODE, grpEparchyCode);
            IDataset tradeList = CSAppCall.call("CS.TradeInfoQrySVC.getTradeInfoBySn", param);

            if (IDataUtil.isEmpty(userList) && IDataUtil.isEmpty(tradeList))
            {
                break;
            }
        }
        // 避免服务号码的重复 add end
        return serialNumber;
    }

    /*
     * @description 根据客户编号查询帐户信息
     * @author xunyl
     * @date 2013-06-22
     */
    protected static IData getAcctByCustId(String custId, String payName) throws Exception
    {
        // 1- 定义返回数据
        IData accoutResult = new DataMap();

        // 2- 根据客户编号查询帐户信息
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
            acctInfo.put("PAY_NAME", payName);
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
}
