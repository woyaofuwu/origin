
package com.asiainfo.veris.crm.order.soa.group.batVpnUpgradetoIMS;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DESUtil;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsIMPUUtil;

public class BatVpnMemUpgrade extends ChangeMemElement
{

    private BatVpnMemUpgradeReqData reqData = null;

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new BatVpnMemUpgradeReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (BatVpnMemUpgradeReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setMEM_DISCNT(new DatasetList(map.getString("MEM_DISCNT_CODE", "[]")));
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        // 登记Other表
        infoRegDataCentreOther();
        regProductTradeData();
        // 登记IMPU表
        infoRegDataImpu();
        // 成员服务
        infoRegTradeSvc();
        // 成员优惠
        infoRegDiscnt();
    }

    /**
     * 添加优惠
     * 
     * @throws Exception
     */
    public void infoRegDiscnt() throws Exception
    {
        IData userDistData = new DataMap();

        if (IDataUtil.isEmpty(reqData.getMEM_DISCNT()))
        {
            CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_3);
        }
        IDataset discntsets = new DatasetList();
        IData memDis = reqData.getMEM_DISCNT().getData(0);
        userDistData.put("DISCNT_CODE", memDis.getString("ELEMENT_ID"));
        userDistData.put("USER_ID", reqData.getUca().getUserId()); // 成员user_id
        userDistData.put("USER_ID_A", reqData.getGrpUca().getUserId()); // 集团user_id
        userDistData.put("START_DATE", memDis.getString("START_DATE"));
        userDistData.put("END_DATE", memDis.getString("END_DATE"));
        userDistData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        userDistData.put("PACKAGE_ID", memDis.getString("PACKAGE_ID")); //
        userDistData.put("PRODUCT_ID", memDis.getString("PRODUCT_ID"));
        userDistData.put("DIVERSIFY_ACCT_TAG", "1"); // 多账期修改:已经处理过元素的时间
        userDistData.put("REMARK", "集团V网升级融合V网 成员批量操作");
        String ind_id = SeqMgr.getInstId();
        userDistData.put("INST_ID", ind_id);
        discntsets.add(userDistData);

        // 删除原优惠
        IDataset discntset = UserDiscntInfoQry.getUserProductDis(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
        if (IDataUtil.isNotEmpty(discntset))
        {
            for (int i = 0; i < discntset.size(); i++)
            {
                IData dis = discntset.getData(i);
                if ("800001".equals(dis.getString("PRODUCT_ID", "")))
                {
                    // 退订原有优惠
                    dis.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    dis.put("END_DATE", this.getAcceptTime());
                    dis.put("ELEMENT_ID", dis.getString("DISCNT_CODE", ""));
                    dis.put("REMARK", "集团V网升级融合V网 成员批量操作");
                    discntsets.add(dis);
                }
            }
        }

        this.addTradeDiscnt(discntsets);
    }

