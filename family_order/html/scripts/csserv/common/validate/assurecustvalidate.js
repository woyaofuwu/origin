var inputPer = "";

function refreshPartAtferAuth(data){
	
	var param ="&USER_ID="+data.get("USER_INFO").get("USER_ID")+"&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val();
	
	$.ajax.submit(this, 'loadChildInfo',param , '', function(data){
		
		if(data && data.get("CODE")=='-1'){
			MessageBox.error("告警提示",data.get("MSG"),null, null, null, null);
			$("#MEM_CUST_INFO_CUST_NAME").attr("disabled",true);
			$("#MEM_CUST_INFO_PSPT_TYPE_CODE").attr("disabled",true);
			$("#MEM_CUST_INFO_PSPT_ID").attr("disabled",true);
			$("#MEM_CUST_INFO_PSPT_ADDRESS").attr("disabled",true);
			return false;
		}
		if(data && data.get("CODE")=='0'){
			$("#AgentFieldPart").show();
			$("#RsrvFieldPart").show();
		}
		
	},function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示", error_info);
    });
	
}

//证件类型
function chkPsptTypeCode(objId){

    var psptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();

    if(null == psptTypeCode || '' == psptTypeCode)
    {
        MessageBox.alert("证件类型不能为空")
        return false;
    }

    if(psptTypeCode == "E")
    {
		$("#MEM_CUST_INFO_legalperson").attr("nullable","no");
	    $("#MEM_CUST_INFO_termstartdate").attr("nullable","no");
	    $("#MEM_CUST_INFO_termenddate").attr("nullable","no");
	    $("#MEM_CUST_INFO_startdate").attr("nullable","no");
        $("#EnterprisePart").css("display", "");
	}
	else{
		$("#MEM_CUST_INFO_legalperson").attr("nullable","yes");
	    $("#MEM_CUST_INFO_termstartdate").attr("nullable","yes");
	    $("#MEM_CUST_INFO_termenddate").attr("nullable","yes");
	    $("#MEM_CUST_INFO_startdate").attr("nullable","yes");
        $("#EnterprisePart").css("display", "none");
	}

    var obj = $("#" + objId);
    var custNameObj, psptObj;
	if(psptTypeCode == "M"){

		$("#MEM_CUST_INFO_orgtype").attr("nullable","no");
	    $("#MEM_CUST_INFO_effectiveDate").attr("nullable","no");
	    $("#MEM_CUST_INFO_expirationDate").attr("nullable","no");
        $("#OrgPart").css("display", "");
	}
	else
    {
		$("#MEM_CUST_INFO_orgtype").attr("nullable","yes");
	    $("#MEM_CUST_INFO_effectiveDate").attr("nullable","yes");
	    $("#MEM_CUST_INFO_expirationDate").attr("nullable","yes");
        $("#OrgPart").css("display", "none");
	}
	
	//仅有居民身份证（含本地、外地）、军人身份证、港澳台居民居住证、外国人永居证 需 系统扫描录入信息，  其余证件手工录入
	var strPsptTypeCode1 = obj.val();
    if (objId == "MEM_CUST_INFO_PSPT_TYPE_CODE")
    {
        custNameObj = $("#MEM_CUST_INFO_CUST_NAME");
        psptObj = $("#MEM_CUST_INFO_PSPT_ID");
        chkCustName("MEM_CUST_INFO_CUST_NAME");

    }
    else if(objId == "MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE")
	{
		if(strPsptTypeCode1 == 0 || strPsptTypeCode1 == 1 || strPsptTypeCode1 == 3
				|| strPsptTypeCode1 == 'Q' || strPsptTypeCode1 == 'P')
		{
			if("0" == inputPer){
				$("#MEM_CUST_INFO_AGENT_PSPT_ID").attr("disabled",true);
			}
		}
		else
        {
			$("#MEM_CUST_INFO_AGENT_PSPT_ID").attr("disabled","");
		}
        custNameObj = $("#MEM_CUST_INFO_AGENT_CUST_NAME");
        psptObj = $("#MEM_CUST_INFO_AGENT_PSPT_ID");
        chkCustName("MEM_CUST_INFO_AGENT_CUST_NAME");

	}
	else if(objId == "MEM_CUST_INFO_RSRV_STR3")
	{
		if(strPsptTypeCode1 == 0 || strPsptTypeCode1 == 1 || strPsptTypeCode1 == 3
				|| strPsptTypeCode1 == 'Q' || strPsptTypeCode1 == 'P'){
			if("0" == inputPer){
				$("#MEM_CUST_INFO_RSRV_STR4").attr("disabled",true);
			}
		}else{
			$("#MEM_CUST_INFO_RSRV_STR4").attr("disabled","");
		}
        //责任人信息
        custNameObj = $("#MEM_CUST_INFO_RSRV_STR2");
        psptObj = $("#MEM_CUST_INFO_RSRV_STR4");
        chkCustName("MEM_CUST_INFO_RSRV_STR2");

	}

	var psptTypeCode = obj.val();
	var custName = $.trim(custNameObj.val());

	//0-本地身份证 1-外地身份证
    psptObj.attr("datatype", (psptTypeCode == 0 || psptTypeCode == 1 || psptTypeCode == 2 || psptTypeCode == 3) ? "pspt" : "");

    if (psptTypeCode == "3")
    {
        MessageBox.alert("请提醒客户同时出示军人身份证明！并进行留存");
    }

    if (custName != "" && psptTypeCode != "A") {
        if (this.includeChineseCount(custName) < 2) {
            $.validate.alerter.one($("#" + objId)[0], obj.attr("desc") + "不是护照，" + custNameObj.attr("desc") + "不能少于2个中文字符！");
            custNameObj.val("");
            return;
        }
    }

    if (objId != "MEM_CUST_INFO_PSPT_TYPE_CODE")
    {
        if (objId == "MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE")
        {
            $("#MEM_CUST_INFO_AGENT_PSPT_ID").trigger("change");
        }
        else if (objId == "MEM_CUST_INFO_RSRV_STR3")
        {
            $("#MEM_CUST_INFO_RSRV_STR4").trigger("change");
        }
        else if (objId == "MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE")
        {
            var agentValue = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
            if (agentValue == "D" || agentValue == "E" || agentValue == "G" || agentValue == "L" || agentValue == "M") {
                $.validate.alerter.one($("#" + objId)[0], "证件类型选择集团证件，经办人的证件类型只能选择个人证件，请重新选择！");
                $.custInfo.selectDefaultValue(objId);
                $("#MEM_CUST_INFO_AGENT_PSPT_ID").attr("datatype", "");
                return;
            }
            $("#MEM_CUST_INFO_AGENT_PSPT_ID").trigger("change");
        }
        return;
    }

	$("#MEM_CUST_INFO_PSPT_ID").trigger("change");

    if (psptTypeCode != "E")
    {
        //清除营业执照的附加属性
        $("#MEM_CUST_INFO_legalperson").val('');
        $("#MEM_CUST_INFO_termstartdate").val('');
        $("#MEM_CUST_INFO_termenddate").val('');
        $("#MEM_CUST_INFO_startdate").val('');
    }
    if (psptTypeCode != "M")
    {
        //清除组织机构代码证的附加属性
        $("#MEM_CUST_INFO_orgtype").val('');
        $("#MEM_CUST_INFO_effectiveDate").val('');
        $("#MEM_CUST_INFO_expirationDate").val('');
    }

	//责任人、经办人不进行一证五号
	if (objId == "MEM_CUST_INFO_PSPT_TYPE_CODE")
	{
		checkGlobalMorePsptId(objId);
	}
}


