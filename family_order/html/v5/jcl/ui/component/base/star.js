/*!
 * star component
 * http://www.wadecn.com/
 * auth:xiedx@asiainfo.com
 * Copyright 2015, WADE
 */
!function(s,e,n){"use strict";if(s&&"undefined"==typeof s.Star){var a=function(e,a){var t=this;t.el=e&&1==e.nodeType?e:n.getElementById(e),t.el&&t.el.nodeType&&(t.id=s.attr(t.el,"id"))&&(a&&s.isObject(a)&&s.extend(t,a),s.attr(t.el,"x-wade-uicomponent")||s.attr(t.el,"x-wade-uicomponent","star"),t._init(),t.constructor.call(t))};a.prototype=s.extend(new s.UIComponent,{val:function(e){var a=this;if(e==undefined)return a.value;if(/^[0-5]$/gi.test(e)&&e!=a.value){var t=a.span.className=a.span.className?a.span.className:"";t=s.trim((" "+t+" ").replace(/ e_star-\d+ /gi," ")),a.span.className=t+" e_star-"+e,a.value=e,a.el.value=e}},getReadonly:function(){return this.readonly},setReadonly:function(e){var a=this;a.readonly=!!e,setTimeout(function(){var e=a.span.className?a.span.className:"";!a.readonly&&(" "+e+" ").indexOf(" e_star-edit ")<0&&(a.span.className=s.trim(e+" e_star-edit"))},0)},getDisabled:function(){return this.disabled},setDisabled:function(e){var a=this;a.disabled=!!e,setTimeout(function(){var e=a.span.className?a.span.className:"";a.disabled?(" "+e+" ").indexOf(" e_dis ")<0&&(a.span.className=s.trim(e+" e_dis")):(e=s.trim((" "+e+" ").replace(/ e_dis /gi," ")),a.span.className=e)},0)},destroy:function(){this.span=null,this.el=null},_init:function(){var n=this;n.span=s(n.el).parent("span.e_star:first")[0];var e=s.attr(n.span,"id");e||(e=n.id+"_span",s.attr(n.span,"id",e)),s.attr(n.el,"x-visible-element",e),n.readonly&&n.setReadonly(!0),n.disabled&&n.setDisabled(!0),s(n.span).tap(function(e){if(!n.disabled&&!n.readonly){var a=e.target;if(a&&1==a.nodeType&&s.nodeName(a,"span")){var t=s.attr(a,"val");t!=undefined&&(n.val(t),s.event.trigger({type:"change",originalEvent:e.originalEvent},null,n.el))}}})}}),e.Star=s.Star=a}}(window.Wade,window,document);