DELETE ti_b_account_acctday t
WHERE t.sync_sequence=:SYNC_SEQUENCE AND t.modify_tag='9'