
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPtypeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.KMP;

public class CreateFixTelUserBean extends CSBizBean
{

    /**
     * 删除预存费用
     * 
     * @author chenzm
     * @param feeList
     * @return
     * @throws Exception
     */
    public static void delDepositFee(IDataset feeList) throws Exception
    {
        String feeMode;
        String feeTypeCode;
        int fee;
        for (int i = 0; i < feeList.size(); i++)
        {
            feeMode = feeList.getData(i).getString("FEE_MODE");
            feeTypeCode = feeList.getData(i).getString("FEE_TYPE_CODE");
            if ("2".equals(feeMode) && !existFeeType(feeTypeCode))
            {
                fee = Integer.parseInt(feeList.getData(i).getString("FEE"));
                if (fee != 0)
                {
                    feeList.remove(i);
                    i--;
                }
            }
        }
    }

    public static IDataset distinct(List<IDataset> sets)
    {
        IDataset datas = new DatasetList();
        Map<String, IData> map = new HashMap<String, IData>();
        for (IDataset set : sets)
        {
            for (int i = 0; i < set.size(); i++)
            {
                IData data = set.getData(i);
                map.put(data.getString("PRODUCT_TYPE_CODE"), data);
            }
        }
        for (String key : map.keySet())
        {
            datas.add(map.get(key));
        }
        return datas;
    }

    /**
     * 过滤不删除得预存费用
     * 
     * @author chenzm
     * @param feeList
     * @return
     * @throws Exception
     */
    private static boolean existFeeType(String feeTypeCode) throws Exception
    {
        IDataset resultList = CommparaInfoQry.getCommparaCode1("CSM", "1811", feeTypeCode, CSBizBean.getTradeEparchyCode());
        return IDataUtil.isNotEmpty(resultList);
    }

    /**
     * 根据证件类型和证件号码获取客户资料
     * 
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getCustInfoByPspt(IData data) throws Exception
    {
        IData custPersonList = null;
        IData custData = null;
        IDataset custList = new DatasetList();// 先给初始值，便于后面判断
        custList = CustomerInfoQry.getCustInfoByPsptCustType("0", data.getString("PSPT_TYPE_CODE"), data.getString("PSPT_ID"));
        return custList;
    }

    /**
     * 根据号段和号码类型获取默认产品
     * 
     * @author chenzm
     * @param serialNumber
     * @param codeTypeCode
     * @return
     * @throws Exception
     */
    public static IData getDefaultProduct(String serialNumber, String codeTypeCode) throws Exception
    {

        IDataset defalutProductInfos = ProductInfoQry.getDefaultProductByPhone(serialNumber, codeTypeCode, CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(defalutProductInfos))
        {
            defalutProductInfos = ProductInfoQry.getDefaultProductByResType(serialNumber, codeTypeCode, CSBizBean.getUserEparchyCode());
        }

        return IDataUtil.isNotEmpty(defalutProductInfos) ? defalutProductInfos.getData(0) : new DataMap();
    }

    /**
     * 处理sql绑定多个值，in方式
     * 
     * @param strOldBindValue
     * @return
     * @throws Exception
     */
    public static String getMulteBindValue(String strOldBindValue) throws Exception
    {

        if (strOldBindValue.startsWith(","))
            strOldBindValue = strOldBindValue.substring(1);// 去掉首字符为","号
        if (strOldBindValue.endsWith(","))
            strOldBindValue = strOldBindValue.substring(0, strOldBindValue.length() - 1);// 去掉尾字符为","号
        // 不存在多个值
        if (strOldBindValue.indexOf(",") == -1)
        {
            return "";
        }
        return strOldBindValue;// 转换后参数
    }

    /**
     * 获取发票类型列表
     * 
     * @author chenzm
     * @param strEparchyCode
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getNoteItemList(String strEparchyCode) throws Exception
    {

        IData inParams = new DataMap();
        inParams.put("PARA_CODE1", strEparchyCode);
        IDataset resultList = null;

        return resultList != null && resultList.size() > 0 ? resultList : new DatasetList();
    }

    /**
     * 根据用户标识获取欠费信息
     * 
     * @@author chenzm
     * @param custList
     * @param allUser
     * @return IData
     * @throws Exception
     */
    public static IData getOweFeeAllUserById(IDataset custList, boolean allUser) throws Exception
    {

        IData oweFeeData = new DataMap();
        IData custData = null;
        IData userData = null;
        IDataset userList = null;
        IData owefeeData = null;
        double dFee = 0;// 往月欠费
        int iOnlineNum = 0;// 当前证件下在网用户数
        boolean isExistsOweFeeFlag = false;// 存在欠费用户标记
        String oweFeeSerialNumber = "";// 欠费号码
        for (int i = 0; i < custList.size(); i++)
        {
            custData = custList.getData(i);
            String cust_id = custData.getString("CUST_ID");
            if (allUser)
            {

                userList = UserInfoQry.getAllUserInfoByCustId(cust_id);
            }
            else
            {
                userList = UserInfoQry.getAllNormalUserInfoByCustId(cust_id);
            }

            if (IDataUtil.isNotEmpty(userList))
            {
                iOnlineNum += userList.size();// 统计在网用户数
                // 未找到欠费用户时，才查欠费信息，找到一条则不查询，提示第一条欠费信息即可
                if (!isExistsOweFeeFlag)
                {
                    // 根据用户标识查询欠费信息
                    for (int j = 0; j < userList.size(); j++)
                    {
                        userData = userList.getData(j);
                        // 老系统就判的写死的时间，不太合理
                        if (CSBizBean.getUserEparchyCode().equals("0736") && userData.getString("OPEN_DATE").compareTo(SysDateMgr.addYears(SysDateMgr.getSysDate(), -5)) < 0)
                            continue;
                        String userID = userData.getString("USER_ID");
                        owefeeData = AcctCall.getOweFeeByUserId(userID);
                        dFee = owefeeData.getDouble("LAST_OWE_FEE");
                        if (dFee > 0)
                        {// 找到有往月欠费用户则退出循环，提示欠费信息
                            isExistsOweFeeFlag = true;
                            oweFeeSerialNumber = userData.getString("SERIAL_NUMBER");
                            break;
                        }
                    }
                }
            }
        }
        // 存在欠费用户时，返回欠费号码，欠费金额，在网用户数
        if (isExistsOweFeeFlag)
        {

            String strFee = String.valueOf(((float) dFee) / 100);
            oweFeeData.put("OWE_FEE_SERIAL_NUMBER", oweFeeSerialNumber);
            oweFeeData.put("OWE_FEE", strFee);
            oweFeeData.put("ONLINE_NUM", iOnlineNum);
            oweFeeData.put("IS_EXISTS_OWE_FEE_FLAG", true);
        }
        return oweFeeData;
    }

