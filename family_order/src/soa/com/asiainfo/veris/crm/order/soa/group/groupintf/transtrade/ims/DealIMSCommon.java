
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.group.common.query.GrpImsInfoQuery;

public class DealIMSCommon
{
    public static IData setProductParam(IDataset attrLists, UcaData grpUcaData, UcaData memUcaData, String memUserProdId, String instId) throws Exception
    {
        String userIdB = memUcaData.getUserId();
        String userIdA = grpUcaData.getUserId();
        IData attrDataMap = checkParam(attrLists, grpUcaData.getProductId(), memUcaData.getUser().getNetTypeCode());

        String packageId = "";
        if (GrpImsInfoQuery.DESKTEL_MEB_PRODUCTID.equals(memUserProdId))
            packageId = GrpImsInfoQuery.DESKTEL_MEBSVC_PAKAGEID;
        else if (GrpImsInfoQuery.VPN_MEB_PRODUCTID.equals(memUserProdId))
            packageId = GrpImsInfoQuery.VPN_SVC_PAKAGE_ID;
        else if (GrpImsInfoQuery.SUPTEL_MEB_PRODUCTID.equals(memUserProdId))
            packageId = GrpImsInfoQuery.VPN_SVC_PAKAGE_ID;
        else if (GrpImsInfoQuery.YHT_MEB_PRODUCTID.equals(memUserProdId))
            packageId = GrpImsInfoQuery.YHT_MEBSVC_PAKAGEID;

        IData conmmitData = new DataMap();
        IDataset elementInfo = new DatasetList();
        IDataset productParam = new DatasetList();
        boolean isDealMemWade = false;
        boolean isDealHSSWade = false;
        for (int i = 0, size = attrLists.size(); i < size; i++)
        {
            IData attrInfo = attrLists.getData(i);
            String modifyTag = attrInfo.getString("MODIFY_TAG");
            String attrCode = attrInfo.getString("ATTR_CODE");
            String attrValue = attrInfo.getString("ATTR_VALUE");

            if ("NOCOND_TRANSFER".equals(attrCode) || "BUSY_TRANSFER".equals(attrCode) || "CNTRX_MEMB_CFNR_BSV".equals(attrCode) || "CNTRX_MEMB_CFNL_BSV".equals(attrCode) || "CallBarring".equals(attrCode) || "HSS_IMPIATTR_IMPI_ID".equals(attrCode)
                    || "AlarmCall".equals(attrCode) || "CNTRX_NO_DISTURB".equals(attrCode) || "ThreePartyService".equals(attrCode) || "SHORT_DIAL".equals(attrCode) || "CNTRX_CORP_CLIP".equals(attrCode) || "CNTRX_CORP_CLIR".equals(attrCode)
                    || "CallWaiting".equals(attrCode) || "CallHold".equals(attrCode) || "CNTRX_MEMB_FAX".equals(attrCode) || "ClosedGroup".equals(attrCode) || "MEMB_ONENUMBER".equals(attrCode) || "DifferRinging".equals(attrCode))
            {
                // 处理服务及服务参数
                IData userSvc = dealService(userIdA, userIdB, attrInfo, memUserProdId, packageId, attrDataMap);
                if (IDataUtil.isEmpty(userSvc))
                {
                    continue;
                }
                elementInfo.add(userSvc);
            }
            else if ("NOCOND_TRANSFER_SN".equals(attrCode) || "BUSY_TRANSFER_SN".equals(attrCode) || "CNTRX_CFR_SN".equals(attrCode) || "CNTRX_CFNL_SN".equals(attrCode) || "MEMB_USERCALLBARRING".equals(attrCode)
                    || "MEMB_WAKE_NUMBER".equals(attrCode) || "MEMB_WAKE_TIME".equals(attrCode) || "NO_DISURB_TYPE".equals(attrCode) || "SHORT_NUMBER".equals(attrCode) || "HSS_ROAM_ID".equals(attrCode) || "HSS_AUTH_TYPE".equals(attrCode))
            {
                // 服务参数，入参无服务的情况单独处理

                if ((("MEMB_WAKE_NUMBER".equals(attrCode) || "MEMB_WAKE_TIME".equals(attrCode)) && isDealMemWade) || (("HSS_AUTH_TYPE".equals(attrCode) || "HSS_ROAM_ID".equals(attrCode)) && isDealHSSWade))
                {
                    continue;
                }
                // 处理服务参数
                IData userSvc = dealSevParamForNoServChg(userIdA, userIdB, attrInfo, memUserProdId, packageId, attrDataMap);
                if (IDataUtil.isEmpty(userSvc))
                {
                    continue;
                }
                if ("MEMB_WAKE_NUMBER".equals(attrCode) || "MEMB_WAKE_TIME".equals(attrCode))
                {
                    isDealMemWade = true;
                }
                else if ("HSS_AUTH_TYPE".equals(attrCode) || "HSS_ROAM_ID".equals(attrCode))
                {
                    isDealHSSWade = true;
                }
                elementInfo.add(userSvc);
            }
            else
            {
                if ("1".equals(modifyTag))
                {// 删除，取不到属性则不需要删除 continue
                    IDataset userAttr = UserAttrInfoQry.getUserAttrSingleByPK(userIdB, attrCode, instId, "P");
                    if (IDataUtil.isNotEmpty(userAttr))
                    {
                        attrValue = userAttr.getData(0).getString("ATTR_VALUE", "");
                    }
                    else if (IDataUtil.isEmpty(userAttr) && !attrCode.equals("OLD_SHORT_CODE"))
                    {
                        continue;
                    }
                }
                IData attrMap = new DataMap();
                if ("CNTRX_MEMB_ONE_RTYPE".equals(attrCode))
                {
                    attrMap.put("ATTR_CODE", "Z_TAG_VAL");
                    attrMap.put("ATTR_VALUE", attrValue);
                }
                else if ("IS_SUPERTELOPER".equals(attrCode))
                {
                    attrMap.put("ATTR_CODE", "IS_TELOPER");
                    attrMap.put("ATTR_VALUE", attrValue);
                }
                else if ("CNTRX_MEMB_POWER".equals(attrCode))
                {
                    if ("1".equals(attrValue) || "2".equals(attrValue))
                    {// 1:普通成员 2：集团管理员
                        conmmitData.put("MEM_ROLE_B", attrValue);
                        continue;
                    }
                    else
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_510);// 传入的CNTRX_MEMB_POWER值不正确！
                    }
                }
                else if (attrCode.equals("SHORT_CODE"))
                {
                    String oldShortCode = "";
                    IData shortCodeData = attrDataMap.getData("OLD_SHORT_CODE");
                    if (null != shortCodeData)
                    {
                        oldShortCode = shortCodeData.getString("ATTR_VALUE");
                    }

                    // 处理短号资源
                    if (!oldShortCode.equals(attrValue) || "".equals(instId))
                    {// instId为空，说明是成员新增
                        IData checkParam = new DataMap();
                        checkParam.put("SHORT_CODE", attrValue);
                        checkParam.put("USER_ID", userIdA);
                        checkParam.put(Route.ROUTE_EPARCHY_CODE, grpUcaData.getUserEparchyCode());
                        if (!GroupImsUtil.checkImsShortCode(checkParam))
                        {
                            CSAppException.apperr(GrpException.CRM_GRP_502, checkParam.getString("ERROR_MESSAGE"));
                        }
                        IDataset shortList = new DatasetList();
                        IData shortRes = new DataMap();
                        shortRes.put("RES_TYPE_CODE", "S");
                        shortRes.put("RES_CODE", attrValue);
                        shortRes.put("CHECKED", "true");
                        shortRes.put("DISABLED", "true");
                        shortRes.put("MODIFY_TAG", "0");
                        shortList.add(shortRes);

                        if (!"".equals(oldShortCode) && !oldShortCode.equals(attrValue))
                        {
                            IData oldShortRes = new DataMap();
                            oldShortRes.put("RES_TYPE_CODE", "S");
                            oldShortRes.put("RES_TYPE", "短号码");
                            oldShortRes.put("RES_CODE", oldShortCode);
                            oldShortRes.put("CHECKED", "true");
                            oldShortRes.put("DISABLED", "true");
                            oldShortRes.put("MODIFY_TAG", "1");
                            shortList.add(oldShortRes);
                        }

                        conmmitData.put("RES_INFO", shortList);
                    }
                    attrMap = attrInfo;
                }
                else
                {
                    attrMap = attrInfo;
                }
                productParam.add(attrMap);
            }
        }// 参数处理结束

        IData prodParamData = new DataMap();
        prodParamData.put("PRODUCT_ID", grpUcaData.getProductId());
        prodParamData.put("PRODUCT_PARAM", productParam);
        IDataset prodParamInfo = new DatasetList();
        prodParamInfo.add(prodParamData);
        conmmitData.put("PRODUCT_PARAM_INFO", prodParamInfo);
        conmmitData.put("ELEMENT_INFO", elementInfo);

        return conmmitData;
    }

    public static IDataset setElementInfo(IDataset discntListInfos, UcaData grpUcaData, UcaData memUcaData, String mebProductId) throws Exception
    {
        String userIdB = memUcaData.getUserId();
        String userIdA = grpUcaData.getUserId();

        IDataset elementInfo = new DatasetList();
        
        if (IDataUtil.isEmpty(discntListInfos))
        {
            return elementInfo;
        }

        for (int i = 0, dSize = discntListInfos.size(); i < dSize; i++)
        {

            IData discntListInfo = discntListInfos.getData(i);
            String modifyTag = discntListInfo.getString("MODIFY_TAG", "");
            String discntCode = discntListInfo.getString("DISCNT_CODE", "");

            // 参数校验
            if ("".equals(discntCode))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_69);// 接口参数检查，输入参数DISCNT_CODE不存在或值为空
            }
            if ("".equals(modifyTag))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_70);// 接口参数检查，输入参数MODIFY_TAG不存在或值为空
            }

            // 查询资费包信息
            IData pkgElement = PkgElemInfoQry.getDiscntsByDiscntCode(discntCode, mebProductId, memUcaData.getUserEparchyCode());
            if (IDataUtil.isEmpty(pkgElement))
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_263);// 根据优惠编码获取信息不存在
            }
            String packageId = pkgElement.getString("PACKAGE_ID", "");

            // 查询是否已定购过此优惠
            IDataset userDiscnt = UserDiscntInfoQry.getUserDiscntByDiscntCode(userIdB, userIdA, discntCode, memUcaData.getUserEparchyCode());

            IData disData = new DataMap();
            String endDate = SysDateMgr.getTheLastTime();

            if ("0".equals(modifyTag))
            {

                if (IDataUtil.isNotEmpty(userDiscnt))
                {
                    CSAppException.apperr(ElementException.CRM_ELEMENT_36, discntCode);// 用户已订购该优惠【%s】，无法再次订购！
                }

                if (ProductCompInfoQry.ifGroupCustomize(grpUcaData.getProductId()))
                {// 如果为定制优惠，查询是否已定制
                    // 查询已定制优惠
                    IDataset customizeDiscntList = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(userIdA, null);
                    if (IDataUtil.isEmpty(customizeDiscntList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_89);
                    }

                    boolean isHaveDis = false;
                    for (int j = 0, jRow = customizeDiscntList.size(); j < jRow; j++)
                    {
                        IData customizeData = customizeDiscntList.getData(j);
                        String customizeDiscntCode = customizeData.getString("ELEMENT_ID");

                        if (discntCode.equals(customizeDiscntCode))
                        {
                            isHaveDis = true;// 已定制
                        }

                    }
                    if (!isHaveDis)
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_89);// 集团未定制此优惠
                    }
                }
                String enableTag = pkgElement.getString("ENABLE_TAG", "");// 生效时间类型
                String startDate = SysDateMgr.getSysTime();
                if ("1".equals(enableTag) || "3".equals(enableTag))
                {// 1下账期生效 3可选下账期或立即，但反向订购无法选择,默认下账期
                    startDate = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
                }
                else if ("2".equals(enableTag))
                {// 次日生效
                    startDate = SysDateMgr.getTomorrowDate() + SysDateMgr.getFirstTime00000();
                }

                disData.put("START_DATE", startDate);
            }
            else if (("1".equals(modifyTag) || "2".equals(modifyTag)))
            {
                if (IDataUtil.isEmpty(userDiscnt))
                {
                    CSAppException.apperr(ElementException.CRM_ELEMENT_38, discntCode);// 用户未订购该优惠【%s】，无法取消!
                }
                disData = userDiscnt.getData(0);

                String cancelTag = pkgElement.getString("CANCEL_TAG", "");// 失效时间类型
                if ("0".equals(cancelTag))
                {// 立即取消
                    endDate = SysDateMgr.getSysTime();
                }
                else if ("1".equals(cancelTag))
                {// 昨天取消
                    endDate = SysDateMgr.getYesterdayDate() + SysDateMgr.getEndTime235959();
                }
                else if ("2".equals(cancelTag))
                {// 今天取消
                    endDate = SysDateMgr.getSysDate() + SysDateMgr.getEndTime235959();
                }
                else if ("3".equals(cancelTag))
                {// 本账期末取消（本月最后一天）
                    endDate = SysDateMgr.getLastDateThisMonth();
                }
            }
            disData.put("PRODUCT_ID", mebProductId);
            disData.put("PACKAGE_ID", packageId);
            disData.put("ELEMENT_ID", discntCode);
            disData.put("ELEMENT_TYPE_CODE", "D");
            disData.put("MODIFY_TAG", modifyTag);
            disData.put("END_DATE", endDate);

            elementInfo.add(disData);
        }
        return elementInfo;
    }

    public static IData checkParam(IDataset attrLists, String productId, String netTypeCode) throws Exception
    {
        IData attrDataMap = new DataMap();
        // 短号参数验证
        String short_code = "";
        String old_short_code = "";
        String supertel_num = "";
        String is_supertel = "";
        for (int i = 0; i < attrLists.size(); i++)
        {
            IData tempMap = attrLists.getData(i);
            String attrCode = tempMap.getString("ATTR_CODE");
            String attrValue = tempMap.getString("ATTR_VALUE");
            if ("SHORT_CODE".equals(attrCode))
            {
                short_code = attrValue;
                old_short_code = attrValue;//融合v网成员新增的时候 OLD_SHORT_CODE可以不传 这里特殊处理下
            }
            else if ("OLD_SHORT_CODE".equals(attrCode))
            {
                old_short_code = attrValue;
            }
            else if ("SUPERTELNUMBER".equals(attrCode))
            {
                supertel_num = attrValue;
            }
            else if ("IS_SUPERTELOPER".equals(attrCode))
            {
                is_supertel = attrValue;
            }
            attrDataMap.put(attrCode, tempMap);
        }
        if ((StringUtils.isEmpty(short_code) || StringUtils.isEmpty(old_short_code)) && GrpImsInfoQuery.VPN_GRP_PRODUCTID.equals(productId))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_511);// 【SHORT_CODE】和【OLD_SHORT_CODE】参数必填！
        }
        else if (("on".equals(is_supertel) && "".equals(supertel_num)) && GrpImsInfoQuery.SUPTEL_GRP_PRODUCTID.equals(productId))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_512);// 若设置为话务员，则总机号码【SUPERTELNUMBER】参数必填！
        }
        else if (GrpImsInfoQuery.SUPTEL_GRP_PRODUCTID.equals(productId) && "on".equals(is_supertel))
        {
            if (!"05".equals(netTypeCode))
            {
                CSAppException.apperr(GrpException.CRM_GRP_402);// 融合总机话务员必须为IMS用户
            }
        }
        return attrDataMap;
    }

    public static IData dealService(String userIdA, String userIdB, IData attrInfo, String memUserProdId, String packageId, IData attrDataMap) throws Exception
    {
        String attrCode = attrInfo.getString("ATTR_CODE");
        String attrValue = attrInfo.getString("ATTR_VALUE");

        String svcId = "";
        String svcParam = ""; // 服务参数
        if ("NOCOND_TRANSFER".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.NOCOND_TRANSFER;
            svcParam = "NOCOND_TRANSFER_SN";
        }
        else if ("BUSY_TRANSFER".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.BUSY_TRANSFER;
            svcParam = "BUSY_TRANSFER_SN";
        }
        else if ("CNTRX_MEMB_CFNR_BSV".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_MEMB_CFNR_BSV;
            svcParam = "CNTRX_CFR_SN";
        }
        else if ("CNTRX_MEMB_CFNL_BSV".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_MEMB_CFNL_BSV;
            svcParam = "CNTRX_CFNL_SN";
        }
        else if ("CallBarring".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CALLBARRING;
        }
        else if ("HSS_IMPIATTR_IMPI_ID".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.HSS_IMPIATTR_IMPI_ID;
        }
        else if ("AlarmCall".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.ALARMCALL;
            svcParam = "MEMB_WAKE_NUMBER,MEMB_WAKE_TIME";
        }
        else if ("CNTRX_NO_DISTURB".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_NO_DISTURB;
            svcParam = "NO_DISURB_TYPE";
        }
        else if ("SHORT_DIAL".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.SHORT_DIAL;
            svcParam = "SHORT_NUMBER";
        }
        else if ("CNTRX_CORP_CLIP".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_CORP_CLIP;
        }
        else if ("CNTRX_CORP_CLIR".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_CORP_CLIR;
        }
        else if ("CallWaiting".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CALLWAITING;
        }
        else if ("CallHold".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CALLHOLD;
        }
        else if ("ThreePartyService".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.THTEECALL;
        }
        else if ("ClosedGroup".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.ClosedGroup;
        }
        else if ("CNTRX_MEMB_FAX".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_MEMB_FAX;
        }
        else if ("MEMB_ONENUMBER".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.MEMB_ONENUMBER;
        }
        else if ("DifferRinging".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.DifferRinging;
        }

        IDataset userSvcs = UserSvcInfoQry.getSvcUserId(userIdB, userIdA, svcId);
        IData userSvc = new DataMap();

        if ("0".equals(attrValue) && IDataUtil.isNotEmpty(userSvcs))
        {// 如果状态为删除，并且已存在服务
            userSvc = userSvcs.getData(0);
            userSvc.put("MODIFY_TAG", "1");
            userSvc.put("END_DATE", SysDateMgr.getSysTime());

            // 缩位拨号服务参数特殊处理,删除缩位拨号
            if ("SHORT_DIAL".equals(attrCode))
            {
                IDataset tmpDs = new DatasetList();
                IData tmpMap = new DataMap();
                tmpMap.put("PARAM_VERIFY_SUCC", "true");
                tmpDs.add(0, tmpMap);
                userSvc.put("ATTR_PARAM", tmpDs);
            }
        }
        else if ("1".equals(attrValue) && IDataUtil.isEmpty(userSvcs))
        {// 如果状态为新增，并且不存在服务
            userSvc.put("MODIFY_TAG", "0");
            userSvc.put("PRODUCT_ID", memUserProdId);
            userSvc.put("PACKAGE_ID", packageId);
            userSvc.put("ELEMENT_ID", svcId);
            userSvc.put("ELEMENT_TYPE_CODE", "S");
            userSvc.put("START_DATE", SysDateMgr.getSysTime());
            userSvc.put("END_DATE", SysDateMgr.getTheLastTime());
        }
        else
        {
            // 如果状态为删除并且不存在用户服务，如果状态为新增并且已存在用户服务，这两种情况不用处理
            return null;
        }

        // 缩位拨号服务参数特殊处理
        if ("SHORT_DIAL".equals(attrCode))
        {
            IData attr = attrDataMap.getData("SHORT_NUMBER");
            IDataset tmpDs = dealShortDial(attrValue, userIdB, attr, svcId);
            userSvc.put("ATTR_PARAM", tmpDs);
            return userSvc;
        }
        // 其他服务参数统一处理
        if (!"".equals(svcParam))
        {
            IDataset attrDataset = new DatasetList();
            String[] attrStrs = svcParam.split(",");
            for (int j = 0, paramSize = attrStrs.length; j < paramSize; j++)
            {
                String svcAttrCode = attrStrs[j];
                if (!attrDataMap.containsKey(svcAttrCode))
                {
                    continue;
                }
                IData attr = attrDataMap.getData(svcAttrCode);
                String svcAttrValue = attr.getString("ATTR_VALUE");
                if ("NO_DISURB_TYPE".equals(svcAttrCode))
                {
                    svcAttrCode = "ListProperty";
                }
                else if ("NOCOND_TRANSFER_SN".equals(svcAttrCode))
                {
                    svcAttrCode = "UserCFUInfo";
                }
                else if ("BUSY_TRANSFER_SN".equals(svcAttrCode))
                {
                    svcAttrCode = "UserCFBInfo";
                }
                else if ("CNTRX_CFR_SN".equals(svcAttrCode))
                {
                    svcAttrCode = "UserCFNRInfo";
                }
                else if ("CNTRX_CFNL_SN".equals(svcAttrCode))
                {
                    svcAttrCode = "userCFNLInfo";
                }
                IData newAttrMap = new DataMap();
                newAttrMap.put("ATTR_CODE", svcAttrCode);
                newAttrMap.put("ATTR_VALUE", svcAttrValue);
                attrDataset.add(newAttrMap);
            }
            userSvc.put("ATTR_PARAM", attrDataset);
        }

        return userSvc;
    }

    public static IData dealSevParamForNoServChg(String userIdA, String userIdB, IData attrInfo, String memUserProdId, String packageId, IData attrDataMap) throws Exception
    {
        String attrCode = attrInfo.getString("ATTR_CODE");
        String attrValue = attrInfo.getString("ATTR_VALUE");

        String svcId = "";
        String svcCode = "";
        String otherParamCode = "";
        if ("NOCOND_TRANSFER_SN".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.NOCOND_TRANSFER;
            svcCode = "NOCOND_TRANSFER";
        }
        else if ("BUSY_TRANSFER_SN".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.BUSY_TRANSFER;
            svcCode = "BUSY_TRANSFER";
        }
        else if ("CNTRX_CFR_SN".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_MEMB_CFNR_BSV;
            svcCode = "CNTRX_MEMB_CFNR_BSV";
        }
        else if ("CNTRX_CFNL_SN".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_MEMB_CFNL_BSV;
            svcCode = "CNTRX_MEMB_CFNL_BSV";
        }
        else if ("NO_DISURB_TYPE".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.CNTRX_NO_DISTURB;
            svcCode = "CNTRX_NO_DISTURB";
        }
        else if ("SHORT_NUMBER".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.SHORT_DIAL;
            svcCode = "SHORT_DIAL";
        }
        else if ("HSS_AUTH_TYPE".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.HSS_BASESVC;
            svcCode = "HSS_AUTH_TYPE";
            otherParamCode = "HSS_ROAM_ID";
        }
        else if ("HSS_ROAM_ID".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.HSS_BASESVC;
            svcCode = "HSS_AUTH_TYPE";
            otherParamCode = "HSS_AUTH_TYPE";
        }
        else if ("MEMB_WAKE_NUMBER".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.ALARMCALL;
            svcCode = "AlarmCall";
            otherParamCode = "MEMB_WAKE_TIME";
        }
        else if ("MEMB_WAKE_TIME".equals(attrCode))
        {
            svcId = GrpImsInfoQuery.ALARMCALL;
            svcCode = "AlarmCall";
            otherParamCode = "MEMB_WAKE_NUMBER";
        }
        if (attrDataMap.containsKey(svcCode))
        {
            // 如果入参中存在服务，则在处理服务的时候处理数据
            return null;
        }

        IDataset userSvcs = UserSvcInfoQry.getSvcUserId(userIdB, userIdA, svcId);
        if (IDataUtil.isEmpty(userSvcs))
        {
            // 无服务实例，不处理参数
            return null;
        }

        IData userSvc = userSvcs.getData(0);
        userSvc.put("MODIFY_TAG", "2");
        userSvc.put("END_DATE", SysDateMgr.getSysTime());

        // 缩位拨号服务参数特殊处理
        if ("SHORT_NUMBER".equals(attrCode))
        {
            IDataset tmpDs = dealShortDial("2", userIdB, attrInfo, svcId);// 修改
            userSvc.put("ATTR_PARAM", tmpDs);
            return userSvc;
        }
        // 其他服务参数统一处理
        if ("NO_DISURB_TYPE".equals(attrCode))
        {
            attrCode = "ListProperty";
        }
        else if ("NOCOND_TRANSFER_SN".equals(attrCode))
        {
            attrCode = "UserCFUInfo";
        }
        else if ("BUSY_TRANSFER_SN".equals(attrCode))
        {
            attrCode = "UserCFBInfo";
        }
        else if ("CNTRX_CFR_SN".equals(attrCode))
        {
            attrCode = "UserCFNRInfo";
        }
        else if ("CNTRX_CFNL_SN".equals(attrCode))
        {
            attrCode = "userCFNLInfo";
        }

        IDataset attrDataset = new DatasetList();
        IData newAttrMap = new DataMap();
        newAttrMap.put("ATTR_CODE", attrCode);
        newAttrMap.put("ATTR_VALUE", attrValue);
        attrDataset.add(newAttrMap);

        if (!"".equals(otherParamCode) && attrDataMap.containsKey(otherParamCode))
        {
            IData otherAttrMap = attrDataMap.getData("otherParamCode");
            attrDataset.add(otherAttrMap);
        }
        userSvc.put("ATTR_PARAM", attrDataset);
        return userSvc;
    }

    public static IDataset dealShortDial(String attrValue, String userIdB, IData attr, String svcId) throws Exception
    {

        IDataset shortDataset = new DatasetList(); // 缩位短号

        if ("0".equals(attrValue))
        {
            IDataset oldShortData = UserOtherInfoQry.getUserOtherByUseridRsrvcode(userIdB, "SHORTDIALSN", null);
            if (IDataUtil.isNotEmpty(oldShortData))
            {
                
                for (int k = 0, shortCount = oldShortData.size(); k < shortCount; k++)
                {
                    IData oldData = oldShortData.getData(k);
                    
                    IData shortdata = new DataMap();
                    shortdata.put("LONG_NUMBER", oldData.getString("RSRV_STR1"));
                    shortdata.put("SHORT_NUMBER", oldData.getString("RSRV_VALUE"));
                    shortdata.put("tag", "1");
                    
                    shortDataset.add(shortdata);
                }
            }
        }
        else
        {
            IData shortdata = new DataMap();

            String svcAttrValue = attr.getString("ATTR_VALUE");
            String mTag = attr.getString("MODIFY_TAG");
            String[] shortlist = svcAttrValue.split("_");
            shortdata.put("LONG_NUMBER", shortlist[0]);
            shortdata.put("SHORT_NUMBER", shortlist[1]);
            shortdata.put("tag", mTag);

            shortDataset.add(shortdata);
        }

        IData longList = new DataMap();
        longList.put("LONG_LIST", shortDataset);
        longList.put("SERVICE_ID", svcId);

        IDataset tmpDs = new DatasetList();
        IData tmpMap = new DataMap();
        tmpMap.put("PARAM_VERIFY_SUCC", "true");
        tmpDs.add(0, tmpMap);
        tmpDs.add(1, longList);
        return tmpDs;
    }
}
