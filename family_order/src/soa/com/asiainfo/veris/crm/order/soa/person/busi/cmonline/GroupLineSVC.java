package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import org.apache.commons.lang.StringUtils;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.DatalineOrderDAO;

public class GroupLineSVC extends CSBizService{

	//集团专线列表信息查询接口
	public IData groupLineQry(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String groupId = IDataUtil.chkParam(data, "groupId");
		IDataset results = new DatasetList();
		
		IData bizInfo = new DataMap();
		IDataset bizInfos = new DatasetList();

		IData rtn = new DataMap();
		
		//先查询集团客户信息
		IData groupInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
		if(IDataUtil.isEmpty(groupInfo)){
			rtn = prepareOutResultList(1, "该集团客户资料为空", results);
			return rtn;
		}
		String custName = groupInfo.getString("CUST_NAME", "");
		String custId = groupInfo.getString("CUST_ID");
		//再查询集团用户信息
		IDataset userInfos = UserInfoQry.getAllNormalUserInfoByCustId(custId);
		if(IDataUtil.isNotEmpty(userInfos)){
			for(int i=0;i<userInfos.size();i++){
				IData userInfo = userInfos.getData(i);
				IData dataLine = DatalineOrderDAO.queryDataline(userInfo.getString("USER_ID"));
				if(IDataUtil.isNotEmpty(dataLine)){
					IData lineInfo = new DataMap();
					lineInfo.put("custName", custName);
					lineInfo.put("custNo", custId);
					String sheetType = dataLine.getString("SHEET_TYPE", "");
					if(sheetType.equals("4")){
						lineInfo.put("bizType", "数据专线");
					}else if(sheetType.equals("6")){
						lineInfo.put("bizType", "互联网专线");
					}else if(sheetType.equals("7")){
						lineInfo.put("bizType", "语音专线");
					}else{
						//GPRS专线 短信/彩信 这两种情形在数据库里dataline表暂时没找到 不方便判断

					}
					
					lineInfo.put("bizId", dataLine.getString("PRODUCT_NO",""));
					lineInfo.put("bizOpenDate", SysDateMgr.decodeTimestamp(dataLine.getString("START_DATE"),SysDateMgr.PATTERN_STAND_YYYYMMDD));
					lineInfo.put("remark", dataLine.getString("REMARK", ""));
					
					String endDate = dataLine.getString("END_DATE", "");
					int valid = SysDateMgr.compareTo(endDate,SysDateMgr.getSysDate());
					if(valid>=0){
						lineInfo.put("isMature","false");//未到期
					}else{
						lineInfo.put("isMature","true");//到期
					}
					
					lineInfo.put("isArrearage","false");//是否欠费
					IDataset userproductInfos = UserProductInfoQry.getUserAllProducts(userInfo.getString("USER_ID"));
					if(IDataUtil.isNotEmpty(userproductInfos)){
						String productId = userproductInfos.first().getString("PRODUCT_ID");
						IDataset paramDs = ParamInfoQry.getCommparaByCode("CSM", "1138", productId, "0898");
				        if(IDataUtil.isNotEmpty(paramDs)){
				        		String rsrvStr10 = userInfo.getString("RSRV_STR10", "");
				                if("CRDIT_STOP".equals(rsrvStr10)){
				                	lineInfo.put("isArrearage","true");//集团产品欠费
				                }
				        }
					}
					
					
					lineInfo.put("isBigFailure","fales");//是否大面积故障这个值不知道从哪里取，先填个否吧
					
					if(sheetType.equals("4")){//需求里写了，只有数据专线才返回这两个值
					lineInfo.put("bizStartDetailAddr", dataLine.getString("CITY_A","")+dataLine.getString("AREA_A","")+dataLine.getString("COUNTY_A","")+dataLine.getString("VILLAGE_A",""));
					lineInfo.put("bizEndDetailAddr", dataLine.getString("CITY_Z","")+dataLine.getString("AREA_Z","")+dataLine.getString("COUNTY_Z","")+dataLine.getString("VILLAGE_Z",""));
					}
					
					bizInfos.add(lineInfo);
				}
			}
			
			bizInfo.put("bizInfo", bizInfos);
			results.add(bizInfo);
			
			rtn = prepareOutResultList(0, "", results);
			return rtn;
		}

		rtn = prepareOutResultList(1, "该集团用户资料为空", results);
		return rtn;
		
	}
	
