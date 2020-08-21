
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PoProductQry
{

    /*
     * @descrption 获取产品信息
     * @author xunyl
     * @date 2013-07-25
     */
    public static IData getPoProductInfoByPK(IData inparam) throws Exception
    {
        return Dao.qryByPK("TD_F_POPRODUCT", inparam, Route.CONN_CRM_CEN);
    }

    /**
     * 通过产品编号查询BBOSS产品名称
     * 
     * @param specNumber
     * @return
     * @throws Exception
     */
    public static String getProductSpecNameByProductSpecNumber(String productSpecNumber) throws Exception
    {
    	String retStr = "";
        //return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_F_POPRODUCT", "PRODUCTSPECNUMBER", "PRODUCTSPECNAME", productSpecNumber);
    	IDataset ids = UpcCall.queryProductSpecNameByProductSpecNumber(productSpecNumber);
    	if(IDataUtil.isNotEmpty(ids)){
    		retStr = ids.getData(0).getString("PRODUCTSPECNAME", "");
    	}
    	return retStr;
    }

    /**
     *通过商产品编号查询产品信息
     * 
     * @author liuxx3
     *@date 20140-07-08
     */
    public static IDataset qryProductInfosByProductSpecNumber(String pospecnumber, String productSpecNumber) throws Exception
    {
        IDataset poDatas = UpcCall.queryPoproductByProductSpecNumber(productSpecNumber, pospecnumber);
		return poDatas;
    }

    public static IDataset qryProInfosByPoSpecNumber(String pospecnumber) throws Exception
    {
        IData param = new DataMap();
        param.put("POSPECNUMBER", pospecnumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select  ");
        parser.addSQL("T.POSPECNUMBER, ");
        parser.addSQL("T.PRODUCTSPECNUMBER, ");
        parser.addSQL("T.PRODUCTSPECNAME, ");
        parser.addSQL("T.PRODUCTSPECID_B, ");
        parser.addSQL("T.STATUS, ");
        parser.addSQL("T.RELATIONTYPE, ");
        parser.addSQL("T.RSRV_STR1, ");
        parser.addSQL("T.RSRV_STR2, ");
        parser.addSQL("T.RSRV_STR3, ");
        parser.addSQL("T.RSRV_STR4, ");
        parser.addSQL("T.RSRV_STR5, ");
        parser.addSQL("T.RSRV_NUM1, ");
        parser.addSQL("T.RSRV_NUM2, ");
        parser.addSQL("T.RSRV_DATE1, ");
        parser.addSQL("T.RSRV_DATE2, ");
        parser.addSQL("T.UPDATE_TIME, ");
        parser.addSQL("T.REMARK ");
        parser.addSQL("from TD_F_POPRODUCT T ");
        parser.addSQL("Where 1=1  ");
        parser.addSQL("AND T.POSPECNUMBER = :POSPECNUMBER ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

}
