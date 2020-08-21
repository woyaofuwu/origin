
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

/**
 * 互联网专线（GPON统付）成员批量注销
 * 
 * @author liujy
 */
public class GrpBatCancelGponMenberSVC extends GroupBatService
{
    private static final long serialVersionUID = -4664521487269632999L;

    private static final String SERVICE_NAME = "CS.DestroyGroupMemberSvc.destroyGroupMember";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {

        svcName = SERVICE_NAME;
        batData.put(BIZ_CTRL_TYPE, BizCtrlType.DestoryMember);
    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {
        checkParam(batData);

        String user_id = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkMemberUCABySerialNumber(inparam);

        // 判断服务号码状态
        if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_471, serial_number);
        }

        // 判断成员号码是否为集团号码
        if (isGroupSerialNumber(serial_number))
        {
            CSAppException.apperr(GrpException.CRM_GRP_120, serial_number);
        }

        // 校验是否是集团成员
        IData data = new DataMap();
        String user_id_b = getMebUcaData().getUserId(); // 成员user_id
        data.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        data.put("USER_ID", user_id);// 集团用户编码
        data.put("MEM_USER_ID", user_id_b);// 成员用户编码
        data.put("MEM_EPARCHY_CODE", getMebUcaData().getUser().getEparchyCode());// 成员用户地州

        boolean uuFlag = super.chkIsExitsRelation(data);// 不存在UU关系返回false

        if (!uuFlag)
        {
            CSAppException.apperr(GrpException.CRM_GRP_51);
        }

    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("REMARK", batData.getString("REMARK", "pushmail批量成员注销"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));

        // 是否退出对应的集团资料， 0 不退出 1 退出
        String joinIn = condData.getString("JOIN_IN");
        if (StringUtils.isNotBlank(joinIn))
            svcData.put("JOIN_IN", joinIn);
        // 业务是否预约 true 预约到账期末执行 false 非预约工单
        String ifBooking = condData.getString("ifBooking");
        if (StringUtils.isNotBlank(ifBooking))
            svcData.put("IF_BOOKING", ifBooking);

    }

    /**
     * 校验参数必填
     * 
     * @param batData
     * @throws Exception
     */
    public void checkParam(IData batData) throws Exception
    {

        IDataUtil.chkParam(condData, "PRODUCT_ID");

        IDataUtil.chkParam(condData, "USER_ID");

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");

    }

}
