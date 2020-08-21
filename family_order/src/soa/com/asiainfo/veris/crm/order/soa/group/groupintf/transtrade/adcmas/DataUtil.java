
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.adcmas;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IntfIAGWException;
import com.asiainfo.veris.crm.order.pub.exception.IntfPADCException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class DataUtil
{
    /**
     * 作用：根据传入的BIZ_CODE、GROUP_ID获取集团用户信息、产品信息
     *
     * @author liaolc 2014-07-10
     * @param data
     * @return
     * @throws Exception
     */
    public static void getPADCGroupAndProduct(IData data) throws Exception
    {
        // 1. 查询集团定购产品信息
        String mainSn = data.getString("FEE_MOB_NUM", "");
        String servCode = data.getString("BIZ_SERV_CODE", "");
        String groupId = data.getString("ECID", "");
        if (StringUtils.isEmpty(mainSn))
            CSAppException.apperr(IntfPADCException.CRM_SAGM_03);
        if (StringUtils.isEmpty(groupId))
            CSAppException.apperr(IntfPADCException.CRM_SAGM_10);
        if (StringUtils.isBlank(servCode))
            CSAppException.apperr(IntfPADCException.CRM_SAGM_02);

        IDataset serDatas = UserGrpPlatSvcInfoQry.getuserPlatsvcbygroupidservcode(servCode, groupId);

        if (IDataUtil.isEmpty(serDatas))
            CSAppException.apperr(IntfPADCException.CRM_SAGM_10);
        IData pltSvc = serDatas.getData(0);

        String grpServiceId = pltSvc.getString("SERVICE_ID", "");
        String grpUserId = pltSvc.getString("USER_ID", "");
        // 2. 取集团服务和成员服务对应关系,根据集团产品服务找到对应的成员服务
        String mebServId = MemParams.getmebServIdByGrpServId(grpServiceId);

        // 3. 查询集团产品用户信息
        IData grpUserInfo = getUserInfoByUserId(grpUserId);

        // 4. 获取手机号码用户信息
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(grpUserInfo.getString("PRODUCT_ID", ""), "DstMb");
        String grp_mebcheckflag = ctrlInfo.getAttrValue("GRP_MEBCHECKFLAG");
        
        IData mebUserInfo = null;
        if (!StringUtils.equals("true", grp_mebcheckflag))// true表示不需要校验成员状态
        {
            mebUserInfo = getUserInfoByNumber(mainSn);
        } else {
            mebUserInfo = getAllUserInfoByNumber(mainSn);
        }
          //如果没有用户资料可能该用户已经携号转网了
        if (IDataUtil.isEmpty(mebUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_917);
        }

        data.put("GRP_SERVICE_ID", grpServiceId);
        data.put("MEB_SERVICE_ID", mebServId);
        data.put("GRP_PRODUCT_ID", grpUserInfo.getString("PRODUCT_ID", ""));
        data.put("GRP_USER_ID", grpUserInfo.getString("USER_ID", ""));
        data.put("MEB_USER_ID", mebUserInfo.getString("USER_ID", ""));
        data.put("MEB_EPARCHY_CODE", mebUserInfo.getString("EPARCHY_CODE"));
    }

    /**
     * 作用：根据传入的BIZ_CODE、SERV_CODE获取集团用户信息、产品信息、手机号码信息
     *
     * @author liaolc 2014-07-10
     * @param data
     * @return
     * @throws Exception
     */
    public static void getGroupAndProduct(IData data) throws Exception
    {
        // 1. 查询集团定购产品信息
        String mainSn = data.getString("MOB_NUM", "");
        String bizCode = data.getString("BIZ_CODE", "");
        String servCode = data.getString("SERV_CODE", "");
        if (StringUtils.isEmpty(mainSn))
            CSAppException.apperr(IntfIAGWException.CRM_IGU_01);
        if (StringUtils.isBlank(bizCode))
            CSAppException.apperr(IntfIAGWException.CRM_IGU_04);
        if (StringUtils.isBlank(servCode))
            CSAppException.apperr(IntfIAGWException.CRM_IGU_05);

        IDataset serDatas = UserGrpPlatSvcInfoQry.getuserPlatsvcbybizcodeservcode(bizCode, servCode);
        if (IDataUtil.isEmpty(serDatas))
            CSAppException.apperr(IntfIAGWException.CRM_IGU_07);
        IData pltSvc = serDatas.getData(0);

        String grpServiceId = pltSvc.getString("SERVICE_ID", "");
        String grpUserId = pltSvc.getString("USER_ID", "");
        // 2. 取集团服务和成员服务对应关系,根据集团产品服务找到对应的成员服务
        String mebServId = MemParams.getmebServIdByGrpServId(grpServiceId);

        // 3. 查询集团产品用户信息
        IData grpUserInfo = getUserInfoByUserId(grpUserId);
        
        // 4. 获取手机号码用户信息
        IData mebUserInfo = getUserInfoByNumber(mainSn);
        
        data.put("GRP_SERVICE_ID", grpServiceId);
        data.put("MEB_SERVICE_ID", mebServId);
        data.put("GRP_PRODUCT_ID", grpUserInfo.getString("PRODUCT_ID", ""));
        data.put("GRP_USER_ID", grpUserInfo.getString("USER_ID", ""));
        data.put("MEB_USER_ID", mebUserInfo.getString("USER_ID", ""));
        data.put("MEB_EPARCHY_CODE", mebUserInfo.getString("EPARCHY_CODE"));
    }

    /**
     * 作用:根据USER_ID查询用户信息
     *
     * @author liaolc
     * @param pd
     * @param userId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    protected static IData getUserInfoByUserId(String userId) throws Exception
    {
        String removeTag = "0";
        IData grpUserData = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, removeTag); // 查询集团用户信息
        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(IntfPADCException.CRM_SAGM_10);
        }
        return grpUserData;
    }

    /**
     * 作用：查询用户相关信息
     *
     * @author liaolc 2013-08-17
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    private static IData getUserInfoByNumber(String serialNumber) throws Exception
    {
        IData userLists = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userLists)|| (!"0".equals(userLists.getString("USER_STATE_CODESET"))&&!"N".equals(userLists.getString("USER_STATE_CODESET")) &&!"00".equals(userLists.getString("USER_STATE_CODESET"))))//0开通,N信用有效时长开通
        {
        	CSAppException.apperr(IntfPADCException.CRM_SAGM_03);
        }
        return userLists;
    }
    
    private static IData getAllUserInfoByNumber(String serialNumber) throws Exception
    {
        IData userLists = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
        return userLists;
    }

    /**
     * 判断用户uu关系 成员加入:uu关系存在时,tf_b_trade表的RSRV_STR10字段填1.成员退出:uu关系不存在时报错
     *
     * @author liaolc
     * @param td
     * @param data
     * @throws Exception
     */
    public static boolean isSnExistBB(String grpUserId, String mebUserInfo, String productId) throws Exception
    {
        IData uuData = new DataMap();
        uuData.put("USER_ID", grpUserId);
        uuData.put("USER_ID_B", mebUserInfo);
        uuData.put("PRODUCT_ID", productId);
        uuData.put("CHECK_TAG", "-1");
        IDataset dataset = ParamInfoQry.getProductUU(uuData);
        if ("0".equals(dataset.getData(0).get("RECORDCOUNT")))
            return false;
        else
            return true;
    }

    /**
     * 判断异网号码是否已经在该集团下
     *
     * @param data
     * @param Sn
     * @return
     * @throws Exception
     */
    public static boolean isSnExistBW(IData data, String sn) throws Exception
    {
        boolean isBwSn = false;
        String grpUserId = data.getString("GRP_USER_ID");
        String mebservId = data.getString("MEB_SERVICE_ID");
        IDataset dataset = UserBlackWhiteInfoQry.qryBlackWhiteBySnEcuserIdGropIdServId(grpUserId, mebservId, sn);
        if (IDataUtil.isNotEmpty(dataset))
        {
            isBwSn = true;
        }
        return isBwSn;
    }

    /**
     * 校讯通新增，判断一个异网号码是不是该付费号码下的最后一个号码
     *
     * @param pd
     * @param strSerialNumber
     *            缴费号码
     * @return
     * @throws Exception
     * @author chenlei
     */
    public static boolean isLastOutSnExistXXT(IData data, String sn) throws Exception
    {
        boolean isLastSn = false;

        String grpUserId = data.getString("GRP_USER_ID");
        IDataset mainSnXXTInfo = RelaXxtInfoQry.queryMemInfoBySNandUserIdA(sn, grpUserId);
        if (mainSnXXTInfo.size() == 1)
        {
            isLastSn = true;
        }
        return isLastSn;
    }

    /**
     * 查询异网号码与付费号码有UU关系
     *
     * @param data
     * @param sn
     * @return
     * @throws Exception
     */
    public static boolean isOutSnExistUU(String outSn, String mebUserId,String GrpUserid) throws Exception
    {
        boolean isOutSnUU = false;

        IDataset outSnUUInfo = RelaUUInfoQry.qryXxtUUInfo(outSn, mebUserId, GrpUserid);
        if (IDataUtil.isNotEmpty(outSnUUInfo))
        {
            isOutSnUU = true;
        }
        return isOutSnUU;
    }

    /**
     * 拼成产品参数页面串,里有ELEMENT_INFO(IDataset) "PRODUCT_PARAM_INFO":[{"PRODUCT_ID":"10005742","PRODUCT_PARAM":[{},{}]}],
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset dealProductParamInfo(IData data, String modifTag) throws Exception
    {

        IDataset productParamInfo = new DatasetList();
        IData productParam = new DataMap();

        IDataset paramInfo = new DatasetList();

        IData studentData = new DataMap();

        String grpProductId = data.getString("GRP_PRODUCT_ID", "");

        String point_Code = data.getString("POINT_CODE", "");
        IDataset discntsList = dealDiscnts(point_Code);

        IDataset stuParamList = new DatasetList();


        for (int i = 0, iSize = discntsList.size(); i < iSize; i++)
        {

            IData strdiscnt1 = new DataMap();
            IData strdiscnt2 = new DataMap();
            IData strdiscnt3 = new DataMap();
            IData strdiscnt4 = new DataMap();
            IData strdiscnt5 = new DataMap();
            IData strdiscnt6 = new DataMap();

            String elementId = discntsList.get(i).toString();
            // 取得资费名称
            IData params = UDiscntInfoQry.getDiscntInfoByPk(elementId);
            String str = params.getString("RSRV_STR2");

            if (str.startsWith("group_1"))
            {
                strdiscnt1.put("STUD_KEY", "stu_name1");
                strdiscnt1.put("STUD_NAME", data.getString("stu_name1"));
                strdiscnt1.put("ELEMENT_ID", elementId);
                strdiscnt1.put("tag", modifTag);
                stuParamList.add(strdiscnt1);

            }
            else if (str.startsWith("group_2"))
            {
                strdiscnt2.put("STUD_KEY", "stu_name2");
                strdiscnt2.put("STUD_NAME", data.getString("stu_name2"));
                strdiscnt2.put("ELEMENT_ID", elementId);
                strdiscnt2.put("tag", modifTag);
                stuParamList.add(strdiscnt2);
            }

            else if (str.startsWith("group_3"))
            {
                strdiscnt3.put("STUD_KEY", "stu_name3");
                strdiscnt3.put("STUD_NAME", data.getString("stu_name3"));
                strdiscnt3.put("ELEMENT_ID", elementId);
                strdiscnt3.put("tag", modifTag);
                stuParamList.add(strdiscnt3);
            }
            else if (str.startsWith("group_4"))
            {
            	strdiscnt4.put("STUD_KEY", "stu_name4");
            	strdiscnt4.put("STUD_NAME", data.getString("stu_name4"));
            	strdiscnt4.put("ELEMENT_ID", elementId);
            	strdiscnt4.put("tag", modifTag);
                stuParamList.add(strdiscnt4);
            }
            else if (str.startsWith("group_5"))
            {
            	strdiscnt5.put("STUD_KEY", "stu_name5");
            	strdiscnt5.put("STUD_NAME", data.getString("stu_name5"));
            	strdiscnt5.put("ELEMENT_ID", elementId);
            	strdiscnt5.put("tag", modifTag);
                stuParamList.add(strdiscnt5);
            }
            else if (str.startsWith("group_6"))
            {
            	strdiscnt6.put("STUD_KEY", "stu_name6");
            	strdiscnt6.put("STUD_NAME", data.getString("stu_name6"));
            	strdiscnt6.put("ELEMENT_ID", elementId);
            	strdiscnt6.put("tag", modifTag);
                stuParamList.add(strdiscnt6);
            }
        }

        studentData.put("NOTIN_STU_PARAM_LIST0", stuParamList);
        studentData.put("NOTIN_ctag0", "on");
        studentData.put("NOTIN_OUT_SN0", data.getString("servicephone", ""));
        studentData.put("NOTIN_OPER_TYPE0", data.getString("OUT_OPER_TYPE", ""));

        productParam.put("PRODUCT_ID", grpProductId);
        productParam.put("PRODUCT_PARAM", studentData);
        paramInfo.addAll(IDataUtil.iData2iDataset(studentData, "ATTR_CODE", "ATTR_VALUE"));
        productParam.put("PRODUCT_ID", grpProductId);
        productParam.put("PRODUCT_PARAM", paramInfo);
        productParamInfo.add(productParam);

        return productParamInfo;
    }

    /**
     * 拼成产品参数页面串,里有ELEMENT_INFO(IDataset) "PRODUCT_PARAM_INFO":[{"PRODUCT_ID":"10005742","PRODUCT_PARAM":[{},{}]}],
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset dealXfkProductParamInfo(IData data, String modifTag) throws Exception
    {

        IDataset productParamInfo = new DatasetList();
        IData productParam = new DataMap();

        IDataset paramInfo = new DatasetList();

        IData paramData = new DataMap();

        String grpProductId = data.getString("GRP_PRODUCT_ID", "");

        IDataset uparamList = new DatasetList();

        String[] famnums = data.getString("FAMNUM", "").split(",");

        for (int i = 0; i < famnums.length; i++) {
            IData famsumData = new DataMap();
            famsumData.put("FAMNUM", famnums[i]);
            uparamList.add(famsumData);
        }


        paramData.put("NOTIN_FAM_SN_PARAM_LIST0", uparamList);
        paramData.put("NOTIN_FAM_SN", data.getString("PAY_MOB_NUM"));

        paramInfo.addAll(IDataUtil.iData2iDataset(paramData, "ATTR_CODE", "ATTR_VALUE"));
        productParam.put("PRODUCT_ID", grpProductId);
        productParam.put("PRODUCT_PARAM", paramInfo);
        productParamInfo.add(productParam);

        return productParamInfo;
    }

    public static IDataset dealProductParamInfo2ChgMeb(IDataset dataset, IData data) throws Exception
    {

        IDataset productParamInfo = new DatasetList();
        IData productParam = new DataMap();

        IDataset paramInfo = new DatasetList();

        IData studentData = new DataMap();

        IDataset stuParamList = new DatasetList();



        for (int i = 0, iSize = dataset.size(); i < iSize; i++)
        {
            IData elementInfo = dataset.getData(i);

            IData strdiscnt1 = new DataMap();
            IData strdiscnt2 = new DataMap();
            IData strdiscnt3 = new DataMap();
            IData strdiscnt4 = new DataMap();
            IData strdiscnt5 = new DataMap();
            IData strdiscnt6 = new DataMap();

            String elementId = elementInfo.getString("DISCNT_CODE");

            String modifyTag = elementInfo.getString("MODIFY_TAG");

            // 取得资费名称
            IData params = UDiscntInfoQry.getDiscntInfoByPk(elementId);
            String str = params.getString("RSRV_STR2");
            String elemnetName = params.getString("DISCNT_NAME");

            if (str.startsWith("group_1"))
            {
                strdiscnt1.put("STUD_KEY", "stu_name1");
                strdiscnt1.put("STUD_NAME", data.getString("stu_name1"));
                strdiscnt1.put("ELEMENT_ID", elementId);
                strdiscnt1.put("tag", modifyTag);
                stuParamList.add(strdiscnt1);

            }
            else if (str.startsWith("group_2"))
            {
                strdiscnt2.put("STUD_KEY", "stu_name2");
                strdiscnt2.put("STUD_NAME", data.getString("stu_name2"));
                strdiscnt2.put("ELEMENT_ID", elementId);
                strdiscnt2.put("tag", modifyTag);
                stuParamList.add(strdiscnt2);
            }

            else if (str.startsWith("group_3"))
            {
                strdiscnt3.put("STUD_KEY", "stu_name3");
                strdiscnt3.put("STUD_NAME", data.getString("stu_name3"));
                strdiscnt3.put("ELEMENT_ID", elementId);
                strdiscnt3.put("tag", modifyTag);
                stuParamList.add(strdiscnt3);
            }
            else if (str.startsWith("group_4"))
            {
            	strdiscnt4.put("STUD_KEY", "stu_name4");
            	strdiscnt4.put("STUD_NAME", data.getString("stu_name4"));
            	strdiscnt4.put("ELEMENT_ID", elementId);
            	strdiscnt4.put("tag", modifyTag);
                stuParamList.add(strdiscnt4);
            }
            else if (str.startsWith("group_5"))
            {
            	strdiscnt5.put("STUD_KEY", "stu_name5");
            	strdiscnt5.put("STUD_NAME", data.getString("stu_name5"));
            	strdiscnt5.put("ELEMENT_ID", elementId);
            	strdiscnt5.put("tag", modifyTag);
                stuParamList.add(strdiscnt5);
            }
            else if (str.startsWith("group_6"))
            {
            	strdiscnt6.put("STUD_KEY", "stu_name6");
            	strdiscnt6.put("STUD_NAME", data.getString("stu_name6"));
            	strdiscnt6.put("ELEMENT_ID", elementId);
            	strdiscnt6.put("tag", modifyTag);
                stuParamList.add(strdiscnt6);
            }
        }

        studentData.put("NOTIN_STU_PARAM_LIST0", stuParamList);
        studentData.put("NOTIN_ctag0", "on");
        studentData.put("NOTIN_OUT_SN0", data.getString("servicephone", ""));
        studentData.put("NOTIN_OPER_TYPE0", data.getString("OUT_OPER_TYPE", ""));

        String grpProductId = data.getString("GRP_PRODUCT_ID");
        productParam.put("PRODUCT_ID", grpProductId);
        productParam.put("PRODUCT_PARAM", studentData);
        paramInfo.addAll(IDataUtil.iData2iDataset(studentData, "ATTR_CODE", "ATTR_VALUE"));
        productParam.put("PRODUCT_ID", grpProductId);
        productParam.put("PRODUCT_PARAM", paramInfo);
        productParamInfo.add(productParam);

        return productParamInfo;
    }

    /**
     * liaolc 2014-7-26 作用：见外号服务参数串 SERVICE_INFOS[{}]
     */
    public static IData dealServiceParamInfo(IData data) throws Exception
    {
        String mainSn = data.getString("MOB_NUM");
        String grpUserId = data.getString("GRP_USER_ID");
        String grpSvcCode = data.getString("GRP_SERVICE_ID");
        String operCode = data.getString("OPR_CODE");
        String ctrlType = data.getString("BIZ_CTRL_TYPE");
        String mebSvcCode = data.getString("MEB_SERVICE_ID");
        String grpProductId = data.getString("GRP_PRODUCT_ID");

        IData serviceInfo = new DataMap();

        // 查询该网外号码是否已经加入黑白名单
        IDataset blackWhiteInfo = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuseridSerid(mainSn, grpUserId, mebSvcCode);// 查sn在blackwhite的记录
        if (IDataUtil.isNotEmpty(blackWhiteInfo) && !BizCtrlType.CreateMember.equals(ctrlType) && !"01".equals(operCode))
        {
            IData oldblackwhite = blackWhiteInfo.getData(0);
            serviceInfo.put("OPER_TYPE", operCode); // 退出黑白名单
            serviceInfo.put("MEB_USER_ID", oldblackwhite.getString("USER_ID", "")); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "1"); // 删除
        }

        else
        {
            serviceInfo.put("OPER_TYPE", operCode); // 加入黑白名单
            serviceInfo.put("MEB_USER_ID", "-1"); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "0"); // 新增
        }
        serviceInfo.put("PRODUCT_ID", grpProductId);
        serviceInfo.put("SERVICE_ID", grpSvcCode);

        return serviceInfo;
    }

    /**
     * 作用：加载网外号码时，根据传入的BIZ_CODE、SERV_CODE获取集团用户信息、产品信息、手机号码信息
     *
     * @author liaolc 2014-07-17
     * @param data
     * @return
     * @throws Exception
     */
    public static void getOutGroupAndProduct(IData data) throws Exception
    {

        // 1. 查询集团定购产品信息
        String bizCode = data.getString("BIZ_CODE", "");
        String servCode = data.getString("SERV_CODE", "");
        if (StringUtils.isBlank(bizCode))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_04);
        }
        if (StringUtils.isBlank(servCode))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_05);
        }

        IDataset serDatas = UserGrpPlatSvcInfoQry.getuserPlatsvcbybizcodeservcode(bizCode, servCode);

        if (IDataUtil.isEmpty(serDatas))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_07);
        }

        IData pltSvc = serDatas.getData(0);
        String grpServiceId = pltSvc.getString("SERVICE_ID", "");// 获取serv_code对应的SERVICE_ID
        if (StringUtils.isBlank(grpServiceId))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_11);
        }
        // 2.取集团服务和成员服务对应关系,根据集团产品服务找到对应的成员服务
        String mebSvcCode = MemParams.getmebServIdByGrpServId(grpServiceId);

        // 3.查询集团用户信息
        String userId = pltSvc.getString("USER_ID");
        IData grpUserData = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0"); // 查询集团用户信息
        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_11);
            ;
        }

        data.put("GRP_SERVICE_ID", grpServiceId);
        data.put("MEB_SERVICE_ID", mebSvcCode);
        data.put("GRP_USER_ID", userId);
        data.put("GRP_PRODUCT_ID", grpUserData.getString("PRODUCT_ID", ""));
    }

    public static IDataset dealDiscnts(String discnts)
    {
        IDataset result = new DatasetList();
        if (StringUtils.isBlank(discnts))
            return result;

        String[] aa = discnts.split("\\|\\|");
        for (int i = 0; i != aa.length; ++i)
        {
            result.add(aa[i]);
        }
        return result;
    }

}
