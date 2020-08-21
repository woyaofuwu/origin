var moList = new Wade.DatasetList();

function initPageParam_110000008000()
{
	
	if($.os.phone){
		var publicInfos = $("[name='openOfferChaId']");
		for(var i = 0, size = publicInfos.length; i < size; i++){
			var publicInfo = publicInfos[i];
			publicInfo.style.cssText="width:11em";
		}
	}
	
	var offerTpye = $("#cond_OPER_TYPE").val();
	
	if("CrtUs"==offerTpye){
		$("#pam_GRP_PAY_DISCNT").closest("li").css("display", "none");
		$("#pam_GRP_PAY_DISCNT").closest("li").removeClass("link required");
		$("#pam_GRP_PAY_DISCNT").attr("nullable", "yes");
        $("#pam_MAX_USERS").attr("disabled",true);
	}
	if("ChgUs"==offerTpye){
		debugger;
		$("#pam_VPN_GRP_ATTR").closest("li").css("display", "none");
		$("#pam_VPN_GRP_ATTR").closest("li").removeClass("link required");
		$("#pam_VPN_GRP_ATTR").attr("nullable", "yes");
		$("#pam_SCP_CODE").closest("li").css("display", "none");
		$("#pam_SCP_CODE").closest("li").removeClass("link required");
		$("#pam_SCP_CODE").attr("nullable", "yes");
		$("#pam_WORK_TYPE_CODE").attr("disabled", "true");
	}
	if("ChgUs"==offerTpye){
		changeVpnGrpClipType();
	}
    $("#pam_SHORT_CODE_LEN").attr("disabled",true);
	$("#pam_MAX_TELPHONIST_NUM").css("style", "width:10em");
	
}

