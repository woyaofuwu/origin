var productGoodInfos = 	PageData.getData($(".e_SelectOfferPart"));

/**
 * bboss业务前台初始化js
 * Created by chenkh on 2016/3/19.
 */
function initPageParam() {
    // 1 取子商品ID
    var nowProdOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
    var offerId = nowProdOfferData.get("OFFER_ID");
    var operType = $("#cond_OPER_TYPE").val();
    var brandCode = nowProdOfferData.get("BRAND_CODE");
    
    //处理成员属性初始化  成员类型  成员操作类型  成员状态
    if("BOSG" == brandCode){
       if("CrtMb" == operType || "ChgMb" == operType){
    	   dealMbInitInfo();
       }
    }

    // 2 根据产品规格分发
    // 2.1 400业务处理（除了400语音）
    if ( "110000998310" == offerId || "110000998302" == offerId || "110000998303" == offerId || "110000998304" == offerId || "110000998306" == offerId ||
        "110000998309" == offerId || "210000003801" == offerId  || "110000998305" == offerId) {
        deal400Biz(operType);
    }
    //农政通ec接入号初始化
    if ( "110000998201" == offerId || "110000998202" == offerId || "110000998203" == offerId || "110000998204" == offerId || "110000998205" == offerId) {
    	dealAgricultureEcNum();
     }
    // 一点支付业务处理
    if("110000990801" == offerId){
    	dealYDZHBiz();
    }
    // yunmas
    if("110000997603" == offerId || offerId== "110000997607" || offerId== "110000997601" ||offerId== "110000997602"
    	|| offerId== "110000997604" || offerId== "110000997605" || offerId== "110000997606" || "110000997501"==offerId || "110000997502"==offerId || 
    	"110000997503"==offerId || "110000997504"==offerId){
    	 		
    	
    	IAGWCloudMAS_init();
    }

}

//处理成员属性初始化  成员类型  成员操作类型  成员状态
function dealMbInitInfo(){
	var nowProdOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
    var offerId = nowProdOfferData.get("OFFER_ID");
    var productId = $("#cond_BBOSS_SUB_OFFER_ID").val();
    var operType = $("#cond_OPER_TYPE").val();
    var grpUserId = $("#cond_EC_USER_ID").val();
    var eparchyCode = $("#cond_USER_EPARCHY_CODE").val();
    var param = "";
    
    var totalOfferData = PageData.getData($("#class_"+offerId));

	param = "&OPER_TYPE="+operType+"&MEB_OFFER_ID="+productId+"&GRP_USER_ID="+grpUserId+"&EPARCHY_CODE="+eparchyCode;
    if("ChgMb" == operType){
    	var merSerialNumber = $("#cond_VALID_SERIAL_NUMBER").val();
    	var mebUserId = $("#cond_USER_ID").val();
    	param += "&MEB_SERIAL_NUMBER="+merSerialNumber+"&MEB_USER_ID="+mebUserId;
    }
    
    Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'dealMbInitInfo',
			param, function(d) {
        		var dataList = d.map.result; 
                if (dataList.length>0){
                	//mebOpers  成员操作类型  M710000000734
                	var mebOfers = dataList.get("MEB_OPERS");
                	
                	//mebTypeSet 成员类型
                	$("#input_BM710000000733").val(dataList.get("MEB_TYPE_SET").get("ATTR_NAME"));
                	
                	//productStatus 成员状态
                	$("#input_BM710000000735").val(dataList.get("PRODUCT_STATUS"));
                	
                	
                	
                }
            }, function(e, i) {
                MessageBox.alert("操作失败");
                return false;
            },{async:false});
}


