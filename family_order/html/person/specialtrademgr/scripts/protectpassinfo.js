function init()
{
	setCustomQuestionFirst();
	setCustomQuestionSecond();
	setCustomQuestionThird();
}

function refreshPartAtferAuth(data)
{
	var user_info = data.get("USER_INFO").toString();
	var cust_info = data.get("CUST_INFO").toString();
	var acct_info = data.get("ACCT_INFO").toString();
	
	
	var param = "&USER_INFO="+user_info+"&CUST_INFO="+cust_info+"&ACCT_INFO="+acct_info;
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('AuthPart', 'loadChildInfo', param, 'QueryPartTwo,custInfoPart,CustInfoTwoPart', function(data){
		setCustomQuestionFirst();
		setCustomQuestionSecond();
		setCustomQuestionThird();
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}
//问题一的设置
function setCustomQuestionFirst(){
	var otherinfoQuestionThird = $("#otherinfo_QUESTION_THIRD").val();
	var otherinfoQuestionSecond = $("#otherinfo_QUESTION_SECOND").val();
	var otherinfoQuestionFirst = $("#otherinfo_QUESTION_FIRST").val();
	
	var customQuestionFirst = $("#CUSTOM_QUESTION_FIRST");
	var spanCustomQuestionFirst = $("#span_CUSTOM_QUESTION_FIRST");
	var otherinfoCustomQuestionFirst = $("#otherinfo_CUSTOM_QUESTION_FIRST");
	var customAnswerFirst =  $("#CUSTOM_ANSWER_FIRST");
	
	if(otherinfoQuestionFirst == 'z'){
		customQuestionFirst.css("display","");
		customAnswerFirst.attr("class","li");
		spanCustomQuestionFirst.attr("class","e_required");
		otherinfoCustomQuestionFirst.attr("nullable", "no");
		
	} else {
		customQuestionFirst.css("display","none");
		customAnswerFirst.attr("class","li col-2");
		spanCustomQuestionFirst.attr("class","");
		otherinfoCustomQuestionFirst.attr("nullable", "yes");
	}
	
	/*校验选择的问题是否已经设置*/
	if(otherinfoQuestionSecond != "" && otherinfoQuestionSecond !='z'){
		if(otherinfoQuestionFirst == otherinfoQuestionSecond){
			alertMessage($("#otherinfo_QUESTION_FIRST"));
		}
	}
	if(otherinfoQuestionThird != "" && otherinfoQuestionThird !='z'){
		if(otherinfoQuestionFirst == otherinfoQuestionThird){
			alertMessage($("#otherinfo_QUESTION_FIRST"));
		}
	}
}

//问题二的设置
function setCustomQuestionSecond(){
	var otherinfoQuestionThird = $("#otherinfo_QUESTION_THIRD").val();
	var otherinfoQuestionSecond = $("#otherinfo_QUESTION_SECOND").val();
	var otherinfoQuestionFirst = $("#otherinfo_QUESTION_FIRST").val();
	
	var customQuestionSecond = $("#CUSTOM_QUESTION_SECOND");
	var spanCustomQuestionSecond = $("#span_CUSTOM_QUESTION_SECOND");
	var otherinfoCustomQuestionSecond = $("#otherinfo_CUSTOM_QUESTION_SECOND");
	var customAnswerSecond =  $("#CUSTOM_ANSWER_SECOND");
	
	if(otherinfoQuestionSecond == 'z'){
		customQuestionSecond.css("display","");
		customAnswerSecond.attr("class","li");
		spanCustomQuestionSecond.attr("class","e_required");
		otherinfoCustomQuestionSecond.attr("nullable", "no");
	} else {
		customQuestionSecond.css("display","none");
		customAnswerSecond.attr("class","li col-2");
		spanCustomQuestionSecond.attr("class","");
		otherinfoCustomQuestionSecond.attr("nullable", "yes");
	}
	 
	
	/*校验选择的问题是否已经设置*/
	if(otherinfoQuestionFirst != "" && otherinfoQuestionFirst !='z'){
		if(otherinfoQuestionFirst == otherinfoQuestionSecond){
			alertMessage($("#otherinfo_QUESTION_SECOND"));
		}
	}
	if(otherinfoQuestionThird != "" && otherinfoQuestionThird !='z'){
		if(otherinfoQuestionSecond == otherinfoQuestionThird){
			alertMessage($("#otherinfo_QUESTION_SECOND"));
		}
	}
}



//问题三的设置
function setCustomQuestionThird(){
	var otherinfoQuestionThird = $("#otherinfo_QUESTION_THIRD").val();
	var otherinfoQuestionSecond = $("#otherinfo_QUESTION_SECOND").val();
	var otherinfoQuestionFirst = $("#otherinfo_QUESTION_FIRST").val();
	
	var customQuestionThird = $("#CUSTOM_QUESTION_THIRD");
	var spanCustomQuestionThird = $("#span_CUSTOM_QUESTION_THIRD");
	var otherinfoCustomQuestionThird = $("#otherinfo_CUSTOM_QUESTION_THIRD");
	var customAnswerThird =  $("#CUSTOM_ANSWER_THIRD");
	
	if(otherinfoQuestionThird == 'z'){
		customQuestionThird.css("display","");
		customAnswerThird.attr("class","li");
		spanCustomQuestionThird.attr("class","e_required");
		otherinfoCustomQuestionThird.attr("nullable", "no");
	} else {
		customQuestionThird.css("display","none");
		customAnswerThird.attr("class","li col-2");
		spanCustomQuestionThird.attr("class","");
		otherinfoCustomQuestionThird.attr("nullable", "yes");
	}
	
	    
	/*校验选择的问题是否已经设置*/
	if(otherinfoQuestionFirst != "" && otherinfoQuestionFirst !='z'){
		if(otherinfoQuestionFirst == otherinfoQuestionThird){
			alertMessage($("#otherinfo_QUESTION_THIRD"));
		}
	}
	if(otherinfoQuestionSecond != "" && otherinfoQuestionSecond !='z'){
		if(otherinfoQuestionSecond == otherinfoQuestionThird){
			alertMessage($("#otherinfo_QUESTION_THIRD"));
		}
	}
	 
}

/*处理选择相同问题后的提示信息*/
function alertMessage(obj){
	alert("该问题已经设置，请重新选择设置的问题！");
	obj.val("");
	return false;
}

function checkEmail(e)
{
	
	if(""!=e&&!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(e))
	{
		var msg= e+"不符合邮件地址的格式要求";
		alert(msg)
		return false;
	}
	
	return true;
}

function checkAll()
{

	//查询条件校验
	if(!$.validate.verifyAll("CustInfoTwoPart")) {
		return false;
	}
	
	if(!checkEmail($("#otherinfo_EMAIL").val()))
	{
		return false;
	}
	
	return true;
}
  
 
 












