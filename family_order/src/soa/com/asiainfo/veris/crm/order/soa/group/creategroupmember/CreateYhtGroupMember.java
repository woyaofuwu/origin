
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateYhtGroupMemberReqData;

public class CreateYhtGroupMember extends CreateGroupMember
{
    private CreateYhtGroupMemberReqData reqData = null;

    private String strZyht = "[]";

    private String strByht = "[]";
    
    private boolean crtFlag = true;

    private boolean flag = true;

    private String zTag = "0"; // 被叫一号通振动方式

    private String sertype = ""; // 业务类型取值（0～2） =0：开通被叫一号通业务 =1：开通主叫一号通业务 =2：同时开通主被叫一号通业务

    String cntrxMembPoer = "5";

    public CreateYhtGroupMember()
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
        // hy修改,ysh记得找我 :不能用这个名字 会覆盖基类 infoRegDataSvc();
        modRegSvcDataByYht();
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IDataset params = reqData.cd.getProductParamList(mebProductId);
        if (IDataUtil.isEmpty(params))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        for (int i = 0; i < params.size(); i++)
        {
            IData paramData = params.getData(i);

            if ("byht".equals(paramData.getString("ATTR_CODE")) || "zyht".equals(paramData.getString("ATTR_CODE")) || "oldbyht".equals(paramData.getString("ATTR_CODE")) || "oldzyht".equals(paramData.getString("ATTR_CODE"))) // 删除一号通主副号,不写attr表
            {
                params.remove(paramData);
                i--;
                continue;
            }
            else if ("Z_TAG_VAL".equals(paramData.getString("ATTR_CODE")))
            {
                paramData.put("ATTR_CODE", "CNTRX_MEMB_ONE_RTYPE"); // 设置振动方式保存在ATTR表
            }
            else if ("zpause".equals(paramData.getString("ATTR_CODE")))
            {
                paramData.put("ATTR_CODE", "CallingActivated");
            }
            else if ("bpause".equals(paramData.getString("ATTR_CODE")))
            {
                paramData.put("ATTR_CODE", "CalledActivated");
            }
        }
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
        
        if (crtFlag)
        {
            //只有手机成员才会进入这里，因为IMS用户必须先订购多媒体桌面电话
            infoRegDataImpu();
        }

