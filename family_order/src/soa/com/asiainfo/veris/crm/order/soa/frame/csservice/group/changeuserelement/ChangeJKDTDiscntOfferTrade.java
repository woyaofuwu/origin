
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepOfferfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class ChangeJKDTDiscntOfferTrade extends ChangeUserElement {
	private final static Logger logger = Logger.getLogger(ChangeJKDTDiscntOfferTrade.class);
    protected ChangeBBossUserReqData reqData = null;

    // XXX 处理基类取不到业务类型
    @Override
    public String setTradeTypeCode() throws Exception
    {
        // 1- 继承基类处理
        //super.setTradeTypeCode();

       return "2313";
    }
    /**
     * @description 生成其它台帐数据（生成台帐后）
     * @author xunyl
     * @date 2013-08-26
     */
    @Override
    public void actTradeSub() throws Exception {
        // 1- 继承基类处理
        super.actTradeSub();

        String merchOperType = reqData.getGOOD_INFO().getString("MERCH_OPER_CODE");// 商品操作类型
        // 2- 修改BBOSS侧的商品表数据(TF_F_USER_GRP_MERCH)
        this.infoRegDataEntireMerch(merchOperType);

        // 3- 登记other表
        infoRegDataOther(merchOperType);
    }

    /*
     * @description 根据资费编码到trade_discnt表查找inst_id,用于与资费参数进行关联
     * @author xunyl
     * @date 2013-09-03
     */
    protected String getInstIdByDiscntCode(String discntCode) throws Exception {
        // 1- 定义返回对象
        String instId = "";

        // 2- 获取资费对象
        IDataset merchPDsts = bizData.getTradeDiscnt();

        // 2- 循环产品资费信息，获取相应的inst_id
        for (int i = 0; i < merchPDsts.size(); i++) {
            IData merchPDst = merchPDsts.getData(i);
            String elementId = merchPDst.getString("DISCNT_CODE");
            if (discntCode.equals(elementId)) {
                instId = merchPDst.getString("INST_ID");
                break;
            }
        }

        // 3- 返回数据
        return instId;
    }

    /*
     * @description 处理商品级资费
     * @author xunyl
     * @date 2013-07-23
     */
    protected IDataset getMerchDsn() throws Exception {
        // 1- 定义商品级资费对象
        IDataset merchDiscntInfoList = new DatasetList();

        // 2- RD中获取商品级资费
        IDataset merchDiscnts = reqData.cd.getDiscnt();
        if (IDataUtil.isEmpty(merchDiscnts)) {
            return merchDiscnts;
        }

        // 3-拼装商品资费
        for (int i = 0; i < merchDiscnts.size(); i++) {
            // 3-1 获取资费状态
            IData merchDst = merchDiscnts.getData(i);
            String modifyTag = merchDst.getString("MODIFY_TAG");

            // 3-2 根据资费状态拼装资费信息
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)) {
                // 3-2-1 添加商品操作类型
                String merchSpecCode = GrpCommonBean.productJKDTToMerch(reqData.getUca().getProductId(), 0);
                if (StringUtils.isBlank(merchSpecCode)) {
                    CSAppException.apperr(GrpException.CRM_GRP_37, reqData.getUca().getProductId());
                }
                merchDst.put("MERCH_SPEC_CODE", merchSpecCode);

                // 3-2-2 添加套餐ID
                String packageId = merchDst.getString("PACKAGE_ID", "");
                String planId = GrpCommonBean.productJKDTToMerch(packageId, 1);
                if (StringUtils.isBlank(planId)) {
                    merchDiscnts.remove(i);
                    i--;
                    continue;
                }
                merchDst.put("RSRV_STR1", planId);

                // 3-2-3 添加套餐名称
                String packageName = PkgInfoQry.getPackageNameByPackageId(packageId);
                merchDst.put("RSRV_STR2", packageName);

                // 3-2-4 添加商品资费编号
                String discntCode = merchDst.getString("DISCNT_CODE");
                String merchDiscntCode = GrpCommonBean.productJKDTToMerch(discntCode, 1);// BBOSS产品优惠编码
                merchDst.put("MERCH_DISCNT_CODE", merchDiscntCode);

                // 3-2-5 添加开始时间
                merchDst.put("START_DATE", "".equals(merchDst.getString("START_DATE", "")) ? getAcceptTime() : merchDst.getString("START_DATE"));

                // 3-2-6 添加结束时间
                merchDst.put("END_DATE", "".equals(merchDst.getString("END_DATE", "")) ? SysDateMgr.getTheLastTime() : merchDst.getString("END_DATE"));

                // 3-2-7 添加实例编号
                merchDst.put("INST_ID", SeqMgr.getInstId());

                // 3-2-8 添加关联TRADE_DISCNT表的实例编号
                merchDst.put("RELA_INST_ID", getInstIdByDiscntCode(discntCode));

                // 3-2-8 添加商品资费操作代码，服开拼报文用
                merchDst.put("RSRV_STR3", GroupBaseConst.PARMA_STATUS.PARAM_ADD.getValue());

                // 3-2-9 添加商品至商品资费集
                merchDiscntInfoList.add(merchDst);
            } else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)) {
                // 3-2-1 查找资费信息
                String merchUserId = reqData.getUca().getUser().getUserId();
                String elementId = merchDst.getString("ELEMENT_ID");
                String merchDiscntCode = GrpCommonBean.productJKDTToMerch(elementId, 1);// BBOSS产品优惠编码
                IDataset reteInfos = UserGrpMerchDiscntInfoQry.qryMerchDiscntByUserIdMerchScMerchDc(merchUserId, null, merchDiscntCode, null);
                if (IDataUtil.isEmpty(reteInfos)) {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_214);
                }
                IData rateInfo = reteInfos.getData(0);

                // 3-2-2 修改资费状态
                rateInfo.put("MODIFY_TAG", modifyTag);

                // 3-2-3 修改结束时间(先取资费本省的结束时间，没有则默认为立即失效)
                rateInfo.put("END_DATE", "".equals(merchDst.getString("END_DATE", "")) ? getAcceptTime() : merchDst.getString("END_DATE"));

                // 3-2-4 添加商品资费操作代码，服开拼报文用
                rateInfo.put("RSRV_STR3", GroupBaseConst.PARMA_STATUS.PARAM_DEL.getValue());

                // 3-2-5 添加商品至商品资费集
                merchDiscntInfoList.add(rateInfo);
            }

        }

        // 4- 返回商品级资费对象
        return merchDiscntInfoList;
    }

    @Override
    protected BaseReqData getReqData() throws Exception {
        return new ChangeBBossUserReqData();
    }

    /*
     * @description 根据接口规范的商品操作编号获取服务开通侧配置的商品操作编号(供服务开通用)
     * @author xunyl
     * @date 2013-08-26
     */
    protected String getServMerchOpType(String merchOpType) throws Exception {
        // 1- 定义返回值
        String servMerchOpType = "";

        // 2-获取对应的商品编号
        if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue())) {
            servMerchOpType = "04";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue())) {
            servMerchOpType = "05";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue())) {
            servMerchOpType = "15";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_GROUP.getValue())) {
            servMerchOpType = "17";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue())) {
            servMerchOpType = "19";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue())) {
            servMerchOpType = "10";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue())) {
            servMerchOpType = "11";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_MEB.getValue())) {
            servMerchOpType = "16";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PROV.getValue())) {
            servMerchOpType = "13";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue())) {
            servMerchOpType = "22";
        } else if (merchOpType.equals(GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue())) {
            servMerchOpType = "23";
        }

        // 3- 返回服务开通侧商品操作编号
        return servMerchOpType;
    }

    /*
     * 修改商品用户表数据
     * @author xunl
     * @date 2013-05-06
     */
    public IData getTradeUserExtendData() throws Exception {

        IData userExtenData = super.getTradeUserExtendData();

        String merchOperType = reqData.getGOOD_INFO().getString("MERCH_OPER_CODE");// 商品操作类型

        // 修改商品用户表数据(商品操作类型为修改商品时不改该表)

        if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType)
                || GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX.getValue().equals(merchOperType) || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue().equals(merchOperType)) {

            // 用户状态
            String userStatus = reqData.getUca().getUser().getRsrvStr5();
            boolean isCredit = reqData.getGOOD_INFO().getBoolean("IS_CREDIT");// 信控暂停标志
            if (GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(
                    merchOperType)
                    || (GroupBaseConst.MERCH_STATUS.MERCH_PASTE_MEBFLUX
                    .getValue().equals(merchOperType) && (isCredit || "6"
                    .equals(CSBizBean.getVisit().getInModeCode()))))// 商品操作类型为暂停
            {
                userStatus = "N";
                userExtenData.put("USER_STATE_CODESET", "3");
            } else if (GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperType))// 商品操作类型为预取消
            {
                userStatus = "D";
                userExtenData.put("USER_STATE_CODESET", "5");
            } else if (GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperType) || (GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperType)
                    || GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE_MEBFLUX.getValue().equals(merchOperType) && isCredit)) {// 商品操作类型为冷冻期取消恢复商品订购
                userStatus = "A";
                userExtenData.put("USER_STATE_CODESET", "0");
            }

            userExtenData.put("RSRV_STR1", reqData.getOrderId());// 商品订单号
            userExtenData.put("RSRV_STR5", userStatus);// 用户状态细分
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        }

        return userExtenData;
    }

    /**
     * @description 修改BBOSS侧的商品表数据(TF_F_USER_GRP_MERCH)
     * @author xunyl
     * @Date 2013-05-06
     */
    public void infoRegDataEntireMerch(String merchOperType) throws Exception {
        String userId = reqData.getUca().getUserId();// 商品用户编号
        String payMode = reqData.getGOOD_INFO().getString("PAY_MODE");// 套餐生效规则
        String busNeedDegree = reqData.getGOOD_INFO().getString("BUS_NEED_DEGREE");// 业务保障等级

        // 根据用户编号查询BBOSS侧的商品表
        IDataset grpMerchInfo = UserEcrecepOfferfoQry.qryJKDTMerchInfoByUserIdMerchSpecStatus(userId, null, null);
        if (grpMerchInfo == null || grpMerchInfo.size() == 0) {
            CSAppException.apperr(CrmUserException.CRM_USER_540);
        }
        IData merchInfo = grpMerchInfo.getData(0);
        merchInfo.put("PAY_MODE", payMode);
        merchInfo.put("RSRV_TAG1", payMode);
        merchInfo.put("RSRV_STR5", busNeedDegree);// 业务保障等级
        merchInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());// 修改商品
        merchInfo.put("RSRV_STR1", merchOperType);// 商品操作类型
        merchInfo.put("MERCH_ORDER_ID", SeqMgr.getBBossMerchIdForGrp()); // 商品订单号
        if ("6".equals(CSBizBean.getVisit().getInModeCode())) {
            merchInfo.put("MERCH_OFFER_ID", reqData.getGOOD_INFO().getString("MERCH_OFFER_ID"));
            merchInfo.put("MERCH_ORDER_ID", reqData.getGOOD_INFO().getString("MERCH_ORDER_ID")); // 商品订单号
        }
        // 登记BBOSS侧商品表信息
        super.addTradeEcrecepOffer(IDataUtil.idToIds(merchInfo));

    
      //登记集客受理大厅流程信息
        IData epData = GrpCommonBean.actEcrecepProcedure(reqData.getUca().getUser().getUserId(), SeqMgr.getInstId(), reqData.getUca().getUser().getSerialNumber(), merchInfo.getString("MERCH_ORDER_ID"), merchInfo.getString("MERCH_OFFER_ID", ""), merchOperType, "0", "0");
        this.addTradeEcrecepProcedure(epData);
        
        // 处理商品资费  daidl
        IDataset merchDiscnts = new DatasetList();
        if (merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_DISCNT.getValue()))// 修改商品资费
        {
            merchDiscnts = this.getMerchDsn();
            if (merchDiscnts == null || merchDiscnts.size() == 0)
            {
                return;
            }
        }
        this.addTradeGrpMerchDiscnt(merchDiscnts);
    }

    /**
     * @throws Exception
     * @description 插Other表
     * @author chenyi
     */
    public void infoRegDataOther(String merchOperType) throws Exception {
        IDataset dataset = new DatasetList();

        // 1- 附件信息(合同附件和普通附件)入表
        IDataset attInfosDS = reqData.getGOOD_INFO().getDataset("ATT_INFOS");
        if (attInfosDS != null && attInfosDS.size() > 0) {
            for (int i = 0; i < attInfosDS.size(); i++) {
                IData attInfos = new DataMap();
                DataMap dm = (DataMap) attInfosDS.get(i);

                attInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                attInfos.put("RSRV_VALUE_CODE", "ATT_INFOS");
                attInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                attInfos.put("RSRV_STR2", dm.getString("ATT_TYPE_CODE"));
                IData inparam = new DataMap();
                inparam.put("CUST_ID", reqData.getUca().getCustId());
                inparam.put("PRODUCT_ID", reqData.getUca().getProductId());
                inparam.put("ATT_TYPE_CODE", dm.getString("ATT_TYPE_CODE"));
                IData cliAttInfo = CSAppCall.callCCHT("ITF_CRM_ContractList", inparam, false).getData(0);
                attInfos.put("RSRV_STR3", cliAttInfo.getString("CONTRACT_BBOSS_CODE"));
                attInfos.put("RSRV_STR4", cliAttInfo.getString("CONTRACT_NAME"));
                String attName = GrpCommonBean.checkFileState(dm.getString("ATT_NAME"));
                attInfos.put("RSRV_STR5", attName);
                attInfos.put("RSRV_STR6", cliAttInfo.getString("CONTRACT_START_DATE"));
                attInfos.put("RSRV_STR7", cliAttInfo.getString("CONTRACT_END_DATE"));
                attInfos.put("RSRV_STR8", cliAttInfo.getString("CONTRACT_IS_AUTO_RENEW"));
                attInfos.put("RSRV_STR9", cliAttInfo.getString("RENEW_END_DATE"));
                attInfos.put("RSRV_STR10", cliAttInfo.getString("CONT_FEE"));
                attInfos.put("RSRV_STR11", cliAttInfo.getString("PERFER_PALN"));
                attInfos.put("RSRV_STR12", cliAttInfo.getString("CONTRACT_AUTO_RENEW_CYCLE", "按月"));
                attInfos.put("RSRV_STR13", cliAttInfo.getString("CONTRACT_IS_RENEW"));
                attInfos.put("START_DATE", getAcceptTime());
                attInfos.put("END_DATE", SysDateMgr.getTheLastTime());
                attInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                attInfos.put("INST_ID", SeqMgr.getInstId());
                dataset.add(attInfos);
            }
        }

        // 2- 审批信息入表
        IDataset auditorInfosDS = reqData.getGOOD_INFO().getDataset("AUDITOR_INFOS");
        if (auditorInfosDS != null && auditorInfosDS.size() > 0) {
            for (int i = 0; i < auditorInfosDS.size(); i++) {
                IData auditorInfos = new DataMap();
                DataMap dm2 = (DataMap) auditorInfosDS.get(i);
                auditorInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                auditorInfos.put("RSRV_VALUE_CODE", "AUDITOR_INFOS");
                auditorInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                auditorInfos.put("RSRV_STR2", dm2.getString("AUDITOR"));
                auditorInfos.put("RSRV_STR3", dm2.getString("AUDITOR_TIME"));
                auditorInfos.put("RSRV_STR4", dm2.getString("AUDITOR_DESC"));
                auditorInfos.put("START_DATE", getAcceptTime());
                auditorInfos.put("END_DATE", SysDateMgr.getTheLastTime());
                auditorInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                auditorInfos.put("INST_ID", SeqMgr.getInstId());
                dataset.add(auditorInfos);
            }
        }

        // 3- 联系人信息入表
        IDataset contactorInfosDS = reqData.getGOOD_INFO().getDataset("CONTACTOR_INFOS");
        if (contactorInfosDS != null && contactorInfosDS.size() > 0) {
            for (int i = 0; i < contactorInfosDS.size(); i++) {
                IData contactorInfos = new DataMap();
                DataMap dm3 = (DataMap) contactorInfosDS.get(i);
                contactorInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                contactorInfos.put("RSRV_VALUE_CODE", "CONTACTOR_INFOS");
                contactorInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                contactorInfos.put("RSRV_STR2", dm3.getString("CONTACTOR_TYPE_CODE"));
                contactorInfos.put("RSRV_STR3", dm3.getString("CONTACTOR_NAME"));
                contactorInfos.put("RSRV_STR4", dm3.getString("CONTACTOR_PHONE"));
                contactorInfos.put("RSRV_STR5", dm3.getString("STAFF_NUMBER"));
                contactorInfos.put("START_DATE", getAcceptTime());
                contactorInfos.put("END_DATE", SysDateMgr.getTheLastTime());
                contactorInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                contactorInfos.put("INST_ID", SeqMgr.getInstId());
                dataset.add(contactorInfos);
            }
        }

        // 4- BBOSS侧服开信息入表(服务开通用)
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "7810");// 服务开通侧集团service_id对应为7810
        serviceInfo.put("OPER_CODE", getServMerchOpType(merchOperType));
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        serviceInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());
        dataset.add(serviceInfo);
        
        //REQ201911040021_(集团全网)关于更新集客大厅与省公司接口规范的通知--启用成员叠加包、增加订购渠道信息  add by huangzl3
        
          // 5- 渠道信息入表
          logger.info("MERCH_INFO(hzl)=="+ reqData.getGOOD_INFO().toString());
          IData channelInfos = reqData.getGOOD_INFO().getData("CHANNEL_INFO");
          logger.info("channelInfos(hzl)=="+ channelInfos);
          if (channelInfos != null )
          {       
              if(!"".equals(channelInfos.getString("MAINTEANCE_CHANNELID",""))) {
              	IDataset channelInfolist = UserOtherInfoQry.getOtherInfoByCodeUserId(reqData.getUca().getUser().getUserId(),"MAINTEANCE_CHANNELID");            	            	
              	if(channelInfolist.size()>0) {
              		IData channelInfo = channelInfolist.getData(0);
              		channelInfo.put("RSRV_VALUE", channelInfos.getString("MAINTEANCE_CHANNELID", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                  	channelInfo.put("UPDATE_TIME", getAcceptTime());
                      dataset.add(channelInfo);
              	}else {
              		IData channelInfo = new DataMap();
                  	channelInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                  	channelInfo.put("RSRV_VALUE_CODE", "MAINTEANCE_CHANNELID");
                  	channelInfo.put("RSRV_VALUE", channelInfos.getString("MAINTEANCE_CHANNELID", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                  	channelInfo.put("INST_ID", SeqMgr.getInstId());
                      dataset.add(channelInfo);
              	}
              }
              if(!"".equals(channelInfos.getString("CHANNEL_TYPE",""))) {
              	IDataset channelInfolist = UserOtherInfoQry.getOtherInfoByCodeUserId(reqData.getUca().getUser().getUserId(),"CHANNEL_TYPE");            	            	
              	if(channelInfolist.size()>0) {
              		IData channelInfo = channelInfolist.getData(0);
              		channelInfo.put("RSRV_VALUE", channelInfos.getString("CHANNEL_TYPE", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                  	channelInfo.put("UPDATE_TIME", getAcceptTime());
                      dataset.add(channelInfo);
              	}else {
              		IData channelInfo = new DataMap();
                  	channelInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                  	channelInfo.put("RSRV_VALUE_CODE", "CHANNEL_TYPE");
                  	channelInfo.put("RSRV_VALUE", channelInfos.getString("CHANNEL_TYPE", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                  	channelInfo.put("INST_ID", SeqMgr.getInstId());
                      dataset.add(channelInfo);
              	}
              }
              if(!"".equals(channelInfos.getString("PROJECT_NAME",""))) {
              	IDataset channelInfolist = UserOtherInfoQry.getOtherInfoByCodeUserId(reqData.getUca().getUser().getUserId(),"PROJECT_NAME");            	            	
              	if(channelInfolist.size()>0) {
              		IData channelInfo = channelInfolist.getData(0);
              		channelInfo.put("RSRV_VALUE", channelInfos.getString("PROJECT_NAME", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                  	channelInfo.put("UPDATE_TIME", getAcceptTime());
                      dataset.add(channelInfo);
              	}else {
              		IData channelInfo = new DataMap();
                  	channelInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                  	channelInfo.put("RSRV_VALUE_CODE", "PROJECT_NAME");
                  	channelInfo.put("RSRV_VALUE", channelInfos.getString("PROJECT_NAME", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                  	channelInfo.put("INST_ID", SeqMgr.getInstId());
                      dataset.add(channelInfo);
              	}
              }
              if(!"".equals(channelInfos.getString("PROJECT_ID",""))) {
              	IDataset channelInfolist = UserOtherInfoQry.getOtherInfoByCodeUserId(reqData.getUca().getUser().getUserId(),"PROJECT_ID");            	            	
              	if(channelInfolist.size()>0) {
              		IData channelInfo = channelInfolist.getData(0);
              		channelInfo.put("RSRV_VALUE", channelInfos.getString("PROJECT_ID", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                  	channelInfo.put("UPDATE_TIME", getAcceptTime());
                      dataset.add(channelInfo);
              	}else {
              		IData channelInfo = new DataMap();
                  	channelInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                  	channelInfo.put("RSRV_VALUE_CODE", "PROJECT_ID");
                  	channelInfo.put("RSRV_VALUE", channelInfos.getString("PROJECT_ID", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                  	channelInfo.put("INST_ID", SeqMgr.getInstId());
                      dataset.add(channelInfo);
              	}
              }
              if(!"".equals(channelInfos.getString("SETTLEMENT_RATIO",""))) {
              	IDataset channelInfolist = UserOtherInfoQry.getOtherInfoByCodeUserId(reqData.getUca().getUser().getUserId(),"SETTLEMENT_RATIO");            	            	
              	if(channelInfolist.size()>0) {
              		IData channelInfo = channelInfolist.getData(0);
              		channelInfo.put("RSRV_VALUE", channelInfos.getString("SETTLEMENT_RATIO", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                  	channelInfo.put("UPDATE_TIME", getAcceptTime());
                      dataset.add(channelInfo);
              	}else {
              		IData channelInfo = new DataMap();
                  	channelInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                  	channelInfo.put("RSRV_VALUE_CODE", "SETTLEMENT_RATIO");
                  	channelInfo.put("RSRV_VALUE", channelInfos.getString("SETTLEMENT_RATIO", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                  	channelInfo.put("INST_ID", SeqMgr.getInstId());
                      dataset.add(channelInfo);
              	}
              }
              if(!"".equals(channelInfos.getString("CHANNEL_ID",""))) {
              	IDataset channelInfolist = UserOtherInfoQry.getOtherInfoByCodeUserId(reqData.getUca().getUser().getUserId(),"CHANNEL_ID");            	            	
              	if(channelInfolist.size()>0) {
              		IData channelInfo = channelInfolist.getData(0);
              		channelInfo.put("RSRV_VALUE", channelInfos.getString("CHANNEL_ID", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                  	channelInfo.put("UPDATE_TIME", getAcceptTime());
                      dataset.add(channelInfo);
              	}else {
              		IData channelInfo = new DataMap();
                  	channelInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
                  	channelInfo.put("RSRV_VALUE_CODE", "CHANNEL_ID");
                  	channelInfo.put("RSRV_VALUE", channelInfos.getString("CHANNEL_ID", ""));
                  	channelInfo.put("START_DATE", getAcceptTime());
                  	channelInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                  	channelInfo.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.Add.getValue());
                  	channelInfo.put("INST_ID", SeqMgr.getInstId());
                      dataset.add(channelInfo);
              	}
              } 
          }
          //REQ201911040021_(集团全网)关于更新集客大厅与省公司接口规范的通知--启用成员叠加包、增加订购渠道信息  end  

        this.addTradeOther(dataset);
    }

    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-05-06
     */
    @Override
    protected void initReqData() throws Exception {
        super.initReqData();
        reqData = (ChangeBBossUserReqData) getBaseReqData();
    }

    /*
     * @description 将前台传递过来的BBOSS数据放入RD中
     * @author xunyl
     * @date 2013-05-06
     */
    public void makBBossReqData(IData map) throws Exception {
        // 将BBOSS侧的商品信息保存起来
        reqData.setGOOD_INFO(map.getData("GOOD_INFO"));

        // 设置订单编号
        reqData.setOrderId(map.getString("ORDER_ID"));
    }

    /*
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-25
     */
    @Override
    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);
        makBBossReqData(map);
    }

    /**
     * bboss资费修改是特殊处理 新增一条删除一条，基类的这个方法处理不了
     */
    protected void modifyDiscnt() throws Exception {

    }

    /*
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        IData data = bizData.getTrade();
        String merchOperType = reqData.getGOOD_INFO().getString("MERCH_OPER_CODE");// 商品操作类型
        if ("6".equals(CSBizBean.getVisit().getInModeCode()) || merchOperType.equals(GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_LOCALDISCNT.getValue())) {
            // 渠道类型为IBOSS
            data.put("OLCOM_TAG", "0");
        } else {
            data.put("OLCOM_TAG", "1");
        }
        //集客大厅受理逻辑  daidl
        String specProductId = GrpCommonBean.productJKDTToMerch(reqData.getUca().getProductId(), 0);  
        IDataset dataset = CommparaInfoQry.getCommparaInfos("CSM", "9079", specProductId);
        if (IDataUtil.isNotEmpty(dataset)) {
        	//读配置判断是否发指令
        	data.put("OLCOM_TAG", dataset.getData(0).getString("PARA_CODE1"));
            //读配置判断是否等待服开回单
            data.put("PF_WAIT", dataset.getData(0).getString("PARA_CODE2"));
        }
        
    }
}
