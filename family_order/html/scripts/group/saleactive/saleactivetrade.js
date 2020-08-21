(function($){
    if(typeof($.SaleActiveTrade=="undefined")){
        $.SaleActiveTrade={
            saleActiveData:{},     
            onTradeSubmit:function(){
                if(!saleactiveModule.saleactiveSubmitJSCheck()) return false;
                if(!($.SaleActiveTrade.checkAuditStaffFile())) return false;
 			   	var saleactiveData = saleactiveModule.getSaleActiveSubmitData();
 			   
 			   	var saleFileStr = $("#SALE_FILE_LIST").val();
 			   	if(saleFileStr != null && saleFileStr != ""){
 			   		var fileList = $.DatasetList();
 			   		var fileIdArray = new Array();
 			   		fileIdArray = saleFileStr.split(',');
 			   		for(var i =0; i<fileIdArray.length; i++){
 			   			var fileIdData = new Wade.DataMap();
 			   			var fileId = fileIdArray[i];
 			   			fileIdData.put("FILE_ID",fileId);
 			   			fileList.add(fileIdData);
 			   		}
 			   		if(fileList.length > 0){
 			   			saleactiveData.put("SALE_FILE_LIST",fileList);
 			   		}
 			   	}
 			   
 			   	var fileObj = $("#VOUCHER_FILE_LIST");
    			if(fileObj && typeof(fileObj) !='undefined' )
    			{
    				var fileValue = fileObj.val();
    				if(fileValue != '' && typeof(fileValue) != 'undefined')
    				{
    					saleactiveData.put("VOUCHER_FILE_LIST",fileValue);
    				}
    			}
    			
    			var staffObj = $("#AUDIT_STAFF_ID");
    			if(staffObj && typeof(staffObj) !='undefined' )
    			{
    				var staffValue = staffObj.val();
    				if(staffValue != '' && typeof(staffValue) != 'undefined')
    				{
    					saleactiveData.put("AUDIT_STAFF_ID",staffValue);
    				}
    			}
    			
 			   var param = '&SERIAL_NUMBER='+$("#SERIAL_NUMBER").val();
 			   param += '&SALEACITVEDATA='+saleactiveData.toString();
 			   var netOrderId = $("#NET_ORDER_ID").val();
 			   if(netOrderId!="undefined" && ""!=netOrderId){
 			      param +='&NET_ORDER_ID='+netOrderId;
 			   }
 			   $.cssubmit.addParam(param);
 			   var checkParam = "";
 			   checkParam += '&PACKAGE_ID_A='+saleactiveData.get('PACKAGE_ID');
 			   checkParam += '&PRODUCT_ID_A='+saleactiveData.get('PRODUCT_ID');
 			   if(saleactiveData.get('OTHER_NUMBER','') != ''){
 				  checkParam += '&OTHER_NUMBER=' + saleactiveData.get('OTHER_NUMBER','');
 			   }
 			   $.tradeCheck.addParam(checkParam);
 			   return true;
            },
            displaySwitch:function(btn, o){
               var button = $(btn);
	           var div = $('#'+o);
	           if (div.css('display') != "none")
	           {
				  div.css('display', 'none');
				  button.children("i").attr('className', 'e_ico-unfold'); 
				  button.children("span:first").text("展示客户信息");
	           }else {
		          div.css('display', '');
		          button.children("i").attr('className', 'e_ico-fold'); 
		          button.children("span:first").text("不展示客户信息");
	           }
            },          
            selectGrpAfterAction:function(data){
            	
            	//设置集团信息
            	if(data==null || typeof(data)=="undefined"){
                    data = $.SaleActiveTrade.saleActiveData
                 };
            	var grpUserInfo = data.get("GRP_USER_INFO");
              	var grpCustInfo = data.get("GRP_CUST_INFO");
              	insertGroupCustInfo(grpCustInfo);
              	insertGroupUserInfo(grpUserInfo);
              	$('#USER_ID').val(grpUserInfo.get('USER_ID'));
              	$('#SERIAL_NUMBER').val(grpUserInfo.get('SERIAL_NUMBER'));
              	$('#GROUP_ID').val(grpCustInfo.get('GROUP_ID'));
              	$('#GRP_PRODUCT_ID').val(grpUserInfo.get('PRODUCT_ID'));
              	$('#custinfo_EPARCHY_CODE').val(grpUserInfo.get('EPARCHY_CODE'));
              	
                //清空树和营销详情         	
                groupPackageTree.empty(true);
                $("#detailContent").css('display', 'none');
                $("#cancel_file1").css('display', 'none');
                $('#SALE_PRODUCT_ID').val("");
                $('#CAMPN_TYPE').val("");
                saleactiveModule.clearSaleActive();
            	// 校验集团用户是否欠费
            	/*
            	var userId = grpUserInfo.get('USER_ID');
            	$.beginPageLoading();
            	$.ajax.submit(this, 'checkUserFee', '&USER_ID='+userId, '', function(data){	
            		$.endPageLoading();
            		var resultcode = data.get('FLAG',true);
            		var message = data.get('ERROR_MESSAGE','0');
               		if(resultcode=='false'){   	
               			$.showWarnMessage("系统提示",message);
                      	$("#CAMPN_TYPE").attr('disabled', true);
               			return false;
               		}		
               		
               		$.beginPageLoading();
                	$.ajax.submit(this, 'queryCampType', '', 'CampTypePart', function(data){	
                		$.endPageLoading();		
                		$("#CAMPN_TYPE").attr('disabled', false);
                      	$("#SALE_PRODUCT_ID").attr('disabled', false);
                	},
                	function(error_code,error_info,derror){
                		$.endPageLoading();
                		showDetailErrorInfo(error_code,error_info,derror);
                    });
            	},
            	function(error_code,error_info,derror){
            		$.endPageLoading();
            		showDetailErrorInfo(error_code,error_info,derror);
            		return false;
                });
                */
                
                $.beginPageLoading();
               	$.ajax.submit(this, 'queryCampType', '', 'CampTypePart', function(data){	
					$.endPageLoading();		
               		$("#CAMPN_TYPE").attr('disabled', false);
                  	$("#SALE_PRODUCT_ID").attr('disabled', false);
               	},
               	function(error_code,error_info,derror){
               		$.endPageLoading();
               		showDetailErrorInfo(error_code,error_info,derror);
                });
                    
                $.beginPageLoading();
            	$.ajax.submit(this, 'queryUserAcctInfo', '&USER_ID='+grpUserInfo.get('USER_ID'), 'ProductPart', function(data){	
            		$.endPageLoading();		
            		var acctDay = data.get("ACCTDAY");
            		$("#ACCTDAY").val(acctDay);
            	},
            	function(error_code,error_info,derror){
            		$.endPageLoading();
            		showDetailErrorInfo(error_code,error_info,derror);
            		$("#CAMPN_TYPE").attr('disabled', true);
                  	$("#SALE_PRODUCT_ID").attr('disabled', true);
                });

              	$("#CAMPN_TYPE").attr('disabled', false);
              	$("#SALE_PRODUCT_ID").attr('disabled', false);     
              	
            },
            selectGrpErrAfterAction:function(){              
              clearGroupCustInfo();
              clearGroupUserInfo();
              //清空树和营销详情
              groupPackageTree.empty(true);
              $("#detailContent").css('display', 'none');
              $('#SALE_PRODUCT_ID').val("");
              $('#CAMPN_TYPE').val("");
              saleactiveModule.clearSaleActive();
              $("#CAMPN_TYPE").attr('disabled', true);
              $("#SALE_PRODUCT_ID").attr('disabled', true);
              $("#cancel_file1").css('display', 'none');
            },
            changeCampnType:function(){
                var campnType = $("#CAMPN_TYPE").val();  //成员号码   
                if(campnType == ''){
                   alert("活动类型不能为空");
                   return false;
                }
                //清空树和营销详情
                groupPackageTree.empty(true);
                $("#detailContent").css('display', 'none');
                $("#cancel_file1").css('display', 'none');
                saleactiveModule.clearSaleActive();
                //查询营销活动方案
            	$.beginPageLoading();
            	$.ajax.submit(this, 'queryCampnNames', '&CAMPN_TYPE='+campnType, 'ProductPart', function(data){	
            		$.endPageLoading();		
            		$("#SALE_PRODUCT_ID").attr('disabled', false);
            	},
            	function(error_code,error_info,derror){
            		$.endPageLoading();
            		showDetailErrorInfo(error_code,error_info,derror);
                });
            },
            changeCampn:function(){
            
				// 校验集团用户是否欠费
            	var userId = $('#USER_ID').val();
            	var selectedSaleProductId = $("#SALE_PRODUCT_ID").val();
            	$.beginPageLoading();
            	$.ajax.submit(this, 'checkUserFee', '&USER_ID=' + userId + '&SALE_PRODUCT_ID=' + selectedSaleProductId, '', function(data){	
            		$.endPageLoading();
            		var resultcode = data.get('FLAG',true);
            		var message = data.get('ERROR_MESSAGE','0');
               		if(resultcode=='false'){   	
               			$.showWarnMessage("系统提示",message);
                      	//$("#CAMPN_TYPE").attr('disabled', true);
                      	//$("#SALE_PRODUCT_ID").attr('disabled', true);
                      	groupPackageTree.empty(true);
	            		saleactiveModule.clearSaleActive();	
	            		$("#detailContent").css('display', 'none');
               			return false;
               		}		


                    //首先清空树
	            	groupPackageTree.empty(true);
	            	saleactiveModule.clearSaleActive();	
	            	$("#detailContent").css('display', 'none');
	            	// 校验营销活动产品是否可选
	            	var productId = $('#SALE_PRODUCT_ID').val();
	            	var groupId = $('#GROUP_ID').val();
	            	var grpProductId = $('#GRP_PRODUCT_ID').val();
	            	$.beginPageLoading();
	            	$.ajax.submit(this, 'checkByProduct', '&PRODUCT_ID='+productId+'&USER_ID='+userId+'&GRP_PRODUCT_ID='+grpProductId+'&GROUP_ID='+groupId, '', function(data){	
	            		$.endPageLoading();
	            		var resultcode = data.get('FLAG',true);
	            		var message = data.get('ERROR_MESSAGE','0');
	               		if(resultcode=='false'){   	
	               			$.showWarnMessage("系统提示",message);
	               			return false;
	               		}
	                	$.SaleActiveTrade.queryPackages();
	                	if(productId == '69901002'){
	                		$("#cancel_file1").css('display', 'block');
	                	} else {
	                		$("#cancel_file1").css('display', 'none');
	                	}
	            	},
	            	function(error_code,error_info,derror){
	            		$.endPageLoading();
	            		showDetailErrorInfo(error_code,error_info,derror);
	            		$("#cancel_file1").css('display', 'none');
	                });
                
            	},
            	function(error_code,error_info,derror){
            		$.endPageLoading();
            		showDetailErrorInfo(error_code,error_info,derror);
            		return false;
                });
            },
            queryPackages:function(){
            	groupPackageTree.empty(true);
            	groupPackageTree.setParam('MAIN_PRODUCT_ID',$('#SALE_PRODUCT_ID').val());
            	groupPackageTree.setParam('METHOD_NAME','loadGroupProductPackageTree');
            	groupPackageTree.init();
            },
            selectSaleActiveAction:function(data){
            	var nodeid = data.id;
            	if(nodeid.indexOf('node_')==0){
	            	var product_package=nodeid.replace("node_","").split('^');
	            	if(product_package.length != 2) return false;
	            	var productId = product_package[0];
	            	var packageId = product_package[1];
	            	$('#PACKAGE_ID').val(packageId);
	            	var campnType = $("#CAMPN_TYPE").val();
	            	var userId = $('#USER_ID').val();
	            	var serialNumber = $('#SERIAL_NUMBER').val();
	            	var eparchyCode = $('#custinfo_EPARCHY_CODE').val();
	            	
	            	$("#saleactiveModule").attr('packageId', packageId);
	            	$("#saleactiveModule").attr('productId', productId);
	            	var acctDay = $("#ACCTDAY").val();
	            	var param = '&EPARCHY_CODE='+eparchyCode+'&PRODUCT_ID='+productId+'&PACKAGE_ID='+packageId+'&CAMPN_TYPE='+campnType+'&USER_ID='+userId+'&SERIAL_NUMBER='+serialNumber+'&ACCTDAY='+acctDay;
	    			saleactiveModule.readerComponent(param,userId);
	    			$("#detailContent").css('display', '');
            	}
    		},
    		checkAuditStaffFile:function(){
    			var fileObj = $("#VOUCHER_FILE_LIST");
    			if(fileObj && typeof(fileObj) !='undefined' )
    			{
    				var fileValue = fileObj.val();
    				if(fileValue == '')
    				{
    					alert("请上传凭证附件！");
    					return false;
    				}
    			}
    			var staffObj = $("#AUDIT_STAFF_ID");
    			if(staffObj && typeof(staffObj) !='undefined' )
    			{
    				var staffValue = staffObj.val();
    				if(staffValue == '')
    				{
    					alert("请选择稽核员！");
    					return false;
    				}
    			}
    			return true;
    		}
        }
    }
})(Wade);

