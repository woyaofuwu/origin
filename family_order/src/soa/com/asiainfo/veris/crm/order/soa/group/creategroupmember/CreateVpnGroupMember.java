
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CreateVpnGroupMember extends CreateGroupMember
{
    private CreateVpnGroupMemberReqData reqData = null;

    private IData paramData = new DataMap();

    public CreateVpnGroupMember()
    {

    }

    /**
     * 生成登记信息
     * 
     * @author liaoyi
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramData = getParamData(); // 获取参数
        String shortCode = paramData.getString("SHORT_CODE", "");
        // if (StringUtils.isBlank(shortCode))
        // {
        // CSAppException.apperr(VpmnUserException.VPMN_USER_186, "VPMN成员新增，短号不能为空！");
        // }
        String mebUserId = reqData.getUca().getUserId();
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        String lastTimeThisAcctday = DiversifyAcctUtil.getLastTimeThisAcctday(mebUserId, "");// 账期最后一天
        String nextMonthFirstTime = DiversifyAcctUtil.getFirstTimeNextAcct(mebUserId); // 下账期第一时间

        // 1. 如新增了358套餐，且本账期有删除358套餐的记录，则将删除的延长至本账期末，新增的下账期生效 start
        String strSpecDiscnt = "1285,1286,1391,";
        IDataset chooseDiscnts = reqData.cd.getDiscnt();
        if (IDataUtil.isNotEmpty(chooseDiscnts))
        {
            for (int i = 0; i < chooseDiscnts.size(); i++)
            {
                IData addDiscnt358 = chooseDiscnts.getData(i);
                String elementId = addDiscnt358.getString("ELEMENT_ID", "");
                String elementType = addDiscnt358.getString("ELEMENT_TYPE_CODE", "");
                if (strSpecDiscnt.indexOf(elementId + ",") >= 0 && elementType.equals("D"))// 如果是新增的358套餐
                {
                    // 查出用户本账期内退订的358套餐(del358),有则走下列逻辑：
                    // 1.如果 del358不是新增的add358优惠，则新增del358优惠（开始时间为立即生效<预约的话为下月第一时间>，结束时间为本月最后时间），同时add358改为下账期第一天生效；
                    // 2.如果 del358是新增的add358优惠，则改add358优惠开始时间为立即生效<预约的话为下月第一时间>
                    IDataset delUsrDiscnt358 = UserDiscntInfoQry.qryDelUsrDiscnt358(mebUserId, DiversifyAcctUtil.getFirstDayNextAcct(mebUserId), CSBizBean.getTradeEparchyCode());
                    if (IDataUtil.isNotEmpty(delUsrDiscnt358))
                    {
                        IData delDis358Info = delUsrDiscnt358.getData(0);
                        String endDate = delDis358Info.getString("END_DATE", "");
                        if (endDate.compareTo(lastTimeThisAcctday) < 0 && IDataUtil.isNotEmpty(addDiscnt358))
                        {
                            if (delDis358Info.getString("DISCNT_CODE", "").equals(addDiscnt358.getString("ELEMENT_ID", "")))
                            {
                                addDiscnt358.put("START_DATE", "true".equals(diversifyBooking) ? nextMonthFirstTime : this.getAcceptTime());
                            }
                            // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                            addDiscnt358.put("DIVERSIFY_ACCT_TAG", "1");
                        }
                    }
                    break;
                }
            }
        }
        // 1. 如新增了358套餐，且本账期有删除358套餐的记录，则将删除的延长至本账期末，新增的下账期生效 end
        // 2.判断
        boolean hasOutProDiscnt = false;
        if (IDataUtil.isNotEmpty(chooseDiscnts))
        {
            for (int i = 0; i < chooseDiscnts.size(); i++)
            {
                IData addDiscnt = chooseDiscnts.getData(i);
                String elementType = addDiscnt.getString("ELEMENT_TYPE_CODE", "");
                String packageId = addDiscnt.getString("PACKAGE_ID", "");
                if ("80000103".equals(packageId) && elementType.equals("D"))// 如果是新增的跨省V网套餐
                {
                    hasOutProDiscnt = true;
                    break;
                }
            }
        }
        IDataset ds = UserVpnInfoQry.qryUserVpnByUserId(reqData.getGrpUca().getUser().getUserId());
        if (IDataUtil.isNotEmpty(ds))
        {
            IData data = ds.getData(0);
            if ("2".equals(data.getString("VPN_SCARE_CODE")) && !hasOutProDiscnt)
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_221);
            }
        }
        // 2.判断end
        // 2.联络员特殊优惠 如果角色为2-集团管理员,则成员新增配置的该成员产品优惠 start
        if ("2".equals(reqData.getMemRoleB()))
        {
            IDataset discnts = reqData.cd.getDiscnt();
            IDataset datas = ParamInfoQry.getCommparaByCode("CSM", "6018", "", reqData.getUca().getUserEparchyCode());
            if (IDataUtil.isNotEmpty(datas))
            {
                for (int i = 0; i < datas.size(); i++)
                {
                    IData discnt = new DataMap();
                    discnt.put("PRODUCT_ID", baseMemProduct);
                    discnt.put("PACKAGE_ID", "-1");
                    discnt.put("ELEMENT_ID", datas.getData(i).getString("PARAM_CODE"));

                    discnt.put("INST_ID", SeqMgr.getInstId());
                    discnt.put("START_DATE", "true".equals(diversifyBooking) ? nextMonthFirstTime : this.getAcceptTime());
                    discnt.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                    discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                    discnt.put("DIVERSIFY_ACCT_TAG", "1");
                    discnts.add(discnt);
                }
            }
        }
        // 2.联络员特殊优惠 2-集团管理员 end

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        String serialNumber = reqData.getGrpUca().getSerialNumber();

        // 海南处理,成员代付关系表
        infoRegSpecInfo();
        if ("V0HN001010".equals(serialNumber) || "V0SJ004001".equals(serialNumber))
        {
            // 如果是这2个集团，不再调用公用的插付费关系表
            // 网外号码
            if (reqData.isOutNet())
            {
                // 网外号码客户核心信息
                actTradeCustomerForOutNet();

                // 网外号码个人客户信息
                actTradeCustPersonForOutNet();

                // 网外号码用户信息
                actTradeUserForOutNet();

                // 网外号码付费关系信息
                actTradeOutNetPayRelation();

                // 网外号码主产品信息
                actTradeutNetPrdInfo();

            }

            // 产品子表
            actTradePrdAndPrdParam();

            // 服务状态表
            super.actTradeSvcState();

            // 关系表
            actTradeRelationUU();

            // 付费关系表
            // actTradePayRela();
        }
        else
        {
            super.actTradeSub();
        }

        // VPN_MEB表
        infoRegDataVPMNVpnMeb();

        //start add by wangyf6 at 20191111
        infoRegOtherData();
        //end add by wangyf6 at 20191111
        
        /*
         * 屏蔽这段内容，移植到 modTradeData 方法中，防止二次短信执行时报重复插入数据的错误
        String shortCode = paramData.getString("SHORT_CODE", "");
        if (StringUtils.isNotBlank(shortCode))
        {
            // shortCodeValidate(shortCode); //第二次校验，觉得没必要，先屏蔽
            insertTemporaryShortCode();
        }
        */
    }

    /**
     * 获取VPMN个性化参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamData() throws Exception
    {
        // VPMN个性化参数
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        return paramData;
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateVpnGroupMemberReqData();
    }

    /**
     * 处理台帐VPN子表的数据
     * 
     * @param Datas
     *            VPN参数
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataVPMNVpnMeb() throws Exception
    {
        // VPN数据
        IDataset dataset = new DatasetList();
        IData vpnData = super.infoRegDataVpnMeb();
        vpnData.put("USER_ID", reqData.getUca().getUserId());
        vpnData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        vpnData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        vpnData.put("MEMBER_KIND", "1");// 关系--普通客户
        vpnData.put("SHORT_CODE", paramData.getString("SHORT_CODE", ""));// 成员短号码;
        vpnData.put("FUNC_TLAGS", TagInfoQry.getSysTagInfo("CS_INF_DEFTAGSET_VPN1", "TAG_INFO", "444444442211111110001000000000000000", reqData.getUca().getUserEparchyCode()));
        vpnData.put("OPEN_DATE", this.getAcceptTime());
        vpnData.put("REMOVE_TAG", "0");
        vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        dataset.add(vpnData);
        addTradeVpnMeb(dataset);
    }

    /**
     * 海南处理
     * 
     * @author sht
     * @throws Exception
     */
    public void infoRegSpecInfo() throws Exception
    {
        String serialNumber = reqData.getGrpUca().getSerialNumber();
        String memUserId = reqData.getUca().getUserId();
        String nextMonthFirstTime = DiversifyAcctUtil.getFirstTimeNextAcct(memUserId);
        // //V0SJ001835 V0HN001010
        if ("V0HN001010".equals(serialNumber) || "V0SJ004001".equals(serialNumber))
        {
            IData dataspplan = new DataMap();
            dataspplan.put("USER_ID_A", reqData.getGrpUca().getUserId());// payplan表中user_id_a为付费计划作用对象
            dataspplan.put("USER_ID", memUserId);
            dataspplan.put("PLAN_TYPE_CODE", "P");
            dataspplan.put("PLAN_ID", SeqMgr.getPlanId());
            dataspplan.put("PLAN_NAME", "个人付费"); // 付费计划名称
            dataspplan.put("PLAN_DESC", "个人付费"); // 付费计划描述
            dataspplan.put("DEFAULT_TAG", "0");
            dataspplan.put("RULE_ID", "0");
            dataspplan.put("START_DATE", getAcceptTime()); // 生效时间
            dataspplan.put("END_DATE", SysDateMgr.getTheLastTime()); // 失效时间
            dataspplan.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            super.addTradeUserPayplan(dataspplan); // 付费计划子表
            IDataset discnts = reqData.cd.getDiscnt();
            String discnt_code = ""; // VPMN成员新增只能选择80000102集团VPMN成员产品优惠包下的一个套餐
            if (IDataUtil.isNotEmpty(discnts))
            {
                for (int i = 0; i < discnts.size(); i++)
                {
                    IData disinfo = discnts.getData(i);
                    if ("80000102".equals(disinfo.getString("PACKAGE_ID")))
                    {
                        discnt_code = disinfo.getString("ELEMENT_ID");
                        break;
                    }
                }
            }

            if ("270".equals(discnt_code)) // 270 公司员工套餐(VPMN JTZ)
            {
                // addbylixiuyu@20110919用户要求如果有非移动员工代付(RSRV_VALUE_CODE='40')的员工加入移动集团，则截止非移动员工代付(RSRV_VALUE_CODE='40')数据
                IDataset otherSet = UserOtherInfoQry.getUserOther(memUserId, "40");
                if (IDataUtil.isNotEmpty(otherSet))
                {
                    IData otherdata = otherSet.getData(0);
                    otherdata.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    otherdata.put("END_DATE", this.getAcceptTime());
                    otherdata.put("REMARK", "新增移动集团成员截止非移动员工代付");

                    super.addTradeOther(otherdata);
                }

                IData othe = new DataMap();
                othe.put("RSRV_VALUE_CODE", "30");
                othe.put("RSRV_VALUE", "50");
                othe.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                othe.put("START_DATE", "true".equals(diversifyBooking) ? nextMonthFirstTime : this.getAcceptTime());
                othe.put("END_DATE", SysDateMgr.getTheLastTime());
                othe.put("USER_ID", memUserId);

                super.addTradeOther(othe);
            }
        }
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateVpnGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        IDataset productParamInfos = map.getDataset("PRODUCT_PARAM_INFO");
        super.makReqData(map);
        reqData.setNeedSms(map.getBoolean("IF_SMS", true)); // 是否发短信
        
        //记录PRODUCT_PARAM_INFO,父类中会去该数据，在GrpModuleData.java中获取后，会将该值删除，所以此处需要重新设置
        map.put("PRODUCT_PARAM_INFO", productParamInfos);
        //add by chenzg@20180314 REQ201803020016关于优化集团V网、集团彩铃的集团成员级业务二次确认短信的需求
        this.paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            if ("1".equals(paramData.getString("TWOCHECK_SMS_FLAG", "")))
            {
                map.put("PAGE_SELECTED_TC", "true");	//页面上选择了下发二次确认短信选项
            }
        }
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR4", reqData.getGrpUca().getBrandCode()); // 品牌编码
        data.put("RSRV_STR6", reqData.getGrpUca().getCustId()); // 集团CUST_ID
        data.put("RSRV_STR7", "，短号是" + paramData.getString("SHORT_CODE", "")); // 成员短号码
        data.put("RSRV_STR10", paramData.getString("OUT_PROV_DISCNT", "")); // 跨省集团优惠(取决于是否有“添加跨省VPN集团”功能点)

        // 如果加入集团复选框选择了，则把标志位集插入tf_b_trade和tf_b_order,PROCESS_TAG_SET=1000000000000000000000000000000000000000
        if (IDataUtil.isNotEmpty(paramData))
        {
            if ("1".equals(paramData.getString("JOIN_IN", "")))
            {
                // 替换第一位
                setProcessTag(1, "1");
            }
        }
    }

    //是全网彩印新增
    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
    	super.setTradeSvc(map);
    	
    	if (IDataUtil.isNotEmpty(map)){//全网彩印要使用
    	    if("860".equals(map.getString("ELEMENT_ID","")) 
    	            && "S".equals(map.getString("ELEMENT_TYPE_CODE",""))){
    	        //校验集团是否是全国V网
    	        String vwFlagStr = "0";
    	        IDataset vpnInfo = UserVpnInfoQry.getVpnInfoByUser(reqData.getGrpUca().getUserId());
    	        if (null != vpnInfo && vpnInfo.size() > 0){
    	            int index = vpnInfo.size();
    	            for(int i = 0 ; i < index ; i++){
    	                IData vpn = vpnInfo.getData(i);
    	                if ("4".equals(vpn.getString("VPN_SCARE_CODE")) 
    	                        || "2".equals(vpn.getString("VPN_SCARE_CODE"))){
    	                    vwFlagStr = "1";
    	                    break;
    	                }
    	            }
    	        }

    	        IData inparams = new DataMap();
    	        if ("0".equals(vwFlagStr)){
    	            inparams.put("USER_ID", reqData.getGrpUca().getUserId());
    	            inparams.put("SERVICE_ID", "801");
    	            IDataset userPlatSvc = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USER_SVC_ID", inparams);
    	            if (null != userPlatSvc && !userPlatSvc.isEmpty()){
    	                vwFlagStr = "1";
    	            }
    	        }
    	        
    	        map.put("RSRV_STR6", "0"); //0非VoLTE用户 1：VoLTE用户
                map.put("RSRV_STR7", vwFlagStr);//标识用户加入的V网是否是全国V网,1:全国V网;0:本地V网
                
    	    }
    	}
    }
    
    @Override
    protected void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);
        map.put("SHORT_CODE", paramData.getString("SHORT_CODE", "")); // 短号
    }

    /**
     * 新增短号临时表 wangyf6
     * 
     * @param pd
     * @throws Exception
     */
    public void insertTemporaryShortCode() throws Exception
    {
        String userID = reqData.getGrpUca().getUserId();

        IData infoData = new DataMap();
        infoData.put("SHORT_CODE", paramData.getString("SHORT_CODE", ""));

        // 是否有母集团
        IDataset relaData = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userID, "40", Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(relaData))
        {
            String userIDa = relaData.getData(0).getString("USER_ID_A", "");
            if (StringUtils.isNotBlank(userIDa))
            {
                infoData.put("PARTITION_ID", userIDa.substring(userIDa.length() - 4, userIDa.length()));
                infoData.put("USER_ID_A", userIDa);// 母集团的USER_ID
                infoData.put("RSRV_TAG1", "1");// 1标识是母集团
            }
        }
        else
        {
            infoData.put("PARTITION_ID", userID.substring(userID.length() - 4, userID.length()));
            infoData.put("USER_ID_A", userID);// 集团VPMN的USER_ID
        }
        infoData.put("USER_ID_B", reqData.getUca().getUserId());
        infoData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
        infoData.put("ACCEPT_DATE", this.getAcceptTime());
        infoData.put("TRADE_ID", getTradeId());
        infoData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        infoData.put("REMARK", "集团VPMN成员新增");
        try
        {
            boolean resultFlag = Dao.insert("TF_F_TEMPORARY_SHORTCODE", infoData, Route.getCrmDefaultDb());
        }
        catch (Exception e)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_219, infoData.getString("SHORT_CODE"), infoData.getString("USER_ID_A"));

        }
    }

    /**
     * 防止前台相同短号同时验证通过的情况，对短号进行写入台账前的第二次验证
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public void shortCodeValidate(String shortCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SHORT_CODE", shortCode);
        param.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
        IData retData = new DataMap();
        retData = VpnUnit.shortCodeValidateVpn(param);

        if (IDataUtil.isNotEmpty(retData))
        {
            if (!retData.getBoolean("RESULT"))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_220, retData.getString("ERROR_MESSAGE"));
            }
        }
    }
    
    /**
     * 覆盖TradeBaseBean的方法
     */
    @Override
    public void modTradeData() throws Exception
    {
        super.modTradeData();
        //以下逻辑从actTradeSub移植过来，原先防止二次确认短信报插入数据重复的错误，actTradeSub 在下发二次确认短信前执行的，
        //且以下逻辑为直接插表，所以有二次确认短信也不会回滚，移植到此方法中，此方法在二次确认短信后执行
        String shortCode = paramData.getString("SHORT_CODE", "");
        if (StringUtils.isNotBlank(shortCode))
        {
            // shortCodeValidate(shortCode); //第二次校验，觉得没必要，先屏蔽
            insertTemporaryShortCode();
        }
    }
    
    /**
     * 处理台帐Other子表的数据,处理V网来显方式
     * for REQ201910230023
     * @throws Exception
     */
    public void infoRegOtherData() throws Exception 
    {
        String grpClipType = paramData.getString("NOTIN_GRP_CLIP_TYPE","");//GRP_CLIP_TYPE 呼叫来显方式
        //String grpUserClipType = paramData.getString("NOTIN_GRP_USER_CLIP_TYPE","");//GRP_USER_CLIP_TYPE 选择号显方式
        String grpUserMod = paramData.getString("NOTIN_GRP_USER_MOD","");//GRP_USER_MOD 成员修改号显方式
        String clipType = paramData.getString("NOTIN_CLIP_TYPE","");
        
        if(StringUtils.isNotBlank(grpClipType) && StringUtils.isNotBlank(grpUserMod)
        		&& StringUtils.equals("1", grpClipType) && StringUtils.equals("1", grpUserMod))
        {
        	if(StringUtils.isNotBlank(clipType))
        	{
        		IDataset lineDataset = new DatasetList();
        		
        		IData data = new DataMap();
                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("RSRV_VALUE_CODE", "VPMN_MEBCLIP");
                data.put("RSRV_VALUE", clipType);
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                lineDataset.add(data);
                
                super.addTradeOther(lineDataset);
        	}
        }
    }
    
}