function chkCustName(fieldName)
{
	var obj = $("#" + fieldName);
    var custNameTrim = obj.val().replace(/\s+/g, "");
    obj.val(custNameTrim);
    var psptTypeCode, psptTypeDesc;
    //判断到底是客户姓名还是经办人姓名
    if (fieldName == "MEM_CUST_INFO_CUST_NAME")
    {
        //客户
        psptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
        psptTypeDesc = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").attr("desc");
    }
    else if (fieldName == "MEM_CUST_INFO_AGENT_CUST_NAME")
    {
        //经办人信息
        psptTypeCode = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
        psptTypeDesc = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").attr("desc");
    }
    else if (fieldName == "MEM_CUST_INFO_RSRV_STR2")
    {
        //责任人信息
        psptTypeCode = $("#MEM_CUST_INFO_RSRV_STR3").val();
        psptTypeDesc = $("#MEM_CUST_INFO_RSRV_STR3").attr("desc");
    }
    else if (fieldName == "MEM_CUST_INFO_USE")
    {
        //使用人信息
        psptTypeCode = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE").val();
        psptTypeDesc = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE").attr("desc");
    }
    var custName = $.trim(obj.val());
    var desc = obj.attr("desc");

    if (!custName) return;

    if (psptTypeCode == "E" || psptTypeCode == "G" || psptTypeCode == "M") {//	营业执照、组织机构代码证、事业单位法人登记证书
        if (this.includeChineseCount(custName) < 4) {
            $.validate.alerter.one(obj[0], psptTypeDesc + "不是护照," + desc + "不能少于4个中文字符！");
            obj.val("");
            obj.focus();
            return;
        }
    }

    var mainPsptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
    if (mainPsptTypeCode == "E" || mainPsptTypeCode == "G" || mainPsptTypeCode == "D" || mainPsptTypeCode == "M" || mainPsptTypeCode == "L")
    {
        //	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
        var custnametemp = trim($("#MEM_CUST_INFO_CUST_NAME").val());
        var agentnametemp = trim($("#MEM_CUST_INFO_AGENT_CUST_NAME").val());
        if (custnametemp != "" && agentnametemp != "" && custnametemp == agentnametemp) {
            $.validate.alerter.one(obj[0], "单位名称和经办人名称不能相同！");
            obj.val("");
            obj.focus();
            return;
        }
    }

    if (psptTypeCode != "A" && psptTypeCode != "D") {
        if (psptTypeCode != "E" && psptTypeCode != "G" && psptTypeCode != "D" && psptTypeCode != "M" && psptTypeCode != "L") {//	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
            var pattern = /[a-zA-Z0-9]/;
            if (pattern.test(custName)) {
                $.validate.alerter.one(obj[0], psptTypeDesc + "不是护照, " + desc + "不能包含数字和字母！");
                obj.val("");
                obj.focus();
                return;
            }
        }
        if (this.includeChineseCount(custName) < 2) {
            $.validate.alerter.one(obj[0], psptTypeDesc + "不是护照," + desc + "不能少于2个中文字符！");
            obj.val("");
            obj.focus();
            return;
        }
    }
    else if (psptTypeCode == "A")
    {
        /*护照：客户名称须大于三个字符，不能全为阿拉伯数字*/
        if (custName.length < 3 || $.toollib.isNumber(custName)) {
            $.validate.alerter.one(obj[0], psptTypeDesc + "是护照," + desc + "须大于三个字符，且不能全为阿拉伯数字！");
            obj.val("");
            obj.focus();
            return;
        }
        var specialStr = "“”‘’，《》~！@#￥%……&*（）【】｛｝；：‘’“”，。、《》？+——-=";
        for (var i = 0; i < specialStr.length; i++) {
            if (custName.indexOf(specialStr.charAt(i)) > -1) {
                $.validate.alerter.one(obj[0], obj.attr("desc") + "包含特殊字符，请检查！");
                obj.val("");
                obj.focus();
                return;
            }
        }
    }
    else if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3" ||
        psptTypeCode == "C" || psptTypeCode == "H" || psptTypeCode == "I" || psptTypeCode == "N" || psptTypeCode == "O")
    {
        var re = /^[•··.．·\d\u4e00-\u9fa5]+$/;
        if (!re.test(custName)) {
            $.validate.alerter.one(obj[0], obj.attr("desc") + "包含特殊字符，请检查！");
            obj.val("");
            obj.focus();
            return;
        }

        if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3")
        {
            this.verifyIdCard(fieldName);
        }

    }

    if (psptTypeCode == "H" || psptTypeCode == "I" || psptTypeCode == "J")
    {
        // 港澳证、台胞证
        if (custName.length < 2) {
            $.validate.alerter.one(obj[0], psptTypeDesc + "是护照," + desc + "须两个字符及以上");
            obj.val("");
            obj.focus();
            return;
        }
        var re = /^[•··.．·\d\u4e00-\u9fa5]+$/;
        if (!re.test(custName)) {
            $.validate.alerter.one(obj[0], obj.attr("desc") + "包含特殊字符，请检查！");
            obj.val("");
            obj.focus();
            return;
        }
    }

    //责任人、经办人、使用人不进行一证五号
    if (fieldName == "MEM_CUST_INFO_PSPT_TYPE_CODE")
    {
    	this.checkGlobalMorePsptId(fieldName);
    }
    verifyIdCardName(fieldName);
    checkPsptIdForReal();
}

