
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.BaseCreateUserRequestData;

public class BuildIntfCreatePersonUserRequestData extends BuildCreatePersonUserRequestData implements IBuilder
{

    public void buildElems(IData param, BaseCreateUserRequestData brd) throws Exception
    {
        String openType = param.getString("OPEN_TYPE");
        if(StringUtils.isNotEmpty(openType) && "BESTUSE_OPEN".equals(openType)){
            super.buildElems(param, brd);
            return;
        }
        String product_id = param.getString("PRODUCT_ID");
        String eparchyCode = param.getString("TRADE_EPARCHY_CODE");
        String cityCode = param.getString("TRADE_CITY_CODE");
        /* */
        IData productInfo = UProductInfoQry.getProductInfo(product_id);
        if (IDataUtil.isEmpty(productInfo))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_135, product_id);
        }
        param.put("PRODUCT_ID", product_id);
        // 默认必选服务处理
        IDataset svcElems = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, product_id, BofConst.ELEMENT_TYPE_CODE_SVC);
        if (svcElems.isEmpty())
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_192, product_id);
        }
        boolean gprsFlag = false;
        String packageIdForGprs = "";
        for (int i = 0; i < svcElems.size(); i++)
        {
            IData elem = svcElems.getData(i);
            if ("22".equals(elem.getString("SERVICE_ID")))
            {
                gprsFlag = true;
                packageIdForGprs = elem.getString("PACKAGE_ID");
            }
            SvcData svcData = new SvcData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(brd.getAcceptTime());
            String startDate = ProductModuleCalDate.calStartDate(svcData, env);
            svcData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(svcData, startDate);
            svcData.setEndDate(endDate);
            svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            List<AttrData> attrDatas = new ArrayList<AttrData>();
//            IDataset svcAttrItems = AttrItemInfoQry.getSvcElementItemaByPk(elem.getString("SERVICE_ID"), eparchyCode);
            IDataset svcAttrItems = AttrItemInfoQry.getElementItemA(BofConst.ELEMENT_TYPE_CODE_SVC, elem.getString("SERVICE_ID"), eparchyCode);
            for (int j = 0; j < svcAttrItems.size(); j++)
            {
                IData svcAttrItem = svcAttrItems.getData(j);
                if("0".equals(svcAttrItem.getString("ATTR_CAN_NULL",""))){
                	if ("".equals(svcAttrItem.getString("ATTR_INIT_VALUE")))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_193, elem.getString("SERVICE_ID"), svcAttrItem.getString("ATTR_CODE"));
                    }
                    else
                    {
                        AttrData attrData = new AttrData();
                        attrData.setAttrCode(svcAttrItem.getString("ATTR_CODE"));
                        attrData.setAttrValue(svcAttrItem.getString("ATTR_INIT_VALUE"));
                        attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        attrDatas.add(attrData);
                    }
                }
            }
            svcData.setAttrs(attrDatas);
            brd.addPmd(svcData);

        }

        // 22服务绑定902优惠
        if (gprsFlag)
        {
            IData elemD = new DataMap();
            ProductTimeEnv env = new ProductTimeEnv();
            elemD.put("PRODUCT_ID", product_id);
            elemD.put("PACKAGE_ID", packageIdForGprs);
            elemD.put("DISCNT_CODE", "902");
            DiscntData discntData = new DiscntData(elemD);
            env.setBasicAbsoluteStartDate(brd.getAcceptTime());
            String startDateD = ProductModuleCalDate.calStartDate(discntData, env);
            discntData.setStartDate(startDateD);
            String endDateD = ProductModuleCalDate.calEndDate(discntData, startDateD);
            discntData.setEndDate(endDateD);
            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            brd.addPmd(discntData);
        }
        // 默认必选优惠处理
        IDataset discntEles = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, product_id, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        for (int i = 0; i < discntEles.size(); i++)
        {
            IData elem = discntEles.getData(i);
            DiscntData discntData = new DiscntData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(param.getString("OPEN_DATE"));
            String startDate = ProductModuleCalDate.calStartDate(discntData, env);
            discntData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);
            discntData.setEndDate(endDate);
            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            List<AttrData> attrDatas = new ArrayList<AttrData>();
