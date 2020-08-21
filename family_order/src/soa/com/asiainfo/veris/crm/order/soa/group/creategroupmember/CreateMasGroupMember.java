
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateMasGroupMemberReqData;
import com.asiainfo.veris.crm.order.soa.group.common.query.LimitBlackwhiteBean;
import com.asiainfo.veris.crm.order.soa.group.param.mas.MemParams;

public class CreateMasGroupMember extends CreateGroupMember
{

    protected CreateMasGroupMemberReqData reqData = null;

    public CreateMasGroupMember() throws Exception
    {
        super();
    }

    /**
     * adcmas不插UU关系表，UU关系插在TF_B_TRADE_RELATION_BB j2ee项目改造
     * 
     * @author liaolc
     * @throws Exception
     *             2013-08-29
     */
    @Override
    public void actTradeRelationUU() throws Exception
    {
        IData relaData = new DataMap();
        relaData.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", reqData.getMemRoleB());
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());

        // 处理产品级控制UU关系生效时间
        dealRelationStartDate(relaData);

        super.addTradeRelationBb(relaData);

    }

    /**
     * 作用:生成其它台帐数据（生成台帐后）
     * 
     * @author liaolc 2013-09-02 23:12
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        this.regBlackWhiteAndMemPlatsvc();
        // 移动oa考核版,若成员开通了apn服务,那么需要维护apn接入点参数
        if (("9127").equals(reqData.getGrpUca().getProductId()))
        {
            this.regApnEntryPoint();// 维护apn接入点信息
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateMasGroupMemberReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateMasGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // req产品控制信息
        mekReqProductCtrlInfo();

    }

    private void mekReqProductCtrlInfo() throws Exception
    {

        String productId = reqData.getUca().getProductId();
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUser);
        // 得到是否向黑白名单台帐表中拼套餐信息
        String synDiscntFlag = ctrlInfo.getAttrValue("SynDiscntFlag");
        reqData.setSynDiscntFlag(synDiscntFlag);
    }

    /**
     * 新增移动OA考核版apn服务时,登记APN接入点个性信息
     * 
     * @throws Exception
     */
    private void regApnEntryPoint() throws Exception
    {
        IDataset svcset = reqData.cd.getSvc();
        for (int i = 0; i < svcset.size(); i++)
        {
            IData svc = svcset.getData(i);
            String svcstr = svc.getString("ELEMENT_ID", "");
            String state = svc.getString("MODIFY_TAG", "");
            if (svcstr.equals("952701") && "0".equals(state))// 如果是新增,并且服务为移动OAAPN服务,需要插apn接入点参数给userattr
            {
                IData param = new DataMap();
                param.put("EXTEND_VALUE", reqData.getGrpUca().getCustId());
                param.put("EXTEND_TAG", "ApnPoint");
                param.put("RSRV_STR1", reqData.getGrpUca().getProductId());// 产品ID
                param.put("RSRV_STR2", "9327");// 服务ID

                // select * from TF_F_CUST_GROUP_EXTEND t where t.extend_tag='ApnPoint'; 生产库没有符合条件的数据，这个根本没有使用？？
                IDataset groupextendset = GrpExtInfoQry.getGrpExtendInfoByCustIdRS1RS2(param);

                String apnEntryPoint = "";
                if (IDataUtil.isNotEmpty(groupextendset))
                {
                    for (int j = 0; j < groupextendset.size(); j++)
                    {
                        IData groupextend = groupextendset.getData(j);
                        String extendtag = groupextend.getString("EXTEND_TAG", "");
                        if (extendtag.equals("ApnPoint"))
                        {
                            apnEntryPoint = groupextend.getString("RSRV_STR3", "");
                            break;
                        }
                    }
                }

                if (StringUtils.isEmpty(apnEntryPoint))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_738);
                }

                IDataset otherSet = new DatasetList();
                IData other = new DataMap();
                other.put("USER_ID", reqData.getUca().getUserId());

                other.put("RSRV_VALUE_CODE", "MOA");
                other.put("RSRV_VALUE", apnEntryPoint);
                other.put("RSRV_STR1", "1");
                other.put("RSRV_STR2", svc.getString("INST_ID", ""));

                other.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                other.put("START_DATE", svc.getString("START_DATE", ""));
                other.put("END_DATE", svc.getString("END_DATE", ""));
                other.put("REMARK", "移动OA考核版APN接入点");
                otherSet.add(other);
                addTradeOther(otherSet);
                break;
            }
        }
    }

    /**
     * 作用:处理黑白名单 和成员 业务平台参数信息,
     * <p>
     * 参数从页面 获取和在TF_F_USER_GRP_PLATSVC表里查集团订购的平台信息
     * </p>
     * 
     * @author liaolc 2013-09-03 10:06
     * @throws Exception
     */
    public void regBlackWhiteAndMemPlatsvc() throws Exception
    {

        IDataset specialServiceset = reqData.cd.getSpecialSvcParam();
        if (IDataUtil.isEmpty(specialServiceset))
        {
            specialServiceset = new DatasetList();
        }

        for (int i = 0, size = specialServiceset.size(); i < size; i++)
        {
            IDataset specialServicDataset = (IDataset) specialServiceset.get(i);
            IData specialServic = specialServicDataset.getData(1);// 0
            IData platsvcdata = specialServic.getData("PLATSVC");// platsvc表个性参数
            platsvcdata = IDataUtil.replaceIDataKeyDelPrefix(platsvcdata, "pam_");
            String instId = specialServic.getString("INST_ID");
            String serviceId = platsvcdata.getString("SERVICE_ID");
            String grpUserId = reqData.getGrpUca().getUser().getUserId();

            IData platsvc = MemParams.getUserAPlatSvcParam(grpUserId, serviceId);

            if (platsvc.isEmpty())
            {
                CSAppException.apperr(GrpException.CRM_GRP_25);
            }
            // 20090601 业务状态非正常商用时，强制校验
            if (!"A".equals(platsvc.getString("BIZ_STATUS")))
            {
                CSAppException.apperr(GrpException.CRM_GRP_637, serviceId);
            }
            setRegBlackWhite(platsvc, platsvcdata, instId);
            setRegGrpMebPlatSvc(platsvc, platsvcdata, instId);
        }
    }

    /**
     * 作用:处理成员新增时的黑白名单表
     * 
     * @author liaolc 2013-09-02 23:29
     * @param platsvc
     *            集团在TF_F_USER_GRP_PLATSVC表里的平台信息
     * @param platsvcdata
     *            在页面上获取的成员个性化参数
     * @throws Exception
     */
    public void setRegBlackWhite(IData platsvc, IData platsvcdata, String instId) throws Exception
    {

        IData data = new DataMap();
        data.put("INST_ID", instId);
        data.put("USER_ID", reqData.getUca().getUserId());
        String serialNumber = reqData.getUca().getUser().getSerialNumber();// 成员手机号
        String bizInCode = platsvc.getString("BIZ_IN_CODE");// 服务代码
        String custName = reqData.getUca().getCustPerson().getCustName();// 成员客户名
        String bizattr = platsvc.getString("BIZ_ATTR", "");
        String usertypecode = "";

		if ("0".equals(bizattr)) {// 订购关系
			usertypecode = "S";
		} else if ("1".equals(bizattr)) {// 白名单
			String dataId = platsvc.getString("RSRV_TAG2");
			int count = platsvc.getInt("RSRV_NUM1");// 成员数量
			if (!"4".equals(dataId))// VIP级成员数量不受限制
			{
				String typeId = "GRP_PLAT_RSRV_TAG2";
				IData staticInfo = StaticInfoQry.getStaticInfoByTypeIdDataId(
						typeId, dataId);// 获取该集团允许的最大成员数量
				if (IDataUtil.isNotEmpty(staticInfo)) {
					// String ecUserId = reqData.getGrpUca().getUserId();
					// String serviceId = platsvcdata.getString("SERVICE_ID");
					// String userTypeCode = "W"; // 白名单用户
					// IData gblackWhiteUserCount =
					// UserBlackWhiteInfoQry.qryblackWhiteByServiceIdEcUserId(ecUserId,
					// serviceId, userTypeCode);// 获取该集团当前成员数量
					// if (IDataUtil.isNotEmpty(gblackWhiteUserCount))
					// {
					if (staticInfo.getInt("PDATA_ID") <= count) {
						// common.error("该集团的成员总数已经超额,请联系管理员:目前该集团允许成员总数为："+staticInfo.getString("PDATA_ID")+" ;该集团现有成员总数为："+gblackWhiteUserCount.getString("USER_COUNT"));
						CSAppException.apperr(CrmCommException.CRM_COMM_1025,
								staticInfo.getString("PDATA_ID"), count);
					}
					// }
				}
			}
			usertypecode = "W";
		} else if ("2".equals(bizattr)) {// 黑名单
			usertypecode = "B";
		} else if ("3".equals(bizattr)) { // 限制次数的白名单
			usertypecode = "XW";
		} else if ("4".equals(bizattr)) { // 点播业务，不能在BOSS侧订购
			CSAppException.apperr(CrmCommException.CRM_COMM_328);
		}

        // 2013-10-16 -------------- liqi添加的需求----start---
        // G代表从网关过来的
        if ("G".equals(CSBizBean.getVisit().getInModeCode()))
        {
            if ("2".equals(bizattr))
            // 黑名单
            {
                LimitBlackwhiteBean.insertLimitBlackWhite(serialNumber, bizInCode, custName);
            }
            if ("1".equals(bizattr))
            // 白名单
            {
                LimitBlackwhiteBean.deleteLimitBlackWhite(serialNumber, bizInCode, custName);
            }
        }
        // 非G代表从网关过来
        else
        {
            if ("1".equals(bizattr))
            // 白名单
            {
                IDataset limitDataset = LimitBlackwhiteBean.queryLimitBlackWhite(serialNumber, bizInCode);
                if (IDataUtil.isNotEmpty(limitDataset))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_1027, serialNumber);
                }
            }
        }
        // -------2013-10-16 liqi添加的需求----------end--------------------------------------------

        data.put("OPER_STATE", "01"); // 01表示新增 2013-01-19 J2EE项目修改，与接口规范保持一致。
        data.put("USER_TYPE_CODE", usertypecode);
        data.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());// 集团 实例标识
        data.put("SERV_CODE", platsvc.getString("BIZ_IN_CODE"));
        data.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber()); //
        data.put("GROUP_ID", platsvc.getString("GROUP_ID"));
        data.put("BIZ_CODE", platsvc.getString("BIZ_CODE"));
        data.put("BIZ_NAME", platsvc.getString("BIZ_NAME"));
        data.put("BIZ_DESC", platsvc.getString("BIZ_DESC"));
        // 是否立即生效以 分散账期为准
        data.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        data.put("OPR_EFF_TIME", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        data.put("REMARK", "");
        data.put("RSRV_NUM1", "0");
        data.put("RSRV_NUM2", "0");
        data.put("RSRV_NUM3", "0");
        data.put("RSRV_NUM4", "0");
        data.put("RSRV_NUM5", "0");
        data.put("RSRV_STR1", "");
        data.put("RSRV_STR2", "");
        data.put("RSRV_STR3", "");

        data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空：
        // 走服务开通发指令,0：不走服务开通不发指令

        // ------------ end --------------------------
        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");
        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", "");
        data.put("RSRV_TAG2", platsvcdata.getString("RSRV_TAG2", "0"));// 0-实时接口 1-文件接口 td_b_attr_biz 默认取0
        // 放到blackwhite表的rsrv_tag2字段
        data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3", "0")); // J2EE前，标识是否走服务开通，现在没用了

        data.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE"));
        data.put("BIZ_IN_CODE_A", platsvc.getString("EC_BASE_IN_CODE_A"));
        data.put("SERVICE_ID", platsvcdata.getString("SERVICE_ID"));
        String isfree = platsvc.getString("BILLING_TYPE", "");
        String billingmode = platsvc.getString("BILLING_MODE", "");
        String membillingtype = "";
        if (isfree.equals("00"))// 整个业务免费时
        {
            membillingtype = "2";// 免费
        }
        else if (billingmode.equals("4"))// 上行按集团 下行按集团时为免费
        {
            membillingtype = "0";// 集团付费
        }
        else if (billingmode.equals("2"))// 上行按用户 下行按用户时为个人付费
        {
            membillingtype = "1";// 个人付费
        }
        else
        // 按集团付费
        {
            membillingtype = "0";// 集团付费
        }
        data.put("BILLING_TYPE", membillingtype);
        data.put("EXPECT_TIME", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        data.put("PLAT_SYNC_STATE", platsvcdata.getString("PLAT_SYNC_STATE"));
        data.put("EC_SERIAL_NUMBER", platsvc.getString("SERIAL_NUMBER"));

        super.addTradeBlackwhite(data);
    }

    /**
     * 作用:处理成员新增时的平台信息TF_F_USER_GRP_MEB_PLATSVC表
     * 
     * @author liaolc 2013-09-02 23:43
     * @param specialServiceset
     * @throws Exception
     */
    public void setRegGrpMebPlatSvc(IData platsvc, IData platsvcdata, String instId) throws Exception
    {

        IData memplatdata = new DataMap();
        memplatdata.put("INST_ID", instId);
        memplatdata.put("USER_ID", reqData.getUca().getUser().getUserId());
        memplatdata.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());
        memplatdata.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());
        memplatdata.put("EC_SERIAL_NUMBER", platsvc.getString("SERIAL_NUMBER"));

        memplatdata.put("SERV_CODE", platsvc.getString("SERV_CODE", ""));
        memplatdata.put("BIZ_CODE", platsvc.getString("BIZ_CODE", ""));
        memplatdata.put("BIZ_NAME", platsvc.getString("BIZ_NAME", ""));

        memplatdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        // 是否立即生效以 分散账期为准
        memplatdata.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());// 开始时间
        memplatdata.put("END_DATE", SysDateMgr.getTheLastTime());

        memplatdata.put("REMARK", "");

        memplatdata.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE"));
        memplatdata.put("SERVICE_ID", platsvcdata.getString("SERVICE_ID"));

        addTradeGrpMebPlatsvc(memplatdata);
    }

}