function includeChineseCount (custName) {
    // 是否包含中文字符个数
    var count = 0;
    for (var i = 0; i < custName.length; i++) {
        if ($.toollib.isChinese(custName.charAt(i))) {
            count++;
        }
    }
    return count;
}

//营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
function verifyIdCardName(fieldName)
{
    var obj = $("#" + fieldName);
    var custNameObj, psptObj, psptcodeObj;
    if (fieldName == "MEM_CUST_INFO_PSPT_ID" || fieldName == "MEM_CUST_INFO_CUST_NAME" || fieldName=="MEM_CUST_INFO_PSPT_TYPE_CODE")
    {
        custNameObj = $("#MEM_CUST_INFO_CUST_NAME");
        psptObj = $("#MEM_CUST_INFO_PSPT_ID");
        psptcodeObj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE");
    }
    else if (fieldName == "MEM_CUST_INFO_AGENT_PSPT_ID" || fieldName == "MEM_CUST_INFO_AGENT_CUST_NAME" || fieldName=="MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE")
    {
        custNameObj = $("#MEM_CUST_INFO_AGENT_CUST_NAME");
        psptObj = $("#MEM_CUST_INFO_AGENT_PSPT_ID");
        psptcodeObj = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE");
    }
    else  if (fieldName == "MEM_CUST_INFO_RSRV_STR4" || fieldName == "MEM_CUST_INFO_RSRV_STR2" || fieldName=="MEM_CUST_INFO_RSRV_STR3")
    {
        //责任人信息
        psptcodeObj = $("#MEM_CUST_INFO_RSRV_STR3");
        custNameObj = $("#MEM_CUST_INFO_RSRV_STR2");
        psptObj = $("#MEM_CUST_INFO_RSRV_STR4");
    }

    if ( psptObj || custNameObj || psptcodeObj) {
        return false;
    }

    var psptId = $.trim(psptObj.val());
    var psptName = $.trim(custNameObj.val());
    var psptcode = $.trim(psptcodeObj.val());
    if (psptId == "" || psptName == "" || psptcode == "") {
        return false;
    }
    
    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
    
    var param = "&CERT_TYPE=" + psptcode + "&CERT_ID=" + psptId + "&CERT_NAME=" + psptName
    			+ "&SERIAL_NUMBER=" + serialNumber ;	    //+ "&TRADE_TYPE_CODE="+$("#TRADE_TYPE_CODE").val()
    
    if(psptcode=="0"||psptcode=="1"||psptcode=="2"||psptcode=="3"){//《一证多名需求》，除了本地外地身份证和户口军人身份证，其他证件都不允许一证多名
    	return false;
    } else {
        $.beginPageLoading("单位名称校验。。。");
        $.ajax.submit(null, "verifyIdCardName", param,'', function(data){
                $.endPageLoading();
                
                if (data && data.get("X_RESULTCODE") != "0") {
                    MessageBox.error("告警提示", "同一个证件号码不能对应不同的名称", null, null, null, null);
                    psptObj.val("");
                    return false;
                } else {
                    verifyEnterpriseCard();
                    verifyOrgCard();
                }
            }, function (error_code,error_info,derror) {
                $.endPageLoading();
                showDetailErrorInfo(error_code,error_info,derror);
            });
    }
}

//全网一证五号检查
function checkGlobalMorePsptId(objId){
	
    var custName = "";
    var psptId = "";
    var psptTypeCode = "";
    var clearPsptId = "";
    if(objId=="MEM_CUST_INFO_PSPT_ID"||objId=="MEM_CUST_INFO_CUST_NAME"||objId=="MEM_CUST_INFO_PSPT_TYPE_CODE"){
        custName = $("#MEM_CUST_INFO_CUST_NAME").val();
        psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
        psptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
        clearPsptId = "#MEM_CUST_INFO_PSPT_ID";
    }
  
    if(custName == "" || psptId == "" || psptTypeCode == ""){
        return false;
    }
    var offerTpye = $("#cond_OPER_TYPE").val();
    var tradeType = "00";
    if(offerTpye=='CrtUs')
    {
        tradeType = "3621";
    }
    else if (offerTpye=='ChgUs')
    {
        tradeType = "3622";
    }
    var param = "&CUST_NAME="+encodeURIComponent(custName)+"&PSPT_ID="+psptId+"&PSPT_TYPE_CODE="+psptTypeCode
               +"&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()
               +"&TRADE_TYPE_CODE="+tradeType;
    $.beginPageLoading("全网证件信息数量校验。。。");
    $.ajax.submit(null, 'checkGlobalMorePsptId', param, '', function(data){
        $.endPageLoading();
            if(data && data.get("CODE")!= "0"){
                if(data.get("CODE")== "1"){
                    $(clearPsptId).val("");
                }
                alert(data.get("MSG"));
                return false;
            }
    },
    function(error_code,error_info,derror){
        $.endPageLoading();
        $(clearPsptId).val("");
        showDetailErrorInfo(error_code,error_info,derror);
    });
}

