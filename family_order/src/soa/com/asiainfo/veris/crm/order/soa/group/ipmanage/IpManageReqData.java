
package com.asiainfo.veris.crm.order.soa.group.ipmanage;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class IpManageReqData extends MemberReqData
{

    private String SerialNumber;

    private String OpenDate;

    private String BrandCode;

    private String ProductId;

    private String OldProductId;

    private String MDealTag;

    private String UserPasswd;

    private String Conditionm;

    private String OlcomTag;

    private String UserIdB;

    private String IPServiceText;

    private String OldPServiceText;

    private String DiscntCode;

    private String OldDiscntCode;

    private String Packagesvc;

    private IDataset DataList;

    public void setSerialNumber(String SerialNumber)
    {
        this.SerialNumber = SerialNumber;
    }

    public void setOpenDate(String OpenDate)
    {
        this.OpenDate = OpenDate;
    }

    public void setBrandCode(String BrandCode)
    {
        this.BrandCode = BrandCode;
    }

    public void setProductId(String ProductId)
    {
        this.ProductId = ProductId;
    }

    public void setOldProductId(String OldProductId)
    {
        this.OldProductId = OldProductId;

    }

    public void setMDealTag(String MDealTag)
    {
        this.MDealTag = MDealTag;

    }

    public void setUserPasswd(String UserPasswd)
    {
        this.UserPasswd = UserPasswd;

    }

    public void setConditionm(String Conditionm)
    {
        this.Conditionm = Conditionm;

    }

    public void setOlcomTag(String OlcomTag)
    {
        this.OlcomTag = OlcomTag;

    }

    public void setUserIdB(String UserIdB)
    {
        this.UserIdB = UserIdB;

    }

    public void setIPServiceText(String IPServiceText)
    {
        this.IPServiceText = IPServiceText;

    }

    public void setOldPServiceText(String OldPServiceText)
    {
        this.OldPServiceText = OldPServiceText;

    }

    public void setDiscntCode(String DiscntCode)
    {
        this.DiscntCode = DiscntCode;

    }

    public void setOldDiscntCode(String OldDiscntCode)
    {
        this.OldDiscntCode = OldDiscntCode;

    }

    public void setPackagesvc(String Packagesvc)
    {
        this.Packagesvc = Packagesvc;

    }

    public String getSerialNumber()
    {
        return SerialNumber;

    }

    public String getOpenDate()
    {
        return OpenDate;

    }

    public String getBrandCode()
    {
        return BrandCode;

    }

    public String getProductId()
    {
        return ProductId;

    }

    public String getOldProductId()
    {
        return OldProductId;

    }

    public String getMDealTag()
    {
        return MDealTag;

    }

    public String getUserPasswd()
    {
        return UserPasswd;

    }

    public String getConditionm()
    {
        return Conditionm;

    }

    public String getOlcomTag()
    {
        return OlcomTag;

    }

    public String getUserIdB()
    {
        return UserIdB;

    }

    public String getIPServiceText()
    {
        return IPServiceText;

    }

    public String getOldPServiceText()
    {
        return OldPServiceText;

    }

    public String getDiscntCode()
    {
        return DiscntCode;

    }

    public String getOldDiscntCode()
    {
        return OldDiscntCode;

    }

    public String getPackagesvc()
    {
        return Packagesvc;

    }

    public void setDataList(IDataset DataList)
    {
        this.DataList = DataList;

    }

    public IDataset getDataList()
    {
        return DataList;
    }

}
