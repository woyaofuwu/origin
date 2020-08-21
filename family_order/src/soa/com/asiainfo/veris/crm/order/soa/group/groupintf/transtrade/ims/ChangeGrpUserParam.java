
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GrpImsInfoQuery;

public class ChangeGrpUserParam
{
    /*
     * @description Centrex业务修改集团用户参数信息
     * @author liuzz
     * @date 2014/06/11
     */
    public static IDataset OperGrpUserAttr(IData data) throws Exception
    {
        String grpuserId = data.getString("USER_ID_A");
        IDataset productparams = data.getDataset("PRODUCT_ATTR", new DatasetList());
        IDataset discntListInfos = data.getDataset("LIST_INFOS", new DatasetList());

        // 一、查询用户产品信息
        IData userProd = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(grpuserId);
        if (IDataUtil.isEmpty(userProd))
        {
            CSAppException.apperr(GrpException.CRM_GRP_93);// 获取集团用户订购产品资料无信息！
        }
        String productId = userProd.getString("PRODUCT_ID");
        // 二、如果融合总机，处理融合总机参数
        delSpecialProdParam(productId, grpuserId, productparams);
        // ***************处理属性信息**************
        IDataset productParam = new DatasetList();
        for (int i = 0, size = productparams.size(); i < size; i++)
        {
            IData proparam = productparams.getData(i);
            String attrCode = proparam.getString("ATTR_CODE", "");
            String attrValue = proparam.getString("ATTR_VALUE", "");
            String modTag = proparam.getString("MODIFY_TAG", ""); // 0-新增,1-删除,2-修改
            if ("1".equals(modTag))
            {// 删除，将属性值置空
                attrValue = "";
            }
            // 移动总机参数已经在前面处理放在SUPERNUMBER中了
            if (GrpImsInfoQuery.SUPTEL_GRP_PRODUCTID.equals(productId) && !attrCode.equals("SUPERNUMBER") && attrCode.equals("EXCHANGETELE_SN"))
            {
                continue;
            }
            IData attrData = new DataMap();
            attrData.put("ATTR_CODE", attrCode);
            attrData.put("ATTR_VALUE", attrValue);
            productParam.add(attrData);
        }
        IData prodParam = new DataMap();
        prodParam.put("PRODUCT_ID", productId);
        prodParam.put("PRODUCT_PARAM", productParam);
        IDataset productParamList = new DatasetList();
        productParamList.add(prodParam);

        // ***************处理资费信息**************
        IDataset productDistctList = new DatasetList();
        int dealDiscntCount = 0;
        String packageId = "";
        String enableTag = "";// 生效时间类型
        for (int j = 0, dSize = discntListInfos.size(); j < dSize; j++)
        {
            IData discntListInfo = (IData) discntListInfos.get(j);
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
            IData pkgElement = PkgElemInfoQry.getDiscntsByDiscntCode(discntCode, productId, userProd.getString("EPARCHY_CODE"));
            if (IDataUtil.isEmpty(pkgElement))
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_263);// 根据优惠编码获取信息不存在
            }
            packageId = pkgElement.getString("PACKAGE_ID", "");
            enableTag = pkgElement.getString("ENABLE_TAG", "");
            IDataset userDiscnt = UserDiscntInfoQry.getAllDiscntByUser(grpuserId, discntCode);
            IData disData = new DataMap();
            String endDate = SysDateMgr.getTheLastTime();
            if ("0".equals(modifyTag))
            {
                if (IDataUtil.isNotEmpty(userDiscnt))
                {
                    CSAppException.apperr(ElementException.CRM_ELEMENT_36, discntCode);// 用户已订购该优惠【%s】，无法再次订购！
                }

                String startDate = SysDateMgr.getSysTime();
                if ("1".equals(enableTag))
                {// 下账期生效
                    startDate = SysDateMgr.getFirstDayOfNextMonth() + SysDateMgr.getFirstTime00000();
                }
                else if ("2".equals(enableTag))
                {// 次日生效
                    startDate = SysDateMgr.getTomorrowDate() + SysDateMgr.getFirstTime00000();
                }
                disData.put("START_DATE", startDate);
                disData.put("PRODUCT_ID", productId);
                disData.put("PACKAGE_ID", packageId);
                disData.put("ELEMENT_ID", discntCode);
                dealDiscntCount++;
            }
            else if (("1".equals(modifyTag) || "2".equals(modifyTag)))
            {
                if (IDataUtil.isEmpty(userDiscnt))
                {
                    CSAppException.apperr(ElementException.CRM_ELEMENT_38, discntCode);// 用户未订购该优惠【%s】，无法取消!
                }
                disData = userDiscnt.getData(0);

                String cancelTag = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PACKAGE_ELEMENT", new String[]
                { "PACKAGE_ID", "ELEMENT_TYPE_CODE", "ELEMENT_ID" }, "CANCEL_TAG", new String[]
                { packageId, "D", discntCode });
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
                else if ("3".equals("cancelTag"))
                {// 本账期末取消（本月最后一天）
                    endDate = SysDateMgr.getLastDateThisMonth();
                }
                if ("1".equals(modifyTag))
                {
                    dealDiscntCount--;
                }
            }

            disData.put("ELEMENT_TYPE_CODE", "D");
            disData.put("MODIFY_TAG", modifyTag);
            disData.put("END_DATE", endDate);

            if (GrpImsInfoQuery.DESKTEL_DIS_PAKAGEID.equals(packageId))
            {// 桌面电话资费,处理资费属性
                IDataset attrParamList = new DatasetList();
                String commuCost = discntListInfo.getString("COMMU_COST", "");
                String discount = discntListInfo.getString("DISCOUNT", "");
                IData commuData = new DataMap();
                IData discountData = new DataMap();
                commuData.put("ATTR_VALUE", commuCost);
                commuData.put("ATTR_CODE", GrpImsInfoQuery.DESKTEL_DIS_COMMU_COST);

                discountData.put("ATTR_VALUE", discount);
                discountData.put("ATTR_CODE", GrpImsInfoQuery.DESKTEL_DIS_DISCOUNT);

                attrParamList.add(commuData);
                attrParamList.add(discountData);

                disData.put("ATTR_PARAM", attrParamList);
            }
            productDistctList.add(disData);
        }
        IData param = new DataMap();
        param.put("EFFECT_TIME", enableTag);
        param.put("USER_ID", grpuserId);
        param.put("USER_ID_A", "-1");
        param.put("PACKAGE_ID", packageId);
        IDataset oldDiscnt = UserDiscntInfoQry.getUserDiscntByUserId(param, userProd.getString("EPARCHY_CODE"));
        if (IDataUtil.isNotEmpty(oldDiscnt))
        {
            dealDiscntCount = dealDiscntCount + oldDiscnt.size();
        }
        if (dealDiscntCount > 1)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_232, packageId);// 优惠元素个数限制，已有优惠已经达到包元素上限
        }
        IData conmmitData = new DataMap();
        conmmitData.put("PRODUCT_PARAM_INFO", productParamList);
        conmmitData.put("ELEMENT_INFO", productDistctList);
        conmmitData.put("USER_ID", grpuserId);
        conmmitData.put("PRODUCT_ID", productId);
        conmmitData.put(Route.USER_EPARCHY_CODE, userProd.getString("EPARCHY_CODE"));
        conmmitData.put("IS_NEED_TRANS", false);
        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("CS.ChangeUserElementSvc.changeUserElement", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }

    public static void delSpecialProdParam(String productId, String grpuserId, IDataset paramset) throws Exception
    {
        if (IDataUtil.isEmpty(paramset) || !GrpImsInfoQuery.SUPTEL_GRP_PRODUCTID.equals(productId))
        {
            return;
        }

        String EXCHANGETELE_SN = ""; // 总机号
        String MAXWAITINGLENGTH = ""; // 等待队列数
        String CORP_DEREGCODE = ""; // 话务员注销码
        String CORP_REGCODE = ""; // 话务员注册码
        String CALLCENTERSHOW = ""; // 呼群用户显号码
        String IMS_CALLCENTERTYPE = ""; // 呼叫种类
        String moditytag = "";

        for (int i = paramset.size() - 1; i >= 0; i--)
        {
            IData param = paramset.getData(i);
            String attrcode = param.getString("ATTR_CODE", "");
            String attrvalue = param.getString("ATTR_VALUE", "");
            if (attrcode.equals("EXCHANGETELE_SN"))
            {
                EXCHANGETELE_SN = attrvalue;
                paramset.remove(i);
                moditytag = param.getString("MODIFY_TAG", "");
            }
            else if (attrcode.equals("MAXWAITINGLENGTH"))
            {
                MAXWAITINGLENGTH = attrvalue;
                paramset.remove(i);
            }
            else if (attrcode.equals("CORP_DEREGCODE"))
            {
                CORP_DEREGCODE = attrvalue;
                paramset.remove(i);
            }
            else if (attrcode.equals("CORP_REGCODE"))
            {
                CORP_REGCODE = attrvalue;
                paramset.remove(i);
            }
            else if (attrcode.equals("CALLCENTERSHOW"))
            {
                CALLCENTERSHOW = attrvalue;
                paramset.remove(i);
            }
            else if (attrcode.equals("CALLCENTERTYPE"))
            {
                IMS_CALLCENTERTYPE = attrvalue;
                paramset.remove(i);
            }

        }

        IDataset dataset = new DatasetList();

        if (!EXCHANGETELE_SN.equals(""))
        {
            IDataset otherInfos = UserOtherInfoQry.getUserOther(grpuserId, "MUTISUPERTEL");

            boolean ifexist = false;
            IData oldsuperdata = new DataMap();
            if (otherInfos != null && otherInfos.size() > 0)
            {
                for (int k = 0; k < otherInfos.size(); k++)
                {
                    IData tempother = otherInfos.getData(k);
                    String serialnumber = tempother.getString("RSRV_VALUE", "");
                    if (EXCHANGETELE_SN.equals(serialnumber))
                    {
                        ifexist = true;
                        oldsuperdata = tempother;
                        break;
                    }
                }
            }

            if (moditytag.equals("0"))
            {
                if (ifexist)
                {
                    CSAppException.apperr(GrpException.CRM_GRP_631, EXCHANGETELE_SN);// 该号码[%s]当前已经存在融合总机集团关系，不能作为总机号码！
                }
                IData data = new DataMap();

                data.put("EXCHANGETELE_SN", EXCHANGETELE_SN);
                data.put("tag", "0");
                data.put("CORP_DEREGCODE", CORP_DEREGCODE);
                data.put("CORP_REGCODE", CORP_REGCODE);
                data.put("CALLCENTERSHOW", CALLCENTERSHOW);
                data.put("CALLCENTERTYPE", IMS_CALLCENTERTYPE);
                data.put("MAXWAITINGLENGTH", MAXWAITINGLENGTH);

                IData userparam = new DataMap();
                userparam.put("REMOVE_TAG", "0");
                userparam.put("SERIAL_NUMBER", EXCHANGETELE_SN);
                UcaData uca = UcaDataFactory.getNormalUca(EXCHANGETELE_SN);

                data.put("E_CUST_NAME", uca.getCustomer().getCustName()); // 客户名称
                data.put("E_BRAND_CODE", uca.getBrandCode()); // 品牌
                data.put("E_EPARCHY_CODE", uca.getUserEparchyCode()); // 归属地州
                data.put("E_USER_ID", uca.getUserId()); // 用户ID
                data.put("E_CUST_ID", uca.getCustId()); // 客户ID

                dataset.add(data);

            }
            else if (moditytag.equals("1"))
            {
                if (!ifexist)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_395);// 该号码不是总机号码，不需要删除总机号
                }
                IData data = new DataMap();
                data.put("EXCHANGETELE_SN", EXCHANGETELE_SN);
                data.put("tag", "1");
                dataset.add(data);
            }
            else if (moditytag.equals("2"))
            {
                if (!ifexist)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_396);// 该号码不是总机号码，不需要修改总机号信息
                }

                if (MAXWAITINGLENGTH.equals(""))
                    MAXWAITINGLENGTH = oldsuperdata.getString("RSRV_STR6", "");
                if (CORP_DEREGCODE.equals(""))
                    CORP_DEREGCODE = oldsuperdata.getString("RSRV_STR10", "");
                if (CORP_REGCODE.equals(""))
                    CORP_REGCODE = oldsuperdata.getString("RSRV_STR9", "");
                if (CALLCENTERSHOW.equals(""))
                    CALLCENTERSHOW = oldsuperdata.getString("RSRV_STR8", "");
                if (IMS_CALLCENTERTYPE.equals(""))
                    IMS_CALLCENTERTYPE = oldsuperdata.getString("RSRV_STR7", "");
                IData data = new DataMap();
                data.put("EXCHANGETELE_SN", EXCHANGETELE_SN);
                data.put("tag", "2");
                data.put("CORP_DEREGCODE", CORP_DEREGCODE);
                data.put("CORP_REGCODE", CORP_REGCODE);
                data.put("CALLCENTERSHOW", CALLCENTERSHOW);
                data.put("CALLCENTERTYPE", IMS_CALLCENTERTYPE);
                data.put("MAXWAITINGLENGTH", MAXWAITINGLENGTH);
                dataset.add(data);

            }
            else
            {
                CSAppException.apperr(ParamException.CRM_PARAM_222);// 获取MODIFY_TAG的取值不正确
            }
        }
        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }
        IData supdata = new DataMap();
        supdata.put("ATTR_CODE", "SUPERNUMBER");
        supdata.put("ATTR_VALUE", dataset);
        supdata.put("MODIFY_TAG", "2");
        paramset.add(supdata);
    }

}
