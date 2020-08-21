
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.sms;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QuerySmsQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook.WideNetBookSVC;

/**
 * 功能：短信查询 作者：GongGuang
 */
public class QuerySmsBean extends CSBizBean
{
    private static Logger log=Logger.getLogger(WideNetBookSVC.class);
    /**
     * 功能：依据前台页面的不同输入或选择条件返回查询结果
     */
    public IDataset querySms(IData inparams, Pagination page) throws Exception
    {
        String startDate = inparams.getString("START_DATE");
        String endDate = inparams.getString("END_DATE");
        String serialNumber = inparams.getString("SERIAL_NUMBER");
        String theStartMonth = startDate.substring(5, 7);
        String theEndMonth = endDate.substring(5, 7);
        IDataset resultList = new DatasetList();
        if ("1".equals(inparams.getString("QUERY_MODE")))
        { // 10086接口
            if (theStartMonth.equals(theEndMonth))
            {// 起始、结束的月份相同查一张表
                resultList = QuerySmsQry.querySms10086SameMonth(startDate, endDate, serialNumber, theStartMonth, page);
            }
            else
            {
                resultList = QuerySmsQry.querySms10086DiffMonth(startDate, endDate, serialNumber, theStartMonth, theEndMonth, page);
            }
            
            if( IDataUtil.isNotEmpty(resultList) ){
            	/*
            	 for (int i = 0; i < resultList.size(); i++) {
            		IData result = resultList.getData(i);
            		String strP1 = "次验证码 ";
            		
            		String strP3 = "中国移动。";
            		String strNC = result.getString("PARA_CODE3", "");
            		int n1 = strNC.lastIndexOf(strP1);
            		int n2 = strNC.lastIndexOf(strP3);
            		if( n1 > 0 && n2 > 0 ){
            			int n3 = n2 - n1;
            			if( n3 == 8 ){
            				String strP2 = "**";
            				String strPC3 = strNC.substring(0, n1) + strP1;
                			int n4 = n2-3;
                			int n5 = n2-2;
                			String strCode = strNC.substring(n4, n5);
                			strNC = strPC3 + strCode + strP2 + strP3;
            			}else {
            				String strP2 = "***";
            				String strPC3 = strNC.substring(0, n1) + strP1;
                			int n4 = n2-4;
                			int n5 = n2-3;
                			String strCode = strNC.substring(n4, n5);
                			strNC = strPC3 + strCode + strP2 + strP3;
            			}
            		}
            		result.put("PARA_CODE3", strNC);
				
				*/
            	
               /**
                * REQ201611220039_短信查询界面优化
                * @author zhuoyingzhi
                * 20170104
                * 短信内容替全部修改为配置方式
                */
               for (int i = 0; i < resultList.size(); i++) {
           		       IData result = resultList.getData(i);
           		       String noticeContent = result.getString("PARA_CODE3", "");
           		       String  replaceAfterStr=replaceNoticeContent(noticeContent);
           		       if("".equals(replaceAfterStr)){
           		    	  result.put("PARA_CODE3", noticeContent);
           		       }else{
           		    	  result.put("PARA_CODE3", replaceAfterStr);
           		       }
           	    }
            	
            }
            
        }
        else if ("2".equals(inparams.getString("QUERY_MODE")))
        { // 10088接口
            if (theStartMonth.equals(theEndMonth))
            {// 起始、结束的月份相同查一张表
                resultList = QuerySmsQry.querySms10088SameMonth(startDate, endDate, serialNumber, theStartMonth, page);
            }
            else
            {
                resultList = QuerySmsQry.querySms10088DiffMonth(startDate, endDate, serialNumber, theStartMonth, theEndMonth, page);
            }
        }else if("3".equals(inparams.getString("QUERY_MODE")))
        {
        	resultList = QuerySmsQry.querySms0SameMonthNew(startDate, endDate, serialNumber, theStartMonth, page);
        }
        else
        {// 0 10658666接口
            if (theStartMonth.equals(theEndMonth))
            {// 起始、结束的月份相同查一张表
                resultList = QuerySmsQry.querySms0SameMonth(startDate, endDate, serialNumber, theStartMonth, page);
            }
            else
            {
                resultList = QuerySmsQry.querySms0DiffMonth(startDate, endDate, serialNumber, theStartMonth, theEndMonth, page);
            }
            /**
             * REQ201611220039_短信查询界面优化
             * @author zhuoyingzhi
             * 20170104
             * 短信内容替全部修改为配置方式
             */
            if(IDataUtil.isNotEmpty(resultList)){
               for (int i = 0; i < resultList.size(); i++) {
           		       IData result = resultList.getData(i);
           		       String noticeContent = result.getString("PARA_CODE5", "");
           		       String  replaceAfterStr=replaceNoticeContent(noticeContent);
           		       if("".equals(replaceAfterStr)){
           		    	  result.put("PARA_CODE5", noticeContent);
           		       }else{
           		    	  result.put("PARA_CODE5", replaceAfterStr);
           		       }
           	    }
            	            	
            } 
           /*********************end*******************************/
        }
        return resultList;
    }
    
    /**
     * REQ201611220039_短信查询界面优化
     * @author zhuoyingzhi
     * <br/>
     * 按照配置中的信息替换短信内容
     * 当返回""时表示无替换
     * @param noticeContent
     * @return
     * @throws Exception
     */
    public String replaceNoticeContent(String noticeContent)throws Exception{
    	String  resulStr="";
    	try {
             IDataset commparaList= CommparaInfoQry.getCommparaAllCol("CSM", "2017", "0", "0898");
             if(IDataUtil.isNotEmpty(commparaList)){
            	   for(int i=0;i<commparaList.size();i++){
            		     IData data=commparaList.getData(i);
            		     //关键字
            		     String param_code1=data.getString("PARA_CODE1","");
            		     if(noticeContent.indexOf(param_code1) >= 0){
            		    	 //存在
                		     //*个数
                		     int param_code2=data.getInt("PARA_CODE2", 0);
                		     //要替换的*个数
                		     String  classNum="";
                		     for(int j=0;j<param_code2;j++){
                		    	 classNum = classNum+"*";
                		     }
                		    //关键字的开始位置
                		    int beginNum=noticeContent.lastIndexOf(param_code1);
                		    //关键字结束位置
                		    int endNum=(beginNum+param_code1.length());
                		    //获取要替换的信息
                		    if(noticeContent.length() >= (endNum+param_code2)){
                    		    String replaceStr=noticeContent.substring(endNum, endNum+param_code2);
                    		    //替换后的信息
                    		    resulStr=noticeContent.replaceAll(replaceStr, classNum);
                		    }
                		    break;
            		     }
            	   }
             }
    		return resulStr;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
    }
}
