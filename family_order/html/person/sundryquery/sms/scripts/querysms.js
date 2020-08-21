$(function(){
	var inModeCode = $("#cond_SERIAL_NUMBER").attr("inModeCode");
	if(inModeCode == 1 && typeof(eval(window.top.getCustorInfo))=="function"){
		var sn = window.top.getCustorInfo();
		$("#cond_SERIAL_NUMBER").val(sn);
	}
})

function openUipQuery(receive_time){
	var serial_number = $("#cond_SERIAL_NUMBER").val();
	
	openNav('UIP接口查询','uiop.queryReq', 'queryList', '&RECEIVE_TIME='+receive_time+'&QUERY_FROM=sms&SERIAL_NUMBER=' + serial_number);
}

//查询
function querySms()
{
	document.getElementById("QueryListPart").style.display="";
	refreshTable();
	var begin = $("#cond_START_DATE").val();
	var end = $("#cond_END_DATE").val();
	var queryMode = $("#cond_QUERY_MODE").val();
	if(begin.substring(0,7) != end.substring(0,7)){
		alert("时间范围不能跨月！");
		return false;
	}
	if(queryMode=='3'){
		if(begin.substring(0,7) == end.substring(0,7)){
			var tmp = parseInt(end.substring(8)-begin.substring(8));
			if(parseInt(tmp) >= 2){
			alert("最多只能查两天的数据！");
			return false;
			}
		}
		$("#cond_SERIAL_NUMBER").attr("nullable","yes");
	}
	//查询条件校验
	if(!$.validate.verifyAll("QuerySmsPart")) {
		return false;
	}
	$.beginPageLoading("数据查询中..");
	if(queryMode=='1'){
		$.ajax.submit('QuerySmsPart', 'querySms', null, 'RefreshTable10086', function(data){
			$.endPageLoading();
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}else{
		$.ajax.submit('QuerySmsPart', 'querySms', null, 'RefreshTable10658666', function(data){
			$.endPageLoading();
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
				$("#cond_SERIAL_NUMBER").attr("nullable","no");
			}			
			
			if( queryMode=='0' || queryMode=='3'){
				document.getElementById("id_col_queryuipbtn").style.display="";
				var table = document.getElementById("Non10086Table");
				if( null != table ){
					setDisplayCol(table,0);
				}
			}else{
				document.getElementById("id_col_queryuipbtn").style.display="none";
				var table = document.getElementById("Non10086Table");
				if( null != table ){
					setHiddenCol(table,0);
				}
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
			$("#cond_SERIAL_NUMBER").attr("nullable","no");
	    });
	}
}


function resultAreaCon(){
	var queryMode = $("#cond_QUERY_MODE").val();
	var table;
	if(queryMode=='1'){
		table = $.table.get("10086Table");
	}else{
		table = $.table.get("Non10086Table");
	}
	table.cleanRows();//清空表格的内容
	refreshTable();
}

/**
 * 控制两个FORM的显示隐藏，同一时间只能只能显示一个FORM
 */
function refreshTable(){
	var queryMode = $("#cond_QUERY_MODE").val();
	if(queryMode=='1'){
		document.getElementById("10086area").style.display="";
		document.getElementById("10658666area").style.display="none";
	}else{
		document.getElementById("10658666area").style.display="";
		document.getElementById("10086area").style.display="none";
		if( queryMode=='0' || queryMode=='3'){
			document.getElementById("id_col_queryuipbtn").style.display="";
			var table = document.getElementById("Non10086Table");
			if( null != table ){
				setDisplayCol(table,0);
			}
		}else{
			document.getElementById("id_col_queryuipbtn").style.display="none";
			var table = document.getElementById("Non10086Table");
			if( null != table ){
				setHiddenCol(table,0);
			}
		}
	}
}

function setHiddenCol(oTable,iCol)
{
 	for (i=0;i < oTable.rows.length ; i++)
	{
 		oTable.rows[i].cells[iCol].style.display = "none";
	}
}

function setDisplayCol(oTable,iCol)
{
 	for (i=0;i < oTable.rows.length ; i++)
	{
 		oTable.rows[i].cells[iCol].style.display = "";
	}
}
