 
function qryClick(){
  	 
  	var grpCityCode = $("#cond_GRP_CITYCODE").val();
  	
  	if(grpCityCode == "" || grpCityCode == null)
  	{
  		alert("请选择集团归属市县!");
  		return false;
  	}
  	
    $.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos',null ,'RefreshPart,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}