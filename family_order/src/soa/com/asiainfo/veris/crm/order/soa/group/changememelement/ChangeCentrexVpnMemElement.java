
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ChangeCentrexVpnMemElement extends ChangeMemElement
{
    private String netTypeCode = "05";

    public ChangeCentrexVpnMemElement()
    {

    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        infoRegDataRes();// Centrex 处理资源信息
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 配置成固定指令方式，因此都插VPN台帐
        infoRegDataVpn();
        // 必须插服务才能生成完整的参数
        infoRegDataSvc();

        infoRegDataCentreOther();

        infoRegDataRelation();
    }

    public String getDiscntCode() throws Exception
    {
        String discntCode = "";
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getUca().getProductId());
        IDataset element = reqData.cd.getProductsElement(mebProductId);
        if (IDataUtil.isNotEmpty(element))
        {
            for (int i = 0; i < element.size(); i++)
            {
                IData map = element.getData(i);
                if (map.getString("ELEMENT_TYPE_CODE", "").equals("D") && map.getString("MODIFY_TAG").equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                    discntCode = map.getString("DISCNT_CODE");
                    break;
                }
            }
        }
        return discntCode;
    }

    /**
     * 获取参数
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
     * Centrex 登记平台other表 判读
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        
        if("05".equals(netTypeCode)) //手机号码加融合V网不发数指
        {
        	IData centreData = new DataMap();
            centreData.put("USER_ID", reqData.getUca().getUserId());
            centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData.put("RSRV_VALUE", "Centrex成员业务");
            centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            centreData.put("RSRV_STR9", "860"); // 服务id
            centreData.put("OPER_CODE", "08"); // 操作类型

            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            centreData.put("START_DATE", getAcceptTime());
            centreData.put("END_DATE", SysDateMgr.getTheLastTime());
            centreData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData);
        }

        addTradeOther(dataset);
    }

    /**
     * 短号码变更处理关系表
     * 
     * @throws Exception
     */
    private void infoRegDataRelation() throws Exception
    {
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);

        if (paramData.getString("NOTIN_OLD_SHORT_CODE", "").equals(paramData.getString("SHORT_CODE", "")))
        {
            return;
        }

        // 查询用户的关系信息
        String userIdA = reqData.getGrpUca().getUserId();// 用户USER_ID
        String userIdB = reqData.getUca().getUserId();// 成员USER_ID
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IDataset uuInfos = RelaUUInfoQry.getSEL_BY_PK1(userIdA, userIdB, relationTypeCode, null, eparchyCode);
        if (IDataUtil.isEmpty(uuInfos))
        {
            return;
        }

        IData uuInfo = uuInfos.getData(0);
        String newShortCode = paramData.getString("SHORT_CODE", "");
        if (StringUtils.isBlank(newShortCode))
        {
            return;
        }
        uuInfo.put("SHORT_CODE", newShortCode);
        uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        this.addTradeRelation(uuInfo);
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

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);
        // 判断短号是否修改
        String oldShortCode = paramData.getString("OLD_SHORT_CODE");
        String shortCode = paramData.getString("SHORT_CODE");
        if (oldShortCode.equals(shortCode))
        {
            return;
        }

        String uIda = "";
        // 查询集团订购的多媒体桌面电话信息
        IDataset ds = UserProductInfoQry.getMainUserProductInfoByCstId(grp_cust_id, "2222", null);
        if (IDataUtil.isNotEmpty(ds))
        {
            IData tmp = ds.getData(0);
            uIda = tmp.getString("USER_ID", "");
        }
        IDataset resDataset = new DatasetList();
        if ("05".equals(netTypeCode))
        {
            // 多媒体桌面电话的资源修改 start
            // 新增一条资源信息
            IData resData = new DataMap();
            resData.put("USER_ID_A", uIda); // 赋该成员的多媒体桌面电话集团userida，以修改短号码
            resData.put("RES_CODE", shortCode);
            resData.put("RES_TYPE_CODE", "S");
            resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            resData.put("IMSI", "0"); // IMSI
            resData.put("START_DATE", getAcceptTime());
            resData.put("END_DATE", SysDateMgr.getTheLastTime());
            resData.put("INST_ID", SeqMgr.getInstId());
            resDataset.add(resData);

            // 删除原来的资源信息
            IDataset results = UserResInfoQry.getResByUserIdResType(user_id, uIda, "S", oldShortCode);
            if (IDataUtil.isEmpty(results))
            {
                CSAppException.apperr(GrpException.CRM_GRP_649);
            }

            IData result = results.getData(0);
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
            // 多媒体桌面电话的资源修改 end
        }

        // 融合V网的资源修改 start
        // 新增一条资源信息
        IData resData2 = new DataMap();
        resData2.put("RES_CODE", shortCode);
        resData2.put("RES_TYPE_CODE", "S");
        resData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        resData2.put("IMSI", "0"); // IMSI
        resData2.put("START_DATE", getAcceptTime());
        resData2.put("END_DATE", SysDateMgr.getTheLastTime());
        resData2.put("INST_ID", SeqMgr.getInstId());
        resDataset.add(resData2);

        // 删除原来的资源信息
        IDataset results2 = UserResInfoQry.getResByUserIdResType(user_id, reqData.getGrpUca().getUserId(), "S", oldShortCode);
        if (IDataUtil.isEmpty(results2))
        {
            CSAppException.apperr(GrpException.CRM_GRP_650);
        }

        IData result2 = results2.getData(0);
        result2.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        if (result2.getString("START_DATE").compareTo(getAcceptTime()) > 0)
        {
            String lastSecond = SysDateMgr.getLastSecond(getAcceptTime()); // 当前时间减一秒
            result2.put("END_DATE", lastSecond);
        }
        else
        {
            result2.put("END_DATE", getAcceptTime());
        }
        resDataset.add(result2);
        // 融合V网的资源修改 end

        reqData.cd.putRes(resDataset);
    }

    /**
     * 修改800智能网VPMN集团，服务开通才会发联指
     * 
     * @throws Exception
     */
    public void infoRegDataSvc() throws Exception
    {
        // 查询用户的服务信息
        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();
        String serviceId = "860"; // 860服务

        IDataset svcInfos = UserSvcInfoQry.getSvcUserId(userId, userIdA, serviceId);
        if (IDataUtil.isEmpty(svcInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_285);
        }

        IData svcInfo = svcInfos.getData(0);
        svcInfo.put("ELEMENT_ID", serviceId);
        svcInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        this.addTradeSvc(svcInfo);
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
    private void infoRegDataVpn() throws Exception
    {
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);

        // 获取成员VPN个性信息
        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IData vpnData = UserVpnInfoQry.getMemberVpnByUserId(userId, userIdA, eparchyCode);
        if (IDataUtil.isEmpty(vpnData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_240);
        }
        else
        {
            // VPN数据
            IDataset dataset = new DatasetList();

            // 个性化参数
            // vpnData.put("MEMBER_KIND", "1");//关系--普通客户
            vpnData.put("SHORT_CODE", paramData.getString("SHORT_CODE", ""));// 成员短号码;
            vpnData.put("PERFEE_PLAY_BACK", paramData.getString("PERFEE_PLAY_BACK", ""));// 个人付费放音标志
            vpnData.put("SINWORD_TYPE_CODE", paramData.getString("M_SINWORD_TYPE_CODE", ""));// 语种
            vpnData.put("CALL_DISP_MODE", paramData.getString("CALL_DISP_MODE", ""));// 主叫号码显示方式
            vpnData.put("CALL_AREA_TYPE", paramData.getString("M_CALL_AREA_TYPE", ""));// 呼叫区域类型

            // CALL_NET_TYPE 1111 呼叫网络类型
            // M_CALL_NET_TYPE1内网,M_CALL_NET_TYPE2网间,M_CALL_NET_TYPE3网外,M_CALL_NET_TYPE4网外号码组

            paramData.put("CALL_NET_TYPE1", paramData.getString("M_CALL_NET_TYPE1", ""));// CALL_NET_TYPE1内网
            paramData.put("CALL_NET_TYPE2", paramData.getString("M_CALL_NET_TYPE2", ""));// CALL_NET_TYPE2网间
            paramData.put("CALL_NET_TYPE3", paramData.getString("M_CALL_NET_TYPE3", ""));// CALL_NET_TYPE3网外
            paramData.put("CALL_NET_TYPE4", paramData.getString("M_CALL_NET_TYPE4", ""));// CALL_NET_TYPE4网外号码组

            vpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(paramData)); // 呼叫网络类型
            // CALL_NET_TYPE

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

            // vpnData.put("PKG_TYPE", paramData.getString("PKG_TYPE", ""));//
            // 资费套餐类型--页面上有、数据库没配
            vpnData.put("CALL_ROAM_TYPE", paramData.getString("CALL_ROAM_TYPE", ""));// 网外呼叫主叫权限
            vpnData.put("BYCALL_ROAM_TYPE", paramData.getString("BYCALL_ROAM_TYPE", ""));// 网外呼叫被叫权限

            vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

            dataset.add(vpnData);
            addTradeVpnMeb(dataset);
        }
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);
        String short_code = paramData.getString("SHORT_CODE");
        paramData.put("CNRT_SHORT_CODE", short_code); // 固话短号发报文

        if (!"05".equals(netTypeCode)) // 判断当前号码是否是移动的号码
        {
            paramData.put("OneNumber", "1"); // 移动的号码一号通开通
            paramData.put("AnchorFlag", "1"); // 移动的号码锚定标识
        }
        paramData.put("ServiceType", "1"); // 短号有效标志，0-多媒体电话无效；1-融合V网有效

        reqData.cd.putProductParamList(mebProductId, IDataUtil.iData2iDataset(paramData, "ATTR_CODE", "ATTR_VALUE")); // 参数插入attr表
    }

    /**
     * 覆写父类方法
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        String discntCode = getDiscntCode();

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getUca().getUserId());
        data.put("RSRV_STR2", "5"); // CENTREX 参数CNTRX_MEMB_POWER取值
        
        if (!"05".equals(netTypeCode))
        {
            data.put("RSRV_STR3", "0"); // HLR发送标志;海南特殊，只发智能网和HLR，不发报文
        } else {
            data.put("RSRV_STR3", reqData.getUca().getSerialNumber());
        }
        
        data.put("RSRV_STR6", reqData.getUca().getCustPerson().getCustName());
        data.put("RSRV_STR8", discntCode);// 只能取一个discntCode，为什么？
        String payMode = "";
        data.put("RSRV_STR10", payMode); // 需细化

    }

    /**
     * 覆写父类方法
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        String discntCode = getDiscntCode();

        map.put("RSRV_STR1", reqData.getUca().getUserId());
        map.put("RSRV_STR2", "5"); // CENTREX 参数CNTRX_MEMB_POWER取值
        map.put("RSRV_STR3", reqData.getUca().getSerialNumber());
        map.put("RSRV_STR6", reqData.getUca().getCustPerson().getCustName());
        map.put("RSRV_STR8", discntCode);// 只能取一个discntCode，为什么？
        String payMode = "";
        map.put("RSRV_STR10", payMode); // 需细化
    }
}
