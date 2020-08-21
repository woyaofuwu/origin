(function($){
   if(typeof($.ShoppingCart)=="undefined"){$.ShoppingCart={
        loadShoppingCart:function(data){
	       if(!data) return;
	       $("#TRADE_BUTTON").attr("disabled",false);
	       $("#BARCODE_ID").attr("disabled",false);
	       document.getElementById("CLEAN_SHOPPING").disabled=false;
           var userInfo = data.get("USER_INFO");
           var custInfo = data.get("CUST_INFO");
           if(!userInfo||!custInfo) return;
           var params = "&USER_ID="+userInfo.get("USER_ID")+"&CUST_ID="+custInfo.get("CUST_ID");
               params += "&CUST_NAME="+custInfo.get("CUST_NAME")+"&PSPT_ID="+custInfo.get("PSPT_ID");
               params += "&ROUTE_EPARCHY_CODE="+userInfo.get("EPARCHY_CODE");
	           $.ajax.submit('', 'loadShoppingCart', params, 'shoppingCartPart,shoppingCartTree,shoppingFeeList', 
	                         function(data){
	                            /*var treeNodes = $("a[class=ico]");
	                            for(var x=0; x<treeNodes.length; x++){
	                               treeNodes[x].click();
	                            }*/
	                         },
	                         function(error_code,error_info){
	                             $.endPageLoading();alert(error_info);
	                         });
	    },
	    refreshShoppingCart:function(){
	    
	    },
	    cleanShoppingCart:function(){
	        if(!$.ShoppingCart.treeTable.checkClean()) return;
	        var param = "&SHOPPING_CART_ID="+$("#SHOPPINGCART_ID").html();
	        var user=$.auth.getAuthData().get("USER_INFO");
  	        param += "&ROUTE_EPARCHY_CODE="+user.get("EPARCHY_CODE");
  	        $.ajax.submit('', 'cleanShoppingCart', param, 'shoppingCartTree', 
  	                      function(data){
  	        				document.getElementById("CLEAN_SHOPPING").disabled=true;
                              alert("购物车已经清空！");
	                      },
	                      function(error_code,error_info){
		                      $.endPageLoading();
		                      alert(error_info);
		                  }
            );
            $.ShoppingCart.dealFee.cleanShoppingFeeList();
	    },
	    submitShoppingCart:function(){
	        var data = $.ShoppingCart.treeTable.getTreeTableDataAll();
	        var param = "&SHOPPINGCART_ID="+$("#SHOPPINGCART_ID").html();
			if(data&&data.length>0){
				param += "&SHOPPINGCART_ELEMENTS="+data;
				$.cssubmit.addParam(param);
				return true;
			}
			alert("购物车为空，不能提交！");
	    },
	    treeTable:{
	       getTreeTableDataAll:function(){
              var tableObj = $.treetable.get("shoppingCartTreeTable");
              return tableObj.getTableData(null,true);
           },
           checkDelete:function(rowData){
              var shoppingStateCode = rowData.get("shoppingStateCode");
              if("D"==shoppingStateCode){
  	             alert("该业务操作的购物车状态已为“删除”！");
  	             return false;
  	          }
  	          //TODO call breCheck
  	          if(window.confirm("业务删除后，只能重新办理！确认删除吗？")){
  	             return true;
  	          }
  	          return false;
           },
           checkClean:function(){
              if(window.confirm("购物车清空后，只能重新办理业务！确认清空吗？")){
  	             return true;
  	          }
  	          return false;
           },
           deleteRow:function(a){
              var row = a.parentNode.parentNode;
              var objTable = $.treetable.get("shoppingCartTreeTable");
              objTable.clickRow(row);
  	          var rowIndex = objTable.getTable().attr("selected");
  	          var rowData = objTable.getRowData(null, rowIndex);
  	          var elementTypeCode = rowData.get("elementTypeCode");
  	          var busiOrderId = rowData.get("busiOrderId");
  	          var fee = rowData.get("fee");
  	          if(!$.ShoppingCart.treeTable.checkDelete(rowData)) return;
  	          if("T"==elementTypeCode){
  	              var param = "&ORDER_ID="+busiOrderId;
  	              var user=$.auth.getAuthData().get("USER_INFO");
  	              param += "&ROUTE_EPARCHY_CODE="+user.get("EPARCHY_CODE");
  	              $.ajax.submit('', 'deleteOrderRow', param, '', 
  	                      function(data){
  	                          var delNum = data.get(0).get("DEL_NUM");
  	                          if(delNum=="0"){
  	                            alert("删除失败！");return;
  	                          }
  	                          var tableData = $.ShoppingCart.treeTable.getTreeTableDataAll();
  	                          if(fee!=""&&fee!="0"){
  	                             $.ShoppingCart.dealFee.dealFeeByDeleteOrder(fee);
  	                          }
  	                          if(tableData.length>0) {
                                 tableData.each(function(item,index,totalCount){
                                     var orderId = item.get("busiOrderId");
                                     var subIndex = item.get("rowIndex");
                                     var tempIndex = subIndex.split(".");
                                     var subStr = 0;
                                     if(tempIndex && tempIndex.length>1){subStr = parseInt(tempIndex[1])}
                                     if(busiOrderId == orderId)
                                     {
                                        item.shoppingStateCode="D";
  	                                    item.shoppingState="删除";
  	                                    item.feeModeDesc="";
  	                                    item.feeTypeDesc="";
  	                                    item.fee="";
  	                                    if("T"==item.get("elementTypeCode")){item.fee="0";}
  	                                    var finalIndex = parseInt(rowIndex)+subStr;
  	                                    objTable.updateRow(item,finalIndex);
  	                                 }
                                  });
                              }
                              alert("删除成功！");
	                      },
	                      function(error_code,error_info){
		                      $.endPageLoading();
		                      alert(error_info);
		                  }
                   );
  	          }else{
  	             var user=$.auth.getAuthData().get("USER_INFO");
  	             var param = "&TRADE_ID="+rowData.get("busiTradeId");
  	             param += "&BUSI_OPER_TABLE="+rowData.get("busiOperTable");
  	             param += "&ROUTE_EPARCHY_CODE="+user.get("EPARCHY_CODE");
  	             $.ajax.submit('', 'deleteItemRow', param, '', 
  	                      function(data){
  	                          var delNum = data.get(0).get("DEL_NUM");
  	                          if(delNum=="0"){
  	                            alert("删除失败！");return;
  	                          }
  	                          $.ShoppingCart.dealFee.dealFeeByDeleteElement(fee,rowIndex,rowData);
		                      rowData.shoppingStateCode="D";
  	                          rowData.shoppingState="删除";
  	                          rowData.feeModeDesc="";
  	                          rowData.feeTypeDesc="";
  	                          rowData.fee="";
  	                          objTable.updateRow(rowData,rowIndex);
  	                          alert("删除成功！");
	                      },
	                      function(error_code,error_info){
		                      $.endPageLoading();
		                      alert(error_info);
		                  }
                 );
  	          }
           },
           cleanTreeTable:function(){
              var objTable = $.treetable.get("shoppingCartTreeTable");
              objTable.cleanRows();
              var data = objTable.getTableData(null,true);
              alert(data);
           },
           restore:function(a){
           
           }
	   },
	   barCode:{
	       scanbarcode:function(){
	          var user=$.auth.getAuthData().get("USER_INFO");
	          var param = "&SERIAL_NUMBER="+user.get("SERIAL_NUMBER");
	          if(!$.validate.verifyAll('testPart')) {
	      		return false;
	      	  }
	          var barcodeId=parseInt($("#BARCODE_ID").val(),10);
	          $("#BARCODE_ID").val(barcodeId);
	          param += "&BARCODE_ID="+$("#BARCODE_ID").val();
	          param += "&ROUTE_EPARCHY_CODE="+user.get("EPARCHY_CODE");
  	          $.ajax.submit('', 'tradeByBarcodeCheck', param, 'goodsInfoPart', 
  	                      function(dataset){
                              if(dataset&&dataset.length>0){
                                 $.ShoppingCart.barCode.popResInfo();
                              }else{
                                 $.beginPageLoading("扫描受理中，请勿重复扫描。。。");
                                 $.ShoppingCart.barCode.barcodeTrade();
                              }
	                      },
	                      function(error_code,error_info){
		                      $.endPageLoading();
		                      alert(error_info);
		                  }
              );
	       },
	       selectGoods:function(obj){
	          var goodsId = obj.value;
	          var user=$.auth.getAuthData().get("USER_INFO");
	          var param = "&GOODS_ID="+goodsId;
	          param += "&ROUTE_EPARCHY_CODE="+user.get("EPARCHY_CODE");
	          $.ajax.submit('', 'selectGoods', param, null, 
  	                      function(dataset){
                              if(dataset&&dataset.length>0){
                                 var goodsInfo = dataset.get(0);
                                 var resTag = goodsInfo.get("RES_TAG");
                                 if("0"==resTag){
                                    $.closeExternalPopupDiv('goodsInfoPart');
                                    $.beginPageLoading("扫描受理中，请勿重复扫描。。。");
                                    $.ShoppingCart.barCode.barcodeTrade();
                                 }else{
                                    $("#resCode").attr("resTypeCode",goodsInfo.get("RES_TYPE_CODE"));
                                    $("#resCode").attr("resId",goodsInfo.get("RES_ID"));
                                    $("#resCode").attr("resCheck",goodsInfo.get("RES_CHECK"));
                                    alert("请录入资源编码，点击[确定]后受理！");
                                 }
                              }
	                      },
	                      function(error_code,error_info){
		                      $.endPageLoading();
		                      alert(error_info);
		                  }
              );
	       },
	       checkRes:function(obj){
	          //调用营销活动接口办理，资源校验和受理放到一起。
	          var data=$.DataMap();
	          data.put("RES_ID",$("#resCode").attr("resId"));
	          data.put("RES_CODE",$("#resCode").val());
	          $.closeExternalPopupDiv('goodsInfoPart');
	          $.beginPageLoading("受理中，请勿重复扫描。。。");
	          $.ShoppingCart.barCode.barcodeTrade(data);
	       },
	       barcodeTrade:function(inputParam){
	          var user=$.auth.getAuthData().get("USER_INFO");
	          var param = "&SERIAL_NUMBER="+user.get("SERIAL_NUMBER");
	          param += "&BARCODE_ID="+$("#BARCODE_ID").val();
	          param += "&ROUTE_EPARCHY_CODE="+user.get("EPARCHY_CODE");
	          param += "&SUBMIT_TYPE=addShoppingCart";
	          if(inputParam && inputParam.length>0){
	             inputParam.eachKey(function(key, index, totalCount){
					param += "&"+key+"="+inputParam.get(key);
				 });
	          }
  	          $.ajax.submit('', 'tradeByBarcode', param, '', 
  	                      function(data){
                              var user=$.auth.getAuthData().get("USER_INFO");
	                          $.ShoppingCart.barCode.loadShoppingCartSimple(user);
	                          document.getElementById("CLEAN_SHOPPING").disabled=false;
	                      },
	                      function(error_code,error_info){
		                      $.endPageLoading();
		                      alert(error_info);
		                  }
              );
	       },
	       loadShoppingCartSimple:function(data){
		       if(!data) return;
	           var params = "&USER_ID="+data.get("USER_ID");
	               params += "&ROUTE_EPARCHY_CODE="+data.get("EPARCHY_CODE");
		           $.ajax.submit('', 'loadShoppingCart', params, 'shoppingCartPart,shoppingCartTree,shoppingFeeList', 
		                         function(data){
		                            /*var treeNodes = $("a[class=ico]");
		                            for(var x=0; x<treeNodes.length; x++){
		                               treeNodes[x].click();
		                            }*/
		                            $.endPageLoading();
		                         },
		                         function(error_code,error_info){
		                             $.endPageLoading();alert(error_info);
		                         });
	       },
	       popResInfo:function(){
	           $("#ResTextId").val("");
	           popupDiv('goodsInfoPart',600,'实物信息',true);
	       }
	   },
	   dealFee:{
	       dealFeeByDeleteOrder:function(fee){
	          debugger;
	          $.ShoppingCart.dealFee.updateShoppingFeeList(fee);
	       },
	       dealFeeByDeleteElement:function(fee,rowIndex,rowData){
	          debugger;
	          var deleteIndex = rowData.get("rowIndex");
              var tempIndexArr = deleteIndex.split(".");
              var tempIndex = "";
              if(tempIndexArr && tempIndexArr.length>1){
                  tempIndex = parseInt(tempIndexArr[1]);
              }
              var parentRowIndex = parseInt(rowIndex)-parseInt(tempIndex);
              var objTable = $.treetable.get("shoppingCartTreeTable");
              var parentRowData = objTable.getRowData(null, parentRowIndex);
              var thisFee = rowData.get("fee");
              var parentFee = parentRowData.get("fee");
              if(thisFee!=""&&thisFee!="0"){
                  parentFee = parseInt(parentFee) - parseInt(thisFee);
                  parentRowData.fee=parentFee;
                  objTable.updateRow(parentRowData,parentRowIndex);
                  $.ShoppingCart.dealFee.updateShoppingFeeList(thisFee);
              }
	       },
	       updateShoppingFeeList:function(fee){
	          var leaveShoppingFee = parseInt($("#SHOPPING_FEE").val()) - parseInt(fee);
	          $("#SHOPPING_FEE").val(leaveShoppingFee);
	          $("#FREE_FEE").val("0");
	          $("#PAY_FEE").val(leaveShoppingFee);
	          $("#FACT_PAY_FEE").val(leaveShoppingFee);
	       },
	       cleanShoppingFeeList:function(){
	          $("#SHOPPING_FEE").val("0");
	          $("#FREE_FEE").val("0");
	          $("#PAY_FEE").val("0");
	          $("#FACT_PAY_FEE").val("0");
	       }
	   }	  
   }};
})(Wade);

$(document).ready(function(){
   $.table.fn.selectedRow = function (c, f){
        var e = $.table.fn.get(c);
	    var b = e.getTable().attr("selected");
	    if (b == null || b == "-1") {
		    //a(f).attr("class", "on");
	    } else {
		    var d = e.getSelected();
		    if (d && d.length) {
			    if (d.attr("raw_class")) {
				    d.attr("class", d.attr("raw_class"));
			    } else {
				    d.attr("class", "");
			    }
		    }
		    //a(f).attr("class", "on");
	    }
	    e.getTable().attr("selected", f.rowIndex);
   };
});

