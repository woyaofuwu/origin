--IS_CACHE=Y
select tmpl.* from TD_S_MENU_ATTR_TMPL tmpl where 1 = 1
 and tmpl.MENU_TMPL_ID = :MENU_TMPL_ID
 and tmpl.PARAM_SOURCE_TYPE = :PARAM_SOURCE_TYPE