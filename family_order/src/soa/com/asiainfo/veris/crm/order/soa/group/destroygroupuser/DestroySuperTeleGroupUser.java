
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroySuperTeleGroupUser extends DestroyGroupUser
{
    private IData superTeleParam = new DataMap();

    private IData mebSvcData = new DataMap();

    public DestroySuperTeleGroupUser()
    {

    }

    /**
     * 生成其它台帐数据
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 登记VPN用户信息
        super.infoRegDataVpn();

        if (IDataUtil.isNotEmpty(mebSvcData))
        {
            mebSvcData.put("TRADE_ID", getTradeId());
        }
    }

    /**
     * 构建总机号码调用成员服务数据
     * 
     * @param map
     * @throws Exception
     */
    public void makMebSvcData(IData map) throws Exception
    {
        if (StringUtils.isNotEmpty(superTeleParam.getString("E_SERIAL_NUMBER", "")))
        {
            mebSvcData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
            mebSvcData.put("SERIAL_NUMBER", superTeleParam.getString("E_SERIAL_NUMBER"));// 成员服务号码
            mebSvcData.put("MEB_MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 成员操作类型

            map.put("MEB_SVC_DATA", mebSvcData);
        }
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // 构建总机参数数据
        makSuperTeleParam(map);

        // 构建总机号码调用成员服务数据
        makMebSvcData(map);
    }

    /**
     * 构建总机参数数据
     * 
     * @param map
     * @throws Exception
     */
    public void makSuperTeleParam(IData map) throws Exception
    {
        String serialNumber = reqData.getUca().getUser().getRsrvStr3();// 总机号码
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", serialNumber);

        IData mofficeInfo = RouteInfoQry.getMofficeInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(mofficeInfo))
        {
            superTeleParam.put("EPARCHY_CODE", mofficeInfo.getString("EPARCHY_CODE"));
        }

        IDataset realList25 = RelaUUInfoQry.getSEL_USER_ROLEA(reqData.getUca().getUserId(), "2", "25", null, superTeleParam.getString("EPARCHY_CODE"));

        if (IDataUtil.isNotEmpty(realList25))
        {
            IData relaData = realList25.getData(0);

            String userId = relaData.getString("USER_ID_B", "");// 总机用户标识

            if (!userId.equals(reqData.getUca().getUser().getRsrvStr4()))
            {
                CSAppException.apperr(UUException.CRM_UU_51);
            }

            superTeleParam.put("E_SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B", ""));// 总机服务号码

            // 如果总机做了优惠变更的操作，优惠编码会变化，取最新的优惠编码
            inparam.clear();
            inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
            inparam.put("PRODUCT_ID", reqData.getUca().getProductId());

            IData discntData = UserDiscntInfoQry.getUserDiscntByUserId(inparam);
            if (IDataUtil.isNotEmpty(discntData))
            {
                superTeleParam.put("DISCNT_CODE", discntData.getString("DISCNT_CODE", ""));
            }
        }

        String rsrvStr6 = reqData.getUca().getUser().getRsrvStr6();
        rsrvStr6 = rsrvStr6 == null ? "集团名称" : rsrvStr6;
        superTeleParam.put("RSRV_STR6", rsrvStr6);// 集团客户名称

        String rsrvStr7 = reqData.getUca().getUser().getRsrvStr7();
        rsrvStr7 = rsrvStr7 == null ? "61860" : rsrvStr7;
        superTeleParam.put("RSRV_STR7", rsrvStr7);
        superTeleParam.put("RSRV_STR8", superTeleParam.getString("DISCNT_CODE"));// 总机优惠
        superTeleParam.put("REMARK", getAcceptTime().substring(0, 4) + getAcceptTime().substring(5, 7));// 总机优惠生效月份
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();
        tradeData.put("RSRV_STR6", superTeleParam.getString("RSRV_STR6"));
        tradeData.put("RSRV_STR7", superTeleParam.getString("RSRV_STR7"));
        tradeData.put("RSRV_STR8", superTeleParam.getString("RSRV_STR8"));
        tradeData.put("REMARK", superTeleParam.getString("REMARK"));
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        map.put("RSRV_STR6", superTeleParam.getString("RSRV_STR6"));
        map.put("RSRV_STR7", superTeleParam.getString("RSRV_STR7"));
        map.put("RSRV_STR8", superTeleParam.getString("RSRV_STR8"));
    }

}
