
package com.asiainfo.veris.crm.order.soa.group.gfffmanage;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class GfffUserMaxGprsSetBean extends CSBizBean
{
    private Logger logger = Logger.getLogger(GfffUserMaxGprsSetBean.class);
    
    /**
     * 根据cust_id查询标准集团下的产品
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryProductByCustId(IData data) throws Exception
    {
        if(logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 BreakGrpPayMarkBean()  >>>>>>>>>>>>>>>>>>");
        }
        IDataset infos = GfffUserMaxGprsSetQry.queryProductByCustId(data);
        return infos;
    }
    
    /**
     * 不截止代付关系的分页查询
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGfffUserMaxGprsSetInfo(IData param, Pagination pagination) 
    	throws Exception
    {
    	//查询是否集团流量自由充限量统付用户
        IDataset infos = GfffUserMaxGprsSetQry.qryGfffUserInfo(param, pagination);
        if(IDataUtil.isEmpty(infos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团产品用户["+param.getString("SERIAL_NUMBER", "")+"]不是集团流量自由充限量统付用户");
        }
        //查询统付总流量上限值
        for(int i=0,size=infos.size();i<size;i++){
        	IData each = infos.getData(i);
        	IDataset setInfos = GfffUserMaxGprsSetQry.qryGfffUserMaxGprsSetInfo(each.getString("USER_ID"));
        	String grpsMax = "0";	//默认情况下，集团用户的MAX值为0
        	String startDate = "";
        	String endDate = "";
        	String otInstId = "";
        	if(IDataUtil.isNotEmpty(setInfos)){
        		grpsMax = setInfos.getData(0).getString("RSRV_VALUE", "");
        		startDate = setInfos.getData(0).getString("START_DATE", "");
        		endDate = setInfos.getData(0).getString("END_DATE", "");
        		otInstId = setInfos.getData(0).getString("INST_ID", "");
        	}
        	each.put("GPRS_MAX", grpsMax);
        	each.put("START_DATE", startDate);
        	each.put("END_DATE", endDate);
        	each.put("INST_ID", otInstId);
        }
        
        return infos;
    }
    /**
     * 处理新增绑定关系
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-19
     */
    public void dealAddBindInfo(IData param) throws Exception
    {
    	this.checkForDealBind(param);
    	this.addBindInfo(param);
    }
    /**
     * 处理修改绑定关系
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-19
     */
    public void dealModBindInfo(IData param) throws Exception
    {
    	this.checkForDealBind(param);
    	this.modBindInfo(param);
    }

    /**
     * 新增绑定信息
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void addBindInfo(IData param) throws Exception
    {
    	IData bindInfo = new DataMap();
    	bindInfo.put("INST_ID", SeqMgr.getInstId());
    	bindInfo.put("TRADE_STAFF_ID", param.getString("TRADE_STAFF_ID"));
    	bindInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
    	bindInfo.put("INSERT_TIME", SysDateMgr.getSysTime());
    	bindInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	bindInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	Dao.insert("TF_F_USER_GFFF_BINDGRPSN", bindInfo);
    }
    /**
     * 修改绑定关系
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void modBindInfo(IData param) throws Exception
    {
    	IData bindInfo = new DataMap();
    	bindInfo.put("INST_ID", param.getString("INST_ID"));
    	bindInfo.put("TRADE_STAFF_ID", param.getString("TRADE_STAFF_ID"));
    	bindInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
    	bindInfo.put("INSERT_TIME", SysDateMgr.getSysTime());
    	bindInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
    	bindInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    	Dao.save("TF_F_USER_GFFF_BINDGRPSN", bindInfo);
    }
    /**
     * 删除绑定关系
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-12
     */
    public void delBindInfo(IData param) throws Exception
    {
    	IData bindInfo = new DataMap();
    	bindInfo.put("INST_ID", param.getString("INST_ID"));
    	bindInfo.put("TRADE_STAFF_ID", param.getString("TRADE_STAFF_ID"));
    	bindInfo.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
    	Dao.delete("TF_F_USER_GFFF_BINDGRPSN", bindInfo);
    }
    /**
     * 绑定关系的增删改校验
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-19
     */
    private void checkForDealBind(IData param) throws Exception {
		String staffId = param.getString("TRADE_STAFF_ID");	//工号
    	String grpSn = param.getString("SERIAL_NUMBER");	//集团产品编码
    	//1.校验集团产品是否自由充产品
    	IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(grpSn);
    	if(IDataUtil.isEmpty(userInfo)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据集团产品编码["+grpSn+"]查询无资料！");
    	}
    	String productId = userInfo.getString("PRODUCT_ID", "");
    	if(!"7342".equals(productId)&&!"7343".equals(productId)&&!"7344".equals(productId)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "输入的集团产品编码["+grpSn+"]不是流量自由充产品！");
    	}
    	//2.校验一个产品编码只能被一个工号绑定
    	IData qryParam = new DataMap();
    	qryParam.put("SERIAL_NUMBER", grpSn);
    	IDataset binds = GfffUserMaxGprsSetQry.qryGfffStaffIdBindInfos(qryParam, null);
    	if(IDataUtil.isNotEmpty(binds)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "输入的集团产品编码["+grpSn+"]已被其他工号绑定！");
    	}
	}
}
