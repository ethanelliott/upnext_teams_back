package team.upnext.upnextteams

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import org.junit.jupiter.api.Test

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("team.upnext.upnextteams")

        noClasses()
            .that()
                .resideInAnyPackage("team.upnext.upnextteams.service..")
            .or()
                .resideInAnyPackage("team.upnext.upnextteams.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..team.upnext.upnextteams.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses)
    }
}
