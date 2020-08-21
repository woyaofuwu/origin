  /**
  * 选择查询类型(两种)
 */
 function chgQryType() {  
	var choose = $("#cond_ENET_INFO_QUERY").val();
	var qryByEcCode = document.getElementById("QryByEcCode");
	var qryByMebSerial = document.getElementById("QryByMebSerial");
	if (choose=='0'){//按集团查询
	   qryByEcCode.style.display='';
	   qryByMebSerial.style.display='none';
	   choose = $("#cond_SERIAL_NUMBER").val("");
	   return ;
	}
	
	if (choose=='1'){ //按手机号码查询
	   qryByEcCode.style.display='none';
	   qryByMebSerial.style.display='';
	   choose = $("#cond_GROUP_ID").val("");
	   return;
	}
 }
 
 
 function qryClick(){
  	var selType = $("#cond_ENET_INFO_QUERY").val();
 	var serianNumA = $("#cond_SERIAL_NUMBER_A").val();
 	var serialNumB = $("#cond_SERIAL_NUMBER_B").val();
    
    if(selType=='0') {
    	if(serianNumA=='') {
	    	alert('请输入集团产品编码！');
	    	return false ;
    	}
    }
    if(selType=='1') {
    	if(serialNumB=='') {
	    	alert('请输入成员号码！');
	    	return false ;
    	}
    }
    
    $.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos', '&SERIAL_NUMBER_A='+serianNumA+'&SERIAL_NUMBER_B='+serialNumB,'groupSpecialInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }