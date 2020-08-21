
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateCentrexVpnGroupUserReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CreateCentrexVpnGroupUser extends CreateGroupUser
{
    boolean flattag = false;

    protected CreateCentrexVpnGroupUserReqData reqData = null;

    public CreateCentrexVpnGroupUser()
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

        IData paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            String vpn_no = paramData.getString("VPN_NO", ""); // 页面传过来的vpn编码
            if (StringUtils.isNotBlank(vpn_no))
            {
                flattag = false;
            }
            else
            {
                flattag = true;
            }
        }

        // 没有vpn_no，则取多媒体桌面电话页面参数初始值，写入attr表
        if (flattag)
        {
        	IDataset paramList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
            String eparchyCode = BizRoute.getRouteId();
            // 取多媒体桌面电话默认参数值
            IDataset dataset = AttrItemInfoQry.getAttrItemAByIDTO("2222", "P", "0", eparchyCode, null);
            for (int row = 0, len = dataset.size(); row < len; row++)
            {
                IData dparam = (IData) dataset.get(row);
                String attr_code = dparam.getString("ATTR_CODE");
                String attr_init_value = dparam.getString("ATTR_INIT_VALUE");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attr_code);
                attr.put("ATTR_VALUE", attr_init_value);
                paramList.add(attr);
            }
        }
    }

    /**
     * 其它台帐处理
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVPMNVpn();
        infoRegDataRelation();
        infoRegDataCentreOther();
    }

    /*
     * 获取VPMN费率参数
     */
    public IData getFee() throws Exception
    {
        IData feeData = new DataMap();

        // 湖南VPMN业务根据前台选择的VPMN优惠编码查找
        // VPN费率参数
        IDataset ids = CommparaInfoQry.getCommparaAllCol("CSM", "250", "0", reqData.getUca().getUser().getEparchyCode());
        if (IDataUtil.isNotEmpty(ids))
        {
            IData objCommpara = ids.getData(0);

            if (objCommpara.size() == 1)
            {
                feeData.put("INTER_FEEINDEX", objCommpara.getString("PARA_CODE2", ""));
                feeData.put("OUT_FEEINDEX", objCommpara.getString("PARA_CODE3", ""));
                feeData.put("OUTGRP_FEEINDEX", objCommpara.getString("PARA_CODE4", ""));
                feeData.put("NOTDISCNT_FEEINDEX", objCommpara.getString("PARA_CODE7", ""));
            }
        }
        return feeData;
    }

    /**
     * @description 得到融合v网产品参数
     * @author yish
     * @return
     * @throws Exception
     */
    private IData getParamData() throws Exception
    {
        String curProductId = reqData.getUca().getProductId();
        // 融合v网产品参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        return paramData;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateCentrexVpnGroupUserReqData();
    }

    /**
     * 融合V网 登记平台other表
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();

        String userId = reqData.getUca().getUserId();
        String procuctId = reqData.getUca().getProductId();

        centreData.put("USER_ID", userId);
        centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        centreData.put("RSRV_VALUE", "融合V网开通");
        centreData.put("RSRV_STR1", procuctId);// 产品ID

        centreData.put("RSRV_STR9", "8001"); // 服务id
        centreData.put("OPER_CODE", "03"); // 操作类型

        centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);

        // 如果VPN_NO没数据则发创建集团
        if (flattag)
        {
            IData centreData1 = new DataMap();

            centreData1.put("USER_ID", userId);
            centreData1.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData1.put("RSRV_VALUE", "创建集团");
            centreData1.put("RSRV_STR1", procuctId);// 产品ID

            centreData1.put("RSRV_STR9", "817"); // 服务id
            centreData1.put("OPER_CODE", "01"); // 操作类型 01 注册

            centreData1.put("STATE", TRADE_MODIFY_TAG.Add.getValue());
            centreData1.put("START_DATE", getAcceptTime());
            centreData1.put("END_DATE", SysDateMgr.getTheLastTime());
            centreData1.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData1);
        }
        addTradeOther(dataset);
    }

    /**
     * 插关系表
     * 
     * @author
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {
        IDataset relaset = new DatasetList();
        IData rela1 = new DataMap();
        IData rela2 = new DataMap();
        String roleCodeA = "0";// 原cb从TD_S_RELATION_ROLE取，现从静态表取，无法像原来那样获得ROLE_CODE_A

        String ouser_id01 = reqData.getOUSER_ID01();
        String outno1 = reqData.getOUTNO1();
        String ouser_id11 = reqData.getOUSER_ID11();
        String outno2 = reqData.getOUTNO2();

        String inst_id = SeqMgr.getInstId();

        if (StringUtils.isNotBlank(ouser_id01))
        {
            rela1.put("RELATION_TYPE_CODE", "04");// 网外号码组与V网关系
            rela1.put("USER_ID_A", reqData.getUca().getUserId());
            rela1.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
            rela1.put("USER_ID_B", ouser_id01);
            rela1.put("SERIAL_NUMBER_B", ouser_id01);
            rela1.put("ROLE_CODE_A", roleCodeA);
            rela1.put("ROLE_CODE_B", outno1);
            rela1.put("RSRV_STR1", outno1);// 存放out_no
            rela1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            rela1.put("START_DATE", getAcceptTime());
            rela1.put("END_DATE", SysDateMgr.getTheLastTime());
            rela1.put("REMARK", reqData.getUca().getUserId());
            rela1.put("INST_ID", inst_id);
            relaset.add(rela1);
        }

        if (StringUtils.isNotBlank(ouser_id11))
        {
            rela2.put("RELATION_TYPE_CODE", "04");// 网外号码组与V网关系
            rela2.put("USER_ID_A", reqData.getUca().getUserId());
            rela2.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
            rela2.put("USER_ID_B", ouser_id11);
            rela2.put("SERIAL_NUMBER_B", ouser_id11);
            rela2.put("ROLE_CODE_A", roleCodeA);
            rela2.put("ROLE_CODE_B", outno2);
            rela2.put("RSRV_STR1", outno2);// 存放out_no
            rela2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            rela2.put("START_DATE", getAcceptTime());
            rela2.put("END_DATE", SysDateMgr.getTheLastTime());
            rela2.put("REMARK", reqData.getUca().getUserId());
            rela2.put("INST_ID", inst_id);
            relaset.add(rela2);
        }
        addTradeRelation(relaset);
    }

    /**
     * 处理台帐VPN子表的数据-用户
     * 
     * @param Datas
     *            页面VPMN参数
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataVPMNVpn() throws Exception
    {
        // VPMN个性化参数
        IData paramData = getParamData();

        // VPN数据
        IDataset dataset = new DatasetList();
        IData vpnData = super.infoRegDataVpn();
        // 个性化参数
        // 1、SCP_CODE
        vpnData.put("SCP_CODE", paramData.getString("SCP_CODE", "10"));// 湖南固定为00  海南固定为10  否则无法被叫
        vpnData.put("MAX_USERS", paramData.getString("MAX_USERS", "50000")); // 集团最大用户数-初始值50000
        // 2、集团范围属性
        vpnData.put("VPN_SCARE_CODE", paramData.getString("VPN_SCARE_CODE", "")); // 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取
        vpnData.put("SINWORD_TYPE_CODE", paramData.getString("SINWORD_TYPE_CODE", "")); // 语种选择
        vpnData.put("OVER_FEE_TAG", paramData.getString("OVER_FEE_TAG", "")); // 呼叫超出限额处理标记
        vpnData.put("CALL_AREA_TYPE", paramData.getString("CALL_AREA_TYPE", "")); // 呼叫区域类型
        // CALL_NET_TYPE 1111
        // CALL_NET_TYPE1内网,CALL_NET_TYPE2网间,CALL_NET_TYPE3网外,CALL_NET_TYPE4网外号码组
        vpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(paramData)); // 呼叫网络类型
        vpnData.put("MAX_INNER_NUM", paramData.getString("MAX_INNER_NUM", "")); // 最大网内号码总数
        vpnData.put("MAX_OUTNUM", paramData.getString("MAX_OUTNUM", "")); // 最大网外号码总数
        vpnData.put("MAX_CLOSE_NUM", paramData.getString("MAX_CLOSE_NUM", "")); // 最大闭合用户群数
        vpnData.put("MAX_NUM_CLOSE", paramData.getString("MAX_NUM_CLOSE", "")); // 单个闭合用户群能包含的最大用户数
        vpnData.put("PERSON_MAXCLOSE", paramData.getString("PERSON_MAXCLOSE", "")); // 单个用户最大可加入的闭合群数
        vpnData.put("MAX_TELPHONIST_NUM", paramData.getString("MAX_TELPHONIST_NUM", "")); // 话务员最大数
        // 行业类型-集团V网类别
        vpnData.put("WORK_TYPE_CODE", paramData.getString("WORK_TYPE_CODE", "")); // 行业类型-集团V网类别
        // 3、集团分机号码长度
        vpnData.put("SHORT_CODE_LEN", paramData.getString("SHORT_CODE_LEN", "")); // 集团分机号码长度
        // 4、综合VPN标识（铁通号码加入V网关键点）
        vpnData.put("VPN_USER_CODE", paramData.getString("VPN_USER_CODE", "2")); // 综合VPN标识（铁通号码加入V网关键点）

        vpnData.put("RSRV_STR1", paramData.getString("DISCNT_TYPE", "")); // 集团优惠类型
        // 5、VPMN_TYPE根据品牌不同，采用固定值
        vpnData.put("VPMN_TYPE", "0");//
        // 6、FUNC_TLAGS根据CALL_AREA_TYPE和CALL_NET_TYPE不同进行组合--湖南不需要
        // FUNC_TLAGS 44444444 22111111111111100
        // VPN009主叫漫游权限,VPN010被叫漫游权限,VPN012呼叫前转,VPN013闭合用户群功能,VPN022网外号码组功能
        vpnData.put("FUNC_TLAGS", "1100000000000000000000000001000000000000");// 湖南的功能串-直接从参数表获取，老数据为空也可以
        // 7、集团VPN编码
        String vpn_no = paramData.getString("VPN_NO", "");
        if (StringUtils.isNotBlank(vpn_no))
        {
            vpnData.put("VPN_NO", vpn_no); // 集团VPN编码
        }
        else
        {
            vpnData.put("VPN_NO", reqData.getUca().getSerialNumber());
        }

        // 8、联指参数 V213 跨省V网 也是取VPN_SCARE_CODE值，湖南缺省为0
        // 2为跨省集团，VPN_NO通过集团资料获取字段VPMN_GROUP_ID RSRV_STR1 字段为集团省代码
        String vpn_scare_code = paramData.getString("VPN_SCARE_CODE", "");
        if (vpn_scare_code.equals("2"))
        {
            // 湖南RSRV_STR1已经被优惠类型占用

            // 联指V210参数
            // 跨省VPN 集团省间用户拨号方式，目前过渡阶段仅支持0，请在前台界面限定目前只能选择长号方式。
            // ‘0’：仅能通过长号方式拨打
            // ‘1’：仅能通过短号方式拨打
            // ‘2’：能通过长/短号方式拨打
            vpnData.put("RSRV_STR2", "0");// 跨省VPN 集团省间用户拨号方式
            // vpnData.put("RSRV_STR2", paramData.getString("DIAL_TYPE","0"));// 跨省VPN 集团省间用户拨号方式

            // 联指V211参数，V214参数，VPNCODE
            // 跨省集团总部集团编号 PROVINCEVPNCODE ，云南是用PROVINCEVPNCODE替代VPNNO，湖南保持原有的VPNNO不变
            vpnData.put("RSRV_STR3", paramData.getString("PROV_GROUPID", ""));

            // 联指V212参数
            vpnData.put("RSRV_STR4", paramData.getString("SCP_GT", ""));// 跨省集团所在省的省ID（12&16&01）
        }

        // 费率参数
        vpnData.put("FEEINDEX", "");

        IData feeData = getFee();

        vpnData.put("INTER_FEEINDEX", feeData.getString("INTER_FEEINDEX", "-1"));
        vpnData.put("OUT_FEEINDEX", feeData.getString("OUT_FEEINDEX", "-1"));
        vpnData.put("OUTGRP_FEEINDEX", feeData.getString("OUTGRP_FEEINDEX", "-1"));
        vpnData.put("NOTDISCNT_FEEINDEX", feeData.getString("NOTDISCNT_FEEINDEX", "-1"));

        dataset.add(vpnData);
        addTradeVpn(dataset);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateCentrexVpnGroupUserReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setOUSER_ID01(map.getString("OUSER_ID01"));
        reqData.setOUTNO1(map.getString("OUTNO1"));
        reqData.setOUSER_ID11(map.getString("OUSER_ID11"));
        reqData.setOUTNO2(map.getString("OUTNO2"));
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        /*
         * String cust_id = reqData.getUca().getCustId(); IData data = bizData.getTrade(); //双跨融合通讯 start //判断 如果融合V网
         * 开通了bboss产品（产品编码 20004），则把vpnno 写到SERIAL_NUMBER； IDataset attrbizDatas =
         * AttrBizInfoQry.getBizAttrByAttrValue("1","B","PRO","20004",null); if(IDataUtil.isNotEmpty(attrbizDatas)) {
         * IData bizData = attrbizDatas.getData(0); String product_id = bizData.getString("ATTR_VALUE"); IDataset
         * dataset = UserProductInfoQry.getUserProductInfoByCstId(cust_id,product_id,null);
         * if(IDataUtil.isNotEmpty(dataset)) { IData tmpData = dataset.getData(0); String serial_number =
         * tmpData.getString("SERIAL_NUMBER"); data.put("SERIAL_NUMBER", serial_number); } } //双跨融合通讯 end
         */
    }
}
