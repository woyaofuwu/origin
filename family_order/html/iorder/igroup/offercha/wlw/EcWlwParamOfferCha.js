function validateParamPage(methodName) {
	if(methodName=="CrtUs"||methodName=="ChgUs"){
		var mgrPhone=$("#pam_MGR_PHONE").val();
		//需要考虑其他产品调用这个JS的兼容性,别只考虑仅需做校验的产品
		if(mgrPhone!=""&& typeof(mgrPhone) != "undefined"){
            if (mgrPhone.length < 6 ){
                $.validate.alerter.one($("#pam_MGR_PHONE")[0],"管理员电话最小长度不能低于6！");
                $("#pam_MGR_PHONE").focus();
                return false;
            }
            if (mgrPhone.length > 20){
                $.validate.alerter.one($("#pam_MGR_PHONE")[0],"管理员电话最大长度不能超过20个字符！");
                $("#pam_MGR_PHONE").focus();
                return false;
            }
            if(isNaN($("#pam_MGR_PHONE").val())){
                $.validate.alerter.one($("#pam_MGR_PHONE")[0],"管理员电话必须为数字！");
                $("#pam_MGR_PHONE").focus();
                return false;
			}
		}
	}
	return true;
}
//校验不同操作状态下的参数值
function checkSubForOperType(){
	debugger;
	var offerId = $("#prodDiv_OFFER_ID").val();
	var offercode = $("#prodDiv_OFFER_OPER_CODE").val();
	
	var UserClassOperType = $("#UserClassOperType").val();
	var UserClass = $("#UserClass").val();
	var UserAccount = $("#UserAccount").val();
	var GroupAccount = $("#GroupAccount").val();
	
	if(offerId=="120099011015"){	
		if(offercode == "3"){

				if(UserClassOperTypeFrist == null||UserClassOperTypeFrist == ""){
					if(UserClassOperType !="1"){
						$.validate.alerter.one($("#UserClassOperType")[0],"首次设置集团信息时，“企业客户成员级别操作类型” 必须设置为添加！");
						return false;
					}else if(UserClass !="999"){
						$.validate.alerter.one($("#UserClass")[0],"首次设置集团信息时，“企业客户成员级别” 必须填写999！");
						return false;	
					}else if(UserAccount != "0"){
						$.validate.alerter.one($("#UserAccount")[0],"首次设置集团信息时，“企业客户成员个人通话阀值” 必须填写0！");
						return false;
				     }else {
				    	 return true;
				     }				
				}else {
						return true;
				}	
			}else if(offercode == "0"){
				 if(GroupAccount !="0"){
					 $.validate.alerter.one($("#GroupAccount")[0],"新增集团智能网语音服务时，“通话阀值” 必须填0！");
					 return false;
				 }else{
					 return true;
				 }
			}else {
				return true;
			}
		
		  }else {
				return true;
		  }
}
//提交
//有多个服务调用这个JS,有特殊的校验请记得加判断,不要影响其他服务
function checkSub(obj)
{
    var operType=$("#cond_OPER_TYPE").val();
    if(!validateParamPage(operType))
    	return false;
	if(!submitOfferCha())
		return false; 
	if(!checkSubForOperType()){
		return false; 
	}
	backPopup(obj);
}