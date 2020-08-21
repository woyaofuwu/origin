
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.buildrequest;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberSingleReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.buildrequest.BuildBaseFamilyBusiReqData;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.FamilyCallerBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * @Description 单个成员新增构建类
 * @Auther: zhenggang
 * @Date: 2020/7/31 18:14
 * @version: V1.0
 */
public class BuildAddSingleFamilyMemberReqData extends BuildBaseFamilyBusiReqData implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // 1.入参校验
        checkInParam(param);
        // 成员自己台账返回的数据
        FamilyMemberSingleReqData reqData = (FamilyMemberSingleReqData) brd;
        reqData.setMiddleTradeId(param.getString("MIDDLE_TRADE_ID", ""));
        reqData.setMiddleEparchyCode(param.getString("MIDDLE_EPARCHY_CODE", ""));
        super.buildBusiRequestData(param, brd);
    }

    private void checkInParam(IData param) throws Exception
    {
        IDataUtil.chkParam(param, "TOP_TRADE_ID");
        IDataUtil.chkParam(param, "TOP_EPARCHY_CODE");
        IDataUtil.chkParam(param, "FAMILY_SERIAL_NUMBER");
        IDataUtil.chkParam(param, "FAMILY_USER_ID");
        IDataUtil.chkParam(param, "FAMILY_ACCT_ID");
        IDataUtil.chkParam(param, "FAMILY_PRODUCT_ID");
        IDataUtil.chkParam(param, "MANAGER_SN");
        IDataUtil.chkParam(param, "ORDER_TYPE_CODE");
        // 到单删接口里面角色信息必须传
        IDataUtil.chkParam(param, "ROLE_CODE");
        IDataUtil.chkParam(param, "MEMBER_MAIN_SN");
        // FAMILY_PARAM
        IDataUtil.chkParam(param, "BUSI_TYPE");
        // 成员关系实例ID
        IDataUtil.chkParam(param, "FAMILY_MEMBER_INST_ID");
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        // ---------------------------请勿修改-------------------------------------
        param.put("FAMILY_MEMBER_INST_ID", SeqMgr.getInstId());
        // ----------------------------------------------------------------------
        IData role = FamilyCallerBean.busiParamTrans(param);
        if (IDataUtil.isNotEmpty(role))
        {
            IData result = callRoleRegSvc(role);

            if (StringUtils.isNotEmpty(result.getString("TRADE_ID", "")))
            {
                param.put("MIDDLE_TRADE_ID", result.getString("TRADE_ID"));
                param.put("MIDDLE_EPARCHY_CODE", result.getString("DB_SOURCE"));
            }
        }
        return super.buildUcaData(param);
    }

    private IData callRoleRegSvc(IData role) throws Exception
    {
        String route = StringUtils.isBlank(role.getString(Route.ROUTE_EPARCHY_CODE)) ? CSBizBean.getTradeEparchyCode() : role.getString(Route.ROUTE_EPARCHY_CODE);

        role.put(Route.ROUTE_EPARCHY_CODE, route);

        if (StringUtils.isNotBlank(role.getString("CALL_REGSVC")))
        {
            IDataset results = CSAppCall.call(role.getString("CALL_REGSVC"), role);
            return results.first();
        }
        return new DataMap();
    }

    @Override
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyMemberSingleReqData();
    }
}
