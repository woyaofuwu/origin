
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class FamilyCreateBean extends CSBizBean
{

    /**
     * 添加副号码的校验
     * 
     * @param input
     * @throws Exception
     * @author zhouwu
     * @date 2014-05-28 15:29:18
     */
    public IData checkAddMeb(IData input) throws Exception
    {
        String mainNum = input.getString("SERIAL_NUMBER");// 主号码
        String mebNum = input.getString("SERIAL_NUMBER_B");// 副号码

        IData data = new DataMap();// 返回结果

        // 校验主号码和副号码是否一致
        if (mainNum.equals(mebNum))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_58, mebNum);
        }

        // 副号码是否相同校验
        IData mainUser = UcaInfoQry.qryUserInfoBySn(mainNum);
        if (IDataUtil.isEmpty(mainUser))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mainNum);
        }
        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(mainUser.getString("USER_ID"), "45", null);
        if (IDataUtil.isNotEmpty(result))
        {
            IData rela = result.getData(0);
            String userIdA = rela.getString("USER_ID_A");
            IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);
            if (IDataUtil.isNotEmpty(mebList))
            {
                for (int i = 0, size = mebList.size(); i < size; i++)
                {
                    IData meb = mebList.getData(i);
                    String mebSn = meb.getString("SERIAL_NUMBER_B");
                    if (mebNum.equals(mebSn))
                    {
                        CSAppException.apperr(FamilyException.CRM_FAMILY_57, mebNum);
                    }
                }
            }
        }

        IData user = UcaInfoQry.qryUserMainProdInfoBySn(mebNum);
        if (IDataUtil.isNotEmpty(user))
        {
            String userId = user.getString("USER_ID");
            String eparchyCode = user.getString("EPARCHY_CODE");
            String netTypeCode = user.getString("NET_TYPE_CODE");
            String bandCode = user.getString("BRAND_CODE");

            // TD二代无线固话号码不能办理亲亲网业务
            if ("18".equals(netTypeCode))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_61);
            }

            // 非移动全球通、神州行和动感地带品牌用户不能作为副号码
            if (!"G001".equals(bandCode) && !"G002".equals(bandCode) && !"G010".equals(bandCode))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_62);
            }

            // 查询用户当前生效的优惠
            IDataset userDiscnts = UserDiscntInfoQry.qryUserNormalDiscntByIdCodeFromDB(userId, "45", eparchyCode);
            if (IDataUtil.isEmpty(userDiscnts))
            {
                // 校验是否监务通用户
                boolean isJWTUser = checkIsJWTUser(userId);
                data.put("IS_JWT_USER", isJWTUser);
            }
            else
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_59);
            }
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, mebNum);
        }

        return data;
    }

    /**
     * 添加副号码的校验（多个号码）
     *
     * @param input
     * @throws Exception
     * @author
     * @date
     */
    public IDataset checkAddMebMul(IData input) throws Exception
    {

        String[] mebList={};
        String mebListStr = input.getString("MEB_LIST");// 副号码
        if(StringUtils.isNotBlank(mebListStr)){
            mebList = mebListStr.split(",");
        }

        IDataset resultList = new DatasetList();// 返回结果

        if(null!=mebList&&mebList.length>0){
            for (int i = 0; i < mebList.length; i++) {
                input.put("SERIAL_NUMBER_B",mebList[i]);
                DataMap result = new DataMap();
                result.put("SERIAL_NUMBER_B",mebList[i]);
                try{
                    IData checkResult = checkAddMeb(input);
                    result.put("IS_JWT_USER",checkResult.getString("IS_JWT_USER"));
                    result.put("ERROR_TAG","0");
                    result.put("ERROR_INFO","校验通过");
                }catch (Exception e){
                    result.put("ERROR_TAG","1");
                    if(StringUtils.isNotBlank(e.getMessage())){
                        String[] errorInfo = e.getMessage().split("`");
                        //去除显示类似CRM_FAMILY_59错误编码
                        if(errorInfo.length>1){
                            result.put("ERROR_INFO",errorInfo[1]);
                        }else{
                            result.put("ERROR_INFO",e.getMessage());
                        }
                    }
                }
                resultList.add(result);
            }
        }
        return resultList;
    }

    /**
     * 校验入参
     * 
     * @param input
     * @throws Exception
     */
    public IData checkInData(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String responseTag = input.getString("RESPONSE_TAG");

        IDataset memberList = DataHelper.toDataset(input);// 直接转为Dataset就是memList ？？？？？？？？？？
        if (IDataUtil.isEmpty(memberList))
        {
            memberList.add(input);
        }
        // >>>>>>>>>>>>>>>>>>>>>>>>>>二次确认短信延后
        boolean secondCorfirmTag = StringUtils.isNotBlank(responseTag) && memberList.size() == 1;
        boolean secondCorfirmFind = false;

        if (StringUtils.isBlank(serialNumber))
        {
            // SERIAL_NUMBER为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_70);
        }

        // 校验服务号码是否有效
        IData user = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            // SERIAL_NUMBER无效
            CSAppException.apperr(FamilyException.CRM_FAMILY_78, serialNumber);
        }

        // 屏蔽，规则里已做处理
        /*
         * String brandCode = user.getString("BRAND_CODE");//品牌编码 String netTypeCode =
         * user.getString("NET_TYPE_CODE");//网络类型编码 //TD二代无线固话号码不能办理亲亲网业务 if ("18".equals(netTypeCode)) {
         * CSAppException.apperr(FamilyException.CRM_FAMILY_61); } //非移动全球通、神州行和动感地带品牌用户不能作为主号码 if
         * (!"G001".equals(brandCode) && !"G002".equals(brandCode) && !"G010".equals(brandCode)) {
         * CSAppException.apperr(FamilyException.CRM_FAMILY_717); }
         */

        IData param = new DataMap();
        String userId = user.getString("USER_ID");
        String userProductId = user.getString("PRODUCT_ID");
        String eparchyCode = user.getString("EPARCHY_CODE");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);
        param.put("USER_PRODUCT_ID", userProductId);
        param.put("EPARCHY_CODE", eparchyCode);

        IData pageInfo = loadFamilyCreateInfo(param);
        pageInfo.put("SERIAL_NUMBER", serialNumber);

        String serviceId = pageInfo.getString("SERVICE_ID", "");
        if (StringUtils.isBlank(serviceId))
        {// 组建亲亲网
            // 产品的处理
            String inProductId = input.getString("PRODUCT_ID");// 入参：产品ID
            IDataset productList = pageInfo.getDataset("PRODUCT_LIST");
            if (StringUtils.isNotBlank(inProductId))
            {
                boolean isExistProduct = isExistValue(productList, "PRODUCT_ID", inProductId);
                if (!isExistProduct)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_710, inProductId);// 产品ID[%d]无效
                pageInfo.put("PRODUCT_ID", inProductId);
            }
            else
            {
                if (IDataUtil.isNotEmpty(productList) && productList.size() == 1)
                {
                    String tempProductId = productList.getData(0).getString("PRODUCT_ID", "");
                    pageInfo.put("PRODUCT_ID", tempProductId);
                }
                else if (IDataUtil.isNotEmpty(productList) && productList.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_711);// PRODUCT_ID不能为空
                }
            }

            // 优惠的处理
            String inDiscntCode = input.getString("DISCNT_CODE");
            IDataset discntList = pageInfo.getDataset("DISCNT_LIST");
            if (StringUtils.isNotBlank(inDiscntCode))
            {
                boolean isExistDiscnt = isExistValue(discntList, "DISCNT_CODE", inDiscntCode);
                if (!isExistDiscnt)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, inDiscntCode);// 优惠[%d]无效
                pageInfo.put("DISCNT_CODE", inDiscntCode);
            }
            else
            {
                if (IDataUtil.isNotEmpty(discntList) && discntList.size() == 1)
                {
                    String tempDiscntCode = discntList.getData(0).getString("DISCNT_CODE", "");
                    pageInfo.put("DISCNT_CODE", tempDiscntCode);
                }
                else if (IDataUtil.isNotEmpty(discntList) && discntList.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_713);// DISCNT_CODE不能为空
                }
            }

            // 叠加优惠的处理
            String inAppDiscntCode = input.getString("APP_DISCNT_CODE");
            IDataset appDiscntList = pageInfo.getDataset("APP_DISCNT_LIST");
            if (StringUtils.isNotBlank(inAppDiscntCode))
            {
                boolean isExistAppDiscnt = isExistValue(appDiscntList, "DISCNT_CODE", inAppDiscntCode);
                if (!isExistAppDiscnt)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, inAppDiscntCode);// 优惠[%d]无效
                pageInfo.put("APP_DISCNT_CODE", inAppDiscntCode);
            }
            else
            { // 叠加优惠为可选包 不是必选 暂时屏蔽 老系统必选
                pageInfo.put("APP_DISCNT_CODE", "");
                /*
                 * if (IDataUtil.isNotEmpty(appDiscntList) && appDiscntList.size() == 1) { String tempAppDiscntCode =
                 * appDiscntList.getData(0).getString("DISCNT_CODE", ""); pageInfo.put("APP_DISCNT_CODE",
                 * tempAppDiscntCode); } else if (IDataUtil.isNotEmpty(appDiscntList) && appDiscntList.size() > 1) {
                 * CSAppException.apperr(FamilyException.CRM_FAMILY_714);//APP_DISCNT_CODE不能为空 }
                 */
            }

            // 短号的处理
            String shortCode = input.getString("SHORT_CODE", "");
            boolean isJwtUser = pageInfo.getBoolean("ISJWT_FLAG");
            if (!isJwtUser && StringUtils.equals(shortCode, ""))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_715);// 主卡的短号不能为空
            }
            if (isJwtUser && !StringUtils.equals(shortCode, ""))
            {
                shortCode = "";
            }
            if (!isJwtUser)
            {
                checkShortCode(shortCode);
            }
            pageInfo.put("SHORT_CODE", shortCode);
        }

        // 成员的处理
        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            IData member = memberList.getData(i);
            String serialNumberB = member.getString("SERIAL_NUMBER_B");
            String shortCodeB = member.getString("SHORT_CODE_B");

            if (StringUtils.isBlank(serialNumberB))
            {
                if (size != 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_72);// SERIAL_NUMBER_B为空
                }
                if (StringUtils.isNotBlank(serviceId))
                {// 组建亲亲网
                    // 您已经存在亲亲网，本次创建失败
                    CSAppException.apperr(FamilyException.CRM_FAMILY_741);
                }
                continue;
            }

            IData userB = UcaInfoQry.qryUserInfoBySn(serialNumberB);
            if (IDataUtil.isEmpty(userB))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberB);
            }

            member.put("SERIAL_NUMBER", serialNumber);
            checkAddMeb(member);

            // 二次确认短信
            if (secondCorfirmTag)
            {
                continue;
            }

            // 成员短号的处理
            String userIdB = userB.getString("USER_ID");
            boolean isJwtUser = checkIsJWTUser(userIdB);
            if (!isJwtUser && StringUtils.isBlank(shortCodeB))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_718, serialNumberB);// 副卡的短号不能为空
            }
            if (isJwtUser && StringUtils.isNotBlank(shortCodeB))
            {
                shortCodeB = "";
            }
            if (!isJwtUser)
            {
                checkShortCode(shortCodeB);
            }
            member.put("SHORT_CODE_B", shortCodeB);

            // 副卡优惠的处理
            String discntCodeB = member.getString("DISCNT_CODE_B");
            IDataset discntListB = pageInfo.getDataset("VICE_DISCNT_LIST");
            if (StringUtils.isNotBlank(discntCodeB))
            {
                boolean isExistDiscntCode = isExistValue(discntListB, "DISCNT_CODE", discntCodeB);
                if (!isExistDiscntCode)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, discntCodeB);// 副卡优惠无效
            }
            else
            {
                if (IDataUtil.isNotEmpty(discntListB) && discntListB.size() == 1)
                {
                    String tempDiscntCodeB = discntListB.getData(0).getString("DISCNT_CODE", "");
                    member.put("DISCNT_CODE_B", tempDiscntCodeB);
                }
                else if (IDataUtil.isNotEmpty(discntListB) && discntListB.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_719);// DISCNT_CODE_B不能为空
                }
            }

            // 副卡叠加优惠的处理
            String appDiscntCodeB = member.getString("APP_DISCNT_CODE_B");
            IDataset appDiscntListB = pageInfo.getDataset("APP_DISCNT_LIST");
            if (StringUtils.isNotBlank(appDiscntCodeB))
            {
                boolean isExistAppDiscntCodeB = isExistValue(appDiscntListB, "DISCNT_CODE", appDiscntCodeB);
                if (!isExistAppDiscntCodeB)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, appDiscntCodeB);// 副卡叠加优惠无效
            }

            member.put("tag", "0");// 表示新增成员
        }

        /**
         * 处理短信回复内容
         */
        if (secondCorfirmTag)
        {
            IData member = memberList.getData(0);
            String serialNumberB = member.getString("SERIAL_NUMBER_B");

            IDataset bhTrades = TradeBhQry.qryBhTradeBySn(serialNumber, userId, "283");

            if (IDataUtil.isNotEmpty(bhTrades))
            {
                for (int i = 0, size = bhTrades.size(); i < size; i++)
                {
                    IData bhTrade = bhTrades.getData(i);
                    String processsTagSet = bhTrade.getString("PROCESS_TAG_SET");
                    processsTagSet = StringUtils.substring(processsTagSet, 0, 1);

                    if (StringUtils.equals(processsTagSet, "1"))
                    {
                        String discntCodeBs = bhTrade.getString("RSRV_STR4", "");
                        String shortCodeBs = bhTrade.getString("RSRV_STR6", "");
                        String serialNumberBs = bhTrade.getString("RSRV_STR7", "");
                        String appDiscntCodeBs = bhTrade.getString("RSRV_STR8", "");

                        // appDiscntCodeBs=appDiscntCodeBs+",1";//老系统这个是什么意思？ 屏蔽

                        String[] discntCodeBArr = StringUtils.split(discntCodeBs, ",");
                        String[] shortCodeBArr = StringUtils.split(shortCodeBs, ",");
                        String[] serialNumberBArr = StringUtils.split(serialNumberBs, ",");
                        String[] appDiscntCodeBArr = StringUtils.split(appDiscntCodeBs, ",");

                        // 去掉了判断条件&& serialNumberBArr.length == shortCodeBArr.length 因为短号可能存在为空
                        if (serialNumberBArr.length == discntCodeBArr.length)
                        {
                            for (int j = 0; j < serialNumberBArr.length; j++)
                            {
                                if (StringUtils.equals(serialNumberBArr[j], serialNumberB))
                                {
                                    secondCorfirmFind = true;
                                    member.put("APP_DISCNT_CODE_B", appDiscntCodeBArr[j]);
                                    member.put("SHORT_CODE_B", shortCodeBArr[j]);
                                    member.put("DISCNT_CODE_B", discntCodeBArr[j]);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            // 获取界面受理参数有误
                            CSAppException.apperr(FamilyException.CRM_FAMILY_737);
                        }
                    }

                    if (secondCorfirmFind)
                    {
                        break;
                    }
                }
            }
            else
            {
                // 界面受理信息没查询到
                CSAppException.apperr(FamilyException.CRM_FAMILY_738);
            }

            if (member.containsKey("APP_DISCNT_CODE_B") && !"1".equals(responseTag) && !"Y".equals(responseTag) && !"y".equals(responseTag))
            {
                member.remove("APP_DISCNT_CODE_B");
            }

            member.put("tag", "0");// 表示新增成员
        }

        if (secondCorfirmTag && !secondCorfirmFind)
        {
            // 没有找到您的二次确认相关信息
            CSAppException.apperr(FamilyException.CRM_FAMILY_739);
        }

        pageInfo.put("MEB_LIST", memberList);// 成员列表
        pageInfo.put("FMY_VERIFY_MODE", "9");// 副号校验方式：接口传9

        return pageInfo;
    }

    /**
     * 校验入参接口
     * 
     * @param input
     * @throws Exception
     */
    public IData checkInDataYanwu(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String responseTag = input.getString("RESPONSE_TAG");

        IDataset memberList = DataHelper.toDataset(input);// 直接转为Dataset就是memList ？？？？？？？？？？
        if (IDataUtil.isEmpty(memberList))
        {
            memberList.add(input);
        }
        // >>>>>>>>>>>>>>>>>>>>>>>>>>二次确认短信延后
        boolean secondCorfirmTag = StringUtils.isNotBlank(responseTag) && memberList.size() == 1;
        boolean secondCorfirmFind = false;

        if (StringUtils.isBlank(serialNumber))
        {
            // SERIAL_NUMBER为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_70);
        }

        // 校验服务号码是否有效
        IData user = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            // SERIAL_NUMBER无效
            CSAppException.apperr(FamilyException.CRM_FAMILY_78, serialNumber);
        }

        // 屏蔽，规则里已做处理
        /*
         * String brandCode = user.getString("BRAND_CODE");//品牌编码 String netTypeCode =
         * user.getString("NET_TYPE_CODE");//网络类型编码 //TD二代无线固话号码不能办理亲亲网业务 if ("18".equals(netTypeCode)) {
         * CSAppException.apperr(FamilyException.CRM_FAMILY_61); } //非移动全球通、神州行和动感地带品牌用户不能作为主号码 if
         * (!"G001".equals(brandCode) && !"G002".equals(brandCode) && !"G010".equals(brandCode)) {
         * CSAppException.apperr(FamilyException.CRM_FAMILY_717); }
         */

        IData param = new DataMap();
        String userId = user.getString("USER_ID");
        String userProductId = user.getString("PRODUCT_ID");
        String eparchyCode = user.getString("EPARCHY_CODE");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);
        param.put("USER_PRODUCT_ID", userProductId);
        param.put("EPARCHY_CODE", eparchyCode);

        IData pageInfo = loadFamilyCreateInfo(param);
        pageInfo.put("SERIAL_NUMBER", serialNumber);

        String productId = pageInfo.getString("PRODUCT_ID", "");
        if (StringUtils.isBlank(productId))
        {// 组建亲亲网
            // 产品的处理
            String inProductId = input.getString("PRODUCT_ID");// 入参：产品ID
            IDataset productList = pageInfo.getDataset("PRODUCT_LIST");
            if (StringUtils.isNotBlank(inProductId))
            {
                boolean isExistProduct = isExistValue(productList, "PRODUCT_ID", inProductId);
                if (!isExistProduct)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_710, inProductId);// 产品ID[%d]无效
                pageInfo.put("PRODUCT_ID", inProductId);
            }
            else
            {
                if (IDataUtil.isNotEmpty(productList) && productList.size() == 1)
                {
                    String tempProductId = productList.getData(0).getString("PRODUCT_ID", "");
                    pageInfo.put("PRODUCT_ID", tempProductId);
                }
                else if (IDataUtil.isNotEmpty(productList) && productList.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_711);// PRODUCT_ID不能为空
                }
            }

            // 优惠的处理
            String inDiscntCode = input.getString("DISCNT_CODE");
            IDataset discntList = pageInfo.getDataset("DISCNT_LIST");
            if (StringUtils.isNotBlank(inDiscntCode))
            {
                boolean isExistDiscnt = isExistValue(discntList, "DISCNT_CODE", inDiscntCode);
                if (!isExistDiscnt)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, inDiscntCode);// 优惠[%d]无效
                pageInfo.put("DISCNT_CODE", inDiscntCode);
            }
            else
            {
                if (IDataUtil.isNotEmpty(discntList) && discntList.size() == 1)
                {
                    String tempDiscntCode = discntList.getData(0).getString("DISCNT_CODE", "");
                    pageInfo.put("DISCNT_CODE", tempDiscntCode);
                }
                else if (IDataUtil.isNotEmpty(discntList) && discntList.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_713);// DISCNT_CODE不能为空
                }
            }

            // 叠加优惠的处理
            String inAppDiscntCode = input.getString("APP_DISCNT_CODE");
            IDataset appDiscntList = pageInfo.getDataset("APP_DISCNT_LIST");
            if (StringUtils.isNotBlank(inAppDiscntCode))
            {
                boolean isExistAppDiscnt = isExistValue(appDiscntList, "DISCNT_CODE", inAppDiscntCode);
                if (!isExistAppDiscnt)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, inAppDiscntCode);// 优惠[%d]无效
                pageInfo.put("APP_DISCNT_CODE", inAppDiscntCode);
            }
            else
            { // 叠加优惠为可选包 不是必选 暂时屏蔽 老系统必选
                pageInfo.put("APP_DISCNT_CODE", "");
                /*
                 * if (IDataUtil.isNotEmpty(appDiscntList) && appDiscntList.size() == 1) { String tempAppDiscntCode =
                 * appDiscntList.getData(0).getString("DISCNT_CODE", ""); pageInfo.put("APP_DISCNT_CODE",
                 * tempAppDiscntCode); } else if (IDataUtil.isNotEmpty(appDiscntList) && appDiscntList.size() > 1) {
                 * CSAppException.apperr(FamilyException.CRM_FAMILY_714);//APP_DISCNT_CODE不能为空 }
                 */
            }

            // 短号的处理
            String shortCode = input.getString("SHORT_CODE", "");
            boolean isJwtUser = pageInfo.getBoolean("ISJWT_FLAG");
            if (!isJwtUser && StringUtils.equals(shortCode, ""))
            {
            	//shortCode = "520";
                //CSAppException.apperr(FamilyException.CRM_FAMILY_715);// 主卡的短号不能为空
            }
            if (isJwtUser && !StringUtils.equals(shortCode, ""))
            {
                shortCode = "";
            }
            if (!isJwtUser)
            {
                checkShortCode(shortCode);
            }
            pageInfo.put("SHORT_CODE", shortCode);
        }

        // 成员的处理
        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            IData member = memberList.getData(i);
            String serialNumberB = member.getString("SERIAL_NUMBER_B");
            String shortCodeB = member.getString("SHORT_CODE_B");

            if (StringUtils.isBlank(serialNumberB))
            {
                if (size != 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_72);// SERIAL_NUMBER_B为空
                }
                if (StringUtils.isNotBlank(productId))
                {// 组建亲亲网
                    // 您已经存在亲亲网，本次创建失败
                    CSAppException.apperr(FamilyException.CRM_FAMILY_741);
                }
                continue;
            }

            IData userB = UcaInfoQry.qryUserInfoBySn(serialNumberB);
            if (IDataUtil.isEmpty(userB))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberB);
            }

            member.put("SERIAL_NUMBER", serialNumber);
            checkAddMeb(member);

            // 二次确认短信
            if (secondCorfirmTag)
            {
                continue;
            }

            // 成员短号的处理
            String userIdB = userB.getString("USER_ID");
            boolean isJwtUser = checkIsJWTUser(userIdB);
            if (!isJwtUser && StringUtils.isBlank(shortCodeB))
            {
                //CSAppException.apperr(FamilyException.CRM_FAMILY_718, serialNumberB);// 副卡的短号不能为空
            }
            if (isJwtUser && StringUtils.isNotBlank(shortCodeB))
            {
                shortCodeB = "";
            }
            if (!isJwtUser)
            {
                checkShortCode(shortCodeB);
            }
            member.put("SHORT_CODE_B", shortCodeB);

            // 副卡优惠的处理
            String discntCodeB = member.getString("DISCNT_CODE_B");
            IDataset discntListB = pageInfo.getDataset("VICE_DISCNT_LIST");
            if (StringUtils.isNotBlank(discntCodeB))
            {
                boolean isExistDiscntCode = isExistValue(discntListB, "DISCNT_CODE", discntCodeB);
                if (!isExistDiscntCode)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, discntCodeB);// 副卡优惠无效
            }
            else
            {
                if (IDataUtil.isNotEmpty(discntListB) && discntListB.size() == 1)
                {
                    String tempDiscntCodeB = discntListB.getData(0).getString("DISCNT_CODE", "");
                    member.put("DISCNT_CODE_B", tempDiscntCodeB);
                }
                else if (IDataUtil.isNotEmpty(discntListB) && discntListB.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_719);// DISCNT_CODE_B不能为空
                }
            }

            // 副卡叠加优惠的处理
            String appDiscntCodeB = member.getString("APP_DISCNT_CODE_B");
            IDataset appDiscntListB = pageInfo.getDataset("APP_DISCNT_LIST");
            if (StringUtils.isNotBlank(appDiscntCodeB))
            {
                boolean isExistAppDiscntCodeB = isExistValue(appDiscntListB, "DISCNT_CODE", appDiscntCodeB);
                if (!isExistAppDiscntCodeB)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, appDiscntCodeB);// 副卡叠加优惠无效
            }

            member.put("tag", "0");// 表示新增成员
        }

        /**
         * 处理短信回复内容
         */
        if (secondCorfirmTag)
        {
            IData member = memberList.getData(0);
            String serialNumberB = member.getString("SERIAL_NUMBER_B");

            IDataset bhTrades = TradeBhQry.qryBhTradeBySn(serialNumber, userId, "283");

            if (IDataUtil.isNotEmpty(bhTrades))
            {
                for (int i = 0, size = bhTrades.size(); i < size; i++)
                {
                    IData bhTrade = bhTrades.getData(i);
                    String processsTagSet = bhTrade.getString("PROCESS_TAG_SET");
                    processsTagSet = StringUtils.substring(processsTagSet, 0, 1);

                    if (StringUtils.equals(processsTagSet, "1"))
                    {
                        String discntCodeBs = bhTrade.getString("RSRV_STR4", "");
                        String shortCodeBs = bhTrade.getString("RSRV_STR6", "");
                        String serialNumberBs = bhTrade.getString("RSRV_STR7", "");
                        String appDiscntCodeBs = bhTrade.getString("RSRV_STR8", "");

                        // appDiscntCodeBs=appDiscntCodeBs+",1";//老系统这个是什么意思？ 屏蔽

                        String[] discntCodeBArr = StringUtils.split(discntCodeBs, ",");
                        String[] shortCodeBArr = StringUtils.split(shortCodeBs, ",");
                        String[] serialNumberBArr = StringUtils.split(serialNumberBs, ",");
                        String[] appDiscntCodeBArr = StringUtils.split(appDiscntCodeBs, ",");

                        // 去掉了判断条件&& serialNumberBArr.length == shortCodeBArr.length 因为短号可能存在为空
                        if (serialNumberBArr.length == discntCodeBArr.length)
                        {
                            for (int j = 0; j < serialNumberBArr.length; j++)
                            {
                                if (StringUtils.equals(serialNumberBArr[j], serialNumberB))
                                {
                                    secondCorfirmFind = true;
                                    member.put("APP_DISCNT_CODE_B", appDiscntCodeBArr[j]);
                                    member.put("SHORT_CODE_B", shortCodeBArr[j]);
                                    member.put("DISCNT_CODE_B", discntCodeBArr[j]);
                                    break;
                                }
                            }
                        }
                        else
                        {
                            // 获取界面受理参数有误
                            CSAppException.apperr(FamilyException.CRM_FAMILY_737);
                        }
                    }

                    if (secondCorfirmFind)
                    {
                        break;
                    }
                }
            }
            else
            {
                // 界面受理信息没查询到
                CSAppException.apperr(FamilyException.CRM_FAMILY_738);
            }

            if (member.containsKey("APP_DISCNT_CODE_B") && !"1".equals(responseTag) && !"Y".equals(responseTag) && !"y".equals(responseTag))
            {
                member.remove("APP_DISCNT_CODE_B");
            }

            member.put("tag", "0");// 表示新增成员
        }

        if (secondCorfirmTag && !secondCorfirmFind)
        {
            // 没有找到您的二次确认相关信息
            CSAppException.apperr(FamilyException.CRM_FAMILY_739);
        }

        pageInfo.put("MEB_LIST", memberList);// 成员列表
        pageInfo.put("FMY_VERIFY_MODE", "9");// 副号校验方式：接口传9

        return pageInfo;
    }
    
    /**
     * 校验是否监务通用户
     * 
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-05-28 16:35:03
     */
    public boolean checkIsJWTUser(String userId) throws Exception
    {
        IDataset userSvcInfo = UserSvcInfoQry.getSvcUserId(userId, "5860");// 监务通成员呼叫限制服务
        if (IDataUtil.isNotEmpty(userSvcInfo))
            return true;
        else
            return false;
    }

    /**
     * 校验短号是否在【520-529】之间
     * 
     * @param shortCode
     * @throws Exception
     */
    public void checkShortCode(String shortCode) throws Exception
    {
    	if (StringUtils.isBlank(shortCode))
        {
            return;
        }
/*        Pattern p = Pattern.compile("52\\d|53\\d");
        Matcher m = p.matcher(shortCode);
        boolean b = m.matches();
        if (!b)
        {
            // 短号非法,短号必须为【520-529】
            CSAppException.apperr(FamilyException.CRM_FAMILY_716,shortCode);
        }*/
        IDataset dataset = new DatasetList();
        dataset = CommparaInfoQry.getCommpara("CSM", "112", "QQWLIMIT","ZZZZ");  //根据套餐代码查询本省套餐
		if(IDataUtil.isEmpty(dataset)){
	        Pattern p = Pattern.compile("52\\d|53\\d");
	        Matcher m = p.matcher(shortCode);
	        boolean b = m.matches();
	        if (!b)
	        {
	            // 短号非法,短号必须为【520-539】
	            CSAppException.apperr(FamilyException.CRM_FAMILY_716,shortCode);
	        }	
		}else{
	        Pattern p = Pattern.compile("52\\d");
	        Matcher m = p.matcher(shortCode);
	        boolean b = m.matches();
	        if (!b)
	        {
	            // 短号非法,短号必须为【520-529】
	            CSAppException.apperr(FamilyException.CRM_FAMILY_833,shortCode);
	        }	
		}
    }

    /**
     * 亲亲网组建/新增成员受理接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData createFamily(IData input) throws Exception
    {
        IData backInfo = new DataMap();

        IData param = checkInData(input);
        //AddCommendstaffLogInfoAction需要推荐工号字段,param未透传
        if(StringUtils.isNotBlank(input.getString("COMMEND_STAFF_ID"))){
            param.put("COMMEND_STAFF_ID",input.getString("COMMEND_STAFF_ID"));
        }
        IDataset rtDataset = CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", param);

        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "业务受理成功");
        backInfo.put("X_RECORDNUM", "1");
        backInfo.put("TRADE_ID", rtDataset.getData(0).getString("TRADE_ID", ""));
        return backInfo;
    }
    /**
     * 亲亲网组建/新增成员受理接口
     *
     * @param input
     * @return
     * @throws Exception
     * @author liangdg3
     */
    public IData createFamilyNew(IData input) throws Exception
    {
        IData backInfo = new DataMap();

        IData param = checkInDataNew(input);
        //AddCommendstaffLogInfoAction需要推荐工号字段,param未透传
        if(StringUtils.isNotBlank(input.getString("COMMEND_STAFF_ID"))){
            param.put("COMMEND_STAFF_ID",input.getString("COMMEND_STAFF_ID"));
        }
        IDataset rtDataset = CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", param);

        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "业务受理成功");
        backInfo.put("TRADE_ID", rtDataset.getData(0).getString("TRADE_ID", ""));
        return backInfo;
    }

    /**
     * 亲亲网组建/新增成员校验接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData createFamilyCheck(IData input) throws Exception
    {
        IData backInfo = new DataMap();

        IData param = checkInDataYanwu(input);
        param.put("PRE_TYPE", "1");// 不生成台帐，只做校验
        IDataset rtDataset = CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", param);

        backInfo.put("X_RESULTCODE", "0");
        backInfo.put("X_RESULTINFO", "业务校验成功");
        backInfo.put("X_RECORDNUM", "1");
        backInfo.put("TRADE_ID", rtDataset.getData(0).getString("TRADE_ID", ""));

        return backInfo;
    }

    /**
     * 根据手机号码得到所有的成员包括主卡 变更亲亲网短号查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getAllMebByMainSn(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(sn);

        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }

        String userId = user.getString("USER_ID");

        // 监务通用户不能办理亲亲网短号业务
        boolean isJWTUser = checkIsJWTUser(user.getString("USER_ID"));
        if (isJWTUser)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_67);
        }
        String discntArray ="3403,3410";
        UcaData uca = UcaDataFactory.getUcaByUserId(userId);
        List<DiscntTradeData> result = uca.getUserDiscntsByDiscntCodeArray(discntArray);
        //IDataset result = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
        if (ArrayUtil.isEmpty(result))
        {
            // 您还未开通任何亲亲网套餐,不能办理该业务！
            CSAppException.apperr(FamilyException.CRM_FAMILY_68);
        }
        DiscntTradeData userDiscnt = result.get(0);
        String discntCode = userDiscnt.getDiscntCode();
        if (!"3403".equals(discntCode)&&!"3410".equals(discntCode))
        {
            // 非主号码不能为亲亲网成员办理短号业务！
            CSAppException.apperr(FamilyException.CRM_FAMILY_69);
        }
        String userIdA = userDiscnt.getUserIdA();
        IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(virtualUser))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }

        // 主号码亲亲网套餐非永久有效，不能办理亲亲网短号业务 SEL_USER_VALID_FMYDISCNT
        IDataset userDiscntVF = UserDiscntInfoQry.getUserDiscntValidForever(userId, discntCode);
        if (IDataUtil.isEmpty(userDiscntVF))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_88);
        }

        // 是否开通家庭网服务
        IDataset userSvc = UserSvcInfoQry.getSvcUserId(userIdA, "830");
        if (IDataUtil.isEmpty(userSvc))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_87);
        }

        IDataset uuInfoList = RelaUUInfoQry.qryRelaUUByUIdAAllDBWithOrder(userIdA, "45");

        return uuInfoList;
    }

    /**
     * 亲亲网挂机提醒短信办理时的查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getAllMebList(IData input) throws Exception
    {
        IData rtData = new DataMap();
        String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        String userId = user.getString("USER_ID");
        String mainFlag = "false";
        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }

        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "45", null);
        if (IDataUtil.isEmpty(result))
        {
            // 您还未开通亲亲网业务,不能办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_89);
        }
        IData rela = result.getData(0);
        String userIdA = rela.getString("USER_ID_A");

        // 未找到虚拟用户资料
        IData vUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(vUser))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }

        // 如果不是正常用户则报错
        String removeTag = vUser.getString("REMOVE_TAG");
        if (!StringUtils.equals(removeTag, "0"))
        {
            // 亲亲网已被注销，请确认再办理
            CSAppException.apperr(FamilyException.CRM_FAMILY_98);
        }

        IDataset allRejectSvcs = UserSvcInfoQry.getSvcUserId(userIdA, "832");
        if (IDataUtil.isNotEmpty(allRejectSvcs))
        {
            // 亲亲网已存在全网拒接挂机提醒服务,不需要重复办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_104);
        }

        String roleCodeB = rela.getString("ROLE_CODE_B");
        if (roleCodeB.equals("1"))
        {
            // 主卡
            mainFlag = "true";
        }
        else
        {
            // 副卡
            mainFlag = "false";

            IDataset userSvc = UserSvcInfoQry.getSvcUserId(userId, "833");
            if (IDataUtil.isNotEmpty(userSvc))
            {
                // 亲亲网已存在成员拒接挂机提醒服务,不需要重复办理该业务
                CSAppException.apperr(FamilyException.CRM_FAMILY_105);
            }
        }

        IDataset mebList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData meb = mebList.getData(i);
            String roleCode = meb.getString("ROLE_CODE_B");
            if (StringUtils.equals(roleCode, "1"))
            {
                meb.put("ROLE_CODE_B_NAME", "主号");
            }
            else
            {
                meb.put("ROLE_CODE_B_NAME", "副号");
            }

            // 办理过拒收挂机提醒的成员不能再次办理
            String userIdB = meb.getString("USER_ID_B");
            IDataset userSvc = UserSvcInfoQry.getSvcUserId(userIdB, "833");
            if (IDataUtil.isEmpty(userSvc))
            {
                meb.put("HAVA_RR_SVC", "false");
            }
            else
            {
                meb.put("HAVA_RR_SVC", "true");
            }
        }

        rtData.put("MAIN_FLAG", mainFlag);
        rtData.put("MEB_LIST", mebList);

        return rtData;
    }

    /**
     * 拒收提醒短信查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getRejectRemindInfo(IData input) throws Exception
    {
        String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(sn);

        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }

        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(user.getString("USER_ID"), "45", null);
        if (IDataUtil.isEmpty(result))
        {
            // 您还未开通亲亲网业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_99);
        }

        IData rela = result.getData(0);
        String userIdA = rela.getString("USER_ID_A");

        IData vUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(vUser))
        {
            // 没有找到虚拟用户资料
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }

        // 查询全网拒收挂机提醒短信服务
        IDataset allRejectSvcs = UserSvcInfoQry.getSvcUserId(userIdA, "832");

        IDataset mebList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData meb = mebList.getData(i);
            String roleCode = meb.getString("ROLE_CODE_B");
            String userIdB = meb.getString("USER_ID_B");
            if (StringUtils.equals(roleCode, "1"))
            {
                meb.put("ROLE_CODE_B_NAME", "主号");
            }
            else
            {
                meb.put("ROLE_CODE_B_NAME", "副号");
            }

            if (IDataUtil.isNotEmpty(allRejectSvcs))
            {
                IData allRejectSvc = allRejectSvcs.getData(0);
                String startDate = allRejectSvc.getString("START_DATE");// 服务开始时间
                String endDate = allRejectSvc.getString("END_DATE");// 服务结束时间
                meb.put("REJECT_MODE_NANE", "全网拒收");
                meb.put("SVC_START_DATE", startDate);
                meb.put("SVC_END_DATE", endDate);
            }
            else
            {
                // 查询成员拒收挂机提醒短信服务
                IDataset mebRejectSvcs = UserSvcInfoQry.getSvcUserId(userIdB, "833");
                if (IDataUtil.isNotEmpty(mebRejectSvcs))
                {
                    IData mebRejectSvc = mebRejectSvcs.getData(0);
                    String startDate = mebRejectSvc.getString("START_DATE");// 服务开始时间
                    String endDate = mebRejectSvc.getString("END_DATE");// 服务结束时间
                    meb.put("REJECT_MODE_NANE", "成员拒收");
                    meb.put("SVC_START_DATE", startDate);
                    meb.put("SVC_END_DATE", endDate);
                }
            }
        }

        return mebList;
    }

    /**
     * 删除亲亲网成员时的查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getViceMebList(IData input) throws Exception
    {
        IData rtData = new DataMap();
        String sn = input.getString("SERIAL_NUMBER");
        IData user = UcaInfoQry.qryUserInfoBySn(sn);
        String mainFlag = "false";

        if (IDataUtil.isEmpty(user))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
        }

        String userIdB = user.getString("USER_ID");

        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userIdB, "45", null);
        if (IDataUtil.isEmpty(result))
        {
            // 您还未开通亲亲网业务,不能办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_89);
        }

        IData rela = result.getData(0);
        String userIdA = rela.getString("USER_ID_A");
        String roleCodeB = rela.getString("ROLE_CODE_B");

        IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(virtualUser))
        {
            // 没有找到虚拟用户
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }

        // 如果不是正常用户则报错
        String removeTag = virtualUser.getString("REMOVE_TAG");
        if (!StringUtils.equals(removeTag, "0"))
        {
            // 亲亲网已被注销，请确认再办理
            CSAppException.apperr(FamilyException.CRM_FAMILY_98);
        }

        if (roleCodeB.equals("1"))
        {
            // 主卡
            mainFlag = "true";
        }
        else
        {
            // 副卡
            mainFlag = "false";
        }

        // 已经删除的成员不能再次删除，通过比较成员的失效时间是否等于最大失效时间，如果是则页面能选择，如果不等则勾选置为灰
        String lastTime = SysDateMgr.getTheLastTime();// 最大失效时间
        IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData meb = mebList.getData(i);
            String mebEndDate = meb.getString("END_DATE");
            meb.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(meb.getString("DISCNT_CODE")));
            if (StringUtils.equals(lastTime, mebEndDate))
            {
                meb.put("IS_DELETE", "false");
            }
            else
            {
                meb.put("IS_DELETE", "true");
            }

            String lastDayAcct = AcctDayDateUtil.getLastDayThisAcct(meb.getString("USER_ID_B"));
            meb.put("LAST_DAY_THIS_ACCT", lastDayAcct);
        }

        rtData.put("MAIN_FLAG", mainFlag);
        rtData.put("MEB_LIST", mebList);

        return rtData;
    }

    /**
     * 是否存在对应的值
     * 
     * @param list
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public boolean isExistValue(IDataset list, String field, String value) throws Exception
    {
        IData data = null;
        String fieldValue = "";
        if (IDataUtil.isNotEmpty(list))
        {
            for (int i = 0; i < list.size(); i++)
            {
                data = list.getData(i);
                fieldValue = data.getString(field);
                if (fieldValue != null && fieldValue.equals(value))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取亲亲网一键注销界面信息
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     */
    public IData loadDestroyFamilyInfo(IData input) throws Exception
    {
        IData rtData = new DataMap();
        String userId = input.getString("USER_ID");
        String mainSn = input.getString("SERIAL_NUMBER");

        IDataset productList = UpcCall.queryProductInfosByMode("PRODUCT_MODE","05");
        rtData.put("PRODUCT_LIST", productList);
        if (null != productList && productList.size() == 1)
        {
            String productId = productList.getData(0).getString("PRODUCT_ID");
            IDataset discntList = UpcCall.queryMebOffersByTopOfferIdRole(productId, "1","D");
            rtData.put("DISCNT_LIST", discntList);// 主号优惠 pkgTypeCode=5为主卡包

            // 叠加包
            UcaData uca = UcaDataFactory.getUcaByUserId(userId);
            IDataset tmpList =UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter("P", uca.getProductId(),null, "0");// new DatasetList();//DiscntInfoQry.getDiscntByProduct(input.getString("USER_PRODUCT_ID"));
            IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", this.getUserEparchyCode());
            IDataset appDiscntList = filterByEqualsCol(tmpList, "OFFER_CODE", commList, "PARAM_CODE");
            rtData.put("APP_DISCNT_LIST", appDiscntList);
        }

        IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);
		
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);

		List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray(discntArrays);
        if (ArrayUtil.isEmpty(userDiscntList))
        {
            // 您还未开通亲亲网业务,不能办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_89);
        }

        if (userDiscntList.size() > 1)
        {
            // 用户资料有误，归属于多个亲亲网
            //CSAppException.apperr(FamilyException.CRM_FAMILY_96);
        }

        DiscntTradeData userDiscnt = userDiscntList.get(0);
        rtData.put("START_DATE", userDiscnt.getStartDate());// 开始时间
        rtData.put("END_DATE", userDiscnt.getEndDate());// 结束时间
        rtData.put("PRODUCT_ID", userDiscnt.getProductId());// 主号产品ID
        rtData.put("DISCNT_CODE", userDiscnt.getDiscntCode());// 主号优惠编码

        String userIdA = userDiscnt.getUserIdA();
        IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(virtualUser))
        {
            // 没有找到虚拟用户
            CSAppException.apperr(FamilyException.CRM_FAMILY_53);
        }
        // 如果不是正常用户则报错
        String removeTag = virtualUser.getString("REMOVE_TAG");
        if (!StringUtils.equals(removeTag, "0"))
        {
            // 亲亲网已被注销，请确认再办理
            CSAppException.apperr(FamilyException.CRM_FAMILY_98);
        }

        // 判断输入的号码是否是副号，如果是，则不能办理此业务
        IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData data = mebList.getData(i);
            String mebSn = data.getString("SERIAL_NUMBER_B");// 副号码
            data.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE")));
            if (StringUtils.equals(mainSn, mebSn))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_97);
            }
        }
        rtData.put("MEB_LIST", mebList);

        // 查询家庭客户
        IDataset result = BofQuery.queryCustFamily(virtualUser.getString("CUST_ID"), virtualUser.getString("EPARCHY_CODE"));
        if (IDataUtil.isNotEmpty(result))
        {
            IData custFamilyInfo = result.getData(0);
            rtData.put("HOME_NAME", custFamilyInfo.getString("HOME_NAME"));
            rtData.put("HOME_ADDRESS", custFamilyInfo.getString("HOME_ADDRESS"));
            rtData.put("HOME_PHONE", custFamilyInfo.getString("HOME_PHONE"));
        }

        // 查询主号短号
        IData mainRela = RelaUUInfoQry.getRelaByPK(userIdA, userId, "45");
        if (IDataUtil.isEmpty(mainRela))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_45);
        }
        rtData.put("SHORT_CODE", mainRela.getString("SHORT_CODE"));

        // 查询可选包优惠编码
        IDataset mainUserDiscnt = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
        IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", input.getString("EPARCHY_CODE"));
        IDataset mainUserAppDiscnt = IDataUtil.filterByEqualsCol(mainUserDiscnt, "DISCNT_CODE", commList, "PARAM_CODE");
        if (IDataUtil.isNotEmpty(mainUserAppDiscnt))
        {
            rtData.put("APP_DISCNT_CODE", mainUserAppDiscnt.getData(0).getString("DISCNT_CODE"));
        }

        return rtData;
    }
    
    public String getDiscntArray(IDataset datas) throws Exception
    {
    	String discnts ="";
    	if(IDataUtil.isNotEmpty(datas))
    	{
    		for(int i=0;i<datas.size();i++)
    		{
    			IData data = datas.getData(i);
    			discnts += data.getString("OFFER_CODE") +",";
    		}
    		if(StringUtils.isNotBlank(discnts))
    		{
    			discnts = discnts.substring(0, discnts.length()-1);
    		}
    	}
    	return discnts;
    }
    
    public List<DiscntTradeData> getEffectedDiscnts(List<DiscntTradeData> discnts)throws Exception
    {
    	List<DiscntTradeData> effectedDiscnts = new ArrayList<DiscntTradeData>();
    	if(ArrayUtil.isNotEmpty(discnts))
    	{
    		for(int i=0;i<discnts.size();i++)
    		{
    			DiscntTradeData userDiscnt = discnts.get(i);
    			String sysdate =SysDateMgr.getSysDate();
    			if(sysdate.compareTo(userDiscnt.getStartDate())>=0 && sysdate.compareTo(userDiscnt.getEndDate())<0)
    			{
    				effectedDiscnts.add(userDiscnt);
    			}
    		}
    	}
    	return effectedDiscnts;
    }
    
    /**
	 * 获取亲亲网套餐升级界面信息
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 * @author yanwu
	 */
	public IData loadUpdateFamilyInfo(IData input) throws Exception {
		IData rtData = new DataMap();
		String userId = input.getString("USER_ID");
		String mainSn = input.getString("SERIAL_NUMBER");

		//IDataset productList = ProductInfoQry.getProductsBySelByPModeBrand(
				//"VPCN", "05", this.getUserEparchyCode());
		IDataset productList = UpcCall.queryProductInfosByMode("PRODUCT_MODE","05");
		rtData.put("PRODUCT_LIST", productList);
		if (null != productList && productList.size() == 1) {
			String productId = productList.getData(0).getString("PRODUCT_ID");
			IDataset discntList =  UpcCall.queryMebOffersByTopOfferIdRole(productId, "1","D");
			rtData.put("DISCNT_LIST", discntList);// 主号优惠 pkgTypeCode=5为主卡包

			// 叠加包
			UcaData uca = UcaDataFactory.getUcaByUserId(userId);
            IDataset tmpList =UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter("P", uca.getProductId(),null, "0");// new DatasetList();//DiscntInfoQry.getDiscntByProduct(input.getString("USER_PRODUCT_ID"));
			IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009",
					this.getUserEparchyCode());
			IDataset appDiscntList = filterByEqualsCol(tmpList,
					"OFFER_CODE", commList, "PARAM_CODE");
			rtData.put("APP_DISCNT_LIST", appDiscntList);
		}
		IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);
		
		UcaData uca = UcaDataFactory.getUcaByUserId(userId);

		List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray(discntArrays);
		List<DiscntTradeData> userDiscnts = this.getEffectedDiscnts(userDiscntList);
		if (ArrayUtil.isEmpty(userDiscnts)) {
			// 您还未开通亲亲网业务,不能办理该业务
			CSAppException.apperr(FamilyException.CRM_FAMILY_89);
		}

		if (userDiscnts.size() > 1) {
			// 用户资料有误，归属于多个亲亲网
			CSAppException.apperr(FamilyException.CRM_FAMILY_96);
		}

		DiscntTradeData userDiscnt = userDiscnts.get(0);
		rtData.put("START_DATE", userDiscnt.getStartDate());// 开始时间
		rtData.put("END_DATE", userDiscnt.getEndDate());// 结束时间
		rtData.put("PRODUCT_ID", userDiscnt.getProductId());// 主号产品ID
		rtData.put("DISCNT_CODE", userDiscnt.getDiscntCode());// 主号优惠编码

		String userIdA = userDiscnt.getUserIdA();
		IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
		if (IDataUtil.isEmpty(virtualUser)) {
			// 没有找到虚拟用户
			CSAppException.apperr(FamilyException.CRM_FAMILY_53);
		}
		// 如果不是正常用户则报错
		String removeTag = virtualUser.getString("REMOVE_TAG");
		if (!StringUtils.equals(removeTag, "0")) {
			// 亲亲网已被注销，请确认再办理
			CSAppException.apperr(FamilyException.CRM_FAMILY_98);
		}

		// 判断输入的号码是否是副号，如果是，则不能办理此业务
		IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);
		for (int i = 0, size = mebList.size(); i < size; i++) {
			IData data = mebList.getData(i);
			String mebSn = data.getString("SERIAL_NUMBER_B");// 副号码
			data.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE")));
			if (StringUtils.equals(mainSn, mebSn)) {
				CSAppException.apperr(FamilyException.CRM_FAMILY_107);
			}
		}
		rtData.put("MEB_LIST", mebList);

		// 查询家庭客户
		IDataset result = BofQuery.queryCustFamily(virtualUser
				.getString("CUST_ID"), virtualUser.getString("EPARCHY_CODE"));
		if (IDataUtil.isNotEmpty(result)) {
			IData custFamilyInfo = result.getData(0);
			rtData.put("HOME_NAME", custFamilyInfo.getString("HOME_NAME"));
			rtData
					.put("HOME_ADDRESS", custFamilyInfo
							.getString("HOME_ADDRESS"));
			rtData.put("HOME_PHONE", custFamilyInfo.getString("HOME_PHONE"));
		}

		// 查询主号短号
		IData mainRela = RelaUUInfoQry.getRelaByPK(userIdA, userId, "45");
		if (IDataUtil.isEmpty(mainRela)) {
			CSAppException.apperr(FamilyException.CRM_FAMILY_45);
		}
		rtData.put("SHORT_CODE", mainRela.getString("SHORT_CODE"));

				//REQ201505120004 亲亲网升级界面优化 Modify @yanwu begin
		// 您的亲亲网已经升级过了,不需要重复办理该业务！
		//IDataset userDiscnts = UserDiscntInfoQry.getDiscntsByPModeUpdate01(userId, "05");
		
		if (ArrayUtil.isNotEmpty(userDiscnts)) {
			for (int i = 0, size = userDiscnts.size(); i < size; i++) {
				DiscntTradeData data = userDiscnts.get(i);
				String mebdc = data.getDiscntCode(); // 获取优惠
				if ("3410".equals(mebdc)) { // 如果等于升级优惠，则报错
					CSAppException.apperr(FamilyException.CRM_FAMILY_106);
				}
			}
		}
		//REQ201505120004 亲亲网升级界面优化 Modify @yanwu end
		
		// 您的亲亲网成员超出10个，不能升级亲亲网，请删除成员！
		IDataset Goods = RelaUUInfoQry.getRelaByGood(userIdA, "", "45");
		if (Goods.size() > 10 && IDataUtil.isNotEmpty(Goods)) {
			CSAppException.apperr(FamilyException.CRM_FAMILY_108);
		}

		// 查询可选包优惠编码
		IDataset mainUserDiscnt = UserDiscntInfoQry
				.getAllValidDiscntByUserId(userId);
		IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", input
				.getString("EPARCHY_CODE"));
		IDataset mainUserAppDiscnt = IDataUtil.filterByEqualsCol(
				mainUserDiscnt, "DISCNT_CODE", commList, "PARAM_CODE");
		if (IDataUtil.isNotEmpty(mainUserAppDiscnt)) {
			rtData.put("APP_DISCNT_CODE", mainUserAppDiscnt.getData(0)
					.getString("DISCNT_CODE"));
		}

		return rtData;
	}
	
	public String getFamilyDiscntArray(IData param)throws Exception
	{
		String discntArray ="";
		if(IDataUtil.isNotEmpty(param))
		{
			IDataset mainDiscnts = param.getDataset("DISCNT_LIST");
			if(IDataUtil.isNotEmpty(mainDiscnts))
			{
				for(int i=0;i<mainDiscnts.size();i++)
				{
					IData temp = mainDiscnts.getData(i);
					String discntCode = temp.getString("DISCNT_CODE");
					if(StringUtils.isNotBlank(discntCode))
					{
						discntArray += StringUtils.trim(discntCode) +",";
					}	
				}
			}
			IDataset viceDiscnts = param.getDataset("VICE_DISCNT_LIST");
			if(IDataUtil.isNotEmpty(viceDiscnts))
			{
				for(int i=0;i<viceDiscnts.size();i++)
				{
					IData temp = viceDiscnts.getData(i);
					String discntCode = temp.getString("DISCNT_CODE");
					if(StringUtils.isNotBlank(discntCode))
					{
						discntArray +=StringUtils.trim(discntCode) +",";
					}	
				}
			}
			if(StringUtils.isNotBlank(discntArray))
			{
				discntArray = StringUtils.substring(discntArray, 0, discntArray.length()-1);
				return discntArray;
			}
		}
		return discntArray;
	}

    /**
     * 亲亲网建家界面展示信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData loadFamilyCreateInfo(IData input) throws Exception
    {
        IData rtData = new DataMap();
        String userId = input.getString("USER_ID");
        String mainSn = input.getString("SERIAL_NUMBER");

        //IDataset productList = ProductInfoQry.getProductsBySelByPModeBrand("VPCN", "05", this.getUserEparchyCode());
        IDataset productList = UpcCall.queryProductInfosByMode("PRODUCT_MODE","05");
        rtData.put("PRODUCT_LIST", productList);
        if (null != productList && productList.size() == 1)
        {
            String productId = productList.getData(0).getString("PRODUCT_ID");
            // rtData.put("PRODUCT_ID", productId);//新加 by zhouwu
            //IDataset discntList = DiscntInfoQry.qryByPidPkgTypeCode(productId, "5", this.getUserEparchyCode());
            IDataset discntList = UpcCall.queryMebOffersByTopOfferIdRole(productId, "1","D");
            rtData.put("DISCNT_LIST", discntList);// 主号优惠 pkgTypeCode=5为主卡包

            //IDataset viceDiscntList = DiscntInfoQry.qryByPidPkgTypeCode(productId, "6", this.getUserEparchyCode());
            IDataset viceDiscntList = UpcCall.queryMebOffersByTopOfferIdRole(productId, "2","D");
            rtData.put("VICE_DISCNT_LIST", viceDiscntList);// 副号优惠 pkgTypeCode=6为副卡包

            // 对旧优惠的处理
			isOldAddMember(mainSn, userId, discntList, viceDiscntList);
            
            // 叠加包
			UcaData uca = UcaDataFactory.getUcaByUserId(userId);
			if("2".equals(uca.getUser().getAcctTag()))
			{
				// 您好，此号码为待激活用户，请给待号码激活后方可继续办理。
                CSAppException.apperr(FamilyException.CRM_FAMILY_538);
			}
			if(!"1".equals(uca.getCustomer().getIsRealName()))
			{
				// 您好，此号码为非实名用户，请补录实名信息后方可继续办理。
                CSAppException.apperr(FamilyException.CRM_FAMILY_539);
			}
            IDataset tmpList =UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter("P", uca.getProductId(),null, "0");// new DatasetList();//DiscntInfoQry.getDiscntByProduct(input.getString("USER_PRODUCT_ID"));
            IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", this.getUserEparchyCode());
            IDataset appDiscntList = filterByEqualsCol(tmpList, "OFFER_CODE", commList, "PARAM_CODE");
            rtData.put("APP_DISCNT_LIST", appDiscntList);
        }

        // 此处是根据user_id和产品模式查询用户服务资料，老系统是根据user_id查询用户优惠资料进行逻辑判断
        IDataset userSvcList =UserSvcInfoQry.getSvcUserId(userId, "831");//getByPMode(userId, "05");
        if (IDataUtil.isEmpty(userSvcList))
        {
        	IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "45", null);
        	if (IDataUtil.isNotEmpty(result))
        	{
        		IData rela = result.getData(0);
                String roleCodeB = rela.getString("ROLE_CODE_B");
                String endDate = rela.getString("END_DATE");
                String lastTime = SysDateMgr.getTheLastTime();// 最大失效时间
                
                String userIDA = rela.getString("USER_ID_A");
                if (StringUtils.equals(roleCodeB, "2"))
                {
                	IDataset relamian = RelaUUInfoQry.getRelationsByUserIdA("45", userIDA, "1");
                	if( IDataUtil.isNotEmpty(relamian) ){
                		IData relamain = relamian.getData(0);
                		String strSn = relamain.getString("SERIAL_NUMBER_B");
                		CSAppException.apperr(FamilyException.CRM_FAMILY_60, strSn);
                	}else{
                		CSAppException.apperr(FamilyException.CRM_FAMILY_60, "");
                	}
				}
                else
                {
                	if (!StringUtils.equals(lastTime, endDate))
                    {
                		String discntArray = this.getFamilyDiscntArray(rtData);
                		if(StringUtils.isNotBlank(discntArray))
                		{
                			UcaData uca = UcaDataFactory.getUcaByUserId(userId);
                			List<DiscntTradeData> userDiscnts = uca.getUserDiscntsByDiscntCodeArray(discntArray);
                    		//IDataset userDiscnts = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
                            if (ArrayUtil.isNotEmpty(userDiscnts))
                            {
                                // 您为原亲亲网用户，还未失效，请在下账期再来办理此业务
                                CSAppException.apperr(FamilyException.CRM_FAMILY_101);
                            }
                		}
                    }
                }
			}
        	
            rtData.put("START_DATE", SysDateMgr.getSysTime());
            rtData.put("END_DATE", SysDateMgr.getTheLastTime());
        }
        else
        {
            IData userSvc = userSvcList.getData(0);
            rtData.put("START_DATE", userSvc.getString("START_DATE"));
            rtData.put("END_DATE", userSvc.getString("END_DATE"));
            rtData.put("PRODUCT_ID", "99000001");
            rtData.put("SERVICE_ID", userSvc.getString("SERVICE_ID"));

            String userIdA = userSvc.getString("USER_ID_A");

            // 在这之前需要判断输入的号码是否是副号，如果是，则不能办理此业务 zhouwu 2014/5/27
            IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);
            for (int i = 0, size = mebList.size(); i < size; i++)
            {
                IData data = mebList.getData(i);
                String mebSn = data.getString("SERIAL_NUMBER_B");// 副号码
                data.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE")));
                String userIDA = data.getString("USER_ID_A");
                if (StringUtils.equals(mainSn, mebSn))
                {
                	IDataset relamian = RelaUUInfoQry.getRelationsByUserIdA("45", userIDA, "1");
                	if( IDataUtil.isNotEmpty(relamian) ){
                		IData relamain = relamian.getData(0);
                		String strSn = relamain.getString("SERIAL_NUMBER_B");
                		CSAppException.apperr(FamilyException.CRM_FAMILY_60, strSn);
                	}else{
                		CSAppException.apperr(FamilyException.CRM_FAMILY_60, "");
                	}
                }

                String userIdB = data.getString("USER_ID_B");
                IDataset appDiscntList = UserDiscntInfoQry.queryUserDiscntByParamattr(userIdB, "1009");
                if (IDataUtil.isNotEmpty(appDiscntList))
                {
                    IData appDiscnt = appDiscntList.getData(0);
                    String appDiscntCode = appDiscnt.getString("DISCNT_CODE");
                    String appDiscntName = UDiscntInfoQry.getDiscntNameByDiscntCode(appDiscntCode);
                    data.put("APP_DISCNT_CODE_B", appDiscntCode);
                    data.put("APP_DISCNT_NAME_B", appDiscntName);
                }
            }
            rtData.put("MEB_LIST", mebList);

            IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (IDataUtil.isEmpty(virtualUser))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_53);
            }
            IDataset result = BofQuery.queryCustFamily(virtualUser.getString("CUST_ID"), virtualUser.getString("EPARCHY_CODE"));
            if (IDataUtil.isEmpty(result))
            {
                // CSAppException.apperr(FamilyException.CRM_FAMILY_27);
            }
            else
            {
                IData custFamilyInfo = result.getData(0);
                rtData.put("HOME_NAME", custFamilyInfo.getString("HOME_NAME"));
                rtData.put("HOME_ADDRESS", custFamilyInfo.getString("HOME_ADDRESS"));
                rtData.put("HOME_PHONE", custFamilyInfo.getString("HOME_PHONE"));
            }

            IDataset userMainDiscnt = UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, userIdA);
            if (IDataUtil.isNotEmpty(userMainDiscnt))
            {
                rtData.put("DISCNT_CODE", userMainDiscnt.getData(0).getString("DISCNT_CODE"));
                //放开限制
                /*String sysNow = SysDateMgr.getSysTime();
				for (int i = 0, size = userMainDiscnt.size(); i < size; i++) {
					IData data = userMainDiscnt.getData(i);
					String strDC = data.getString("DISCNT_CODE");
					String strSd = data.getString("START_DATE");
					if ("3410".equals(strDC)) {
						int nCount = sysNow.compareTo(strSd);
						if (nCount < 0) {
							// 您的亲亲网升级优惠未生效，还不能增加成员，升级优惠生效时间[%s]！
							CSAppException.apperr(
									FamilyException.CRM_FAMILY_109, strSd);
						}
					}
				}*/
            }

            IData mainRela = RelaUUInfoQry.getRelaByPK(userIdA, userId, "45");
            if (IDataUtil.isEmpty(mainRela))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_45);
            }
            rtData.put("SHORT_CODE", mainRela.getString("SHORT_CODE"));

            IDataset mainUserDiscnt = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
            IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", input.getString("EPARCHY_CODE"));
            IDataset mainUserAppDiscnt = IDataUtil.filterByEqualsCol(mainUserDiscnt, "DISCNT_CODE", commList, "PARAM_CODE");
            if (IDataUtil.isNotEmpty(mainUserAppDiscnt))
            {
                rtData.put("APP_DISCNT_CODE", mainUserAppDiscnt.getData(0).getString("DISCNT_CODE"));
            }
            rtData.put("APP_DISCNT_CODE", null);
        }

        // 监务通用户不能办理短号
        boolean isJWTUser = checkIsJWTUser(userId);
        rtData.put("ISJWT_FLAG", isJWTUser);

        return rtData;
    }
    
    public void isOldAddMember(String mainSn, String userId,
			IDataset discntList, IDataset viceDiscntList) throws Exception {
		// 查询组建或新增成员的号码，是否主卡，然后进一步判断，是旧的亲亲网还是新的亲亲网
		boolean flag = false;// true:旧亲亲网主号新增成员
		IDataset relaList = RelaUUInfoQry
				.getRelationsByUserIdAndTypeAndRoleCodeB("45", userId, "1");
		if (IDataUtil.isNotEmpty(relaList))// 新增成员
		{
			UcaData mebUca = UcaDataFactory.getNormalUca(mainSn);
			// 处理优惠
			List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
			for (int j = 0; j < userDiscntList.size(); j++) {
				DiscntTradeData userDiscnt = userDiscntList.get(j);
				if ("3403".equals(userDiscnt.getDiscntCode())) {
					flag = true;
					break;
				}
			}
		}

		IDataset tmpist1 = new DatasetList();
		tmpist1.addAll(discntList);
		IDataset tmpist2 = new DatasetList();
		tmpist2.addAll(viceDiscntList);
		if (flag) {// 旧亲亲网主号新增成员，
			for (Object a : tmpist1) {
				IData data = (IData) a;
				if (!"3403".equals(data.getString("DISCNT_CODE"))) {
					discntList.remove(a);
				}
			}
			for (Object a : tmpist2) {
				IData data = (IData) a;
				if (!"3404".equals(data.getString("DISCNT_CODE"))) {
					viceDiscntList.remove(a);
				}
			}
		} else {
			for (Object a : tmpist1) {
				IData data = (IData) a;
				if ("3403".equals(data.getString("DISCNT_CODE"))) {
					discntList.remove(a);
				}
			}
			for (Object a : tmpist2) {
				IData data = (IData) a;
				if ("3404".equals(data.getString("DISCNT_CODE"))) {
					viceDiscntList.remove(a);
				}
			}
		}
	}
    
    public static IDataset filterByEqualsCol(IDataset oriList, String col, IDataset compareList, String compareCol) throws Exception
    {
        IDataset rtList = new DatasetList();
        for (int i = 0, size = oriList.size(); i < size; i++)
        {
            IData oriData = oriList.getData(i);
            String value = oriData.getString(col);
            if (StringUtils.isBlank(value))
            {
                continue;
            }
            for (int j = 0, size2 = compareList.size(); j < size2; j++)
            {
                IData compareData = compareList.getData(j);
                String compareValue = compareData.getString(compareCol);
                if (value.equals(compareValue))
                {
                	oriData.put("DISCNT_NAME", oriData.getString("OFFER_NAME"));
                	oriData.put("DISCNT_CODE", oriData.getString("OFFER_CODE"));
                    rtList.add(oriData);
                }
            }
        }

        return rtList;
    }

    /**
     * @Description 海南界面互联网项目-亲亲网业务办理（新）界面展示信息
     * @Author zhaohj3
     * @Date 2018/8/7 下午 8:44
     * @param input
     * @return rtData
     */
    public IData loadFamilyCreateInfoNew(IData input) throws Exception
    {
        IData rtData = new DataMap();
        String userId = input.getString("USER_ID");
        String mainSn = input.getString("SERIAL_NUMBER");

        IDataset mainRelationSet = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("45", userId, "1");
        if (IDataUtil.isNotEmpty(mainRelationSet)) {
            rtData.put("MAIN_LIST", mainRelationSet);
        }

        //IDataset productList = ProductInfoQry.getProductsBySelByPModeBrand("VPCN", "05", this.getUserEparchyCode());
        IDataset productList = UpcCall.queryProductInfosByMode("PRODUCT_MODE","05");
        rtData.put("PRODUCT_LIST", productList);
        if (null != productList && productList.size() == 1)
        {
            String productId = productList.getData(0).getString("PRODUCT_ID");
            // rtData.put("PRODUCT_ID", productId);//新加 by zhouwu
            //IDataset discntList = DiscntInfoQry.qryByPidPkgTypeCode(productId, "5", this.getUserEparchyCode());
            IDataset discntList = UpcCall.queryMebOffersByTopOfferIdRole(productId, "1","D");
            rtData.put("DISCNT_LIST", discntList);// 主号优惠 pkgTypeCode=5为主卡包

            //IDataset viceDiscntList = DiscntInfoQry.qryByPidPkgTypeCode(productId, "6", this.getUserEparchyCode());
            IDataset viceDiscntList = UpcCall.queryMebOffersByTopOfferIdRole(productId, "2","D");
            rtData.put("VICE_DISCNT_LIST", viceDiscntList);// 副号优惠 pkgTypeCode=6为副卡包

            // 对旧优惠的处理
            isOldAddMember(mainSn, userId, discntList, viceDiscntList);

            // 叠加包
            UcaData uca = UcaDataFactory.getUcaByUserId(userId);
            if("2".equals(uca.getUser().getAcctTag()))
            {
                // 您好，此号码为待激活用户，请给待号码激活后方可继续办理。
                CSAppException.apperr(FamilyException.CRM_FAMILY_538);
            }
            if(!"1".equals(uca.getCustomer().getIsRealName()))
            {
                // 您好，此号码为非实名用户，请补录实名信息后方可继续办理。
                CSAppException.apperr(FamilyException.CRM_FAMILY_539);
            }
            IDataset tmpList =UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter("P", uca.getProductId(),null, "0");// new DatasetList();//DiscntInfoQry.getDiscntByProduct(input.getString("USER_PRODUCT_ID"));
            IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", this.getUserEparchyCode());
            IDataset appDiscntList = filterByEqualsCol(tmpList, "OFFER_CODE", commList, "PARAM_CODE");
            rtData.put("APP_DISCNT_LIST", appDiscntList);
        }

        // 此处是根据user_id和产品模式查询用户服务资料，老系统是根据user_id查询用户优惠资料进行逻辑判断
        IDataset userSvcList =UserSvcInfoQry.getSvcUserId(userId, "831");//getByPMode(userId, "05");
        if (IDataUtil.isEmpty(userSvcList))
        {
            IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "45", null);
            if (IDataUtil.isNotEmpty(result))
            {
                IData rela = result.getData(0);
                String roleCodeB = rela.getString("ROLE_CODE_B");
                String endDate = rela.getString("END_DATE");
                String lastTime = SysDateMgr.getTheLastTime();// 最大失效时间

                String userIDA = rela.getString("USER_ID_A");
                if (StringUtils.equals(roleCodeB, "2"))
                {
                    IDataset relamian = RelaUUInfoQry.getRelationsByUserIdA("45", userIDA, "1");
                    if( IDataUtil.isNotEmpty(relamian) ){
                        IData relamain = relamian.getData(0);
                        String strSn = relamain.getString("SERIAL_NUMBER_B");
                        rtData.put("MAIN_SERIAL_NUMBER", strSn);
                        rtData.putAll(getViceMebList(input));
                        return rtData;
//                		CSAppException.apperr(FamilyException.CRM_FAMILY_60, strSn);
                    }else{
                        CSAppException.apperr(FamilyException.CRM_FAMILY_60, "");
                    }
                }
                else
                {
                    if (!StringUtils.equals(lastTime, endDate))
                    {
                        String discntArray = this.getFamilyDiscntArray(rtData);
                        if(StringUtils.isNotBlank(discntArray))
                        {
                            UcaData uca = UcaDataFactory.getUcaByUserId(userId);
                            List<DiscntTradeData> userDiscnts = uca.getUserDiscntsByDiscntCodeArray(discntArray);
                            //IDataset userDiscnts = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
                            if (ArrayUtil.isNotEmpty(userDiscnts))
                            {
                                // 您为原亲亲网用户，还未失效，请在下账期再来办理此业务
                                CSAppException.apperr(FamilyException.CRM_FAMILY_101);
                            }
                        }
                    }
                }
            }

            rtData.put("START_DATE", SysDateMgr.getSysTime());
            rtData.put("END_DATE", SysDateMgr.getTheLastTime());
        }
        else
        {
            IData userSvc = userSvcList.getData(0);
            rtData.put("START_DATE", userSvc.getString("START_DATE"));
            rtData.put("END_DATE", userSvc.getString("END_DATE"));
            rtData.put("PRODUCT_ID", "99000001");
            rtData.put("SERVICE_ID", userSvc.getString("SERVICE_ID"));

            String userIdA = userSvc.getString("USER_ID_A");

            // 在这之前需要判断输入的号码是否是副号，如果是，则不能办理此业务 zhouwu 2014/5/27
            IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);
            for (int i = 0, size = mebList.size(); i < size; i++)
            {
                IData data = mebList.getData(i);
                String mebSn = data.getString("SERIAL_NUMBER_B");// 副号码
                data.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE")));
                String userIDA = data.getString("USER_ID_A");
                if (StringUtils.equals(mainSn, mebSn))
                {
                    IDataset relamian = RelaUUInfoQry.getRelationsByUserIdA("45", userIDA, "1");
                    if( IDataUtil.isNotEmpty(relamian) ){
                        IData relamain = relamian.getData(0);
                        String strSn = relamain.getString("SERIAL_NUMBER_B");
                        rtData.putAll(getViceMebList(input));
                        rtData.put("MAIN_SERIAL_NUMBER", strSn);
                        return rtData;
//                		CSAppException.apperr(FamilyException.CRM_FAMILY_60, strSn);
                    }else{
                        CSAppException.apperr(FamilyException.CRM_FAMILY_60, "");
                    }
                }

                String userIdB = data.getString("USER_ID_B");
                IDataset appDiscntList = UserDiscntInfoQry.queryUserDiscntByParamattr(userIdB, "1009");
                if (IDataUtil.isNotEmpty(appDiscntList))
                {
                    IData appDiscnt = appDiscntList.getData(0);
                    String appDiscntCode = appDiscnt.getString("DISCNT_CODE");
                    String appDiscntName = UDiscntInfoQry.getDiscntNameByDiscntCode(appDiscntCode);
                    data.put("APP_DISCNT_CODE_B", appDiscntCode);
                    data.put("APP_DISCNT_NAME_B", appDiscntName);
                }
            }
            rtData.put("MEB_LIST", mebList);

            IData virtualUser = UcaInfoQry.qryUserInfoByUserId(userIdA);
            if (IDataUtil.isEmpty(virtualUser))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_53);
            }
            IDataset result = BofQuery.queryCustFamily(virtualUser.getString("CUST_ID"), virtualUser.getString("EPARCHY_CODE"));
            if (IDataUtil.isEmpty(result))
            {
                // CSAppException.apperr(FamilyException.CRM_FAMILY_27);
            }
            else
            {
                IData custFamilyInfo = result.getData(0);
                rtData.put("HOME_NAME", custFamilyInfo.getString("HOME_NAME"));
                rtData.put("HOME_ADDRESS", custFamilyInfo.getString("HOME_ADDRESS"));
                rtData.put("HOME_PHONE", custFamilyInfo.getString("HOME_PHONE"));
            }

            IDataset userMainDiscnt = UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, userIdA);
            if (IDataUtil.isNotEmpty(userMainDiscnt))
            {
                rtData.put("DISCNT_CODE", userMainDiscnt.getData(0).getString("DISCNT_CODE"));
                //放开限制
                /*String sysNow = SysDateMgr.getSysTime();
				for (int i = 0, size = userMainDiscnt.size(); i < size; i++) {
					IData data = userMainDiscnt.getData(i);
					String strDC = data.getString("DISCNT_CODE");
					String strSd = data.getString("START_DATE");
					if ("3410".equals(strDC)) {
						int nCount = sysNow.compareTo(strSd);
						if (nCount < 0) {
							// 您的亲亲网升级优惠未生效，还不能增加成员，升级优惠生效时间[%s]！
							CSAppException.apperr(
									FamilyException.CRM_FAMILY_109, strSd);
						}
					}
				}*/
            }

            IData mainRela = RelaUUInfoQry.getRelaByPK(userIdA, userId, "45");
            if (IDataUtil.isEmpty(mainRela))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_45);
            }
            rtData.put("SHORT_CODE", mainRela.getString("SHORT_CODE"));

            IDataset mainUserDiscnt = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
            IDataset commList = CommparaInfoQry.getOnlyByAttr("CSM", "1009", input.getString("EPARCHY_CODE"));
            IDataset mainUserAppDiscnt = IDataUtil.filterByEqualsCol(mainUserDiscnt, "DISCNT_CODE", commList, "PARAM_CODE");
            if (IDataUtil.isNotEmpty(mainUserAppDiscnt))
            {
                rtData.put("APP_DISCNT_CODE", mainUserAppDiscnt.getData(0).getString("DISCNT_CODE"));
            }
            rtData.put("APP_DISCNT_CODE", null);
        }

        // 监务通用户不能办理短号
        boolean isJWTUser = checkIsJWTUser(userId);
        rtData.put("ISJWT_FLAG", isJWTUser);

        return rtData;
    }

    /**
     * 校验入参
     * REQ201912260028取消亲亲网副号码添加确认规则的需求
     * 取消二次确认短信
     * @param input
     * @throws Exception
     */
    public IData checkInDataNew(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset memberList = new DatasetList(input.getString("MEB_LIST"));// 直接转为Dataset就是memList ？？？？？？？？？？



        if (StringUtils.isBlank(serialNumber))
        {
            // SERIAL_NUMBER为空
            CSAppException.apperr(FamilyException.CRM_FAMILY_70);
        }

        // 校验服务号码是否有效
        IData user = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(user))
        {
            // SERIAL_NUMBER无效
            CSAppException.apperr(FamilyException.CRM_FAMILY_78, serialNumber);
        }

        IData param = new DataMap();
        String userId = user.getString("USER_ID");
        String userProductId = user.getString("PRODUCT_ID");
        String eparchyCode = user.getString("EPARCHY_CODE");
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", userId);
        param.put("USER_PRODUCT_ID", userProductId);
        param.put("EPARCHY_CODE", eparchyCode);

        IData pageInfo = loadFamilyCreateInfo(param);
        pageInfo.put("SERIAL_NUMBER", serialNumber);

        String serviceId = pageInfo.getString("SERVICE_ID", "");
        if (StringUtils.isBlank(serviceId))
        {// 组建亲亲网
            // 产品的处理
            String inProductId = input.getString("PRODUCT_ID");// 入参：产品ID
            IDataset productList = pageInfo.getDataset("PRODUCT_LIST");
            if (StringUtils.isNotBlank(inProductId))
            {
                boolean isExistProduct = isExistValue(productList, "PRODUCT_ID", inProductId);
                if (!isExistProduct)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_710, inProductId);// 产品ID[%d]无效
                pageInfo.put("PRODUCT_ID", inProductId);
            }
            else
            {
                if (IDataUtil.isNotEmpty(productList) && productList.size() == 1)
                {
                    String tempProductId = productList.getData(0).getString("PRODUCT_ID", "");
                    pageInfo.put("PRODUCT_ID", tempProductId);
                }
                else if (IDataUtil.isNotEmpty(productList) && productList.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_711);// PRODUCT_ID不能为空
                }
            }

            // 优惠的处理
            String inDiscntCode = input.getString("DISCNT_CODE");
            IDataset discntList = pageInfo.getDataset("DISCNT_LIST");
            if (StringUtils.isNotBlank(inDiscntCode))
            {
                boolean isExistDiscnt = isExistValue(discntList, "DISCNT_CODE", inDiscntCode);
                if (!isExistDiscnt)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, inDiscntCode);// 优惠[%d]无效
                pageInfo.put("DISCNT_CODE", inDiscntCode);
            }
            else
            {
                if (IDataUtil.isNotEmpty(discntList) && discntList.size() == 1)
                {
                    String tempDiscntCode = discntList.getData(0).getString("DISCNT_CODE", "");
                    pageInfo.put("DISCNT_CODE", tempDiscntCode);
                }
                else if (IDataUtil.isNotEmpty(discntList) && discntList.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_713);// DISCNT_CODE不能为空
                }
            }

            // 叠加优惠的处理
            String inAppDiscntCode = input.getString("APP_DISCNT_CODE");
            IDataset appDiscntList = pageInfo.getDataset("APP_DISCNT_LIST");
            if (StringUtils.isNotBlank(inAppDiscntCode))
            {
                boolean isExistAppDiscnt = isExistValue(appDiscntList, "DISCNT_CODE", inAppDiscntCode);
                if (!isExistAppDiscnt)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, inAppDiscntCode);// 优惠[%d]无效
                pageInfo.put("APP_DISCNT_CODE", inAppDiscntCode);
            }
            else
            { // 叠加优惠为可选包 不是必选 暂时屏蔽 老系统必选
                pageInfo.put("APP_DISCNT_CODE", "");
                /*
                 * if (IDataUtil.isNotEmpty(appDiscntList) && appDiscntList.size() == 1) { String tempAppDiscntCode =
                 * appDiscntList.getData(0).getString("DISCNT_CODE", ""); pageInfo.put("APP_DISCNT_CODE",
                 * tempAppDiscntCode); } else if (IDataUtil.isNotEmpty(appDiscntList) && appDiscntList.size() > 1) {
                 * CSAppException.apperr(FamilyException.CRM_FAMILY_714);//APP_DISCNT_CODE不能为空 }
                 */
            }

            // 短号的处理
            String shortCode = input.getString("SHORT_CODE", "");
            boolean isJwtUser = pageInfo.getBoolean("ISJWT_FLAG");
            if (!isJwtUser && StringUtils.equals(shortCode, ""))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_715);// 主卡的短号不能为空
            }
            if (isJwtUser && !StringUtils.equals(shortCode, ""))
            {
                shortCode = "";
            }
            if (!isJwtUser)
            {
                checkShortCode(shortCode);
            }
            pageInfo.put("SHORT_CODE", shortCode);
        }

        // 成员的处理
        for (int i = 0, size = memberList.size(); i < size; i++)
        {
            IData member = memberList.getData(i);
            String serialNumberB = member.getString("SERIAL_NUMBER_B");
            String shortCodeB = member.getString("SHORT_CODE_B");

            if (StringUtils.isBlank(serialNumberB))
            {
                if (size != 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_72);// SERIAL_NUMBER_B为空
                }
                if (StringUtils.isNotBlank(serviceId))
                {// 组建亲亲网
                    // 您已经存在亲亲网，本次创建失败
                    CSAppException.apperr(FamilyException.CRM_FAMILY_741);
                }
                continue;
            }

            IData userB = UcaInfoQry.qryUserInfoBySn(serialNumberB);
            if (IDataUtil.isEmpty(userB))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberB);
            }

            member.put("SERIAL_NUMBER", serialNumber);
            checkAddMeb(member);


            // 成员短号的处理
            String userIdB = userB.getString("USER_ID");
            boolean isJwtUser = checkIsJWTUser(userIdB);
            if (!isJwtUser && StringUtils.isBlank(shortCodeB))
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_718, serialNumberB);// 副卡的短号不能为空
            }
            if (isJwtUser && StringUtils.isNotBlank(shortCodeB))
            {
                shortCodeB = "";
            }
            if (!isJwtUser)
            {
                checkShortCode(shortCodeB);
            }
            member.put("SHORT_CODE_B", shortCodeB);

            // 副卡优惠的处理
            String discntCodeB = member.getString("DISCNT_CODE_B");
            IDataset discntListB = pageInfo.getDataset("VICE_DISCNT_LIST");
            if (StringUtils.isNotBlank(discntCodeB))
            {
                boolean isExistDiscntCode = isExistValue(discntListB, "DISCNT_CODE", discntCodeB);
                if (!isExistDiscntCode)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, discntCodeB);// 副卡优惠无效
            }
            else
            {
                if (IDataUtil.isNotEmpty(discntListB) && discntListB.size() == 1)
                {
                    String tempDiscntCodeB = discntListB.getData(0).getString("DISCNT_CODE", "");
                    member.put("DISCNT_CODE_B", tempDiscntCodeB);
                }
                else if (IDataUtil.isNotEmpty(discntListB) && discntListB.size() > 1)
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_719);// DISCNT_CODE_B不能为空
                }
            }

            // 副卡叠加优惠的处理
            String appDiscntCodeB = member.getString("APP_DISCNT_CODE_B");
            IDataset appDiscntListB = pageInfo.getDataset("APP_DISCNT_LIST");
            if (StringUtils.isNotBlank(appDiscntCodeB))
            {
                boolean isExistAppDiscntCodeB = isExistValue(appDiscntListB, "DISCNT_CODE", appDiscntCodeB);
                if (!isExistAppDiscntCodeB)
                    CSAppException.apperr(FamilyException.CRM_FAMILY_712, appDiscntCodeB);// 副卡叠加优惠无效
            }

            member.put("tag", "0");// 表示新增成员
        }

        pageInfo.put("MEB_LIST", memberList);// 成员列表
        pageInfo.put("FMY_VERIFY_MODE", "9");// 副号校验方式：接口传9

        return pageInfo;
    }
}
