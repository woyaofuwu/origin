
package com.asiainfo.veris.crm.order.soa.group.demo.bboss;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.biz.process.BizProcess;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;

public class TestBbossXmlInfo extends BizProcess
{
    private static final Logger logger = Logger.getLogger(TestBbossXmlInfo.class);

    private static final long serialVersionUID = 1L;


    /**
     * 运行输入[文件路径 + 文件名] 右键 Run As——>Open Run Dialog
     * 
     * @param args
     * @throws Exception
     */
    // 

    public static void main(String[] args) throws Exception
    {
    	IDataInput input = new DataInput();
    	IData headerData = loadDataFromFile("F:/ServiceFile/HEADER.txt", false);
    	input.getHead().putAll(headerData);
		input.getData().put("SEQ_ID", "2017033040561");
		String svcName = "CS.BbossXmlMainInfoQrySVC.qryXmlContentInfoBySeqId";
		String addr = GlobalCfg.getProperty("service.router.addr");
		IDataOutput iDataOutput = ServiceFactory.call(addr, svcName, input, null, false, true);
		IDataset delayXmlContentInfoList = iDataOutput.getData();
		if(delayXmlContentInfoList==null || delayXmlContentInfoList.size()==0){
			return;
		}
		IData delayXmlContentInfo = delayXmlContentInfoList.getData(0);
		StringBuilder delayXmlContent = new StringBuilder();
		for(int j=1;j<11;j++){
			String xmlContent = delayXmlContentInfo.getString("XML_CONTENT_"+j);
			if(StringUtils.isNotBlank(xmlContent)){
				delayXmlContent.append(xmlContent);
			}
		}
		Map delayXmlMap = Wade3DataTran.strToMap(delayXmlContent.toString());
		IData svcData = Wade3DataTran.wade3To4DataMap(delayXmlMap);
		//svcName = "CS.BbossPayBizSVC.bbossPayBizOrderOpenChkMeb";
		svcName = "CS.SynMebWordOrderSVC.dealMebWordOrder";
		input.getHead().put("STAFF_EPARCHY_CODE", svcData.getString("TRADE_EPARCHY_CODE",""));
		input.getHead().put("IN_MODE_CODE", "6");
		input.getHead().put("SEQ_ID", "2017033040561");
		svcData.put("SEQ_ID", "2017033040561");
		//svcData.put("PRODUCTID", svcData.getString("PRODUCT_ID",""));
		input.getData().putAll(svcData);
		 IDataOutput dataset = ServiceFactory.call(addr,svcName, input, null, false, true);	

    }

    public static IData loadDataFromFile(String filePath, boolean needMapTrans) throws Exception
    {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        StringBuilder buffer = new StringBuilder(100000);
        byte[] bytes = new byte[1024];
        int fileLength = 0;

        while ((fileLength = fis.read(bytes)) != -1)
        {
            buffer.append(new String(bytes, 0, fileLength));
        }
        if (needMapTrans)
        {
            String data = new String(buffer);
            Map map = Wade3DataTran.strToMap(data.trim());
            
            return Wade3DataTran.wade3To4DataMap(map);
        }
        else 
        {
            return new DataMap(buffer.toString().trim());
        }
    }



}
