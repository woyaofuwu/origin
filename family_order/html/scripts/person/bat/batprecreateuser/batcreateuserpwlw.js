if(typeof(BatCreateUserPWLW)=="undefined"){
	window["BatCreateUserPWLW"]=function(){
		 
	};      
	var batcreateuserpwlw = new BatCreateUserPWLW();
}

(function(){
	$.extend(BatCreateUserPWLW.prototype,{
		
		/*****************************以下为用户密码校验部分********************************************/
		/*密码组件前校验*/
		PasswdbeforeAction:function() {
			 if($("#PSPT_TYPE_CODE").val() == ""){
		      alert("证件类型不能为空！");
		      return false;
		    }
		    if($("#PSPT_ID").val() == ""){
		      alert("证件号码不能为空！");
		      return false;
		    }
		    if($("#PHONE").val() == ""){
		      alert("联系电话不能为空！");
		      return false;
		    }
		    
		    //将值赋给组件处理
		    var psptId = $("#PSPT_ID").val();
		    var serialNumber = $("#PHONE").val();
		    $.password.setPasswordAttr(psptId, serialNumber);
		    return true ;
		},
		/*密码组件后赋值*/
		PasswdafterAction:function(data) {
			$("#USER_PASSWD").val(data.get("NEW_PASSWORD"));
		},				 
		
		clickScanPspt:function()
		{
			getMsgByEForm("PSPT_ID","CUST_NAME",null,null,null,"PSPT_ADDR",null,null);
			this.verifyIdCard("PSPT_ID");
			this.verifyIdCardName("PSPT_ID");
		},
		clickScanPspt2:function(){	
			getMsgByEForm("AGENT_PSPT_ID","AGENT_CUST_NAME",null,null,null,"AGENT_PSPT_ADDR",null,null);
			this.verifyIdCard("AGENT_PSPT_ID");
			this.verifyIdCardName("AGENT_PSPT_ID");
		},
		clickScanPspt3:function(){			
			getMsgByEForm("USE_PSPT_ID","USE",null,null,null,"USE_PSPT_ADDR",null,null);
			this.verifyIdCard("USE_PSPT_ID");
			this.verifyIdCardName("USE_PSPT_ID");
		},		
		clickScanPspt4:function(){			
			getMsgByEForm("RSRV_STR4","RSRV_STR2",null,null,null,"RSRV_STR5",null,null);
			this.verifyIdCard("RSRV_STR4");
			this.verifyIdCardName("RSRV_STR4");
		},	
				
		/*****************************以下为客户资料校验部分********************************************/
		//客户姓名校验
		chkCustName:function(fieldName){
			var obj=$("#"+fieldName); 
			/**
		     * REQ201609280015 chenxy3 20161104
		     * */
		    var custNameTrim=obj.val().replace(/\s+/g,"");
		    obj.val(custNameTrim);
			var psptTypeCode,psptTypeDesc;
			//判断到底是客户姓名还是经办人姓名
			if(fieldName=="CUST_NAME"){
				psptTypeCode = $("#PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#PSPT_TYPE_CODE").attr("desc");
			}else if(fieldName=="AGENT_CUST_NAME"){
				psptTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#AGENT_PSPT_TYPE_CODE").attr("desc");
			}else if(fieldName=="USE"){
				psptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#USE_PSPT_TYPE_CODE").attr("desc");
			}else{
				psptTypeCode = $("#RSRV_STR3").val();
			    psptTypeDesc = $("#RSRV_STR3").attr("desc");
			}
			var custName = $.trim(obj.val());
			var desc = obj.attr("desc");
						   
		    if(!custName) return;
		    
		    if(psptTypeCode == "E"||psptTypeCode == "G"||psptTypeCode == "M"){//	营业执照、组织机构代码证、事业单位法人登记证书
		    	if(this.includeChineseCount(custName)<4){
					alert(psptTypeDesc+"不是护照,"+desc+"不能少于4个中文字符！");
					obj.val("");
					obj.focus();
					return;
				}
		    }
		    
		    var mainPsptTypeCode = $("#PSPT_TYPE_CODE").val();
		    if(mainPsptTypeCode == "E"||mainPsptTypeCode == "G"||mainPsptTypeCode == "D" ||mainPsptTypeCode == "M" ||mainPsptTypeCode == "L"){
		    	//	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
		    	var custnametemp = trim($("#CUST_NAME").val());
		    	var agentnametemp = trim($("#AGENT_CUST_NAME").val());		    	
		    	if(custnametemp!=""&&agentnametemp!=""&&custnametemp==agentnametemp){
					alert("单位名称和经办人名称不能相同！");
					obj.val("");
					obj.focus();
					return;
		    	}
		    	
		     
		    	var usenametemp = trim($("#USE").val());
		    	if(custnametemp!=""&&usenametemp!=""&&custnametemp==usenametemp){
					alert("单位名称和使用人名称不能相同！");
					obj.val("");
					obj.focus();
					return;
		    	}
		    	 
		    }

		    if(psptTypeCode != "A" && psptTypeCode != "D"){
		    	if(psptTypeCode != "E" && psptTypeCode != "G" && psptTypeCode != "D"  && psptTypeCode != "M"  && psptTypeCode != "L")
		        {//E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
		    	
		    		var pattern =/[a-zA-Z0-9]/;
		    		if(pattern.test(custName)){
		    			alert(psptTypeDesc+"不是护照, "+desc+"不能包含数字和字母！");
		    			obj.val("");
		    			obj.focus();
		    			return;
		    		}
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
			if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="C"|| psptTypeCode=="H"|| psptTypeCode=="I"|| psptTypeCode=="N"|| psptTypeCode=="O"){
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
			this.verifyIdCardName(fieldName);
			
		    //如果不需要进行实名制，或者是非客户姓名，则不需要进行实名制校验限制
		    if(!this.isRealName || fieldName!="CUST_NAME") {
				return;
			}
			
			if(this.checkPsptIdForReal()) {
				//this.checkRealNameLimitByPspt();
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
			var obj = $("#CUST_NAME");
			var custName = $.trim(obj.val()); 
			/**
		     * REQ201609280015 chenxy3 20161104
		     * */ 
		    var custNameTrim=obj.val().replace(/\s+/g,"");
		    obj.val(custNameTrim);
		    
			if(custName == ""){
				return;
		    }
			var specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
			for(i=0;i<specialStr.length;i++){
				if (custName.indexOf(specialStr.charAt(i)) > -1){
					alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				}
			}
			//判断如果存在账户名字，则设置账户名
			if($("#PAY_NAME") && $("#PAY_NAME").length){
				$("#PAY_NAME").val(custName);					
			}
		},
		afterChkAgentCustName:function(){
			var obj = $("#AGENT_CUST_NAME");
			var custName = $.trim(obj.val());
			//$("#RSRV_STR2").val(custName);
			if(custName == ""){
				return;
		    }
			var specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
			for(i=0;i<specialStr.length;i++){
				if (custName.indexOf(specialStr.charAt(i)) > -1){
					alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				}
			}
			//判断如果存在账户名字，则设置账户名
			//if($("#RSRV_STR2") && $("#RSRV_STR2").length){
			//	$("#RSRV_STR2").val(custName);					
			//}
		},
		afterChkAgentPsptTypeCode:function(fieldName){
			
			var agentval = $("#"+fieldName).val();
			//alert(agentval);
			//var rsrvstr = $("#RSRV_STR3").val();
			//if(rsrvstr == ""){
				//$("#RSRV_STR3").val(agentval);					
			//}
		},
		afterChkAgentPsptId:function(fieldName){
			
			var agentval = $("#"+fieldName).val();
			//alert(agentval);
			//var rsrvstr = $("#RSRV_STR4").val();
			//if(rsrvstr == ""){
				//$("#RSRV_STR4").val(agentval);					
			//}
		},
		afterChkAgentPsptAddr:function(fieldName){
			
			var agentval = $("#"+fieldName).val();
			//alert(agentval);
			//var rsrvstr = $("#RSRV_STR5").val();
			//if(rsrvstr == ""){
				//$("#RSRV_STR5").val(agentval);					
			//}
		},
		chkPsptAddr:function(fieldName){
			var obj=$("#"+fieldName);
			var psptAddr = this.trimAll(obj.val());
			var desc = obj.attr("desc");
			
			var zjNum = psptAddr.replace(/[^\x00-\xff]/g, "**").length ; //字节数
			if(psptAddr=="" || zjNum*1<12){
				alert(desc+"栏录入文字需不少于6个汉字或12个字节！");
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
		trimAll:function(str)
		{
		  return str.replace(/(^\s+)|(\s+$)/g,"").replace(/\s/g,""); 
		},			
		//联系电话校验
		checkPhone: function (objId){
			var phoneObj = $("#"+objId);
			var desc = phoneObj.attr("desc");
			var phone = phoneObj.val();
			
			if(phone==""){
				return false;
			}else{
				$.beginPageLoading("数据校验中...");
				var param = "&PHONE="+phone;
				$.ajax.submit('','checkPhone',param,'',function(data){
					$.endPageLoading();
					if( data.get('X_RESULT_CODE') == '-1' ){
						var info = data.get("X_RESULTINFO");
						alert(info);
						phoneObj.val("");
						phoneObj.focus();
						return false;
					}
				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
					phoneObj.val("");
					phoneObj.focus();
					return false;
				});
			}
			
			/*if(!$.toollib.isNumber(phone) || phone.length < 6){
				alert(desc+"必须大于等于6阿拉伯数字！");
				phoneObj.val("");
				phoneObj.focus();
				return false;
			}
			
			if($.toollib.isRepeatCode(phone)){
				alert(desc+"不能全为同一个数字，请重新输入！");
				phoneObj.val("");
				phoneObj.focus();
				return false;
			}
			if($.toollib.isSerialCode(phone)){
				alert("连续数字不能作为"+desc+"，请重新输入！");
				phoneObj.val("");
				phoneObj.focus();
				return false;
			}*/
		},
		chkPsptTypeCode:function(fieldName)
		{
			var obj=$("#"+fieldName);
			var custNameObj,psptObj;
			
			if(fieldName=="PSPT_TYPE_CODE"){
				custNameObj = $("#CUST_NAME");
				psptObj = $("#PSPT_ID");
				this.chkCustName("CUST_NAME");
			}else if(fieldName=="AGENT_PSPT_TYPE_CODE"){
				custNameObj = $("#AGENT_CUST_NAME");
				psptObj = $("#AGENT_PSPT_ID");
				this.chkCustName("AGENT_CUST_NAME");
			}else if(fieldName=="USE_PSPT_TYPE_CODE"){
				custNameObj = $("#USE");
				psptObj = $("#USE_PSPT_ID");
				this.chkCustName("USE");
			}else{
				custNameObj = $("#RSRV_STR2");
				psptObj = $("#RSRV_STR4");
				this.chkCustName("RSRV_STR2");
			}
			var psptTypeCode = obj.val();
			var custName = $.trim(custNameObj.val());

			/**
			 * REQ201804020010_物联网卡批量开户责任人、经办人等证件类型新增军人身份证
			 * 
			 * @author zhuoyingzhi
			 * @date 20180423
			 */
			if(fieldName == "AGENT_PSPT_TYPE_CODE"
				||fieldName == "RSRV_STR3"
				||fieldName == "USE_PSPT_TYPE_CODE"){
				//经办人、责任人和使用人证件类型
				if(psptTypeCode == "3"){
					//军人身份证
					//alert("请提醒客户同时出示军人身份证明,并进行留存.");
					MessageBox.alert("提示","请提醒客户同时出示军人身份证明,并进行留存.",null,null,null);
				}
			}
			
			if(fieldName=="PSPT_TYPE_CODE")
			{
				if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="A")
				{
					$("#USER_TYPE_CODE").val('0');
				}
				else
				{
					$("#USER_TYPE_CODE").val('8');	 
				}				  
				
				if(psptTypeCode=="3")
				{
					alert("请提醒客户同时出示军人身份证明！并进行留存");
				}
				    
				if(psptTypeCode=="E")
				{//法人、成立时间、营业开始时间、营业结束时间都要填		
					$("#legalperson").attr("nullable","no");
					$("#startdate").attr("nullable","no");
					$("#termstartdate").attr("nullable","no");
					$("#termenddate").attr("nullable","no");
					$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").addClass("e_required");
					$("#enterprisePart").css("display","");
				}
				else
				{
					$("#legalperson").attr("nullable","yes");
					$("#startdate").attr("nullable","yes");
					$("#termstartdate").attr("nullable","yes");
					$("#termenddate").attr("nullable","yes");
					$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").removeClass("e_required");
					$("#enterprisePart").css("display","none");
					$("#legalperson").val('');
					$("#termstartdate").val('');
					$("#termenddate").val('');
					$("#startdate").val('');
					
				} 
				
				if(psptTypeCode=="M")
				{// 机构类型、有效日期、失效日期
					    
					$("#orgtype").attr("nullable","no");
					$("#effectiveDate").attr("nullable","no");
					$("#expirationDate").attr("nullable","no");
					$("#span_orgtype,#span_effectiveDate,#span_expirationDate").addClass("e_required");
					$("#orgPart").css("display","");
				}
				else
				{
					$("#orgtype").attr("nullable","yes");
					$("#effectiveDate").attr("nullable","yes");
					$("#expirationDate").attr("nullable","yes");
					$("#span_orgtype,#span_effectiveDate,#span_expirationDate").removeClass("e_required");
					$("#orgPart").css("display","none");
					$("#orgtype").val('');
					$("#effectiveDate").val('');
					$("#expirationDate").val('');
				}
			}						
			
			//0-本地身份证 1-外地身份证
			psptObj.attr("datatype", (psptTypeCode==0 || psptTypeCode==1 || psptTypeCode==2 || psptTypeCode==3)? "pspt" : "");
			
			if(custName!="" && psptTypeCode != "A"){
			    if(psptTypeCode == "E"||psptTypeCode == "G"||psptTypeCode == "M"){//	营业执照、组织机构代码证、事业单位法人登记证书
			    	if(this.includeChineseCount(custName)<4){
						alert(obj.attr("desc")+"不是护照，"+custNameObj.attr("desc")+"不能少于4个中文字符！");
						obj.val("");
						obj.focus();
						return;
					}
			    }else{
			    	if(this.includeChineseCount(custName)<2){
			    		alert(obj.attr("desc")+"不是护照，"+custNameObj.attr("desc")+"不能少于2个中文字符！");
			    		custNameObj.val("");
						return ;
			    	}
			    }

			}
			
			if(fieldName == "AGENT_PSPT_TYPE_CODE"){
				$("#AGENT_PSPT_ID").trigger("change");
				return;
			}
			
			if(fieldName == "USE_PSPT_TYPE_CODE"){
				$("#USE_PSPT_ID").trigger("change");
				return;
			}
			
			if(fieldName == "RSRV_STR3"){
				$("#RSRV_STR4").trigger("change");
				return;
			}
			
			
			
			//实名制开户限制
			if(psptTypeCode == "Z" && this.isRealName){
				alert("实名制开户，证件类型不能为其他，请重新选择！");
				$(this).find("option[index=0]").attr("selected", true);
				psptObj.attr("datatype","");
				return;
			}
			
			$("#PSPT_ID").trigger("change");
			this.verifyIdCardName(fieldName);
			this.renderSpecialField(psptTypeCode);

			 
		},
		/**
		 * 选择“集团客户”证件类型（营业执照、事业单位法人证书、单位证明、组织机构代码证、社会团体法人登记证书）开户时， 
		 * 需支持录入经办人名称、经办人证件类型、经办人证件号码、经办人证件地址，这些信息为必填项目。经办人的证件校验规则同个人大众客户的规则一致。
		 * E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
		 */
		renderSpecialField:function(psptTypeCode){
		    var psptTypeCodecust = $("#PSPT_TYPE_CODE").val() ;
			if(psptTypeCodecust=="D" || psptTypeCodecust=="E" || psptTypeCodecust=="G" || psptTypeCodecust=="L" || psptTypeCodecust=="M"){	
			       this.setRequiredField("BIRTHDAY", false);
			}else{
				this.setRequiredField("BIRTHDAY", true);
			}
			
			var fields=['AGENT_CUST_NAME','AGENT_PSPT_TYPE_CODE','AGENT_PSPT_ID','AGENT_PSPT_ADDR'];
			var Rsrvfields=["RSRV_STR2","RSRV_STR3","RSRV_STR4","RSRV_STR5"];			
			//var psptTypeCode = $("#PSPT_TYPE_CODE").val();//这里确实是用客户证件类型来做判断
			/*选择“集团客户”证件类型（营业执照、事业单位法人证书、单位证明、组织机构代码证、社会团体法人登记证书）开户时，
			 * 需支持录入经办人名称、经办人证件类型、经办人证件号码、经办人证件地址，这些信息为必填项目。经办人的证件校验规则同个人大众客户的规则一致。
			 * E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书*/
			if(psptTypeCode=="E" || psptTypeCode=="G" || psptTypeCode=="D" || psptTypeCode=="M" || psptTypeCode=="L"){
				for(var i=0; i<fields.length; i++){
					this.setRequiredField($.trim(fields[i]), true);
				}
				
				for(var i=0; i<Rsrvfields.length; i++){
					this.setRequiredField($.trim(Rsrvfields[i]), true);
				}				
												
			}else{
				for(var i=0; i<fields.length; i++){
					this.setRequiredField($.trim(fields[i]), false);
				}
				for(var i=0; i<Rsrvfields.length; i++){
					this.setRequiredField($.trim(Rsrvfields[i]), false);
				}
		
			}
	    if(psptTypeCode=="E"){//法人、成立时间、营业开始时间、营业结束时间都要填		 
			$("#legalperson").attr("nullable","no");
			$("#startdate").attr("nullable","no");
			$("#termstartdate").attr("nullable","no");
			$("#termenddate").attr("nullable","no");
			$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").addClass("e_required");		
			$("#enterprisePart").css("display","");		
		}else{
			$("#legalperson").attr("nullable","yes");
			$("#startdate").attr("nullable","yes");
			$("#termstartdate").attr("nullable","yes");
			$("#termenddate").attr("nullable","yes");
			$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").removeClass("e_required");			
			$("#enterprisePart").css("display","none");	
			$("#legalperson").val('');
			$("#termstartdate").val('');
			$("#termenddate").val('');
			$("#startdate").val('');

		}
	    
	   if(psptTypeCode=="M"){// 机构类型、有效日期、失效日期
		    $("#orgtype").attr("nullable","no");
		    $("#effectiveDate").attr("nullable","no");
		    $("#expirationDate").attr("nullable","no");
		    $("#span_orgtype,#span_effectiveDate,#span_expirationDate").addClass("e_required");		
		    $("#orgPart").css("display","");
	   }else{ 
		   $("#orgtype").attr("nullable","yes");
		   $("#effectiveDate").attr("nullable","yes");
		   $("#expirationDate").attr("nullable","yes");
		   $("#span_orgtype,#span_effectiveDate,#span_expirationDate").removeClass("e_required");				
		   $("#orgPart").css("display","none");
		   $("#orgtype").val('');
		   $("#effectiveDate").val('');
		   $("#expirationDate").val('');

	   } 
	
		},
		renderSpecialFieldByPsptId:function(psptId){//检查年龄是否在12-120之间，显示隐藏经办人栏	
			if(trim(psptId)!=""&&!this.checkAge(psptId)){		   	
				var fields=["AGENT_CUST_NAME","AGENT_PSPT_TYPE_CODE","AGENT_PSPT_ID","AGENT_PSPT_ADDR"];
				for(var i=0; i<fields.length; i++){
					this.setRequiredField(fields[i],true);
				}
				//$("#AgentFieldPart li").removeClass("e_hideX");									   
			}else{
				var fields=["AGENT_CUST_NAME","AGENT_PSPT_TYPE_CODE","AGENT_PSPT_ID","AGENT_PSPT_ADDR"];					 					
				for(var i=0; i<fields.length; i++){
					this.setRequiredField(fields[i], false);
				}
				//$("#AgentFieldPart li").addClass("e_hideX");
			}
		},
		
		//设置必填字段
		setRequiredField:function(fieldId, tag){
			var nullable=tag?"no":"yes";
			$("#"+fieldId).attr("nullable", nullable);
			if(tag){
				$("#span_"+fieldId).addClass("e_required");
			}else{
				$("#span_"+fieldId).removeClass("e_required");
			}
			
		},
		chkPsptId:function(fieldName){
			var obj=$("#"+fieldName);
			var psptTypeObj;
			if(fieldName == "PSPT_ID"){
				psptTypeObj = $("#PSPT_TYPE_CODE");
			}else if(fieldName == "AGENT_PSPT_ID"){
				psptTypeObj = $("#AGENT_PSPT_TYPE_CODE");
			}else if(fieldName == "USE_PSPT_ID"){
				psptTypeObj = $("#USE_PSPT_TYPE_CODE");
			}else{
				psptTypeObj = $("#RSRV_STR3");
			}
			
			var psptId = $.trim(obj.val());
			var desc = $.trim(obj.attr("desc"));
			var psptTypeCode = psptTypeObj.val();
			//经办人、责任人证件号码不能相同
			var agentid = $.trim($("#AGENT_PSPT_ID").val());
			var rsrv4id = $.trim($("#RSRV_STR4").val());
			if(agentid.length>0&&rsrv4id.length>0&&agentid==rsrv4id){
				 alert("经办人、责任人证件号码不能相同！");
				 $("#AGENT_PSPT_ID").val("");
				 $("#RSRV_STR4").val("");
				 return false;
			}
			
			if(psptId=="") return;
			//提示开发人员在使用该组件校验时候，必须设置手机服务号码
			/*if(!this.serialNumber){
				alert("请先填写服务号码!");
				return;
			}*/
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
			}else if (psptTypeCode == "O") {
                var psptIdtemp = psptId.replace(/\s/g, '');
                if (psptIdtemp != psptId) {
                	alert("港澳居民来往内地通行证校验：证件号码中间不能有空格。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
                if (psptId.length != 9 && psptId.length != 11) {
                	alert("港澳居民来往内地通行证校验：" + desc + "必须为9位或11位。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (!(psptId.charAt(0) == "H" || psptId.charAt(0) == "M") || !$.toollib.isNumber(psptId.substr(1))) {
                	alert("港澳居民来往内地通行证校验：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "N") {
                //台湾居民来往大陆通行证:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
                var psptIdtemp = psptId.replace(/\s/g, '');
                if (psptIdtemp != psptId) {
                	alert("台湾居民来往大陆通行证校验：证件号码中间不能有空格。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (psptId.substr(0, 2) != "TW" && psptId.substr(0, 4) != "LXZH") {
                    if (psptId.length == 11 || psptId.length == 12) {
                        if (!$.toollib.isNumber(psptId.substring(0, 10))) {
                        	alert("台湾居民来往大陆通行证校验：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
                            obj.val("");
                            obj.focus();
                            return;
                        }
                    } else if (psptId.length == 8) {
                        if (!$.toollib.isNumber(psptId)) {
                        	alert("台湾居民来往大陆通行证校验：" + desc + "为8位时，必须均为阿拉伯数字。");
                            obj.val("");
                            obj.focus();
                            return;
                        }
                    } else if (psptId.length == 7) {
                        if (!$.toollib.isNumber(psptId)) {
                        	alert("台湾居民来往大陆通行证校验：" + desc + "为7位时，必须均为阿拉伯数字。");
                            obj.val("");
                            obj.focus();
                            return;
                        }
                    } else {
                    	alert("台湾居民来往大陆通行证校验：" + desc + "格式错误");
                        obj.val("");
                        obj.focus();
                        return;
                    }
                } else {
                    var psptIdsub;
                    if (psptId.substr(0, 2) == "TW") {
                        psptIdsub = psptId.substr(2);
                    } else if (psptId.substr(0, 4) == "LXZH") {
                        psptIdsub = psptId.substr(4);
                    }
                    var re = /^[()A-Z0-9]+$/;
                    var re1 = /^[•··.．·\d\u4e00-\u9fa5]+$/;
                    var pattern1 = /[A-Z]/;
                    var pattern2 = /[0-9]/;
                    var pattern3 = /[(]/;
                    var pattern4 = /[)]/;
                    if (re1.test(psptIdsub) || !re.test(psptIdsub) || !pattern1.test(psptIdsub) || !pattern2.test(psptIdsub) || !pattern3.test(psptIdsub) || !pattern4.test(psptIdsub)) {
                    	alert("台湾居民来往大陆通行证校验：" + desc + "前2位“TW”或 “LXZH”字符时，后面是阿拉伯数字、英文大写字母与半角“()”的组合");
                        obj.val("");
                        obj.focus();
                        return;
                    }
                }
            }
			
			else if(psptTypeCode=="I"){
				//台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
				if(psptId.length!=8 && psptId.length!=11){
					alert("台湾居民回乡校验："+desc+"必须为8位或11位。");
					obj.val("");
					obj.focus();
					return;
				}
				if(psptId.length==8){
					if(!$.toollib.isNumber(psptId)){
						alert("台湾居民回乡校验："+desc+"为8位时，必须均为阿拉伯数字。");
						obj.val("");
						obj.focus();
						return;
					}
				}
				if(psptId.length==11){
					if(!$.toollib.isNumber(psptId.substring(0,10))){
						alert("台湾居民回乡校验：："+desc+"为11位时，前10位必须均为阿拉伯数字。");
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
				if(psptId.length == 10 &&psptId.charAt(8) != "-"){
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
				 if(fieldName == "PSPT_ID" || fieldName == "AGENT_PSPT_ID" || fieldName == "RSRV_STR4" ||fieldName == "USE_PSPT_ID"){
					 
					 if(trim(psptId)!=""&&!this.checkAge11(psptId)){
					     	alert('年龄范围必须在16-120岁之间'); 
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
				if(fieldName == "PSPT_ID"){
					var psptvalue = this.getBirthdayByPsptId(psptId);
					//alert(psptvalue);
					$("#BIRTHDAY").val(psptvalue);
				}
			}
			this.verifyIdCardName(fieldName);
			
			if(fieldName != "PSPT_ID") {
				return;
			}
			
			if(!this.checkPsptIdForReal()) {
				obj.val("");
				obj.focus();
				return;
			}
			//this.checkRealNameLimitByPspt();

		},
		afterChkPsptId:function(){
			var obj =$(this)
			if(obj.val()=="") return;
		},
		/*****************************以下为客户资料校验辅助校验部分********************************************/
		//实名制基本数据校验
		checkPsptIdForReal:function(){
		    if($("#PSPT_TYPE_CODE").val()=="Z") {
		        alert("实名制开户，证件类型不能为其他，请重新选择！");
		        return false;
		    }
		    var psptId = $("#PSPT_ID").val();
		    if($.toollib.isRepeatCode(psptId))
		    {
		        alert("实名制开户，证件号码过于简单，请重新输入！");
		        return false;
		    }
		    /*if($("#CUST_NAME").val().indexOf("海南通")>-1)
		    {
		        alert("实名制开户，客户名称不能为【海南通】，请重新输入！");
		        $("#CUST_NAME").val("");
		        return false;
		    }*/
		    if(!$.validate.verifyField("PSPT_ID")){
		        return false;
		    }
		    return true;
		},
		// 根据身份证号码获取生日日期
		getBirthdayByPsptId:function(psptId) {
			var tmpStr="";
			if((psptId.length!=15) &&(psptId.length!=18)) {
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
		/**
		 * REQ201707210018_关于年龄外经办人限制变更的优化
		 * <br/>
		 * 经办人、使用人、责任人年龄范围在11岁（含）至120岁（不含）之间
		 * @author zhuoyingzhi
		 * @date 20170925
		 */
		checkAge11:function(idCard){
			if(!idCard){return false;}
			var _age = this.jsGetAge(idCard);			
			return _age>=16 && _age<120;
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
			return _age>=16 && _age<=120;
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
		//在线实名制校验
		verifyIdCard:function(fieldName){      
			var obj=$("#"+fieldName);
			var custNameObj,psptObj;
			if(fieldName == "PSPT_ID"||fieldName=="CUST_NAME"){
				custNameObj = $("#CUST_NAME");
				psptObj = $("#PSPT_ID");
			}else if(fieldName == "AGENT_PSPT_ID"||fieldName=="AGENT_CUST_NAME"){
				custNameObj = $("#AGENT_CUST_NAME");
				psptObj = $("#AGENT_PSPT_ID");
			}else if(fieldName == "USE_PSPT_ID"||fieldName=="USE"){
				custNameObj = $("#USE");
				psptObj = $("#USE_PSPT_ID");
			}else{
				custNameObj = $("#RSRV_STR2");
				psptObj = $("#RSRV_STR4");
			}
		    var psptId = $.trim(psptObj.val());
		    var psptName = $.trim(custNameObj.val());
		    if( psptId == ""||psptName==""){
		        return false;
		    }
            /**	
             * REQ201706130001_关于录入联网核验情况的需求	 	 
             * @author zhuoyingzhi	 	 
             * @date 20170921	 	 
             */	 	 
		    var serialNunber = $("#PHONE").val();
		    
		    var param =  "&CERT_ID="+psptId +"&CERT_NAME="+encodeURIComponent(psptName)
		    			 +"&TRADE_TYPE_CODE=10"+"&SERIAL_NUMBER="+serialNunber+"&BUISUSERTYPE=PWLW";
		    $.beginPageLoading("在线实名制校验。。。");
			$.ajax.submit(null, 'verifyIdCard', param, '', function(data){
				$.endPageLoading();								
				if(data && data.get("X_RESULTCODE")== "1"){			
					//MessageBox.error("告警提示","该证件在公安部系统校验不通过，建议用户到当地派出所核对自己的证件信息。若是军人请用军官证或军人身份证开户。",null, null, null, null);
                    MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
					psptObj.val("");
					return false;
				}else if(data && data.get("X_RESULTCODE")== "2"){ 
					MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);						
					return true;
				}else{
					return true;
				}								
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
   			});
   			
		},
		//	营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
		verifyIdCardName:function(fieldName){
			var obj=$("#"+fieldName);
			var custNameObj,psptObj,psptcodeObj;
			if(fieldName == "PSPT_ID"||fieldName=="CUST_NAME"){
				custNameObj = $("#CUST_NAME");
				psptObj = $("#PSPT_ID");
				psptcodeObj = $("#PSPT_TYPE_CODE");
				
			}else if(fieldName == "AGENT_PSPT_ID"||fieldName=="AGENT_CUST_NAME"){
				custNameObj = $("#AGENT_CUST_NAME");
				psptObj = $("#AGENT_PSPT_ID");
				psptcodeObj = $("#AGENT_PSPT_TYPE_CODE");
			}else if(fieldName == "USE_PSPT_ID"||fieldName=="USE"){
				
				custNameObj = $("#USE");
				psptObj = $("#USE_PSPT_ID");
				psptcodeObj = $("#USE_PSPT_TYPE_CODE");
			}else{
				
				custNameObj = $("#RSRV_STR2");
				psptObj = $("#RSRV_STR4");
				psptcodeObj = $("#RSRV_STR3");
			}
		    var psptId = $.trim(psptObj.val());
		    var psptName = $.trim(custNameObj.val());
		    var psptcode = $.trim(psptcodeObj.val());
		    if( psptId == ""||psptName==""||psptcode==""){
		        return false;
		    }
		  //REQ201701160002关于集团客户界面优化需求
		    var param =  "&CERT_TYPE="+psptcode+"&CERT_ID="+psptId +"&CERT_NAME="+encodeURIComponent(psptName)+"&BUISUSERTYPE=PWLW";
		    if(psptcode=="0"||psptcode=="1"||psptcode=="3"||psptcode=="A"){//《一证多名需求》，除了本地外地身份证和户口，其他证件都不允许一证多名
				  if(fieldName == "PSPT_ID"||fieldName=="CUST_NAME"||fieldName=="PSPT_TYPE_CODE"
						||fieldName == "USE_PSPT_ID"||fieldName=="USE"||fieldName=="USE_PSPT_TYPE_CODE" 
					  ){
				    	$.beginPageLoading("个人证件校验。。。");
				    	$.ajax.submit(null, 'verifyIdCardName', param, '', function(data){
				    		$.endPageLoading();								
				    		if(data && data.get("X_RESULTCODE")!= "0"){ 
				    			MessageBox.error("告警提示","该证件开户的号码已经达到5个，不能再开户",null, null, null, null);
				    			psptObj.val("");
				    			return false;
				    		}
				    	},
				    	function(error_code,error_info,derror){
				    		$.endPageLoading();
				    		showDetailErrorInfo(error_code,error_info,derror);
				    	});					  					  
				  }else{		    	
					  return true;
				  }
		    }else{
		    	$.beginPageLoading("单位名称校验。。。");
		    	$.ajax.submit(null, 'verifyIdCardName', param, '', function(data){
		    		$.endPageLoading();								
		    		if(data && data.get("X_RESULTCODE")!= "0"){ 
		    			MessageBox.error("告警提示","同一个证件号码不能对应不同的名称",null, null, null, null);
		    			psptObj.val("");
		    			return false;
		    		}else{ 
		    			verifyEnterpriseCard();
		    			//REQ201908150006-申请优化单位证件开户及校验的需求-guonj-去掉验证
			            //verifyOrgCard();
		    		}
		    	},
		    	function(error_code,error_info,derror){
		    		$.endPageLoading();
		    		showDetailErrorInfo(error_code,error_info,derror);
		    	});
		    }
		  //REQ201701160002关于集团客户界面优化需求
		},	
		//	营业执照在线校验
		 verifyEnterpriseCard:function(){			 
		    var psptcode = $.trim($("#PSPT_TYPE_CODE").val());
		    var psptId = $.trim($("#PSPT_ID").val());
		    var psptName = $.trim($("#CUST_NAME").val());
		    var legalperson = $.trim($("#legalperson").val());
		    var termstartdate = $.trim($("#termstartdate").val());
		    var termenddate = $.trim($("#termenddate").val());
		    var startdate = $.trim($("#startdate").val());
		    		   
		    //营业执照 
		    if(psptcode!="E" ){
		    	return false;
		    }
		    if(psptId == ""||psptName==""||psptcode==""||legalperson==""||termstartdate==""||termenddate==""||startdate==""){
		       return false;
		    }
            /**
             * REQ201706130001_关于录入联网核验情况的需求	 	 
             * @author zhuoyingzhi	 	 
             * @date 20170921	 	 
             */	 	 
            var serialNunber = $("#PHONE").val();
            
            
		    var param =  "&regitNo="+psptId+"&enterpriseName="+encodeURIComponent(psptName)+"&legalperson="+encodeURIComponent(legalperson)
		                 +"&termstartdate="+encodeURIComponent(termstartdate)+"&termenddate="+encodeURIComponent(termenddate)+"&startdate="+encodeURIComponent(startdate)
		                 +"&SERIAL_NUMBER="+serialNunber+"&TRADE_TYPE_CODE=10";

		    $.beginPageLoading("营业执照校验。。。");	
			$.ajax.submit(null, 'verifyEnterpriseCard', param, '', function(data){
				$.endPageLoading();
				if(data && data.get("X_RESULTCODE")!= "0"){
					MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
					$("#PSPT_ID").val('');
					return false;
				}else{
					return true;
				}
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });	
		},

		//	组织机构代码在线校验
		 verifyOrgCard:function(){		 
			//REQ201908150006-申请优化单位证件开户及校验的需求-guonj-去掉验证
		    /*var psptcode = $.trim($("#PSPT_TYPE_CODE").val());
		    var psptId = $.trim($("#PSPT_ID").val());
		    var psptName = $.trim($("#CUST_NAME").val());
		    var orgtype = $.trim($("#orgtype").val());
		    var effectiveDate = $.trim($("#effectiveDate").val());
		    var expirationDate = $.trim($("#expirationDate").val());		    

		    // 组织机构代码证
		    if(psptcode!="M" ){
		    	return false;
		    }
		    
		    if(psptId == ""||psptName==""||psptcode==""||orgtype==""||effectiveDate==""||expirationDate==""){
		       return false;
		    }

		    var param =  "&orgCode="+psptId+"&orgName="+encodeURIComponent(psptName)+"&orgtype="+encodeURIComponent(orgtype)
		                 +"&effectiveDate="+encodeURIComponent(effectiveDate)+"&expirationDate="+encodeURIComponent(expirationDate); 
		    $.beginPageLoading("组织机构代码证校验。。。");
			$.ajax.submit(null, 'verifyOrgCard', param, '', function(data){
				$.endPageLoading();
				if(data && data.get("X_RESULTCODE")!= "0"){ 
					MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
					$("#PSPT_ID").val('');
					return false;
				}else{
					return true;
				}
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });*/
		    
		 },    		
		
		/*****************************以下为账务资料校验部分********************************************/
		checkPayModeCode:function(){
			//如果帐户类型为现金 或者没有选择
			this.controlForPayMode();
			
			//清空数据
			$("#SUPER_BANK_CODE").find("option[index=0]").attr("selected", true);
			$("#BANK_CODE,#BANK,#BANK_ACCT_NO").val("");
		},
		checkSuperBankCode:function(){
			//var superBankCode = $(this).val();
			/**
			 * 下级银行选择由下拉框修改为弹出框，注释本行代码
			 */
			//$.acctInfo.loadBankList(superBankCode);
			$("#BANK_CODE,#BANK,#BANK_ACCT_NO").val("");
		},
		qryBanks:function(){
				var superBankCode = $("#SUPER_BANK_CODE").val();
				if(superBankCode==""){
					alert("请先选择上级银行！");
					return;
				}
				$.popupPage("components.BankList", "onInitTrade",
						"&SUPER_BANK_CODE=" +superBankCode, "银行名称列表", "500", "350");		
		},
		/*****************************以下为账务资料校验辅助校验部分********************************************/
		controlForPayMode:function(){
			var payModeCode = $("#PAY_MODE_CODE").val();
			//如果帐户类型为现金 或者没有选择
			if(payModeCode=="0" || payModeCode=="") {
				//设置禁用样式
				$("#SuperBankCodeLi,#BankCodeLi,#BankAcctNoLi").removeClass().addClass("li e_dis");
				$("#SUPER_BANK_CODE,#BANK_CODE,#BANK,#BANK_ACCT_NO").attr("disabled", true).attr("nullable", "yes");
				$("#span_SUPER_BANK_CODE,#span_BANK_CODE,#span_BANK_ACCT_NO").removeClass("e_required");
				//var flag=false;
				//if(!this.changeAcctDay){
					//$("#ACCT_DAY").val("1");
					//flag=true;
				//}
				
				//alert($("#ACCT_DAY_YW").val());
				$("#ACCT_DAY_YW").attr("disabled", false);
			}
			else {
				
				$("#SuperBankCodeLi,#BankCodeLi,#BankAcctNoLi").removeClass().addClass("li");
				$("#SUPER_BANK_CODE,#BANK_CODE,#BANK,#BANK_ACCT_NO").attr("disabled", false).attr("nullable", "no");
				$("#span_SUPER_BANK_CODE,#span_BANK_CODE,#span_BANK_ACCT_NO").addClass("e_required");
				
				$("#ACCT_DAY_YW").val("1");
				//$("#ACCT_DAY_YW").find("option[index=1]").attr("selected", true);
				$("#ACCT_DAY_YW").attr("disabled", true);
				
			}
		},
		/**
		 * REQ201707170020_新增物联卡开户人像采集功能
		 * @author zhuoyingzhi
		 * @date 20170814
		 * 物联网开户,人像比对按钮
		 */
		identification: function(picid,picstream){
			var custName,psptId,psptType,fornt;
			if(picid == "custInfo_PIC_ID"){
				//客户
				custName = $("#CUST_NAME").val();
				psptId = $("#PSPT_ID").val();
				psptType = $("#PSPT_TYPE_CODE").val();
				fornt = $("#custInfo_FRONTBASE64").val();
			}else{
				//经办人
				custName = $("#AGENT_CUST_NAME").val();
				psptId = $("#AGENT_PSPT_ID").val();
				psptType = $("#AGENT_PSPT_TYPE_CODE").val();
				fornt = $("#custInfo_AGENT_FRONTBASE64").val();
			}
			if( psptId == ""){
				alert("请输入证件号码!");
				return false;
			}
			if(custName == ""){
				alert("请输入客户姓名!");
				return false;
			}
			//手机号码(联系电话)
			var phone=$("#PHONE").val();
			
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
			bossOriginalXml.push('	<phone>'+phone+'</phone>');
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
			if("0" == result){
				$("#"+picid).val(picID);
				$("#"+picstream).val(picStream);
				var param = "&BLACK_TRADE_TYPE=10";			
				param += "&CERT_ID="+psptId;
				param += "&CERT_NAME="+custName;
				param += "&CERT_TYPE="+psptType;
				param += "&PIC_STREAM="+picStream+"&FRONTBASE64="+escape (encodeURIComponent(fornt));			
				param += "&SERIAL_NUMBER="+phone;
				$.beginPageLoading("正在进行人像比对。。。");
				$.ajax.submit(null, 'cmpPicInfo', param, '', function(data){
					$.endPageLoading();
					if(data && data.get("X_RESULTCODE")== "0"){			
						MessageBox.success("成功提示","人像比对成功", null, null, null);
						return true;
					}else if(data && data.get("X_RESULTCODE")== "1"){
						$("#"+picid).val("ERROR");
						$("#"+picstream).val(data.get("X_RESULTINFO"));
						MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
						return false;
					}
				},
				function(error_code,error_info,derror){
					$("#"+picid).val("ERROR");
					$("#"+picstream).val("人像比对失败,请重新拍摄");
					$.endPageLoading();
					showDetailErrorInfo(error_code,error_info,derror);
			    });
			}else{
				MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
			}			
		}
		/*queryBanks: function (){
			var pay_mode_code=$("#PAY_MODE_CODE").val();
			if(pay_mode_code == '0') 
			{
				
			}else{
				var superbank_code = $("#SUPER_BANK_CODE").val();
				if(superbank_code == ''){
				
				}else{
					popupPage('bat.batbankquery.BankPopupQry','queryBankInfo','&SUPER_BANK_CODE='+$("#SUPER_BANK_CODE").val()+"&IS_POP=BANKQRY",'选择银行',900,490,'BANK_CODE');
				}
			}
		}*/
		
	});
})();
