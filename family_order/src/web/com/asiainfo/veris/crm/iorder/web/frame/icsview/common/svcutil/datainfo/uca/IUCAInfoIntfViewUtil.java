package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcutil.datainfo.uca;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcintf.datainfo.uca.IUCAInfoIntf;


public class IUCAInfoIntfViewUtil
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

        return IUCAInfoIntf.qryUserAndProductByCustIdForGrp(bc, grpCustId);
    }
    
    public static IData qryCustManagerByCustManagerId(IBizCommon bc, String custManagerId) throws Exception
    {
        return IUCAInfoIntf.qryCustManagerByCustManagerId(bc, custManagerId);
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

        return IUCAInfoIntf.getGrpUserInfoByCustId(bc, grpCustId, pagination);
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

        return IUCAInfoIntf.getGrpAccountDepositByCustId(bc, grpCustId, pagination);
    }
    
    /**
     *  根据集团客户查询当前时间前后各推六个月合同信息
     * 
     * @param bc
     * @param grpCustId
     * @param isThrowException
     * @return
     * @throws Exception
     */
    public static IDataOutput getGrpContactInfoByCustId(IBizCommon bc, String grpCustId, Pagination pagination) throws Exception
    {

        return IUCAInfoIntf.getGrpContactInfoByCustId(bc, grpCustId, pagination);
    }
}
