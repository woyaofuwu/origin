$(document).ready(function(){
	if($("#isReadOnly").val()=="false")
	{
		$("#modfiyCustcontact").css("display",'');
	}
	
});


/*客户接触查询时,选择不同的查询方法*/
function custcontquerymodchg(){
	var queryType=$("#CUSTCONTACT_QUERY_TYPE").val();
	var sn=$("#SERIAL_NUMBER");
	var ccid=$("#CUST_CONTACT_ID");
	if(queryType==0)
	{
		$("#custid").css('display','none');
		$("#sn").css('display','');
		$("#userstate").css('display','');
	/*	if($("#custinfo")!=null){
			$("#custinfo").css('display','');
		}*/
		$("#timehorizon").css('display','');
		sn.attr('nullable',"no");
		ccid.attr("nullable","yes");
		//切换选择时,重新显示日期,并设置不可用
		$("#CUSTCONTACT_TIME_HORIZON").attr("disabled",false);
		if($("#CUSTCONTACT_TIME_HORIZON").val()==0)
		{
			custcontquerytimechg();
		}
	}
	else if(queryType==1)
	{
		
		$("#custid").css('display','');
		$("#sn").css('display','none');
		$("#userstate").css('display','none');
		$("#timehorizon").css('display','none');
		$("#sdate").css('display','none');
		$("#edate").css('display','none');
		
		sn.attr('nullable', 'yes');
		ccid.attr('nullable', 'no');
		$("#START_DATE").val("");
		$("#END_DATE").val("");
		$("#CUSTCONTACT_TIME_HORIZON").attr("disabled",false);
		$("#START_DATE").attr("disabled",true);
		$("#END_DATE").attr("disabled",true);
		
		
	}
}
/*客户接触查询,是否选择时间查询方式*/
function custcontquerytimechg(){
	var queryType=$("#CUSTCONTACT_TIME_HORIZON");
	/*if(queryType.attr("disabled",true))//若已不可用,则直接返回,用于客户接触标识查询
	{
		return false;
	}*/
	if(queryType.val()==0)
	{
		$("#sdate").css('display','');
		$("#edate").css('display','');
		$("#START_DATE").attr("disabled",false);
		$("#END_DATE").attr("disabled",false);
	}
	else if(queryType.val()==1)
	{
		$("#sdate").css('display','none');
		$("#edate").css('display','none');
		$("#START_DATE").attr("disabled",true);
		$("#END_DATE").attr("disabled",true);
		$("#START_DATE").val("");
		$("#END_DATE").val("");
	}
		
}

//表格点击事件
function custContactTableClick(){
	
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#CUST_CONTACT_ID2").val(rowData.get("col_CUST_CONTACT_ID"));
	$("#ACCEPT_MONTH").val(rowData.get("col_ACCEPT_MONTH"));
	$("#REMARK").val(rowData.get("col_REMARK"));
}

//接触查询
function queryIntegrateCustContact(){
	
	var custcontact_time_horizon = $("#CUSTCONTACT_TIME_HORIZON").val();//查询范围   0 时间   1 所有
	var custcontact_query_type = $("#CUSTCONTACT_QUERY_TYPE").val();//查询方式   0 为服务号码  1 客户接触标识
	var cust_contact_id = $("#CUST_CONTACT_ID").val(); // 客户接触标识
	var serial_number = $("#SERIAL_NUMBER").val();
	var start_date = $("#START_DATE").val();
	var end_date = $("#END_DATE").val();
	
	
	
	if(custcontact_query_type == 1 && cust_contact_id == ""){
		alert("客户接触标识不能为空!");
		return false;
	}
	if(custcontact_query_type == 0 && serial_number == ""){
		alert("服务号码不能为空!");
		return false;
	}
	if(custcontact_query_type == 0 && custcontact_time_horizon == 0){//按时间查询
		 if(start_date == "" || end_date==""){
		 	alert("时间不能为空！");
		    return false;
		 }
		 if(start_date > end_date){
		 	alert("开始时间不能大于结束时间！");
		    return false;
		 }
	}
	$.beginPageLoading("数据查询中..");
	//查询条件校验
//	if(!$.validate.verifyAll("QueryCondPart")) {
//		return false;
//	}
	//查询
	$.ajax.submit('QueryIntegrateCustContactCondPart,custContactnav', 'queryIntegrateCustContact', null, 'QueryIntegrateCustContactListPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//点击表格链接
function redirectTheUrl(obj) {
	
	var ccid = obj.getAttribute("ccid");
	var inmodecode =obj.getAttribute("in_mode_code");
	var param='&CUST_CONTACT_ID=' + ccid ;
    openNav('客户接触','sundryquery.custcontact.CustContactSub', 'queryCustContactSub', param);
}

/*修改客户接触*/
function modifycustcontact(){
	
	
	var deptTable=$.table.get("DeptTable").getTableData(null,true);
	if(deptTable.length<1)
	{
		alert("表格没有数据 ,不能修改！");
		return false;
	}
	
	if( $("#CUST_CONTACT_ID2").val() == ''){
		
		alert('请点击你要修改的行！');
		return false;
	}
	if($("#REMARK").val()=='')
	{
		alert('接触信息明细不能为空！');
		return false;
	}
	if($("#MODIFY_DESC").val()=='')
	{
		alert('必须输入修改原因描述!');
		return false;
	}
	
	//'QueryIntegrateCustContactCondPart,QueryIntegrateCustContactListPart' 区域
	$.beginPageLoading("数据处理中..");
	$.ajax.submit('QueryIntegrateCustContactCondPart', 'modifyIntegrateCustContact', 
   '&ACCEPT_MONTH='+$("#ACCEPT_MONTH").val()+'&CUST_CONTACT_ID2='+$("#CUST_CONTACT_ID2").val()
	+'&REMARK='+$("#REMARK").val()+'&MODIFY_DESC='+$("#MODIFY_DESC").val()
	/*+'&lastcond='+$("#LAST_COND").val()+'&pvstr='+$("#pvstr").val() 
	+'&SERIAL_NUMBER='+$("#SERIAL_NUMBER").val()*/,
	null, function(data){
			MessageBox.success("系统提示：",data.get([0],"TIP"),dealMsgBox);
			$.endPageLoading();
			
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}



//处理完之后刷新界面
function dealMsgBox(){
	
	openNavByUrl('客户接触综合查询','/order/order?service=page/sundryquery.custcontact.QueryIntegrateCustContact&listener=qryInit');
	//openNav('客户接触综合查询','sundryquery.custcontact.QueryIntegrateCustContact', 'onInitTrade');
}

