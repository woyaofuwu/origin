
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossMemberChgSync;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeMember.ChangeBBossMemSVC;

public class BbossMemberChgSync extends CSBizBean
{
    /**
     * 一卡通成员变更、400白名单成员变更
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset bbossDataUdr(IData inParam) throws Exception
    {
        String gvalueCode = IDataUtil.getMandaData(inParam, "GVALUE_CODE");// 产品订单编码/产品订购关系编码
        inParam.put("PRODUCTID", gvalueCode);
        inParam.put("ORDER_NO", gvalueCode);

        String itemFieldName = IDataUtil.getMandaData(inParam, "ITEM_FIELD_NAME");// 订单来源
        inParam.put("ANTI_INTF_FLAG", itemFieldName);

        IDataUtil.getMandaData(inParam, "SERIAL_NUMBER");// 成员号码

        String operCode = IDataUtil.getMandaData(inParam, "OPER_CODE");// 操作类型
        inParam.put("ACTION", operCode);

        IDataUtil.getMandaData(inParam, "USER_TYPE");// 成员类型

        String compTag = inParam.getString("COMP_TAG", "");// 成员群组号
        inParam.put("RSRV_STR10", compTag);

        // String startData = IDataUtil.getMandaData(inParam, "START_DATE");// 期望生效时间
        // inParam.put("EFFDATE", startData);

        String paramType = inParam.getString("PARM_TYPE", "");// 属性编码
        inParam.put("RSRV_STR11", paramType);

        String paramName = inParam.getString("PARM_NAME", "");// 属性名
        inParam.put("RSRV_STR12", paramName);

        String paramValue = inParam.getString("PARM_VALUE", "");// 属性值
        inParam.put("RSRV_STR13", paramValue);

        String serailNumber = inParam.getString("SERIAL_NUMBER");
        // 获取路由地州
        String eparchy_code = RouteInfoQry.getEparchyCodeBySn(serailNumber);

        inParam.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);

        // 调用服务
        ChangeBBossMemSVC svc = new ChangeBBossMemSVC();
        IDataset datast = svc.crtOrder(inParam);

        return datast;
    }

}
