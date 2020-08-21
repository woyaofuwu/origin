package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.Point;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.PointLay;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.PointRs;

public class PointCal 
{
	/**
     * @param pl
     * @param multimatch
     * @return
     * @throws Exception
     */
    private static List<Point> getContexts(PointLay pl, boolean multimatch, BizPage page) throws Exception
    {
        List<Point> ps = new ArrayList<Point>();
        PointRs root = new PointRs();
        if (pl.getLayCount() <= 0)
            return ps;
        root.addAll(pl.get(0));
        for (int i = 1; i < pl.getLayCount(); i++)
        {
            root = getPoints(root, pl.get(i), multimatch, pl.getContexttype(), page);
            if (root.isMatch())
            {
                List<Integer> indexs = root.getIndexs();
                for (int j = 0; j < indexs.size(); j++)
                {
                    ps.add(root.get(indexs.get(j)));
                }
                if (!multimatch)
                {
                    return ps;
                }
            }
        }
        return ps;
    }

    public static List<Point> getContexts(String expression, IData values, boolean multimatch, String pointtype, BizPage page) throws Exception
    {
        PointLay pl = new PointLay(expression, values, pointtype);
        return getContextsWithCache(pl, multimatch, page);
    }

    public static List<Point> getContextsWithCache(PointLay pl, boolean multimatch, BizPage page) throws Exception
    {
        ArrayList<Point> ps = (ArrayList<Point>) getContexts(pl, multimatch, page);
        return ps;
    }

    /**
     * @param p1s
     * @param p2s
     * @param use_tags
     * @param multimatch
     * @param pointtype
     * @return
     * @throws Exception
     */
    private static PointRs getPoints(List<Point> p1s, List<Point> p2s, boolean multimatch, String pointtype, BizPage page) throws Exception
    {
        PointRs plist = new PointRs();
        for (Point p2 : p2s)
        {
            for (Point p1 : p1s)
            {
                PointRs ps = PointAssist.getPointRs(p1.getPointName(), p2.getPointName(), pointtype, page);
                if (!multimatch)
                    return ps;
                else
                {
                    List<Integer> list = ps.getIndexs();
                    if(DataUtils.isNotEmpty(list))
                    {
                    	for(int i = 0; i < list.size() ; i ++)
                    	{
                    		plist.addIndex(list.get(i)+plist.size());
                    	}
                    }
                    
                    plist.addAll(ps);
                }
            }
        }
        return plist;
    }
}
