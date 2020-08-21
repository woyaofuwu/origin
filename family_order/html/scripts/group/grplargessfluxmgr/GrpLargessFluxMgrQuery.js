 
 function qryClick(){
 	var grpSn = $("#cond_GRP_SERIAL_NUMBER").val();
    
    if(grpSn == '' || grpSn == null) {
    	alert('请输入查询的集团产品编码！');
    	return false ;
    }
    
    //var startDate = $("#cond_START_DATE").val();
    //var endDate = $("#cond_END_DATE").val();
    
    $.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos', '&GRP_SERIAL_NUMBER='+grpSn ,'groupSpecialInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }