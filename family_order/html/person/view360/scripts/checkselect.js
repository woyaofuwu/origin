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

function checkSelectAllPlatSvc(obj) {
	var selectTagValue = "0";	
  
	var selectTag = $("#SelTag")[0].checked;

	if(selectTag) {
		selectTagValue = "1";	//1-展示全部
	}
	
	$("#SelectTag").val(selectTagValue);
	
	$.ajax.submit('QueryCondPart', 'queryPlatServInfo', null , 'QueryListPart', function(data){
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

function queryIMSInfo(data){
	if(!$.validate.verifyAll("ConditionPart")) {
		$("#IMS_NUMBER").text("");
		$("#BRAND_CODE").text("");
		$("#PRODUCT_NAME").text("");
		return false;
	}
	$.ajax.submit('ConditionPart', 'queryIMSInfo', null , 'IMSInfoPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		$("#IMS_NUMBER").text("");
		$("#BRAND_CODE").text("");
		$("#PRODUCT_NAME").text("");
		alert(error_info);
    });
}

function setIMSInfo() {
	if(!infoIsNull()){
		$("#IMS_NUMBER").text("");
		$("#BRAND_CODE").text("");
		$("#PRODUCT_NAME").text("");
		return false;
	}
	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val()
	+'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
 	$.ajax.submit(null, 'setCommInfo', param, 'ConditionPart', function(data){
 		queryIMSInfo(data);
 	},
	function(error_code,error_info){
		$.endPageLoading();
		$("#IMS_NUMBER").text("");
		$("#BRAND_CODE").text("");
		$("#PRODUCT_NAME").text("");
		alert(error_info);
    });
 }