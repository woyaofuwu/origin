function initPageParam() {
	debugger;
	var operType = $("#cond_OPER_TYPE").val();
	if(operType=="20"){//开通
		//专线快速开通
		var ifChooseConfcrm = $("#pattr_IF_CHOOSE_CONFCRM").val();//是否选择勘察单
		var newOrModify = $("#OFFER_CHA_HIDDEN_ID").val(); 
		if (ifChooseConfcrm == '0' && newOrModify.match("new")) {// 不选择勘察单并且是新增
			$("#pattr_LINEOPENTAG").val("1");
			$("#pattr_ISCOVER").val("Y");
		 	$("#pattr_coverType").val("PON快开");
			
			// 新增专线初始化获得专线实例号
			ajaxSubmit(this, 'getProductNo', null, null, function(data) {
				$("#NOTIN_LINE_NO").val(data.get("LINE_NO"));
				$("#NOTIN_RSRV_STR9").val(data.get("RSRV_STR9"));
				$("#pattr_TRADEID").val(data.get("LINE_NO"));
				$("#pattr_PRODUCTNO").val(data.get("RSRV_STR9"));
				$("#TRADEID").val(data.get("LINE_NO"));
				$("#PRODUCTNO").val(data.get("RSRV_STR9"));
				var productId = $("#cond_OFFER_CODE").val();
				if (productId == "7010") {
					$("#pattr_TRANSFERMODE").val("PTN");
				}
			}, null);
		} 
		//专线开通(综合资管)
		else if (ifChooseConfcrm == '1') {// 是
			$("#pattr_LINEOPENTAG").val("0");
			$("#pattr_ISCOVER").val("N");
			
			$("#quickAddrA").css("display", "none");
			$("#quickAddrZ").css("display", "none");
			$("#pattr_QUICKADDRA").attr("nullable", "yes");
			$("#pattr_QUICKADDRZ").attr("nullable", "yes");
		}
		//开通勘察单
		else{
			var newOrModify = $("#OFFER_CHA_HIDDEN_ID").val(); 
			if(newOrModify.match("new")){
				ajaxSubmit(this,'getProductNo',null,null,function(data){
					//资源确认(综合资管)
					$("#pattr_TRADEID").val(data.get("LINE_NO"));	
					$("#pattr_PRODUCTNO").val(data.get("RSRV_STR9"));	
					var productId = $("#cond_OFFER_CODE").val(); 
					if(productId=="7010"){
						$("#pattr_TRANSFERMODE").val("PTN");
					}
				},null);
			}
		}
	}
	setTimeout(function (){
		if(document.getElementById("pattr_CITYA")!=null &&
				document.getElementById("pattr_CITYA").value!=''){
			var areaVal=$("#pattr_AREAA").val();
			var areaText=$("#pattr_AREAA_span span").text();

			changeAreaAByCityA();
			$("#pattr_AREAA").val(areaVal);
			$("#pattr_AREAA_span span").text(areaText);
		}
		if(document.getElementById("pattr_CITYZ")!=null
				&&document.getElementById("pattr_CITYZ").value!=''){
			var areaVal=$("#pattr_AREAZ").val();
			var areaText=$("#pattr_AREAZ_span span").text();
			changeAreaZByCityZ();
			$("#pattr_AREAZ").val(areaVal);
			$("#pattr_AREAZ_span span").text(areaText);			

		}
	}, 1000);
	
//	$("#batchAddImportZHZG").beforeAction(function(e){
//		return confirm('是否导入?');
//	});
//	
//	$("#batchAddImportZHZG").afterAction(function(e, status){
//		debugger;
//		if('ok' == status){
//			$.beginPageLoading("正在处理...");
//			ajaxSubmit('','batchImportFileZHZG',null,null,function(datas){
//				
//				var offerCode = $("#cond_OFFER_CODE").val();
//				var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
//				
//				var offerchaList = new Wade.DatasetList($("#offerchalist").val());
//				var temp = new Wade.DatasetList();
//				for (var i = 0; i < offerchaList.length; i++) {
//					var offercha = offerchaList[i];
//					temp.add(offercha);
//				}
//				for(var j=0;j<datas.length;j++){
//					var batchImports = new Wade.DatasetList();
//					var data = datas.get(j);
//					loadBatchDataLineParamList(data);
//					var key = data.keys;
//					for(var m=0;m<key.length;m++){
//						var value = data.get(m);
//						var batchImport = new Wade.DataMap();
//						batchImport.put("ATTR_CODE", key[m]);
//						batchImport.put("ATTR_VALUE", value);
//						batchImport.put("ATTR_NAME", $("input[name='"+key[m]+"']").attr("desc"));
//						batchImports.add(batchImport);
//					}
//
//					var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
//					$("#"+offerChaHiddenId).val(batchImports);
//					maxIndex++;
//					$("#DATALINE_MAX_INDEX").val(maxIndex);
//					temp.add(batchImports);
//				}
//				
//				$("#offerchalist").val(temp);
//				
//				$.endPageLoading();
//				MessageBox.success("导入成功！", "页面展示导入数据!",function(){
//					flag="true";
//				});
//			},function(error_code,error_info,derror){
//				$.endPageLoading();
//				showDetailErrorInfo(error_code,error_info,derror);
//			});
//		}		
//	});
//	
//	$("#batchAddImport").beforeAction(function(e){
//		return confirm('是否导入?');
//	});
//	
//	$("#batchAddImport").afterAction(function(e, status){
//		debugger;
//		if('ok' == status){
//			$.beginPageLoading("正在处理...");
//			ajaxSubmit('','batchImportFile',null,null,function(datas){
//				
//				var offerCode = $("#cond_OFFER_CODE").val();
//				var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
//				
//				var offerchaList = new Wade.DatasetList($("#offerchalist").val());
//				var temp = new Wade.DatasetList();
//				for (var i = 0; i < offerchaList.length; i++) {
//					var offercha = offerchaList[i];
//					temp.add(offercha);
//				}
//				for(var j=0;j<datas.length;j++){
//					var batchImports = new Wade.DatasetList();
//					var data = datas.get(j);
//					loadBatchDataLineParamList(data);
//					var key = data.keys;
//					for(var m=0;m<key.length;m++){
//						var value = data.get(m);
//						var batchImport = new Wade.DataMap();
//						if (key[m] == "NOTIN_RSRV_STR1") {
//							var bandWidth = new Wade.DataMap();
//							bandWidth.put("ATTR_CODE", "pattr_BANDWIDTH");
//							bandWidth.put("ATTR_VALUE", value);
//							batchImports.add(bandWidth);
//						}
//						batchImport.put("ATTR_CODE", key[m]);
//						batchImport.put("ATTR_VALUE", value);
//						batchImports.add(batchImport);
//					}
//
//					var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
//					$("#"+offerChaHiddenId).val(batchImports);
//					maxIndex++;
//					$("#DATALINE_MAX_INDEX").val(maxIndex);
//					temp.add(batchImports);
//				}
//				
//				$("#offerchalist").val(temp);
//				
//				$.endPageLoading();
//				MessageBox.success("导入成功！", "页面展示导入数据!",function(){
//					flag="true";
//				});
//			},function(error_code,error_info,derror){
//				$.endPageLoading();
//				showDetailErrorInfo(error_code,error_info,derror);
//			});
//		}		
//	});
} 

