$(function(){

	$.searchresult = {	
		onMenuItemTap: function(menuId,modeName,title){
			if(parent.parent.$ && parent.parent.$.menuopener){
				parent.parent.$.menuopener.openMenu(menuId, title, modeName);
			}
			//更新点击次数
			if($.hotmenu){
				$.hotmenu.actions.updateMenu(menuId);
			}
		},
		
		onProductItemTap: function(offerId, offerType,offerName){
			//点击搜索结果中商品的操作
			if(parent.parent.$ && parent.parent.$.menuopener){
				var url,title;
				if(offerType == '10' || offerType == '11' || offerType == '37'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.OperGroupUser", "initial", "");
					title = "商品业务受理";
				}else if(offerType == '12' || offerType == '13'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.OperGroupMember", null, "");
					title = "成员业务受理";
				}else if(offerType == '14'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.EcCampnOper", "initPage", '&TYPE=0');
					title = '集团营销受理';
				}else if(offerType == '20'){
					url = $.redirect.buildUrl("/ordercentre/ordercentre", "oc.enterprise.cs.OpenGroupMember", "initPage", "");
					title = "集团成员开户";
				}
				if(url) {
					parent.parent.$.menuopener.openMenu("", title, url,offerId);
					return ;
				}else{
					title = "商品详情-" + offerName;
					$.ajax.get("ngboss.frame.pc.common.WelcomeNew","fetchProductPage","&offerId="+offerId+"&offerType="+offerType,null,function(data){
						var p = data.get("DETAIL_PAGE");
						if(p && p!=""){
							url = $.redirect.buildUrl("/ordercentre/ordercentre", p, "init", "&OFFER_ID="+offerId+"&PARENT_OFFER_ID="+offerId);
							parent.parent.$.menuopener.openMenu("", title, url,offerId);
						}else{
							alert("商品页面不存在");
						}
					},function(code, info){
						alert('获取商品信息时发生错误：\n' + code + ':' + info);
					});
				}
			}
		}
	}
});
