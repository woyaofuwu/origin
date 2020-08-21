
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeAdcMemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeNewXxtMemElement extends ChangeMemElement
{

    protected ChangeAdcMemberReqData reqData = null;

    public ChangeNewXxtMemElement()
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
     * @author liaolc
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        this.validateNewXxtData();

        this.regXxtUUAndBlackWhite();

        // this.regBlackWhiteAndMemPlatsvc();

        //捆绑500M套餐
        actTradeDiscnt();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeAdcMemberReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeAdcMemberReqData) getBaseReqData();
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
     * 新校讯通校验
     *
     * @throws Exception
     */
    private void validateNewXxtData() throws Exception
    {
        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        IDataset vdisCodeList = new DatasetList();

        // 新ADC校讯通
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData productParam = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(GrpException.CRM_GRP_740);
        }
        productParam = IDataUtil.replaceIDataKeyDelPrefix(productParam, "NOTIN_");

        IDataset keyDataset = new DatasetList();
        Iterator<String> iterator = productParam.keySet().iterator();
        ;
        while (iterator.hasNext())
        {
            String key = iterator.next();
            // 获得选中的数据
            if ("on".equals(productParam.getString(key)) && key.startsWith("ctag"))
            {
                String num = key.substring(4);
                keyDataset.add(num);// ["1","2"]

            }

        }
        for (int i = 0; i < keyDataset.size(); i++)
        {
            String str = (String) keyDataset.get(i);
            String stuParamstr = productParam.getString("STU_PARAM_LIST" + str, "");
            String outSn = productParam.getString("OUT_SN" + str, "");
            String operType = productParam.getString("OPER_TYPE" + str, "");
            IDataset stuParamList = new DatasetList(stuParamstr);
            if (IDataUtil.isEmpty(stuParamList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_740);
            }

            // 判断是否是最后一个成员
            if (TRADE_MODIFY_TAG.DEL.getValue().equals(operType))
            {
                String roleCodeA = "1";
                String roleCodeB = "1";
                String relationTypeCode = "XT";// 注销姓名
                String ecUserid = reqData.getGrpUca().getUser().getUserId();
                IDataset relaDatas = RelaUUInfoQry.getXXTRelation(mainSn, mebUserId, roleCodeA, roleCodeB, relationTypeCode,ecUserid);
                if (relaDatas.size() == 1)
                {
                    CSAppException.apperr(GrpException.CRM_GRP_757);
                }
            }
            /*允许同一个家长号码订购不同的套餐
            // 判断是否重复订购
            if (TRADE_MODIFY_TAG.Add.getValue().equals(operType))
            {
                IDataset xxtDatas = RelaXxtInfoQry.queryMemInfoByOutSnMebUserIdGrpUserId(outSn, mainSn, grpUserId);

                if (IDataUtil.isNotEmpty(xxtDatas))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_758, outSn);
                }
            }*/
            List elementIdList = new ArrayList();
            for (int j = 0; j < stuParamList.size(); j++)
            {
                IData temp = stuParamList.getData(j);
                String elementId = temp.getString("ELEMENT_ID");

                elementIdList.add(elementId);
            }
            IData groupMap = new DataMap();
            for (int j = 0, jSize = stuParamList.size(); j < jSize; j++)
            {
                IData temp = stuParamList.getData(j);
                String tag = temp.getString("tag", "");
                String elementId = temp.getString("ELEMENT_ID", "");
                String stuKey = temp.getString("STUD_KEY");
                
                IData param = UDiscntInfoQry.getDiscntExtChaInfoByDiscntCode(elementId);
                if ("0".equals(tag))
                {
                    vdisCodeList.add(elementId);
                    // 取得资费名称
                    
                    String rsrv_str2 = param.getString("RSRV_STR2", "");
                    if (groupMap.containsKey(rsrv_str2)) {
                        CSAppException.apperr(GrpException.CRM_GRP_840, stuKey);
                    } else {
                        groupMap.put(rsrv_str2, elementId);
                    }
                }

               

                // 包年套餐必须 依赖于包月套餐
                String rsrv_str3 = param.getString("RSRV_STR3", "");

                if (!StringUtils.isBlank(rsrv_str3) && !elementIdList.contains(rsrv_str3)) {
                    CSAppException.apperr(GrpException.CRM_GRP_847, elementId, rsrv_str3);
                }
            }
        }
        // 校验所选的资费是否都分配到了学生
        IDataset vDiscntList = reqData.cd.getDiscnt();
        for (int a = 0; a < vDiscntList.size(); a++)
        {
            IData vDiscntData = vDiscntList.getData(a);
            String pModifyTag = vDiscntData.getString("MODIFY_TAG", "");
            String pElementId = vDiscntData.getString("ELEMENT_ID", "");
            if (TRADE_MODIFY_TAG.Add.getValue().equals(pModifyTag))
            {
                if (!vdisCodeList.contains(pElementId)  && !"5911".equals(pElementId))// 如果列表包含指定的元素，则返回 true。
                {
                    IData vparam = UDiscntInfoQry.getDiscntInfoByPk(pElementId);
                    CSAppException.apperr(GrpException.CRM_GRP_744, vparam.getString("DISCNT_NAME", ""));
                }

            }
        }

    }

    public void regXxtUUAndBlackWhite() throws Exception
    {
        // 新ADC校讯通
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData productParam = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        productParam = IDataUtil.replaceIDataKeyDelPrefix(productParam, "NOTIN_");

        IDataset keyDataset = new DatasetList();
        Iterator<String> iterator = productParam.keySet().iterator();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            // 获得选中的数据
            if ("on".equals(productParam.getString(key)) && key.startsWith("ctag"))
            {
                String num = key.substring(4);
                keyDataset.add(num);// ["1","2"]

            }

        }
        for (int i = 0; i < keyDataset.size(); i++)
        {
            String str = (String) keyDataset.get(i);
            String stuParamstr = productParam.getString("STU_PARAM_LIST" + str, "");
            String outSn = productParam.getString("OUT_SN" + str, "");
            String operType = productParam.getString("OPER_TYPE" + str, "");
            IDataset stuParamList = new DatasetList(stuParamstr);
            if (IDataUtil.isEmpty(stuParamList))
            {
                CSAppException.apperr(GrpException.CRM_GRP_740);
            }

            // 根据主号码和EC_USER_ID查询订购过的校讯通业务信息
            String user_id = reqData.getUca().getUserId();
            String ec_user_id = reqData.getGrpUca().getUserId();
            String mainSn = reqData.getUca().getSerialNumber();
            String bizCode = "AHI3911602";// 校讯通短信服务业务代码
            String relaTypeCode = "XT";
            if (!outSn.equals(mainSn))
            {
                IDataset outSnUUList = RelaUUInfoQry.qryUUBySnBUserIdRelatypeInfo(outSn, mainSn, user_id, relaTypeCode,ec_user_id);
                if (IDataUtil.isNotEmpty(outSnUUList))
                {
                    user_id = outSnUUList.getData(0).getString("USER_ID_A");// 异网号的USERID
                }
			}

            IDataset blackwhites = UserBlackWhiteInfoQry.getXxtBlackWhiteByUserIdBizCode2(user_id, ec_user_id, bizCode);

            if (IDataUtil.isEmpty(blackwhites))
            {
                CSAppException.apperr(GrpException.CRM_GRP_747);
            }

            // 目前只能加短信服务
            IData bwdata = blackwhites.getData(0);

            if (TRADE_MODIFY_TAG.Add.getValue().equals(operType))
            {
                regBWRelaAdd(bwdata, stuParamList, outSn);

            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(operType))
            {
                regBWRelaDel(bwdata, stuParamList, outSn);

            }
            else if (TRADE_MODIFY_TAG.MODI.getValue().equals(operType))
            {
                regBWRelaMod(bwdata, stuParamList, outSn);
            }
        }
    }

    /**
     * 新增异网号码
     *
     * @param bwData
     * @param stuParamList
     * @param outSn
     * @throws Exception
     */

    public void regBWRelaAdd(IData bwData, IDataset stuParamList, String outSn) throws Exception
    {
        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        String userIdoutSn = SeqMgr.getUserId();
        String userIdoutSnUu  = userIdoutSn;

        StringBuilder stuNameSB = new StringBuilder();
        StringBuilder stuKeySB = new StringBuilder();
        StringBuilder stuDiscodeSB = new StringBuilder();
        StringBuilder stuDisNameSB = new StringBuilder();

        IData temp = null;
        IData data = new DataMap();
        IData param = new DataMap();

        IDataset userInfoDiscnts = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(mebUserId, grpUserId);
        IDataset userDiscntsList = DataHelper.distinct(userInfoDiscnts, "DISCNT_CODE", ",");// 排除有相同资费的数据

        for (int i = 0, iSize = stuParamList.size(); i < iSize; i++)
        {
            String instIdDis = SeqMgr.getInstId(); // 新增相同资费时discnt表用

            temp = stuParamList.getData(i);
            String name = temp.getString("STUD_NAME", "");
            String elementId = temp.getString("ELEMENT_ID");

            // 取得资费配置
            param = UDiscntInfoQry.getDiscntExtChaInfoByDiscntCode(elementId);
            String rsrv_str2 = param.getString("RSRV_STR2", "");
            String elementName = param.getString("DISCNT_NAME", "");
            if (StringUtils.isBlank(name))
            {// 主要用于校验反向的数据
                CSAppException.apperr(GrpException.CRM_GRP_713, "订购的[" + elementId + "||" + elementName + "]资费没有分配给学生，业务不能办理!");
            }

            // 一个移动号码为多个异网号码付费的情况，多个异网号码有可能都订购了同一套餐，在discnt表里有对同一个套餐传多条数据，资费INST_ID不一样 资费的INST_ID= XXT表的RELA_INST_ID
            if (StringUtils.isNotBlank(rsrv_str2) && rsrv_str2.startsWith("group_"))
            { // 判断是否是包年或者是包半年
                String[] group = param.getString("RSRV_STR2", "").split("_");

                for (int a = 0, aSize = userDiscntsList.size(); a < aSize; a++)
                {
                    IData userDisInfo = userDiscntsList.getData(a);

                    if (elementId.equals(userDisInfo.getString("DISCNT_CODE", "")))
                    {
                        // 相同资费的生效时间 和 结束时间 怎么定义 ？ 生效时间都为立即生效?
                        // 结束时间有--->包月，包半年，包年。
                        String endDate = "";
                        userDisInfo.put("START_DATE", getAcceptTime());
                        if ("2".equals(group[2])) // 包年
                        {
                            endDate = SysDateMgr.getAddMonthsNowday(12, getAcceptTime());
                        }
                        else if ("3".equals(group[2])) // 包半年
                        {
                            endDate = SysDateMgr.getAddMonthsNowday(6, getAcceptTime());
                        }
                        else
                        {
                            endDate = SysDateMgr.getTheLastTime();
                        }
                        userDisInfo.put("INST_ID", instIdDis);
                        userDisInfo.put("END_DATE", endDate);
                        userDisInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 新增一条相同资费

                        super.addTradeDiscnt(userDisInfo);

                    }
                }
            }

            // 如果是新增优惠，relation_xxt表的INST_ID要与新增优惠的INST_ID相同
            IDataset pgDiscntList = reqData.cd.getDiscnt();
            for (int a = 0; a < pgDiscntList.size(); a++)
            {
                IData pDiscntData = pgDiscntList.getData(a);
                String pModifyTag = pDiscntData.getString("MODIFY_TAG", "");
                String pElementId = pDiscntData.getString("ELEMENT_ID", "");
                String pInstId = pDiscntData.getString("INST_ID", "");
                if (TRADE_MODIFY_TAG.Add.getValue().equals(pModifyTag) && elementId.equals(pElementId))
                {
                    instIdDis = pInstId; // 每个学生订购的资费 生成discnt表与relation_xxt表的关联字段
                    break;
                }
            }
            data.put("SERVICE_ID", "915001"); // 新校讯通目前只支持 校讯通成员短信服务
            data.put("RELA_INST_ID", instIdDis); // 每个学生订购的资费 生成discnt表与relation_xxt表的关联字段
            data.put("INST_ID", SeqMgr.getInstId());
            data.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());// 集团userId
            data.put("USER_ID_A", reqData.getUca().getUser().getUserId()); // 成员用户标识
            data.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber()); // 成员手机号
            data.put("SERIAL_NUMBER_B", outSn); // 代付费手机号
            data.put("ELEMENT_TYPE_CODE", "D");
            data.put("ELEMENT_ID", elementId);
            data.put("START_DATE", getAcceptTime()); // 起始时间
            data.put("END_DATE", SysDateMgr.getTheLastTime()); // 终止时间
            data.put("NAME", temp.getString("STUD_NAME"));// 学生姓名
            data.put("REMARK", "");
            data.put("RSRV_NUM1", "0");//
            data.put("RSRV_NUM2", "0");
            data.put("RSRV_NUM3", "0");
            data.put("RSRV_STR1", temp.getString("STUD_KEY"));
            data.put("RSRV_STR2", param.getString("RSRV_STR2", ""));
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

        if (outSn.equals(mainSn))// 如果异网号码是计费号码 修改原黑白名单记录
        {
            bwData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            userIdoutSn = reqData.getUca().getUserId();// 成员的USER_ID
        }
        else
        {
            bwData.put("INST_ID", SeqMgr.getInstId());
            bwData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        }
        bwData.put("USER_ID", userIdoutSn);
        bwData.put("SERIAL_NUMBER", outSn);
        bwData.put("RSRV_STR2", stuNameSB);// 徐世富,陈业仁,13976514111
        bwData.put("RSRV_STR3", stuKeySB);// stu_name1,stu_name2,servicephone
        bwData.put("RSRV_STR4", stuDiscodeSB.substring(0, stuDiscodeSB.length() - 1)); // 4302,4303
        bwData.put("RSRV_STR5", stuDisNameSB.substring(0, stuDisNameSB.length() - 1)); // 优惠名称
        bwData.put("OPER_STATE", "01");// 新增
        bwData.put("IS_NEED_PF", "1");// 1或者是空 走服务开通发指令,0：不走服务开通不发指令
        bwData.put("RSRV_TAG3", "0");// 0 正常走服务开通模式 1 ADC平台 2 行业网关

        super.addTradeBlackwhite(bwData);// blackwhite

        IData rela = new DataMap();
        rela.put("RELATION_TYPE_CODE", "XT");
        rela.put("USER_ID_A", userIdoutSnUu);
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

    }

    /**
     * 注销异网号码 删除relation_uu表 删除relation_xxt，删除DISCNT数据
     *
     * @param bwdata
     * @param stuParamList
     * @param outSn
     * @throws Exception
     */
    public void regBWRelaDel(IData bwdata, IDataset stuParamList, String outSn) throws Exception
    {
        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        String outUserId = reqData.getUca().getUserId();

        IDataset relaDatas = RelaUUInfoQry.qryXxtUUInfo(outSn, mebUserId,grpUserId);

        if (IDataUtil.isNotEmpty(relaDatas))
        {
            IData outSnRelaUU = relaDatas.getData(0);
            // 删除主号码与异网号UU关系
            outSnRelaUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            outSnRelaUU.put("END_DATE", getAcceptTime());
            super.addTradeRelation(outSnRelaUU);// 注销relation_uu表的记录

            // 删除blackwhite表
            if (!outSn.equals(mainSn))
            {
            	outUserId = outSnRelaUU.getString("USER_ID_A", "");
			}

            IDataset blackwhiteDatas = UserBlackWhiteInfoQry.getBlackWhitedataByUserIdEcuserid(outUserId, grpUserId);
            IData bw = blackwhiteDatas.getData(0);

            if (outSn.equals(mainSn))
            {
                bw.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            }
            else
            {
                bw.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }

            bw.put("END_DATE", getAcceptTime());
            bw.put("OPER_STATE", "02");// 终止黑白名单关系
            bw.put("IS_NEED_PF", "1");// 1或者是空 走服务开通发指令,0：不走服务开通不发指令
            bw.put("RSRV_TAG3", "0");// 0 正常走服务开通模式 1 ADC平台 2 行业网关

            super.addTradeBlackwhite(bw);// 删除blackwhite表

            // 注销relation_xxt表数据 和 tf_f_user_discnt
            IDataset relationxxtDatas = RelaXxtInfoQry.queryMemInfoByOutSnMebUserIdGrpUserId(outSn, mainSn, grpUserId);
            IData relaxxt = null;
            for (int i = 0; i < relationxxtDatas.size(); i++)
            {
                relaxxt = relationxxtDatas.getData(i);
                relaxxt.put("SERVICE_ID", "915001"); // 新校讯通目前只支持 校讯通成员短信服务
                relaxxt.put("END_DATE", getAcceptTime());
                relaxxt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                super.addTradeRelationXxt(relaxxt);

                // tf_f_user_discnt
                String instId = relaxxt.getString("RELA_INST_ID");
                IDataset discntDatas = UserDiscntInfoQry.quyUserDiscntByUserIdAndInstId(mebUserId, instId);

                for (int j = 0; j < discntDatas.size(); j++)
                {
                    IData discntData = discntDatas.getData(j);
                    discntData.put("END_DATE", getAcceptTime());// 结束时间
                    discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                    super.addTradeDiscnt(discntData);
                }
            }

        }
    }

    /**
     * 修改异网学生参数信息，修改学生参数信息时可以新增资费，删除资费，修改资费，修改姓名 tag 为0新增，2修改，1删除
     */
    public void regBWRelaMod(IData bwdata, IDataset tempStuParamList, String outSn) throws Exception
    {
        StringBuilder stuNameSB = new StringBuilder();
        StringBuilder stuKeySB = new StringBuilder();
        StringBuilder stuDiscodeSB = new StringBuilder();
        StringBuilder stuDisNameSB = new StringBuilder();
        IData temp = null;
        IData param = new DataMap();

        String mainSn = reqData.getUca().getSerialNumber();
        String mebUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        String outUserId = reqData.getUca().getUserId();
        IDataset stuParamList =new DatasetList();
        for (int i = 0, iSize = tempStuParamList.size(); i < iSize; i++)
        {
        	IData tempStuParamData = tempStuParamList.getData(i);
            String tempStuKey = tempStuParamData.getString("STUD_KEY");
            String tempTag =tempStuParamData.getString("tag","");

            IDataset filterDataset = DataHelper.filter(tempStuParamList,"STUD_KEY="+tempStuKey);
            if(IDataUtil.isNotEmpty(filterDataset) && filterDataset.size() >1 && tempTag.equals("1"))
            {
        		tempStuParamData.put("tag", "2_1");
        		stuParamList.add(tempStuParamData);

            }else
            {
            	stuParamList.add(tempStuParamData);
            }
        }

        for (int i = 0, iSize = stuParamList.size(); i < iSize; i++)
        {
            IData data = new DataMap();
            temp = stuParamList.getData(i);
            String instIdDis = SeqMgr.getInstId(); // 新增相同资费时discnt表用
            String name = temp.getString("STUD_NAME", "");
            String elementId = temp.getString("ELEMENT_ID");
            String stuKey = temp.getString("STUD_KEY");//stu_name3,stu_name1

            // 取得资费配置
            param = UDiscntInfoQry.getDiscntExtChaInfoByDiscntCode(elementId);

            IDataset userInfoDiscnts = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(mebUserId, grpUserId);


            // 排除有相同资费的数据
            IDataset userDiscntsList = DataHelper.distinct(userInfoDiscnts, "DISCNT_CODE", ",");

            String rsrv_str2 = param.getString("RSRV_STR2", "");
            String elementName = param.getString("DISCNT_NAME", "");

            String tag = temp.getString("tag");// 0新增，1删除

            if ("0".equals(tag))// 0新增
            {
            	IDataset userDisDataset = new DatasetList();
                if (StringUtils.isBlank(name))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "资费[" + elementId + "||" + elementName + "]没有分配给学生，业务不能办理!");
                }

                if (StringUtils.isNotBlank(rsrv_str2) && rsrv_str2.startsWith("group_"))
                {// 判断是否是包年或者是包半年
                    String[] group = param.getString("RSRV_STR2", "").split("_");
                    for (int a = 0, aSize = userDiscntsList.size(); a < aSize; a++)
                    {
                        IData userDisInfo = userDiscntsList.getData(a);
                        // 如果新增异网2 与 新增异网号1有相同资费，则TF_F_USER_Discnt表里多新增一条资费,INST_ID不一样
                        if (elementId.equals(userDisInfo.getString("DISCNT_CODE", "")))
                        {
                            // 结束时间有--->包月，包半年，包年。
                            String endDate = "";
                            if ("2".equals(group[2])) // 包年
                            {
                                endDate = SysDateMgr.getAddMonthsNowday(12, getAcceptTime());
                            }
                            else if ("3".equals(group[2])) // 包半年
                            {
                                endDate = SysDateMgr.getAddMonthsNowday(6, getAcceptTime());
                            }
                            else
                            {
                                endDate = SysDateMgr.getTheLastTime();
                            }
                            userDisInfo.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                            userDisInfo.put("INST_ID", instIdDis);
                            userDisInfo.put("END_DATE", endDate);
                            userDisInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 新增一条相同资费
                            userDisDataset.add(userDisInfo);
                        }
                    }

                    //查询这个月是否有退订过相同组的资费，如果有，则日期截止到上个月底start
                    IDataset relaXxtInfo =  RelaXxtInfoQry.qryOutSnInfobyStuKeyThisMonthEnd(mainSn, outSn, stuKey, grpUserId);
                    if (IDataUtil.isNotEmpty(relaXxtInfo))
                    {
    					String relaInstId = "";
    					for (int j = 0 ,jSize=relaXxtInfo.size(); j < jSize; j++)
    					{
    						relaInstId = relaXxtInfo.getData(j).getString("RELA_INST_ID");
    						IData tempData = new DataMap();
    						tempData.put("INST_ID", relaInstId);
    						tempData.put("USER_ID", mebUserId);
    						IDataset disDataset = UserDiscntInfoQry.queryUserDiscntByUserIdAndInstId(tempData);
    						for (int k = 0,ksize=disDataset.size(); k < ksize; k++)
    						{
    							IData disDate = disDataset.getData(k);
    							disDate.put("END_DATE", SysDateMgr.getLastMonthLastDate());// 结束时间,设置为上个月最后的时间
    							disDate.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
    							userDisDataset.add(disDate);
    						}
    					}
    				}
                    //查询这个月是否有退订过相同组的资费，如果有，则日期截止到上个月底end

                    super.addTradeDiscnt(userDisDataset);

                    // 如果产品信息页面新增的优惠，relation_xxt表的RELA_INST_ID要与新增优惠的INST_ID相同
                    String dealInstId = dealPgDiscnt(elementId);

                    if (StringUtils.isNotBlank(dealInstId))
                    {
                        instIdDis = dealInstId;
                    }
                    data.put("SERVICE_ID", "915001"); // 新校讯通目前只支持 校讯通成员短信服务
                    data.put("RELA_INST_ID", instIdDis);// relation_xxt表的RELA_INST_ID要与优惠的INST_ID相同
                    data.put("INST_ID", SeqMgr.getInstId()); // 每个学生订购的资费 生成discnt表与relation_xxt表的关联字段
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
                    data.put("RSRV_STR2", param.getString("RSRV_STR2", ""));
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

            }
            else if ("1".equals(tag))// 1删除
            {
                String ecUserId = reqData.getGrpUca().getUserId();
                IDataset xxtDataset = RelaXxtInfoQry.qryOutSnInfobySnaAndSnbAndEleId(mainSn, outSn, elementId, ecUserId);
                if (IDataUtil.isNotEmpty(xxtDataset))
                {
                    IData map = xxtDataset.getData(0);

                    String relaId = map.getString("RELA_INST_ID");// 记录的是discnt表的inst_id

                    // tf_f_user_discnt
                    IDataset discntDatas = UserDiscntInfoQry.quyUserDiscntByUserIdAndInstId(mebUserId, relaId);
                    if (IDataUtil.isNotEmpty(discntDatas))
                    {
                        IData data2 = null;
                        for (int d = 0; d < discntDatas.size(); d++)
                        {
                            data2 = discntDatas.getData(d);
                            data2.put("END_DATE", SysDateMgr.getLastDateThisMonth());// 结束时间,获取本月最后一天
                            data2.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            super.addTradeDiscnt(data2);// 删除老资费
                        }

                    }
                    map.put("SERVICE_ID", "915001"); // 新校讯通目前只支持 校讯通成员短信服务
                    map.put("END_DATE", getAcceptTime());// 结束时间
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                    super.addTradeRelationXxt(map);// 删除relationXXT记录

                }

            }
            else if ("2_1".equals(tag))// 2-1表示对学生A做了如下操作 删除原有的学生A的套餐，再为学生A新增一个套餐，这个时候删除的学生套餐标记的tag为2-1 表示要终止到上月末，而不是本月末
            {
                String ecUserId = reqData.getGrpUca().getUserId();
                IDataset xxtDataset = RelaXxtInfoQry.qryOutSnInfobySnaAndSnbAndEleId(mainSn, outSn, elementId, ecUserId);
                if (IDataUtil.isNotEmpty(xxtDataset))
                {
                    IData map = xxtDataset.getData(0);

                    String relaId = map.getString("RELA_INST_ID");// 记录的是discnt表的inst_id

                    // tf_f_user_discnt
                    IDataset discntDatas = UserDiscntInfoQry.quyUserDiscntByUserIdAndInstId(mebUserId, relaId);
                    if (IDataUtil.isNotEmpty(discntDatas))
                    {
                        IData data2 = null;
                        for (int d = 0; d < discntDatas.size(); d++)
                        {
                            data2 = discntDatas.getData(d);
                            data2.put("END_DATE", SysDateMgr.getLastMonthLastDate());// 结束时间,获取本月最后一天
                            data2.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            super.addTradeDiscnt(data2);// 删除老资费
                        }

                    }
                    map.put("SERVICE_ID", "915001"); // 新校讯通目前只支持 校讯通成员短信服务
                    map.put("END_DATE", getAcceptTime());// 结束时间
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                    super.addTradeRelationXxt(map);// 删除relationXXT记录

                }

            }
            else
            {
                // 删除的学生优惠和姓名不发服务开通，只发页面上有的学生参数
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
            }

        }

        if (StringUtils.isBlank(stuDiscodeSB))
        {
            CSAppException.apperr(GrpException.CRM_GRP_763, outSn);
        }

        stuNameSB.append(reqData.getUca().getSerialNumber());
        stuKeySB.append("servicephone");

        // 查询有异网号码与付费号码的UU关系
        IDataset relaDatas = RelaUUInfoQry.qryXxtUUInfo(outSn, mebUserId,grpUserId);
        if (IDataUtil.isEmpty(relaDatas))
        {
        	//没有UU关系也允许变更，为变更新增异网号码，新增一条UU关系；------------------
            //CSAppException.apperr(GrpException.CRM_GRP_815, outSn, mainSn);
            
            String userIdoutSn = SeqMgr.getUserId();
            String userIdoutSnUu  = userIdoutSn;
            if (outSn.equals(mainSn))// 如果异网号码是计费号码 修改原黑白名单记录
            {
            	bwdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                userIdoutSn = reqData.getUca().getUserId();// 成员的USER_ID
            }
            else
            {
            	bwdata.put("INST_ID", SeqMgr.getInstId());
            	bwdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            }
            bwdata.put("USER_ID", userIdoutSn);
            bwdata.put("SERIAL_NUMBER", outSn);
            bwdata.put("RSRV_STR2", stuNameSB);// 徐世富,陈业仁,13976514111
            bwdata.put("RSRV_STR3", stuKeySB);// stu_name1,stu_name2,servicephone
            bwdata.put("RSRV_STR4", stuDiscodeSB.substring(0, stuDiscodeSB.length() - 1)); // 4302,4303
            bwdata.put("RSRV_STR5", stuDisNameSB.substring(0, stuDisNameSB.length() - 1)); // 优惠名称
            bwdata.put("OPER_STATE", "01");// 新增
            bwdata.put("IS_NEED_PF", "1");// 1或者是空 走服务开通发指令,0：不走服务开通不发指令
            bwdata.put("RSRV_TAG3", "0");// 0 正常走服务开通模式 1 ADC平台 2 行业网关

            super.addTradeBlackwhite(bwdata);// blackwhite

            IData rela = new DataMap();
            rela.put("RELATION_TYPE_CODE", "XT");
            rela.put("USER_ID_A", userIdoutSnUu);
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
            return;
            //---------------------------新增UU和BlackWhite结束,不再执行下面的代码-----------
        }
        IData outSnRelaUU = relaDatas.getData(0);
        if (!outSn.equals(mainSn))
        {
          outUserId = outSnRelaUU.getString("USER_ID_A", "");
        }

        // 查询有异网号码的BlackWhite数据
        IDataset blackwhiteSet = UserBlackWhiteInfoQry.getXxtBlackWhite(outUserId, grpUserId, outSn);
        if (IDataUtil.isEmpty(blackwhiteSet))
        {
            CSAppException.apperr(GrpException.CRM_GRP_816, outSn, mainSn);
        }

        IData outSnBwData = blackwhiteSet.getData(0);

        // 处理BlackWhite
        outSnBwData.put("RSRV_STR2", stuNameSB);// 徐aa,13976514111
        outSnBwData.put("RSRV_STR3", stuKeySB);// stu_name1,servicephone
        outSnBwData.put("RSRV_STR4", stuDiscodeSB.substring(0, stuDiscodeSB.length() - 1)); // 4302,4303
        outSnBwData.put("RSRV_STR5", stuDisNameSB.substring(0, stuDisNameSB.length() - 1)); // 优惠名称
        outSnBwData.put("OPER_STATE", "08");// 变更
        outSnBwData.put("IS_NEED_PF", "1");// 1或者是空 走服务开通发指令,0：不走服务开通不发指令
        outSnBwData.put("RSRV_TAG3", "1");// 0 正常走服务开通模式 1 ADC平台 2 行业网关 变更只发平台
        outSnBwData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeBlackwhite(outSnBwData);// 修改blackwhite
    }

    /**
     * 由于业务复杂前面页面做修改功能，只有新增和删除 else if ("2".equals(tag))//2修改， { //如果修改的是优惠，是先删除老数据，新增新数据
     * //如果修改的是学生姓名只修改relation_xx表的学生姓名字段 String instIdDisnew = SeqMgr.getInstId();// String stuKey =
     * temp.getString("STUD_KEY"); String ecUserId = reqData.getGrpUca().getUserId(); //查询同一集团 同一个付费号下某异号 学生X的数据
     * IDataset xxtDataset = RelaXxtInfoQry.qryOutSnInfobyStuKey(mainSn, outSn, stuKey,ecUserId); if
     * (IDataUtil.isNotEmpty(xxtDataset)) { IData map = xxtDataset.getData(0); String relaId =
     * map.getString("INST_ID");//rsrv_num1记录的是discnt表的inst_id String elementIdold = map.getString("ELEMENT_ID"); String
     * stu_name_old = map.getString("NAME"); //如果只修改学生姓名则只修改relation_xx表的学生姓名字段 if
     * (!stu_name_old.equals(temp.getString("STUD_NAME"))&&elementIdold.equals(elementId)) { map.put("NAME",
     * temp.getString("STUD_NAME")); map.put("RSRV_STR3", param.getString("DISCNT_NAME", ""));//资费名称
     * map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); super.addTradeRelationXxt(map); } else {
     * //如果修改的是优惠，是先删除老数据，新增新数据 //根据学生X的inst_id对应的优惠， tf_f_user_discnt IDataset discntDatas =
     * UserDiscntInfoQry.quyUserDiscntByUserIdAndInstId(mebUserId, relaId); if (IDataUtil.isNotEmpty(discntDatas)) {
     * //删除老资费 IData discntData = discntDatas.getData(0); discntData.put("END_DATE",
     * SysDateMgr.getLastMonthLastDate());// 结束时间，设置为上个月最后一天 discntData.put("MODIFY_TAG",
     * TRADE_MODIFY_TAG.DEL.getValue()); super.addTradeDiscnt(discntData); //新增新增资费 if
     * (StringUtils.isNotBlank(rsrv_str2) && rsrv_str2.startsWith("group_")) { String[] group =
     * param.getString("RSRV_STR2", "").split("_"); for (int a = 0 ,aSize = userDiscntsList.size() ; a < aSize; a++) {
     * IData userDisInfo = userDiscntsList.getData(a); if
     * (param.getString("DISCNT_CODE").equals(userDisInfo.getString("DISCNT_CODE", ""))) { //相同资费的生效时间 和 结束时间 怎么定义 ？
     * 生效时间都为立即生效 //结束时间有--->包月，包半年，包年。 String endDate = ""; userDisInfo.put("START_DATE",getAcceptTime()); if
     * ("2".equals(group[2])) //包年 { endDate = SysDateMgr.getAddMonthsNowday(12, getAcceptTime()); } else if
     * ("3".equals(group[2])) //包半年 { endDate = SysDateMgr.getAddMonthsNowday(6, getAcceptTime()); } else { endDate =
     * SysDateMgr.getTheLastTime(); } userDisInfo.put("INST_ID", instIdDisnew); userDisInfo.put("END_DATE", endDate);
     * userDisInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());//新增一条相同资费 super.addTradeDiscnt(userDisInfo); } }
     * } } //删除一条老的xxt数据 map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); map.put("END_DATE", getAcceptTime());
     * super.addTradeRelationXxt(map);//删除一条老的xxt //新增一条新的xxt数据 //如果是阁信息页面新增的优惠，relation_xxt表的INST_ID要与新增优惠的INST_ID相同
     * String dealInstId = dealPgDiscnt(elementId); if (StringUtils.isNotBlank(dealInstId)) { instIdDis=dealInstId; }
     * map.put("ELEMENT_ID", elementId); map.put("NAME", temp.getString("STUD_NAME"));// 学生姓名 map.put("INST_ID",
     * instIdDisnew);//每个学生订购的资费 生成discnt表与relation_xxt表的关联字段 map.put("RSRV_STR2", param.getString("RSRV_STR2", ""));
     * map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());//修改 map.put("END_DATE", SysDateMgr.getTheLastTime()); //
     * 终止时间 super.addTradeRelationXxt(map);//新增relationXXT } } }
     **/

    /**
     * 如果是产品信息页面新增优惠，relation_xxt表的INST_ID要与新增优惠的INST_ID相同
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
        	String modifyTag =  data.getString("MODIFY_TAG","0");
        	for (int j = 0; j < discntConfig.size(); j++) {
        		IData conData = discntConfig.getData(j);
				String disConStr = conData.getString("PARAM_CODE","");
				if (disTraStr.equals(disConStr)) {		//取得配置捆绑套餐;
					if ("2".equals(modifyTag))  return;//变更优惠不捆绑;
					System.out.println("chenhh==当前办理的优惠"+disConStr);
					System.out.println("chenhh==取得需要捆绑的优惠"+conData.getString("PARA_CODE1","0"));
					//增加配置捆绑套餐
					System.out.println("chenhh==原优惠"+data);
					IData newData = getnewData(data);
					System.out.println("chenhh==转换后的Data"+newData);
					newData.put("INST_ID", SeqMgr.getInstId());
					newData.put("DISCNT_CODE", conData.getString("PARA_CODE1","0"));
					
					//捆绑套餐月底结束
                    if ("1".equals(modifyTag)) {
                    	newData.put("END_DATE", SysDateMgr.getLastDateThisMonth());// 捆绑套餐月底结束
                    	IDataset userDis = UserDiscntInfoQry.getUserDiscntByDiscntCode(newData.getString("USER_ID"),
                    			newData.getString("USER_ID_A"),newData.getString("DISCNT_CODE"),Route.CONN_CRM_CG);
                    	//注销找到指定的INSTID
                    	if (!IDataUtil.isEmpty(userDis))  {
                    		newData.put("INST_ID", userDis.getData(0).getString("INST_ID",SeqMgr.getInstId())); 
                    	}else {
                    		System.out.println("chh==没有找到绑定优惠，取消退订捆绑优惠。");
							continue;
						}
    				}
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
