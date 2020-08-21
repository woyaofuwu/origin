
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.crtUs;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.CreateGroupUserTransHAIN;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CreateVpmnGroupUserTransHAIN extends CreateGroupUserTransHAIN
{
    // 重写
    public void addSubInfoDataBefore(IData idata) throws Exception
    {

        IData genSnData = new DataMap();

        genSnData.put("PRODUCT_ID", idata.getString("PRODUCT_ID"));
        genSnData.put("VPMN_TYPE_CODE", idata.getString("VPMN_TYPE_CODE", "A"));

        String sn = genGrpSn(genSnData);

        idata.put("SERIAL_NUMBER", sn);
    }

    // 重写vmpn开户生成sn的方法
    private String genGrpSn(IData idata) throws Exception
    {

        String vpmnTypeCode = idata.getString("VPMN_TYPE_CODE");

        String vpnNo = "";
        for (int i = 0, iSize = 1000; i < iSize; i++)
        {
            vpnNo = VpnUnit.vpmnNoCrt(vpmnTypeCode);

            if (StringUtils.isEmpty(vpnNo))
            {
                break;
            }

            IData data = UcaInfoQry.qryUserMainProdInfoBySnForGrp(vpnNo);

            if (IDataUtil.isEmpty(data))
            {
                break;
            }
            if (i == 999)
            {
                CSAppException.apperr(GrpException.CRM_GRP_713, "该VPMN集团类型的VPN_NO已经全部被使用，请重新选择VPMN集团类型！");
            }
        }

        return vpnNo;
    }

}