    public void infoRegTradeSvc() throws Exception
    {
        String grpUserId = reqData.getGrpUca().getUserId();
        String memUserId = reqData.getUca().getUserId();
        String memEparchyCode = reqData.getUca().getUser().getEparchyCode();
        // 处理集团成员服务变更
        IData svcparams = new DataMap();
        svcparams.put("USER_ID", memUserId);
        svcparams.put("EPACHY_CODE", memEparchyCode);
        IDataset svcDataset = UserSvcInfoQry.queryUserSvcByUserIdAll(memUserId);
        IDataset svcds = new DatasetList();
        if (IDataUtil.isNotEmpty(svcDataset))
        {
            for (int i = 0; i < svcDataset.size(); i++)
            {
                IData temp = svcDataset.getData(i);
                if ("860".equals(temp.getString("SERVICE_ID", "")) || "861".equals(temp.getString("SERVICE_ID", "")))
                {
                    // 退订原有服务
                    temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    temp.put("END_DATE", this.getAcceptTime());
                    temp.put("ELEMENT_ID", temp.getString("SERVICE_ID", ""));
                    temp.put("REMARK", "集团V网升级融合V网 成员批量操作");
                    svcds.add(temp);
                }
            }
        }
        // 新增融合V网860 861服务
        IData svc860 = new DataMap();
        IData svc861 = new DataMap();
        svc860.put("USER_ID", memUserId);// 用户标识
        svc860.put("USER_ID_A", grpUserId);// 用户标识A
        svc860.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        svc860.put("MAIN_TAG", "0");// 主体服务标志：0-否，1-是
        svc860.put("CANCEL_TAG", "0");
        svc860.put("ENABLE_TAG", "0");
        svc860.put("INST_ID", SeqMgr.getInstId());
        svc860.put("START_DATE", this.getAcceptTime());
        svc860.put("END_DATE", SysDateMgr.getTheLastTime());
        svc860.put("ELEMENT_TYPE_CODE", "S");
        svc860.put("ELEMENT_ID", "860");
        svc860.put("PRODUCT_ID", "22000020"); // 融合V网集团成员产品 22000020
        svc860.put("RELATION_TYPE_CODE", "20");
        svc860.put("PACKAGE_ID", "33001971"); // 融合V网集团成员产品服务包 33001971
        svc860.put("REMARK", "集团V网升级融合V网 成员批量操作");

        svc861.put("USER_ID", memUserId);// 用户标识
        svc861.put("USER_ID_A", grpUserId);// 用户标识A
        svc861.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        svc861.put("MAIN_TAG", "0");// 主体服务标志：0-否，1-是
        svc861.put("CANCEL_TAG", "0");
        svc861.put("ENABLE_TAG", "0");
        svc861.put("INST_ID", SeqMgr.getInstId());
        svc861.put("START_DATE", this.getAcceptTime());
        svc861.put("END_DATE", SysDateMgr.getTheLastTime());
        svc861.put("ELEMENT_TYPE_CODE", "S");
        svc861.put("ELEMENT_ID", "861");
        svc861.put("PRODUCT_ID", "22000020"); // 融合V网集团成员产品 22000020
        svc861.put("RELATION_TYPE_CODE", "20");
        svc861.put("PACKAGE_ID", "33001971"); // 融合V网集团成员产品服务包 33001971
        svc861.put("REMARK", "集团V网升级融合V网 成员批量操作");
        svcds.add(svc860);
        svcds.add(svc861);
        addTradeSvc(svcds);

    }

