
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.adcmas;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.IntfIAGWException;
import com.asiainfo.veris.crm.order.pub.exception.IntfPADCException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class MemParamData extends CSBizBean
{
    private static transient Logger logger = Logger.getLogger(MemParamData.class);
    /**
     * 处理adc/mas 校讯通(新)反向接口
     *
     * @author
     * @param data
     * @return
     * @throws Exception
     */
    public IData subAdcGrpMemBiz(IData data) throws Exception
    {

        // 1,获取集团用户标识,校验集团,成员三户信息:必须传GROUP_ID,BIZ_CODE,opr_code 为03,04报错
        DataUtil.getPADCGroupAndProduct(data);
        String directTion = data.getString("DIRECTION", "");// 业务方向
        String grpUserId = data.getString("GRP_USER_ID");
        String mebUserId = data.getString("MEB_USER_ID"); // 成员usrId
        String oprCode = data.getString("OPR_CODE");

        //String mainSn = data.getString("MOB_NUM");
       // String outSn = data.getString("servicephone"); // 异网号码

        String mainSn = data.getString("FEE_MOB_NUM");
        String outSn = data.getString("MOB_NUM"); // 异网号码
        data.put("servicephone", outSn);

        String memEparchyCode = data.getString("MEB_EPARCHY_CODE");
        String grpServId = data.getString("GRP_SERVICE_ID");
        String grpProductId = data.getString("GRP_PRODUCT_ID");

        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(grpProductId);
        //如果用户已经办理了教师套餐，则返回失败给平台
        IDataset isTeacher = RelaXxtInfoQry.qryOutSnInfobySnaAndSnbAndEleId(mainSn,outSn,"5912",grpUserId);
        if (!IDataUtil.isEmpty(isTeacher) && "PXXT".equals(directTion))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_12);

        }

        if (!"01".equals(oprCode) && !"02".equals(oprCode) && !"05".equals(oprCode))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_01);
        }

        String bizCtrlType = ""; // 操作类型

        boolean isBB = DataUtil.isSnExistBB(grpUserId, mebUserId, grpProductId);// 判断用户relaBB关系isMemExist
        boolean isUU = DataUtil.isOutSnExistUU(outSn, mebUserId,grpUserId);// 异网号码和计费号码是否存在UU关系

        IDataset mainSnXXTInfo = null;

        String operCodeByOutSn = "";

        // 1, bb没记录 && opr_code == 01 是成员新增,opr_code为其他值,抛错:成员不在白名单内
        if (!isBB && (!"01".equals(oprCode)))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_06);
        }

        // 1.1, bb没记录 && opr_code == 01 说明是成员新增
        if (!isBB && ("01".equals(oprCode)))
        {// 成员新增
            bizCtrlType = BizCtrlType.CreateMember;
        }

        // 2, bb有记录 && opr_code == 01 && xxtSize>0有记录抛错,否则就成员变更里面的新增异网号码
        if (isBB && "01".equals(oprCode))
        {
            if (isUU)
            {// 此异网号码已经新增
                CSAppException.apperr(IntfPADCException.CRM_SAGM_05);
            }
            else
            {// 变更里新增异网号码
                bizCtrlType = BizCtrlType.ChangeMemberDis;
                operCodeByOutSn = "0";// 新增当前异网号码
            }
        }

        // 3, bb有记录 && opr_code == 02 && xxtSize==1,做成员退订,xxtSize>1,则成员变更 xxxSize==0 则opr_code错误
        if (isBB && "02".equals(oprCode))
        {
            if (isUU)
            {
                IDataset outSnIDatset = RelaXxtInfoQry.queryMemInfoBySNandUserIdA(mainSn, grpUserId);
                if (IDataUtil.isEmpty(outSnIDatset))
                {
                    CSAppException.apperr(IntfPADCException.CRM_SAGM_01);
                }
                bizCtrlType = BizCtrlType.DestoryMember;	//只管计费号码，不管家长号码，因而都是成员退订
                /*int outSnSize = outSnIDatset.size();
                if (outSnSize == 1)
                {// 成员退订
                    bizCtrlType = BizCtrlType.DestoryMember;// 成员退订
                }
                else if (outSnSize > 1)
                {// 变更里面删除异网号码
                    bizCtrlType = BizCtrlType.ChangeMemberDis;
                    operCodeByOutSn = "1";// 删除当前异网号码
                }*/
            }
            else
            {// 此异网号码没有记录,不能退订
                CSAppException.apperr(IntfPADCException.CRM_SAGM_06);
            }
        }
        // 4, bb有记录 && opr_code == 05 && xxtSize==0 抛错, xxtSize>0 则变更
        if (isBB && "05".equals(oprCode))
        {
        	/*
        	 * UU表没有记录也允许变更，为变更订购
            if (!isUU)
            {
                CSAppException.apperr(IntfPADCException.CRM_SAGM_05);
                // xxt没记录,不能变更
            }
            else
            { */
            	//REQ201909230005关于和教育业务2019秋季开学问题的优化需求   订购关系以付费号码为准，取消家长号码的判断；
                //mainSnXXTInfo = RelaXxtInfoQry.queryMemInfoByOutSnMebUserIdGrpUserId(outSn, mainSn, grpUserId);// to-do
                mainSnXXTInfo = RelaXxtInfoQry.qryMemInfoBySNForUIPDestroy(mainSn, grpUserId);//查询同一集团 同一个付费号下所有代付号码
                mainSnXXTInfo = DataHelper.filter(mainSnXXTInfo, "ELEMENT_TYPE_CODE=D");//只处理资费
                // 根据集团用户标识+MOB_NUM(计费号码)+servicephone(异网号码)查询xxt
                int xxtSize = mainSnXXTInfo.size();
                if (xxtSize == 0)
                {
                    CSAppException.apperr(IntfPADCException.CRM_SAGM_01);
                }
                else
                {// 变更当前异网号码 元素 or xxt参数
                    bizCtrlType = BizCtrlType.ChangeMemberDis;
                    operCodeByOutSn = "2";// 变更当前异网号码
                }
            //}
        }

        IData result = new DataMap();// 返回结果

        if (BizCtrlType.CreateMember.equals(bizCtrlType))
        {// 成员新增
            // 格式：PRODUCT_ID,PACKAGE_ID,ELEMENT_ID ;
            IData servInfos = getServInfo(mebUserId, grpUserId, grpServId, memEparchyCode, memProductId);

            // 资费
            IData discntInfos = getDiscntInfo(memProductId, data);

            result.put("PRODUCT_ID", grpProductId); // 10009150-ADC校讯通（新）
            result.put("SERVICE_CODE", servInfos.getString("SERVICE_CODE"));
            result.put("DISCNT_CODE", discntInfos.getString("DISCNT_CODE"));

            // grp_product_id,ponint_code
            // 产品参数
            IDataset productParamInfos = DataUtil.dealProductParamInfo(data, "0");
            result.put("PRODUCT_PARAM_INFO", productParamInfos);
            
            //同一个计费号码在六个套餐种，每个套餐只允许订购一次
            IData inparam = new DataMap();
            inparam.put("SERIAL_NUMBER_A", mainSn);
            inparam.put("EC_USER_ID", grpUserId);
    		IDataset grouplist = RelaXxtInfoQry.queryXxtInfoBySnGroup(inparam);
            checkstu(productParamInfos,grouplist);
        }
        else if (BizCtrlType.ChangeMemberDis.equals(bizCtrlType))
        {// 变更
            IDataset productParamInfos = null;
            result.put("PRODUCT_ID", grpProductId); // 10009150-ADC校讯通（新）

            if ("0".equals(operCodeByOutSn))
            {// 变更里面新增异网号码

                data.put("OUT_OPER_TYPE", operCodeByOutSn);
                IDataset discntInfos = getDiscntInfo2ChgMeb2AddMeb(memProductId, data, "0");
                result.put("ELEMENT_INFO", discntInfos);
                productParamInfos = DataUtil.dealProductParamInfo(data, "0");

            }
            else if ("1".equals(operCodeByOutSn))
            {// 变更里面删除异网号码
                data.put("OUT_OPER_TYPE", operCodeByOutSn);
                IDataset discntInfos = getDiscntInfo2ChgMeb2DelMeb(memProductId, data, "0");
                result.put("ELEMENT_INFO", discntInfos);
                productParamInfos = DataUtil.dealProductParamInfo(data, "1");

            }
            else
            {// 变更里面变更异网号码 资费 or 学生姓名
                data.put("OUT_OPER_TYPE", operCodeByOutSn);
//                //如果订购了家长套餐，则保留原先的套餐
//                IDataset isParent = RelaXxtInfoQry.qryOutSnInfobySnaAndSnbAndEleId(mainSn,outSn,"5911",grpUserId);
//                if(!IDataUtil.isEmpty(isParent))
//                {
//                    data.put("POINT_CODE", data.get("POINT_CODE")+"||" +"5911");
//                }

                IData discntInfos = getDiscntInfo2ChgMeb2ChgMeb(memProductId, data, mainSnXXTInfo);

                IDataset elementInfos = discntInfos.getDataset("ELEMENT_INFOS");

                IDataset paramsInfos = discntInfos.getDataset("XXT_INOFS");

                result.put("ELEMENT_INFO", elementInfos);

                productParamInfos = DataUtil.dealProductParamInfo2ChgMeb(paramsInfos, data);
                
                //同一个计费号码在六个套餐种，每个套餐只允许订购一次
                IData inparam = new DataMap();
                inparam.put("SERIAL_NUMBER_A", mainSn);
                inparam.put("EC_USER_ID", grpUserId);
        		IDataset grouplist = RelaXxtInfoQry.queryXxtInfoBySnGroup(inparam);
                checkstu(productParamInfos,grouplist);
            }

            result.put("PRODUCT_PARAM_INFO", productParamInfos);
        }

        result.put("BIZ_CTRL_TYPE", bizCtrlType);

        result.put("USER_ID", grpUserId);// 集团用户标识
        result.put("SERIAL_NUMBER", mainSn);// 成员用户号码

        return result;
    }
    
    
    /**
     * 处理adc/mas 校讯通(新)反向接口
     *
     * @author
     * @param data
     * @return
     * @throws Exception
     */
    public IData subTxlGrpMemBiz(IData data) throws Exception
    {

        IData result = new DataMap();

        result.put("IS_OUT_NET", false);//
        String modifyTag = "";// 新增,变更,退订,返回出去根据值看调哪个服务
        String oprCode = data.getString("OPR_CODE");
        String mainSn = data.getString("MOB_NUM");
        if (!"01".equals(oprCode) && !"02".equals(oprCode))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_02);
        }

        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userGrpInfo))
        {// 一、MOB_NUM网外号码
            result.put("IS_OUT_NET", true);
            DataUtil.getOutGroupAndProduct(data); // 获取用户、客户、集团用户的产品信息存入TD中

            String grpServId = data.getString("GRP_SERVICE_ID");
            String grpProductId = data.getString("GRP_PRODUCT_ID");
            String grpUserId = data.getString("GRP_USER_ID");
            String mebSvcCode = data.getString("MEB_SERVICE_ID");
            IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuseridSerid(mainSn, grpUserId, mebSvcCode);// 查sn在blackwhite的记录
            boolean isBW = IDataUtil.isNotEmpty(blackwhite) ? true : false;

            if ("01".equals(oprCode))
            {
                if (!isBW)// BW没记录 CrtMb
                {
                    modifyTag = BizCtrlType.CreateMember;
                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_08); // ("用户已在名单内")
                }
            }
            if ("02".equals(oprCode))
            {
                // uu记录,BW除了该服务记录 还有其它服务记录，说明是变更中退让一个服务
                if (isBW)
                {
                    modifyTag = BizCtrlType.DestoryMember;
                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
                }
            }
            // 拼服务参数数据----START---
            IData serviceInfo = new DataMap();

            serviceInfo.put("PRODUCT_ID", grpProductId);
            serviceInfo.put("SERVICE_ID", grpServId);
            // 查询该网外号码是否已经加入黑白名单
            if (IDataUtil.isEmpty(blackwhite) && BizCtrlType.CreateMember.equals(modifyTag) && "01".equals(oprCode))
            {
                serviceInfo.put("OPER_TYPE", oprCode); // 加入黑白名单
                serviceInfo.put("MEB_USER_ID", "-1"); // 成员USER_ID
                serviceInfo.put("MODIFY_TAG", "0"); // 新增
            }
            else
            {
                IData oldblackwhite = blackwhite.getData(0);
                serviceInfo.put("OPER_TYPE", oprCode); // 退出黑白名单
                serviceInfo.put("MEB_USER_ID", oldblackwhite.getString("USER_ID", "")); // 成员USER_ID
                serviceInfo.put("MODIFY_TAG", "1"); // 删除
            }
            // 拼服务参数数据----end------

            // IData serviceParamInfos = dealServiceParamInfo(data);
            result.put("PRODUCT_ID", grpUserId);// 集团产品ID
            result.put("MEB_USER_ID", "-1");// 成员USER_ID
            result.put("SERIAL_NUMBER", mainSn);// 成员用户号码
            result.put("USER_ID", grpUserId);// 集团用户标识
            result.put("SERVICE_INFOS", new DatasetList(serviceInfo));// 服务参数信息
            result.put("BIZ_CTRL_TYPE", modifyTag);
            result.put(Route.ROUTE_EPARCHY_CODE, "0898");
        }
        else
        {// 二、MOB_NUM 网内号码
         // 1,获取集团用户标识,校验集团,成员三户信息:必须传SERV_CODE,BIZ_CODE
            DataUtil.getGroupAndProduct(data);

            String userIdA = data.getString("GRP_USER_ID");
            String userId = data.getString("MEB_USER_ID"); // 成员usrId
            String grpProductId = data.getString("GRP_PRODUCT_ID");
            String grpServId = data.getString("GRP_SERVICE_ID");
            String memEparchyCode = data.getString("MEB_EPARCHY_CODE");
            String mebSvcCode = data.getString("MEB_SERVICE_ID");
            String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(grpProductId);

            boolean isBB = DataUtil.isSnExistBB(userIdA, userId, grpProductId);// 判断用户relaBB关系isMemExist
            IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuserid(mainSn, userIdA);// 查sn在blackwhite的记录
            IDataset blackwhiteExp = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuseridSerid(mainSn, userIdA, mebSvcCode);// 查sn在blackwhite的记录
            boolean isBW = IDataUtil.isNotEmpty(blackwhiteExp) ? true : false;
            int iBlackwhite = blackwhite.size();

            if ("01".equals(data.getString("OPR_CODE")))
            {
                if (!isBB)// bb没记录 CrtMb
                {
                    modifyTag = BizCtrlType.CreateMember;
                }
                else if (isBB && !isBW ) // bb有记录，bw没有记录changeMeb里面的新增服务
                {
                    modifyTag = BizCtrlType.ChangeMemberDis;
                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_08); // ("用户已在名单内")
                }
            }
            else if ("02".equals(data.getString("OPR_CODE")))
            {
                // uu记录,BW除了该服务记录 还有其它服务记录，说明是变更中退让一个服务（海南不在在变更时退订服务）
                if (isBB && isBW && iBlackwhite <= 1)
                {
                    modifyTag = BizCtrlType.DestoryMember;
                }
                else if (isBB &&isBW && iBlackwhite > 1)
                {
                    modifyTag = BizCtrlType.ChangeMemberDis;

                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
                }
            }
            else
            {
                CSAppException.apperr(IntfIAGWException.CRM_IGU_11);// ("用户不在名单内")
            }
            
            if (BizCtrlType.CreateMember.equals(modifyTag))
            {
                IData servInfos = getServInfo(userId, userIdA, grpServId, memEparchyCode, memProductId);
                IData discntInfos = getDiscntInfo(memProductId, data);
                result.put("SERVICE_CODE", servInfos.getString("SERVICE_CODE"));
                result.put("DISCNT_CODE", discntInfos.getString("DISCNT_CODE"));
            }
            else if (BizCtrlType.ChangeMemberDis.equals(modifyTag))
            {
                IDataset elementInfos = new DatasetList();
                if (StringUtils.isNotEmpty(data.getString("POINT_CODE")))
                {

                    IData elementInfo = new DataMap();
                    if ("01".equals(data.getString("OPR_CODE")))
                    {
                        elementInfo.put("DISCNT_CODE", data.getString("POINT_CODE"));
                        elementInfo.put("MODIFY_TAG", "0");
                        elementInfo.put("ELEMENT_TYPE_CODE", "D");
                        elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                        elementInfo.put("START_DATE", SysDateMgr.getSysTime());
                        elementInfo.put("PRODUCT_ID", "980501");
                        elementInfo.put("PACKAGE_ID", "98050102");
                        elementInfo.put("INST_ID", "");
                        elementInfo.put("ELEMENT_ID", data.getString("POINT_CODE"));
                        elementInfos.add(elementInfo);
                    }
                    else
                    {
                        IDataset userInfoDiscntAll = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(userId, userIdA);
                        IDataset newDiscnts1 = DataHelper.filter(userInfoDiscntAll, "DISCNT_CODE=" + data.getString("POINT_CODE"));
                        if (IDataUtil.isNotEmpty(newDiscnts1))
                        {
                            elementInfo.put("DISCNT_CODE", data.getString("POINT_CODE"));
                            elementInfo.put("INST_ID", newDiscnts1.getData(0).getString("INST_ID"));
                            elementInfo.put("START_DATE", newDiscnts1.getData(0).getString("START_DATE"));
                            elementInfo.put("ELEMENT_TYPE_CODE", "D");
                            elementInfo.put("MODIFY_TAG", "1");
                            elementInfo.put("PRODUCT_ID", newDiscnts1.getData(0).getString("PRODUCT_ID"));
                            elementInfo.put("END_DATE", SysDateMgr.getSysTime());
                            elementInfo.put("PACKAGE_ID", newDiscnts1.getData(0).getString("PACKAGE_ID"));
                            elementInfo.put("ELEMENT_ID", newDiscnts1.getData(0).getString("DISCNT_CODE"));
                            elementInfos.add(elementInfo);
                        }
                    }
                }
                if (StringUtils.isNotEmpty(mebSvcCode))
                {

                    IData elementInfo = new DataMap();
                    if ("01".equals(data.getString("OPR_CODE")))
                    {
                        elementInfo.put("MODIFY_TAG", "0");
                        elementInfo.put("ELEMENT_TYPE_CODE", "S");
                        elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                        elementInfo.put("START_DATE", SysDateMgr.getSysTime());
                        elementInfo.put("PRODUCT_ID", "980501");
                        IDataset elementList = UProductElementInfoQry.queryElementInfosByProductIdAndElementIdElemetnTypeCode("980501",mebSvcCode,"S");
                        elementInfo.put("PACKAGE_ID", elementList.getData(0).getString("PACKAGE_ID"));
                        elementInfo.put("INST_ID", "");
                        elementInfo.put("ELEMENT_ID", mebSvcCode);

                    }
                    else
                    {
                        IDataset userSvc = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);
                        IDataset newSvc = DataHelper.filter(userSvc, "SERVICE_ID=" + mebSvcCode);
                        if (IDataUtil.isNotEmpty(newSvc))
                        {
                            elementInfo.put("INST_ID", newSvc.getData(0).getString("INST_ID"));
                            elementInfo.put("START_DATE", newSvc.getData(0).getString("START_DATE"));
                            elementInfo.put("ELEMENT_TYPE_CODE", "S");
                            elementInfo.put("MODIFY_TAG", "1");
                            elementInfo.put("PRODUCT_ID", newSvc.getData(0).getString("PRODUCT_ID"));
                            elementInfo.put("END_DATE", SysDateMgr.getSysTime());
                            elementInfo.put("PACKAGE_ID", newSvc.getData(0).getString("PACKAGE_ID"));
                            elementInfo.put("ELEMENT_ID", newSvc.getData(0).getString("SERVICE_ID"));
                        }
                    }
                    IData params = new DataMap();
                    params.put("USER_ID", userId);
                    params.put("USER_ID_A", userIdA);

                    params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 服务根据成员EPARCHY_CODE路由
                    params.put("SERVICE_ID", mebSvcCode);
                    IDataset plantSvcParams = CSAppCall.call("SS.AdcMebParamsSvc.getServiceParam", params);

                    elementInfo.put("ATTR_PARAM", plantSvcParams);
                    elementInfos.add(elementInfo);

                }
                result.put("ELEMENT_INFO", elementInfos);

            }
            result.put("PRODUCT_ID", grpProductId);
            result.put("BIZ_CTRL_TYPE", modifyTag);
            result.put("USER_ID", userIdA);// 集团用户标识
            result.put("SERIAL_NUMBER", mainSn);// 成员用户号码
        }

        return result;
    }

    // 变更里面变更元素 or 产品参数
    // 1,根据当前异网号码查询xxt获得资费 和 POINT_CODE(最终异网号码的资费) 比较,得出哪个是新增的资费,哪个是删除的资费
    // 2,比较得出来的新增资费如果在userInfoDiscnts(计费号码+集团userid查出)没有,则应该拼到ElementInfo里面
    // 3,比较得出来的删除资费,不需要拼到ElementInfo里面
    // 4,比较的出来的不变资费,不处理
    private IData getDiscntInfo2ChgMeb2ChgMeb(String mebProductId, IData data, IDataset mainSnXXTInfo) throws Exception
    {

        String disncts = data.getString("POINT_CODE");
        String grpUserId = data.getString("GRP_USER_ID");
        String mebUserId = data.getString("MEB_USER_ID"); // 成员usrId

        // 查询移动手机号订购某某集团的所有资费数据
        IDataset userInfoDiscntAll = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(mebUserId, grpUserId);
        // 排除有相同资费的数据
        IDataset userDiscntsList = DataHelper.distinct(userInfoDiscntAll, "DISCNT_CODE", ",");

        IDataset userInfoDiscntsClone = (IDataset) Clone.deepClone(mainSnXXTInfo);

        IDataset discntsList = DataUtil.dealDiscnts(disncts);

        if (IDataUtil.isEmpty(discntsList))// 变更异网号码资费,discntsList必须有值
            CSAppException.apperr(IntfPADCException.CRM_SAGM_11);

        IDataset elementInfos = new DatasetList();// elmentInfos

        IDataset xxtInfos = new DatasetList();// xxt

        for (int i = 0, iSize = discntsList.size(); i < iSize; i++)
        {

            IData elementInfo = new DataMap(); //

            IData xxtData = new DataMap(); // 操作xxt的资费

            String elementId = (String) discntsList.get(i);// 接口传进来的资费

            // 1.根据当前异网号码查询xxt获得资费 和 POINT_CODE(最终异网号码的资费) 比较,得出哪个是新增的资费,不变的资费
            IDataset newDiscntsXxt = DataHelper.filter(mainSnXXTInfo, "ELEMENT_ID=" + elementId);
            if (IDataUtil.isEmpty(newDiscntsXxt))
            {// 说明是新增资费,需要拼到elementInfo
                xxtData.put("DISCNT_CODE", elementId);
                xxtData.put("MODIFY_TAG", "0");
            }
            else
            {// 元素不变的
                xxtData.put("DISCNT_CODE", elementId);
                xxtData.put("MODIFY_TAG", "");
                userInfoDiscntsClone.remove(newDiscntsXxt.getData(0));
            }

            // 2,比较得出，新增资费如果在userInfoDiscntAll(计费号码+集团userid查出)没有,则应该拼到ElementInfo里面
            IDataset newDiscnts = DataHelper.filter(userDiscntsList, "DISCNT_CODE=" + elementId);
            if (IDataUtil.isEmpty(newDiscnts))
            {// 说明是新增资费,需要拼到elementInfo
                elementInfo.put("DISCNT_CODE", elementId);
                elementInfo.put("MODIFY_TAG", "0");
                elementInfo.put("ELEMENT_TYPE_CODE", "D");
                elementInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                elementInfo.put("START_DATE", SysDateMgr.getSysTime());
                elementInfo.put("PRODUCT_ID", "915001");
                elementInfo.put("PACKAGE_ID", "91500102");
                elementInfo.put("INST_ID", "");
                elementInfo.put("ELEMENT_ID", elementId);
                elementInfos.add(elementInfo);
            }

            xxtInfos.add(xxtData);
        }


        IDataset newDiscnts1 = DataHelper.filter(userDiscntsList, "DISCNT_CODE="+"5911");
        IData elementInfo = new DataMap();
        if (IDataUtil.isNotEmpty(newDiscnts1) && !discntsList.contains("5911"))
        {
            elementInfo.put("DISCNT_CODE", "5911");
            elementInfo.put("MODIFY_TAG", "1");
            elementInfo.put("ELEMENT_TYPE_CODE", "D");
            elementInfo.put("END_DATE", SysDateMgr.getSysTime());
            elementInfo.put("START_DATE", SysDateMgr.getSysTime());
            elementInfo.put("PRODUCT_ID", "915001");
            elementInfo.put("PACKAGE_ID", "91500102");
            elementInfo.put("INST_ID", "");
            elementInfo.put("ELEMENT_ID", "5911");
            elementInfos.add(elementInfo);

        }

        // 3.比较,得出哪些是要被删除的资费
        IDataset removeDisnct = new DatasetList();

        for (int k = 0, kSize = userInfoDiscntsClone.size(); k < kSize; k++)
        {

            IData xxtData = new DataMap(); // 操作xxt的资费

            IData userInfoDisnct = userInfoDiscntsClone.getData(k);

            String elementId = userInfoDisnct.getString("ELEMENT_ID");

            xxtData.put("DISCNT_CODE", elementId);
            xxtData.put("MODIFY_TAG", "1");

            removeDisnct.add(xxtData);

        }

        xxtInfos.addAll(removeDisnct);

        IData result = new DataMap();
        result.put("ELEMENT_INFOS", elementInfos);
        result.put("XXT_INOFS", xxtInfos);

        return result;
    }

    private IDataset getDiscntInfo2ChgMeb2AddMeb(String mebProductId, IData data, String modifTag) throws Exception
    {

        String disncts = data.getString("POINT_CODE");

        String mebUserId = data.getString("MEB_USER_ID");

        String grpUserId = data.getString("GRP_USER_ID");

        IDataset userInfoDiscnts = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(mebUserId, grpUserId);

        IDataset discntsList = DataUtil.dealDiscnts(disncts);

        IDataset results = new DatasetList();

        for (int i = 0, iSize = discntsList.size(); i < iSize; i++)
        {
            IData idata = new DataMap();

            String elementId = (String) discntsList.get(i);

            IDataset newDiscnts = DataHelper.filter(userInfoDiscnts, "DISCNT_CODE=" + elementId);

            if (IDataUtil.isNotEmpty(newDiscnts))
                continue;

            idata.put("DISCNT_CODE", elementId);
            idata.put("MODIFY_TAG", modifTag);
            idata.put("ELEMENT_TYPE_CODE", "D");
            idata.put("END_DATE", SysDateMgr.getTheLastTime());
            idata.put("START_DATE", SysDateMgr.getSysTime());
            idata.put("PRODUCT_ID", "915001");
            idata.put("PACKAGE_ID", "91500102");
            idata.put("INST_ID", "");
            idata.put("ELEMENT_ID", elementId);;

            results.add(idata);
        }

        return results;

    }

    private IDataset getDiscntInfo2ChgMeb2DelMeb(String mebProductId, IData data, String modifTag) throws Exception
    {

        String disncts = data.getString("POINT_CODE");

        String mebUserId = data.getString("MEB_USER_ID");

        String grpUserId = data.getString("GRP_USER_ID");


        IDataset userInfoDiscnts = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(mebUserId, grpUserId);
        IDataset discntsList = DataUtil.dealDiscnts(disncts);
        IDataset results = new DatasetList();

        IDataset newDiscnts1 = DataHelper.filter(userInfoDiscnts, "DISCNT_CODE="+"5911");
        IData elementInfo = new DataMap();
        if (IDataUtil.isNotEmpty(newDiscnts1) && discntsList.contains("5911"))
        {
            elementInfo.put("DISCNT_CODE", "5911");
            elementInfo.put("MODIFY_TAG", "1");
            elementInfo.put("ELEMENT_TYPE_CODE", "D");
            elementInfo.put("END_DATE", SysDateMgr.getSysTime());
            elementInfo.put("START_DATE", SysDateMgr.getSysTime());
            elementInfo.put("PRODUCT_ID", "915001");
            elementInfo.put("PACKAGE_ID", "91500102");
            elementInfo.put("INST_ID", "");
            elementInfo.put("ELEMENT_ID", "5911");
            results.add(elementInfo);

        }

        return results;

    }

    private IData getDiscntInfo(String mebProductId, IData data) throws Exception
    {
        String disncts = data.getString("POINT_CODE");

        IDataset discntsList = DataUtil.dealDiscnts(disncts);

        StringBuilder sb = new StringBuilder();

        for (int i = 0, iSize = discntsList.size(); i < iSize; i++)
        {
            String elementId = (String) discntsList.get(i);
            IDataset packages = PkgElemInfoQry.getElementByPIdElemId(mebProductId, "D", elementId, CSBizBean.getUserEparchyCode());
            if (IDataUtil.isEmpty(packages))
                continue;

            sb.append(mebProductId);
            sb.append(",");
            sb.append(packages.getData(0).getString("PACKAGE_ID"));
            sb.append(",");
            sb.append(elementId);
            if (i == iSize - 1)
                continue;
            sb.append(";");
        }

        IData result = new DataMap();
        result.put("DISCNT_CODE", sb.toString());

        /*
         * IData params = getProductParams(discntsList,data); result.put("PRODUCT_PARAM",params);
         */

        return result;

    }

    private IData getServInfo(String userId, String userIdA, String grpServId, String eparchyCode, String mebProductId) throws Exception
    {
        // 取集团服务和成员服务对应关系,根据集团产品服务找到对应的成员服务
        String mebSvcCode = MemParams.getmebServIdByGrpServId(grpServId);

        StringBuilder sb = new StringBuilder();

        IDataset packages = PkgElemInfoQry.getElementByPIdElemId(mebProductId, "S", mebSvcCode, CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(packages))
            return new DataMap();

        sb.append(mebProductId);
        sb.append(",");
        sb.append(packages.getData(0).getString("PACKAGE_ID"));
        sb.append(",");
        sb.append(mebSvcCode);

        IData result = new DataMap();
        result.put("SERVICE_CODE", sb.toString());

        return result;
    }

    /*
     * @description 成员个性化参数 1630
     * @author liaolc
     * @date 2014/07/08
     */
    public IData IAGWGrpMemBiz(IData data) throws Exception
    {
        IData result = new DataMap();

        result.put("IS_OUT_NET", false);//
        String modifyTag = "";// 新增,变更,退订,返回出去根据值看调哪个服务
        String oprCode = data.getString("OPR_CODE");
        String mainSn = data.getString("MOB_NUM");
        if (!"01".equals(oprCode) && !"02".equals(oprCode))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_02);
        }

        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userGrpInfo))
        {// 一、MOB_NUM网外号码
            result.put("IS_OUT_NET", true);
            DataUtil.getOutGroupAndProduct(data); // 获取用户、客户、集团用户的产品信息存入TD中

            String grpServId = data.getString("GRP_SERVICE_ID");
            String grpProductId = data.getString("GRP_PRODUCT_ID");
            String grpUserId = data.getString("GRP_USER_ID");
            String mebSvcCode = data.getString("MEB_SERVICE_ID");
            IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuseridSerid(mainSn, grpUserId, mebSvcCode);// 查sn在blackwhite的记录
            boolean isBW = IDataUtil.isNotEmpty(blackwhite) ? true : false;

            if ("01".equals(oprCode))
            {
                if (!isBW)// BW没记录 CrtMb
                {
                    modifyTag = BizCtrlType.CreateMember;
                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_08); // ("用户已在名单内")
                }
            }
            if ("02".equals(oprCode))
            {
                // uu记录,BW除了该服务记录 还有其它服务记录，说明是变更中退让一个服务
                if (isBW)
                {
                    modifyTag = BizCtrlType.DestoryMember;
                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
                }
            }
            // 拼服务参数数据----START---
            IData serviceInfo = new DataMap();

            serviceInfo.put("PRODUCT_ID", grpProductId);
            serviceInfo.put("SERVICE_ID", grpServId);
            // 查询该网外号码是否已经加入黑白名单
            if (IDataUtil.isEmpty(blackwhite) && BizCtrlType.CreateMember.equals(modifyTag) && "01".equals(oprCode))
            {
                serviceInfo.put("OPER_TYPE", oprCode); // 加入黑白名单
                serviceInfo.put("MEB_USER_ID", "-1"); // 成员USER_ID
                serviceInfo.put("MODIFY_TAG", "0"); // 新增
            }
            else
            {
                IData oldblackwhite = blackwhite.getData(0);
                serviceInfo.put("OPER_TYPE", oprCode); // 退出黑白名单
                serviceInfo.put("MEB_USER_ID", oldblackwhite.getString("USER_ID", "")); // 成员USER_ID
                serviceInfo.put("MODIFY_TAG", "1"); // 删除
            }
            // 拼服务参数数据----end------

            // IData serviceParamInfos = dealServiceParamInfo(data);
            result.put("PRODUCT_ID", grpUserId);// 集团产品ID
            result.put("MEB_USER_ID", "-1");// 成员USER_ID
            result.put("SERIAL_NUMBER", mainSn);// 成员用户号码
            result.put("USER_ID", grpUserId);// 集团用户标识
            result.put("SERVICE_INFOS", new DatasetList(serviceInfo));// 服务参数信息
            result.put("BIZ_CTRL_TYPE", modifyTag);
            result.put(Route.ROUTE_EPARCHY_CODE, "0898");
        }
        else
        {// 二、MOB_NUM 网内号码
            // 1,获取集团用户标识,校验集团,成员三户信息:必须传SERV_CODE,BIZ_CODE
            DataUtil.getGroupAndProduct(data);

            String userIdA = data.getString("GRP_USER_ID");
            String userId = data.getString("MEB_USER_ID"); // 成员usrId
            String grpProductId = data.getString("GRP_PRODUCT_ID");
            String grpServId = data.getString("GRP_SERVICE_ID");
            String memEparchyCode = data.getString("MEB_EPARCHY_CODE");
            String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(grpProductId);

            boolean isBB = DataUtil.isSnExistBB(userIdA, userId, grpProductId);// 判断用户relaBB关系isMemExist
            IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhitedataByUserIdEcuserid(userId, userIdA);// 查sn在blackwhite的记录
            boolean isBlackwhite = blackwhite.size() > 0 ? true : false;

            // 1 mob_num+ userIdA == uu没记录 && opr_code == 01 说明是成员新增createMb,其他opr_code抛错,==》不在白名单内。
            // 2, mob_num+ userIdA == bb有记录 && opr_code == 01 (新增) , ecuserid+mob_num+serviesId 查询BW,
            // 如果bw没有记录， changeMeb里面的新增服务. 如果bw有记录 报错 =》已在名单内。
            // 3, mob_num+ userIdA == bb有记录 && opr_code == 02 (退定) ，ecuserid+mob_num 查询BW，
            // 是不是只有一条,如果是，成员退订DstMb。如果不是,changeMeb里面的退订，==》用户不在名单内。
            if ("01".equals(data.getString("OPR_CODE")))
            {
                if (!isBB)// uu没记录 CrtMb
                {
                    modifyTag = BizCtrlType.CreateMember;
                }
                else if (isBB && !isBlackwhite) // uu有记录，bw没有记录changeMeb里面的新增服务
                {
                    modifyTag = BizCtrlType.ChangeMemberDis;
                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_08); // ("用户已在名单内")
                }
            }
            else if ("02".equals(data.getString("OPR_CODE")))
            {
                // uu记录,BW除了该服务记录 还有其它服务记录，说明是变更中退让一个服务（海南不在在变更时退订服务）
                if (isBB && isBlackwhite)
                {
                    modifyTag = BizCtrlType.DestoryMember;
                }
                else
                {
                    CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
                }
            }
            else
            {
                CSAppException.apperr(IntfIAGWException.CRM_IGU_11);// ("用户不在名单内")
            }
            IData servInfos = getServInfo(userId, userIdA, grpServId, memEparchyCode, memProductId);
            result.put("PRODUCT_ID", grpProductId);
            result.put("SERVICE_CODE", servInfos.getString("SERVICE_CODE"));

            result.put("BIZ_CTRL_TYPE", modifyTag);
            result.put("USER_ID", userIdA);// 集团用户标识
            result.put("BIZ_CTRL_TYPE", modifyTag);
            result.put("USER_ID", userIdA);// 集团用户标识
            result.put("SERIAL_NUMBER", mainSn);// 成员用户号码
        }

        return result;
    }

    /**
     * 学护卡反向接口数据处理
     *
     * @author
     * @param data
     * @return
     * @throws Exception
     */
    public IData subXfkGrpMemBiz(IData data) throws Exception
    {

        // 1,获取集团用户标识,校验集团,成员三户信息:必须传GROUP_ID,BIZ_CODE,opr_code 为03,04报错
        DataUtil.getPADCGroupAndProduct(data);

        String grpUserId = data.getString("GRP_USER_ID");
        String mebUserId = data.getString("MEB_USER_ID"); // 成员usrId
        String oprCode = data.getString("OPR_CODE");

        String mainSn = data.getString("FEE_MOB_NUM");
        String payMobNum = data.getString("PAY_MOB_NUM"); // 付费号码
        data.put("PAY_MOB_NUM", payMobNum);

        String memEparchyCode = data.getString("MEB_EPARCHY_CODE");
        String grpServId = data.getString("GRP_SERVICE_ID");
        String grpProductId = data.getString("GRP_PRODUCT_ID");

        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(grpProductId);

        if (!"01".equals(oprCode) && !"02".equals(oprCode) && !"05".equals(oprCode))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_01);
        }

        String bizCtrlType = ""; // 操作类型

        boolean isBB = DataUtil.isSnExistBB(grpUserId, mebUserId, grpProductId);// 判断用户relaBB关系isMemExist
        IDataset mainSnXXTInfo = null;

        String operCodeByOutSn = "";

        // 1, bb没记录 && opr_code == 01 是成员新增,opr_code为其他值,抛错:成员不在白名单内
        if (!isBB && (!"01".equals(oprCode)))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_06);
        }
        if (isBB && "01".equals(oprCode))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_05);
        }

        if (!isBB && ("01".equals(oprCode)))
        {// 成员新增
            bizCtrlType = BizCtrlType.CreateMember;
        }

        if (isBB && "02".equals(oprCode))
        {
            bizCtrlType = BizCtrlType.DestoryMember;
        }
        if (isBB && "05".equals(oprCode))
        {
            bizCtrlType = BizCtrlType.ChangeMemberDis;
        }

        IData result = new DataMap();// 返回结果

        if (BizCtrlType.CreateMember.equals(bizCtrlType))
        {// 成员新增
            // 格式：PRODUCT_ID,PACKAGE_ID,ELEMENT_ID ;
            IDataset servInfos = getXhkServInfo(mebUserId, grpUserId, grpServId, memEparchyCode, memProductId, data);
            result.put("PRODUCT_ID", grpProductId);
            // grp_product_id,ponint_code
            // 产品参数
            IDataset productParamInfos = DataUtil.dealXfkProductParamInfo(data, "0");
            IDataset discntInfos = getXhkDiscntInfo2ChgMeb2AddMeb(memProductId, data, "0");
            IDataset elementInfos = new DatasetList();
            elementInfos.addAll(discntInfos);
            elementInfos.addAll(servInfos);
            result.put("ELEMENT_INFO", elementInfos);
            result.put("PRODUCT_PARAM_INFO", productParamInfos);

        }
        else if (BizCtrlType.ChangeMemberDis.equals(bizCtrlType))
        {// 变更
            IDataset productParamInfos = null;
            result.put("PRODUCT_ID", grpProductId); // 10009150-ADC校讯通（新）
            productParamInfos = DataUtil.dealXfkProductParamInfo(data, "0");

            result.put("PRODUCT_PARAM_INFO", productParamInfos);
        }

        result.put("BIZ_CTRL_TYPE", bizCtrlType);

        result.put("USER_ID", grpUserId);// 集团用户标识
        result.put("SERIAL_NUMBER", mainSn);// 成员用户号码

        return result;
    }

    private IDataset getXhkDiscntInfo2ChgMeb2AddMeb(String mebProductId, IData data, String modifTag) throws Exception
    {

        String disncts = data.getString("POINT_CODE");

        String mebUserId = data.getString("MEB_USER_ID");

        String grpUserId = data.getString("GRP_USER_ID");

        IDataset userInfoDiscnts = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(mebUserId, grpUserId);

        IDataset discntsList = DataUtil.dealDiscnts(disncts);

        IDataset results = new DatasetList();

        for (int i = 0, iSize = discntsList.size(); i < iSize; i++)
        {
            IData idata = new DataMap();

            String elementId = (String) discntsList.get(i);

            IDataset newDiscnts = DataHelper.filter(userInfoDiscnts, "DISCNT_CODE=" + elementId);

            if (IDataUtil.isNotEmpty(newDiscnts))
                continue;

            idata.put("DISCNT_CODE", elementId);
            idata.put("MODIFY_TAG", modifTag);
            idata.put("ELEMENT_TYPE_CODE", "D");
            idata.put("END_DATE", SysDateMgr.getTheLastTime());
            idata.put("START_DATE", SysDateMgr.getSysTime());
            idata.put("PRODUCT_ID", "574401");
            idata.put("PACKAGE_ID", "57440102");
            idata.put("INST_ID", "");
            idata.put("ELEMENT_ID", elementId);;

            results.add(idata);
        }

        return results;

    }

    private IDataset getXhkServInfo(String userId, String userIdA, String grpServId, String eparchyCode, String mebProductId, IData data) throws Exception
    {
        // 取集团服务和成员服务对应关系,根据集团产品服务找到对应的成员服务
        String mebSvcCode = MemParams.getmebServIdByGrpServId(grpServId);

        IDataset packages = PkgElemInfoQry.getElementByPIdElemId(mebProductId, "S", mebSvcCode, CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(packages))
            return new DatasetList();

        IDataset results = new DatasetList();

        IData idata = new DataMap();
        idata.put("MODIFY_TAG", "0");
        idata.put("ELEMENT_TYPE_CODE", "S");
        idata.put("END_DATE", SysDateMgr.getTheLastTime());
        idata.put("START_DATE", SysDateMgr.getSysTime());
        idata.put("PRODUCT_ID", mebProductId);
        idata.put("PACKAGE_ID", packages.getData(0).getString("PACKAGE_ID"));
        idata.put("INST_ID", "");
        idata.put("ELEMENT_ID", mebSvcCode);

        IDataset paramList = new DatasetList();
        IData platsvc = new DataMap();
        platsvc.put("pam_BIZ_IN_CODE", data.getString("BIZ_SERV_CODE"));
        platsvc.put("pam_OPER_STATE", "01");
        platsvc.put("pam_SERVICE_ID", mebSvcCode);
        platsvc.put("pam_BIZ_CODE", "AHI3911605");
        platsvc.put("pam_BIZ_IN_CODE_A", "01");
        platsvc.put("pam_EXPECT_TIME", SysDateMgr.getSysTime());
        platsvc.put("pam_BIZ_ATTR", "2");
        platsvc.put("pam_PLAT_SYNC_STATE", "1");
        platsvc.put("pam_MODIFY_TAG", "0");
        platsvc.put("pam_BIZ_NAME", "学护卡");
        platsvc.put("pam_GRP_PLAT_SYNC_STATE", "1");

        IData param = new DataMap();
        param.put("ID", mebSvcCode);
        param.put("PLATSVC", platsvc);
        param.put("CANCLE_FLAG", false);

        paramList.add(param);

        idata.put("ATTR_PARAM", paramList);

        results.add(idata);

        // 主服务
        IData mainIdata = new DataMap();
        mainIdata.put("MODIFY_TAG", "0");
        mainIdata.put("ELEMENT_TYPE_CODE", "S");
        mainIdata.put("END_DATE", SysDateMgr.getTheLastTime());
        mainIdata.put("START_DATE", SysDateMgr.getSysTime());
        mainIdata.put("PRODUCT_ID", mebProductId);
        mainIdata.put("PACKAGE_ID", packages.getData(0).getString("PACKAGE_ID"));
        mainIdata.put("INST_ID", "");
        mainIdata.put("ELEMENT_ID", "10003501");

        results.add(mainIdata);

        return results;
    }
    
    private void checkstu(IDataset productParamInfos,IDataset grouplist) throws Exception{
    	if(!IDataUtil.isEmpty(productParamInfos) && !IDataUtil.isEmpty(grouplist)){
    		IDataset stuParamlist = productParamInfos.getData(0).getDataset("PRODUCT_PARAM");
    		IDataset stuParamset = new DatasetList();
    		for (int i = 0; i < stuParamlist.size(); i++) {
    			String str = stuParamlist.getData(i).getString("ATTR_CODE");
    			System.out.println("chenhh==str"+str);
    			if ("NOTIN_STU_PARAM_LIST0".equals(str)) {
    				stuParamset = stuParamlist.getData(i).getDataset("ATTR_VALUE");
				}
			}
    		
    		
    		//String stuParam = productParamInfos.getData(0).getData("PRODUCT_PARAM").getString("NOTIN_STU_PARAM_LIST0");
    		//IDataset stuParamset = new DatasetList(stuParam);
    		if (!IDataUtil.isEmpty(stuParamset)) {
				for (int i = 0; i < stuParamset.size(); i++) {
					IData strdiscnt = stuParamset.getData(0);
					String elementId = strdiscnt.getString("ELEMENT_ID");
					String stuKey = strdiscnt.getString("STUD_KEY");
					
		            IDataset newDiscntsXxt = DataHelper.filter(grouplist, "ELEMENT_ID=" + elementId);
		            if (!IDataUtil.isEmpty(newDiscntsXxt))
		            {//说明该计费号码已在其它集团订购过学生套餐
		            	if ("stu_name1".equals(stuKey)) {
		            		CSAppException.apperr(IntfPADCException.CRM_SAGM_99,"计费号码的学生一已经在其它学校订购，不能重复订购！");
		    			}
		            	if ("stu_name2".equals(stuKey)) {
		            		CSAppException.apperr(IntfPADCException.CRM_SAGM_99,"计费号码的学生二已经在其它学校订购，不能重复订购！");
		    			}
		            	if ("stu_name3".equals(stuKey)) {
		            		CSAppException.apperr(IntfPADCException.CRM_SAGM_99,"计费号码的学生三已经在其它学校订购，不能重复订购！");
		    			}
		            	if ("stu_name4".equals(stuKey)) {
		            		CSAppException.apperr(IntfPADCException.CRM_SAGM_99,"计费号码的学生四已经在其它学校订购，不能重复订购！");
		    			}
		            	if ("stu_name5".equals(stuKey)) {
		            		CSAppException.apperr(IntfPADCException.CRM_SAGM_99,"计费号码的学生五已经在其它学校订购，不能重复订购！");
		    			}
		            	if ("stu_name6".equals(stuKey)) {
		            		CSAppException.apperr(IntfPADCException.CRM_SAGM_99,"计费号码的学生六已经在其它学校订购，不能重复订购！");
		    			}
		            }
				}
			}
    	}
    }
}
