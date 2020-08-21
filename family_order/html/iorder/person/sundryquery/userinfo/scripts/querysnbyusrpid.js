//用户资料模糊查询
function querySnByUsrpid() {
    //查询条件校验
    if (!$.validate.verifyAll("QuerySnByUsrpidPart")) {
        return false;
    }
    $.beginPageLoading("正在查询数据...");
    //用户资料模糊查询
    $.ajax.submit("QuerySnByUsrpidPart", "querySnByUsrpid", null, "QueryListPart",
        function () {
            $.endPageLoading();
        },
        function (error_code, error_info) {
            $.endPageLoading();
            MessageBox.error(error_info);
        });
}

function querySnByUsrpidMod() {
    //查询条件校验
    if (!$.validate.verifyAll("QuerySnByUsrpidPart")) {
        return false;
    }
    $.beginPageLoading("正进行金库认证...");
    $.treasury.auth("CUST_4A_querySnByUsrpidMod", function (ret) {
        $.endPageLoading();
        if (true === ret) {
            var param = "&cond_X_DATA_NOT_FUZZY=true";
            //用户资料模糊查询
            $.ajax.submit("QuerySnByUsrpidPart", "querySnByUsrpid", param, "QueryListPart",
                function () {},
                function (error_code, error_info) {
                    MessageBox.error(error_info);
                    return false;
                });
        } else {
            MessageBox.error("认证失败");
            return false;
        }
    });
}

function userQueryModeChg() {
    var queryMode = $("#cond_QUERY_MODE").val();
    if (queryMode === "1") {
        $("#pspttypecode").css("display", "none");
        $("#psptid").css("display", "none");
        $("#sn").css("display", "");
        $("#custname").css("display", "none");
        $("#cond_CUST_NAME").attr("nullable", "yes");
        $("#cond_PSPT_ID").attr("nullable", "yes");
        $("#cond_SERIAL_NUMBER").val("").attr("nullable", "no");
    } else if (queryMode === "2" || queryMode === "3") { // query by CUST_NAME
        $("#pspttypecode").css("display", "none");
        $("#psptid").css("display", "none");
        $("#sn").css("display", "none");
        $("#custname").css("display", "");
        $("#cond_CUST_NAME").attr("nullable", "no");
        $("#cond_PSPT_ID").attr("nullable", "yes");
        $("#cond_SERIAL_NUMBER").val("").attr("nullable", "yes");
    } else if (queryMode === "4" || queryMode === "5") { // query by PSPT_ID
        $("#pspttypecode").css("display", "");
        $("#psptid").css("display", "");
        $("#sn").css("display", "none");
        $("#custname").css("display", "none");
        $("#cond_CUST_NAME").attr("nullable", "yes");
        $("#cond_PSPT_ID").attr("nullable", "no");
        $("#cond_SERIAL_NUMBER").val("").attr("nullable", "yes");
    } else if (queryMode === "7") {                      // REQ201512300001 客户资料关联查询优化--业务挑刺问题
        var $psptTyepCode = $("#cond_PSPT_TYPE_CODE");
        $("#pspttypecode").css("display", "none");
        $("#psptid").css("display", "");
        $("#sn").css("display", "none");
        $("#custname").css("display", "");
        $psptTyepCode.val("");
        $psptTyepCode.attr("nullable", "yes");
        $("#cond_CUST_NAME").val("").attr("nullable", "no");
        $("#cond_PSPT_ID").val("").attr("nullable", "no");
        $("#cond_SERIAL_NUMBER").val("").attr("nullable", "yes");
    }
}

function exportBeforeAction(domId) {
    var QUERY_MODE = $("#cond_QUERY_MODE").val();
    var SERIAL_NUMBER = $("#cond_SERIAL_NUMBER").val();
    var CUST_NAME = $("#cond_CUST_NAME").val();
    var PSPT_TYPE_CODE = $("#cond_PSPT_TYPE_CODE").val();
    var PSPT_ID = $("#cond_PSPT_ID").val();

    var params = "&QUERY_MODE=" + QUERY_MODE + "&SERIAL_NUMBER=" + SERIAL_NUMBER
            + "&CUST_NAME=" + CUST_NAME + "&PSPT_TYPE_CODE=" + PSPT_TYPE_CODE
            + "&PSPT_ID=" + PSPT_ID;

    $.Export.get(domId).setParams(params);
    return true;
}

//oper: 取消：cancel；终止：terminate；状态修改中的 确定：loading；导出完成后的确定：ok；导出失败时的确定：fail；
function exportAction(oper) {
    if (oper === "cancel") {
        MessageBox.alert("点击[取消]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
    } else if (oper === "terminate") {
        MessageBox.alert("点击[终止]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
    } else if (oper === "loading") {
        MessageBox.alert("点击[加载]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
    } else if (oper === "ok") {
        MessageBox.alert("成功时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
    } else if (oper === "fail") {
        MessageBox.alert("失败时点击[确定]按钮时执行的JS方法exportAction,当前状态[" + oper + "]");
    }
    return true;
}


//下拉框
function broadquerymodechg()
{
	var broadquerymode = $("#broad_QUERY_MODE").val();
	if(broadquerymode=='1')
	{
		$("#broadpspttypecode").css("display", "");
		$("#broadpsptid").css("display", "");
		$("#broadcustname").css("display", "none");
		$("#broad_CUST_NAME").attr("nullable","yes");
		$("#broad_PSPT_ID").attr("nullable","no");
	}else if(broadquerymode=='2' ){
		$("#broad_PSPT_TYPE_CODE").val("");
		$("#broad_CUST_NAME").val("");
		$("#broad_PSPT_ID").val("");
		
		$("#broadcustname").css("display", "");
		$("#broadpsptid").css("display", "");
		$("#broadpspttypecode").css("display", "none"); 
		
		$("#broad_CUST_NAME").attr("nullable","no");
		$("#broad_PSPT_ID").attr("nullable","no");
		$("#broad_PSPT_TYPE_CODE").attr("nullable","yes");
	}
}


//宽带业务查询
function broadQuerySnByUsrpid(){
	//查询条件校验
	if(!$.validate.verifyAll("BroadQuerySnByUsrpidPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	//用户资料模糊查询
	$.ajax.submit('BroadQuerySnByUsrpidPart', 'broadQuerySnByUsrpid', null, 'BroadQueryListPart', function(data){
		var counts = data.get("counts","");
		if('0' != counts){
			$("#broadcon").css("display", "none");
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}