function dealYDZHBiz(){
	var productOrderId = $("#PRODUCT_ORDER_ID").val();
	if(typeof(productOrderId) == "undefined"){
		productOrderId = "";
	}
	//获取成员附件上传文件名
	if ( typeof($("#input_B999033717").val()) != "undefined" && $("#input_B999033717").attr("style")!="display:none;" && $("#input_B999033717").attr("nullable") =="no" ) {
		
        var fileName = "";

		//成员附件 1 确认= 2   开通 3  
		var fileType="1";
		var flag = true;
		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getYDZHUploadFileName',
            	'&PRODUCT_ORDER_ID=' + productOrderId + '&FILE_TYPE='+fileType, function(d) {
            		var dataList = d.map.result;
	                var dataMap = dataList.get(0);
	                if (dataMap){
	                	fileName = dataMap.get('VALUE');
	                }
	            }, function(e, i) {
	                MessageBox.alert("操作失败");
	                flag = false;
	            },{async:false});
		if (!flag){
		    return;
        }
		if(fileName =="" || fileName == null)
		{
			MessageBox.alert("提示信息","成员附件未上传，请到批量界面上传主办省成员附件！");
		}
		
        $("#input_B999033717").val(fileName);
    }
	
	if ( typeof($("#input_B999033734").val()) != "undefined" && $("#input_B999033734").attr("style")!="display:none;" && $("#input_B999033734").attr("nullable") =="no") {
		
        var fileName = "";

		//号码确认反馈明细附件 
		var fileType="2";
		Wade.httphandler.submit('',
				'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getYDZHUploadFileName',
            	'&PRODUCT_ORDER_ID=' + productOrderId + '&FILE_TYPE='+fileType, function(d) {
            		var dataList = d.map.result;
	                var dataMap = dataList.get(0);
	                if (dataMap){
	                	fileName = dataMap.get('VALUE');
	                }
	            }, function(e, i) {
	                MessageBox.alert("操作失败");
	                return false;
	            },{async:false});
		
		if(fileName =="" || fileName == null)
		{
			MessageBox.alert("提示信息","成员附件未上传，请到批量界面上传配合省号码确认反馈明细附件！");
		}
		
        $("#input_B999033734").val(fileName);
    }

	if ( typeof($("#input_B999033735").val()) != "undefined" && $("#input_B999033735").attr("style")!="display:none;" && $("#v").attr("nullable") =="no") {
	
    var fileName = "";

	//号码开通反馈明细附件 
	var fileType="3";
	Wade.httphandler.submit('',
			'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getYDZHUploadFileName',
        	'&PRODUCT_ORDER_ID=' + productOrderId + '&FILE_TYPE='+fileType, function(d) {
        		var dataList = d.map.result;
                var dataMap = dataList.get(0);
                if (dataMap){
                	fileName = dataMap.get('VALUE');
                }
            }, function(e, i) {
                MessageBox.alert("操作失败");
                return false;
            },{async:false});
	
	if(fileName =="" || fileName == null)
	{
		MessageBox.alert("提示信息","成员附件未上传，请到批量界面上传配合省号码开通反馈明细附件！");
	}
	
    $("#input_B999033735").val(fileName);
}
	
	
}
/**
 * 云mas取服务序列
 */

function getRandom1(){

	var rand = "";
	  for(var i = 0; i < 3; i++){
	      var r = Math.floor(Math.random() * 10);
	      rand += r;
	  }

	  var arr = ["106509652","106509022","106509622","106509052"];
	  var index = Math.floor((Math.random()*arr.length));

	  var servCode = arr[index]+rand;
	//取后台序列
	  Wade.httphandler.submit('',
	            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getBbossSeqId',
	            '&SERV_CODE=' + servCode, function(d) {
					    if(d.result == "1"){
					    	dealServicId();
					    }
	            }, function(e, i) {
	                MessageBox.alert("操作失败");
	                return false;
	            },{async:false});
	  
	  return servCode;

}

function dealServicId() {
	var servCode = getRandom1();
	if ( typeof($("#input_B1101594002").val()) != "undefined" ) {
          $("#input_B1101594002").val(servCode);
     }
	
    //设置业务代码
    var servCodeTemp = servCode.substring(0,9);
  	var bizCode = "4444444444";
  	if(servCodeTemp == "106509652"){
			bizCode = "6666666666";
		}else if(servCodeTemp == "106509022"){
			bizCode = "7777777777";
		}else if(servCodeTemp == "106509622"){
			bizCode = "4444444444";
		}else{
			bizCode = "1111111111";
		}
	$('#input_B1101594010').val(bizCode);
}


function dealAgricultureEcNum() {
	var ecNum="";
	//取后台序列
	  Wade.httphandler.submit('',
	            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getEcNum',
	            null, function(d) {
	                ecNum = d.map.result;
	            }, function(e, i) {
	                MessageBox.alert("操作失败");
	                return false;
	            },{async:false});
	
	var frontValue="12582999952";
	var instData=frontValue+ecNum;
	
	if ( typeof($("#input_B331011009").val()) != "undefined" ) {
        $("#input_B331011009").val(instData);
    }
	if ( typeof($("#input_B331012009").val()) != "undefined" ) {
        $("#input_B331012009").val(instData);
    }
	if ( typeof($("#input_B1102221009").val()) != "undefined" ) {
          $("#input_B1102221009").val(instData);
      }
	if ( typeof($("#input_B1102222009").val()) != "undefined" ) {
        $("#input_B1102222009").val(instData);
    }
	if ( typeof($("#input_B331012009").val()) != "undefined" ) {
        $("#input_B331012009").val(instData);
    }
	if ( typeof($("#input_B331041009").val()) != "undefined" ) {
        $("#input_B331041009").val(instData);
    }
	if ( typeof($("#input_B331042009").val()) != "undefined" ) {
        $("#input_B331042009").val(instData);
    }
	if ( typeof($("#input_B331051009").val()) != "undefined" ) {
        $("#input_B331051009").val(instData);
    }	
	if ( typeof($("#input_B331052009").val()) != "undefined" ) {
        $("#input_B331052009").val(instData);
    }
	if ( typeof($("#input_B331031009").val()) != "undefined" ) {
        $("#input_B331031009").val(instData);
    }
	if ( typeof($("#input_B331032019").val()) != "undefined" ) {
        $("#input_B331032019").val(instData);
    }
	
	//
	frontValue="12582605147";
	instData=frontValue+ecNum;
	if ( typeof($("#input_B399011009").val()) != "undefined" ) {
        $("#input_B399011009").val(instData);
    }
	if ( typeof($("#input_B399011019").val()) != "undefined" ) {
        $("#input_B399011019").val(instData);
    }
	if ( typeof($("#input_B399012019").val()) != "undefined" ) {
        $("#input_B399012019").val(instData);
    }
	if ( typeof($("#input_B399021009").val()) != "undefined" ) {
        $("#input_B399021009").val(instData);
    }
	if ( typeof($("#input_B399021019").val()) != "undefined" ) {
        $("#input_B399021019").val(instData);
    }
	if ( typeof($("#input_B331032019").val()) != "undefined" ) {
        $("#input_B399022019").val(instData);
    }
	if ( typeof($("#input_B399031009").val()) != "undefined" ) {
        $("#input_B399031009").val(instData);
    }
	if ( typeof($("#input_B399031019").val()) != "undefined" ) {
        $("#input_B399031019").val(instData);
    }
    if ( typeof($("#input_B399032019").val()) != "undefined" ) {
        $("#input_B399032019").val(instData);
    }
}
function deal400Biz(operType) {
    var merchData = PageData.getData($("#class_110000009983"));
    // 如果没有取到商品信息直接返回
    if ( !merchData ) {
        return;
    }
    var merchpData = new Wade.DatasetList();
    merchpData = merchData.get("SUBOFFERS");
    // 如果没有取到子商品信息直接返回
    if ( !merchpData ) {
        return;
    }
    if ("CrtUs" == operType) {
        get400NumForCrt(merchpData);
    }
    if ("ChgUs" == operType){
        get400NumForChg(merchpData);
    }

}
function get400NumForChg(merchpData){
    for (var i = 0; i<merchpData.length; i++) {
        var offerId = merchpData.get(i).get("OFFER_ID");
        // 取不到或者不是400语音产品直接下次循环
        if ( typeof(offerId) == "undefined" || offerId == "" || offerId != "110000998301" ) {
            continue;
        }
        var subscriberInstId = merchpData.get(i).get("USER_ID");
        if (!subscriberInstId) {
            return ;
        }
        var instData = "";
        // 查询后台400语音的数据
        Wade.httphandler.submit('',
            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getInsDataBySubInstIdAndChaSpecId',
            '&SUBSCRIBER_ID=' + subscriberInstId + '&CHA_SPEC_ID=4115017001', function(d) {
                var dataList = d.map.result;
                var dataMap = dataList.get(0);
                if (dataMap){
                    instData = dataMap.get('ATTR_VALUE');
                }
            }, function(e, i) {
                MessageBox.alert("操作失败");
                return false;
            },{async:false});
	        if ( typeof(instData) == "undefined" || "" == instData ) {
	            MessageBox.alert("400语音子商品的实例数据中400号码特征规格信息缺失！");
	            return;
	        }
        if ( typeof($("#input_B4115067011").val()) != "undefined" ) {
            $("#input_B4115067011").val(instData);
            $("#input_B4115061009").val("10657" + instData);
            $("#input_B4115062009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115087011").val()) != "undefined" ) {
            $("#input_B4115087011").val(instData);
            $("#input_B4115081009").val("10657" + instData);
            $("#input_B4115082009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115047011").val()) != "undefined" ) {
            $("#input_B4115047011").val(instData);
            $("#input_B4115041009").val("10657" + instData);
            $("#input_B4115042009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115107011").val()) != "undefined" ) {
            $("#input_B4115107011").val(instData);
            $("#input_B4115101009").val("10657" + instData);
            $("#input_B4115102009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115057011").val()) != "undefined" ) {
            $("#input_B4115057011").val(instData);
            $("#input_B4115051009").val("10657" + instData);
            $("#input_B4115052009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115027011").val()) != "undefined" ) {
            $("#input_B4115027011").val(instData);
            $("#input_B4115021009").val("10657" + instData);
            $("#input_B4115022009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115037011").val()) != "undefined" ) {
            $("#input_B4115037011").val(instData);
            $("#input_B4115031009").val("10657" + instData);
            $("#input_B4115032009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115077011").val()) != "undefined" ) {
            $("#input_B4115077011").val(instData);
            $("#input_B4115071009").val("10657" + instData);
            $("#input_B4115072009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115097011").val()) != "undefined" ) {
            $("#input_B4115097011").val(instData);
            $("#input_B4115091009").val("10657" + instData);
            $("#input_B4115092009").val("10657" + instData);
            return;
        }
        if ( typeof($("#input_B4115111001").val()) != "undefined" ) {
            $("#input_B4115111001").val(instData);
            return;
        }
    }
}

