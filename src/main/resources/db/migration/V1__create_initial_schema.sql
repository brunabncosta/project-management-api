CREATE TABLE project (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(150) NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employee (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          cpf VARCHAR(11) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          salary NUMERIC(15, 2) NOT NULL,

                          CONSTRAINT uk_employee_cpf UNIQUE (cpf),
                          CONSTRAINT uk_employee_email UNIQUE (email),
                          CONSTRAINT ck_employee_salary_positive CHECK (salary >= 0)
);

CREATE TABLE project_employee (
                                  project_id BIGINT NOT NULL,
                                  employee_id BIGINT NOT NULL,

                                  CONSTRAINT pk_project_employee
                                      PRIMARY KEY (project_id, employee_id),

                                  CONSTRAINT fk_project_employee_project
                                      FOREIGN KEY (project_id)
                                          REFERENCES project(id)
                                          ON DELETE CASCADE,

                                  CONSTRAINT fk_project_employee_employee
                                      FOREIGN KEY (employee_id)
                                          REFERENCES employee(id)
                                          ON DELETE CASCADE
);