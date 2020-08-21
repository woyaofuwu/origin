
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyMasGroupMemberReqData;
import com.asiainfo.veris.crm.order.soa.group.common.query.LimitBlackwhiteBean;
import com.asiainfo.veris.crm.order.soa.group.param.mas.MemParams;

public class DestroyMasGroupMember extends DestroyGroupMember
{

    protected DestroyMasGroupMemberReqData reqData = null;

    public DestroyMasGroupMember()
    {

    }

    /**
     * 作用:TF_F_RELATION_BB表中的关系 ADCMAS产品之前插UU表的，J2EE项目之后则改成插BB表
     * 
     * @author admin
     * @throws Exception
     */
    public void actTradeRelationUU() throws Exception
    {
        String uesrIdA = reqData.getGrpUca().getUser().getUserId();
        String userIdB = reqData.getUca().getUserId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IDataset uuInfos = RelaBBInfoQry.qryBB(uesrIdA, userIdB, relationTypeCode, null);
        if (IDataUtil.isEmpty(uuInfos))
        {
            return;
        }

        IData uuInfo = uuInfos.getData(0);
        reqData.setRoleCodeB(uuInfo.getString("ROLE_CODE_B"));

        uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        uuInfo.put("END_DATE", getAcceptTime()); // 立即结束

        this.addTradeRelationBb(uuInfo);
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author LUOJH
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        regBlackWhite();
        regMemPlatsvc();

        // 移动oa考核版,apn接入点信息
        if ("9127".equals(reqData.getGrpUca().getProductId()))
        {
            this.regApnEntryPoint();
        }
    }

