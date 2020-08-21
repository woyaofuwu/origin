
package com.asiainfo.veris.crm.order.soa.person.busi.contractsale.order.filter.in;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ContractResourceInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;

public class GroupContractPreSaleFilterIn implements IFilterIn
{

    private void addResource(IDataset elements, String resourceCode) throws Exception
    {
        IDataset tempSet = ContractResourceInfoQry.queryContractResourceByRCode(resourceCode);
        if (IDataUtil.isEmpty(tempSet))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "营销资源不存在[" + resourceCode + "]");
        }
        IData resourceInfo = tempSet.getData(0);
        IData element = new DataMap();
        String resourceType = resourceInfo.getString("RESOURCE_TYPE");
        element.put("ELEMENT_ID", resourceInfo.getString("ELEMENT_ID"));
        element.put("ELEMENT_TYPE_CODE", resourceInfo.getString("ELEMENT_TYPE_CODE"));
        element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        elements.add(element);
        if (resourceType.equals("101"))// 语音资源
        {
            String rsrvNum1 = resourceInfo.getString("RSRV_NUM1");
            String elementType = resourceInfo.getString("ELEMENT_TYPE_CODE");
            if ("D".equals(elementType) && StringUtils.isNotEmpty(rsrvNum1))
            {
                element = new DataMap();
                element.put("ELEMENT_ID", rsrvNum1);
                element.put("ELEMENT_TYPE_CODE", "P");
                element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                elements.add(element);
            }
        }
    }

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // 入参校验
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "VERIFT_TIME");
        IDataUtil.chkParam(input, "ORDER_CODE");
        IDataUtil.chkParam(input, "CONTRACT_TYPE");
        IDataUtil.chkParam(input, "CONTRACT_START_DATE");
        IDataUtil.chkParam(input, "COMPANY_CODE");
        IDataUtil.chkParam(input, "SHOP_CODE");
        IDataUtil.chkParam(input, "SALES_PERSON_ID");
        IDataUtil.chkParam(input, "MOBILE_NO");
        IDataUtil.chkParam(input, "MATERIAL_CODE");
        IDataUtil.chkParam(input, "COMPENSATE_FEE");
        IDataUtil.chkParam(input, "DEPOSIT_FEE");
        IDataUtil.chkParam(input, "GRADE_LOW_LIMIT");
        IDataUtil.chkParam(input, "PROMISE_DURATION");
        // 入参校验结束

        // 入参转换
        if (StringUtils.isNotBlank(input.getString("IMEI")))
        {
            input.put("RES_CODE", input.getString("IMEI"));
        }
        if (StringUtils.isNotBlank(input.getString("PROMISE_DURATION")))
        {
            input.put("MONTHS", input.getString("PROMISE_DURATION"));
        }
        if (StringUtils.isNotBlank(input.getString("GRADE_LOW_LIMIT")))
        {
            input.put("CONSUME_LIMIT", input.getInt("GRADE_LOW_LIMIT") * 100);
        }
        if (StringUtils.isNotBlank(input.getString("COMPENSATE_FEE")))
        {
            input.put("OPER_FEE", input.getInt("COMPENSATE_FEE") * 100);
        }
        if (StringUtils.isNotBlank(input.getString("MOBILE_NO")))
        {
            input.put("SERIAL_NUMBER", input.getString("MOBILE_NO"));
        }
        if (StringUtils.isNotBlank(input.getString("ORDER_CODE")))
        {
            input.put("OUT_REQUEST_ID", input.getString("ORDER_CODE"));
        }
        if (StringUtils.isNotBlank(input.getString("DEPOSIT_FEE")))
        {
            input.put("DEPOSIT_FEE", input.getInt("DEPOSIT_FEE") * 100);
        }

        // 得到合约ID
        String productId = StaticUtil.getStaticValue("GROUP_CONTRACT", "PRODUCT_ID");

        if (StringUtils.isBlank(productId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "得不到合约ID");
        }
        input.put("PRODUCT_ID", productId);

        String campnType = LabelInfoQry.getLogicLabelIdByElementId(productId);
        input.put("CAMPN_TYPE", campnType);// 查询活动类型

        // 获取营销活动的编码
        IData saleactivePkgInfo = new DataMap();
        String packageId = "";
        IDataset tempSaleGroups = ProductPkgInfoQry.queryPackagesByProductId(productId);
        int size = tempSaleGroups.size();
        for (int i = 0; i < size; i++)
        {
            IData tempSaleGroup = tempSaleGroups.getData(i);
            String tempPackageId = tempSaleGroup.getString("PACKAGE_ID");
            IData packageInfo = PkgInfoQry.getPackageByPK(tempPackageId);
            String packageTypeCode = packageInfo.getString("PACKAGE_TYPE_CODE");
            if ("4".equals(packageTypeCode))
            {
                // 营销活动的包过滤掉
                saleactivePkgInfo = packageInfo;
                break;
            }
        }
        if (IDataUtil.isEmpty(saleactivePkgInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有配置营销活动");
        }
        packageId = saleactivePkgInfo.getString("PACKAGE_ID");
        input.put("PACKAGE_ID", packageId);
        input.put("PRE_TYPE", "G_CONTRACT_SALE");

        // 营销资源转换
        IDataset elements = new DatasetList();
        if (StringUtils.isNotBlank(input.getString("RESOURCE_CODE")))
        {
            IDataset resourceCodeList = input.getDataset("RESOURCE_CODE");
            if (IDataUtil.isNotEmpty(resourceCodeList))// 有多条
            {
                for (int i = resourceCodeList.size() - 1; i >= 0; i--)
                {
                    String resourceCode = resourceCodeList.get(i).toString();
                    addResource(elements, resourceCode);
                }
            }
            else
            {
                // 只有一条
                String resourceCode = input.getString("RESOURCE_CODE");
                addResource(elements, resourceCode);
            }

            if (IDataUtil.isNotEmpty(elements))
            {
                input.put("ELEMENTS", elements.toString());
            }
        }
        // 入参转换结束
    }
}
