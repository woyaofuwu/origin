package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

/**
 * @author Administrator
 */
public class MoListData
{
    private String MoCode;// 服务ID

    private String MoMath;

    private String MoType;

    private String DestServCode;

    private String DestServCodeMath;

    private String Remarks;

    private String seqId;

    private String tag;

    public String getMoCode()
    {
        return MoCode;
    }

    public void setMoCode(String moCode)
    {
        MoCode = moCode;
    }

    public String getMoMath()
    {
        return MoMath;
    }

    public void setMoMath(String moMath)
    {
        MoMath = moMath;
    }

    public String getMoType()
    {
        return MoType;
    }

    public void setMoType(String moType)
    {
        MoType = moType;
    }

    public String getDestServCode()
    {
        return DestServCode;
    }

    public void setDestServCode(String destServCode)
    {
        DestServCode = destServCode;
    }

    public String getDestServCodeMath()
    {
        return DestServCodeMath;
    }

    public void setDestServCodeMath(String destServCodeMath)
    {
        DestServCodeMath = destServCodeMath;
    }

    public String getRemarks()
    {
        return Remarks;
    }

    public void setRemarks(String remarks)
    {
        Remarks = remarks;
    }

    public String getSeqId()
    {
        return seqId;
    }

    public void setSeqId(String seqId)
    {
        this.seqId = seqId;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public static MoListData getInstance(IData moinfo) throws Exception
    {

        if (IDataUtil.isEmpty(moinfo))
        {
            return null;
        }

        MoListData moinforeg = new MoListData();
        moinforeg.setMoCode(moinfo.getString("MO_CODE"));
        moinforeg.setMoMath(moinfo.getString("MO_MATH"));
        moinforeg.setMoType(moinfo.getString("MO_TYPE"));
        moinforeg.setDestServCode(moinfo.getString("DEST_SERV_CODE"));
        moinforeg.setDestServCodeMath(moinfo.getString("DEST_SERV_CODE_MATH"));
        moinforeg.setSeqId(moinfo.getString("SEQ_ID"));
        moinforeg.setRemarks(moinfo.getString("REMARKS"));
        String tag = moinfo.getString("tag");
        if (StringUtils.isEmpty(tag))
        {
            tag = "2";
        }
        moinforeg.setTag(tag);
        return moinforeg;
    }
}
