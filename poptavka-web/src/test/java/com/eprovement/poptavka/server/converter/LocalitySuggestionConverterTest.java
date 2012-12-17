/*
 * Copyright (C) 2007-2011, GoodData(R) Corporation. All rights reserved.
 */
package com.eprovement.poptavka.server.converter;

import com.eprovement.poptavka.base.BasicIntegrationTest;
import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.enums.LocalityType;
import com.eprovement.poptavka.shared.domain.LocalitySuggestionDetail;
import org.apache.commons.lang.Validate;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class LocalitySuggestionConverterTest extends BasicIntegrationTest {

    @Autowired
    @Qualifier("localitySuggestionConverter")
    Converter<Locality, LocalitySuggestionDetail> localitySuggestionConverter;

    @Test
    public void testConvertToTarget() throws Exception {
        Validate.notNull(localitySuggestionConverter);
        final Locality locality = createTestLocalityCity();
        final LocalitySuggestionDetail localitySuggestionDetail =
                localitySuggestionConverter.convertToTarget(locality);

        assertNotNull(localitySuggestionDetail);
        assertThat(Long.toString(localitySuggestionDetail.getCityId()), is("3"));
        assertThat(localitySuggestionDetail.getCityCode(), is("cat3"));
        assertThat(localitySuggestionDetail.getCityName(), is("cityName"));
        assertThat(Long.toString(localitySuggestionDetail.getStateId()), is("2"));
        assertThat(localitySuggestionDetail.getStateCode(), is("cat2"));
        assertThat(localitySuggestionDetail.getStateName(), is("regionName"));
    }

    private Locality createTestLocalityCity() {
        final Locality locality = createLocalityCity();
        locality.setParent(createLocalityRegion());
        return locality;
    }

    private Locality createLocalityCountry() {
        final Locality country = new Locality();
        country.setId(1L);
        country.setCode("cat1");
        country.setName("countryName");
        country.setType(LocalityType.COUNTRY);
        return country;
    }

    private Locality createLocalityRegion() {
        final Locality region = new Locality();
        region.setId(2L);
        region.setCode("cat2");
        region.setName("regionName");
        region.setType(LocalityType.REGION);
        return region;
    }

    private Locality createLocalityCity() {
        final Locality city = new Locality();
        city.setId(3L);
        city.setCode("cat3");
        city.setName("cityName");
        city.setType(LocalityType.CITY);
        return city;
    }

    private Locality createLocalityDistrict() {
        final Locality district = new Locality();
        district.setId(4L);
        district.setCode("cat4");
        district.setName("districtName");
        district.setType(LocalityType.DISTRICT);
        return district;
    }
}