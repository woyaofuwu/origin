
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.mvalue;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryMValueQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QueryUserMvalueBean extends CSBizBean
{

    public IData isPrint(String staffId, String eparchycode) throws Exception
    {
        QueryMValueQry dao = new QueryMValueQry();
        IDataset dataset = dao.isPrint(staffId, eparchycode);
        int count = 0;
        if (IDataUtil.isNotEmpty(dataset))
        {
            count = Integer.parseInt(dataset.getData(0).getString("THECOUNT"));
        }
        IData result = new DataMap();
        result.put("ISPRINT", count);
        return result;
    }

    /**
     * 得到cycle list 列表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryCycleList() throws Exception
    {
        QueryMValueQry dao = new QueryMValueQry();
        return dao.queryCycleList();
    }

    /**
     * 查询用户M值
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryMvalue(IData data, Pagination pagination) throws Exception
    {
        IData userInfo = queryUserInfo(data.getString("SERIAL_NUMBER"));
        IDataset dataset = UserInfoQry.getUserInfoChgByUserId(userInfo.getString("USER_ID"));

        String brandCode = "";

        if (IDataUtil.isNotEmpty(dataset))
        {
            brandCode = dataset.getData(0).getString("BRAND_CODE");
        }

        if (!"G010".equals(brandCode))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_113);
        }
        IDataset rtnDateset = AcctCall.queryUserScore(userInfo.getString("USER_ID")); // 原来的MValue接口已经不用，改作调用用户积分接口，返回的结果相同。
        return rtnDateset;
    }

    /**
     * 导出用
     * 
     * @param data
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public IDataset queryMvalue(IData data, String eparchy_code) throws Exception
    {
        return null;
    }

    /**
     * 查询用户资料信息
     * 
     * @param serial_number
     * @return
     * @throws Exception
     */
    public IData queryUserInfo(String serial_number) throws Exception
    {
        /* 获取用户资料 */
        IData userInfos = UcaInfoQry.qryUserInfoBySn(serial_number);
        IData userInfo = new DataMap();
        if (IDataUtil.isNotEmpty(userInfos))
        {
            userInfo = userInfos;
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_260);
        }

        return userInfo;
    }
}
