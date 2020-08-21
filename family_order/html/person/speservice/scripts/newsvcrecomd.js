
//设置历史推荐信息
function refresHis(){
	$("#newID").attr("className", "");
	$("#hisID").attr("className", "on");
	
	$("#RecomdListPart").css("display","none");
	$("#HistoryRecomdListPart").css("display","");
}

//设置新业务推荐受理
function refresNew(){
	$("#newID").attr("className", "on");
	$("#hisID").attr("className", "");
	
	$("#RecomdListPart").css("display","");
	$("#HistoryRecomdListPart").css("display","none");
}

//设置短信用语
function showOne(){
	var flagOne = $("#showOne"); 
	var flagTwo = $("#showTwo"); 
	if(flagOne.attr("checked") == true)
	{ 
		if(flagTwo.attr("checked") == true)
		{
			
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:115px;");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:115px;");
		}
		else
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:258px;");
			$("#showPart2").css("display","none");
		}
		 
	} 
	else
	{
		if(flagTwo.attr("checked") == true)
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:258px;");
		}
		else
		{
			$("#showPart").css("display","none");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","none");
		}
	}
}

//设置推荐用语
function showTwo(){
	var flagOne = $("#showOne"); 
	var flagTwo = $("#showTwo"); 
	if(flagTwo.attr("checked") == true)
	{ 
		if(flagOne.attr("checked") == true)
		{
			 
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:115px;");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:115px;");
		}
		else
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","");
			$("#showPart4").attr("style","height:258px;");
		}
		 
	} 
	else
	{
		if(flagOne.attr("checked") == true)
		{
			$("#showPart").css("display","");
			$("#showPart1").css("display","");
			$("#showPart3").attr("style","height:258px;");
			$("#showPart2").css("display","none");
		}
		else
		{
			$("#showPart").css("display","none");
			$("#showPart1").css("display","none");
			$("#showPart2").css("display","none");
		}
	}
}

function refreshPartAtferAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo',  "&USER_INFO="+(data.get("USER_INFO")).toString()+"&CUST_INFO="+(data.get("CUST_INFO")).toString(),
	'RecomdInfoPart,RecomdListPart,HistoryRecomdListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function openPush(data)
{
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	if(sn != null && "" != sn) 
	{
		refreshPartAuth(data);
	}
}

