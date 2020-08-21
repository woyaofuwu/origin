function init(){
	var isExist = document.getElementById("IS_EXIST");
	var isStop = document.getElementById("IS_STOP");
	var isEnd = document.getElementById("IS_END");
	var cancelCluster = document.getElementById("cond_CANCEL_CLUSTER");     //取消群组共享
	if(isExist.value == "0"){    //当用户没有建立群组共享的时候，影藏取消群组共享的复选框
		cancelCluster.disabled = "true";
		document.getElementById("cond_CANCEL_CLUSTER").checked = false;
		document.getElementById("cancelpart").style.display = "none";
	}else if(isExist.value == "1"){
		cancelCluster.disabled = "";
		document.getElementById("cond_CANCEL_CLUSTER").checked = false;
		document.getElementById("cancelpart").style.display ="";
	}
	if(isStop.value == "1"){    //主卡停机时不能操作
		
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$("#bBcommit").attr("disabled",true).addClass("e_dis");
		
		cancelCluster.disabled = "true";
		document.getElementById("cond_CANCEL_CLUSTER").checked = false;
		document.getElementById("cancelpart").style.display = "none";
		alert("该主卡处于停机状态，不能办理4G流量共享业务！");
	}
	if(isEnd.value == "1"){    //主卡已经取消群组后不能操作
		
		$("#CSSUBMIT_BUTTON").attr("disabled",true).addClass("e_dis");
		$("#bBcommit").attr("disabled",true).addClass("e_dis");
		cancelCluster.disabled = "true";
		document.getElementById("cond_CANCEL_CLUSTER").checked = false;
		document.getElementById("cancelpart").style.display = "none";
		alert("该主卡已经取消了群组，不能再操作！");
	}
}
/**
 * 设置是否取消家庭群组
 */
function cancelClusterConfirm(c){
	if(c && c.checked){
		alert("您已经选择了注销家庭共享群组！请谨慎操作");
		document.getElementById("cond_CANCEL_CLUSTER").value = "1"
		document.getElementById("clusterpart").style.display='none';
		document.getElementById("viceInfopart").style.display='none';
		return;
	}
	if(c && !c.checked){
		alert("您已经取消了注销整个家庭共享群组！");
		document.getElementById("cond_CANCEL_CLUSTER").value = "0"
		document.getElementById("viceInfopart").style.display = "";
		document.getElementById("clusterpart").style.display = "";
		return;
	}
}
function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, 'viceInfopart,discntInfopart,cancelpart',function()
	{
		init();
	},loadInfoError);
}

