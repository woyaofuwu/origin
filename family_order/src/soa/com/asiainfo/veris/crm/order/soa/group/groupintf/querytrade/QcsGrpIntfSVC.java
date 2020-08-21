package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.CSQryBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;

public class QcsGrpIntfSVC extends CSQryBizService {
    private static final long serialVersionUID = 1L;

    /**
     * 查询集团成员集团相关优惠（外围接口）
     *
     * @param inparam
     *            （SERIAL_NUMBER_A, SERIAL_NUMBER_B）
     * @return
     * @throws Exception
     */
    public IDataset getMemberDiscnt(IData inparam) throws Exception {
        return GrpMebQryIntf.getMemberDiscnt(inparam);
    }

    /**
     * 查询集团成员集团相关优惠（电子渠道 门户）
     *
     * @param inparam
     *            （SERIAL_NUMBER_A, SERIAL_NUMBER_B）
     * @return
     * @throws Exception
     */
    public IDataset getMemChanelDiscnt(IData inparam) throws Exception {
        return GrpMebQryIntf.getMemberDiscnt(inparam);
    }

    /**
     * 成员新增时，可选的集团产品
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getMemAddCanSelProd(IData inparam) throws Exception {
        return GrpMebQryIntf.getMemAddCanSelProd(inparam);
    }

    /**
     * 成员新增时，可选的集团产品优惠
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getMemAddCanSelDis(IData inparam) throws Exception {
        return GrpMebQryIntf.getMemAddCanSelDis(inparam);
    }

    /**
     * 优惠变更时，可选的集团产品优惠
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getMemChgCanSelDis(IData inparam) throws Exception {
        return GrpMebQryIntf.getMemAddCanSelDis(inparam);
    }

    /**
     * 查询集团V网营销活动资料
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryVpmnSaleActive(IData inparam) throws Exception {
        return GrpMebQryIntf.qryVpmnSaleActive(inparam);
    }

    /**
     * 校验是否能够满足办理集团V网双网有礼活动
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset checkVpmnBothWebActive(IData inparam) throws Exception {
        return GrpMebQryIntf.checkVpmnBothWebActive(inparam);
    }

    /**
     * 提供给端到端用于判断产品是否可以办理
     *
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset checkOperProduct(IData inParam) throws Throwable {
        return GrpUserQryIntf.checkOperProduct(inParam);
    }

    /**
     * 根据GROUP_ID查询集团客户信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpCustInfo(IData inParam) throws Exception {
        return GrpUserQryIntf.qryGrpCustInfo(inParam);
    }

    /**
     * 查询集团产品类型信息
     *
     * @author liujy
     * @param inParam
     * @throws Throwable
     */
    public IDataset qryGrpProductType(IData inParam) throws Throwable {
        return GrpProductQryIntf.qryGrpProductType(inParam, getPagination());
    }

    /**
     * 根据PRODUCT_TYPE_CODE获取集团产品子类型
     *
     * @author liujy
     * @param inParam
     * @throws Throwable
     */
    public IDataset qryGrpProductByType(IData inParam) throws Throwable {
        return GrpProductQryIntf.qryGrpProductByType(inParam, getPagination());
    }

