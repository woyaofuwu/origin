
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.client.ServiceFactory;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.Point;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.layout.point.model.PointRs;

public class PointAssist
{

	private static final Logger logger = Logger.getLogger(PointAssist.class);
    /**
     * 根据point_one，point_two和标志位查询point表
     * 
     * @param p1
     * @param p2
     * @param use_tags
     * @return
     * @throws Exception
     */
    public static IDataset getNewPoints(String p1, String p2, BizPage page) throws Exception
    {
    	logger.debug("p1==========================" + p1);
    	logger.debug("p2==========================" + p2);
        if(p1 == null || p2 == null || "".equals(p1) || "".equals(p2))
        {
    	   return new DatasetList();
        }
        
        IDataInput dataInput = new DataInput();
        dataInput.getData().put("POINT_ONE", p1);
        dataInput.getData().put("POINT_TWO", p2);
        
        IDataOutput output = ServiceFactory.call("SS.PointInfoSVC.queryPoint", dataInput);
		IDataset dataset = output.getData();
        
        return dataset;
    }

    public static List<Point> getPointListNewPoints(String p1, String p2, String[] use_tags, BizPage page) throws Exception
    {
        IDataset ds = getNewPoints(p1, p2, page);
        List<Point> ps = new ArrayList<Point>();
        for (int i = 0; i < ds.size(); i++)
        {
            IData data = ds.getData(i);
            Point p = new Point(data.getString("NEW_POINT"));
            p.setPointType(data.getString("POINT_TYPE"));
            p.setSeq(data.getString("SEQ"));
            p.setUseTag(data.getString("USE_TAG"));
            p.setPointTwo(data.getString("POINT_TWO"));
            p.setPointOne(data.getString("POINT_ONE"));
            p.setTempletId(data.getString("TEMPLET_ID"));
            p.setExtraId(data.getString("EXTRA_ID"));
            p.setRsrvStr1(data.getString("RSRV_STR1"));
            p.setRsrvStr2(data.getString("RSRV_STR2"));
            p.setRsrvStr3(data.getString("RSRV_STR3"));
            p.setRsrvStr4(data.getString("RSRV_STR4"));
            ps.add(p);
        }
        return ps;
    }

    /**
     * 返回查询出的point表数据
     * 
     * @param p1
     * @param p2
     * @param use_tags
     * @param pointtype
     * @return
     * @throws Exception
     */
    public static PointRs getPointRs(String p1, String p2, String pointtype, BizPage page) throws Exception
    {
        IDataset ds = getNewPoints(p1, p2, page);
        PointRs ps = new PointRs();
        if(null != ds && ds.size() > 0)
        {
            for (int i = 0; i < ds.size(); i++)
            {
                IData data = ds.getData(i);
                Point p = new Point(data.getString("NEW_POINT"));
                p.setPointType(data.getString("POINT_TYPE"));
                p.setSeq(data.getString("SEQ"));
                p.setUseTag(data.getString("USE_TAG"));
                p.setPointTwo(data.getString("POINT_TWO"));
                p.setPointOne(data.getString("POINT_ONE"));
                p.setTempletId(data.getString("TEMPLET_ID"));
                p.setExtraId(data.getString("EXTRA_ID"));
                p.setRsrvStr1(data.getString("RSRV_STR1"));
                p.setRsrvStr2(data.getString("RSRV_STR2"));
                p.setRsrvStr3(data.getString("RSRV_STR3"));
                p.setRsrvStr4(data.getString("RSRV_STR4"));
                ps.add(p);
                if (pointtype.equals(data.getString("POINT_TYPE", "")))
                    ps.addIndex(i);
            }
        }
        return ps;
    }
}
