
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class CheckPLKFInfoSyn extends BreBase implements IBREScript
{
	private static Logger logger = Logger.getLogger(CheckPLKFInfoSyn.class);
	
	private static final long serialVersionUID = 1L;
	
    public boolean run(IData databus, BreRuleParam rule) throws Exception{
    	
    	if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckPLKFInfoSyn() >>>>>>>>>>>>>>>>>>");
    	
        String custId = databus.getString("CUST_ID");
        //String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
        //if (StringUtils.isNotBlank(userElementsStr))
        //    return false;
        IData custInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
        if (IDataUtil.isEmpty(custInfo))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, GrpException.CRM_GRP_190.toString(), GrpException.CRM_GRP_190.getValue());
            return false;
        }

        String groupId = custInfo.getString("GROUP_ID");
        String custName = custInfo.getString("CUST_NAME", "");
        String custTel = custInfo.getString("RSRV_STR7", "");

        IData ibossParam = new DataMap();
        ibossParam.put("PKG_SEQ", ""); // 交易包流水号
        String sysTimeString = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        String seqId = sysTimeString + "001";
        ibossParam.put("OPR_SEQ", seqId); // 本次操作的流水号
        ibossParam.put("EC_ID", groupId); // EC客户编码
        ibossParam.put("EC_NAME", custName);// EC客户名称
        ibossParam.put("EC_TEL", custTel);// EC客服电话/联系人号码
        
        //新增
        ibossParam.put("PROV_ID", custInfo.getString("PROVINCE_CODE",""));//省份代码
        
        ibossParam.put("OPR_CODE", "01"); // 本业务信息的操作编码: 01－增加,02－终止,03－暂停,04－恢复,05－变更
        ibossParam.put("KIND_ID", "BIPLKF01_TX100001_0_0");
        IDataset callResult = IBossCall.dealInvokeUrl("BIPLKF01_TX100001_0_0", "IBOSS7", ibossParam);

        if (IDataUtil.isEmpty(callResult))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, IBossException.CRM_IBOSS_6.toString(), IBossException.CRM_IBOSS_6.getValue());
            return true;
        }
	  logger.debug(" >>>>>>>>>>>20190912 test:"+callResult.toString());
      String errCode = callResult.getData(0).getString("X_RSPCODE", "");
  	  logger.debug(" >>>>>>>>>>>20190912 errCode:"+callResult.getData(0).getString("X_RSPCODE", ""));
      /*  // String errCode2= callResult.getData(0).getString("X_RESULTCODE", "");
        if (!"0000".equals(errCode))
        {
            String errInfo = "全网短彩信产品手机报平台校验失败：" + callResult.getData(0).getString("X_RSPDESC", "");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, errInfo);
            return true;
        }*/
        return false;
    }

}
