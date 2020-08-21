$(function(){
	var exceptionId =$("#EXCEPTION").val();
	if(exceptionId){
		MessageBox.error("错误信息", exceptionId, function(btn){
			$.nav.close();
		});
	}
});

function qryTradeInfo(obj){
	var productrId = $("#cond_PRODUCT_ID").val()
	var custId = $("#cond_CUST_ID").val()
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit(null,'qryTradeInfoByUserId','&PRODUCT_ID='+productrId + '&CUST_ID='+custId,'tradeInfoPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	})

}
function qryMemberListInfo(obj){
	var userId = $(obj).attr("userida");
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit(null,'qryMemberStateList','&USER_ID_A='+userId,'MemberInfo',function(data){
		hidePopup(obj);
		showPopup("qryPopup1", "memberTradePopupItem", true);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	})
}

function submitApply()
{
	debugger;
	var table = new Wade.DatasetList();
	table = getTables();
	if(table == null || table.length == 0){
		MessageBox.alert("错误","您未选择订单，请重新操作后再提交！");
		return false;
	}
	var contractId = $("#ESP_CONTRACT_ID").val()
	var bpmTempletId = $("#ESP_BPM_TEMPLET_ID").val()
	MessageBox.confirm("提示信息", "请再次确认订单及成员已全部完工！", function(btn){
		if("ok" == btn){
			var submitData = buildSubmitData(); // 拼凑 提交参数；
			var params = "&ROWDATAS=" + table +"&CONTRACT_ID=" + contractId +"&BPM_TEMPLET_ID=" + bpmTempletId + "&SUBMIT_PARAM="+encodeURIComponent(submitData.toString());
			var message = "提交成功!";
			$.beginPageLoading("数据提交中，请稍后...");
			$.ajax.submit("", "submit", params, "", function(data){
				$.endPageLoading();
				MessageBox.success(message, "工单号："+data.get("IBSYSID"), function(btn){
					if("ok" == btn){
						closeNav();
					}
				});
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	});
}

//获取选中稽核数据
function getTables(){
	var chk = document.getElementsByName("choiceEspOrder");
	var tables = new Wade.DatasetList();
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
				var table = new Wade.DataMap();
				table.put("USER_ID",chk[i].getAttribute("USER_ID"));
				table.put("SERIAL_NUMBER",chk[i].getAttribute("SERIAL_NUMBER"));
				table.put("TRADE_ID",chk[i].getAttribute("TRADE_ID"));
				table.put("PRODUCT_ID",chk[i].getAttribute("PRODUCT_ID"));
				tables.add(table);
            }
       	}
		
	}
	return tables;
}

function isEmpty(param){
	if (param == null || param == "" || undefined == param) {
		return true;
	}
	return false;
}

function showMemberListInfo(){
	showPopup("qryPopup2", "memberPopupItem", true);
}

//拼凑 提交参数；
function buildSubmitData() {

    var submitData = new Wade.DataMap();
	submitData.put("COMMON_DATA", saveEosCommonData());
    return submitData;
}

function saveEosCommonData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	if(!eosCommonData.get("CUST_NAME"))
	{
		eosCommonData.put("CUST_NAME", $("#CUST_NAME").val());
	}
	return eosCommonData;
}



