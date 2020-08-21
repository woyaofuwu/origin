
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes;

import java.sql.Timestamp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherservQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class OneCardNCodesChangeCardBean extends CSBizBean
{

    /**
     * 一卡双号换卡
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset changeCard(String sn) throws Exception
    {

        // 校验事件
        IData userInfo = UserInfoQry.getUserInfoBySN(sn);
        if (userInfo == null)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户信息出错，无用户信息!");
        }
        // 获取资源数据
        IData resInfo = getUserResource(userInfo);

        IDataset relationInfosB = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userInfo.getString("USER_ID"), "30", null);
        if (IDataUtil.isEmpty(relationInfosB))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到一卡双号信息");
        }
        if (!relationInfosB.getData(0).getString("ROLE_CODE_B").equals("1"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入的服务号码不是一卡双号主号码，业务无法继续！");
        }
        String userIdA = relationInfosB.getData(0).getString("USER_ID_A");
        if ("".equals(userIdA))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户不是一卡双号用户，业务无法继续！");
        }
        // IDataset relationInfosA = RelaUUInfoQry.getUserRelationAll( relationInfosB.getData(0).getString("USER_ID_A",
        // ""));
        IData relationInfosA = GetGroupInfo(relationInfosB.getData(0).getString("USER_ID_A"));
        String userSN = relationInfosA.getString("SERIAL_NUMBER_B");

        if (IDataUtil.isEmpty(relationInfosA) || !"2".equals(relationInfosA.getString("ROLE_CODE_B")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的副号码信息");
        }
        IData ouserInfo = UserInfoQry.getUserinfo(userSN).getData(0);

        // IData ouserInfo = UcaInfoQry.qryUserInfoBySn(userSN);
        if (IDataUtil.isEmpty(ouserInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户信息出错，无用户信息!");
        }
        // IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userInfo.getString("USER_ID"));
        IData ocustInfo = UcaInfoQry.qryCustInfoByCustId(ouserInfo.getString("CUST_ID"));
        IData oresInfo = getUserResource(ouserInfo);

        IDataset userPro = UserProductInfoQry.getProductInfo(ouserInfo.getString("USER_ID"), "-1");
        String brand_code = userPro.getData(0).getString("BRAND_CODE");
        String product_id = userPro.getData(0).getString("PRODUCT_ID");

        ouserInfo.put("PRODUCT_ID", product_id);
        ouserInfo.put("BRAND_CODE", brand_code);
        ouserInfo.put("PSPT_ADDR", "******");
        String openDate = ouserInfo.getString("OPEN_DATE","");
        String custName = ouserInfo.getString("CUST_NAME");

        ouserInfo.put("OPEN_DATE", openDate.substring(0, openDate.length()-2));
        ouserInfo.put("CUST_NAME", custName.substring(0, 1)+"**");

        if (IDataUtil.isEmpty(oresInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的用户SIM卡资源信息！");
        }

        IData data = new DataMap();
        data.put("OUSERRESINFO", oresInfo);
        data.put("OUSERINFO", ouserInfo);
        data.put("OCUSTINFO", ouserInfo);

        data.put("USERINFO", userInfo);
        data.put("USERRESINFO", resInfo);

        IDataset dataset = new DatasetList();
        dataset.add(data);
        // 付费账户
        // IData accountInfoA = bean.GetAcct(pd, ouserInfo.getString("USER_ID"));
        // IData accountInfoB=xbean.GetAcct(pd, ouserInfo.getString("USER_ID"));

        // int iresultCode=vipChangeCardFreeInfo();
        // if(iresultCode==1){
        // strProcessTagSet+="1";
        // }else{
        // strProcessTagSet+="0";
        // }

        return dataset;
    }

    public IDataset CheckNewSIM(String simcardNo,String snA,String snB,String oldSimcardNo) throws Exception
    {
        int pv_str_FreeChangeSimTag = 0;

        IDataset result = ResCall.getSimCardInfo("0", simcardNo, "", "");
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：查询不到SIM卡信息");
        }

        if (!"1U".equals(result.getData(0).getString("RES_TYPE_CODE")) && !"1I".equals(result.getData(0).getString("RES_TYPE_CODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入的SIM卡不是一卡双号主卡,请重新输入!" + result.getData(0).getString("RES_TYPE_CODE"));
        }
        if ("".equals(result.getData(0).getString("DOUBLE_TAG")) || result.getData(0).getString("DOUBLE_TAG") == null)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入的SIM卡不是一卡双号卡,请重新输入!");
        }
        // if ("1I".equals(result.getData(0).getString("RES_TYPE_CODE")) && !brandCode.equals("G001"))
        // {
        // CSAppException.apperr(CrmCommException.CRM_COMM_103, "SIM卡类型与用户品牌不符，业务无法继续！");
        // }

        IDataset OResult = ResCall.getSimCardInfo("0", result.getData(0).getString("DOUBLE_TAG"), "", "");
        if (IDataUtil.isEmpty(OResult))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "资料查询：查询不到副卡SIM卡信息");
        }

        IData data = new DataMap();
        IDataset dataset = new DatasetList();

        data.put("SIMCARDNO_A", result.getData(0).getString("SIM_CARD_NO", ""));
        data.put("IMSI_A", result.getData(0).getString("IMSI", ""));
        
        data.put("SIMCARDNO_B", OResult.getData(0).getString("SIM_CARD_NO", ""));
        data.put("IMSI_B", OResult.getData(0).getString("IMSI", ""));
        data.put("KI_A", result.getData(0).getString("KI", ""));
        data.put("KI_B", result.getData(0).getString("KI", ""));

        String resTypeCode = result.getData(0).getString("RES_TYPE_CODE");
        String resKindCode = result.getData(0).getString("RES_KIND_CODE");

        
        String fee = getDevicePrice(simcardNo, resTypeCode, resKindCode);
        data.put("FEE_DATA", fee);

        dataset.add(data);
        
        //资源选占
        selOccupySimCard(result.getData(0).getString("SIM_CARD_NO"),OResult.getData(0).getString("SIM_CARD_NO"),snA,snB);
        
        return dataset;
    }

    // 根据USERIDA获取集团关系 /**获取副卡用户信息**/
    public IData GetGroupInfo(String userIdA) throws Exception
    {
        IData param = new DataMap();
        IDataset userRelationInfos = RelaUUInfoQry.getUserRelationByUserIDA(userIdA, "30");
        if (userRelationInfos.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的一卡双号集团资料，业务无法继续！");
        }
        for (int i = 0; i < userRelationInfos.size(); i++)
        {
            String roleCode = userRelationInfos.getData(i).getString("ROLE_CODE_B");
            if (roleCode.equals("2"))
            {
                return userRelationInfos.getData(i);
            }
        }
        return null;
    }

    /**
     * 一卡双号选占
     * @param userInfo
     * @return
     * @throws Exception
     */
    public IDataset selOccupySimCard(String simcardnoA, String simcardnoB, String serialNumA, String serialNumB) throws Exception
    {

        IDataset resInfos = ResCall.ocncSelOccupy(simcardnoA, simcardnoB, serialNumA, serialNumB);
        if (IDataUtil.isEmpty(resInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "选占资源信息失败！");
        }
        return resInfos;
    }
    
    /**
     * 查询用户资源信息
     * 
     * @param userInfo
     * @throws Exception
     */
    public IData getUserResource(IData userInfo) throws Exception
    {

        IDataset resInfos = UserResInfoQry.getUserResInfoByUserId(userInfo.getString("USER_ID"));
        if (IDataUtil.isEmpty(resInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户资源资料无数据");
        }
        for (int i = 0; i < resInfos.size(); i++)
        {
            String resTypeCode = resInfos.getData(i).getString("RES_TYPE_CODE");
            if (resTypeCode.equals("1"))
            {
                return resInfos.getData(i);
            }
        }
        return null;
    }

    /**
     * @param input
     *            OLD_SIM_CARD_NO 用户老卡 ，NEW_SIM_CARD_NO 用户新卡， SERIAL_NUMEBR 手机号码，TRADE_TYPE_CODE默认是142
     * @return FEE_TAG 0:免费 1：收费 FEE： 费用 FEE_INFO：免费详细信息或部分收费信息 FEE_TARDE 0：登记OHTER台账 1：不登记OHTER台账
     * @throws Exception
     */
    public IData getSimCardPrice(String oldSimCardNo, String newSimCardNo, String serialNumber, String tradeTypeCode) throws Exception
    {
        IData returnInfo = new DataMap();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        IData oldSimInfo = ResCall.getSimCardInfo("0", oldSimCardNo, "", "1").getData(0);
        IData newSimInfo = ResCall.getSimCardInfo("0", newSimCardNo, "", "0").getData(0);
//        /*
//         * 2,3G卡，更换至4G卡，免费
//         */
//        IData startData = StaticInfoQry.getStaticInfoByTypeIdDataId("QUERY_STATICTIME_4G_ENDTIME", "01");
//        String startTime = startData.getString("DATA_NAME", "");
//        IData endData = StaticInfoQry.getStaticInfoByTypeIdDataId("QUERY_STATICTIME_4G_ENDTIME", "02");
//        String endTime = endData.getString("DATA_NAME", "");
//        String curTime = SysDateMgr.getSysTime();
//        Timestamp beginTimeTs = Timestamp.valueOf(startTime);
//        Timestamp endTimeTs = Timestamp.valueOf(endTime);
//        Timestamp curTimeTs = Timestamp.valueOf(curTime);
//        if (curTimeTs.before(endTimeTs) && curTimeTs.after(beginTimeTs))
//        {
//            boolean simModeFlag = true;// 默认23G卡
//            String oldSimTypeCode = oldSimInfo.getString("RES_TYPE_CODE").substring(1);
//            IDataset oldSet = ResParaInfoQry.checkUser4GUsimCard(oldSimTypeCode);
//            if (IDataUtil.isNotEmpty(oldSet))
//            {
//                simModeFlag = false;
//            }
//            if (simModeFlag)
//            {// 如果旧卡是23G卡，就去判断新卡类型
//
//                String newSimTypeCode = oldSimInfo.getString("RES_TYPE_CODE").substring(1);
//                IDataset newSet = ResParaInfoQry.checkUser4GUsimCard(newSimTypeCode);
//                if (IDataUtil.isNotEmpty(newSet))
//                {// 如果新卡是4G卡
//                    returnInfo.put("FEE_TAG", "0");
//                    returnInfo.put("FEE_INFO", "2G/3G卡用户更换4G卡免费");
//                    returnInfo.put("FEE", "0");
//                    returnInfo.put("FEE_TARDE", "1");
//                    return returnInfo;
//                }
//            }
//        }
//
//        /*
//         * 非专属卡转专属卡为免费 --白卡专属卡 --5 神州行专用卡白卡 --0 全球通专属白卡 --D OTA白卡 --SIM卡专属卡 --D M2.0 OTA卡 --F 全球通专属卡 --H 神州行专用卡
//         */
//        String oldResTypeCode = oldSimInfo.getString("RES_TYPE_CODE");
//        String newResTypeCode = newSimInfo.getString("RES_TYPE_CODE");
//        boolean newCardFlag = false;
//        boolean oldCardFlag = false;
//        if (StringUtils.isNotEmpty(oldSimInfo.getString("EMPTY_CARD_ID")))
//        {
//            oldCardFlag = true;// 老卡是否为白卡写成
//            IData oldEmptyCardInfo = ResCall.getEmptycardInfo(oldSimInfo.getString("EMPTY_CARD_ID"), serialNumber, "USE").getData(0);
//            oldResTypeCode = oldEmptyCardInfo.getString("RES_TYPE_CODE", "");
//        }
//        if (StringUtils.isNotEmpty(newSimInfo.getString("EMPTY_CARD_ID")))
//        {
//            newCardFlag = true;// 新卡是否为白卡写成
//            IData newEmptyCardInfo = ResCall.getEmptycardInfo(newSimInfo.getString("EMPTY_CARD_ID"), serialNumber, "").getData(0);
//            newResTypeCode = newEmptyCardInfo.getString("RES_TYPE_CODE", "");
//        }
//        // 旧卡为非专用卡，新卡为白卡专属卡时不收费
//        if (newCardFlag && ("1D".equals(newResTypeCode) || "10".equals(newResTypeCode) || "15".equals(newResTypeCode)))
//        {
//            if (!oldCardFlag && !"1D".equals(oldResTypeCode) && !"1F".equals(oldResTypeCode) && !"1H".equals(oldResTypeCode))
//            {
//                returnInfo.put("FEE_TAG", "0");
//                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "1");
//
//                return returnInfo;
//            }
//            else if (oldCardFlag && !"1D".equals(oldResTypeCode) && !"15".equals(oldResTypeCode) && !"10".equals(oldResTypeCode))
//            {
//                returnInfo.put("FEE_TAG", "0");
//                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "1");
//
//                return returnInfo;
//            }
//        }
//        // 旧卡为非专用卡，新卡为SIM卡专属卡时不收费
//        else if (!newCardFlag && ("1D".equals(newResTypeCode) || "1F".equals(newResTypeCode) || "1H".equals(newResTypeCode)))
//        {
//            if (!oldCardFlag && !"D".equals(oldResTypeCode) && !"F".equals(oldResTypeCode) && !"H".equals(oldResTypeCode))
//            {
//                returnInfo.put("FEE_TAG", "0");
//                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "1");
//
//                return returnInfo;
//            }
//            else if (oldCardFlag && !"D".equals(oldResTypeCode) && !"5".equals(oldResTypeCode) && !"0".equals(oldResTypeCode))
//            {
//                returnInfo.put("FEE_TAG", "0");
//                returnInfo.put("FEE_INFO", "非专属卡转专属卡为免费");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "1");
//
//                return returnInfo;
//            }
//        }
//
//        /*
//         * 先判断是否为大客户，大客户每年可免费补换卡2次， 非大客户则判断用户主套餐，全球通商旅、全球通上网888/588/388套餐每年免费换卡2次， 全球通商旅、全球通上网288套餐每年免费换卡1次
//         */
//        int vipFreeChgCard = 0;
//        IDataset tagInfo = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), "CS_NUM_VIPFREECHGCARDS", "CSM", "0");
//        // 获取大客户是否需要缴费：大客户免费换卡次数 减掉 本年换卡次数 大于 零 免费；否则交费
//        IDataset vipInfo = CustVipInfoQry.qryVipInfoByUserId(userInfo.getString("USER_ID"));
//        String userVipTypeCode = "";
//        String userVipClassId = "";
//        if (IDataUtil.isNotEmpty(vipInfo))
//        {
//            userVipTypeCode = vipInfo.getData(0).getString("VIP_TYPE_CODE", "");
//            userVipClassId = vipInfo.getData(0).getString("VIP_CLASS_ID", "");
//        }
//        if (userVipTypeCode.equals("2") || userVipClassId.equals("1") || userVipClassId.equals("2") || userVipClassId.equals("3") || userVipClassId.equals("4") || userVipClassId.equals("5"))
//        {
//            if (IDataUtil.isNotEmpty(tagInfo))
//            {
//                vipFreeChgCard = Integer.parseInt(tagInfo.getData(0).getString("TAG_NUMBER"));
//            }
//            else
//            {
//                // 获取大客户免费换卡次数失败
//                CSAppException.apperr(CrmCardException.CRM_CARD_242);
//            }
//        }
//
//        int productFreeChgCard = 0;
//        String strTagInfo = "";
//        IDataset tagInfo2 = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), "CS_NUM_VIPFREECHGCARDS", "CSM", "0");
//        IData userProdInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
//        if (IDataUtil.isNotEmpty(tagInfo2))
//        {
//            strTagInfo = tagInfo.getData(0).getString("TAG_INFO", "");
//            String[] p = strTagInfo.split(",");
//            for (int i = 0; i < p.length; i = i + 2)
//            {
//                if (userProdInfo.getString("PRODUCT_ID").equals(p[i]))
//                {
//                    productFreeChgCard = Integer.parseInt(p[i + 1]);
//                }
//            }
//        }
//        else
//        {
//            CSAppException.apperr(CrmCardException.CRM_CARD_254);
//        }
//        // 如果vip大客户免费换卡次数大于全球通商旅套餐免费换卡次数
//        if (vipFreeChgCard > productFreeChgCard)
//        {
//            // 获取大客户是否需要缴费：大客户免费换卡次数 减掉 本年换卡次数 大于 零 免费；否则交费
//            IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userInfo.getString("USER_ID", ""), "05", "0");// new
//            int iCount = 0;
//            for (int i = 0; i < userOtherserv.size(); i++)
//            {
//                IData tmp = (IData) userOtherserv.get(i);
//                String nowYear = SysDateMgr.getNowYear();
//                if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
//                {
//                    iCount++;
//                }
//            }
//            if (iCount < vipFreeChgCard || vipFreeChgCard == 0)
//            {
//                returnInfo.put("FEE_TAG", "0");
//                returnInfo.put("FEE_INFO", "大客户免费换卡，每年可免费补换卡2次");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "0");
//                returnInfo.put("RSRV_VALUE_CODE", "05");
//                returnInfo.put("RSRV_VALUE", "VIP用户免费补换卡");
//
//                return returnInfo;
//
//            }
//            else
//            {
//                returnInfo.put("FEE_TAG", "1");
//                returnInfo.put("FEE_INFO", "该用户在今年免费补换卡次数已超出，本次换卡需交卡费！");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "1");
//            }
//        }
//        else
//        {
//            if (productFreeChgCard > 0)
//            {
//                IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userInfo.getString("USER_ID"), "07", "0");// new
//                int iCount = 0;
//                for (int i = 0; i < userOtherserv.size(); i++)
//                {
//                    IData tmp = (IData) userOtherserv.get(i);
//                    String nowYear = SysDateMgr.getNowYear();
//                    if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
//                    {
//                        iCount++;
//                    }
//                }
//                if (iCount < productFreeChgCard || productFreeChgCard == 0)
//                {
//                    returnInfo.put("FEE_TAG", "0");
//                    returnInfo.put("FEE_INFO", "该全球通商旅套餐用户在今年可以免费补换" + productFreeChgCard + "次！");
//                    returnInfo.put("FEE", "0");
//                    returnInfo.put("FEE_TARDE", "0");
//
//                    returnInfo.put("RSRV_VALUE_CODE", "07");
//                    returnInfo.put("RSRV_VALUE", "2011年全球通商旅套餐免费换卡");
//
//                    return returnInfo;
//                }
//                else
//                {
//                    returnInfo.put("FEE_TAG", "1");
//                    returnInfo.put("FEE_INFO", "该全球通商旅套餐用户在今年免费补换卡次数已超出，本次换卡需交卡费！");
//                    returnInfo.put("FEE", "0");
//                    returnInfo.put("FEE_TARDE", "1");
//                }
//            }
//        }
//
//        // SIM卡补换卡费用调整,在网3年以上的老客户可以免费换卡
//        String strOpenDate = userInfo.getString("OPEN_DATE", "");
//        int openMonths = SysDateMgr.monthInterval(strOpenDate, SysDateMgr.getSysDate());
//        if (openMonths >= 36)
//        {
//            // 获取3年老客户免费换卡次数
//            int oldCustFreeChgCard = 0;
//            IDataset tagInfo3 = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), "CS_NUM_OLDCUSTFREECHGCARDS", "CSM", "0");
//            if (IDataUtil.isNotEmpty(tagInfo3))
//            {
//                oldCustFreeChgCard = Integer.parseInt(tagInfo3.getData(0).getString("TAG_NUMBER"));
//            }
//            else
//            {
//                CSAppException.apperr(CrmCardException.CRM_CARD_243);
//            }
//
//            IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userInfo.getString("USER_ID"), "03", "0");// new
//            int iCount = 0;
//            for (int i = 0; i < userOtherserv.size(); i++)
//            {
//                IData tmp = (IData) userOtherserv.get(i);
//                String nowYear = SysDateMgr.getNowYear();
//                if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
//                {
//                    iCount++;
//                }
//            }
//            if (iCount < oldCustFreeChgCard || oldCustFreeChgCard == 0)
//            {
//                returnInfo.put("FEE_TAG", "0");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "0");
//
//                if (oldCustFreeChgCard == 0)
//                    returnInfo.put("FEE_INFO", "该用户在网年限大于3年,可以不限次数免费换卡！");
//                else
//                    returnInfo.put("FEE_INFO", "该用户在网年限大于3年,可以一年免费换卡" + oldCustFreeChgCard + "次！");
//
//                // if("true".equals(Va)) //如果营改增不是放开标记则直接返回
//                // {
//                // //start
//                // td.put("CARD_FREE_CARD", "1");
//                // td.put("CARD_RES_KIND_CODE", strResKindCode);
//                // td.put("CARD_CAPACITY_TYPE_CODE", strCapacityTypeCode);
//                // td.put("CARD_PRODUCT_ID", strProductId);
//                // td.put("CARD_CLASS_ID", strClassId);
//                // //end
//                // }
//                returnInfo.put("RSRV_VALUE_CODE", "03");
//                returnInfo.put("RSRV_VALUE", "在网3年以上老客户免费补换卡");
//
//                return returnInfo;
//            }
//            else
//            {
//                returnInfo.put("FEE_TAG", "1");
//                returnInfo.put("FEE_INFO", "该用户在今年免费补换卡次数已超出，本次换卡需交卡费！");
//                returnInfo.put("FEE", "0");
//                returnInfo.put("FEE_TARDE", "1");
//            }
//        }

        /**
         * 获取卡费，资源取消了CAPACITY_TYPE_CODE等字段，融合至RES_KIND_CODE
         */
        String resKindCode = newSimInfo.getString("RES_KIND_CODE", "");
        IData feeData = DevicePriceQry.getDevicePrice(getTradeEparchyCode(), "", tradeTypeCode, "-1", resKindCode);
        if (IDataUtil.isEmpty(feeData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "544032:获取补换卡费用失败！");
        }
        else
        {
            // returnInfo.put("FEE_MODE", "0");//营业费
            // returnInfo.put("FEE_TYPE_CODE", "10");
            returnInfo.put("FEE", feeData.getString("DEVICE_PRICE"));
            // returnInfo.put("FACT_PAY_FEE", feeData.getString("DEVICE_PRICE"));
        }

        return returnInfo;
    }
    
    /**
     * 获取卡费，通过resTypeCode获取卡费
     * @throws Exception 
     */
    public String getDevicePrice(String simCardNo,String resTypeCode,String resKindCode) throws Exception {
    	
    	
    	String fee = "";
        IData feeData = DevicePriceQry.getDevicePrice(getTradeEparchyCode(), "", "321", resKindCode, resTypeCode);
        if (IDataUtil.isEmpty(feeData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "544032:获取补换卡费用失败！");
        }
        else
        {
            fee =  feeData.getString("DEVICE_PRICE");
        }
    	return fee;
    }
    
}
