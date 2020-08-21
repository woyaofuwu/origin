$(window).load(function() {
    if($("#TRADE_AUTH_SERIAL_NUMBER").val()){
	   initModifyPostInfo()
    }
});


function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'refresh2', function(){
		initModifyPostInfo(data);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

//页面初始化方法
function initModifyPostInfo()
{ 
	$("#busi").attr("style","");
//	if($("#postinfo_POST_DATE_MON").val()){
//		$("#li_post_date").attr('disabled',false);
//		$("#span_post_date").addClass("e_required");
//		$("#postinfo_POST_DATE_MON").attr("nullable","no");
//	}else{
//		$("#postinfo_POST_DATE_MON").attr("nullable","yes");
//	}
	var epostChannel = $("*[name='POST_CHANNEL']");  
	for(var i=0;i<epostChannel.length;i++){
	    if(epostChannel[i].checked){
	    	if(i==0){
	    		$("#span_receive_number").addClass("e_required");
			    $("#postinfo_RECEIVE_NUMBER").attr("nullable","no");
	    	}else{
	    		$("#span_post_adr").addClass("e_required");
			    $("#postinfo_POST_ADR").attr("nullable","no");
	    	}
	    }
    }
	
}

function setPostTypeChange(objCheck)
{
//	if($(objCheck).attr("name")=='POST_TYPE_MON'){ 
//		if(objCheck.checked){
//			$("#postinfo_POST_DATE_MON").attr("nullable","no");
//			$("#li_post_date").attr('disabled',false);
//			$("#span_post_date").addClass("e_required");
////			$("#POST_TYPE_CASH").attr("checked",false);
//		}else{
//			$("#li_post_date").attr('disabled',true);
//	 		$("#span_post_date").attr("className","");
//	 		$("#postinfo_POST_DATE_MON").attr("nullable","yes");
//	 		$("#postinfo_POST_DATE_MON").val("");
//			
//		}
//	}
//	if($(objCheck).attr("name")=='POST_TYPE_CASH'){ 
//		$("#POST_TYPE_MON").attr("checked",false);
//		$("#li_post_date").attr('disabled',true);
//		$("#span_post_date").attr("className","");
//		$("#postinfo_POST_DATE_MON").attr("nullable","yes");
//		$("#postinfo_POST_DATE_MON").val("");	
//	}
}

function setPostChannelChange(objCheck)
{
	if($(objCheck).attr("name")=='POST_CHANNEL'){ 
		if(objCheck.checked){
	        $("#POST_TYPE").attr("checked",true);
			if(objCheck.value==02){
				$("#span_receive_number").addClass("e_required");
				$("#postinfo_RECEIVE_NUMBER").attr("nullable","no");
			}
			if(objCheck.value==12){		
				$("#span_post_adr").addClass("e_required");
				$("#postinfo_POST_ADR").attr("nullable","no");
			}
		}else{
			if(objCheck.value==02){
				$("#span_receive_number").attr("className","");
				$("#postinfo_RECEIVE_NUMBER").attr("nullable","yes");
				$("#postinfo_RECEIVE_NUMBER").val("");
			}
			if(objCheck.value==12){
				$("#span_post_adr").attr("className","");
				$("#postinfo_POST_ADR").attr("nullable","yes");
				$("#postinfo_POST_ADR").val("");
				$("#postinfo_POST_ADR_SEC").val("");
			}	
		}
	}
	
	
}

function checkFinal(){

	
	var serialNumber = $('#AUTH_SERIAL_NUMBER').val();
	if(!checkInfo())
	{
		return false;	
	}
	$.beginPageLoading();
	$.ajax.submit('refresh2', 'onTradeSubmit', "&AUTH_SERIAL_NUMBER="+serialNumber, 'refresh2', function(data){
		$.endPageLoading();
		/*MessageBox.alert("提示","设置成功",function(btn){
		$.redirect.toPage('changeuserinfo.ModifyEPostInfo','onInitTrade',null);
	    });*/
		
		MessageBox.show({
			"title":"成功提示",
			"msg":"业务受理成功!",
			"success":true,
			"fn":function(btn){
					if(btn == "ok" || btn=="cancel"){	
						//window.location.reload();//清空页面数据
						$.redirect.toPage('changeuserinfo.ModifyEPostInfo','onInitTrade',null);
					}else if(btn == "ext0"){
						return false;
					}
				},
             "beforeHide":function(btn){
					if(btn == "ok"){	
						return true;													
					}else if(btn == "ext0"){
						printEdocInfo(data);
						return false;
					}
				},
			"buttons":$.extend({ok:"确定"},{ext0:"打印电子工单,print"})
});
	},
	function(code,info,detail){
		$.endPageLoading();
		MessageBox.error("提示","设置失败",null,null,info,detail);
	});
}

function checkFinal2(){
    var serialNumber = $('#TRADE_AUTH_SERIAL_NUMBER').val();
	if(!checkInfo())
	{
		return false;	
	}
	$.beginPageLoading();
	$.ajax.submit('refresh2', 'onTradeSubmit', "&TRADE_AUTH_SERIAL_NUMBER="+serialNumber, 'refresh2', function(data){
		$.endPageLoading();
	/*	MessageBox.alert("提示","设置成功",function(btn){
        setPopupReturnValue('','');
		});*/
		MessageBox.show({
			"title":"成功提示",
			"msg":"业务受理成功!",
			"success":true,
			"fn":function(btn){
					if(btn == "ok" || btn=="cancel"){	
						//window.location.reload();//清空页面数据
						//$.redirect.toPage('changeuserinfo.ModifyEPostInfo','onInitTrade',null);
						setPopupReturnValue('','');
					}else if(btn == "ext0"){
						return false;
					}
				},
             "beforeHide":function(btn){
					if(btn == "ok"){	
						return true;													
					}else if(btn == "ext0"){
						printEdocInfo(data);
						return false;
					}
				},
			"buttons":$.extend({ok:"确定"},{ext0:"打印电子工单,print"})
});
	},
	function(code,info,detail){
		$.endPageLoading();
		MessageBox.error("提示","设置失败",null,null,info,detail);
	});
    
}


//电子工单补打
function printEdocInfo(data){
	var tradeId=data.get(0).get("X_TRADEID");
	$.beginPageLoading("获取打印数据。。。");
	$.ajax.submit("", "printEdoc", "&TRADE_ID="+tradeId, null,function(data){
		$.endPageLoading();
		
		if(data.get("RESULT_CODE")!="0"){
			MessageBox.alert("告警提示","没有找到电子工单数据！");
			return;
		}
		var notePrintData=data.get("CNOTE_DATA");
		
		if(!notePrintData || !notePrintData.length){
			alert("没有找到电子工单数据！");
			return;
		}
		//由于历史原因，系统换行符号以~区分，而电子工单需要两个 
		notePrintData.put("RECEIPT_INFO1", (notePrintData.get("RECEIPT_INFO1","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO2", (notePrintData.get("RECEIPT_INFO2","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO3", (notePrintData.get("RECEIPT_INFO3","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO4", (notePrintData.get("RECEIPT_INFO4","")).replace(/\~/g,"~~"));
		notePrintData.put("RECEIPT_INFO5", (notePrintData.get("RECEIPT_INFO5","")).replace(/\~/g,"~~"));

		$.printMgr.params.put("EPARCHY_CODE", "0898");
		
		var printInfos=$.DatasetList();
		var printInfo=$.DataMap();
		printInfo.put("TRADE_ID", tradeId);
		printInfo.put("TYPE", "P0003");
		printInfo.put("EPARCHY_CODE", "0898");
		printInfos.add(printInfo);
		
		//设置打印数据，设置成标准打印格式，以期后面整个走打印流程
		$.printMgr.setPrintData(printInfos);
		
		//启动电子工单打印
		$.printMgr.setElcNoteData(notePrintData);
		
		//$("#CSSUBMIT_BUTTON").attr("edocSecond")=="true";
		$.printMgr.getElecAcceptBill(notePrintData);
		
		
	},function(code, info, detail){
		$.endPageLoading();
		MessageBox.error("错误提示","获取打印数据错误！", null, null, info, detail);
	});
}


function checkInfo(){
	if($('#TRADE_AUTH_SERIAL_NUMBER').val()!=''
		&&!$("#POST_TYPE_MON").attr("checked")){  //若是页面跳转过来的
		$("#postinfo_POST_DATE_MON").attr("nullable","yes");
	}
	
	if($('#TRADE_AUTH_SERIAL_NUMBER').val()!=''){  //若是页面跳转过来的
		var teleNumber = $("#TRADE_AUTH_SERIAL_NUMBER").val();
		$("#AUTH_SERIAL_NUMBER").val(teleNumber);
	}
	
	if(!isCheckBox("POST_CHANNEL")&&(isCheckBox("POST_TYPE_MON")||isCheckBox("POST_TYPE_CASH")||isCheckBox("POST_TYPE_BUSI"))) // 在有选择发票类型的情况下,邮箱地址和手机号码至少选一个
	{
	   alert("请选择推送方式并添加邮箱地址或手机号码信息!");
	   return false;
	}
	if(isCheckBox("POST_CHANNEL")&&(!isCheckBox("POST_TYPE_MON")&&!isCheckBox("POST_TYPE_CASH")&&!isCheckBox("POST_TYPE_BUSI"))) // 在选择推送方式时，发票类型必须选一个
	{
	   alert("请选择电子发票类型!");
	   return false;
	}
	//在选择了短信或电子邮箱的情况下，营业发票和月结发票需至少选一个
//	if(isCheckBox("POST_CHANNEL")){
//		if(!isCheckBox("EPOST_TYPE")){
//			alert("请选择电子发票类型");
//			return false;
//		}
//	}
	
//	if(!isCheckBox("EPOST_TYPE")){
//		alert("请选择发票类型！");
//		return false;
//   }
	if($("#postinfo_RECEIVE_NUMBER").val()!=""&&
	!isTel($("#postinfo_RECEIVE_NUMBER").val())){
		alert("手机号码格式错误");
		return false;
	}
	if($("#postinfo_POST_ADR_SEC").val()!=""&&
	!isEmail($("#postinfo_POST_ADR_SEC").val())){
		alert("邮箱格式错误");
		return false;
	}
	if(!verifyAll('refresh2'))
	{
		return false;
	}
	return true;

}

//function getPostValue(){
//	var epostTypeStr="";
//	var epostType = $("*[name='EPOST_TYPE']");
//	for(var i=0;i<epostType.length;i++){
//		if(epostType[i].checked){
//			epostTypeStr = epostTypeStr+epostType[i].value;
//		}
//	}
//	$("#postinfo_EPOSTTYPE").val(epostTypeStr);
//	var epostChannelStr="";
//	var epostChannel = $("*[name='POST_CHANNEL']");
//	for(var i=0;i<epostChannel.length;i++){
//		if(epostChannel[i].checked){
//			epostChannelStr = epostChannelStr+epostChannel[i].value;
//		}
//	}
//	$("#postinfo_EPOSTCHANNEL").val(epostChannelStr);
//}

/**
 * 检查是否没有一个被选中
 */
var isCheckBox= function(objId){
	var obj = $("*[name=" + objId +"]");
	if(obj&&obj.length){
		for(var i=0;i<obj.length;i++){
			if(obj[i].checked)
				return true;
		}
	}
	return false;
}

function isTel(str){
   var reg=/^([0-9]|[\-])+$/g ;
   if(str.length!=11&&str.length!=13){//增加物联网手机号码长度 13位
    return false;
   }
   else{
     return reg.exec(str);
   }
}

function isEmail(str){
	reg=/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
	return reg.exec(str);
}
















