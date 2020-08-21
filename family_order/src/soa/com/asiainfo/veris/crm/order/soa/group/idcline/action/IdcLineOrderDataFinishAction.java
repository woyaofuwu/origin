
package com.asiainfo.veris.crm.order.soa.group.idcline.action;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
/**
 * 集团IDC专线订购信息数据完工同步第三方处理
 * REQ201805150002新增本省IDC峰值流量及95峰值流量计费套餐的需求
 * @author chenzg
 * @date 2018-06-25
 */
public class IdcLineOrderDataFinishAction implements ITradeFinishAction
{
    private final static Logger logger = Logger.getLogger(IdcLineOrderDataFinishAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
    	logger.debug("In [IdcLineOrderDataFinishAction]>>>"+mainTrade);
    	String tradeId = mainTrade.getString("TRADE_ID");
    	String userId = mainTrade.getString("USER_ID");
    	IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
    	if(IDataUtil.isNotEmpty(tradeOthers)){
    		IDataset devices = new DatasetList();
    		for(int i=0;i<tradeOthers.size();i++){
    			IData device = new DataMap();
    			IData trade = tradeOthers.getData(i);
    			String rsrvValueCode = trade.getString("RSRV_VALUE_CODE");
    			String modifyTag = trade.getString("MODIFY_TAG");
    			if(!"IDCORDER".equals(rsrvValueCode)){
    				continue;
    			}
    			device.put("devName", trade.getString("RSRV_STR1", ""));	//设备名称
    			device.put("devIp", trade.getString("RSRV_STR2", ""));		//设备IP
    			device.put("devPort", trade.getString("RSRV_STR3", ""));	//端口名称
    			device.put("orderId", userId);								//产品订购实例ID
    			device.put("instId", trade.getString("INST_ID", ""));		//唯一标识
    			//第三方返回的唯一标识，新增时返回，修改、删除我们传过去对方原样返回
    			device.put("primaryKey", trade.getString("RSRV_STR10", ""));	
    			device.put("modifyTag", modifyTag);							//操作类型:0-新增、1-删除、2-修改
    			device.put("type", "0");									//类型
    			devices.add(device);
    		}
    		
    		if(IDataUtil.isNotEmpty(devices)){
    			//调用第三方接口
    			String retStr = this.callHttpIntf(devices);
    			//处理结果记录
    			IData respData = new DataMap(retStr);
    			if(IDataUtil.isNotEmpty(respData)){
    				if("200".equals(respData.getString("code", ""))){
    					String dataStr = respData.getString("data", "");	//接口传过去什么，对方返回什么
    					if(StringUtils.isBlank(dataStr)){
    						CSAppException.apperr(CrmCommException.CRM_COMM_13, "接口返回的data字段为空:"+retStr);
    					}
    					IDataset dataLst = new DatasetList(dataStr);
    					for(int i=0;i<dataLst.size();i++){
    						IData each = dataLst.getData(i);
    						IData upParam = new DataMap();
    						upParam.put("TRADE_ID", tradeId);
    						upParam.put("USER_ID", userId);
    						upParam.put("RSRV_VALUE_CODE", "IDCORDER");
    						upParam.put("INST_ID", each.getString("instId"));	//crm的inst_id
    						upParam.put("RSRV_STR10", each.getString("primaryKey"));	//第三方数据的唯一标识
    						upParam.put("REMARK", "调用第三方接口同步成功["+SysDateMgr.getSysTime()+"]");
    						UserOtherInfoQry.updIdcOrderDataInfo(upParam);
    					}
    				}else{
    					CSAppException.apperr(CrmCommException.CRM_COMM_13, "调用第三方接口异常",retStr);
    				}				
    			}
    		}
    		
    	}
    	logger.debug("Out [IdcLineOrderDataFinishAction]>>>"+mainTrade);
    }
    /**
     * 第三方接口协议需要调整，目前设备信息的新增、修改、删除都是单独的接口，而BOSS的业务办理时（比如集团产品资料变更）是可以同时操作新增、修改、删除
     * 因此需要第三方重新提供接口，支持同一个接口里做增、删、改操作，且要一起成功一起失败，否则会出现数据不一致的情况
     * @param param
     * @param httpMethod
     * @throws Exception
     * @author chenzg
     * @date 2018-6-25
     */
    private String callHttpIntf(IDataset datas) throws Exception{
    	long start = System.currentTimeMillis();
    	String retStr = "[]";
    	String url = BizEnv.getEnvString("idcline.plat.url", "http://10.209.168.55:8188/idc/device/modify");
    	StringBuilder logStr = new StringBuilder("");
    	logStr.append("[IdcLineOrderDataFinishAction]请求地址:"+url+"\n");
    	logStr.append("[IdcLineOrderDataFinishAction]请求参数:"+datas.toString()+"\n");
    	System.out.println(logStr);
    	logger.debug(logStr);
    	// 1. 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        RequestConfig requestConfig = RequestConfig.custom()   
        .setConnectTimeout(60000)
        .setConnectionRequestTimeout(60000)   
        .setSocketTimeout(60000).build();  
        // 2. 创建HttpPost对象
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        // 3. 设置POST请求传递参数
        post.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8");
        StringEntity se = new StringEntity(datas.toString(), "UTF-8");
        se.setContentEncoding("UTF-8");
        se.setContentType("application/json"); 
        post.setEntity(se);
        // 4. 执行请求并处理响应
        try {
        	logStr = new StringBuilder("");
            CloseableHttpResponse response = httpClient.execute(post);
            logStr.append("[IdcLineOrderDataFinishAction]调用时长:"+(System.currentTimeMillis()-start)+"\n");
            logStr.append("[IdcLineOrderDataFinishAction]响应状态:"+response.getStatusLine().getStatusCode()+"\n");
            HttpEntity entity = response.getEntity();
            if (entity != null){
            	retStr = EntityUtils.toString(entity, "UTF-8");
            	logStr.append("[IdcLineOrderDataFinishAction]响应内容:\n"+retStr);
            }
            System.out.println(logStr);
            logger.debug(logStr);
            response.close();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        } finally {
            // 释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
            	logger.error(e);
            }
        }
        
        return retStr;
    }

    
}
