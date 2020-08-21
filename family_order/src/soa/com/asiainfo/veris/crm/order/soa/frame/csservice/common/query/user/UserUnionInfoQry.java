
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserUnionInfoQry
{

    /**
     * 查询用户营销历史信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getSaleHistory(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_UNION_RECOMM_INFO", "SEL_SALEHISTORY_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询用户推荐信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getSaleUser(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_UNION_RECOMM", "SEL_ACTIVEUSER_BY_SERIALNUM", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询用户推荐信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getUserRecomm(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_UNION_RECOMM", "SEL_RECOMMINFO_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询接受1次或拒绝2次的营销推荐信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getUserRecommInfo(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_UNION_RECOMM_INFO", "SEL_RECOMMADVANCE_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询预存营销产品到期的用户
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset getUserRecommPastInfo(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_ID_PAST", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    public static boolean insertUnionRecomm(IData inparam) throws Exception
    {
        return Dao.insert("TF_F_UNION_RECOMM_INFO", inparam);
    }

    /**
     * 查询客户视图信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryCustViewInfo(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_LABLE_VIEW", "SEL_CUSTVIEW_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询用户优惠信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserDiscnt(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }
    /**
     * 查询用户优惠信息(生效的和未生效的)
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserDiscntNew(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_Q", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询用户的平台服务信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserPlatSvc(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_NOWPLATINFO_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }
    /**
     * 查询用户的平台服务信息(生效的和未生效的)
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserPlatSvcNew(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_Q", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询用户资源信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserResInfo(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询用户订购的营销活动信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSaleActive(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_USER_ID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询用户服务信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserSvc(IData inparam) throws Exception
    {
        IDataset dataSet = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID", inparam);
        if (IDataUtil.isNotEmpty(dataSet))
        {
            return dataSet;
        }
        else
        {
            return new DatasetList();
        }
    }
    
    /**
   	 * 查询所有产品
   	 * @param pd
   	 * @param inparam
   	 * @return
   	 * @throws Exception
   	 */
       public static IDataset getMarketProductByProvice(IData inparam) throws Exception{
   		IDataset datas = Dao.qryByCode("TD_B_MARKET_PRODUCT", "SELECT_MARKET_PRODUCT",inparam,Route.CONN_CRM_CEN);
   		return datas;
   	}
}
