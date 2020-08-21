
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateIdcGroupUser extends CreateGroupUser
{
    /**
     * 构造函数
     */
    public CreateIdcGroupUser()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        regOtherInfoTrade();
        infoRegIdcOrderOther();
    }

    protected void actTradeUser() throws Exception
    {
        UserTradeData userTradeData = reqData.getUca().getUser();
        // 用户
        if (userTradeData != null)
        {
            // 存产品产品信息到user表
            String product_id = reqData.getUca().getProductId();
            IData productParams = reqData.cd.getProductParamMap(product_id);

            if (IDataUtil.isNotEmpty(productParams))
            {
                userTradeData.setRsrvStr7(productParams.getString("NOTIN_DETMANAGER_INFO"));
                userTradeData.setRsrvStr8(productParams.getString("NOTIN_DETMANAGER_PHONE"));
                userTradeData.setRsrvStr9(productParams.getString("NOTIN_DETADDRESS"));
                userTradeData.setRsrvStr10(productParams.getString("N_CABINET_SVC_EC_INCOME")+ "|" + productParams.getString("N_IDC_VAL_ADDED_SVC_INCOME")); // 机柜服务总电费（元）|IDC总增值服务费（元）
                
                userTradeData.setRsrvStr1(productParams.getString("N_DEPOSIT_SUM"));
                userTradeData.setRsrvStr2(productParams.getString("N_DEPOSIT_DISCOUNT"));
                userTradeData.setRsrvStr3(productParams.getString("N_ACCESS_BANDWIDTH"));
                userTradeData.setRsrvStr4(productParams.getString("N_ACCESS_DISCOUNT"));
                userTradeData.setRsrvStr5(productParams.getString("N_JF_CODE"));
                userTradeData.setRsrvStr6(productParams.getString("N_GROUP_CITY_CODE"));
                userTradeData.setRsrvNum1(productParams.getString("N_IDC_INCOME"));
                userTradeData.setRsrvNum2(productParams.getString("N_DEPOSIT_INCOME"));
                userTradeData.setRsrvNum3(productParams.getString("N_ACCESS_INCOME"));
                userTradeData.setRsrvNum4(productParams.getString("N_OTHER_INCOME"));
                userTradeData.setRsrvNum5(productParams.getString("N_DEVICE_SIZE"));
                //REQ201808130004关于IDC产品界面增加“是否为统谈合同”字段的需求
                userTradeData.setRsrvTag2(productParams.getString("N_IDC_AGREEMENT_CONTRACT"));
                
            }
        }
        super.actTradeUser();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    public void regOtherInfoTrade() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();
        IData data = null;
        IDataset attrInternet = new DatasetList(param.getString("NOTIN_AttrInternet", "[]"));

        if (null != attrInternet && attrInternet.size() > 0)
        {
            for (int i = 0; i < attrInternet.size(); i++)
            {
                data = new DataMap();
                IData internet = attrInternet.getData(i);

                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("RSRV_VALUE_CODE", "IDC");
                data.put("STATE", "ADD");
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());

                data.put("RSRV_VALUE", internet.getString("pam_NOTIN_OPER_TAG"));
                // 端口通信费
                data.put("RSRV_STR1", internet.getString("pam_NOTIN_PORT_PRICE"));
                // 空间租用费
                data.put("RSRV_STR2", internet.getString("pam_NOTIN_SPACE_PRICE"));
                // 服务器型号
                data.put("RSRV_STR3", internet.getString("pam_NOTIN_SERVER_TYPE"));
                // IP地址
                data.put("RSRV_STR4", internet.getString("pam_NOTIN_IP_ADDRESS"));
                // 免费IP端口
                data.put("RSRV_STR5", internet.getString("pam_NOTIN_IP_PORT"));
                // 免费IP端口
                data.put("RSRV_STR6", internet.getString("pam_NOTIN_IP_PORT_NAME"));
                // 机柜服务电费（元）
                data.put("RSRV_STR7", internet.getString("pam_CABINET_SVC_EC_INCOME"));
                // IDC增值服务费（元）
                data.put("RSRV_STR8", internet.getString("pam_IDC_VAL_ADDED_SVC_INCOME"));
                // 专线实例号
                data.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));
                // REQ201808210047 关于IDC产品界面增加“IP地址类型”字段的需求
                data.put("RSRV_STR11", internet.getString("pam_TYPE_OF_IP_ADDRESS"));

                dataset.add(data);
            }

            addTradeOther(dataset);
        }
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR7", paramData.getString("NOTIN_DETMANAGER_INFO", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETMANAGER_PHONE", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_DETADDRESS", ""));

    }
    
    /**
     * REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
     * 处理订购信息other表台帐
     * @throws Exception
     * @author chenzg
     * @date 2018-6-25
     */
    public void infoRegIdcOrderOther() throws Exception
    {
    	IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
    	String dataStr = param.getString("IDC_ORDER_DATA", "[]");
    	if(StringUtils.isBlank(dataStr)){
    		return;
    	}
        IDataset attrOrderDs = new DatasetList(dataStr);
        if(IDataUtil.isNotEmpty(attrOrderDs)){
        	IDataset attrOthers = new DatasetList();
        	for(int i=0;i<attrOrderDs.size();i++){
        		IData each = attrOrderDs.getData(i);
        		String tag = each.getString("tag");
        		if(!"0".equals(tag)){
        			continue;
        		}
        		IData other = new DataMap();
        		other.put("USER_ID", reqData.getUca().getUserId());
        		other.put("RSRV_VALUE_CODE", "IDCORDER");
        		other.put("RSRV_VALUE", this.reqData.getUca().getSerialNumber());
        		other.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        		other.put("START_DATE", getAcceptTime());
        		other.put("RSRV_STR1", each.getString("IDC_DEVICE_NAME"));	//设备名称
        		other.put("RSRV_STR2", each.getString("IDC_DEVICE_IP"));	//设备IP
        		other.put("RSRV_STR3", each.getString("IDC_DEVICE_PORT"));	//设备端口名称
        		other.put("END_DATE", SysDateMgr.getTheLastTime());		
        		//REQ201812180018关于本省IDC流量计费产品增加按天生效时间免费月份及其他优化的需求
        		other.put("RSRV_STR4", each.getString("pam_EFFECTIVE_WAY_TYPE")); //生效方式
        		other.put("RSRV_STR5", each.getString("pam_EFFECTIVE_DATE")); //生效日期
        		other.put("RSRV_STR6", each.getString("pam_FREE_MONTH")); //免费月份
        		other.put("RSRV_STR7", each.getString("pam_FREE_MONTH1")); //免费月份
        		other.put("RSRV_STR8", each.getString("pam_FREE_MONTH2")); //免费月份
        		attrOthers.add(other);
        	}
        	addTradeOther(attrOthers);
        	//过滤掉产品属性[IDC_ORDER_DATA]
            IDataset tradeAttrs = this.bizData.getTradeAttr();
        	if(IDataUtil.isNotEmpty(tradeAttrs)){
        		for(int i=0;i<tradeAttrs.size();i++){
        			IData each = tradeAttrs.getData(i);
        			if("IDC_ORDER_DATA".equals(each.getString("ATTR_CODE"))){
        				tradeAttrs.remove(each);
        				i--;
        			}
        		}
        	}
        }
    }

}
