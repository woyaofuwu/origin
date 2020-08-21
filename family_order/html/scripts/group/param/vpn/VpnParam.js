
function init() {    
	var METHOD_NAME = $('#METHOD_NAME').val();
	if(METHOD_NAME=='CrtUs'||METHOD_NAME=='ChgUs'){ 
	    changeTagValue();   //初始化动态列表拼值
	    var vpn_scare = $('#pam_VPN_SCARE_CODE').val(); 
	    if(vpn_scare == 2 ){
	       $('#dialtype').css('display','');
	       $('#pam_VPN_NO').attr('disabled',true);
	       $('#pam_VPN_NO_Box_Text').attr('disabled',true);	
	    }else{
	       $('#dialtype').css('display','none');	
	    }
	}else if(METHOD_NAME=='CrtMb'||METHOD_NAME=='ChgMb'){   
		 initMemParamInfo();   //成员参数界面初始化
	}
   
}

//成员初始化方法
function initMemParamInfo() {  
    	//不是跨省集团，隐藏跨省资费
   	var vpnscare = $('#VPN_SCARE_CODE').val();  
   	if(vpnscare != 2){  
   		if($('#80000103')!= null ) 
   			$('li[title^=80000103]').css('display','none');
   	}
	var METHOD_NAME = $('#METHOD_NAME').val();
   	var RIGHT_CODE = $('#RIGHT_CODE').val();
   	var service_id = $('#SERVICE_ID').val();  
   	if(METHOD_NAME == "CrtMb") {
   		if(RIGHT_CODE == "yes" && service_id == "861"){
   			Changeinput(1);
   		}
   		// j2ee  pam_JOIN_IN已经去除，这里没用了
//   		var jion_in = $('#pam_JOIN_IN').val();
//   		if(hasPriv("GROUPMENBER_VPMN_PRV") && jion_in =="0"){
//   			$('#pam_JOIN_IN").css('disabled','false');
//   		}
//   		else {
//   			$('#pam_JOIN_IN").css('disabled','true');
//   		}
   		 
   	}
  
}

function Changeinput(obj) {  
	if(obj==1){
		 Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
		    		'&MEB_USER_ID='+$('#MEB_USER_ID').val()+'&GRP_USER_ID='+$('#GRP_USER_ID').val()+'&METHOD_NAME=creatshortcode&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.MemParamInfo',creatShortCodeAfter,errafterCheck);
	}
	else{
		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
		if( shortNumber !=""){
			del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortNumber);
			deleteRes(del);
			$('#SHORT_NUMBER_PARAM_INPUT').val("");
		}
		$('#pam_SHORT_CODE').attr('disabled',false);;
		$('#pam_SHORT_CODE').val('');
		$('#validButton').css('display','');
	}
}
function creatShortCodeAfter(data){
	$.endPageLoading();
	var result = data.get("RESULT"); 
	if(result==true||result=='true'){       
		GetSHORT_CODE(data.get("SHORT_CODE"));
	}else{   
	   var message=data.get("ERROR_MESSAGE");
	   $.showWarnMessage(message);  
	}
}

function GetSHORT_CODE(shortCode){
	var SHORT_CODE = shortCode;
	$('#pam_SHORT_CODE').val(SHORT_CODE); 
	if ($('#pam_SHORT_CODE').val()== "") {
		$('#q1a1')[0].checked = false;     //自动
		$('#q1a1').css('disabled','true');
		$('#q1a2')[0].checked = true;      //人工
		$('#validButton').css('display','');
		alert("\u81EA\u52A8\u751F\u6210\u77ED\u53F7\u7801\u5931\u8D25\uFF0C\u8BF7\u624B\u52A8\u8F93\u5165\uFF01");
	} else {
		$('#pam_SHORT_CODE').attr('disabled',true);
		$('#validButton').css('display','none');
		displayShort(SHORT_CODE);
//		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
//		if(shortNumber != ""){ 
//			var del = $.DataMap();
//			del.put("RES_TYPE_CODE","S");
//			del.put("RES_CODE",shortNumber);
//			deleteRes(del);
//		} 
//		var obj =$.DataMap();
//		obj.put("RES_TYPE_CODE","S");
//		obj.put("RES_CODE",SHORT_CODE);
//		obj.put("CHECKED","true");
//		obj.put("DISABLED","true");
//		insertRes(obj);
//		$('#SHORT_NUMBER_PARAM_INPUT').val(SHORT_CODE); 
	}
}
  
