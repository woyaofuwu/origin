
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;

public class ProcessGrpForWsBean extends CSBizBean
{
    public static String genGrpSn(IData idGen) throws Exception
    {

        String groupId = idGen.getString("GROUP_ID", "");
        String productId = idGen.getString("PRODUCT_ID", "");

        IData data = new DataMap();

        // 得到服务号码生成规则配置
        data.put("ID", productId);
        data.put("ID_TYPE", "P");
        data.put("ATTR_OBJ", "0");
        data.put("ATTR_CODE", "genGrpSn");
        IDataset dataList = AttrBizInfoQry.getBizAttr(productId, "P", "0", "genGrpSn", null);

        if (IDataUtil.isEmpty(dataList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_434, productId);
        }

        // 得到配置数据
        data = dataList.getData(0);
        String attrValue = data.getString("ATTR_VALUE", "");

        // 手工输入标志 1 = 手工输入，可改写 ；0 = 自动生成，不能改写

        String Input_Tag = "0";

        // 得到配置名称，根据[;]
        String ruleName[] = attrValue.split(";");

        // 根据配置规则生成服务号码，如：[PRODUCT_ID,1,2;EPARCHY_CODE,3,4]
        String strEparchyCode = CSBizBean.getTradeEparchyCode();
        String strCityCode = getVisit().getCityCode();
        String preSN = "";
        String seqVpmnId = "";

        String sn = "";
        String tmp = "";

        String ruelValue[];
        String value = "";
        int valueMin = -1;
        int valueMax = -1;
        int length = 0;

        for (String element : ruleName)
        {
            value = element;

            // 得到配置名称，根据[,]
            ruelValue = value.split(",");

            // 初始化
            value = "";
            valueMin = -1;
            valueMax = -1;

            // 得到配置项目
            for (int index = 0, valueSize = ruelValue.length; index < valueSize; index++)
            {
                tmp = ruelValue[index].trim();

                switch (index)
                {
                    case 0:
                    {
                        value = tmp; // 名称
                        break;
                    }
                    case 1:
                    {
                        if ((tmp == null) || tmp.equals(""))
                        {
                            valueMin = -1;
                        }
                        else
                        {
                            valueMin = Integer.valueOf(tmp); // sub小值
                        }
                        break;
                    }
                    case 2:
                    {
                        if ((tmp == null) || tmp.equals(""))
                        {
                            valueMax = -1;
                        }
                        else
                        {
                            valueMax = Integer.valueOf(tmp); // sub大值
                        }
                        break;
                    }
                }
            }

            if (value.equals("PRODUCT_ID"))
            {
                // 如果是产品
                sn = sn + productId;
            }
            else if (value.equals("EPARCHY_CODE"))
            {
                // 如果是地州
                sn = sn + strEparchyCode.substring(valueMin, valueMax);
            }
            else if (value.equals("CITY_CODE"))
            {
                // 如果是县市
                sn = sn + strCityCode.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_VPMN_ID"))
            {
                // 如果是序列号
                seqVpmnId = SeqMgr.getVpmnIdIdForGrp();
                sn = sn + seqVpmnId.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_VPN_ID"))
            {
                // 如果是序列号
                seqVpmnId = SeqMgr.getVpnIdIdForGrp();
                sn = sn + seqVpmnId.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_VPMN_SERIAL"))
            {
                // 如果是序列号
                seqVpmnId = SeqMgr.getVpmnSerialForGrp();
                sn = sn + seqVpmnId.substring(valueMin, valueMax);
            }
            else if (value.equals("GROUP_ID"))
            {
                sn = sn + groupId;
            }
            else if (value.indexOf("SELECT_") != -1)
            {
                // 对于不同的业务，扩展getGrpSnBySelectParam方法即可
                // preSN = CommparaInfoQry.getGrpSnBySelectParam(productId, groupId);
                CSAppException.apperr(GrpException.CRM_GRP_1);
                preSN = null;
                sn = sn + preSN;
            }
            else if (value.equals("SELF_DEF"))
            {
                // 自定义
                length = value.length();
                continue;
            }
            else if (value.equals("INPUT_TAG"))
            {
                // 输入
                Input_Tag = "1";
            }
            else
            {
                // 其他的默认固定字符串
                sn = sn + value;
            }
        }
        return sn;
    }

    /**
     * 解析数据格式
     * 
     * @param srcData
     * @param keyList
     * @param split
     * @return
     * @throws Exception
     */
    public static IDataset parserHttpDataToDataset(IData srcData, IDataset keyList, String split) throws Exception
    {
        IDataset retDataset = new DatasetList();

        if (IDataUtil.isEmpty(srcData))
            return retDataset;

        if (IDataUtil.isEmpty(keyList))
            return retDataset;

        IData retData = new DataMap();

        if (StringUtils.isBlank(split))
            split = ",";

        for (int i = 0, row = keyList.size(); i < row; i++)
        {
            String key = keyList.get(i).toString();
            String valueListStr = srcData.getString(key);
            String[] valueArray = StringUtils.split(valueListStr, split);

            IDataset valuelist = new DatasetList();
            if (valueArray != null && valueArray.length > 0)
            {
                for (int k = 0, arraySize = valueArray.length; k < arraySize; k++)
                {
                    valuelist.add(valueArray[k]);
                }
            }
            else
            {
                valuelist.add(valueListStr);
            }
            retData.put(key, valuelist);
        }

        retDataset.add(retData);

        return retDataset;
    }

    private String groupId = "";

    private String productId = "";

    private String custId = "";

    private String grpSn = "";

    /**
     * 处理集团产品变更
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset changeGroupUserIntf(IData inData) throws Exception
    {
        IDataset userInfoList = UserInfoQry.getUserInfoByCstIdProIdForGrp(custId, productId, null);

        if (IDataUtil.isEmpty(userInfoList))
        {
            String productName = UProductInfoQry.getProductNameByProductId(productId);
            CSAppException.apperr(GrpException.CRM_GRP_617, groupId, productName);
        }

        IData userData = userInfoList.getData(0);

        String userId = userData.getString("USER_ID");
        String userEparchyCode = userData.getString("EPARCHY_CODE");

        inData.put("USER_ID", userId);

        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("IF_CENTRETYPE", null);
        svcData.put("REMARK", "集团门户接口变更");
        svcData.put("RES_INFO", null);
        svcData.put("POST_INFO", null);
        svcData.put("ASKPRINT_INFO", null);

        IDataset productElementList = getChgUsElements(inData);

        IDataset userElementList = new DatasetList();
        IDataset grpPackageElementList = new DatasetList();

        if (IDataUtil.isNotEmpty(productElementList))
        {
            for (int i = 0, row = productElementList.size(); i < row; i++)
            {
                IData elementData = productElementList.getData(i);

                String productMode = elementData.getString("PRODUCT_MODE");

                if (GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue().equals(productMode) || GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue().equals(productMode))
                {
                    userElementList.add(elementData);
                }
                else if (GroupBaseConst.PRODUCT_MODE.MEM_BASE_PRODUCT.getValue().equals(productMode) || GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue().equals(productMode)
                        || GroupBaseConst.PRODUCT_MODE.MEM_PLUS_PRODUCT.getValue().equals(productMode))
                {
                    grpPackageElementList.add(elementData);
                }
            }
        }

        svcData.put("GRP_PACKAGE_INFO", grpPackageElementList);
        svcData.put("ELEMENT_INFO", userElementList);
        svcData.put(Route.USER_EPARCHY_CODE, userEparchyCode);

        // 产品参数信息
        IData productParamData = getChgUsProductParam(inData);
        IData savePParamData = new DataMap();
        savePParamData.put("PRODUCT_ID", productId);
        savePParamData.put("PRODUCT_PARAM", IDataUtil.iData2iDataset(productParamData, "ATTR_CODE", "ATTR_VALUE"));
        svcData.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(savePParamData));

        if ("6200".equals(productId))// 集团彩铃
        {
            return CSAppCall.call("SS.ChangeColorRingUserElementSVC.crtOrder", svcData);
        }

        if ("6130".equals(productId))// 融合总机
        {
            return CSAppCall.call("SS.ChangeCentrexSuperTeleUserElementSVC.crtOrder", svcData);
        }

        return CSAppCall.call("CS.ChangeUserElementSvc.changeUserElement", svcData);
    }

    /**
     * 处理集团产品受理
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset creteGroupUserIntf(IData inData) throws Exception
    {
        // 判断用户是否就已经受理产品
        IDataset userInfoList = UserInfoQry.getUserInfoByCstIdProIdForGrp(custId, productId, null);

        String rsrvTag2 = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "RSRV_TAG2", productId);

        if (IDataUtil.isNotEmpty(userInfoList) && !"M".equals(rsrvTag2))
        {
            CSAppException.apperr(GrpException.CRM_GRP_616);
        }

        // 处理默认产品参数信息
        IData productParamData = qryProductParamByProductId();

        // 处理传如的产品参数信息
        if (StringUtils.isNotBlank(inData.getString("ATTR_CODE")))
        {
            IDataset inParamList = new DatasetList();
            inParamList.add("ATTR_CODE");
            inParamList.add("ATTR_VALUE");
            // 输入参数列表
            IDataset inputParamList = parserHttpDataToDataset(inData, inParamList, null);

            for (int j = 0, jRow = inputParamList.size(); j < jRow; j++)
            {
                IData inputParamData = inputParamList.getData(j);
                if (!productParamData.containsKey(inputParamData.getString("ATTR_CODE")))
                {
                    productParamData.put(inputParamData.getString("ATTR_CODE"), inputParamData.getString("ATTR_VALUE"));
                }
            }
        }

        // 初始化产品元素
        IDataset productElementList = getInitialProduct(inData, productId);

        IDataset grpPackageElementList = new DatasetList();
        IDataset userElementList = new DatasetList();

        for (int i = 0, row = productElementList.size(); i < row; i++)
        {
            IData elementData = productElementList.getData(i);

            elementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            // 新增的时候，资费开始时间为立即
            if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementData.getString("ELEMENT_TYPE_CODE")))
            {
                elementData.put("START_DATE", SysDateMgr.getSysTime());
            }

            String productMode = elementData.getString("PRODUCT_MODE");
            // 集团用户元素和集团定制元素分类
            if (GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue().equals(productMode) || GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue().equals(productMode))
            {
                userElementList.add(elementData);
            }
            else if (GroupBaseConst.PRODUCT_MODE.MEM_BASE_PRODUCT.getValue().equals(productMode) || GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue().equals(productMode)
                    || GroupBaseConst.PRODUCT_MODE.MEM_PLUS_PRODUCT.getValue().equals(productMode))
            {
                grpPackageElementList.add(elementData);
            }
        }

        // 如果有服务参数
        if (StringUtils.isNotBlank(inData.getString("SVC_ATTR_CODE")))
        {
            IDataset keyset = new DatasetList();
            keyset.add("SVC_ATTR_CODE");
            keyset.add("SVC_ATTR_VALUE");
            IDataset svcparams = parserHttpDataToDataset(inData, keyset, null);

            // 处理ADC服务
            for (int i = 0; i < productElementList.size(); i++)
            {
                IData productElement = productElementList.getData(i);

                IDataset elements = productElement.getDataset("ELEMENTS");
                for (int y = 0; elements != null && y < elements.size(); y++)
                {
                    IData element = elements.getData(y);
                    String elementId = element.getString("ELEMENT_ID");
                    String elementType = element.getString("ELEMENT_TYPE_CODE");
                    if ("S".equals(elementType))
                    {
                        // 查TD_B_ATTR_ITEMA中ID=服务ID，ID_TYPE=‘S’，ATTR_TYPE_CODE=‘9’的，如果有，就说明是ADC服务
                        // 查询选择产品的所有服务的itema配置
                        IData elementparma = new DataMap();
                        elementparma.put("ID", elementId);
                        elementparma.put("ID_TYPE", "S");// 表示为服务
                        IDataset productsvcItema = AttrItemInfoQry.getelementItemaByPk(elementparma);
                        IDataset platSvcs = DataHelper.filter(productsvcItema, "ATTR_TYPE_CODE=9");
                        /*
                         * BIZ_IN_CODE//接入号 TEXT_ECGN_ZH//中文签名 TEXT_ECGN_EN//英文签名 OPER_STATE// 0表示新增 MODIFY_TAG// 0表示新增
                         * PLAT_SYNC_STATE//1
                         */
                        if (platSvcs != null && platSvcs.size() > 0)
                        {
                            IDataset platSvcParams = DataHelper.filter(productsvcItema, "ATTR_TYPE_CODE=0");

                            IData platParam = IDataUtil.hTable2StdSTable(platSvcParams, "ATTR_CODE", "ATTR_INIT_VALUE");
                            IDataset servParams = new DatasetList();
                            IData servParamData = new DataMap();
                            IData servParamControlData = new DataMap();
                            servParamControlData.put("PARAM_VERIFY_SUCC", "true");
                            servParamData.put("CANCLE_FLAG", "false");
                            servParamData.put("ID", elementId);
                            servParams.add(0, servParamControlData);
                            servParams.add(1, servParamData);

                            for (int j = 0; j < svcparams.size(); j++)
                            {
                                IData param = svcparams.getData(j);
                                if (!platParam.containsKey(param.getString("SVC_ATTR_CODE")))
                                {
                                    platParam.put(param.getString("SVC_ATTR_CODE"), param.getString("SVC_ATTR_VALUE"));
                                }
                            }

                            platParam = IDataUtil.replaceIDataKeyAddPrefix(platParam, "pam_");
                            servParamData.put("PLATSVC", platParam);

                            element.put("SERV_PARAM", servParams);
                        }
                    }
                }
            }
        }

        IData param = new DataMap();
        param.put("GROUP_ID", groupId);
        param.put("PRODUCT_ID", productId);
        param.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        grpSn = genGrpSn(param);

        // 调用服务
        IData svcData = new DataMap();
        svcData.put("CUST_ID", custId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("SERIAL_NUMBER", grpSn);
        svcData.put("ACCT_IS_ADD", null);
        svcData.put("ACCT_ID", getAcctInfo(custId).getString("ACCT_ID"));
        svcData.put("IF_CENTRETYPE", null);
        IData userInfo = new DataMap();
        userInfo.put("USER_DIFF_CODE", null);
        svcData.put("USER_INFO", userInfo);

        svcData.put("RES_INFO", getGrpResourceInfo(grpSn));
        svcData.put("ASKPRINT_INFO", null);
        svcData.put("GRP_PACKAGE_INFO", grpPackageElementList);
        svcData.put("PLAN_INFO", null);
        svcData.put("ELEMENT_INFO", userElementList);

        // 产品参数信息
        IData savePParamData = new DataMap();
        savePParamData.put("PRODUCT_ID", productId);
        savePParamData.put("PRODUCT_PARAM", IDataUtil.iData2iDataset(productParamData, "ATTR_CODE", "ATTR_VALUE"));
        svcData.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(savePParamData));

        svcData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        if ("6130".equals(productId))// 融合总机
        {
            return CSAppCall.call("SS.CreateCentrexSuperTeleGroupUserSVC.crtOrder", svcData);
        }
        if ("6100".equals(productId))// 移动总机
        {
            return CSAppCall.call("SS.CreateSuperTeleGroupUserSVC.crtOrder", svcData);
        }
        if ("6200".equals(productId))// 集团彩铃
        {
            return CSAppCall.call("SS.CreateColorRingGroupUserSVC.crtOrder", svcData);
        }

        return CSAppCall.call("CS.CreateGroupUserSvc.createGroupUser", svcData);
    }

    /**
     * 处理集团产品注销
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset destroyGroupUserIntf(IData inData) throws Exception
    {
        String userId = inData.getString("USER_ID");
        String userEparchyCode = getTradeEparchyCode();

        if (StringUtils.isBlank(userId))
        {
            IDataset userInfoList = UserInfoQry.getUserInfoByCstIdProIdForGrp(custId, productId, null);

            if (IDataUtil.isNotEmpty(userInfoList))
            {
                IData userData = userInfoList.getData(0);
                userId = userData.getString("USER_ID");
                userEparchyCode = userData.getString("EPARCHY_CODE");
            }
        }

        if (StringUtils.isBlank(userId))
        {
            String productName = UProductInfoQry.getProductNameByProductId(productId);
            CSAppException.apperr(GrpException.CRM_GRP_617, groupId, productName);
        }
        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.USER_EPARCHY_CODE, userEparchyCode);
        svcData.put("IF_BOOKING", "false");
        svcData.put("REASON_CODE", null);
        svcData.put("REMARK", "集团门户接口销户");
        svcData.put("IF_CENTRETYPE", null);
        svcData.put("PRODUCT_ID", productId);

        if ("6200".equals(productId))// 集团彩铃
        {
            return CSAppCall.call("SS.DestoryColorRingGroupUserSVC.crtOrder", svcData);
        }
        if ("6100".equals(productId))// 移动总机
        {
            return CSAppCall.call("SS.DestroySuperTeleGroupUserSVC.crtOrder", svcData);
        }
        if ("6130".equals(productId))// 融合总机
        {
            return CSAppCall.call("SS.DestroyCentrexSuperTeleGroupUserSVC.crtOrder", svcData);
        }

        return CSAppCall.call("CS.DestroyGroupUserSvc.destroyGroupUser", svcData);
    }

    private IData getAcctInfo(String custId) throws Exception
    {
        IDataset acctInfoList = AcctInfoQry.getAcctInfoByCustId(custId);
        if (IDataUtil.isEmpty(acctInfoList))
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_141, custId);
        }

        for (int i = 0, row = acctInfoList.size(); i < row; i++)
        {
            IData acctInfoData = acctInfoList.getData(i);
            acctInfoData.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(acctInfoData.getString("EPARCHY_CODE")));
        }

        // 获取第一个帐号作为默认帐号
        IData acctInfo = new DataMap();
        acctInfo = acctInfoList.getData(0);
        String acctId = acctInfo.getString("ACCT_ID");
        if (StringUtils.isBlank(acctId))
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_68);
        }

        IData newAcctInfo = new DataMap();
        newAcctInfo.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
        newAcctInfo.put("PAY_NAME", acctInfo.getString("PAY_NAME"));
        newAcctInfo.put("PAY_MODE_CODE", acctInfo.getString("PAY_MODE_CODE"));
        newAcctInfo.put("ACCT_ID_Box_Text", acctInfo.getString("ACCT_ID"));

        newAcctInfo.put("RSRV_STR8", "0");// 打印模式
        newAcctInfo.put("RSRV_STR9", "0");// 发票模式

        return newAcctInfo;
    }

    public IDataset getChgUsElements(IData inData) throws Exception
    {
        IDataset retDataset = new DatasetList();

        String sysTime = SysDateMgr.getSysTime();

        String discntCodeStr = inData.getString("DISCNT_CODE");

        if (StringUtils.isBlank(discntCodeStr))
            return retDataset;

        String[] discntCodeArray = StringUtils.split(discntCodeStr, ",");

        for (int i = 0, row = discntCodeArray.length; i < row; i++)
        {
            String discntCode = discntCodeArray[i];

            IData pkgElementData = PkgElemInfoQry.getDiscntsByDiscntCode(discntCode, productId, CSBizBean.getTradeEparchyCode());

            if (IDataUtil.isEmpty(pkgElementData))
            {
                CSAppException.apperr(GrpException.CRM_GRP_618, discntCode, productId);
            }

            String packageId = pkgElementData.getString("PACKAGE_ID", "");

            IDataset userDiscntList = UserDiscntInfoQry.getUserDiscntByPk(inData.getString("USER_ID"));

            boolean isAdd = false;

            for (int j = 0, jRow = userDiscntList.size(); j < jRow; j++)
            {
                IData userDiscntData = userDiscntList.getData(j);
                String userPackageId = userDiscntData.getString("PACKAGE_ID");
                String userDiscntCode = userDiscntData.getString("DISCNT_CODE");

                IData elementData = PkgElemInfoQry.getElementByElementId(userPackageId, "D", userDiscntCode);

                String cancelTag = "0";
                if (IDataUtil.isNotEmpty(elementData))
                {
                    cancelTag = elementData.getString("CANCEL_TAG");
                }

                if (packageId.equals(userPackageId) && !discntCode.equals(userDiscntCode))
                {
                    String endDate = sysTime;
                    userDiscntData.put("PRODUCT_MODE", UProductInfoQry.getProductModeByProductId(productId));

                    if ("3".equals(cancelTag))
                    {
                        endDate = SysDateMgr.getLastDateThisMonth();
                    }
                    userDiscntData.put("END_DATE", endDate);
                    userDiscntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    userDiscntData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                    isAdd = true;
                    retDataset.add(userDiscntData);
                }
            }

            if (isAdd)
            {
                String startDate = sysTime;
                String enableTag = pkgElementData.getString("ENABLE_TAG");

                if ("1".endsWith(enableTag))
                {
                    startDate = SysDateMgr.getFirstDayOfNextMonth();
                }

                pkgElementData.put("PRODUCT_ID", productId);
                pkgElementData.put("PRODUCT_MODE", UProductInfoQry.getProductModeByProductId(productId));
                pkgElementData.put("START_DATE", startDate);
                pkgElementData.put("END_DATE", SysDateMgr.getTheLastTime());
                pkgElementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                pkgElementData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                retDataset.add(pkgElementData);
            }
        }

        return retDataset;
    }

    public IData getChgUsProductParam(IData inData) throws Exception
    {
        String userId = inData.getString("USER_ID");

        IDataset userAttrList = UserAttrInfoQry.getUserAttrByUserId(userId);

        IData productParamData = IDataUtil.hTable2StdSTable(userAttrList, "ATTR_CODE", "ATTR_VALUE");

        if ("8000".equals(productId))
        {
            IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userId);

            if (IDataUtil.isNotEmpty(userVpnList))
            {
                IData userVpnData = userVpnList.getData(0);

                // VPMN产品参数，如果不传会导致联指发送错误指令导致集团功能异常
                productParamData.put("SCP_CODE", userVpnData.getString("SCP_CODE", "00"));// 湖南固定为00
                productParamData.put("MAX_USERS", userVpnData.getString("MAX_USERS", "50000")); // 集团最大用户数-初始值50000
                productParamData.put("VPN_SCARE_CODE", userVpnData.getString("VPN_SCARE_CODE", "")); // 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取
                productParamData.put("SINWORD_TYPE_CODE", userVpnData.getString("SINWORD_TYPE_CODE", "")); // 语种选择
                productParamData.put("OVER_FEE_TAG", userVpnData.getString("OVER_FEE_TAG", "")); // 呼叫超出限额处理标记
                productParamData.put("CALL_AREA_TYPE", userVpnData.getString("CALL_AREA_TYPE", "")); // 呼叫区域类型

                // 分解拼串数据
                String callnettype = userVpnData.getString("CALL_NET_TYPE", "");// 呼叫网络类型

                if (callnettype.length() >= 1)// 内网
                {
                    String CALL_NET_TYPE1 = callnettype.substring(0, 1);
                    productParamData.put("CALL_NET_TYPE1", CALL_NET_TYPE1);
                }
                if (callnettype.length() >= 2)// 网间
                {
                    String CALL_NET_TYPE2 = callnettype.substring(1, 2);
                    productParamData.put("CALL_NET_TYPE2", CALL_NET_TYPE2);
                }
                if (callnettype.length() >= 3)// 网外
                {
                    String CALL_NET_TYPE3 = callnettype.substring(2, 3);
                    productParamData.put("CALL_NET_TYPE3", CALL_NET_TYPE3);
                }
                if (callnettype.length() >= 4)// 网外号码组
                {
                    String CALL_NET_TYPE4 = callnettype.substring(3, 4);
                    productParamData.put("CALL_NET_TYPE4", CALL_NET_TYPE4);
                }

                productParamData.put("MAX_INNER_NUM", userVpnData.getString("MAX_INNER_NUM", "")); // 最大网内号码总数
                productParamData.put("MAX_OUTNUM", userVpnData.getString("MAX_OUTNUM", "")); // 最大网外号码总数
                productParamData.put("MAX_CLOSE_NUM", userVpnData.getString("MAX_CLOSE_NUM", "")); // 最大闭合用户群数
                productParamData.put("MAX_NUM_CLOSE", userVpnData.getString("MAX_NUM_CLOSE", "")); // 单个闭合用户群能包含的最大用户数
                productParamData.put("PERSON_MAXCLOSE", userVpnData.getString("PERSON_MAXCLOSE", "")); // 单个用户最大可加入的闭合群数
                productParamData.put("MAX_TELPHONIST_NUM", userVpnData.getString("MAX_TELPHONIST_NUM", "")); // 话务员最大数
                productParamData.put("WORK_TYPE_CODE", userVpnData.getString("WORK_TYPE_CODE", "")); // 行业类型-集团V网类别
                productParamData.put("SHORT_CODE_LEN", userVpnData.getString("SHORT_CODE_LEN", "")); // 集团分机号码长度
                productParamData.put("VPN_USER_CODE", userVpnData.getString("VPN_USER_CODE", "")); // 综合VPN标识（铁通号码加入V网关键点）
                productParamData.put("DISCNT_TYPE", userVpnData.getString("RSRV_STR1", "")); // 集团优惠类
                productParamData.put("PROV_GROUPID", userVpnData.getString("RSRV_STR3", ""));
                productParamData.put("SCP_GT", userVpnData.getString("RSRV_STR4", ""));// 跨省集团所在省的省ID（12&16&01）

                // 以下为跨省集团参数，目前前台页面也没有传这些参数 xiaoxl2 20120608
                productParamData.put("GRP_PAY", "");// 是否统一付费
                productParamData.put("PROV_CODE", "");// 代付费省代码
                productParamData.put("S_ID", "");// 受理单号
                productParamData.put("DIAL_TYPE", "0");// 省间用户拨号方式0-长号
            }
        }

        return productParamData;
    }

    private String getElementTypeName(IData elementType, String elementTypeCode) throws Exception
    {
        // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
        return elementType.getString(elementTypeCode);
    }

    private IDataset getGroupCustomizeInfos(IDataset disDataset, IDataset disSet, String productId) throws Exception
    {
        IDataset memProductList = ProductMebInfoQry.getMebProduct(productId);

        IDataset disElementAll = new DatasetList();
        for (int i = 0; i < memProductList.size(); i++)
        {
            IData memProduct = memProductList.getData(i);
            String memProductId = memProduct.getString("PRODUCT_ID");

            IDataset memPackageList = ProductInfoQry.getGrpPackagesByProductId(productId, CSBizBean.getTradeEparchyCode());
            for (int j = 0; j < memPackageList.size(); j++)
            {
                IData memPackage = memPackageList.getData(j);
                String limitType = memPackage.getString("LIMIT_TYPE");
                int maxNumber = memPackage.getInt("MAX_NUMBER");
                int minNumber = memPackage.getInt("MIN_NUMBER");
                String packageId = memPackage.getString("PACKAGE_ID");
                IDataset disElement = new DatasetList();
                if (!"S".equals(limitType))
                {
                    IDataset tempDisElement = ProductInfoQry.getPackageElementsNoPriv(packageId, CSBizBean.getTradeEparchyCode());
                    boolean flag = true;
                    for (int m = 0; m < disSet.size(); m++)
                    {
                        IData dis = disSet.getData(m);
                        String disCode = dis.getString("DISCNT_CODE");
                        for (int k = 0; k < tempDisElement.size(); k++)
                        {
                            IData tempDis = tempDisElement.getData(k);
                            String tempDisCode = tempDis.getString("ELEMENT_ID", "");
                            String forceTag = tempDis.getString("FORCE_TAG", "");
                            if ((!"".equals(disCode) && tempDisCode.equals(disCode) || ("1".equals(forceTag) && flag)) && !(tempDisCode.equals(disCode) && "1".equals(forceTag) && !flag))
                            {
                                tempDis.put("PACKAGE_ID", packageId);
                                tempDis.put("PRODUCT_ID", memProductId);
                                tempDis.put("PRODUCT_MODE", UProductInfoQry.getProductModeByProductId(productId));
                                tempDis.put("START_DATE", SysDateMgr.getSysTime());
                                tempDis.put("END_DATE", SysDateMgr.getTheLastTime());
                                IData data = new DataMap();
                                data.put("PRODUCT_ID", memProductId);

                                String productMode = UProductInfoQry.getProductModeByProductId(productId);

                                tempDis.put("PRODUCT_MODE", productMode);
                                disElement.add(tempDis);
                                if ("1".equals(forceTag))
                                {
                                    flag = false;
                                }
                            }
                        }
                    }

                    disElementAll.addAll(disElement);
                    int number = disElement.size();
                    if (!((maxNumber != -1 && minNumber == -1 && number <= maxNumber) || (maxNumber == -1 && minNumber != -1 && number >= minNumber) || (maxNumber == -1 && minNumber == -1) || (maxNumber != -1 && minNumber != -1
                            && number <= maxNumber && number >= minNumber)))
                    {
                        CSAppException.apperr(ElementException.CRM_ELEMENT_25, packageId, minNumber, maxNumber);
                    }
                }
            }
        }

        return disElementAll;
    }

    private IDataset getGrpResourceInfo(String serialNumber) throws Exception
    {
        IDataset resourceInfo = new DatasetList();
        IData resData = new DataMap();
        resData.put("CHECKED", "true");
        resData.put("STATE", "ADD");
        resData.put("RES_TYPE_CODE", "0");
        resData.put("RES_CODE", serialNumber);
        resourceInfo.add(resData);
        return resourceInfo;
    }

    private IDataset getInitialProduct(IData inData, String productId) throws Exception
    {
        IDataset result = new DatasetList();

        // 处理传入服务信息
        String svcStr = inData.getString("SERVICE_ID");
        IDataset svcList = new DatasetList();
        if (StringUtils.isNotBlank(svcStr))
        {
            String[] valueArray = StringUtils.split(svcStr, ",");
            for (int i = 0; i < valueArray.length; i++)
            {
                svcList.add(valueArray[i]);
            }
        }

        // 处理传入资费信息
        String discntStr = inData.getString("DISCNT_CODE");
        IDataset discntList = new DatasetList();
        if (StringUtils.isNotBlank(discntStr))
        {
            String[] valueArray = StringUtils.split(discntStr, ",");
            for (int i = 0; i < valueArray.length; i++)
            {
                discntList.add(valueArray[i]);
            }
        }

        IDataset packageList = ProductInfoQry.getGrpPackagesByProductId(productId, CSBizBean.getTradeEparchyCode());

        for (int i = 0; i < packageList.size(); i++)
        {
            IData packageData = packageList.getData(i);
            String packageId = packageData.getString("PACKAGE_ID");
            int minNumber = packageData.getInt("MIN_NUMBER");
            int maxNumber = packageData.getInt("MAX_NUMBER");
            packageData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

            IDataset tempElementList = ProductInfoQry.getPackageElementsNoPriv(packageId, CSBizBean.getTradeEparchyCode());

            IDataset tempSvcList = new DatasetList();
            IDataset tempDiscntList = new DatasetList();

            // 拆分服务和资费
            for (int j = 0, jRow = tempElementList.size(); j < jRow; j++)
            {
                IData tempElementData = tempElementList.getData(j);
                tempElementData.put("PRODUCT_ID", productId);
                tempElementData.put("PRODUCT_MODE", UProductInfoQry.getProductModeByProductId(productId));
                tempElementData.put("PACKAGE_ID", packageId);
                tempElementData.put("START_DATE", SysDateMgr.getSysTime());
                tempElementData.put("END_DATE", SysDateMgr.getTheLastTime());

                String elementTypeCode = tempElementData.getString("ELEMENT_TYPE_CODE");

                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
                {
                    tempSvcList.add(tempElementData);

                }
                else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
                {
                    tempDiscntList.add(tempElementData);

                }
            }

            IDataset disElement = new DatasetList();
            IDataset servElement = new DatasetList();

            if (IDataUtil.isNotEmpty(svcList))
            {
                for (int j = 0; j < svcList.size(); j++)
                {
                    String servCode = svcList.get(j).toString();
                    for (int k = 0; k < tempSvcList.size(); k++)
                    {
                        IData tempServ = tempSvcList.getData(k);
                        String tempServCode = tempServ.getString("ELEMENT_ID", "");
                        String forceTag = tempServ.getString("FORCE_TAG", "");
                        if ("1".equals(forceTag) || (!"1".equals(forceTag) && tempServCode.equals(servCode)))
                        {
                            if (IDataUtil.isEmpty(DataHelper.filter(servElement, "ELEMENT_ID=" + servCode)))
                            {
                                servElement.add(tempServ);
                            }
                        }
                    }
                }
            }
            else
            {
                for (int j = 0, jRow = tempSvcList.size(); j < jRow; j++)
                {
                    IData tempSvcData = tempSvcList.getData(j);
                    String forceTag = tempSvcData.getString("FORCE_TAG");

                    if ("1".equals(forceTag))
                    {
                        servElement.add(tempSvcData);
                    }
                }
            }

            // 处理资费信息
            if (IDataUtil.isNotEmpty(discntList))
            {
                for (int j = 0; j < discntList.size(); j++)
                {
                    String disCode = discntList.get(j).toString();

                    for (int k = 0; k < tempDiscntList.size(); k++)
                    {
                        IData tempDis = tempDiscntList.getData(k);
                        String tempDisCode = tempDis.getString("ELEMENT_ID", "");

                        String forceTag = tempDis.getString("FORCE_TAG", "");
                        if ("1".equals(forceTag) || (!"1".equals(forceTag) && tempDisCode.equals(disCode)))
                        {
                            if (IDataUtil.isEmpty(DataHelper.filter(servElement, "ELEMENT_ID=" + disCode)))
                            {
                                servElement.add(tempDis);
                            }
                        }
                    }
                }
            }
            else
            {
                for (int j = 0, jRow = tempDiscntList.size(); j < jRow; j++)
                {
                    IData tempDistData = tempDiscntList.getData(j);
                    String forceTag = tempDistData.getString("FORCE_TAG");

                    if ("1".equals(forceTag))
                    {
                        disElement.add(tempDistData);
                    }
                }
            }

            int number = disElement.size() + servElement.size();

            /*
             * if((!"-1".equals(maxNumber) && number > maxNumber) || (!"-1".equals(minNumber) && number < minNumber)) {
             * CSAppException.apperr(ElementException.CRM_ELEMENT_25, packageId, minNumber, maxNumber); }
             */

            result.addAll(servElement);
            result.addAll(disElement);
        }

        String useTag = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PRODUCT_COMP", "PRODUCT_ID", "USE_TAG", productId);

        boolean isGrpDesigned = "1".equals(useTag);

        if (isGrpDesigned)// 集团定制
        {
            // IDataset grpCustomizeElement = new DatasetList();
            // grpCustomizeElement = getGroupCustomizeInfos(null, discntList, productId);
            // result.addAll(grpCustomizeElement);
        }

        return result;
    }

    private IDataset operelementsdata(IDataset elements, IDataset result, boolean flag) throws Exception
    {
        IDataset servParam = null;
        String packageId = "";
        IDataset theElements = new DatasetList();
        IData thePackage = new DataMap();

        for (int i = 0; i < elements.size(); i++)
        {
            IData element = elements.getData(i);

            // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
            String elementTypeName = "";
            IData elementType = setElementTypeName();

            if (flag)
            {
                if (!packageId.equals(element.getString("PACKAGE_ID")))
                {
                    // 一个新的包的开始

                    thePackage = new DataMap();
                    theElements = new DatasetList();
                    packageId = element.getString("PACKAGE_ID");

                    result.add(thePackage);

                    thePackage.put("PRODUCT_ID", element.getString("PRODUCT_ID"));
                    thePackage.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
                    thePackage.put("PRODUCT_MODE", element.getString("PRODUCT_MODE"));
                    thePackage.put("ELEMENTS", theElements);

                    // 包中的第一个元素
                    IData tmpElement = new DataMap();
                    tmpElement.put("PRODUCT_ID", element.getString("PRODUCT_ID"));

                    // 根据产品ID查询产品名称
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    tmpElement.put("PRODUCT_NAME", productName);

                    tmpElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));

                    // 根据package_id查询包信息
                    String packageName = PkgInfoQry.getPackageNameByPackageId(packageId);
                    tmpElement.put("PACKAGE_NAME", packageName);

                    tmpElement.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                    tmpElement.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                    tmpElement.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));

                    // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
                    elementTypeName = getElementTypeName(elementType, element.getString("ELEMENT_TYPE_CODE"));
                    tmpElement.put("ELEMENT_TYPE_NAME", elementTypeName);

                    tmpElement.put("START_DATE", element.getString("START_DATE"));
                    tmpElement.put("END_DATE", element.getString("END_DATE"));
                    tmpElement.put("PRODUCT_MODE", element.getString("PRODUCT_MODE"));

                    tmpElement.put("STATE", "ADD");

                    if ("S".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        // 为服务元素 需要加入SERV_PARAM节点
                        servParam = new DatasetList();
                        tmpElement.put("SERV_PARAM", servParam);
                    }
                    theElements.add(tmpElement);
                }
                else
                {
                    // 在一个包内
                    // 不同的元素
                    IData tmpElement = new DataMap();
                    servParam = new DatasetList();
                    tmpElement.put("PRODUCT_ID", productId);

                    // 根据产品ID查询产品名称
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    tmpElement.put("PRODUCT_NAME", productName);

                    tmpElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));

                    // 根据package_id查询包信息
                    String packageName = PkgInfoQry.getPackageNameByPackageId(packageId);
                    tmpElement.put("PACKAGE_NAME", packageName);

                    tmpElement.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                    tmpElement.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                    tmpElement.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));

                    // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
                    elementTypeName = getElementTypeName(elementType, element.getString("ELEMENT_TYPE_CODE"));
                    tmpElement.put("ELEMENT_TYPE_NAME", elementTypeName);

                    tmpElement.put("START_DATE", element.getString("START_DATE"));
                    tmpElement.put("END_DATE", element.getString("END_DATE"));
                    tmpElement.put("PRODUCT_MODE", element.getString("PRODUCT_MODE"));
                    tmpElement.put("STATE", element.getString("STATE", "ADD"));
                    if ("S".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        // 为服务元素 需要加入SERV_PARAM节点
                        tmpElement.put("SERV_PARAM", servParam);
                    }
                    theElements.add(tmpElement);
                }
            }
            else
            {
                continue;
            }
        }

        return result;
    }

    private IDataset operelementsdata(String productId, IDataset elements, IDataset result, IData data, boolean flag) throws Exception
    {

        IDataset servParam = null;
        String packageId = "";
        IDataset theElements = new DatasetList();
        IData thePackage = new DataMap();

        for (int i = 0; i < elements.size(); i++)
        {
            IData element = elements.getData(i);

            // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
            String elementTypeName = "";
            IData elementType = setElementTypeName();

            if (flag)
            {
                if (!packageId.equals(element.getString("PACKAGE_ID")))
                {
                    // 一个新的包的开始

                    thePackage = new DataMap();
                    theElements = new DatasetList();
                    packageId = element.getString("PACKAGE_ID");

                    result.add(thePackage);

                    thePackage.put("PRODUCT_ID", productId);
                    thePackage.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
                    thePackage.put("PRODUCT_MODE", data.getString("PRODUCT_MODE"));
                    thePackage.put("ELEMENTS", theElements);

                    // 包中的第一个元素
                    IData tmpElement = new DataMap();
                    tmpElement.put("PRODUCT_ID", productId);

                    // 根据产品ID查询产品名称
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    tmpElement.put("PRODUCT_NAME", productName);

                    tmpElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));

                    // 根据package_id查询包信息
                    String packageName = PkgInfoQry.getPackageNameByPackageId(packageId);
                    tmpElement.put("PACKAGE_NAME", packageName);

                    tmpElement.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                    tmpElement.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                    tmpElement.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));

                    // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
                    elementTypeName = getElementTypeName(elementType, element.getString("ELEMENT_TYPE_CODE"));
                    tmpElement.put("ELEMENT_TYPE_NAME", elementTypeName);

                    tmpElement.put("START_DATE", element.getString("START_DATE"));
                    tmpElement.put("END_DATE", element.getString("END_DATE"));
                    tmpElement.put("PRODUCT_MODE", data.getString("PRODUCT_MODE"));

                    tmpElement.put("STATE", "ADD");

                    if ("S".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        // 为服务元素 需要加入SERV_PARAM节点
                        servParam = new DatasetList();
                        tmpElement.put("SERV_PARAM", servParam);
                    }
                    theElements.add(tmpElement);
                }
                else
                {
                    // 在一个包内
                    // 不同的元素
                    IData tmpElement = new DataMap();
                    servParam = new DatasetList();
                    tmpElement.put("PRODUCT_ID", productId);

                    // 根据产品ID查询产品名称
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    tmpElement.put("PRODUCT_NAME", productName);

                    tmpElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));

                    // 根据package_id查询包信息
                    String packageName = PkgInfoQry.getPackageNameByPackageId(packageId);
                    tmpElement.put("PACKAGE_NAME", packageName);

                    tmpElement.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                    tmpElement.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                    tmpElement.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));

                    // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
                    elementTypeName = getElementTypeName(elementType, element.getString("ELEMENT_TYPE_CODE"));
                    tmpElement.put("ELEMENT_TYPE_NAME", elementTypeName);

                    tmpElement.put("START_DATE", element.getString("START_DATE"));
                    tmpElement.put("END_DATE", element.getString("END_DATE"));
                    tmpElement.put("PRODUCT_MODE", data.getString("PRODUCT_MODE"));
                    tmpElement.put("STATE", data.getString("STATE"));
                    if ("S".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        // 为服务元素 需要加入SERV_PARAM节点
                        tmpElement.put("SERV_PARAM", servParam);
                    }
                    theElements.add(tmpElement);
                }
            }
            else
            {
                continue;
            }
        }

        return result;
    }

    /**
     * 网厅处理集团产品总入口
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset processGrpWs(IData inData) throws Exception
    {
        String removeTag = IDataUtil.getMandaData(inData, "REMOVE_TAG");
        String modifyTag = IDataUtil.getMandaData(inData, "MODIFY_TAG");

        if (!"0".equals(removeTag))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_646, removeTag);
        }

        groupId = IDataUtil.getMandaData(inData, "GROUP_ID");
        productId = IDataUtil.getMandaData(inData, "PRODUCT_ID");

        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(grpCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_996, groupId);
        }

        custId = grpCustData.getString("CUST_ID");

        IDataset retDataset = new DatasetList();

        // 根据标志位分情况处理
        if (TRADE_MODIFY_TAG.MODI.Add.getValue().equals(modifyTag))
        {
            // 新增用户
            retDataset = creteGroupUserIntf(inData);
        }
        else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
        {
            // 删除用户
            retDataset = destroyGroupUserIntf(inData);
        }
        else if (TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag))
        {
            // 修改用户
            retDataset = changeGroupUserIntf(inData);
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_615, modifyTag);
        }
        return retDataset;
    }

    /**
     * 根据产品编码获取产品参数信息
     * 
     * @return
     * @throws Exception
     */
    public IData qryProductParamByProductId() throws Exception
    {
        IData param = new DataMap();
        param.put("ID", productId);
        param.put("ID_TYPE", "P");
        param.put("ATTR_OBJ", "0");
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        IDataset attrItemList = AttrItemInfoQry.getAttrItemAByIDTO(param, null);

        IData productParamData = IDataUtil.hTable2StdSTable(attrItemList, "ATTR_CODE", "ATTR_INIT_VALUE");

        return productParamData;
    }

    private IData setElementTypeName() throws Exception
    {
        // S:服务，D:资费，R:资源，Z-SP服务，A:预存赠送，G:实物，J:积分，C:信誉度';
        IData elementType = new DataMap();
        elementType.put("S", "服务");
        elementType.put("D", "优惠"); // "资费"
        elementType.put("R", "资源");
        elementType.put("ZSP", "服务");
        elementType.put("A", "预存赠送");
        elementType.put("G", "实物");
        elementType.put("J", "积分");
        elementType.put("C", "信誉度");

        return elementType;
    }
}
