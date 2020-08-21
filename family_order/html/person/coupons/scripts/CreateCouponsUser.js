var ticketIdx=1;
var ticketList=$.DatasetList(); 


function checkSn(){
	if(!$.validate.verifyAll("mbphonePart")) {
		$("#cond_SERIAL_NUMBER").focus();
		return false;
	}
	 $.beginPageLoading("正在校验号码...");
	 $.ajax.submit('AddListPart', 'checkSn', "", '', function(data){
		 	var rtnTag=data.get("USER_FLAG");
		 	if(rtnTag=="0"){
		 		alert(data.get("USER_MSG"));
		 		$("#cond_SERIAL_NUMBER").val("");
		 		$("#cond_SERIAL_NUMBER").focus();
		 		$.endPageLoading();
		 	}else{
		 		$.endPageLoading();
		 	}
	 },function(code, info, detail){
			$.endPageLoading();
			alert("校验号码错误！\n"+info);
		},function(){
			$.endPageLoading(); 
		}); 
}

function addTicketList() { 
	if(!$.validate.verifyAll("AddListPart")) {
		return false;
	}
	
	var ticketValue = $("#cond_TICKET_VALUE").val();
	var balance=$("#cond_BALANCE").val();
		
	
	var auditOrderId=$("#cond_AUDIT_ORDER_ID").val();
	var tticket_value=$("#TTICKET_VALUE").val();
	if(ticketValue>tticket_value){
		alert("优惠券单笔最大金额不能超过"+tticket_value);
		return false;
	}
//	if(parseInt(ticketValue)>parseInt(balance)){
//		alert("电子券审批工单余额不足，请选择其他工单或重新申请审批工单！");
//		return false;
//	}
//	balance = parseInt(balance)-parseInt(ticketValue);
	

	//已分配总额
	var used_balance = parseInt($("#cond_SUM_VALUE").val());
	//获取可用限额
	var surplus = parseInt(balance) - used_balance;
	used_balance = used_balance+parseInt(ticketValue);

	if(used_balance>parseInt(balance)){
		//如果超出额度则不再增加
		used_balance = used_balance-parseInt(ticketValue);
		$("#cond_SUM_VALUE").val(used_balance);
		
		alert("电子券审批工单余额不足，请选择其他工单或重新申请审批工单！余额仅剩："+surplus+"元！");
		return false;
	}

	var serialNum=$("#cond_SERIAL_NUMBER").val();
	var remark=$("#cond_REMARK").val();
	
	var tikObj = $.DataMap();
	tikObj.put("TICKET_IDX", ticketIdx);
	tikObj.put("TICKET_VALUE", ticketValue);  
	tikObj.put("SERIAL_NUMBER", serialNum);  
	tikObj.put("AUDIT_ORDER_ID", auditOrderId);
	tikObj.put("BALANCE", balance);
	tikObj.put("REMARK", remark);  
	tikObj.put("SUM_VALUE",used_balance);
	ticketList.add(tikObj);
	ticketIdx++;
	$("#cond_BALANCE").val(balance);
	//设置页面上sum值
	$("#cond_SUM_VALUE").val(used_balance);
	renderList(tikObj);
}

function renderList(ticketObj){
	var curIdx=$("#ListPart tr").length+1;
	var tikArr = [];
	tikArr.push('<tr ticketIdx="'+ticketObj.get("TICKET_IDX")+'">');
	tikArr.push('<td name="SEQ" class="e_center">'+curIdx+'</td>');
	tikArr.push('<td>'+ticketObj.get("TICKET_VALUE")+'</td>');
	tikArr.push('<td>'+ticketObj.get("SERIAL_NUMBER")+'</td>'); 
	tikArr.push('<td>'+ticketObj.get("AUDIT_ORDER_ID")+'</td>');
	tikArr.push('<td>'+ticketObj.get("BALANCE")+'</td>');  
	tikArr.push('<td class="e_center"><a href="javascript:void(0)" onclick="removeList(this)">删除</a></td>');
	tikArr.push('</tr>');
	$("#ListPart").append(tikArr.join(""));
	tikArr=null;
}

function resetButton(){  
	$("#TICKET_VALUE").val("");
	$("#SERIAL_NUMBER").val("");
	$("#AUDIT_ORDER_ID").val("");
	$("#BALANCE").val("");
	$("#REMARK").val("");
}

function removeList(obj){
	var tikTr=$(obj).parent().parent();
	var tikIdx = tikTr.attr("ticketIdx");
	var used_balance= $("#cond_SUM_VALUE").val();
	
	tikTr.remove();
	ticketList.each(function(item,index,totalcount){
		if(item.get("TICKET_IDX") == tikIdx){ 
			used_balance=parseInt(used_balance)-parseInt(item.get("TICKET_VALUE"));
			$("#cond_SUM_VALUE").val(used_balance);
			ticketList.removeAt(index);
			return false;
		}
	});
	$("#ListPart tr").each(function(i, el){
		$(el).find("td[name=SEQ]").html(i+1);
	}); 
}

function clearlist(){ 
	var now_used_balance = parseInt($("#cond_ROLLBACK_SUM_VALUE").val());
	$("#cond_SUM_VALUE").val(now_used_balance);
	ticketIdx=0;
	ticketList=$.DatasetList();
	$("#ListPart").empty(); 
}

 

function submitBeforeCheck(){
	//if(!$.validate.verifyAll($("#PayMoneyPart")[0])){
	//	return false;
	//}
	if(ticketList.length <1){
		alert("列表无数据，请编辑并点击\"暂存\"按钮保存数据到列表中再提交！");
		return false;
	} 
	if(!confirm("确定提交吗?")){ 
		return false;
	}

	var tikInfo="&TICKET_INFO="+ticketList.toString();
	tikInfo=tikInfo.replace(/%/g,"%25"); 
	$.cssubmit.addParam(tikInfo); 
	return true; 
}

//显示余额
function showBalance(obj){
	$("#cond_BALANCE").val(obj.value);
	$("#cond_AUDIT_ORDER_ID").val(obj.options[obj.selectedIndex].text);
}