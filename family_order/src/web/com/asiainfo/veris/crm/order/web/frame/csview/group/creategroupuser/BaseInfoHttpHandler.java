
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupuser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class BaseInfoHttpHandler extends CSBizHttpHandler
{
    public void queryProductInfo() throws Exception
    {
    	//REQ202001100001关于限制长期欠费的集团客户开通新业务的需求
    	IData data = checkFee(getData().getString("CUST_ID", ""));
    	if (!DataUtils.isEmpty(data)) {
    		String state = data.getString("STATE","true");
    		if ("false".equals(state)) {
    			CSViewException.apperr(CrmCommException.CRM_COMM_103, data.getString("MSG",""));
			}
        }
    	
        IData productDescInfoData = new DataMap();
        IDataset grpUserList = new DatasetList();
        String productId = getData().getString("PRODUCT_ID");
        String custid = getData().getString("CUST_ID", "");
        productDescInfoData = GroupProductUtilView.getProductExplainInfo(this, productId);
        IData productCtrlInfoData = AttrBizInfoIntfViewUtil.qryCrtUsProductCtrlInfoByProductId(this, productId);
        grpUserList = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, custid, productId, false);

        IData result = new DataMap();
        if (IDataUtil.isNotEmpty(productDescInfoData))
            result.put("PRODUCT_DESC_INFO", productDescInfoData);
        if (IDataUtil.isNotEmpty(productCtrlInfoData))
            result.put("PRODUCT_CTRL_INFO", productCtrlInfoData);
        if (IDataUtil.isNotEmpty(grpUserList))
            result.put("GRP_USER_LIST", grpUserList);

        setAjax(result);

    }
    
    
    /**
     * 检验EC是否已经同步到平台
     * @throws Exception
     * @author chenhh6
     */
	public void checkEcSyn() throws Exception{
		String success = "false";
		String msg = "";
		IData map = new DataMap();
		String custid = getData().getString("CUST_ID");
		String productId = getData().getString("PRODUCT_ID");
		if ("20005013".equals(productId) || "20005015".equals(productId)
				||"20161122".equals(productId) || "20161124".equals(productId)
				|| "20171214".equals(productId)) {
			String rsrvtag5 = UCAInfoIntfViewUtil.queryUserInfoByCustId(this, custid);
			if (rsrvtag5 == null || "".equals(rsrvtag5)) {
				msg = "未同步";
			}else if ("0".equals(rsrvtag5)) {
				msg = "未同步";
			}else if ("1".equals(rsrvtag5)) {
				success = "true";
			}else if ("2".equals(rsrvtag5)) {
				msg = "正在同步";
			}else if ("3".equals(rsrvtag5)) {
				msg = "同步失败";
			}else {
				msg = "未同步";
			}
		}else{
			success = "true";
		}
		map.put("success", success);
    	map.put("msg", msg);
    	setAjax(map);
    }
	
	private IData checkFee(String custId) throws Exception {
    	String status = "true";
    	String msg = "";
    	IData results = new DataMap();
    	
    	IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "1066");
        iparam.put("PARAM_CODE", "VERIFY_FEE");
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset staticInfo = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        
		String isVerify = "";
		if (IDataUtil.isNotEmpty(staticInfo)){
			isVerify = staticInfo.getData(0).getString("PARA_CODE1", "").trim();// 是否进行验证,返回1为验证,0不验证
		}
		if (!"1".equals(isVerify)){
			results.put("STATE", status);
			results.put("MSG", msg);
			return results;
		}
		
		//免限制长期欠费集团订购产品权限
		if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_CHECKFEE")) {
			results.put("STATE", status);
			results.put("MSG", msg);
			return results;
		}
    	
    	
    	IData map = new DataMap();
    	map.put("CUST_ID", custId);
        IDataset idata = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctUserInfoByCustIDForGrpNoPage", map);
    	if (IDataUtil.isNotEmpty(idata)) {
    		for (int i = 0; i < idata.size(); i++) {
    			IData data = idata.getData(i);
    			String acctId = data.getString("ACCT_ID");
    			String payModeCode = data.getString("PAY_MODE_CODE");
    			//调用账务接口
    			IData inparam = new DataMap();
    	        inparam.put("ACCT_ID", acctId);
    	        IDataset acctData = CSViewCall.call(this, "CS.AcctInfoQrySVC.checkFee", inparam);
    	        //需要对账务数据进行处理
    	        System.out.println("chenhh==acctData:"+acctData);
    			if (IDataUtil.isNotEmpty(acctData)) {
    				String minOweMouth = acctData.getData(0).getString("MIN_OWE_MONTH");	//账务返回最小账单月：201904
    				String Owefee = acctData.getData(0).getString("OWE_FEE");				//欠费金额
    				if (StringUtils.isNotEmpty(minOweMouth)) {
    					if (!checkMonth(minOweMouth) && StringUtils.isNotEmpty(Owefee)) {
    						double OwefeeD = Double.parseDouble(Owefee) * 0.01;
    						DecimalFormat df = new DecimalFormat("0.00"); 
    						String OwefeeS = df.format(OwefeeD);
    						status = "false";
    						msg = "该集团账户已欠费超过3个月，集团账户共计欠费" + OwefeeS + "元，请缴费后再办理业务";
						}
					}
				}else {
					status = "false";
					msg = "调用账务接口失败。";
				}
    		}
		}
        
    	
    	results.put("STATE", status);
		results.put("MSG", msg);
    	return results;
	}
    
    private boolean checkMonth(String minOweMouth){
    	boolean state = true;
    	Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		String nowMonth = sdf.format(date);
		int minOweMouthY = Integer.parseInt(minOweMouth.substring(0, 4));
		int minOweMouthM = Integer.parseInt(minOweMouth.substring(4, 6));
		int nowMonthY = Integer.parseInt(nowMonth.substring(0, 4));
		int nowMonthM = Integer.parseInt(nowMonth.substring(4, 6));
		int sad = (nowMonthY - minOweMouthY)*12 + (nowMonthM - minOweMouthM);//月份差 = （第二年份-第一年份）*12 + 第二月份 -第一月份 ；
		System.out.println("总共欠费月份："+sad);
		if (sad > 3) {
			state = false;
		}
    	return state;
    }
}
