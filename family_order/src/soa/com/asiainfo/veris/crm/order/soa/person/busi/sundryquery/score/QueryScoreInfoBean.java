
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.score;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.QryScoreInfo;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QueryScoreInfoBean extends CSBizBean
{
    public IData getCommInfo(IData inparam) throws Exception
    {
        String serial_number = inparam.getString("SERIAL_NUMBER");
        // 获取用户资料
        IData userInfo = queryUserInfo(serial_number);
        if (userInfo == null || userInfo.isEmpty())
        {
            CSAppException.apperr(CrmUserException.CRM_USER_260);
        }

        String user_id = userInfo.getString("USER_ID", "");
        /* 获取客户资料 */
        IData custInfo = UcaInfoQry.qryPerInfoByUserId(user_id);
        if (custInfo == null || custInfo.isEmpty())
        {
            CSAppException.apperr(CustException.CRM_CUST_107);
        }
        /* 获取用户积分 */
        // 查用户积分
        IDataset userScoreInfos = AcctCall.queryUserScore(user_id);
        if (IDataUtil.isEmpty(userScoreInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }

        IData returnData = new DataMap();
        returnData.put("SCORE_INFO", userScoreInfos.getData(0));
        returnData.put("CUST_INFO", custInfo);
        returnData.put("USER_INFO", userInfo);

        return returnData;
    }

    /**
     * 查询积分业务情况
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryScoreBizInfos(IData inparam, Pagination pagination) throws Exception
    {
        inparam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        return QryScoreInfo.queryScoreBizInfos(inparam, pagination);
    }

    /**
     * 查询积分里程明细
     * 
     * @param inparam
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset queryScoreDetail(IData inparam, Pagination pagination) throws Exception
    {
        String score_type_code = inparam.getString("SCORE_TYPE_CODE");
        score_type_code = StringUtils.isEmpty(score_type_code) ? "-1" : score_type_code;

        IDataset dataset = AcctCall.queryScoreDetail(inparam.getString("USER_ID"), score_type_code, inparam.getString("START_CYCLE_ID"), inparam.getString("END_CYCLE_ID"));

        return dataset;
    }

    /**
     * 查询年度兑奖积分
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryScoreExchangeYear(IData inparam) throws Exception
    {
        IDataset dataset = AcctCall.queryYearUserScore(inparam.getString("USER_ID"), inparam.getString("YEAR_ID"));
        return dataset;
    }

    /**
     * 查询积分类型
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryScoreType(IData inparam) throws Exception
    {
        return QryScoreInfo.queryScoreType(inparam);
    }

    /**
     * 查询各品牌的门限
     * 
     * @param brandCode
     * @return
     * @throws Exception
     */
    public String queryTagNumber(String brandCode) throws Exception
    {

        String tagNumber = "0";
        if ("G001".equals(brandCode))
        {
            tagNumber = "1000";
        }
        else if ("G010".equals(brandCode))
        {
            tagNumber = "500";
        }

        return tagNumber;
    }

    /**
     * 查询用户品牌
     * 
     * @param user_id
     * @return
     * @throws Exception
     */
    public String queryUserBrand(String user_id) throws Exception
    {
        IDataset dataset = UserInfoQry.getUserInfoChgByUserId(user_id);
        IData brandInfo = dataset.size() > 0 ? (IData) dataset.getData(0) : null;
        String brandCode = (brandInfo != null && !brandInfo.isEmpty()) ? brandInfo.getString("BRAND_CODE") : "";

        return brandCode;
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

        // IDataset userInfos = UserInfoQry.getUserInfoBySnCrmOneDb(serial_number, "0", "00");
        IDataset userInfos = null;// UserInfoQry.getUserInfoBySnCrmOneDbTempHI(serial_number, "0", "00");

        IData userInfo = null;
        if (IDataUtil.isNotEmpty(userInfos))
        {
            userInfo = userInfos.getData(0);
        }

        return userInfo;
    }

    /**
     * 查询年度累计积分
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset queryYearSumScore(IData inparam) throws Exception
    {
        IDataset dataset = AcctCall.queryYearSumScore(inparam.getString("SERIAL_NUMBER"), inparam.getString("START_CYCLE_ID"), inparam.getString("END_CYCLE_ID"));
        return dataset;
    }

}