//	营业执照在线校验
function verifyEnterpriseCard(){	
    var psptcode = $.trim($("#MEM_CUST_INFO_PSPT_TYPE_CODE").val());
    var psptId = $.trim($("#MEM_CUST_INFO_PSPT_ID").val());
    var psptName = $.trim($("#MEM_CUST_INFO_CUST_NAME").val());
    var legalperson = $.trim($("#legalperson").val());
    var termstartdate = $.trim($("#termstartdate").val());
    var termenddate = $.trim($("#termenddate").val());
    var startdate = $.trim($("#startdate").val());
    var offerTpye = $("#cond_OPER_TYPE").val();
	var tradeType = "00";
	if(offerTpye=='CrtUs'){
		tradeType = "3621";
	}else if (offerTpye=='ChgUs'){
		tradeType = "3622";
	}
   
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
    //var serialNunber = $("#AUTH_SERIAL_NUMBER").val();
    
    var param =  "&regitNo="+psptId+"&enterpriseName="+encodeURIComponent(psptName)+"&legalperson="+encodeURIComponent(legalperson)
                 +"&termstartdate="+encodeURIComponent(termstartdate)+"&termenddate="+encodeURIComponent(termenddate)+"&startdate="+encodeURIComponent(startdate)
                 +"&TRADE_TYPE_CODE="+tradeType; 
                 //+"&SERIAL_NUMBER="+serialNunber+"&TRADE_TYPE_CODE=3622"; 
    $.beginPageLoading("营业执照校验。。。");	
	$.ajax.submit(null, 'verifyEnterpriseCard', param, '', function(data){
		$.endPageLoading();
		if(data && data.get("X_RESULTCODE")!= "0"){
			$("#MEM_CUST_INFO_PSPT_ID").val('');
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
			return false;
		}else{
			return true;
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#MEM_CUST_INFO_PSPT_ID").val('');
		showDetailErrorInfo(error_code,error_info,derror);
    });	
}

//	组织机构代码在线校验
function verifyOrgCard(){
    var psptcode = $.trim($("#MEM_CUST_INFO_PSPT_TYPE_CODE").val());
    var psptId = $.trim($("#MEM_CUST_INFO_PSPT_ID").val());
    var psptName = $.trim($("#MEM_CUST_INFO_CUST_NAME").val());
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
			$("#MEM_CUST_INFO_PSPT_ID").val('');
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
			return false;
		}else{
			return true;
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#MEM_CUST_INFO_PSPT_ID").val('');
		showDetailErrorInfo(error_code,error_info,derror);
    });
    
}   

//实名制基本数据校验
function checkPsptIdForReal() {
    if ($("#MEM_CUST_INFO_PSPT_TYPE_CODE").val() == "Z") {
        $.validate.alerter.one($("#MEM_CUST_INFO_PSPT_TYPE_CODE")[0], "实名制开户，证件类型不能为其他，请重新选择！");
        return false;
    }
    var psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
    if ($.toollib.isRepeatCode(psptId)) {
        $.validate.alerter.one($("#MEM_CUST_INFO_PSPT_ID")[0], "实名制开户，证件号码过于简单，请重新输入！");
        return false;
    }
    var psptTypeCode = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();

    if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3") {
        if (!$.validate.verifyField("MEM_CUST_INFO_PSPT_ID")) {
            return false;
        }
    }
    return true;
}

