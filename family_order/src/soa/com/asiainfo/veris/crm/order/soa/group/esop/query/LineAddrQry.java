package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class LineAddrQry extends CSBizBean {
    public static IDataset qryQuickOpenLineAddr(String standardAddr, String city, String area, Pagination page) throws Exception {

        IData params = new DataMap();
        if(!"".equals(area)) {
            if ("市".equals(area.substring(area.length()-1)) || "县".equals(area.substring(area.length()-1)) || "琼中黎族苗族自治县".equals(area) || "保亭黎族苗族自治县".equals(area) || "白沙黎族自治县".equals(area) || "昌江黎族自治县".equals(area) || "乐东黎族自治县".equals(area) || "陵水黎族自治县".equals(area)) {
                area = city;
            }
        }
        params.put("GIS1", city);
        params.put("GIS2", area);
        params.put("DETAILGIS", standardAddr);
        String partName = "";
        partName = params.getString("GIS1");
        String partCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
                { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[]
                { "PAR_CITY_CODE", partName });
        if("".equals(partCode) || partCode == null) {
            partCode = "PAR_OTHERS";
        }
        SQLParser parser = new SQLParser(params);
        parser.addSQL("SELECT DEVICE_NAME, ");
        parser.addSQL("       DEVICE_ID, ");
        parser.addSQL("       GIS  PROVINCE_ADDR, ");
        parser.addSQL("       GIS1  CITY_ADDR, ");
        parser.addSQL("       GIS2  AREA_ADDR, ");
        parser.addSQL("       GIS3  COUNTY_ADDR, ");
        parser.addSQL("       GIS4  VILLAGE_ADDR, ");
        parser.addSQL("       DETAILGIS  STANDARD_ADDR, ");
        parser.addSQL("       CAPACITY_UNUSED, ");
        parser.addSQL("       UPDATETIME ");
        parser.addSQL("FROM TD_B_EOP_DEVICEINFO PARTITION("+partCode);
        parser.addSQL(") WHERE 1=1 ");
        parser.addSQL("  AND ( :DETAILGIS is null or  instr(DETAILGIS,:DETAILGIS)>0 ) ");
        parser.addSQL("  AND ( :GIS2 is null or GIS2=:GIS2 ) ");

        IDataset dataset = Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset qryCoverInfoAddr(String standardAddr) throws Exception {
        SQLParser parser = new SQLParser(null);
        parser.addSQL("SELECT T.* FROM TD_B_EOP_COVERINFOADDR T");
        parser.addSQL(" WHERE T.STANDARD_ADDR = '" + standardAddr + "'");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset queryLineCoverAddr(String standardAddr, String coverTag, Pagination page) throws Exception {
        SQLParser parser = new SQLParser(null);
        parser.addSQL("SELECT T.* FROM TD_B_EOP_COVERINFOADDR T");
        parser.addSQL(" WHERE T.STANDARD_ADDR like '%" + standardAddr + "%'");
        if (coverTag != null && !coverTag.equals("")) {
            parser.addSQL(" and IS_COVER_TAG = '" + coverTag + "'");
        }
        IDataset dataset = Dao.qryByParse(parser, page, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IData qryExistQuickOpenLineAddr(String standardAddr) throws Exception {
        SQLParser parser = new SQLParser(null);
        parser.addSQL("SELECT T.* FROM TD_B_EOP_STANDARDADDR T");
        parser.addSQL(" WHERE T.STANDARD_ADDR = :STANDARD_ADDR");
        IDataset dataset = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
        if (IDataUtil.isNotEmpty(dataset)) {
            return dataset.getData(0);
        } else {
            return null;
        }
    }
}
