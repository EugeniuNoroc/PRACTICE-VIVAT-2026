create table task_tags (
                           added_at timestamp(6),
                           tag_id uuid not null,
                           task_id uuid not null,
                           primary key (tag_id, task_id)
);

alter table if exists task_tags
    add constraint FKeiqe3k9ent7icelm1cihqn164
    foreign key (tag_id) references tags;

alter table if exists task_tags
    add constraint FK7xi1reghkj37gqwlr1ujxrxll
    foreign key (task_id) references tasks;