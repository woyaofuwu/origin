
function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'PostInfoPart,PostRepairPart', function(){
		initModifyPostInfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		$("#CSSUBMIT_BUTTON").attr("disabled",true);
		MessageBox.alert("提示", error_info, function(){$.auth.reflushPage();});
    });
    
}

//页面初始化方法
function initModifyPostInfo()
{
	if($("#postinfo_POST_TAG").val() == 0){
		$("#mySwitch").val("off");
		$("#showPart").css('display', '');
	}
	if($("#postinfo_POST_TAG").val() == 1){
		$("#mySwitch").val("on");
		$("#showPart").css('display', '');
	}
	
	$("#mySwitch").attr('disabled',true);
//	$("#postTag").attr('disabled',false);
	$("#remark").attr('disabled',false);
     $("#OLD_POST_TYPE").val("0");//记录用户以前是否选择了 邮政邮递 和 EMAIL 移动E 信的方式
     $("#OLD_EMAIL_TYPE").val("0");
  
   //如果邮寄标志有效 则展现邮递资料相关编辑框
    if ($("#postinfo_POST_TAG").val() =="1"){  
    	
        $("#postInfoDiv").css('display','');
        $("#postMothodDiv").css('display','');
        $("#postContentDiv").css('display','');
	    $("#emailContentDiv").css('display','');
	    $("#postInfoDiv").css('display','');
		
		var postTypes =  $("*[name='POST_TYPESET']");
		var postContent =  $("*[name='POSTTYPE_CONTENT']");
        var emailContent =  $("*[name='EMAILTYPE_CONTENT']");
			
		if (postTypes[0].checked){//选择了邮政投递
		    
			 $("#postContentDiv").attr('disabled',false);
			 $("#span_POST_CONTENT").addClass("required");
			 $("#span_address").addClass("required");	  			 
			 $("#postinfo_POST_ADDRESS").attr("nullable","no");  			 
			// postContent[0].disabled=true;		 
			 $("#OLD_POST_TYPE").val("1");//用户已经选择过邮政投递的方式
		 }else{
		     $("#postContentDiv").attr('disabled',true);
			 $("#span_POST_CONTENT").attr("className","label");
			 $("#span_address").attr("className","");	 
			 $("#postinfo_POST_ADDRESS").attr("nullable","yes"); 
		 }
		 
		 if (postTypes[1].checked){//选择了EMAIL投递方式
			 $("#span_EMAIL_CONTENT").attr("className","label");
		     $("#emailContentDiv").attr('disabled',false);
			 $("#span_EMAIL_CONTENT").addClass("required");
			 $("#span_email").addClass("required");
			 $("#postinfo_EMAIL").attr("nullable","no");
			 emailContent[0].disabled=true;
			 $("#OLD_EMAIL_TYPE").val("1");//用户已经选择过EMAIL移动E信的方式
		}else{
			 $("#emailContentDiv").attr('disabled',true);
			 $("#span_EMAIL_CONTENT").attr("className","label");
			 $("#span_email").attr("className","");
			 $("#postinfo_EMAIL").attr("nullable","yes"); 
			 $("#OLD_EMAIL_TYPE").val("0");//用户已经选择了EMAIL的方式

		}
    }
}


/*处理不同邮寄内容，不同校验*/
function setPostTypeChange(objCheck){
      var postContent = $("*[name='POSTTYPE_CONTENT']");//邮政投递内容
      var emailContent = $("*[name='EMAILTYPE_CONTENT']");//EMAIL 投递内容
      var postTypes = $("*[name='POST_TYPESET']");//投递方式
	if(objCheck.checked){//checked
		if(objCheck.value==0){//邮政投递
  		       $("#postContentDiv").attr('disabled',false);
      		   $("#span_POST_CONTENT").addClass("required");
      		   $("#span_address").addClass("required");
      		   $("#postinfo_POST_ADDRESS").attr("nullable","no"); 
      		   $("#postinfo_POST_NAME").attr("nullable","no"); 
		}
		if(objCheck.value==2){//移动E信
        		  $("#emailContentDiv").attr('disabled',false);
        		  $("#span_EMAIL_CONTENT").addClass("required");//email移动E信 状态设为必填
        		  $("#span_email").addClass("required");		 
        		  $("#postinfo_EMAIL").attr("nullable","no"); 	
        		  emailContent[0].checked=true;
        		  emailContent[0].disabled=true;
        		  $("#span_EMAIL_NAME").addClass("e_gray");
		}
	}else{//no checked
		if(objCheck.value==0){//邮政投递
		     $("#postContentDiv").attr('disabled',true);
    		 $("#span_POST_CONTENT").attr("className","label");
    		 $("#span_address").attr("className","");
    		 $("#postinfo_POST_ADDRESS").attr("nullable","yes"); 
    		 $("#postinfo_POST_NAME").attr("nullable","yes"); 
    		 	 
             var elements = $("*[name='POSTTYPE_CONTENT']");
        	 for(var i=0;i<elements.length;i++)
        	 {
        	 	elements[i].checked=false;
        	 }
        	 postTypes[1].disabled=false;
        	
		}
		if(objCheck.value==2){//移动E信
			 $("#emailContentDiv").attr('disabled',true);
    		 $("#span_EMAIL_CONTENT").attr("className","label");//email移动E信 状态设为必填
    		 $("#span_email").attr("className","");		
    		 $("#postinfo_EMAIL").attr("nullable","yes"); 
    		 var elements = $("*[name='EMAILTYPE_CONTENT']");
        	 for(var i=0;i<elements.length;i++)
        	 {
        	 	elements[i].checked=false;
        	 }		
		}
	}
}

