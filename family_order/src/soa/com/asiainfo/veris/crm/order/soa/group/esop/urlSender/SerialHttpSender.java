/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.group.esop.urlSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;

import com.ailk.uip.common.exception.HttpReadTimeOutException;


/*****************************************************************  
 * <p>Filename:    SerialHttpSender.java 
 * <p>Description: 
 * <p>
 * <p>Copyright:   Copyright (c)2008  
 * <p>Company:     Linkage Group  
 * <p>Create at:   2009-2-28 下午12:35:50  
 * <p> 
 * <p>Modification History:  
 * <p>Date          Author     Version   Description  
 * <p>------------------------------------------------------------------  
 * <p>2008-12-26    wanglj     1.0       first Version 
 * <p>------------------------------------------------------------------
 *
 * @author         wanglj
 * @version        1.0 
 * @since          JDK1.5
 *********************************************************************/
public class SerialHttpSender  {
    /**
     * contenttype
     */
    private String contentType =
        "application/x-www-form-urlencoded;";

    /**
     * 是否进行验证
     */
    private boolean doAuthentication = false;

    /**
     * http的验证用户名
     */
    private String userName = null;
    
    private static String encode = "GBK";

    /**
     * 验证密码
     */
    private String password = null;

    /**
     * 设置http的超时时间,默认值0是没有超时时间
     */
    private int timeout = 0;

    /**
     * 域
     */
    private String realm = "default";

    private String host = "";

    /**
     * 默认构造函数
     */
    public SerialHttpSender() {

    }
    

    /**
     * 发送数据
     * @param url URL
     * @param requestParamNames String[]
     * @param requestParamValues String[]
     * @param typeCode String GBK UTF-8
     * @return String
     */
    public String sendHttpData(URL url, String[] requestParamNames,
                               String[] requestParamValues,String typeCode) throws
        Exception {
    	InputStream is = null;
        UsernamePasswordCredentials credentials = null;
        if (url == null)
        {
            return null;
        }

        String userInfo = url.getUserInfo();
        //判断是否有验证信息
        if (userInfo != null && userInfo.trim().length() != 0 &&
            userInfo.indexOf(":") != -1)
        {

            credentials = new UsernamePasswordCredentials(userInfo);

            setUserInfo(credentials.getUserName(), credentials.getPassword());
            setDoAuthentication(true);
            setHost(url.getHost());
        }

        HttpClient client = new HttpClient();
        PostMethod httpPost = new encodePostMethod(url.toString());

        //设置验证信息
        if (isDoAuthentication() && credentials != null)
        {
            HttpState state = new HttpState();
            //state.setCredentials(getRealm(), getHost(), credentials);
            client.setState(state);

            httpPost.setDoAuthentication(true);
        }


		httpPost.setRequestHeader("Content-type", contentType);


        //设置超时时间
        client.setTimeout(timeout);
//        UIPLogger.getLogger(getClass()).debug(
//                "[SerialHttpSender] Http超时时间:"+timeout);

        //放入需要发送的数据
//        NameValuePair valuePairs[] = getValuePairArray(requestParamNames,
//                                                       requestParamValues);
//        if (valuePairs == null || valuePairs.length == 0)
//        {
//        	UIPLogger.getLogger(getClass()).info(
//                "[SerialHttpSender] Post发送的键值对为空!");
//        }
//        
    	int requestValueLen = requestParamValues.length;
    	StringBuffer buffer = new StringBuffer("");
    	for (int i=0;i< requestValueLen; i++) {
    		buffer.append(requestParamValues[i]);
    	}
    	String xml = buffer.toString();
    	
    	// System.out.println(" xml = " + xml);
//    	System.out.print("\n====================request sender string==========================\n"+xml);
//    	UIPLogger.getLogger(getClass()).debug("\n====================request sender string==========================\n"+xml);
    	is = new java.io.ByteArrayInputStream(xml.getBytes(typeCode));
    	httpPost.setRequestBody(is);
        
        
        //发送数据并获取响应
        String responseData = null;
//        UIPLogger.getLogger(getClass()).debug("[SerialHttpSender] 发送http请求URL: "+url.toString());
        try
        {
            Exception exception = null;
            client.getState().setCredentials(null,getHost(), credentials);
            client.executeMethod(httpPost);
            int resStatusCode = httpPost.getStatusCode();
            //判断响应代码
            if (resStatusCode == HttpStatus.SC_OK)
            {
                BufferedReader br = new BufferedReader( new InputStreamReader(httpPost.getResponseBodyAsStream(),"GBK"));
    			String str = null;
    			StringBuffer sb = new StringBuffer();
    			while ((str = br.readLine()) != null) {
    				sb.append(str);
    			}
    			responseData = sb.toString();
    			//responseData = new String(responseData.getBytes(),"UTF-8");
    			//System.out.println("========= responseData:" + responseData);
    			
    			//String httpParamValueUTF = java.net.URLEncoder.encode(responseData, "UTF-8");
    			//responseData = java.net.URLDecoder.decode(httpParamValueUTF, "UTF-8");
    			// System.out.println(" xml res = " + responseData);
            }
            else
            {
                exception = new Exception("[SerialHttpSender] Http连接返回失败.ErrorCode:" +
                                          resStatusCode);
            }
            if (exception != null)
            {
                throw exception;
            }
        }
        catch (java.net.ConnectException ex)
        {
            throw ex;
        }
        catch (IOException ex)
        {
            //能够建立连接,但是等待响应的时候超时SocketTimeoutException
            //org.apache.commons.httpclient.HttpRecoverableException:
            //java.net.SocketTimeoutException: Read timed out

            String message = ex.getMessage();
            if (message != null && message.toLowerCase().indexOf("read timed") > -1)
            {
//            	UIPLogger.getLogger(getClass()).error("[SerialHttpSender] 等待响应超时!");
                throw new HttpReadTimeOutException(ex.getMessage());
            }else{
//            	UIPLogger.getLogger(getClass()).error("[SerialHttpSender] 连接主机超时!");
                throw ex;
            }

        }
        catch (Exception ex)
        {
//        	UIPLogger.getLogger(getClass()).error(
//                "[SerialHttpSender] 发送http请求未知异常! ");
            throw ex;
        }
        finally{
        	if(is != null)
        		is.close();
        	httpPost.releaseConnection();
        }
//        UIPLogger.getLogger(getClass()).debug("\n====================response sender string end ==========================\n"+responseData);
        return responseData;
    }

