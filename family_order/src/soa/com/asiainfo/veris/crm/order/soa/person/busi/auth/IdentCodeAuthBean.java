
package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.IdentcardInfoQry;

public class IdentCodeAuthBean extends CSBizBean
{

    /**
     * 身份凭证鉴权
     * 
     * @param pd
     * @param inparams
     * @return
     * @throws Exception
     * @author dengyong
     */
    public void identAuth(IData data) throws Exception
    {
        IData res = IdentcardInfoQry.checkIdentInfoByIdent(data.getString("IDENT_CODE", ""), data.getString("BUSINESS_CODE", ""), data.getString("SERIAL_NUMBER", ""), data.getString("IDENT_CODE_TYPE", ""), data.getString("IDENT_CODE_LEVEL", ""));

        if (null == res)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_915);
        }

        data.put("IDENT_CODE_TYPE", res.getString("IDENT_CODE_TYPE", ""));

        if (data.getString("IDENT_CODE_TYPE").equals("02") && 0 == data.getString("BUSINESS_CODE", "").length())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1103);
        }

        // callUspRequest(pd,data,"identAuth");
    }

    /**
     * 检查身份凭证
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData identAuthUacp(IData data) throws Exception
    {
            IData result = new DataMap();
            // 参数检查
            IDataUtil.chkParam(data, "IDVALUE");

            IDataset identCodeInfoLst = (IDataset) data.get("IDENT_CODE_INFO");
            if (IDataUtil.isEmpty(identCodeInfoLst))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口参数检查，输入参数[IDENT_CODE_INFO]的数据集合为空");
            }

            for (int i = 0; i < identCodeInfoLst.size(); ++i)
            {
                data.put("IDENT_CODE", identCodeInfoLst.getData(i).getString("IDENT_CODE", ""));
                data.put("BUSINESS_CODE", identCodeInfoLst.getData(i).getString("BUSINESS_CODE", ""));
                data.put("IDENT_CODE_LEVEL", identCodeInfoLst.getData(i).getString("IDENT_CODE_LEVEL", ""));
                data.put("IDENT_CODE_TYPE", identCodeInfoLst.getData(i).getString("IDENT_CODE_TYPE", ""));

                identAuth(data);
            }
            
            // 获取用户信息
            String serialNumber = data.getString("SERIAL_NUMBER");
            IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_573, serialNumber);
            }

            // 返回操作结果
            // 返回操作结果
            result.put("X_RSPCODE", "0");
            return result;
    }
}
