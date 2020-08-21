
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GroupBizIntercept;

public class GroupOrderService extends CSBizService
{
    private static final long serialVersionUID = -3084802265865402141L;

    public void destroy(IDataInput input, IDataOutput output) throws Exception
    {

        super.destroy(input, output);
    }

    public void setIntercept() throws Exception
    {
        setMethodIntercept(GroupBizIntercept.class.getName());
    }

    protected final void setRoute(IData input) throws Exception
    {
        boolean isOutNet = input.getBoolean("IS_OUT_NET", false);

        // 网外号码标识
        if (isOutNet)
        {
            String routeId = Route.getCrmDefaultDb();

            // 用户归属地州
            String userEparchyCode = input.getString(Route.USER_EPARCHY_CODE, "");

            // 用户归属地州, 如果为空, 默认为路由地州
            if (StringUtils.isBlank(userEparchyCode) && routeId.length() == 4 && StringUtils.isNumeric(routeId))
            {
                userEparchyCode = routeId;
            }

            // 交易地州
            String tradeEparchyCode = getTradeEparchyCode();

            // 接入渠道
            String inModeCode = CSBizBean.getVisit().getInModeCode();

            // 当前工号
            String staffId = CSBizBean.getVisit().getStaffId();

            // 非营业厅，或有全省权限，交易地州=用户地州
            if ((!"0".equals(inModeCode) || StaffPrivUtil.isSysProvince(staffId) == true) && StringUtils.isNotBlank(userEparchyCode))
            {
                tradeEparchyCode = userEparchyCode;
            }

            // HNAN 07XX 等非数字地州，都转换成用户归属地州
            if (!StringUtils.isNumeric(tradeEparchyCode) && StringUtils.isNotBlank(userEparchyCode))
            {
                tradeEparchyCode = userEparchyCode;
            }

            // 设置 服务路由ID
            setRouteId(routeId);

            // 设置 用户归属地州
            setUserEparchyCode(userEparchyCode);

            // 设置 交易地州
            setTradeEparchyCode(tradeEparchyCode);

            return;
        }

        super.setRoute(input);

    }
}
