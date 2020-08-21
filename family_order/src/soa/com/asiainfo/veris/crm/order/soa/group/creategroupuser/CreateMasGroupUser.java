
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateMasGroupUserReqData;

public class CreateMasGroupUser extends CreateGroupUser
{
    protected CreateMasGroupUserReqData reqData = null;

    // private static Logger log = Logger.getLogger(CreateMasGroupUser.class);

    public CreateMasGroupUser() throws Exception
    {
        super();
    }

    /**
     * 作用：生成其它台帐数据（生成台帐后）
     *
     * @author liaolc 2014-03-01
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        this.infoRegSpecialSvcParams();
        // 处理信誉度台帐子表(信用度重算)
        this.actTradeCredit();
    }

    /**
     * 处理信誉度台帐子表(信用度重算)
     *
     * @author
     * @throws Exception
     */
    protected void actTradeCredit() throws Exception
    {
        IData map = new DataMap();
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("MODIFY_TAG", map.getString("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()));
        super.addTradeCredit(map);
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateMasGroupUserReqData();
    }

    /**
     * 作用：处理ADC的服务个性化参数 暂时包括platsvc表和 TF_F_USR_GRP_MOLIST业务上行指令表
     *
     * @author liaolc 2013-08-10
     * @throws Exception
     */
    public void infoRegSpecialSvcParams() throws Exception
    {

        IDataset serparamset = reqData.cd.getSpecialSvcParam();

        if (IDataUtil.isEmpty(serparamset))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1005);
        }
        // 对于MAS业务仅能有1个主端口，主端口下可以有多个子端口，必须生成主端口后，方可生成子端口。
        int mainPortCount = 0;
        String mainPortErrMsg = "";
        for (int i = 0; i < serparamset.size(); i++)
        {
            IDataset serparaminfoset = (IDataset) serparamset.get(i);// 个性化参数信息结构
            IData serparam = serparaminfoset.getData(1);
            IData platsvc = serparam.getData("PLATSVC"); // platsvc表个性参数
            platsvc = IDataUtil.replaceIDataKeyDelPrefix(platsvc, "pam_");

            String portType = platsvc.getString("PORT_TYPE", "");
            String bizName = String.valueOf(IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_NAME"));

            if ("1".equals(portType)) {
                mainPortCount += 1;
                mainPortErrMsg += bizName + ",";
            }
        }
        if (mainPortCount == 0) {
            CSAppException.apperr(PlatException.CRM_PLAT_3028_1);
        } else if (mainPortCount != 1) {
            CSAppException.apperr(PlatException.CRM_PLAT_3028_2, mainPortErrMsg);
        }

        for (int i = 0; i < serparamset.size(); i++)
        {
            IDataset serparaminfoset = (IDataset) serparamset.get(i);// 个性化参数信息结构
            IData serparam = serparaminfoset.getData(1);
            String serviceId = serparam.getString("ID");// 取集团service_id
            String instId = serparam.getString("INST_ID");// 服务对应的实例标识,同attr表的rela_inst_id相对应/
            IData platsvc = serparam.getData("PLATSVC"); // platsvc表个性参数

            platsvc = IDataUtil.replaceIDataKeyDelPrefix(platsvc, "pam_");
            IDataset tempMolists = IDataUtil.getDataset(serparam, "MOLIST");// 业务上行指令参数

            if (IDataUtil.isNotEmpty(platsvc))
            {
                setRegPlatSvc(platsvc, serviceId, instId);
            }
            if (IDataUtil.isNotEmpty(tempMolists))
            {
                setRegGrpMoList(tempMolists, serviceId);
            }
        }
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateMasGroupUserReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    /**
     * 作用：处理ADC的指令参数，写入TF_F_USER_GRP_MOLIST
     *
     * @author liaolc 2013-09-02 16:24
     * @param serparamset
     * @throws Exception
     */
    public void setRegGrpMoList(IDataset molist, String serviceId) throws Exception
    {

        if (IDataUtil.isEmpty(molist))// 执行登记业务上行指令台帐操作
            return;

        for (int j = 0; j < molist.size(); j++)
        {
            IData moinfo = molist.getData(j);
            IData moinforeg = new DataMap();

            moinforeg.put("SEQ_ID", SeqMgr.getGrpMolist());
            moinforeg.put("USER_ID", reqData.getUca().getUserId());
            moinforeg.put("SERVICE_ID", serviceId);
            moinforeg.put("MO_CODE", moinfo.getString("MO_CODE", ""));
            moinforeg.put("MO_MATH", moinfo.getString("MO_MATH", ""));
            moinforeg.put("MO_TYPE", moinfo.getString("MO_TYPE", ""));
            moinforeg.put("DEST_SERV_CODE", moinfo.getString("DEST_SERV_CODE", ""));
            moinforeg.put("DEST_SERV_CODE_MATH", moinfo.getString("DEST_SERV_CODE_MATH", ""));
            moinforeg.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 标识新增

            moinforeg.put("REMARK", moinfo.getString("REMARK", ""));
            moinforeg.put("RSRV_NUM1", moinfo.getString("RSRV_NUM1", ""));
            moinforeg.put("RSRV_NUM2", moinfo.getString("RSRV_NUM2", ""));
            moinforeg.put("RSRV_NUM3", moinfo.getString("RSRV_NUM3", ""));
            moinforeg.put("RSRV_NUM4", moinfo.getString("RSRV_NUM4", ""));
            moinforeg.put("RSRV_NUM5", moinfo.getString("RSRV_NUM5", ""));
            moinforeg.put("RSRV_STR1", moinfo.getString("RSRV_STR1", ""));
            moinforeg.put("RSRV_STR2", moinfo.getString("RSRV_STR2", ""));
            moinforeg.put("RSRV_STR3", moinfo.getString("RSRV_STR3", ""));
            moinforeg.put("RSRV_STR4", moinfo.getString("RSRV_STR4", ""));
            moinforeg.put("RSRV_STR5", moinfo.getString("RSRV_STR5", ""));
            moinforeg.put("RSRV_DATE1", moinfo.getString("RSRV_DATE1", ""));
            moinforeg.put("RSRV_DATE2", moinfo.getString("RSRV_DATE2", ""));
            moinforeg.put("RSRV_DATE3", moinfo.getString("RSRV_DATE3", ""));
            moinforeg.put("RSRV_TAG1", moinfo.getString("RSRV_TAG1", ""));
            moinforeg.put("RSRV_TAG2", moinfo.getString("RSRV_TAG2", ""));
            moinforeg.put("RSRV_TAG3", moinfo.getString("RSRV_TAG3", ""));

            addTradeGrpMolist(moinforeg);
        }
    }

    /**
     * 作用：处理ADC的服务个性化参数，写入TF_F_USER_GRP_PLATSVC表
     *
     * @author liaolc 2013-09-02 15:43
     * @param serparamset
     *            从页面中获取的个性化参数串
     * @throws Exception
     */
    public void setRegPlatSvc(IData platsvc, String serviceId, String instId) throws Exception
    {

        if (IDataUtil.isEmpty(platsvc))// 完成对platsvc参数表的处理
            return;
        IData data = new DataMap();
        data.put("INST_ID", instId);
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());
        data.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId());
        data.put("CS_TEL", IDataUtil.getAndDelColValueFormIData(platsvc, "CS_TEL"));
        data.put("ACCESS_NUMBER", platsvc.getString("BIZ_IN_CODE"));
        data.put("BIZ_STATE_CODE", "A");
        data.put("BIZ_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_CODE")); // 1//
        // 业务代码
        data.put("BIZ_NAME", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_NAME")); // 2//
        // 业务名称
        data.put("SERV_CODE", platsvc.getString("BIZ_IN_CODE")); // 3//
        // 服务代码
        data.put("BILLING_TYPE", IDataUtil.getAndDelColValueFormIData(platsvc, "BILLING_TYPE")); // 4//
        // 计费类型//
        // (包月。。)
        data.put("ACCESS_MODE", platsvc.getString("ACCESS_MODE", "")); // 5对应1.3.3版业务承载方式01－SMS，02－WAPPush，03－MMS
        data.put("BIZ_STATUS", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_STATUS")); // 6//
        // 业务状态
        // (正常商用..)
        data.put("BIZ_ATTR", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_ATTR")); // 7
        // 业务属性(订购关系，白黑名单)
        data.put("PRICE", IDataUtil.getMandaData(platsvc, "PRICE", "0")); // 8 单价
        data.put("BIZ_TYPE_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_TYPE_CODE")); // 9// 业务细类
        // (短信,wap..)NOW这个要加到其他要展现的地方做获取
        data.put("USAGE_DESC", IDataUtil.getAndDelColValueFormIData(platsvc, "USAGE_DESC")); // 10业务方法描述
        data.put("BIZ_PRI", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_PRI")); // 11业务优先级
        data.put("INTRO_URL", IDataUtil.getAndDelColValueFormIData(platsvc, "INTRO_URL")); // 12
        data.put("CS_URL", IDataUtil.getAndDelColValueFormIData(platsvc, "CS_URL"));

        data.put("PRE_CHARGE", IDataUtil.getAndDelColValueFormIData(platsvc, "PRE_CHARGE")); // 15
        // 预付费标记
        data.put("MAX_ITEM_PRE_DAY", IDataUtil.getAndDelColValueFormIData(platsvc, "MAX_ITEM_PRE_DAY")); // 每天最大短信数

        data.put("IS_TEXT_ECGN", IDataUtil.getAndDelColValueFormIData(platsvc, "IS_TEXT_ECGN")); // 17
        data.put("MAX_ITEM_PRE_MON", IDataUtil.getAndDelColValueFormIData(platsvc, "MAX_ITEM_PRE_MON")); // 每月最大短信

        data.put("DEFAULT_ECGN_LANG", IDataUtil.getAndDelColValueFormIData(platsvc, "DEFAULT_ECGN_LANG")); // 19

        data.put("TEXT_ECGN_EN", IDataUtil.getAndDelColValueFormIData(platsvc, "TEXT_ECGN_EN"));
        data.put("ADMIN_NUM", IDataUtil.getAndDelColValueFormIData(platsvc, "ADMIN_NUM"));// 21 管理员手机号码
        data.put("TEXT_ECGN_ZH", IDataUtil.getAndDelColValueFormIData(platsvc, "TEXT_ECGN_ZH"));

        data.put("AUTH_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "AUTH_CODE"));// 23业务接入号鉴权方式
        data.put("FORBID_START_TIME_A", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_A"));
        data.put("FORBID_END_TIME_A", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_A"));
        data.put("FORBID_START_TIME_B", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_B"));
        data.put("FORBID_END_TIME_B", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_B"));
        data.put("FORBID_START_TIME_C", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_C"));
        data.put("FORBID_END_TIME_C", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_C"));
        data.put("FORBID_START_TIME_D", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_D"));
        data.put("FORBID_END_TIME_D", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_D"));
        data.put("FIRST_DATE", getAcceptTime());
        data.put("START_DATE", getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());

        data.put("REMARK", IDataUtil.getAndDelColValueFormIData(platsvc, "REMARK"));
        data.put("OPR_EFF_TIME", getAcceptTime());
        data.put("RSRV_NUM1", "");
        data.put("RSRV_NUM2", "");
        data.put("RSRV_NUM3", "");
        data.put("RSRV_NUM4", "");
        data.put("RSRV_NUM5", "");
        data.put("RSRV_STR1", platsvc.getString("MO_ACCESS_NUM", ""));// 短信上行访问码
        data.put("RSRV_STR2", platsvc.getString("RSRV_STR2", ""));// 企业邮箱业务时前台传入的邮箱域名

        data.put("RSRV_STR3", "02");// 对应行业网关2.0规范的EC业务信息同步isMsgReturn字段

        data.put("RSRV_STR4", platsvc.getString("RSRV_STR4", ""));
        data.put("RSRV_STR5", platsvc.getString("IS_MAS_SERV", ""));//mas增加是否mas服务器的配置

        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", "");
        data.put("RSRV_TAG2", platsvc.getString("RSRV_TAG2", "1"));// 集团客户等级
        // J2EE以前标识是否发服务开通，现在没用了.反向接口服务开通用TF_B_TRADE.in_mode_code字段绑定新增和删除指令
        data.put("RSRV_TAG3", platsvc.getString("RSRV_TAG3", "0"));

        // 1走服务开通发指令,0：不走服务开通不发指令
        data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));

        data.put("OPER_STATE", "01");// 01表示新增，与接口规范保持一致。
        data.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE", ""));// 业务接入号

        data.put("SI_BASE_IN_CODE", ""); // SI基本接入号
        data.put("SI_BASE_IN_CODE_A", "");// SI 基本接入号属性

        data.put("SERVICE_ID", serviceId);
        data.put("EC_BASE_IN_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "EC_BASE_IN_CODE"));
        data.put("EC_BASE_IN_CODE_A", platsvc.getString("EC_BASE_IN_CODE_A"));
        data.put("MAS_ID", platsvc.getString("MAS_ID")); // mas服务器标识

        data.put("PLAT_SYNC_STATE", platsvc.getString("PLAT_SYNC_STATE"));
        data.put("BILLING_MODE", platsvc.getString("BILLING_MODE", "")); // 计费模式
        data.put("DELIVER_NUM", platsvc.getString("DELIVER_NUM", "0")); // 限制下发次数(0为不限制)：

        data.put("MODIFY_TAG", platsvc.getString("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()));

        data.put("RSRV_NUM2", platsvc.getString("SERVICE_TYPE", "")); // 业务类型
        data.put("RSRV_NUM3", platsvc.getString("WHITE_TOWCHECK", "")); // 白名单二次确认
        data.put("RSRV_NUM5", platsvc.getString("SMS_TEMPALTE", "")); // 模板短信管理
        data.put("RSRV_STR1", platsvc.getString("PORT_TYPE", "")); // 端口类别

        addTradeGrpPlatsvc(data);
    }

}