function displayShort(shortCode){ 
		var shortNumberInput= $('#SHORT_NUMBER_PARAM_INPUT').val();
		var shortNumber = $('#pam_SHORT_CODE').val(); 
		if(shortNumberInput != "")	{
			if(shortNumber != shortNumberInput) { 
				var del = $.DataMap();
				del.put("RES_TYPE_CODE","S");
				del.put("RES_CODE",shortNumberInput);
				deleteRes(del);
			}
		} 
		if(shortNumber != shortNumberInput) { 
			var obj =$.DataMap();
			obj.put("RES_TYPE_CODE","S");
			obj.put("RES_CODE",shortCode);
			obj.put("CHECKED","true");
			obj.put("DISABLED","true");
			insertRes(obj);
			$('#SHORT_NUMBER_PARAM_INPUT').val(shortCode);
		} 
		 
}

function querySCPInfos(){  
	var vpnAttr = $('#pam_VPN_GRP_ATTR').val(); 
	if(vpnAttr==''){ 
		return false;
	} 
//    ajaxDirect('group.param.vpn.UserParamInfo','querySCPInfos','&VPN_GRP_ATTR='+this.value,'SCPart',false,afterAction);
	$.beginPageLoading();
    Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
    		'&VPN_GRP_ATTR='+vpnAttr+'&METHOD_NAME=querySCPInfos&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.UserParamInfo',afterCheck,errafterCheck);	
    
}
 
 
function afterCheck(data){
  $.endPageLoading();
  var result = data.get("RESULT");  
  if(result==true||result=='true'){       
  	 $("#pam_SCP_CODE").empty();
  	 $("#pam_SCP_CODE").append("<option value='"+data.get("DATA_ID")+"'>"+data.get("DATA_NAME")+"</option>");  //添加一项option
  }else{   
     var message=data.get("ERROR_MESSAGE");
     $.showWarnMessage(message);  
  }
} 
function errafterCheck(e,i)
{   
	$.endPageLoading();
    $.showErrorMessage(e+":"+i);
}

 
function changeTagValue() { 
	var dset = $.table.get("VpmnParams");
	var ds = dset.getTableData("X_TAG,SEQ,TAG_VALUE,TAG_CODE",true); 
	if (ds.length>0) {
	    for (var i=0;i<ds.length;i++) {
	        var map = ds.get(i);
	        map.put('TAG_VALUE', $('#SELECT_TAG_VALUE_'+i).val()); 
	    }
	    $('#pam_NOTIN_VPN_TAG_SET').val(ds);
	} 
}
 
function createserialnumber() { 
	var work_type_code = $('#pam_WORK_TYPE_CODE').val();;
	if(null == work_type_code || "" == work_type_code || work_type_code =="-----选择-----"){
		alert("\u0056\u0050\u004D\u004E\u96C6\u56E2\u7C7B\u578B\u4E0D\u80FD\u4E3A\u7A7A\uFF01");
		return false;
	} 
	var serialNumber = $('#SERIAL_NUMBER').val();
	if(serialNumber != "" && serialNumber != null)	{
		var del = $.DataMap();
		del.put("RES_TYPE_CODE", "L");
		del.put("RES_CODE",serialNumber);
		deleteRes(del);
	}
	$.beginPageLoading();
//	ajaxSubmit('group.param.vpn.UserParamInfo', 'createSerialNumber', '&WORK_TYPE_CODE='+work_type_code, '','',null,afterValidchk)
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
    		'&WORK_TYPE_CODE='+work_type_code+'&METHOD_NAME=createSerialNumber&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.UserParamInfo',
    		afterValidchk,errafterCheck);
}

