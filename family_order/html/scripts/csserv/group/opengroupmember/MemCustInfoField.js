(function($){
	$.extend({MemCustInfo:{
		infoPart: "GroupUserInfoPart",
		requiredField: "PSPT_TYPE_CODE,PSPT_ID,CONTACT_PHONE,CUST_NAME,USER_TYPE_CODE,POST_CODE,PSPT_ADDRESS",
		realNameSVCName:null,		//实名制数量限制服务名字，如果有需要可以指向自己实现的服务
		relatePayName:true,			//该参数表示在设置客户姓名时候，如果检测到页面使用了账户编辑里面的PAY_NAME,则两边数据关联起来
		widgets:[],
		isRealName:true,			//是否启用实名制校验
		serialNumber:null,			//服务号码，证件号码校验需要配置
		init:function(infoPart){
			this.infoPart = infoPart;
			this.bindEvents();		//绑定组件内部默认DOM接点事件
		},
		setSerialNumber:function(sn){
			this.serialNumber = sn;
		},
		setRelatePayName:function(flag){
			this.relatePayName = flag;
		},
		setRealName:function(isRealName){
			this.isRealName = isRealName;
		},
		setRealNameSVCName:function(svcName){
			this.realNameSVCName = svcName;
		},
		setRequiredFielde:function(requiredField){
			this.requiredField = requiredField;
		},
		//绑定事件
		bindEvents:function(){
			var oThis = this;
			//身份证类型
			$("#MEM_CUST_INFO_PSPT_TYPE_CODE,#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE,#MEM_CUST_INFO_USE_PSPT_TYPE_CODE,#MEM_CUST_INFO_RSRV_STR3").bind("change", function(){
				oThis.chkPsptTypeCode($(this).attr("id"));
			});
			//校验营业执照
			$("#MEM_CUST_INFO_legalperson,#MEM_CUST_INFO_orgtype").bind("change", function(){			 
				oThis.verifyEnterpriseCard();
			});		
			//校验组织机构代码证
			$("#MEM_CUST_INFO_orgtype").bind("change", function(){			 
				oThis.verifyOrgCard();
			});

			$("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").bind("blur", function(){
				oThis.afterChkAgentPsptTypeCode();
			});
			//校验身份证
			$("#MEM_CUST_INFO_PSPT_ID,#MEM_CUST_INFO_AGENT_PSPT_ID,#MEM_CUST_INFO_USE_PSPT_ID,#MEM_CUST_INFO_RSRV_STR4").bind("change", function(){
				oThis.chkPsptId($(this).attr("id"));
			});
			$("#MEM_CUST_INFO_AGENT_PSPT_ID").bind("blur", function(){
				oThis.afterChkAgentPsptId();
			});
			$("#MEM_CUST_INFO_PSPT_ID,#MEM_CUST_INFO_USE_PSPT_ID").bind("blur", function(){
				oThis.afterChkPsptId();
			});
			
			//客户摄像按钮@zhuoyingzhi @date20180402
			$("#SHOT_IMG").bind("click", function(){
				oThis.identification("MEM_CUST_INFO_PIC_ID","MEM_CUST_INFO_PIC_STREAM");
			});	
			
			//扫描身份证
			$("#SCAN_PSPT").bind("click", function(){
				oThis.clickScanPspt();
			});	
			//经办人扫描身份证
			$("#SCAN_PSPT2").bind("click", function(){
				oThis.clickScanPspt2();
			});
			
			//经办人摄像按钮 @zhuoyingzhi @date20180402
			$("#AGENT_SHOT_IMG").bind("click", function(){
				oThis.identification("MEM_CUST_INFO_AGENT_PIC_ID","MEM_CUST_INFO_AGENT_PIC_STREAM");
			});
			
			
			//使用人扫描身份证
			$("#SCAN_PSPT3").bind("click", function(){
				oThis.clickScanPspt3();
			});	
			//责任人扫描身份证
			$("#SCAN_PSPT4").bind("click", function(){
				oThis.clickScanPspt4();
			});	
			//客户姓名
			$("#MEM_CUST_INFO_CUST_NAME,#MEM_CUST_INFO_AGENT_CUST_NAME,#MEM_CUST_INFO_USE,#MEM_CUST_INFO_RSRV_STR2").bind("change", function(){
				oThis.chkCustName($(this).attr("id"));
			});
			$("#MEM_CUST_INFO_CUST_NAME").bind("blur", function(){
				oThis.afterChkCustName();
			});
			$("#MEM_CUST_INFO_AGENT_CUST_NAME").bind("blur", function(){
				oThis.afterChkAgentCustName();
			});
			//地址
			$("#MEM_CUST_INFO_PSPT_ADDRESS,#MEM_CUST_INFO_AGENT_PSPT_ADDR,#MEM_CUST_INFO_USE_PSPT_ADDR,#MEM_CUST_INFO_RSRV_STR5").bind("change", function(){
				oThis.chkPsptAddr($(this).attr("id"));
			});
			$("#MEM_CUST_INFO_AGENT_PSPT_ADDR").bind("blur", function(){
				oThis.afterChkAgentPsptAddr();
			});
		},
		//初始化字段属性及样式
		initFieldAttr:function(){
			var fieldName, nullable;
			var requiredField = "";
			this.initRequiredField(requiredField);
			
			$("#"+this.infoPart).find("li").each(function(){
				nullable="yes";
				if($(this).find("input") && $(this).find("input").length){
					fieldName = $(this).find("input:eq(0)").attr("name");
					nullable = $(this).find("input:eq(0)").attr("nullable");
				}else if($(this).find("select") && $(this).find("select").length){
					fieldName = $(this).find("select:eq(0)").attr("name");
					nullable = $(this).find("select:eq(0)").attr("nullable");
				}else if($(this).find("textarea") && $(this).find("textarea").length){
					fieldName = $(this).find("textarea:eq(0)").attr("name");
					nullable = $(this).find("textarea:eq(0)").attr("nullable");
				}else{
					fieldName = "";
				}
				if(requiredField.indexOf(fieldName) == -1 || nullable!="no"){
					$(this).addClass("e_hideX");
				}
			});
		},
		
		renderSpecialField:function(psptTypeCode){ 
			/*单位证件：经办人、责任人为必填，使用人非必填*/
			var fields=["MEM_CUST_INFO_AGENT_CUST_NAME","MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE","MEM_CUST_INFO_AGENT_PSPT_ID","MEM_CUST_INFO_AGENT_PSPT_ADDR"];
			var tag = (psptTypeCode=="E" || psptTypeCode=="G" || psptTypeCode=="D" || psptTypeCode=="M" || psptTypeCode=="L")? true : false;
			for(var i=0; i<fields.length; i++){
				this.setRequiredField(fields[i], tag);
			}			
			var usefields=["MEM_CUST_INFO_RSRV_STR2","MEM_CUST_INFO_RSRV_STR3","MEM_CUST_INFO_RSRV_STR4","MEM_CUST_INFO_RSRV_STR5"];
			for(var i=0; i<usefields.length; i++){
				this.setRequiredField(usefields[i], tag);
			}
			var birthdayFields=["MEM_CUST_INFO_BIRTHDAY"];
			var birthdayTag = (psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="A")? true : false;
			for(var i=0; i<birthdayFields.length; i++){
				this.setRequiredField(birthdayFields[i], birthdayTag);
			}
			/*组织机构、营业执照信息填写*/
			var eFields = ["MEM_CUST_INFO_legalperson", "MEM_CUST_INFO_termstartdate", "MEM_CUST_INFO_termenddate", "MEM_CUST_INFO_startdate"];
			var oFields = ["MEM_CUST_INFO_orgtype", "MEM_CUST_INFO_effectiveDate", "MEM_CUST_INFO_expirationDate"];
			var eTag = (psptTypeCode=="E")? true : false;
			var oTag = (psptTypeCode=="M")? true : false;
			for(var i=0; i<eFields.length; i++){
				this.setRequiredField(eFields[i], eTag);
				this.showFields(eFields[i], eTag);
			}
			for(var i=0; i<oFields.length; i++){
				this.setRequiredField(oFields[i], oTag);
				this.showFields(oFields[i], oTag);
			}
		},
		showFields:function(fieldId, flag){
			$("#li_"+fieldId).css("display", flag ? "" : "none");
			$("#"+fieldId).val(flag ? $("#"+fieldId).val() : "");
		},

		renderSpecialFieldByPsptId:function(psptId){//检查年龄是否在12-120之间，显示隐藏经办人栏	
			if(trim(psptId)!=""&&!this.checkAge(psptId)){		   	
				var fields=["MEM_CUST_INFO_AGENT_CUST_NAME","MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE","MEM_CUST_INFO_AGENT_PSPT_ID","MEM_CUST_INFO_AGENT_PSPT_ADDR","MEM_CUST_INFO_BIRTHDAY"];
				for(var i=0; i<fields.length; i++){
					this.setRequiredField(fields[i],true);
				}
			}else{
				var fields=["MEM_CUST_INFO_AGENT_CUST_NAME","MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE","MEM_CUST_INFO_AGENT_PSPT_ID","MEM_CUST_INFO_AGENT_PSPT_ADDR"];					 					
				for(var i=0; i<fields.length; i++){
					this.setRequiredField(fields[i], false);
				}
			}
			$("#MEM_CUST_INFO_BIRTHDAY").val(this.getBirthdayByPsptId(psptId));
		},
		
		// 根据身份证号码获取生日日期
		getBirthdayByPsptId:function(psptId) {
			var tmpStr="";
			if((psptId.length!=15) &&(psptId.length!=18)&&(psptId.length!=0)) {
			    alert("输入的证件号码位数错误！");
				return tmpStr;
			}
			if(psptId.length==15) {
				tmpStr= "19" + psptId.substring(6,12);
			}else {
		    	tmpStr = psptId.substring(6,14);
			}
			return tmpStr.substring(0,4) + "-" + tmpStr.substring(4,6) + "-" + tmpStr.substring(6);
		},
		
		chkPsptTypeCode:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var custNameObj,psptObj;
			var objVal = $.trim(obj.val());
			
			if(fieldName=="MEM_CUST_INFO_PSPT_TYPE_CODE"){
				custNameObj = $("#MEM_CUST_INFO_CUST_NAME");
				psptObj = $("#MEM_CUST_INFO_PSPT_ID");
				//------根据证件类型设置用户类型------
				$("#MEM_USER_INFO_USER_TYPE_CODE").attr("disabled", true);
				if("0"==objVal || "1"==objVal || "A"==objVal || "3"==objVal){
					$("#MEM_USER_INFO_USER_TYPE_CODE").val("0");	//个人
				}else if("E"==objVal || "M"==objVal || "G"==objVal || "D"==objVal){
					$("#MEM_USER_INFO_USER_TYPE_CODE").val("8");	//集团用户
				}else{
					$("#MEM_USER_INFO_USER_TYPE_CODE").val("");
				}
				this.chkCustName("MEM_CUST_INFO_CUST_NAME");
			}else if(fieldName=="MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE"){
				custNameObj = $("#MEM_CUST_INFO_AGENT_CUST_NAME");
				psptObj = $("#MEM_CUST_INFO_AGENT_PSPT_ID");
				this.chkCustName("MEM_CUST_INFO_AGENT_CUST_NAME");
			}else if(fieldName=="MEM_CUST_INFO_USE_PSPT_TYPE_CODE"){
				custNameObj = $("#MEM_CUST_INFO_USE");
				psptObj = $("#MEM_CUST_INFO_USE_PSPT_ID");
				this.chkCustName("MEM_CUST_INFO_USE");
			}else{
				custNameObj = $("#MEM_CUST_INFO_RSRV_STR2");
				psptObj = $("#MEM_CUST_INFO_RSRV_STR4");
				this.chkCustName("MEM_CUST_INFO_RSRV_STR2");
			}
			var psptTypeCode = obj.val();
			var custName = $.trim(custNameObj.val());
			
			//0-本地身份证 1-外地身份证 3-军人身份证
			psptObj.attr("datatype", (psptTypeCode==0 || psptTypeCode==1 || psptTypeCode==2 || psptTypeCode==3)? "pspt" : "");	
			
			if(psptTypeCode=="3"){
				alert("请提醒客户同时出示军人身份证明！并进行留存");
			}
			
			if(custName!="" && psptTypeCode != "A"){
		    	if(this.includeChineseCount(custName)<2){
		    		alert(obj.attr("desc")+"不是护照，"+custNameObj.attr("desc")+"不能少于2个中文字符！");
		    		custNameObj.val("");
					return ;
		    	}
			}
			
			if(fieldName != "MEM_CUST_INFO_PSPT_TYPE_CODE"){
				if(fieldName == "MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE"){
					$("#MEM_CUST_INFO_AGENT_PSPT_ID").trigger("change");
				}
				if(fieldName == "MEM_CUST_INFO_USE_PSPT_TYPE_CODE"){
					$("#MEM_CUST_INFO_AGENT_PSPT_ID").trigger("change");
				}
				if(fieldName == "MEM_CUST_INFO_RSRV_STR3"){
					$("#RSRV_STR4").trigger("change");
				}
				
				//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
				//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
				//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
				if(fieldName=="MEM_CUST_INFO_USE_PSPT_TYPE_CODE")
				{
					var objValue = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
					if(objValue=="D" || objValue=="E" || objValue=="G" || objValue=="L" || objValue=="M")
					{
						var useObj=$("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE");
						var useObjValue = useObj.val();
						
						if(useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M")
						{
							alert("证件类型选择集团证件，使用人的证件类型只能选择个人证件，请重新选择！");
							useObj.find("option[index=0]").attr("selected", true);
							$("#MEM_CUST_INFO_USE_PSPT_ID").attr("datatype", "");
							return;
						}
						
					}
					$("#MEM_CUST_INFO_USE_PSPT_ID").trigger("change");
				}
				
				if(fieldName=="MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE")
				{
					var objValue = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
						var agentObj=$("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE");
						var agentValue = agentObj.val();
						if(agentValue=="D" || agentValue=="E" || agentValue=="G" || agentValue=="L" || agentValue=="M")
						{	
							alert("证件类型选择集团证件，经办人的证件类型只能选择个人证件，请重新选择！");
							//alert("经办人只能选择个人证件，请重新选择！");
							agentObj.find("option[index=0]").attr("selected", true);
							$("#MEM_CUST_INFO_AGENT_PSPT_ID").attr("datatype", "");
							return;							
						}
											
					$("#MEM_CUST_INFO_AGENT_PSPT_ID").trigger("change");
				}
				
				return;
			}
			
			//实名制开户限制
			if(psptTypeCode == "Z" && this.isRealName){
				alert("实名制开户，证件类型不能为其他，请重新选择！");
				$(this).find("option[index=0]").attr("selected", true);
				psptObj.attr("datatype","");
				return;
			}
			
			$("#MEM_CUST_INFO_PSPT_ID").trigger("change");
			
			this.renderSpecialField(psptTypeCode);
			
			//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
			//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
			//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
			if(fieldName=="MEM_CUST_INFO_PSPT_TYPE_CODE" || fieldName=="MEM_CUST_INFO_USE_PSPT_TYPE_CODE") 
			{
				
				var objValue = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
				if(objValue=="D" || objValue=="E" || objValue=="G" || objValue=="L" || objValue=="M")
				{
					var useObj=$("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE");
					var useObjValue = useObj.val();
					
					if(useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M")
					{
						alert("证件类型选择集团证件，使用人的证件类型只能选择个人证件，请重新选择！");
						useObj.find("option[index=0]").attr("selected", true);
						$("#MEM_CUST_INFO_USE_PSPT_ID").attr("datatype", "");
						return;
					}					
				}
				
			}
			if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="3") {
				this.renderSpecialFieldByPsptId($("#MEM_CUST_INFO_PSPT_ID").val());
			}
			//this.verifyEnterpriseCard();			
			//this.verifyOrgCard();
			this.verifyIdCardName(fieldName);
			
		},
		chkPsptId:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var psptTypeObj;
			if(fieldName == "MEM_CUST_INFO_PSPT_ID"){
				psptTypeObj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE");
			}else if(fieldName == "MEM_CUST_INFO_AGENT_PSPT_ID"){
				psptTypeObj = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE");
			}else if(fieldName == "MEM_CUST_INFO_USE_PSPT_ID"){
				psptTypeObj = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE");
			}else{
				psptTypeObj = $("#MEM_CUST_INFO_RSRV_STR3");
			}
			var psptId = $.trim(obj.val());
			var desc = $.trim(obj.attr("desc"));
			var psptTypeCode = psptTypeObj.val();
			
			if(psptId=="") return;
			
			//经办人、责任人证件号码不能相同
			var agentid = $.trim($("#MEM_CUST_INFO_AGENT_PSPT_ID").val());
			var rsrv4id = $.trim($("#MEM_CUST_INFO_RSRV_STR4").val());
			if(agentid.length>0&&rsrv4id.length>0&&agentid==rsrv4id){
				 alert("经办人、责任人证件号码不能相同！");
				 $("#MEM_CUST_INFO_AGENT_PSPT_ID").val("");
				 $("#MEM_CUST_INFO_RSRV_STR4").val("");
				 return false;
			}
			if($.toollib.isRepeatCode(psptId)){
				alert(desc+"不能全为同一个数字，请重新输入!");
				obj.val("");
				obj.focus();
				return;
			}
			if($.toollib.isSerialCode(psptId)){
				alert("连续数字不能作为"+desc+"，请重新输入!");
				obj.val("");
				obj.focus();
				return;
			}
			/*if(psptId.length>=4 && this.serialNumber.length>=psptId.length && ( this.serialNumber.indexOf(psptId) ==0 
				|| this.serialNumber.lastIndexOf(psptId) == (this.serialNumber.length-psptId.length) )){
				alert("电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为"+desc+"!");
				obj.val("");
				obj.focus();
				return;
			}*/
			
			//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
			if(psptTypeCode=="H"){
				if(psptId.length!=9 && psptId.length!=11){
					alert("港澳居民回乡证校验："+desc+"必须为9位或11位。");
					obj.val("");
					obj.focus();
					return;
				}
				if( !(psptId.charAt(0)=="H" || psptId.charAt(0)=="M") || !$.toollib.isNumber(psptId.substr(1)) ){
					alert("港澳居民回乡证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
					obj.val("");
					obj.focus();
					return;
				}
			}else if(psptTypeCode=="O"){
				   var psptIdtemp = psptId.replace(/\s/g,''); 				 				
				    if(psptIdtemp!=psptId){
						alert("港澳居民来往内地通行证校验：证件号码中间不能有空格。");
						obj.val("");
						obj.focus();
						return;
				    }					    
				    //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
					if(psptId.length!=9 && psptId.length!=11){
						alert("港澳居民来往内地通行证校验："+desc+"必须为9位或11位。");
						obj.val("");
						obj.focus();
						return;
					}
					if( !(psptId.charAt(0)=="H" || psptId.charAt(0)=="M") || !$.toollib.isNumber(psptId.substr(1)) ){
						alert("港澳居民来往内地通行证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
						obj.val("");
						obj.focus();
						return;
					}
			}else if(psptTypeCode=="I"){		
				   //台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
				if(psptId.length!=8 && psptId.length!=11){
					alert("台湾居民回乡校验："+desc+"必须为8位或11位！");
					obj.val("");
					obj.focus();
					return false;
				}
				if(psptId.length==8){
					if(!$.toollib.isNumber(psptId)){
						alert("台湾居民回乡校验："+desc+"为8位时，必须均为阿拉伯数字！");
						obj.val("");
						obj.focus();
						return false;
					}
				}
				if(psptId.length==11){
					if(!$.toollib.isNumber(psptId.substring(0,10))){
						alert("台湾居民回乡校验：："+desc+"为11位时，前10位必须均为阿拉伯数字。");
						obj.val("");
						obj.focus();
						return false;
					}
				}
           }else if(psptTypeCode=="N"){
				//台湾居民来往大陆通行证:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
				var psptIdtemp = psptId.replace(/\s/g,''); 		
				if(psptIdtemp!=psptId){
					alert("台湾居民来往大陆通行证校验：证件号码中间不能有空格。");
					obj.val("");
					obj.focus();
					return;
			    }
				if(psptId.substr(0, 2)!="TW"&&psptId.substr(0, 4)!="LXZH"){				
					if(psptId.length==11 ||psptId.length==12){
						if(!$.toollib.isNumber(psptId.substring(0,10))){
							alert("台湾居民来往大陆通行证校验："+desc+"为11或12位时，前10位必须均为阿拉伯数字。");
							obj.val("");
							obj.focus();
							return;
						}
					}else if(psptId.length==8){
						if(!$.toollib.isNumber(psptId)){
							alert("台湾居民来往大陆通行证校验："+desc+"为8位时，必须均为阿拉伯数字。");
							obj.val("");
							obj.focus();
							return;
						}
					}else if(psptId.length==7){
						if(!$.toollib.isNumber(psptId)){
							alert("台湾居民来往大陆通行证校验："+desc+"为7位时，必须均为阿拉伯数字。");
							obj.val("");
							obj.focus();
							return;
						}
					}else{
						alert("台湾居民来往大陆通行证校验："+desc+"格式错误");
						obj.val("");
						obj.focus();
						return;
					}
				}else{
					var psptIdsub ;
					if(psptId.substr(0, 2)=="TW"){
						psptIdsub = psptId.substr(2);
					}else if(psptId.substr(0, 4)=="LXZH"){
						psptIdsub = psptId.substr(4);
					}
					
					var re=/^[()A-Z0-9]+$/;
					var re1=/^[•··.．·\d\u4e00-\u9fa5]+$/;
					var pattern1 =/[A-Z]/;
					var pattern2 =/[0-9]/;
					var pattern3 =/[(]/;
					var pattern4 =/[)]/;
					
					if(re1.test(psptIdsub)||!re.test(psptIdsub)||!pattern1.test(psptIdsub)||!pattern2.test(psptIdsub)||!pattern3.test(psptIdsub)||!pattern4.test(psptIdsub)){
						alert("台湾居民来往大陆通行证校验："+desc+"前2位“TW”或 “LXZH”字符时，后面是阿拉伯数字、英文大写字母与半角“()”的组合");
						obj.val("");
						obj.focus();
						return;
					}				
				}
			}else if(psptTypeCode=="C" || psptTypeCode=="A"){
				var psptIdtemp = psptId.replace(/\s/g,''); 				 				
				if(psptIdtemp!=psptId){
					alert("证件号码中间不能有空格。");
					obj.val("");
					obj.focus();
					return;
			    }
				//军官证、警官证、护照：证件号码须大于等于6位字符
				if(psptId.length < 6){
					var tmpName= psptTypeCode=="A" ? "护照校验：" : "军官证类型校验：";
					alert(tmpName+desc+"须大于等于6位字符!");
					obj.val("");
					obj.focus();
					return;
				}
			}else if(psptTypeCode=="E"){
				//营业执照：证件号码长度需满足15位 20151022 REQ201510140003 营业执照证件规则调整 CHENXY3
				if(psptId.length != 13 && psptId.length != 15 && psptId.length != 18 && psptId.length != 20 && psptId.length != 22 && psptId.length != 24){
					alert("营业执照类型校验："+desc+"长度需满足13位、15位、18位、20位、22位或24位！当前："+psptId.length+"位。");
					obj.val("");
					obj.focus();
					return;
				}
			}else if(psptTypeCode=="M"){
				//组织机构代码校验
				if(psptId.length != 10 && psptId.length != 18 && psptId.length != 20){
					alert("组织机构代码证类型校验："+desc+"长度需满足10位或18位或20位。");
					obj.val("");
					obj.focus();
					return;
				}
				if(psptId.length == 10 && psptId.charAt(8) != "-"){
					alert("组织机构代码证类型校验："+desc+"规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
					obj.val("");
					obj.focus();
					return;
				}
			}else if(psptTypeCode=="G"){
				//事业单位法人登记证书：证件号码长度需满足12位
				if(psptId.length != 12 && psptId.length != 18){
					alert("事业单位法人登记证书类型校验："+desc+"长度需满足12位或者18位。");
					obj.val("");
					obj.focus();
					return;
				}
			}else if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="3") {
				 if(psptId.substr(psptId.length-1,psptId.length)=="x"){
					obj.val(psptId.substr(0,psptId.length-1)+"X");
				 }
				 debugger;
				 if(fieldName == "MEM_CUST_INFO_AGENT_PSPT_ID" || fieldName == "MEM_CUST_INFO_USE_PSPT_ID" || fieldName == "MEM_CUST_INFO_RSRV_STR4" ){
					 if(trim(psptId)!=""&&!this.checkAge(psptId)){
				     	alert('身份证年龄范围必须在11-120岁之间'); 
						obj.val("");
						obj.focus();
						return;
					 }
				 } 
				

				
				//身份证相关检查
				obj.attr("datatype", "pspt");
				if(!$.validate.verifyField(obj[0])) {
					//alert(psptTypeObj.find("option[value="+psptTypeCode+"]").text()+"号码格式不对。");
					obj.val("");
					obj.focus();
					return;
				}else{
					obj.attr("datatype", "");
				}
				
				
				var area = {11:"\u5317\u4eac", 12:"\u5929\u6d25", 13:"\u6cb3\u5317", 14:"\u5c71\u897f", 15:"\u5185\u8499\u53e4", 21:"\u8fbd\u5b81", 22:"\u5409\u6797", 23:"\u9ed1\u9f99\u6c5f", 31:"\u4e0a\u6d77", 32:"\u6c5f\u82cf", 33:"\u6d59\u6c5f", 34:"\u5b89\u5fbd", 35:"\u798f\u5efa", 36:"\u6c5f\u897f", 37:"\u5c71\u4e1c", 41:"\u6cb3\u5357", 42:"\u6e56\u5317", 43:"\u6e56\u5357", 44:"\u5e7f\u4e1c", 45:"\u5e7f\u897f", 46:"\u6d77\u5357", 50:"\u91cd\u5e86", 51:"\u56db\u5ddd", 52:"\u8d35\u5dde", 53:"\u4e91\u5357", 54:"\u897f\u85cf", 61:"\u9655\u897f", 62:"\u7518\u8083", 63:"\u9752\u6d77", 64:"\u5b81\u590f", 65:"\u65b0\u7586", 71:"\u53f0\u6e7e", 81:"\u9999\u6e2f", 82:"\u6fb3\u95e8", 91:"\u56fd\u5916"};
			
				psptId = psptId.toUpperCase();
				
				if (area[parseInt(psptId.substr(0, 2))] == null) {
					alert("\u8eab\u4efd\u8bc1\u53f7\u7801\u4e0d\u6b63\u786e\u0028\u5730\u533a\u975e\u6cd5\u0029\uff01");
					obj.val("");
					obj.focus();
					return;
				}
				
				if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(psptId))) {
					alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u957f\u5ea6\u4e0d\u5bf9\uff0c\u6216\u8005\u53f7\u7801\u4e0d\u7b26\u5408\u89c4\u5b9a\uff01\n15\u4f4d\u53f7\u7801\u5e94\u5168\u4e3a\u6570\u5b57\uff0c18\u4f4d\u53f7\u7801\u672b\u4f4d\u53ef\u4ee5\u4e3a\u6570\u5b57\u6216X\u3002 ");
					obj.val("");
					obj.focus();
					return;
				}
				
			    // 下面分别分析出生日期和校验位
				var len, re;
				len = psptId.length;
				var arrSplit = "";
				var dtmBirth = "";
				
				if (len == 15) {
					re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
					arrSplit = psptId.match(re);  // 检查生日日期是否正确
					dtmBirth = new Date("19" + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
				}
				
				if (len == 18) {
					re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
					arrSplit = psptId.match(re);  // 检查生日日期是否正确
					dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
				}
				
				var bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
				if (!bGoodDay) {
					alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u51fa\u751f\u65e5\u671f\u4e0d\u5bf9\uff01");
					obj.val("");
					obj.focus();
					return;
				}
				
				/*每位加权因子*/
				var powers= new Array("7","9","10","5","8","4","2","1","6","3","7","9","10","5","8","4","2");
				/*第18位校检码*/
				var parityBit=new Array("1","0","X","9","8","7","6","5","4","3","2");
				var psptBit = psptId.charAt(17).toUpperCase();
				var id17 = psptId.substring(0,17);    
			   /*加权 */
			   var power = 0;
			   for(var i=0;i<17;i++){
			     power += parseInt(id17.charAt(i),10) * parseInt(powers[i]);
			   }              
			   /*取模*/ 
			   var mod = power % 11;
			   var checkBit = parityBit[mod];
			   if(psptBit!=checkBit){
			   		alert('身份证号码不合法'); 
					obj.val("");
					obj.focus();
					return;
			   }
			   
			   var bit11 = psptId.substring(10,11);
    		   var bit13 = psptId.substring(12,13);
			   if(bit11!="0"&&bit11!="1")
			   {
			   		alert('18位身份证号码11位必须为0或者1');
					obj.val("");
					obj.focus();
					return;
			   }
			   if(parseInt(bit13)>3)
			   {
			     	alert('18位身份证号码13位必须小于等于3'); 
					obj.val("");
					obj.focus();
					return;
			   }
			   
			   this.verifyIdCard(fieldName);
			   
				//设置生日日期
				if(fieldName == "MEM_CUST_INFO_PSPT_ID"){
					this.renderSpecialFieldByPsptId(psptId);
				}
			}
			//this.verifyEnterpriseCard();
			//this.verifyOrgCard();
			this.verifyIdCardName(fieldName);

			if(!this.isRealName || fieldName != "MEM_CUST_INFO_PSPT_ID") {
				if( fieldName=="MEM_CUST_INFO_USE_PSPT_ID" )
				{
					if(!this.checkUsePsptIdForReal()) {
						obj.val("");
						obj.focus();
						return;
					}
					this.checkRealNameLimitByUsePspt();
				}
				
				return;
			}
			
			if(!this.checkPsptIdForReal()) {
				obj.val("");
				obj.focus();
				return;
			}
			
			if(fieldName == "MEM_CUST_INFO_PSPT_ID" && this.isRealName)
			{
				if(psptTypeCode=="E" || psptTypeCode=="M" || psptTypeCode=="G" || psptTypeCode=="D")
				{
					var productId = $.trim($("#PRODUCT_ID").val());
					if(psptId != "" && productId == "801110")
					{
						this.checkRealNameLimitByPspt();
					}
				}
			}
			
//			this.checkRealNameLimitByPspt();
			this.checkPsptidBlackListInfo();
			 
		},
		afterChkPsptId:function(){
			var obj =$(this)
			if(obj.val()=="") return;
		},
		clickScanPspt:function()
		{
			getMsgByEForm("MEM_CUST_INFO_PSPT_ID","MEM_CUST_INFO_CUST_NAME,MEM_ACCT_INFO_PAY_NAME",null,null,null,"MEM_CUST_INFO_PSPT_ADDRESS",null,null);
			this.verifyIdCard("MEM_CUST_INFO_PSPT_ID");
			this.verifyIdCardName("MEM_CUST_INFO_PSPT_ID");
			
			if(!this.isRealName) 
			{
				return;
			}
//			this.checkRealNameLimitByPspt();
			$("#MEM_CUST_INFO_PSPT_ID").trigger("change");
		},
		clickScanPspt2:function(){
			getMsgByEForm("MEM_CUST_INFO_AGENT_PSPT_ID","MEM_CUST_INFO_AGENT_CUST_NAME",null,null,null,"MEM_CUST_INFO_AGENT_PSPT_ADDR",null,null);
			this.verifyIdCard("MEM_CUST_INFO_AGENT_PSPT_ID");
			this.verifyIdCardName("MEM_CUST_INFO_AGENT_PSPT_ID");
			if(!this.isRealName) {
				return;
			}
			$("#MEM_CUST_INFO_AGENT_PSPT_ID").trigger("change");
		},
		clickScanPspt3:function(){
			getMsgByEForm("MEM_CUST_INFO_USE_PSPT_ID","MEM_CUST_INFO_USE",null,null,null,"MEM_CUST_INFO_USE_PSPT_ADDR",null,null);
			this.verifyIdCard("MEM_CUST_INFO_USE_PSPT_ID");
			this.verifyIdCardName("MEM_CUST_INFO_USE_PSPT_ID");
			if(!this.isRealName) {
				return;
			}
			$("#MEM_CUST_INFO_USE_PSPT_ID").trigger("change");
		},
		clickScanPspt4:function(){
			getMsgByEForm("MEM_CUST_INFO_RSRV_STR4","MEM_CUST_INFO_RSRV_STR2",null,null,null,"MEM_CUST_INFO_RSRV_STR5",null,null);
			this.verifyIdCard("MEM_CUST_INFO_RSRV_STR4");
			this.verifyIdCardName("MEM_CUST_INFO_RSRV_STR4");
			if(!this.isRealName) {
				return;
			}
			$("#MEM_CUST_INFO_RSRV_STR4").trigger("change");
		},
		
		chkCustName:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var psptTypeCode,psptTypeDesc;
			//判断到底是客户姓名还是经办人姓名
			if(fieldName=="MEM_CUST_INFO_CUST_NAME"){
				psptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").attr("desc");
			}else if(fieldName=="MEM_CUST_INFO_AGENT_CUST_NAME"){
				psptTypeCode = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").attr("desc");
			}else if(fieldName=="MEM_CUST_INFO_USE"){
				psptTypeCode = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE").attr("desc");
			}else{
				psptTypeCode = $("#MEM_CUST_INFO_RSRV_STR3").val();
			    psptTypeDesc = $("#MEM_CUST_INFO_RSRV_STR3").attr("desc");
			}
			var custName = $.trim(obj.val());
			var desc = obj.attr("desc");
			
		    if(!custName) return;
		    		    		
		    if(psptTypeCode == "E"||psptTypeCode == "G"||psptTypeCode == "M"){//	营业执照、组织机构代码证、事业单位法人登记证书
		    	if(this.includeChineseCount(custName)<4){
					alert(psptTypeDesc+"不是护照,"+desc+"不能少于4个中文字符！");
					obj.val("");
					obj.focus();
					return;
				}
		    }	
		    
		    var mainPsptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
		    if(mainPsptTypeCode == "E"||mainPsptTypeCode == "G"||mainPsptTypeCode == "D" ||mainPsptTypeCode == "M" ||mainPsptTypeCode == "L"){
		    	//	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
		    	var custnametemp = trim($("#MEM_CUST_INFO_CUST_NAME").val());
		    	var agentnametemp = trim($("#MEM_CUST_INFO_AGENT_CUST_NAME").val());
		    	var usenametemp = trim($("#MEM_CUST_INFO_USE").val());
		    	if(custnametemp!=""&&agentnametemp!=""&&custnametemp==agentnametemp){
					alert("单位名称和经办人名称不能相同！");
					obj.val("");
					obj.focus();
					return;
		    	}
		    	if(custnametemp!=""&&usenametemp!=""&&custnametemp==usenametemp){
					alert("单位名称和使用人名称不能相同！");
					obj.val("");
					obj.focus();
					return;
		    	}
		    }
		    		    		    
		    if(psptTypeCode != "A" && psptTypeCode != "D"){
		    	var pattern =/[a-zA-Z0-9]/;
		    	if(pattern.test(custName)){
		    		alert(psptTypeDesc+"不是护照, "+desc+"不能包含数字和字母！");
		    		obj.val("");
		    		obj.focus();
					return;
		    	}
				if(this.includeChineseCount(custName)<2){
					alert(psptTypeDesc+"不是护照,"+desc+"不能少于2个中文字符！");
					obj.val("");
					obj.focus();
					return;
				}
				
			}else if(psptTypeCode == "A"){
				/*护照：客户名称须大于三个字符，不能全为阿拉伯数字*/
				if(custName.length<3 || $.toollib.isNumber(custName)){
					alert(psptTypeDesc+"是护照,"+desc+"须大于三个字符，且不能全为阿拉伯数字！");
					obj.val("");
					obj.focus();
					return;
				}
				var specialStr ="“”‘’，《》~！@#￥%……&*（）【】｛｝；：‘’“”，。、《》？+——-=";
				for(i=0;i<specialStr.length;i++){
					if (custName.indexOf(specialStr.charAt(i)) > -1){
						alert(obj.attr("desc")+"包含特殊字符，请检查！");
						obj.val("");
						obj.focus();
						return;
					}
				}
			}
			if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="3" || psptTypeCode=="C"|| psptTypeCode=="H"|| psptTypeCode=="I"|| psptTypeCode=="N"|| psptTypeCode=="O"){
				var re=/^[•··.．·\d\u4e00-\u9fa5]+$/;
				if(!re.test(custName)){
					alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				}
			}else if(psptTypeCode=="H"|| psptTypeCode=="I"||psptTypeCode=="J"){
				//	港澳证、台胞证
				if(custName.length<2){
					alert(psptTypeDesc+"是护照,"+desc+"须两个字符及以上");
					obj.val("");
					obj.focus();
					return;
				}
				var re=/^[•··.．·\d\u4e00-\u9fa5]+$/;
				if(!re.test(custName)){
					alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				}
			}
		    
		    /*if(custName.indexOf("校园")>-1 || custName.indexOf("海南通")>-1 || custName.indexOf("神州行")>-1
		    		|| custName.indexOf("动感地带")>-1 || custName.indexOf("套餐")>-1) {
		        alert(desc+"不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！");
		        obj.val("");
		        obj.focus();
		        return false;
		    }*/
            if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2"|| psptTypeCode == "3"
                || psptTypeCode == "O" || psptTypeCode == "N" || psptTypeCode == "P"
                || psptTypeCode == "Q" || psptTypeCode == "W"	) {
				this.verifyIdCard(fieldName);
			}
		    //this.verifyEnterpriseCard();
		    //this.verifyOrgCard();
			this.verifyIdCardName(fieldName);

		    //如果不需要进行实名制，或者是非客户姓名，则不需要进行实名制校验限制
		    if(!this.isRealName || fieldName!="MEM_CUST_INFO_CUST_NAME") {
		    	if(fieldName=="MEM_CUST_INFO_USE"){
					if(this.checkUsePsptIdForReal()) {
						this.checkRealNameLimitByUsePspt();
					}
				}
		    	
				return;
			}
			
			if(this.checkPsptIdForReal()) {
//				this.checkRealNameLimitByPspt();
			}
			
			if(fieldName == "MEM_CUST_INFO_CUST_NAME" && this.isRealName)
			{
				if(psptTypeCode=="E" || psptTypeCode=="M" || psptTypeCode=="G" || psptTypeCode=="D")
				{
					var productId = $.trim($("#PRODUCT_ID").val());
					var psptId = $.trim($("#MEM_CUST_INFO_PSPT_ID").val());
					if(psptId != "" && productId == "801110")
					{
						this.checkRealNameLimitByPspt();
					}
				}
			}
			
		},
		includeChinese:function(custName){
			// 是否包含中文字符
			for(var i=0; i<custName.length; i++){
				if($.toollib.isChinese(custName.charAt(i))){
					return true;
				}
			}
			return false;
		},
		includeChineseCount:function(custName){
			// 是否包含中文字符个数
			var count = 0;
			for(var i=0; i<custName.length; i++){
				if($.toollib.isChinese(custName.charAt(i))){
					count++;
				}
			}
			return count;
		},		
		afterChkCustName:function(){
			var obj = $("#MEM_CUST_INFO_CUST_NAME");
			var custName = $.trim(obj.val());
			
			if(custName == ""){
				return;
		    }
			var mainPsptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
		    if(mainPsptTypeCode == "E"||mainPsptTypeCode == "G"||mainPsptTypeCode == "D" ||mainPsptTypeCode == "M" ||mainPsptTypeCode == "L"){
		    	//	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书  不进行特殊字符校验
		    	
		    }else{					
			  var specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
			  for(i=0;i<specialStr.length;i++){
				 if (custName.indexOf(specialStr.charAt(i)) > -1){
				  	alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				 }
			  }
		    }
			//判断如果存在账户名字，则设置账户名
			if(this.relatePayName && $("#MEM_ACCT_INFO_PAY_NAME") && $("#MEM_ACCT_INFO_PAY_NAME").length){
				$("#MEM_ACCT_INFO_PAY_NAME").val(custName);					
			}
		},
		chkPsptAddr:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var psptAddr = $.trim(obj.val());
			var desc = obj.attr("desc");
			
			if(psptAddr=="" || psptAddr.length<6){
				alert(desc+"栏录入文字需不少于6个！");
				obj.val("");
				obj.focus();
				return;
			}
			
			if($.toollib.isNumber(psptAddr)){
				alert(desc+"栏不能全部为数字！");
				obj.val("");
				obj.focus();
				return;
			}
		},
		afterChkAgentCustName:function()
		{
			var agentval = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
			//$("#MEM_CUST_INFO_RSRV_STR2").val(agentval);
		},
		afterChkAgentPsptTypeCode:function()
		{
			var agentval = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
			//$("#MEM_CUST_INFO_RSRV_STR3").val(agentval);
		},	
		afterChkAgentPsptId:function()
		{
			var agentval = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
			//$("#MEM_CUST_INFO_RSRV_STR4").val(agentval);
		},
		afterChkAgentPsptAddr:function()
		{
			var agentval = $("#MEM_CUST_INFO_AGENT_PSPT_ADDR").val();
			//$("#MEM_CUST_INFO_RSRV_STR5").val(agentval);
		},
		/*****************************以下为客户校验辅助校验部分********************************************/
		//实名制基本数据校验
		checkPsptIdForReal:function(){
		    if($("#MEM_CUST_INFO_PSPT_TYPE_CODE").val()=="Z") {
		        alert("实名制开户，证件类型不能为其他，请重新选择！");
		        return false;
		    }
		    var psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
		    if($.toollib.isRepeatCode(psptId))
		    {
		        alert("实名制开户，证件号码过于简单，请重新输入！");
		        return false;
		    }
		    /*if($("#MEM_CUST_INFO_CUST_NAME").val().indexOf("海南通")>-1)
		    {
		        alert("实名制开户，客户名称不能为【海南通】，请重新输入！");
		        $("#MEM_CUST_INFO_CUST_NAME").val("");
		        return false;
		    }*/
		    if(!$.validate.verifyField("MEM_CUST_INFO_PSPT_ID")){
		        return false;
		    }
		    return true;
		},
		//使用人基本数据校验
		checkUsePsptIdForReal:function(){
		    if($("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE").val()=="Z") {
		        alert("实名制开户，使用人证件类型不能为其他，请重新选择！");
		        return false;
		    }
		    var psptId = $("#MEM_CUST_INFO_USE_PSPT_ID").val();
		    if($.toollib.isRepeatCode(psptId))
		    {
		        alert("实名制开户，使用人证件号码过于简单，请重新输入！");
		        return false;
		    }
		    /*if($("#MEM_CUST_INFO_USE").val().indexOf("海南通")>-1)
		    {
		        alert("实名制开户，使用人姓名不能为【海南通】，请重新输入！");
		        $("#MEM_CUST_INFO_USE").val("");
		        return false;
		    }*/
		    if(!$.validate.verifyField("MEM_CUST_INFO_USE_PSPT_ID")){
		        return false;
		    }
		    return true;
		},
		//在线实名制校验
		verifyIdCard:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var custNameObj,psptObj,psptTypeObj;
			if(fieldName == "MEM_CUST_INFO_PSPT_ID"||fieldName=="MEM_CUST_INFO_CUST_NAME"){
				custNameObj = $("#MEM_CUST_INFO_CUST_NAME");
				psptObj = $("#MEM_CUST_INFO_PSPT_ID");
				psptTypeObj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE"); 
			}else if(fieldName == "MEM_CUST_INFO_AGENT_PSPT_ID"||fieldName=="MEM_CUST_INFO_AGENT_CUST_NAME"){
				custNameObj = $("#MEM_CUST_INFO_AGENT_CUST_NAME");
				psptObj = $("#MEM_CUST_INFO_AGENT_PSPT_ID");
				psptTypeObj = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE");
			}else if(fieldName == "MEM_CUST_INFO_USE_PSPT_ID"||fieldName=="MEM_CUST_INFO_USE"){
				custNameObj = $("#MEM_CUST_INFO_USE");
				psptObj = $("#MEM_CUST_INFO_USE_PSPT_ID");
				psptTypeObj = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE");
			}else{
				custNameObj = $("#MEM_CUST_INFO_RSRV_STR2");
				psptObj = $("#MEM_CUST_INFO_RSRV_STR4");
				psptTypeObj = $("#MEM_CUST_INFO_RSRV_STR3");
			}
		    var psptId = $.trim(psptObj.val());
		    var psptName = $.trim(custNameObj.val());
		    if( psptId == ""||psptName==""){
		        return false;
		    }
//		    var param =  "&CERT_ID="+psptId +"&CERT_NAME="+encodeURIComponent(psptName);
		    var param =  "&CERT_ID="+psptId +"&CERT_TYPE="+$.trim(psptTypeObj.val())+"&CERT_NAME="+encodeURIComponent(psptName);
		    $.beginPageLoading("在线实名制校验。。。");
			$.httphandler.get(this.clazz, "verifyIdCard", param, 
				function(data){
					$.endPageLoading();
					if(data && data.get("X_RESULTCODE")== "1"){ 
						//MessageBox.error("告警提示","证件信息不合法",null, null, null, null);
						MessageBox.error("告警提示","该证件在公安部系统校验不通过，建议用户到当地派出所核对自己的证件信息。",null, null, null, null);
						psptObj.val("");
						return false;
					}else if(data && data.get("X_RESULTCODE")== "2"){ 
						MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);						
						return true;
					}else{
						return true;
					}
				},function(code, info, detail){
					$.endPageLoading(); 
					MessageBox.error("错误提示","在线实名制校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "在线实名制校验失败");
			});
				
		},
		//	营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
		verifyIdCardName:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var custNameObj,psptObj,psptcodeObj;
			if(fieldName == "MEM_CUST_INFO_PSPT_ID"||fieldName=="MEM_CUST_INFO_CUST_NAME"){
				custNameObj = $("#MEM_CUST_INFO_CUST_NAME");
				psptObj = $("#MEM_CUST_INFO_PSPT_ID");
				psptcodeObj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE");
			}else if(fieldName == "MEM_CUST_INFO_AGENT_PSPT_ID"||fieldName=="MEM_CUST_INFO_AGENT_CUST_NAME"){
				custNameObj = $("#MEM_CUST_INFO_AGENT_CUST_NAME");
				psptObj = $("#MEM_CUST_INFO_AGENT_PSPT_ID");
				psptcodeObj = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE");
				//return false;
			}else if(fieldName == "MEM_CUST_INFO_USE_PSPT_ID"||fieldName=="MEM_CUST_INFO_USE"){
				custNameObj = $("#MEM_CUST_INFO_USE");
				psptObj = $("#MEM_CUST_INFO_USE_PSPT_ID");
				psptcodeObj = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE");
				//return false;
			}else{
				custNameObj = $("#MEM_CUST_INFO_RSRV_STR2");	//责任人姓名
				psptObj = $("#MEM_CUST_INFO_RSRV_STR4");		//责任证件号码
				psptcodeObj = $("#MEM_CUST_INFO_RSRV_STR3");	//责任人证件类型
				//return false;
			}
		    var psptId = $.trim(psptObj.val());
		    var psptName = $.trim(custNameObj.val());
		    var psptcode = $.trim(psptcodeObj.val());
		    if( psptId == ""||psptName==""||psptcode==""){
		        return false;
		    }
		    
		    //-----begin----REQ201701160002关于集团客户界面优化需求------
		    var param =  "&CERT_TYPE="+psptcode+"&CERT_ID="+psptId +"&CERT_NAME="+encodeURIComponent(psptName);
		    if(psptcode=="0"||psptcode=="1"||psptcode=="2"||psptcode=="3")
		    {//《一证多名需求》，除了本地外地身份证和户口军人身份证，其他证件都不允许一证多名
		    	if(fieldName == "MEM_CUST_INFO_PSPT_ID"||fieldName=="MEM_CUST_INFO_CUST_NAME"||fieldName=="MEM_CUST_INFO_PSPT_TYPE_CODE"
					||fieldName == "MEM_CUST_INFO_USE_PSPT_ID"||fieldName=="MEM_CUST_INFO_USE"||fieldName=="MEM_CUST_INFO_USE_PSPT_TYPE_CODE")
		    	{										  					  
		    	    $.beginPageLoading("个人证件一证多号校验。。。");
				    $.httphandler.get(this.clazz, "verifyIdCardName", param, 
						function(data){
							$.endPageLoading();
							if(data && data.get("X_RESULTCODE")!= "0"){ 
								MessageBox.error("告警提示","该证件开户的号码已经达到5个，不能再开户",null, null, null, null);
								psptObj.val("");
								return false;
							}
						},function(code, info, detail){
							$.endPageLoading(); 
							MessageBox.error("错误提示","【该证件开户的号码达到5个】校验获取后台数据错误！",null, null, info, detail);
						},function(){
							$.endPageLoading();
							MessageBox.alert("告警提示", "【该证件开户的号码达到5个】校验失败");
					    }
					);					    
				}else
				{   
				  return true;
				}
            }else{		    		    
		        $.beginPageLoading("单位名称校验。。。");
			    $.httphandler.get(this.clazz, "verifyIdCardName", param, 
					function(data){
						$.endPageLoading();
						if(data && data.get("X_RESULTCODE")!= "0"){ 
							MessageBox.error("告警提示","同一个证件号码不能对应不同的名称",null, null, null, null);
							psptObj.val("");
							return false;
						}else{
							this.verifyEnterpriseCard();
							//REQ201908150006-申请优化单位证件开户及校验的需求-guonj-去掉验证
							//this.verifyOrgCard();	
						}
					},function(code, info, detail){
						$.endPageLoading(); 
						MessageBox.error("错误提示","【同一个证件号码不能对应不同的单位名称】校验获取后台数据错误！",null, null, info, detail);
					},function(){
						$.endPageLoading();
						MessageBox.alert("告警提示", "【同一个证件号码不能对应不同的单位名称】校验失败");
					}
				);	
            }
		    //----end---REQ201701160002关于集团客户界面优化需求----------
		},
		//营业执照在线校验
		verifyEnterpriseCard:function(){			 
			debugger;		 
		    var psptcode = $.trim($("#MEM_CUST_INFO_PSPT_TYPE_CODE").val());
		    var psptId = $.trim($("#MEM_CUST_INFO_PSPT_ID").val());
		    var psptName = $.trim($("#MEM_CUST_INFO_CUST_NAME").val());
		    var legalperson = $.trim($("#MEM_CUST_INFO_legalperson").val());
		    var termstartdate = $.trim($("#MEM_CUST_INFO_termstartdate").val());
		    var termenddate = $.trim($("#MEM_CUST_INFO_termenddate").val());
		    var startdate = $.trim($("#MEM_CUST_INFO_startdate").val());
		    //营业执照 
		    if(psptcode!="E"){
            	return false;
            }
		    if(psptId == ""||psptName==""||psptcode==""||legalperson==""||termstartdate==""||termenddate==""||startdate==""){
		       return false;
		    }
		    //termstartdate = termstartdate.replace("年","-").replace("月","-").replace("日","");
		    //termenddate = termenddate.replace("年","-").replace("月","-").replace("日","");
		    //startdate = startdate.replace("年","-").replace("月","-").replace("日","");
		    var param =  "&regitNo="+psptId+"&enterpriseName="+encodeURIComponent(psptName)+"&legalperson="+encodeURIComponent(legalperson)
		                 +"&termstartdate="+encodeURIComponent(termstartdate)+"&termenddate="+encodeURIComponent(termenddate)+"&startdate="+encodeURIComponent(startdate); 
		    $.beginPageLoading("营业执照校验。。。");
			$.httphandler.get(this.clazz, "verifyEnterpriseCard", param, 
				function(data){
					$.endPageLoading(); 
					if(data && data.get("X_RESULTCODE")!= "0"){
						MessageBox.error("营业执照校验告警提示",data.get("X_RESULTINFO"),null, null, null, null);
						$("#MEM_CUST_INFO_PSPT_ID").val('');
						return false;
					}else{
						return true;
					}
				},function(code, info, detail){
					$.endPageLoading(); 
					MessageBox.error("营业执照校验错误提示","校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("营业执照校验告警提示", "校验失败");
			});	
		},	
		//组织机构代码在线校验
		verifyOrgCard:function(){			 
			//REQ201908150006-申请优化单位证件开户及校验的需求-guonj-去掉验证
			/*debugger;		 
		    var psptcode = $.trim($("#MEM_CUST_INFO_PSPT_TYPE_CODE").val());
		    var psptId = $.trim($("#MEM_CUST_INFO_PSPT_ID").val());
		    var psptName = $.trim($("#MEM_CUST_INFO_CUST_NAME").val());
		    var orgtype = $.trim($("#MEM_CUST_INFO_orgtype").val());
		    var effectiveDate = $.trim($("#MEM_CUST_INFO_effectiveDate").val());
		    var expirationDate = $.trim($("#MEM_CUST_INFO_expirationDate").val());		    
		    //组织机构代码证
		    if(psptcode!="M"){
            	return false;
            }
		    if(psptId == ""||psptName==""||psptcode==""||orgtype==""||effectiveDate==""||expirationDate==""){
		       return false;
		    }
		    //effectiveDate = effectiveDate.replace("年","-").replace("月","-").replace("日","");
		    //expirationDate = expirationDate.replace("年","-").replace("月","-").replace("日","");
		    var param =  "&orgCode="+psptId+"&orgName="+encodeURIComponent(psptName)+"&orgtype="+encodeURIComponent(orgtype)
		                 +"&effectiveDate="+encodeURIComponent(effectiveDate)+"&expirationDate="+encodeURIComponent(expirationDate); 
		    
		    $.beginPageLoading("组织机构代码证校验。。。");
			$.httphandler.get(this.clazz, "verifyOrgCard", param, 
				function(data){
					$.endPageLoading(); 
					if(data && data.get("X_RESULTCODE")!= "0"){ 
						MessageBox.error("组织机构代码证校验告警提示",data.get("X_RESULTINFO"),null, null, null, null);
						$("#MEM_CUST_INFO_PSPT_ID").val('');
						return false;
					}else{
						return true;
					}
				},function(code, info, detail){
					$.endPageLoading(); 
					MessageBox.error("组织机构代码证校验错误提示","校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("组织机构代码证校验告警提示", "校验失败");
			});*/	
		},
		checkAge:function(idCard){
			if(!idCard){return false;}
			var _age = this.jsGetAge(idCard);			
			/*
			var bstr = idCard.substring(6,14)
			var _now = new Date();
			var _bir = new Date(bstr.substring(0,4),bstr.substring(4,6),bstr.substring(6,8));
			var _agen = _now-_bir;
			var _age = Math.round(_agen/(365*24*60*60*1000));*/
			return _age>=11 && _age<120;
		},		
		jsGetAge:function(idCard){				 
		    var returnAge;
			var bstr = idCard.substring(6,14)			 
		    var birthYear = bstr.substring(0,4);
		    var birthMonth = bstr.substring(4,6);
		    var birthDay = bstr.substring(6,8);
		    
		    var d = new Date();
		    var nowYear = d.getFullYear();
		    var nowMonth = d.getMonth() + 1;
		    var nowDay = d.getDate();
		    
		    if(nowYear == birthYear)
		    {
		        returnAge = 0;//同年 则为0岁
		    }
		    else
		    {
		        var ageDiff = nowYear - birthYear ; //年之差
		        if(ageDiff > 0)
		        {
				/*
		            if(nowMonth == birthMonth)
		            {
		                var dayDiff = nowDay - birthDay;//日之差
		                if(dayDiff < 0)
		                {
		                    returnAge = ageDiff - 1;
		                }
		                else
		                {
		                    returnAge = ageDiff ;
		                }
		            }
		            else
		            { */
		                var monthDiff = nowMonth - birthMonth;//月之差
		                if(monthDiff <= 0)
		                {
		                    returnAge = ageDiff - 1;
		                }
		                else
		                {
		                    returnAge = ageDiff ;
		                }
		           // }
		        }
		        else
		        {
		            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
		        }
		    }
		    return returnAge;//返回周岁年龄		    
		},

		//检查同一证件号已开实名制用户的数量是否已超出预定值
		checkRealNameLimitByPspt:function(){
			debugger;
		    var custName = $("#MEM_CUST_INFO_CUST_NAME").val();
		    var psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
		    if(custName == "" || psptId == ""){
		        return false;
		    }
		    var param = "&CUST_NAME="+encodeURIComponent(custName)+"&PSPT_ID="+psptId+"&EPARCHY_CODE=0898";
		    
		    var psptTypeObj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE");
		    if(psptTypeObj){
		    	var psptTypeCode = $.trim(psptTypeObj.val());
		    	var productId = $.trim($("#PRODUCT_ID").val());
				if(productId == "801110" && (psptTypeCode == "E" || psptTypeCode == "M" || psptTypeCode == "G" || psptTypeCode == "D"))
				{
					param = param + "&USER_TYPE=1";
				}
		    }
		    
		    //如果没有设置则取默认服务名
		    var strCheckUserPsptId = $("#CHECK_USER_PSPTID").val();
		    if(strCheckUserPsptId == "CREATEUSERSW")
		    {
		    	param += "&NET_TYPE_CODE=12";
		    	this.realNameSVCName = "SS.CreatePersonUserSVC.checkRealNameLimitByPsptCtt";
		    	//this.setRealNameSVCName("SS.CreatePersonUserSVC.checkRealNameLimitByPsptCtt");
		    }
		    
		    if(this.realNameSVCName)
		    {
		    	param += "&SVC_NAME="+this.realNameSVCName;
		    }
		    $.beginPageLoading("实名制用户开户数量校验。。。");
			$.httphandler.get(this.clazz, "checkRealNameLimitByPspt", param, 
				function(data){
					$.endPageLoading();
					if(data && data.get("CODE")!= "0"){
						$("#REALNAME_LIMIT_CHECK_RESULT").val("false");
						//alert(data.get("MSG"));
						$("#MEM_CUST_INFO_PSPT_ID").val("");
						var errMessage = data.get("MSG");
						MessageBox.error("告警提示",errMessage,null, null, null, null);
						return;
					}else{
						$("#REALNAME_LIMIT_CHECK_RESULT").val("true");
					}
					$("#MEM_CUST_INFO_SPAN_PSPT_ID").addClass("e_elements-success");
				},function(code, info, detail){
					$.endPageLoading();
					$("#MEM_CUST_INFO_SPAN_PSPT_ID").addClass("e_elements-error");
					MessageBox.error("错误提示","实名制用户开户数量校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "实名制用户开户数量校验超时");
			});	
		},
		//开户-身份证判定是否黑名单用户
		checkPsptidBlackListInfo:function(){
		    var psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
		    var blackTradeType=$("#BLACK_TRADE_TYPE").val();
		    if( psptId == ""){
		        return false;
		    }
		    var param =  "&PSPT_ID="+psptId+"&BLACK_TRADE_TYPE="+blackTradeType ;
		     
		    $.beginPageLoading("判定是否黑名单用户。。。");
			$.httphandler.get(this.clazz, "checkPsptidBlackListInfo", param, 
				function(data){
					$.endPageLoading();
					if( data.get("BLACKCODE")!= "0"){ 
						//alert(data.get("BLACKMSG")); 
						MessageBox.error("告警提示",data.get("BLACKMSG"),null, null, null, null);
						$("#PSPT_ID").val("");
						return false;
					} 
				},function(code, info, detail){
					$.endPageLoading(); 
					MessageBox.error("错误提示","黑名单用户校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "黑名单用户校验超时");
			});	
		},
		//检查同一证件号已开实名制用户的数量是否已超出预定值
		checkRealNameLimitByUsePspt:function(){
		    var custName = $("#USE").val();
		    var psptId = $("#MEM_CUST_INFO_USE_PSPT_ID").val();
		    if(custName == "" || psptId == ""){
		        return false;
		    }
		    var param = "&CUST_NAME="+encodeURIComponent(custName)+"&PSPT_ID="+psptId+"&EPARCHY_CODE=0898";
		    //如果没有设置则取默认服务名
		    /*if(this.realNameSVCName){
		    	param += "&SVC_NAME="+this.realNameSVCName;
		    }*/ 
		    param += "&SVC_NAME=SS.CreatePersonUserSVC.checkRealNameLimitByUsePspt";
		    $.beginPageLoading("使用人证件信息数量校验。。。");
			$.httphandler.get(this.clazz, "checkRealNameLimitByPspt", param, 
				function(data){
					$.endPageLoading();
					if(data && data.get("CODE")!= "0"){
						$("#REALNAME_LIMIT_CHECK_RESULT").val("false");
						alert(data.get("MSG"));
						return;
					}else{
						$("#REALNAME_LIMIT_CHECK_RESULT").val("true");
					}
					$("#span_MEM_CUST_INFO_USE_PSPT_ID").addClass("e_elements-success");
				},function(code, info, detail){
					$.endPageLoading();
					$("#span_MEM_CUST_INFO_USE_PSPT_ID").addClass("e_elements-error");
					MessageBox.error("错误提示","使用人证件信息数量校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "使用人证件信息数量校验超时");
			});	
		},
		/**
		 * REQ201801150022_新增IMS号码开户人像比对功能
		 * <br/>
		 * 人像比对
		 * @returns {Boolean}
		 * @author zhuoyingzhi
		 * @date 20180402
		 */
		identification:function (picid,picstream){
			var custName,psptId,psptType,fornt;
			if(picid == "MEM_CUST_INFO_PIC_ID"){
				custName = $("#MEM_CUST_INFO_CUST_NAME").val();
				psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
				psptType = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
				fornt = $("#MEM_CUST_INFO_FRONTBASE64").val();
			}else{
				custName = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
				psptId = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
				psptType = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
				fornt = $("#MEM_CUST_INFO_AGENT_FRONTBASE64").val();
			}
			if( psptId == ""){
				alert("请输入证件号码!");
				return false;
			}
			if(custName == ""){
				alert("请输入客户姓名!");
				return false;
			}
			
			//联系电话
			var sn = $("#MEM_CUST_INFO_CONTACT_PHONE").val();
			
			if(sn == ""){
				alert("请输入联系电话!");
				return false;
			}
			
			var bossOriginalXml = [];
			bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
			bossOriginalXml.push('<req>');
			bossOriginalXml.push('	<billid>'+'</billid>');
			bossOriginalXml.push('	<brand_name>'+'</brand_name>');
			bossOriginalXml.push('	<brand_code>'+'</brand_code>');
			bossOriginalXml.push('	<work_name>'+'</work_name>');
			bossOriginalXml.push('	<work_no>'+'</work_no>');
			bossOriginalXml.push('	<org_info>'+'</org_info>');
			bossOriginalXml.push('	<org_name>'+'</org_name>');
			bossOriginalXml.push('	<phone>'+sn+'</phone>');//联系电话
			bossOriginalXml.push('	<serv_id>'+'</serv_id>');
			bossOriginalXml.push('	<op_time>'+'</op_time>');
			
			bossOriginalXml.push('	<busi_list>');
			bossOriginalXml.push('		<busi_info>');
			bossOriginalXml.push('			<op_code>'+'</op_code>');
			bossOriginalXml.push('			<sys_accept>'+'</sys_accept>');
			bossOriginalXml.push('			<busi_detail>'+'</busi_detail>');
			bossOriginalXml.push('		</busi_info>');
			bossOriginalXml.push('	</busi_list>');

			bossOriginalXml.push('	<verify_mode>'+'</verify_mode>');
			bossOriginalXml.push('	<id_card>'+'</id_card>');
			bossOriginalXml.push('	<cust_name>'+'</cust_name>');
			bossOriginalXml.push('	<copy_flag></copy_flag>');
			bossOriginalXml.push('	<agm_flag></agm_flag>');
			bossOriginalXml.push('</req>');
			var bossOriginaStr = bossOriginalXml.join("");
			//调用拍照方法
			var resultInfo = makeActiveX.Identification(bossOriginaStr);
			//获取保存结果
			var result = makeActiveX.IdentificationInfo.result;			
			//获取保存照片ID
			var picID = makeActiveX.IdentificationInfo.pic_id;
			
			if(picID != ''){
				alert("人像摄像成功");
			}else{
				alert("人像摄像失败");
				return false;
			}	
			//获取照片流
			var picStream = makeActiveX.IdentificationInfo.pic_stream;
			picStream = escape (encodeURIComponent(picStream));
			//alert("picStream"+picStream+",picID:"+picID);
			if("0" == result){
				$("#"+picid).val(picID);
				//$("#"+picstream).val(picStream);
				var param = "&BLACK_TRADE_TYPE=3008";			
				param += "&CERT_ID="+psptId;
				param += "&CERT_NAME="+custName;
				param += "&CERT_TYPE="+psptType;
				param += "&PIC_STREAM="+picStream+"&FRONTBASE64="+escape (encodeURIComponent(fornt));			
				param += "&SERIAL_NUMBER="+sn;
				$.beginPageLoading("正在进行人像比对。。。");
				$.httphandler.post(this.clazz, "cmpPicInfo", param, 
						function(data){
							$.endPageLoading();
							if(data && data.get("X_RESULTCODE")== "0"){ 
								MessageBox.success("成功提示","人像比对成功", null, null, null);						
								return true;
							}else if(data && data.get("X_RESULTCODE")== "1"){
						
								 var detailReasonType=data.get("X_DETAIL_REASON_TYPE");
								 if(detailReasonType == "3"){
									 //无公安部头像
									 //showMessagePicInfoCustInfoFieid(picid, picstream,data);
								 }else{
									$("#"+picid).val("ERROR");
									$("#"+picstream).val(data.get("X_RESULTINFO"));
									MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);		
									return false;									 
								 }
							}
						},function(code, info, detail){
							$.endPageLoading();
							$("#"+picid).val("ERROR");
							$("#"+picstream).val("人像比对信息错误，请重新拍摄！");
							MessageBox.error("错误提示","人像比对信息错误，请重新拍摄！",null, null, null, null);
						},function(){
							$.endPageLoading();
							$("#"+picid).val("ERROR");
							$("#"+picstream).val("人像比对失败，请重新拍摄");
							MessageBox.alert("告警提示", "人像比对失败，请重新拍摄");
					});
			}else{
				MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
			}			
		}
	   /******************************************/
	}});
	$.extend($.MemCustInfo, BaseInfoLib);
})(Wade);