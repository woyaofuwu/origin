
package com.asiainfo.veris.crm.order.soa.person.busi.badness.blackwhite;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.ReporterBlackInfoQry;

public class BlackWhiteManageBean extends CSBizBean
{

    public IDataset addBlack(IData data) throws Exception
    {
        String serialNumber = data.getString("NEW_SERIAL_NUMBER");
        String blackType = data.getString("BLACK_TYPE");
        IDataset result = ReporterBlackInfoQry.qryAllBlackBySn(serialNumber);
        if (IDataUtil.isNotEmpty(result))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_81);
        }
        else
        {
            data.clear();
            data.put("IS_BLACK", blackType);
            data.put("SERIAL_NUMBER", serialNumber);
            data.put("IN_DATE", SysDateMgr.getSysTime());
            Dao.insert("TF_F_REPORTERBALCK", data, Route.CONN_CRM_CEN);
        }
        return null;
    }

    public IDataset delBlack(IData data) throws Exception
    {
        String serialNumber = data.getString("NEW_SERIAL_NUMBER");
        String blackType = data.getString("BLACK_TYPE");
        IDataset result = ReporterBlackInfoQry.qryAllBlackBySn(serialNumber);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_82);
        }
        else
        {
            if (StringUtils.equals(blackType, result.getData(0).getString("IS_BLACK")))
            {
                data.clear();
                data.put("SERIAL_NUMBER", serialNumber);
                Dao.delete("TF_F_REPORTERBALCK", data, new String[]
                { "SERIAL_NUMBER" }, Route.CONN_CRM_CEN);
            }
            else
            {
                CSAppException.apperr(DedInfoException.CRM_DedInfo_90, result.getData(0).getString("BLACK_TYPE"));
            }
        }
        return null;
    }

    public IDataset updBlack(IData data) throws Exception
    {
        String serialNumber = data.getString("NEW_SERIAL_NUMBER");
        String blackType = data.getString("BLACK_TYPE");
        IDataset result = ReporterBlackInfoQry.qryAllBlackBySn(serialNumber);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_82);
        }
        else
        {
            data.clear();
            data.put("IS_BLACK", blackType);
            data.put("SERIAL_NUMBER", serialNumber);
            Dao.save("TF_F_REPORTERBALCK", data, new String[]
            { "SERIAL_NUMBER" }, Route.CONN_CRM_CEN);
        }
        return null;
    }
}
