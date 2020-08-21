
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class PayRelaInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 高级付费关系查询
     * 
     * @author linyl3 add at 2013-03-07
     */
    public IDataset getAdvPayRelation(IData input) throws Exception
    {

        return PayRelaInfoQry.getAdvPayRelation(input, getPagination());
    }

    public IDataset getAdvPayRelationAllByAcctID(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getAdvPayRelationAllByAcctID(input.getString("ACCT_ID", ""), input.getString("PAYRELATION_QUERY_TYPE", ""), getPagination());
        return data;
    }

    public IDataset getAdvPayRelationByAcctID(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getAdvPayRelationByAcctID(input.getString("ACCT_ID", ""), null);
        return data;
    }

    public IDataset getAdvPayRelationInfo_ALL(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getAdvPayRelationInfo_ALL(input.getString("ACCT_ID", ""), getPagination());
        return data;
    }

    public IDataset getAdvPayRelationInfo_Pay(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getAdvPayRelationInfo_Pay(input.getString("ACCT_ID", ""), getPagination());
        return data;
    }

    public IDataset getAdvPayRelationInfo_PayD(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getAdvPayRelationInfo_PayD(input.getString("ACCT_ID", ""), input.getString("PAYRELATION_QUERY_TYPE", ""), getPagination());
        return data;
    }

    /**
     * 根据集团编码获取该集团的所有集团产品及付费关系
     * 
     * @param inparams
     *            查询所要输入的参数(SERIAL_NUMBER,PAYRELATION_QUERY_TYPE,PAYRELATION_PAY_TYPE )
     * @return IDataset 高级付费关系结果集
     * @throws Exception
     *             集团彩铃（6200）、移动总机（6100） 手机邮箱（9001） 企业随E行（7050）、行业应用卡（7051）、M2M（7500） 企业建站（9003）、
     *             企业邮箱（9022）、移动OA（9009）、办公助理（9007） MAS二次开发及短信流量费（8900、8901、8902、8910、8920） 移动传真（9143）
     *             移动400（22000333）、车务通（22000340、22000348） 移动一卡通（6083）、呼叫中心直联（6085）
     */
    public IDataset getGroupPayRelationInfoByCustID(IData input) throws Exception
    {
        Pagination pagination = getPagination();
        IDataset data = PayRelaInfoQry.getGroupPayRelationInfoByCustID(input.getString("CUST_ID", ""), pagination);
        return data;
    }

    public IDataset getGrpMemCountRelationByUserID(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getGrpMemCountRelationByUserID(input.getString("USER_ID_A", ""), input.getString("RELATION_TYPE_CODE", ""), null);
        return data;
    }

    public IDataset getNoCachePayItemDetail(IData input) throws Exception
    {

        return PayRelaInfoQry.getNoCachePayItemDetail(input, getPagination());
    }

    /**
     * 普通付费关系查询 add by linyl3 at 2013-03-07
     */
    public IDataset getNormalPayRelation(IData input) throws Exception
    {

        return PayRelaInfoQry.getNormalPayRelation(input, getPagination());
    }

    public IDataset getNorPayRelationByAcctID(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getNorPayRelationByAcctID(input.getString("ACCT_ID", ""), null);
        return data;
    }

    public IDataset getPayItemDetail(IData input) throws Exception
    {

        return PayRelaInfoQry.getPayItemDetail(input, getPagination());
    }

    public IDataset getPayrelationbyAcct(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getPayrelationbyAcct(input.getString("USER_ID", ""), input.getString("ACCT_ID", ""), null);
        return data;
    }

    public IDataset getPayrelationByUserIdAndAcctId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String acctId = input.getString("ACCT_ID");
        IDataset data = PayRelaInfoQry.getPayrelationByUserIdAndAcctId(acctId, userId, null);
        return data;
    }

    public IDataset getPayrelationInfo(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getPayrelationInfo(input.getString("USER_ID"));
        return data;
    }

    public IDataset getPayrelationInfoForGrp(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getPayrelationInfoForGrp(input);
        return data;
    }

    public IDataset getPayRelByUserIdPayCode(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getPayRelByUserIdPayCode(input);
        return data;
    }

    public IDataset qryGrpSpecialPayByCustId(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");

        IDataset payList = PayRelaInfoQry.qryGrpSpecialPayByCustId(custId, getPagination());

        if (IDataUtil.isNotEmpty(payList))
        {
            IData grpCustData = UcaInfoQry.qryGrpInfoByCustId(custId);

            for (int i = 0, row = payList.size(); i < row; i++)
            {
                IData payData = payList.getData(i);
                payData.put("GROUP_ID", grpCustData.getString("GROUP_ID"));
                payData.put("CUST_NAME", grpCustData.getString("CUST_NAME"));
                IDataset ids = PayRelaInfoQry.qryUserGpwpPrdtInfo(payData.getString("USER_ID", ""));
                if(IDataUtil.isNotEmpty(ids)){
                	payData.put("IS_GPWPUSER", "1");
                	payData.put("REMARK", "该成员已订购集团工作手机产品");
                }
            }
        }

        return payList;
    }

    public IDataset qryGrpSpecialPayByCustIdAcctId(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        String acctId = input.getString("ACCT_ID");

        IDataset payList = PayRelaInfoQry.qryGrpSpecialPayByCustIdAcctId(custId, acctId, getPagination());

        if (IDataUtil.isNotEmpty(payList))
        {
            for (int i = 0, row = payList.size(); i < row; i++)
            {
                IData payData = payList.getData(i);
                payData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(payData.getString("PRODUCT_ID", "")));
            }
        }

        return payList;
    }

    public IDataset qryGrpSpecialPayByCustIdAcctIdProductId(IData input) throws Exception
    {
        String custId = input.getString("CUST_ID");
        String acctId = input.getString("ACCT_ID");
        String productId = input.getString("PRODUCT_ID");

        IDataset payList = PayRelaInfoQry.qryGrpSpecialPayByCustIdAcctIdProductId(custId, acctId, productId, getPagination());

        if (IDataUtil.isNotEmpty(payList))
        {
            for (int i = 0, row = payList.size(); i < row; i++)
            {
                IData payData = payList.getData(i);
                payData.put("PRODUCT_ID", productId);
                payData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(payData.getString("PRODUCT_ID", "")));
            }
        }

        return payList;
    }

    public IDataset qryGrpSpecialPayBySerialNumber(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");

        IDataset payList = PayRelaInfoQry.qryGrpSpecialPayBySerialNumber(serialNumber, getPagination());

        if (IDataUtil.isNotEmpty(payList))
        {
            for (int i = 0, row = payList.size(); i < row; i++)
            {
            	IData grpCustData = UcaInfoQry.qryGrpInfoByCustId(payList.getData(i).getString("CUST_ID"));
            	
            	if(IDataUtil.isNotEmpty(grpCustData)){
            		IData payData = payList.getData(i);
                    payData.put("GROUP_ID", grpCustData.getString("GROUP_ID"));
                    payData.put("CUST_NAME", grpCustData.getString("CUST_NAME"));
                    IDataset ids = PayRelaInfoQry.qryUserGpwpPrdtInfo(payData.getString("USER_ID", ""));
                    if(IDataUtil.isNotEmpty(ids)){
                    	payData.put("IS_GPWPUSER", "1");
                    	payData.put("REMARK", "该成员已订购集团工作手机产品");
                    }
            	}
            }
        }

        return payList;
    }

    public IDataset queryDefaultPayRelaByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IDataset data = PayRelaInfoQry.queryDefaultPayRelaByUserId(userId);
        return data;
    }

    public IDataset queryUserValidPay(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String cycId = input.getString("ACYC_ID");
        IDataset data = PayRelaInfoQry.queryUserValidPay(userId, cycId);
        return data;
    }
    
    public IDataset queryAllPayrelationByProductUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        IDataset data = PayRelaInfoQry.queryAllPayrelationByProductUserId(userId);
        return data;
    }
    
    /**
     * 查询集团统一付费产品的付费关系信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUnifyPayRelaBySerialNumber(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        IDataset payList = PayRelaInfoQry.qryUnifyPayRelaBySerialNumber(serialNumber, getPagination());

        if (IDataUtil.isNotEmpty(payList))
        {
            for (int i = 0, row = payList.size(); i < row; i++)
            {
            	IData grpCustData = UcaInfoQry.qryGrpInfoByCustId(payList.getData(i).getString("CUST_ID"));
            	
            	if(IDataUtil.isNotEmpty(grpCustData)){
            		IData payData = payList.getData(i);
                    payData.put("GROUP_ID", grpCustData.getString("GROUP_ID"));
                    payData.put("CUST_NAME", grpCustData.getString("CUST_NAME"));
            	}
            }
        }

        return payList;
    }
    /**
     * 查询已被集团信控发起暂停集团代付关系的集团产品用户
     * @param input
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-8-1
     */
    public IDataset getCreditStopPayRelInfo(IData input) throws Exception
    {
        IDataset data = PayRelaInfoQry.getCreditStopPayRelInfoByCond(input.getString("GROUP_ID", ""), input.getString("GROUP_SN", ""), getPagination());
        return data;
    }
}