        infoRegDataYhtMeb();
        infoRegDataOther();
    }

    /**
     * 将手机用户登记在IMPU用户信息
     * 
     * @author liuzz
     * @2014-11-10
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

            impuData.put("IMS_PASSWORD", "123456"); // IMS门户网站密码 
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
            impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            dataset.add(impuData);
            this.addTradeImpu(dataset);
        }
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
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        return paramData;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateYhtGroupMemberReqData();
    }

    /**
     * 处理台帐Other子表的数据,用于发指令
     * 
     * @param Datas
     * @author liaoyi
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData dataOtherCNTRXMD = new DataMap();
        IData dataOtherCNTRX = new DataMap();
        String mebUserId = reqData.getUca().getUserId();
        if (flag)
        {
            dataOtherCNTRXMD.put("USER_ID", mebUserId);
            dataOtherCNTRXMD.put("RSRV_VALUE_CODE", "CNTRX");
            dataOtherCNTRXMD.put("RSRV_VALUE", "融合一号通成员锚定服务");
            dataOtherCNTRXMD.put("RSRV_STR9", "8611"); // 锚定，service_id
            dataOtherCNTRXMD.put("OPER_CODE", "0");
            dataOtherCNTRXMD.put("RSRV_STR11", "0");
            dataOtherCNTRXMD.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            dataOtherCNTRXMD.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            dataOtherCNTRXMD.put("END_DATE", SysDateMgr.getTheLastTime());
            dataOtherCNTRXMD.put("INST_ID", SeqMgr.getInstId());
            dataset.add(dataOtherCNTRXMD);
        }

        dataOtherCNTRX.put("USER_ID", mebUserId);
        dataOtherCNTRX.put("RSRV_VALUE_CODE", "CNTRX");
        dataOtherCNTRX.put("RSRV_VALUE", "融合一号通成员CNTRX服务");
        String userType = "0";// =0 (SIP终端): IMS SIP-UE 用户

        if (flag)
        {
            userType = "4";// =4 : 传统移动用户
        }

        dataOtherCNTRX.put("RSRV_STR1", userType);// 配置主号码用户类型
        dataOtherCNTRX.put("RSRV_STR2", zTag);// 振铃方式 ， 取值(范围)：Int(0~1) , =0：同振=1：顺振
        dataOtherCNTRX.put("RSRV_STR9", "8174");// 用于服务开通，service_id
        dataOtherCNTRX.put("OPER_CODE", "31");
        dataOtherCNTRX.put("RSRV_STR11", "31"); // 操作类型
        dataOtherCNTRX.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        dataOtherCNTRX.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        dataOtherCNTRX.put("END_DATE", SysDateMgr.getTheLastTime());
        dataOtherCNTRX.put("INST_ID", SeqMgr.getInstId());
        dataset.add(dataOtherCNTRX);

        IData dataOtherCNTRX1 = new DataMap();
        dataOtherCNTRX1.putAll(dataOtherCNTRX);
        dataOtherCNTRX1.put("OPER_CODE", "03");
        dataOtherCNTRX1.put("RSRV_STR11", "03"); // 操作类型
        dataOtherCNTRX1.put("INST_ID", SeqMgr.getInstId());
        dataset.add(dataOtherCNTRX1);
        
        if(crtFlag){
            IData hss = new DataMap();
            hss.put("RSRV_VALUE_CODE", "HSS");// domain域
            hss.put("RSRV_VALUE", "融合一号通创建成员");
            hss.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
            hss.put("RSRV_STR9", "8172"); // 服务id
            hss.put("RSRV_STR10", "100");      //模版id
            
            hss.put("RSRV_STR12","1350");//HSS_SP_SIFC 
            hss.put("RSRV_STR20","101");//HSS_SPIFC_TEMPLATE_ID
            
            hss.put("OPER_CODE", "01"); // 作类型 01 开户
            hss.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            // 分散账期修改
            hss.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
            hss.put("END_DATE", SysDateMgr.getTheLastTime());
            hss.put("INST_ID", SeqMgr.getInstId());
            dataset.add(hss);
        }

        this.addTradeOther(dataset);
    }

    /**
     * 处理台帐uu表的数据
     * 
     * @param Datas
     * @author luoyong
     * @throws Exception
     */
    public void infoRegDataYhtMeb() throws Exception
    {
        IDataset zyhtds = new DatasetList(strZyht);
        IDataset byhtds = new DatasetList(strByht);

        IDataset lineDataset = new DatasetList();
        if (IDataUtil.isNotEmpty(zyhtds))
        {
            for (int i = 0; i < zyhtds.size(); i++)
            {
                IData yhtData = zyhtds.getData(i);

                IData rela = new DataMap();
                String roleCodeB = "1";
                String roleCodeA = "0";
                String ind_id = SeqMgr.getInstId();// 关系实例ID
                rela.put("INST_ID", ind_id);
                rela.put("RELATION_TYPE_CODE", "S6");
                rela.put("USER_ID_A", reqData.getUca().getUserId());
                rela.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
                rela.put("USER_ID_B", yhtData.getString("ZUSERID", ""));
                rela.put("SERIAL_NUMBER_B", yhtData.getString("SERIAL_NUMBER", "")); //

                String serialnumberb = yhtData.getString("SERIAL_NUMBER", "");
                if (StringUtils.isNotBlank(serialnumberb) && serialnumberb.startsWith("0"))
                {
                    serialnumberb = serialnumberb.substring(1);
                    rela.put("RSRV_STR1", serialnumberb); //
                }

                rela.put("ROLE_CODE_A", roleCodeA);
                rela.put("ROLE_CODE_B", roleCodeB);
                rela.put("RSRV_TAG1", yhtData.getString("MAIN_FLAG_CODE", "1")); // 主显标志1；0
                rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                rela.put("SHORT_CODE", "0");
                rela.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());

                rela.put("END_DATE", SysDateMgr.getTheLastTime());
                rela.put("REMARK", reqData.getGrpUca().getUserId()); // 集团userid

                lineDataset.add(rela);
            }
        }
        if (IDataUtil.isNotEmpty(byhtds))
        {
            for (int i = 0; i < byhtds.size(); i++)
            {
                IData byhtData = byhtds.getData(i);

                IData rela = new DataMap();
                String roleCodeB = "1";
                String roleCodeA = "0";
                String ind_id = SeqMgr.getInstId(); // 关系实例ID
                rela.put("INST_ID", ind_id);
                rela.put("RELATION_TYPE_CODE", "S7");
                rela.put("USER_ID_A", reqData.getUca().getUserId());
                rela.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
                rela.put("USER_ID_B", byhtData.getString("BUSERID", ""));
                rela.put("SERIAL_NUMBER_B", byhtData.getString("BSERIAL_NUMBER", "")); //
                rela.put("ROLE_CODE_A", roleCodeA);
                rela.put("ROLE_CODE_B", roleCodeB);
                // rela.put("RSRV_TAG1", byhtData.getString("Z_TAG_VAL","")); //同振0；顺振1；
                rela.put("RSRV_TAG1", zTag); // 同振0；顺振1；
                rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                rela.put("SHORT_CODE", "0");
                rela.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
                rela.put("END_DATE", SysDateMgr.getTheLastTime());
                rela.put("REMARK", reqData.getGrpUca().getUserId()); // 集团userid

                lineDataset.add(rela);
            }
        }
        this.addTradeRelation(lineDataset);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateYhtGroupMemberReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        IData paramData = getParamData();
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        strZyht = paramData.getString("zyht", "[]");
        if (StringUtils.isBlank(strZyht))
        {
            strZyht = "[]";
        }
        strByht = paramData.getString("byht", "[]");
        if (StringUtils.isBlank(strByht))
        {
            strByht = "[]";
        }
        zTag = paramData.getString("Z_TAG_VAL", "");
        if (StringUtils.isBlank(zTag))
        {
            zTag = "0";
        }
        paramData.clear();
        // strZyht = paramData.getString("zyht","[]");
        if (StringUtils.isNotBlank(strZyht) && !strZyht.equals("[]"))
        {
            if (StringUtils.isBlank(sertype))
                sertype = "1";
            else if (sertype.equals("0"))
                sertype = "2";
        }
        if (StringUtils.isNotBlank(strByht) && !strByht.equals("[]"))
        {
            if (StringUtils.isBlank(sertype))
                sertype = "0";
            else if (sertype.equals("1"))
                sertype = "2";
        }
        String netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别
        if ("05".equals(netTypeCode))
        {
            flag = false;
        }
        String power = map.getString("MEM_ROLE_B", "1");
        if ("2".equals(power))
        {
            cntrxMembPoer = "0";
        }
        // 判断成员是否已经订购其他ims产品，如果没有订购则返回true
        String cust_id = reqData.getGrpUca().getCustId(); // 集团客户id
        String user_id_b = reqData.getUca().getUserId(); // 成员用户id
        crtFlag = GroupImsUtil.getCreateMebFlag(cust_id, user_id_b);
    }

    /**
     * @Description:特殊服务处理
     */
    public void modRegSvcDataByYht() throws Exception
    {
        // super.infoRegDataSvc();
        // 当为固定电话时，多媒体已增加这两服务，也不用发HSS,ENUM,不加这两个服务
        if (!flag)
        {
            IDataset svcs = reqData.cd.getSvc();
            for (int i = 0; i < svcs.size(); i++)
            {
                IData svc = svcs.getData(i);
                if ("8172".equals(svc.getString("ELEMENT_ID")) || "8173".equals(svc.getString("ELEMENT_ID")))
                {
                    svcs.remove(i);
                }
            }
        }
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        data.put("RSRV_STR2", cntrxMembPoer);
        data.put("RSRV_STR10", sertype);
        if (flag)
        {
            data.put("RSRV_STR1", "1");// aliasId hss平台要求手机号码这个字段写死为1
        }
    }
}
