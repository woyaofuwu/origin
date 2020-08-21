$(function(){
	if(typeof(eval(window.top.getCustorInfo))=="function"){
		var sn = window.top.getCustorInfo();
		$("#SERIAL_NUMBER").val(sn);
		$("#QUERY_BTN").click();
	}
})

function queryInfo(){
	$.ajax.submit('QueryCondPart', 'queryInfo', null, 'OpenSvcPart,SaleInfoPart', function(data){		
		$.endPageLoading();		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function sortByBussiness(){
	var param = '&TYPE='+1;
	$.ajax.submit('QueryCondPart', 'sort', param, 'OpenSvcPart', function(data){		
		$.endPageLoading();		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function sortByMoney(){
	var param = '&TYPE='+2;
	$.ajax.submit('QueryCondPart', 'sort', param, 'OpenSvcPart', function(data){		
		$.endPageLoading();		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function sortByType(){
	var param = '&TYPE='+3;
	$.ajax.submit('QueryCondPart', 'sort', param, 'OpenSvcPart', function(data){		
		$.endPageLoading();		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function sortByTime(){
	var param = '&TYPE='+4;
	$.ajax.submit('QueryCondPart', 'sort', param, 'OpenSvcPart', function(data){		
		$.endPageLoading();		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
