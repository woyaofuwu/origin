if(typeof(BatPreCreateUser)=="undefined"){
	window["BatPreCreateUser"]=function(){
		
	};
	var batprecreateuser = new BatPreCreateUser();
}

(function(){
	$.extend(BatPreCreateUser.prototype,{
		
	chkCustName:function(fieldName){
		var obj=$("#"+fieldName); 
		/**
	     * REQ201609280015 chenxy3 20161104
	     * */
	    var custNameTrim=obj.val().replace(/\s+/g,"");
	    obj.val(custNameTrim);	
	},
	checkPaymode: function (){	
		var pay_mode_code=$("#PAY_MODE_CODE").val();
		$("#SUPER_BANK_CODE").val("");
	    $("#BANK_CODE").val("");
	    $("#BANK_ACCT_NO").val("");
	    $("#POP_BANK_CODE").val("");
	    
		if(pay_mode_code == '0') 
		{
		    $("#SUPER_BANK_CODE").attr("disabled", true);
		    $("#BANK_CODE").attr("disabled", true);
		    $("#POP_BANK_CODE").attr("disabled", true);
		    $("#BANK_ACCT_NO").attr("disabled", true);
		    
		    $("#bname").attr("class", "");
		    $("#sname").attr("class", "");
		    $("#baname").attr("class", "");
		    $("#POP_BANK_CODE").attr("nullable", "yes");
		    $("#BANK_CODE").attr("nullable", "yes");
		    $("#BANK_ACCT_NO").attr("nullable", "yes");
		    $("#SUPER_BANK_CODE").attr("nullable", "yes");
		} 
		else 
		{
		    $("#SUPER_BANK_CODE").attr("disabled", false);
		    $("#BANK_CODE").attr("disabled", false);
		    $("#BANK_ACCT_NO").attr("disabled", false);
		    
		    $("#bname").attr("class", "e_required");
		    $("#sname").attr("class", "e_required");
		    $("#baname").attr("class", "e_required");
		    $("#POP_BANK_CODE").attr("nullable", "no");
		    $("#BANK_CODE").attr("nullable", "no");
		    $("#BANK_ACCT_NO").attr("nullable", "no");
		    $("#SUPER_BANK_CODE").attr("nullable", "no");
		}
	},

	queryBanks: function (){
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
	},
	/**
	 * REQ201609060001_2016年下半年测试卡功能优化（二）
	 * @author zhuoyingzhi
	 * 20160930
	 * <br/>
	 * 当用户类型选择为 测试机用户时 显示 测试卡类型
	 * */
	changeTestCardType: function(){
		   var userTypeCode=$("#USER_TYPE_CODE").val();
		   var privId = "";
		   if(userTypeCode == 'A'){
			   privId = "SYS_CRM_CREATE_USERTYPE_A";
				
			   if (privId != "") {
				   $.beginPageLoading("个人用户开户-用户类型开户权限校验中...");
				   var param = "&PRIV_ID=" + privId;
				   var hasPrivFlag = "true";
				   $.ajax.submit(null, 'hasPriv', param, null, function (data) {
					   hasPrivFlag = data.get("HAS_PRIV");
					   var staffId = data.get("STAFF_ID");
					   if (hasPrivFlag == "false") {
						   alert("操作员[" + staffId +"]没有[个人用户开户-用户类型（测试机用户）开户权限]，权限编码为：[" + privId + "]");
						   $("#USER_TYPE_CODE").val("B"); // 默认用户类型：轻松卡用户
					   }
					   $.endPageLoading();
				   },function (error_code,error_info) {
					   $.MessageBox.error(error_code,error_info);
					   $.endPageLoading();
				   },{async:false});
					
				   if (hasPrivFlag == "false") {
					   return false;
				   }
			   }
			   //显示  测试卡类型
			   $('#TEST_CARD_TYPE_ID').css("display","block");
			   $('#UseInfoFieldPart').css("display",""); 
			   
			   $("#span_USE_PSPT_ID").attr("class", "e_required");
			   $("#USE_PSPT_ID").attr("nullable", "no");
			   
			   $("#span_USE").attr("class", "e_required");
			   $("#USE_CUSTNAME").attr("nullable", "no");
			   
			   $("#span_USE_PSPT_TYPE_CODE").attr("class", "e_required");
			   $("#USE_PSPT_TYPE_CODE").attr("nullable", "no");
			   
			   $("#span_USE_PSPT_ADDR").attr("class", "e_required");
			   $("#USE_PSPT_ADDR").attr("nullable", "no");
		   }else{
			   //隐藏   测试卡类型
			   $('#TEST_CARD_TYPE_ID').css("display","none");
			   
			   $("#TEST_CARD_TYPE").val("");//隐藏测试卡后，可能还需要置为空值
			   $('#UseInfoFieldPart').css("display","none");
			   
			   $("#span_USE_PSPT_ID").attr("class", "");
			   $("#USE_PSPT_ID").attr("nullable", "yes");
			   $("#USE_PSPT_ID").val("");
			   
			   $("#span_USE").attr("class", "");
			   $("#USE_CUSTNAME").attr("nullable", "yes");
			   $("#USE_CUSTNAME").val("");
			   
			   $("#span_USE_PSPT_TYPE_CODE").attr("class", "");
			   $("#USE_PSPT_TYPE_CODE").attr("nullable", "yes");
			   $("#USE_PSPT_TYPE_CODE").val("");
			   
			   $("#span_USE_PSPT_ADDR").attr("class", "");
			   $("#USE_PSPT_ADDR").attr("nullable", "yes");
			   $("#USE_PSPT_ADDR").val("");
		   }
	 }, 
	 //校验身份证
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
			}else if(psptTypeCode=="I"){
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
				if(psptId.length != 10 && psptId.length != 18){
					alert("组织机构代码证类型校验："+desc+"长度需满足10位或18位。");
					obj.val("");
					obj.focus();
					return;
				}
				if((psptId.length == 10 &&psptId.charAt(8) != "-")||(psptId.length == 18 &&psptId.charAt(16) != "-")){
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
			}else if(psptTypeCode=="0" || psptTypeCode=="1" || psptTypeCode=="2") {
				 if(fieldName == "PSPT_ID" || fieldName == "AGENT_PSPT_ID" || fieldName == "RSRV_STR4"|| fieldName=="USE_PSPT_ID"){
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
				if(fieldName == "PSPT_ID"){
					var psptvalue = this.getBirthdayByPsptId(psptId);
					//alert(psptvalue);
					$("#BIRTHDAY").val(psptvalue);
				}
			}
			if(fieldName != "PSPT_ID") {
				return;
			}
			
			if(!this.checkPsptIdForReal()) {
				obj.val("");
				obj.focus();
				return;
			}
			//this.checkRealNameLimitByPspt();
			this.verifyIdCardName(fieldName);
		},
	//扫描读取身份证信息（使用人）
	clickScanPspt3: function(){
		
		getMsgByEForm("USE_PSPT_ID","USE_CUSTNAME",null,null,null,"USE_PSPT_ADDR",null,null); 
	},
	/**
	 * REQ201707210018_关于年龄外经办人限制变更的优化
	 * @author zhuoyingzhi
	 * @date 20170925
	 * @param idCard
	 * @returns {Boolean}
	 */
	checkAge:function(idCard){
		if(!idCard){return false;}
		var _age = this.jsGetAge(idCard);
		return _age>=11 && _age<120;
	},		
	jsGetAge:function(idCard){				 
	    var returnAge;
		var bstr = idCard.substring(6,14);			 
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
                var monthDiff = nowMonth - birthMonth;//月之差
                if(monthDiff <= 0)
                {
                    returnAge = ageDiff - 1;
                }
                else
                {
                    returnAge = ageDiff ;
                }
	        }
	        else
	        {
	            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
	        }
	    }
	    return returnAge;//返回周岁年龄		    
	 }	
	
	});
	}
)();