//展现快速开通地址查询
function showLineAddr(obj){
	var serviceType = $("#pattr_SERVICETYPE").val(); 
	if(serviceType==""){
		MessageBox.alert("提示", "请先在业务区域选择专线类型！");
	}else{
		$("#ADDR_PORT").val(obj); 
		showPopup("popup04", "queryLineAddrPopupItem", true);
	}
}

//快速开通地址查询
function queryLineAddr(){
	var addrStandard = $("#cond_addrStandard").val(); 
	
	var city = $("#cond_CITY").val(); 
	var area = $("#cond_AREA").val(); 
	if(city==null||city==""||undefined==city){
		$.validate.alerter.one($("#cond_CITY")[0],"您未选择地市，请选择地市进行查询!");
		return false;
	}
	
	var param = "&cond_addrStandard="+addrStandard+"&cond_CITY="+city+"&cond_AREA="+area;
	ajaxSubmit(this,'queryLineAddr',param,"LineAddrResultPart",null,null);
}

//业务保障等级说明
function showExplian(){
	showPopup("popup06", "queryshowExplianPopup", true);
}

function sureLineAddrParamList(rowData){
	var addrPort = $("#ADDR_PORT").val(); 
	var addrStandard = rowData.get("addrStandard");
	var provinceA = rowData.get("PROVINCEA");
	var cityA = rowData.get("CITYA");
	var areaA = rowData.get("AREAA");
	var countyA = rowData.get("COUNTYA");
	var villageA = rowData.get("VILLAGEA");
	var addr = addrStandard + "," + provinceA + "," + cityA + "," + areaA + "," + countyA + "," + villageA;
	var serviceType = $("#pattr_SERVICETYPE").val(); 
	if(addrPort=="A"){
		if(serviceType!=""){
			if(serviceType=="0"){//本地市传输
				if(cityA!=$("#pattr_CITYZ").val() && $("#pattr_CITYZ").val()!=""){
					MessageBox.alert("错误", "专线类型为本地市传输专线，城市A和城市Z必须为同一地市，请修改！");
	    			return false;
				}
			}else if(serviceType=="1"){//跨地市传输
				if(cityA==$("#pattr_CITYZ").val() && $("#pattr_CITYZ").val()!=""){
					MessageBox.alert("错误", "专线类型为跨地市传输专线，城市A和城市Z为不同地市，请修改！");
					return false;
				}
			}
		}
		
		$("#pattr_QUICKADDRA").val(addr); 
		$("#pattr_PROVINCEA").val(provinceA); 
		$("#pattr_AREAA").val(areaA); 
		$("#pattr_CITYA").val(cityA); 
		$("#pattr_COUNTYA").val(countyA); 
		$("#pattr_VILLAGEA").val(villageA);
	}
	if(addrPort=="Z"){
		if(serviceType!=""){
			if(serviceType=="0"){//本地市传输
				if($("#pattr_CITYA").val()!=cityA && $("#pattr_CITYA").val()!=""){
					MessageBox.alert("错误", "专线类型为本地市传输专线，城市A和城市Z必须为同一地市，请修改！");
	    			return false;
				}
			}else if(serviceType=="1"){//跨地市传输
				if($("#pattr_CITYA").val()==cityA && $("#pattr_CITYA").val()!=""){
					MessageBox.alert("错误", "专线类型为跨地市传输专线，城市A和城市Z为不同地市，请修改！");
					return false;
				}
			}
		}
		
		$("#pattr_QUICKADDRZ").val(addr); 
		$("#pattr_PROVINCEZ").val(provinceA); 
		$("#pattr_AREAZ").val(areaA); 
		$("#pattr_CITYZ").val(cityA); 
		$("#pattr_COUNTYZ").val(countyA); 
		$("#pattr_VILLAGEZ").val(villageA);
	}
	return true;
}
//校验Ip地址类型
function ipvCheck(){
	debugger;
	var ipType =$("#pattr_IPTYPE").val();
	if("IPV4和IPV6"==ipType){
		$("#pattr_CUSAPPSERVIPADDNUM").val("0");
		$("#pattr_CUSAPPSERVIPV4ADDNUM").val("");
		$("#pattr_CUSAPPSERVIPV6ADDNUM").val("");
	}else{
		$("#pattr_CUSAPPSERVIPV4ADDNUM").val("0");
		$("#pattr_CUSAPPSERVIPV6ADDNUM").val("0");
		$("#pattr_CUSAPPSERVIPADDNUM").val("");
	}
}

//校验Ip地址类型
function ipvAddNum(){
	debugger;
	var ipType =$("#pattr_IPTYPE").val();
	var ipvAddNum = $("#pattr_CUSAPPSERVIPADDNUM").val();
	if("IPV4和IPV6"==ipType&&"0"!=ipvAddNum){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPADDNUM")[0], "IP地址类型为IPV4和IPV6时,客户申请公网IP数只能为【0】!\n");
		$("#pattr_CUSAPPSERVIPADDNUM").val("0");
		  return false;
	}
	
}

//校验Ip地址类型
function ipvAddNum4(){
	debugger;
	var ipType =$("#pattr_IPTYPE").val();
	var ipvAddNum4 = $("#pattr_CUSAPPSERVIPV4ADDNUM").val();
	if("IPV4"==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV4ADDNUM")[0], "IP地址类型为IPV4时,客户申请公网IPV4数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV4ADDNUM").val("0");
		 return false;
	}
	if("IPV6"==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV4ADDNUM")[0], "IP地址类型为IPV6时,客户申请公网IPV4数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV4ADDNUM").val("0");
		return false;
	} 
	if("IPV4或IPV6"==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV4ADDNUM")[0], "IP地址类型为IPV4或IPV6时,客户申请公网IPV4数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV4ADDNUM").val("0");
		return false;
	}
	if(""==ipType&&"0"!=ipvAddNum4){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV4ADDNUM")[0], "IP地址类型为'',客户申请公网IPV4数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV4ADDNUM").val("0");
		return false;
	}	
}

