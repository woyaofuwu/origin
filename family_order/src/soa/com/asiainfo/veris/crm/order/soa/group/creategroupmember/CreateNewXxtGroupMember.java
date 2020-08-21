
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
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
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateAdcGroupMemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.common.query.LimitBlackwhiteBean;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class CreateNewXxtGroupMember extends CreateGroupMember
{
    protected CreateAdcGroupMemberReqData reqData = null;

    public CreateNewXxtGroupMember() throws Exception
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
        this.validateNewXxtData();

        super.actTradeSub();

        this.regBlackWhiteAndMemPlatsvc();

        // 新ADC校讯通
        // this.regXxtUUAndBlackWhite();

        //捆绑500M套餐
        actTradeDiscnt();
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
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUser);
        // 得到是否向黑白名单台帐表中拼套餐信息
        String synDiscntFlag = ctrlInfo.getAttrValue("SynDiscntFlag");
        reqData.setSynDiscntFlag(synDiscntFlag);
    }

    private void validateNewXxtData() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData productParam = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(GrpException.CRM_GRP_740);
        }
        productParam = IDataUtil.replaceIDataKeyDelPrefix(productParam, "NOTIN_");

        IData temp = null;
        IData param = new DataMap();

        // 获得选中的数据
        IDataset keyDataset = new DatasetList();
        Iterator<String> iterator = productParam.keySet().iterator();
        ;
        while (iterator.hasNext())
        {
            String key = iterator.next();

            if ("on".equals(productParam.getString(key)) && key.startsWith("ctag"))
            {
                String num = key.substring(4);
                keyDataset.add(num);// ["1","2"]

            }

        }
        if (IDataUtil.isEmpty(keyDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_740);
        }
        for (int j = 0; j < keyDataset.size(); j++)
        {
            String num = (String) keyDataset.get(j);
            String stuParamstr = productParam.getString("STU_PARAM_LIST" + num, "");
            IDataset stuParamList = new DatasetList(stuParamstr);
            if (IDataUtil.isEmpty(stuParamList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_740);
            }
            List elementIdList = new ArrayList();
            for (int i = 0; i < stuParamList.size(); i++)
            {
                temp = stuParamList.getData(i);
                String elementId = temp.getString("ELEMENT_ID");

                elementIdList.add(elementId);
            }
            IData groupMap = new DataMap();
            for (int i = 0; i < stuParamList.size(); i++)
            {
                temp = stuParamList.getData(i);
                String elementId = temp.getString("ELEMENT_ID");
                String stuKey = temp.getString("STUD_KEY");
                // 取得资费名称
                param = UDiscntInfoQry.getDiscntExtChaInfoByDiscntCode(elementId);
                String rsrv_str2 = param.getString("RSRV_STR2", "");
                if (groupMap.containsKey(rsrv_str2)) {
                    CSAppException.apperr(GrpException.CRM_GRP_840, stuKey);
                } else {
                    groupMap.put(rsrv_str2, elementId);
                }

                if (StringUtils.isNotBlank(rsrv_str2) && rsrv_str2.startsWith("group_"))
                {
                    String[] group = param.getString("RSRV_STR2", "").split("_");
                    String stu_nameX = "stu_name" + group[1];
                    if (!stuKey.equals(stu_nameX))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_741, "学生" + i, param.getString("DISCNT_NAME", ""));
                    }
                }

                // 包年套餐必须 依赖于包月套餐
                String rsrv_str3 = param.getString("RSRV_STR3", "");

                if (!StringUtils.isBlank(rsrv_str3) && !elementIdList.contains(rsrv_str3)) {
                    CSAppException.apperr(GrpException.CRM_GRP_847, elementId, rsrv_str3);
                }
            }
        }
    }

    // ADC校讯通
    private void regXxtUUAndBlackWhite(IData bwData) throws Exception
    {
        String mainSn = reqData.getUca().getSerialNumber();
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData productParam = reqData.cd.getProductParamMap(baseMemProduct);
        IDataset vdisCodeList = new DatasetList();

        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(GrpException.CRM_GRP_740);
        }
        productParam = IDataUtil.replaceIDataKeyDelPrefix(productParam, "NOTIN_");

        StringBuilder stuNameSB = new StringBuilder();
        StringBuilder stuKeySB = new StringBuilder();
        StringBuilder stuDiscodeSB = new StringBuilder();
        StringBuilder stuDisNameSB = new StringBuilder();
        IData temp = null;
        IData data = new DataMap();
        IData param = new DataMap();

        // 过渡出选中的异网号信息（包括校讯通成员参数信息）
        IDataset keyDataset = new DatasetList();
        Iterator<String> iterator = productParam.keySet().iterator();
        ;
        while (iterator.hasNext())
        {
            String key = iterator.next();

            if ("on".equals(productParam.getString(key)) && key.startsWith("ctag"))
            {
                String num = key.substring(4);
                keyDataset.add(num);// ["1","2"]

            }

        }
        if (IDataUtil.isEmpty(keyDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_740);
        }
        for (int j = 0; j < keyDataset.size(); j++)
        {
            String num = (String) keyDataset.get(j);
            String stuParamstr = productParam.getString("STU_PARAM_LIST" + num, "");
            String outSn = productParam.getString("OUT_SN" + num, "");
            String operType = productParam.getString("OPER_TYPE" + num, "");

            String userIdOutSn = SeqMgr.getUserId();
            String userIdOutSnUu = userIdOutSn;

            IDataset stuParamList = new DatasetList(stuParamstr);
            // 校验校讯通成员参数信息是否有数据
            if (IDataUtil.isEmpty(stuParamList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_740);
            }

            for (int i = 0, iSize = stuParamList.size(); i < iSize; i++)
            {
                temp = stuParamList.getData(i);
                String elementId = temp.getString("ELEMENT_ID");
                // 取得资费名称
                param = UDiscntInfoQry.getDiscntExtChaInfoByDiscntCode(elementId);

                String instIdDis = SeqMgr.getInstId();// 用来关联relationXXT 与 discnt 表

                IDataset pgDiscntList = reqData.cd.getDiscnt();

                // 判断校讯通参数页面选择的优惠是否在产品信息的已选区里。
                IDataset checkDiscntList = DataHelper.filter(pgDiscntList, "ELEMENT_ID=" + elementId);
                if (IDataUtil.isEmpty(checkDiscntList))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_746, param.getString("DISCNT_NAME", ""));
                }

                // 如果是新增优惠，relation_xxt表的RELA_INST_ID要与新增优惠的INST_ID相同
                String dealInstId = dealPgDiscnt(elementId);
                if (StringUtils.isNotBlank(dealInstId))
                {
                    instIdDis = dealInstId;
                }
                data.put("SERVICE_ID", "915001"); // 新校讯通目前只支持 校讯通成员短信服务
                data.put("RELA_INST_ID", instIdDis); // 用来关联relationXXT 与 discnt 表
                data.put("INST_ID", SeqMgr.getInstId());
                data.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());// 集团userId
                data.put("USER_ID_A", reqData.getUca().getUser().getUserId()); // 成员用户标识
                data.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber()); // 成员手机号
                data.put("SERIAL_NUMBER_B", outSn); // 代付费手机号
                data.put("ELEMENT_TYPE_CODE", "D");
                data.put("ELEMENT_ID", elementId);
                data.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime()); // 起始时间
                data.put("END_DATE", SysDateMgr.getTheLastTime()); // 终止时间
                data.put("NAME", temp.getString("STUD_NAME"));// 学生姓名
                data.put("REMARK", "");
                data.put("RSRV_NUM1", "0");
                data.put("RSRV_NUM2", "0");
                data.put("RSRV_NUM3", "0");
                data.put("RSRV_STR1", temp.getString("STUD_KEY"));
                data.put("RSRV_STR2", param.getString("RSRV_STR2", ""));// 学生类型资费标记
                data.put("RSRV_STR3", param.getString("DISCNT_NAME", ""));// 资费名称
                data.put("RSRV_STR4", "");
                data.put("RSRV_STR5", "");
                data.put("RSRV_STR6", "");
                data.put("RSRV_STR7", "");
                data.put("RSRV_STR8", "");
                data.put("RSRV_STR9", "");
                data.put("RSRV_STR10", "");
                data.put("RSRV_DATE1", "");
                data.put("RSRV_DATE2", "");
                data.put("RSRV_DATE3", "");
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 操作类型

                vdisCodeList.add(elementId);

              
                if(!stuKeySB.toString().contains(temp.getString("STUD_KEY")))
                {
                    stuNameSB.append(temp.getString("STUD_NAME"));
                    stuNameSB.append(",");

                    stuKeySB.append(temp.getString("STUD_KEY"));
                    stuKeySB.append(",");
                    
                }

                stuDiscodeSB.append(elementId);
                stuDiscodeSB.append(",");

                stuDisNameSB.append(param.getString("DISCNT_NAME", ""));
                stuDisNameSB.append(",");
                super.addTradeRelationXxt(data);// relationXXT

            }

            stuNameSB.append(reqData.getUca().getSerialNumber());
            stuKeySB.append("servicephone");
            if (outSn.equals(mainSn))
            {
                userIdOutSn = reqData.getUca().getUserId();// 成员的USER_ID
            }
            bwData.put("USER_ID", userIdOutSn);
            bwData.put("SERIAL_NUMBER", outSn);
            bwData.put("INST_ID", SeqMgr.getInstId());
            bwData.put("RSRV_STR2", stuNameSB);// 徐世富,陈业仁,13976514111
            bwData.put("RSRV_STR3", stuKeySB);// stu_name1,stu_name2,servicephone
            bwData.put("RSRV_STR4", stuDiscodeSB.substring(0, stuDiscodeSB.length() - 1)); // 4302,4303
            bwData.put("RSRV_STR5", stuDisNameSB.substring(0, stuDisNameSB.length() - 1)); // 优惠名称
            bwData.put("OPER_STATE", "01");// 新增
            // 1或者是空 走服务开通发指令,0：不走服务开通不发指令，校讯通产品第一笔台账不走务开通发，第二比笔台账才走
            bwData.put("IS_NEED_PF", "1");// J2EE新增IS_NEED_PF字段表示是否走服务开通

            super.addTradeBlackwhite(bwData);// blackwhite

            IData rela = new DataMap();
            rela.put("RELATION_TYPE_CODE", "XT");
            rela.put("USER_ID_A", userIdOutSnUu);
            rela.put("USER_ID_B", reqData.getUca().getUserId());
            rela.put("SERIAL_NUMBER_A", outSn);
            rela.put("SERIAL_NUMBER_B", mainSn);
            rela.put("ROLE_CODE_A", "1");
            rela.put("ROLE_CODE_B", "1");
            rela.put("INST_ID", SeqMgr.getInstId());
            rela.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            rela.put("END_DATE", SysDateMgr.getTheLastTime());
            rela.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId());
            rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 操作类型

            super.addTradeRelation(rela);// UU关系

            // 校验所选的资费是否都分配到了学生
            IDataset vDiscntList = reqData.cd.getDiscnt();
            for (int a = 0; a < vDiscntList.size(); a++)
            {
                IData vDiscntData = vDiscntList.getData(a);
                String pModifyTag = vDiscntData.getString("MODIFY_TAG", "");
                String pElementId = vDiscntData.getString("ELEMENT_ID", "");
                if (TRADE_MODIFY_TAG.Add.getValue().equals(pModifyTag))
                {
                    if (!vdisCodeList.contains(pElementId) && !"5911".equals(pElementId))// 如果列表包含指定的元素，则返回 true。
                    {
                        IData vparam = UDiscntInfoQry.getDiscntInfoByPk(pElementId);
                        CSAppException.apperr(GrpException.CRM_GRP_744, vparam.getString("DISCNT_NAME", ""));
                    }

                }
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
            CSAppException.apperr(GrpException.CRM_GRP_25);
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
                CSAppException.apperr(GrpException.CRM_GRP_637);
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

        if ("0".equals(bizattr))
        {// 订购关系
            usertypecode = "S";
        }
        else if ("1".equals(bizattr))
        {// 白名单
            usertypecode = "W";
            String dataId = platsvc.getString("RSRV_TAG2");
            if (!"4".equals(dataId) && !"3".equals(dataId) && !"2".equals(dataId) && !"1".equals(dataId))// VIP级成员数量不受限制

            {
                String typeId = "GRP_PLAT_RSRV_TAG2";
                IData staticInfo = StaticInfoQry.getStaticInfoByTypeIdDataId(typeId, dataId);// 获取该集团允许的最大成员数量
                if (IDataUtil.isNotEmpty(staticInfo))
                {
                    String ecUserId = reqData.getGrpUca().getUserId();
                    String serviceId = platsvcdata.getString("SERVICE_ID");
                    String userTypeCode = "W"; // 白名单用户
                    IData gblackWhiteUserCount = UserBlackWhiteInfoQry.qryblackWhiteByServiceIdEcUserId(ecUserId, serviceId, userTypeCode);// 获取该集团当前成员数量
                    if (IDataUtil.isNotEmpty(gblackWhiteUserCount))
                    {
                        if (staticInfo.getInt("PDATA_ID") <= gblackWhiteUserCount.getInt("USER_COUNT"))
                        {
                            // common.error("该集团的成员总数已经超额,请联系管理员:目前该集团允许成员总数为："+staticInfo.getString("PDATA_ID")+" ;该集团现有成员总数为："+gblackWhiteUserCount.getString("USER_COUNT"));
                            CSAppException.apperr(CrmCommException.CRM_COMM_1025, staticInfo.getString("PDATA_ID"), gblackWhiteUserCount.getString("USER_COUNT"));
                        }
                    }
                }
            }

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

        data.put("OPER_STATE", "01");// 01表示新增 2013-01-19 J2EE项目修改，与接口规范保持一致。
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
        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");
        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", "");
        data.put("RSRV_TAG2", platsvcdata.getString("RSRV_TAG2", "0"));// 0-实时接口 1-文件接口
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
        data.put("EXPECT_TIME", platsvcdata.getString("EXPECT_TIME"));
        data.put("PLAT_SYNC_STATE", platsvcdata.getString("PLAT_SYNC_STATE"));
        data.put("EC_SERIAL_NUMBER", platsvc.getString("SERIAL_NUMBER"));

        data.put("IS_NEED_PF", "1");// // 1或者是空 走服务开通发指令,0：不走服务开通不发指令
        data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3", "0"));// 标识是否走服务开通 0 正常走服务开通模式 1 ADC平台 2 行业网关

        IData commBWDate = new DataMap(data.toString());

        // 新ADC校讯通处理
        this.regXxtUUAndBlackWhite(data);

        String userId = reqData.getUca().getUserId();
        String mainSn = reqData.getUca().getSerialNumber();

        if (!userId.equals(data.getString("USER_ID")) && !mainSn.equals(data.getString("SERIAL_NUMBER")))
        {
            commBWDate.put("IS_NEED_PF", "0");// // 1或者是空 走服务开通发指令,0：不走服务开通不发指令
            super.addTradeBlackwhite(commBWDate);
        }

    }

    /*
     * @method 登记校讯通业务,主号码绑定的其他号码的blackwhite表
     */

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

    /**
     * 如果是新增优惠，relation_xxt表的INST_ID要与新增优惠的INST_ID相同
     */
    public String dealPgDiscnt(String elementId) throws Exception
    {
        // 如果是新增优惠，relation_xxt表的INST_ID要与新增优惠的INST_ID相同
        IDataset pgDiscntList = reqData.cd.getDiscnt();
        String inst_id = "";
        for (int a = 0; a < pgDiscntList.size(); a++)
        {
            IData pDiscntData = pgDiscntList.getData(a);
            String pModifyTag = pDiscntData.getString("MODIFY_TAG", "");
            String pElementId = pDiscntData.getString("ELEMENT_ID", "");
            String pInstId = pDiscntData.getString("INST_ID", "");
            if (TRADE_MODIFY_TAG.Add.getValue().equals(pModifyTag) && elementId.equals(pElementId))
            {
                inst_id = pInstId; // 每个学生订购的资费 生成discnt表与relation_xxt表的关联字段
                break;
            }
        }
        return inst_id;
    }
    
    /**
     * 捆绑500M套餐
     * @throws Exception
     */
    protected void actTradeDiscnt() throws Exception{
    	System.out.println("chenhh==捆绑500M套餐");
    	IDataset dataset = bizData.getTradeDiscnt();
        IDataset discntConfig = CommparaInfoQry.getCommparaByAttrCode2("CSM", "9055", "D", "ZZZZ", null);	    	//捆绑配置表
        if (IDataUtil.isEmpty(dataset) || IDataUtil.isEmpty(discntConfig))  return;
        
        for (int i = 0; i < dataset.size(); i++) {
        	IData data = dataset.getData(i);
        	String disTraStr =  data.getString("DISCNT_CODE","0");
        	for (int j = 0; j < discntConfig.size(); j++) {
        		IData conData = discntConfig.getData(j);
				String disConStr = conData.getString("PARAM_CODE","");
				if (disTraStr.equals(disConStr)) {		//取得配置捆绑套餐;
					System.out.println("chenhh==当前办理的优惠"+disConStr);
					System.out.println("chenhh==取得需要捆绑的优惠"+conData.getString("PARA_CODE1","0"));
					//增加配置捆绑套餐
					System.out.println("chenhh==原优惠"+data);
					IData newData = getnewData(data);
					System.out.println("chenhh==转换后的Data"+newData);
					newData.put("INST_ID", SeqMgr.getInstId());
					newData.put("DISCNT_CODE", conData.getString("PARA_CODE1","0"));
					
					System.out.println("chenhh==捆绑的新优惠"+newData);
					dataset.add(newData);
					System.out.println("chenhh==捆绑后所有优惠"+bizData.getTradeDiscnt());
				}
			}
		}
    }
    
    protected IData getnewData(IData data) throws Exception{
    	IData newData = new DataMap();
    	Iterator it = data.entrySet().iterator();
    	while (it.hasNext()) {
    		IData.Entry entry = (IData.Entry) it.next();
    		String key = (String)entry.getKey();
    		Object value = entry.getValue();
    		newData.put(key, value);
		}
    	return newData;
    }
    
}
