	
	function queryAreaConNew(){
	   document.getElementById("cond_PARA_CODE1").value='';
	   queryAreaCon();
	}
    /**
     * IP直通车工作记录查询
     */
	function queryAreaCon(){
		var queryMode = $("#cond_QUERY_MODE").val();
		if(queryMode=='2'){//根据查询方式的三种不同状态控制查询条件区的显示或隐藏
			document.getElementById("phCode").innerHTML='员工号码：';
			document.getElementById("dateArea").style.display='';
			document.getElementById("cond_PARA_CODE2").nullable='no';
			document.getElementById("cond_PARA_CODE3").nullable='no';
			document.getElementById("cond_PARA_CODE1").datatype='';
			document.getElementById("cond_PARA_CODE1").equsize='8';
			document.getElementById("cond_PARA_CODE1").desc='员工号码';
		}else if(queryMode=='1'){
			document.getElementById("phCode").innerHTML='固定号码：';
			document.getElementById("dateArea").style.display='none';
			$("#cond_PARA_CODE2").val("");
			$("#cond_PARA_CODE3").val("");
			document.getElementById("cond_PARA_CODE2").nullable='yes';
			document.getElementById("cond_PARA_CODE3").nullable='yes';
			document.getElementById("cond_PARA_CODE1").datatype='numeric';
			document.getElementById("cond_PARA_CODE1").equsize='';
			document.getElementById("cond_PARA_CODE1").desc='固定号码';
		}else{
			document.getElementById("phCode").innerHTML='手机号码：';
			document.getElementById("dateArea").style.display='none';
			$("#cond_PARA_CODE2").val("");
			$("#cond_PARA_CODE3").val("");
			document.getElementById("cond_PARA_CODE2").nullable='yes';
			document.getElementById("cond_PARA_CODE3").nullable='yes';
			document.getElementById("cond_PARA_CODE1").datatype='mbphone';
			document.getElementById("cond_PARA_CODE1").equsize='';
			document.getElementById("cond_PARA_CODE1").desc='手机号码';
		}
	
	}
	
	/**
	 * 提交查询前校验
	 */
	function queryIPExpressLog(obj){
		var paraCode1 = document.getElementById("cond_PARA_CODE1").value;
		var queryMode = document.getElementById("cond_QUERY_MODE").value;
		//查询条件校验
		if (queryMode == '2') {  //员工号码
			if (paraCode1 ==""){
				alert("员工号码不能为空!");
				return false;
			}
			if (paraCode1.length !=8){
				alert("员工号码长度必须为8!");
				return false;
			}
			var startDate = document.getElementById("cond_PARA_CODE2").value;
			if (startDate == "") {
				alert("起始时间不能为空!");
				return false;
			}
			var endDate = document.getElementById("cond_PARA_CODE3").value;
			if (endDate == "") {
				alert("结束时间不能为空!");
				return false;
			}
		}else if(queryMode=='1'){
			if (paraCode1 ==""){
				alert("固定号码不能为空!");
				return false;
			}
			debugger;
			var areaCode = $.trim(paraCode1).substring(0,4);
			if (paraCode1.length !=12 || areaCode != "0898"){
				alert("请输入合法的固定号码!");
				return false;
			}
			
			
		}else{
			if (paraCode1 ==""){
				alert("手机号码不能为空!");
				return false;
			}
			if ($.verifylib.checkMbphone(paraCode1) == false || paraCode1.length !=11){
				alert("请输入合法的手机号码!");
				return false;
			}
		}
		$.ajax.submit('QueryCondPart', 'queryIPExpressLog', null, 'QueryListPart', function(data){
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}