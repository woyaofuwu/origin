
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeCentrexSuperTeleMebElement extends ChangeMemElement
{

    private IData centrexProductParam = new DataMap();

    public ChangeCentrexSuperTeleMebElement()
    {

    }

    /**
     * 生成登记信息
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        // 处理产品参数信息
        setProductParam();
    }

    /**
     * 生成其它台帐数据
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理UU关系
        infoRegDataRelation();

        // 处理VPN成员信息
        infoRegDataVpnMeb();

        // 处理成员报文信息
        infoRegDataCentrexMeb();

    }

    /**
     * 处理发送报文信息
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexMeb() throws Exception
    {

        String shortCodeState = centrexProductParam.getString("SHORTCODESTATE", ""); // 是否修改了短号标记
        String isModifyTelOper = centrexProductParam.getString("IS_MODIFYTELOPER", ""); // 是否做了话务员操作

        if (shortCodeState.equals(TRADE_MODIFY_TAG.MODI.getValue()))
        {
            // 修改融合总机成员报文
            infoRegDataCentrexOther("25");
        }

        if ("21".equals(isModifyTelOper) || "22".equals(isModifyTelOper))
        {
            // 新增或删除话务员报文
            infoRegDataCentrexOther(isModifyTelOper);
        }
    }

    /**
     * 处理Other表,发报文信息
     * 
     * @param operCode
     *            操作类型
     * @throws Exception
     */
    public void infoRegDataCentrexOther(String operCode) throws Exception
    {

        IData centreData = new DataMap();
        centreData.put("USER_ID", reqData.getUca().getUserId());
        centreData.put("RSRV_VALUE_CODE", "CNTRX"); // domain域
        centreData.put("RSRV_VALUE", "Centrex成员总机业务");
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", "6301"); // 服务id
        centreData.put("RSRV_STR11", operCode); // 操作类型
        centreData.put("OPER_CODE", operCode); // 操作类型
        centreData.put("INST_ID", SeqMgr.getInstId());
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", getAcceptTime());
        centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeOther(centreData);
    }

    /**
     * 成员UU关系的处理
     * 
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {

        String userIdA = reqData.getGrpUca().getUserId();
        String userIdB = reqData.getUca().getUserId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IDataset uuInfos = RelaUUInfoQry.qryUU(userIdA, userIdB, relationTypeCode, null, null);
        if (IDataUtil.isEmpty(uuInfos))
        {
            return;
        }

        IData uuInfo = uuInfos.getData(0);
        String roleCodeB = uuInfo.getString("ROLE_CODE_B", "");

        String shortCodeState = centrexProductParam.getString("SHORTCODESTATE", ""); // 获取短号状态
        String isSuperTelOper = centrexProductParam.getString("IS_SUPERTELOPER", ""); // 话务员标志

        boolean isModifyTelOper = false;

        if (roleCodeB.equals("3") && !"on".equals(isSuperTelOper))
        {
            isModifyTelOper = true;
            centrexProductParam.put("IS_MODIFYTELOPER", "22");// 删除话务员
        }

        if (!roleCodeB.equals("3") && "on".equals(isSuperTelOper))
        {
            isModifyTelOper = true;
            centrexProductParam.put("IS_MODIFYTELOPER", "21");// 新增话务员
        }

        if (isModifyTelOper && !shortCodeState.equals(TRADE_MODIFY_TAG.MODI.getValue()))
        {
            return;
        }

        if (shortCodeState.equals(TRADE_MODIFY_TAG.MODI.getValue()))
        {
            uuInfo.put("SHORT_CODE", centrexProductParam.getString("SHORT_CODE"));
        }

        if ("on".equals(isSuperTelOper))
        {
            uuInfo.put("ROLE_CODE_B", "3");
        }
        else
        {
            uuInfo.put("ROLE_CODE_B", "1");
        }

        uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeRelation(uuInfo);
    }

    /**
     * 处理VPN成员信息
     * 
     * @throws Exception
     */
    public void infoRegDataVpnMeb() throws Exception
    {

        String shortCodeState = centrexProductParam.getString("SHORTCODESTATE", ""); // 获取短号状态
        String isSuperTelOper = centrexProductParam.getString("IS_SUPERTELOPER", ""); // 是否修改了话务员

        if (!shortCodeState.equals(TRADE_MODIFY_TAG.MODI.getValue()) && !"on".equals(isSuperTelOper))
            return;

        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("USER_ID_A", userIdA);

        IData vpnMebInfo = UserVpnInfoQry.getMemberVpnByUserId(inparam);

        if (IDataUtil.isEmpty(vpnMebInfo))
        {
            return;
        }

        if (shortCodeState.equals(TRADE_MODIFY_TAG.MODI.getValue()))
        {
            vpnMebInfo.put("SHORT_CODE", centrexProductParam.getString("SHORT_CODE", ""));
        }

        if ("on".equals(isSuperTelOper))
        {
            vpnMebInfo.put("RSRV_STR2", centrexProductParam.getString("SUPERTELNUMBER", ""));
            vpnMebInfo.put("RSRV_STR3", centrexProductParam.getString("OPERATORPRIONTY", ""));
        }
        vpnMebInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeVpnMeb(vpnMebInfo);
    }

    /**
     * 获取产品参数信息
     * 
     * @return IData
     * @throws Exception
     */
    public void setProductParam() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData productParam = reqData.cd.getProductParamMap(baseMemProduct);

        centrexProductParam.put("SUPERTELNUMBER", productParam.getString("SUPERTELNUMBER", ""));// 总机号码
        centrexProductParam.put("OPERATORPRIONTY", productParam.getString("OPERATORPRIONTY", ""));// 优先级
        centrexProductParam.put("IS_SUPERTELOPER", productParam.getString("IS_SUPERTELOPER", ""));// 是否话务员

        String oldShortCode = "";
        IDataset userResInfoList = UserResInfoQry.getUserProductRes(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), null);
        if (IDataUtil.isNotEmpty(userResInfoList))
        {
            oldShortCode = userResInfoList.getData(0).getString("RES_CODE", "");
        }

        String shortCode = productParam.getString("SHORT_CODE", "");
        if (!shortCode.equals("") && !shortCode.equals(oldShortCode))
        {
            centrexProductParam.put("SHORTCODESTATE", TRADE_MODIFY_TAG.MODI.getValue());// 短号状态标志为修改
            centrexProductParam.put("SHORT_CODE", shortCode);// 短号
        }
    }
}
