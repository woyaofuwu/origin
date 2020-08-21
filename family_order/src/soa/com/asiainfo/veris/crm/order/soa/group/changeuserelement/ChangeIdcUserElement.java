
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeIdcUserElement extends ChangeUserElement
{

    public ChangeIdcUserElement()
    {

    }

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegVispDataOther();
        infoRegIdcOrderOther();
    }

    public IData getTradeUserExtendData() throws Exception
    {

        IData userExtenData = super.getTradeUserExtendData();

        // 存产品产品信息到user表
        String product_id = reqData.getUca().getProductId();
        IData productParams = reqData.cd.getProductParamMap(product_id);
        if (IDataUtil.isNotEmpty(productParams))
        {
            userExtenData.put("RSRV_STR7", productParams.getString("NOTIN_DETMANAGER_INFO"));
            userExtenData.put("RSRV_STR8", productParams.getString("NOTIN_DETMANAGER_PHONE"));
            userExtenData.put("RSRV_STR9", productParams.getString("NOTIN_DETADDRESS"));
            userExtenData.put("RSRV_STR10",productParams.getString("N_CABINET_SVC_EC_INCOME") + "|" + productParams.getString("N_IDC_VAL_ADDED_SVC_INCOME")); // TODO 机柜服务总电费（元）|IDC总增值服务费（元）
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            
            userExtenData.put("RSRV_STR1",productParams.getString("N_DEPOSIT_SUM"));
            userExtenData.put("RSRV_STR2",productParams.getString("N_DEPOSIT_DISCOUNT"));
            userExtenData.put("RSRV_STR3",productParams.getString("N_ACCESS_BANDWIDTH"));
            userExtenData.put("RSRV_STR4",productParams.getString("N_ACCESS_DISCOUNT"));
            userExtenData.put("RSRV_STR5",productParams.getString("N_JF_CODE"));
            userExtenData.put("RSRV_STR6",productParams.getString("N_GROUP_CITY_CODE"));
            userExtenData.put("RSRV_NUM1",productParams.getString("N_IDC_INCOME"));
            userExtenData.put("RSRV_NUM2",productParams.getString("N_DEPOSIT_INCOME"));
            userExtenData.put("RSRV_NUM3",productParams.getString("N_ACCESS_INCOME"));
            userExtenData.put("RSRV_NUM4",productParams.getString("N_OTHER_INCOME"));
            userExtenData.put("RSRV_NUM5",productParams.getString("N_DEVICE_SIZE"));
            //REQ201808130004关于IDC产品界面增加“是否为统谈合同”字段的需求
            userExtenData.put("RSRV_TAG2",productParams.getString("N_IDC_AGREEMENT_CONTRACT"));
        }
        return userExtenData;

    }

    public void infoRegVispDataOther() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();

        IDataset attrInternet = new DatasetList(param.getString("NOTIN_AttrInternet", "[]"));

        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("RSRV_VALUE_CODE", "IDC");
        IDataset userOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);

        for (int j = 0; j < attrInternet.size(); j++)
        {
            IData internet = attrInternet.getData(j);
            String numberCode = internet.getString("pam_NOTIN_OPER_TAG");
            String flag = internet.getString("tag", "");

            for (int i = 0; i < userOther.size(); i++)
            {
                IData vispUser = userOther.getData(i);
                String lineNumberCode = vispUser.getString("RSRV_VALUE");
                if (numberCode.equals(lineNumberCode))
                {
                    if ("1".equals(flag))
                    {
                        vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        vispUser.put("UPDATE_TIME", getAcceptTime());
                        vispUser.put("END_DATE", getAcceptTime());

                        dataset.add(vispUser);
                    }
                    else if ("2".equals(flag))
                    {
                        vispUser.put("RSRV_VALUE", String.valueOf(internet.getString("pam_NOTIN_OPER_TAG")));
                        // 端口通信费
                        vispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_PORT_PRICE"));
                        // 空间租用费
                        vispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_SPACE_PRICE"));
                        // 服务器型号
                        vispUser.put("RSRV_STR3", internet.getString("pam_NOTIN_SERVER_TYPE"));
                        // IP地址
                        vispUser.put("RSRV_STR4", internet.getString("pam_NOTIN_IP_ADDRESS"));
                        // 免费IP端口
                        vispUser.put("RSRV_STR5", internet.getString("pam_NOTIN_IP_PORT"));
                        // 免费IP端口
                        vispUser.put("RSRV_STR6", internet.getString("pam_NOTIN_IP_PORT_NAME"));
                        // 机柜服务电费（元）
                        vispUser.put("RSRV_STR7", internet.getString("pam_CABINET_SVC_EC_INCOME"));
                        // IDC增值服务费（元）
                        vispUser.put("RSRV_STR8", internet.getString("pam_IDC_VAL_ADDED_SVC_INCOME"));
                        // 专线实例号
                        vispUser.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));
                        // REQ201808210047 关于IDC产品界面增加“IP地址类型”字段的需求
                        vispUser.put("RSRV_STR11", internet.getString("pam_TYPE_OF_IP_ADDRESS"));

                        vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        dataset.add(vispUser);

                    }
                }
            }

            if ("0".equals(flag))
            {
                IData vispUsers = new DataMap();

                vispUsers.put("USER_ID", reqData.getUca().getUserId());
                vispUsers.put("RSRV_VALUE_CODE", "IDC");
                vispUsers.put("UPDATE_TIME", getAcceptTime());

                vispUsers.put("RSRV_VALUE", String.valueOf(internet.getString("pam_NOTIN_OPER_TAG")));
                // 端口通信费
                vispUsers.put("RSRV_STR1", internet.getString("pam_NOTIN_PORT_PRICE"));
                // 空间租用费
                vispUsers.put("RSRV_STR2", internet.getString("pam_NOTIN_SPACE_PRICE"));
                // 服务器型号
                vispUsers.put("RSRV_STR3", internet.getString("pam_NOTIN_SERVER_TYPE"));
                // IP地址
                vispUsers.put("RSRV_STR4", internet.getString("pam_NOTIN_IP_ADDRESS"));
                // 免费IP端口
                vispUsers.put("RSRV_STR5", internet.getString("pam_NOTIN_IP_PORT"));
                // 免费IP端口
                vispUsers.put("RSRV_STR6", internet.getString("pam_NOTIN_IP_PORT_NAME"));
                // 机柜服务电费（元）
                vispUsers.put("RSRV_STR7", internet.getString("pam_CABINET_SVC_EC_INCOME"));
                // IDC增值服务费（元）
                vispUsers.put("RSRV_STR8", internet.getString("pam_IDC_VAL_ADDED_SVC_INCOME"));
                // 专线实例号
                vispUsers.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));
                // REQ201808210047 关于IDC产品界面增加“IP地址类型”字段的需求
                vispUsers.put("RSRV_STR11", internet.getString("pam_TYPE_OF_IP_ADDRESS"));

                vispUsers.put("START_DATE", getAcceptTime());
                vispUsers.put("END_DATE", SysDateMgr.getTheLastTime());

                vispUsers.put("PROCESS_TAG", ""); // 处理标志map.getString("TRADE_TAG", "")
                vispUsers.put("INST_ID", SeqMgr.getInstId());
                vispUsers.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                dataset.add(vispUsers);
            }

        }
        addTradeOther(dataset);
    }

    @Override
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
        IDataset dataset = new DatasetList();
        String dataStr = param.getString("IDC_ORDER_DATA", "[]");
    	if(StringUtils.isBlank(dataStr)){
    		return;
    	}
        IDataset orderOthers = new DatasetList(dataStr);
        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("RSRV_VALUE_CODE", "IDCORDER");
        IDataset userOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);

        for (int j = 0; j < orderOthers.size(); j++)
        {
            IData order = orderOthers.getData(j);
            String idcInstId = order.getString("IDC_DEVICE_INSTID");
            String flag = order.getString("tag", "");
            //新增
            if ("0".equals(flag))
            {
            	IData other = new DataMap();
        		other.put("USER_ID", reqData.getUca().getUserId());
        		other.put("RSRV_VALUE_CODE", "IDCORDER");
        		other.put("RSRV_VALUE", this.reqData.getUca().getSerialNumber());
        		other.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        		other.put("START_DATE", getAcceptTime());
        		other.put("RSRV_STR1", order.getString("IDC_DEVICE_NAME"));	//设备名称
        		other.put("RSRV_STR2", order.getString("IDC_DEVICE_IP"));	//设备IP
        		other.put("RSRV_STR3", order.getString("IDC_DEVICE_PORT"));	//设备端口名称
        		other.put("INST_ID", SeqMgr.getInstId());
        		other.put("END_DATE", SysDateMgr.getTheLastTime());		
        		//REQ201812180018关于本省IDC流量计费产品增加按天生效时间免费月份及其他优化的需求
        		other.put("RSRV_STR4", order.getString("pam_EFFECTIVE_WAY_TYPE")); //生效方式
        		other.put("RSRV_STR5", order.getString("pam_EFFECTIVE_DATE")); //生效日期
        		other.put("RSRV_STR6", order.getString("pam_FREE_MONTH")); //免费月份
        		other.put("RSRV_STR7", order.getString("pam_FREE_MONTH1")); //免费月份
        		other.put("RSRV_STR8", order.getString("pam_FREE_MONTH2")); //免费月份
                dataset.add(other);
            }
            //删除、修改
            else if("1".equals(flag) || "2".equals(flag)){
            	for (int i = 0; i < userOther.size(); i++)
                {
                    IData each = userOther.getData(i);
                    String instId = each.getString("INST_ID");
                    if (idcInstId.equals(instId))
                    {
                        if ("1".equals(flag))
                        {
                        	each.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        	each.put("UPDATE_TIME", getAcceptTime());
                        	each.put("END_DATE", getAcceptTime());
                            dataset.add(each);
                        }
                        else if ("2".equals(flag))
                        {
                        	each.put("RSRV_STR1", order.getString("IDC_DEVICE_NAME"));	//设备名称
                        	each.put("RSRV_STR2", order.getString("IDC_DEVICE_IP"));	//设备IP
                        	each.put("RSRV_STR3", order.getString("IDC_DEVICE_PORT"));	//设备端口名称
                        	each.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        	//REQ201812180018关于本省IDC流量计费产品增加按天生效时间免费月份及其他优化的需求
                        	each.put("RSRV_STR4", order.getString("pam_EFFECTIVE_WAY_TYPE")); //生效方式
                        	each.put("RSRV_STR5", order.getString("pam_EFFECTIVE_DATE")); //生效日期
                        	each.put("RSRV_STR6", order.getString("pam_FREE_MONTH")); //免费月份
                        	each.put("RSRV_STR7", order.getString("pam_FREE_MONTH1")); //免费月份
                        	each.put("RSRV_STR8", order.getString("pam_FREE_MONTH2")); //免费月份
                            dataset.add(each);
                        }
                    }
                }
            }
        }
        
        addTradeOther(dataset);
        
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
