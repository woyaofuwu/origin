package com.asiainfo.veris.crm.order.soa.person.busi.onecertificatefiveno;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.onecertificatefiveno.OnecertificatefiveNoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class OnecertificatefiveNoRecordBean extends CSBizBean {
	/**
	 * 根据条件查询天猫对账差异记录
	 * @param Datamap
	 * @param page
	 * @return
	 * @throws Exception
	 * @author huangzl3
	 */
	public IDataset queryData(IData Datamap,Pagination page) throws Exception {
		IDataset list=new DatasetList();
		String PHONENUMBER = Datamap.getString("PHONENUMBER");
		String SRECONDATE = Datamap.getString("SRECONDATE");
		String ERECONDATE = Datamap.getString("ERECONDATE");
		String COMPRESULT = Datamap.getString("COMPRESULT");
		String SYNRESULT = Datamap.getString("SYNRESULT");
        list= OnecertificatefiveNoQry.queryData(PHONENUMBER,SRECONDATE,ERECONDATE,COMPRESULT,SYNRESULT,page);
		return list;
	}	
	
	/**
	 * 回写修改处理字段
	 * @param Datamap
	 * @param page
	 * @return
	 * @throws Exception
	 * @author huangzl3
	 */
	public int updateData(IData Datamap) throws Exception {
		String PHONENUMBER=Datamap.getString("SERIAL_NUMBER");
		String COMPRESULT=Datamap.getString("COMP_RESULT");
		String SYNRESULT=Datamap.getString("SYN_RESULT");
        return OnecertificatefiveNoQry.updateData(SYNRESULT,PHONENUMBER,COMPRESULT);
	}	
    
    
    /**
     * @Function: addSeq
     * @Description: 补充操作流水号
     * @param: @param data
     * @author: huangzl3
     */
    public IData addSeq(IData data) throws Exception
    {
        //补充操作流水号
        if (StringUtils.isEmpty(data.getString("SEQ", ""))) {
            String date = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
            String seqRealId = SeqMgr.getRealId();
            data.put("SEQ", "898" + date + seqRealId);
        }
        return data;
    }
    
    /**
     * @Function: checkPspt
     * @Description: 发平台证件类型，没有配置则不调用平台
     * @param: @param data
     * @author: huangzl3
     */
    public IDataset checkPspt(String psptTypeCode) throws Exception
    {
        return CommparaInfoQry.getCommparaAllCol("CSM", "2553", psptTypeCode, "ZZZZ");
    }
    
    /**
     * 
     * 写入IBOSS扫描操作的预占表  TF_F_OPENLIMIT_CAMPON_IBOSS
     * 
     */
    public void InsertCampOnIBOSS(IData data) throws Exception{
    	IDataUtil.chkParam(data, "SEQ");
        IDataUtil.chkParam(data, "CUSTOMER_NAME");
        IDataUtil.chkParam(data, "ID_CARD_TYPE");
        IDataUtil.chkParam(data, "ID_CARD_NUM");
        IDataUtil.chkParam(data, "ID_VALUE");
        data.put("INSERT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        data.put("CAMP_ON", "01");
        data.put("RSRV_STR1", "NoDecrypt");
        data.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());   
        Dao.insert("TF_F_OPENLIMIT_CAMPON_IBOSS", data,Route.CONN_CRM_CEN);
    }
    

}
