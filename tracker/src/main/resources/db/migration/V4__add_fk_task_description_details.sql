alter table task_description_details
    add constraint fk_details_task
        foreign key (task_id) references tasks(id) on delete cascade;