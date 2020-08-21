
function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString(), 'PostInfoPart', function(){
		initModifyPostInfo();
		var postName = $('#postinfo_POST_NAME').val();
		if(postName==""){
			$('#postinfo_POST_NAME').val(data.get("CUST_INFO").get("ORIGIN_CUST_NAME"))
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
}

//页面初始化方法
function initModifyPostInfo()
{
	$("#postinfo_POST_TAG").attr('disabled',false);
	$("#postinfo_REMARK").attr('disabled',false);
     $("#OLD_POST_TYPE").val("0");//记录用户以前是否选择了 邮政邮递 和 EMAIL 移动E 信的方式
     $("#OLD_EMAIL_TYPE").val("0");
     $("#OLD_MMS_TYPE").val("0");
   //如果邮寄标志有效 则展现邮递资料相关编辑框
    if ($("#postinfo_POST_TAG").val() =="1"){  
    	
        $("#postInfoDiv").css('display','');
        $("#postMothodDiv").css('display','');
        $("#postContentDiv").css('display','');
	    $("#emailContentDiv").css('display','');
	    $("#mmsContentDiv").css('display','');
	    $("#postInfoDiv").css('display','');
		
		var postTypes =  $("*[name='POST_TYPESET']");
		var postContent =  $("*[name='POSTTYPE_CONTENT']");
        var emailContent =  $("*[name='EMAILTYPE_CONTENT']");
        var mmsContent =  $("*[name='MMSTYPE_CONTENT']");
			
		if (postTypes[0].checked && (postContent[0].checked || postContent[1].checked)){//选择了邮政投递，同时存在一个选择 账单或者发票
		    
			 $("#postContentDiv").attr('disabled',false);
			 $("#span_POST_CONTENT").addClass("e_required");
			 $("#span_address").addClass("e_required");	  			 
			 $("#postinfo_POST_ADDRESS").attr("nullable","no");  			 
			 postContent[0].disabled=true;		 
			 $("#OLD_POST_TYPE").val("1");//用户已经选择过邮政投递的方式
			 
			//REQ201406170016 关于暂停邮寄月结发票业务
			if(!postContent[1].checked)//如果选择了邮政投递， 但没有选中发票，则将发票置为不可选
			{
				postContent[1].disabled = true;
			}			
			if(postContent[1].checked)//如果选择了邮政投递， 并选中发票
			{
				postContent[1].disabled = false;//则让发票置可取消选中
			}
			//REQ201406170016 关于暂停邮寄月结发票业务
			     
		 }else{
			 postTypes[0].disabled = true;//若未选择邮政投递，则不能再选择邮政投递 songlm REQ201406170016 关于暂停邮寄月结发票业务 冼乃捷测试提出的完善，结合了 REQ201304120008关于屏蔽办理邮寄纸质账单的需求，即未选择“邮政投递”的，不能再选择“邮政投递”
		     $("#postContentDiv").attr('disabled',true);
			 $("#span_POST_CONTENT").attr("className","");
			 $("#span_address").attr("className","");	 
			 $("#postinfo_POST_ADDRESS").attr("nullable","yes"); 
		 }
		 
		 if (postTypes[1].checked){//选择了EMAIL投递方式
			 
		     $("#emailContentDiv").attr('disabled',false);
			 $("#span_EMAIL_CONTENT").addClass("e_required");
			 $("#span_email").addClass("e_required");
			 $("#postinfo_EMAIL").attr("nullable","no");
			 emailContent[0].disabled=true;
			 $("#OLD_EMAIL_TYPE").val("1");//用户已经选择过EMAIL移动E信的方式
		}else{
			 $("#emailContentDiv").attr('disabled',true);
			 $("#span_EMAIL_CONTENT").attr("className","");
			 $("#span_email").attr("className","");
			 $("#postinfo_EMAIL").attr("nullable","yes"); 
			 $("#OLD_EMAIL_TYPE").val("0");//用户已经选择了EMAIL的方式

		}
		 
		 if (postTypes[2].checked)//选择了MMS投递方式
		{   
		     $("#mmsContentDiv").attr('disabled',false);
			 $("#span_MMS_CONTENT").addClass("e_required");
			 mmsContent[0].disabled=true;
			 $("#OLD_MMS_TYPE").val("1");//用户已经选择过MMS的方式
		}else{
			 $("#mmsContentDiv").attr('disabled',true);
			 $("#span_MMS_CONTENT").attr("className","");
			 $("#OLD_MMS_TYPE").val("0");//用户已经选择了MMS的方式
		} 
    }else{
    	var postTypes =  $("*[name='POST_TYPESET']");
        postTypes[0].disabled = true;//新选择邮寄，不能选择邮政投递 songlm REQ201406170016 关于暂停邮寄月结发票业务 冼乃捷测试提出的完善，结合了 REQ201304120008关于屏蔽办理邮寄纸质账单的需求，即未选择“邮政投递”的，不能再选择“邮政投递”
    }
}
//改变邮寄标志的方法
function postSelect(){	
          //不选或无效
		if($("#postinfo_POST_TAG").val() =="0" || $("#postinfo_POST_TAG").val() ==""){
		
			$("#postContentDiv").css('display','none');
			$("#emailContentDiv").css('display','none');
			$("#mmsContentDiv").css('display','none');
		    $("#postInfoDiv").css('display','none');
		    $("#postMothodDiv").css('display','none');
			$("#postinfo_POST_ADDRESS").attr("nullable","yes"); 
			$("#postinfo_EMAIL").attr("nullable","yes"); 
			$("#postinfo_POST_NAME").attr("nullable","yes"); 			
		}
		else{ 
			$("#postMothodDiv").css('display','');
		    $("#postContentDiv").css('display','');
		    $("#emailContentDiv").css('display','');
		    $("#mmsContentDiv").css('display','');
		    $("#postInfoDiv").css('display','');
			
			var postTypes = $("*[name='POST_TYPESET']");
            $("#postinfo_POST_NAME").attr("nullable","no");
            
			if (postTypes[0].checked)//选择了邮政投递
    		{
    			 $("#postContentDiv").attr('disabled',false);
    			 $("#span_POST_CONTENT").addClass("e_required");
    			 $("#span_address").addClass("e_required");	 
    			 $("#postinfo_POST_ADDRESS").attr("nullable","no");
    		 }else{
    		     $("#postContentDiv").attr('disabled','true');
    			 $("#span_POST_CONTENT").attr("className","");
    			 $("#span_address").attr("className","");	 
    			 $("#postinfo_POST_ADDRESS").attr("nullable","yes"); 
    		 }
    		 
    		 if (postTypes[1].checked)//选择了EMAIL投递方式
    		{   
    		     $("#emailContentDiv").attr('disabled',false);
    			 $("#span_EMAIL_CONTENT").addClass("e_required");
    			 $("#span_email").addClass("e_required");
    			 $("#postinfo_EMAIL").attr("nullable","no"); 
    		}else{
    			 $("#emailContentDiv").attr('disabled','true');
    			 $("#span_EMAIL_CONTENT").attr("className","");
    			 $("#span_email").attr("className","");
    			 $("#postinfo_EMAIL").attr("nullable","yes"); 
    
    		}
    		 
    		if (postTypes[2].checked)//选择了MMS投递方式
     		{   
     		     $("#mmsContentDiv").attr('disabled',false);
     			 $("#span_MMS_CONTENT").addClass("e_required");
     		}else{
     			 $("#mmsContentDiv").attr('disabled','true');
     			 $("#span_MMS_CONTENT").attr("className","");
     		}
    		if($("#postinfo_POST_NAME").value == ""){
			  $("#postinfo_POST_NAME").val( $("#CUST_NAME").val() );
			}
					
		}
}

/*处理不同邮寄内容，不同校验*/
function setPostTypeChange(objCheck){
      var postContent = $("*[name='POSTTYPE_CONTENT']");//邮政投递内容
      var emailContent = $("*[name='EMAILTYPE_CONTENT']");//EMAIL 投递内容
      var mmsContent = $("*[name='MMSTYPE_CONTENT']");//MMS 投递内容
      var postTypes = $("*[name='POST_TYPESET']");//投递方式
	if(objCheck.checked){//checked
		if(objCheck.value==0){//邮政投递
			   //songlm 20140704 更正REQ201304120008关于屏蔽办理邮寄纸质账单的需求时 遗留的bug 为了更好的满足REQ201406170016 关于暂停邮寄月结发票业务的需求----start---
            /*	
    		   if($("#OLD_EMAIL_TYPE").val() =="1" && !postTypes[1].checked)//以前选择过E-mail,但是界面上现在的EMAIL移动E信 没有勾选着
		       {
    			   postTypes[1].disabled=true;//不允许再选择EMAIL移动E信
		       }
    		   if($("#OLD_MMS_TYPE").val() =="1" && !postTypes[2].checked)//以前选择过MMS,但是界面上现在的MMS没有勾选着
		       {
    			   postTypes[2].disabled=true;//不允许再选择MMS
		       }
		    */
		       //songlm 20140704 更正REQ201304120008关于屏蔽办理邮寄纸质账单的需求时 遗留的bug 为了更好的满足REQ201406170016 关于暂停邮寄月结发票业务的需求----end---
		         
  		       $("#postContentDiv").attr('disabled',false);
      		   $("#span_POST_CONTENT").addClass("e_required");
      		   $("#span_address").addClass("e_required");
      		   $("#postinfo_POST_ADDRESS").attr("nullable","no"); 
      		   $("#postinfo_POST_NAME").attr("nullable","no");       		 
               postContent[0].disabled=true;
    		   postContent[1].disabled=true;//邮政投递的发票方式的Checkbox的不可选状态生效，即不可选  @up by songlm 20140704 REQ201406170016 关于暂停邮寄月结发票业务
		}
		if(objCheck.value==2){//移动E信
        		  $("#emailContentDiv").attr('disabled',false);
        		  $("#span_EMAIL_CONTENT").addClass("e_required");//email移动E信 状态设为必填
        		  $("#span_email").addClass("e_required");		 
        		  $("#postinfo_EMAIL").attr("nullable","no"); 	
        		 
        		  emailContent[0].checked=true;
        		  emailContent[0].disabled=true;
		}
		if(objCheck.value==3){//MMS
    		  $("#mmsContentDiv").attr('disabled',false);
    		  $("#span_MMS_CONTENT").addClass("e_required");//email移动E信 状态设为必填
    		  mmsContent[0].checked=true;
    		  mmsContent[0].disabled=true;
		}
	}else{//no checked
		if(objCheck.value==0){//邮政投递
		     $("#postContentDiv").attr('disabled',true);
    		 $("#span_POST_CONTENT").attr("className","");
    		 $("#span_address").attr("className","");
    		 $("#postinfo_POST_ADDRESS").attr("nullable","yes"); 
    		 $("#postinfo_POST_NAME").attr("nullable","yes"); 
    		 	 
             var elements = $("*[name='POSTTYPE_CONTENT']");
        	 for(var i=0;i<elements.length;i++)
        	 {
        	 	elements[i].checked=false;
        	 }
        	 postTypes[1].disabled=false;
        	 postTypes[0].disabled=true;//若未选择邮政投递，则不能再选择邮政投递 songlm REQ201406170016 关于暂停邮寄月结发票业务 冼乃捷测试提出的完善，结合了 REQ201304120008关于屏蔽办理邮寄纸质账单的需求，即未选择“邮政投递”的，不能再选择“邮政投递”
        	
		}
		if(objCheck.value==2){//移动E信
			 $("#emailContentDiv").attr('disabled',true);
    		 $("#span_EMAIL_CONTENT").attr("className","");//email移动E信 状态设为必填
    		 $("#span_email").attr("className","");		
    		 $("#postinfo_EMAIL").attr("nullable","yes"); 	
    		 
    		 var elements = $("*[name='EMAILTYPE_CONTENT']");
        	 for(var i=0;i<elements.length;i++)
        	 {
        	 	elements[i].checked=false;
        	 }		
		}
		if(objCheck.value==3){//MMS
			 $("#mmsContentDiv").attr('disabled',true);
	   		 $("#span_MMS_CONTENT").attr("className","");//MMS状态设为必填
	   		 var elements = $("*[name='MMSTYPE_CONTENT']");
	       	 for(var i=0;i<elements.length;i++)
	       	 {
	       	 	elements[i].checked=false;
	       	 }		
		}
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
    alert("传真号码的数字位不能是同一个数字");
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
          alert("请选择邮寄标志！");
		  return false;
    }else if ($("#postinfo_POST_TAG").val()=="0")
    { 
          return true;
    }else
    {   
        var postTypes =$("*[name='POST_TYPESET']");
        
        var postContent = $("*[name='POSTTYPE_CONTENT']");
        
        var emailContent = $("*[name='EMAILTYPE_CONTENT']");
        
        var mmsContent = $("*[name='MMSTYPE_CONTENT']"); 
        
      if(!postTypes[0].checked && !postTypes[1].checked && !postTypes[2].checked){
			alert("请选择邮寄方式！");
			return false;
	  }
      //REQ201406170016 关于暂停邮寄月结发票业务  结合  REQ201304120008关于屏蔽办理邮寄纸质账单的需求   不再检查邮政投递的方式是否已选@up by songlm 20140704 --------start----------------------
	/*
	  if( postTypes[0].checked){ //检查邮政投递的方式是否已选
		    for(var i=0;i<postContent.length;i++){
	      		if (postContent[i].checked){
	      		    check=true;
	      		    break;
	      		}
	  	    }
	       if (!check){
	      	   alert("请选择邮政投递的内容！");
			   return false;
	       }
	 }
	*/
	  //REQ201406170016 关于暂停邮寄月结发票业务  结合  REQ201304120008关于屏蔽办理邮寄纸质账单的需求   不再检查邮政投递的方式是否已选@up by songlm 20140704 --------end----------------------
			
		var check=false;
		if( postTypes[1].checked){//检查eamil投递的方式是否已选
		    for(var i=0;i<emailContent.length;i++){
	      		if (emailContent[i].checked)
	      		{
	      		    check=true;
	      		    break;
	      		}
      	   }
      	
      	  if (!check){
      	     alert("请选择email投递的内容！");
			 return false;
      	  }
		}
		
		check=false;
		if( postTypes[2].checked){//检查mms投递的方式是否已选
		
		    for(var i=0;i<mmsContent.length;i++){
	      		if (mmsContent[i].checked)
	      		{
	      		    check=true;
	      		    break;
	      		}
		    }
	      	if (!check)
	      	{
	      	   alert("请选择mms投递的内容！");
				   return false;
	      	}
		}
		if(!verifyAll(obj)) return false;
		if(!faxChecked()) return false;

		return true;	 
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
        var mmsContent = $("*[name='MMSTYPE_CONTENT']");
        
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
    	 
    	 for(var i=0;i<mmsContent.length;i++)
     	{
     		if (mmsContent[i].checked)
     		{
     		    mmsContentStr=mmsContentStr+mmsContent[i].value;
     		}
     	}
    	$('#postinfo_POST_TYPE').val(postTypeStr);
		$('#postinfo_POST_CONTENT').val(postContentStr);
		$('#postinfo_EMAIL_CONTENT').val(emailContentStr);
		$('#postinfo_MMS_CONTENT').val(mmsContentStr);
}
//songlm REQ201406170016 关于暂停邮寄月结发票业务 增加邮政投递内容的触发函数
function ChangePostContext(objCheck)
{
	if((objCheck.value==2)&&(!objCheck.checked))//如果取消选中，并取消的是发票
	{
		var postContent = $("*[name='POSTTYPE_CONTENT']");//邮政投递的Checkbox
		postContent[1].disabled = true;

		if(!postContent[0].checked)//如果取消发票的同时，账单处于未被选中，则将邮寄方式：邮寄投递置为取消并不可选
		{
    		 $("#postContentDiv").attr('disabled',true);
    		 $("#span_POST_CONTENT").attr("className","");
    		 $("#span_address").attr("className","");
    		 $("#postinfo_POST_ADDRESS").attr("nullable","yes"); 
    		 $("#postinfo_POST_NAME").attr("nullable","yes"); 
    		 
        	 var postTypes =  $("*[name='POST_TYPESET']");//投递方式
        	 postTypes[0].checked=false;
        	 postTypes[0].disabled=true;//若未选择邮政投递，则不能再选择邮政投递 songlm REQ201406170016 关于暂停邮寄月结发票业务 冼乃捷测试提出的完善，结合了 REQ201304120008关于屏蔽办理邮寄纸质账单的需求，即未选择“邮政投递”的，不能再选择“邮政投递”
        	
		}
	}
}
//songlm REQ201406170016 关于暂停邮寄月结发票业务 增加邮政投递内容的触发函数
	
function checkFinal(obj){
	if(!checkInfo(obj)) return false;	
	getPostValue();
	return true;
};