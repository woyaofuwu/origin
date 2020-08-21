/*$Id:$*/
 

function query() {	
if(!$.validate.verifyAll("QueryCondPart2")) {
		return false;
	}
	
	$.ajax.submit('QueryCondPart,QueryCondPart2,CustContactNav', 'queryInfo', null , 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function showMore(data){
	$.ajax.submit('QueryCondPart', 'qryContactTrac', data , 'QueryListPart2', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}