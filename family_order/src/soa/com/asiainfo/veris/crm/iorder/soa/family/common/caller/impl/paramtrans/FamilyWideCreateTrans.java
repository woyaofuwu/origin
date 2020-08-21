
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.paramtrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IParamTrans;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.ParamTransUtil;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
 * 家庭开户，转换为标准参数
 * 
 * @author duhj
 */
public class FamilyWideCreateTrans implements IParamTrans
{

    @Override
    public IData getTransParamters(IData input) throws Exception
    {
        IData widenetData = new DataMap();

        widenetData.putAll(input);

        // 校验入参
        checkInParam(widenetData);

        // 产品信息转换
        transProudctInfo(widenetData);

        // 设置默认参数
        setDefaultParamValue(widenetData);

        // IMS参数转换 TV+参数转换
        transImsAndTVTradeParam(widenetData,input);

        return widenetData;
    }

    /**
     * 设置默认参数值
     * 
     * @param input
     * @throws Exception
     */
    public void setDefaultParamValue(IData input) throws Exception
    {
        // 传递登记服务名
        input.put("CALL_REGSVC", "SS.MergeWideUserCreateRegSVC.tradeReg");// 宽带开户接口
        input.put("TRADE_TYPE_CODE", "600");
        String memberMainSn = input.getString("MEMBER_MAIN_SN");
        input.put("SERIAL_NUMBER", memberMainSn);
        String serialNumber = input.getString("SERIAL_NUMBER");
        input.put("WIDE_SERIAL_NUMBER", serialNumber);// 宽带号码,这里其实也是手机号码，后面会拼KD

    }

    /**
     * 转换产品信息
     * 
     * @param input
     * @throws Exception
     */
    public void transProudctInfo(IData input) throws Exception
    {
        String wideProductId = ParamTransUtil.findRoleMainOffer(input);
        if (StringUtils.isEmpty(wideProductId))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_9);
        }
        input.put("WIDE_PRODUCT_ID", wideProductId);
        // 获取主产品下的必选元素
        String svcName = "CS.SelectedElementSVC.getWidenetUserOpenElements";
        IData data = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("NEW_PRODUCT_ID", wideProductId);
        data.put("TRADE_TYPE_CODE", FamilyConstants.ROLE_TRADE_TYPE.WIDE_OPEN);
        IDataset wideElementsInfos = CSAppCall.call(svcName, data);
        if (IDataUtil.isNotEmpty(wideElementsInfos))
        {
            IDataset wideSelectedElements = wideElementsInfos.getData(0).getDataset("SELECTED_ELEMENTS");
            input.put("SELECTED_ELEMENTS", wideSelectedElements);
        }
    }

    /**
     * IMS固话开户参数转换,补充宽带的基本信息，传递给下一层
     * 
     * @param input
     * @throws Exception
     */
    public void transImsAndTVTradeParam(IData param,IData input) throws Exception
    {
        if (input.containsKey("SUB_ROLES"))
        {
            IDataset subRoles = new DatasetList(input.getString("SUB_ROLES", "[]"));

            if (IDataUtil.isNotEmpty(subRoles))
            {
                for (Object obj : subRoles)
                {
                    IData subRole = (DataMap) obj;
                    subRole.put(Route.ROUTE_EPARCHY_CODE, subRole.getString(KeyConstants.EPARCHY_CODE));
                    subRole.put("CONTACT", input.getString("CONTACT"));
                    subRole.put("CONTACT_PHONE", input.getString("CONTACT_PHONE"));
                    subRole.put("PHONE", input.getString("PHONE"));
                    subRole.put("STAND_ADDRESS", input.getString("STAND_ADDRESS"));
                    subRole.put("DETAIL_ADDRESS", input.getString("DETAIL_ADDRESS"));
                    subRole.put("STAND_ADDRESS_CODE", input.getString("STAND_ADDRESS_CODE"));
                    subRole.put("AREA_CODE", input.getString("AREA_CODE"));
                    subRole.put("WIDE_PRODUCT_TYPE", input.getString("WIDE_PRODUCT_TYPE"));
                    subRole.put("DEVICE_ID", input.getString("DEVICE_ID"));
                    subRole.put("BUSI_TYPE", input.getString("BUSI_TYPE"));
                }
            }

            input.put("SUB_ROLES", subRoles);
            param.put("SUB_ROLES", subRoles);
        }

    }

    /**
     * 参数校验
     * 
     * @param input
     * @throws Exception
     */
    public void checkInParam(IData input) throws Exception
    {

        IDataUtil.chkParam(input, "SERIAL_NUMBER");// 宽带开户的手机号码

        IDataUtil.chkParam(input, "STAND_ADDRESS");// 标准地址

        IDataUtil.chkParam(input, "DETAIL_ADDRESS");// 详细地址

        IDataUtil.chkParam(input, "WIDENET_PAY_MODE");// 支付模式 P:立即支付 A:先装后付

    }
}
