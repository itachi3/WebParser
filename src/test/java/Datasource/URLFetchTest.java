package Datasource;

import com.scout24.datasource.DownloadDataSource;
import com.scout24.models.Result;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class URLFetchTest {

    private final DownloadDataSource ds;

    public URLFetchTest() {
        String url = "https://github.com/login";
        ds = new DownloadDataSource(url);
        ds.fetch();
    }

    @Test
    public void checkLoginForm() throws Exception {
        Result result = ds.getResult();
        assertThat(result.isLoginForm());
    }

    @Test
    public void title() throws Exception {
        String expected = "Sign in to GitHub · GitHub";
        Result result = ds.getResult();
        assertThat(result.getTitle()).isEqualTo(expected);
    }

    @Test
    public void checkNonLoginForm() throws Exception {
        String url = "https://www.google.co.in/";
        DownloadDataSource ds = new DownloadDataSource(url);
        ds.fetch();
        Result result = ds.getResult();
        assertThat(!result.isLoginForm());
    }

}