function get400NumForCrt(merchpData){
    var numberOf400 = "";
    for (var i = 0; i < merchpData.length; i++) {
        var offerId = merchpData.get(i).get("OFFER_ID");
        // 取不到或者不是400语音产品直接下次循环
        typeof(offerId);
        if ( typeof(offerId) == "undefined" || offerId == "" || offerId != "110000998301" ) {
            continue;
        }
        var offerChaSpecs = merchpData.get(i).get("OFFER_CHA_SPECS");
        if ( !offerChaSpecs ) {
            continue;
        }
        for (var j = 0; j < offerChaSpecs.length; j++) {
            var chaId = offerChaSpecs.get(j).get("CHA_SPEC_ID");
            if ( "4115017001" == chaId ) {
                numberOf400 = offerChaSpecs.get(j).get("ATTR_VALUE");
            }
        }
    }
    if ( typeof(numberOf400) == "undefined" || "" == numberOf400 ) {
        MessageBox.alert("请先填写400语音子商品的特征规格信息！");
        return;
    }
    if ( typeof($("#input_B4115067011").val()) != "undefined" ) {
        $("#input_B4115067011").val(numberOf400);
        $("#input_B4115061009").val("10657" + numberOf400);
        $("#input_B4115062009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115087011").val()) != "undefined" ) {
        $("#input_B4115087011").val(numberOf400);
        $("#input_B4115081009").val("10657" + numberOf400);
        $("#input_B4115082009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115047011").val()) != "undefined" ) {
        $("#input_B4115047011").val(numberOf400);
        $("#input_B4115041009").val("10657" + numberOf400);
        $("#input_B4115042009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115107011").val()) != "undefined" ) {
        $("#input_B4115107011").val(numberOf400);
        $("#input_B4115101009").val("10657" + numberOf400);
        $("#input_B4115102009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115057011").val()) != "undefined" ) {
        $("#input_B4115057011").val(numberOf400);
        $("#input_B4115051009").val("10657" + numberOf400);
        $("#input_B4115052009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115027011").val()) != "undefined" ) {
        $("#input_B4115027011").val(numberOf400);
        $("#input_B4115021009").val("10657" + numberOf400);
        $("#input_B4115022009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115037011").val()) != "undefined" ) {
        $("#input_B4115037011").val(numberOf400);
        $("#input_B4115031009").val("10657" + numberOf400);
        $("#input_B4115032009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115077011").val()) != "undefined" ) {
        $("#input_B4115077011").val(numberOf400);
        $("#input_B4115071009").val("10657" + numberOf400);
        $("#input_B4115072009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115097011").val()) != "undefined" ) {
        $("#input_B4115097011").val(numberOf400);
        $("#input_B4115091009").val("10657" + numberOf400);
        $("#input_B4115092009").val("10657" + numberOf400);
        return;
    }
    if ( typeof($("#input_B4115111001").val()) != "undefined" ) {
        $("#input_B4115111001").val(numberOf400);
        return;
    }
    
}



/** **************************************省行业网关云MAS商品***************************************** */
/*
  	 * 省行业网关云MAS初始化入口 @author chenkh @param productGoodInfos
  	 */
