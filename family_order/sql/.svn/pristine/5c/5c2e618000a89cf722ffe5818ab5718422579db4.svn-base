--IS_CACHE=N
select ACCOUNT_ID　ACCT_ID, IP IP_ADDRESS, RSRV_STR1 AREA_CODE, STATE
  from TD_B_ACCOUNT_IP a
 where account_id = (select min(b.account_id)
                    from TD_B_ACCOUNT_IP b
                   where b.state = :STATE
                     and (b.rsrv_str1 is null or b.rsrv_str1 =:AREA_CODE))
   and rownum < 2