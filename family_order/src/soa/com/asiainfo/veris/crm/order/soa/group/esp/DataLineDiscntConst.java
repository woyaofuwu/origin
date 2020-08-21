package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
public class DataLineDiscntConst {
    
    public final static String viopElementId = "84019249";  //VIOP优惠编码
    public final static String internetElementId = "84019250"; //互联网专线优惠编码
    public final static String datalineElementId = "84019251"; //数据专线优惠编码
	public final static String imsElementId = "84019254"; //IMS专线优惠编码
    public final static String disposableElementId = "84019252"; //专线成员减免优惠
    public final static String ONCEElementId = "84019253"; //一次性优惠生效
    public final static int CHARGEABLETIME = 25; //每月办理业务计费时间点
    public final static String SVCELEMENTID = "1"; //7012默认服务参数
    //三种专线共有
    public final static String productNO = "59701001";  //专线实例号
    public final static String bandWidth = "59701002"; //专线宽带(兆)
    public final static String productPrice = "59701003"; //月租费（元/月）
    public final static String cost = "59701004";  //一次性费用（安装调试费）（元）
    public final static String oneCost = "59701005"; //一次性通信服务费(元)
    public final static String tradeId = "59701006"; //业务标识

    
    //互联网专线特有
    public final static String ipPrice = "59701007"; //IP地址使用费
    public final static String softwarePrice = "59701008";  //软件应用服务费(元)
    public final static String netPrice = "59701009"; //技术支持服务费(元)
    
    //数据专线特有
    public final static String groupPercent = "59701010"; //集团所在市县分成比例
    public final static String aPercent = "59701011"; //A端所在市县分成比例
    public final static String zPercent = "59701012"; //Z端所在市县分成比例
    public final static String SLA = "59701013"; //SLA服务费（元/月）
	//REQ201912310017 新增集团产品订购界面和账目界面需求
	public final static String viopProductId = "7010";  //VIOP编码
	public final static String internetProductId = "7011"; //互联网专线编码
	public final static String datalineProductId = "7012"; //数据专线编码
	public final static String internet1ProductId = "70111"; //云互联（互联网）
	public final static String internet2ProductId = "70112"; //云专线（互联网)
	public final static String dataline1ProductId = "70121"; // 云互联（数据传输）
	public final static String dataline2ProductId = "70122"; //云专线（数据传输）
	//成员产品
	public final static String internetProductIdMember = "97011"; //互联网专线编码
	public final static String datalineProductIdMember = "97012"; //数据专线编码
	public final static String internet1ProductIdMember = "970111"; //云互联（互联网）
	public final static String internet2ProductIdMember = "970112"; //云专线（互联网)
	public final static String dataline1ProductIdMember = "970121"; // 云互联（数据传输）
	public final static String dataline2ProductIdMember = "970122"; //云专线（数据传输）
	
	public final static String internet1ElementId = "84019255"; //互联网专线优惠编码 云互联（互联网）
	public final static String internet2ElementId = "84019256"; //互联网专线优惠编码 云专线（互联网)
	public final static String dataline1ElementId = "84019257"; //数据专线优惠编码 云互联（数据传输）
	public final static String dataline2ElementId = "84019258"; //数据专线优惠编码 云专线（数据传输）
	/**
	 * 
	 * @param productId 产品编码	
	 * @param userIdB 专线user_id
	 * 
	 * @return
	 * @throws Exception
	 */
	public final static String getElementIdToProductId(String productId,String userIdB) throws Exception {
		String elementId=null;
		if(internetProductId.equals(productId)){
			elementId=internetElementId;
		}else if(datalineProductId.equals(productId)){
			elementId=datalineElementId;
		}else if(internet1ProductId.equals(productId)){
			elementId=internet1ElementId;
		}else if(internet2ProductId.equals(productId)){
			elementId=internet2ElementId;
		}else if(dataline1ProductId.equals(productId)){
			elementId=dataline1ElementId;
		}else if(dataline2ProductId.equals(productId)){
			elementId=dataline2ElementId;
		}
		IDataset LineTypeList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID",new String[] {
                "TYPE_ID"
            }, new String[] {
        		"LINETYPE_"+productId
            });
		IData discntParam = new DataMap();
        discntParam.put("USER_ID", userIdB);
        discntParam.put("ATTR_CODE", "LINE_TYPE");
        discntParam.put("INST_TYPE", "P");
        IDataset userAttrs = UserAttrInfoQry.getUserLineInfoByUserIdAttrCode(discntParam);
        if(IDataUtil.isNotEmpty(userAttrs)&&IDataUtil.isNotEmpty(userAttrs.getData(0))){
            IData userAttrData = userAttrs.getData(0);
            for(int j=0,sizej=LineTypeList.size();j<sizej;j++){//配置数据循环
            	IData LineTypeData = LineTypeList.getData(j);
                if(IDataUtil.isNotEmpty(LineTypeData)&&userAttrData.getString("ATTR_VALUE", "").equals(LineTypeData.getString("DATA_ID",""))){
                	elementId=LineTypeData.getString("DATA_ID","");
                }
   		 	}

        }
        if(elementId!=null){
        	return elementId;
        }else{
        	return "";
        }
			
		
	}
	/**
	 * 修改专线ElementId
	 * @param productId
	 * @param userIdB
	 * @param selectElements
	 * @throws Exception
	 */
	public final static void changeElementIdToProductId(String productId,String userIdB,IDataset selectElements)throws Exception {
		IDataset LineTypeList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID",new String[] {
                "TYPE_ID"
            }, new String[] {
        		"LINETYPE_"+productId
            });
		
		boolean lineTypeFlag=false;
		IData discntParam = new DataMap();
        discntParam.put("USER_ID", userIdB);
        discntParam.put("ATTR_CODE", "LINE_TYPE");
        discntParam.put("INST_TYPE", "P");
        IDataset userAttrs = UserAttrInfoQry.getUserLineInfoByUserIdAttrCode(discntParam);
        for(int i=0,sizei=userAttrs.size();i<sizei;i++){//attr表数据循环
            IData userAttrData = userAttrs.getData(i);
            if(IDataUtil.isNotEmpty(userAttrData)){
            	String lineType=userAttrData.getString("ATTR_VALUE","");
                for(int j=0,sizej=LineTypeList.size();j<sizej;j++){//配置数据循环
                    IData LineTypeData = LineTypeList.getData(j);
                    if(IDataUtil.isNotEmpty(LineTypeData)&&lineType.equals(LineTypeData.getString("DATA_ID",""))){
                        	String lineTypePID=LineTypeData.getString("PDATA_ID","");
                            for(int k=0,sizek=selectElements.size();k<sizek;k++){//必选优惠循环
                                IData selectElementData = selectElements.getData(k);
                                if(IDataUtil.isNotEmpty(LineTypeData)&&lineTypePID.equals(selectElementData.getString("ELEMENT_ID")))
                                {
                                	selectElementData.put("ELEMENT_ID", LineTypeData.getString("DATA_ID",""));
                                }
                            	lineTypeFlag=true;
                            }
                            if(lineTypeFlag) break;
                    	
            		}
                	
                }
                if(lineTypeFlag) break;
            }
        }
	}

}
