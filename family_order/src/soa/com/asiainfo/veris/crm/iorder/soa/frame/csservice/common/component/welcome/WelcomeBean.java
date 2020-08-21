package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.component.welcome;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

import java.util.Date;

public class WelcomeBean extends CSBizBean {

    public IDataset getHotAndRecInfo(IData param) throws Exception{
        IData sqlParam = new DataMap();
        sqlParam.put("POPULARITY_TRADE_TYPE",param.getString("POPUTRADECODE"));
        //TF_F_POPULARITY表查询配置数据
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT A.MENU_ID,A.OFFER_NAME,A.OFFER_DESCRIPTION,A.OFFER_CODE,A.POPULARITY_TYPE,A.POPULARITY_TRADE_TYPE,A.POPULARITY_DEFAULT_ICON,A.POPULARITY_ICON,A.PRODUCT_ID AS SALE_PRODUCT_ID,A.CAMPN_TYPE FROM");
        sql.append(" TF_F_POPULARITY A WHERE A.POPULARITY_TRADE_TYPE=:POPULARITY_TRADE_TYPE AND SYSDATE>START_DATE AND SYSDATE<END_DATE ");
        sql.append(" ORDER BY A.POPULARITY_TYPE ASC,A.POPULARITY_TRADE_TYPE ASC,A.PRIORITY ASC");
        return Dao.qryBySql(sql, sqlParam);
    }

    public IDataset getPlatSvcPrice(String serviceId) throws Exception{
        IDataset priceList = new DatasetList();
        priceList = UpcCall.querySpComprehensiveInfoByServiceId(serviceId);
        return  priceList;
    }

    public static IDataset getProductInfo(String offerCode) throws Exception{
        IDataset offerList = new DatasetList();
        offerList = UpcCall.qryCatalogByCatalogId(offerCode);
        return  offerList;
    }

    public IDataset getScoreInfo(String offerCode) throws Exception {
        IData param = new DataMap();
        param.put("RULE_ID", offerCode);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT A.AMONTHS,A.BRAND_CODE,");
        sql.append("A.CLASS_LIMIT,A.DEPOSIT_CODE,");
        sql.append("A.END_DATE,A.EPARCHY_CODE,");
        sql.append("A.EXCHANGE_LIMIT,A.EXCHANGE_MODE,");
        sql.append("A.EXCHANGE_TYPE_CODE");
        sql.append(",A.FMONTHS,");
        sql.append("A.GIFT_TYPE_CODE,A.MONEY_RATE,A.REMARK,A.REWARD_LIMIT,A.RIGHT_CODE,");
        sql.append("A.RSRV_STR1,A.RSRV_STR2,A.RSRV_STR3,A.RSRV_STR4,A.RSRV_STR5,");
        sql.append("A.RULE_ID,");
        sql.append("A.RULE_NAME,");
        sql.append("A.SCORE,A.SCORE_NUM,A.SCORE_TYPE_CODE,A.START_DATE,A.STATUS,A.UNIT,A.UPDATE_DEPART_ID,A.UPDATE_STAFF_ID,A.UPDATE_TIME,B.EXCHANGE_TYPE");
        sql.append(" FROM TD_B_EXCHANGE_RULE A,TD_B_SCORE_EXCHANGE_TYPE B");
        sql.append(" WHERE A.EXCHANGE_TYPE_CODE(+) = B.EXCHANGE_TYPE_CODE");
        sql.append(" AND   A.STATUS = '0'");
        sql.append(" AND   A.END_DATE >= SYSDATE");
        sql.append(" AND   A.RULE_ID=:RULE_ID");
        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }
    //根据munuId查询，title，url和path
    public static IData qrySystemGuiMenu (String menuId) throws Exception {
        IData result = new DataMap();
        IData sqlParam = new DataMap();
        StringBuilder menuTempTile = new StringBuilder(1000);
        StringBuilder qryModeNameAndTileSql = new StringBuilder(1000);
        StringBuilder qryPathSql = new StringBuilder(1000);
        String path = "";
        String title = "";
        String modeName = "";
        sqlParam.put("MENU_ID",menuId);
        qryPathSql.append("SELECT * FROM TD_B_SYSTEMGUIMENU A START WITH  A.MENU_ID=:MENU_ID CONNECT BY PRIOR A.PARENT_MENU_ID=A.MENU_ID");
        IDataset menuList =  Dao.qryBySql(qryPathSql, sqlParam, Route.CONN_SYS);
        //拼接menuTile
        for (int i=menuList.size()-1;i>=0;i--) {
            menuTempTile.append(menuList.getData(i).getString("MENU_TITLE")+" > ");
        }
        if(menuTempTile.toString().length()>1) {
            path = menuTempTile.toString().substring(0,menuTempTile.toString().length()-2);
            //子系统编码
            String subSys = menuList.getData(0).getString("SUBSYS_CODE");
            String subSysStr = "CRM";
            if ("NCM".equals(subSys)) {
                subSysStr = "CRM";
            } else if ("BIL".equals(subSys)) {
                subSysStr = "计费账务";
            } else if ("SAG".equals(subSys)) {
                subSysStr = "统计分析";
            } else if ("NGC".equals(subSys)) {
                subSysStr = "客服";
            } else if ("NKF".equals(subSys)) {
                subSysStr = "客服新";
            } else if ("PBO".equals(subSys)) {
                subSysStr = "宽带PBOSS";
            } else if ("EMS".equals(subSys)) {
                subSysStr = "电子单据";
            }
            path = subSysStr+" > "+path;
        }
        qryModeNameAndTileSql.append("SELECT B.MENU_TITLE,A.MOD_NAME FROM TD_S_MODFILE A,TD_B_SYSTEMGUIMENU B WHERE A.MOD_CODE = B.MENU_ID AND A.MOD_CODE=:MENU_ID");
        IDataset titleAndNameList =  Dao.qryBySql(qryModeNameAndTileSql, sqlParam, Route.CONN_SYS);
        if(DataUtils.isNotEmpty(titleAndNameList)) {
            title = titleAndNameList.getData(0).getString("MENU_TITLE");
            modeName = titleAndNameList.getData(0).getString("MOD_NAME");
        }
        result.put("MENU_PATH_NAME",path);
        result.put("MENU_TITLE",title);
        result.put("MENU_URL",modeName);
        return  result;
    }
    public static boolean setDescriptionAndName(IData iData,String offerType) throws Exception {
        IDataset iDataset = UpcCall.queryOfferInfoByOfferCodeAndOfferType(iData.getString("OFFER_CODE"),offerType);
        if(DataUtils.isEmpty(iDataset)) {
            return true;
        }
        //过滤生失效
        if(DataUtils.isNotEmpty(iDataset)) {
            Date vaildDate = TimeUtil.getDate(iDataset.getData(0).getString("VALID_DATE"));
            Date expireDate = TimeUtil.getDate(iDataset.getData(0).getString("EXPIRE_DATE"));
            Date currentDate = TimeUtil.getDefaultSysDate();
            if(currentDate.before(vaildDate) || currentDate.after(expireDate)) {
                return true;
            }
        }
        if(DataUtils.isNotEmpty(iDataset)  && DataUtils.isEmpty(iData.getString("OFFER_DESCRIPTION"))) {
            iData.put("OFFER_DESCRIPTION",iDataset.getData(0).getString("DESCRIPTION"));
        }
        if(DataUtils.isNotEmpty(iDataset)  && DataUtils.isEmpty(iData.getString("OFFER_NAME"))) {
            iData.put("OFFER_NAME",iDataset.getData(0).getString("OFFER_NAME"));
        }
        return false;
    }


}