//校验Ip地址类型
function ipvAddNum6(){
	debugger;
	var ipType =$("#pattr_IPTYPE").val();
	var ipvAddNum6 = $("#pattr_CUSAPPSERVIPV6ADDNUM").val();
	if("IPV4"==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV6ADDNUM")[0], "IP地址类型为IPV4时,客户申请公网IPV6数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV6ADDNUM").val("0");
		return false;
	} 
	if("IPV6"==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV6ADDNUM")[0], "IP地址类型为IPV6时,客户申请公网IPV6数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV6ADDNUM").val("0");
		return false;
	} 
	if("IPV4或IPV6"==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV6ADDNUM")[0], "IP地址类型为IPV4或IPV6时,客户申请公网IPV6数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV6ADDNUM").val("0");
		return false;
	}
	if(""==ipType&&"0"!=ipvAddNum6){
		 $.validate.alerter.one($("#pattr_CUSAPPSERVIPV6ADDNUM")[0], "IP地址类型为'',客户申请公网IPV6数只能为【0】!\n");
		 $("#pattr_CUSAPPSERVIPV6ADDNUM").val("0");
		return false;
	}			
}
//提交
function checkSub(obj)
{
	debugger;
	var bandWidth = $("#pattr_BANDWIDTH").val();
	$("#NOTIN_RSRV_STR1").val(bandWidth);
	
	if(!AZaddr()){
		return false; 
	}
	
	if(!modifyBandWith()){
		return false; 
	}
	
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}

//数据专线判断A、Z端详细安装地址是否相同
function AZaddr(){
	var productId = $("#cond_OFFER_CODE").val();
	if(productId=='7012'||productId=='70121'||productId=='70122'){
		var provinceA = $("#pattr_PROVINCEA").val();
		var cityA = $("#pattr_CITYA").val();
		var areaA = $("#pattr_AREAA").val();
		var countyA = $("#pattr_COUNTYA").val();
		var villageA = $("#pattr_VILLAGEA").val();
		var addrA = provinceA + cityA + areaA + countyA + villageA ;
		
		var provinceZ = $("#pattr_PROVINCEZ").val();
		var cityZ = $("#pattr_CITYZ").val();
		var areaZ = $("#pattr_AREAZ").val();
		var countyZ = $("#pattr_COUNTYZ").val();
		var villageZ = $("#pattr_VILLAGEZ").val();
		var addrZ = provinceZ + cityZ + areaZ + countyZ + villageZ ;
		
		if(addrA==addrZ){
			MessageBox.alert("提示", "A、Z端详细安装地址不能相同！");
			return false; 
		}
	}
	return true; 
}

//判断勘察单带宽修改后大小
function modifyBandWith(){
	debugger;
	var ifChooseConfcrm = $("#pattr_IF_CHOOSE_CONFCRM").val();//是否选择勘察单
	var changeMode = $("#pattr_CHANGEMODE").val();
	var condTempletId = $("#cond_TEMPLET_ID").val();
	
	if(ifChooseConfcrm=='1'){//选择勘察单
		var hiddenBandWidth = $("#pattr_HIDDEN_BANDWIDTH").val();
		var bandWidth = $("#pattr_BANDWIDTH").val();
		if(eval(hiddenBandWidth)<eval(bandWidth)){
			$.validate.alerter.one($("#pattr_BANDWIDTH")[0], "带宽不能大于勘察单带宽！");
			$("#pattr_BANDWIDTH").val(hiddenBandWidth);
			return false;
		}
	}else if(condTempletId=="DIRECTLINECHANGESIMPLE"&&changeMode=="减容"){
		var hiddenBandWidth = $("#pattr_HIDDEN_BANDWIDTH").val();
		var bandWidth = $("#pattr_BANDWIDTH").val();
		if(eval(hiddenBandWidth)<eval(bandWidth)){
			$("#pattr_BANDWIDTH").val(hiddenBandWidth);
			$.validate.alerter.one($("#pattr_BANDWIDTH")[0], "减容场景带宽必须小于原来带宽！");
			return false;
		}
	}
	var productId = $("#cond_OFFER_CODE").val();
	if((productId=='7011'||productId=='70111'||productId=='70112')&&condTempletId=="ERESOURCECONFIRMZHZG"){
		var bandWidth = $("#pattr_BANDWIDTH").val();
		if(eval(bandWidth)<10){
			$.validate.alerter.one($("#pattr_BANDWIDTH")[0], "互联网专线带宽必须大于或等于10（M）！");
			return false;
		}
	}
	
	return true; 
}

