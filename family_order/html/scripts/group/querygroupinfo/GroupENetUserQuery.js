  /**
  * 选择查询类型(两种)
 */
 function chgQryType() {  
	var choose = $("#cond_ENET_INFO_QUERY").val();
	var qryByEcCode = document.getElementById("QryByEcCode");
	var qryByMebSerial = document.getElementById("QryByMebSerial");
	if (choose=='1'){//按集团查询
	   qryByEcCode.style.display='';
	   qryByMebSerial.style.display='none';
	   var choose = $("#cond_SERIAL_NUMBER").val("");
	   return ;
	}
	
	if (choose=='2'){ //按手机号码查询
	   qryByEcCode.style.display='none';
	   qryByMebSerial.style.display='';
	   var choose = $("#cond_GROUP_ID").val("");
	   return;
	}
 }
 
 
 function qryClick(){	
 	var selType = $("#cond_ENET_INFO_QUERY").val();
 	var ecCode = $("#cond_GROUP_ID").val();
 	var memSeial = $("#cond_SERIAL_NUMBER").val();
 	var startDate = $("#cond_START_DATE").val();
 	var endDate = $("#cond_END_DATE").val();
 	var state = $("#cond_ENET_INFO_QUERY_STATE").val();
    
    if(selType=='') {
    	alert('请选择查询方式！');
    	return false;
    }
    
    if(selType=='1') {
    	if(ecCode=='') {
	    	alert('请输入集团编码！');
	    	return false ;
    	}
    }
    
    if(selType=='2') {
    	if(memSeial=='') {
	    	alert('请输入成员号码！');
	    	return false;
    	}
    }
	
    $.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos','','groupNetInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
 }