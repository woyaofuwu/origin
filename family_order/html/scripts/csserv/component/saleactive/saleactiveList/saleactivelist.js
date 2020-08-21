if(typeof(SaleActiveList)=="undefined"){
	window["SaleActiveList"]=function(){
		this.beforeSelectedPackageId = "";
	};
	var saleactiveList = new SaleActiveList();
}

 
(function(){
	$.extend(SaleActiveList.prototype,{
		readerComponent: function(param){ 
			$('#SALEACTIVE_LIST_DETAIL').html('');
			this.beforeSelectedPackageId = '';
			param += "&EPARCHY_CODE=" + $('#'+$('#SALEACTIVELIST_EPARCHY_CODE_COMPID').val()).val();
			param += "&SALEACTIVELIST_EPARCHY_CODE_COMPID=" + $('#SALEACTIVELIST_EPARCHY_CODE_COMPID').val();
			$.beginPageLoading("营销活动列表查询中。。。");
			ajaxSubmit(null,null,param,$('#SALEACTIVELIST_COMPONENT_ID').val(),
			function(d){
			    debugger;
			    var allFail = d.get("ALL_FAIL");
			    if(allFail == "true")
			    {
			    	alert('用户不满足参与该活动的条件，详细原因请点击相应营销包！');
			    }else{
				    /**chenxy3 老用戶免费领取4G手机需求*/
				    saleactiveList.checkProdNeedOldCustSn($("#SALE_PRODUCT_ID").val());
			    }
			    //Iphone6活动特殊处理 20141030 
			    var iphone6_x_resultCode = d.get("IPHONE6_X_RESULTCODE");
			    if("undefined"!=typeof(iphone6_x_resultCode)&&"-1"==iphone6_x_resultCode){
			    	$("#IPHONE6_IMEI").val("");
			    	alert(d.get("IPHONE6_X_RESULTINFO"));
			    }
			    var goodsInfo = d.get("GOODS_INFO");
			    if(typeof(goodsInfo)!="undefined"){
			       saleactive.showGoodsInfo(goodsInfo);
			       $('#TERMINAL_DETAIL_INFO').val(goodsInfo.get("TERMINAL_DETAIL_INFO"));
			    }else{
			       saleactive.hiddenGoodsInfo();
			    } 
			    
				$.endPageLoading();
			},
			function(errorcode, errorinfo){
				$.endPageLoading();
				$.showErrorMessage('活动列表查询失败',errorinfo);
			});
			
		},
		/**
		 * 老用户免费领取4G手机业务
		 * 判断产品是否需要录入老用户手机号
		 * chenxy3 2015-09-21
		 * */
		checkProdNeedOldCustSn: function(prodId){ 
			var param="&PRODUCT_ID="+prodId+"&CHECK_TYPE=CHECKPROD"; 
			var serialNumber=$("#AUTH_SERIAL_NUMBER").val();
			ajaxSubmit(null, "checkProdNeedOldCustSn", param, "", function(rtnData){
			   var ifNeed = rtnData.get('IF_NEED');	  
			   if(ifNeed=="Y"){
		           MessageBox.prompt("校验营销活动","请输入用户手机号:" ,function(btn, codeNumber){
		              debugger;
				      if(btn==="ok"){
					     if(codeNumber==null || codeNumber.length==0){alert('请输入用户手机号！');saleactiveList.checkProdNeedOldCustSn(prodId);return;} 
					     if(serialNumber==codeNumber){alert('校验号码不能与办理号码一致！');saleactiveList.checkProdNeedOldCustSn(prodId);return;}
					     var userId = $('#SALEACTIVE_USER_ID').val();
					     var params = '&USER_ID='+userId+'&CHECK_SERIAL_NUMBER='+codeNumber+'&PRODUCT_ID=' + prodId ;
					     
					     var newUserActive=rtnData.get('IF_NEW_USER_ACTIVE');
					     if(newUserActive == "NEW"){
					    	 params = params + '&CHECK_TYPE=NEW_USER_ACTIVE' + '&AUTH_SERIAL_NUMBER=' + serialNumber ;
					    	 ajaxSubmit(null, "checkProdNeedOldCustSn", params, "", 
							         function(ajaxReturnData){
					    		 		/**
					    		 		 * * 新号码校验：
									     * 1、是否办理过该活动   SN_HAVE_PRODUCT  Y:办理过   N:没办理过
									     * 2、0存折不能大于0  SN_FEE_TYPE    Y:大于0   N：不大于0
									     * 3、不能是多终端共享业务数据     SHARE_INFO_TYPE  Y:属于   N:不属于
									     * 4、不能统一付费业务数据。          RELATION_UU_TYPE  Y:属于   N:不属于
									     * 5、与新号码的身份证是否相同      PSPT_ID_SAME Y:相同    N：不相同
									     * 6、新号码存在红海行动营销活动，不能再办理感恩大派送活动      PSPT_ID_SAME Y:相同    N：不相同
									     * 7、老号码作为主卡与新号码办统一付费业务 MAIN_CARD Y:是    N：否
									     * 8、老号码作为主卡与新号码办统一付费业务 FAMILY_CARD Y:是    N：否
									     * 9、老号码作为主卡与新号码办统一付费业务 WIDENET_TYPE Y:是    N：否
					    		 		 * */
							            var haveTag=ajaxReturnData.get("SN_HAVE_PRODUCT");
							            var feeTag =ajaxReturnData.get("SN_FEE_TYPE");
							            var shareTag =ajaxReturnData.get("SHARE_INFO_TYPE");
							            var relaTag=ajaxReturnData.get("RELATION_UU_TYPE");
							            var psptIdTag=ajaxReturnData.get("PSPT_ID_SAME");
							            var open48tag=ajaxReturnData.get("OPEN_48_HOUR");
							            var haveActivetag=ajaxReturnData.get("HAVE_ACTIVE");
							            var mainCardTag=ajaxReturnData.get("MAIN_CARD");
							            var familyTag=ajaxReturnData.get("FAMILY_CARD");
							            var widenetTag=ajaxReturnData.get("WIDENET_TYPE");
							            
							            if(open48tag=="N"){
							            	alert("输入的用户手机号不属于7天内开户的号码，不允许办理该业务！");
							            	saleactiveList.checkProdNeedOldCustSn(prodId);
							            	return;
							            }
							            if(haveTag=="Y"){
						            		alert("输入的号码已经办理过该活动，不能再次办理！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}
						            	if(shareTag=="Y"){
						            		alert("输入的号码办理过多终端共享业务，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}
						            	if(relaTag=="Y"){
						            		alert("输入的号码办理过统一付费业务，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}
						            	if(psptIdTag == "N"){
						            		alert("输入的号码与主号码的身份证不一致，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}
						            	if(feeTag == "Y"){
						            		alert("输入的号码预存款大于0，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}
						            	if(haveActivetag == "Y"){
						            		alert("输入的号码存在红海行动营销活动，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}
							        	if(mainCardTag == "N"){
						            		alert("输入的号码对应的老号码没有作为主卡与新号码办统一付费业务  ，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
							        	}
							        	if(familyTag == "N"){
						            		alert("输入的号码没有办理亲亲网业务  ，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
							        	}
							        	if(widenetTag == "N"){
						            		alert("输入的号码没有办理宽带业务(或预约)  ，不能办理该活动！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
							        	}
						            	alert("校验通过。");
							         }, function(error_code,error_info){
					                    alert(error_info);
					                    $('#SALEACTIVE_LIST_DETAIL').css('display', 'none');
			                      });
					     }else{
					    	 params = params + '&CHECK_TYPE=CHECKSN' ;
						     ajaxSubmit(null, "checkProdNeedOldCustSn", params, "", 
						         function(ajaxReturnData){
						            var checkSnTag=ajaxReturnData.get("SN_HAVE_PRODUCT");
						            var checkSnUse =ajaxReturnData.get("SN_HAVE_USE");
						            if(checkSnTag=="N"){
						            	alert("输入的老用户手机号未办理指定活动，请输入其他号码！");
						            	saleactiveList.checkProdNeedOldCustSn(prodId);
						            	return;
						            }else{
						            	if(checkSnUse=="Y"){
						            		alert("该号码已经被其他号码校验过，请输入新号码校验！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}else if(checkSnUse=="Z"){
						            		alert("老用户证件和新用户证件号码不一致，不能办理！");
						            		saleactiveList.checkProdNeedOldCustSn(prodId);
						            		return;
						            	}else{
						            		alert("成功登记老用户手机号");
						            	}
						            }
						         }, function(error_code,error_info){
				                    alert(error_info);
				                    $('#SALEACTIVE_LIST_DETAIL').css('display', 'none');
		                      });
					     }
				      }else{ 
				    	  $('#SALEACTIVE_LIST_DETAIL').css('display', 'none');
				    	  return;
				      }
		           });
			   }
		   },function(error_code,error_info){alert(error_info);$('#SALEACTIVE_LIST_DETAIL').css('display', 'none');} );
		},
		
		/**
		 * 
		 * 判断产品包是否需要录入老用户手机号
		 * zhangxing3
		 * */
		checkPackNeedOldCustSn: function(prodId,packId,ojb){ 
			var param="&PRODUCT_ID="+prodId+"&PACKAGE_ID="+packId+"&CHECK_TYPE=CHECKPACK";
			var serialNumber=$("#AUTH_SERIAL_NUMBER").val();
			ajaxSubmit(null, "checkProdNeedOldCustSn", param, "", function(rtnData){
			   var ifNeed = rtnData.get('IF_NEED');	 
			   if(ifNeed=="Y"){
		           MessageBox.prompt("校验活动规则","请输入校验用户手机号:" ,function(btn, codeNumber){
		              debugger;
				      if(btn==="ok"){
					     if(codeNumber==null || codeNumber.length==0){alert('请输入校验用户手机号！');saleactiveList.checkPackNeedOldCustSn(prodId,packId);return;} 
					     if(serialNumber==codeNumber){alert('校验号码不能与办理号码一致！');saleactiveList.checkPackNeedOldCustSn(prodId,packId);return;}
					     var userId = $('#SALEACTIVE_USER_ID').val();
					     var params = '&USER_ID='+userId+'&CHECK_SERIAL_NUMBER='+codeNumber+'&PRODUCT_ID=' + prodId+'&PACKAGE_ID=' + packId + '&CHECK_TYPE=CHECKSN' ;
					     ajaxSubmit(null, "checkProdNeedOldCustSn", params, "", 
					         function(ajaxReturnData){
					            var checkSnTag=ajaxReturnData.get("SN_HAVE_PRODUCT");
					            var checkSnUse =ajaxReturnData.get("SN_HAVE_USE");
					            var checkSnMsg =ajaxReturnData.get("SN_MSG");
					            
					            if(checkSnTag=="N"){
					            	alert("输入的校验用户手机号未办理指定活动，请输入其他号码！");
					            	saleactiveList.checkPackNeedOldCustSn(prodId,packId);
					            	return;
					            }else if (checkSnTag=="K") {
					            	alert(checkSnMsg);
					            	saleactiveList.checkPackNeedOldCustSn(prodId,packId);
					            	return;
								}else{
					            	if(checkSnUse=="Y"){
					            		alert("该号码已经被其他号码校验过，请输入新号码校验！");
					            		saleactiveList.checkPackNeedOldCustSn(prodId,packId);
					            		return;
					            	}else if(checkSnUse=="Z"){
					            		alert("办理用户证件和校验用户证件号码不一致，不能办理！");
					            		saleactiveList.checkPackNeedOldCustSn(prodId,packId);
					            		return;
					            	}else{
					            		alert("成功登记校验用户手机号");
					            		saleactiveList.selectSaleActiveAction(ojb);
					            	}
					            }
					         }, function(error_code,error_info){
			                    alert(error_info);
			                    $('#SALEACTIVE_LIST_DETAIL').css('display', 'none');
	                      });
				      }else{ 
				    	  $('#SALEACTIVE_LIST_DETAIL').css('display', 'none');
				    	  return;
				      }
		           });
			   }
			   else{
				   saleactiveList.selectSaleActiveAction(ojb);
			   }
		   },function(error_code,error_info){alert(error_info);$('#SALEACTIVE_LIST_DETAIL').css('display', 'none');} );
		},
		
		checkByPkgId: function(ojb){
			var JQryobj = $(ojb);
			var packageId = JQryobj.attr('packageId');
			var productId = JQryobj.attr('productId');
			var campnType = JQryobj.attr('campnType');
			var userId = $('#SALEACTIVELIST_USER_ID').val();
			var custId = $('#SALEACTIVELIST_CUST_ID').val();
			var errorFlag = $('#SALEACTIVE_ERROR_FLAG').val();
			if(packageId != ""){
				var param = "&PRODUCT_ID=" + productId;
				param += "&USER_ID=" + userId;
				param += "&CUST_ID=" + custId;
				param += "&SPEC_TAG=" + "checkByPkgId";
				param += "&PACKAGE_ID=" + packageId;
				param += "&CAMPN_TYPE=" + campnType;
				param += "&CHECK_TYPE=CHECKPACK";
				var serialNumber=$("#AUTH_SERIAL_NUMBER").val();
				$.beginPageLoading("营销包校验中。。。");
				ajaxSubmit(null,null,param,'saleActiveListHiddenPart', function(d){
				    debugger;
				    if(1==errorFlag)
				    {
				    	saleactiveList.selectSaleActiveAction(ojb);
				    }else{
					    saleactiveList.checkPackNeedOldCustSn(productId,packageId,ojb);
				    }
					$.endPageLoading();							
					
				},
				function(errorcode, errorinfo){
					$.endPageLoading();
					$.showErrorMessage('营销包校验中失败',errorinfo);
				});				
			}
		},

		selectSaleActiveAction: function(obj){
			obj = $(obj);
			var packageId = obj.attr('packageId');
			var productId = obj.attr('productId');
			var campnType = obj.attr('campnType');
			obj.parent().attr("className","on");
			if(this.beforeSelectedPackageId != '')
			{
				$("#"+this.beforeSelectedPackageId).parent().attr("className","");
			}
			this.beforeSelectedPackageId = productId+'_'+packageId;
			var newImei = $("#SALEACTIVE_IMEI").val();
			var deviceModelCode = $("#DEVICE_MODEL_CODE").val();
			//var tminalDetailInfo = $("#TERMINAL_DETAIL_INFO").val();
			//saleactiveModule.readerComponent(packageId, productId, campnType, newImei, tminalDetailInfo);
			
			saleactiveModule.readerComponent(packageId, productId, campnType, newImei, deviceModelCode);
		},
		clearSaleActiveList:function(){
		    $("#SaleActiveListTable_Body").html("");
		}
	});
}
)();