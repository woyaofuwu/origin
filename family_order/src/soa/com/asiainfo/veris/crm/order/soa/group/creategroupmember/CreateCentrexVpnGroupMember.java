
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CreateCentrexVpnGroupMember extends CreateGroupMember
{
    protected String cntrxMembPoer = "2";

    protected String power = "";

    private boolean crtFlag = true;

    private String userType = "";

    private String netTypeCode = "05";

    private String roleShortCode = "";

    protected String short_code = "";

    public CreateCentrexVpnGroupMember()
    {

    }

    /**
     * 生成登记信息
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        infoRegDataRes();// Centrex 处理资源信息
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        if (IDataUtil.isEmpty(reqData.cd.getSvc()))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_188);
        }

        if (crtFlag)
        {
            //只有手机成员才会进入这里，因为IMS用户必须先订购多媒体桌面电话
            infoRegDataImpu();
        }

        // 判断当前号码是否是IMS域的号码，是才发报文指令，屏蔽联指指令
        infoRegDataCentreOther("860", TRADE_MODIFY_TAG.Add.getValue());

        infoRegDataVPMNVpnMeb();
    }
    /**
     * 将手机用户登记在IMPU用户信息
     * 
     * @author tengg
     * @2010-8-16
     * @throws Exception
     */
    public void infoRegDataImpu() throws Exception
    {
        // 查询是否存在IMPU信息；
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();
        String user_id = reqData.getUca().getUserId();
        String rsrv_str1 = "1"; // 手机用户

        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(user_id, rsrv_str1, eparchyCode);
        if (IDataUtil.isEmpty(impuInfo))
        {
            IDataset dataset = new DatasetList();
            IData impuData = new DataMap();
            String serialNumber = reqData.getUca().getSerialNumber();
            // 获取IMPI
            StringBuilder strImpi = new StringBuilder();
            GroupImsUtil.genImsIMPI(serialNumber, strImpi, "3");
            // 获取IMPU
            StringBuilder strTel = new StringBuilder();
            StringBuilder strSip = new StringBuilder();
            GroupImsUtil.genImsIMPU(serialNumber, strTel, strSip, "3");

            impuData.put("INST_ID", SeqMgr.getInstId());// 实例ID
            impuData.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
            impuData.put("TEL_URL", strTel); // 公有标识IMPU
            impuData.put("SIP_URL", strSip);
            impuData.put("IMPI", strImpi); // 私有标识IMPI
            impuData.put("IMS_USER_ID", serialNumber); // IMS门户网站用户名

            //生成IMS密码15位 由字母+数字组成，区分字母大小写，且是由BOSS系统随机生成。 必须加密  关于优化IMS业务开通默认密码的需求
            String imsPwd = StrUtil.getRandomNumAndChar(15);
            String encryptImsPwd = DESUtil.encrypt(imsPwd);//加密。 服开再解密
            
            impuData.put("IMS_PASSWORD", encryptImsPwd); // IMS门户网站密码 
            impuData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());// 开始时间
            impuData.put("END_DATE", SysDateMgr.getTheLastTime());// 结束时间
            String tmp = strTel.toString();
            tmp = tmp.replaceAll("\\+", "");
            char[] c = tmp.toCharArray();
            String str2 = "";
            for(int i=c.length-1; i>=0; i--){
                
                str2 += String.valueOf(c[i]);
                str2 += ".";
            }
            str2 += "e164.arpa";
            String str3 = "";
            for(int i=4; i>=0; i--){
                
                str3 += String.valueOf(c[i]);
                str3 += ".";
            }
            str3 += "e164.arpa";
            
            impuData.put("RSRV_STR2", str2);
            impuData.put("RSRV_STR3", str3);
            impuData.put("RSRV_STR1", "1");
            impuData.put("RSRV_STR4", roleShortCode); // 成员角色|短号
            impuData.put("RSRV_STR5", "1");// 用于信控，标识为1是发了HSS,ENUM信息，为0则是发了后又取消
            impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            dataset.add(impuData);
            this.addTradeImpu(dataset);
        }
        else
        {
            IDataset dataset = new DatasetList();
            IData impuData = new DataMap();
            impuData = impuInfo.getData(0);
            impuData.put("RSRV_STR5", "1");// 用于信控，标识为1是发了HSS,ENUM信息，为0则是发了后又取消
            impuData.put("RSRV_STR4", roleShortCode); // 成员角色|短号
            impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            dataset.add(impuData);
            this.addTradeImpu(dataset);
        }
    }
    /**
     * 得到个性化参数
     * 
     * @return
     * @throws Exception
     */
    private IData getMebParamInfo(String mebProductId) throws Exception
    {
        IData paramData = reqData.cd.getProductParamMap(mebProductId);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        return paramData;
    }

    /**
     * 登记平台other表 判读
     */
    public void infoRegDataCentreOther(String serviceid, String modify_tag) throws Exception
    {
        String opercode = "08";
        if (modify_tag.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            opercode = "01"; // 操作码 01 注册
        }
        else if (modify_tag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            opercode = "02"; // 操作码 02注销
        }

        IDataset dataset = new DatasetList();

        if("05".equals(netTypeCode)) //手机号码加融合V网不发数指
        {
        	// 发CNTRX平台成员配置业务指令
            IData data = new DataMap();
            data.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            data.put("RSRV_VALUE", "融合V网成员业务配置");
            data.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
            data.put("RSRV_STR9", "8171"); // 服务id
            data.put("OPER_CODE", "03"); // 操作类型 配置成员
            data.put("MODIFY_TAG", modify_tag);
            // 分散修改
            data.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            data.put("INST_ID", SeqMgr.getInstId());
            dataset.add(data);

            if (crtFlag)
            {
                // 发CNTRX指令 创建成员
                IData centreData = new DataMap();

                centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
                centreData.put("RSRV_VALUE", "融合V网创建成员");
                centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
                centreData.put("RSRV_STR9", "8171"); // 服务id
                centreData.put("OPER_CODE", opercode); // 操作类型
                centreData.put("MODIFY_TAG", modify_tag);
                // 分散修改
                centreData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                centreData.put("END_DATE", SysDateMgr.getTheLastTime());
                centreData.put("INST_ID", SeqMgr.getInstId());
                dataset.add(centreData);

                IData hss = new DataMap();
                hss.put("RSRV_VALUE_CODE", "HSS");// domain域
                hss.put("RSRV_VALUE", "融合V网创建成员");
                hss.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
                hss.put("RSRV_STR9", "8172"); // 服务id
                hss.put("OPER_CODE", "01"); // 作类型 01 开户
                hss.put("RSRV_STR10", "100"); // 模版id
                hss.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                // 分散账期修改
                hss.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                hss.put("END_DATE", SysDateMgr.getTheLastTime());
                hss.put("INST_ID", SeqMgr.getInstId());
                dataset.add(hss);

                /*
                IData enumData = new DataMap();
                enumData.put("RSRV_VALUE_CODE", "ENUM");// domain域
                enumData.put("RSRV_VALUE", "融合V网创建成员");
                enumData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
                enumData.put("RSRV_STR9", "8173"); // 服务id
                enumData.put("OPER_CODE", "01"); // 操作类型
                enumData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                // 分散账期修改
                enumData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                enumData.put("END_DATE", SysDateMgr.getTheLastTime());
                enumData.put("INST_ID", SeqMgr.getInstId());
                dataset.add(enumData);
                */
            }
            else
            {
                // 发CNTRX指令,成员信息变更ServiceType由0为1：短号可用
                IData centreData2 = new DataMap();
                centreData2.put("RSRV_VALUE_CODE", "CNTRX");// domain域
                centreData2.put("RSRV_VALUE", "多媒体桌面电话");
                centreData2.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

                centreData2.put("RSRV_STR9", "8171"); // 服务id
                if ("2".equals(power))
                {
                    centreData2.put("OPER_CODE", "28"); // 操作类型：修改管理员
                }
                else
                {
                    centreData2.put("OPER_CODE", "08"); // 操作类型：修改成员
                }
                centreData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                centreData2.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                centreData2.put("END_DATE", SysDateMgr.getTheLastTime());
                centreData2.put("INST_ID", SeqMgr.getInstId());
                dataset.add(centreData2);
            }
        }
        
        addTradeOther(dataset);
    }

    /**
     * Centrex 处理资源 如果资源是从多媒体中同步过来的，需要特殊处理新增
     * 
     * @throws Exception
     */
    public void infoRegDataRes() throws Exception
    {
        String grp_cust_id = reqData.getGrpUca().getCustId(); // 集团客户id
        String user_id = reqData.getUca().getUserId(); // 成员用户id
        String uIda = "";
        // 查询集团订购的多媒体桌面电话信息
        IDataset ds = UserProductInfoQry.getMainUserProductInfoByCstId(grp_cust_id, "2222", null);
        if (IDataUtil.isNotEmpty(ds))
        {
            IData tmp = ds.getData(0);
            uIda = tmp.getString("USER_ID", "");
        }
        // 查用户资源(2222)
        IDataset results = UserResInfoQry.getUserResInfoByUserIdRestype(user_id, uIda, "S");
        if (IDataUtil.isEmpty(results))
        {
            IDataset resDataset = new DatasetList();
            IDataset resDatas = reqData.cd.getRes();
            if (IDataUtil.isNotEmpty(resDatas))
            {
                for (int i = 0, size = resDatas.size(); i < size; i++)
                {
                    IData resData = resDatas.getData(i);

                    resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    resData.put("IMSI", "0"); // IMSI
                    resData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                    resData.put("END_DATE", SysDateMgr.getTheLastTime());
                    resData.put("INST_ID", SeqMgr.getInstId());
                    resData.put("USER_ID", reqData.getUca().getUser().getUserId());
                    resData.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
                    
                    resDataset.add(resData);
                }
            }
            reqData.cd.putRes(resDataset);
        }
        else
        {
            String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
            // 得到成员参数
            IData paramData = getMebParamInfo(mebProductId);
            // 判断短号是否修改
            String oldShortCode = paramData.getString("OLD_SHORT_CODE");

            // 新增一条融合V网成员短号资源信息
            IData result = results.getData(0);
            if (StringUtils.isBlank(oldShortCode))
                oldShortCode = result.getString("RES_CODE", "");
            IDataset resDataset = new DatasetList();
            IData resData = new DataMap();
            resData.put("RES_CODE", short_code);
            resData.put("RES_TYPE_CODE", "S");
            resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            resData.put("IMSI", "0");
            resData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            resData.put("END_DATE", SysDateMgr.getTheLastTime());
            resData.put("INST_ID", SeqMgr.getInstId());
            resData.put("USER_ID", reqData.getUca().getUser().getUserId());
            resData.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
            
            resDataset.add(resData);
            if (!oldShortCode.equals(short_code)) // 新增一条资源信息
            {
                IData resData2 = new DataMap();
                resData.put("USER_ID", reqData.getUca().getUser().getUserId());
                resData2.put("USER_ID_A", uIda); // 赋该成员的多媒体桌面电话集团userida，以修改短号码
                resData2.put("RES_CODE", short_code);
                resData2.put("RES_TYPE_CODE", "S");
                resData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                resData2.put("IMSI", "0"); // IMSI
                resData2.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                resData2.put("END_DATE", SysDateMgr.getTheLastTime());
                resData2.put("INST_ID", SeqMgr.getInstId());
                resDataset.add(resData2);

                // 删除原来的资源信息
                result.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                if (result.getString("START_DATE").compareTo(getAcceptTime()) > 0)
                {
                    String lastSecond = SysDateMgr.getLastSecond(getAcceptTime()); // 当前时间减一秒
                    result.put("END_DATE", lastSecond);
                }
                else
                {
                    result.put("END_DATE", getAcceptTime());
                }
                resDataset.add(result);
            }
            reqData.cd.putRes(resDataset);
        }
    }

    @Override
    protected void setTradeRes(IData map) throws Exception
    {       
        map.put("USER_ID", reqData.getUca().getUser().getUserId());
        
        if (diversifyBooking)
        {
            map.put("START_DATE", reqData.getFirstTimeNextAcct());
        }
    }
    
    /**
     * 处理话务员服务数据
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataTelSVC() throws Exception
    {

        IData svcData = new DataMap();
        // 如果有话务员标志，取消870服务
        // 问题：cpp代码中是写死的，不一定会有870服务，即使有怎么捞出来？

        svcData.put("USER_ID", reqData.getUca().getUserId());
        //
        svcData.put("SERVICE_ID", "870");
        svcData.put("MODIFY_TAG", "1");
        svcData.put("SERV_PARA1", "");
        svcData.put("SERV_PARA2", "");
        svcData.put("SERV_PARA3", "");
        svcData.put("SERV_PARA4", "");
        svcData.put("SERV_PARA5", "");
        svcData.put("SERV_PARA6", "");
        svcData.put("SERV_PARA7", "");
        svcData.put("SERV_PARA8", "");
        svcData.put("START_DATE", getAcceptTime());
        svcData.put("END_DATE", getAcceptTime());

        addTradeSvc(svcData);
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
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        else
        {
            // VPN数据
            IDataset dataset = new DatasetList();
            IData vpnData = super.infoRegDataVpnMeb();

            // 个性化参数
            // 待修改
            // vpnData.put("MEMBER_KIND", "1");//关系--普通客户
            vpnData.put("SHORT_CODE", paramData.getString("SHORT_CODE", ""));// 成员短号码;
            vpnData.put("PERFEE_PLAY_BACK", paramData.getString("PERFEE_PLAY_BACK", ""));// 个人付费放音标志
            if ("".equals(vpnData.getString("PERFEE_PLAY_BACK")))
            {
                vpnData.put("PERFEE_PLAY_BACK", "1");// 缺省值
            }

            vpnData.put("SINWORD_TYPE_CODE", paramData.getString("M_SINWORD_TYPE_CODE", ""));// 语种
            if ("".equals(vpnData.getString("SINWORD_TYPE_CODE")))
            {
                vpnData.put("SINWORD_TYPE_CODE", "1");// 缺省值
            }

            vpnData.put("CALL_DISP_MODE", paramData.getString("CALL_DISP_MODE", ""));// 主叫号码显示方式
            if ("".equals(vpnData.getString("CALL_DISP_MODE")))
            {
                vpnData.put("CALL_DISP_MODE", "1");// 缺省值-短号
            }

            vpnData.put("CALL_AREA_TYPE", paramData.getString("M_CALL_AREA_TYPE", ""));// 呼叫区域类型
            if ("".equals(vpnData.getString("CALL_AREA_TYPE")))
            {
                vpnData.put("CALL_AREA_TYPE", "1");// 缺省值
            }

            // CALL_NET_TYPE 1111 呼叫网络类型
            // M_CALL_NET_TYPE1内网,M_CALL_NET_TYPE2网间,M_CALL_NET_TYPE3网外,M_CALL_NET_TYPE4网外号码组
            paramData.put("CALL_NET_TYPE1", paramData.getString("M_CALL_NET_TYPE1", ""));// CALL_NET_TYPE1内网
            paramData.put("CALL_NET_TYPE2", paramData.getString("M_CALL_NET_TYPE2", ""));// CALL_NET_TYPE2网间
            paramData.put("CALL_NET_TYPE3", paramData.getString("M_CALL_NET_TYPE3", ""));// CALL_NET_TYPE3网外
            paramData.put("CALL_NET_TYPE4", paramData.getString("M_CALL_NET_TYPE4", ""));// CALL_NET_TYPE4网外号码组
            vpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(paramData)); // 呼叫网络类型
            if ("".equals(vpnData.getString("CALL_NET_TYPE")))
            {
                vpnData.put("CALL_NET_TYPE", "1111");// 缺省值
            }

            vpnData.put("ADMIN_FLAG", VpnUnit.comFlagField(paramData.getString("ADMIN_FLAG", "")));// 个人标志-管理员
            // 如果话务员标志有效，取消服务，插台帐服务子表
            vpnData.put("TELPHONIST_TAG", VpnUnit.comFlagField(paramData.getString("TELPHONIST_TAG", "")));// 个人标志-话务员
            vpnData.put("LOCK_TAG", VpnUnit.comFlagField(paramData.getString("LOCK_TAG", "")));// 个人标志-锁定

            // CALL_NET_TYPE 1111 费用限额标志
            //
            // M_LIMFEE_TYPE_CODE1内网,M_LIMFEE_TYPE_CODE1网间,/M_LIMFEE_TYPE_CODE1网外,M_LIMFEE_TYPE_CODE1网外号码组

            vpnData.put("LIMFEE_TYPE_CODE", VpnUnit.comFeeTypeField(paramData)); // 费用限额标志
            // LIMFEE_TYPE_CODE
            // 字段最好改成和VPN表一样LIMIT_FEE
            vpnData.put("MON_FEE_LIMIT", paramData.getString("MON_FEE_LIMIT", ""));// 月费用限额

            vpnData.put("FUNC_TLAGS", "1100000000000000000000000001000000000000");

            // 资费套餐类型--页面上有、数据库没配
            vpnData.put("CALL_ROAM_TYPE", paramData.getString("CALL_ROAM_TYPE", ""));// 网外呼叫主叫权限
            vpnData.put("BYCALL_ROAM_TYPE", paramData.getString("BYCALL_ROAM_TYPE", ""));// 网外呼叫被叫权限

            dataset.add(vpnData);
            addTradeVpnMeb(dataset);
        }
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String cust_id = reqData.getGrpUca().getCustId(); // 集团客户id
        String user_id_b = reqData.getUca().getUserId(); // 成员用户id
        String eparchy_code = reqData.getUca().getUser().getEparchyCode(); // 成员地州
        netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);
        short_code = paramData.getString("SHORT_CODE");
        paramData.put("CNRT_SHORT_CODE", short_code); // 固话短号发报文

        if (!"05".equals(netTypeCode)) // 判断当前号码是否是移动的号码
        {
            paramData.put("OneNumber", "1"); // 移动的号码一号通开通
            paramData.put("AnchorFlag", "1"); // 移动的号码锚定标识
            paramData.put("CallBarring", "1"); // 呼叫限制开通
        }
        paramData.put("ServiceType", "1"); // 短号有效标志，0-多媒体电话无效；1-融合V网有效
        paramData.remove("OLD_SHORT_CODE"); // 移除该字段，保证短号入attr表

        // 查impu信息
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(user_id_b, eparchy_code);
        if (IDataUtil.isNotEmpty(impuInfo))
        {
            IData datatmp = impuInfo.getData(0);
            userType = datatmp.getString("RSRV_STR1", ""); // 用户类型
        }
        if ("00".equals(netTypeCode))
        {
            userType = "1"; // 1: 传统移动用户
        }

        // 判断成员是否已经订购其他ims产品，如果没有订购则返回true
        crtFlag = GroupImsUtil.getCreateMebFlag(cust_id, user_id_b);
        if (crtFlag)
        { // 如果成员角色已经存在，则覆盖当前成员角色
            power = map.getString("MEM_ROLE_B", "1"); // 1:普通成员 2：集团管理员
        }
        else
        {
            String tmp = GroupImsUtil.getImpuStr4(user_id_b, userType, 0);
            if (StringUtils.isNotBlank(tmp))
            {
                power = tmp; // 已订购ims业务的取impu表, RSRV_STR4 中的成员角色值
            }
        }
        if ("2".equals(power))
        {
            cntrxMembPoer = "1"; // 规范建议选: 1-企业管理员; 5-普通成员标志
        }

        reqData.cd.putProductParamList(mebProductId, IDataUtil.iData2iDataset(paramData, "ATTR_CODE", "ATTR_VALUE")); // 参数插入attr表

        roleShortCode = power + "|" + short_code; // impu表RSRV_STR4字段拼值
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        data.put("RSRV_STR2", cntrxMembPoer); // CENTREX 参数CNTRX_MEMB_POWER取值
        if (!"05".equals(netTypeCode))
        {
            data.put("RSRV_STR3", "0"); // HLR发送标志;海南特殊，只发智能网和HLR，不发报文
        }
    }

    /**
     * 重写父类setTradeRelation
     */
    protected void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);
        map.put("SHORT_CODE", paramData.getString("SHORT_CODE"));
    }

    /**
     * 覆写父类方法
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR2", cntrxMembPoer); // CENTREX 参数CNTRX_MEMB_POWER取值
        if (!"05".equals(netTypeCode))
        {
            map.put("RSRV_STR3", "0"); // HLR发送标志;海南特殊，只发智能网和HLR，不发报文
        }
    }
}