function IAGWCloudMAS_init() {
		//获取业务代码
		dealServicId();
		
	    var nowProdOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
	    var offerId = nowProdOfferData.get("OFFER_CODE");
	    var operType = nowProdOfferData.get("MERCHP_OPER_TYPE");
	    var allNetProductId = "";
		 Wade.httphandler.submit('',
		            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMerchPId',
		            '&ALLNET_PRODUCT_ID='+offerId, function(d) {
			 allNetProductId = d.map.result;
		            }, function(e, i) {
		                MessageBox.alert("操作失败");
		                return false;
		            },{async:false});
	    
	    // 1- 局数据产品和全网短流程基本接入号和企业代码校验
	    checkBizInCode(allNetProductId, operType);

	    if (operType != "1" && operType != "7") {
	        return false;
	    }
	    // 2- 服务代码一致性校验
	    checkServiceCode(operType, allNetProductId);
	    // 3- 企业代码一致性校验
	    checkEnterpriseCode(operType,allNetProductId);
	    // 4-端口速率一致性校验
	    checkPortRate(productGoodInfos, allNetProductId);

}


function dealbaseinCodeputAfter(data, allNetProductId) {
	    var ecBaseInCode = data.get("EC_BASE_IN_CODE", "");
	    var ecBaseInCodea = data.get("EC_BASE_IN_CODE_A", "");
	    if (ecBaseInCode == '' || ecBaseInCode == '[]') {
	    	MessageBox.alert("提示","该集团的基本接入号为空,请通过集团业务--资料管理--集团客户资料管理先维护此要素!");
	        return;

	    }
	    if ("110164" == allNetProductId) {
	        if (ecBaseInCodea == '01') {
	            $("#input_B1101644005").val(ecBaseInCode);
	            //changeValue($('#input_1101644005')[0]);
	            $('#input_B1101644005').attr('disabled', true);

	        } else {
	            $("#input_B1101644007").val(ecBaseInCode);
	            //changeValue($('#input_1101644007')[0]);
	            $('#input_B1101644007').attr('disabled', true);

	        }

	        $("#input_B1101644002").val(data.get("SP_CODE", ""));
	        //changeValue($('#input_B1101644002')[0]);
	        $('#input_B1101644002').attr('disabled', true);
	    } else if ("110163" == allNetProductId) {
	        if (ecBaseInCodea == '01') {
	            $("#input_B1101631009").val(ecBaseInCode);
	            //changeValue($('#input_B1101631009')[0]);
	            $('#input_B1101631009').attr('disabled', true);

	        } else {
	            $("#input_B1101631019").val(ecBaseInCode);
	            //changeValue($('#input_B1101631019')[0]);
	            $('#input_B1101631019').attr('disabled', true);

	        }

	        $("#input_B1101630004").val(data.get("SP_CODE", ""));
	        //changeValue($('#input_B1101630004')[0]);
	        $('#input_B1101630004').attr('disabled', true);

	    } else if (allNetProductId == "110157" || allNetProductId == "110158" || allNetProductId == "110159") {
	        $("#input_B" + allNetProductId + "4002").val(ecBaseInCode);
	        //changeValue($("#input_" + allNetProductId + "4002")[0]);
	        $("#input_B" + allNetProductId + "4002").attr('disabled', true);
	        // 服务代码为1065096开头，系统默认“白名单”；为1065097开头，系统默认“黑名单”。不允许修改
	        if ("110157" == allNetProductId) {
	            if (ecBaseInCode.length > 9 && (ecBaseInCode.substring(0, 9) == "106509622")||ecBaseInCode.substring(0, 9) == "106509652"
	            	||ecBaseInCode.substring(0, 9) == "106509022"||ecBaseInCode.substring(0, 9) == "106509052") {
	                $('#input_B1101574011').val("2");
	                //$('#input_B1101574011').change();
	                $('#input_B1101574011').attr("disabled", true);
	                //$('#PARAM_NAME_1101574012').removeClass("e_required");
	                $('#input_B1101574012').attr("nullable", "yes");

	            } else if (ecBaseInCode.length > 9 && (ecBaseInCode.substring(0, 9) == "106509722"||ecBaseInCode.substring(0, 9) == "106509752"||ecBaseInCode.substring(0, 9) == "106509122"||ecBaseInCode.substring(0, 9) == "106509152")) {
	                $('#input_B1101574011').val("0");
	                //$('#input_B1101574011').change();
	                $('#input_B1101574011').attr("disabled", true);
	                //$('#PARAM_NAME_1101574012').attr("class", "e_required");
	                $('#input_B1101574012').attr("nullable", "no");
	            } else {
	    	    	MessageBox.alert("提示","省行业网关短流程服务代码必须以白名单1065096-XY-ABC，1065090-XY-ABC或者黑名单1065097-XY-ABC，1065091-XY-ABC(xy的校验规则请参照枚举对应关系)开头!");

	            }
	        }

	    }
	    return;
	}
