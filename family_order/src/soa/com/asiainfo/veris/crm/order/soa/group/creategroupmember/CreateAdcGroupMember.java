
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateAdcGroupMemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.common.query.LimitBlackwhiteBean;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class CreateAdcGroupMember extends CreateGroupMember
{
    protected CreateAdcGroupMemberReqData reqData = null;

    public CreateAdcGroupMember() throws Exception
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

        this.regSetAttr();

        this.regBlackWhiteAndMemPlatsvc();

        this.regPayrelation();

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateAdcGroupMemberReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateAdcGroupMemberReqData) getBaseReqData();
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

        String productId = reqData.getGrpUca().getProductId();
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateMember);
        // 得到是否向黑白名单台帐表中拼套餐信息
        String synDiscntFlag = ctrlInfo.getAttrValue("SynDiscntFlag");
        reqData.setSynDiscntFlag(synDiscntFlag);

        // 学护卡家长号码
        String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IDataset productParamList = reqData.cd.getProductParamList(mebBaseProductId);

        if (productParamList!= null) {
            for (int i = 0;  i < productParamList.size(); i++) {
                IData paramData = productParamList.getData(i);
                if ("NOTIN_FAM_SN".equals(paramData.getString("ATTR_CODE"))) {
                    reqData.setFamilyNumber(paramData.getString("ATTR_VALUE", ""));
                }
            }
        }
    }

    /**
     *  学护卡家长号码统付
     */
    public void regPayrelation() throws Exception {
        if(StringUtils.isNotEmpty(reqData.getFamilyNumber())) {
            String mebUserId = reqData.getUca().getUserId();
            String fanNum = reqData.getFamilyNumber();

            boolean isExistsPay = false;
            IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(mebUserId, "56", "2");
            if (ds.size() > 0) {
                String xnUserId = ds.getData(0).getString("USER_ID_A", "");
                IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", fanNum);
                if (userInfos.size() > 0)
                {
                    String fanNumUserId = userInfos.getData(0).getString("USER_ID");
                    IDataset dsFam = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(fanNumUserId, "56", "1");
                    if (dsFam.size() > 0 && xnUserId.equals(dsFam.getData(0).getString("USER_ID_A"))) {
                        isExistsPay = true;
                    }
                }
            }

            if (!isExistsPay) {
                this.regTradeRelation();
                this.actTradefanNumPayRelation();
            }
        }
    }

    /**
     * 学护卡特殊处理亲情号码
     *
     * @throws Exception
     */
    public void regSetAttr() throws Exception
    {
        String productId = reqData.getGrpUca().getProductId();
        IDataset dctDataset = reqData.cd.getDiscnt();// 前台页面办理的资费
        //  NOTIN_FAM_SN_PARAM_LIST0
        String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData productParamMap = reqData.cd.getProductParamMap(mebBaseProductId);

        IDataset famSnList = new DatasetList();
        IDataset dataset = new DatasetList();
        if (productParamMap != null && productParamMap.getString("NOTIN_FAM_SN_PARAM_LIST0") != null) {
            famSnList = new DatasetList(productParamMap.getString("NOTIN_FAM_SN_PARAM_LIST0"));

            IData famSnMap = null;
            for (int i=0; i<famSnList.size(); i++) {
                famSnMap = famSnList.getData(i);
                if (!"".equals(famSnMap.getString("FAMNUM", ""))) {
                    IData map = new DataMap();
                    map.put("INST_TYPE", "D");
                    map.put("RELA_INST_ID", dctDataset.getData(0).getString("INST_ID"));
                    map.put("ATTR_CODE", i+1);
                    map.put("ATTR_VALUE", famSnMap.getString("FAMNUM", "-1"));
                    map.put("START_DATE", dctDataset.getData(0).getString("START_DATE"));
                    map.put("END_DATE", SysDateMgr.getTheLastTime());
                    map.put("INST_ID", SeqMgr.getInstId());
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    dataset.add(map);
                }
            }
        }
        if ("10005744".equals(productId) && IDataUtil.isNotEmpty(dctDataset))
        {
            for (int j = 0, jSize = dctDataset.size(); j < jSize; j++)
            {
                for (int i = famSnList.size() + 1; i < 6; i++)
                {
                    IData map = new DataMap();
                    map.put("INST_TYPE", "D");
                    map.put("RELA_INST_ID", dctDataset.getData(j).getString("INST_ID"));
                    map.put("ATTR_CODE", i);
                    map.put("ATTR_VALUE", "-1");
                    map.put("START_DATE", dctDataset.getData(j).getString("START_DATE"));
                    map.put("END_DATE", SysDateMgr.getTheLastTime());
                    map.put("INST_ID", SeqMgr.getInstId());
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    dataset.add(map);

                }
            }
            super.addTradeAttr(dataset);
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
        int  count = platsvc.getInt("RSRV_NUM1");// 成员数量
        String custName = reqData.getUca().getCustPerson().getCustName();// 成员客户名
        String bizattr = platsvc.getString("BIZ_ATTR", "");
        String usertypecode = "";

		if ("0".equals(bizattr)) {// 订购关系
			usertypecode = "S";
			// data.put("OPER_STATE", "2");// 订购 老规范2.5
		} else if ("1".equals(bizattr)) {// 白名单
			String dataId = platsvc.getString("RSRV_TAG2");
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
		}
        else if ("2".equals(bizattr))
        {// 黑名单
            usertypecode = "B";
            // data.put("OPER_STATE", "0");// 加入黑名单 老规范2.5
        }
        else if ("3".equals(bizattr))
        { // 限制次数的白名单
            usertypecode = "XW";
        }
        else if ("4".equals(bizattr))
        { // 点播业务，不能在BOSS侧订购
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

        data.put("OPER_STATE", "01");// 01表示新增 新规范3.0.1。
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
        // 学护卡付费号码
        if(StringUtils.isNotEmpty(reqData.getFamilyNumber())) {
            data.put("RSRV_STR3", reqData.getFamilyNumber());
        } else {
            data.put("RSRV_STR3", "");
        }

        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");
        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", "");
        data.put("RSRV_TAG2", platsvcdata.getString("RSRV_TAG2", "0"));// 0-实时接口 1-文件接口 td_b_attr_biz 默认取0
        // 放到blackwhite表的rsrv_tag2字段

        data.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE"));
        data.put("BIZ_IN_CODE_A", platsvc.getString("SI_BASE_IN_CODE_A"));
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
        data.put("PLAT_SYNC_STATE", platsvcdata.getString("PLAT_SYNC_STATE"));
        data.put("EC_SERIAL_NUMBER", platsvc.getString("SERIAL_NUMBER"));
        data.put("EXPECT_TIME", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());

        if ("9188".equals(reqData.getGrpUca().getProductId()))// 如果为商信通产品,则用户期望生效时间 加一天
        {
            data.put("EXPECT_TIME", SysDateMgr.getAddHoursDate(data.getString("EXPECT_TIME"), 24));
        }
        IDataset dctDataset = reqData.cd.getDiscnt();// 前台页面办理的资费
        String isSysDisFlag = reqData.getSynDiscntFlag();
        String strDntList = "";
        String strDntName = "";

        if (IDataUtil.isNotEmpty(dctDataset) && !"10009805".equals(reqData.getGrpUca().getProductId()))// 海南往adc平台只传一个优惠,所以如果有优惠,先对优惠的起始时间进行排序,然后取最后生效的这条
        {
            DataHelper.sort(dctDataset, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            strDntList = dctDataset.getData(0).getString("ELEMENT_ID", "-1");
            strDntName = UDiscntInfoQry.getDiscntNameByDiscntCode(strDntList);
        }
        else if (IDataUtil.isNotEmpty(dctDataset) && "10009805".equals(reqData.getGrpUca().getProductId()))
        {
            for (int i = 0; i < dctDataset.size(); i++)
            {
                IDataset tempLists = AttrBizInfoQry.getBizAttrByDynamic(platsvcdata.getString("SERVICE_ID"), "S", platsvcdata.getString("SERVICE_ID"), "SvcBindDiscnt", null);
                String strElementId = dctDataset.getData(i).getString("ELEMENT_ID", "-1");
               
                if (IDataUtil.isNotEmpty(tempLists))
                {
                    String strAttrValue = tempLists.getData(0).getString("ATTR_VALUE", "-1");
                    if (strElementId.equals(strAttrValue))
                    {
                        strDntList = strElementId;
                        strDntName = UDiscntInfoQry.getDiscntNameByDiscntCode(strDntList);
                        break;
                    }
                }
            }
        }
        if ("true".equals(isSysDisFlag))// 对于需要同步优惠给adc平台的 则保存在这字段
        {
            data.put("RSRV_STR4", strDntList);// 存优惠编码
            data.put("RSRV_STR5", strDntName);// 存优惠名称
        }
        else
        {
            data.put("RSRV_STR4", "");
            data.put("RSRV_STR5", "");
        }

        data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// 1或者是空走服务开通发指令,0：不走服务开通不发指令
        data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3", "0"));// 标识是否走服务开通 0 正常走服务开通模式 1 ADC平台 2 行业网关

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

        super.addTradeGrpMebPlatsvc(memplatdata);
    }

    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);
        map.put("INST_TYPE", map.getString("INST_TYPE", ""));
        map.put("RELA_INST_ID", map.getString("RELA_INST_ID", ""));
        map.put("INST_ID", map.getString("INST_ID", ""));
        map.put("ATTR_CODE", map.getString("ATTR_CODE", ""));
        map.put("ATTR_VALUE", map.getString("ATTR_VALUE", ""));
        map.put("START_DATE", map.getString("START_DATE", getAcceptTime()));// 起始时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 终止时间
        // 状态属性：0-增加，1-删除，2-变更
        map.put("MODIFY_TAG", map.getString("MODIFY_TAG", ""));
        map.put("REMARK", map.getString("REMARK", "")); // 备注
        map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "")); // 预留数值1
        map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "")); // 预留数值2
        map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "")); // 预留数值3
        map.put("RSRV_NUM4", map.getString("RSRV_NUM4", "")); // 预留数值4
        map.put("RSRV_NUM5", map.getString("RSRV_NUM5", "")); // 预留数值5
        map.put("RSRV_STR1", map.getString("RSRV_STR1", "")); // 预留字段1
        map.put("RSRV_STR2", map.getString("RSRV_STR2", "")); // 预留字段2
        map.put("RSRV_STR3", map.getString("RSRV_STR3", "")); // 预留字段3
        map.put("RSRV_STR4", map.getString("RSRV_STR4", "")); // 预留字段4
        map.put("RSRV_STR5", map.getString("RSRV_STR5", "")); // 预留字段5
        map.put("RSRV_DATE1", map.getString("RSRV_DATE1", "")); // 预留日期1
        map.put("RSRV_DATE2", map.getString("RSRV_DATE2", "")); // 预留日期2
        map.put("RSRV_DATE3", map.getString("RSRV_DATE3", "")); // 预留日期3
        map.put("RSRV_TAG1", map.getString("RSRV_TAG1", "")); // 预留标志1
        map.put("RSRV_TAG2", map.getString("RSRV_TAG2", "")); // 预留标志2
        map.put("RSRV_TAG3", map.getString("RSRV_TAG3", "")); // 预留标志3
    }


    /**
     * 学护卡家长号码统付关系登记
     *
     * @throws Exception
     */
    protected void actTradefanNumPayRelation() throws Exception
    {
        String fanNum = reqData.getFamilyNumber();
        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", fanNum);
        String famNumUserId = userInfos.getData(0).getString("USER_ID");

        IData data = new DataMap();
        IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(famNumUserId);
        data.put("ACCT_ID", mainPayRelations.getString("ACCT_ID"));
        data.put("USER_ID", mebUserId);
        data.put("PAYITEM_CODE", "41000"); // 付费帐目编码
        data.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        data.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        data.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        data.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        data.put("DEFAULT_TAG", "0"); // 默认标志
        data.put("LIMIT_TYPE", "0"); // 限定方式：0-不限定，1-金额，2-比例
        data.put("LIMIT", "0"); // 限定值
        data.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        data.put("INST_ID", SeqMgr.getInstId());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        data.put("START_CYCLE_ID", SysDateMgr.getNowCyc());
        data.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());
        data.put("REMARK", "统付成员付费关系");

        super.addTradePayrelation(data);
    }

    public void regTradeRelation() throws Exception
    {
        String fanNum = reqData.getFamilyNumber();
        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();

        // 主号码校验
        this.checkMainSerialBusiLimits();
        // 副号码校验
        IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSn);
        this.checkOtherSerialBusiLimits(userInfo, fanNum , "0");

        IDataset dataset = new DatasetList();
        IData map = null;
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", fanNum);
        String famNumUserId = userInfos.getData(0).getString("USER_ID");
        String custId = userInfos.getData(0).getString("CUST_ID");

        // --------------判断主号是否已办理家庭统付关系---------------
        IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(famNumUserId, "56", "1");
        // ---第一次办理，则要新加主号的UU关系，否则不需要加主号的uu关系
        String userIdA = "-1";
        String serialNumberA = "-1";

        if (IDataUtil.isEmpty(uuDs))
        {
            userIdA = SeqMgr.getUserId();
            //防止虚拟用户手机号码重复，改成56+主号手机号码作为虚拟用户的号码。
            //serialNumberA = "56" + userIdA.substring(userIdA.length() - 8);
            serialNumberA = "56" + fanNum;
            createFamily(userIdA, serialNumberA);

//            // 生成虚拟User
//            IData data = new DataMap();
//            IDataset visualuser = new DatasetList();
//            data.put("USER_ID", userIdA);
//            data.put("SERIAL_NUMBER", serialNumberA);
//            data.put("USER_TYPE_CODE", "0");
//            data.put("USER_STATE_CODESET", "0");
//            data.put("ACCT_TAG", "0");
//            data.put("OPEN_MODE", "0");
//            data.put("CUST_ID", custId);
//            data.put("USECUST_ID", custId);
//            data.put("MPUTE_MONTH_FEE", "0");
//            data.put("NET_TYPE_CODE", "00");
//            data.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
//            data.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
//            data.put("IN_DATE", reqData.getAcceptTime());
//            data.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
//            data.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
//            data.put("OPEN_DATE", reqData.getAcceptTime());
//            data.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
//            data.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
//            data.put("PREPAY_TAG",  "1");
//            data.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
//            data.put("REMOVE_TAG", "0");
//            visualuser.add(data);
//            super.addTradeUser(visualuser);

            map = new DataMap();
            map.put("USER_ID_A", userIdA); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
            map.put("SERIAL_NUMBER_A", serialNumberA); // A服务号码
            map.put("USER_ID_B", famNumUserId); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
            map.put("SERIAL_NUMBER_B", fanNum); // B服务号码
            map.put("RELATION_TYPE_CODE", "56");
            map.put("ROLE_TYPE_CODE", "0");
            map.put("ROLE_CODE_A", "0");
            map.put("ROLE_CODE_B", "1");
            map.put("ORDERNO", "");
            map.put("START_DATE", reqData.getAcceptTime());
            map.put("END_DATE", SysDateMgr.getTheLastTime());
            map.put("INST_ID", SeqMgr.getInstId());
            map.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            map.put("REMARK", "统一付费关系主号");

            map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "0"));// 暂时不知道老系统为什么入这个值
            map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "0"));// 暂时不知道老系统为什么入这个值
            map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "0"));// 暂时不知道老系统为什么入这个值

            dataset.add(map);

        }
        else
        {
            userIdA = uuDs.getData(0).getString("USER_ID_A", "-1");
            serialNumberA = uuDs.getData(0).getString("SERIAL_NUMBER_A", "-1");
        }

        map = new DataMap();
        map.put("USER_ID_A", userIdA); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        map.put("SERIAL_NUMBER_A", serialNumberA); // A服务号码
        map.put("USER_ID_B", mebUserId); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
        map.put("SERIAL_NUMBER_B", mainSn); // B服务号码
        map.put("RELATION_TYPE_CODE", "56");
        map.put("ROLE_TYPE_CODE", "0");
        map.put("ROLE_CODE_A", "0");
        map.put("ROLE_CODE_B", "2");
        map.put("ORDERNO", "0");
        map.put("START_DATE", reqData.getAcceptTime());
        map.put("END_DATE", SysDateMgr.getTheLastTime());
        map.put("INST_ID", SeqMgr.getInstId());
        map.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        map.put("REMARK", "统一付费关系副号");

        map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "0"));// 暂时不知道老系统为什么入这个值

        dataset.add(map);

        super.addTradeRelation(dataset);
    }

    private void createFamily(String userIdA, String virtualSn) throws Exception
    {
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();

        String homeAcctId = SeqMgr.getAcctId();
        String packageId = null;
        String homeCustId = SeqMgr.getCustId();
        // String homeId = SeqMgr.getHomeId();
        String acctDay = uca.getAcctDay();

        // 新增用户
        UserTradeData user = new UserTradeData();
        UserTradeData tempUser = uca.getUser();
        user.setUserId(userIdA);
        user.setCustId(homeCustId);
        user.setUsecustId(homeCustId);
        user.setEparchyCode(tempUser.getEparchyCode());
        user.setCityCode(tempUser.getCityCode());
        user.setUserPasswd(tempUser.getUserPasswd());
        user.setAcctTag("0");
        user.setUserTypeCode("0");
        user.setContractId(tempUser.getContractId());
        user.setSerialNumber(virtualSn);
        user.setPrepayTag("0");
        user.setOpenDate(sysdate);
        user.setOpenMode("0");
        user.setUserStateCodeset("0");
        user.setNetTypeCode("00");
        user.setMputeMonthFee("0");
        user.setInDate(sysdate);
        user.setRemoveTag("0");
        user.setInStaffId(CSBizBean.getVisit().getStaffId());
        user.setInDepartId(CSBizBean.getVisit().getDepartId());
        user.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        user.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        user.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        user.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        user.setModifyTag(BofConst.MODIFY_TAG_ADD);
        super.addTradeUser(user.toData());
        
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(userIdA);
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getUca().getProductId());
        productTD.setProductMode("00");
        productTD.setBrandCode("JTTF");
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getAcceptTime());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");
        super.addTradeProduct(productTD.toData());

        // 新增客户
        CustomerTradeData customer = new CustomerTradeData();
        CustomerTradeData tempCustomer = uca.getCustomer().clone();
        customer.setCustId(homeCustId);
        customer.setCustName(tempCustomer.getCustName());
        customer.setCustName(uca.getCustomer().getCustName());
        customer.setCustType("2");
        customer.setCustState("0");
        customer.setPsptTypeCode(tempCustomer.getPsptTypeCode());
        customer.setPsptId(tempCustomer.getPsptId());
        customer.setOpenLimit(tempCustomer.getOpenLimit());
        customer.setEparchyCode(tempCustomer.getEparchyCode());
        customer.setCityCode(tempCustomer.getCityCode());
        customer.setCustPasswd(tempCustomer.getCustPasswd());
        customer.setDevelopDepartId(tempCustomer.getDevelopDepartId());
        customer.setDevelopStaffId(tempCustomer.getDevelopStaffId());
        customer.setInDepartId(tempCustomer.getInDepartId());
        customer.setInStaffId(tempCustomer.getInStaffId());
        customer.setInDate(tempCustomer.getInDate());
        customer.setRemark("办理学护卡业务添加虚拟客户");
        customer.setModifyTag(BofConst.MODIFY_TAG_ADD);
        customer.setRemoveTag("0");
        super.addTradeCustomer(customer.toData());

        // 新增帐户
        AccountTradeData acct = new AccountTradeData();
        acct.setEparchyCode(uca.getUserEparchyCode());
        acct.setCityCode(uca.getUser().getCityCode());
        acct.setAcctId(homeAcctId);
        acct.setCustId(homeCustId);
        acct.setPayName(uca.getCustomer().getCustName());
        acct.setPayModeCode("0");
        acct.setScoreValue("0");
        acct.setOpenDate(sysdate);
        acct.setRemoveTag("0");
        acct.setBasicCreditValue("0");
        acct.setCreditValue("0");
        acct.setModifyTag(BofConst.MODIFY_TAG_ADD);
        super.addTradeAccount(acct.toData());
    }

    /**
     *
     */
    protected void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);

        map.put("USER_ID_A", map.getString("USER_ID_A", reqData.getGrpUca().getUserId())); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        map.put("SERIAL_NUMBER_A", map.getString("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber())); // A服务号码
        map.put("USER_ID_B", map.getString("USER_ID_B", reqData.getUca().getUserId())); // B用户标识：对应关系类型参数表中的B角，通常为普通用户
        map.put("SERIAL_NUMBER_B", map.getString("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber())); // B服务号码

        map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE", "56"));
        map.put("ROLE_TYPE_CODE", map.getString("ROLE_TYPE_CODE", "0"));
        map.put("ROLE_CODE_A", map.getString("ROLE_CODE_A", "0"));
        map.put("ROLE_CODE_B", map.getString("ROLE_CODE_B", "2"));
        map.put("ORDERNO", map.getString("ORDERNO", "0"));
        map.put("START_DATE", map.getString("START_DATE", reqData.getAcceptTime()));
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime()));
        map.put("INST_ID", map.getString("INST_ID", SeqMgr.getInstId()));

        map.put("MODIFY_TAG", map.getString("MODIFY_TAG"));

        map.put("RSRV_NUM1", map.getString("RSRV_NUM1", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM2", map.getString("RSRV_NUM2", "0"));// 暂时不知道老系统为什么入这个值
        map.put("RSRV_NUM3", map.getString("RSRV_NUM3", "0"));// 暂时不知道老系统为什么入这个值
    }

    /**
     * 校验主卡的业务办理限制
     *
     * @param input
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    private void checkMainSerialBusiLimits() throws Exception
    {
        String serialNumber = reqData.getFamilyNumber();
        RelationTradeData uuTD = new RelationTradeData();
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
        if (userInfos.size() < 1)
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_802, serialNumber);
        }

        String userId = userInfos.getData(0).getString("USER_ID");

        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);

        if (dataset.size() < 1)
        {
            // common.error("991010", "获取TF_F_USER_INFOCHANGE数据异常["+userId+"]！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        // -----6.非正常状态用户不能办理业务----------------
        if (!"0".equals(reqData.getUca().getUser().getUserStateCodeset()))
        {
            // common.error("880015", "该号码["+serialNumber+"]是非正常状态用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_802, serialNumber);
        }
        // -----1.主卡不能是其他家庭统付关系的副卡-----------
        IDataset ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "2");
        if (ds.size() > 0)
        {
            // common.error("880010", "该号码["+serialNumber+"]是其他统一付费关系的副卡，不能作为主卡，请先退出！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_803, serialNumber);
        }
        // -----2.如果该号码存在多条家庭统付关系的主号信息，则提示资料不正常---------
        ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");
        if (ds.size() > 1)
        {
            // common.error("880011", "该号码["+serialNumber+"]存在多条统一付费的主号UU数据，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_804, serialNumber);

        }
        // -----3.随E行、移动公话、8位或11位TD无线固话不可作为主卡-----------------
        ds = CommparaInfoQry.getCommparaByCode1("CSM", "698", "1", productId);
        if (ds.size() > 0)
        {
            // common.error("880012", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_805, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        // -----4.随E行绑定、IP后付费捆绑、一卡双号、一卡付多号业务的副卡不能作为家庭统一付费业务的主卡----
        ds = RelaUUInfoQry.queryLimitUUInfos(userId, "2", "1");
        if (ds.size() > 0)
        {
            // common.error("880013", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_805, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        // -----5.宽带捆绑的副卡不能作为家庭统一付费业务的主卡------------
        /*
         * String serialNumberTmp = "KD_"+serialNumber; ds = FamilyUnionPayUtilBean.queryUserInfoBySn2(pd,
         * serialNumberTmp); if(ds!=null && ds.size()>0){ common.error("880014",
         * "该号码["+serialNumber+"]是[宽带捆绑]用户，不能作为主卡，请确认！"); }
         */
        // -----7.限制某些优惠不能作为主卡-------------------------------

        ds = DiscntInfoQry.queryLimitDiscnts(userId, "1");
        if (ds.size() > 0)
        {
            // common.error("880016", "该号码["+serialNumber+"]存在["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]优惠，不能作为主卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_807, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
    }

    /**
     * 校验副卡的业务办理限制
     *
     * @param checkSnUserInfo
     * @param mainSn
     * @param modifytag
     * @throws Exception
     * @CREATE BY GONGP@2014-5-21
     */
    public void checkOtherSerialBusiLimits(IData checkSnUserInfo, String mainSn, String modifyTag) throws Exception
    {
        UcaData mainSnUcaData = UcaDataFactory.getNormalUca(mainSn);
        String userId = checkSnUserInfo.getString("USER_ID", "");
        String checkSnCityCode = checkSnUserInfo.getString("CITY_CODE");
        String serialNumber = checkSnUserInfo.getString("SERIAL_NUMBER", "");
        String mainSnAcctCityCode = mainSnUcaData.getAccount().getCityCode();
        IDataset ds = null;

        IDataset dataset = UserInfoQry.getUserInfoChgByUserIdNxtvalid(userId);

        if (dataset.size() < 1)
        {
            // common.error("991010", "获取TF_F_USER_INFOCHANGE数据异常["+userId+"]！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_801, userId);
        }
        String productId = dataset.getData(0).getString("PRODUCT_ID");

        // -----8.非正常状态用户不能作为副卡-------------------
        if (!"0".equals(checkSnUserInfo.getString("USER_STATE_CODESET", "")))
        {
            // common.error("889917", "该号码["+serialNumber+"]是非正常状态用户，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_808, serialNumber);
        }
        // -----7.成员号码不能主号码一致-----------------------
        if (serialNumber.equals(mainSn))
        {
            // common.error("889916", "该号码["+serialNumber+"]与主号码["+mainSn+"]一致，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_809, serialNumber, mainSn);
        }
        if ("0".equals(modifyTag))
        {
            if("HNSJ".equals(mainSnAcctCityCode)||"HNHN".equals(mainSnAcctCityCode)){
                if(!mainSnAcctCityCode.equals(checkSnCityCode))
                    CSAppException.apperr(FamilyException.CRM_FAMILY_835);
            }else{
                if("HNSJ".equals(checkSnCityCode)||"HNHN".equals(checkSnCityCode))
                    CSAppException.apperr(FamilyException.CRM_FAMILY_836);
            }
            // -----1.成员号码不能是其他家庭统付关系的副卡-----------
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "2");
            if (ds.size() > 0)
            {
                // common.error("889910", "该号码["+serialNumber+"]是其他统一付费关系的副卡，请先退出！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_811, serialNumber);
            }
            // -----2.成员号码不能是其他家庭家庭统付关系的主卡---------
            ds = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");
            if (ds.size() > 0)
            {
                // common.error("889911", "该号码["+serialNumber+"]是其他统一付费关系的主卡，请先退出！");
                CSAppException.apperr(FamilyException.CRM_FAMILY_812, serialNumber);
            }
        }
        // -----3.移动公话不可作为副卡-----------------
        ds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "698", "2", productId);
        if (IDataUtil.isNotEmpty(ds))
        {
            // common.error("889912", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_813, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }

        // -----4.随E行绑定、IP后付费捆绑、一卡双号、一卡付多号业务的副卡不能作为家庭统一付费业务的副卡----
        ds = RelaUUInfoQry.queryLimitUUInfos(userId, "2", "2");
        if (ds.size() > 0)
        {
            // common.error("889913", "该号码["+serialNumber+"]是["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]用户，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_813, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }
        // -----5.宽带捆绑的副卡不能作为家庭统一付费业务的副卡------------
        /*
         * String serialNumberTmp = "KD_"+serialNumber; ds = FamilyUnionPayUtilBean.queryUserInfoBySn2(pd,
         * serialNumberTmp); if(ds!=null && ds.size()>0){ common.error("889914",
         * "该号码["+serialNumber+"]是[宽带捆绑]用户，不能作为副卡，请确认！"); }
         */
        // -----6.用户存在往月欠费不能作为副卡--------------------------
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        String fee1 = oweFee.size() > 0 ? oweFee.getString("LAST_OWE_FEE") : "0";
        if (Integer.parseInt(fee1) > 0)
        {
            // common.error("889915","该号码["+serialNumber+"]存在往月欠费，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_815, serialNumber);
        }

        // -----7.限制某些优惠不能作为副卡-------------------------------
        ds = DiscntInfoQry.queryLimitDiscnts(userId, "2");
        if (ds.size() > 0)
        {
            // common.error("889918", "该号码["+serialNumber+"]存在["+ds.getData(0).getString("PARAM_NAME",
            // "")+"]优惠，不能作为副卡，请确认！");
            CSAppException.apperr(FamilyException.CRM_FAMILY_816, serialNumber, ds.getData(0).getString("PARAM_NAME", ""));
        }

        //begin QR-20150310-07 898号码是否允许办理统一付费问题 @yanwu add
        String strNetTypeCode = checkSnUserInfo.getString("NET_TYPE_CODE");
        if( "11".equals(strNetTypeCode) || "12".equals(strNetTypeCode)
         || "13".equals(strNetTypeCode) || "14".equals(strNetTypeCode) || "15".equals(strNetTypeCode)  ){
            CSAppException.apperr(FamilyException.CRM_FAMILY_834, serialNumber);
        }
        //end
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
//        super.setTradeUser(map);

        map.put("CUST_ID", map.getString("CUST_ID", reqData.getUca().getCustPerson().getCustId()));
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUser().getUserId()));

        map.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        map.put("CITY_CODE", CSBizBean.getVisit().getCityCode());

        map.put("USER_PASSWD", ""); // 用户密码

        map.put("IN_DATE", getAcceptTime());
        map.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        map.put("OPEN_DATE", getAcceptTime());
        map.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        map.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());

        map.put("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId()); // 发展渠道
        map.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 发展业务区

        map.put("REMOVE_TAG", "0");
    }

}
