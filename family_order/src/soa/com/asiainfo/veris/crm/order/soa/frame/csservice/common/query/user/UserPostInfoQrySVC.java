
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class UserPostInfoQrySVC extends CSBizService
{

    /**
     * 作用：根据user_id查找这个用户邮寄资料
     * 
     * @param param
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset getUserPostInfo(IData input) throws Exception
    {
        String id = input.getString("ID");
        String id_type = input.getString("ID_TYPE");
        IDataset output = UserPostInfoQry.qryUserPostInfo(id, id_type);
        return output;
    }

    /**
     * 通过服务号码，找出正常用户的邮寄资料 ITF_CRM_PostInfoQuery
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserPostInfoBySn(IData input) throws Exception
    {
        String servialNumber = input.getString("SERIAL_NUMBER", "");
        IDataset dataSet = new DatasetList();
        if (StringUtils.isNotBlank(servialNumber))
        {
            dataSet = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn(servialNumber));
            if (IDataUtil.isEmpty(dataSet))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入号码有误或者用户资料不存在！");
            }
            IData userData = IDataUtil.isNotEmpty(dataSet) ? dataSet.getData(0) : new DataMap();
            IData param = new DataMap();
            param.put("ID", userData.getString("USER_ID"));
            param.put("ID_TYPE", "1");
            dataSet.clear();
            dataSet = getUserPostInfo(param);
            if (dataSet.isEmpty() || dataSet.size() <= 0)
            {
                IData ida = new DataMap();
                ida.put("X_RESULTINFO", "该用户没有办理邮寄业务！");
                ida.put("X_RESULTCODE", "0");
                ida.put("X_RECORDNUM", "0");
                ida.put("X_RSPCODE", "0");
                ida.put("POST_TAG", "0");
                dataSet.add(ida);
                return dataSet;
            }
            return dataSet;
        }
        return dataSet;
    }

    /**
     * 作用：根据user_id查找这个用户邮寄资料
     * 
     * @param param
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public IDataset qryPostInfoForGrp(IData input) throws Exception
    {
        String id = input.getString("ID");
        String id_type = input.getString("ID_TYPE");
        IDataset output = UserPostInfoQry.qryUserPostInfoForGrp(id, id_type);
        return output;
    }

}