function afterValidchk(data) {  
	$.endPageLoading();
	var result = data.get("RESULT"); 
	  if(result==true||result=='true'){    
		    var vpnno = data.get("VPN_NO"); 
			var obj =$.DataMap();
			obj.put("RES_TYPE_CODE", "L");
			obj.put("RES_CODE",vpnno);
			obj.put("CHECKED","true");
			obj.put("DISABLED","true");
			insertRes(obj);
			$('#SERIAL_NUMBER').val(vpnno);  
			$('#HIDDEN_SERIAL_NUMBER').val(vpnno); 
	  }else{   
	     var message=data.get("ERROR_MESSAGE");
	     $.showWarnMessage(message);  
	  }
	  
}


function checkVpnScare(){
	
	var vpnscare =  $('#pam_VPN_SCARE_CODE').val();
	var oldvpnscare =  $('#pam_OLD_VPN_SCARE_CODE').val();
	var index = $('#pam_VPN_SCARE_CODE').selectedIndex;
	var serial =  $('#SERIAL_NUMBER').val();
	var methodName =  $('#METHOD_NAME').val(); 
	if(vpnscare=='2' && methodName !='CrtUs') {   //全国集团
//	    ajaxDirect(this, 'checkShortNumber', '&SERIAL_NUMBER='+$('#SERIAL_NUMBER').val(), null,null,aftercheckVpnScare);
		$.beginPageLoading(); 
	    Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
	    		'&GRP_USER_ID='+$('#GRP_USER_ID').val()+'&METHOD_NAME=checkShortNumber&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.UserParamInfo',aftercheckVpnScare,errafterCheck);
	}
	else if(vpnscare=='2' && methodName =='CrtUs'){
		  $('#dialtype').css('display','block');
		  if(methodName == 'CrtUs'){
		  	   if(vpnscare == '2'){
		  	   	   if(!confirm('您选择新增跨省V网操作（需要选择VPN号和拨打方式信息），请确认是否继续！')){
		  	   	   	   $('#dialtype').css('display','none');	
		  	   	   	   $('#pam_VPN_SCARE_CODE').val('0');
		  	   	   	}
		  	   	}
		  	} 
	  } 
	  else{
  		$('#dialtype').css('display','none');
  	     //var methodName =  $('#VPN_METHOD_NAME').val();
		  if(methodName == 'ChgUs'){
		      if(vpnscare == '2' && oldvpnscare != '2'  ){
		         	if(!confirm('您选择了本省VPN升级跨省VPN操作，请确认是否继续!')){
		         	  $('#dialtype').css('display','none');	
		         	  $('#defaultDiscnt').css('display','none');
		         	  $('#pam_VPN_SCARE_CODE').val(oldvpnscare);
		         	  return false;
		         	}
		      	    $('#defaultDiscnt').css('display','');
		      }else{
		            $('#defaultDiscnt').css('display','none');
		      }
		      if(vpnscare != '2' && oldvpnscare == '2' ){
		          if(!confirm('您选择了跨省VPN降级本省VPN操作，请确认是否继续!')){
		              $('#dialtype').css('display','');	
		              $('#pam_VPN_SCARE_CODE').val('2');
		             return false;
		           }	
		      }
		  }
		  if(methodName == 'CrtUs'){ 
		  	   if(vpnscare == '2'){
//		  		   if($('#VPN_NO_LIST').val()==''){
//		  			   $.showWarnMessage("提示","您选择新增跨省V网操作,需要选择VPN号，但是VPN号无数据！");  
//		  		   }
		  	   	   if(!confirm('您选择新增跨省V网操作（需要选择VPN号和拨打方式信息），请确认是否继续！')){
		  	   	   	   $('#dialtype').css('display','none');	
		  	   	   	   $('#pam_VPN_SCARE_CODE').val('0');
		  	   	   	}
		  	   	}
	  	}
  	}
}
//短号验证
function validateShortNum(obj) {  
	var shortCode = $('#pam_SHORT_CODE').val();
	var shortNumber = $('#pam_NOTIN_OLD_SHORT_CODE').val();
	if(shortCode != "" && shortNumber != shortCode) {
		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
		if( shortNumber !=""){
			del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortNumber);
			deleteRes(del);
		}
		var shortCode = $('#pam_NOTIN_OLD_SHORT_CODE').val();
		if( shortNumber !=""){
			del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortCode);
			deleteRes(del);
		}
