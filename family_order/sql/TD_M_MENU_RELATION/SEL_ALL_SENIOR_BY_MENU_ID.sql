--IS_CACHE=Y
select r.CURRENT_MENU_ID MENU_ID
 from (select * from TD_M_MENU_RELATION where RELATION_TYPE = '0') r
 start with  r.CURRENT_MENU_ID = :CURRENT_MENU_ID
 connect by prior r.RELATION_MENU_ID=r.CURRENT_MENU_ID