
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcintf.datainfo.uca;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class IUCAInfoIntf
{

    /**
     * 根据集团客户标志查询集团用户信息
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataset qryUserAndProductByCustIdForGrp(IBizCommon bc, String grpCustId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);

        IDataset grpUserList = CSViewCall.call(bc, "CS.UserInfoQrySVC.getUserInfoByCstIdForGrp", param);

        return grpUserList;
    }
    
    /**
     * 根据客户经理id查询客户经理信息
     * @param bc
     * @param custManagerId
     * @return
     * @throws Exception
     */
    public static IData qryCustManagerByCustManagerId(IBizCommon bc, String custManagerId) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", custManagerId);
        IData custManager = CSViewCall.callone(bc, "CS.CustManagerInfoQrySVC.qryCustManagerInfoById", param);
        return custManager;
    }
    
    /**
     * 根据集团客户标志查询集团用户信息
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataOutput getGrpUserInfoByCustId(IBizCommon bc, String grpCustId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);

        IDataOutput grpUserList = CSViewCall.callPage(bc, "CS.UserInfoQrySVC.getUserInfoByCstIdForGrp", param, pagination);

        return grpUserList;
    }
    
    /**
     * 根据集团客户下用户费用情况
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataOutput getGrpAccountDepositByCustId(IBizCommon bc, String grpCustId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);

        IDataOutput grpUserList = CSViewCall.callPage(bc, "CS.CustGroupInfoQrySVC.getGrpAccountDepositByCustId", param, pagination);

        return grpUserList;
    }
    
    
    /**
     * 根据集团客户查询当前时间前后各推六个月合同信息
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataOutput getGrpContactInfoByCustId(IBizCommon bc, String grpCustId, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", grpCustId);

        IDataOutput grpUserList = CSViewCall.callPage(bc, "CS.CustGroupInfoQrySVC.getGrpContactInfoByCustId", param, pagination);

        return grpUserList;
    }
}
