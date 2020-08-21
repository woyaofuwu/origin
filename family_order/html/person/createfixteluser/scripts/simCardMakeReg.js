function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'BilssCardListPart,BilssCardOperPart,hiddenPart', function(){
		
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
function queryServiceBack(obj) {

	if( obj == null )
		return;
		
	var table1 = document.getElementById('DeptTable');//table1');
	
	var rsrv_str1 = table1.rows[obj].cells[1].innerHTML;  
	var rsrv_str2 = table1.rows[obj].cells[2].innerHTML; 
	var rsrv_str3 = table1.rows[obj].cells[3].innerHTML;	 
	var rsrv_str4 = table1.rows[obj].cells[4].innerHTML; 
	var rsrv_str10 = table1.rows[obj].cells[7].innerHTML; 
	var rsrv_str0 = table1.rows[obj].cells[6].innerHTML; 
	var rsrv_str8 = table1.rows[obj].cells[8].innerHTML; 
	
	$("#comp_WorkID").val(rsrv_str0); 
	$("#comp_SimCardNo").val(rsrv_str1);
	$("#comp_CardNum").val(rsrv_str2);	
	$("#comp_MakeName").val( rsrv_str3);		
	$("#comp_Tel").val(rsrv_str4);	
	
	$("#REMARK").val(rsrv_str10);
	$("#cond_DEAL_TAG").val(rsrv_str8);

	return;
}

// 将TABLE2中数据拼串
function coding(objTable) {
	var xCodingStr = "";
	var idata = null;
	var dataset = new Wade.DatasetList();
	for(var i = 1; i < objTable.rows.length; i++) {
		idata = new Wade.DataMap();
		idata.put("RSRV_STR1", objTable.rows[i].cells[6].innerHTML);
		idata.put("RSRV_STR2", objTable.rows[i].cells[1].innerHTML);
		idata.put("RSRV_STR3", objTable.rows[i].cells[2].innerHTML);
		idata.put("RSRV_STR4", objTable.rows[i].cells[3].innerHTML);
		idata.put("RSRV_STR5", objTable.rows[i].cells[4].innerHTML);
		idata.put("START_DATE", objTable.rows[i].cells[5].innerHTML); 
		idata.put("RSRV_STR10", objTable.rows[i].cells[7].innerHTML);
		idata.put("DEAL_TAG", objTable.rows[i].cells[8].innerHTML);
		
		idata.put("INST_ID", objTable.rows[i].cells[9].innerHTML);
		
		dataset.add(idata);
	}
	idata = null;
	xCodingStr = dataset.toString();
	dataset = null;
	return xCodingStr;
}
// 添加SIM
function AddSimCard(objTable)
{	
	if( objTable == null )
		return;
	/*	
	if(objTable.rows.length < 2) {
		alert("没有可以销售的卡！");
		return false;
	}
	*/	
	
	if( $("#comp_WorkID").val() == "" )
	{
		alert( "请输入工单编号!" );
		$("#comp_WorkID").focus();
		return;
		
	}
	
	if( $("#comp_CardNum").val() == "" )
	{
		alert( "请输入制卡数量!" );
		$("#comp_CardNum").focus();
		return;
		
	}
	
	if( $("#comp_SimCardNo").val() == "" )
	{
		alert( "请输入SIM卡号!" );
		$("#comp_SimCardNo").focus();
		return;
		
	}
	
	
	document.getElementById("X_CODING_STR").value =  coding(objTable);
	
	var pp = '&cond_X_CODING_STR=' + document.getElementById("X_CODING_STR").value ;
	$.beginPageLoading();
	//ajaxSubmit4CS(this, 'AddSimCard', '&cond_X_CODING_STR' , 'BasicInfosPart', null, null, OnAddSimCard );
  $.ajax.submit('BilssCardOperPart', 'AddSimCard', pp ,'BilssCardListPart', OnAddSimCard ,function(error_code,error_info){
		$.endPageLoading();
		alert(error_info); 
    });
	return;
	
}

// AddSimCard的回调函数
function OnAddSimCard()
{
	var objTable = document.getElementById('DeptTable');
	
	for(var i = 1; i < objTable.rows.length; i++) 
	{	
		if( "1" == objTable.rows[i].cells[8].innerHTML || "8" == objTable.rows[i].cells[8].innerHTML  )
		{
			objTable.rows[i].style.display = 'none';
		}
		

	}
	$.endPageLoading();
}


// 修改SIM
function ModifySimCard(objTable)
{	  
	if( objTable == null )
		return;
		
	if(objTable.rows.length < 2) {
		alert("没有可以SIM卡信息！");
		return false;
	}	 
	
	if( $("#comp_WorkID").val() == "" )
	{
		alert( "请输入工单编号!" );
		$("#comp_WorkID").focus();
		return;
		
	} 
	
	if( $("#comp_CardNum").val() == "" )
	{
		alert( "请输入制卡数量!" );
		$("#comp_CardNum").focus();
		return;
		
	} 
	
	if( $("#comp_SimCardNo").val() == "" )
	{
		alert( "请输入SIM卡号!" );
		$("#comp_SimCardNo").focus();
		return;
		
	} 
	//alert($("#comp_WorkID"));
	//alert($("#OLD_WORKID") );
	if( $("#comp_WorkID").val() != $("#OLD_WORKID").val() )
	{
	    //去掉以后, 没有影响, 解决了提示报错的问题, 就是说 OLD_WORKID已经没有用了, 搜索也没有得到对这个值进行设置的地方
		//alert( "当前操作记录与所选记录不一致， 请重新选择!" );
		//getElement("comp_WorkID").focus();
		// today test return;
	}
 
	var qryTag = document.getElementById("QRY_TAG");
	
	if( $("#ond_DEAL_TAG").val() == "9" && qryTag.value == "0" )
	{
		alert( "你没有修改SIM卡免费制卡登记业务的权限!" );
	}
  
	for(var i = 1; i < objTable.rows.length; i++) 
	{ /*alert($("#comp_WorkID").val());
		alert('0,'+objTable.rows[i].cells[0].innerHTML);//6
		alert('1,'+objTable.rows[i].cells[1].innerHTML);
		alert('2,'+objTable.rows[i].cells[2].innerHTML);
		alert('3,'+objTable.rows[i].cells[3].innerHTML);
		alert('4,'+objTable.rows[i].cells[4].innerHTML);
		alert('5,'+objTable.rows[i].cells[5].innerHTML);
		alert('6,'+objTable.rows[i].cells[6].innerHTML);
		alert('7,'+objTable.rows[i].cells[7].innerHTML);
		alert('8,'+objTable.rows[i].cells[8].innerHTML);*/
		if( $("#comp_WorkID").val() == objTable.rows[i].cells[6].innerHTML )//6
		{ 
			//objTable.rows[i].cells[6].innerHTML = "1";
			objTable.rows[i].cells[1].innerHTML = $("#comp_SimCardNo").val()  ;
			objTable.rows[i].cells[2].innerHTML = $("#comp_CardNum").val() ;
			objTable.rows[i].cells[3].innerHTML = $("#comp_MakeName").val() ;
			objTable.rows[i].cells[4].innerHTML =  $("#comp_Tel").val();
			//objTable.rows[i].cells[5].innerHTML = "6";
			
			if( objTable.rows[i].cells[8].innerHTML != "0" && objTable.rows[i].cells[8].innerHTML != "8" )
			{
				objTable.rows[i].cells[7].innerHTML = "已修改";
				$("#REMARK").val("已修改");
				objTable.rows[i].cells[8].innerHTML = "2";
			}
		}
		

	}
	
	return;
	
}

// ModifySimCard的回调函数
function OnModifySimCard()
{
}


// 删除SIM
function DelSimCard(objTable)
{	
	if( objTable == null )
		return;
		
	if(objTable.rows.length < 2) {
		alert("没有可以SIM卡信息！");
		return false;
	}	
	
	if( $("#comp_WorkID").val() == "" )
	{
		alert( "请输入工单编号!" );
		$("#comp_WorkID").focus();
		return;
		
	}	

	if( $("#comp_WorkID").val() != $("#OLD_WORKID").val() )
	{
	    //去掉以后, 没有影响, 解决了提示报错的问题, 就是说 OLD_WORKID已经没有用了, 搜索也没有得到对这个值进行设置的地方
		//alert( "当前操作记录与所选记录不一致， 请重新选择!" );
		//getElement("comp_WorkID").focus();
		//today test return;
	}
	
	for(var i = 1; i < objTable.rows.length; i++) 
	{
		if( $("#comp_WorkID").val() == objTable.rows[i].cells[6].innerHTML)
		{
		
			if(  objTable.rows[i].cells[8].innerHTML != "0"  )
			{			
				objTable.rows[i].cells[7].innerHTML = "已删除";
				$("#REMARK").value = "已删除";
				objTable.rows[i].cells[8].innerHTML = "1";
			}
			else
			{
				objTable.rows[i].cells[7].innerHTML = "已删除";
				$("#REMARK").value = "已删除";
				objTable.rows[i].cells[8].innerHTML = "8";
			}
			// .style.display = '' 可见
			// .style.display = 'none' 不可见
			
			objTable.rows[i].style.display = 'none';
		}
	}
	
	
	return;
	
}

// DelSimCard的回调函数
function OnDelSimCard()
{
}


function tableRowClick() {
	//获取选择行的数据
	var rowData = $.table.get("DeptTable").getRowData();
	$("#BLISS_CARD_NO").val(rowData.get("BLISS_CARD_NO"));
	$("#GET_TAG").val(rowData.get("GET_TAG"));
	$("#PAY_FEE").val(rowData.get("PAY_FEE"));
}

 
// 删除SIM
function delBlissCard()
{	
	
    var rowData = $.table.get("DeptTable").getRowData();
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	$.table.get("DeptTable").deleteRow();
	
	return;
}

function clickOK( objTable ) 
{	
	if( objTable == null )
		return;
		
	if(objTable.rows.length < 2) {
		alert("没有可以登记的SIM卡信息！");
		return false;
	}	
	
	document.getElementById("X_CODING_STR").value =  coding(objTable);
	

	return true;
}

// 将TABLE2中数据拼串
function codi_ng(objTable) {
	var xCodingStr = "";
	var idata = null;
	var dataset = new Wade.DatasetList();
	for(var i = 1; i < objTable.rows.length; i++) {
		idata = new Wade.DataMap();
		idata.put("RSRV_STR1", objTable.rows[i].cells[6].innerHTML);
		idata.put("RSRV_STR2", objTable.rows[i].cells[1].innerHTML);
		idata.put("RSRV_STR3", objTable.rows[i].cells[2].innerHTML);
		idata.put("RSRV_STR4", objTable.rows[i].cells[3].innerHTML);
		idata.put("RSRV_STR5", objTable.rows[i].cells[4].innerHTML);
		idata.put("START_DATE", objTable.rows[i].cells[5].innerHTML);
		idata.put("RSRV_STR10", objTable.rows[i].cells[7].innerHTML);
		idata.put("DEAL_TAG", objTable.rows[i].cells[8].innerHTML);
		
		dataset.add(idata);
	}
	idata = null;
	xCodingStr = dataset.toString();
	dataset = null;
	return xCodingStr;
}


//--------------------------------------
function setValues(){
	
	var deptTable=$.table.get("DeptTable").getTableData(null,true);
	$("#X_CODING_STR").val(deptTable);
	return true;
}

//截取空格
String.prototype.trim = function()
{
	var reExtraSpace = /^\s*(.*?)\s+$/;
	return this.replace(reExtraSpace,"$1");
}

String.prototype.strlen = function()
{ 
 var cArr = this.match(/[^x00-xff]/ig); 
 return this.length + (cArr == null ? 0 : cArr.length);
}

