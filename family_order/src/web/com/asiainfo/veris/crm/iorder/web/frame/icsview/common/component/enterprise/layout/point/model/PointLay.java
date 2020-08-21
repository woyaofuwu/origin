package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.CommonTools;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.common.LayoutConstants;

public class PointLay extends ArrayList<List<Point>>
{
    private String contexttype;

    public PointLay()
    {
    }

    public PointLay(String expression, IData values, String contexttype)
    {
        super();
        this.contexttype = contexttype;
        String[] keystrs = CommonTools.split(expression, LayoutConstants.SPLIT_EXPRESS);
        for (String keystr : keystrs)
        {
            String[] keys = CommonTools.split(keystr, LayoutConstants.SPLIT_KEY);
            List<Point> ps = new ArrayList<Point>();
            for (String key : keys)
            {
                ps.add(new Point(key, values));
            }
            this.add(ps);
        }
    }

    public String getContexttype()
    {
        return contexttype;
    }

    public int getLayCount()
    {
        return size();
    }

    public List<Point> getPointNames(int index)
    {
        return get(index);
    }
    public void setContexttype(String contexttype)
    {
        this.contexttype = contexttype;
    }
}
