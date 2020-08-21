
function init()
{

}

function queryCautionFeeList(){
	
	$.beginPageLoading("\u6570\u636e\u67e5\u8be2\u4e2d......");//数据查询中......
	$.ajax.submit("AddDelPart", "queryCautionFeeList", null, "AddDelPart,ratioPart,hiddenPart", 
		function(data){
			$.endPageLoading();
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

