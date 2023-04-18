CREATE TABLE event_types(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE request_types(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE request_states(
  id serial PRIMARY KEY,
  name varchar(255) NOT NULL
);

CREATE TABLE closing_statuses(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE levels_of_edu(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE profiles(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE faculties(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE forms_of_edu(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE task_statuses(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL
);

CREATE TABLE chapters(
    id serial PRIMARY KEY,
    serial_number int NOT NULL,
    name varchar(255) NOT NULL
);

CREATE TABLE blocks(
    id serial PRIMARY KEY,
    serial_number int NOT NULL,
    name varchar(255) NOT NULL,
    chapter_id int NOT NULL,

    FOREIGN KEY (chapter_id) REFERENCES chapters (id)
);

CREATE TABLE tasks(
    id serial PRIMARY KEY,
    serial_number int NOT NULL,
    name varchar(255) NOT NULL,
    description text  NOT NULL,
    manual_check_required boolean  NOT NULL,
    block_id int NOT NULL,

    FOREIGN KEY (block_id) REFERENCES blocks (id)
);

CREATE TABLE logically_related_previous_tasks(
    id serial PRIMARY KEY,
    task_id int NOT NULL,
    previous_task_id int NOT NULL,

    FOREIGN KEY (task_id) REFERENCES tasks (id),
    FOREIGN KEY (previous_task_id) REFERENCES tasks (id)
);

CREATE TABLE users(
    id serial PRIMARY KEY,
    name varchar(255) NOT NULL,
    lastname varchar(255) NOT NULL,
    patronymic varchar(255) NOT NULL,
    email varchar(255) UNIQUE NOT NULL,
    password varchar(255) NOT NULL,
    registration_date timestamp NOT NULL,
    role varchar(255) NOT NULL
);

CREATE TABLE groups(
    id serial PRIMARY KEY,
    level_of_edu_id int NOT NULL,
    profile_id int NOT NULL,
    faculty_id int NOT NULL,
    form_of_edu_id int NOT NULL,
    course_number int NOT NULL,
    group_number int NOT NULL,
    year int NOT NULL,
    teacher_id int NOT NULL,

    FOREIGN KEY (level_of_edu_id) REFERENCES levels_of_edu (id),
    FOREIGN KEY (profile_id) REFERENCES profiles (id),
    FOREIGN KEY (faculty_id) REFERENCES faculties (id),
    FOREIGN KEY (form_of_edu_id) REFERENCES forms_of_edu (id),
    FOREIGN KEY (teacher_id) REFERENCES users (id)
);

ALTER TABLE users ADD group_id int;
ALTER TABLE users
    ADD CONSTRAINT fk_users_groups FOREIGN KEY (group_id) REFERENCES groups (id);

CREATE TABLE student_tasks(
    id serial PRIMARY KEY,
    user_id int NOT NULL,
    task_id int NOT NULL,
    curr_status_id int NOT NULL,

    FOREIGN KEY (task_id) REFERENCES tasks (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (curr_status_id) REFERENCES task_statuses (id)
);

CREATE TABLE task_statuses_history(
    id serial PRIMARY KEY,
    student_task_id int NOT NULL,
    old_status_id int NOT NULL,
    new_status_id int NOT NULL,
    change_time timestamp NOT NULL,

    FOREIGN KEY (student_task_id) REFERENCES student_tasks (id),
    FOREIGN KEY (old_status_id) REFERENCES task_statuses (id),
    FOREIGN KEY (new_status_id) REFERENCES task_statuses (id)
);

CREATE TABLE requests(
    id serial PRIMARY KEY,
    student_task_id int NOT NULL,
    teacher_id int NOT NULL,
    type_id int NOT NULL,
    state_id int NOT NULL,
    student_message text,
    creation_time timestamp NOT NULL,
    teacher_message text,
    closing_time timestamp,
    closing_status_id int,

    FOREIGN KEY (student_task_id) REFERENCES student_tasks (id),
    FOREIGN KEY (teacher_id) REFERENCES users (id),
    FOREIGN KEY (type_id) REFERENCES request_types (id),
    FOREIGN KEY (state_id) REFERENCES request_states (id),
    FOREIGN KEY (closing_status_id) REFERENCES closing_statuses (id)
);

CREATE TABLE event_history(
    id serial PRIMARY KEY,
    type_id int NOT NULL,
    request_id int NOT NULL,
    time timestamp NOT NULL,

    FOREIGN KEY (type_id) REFERENCES event_types (id),
    FOREIGN KEY (request_id) REFERENCES requests (id)
);


CREATE TABLE years(
     id serial PRIMARY KEY,
     name int NOT NULL
);

ALTER TABLE groups ADD year_id int;
ALTER TABLE groups
    ADD CONSTRAINT fk_groups_years FOREIGN KEY (year_id) REFERENCES years (id);
---------------------------

INSERT INTO event_types (name) VALUES ('Студент отправил на рассмотрение');
INSERT INTO event_types (name) VALUES ('Студент отправил на проверку');
INSERT INTO event_types (name) VALUES ('Преподаватель принял решение');
INSERT INTO event_types (name) VALUES ('Преподаватель отклонил решение');

INSERT INTO request_types (name) VALUES ('На рассмотрение');
INSERT INTO request_types (name) VALUES ('На проверку');

INSERT INTO request_states (name) VALUES ('Не просмотрен');
INSERT INTO request_states (name) VALUES ('Просмотрен');
INSERT INTO request_states (name) VALUES ('Обработан');
INSERT INTO request_states (name) VALUES ('Отменён');

INSERT INTO closing_statuses (name) VALUES ('Решение принято');
INSERT INTO closing_statuses (name) VALUES ('Решение отклонено');

INSERT INTO task_statuses (name) VALUES ('Не решена');
INSERT INTO task_statuses (name) VALUES ('На проверке');
INSERT INTO task_statuses (name) VALUES ('Не рассмотрении');
INSERT INTO task_statuses (name) VALUES ('Возвращена преподавателем');
INSERT INTO task_statuses (name) VALUES ('Решена');
INSERT INTO task_statuses (name) VALUES ('На тестировании');
INSERT INTO task_statuses (name) VALUES ('Не прошла тесты');
INSERT INTO task_statuses (name) VALUES ('Прошла тесты');

INSERT INTO levels_of_edu (name) VALUES ('Бакалавриат');
INSERT INTO levels_of_edu (name) VALUES ('Магистратура');

INSERT INTO profiles (name) VALUES ('1');
INSERT INTO profiles (name) VALUES ('2');
INSERT INTO profiles (name) VALUES ('3');
INSERT INTO profiles (name) VALUES ('Без профиля');

INSERT INTO faculties (name) VALUES ('ИФСТ');
INSERT INTO faculties (name) VALUES ('ПИНФ');
INSERT INTO faculties (name) VALUES ('ПИНЖ');

INSERT INTO forms_of_edu (name) VALUES ('Очная');
INSERT INTO forms_of_edu (name) VALUES ('Заочная');

ALTER TABLE blocks ADD text_theory text;