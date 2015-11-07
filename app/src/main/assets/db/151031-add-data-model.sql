CREATE TABLE IF NOT EXISTS FINAL_NOTE (
    ID text,
    TITLE text,
    DESCRIPTION text,
    CREATE_TS DATETIME DEFAULT CURRENT_TIMESTAMP,
    TAG_ID text
)^

CREATE TABLE IF NOT EXISTS FINAL_TAG (
    ID text,
    NAME text
)^

INSERT INTO FINAL_TAG (ID, NAME) VALUES ('f6aa5461-3b41-4123-8eaf-0b6b811f9787', 'Default')^
INSERT INTO FINAL_TAG (ID, NAME) VALUES ('56f99665-44fc-4de3-b68f-1ee5b12a1b3e', 'Job')^

INSERT INTO FINAL_NOTE (ID, TITLE, DESCRIPTION, CREATE_TS, TAG_ID) VALUES ('f6aa5261-3b41-4003-8eaf-0b6b811f9687', 'На работу в четверг', 'Мне нужно в чт на работу, сказали что срочно', CURRENT_TIMESTAMP, '56f99665-44fc-4de3-b68f-1ee5b12a1b3e')^
INSERT INTO FINAL_NOTE (ID, TITLE, DESCRIPTION, CREATE_TS, TAG_ID) VALUES ('56c99665-44cf-4de3-b68f-1ee5bdaa1b3e', 'На тренировку', 'Не забыть бы сходить', CURRENT_TIMESTAMP, NULL)^
INSERT INTO FINAL_NOTE (ID, TITLE, DESCRIPTION, CREATE_TS, TAG_ID) VALUES ('8b1fcb3b-a114-4994-a7b5-1f6955168180', 'Работать лучше дома', 'Сиди и работай дома', CURRENT_TIMESTAMP, 'f6aa5461-3b41-4123-8eaf-0b6b811f9787')^