package team.projectpulse.ram.document.template;

import team.projectpulse.ram.requirement.SectionType;

import java.util.List;

/**
 * A requirement document template, loaded once from YAML into the singleton {@link DocumentTemplateRegistry} and
 * handed to every caller of {@code find()}. It is immutable so that no caller can change the template that every
 * later document is created from.
 */
public record DocumentTemplate(String title, List<SectionTemplate> sections) {

    public DocumentTemplate {
        // A template file with no "sections:" key is allowed and yields no sections. A null entry inside the list
        // means a broken template file, and List.copyOf rejects it, so the application fails at startup.
        sections = sections == null ? List.of() : List.copyOf(sections);
    }

    public record SectionTemplate(String sectionKey, String title, SectionType type, String guidance) {
    }

}
