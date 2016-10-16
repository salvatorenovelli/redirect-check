package com.snovelli.seo.redirect;

import com.snovelli.model.RedirectSpecification;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class RedirectSpecificationCSVReaderTest {

    RedirectSpecificationCSVReader sut;

    @Test
    public void testCSVParsing() throws URISyntaxException, IOException {

        URL csvFile = RedirectSpecification.class.getResource("/redirectspec-simple.csv");
        List<RedirectSpecification> specificationList = RedirectSpecificationCSVReader.parse(Paths.get(csvFile.toURI()));

        assertThat(specificationList, notNullValue());
        assertThat(specificationList.size(), is(3));


        int i = 1;
        for (RedirectSpecification curEntry : specificationList) {
            assertThat(curEntry.getSourceURI(), equalTo("http://sourceURI" + i + ".com"));
            assertThat(curEntry.getExpectedDestination(), equalTo("http://expectedURI" + i + ".com"));
            i++;
        }


    }

    @Test
    public void testCSVParsingWithURIError() throws URISyntaxException, IOException {

        URL csvFile = RedirectSpecification.class.getResource("/redirectspec-urierror.csv");
        List<RedirectSpecification> specificationList = RedirectSpecificationCSVReader.parse(Paths.get(csvFile.toURI()));

        assertThat(specificationList, notNullValue());
        assertThat(specificationList.size(), is(3));


    }


}