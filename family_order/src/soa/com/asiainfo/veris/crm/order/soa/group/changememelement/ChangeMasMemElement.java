
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElementReqData;
import com.asiainfo.veris.crm.order.soa.group.param.mas.MemParams;

public class ChangeMasMemElement extends ChangeMemElement
{

    protected ChangeMemElementReqData reqData = null;

    public ChangeMasMemElement()
    {

    }

    @Override
    protected void actTradeRelationUU() throws Exception
    {
        String role_code_b = reqData.getMemRoleB();
        if (StringUtils.isNotEmpty(role_code_b))
        {
            IDataset relaBBList = RelaBBInfoQry.getBBInfoByUserIdAB(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId()); // 变更的时候查UU关系必须有一条记录，这里不校验为空的情况，然这种情况报错，修数据

            IData relaBB = relaBBList.getData(0);

            if (!role_code_b.equals(relaBB.getString("ROLE_CODE_B")))
            {
                relaBB.put("ROLE_CODE_B", role_code_b);
                relaBB.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                super.addTradeRelationBb(relaBB);

                reqData.setIsChange(true);
            }
        }
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        this.regBlackWhiteAndMemPlatsvc();

        // 移动oa考核版,若成员开通了apn服务,那么需要维护apn接入点参数
        if ("9127".equals(reqData.getGrpUca().getProductId()))
        {
            this.regApnEntryPoint();// 维护apn接入点信息
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
            else if (svcstr.equals("952701") && "1".equals(state))
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
        return new ChangeMemElementReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeMemElementReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    /**
     * 作用：处理除基本资料表外的其它资料
     * 
     * @author liaolc 2014-04-02
     * @throws Exception
     */
    public void regBlackWhiteAndMemPlatsvc() throws Exception
    {
        IDataset specialServiceset = reqData.cd.getSpecialSvcParam();
        String grpUserId = reqData.getGrpUca().getUser().getUserId();
        //String mebUserId = reqData.getUca().getUser().getUserId();

        for (int i = 0; i < specialServiceset.size(); i++)
        {
            IDataset specialServicDataset = (IDataset) specialServiceset.get(i);
            IData specialServic = specialServicDataset.getData(1);
            IData platsvcdata = specialServic.getData("PLATSVC");// platsvc表个性参数
            String state = specialServic.getString("MODIFY_TAG", "");
            String startdate = specialServic.getString("START_DATE", "");
            String enddate = specialServic.getString("END_DATE", "");
            String canleflag = specialServic.getString("CANCLE_FLAG", "");
            platsvcdata = IDataUtil.replaceIDataKeyDelPrefix(platsvcdata, "pam_");
            String instId = specialServic.getString("INST_ID");// 服务对应的实例标识
            String operState = platsvcdata.getString("OPER_STATE", "");

            String mebServiceId = platsvcdata.getString("SERVICE_ID", "");

            // 查询集团用户grp_platsvc表信息
            IData platsvc = MemParams.getUserAPlatSvcParam(grpUserId, mebServiceId);

            if (platsvc.isEmpty())
            {
                CSAppException.apperr(GrpException.CRM_GRP_25);
            }

            platsvc.put("SERVICE_ID", mebServiceId);

            if (IDataUtil.isNotEmpty(platsvc))
            {
                setRegMemPlatsvc(platsvc, startdate, enddate, canleflag, state, operState, instId);
            }

            if (IDataUtil.isNotEmpty(platsvcdata))
            {
                setRegBlackWhite(platsvcdata, platsvc, startdate, enddate, canleflag, state, operState, instId);
            }
            // 暂停、恢复时处理服务状态表
            // setRegChangeSvcState(operState, mebServiceId);
        }
    }

    /**
     * 作用：处理黑白名单表
     * 
     * @author liaolc 2014-04-03
     * @param platsvcdata
     * @param startdate
     * @param enddate
     * @param canleflag
     * @param state
     * @throws Exception
     */
    public void setRegBlackWhite(IData platsvcdata, IData platsvc, String startdate, String enddate, String canleflag, String state, String operState, String instId) throws Exception
    {

        IData data = new DataMap();
        String bizattr = platsvc.getString("BIZ_ATTR", "");

        if (TRADE_MODIFY_TAG.Add.getValue().equals(state))
        {
            if (platsvc.isEmpty())
            {
                CSAppException.apperr(PlatException.CRM_PLAT_20);
            }
            data.put("INST_ID", instId);
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("SERVICE_ID", platsvcdata.getString("SERVICE_ID"));
            String usertypecode = "";

            if ("0".equals(bizattr))
            {// 订购关系
                usertypecode = "S";
            }
            else if ("1".equals(bizattr))
            {// 白名单
                usertypecode = "W";
            }
            else if ("2".equals(bizattr))
            {// 黑名单
                usertypecode = "B";
            }
            else if ("3".equals(bizattr))
            { // 限制次数的白名单
                usertypecode = "XW";
            }
            else if ("4".equals(bizattr))
            { // 点播业务，不能在BOSS侧订购
                CSAppException.apperr(CrmCommException.CRM_COMM_103);
            }

            data.put("USER_TYPE_CODE", usertypecode);
            data.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());

            data.put("SERV_CODE", platsvc.getString("BIZ_IN_CODE"));
            data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            data.put("GROUP_ID", platsvc.getString("GROUP_ID"));
            data.put("BIZ_CODE", platsvc.getString("BIZ_CODE"));
            data.put("BIZ_NAME", platsvc.getString("BIZ_NAME"));
            data.put("BIZ_DESC", platsvc.getString("BIZ_DESC"));
            data.put("EC_SERIAL_NUMBER", reqData.getGrpUca().getSerialNumber());

            data.put("REMARK", "");
            data.put("RSRV_NUM1", "0");
            data.put("RSRV_NUM2", "0");
            data.put("RSRV_NUM3", "0");
            data.put("RSRV_NUM4", "0");
            data.put("RSRV_NUM5", "0");
            data.put("RSRV_STR1", "");
            data.put("RSRV_STR2", "");
            data.put("RSRV_STR3", "");
            data.put("RSRV_STR4", "");
            data.put("RSRV_STR5", "");
            data.put("RSRV_DATE1", "");
            data.put("RSRV_DATE2", "");
            data.put("RSRV_DATE3", "");

            data.put("RSRV_TAG2", platsvcdata.getString("RSRV_TAG2", "0"));// 0-实时接口 1-文件接口 td_b_attr_biz 默认取0
            // 放到blackwhite表的rsrv_tag2字段
            // data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3","0"));//标识是否走服务开通 0 正常走服务开通模式 1 ADC平台 2 行业网关
            data.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE"));
            data.put("BIZ_IN_CODE_A", platsvc.getString("EC_BASE_IN_CODE_A"));
            data.put("SERVICE_ID", platsvcdata.getString("SERVICE_ID", ""));
            data.put("BILLING_TYPE", platsvc.getString("BILLING_TYPE"));

            data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// 1或者是空：走服务开通发指令,0：不走服务开通不发指令
            data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3", "0"));// 0 默认值没意义，1 只ADC平台，2 只行业网关，
            // 以后有只发一个平台的产品时，可与 in_mode_code绑定用,服开暂时没取RSRV_TAG3字段;现在用的主表in_mode_code字段,字段值为P 只向网关发送数据 值为G 只向ADC发送数据
            // 其他值 adc平台和网关都发送.

            data.put("OPER_STATE", "01");// 3.0规范 新增

            data.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            data.put("OPR_EFF_TIME", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            data.put("EXPECT_TIME", platsvcdata.getString("EXPECT_TIME", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime()));
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 新增记录
            data.put("PLAT_SYNC_STATE", "1");
        }
        else if (!TRADE_MODIFY_TAG.Add.getValue().equals(state))
        {
            String user_id = reqData.getUca().getUserId();
            String group_id = reqData.getGrpUca().getCustGroup().getGroupId();
            String service_id = platsvcdata.getString("SERVICE_ID");

            IDataset blackWhiteoldset = UserBlackWhiteInfoQry.getBlackWhitedata(user_id, group_id, service_id);
            if (IDataUtil.isNotEmpty(blackWhiteoldset))
            {
                data.putAll(blackWhiteoldset.getData(0));
            }
            // 删除服务
            if (TRADE_MODIFY_TAG.DEL.getValue().equals(state))
            {
                data.put("OPER_STATE", "02");// 3.0规范 02终止 2013-01-19 J2EE项目修改，与接口规范保持一致。
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                data.put("END_DATE", enddate);
                data.put("EXPECT_TIME", enddate);
            }
            else
            {
                data.put("OPER_STATE", operState);
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                data.put("PLAT_SYNC_STATE", "04".equals(operState) ? "P" : "1");
            }

            data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// J2EE新增IS_NEED_PF字段表示是否走服务开通
            data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3", "0"));
        }
        super.addTradeBlackwhite(data);
    }

    /**
     * 作用：处理成员服务平台参数表
     * 
     * @author liaolc
     * @param platsvc
     * @param startdate
     * @param enddate
     * @param canleflag
     * @param state
     * @throws Exception
     */
    public void setRegMemPlatsvc(IData platsvc, String startdate, String enddate, String canleflag, String state, String operState, String instId) throws Exception
    {
        IData memplatdata = new DataMap();
        String mebUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        String serviceId = platsvc.getString("SERVICE_ID");

        memplatdata.put("USER_ID", mebUserId);
        memplatdata.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        memplatdata.put("EC_USER_ID", grpUserId);
        memplatdata.put("EC_SERIAL_NUMBER", reqData.getGrpUca().getSerialNumber());

        memplatdata.put("SERV_CODE", platsvc.getString("SERV_CODE", ""));
        memplatdata.put("BIZ_CODE", platsvc.getString("BIZ_CODE", ""));
        memplatdata.put("BIZ_NAME", platsvc.getString("BIZ_NAME", ""));
        memplatdata.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE"));
        memplatdata.put("SERVICE_ID", serviceId);// 成员服务ID
        memplatdata.put("REMARK", "");
        memplatdata.put("INST_ID", instId);
        // 这几个应该从服务参数传入NOW，非企信通的，应该从产品参数取得，但现在的产品参数没有从用户paltserv表取

        if (TRADE_MODIFY_TAG.Add.getValue().equals(state))// 新增服务
        {
            memplatdata.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            memplatdata.put("END_DATE", SysDateMgr.getTheLastTime());
            memplatdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 新增记录
        }
        else if (TRADE_MODIFY_TAG.MODI.getValue().equals(state))// 变更服务
        {
            if ("true".equals(canleflag))// 处理点开弹出窗口 导致服务操作状态变为MODI
            // 但任何内容没改的情况,此时实际没做修改 所以跳到下次循环
            {
                // continue;
            }
            else
            {
                memplatdata.put("START_DATE", startdate);
                memplatdata.put("END_DATE", enddate);
                memplatdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            }
        }
        else if (TRADE_MODIFY_TAG.DEL.getValue().equals(state))
        {
            memplatdata.put("START_DATE", startdate);
            memplatdata.put("END_DATE", enddate);
            memplatdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }

        // 非新增业务INST_ID取资料表的数据
        if (!TRADE_MODIFY_TAG.Add.getValue().equals(state))
        {
            // 取GRP_MEB_PLATSVC平台服务表已经存在的参数
            IDataset mebPlatsvcset = UserGrpMebPlatSvcInfoQry.getMemPlatSvc(mebUserId, grpUserId, serviceId);
            memplatdata.put("INST_ID", mebPlatsvcset.getData(0).getString("INST_ID"));
        }

        addTradeGrpMebPlatsvc(memplatdata);
    }
}
