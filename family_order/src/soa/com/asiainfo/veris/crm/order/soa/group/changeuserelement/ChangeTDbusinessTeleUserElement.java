
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeTDbusinessTeleUserElement extends ChangeUserElement
{

    private IData paramData;

    /**
     * 构造函数
     */
    public ChangeTDbusinessTeleUserElement()
    {

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String curProductId = reqData.getUca().getProductId();

        paramData = (IData) Clone.deepClone(reqData.cd.getProductParamMap(curProductId));
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        // 一些个性化参数存放到主台帐和USER表的预留字段里, 因此将产品参数信息清空
        reqData.cd.putProductParamList(curProductId, new DatasetList());

    }

    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        String userId = reqData.getUca().getUser().getUserId();// 产品用户编号

        // 根据产品用户编号获取产品用户信息
        IData users = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

        String managerNo = paramData.getString("CUST_MANAGER", "");
        String phone = paramData.getString("DETMANAGERPHONE", "");
        String info = paramData.getString("DETMANAGERINFO", "");
        String addr = paramData.getString("DETADDRESS", "");

        if (managerNo.equals(users.getString("RSRV_STR5", "")) && phone.equals(users.getString("RSRV_STR8", "")) && info.equals(users.getString("RSRV_STR7", "")) && addr.equals(users.getString("RSRV_STR9", "")))
        {
            reqData.setIsChange(false);
        }
        else
        {
            reqData.setIsChange(true);
        }

    }

    public IData getTradeUserExtendData() throws Exception
    {

        IData userExtenData = super.getTradeUserExtendData();

        userExtenData.put("RSRV_STR5", paramData.getString("CUST_MANAGER", ""));
        userExtenData.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        userExtenData.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        userExtenData.put("RSRV_STR9", paramData.getString("DETADDRESS", ""));
        userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        return userExtenData;
    }

}
