
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import com.ailk.biz.ftpmgr.FtpFileAction;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.file.FileUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.wade.container.util.log.Log;

/**
 * 浪潮文件同步接口
 * @author Administrator
 *
 */
public class TermLCSyncFileOperateSVC extends CSBizService
{
   
	private static final long serialVersionUID = 1L;


    public IDataset termFileUpload(IData input) throws Exception
    {
    	String fileCode = input.getString("FILECODE","");
    	if(StringUtils.isNotBlank(fileCode))
    	{
    		if("233".equals(fileCode))
    		{
    			/*2.3.3终端出库明细(营业侧）
    			接口名：TMSalePerson
    			说明：每天增量
    			文件名格式：TMSalePerson_2018XXXXXX.dat
    			字段顺序：
    			  IMEI|||KD_ACCOUNT|||TRADE_DEPT||| TRADE_DEPT_NAME ||| CITY_CODE ||| CITY_NAME |||TRADE_STAFF||| TRADE_STAFF_NAME |||TRADE_ID
    			*/
    			IDataset values = TradeInfoQry.qryTMSalePerson(input);
    			if(IDataUtil.isNotEmpty(values))
    			{
    				WriteTXTFile(values,"TMSalePerson_"+TimeUtil.getSysDateYYYYMMDDHHMMSS().substring(0,10),"233");
    				return values;
    			}
    			return null;
    		}else if ("237".equals(fileCode)) {
    			/*2.3.7终端换机明细（营业侧）
			                接口名：TMChangeBackPerson
				     说明：每天增量
			                文件名格式：TMChangeBackPerson_2018XXXXXX.dat
                字段顺序：
   				  OLD_IMEI ||| NEW_IMEI ||| KD_ACCOUNT ||| MOBILE_NO||| CUST_NAME||| TRADE_DEPT||| TRADE_STAFF||| DONE_TIME||| TRADE_ID
    			 */
    			IDataset values = TradeInfoQry.qryTMChangeBackPerson(input);
    			if(IDataUtil.isNotEmpty(values))
    			{
    				WriteTXTFile(values,"TMChangeBackPerson_"+TimeUtil.getSysDateYYYYMMDDHHMMSS().substring(0,10),"237");
    				return values;
    			}
    			return null;
			}else if ("238".equals(fileCode)) {
				/*2.3.8宽带新装明细（营业侧）
				接口名：WideInstall
				说明：每天增量
				文件名格式：WideInstall_2018XXXXXX.dat
				字段顺序：
				   KD_ACCOUNT ||| USER_ID ||| USER_NAME ||| TRADE_DEPT ||| TRADE_DEPT_NAME ||| TRADE_STAFF ||| TRADE_STAFF_NAME |||CITY_CODE ||| CITY_NAME ||| DONE_TIME||| TRADE_ID||| DEVICE_ID||| END_TIME||| START_TIME||| ADDRESS
				*/
				IDataset values = TradeInfoQry.qryWideInstall(input);
    			if(IDataUtil.isNotEmpty(values))
    			{
    				WriteTXTFile(values,"WideInstall_"+TimeUtil.getSysDateYYYYMMDDHHMMSS().substring(0,10),"238");
    				return values;
    			}
				return null;
			}else if ("239".equals(fileCode)) {
				/*2.3.9宽带移机明细（营业侧）
				     接口名：WideMove
			                说明：每天增量
				     文件名格式：WideMove_2018XXXXXX.dat
				     字段顺序：
   			      KD_ACCOUNT ||| TRADE_DEPT ||| TRADE_DEPT_NAME ||| TRADE_STAFF ||| TRADE_STAFF_NAME ||| CITY_CODE ||| CITY_NAME ||| DONE_TIME||| TRADE_ID||| DEVICE_ID||| END_TIME||| START_TIME||| OLD_ADDRESS||| NEW_ADDRESS||| USER_ID||| USER_NAME
				*/
				IDataset values = TradeInfoQry.qryWideMove(input);
    			if(IDataUtil.isNotEmpty(values))
    			{
    				WriteTXTFile(values,"WideMove_"+TimeUtil.getSysDateYYYYMMDDHHMMSS().substring(0,10),"239");
    				return values;
    			}
				return null;
			}else if ("2310".equals(fileCode)) {
				/*2.3.10宽带拆机明细（营业侧）
			                接口名：WideRemove
				     说明：每天增量
				     文件名格式：WideRemove_2018XXXXXX.dat
				     字段顺序：
   				  KD_ACCOUNT ||| TRADE_DEPT ||| TRADE_DEPT_NAME ||| TRADE_STAFF ||| TRADE_STAFF_NAME ||| CITY_CODE ||| CITY_NAME ||| DONE_TIME||| TRADE_ID||| DEVICE_ID||| END_TIME||| START_TIME||| USER_ID||| USER_NAME
				*/
				IDataset values = TradeInfoQry.qryWideRemove(input);
    			if(IDataUtil.isNotEmpty(values))
    			{
    				WriteTXTFile(values,"WideRemove_"+TimeUtil.getSysDateYYYYMMDDHHMMSS().substring(0,10),"2310");
    				return values;
    			}
				return null;
			}else if ("2311".equals(fileCode)) {
				/*2.3.11家宽工单竣工（营业侧）
			                接口名：WideCompleted
				     说明：每天增量
				     文件名格式：WideCompleted_2018XXXXXX.dat
				     字段顺序：
   				  KD_ACCOUNT ||| USER_ID ||| USER_NAME ||| TRADE_DEPT ||| TRADE_DEPT_NAME ||| TRADE_STAFF ||| TRADE_STAFF_NAME |||CITY_CODE ||| CITY_NAME ||| DONE_TIME||| TRADE_ID||| DEVICE_ID||| END_TIME||| START_TIME||| ADDRESS
				*/
				IDataset values = TradeInfoQry.qryWideCompleted(input);
    			if(IDataUtil.isNotEmpty(values))
    			{
    				WriteTXTFile(values,"WideCompleted_"+TimeUtil.getSysDateYYYYMMDDHHMMSS().substring(0,10),"2311");
    				return values;
    			}
				return null;
			}else if ("2312".equals(fileCode)) {
				/*2.3.12家宽工单撤单（营业侧）
			                接口名：WideCancell
				     说明：每天增量
				     文件名格式：WideCancell_2018XXXXXX.dat
				     字段顺序：
   				  KD_ACCOUNT ||| USER_ID ||| USER_NAME ||| TRADE_DEPT ||| TRADE_DEPT_NAME ||| TRADE_STAFF ||| TRADE_STAFF_NAME |||CITY_CODE ||| CITY_NAME ||| DONE_TIME||| TRADE_ID||| DEVICE_ID||| END_TIME||| START_TIME||| ADDRESS
				*/
				IDataset values = TradeInfoQry.qryWideCancell(input);
    			if(IDataUtil.isNotEmpty(values))
    			{
    				WriteTXTFile(values,"WideCancell_"+TimeUtil.getSysDateYYYYMMDDHHMMSS().substring(0,10),"2312");
    				return values;
    			}
				return null;
			}else
			{
				return null;
			}
    	}else
    	{
    		return null;
    	}
    	
    }
    