function changeDisable(){
	var value = $("#pattr_ISPREOCCUPY").val();
	if(value == '1'){
		$("#pattr_PREREASON").removeAttr("disabled");
		$("#pattr_PREREASON").attr("placeholder","请填写");
		
	}else{
		$("#pattr_PREREASON").removeAttr("placeholder");
		$("#pattr_PREREASON").attr("disabled","true");
	}
}

function checkCityA(){
	debugger;
	var serviceType = $("#pattr_SERVICETYPE").val(); 
	if(serviceType ==""){
		MessageBox.alert("提示", "请先在业务区域选择专线类型！");
		return false;
	}else if(serviceType!=""){
		if (serviceType == "0" && $("#pattr_CITYZ").val() != ""	&& $("#pattr_CITYA").val() != $("#pattr_CITYZ").val()) {
			$("#pattr_CITYA").val("");
			MessageBox.alert("错误", "专线类型为本地市传输专线，城市A和城市Z必须为同一地市，请修改！");
			return false;
		}
		if (serviceType == "1" && $("#pattr_CITYZ").val() != ""	&& $("#pattr_CITYA").val() == $("#pattr_CITYZ").val()) {
			$("#pattr_CITYA").val("");
			MessageBox.alert("错误", "专线类型为跨地市传输专线，城市A和城市Z为不同地市，请修改！");
			return false;
		}
	}
	changeAreaAByCityA();
}

function checkCityZ(){
	debugger;
	var serviceType = $("#pattr_SERVICETYPE").val(); 
	if(serviceType ==""){
		MessageBox.alert("提示", "请先在业务区域选择专线类型！");
		return false;
	}else if(serviceType!=""){
		if (serviceType == "0" && $("#pattr_CITYA").val() != ""	&& $("#pattr_CITYA").val() != $("#pattr_CITYZ").val()) {
			$("#pattr_CITYZ").val("");
			MessageBox.alert("错误", "专线类型为本地市传输专线，城市A和城市Z必须为同一地市，请修改！");
			return false;
		}
		if (serviceType == "1" && $("#pattr_CITYA").val() != ""	&& $("#pattr_CITYA").val() == $("#pattr_CITYZ").val()) {
			$("#pattr_CITYZ").val("");
			MessageBox.alert("错误", "专线类型为跨地市传输专线，城市A和城市Z为不同地市，请修改！");
			return false;
		}
	}
	changeAreaZByCityZ();
}

function changeAreaAByCityA()
{
	debugger;
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("pattr_CITYA");
	var cityField = document.getElementById("pattr_CITYA");
	if (cityField.value == '海口')	{
		pattr_AREAA.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			pattr_AREAA.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		pattr_AREAA.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			pattr_AREAA.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		pattr_AREAA.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				pattr_AREAA.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
};

function changeAreaZByCityZ()
{
	debugger;
	var cityArr = new Array('白沙','保亭','昌江','乐东','陵水','澄迈','儋州','定安','东方','临高','琼海','琼中','三亚','屯昌','万宁','文昌','五指山','三沙');
	var groupArr = new Array(new Array(), new Array());
	groupArr[0] = new Array('美兰区','龙华区','琼山区','秀英区');
	groupArr[1] = new Array('白沙黎族自治县','保亭黎族苗族自治县','昌江黎族自治县','乐东黎族自治县','陵水黎族自治县','澄迈县','儋州市','定安县','东方市','临高县','琼海市','琼中黎族苗族自治县','三亚市','屯昌县','万宁市','文昌市','五指山市','三沙市');
	groupArr[2] = new Array('天涯区','吉阳区','海棠区','崖州区');
	var cityField = document.getElementById("pattr_CITYZ");
	if (cityField.value == '海口')	{
		pattr_AREAZ.empty();
		for (var i = 0; i < groupArr[0].length; i++){
			pattr_AREAZ.append(groupArr[0][i],groupArr[0][i]);
		}
	} else if (cityField.value == '三亚'){
		pattr_AREAZ.empty();
		for (var i = 0; i < groupArr[0].length; i++)
			pattr_AREAZ.append(groupArr[2][i], groupArr[2][i]);
	}
	else	{
		pattr_AREAZ.empty();
		for (var i = 0; i < cityArr.length; i++) {
			if (cityField.value == cityArr[i]) {
				pattr_AREAZ.append(groupArr[1][i], groupArr[1][i]);
				break;
			}
		}
	}
};