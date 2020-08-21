
package com.asiainfo.veris.crm.order.web.group.param.idcline;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        String productNo = parainfo.getString("PRODUCT_ID");
        
        // 调用后台服务查专线名称
        IDataset dataLineInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "IP_PORT", "ZZZZ");
        
        parainfo.put("DATALINE_INFO", dataLineInfo);
        parainfo.put("NOTIN_METHOD_NAME", "ChgUs");

        String userId = data.getString("USER_ID");
        IData userInParam = new DataMap();
        userInParam.put("USER_ID", userId);
        userInParam.put("REMOVE_TAG", "0");
        IDataset userInfo = CSViewCall.call(bp, "CS.UserInfoQrySVC.getTradeUserInfoByUserIdAndTag", userInParam);
        if (null != userInfo && userInfo.size() > 0)
        {
            IData userData = (IData) userInfo.get(0);
            parainfo.put("NOTIN_DETMANAGER_INFO", userData.get("RSRV_STR7"));
            parainfo.put("NOTIN_DETMANAGER_PHONE", userData.get("RSRV_STR8"));
            parainfo.put("NOTIN_DETADDRESS", userData.get("RSRV_STR9"));
//            parainfo.put("NOTIN_PROJECT_NAME", userData.get("RSRV_STR10"));

            String rsrvStr10 = userData.getString("RSRV_STR10"); // TODO 机柜服务总电费（元）|IDC总增值服务费（元）
            
            if (StringUtils.isNotBlank(rsrvStr10)) {
	        	String[] resultMsg = rsrvStr10.split("\\|");
	        	if (resultMsg.length > 0) {
	        		parainfo.put("N_CABINET_SVC_EC_INCOME", resultMsg[0]); // 机柜服务总电费
	        	}

	        	if (resultMsg.length > 1) {
	        		parainfo.put("N_IDC_VAL_ADDED_SVC_INCOME", resultMsg[1]); // IDC总增值服务费
	        	}
            }
            
            parainfo.put("N_GROUP_CITY_CODE", userData.get("RSRV_STR6"));
            parainfo.put("N_JF_CODE", userData.get("RSRV_STR5"));
            parainfo.put("N_IDC_INCOME", userData.get("RSRV_NUM1"));
            parainfo.put("N_DEPOSIT_INCOME", userData.get("RSRV_NUM2"));
            parainfo.put("N_ACCESS_INCOME", userData.get("RSRV_NUM3"));
            parainfo.put("N_OTHER_INCOME", userData.get("RSRV_NUM4"));
            parainfo.put("N_DEPOSIT_SUM", userData.get("RSRV_STR1"));
            parainfo.put("N_DEVICE_SIZE", userData.get("RSRV_NUM5"));
            parainfo.put("N_DEPOSIT_DISCOUNT", userData.get("RSRV_STR2"));
            parainfo.put("N_ACCESS_BANDWIDTH", userData.get("RSRV_STR3"));
            parainfo.put("N_ACCESS_DISCOUNT", userData.get("RSRV_STR4"));
          //REQ201808130004关于IDC产品界面增加“是否为统谈合同”字段的需求
            parainfo.put("N_IDC_AGREEMENT_CONTRACT", userData.get("RSRV_TAG2"));

        }

        // 从ESOP获取专线实例编号
        IData inParam = new DataMap();
        IDataset seqDataSet = CSViewCall.call(bp, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
        IData seqData = (IData) seqDataSet.get(0);
        String maxNumberLine = seqData.getString("seq_id");
        long maxIong = Long.parseLong(maxNumberLine) * 1000;
        parainfo.put("NOTIN_MAX_NUMBER_LINE", String.valueOf(maxIong));

        // 调用后台服务
        IData inparme = new DataMap();
        inparme.put("USER_ID", userId);
        inparme.put("RSRV_VALUE_CODE", "IDC");

        IDataset userAttrInfo = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", inparme);

        // 判断OTHER表中有没有数据，没有从ESOP获取
        if (null != userAttrInfo && userAttrInfo.size() > 0)
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < userAttrInfo.size(); i++)
            {
                IData userAttrData = (IData) userAttrInfo.get(i);
                IData userData = new DataMap();

                userData.put("pam_NOTIN_OPER_TAG", userAttrData.get("RSRV_VALUE"));
                userData.put("pam_NOTIN_PORT_PRICE", userAttrData.get("RSRV_STR1"));
                userData.put("pam_NOTIN_SPACE_PRICE", userAttrData.get("RSRV_STR2"));
                userData.put("pam_NOTIN_SERVER_TYPE", userAttrData.get("RSRV_STR3"));
                userData.put("pam_NOTIN_IP_ADDRESS", userAttrData.get("RSRV_STR4"));
                userData.put("pam_NOTIN_IP_PORT", userAttrData.get("RSRV_STR5"));
                userData.put("pam_NOTIN_IP_PORT_NAME", userAttrData.get("RSRV_STR6"));
                userData.put("pam_CABINET_SVC_EC_INCOME", userAttrData.get("RSRV_STR7")); // 机柜服务电费（元）
                userData.put("pam_IDC_VAL_ADDED_SVC_INCOME", userAttrData.get("RSRV_STR8")); // IDC增值服务费（元）
                userData.put("pam_NOTIN_LINE_INSTANCENUMBER", userAttrData.get("RSRV_STR9"));
                userData.put("pam_TYPE_OF_IP_ADDRESS", userAttrData.get("RSRV_STR11")); // REQ201808210047 关于IDC产品界面增加“IP地址类型”字段的需求
                dataset.add(userData);
            }
            parainfo.put("VISP_INFO", dataset);

            // String eos = pd.getParameter("EOS");
            String esop = "";
            if (StringUtils.isNotBlank(esop))
            {
                // 从ESOP获取数据

            }
            else
            {
                parainfo.put("NOTIN_AttrInternet", dataset);
                parainfo.put("NOTIN_OLD_AttrInternet", dataset);
            }
        }
        else
        {

            // String eos = pd.getParameter("EOS");
            // IDataset resultDataset = new DatasetList();
            // IData idcpamData = new DataMap();
            // if(!"null".equals(eos) && !"".equals(eos)){
            // IDataset eosDataset = new DatasetList(eos);
            // if(null != eosDataset && eosDataset.size() > 0){
            // resultDataset = getEsopData(pd,eosDataset);
            // idcpamData.put("pam_idc", resultDataset);
            // setInfos(resultDataset);
            // }
            // }
            // String maxNumberLine = getMaxNumberLine(pd,resultDataset);
            // idcpamData.put("MAX_NUMBER_LINE", maxNumberLine.toString());
            // setIdcpam(idcpamData);

        }
        
        //---add by chenzg@20180625--begin--REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求----
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RSRV_VALUE_CODE", "IDCORDER");
        IDataset orderInfos = CSViewCall.call(bp, "CS.TradeOtherInfoQrySVC.queryUserOtherInfoByUserId", param);
        if(IDataUtil.isNotEmpty(orderInfos)){
        	IDataset ds = new DatasetList();
        	for(int i=0;i<orderInfos.size();i++){
        		IData each = orderInfos.getData(i);
        		IData userData = new DataMap();
                userData.put("IDC_DEVICE_NAME", each.getString("RSRV_STR1", ""));	//设备名称
                userData.put("IDC_DEVICE_IP", each.getString("RSRV_STR2", ""));	//设备IP
                userData.put("IDC_DEVICE_PORT", each.getString("RSRV_STR3", ""));	//设备端口名称
                userData.put("IDC_DEVICE_INSTID", each.getString("INST_ID", ""));
                ds.add(userData);
        	}
        	parainfo.put("IDCORDER_INFO", ds);
        }
        //---add by chenzg@20180625--end----REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求----
        return result;
    }

    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }
        String productNo = parainfo.getString("PRODUCT_ID");
        
        // 调用后台服务查专线名称
        IDataset dataLineInfo = AttrItemInfoIntfViewUtil.qryAttrItemBByIdAndIdtypeAttrCode(bp, productNo, "P", "IP_PORT", "ZZZZ");

        // 权限控制优惠价格未实现

        // 从ESOP获取专线实例编号
        IData inParam = new DataMap();
        IDataset seqDataSet = CSViewCall.call(bp, "CS.SeqMgrSVC.getMaxNumberLine", inParam);
        IData seqData = (IData) seqDataSet.get(0);
        String maxNumberLine = seqData.getString("seq_id");
        long maxIong = Long.parseLong(maxNumberLine) * 1000;
        parainfo.put("NOTIN_MAX_NUMBER_LINE", String.valueOf(maxIong));
        parainfo.put("DATALINE_INFO", dataLineInfo);

        return result;
    }
}
