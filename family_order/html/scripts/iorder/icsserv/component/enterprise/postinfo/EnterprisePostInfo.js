	function initPost(userId){
		var postUserId = $("#post_USER_ID").val();
		if(postUserId != userId){
	    	$.beginPageLoading();
	        ajaxPost(null, null, '&ajaxListener=queryPostInfo&USER_ID='+userId, "postInfoMgrPart", function (data) {
	        	$("#post_USER_ID").val(userId);
	            // success
	        	$.endPageLoading();
	        }, function (errCode, errDesc, errStack) {
	            // faild
	        	$.endPageLoading();
	        	$.MessageBox.error("错误提示", "操作失败！", "", null, errStack);
	        });
		}
	}
	function postSubmit(el) {
		
		if(!isPostInfo()){
			return;
		}

        hidePopup($(el).closest(".c_popup").attr("id"));
	}
	
	function changePostTag() {
    	var type = $("#post_POST_TAG").val();
    	if (type == "0") {
        	$("#IS_POST_PART").css("display","none");
    		$("#POST_CONTENT_LABEL").attr("class","");
    		$("#POST_TYPESET_LABEL").attr("class","");
    		$("#POST_CYC_LABEL").attr("class","");
    		$("#POST_NAME_LABEL").attr("class","");
    	}else if (type == "1"){
        	$("#IS_POST_PART").css("display","");

    		$("#POST_CONTENT_LABEL").attr("class","e_required");
    		$("#POST_TYPESET_LABEL").attr("class","e_required");
    		$("#POST_CYC_LABEL").attr("class","e_required");
    		$("#POST_NAME_LABEL").attr("class","e_required");
    	}
    	this.afterShowPopupPost();
    }

	function afterShowPopupPost() {
    	// 滚动条刷新
    	var scroll = window["postDiv"];
    	scroll.refresh();
    }

	function isPostInfo() {

    	if($('#post_POST_TAG').val() == '1')
    	{
    		getPostContent();
    		if($('#post_POST_CONTENT').val() == '')
    		{
    			MessageBox.alert('提示','请填写邮寄内容!');
    			return false;
    		}
    		if(!getPostType())
    			return false;
    		if($('#post_POST_TYPESET').val() == '')
    		{
    			MessageBox.alert('提示','请填写邮寄方式!');
    			return false;
    		}
    		if($('#post_POST_CYC').val() == '')
    		{
    			MessageBox.alert('提示','请填写邮寄周期!');
    			return false;
    		}
    		if($('#post_POST_NAME').val() == '')
    		{
    			MessageBox.alert('提示','请填写邮寄名称!');
    			return false;
    		}
    	}
    	return true;
    }
	function getPostContent() {
		var POST_CONTENT = $('#post_POST_CONTENT');
		POST_CONTENT.val("");
		if($("#post_POST_CONTENT_0").attr("checked"))
		{
			POST_CONTENT.val(POST_CONTENT.val() + "0");
		}
		if($("#post_POST_CONTENT_1").attr("checked"))
		{
			POST_CONTENT.val(POST_CONTENT.val() + "1");
		}
		if($("#post_POST_CONTENT_2").attr("checked"))
		{
			POST_CONTENT.val(POST_CONTENT.val() + "2");
		}
		if($("#post_POST_CONTENT_3").attr("checked"))
		{
			POST_CONTENT.val(POST_CONTENT.val() + "3");
		}
    }

	function getPostType() {
		var POST_TYPESET = $('#post_POST_TYPESET');
		POST_TYPESET.val("");
		if($("#post_POST_TYPESET_0").attr("checked"))
		{
			if($("#post_POST_ADDRESS").val()==''){
				MessageBox.alert('提示','邮寄地址不能为空！');
				return false;
			}
			if($("#post_POST_CODE").val()==''){
				MessageBox.alert('提示','邮寄邮编不能为空！');
				return false;
			}
			
			//检查邮寄邮编是否为数字
			if(g_IsDigitPost($("#post_POST_CODE").val())==false){
				MessageBox.alert('提示','邮寄邮编必须为数字');
				return false;
			}
			
			POST_TYPESET.val(POST_TYPESET.val() + "0");
		}
		if($("#post_POST_TYPESET_1").attr("checked"))
		{
			if($("#post_FAX_NBR").val()==''){
				MessageBox.alert('提示','传真电话不能为空！');
				return false;
			}
			POST_TYPESET.val(POST_TYPESET.val() + "1");
		}
		if($("#post_POST_TYPESET_2").attr("checked"))
		{
			if($("#post_EMAIL").val()==''){
				MessageBox.alert('提示','E-mail地址不能为空！');
				return false;
			}
			POST_TYPESET.val(POST_TYPESET.val() + "2");
		}
		
		return true;
    }
	function checkPostNum(obj) {
    	var num1 = $("#askPrint_RSRV_STR2").val();
    	var num2 = $("#askPrint_RSRV_STR5").val();
    	
    	if(!$.verifylib.checkMbphone(obj.value)){
    		MessageBox.alert('提示',obj.getAttribute("desc") + "必须为手机号码!");
    		$("#"+obj.id).val("");
    		obj.focus();
    		return false;
    	}
    	
    	if("" != num1 && num1 == num2) {
    		MessageBox.alert('提示',"两个催缴号码不能相同，请重新输入催缴人手机号！");
    		$("#"+obj.id).val("");
    		obj.focus();
    		return false;
    	}

    	$.beginPageLoading();
        ajaxPost(null, null, '&ajaxListener=queryMemberInfo&cond_SERIAL_NUMBER='+obj.value, "refreshPart", function (data) {
            // success
        	$.endPageLoading();
    		var flag = data.get("flag");
			if(flag == "false"){
				MessageBox.alert('提示',"此号码不是正常用户，请重新输入催缴人手机号！");
				$("#"+obj.id).val("");
				obj.focus();
			}
        }, function (errCode, errDesc, errStack) {
            // faild
        	$("#"+obj.id).val("");
        	$.endPageLoading();
        	$.MessageBox.error("错误提示", "操作失败！", "", null, errStack);
        });
    }
  //校验是否为数字类型
	function g_IsDigitPost(s){
    	 if(s==null){
    	 	return false;
    	 }
    	 if(s==''){
    	 	return true;
    	 }
    	 s=''+s;
    	 if(s.substring(0,1)=='-' && s.length>1){
    	 	s=s.substring(1,s.length);
    	 }
    	 var patrn=/^[0-9]*$/;
    	 if (!patrn.exec(s)){
    	 	return false;
    	 }
    	 return true;
    }
