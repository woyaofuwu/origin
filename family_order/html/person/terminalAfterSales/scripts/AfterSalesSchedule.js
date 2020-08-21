
/**
 * 
 * @returns {Boolean}
 */
function queryScheduleInfo(){
	
	if (!$.validate.verifyAll()){
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart', 'qryScheduleInfo', null, 'QueryDataPart', function(data){
		$.endPageLoading();
		if(data.get("DATA_COUNT")=="0"){
			alert("查询无数据！");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}