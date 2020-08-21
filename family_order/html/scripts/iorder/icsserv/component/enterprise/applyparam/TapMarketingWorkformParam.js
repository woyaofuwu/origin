$(function(){
	debugger;
	var offerCode = $("#cond_OFFER_CODE");
	
	$("#batchMarketingAddImport").beforeAction(function(e){
		return confirm('是否导入?');
	});
	
	$("#batchMarketingAddImport").afterAction(function(e, status){
		debugger;
		if('ok' == status){
			$.beginPageLoading("正在处理...");
			ajaxSubmit('','batchImportFileForMarketing',null,null,function(datas){
				
				var offerCode = $("#cond_OFFER_CODE").val();
				var maxIndex = $("#MARKETING_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
				
				var temp = new Wade.DatasetList();
				for(var j=0;j<datas.length;j++){
					var batchImports = new Wade.DatasetList();
					var data = datas.get(j);
					loadBatchDataLineParamList(data);
					var key = data.keys;
					for(var m=0;m<key.length;m++){
						var value = data.get(m);
						if(key[m].indexOf('_STRING')<0&&key[m]!='ROW_NUM'&&key[m]!='IMPORT_RESULT'&&key[m]!='IMPORT_ERROR'){
							var batchImport = new Wade.DataMap();
							batchImport.put("ATTR_CODE", key[m]);
							batchImport.put("ATTR_VALUE", value);
							batchImports.add(batchImport);
						}
					}
					var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
					$("#"+offerChaHiddenId).val(batchImports);
					maxIndex++;
					$("#MARKETING_MAX_INDEX").val(maxIndex);
					temp.add(batchImports);
				}
				
				$.endPageLoading();
				MessageBox.success("导入成功！", "页面展示导入数据!",function(){
					flag="true";
				});
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}		
	});
});

var paraName='MARKETING';
function initPageParam(){


}

function loadMarketingLineList(callback){
	debugger;
	var ibsysid = $("#IBSYSID").val();
	var nodeId = $("#NODE_ID").val();
	var bpmTempletId = $("#cond_TEMPLET_ID").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	var userId = $("#apply_USER_ID").val();
	var opertype = $("#cond_OPER_TYPE").val();
	var param = "&TEMPLET_ID="+bpmTempletId+"&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val()+"&GROUP_ID="+$("#cond_GROUP_ID").val();
	
	
	param += "&IBSYSID="+ibsysid+"&NODE_ID="+nodeId;
	//$.beginPageLoading();
	$.beginPageLoading("加载中...");
	$.ajax.submit('','queryMarketingLineList',param,'OfferPart,ParamPart,UploadPart,LineSpecialParam',function(datas){
		if(bpmTempletId=="ETAPMARKETINGENTERING"){
			var offerCode = $("#cond_OFFER_CODE").val();
			var maxIndex = $("#MARKETING_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
			
			var temp = new Wade.DatasetList();
			for(var j=0;j<datas.length;j++){
				var batchImports = new Wade.DatasetList();
				var data = datas.get(j);
				loadBatchDataLineParamList(data);
				var key = data.keys;
				for(var m=0;m<key.length;m++){
					var value = data.get(m);
					if(key[m].indexOf('_STRING')<0&&key[m]!='ROW_NUM'&&key[m]!='IMPORT_RESULT'&&key[m]!='IMPORT_ERROR'){
						var batchImport = new Wade.DataMap();
						batchImport.put("ATTR_CODE", key[m]);
						batchImport.put("ATTR_VALUE", value);
						batchImports.add(batchImport);
					}
				}
				var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
				$("#"+offerChaHiddenId).val(batchImports);
				maxIndex++;
				$("#MARKETING_MAX_INDEX").val(maxIndex);
				temp.add(batchImports);
			}
			
		}else if(bpmTempletId=="ETAPMARKETINGENTERINGUPD"){
			var offerCode = $("#cond_OFFER_CODE").val();
			$.endPageLoading();
			var maxIndex = $("#MARKETING_MAX_INDEX").val();
			for(var n=0;n<datas.length;n++){
				var batchImports = new Wade.DatasetList();
				var pattrData = new Wade.DataMap();
				pattrData=datas.get(n);
				var key = pattrData.keys;
				for(var m=0;m<key.length;m++){
					var value = pattrData.get(m);
					if(key[m].indexOf('_STRING')<0&&key[m]!='ROW_NUM'&&key[m]!='IMPORT_RESULT'&&key[m]!='IMPORT_ERROR'){
						var batchImport = new Wade.DataMap();
						batchImport.put("ATTR_CODE", key[m]);
						batchImport.put("ATTR_VALUE", value);
						batchImports.add(batchImport);
					}
				}
				var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
				$("#"+offerChaHiddenId).val(batchImports);
				maxIndex++;
				$("#MARKETING_MAX_INDEX").val(maxIndex);

			}
		}
		loadingMarketingFile();
		$.endPageLoading();
		if(typeof callback=='function'){
			callback();
		}
		
		
		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//新增专线参数
function addDataLineParam()
{
	debugger;
	var maxIndex = $("#MARKETING_MAX_INDEX").val();
	var offerChaHiddenId = "OFFER_CHA_" + $("#cond_OFFER_CODE").val() + "_" + maxIndex + "_new";
	var offerCode = $("#cond_OFFER_CODE").val();

	initOfferCha(offerChaHiddenId);
}

//修改专线参数
function modifyDataLineParam(offerChaHiddenId)
{
	debugger;
    var offerListNum = $("#MARKETING_PARAM_UL").length;
    if (offerListNum != 0) {
        if ($("#MARKETING_PARAM_UL input[type=radio]:checked").length == 0) {
            MessageBox.alert("提示", "请勾选需要变更的信息进行修改！");
            return false;
        }else if ($("#MARKETING_PARAM_UL input[type=radio]:checked").length > 1) {
            MessageBox.alert("提示", "请只勾选一条需要变更的信息进行修改！");
            return false;
        }
    }
    
	var chks = $("#MARKETING_PARAM_UL [type=radio]");
	{
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)// 获取选中的列表
			{
				var arr = offerChaHiddenId.split("_");
				var chkId = arr[3];
				var idNum = chks[j].id.substring((paraName.length+1));
				if(chkId!=idNum){
					MessageBox.alert("提示", "请对勾选的的信息进行修改！");
		            return false;
				}
			}
		}
	}
	
	initOfferCha(offerChaHiddenId);
}

//保存参数
function afterSubmitOfferChaForMarkting(offerChaSpecDataset)
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#MARKETING_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
	var bpmTempletId = $("#cond_TEMPLET_ID").val();
	if(bpmTempletId=="ETAPMARKETINGENTERINGUPD"){
		loadDataLineParamListForUpd();
	}else{
		loadDataLineParamList();

	}
	
	var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
	$("#"+offerChaHiddenId).val(offerChaSpecDataset);
}
function loadDataLineParamListForUpd(){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#MARKETING_MAX_INDEX").val()-1;
	var check_tag = $("#isCheckTag").val();
	var bpmTempletId = $("#cond_TEMPLET_ID").val();
	
	var lineNo = $("#NOTIN_LINE_NO").val();
	var bandWidth = $("#NOTIN_RSRV_STR1").val();
	var price = $("#NOTIN_RSRV_STR2").val();
	var installationCost = $("#NOTIN_RSRV_STR3").val();//一次性费用（安装调试费）（元）
	
	var tradeIdZHZG = $("#pattr_TRADEID").val();
	var bandWidthZHZG = $("#pattr_BANDWIDTH").val();
	var bizSecurityLvZHZG = $("#pattr_BIZSECURITYLV").val();
	
	
	paraName='MARKETING';
	var lineName = $("#NOTIN_LINENAME").val();

	var bandWidth = $("#NOTIN_BANDWIDTH").val();
	var rsrvStr2 = $("#NOTIN_RSRV_STR2").val();
	var oppcOnntent = $("#NOTIN_OPPONENTMARKETINGCONTENT").val();
	var opponDate = $("#NOTIN_OPPONENTENDDATE").val();
	var renewModeStr = $("#NOTIN_RENEWMODE_span span").text();
	var custConyent = $("#NOTIN_CUSTRENEWCONTENT").val();
	var friendBusinessNameStr = $("#NOTIN_FRIENDBUSINESS_NAME_span span").text();
	var custName = $("#NOTIN_CUST_NAME").val();
	
	$("#"+maxIndex+"NOTIN_LINENAME").text(lineName);
	$("#"+maxIndex+"NOTIN_BANDWIDTH").text(bandWidth);
	$("#"+maxIndex+"NOTIN_RSRV_STR2").text(rsrvStr2);
	$("#"+maxIndex+"NOTIN_OPPONENTMARKETINGCONTENT").text(oppcOnntent);
	$("#"+maxIndex+"NOTIN_OPPONENTENDDATE").text(opponDate);
	$("#"+maxIndex+"NOTIN_RENEWMODE").text(renewModeStr);
	$("#"+maxIndex+"NOTIN_CUSTRENEWCONTENT").text(custConyent);
	$("#"+maxIndex+"NOTIN_FRIENDBUSINESS_NAME").text(friendBusinessNameStr);
	$("#"+maxIndex+"NOTIN_CUST_NAME").text(custName);
	
}

//回写参数列表
function loadDataLineParamList()
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#MARKETING_MAX_INDEX").val();
	var check_tag = $("#isCheckTag").val();
	var bpmTempletId = $("#cond_TEMPLET_ID").val();
	
	var lineNo = $("#NOTIN_LINE_NO").val();
	var bandWidth = $("#NOTIN_RSRV_STR1").val();
	var price = $("#NOTIN_RSRV_STR2").val();
	var installationCost = $("#NOTIN_RSRV_STR3").val();//一次性费用（安装调试费）（元）
	
	var tradeIdZHZG = $("#pattr_TRADEID").val();
	var bandWidthZHZG = $("#pattr_BANDWIDTH").val();
	var bizSecurityLvZHZG = $("#pattr_BIZSECURITYLV").val();
//	var isPreoccupyZHZG = $("#pattr_ISPREOCCUPY").val();

	
	if(bpmTempletId=="ETAPMARKETINGENTERING"){
		paraName='MARKETING';
		var lineName = $("#NOTIN_LINENAME").val();

		var bandWidth = $("#NOTIN_BANDWIDTH").val();
		var rsrvStr2 = $("#NOTIN_RSRV_STR2").val();
		var oppcOnntent = $("#NOTIN_OPPONENTMARKETINGCONTENT").val();
		var opponDate = $("#NOTIN_OPPONENTENDDATE").val();
		var renewModeStr = $("#NOTIN_RENEWMODE_span span").text();
		var custConyent = $("#NOTIN_CUSTRENEWCONTENT").val();
		var friendBusinessNameStr = $("#NOTIN_FRIENDBUSINESS_NAME_span span").text();

		var custName = $("#NOTIN_CUST_NAME").val();


		
		var liHtml = "<li id='"+paraName+"_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='radio' name='DATALINE_TAG' id='"+paraName+"_"+maxIndex+"'/></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线名称：</span><span class='value'>"+lineName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线带宽（兆）：</span><span class='value'>"+bandWidth+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线资费（元/月）：</span><span class='value'>"+rsrvStr2+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>对手营销活动内容：</span><span class='value'>"+oppcOnntent+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>对手签约到期时间：</span><span class='value'>"+opponDate+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>续签方式：</span><span class='value'>"+renewModeStr+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户续签需求：</span><span class='value'>"+custConyent+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>友商名称：</span><span class='value'>"+friendBusinessNameStr+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户名称：</span><span class='value'>"+custName+"</span>";
		liHtml += "</li>";
	}
		
	liHtml += "</ul>";
	liHtml += "</div></div></div>";
	liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
	liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
	liHtml += "</li>";
	
	var newOrModify = $("#OFFER_CHA_HIDDEN_ID").val();
	if(newOrModify.match("new")){
		$("#MARKETING_PARAM_UL").append(liHtml);
	}else{
		$("input:radio[name=DATALINE_TAG]:checked").each(function(){
			var checkBoxId = $(this).attr("id");
			$("#"+checkBoxId+"_LI").replaceWith(liHtml);
		});
	}
	if($("#MARKETING_PARAM_UL").children().length > 0)
	{
		$("#MARKETING_PARAM_UL").parent(".c_list").css("display", "");
	}
	maxIndex++;
	$("#MARKETING_MAX_INDEX").val(maxIndex);
	$("#check_tag").val(check_tag);
}

//删除专线参数列表
function deleteDataLineParam()
{
	var checkedNum = getCheckedBoxNum("DATALINE_TAG");
	if(checkedNum < 1)
	{
		MessageBox.alert("提示信息", "请选择需要删除的信息！");
		return false;
	}
	$("input:radio[name=DATALINE_TAG]:checked").each(function(){
		var checkBoxId = $(this).attr("id");
		$("#"+checkBoxId+"_LI").remove();
		if($("#MARKETING_PARAM_UL").children().length == 0)
		{
			$("#MARKETING_PARAM_UL").parent(".c_list").css("display", "none");
		}
		var offerCode = $("#cond_OFFER_CODE").val();
		if("9983"==offerCode){
		$("#MARKETING_MAX_INDEX").val("0");
		}
	});
}







function loadBatchDataLineParamList(obj){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var bpmTempletId = $("#cond_TEMPLET_ID").val();
	var maxIndex = $("#MARKETING_MAX_INDEX").val();
	if(bpmTempletId=="ETAPMARKETINGENTERING"){
		paraName='MARKETING';
		var lineName = obj.get("NOTIN_LINENAME");

		var bandWidth = obj.get("NOTIN_BANDWIDTH");
		var rsrvStr2 = obj.get("NOTIN_RSRV_STR2");
		var oppcOnntent = obj.get("NOTIN_OPPONENTMARKETINGCONTENT");
		var opponDate = obj.get("NOTIN_OPPONENTENDDATE");
		var renewModeStr = obj.get("NOTIN_RENEWMODE_STRING");
		var custConyent = obj.get("NOTIN_CUSTRENEWCONTENT");
		var friendBusinessNameStr = obj.get("NOTIN_FRIENDBUSINESS_NAME_STRING");

		var custName = obj.get("NOTIN_CUST_NAME");


		
		var liHtml = "<li id='"+paraName+"_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='radio' name='DATALINE_TAG' id='"+paraName+"_"+maxIndex+"'/></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线名称：</span><span class='value'>"+lineName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线带宽（兆）：</span><span class='value'>"+bandWidth+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线资费（元/月）：</span><span class='value'>"+rsrvStr2+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>对手营销活动内容：</span><span class='value'>"+oppcOnntent+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>对手签约到期时间：</span><span class='value'>"+opponDate+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>续签方式：</span><span class='value'>"+renewModeStr+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户续签需求：</span><span class='value'>"+custConyent+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>友商名称：</span><span class='value'>"+friendBusinessNameStr+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户名称：</span><span class='value'>"+custName+"</span>";
		liHtml += "</li>";
	}
		
	liHtml += "</ul>";
	liHtml += "</div></div></div>";
	liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
	liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
	liHtml += "</li>";

	
	$("#MARKETING_PARAM_UL").append(liHtml);
	if($("#MARKETING_PARAM_UL").children().length > 0)
	{
		$("#MARKETING_PARAM_UL").parent(".c_list").css("display", "");
	}
	$("#MARKETING_MAX_INDEX").val(maxIndex);
}

function selectTapMarketing(el){
	debugger;
	var tapMarketing_ibsyid = el.value;
	if(tapMarketing_ibsyid==''){
		return false;
	}
	var offerCode = $("#cond_OFFER_CODE").val();
	if(offerCode == null || offerCode == "")
	{
		$.validate.alerter.one(document.getElementById("cond_OFFER_CODE"), "请选择产品！");
		return false;
	}
	
	var userId = $("#apply_USER_ID").val();
	var opertype = $("#cond_OPER_TYPE").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	if(!templetId){
		return false;
	}
	var pattrTitle = $("#OrderPart input[name=pattr_TITLE]").val();
	var pattrLevel = $("#OrderPart input[name=pattr_URGENCY_LEVEL]").val();
	
	var param = "&TEMPLET_ID="+templetId+"&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val()+"&GROUP_ID="+$("#cond_GROUP_ID").val();
	
	
	param += "&MARKETINGIBSYSID="+tapMarketing_ibsyid;
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("", "getMarketingById", param, "OfferPart,ParamPart,UploadPart,LineSpecialParam", function(data){
	/*ajaxSubmit(this,'getMarketingById',param,null,function(data){*/
		/*$.endPageLoading();
		$("#C_PROVINCEA").val(data.get("C_PROVINCEA"));	
		$("#C_CITYA").val(data.get("C_CITYA"));	

		$("#C_RESPONSIBILITY_NAME").val(data.get("C_RESPONSIBILITY_NAME"));	
		$("#C_RESPONSIBILITY_ID").val(data.get("C_RESPONSIBILITY_ID"));	

		$("#C_RESPONSIBILITY_CITYCODE").val(data.get("C_RESPONSIBILITY_CITYCODE"));	

		$("#C_RESPONSIBILITY_PHONE").val(data.get("C_RESPONSIBILITY_PHONE"));	*/


		$.endPageLoading();
		$("#C_TAPMARKETING_SELECT").val(tapMarketing_ibsyid);	
		var pattrList = new Wade.DatasetList();
		pattrList=data.get("pattrList");
		var maxIndex = $("#MARKETING_MAX_INDEX").val();
		for(var n=0;n<pattrList.length;n++){
			var batchImports = new Wade.DatasetList();
			var pattrData = new Wade.DataMap();
			pattrData=pattrList.get(n);
			var key = pattrData.keys;
			for(var m=0;m<key.length;m++){
				var value = pattrData.get(m);
				if(key[m].indexOf('_STRING')<0&&key[m]!='ROW_NUM'&&key[m]!='IMPORT_RESULT'&&key[m]!='IMPORT_ERROR'){
					var batchImport = new Wade.DataMap();
					batchImport.put("ATTR_CODE", key[m]);
					batchImport.put("ATTR_VALUE", value);
					batchImports.add(batchImport);
				}
			}
			var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
			$("#"+offerChaHiddenId).val(batchImports);
			maxIndex++;
		}
		
		
		$("#OrderPart input[name=pattr_TITLE]").val(pattrTitle);
		$("#OrderPart input[name=pattr_URGENCY_LEVEL]").val(pattrLevel);

		$("#MARKETING_MAX_INDEX").val(maxIndex);
		if($("#C_MARKETING_FILE_ID").val()){
			var fileListM = $("#C_MARKETING_FILE_ID").val();
			marketingupload.loadFile(fileListM);
		}
		loadingMarketingFile();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	

}
function selectMarketingOperation(el){
	debugger;
	var operation = el.value;
	if(operation==''){
		return false;
	}else if(operation=='0'){
		$("#C_TAPMARKETING_OPERATION").val('');
		MessageBox.alert("提示信息", "执行修改时无法新增！");
		return false;
	}
}
function selectMarketingProductNo(el){
	debugger;
	var groupId=$("#cond_GROUP_ID").val();
	if(groupId==''){
		$('#'+el.id).val('');
		MessageBox.alert("提示信息", "请先输入集团信息！");
		return false;
	}
	var productNo = el.value;
	if(productNo==''){
		$('#'+el.id).val('');
		MessageBox.alert("提示信息", "该参数不可填入空值！");
		return false;
	}
	var offerCode = $("#cond_OFFER_CODE").val();
	var param = "&PRODUCTNO="+productNo+"&GROUPID="+groupId+"&OFFER_CODE="+offerCode;
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("", "getMarketingProductNo", param, null, function(datas){
		$.endPageLoading();
		if(datas!=null&&datas.length>0){
			var data = new Wade.DataMap();
			data=datas.get(0);
			if(data.get('getDatalineforTapMarkting')!=null&&data.get('getDatalineforTapMarkting')=='0000'){
				var productNoId=el.id;;
				$('#'+productNoId.replace('NOTIN_PRODUCTNO','NOTIN_PRODUCTNUMBER')).val(data.get('PRODUCTNUMBER'));
				$('#'+productNoId.replace('NOTIN_PRODUCTNO','NOTIN_MONTHLYFEE_EXCITATION')).val(data.get('MONTHLYFEE_EXCITATION'));
			}else{
				$('#'+el.id).val('');
				MessageBox.alert("提示信息", "校验实例号异常："+data.get('getDatalineforTapMarkting')+" 异常："+data.get('getDatalineforTapMarktingInfo'));
				return false;
			}
		}
	
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function selectMarketingContractAge(el){
	debugger;
	var param = el.value;
	if(isNaN(param)){
		alert('需输入数字！');
		$('#'+el.id).val('');
		return false;
	}
	else{
		if(parseInt(param)<1){
			alert('签约年限需大于1年！');
			$('#'+el.id).val('');
			return false;
		}
	}
}
function loadingMarketingFile()
{
	var fileList=$("#C_MARKETING_FILE_ID").val();
	var fileListName=$("#C_MARKETING_FILENAME").val();
	if(fileList!=''&&fileListName!=''){
		var dataList = new Wade.DatasetList();
		var arr =fileList.split(",");
		var arrName =fileListName.split(",");
		for (var j=0;j<arr.length;j++){
			var data2 = new Wade.DataMap();
			data2.put("FILE_ID", arr[j]);
			data2.put("FILE_NAME", arrName[j]);
			data2.put("ATTACH_TYPE", "M");
			dataList.add(data2);
		}
		if(dataList.length>0){
			$("#C_MARKETING_FILE_LIST").text(dataList.toString());
		}
	}
}
