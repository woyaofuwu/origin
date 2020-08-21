
package com.asiainfo.veris.crm.order.soa.group.groupunit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class VpnUnitSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 创建服务号码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset createParentVpnSN(IData data) throws Exception
    {
        IData retData = new DataMap();

        String vpnNo = VpnUnit.createParentVpnSN(data);

        retData.put("VPN_NO", vpnNo);

        return IDataUtil.idToIds(retData);
    }

    public IDataset createShortCode(IData data) throws Exception
    {
        IData retData = new DataMap();

        String shortCode = VpnUnit.createShortCode(data);

        retData.put("SHORT_CODE", shortCode);

        return IDataUtil.idToIds(retData);
    }

    public IDataset parentVpmnNoCrt(IData data) throws Exception
    {
        IData retData = new DataMap();

        for (int i = 0; i < 1000; i++)
        {

            String vpnNo = VpnUnit.createParentVpmnNoCrt(data);

            IData userInfo = UcaInfoQry.qryUserInfoBySnForGrp(vpnNo);

            if (IDataUtil.isEmpty(userInfo))
            {
                retData.put("VPN_NO", vpnNo);
                break;
            }

            if (i == 999)
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_22);
            }
        }

        return IDataUtil.idToIds(retData);
    }

    public IDataset shortCodeValidateVpn(IData data) throws Exception
    {
        IData retData = new DataMap();
        retData = VpnUnit.shortCodeValidateVpn(data);

        return IDataUtil.idToIds(retData);
    }

    public IDataset vpmnNoCrt(IData data) throws Exception
    {
        IData retData = new DataMap();
        String work_yype_code = data.getString("WORK_TYPE_CODE");

        String vpnNo = VpnUnit.vpmnNoCrt(work_yype_code);

        retData.put("VPN_NO", vpnNo);

        return IDataUtil.idToIds(retData);
    }

}
