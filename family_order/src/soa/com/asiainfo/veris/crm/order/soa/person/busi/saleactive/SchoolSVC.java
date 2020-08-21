package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

public class SchoolSVC extends CSBizService
{

    public IData tradeSCIntf(IData params) throws Exception
    {
        IData intfResult = new DataMap();
        int counts = 0;
        String serialNumber = params.getString("SERIAL_NUMBER", "");
        String schoolName = params.getString("SCHOOL_NAME");
        String studentName = params.getString("STUDENT_NAME");
        String productId = params.getString("PRODUCT_ID");
        String packageId = params.getString("PACKAGE_ID");
        // IDataset packageExtInfos = PkgExtInfoQry.queryPackageExtInfo(packageId, "0898");
        IData packageExtData = UPackageInfoQry.getPackageByPK(packageId);
        if (IDataUtil.isNotEmpty(packageExtData))
        {
            // packageExtData = packageExtInfos.getData(0);
        }
        else
        {
            CSAppException.appError("2005", "不存在该营销包" + packageId);
        }
        if (serialNumber.isEmpty())
        {
            CSAppException.appError("2005", "手机号码不能为空");
        }
        if (schoolName.isEmpty())
        {
            CSAppException.appError("2005", "学校名称不能为空");
        }
        if (studentName.isEmpty())
        {
            CSAppException.appError("2005", "学生名称不能为空");
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        String isRealName = ucaData.getCustomer().getIsRealName();
        if (!"1".equals(isRealName))
        {
            intfResult.put("X_RESULTCODE", "2812");
            intfResult.put("X_RESULTINFO", "非实名制");
            intfResult.put("TRADE_ID", "-1");
            return intfResult;
        }
        else
        {
            if (!studentName.equals(ucaData.getCustomer().getCustName()))
            {
                intfResult.put("X_RESULTCODE", "2813");
                intfResult.put("X_RESULTINFO", "客户名字和实名制不匹配");
                intfResult.put("TRADE_ID", "-1");
                return intfResult;
            }
        }
        IData param1 = new DataMap();
        param1.put("SCHOOL_NAME", schoolName);
        param1.put("STUDENT_NAME", studentName);
        IDataset dataset1 = Dao.qryByCode("TF_SM_SCHOOL_MEMBER", "SEL_BY_SCHOOL_STUDENT", param1);
        if (IDataUtil.isNotEmpty(dataset1))
        {
            counts = Integer.parseInt(dataset1.getData(0).getString("COUNT_LIMIT"));
            IData param2 = new DataMap();
            param2.put("PRODUCT_ID", productId);
            param2.put("SCHOOL_NAME", schoolName);
            param2.put("STUDENT_NAME", studentName);
            param2.put("PACKAGE_ID", packageId);
            IDataset saleactive = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_SC_PRODUCTID", param2);
            if (saleactive.size() >= counts)
            {

                IDataset ageRules = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "5280", productId, packageId);
                if (IDataUtil.isNotEmpty(ageRules))
                {
                    IData ageRule = ageRules.getData(0);
                    String paraCode2 = ageRule.getString("PARA_CODE2", "");
                    IDataset troopMemberSet = BreQry.getTroopMemberByTroopIdUserId(paraCode2, userId);
                    if (IDataUtil.isEmpty(troopMemberSet))
                    {
                        intfResult.put("X_RESULTCODE", "2811");
                        intfResult.put("X_RESULTINFO", "非目标客户群");
                        intfResult.put("TRADE_ID", "-1");
                        return intfResult;
                    }
                }
            }
        }
        else
        {
            IDataset ageRules = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "5280", productId, packageId);
            if (IDataUtil.isNotEmpty(ageRules))
            {
                IData ageRule = ageRules.getData(0);
                String paraCode2 = ageRule.getString("PARA_CODE2", "");
                IDataset troopMemberSet = BreQry.getTroopMemberByTroopIdUserId(paraCode2, userId);
                if (IDataUtil.isEmpty(troopMemberSet))
                {
                    intfResult.put("X_RESULTCODE", "2811");
                    intfResult.put("X_RESULTINFO", "非目标客户群");
                    intfResult.put("TRADE_ID", "-1");
                    return intfResult;
                }
            }
        }

        try
        {
            IData param3 = new DataMap();
            param3.put("SERIAL_NUMBER", serialNumber);
            param3.put("PRODUCT_ID", productId);
            param3.put("PACKAGE_ID", packageId);
            param3.put("SCHOOL_NAME", schoolName);
            param3.put("STUDENT_NAME", studentName);
            String inModeCode = CSBizBean.getVisit().getInModeCode();
            String TRADE_STAFF_ID = CSBizBean.getVisit().getStaffId();
            String TRADE_DEPART_ID = CSBizBean.getVisit().getDepartId();
            param3.put("IN_MODE_CODE", inModeCode);
            param3.put("TRADE_STAFF_ID", TRADE_STAFF_ID);
            param3.put("TRADE_DEPART_ID", TRADE_DEPART_ID);
            param3.put("ACTION_TYPE", "0");
            param3.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账

            IData head = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param3).getData(0);
            if (head != null && !head.getString("TRADE_ID").isEmpty() && !head.getString("TRADE_ID").equals("-1"))
            {
                intfResult.put("X_RESULTCODE", "0");
                intfResult.put("X_RESULTINFO", "成功");
                intfResult.put("TRADE_ID", head.getString("TRADE_ID"));
                return intfResult;
            }
        }
        catch (Exception ex)
        {
            try
            {
                if (ex.getMessage().contains("`"))
                {
                    String[] errorMessage = ex.getMessage().split("`");
                    intfResult.put("X_RESULTCODE", errorMessage[0]);
                    // intfResult.put("X_RESULTCODE", "2005");
                    intfResult.put("X_RESULTINFO", errorMessage[1]);
                    intfResult.put("TRADE_ID", "-1");
                    return intfResult;
                }
                else
                {
                    intfResult.put("X_RESULTCODE", "2998");
                    intfResult.put("X_RESULTINFO", "其他系统异常" + ex.getMessage());
                    intfResult.put("TRADE_ID", "-1");
                    return intfResult;
                }
            }
            catch (Exception e)
            {
                intfResult.put("X_RESULTCODE", "2998");
                intfResult.put("X_RESULTINFO", "其他系统异常");
                intfResult.put("TRADE_ID", "-1");
                return intfResult;
            }

        }

        return intfResult;
    }
}
