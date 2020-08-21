function refreshPartAtferAuth(data)
{
	$("#USER_ID").val(data.get("USER_INFO").get("USER_ID"));
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('', 'queryUserTicketList', "&USER_INFO="+data.get("USER_INFO")+"&CUST_NAME="+data.get("CUST_INFO").get("CUST_NAME"), 'ResultPart', function(){
		$.endPageLoading();
	},function(code, info, detail){
		$.endPageLoading();
		alert("查询数据错误！\n"+info);
	},function(){
		$.endPageLoading(); 
	});
} 

function submitBeforeCheck(){
	if(!$.validate.verifyAll("editPart")) { 
		return false;
	}
	var spendVal=$("#SPEND_VALUE").val();
	var logObj=$("#ResultPart").find("input[name=RADIO_LIST]:checked");
	if(!logObj || !logObj.length){
		alert("请选择要使用的优惠券！");
		return false;
	}
	var spendVal=$("#SPEND_VALUE").val();
	if(spendVal==""){
		alert("请录入“使用金额”。");
		return false;
		$("#SPEND_VALUE").focus();
	}
	var ticketList=$.DatasetList();
	var checkTag=true;
	logObj.each(function(){ 
		var state=$(this).attr("ticketState");//状态为2全部领取完成  3已返销'不能领取 
		if(state == "1" ){
			alert("优惠券已被使用过，无法再次使用！");
			checkTag=false;
			return false;
		}  
		var dateState=$(this).attr("dateState");//过期不允许领取
		if(dateState=="EXP"){
			alert("优惠券已于"+$(this).attr("ticketEndDate")+"到期，无法使用！");
			checkTag=false;
			return false;
		} 
		var ticketVal=$(this).attr("ticketVal");
		if(parseFloat(spendVal)>parseFloat(ticketVal)){
			alert("“使用金额”"+spendVal+"元超过优惠券金额，最大金额为"+ticketVal+"元！");
			checkTag=false;
			return false;
		}
		
		var m=$.DataMap(); 
		m.put("TICKET_CODE", $(this).val());
		m.put("TICKET_VALUE", $(this).attr("ticketVal"));
		m.put("USER_ID", $(this).attr("userId"));
		m.put("TICKET_END_DATE", $(this).attr("ticketEndDate"));
		m.put("TICKET_STATE", $(this).attr("ticketState"));
		m.put("SERIAL_NUMBER", $(this).attr("serialNum")); 
		m.put("SPEND_VALUE", spendVal); 
		m.put("REPAIR_NO", $("#REPAIR_NO").val()); 
		m.put("REMARK", $("#REMARK").val()); 
		ticketList.add(m);
		var tikInfo="&TICKET_INFO="+ticketList.toString();
		tikInfo=tikInfo.replace(/%/g,"%25"); 
		$.cssubmit.addParam(tikInfo);
		
	}); 
	if(checkTag){
		return true;
	}
}

function tableRowClick(){

}