//检查补寄月份
function checkMonth()
{
 var repairMonth = $('#postinfo_REPAIR_MONTH').val();
 if(repairMonth=="") return;
 
 if(!$.verifylib.checkPInteger(repairMonth) || !$.verifylib.checkNumberRange(repairMonth,1,12))
 {
	 MessageBox.alert("提示", "请输入正确的补寄月份。例如(1~12)之间的整数！");
	 $('#postinfo_REPAIR_MONTH').val('');
 	return false;
 }
 var tep= repairMonth + "";
 if($.trim(tep).length<2)
 {
	 repairMonth = "0" + tep;
 }
}

//检查传真号码
function faxChecked()
{
	
 var fax = $("#postinfo_FAX_NBR").val();
 if(fax=="" || fax==null) return true;
 
 if(!$.validate.verifyField($("#postinfo_FAX_NBR")[0]))
 {
 	return false;
 }
    
  var ch=fax.substr(0,1);
  var bool=false;
  for (var i=1;i<fax.length;i++)
  {
      if(ch!=fax.substr(i,1))
      {
        bool=true;
        break;
      }
  }
  
  if(!bool)
  {
	  MessageBox.alert("提示", "传真号码的数字位不能是同一个数字");
    $("#postinfo_FAX_NBR").focus();
    return false;
  }
  return true;
}


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



//点击确定按钮时的检查方法
function checkInfo(obj)
{       

    if($("#postinfo_POST_TAG")==null || $("#postinfo_POST_TAG").val() =="" )
    {
    	MessageBox.alert("提示", "请选择邮寄标志！");
		  return false;
    }else if ($("#postinfo_POST_TAG").val()=="0"){ 
          return true;
    }else{   
        
    	var postTypes =$("*[name='POST_TYPESET']");
        
        var postContent = $("*[name='POSTTYPE_CONTENT']");
        
        var emailContent = $("*[name='EMAILTYPE_CONTENT']");
        
        var mmsContent = $("*[name='MMSTYPE_CONTENT']");
        
        var check=false;        
        
        if(!postTypes[0].checked && !postTypes[1].checked){
        	MessageBox.alert("提示", "请选择邮寄方式！");
			return false;
	  }
        if( postTypes[0].checked){ //检查邮政投递的方式是否已选
		    for(var i=0;i<postContent.length;i++){
	      		if (postContent[i].checked){
	      		    check=true;
	      		    break;
	      		}
	  	    }
	       if (!check){
	    	   MessageBox.alert("提示", "请选择邮政投递的内容！");
			   return false;
	       }
	 }
		
		check=false;
		if( postTypes[1].checked){//检查eamil投递的方式是否已选
		    for(var i=0;i<emailContent.length;i++){
	      		if (emailContent[i].checked)
	      		{
	      		    check=true;
	      		    break;
	      		}
      	   }
      	
      	  if (!check){
      		MessageBox.alert("提示", "请选择email投递的内容！");
			 return false;
      	  }
		}
		if(!verifyAll(obj)) return false;		
		if(!faxChecked()) return false;
		
		if($("#postinfo_REPAIR_REASON")==null || $("#postinfo_REPAIR_REASON").val() =="" )
	    {
			MessageBox.alert("提示", "请选择补寄原因！");
	          $("#postinfo_REPAIR_REASON").focus();
			  return false;
	    }

		return true;	 
    }    
}
//邮寄标志值
function isPostTagChg()
{
	var tagValue = $("#mySwitch").val();
	if("off" == tagValue){
		$("#postinfo_POST_TAG").val("1");
	}else{
		$("#postinfo_POST_TAG").val("0");
	}
}

//根据界面上的勾选值 来拼接
function getPostValue()
{      
	    var postTypeStr="";
        var emailContentStr="";
    	var postContentStr="";
    	var mmsContentStr="";
    	//选择了 邮政投递的方式
    	var postTypes =  $("*[name='POST_TYPESET']");
	    var postContent = $("*[name='POSTTYPE_CONTENT']");
        var emailContent = $("*[name='EMAILTYPE_CONTENT']");
        
        for(var i=0;i<postTypes.length;i++)
    	{
    		if (postTypes[i].checked)
    		{
    			postTypeStr=postTypeStr+postTypes[i].value;
    		}
    	}
        
	    for(var i=0;i<postContent.length;i++)
    	{
    		if (postContent[i].checked)
    		{
    		    postContentStr=postContentStr+postContent[i].value;
    		}
    	}
    	
    	 for(var i=0;i<emailContent.length;i++)
    	{
    		if (emailContent[i].checked)
    		{
    		    emailContentStr=emailContentStr+emailContent[i].value;
    		}
    	}
    	$('#postinfo_POST_TYPE').val(postTypeStr);
		$('#postinfo_POST_CONTENT').val(postContentStr);
		$('#postinfo_EMAIL_CONTENT').val(emailContentStr);
}
	
function checkFinal(obj){
	if(!checkInfo(obj)) return false;	
	getPostValue();
	return true;
};

/**
 * 控制显示隐藏区
 */
$(function(){
	$("#mySwitch").change(function(){
		if(this.value == "on") {
			$("#showPart").css('display', '');
		} else {
			$("#showPart").css('display', 'none');
		}
	});
});