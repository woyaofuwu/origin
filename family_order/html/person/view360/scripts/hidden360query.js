function showLayer(optionID) {
	$("#Complete").css("display","block");
}
function hideLayer(optionID) {
	$("#Complete").css("display","none");
}

//第二种风格
function initQueryInput(){
	if(($('#CUST_ID')!=null&&$('#CUST_ID').val()!='')){
		hideLayer('Complete');
	}
	else{
		showLayer('Complete');
	}
}