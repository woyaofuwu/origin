
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeAdcGroupUserElementReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeAdcGroupUser extends ChangeUserElement
{
    protected ChangeAdcGroupUserElementReqData reqData = null;

    private  String STR_USER_STATE_CODESET = "";

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // req产品控制信息
        mekReqProductCtrlInfo();

        // 企业一卡通设备租赁费（由前台界面录入）
        if (("10005743").equals(reqData.getUca().getProductId()))
        {
            mekReqUserOther();// 企业一卡通设备租赁费（由前台界面录入）
        }

    }

    private void mekReqProductCtrlInfo() throws Exception
    {
        /*********** 海南往adc平台只传一个优惠****start******目前需要同步的产品10005801 、10005841 ************************/
        String productId = reqData.getUca().getProductId();
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.ChangeUserDis);
        // 得到是否向黑白名单台帐表中拼套餐信息
        String synDiscntFlag = ctrlInfo.getAttrValue("SynDiscntFlag");
        reqData.setSynDiscntFlag(synDiscntFlag);
    }

    private void mekReqUserOther() throws Exception
    {
        // 企业一卡通设备租赁费（由前台界面录入）
        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        reqData.setHireFee(productParam.getString("NOTIN_HIRE_FEE", ""));
        reqData.setFeeCycle(productParam.getString("NOTIN_FEE_CYCLE", ""));
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     *
     * @author liaolc
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 服务状态表
        // actTradeSvcState();

        this.infoRegSpecialSvcParams();

        // ADC一卡通租赁费
        this.infoRegHireFee();
        
        //处理EC客户信息
        actTrade();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeAdcGroupUserElementReqData();
    }

    /**
     * 服务状态表 liaolc 2014-03-20
     *
     * @throws Exception
     */
    @Override
    public void actTradeSvcState() throws Exception
    {

        IDataset svcDataset = reqData.cd.getSvc();
        if (IDataUtil.isEmpty(svcDataset))
        {
            return;
        }
        IDataset specialSvcDataset = reqData.cd.getSpecialSvcParam();

        IDataset svcStates = new DatasetList();
        String eparchyCode = Route.CONN_CRM_CG;

        for (int i = 0; i < specialSvcDataset.size(); i++)// 每一条记录 包含一条服务的个性化参数信息
        {
            IDataset serparaminfoset = (IDataset) specialSvcDataset.get(i);// 个性化参数信息结构
            // 20090527 加上NEXT的控制 DataMap 后，数据的就在1位置了
            IData serparam = serparaminfoset.getData(1);
            IData platsvc = serparam.getData("PLATSVC");// platsvc表个性参数
            platsvc = IDataUtil.replaceIDataKeyDelPrefix(platsvc, "pam_");
            String operstate = platsvc.getString("OPER_STATE", "");
            String serviceId = serparam.getString("ID");

            String newstate = ""; // 新状态
            IDataset result = new DatasetList();

            // 暂停恢复操作 需要对该服务状态进行特殊操作,此处处理的肯定是该服务为Modi的状态//后面再有一段代码处理ADD和DEL的情况
            if (operstate.equals("04") || operstate.equals("05"))
            {
                if (operstate.equals("04"))// 暂停
                {
                    newstate = "5";// 欠费停机
                }
                else if (operstate.equals("05"))// 恢复
                {
                    newstate = "0";// 开通
                }

                modiSvcState(serviceId, result, eparchyCode, newstate);// 需要对该服务状态进行修改 即 删除一条状态，新增一条状态

                svcStates.addAll(result);
            }
        }

        int delsvc = 0;// 计算办理时删除的服务个数
        for (int i = 0, iSize = svcDataset.size(); i < iSize; i++)
        {
            IDataset svcStateList = new DatasetList();
            IData svcData = svcDataset.getData(i);
            String modifyTag = svcData.getString("MODIFY_TAG", "");

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                addSvcState(svcData, svcStateList);
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                delsvc++;
                delSvcState(svcData, svcStateList);
            }
            svcStates.addAll(svcStateList);
        }

        // 如果所有的业务服务都暂停了，则把主体服务也暂停
        svcStates = mainServState(svcStates, svcDataset, delsvc, eparchyCode);

        super.addTradeSvcstate(svcStates);
    }

    public void infoRegHireFee() throws Exception
    {
        // 企业一卡通设备租赁费（由前台界面录入）
        if (!("10005743").equals(reqData.getUca().getProductId()))
        {
            return;
        }
        String hireFee = reqData.getHireFee();
        if (StringUtils.isNotEmpty(hireFee))
        {
            String grpUserId = reqData.getUca().getUser().getUserId();
            String rsrvValueCode = "OFEE";
            IDataset userOther = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(grpUserId, rsrvValueCode);
            IData map = null;
            if (IDataUtil.isEmpty(userOther))
            {
                map = new DataMap();
                map.put("INST_ID", SeqMgr.getInstId());
                map.put("USER_ID", grpUserId);
                map.put("RSRV_VALUE_CODE", "OFEE");
                map.put("RSRV_VALUE", reqData.getHireFee());// 设备租赁费
                map.put("RSRV_STR1", reqData.getFeeCycle());// 计费周期
                map.put("RSRV_STR2", "AYKT");// adc的一卡通
                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                map.put("START_DATE", getAcceptTime());
                map.put("END_DATE", SysDateMgr.getTheLastTime());
                map.put("REMARK", "企业一卡通设备租赁费");
                super.addTradeOther(map);
            }
            else
            {
                map = userOther.getData(0);

                String hireFeeOld = map.getString("RSRV_VALUE", "");// 老设备租赁费
                String feeCycleOld = map.getString("RSRV_STR1", "");// 老计费周期

                if (!hireFeeOld.equals(reqData.getHireFee()) || feeCycleOld.equals(reqData.getFeeCycle()))
                {
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    map.put("RSRV_VALUE", reqData.getHireFee());// 设备租赁费
                    map.put("RSRV_STR1", reqData.getFeeCycle());// 计费周期
                    super.addTradeOther(map);
                }
            }
        }
    }

    /**
     * 作用：处理ADC的服务个性化参数 暂时包括platsvc表和 TF_F_USR_GRP_MOLIST业务上行指令表
     *
     * @author liaolc 2014-08-10
     * @throws Exception
     */
    public void infoRegSpecialSvcParams() throws Exception
    {
        IDataset serparamset = reqData.cd.getSpecialSvcParam();
        IDataset distincnt = reqData.cd.getDiscnt();// 集团的优惠

        if (IDataUtil.isEmpty(serparamset))
        {
            serparamset = new DatasetList();
        }


        // 以下一段 根据需要设置成员对应该集团的优惠
        String userID = reqData.getUca().getUserId();
        IDataset userPlatLists = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserId(userID);// 取平集团用户参数

        // 对于需要同优惠给平台的 只变更了优惠时,对该用户的所有服务,向平台发起变更操作
        if (IDataUtil.isEmpty(serparamset) && IDataUtil.isNotEmpty(distincnt) && "true".equals(reqData.getSynDiscntFlag()))
        {

            for (int i = 0; i < userPlatLists.size(); i++)
            {
                IData platSvcdata = userPlatLists.getData(i);
                if (!"P".equals(platSvcdata.getString("PLAT_SYNC_STATE", "")))
                {

                    platSvcdata.put("OPER_STATE", "08");
                    platSvcdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    platSvcdata.put("SVR_CODE_HEAD", platSvcdata.getString("RSRV_NUM4", ""));
                    platSvcdata.put("SIBASE_INCODE", platSvcdata.getString("SI_BASE_IN_CODE", ""));
                    platSvcdata.put("SIBASE_INCODE_A", platSvcdata.getString("SI_BASE_IN_CODE_A", ""));
                    platSvcdata.put("EC_BASE_IN_CODE_A", platSvcdata.getString("SI_BASE_IN_CODE_A", ""));

                    IDataset serparaminfoset = new DatasetList();
                    IData serparam = new DataMap();
                    serparam.put("PLATSVC", platSvcdata);
                    serparam.put("INST_ID", platSvcdata.getString("INST_ID"));
                    serparam.put("ID", platSvcdata.getString("SERVICE_ID", ""));
                    serparam.put("CANCLE_FLAG", "false");
                    serparam.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    IData paramverify = new DataMap();
                    serparaminfoset.add(0, paramverify);
                    serparaminfoset.add(1, serparam);
                    serparamset.add(serparaminfoset);
                }
            }
        }

        if (IDataUtil.isNotEmpty(serparamset))
        {
            for (int i = 0, size = serparamset.size(); i < size; i++)
            {
                IDataset serparaminfoset = (IDataset) serparamset.get(i);// 个性化参数信息结构
                IData serparam = serparaminfoset.getData(1);
                IData platsvc = serparam.getData("PLATSVC"); // platsvc表个性参数
                platsvc = IDataUtil.replaceIDataKeyDelPrefix(platsvc, "pam_");
                IDataset tempMolists = IDataUtil.getDataset(serparam, "MOLIST");// 业务上行指令参数
                String serviceId = serparam.getString("ID");// 取service_id
                String instId = serparam.getString("INST_ID");// 服务对应的实例标识
                String servicestate = serparam.getString("MODIFY_TAG", "");
                String canflag = serparam.getString("CANCLE_FLAG", "");

                if (!"false".equals(canflag) && !TRADE_MODIFY_TAG.DEL.getValue().equals(servicestate))
                {
                    continue;// 这条记录虽然界面上点击弹出窗口弹出来过参数页面,但没点击过确定按钮,所以直接跳过此记录的处理
                }

                if (IDataUtil.isNotEmpty(platsvc))
                {
                    setRegChangePlatsvc(platsvc, serviceId, servicestate, instId);
                }
                if (IDataUtil.isNotEmpty(tempMolists))
                {
                    setRegChangeMolist(tempMolists, serviceId);
                }
            }
        }

    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeAdcGroupUserElementReqData) getBaseReqData();
    }

    /*
     * 以下判断如果所有的业务服务都暂停了，则把主体服务也暂停
     */
    public IDataset mainServState(IDataset svcStates, IDataset svcDataset, int delsvc, String eparchyCode) throws Exception
    {
        String stateCode = "5";
        String mainStateCode = "0";
        String main_serv = "";
        String userId = reqData.getUca().getUser().getUserId();
        IDataset userSvc = UserSvcStateInfoQry.getUserNowSvcStateByUserIdNow(userId, eparchyCode);
        IDataset normalUserSvc = new DatasetList();
        for (int i = 0; i < userSvc.size(); i++)
        {
            IData normaData = new DataMap();
            IData data = userSvc.getData(i);
            if ("1".equals(data.getString("MAIN_TAG", "")))
            {// 剔除主体服务，只要业务服务的记录
                mainStateCode = data.getString("STATE_CODE", "");
                main_serv = data.getString("SERVICE_ID", "");
                continue;
            }
            else if ("5".equals(data.getString("STATE_CODE", "")))
            {
                continue;

            }
            normaData.put("SERVICE_ID", data.getString("SERVICE_ID", ""));
            normalUserSvc.add(normaData);
        }

        for (int j = 0; j < svcStates.size(); j++)
        {
            IData svcStatData = svcStates.getData(j);
            IData tempData = new DataMap();
            String modifyTag = svcStatData.getString("MODIFY_TAG", "");
            String serviceId = svcStatData.getString("SERVICE_ID", "");
            String svcStat = svcStatData.getString("STATE_CODE", "");
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag) && "0".equals(svcStat))
            {
                tempData.put("SERVICE_ID", serviceId);
                normalUserSvc.add(tempData);

            }
            else if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag) && "5".equals(svcStat) || (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)))
            {
                tempData.put("SERVICE_ID", serviceId);
                normalUserSvc.remove(tempData);
            }

        }
        if (normalUserSvc.size() > 0)
        {
            stateCode = "0";

        }
        if (!mainStateCode.equals(stateCode))
        {
            IDataset temp = new DatasetList();
            modiSvcState(main_serv, temp, eparchyCode, stateCode);
            svcStates.addAll(temp);
            setUserStateCodeset(stateCode);

        }
        return svcStates;
    }

    /*
     * 设置 USER_STATE_CODESET 的值， ADC主体服务暂停或恢复要修改tf_f_user。 在方法 getTradeUserExtendData 中判断USER_STATE_CODESET是否改变
     */
    private void setUserStateCodeset(String userStateCodeset) throws Exception
    {
        if (StringUtils.isNotEmpty(userStateCodeset))
        {
            STR_USER_STATE_CODESET = userStateCodeset;
        }
    }

    @Override
    public IData getTradeUserExtendData() throws Exception
    {
        IData userExtenData = super.getTradeUserExtendData();
        if (StringUtils.isNotEmpty(STR_USER_STATE_CODESET))
        {
            userExtenData.put("USER_STATE_CODESET", STR_USER_STATE_CODESET);// 跟服务状态一致
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        return userExtenData;
    }

    /**
     * 作用:处理上行指令参数，组织台账数据
     *
     * @author liaolc 2013-9-15
     * @param molist
     * @param serviceId
     * @return
     * @throws Exception
     */
    public void setRegChangeMolist(IDataset molist, String serviceId) throws Exception
    {
        if (IDataUtil.isEmpty(molist))// 执行登记业务上行指令台帐操作
            return;

        for (int j = 0, jSize = molist.size(); j < jSize; j++)
        {
            IData moinfo = molist.getData(j);
            IData moinforeg = new DataMap();
            moinforeg.put("SEQ_ID", moinfo.getString("SEQ_ID", ""));
            moinforeg.put("USER_ID", reqData.getUca().getUserId());
            moinforeg.put("SERVICE_ID", serviceId);
            moinforeg.put("MO_CODE", moinfo.getString("MO_CODE", ""));
            moinforeg.put("MO_MATH", moinfo.getString("MO_MATH", ""));
            moinforeg.put("MO_TYPE", moinfo.getString("MO_TYPE", ""));
            moinforeg.put("DEST_SERV_CODE", moinfo.getString("DEST_SERV_CODE", ""));
            moinforeg.put("DEST_SERV_CODE_MATH", moinfo.getString("DEST_SERV_CODE_MATH", ""));
            String xtag = moinfo.getString("tag", "2");// 默认取为要修改,这样可以保证trade表有全量需要同步的数据
            if ("0".equals(xtag))// 新加记录
            {
                moinforeg.put("SEQ_ID", SeqMgr.getGrpMolist());
                moinforeg.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 标识新增
            }
            else if ("1".equals(xtag))// 要删除的记录
            {
                moinforeg.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 标识删除
            }
            else if ("2".equals(xtag))// 要修改的记录
            {
                moinforeg.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());// 标识修改
            }
            else
            {
                moinforeg.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());// 标识修改
            }

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
     * 作用：TF_F_USER_GRP_PLATSVC表组织数据写台账
     *
     * @author liaolc 2013-9-15 14:35
     * @param platsvc
     * @param serviceId
     * @param servicestate
     * @throws Exception
     */
    public void setRegChangePlatsvc(IData platsvc, String serviceId, String servicestate, String instId) throws Exception
    {
        String operState = (IDataUtil.isNotEmpty(platsvc)) ? platsvc.getString("OPER_STATE", "") : ""; // 操作类型

        IData data = new DataMap();
        String userId = reqData.getUca().getUserId();
        data.put("USER_ID", userId);
        data.put("INST_ID", instId);
        // 非新增业务INST_ID取资料表的数据
        if (!TRADE_MODIFY_TAG.Add.getValue().equals(servicestate))
        {
            IData platsvcparam = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(userId, serviceId);// 取平台服务表已经存在的参数
            data.put("INST_ID", platsvcparam.getString("INST_ID"));
        }
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        data.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId());
        data.put("CS_TEL", IDataUtil.getAndDelColValueFormIData(platsvc, "CS_TEL"));
        data.put("ACCESS_NUMBER", platsvc.getString("BIZ_IN_CODE"));

        data.put("BIZ_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_CODE")); // 1业务代码
        data.put("BIZ_NAME", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_NAME")); // 2 业务名称
        data.put("SERV_CODE", platsvc.getString("BIZ_IN_CODE"));// 3服务代码
        data.put("BILLING_TYPE", IDataUtil.getAndDelColValueFormIData(platsvc, "BILLING_TYPE")); // 4计费类型
        data.put("ACCESS_MODE", platsvc.getString("ACCESS_MODE", "")); // 5 对应1.3.3版 业务承载方式01－SMS，02－WAPPush，03－MMS
        data.put("BIZ_STATUS", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_STATUS")); // 6 业务状态
        data.put("BIZ_ATTR", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_ATTR")); // 7 业务属性 (订购关系，白黑名单)
        data.put("PRICE", IDataUtil.getAndDelColValueFormIData(platsvc, "PRICE")); // 8 单价
        data.put("BIZ_TYPE_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_TYPE_CODE")); // 9 业务细类(短信,wap..)
        // NOW
        // 这个要加到其他要展现的地方做获取
        data.put("USAGE_DESC", IDataUtil.getAndDelColValueFormIData(platsvc, "USAGE_DESC")); // 10 业务方法描述
        data.put("BIZ_PRI", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_PRI")); // 11 业务优先级
        data.put("INTRO_URL", IDataUtil.getAndDelColValueFormIData(platsvc, "INTRO_URL")); // 12业务介绍网址
        data.put("CS_URL", IDataUtil.getAndDelColValueFormIData(platsvc, "CS_URL")); // 13 EC 的 URL
        // 14 计费模式 (上行，下行..)NOW暂无表字段
        data.put("PRE_CHARGE", IDataUtil.getAndDelColValueFormIData(platsvc, "PRE_CHARGE")); // 15 预付费标记
        data.put("MAX_ITEM_PRE_DAY", IDataUtil.getAndDelColValueFormIData(platsvc, "MAX_ITEM_PRE_DAY")); // 16 每天最大短信数
        data.put("IS_TEXT_ECGN", IDataUtil.getAndDelColValueFormIData(platsvc, "IS_TEXT_ECGN")); // //17 短信正文签名
        data.put("MAX_ITEM_PRE_MON", IDataUtil.getAndDelColValueFormIData(platsvc, "MAX_ITEM_PRE_MON")); // 18 每月最大短信
        data.put("DEFAULT_ECGN_LANG", IDataUtil.getAndDelColValueFormIData(platsvc, "DEFAULT_ECGN_LANG")); // 19 签名语言
        data.put("TEXT_ECGN_EN", IDataUtil.getAndDelColValueFormIData(platsvc, "TEXT_ECGN_EN")); // 20 中文签名
        data.put("ADMIN_NUM", IDataUtil.getAndDelColValueFormIData(platsvc, "ADMIN_NUM")); // 21管理员手机号码
        data.put("TEXT_ECGN_ZH", IDataUtil.getAndDelColValueFormIData(platsvc, "TEXT_ECGN_ZH")); // 22 英文签名
        data.put("AUTH_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "AUTH_CODE"));// 23 业务接入号鉴权方式

        data.put("FIRST_DATE", getAcceptTime()); // 第一次受理时间

        String startdate = platsvc.getString("START_DATE", "");
        data.put("START_DATE", "".equals(startdate) ? getAcceptTime() : startdate);// 开始时间

        data.put("REMARK", IDataUtil.getAndDelColValueFormIData(platsvc, "REMARK"));
        data.put("OPR_EFF_TIME", IDataUtil.getAndDelColValueFormIData(platsvc, "OPR_EFF_TIME"));
        data.put("RSRV_NUM1", "");
        data.put("RSRV_NUM2", "");
        data.put("RSRV_NUM3", "");
        data.put("RSRV_NUM4", platsvc.getString("SVR_CODE_HEAD", ""));
        data.put("RSRV_NUM5", "");
        data.put("RSRV_STR1", platsvc.getString("MO_ACCESS_NUM", ""));// 短信上行访问号码
        if("100022".equals(serviceId) || "100024".equals(serviceId))
        {
            data.put("RSRV_STR2", platsvc.getString("SP_CODE", ""));//校讯通填写合作伙伴编码
            
        }
        else if("100082".equals(serviceId) || "100083".equals(serviceId))
        {
            data.put("RSRV_STR2", platsvc.getString("CITY_CODE", "")+","+platsvc.getString("CUST_MANAGER_ID", "")
                    +","+platsvc.getString("STAFF_NAME", "")+","+serviceId);//集团通讯录改造
            
        }
        else
        {
            data.put("RSRV_STR2", platsvc.getString("RSRV_STR2", ""));// 企业邮箱业务时前台传入的邮箱域名
        }
        
        if("100082".equals(serviceId) || "100083".equals(serviceId))
        {
            data.put("RSRV_STR4", "CITY_CODE,CUST_MANAGER_ID,STAFF_NAME,SERVICE_ID");
            
        }

        IDataset existDiscnt = UserDiscntInfoQry.queryUserAllDiscntByUserIdForGrp(userId);// GroupUtil.getDataset(commData,
        // "EXIST_DISCNT");
        String strDntList = "";
        String strDntName = "";

        if (IDataUtil.isNotEmpty(existDiscnt))// 海南往adc平台只传一个优惠,所以如果有优惠,先对优惠的起始时间进行排序,然后取最后生效的这条
        {
            DataHelper.sort(existDiscnt, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            strDntList = existDiscnt.getData(0).getString("DISCNT_CODE", "-1");
            strDntName = UDiscntInfoQry.getDiscntNameByDiscntCode(strDntList);
            ;
        }
        else
        {
            strDntList = "-1";
            strDntName = "-1";
        }

        if (reqData.getSynDiscntFlag().equals("true"))// 如果需要同步优惠给平台,则存在这两个备用字段内
        {
            data.put("RSRV_STR3", strDntList);// 存优惠编码
            data.put("RSRV_STR5", strDntName);// 存优惠名称
        }
        else
        {
            data.put("RSRV_STR3", "");
            data.put("RSRV_STR5", "");
        }

        // String siBaseInCode = AttrItemInfoQry.queryServiceItemA(serviceId);// SI基本接入号应该是不加地市扩展码之前的号码，对应ITEMA表中的ZZZZ配置
       // data.put("RSRV_STR4", "");
        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", platsvc.getString("RSRV_TAG1", ""));// 全网ADC时 产品受理付费模式
        data.put("RSRV_TAG2", platsvc.getString("RSRV_TAG2", "1"));// 集团客户等级

        data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// 1或者是空,走服务开通发指令,0：不走服务开通不发指令

        data.put("RSRV_TAG3", platsvc.getString("RSRV_TAG3", "0"));// J2EE修改 0 正常走服务开通模式，1 只ADC平台，2 只行业网关，

        // 以后有只发一个平台的产品时，可与 in_mode_code绑定用,服开暂时没取RSRV_TAG3字段;现在用的主表in_mode_code字段,字段值为P 只向网关发送数据 值为G 只向ADC发送数据 其他值
        // adc平台和网关都发送.

        data.put("BIZ_IN_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_IN_CODE"));// 业务接入号
        data.put("SI_BASE_IN_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "SIBASE_INCODE"));// SI基本接入号应该是不加地市扩展码之前的号码，对应ITEMA表中的ZZZZ配置
        // ADDBY ZHANGWG
        // 20110607
        data.put("SI_BASE_IN_CODE_A", IDataUtil.getAndDelColValueFormIData(platsvc, "SIBASE_INCODE_A"));// SI 基本接入号属性
        data.put("SERVICE_ID", serviceId);// "92000076");
        data.put("EC_BASE_IN_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "EC_BASE_IN_CODE"));
        data.put("EC_BASE_IN_CODE_A", IDataUtil.getAndDelColValueFormIData(platsvc, "SIBASE_INCODE_A")); // 这个没有界面，现直接写SI接入号的属性
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(servicestate))
        {
            data.put("OPER_STATE", "02");// 02终止 2013-01-19 J2EE项目修改，与接口规范保持一致。
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 删除这个服务
            data.put("END_DATE", getAcceptTime());
        }
        else if (TRADE_MODIFY_TAG.Add.getValue().equals(servicestate))//
        {
            data.put("OPER_STATE", "01");// 新增
            data.put("FIRST_DATE", getAcceptTime());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());//
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            data.put("OPR_EFF_TIME", getAcceptTime());
        }
        else
        {

            data.put("OPER_STATE", IDataUtil.getAndDelColValueFormIData(platsvc, "OPER_STATE"));
            //data.put("MODIFY_TAG", platsvc.getString("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()));// 修改还是新增服务参数
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            // 根据外部传入 默认为修改
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            // 根据外部传入
        }

        if ("04".equals(operState))// 04暂停
        {
            data.put("PLAT_SYNC_STATE", "P");
            data.put("BIZ_STATE_CODE", "N"); // cb里无此界面
        }
        else
        {
            data.put("PLAT_SYNC_STATE", "1");
            data.put("BIZ_STATE_CODE", "A"); // cb里无此界面
        }

        data.put("BILLING_MODE", platsvc.getString("BILLING_MODE", "")); // 计费模式
        data.put("DELIVER_NUM", platsvc.getString("DELIVER_NUM", ""));// 限制下发次数(0为不限制)：

        data.put("FORBID_START_TIME_A", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_A"));
        data.put("FORBID_END_TIME_A", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_A"));
        data.put("FORBID_START_TIME_B", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_B"));
        data.put("FORBID_END_TIME_B", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_B"));
        data.put("FORBID_START_TIME_C", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_C"));
        data.put("FORBID_END_TIME_C", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_C"));
        data.put("FORBID_START_TIME_D", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_D"));
        data.put("FORBID_END_TIME_D", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_D"));

        data.put("RSRV_NUM2", platsvc.getString("SERVICE_TYPE", "")); // 业务类型
        data.put("RSRV_NUM3", platsvc.getString("WHITE_TOWCHECK", "")); // 白名单二次确认
        data.put("RSRV_NUM5", platsvc.getString("SMS_TEMPALTE", "")); // 模板短信管理
        data.put("RSRV_STR1", platsvc.getString("PORT_TYPE", "")); // 端口类别

        addTradeGrpPlatsvc(data);// 组织添加台帐表TF_B_TRADE_GRP_PLATSVC
    }
    
    /**
     * 处理EC客户信息
     * @throws Exception
     */
    public void actTrade() throws Exception{
        if (("9230").equals(reqData.getUca().getProductId())){
        	//直接存台账 
        	IData trade = bizData.getTrade();
        	trade.put("RSRV_STR1", reqData.getUca().getCustGroup().getProvinceCode());//EC归属省编码
        	IData qryParam = new DataMap();
			qryParam.put("CUST_MANAGER_ID", reqData.getUca().getCustGroup().getCustManagerId());
			IDataset maninfos = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", qryParam, Route.CONN_CRM_CEN);
			if (maninfos.size()>0) {
				trade.put("RSRV_STR2", maninfos.getData(0).getString("SERIAL_NUMBER",""));//EC客户经理电话
			}
        }
    }
}
