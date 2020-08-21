SELECT COUNT(1) recordcount
  FROM tf_f_user_mbmp_plus
   WHERE user_id=:USER_ID
     And partition_id=Mod(:USER_ID,10000)
      And biz_type_code='98'
        And info_code=:INFO_CODE
          And (info_value=:INFO_VALUE OR :INFO_VALUE = '*')