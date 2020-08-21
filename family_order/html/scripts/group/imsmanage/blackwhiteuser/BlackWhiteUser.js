function isNumber(){
	var obj = getElement("cond_SERIAL_NUMBER").value;
	var reg= /^([1-9]|(0[.]))[0-9]{0,}(([.]*\d{1,2})|[0-9]{0,})$/; 
	if (null == obj || '' == obj ){
		alert('请输入服务号码');
		return false;
	}
	if (!reg.test(obj) ){
	alert('请输入数字');
		return false;
	}
	if(obj.length!=11){
	 alert('服务号码必须是11位');
	 return false;
	}
	
	return true;
} ;
 function changeQueryType() {
 	
	   var choose=getElement("cond_USER_TYPE_CODE").value;
       if (choose=="QB") //全局级黑名单
       {
          getElement("bGroupId").style.display = "none";
          getElement("cond_GROUP_ID").value ="";
       }
       else if (choose=="EB") //全局级黑名单
       {
          getElement("bGroupId").style.display = "block";
       }
   };
function queryBlackUserInfo(){
	var serial_number = getElement("cond_SERIAL_NUMBER").value;
	var user_type_code = getElement("cond_USER_TYPE_CODE").value;
	if(user_type_code =="QB"){
		
		ajaxDirect(this, 'queryUserInfo', '&SERIAL_NUMBER='+serial_number+'&USER_TYPE_CODE='+user_type_code, 'UserInfoPart',false,dealOperStateoptions);
		
	}
	else if (user_type_code=="EB"){
		var group_id = getElement("cond_GROUP_ID").value;
		if (null == group_id || ''== group_id) {
			alert("加入或取消EC级黑名单，集团客户不能为空！");
			return false;
		}
		else {
			ajaxDirect(this, 'queryUserInfo', '&GROUP_ID='+group_id+'&SERIAL_NUMBER='+serial_number+'&USER_TYPE_CODE='+user_type_code, 'UserInfoPart',false,dealOperStateoptions);
		}
	}
	getElement("bsubmit").disabled = false;
};
function dealOperStateoptions()
{ 
 	var usertype=this.ajaxDataset.get(0, "USER_TYPE_CODE");
 	var usertypecode=getElement('cond_USER_TYPE_CODE').value;
 	getElement('USER_TYPE_CODE').value =usertypecode;
 	if("QB"==usertype)//已加入全局级黑名单
 	{
 	  getElement('OPER_STATE').options.length = 0;  //清空操作状态选项
 	  jsAddItemToSelect(getElement('OPER_STATE'), '退出全局级黑名单', 'N');
 	}
 	else if("EB"==usertype)//已加入EC级黑名单
 	{
 	  getElement('OPER_STATE').options.length = 0;  //清空操作状态选项
 	  jsAddItemToSelect(getElement('OPER_STATE'), '退出EC级黑名单', 'L');
 	}
 	else {
 		if("QB"==usertypecode)//未加入全局级黑名单
 		{
 			getElement('OPER_STATE').options.length = 0;  //清空操作状态选项
 	  		jsAddItemToSelect(getElement('OPER_STATE'), '加入全局级黑名单', 'M');
 		}
 		else if("EB"==usertypecode)//未加入EC级黑名单
 		{
 	  		getElement('OPER_STATE').options.length = 0;  //清空操作状态选项
 	  		jsAddItemToSelect(getElement('OPER_STATE'), '加入EC级黑名单', 'K');
 		}
 	}
};

function jsAddItemToSelect(objSelect, objItemText, objItemValue) {        
    //判断是否存在        
    if (jsSelectIsExitItem(objSelect, objItemValue)) {        
        alert("该Item的Value值已经存在");        
    } else {        
        var varItem = new Option(objItemText, objItemValue);      
        objSelect.options.add(varItem);    
    }        
} ;   		

function jsSelectIsExitItem(objSelect, objItemValue) {        
    var isExit = false;        
    for (var i = 0; i < objSelect.options.length; i++) {        
        if (objSelect.options.value == objItemValue) {        
            isExit = true;        
            break;        
        }        
    }        
    return isExit;        
};     
//获取当前格式化后的时间
function getNowFormatDate(){
   var day = new Date();
   var CurrentDate = "";
   var Year = day.getFullYear();
   var Month = day.getMonth()+1;
   var Day = day.getDate();
   CurrentDate += Year + "-";
   
   if (Month >= 10 ){
    	CurrentDate += Month + "-";
   }
   else{
   		CurrentDate += "0" + Month + "-";
   }
	if (Day >= 10 ){
    	CurrentDate += Day ;
   }
   else{
    	CurrentDate += "0" + Day ;
   } 
   return CurrentDate;
};

function checkExpertDate() {
	var edate = getElement("EXPECT_TIME").value;
	var today = getNowFormatDate(); 
	var startDate = new Date(today.replace("-",",")).getTime() ;
	var endDate = new Date(edate.replace("-",",")).getTime() ;

	if( startDate > endDate ) 
	{  
		alert("期望生效时间不能小于当前时间，请重新选择期望生效时间！");
  		return false;
	}
	return true;
};
