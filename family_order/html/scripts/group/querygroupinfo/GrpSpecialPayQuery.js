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
 	var gooupId = $("#cond_GROUP_ID").val();
 	var serialNum = $("#cond_SERIAL_NUMBER").val();
 	var state = $("#cond_STATE").val();
    
    if(selType=='0') {
    	if(gooupId=='') {
	    	alert('请输入集团编码！');
	    	return false ;
    	}
    }
    if(selType=='1') {
    	if(serialNum=='') {
	    	alert('请输入手机号码！');
	    	return false ;
    	}
    }
    
    $.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos', '&GROUP_ID='+gooupId+'&SERIAL_NUMBER='+serialNum+'&STATE='+state,'groupSpecialInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }