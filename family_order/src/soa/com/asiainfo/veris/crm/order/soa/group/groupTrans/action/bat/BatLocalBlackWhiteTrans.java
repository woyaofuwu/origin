
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.IntfIAGWException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class BatLocalBlackWhiteTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 初始化数据
        InitialDataSub(batData);

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);

        // 根据条件判断调用服务
        setSVC(batData);
    }
    /**
     * 检查用户是否具有办理资格。 携号转网用户 不允许办理
     *
     * @return
     */
    private boolean checkUserLegalTrans(IData batData) throws Exception
    {
        boolean flag = true;

        IDataset userDs = UserInfoQry.getLatestUserInfosBySerialNumber(batData.getString("SERIAL_NUMBER"));

        if (IDataUtil.isNotEmpty(userDs))
        {

            String userIdB = userDs.get(0, "USER_ID").toString();// 根据号码获取开户时间最大的user_id

            IDataset npDs = UserNpInfoQry.qryUserNpInfosByUserId(userIdB);
            if (npDs != null && npDs.size() > 0)
            {
                if (!npDs.get(0, "PORT_IN_NETID").toString().equals(npDs.get(0, "HOME_NETID").toString()))
                {
                    flag = false;
                }
            }
        }

        return flag;
    }
    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        String oprCode = IDataUtil.chkParam(condData, "OPER_CODE");
        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        String product_id =IDataUtil.getMandaData(condData, "PRODUCT_ID");//集团产品ID

        IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuserid(mainSn, grpUserId);// 查sn在blackwhite的记录
        boolean isBW = IDataUtil.isNotEmpty(blackwhite) ? true : false;

        IData userGrpInfo = UcaInfoQry.qryUserInfoBySn(mainSn);// 查询remove_tag为0的数据
        

        if ("01".equals(oprCode))
        {
            if (isBW)
            {
                CSAppException.apperr(IntfIAGWException.CRM_IGU_08); // ("用户已在名单内")
            }
            else
            {
                if (IDataUtil.isEmpty(userGrpInfo))// 新增判断，如果用户bb表没有资料，也作为往外号码处理
                {// 网外号码

                    IData msisdninfo = MsisdnInfoQry.getCrmMsisonBySerialnumber(mainSn);
                    if ("1".equals(msisdninfo.getString("ASP")))// 根据号段表判断是否是移动号码 1是移动号
                    {
                        IDataset userInfo = UserInfoQry.getUserInfoBySn(mainSn, "7");
                        if (IDataUtil.isNotEmpty(userInfo))
                        {
                            condData.put("IS_OUT_NET", true);
                        }
                        else
                        {
                            if("3".equals(msisdninfo.getString("HOME_TYPE")))//非本省的用户
                            {
                                condData.put("IS_OUT_NET", true);



                            }else if ("1".equals(msisdninfo.getString("HOME_TYPE"))){//本省的用户

                                // 对方为协号用户不允许办理
                                if (!this.checkUserLegalTrans(batData))//是携转用户
                                {
                                    condData.put("IS_OUT_NET", true);
                                }else{//非携转用户
                                    CSAppException.apperr(IntfIAGWException.CRM_IGU_01); // (用户手机号码错误)

                                }

                            }
                            else
                            {
                                CSAppException.apperr(IntfIAGWException.CRM_IGU_01); // (用户手机号码错误)
                            }
                        }

                    }
                    else
                    {
                        condData.put("IS_OUT_NET", true);

                    }
                }

            }
        }
        else if ("02".equals(oprCode))//退出
        {
            if (!isBW)
            {
                CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")

            }
            else
            {
                IDataset UserbbInfo = RelaBBInfoQry.getBBInfoByUserIdAB(grpUserId, blackwhite.getData(0).getString("USER_ID", ""));
                if (IDataUtil.isEmpty(UserbbInfo))
                {
                    condData.put("IS_OUT_NET", true);
                }

            }
        }
        else if ("08".equals(oprCode) && "10009805".equals(product_id))// 如果是变更，则需要原先的自费截止，新套餐立即生效
        {
            
            if (!isBW)
            {
                CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")

            }
            String selectedElements = condData.getString("SELECTED_ELEMENTS");
            IDataset selectedElementslist = new DatasetList(selectedElements);
            IDataset userDiscnt = new DatasetList();
            
            userDiscnt = UserDiscntInfoQry.getUserDiscntByUserIdAB(userGrpInfo.getString("USER_ID"), grpUserId);
            IDataset userInfoDiscntsClone = (IDataset) Clone.deepClone(userDiscnt);
            for (int i = 0; i < userDiscnt.size(); i++)
            {
                String oriDiscnt = userDiscnt.getData(i).getString("DISCNT_CODE");
                IDataset newDiscnts = DataHelper.filter(selectedElementslist, "ELEMENT_ID=" + oriDiscnt);
                // 如果原先已经有这个套餐了，就把这个套餐删掉,同时也把克隆的用户资料中的删掉，这样克隆的用户资料中剩下的就是需要删除的
                if(IDataUtil.isNotEmpty(newDiscnts))
                {
                    selectedElementslist.remove(newDiscnts.getData(0));
                    userInfoDiscntsClone.remove(userDiscnt.getData(i));
                }
            }
            
            for (int k = 0; k < userInfoDiscntsClone.size(); k++)
            {
                IData elementInfo = new DataMap();
                elementInfo.put("DISCNT_CODE", userInfoDiscntsClone.getData(k).getString("DISCNT_CODE"));
                elementInfo.put("MODIFY_TAG", "1");
                elementInfo.put("ELEMENT_TYPE_CODE", "D");
                elementInfo.put("END_DATE", SysDateMgr.getSysTime());
                elementInfo.put("START_DATE", SysDateMgr.getSysTime());
                elementInfo.put("PRODUCT_ID", userInfoDiscntsClone.getData(k).getString("PRODUCT_ID"));
                elementInfo.put("PACKAGE_ID", userInfoDiscntsClone.getData(k).getString("PACKAGE_ID"));
                elementInfo.put("INST_ID", userInfoDiscntsClone.getData(k).getString("INST_ID"));
                elementInfo.put("ELEMENT_ID", userInfoDiscntsClone.getData(k).getString("DISCNT_CODE"));
                selectedElementslist.add(elementInfo);

            }

            IDataset userSvc = new DatasetList();
            userSvc=UserSvcInfoQry.getUserProductSvc(userGrpInfo.getString("USER_ID"), grpUserId, null);
            IDataset userInfoSvcsClone = (IDataset) Clone.deepClone(userSvc);
            
            for (int i = 0; i < userSvc.size(); i++)
            {
                String oriSvc = userSvc.getData(i).getString("SERVICE_ID");
                IDataset newSvcs = DataHelper.filter(selectedElementslist, "ELEMENT_ID=" + oriSvc);
                // 如果原先已经有这个服务了，就把这个服务删掉
                if(IDataUtil.isNotEmpty(newSvcs))
                {
                    IDataset tempLists = AttrBizInfoQry.getBizAttrByDynamic(oriSvc, "S", oriSvc, "SvcBindDiscnt", null);
                    if(IDataUtil.isNotEmpty(tempLists))
                    {
                        IDataset newDiscnts = DataHelper.filter(selectedElementslist, "ELEMENT_ID=" + tempLists.getData(0).getString("ATTR_VALUE"));
                        if(IDataUtil.isNotEmpty(newDiscnts))
                        {
                             int j= selectedElementslist.indexOf(newSvcs.getData(0));
                             selectedElementslist.getData(j).put("MODIFY_TAG", 2);
                             selectedElementslist.getData(j).put("INST_ID", userSvc.getData(i).getString("INST_ID"));
                             IDataset  attr=IDataUtil.getDataset(selectedElementslist.getData(j), "ATTR_PARAM");
                             IData platsvc=IDataUtil.getData(attr.getData(1), "PLATSVC");
                             platsvc.put("pam_OPER_STATE", "08");
                             userInfoSvcsClone.remove(userSvc.getData(i));
                        }
                        else 
                        {
                            userInfoSvcsClone.remove(userSvc.getData(i));
                            selectedElementslist.remove(newSvcs.getData(0));
                            
                        }
                    }
                    else
                    {
                        userInfoSvcsClone.remove(userSvc.getData(i));
                        selectedElementslist.remove(newSvcs.getData(0));
                        
                    } 
                    
                }
            }
            
            for (int k = 0; k < userInfoSvcsClone.size(); k++)
            {
                IData elementInfo = new DataMap();
                elementInfo.put("INST_ID", userInfoSvcsClone.getData(k).getString("INST_ID"));
                elementInfo.put("START_DATE", SysDateMgr.getSysTime());
                elementInfo.put("ELEMENT_TYPE_CODE", "S");
                elementInfo.put("MODIFY_TAG", "1");
                elementInfo.put("PRODUCT_ID",  userInfoSvcsClone.getData(k).getString("PRODUCT_ID"));
                elementInfo.put("END_DATE", SysDateMgr.getSysTime());
                elementInfo.put("PACKAGE_ID", userInfoSvcsClone.getData(k).getString("PACKAGE_ID"));
                elementInfo.put("ELEMENT_ID", userInfoSvcsClone.getData(k).getString("SERVICE_ID"));
                selectedElementslist.add(elementInfo);

            }
            condData.put("ELEMENT_INFO", selectedElementslist);
        }
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());

        boolean isOutNet = condData.getBoolean("IS_OUT_NET", false);
        String sProduct =IDataUtil.getMandaData(condData, "PRODUCT_ID");
        if("10009805".equals(sProduct))
        {
        	svcData.put("PAGE_SELECTED_TC", IDataUtil.getMandaData(condData, "PAGE_SELECTED_TC"));
        }
        if (isOutNet)
        {// 网外号码

            IData serviceInfo = dealServiceParamInfo(batData);
            svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));// 集团产品ID
            svcData.put("MEB_USER_ID", serviceInfo.getString("MEB_USER_ID", "-1"));// 成员USER_ID
            svcData.put("SERIAL_NUMBER", IDataUtil.chkParam(batData, "SERIAL_NUMBER"));// 成员用户号码
            svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团用户标识
            svcData.put("SERVICE_INFOS", new DatasetList(serviceInfo));// 服务参数信息
            svcData.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
        }
        else
        {
            svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团USER_ID
            svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER"));// 成员SN
            svcData.put("MEM_ROLE_B", condData.getString("PLAN_TYPE_CODE", "1"));// 成员角色
            svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE_CODE", "P"));// 个人付款
            svcData.put("RES_INFO", condData.getDataset("RES_INFO", new DatasetList("[]")));
            svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "true"));
            svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID", ""));// 集团产品ID
            if("10009805".equals(condData.getString("PRODUCT_ID", "")) &&"08".equals(IDataUtil.chkParam(condData, "OPER_CODE")))
            {
                svcData.put("ELEMENT_INFO", condData.getDataset("ELEMENT_INFO", new DatasetList("[]")));
            }
            else
            {
                svcData.put("ELEMENT_INFO", condData.getDataset("SELECTED_ELEMENTS", new DatasetList("[]")));
                
            }
            svcData.put("PRODUCT_PARAM_INFO", condData.getDataset("PRODUCT_PARAM_INFO", new DatasetList("[]")));// 产品参数
        }

    }

    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception
    {
        String svcName = "";
        IData condData = batData.getData("condData", new DataMap());
        IData svcData = batData.getData("svcData", new DataMap());

        boolean isOutNet = condData.getBoolean("IS_OUT_NET", false);
        String operType = IDataUtil.chkParam(condData, "OPER_CODE");

        if (isOutNet)
        {// 网外号码处理
            svcName = "SS.MgrBlackWhiteOutSVC.crtTrade";
        }
        else
        {
            if ("01".equals(operType))
            {// 成员新增
                svcName = "CS.CreateGroupMemberSvc.createGroupMember";
            }
            else if ("02".equals(operType))
            {// 成员删除
                svcName = "CS.DestroyGroupMemberSvc.destroyGroupMember";
            }
            else
            {// 成员变更
                svcName = "CS.ChangeMemElementSvc.changeMemElement";
            }
        }

        svcData.put("REAL_SVC_NAME", svcName);
    }

    /**
     * 初始化数据 1, 批量黑白名单导入,INDIGRPUSERREG;--->SubADCGrpMemDiscntBiz("BIP4B248_T2101709_1_0"), //省内ADC 行业应用黑白名单同步业务(3.0版本)
     * 
     * @param batData
     */
    public void InitialDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IDataset mebServIdList = new DatasetList();
        String selectedElements = condData.getString("SELECTED_ELEMENTS");
        IDataset selectedElementslist = new DatasetList(selectedElements);

        for (int i = 0; i < selectedElementslist.size(); i++)
        {
            String mebServId = selectedElementslist.getData(i).getString("ELEMENT_ID");
            String elementTypeCode = selectedElementslist.getData(i).getString("ELEMENT_TYPE_CODE");
            String attrParam = selectedElementslist.getData(i).getString("ATTR_PARAM", "");

            if ("S".equals(elementTypeCode) && StringUtils.isNotBlank(attrParam))
            {// 网外号码用
                mebServIdList.add(mebServId);
            }
        }

        condData.put("MEB_SERVICE_ID_LIST", mebServIdList);
    }

    /**
     * liaolc 2014-7-26 作用：见外号服务参数串 SERVICE_INFOS[{}]
     */
    public static IData dealServiceParamInfo(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        IDataset MebServIdList = condData.getDataset("MEB_SERVICE_ID_LIST");
        String oprCode = IDataUtil.chkParam(condData, "OPER_CODE");
        String grpProductId = IDataUtil.chkParam(condData, "PRODUCT_ID");
        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        String grpServId = "";

        IData serviceInfo = new DataMap();
        IDataset blackWhiteInfo = new DatasetList();

        // 查询该网外号码是否已经加入黑白名单
        for (int i = 0, iSize = MebServIdList.size(); i < iSize; i++)
        {
            String mebServId = (String) MebServIdList.get(i);
            grpServId = MemParams.getmebServIdByGrpServId(mebServId);

            blackWhiteInfo = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuseridSerid(mainSn, grpUserId, mebServId);// 查sn在blackwhite的记录
            if (IDataUtil.isNotEmpty(blackWhiteInfo))
            {
                break;
            }
        }

        if (IDataUtil.isNotEmpty(blackWhiteInfo) && !"01".equals(oprCode))
        {
            IData oldblackwhite = blackWhiteInfo.getData(0);
            serviceInfo.put("OPER_TYPE", oprCode); // 退出黑白名单
            serviceInfo.put("MEB_USER_ID", oldblackwhite.getString("USER_ID", "")); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "1"); // 删除
        }

        else
        {
            serviceInfo.put("OPER_TYPE", oprCode); // 加入黑白名单
            serviceInfo.put("MEB_USER_ID", "-1"); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "0"); // 新增
        }
        serviceInfo.put("PRODUCT_ID", grpProductId);
        serviceInfo.put("SERVICE_ID", grpServId);

        return serviceInfo;
    }
}