    /**
     * 根据默认标记获取产品类型
     * 
     * @param strDefaultTag
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypeCodeByDefaultTagParentTypeCode(String strDefaultTag, String parentTypeCode) throws Exception
    {

        IDataset typeset = null;
        if (strDefaultTag.indexOf(",") == -1)
        {// 单参数
            return ProductTypeInfoQry.getProductTypeByDefaultTagParentTypeCode(strDefaultTag, parentTypeCode);
        }
        else
        {
            // TODO defaulttag多值下查找应该一个sql查出,而不是查多次,等待平台提供API
            strDefaultTag = getMulteBindValue(strDefaultTag);
            String[] strBindValueArry = StringUtils.split(strDefaultTag, ',');
            List<IDataset> sets = new ArrayList<IDataset>();
            for (int i = 0; i < strBindValueArry.length; i++)
            {
                typeset = ProductTypeInfoQry.getProductTypeByDefaultTagParentTypeCode(strBindValueArry[i], parentTypeCode);
                sets.add(typeset);
            }
            return distinct(sets);
        }
    }

    /**
     * 根据产品标识获取产品类型
     * 
     * @param strProductId
     * @param strEparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getProductTypeCodeByProductId(String strProductId, String strEparchyCode) throws Exception
    {

        IData inData = new DataMap();
        inData.put("PRODUCT_ID", strProductId);// 个人产品标识
        inData.put("EPARCHY_CODE", strEparchyCode);// 用户路由
        if (strProductId.indexOf(",") == -1)
        {// 单参数
            return ProductTypeInfoQry.getProductTypeByProductID(strProductId, strEparchyCode);
        }
        else
        {
            // 现没有绑定多个产品情况
            String strOldBindValue = getMulteBindValue(strProductId);
            return ProductTypeInfoQry.getProductTypeByProductID(strOldBindValue, strEparchyCode);
        }
    }

    /**
     * 检查证件是否为黑名单
     * 
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public static IData IsBlackUser(IData data) throws Exception
    {

        boolean isBlackFlag = false;
        IData returnData = new DataMap();
        String pspt_type_code = data.getString("PSPT_TYPE_CODE");
        String pspt_id = data.getString("PSPT_ID");
        IDataset resultList = UCustBlackInfoQry.qryBlackCustInfo(pspt_type_code, pspt_id);
        if (resultList != null && resultList.size() > 0)
        {// 是黑名单时再根据黑名单级别处理
            IData blackData = resultList.getData(0);
            if ("6".equals(blackData.getString("BLACK_USER_CLASS_CODE")))
            {// 实名保护资料，不做黑名单处理
                isBlackFlag = false;
            }
            else
            {
                isBlackFlag = true;
            }
            // 黑名单时提示继续或中断
            if (isBlackFlag)
            {
                returnData.put("IS_BLACK_USER", isBlackFlag);// 黑名单标记
                returnData.put("MOB_PHONECODE", blackData.getString("MOB_PHONECODE"));// 黑名单号码
                returnData.put("JOIN_CAUSE", blackData.getString("JOIN_CAUSE"));// 黑名单原因
            }
        }
        return returnData;
    }

    /**
     * 校验证件下停机，黑名单，欠费销户用户等
     * 
     * @author chenzm
     * @param data
     * @throws Exception
     */
    public IData checkPsptId(IData data) throws Exception
    {
        IData ajaxReturnData = new DataMap();
        // 黑名单
        IData blackData = IsBlackUser(data);
        if (!blackData.isEmpty())
        {
            if (blackData.getBoolean("IS_BLACK_USER", false))
            {
                String strBlackHintMsg = "该证件为黑名单资料！黑名单号码：" + blackData.getString("MOB_PHONECODE") + "。加入黑名单原因：" + blackData.getString("JOIN_CAUSE") + "。";
                if (!"1".equals(data.getString("CHR_BLACKCHECKMODE")))
                {

                    // 提示是否继续
                    ajaxReturnData.put("IS_BLACK_USER", true);
                    ajaxReturnData.put("BLACK_USER_MSG", strBlackHintMsg);
                }
                else
                {
                    CSAppException.apperr(BizException.CRM_BIZ_5, strBlackHintMsg);
                }
            }
        }

        // 提示是否选择同客户,正确的业务逻辑应该是提示黑名单后中断，点击继续办理时才调用下面的方法，考虑将下面的方法抽成方法，实现java中断。后续再完善。
        // 获取客户资料,并校验证件号码下有无欠费用户
        IDataset custList = getCustInfoByPspt(data);

        // 根据客户标记获取用户是否有欠费判断
        if (IDataUtil.isNotEmpty(custList))
        {
            // 根据客户标记获取用户欠费信息
            if (data.getBoolean("CHR_CHECKOWEFEEBYPSPT", false))
            {// 根据证件号码判断欠费(巧妙的实现方式是根据用户标记)
                // HXYD-YZ-REQ-20100420-008常德分公司要求一证多号判断含消号用户欠费需求 销户的号码也要判往月欠费
                IData oweFeeData = getOweFeeAllUserById(custList, data.getBoolean("CHR_CHECKOWEFEEBYPSPT_ALLUSER", false));
                if (IDataUtil.isEmpty(oweFeeData))
                {
                    if (oweFeeData.getBoolean("IS_EXISTS_OWE_FEE_FLAG", false))
                    {
                        String strOnlineHintMsg = "当前用户证件下共有在网用户【" + oweFeeData.getString("ONLINE_NUM") + "】个！";
                        if (data.getBoolean("CHR_CHECKOWEFEEBYPSPT_ALLUSER", false))
                        {
                            strOnlineHintMsg = "当前用户证件下共有历史入网记录【" + oweFeeData.getString("ONLINE_NUM") + "】个！";
                        }

                        if (data.getBoolean("OPEN_LIMIT_TAG", false))
                        {// 存在开户限制时，中断业务操作
                            String strOweFeeExitHintMsg = strOnlineHintMsg + "\n其中号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元，不能再次使用该证件办理开户业务！";

                            if (CSBizBean.getUserEparchyCode().equals("0736"))
                                strOweFeeExitHintMsg = strOnlineHintMsg + "\n其中2008年1月1日后入网的号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元，不能再次使用该证件办理开户业务！";

                            CSAppException.apperr(BizException.CRM_BIZ_5, strOweFeeExitHintMsg);
                        }
                        else
                        {
                            // 前台判断存在欠费用户时，提示是否继续，点击否中断，点击是则调用后续客户开户数限制:JudgeOpenLimit
                            String strOweFeeConfirmHintMsg = strOnlineHintMsg + "\n其中号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元。";
                            if (CSBizBean.getUserEparchyCode().equals("0736"))
                                strOweFeeConfirmHintMsg = strOnlineHintMsg + "\n其中2008年1月1日后入网的号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元。";

                            ajaxReturnData.put("IS_EXISTS_OWE_FEE_FLAG", true);
                            ajaxReturnData.put("OWE_CONFIRM_HINT_MSG", strOweFeeConfirmHintMsg);// 欠费信息提示
                        }
                    }
                }
            }
        }
        return ajaxReturnData;
    }

