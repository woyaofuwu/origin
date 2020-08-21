function refreshPartAtferAuth(data)
{
	var userId = data.get('USER_INFO').get('USER_ID');
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&USER_ID='+userId;
	param += '&SERIAL_NUMBER=' + serialNumber;
	$.ajax.submit(null, 'loadInfo', param, 'viceInfopart,discntInfopart',null,loadInfoError);
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
	$.ajax.submit('', 'checkAddMeb', param, '',function(rtnData) {
		$.endPageLoading();
		if(rtnData!=null&&rtnData.length > 0){		
			$('#FMY_SERIAL_NUMBER').val('');
			if(rtnData.get("WARM_TIPS")=="1"){
				alert("您将邀请副卡使用共享套餐内的流量，24小时内生效。生效后，每张副卡收取月功能费10元。");
			}
			if(rtnData.get("WARM_TIPS")=="-1"){
				alert(rtnData.get("WARM_INFO"));
			}else{
				checkAddMebSucc(rtnData);
			}
		}
		
	},
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
	var edt = ajaxData.get('END_DATE');
	var familyEdit = new Array();
	familyEdit["INST_ID"]='<input type="checkbox" id="viceCheckBox" name="viceCheckBox" value="">';
    familyEdit["SERIAL_NUMBER"] = mebSn;
    familyEdit["START_DATE"] = "立即";
	familyEdit["END_DATE"] = edt;//"2050-12-31 23:59:59";
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

function onTradeSubmit()
{
   
   var data = $.table.get("viceInfoTable").getTableData();
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&MEB_LIST='+data;
	param += '&MEMBER_CANCEL='+"0";
	param += '&REMARK='+$('#REMARK').val();
	if(data.length == 0) {
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	
	$.cssubmit.addParam(param);
	
	return true;
}

function onTradeSubmitForcancel()
{
   var data="";
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
	
	return true;
}