
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GrpImsInfoQuery;

public class GrpIMSQryIntf
{
    /**
     * 校验成员的IMS密码，X_GETMODE：0-密码校验,返回密码校验结果1-取用户信息值
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset checkUserPassword(IData data, Pagination pg) throws Exception
    {

        String sn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        String opCode = data.getString("X_GETMODE", "0");// 0-密码校验,返回密码校验结果1-取用户信息值
        String password = "";
        if (opCode.equals("0"))// 校验密码
        {
            password = IDataUtil.getMandaData(data, "IMS_PASSWORD");
        }
        else
        {
            password = data.getString("IMS_PASSWORD", "");
        }

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);

        IDataset ds = GrpImsInfoQuery.getUserPassword(param);

        if (ds.size() > 0)
        {
            if (opCode.equals("0"))// 校验密码
            {
                IData mebUser = ds.getData(0);
                if (!password.equals(mebUser.getString("IMS_PASSWORD", "")))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_774);
                }
            }
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_65);
        }
        return ds;
    }

    /**
     * 根据集团编号GROUP_ID进行集团订购IMS业务的用户信息查询
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getGrpImsUserInfo(IData data, Pagination pg) throws Exception
    {

        String strGroupId = IDataUtil.getMandaData(data, "GROUP_ID");
        if (strGroupId.equals(""))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_66, strGroupId);
        }

        IData param = new DataMap();
        param.put("GROUP_ID", strGroupId);

        // 根据集团GROUP_ID查询集团用户信息总记录数
        IDataset idsImsGrpUserInfo = GrpImsInfoQuery.getGrpImsUserInfo(param);

        return idsImsGrpUserInfo;
    }

    public static IDataset qryImsUserDiscnts(IData data, Pagination pg) throws Exception
    {
        // 查询成员用户信息
        String strMebSn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        String userIdA = IDataUtil.getMandaData(data, "USER_ID_A", "");

        String eparchyCode = RouteInfoQry.getEparchyCodeBySn(strMebSn);
        IData mebUserData = UcaInfoQry.qryUserInfoBySn(strMebSn, eparchyCode);
        if (mebUserData == null || mebUserData.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_72);
        }

        IData inparam = new DataMap();
        inparam.put("USER_ID_B", mebUserData.getString("USER_ID"));
        inparam.put("USER_ID_A", userIdA);

        IDataset ds = GrpImsInfoQuery.qryImsUserDiscnts(inparam);

        return ds;
    }

    /**
     * 获取用户产品参数信息
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset getGrpUserAttr(IData data, Pagination pg) throws Exception
    {

        String xGetMode = IDataUtil.chkParam(data, "X_GETMODE");// 0:已订购; 1:未订购
        String userIdA = IDataUtil.chkParam(data, "USER_ID_A");// 集团用户标识

        String attrCode = data.getString("ATTR_CODE", "");// 属性编码

        if (!"0".equals(xGetMode) && !"1".equals(xGetMode))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_295);
        }

        // 查询用户信息
        IData userInfoList = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);

        String productId = "";

        if (IDataUtil.isNotEmpty(userInfoList))
        {
            productId = userInfoList.getString("PRODUCT_ID", "");
        }

        IData mapData = new DataMap();

        IDataset userAttrList = null;

        if ("0".equals(xGetMode))
        {
            if ("".equals(attrCode))
            {
                userAttrList = UserAttrInfoQry.getUserAttrByUserId(userIdA);
            }
            else
            {
                userAttrList = UserAttrInfoQry.getUserAttrInfoByAttrCode2(userIdA, attrCode);
            }

            if ("6130".equals(productId))// 融合总机处理
            {
                IData inparam = new DataMap();
                inparam.put("RSRV_VALUE_CODE", "MUTISUPERTEL");
                inparam.put("USER_ID", userIdA);
                // 融合总机总机号码信息
                IDataset userOtherList = GrpImsInfoQuery.qryImsMutilSuperTel(inparam);
                mapData.put("TELEPHONE_INFOS", userOtherList);
            }
        }
        else if ("1".equals(xGetMode))
        {
            userAttrList = UserAttrInfoQry.getUserAttrByUserId(userIdA);
        }

        mapData.put("GRP_ATTR_ORDER", userAttrList);

        return IDataUtil.idToIds(mapData);
    }

    /**
     * 集团群组查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getGroupTeam(IData data) throws Exception
    {
        String userIdA = IDataUtil.getMandaData(data, "USER_ID", "");
        String teamType = IDataUtil.getMandaData(data, "TEAM_TYPE", "");
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);

        if ("0".equals(teamType))
            teamType = "XH";
        else if ("1".equals(teamType))
            teamType = "DD";
        else if ("2".equals(teamType))
            teamType = "VB";
        else
        {
            CSAppException.apperr(ParamException.CRM_PARAM_378);
        }
        param.put("RELATION_TYPE", teamType);

        return GrpImsInfoQuery.getGroupTeam(param);
    }

    /**
     * 集团群组成员的查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getTeamMeb(IData data) throws Exception
    {
        String userIdA = IDataUtil.getMandaData(data, "TEAM_ID", "");
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("RELATION_TYPE_CODE", "MB");

        return GrpImsInfoQuery.getTeamMeb(param);
    }

    /**
     * 集团产品成员查询
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset qryMebLists(IData data, Pagination pg) throws Exception
    {
        String userIdA = IDataUtil.getMandaData(data, "USER_ID");
        String roleCodeB = data.getString("X_GETMODE");// 1-普通成员;2-集团管理员;3-话务员; 如果不传，查全部成员。
        IData param = new DataMap();
        param.put("USER_ID_A", userIdA);
        param.put("ROLE_CODE_B", roleCodeB);

        IDataset userList = GrpImsInfoQuery.qryMebLists(param);
        if (IDataUtil.isEmpty(userList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_67);
        }
        String productid = userList.getData(0).getString("PRODUCT_ID");
        String relationtypecode = ProductCompInfoQry.getRelaTypeCodeByProductId(productid);
        param.put("RELATION_TYPE_CODE", relationtypecode);
        IDataset ds = GrpImsInfoQuery.qryRelaByUserIdaAndRoleB(param);
        return ds;
    }

    // 通过user_id查询一号通成员的主叫、被叫一号通号码
    public static IDataset GetQryMebYhtNum(IData data, Pagination pg) throws Exception
    {
        // 集团用户手机号码
        String strUserId = IDataUtil.getMandaData(data, "USER_ID");
        IDataset ids = new DatasetList();
        IData param = new DataMap();
        IData idReturn = new DataMap();
        param.put("USER_ID", strUserId);

        // 根据USER_ID查询一号通成员的主叫、被叫一号通号码
        IDataset USER_INFOS = GrpImsInfoQuery.GetQryMebYhtNumUserInfo(param);

        IDataset PRODUCT_ATTR = GrpImsInfoQuery.GetQryMebYhtNumProdAttr(param);

        idReturn.put("USER_INFOS", USER_INFOS);
        idReturn.put("PRODUCT_ATTR", PRODUCT_ATTR);

        ids.add(idReturn);

        return ids;
    }

    /**
     * 校验成员的服务密码，X_GETMODE：0-密码校验,返回密码校验结果1-取用户信息值
     * 
     * @param data
     * @param pg
     * @return
     * @throws Exception
     */
    public static IDataset checkUserPasswordByPer(IData data, Pagination pg) throws Exception
    {

        String sn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        String opCode = data.getString("X_GETMODE", "0");// 0-密码校验,返回密码校验结果1-取用户信息值
        String password = "";
        if (opCode.equals("0"))// 校验密码
            password = IDataUtil.getMandaData(data, "IMS_PASSWORD");
        else
            password = data.getString("IMS_PASSWORD", "");

        IData res = UcaInfoQry.qryUserInfoBySn(sn);
        if (IDataUtil.isEmpty(res))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String userid = ((IData) res.get(0)).getString("USER_ID");
        String userpasswd = ((IData) res.get(0)).getString("USER_PASSWD");
        if (null == userpasswd || "".equals(userpasswd))// 用户服务密码不存在
        {
            CSAppException.apperr(CrmUserException.CRM_USER_81);
        }
        if (password.length() != 6)// 密码长度不正确
        {
            CSAppException.apperr(CrmUserException.CRM_USER_110);
        }
        boolean irs = UserInfoQry.checkUserPassWd(userid, password);
        if (irs == false)// 密码错误
            CSAppException.apperr(CrmUserException.CRM_USER_91);
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);
        IDataset ds = GrpImsInfoQuery.checkUserPasswordByPer(param);

