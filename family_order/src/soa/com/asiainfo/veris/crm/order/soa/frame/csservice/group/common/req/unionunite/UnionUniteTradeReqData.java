
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.req.unionunite;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMemberReqData;

public class UnionUniteTradeReqData extends CreateGroupMemberReqData
{
    private String memUserID;

    private String productID;

    private String relationTypeCode;

    private String serialNumber;

    private UcaData targetUca;

    private String userID;

    /**
     * @return the memUserID
     */
    public String getMemUserID()
    {
        return memUserID;
    }

    /**
     * @return the productID
     */
    public String getProductID()
    {
        return productID;
    }

    /**
     * @return the relationTypeCode
     */
    public String getRelationTypeCode()
    {
        return relationTypeCode;
    }

    /**
     * @return the serialNumber
     */
    public String getSerialNumber()
    {
        return serialNumber;
    }

    /**
     * @return the targetUca
     */
    public UcaData getTargetUca()
    {
        return targetUca;
    }

    /**
     * @return the userID
     */
    public String getUserID()
    {
        return userID;
    }

    /**
     * @param memUserID
     *            the memUserID to set
     */
    public void setMemUserID(String memUserID)
    {
        this.memUserID = memUserID;
    }

    /**
     * @param productID
     *            the productID to set
     */
    public void setProductID(String productID)
    {
        this.productID = productID;
    }

    /**
     * @param relationTypeCode
     *            the relationTypeCode to set
     */
    public void setRelationTypeCode(String relationTypeCode)
    {
        this.relationTypeCode = relationTypeCode;
    }

    /**
     * @param serialNumber
     *            the serialNumber to set
     */
    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    /**
     * @param targetUca
     *            the targetUca to set
     */
    public void setTargetUca(UcaData targetUca)
    {
        this.targetUca = targetUca;
    }

    /**
     * @param userID
     *            the userID to set
     */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }

}
