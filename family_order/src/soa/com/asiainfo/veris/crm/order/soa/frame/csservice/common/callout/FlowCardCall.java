package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class FlowCardCall extends CSBizService
{
    public static Logger logger = Logger.getLogger(FlowCardCall.class);
    
    public static String TIME_FORMAT ="yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    
    
    /*public static void main(String[] args) throws Exception
	{
    	SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
    	String baseURL= "http://10.200.158.40:7756/hntest";
        String authorizationURL = baseURL + "/auth.html";
        String authRequestTime = sdf.format(new Date());
        String appKey = "c03ccba2b3f3eb9493e8005c5210410b";
        String appSecret = "22c0b9c2fa10cc7a1e56830937b48d15";
        String sign = DigestUtils.shaHex(appKey + authRequestTime + appSecret);
        String requestXML = "<Request><Datetime>" + authRequestTime + "</Datetime><Authorization><AppKey>" + appKey + "</AppKey><Sign>" + sign + "</Sign></Authorization></Request>";

        String response = doPost(authorizationURL, requestXML, null, null, "utf-8", false);
        
      //解析返回报文，获取token
        Document doc = DocumentHelper.parseText(response);
        Element rootElement = doc.getRootElement();
        Element authorization = rootElement.element("Authorization");
        
        String token = authorization.element("Token").getStringValue();
        String orderRequestTime = sdf.format(new Date());
        
      //营销卡充值接口  
        String cardNum = "100001623000100001000000005";//卡号
        String password = "60092868945129";//密码
        String mobile = "18867101970";//手机号码
        String serialNum = "8989859421";//序列号
        
        String orderUrl = baseURL + "/mdrc/order.html";
        String mdrcChargeBody = "<Request><Card><Password>60092868945129</Password><Mobile>18867101970</Mobile><SerialNum>8989859421</SerialNum><CardNum>100001623000100001000000005</CardNum></Card><Datetime>2016-06-30T17:24:57.930+0800</Datetime></Request>";
          
        String mdrcCharegeSignatrue = DigestUtils.shaHex(mdrcChargeBody + appSecret);
        String operateResponse = doPost(orderUrl, mdrcChargeBody, token, mdrcCharegeSignatrue, "utf-8", false);
        
	}*/
       
    public static IData callHttpFlowPlat(IData svcData) throws Exception
    {
    	if(IDataUtil.isNotEmpty(svcData))
    	{
    		svcData.remove("TRADE_EPARCHY_CODE");
    	}
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        
        String baseURL = BizEnv.getEnvString("crm.callflowcard.url");
        
        //调认证接口
        String authRequestTime = sdf.format(new Date());
        
        String appKey = BizEnv.getEnvString("crm.callflowcard.appKey");
        String appSecret = BizEnv.getEnvString("crm.callflowcard.appSecret");
        
        if(StringUtils.isBlank(appKey))
        {
            CSAppException.apperr(GrpException.CRM_GRP_902,"crm.callflowcard.appKey");
        }
        
        if(StringUtils.isBlank(appSecret))
        {
            CSAppException.apperr(GrpException.CRM_GRP_902,"crm.callflowcard.appSecret");
        }
        
        String response="";
        String authURL = baseURL + BizEnv.getEnvString("crm.callflowcard.authurl");
        
        try
        {
        	String sign = sha256Hex(appKey + authRequestTime + appSecret);
            String requestXML = "<Request><Authorization><AppKey>" + appKey + "</AppKey><Sign>" + sign + "</Sign></Authorization><Datetime>" + authRequestTime +"</Datetime></Request>";
            /*if (logger.isDebugEnabled()){
            	 logger.debug("==============sign========"+sign);
                 logger.debug("==============requestXML========"+requestXML);
                 logger.debug("==============authURL========"+authURL);
            }*/
           
            logger.info("==============sign========"+sign);
            logger.info("==============requestXML========"+requestXML);
            logger.info("==============authURL========"+authURL);
            //response = doPostAuth(authURL,requestXML,"utf-8",false);
            response = doPost(authURL, requestXML, null, null, "utf-8", false);
            logger.info("==============response========"+response);
            /*if (logger.isDebugEnabled())
            logger.debug("==============response========"+response);*/
        }
        catch(Exception e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_899,e.toString());
        }
        
        if("".equals(response))
        {
            CSAppException.apperr(GrpException.CRM_GRP_900);
        }
        
        //解析返回报文，获取token
        Document doc = DocumentHelper.parseText(response);
        Element rootElement = doc.getRootElement();
        Element authorization = rootElement.element("Authorization");       
        String token = authorization.element("Token").getStringValue();
        
        //调变更接口
        String orderRequestTime = sdf.format(new Date());
        svcData.put("Datetime", orderRequestTime);
        
        String operateURL = baseURL + BizEnv.getEnvString("crm.callflowcard.operateurl");
        String orderURL = baseURL + BizEnv.getEnvString("crm.callflowcard.orderurl");
        String xmlStr="";
        
        try
        {
            Document document = DocumentHelper.createDocument();
            document.setXMLEncoding("utf-8");
            Element element = document.addElement("Request");
            toXml(element, (Object)svcData);
            String requestBody = element.asXML();
            //String signatrue = HMACSign(requestBody, token);
            String signatrue = sha256Hex(requestBody + appSecret);
            /*if (logger.isDebugEnabled()){
            	logger.debug("=========order=====svcData========"+svcData);
                logger.debug("=========order=====signatrue========"+signatrue);
                logger.debug("=========order=====requestBody========"+requestBody);
                logger.debug("=========order=====token========"+token);
            }*/
            logger.info("=========order=====svcData========"+svcData);
            logger.info("=========order=====signatrue========"+signatrue);
            logger.info("=========order=====requestBody========"+requestBody);
            logger.info("=========order=====token========"+token);
            IData data = new DataMap(svcData.getString("Card",""));
            if(!"".equals(data.getString("Password",""))){
            	
            	/* if (logger.isDebugEnabled()){	
            	logger.debug("=========order=====充值请求========");
            	logger.debug("=========order=====orderURL========"+orderURL);
            	 }*/
            	logger.info("=========order=====充值请求========");
             	logger.info("=========order=====orderURL========"+orderURL);
            	xmlStr = doPost(orderURL, requestBody, token, signatrue, "utf-8", false);
            	logger.info("=========order=====xmlStr========"+xmlStr);
            }else
            {
            	/* if (logger.isDebugEnabled()){
	            	logger.debug("=========order=====变更请求========");
	                logger.debug("=========order=====operateURL========"+operateURL);
            	 }*/
            	 logger.info("=========order=====变更请求========");
	             logger.info("=========order=====operateURL========"+operateURL);
            	xmlStr = doPut(operateURL, requestBody, token, signatrue, "utf-8", false);
            	logger.info("=========order=====xmlStr========"+xmlStr);
            }
            /*if (logger.isDebugEnabled())
            logger.debug("=========order=====xmlStr========"+xmlStr);*/
            
            logger.info("=========order=====xmlStr========"+xmlStr);
        }
        catch(Exception e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_901,e.toString());
        }
       
        if("".equals(xmlStr))
        {
            CSAppException.apperr(GrpException.CRM_GRP_894);
        }
        IData resultData = parseXml(xmlStr);
        return resultData;
    }
    
    private static IData Element2Data(Element element)
    {
        IData data = new DataMap();
        List list = element.elements();
        for (Iterator<Element> it = list.iterator(); it.hasNext();)
        {
            Element elm = it.next();
            data.put(elm.getName().toUpperCase(), elm.getText());
        }
        
        return data;
    }
    
    private static void toXml(Element element, Object obj)
    {

        if (obj instanceof String)
        {
            String o = (String) obj;
            String s = String.valueOf(o.length());
            element.addText(o);
        }
        else if (obj instanceof IData)
        {
            IData o = (IData) obj;
            String s = String.valueOf(o.size());
            addIData(element, o);
        }
        else if (obj instanceof IDataset)
        {
            IDataset o = (IDataset) obj;
            String s = String.valueOf(o.size());
            
            addIDataset(element, o);
        }
    }
    
    private static Element addIData(Element element, IData idata)
    {

        Set it = idata.entrySet();
        if (it != null)
        {
            Iterator iterator = it.iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Entry) iterator.next();
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                Element newElement = element.addElement(key);
                toXml(newElement, value);
            }
        }
        return element;
    }

    private static Element addIDataset(Element element, IDataset idataset)
    {

        for (int size = idataset.size(), i = 0; i < size; i++)
        {
            Object childObj = idataset.get(i);
            toXml(element, childObj);
        }
        return element;
    }

    private static Element addString(Element element, String s)
    {

        element.addAttribute("Value", s);
        return element;
    }
    
    public static IData parseXml(String responseXml)throws Exception {
    	IData data = new DataMap();
    	XMLInputFactory factory = XMLInputFactory.newInstance();   
    	factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);   
    	XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(responseXml));  
    	try {   
    	 int event = reader.getEventType();//获取节点类型,结果是以整形的方式返回的。   
    	 while (true) {
    	                switch (event) {   
    	                    case XMLStreamConstants.START_DOCUMENT://表示的是文档的开通节点。   
    	                        break;   
    	                    case XMLStreamConstants.START_ELEMENT://开始解析开始节点 
    	                     //log.debug("getLocalName:"+reader.getLocalName());
    	                        if (reader.getLocalName().equals("DateTime")) {
    	                         data.put("DateTime", reader.getElementText());
    	                        } 
    	                        if (reader.getLocalName().equals("RspCode")) {
    	                         data.put("RspCode", reader.getElementText());
    	                        } 
    	                        if (reader.getLocalName().equals("RspDesc")) {
    	                         data.put("RspDesc", reader.getElementText());
    	                        }
    	                        if (reader.getLocalName().equals("CardTotal")) {
    	                         data.put("CardTotal", reader.getElementText());
    	                        }
    	                        if (reader.getLocalName().equals("FailCardNum")) {
       	                         data.put("FailCardNum", reader.getElementText());
    	                        }
    	                    case XMLStreamConstants.CHARACTERS:   
    	                        // if(reader.isWhiteSpace())   
    	                        // break;   
    	                        break;   
    	                        
    	                        //文档的结束元素   
    	                    case XMLStreamConstants.END_ELEMENT: 

    	                        break;   
    	                        //文档的结束。   
    	                    case XMLStreamConstants.END_DOCUMENT:   
    	                        break;   
    	                    }
    	             if (!reader.hasNext()) {   
    	              break;   
    	             } 
    	 event = reader.next();
    	 }
    	}catch (Exception e) {
    	     e.printStackTrace();
    	} finally {   
    	reader.close(); 
    	return data;
    	    }
    	}
    
    public static String doPut(String url, String requestXML, String token, String signatrue, String charset, boolean pretty) {
        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();

        PutMethod method = new PutMethod(url);
        try {
        	if(StringUtils.isNotBlank(token)){
               method.addRequestHeader("4GGOGO-Auth-Token", token);
        	}
        	if(StringUtils.isNotBlank(signatrue)){
        		 method.addRequestHeader("HTTP-X-4GGOGO-Signature", signatrue);
        	}           
            method.setRequestEntity(new StringRequestEntity(requestXML, "application/xml", "utf-8"));

            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(method.getResponseBodyAsStream(),
                                charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (pretty) {
                        response.append(line).append(
                                System.getProperty("line.separator"));
                    } else {
                        response.append(line);
                    }
                }
                
                reader.close();
            }
        } catch (UnsupportedEncodingException e1) {
        	if (logger.isDebugEnabled())

        	logger.debug("UnsupportedEncodingException异常！"+e1.getMessage());
            return "";
        } catch (IOException e) {
        	if (logger.isDebugEnabled())

        	logger.debug("执行HTTP doPut请求" + url + "时，发生IO异常！"+ e.getMessage());

            return "";
        } finally {
            method.releaseConnection();
        }
        return response.toString();
    }
    
    public static String doPost(String url, String requestXML, String token, String signatrue, String charset, boolean pretty) {
        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();

        PostMethod method = new PostMethod(url);
        try {
        	if(StringUtils.isNotBlank(token)){
               method.addRequestHeader("4GGOGO-Auth-Token", token);
        	}
        	if(StringUtils.isNotBlank(signatrue)){
        		 method.addRequestHeader("HTTP-X-4GGOGO-Signature", signatrue);
        	}           
            method.setRequestEntity(new StringRequestEntity(requestXML, "application/xml", "utf-8"));

            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(method.getResponseBodyAsStream(),
                                charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (pretty) {
                        response.append(line).append(
                                System.getProperty("line.separator"));
                    } else {
                        response.append(line);
                    }
                }

                reader.close();
            }
        } catch (UnsupportedEncodingException e1) {
        	if (logger.isDebugEnabled())
        	logger.debug("UnsupportedEncodingException异常！"+e1.getMessage());
            return "";
        } catch (IOException e) {
        	if (logger.isDebugEnabled())
        	logger.debug("执行HTTP doPost请求" + url + "时，发生IO异常！"+ e.getMessage());

            return "";
        } finally {
            method.releaseConnection();
        }
        return response.toString();
    }
    
    
    
    public static String sha256Hex(String data)
    {
        return encodeHexString(sha256(data));
    }
    public static byte[] sha256(String data)
    {
        return sha256(getBytesUtf8(data));
    }
    public static byte[] sha256(byte data[])
    {
        return getSha256Digest().digest(data);
    }
    public static MessageDigest getSha256Digest()
    {
        return getDigest("SHA-256");
    }
    public static MessageDigest getDigest(String algorithm)
    {
        try
        {
            return MessageDigest.getInstance(algorithm);
        }
        catch(NoSuchAlgorithmException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static String encodeHexString(byte data[])
    {
        return new String(Hex.encodeHex(data));
    }
    
    public static byte[] getBytesUtf8(String string)
    {
        if(string == null)
            return null;
        else
            return string.getBytes(UTF_8);
    }
    

    public static final Charset UTF_8 = Charset.forName("UTF-8");
}
