package com.asiainfo.veris.crm.order.soa.group.esop.query;

import java.util.Iterator;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class WorkformDisBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformDis(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_DIS", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset qryDisByIbsysid(String ibsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_DIS", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryDisByIbsysidRecordNum(String ibsysid, String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_DIS", "SEL_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delDisByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_DIS", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset queryWorkformDisStrList(String ibsysid, String mebOfferCode) throws Exception
    {
        IDataset wfDisStrList = new DatasetList();
        IDataset wfProductList = WorkformProductBean.qryProductListByIbsysidProductId(ibsysid, mebOfferCode);
        if(IDataUtil.isNotEmpty(wfProductList))
        {
            for(int i = 0, sizeI = wfProductList.size(); i < sizeI; i++)
            {
                IDataset wfDisList = qryDisByIbsysidRecordNum(ibsysid, wfProductList.getData(i).getString("RECORD_NUM"));
                if(IDataUtil.isEmpty(wfDisList))
                {
                    continue;
                }
                IData wfDisStrData = new DataMap();
                IData wfDisTempData = new DataMap();
                IData temp = new DataMap();
                for(int j = 0, sizeJ = wfDisList.size(); j < sizeJ; j++)
                {
                    IData wfDis = wfDisList.getData(j);
                    wfDisTempData.put("SERIAL_NUMBER", wfDis.getString("SERIAL_NUMBER",""));
                    if("-1".equals(wfDis.getString("PARENT_ATTR_CODE")))
                    {
                        if(wfDisTempData.containsKey(wfDis.getString("ATTR_CODE")))
                        {
                            temp = wfDisTempData.getData(wfDis.getString("ATTR_CODE"));
                        }
                        else
                        {
                            temp = new DataMap();
                        }
                        if("OFFER_CODE".equals(wfDis.getString("ATTR_NAME")))
                        {
                            temp.put("OFFER_CODE", wfDis.getString("ATTR_VALUE"));
                        }
                        if("OFFER_NAME".equals(wfDis.getString("ATTR_NAME")))
                        {
                            temp.put("OFFER_NAME", wfDis.getString("ATTR_VALUE"));
                        }
                        if("OPER_CODE".equals(wfDis.getString("ATTR_NAME")))
                        {
                            temp.put("OPER_CODE", wfDis.getString("ATTR_VALUE"));
                        }
                        wfDisTempData.put(wfDis.getString("ATTR_CODE"), temp);
                    }
                    else
                    {
                        if(wfDisTempData.containsKey(wfDis.getString("PARENT_ATTR_CODE")))
                        {
                            temp = wfDisTempData.getData(wfDis.getString("PARENT_ATTR_CODE"));
                        }
                        else
                        {
                            temp = new DataMap();
                        }
                        IDataset tempOfferChaList = temp.getDataset("OFFER_CHA", new DatasetList());
                        tempOfferChaList.add(wfDis);
                        temp.put("OFFER_CHA", tempOfferChaList);
                        wfDisTempData.put(wfDis.getString("PARENT_ATTR_CODE"), temp);
                    }
                }
                if(IDataUtil.isNotEmpty(wfDisTempData))
                {
                    StringBuilder wfDisStrSb = new StringBuilder(500);
                    Iterator<String> tmpItr = wfDisTempData.keySet().iterator();
                    while(tmpItr.hasNext())
                    {
                        String key = tmpItr.next();
                        IData tmpData = wfDisTempData.getData(key);
                        if(IDataUtil.isEmpty(tmpData))
                        {
                            continue;
                        }
                        if ("0".equals(tmpData.getString("OPER_CODE")) || "2".equals(tmpData.getString("OPER_CODE"))) {
                        	wfDisStrSb.append("新资费：");
						}else {
							wfDisStrSb.append("老资费：");
						}
                        wfDisStrSb.append(tmpData.getString("OFFER_CODE")).append("[").append(tmpData.getString("OFFER_NAME")).append("]");
                        IDataset tempOfferChaList = tmpData.getDataset("OFFER_CHA");
                        if(IDataUtil.isNotEmpty(tempOfferChaList))
                        {
                            wfDisStrSb.append("(");
                            for(int j = 0, sizeJ = tempOfferChaList.size(); j < sizeJ; j++)
                            {
                                IData tempOfferCha = tempOfferChaList.getData(j);
                                String attrName = tempOfferCha.getString("RSRV_STR1");
                                if(StringUtils.isBlank(attrName))
                                {
                                    attrName = tempOfferCha.getString("ATTR_NAME"); //兼容老数据（老数据数据名称没导）
                                }
                                wfDisStrSb.append(attrName).append("：").append(tempOfferCha.getString("ATTR_VALUE")).append("，");
                            }
                            wfDisStrSb.append(")；");
                        }
                    }
                    if(StringUtils.isNotBlank(wfDisStrSb))
                    {
                        wfDisStrData.put("PRICE_OFFER_STR", wfDisStrSb.toString());
                        wfDisStrData.put("SERIAL_NUMBER", wfDisTempData.getString("SERIAL_NUMBER"));
                        wfDisStrList.add(wfDisStrData);
                    }
                }
            }
        }
        return wfDisStrList;
    }
    
    public static IDataset qryDisBySubIbsysidAndRecordNum(String subIbsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_DIS", "SEL_BY_SUBIBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryDisBySubIbsysidAndRecordNumOrderByCode(String subIbsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_DIS", "SEL_BY_SUBIBSYSID_RECORDNUM_ORDER", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryDisGroupSeqBySubIbsysidAndRecordNum(String subIbsysid, String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_IBSYSID", subIbsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCode("TF_B_EOP_DIS", "SEL_GROUPSEQ_BY_SUBIBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