    /**
     * 输入新开户号码后的校验，获取开户信息
     * 
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkSerialNumber(IData data) throws Exception
    {
        IData returnData = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER");
        String agentDepartId = data.getString("AGENT_DEPART_ID", "");

        returnData.put("CHECK_DEPART_ID", getVisit().getDepartId());// 根据操作员部门校验
        // 分库标识
        int iResult = getDiffDataBase();
        // 获取号码归属
        IData mofficeInfo = getMphoneAddress(data, iResult);
        returnData.put("B_SAME_EPARCHY_CODE", mofficeInfo.getString("B_SAME_EPARCHY_CODE"));
        returnData.put("B_DIFF_TRADE", mofficeInfo.getString("B_DIFF_TRADE"));

        // 新增,产品组件需要传入
        returnData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        if (!BizRoute.getRouteId().equals(CSBizBean.getTradeEparchyCode()))
            CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.EPARCHY_IS_MUST_BE_DIF_IN_PROV_OPEN);

        // 查看是否可开户
        IData canOpenInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        boolean bOpenAgain = false;
        if (IDataUtil.isNotEmpty(canOpenInfo))
            CSAppException.apperr(CrmUserException.CRM_USER_1059);

        returnData.put("B_REOPEN_TAG", "2"); // 默认正常开户

        // checkResourceForMphone校验需要用到的数据
        data.put("CHECK_DEPART_ID", returnData.getString("CHECK_DEPART_ID"));
        data.put("B_REOPEN_TAG", returnData.getString("B_REOPEN_TAG"));
        /* 测试需要,先注释 */
        /*
         * IDataset checkMphoneDatas = new DatasetList(); if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE",
         * ""))) { checkMphoneDatas = ResCall.checkResourceForIOTMphone("0", "0", serialNumber, "0",
         * returnData.getString("CHECK_DEPART_ID")); } else { checkMphoneDatas = ResCall.checkResourceForMphone("0",
         * "0", serialNumber, "0", returnData.getString("CHECK_DEPART_ID")); } IData checkMphoneData =
         * checkMphoneDatas.getData(0);
         */
        IData checkMphoneData = new DataMap();
        returnData.put("CHECK_RESULT_CODE", "0"); // 服务号码校验成功！

        /** ******************选号、密码卡、交预存送预存、号码绑定产品，优惠、预配预开等处理开始**************************************** */
        // 获取号费
        String strModeCode = checkMphoneData.getString("RSRV_DATE1", "");// 费号类型
        String strItemCodeMp = checkMphoneData.getString("RSRV_DATE2", "");// 费号子类型
        String strCodeFee = checkMphoneData.getString("RSRV_DATE3", "");// 费号金额
        String strGetCodeMode = checkMphoneData.getString("OCCUPY_GET_CODE", ""); // 获取号码获取方式 OCCUPY_GET_CODE 0－普通号码
        // 1－网上选号 2－大屏幕选号

        String strNetUsrpid = checkMphoneData.getString("PSPT_ID", ""); // 密码，用于网上选号时与客户证件比对值
        if ("1".equals(strGetCodeMode))
        {
            returnData.put("CHOICE_GetCodeMode", "2");
            returnData.put("CHOICE_NetUsrpid", strNetUsrpid);
        }

        String tieTBusyTag = checkMphoneData.getString("RSRV_STR3", ""); // 铁通无线座机
        String strSimCardNo = checkMphoneData.getString("SIM_CARD_NO", ""); // SIM卡
        String strPreOpenTag = checkMphoneData.getString("PREOPEN_TAG", "0"); // 预开
        String strPreCodeTag = checkMphoneData.getString("PRECODE_TAG", "0"); // 预配

        returnData.put("TIETBUSY_TAG", tieTBusyTag);
        /*************************************** 选号费处理 开始(188号码特殊处理) *******************************************************/

