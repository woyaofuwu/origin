
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.design.DesignDetailRuleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.design.DesignRuleInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class CheckGrpPackageLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckGrpPackageLimit.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入规则 CheckGrpPackageLimit() >>>>>>>>>>>>>>>>>>");

        String resultStr = "";

        String busiType = databus.getString("BUSI_TYPE", "");
        String grpProductId = databus.getString("PRODUCT_ID", "");

        String selectedElementStr = databus.getString("ALL_SELECTED_ELEMENTS", "[]"); // 集团元素
        String grpPackageStr = databus.getString("SELECTED_GRPPACKAGE_LIST", "[]"); // 集团成员定制元素
        String eparchyCode = databus.getString("EPARCHY_CODE");
        String grpUserId = databus.getString("USER_ID", "");

        IDataset selectedElementList = new DatasetList(selectedElementStr);
        IDataset grpPackageList = new DatasetList(grpPackageStr);

        // 添加元素信息
        IDataset elementList = new DatasetList();
        elementList.addAll((IDataset) Clone.deepClone(selectedElementList));
        elementList.addAll((IDataset) Clone.deepClone(grpPackageList));

        if (IDataUtil.isEmpty(elementList))
        {
            return false;
        }

        // 1.删除的服务时,判断是否有成员在使用.
        resultStr = runDelMebSvcLimit(grpUserId, eparchyCode, grpPackageList);
        if (StringUtils.isNotBlank(resultStr))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201408070001", resultStr);
            return true;
        }

        // 2.判断集团服务与成员服务之间的依赖关系
        resultStr = runGrpSvcAndMebSvcLimit(busiType, grpProductId, selectedElementList, grpPackageList);
        if (StringUtils.isNotBlank(resultStr))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201408070001", resultStr);
            return true;
        }

        // 3. 产品服务与资费的依赖
        resultStr = runSvcDisElementLimit(elementList);

        if (StringUtils.isNotBlank(resultStr))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201408070001", resultStr);
            return true;
        }

        // 4.服务暂停或恢复时，不允许改对应的资费
        resultStr = runSvcStopOrResumeLimit(elementList);

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 结束规则 CheckGrpPackageLimit() 返回结果>>>>>>>>>>>>>>>>>>");

        if (StringUtils.isNotBlank(resultStr))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201408070001", resultStr);
            return true;
        }

        return false;
    }

    /**
     * 判断被删除的服务是否有成员在使用
     * 
     * @param grpUserId
     * @param eparchyCode
     * @param grpPackageList
     * @return
     * @throws Exception
     */
    public static String runDelMebSvcLimit(String grpUserId, String eparchyCode, IDataset grpPackageList) throws Exception
    {
        String retStr = ""; // 返回错误信息

        IDataset delMebSvcElement = DataHelper.filter(grpPackageList, "MODIFY_TAG=1,ELEMENT_TYPE_CODE=S");// 只处理变更时删除的定制服务
        if (IDataUtil.isNotEmpty(delMebSvcElement))
        {
            for (int i = 0, row = grpPackageList.size(); i < row; i++)
            {
                IData grpPackageData = grpPackageList.getData(i);

                // 只对删除的定制服务进行判断
                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(grpPackageData.getString("ELEMENT_TYPE_CODE")) && TRADE_MODIFY_TAG.DEL.getValue().equals(grpPackageData.getString("MODIFY_TAG")))
                {
                    String serviceId = grpPackageData.getString("ELEMENT_ID", "");

                    boolean isExistWB = ParamInfoQry.isExistsBW(grpUserId, serviceId, eparchyCode);

                    if (isExistWB == true)
                    {
                        String mebServerName = getElementName("S", serviceId);
                        retStr = "该集团用户下存在使用【" + mebServerName + "】的成员，暂时不能取消!";
                    }

                }
            }
        }

        return retStr;
    }

    /**
     * 判断集团服务与成员服务之间的依赖关系
     * 
     * @param busiType
     * @param prodcutId
     * @param grpSvcElement
     * @param mebSvcElement
     * @return
     * @throws Exception
     */
    public static String runGrpSvcAndMebSvcLimit(String busiType, String productId, IDataset grpSvcElement, IDataset mebSvcElement) throws Exception
    {
        String retStr = ""; // 返回错误信息
        String grpServerId = "";
        String mebServerId = "";
        boolean platErr = false;
        IDataset cheSvcElement = null;

        IDataset isCheGrpSvcElement = DataHelper.filter(grpSvcElement, "ELEMENT_TYPE_CODE=S");
        IDataset isCheMebSvcElement = DataHelper.filter(mebSvcElement, "ELEMENT_TYPE_CODE=S");

        // 1.判断选择了某某集团服务,必须选择相应的成员服务
        if (IDataUtil.isNotEmpty(isCheGrpSvcElement))
        {
            for (int i = 0, rowSize = isCheGrpSvcElement.size(); i < rowSize; i++)
            {
                IData packageData = isCheGrpSvcElement.getData(i);
                grpServerId = packageData.getString("ELEMENT_ID", "");
                String modifyTag = packageData.getString("MODIFY_TAG", "");
                mebServerId = MemParams.getServIdByGrpORmebServId(grpServerId);// 根据集团的服务查询对应的成员服务
                if (StringUtils.isNotBlank(mebServerId) && "0".equals(modifyTag)) // 只对服务删除或新增进行校验
                {
                    cheSvcElement = DataHelper.filter(isCheMebSvcElement, "MODIFY_TAG=0,ELEMENT_ID=" + mebServerId);
                    if (IDataUtil.isEmpty(cheSvcElement))
                    {
                        String grpSvcName = USvcInfoQry.getSvcNameBySvcId(grpServerId);
                        String mebSvcName = USvcInfoQry.getSvcNameBySvcId(mebServerId);
                        retStr = "订购集团产品信息的 【" + grpSvcName + "】 服务  ，必需订购定制信息的【" + mebSvcName + "】 服务.";
                        platErr = true;
                        break;

                    }
                }
                if (StringUtils.isNotBlank(mebServerId) && "1".equals(modifyTag)) // 只对服务删除或新增进行校验
                {
                    cheSvcElement = DataHelper.filter(isCheMebSvcElement, "MODIFY_TAG=1,ELEMENT_ID=" + mebServerId);
                    if (IDataUtil.isEmpty(cheSvcElement))
                    {
                        String grpSvcName = USvcInfoQry.getSvcNameBySvcId(grpServerId);
                        String mebSvcName = USvcInfoQry.getSvcNameBySvcId(mebServerId);
                        retStr = "删除集团产品信息的 【" + grpSvcName + "】 服务  ，必需删除定制信息的【" + mebSvcName + "】 服务.";
                        platErr = true;
                        break;
                    }
                }
            }
        }

        // 2.判断选择了某某成员服务,必须选择相应的集团服务
        if (platErr == false)
        {
            for (int i = 0, rowSize = isCheMebSvcElement.size(); i < rowSize; i++)
            {
                IData packageData = isCheMebSvcElement.getData(i);
                mebServerId = packageData.getString("ELEMENT_ID", "");
                String modifyTag = packageData.getString("MODIFY_TAG", "");
                grpServerId = MemParams.getServIdByGrpORmebServId(mebServerId);// 根据集团的服务查询对应的成员服务
                if (StringUtils.isNotBlank(grpServerId) && "0".equals(modifyTag)) // 只对服务删除或新增进行校验
                {
                    cheSvcElement = DataHelper.filter(isCheGrpSvcElement, "MODIFY_TAG=0,ELEMENT_ID=" + grpServerId);
                    if (IDataUtil.isEmpty(cheSvcElement))
                    {
                        String grpSvcName = USvcInfoQry.getSvcNameBySvcId(grpServerId);
                        String mebSvcName = USvcInfoQry.getSvcNameBySvcId(mebServerId);
                        retStr = "订购定制信息的【" + mebSvcName + "】服务，必需订购集团产品信息的 【" + grpSvcName + "】 服务";
                        break;
                    }
                }
                if (StringUtils.isNotBlank(grpServerId) && "1".equals(modifyTag)) // 只对服务删除或新增进行校验
                {
                    cheSvcElement = DataHelper.filter(isCheGrpSvcElement, "MODIFY_TAG=1,ELEMENT_ID=" + grpServerId);
                    if (IDataUtil.isEmpty(cheSvcElement))
                    {
                        String grpSvcName = USvcInfoQry.getSvcNameBySvcId(grpServerId);
                        String mebSvcName = USvcInfoQry.getSvcNameBySvcId(mebServerId);
                        retStr = "删除定制信息的【" + mebSvcName + "】服务 ，必需删除集团产品信息的 【" + grpSvcName + "】 服务";
                        break;
                    }
                }
            }
        }
        return retStr;

    }

    /**
     * 判断元素之间的依赖关系 产品服务与资费的依赖
     * 
     * @param elementList
     * @param grpPkgList
     * @return
     * @throws Exception
     */
    public static String runSvcDisElementLimit(IDataset allElementList) throws Exception
    {
        String retStr = ""; // 返回错误信息
        StringBuilder builder = new StringBuilder(1000);
        int elementListSize = allElementList.size();

        // 遍历元素信息
        for (int i = 0; i < elementListSize; i++)
        {
            IData elementData = allElementList.getData(i);

            String productId = elementData.getString("PRODUCT_ID", "");
            String packageId = elementData.getString("PACKAGE_ID", "");
            String elementTypeCode = elementData.getString("ELEMENT_TYPE_CODE", "");
            String elementId = elementData.getString("ELEMENT_ID", "");
            String mofidyTag = elementData.getString("MODIFY_TAG", "");

            // 只有新增元素时判断服务与资费的依赖
            if (!BofConst.MODIFY_TAG_ADD.equals(mofidyTag) || !"exist".equals(mofidyTag))
            {
                continue;
            }
            // 查询规则信息
            IDataset ruleList = DesignRuleInfoQry.qryDesignRule(productId, packageId, elementTypeCode, elementId);

            if (IDataUtil.isEmpty(ruleList))
            {
                continue;
            }

            // 遍历规则信息
            for (int j = 0, jRow = ruleList.size(); j < jRow; j++)
            {
                IData rurleData = ruleList.getData(j);

                if ("STOP".equals(rurleData.getString("RULE_TYPE"))) // 暂停规则不处理
                {
                    continue;
                }

                int min = Integer.valueOf(rurleData.getString("MIN_NUMBER", "-1"));
                int max = Integer.valueOf(rurleData.getString("MAX_NUMBER", "-1"));
                int addSum = 0;
                int delSum = 0;

                min = (min == -1) ? 0 : min;
                max = (max == -1) ? 256 : max;

                // 查询规则明细信息
                String ruleId = rurleData.getString("RULE_ID");
                IDataset ruleDetailList = DesignDetailRuleInfoQry.qryDesignDetailRuleByRuleId(ruleId);

                if (IDataUtil.isEmpty(ruleDetailList))
                {
                    continue;
                }

                builder.append("选择");
                builder.append("【" + PkgInfoQry.getPackageNameByPackageId(packageId) + "】的");
                builder.append("【" + getElementName(elementTypeCode, elementId) + "】必须选择：</br>");

                // 遍历规则明细信息
                for (int k = 0, kRow = ruleDetailList.size(); k < kRow; k++)
                {

                    IData ruleDetailData = ruleDetailList.getData(k);
                    boolean isExist = false;

                    String ruleProductId = ruleDetailData.getString("PRODUCT_ID");
                    String rulePackageId = ruleDetailData.getString("PACKAGE_ID");
                    String ruleElementTypeCode = ruleDetailData.getString("ELEMENT_TYPE_CODE");
                    String ruleElementId = ruleDetailData.getString("ELEMENT_ID");
                    // 判断是否存在元素
                    for (int m = 0; m < elementListSize; m++)
                    {
                        IData existData = allElementList.getData(m);
                        String mofidyTagDis = existData.getString("MODIFY_TAG", "");
                        // 判断新增元素
                        if (BofConst.MODIFY_TAG_ADD.equals(mofidyTagDis))
                        {
                            // 计算选择的元素个数
                            if (ruleProductId.equals(existData.getString("PRODUCT_ID")) && rulePackageId.equals(existData.getString("PACKAGE_ID")) && ruleElementTypeCode.equals(existData.getString("ELEMENT_TYPE_CODE"))
                                    && ruleElementId.equals(existData.getString("ELEMENT_ID")))
                            {
                                addSum++;
                                isExist = true;
                                break;
                            }
                        }

                    }
                    if (isExist == false)
                    {
                        String disPackageIdName = PkgInfoQry.getPackageNameByPackageId(rulePackageId);
                        String disIdName = getElementName(ruleElementTypeCode, ruleElementId);

                        builder.append("【" + disPackageIdName + "】的");
                        builder.append("【" + ruleElementId + "||" + disIdName + "】<br/>");
                    }
                } // 遍历规则明细信息结束

                if (addSum < min || addSum > max)
                {
                    String minStr = (min == 0) ? "" : "最少" + min + "个元素";
                    String maxStr = (max == 256) ? "" : "最多" + max + "个元素";
                    builder.append("必须要关联选择" + minStr + ((StringUtils.isEmpty(minStr) && StringUtils.isEmpty(maxStr)) ? "、" : "") + maxStr + "，现在选择了" + addSum + "个。<br/><br/>");
                    return builder.toString();
                }
            } // 遍历规则信息结束
        } // 遍历元素信息结束

        return retStr;
    }

    /**
     * 集团服务暂停操作或处于暂停做恢复操作时, 不能操作对应的资费。
     * 
     * @param busiType
     * @param allElementList
     * @return
     * @throws Exception
     */
    public static String runSvcStopOrResumeLimit(IDataset allElementList) throws Exception
    {
        String retStr = ""; // 返回错误信息

        // 过滤服务信息
        IDataset svcList = DataHelper.filter(allElementList, "ELEMENT_TYPE_CODE=S");
        IDataset disList = DataHelper.filter(allElementList, "ELEMENT_TYPE_CODE=D");

        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0, iRow = svcList.size(); i < iRow; i++)
        {

            IData svcData = svcList.getData(i);

            String productId = svcData.getString("PRODUCT_ID");
            String packageId = svcData.getString("PACKAGE_ID");
            String elementTypeCode = svcData.getString("ELEMENT_TYPE_CODE");
            String elementId = svcData.getString("ELEMENT_ID");
            String modifyTagSvc = svcData.getString("MODIFY_TAG");
            // 处理正在变更的服务或正处于暂停的服务
            if (!"2".equals(modifyTagSvc) || !"exist".equals(modifyTagSvc))
            {
                continue;
            }
            // 服务参数信息
            IDataset attrParamList = svcData.getDataset("ATTR_PARAM");

            if (IDataUtil.isEmpty(attrParamList))
            {
                continue;
            }

            IData attrParamData = attrParamList.getData(1);

            IData serparam = attrParamData.getData("PLATSVC");
            String platsyncState = serparam.getString("pam_PLAT_SYNC_STATE");
            String operState = serparam.getString("pam_OPER_STATE", "");

            // 判断是否是做暂停或恢复操作(服务正处于暂停做恢复操作或服务正在做暂停操作)
            if (("P".equals(operState) && "05".equals(operState)) || "04".equals(operState))
            {
                // 查询规则信息
                IDataset ruleList = DesignRuleInfoQry.qryDesignRule(productId, packageId, elementTypeCode, elementId);

                if (IDataUtil.isEmpty(ruleList))
                {
                    continue;
                }

                // 遍历规则信息
                for (int j = 0, jRow = ruleList.size(); j < jRow; j++)
                {
                    IData ruleData = ruleList.getData(j);
                    if (!"STOP".equals(ruleData.getString("RULE_TYPE", ""))) // 处理暂停时不能变更的资费,
                        continue;

                    String ruleId = ruleData.getString("RULE_ID");
                    // 查询规则明细
                    IDataset detailRules = DesignDetailRuleInfoQry.qryDesignDetailRuleByRuleId(ruleId);

                    if (IDataUtil.isEmpty(detailRules))
                    {
                        continue;
                    }

                    String elementIdName = getElementName(elementTypeCode, elementId);

                    // StringBuilder tmpSb = new StringBuilder();
                    String tempStr = "";
                    if ("04".equals(operState))
                    {
                        tempStr = "\"" + elementIdName + "\"业务做 暂停　操作，不能修改该业务的<br/>";
                    }
                    if (operState.equals("05") || ("P".equals(platsyncState) && "04".equals(operState)))
                    {
                        tempStr = "\"" + elementIdName + "\"业务处于 暂停　状态，不能修改该业务的<br/>";
                    }

                    int ifirst = 0;
                    for (int k = 0; k < detailRules.size(); k++)
                    {
                        boolean isExist = false;

                        IData detailRule = detailRules.getData(k);
                        String ruleProductId = detailRule.getString("PRODUCT_ID");
                        String rulePackageId = detailRule.getString("PACKAGE_ID");
                        String ruleElementTypeCode = detailRule.getString("ELEMENT_TYPE_CODE");
                        String ruleElementId = detailRule.getString("ELEMENT_ID");

                        for (int m = 0; m < disList.size(); m++)
                        {
                            IData disData = disList.getData(m);
                            String modifyTag = disData.getString("MODIFY_TAG");
                            if ("0".equals(modifyTag) || "1".equals(modifyTag) || "2".equals(modifyTag)) // 不允许对服务对应的资费作增、删、改操作。
                            {
                                if (ruleProductId.equals(disData.getString("PRODUCT_ID")) && rulePackageId.equals(disData.getString("PACKAGE_ID")) && ruleElementTypeCode.equals(disData.getString("ELEMENT_TYPE_CODE"))
                                        && ruleElementId.equals(disData.getString("ELEMENT_ID")))
                                {
                                    isExist = true;
                                    break;
                                }
                            }

                        }

                        if (isExist == true)
                        {
                            if (ifirst == 0)
                            {
                                sBuilder.append(tempStr);
                            }
                            ifirst++;
                            sBuilder.append("\"资费：" + getElementName(ruleElementTypeCode, ruleElementId) + "\"<br/>");
                        }
                    }

                    if (StringUtils.isNotBlank(sBuilder))
                    {
                        if ("P".equals(platsyncState) && "04".equals(operState) || "05".equals(operState))
                        {
                            sBuilder.append("，请恢复后再修改！");
                        }
                        else
                        {
                            sBuilder.append("，请返回修改！");
                        }
                        return sBuilder.toString();
                    }
                }
            }// 判断是否是做暂停或恢复操作end
        }

        return retStr;
    }

    /**
     * 获取元素名称
     * 
     * @param elementTypeCode
     * @param elementId
     * @return
     * @throws Exception
     */
    public static String getElementName(String elementTypeCode, String elementId) throws Exception
    {
        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode))
        {
            return USvcInfoQry.getSvcNameBySvcId(elementId);
        }
        else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode))
        {
            return UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);
        }
        else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(elementTypeCode))
        {
            return elementId;
        }

        return elementId;

    }

}
