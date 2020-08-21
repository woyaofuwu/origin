
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;

public class CreateNpUserReqData extends BaseReqData
{

    private String homeOperator;// 归属运营商

    private String netWorkType;// 网络类型

    private String superBankCode;// 上级银行

    private String provCode;// 省别编码

    private String invoiceNo;// 发票号

    private String isRealName;// 是否实名制 1是

    private String isNpBack;// 是否快速携回 1是
    
    private String authCode;// 授权码
	
	private String authCodeExpired;// 授权码有效期
	
	private String handerInput4A;// 手输身份4A -1
	
	private String more5Pspt4A;// 一证5号4A -1
	
	
	public String getHanderInput4A() {
		return handerInput4A;
	}

	public void setHanderInput4A(String handerInput4A) {
		this.handerInput4A = handerInput4A;
	}

	public String getMore5Pspt4A() {
		return more5Pspt4A;
	}

	public void setMore5Pspt4A(String more5Pspt4A) {
		this.more5Pspt4A = more5Pspt4A;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getAuthCodeExpired() {
		return authCodeExpired;
	}

	public void setAuthCodeExpired(String authCodeExpired) {
		this.authCodeExpired = authCodeExpired;
	}



    // ---------------------------产品信息---------------------------------//

    private ProductData mainProduct;

    private List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();// 本次变化的元素列表

    private OtherInfo other;// 其它信息

    private List<PostInfo> posts = new ArrayList<PostInfo>();// 邮寄信息

    private List<ResInfo> resInfos = new ArrayList<ResInfo>();// 资源信息

    public final void addPmd(ProductModuleData pmd)
    {
        this.pmds.add(pmd);
    }

    public final void addPosts(PostInfo e)
    {
        this.posts.add(e);

    }

    public final void addResInfo(ResInfo resInfo)
    {
        this.resInfos.add(resInfo);
    }

    public final String getHomeOperator()
    {
        return homeOperator;
    }

    public final String getInvoiceNo()
    {
        return invoiceNo;
    }

    public final String getIsNpBack()
    {
        return isNpBack;
    }

    public final String getIsRealName()
    {
        return isRealName;
    }

    public final ProductData getMainProduct()
    {
        return mainProduct;
    }

    public final String getNetWorkType()
    {
        return netWorkType;
    }

    public final OtherInfo getOther()
    {
        return other;
    }

    public final List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    public final List<PostInfo> getPosts()
    {
        return posts;
    }

    public final String getProvCode()
    {
        return provCode;
    }

    public final List<ResInfo> getResInfos()
    {
        return resInfos;
    }

    public final String getSuperBankCode()
    {
        return superBankCode;
    }

    public final void setHomeOperator(String homeOperator)
    {
        this.homeOperator = homeOperator;
    }

    public final void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public final void setIsNpBack(String isNpBack)
    {
        this.isNpBack = isNpBack;
    }
   
    public final void setIsRealName(String isRealName)
    {
        this.isRealName = isRealName;
    }

    public final void setMainProduct(ProductData mainProduct)
    {
        this.mainProduct = mainProduct;
    }

    public final void setNetWorkType(String netWorkType)
    {
        this.netWorkType = netWorkType;
    }

    public final void setOther(OtherInfo other)
    {
        this.other = other;
    }

    public final void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }

    public final void setPosts(List<PostInfo> posts)
    {
        this.posts = posts;
    }

    public final void setProvCode(String provCode)
    {
        this.provCode = provCode;
    }

    public final void setResInfos(List<ResInfo> resInfos)
    {
        this.resInfos = resInfos;
    }

    public final void setSuperBankCode(String superBankCode)
    {
        this.superBankCode = superBankCode;
    }

}
