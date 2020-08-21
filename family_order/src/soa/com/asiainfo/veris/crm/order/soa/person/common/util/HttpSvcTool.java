package com.asiainfo.veris.crm.order.soa.person.common.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.client.request.Wade3ClientRequest;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import java.io.InputStream;

/**
 * Http接口调用工具类
 * 
 * @param
 * @return
 * @author 
 */
public class HttpSvcTool extends CSBizService{
	private static final Logger log = Logger.getLogger(HttpSvcTool.class);

	public static IDataset sendHttpData(Map<String, Object> inParam, String urlQryParam) throws Exception {
		
		//inParam.put("PROVINCE_CODE", visit.getProvinceCode());
		inParam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
		inParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

		inParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		inParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		inParam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		inParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	
		String in = Wade3DataTran.toWadeString(inParam);

		// 调用接口
		String url = BizEnv.getEnvString("crm.jrbank");
//		url = "http://10.200.130.85:9105/uip_inhttp/crm";
		
		String response = Wade3ClientRequest.request(url, urlQryParam, in, "GBK");

		@SuppressWarnings("unchecked")
		Map<String, Object> tomap = Wade3DataTran.strToMap(response);
		IData data = new DataMap(tomap);
		IDataset dataset = toDataset2(data);
		return dataset;
	}

	static IDataset toDataset2(IData dataMap) throws Exception {
		int size = 1;
		IDataset dataset = new DatasetList();
		String names[] = dataMap.keySet().toArray(new String[0]);
		for (int i = 0; i < names.length; i++) {
			if (!(dataMap.get(names[i]) instanceof List))
				continue;
			List<?> list = (List<?>) dataMap.get(names[i]);
			if (size < list.size())
				size = list.size();
		}

		for (int i = 0; i < size; i++) {
			IData data = new DataMap();
			for (int j = 0; j < names.length; j++) {
				Object obj = dataMap.get(names[j]);

				if (obj instanceof List) {
					List<?> list = (List<?>) obj;
					if (i < list.size())
						data.put(names[j], list.get(i));
					else
						data.put(names[j], "");
				} else {
					if (i == 0) {
						data.put(names[j], obj);
					}
				}
			}

			dataset.add(data);
		}

		return dataset;
	}
	/**
	 *  http调用
	 * @param 
	 * GET 时 参数放于url urlNameString http://127.0.0.1:8080/weblobby?flag=0&test=2
	 * GET
	 * @return {"code":"0","msg":"成功！"}
	 * @throws Exception
	 */
	public static String sendHttpGetMsg(String urlNameString) throws Exception{


		log.info("sendHttpGetMsg-urlNameString"+urlNameString);
		
		URL realUrls = new URL(urlNameString);
		URLConnection connection = realUrls.openConnection();
		
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		// 建立实际的连接
		connection.connect();
		// 获取所有响应头字段
		Map<String, List<String>> map = connection.getHeaderFields();
		// 遍历所有的响应头字段
		for (String key : map.keySet())
		{
		}
		// 定义 BufferedReader输入流来读取URL的响应
		BufferedReader ins = null;
		ins=new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		
		String line;
		String httpreturn="";
		while ((line = ins.readLine()) != null)
		{
			httpreturn += line;
		}
		log.info("sendHttpGetMsg-httpreturn"+httpreturn);
		
		return httpreturn;
	}
	/**
	 * 
	 * @param url  发送地址
	 * @param str  入参 json格式 {"sourceCode":"1085","reqInfo":{"picNameR":"/9j/4A"}}
	 * @param paramsCode  入参编码 null、 GBK 、UTF-8
	 * @return
	 * @throws Exception
	 */
	public  static IData sendHttpPostMsg(String url,String str,String paramsCode)throws Exception {
		   String params =  str;
		   PostMethod post = null;
		   IData resultMap = null;
		   InputStreamReader isr=null;
		   InputStream fis=null;
		   try{
			   log.info("sendHttpPostMsg-url"+url);
			   log.info("sendHttpPostMsg-params"+params);
			   log.info("sendHttpPostMsg-paramsCode"+paramsCode);
			   IData param = new DataMap();
			   param.put("EPARCHY_CODE", "ZZZZ");
			   param.put("PARA_ATTR", "10");
			   param.put("PARA_CODE1", "HTTP_SERVICE_ADDR");
			   param.put("PARA_CODE2", "PF");
			   
			   post = new PostMethod(url);
			   HttpClient client = new HttpClient();
		       Header header = new Header();
		       header.setName("content-type");
		       header.setValue("application/x-www-form-urlencoded;charset=UTF-8");
		       
		       post.addRequestHeader(header);
		       InputStream is = new java.io.ByteArrayInputStream(params.getBytes());
		       if(paramsCode!=null){
		    	   is = new java.io.ByteArrayInputStream(params.getBytes(paramsCode));
		       }
		       
		       /*InputStream is = new java.io.ByteArrayInputStream(params.getBytes("UTF-8"));*/
			   post.setRequestBody(is);
			   client.executeMethod(post);
			   
			   if (post.getStatusCode() == HttpStatus.SC_OK) {

				  fis=post.getResponseBodyAsStream();
				  isr=new InputStreamReader(fis, "UTF-8");
			      BufferedReader br = new BufferedReader(isr);
				  StringBuffer buff = new StringBuffer();
			      String response = null;
				  while ((response = br.readLine()) != null) {
					 buff.append(response);
				  }
				  log.info("sendHttpPostMsg-responseStr"+buff.toString());
				  resultMap = Wade3DataTran.wade3To4DataMap(Wade3DataTran.strToMap(buff.toString()));
				  
			   }
			}catch(Exception e){
			   throw e;
		    }finally{
		    	if(fis!=null){
		    		fis.close();
		    	}
		    	if(isr!=null){
		    		isr.close();
		    	}
		    }
		   return resultMap;
		}
}
