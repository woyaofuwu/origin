
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.opengroupmember.OpenGroupMemberBean;

public class BatOpenGroupMemberTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IDataUtil.chkParam(condData, "PRODUCT_ID");
        IDataUtil.chkParam(condData, "MEM_CUST_INFO");
        IDataUtil.chkParam(condData, "ELEMENT_INFO");
        IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        // 组织三户资料
        IData param = condData.getData("MEM_CUST_INFO");
        IData userInfo = new DataMap();
        IData custInfo = new DataMap();
        IData acctInfo = new DataMap();

        userInfo.put("USER_TYPE_CODE", param.getString("USER_TYPE_CODE", ""));
        userInfo.put("REMARK", param.getString("REMARK", "集团成员批量开户"));
        
        custInfo.putAll(param);
        String birthday = param.getString("BIRTHDAY", "");
        if(birthday == null || "".equals(birthday))
        {
            custInfo.put("BIRTHDAY", "1900-01-01");
        }
        else
        {
            custInfo.put("BIRTHDAY", birthday);
        }
        custInfo.put("PSPT_TYPE_CODE", param.getString("PSPT_TYPE_CODE", ""));
        custInfo.put("POST_CODE", param.getString("POST_CODE", ""));
        custInfo.put("CONTACT_PHONE", param.getString("CONTACT_PHONE", ""));
        custInfo.put("CUST_NAME", param.getString("CUST_NAME", ""));
        custInfo.put("USER_TYPE_CODE", param.getString("USER_TYPE_CODE", ""));
        custInfo.put("PSPT_ID", param.getString("PSPT_ID", ""));
        custInfo.put("GROUP_ID", condData.getString("GROUP_ID", ""));
        custInfo.put("CUST_INFO_TELTYPE", condData.getString("CUST_INFO_TELTYPE", "")); // ims 客户端类型

        acctInfo.put("PAY_NAME", param.getString("CUST_NAME", ""));
        acctInfo.put("PAY_MODE_CODE", "0");

        batData.put("MEM_USER_INFO", userInfo);
        batData.put("MEM_CUST_INFO", custInfo);
        batData.put("MEM_ACCT_INFO", acctInfo);

        String productId = condData.getString("PRODUCT_ID");
        // 校验资源信息
        IDataset resInfoList = checkResInfo(batData, productId);
        batData.put("RES_INFO", resInfoList);
        checkSerialNumer(batData);
        
        
        //String batchId = batData.getString("BATCH_ID","");//批量号
        //String custName = param.getString("CUST_NAME", "");
        //String psptTypeCode = param.getString("PSPT_TYPE_CODE", "");
        //String psptId = param.getString("PSPT_ID", "");
        //checkGrpCustPsptId(productId,psptTypeCode,psptId,custName,batchId);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("MEM_CUST_INFO", batData.getData("MEM_CUST_INFO"));
        svcData.put("MEM_USER_INFO", batData.getData("MEM_USER_INFO"));
        svcData.put("MEM_ACCT_INFO", batData.getData("MEM_ACCT_INFO"));
        svcData.put("CUST_INFO_TELTYPE", condData.getString("CUST_INFO_TELTYPE"));
        svcData.put("USER_EPARCHY_CODE", condData.getString("USER_EPARCHY_CODE"));
        svcData.put(Route.ROUTE_EPARCHY_CODE, condData.getString(Route.ROUTE_EPARCHY_CODE, condData.getString("USER_EPARCHY_CODE")));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("CUST_ID", condData.getString("CUST_ID"));
        svcData.put("RES_INFO", batData.getDataset("RES_INFO"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "true"));
        svcData.put("REMARK", batData.getString("REMARK", "集团成员批量开户"));
    }

    /**
     * 检查产品是否需要资源
     * 
     * @param batData
     * @throws Exception
     */
    public IDataset checkResInfo(IData batData, String productId) throws Exception
    {
        OpenGroupMemberBean bean = new OpenGroupMemberBean();

        IData condData = batData.getData("condData", new DataMap());

        IDataset needResList = ProductInfoQry.getResTypeByMainProduct(productId);

        // 过滤重复资源
        IDataset resultList = new DatasetList();

        for (int i = 0, size = needResList.size(); i < size; i++)
        {
            String resType = needResList.getData(i).getString("RES_TYPE_CODE");

            IDataset tempIdata = DataHelper.filter(resultList, "RES_TYPE_CODE=" + resType);
            if (tempIdata.size() < 1)
            {
                resultList.add(needResList.getData(i));
            }
        }

        // 校验资源信息
        for (int j = 0, size = resultList.size(); j < size; j++)
        {
            IData resParam = resultList.getData(j);
            IData result = null;
            String resTypeCode = resParam.getString("RES_TYPE_CODE");
            String resType = resParam.getString("RES_TYPE");
            if (StringUtils.equals("0", resTypeCode))
            {
                String serialNumber = batData.getString("SERIAL_NUMBER", "");
                if (StringUtils.isBlank(serialNumber))
                    CSAppException.apperr(ResException.CRM_RES_13); // 获取资源信息手机号码无数据!

                resParam.put("PRODUCT_ID", productId);
                resParam.put("RES_VALUE", serialNumber);
                resParam.put("EPARCHY_CODE", condData.getString("USER_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
                resParam.put("RES_TYPE", resTypeCode);

                result = bean.checkOuterNumber(resParam);

                if (IDataUtil.isEmpty(result) || !"0".equals(result.getString("X_RESULTCODE")))
                    CSAppException.apperr(ResException.CRM_RES_16); // 资源校验手机号码错误，["+serialNumber+"]手机号码不可用!

                IData resData = result.getDataset("RES_LIST").getData(0);
                resData.remove("RES_TYPE");
                resData.put("RES_TYPE", resType);
                resParam.putAll(resData);

            }
            else if (StringUtils.equals("1", resTypeCode))
            {
                String simCardNo = condData.getString("SIM_CARD_NO", "");
                if (StringUtils.isBlank(simCardNo))
                    CSAppException.apperr(ResException.CRM_RES_12); // 获取资源信息SIM卡无数据!

                resParam.put("PRODUCT_ID", productId);
                resParam.put("RES_VALUE", simCardNo);
                resParam.put("EPARCHY_CODE", condData.getString("USER_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
                resParam.put("RES_TYPE", resTypeCode);

                result = bean.checkSimResource(resParam);

                if (IDataUtil.isEmpty(result) || !"0".equals(result.getString("X_RESULTCODE")))
                    CSAppException.apperr(ResException.CRM_RES_12); // 资源校验SIM卡错误，["+simCardNo+"]卡号不可用!

                IData resData = result.getDataset("RES_LIST").getData(0);
                resData.remove("RES_TYPE");
                resData.put("RES_TYPE", resType);
                resParam.putAll(resData);

            }
            else if (StringUtils.equals("W", resTypeCode))
            {
                String serialNumber = batData.getString("SERIAL_NUMBER", "");
                if (StringUtils.isBlank(serialNumber))
                    CSAppException.apperr(ResException.CRM_RES_87); // 获取外网服务号码无数据!

                IData userInfoData = UserInfoQry.getMebUserInfoBySN(serialNumber);

                if (IDataUtil.isNotEmpty(userInfoData))
                    CSAppException.apperr(CrmUserException.CRM_USER_1098, serialNumber); // 用户资料表里已存在相同的网外号码！

                resParam.put("PRODUCT_ID", productId);
                resParam.put("RES_VALUE", serialNumber);
                resParam.put("EPARCHY_CODE", condData.getString("USER_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
                resParam.put("RES_TYPE", resTypeCode);

                result = bean.checkOuterNumber(resParam);

                if (IDataUtil.isEmpty(result) || !"0".equals(result.getString("X_RESULTCODE")))
                    CSAppException.apperr(ResException.CRM_RES_16); // 资源校验手机号码错误，["+serialNumber+"]手机号码不可用!

                IData resData = result.getDataset("RES_LIST").getData(0);
                resData.remove("RES_TYPE");
                resData.put("RES_TYPE", resType);
                resParam.putAll(resData);

            }
        }

        return resultList;
    }

    public void checkSerialNumer(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        if (StringUtils.isEmpty(batData.getString("SERIAL_NUMBER")))
        {
            String productId = condData.getString("PRODUCT_ID");

            String groupId = condData.getString("GROUP_ID");

            String grpMebSn = OpenGroupMemberBean.genGrpMebSn(productId, groupId);

            batData.put("SERIAL_NUMBER", grpMebSn);
        }
    }

    private void checkGrpCustPsptId(String productId,String psptTypeCode,
    		String psptId,String custName,String batchId) throws Exception 
    {
        if(StringUtils.isNotBlank(productId) && StringUtils.equals("801110", productId))
        {
    		if("E".equals(psptTypeCode) || "M".equals(psptTypeCode) ||
    				"G".equals(psptTypeCode) || "D".equals(psptTypeCode))
    		{
    			if(StringUtils.isNotBlank(custName) && StringUtils.isNotBlank(psptId) && StringUtils.isNotBlank(batchId))
    			{
    				//阀值
        			int setCount = UserInfoQry.getRealNameUserLimitByPsptNew(custName, psptId, "1");
        			
        			//开户的数量
        			int openCount = UserInfoQry.getRealNameUserCountByPspt2New(custName, psptId, "1");
        			
        			int batCount = BatTradeInfoQry.queryBatDealCntByBatchId(batchId);
        			
        			//比较
        			if(setCount < openCount + batCount)
        			{
        				String errMsg = "该证件号码" + psptId + "的开户数量(" + openCount + ")与本次批量的数量(" + batCount + ")和已经达到了设置的阀值(" + setCount + ").";
        				CSAppException.apperr(GrpException.CRM_GRP_713, errMsg);
        			}
    			}
    		}
        }
    }
    
}
