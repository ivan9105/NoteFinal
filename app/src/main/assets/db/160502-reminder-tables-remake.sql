DROP TABLE FINAL_REMINDER_DATE^
DROP TABLE FINAL_REMINDER_DAY^
DROP TABLE FINAL_REMINDER_HOUR^
DROP TABLE FINAL_REMINDER_NOTE^

CREATE TABLE IF NOT EXISTS FINAL_REMINDER_NOTE (
    ID text,
    LOOP INTEGER,
    TYPE text,
    DESCRIPTION text,
    SETTING_XML text
)^