        // 如果有选号费
        if (!"".equals(strModeCode) && !"".equals(strItemCodeMp) && !"".equals(strCodeFee))
        {
            if (Integer.parseInt(strCodeFee) > 0)
            {
                returnData.put("FEE_MODE", strModeCode);
                returnData.put("FEE_TYPE_CODE", strItemCodeMp);
                returnData.put("FEE", strCodeFee);
            }
        }
        /*************************************** 选号费处理 结束 *******************************************************/

        /*************************************** 产品集合处理 开始 *******************************************************/

        String strDefaultTagProductType = getProductTypeByFilter(0);
        returnData.put("EXISTS_DEFAULT_TAG", strDefaultTagProductType);
        /*************************************** 产品集合处理 结束 *******************************************************/

        /*************************************** Sim卡处理 开始 *******************************************************/
        if (!"".equals(strSimCardNo) && ("1".equals(strPreOpenTag) || "1".equals(strPreCodeTag)))
        {
            returnData.put("PRE_CODE_TAG", "1"); // 设置标记位，如果是预配则不显示读卡和写卡按钮
            returnData.put("SIM_CARD_NO", strSimCardNo);

            IDataset checkSimDatas = new DatasetList();
            if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", "")))
            {
                checkSimDatas = ResCall.checkResourceForIOTSim("0", "0", serialNumber, strSimCardNo, "1", returnData.getString("CHECK_DEPART_ID"), "", "0");
            }
            else
            {
                checkSimDatas = ResCall.checkResourceForSim("0", serialNumber, strSimCardNo, null);
            }

            IData checkSimData = checkSimDatas.getData(0);

            // 取出卡资源部分数据用于登记台帐
            returnData.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE", ""));// 卡类型名称
            returnData.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));// 卡类型编码
            returnData.put("IMSI", checkSimData.getString("IMSI", ""));
            returnData.put("KI", checkSimData.getString("KI", ""));
            // 是否为USIM卡,3G,将OPC记录在attr表:3G卡标准：只要opc不为空，黄松确认
            String uSimOpc = checkSimData.getString("OPC", "");
            // OPC不为空时，生成资源台帐表RSRV_STR3
            if (!StringUtils.isBlank(uSimOpc))
            {
                returnData.put("OPC_VALUE", uSimOpc);
            }
            // 获取设备价格
            String strResTypeCode = checkSimData.getString("RES_TYPE_CODE", "10");// 卡类型
            String strProductId = "-1";
            String strClassId = "Z";

            // 获取卡号费用 湖南获取卡费时strCapacityTypeCode=0
            IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), "1", strProductId, "10", "0", strResTypeCode);
            if (IDataUtil.isNotEmpty(feeData))
            {
                returnData.put("FEE_MODE", "0");
                returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
                returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
            }
            returnData.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！

        }
        /*************************************** Sim卡处理 结束 *******************************************************/

        /** *********************选号、密码卡、交预存送预存、号码绑定产品，优惠、预配预开等处理 结束************************************** */

        IDataset acctInfoDays = DiversifyAcctUtil.queryAcctInfoDay();
        returnData.put("ACCT_INFO_DAYS", acctInfoDays);
        IDataset noteTypes = StaticInfoQry.getStaticListByTypeIdEparchy(BizRoute.getRouteId(), "NOTE_TYPE_", null);
        returnData.put("NOTE_TYPE_LIST", noteTypes);
        IDataset cityCodes = StaticUtil.getList(CSBizBean.getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", "PARENT_AREA_CODE", BizRoute.getRouteId());
        returnData.put("CITY_CODE_LIST", cityCodes);
        returnData.put("OPEN_TYPE", data.getString("OPEN_TYPE", ""));
        return returnData;
    }

    /**
     * 检查密码的设置是否符合要求
     * 
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkSimplePassword(IData data) throws Exception
    {

        String routeEparchyCode = BizRoute.getRouteId();
        String configEparchyCode = TagInfoQry.getSysTagInfo("CSM_PASSWORD_LIMIT_SWITCH", "TAG_INFO", "NO_DATA", routeEparchyCode);
        IData rtnData = new DataMap();
        rtnData.put("CHECK_PASS", "TRUE");
        rtnData.put("CHECK_MESSAGE", "密码设置符合要求，验证通过");

        if (routeEparchyCode.equals(configEparchyCode))
        {

            String serialNumber = data.getString("SERIAL_NUMBER");
            String psptType = data.getString("PSPT_TYPE_CODE");
            String psptId = data.getString("PSPT_ID");
            String birthday = data.getString("BIRTHDAY");
            String newPassword = data.getString("USER_PASSWD");

            String simplePwd1 = serialNumber.substring(0, 3) + serialNumber.substring(serialNumber.length() - 3, serialNumber.length());
            if (newPassword != null && !"".equals(newPassword))
            {

                // 密码不能为手机号码前三位和后三位
                if (simplePwd1.equals(newPassword))
                {
                    rtnData.put("CHECK_PASS", "FALSE");
                    rtnData.put("CHECK_MESSAGE", "密码不能为手机号码前三位和后三位！");
                    return rtnData;
                }

                // AAABBB类号码（A和B不要求连续）
                IData param = new DataMap();
                for (int i = 0; i < newPassword.length(); i++)
                {
                    char charI = newPassword.charAt(i);

                    if (param.isEmpty())
                    {
                        param.put(String.valueOf(charI), charI);

                    }
                    else
                    {
                        if (!param.containsKey(charI))
                        {
                            param.put(String.valueOf(charI), charI);
                        }
                    }
                }
                if (param.size() <= 2)
                {
                    rtnData.put("CHECK_PASS", "FALSE");
                    rtnData.put("CHECK_MESSAGE", "密码不能为AAABBB类号码（A和B不要求连续）");
                    return rtnData;
                }

                // 不允许用户使用自己的生日作为密码。
                String[] birthdayArray = birthday.split("-");
                String birthdayMatch = "";
                for (int i = 0; i < birthdayArray.length; i++)
                {
                    if (birthdayArray[i].length() == 1)
                    {
                        birthdayMatch = birthdayMatch + "0" + birthdayArray[i];
                    }
                    else
                    {
                        birthdayMatch = birthdayMatch + birthdayArray[i];
                    }
                }
                birthdayMatch = birthdayMatch.substring(2);
                if (birthdayMatch.equals(newPassword))
                {
                    rtnData.put("CHECK_PASS", "FALSE");
                    rtnData.put("CHECK_MESSAGE", "不允许使用自己的生日作为密码");
                    return rtnData;
                }

                // 如用户使用身份证上户，不允许用户采用身份证中连续的6位数字。
                if ("0".equals(psptType) || "1".equals(psptType) || "2".equals(psptType))
                {
                    if (KMP.kmpMatch(psptId, newPassword))
                    {
                        rtnData.put("CHECK_PASS", "FALSE");
                        rtnData.put("CHECK_MESSAGE", "密码不允许采用身份证中连续的6位数字。");
                        return rtnData;
                    }
                }

            }
            else
            {
                rtnData.put("CHECK_PASS", "FALSE");
                rtnData.put("CHECK_MESSAGE", "密码不能为空！");
                return rtnData;
            }
        }
        return rtnData;
    }

    /**
     * 资源sim卡校验
     * 
     * @author chenzm
     * @param data
     * @throws Exception
     */
    public IData checkSimResource(IData data) throws Exception
    {

        IData returnData = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String simCardNo = data.getString("SIM_CARD_NO", "");
        IDataset checkSimDatas = new DatasetList();
        /* 测试需要,先注释 */
        // SIM卡校验
        /*
         * if (PersonConst.IOT_OPEN.equals(data.getString("OPEN_TYPE", ""))) { checkSimDatas =
         * ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", data.getString("CHECK_DEPART_ID"), "",
         * "0"); } else { checkSimDatas = ResCall.checkResourceForSim("0", "0", serialNumber, simCardNo, "1",
         * data.getString("CHECK_DEPART_ID"), "", "0"); } IData checkSimData = checkSimDatas.getData(0);
         */
        IData checkSimData = new DataMap();
        checkSimData.put("IMSI", "11");
        checkSimData.put("KI", "11");
        returnData.put("CHECK_RESULT_CODE", "1");// SIM校验成功，且服务号码成功！
        IDataUtil.chkParam(checkSimData, "IMSI");
        IDataUtil.chkParam(checkSimData, "KI");
        // 取出卡资源部分数据用于登记台帐
        returnData.put("RES_KIND_CODE", checkSimData.getString("RES_KIND_CODE", ""));// 卡类型名称
        returnData.put("RES_KIND_NAME", checkSimData.getString("RES_KIND_NAME", ""));// 卡类型编码
        returnData.put("IMSI", checkSimData.getString("IMSI", ""));
        returnData.put("KI", checkSimData.getString("KI", ""));

        // 是否为USIM卡,3G,将OPC记录在attr表:3G卡标准：只要opc不为空，黄松确认
        String uSimOpc = checkSimData.getString("OPC", "");
        // OPC不为空时，生成资源台帐表RSRV_STR3
        if (!StringUtils.isBlank(uSimOpc))
        {
            returnData.put("OPC_VALUE", uSimOpc);
        }
        // 获取设备价格
        String strResTypeCode = checkSimData.getString("RES_TYPE_CODE", "10");// 卡类型
        String strProductId = "-1";
        String strClassId = "Z";

        // 获取卡号费用 湖南获取卡费时strCapacityTypeCode=0
        IData feeData = DevicePriceQry.getDevicePrice(BizRoute.getRouteId(), "1", strProductId, "10", "0", strResTypeCode);
        if (IDataUtil.isNotEmpty(feeData))
        {
            returnData.put("FEE_MODE", "0");
            returnData.put("FEE_TYPE_CODE", feeData.getString("FEEITEM_CODE"));
            returnData.put("FEE", feeData.getString("DEVICE_PRICE"));
        }

        return returnData;
    }

    /**
     * 处理通常及特殊情况下产品信息
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public IData createProductInfo(IData data) throws Exception
    {

        IData returnData = new DataMap();
        boolean specDealTag = false;// 不同处理标志
        String strBindSingleProduct = data.getString("EXISTS_SINGLE_PRODUCT", "");
        if (data.getString("B_REOPEN_TAG").equals("1"))// 如果是二次开户，则肯定绑定的是单个产品，获取绑定的单个产品id
        {
            IData pData = getMainProduct(data.getString("USER_ID"));
            if (!pData.isEmpty())
            {
                strBindSingleProduct = pData.getString("PRODUCT_ID");
            }
        }
        String strBindDefaultTag = data.getString("EXISTS_DEFAULT_TAG", "");

        // 判断标志位:只要有一个不为空，作特殊处理
        if (!StringUtils.isBlank(strBindSingleProduct) || !StringUtils.isBlank(strBindDefaultTag))
        {
            specDealTag = true;// 单产品
        }
        IDataset productTypeList = null;
        // 产品类型:不存在绑定产品:无特殊处理
        if (!specDealTag)
        {
            productTypeList = ProductTypeInfoQry.getProductsType("5000", null);// 无线固话开户产品为5000子产品
        }
        else
        {
            if (!StringUtils.isBlank(strBindSingleProduct))
            {
                productTypeList = getProductTypeCodeByProductId(strBindSingleProduct, BizRoute.getRouteId());

                // 号码绑定单个产品时，不显示产品目录，直接将此产品下的必选包下的必选择默认元素显示
                if (productTypeList.size() > 0)
                {
                    IData productInfo = UProductInfoQry.qryProductByPK(strBindSingleProduct);
                    returnData.put("PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
                    returnData.put("PRODUCT_ID", strBindSingleProduct);
                    returnData.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                }
            }
            else
            {
                // 绑定默认标记
                if (!StringUtils.isBlank(strBindDefaultTag))
                {
                    productTypeList = getProductTypeCodeByDefaultTagParentTypeCode(strBindDefaultTag, "3000");// 固话开户产品为5000子产品
                }
            }
        }

        returnData.put("PRODUCT_TYPE_LIST", productTypeList);
        return returnData;
    }

    public String getAlertNum() throws Exception
    {

        IData tmp = getTagInfo(CSBizBean.getUserEparchyCode(), "USER_OPEN_NUM_ALERT", "0");
        String alertnum = tmp.getString("TAG_NUMBER", "0");
        return alertnum;
    }

    /**
     * 获取是否分库标志
     * 
     * @author chenzm
     * @return int
     * @throws Exception
     */
    public int getDiffDataBase() throws Exception
    {
        int iResult = -1;

        IDataset dataset = CommparaInfoQry.getOnlyByAttr("CSM", "1013", CSBizBean.getUserEparchyCode());

        if (!IDataUtil.isEmpty(dataset))
        {
            IData resultData = dataset.getData(0);

            // 有异地业务，需在省中心记录台帐资料
            if ("1".equals(resultData.getString("PARAM_CODE")))
            {
                iResult = 0;
            }
            // 有异地业务，无需在省中心记录台帐资料
            else if ("2".equals(resultData.getString("PARAM_CODE")))
            {
                iResult = 1;
            }
            else
            {
                iResult = -1;
            }
        }

        return iResult;
    }

    /**
     * 获取用户绑定产品信息
     * 
     * @author chenzm
     * @param strUserId
     * @throws Exception
     */
    public IData getMainProduct(String strUserId) throws Exception
    {
        IData data = UcaInfoQry.qryMainProdInfoByUserId(strUserId);
        return data;
    }

    /**
     * 获取号码归属
     * 
     * @author chenzm
     * @param data
     * @param intDiffData
     * @throws Exception
     */
    public IData getMphoneAddress(IData data, int intDiffData) throws Exception
    {
        boolean bSameEparchyCode = true;
        boolean bDiffTrade = false;
        String strRouteEparchyCode = "";
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        // 根据号码获取路由的方法，需要公用出来，最好放专门的路由类，或放基类里，直接调用
        String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(data.getString("SERIAL_NUMBER"));
        if (StringUtils.isBlank(strEparchyCode))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_475, data.getString("SERIAL_NUMBER"));
        }

        // 异地业务
        if (!strEparchyCode.equals(tradeEparchyCode))
        {
            bSameEparchyCode = false;
            strRouteEparchyCode = strEparchyCode;

            if (intDiffData == 0)
            {
                bDiffTrade = true; // 省中心记台帐
            }
            else
            {
                bDiffTrade = false; // 省中心不记台帐
            }
        }
        else
        {
            strRouteEparchyCode = tradeEparchyCode;
            bDiffTrade = false; // 非异地
        }

        IData temp = new DataMap();
        temp.put("B_SAME_EPARCHY_CODE", bSameEparchyCode ? "0" : "1"); // 0:同地市 ;1:不同地市
        temp.put("B_DIFF_TRADE", bDiffTrade ? "0" : "1"); // 是否登记省中心台帐:0:登记;1:不登记

        return temp;
    }

    /**
     * 获取产品费用
     * 
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getProductFeeInfo(IData data) throws Exception
    {

        String product_id = data.getString("PRODUCT_ID");
        String brand_code = data.getString("BRAND_CODE");
        String eparchy_code = CSBizBean.getUserEparchyCode();
        IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("9701", product_id, "-1", "-1", brand_code, "3", eparchy_code);
        return dataset;

    }

    /**
     * 获取产品类型 iMode 0-普通开户(神州行除外) 1-神州行开户 2-所有
     * 
     * @author chenzm
     * @param iMode
     * @return
     * @throws Exception
     */
    public String getProductTypeByFilter(int iMode) throws Exception
    {
        String filterDefaultTag = "";

        if (iMode == 0 || iMode == 1)
        {
            if (iMode == 0)
            {
                filterDefaultTag = "1,2";// DEFAULT_TAG
            }
            else if (iMode == 1)
            {
                filterDefaultTag = "6";// DEFAULT_TAG
            }
        }

        return filterDefaultTag;
    }

    /**
     * 证件下欠费销户数检查
     * 
     * @author chenzm
     * @param data
     * @return int
     * @throws Exception
     */
    public int getQfXhCnt(IData data) throws Exception
    {

        int num = 0;
        String pspt_type_code = data.getString("PSPT_TYPE_CODE");
        String pspt_id = data.getString("PSPT_ID");
        String eparchy_code = CSBizBean.getUserEparchyCode();
        // 已欠费销户用户数
        IDataset restult = UserInfoQry.getQfxhUserInfoByPspt(pspt_type_code, pspt_id, eparchy_code);

        if (restult != null && restult.size() > 0)
        {
            num = restult.getData(0).getInt("QFXH_NUM", 0);
        }
        return num;
    }

    /**
     * 查询td_s_tag表参数
     * 
     * @param strEparchyCode
     * @param tagCode
     * @param userTag
     * @return
     * @throws Exception
     */
    public IData getTagInfo(String strEparchyCode, String tagCode, String userTag) throws Exception
    {
        IDataset tagList = new DatasetList();
        tagList = TagInfoQry.getTagInfo(strEparchyCode, tagCode, userTag, null);
        return IDataUtil.isEmpty(tagList) ? new DataMap() : tagList.getData(0);
    }

    /**
     * 界面初始化方法
     * 
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public IData InitPara(IData data) throws Exception
    {
        IData returnData = new DataMap();

        IData rDualInfo;
        String strTagInfo = "";
        String strTagChar = "";
        String strTagNumber = "";
        String strOpenType = data.getString("OPEN_TYPE", ""); // 是否代理商开户，通过地址栏参数
        String strEparchyCode = CSBizBean.getTradeEparchyCode();

        // 设置开户方式 OPEN_TYPE
        returnData.put("OPEN_TYPE", strOpenType);

        // 获取开户限制标记
        rDualInfo = getTagInfo(strEparchyCode, "CS_TAG_OPEN_LIMIT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("OPEN_LIMIT_TAG", "1".equals(strTagChar) ? true : false);

        // 获取默认开户用户数
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_OPENLIMITCOUNT", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "0");
        returnData.put("OPEN_LIMIT_COUNT", strTagNumber);

        // 获取客户名称限制标志
        rDualInfo = getTagInfo(strEparchyCode, "CS_CUSTNAME_LIMIT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("CUSTNAME_LIMIT", strTagChar);

        // 获取是否显示用户提示信息的标记(用户开户时，仅做新业务推荐用,各省都没有实现)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_SHOWHINTINFO", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("SHOW_HINT_INFO", strTagChar);

        // 获取是否显示引导信息
        rDualInfo = getTagInfo(strEparchyCode, "CS_SHOWUSERNUM", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("SHOW_USER_NUM_TAG", "1".equals(strTagChar) ? true : false);
        if ("1".equals(strTagChar))
        {
            returnData.put("SHOW_USER_NUM", rDualInfo.getString("TAG_NUMBER", "0"));
        }

        // 获取默认密码的使用方式
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPWDMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PWD_MODE", strTagChar);

        // 获取默认密码
        rDualInfo = getTagInfo(strEparchyCode, "CS_INF_DEFAULTPWD", "0");
        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("DEFAULT_PWD", strTagInfo);

        // 默认密码未变更是否继续(用户开户密码必输，代理商开户默认，还要这个标志何用？)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_PWDNOCHANGE", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        rDualInfo.put("PWDNOCHANGE", strTagNumber);

        // 获取密码长度
        rDualInfo = getTagInfo(strEparchyCode, "CS_NUM_PASSWORDLENGTH", "0");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        returnData.put("DEFAULT_PWD_LENGTH", strTagNumber);

        // 配置是否需要新版本的身份校验,0:不需要,1:需要
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_IDCHKDEALDISMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("ID_CHKDEALDIS_MODE", strTagChar);

        // 是否使用密码键盘 0-否 1-是
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_USEPASSWDKEYBOARD", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("USE_PASSWD_KEYBOARD", strTagChar);

        // 默认用户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTUSERTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_USER_TYPE", strTagChar);

        // 默认证件类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPSPTTYPE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PSPT_TYPE", strTagChar);

        // 默认帐户类型
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_DEFAULTPAYMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("DEFAULT_PAY_MODE", strTagChar);

        // 获取黑名单提示方式标记（0-不允许办理[默认]，1-提示是否继续）
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_BLACKCHECKMODE", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        returnData.put("CHR_BLACKCHECKMODE", strTagChar);

        // 获取是否根据证件号码判断欠费标记(默认为不按证件号码判欠费)
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CHECKOWEFEEBYPSPT", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "");
        strTagNumber = rDualInfo.getString("TAG_NUMBER", "");
        returnData.put("CHR_CHECKOWEFEEBYPSPT", "1".equals(strTagChar) ? true : false);
        returnData.put("CHR_CHECKOWEFEEBYPSPT_ALLUSER", "1".equals(strTagNumber) ? true : false);

        // 开户是否支持邮寄（0-支持[默认]，1-不支持）
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_OPENPOSTINFO", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "0");
        returnData.put("CHR_OPENPOSTINFO", strTagChar);

        // 是否已检查号码选占数据
        rDualInfo = getTagInfo(strEparchyCode, "CS_CHR_CHECKSELENUM", "0");
        strTagChar = rDualInfo.getString("TAG_CHAR", "0");
        returnData.put("CHR_CHECKSELENUM", strTagChar);

        // 以下为固话特有的初始化设置,上面部分暂时先不动
        // 获取本省代码
        rDualInfo = getTagInfo(strEparchyCode, "PUB_INF_PROVINCE", "0");
        strTagInfo = rDualInfo.getString("TAG_INFO", "");
        returnData.put("PROVINCE", strTagInfo);

        returnData.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

        return returnData;
    }

    public IDataset initProductType(IData input) throws Exception
    {
        IDataset productTypeList = UProductInfoQry.getProductsType("3000", null);// 固话产品类型
        return productTypeList;
    }

    /**
     * 查询某个工号的登陆时间限制
     * 
     * @author chenzm
     * @param data
     * @throws Exception
     */
    public void IsCanOperate(IData data) throws Exception
    {
        // 查询配置，如果不做限制，则直接返回
        IDataset dsCommpara = CommparaInfoQry.getCommparaCode1("CSM", "3270", "10", getVisit().getStaffEparchyCode());
        if (IDataUtil.isEmpty(dsCommpara) || !"1".equals(dsCommpara.getData(0).getString("PARA_CODE1")))
        {
            return;
        }
        String errorcontent = dsCommpara.getData(0).getString("PARA_CODE23");

        // 根据员工查询登录时间
        IData inData = new DataMap();
        inData.put("STAFF_ID", getVisit().getStaffId());
        IDataset resultList = StaffInfoQry.queryDateTimeByStaffId(getVisit().getStaffId());
        if (resultList != null && resultList.size() > 0)
        {
            String sysdate = SysDateMgr.getSysDate();
            String flag = "0";
            for (int i = 0; i < resultList.size(); i++)
            {
                if (!(sysdate.compareTo(resultList.getData(i).getString("ALLOW_STARTTIME")) < 0) && !(resultList.getData(i).getString("ALLOW_ENDTIME").compareTo(sysdate) < 0))
                {
                    flag = "1";
                    break;

                }
            }

            if ("0".equals(flag))
            {
                CSAppException.apperr(BizException.CRM_BIZ_5, errorcontent);
            }
        }
    }

    /**
     * 证件下最大用户开户数检查
     * 
     * @author chenzm
     * @param data
     * @return int
     * @throws Exception
     */
    public int JudgeOpenLimit(IData data) throws Exception
    {
        int openNum = 0;
        String pspt_type_code = data.getString("PSPT_TYPE_CODE");
        String pspt_id = data.getString("PSPT_ID");
        // 已开户用户数
        IDataset openList = UserInfoQry.getUserInfoByPsptEx(pspt_type_code, pspt_id, CSBizBean.getUserEparchyCode());

        if (openList != null && openList.size() > 0)
        {
            openNum = openList.getData(0).getInt("OPEN_NUM", 0);
        }

        // 某些证件类型不限制最多只允许办理5个移动号码 add by chenzm
        IData rDualInfo = getTagInfo(CSBizBean.getUserEparchyCode(), "CS_NUM_OPENLIMITCOUNT", "0");
        String tagInfo = rDualInfo.getString("TAG_INFO", "");
        if (tagInfo.indexOf(pspt_type_code) > -1)
        {
            return openNum;
        }
        // 是否证件开户限制
        int openLimitNum = data.getInt("OPEN_LIMIT_COUNT", 0);
        if (openLimitNum > 0)
        {
            if (openList != null && openList.size() > 0)
            {
                openNum = openList.getData(0).getInt("OPEN_NUM", 0);
            }
            // 比较
            if (openNum >= openLimitNum)
            {
                CSAppException.apperr(CustException.CRM_CUST_59, openLimitNum);
            }
        }
        return openNum;
    }

    /**
     * 开户营销费用重算
     * 
     * @author chenzm
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset mputeFee(IData data) throws Exception
    {
        IDataset feelist = new DatasetList(data.getString("FEE_LIST"));
        IDataset feeDatas = new DatasetList();
        IDataset resultFee = new DatasetList();
        // 开户费用
        IDataset openUserFee = new DatasetList();
        // 营销活动费用
        IDataset saleactiveFee = new DatasetList();
        for (int i = 0; i < feelist.size(); i++)
        {
            IData temp = feelist.getData(i);
            if ("10".equals(temp.getString("TRADE_TYPE_CODE", "")))
            {
                openUserFee.add(temp);
            }
            else
            {
                saleactiveFee.add(temp);
            }
        }
        IDataset comparamInfos = CommparaInfoQry.getCommparaInfoBy5("CSM", "2001", "SALEACTIVEFILTER4CREATEUSER", data.getString("SALE_PRODUCT_ID"), CSBizBean.getTradeEparchyCode(), null);
        if (IDataUtil.isEmpty(comparamInfos))
        {

        }
        else
        {
            String getFeeType = comparamInfos.getData(0).getString("PARA_CODE2");
            // 费用重叠
            if ("0".equals(getFeeType))
            {

            }
            // 只取营销活动费用
            else if ("1".equals(getFeeType))
            {
                // 计算后的费用

                resultFee.addAll(saleactiveFee);
                delDepositFee(openUserFee);
                resultFee.addAll(openUserFee);

            }// 预存就高
            else if ("2".equals(getFeeType))
            {
                int iOpenFee = 0;
                int iSaleFee = 0;
                int fee = 0;
                IData temp = new DataMap();
                for (int i = 0; i < openUserFee.size(); i++)
                {
                    temp = openUserFee.getData(i);
                    if ("2".equals(temp.getString("FEE_MODE")) && !existFeeType(temp.getString("FEE_TYPE_CODE")))
                    {
                        fee = Integer.parseInt(temp.getString("FEE"));
                        iOpenFee = iOpenFee + fee;
                    }
                }
                fee = 0;
                for (int i = 0; i < saleactiveFee.size(); i++)
                {
                    temp = saleactiveFee.getData(i);
                    if ("2".equals(temp.getString("FEE_MODE")))
                    {
                        fee = Integer.parseInt(temp.getString("FEE"));
                        iSaleFee = iSaleFee + fee;
                    }
                }
                // 预存就高，营销活动的钱还是保留，开户预存减去营销活动的钱
                if (iOpenFee > iSaleFee)
                {
                    for (int i = 0; i < openUserFee.size(); i++)
                    {
                        temp = openUserFee.getData(i);
                        if ("2".equals(temp.getString("FEE_MODE")))
                        {
                            fee = Integer.parseInt(temp.getString("FEE"));
                            if (fee > iSaleFee)
                            {
                                temp.put("OLDFEE", String.valueOf(fee - iSaleFee));
                                temp.put("FEE", String.valueOf(fee - iSaleFee));
                                break;
                            }
                        }
                    }
                }
                else
                {
                    delDepositFee(openUserFee);
                }

                resultFee.addAll(saleactiveFee);
                resultFee.addAll(openUserFee);
            }
        }
        for (int i = 0; i < resultFee.size(); i++)
        {
            IData feeConfig = resultFee.getData(i);
            IData feeData = new DataMap();
            feeData.put("TRADE_TYPE_CODE", feeConfig.getString("TRADE_TYPE_CODE"));
            feeData.put("MODE", feeConfig.getString("FEE_MODE"));
            feeData.put("CODE", feeConfig.getString("FEE_TYPE_CODE"));
            feeData.put("FEE", feeConfig.getString("OLDFEE"));
            if (StringUtils.isNotBlank(feeConfig.getString("DISCNT_GIFT_ID")))
            {
                feeData.put("ELEMENT_ID", feeConfig.getString("DISCNT_GIFT_ID"));
            }
            feeDatas.add(feeData);
        }
        return feeDatas;
    }

    /**
     * 释放选占的号码资源
     * 
     * @auth dengyong3
     * @param input
     * @throws Exception
     */
    public void releaseSelectedSNRes(IData input) throws Exception
    {
        IData param = new DataMap();
        String resTradeCode = "IRes_Fixed_MphoneRelease";
        String xGetMode = "0";
        String resNo = input.getString("RES_NO");
        String resTypeCode = "N";
        PBossCall.ResOccupyUseTT(resTradeCode, xGetMode, resNo, resTypeCode);
    }
}