function checkBizInCode(allNetProductId, productOpType) {

	    if (allNetProductId == "110164" || allNetProductId == "110163") {

	        // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接推出
	        if (productOpType != "1") {
	            return false;
	        }
	        // 获取短信基本接入号和企业代码
	    	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	    	var groupId = groupInfo.get("GROUP_ID");
	    	var custId = groupInfo.get("CUST_ID");
	    	var incodea = "01";
	        var bizTypeCode = "001";
			 Wade.httphandler.submit('',
			            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMasEcCodeListByA',
			            '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + groupId + '&BIZ_TYPE_CODE=' + bizTypeCode, function(d) {
				 			dealbaseinCodeputAfter(d, allNetProductId);			            }, function(e, i) {
			                MessageBox.alert("操作失败");
			                return false;
			            },{async:false});
	    		        
	    } else if (allNetProductId == "110157" || allNetProductId == "110158" || allNetProductId == "110159") {
	        if (productOpType != "1") {
	            return false;
	        }
	        // 获取短信基本接入号和企业代码
	    	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	    	var groupId = groupInfo.get("GROUP_ID");	        
	    	var incodea = allNetProductId == "110159" ? "02": "01";
	        var bizTypeCode = allNetProductId == "110159" ? "002": "001";
			 Wade.httphandler.submit('',
			            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMasEcCodeListByA',
			            '&EC_BASE_IN_CODE_A=' + incodea + '&GROUP_ID=' + groupId + '&BIZ_TYPE_CODE=' + bizTypeCode, function(d) {
				 			dealbaseinCodeputAfter(d, allNetProductId);			            }, function(e, i) {
			                MessageBox.alert("操作失败");
			                return false;
			            },{async:false});

	    }

	}

function checkServiceCode(operType, allNetProductId) {
	    // 1- 调用产品一致性检查，取得主产品中主属性
	    var baseProduct = "";
	    var baseincode = ""; //短信基本接入号属性编码
	    if ("110154" == allNetProductId || "110155" == allNetProductId) {
	        baseProduct = "110163";
	        baseincode = "1101631009";
	    } else if ("110156" == allNetProductId) {
	        baseProduct = "110163";
	        baseincode = "1101631019";
	    } else if ("110160" == allNetProductId || "110161" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644005";
	    } else if ("110162" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644007";
	    } else {
	        return;

	    }
	    var attrValue = checkAttrSync(operType, baseProduct, baseincode);
	    // 3- 对其他产品进行赋值
	    if ("110154" == allNetProductId) {
	        $("#input_B1101544006").val(attrValue);
	       // changeValue($('#input_B1101544006')[0]);
	        //$('#input_B1101544006').attr('disabled', true); 
	    } else if ("110155" == allNetProductId) {
	        $("#input_B1101554006").val(attrValue);
	        changeValue($('#input_B1101554006')[0]);
	        $('#input_B1101554006').attr('disabled', true);
	    } else if ("110156" == allNetProductId) {
	        $("#input_B1101564006").val(attrValue);
	        //changeValue($('#input_B1101564006')[0]);
	        $('#input_B1101564006').attr('disabled', true);
	    } else if ("110160" == allNetProductId) {
	        $("#input_B1101604002").val(attrValue);
	        //changeValue($('#input_B1101604002')[0]);
	        $('#input_B1101604002').attr('disabled', true);
	    } else if ("110161" == allNetProductId) {
	        $("#input_B1101614002").val(attrValue);
	        //changeValue($('#input_B1101614002')[0]);
	        $('#input_B1101614002').attr('disabled', true);
	    } else if ("110162" == allNetProductId) {
	        $("#input_B1101624002").val(attrValue);
	        //changeValue($('#input_B1101624002')[0]);
	        $('#input_B1101624002').attr('disabled', true);
	    }
	}