	//集团专线详细信息查询接口
	public IData groupLineDetailQry(IData datas) throws Exception{
		IData data = new DataMap(datas.toString()).getData("params");
		String bizType = IDataUtil.chkParam(data, "bizType");
		String bizId = IDataUtil.chkParam(data, "bizId");
		
		IData rtn = new DataMap();
		
		IDataset dataset = new DatasetList();
		IData results = new DataMap();
		
		String sheetType = "";
		String lineName = "";
		//先对数据类型做出转换
		//因为测试说 专线详细取的是专线查询返回结果的bizType返回字段 是中文不是数字 要做一轮转换
		if(bizType.equals("数据专线")){//数据专线 1
			sheetType="4";
			lineName="数据专线";
		}else if(bizType.equals("语音专线")){//语音专线 2
			sheetType="7";
			lineName="语音专线";
		}else if(bizType.equals("互联网专线")){//互联网专线 3
			sheetType="6";
			lineName="互联网专线";
		}else{
			//GPRS专线 短信/彩信 这两种情形在数据库里dataline表暂时没找到 不方便进行转换 直接返回空
			rtn = prepareOutResultList(1,"没有对应数据类型的客户",dataset);
			return rtn;
		}

		
		IData dataLine = DatalineOrderDAO.queryDataDetailline(sheetType, bizId);
		if(IDataUtil.isNotEmpty(dataLine)){
			String userId = dataLine.getString("USER_ID");
			IData grpInfo = UcaInfoQry.qryGrpInfoByUserId(userId);
			if(IDataUtil.isEmpty(grpInfo)){
				rtn = prepareOutResultMap(1,"没有对应的集团",results);
				return rtn;//没查询出集团信息直接返回空
			}
			if(StringUtils.isNotBlank(data.getString("groupId",""))){
				String groupId = data.getString("groupId");
			  if(!(grpInfo.getString("GROUP_ID").equals(groupId))) {
			  	  rtn = prepareOutResultList(1,"没有对应的集团",dataset);
			  	  return rtn;//没查询出集团信息直接返回空
			  }
			}
				
			//results.put("custName", grpInfo.getString("CUST_NAME"));
			//新需求要求CUST_NAME字段塞专线名称
			results.put("custName", lineName);
			results.put("bizId", bizId);
			IDataset lineProps = new DatasetList();
			
			//先处理语音专线,其他专线说明不清晰
			if(sheetType.equals("7")){
				
				IData relayCnt = new DataMap();
				relayCnt.put("propCode", "relayCnt");
				relayCnt.put("propName", "中继数");
				relayCnt.put("propValue", dataLine.getString("AMOUNT"));
				lineProps.add(relayCnt);
				
				IData accessMode = new DataMap();
				accessMode.put("propCode", "accessMode");
				accessMode.put("propName", "传输接入方式");
				accessMode.put("propValue", dataLine.getString("SUPPORT_MODE"));
				lineProps.add(accessMode);
				
				IData bizEndDetailAddr = new DataMap();
				bizEndDetailAddr.put("propCode", "bizEndDetailAddr");
				bizEndDetailAddr.put("propName", "客户业务接入地址");
				bizEndDetailAddr.put("propValue", dataLine.getString("CITY_Z","")+dataLine.getString("AREA_Z","")+dataLine.getString("COUNTY_Z","")+dataLine.getString("VILLAGE_Z",""));
				lineProps.add(bizEndDetailAddr);
				
				IData deviceName = new DataMap();
				deviceName.put("propCode", "deviceName");
				deviceName.put("propName", "传输设备名称");
				deviceName.put("propValue", "");//不知
				lineProps.add(deviceName);
				
				IData devicePort = new DataMap();
				devicePort.put("propCode", "devicePort");
				devicePort.put("propName", "传输设备端口名称");
				devicePort.put("propValue", dataLine.getString("PORT_CUSTOME_A"));
				lineProps.add(devicePort);
				
				IData bizIpAddr = new DataMap();
				bizIpAddr.put("propCode", "bizIpAddr");
				bizIpAddr.put("propName", "业务应用ip地址");
				bizIpAddr.put("propValue", "");//不知道是哪个值,头疼
				lineProps.add(bizIpAddr);
				
				IData bizWarnLvl = new DataMap();
				bizWarnLvl.put("propCode", "bizWarnLvl");
				bizWarnLvl.put("propName", "业务报障级别");
				bizWarnLvl.put("propValue", dataLine.getString("BIZ_SECURITY_LV"));
				lineProps.add(bizWarnLvl);
				
				IData bizOpenDate = new DataMap();
				bizOpenDate.put("propCode", "bizOpenDate");
				bizOpenDate.put("propName", "业务开通日期");
				bizOpenDate.put("propValue", SysDateMgr.decodeTimestamp(dataLine.getString("START_DATE"),SysDateMgr.PATTERN_STAND_YYYYMMDD));
				lineProps.add(bizOpenDate);
				
				IData remark = new DataMap();
				remark.put("propCode", "remark");
				remark.put("propName", "备注");
				remark.put("propValue", dataLine.getString("REMARK",""));
				lineProps.add(remark);
				
				IData exchangeName = new DataMap();
				exchangeName.put("propCode", "exchangeName");
				exchangeName.put("propName", "交换接入设备名称");
				exchangeName.put("propValue", "");//不知
				lineProps.add(exchangeName);
				
				IData exchangePort = new DataMap();
				exchangePort.put("propCode", "exchangePort");
				exchangePort.put("propName", "交换接入设备端口");
				exchangePort.put("propValue", dataLine.getString("PORT_CUSTOME_Z"));
				lineProps.add(exchangePort);
				
				IData bizProtect = new DataMap();
				bizProtect.put("propCode", "bizProtect");
				bizProtect.put("propName", "业务端点到城域传送网接入点的电路保护方式");
				bizProtect.put("propValue", dataLine.getString("ROUTE_MODE",""));
				lineProps.add(bizProtect);
			}
			
			IData circuitName = new DataMap();
			circuitName.put("propCode", "circuitName");
			circuitName.put("propName", "电路名称");
			circuitName.put("propValue", dataLine.getString("LINE_NAME"));
			lineProps.add(circuitName);

			IData segment = new DataMap();
			segment.put("propCode", "segment");
			segment.put("propName", "号段");
			segment.put("propValue", dataLine.getString("PHONE_LIST"));
			lineProps.add(segment);
			
			IData speed = new DataMap();
			speed.put("propCode", "speed");
			speed.put("propName", "速率");
			speed.put("propValue", dataLine.getString("BAND_WIDTH","0")+"M");
			lineProps.add(speed);
			
			results.put("lineProps", lineProps);
		}
		
		
		dataset.add(results);
		rtn = prepareOutResultList(0,"",dataset);
		return rtn;
	}
	
	public IData prepareOutResultList(int i,String rtnMsg,IDataset outData)
    {
    	IData object = new DataMap();
    	IData result = new DataMap();

    	if (i==0)//成功
    	{
        	object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            
            result.put("object", object);
    		result.put("rtnCode", "0");	
    		result.put("rtnMsg", "成功!");	
            return result;
    	}
    	else if(i==1)//失败
    	{
        	object.put("result", outData);
        	//object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
    	}
    	return null;
    }
	
	public IData prepareOutResultMap(int i,String rtnMsg,IData outData)
    {
    	IData object = new DataMap();
    	IData result = new DataMap();

    	if (i==0)//成功
    	{
        	object.put("result", outData);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            
            result.put("object", object);
    		result.put("rtnCode", "0");	
    		result.put("rtnMsg", "成功!");	
            return result;
    	}
    	else if(i==1)//失败
    	{
        	object.put("result", outData);
        	//object.put("resultRows", 0);
            object.put("respCode", "-1");
            object.put("respDesc", rtnMsg);
            
            result.put("object", object);
    		result.put("rtnCode", "-9999");	
    		result.put("rtnMsg", "失败");	
            
            return result;
    	}
    	return null;
    }
}
