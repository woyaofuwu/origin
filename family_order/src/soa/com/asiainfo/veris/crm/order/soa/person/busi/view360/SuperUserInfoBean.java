
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UUserTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.SuperUserInfoDAO;

public class SuperUserInfoBean extends CSBizBean
{
    // 这个类用来做原来营业的用户查询

    /**
     * 导出文件
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IData exportUserInfo(IData param, Pagination pagination) throws Exception
    {
        IDataset outdataset = new DatasetList();
        IData out = new DataMap();
        IData outData = new DataMap();
        SuperUserInfoDAO dao = new SuperUserInfoDAO();
        IData qryData = new DataMap();
        String user_id = param.getString("USER_ID", "");
        qryData.put("USER_ID", param.getString("USER_ID", ""));
        qryData.put("PARTITION_ID", this.getPartitionId(param.getString("USER_ID", "")));
        outdataset = dao.qryUserInfoByUid(qryData, pagination);
        if (outdataset == null || outdataset.size() <= 0)
        {
            return out;
        }
        outData = outdataset.getData(0);
        out.put("SERIAL_NUMBER", outData.get("SERIAL_NUMBER"));
        out.put("OPEN_DATE", outData.get("OPEN_DATE"));
        out.put("DESTROY_TIME", outData.get("DESTROY_TIME"));
        out.put("BRAND_CODE", outData.get("BRAND_CODE"));

        // 用户类型
        String userType = UUserTypeInfoQry.getUserTypeByUserTypeCode(outData.getString("USER_TYPE_CODE"));

        out.put("USER_TYPE", userType);

        // 用户状态
        String cust_id = outData.getString("CUST_ID", "");
        String user_state_codeset = outData.getString("USER_STATE_CODESET", "");
        String stateExplain = "";
        if ("0".equals(user_state_codeset))
        {
            stateExplain = "开通";
            out.put("X_SVCSTATE_EXPLAIN", "开通");
        }
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", this.getPartitionId(user_id));
        IDataset outset = dao.getUserSvc(qryData, pagination);

        if (outset.size() <= 0)
        {
            // 获取用户最后的主体服务标识
            outset = new DatasetList();
            outset = dao.getUserLastSvc(qryData, pagination);
            if (outset.size() > 0)
                outData = (IData) outset.get(0);
        }
        else
        {
            outData = (IData) outset.get(0);
        }
        int serviceId = outData.getInt("SERVICE_ID", 0);
        String startDate = outData.getString("START_DATE", "");
        if (user_state_codeset.length() == 0) // 根据用户标识和服务标识获取服务状态编码列表
        {
            if ("".equals(user_id))
            {
                // common.error("解析服务状态:未输入用户标识！");
                CSAppException.apperr(CustException.CRM_CUST_995, "解析服务状态:未输入用户标识！");
            }
            qryData = new DataMap();
            qryData.put("USER_ID", user_id);
            qryData.put("PARTITION_ID", this.getPartitionId(user_id));
            qryData.put("SERVICE_ID", "" + serviceId);
            IDataset outset1 = dao.getUserSvcState(qryData, pagination);
            for (int i = 0; i < outset1.size(); i++)
            {
                user_state_codeset += ((IData) outset1.get(i)).getString("STATE_CODE");
            }
        }
        
        // //获取服务状态集对应状态名称
//        IDataset ox1 = USvcStateInfoQry.qryStateNameBySvcIdStateCode(String.valueOf(serviceId), user_state_codeset);
        IData ox1 = UpcCall.queryProdStaByCond("S", String.valueOf(serviceId), user_state_codeset);
//modify by lijun17        boolean firstState = true;
//        stateExplain = "";
//        for (int i = 0; i < ox1.size(); i++)
//        {
//            if (!firstState)
//                stateExplain += ",";
//            stateExplain += ((IData) ox1.get(i)).getString("STATE_NAME");
//            firstState = false;
//        }
        stateExplain = ox1.getString("STATUS_NAME","");
        if (!"".equals(stateExplain))
        {
            out.put("X_SVCSTATE_EXPLAIN", stateExplain);
        }
        // 客户资料信息
        qryData = new DataMap();
        qryData.put("CUST_ID", cust_id);
        qryData.put("PARTITION_ID", this.getPartitionId(cust_id));
        IDataset oo = dao.getPerson(qryData, pagination);
        if (oo.size() > 0)
        {
            out.put("CUST_NAME", oo.getData(0).getString("CUST_NAME"));
            out.put("PSPT_TYPE_CODE", oo.getData(0).getString("PSPT_TYPE_CODE"));
            out.put("PSPT_ID", oo.getData(0).getString("PSPT_ID"));
            out.put("PSPT_ADDR", oo.getData(0).getString("PSPT_ADDR"));
            out.put("HOME_ADDRESS", oo.getData(0).getString("HOME_ADDRESS"));
            out.put("POST_CODE", oo.getData(0).getString("POST_CODE"));
            out.put("PHONE", oo.getData(0).getString("PHONE"));
            out.put("FAX_NBR", oo.getData(0).getString("FAX_NBR"));
            out.put("EMAIL", oo.getData(0).getString("EMAIL"));
            out.put("CONTACT", oo.getData(0).getString("CONTACT"));

        }
        // 获取用户帐户信息
        qryData = new DataMap();
        qryData.put("USER_ID", user_id);
        qryData.put("PARTITION_ID", this.getPartitionId(user_id));
        IDataset o14 = dao.getPayrelationByUid(qryData, pagination);
        if (o14.size() > 0)
        {
            IData o14x = (IData) o14.get(0);
            qryData = new DataMap();
            qryData.put("ACCT_ID", o14x.getString("ACCT_ID", ""));
            qryData.put("PARTITION_ID", getPartitionId(o14x.getString("ACCT_ID", "")));
            IData o15 = dao.getAccountByAid(qryData, pagination);
            if (o15 != null)
            {
                out.put("PAY_NAME", o15.getString("PAY_NAME"));// 帐户名称
                out.put("BANK_CODE", o15.getString("BANK_CODE"));// 银行名称编码
                out.put("BANK_ACCT_NO", o15.getString("BANK_ACCT_NO"));// 银行帐号
                out.put("PAY_MODE_CODE", o15.getString("PAY_MODE_CODE"));// 帐户类型
            }
        }

        return out;

    }

    public String getPartitionId(String id)
    {
        if (id == null)
        {
            return null;
        }
        return String.valueOf(Long.parseLong(id) % (int) Math.pow(10.0D, 4));
    }

}