function getMasBaseProduct(productGoodInfos) {	    
	    var nowProdOfferData = PageData.getData($("#prodDiv_OFFER_DATA"));
	    var offerCode = nowProdOfferData.get("OFFER_CODE");
	    var allNetProductId ="";
		 Wade.httphandler.submit('',
		            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getMerchPId',
		            '&ALLNET_PRODUCT_ID='+offerCode, function(d) {
			 allNetProductId = d.map.result;
		            }, function(e, i) {
		                MessageBox.alert("操作失败");
		                return false;
		            },{async:false});
	   
		var baseProduct = "";
	    if ("110154" == allNetProductId || "110155" == allNetProductId || "110156" == allNetProductId) {
	        baseProduct = "110163";
	    } else if ("110160" == allNetProductId || "110161" == allNetProductId || "110162" == allNetProductId) {
	        baseProduct = "110164";
	    }
	    return baseProduct;

	}

function checkEnterpriseCode(productGoodInfos,allNetProductId) {
	    var attrValue = "";
	    var baseProduct = getMasBaseProduct();
	    // 1- 调用产品一致性检查，取得主产品中主属性
	    if ("110163" == baseProduct) {
	        attrValue = checkAttrSync(productGoodInfos, "110163", "1101630004");
	    }
	    else if("110164" == baseProduct){
	    	attrValue = checkAttrSync(productGoodInfos, "110164", "1101644002");
	    }
	    // 2- 如果返回值为空，直接返回false
	    else {
	        return;
	    }
	    // 3- 对其他产品进行赋值
	    if ("110154" == allNetProductId) {
	        $("#input_B1101544001").val(attrValue);
	        //changeValue($('#input_B1101544001')[0]);
	    } else if ("110155" == allNetProductId) {
	        $("#input_B1101554001").val(attrValue);
	        //changeValue($('#input_B1101554001')[0]);
	    } else if ("110156" == allNetProductId) {
	        $("#input_B1101564001").val(attrValue);
	        //changeValue($('#input_B1101564001')[0]);
	    }
	    else if ("110160" == allNetProductId) {
	    	//省行业网关短流程云MAS需求中企业代码写死故注掉
	        //$("#input_B1101604037").val(attrValue);
	        //changeValue($('#input_B1101604037')[0]);
	    }
	    else if ("110161" == allNetProductId) {
	        $("#input_B1101614037").val(attrValue);
	        //changeValue($('#input_B1101614037')[0]);
	    }
	    else if ("110162" == allNetProductId) {
	    	//省行业网关短流程云MAS需求中企业代码写死故注掉
	        //$("#input_B1101624037").val(attrValue);
	        //changeValue($('#input_B1101624037')[0]);
	    }
	}

function checkPortRate(productGoodInfos, allNetProductId) {
	    var baseProduct = "";
	    var baseincode = ""; //短信基本接入号属性编码
	    if ("110160" == allNetProductId || "110161" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644009";
	    } else if ("110162" == allNetProductId) {
	        baseProduct = "110164";
	        baseincode = "1101644010";
	    } else {
	        return;

	    }
	    var attrValue = checkAttrSync(productGoodInfos, baseProduct, baseincode);
	    // 3- 对其他产品进行赋值
	    if ("110160" == allNetProductId) {
	        $("#input_B1101604005").val(attrValue);
	        //changeValue($('#input_B1101604005')[0]);
	    } else if ("110161" == allNetProductId) {
	        $("#input_B1101614005").val(attrValue);
	        //changeValue($('#input_B1101614005')[0]);
	    } else if ("110162" == allNetProductId) {
	        $("#input_B1101624005").val(attrValue);
	        //changeValue($('#input_B1101624005')[0]);
	    }
	}
/**
	 * 产品一致性检查
	 * 
	 * @author chenkh
	 * @param productGoodInfos
	 * @param mainMerchId
	 * @param mainAttrCode
	 * @returns 如果为空，则表示取值失败，上级函数返回false，否则返回需要取得的主属性值。
	 */
function checkAttrSync(productOpType, mainMerchId, attrCode) {

	    // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接推出
	    if (productOpType != "1") {
	        return "";
	    }

	    // 3- 获取主产品对应的省内产品编号
	    var proProductId = "";
		Wade.httphandler.submit('',
		            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'getProProductId',
		            '&ALLNET_PRODUCT_ID=' + mainMerchId, function(d) {
			 			proProductId = d.map.result;		            
			 			}, function(e, i) {
		                MessageBox.alert("操作失败");
		                return false;
		            },{async:false});


	    // 4- 获取商品操作类型，根据商品操作类型通过不同途径获取主产品属性
	    var attrvalue = "";
	    if (productOpType == "1") {
	        attrvalue = getAttrValueForGrpOpen(productGoodInfos, proProductId, attrCode);
	    } else if (productOpType == "7") {//修改商品组合
	    	
	        attrvalue = getAttrValueForGrpChg(productGoodInfos, proProductId, attrCode);
	    }

	    // 5- 如果主产品未订购，提示用户先订购主产品，再订购该产品
	    if (attrvalue == "") {
	    	MessageBox.alert("提示","商品主产品还未订购，请先订购商品主产品，否则该页面将无法提交！");
	        return "";
	    }
	    return attrvalue;
	}