//		$.showSucMessage("提示","效验成功");
//		displayShort(shortCode);
		if(verifyField(obj))
		{
			$.beginPageLoading();  
			Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',
		    		'&SHORT_CODE='+obj.val()+'&GRP_USER_ID='+$('#GRP_USER_ID').val()+'&MEB_EPARCHY_CODE='+$('#MEB_EPARCHY_CODE').val()+'&METHOD_NAME=validchk&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.MemParamInfo',afterValidateShortNum,errafterCheck);
		}
	}else{
		$.showWarnMessage("提示","未做短号修改，不需要验证！");  
	}
	
}

function afterValidateShortNum(data) { 
	$.endPageLoading();
	var shortCode = data.get("SHORT_CODE");
	var result = data.get("RESULT");  
 
	if(result==true||result=='true'){    
		$.showSucMessage("提示","效验成功");
		displayShort(shortCode);
	}else{   
		$('#pam_SHORT_CODE').val('');
		focus($('#pam_SHORT_CODE'));
	    var message=data.get("ERROR_MESSAGE");
	    $.showWarnMessage("错误提示",message);  
	}
}

function aftercheckVpnScare(data){ 
	$.endPageLoading();
    var vpnscare =  $('#pam_VPN_SCARE_CODE').val();
    var oldvpnscare =  $('#pam_OLD_VPN_SCARE_CODE').val();
    var flag =data.get('FLAG');  
    
    if(flag == "0")
    {
    	var message = data.get('ERR_MESSAGE');
//	    alert("集团成员短号不符合集团公司要求故不能转成跨省集团!");
	    $('#pam_VPN_SCARE_CODE').val(oldvpnscare);
	    $.showWarnMessage("错误提示",message); 
        return false;
    }
    else{
      	$('#dialtype').css('display','block');
      	var methodName =  $('#METHOD_NAME').val(); 
	  if(methodName == 'ChgUs'){ 
	      if(vpnscare == '2' && oldvpnscare != '2'  ){
	     	if(!confirm('您选择了本省VPN升级跨省VPN操作，请确认是否继续!')){
	     	  $('#dialtype').css('display','none');	
	     	  $('#defaultDiscnt').css('display','none');
	     	  $('#pam_VPN_SCARE_CODE').val(oldvpnscare);
	     	  return false;
	     	}
	      	$('#defaultDiscnt').css('display','');
	      }else{
	        $('#defaultDiscnt').css('display','none');
	      }
	      if(vpnscare != '2' && oldvpnscare == '2' ){
	          if(!confirm('您选择了跨省VPN降级本省VPN操作，请确认是否继续!')){
	              $('#dialtype').css('display','');	
	              $('#pam_VPN_SCARE_CODE').val('2');
	             return false;
	           }	
	      }
	  }
	  if(methodName == 'CrtUs'){
	  	   if(vpnscare == '2'){
	  	   	   if(!confirm('您选择新增跨省V网操作（需要选择VPN号和拨打方式信息），请确认是否继续！')){
	  	   	   	   $('#dialtype').css('display','none');	
	  	   	   	   $('#pam_VPN_SCARE_CODE').val('0');
	  	   	   	}
	  	   	}
	  }
  }
}


 
function validateParamPage(methodName) {   
 
	if(methodName=='CrtMb' || methodName=='ChgMb')
	{
		if(!$('#pam_TWOCHECK_SMS_FLAG').attr('checked')){
			if(!confirm("当前业务办理涉及不知情订购，若需用户短信二次确认请在界面中选择，重新选择请点【取消】按钮！")){
				return false;
			}
		}
		
		var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
		var shortNumberInput = $('#pam_SHORT_CODE').val();
		if( shortNumberInput =="" && shortNumber !=""){
			del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortNumber);
			deleteRes(del);
			shortNumber="";
		}

		var hasSvc ="0";
		
		// selectedEls标识“产品信息”选择区里所有的新增、删除、修改、已存在的元素集合 
		var length = selectedElements.selectedEls.length;
		var selectedElesData = $.DatasetList();
		for(var i=0;i<length;i++){
			var temp = selectedElements.selectedEls.get(i);
			var elementType = temp.get("ELEMENT_TYPE_CODE");
 			var elementId = temp.get("ELEMENT_ID");
 			var state = temp.get("MODIFY_TAG"); 
    		if(elementType=="S" && elementId == "861" && state != "1" && state != "0_1") {
 				hasSvc ="1";
 			}
			 
		}
		 
		if(hasSvc =="1" && shortNumberInput == ""){
			alert("短号服务与短号必须同时存在，请填写短号！");
			return false;
		}
		if(hasSvc =="0" && shortNumberInput != ""){
			alert("没有选择短号服务，请选择短号服务！");
			return false;
		}
		
		if(methodName=='CrtMb'){
			if(shortNumber == "" || shortNumber == null){
				if(shortNumber != shortNumberInput && shortNumberInput != "") {
					 var oldshortNumber = $('#pam_NOTIN_OLD_SHORT_CODE').val();
					 if(shortNumberInput != oldshortNumber ){ 
							alert("请先验证短号码！");
							return false;
					}
				}
			}
		} else if(methodName=='ChgMb'){
			if(shortNumber)	{
				if(shortNumber != shortNumberInput && shortNumberInput != "") {
					 var oldshortNumber = $('#pam_NOTIN_OLD_SHORT_CODE').val();
					 if(shortNumberInput != oldshortNumber ){ 
							alert("请先验证短号码！");
							return false;
						}
					}
			}
		}
		
	}
	 
 
	if(methodName=='CrtUs')
	{
		var vpnGrpAttr = $('#pam_VPN_GRP_ATTR').val();
		if(vpnGrpAttr == null || vpnGrpAttr == "")	{
			alert("集团属性不能为空！");
			return false;
		}
		
		var workTypeCode = $('#pam_WORK_TYPE_CODE').val();;
		if(workTypeCode == null || workTypeCode == "")	{
			alert("VPMN集团类型不能为空！");
			return false;
		}
		
		var vpnClass = $('#pam_VPN_CLASS').val();;
		if(vpnClass == null || vpnClass == "")	{
			alert("VPMN类别不能为空！");
			return false;
		}
		
		var manageNo = $('#pam_CUST_MANAGER').val();;
		if(manageNo == null || manageNo == "")	{
			alert("分管客户经理不能为空！");
			return false;
		}
		var vpnscare =  $('#pam_VPN_SCARE_CODE').val();
        var scpcode = $('#pam_SCP_CODE').val();
	
		if(vpnscare=='2') {
		  	if(scpcode == '10'){
		  	     alert('跨省集团只能在智能网scp2上办理,如果需要办理跨省集团，请重新选择集团属性！');
		  	     return false;
		  	}
		  	if(scpcode == ''){
		  	     alert('跨省集团只能在智能网scp2上办理,如果需要办理跨省集团，请重新选择集团属性！');
		  	     return false;
		  	}
		
		    var vpnno = $('#pam_VPN_NO').val();
		  	if(vpnno == ''){
		  	    alert('新增跨省V网需要选择VPN号信息，请选择vpn号！');
		  	    return false;	
		  	}
		 }
		//判断集团能否给成员定制跨省V网优惠 start
		var haserr = '0';
		var grpPackageList = $("#selectedGrpPackageList input");  //最终选择的集团定制包 老+新  datasetlist
		grpPackageList.each(function(){
			var checkvalue = $(this).val();
			var checkvaluelist = checkvalue.split('^');
			var productId = checkvaluelist[0];
			var packageId = checkvaluelist[1];
			var elementId = checkvaluelist[2];
			var elementType = checkvaluelist[3];
			if(elementType=="D" && canOrderDis=='false' && (elementId =='99720501' ||elementId =='99720502' ||elementId =='99720503' ||elementId =='99720504' ||elementId =='99720505')){
				alert('本集团不能订购跨省V网优惠['+elementId+']！如需订购，请对TD_S_COMMPARA表配置！');
				haserr = '1';
				return false;	
			}  
		}); 
		if(haserr=='1'){ 
		   return false;	
		}
		//判断集团能否给成员定制跨省V网优惠 end
	} 
	if(methodName=='ChgUs')
	{ 
		var vpnGrpAttr = $('#pam_GRP_PAY_DISCNT').val();
		if(vpnGrpAttr == null || vpnGrpAttr == "")	{
			alert("统一付费优惠不能为空！");
			return false;
		}
		
		var workTypeCode = $('#pam_WORK_TYPE_CODE').val();;
		if(workTypeCode == null || workTypeCode == "")	{
			alert("VPMN集团类型不能为空！");
			return false;
		}
		
		var vpnClass = $('#pam_VPN_CLASS').val();;
		if(vpnClass == null || vpnClass == "")	{
			alert("VPMN类别不能为空！");
			return false;
		}
		
		var manageNo = $('#pam_CUST_MANAGER').val();;
		if(manageNo == null || manageNo == "")	{
			alert("分管客户经理不能为空！");
			return false;
		}
		var rightcode = $('#RIGHT_CODE').val();
		var maxusers = $('#pam_MAX_USERS').val();
	
		if(rightcode !="true" && maxusers>5000) {
			alert("集团用户最大数不能超过5000，请重新修改！");
			return false;
		} 
		//判断集团能否给成员定制跨省V网优惠 start
		var haserr ="0";
		var canOrderDis = $('#CAN_ORDER_DIS').val(); // 判断该集团能否定制跨省5个资费 99720501,99720502,99720503,99720504,99720505
		var grpPackageList = $("#selectedGrpPackageList input");  //最终选择的集团定制包 老+新  datasetlist
		grpPackageList.each(function(){
			var checkvalue = $(this).val(); 
			var checkvaluelist = checkvalue.split('^');
			var productId = checkvaluelist[0];
			var packageId = checkvaluelist[1];
			var elementId = checkvaluelist[2];
			var elementType = checkvaluelist[3];
			if(elementType=="D" && canOrderDis=='false' && (elementId =='99720501' ||elementId =='99720502' ||elementId =='99720503' ||elementId =='99720504' ||elementId =='99720505')){
				alert('本集团不能订购跨省V网优惠['+elementId+']！如需订购，请对TD_S_COMMPARA表配置！');
				haserr ="1";
				return false;	
			} 
		});  
		if(haserr=='1'){ 
		   return false;	
		}
		//判断集团能否给成员定制跨省V网优惠 end
		var vpnscare =  $('#pam_VPN_SCARE_CODE').val(); 
		if(vpnscare == '2'){
			var vpnno = $('#pam_VPN_NO').val();
			if(vpnno == ''){
	  	    	alert('升级跨省V网需要选择VPN号信息，请选择vpn号！');
	  	    	return false;	
  		    }
			
  		    var old_vpn_scare = $('#pam_OLD_VPN_SCARE_CODE').val(); 
  	        if(old_vpn_scare != '2'){
	  	  	    var defualtdiscnt = $('#pam_DEFAULT_DISCNTCODE').val(); 
	  	  	    if(defualtdiscnt == ''){
		  	  	  	alert('获取集团升级默认的成员资费信息失败!');
		  	  	  	return false;
	  	  	  	 }
	  	  	   //判断集团能否给成员定制资费编码为 “产品参数界面选择的默认成员资费” 的跨省V网优惠 start
				var hasdis ="0"; 
				var grpPackageList = $("#selectedGrpPackageList input");  //最终选择的集团定制包 老+新  datasetlist 
				grpPackageList.each(function(){
					var checkvalue = $(this).val();
					var checkvaluelist = checkvalue.split('^');
					var productId = checkvaluelist[0];
					var packageId = checkvaluelist[1];
					var elementId = checkvaluelist[2];
					var elementType = checkvaluelist[3]; 
					
					if(elementType=="D" && elementId == defualtdiscnt) {
						$('#pam_DEFAULT_PRODUCT_ID').val(productId);
						$('#pam_DEFAULT_PACKAGE_ID').val(packageId);
	   	 				hasdis ="1";
	   	 			} 
					
				});   
				if(hasdis=='0'){
				   alert('请选择集团升级为跨省集团的默认成员资费，资费编码['+defualtdiscnt+']');
				   return false;	
				}
				//判断集团能否给成员定制资费编码为 “产品参数界面选择的默认成员资费” 的跨省V网优惠 end
  	  	    }
		}
	} 
		var vpnscare =  $('#pam_VPN_SCARE_CODE').val();  //集团参数界面 集团范围属性
		if(vpnscare == null){
		    vpnscare = $('#VPN_SCARE_CODE').val();  //成员参数界面 集团范围属性
		}
		var prodiscnt = $('#PROVICE_VPN_DISCNT').val();  // 跨省集团资费 从commparam表配置中取到
		if(vpnscare != '2'){
			var prodiscntlist = prodiscnt.split(',');
			//非跨省集团，不能对跨省集团资费做操作 
			if(methodName=='ChgUs'||methodName=='CrtUs')
			{
				//判断集团能否给成员定制跨省V网优惠 start
				var haserr = '0';
				var grpPackageList = $("#selectedGrpPackageList input");  //最终选择的集团定制包 老+新  datasetlist 
				grpPackageList.each(function(){
					var checkvalue = $(this).val();
					var checkvaluelist = checkvalue.split('^');
					var productId = checkvaluelist[0];
					var packageId = checkvaluelist[1];
					var elementId = checkvaluelist[2];
					var elementType = checkvaluelist[3];  
		 			for(var k=0;k<prodiscntlist.length;k++){
		 				if(elementType=="D" && elementId == prodiscntlist[k]) {
		 			    	alert('非跨省集团，不能对跨省集团资费做操作，资费编码：【'+prodiscntlist[k]+'】');
		 			    	haserr = '1';
		 			    	return false;
		 			    }	
		 			} 
				}); 
				if(haserr=='1'){ 
				   return false;	
				}
				//判断集团能否给成员定制跨省V网优惠 end
			}else if(methodName=='CrtMb' || methodName=='ChgMb'){
				// selectedEls标识“产品信息”选择区里所有的新增0、删除1、修改2、已存在exist的元素集合 
				var length = selectedElements.selectedEls.length;
				var selectedElesData = $.DatasetList();
				for(var i=0;i<length;i++){
					var temp = selectedElements.selectedEls.get(i);
					var elementType = temp.get("ELEMENT_TYPE_CODE");
		 			var elementId = temp.get("ELEMENT_ID");
		 			var state = temp.get("MODIFY_TAG"); 
		 			if(state=='0' || state=='2' || state=='exist'){
			 			for(var k=0;k<prodiscntlist.length;k++){
			 				if(elementType=="D" && elementId == prodiscntlist[k]) {
			 					 alert('非跨省集团，不能对跨省集团资费做操作，资费编码：【'+prodiscntlist[k]+'】');
			 			         return false;
			 			    }	
			 			} 
		 			}
				}
			}
			 
		}else{ 
			//跨省集团需要跨省资费，请选择跨省资费!
			var ifbook =false; 
			var prodiscntlist = prodiscnt.split(',');
			if(methodName=='ChgUs'||methodName=='CrtUs')
			{
				var grpPackageList = $("#selectedGrpPackageList input");  //最终选择的集团定制包 老+新  datasetlist 
				grpPackageList.each(function(){
					var checkvalue = $(this).val();
					var checkvaluelist = checkvalue.split('^');
					var productId = checkvaluelist[0];
					var packageId = checkvaluelist[1];
					var elementId = checkvaluelist[2];
					var elementType = checkvaluelist[3];  
		 			for(var k=0;k<prodiscntlist.length;k++){
		 				if(elementType=="D" && elementId == prodiscntlist[k]) {
		 					ifbook = true;
		 			        break;
		 			    }	
		 			}
					
				});  
			}else if(methodName=='CrtMb' || methodName=='ChgMb'){
				// selectedEls标识“产品信息”选择区里所有的新增、删除、修改、已存在的元素集合 
				var length = selectedElements.selectedEls.length;
				var selectedElesData = $.DatasetList();
				for(var i=0;i<length;i++){
					var temp = selectedElements.selectedEls.get(i);
					var elementType = temp.get("ELEMENT_TYPE_CODE");
		 			var elementId = temp.get("ELEMENT_ID");
		 			var state = temp.get("MODIFY_TAG"); 
		 			if(state=='0' || state=='2' || state=='exist'){ 
			 			for(var k=0;k<prodiscntlist.length;k++){
			 				if(elementType=="D" && elementId == prodiscntlist[k]) {
			 					ifbook = true;
			 			        break;
			 			    }	
			 			} 
					}
				} 
			}
			
			if(ifbook ==false){
			    alert('跨省集团需要跨省资费，请选择跨省资费!');
			    return false;
			}  
		} 
	return true;
}
 
