package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class FlowPlatCall 
{
    public static Logger logger = Logger.getLogger(FlowPlatCall.class);
    
    public static String TIME_FORMAT ="yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    
  
    public static IData callHttpFlowPlat(IData svcData) throws Exception
    {
      
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        
        String baseURL = BizEnv.getEnvString("crm.call.FlowPlatUrl"); 
        
        //调认证接口
        String authRequestTime = sdf.format(new Date());
       // String appKey = "c1aa3ad1b687de7ec7d84b7babc83318";
       // String appSecret = "bd21b69cf6229443a5ffd8e1ef269079";
        
        //String appKey = BizEnv.getEnvString("crm.FlowAuth.appKey");
        String appKey = BizEnv.getEnvString("crm.callflowcard.appKey");
        //String appSecret = BizEnv.getEnvString("crm.FlowAuth.appSecret");
        String appSecret = BizEnv.getEnvString("crm.callflowcard.appSecret");
        if(StringUtils.isBlank(appKey))
        {
            CSAppException.apperr(GrpException.CRM_GRP_902,"crm.FlowAuth.appKey");
        }
        
        if(StringUtils.isBlank(appSecret))
        {
            CSAppException.apperr(GrpException.CRM_GRP_902,"crm.FlowAuth.appSecret");
        }
        
        String response="";
        //String authURL=baseURL+"/auth.html";
        String authURL=baseURL + BizEnv.getEnvString("crm.flowplat.authurl");
        
        try
        {
            String sign = sha256Hex(appKey + authRequestTime + appSecret);
            
            //String requestXML = "<Request><Datetime>" + authRequestTime + "</Datetime><AUTHORIZATION><APP_KEY>" + appKey +"</APP_KEY><SIGN>" + sign +"</SIGN></AUTHORIZATION></Request>";
            String requestXML = "<Request><Authorization><AppKey>" + appKey + "</AppKey><Sign>" + sign + "</Sign></Authorization><Datetime>" + authRequestTime +"</Datetime></Request>";

            response = doPostAuth(authURL,requestXML,"utf-8",false);
            //response = doPost(authURL, requestXML, null, null, "utf-8", false);
            
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
       // String hashedToken = authorization.element("HASHED_TOKEN").getStringValue();
        
        //调订购接口
        String orderRequestTime = sdf.format(new Date());
        svcData.put("DATETIME", orderRequestTime);
        //String baseURL2=BizEnv.getEnvString("crm.call.FlowPlatUrl2");
        //baseURL=baseURL+"/order/BOSSOrder.html";
        baseURL=baseURL+ BizEnv.getEnvString("crm.flowplat.bossorder");
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
            xmlStr = doPost(baseURL, requestBody, token, signatrue, "utf-8", false);
            
        }
        catch(Exception e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_901,e.toString());
        }
       
        if("".equals(xmlStr))
        {
            CSAppException.apperr(GrpException.CRM_GRP_894);
        }
        
        Document doc2 = DocumentHelper.parseText(xmlStr);
        Element rootElement2 = doc2.getRootElement();
        Element contentElement = rootElement2.element("CONTENT");
        IData resultData = Element2Data(contentElement);
        
        return resultData;
    }
    
    /**
     * 调用流量平台,企业账户余额查询接口（流量包状态比对接口）
     * @param svcData
     * @return
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-3-9
     */
    public static IDataset callHttpFlowPlatForCompare(IData svcData) throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        String baseURL = BizEnv.getEnvString("crm.call.FlowPlatUrl"); 
        //调认证接口
        String authRequestTime = sdf.format(new Date());
        String appKey = BizEnv.getEnvString("crm.callflowcard.appKey");
        String appSecret = BizEnv.getEnvString("crm.callflowcard.appSecret");
        String authURL = baseURL + BizEnv.getEnvString("crm.flowplat.authurl");
        if(StringUtils.isBlank(appKey))
        {
            CSAppException.apperr(GrpException.CRM_GRP_902,"crm.FlowAuth.appKey");
        }
        
        if(StringUtils.isBlank(appSecret))
        {
            CSAppException.apperr(GrpException.CRM_GRP_902,"crm.FlowAuth.appSecret");
        }
        
        String response="";
        try
        {
            String sign = sha256Hex(appKey + authRequestTime + appSecret);
            String requestXML = "<Request><Authorization><AppKey>" + appKey + "</AppKey><Sign>" + sign + "</Sign></Authorization><Datetime>" + authRequestTime +"</Datetime></Request>";
            response = doPostAuth(authURL,requestXML,"utf-8",false);
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
        //调订购接口
        String orderRequestTime = sdf.format(new Date());
        svcData.put("DATETIME", orderRequestTime);
        baseURL = baseURL + BizEnv.getEnvString("crm.flowplat.accountQuery");
        String xmlStr="";
        try
        {
        	String groupId = svcData.getString("GROUP_ID");		//集团产品编码
        	baseURL = baseURL + "?ecode=" + groupId;
            String signatrue = sha256Hex(appSecret);			//GET请求中SIGNATURE的计算公式为：SHA256Hex(appSecret)
        	xmlStr = doGet(baseURL, token, signatrue, "utf-8", false);
        }
        catch(Exception e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_901,e.toString());
        }
       
        if("".equals(xmlStr))
        {
            CSAppException.apperr(GrpException.CRM_GRP_894);
        }
        
        return null;
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
    /**
     * 接口报文转为IDataset
     * @param element
     * @return
     * @throws Exception 
     * @Author:chenzg
     * @Date:2017-3-9
     */
    private static IDataset Element2Dataset(String xmlStr) throws Exception
    {
    	IDataset acctDs = new DatasetList();
    	Document doc2 = DocumentHelper.parseText(xmlStr);
        Element rootElement2 = doc2.getRootElement();
        Element accountsEle = rootElement2.element("Accounts");	//<Accounts>
        List acctList = accountsEle.elements("Account");
        for (Iterator<Element> it = acctList.iterator(); it.hasNext();)
        {
            Element acctEle = it.next(); //<Account>
            List eles = acctEle.elements();
            IData acctData = new DataMap();
            for (Iterator<Element> eit = eles.iterator(); eit.hasNext();)
            {
            	Element ele = eit.next();
            	acctData.put(ele.getName().toUpperCase(), ele.getText());
            }
            acctDs.add(acctData);
        }
        
        return acctDs;
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
    
    public static String HMACSign(String text,String key)
    {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            byte[] secretByte = key.getBytes("UTF-8");
            byte[] dataBytes = text.getBytes("UTF-8");
            SecretKey secret = new SecretKeySpec(secretByte, "HMACSHA256");
            mac.init(secret);
            byte[] doFinal = mac.doFinal(dataBytes);
            byte[] hexB = new Hex().encode(doFinal);
            String checksum = new String(hexB);
            return checksum;
        } catch (NoSuchAlgorithmException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } catch (InvalidKeyException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        return null;
    }
    
    public static String doPostAuth(String url, String reqStr,String charset, boolean pretty)
    {
        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();

        PostMethod method = new PostMethod(url);
        try {
            method.setRequestEntity(new StringRequestEntity(reqStr, "application/xml", "utf-8"));
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) 
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(),charset));
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    if (pretty)
                    {
                        response.append(line).append(
                        System.getProperty("line.separator"));
                    }
                    else
                    {
                        response.append(line);
                    }
                }
                reader.close();
            }
        } catch (UnsupportedEncodingException e1) 
        {
        	if (logger.isDebugEnabled())

            logger.debug("UnsupportedEncodingException异常！"+e1.getMessage());
            return "";
        }catch (IOException e) {
        	if (logger.isDebugEnabled())

            logger.debug("执行HTTP Post请求" + url + "时，发生IO异常！"+ e.getMessage());
            return "";
        } 
        finally 
        {
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
        	if(StringUtils.isNotBlank(requestXML)){
                 method.setRequestEntity(new StringRequestEntity(requestXML, "application/xml", "utf-8"));
        	}

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
    
    /**
     * 处理http Get调用
     * @param url
     * @param token
     * @param signatrue
     * @param charset
     * @param pretty
     * @return
     * @Author:chenzg
     * @Date:2017-3-9
     */
    public static String doGet(String url, String token, String signatrue, String charset, boolean pretty) {
        StringBuffer response = new StringBuffer();
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        try {
        	method.addRequestHeader("Content-Type", "text/html; charset="+charset);  
        	if(StringUtils.isNotBlank(token)){
        		method.addRequestHeader("4GGOGO-Auth-Token", token);
        	}
        	if(StringUtils.isNotBlank(signatrue)){
        		method.addRequestHeader("HTTP-X-4GGOGO-Signature", signatrue);
        	}   
            client.executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), charset));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (pretty) {
                        response.append(line).append(System.getProperty("line.separator"));
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

        	logger.debug("执行HTTP doGet请求" + url + "时，发生IO异常！"+ e.getMessage());
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
    
    private static byte[] getBytes(String string, Charset charset)
    {
        if(string == null)
            return null;
        else
            return string.getBytes(charset);
    }

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    
    public static void main(String[] args) throws Exception{
    	/*IDataset packageList = new DatasetList();
    	IData packageData = new DataMap();
        packageData.put("PAK_NUM", "2"); //流量包个数
        packageData.put("PAK_MONEY", "1000");  //流量包单价
        packageData.put("PAK_GPRS", "100");    //每个流量包的流量
        packageData.put("PAK_END_DTAE", "2017-03-31");//流量包失效时间
        
        packageList.add(packageData);
    	IData svcData = new DataMap();
        IData contData = new DataMap();
        contData.put("TRANS_IDO", "transIDO");
        contData.put("GRP_ID", "89800001");
        contData.put("ITEM", packageList);
        svcData.put("CONTENT", contData);
    	Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");
        Element element = document.addElement("Request");
        toXml(element, (Object)svcData);
        String requestBody = element.asXML();
        */
    	
    	String xml = "<Response>"+
					 	"<Datetime>2016-03-19T15:43:33.136+08:00</Datetime>"+
					  	"<TotalCount>10</TotalCount>"+
					    "<Accounts>"+
					    	"<Account>"+
					        	"<ProductCode >123456789</ProductCode>"+
					        	"<EnterCode>3</EnterCode >"+
					        	"<Count>10</Count>"+
					      	"</Account>"+
					      	"<Account>"+
					        	"<ProductCode >123456789</ProductCode>"+
					        	"<EnterCode>3</EnterCode >"+
					        	"<Count>10</Count>"+
					      	"</Account>"+
					  	"</Accounts>"+
					"</Response>";
    	IDataset ds = Element2Dataset(xml);
    }
}
