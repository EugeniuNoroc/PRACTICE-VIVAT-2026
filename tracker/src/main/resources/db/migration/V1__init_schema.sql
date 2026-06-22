create table projects (
                          created_at timestamp(6),
                          id uuid not null,
                          description varchar(255),
                          name varchar(255),
                          primary key (id)
);

create table tags (
                      id uuid not null,
                      color varchar(255),
                      name varchar(255),
                      primary key (id)
);

create table task_description_details (
                                          task_id uuid not null,
                                          markdown_content TEXT,
                                          primary key (task_id)
);

create table tasks (
                       created_at timestamp(6),
                       due_date timestamp(6),
                       updated_at timestamp(6),
                       assignee_id uuid,
                       id uuid not null,
                       project_id uuid not null,
                       title varchar(200) not null,
                       description varchar(255),
                       priority varchar(255) check ((priority in ('LOW','MEDIUM','HIGH','CRITICAL'))),
                       status varchar(255) check ((status in ('TODO','IN_PROGRESS','DONE','CANCELLED'))),
                       primary key (id)
);

create table users (
                       id uuid not null,
                       email varchar(255),
                       username varchar(255),
                       primary key (id)
);

alter table if exists task_description_details
    add constraint FK960tdu8yuca7l1sehrjgenaw
    foreign key (task_id) references tasks;


alter table if exists tasks
    add constraint FKekr1dgiqktpyoip3qmp6lxsit
    foreign key (assignee_id) references users;

alter table if exists tasks
    add constraint FKsfhn82y57i3k9uxww1s007acc
    foreign key (project_id) references projects;