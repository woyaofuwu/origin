
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.cust.CustUserInfoQry;

public class QueryListByUsrpidSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据证件类型，证件号码查询所有的用户信息包含产品信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset qryAllUserAndBrandInfoByPsptId(IData inParam) throws Exception
    {
        return CustUserInfoQry.qryAllUserAndBrandInfoByPsptId(inParam.getString("PSPT_TYPE_CODE"), inParam.getString("PSPT_ID"));
    }
    
    /**
     * 根据证件号码查询证件下在网用户数量和预约开户的用户数量 
     * @param inParam 
     * 证件类型 PSPT_TYPE_CODE 证件号码 PSPT_ID 购买的号码 SERIAL_NUMBER 路由地市 ROUTE_EPARCHY_CODE 用户归属地市 USER_EPARCHY_CODE 业务地市 EPARCHY_CODE 
     * 网上选号标志 OCCUPY_TYPE_CODE 交易地市 TRADE_EPARCHY_CODE 交易市县 TRADE_CITY_CODE 交易部门 TRADE_DEPART_ID 交易工号 TRADE_STAFF_ID
     * @return
     * 应答编码 X_RESULTCODE 应答结果 X_RESULTINFO 可以开户数 SEL_NUM
     * @throws Exception
     */
    public IDataset qryAllUserNumByPsptId(IData inParam) throws Exception
    {
        
        return CustUserInfoQry.qryValidPhoneNumByPsptId(inParam);
    }

}
