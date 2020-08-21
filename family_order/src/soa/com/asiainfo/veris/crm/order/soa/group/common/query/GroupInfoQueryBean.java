
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GroupInfoQueryBean extends GroupBean
{
    /*
     * 查询 inst_id
     */
    public static String qryDisInstId(IData dt) throws Exception
    {

        IDataset idsInstId = GroupInfoQueryDAO.qryDisInstId(dt);
        IData dm = idsInstId.size() > 0 ? (IData) idsInstId.get(0) : null;

        return dm.size() > 0 ? dm.getString("INST_ID") : "";
    }

    /**
     * 根据母集团user_id
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset qryUUnVPN(IData param) throws Exception
    {

        return GroupInfoQueryDAO.qryUUnVPN(param, null);
    }

    /**
     * vpn短号校验 是否已经存在
     */
    public boolean existOldShortNum(IData param) throws Exception
    {

        IData dt = new DataMap();
        int shortCodeExistInt = 0;
        if (GroupInfoQueryDAO.existOldShortNum(param).size() > 0)
        {
            dt = GroupInfoQueryDAO.existOldShortNum(param).getData(0);
            shortCodeExistInt = dt.getInt("RECORDCOUNT");
        }

        return shortCodeExistInt > 0;
    }

    /*
     * 得到 relation_type_code
     */
    public String getRelaTypCode(IData dt) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.getRelaTypCode(dt);

        return ids.size() > 0 ? ids.getData(0).getString("RELATION_TYPE_CODE") : "";
    }

    /*
     * 得到 角色编码
     */
    public IDataset getRoleCodeInfo(IData dt) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.getRoleCodeInfo(dt);

        return ids;
    }

    /*
     * 根据 短号 和 vpn 编码 获得 成员 手机号
     */
    public String getSerNumByVpnNoAndShortCode(IData dt) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.getSerNumByVpnNoAndShortCode(dt);
        IData dm = new DataMap();

        if (ids.size() > 0)
        {
            dm = (IData) ids.get(0);
        }
        else
        {
            // j2ee move //common.warn("该用户的VPN编码：" + dt.getString("VPN_NO") + "没有对应的信息！");
        }

        return dm.size() > 0 ? dm.getString("SERIAL_NUMBER") : "";
    }

    public IDataset getUsrSerA(IData param) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.getUsrSerA(param);

        return ids;
    }

    public IData getVpnInfo(IData param) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.getVpnInfo(param);
        IData dm = new DataMap();
        if (ids.size() > 0)
        {
            dm = (IData) ids.get(0);
        }
        else
        {
            // j2ee move //common.warn("该VPN集团编号：" + param.getString("SERIAL_NUMBER_A") + "没有对应的客户信息！");
        }

        return dm;
    }

    /*
     * 判断 是否 存在 tradeTypeCode
     */
    public boolean hasTradeTypeCode(IData dt) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.hasTradeTypeCode(dt);
        IData dm = ids.size() > 0 ? (IData) ids.get(0) : null;

        String tradeTypeCode = dm.size() > 0 ? dm.getString("A") : "";

        int tradeTypeCodeInt = 0;
        if (tradeTypeCode != "")
        {
            tradeTypeCodeInt = Integer.parseInt(tradeTypeCode);
        }

        if (tradeTypeCodeInt > 0)
            return true;
        else
            return false;
    }

    /*
     * 根据 vpn 编码 得到 对应 的权限信息
     */
    public IDataset intfVpnCheckRight(IData dt) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.intfVpnCheckRight(dt);

        return ids;
    }

    /*
     * 根据 vpn 编码 是否要进行权限判断
     */
    public IDataset intfVpnNeedCheckRight(IData dt) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.intfVpnNeedCheckRight(dt);

        return ids;
    }

    /**
     * @Description:adc集团
     */

    public IDataset qryADCGroupInfoBySn(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryADCGroupInfoBySn(param, pagination);
    }

    /**
     * @Description:adc成员用户资料查询
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */

    public IDataset qryADCMembersbyuser(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryADCMembersbyuser(param, pagination);
    }

    /**
     * @Description:ADC个人订购查询
     * @author wusf
     * @date 2009-8-3
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryADCPersonalOrderInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryADCPersonalOrderInfo(param, pagination);
    }
    
    
    /**
     * @Description:流量统付个人订购查询
     * @author xsf
     * @date 2009-8-3
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryCenpyOrderInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryCenpyOrderInfo(param, pagination);
    }

    /**
     * @Description:集团E网用户查询(包括已注销的)
     * @author wusf
     * @date 2009-8-4
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryAllGroupENetInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryAllGroupENetInfo(param, pagination);
    }

    /**
     * BBOSS工单开通属性
     * 
     * @author jch
     * @param pd
     * @param param
     */
    public IDataset qryBBossktWorkSX(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryBBossktWorkSx(param, pagination);
    }

    /**
     * @Description:查询集团客户已开通业务的接口
     * @author houwl
     * @date 2009-8-28
     * @param pd
     * @param param
     * @return IDataset
     * @throws Exception
     */
    public IDataset qryECBizByGrpID(IData param) throws Exception
    {
        IDataset idsGrpId = GroupInfoQueryDAO.qryECBizByGrpID(param);

        if (idsGrpId.size() <= 0)
        {
            // j2ee move //common.warn("该集团：" + grpId + "没有对应的集团客户编码！");
        }
        return idsGrpId;
    }

    /**
     * @Description:根据子集团ID获取子母集团资料
     * @author meig
     * @date 2009-8-12
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    // public IDataset qryGroupVpmnAQuery(IData param, Pagination pagination) throws Exception {
    // //GroupInfoQueryDAO dao = new GroupInfoQueryDAO(pd);
    // return GroupInfoQueryDAO.qryGroupVpmnAQuery(pd,param, pagination);
    // }

    /**
     * @Description:根据母集团ID获取子母集团资料
     * @author meig
     * @date 2009-8-12
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    // public IDataset qryGroupVpmnBQuery(IData param, Pagination pagination) throws Exception {
    // //GroupInfoQueryDAO dao = new GroupInfoQueryDAO(pd);
    // return GroupInfoQueryDAO.qryGroupVpmnBQuery(pd,param, pagination);
    // }

    /**
     * @Description:集团E网用户查询
     * @author wusf
     * @date 2009-8-4
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupENetInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupENetInfo(param, pagination);
    }

    /**
     * @Description:集团LBS查询(按产品编码查询)
     * @author lixiuyu
     * @date 2009-8-13
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupLBSByProductId(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupLBSByProductId(param, pagination);
    }

    /**
     * @Description:集团LBS查询(按手机号码查询)
     * @author lixiuyu
     * @date 2009-8-13
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupLBSBySN(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupLBSBySN(param, pagination);
    }

    /**
     * @Description:集团LBS查询(按产品编码和手机号码查询)
     * @author wusf
     * @date 2009-8-3
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupLBSInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupLBSInfo(param, pagination);
    }

    /**
     * @Description:集团专网查询
     * @author wusf
     * @date 2009-8-3
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupNetworkInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupNetworkInfo(param, pagination);
    }

    /**
     * @Description:集团彩铃查询(按产品编码查询)
     * @author lixiuyu
     * @date 2009-8-13
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupPRBByProductId(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupPRBTByProductId(param, pagination);
    }

    /**
     * @Description:集团彩铃查询(按手机号码查询)
     * @author lixiuyu
     * @date 2009-8-13
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupPRBTBySN(IData param) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupPRBTBySN(param);
    }

    /**
     * @Description:集团彩铃查询(按产品编码和手机号码查询)
     * @author wusf
     * @date 2009-8-4
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGroupPRBTInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryGroupPRBTInfo(param, pagination);
    }

    /*
     * vpmn 根据 SERIAL_NUMBER ，取 group_id
     */
    public IData qryGrPInfo(IData param) throws Exception
    {
        String serNum = param.getString("SERIAL_NUMBER");

        // serNum = null;
        if (serNum == null || "".equals(serNum))
        {
            // //j2ee move //common.warn("传入的号码：" + serNum + "为空！"); j2ee move
        }

        IDataset idsGrpId = GroupInfoQueryDAO.qryGrPInfo(param);
        IData dm = new DataMap();
        if (idsGrpId.size() > 0)
        {
            dm = (IData) idsGrpId.get(0);
        }
        else
        {
            // j2ee move //common.warn("该集团编号：" + serNum + "没有对应的信息！");
        }

        return dm;
    }

    public IDataset qryGrpSpecialPayInfo(IData dt, Pagination pagination) throws Exception
    {

        IDataset ids = GroupInfoQueryDAO.qryGrpSpecialPayInfo(dt, pagination);
        return ids;

    }

    /**
     * @Description:IP后付费固定电话号码查询（按集团编码）
     * @author wusf
     * @date 2009-8-5
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryIPLaterPayInfoA(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryIPLaterPayInfoA(param, pagination);
    }

    /**
     * @Description:IP后付费固定电话号码查询（按固定电话）
     * @author wusf
     * @date 2009-8-5
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryIPLaterPayInfoB(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryIPLaterPayInfoB(param, pagination);
    }

    /*
     * 得到 成员 优惠名称
     */
    public IDataset qryMemDisName(IData param) throws Exception
    {

        return GroupInfoQueryDAO.qryMemDisName(param);
    }

    public IDataset qryMerchpInfos(IData param) throws Exception
    {
        // 设置路由地州为集团库
        // pd.setRouteEparchy("cg");

        return GroupInfoQueryDAO.qryMerchpInfos(param, new Pagination(), Route.CONN_CRM_CG);
    }

    /**
     * @Description:移动总机号码查询
     * @author wusf
     * @date 2009-8-3
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryMobilePhoneCodeInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryMobilePhoneCodeInfo(param, pagination);
    }

    /**
     * @Description:根据USER_ID获取用户优惠信息
     * @author meig
     * @date 2009-8-12
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryParentVpmnQuery(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryParentVpmnQuery(param, pagination);
    }

    /**
     * @Description:手机代付客利发查询
     * @author wusf
     * @date 2009-8-5
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryPhoneAndKlfInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryPhoneAndKlfInfo(param, pagination);
    }

    /**
     * 根据UserIdA获取动力100子产品信息
     * 
     * @param custId
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryPower100MemberProdInfosByUserIdA(IData inparam, Pagination pagination) throws Exception
    {
        return UserInfoQry.qryPower100MemberProdInfosByUserIdA(inparam, pagination);
    }

    /**
     * @Description:查询集团产品信息
     * @author jch
     * @date 2009-8-12
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryProductInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryProductInfo(param, pagination);
    }

    /**
     * @Description:查询集团产品成员信息
     * @author jch
     * @date 2009-8-12
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryProductMebInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryProductMebInfo(param, pagination);
    }

    /**
     * @Description:短号查询
     * @author lixiuyu
     * @date 2010-12-22
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryShortCodeByVpmn(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryShortCodeByVpmn11(param, pagination);
    }

    /**
     * @Description:用母VPMN来短号查询
     * @author zhouhua
     * @date 2010-12-22
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryShortCodeByVpmn4M(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryShortCodeByVpmn4M(param, pagination);
    }

    public IDataset qrySubPrdInfo(IData param) throws Exception
    {
        // 设置路由地州为集团库
        // pd.setRouteEparchy("cg");

        return GroupInfoQueryDAO.qrySubPrdInfo(param, new Pagination(), Route.CONN_CRM_CG);
    }

    public IDataset qrySubProductInfos(IData param) throws Exception
    {
        // 设置路由地州为集团库
        // pd.setRouteEparchy("cg");

        return GroupInfoQueryDAO.qrySubProductInfos(param, Route.CONN_CRM_CG);
    }

    /**
     * @Description:根据集团ID获取用户优惠信息
     * @author meig
     * @date 2009-8-12
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserDiscntInfoQuery(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryUserDiscntInfoQuery(param, pagination);
    }

    /**
     * @Description:根据集团编号获取用户信息
     * @author meig
     * @date 2009-8-12
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserInfoBySerialnumber(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryUserInfoBySerialnumber(param, pagination);
    }

    /**
     * 根据集团ID和BOSS侧产品ID查询 TF_F_USER_GRP_MERCH 数据
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryUserMerchInfo(IData param) throws Exception
    {

        return GroupInfoQueryDAO.qryUserMerchInfo(param, new Pagination());
    }

    /**
     * @Description:用户订购关系查询
     * @author wusf
     * @date 2009-8-5
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserOrderRelationInfoA(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryUserOrderRelationInfoA(param, pagination);
    }

    /**
     * @Description:用户订购关系查询
     * @author wusf
     * @date 2009-8-5
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryUserOrderRelationInfoB(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryUserOrderRelationInfoB(param, pagination);
    }

    /*
     * 集团产品信息查询
     */
    public IDataset qryUserPrdByGrpId(IData dt) throws Exception
    {

        return GroupInfoQueryDAO.qryUserPrdByGrpId(dt);
    }

    /**
     * @Description:VPMN用户资料查询
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryVpmnInfo(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryVpmnInfo(param, pagination);
    }

    public IDataset qryVpmnInfoRelation(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryVpmnInfoRelation(param, pagination);
    }

    /**
     * @Description:集团V网营销活动资料查询
     * @author lixiuyu
     * @date 20110519
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryVpmnSaleActive(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.qryVpmnSaleActive(param, pagination);
    }

    public IDataset qryVPMNScpInfo(IData param) throws Exception
    {

        return GroupInfoQueryDAO.qryVPMNScpInfo(param);
    }

    /*
     * 根据 vpn_no ，SERIAL_NUMBER_B 取 short_code
     */
    public String qryVpnMemShrtCod(IData param) throws Exception
    {

        IDataset idsGrpId = GroupInfoQueryDAO.qryVpnMemShrtCod(param);
        IData dm = new DataMap();
        if (idsGrpId.size() > 0)
        {
            dm = (IData) idsGrpId.get(0);
        }

        return dm.size() > 0 ? dm.getString("SHORT_CODE") : "";
    }

    public IDataset queryByUserid(IData param, Pagination pagination) throws Exception
    {

        return GroupInfoQueryDAO.queryByUserid(param, pagination);
    }

    /*
     * 取得集团成员已订购产品
     */
    public IDataset queryList4DBTotal(IData param) throws Exception
    {
        IDataset listResult = new DatasetList();
        IDataset listTmp = null;
        // String[] str4DB = ConnectionManager.getInstance().getAllNames();

        // for (int i = 0; i < str4DB.length; i++) {
        // pd.setRouteEparchy(str4DB[i]);

        listTmp = GroupInfoQueryDAO.getUsrMem(param);

        /*
         * if (!listResult.containsAll(listTmp)) { listResult.addAll(listTmp); }
         */
        // }
        listResult.addAll(listTmp);

        return listResult;

    }

    /**
     * 根据CustId获取动力100集团产品信息
     * 
     * @param custId
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryPower100InfoByCustId(IData inparam, Pagination pagination) throws Exception
    {
        return UserInfoQry.queryPower100InfoByCustId(inparam, pagination);
    }

    public IDataset getUserInfo(IData inparam) throws Exception
    {
        return GroupInfoQueryDAO.getUserInfo(inparam);
    }

    public IDataset getUserGrpPackageByUserId(IData inparam) throws Exception
    {
        return GroupInfoQueryDAO.getUserGrpPackageByUserId(inparam);
    }

    public IDataset getUserRealtionUU(IData inparam) throws Exception
    {
        return GroupInfoQueryDAO.getUserRealtionUU(inparam);
    }

    public IDataset getPackageElement(IData inparam) throws Exception
    {
        return GroupInfoQueryDAO.getPackageElement(inparam);
    }

}
