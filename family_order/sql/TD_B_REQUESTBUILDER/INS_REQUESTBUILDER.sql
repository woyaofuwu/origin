DELETE FROM td_b_requestbuilder WHERE TRADE_TYPE_CODE in ('730','720','711','701') and IN_MODE_CODE='1';

insert into td_b_requestbuilder (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('730', '1', '730', '@{Flag == ''1''}', 'com.ailk.personserv.service.busi.common.changesvcstate.order.buildrequest.BuildBad4ChangeSvcStateRequestData', '1');

insert into td_b_requestbuilder (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('720', '1', '720', '@{Flag == ''1''}', 'com.ailk.personserv.service.busi.common.changesvcstate.order.buildrequest.BuildBad4ChangeSvcStateRequestData', '1');

insert into td_b_requestbuilder (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('711', '1', '711', '@{Flag == ''1''}', 'com.ailk.personserv.service.busi.common.changesvcstate.order.buildrequest.BuildBad4ChangeSvcStateRequestData', '1');

insert into td_b_requestbuilder (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('701', '1', '701', '@{Flag == ''1''}', 'com.ailk.personserv.service.busi.common.changesvcstate.order.buildrequest.BuildBad4ChangeSvcStateRequestData', '1');


DELETE FROM td_b_requestbuilder WHERE TRADE_TYPE_CODE in ('138','139','129','548');

insert into td_b_requestbuilder (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('138', '-1', '138', '', 'com.ailk.personserv.service.busi.common.changesvcstate.order.buildrequest.BuildBad4ChangeSvcStateRequestData
', '1');

insert into td_b_requestbuilder (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('139', '-1', '139', '', 'com.ailk.personserv.service.busi.common.changesvcstate.order.buildrequest.BuildBad4ChangeSvcStateRequestData
', '1');

insert into td_b_requestbuilder (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('129', '-1', '129', '', 'com.ailk.personserv.service.busi.common.changesvcstate.order.buildrequest.BuildBad4ChangeSvcStateRequestData
', '1');

insert into TD_B_REQUESTBUILDER (TRADE_TYPE_CODE, IN_MODE_CODE, ORDER_TYPE_CODE, EXPRESSION, CLASS_NAME, STATE)
values ('548', '-1', '548', '', 'com.ailk.personserv.service.busi.local.saleyearcard.order.buildrequest.BuildCardSale', '1');
