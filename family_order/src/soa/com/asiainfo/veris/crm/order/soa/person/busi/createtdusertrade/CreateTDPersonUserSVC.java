
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.privm.CheckPriv;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateTDPersonUserSVC.java
 * @Description: 无线固话开户服务
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-11 上午10:44:34 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-11 yxd v1.0.0 修改原因
 */
public class CreateTDPersonUserSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    public IDataset checkSerialNumber(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        //start--wangsc10--20181031
        boolean ttopen= CheckPriv.checkFuncPermission(CSBizBean.getVisit().getStaffId(), "SYSTTOPEN");
        IDataset retDatasetTT = ResCall.checkResourceForTTphone(serialNumber);
        if(IDataUtil.isNotEmpty(retDatasetTT)){
        	String ACCESS_NUMBER = retDatasetTT.first().getString("ACCESS_NUMBER","");
        	if(ACCESS_NUMBER.equals(serialNumber)){
        		if(!ttopen){
        			CSAppException.appError("-1", "没有特殊铁通号码开户权限,不能用此号码开户!");
        		}
        	}
        }
        //end
        //IDataset retDataset = ResCall.checkResourceForIOTMphone("0", "0", serialNumber, "0", super.getVisit().getDepartId());
        IDataset retDataset = ResCall.checkResourceForMphone("0", serialNumber, "0");
        // 处理密码卡（如果是预配或预开就取密码卡信息，调用SIM卡选择接口）
        IData retData = retDataset.first();
        String simCardNo = retData.getString("SIM_CARD_NO", ""); // SIM卡
        String preOpenTag = retData.getString("PREOPEN_TAG", "0"); // 预开
        String preCodeTag = retData.getString("PRECODE_TAG", "0"); // 预配
        if (StringUtils.isNotBlank(simCardNo) && (StringUtils.equals("1", preOpenTag) || StringUtils.equals("1", preCodeTag)))
        {
            //IDataset simCardSet = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", super.getVisit().getDepartId(), "", "0");
        	retDataset = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "");
        }
        /**
         * 固话吉祥号码修改权限
	     * luys
         * */
        IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
    	if (IDataUtil.isNotEmpty(dataSet)){
         	IData mphonecodeInfo = dataSet.first();
         	String beautifulTag = mphonecodeInfo.getString("BEAUTIFUAL_TAG");
         	if (StringUtils.equals("1", beautifulTag)){
         		retDataset.first().put("TDBEAUTIFUALTAG", "1");
     		    if (StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "CHANGETDFEE")){
     	        	retDataset.first().put("SYSCHANGETDFEE", "1");
     	        }
         	}
        }
        return retDataset;
    }

    public IDataset checkSimCardNo(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        //IDataset retDataset = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", super.getVisit().getDepartId(), "", "0");
        IDataset retDataset = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "");
        return retDataset;
    }

    public IDataset getProductFeeInfo(IData input) throws Exception
    {
        String product_id = input.getString("PRODUCT_ID");
        String eparchy_code = CSBizBean.getUserEparchyCode();
//        IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("3820", product_id, "-1", "-1", "P", "3", eparchy_code);
        IDataset dataset = UpcCall.qryDynamicPrice(product_id, BofConst.ELEMENT_TYPE_CODE_PRODUCT, "-1", null, "3820", null, null, null);
        if(IDataUtil.isNotEmpty(dataset))
        {
            for(Object obj : dataset)
            {
                IData feeInfo = (IData) obj;
                feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
            }
        }
        return dataset;
    }

    public IDataset onInitTrade(IData input) throws Exception
    {
//modify by lijun17        IDataset productTypeList = ProductInfoQry.getProductsType("5000", null);// 商务电话产品类型;O
    	return UProductInfoQry.getProductsType("5000", null);// 商务电话产品类型;O
    }
    /*
    * 省侧一证5号校验
    */
    public IDataset checkProvinceMorePsptId(IData input) throws Exception
    {
        CreateTDPersonUserBean bean = (CreateTDPersonUserBean) BeanManager.createBean(CreateTDPersonUserBean.class);
        IDataset dataset = bean.checkProvinceMorePsptId(input);
        return dataset;
    }
}
