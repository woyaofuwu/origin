package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: WideNetBookInfoQuery.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-31 上午09:44:34 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
 */

public class WideNetBookQuery
{
	/**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-31 上午09:52:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getWideNetBookList(String contactSn, String custName, String auditStatus, String startDate, String endDate, Pagination pagin) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTACT_SN", contactSn);
        param.put("CUST_NAME", custName);
        param.put("AUDIT_STATUS", auditStatus);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_BY_PK", param, pagin);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-31 上午09:52:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getWideNetPreRegBookList(String contactSn, String custName, String serial_number, String regStatus, String set_addr, String startDate, String endDate, String cityCode, Pagination pagin) throws Exception
    {
        IData param = new DataMap();
        param.put("CONTACT_SN", contactSn); //联系人电话
        param.put("CUST_NAME", custName); //客户姓名
        param.put("SERIAL_NUMBER", serial_number);//服务号码
        param.put("REG_STATUS", regStatus);//登记状态
        param.put("SET_ADDR", set_addr); //安装地址
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        //根据REQ201706050023关于优化网厅预约业务处理界面无法通过手机号码查询工单问题的需求。这里放开地域查询，所有工号都可以查询全省信息。
//        if(cityCode.equals("HNSJ") || cityCode.equals("HNKF")){//省局、客服工号查询全部数据
        	return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_BY_PK1", param, pagin);
//        }else{//其余工号查询所属CITY_CODE的数据
//        	IDataset commparaInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM","702","ADDR_CODE",cityCode);
//        	if(DataSetUtils.isNotBlank(commparaInfos)){
//        		param.put("AREA_CODE", commparaInfos.getData(0).getString("PARA_CODE2"));
//        	}else{
//        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据CITY_CODE找不到对应的地址编码"); 
//        	}
//        	return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_BY_PK2", param, pagin);
//        }
    }
    
    /**
     * @Function:
     * @Description: 获取宽带需求收集汇总数据
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-31 上午09:52:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getWideNetBookCollectList(String regStatus, String set_addr, String startDate, String endDate, Pagination pagin) throws Exception
    {
        IData param = new DataMap();
        param.put("REG_STATUS", regStatus);//登记状态
        param.put("SET_ADDR", "%" + set_addr + "%"); //安装地址
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_COLLECT_BY_ADDR", param, pagin);
    }
    
    /**
     * @Function:
     * @Description: 获取宽带需求收集汇总数据
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-31 上午09:52:00 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-31 chengxf2 v1.0.0 修改原因
     */
    public static IDataset getWidePreRegCollectList() throws Exception
    {
        IData param = new DataMap();
        param.put("REG_STATUS", "1");//登记状态 1:未开通
        return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_ALL_COLLECT_BY_ADDR", param);
    }
    
    /**
     * 获取宽带需求收集已开通信息
     * @return
     * @throws Exception
     */
    public static IDataset getWidePreRegDredgeList(String res_status)throws Exception
    {
    	IData param = new DataMap();
        param.put("REG_STATUS", res_status);//登记状态 
        return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_ALL_DREDGE", param);
    }
}