/**
	 * @param productGoodInfos
	 * @param proProductId
	 * @param mainAttrCode
	 *            主属性编号
	 * @returns {String} 主属性值
	 */
function getAttrValueForGrpChg(productGoodInfos, proProductId, attrCode) {
	    // 1- 定义返回值
	    var attrValue = "";

	    // 2- 获取商品对象中的产品信息列表	    
	    var productInfoList = new Wade.DatasetList(productGoodInfos.get("SUBOFFERS").toString());
	    if (productInfoList.length == 0) {
	        return number;
	    }

	    // 3- 遍历产品信息列表，获取主产品对应的用户编号
	    var userId = "";
	    for (var i = 0; i < productInfoList.length; i++) {
	        var productInfo = productInfoList.get(i);
	        var productId = productInfo.get("OFFER_CODE");
	        if (productId == proProductId) {
	            userId = productInfo.get("USER_ID");
	            break;
	        }
	    }

	    // 4- 根据主产品用户编号获取主属性
		Wade.httphandler.submit('',
	            'com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.AjaxDataVerify', 'get400NumByUserIdAttrCode',
	            '&USER_ID=' + userId + '&ATTR_CODE=' + attrCode, function(d) {
					attrValue = d.map.result;		            
		 			}, function(e, i) {
	                MessageBox.alert("操作失败");
	                return false;
	            },{async:false});

	    // 5- 返回主属性值
	    return attrValue;
	}

/**
	 * @param productGoodInfos
	 * @param proProductId
	 * @param attrCode
	 *            主属性编号
	 * @returns {String} 主属性值
	 */
function getAttrValueForGrpOpen(productGoodInfos, proProductId, attrCode) {
	    var attrValue = "";

	    // 2- 获取商产品对象中的产品参数对象
	    var productParams = productGoodInfos.get("SUBOFFERS");
	    var productParamList = new Wade.DatasetList();
	    if(productParams){
		    for(var i=0; i<productParams.length; i++){
		    	var subOfferData = productParams.get(i);
		    	var offerCode = subOfferData.get("OFFER_CODE");
			    // 3- 获取产品对应的产品参数
		    	if(offerCode == proProductId){
		    		productParamList = subOfferData.get("OFFER_CHA_SPECS");
		    	}
		    }
	    }


	    if (productParamList == null || productParamList == "undefined") {
	        return attrValue;
	    }
	    
	    // 4- 从对应的产品参数中遍历找出对应的属性值

	    for(var i=0; i<productParamList.length; i++){
	    	var productParam = productParamList.get(i);
	    	if(productParam.get("ATTR_CODE") == attrCode){
	    		
	    		attrValue = productParam.get("ATTR_VALUE");
	    	}
	    }


	    // 5- 返回属性值
	    return attrValue;
	}
	/**
		 * @param null
		 * @returns void 设置省行业网关短流程云MAS服务代码
		 */
	function setServCode1(){
		  // 2- 获取当前产品的操作类型，如果非产品订购或者预订购，直接退出
		  var productOpType = $("#productOperType").val();
		  if(productOpType != "1" && productOpType != "10"){
			  return false;
		  }
		  // 1- 获取当前的产品编号(全网产品编号)
		  var productParamInfoList = new Wade.DatasetList($("#OLD_PRODUCT_PARAMS").val());
		  var allNetProductId = productParamInfoList.get(0,"PRODUCT_ID");
		  var servCode = getRandom1();
		 
		  if(allNetProductId == "110159"){
			  //设置服务代码
			  $("#input_B1101594002").val(servCode);
		      changeValue($('#input_B1101594002')[0]);
		      //设置业务代码
		      var servCodeTemp = servCode.substring(0,9);
		    	var bizCode = "4444444444";
		    	if(servCodeTemp == "106509652"){
					bizCode = "6666666666";
				}else if(servCodeTemp == "106509022"){
					bizCode = "7777777777";
				}else if(servCodeTemp == "106509622"){
					bizCode = "4444444444";
				}else{
					bizCode = "1111111111";
				}
				$('#input_B1101594010').val(bizCode);
				changeValue($('#input_B1101594010')[0]);
		  }
	}
	