function chkPsptId(fieldName)
{
    var obj = $("#" + fieldName);
    var psptTypeObj;
    if (fieldName == "MEM_CUST_INFO_AGENT_PSPT_ID")
    {
        // 经办人证件类型
        psptTypeObj = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE");

        //人像比对
        if ($("#MEM_CUST_INFO_AGENT_SCAN_TAG").val() == "0")
        {
            $("#MEM_CUST_INFO_AGENT_SCAN_TAG").val("1");
        }
        else
        {
            $("#MEM_CUST_INFO_AGENT_BACKBASE64").val("");
            $("#MEM_CUST_INFO_AGENT_FRONTBASE64").val("");
        }
    }
    else if (fieldName == "MEM_CUST_INFO_PSPT_ID")
    {
        // 客户证件类型
        psptTypeObj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE");
    }
    else if (fieldName == "MEM_CUST_INFO_RSRV_STR4")
    {
        //责任人证件类型
        psptTypeObj = $("#MEM_CUST_INFO_RSRV_STR3");
    }
    else if (fieldName == "MEM_CUST_INFO_USE_PSPT_ID")
    {
        //使用人证件类型
        psptTypeObj = $("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE");
    }

    var psptId = $.trim(obj.val());
    var desc = $.trim(obj.attr("desc"));
    var psptTypeCode = psptTypeObj.val();

    if (psptId == "") return;
    
    if ($.toollib.isRepeatCode(psptId)) {
        $.validate.alerter.one(obj[0], desc + "不能全为同一个数字，请重新输入!");
        obj.val("");
        obj.focus();
        return;
    }
    if ($.toollib.isSerialCode(psptId)) {
        $.validate.alerter.one(obj[0], "连续数字不能作为" + desc + "，请重新输入!");
        obj.val("");
        obj.focus();
        return;
    }

	//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
    if (psptTypeCode == "H") {
        if (psptId.length != 9 && psptId.length != 11) {
            $.validate.alerter.one(obj[0], "港澳居民回乡证校验：" + desc + "必须为9位或11位。");
            obj.val("");
            obj.focus();
            return;
        }
        if (!(psptId.charAt(0) == "H" || psptId.charAt(0) == "M") || !$.toollib.isNumber(psptId.substr(1))) {
            $.validate.alerter.one(obj[0], "港澳居民回乡证校验：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
            obj.val("");
            obj.focus();
            return;
        }
    } else if (psptTypeCode == "O") {
        var psptIdtemp = psptId.replace(/\s/g, '');
        if (psptIdtemp != psptId) {
            $.validate.alerter.one(obj[0], "港澳居民来往内地通行证校验：证件号码中间不能有空格。");
            obj.val("");
            obj.focus();
        }
            return;
        //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
        if (psptId.length != 9 && psptId.length != 11) {
            $.validate.alerter.one(obj[0], "港澳居民来往内地通行证校验：" + desc + "必须为9位或11位。");
            obj.val("");
            obj.focus();
            return;
        }
        if (!(psptId.charAt(0) == "H" || psptId.charAt(0) == "M") || !$.toollib.isNumber(psptId.substr(1))) {
            $.validate.alerter.one(obj[0], "港澳居民来往内地通行证校验：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
            obj.val("");
            obj.focus();
            return;
        }
    } else if (psptTypeCode == "I") {
        //台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
        if (psptId.length != 8 && psptId.length != 11) {
            $.validate.alerter.one(obj[0], "台湾居民回乡校验：" + desc + "必须为8位或11位！");
            obj.val("");
            obj.focus();
            return false;
        }
        if (psptId.length == 8) {
            if (!$.toollib.isNumber(psptId)) {
                $.validate.alerter.one(obj[0], "台湾居民回乡校验：" + desc + "为8位时，必须均为阿拉伯数字！");
                obj.val("");
                obj.focus();
                return false;
            }
        }
        if (psptId.length == 11) {
            if (!$.toollib.isNumber(psptId.substring(0, 10))) {
                $.validate.alerter.one(obj[0], "台湾居民回乡校验：：" + desc + "为11位时，前10位必须均为阿拉伯数字。");
                obj.val("");
                obj.focus();
                return false;
            }
        }
    } else if (psptTypeCode == "N") {
        //台湾居民来往大陆通行证:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
        var psptIdtemp = psptId.replace(/\s/g, '');
        if (psptIdtemp != psptId) {
            $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：证件号码中间不能有空格。");
            obj.val("");
            obj.focus();
            return;
        }
        if (psptId.substr(0, 2) != "TW" && psptId.substr(0, 4) != "LXZH") {
            if (psptId.length == 11 || psptId.length == 12) {
                if (!$.toollib.isNumber(psptId.substring(0, 10))) {
                    $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptId.length == 8) {
                if (!$.toollib.isNumber(psptId)) {
                    $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "为8位时，必须均为阿拉伯数字。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptId.length == 7) {
                if (!$.toollib.isNumber(psptId)) {
                    $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "为7位时，必须均为阿拉伯数字。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else {
                $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "格式错误");
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
                $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "前2位“TW”或 “LXZH”字符时，后面是阿拉伯数字、英文大写字母与半角“()”的组合");
                obj.val("");
                obj.focus();
                return;
            }
        }
    } else if (psptTypeCode == "C" || psptTypeCode == "A") {
        var psptIdtemp = psptId.replace(/\s/g, '');
        if (psptIdtemp != psptId) {
            $.validate.alerter.one(obj[0], "证件号码中间不能有空格。");
            obj.val("");
            obj.focus();
            return;
        }
        //军官证、警官证、护照：证件号码须大于等于6位字符
        if (psptId.length < 6) {
            var tmpName = psptTypeCode == "A" ? "护照校验：" : "军官证类型校验：";
            $.validate.alerter.one(obj[0], tmpName + desc + "须大于等于6位字符!");
            obj.val("");
            obj.focus();
            return;
        }
    } else if (psptTypeCode == "E") {
        //营业执照：证件号码长度需满足15位 20151022 REQ201510140003 营业执照证件规则调整 CHENXY3
        if (psptId.length != 13 && psptId.length != 15 && psptId.length != 18 && psptId.length != 20 && psptId.length != 22 && psptId.length != 24) {
            $.validate.alerter.one(obj[0], "营业执照类型校验：" + desc + "长度需满足13位、15位、18位、20位、22位或24位！当前：" + psptId.length + "位。");
            obj.val("");
            obj.focus();
            return;
        }
    } else if (psptTypeCode == "M") {
        //组织机构代码校验
        if (psptId.length != 10 && psptId.length != 18) {
            $.validate.alerter.one(obj[0], "组织机构代码证类型校验：" + desc + "长度需满足10位或18位。");
            obj.val("");
            obj.focus();
            return;
        }
        if (psptId.length == 10 && psptId.charAt(8) != "-") {
            $.validate.alerter.one(obj[0], "组织机构代码证类型校验：" + desc + "规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
            obj.val("");
            obj.focus();
            return;
        }
    } else if (psptTypeCode == "G") {
        //事业单位法人登记证书：证件号码长度需满足12位
        if (psptId.length != 12 && psptId.length != 18) {
            $.validate.alerter.one(obj[0], "事业单位法人登记证书类型校验：" + desc + "长度需满足12位或者18位。");
            obj.val("");
            obj.focus();
            return;
        }
    } else if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3") {
        if (psptId.substr(psptId.length - 1, psptId.length) == "x")
        {
            obj.val(psptId.substr(0, psptId.length - 1) + "X");
        }            
        
		//身份证相关检查
        obj.attr("datatype", "pspt");
        if (!$.validate.verifyField(obj[0])) {
            obj.val("");
            obj.focus();
            return;
        } else {
            obj.attr("datatype", "");
        }
        
		var area = {11:"\u5317\u4eac", 12:"\u5929\u6d25", 13:"\u6cb3\u5317", 14:"\u5c71\u897f", 15:"\u5185\u8499\u53e4", 21:"\u8fbd\u5b81", 22:"\u5409\u6797", 23:"\u9ed1\u9f99\u6c5f", 31:"\u4e0a\u6d77", 32:"\u6c5f\u82cf", 33:"\u6d59\u6c5f", 34:"\u5b89\u5fbd", 35:"\u798f\u5efa", 36:"\u6c5f\u897f", 37:"\u5c71\u4e1c", 41:"\u6cb3\u5357", 42:"\u6e56\u5317", 43:"\u6e56\u5357", 44:"\u5e7f\u4e1c", 45:"\u5e7f\u897f", 46:"\u6d77\u5357", 50:"\u91cd\u5e86", 51:"\u56db\u5ddd", 52:"\u8d35\u5dde", 53:"\u4e91\u5357", 54:"\u897f\u85cf", 61:"\u9655\u897f", 62:"\u7518\u8083", 63:"\u9752\u6d77", 64:"\u5b81\u590f", 65:"\u65b0\u7586", 71:"\u53f0\u6e7e", 81:"\u9999\u6e2f", 82:"\u6fb3\u95e8", 91:"\u56fd\u5916"};
        psptId = psptId.toUpperCase();
        if (area[parseInt(psptId.substr(0, 2))] == null) {
            $.validate.alerter.one(obj[0], "\u8eab\u4efd\u8bc1\u53f7\u7801\u4e0d\u6b63\u786e\u0028\u5730\u533a\u975e\u6cd5\u0029\uff01");
            obj.val("");
            obj.focus();
            return;
        }
        if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(psptId))) {
            $.validate.alerter.one(obj[0], "\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u957f\u5ea6\u4e0d\u5bf9\uff0c\u6216\u8005\u53f7\u7801\u4e0d\u7b26\u5408\u89c4\u5b9a\uff01\n15\u4f4d\u53f7\u7801\u5e94\u5168\u4e3a\u6570\u5b57\uff0c18\u4f4d\u53f7\u7801\u672b\u4f4d\u53ef\u4ee5\u4e3a\u6570\u5b57\u6216X\u3002 ");
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
        
        var compareyear = "" ;
        if (len == 15) {
        	compareyear = "19"+arrSplit[2];
        }else if (len == 18) {
        	compareyear = arrSplit[2];
        }               	

        var bGoodDay = (dtmBirth.getFullYear() == Number (compareyear) ) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
        if (!bGoodDay) {
            $.validate.alerter.one(obj[0], "\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u51fa\u751f\u65e5\u671f\u4e0d\u5bf9\uff01");
            obj.val("");
            obj.focus();
            return;
        }
        
        if (len == 18) {
            /*每位加权因子*/
            var powers = new Array("7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2");
            /*第18位校检码*/
            var parityBit = new Array("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2");
            var psptBit = psptId.charAt(17).toUpperCase();
            var id17 = psptId.substring(0, 17);
            /*加权 */
            var power = 0;
            for (var i = 0; i < 17; i++)
            {
                power += parseInt(id17.charAt(i), 10) * parseInt(powers[i]);
            }
            /*取模*/
            var mod = power % 11;
            var checkBit = parityBit[mod];
            if (psptBit != checkBit)
            {
                $.validate.alerter.one(obj[0], "身份证号码不合法");
                obj.val("");
                obj.focus();
                return;
            }

            var bit11 = psptId.substring(10, 11);
            var bit13 = psptId.substring(12, 13);
            if (bit11 != "0" && bit11 != "1") {
                $.validate.alerter.one(obj[0], "18位身份证号码11位必须为0或者1");
                obj.val("");
                obj.focus();
                return;
            }

            if (parseInt(bit13) > 3) {
                $.validate.alerter.one(obj[0], "18位身份证号码13位必须小于等于3");
                obj.val("");
                obj.focus();
                return;
            }
        }

        if (fieldName == "MEM_CUST_INFO_AGENT_PSPT_ID" || fieldName == "MEM_CUST_INFO_RSRV_STR4")
        {
            if (trim(psptId) != "")
            {
                var cust_age = this.jsGetAgeNew($.trim(psptId));

                //经办人、使用人、年龄范围必须在16岁（含）至120岁（不含）之间
                if(!this.checkAge($("#MEM_CUST_INFO_PSPT_ID").val())){
                    if(cust_age < 16)
                    {
                        $.validate.alerter.one(obj[0], "年龄范围必须在16-120岁之间");
                        obj.val("");
                        obj.focus();
                        return false;
                    }
                }else if ( 120 <= cust_age)
                {
                    $.validate.alerter.one(obj[0], "年龄范围必须在16-120岁之间");
                    obj.val("");
                    obj.focus();
                    return;
                }
            }
        }                
        
        this.verifyIdCard(fieldName);
    }
    
    this.verifyIdCardName(fieldName);

    //责任人、经办人不进行一证五号
    if (fieldName == "MEM_CUST_INFO_PSPT_ID") {
    	 this.checkGlobalMorePsptId(fieldName);
    }
}

// 身份证在线校验
function verifyIdCard(fieldName){	
	var custNameObj,psptObj,psptTypeObj;
	if(fieldName == "MEM_CUST_INFO_PSPT_ID"||fieldName=="MEM_CUST_INFO_CUST_NAME"){
		custNameObj = $("#MEM_CUST_INFO_CUST_NAME");
		psptObj = $("#MEM_CUST_INFO_PSPT_ID");
		psptTypeObj = $("#MEM_CUST_INFO_PSPT_TYPE_CODE"); 
	}else if(fieldName=="MEM_CUST_INFO_AGENT_PSPT_ID"||fieldName=="MEM_CUST_INFO_AGENT_CUST_NAME"){
		custNameObj = $("#MEM_CUST_INFO_AGENT_CUST_NAME");
		psptObj = $("#MEM_CUST_INFO_AGENT_PSPT_ID");
		psptTypeObj = $("MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE"); 		 
	}else if(fieldName=="MEM_CUST_INFO_RSRV_STR4"||fieldName=="MEM_CUST_INFO_RSRV_STR2"){ //责任人信息
		custNameObj = $("#MEM_CUST_INFO_RSRV_STR2");
		psptObj = $("#MEM_CUST_INFO_RSRV_STR4");
		psptTypeObj = $("#MEM_CUST_INFO_RSRV_STR3"); 
	}
	var psptId = $.trim(psptObj.val());
	var custName = $.trim(custNameObj.val());
    if(custName == "" || psptId == ""){
        return false;
    }
    
    var offerTpye = $("#cond_OPER_TYPE").val();
	var tradeType = "00";
	if(offerTpye=='CrtUs'){
		tradeType = "3621";
	}else if (offerTpye=='ChgUs'){
		tradeType = "3622";
	}
	
	var param =  "&CERT_ID="+psptId+"&CERT_TYPE="+$.trim(psptTypeObj.val())+"&CERT_NAME="+custName
					//+"&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER")
					+"&TRADE_TYPE_CODE="+tradeType;
	$.ajax.submit(null, 'verifyIdCard', param, '', function(data){
		$.endPageLoading();
		if(data && data.get("X_RESULTCODE")== "1")
		{
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);	
			psptObj.val("");
			return false;
		}
		else if(data && data.get("X_RESULTCODE")== "2")
		{
			if(fieldName=="MEM_CUST_INFO_RSRV_STR4"||fieldName=="MEM_CUST_INFO_RSRV_STR2")
			{
				MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
				psptObj.val("");
				return false;
			}
			else if(fieldName=="MEM_CUST_INFO_AGENT_PSPT_ID"||fieldName=="MEM_CUST_INFO_AGENT_CUST_NAME")
			{
				MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
				psptObj.val("");
				return false;
			}
			MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
			return true;
		}else{
			return true;
		}
	}, function (code, info, detail) {
        $.endPageLoading();
        MessageBox.error("错误提示", "校验获取后台数据错误！", null, null, info, detail);
    });
    
}

function jsGetAgeNew(idCard){				 
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
        }
        else
        {
            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
        }
    }
    return returnAge;//返回周岁年龄		    
}