//过滤集团VPMN客服经理
function filterCustManager(){
	
	var custManager = $("#pam_NOTIN_CUST_MANAGER").val();
	/*if(null==custManager|| "" == custManager){
		$.validate.alerter.one($("#pam_NOTIN_CUST_MANAGER")[0], "您输入的客服经理为空，请输入后再过滤!\n");			
		return false; 
    }*/
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.vpmn.SuperteleHandler", "filterCustManagers",'&CUST_MANAGER_NAME='+ custManager, function(data){
		$.endPageLoading();
		arfterDealCustManager(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function arfterDealCustManager(data)
{
    pam_CUST_MANAGER.empty();
	if(data != null){
		/*var infoItems = data.get(0);
		var managerName = infoItems.get("CUST_MANAGER_NAME");
		var managerId = infoItems.get("CUST_MANAGER_ID");	
		$("#pam_CUST_MANAGER").val(managerId);*/
        for(var i=0;i<data.length;i++){
            var infoItems = data.get(i);
            var managerName = infoItems.get("CUST_MANAGER_NAME");
            var managerId = infoItems.get("CUST_MANAGER_ID");
            pam_CUST_MANAGER.append(managerName,managerId,managerName);
		}
	}
}

function createserialnumber() { 
	var workTypeCode = $("#pam_WORK_TYPE_CODE").val();;
	
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.vpmn.SuperteleHandler", "createSerialNumber",'&WORK_TYPE_CODE='+ workTypeCode, function(data){
		$.endPageLoading();
		afterValidchk(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function afterValidchk(data) {  
	debugger;
	$.endPageLoading();
	var result = data.get("RESULT"); 
	  if(result==true||result=="true"){    
		    var vpnno = data.get("VPN_NO"); 
			var obj =$.DataMap();
			obj.put("RES_TYPE_CODE", "L");
			obj.put("RES_CODE",vpnno);
			obj.put("CHECKED","true");
			obj.put("DISABLED","true");
			if($("#SET_OFFERCHA_SOURCE").val() == "EcIntegrateOrder"){//一单清特殊处理
				$('#CHILD_EC_SERIAL_NUMBER_INPUT').val(vpnno); 
				$('#CHILD_EC_REAL_SERIAL_NUMBER').val(vpnno);
			}
			else{
				$('#cond_SERIAL_NUMBER_INPUT').val(vpnno); 
				$('#cond_SERIAL_NUMBER').val(vpnno); 
				$('#HIDDEN_SERIAL_NUMBER').val(vpnno); 
			}
			
	  }else{   
		     MessageBox.alert("验证提示",data.get("ERROR_MESSAGE"));
	  }
	  
}


function querySCPInfos(){  
	var vpnAttr = $('#pam_VPN_GRP_ATTR').val(); 
	
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.vpmn.SuperteleHandler", "querySCPInfos",'&VPN_GRP_ATTR='+ vpnAttr, function(data){
		$.endPageLoading();
		afterCheck(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function afterCheck(data){
	  var result = data.get("RESULT");  
	  if(result==true||result=='true'){       
	  	 $("#pam_SCP_CODE").val(data.get("DATA_ID"));
	  }else{   
	     MessageBox.alert("验证提示",data.get("ERROR_MESSAGE"));
	  }
	} 

//提交
function checkSub(obj)
{
	if(!checkSubInfo()){
		return false;
	}
	vpnParamInfo();
	
	if(!checkVpnGrpClipType()){
		return false;
	}
	
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}
function vpnParamInfo(){
	if($("#pam_GRP_PAY_DISCNT").val()==""||
		$("#pam_WORK_TYPE_CODE").val()==""||
		$("#pam_VPN_CLASS").val()==""||
		$("#pam_CUST_MANAGER").val()==""){
		return;
	}
	var moList = new Wade.DatasetList();
	var vpnData1 = new Wade.DataMap();
	vpnData1.put("TAG_CODE","VPN001");
	vpnData1.put("TAG_VALUE",$("#VPN001").val());
	moList.add(vpnData1);
	var vpnData2 = new Wade.DataMap();
	vpnData2.put("TAG_CODE","VPN002");
	vpnData2.put("TAG_VALUE",$("#VPN002").val());
	moList.add(vpnData2);
	var vpnData3 = new Wade.DataMap();
	vpnData3.put("TAG_CODE","VPN003");
	vpnData3.put("TAG_VALUE",$("#VPN003").val());
	moList.add(vpnData3);
	var vpnData4 = new Wade.DataMap();
	vpnData4.put("TAG_CODE","VPN004");
	vpnData4.put("TAG_VALUE",$("#VPN004").val());
	moList.add(vpnData4);
	var vpnData5 = new Wade.DataMap();
	vpnData5.put("TAG_CODE","VPN005");
	vpnData5.put("TAG_VALUE",$("#VPN005").val());
	moList.add(vpnData5);
	var vpnData6 = new Wade.DataMap();
	vpnData6.put("TAG_CODE","VPN006");
	vpnData6.put("TAG_VALUE",$("#VPN006").val());
	moList.add(vpnData6);
	var vpnData7 = new Wade.DataMap();
	vpnData7.put("TAG_CODE","VPN007");
	vpnData7.put("TAG_VALUE",$("#VPN007").val());
	moList.add(vpnData7);
	var vpnData8 = new Wade.DataMap();
	vpnData8.put("TAG_CODE","VPN008");
	vpnData8.put("TAG_VALUE",$("#VPN008").val());
	moList.add(vpnData8);
	var vpnData9 = new Wade.DataMap();
	vpnData9.put("TAG_CODE","VPN009");
	vpnData9.put("TAG_VALUE",$("#VPN009").val());
	moList.add(vpnData9);
	var vpnData10 = new Wade.DataMap();
	vpnData10.put("TAG_CODE","VPN010");
	vpnData10.put("TAG_VALUE",$("#VPN010").val());
	moList.add(vpnData10);
	var vpnData11 = new Wade.DataMap();
	vpnData11.put("TAG_CODE","VPN011");
	vpnData11.put("TAG_VALUE",$("#VPN011").val());
	moList.add(vpnData11);
	var vpnData12 = new Wade.DataMap();
	vpnData12.put("TAG_CODE","VPN012");
	vpnData12.put("TAG_VALUE",$("#VPN012").val());
	moList.add(vpnData12);
	var vpnData13 = new Wade.DataMap();
	vpnData13.put("TAG_CODE","VPN013");
	vpnData13.put("TAG_VALUE",$("#VPN013").val());
	moList.add(vpnData13);
	var vpnData14 = new Wade.DataMap();
	vpnData14.put("TAG_CODE","VPN014");
	vpnData14.put("TAG_VALUE",$("#VPN014").val());
	moList.add(vpnData14);
	var vpnData15 = new Wade.DataMap();
	vpnData15.put("TAG_CODE","VPN015");
	vpnData15.put("TAG_VALUE",$("#VPN015").val());
	moList.add(vpnData15);
	var vpnData16 = new Wade.DataMap();
	vpnData16.put("TAG_CODE","VPN016");
	vpnData16.put("TAG_VALUE",$("#VPN016").val());
	moList.add(vpnData16);
	var vpnData17 = new Wade.DataMap();
	vpnData17.put("TAG_CODE","VPN017");
	vpnData17.put("TAG_VALUE",$("#VPN017").val());
	moList.add(vpnData17);
	var vpnData18 = new Wade.DataMap();
	vpnData18.put("TAG_CODE","VPN0018");
	vpnData18.put("TAG_VALUE",$("#VPN0018").val());
	moList.add(vpnData18);
	var vpnData19 = new Wade.DataMap();
	vpnData19.put("TAG_CODE","VPN019");
	vpnData19.put("TAG_VALUE",$("#VPN019").val());
	moList.add(vpnData19);
	var vpnData20 = new Wade.DataMap();
	vpnData20.put("TAG_CODE","VPN020");
	vpnData20.put("TAG_VALUE",$("#VPN020").val());
	moList.add(vpnData20);
	var vpnData22 = new Wade.DataMap();
	vpnData22.put("TAG_CODE","VPN022");
	vpnData22.put("TAG_VALUE",$("#VPN022").val());
	moList.add(vpnData22);
	var vpnData23 = new Wade.DataMap();
	vpnData23.put("TAG_CODE","VPN023");
	vpnData23.put("TAG_VALUE",$("#VPN023").val());
	moList.add(vpnData23);
	var vpnData24 = new Wade.DataMap();
	vpnData24.put("TAG_CODE","VPN024");
	vpnData24.put("TAG_VALUE",$("#VPN024").val());
	moList.add(vpnData24);
	var vpnData25 = new Wade.DataMap();
	vpnData25.put("TAG_CODE","VPN025");
	vpnData25.put("TAG_VALUE",$("#VPN025").val());
	moList.add(vpnData25);
	var vpnData26 = new Wade.DataMap();
	vpnData26.put("TAG_CODE","VPN026");
	vpnData26.put("TAG_VALUE",$("#VPN026").val());
	moList.add(vpnData26);
	var vpnData37 = new Wade.DataMap();
	vpnData37.put("TAG_CODE","VPN037");
	vpnData37.put("TAG_VALUE",$("#VPN037").val());
	moList.add(vpnData37);
	var vpnData38 = new Wade.DataMap();
	vpnData38.put("TAG_CODE","VPN038");
	vpnData38.put("TAG_VALUE",$("#VPN038").val());
	moList.add(vpnData38);
	$("#vpmnParamsPart").remove();
	$("#NOTIN_VPN_TAG_SET").val(moList.toString());
	
	
	
}
function checkSubInfo(){
	var vpnscare =  $('#pam_VPN_SCARE_CODE').val();
    var scpcode = $('#pam_SCP_CODE').val();

	if(vpnscare=='2') {
	  	if(scpcode == '10'){
	  		 MessageBox.alert("验证提示","跨省集团只能在智能网scp2上办理,如果需要办理跨省集团，请重新选择集团属性！");
	  	     return false;
	  	}
	  	if(scpcode == ''){
	  		 MessageBox.alert("验证提示","跨省集团只能在智能网scp2上办理,如果需要办理跨省集团，请重新选择集团属性！");
	  	     return false;
	  	}
	
	    var vpnno = $('#pam_VPN_NO').val();
	  	if(vpnno == ''){
	  		MessageBox.alert("验证提示","新增跨省V网需要选择VPN号信息，请选择vpn号！");
	  	    return false;	
	  	}
	 }
	return true;
}

function checkVpnScare(){
	
	var vpnscare =  $('#pam_VPN_SCARE_CODE').val();
	var oldvpnscare =  $('#pam_OLD_VPN_SCARE_CODE').val();
	var serial = $('#cond_SERIAL_NUMBER');
	var methodName =  $('#cond_OPER_TYPE').val(); 
	if(vpnscare=='2' && methodName !='CrtUs') {   //全国集团
		$.beginPageLoading(); 
		$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.vpmn.SuperteleHandler", "checkShortNumber",'&GRP_USER_ID='+$('#USER_ID').val(), function(data){
			$.endPageLoading();
			aftercheckVpnScare(data);
			},    
			function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
		
	}
	else if(vpnscare=='2' && methodName =='CrtUs'){
		  $('#dialtype').css('display','block');
		  if(methodName == 'CrtUs'){
		  	   if(vpnscare == '2'){
		  	     MessageBox.confirm("确认提示", "您选择新增跨省V网操作（需要选择VPN号和拨打方式信息），请确认是否继续！", function(btn){
	                    if("cancel" == btn){
	                    	 
	                    	// $('#dialtype').css('display','none');	
	                    	 var style="none";
	                    	 changeStyle(style);
	  		  	   	   	      $('#pam_VPN_SCARE_CODE').val('0');
	                    }else{
//	                    	 $('#pam_DIAL_TYPE_CODE').css("display","");
	                    	var style="";
	                    	changeStyle(style);
	                    }
	                }, {"ok" : "是", "cancel" : "否"});
		  	   	}
		  	} 
	  } 
	  else{
		  if(methodName == 'ChgUs'){
		      if(vpnscare == '2' && oldvpnscare != '2'  ){
			          MessageBox.confirm("确认提示", "您选择了本省VPN升级跨省VPN操作，请确认是否继续!", function(btn){
		                    if("cancel" == btn){
		                    	var style="none";
		                    	changeStyle(style);
		                    	// $('#dialtype').css('display','none');	
		   		         	  	// $('#defaultDiscnt').css('display','none');
		   		         	     $('#pam_VPN_SCARE_CODE').val(oldvpnscare);
		   		         	 $('#pam_DEFAULT_DISCNTCODE').parents("#pam_DEFAULT_DISCNTCODE_span").css("display","none");
		   		      	     $('#pam_DEFAULT_DISCNTCODE').parents(".link").css("display","none");
		   		         	    return false;
		                    }else{
		                    	var style="";
		                    	changeStyle(style);
		                    	
		                    }
		                }, {"ok" : "是", "cancel" : "否"});
			      
			          $('#pam_DEFAULT_DISCNTCODE').parents("#pam_DEFAULT_DISCNTCODE_span").css("display","");
	   		      	    $('#pam_DEFAULT_DISCNTCODE').parents(".link").css("display","");
		      }else{
		    	  $('#pam_DEFAULT_DISCNTCODE').parents("#pam_DEFAULT_DISCNTCODE_span").css("display","none");
 		      	  $('#pam_DEFAULT_DISCNTCODE').parents(".link").css("display","none");
		      }
		      if(vpnscare != '2' && oldvpnscare == '2' ){
		          MessageBox.confirm("确认提示", "您选择了跨省VPN降级本省VPN操作，请确认是否继续!", function(btn){
	                    if("cancel" == btn){
	                    	//$('#dialtype').css('display','');
	                    	var style="";
	                    	changeStyle(style);
	                    	$('#pam_VPN_SCARE_CODE').val('2');
	   		             	return false;
	                    }else{
	                    	var style="none";
	                    	changeStyle(style);
	                    }
	                }, {"ok" : "是", "cancel" : "否"});
		      }
		  }
		  if(methodName == 'CrtUs'){ 
		  	   if(vpnscare == '2'){
		  		 MessageBox.confirm("确认提示", "您选择新增跨省V网操作（需要选择VPN号和拨打方式信息），请确认是否继续！", function(btn){
	                    if("cancel" == btn){
	                      // $('#dialtype').css('display','none');
	                    	var style="none";
	                    	changeStyle(style);
	  		  	   	   	     $('#pam_VPN_SCARE_CODE').val('0');
	                    }else{
	                    	var style="";
	                    	changeStyle(style);
	                    }
	                }, {"ok" : "是", "cancel" : "否"});
		  	   	}
	  	}
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
	    MessageBox.error("错误信息",message);
        return false;
    }
    else{
      	
      	var methodName =   $('#cond_OPER_TYPE').val(); 
	  if(methodName == 'ChgUs'){ 
	      if(vpnscare == '2' && oldvpnscare != '2'  ){
	  		 MessageBox.confirm("确认提示", "您选择了本省VPN升级跨省VPN操作，请确认是否继续!", function(btn){
                   if("cancel" == btn){
                	 // $('#dialtype').css('display','none');	
                	  //$('#dialtype').css('display','none');	
                		var style="none";
                    	changeStyle(style);
      		    	  $('#pam_DEFAULT_DISCNTCODE').parents("#pam_DEFAULT_DISCNTCODE_span").css("display","none");
     		      	  $('#pam_DEFAULT_DISCNTCODE').parents(".link").css("display","none");
         	     	  $('#pam_VPN_SCARE_CODE').val(oldvpnscare);
         	     	  return false;
                   }else{
                	   var style="";
                   	   changeStyle(style);
                   }
               }, {"ok" : "是", "cancel" : "否"});
	    	  $('#pam_DEFAULT_DISCNTCODE').parents("#pam_DEFAULT_DISCNTCODE_span").css("display","");
		      	  $('#pam_DEFAULT_DISCNTCODE').parents(".link").css("display","");
	      }else{
	    	  $('#pam_DEFAULT_DISCNTCODE').parents("#pam_DEFAULT_DISCNTCODE_span").css("display","none");
		      $('#pam_DEFAULT_DISCNTCODE').parents(".link").css("display","none");
	      }
	      if(vpnscare != '2' && oldvpnscare == '2' ){MessageBox.confirm("确认提示", "您选择了跨省VPN降级本省VPN操作，请确认是否继续!", function(btn){
              if("cancel" == btn){
            	  //$('#dialtype').css('display','');	
            	  var style="";
              	  changeStyle(style);
	              $('#pam_VPN_SCARE_CODE').val('2');
	              return false;
              }else{
            	  var style="none";
              	   changeStyle(style);
              }
          }, {"ok" : "是", "cancel" : "否"});
	     }
	      
	  }
	  if(methodName == 'CrtUs'){
	  	   if(vpnscare == '2'){
	  	   	 MessageBox.confirm("确认提示", "您选择新增跨省V网操作（需要选择VPN号和拨打方式信息），请确认是否继续！", function(btn){
	              if("cancel" == btn){
	            	 // $('#dialtype').css('display','none');	
	            	  var style="none";
	              	   changeStyle(style);
		  	   	   	  $('#pam_VPN_SCARE_CODE').val('0');
	              }else{
	            	  var style="";
	              	   changeStyle(style);
	              }
	          }, {"ok" : "是", "cancel" : "否"});
	  	   	}
	  }
  }
}
function changeStyle(style){
	if("none"==style){
		$('#pam_VPN_NO').parents("#pam_VPN_NO_span").css("display","none");
    	$('#pam_VPN_NO').parents(".link").css("display","none");
    	$('#pam_DIAL_TYPE_CODE').parents("#pam_DIAL_TYPE_CODE_span").css("display","none");
    	$('#pam_DIAL_TYPE_CODE').parents(".link").css("display","none");
		
	}else{
		$('#pam_VPN_NO').parents("#pam_VPN_NO_span").css("display","");
    	$('#pam_VPN_NO').parents(".link").css("display","");
    	$('#pam_DIAL_TYPE_CODE').parents("#pam_DIAL_TYPE_CODE_span").css("display","");
    	$('#pam_DIAL_TYPE_CODE').parents(".link").css("display","");
	}
}


function checkVpnGrpClipType()
{
	var grpClipTypeValue = $('#pam_GRP_CLIP_TYPE').val();//呼叫来显方式
	var grpUserClipTypeValue = $('#pam_GRP_USER_CLIP_TYPE').val(); //选择号显方式
	var grpUserModValue = $('#pam_GRP_USER_MOD').val(); //成员修改号显方式
	if(grpClipTypeValue != '' && grpClipTypeValue == '1')
	{
		if(grpUserClipTypeValue == null || grpUserClipTypeValue == '')
		{
			MessageBox.alert("验证提示","请选择号显方式!");
			return false;
		}
		if(grpUserModValue == null || grpUserModValue == '')
		{
			MessageBox.alert("验证提示","请选择成员修改号显方式!");
			return false;
		}
	}
	
	if(grpClipTypeValue == null || grpClipTypeValue == '')
	{
		if(grpUserClipTypeValue != null && grpUserClipTypeValue != '')
		{
			MessageBox.alert("验证提示","呼叫来显方式选项未选择!请不用单独选择号显方式!");
			return false;
		}
		if(grpUserModValue != null && grpUserModValue != '')
		{
			MessageBox.alert("验证提示","呼叫来显方式选项未选择!请不用单独成员修改号显方式!");
			return false;
		}
	}
	
	return true;
}

function changeVpnGrpClipType()
{
	var grpClipTypeValue = $('#pam_GRP_CLIP_TYPE').val();
	if(grpClipTypeValue != '' && grpClipTypeValue == '0')
	{
		$('#pam_GRP_USER_CLIP_TYPE').parents("#pam_GRP_USER_CLIP_TYPE_span").css("display","none");
		$('#pam_GRP_USER_MOD').parents("#pam_GRP_USER_MOD_span").css("display","none");
		
		$('#pam_GRP_USER_CLIP_TYPE').val("");
		$('#pam_GRP_USER_MOD').val("");
	}
	else if(grpClipTypeValue != '' && grpClipTypeValue == '1')
	{
		$('#pam_GRP_USER_CLIP_TYPE').parents("#pam_GRP_USER_CLIP_TYPE_span").css("display","");
		$('#pam_GRP_USER_MOD').parents("#pam_GRP_USER_MOD_span").css("display","");
	}
	else
	{
		$('#pam_GRP_USER_CLIP_TYPE').parents("#pam_GRP_USER_CLIP_TYPE_span").css("display","");
		$('#pam_GRP_USER_MOD').parents("#pam_GRP_USER_MOD_span").css("display","");
		$('#pam_GRP_USER_CLIP_TYPE').val("");
		$('#pam_GRP_USER_MOD').val("");
	}
}

