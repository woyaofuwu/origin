/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.queryusercomplaints;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CTSCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: QueryUserComplaintsService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-29 下午09:26:59 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-29 chengxf2 v1.0.0 修改原因
 */

public class QueryUserComplaintsService extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public static String maskName(String name) throws Exception
    {
        StringBuilder maskName = new StringBuilder();
        char character = '*';
        if (name != null)
        {
            name = name.trim();
        }
        if (name == null || name.length() == 0)
        {

        }
        else if (name.length() == 1 || name.length() == 2)
        {
            maskName.append(name.charAt(0));
            maskName.append(character);
        }
        else if (name.length() == 3)
        {
            maskName.append(name.charAt(0));
            for (int i = 0; i < name.length() - 1; i++)
            {
                maskName.append(character);
            }
        }
        else
        {
            maskName.append(name.charAt(0));
            maskName.append(name.charAt(1));
            for (int i = 0; i < name.length() - 2; i++)
            {
                maskName.append(character);
            }
        }
        return maskName.toString();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-30 上午09:03:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-30 chengxf2 v1.0.0 修改原因
     */
    public IDataset queryUserComplaints(IData inData) throws Exception
    {
        IDataset results = new DatasetList();

        String startDate = inData.getString("COMPLAINT_START_DATE");
        startDate = SysDateMgr.getAddMonthsNowday(3, startDate);
        String endDate = inData.getString("COMPLAINT_END_DATE");
        if (startDate.compareTo(endDate.substring(0, 10)) < 0)
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_26);
        }

        boolean cityCheck = !inData.getString("CITY_CODE", "").equals("");
        boolean openDateStartCheck = !inData.getString("WIDENET_START_DATE", "").equals("");
        boolean openDateEndCheck = !inData.getString("WIDENET_END_DATE", "").equals("");
        boolean productCheck = !inData.getString("WIDENET_PRODUCT_ID", "").equals("");
        boolean phoneCheck = !inData.getString("PHONE", "").equals("");
        boolean satisfiedCheck = !inData.getString("SATISFIED", "").equals("");

        IData svcParam = new DataMap();
        svcParam.put("ACCEPTPHONECODEARG", "aa");
        svcParam.put("X_TRANS_CODE", "CRM_KFComplainSheet_PbossQuery");
        svcParam.put("ACCEPTFROMTIME", inData.getString("COMPLAINT_START_DATE") + SysDateMgr.getFirstTime00000());
        svcParam.put("ACCEPTTOTIME", inData.getString("COMPLAINT_END_DATE") + SysDateMgr.getEndTime235959());
        IDataset result = CTSCall.callCTS("CRM_KFComplainSheet_PbossQuery", svcParam);
        int recordNum = result.getData(0).getInt("X_RECORDNUM");
        for (int i = 0; i < recordNum; i++)
        {
            IData tempData = result.getData(i);
            String acceptPhoneCode = tempData.getString("ACCEPT_PHONE_CODE");
            IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + acceptPhoneCode);
            if (IDataUtil.isEmpty(userInfo))
            {
                continue;
            }

            String custId = userInfo.getString("CUST_ID");
            IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
            if (IDataUtil.isEmpty(custInfo))
            {
                continue;
            }

            String userId = userInfo.getString("USER_ID");
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
            if (IDataUtil.isEmpty(widenetInfos))
            {
                continue;
            }

            if (cityCheck) // 校验宽带用户归属业务区
            {
                if (!inData.getString("CITY_CODE", "").equals(userInfo.getString("CITY_CODE")))
                {
                    continue;
                }
            }

            if (openDateStartCheck) // 校验宽带用户开户时间
            {
                if (inData.getString("WIDENET_START_DATE", "").compareTo(userInfo.getString("OPEN_DATE")) > 0)
                {
                    continue;
                }
            }

            if (openDateEndCheck) // 校验宽带用户开户时间
            {
                if (userInfo.getString("OPEN_DATE").compareTo(inData.getString("WIDENET_END_DATE", "")) > 0)
                {
                    continue;
                }
            }

            if (productCheck) // 校验宽带用户当前产品
            {
                if (!inData.getString("WIDENET_PRODUCT_ID", "").equals(userInfo.getString("PRODUCT_ID")))
                {
                    continue;
                }
            }

            if (phoneCheck) // 校验联系电话
            {
                if (!inData.getString("PHONE", "").equals(userInfo.getString("PHONE", "")))
                {
                    continue;
                }
            }

            if (satisfiedCheck) // 校验满意状态
            {
                String s = StaticUtil.getStaticValue("COMPLAINT_SATISFIED", inData.getString("SATISFIED", ""));
                if (!s.equals(tempData.getString("CUST_SATISFY", "")))
                {
                    continue;
                }
            }

            IData tempRes = new DataMap();
            tempRes.put("SERIAL_NUMBER", tempData.getString("ACCEPT_PHONE_CODE"));// 宽带号码
            String custName = custInfo.getString("CUST_NAME");
            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS012"))
            {
                custName = maskName(custName);
            }
            tempRes.put("CUST_NAME", custName); // 客户名称
            tempRes.put("CITY_NAME", StaticUtil.getStaticValue("JOB_CALL_CITYCODE", userInfo.getString("CITY_CODE"))); // 业务区
            String phone = widenetInfos.getData(0).getString("PHONE", "");
            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS012"))
            {
                phone = maskName(phone);
            }
            tempRes.put("PHONE", phone);
            tempRes.put("WIDENETOPEN_DATE", widenetInfos.getData(0).getString("OPEN_DATE"));
            String productName = UProductInfoQry.getProductNameByProductId(userInfo.getString("PRODUCT_ID"));
            tempRes.put("PRODUCT_NAME", productName);
            tempRes.put("COMPLAINT_CONTENT", tempData.getString("CONTENT"));
            tempRes.put("COMPLAINT_DATE", tempData.getString("ACCEPTTIME"));
            tempRes.put("COMPLAINT_RESULT", tempData.getString("WORKFORMSTATE"));
            tempRes.put("COMPLAINT_SATISFIED", tempData.getString("CUST_SATISFY", ""));

            results.add(tempRes);
        }

        return results;
    }
}
