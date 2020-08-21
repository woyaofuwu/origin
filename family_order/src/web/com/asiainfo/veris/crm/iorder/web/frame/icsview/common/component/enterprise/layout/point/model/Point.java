package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model;

import com.ailk.common.data.IData;

/**
 * @author
 */
public class Point
{

    private String pointName;

    private String pointType;
    
    private String pointTwo;
    
    private String pointOne;
    
    private String seq;
    
    private String useTag;
    
    private String templetId;
    
    private String extraId;
    
    private String rsrvStr1;
    
    private String rsrvStr2;
    
    private String rsrvStr3;
    
    private String rsrvStr4;

    public static final String POINT_TYPE_POINT = "0";

    public static final String POINT_TYPE_PAGEAREA = "1";

    public static final String POINT_TYPE_TRANSFER = "2";
    
    public static final String DEFAUL_USE_TAG = "36";

    public Point(String pointName)
    {
        this.pointName = pointName;
    }

    public Point(String express, IData value)
    {
        if (express.charAt(0) == '[')
        {
            pointName = express.substring(1, express.length() - 1);
        }
        else
        {
            pointName = value.getString(express);
        }
    }

    public String getPointName()
    {
        return pointName;
    }
    
    public String getPointTwo()
    {
        return pointTwo;
    }
    
    public String getPointOne()
    {
        return pointOne;
    }
    
    public void setPointTwo(String pointTwo)
    {
        this.pointTwo = pointTwo;
    }

    public String getPointType()
    {
        return pointType;
    }

    public void setPointName(String pointName)
    {
        this.pointName = pointName;
    }
    
    public void setPointOne(String pointOne) 
    {
        this.pointOne = pointOne;
    }

    public void setPointType(String pointType)
    {
        this.pointType = pointType;
    }

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getUseTag() {
		return useTag;
	}

	public void setUseTag(String useTag) {
		this.useTag = useTag;
	}

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

	public String getRsrvStr4() {
		return rsrvStr4;
	}

	public void setRsrvStr4(String rsrvStr4) {
		this.rsrvStr4 = rsrvStr4;
	}

	public String getTempletId() {
		return templetId;
	}

	public void setTempletId(String templetId) {
		this.templetId = templetId;
	}

	public String getExtraId() {
		return extraId;
	}

	public void setExtraId(String extraId) {
		this.extraId = extraId;
	}
}
