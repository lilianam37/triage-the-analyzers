package team.projectpulse.team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PMD UseLocaleWithCaseConversions on TeamSpecs: toLowerCase() with no argument uses the JVM's default locale. Under
 * a Turkish locale, capital I lowercases to a dotless ı, so the search term no longer matches what MySQL's lower()
 * produces for the team name. These tests run the specifications under tr-TR.
 */
class TeamSpecsTest {

    private Locale originalLocale;

    private Root<Team> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder criteriaBuilder;
    private Expression<String> loweredColumn;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        this.originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));

        this.root = mock(Root.class);
        this.query = mock(CriteriaQuery.class);
        this.criteriaBuilder = mock(CriteriaBuilder.class);
        this.loweredColumn = mock(Expression.class);
        Path<Object> path = mock(Path.class);
        doReturn(path).when(this.root).get(anyString());
        doReturn(path).when(path).get(anyString());
        when(this.criteriaBuilder.lower(any())).thenReturn(this.loweredColumn);
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(this.originalLocale);
    }

    @Test
    void containsTeamNameLowercasesTheSearchTermIndependentlyOfTheDefaultLocale() {
        TeamSpecs.containsTeamName("IRIS").toPredicate(this.root, this.query, this.criteriaBuilder);

        ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
        verify(this.criteriaBuilder).like(any(), pattern.capture());
        assertThat(pattern.getValue()).isEqualTo("%iris%"); // under tr-TR, toLowerCase() gives "%ırıs%"
    }

    @Test
    void hasSectionNameLowercasesTheSearchTermIndependentlyOfTheDefaultLocale() {
        TeamSpecs.hasSectionName("SECTION I").toPredicate(this.root, this.query, this.criteriaBuilder);

        ArgumentCaptor<Object> value = ArgumentCaptor.forClass(Object.class);
        verify(this.criteriaBuilder).equal(any(Expression.class), value.capture());
        assertThat(value.getValue()).isEqualTo("section i");
    }

}
