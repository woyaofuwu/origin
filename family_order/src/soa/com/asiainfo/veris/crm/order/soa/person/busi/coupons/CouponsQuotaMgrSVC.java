/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.coupons;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CouponsQuotaInfoQry;


/**
 * @CREATED by 
 */
public class CouponsQuotaMgrSVC extends CSBizService
{

    private static final long serialVersionUID = -5864192502093904255L;

    /*
     * 
     */
    private void checkIsExist(String audit_order_id) throws Exception
    {

        IDataset result = CouponsQuotaInfoQry.queryCouponsQuotaInfoByOrderID(audit_order_id);
        if (IDataUtil.isNotEmpty(result))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_263, audit_order_id);
        }
    }

    /**
     * 
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY zhangxing3
     */
    public IDataset getCouponsQuotaInfoByRowId(IData param) throws Exception
    {

        return CouponsQuotaInfoQry.queryCouponsQuotaInfo(param.getString("ROW_ID"));
    }

    /**
     * 获取赠送总额审批工单及工单余额
     */
    public IDataset getWorkOrders(IData param) throws Exception
    {
    	CouponsQuotaMgrBean bean = (CouponsQuotaMgrBean) BeanManager.createBean(CouponsQuotaMgrBean.class);

        return bean.getWorkOrders();
    }

    /**
     * 根据ROWID修改或者删除单条配置 FOR 
     * 
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY zhangxing3
     */
    public IData operCouponsQuotaInfo(IData param) throws Exception
    {

    	CouponsQuotaMgrBean bean = (CouponsQuotaMgrBean) BeanManager.createBean(CouponsQuotaMgrBean.class);

        IData returnData = new DataMap();

        param.put("OPERA_STAFF_ID", getVisit().getStaffId());

        String auditOrderID = param.getString("AUDIT_ORDER_ID", "");

        if ("DELETE".equals(param.getString("OPER_TYPE")))
        {

            int i = bean.deleteCouponsQuotaInfo(param);

            returnData.put("SUCC_FLAG", i);

        }
        else if ("UPDATE".equals(param.getString("OPER_TYPE")))
        {
            // 有记录则不能修改审批工单号和分公司
            IDataset oldInfos = CouponsQuotaInfoQry.queryCouponsQuotaInfo(param.getString("ROW_ID"));

            if (IDataUtil.isNotEmpty(oldInfos))
            {

                IData oldInfo = oldInfos.getData(0);
				double newValue = Double.parseDouble(param.getString(
						"TOTAL_AMOUNT", "0"));
				double oldValue = Double.parseDouble(oldInfo.getString(
						"TOTAL_AMOUNT", "0"));
				if (newValue < oldValue) {
					// common.error("修改的审批总金额不能少于原来的审核总金额。");
					CSAppException.apperr(CrmCardException.CRM_CARD_264);
				}

				// 审批工单号修改则校验其唯一性
				if (!auditOrderID.equals(oldInfo
						.getString("AUDIT_ORDER_ID", ""))) {
					// 校验工号记录是否存在
					checkIsExist(auditOrderID);
				}
				String endDate=param.getString("END_TIME","");
	            endDate=endDate+" 23:59:59";
	        	param.put("END_TIME", endDate);
				int i = bean.updateCouponsQuotaInfo(param);

				returnData.put("SUCC_FLAG", i);
            }
        }
        else if ("ADD".equals(param.getString("OPER_TYPE")))
        {

            checkIsExist(param.getString("AUDIT_ORDER_ID"));
            String endDate=param.getString("END_TIME","");
            endDate=endDate+" 23:59:59";
        	param.put("END_TIME", endDate);
            int i = bean.createCouponsQuotaInfo(param);

            returnData.put("SUCC_FLAG", i);
        }

        return returnData;
    }

    /**
     * @param param
     * @return
     * @throws Exception
     * @CREATE BY zhangxing3
     */
    public IDataset queryCouponsQuotaInfos(IData param) throws Exception
    {

        return CouponsQuotaInfoQry.queryCouponsQuotaInfos(param, this.getPagination());

    }


    
}