function refreshPartAtferAuthForcancel(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfoForcancel', param, 'viceInfopart',null,loadInfoError);
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

//新增操作
function addMeb()
{
	var mebSn = $('#FMY_SERIAL_NUMBER').val();
	var mainSn = $("#AUTH_SERIAL_NUMBER").val();
	if(mebSn == '')
	{
		alert('请先输入成员号码');
		return;
	}
	
	//是否手机号码校验
	if(!$.verifylib.checkMbphone(mebSn)){
		alert('不是手机号码，请重新输入！');
		$('#FMY_SERIAL_NUMBER').val('')
		return;
	}
	
	//成员号码不能与主号一致
	if(mebSn == mainSn){
		alert('对不起，成员号码不能和主卡号码一样，请重新输入！');
		$('#FMY_SERIAL_NUMBER').val('');
		return;
	}
	
	debugger;
	var exits = $("#viceInfoTable tbody").children("tr").length;
	if(exits>3){
	   alert('已经存在4个成员！不能再次新增！');
	   return false;
	}
	var list = $.table.get("viceInfoTable").getTableData();
	if(list.length>3){
	   alert('最多只能添加4个成员！');
	   return false;
	}
	for(var i = 0, size = list.length; i < size; i++)
	{
		var tmp = list.get(i);
		var sn = tmp.get('SERIAL_NUMBER');
		var tag = tmp.get('tag');
		if(mebSn == sn && tag != '1')
		{
			alert('号码'+mebSn+'已经在成员列表');
			return false;
		}
	}
	
	var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	param += '&SERIAL_NUMBER_B='+mebSn+'&MEB_LIST='+list;;
	$.beginPageLoading("成员号码校验...");
	ajaxSubmit(null, 'checkAddMeb', param, '', checkAddMebSucc, 
		function(errorcode, errorinfo){
			$.endPageLoading();
			$('#FMY_SERIAL_NUMBER').val('');
			alert(errorinfo);
		});
}

function checkAddMebSucc(ajaxData)
{
	$.endPageLoading();
	$('#FMY_SERIAL_NUMBER').val('');
	
	var mebSn = ajaxData.get('SERIAL_NUMBER_B');
	var familyEdit = new Array();
	familyEdit["INST_ID"]='<input type="checkbox" id="viceCheckBox" name="viceCheckBox" value="">';
    familyEdit["SERIAL_NUMBER"] = mebSn;
    familyEdit["START_DATE"] = "立即";
	familyEdit["END_DATE"] = "2050-12-31 23:59:59";
    $.table.get("viceInfoTable").addRow(familyEdit);
}
//删除操作
function delMeb()
{
var list = $.table.get("viceInfoTable").getTableData();
	$("#viceInfoTable_Body input[name=viceCheckBox]").each(function(){
	   if(this.checked){
	     	this.click();
	     	//删除时修改结束时间
	        var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("viceInfoTable");
            var json = table.getRowData(null, rowIndex);
            var dealTag = json.get("DEAL_TAG");
            if(dealTag=='1')
            {
              alert('此成员关系到本账期末结束，不需要再次取消！');
              return;
            }
	        table.deleteRow();
	   }
	});
}	
//新增操作主卡
function addMain()
{
	var mebSn = $('#AUTH_SERIAL_NUMBER').val();
	var mainSn = $("#FMY_SERIAL_NUMBER").val();
	if(mainSn == '')
	{
		alert('请先输入家庭主卡号码');
		return;
	}
	
	//是否手机号码校验
	if(!$.verifylib.checkMbphone(mainSn)){
		alert('不是手机号码，请重新输入！');
		$('#FMY_SERIAL_NUMBER').val('')
		return;
	}
	
	//成员号码不能与主号一致
	if(mebSn == mainSn){
		alert('对不起，主卡号码不能和成员号码一样，请重新输入！');
		$('#FMY_SERIAL_NUMBER').val('');
		return;
	}
	
	debugger;
	var exits = $("#viceInfoTable tbody").children("tr").length;
	if(exits>1){
	   alert('一个号码只能加入一个家庭共享组！不能再次新增！');
	   return false;
	}
	var list = $.table.get("viceInfoTable").getTableData();
	if(list.length>0){
	   alert('只能添加1个家庭共享组！');
	   return false;
	}
	/*for(var i = 0, size = list.length; i < size; i++)
	{
		var tmp = list.get(i);
		var sn = tmp.get('SERIAL_NUMBER');
		var tag = tmp.get('tag');
		if(mebSn == sn && tag != '1')
		{
			alert('号码'+mainSn+'已经在家庭主卡列表');
			return false;
		}
	}*/
	
	var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val();
	param += '&SERIAL_NUMBER_B='+mainSn+'&MEB_LIST='+list;;
	$.beginPageLoading("家庭主卡号码校验...");
	ajaxSubmit(null, 'checkAddMain', param, '', checkAddMainSucc, 
		function(errorcode, errorinfo){
			$.endPageLoading();
			$('#FMY_SERIAL_NUMBER').val('');
			alert(errorinfo);
		});
}
function checkAddMainSucc(ajaxData)
{
	$.endPageLoading();
	$('#FMY_SERIAL_NUMBER').val('');
	
	var mainSn = ajaxData.get('SERIAL_NUMBER');
	var familyEdit = new Array();
	familyEdit["INST_ID"]='<input type="checkbox" id="viceCheckBox" name="viceCheckBox" value="">';
    familyEdit["SERIAL_NUMBER"] = mainSn;
    familyEdit["START_DATE"] = "立即";
	familyEdit["END_DATE"] = "2050-12-31 23:59:59";
    $.table.get("viceInfoTable").addRow(familyEdit);
}
//删除操作主卡
function delMain()
{
var list = $.table.get("discntInfoTable").getTableData();
	$("#discntInfoTable_Body input[name=discntCheckBox]").each(function(){
	   if(this.checked){
	     	this.click();
	     	//删除时修改结束时间
	        var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("discntInfoTable");
            var json = table.getRowData(null, rowIndex);
            var dealTag = json.get("DEAL_TAG");
            if(dealTag=='1')
            {
              alert('此成员关系到本账期末结束，不需要再次取消！');
              return;
            }
	        table.deleteRow();
	   }
	});
}	
function onTradeSubmit()
{
   
   var data = $.table.get("viceInfoTable").getTableData();
   var cancel=document.getElementById("cond_CANCEL_CLUSTER").value;
   var isExist=document.getElementById("IS_EXIST").value;
	if(data.length == 0&&cancel!=1&&isExist!=0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&MEB_LIST='+data;
	param += '&MEMBER_CANCEL='+"0";
	param += '&CANCEL_CLUSTER='+cancel;
	param += '&IS_EXIST='+isExist;
	param += '&REMARK='+$('#REMARK').val();
	$.cssubmit.addParam(param);
	
	return true;
}

function onTradeSubmitForcancel()
{
   /*var data="";
   $("#viceInfoTable_Body input[name=viceCheckBox]").each(function(){
      
	        var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("viceInfoTable");
            data = table.getRowData(null, rowIndex);
            data.put("tag","1");
   });
  var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&MEB_LIST='+"["+data+"]";
	param += '&MEMBER_CANCEL='+"1";
	param += '&REMARK='+$('#REMARK').val();
	if(data =="") {
		alert("此副卡数据不存在，请检查！");
		return false;
	}
	
	$.cssubmit.addParam(param);
	
	return true;*/
	
	var data = $.table.get("viceInfoTable").getTableData();
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&MEB_LIST='+data;
	param += '&MEMBER_CANCEL='+"1";
	param += '&CANCEL_CLUSTER='+"0";
	param += '&REMARK='+$('#REMARK').val();
	if(data.length == 0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	
	$.cssubmit.addParam(param);
	
	return true;
}