function checkAge(idCard){
	if(!idCard){return false;}
	var _age = this.jsGetAgeNew(idCard);			
	
	//REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
	return _age>=16 && _age<=120;
}

function checkAddr(objId)
{
	var custAddrObj =  $("#"+objId);
	var zjNum = this.trimAll(custAddrObj.val()).replace(/[^\x00-\xff]/g, "**").length ; //字节数
	if(custAddrObj.val()=="" || zjNum*1<12){
		 alert(custAddrObj.attr("desc")+'栏录入文字需不少于6个汉字!');
		 custAddrObj.val('');
		 custAddrObj.focus();
		 return false;
	}
	 
	 if(!isNaN(custAddrObj.val())&& custAddrObj.val() != ''){
		 alert(custAddrObj.attr("desc")+'栏不能全部为数字');
		 custAddrObj.val('');
		 custAddrObj.focus();
		 return false;
	 }
	 return true;
}

function trimAll(str)
{
	return str.replace(/(^\s+)|(\s+$)/g,"").replace(/\s/g,"");
}


//扫描读取身份证信息
function clickScanPspt1(){
	if ($.os.phone) {
		 try{
			 $.MBOP.getMsgByEForm("MEM_CUST_INFO_PSPT_ID");
         }catch (e) {
				alert(e);
		}
	} else {

		getMsgByEForm("MEM_CUST_INFO_PSPT_ID", "MEM_CUST_INFO_CUST_NAME", null, null, null,
            "MEM_CUST_INFO_PSPT_ADDRESS", null, null);
	}

    this.verifyIdCard("MEM_CUST_INFO_PSPT_ID");
    this.verifyIdCardName("MEM_CUST_INFO_PSPT_ID");

}

