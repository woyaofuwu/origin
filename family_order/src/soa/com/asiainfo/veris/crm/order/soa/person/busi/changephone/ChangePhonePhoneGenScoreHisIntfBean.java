
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;

public class ChangePhonePhoneGenScoreHisIntfBean extends CSBizBean
{

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset changePhoneGenScoreHis(IData input) throws Exception
    {

        IData data = new DataMap();

        IDataset dataset = new DatasetList();

        String serial_number = input.getString("SERIAL_NUMBER");

        IData userinfo = getUserInfo(serial_number); // 用户信息

        // IData custInfo = getCustInfo(userinfo); //客户信息

        String custype = isVipCust(serial_number); // 大客户

        String userid = userinfo.getString("USER_ID");

        IData userScore = getUserScore(userid); // 获取用户积分

        String brandcode = getUserBrandInfo(userinfo); // 获取用户品牌信息

        // 品牌业务信息
        data.put("TRADE_TYPE_CODE", "350");
        data.put("SCORE_TYPE_CODE", "00");
        data.putAll(userinfo);
        // data.putAll(custInfo);
        data.put("CLASS_NAME", custype);
        data.putAll(userScore);
        dataset.add(data);
        return dataset;
    }

    /**
     * 获取客户信息
     * 
     * @param userInfo
     * @return
     * @throws Exception
     */
    private IData getCustInfo(IData userInfo) throws Exception
    {
        IDataset custInfo = CustPersonInfoQry.getMemInfo(userInfo, null);
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_210,userInfo.getString("SERIAL_NUMBER"));
        }
        return custInfo.getData(0);
    }

    /**
     * 查询品牌
     * 
     * @return
     * @throws Exception
     */
    private String getUserBrandInfo(IData param) throws Exception
    {
        String bdcod = "";
        String brand_code = param.getString("BRAND_CODE");
        IData pdfem = new DataMap();
        IDataset dataset = ProductTypeInfoQry.getProductIdsByProductType(brand_code);
        if (IDataUtil.isNotEmpty(dataset))
        {
            pdfem = dataset.getData(0);
        }
        if (IDataUtil.isNotEmpty(pdfem))
            bdcod = pdfem.getString("PARENT_PTYPE_CODE");
        return bdcod;
    }

    /**
     * 获取用户信息
     * 
     * @param serial_number
     * @return
     * @throws Exception
     */
    private IData getUserInfo(String serial_number) throws Exception
    {
        IData userInf = UcaInfoQry.qryUserInfoBySn(serial_number);

        if (IDataUtil.isEmpty(userInf))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_200);
        }

        return userInf;
    }

    /**
     * 获取用户积分
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    private IData getUserScore(String userId) throws Exception
    {
        String userScore = "0";
        IDataset result = AcctCall.queryUserScore(userId);
        return result.getData(0);
    }

    /**
     * 大客户
     * 
     * @param serialnumber
     * @return
     * @throws Exception
     */
    private String isVipCust(String serialnumber) throws Exception
    {
        String custype = "";
        IDataset dataset = CustVipInfoQry.qryVipInfoBySn(serialnumber); // 获取大客户资料
        if (IDataUtil.isNotEmpty(dataset))
        {
            custype = dataset.getData(0).getString("VIP_TYPE_CODE");
        }
        return custype;
    }

}
