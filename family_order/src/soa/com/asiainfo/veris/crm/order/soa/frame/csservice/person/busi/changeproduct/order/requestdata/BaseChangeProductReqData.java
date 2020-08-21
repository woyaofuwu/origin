
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.requestdata;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class BaseChangeProductReqData extends BaseReqData
{

    protected boolean effectNow = false;// 是否立即生效

    protected String remark;// 备注

    protected boolean bookingTag = false;// 是否预约：【预约：true】

    protected String bookingDate; // 预约时间

    private ProductData newMainProduct;

    protected List<ProductData> plusProducts;

    protected List<ProductModuleData> productElements;// 本次变化的元素列表
    
    private String rsrvStr1;
    
    private String rsrvStr2;
    
    private String rsrvStr3;
    
    private String rsrvStr7;
    
    private String rsrvStr8;
    
    public String getRsrvStr1() {
		return rsrvStr1;
	}

	public void setRsrvStr1(String rsrvStr1) {
		this.rsrvStr1 = rsrvStr1;
	}

	public String getRsrvStr2() {
		return rsrvStr2;
	}

	public void setRsrvStr2(String rsrvStr2) {
		this.rsrvStr2 = rsrvStr2;
	}

	public String getRsrvStr3() {
		return rsrvStr3;
	}

	public void setRsrvStr3(String rsrvStr3) {
		this.rsrvStr3 = rsrvStr3;
	}
	
	public String getRsrvStr7() {
		return rsrvStr7;
	}

	public void setRsrvStr7(String rsrvStr7) {
		this.rsrvStr7 = rsrvStr7;
	}

	public String getRsrvStr8() {
		return rsrvStr8;
	}

	public void setRsrvStr8(String rsrvStr8) {
		this.rsrvStr8 = rsrvStr8;
	}
	

    public String getBookingDate()
    {
        return bookingDate;
    }

    public ProductData getNewMainProduct()
    {
        return newMainProduct;
    }

    private List<ProductData> getPlusProducts()
    {
        return plusProducts;
    }

    public List<ProductModuleData> getProductElements()
    {
        return productElements;
    }

    public boolean isBookingTag()
    {
        return bookingTag;
    }

    public boolean isEffectNow()
    {
        return effectNow;
    }

    public void setBookingDate(String bookingDate)
    {
        this.bookingDate = bookingDate;
    }

    public void setBookingTag(boolean bookingTag)
    {
        this.bookingTag = bookingTag;
    }

    public void setEffectNow(boolean effectNow)
    {
        this.effectNow = effectNow;
    }

    public void setNewMainProduct(String newProductId) throws Exception
    {
        this.newMainProduct = new ProductData(newProductId);
    }

    private void setPlusProducts(List<ProductData> plusProducts)
    {
        this.plusProducts = plusProducts;
    }

    public void setProductElements(List<ProductModuleData> productElements)
    {
        this.productElements = productElements;
    }

}
