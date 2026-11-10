package team.projectpulse.ram.document.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import team.projectpulse.ram.document.DocumentType;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentTemplateRegistryTest {

    private DocumentTemplateRegistry registry;

    @BeforeEach
    void setUp() {
        this.registry = new DocumentTemplateRegistry();
        this.registry.loadAllTemplates(); // what @PostConstruct does, against the real YAML files on the classpath
    }

    @Test
    void sharedTemplateCannotBeChangedByACaller() {
        DocumentTemplate srs = this.registry.find(DocumentType.SRS).orElseThrow();
        int sectionCount = srs.sections().size();
        assertThat(sectionCount).isPositive();

        assertThatThrownBy(() -> srs.sections().clear()).isInstanceOf(UnsupportedOperationException.class);

        // The next caller still gets the template exactly as it was loaded.
        assertThat(this.registry.find(DocumentType.SRS).orElseThrow().sections()).hasSize(sectionCount);
    }

    @Test
    void templateDoesNotShareTheListItWasBuiltFrom() {
        List<DocumentTemplate.SectionTemplate> source = new ArrayList<>();
        source.add(new DocumentTemplate.SectionTemplate("INTRODUCTION", "Introduction", null, null));
        DocumentTemplate template = new DocumentTemplate("SRS", source);

        source.clear();

        assertThat(template.sections()).hasSize(1);
    }

    @Test
    void templateWithNoSectionsKeyHasNoSections() {
        assertThat(new DocumentTemplate("Empty", null).sections()).isEmpty();
    }

}