function refreshPartAuth(data)
{
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('AuthPart,hiddenPart', 'loadChildInfo',   "&PUSH_FLAG=1",
	'RecomdInfoPart,RecomdListPart,HistoryRecomdListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function checkNum() {
	var i = 0;
	$("#SendTableValues input[type=radio]").each(function(){
		   if(this.checked){
			    i++;
		   }})
    
	if(i>1){
		alert("最多只能发送一条！");
		return false;
	}	   
		   
	
	return true;
}

function createDept() {
	
	if(!checkNum())
	{
		//设置所有属性值
		$("#SendTableValues input[type=radio]").attr("checked", false);
		return false;
	}
	
	var params = "";
	var replyNum = 0;
	
	var dataset = new Wade.DatasetList();
	$("#SendTableValues input[type=radio]").each(function(){
		   if(this.checked){
				var rowIndex = this.parentNode.parentNode.rowIndex; 
				var row = $.table.get("SendTable").getRowByIndex(""+rowIndex);
				var radios = $("input[type=radio]:checked", row[0]);
				var radio = radios.attr('rType'); 
				var selectParam = $("select", row[0]); 
				var reduseReason = selectParam.find("option:selected").text(); 
				var table = $.table.get("SendTable");
		        var json = table.getRowData(null, rowIndex);
		        
				var idata = new Wade.DataMap();
				 if("ACCEPT" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "0");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));
			            idata.put("NOTICE_CONTENT", json.get("NOTICE_CONTENT"));
			            idata.put("MOD_NAME_L", json.get("MOD_NAME_L"));
			            dataset.add(idata);
			        } 
			        if("HESITATE" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "1");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));
			            idata.put("NOTICE_CONTENT", json.get("NOTICE_CONTENT"));
			            dataset.add(idata);
			        }
			        if("REFUSE" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "2");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));           
			            dataset.add(idata);
			        }   
		   }
		});
		
        for(var i = 0; i < dataset.length; i++){
        	var replyData = dataset.get(i);
        	var replydata1 = replyData.get("REPLY");
        	var replyrefuse = replyData.get("REFUSE_REASON_CODE");
        	if(replydata1 =="0" || replydata1 =="1"){
        		replyNum++;
        	}
        }
        
        if(replyNum == 0){
        	alert("请至少选择一个接收或者犹豫的推荐业务，再发送短信!");
        	return false;
        }
		 
        $("#cond_ADD_SMS_INFO").val(dataset.toString()); 
	
		$.ajax.submit('hiddenPart,AuthPart,RecomdListPart', 'createDept',  null, null, function(data){
			var result = data.get(0, "MESSAGE");
			MessageBox.alert("系统提示：",result);
			  
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
}

function testShow(v,p,chk){  
	
	$("#smsinfo").text(p);
	$("#sendinfo").text(v);   
}

function showOther(obj,r){
	
	var refuseReason = "OTHER_REFUSE_REASON"+r;
	
	if(obj.val() == "其他")
	{
		$("#"+refuseReason).css("display","block");
	}
	else
	{
		$("#"+refuseReason).css("display","none");
	}
}

function updateDealMark(chk){
	if (chk.attr("checked") == true) {
		chk.attr("rowIndex", $.table.get("SendTable").getTable().attr("selected"));
	}
}


function checkFinal(){
	
	if(!checkNum())
	{
		$("#SendTableValues input[type=radio]").attr("checked", false);
		return false;
	} 
	
	var replyNum = 0;
	var dataset = new Wade.DatasetList();
	$("#SendTableValues input[type=radio]").each(function(){
		   if(this.checked){
				var rowIndex = this.parentNode.parentNode.rowIndex; 
				var row = $.table.get("SendTable").getRowByIndex(""+rowIndex);
				var radios = $("input[type=radio]:checked", row[0]);
				var radio = radios.attr('rType'); 
				var selectParam = $("select", row[0]); 
				var reduseReason = selectParam.find("option:selected").attr('data'); 
				var table = $.table.get("SendTable");
		        var json = table.getRowData(null, rowIndex);
		        
				var idata = new Wade.DataMap();
				 if("ACCEPT" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "0");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));
			            idata.put("NOTICE_CONTENT", json.get("NOTICE_CONTENT"));
			            idata.put("MOD_NAME_L", json.get("MOD_NAME_L"));
			            dataset.add(idata);
			        } 
			        if("HESITATE" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "1");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));
			            idata.put("NOTICE_CONTENT", json.get("NOTICE_CONTENT"));
			            dataset.add(idata);
			        }
			        if("REFUSE" == radio){
			            idata.put("CAMPN_NAME", json.get("CAMPN_NAME"));
			            idata.put("OBJECT_TYPE_DESC", json.get("OBJECT_TYPE_DESC"));
			            idata.put("REPLY", "2");
			            idata.put("REFUSE_REASON_CODE", reduseReason);
			            idata.put("OTHER_REFUSE_REASON", json.get("OTHER_REFUSE_REASON"));
			            idata.put("REFUSE_REMARK", json.get("REFUSE_REMARK"));
			            idata.put("CAMPN_ID", json.get("CAMPN_ID"));
			            idata.put("OBJECT_ID", json.get("OBJECT_ID"));
			            idata.put("OBJECT_TYPE", json.get("OBJECT_TYPE"));
			            idata.put("MOD_NAME", json.get("MOD_NAME"));
			            idata.put("BUTTON_NAME", json.get("BUTTON_NAME"));
			            idata.put("SOURCE_ID", json.get("SOURCE_ID"));           
			            dataset.add(idata);
			        }   
		   }
		});
	
	    for(var i = 0; i < dataset.length; i++){
	    	var replyData = dataset.get(i);
	    	var replydata1 = replyData.get("REPLY");
	    	var replyrefuse = replyData.get("REFUSE_REASON_CODE");
	    	if(replydata1.value!=""){
	    		replyNum++;
	    	}
	
	    	if(replydata1 =="2" &&replyrefuse =="0"){
	    		alert("当您选择了拒绝，请至少选择一个拒绝原因");
	    		return false;
	    	}
	    }
	
	    if(replyNum == 0){
	    	alert("请至少选择一个推荐业务");
	    	return false;
	    }
	    
	    $("#cond_ADD_SMS_INFO").val(dataset.toString()); 
	    
	    return true;
		 
}

function afterClickAffirm()
{
	$("#SendTableValues input[type=radio]").each(function()
	{
		   if(this.checked)
		   {
			   var rowIndex = this.parentNode.parentNode.rowIndex; 
				var row = $.table.get("SendTable").getRowByIndex(""+rowIndex);
				var radios = $("input[type=radio]:checked", row[0]);
				var radio = radios.attr('rType'); 
				var selectParam = $("select", row[0]); 
				var reduseReason = selectParam.find("option:selected").attr('data'); 
				var table = $.table.get("SendTable");
			    var json = table.getRowData(null, rowIndex);
				 if("ACCEPT" == radio)
				 {
			            var tradeTypeCode = json.get("OBJECT_ID");
			            var url = json.get("MOD_NAME");
			            var title =  json.get("OBJECT_TYPE_DESC");
			            $.nav.openByUrl(title,url);
			     } 
		   }
	});
}













