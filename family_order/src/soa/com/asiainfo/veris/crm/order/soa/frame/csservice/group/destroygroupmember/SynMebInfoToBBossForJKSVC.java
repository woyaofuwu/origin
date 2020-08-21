
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class SynMebInfoToBBossForJKSVC extends GroupOrderService
{
    protected static final Logger log = Logger.getLogger(SynMebInfoToBBossForJKSVC.class);
    
    public IDataset synMeb(IData inparam) throws Exception
    {
    	log.debug("synMeb--inparam=="+inparam);
    	IData param=new DataMap();
    	String grpUserId = inparam.getString("USER_ID");
    	String serialNumber = inparam.getString("SERIAL_NUMBER");
		IData mebuserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	      if (IDataUtil.isEmpty(mebuserInfo)){
	         CSAppException.apperr(GrpException.CRM_GRP_713, "当前成员[" + serialNumber + "]用户信息不存在!");
	      }
	      String memUserId = mebuserInfo.getString("USER_ID");
	    IData data=new DataMap();
	    data.put("USER_ID", memUserId);
	    data.put("EC_USER_ID", grpUserId);
	    IDataset cepMebInfos=getCepMebInfos(data);
	    String memType="";
	    String productOrderNum="";
	    String productOfferId="";
	    if(IDataUtil.isNotEmpty(cepMebInfos)){
	    	memType=cepMebInfos.getData(0).getString("RSRV_TAG1","");
	    	productOrderNum=cepMebInfos.getData(0).getString("PRODUCT_ORDER_ID","");//订单编码
	    	productOfferId=cepMebInfos.getData(0).getString("PRODUCT_OFFER_ID","");//订购关系ID
	    }
		param.put("PKG_SEQ",SeqMgr.getPkgSeqId());                                                                                     
		param.put("PRODUCT_ORDER_NUMBER",productOrderNum);
		param.put("PRODUCT_ID", productOfferId);
		param.put("ORDER_SOURCE", "0");
		param.put("ORDER_TYPE", "");
		IDataset productMebList=new DatasetList();
		IData mebInfo=new DataMap();
		mebInfo.put("MEMBER_NUMBER",serialNumber);
		mebInfo.put("ACTION", "0");
		mebInfo.put("MEMBER_TYPE_ID", memType);
		mebInfo.put("EFF_DATE",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		productMebList.add(mebInfo);
		param.put("PRODUCT_ORDER_MEMBERS",productMebList);//成员列表
		param.put("KIND_ID", "MemberService_BBOSS_0_0");
		IDataset rets= IBossCall.dealInvokeUrl("MemberService_BBOSS_0_0","IBOSS6", param); 
//		IData data = (IDataUtil.isEmpty(rets)) ? new DataMap() : rets.getData(0);
		rets.getData(0).put("ORDER_ID", SeqMgr.getOrderId());
		log.debug("rets="+rets);
		return rets;
    }
    public IDataset getCepMebInfos(IData inparam) throws Exception
    {
    	return Dao.qryByCodeParser("TF_F_USER_ECRECEP_MEB", "SEL_CEP_MEB_INFO", inparam);
    }
    /**
     * 《中国移动BBOSS与省公司集团业务接口规范》接口4.6成员签约关系同步接口（订单来源0-省BOSS上传）
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset synForBBoss(IData inparam) throws Exception
    {
    	IData param=new DataMap();
    	String grpUserId = inparam.getString("USER_ID");
    	String serialNumber = inparam.getString("SERIAL_NUMBER");
		IData mebuserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	     if (IDataUtil.isEmpty(mebuserInfo)){
	        CSAppException.apperr(GrpException.CRM_GRP_713, "当前成员[" + serialNumber + "]用户信息不存在!");
	     }
	    String memUserId = mebuserInfo.getString("USER_ID");
	    String memEparchyCode=mebuserInfo.getString("EPARCHY_CODE");
	    IDataset MebInfos = UserGrpMerchMebInfoQry.getSEL_BY_USERID_USERIDA(memUserId, grpUserId, memEparchyCode);
	    String memType="";
	    String productOrderNum="";
	    String productOfferId="";
	    if(IDataUtil.isNotEmpty(MebInfos)){
	    	memType=MebInfos.getData(0).getString("RSRV_TAG1","");
	    	productOrderNum=MebInfos.getData(0).getString("PRODUCT_ORDER_ID","");//订单编码
	    	productOfferId=MebInfos.getData(0).getString("PRODUCT_OFFER_ID","");//订购关系ID
	    }
		param.put("PKG_SEQ",SeqMgr.getPkgSeqId());                                                                                     
		param.put("PRODUCT_ORDER_NUMBER",productOrderNum);
		param.put("PRODUCT_ID", productOfferId);
		param.put("ORDER_SOURCE", "0");
		param.put("ORDER_TYPE", "");
		IDataset productMebList=new DatasetList();
		IData mebInfo=new DataMap();
		mebInfo.put("MEMBER_NUMBER",serialNumber);
		mebInfo.put("ACTION", "0");
		mebInfo.put("MEMBER_TYPE_ID", memType);
		mebInfo.put("EFF_DATE",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		productMebList.add(mebInfo);
		param.put("PRODUCT_ORDER_MEMBERS",productMebList);//成员列表
		param.put("KIND_ID", "MemberService_BBOSS_0_0");
    	IDataset rets= IBossCall.dealInvokeUrl("MemberService_BBOSS_0_0","IBOSS2", param); //IBOSS提供上发接口之后修改
//		IData data = (IDataUtil.isEmpty(rets)) ? new DataMap() : rets.getData(0);
		log.debug("rets="+rets);
		return rets;
    }

}
