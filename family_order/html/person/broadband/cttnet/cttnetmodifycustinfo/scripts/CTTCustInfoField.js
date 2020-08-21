(function($){
	$.extend({CTTCustInfo:{
		infoPart: "CustInfoPart",
		realNameSVCName:null,		//实名制数量限制服务名字，如果有需要可以指向自己实现的服务
		relatePayName:true,			//该参数表示在设置客户姓名时候，如果检测到页面使用了账户编辑里面的PAY_NAME,则两边数据关联起来
		widgets:[],
		isRealName:false,			//是否启用实名制校验
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
		//绑定事件
		bindEvents:function(){
			var oThis = this;
			//身份证类型
			$("#custInfo_PSPT_TYPE_CODE,#custInfo_AGENT_PSPT_TYPE_CODE,#custInfo_RESP_PSPT_TYPE_CODE").bind("change", function(){
				oThis.chkPsptTypeCode($(this).attr("name"));
			});
			$("#custInfo_AGENT_PSPT_TYPE_CODE").bind("blur", function(){
				oThis.afterChkAgentPsptTypeCode();
			});
			//校验身份证
			$("#custInfo_PSPT_ID,#custInfo_AGENT_PSPT_ID,#custInfo_RESP_PSPT_ID").bind("change", function(){
				oThis.chkPsptId($(this).attr("name"));
			});
			$("#custInfo_AGENT_PSPT_ID").bind("blur", function(){
				oThis.afterChkAgentPsptId();
			});
			$("#custInfo_PSPT_ID").bind("blur", function(){
				oThis.afterChkPsptId();
			});
			//扫描身份证
			$("#ReadCardButton").bind("click", function(){
				oThis.clickScanPsptNew();
			});		
			//客户姓名
			$("#custInfo_CUST_NAME,#custInfo_AGENT_CUST_NAME,#custInfo_RESP_CUST_NAME").bind("change", function(){
				oThis.chkCustName($(this).attr("name"));
			});
			$("#custInfo_CUST_NAME").bind("blur", function(){
				oThis.afterChkCustName();
			});
			$("#custInfo_AGENT_CUST_NAME").bind("blur", function(){
				oThis.afterChkAgentCustName();
			});
			//地址
			$("#custInfo_PSPT_ADDR,#custInfo_AGENT_PSPT_ADDR,#custInfo_RESP_PSPT_ADDR").bind("change", function(){
				oThis.chkPsptAddr($(this).attr("name"));
			});
			$("#custInfo_AGENT_PSPT_ADDR").bind("blur", function(){
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
		
		/**
		 * 选择“集团客户”证件类型（营业执照、事业单位法人证书、单位证明、组织机构代码证、社会团体法人登记证书）开户时， 
		 * 需支持录入经办人名称、经办人证件类型、经办人证件号码、经办人证件地址，这些信息为必填项目。经办人的证件校验规则同个人大众客户的规则一致。
		 * E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
		 */
		renderSpecialField:function(psptTypeCode){
			//debugger;
			var fields=["custInfo_AGENT_CUST_NAME","custInfo_AGENT_PSPT_TYPE_CODE","custInfo_AGENT_PSPT_ID","custInfo_AGENT_PSPT_ADDR"];
			var tag = (psptTypeCode=="E" || psptTypeCode=="G" 
				|| psptTypeCode=="D" || psptTypeCode=="M" || psptTypeCode=="L")? true : false;
			
			for(var i=0; i<fields.length; i++)
			{
				this.setRequiredField(fields[i], tag);
			}
		},
		renderSpecialFieldByPsptId:function(psptId){
			//debugger;
			//检查年龄是否在12-120之间，显示隐藏经办人栏
			if(trim(psptId)!="" && !this.checkAge(psptId))
			{
				var fields=["custInfo_AGENT_CUST_NAME","custInfo_AGENT_PSPT_TYPE_CODE","custInfo_AGENT_PSPT_ID","custInfo_AGENT_PSPT_ADDR"];
				for(var i=0; i<fields.length; i++)
				{
					this.setRequiredField(fields[i],true);
				}					   
			}
			else
			{
				var fields=["custInfo_AGENT_CUST_NAME","custInfo_AGENT_PSPT_TYPE_CODE","custInfo_AGENT_PSPT_ID","custInfo_AGENT_PSPT_ADDR"];					 					
				for(var i=0; i<fields.length; i++)
				{
					this.setRequiredField(fields[i], false);
				}
			}
		},
		
		// 根据身份证号码获取生日日期
		getBirthdayByPsptId:function(psptId) {
			//debugger;
			var tmpStr="";
			if((psptId.length!=15) &&(psptId.length!=18)) 
			{
			    alert("输入的证件号码位数错误！");
				return tmpStr;
			}
			if(psptId.length==15) 
			{
				tmpStr= "19" + psptId.substring(6,12);
			}
			else 
			{
		    	tmpStr = psptId.substring(6,14);
			}
			return tmpStr.substring(0,4) + "-" + tmpStr.substring(4,6) + "-" + tmpStr.substring(6);
		},
		
		chkPsptTypeCode:function(fieldName){
			//debugger;
			var obj=$("#"+fieldName);
			var custNameObj,psptObj;
			
			if(fieldName == "custInfo_PSPT_TYPE_CODE")
			{
		    	var strPsptTypeCode = obj.val();
		    	if(strPsptTypeCode == 0 || strPsptTypeCode == 1)
		    	{
		    		var departKindCode = $("#DEPART_KIND_CODE").val(); 
					var staffv = $("#STAFF_ID").val().substring(0,4); 
					if(departKindCode != "" && departKindCode != "100" 
						&& departKindCode != "500" && staffv != "HNSJ" 
						&& staffv != "HNHN" && staffv != "SUPERUSR")
					{ 
						$("#custInfo_PSPT_ID").val("");
						$("#custInfo_PSPT_ID").attr("disabled",true);
						$("#custInfo_PSPT_ADDR").attr("disabled",true);
						$("#custInfo_PSPT_END_DATE").attr("disabled",true);
						$("#custInfo_CUST_NAME").attr("disabled",true);
						$("#custInfo_SEX").attr("disabled",true); 
					}
		    	}
		    	else
		    	{
		    		$("#custInfo_PSPT_ID").attr("disabled",false);
					$("#custInfo_PSPT_ADDR").attr("disabled",false);
					$("#custInfo_PSPT_END_DATE").attr("disabled",false);
					$("#custInfo_CUST_NAME").attr("disabled",false);
					$("#custInfo_SEX").attr("disabled",false); 
		    	}
				custNameObj = $("#custInfo_CUST_NAME");
				psptObj = $("#custInfo_PSPT_ID");
				this.chkCustName("custInfo_CUST_NAME");
				
			}
			else if(fieldName == "custInfo_AGENT_PSPT_TYPE_CODE")
			{
				custNameObj = $("#custInfo_AGENT_CUST_NAME");
				psptObj = $("#custInfo_AGENT_PSPT_ID");
				this.chkCustName("custInfo_AGENT_CUST_NAME");
			}
			else if(fieldName == "custInfo_RESP_PSPT_TYPE_CODE")
			{
				custNameObj = $("#custInfo_RESP_CUST_NAME");
				psptObj = $("#custInfo_RESP_PSPT_ID");
				this.chkCustName("custInfo_RESP_CUST_NAME");
			}
			var psptTypeCode = obj.val();
			var custName = $.trim(custNameObj.val());
			
			//0-本地身份证 1-外地身份证
			if(psptTypeCode==0 || psptTypeCode==1 || psptTypeCode==2)
			{
				psptObj.attr("datatype", "pspt");
			}
			else if($("#custInfo_IS_REAL_NAME").attr("checked") && psptTypeCode == "Z")
			{
				alert("办理实名制用户，证件类型不能为其它！");
				$("#custInfo_PSPT_TYPE_CODE").val(''); 
    			$("#custInfo_PSPT_TYPE_CODE").focus();
    			return;
			}
			else 
			{
				psptObj.attr("datatype", "");
			}
			
			if(custName!="" && psptTypeCode != "A")
			{
		    	if(this.includeChineseCount(custName)<2)
		    	{
		    		alert(obj.attr("desc")+"不是护照，"+custNameObj.attr("desc")+"不能少于2个中文字符！");
		    		custNameObj.val("");
					return ;
		    	}
			}
			
			if(fieldName != "custInfo_PSPT_TYPE_CODE")
			{
				if(fieldName == "custInfo_AGENT_PSPT_TYPE_CODE")
				{
					$("#custInfo_AGENT_PSPT_ID").trigger("change");
				}
						
				//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
				//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
				//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
				if(fieldName == "custInfo_AGENT_PSPT_TYPE_CODE")
				{
					var agentObj = $("#custInfo_AGENT_PSPT_TYPE_CODE");
					var agentValue = agentObj.val();
					if(agentValue=="D" || agentValue=="E" || agentValue=="G" || agentValue=="L" || agentValue=="M")
					{	
						alert("证件类型选择集团证件，经办人的证件类型只能选择个人证件，请重新选择！");
						agentObj.find("option[index=0]").attr("selected", true);
						$("#custInfo_AGENT_PSPT_ID").attr("datatype", "");
						return;							
					}
											
					$("#custInfo_AGENT_PSPT_ID").trigger("change");
				}
				
				return;
			}
			
			//实名制开户限制
			if(psptTypeCode == "Z" && this.isRealName)
			{
				alert("实名制开户，证件类型不能为其他，请重新选择！");
				$(this).find("option[index=0]").attr("selected", true);
				psptObj.attr("datatype","");
				return;
			}
			
			$("#custInfo_PSPT_ID").trigger("change");
			
			this.renderSpecialField(psptTypeCode);
			
			//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
			//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
			//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
			if(fieldName=="custInfo_PSPT_TYPE_CODE" || fieldName=="custInfo_USE_PSPT_TYPE_CODE") 
			{
				var objValue = $("#custInfo_PSPT_TYPE_CODE").val();
				if(objValue=="D" || objValue=="E" || objValue=="G" || objValue=="L" || objValue=="M")
				{
					var useObjValue = $("#custInfo_USE_PSPT_TYPE_CODE").val();
					if(useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M")
					{
						
						alert("证件类型选择集团证件，使用人的证件类型只能选择个人证件，请重新选择！");
						useObj.find("option[index=0]").attr("selected", true);
						$("#custInfo_USE_PSPT_ID").attr("datatype", "");
						return;
					}
				}
			}
			
			if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2") 
			{
				this.renderSpecialFieldByPsptId($("#custInfo_PSPT_ID").val());
			}
			
			//this.verifyIdCardName(fieldName);
			
		},
		chkPsptId:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var psptTypeObj;
			if(fieldName == "custInfo_PSPT_ID")
			{
				psptTypeObj = $("#custInfo_PSPT_TYPE_CODE");
			}
			else if(fieldName == "custInfo_AGENT_PSPT_ID")
			{
				psptTypeObj = $("#custInfo_AGENT_PSPT_TYPE_CODE");
			}
			else if(fieldName == "custInfo_RESP_PSPT_ID")
			{
				psptTypeObj = $("#custInfo_RESP_PSPT_TYPE_CODE");
			}
			var psptId = $.trim(obj.val());
			var desc = $.trim(obj.attr("desc"));
			var psptTypeCode = psptTypeObj.val();
			
			if(psptId == "")
			{
				return;
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
			
			/**
			if(psptId.length>=4 && this.serialNumber.length>=psptId.length && ( this.serialNumber.indexOf(psptId) ==0 
				|| this.serialNumber.lastIndexOf(psptId) == (this.serialNumber.length-psptId.length) )){
				alert("电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为"+desc+"!");
				obj.val("");
				obj.focus();
				return;
			}
			*/
			
			//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
			if(psptTypeCode=="H")
			{
				if(psptId.length!=9 && psptId.length!=11)
				{
					alert("港澳居民回乡证校验："+desc+"必须为9位或11位。");
					obj.val("");
					obj.focus();
					return;
				}
				if( !(psptId.charAt(0)=="H" || psptId.charAt(0)=="M") || !$.toollib.isNumber(psptId.substr(1)) )
				{
					alert("港澳居民回乡证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
					obj.val("");
					obj.focus();
					return;
				}
			}
			else if(psptTypeCode=="O")
			{
			   	var psptIdtemp = psptId.replace(/\s/g,''); 				 				
			    if(psptIdtemp!=psptId)
			    {
					alert("港澳居民来往内地通行证校验：证件号码中间不能有空格。");
					obj.val("");
					obj.focus();
					return;
			    }					    
			    //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
				if(psptId.length!=9 && psptId.length!=11)
				{
					alert("港澳居民来往内地通行证校验："+desc+"必须为9位或11位。");
					obj.val("");
					obj.focus();
					return;
				}
				if( !(psptId.charAt(0)=="H" || psptId.charAt(0)=="M") || !$.toollib.isNumber(psptId.substr(1)) )
				{
					alert("港澳居民来往内地通行证校验："+desc+"首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
					obj.val("");
					obj.focus();
					return;
				}
			}
			else if(psptTypeCode=="I")
			{		
				//台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，
				//括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
				if(psptId.length!=8 && psptId.length!=11)
				{
					alert("台湾居民回乡校验："+desc+"必须为8位或11位！");
					obj.val("");
					obj.focus();
					return false;
				}
				if(psptId.length==8)
				{
					if(!$.toollib.isNumber(psptId))
					{
						alert("台湾居民回乡校验："+desc+"为8位时，必须均为阿拉伯数字！");
						obj.val("");
						obj.focus();
						return false;
					}
				}
				if(psptId.length==11)
				{
					if(!$.toollib.isNumber(psptId.substring(0,10)))
					{
						alert("台湾居民回乡校验：："+desc+"为11位时，前10位必须均为阿拉伯数字。");
						obj.val("");
						obj.focus();
						return false;
					}
				}
           }
           else if(psptTypeCode=="N")
           {
				//台湾居民来往大陆通行证:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，
				//括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
				var psptIdtemp = psptId.replace(/\s/g,''); 		
				if(psptIdtemp!=psptId)
				{
					alert("台湾居民来往大陆通行证校验：证件号码中间不能有空格。");
					obj.val("");
					obj.focus();
					return;
			    }
				if(psptId.substr(0, 2)!="TW"&&psptId.substr(0, 4)!="LXZH")
				{				
					if(psptId.length==11 ||psptId.length==12)
					{
						if(!$.toollib.isNumber(psptId.substring(0,10)))
						{
							alert("台湾居民来往大陆通行证校验："+desc+"为11或12位时，前10位必须均为阿拉伯数字。");
							obj.val("");
							obj.focus();
							return;
						}
					}
					else if(psptId.length==8)
					{
						if(!$.toollib.isNumber(psptId))
						{
							alert("台湾居民来往大陆通行证校验："+desc+"为8位时，必须均为阿拉伯数字。");
							obj.val("");
							obj.focus();
							return;
						}
					}
					else if(psptId.length==7)
					{
						if(!$.toollib.isNumber(psptId))
						{
							alert("台湾居民来往大陆通行证校验："+desc+"为7位时，必须均为阿拉伯数字。");
							obj.val("");
							obj.focus();
							return;
						}
					}
					else
					{
						alert("台湾居民来往大陆通行证校验："+desc+"格式错误");
						obj.val("");
						obj.focus();
						return;
					}
				}
				else
				{
					var psptIdsub ;
					if(psptId.substr(0, 2)=="TW")
					{
						psptIdsub = psptId.substr(2);
					}
					else if(psptId.substr(0, 4)=="LXZH")
					{
						psptIdsub = psptId.substr(4);
					}
					
					var re=/^[()A-Z0-9]+$/;
					var re1=/^[•··.．·\d\u4e00-\u9fa5]+$/;
					var pattern1 =/[A-Z]/;
					var pattern2 =/[0-9]/;
					var pattern3 =/[(]/;
					var pattern4 =/[)]/;
					
					if(re1.test(psptIdsub)||!re.test(psptIdsub)||!pattern1.test(psptIdsub)
						||!pattern2.test(psptIdsub)||!pattern3.test(psptIdsub)||!pattern4.test(psptIdsub))
					{
						alert("台湾居民来往大陆通行证校验："+desc+"前2位“TW”或 “LXZH”字符时，后面是阿拉伯数字、英文大写字母与半角“()”的组合");
						obj.val("");
						obj.focus();
						return;
					}
				}
			}
			else if(psptTypeCode=="C" || psptTypeCode=="A")
			{
				var psptIdtemp = psptId.replace(/\s/g,''); 				 				
				if(psptIdtemp!=psptId)
				{
					alert("证件号码中间不能有空格。");
					obj.val("");
					obj.focus();
					return;
			    }
				//军官证、警官证、护照：证件号码须大于等于6位字符
				if(psptId.length < 6)
				{
					var tmpName= psptTypeCode=="A" ? "护照校验：" : "军官证类型校验：";
					alert(tmpName+desc+"须大于等于6位字符!");
					obj.val("");
					obj.focus();
					return;
				}
			}
			else if(psptTypeCode=="E")
			{
				//营业执照：证件号码长度需满足15位 20151022 REQ201510140003 营业执照证件规则调整 CHENXY3
				if(psptId.length != 13 && psptId.length != 15 && psptId.length != 18 
					&& psptId.length != 20 && psptId.length != 22 && psptId.length != 24)
				{
					alert("营业执照类型校验："+desc+"长度需满足13位、15位、18位、20位、22位或24位！当前："+psptId.length+"位。");
					obj.val("");
					obj.focus();
					return;
				}
			}
			else if(psptTypeCode=="M")
			{
				//组织机构代码校验
				if(psptId.length != 10 && psptId.length != 18)
				{
					alert("组织机构代码证类型校验："+desc+"长度需满足10位或18位。");
					obj.val("");
					obj.focus();
					return;
				}
				if((psptId.length == 10 &&psptId.charAt(8) != "-")||(psptId.length == 18 &&psptId.charAt(16) != "-"))
				{
					alert("组织机构代码证类型校验："+desc+"规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
					obj.val("");
					obj.focus();
					return;
				}
			}
			else if(psptTypeCode=="G")
			{
				//事业单位法人登记证书：证件号码长度需满足12位
				if(psptId.length != 12 && psptId.length != 18)
				{
					alert("事业单位法人登记证书类型校验："+desc+"长度需满足12位或者18位。");
					obj.val("");
					obj.focus();
					return;
				}
			}
			else if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2") 
			{
				if(psptId.substr(psptId.length-1,psptId.length)=="x")
				{
					obj.val(psptId.substr(0,psptId.length-1)+"X");
				}
				
				if(fieldName == "custInfo_AGENT_PSPT_ID")
				{
					if(trim(psptId)!="" && !this.checkAge(psptId))
					{
				     	alert('身份证年龄范围必须在12-120岁之间'); 
						obj.val("");
						obj.focus();
						return;
					}
				}
				
				//身份证相关检查
				obj.attr("datatype", "pspt");
				if(!$.validate.verifyField(obj[0])) 
				{
					//alert(psptTypeObj.find("option[value="+psptTypeCode+"]").text()+"号码格式不对。");
					obj.val("");
					obj.focus();
					return;
				}
				else
				{
					obj.attr("datatype", "");
				}
				
				var area = {11:"\u5317\u4eac", 12:"\u5929\u6d25", 13:"\u6cb3\u5317", 14:"\u5c71\u897f", 15:"\u5185\u8499\u53e4", 21:"\u8fbd\u5b81", 22:"\u5409\u6797", 23:"\u9ed1\u9f99\u6c5f", 31:"\u4e0a\u6d77", 32:"\u6c5f\u82cf", 33:"\u6d59\u6c5f", 34:"\u5b89\u5fbd", 35:"\u798f\u5efa", 36:"\u6c5f\u897f", 37:"\u5c71\u4e1c", 41:"\u6cb3\u5357", 42:"\u6e56\u5317", 43:"\u6e56\u5357", 44:"\u5e7f\u4e1c", 45:"\u5e7f\u897f", 46:"\u6d77\u5357", 50:"\u91cd\u5e86", 51:"\u56db\u5ddd", 52:"\u8d35\u5dde", 53:"\u4e91\u5357", 54:"\u897f\u85cf", 61:"\u9655\u897f", 62:"\u7518\u8083", 63:"\u9752\u6d77", 64:"\u5b81\u590f", 65:"\u65b0\u7586", 71:"\u53f0\u6e7e", 81:"\u9999\u6e2f", 82:"\u6fb3\u95e8", 91:"\u56fd\u5916"};
			
				psptId = psptId.toUpperCase();
				
				if (area[parseInt(psptId.substr(0, 2))] == null) 
				{
					alert("\u8eab\u4efd\u8bc1\u53f7\u7801\u4e0d\u6b63\u786e\u0028\u5730\u533a\u975e\u6cd5\u0029\uff01");
					obj.val("");
					obj.focus();
					return;
				}
				
				if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(psptId))) 
				{
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
				
				if (len == 15) 
				{
					re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
					arrSplit = psptId.match(re);  // 检查生日日期是否正确
					dtmBirth = new Date("19" + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
				}
				
				if (len == 18) 
				{
					re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
					arrSplit = psptId.match(re);  // 检查生日日期是否正确
					dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
				}
				
				var bGoodDay = (dtmBirth.getFullYear() == Number(arrSplit[2])) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
				if (!bGoodDay) 
				{
					alert("\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u51fa\u751f\u65e5\u671f\u4e0d\u5bf9\uff01");
					obj.val("");
					obj.focus();
					return;
				}
				
				/*每位加权因子*/
				var powers = new Array("7","9","10","5","8","4","2","1","6","3","7","9","10","5","8","4","2");
				/*第18位校检码*/
				var parityBit = new Array("1","0","X","9","8","7","6","5","4","3","2");
				var psptBit = psptId.charAt(17).toUpperCase();
				var id17 = psptId.substring(0,17);    
			   	/*加权 */
			   	var power = 0;
			   	for(var i=0;i<17;i++)
			   	{
			   		power += parseInt(id17.charAt(i),10) * parseInt(powers[i]);
			   	}              
			   /*取模*/ 
			   var mod = power % 11;
			   var checkBit = parityBit[mod];
			   if(psptBit!=checkBit)
			   {
			   		alert('身份证号码不合法'); 
					obj.val("");
					obj.focus();
					return;
			   }
			   
			   var bit11 = psptId.substring(10,11);
    		   var bit13 = psptId.substring(12,13);
			   if(bit11!="0" && bit11!="1")
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
			   
			   //在线实名制校验
			   this.verifyIdCard(fieldName);
			   
			   //设置生日日期
			   if(fieldName == "custInfo_PSPT_ID")
			   {
					this.renderSpecialFieldByPsptId(psptId);
					$("#custInfo_BIRTHDAY").val(this.getBirthdayByPsptId(psptId));
					
					// 根据身份证号，自动设置性别
					
					var custSex =  $('#custInfo_SEX').val();
					if (psptId.length == 18 && (psptId.charAt(16) % 2 == 0)) {
							$('#custInfo_SEX').val('F');
					}
					if (custSex.length == 18 && (custSex.charAt(16) % 2 == 1)) {
						$('#custInfo_SEX').val('M');
					}
					if (custSex.length == 15 && (custSex.charAt(14) % 2 == 0)) {
						$('#custInfo_SEX').val('F');
					}
					if (custSex.length == 15 && (custSex.charAt(14) % 2 == 1)) {
						$('#custInfo_SEX').val('M');
					}	
			   }
			   			   
			}
						
			//原来的做法
			if(fieldName == "custInfo_PSPT_ID")
			{
				this.checkRealNameLimitByPsptOld();
			}
			
			if(!this.isRealName || fieldName != "custInfo_PSPT_ID") 
			{
				if(fieldName == "custInfo_USE_PSPT_ID")
				{
					if(!this.checkUsePsptIdForReal()) 
					{
						obj.val("");
						obj.focus();
						return;
					}
					this.checkRealNameLimitByUsePspt();
				}
				return;
			}
			
			if(!this.checkPsptIdForReal()) 
			{
				obj.val("");
				obj.focus();
				return;
			}
			
			//检查同一证件号已开实名制用户的数量是否已超出预定值
			//this.checkRealNameLimitByPspt();
			
			//开户-身份证判定是否黑名单用户
			//this.checkPsptidBlackListInfo();
			
			//营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
			//this.verifyIdCardName(fieldName);
		},
		afterChkPsptId:function(){
			//debugger;
			var obj =$(this)
			if(obj.val() == "")
			{
				return;
			}
		},
		clickScanPsptNew:function()
		{
			debugger;
			getMsgByEForm("custInfo_PSPT_ID","custInfo_CUST_NAME","custInfo_SEX","custInfo_FOLK_CODE","custInfo_BIRTHDAY","custInfo_PSPT_ADDR,custInfo_POST_ADDRESS",null,"custInfo_PSPT_END_DATE");

			$("#custInfo_PSPT_ID").focus();
			//原来做法
    		this.checkRealNameLimitByPsptOld();
			//在线实名制校验
			this.verifyIdCard("custInfo_PSPT_ID");
		},
		chkCustName:function(fieldName){
			debugger;
			var obj = $("#"+fieldName);
		    var custNameTrim = obj.val().replace(/\s+/g,"");
		    obj.val(custNameTrim);
			var psptTypeCode,psptTypeDesc;
			//判断到底是客户姓名还是经办人姓名
			if(fieldName=="custInfo_CUST_NAME")
			{
				psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#custInfo_PSPT_TYPE_CODE").attr("desc");
			    
			    var oldName =  $("#OLD_CUST_NAME").val();
				var newName =  $("#custInfo_CUST_NAME").val();
				if ($("#STAFF_SPECIAL_RIGTH").val() != "true"
					&& $("#IS_IN_PURCHASE").val() == "1" && oldName != newName) 
				{
					alert("该用户还处在营销活动期限内，不能够修改客户名称！");
					$("#custInfo_CUST_NAME").val(oldName);
					return;
				}
			}
			else if(fieldName=="custInfo_AGENT_CUST_NAME")
			{
				psptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#custInfo_AGENT_PSPT_TYPE_CODE").attr("desc");
			}
			else if(fieldName=="custInfo_RESP_CUST_NAME")
			{
				psptTypeCode = $("#custInfo_RESP_PSPT_TYPE_CODE").val();
			    psptTypeDesc = $("#custInfo_RESP_PSPT_TYPE_CODE").attr("desc");
			}
			var custName = $.trim(obj.val());
			var desc = obj.attr("desc");
			
		    if(!custName)
		    {
		    	return;
		    }
		    		    		
		    if(psptTypeCode == "E" || psptTypeCode == "G" || psptTypeCode == "M")
		    {
		    	//营业执照、组织机构代码证、事业单位法人登记证书
		    	if(this.includeChineseCount(custName)<4)
		    	{
					alert(psptTypeDesc+"不是护照,"+desc+"不能少于4个中文字符！");
					obj.val("");
					obj.focus();
					return;
				}
		    }	
		    
		    var mainPsptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
		    if(mainPsptTypeCode == "E"||mainPsptTypeCode == "G"||mainPsptTypeCode == "D" ||mainPsptTypeCode == "M" ||mainPsptTypeCode == "L")
		    {
		    	//E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
		    	var custnametemp = trim($("#custInfo_CUST_NAME").val());
		    	var agentnametemp = trim($("#custInfo_AGENT_CUST_NAME").val());
		    	if(custnametemp !="" && agentnametemp !="" && custnametemp == agentnametemp)
		    	{
					alert("单位名称和经办人名称不能相同！");
					obj.val("");
					obj.focus();
					return;
		    	}
		    }
		    		    		    
		    if(psptTypeCode != "A" && psptTypeCode != "D")
		    {
		    	var pattern =/[a-zA-Z0-9]/;
		    	if(pattern.test(custName))
		    	{
		    		alert(psptTypeDesc+"不是护照, "+desc+"不能包含数字和字母！");
		    		obj.val("");
		    		obj.focus();
					return;
		    	}
				if(this.includeChineseCount(custName)<2)
				{
					alert(psptTypeDesc+"不是护照,"+desc+"不能少于2个中文字符！");
					obj.val("");
					obj.focus();
					return;
				}
				
			}
			else if(psptTypeCode == "A")
			{
				/*护照：客户名称须大于三个字符，不能全为阿拉伯数字*/
				if(custName.length<3 || $.toollib.isNumber(custName))
				{
					alert(psptTypeDesc+"是护照,"+desc+"须大于三个字符，且不能全为阿拉伯数字！");
					obj.val("");
					obj.focus();
					return;
				}
				var specialStr ="“”‘’，《》~！@#￥%……&*（）【】｛｝；：‘’“”，。、《》？+——-=";
				for(i=0;i<specialStr.length;i++)
				{
					if (custName.indexOf(specialStr.charAt(i)) > -1)
					{
						alert(obj.attr("desc")+"包含特殊字符，请检查！");
						obj.val("");
						obj.focus();
						return;
					}
				}
			}
			if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2" || psptTypeCode=="C"|| psptTypeCode=="H"|| psptTypeCode=="I"|| psptTypeCode=="N"|| psptTypeCode=="O")
			{
				var re=/^[•··.．·\d\u4e00-\u9fa5]+$/;
				if(!re.test(custName))
				{
					alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				}
			}
			else if(psptTypeCode=="H"|| psptTypeCode=="I"||psptTypeCode=="J")
			{
				//港澳证、台胞证
				if(custName.length<2)
				{
					alert(psptTypeDesc+"是护照,"+desc+"须两个字符及以上");
					obj.val("");
					obj.focus();
					return;
				}
				var re=/^[•··.．·\d\u4e00-\u9fa5]+$/;
				if(!re.test(custName))
				{
					alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				}
			}
		    
		   /* if(custName.indexOf("校园")>-1 || custName.indexOf("海南通")>-1 || custName.indexOf("神州行")>-1
		    		|| custName.indexOf("动感地带")>-1 || custName.indexOf("套餐")>-1) 
		    {
		        alert(desc+"不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！");
		        obj.val("");
		        obj.focus();
		        return;
		    }*/

            if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2"|| psptTypeCode == "3"
                || psptTypeCode == "O" || psptTypeCode == "N" || psptTypeCode == "P"
                || psptTypeCode == "Q" || psptTypeCode == "W"	) {
		    	//在线实名制校验
				this.verifyIdCard(fieldName);
			}
			
			//原来的做法
			if(fieldName == "custInfo_CUST_NAME")
			{
				this.checkRealNameLimitByPsptOld();
			}
			
		    //如果不需要进行实名制，或者是非客户姓名，则不需要进行实名制校验限制
		    if(!this.isRealName || fieldName!="custInfo_CUST_NAME") 
		    {
		    	if(fieldName=="custInfo_USE")
		    	{
					if(this.checkUsePsptIdForReal()) 
					{
						this.checkRealNameLimitByUsePspt();
					}
				}
				return;
			}
			
			//if(this.checkPsptIdForReal()) 
			//{
			//	this.checkRealNameLimitByPspt();
			//}
			
			//this.verifyIdCardName(fieldName);
		},
		includeChinese:function(custName){
			//debugger;
			// 是否包含中文字符
			for(var i=0; i<custName.length; i++)
			{
				if($.toollib.isChinese(custName.charAt(i)))
				{
					return true;
				}
			}
			return false;
		},
		includeChineseCount:function(custName){
			//debugger;
			// 是否包含中文字符个数
			var count = 0;
			for(var i=0; i<custName.length; i++)
			{
				if($.toollib.isChinese(custName.charAt(i)))
				{
					count++;
				}
			}
			return count;
		},		
		afterChkCustName:function(){
			//debugger;
			var obj = $("#custInfo_CUST_NAME");
			var custName = $.trim(obj.val());
			
			if(custName == "")
			{
				return;
		    }
			var mainPsptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
		    if(mainPsptTypeCode == "E"||mainPsptTypeCode == "G"||mainPsptTypeCode == "D" ||mainPsptTypeCode == "M" ||mainPsptTypeCode == "L")
		    {
		    	//E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书  不进行特殊字符校验
		    	
		    }
		    else
		    {					
			  var specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
			  for(i=0;i<specialStr.length;i++)
			  {
				 if (custName.indexOf(specialStr.charAt(i)) > -1)
				 {
				  	alert(obj.attr("desc")+"包含特殊字符，请检查！");
					obj.val("");
					obj.focus();
					return;
				 }
			  }
		    }
		},
		chkPsptAddr:function(fieldName){
			//debugger;
			var obj=$("#"+fieldName);
			var psptAddr = this.trimAll(obj.val());
			
			var desc = obj.attr("desc");
			var zjNum = psptAddr.replace(/[^\x00-\xff]/g, "**").length ; //字节数
			if(psptAddr=="" || zjNum*1<12)
			{
				alert(desc+"栏录入文字需不少于6个汉字或12个字节！");
				obj.val("");
				obj.focus();
				return;
			}
			
			if($.toollib.isNumber(psptAddr))
			{
				alert(desc+"栏不能全部为数字！");
				obj.val("");
				obj.focus();
				return;
			}
			
			if(fieldName == "custInfo_PSPT_ADDR")
			{
				var strPsptAddr = $("#custInfo_PSPT_ADDR").val();
				$('#custInfo_POST_ADDRESS').val(strPsptAddr);
			}
		},
		trimAll:function(str)
		{     
		  return str.replace(/(^\s+)|(\s+$)/g,"").replace(/\s/g,""); 
		},
		afterChkAgentCustName:function()
		{
			//debugger;
			var opentype = $("#OPEN_TYPE").val();
			if(opentype == "IOT_OPEN")
			{
				var agentval = $("#custInfo_AGENT_CUST_NAME").val();
				//$("#RSRV_STR2").val(agentval);
			}
		},
		afterChkAgentPsptTypeCode:function()
		{
			var opentype = $("#OPEN_TYPE").val();
			if(opentype == "IOT_OPEN")
			{
				var agentval = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
			}
		},	
		afterChkAgentPsptId:function()
		{
			var opentype = $("#OPEN_TYPE").val();
			if(opentype == "IOT_OPEN")
			{
				var agentval = $("#custInfo_AGENT_PSPT_ID").val();
			}
		},
		afterChkAgentPsptAddr:function()
		{
			var opentype = $("#OPEN_TYPE").val();
			if(opentype == "IOT_OPEN")
			{
				var agentval = $("#custInfo_AGENT_PSPT_ADDR").val();
			}
		},
		/*****************************以下为客户校验辅助校验部分********************************************/
		//实名制基本数据校验
		checkPsptIdForReal:function(){
			//debugger;
		    if($("#custInfo_PSPT_TYPE_CODE").val()=="Z") 
		    {
		        alert("实名制开户，证件类型不能为其他，请重新选择！");
		        return false;
		    }
		    var psptId = $("#custInfo_PSPT_ID").val();
		    if($.toollib.isRepeatCode(psptId))
		    {
		        alert("实名制开户，证件号码过于简单，请重新输入！");
		        return false;
		    }
		    /*if($("#custInfo_CUST_NAME").val().indexOf("海南通")>-1)
		    {
		        alert("实名制开户，客户名称不能为【海南通】，请重新输入！");
		        $("#custInfo_CUST_NAME").val("");
		        return false;
		    }*/
		    if(!$.validate.verifyField("custInfo_PSPT_ID")){
		        return false;
		    }
		    return true;
		},
		//使用人基本数据校验
		checkUsePsptIdForReal:function(){
			//debugger;
		    if($("#custInfo_USE_PSPT_TYPE_CODE").val()=="Z") 
		    {
		        alert("实名制开户，使用人证件类型不能为其他，请重新选择！");
		        return false;
		    }
		    var psptId = $("#custInfo_USE_PSPT_ID").val();
		    if($.toollib.isRepeatCode(psptId))
		    {
		        alert("实名制开户，使用人证件号码过于简单，请重新输入！");
		        return false;
		    }
		    /*if($("#custInfo_USE").val().indexOf("海南通")>-1)
		    {
		        alert("实名制开户，使用人姓名不能为【海南通】，请重新输入！");
		        $("#custInfo_USE").val("");
		        return false;
		    }*/
		    if(!$.validate.verifyField("custInfo_USE_PSPT_ID"))
		    {
		        return false;
		    }
		    return true;
		},
		//在线实名制校验
		verifyIdCard:function(fieldName){
			debugger;
			var obj=$("#"+fieldName);
			var custNameObj,psptObj;
			if(fieldName == "custInfo_PSPT_ID" || fieldName=="custInfo_CUST_NAME")
			{
				custNameObj = $("#custInfo_CUST_NAME");
				psptObj = $("#custInfo_PSPT_ID");
			}
			else if(fieldName == "custInfo_AGENT_PSPT_ID" || fieldName=="custInfo_AGENT_CUST_NAME")
			{
				custNameObj = $("#custInfo_AGENT_CUST_NAME");
				psptObj = $("#custInfo_AGENT_PSPT_ID");
			}
			
		    var psptId = $.trim(psptObj.val());
		    var psptName = $.trim(custNameObj.val());
		    if(psptId == ""|| psptName == "")
		    {
		        return false;
		    }
		    var param =  "&CERT_ID="+psptId +"&CERT_NAME="+encodeURIComponent(psptName);
		    $.beginPageLoading("在线实名制校验。。。");
			$.httphandler.get(this.clazz, "verifyIdCard", param, 
				function(data){
					$.endPageLoading();
					if(data && data.get("X_RESULTCODE")== "1")
					{ 
						MessageBox.error("告警提示","证件信息不合法",null, null, null, null);
						psptObj.val("");
						return false;
					}
					else if(data && data.get("X_RESULTCODE")== "2")
					{ 
						MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);						
						return true;
					}
					else
					{
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
		//营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
		verifyIdCardName:function(fieldName){
			debugger;
			var obj = $("#"+fieldName);
			var custNameObj,psptObj,psptcodeObj;
			if(fieldName == "custInfo_PSPT_ID" || fieldName=="custInfo_CUST_NAME")
			{
				custNameObj = $("#custInfo_CUST_NAME");
				psptObj = $("#custInfo_PSPT_ID");
				psptcodeObj = $("#custInfo_PSPT_TYPE_CODE");
			}
			else if(fieldName == "custInfo_AGENT_PSPT_ID" || fieldName=="custInfo_AGENT_CUST_NAME")
			{
				//return false;
				custNameObj = $("#custInfo_AGENT_CUST_NAME");
				psptObj = $("#custInfo_AGENT_PSPT_ID");
				psptcodeObj = $("#custInfo_AGENT_PSPT_TYPE_CODE");
			}
			else
			{
				return false;
			}
		    var psptId = $.trim(psptObj.val());
		    var psptName = $.trim(custNameObj.val());
		    var psptcode = $.trim(psptcodeObj.val());
		    if( psptId == "" || psptName=="" || psptcode=="")
		    {
		        return false;
		    }
		    //营业执照、组织机构代码证、事业单位法人登记证书
            if(psptcode!="E" && psptcode!="G" && psptcode!="M")
            //if(psptcode=="0"||psptcode=="1"||psptcode=="2")
            {
            	return false;
            }
		    var param =  "&CERT_TYPE="+psptcode+"&CERT_ID="+psptId +"&CERT_NAME="+encodeURIComponent(psptName);

		    $.beginPageLoading("单位名称校验。。。");
			$.httphandler.get(this.clazz, "verifyIdCardName", param, 
				function(data){
					$.endPageLoading();
					if(data && data.get("X_RESULTCODE")!= "0"){ 
						MessageBox.error("告警提示","同一个证件号码不能对应不同的名称",null, null, null, null);
						psptObj.val("");
						return false;
					}
					else 
					{
						return true;
					} 
				},function(code, info, detail){
					$.endPageLoading(); 
					MessageBox.error("错误提示","【同一个证件号码不能对应不同的单位名称】校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "【同一个证件号码不能对应不同的单位名称】校验失败");
			});	
		},
		checkAge:function(idCard){
			if(!idCard)
			{
				return false;
			}
			var bstr = idCard.substring(6,14)
			var _now = new Date();
			var _bir = new Date(bstr.substring(0,4),bstr.substring(4,6),bstr.substring(6,8));
			var _agen = _now-_bir;
			var _age = Math.round(_agen/(365*24*60*60*1000));
			return _age>=12 && _age<=120;
		},
		//检查同一证件号已开实名制用户的数量是否已超出预定值
		checkRealNameLimitByPspt:function(){
			debugger;
		    var custName = $("#custInfo_CUST_NAME").val();
		    var psptId = $("#custInfo_PSPT_ID").val();
		    var blackTradeType = $("#BLACK_TRADE_TYPE").val();
		    var param = "";
	    	
	    	if ('680' == blackTradeType)
	    	{
	    		if(psptId == "")
	    		{
				     return false;
			    }
	    		param = "&PSPT_ID="+psptId+"&EPARCHY_CODE=0898";
	    	}
	    	else
	    	{
	    		if(custName == "" || psptId == "")
	    		{
				     return false;
			    }
	    		
	    		param = "&CUST_NAME="+encodeURIComponent(custName)+"&PSPT_ID="+psptId+"&EPARCHY_CODE=0898";
	    	}
	    	
		    //如果没有设置则取默认服务名
		    var strCheckUserPsptId = $("#CHECK_USER_PSPTID").val();
		    if(strCheckUserPsptId == "CREATEUSERSW")
		    {
		    	param += "&NET_TYPE_CODE=12";
		    	this.realNameSVCName = "SS.CreatePersonUserSVC.checkRealNameLimitByPsptCtt";
		    	//this.setRealNameSVCName("SS.CreatePersonUserSVC.checkRealNameLimitByPsptCtt");
		    }
		    
		    //无手机宽带开户实名制开户限制
		    if ('680' == blackTradeType)
		    {
		    	this.realNameSVCName = "SS.NoPhoneWideUserCreateSVC.checkRealNameLimitByPspt";
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
						alert(data.get("MSG"));
						return;
					}else{
						$("#REALNAME_LIMIT_CHECK_RESULT").val("true");
					}
					$("#SPAN_PSPT_ID").addClass("e_elements-success");
				},function(code, info, detail){
					$.endPageLoading();
					$("#SPAN_PSPT_ID").addClass("e_elements-error");
					MessageBox.error("错误提示","实名制用户开户数量校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "实名制用户开户数量校验超时");
			});	
		},
		//开户-身份证判定是否黑名单用户
		checkPsptidBlackListInfo:function(){
			debugger;
		    var psptId = $("#custInfo_PSPT_ID").val();
		    var blackTradeType = $("#BLACK_TRADE_TYPE").val();
		    if(psptId == "")
		    {
		        return false;
		    }
		    var param =  "&PSPT_ID="+psptId+"&BLACK_TRADE_TYPE="+blackTradeType ;
		    
		    $.beginPageLoading("判定是否黑名单用户。。。");
			$.httphandler.get(this.clazz, "checkPsptidBlackListInfo", param, 
				function(data){
					$.endPageLoading();
					if( data.get("BLACKCODE")!= "0"){
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
			debugger;
		    var custName = $("#custInfo_USE").val();
		    var psptId = $("#custInfo_USE_PSPT_ID").val();
		    if(custName == "" || psptId == "")
		    {
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
					$("#span_USE_PSPT_ID").addClass("e_elements-success");
				},function(code, info, detail){
					$.endPageLoading();
					$("#span_USE_PSPT_ID").addClass("e_elements-error");
					MessageBox.error("错误提示","使用人证件信息数量校验获取后台数据错误！",null, null, info, detail);
				},function(){
					$.endPageLoading();
					MessageBox.alert("告警提示", "使用人证件信息数量校验超时");
			});	
		},
		checkRealNameLimitByPsptOld:function(){
		
			var custName = $("#custInfo_CUST_NAME").val();
    		var psptId = $("#custInfo_PSPT_ID").val();
    		var strNetTypeCode = $("#NET_TYPE_CODE").val();
    		var psptTypeCode=$("#custInfo_PSPT_TYPE_CODE").val();
    		if(custName == "" || psptId == "")
    		{
        		return false;
    		}
    		if(psptTypeCode=="E" || psptTypeCode=="G" 
    				|| psptTypeCode=="D" || psptTypeCode=="M" || psptTypeCode=="L"){
    		    return ;
    		}
		    var param = "&CUST_NAME=" + custName + "&PSPT_ID="+psptId+"&NET_TYPE_CODE="+strNetTypeCode+"&EPARCHY_CODE=0898&PSPT_TYPE_CODE="+psptTypeCode;
		    $.beginPageLoading("证件信息数量校验。。。");
		    
		    $.ajax.submit(null, 'checkRealNameLimitByPspt', param, '', function(data)
			{
				$.endPageLoading();
				if(data && data.get("CODE") != "0")
				{
					$("#custInfo_PSPT_ID").attr("value", "");
					alert(data.get("MSG"));
					return;
				}
			},
			function(error_code,error_info,derror)
			{
				$.endPageLoading();
				MessageBox.error("错误提示","证件号码校验后台数据错误！",null, null, error_info, derror);
    		});
		}
		
	}});
	$.extend($.CTTCustInfo, BaseInfoLib);
})(Wade);