//            IDataset discntAttrItems = AttrItemInfoQry.getDiscntElementItemaByPk(elem.getString("DISCNT_CODE"), eparchyCode);
            IDataset discntAttrItems = AttrItemInfoQry.getElementItemA(BofConst.ELEMENT_TYPE_CODE_DISCNT, elem.getString("DISCNT_CODE"), eparchyCode);
            for (int j = 0; j < discntAttrItems.size(); j++)
            {
                IData discntAttrItem = discntAttrItems.getData(j);
                if("0".equals(discntAttrItem.getString("ATTR_CAN_NULL",""))){
                	if ("".equals(discntAttrItem.getString("ATTR_INIT_VALUE")))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_194, elem.getString("DISCNT_CODE"), discntAttrItem.getString("ATTR_CODE"));
                    }
                    else
                    {
                        AttrData attrData = new AttrData();
                        attrData.setAttrCode(discntAttrItem.getString("ATTR_CODE"));
                        attrData.setAttrValue(discntAttrItem.getString("ATTR_INIT_VALUE"));
                        attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        attrDatas.add(attrData);
                    }
                }
            }
            discntData.setAttrs(attrDatas);
            brd.addPmd(discntData);
        }

        IDataset commparamInfos = CommparaInfoQry.getCommparaInfoByForceDiscnt("CSM", "829", product_id, cityCode, eparchyCode);
        if (IDataUtil.isNotEmpty(commparamInfos))
        {
            String package_id = commparamInfos.getData(0).getString("PARA_CODE2");
            String discnt_code = commparamInfos.getData(0).getString("PARA_CODE3");
            // 必选优惠组绑定的优惠单独处理
            IData elem = new DataMap();
            elem.put("PRODUCT_ID", product_id);
            elem.put("PACKAGE_ID", package_id);
            elem.put("DISCNT_CODE", discnt_code);
            DiscntData discntData = new DiscntData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(param.getString("OPEN_DATE"));
            String startDate = ProductModuleCalDate.calStartDate(discntData, env);
            discntData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);
            discntData.setEndDate(endDate);
            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);

            List<AttrData> attrDatas = new ArrayList<AttrData>();
//            IDataset discntAttrItems = AttrItemInfoQry.getDiscntElementItemaByPk(discnt_code, eparchyCode);
            IDataset discntAttrItems = AttrItemInfoQry.getElementItemA(BofConst.ELEMENT_TYPE_CODE_DISCNT, discnt_code, eparchyCode);
            for (int j = 0; j < discntAttrItems.size(); j++)
            {
                IData discntAttrItem = discntAttrItems.getData(j);
                if("0".equals(discntAttrItem.getString("ATTR_CAN_NULL",""))){
                	if ("".equals(discntAttrItem.getString("ATTR_INIT_VALUE")))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_194, discnt_code, discntAttrItem.getString("ATTR_CODE"));
                    }
                    else
                    {
                        AttrData attrData = new AttrData();
                        attrData.setAttrCode(discntAttrItem.getString("ATTR_CODE"));
                        attrData.setAttrValue(discntAttrItem.getString("ATTR_INIT_VALUE"));
                        attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        attrDatas.add(attrData);
                    }
                }
            }
            discntData.setAttrs(attrDatas);

            brd.addPmd(discntData);
        }

        makeFeeData(param, brd);
    }

    public void makeFeeData(IData param, BaseCreateUserRequestData brd) throws Exception
    {
        // String res_fee = param.getString("RES_FEE", "0");// 选号费
        String oper_fee = param.getString("OPER_FEE", "0");// 卡费
        String advance_fee = param.getString("ADVANCE_PAY", "0");// 预存款
        String foregift = param.getString("FOREGIFT", "0");// 押金费
        // 选号预存
        // if(Integer.parseInt(res_fee)>0){
        // FeeData feeData = new FeeData();
        // feeData.setFeeMode("2");
        // feeData.setFeeTypeCode("62");
        // feeData.setFee(res_fee);
        // feeData.setOldFee(res_fee);
        // brd.addFeeData(feeData);
        // }
        // 卡费
        if (Integer.parseInt(oper_fee) > 0)
        {
            FeeData feeData = new FeeData();
            feeData.setFeeMode("0");
            feeData.setFeeTypeCode("10");
            feeData.setFee(oper_fee);
            feeData.setOldFee(oper_fee);
            brd.addFeeData(feeData);
        }
        // 预存
        if (Integer.parseInt(advance_fee) >= 0)
        {
            FeeData feeData = new FeeData();
            feeData.setFeeMode("2");
            feeData.setFeeTypeCode("0");// 现金存折
            feeData.setFee(advance_fee);
            feeData.setOldFee(advance_fee);
            brd.addFeeData(feeData);
        }

        // 押金
        if (Integer.parseInt(foregift) > 0)
        {
            FeeData feeData = new FeeData();
            feeData.setFeeMode("1");
            feeData.setFeeTypeCode("0");// 存折类型不确定，先写死
            feeData.setFee(foregift);
            feeData.setOldFee(foregift);
            brd.addFeeData(feeData);
        }
        // 预存时，插0预存费用存折
        /*
         * FeeData feeData = new FeeData(); feeData.setFeeMode("2"); feeData.setFeeTypeCode("0"); feeData.setFee("0");
         * feeData.setOldFee("0"); brd.addFeeData(feeData);
         */
    }

}