    /**
	 * 写文件并上传
	 * @param values
	 * @param fileName
	 * @throws Exception
	 */
	public static void WriteTXTFile(IDataset values, String fileName,
			String fileCoding) throws Exception {

		BufferedWriter bw = null;
		File file = null;
		StringBuilder filePath = new StringBuilder();
		filePath.append(fileName).append(".dat").toString();
		String localFile = filePath.toString();
		try {
			file = new File(localFile);
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(localFile, true), "UTF-8"));

			for (int i = 0; i < values.size(); i++) {
				StringBuilder s = new StringBuilder();
				s.append(values.getData(i).getString("FILEVALUE"));
				s.append("\r\n");
				bw.write(s.toString());
				bw.flush();
			}
			FtpFileAction ftpFileAction = new FtpFileAction();
		    ftpFileAction.setVisit(getVisit());
			IData config = ftpFileAction.getFtpConfig("TermLCSync", getVisit());
	        FileUtil fileUtil = new FileUtil(config);
	        
	        InputStream in = new FileInputStream(file);
            int fileSize = in.available();
            
	        fileUtil.uploadFile(in,localFile);
			
		} catch (Exception e) {
			Log.debug(e);
		} finally {
			if (bw != null) {
				bw.close();
				file.delete();
			}
		}

	}

}