//扫描读取身份证信息(经办人)
function clickScanPspt2(){
	if ($.os.phone) {
		 try{
			 $.MBOP.getMsgByEForm("MEM_CUST_INFO_AGENT_PSPT_ID"); //
         }catch (e) {
				alert(e);
		}
	} else {

		getMsgByEForm("MEM_CUST_INFO_AGENT_PSPT_ID", "MEM_CUST_INFO_AGENT_CUST_NAME", null, null, null,"MEM_CUST_INFO_AGENT_PSPT_ADDR", null, null);
	}

    this.verifyIdCard("MEM_CUST_INFO_AGENT_PSPT_ID");
    this.verifyIdCardName("MEM_CUST_INFO_AGENT_PSPT_ID");

}

//扫描读取身份证信息（责任人）
function clickScanPspt3(){
	
	if ($.os.phone) {
		 try{
			 $.MBOP.getMsgByEForm("MEM_CUST_INFO_RSRV_STR4");
         }catch (e) {
				alert(e);
		}
	}else {
		getMsgByEForm("MEM_CUST_INFO_RSRV_STR4","MEM_CUST_INFO_RSRV_STR2",null,null,null,"MEM_CUST_INFO_RSRV_STR5",null,null);
	}

    this.verifyIdCard("MEM_CUST_INFO_RSRV_STR4");
    this.verifyIdCardName("MEM_CUST_INFO_RSRV_STR4");

}

//扫描读取身份证信息（使用人）
function clickScanPspt4(){
	
	if ($.os.phone) {
		 try{
			 $.MBOP.getMsgByEForm("MEM_CUST_INFO_USE_PSPT_ID");
         }catch (e) {
				alert(e);
		}
	}else {
		getMsgByEForm("MEM_CUST_INFO_USE_PSPT_ID","MEM_CUST_INFO_USE",null,null,null,"MEM_CUST_INFO_USE_PSPT_ADDR",null,null);
		this.verifyIdCard("MEM_CUST_INFO_USE_PSPT_ID");
		this.verifyIdCardName("MEM_CUST_INFO_USE_PSPT_ID");
	}
}