    /**
     * 获取参数的数组
     * @param requestParamNames String[] 参数名数组
     * @param requestParamValues String[] 参数值数组
     * @return NameValuePair[]
     */
    private NameValuePair[] getValuePairArray(String[] requestParamNames,
                                              String[] requestParamValues) {
        NameValuePair[] arrayValuePair = null;
        if (requestParamNames == null || requestParamValues == null)
        {
            return new NameValuePair[0];
        }

        int paramCount = Math.min(requestParamNames.length,
                                  requestParamValues.length);
        arrayValuePair = new NameValuePair[paramCount];

        for (int i = 0; i < arrayValuePair.length; i++)
        {
            arrayValuePair[i] = new NameValuePair(requestParamNames[i],
                                                  requestParamValues[i]);
        }
        return arrayValuePair;
    }

    /**
     * 设置验证信息
     * @param userName String
     * @param pass String
     */
    public void setUserInfo(String userName, String pass) {
        this.userName = userName;
        this.password = pass;
    }

    /**
     * 初始化参数
     * @param params String[]
     */
    public void initInvokeParams(String[] params) {

    }

    public String getContentType() {
        return contentType;
    }

    public boolean isDoAuthentication() {
        return doAuthentication;
    }

    public String getHost() {
        return host;
    }

    public String getRealm() {
        return realm;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setDoAuthentication(boolean doAuthentication) {
        this.doAuthentication = doAuthentication;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

//    public static void main(String[] args) {
//        HttpSender sender = new HttpSender();
//        sender.setTimeout(10000);
//        try
//        {
//            String s = sender.sendHttpData(new URL(
//                "http://a:a123@10.143.15.183:8080/test/2.jsp"),
//                                           new String[] {
//                "pig1", "pig2"
//            }, new String[]
//                {
//                "p1", "p2"});
//
//            System.out.println(s);
//
//        }
//        catch (Exception ex)
//        {
//            System.out.println("pppp");
//            ex.printStackTrace();
//        }
//
//    }
    
    public static class encodePostMethod extends PostMethod{
    	public encodePostMethod(String url){
    		super(url);
    	}

    	public String getRequestCharSet() {
    		//return super.getRequestCharSet();
    		return SerialHttpSender.encode;
    	}
    }
}
