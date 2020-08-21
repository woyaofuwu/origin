
function chgQryType() {  
	var choose = $("#cond_ENET_INFO_QUERY").val();
	var qryByEcCode = document.getElementById("QryByEcCode");
	var qryByMebSerial = document.getElementById("QryByMebSerial");
	if (choose=='0'){//按主分配编码查询
	   qryByEcCode.style.display='';
	   qryByMebSerial.style.display='none';
	   $("#cond_SERIAL_NUMBER_B").val("");
	   $("#cond_START_DATE").val("");
	   $("#cond_END_DATE").val("");
	   return ;
	}
	
	if (choose=='1'){ //按被分配编码查询
	   qryByEcCode.style.display='none';
	   qryByMebSerial.style.display='';
	   $("#cond_SERIAL_NUMBER_A").val("");
	   $("#cond_START_DATE").val("");
	   $("#cond_END_DATE").val("");
	   return;
	}
}
  
 function qryClick(){
 	
    var selType = $("#cond_ENET_INFO_QUERY").val();
    var serianNumA = $("#cond_SERIAL_NUMBER_A").val();
 	var serialNumB = $("#cond_SERIAL_NUMBER_B").val();
    //var startDate = $("#cond_START_DATE").val();
	//var endDate = $("#cond_END_DATE").val();
	   
    if(selType=='0') {
    	if(serianNumA=='') {
	    	alert('请输入主分配编码！');
	    	return false ;
    	}
    }
    if(selType=='1') {
    	if(serialNumB=='') {
	    	alert('请输入被分配号码！');
	    	return false ;
    	}
    }
        
    $.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos', '&SERIAL_NUMBER_A='+serianNumA+'&SERIAL_NUMBER_B='+serialNumB ,'groupSpecialInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }