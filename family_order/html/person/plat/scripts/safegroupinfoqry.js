function getSerialNumberFromCenCallframe(){
getElement("AUTH_SERIAL_NUMBER").value=getAuthTransPhoneVlalue();
}
//获取当前页面的主叫手机号码
function getAuthTransPhoneVlalue(){
 var cenCallframe; 
 var phonevalue="" ;
  if(parent.getFrame("sidebarframe").getElement("callCenterFrame")){
  	cenCallframe=parent.getFrame("sidebarframe").getElement("callCenterFrame").contentWindow;
  	phonevalue=cenCallframe.getElement("SERVICE_PHONE").value;
  }
 if(phonevalue=="")
 {
  return "";
 }
 return phonevalue;
}
/** 
 * 输入条件后查询平安互助群组信息
 */
function queryCustInfo(obj)
{
	var group_area=document.getElementById("cond_GROUP_NAME").value;
	var target_msisdn=document.getElementById("cond_TARGET_MSISDN").value;
	var target_name=document.getElementById("cond_TARGET_NAME").value;
	
	var tradeData = document.getElementById("tradeData");
	var tradeDataMap = "";
	if (tradeData)
	{
		tradeDataMap = $.DataMap(tradeData.value);
	}
		

	$.ajax.submit('', 'loadChildInfo', '&GROUP_NAME='+group_area+'&TARGET_MSISDN='+target_msisdn+'&TARGET_NAME='+target_name+'&tradeData='+tradeDataMap, 'PlatFamilyManagePart', function(data){
		if(data.get('FLAG') == 'true')
		{			
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });    
}
 
 