/*

function afterAction() {
	var vpmnParamset = $.table.get("VpmnParams");
// j2ee   window["vpmnParamset"] = new TableEdit("VpmnParams",false,myOnClickMethod);
}

function myOnClickMethod(e){
    var td = null;
	if (e.target.tagName == 'SELECT') {
		td = e.target.parentNode;
	} else {
		td = e.target;
	}
	var row=td.parentNode;
	window["row"] = row;
}

*/

 function resertShortCode(){
	var shortNumber = $('#SHORT_NUMBER_PARAM_INPUT').val();
	if(shortNumber != "" && shortNumber != null)	{
			var del = $.DataMap();
			del.put("RES_TYPE_CODE","S");
			del.put("RES_CODE",shortNumber);
			deleteRes(del);
		}
	var shortCode = $('#pam_NOTIN_OLD_SHORT_CODE').val();
	$('#pam_SHORT_CODE').val(shortCode);
	$('#SHORT_NUMBER_PARAM_INPUT').val(shortCode);
	if(shortCode != "" && shortCode != null)	{
		
		var obj =$.DataMap();
		obj.put("RES_TYPE_CODE","S");
		obj.put("RES_CODE",shortCode);
		obj.put("CHECKED","true");
		obj.put("DISABLED","true");
		insertRes(obj);
		
	}
	//$("#DYNATABLE_RES_RECORD").val($("#DYNATABLE_RES_LIST").val());
	var oldList = $.DatasetList($("#DYNATABLE_RES_LIST").val());//过滤掉MODIFY_TAG为EXIST的资源信息
	oldList.each(function(item,index,totalcount){
		if (item.get("MODIFY_TAG") == "EXIST"){
			oldList.removeAt(index);
		}
	});
	$("#DYNATABLE_RES_RECORD").val(oldList.toString());
	
}
 
 
 function filterCustManagers()
 {
 	var notinCustManager = $('#pam_NOTIN_CUST_MANAGER').val();
 	$.beginPageLoading('正在客户经理列表过滤...');
 	var param = '&FILTER_CUST_MANAGER='+notinCustManager+'&METHOD_NAME=filterCustManagers&CLASS_NAME=com.asiainfo.veris.crm.order.web.group.param.vpn.UserParamInfo';
 	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam','productParamInvoker',param, arfterDealCustManager,errafterCheck);
 }

 function arfterDealCustManager(data)
 {
 	$.endPageLoading();
 	var managerInfoList = data.get("managerInfoList");
 	if(managerInfoList != null)
 	{
 		var length = managerInfoList.length;
 		if(length > 0)
 		{
 			var objSelect = $('#pam_CUST_MANAGER');
 			if(objSelect)
 			{
 				objSelect.html('');
 				//objSelect.empty();
 				objSelect.append("<option selected value=\"\" title=\"--请选择--\">--请选择--</option>");
 				for(var i=0; i < length; i++)
 				{
 					var infoItems = managerInfoList.get(i);
 					var managerName = infoItems.get("CUST_MANAGER_NAME");
 					var managerId = infoItems.get("CUST_MANAGER_ID");
 					objSelect.append("<option title=\"" + managerName +"\" value=\"" + managerId +"\">" + managerName + "</option>");
 				}
 			}
 		}
 		else 
 		{
 			var objSelect = $('#pam_CUST_MANAGER');
 			if(objSelect)
 			{
 				objSelect.html('');
 				objSelect.append("<option selected value=\"\" title=\"--请选择--\">--请选择--</option>");
 			}
 		}
 	}
 	else 
 	{
 		var objSelect = $('#pam_CUST_MANAGER');
 		if(objSelect)
 		{
 			objSelect.html('');
 			objSelect.append("<option selected value=\"\" title=\"--请选择--\">--请选择--</option>");
 		}
 	}
 }


