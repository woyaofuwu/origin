 function chgQryType() {
 	var choose =  $("#cond_LIMITBLACK_QUERY_WAY").val();
	var qryOneType = document.getElementById("QueryTypeOne");
	var qryTwoType = document.getElementById("QueryTypeTwo");
	var qryThrType = document.getElementById("QueryTypeTHREE");
	
	if (choose=='0'){
	  qryOneType.style.display='';
	  qryTwoType.style.display='none';
	  qryThrType.style.display='none';
	  $("#cond_CUST_NAME").val("");
	  $("#cond_BANK_ACCT_NO").val("");
	  
	}else if (choose=='1'){ 
	  qryOneType.style.display='none';
	  qryTwoType.style.display='';
	  qryThrType.style.display='none';
	  $("#cond_SERIAL_NUMBER").val("");
	  $("#cond_BANK_ACCT_NO").val("");
	  
	}else if (choose=='2'){ 
	  qryOneType.style.display='none';
	  qryTwoType.style.display='none';
	  qryThrType.style.display='';
	  $("#cond_SERIAL_NUMBER").val("");
	  $("#cond_CUST_NAME").val("");
	}
 }
 
 
 function qryClick(){
 	var choose =  $("#cond_LIMITBLACK_QUERY_WAY").val();
	var serialNum = $("#cond_SERIAL_NUMBER").val();
	var custName = $("#cond_CUST_NAME").val();
	var bankNum =  $("#cond_BANK_ACCT_NO").val();
	if(serialNum=='' && custName=='' && bankNum=='')
	{
		MessageBox.alert('提示信息','输入条件不可为空!');
		return false;
	}
	$.beginPageLoading("查询中......");
    $.ajax.submit('QueryCondPart','queryInfos', '&LIMITBLACK_QUERY_WAY='+choose+'&SERIAL_NUMBER='+serialNum
    +'&CUST_NAME='+custName+'&BANK_ACCT_NO='+bankNum,'groupNetInfo,refreshHintBar', function(data){
		$.endPageLoading(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
 }
 
 function deleteRecord(){
	 var records = getCheckedValues("record");
	 if(records == null || records == "")
	 {
		 MessageBox.alert('提示信息','请选择需要删除的记录!');
		 return false;
	 }
	 
	 
	 MessageBox.confirm('提示信息',"确实需要删除记录?",function(btn){
			if(btn == "ok"){
				$.beginPageLoading("处理中......");
				 $.ajax.submit('QueryCondPart','deleteRecord', '&RECORDS='+records,'groupNetInfo,refreshHintBar', function(data){
						$.endPageLoading(); 
					},
					function(error_code,error_info,derror){
						$.endPageLoading();
						showDetailErrorInfo(error_code,error_info,derror);
				    });
			}else{
				return false;
			};
	 });
 }

//ajax 解析java中返回的错误信息
 function showDetailErrorInfo(error_code,error_info,errorDetail){
 	var err_desc = '服务调用异常';
 	if(error_code != null && error_code != ''){
 		error_info ='错误编码:'+error_code+ '<br />' +'错误信息:'+error_info;
 		
 	}
 	MessageBox.error(err_desc, err_desc, null, null, error_info, errorDetail);
 	return false;
 	
 }

 