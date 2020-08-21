
//查询修改业务部分
function queryAuthInfo()
{	
	$.beginPageLoading("校验中...");
	//var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var serialNumber = $('#AUTH_SERIAL_NUMBER').val();
	var param = '&SERIAL_NUMBER_A=' + serialNumber;

	$.ajax.submit(null, 'refreshClick', param, 'QueryCondPart',loadInfoSucc,loadInfoError);
}

function loadInfoSucc(ajaxData)
{
	$.endPageLoading();
	$("#SERIAL_NUMBER_C").attr("disabled",null);
}

function loadInfoError(code,info){
	$.endPageLoading();
	$.showErrorMessage("错误",info);
}

function checkMphone(){
	
	var serialNumberC = $("#SERIAL_NUMBER_C").val();
	if( !isTel(serialNumberC) ){
		alert("输入的手机号码不对，请重新输入！");
		return false;
	}
	$("#SERIAL_NUMBER_C").val(trim($("#SERIAL_NUMBER_C").val()));
	var serialNumberB = $("#SERIAL_NUMBER_B").val();
	if( serialNumberB == $("#SERIAL_NUMBER_C").val() ){
		alert("新号码与原匹配号码相同，请重选号码再进行修改！");
		return false;
	}else{
	
		$.ajax.submit('QueryCondPart', 'checkClick', null, null,checkSucc,checkSuccFail);
	}
}

function checkSucc(){
	alert("新号码校验通过，可用重新匹配！");
	$("#submitBTN").attr("disabled",null);
	$("#SERIAL_NUMBER_C").attr("disabled",true);
	return true;
}

function checkSuccFail(code,info){
	$.showErrorMessage("错误",info);
}

function isTel(str){
  var reg=/^([0-9]|[\-])+$/g ;
  if(str.length!=11){
    return false;
  }
  else{
    return reg.exec(str);
  }
}

function queryMphone(){
		
		$.beginPageLoading("查询中...");
		$.ajax.submit(null, 'queryClick', null, 'infoListPart',function(data){
			$.endPageLoading();
			
			var queryTag = data.get("QUERY_TAG");
			
			if( queryTag == "true"){
				$("#query").attr("disabled",true);
			}else{
				alert( data.get("RESULT_INFO") );
			}
		},function(code,info){
			$.endPageLoading();
			alert(info);
		});
}


function setSN(){
	var data = $.table.get("SnTable").getRowData("SERIAL_NUMBER");
	$("#SERIAL_NUMBER_C").val(data.get("SERIAL_NUMBER"));
}

function checkBeforeSubmit(){
	if(!$.validate.verifyAll("SERIAL_NUMBER_C")) {
		alert("请输入新号码");
		return false;
	}
	
	var makeSure = confirm("确定要提交数据吗?");
	if (!makeSure) {
		return false;
	} 
	
	var serialNumberA = $('#AUTH_SERIAL_NUMBER').val();
	var serialNumberB = $('#SERIAL_NUMBER_C').val();
	var param = '&SERIAL_NUMBER_A=' + serialNumberA;
	param += '&SERIAL_NUMBER_B=' + serialNumberB;
	
	$.beginPageLoading("提交中...");
	$.ajax.submit(null, 'alterClick', param, null,function(data){
		$.endPageLoading();
		var title= "数据提交结果!";
//		var	hint_message = "导入成功，按[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回到本页面！<br/>";
//		$.showSucMessage(title,hint_message);
		var title = "业务受理成功";
		MessageBox.success(title,title,resetUrl,null,null);
	},checkFail);
}

function checkFail(code,info){
	$.endPageLoading();
	$.showErrorMessage("错误",info);
}

function resetUrl()
{
	var href = window.location.href;
	if(href){
		if(href.lastIndexOf("#nogo") == href.length-5){
			href = href.substring(0, href.length-5);
		}
	}
	window.location.href = href;
}
//查询修改业务部分结束

//号码匹配业务部分
function importData(){
	if(!$.validate.verifyAll("dataImportPart")) {
		return false;
	}
	
	$.ajax.submit('dataImportPart', 'importClick', null, 'infoListPart', function(data){
		$.endPageLoading(); 
		afterDo(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function afterDo(ajaxDataset){
//	$("#SubmitPart").style.display="block";
	alert('ok');		
}


function checkBeforeImport(){

	/*if(!$.validate.verifyAll("infoListPart")) {
		return false;
	}*/
	var data = $.table.get("SnTable").getRowData();
	
	var data = $.table.get("SnTable").getTableData(null,true);
	
	if(data.length<=0){
		alert('数据列表为空！');
		return;
	}
	
	var makeSure = confirm("确定要导入数据吗?");
	if (!makeSure) {
		return false;
	} 
	$.beginPageLoading("导入中...");
	
	
	$.ajax.submit('dataImportPart','onTradeSubmit','&ImportList='+data,'',function(data){
//	alert(data);
		$.endPageLoading();
		var title= "数据导入结果!";
		var sucSize=data.get("sucSize");

		//var	hint_message = "导入成功"+sucSize +"条记录，按[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回到本页面！<br/>";
		var	hint_message = "成功导入"+sucSize +"条记录";
		
		MessageBox.success(title,hint_message,resetUrl,null,null);
		//$.showSucMessage(title,hint_message);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