        if (ds == null || ds.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_65);
        }
        return ds;
    }

    /**
     * 查询成员用户设置的黑白名单信息 PageData
     * 
     * @param data
     *            IData 接口传入的参数
     * @return IDataset 查询结果
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    public static IDataset getBwInfo(IData data) throws Exception
    {
        String memberUserId = IDataUtil.getMandaData(data, "USER_ID");
        String bwType = data.getString("USER_TYPE_CODE", "");
        IData param = new DataMap();
        param.put("EC_USER_ID", memberUserId);
        param.put("BIZ_NAME", "IMSBWLIST");
        if (!"".equals(bwType))// 查询全部
        {
            param.put("USER_TYPE_CODE", bwType);
        }
        IDataset result = GrpImsInfoQuery.getBwInfo(param);
        if (!IDataUtil.isEmpty(result))
        {
            return result;
        }
        return new DatasetList();
    }

    public static IDataset getGrpUserDiscnts(IData data, Pagination pg) throws Exception
    {

        String xMode = data.getString("X_GETMODE", "");// 0-已订购,1-未订购,2-成员定制
        String userIdA = data.getString("USER_ID_A", "");
        if ("".equals(xMode))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_87);
        }
        if ("".equals(userIdA))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_85);
        }

        IData usParam = new DataMap();
        usParam.put("USER_ID", userIdA);
        return GrpImsInfoQuery.getGrpUserDiscnts(usParam, xMode);

    }
    
    public static IDataset getImsParam(IData data) throws Exception
    {
        IDataset idsRet = new DatasetList();
        
        String userIdA = IDataUtil.getMandaData(data, "USER_ID_A",""); //集团用户ID
        String userId = IDataUtil.getMandaData(data, "USER_ID","");  //成员用户ID
        String attrCode = data.getString("ATTR_CODE", "");
        //String serialnumber = IDataUtil.getMandaData(data,"SERIAL_NUMBER");//这里不需要 服务路由需要
        
        IData grpUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userIdA, "0");
        
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_93);//获取集团用户订购产品资料无信息！
        }
        
        String grpProductId = grpUserInfo.getString("PRODUCT_ID");
        
        IDataset userProductInfos = UserProductInfoQry.getProductInfo(userId, userIdA);
        
        if (IDataUtil.isEmpty(userProductInfos))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_65);//获取产品信息失败
        }
        
        String instid =  userProductInfos.getData(0).getString("INST_ID","0");
        
        idsRet = GrpImsInfoQuery.getImsParam(userId, userIdA, attrCode, instid, grpProductId);
            
        return idsRet;
    }

}
