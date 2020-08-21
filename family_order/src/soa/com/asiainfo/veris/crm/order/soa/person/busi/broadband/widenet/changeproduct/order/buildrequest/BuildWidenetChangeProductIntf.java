
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproduct.order.requestdata.WidenetProductRequestData;

public class BuildWidenetChangeProductIntf extends BaseBuilder implements IBuilder
{

    public static IDataset getProductElements(WidenetProductRequestData request) throws Exception
    {
        String productId = "";

        IDataset productElements = null;

        UcaData uca = request.getUca();

        ProductTradeData nextMainProduct = uca.getUserNextMainProduct();

        ProductData newMainProduct = request.getNewMainProduct();

        if (newMainProduct != null && !uca.getProductId().equals(newMainProduct.getProductId()))// 当既有预约产品,又办理新产品时候报错
        {
            if (nextMainProduct != null)
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_195);
            }
        }

        if (newMainProduct != null)// 取新产品ID
        {
            productId = newMainProduct.getProductId();
        }
        else if (nextMainProduct != null)// 取预约产品ID
        {
            productId = nextMainProduct.getProductId();
        }
        else
        {
            productId = uca.getProductId();
        }

        productElements = ProductInfoQry.getProductElements(productId, uca.getUserEparchyCode());

        return productElements;
    }

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        WidenetProductRequestData request = (WidenetProductRequestData) brd;
        IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        request.setNewMainProduct(param.getString("PRODUCT_ID"));
        IDataset productElements = this.getProductElements(request);

        // 处理元素
        List<ProductModuleData> elements = new ArrayList<ProductModuleData>();
        for (int i = 0, size = selectedElements.size(); i < size; i++)
        {
            IData element = selectedElements.getData(i);

            String elementId = element.getString("ELEMENT_ID");
            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
            String modifyTag = element.getString("MODIFY_TAG");

            IData pkgElement = this.getPackageElement(productElements, elementId, elementTypeCode);

            if (IDataUtil.isNotEmpty(pkgElement))// 设置PRODUCT_ID,PACKAGE_ID
            {
                element.put("PRODUCT_ID", pkgElement.getString("PRODUCT_ID"));
                element.put("PACKAGE_ID", pkgElement.getString("PACKAGE_ID"));
                // 判断资费是否是产品下默认必选优惠，默认必选优惠后台会自动处理
                if ("1".equals(pkgElement.getString("PACKAGE_FORCE_TAG")) && "1".equals(pkgElement.getString("ELEMENT_FORCE_TAG")))
                {
                    continue;
                }
            }
            else
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_111, elementId);
            }

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                DiscntData discnt = new DiscntData(element);
                elements.add(discnt);
            }
        }
        request.setProductElements(elements);
    }

    public BaseReqData getBlankRequestDataInstance()
    {

        return new WidenetProductRequestData();
    }

    public IData getPackageElement(IDataset productElements, String elementId, String elementTypeCode) throws Exception
    {
        int size = productElements.size();
        for (int i = 0; i < size; i++)
        {
            IData element = productElements.getData(i);
            if (elementId.equals(element.getString("ELEMENT_ID")) && elementTypeCode.equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                return element;
            }
        }
        return null;
    }

}
