package ru.tusur.ShaurmaWebSiteProject;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("ru.tusur.ShaurmaWebSiteProject.backend.repo")
class ApplicationConfiguration {}