    /**
     * 移动OA考核版apn服务时,登记APN接入点个性信息
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

            if (svcstr.equals("952701") && "1".equals(state))
            {
                IData inparams = new DataMap();
                inparams.put("USER_ID", reqData.getUca().getUserId());
                inparams.put("RSRV_VALUE_CODE", "MOA");
                IDataset dataset = UserOtherInfoQry.getOtherInfoByCodeUserId(reqData.getUca().getUserId(), "MOA");

                if (dataset.size() != 1)
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_1167);
                }

                dataset.getData(0).put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                dataset.getData(0).put("END_DATE", getAcceptTime());

                addTradeOther(dataset);
            }
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyMasGroupMemberReqData();
    }

    /**
     * 作用：注销其它服务,暂为农信通产品用到
     * 
     * @param svcList
     * @throws Exception
     */
    public void infoRegUserSvc(IDataset svcList) throws Exception
    {
        IData svcData = null;
        for (int i = 0; i < svcList.size(); i++)// 处理成员服务平台参数信息
        {
            svcData = svcList.getData(i);
            svcData.put("END_DATE", getAcceptTime());
            svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 新增删除标志

        }

        addTradeSvc(svcList);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (DestroyMasGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    /**
     * 作用:处理成员 黑白名单参数信息
     * 
     * @author liaolc 2014-04-03 16:39
     * @throws Exception
     */
    public void regBlackWhite() throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());
        IDataset bw = UserBlackWhiteInfoQry.getSEL_VALID_PLAT_BLACKWHITE(inparam);
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        String custName = reqData.getUca().getCustPerson().getCustName();

        IData data = null;
        for (int i = 0, iSize = bw.size(); i < iSize; i++)
        {
            data = bw.getData(i);

            String grpUserId = reqData.getGrpUca().getUser().getUserId();
            String mebServiceId = data.getString("SERVICE_ID");
            IData platsvc = MemParams.getUserAPlatSvcParam(grpUserId, mebServiceId);

            String grpplatsvcstate = platsvc.getString("PLAT_SYNC_STATE", "");
            // 判断集团服务是否处于暂停状态
//            if ("P".equals(grpplatsvcstate))
//            {
//                IDataset svc = reqData.cd.getSvc();
//                String svcname = "";
//                for (int si = 0, siSize = svc.size(); si < siSize; si++)
//                {
//                    String svcid = svc.getData(si).getString("ELEMENT_ID", "");
//                    if (svcid.equals(mebServiceId))
//                    {
//                        svcname = svc.getData(si).getString("ELEMENT_NAME", "");
//                        break;
//                    }
//                }
//                if (svcname.equals(""))
//                {
//                    svcname = platsvc.getString("BIZ_NAME", "");
//                }
//
//                CSAppException.apperr(GrpException.CRM_GRP_639, svcname);
//            }

            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("END_DATE", getAcceptTime());
            data.put("REMARK", "");

            // String bizAttr = platsvc.getString("BIZ_ATTR", "");
            // if ("0".equals(bizAttr))// 订购关系
            // {
            // data.put("OPER_STATE", "3");// 退订
            // }
            // else
            // // 黑白名单
            // {
            // data.put("OPER_STATE", "1");// 退出
            // }
            data.put("OPER_STATE", "02"); // 02表示中止，J2EE按接口规范

            String startdate = data.getString("START_DATE", "");
            if (startdate.length() > 19)
            {
                startdate = startdate.substring(0, 19);
            }
            data.put("START_DATE", startdate);

            data.put("EXPECT_TIME", getAcceptTime());

            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

            // ------------ end --------------------------
            data.put("IS_NEED_PF", "1");// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空： 走服务开通发指令,0：不走服务开通不发指令
            data.put("RSRV_TAG3", "0"); // J2EE修改 0 默认值没意义，1 只ADC平台，2 只行业网关，
            // 以后有只发一个平台的产品时，可与 in_mode_code绑定用,服开暂时没取RSRV_TAG3字段;现在用的主表in_mode_code字段,字段值为P 只向网关发送数据 值为G 只向ADC发送数据 其他值
            // adc平台和网关都发送.
            String batchId = reqData.getBatchId();
            String batchOperType = reqData.getBatchDealType();
            if (StringUtils.isNotBlank(batchId) && "GROUPMEMCANCEL".equals(batchOperType))// 集团一点注销过来的 默认不走服务开通
            {
                data.put("IS_NEED_PF", "0");// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空： 走服务开通发指令,0：不走服务开通不发指令
            }
            // 2013-10-16 -------------- liqi添加的需求----start-----------
            String bizInCode = data.getString("BIZ_IN_CODE");
            String userTypeCode = data.getString("USER_TYPE_CODE");
            // G代表从反向网关过来的
            if ("G".equals(CSBizBean.getVisit().getInModeCode()))
            {
                if ("W".equals(userTypeCode))
                // 白名单
                {
                    LimitBlackwhiteBean.insertLimitBlackWhite(serialNumber, bizInCode, custName);
                }
                if ("B".equals(userTypeCode))
                // 黑名单
                {
                    LimitBlackwhiteBean.deleteLimitBlackWhite(serialNumber, bizInCode, custName);
                }
            }
            // 非G代表从网关过来
            else
            {
                if ("B".equals(userTypeCode))
                // 黑名单
                {
                    IDataset limitDataset = LimitBlackwhiteBean.queryLimitBlackWhite(serialNumber, bizInCode);
                    if (IDataUtil.isNotEmpty(limitDataset))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_1027, serialNumber);
                    }
                }
            }
            // -------2013-10-16 liqi添加的需求----------end--------------------------------------------
        }
        addTradeBlackwhite(bw);

    }

    /**
     * 作用:处理成员平台服务参数信息
     * 
     * @author liaolc 2014-03-09 16:54
     * @throws Exception
     */
    public void regMemPlatsvc() throws Exception
    {
        String mebUserId = reqData.getUca().getUser().getUserId();
        String gepUserId = reqData.getGrpUca().getUser().getUserId();
        IDataset memplatsvcset = UserGrpMebPlatSvcInfoQry.getGrpMemPlatSvcByUserIdEcUserId(mebUserId, gepUserId);

        IData data = null;

        for (int i = 0; i < memplatsvcset.size(); i++)// 处理成员服务平台参数信息
        {
            data = memplatsvcset.getData(i);
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            data.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());
            data.put("EC_SERIAL_NUMBER", reqData.getGrpUca().getUser().getSerialNumber());
            data.put("REMARK", "");
            data.put("END_DATE", getAcceptTime());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 新增删除标志

        }

        addTradeGrpMebPlatsvc(memplatsvcset);
    }

}
