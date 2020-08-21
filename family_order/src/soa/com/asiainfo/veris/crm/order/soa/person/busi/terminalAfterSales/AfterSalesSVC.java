
package com.asiainfo.veris.crm.order.soa.person.busi.terminalAfterSales;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.terminalAfterSales.AfterSalesQry;

/**
 * 售后服务接口
 * @author zyz
 * @version 1.0
 *
 */
public class AfterSalesSVC extends CSBizService
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1814212176503925400L;
	protected static Logger log = Logger.getLogger(AfterSalesSVC.class);

	/**
	 * 检查优惠券接口
	 * 参数：
	 *  SERIAL_NUMBER
	 *  discountNo
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData checkCouponInfo(IData input) throws Exception{
    	
    	IData data=new DataMap();
    	try {
    	    IDataset dataset=AfterSalesQry.queryCheckCouponInfo(input);
    		if(IDataUtil.isNotEmpty(dataset)){
    			//面额
    			data.put("Amount", dataset.getData(0).getString("TICKET_VALUE"));
    			//有效性
    			data.put("valid", dataset.getData(0).getString("TICKET_STATE"));
    			data.put("X_RESULTCODE", "0");
    			data.put("X_RESULTINFO", "ok");
    		}else{
    			data.put("X_RESULTCODE", "2998");
    			data.put("X_RESULTINFO","无数据");
    		}
		} catch (Exception e) {
			// TODO: handle exception
			//log.info("(e);
			data.put("X_RESULTCODE", "2998");
			data.put("X_RESULTINFO", e.getMessage());
		}
		return data;
    }
	/**
	 * 使用优惠券接口
	 * 参数：
	 *  SERIAL_NUMBER
	 *  discountNo
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData useCouponInfo(IData input) throws Exception{
    	
    	IData data=new DataMap();
    	try {
    	    int mark=AfterSalesQry.useCouponInfo(input);
    	    if(mark >= 1){
    	    	//修改优惠券限额表
        	    int mark_1 = AfterSalesQry.useCouponQuota(input);
      			data.put("X_RESULTCODE", "0");
    			data.put("X_RESULTINFO", "使用优惠券,成功");
    	    }else{
    			data.put("X_RESULTCODE", "2998");
    			data.put("X_RESULTINFO", "使用优惠券,失败");
    	    }
		} catch (Exception e) {
			// TODO: handle exception
			//log.info("(e);
			data.put("X_RESULTCODE", "2998");
			data.put("X_RESULTINFO", e.getMessage());
		}
		return data;
    }
    
    /**
     * 更换串号通知接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData  updateCouponInfo(IData input) throws Exception{
        IData  result=new DataMap();
    	try {
    		AfterSalesQry.updateImeiNoInfo(input);
    		result.put("X_RESULTCODE", "0");
    		result.put("X_RESULTINFO", "更换串号通知,成功");
		} catch (Exception e) {
			//log.info("(e);
			result.put("X_RESULTCODE", "2998");
			result.put("X_RESULTINFO", e.getMessage());
		}
		return result;
    	
    }
    
    
    /**
     * 根据服务号获取优惠信息
     * @param input
     * @return
     * @throws Exception
     */
    public  IDataset  getCouponInfoServiceno(IData input) throws Exception {
    	try {
			return AfterSalesQry.getCouponInfoServiceno(input);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
    }
    
    
    /**
     * 售后服务进度查询
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qrySalesSchedule(IData input)throws Exception {
    	try {
    		  //获取参数信息
    		 IDataset commparaInfos = CommparaInfoQry.getOnlyByAttr("CSM", "1678", "0898");
    		 IData data=commparaInfos.getData(0);
    		 //地址
    		 input.put("URL", data.getString("PARA_CODE17"));
    		 //方法
    		 input.put("ASYNCLOCALPART", data.getString("PARA_CODE2"));
    		 //用户名
    		 input.put("WSUSER", data.getString("PARA_CODE3"));
    		 //密码
    		 input.put("WSPASS", data.getString("PARA_CODE4"));
    		 //命名空间
    		 input.put("BIPNAMESPACE", data.getString("PARA_CODE5"));
    		 
    		 
			return callWebService(input);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
    }
    
    
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset callWebService(IData input)throws Exception{
    	try {
    		IDataset dataset=new DatasetList();
    		
    		//本地代理
/*    		Properties prop = System.getProperties();   
    		prop.put("http.proxyHost","10.199.48.58");   
    		prop.put("http.proxyPort","18081");*/
    		
    		
        	Service service = new Service();
            Call call = (Call) service.createCall();
            
            String url =input.getString("URL");
            String bipNamespace =input.getString("BIPNAMESPACE");
            String asyncLocalPart =input.getString("ASYNCLOCALPART");//方法
            String wsUser=input.getString("WSUSER");
            String wsPass=input.getString("WSPASS");
            
            String strType=input.getString("TYPE");
            String strMobilePhone=input.getString("MOBILEPHONE");
            call.setTargetEndpointAddress(url);
            // 调用的方法名
            call.setOperationName(new QName(bipNamespace, asyncLocalPart));
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(bipNamespace+asyncLocalPart);
            
            call.addParameter(new QName(bipNamespace, "wsUser"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(bipNamespace, "wsPass"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(bipNamespace, "strType"), XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter(new QName(bipNamespace, "strMobilePhone"), XMLType.XSD_STRING, ParameterMode.IN);
            // 设置返回类型
            call.setReturnType(XMLType.XSD_STRING);
            // 设置超时时间
            call.setTimeout(120*1000);
            // 税控返回xml文本
            String responseXml = (String) call.invoke(new Object[] { wsUser,wsPass ,strType,strMobilePhone});
            Document doc2 = DocumentHelper.parseText(responseXml);
            //获取文档的根节点
            Element rootElement2 = doc2.getRootElement();
            
            IData resultData = Element2Data(rootElement2);
            String resultState=resultData.getString("STATE");
            if("SUCCESS".equals(resultState)){
                return resultData.getDataset("DATALIST");
            }else{
            	//返回错误
            	return dataset;
            }
		} catch (Exception e) {
		    //log.info("(e);
		    throw e;
		}
    }
    
    /**
     * 解析xml文件(对格式有要求)
     * @param element
     * @return
     */
    private static IData Element2Data(Element element) throws Exception
    {
    	IData reuslt=new DataMap();
    	try {
    		//存在data集合
        	IDataset dataset=new DatasetList();
            List list = element.elements();
            //只有一条时记录
            IData dataOne = new DataMap();
            for (Iterator<Element> it = list.iterator(); it.hasNext();)
            {
                Element elm = it.next();
                String name=elm.getName().toUpperCase();
                if(name.equals("DATA")){
                	  IData d=new DataMap();
                	  List dataList= elm.elements();
                	 for (Iterator<Element> k = dataList.iterator(); k.hasNext();){
                		 	Element elmT = k.next();
                		 	d.put(elmT.getName().toUpperCase(), elmT.getText());
                	 }
                	 dataset.add(d);
                }else{
                	if("STATE".equals(elm.getName().toUpperCase())||
                			"FAILREASON".equals(elm.getName().toUpperCase())||
                			"RECORDCOUNT".equals(elm.getName().toUpperCase())){
                		//解析头
                		reuslt.put(elm.getName().toUpperCase(), elm.getText());
                	}else{
                		//记录单条信息
                		dataOne.put(elm.getName().toUpperCase(), elm.getText());
                	}
                }
            }
            
            if(IDataUtil.isNotEmpty(dataOne)){
            	dataset.add(dataOne);
            }
            
            //返回集合
            reuslt.put("DATALIST", dataset);
		} catch (Exception e) {
			//log.info("(e);
		    throw e;
		}
        return reuslt;
    }
}
