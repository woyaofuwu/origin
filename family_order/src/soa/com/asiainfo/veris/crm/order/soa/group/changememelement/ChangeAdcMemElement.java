
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeAdcMemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class ChangeAdcMemElement extends ChangeMemElement
{

    protected ChangeAdcMemberReqData reqData = null;

    public ChangeAdcMemElement()
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
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        this.regSetAttr();

        this.regBlackWhiteAndMemPlatsvc();
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

        String productId = reqData.getGrpUca().getProductId();
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateMember);

        // 得到是否向黑白名单台帐表中拼套餐信息
        String synDiscntFlag = ctrlInfo.getAttrValue("SynDiscntFlag");
        reqData.setSynDiscntFlag(synDiscntFlag);
    }

    /**
     * 学护卡特殊处理亲情号码,modify by xushifu 20141224,学护卡优惠变更的时候要求保留之前的亲情号码
     *
     * @throws Exception
     */
    public void regSetAttr() throws Exception
    {
        String productId = reqData.getGrpUca().getProductId();
        String grpUserId = reqData.getGrpUca().getUser().getUserId();
        IDataset dctDataset = reqData.cd.getDiscnt();// 前台页面办理的资费
        IDataset dctDatasetAdd = DataHelper.filter(dctDataset, "MODIFY_TAG=0");
        IDataset dctDatasetDel = DataHelper.filter(dctDataset, "MODIFY_TAG=1");

        // 学护卡反向加亲情号码
        String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData productParamMap = reqData.cd.getProductParamMap(mebBaseProductId);
        IDataset famSnList = new DatasetList();
        IDataset dataset = new DatasetList();
        if (productParamMap != null && productParamMap.getString("NOTIN_FAM_SN_PARAM_LIST0") != null) {
            famSnList = new DatasetList(productParamMap.getString("NOTIN_FAM_SN_PARAM_LIST0"));
        }

        if ("10005744".equals(productId))
        {
            if (IDataUtil.isNotEmpty(dctDatasetAdd)) {
                for (int j = 0, jSize = dctDatasetAdd.size(); j < jSize; j++)
                {

                    String instId = dctDatasetDel.getData(0).getString("INST_ID");
                    IDataset userParamList = UserAttrInfoQry.getUserAttrByInstID( reqData.getUca().getUserId(),instId);

                    // 如果亲情号码发生变更，先注销，后新增
                    if (famSnList.size() > 0) {
                        for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
                        {
                            IData userParam = userParamList.getData(i);
                            userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            userParam.put("END_DATE", getAcceptTime());
                            dataset.add(userParam);
                        }

                        IData famSnMap = null;
                        for (int i=0; i<famSnList.size(); i++) {
                            famSnMap = famSnList.getData(i);
                            if (!"".equals(famSnMap.getString("FAMNUM", ""))) {
                                IData map = new DataMap();
                                map.put("INST_TYPE", "D");
                                map.put("RELA_INST_ID", dctDatasetAdd.getData(j).getString("INST_ID"));
                                map.put("ATTR_CODE", i+1);
                                map.put("ATTR_VALUE", famSnMap.getString("FAMNUM", "-1"));
                                map.put("START_DATE", SysDateMgr.addSecond(getAcceptTime(), 1));
                                map.put("END_DATE", SysDateMgr.getTheLastTime());
                                map.put("INST_ID", SeqMgr.getInstId());
                                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                dataset.add(map);
                            }
                        }

                        // 如果不足6位，侧补足
                        for (int i = famSnList.size() + 1; i < 6; i++)
                        {
                            IData map = new DataMap();
                            map.put("INST_TYPE", "D");
                            map.put("RELA_INST_ID", dctDatasetAdd.getData(j).getString("INST_ID"));
                            map.put("ATTR_CODE", i);
                            map.put("ATTR_VALUE", "-1");
                            map.put("START_DATE",SysDateMgr.addSecond(getAcceptTime(), 1));
                            map.put("END_DATE", SysDateMgr.getTheLastTime());
                            map.put("INST_ID", SeqMgr.getInstId());
                            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            dataset.add(map);

                        }
                    } else {
                        for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
                        {
                            IData userParam = userParamList.getData(i);
                            userParam.put("RELA_INST_ID",  dctDatasetAdd.getData(j).getString("INST_ID"));
                            userParam.put("START_DATE", dctDatasetAdd.getData(j).getString("START_DATE"));
                            userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            userParam.put("END_DATE", SysDateMgr.getTheLastTime());
                            userParam.put("INST_ID", SeqMgr.getInstId());
                            dataset.add(userParam);
                        }
                    }
                }
            } else {
                IDataset discntset = UserDiscntInfoQry.queryDiscntsByUserIdProdIdPkgId(reqData.getUca().getUserId(), grpUserId, "574401", "57440102");
                if (IDataUtil.isNotEmpty(discntset))
                {
                    IData discnt = (IData) discntset.get(0);

                    // 如果亲情号码发生变更，先注销，后新增
                    if (famSnList.size() > 0) {
                        String instId = discnt.getString("INST_ID");
                        IDataset userParamList = UserAttrInfoQry.getUserAttrByInstID( reqData.getUca().getUserId(),instId);
                        for (int i = 0, iSize = userParamList.size(); i < iSize; i++)
                        {
                            IData userParam = userParamList.getData(i);
                            userParam.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            userParam.put("END_DATE", getAcceptTime());
                            dataset.add(userParam);
                        }

                        IData famSnMap = null;
                        for (int i=0; i<famSnList.size(); i++) {
                            famSnMap = famSnList.getData(i);
                            if (!"".equals(famSnMap.getString("FAMNUM", ""))) {
                                IData map = new DataMap();
                                map.put("INST_TYPE", "D");
                                map.put("RELA_INST_ID", discnt.getString("INST_ID"));
                                map.put("ATTR_CODE", i+1);
                                map.put("ATTR_VALUE", famSnMap.getString("FAMNUM", "-1"));
                                map.put("START_DATE", SysDateMgr.addSecond(getAcceptTime(), 1));
                                map.put("END_DATE", SysDateMgr.getTheLastTime());
                                map.put("INST_ID", SeqMgr.getInstId());
                                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                                dataset.add(map);
                            }
                        }

                        // 如果不足6位，侧补足
                        for (int i = famSnList.size() + 1; i < 6; i++)
                        {
                            IData map = new DataMap();
                            map.put("INST_TYPE", "D");
                            map.put("RELA_INST_ID", discnt.getString("INST_ID"));
                            map.put("ATTR_CODE", i);
                            map.put("ATTR_VALUE", "-1");
                            map.put("START_DATE", SysDateMgr.addSecond(getAcceptTime(), 1));
                            map.put("END_DATE", SysDateMgr.getTheLastTime());
                            map.put("INST_ID", SeqMgr.getInstId());
                            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                            dataset.add(map);

                        }
                    }
                }
            }

            super.addTradeAttr(dataset);

        }

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
        IDataset distincnt = reqData.cd.getDiscnt();
        String grpUserId = reqData.getGrpUca().getUser().getUserId();
        String mebUserId = reqData.getUca().getUser().getUserId();

        if (IDataUtil.isEmpty(specialServiceset))
        {
            specialServiceset = new DatasetList();

            if (IDataUtil.isNotEmpty(distincnt) && "true".equals(reqData.getSynDiscntFlag()))// 涉及产品 adc移动oa,企业邮箱，学护卡
            // 只变更了又会也要向adc平台同步
            {
                // 取原来记录的黑白名单信息
                IDataset blackWhiteoldset = UserBlackWhiteInfoQry.getBlackWhitedataByUserIdEcuserid(mebUserId, grpUserId);
                
                for (int i = 0, isize = blackWhiteoldset.size(); i < isize; i++)
                {
                    IData platSvcdata = blackWhiteoldset.getData(i);
                    IDataset tempLists = AttrBizInfoQry.getBizAttrByDynamic(platSvcdata.getString("SERVICE_ID"), "S", platSvcdata.getString("SERVICE_ID"), "SvcBindDiscnt", null);
                    if(IDataUtil.isEmpty(tempLists) && "10009805".equals(reqData.getGrpUca().getProductId()))
                    {
                        continue;
                    }
                    if (!"P".equals(platSvcdata.getString("PLAT_SYNC_STATE", "")))
                    {
                        platSvcdata.put("OPER_STATE", "08");// 变更
                        platSvcdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        IDataset serparaminfoset = new DatasetList();
                        IData serparam = new DataMap();
                        serparam.put("PLATSVC", platSvcdata);
                        serparam.put("INST_ID", platSvcdata.getString("INST_ID", ""));
                        serparam.put("ID", platSvcdata.getString("SERVICE_ID", ""));
                        serparam.put("CANCLE_FLAG", "false");
                        serparam.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        serparam.put("END_DATE", platSvcdata.getString("END_DATE", ""));
                        IData paramverify = new DataMap();
                        serparaminfoset.add(0, paramverify);
                        serparaminfoset.add(1, serparam);
                        specialServiceset.add(serparaminfoset);
                    }
                }

            }
        }
        else  if(specialServiceset.size()==1 && distincnt.size()==2 && "10009805".equals(reqData.getGrpUca().getProductId()))
        {
        	 IDataset blackWhiteoldset = UserBlackWhiteInfoQry.getBlackWhitedataByUserIdEcuserid(mebUserId, grpUserId);
             
             for (int i = 0, isize = blackWhiteoldset.size(); i < isize; i++)
             {
            	 IData platSvcdata = blackWhiteoldset.getData(i);
            	 if(!"980501".equals(platSvcdata.getString("SERVICE_ID")))
            	 {
            		 continue;
            		 
            	 }
            	 if (!"P".equals(platSvcdata.getString("PLAT_SYNC_STATE", "")))
                 {
                     platSvcdata.put("OPER_STATE", "08");// 变更
                     platSvcdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                     IDataset serparaminfoset = new DatasetList();
                     IData serparam = new DataMap();
                     serparam.put("PLATSVC", platSvcdata);
                     serparam.put("INST_ID", platSvcdata.getString("INST_ID", ""));
                     serparam.put("ID", platSvcdata.getString("SERVICE_ID", ""));
                     serparam.put("CANCLE_FLAG", "false");
                     serparam.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                     serparam.put("END_DATE", platSvcdata.getString("END_DATE", ""));
                     IData paramverify = new DataMap();
                     serparaminfoset.add(0, paramverify);
                     serparaminfoset.add(1, serparam);
                     specialServiceset.add(serparaminfoset);
                 }
            	 
             }
        	
        	
        }

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
            // 当ADC服务暂停、恢复时处理服务状态表
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
            data.put("BIZ_IN_CODE_A", platsvc.getString("SI_BASE_IN_CODE_A"));
            data.put("SERVICE_ID", platsvcdata.getString("SERVICE_ID", ""));
            data.put("BILLING_TYPE", platsvc.getString("BILLING_TYPE"));

            data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// 1或者是空：走服务开通发指令,0：不走服务开通不发指令
            data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3", "0"));// 0 默认值没意义，1 只ADC平台，2 只行业网关，
            // 以后有只发一个平台的产品时，可与 in_mode_code绑定用,服开暂时没取RSRV_TAG3字段;现在用的主表in_mode_code字段,字段值为P 只向网关发送数据 值为G 只向ADC发送数据
            // 其他值 adc平台和网关都发送.

            data.put("OPER_STATE", "01");// 3.0规范 新增
            /**
             * 2.5规范 if (bizattr.equals("0"))// 订购关系 { data.put("OPER_STATE", "2");// 订购 } else // 黑白名单 {
             * data.put("OPER_STATE", "0");// 加入 }
             **/

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
            else
            {
                // 资料不对， 报错
                CSAppException.apperr(ProductException.CRM_PRODUCT_522, "根据成员用户编码【"+user_id+"】集团客户编码【"+group_id+"】服务编码【"+service_id+"】查询【TF_F_USER_BLACKWHITE】表资料数据为空!请联系管理员检查相关资料");
            }
            // 删除服务
            if (TRADE_MODIFY_TAG.DEL.getValue().equals(state))
            {
                data.put("OPER_STATE", "02");// 3.0规范 02终止 2013-01-19 J2EE项目修改，与接口规范保持一致。
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                data.put("END_DATE", enddate);
                data.put("EXPECT_TIME", enddate);

                /**
                 * 2.5规范 if (bizattr.equals("0"))// 订购关系 { data.put("OPER_STATE", "3");// 退定 } else // 黑白名单 {
                 * data.put("OPER_STATE", "1");// 退出 }
                 **/
            }
            else
            {
                data.put("OPER_STATE", operState);
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                data.put("PLAT_SYNC_STATE", "04".equals(operState) ? "P" : "1");
            }

            data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空：//
            // 走服务开通发指令,0：不走服务开通不发指令
            data.put("RSRV_TAG3", platsvcdata.getString("RSRV_TAG3", "0"));// J2EE修改 0 默认值没意义，1 只ADC平台，2 只行业网关，
        }

        if ("9188".equals(reqData.getUca().getProductId()))
        {// 如果为商信通产品,则用户期望生效时间 加一天
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
                String strModifyTag = dctDataset.getData(i).getString("MODIFY_TAG", "-1");
               
                if (IDataUtil.isNotEmpty(tempLists))
                {
                    String strAttrValue = tempLists.getData(0).getString("ATTR_VALUE", "-1");
                    if (strElementId.equals(strAttrValue))
                    {
                        if ("1".equals(state) || "0".equals(state) || ("2".equals(state) && "0".equals(strModifyTag)))//如果服务是新增或者删除，一定更新这两个字段，如果是变更，则需要判断资费是新增时，更新这两个
                        {
                            strDntList = strElementId;
                            strDntName = UDiscntInfoQry.getDiscntNameByDiscntCode(strDntList);
                            break;
                        }
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
        if ("04".equals(operState) || "05".equals(operState) || "08".equals(operState))
            return;
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
}
