$(document).ready(function(){
	initValue();
});

function initValue()
{
	$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
	$('#VALID_MEMBER_NUMBER').val("");
}

function refreshPartAtferAuth(data)
{
	$("#DELETE_MEMBER_NUMBER").val("0");
	
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, 'viceInfopart',loadInfoSuccess);
}

function loadInfoSuccess(data){
	var valideMemberNumber = data.get("VALIDE_MEBMER_NUMBER");
	if(valideMemberNumber&&valideMemberNumber!="-1"){
		$("#VALID_MEMBER_NUMBER_AREA").css("display","");
		$("#VALID_MEMBER_NUMBER").val(valideMemberNumber);
	}else{
		$("#VALID_MEMBER_NUMBER_AREA").css("display","none");
		$("#VALID_MEMBER_NUMBER").val("0")
	}
}

function onTradeSubmit()
{debugger;
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&REMARK='+$('#REMARK').val();
	var data = $.table.get("viceInfoTable").getTableData();
	param += '&MEB_LIST='+data;
	
	if(data.length == 0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	
	/*var effectNow = $('#effectNowCheckBox').attr('checked');
	if(effectNow)
	{
		param += '&EFFECT_NOW=YES';
	}
	else
	{
		param += '&EFFECT_NOW=NO';
	}*/

	$.cssubmit.addParam(param);
	
	return true;
}

function delMeb()
{
//	var table = $.table.get("viceInfoTable");
//	var arrIndex = table.getCheckedRows();
//	for(var i = 0, size = arrIndex.length; i < size; i++)
//	{
//		table.deleteRow(arrIndex[i]+1+"");
//	}
	
	var deleteMemberNumber=$("#DELETE_MEMBER_NUMBER").val();
	if(!deleteMemberNumber||deleteMemberNumber==""){
		deleteMemberNumber="0";
	}
	
	var deleteMemberNumberInt=parseInt(deleteMemberNumber);
	
	var effectNow = $('#effectNowCheckBox').attr('checked');
	$("#viceInfoTable_Body input[name=viceCheckBox]").each(function(){
	   
	   if(this.checked){
	     	this.click();
	     	
	     	//删除时修改结束时间
	        var rowIndex = this.parentNode.parentNode.rowIndex;
	        
	   	    var table = $.table.get("viceInfoTable");
	   	    var row = table.getRowByIndex(rowIndex+"");
	   	    var rowData = table.getRowData();
	   	    var data = eval('('+rowData+')');
	   	    var endDate = data.LAST_DAY_THIS_ACCT;
	   	    //alert(this.attr("END_DATE"));
	   	    var rdendDate = data.END_DATE;
	   	    var strsnB = data.SERIAL_NUMBER_B;
	   	    //alert(rdendDate);
	   	    var editEndDate = new Array();
	   	    var sysDate = (new Date()).format('yyyy-MM-dd HH:mm:ss');//
	   	    editEndDate["END_DATE"]=sysDate;
	   	    editEndDate["EFFECT_NOW"]="YES";
	   	    /*if(effectNow){
	   	    	editEndDate["END_DATE"]=sysDate;
	   	    	editEndDate["EFFECT_NOW"]="YES";
	   	    }else{
	   	    	if( rdendDate == endDate){
	   	    		//this.disabled = false;
	   	    		alert('已经删除的成员['+ strsnB +'],请勾选立即生效，再删除！');
	   	    		return;
		   	    }else{
		   	    	editEndDate["END_DATE"]=endDate;
	   	    		editEndDate["EFFECT_NOW"]="NO";
		   	    }
	   	    }*/
	     	this.disabled = true;
			table.updateRow(editEndDate);
	        table.setRowCss(row,"delete");  
	        
	        deleteMemberNumberInt++;
	   }
	});
	
	$("#DELETE_MEMBER_NUMBER").val(deleteMemberNumberInt);
}