    /**
     * Centrex 登记平台other表 写入other表 不走服务开通
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData CentreData = new DataMap();
        CentreData.put("USER_ID", reqData.getUca().getUserId());
        CentreData.put("RSRV_VALUE_CODE", "CNTRX"); // domain域
        CentreData.put("RSRV_VALUE", "Centrex成员业务");
        CentreData.put("RSRV_STR1", ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId()));// 产品ID
        CentreData.put("RSRV_STR9", "0"); // 服务id//填0 不走服务开通
        CentreData.put("RSRV_STR11", "FF"); // 操作类型 //填FF 不走服务开通 此处CALLPF做特殊处理 不走服务开通
        CentreData.put("OPER_CODE", "FF");
        CentreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        CentreData.put("START_DATE", this.getAcceptTime());
        CentreData.put("END_DATE", SysDateMgr.getTheLastTime());
        CentreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(CentreData);
        addTradeOther(dataset);
    }

    public void regProductTradeData() throws Exception
    {
        String memProductId = "800001";
        IDataset productds = new DatasetList();
        // 退订已有 8000产品
        String memUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        String memEparchyCode = reqData.getUca().getUser().getEparchyCode();

        IDataset productset = UserProductInfoQry.GetUserProductInfo(memUserId, grpUserId, memProductId, "12", memEparchyCode);
        if (IDataUtil.isNotEmpty(productset))
        {
            IData temp = productset.getData(0);
            temp.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            temp.put("MAIN_TAG", "0");
            temp.put("END_DATE", this.getAcceptTime());
            temp.put("REMARK", "集团V网升级融合V网 成员批量操作");
            productds.add(temp);
        }

        // 新增22000020 融合V网集团成员产品
        IData Pro800001 = new DataMap();
        Pro800001.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        Pro800001.put("USER_ID", memUserId);
        Pro800001.put("USER_ID_A", grpUserId);
        Pro800001.put("PRODUCT_ID", "22000020");
        Pro800001.put("BRAND_CODE", "CTRX");
        Pro800001.put("PRODUCT_MODE", "12");
        Pro800001.put("START_DATE", this.getAcceptTime());
        Pro800001.put("UPDATE_TIME", this.getAcceptTime());
        Pro800001.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
        Pro800001.put("MAIN_TAG", "0");
        Pro800001.put("INST_ID", SeqMgr.getInstId());
        Pro800001.put("REMARK", "集团V网升级融合V网 成员批量操作");
        productds.add(Pro800001);

        super.addTradeProduct(productds);
    }

    /**
     * impu台帐表录入
     * 
     * @throws Exception
     */
    public void infoRegDataImpu() throws Exception
    {
        String memUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        IDataset uuInfos = RelaUUInfoQry.qryRelationUUAll(grpUserId, memUserId, "20");
        String roleCodeB = "1";
        String shortCode = "";
        if (IDataUtil.isNotEmpty(uuInfos))
        {
            roleCodeB = uuInfos.getData(0).getString("ROLE_CODE_B");
            shortCode = uuInfos.getData(0).getString("SHORT_CODE");
        }
        String rsStr4 = roleCodeB + "|" + shortCode;
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(memUserId, reqData.getUca().getUser().getEparchyCode());
        if (IDataUtil.isEmpty(impuInfo))
        {
            IData inData = new DataMap();

            inData.put("INST_ID", SeqMgr.getInstId());
            inData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            inData.put("USER_ID", reqData.getUca().getUserId());

            StringBuilder strImpi = new StringBuilder("");
            StringBuilder strTel = new StringBuilder("");
            StringBuilder strSip = new StringBuilder("");
            String custInfoTeltype = "3"; // 移动用户

            // 号码是+86+去0的区号＋号码,如果开的号码是073166666666等号，把0去掉
            String serialNumber = reqData.getUca().getUser().getSerialNumber();
            /*
             * if (serialNumber.substring(0, 1).equals("0")) { serialNumber = serialNumber.substring(1); }
             */
            GroupImsIMPUUtil.genImsIMPI(serialNumber, strImpi, custInfoTeltype);
            GroupImsIMPUUtil.genImsIMPU(serialNumber, strTel, strSip, custInfoTeltype);

            inData.put("TEL_URL", strTel.toString());
            inData.put("SIP_URL", strSip.toString());
            inData.put("IMPI", strImpi.toString());
            inData.put("IMS_USER_ID", serialNumber);

            // IMS鉴权码
            String imsPassword = StrUtil.getRandomNumAndChar(15);//生成IMS密码15位 由字母+数字组成，区分字母大小写，且是由BOSS系统随机生成。 必须加密  关于优化IMS业务开通默认密码的需求
            inData.put("IMS_PASSWORD", DESUtil.encrypt(imsPassword)); // IMS门户网站密码 imsPassword
            inData.put("START_DATE", getAcceptTime());
            inData.put("END_DATE", SysDateMgr.getTheLastTime());

            // 用户类型:如固定电话用户、移动电话用户 0-固定用户 1-移动用户 2-PC客户端
            String userType = "1";

            inData.put("RSRV_STR1", userType);

            // 登记一个ENUM的信息
            String tmp = strTel.toString();
            tmp = tmp.replaceAll("\\+", "");
            char[] c = tmp.toCharArray();
            String str2 = "";
            for (int i = c.length - 1; i >= 1; i--)
            {

                str2 += String.valueOf(c[i]);
                str2 += ".";
            }
            str2 += "6.8.e164.arpa";
            String str3 = "";
            for (int i = 3; i >= 1; i--)
            {

                str3 += String.valueOf(c[i]);
                str3 += ".";
            }
            str3 += "6.8.e164.arpa";
            inData.put("RSRV_STR2", str2);
            inData.put("RSRV_STR3", str3);
            inData.put("RSRV_STR4", rsStr4);
            inData.put("RSRV_STR5", "");
            addTradeImpu(inData);
        }
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        // 设置业务类型
        return "2529";
    }
}
