/*$Id:$*/
function checkSelectAll(obj) {
	var selectTagValue = "0";	

	var selectTag = $("#SelTag")[0].checked;
	//alert(selectTag);
	if(selectTag) {
		selectTagValue = "1";	//1-展示全部
	}
	
	$("#SelectTag").val(selectTagValue);
	
	$.ajax.submit('QueryCondPart', 'queryInfo', null , 'QueryListPart', function(data){
		//navbar()
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function navbar(){
		var selectTag = $("#SelTag")[0].checked;
	//alert(selectTag);
	if(selectTag) {
		$('#NavBar').css("display","none");
	}else{
		$('#NavBar').css("display","block");
	}
}
function query() {	
	$.ajax.submit('QueryCondPart,QueryCondPart1', 'queryInfo', null , 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}