DELETE FROM td_s_assigntag
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND assign_tag=:ASSIGN_TAG