    /**
     * 根据PRODUCT_ID获取产品信息
     *
     * @author liujy
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryProductInfoByProductId(IData inParam) throws Throwable {
        return GrpProductQryIntf.qryProductInfoByProductId(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID查询集团产品订购关系
     *
     * @author liujy
     * @param inParam
     * @throws Throwable
     */
    public IDataset qryGrpUserProInfo(IData inParam) throws Throwable {
        // 设置分页
        setPagination(createPagination(inParam));

        return GrpUserQryIntf.qryGrpUserProInfo(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID、ProductId查询
     *
     * @author liujy
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpMebUUInfo(IData inParam) throws Throwable {
        return GrpMebQryIntf.qryGrpMebUUInfo(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员订购关系
     *
     * @author liujy
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpMebProOrder(IData inParam) throws Throwable {
        return GrpMebQryIntf.qryGrpMebProOrder(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID、集团管理员电话号码查询集团成员信息
     *
     * @author liujy
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpMebInfo(IData inParam) throws Throwable {
        return GrpMebQryIntf.qryGrpMebInfo(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID、成员电话号码查询集团成员个人业务信息
     *
     * @author liujy
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpMebBus(IData inParam) throws Throwable {
        return GrpMebQryIntf.qryGrpMebBus(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID进行集团客户对公开户号码查询
     *
     * @author liujy
     * @param inParam
     * @return
     * @throws Throwable
     */

    public IDataset qryGrpPubUserInfo(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryGrpPubUserInfo(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID进行集团客户对公托收号码查询
     *
     * @author liujy
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpAcctUserInfo(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryGrpAcctUserInfo(inParam, getPagination());
    }

    /**
     * 查询动力100产品信息
     *
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryUserPowerProductInfo(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryUserPowerProductInfo(inParam);
    }

    /**
     * 根据集团编号GROUP_ID、ProductId,SEND_CODE查询, 提供给端对端调用; 进行集团变更或是集团注销时, 查询用户的相关信息, 资费、资费参数、产品属性;
     *
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryBBOSSUserOrderInfo(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryBBOSSUserOrderInfo(inParam, null);
    }

    /**
     * 根据集团编号PRODUCT_ID，GROUP_ID查询BBOSS办理过的子产品信息
     *
     * @author liuxx3
     * @date 2014-06-27
     */
    public IDataset qryOperProductForBBOSS(IData inParam) throws Throwable {

        return GrpUserQryIntf.qryOperProductForBBOSS(inParam, getPagination());
    }

    /**
     * 输入：商品实例ID或产品实例ID（下表格中） 输出：GROUP_ID 集团ID EPARCHY_CODE 集团所在地市
     *
     * @author liuxx3
     * @date 2014-06-27
     */
    public IDataset qryDataByMerchOfferId(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryDataByMerchOfferId(inParam, getPagination());
    }

    /**
     * 为esop查询BBOSS商品订单号和产品订单号 add by esop
     *
     * @author liuxx3
     * @date 2014-06-30
     */
    public IDataset getBBossOrderInfoForEsop(IData inParam) throws Throwable {
        return GrpUserQryIntf.getBBossOrderInfoForEsop(inParam, getPagination());
    }

    /**
     * 查询集团可以订购的产品接口
     *
     * @author liuxx3
     * @date 2014-06-30
     */
    public IDataset getCanOpenProduct(IData inParam) throws Throwable {
        return GrpUserQryIntf.getCanOpenProduct(inParam, getPagination());
    }

    /**
     * 查询集团用户订购了的套餐和可以订购的套餐接口
     *
     * @author liuxx3
     * @date 2014-07-01
     */
    public IDataset qryGrpUserDiscntInfos(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryGrpUserDiscntInfos(inParam, getPagination());
    }

    /**
     * 根据集团编码查询集团已经订购的产品信息
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpUserInfo(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryGrpUserInfo(inParam, getPagination());
    }

    /**
     * 根据集团编码查询ADC用户信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpAdcUserInfo(IData inParam) throws Exception {
        return GrpUserQryIntf.qryGrpAdcUserInfo(inParam, getPagination());
    }

    /**
     * 查询用户平台服务参数信息
     *
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpUserPlatSvcParam(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryGrpUserPlatSvcParam(inParam, getPagination());
    }

    /**
     * 根据产品ID获取产品类型
     *
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrProductTypeByProductId(IData inParam) throws Throwable {
        return GrpProductQryIntf.qryGrpProductTypeByProductId(inParam, null);
    }

    /***************************** IMS查询开始 ************************************/
    /**
     * 成员登录权限
     *
     * @param data
     *            data.put("SERIAL_NUMBER", "13707340586"); data.put("X_GETMODE", "0"); data.put("IMS_PASSWORD", "0");
     * @return
     * @throws Throwable
     */
    public IDataset checkUserPassword(IData data) throws Throwable {
        return GrpIMSQryIntf.checkUserPassword(data, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID进行集团订购IMS业务的用户信息查询
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset getGrpImsUserInfos(IData data) throws Throwable {
        return GrpIMSQryIntf.getGrpImsUserInfo(data, getPagination());
    }

    /**
     * 根据成员标识、集团用户标识查询成员订购的产品套餐情况
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryMebDiscntInfos(IData data) throws Throwable {
        return GrpIMSQryIntf.qryImsUserDiscnts(data, getPagination());
    }

    /**
     * 根据集团用户标识查询集团产品属性
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpUserProdAttr(IData data) throws Throwable {
        return GrpIMSQryIntf.getGrpUserAttr(data, getPagination());
    }

    /**
     * 成员订购IMS产品参数信息
     * 
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryMebProductAttr(IData data) throws Throwable {
        return GrpIMSQryIntf.getImsParam(data);
    }

    /**
     * 集团群组成员的查询
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpTeam(IData data) throws Throwable {
        return GrpIMSQryIntf.getGroupTeam(data);
    }

    /**
     * 根据员工工号查询票据信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryStaffTicket(IData inParam) throws Exception {
        return StaffTicketQryIntf.qryStaffTicket(inParam);
    }

    /**
     * 查询短号信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryShort(IData inParam) throws Exception {
        return GrpVpnQryIntf.qryShort(inParam);
    }

    /**
     * 集团群组成员的查询
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryTeamMeb(IData data) throws Throwable {
        return GrpIMSQryIntf.getTeamMeb(data);
    }

    /**
     * 集团产品成员查询
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryMebLists(IData data) throws Throwable {
        return GrpIMSQryIntf.qryMebLists(data, getPagination());
    }

    /**
     * 通过user_id查询一号通成员的主叫、被叫一号通号码
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryMebYhtNum(IData data) throws Throwable {
        return GrpIMSQryIntf.GetQryMebYhtNum(data, getPagination());
    }

    /**
     * 校验成员的服务密码
     *
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset checkUserPasswordByPer(IData inParam) throws Throwable {
        return GrpIMSQryIntf.checkUserPasswordByPer(inParam, getPagination());
    }

    /**
     * IMS集团级黑白名单查询
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpBWLists(IData data) throws Throwable {
        return UserBlackWhiteInfoQry.QryGrpBWLists(data, getPagination());
    }

    /**
     * 查询成员用户设置的黑白名单信息
     *
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset getMemberBwInfo(IData inParam) throws Throwable {
        return GrpIMSQryIntf.getBwInfo(inParam);
    }

    /**
     * 根据集团用户标识，查询集团用户订购了的优惠和可以订购的优惠。
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpUserDiscnts(IData data) throws Throwable {
        return GrpIMSQryIntf.getGrpUserDiscnts(data, getPagination());
    }

    /***************************** IMS查询结束 ************************************/

    /**
     * chenyi 2014-6-10 根据group_id查询集团信息化产品
     *
     * @param data
     * @param pg
     * @return
     */
    public IDataset getGroupProductInfo(IData data) throws Throwable {
        return GrpUserQryIntf.getGroupProductInfo(data, getPagination());
    }

    /**
     * chenyi 2014-6-10 集团产品信息查询
     *
     * @param data
     * @param pg
     * @return
     */
    public IDataset getUserProductByGroupId(IData data) throws Throwable {
        return GrpUserQryIntf.getUserProductByGroupId(data, getPagination());
    }

    /**
     * chenyi 2014-6-10 根据 GROUP_ID 查询集团客户已开通业务的接口
     *
     * @param data
     * @param pg
     * @return
     */
    public IDataset getqryECBizByGrpID(IData data) throws Throwable {
        return GrpUserQryIntf.getqryECBizByGrpID(data, getPagination());
    }

    /**
     * 根据USER_ID判断是否集团付费号码
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset isGrpSerialNumber(IData inParam) throws Exception {
        return GrpUserQryIntf.isGrpSerialNumber(inParam);
    }

    /**
     * 集团成员关系查询 通过用户A的用户标识和用户B的服务号码及关系类型获取用户与用户的关系
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getRelaInfoBySnaSnbRelatypecode(IData inParam) throws Exception {
        return GrpRelaUUInfoQryIntf.getRelaInfoBySnaSnbRelatypecode(inParam);
    }

    /**
     * 集团成员短号,长短号互查
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getRelaAndVpnInfoBySnOrShortcode(IData inParam) throws Exception {
        return GrpRelaUUInfoQryIntf.getRelaAndVpnInfoBySnOrShortcode(inParam);
    }

    /**
     * 集团产品成员优惠查询
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getProductMebDiscntByProductIdAndStaffId(IData inParam) throws Exception {
        return GrpProductQryIntf.getProductMebDiscntByProductIdAndStaffId(inParam);
    }

    /**
     * vpn信息查询 外围接口
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getUserVpnInfo(IData inParam) throws Exception {
        return GrpVpnQryIntf.getUserVpnInfo(inParam);
    }

    /**
     * vpmn 信息查询 短信营业厅
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getUserVpnInfo2ShortMes(IData inParam) throws Exception {
        return GrpVpnQryIntf.getUserVpnInfo2ShortMes(inParam);
    }

    /**
     * 自动生成短号
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getAutoGenShortCode(IData inParam) throws Exception {
        return GrpVpnQryIntf.getAutoGenShortCode(inParam);
    }

    /**
     * 短号校验
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getValidShortCode(IData inParam) throws Exception {
        return GrpVpnQryIntf.getValidShortCode(inParam);
    }

    /**
     * 查询集团vpn 统一付费
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getQryUsrSpePay(IData inParam) throws Exception {
        return GrpVpnQryIntf.getQryUsrSpePay(inParam);
    }

    /**
     * 本月订购次数
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getAddCountMonth(IData inParam) throws Exception {
        return GrpMebQryIntf.getAddCountMonth(inParam);
    }

    /**
     * 得到角色编码
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getRoleCodeInfo(IData inParam) throws Exception {
        return GrpProductQryIntf.getRoleCodeInfo(inParam);
    }

    /**
     * 集团VPMN产品编码和成员优惠编码
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset GetDiscntCodeBySn(IData inParam) throws Exception {
        return GrpVpnQryIntf.GetDiscntCodeBySn(inParam);
    }

    /**
     * 查询判断用户是否VPMN用户并且返回本月办理VPMN成员新增业务的次数
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getCreateVpnCountMonth(IData inParam) throws Exception {
        return GrpVpnQryIntf.getCreateVpnCountMonth(inParam);
    }

    /**
     * 获取集团V网名称和集团订购优惠编码
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset GetVpnNameGrpPackageBySn(IData inParam) throws Exception {
        return GrpVpnQryIntf.GetVpnNameGrpPackageBySn(inParam);
    }

    /**
     * 查询VPMN编码和VPMN名称
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset GetVpnNoBySn(IData inParam) throws Exception {
        return GrpVpnQryIntf.GetVpnNoBySn(inParam);
    }

    /**
     * 为esop查询产品参数信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getProductInfoForEsop(IData inParam) throws Exception {
        return GrpUserProductQryIntf.getProductInfoForEsop(inParam, getPagination());
    }

    /**
     * 集团产品下成员列表查询接口(带分页查询)
     *
     * @author liuxx3
     * @date 2014-07-04
     */
    public IDataset qryGrpMemberListInfo(IData inParam) throws Exception {
        return GrpUserQryIntf.qryGrpMemberListInfo(inParam, getPagination());
    }

    /**
     * 作用：获取集团用户资费
     *
     * @author liuxx3
     * @date 2014-07-04
     */
    public IDataset getGrpUserDiscnt(IData inParam) throws Exception {
        return GrpUserQryIntf.getGrpUserDiscnt(inParam, getPagination());
    }

    /**
     * 查询集团成员用户订购的集团业务(集团V网、集团彩铃、农信通、校讯通、手机邮箱等)
     *
     * @author liuxx3
     * @date 2014-07-08
     */
    public IDataset qryGrpMebOrderInfo(IData inParam) throws Exception {
        return GrpUserQryIntf.qryGrpMebOrderInfo(inParam, getPagination());
    }

    /**
     * 查询集团用户平台服务信息
     *
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryUserGrpPlatSvcInfo(IData inParam) throws Exception {
        return GrpUserQryIntf.qryUserGrpPlatSvcInfo(inParam);
    }

    /**
     * 根据静态参数表的类型查询静态参数
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryDestroyGroupUserKind(IData inparam) throws Exception {
        return GrpUserQryIntf.queryDestroyGroupUserKind();
    }

    /**
     * 根据集团编号GROUP_ID查询集团产品订购关系
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset getGroupUserInfoByGroupId(IData inparam) throws Exception {
        return GrpUserQryIntf.getGroupUserInfoByGroupId(inparam);
    }

    /**
     * 根据集团产品编码查询集团用户客户信息
     *
     * @param data
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpUserCustInfoBySn(IData inParam) throws Throwable {
        return GrpUserQryIntf.qryGrpUserCustInfoBySn(inParam, getPagination());
    }

    /**
     * 根据集团编号GROUP_ID查询集团已订购的可进行集团业务特殊开机的产品
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpSpecialOpenProductByGroupId(IData inparam) throws Exception {
        return GrpUserQryIntf.qryGrpSpecialOpenProductByGroupId(inparam);
    }

    /**
     * 根据集团用户编码USER_ID查询已订购服务信息
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryGrpSpecialOpenProductByUserId(IData inparam) throws Exception {
        return GrpUserQryIntf.qryGrpSpecialOpenProductByUserId(inparam);
    }

    /**
     * 自由充成员查询列表
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getGrpGfffMemInfoBySerialNumber(IData inParam) throws Exception {
        return GrpGfffUserQryIntf.qryGrpGfffMebOrderInfo(inParam, getPagination());
    }

    /**
     * 定额统付集团查询
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset getGrpGfffMemDiscntCountBySn(IData inParam) throws Exception {
        return GrpGfffUserQryIntf.qryGrpGfffMebDiscntCountBySn(inParam);
    }

    /**
     * 根据集团编号GROUP_ID查询集团管理员的电话号码
     * 
     * @param inParam
     * @return
     * @throws Throwable
     */
    public IDataset qryGrpMgrSerialNumberByGroupId(IData inParam) throws Throwable {
        return GrpGfffUserQryIntf.qryGrpMgrSerialNumberByGroupId(inParam);
    }

    /**
     * 作用：获取集团用户关联的服务与资费
     * 
     * @author liuxx3
     * @date 2015-11-17
     */
    public IDataset getGrpUserServiceDiscnt(IData inParam) throws Exception {
        return GrpUserQryIntf.getGrpUserServiceDiscnt(inParam, getPagination());
    }

    /**
     * 作用：获取集团的服务与资费关联的子服务资费
     * 
     * @author liuxx3
     * @date 2015-11-17
     */
    public IDataset getGrpUserServiceDiscnt4Child(IData inParam) throws Exception {
        return GrpUserQryIntf.getGrpUserServiceDiscnt4Child(inParam, getPagination());
    }

    /**
     * 根据集团专线名称查询集团专线的信息
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IData qryGrpUserDatalineByName(IData inParam) throws Exception {
        return GrpUserQryIntf.qryGrpUserDatalineByName(inParam);
    }

    /**
     * ESOP查询稽核员工
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryStaffinfoForESOPNEW(IData inParam) throws Exception {
        return GrpUserQryIntf.qryStaffinfoForESOPNEW(inParam);
    }

    /**
     * ESOP查询工单稽核信息
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryAuditinfoForESOP(IData inParam) throws Exception {
        return GrpUserQryIntf.qryAuditinfoForESOP(inParam);
    }

    /**
     * esop通过专线实列号查询专线名称
     *
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset qryUserDatalineByProductNO(IData inParam) throws Exception {
        return GrpUserQryIntf.qryUserDatalineByProductNO(inParam);
    }
}