function identification(picid,picstream){
	var toDay=new Date();
	var toMonth=(toDay.getMonth()+1).toString();
	if(toMonth.length==1){
		toMonth="0"+toMonth;
	}
	var thisDay=toDay.getDate().toString();
	if(thisDay.length==1){
		thisDay="0"+thisDay;
	} 
	var op_time=toDay.getFullYear().toString()+"-"+toMonth+"-"+thisDay
				+" "+toDay.getHours().toString()+":"+toDay.getMinutes().toString()+":"+toDay.getSeconds().toString();
	
	var offerTpye = $("#cond_OPER_TYPE").val();

	var tradeType = "00";
	if(offerTpye=='CrtUs'){
		tradeType = "3621";
	}else if (offerTpye=='ChgUs'){
		tradeType = "3622";
	}
	var op_code = tradeType;
	var op_code_name = "";
	if(op_code == "3622" || op_code == "3621"){
		op_code_name = "集团商务宽带业务办理";
	}

    var custName,psptId,psptType,fornt;
	if(picid = "MEM_CUST_INFO_PIC_ID")
	{
        psptId = $("#MEM_CUST_INFO_PSPT_ID").val();
        custName = $("#MEM_CUST_INFO_CUST_NAME").val();
        psptType = $("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
        fornt = $("#custInfo_AGENT_FRONTBASE64").val();

        if( psptType == ""){
            $.TipBox.show(document.getElementById('MEM_CUST_INFO_PSPT_TYPE_CODE'), "请选择证件类型!", "red");
            return false;
        }
        if( psptId == ""){
            $.TipBox.show(document.getElementById('MEM_CUST_INFO_PSPT_ID'), "请输入证件号码!", "red");
            return false;
        }
        if(custName == ""){
            $.TipBox.show(document.getElementById('MEM_CUST_INFO_CUST_NAME'), "请输入客户名称!", "red");
            return false;
        }

        if ($.os.phone) {
            try{
                $.MBOP.shotImage("MEM_CUST_INFO_PIC");
                return true;
            }catch (e) {
                alert(e);
            }
        }
    }
	else if(picid = "MEM_CUST_INFO_AGENT_PSPT_ID")
	{
        custName = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
        psptId = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
        psptType = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
        fornt = $("#custInfo_AGENT_FRONTBASE64").val();

        if( psptType == ""){
            $.TipBox.show(document.getElementById('MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE'), "请选择经办人证件类型!", "red");
            return false;
        }
        if( psptId == ""){
            $.TipBox.show(document.getElementById('MEM_CUST_INFO_AGENT_PSPT_ID'), "请输入经办人证件号码!", "red");
            return false;
        }
        if(custName == ""){
            $.TipBox.show(document.getElementById('MEM_CUST_INFO_AGENT_CUST_NAME'), "请输入经办人姓名!", "red");
            return false;
        }

        if ($.os.phone) { // 手机移动端人像比对
            try{
                $.MBOP.shotImage("MEM_CUST_INFO_AGENT");
                return true;
            }catch (e) {
                alert(e);
            }
        }
    }


	var bossOriginalXml = [];
	bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
	bossOriginalXml.push('<req>');
	bossOriginalXml.push('	<billid>'+'</billid>');
	bossOriginalXml.push('	<brand_name>'+'</brand_name>');
	bossOriginalXml.push('	<brand_code>'+'</brand_code>');
	bossOriginalXml.push('	<work_name>'+'</work_name>');
	bossOriginalXml.push('	<work_no>' + '</work_no>');
	bossOriginalXml.push('	<org_info>'+'</org_info>');
	bossOriginalXml.push('	<org_name>'+'</org_name>');
	bossOriginalXml.push('	<phone>'+'</phone>');
	bossOriginalXml.push('	<serv_id>'+'</serv_id>');
	bossOriginalXml.push('	<op_time>' + op_time + '</op_time>');
	bossOriginalXml.push('	<op_code>' + op_code + '</op_code>');
    bossOriginalXml.push('	<op_code_name>' + op_code_name + '</op_code_name>');
	
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
		MessageBox.success("提示信息", "人像摄像成功");
	}else{
		MessageBox.error("提示信息", "人像摄像失败");
		return false;
	}	
	//获取照片流
	var picStream = makeActiveX.IdentificationInfo.pic_stream;
	picStream = escape (encodeURIComponent(picStream));
	if("0" == result){
		$("#"+picid).val(picID);
		$("#"+picstream).val(picStream);
		
		var param = "&BLACK_TRADE_TYPE="+tradeType;			
		param += "&CERT_ID="+psptId;
		param += "&CERT_NAME="+custName;
		param += "&CERT_TYPE="+psptType;
		param += "&PIC_STREAM="+picStream+"&FRONTBASE64="+escape (encodeURIComponent(fornt));			
		//param += "&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
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



addidtype();
function addidtype()
{
	//校验手工输入证件号码权限
	$.ajax.submit(null, 'getInput', '', '', function(data){
		console.log(data);
		$.endPageLoading();
		if(data && data.get("X_RESULTCODE") == "00"){
			$("#MEM_CUST_INFO_AGENT_PSPT_ID").attr("disabled","");
			$("#MEM_CUST_INFO_RSRV_STR4").attr("disabled","");
			inputPer="1";
		}else{
			$("#MEM_CUST_INFO_AGENT_PSPT_ID").attr("disabled",true);
			$("#MEM_CUST_INFO_RSRV_STR4").attr("disabled",true);
			inputPer="0";
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$(clearPsptId).val("");
		showDetailErrorInfo(error_code,error_info,derror);
  });
}



