package models;

import static io.dropwizard.testing.FixtureHelpers.*;
import static org.fest.assertions.api.Assertions.*;

import com.scout24.models.HyperLinks;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class HyperLinksTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void deSerialization() throws Exception {
        List<String> links = new ArrayList<String>() {{
            add("https://github.com");
        }};
        final HyperLinks hyperLink = new HyperLinks(links);

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("hyperlink.json"), HyperLinks.class));

        assertThat(MAPPER.writeValueAsString(hyperLink)).isEqualTo(expected);
    }
}
