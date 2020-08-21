
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GrpImsInfoQuery;

public class CentrexTeamDeal
{
    /*
     * @description 集团群组管理
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperGrpTeam(IData data) throws Exception
    {
        String operCode = data.getString("OPER_CODE", "");
        String teamType = data.getString("TEAM_TYPE", "");
        String userId = data.getString("USER_ID", "");
        String huntType = "";
        String accessCode = "";
        String teamNumber = "";
        String teamId = data.getString("TEAM_ID", "");

        IData param = new DataMap();
        param.put("USER_ID", userId);
        // 查询集团信息
        UcaData grpUcaData = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        if (!GrpImsInfoQuery.VPN_GRP_PRODUCTID.equals(grpUcaData.getProductId()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_767);// 只有融合V网用户才能办理此业务!
        }

        if ("0".equals(operCode))
        {// 群组新增
            if ("0".equals(teamType))
            {// 寻呼组
                teamNumber = IDataUtil.chkParam(data, "TEAM_NUMBER");
                huntType = IDataUtil.chkParam(data, "HUNTING_TYPE");
                // 验证组号码
                checkTeamNumber(teamNumber, grpUcaData.getCustId());
            }
            else if ("1".equals(teamType))
            {
                // 代答组
                accessCode = IDataUtil.chkParam(data, "ACCESS_CODE");
                
                //校验代答组是否被用
                IDataset uuInfos = RelaUUInfoQry.checkMemRelaByUserIdb(userId, teamId, "DD", null);
                
                if (IDataUtil.isNotEmpty(uuInfos))
                {
                    for (int i = 0, size = uuInfos.size(); i < size; i++)
                    {
                        String rsrv_str4 = uuInfos.getData(i).getString("RSRV_STR4");
                        if (accessCode.equals(rsrv_str4))
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "代答组号码"+accessCode+"已经被使用！请核实后重新录入！");
                        }
                    }
                }
            }
        }
        else if ("1".equals(operCode))
        {// 群组删除
            String relationTypeCode = "";
            if ("0".equals(teamType))
            {
                relationTypeCode = "XH";
            }
            else if ("1".equals(teamType))
            {
                relationTypeCode = "DD";
            }
            IDataset uuList = RelaUUInfoQry.checkMemRelaByUserIdb(userId, teamId, relationTypeCode, null);
            if (IDataUtil.isEmpty(uuList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_136, userId, teamId);// 指定的集团[user_id: %s]下没有有效的群内组[群组ID:
                // %s]！
            }
        }
        IData conmmitData = new DataMap();
        conmmitData.put("USER_ID", userId);
        conmmitData.put("TEAMTYPE", teamType);
        conmmitData.put("HUNTTYPE", huntType);
        conmmitData.put("ACCESSCODE", accessCode);
        conmmitData.put("OPERCODE", operCode);
        conmmitData.put("TEAM_ID", teamId);
        conmmitData.put("TEAM_SERIAL", teamNumber);
        // 用户归属地州设置为交易地州
        conmmitData.put(Route.USER_EPARCHY_CODE, grpUcaData.getUserEparchyCode());

        // 调用后台服务,提交数据
        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("SS.CentrexTeamManaBeanSVC.crtTrade", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }

    /*
     * @description 集团群组管理
     * @author liuzz
     * @date 2014/05/30
     */
    public static void checkTeamNumber(String teamNumber, String custId) throws Exception
    {
        IData memUserInfo = UserInfoQry.getMebUserInfoBySN(teamNumber);
        if (IDataUtil.isEmpty(memUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_127, teamNumber);// 查询组号码[%s]的用户信息失败！
        }
        IDataset deskUser = UserInfoQry.getUserInfoByCstIdProIdForGrp(custId, "2222", null);
        if (IDataUtil.isEmpty(deskUser))
        {
            CSAppException.apperr(GrpException.CRM_GRP_134, teamNumber);// 该集团[USER_ID: %s]还没有订购多媒体桌面电话产品！
        }
        // 查询集团成员关系是否存在
        IDataset grpRelat = RelaUUInfoQry.qryUU(deskUser.getData(0).getString("USER_ID"), memUserInfo.getString("USER_ID"), "", null, null);
        if (IDataUtil.isEmpty(grpRelat))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_45);// 组号码不是当前集团的IMS用户，不能作为组号码使用！
        }
        // 查询组号码是否已经被使用
        IDataset teamRelat = RelaUUInfoQry.qryUU(deskUser.getData(0).getString("USER_ID"), "", "XH", null, null);
        if (IDataUtil.isNotEmpty(teamRelat))
        {
            for (int i = 0; i < teamRelat.size(); i++)
            {
                String rsrv_str5 = teamRelat.getData(i).getString("RSRV_STR5");
                if (teamNumber.equals(rsrv_str5))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_46);// 该组号码已经被使用！请核实后重新录入！
                }
            }
        }
        // 查询组号码是否已经是其他组成员
        IDataset memRelat = RelaUUInfoQry.qryUU("", memUserInfo.getString("USER_ID"), "MB", null, null);
        if (IDataUtil.isNotEmpty(memRelat))
        {
            CSAppException.apperr(GrpException.CRM_GRP_730);// 该号码已是其他组成员！请核实后重新录入！
        }
    }

    /*
     * @description 修改成员群组
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperTeamMeb(IData data) throws Exception
    {
        String serialNumber = data.getString("SERIAL_NUMBER", ""); // 成员手机号
        String memUerId = data.getString("USER_ID", "");// 从前台取得用户USER_ID
        String teamUserId = data.getString("TEAM_ID", "");// 从前台取得
        String operCode = data.getString("OPER_CODE", "");

        // 查询组用户信息
        IData teamUserInfo = UcaInfoQry.qryUserInfoByUserId(teamUserId);
        if (IDataUtil.isEmpty(teamUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_103, teamUserId);// 该群组[%s]用户信息不存在！
        }
        // 查询群组信息
        IDataset teamUuList = RelaUUInfoQry.checkMemRelaByUserIdb("", teamUserId, "", null);
        if (IDataUtil.isEmpty(teamUuList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_104, teamUserId);// 根据群组号标示[%s]查询不到对应的集团产品信息！
        }
        // 查询成员群组关系
        IDataset uuList = RelaUUInfoQry.checkMemRelaByUserIdb(teamUserId, memUerId, "MB", null);
        if ("0".equals(operCode) && IDataUtil.isNotEmpty(uuList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_140, serialNumber);// 集团成员[成员用户ID：%s]已经存在对应的群组关系，无法继续新增！
        }
        else if ("1".equals(operCode) && IDataUtil.isEmpty(uuList))
        {
            CSAppException.apperr(UUException.CRM_UU_2, memUerId, teamUserId);// 不存在对应的群内组成员关系[成员User_id: %s, TeamID:
            // %s]，无法执行操作关系！
        }

        // 验证成员号码
        String eparchyCode = checkMemNumber(serialNumber, teamUuList.getData(0).getString("USER_ID_A"));
        IData conmmitData = new DataMap();
        conmmitData.put("USER_ID", teamUuList.getData(0).getString("USER_ID_A"));
        conmmitData.put("TEAMTYPE", "3");
        conmmitData.put("OPERCODE", operCode);
        conmmitData.put("MEMBER_MUMBER", serialNumber);
        conmmitData.put("MEMBER_TEAM", teamUserId);
        conmmitData.put("TEAM_ID", memUerId);
        // 用户归属地州设置为交易地州
        conmmitData.put(Route.USER_EPARCHY_CODE, eparchyCode);

        // 调用后台服务,提交数据
        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("SS.CentrexTeamManaBeanSVC.crtTrade", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }

    /*
     * @description 验证组成员号码
     * @author liuzz
     * @date 2014/05/30
     */
    public static String checkMemNumber(String serialNumber, String groupUserID) throws Exception
    {
        IData memUserInfo = UserInfoQry.getMebUserInfoBySN(serialNumber);
        if (IDataUtil.isEmpty(memUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_101, serialNumber);// 根据成员服务号码[" + mem_sn + "]查询成员用户资料不存在！
        }
        if (!"05".equals(memUserInfo.getString("NET_TYPE_CODE")))
        {
            CSAppException.apperr(GrpException.CRM_GRP_731);// 只有固话号码允许添加为组成员！
        }
        IDataset uuList = RelaUUInfoQry.checkMemRelaByUserIdb(groupUserID, memUserInfo.getString("USER_ID"), "20", null);
        if (IDataUtil.isEmpty(uuList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_732, serialNumber);// 您输入的成员服务号码[" + mem_sn + "]不是该集团的融合V网成员，业务不能办理！
        }
        IDataset userSvcList = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(memUserInfo.getString("USER_ID"), "10122817");
        if (IDataUtil.isEmpty(userSvcList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_733, serialNumber);// 服务号码[" + mem_sn + "]没有开通闭合群功能，请在多媒体桌面电话成员变更增加！
        }
        return memUserInfo.getString("EPARCHY_CODE");
    }
}
