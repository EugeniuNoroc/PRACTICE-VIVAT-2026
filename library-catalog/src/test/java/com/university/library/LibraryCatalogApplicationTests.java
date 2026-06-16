package com.university.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LibraryCatalogApplicationTests {

	// TODO (W2 review): из-за стартера data-jdbc этот @SpringBootTest требует живой Postgres (резолв диалекта
	//  на старте контекста). Без БД `mvn test` падает -> сборка не самодостаточна. Реши вместе с pom.xml:
	//  либо spring-boot-starter-jdbc (ленивый коннект, contextLoads проходит без БД), либо Testcontainers.
	@Test
	void contextLoads() {
	}

}
