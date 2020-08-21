
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model;

import java.util.ArrayList;
import java.util.List;

public class PointRs extends ArrayList<Point>
{
    private static final long serialVersionUID = -30239577134428528L;
    
    private transient List<Integer> indexs = new ArrayList<Integer>();

    public void addIndex(int index)
    {
        this.indexs.add(index);
    }

    public List<Integer> getIndexs()
    {
        return indexs;
    }

    public boolean isMatch()
    {
        return this.indexs.size() > 0;
    }
}
