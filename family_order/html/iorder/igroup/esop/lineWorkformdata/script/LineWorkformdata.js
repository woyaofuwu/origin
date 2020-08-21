$(function(){
	var  eparachyCodeType = $("#EPARCHY_CODE_TYPE").val();
	
	if("false"==eparachyCodeType){
		 $("#cond_CITY_CODE").attr('disabled','');
	}else{
		$("#cond_CITY_CODE").attr('disabled','disabled');
	}
});
function isBlank(obj){
	if(obj == undefined ||obj==null||obj==''){
		return true;
	}
	return false;
}

function qryLineInfos(obj){
	var param = '';
	if(!$.validate.verifyAll("qryInfo")){
		  return false;
	 }
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('qryInfo','qryLineWorkformdata',null,'queryPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}