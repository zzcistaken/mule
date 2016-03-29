/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.filters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SmallTest
public class WildcardFilterTestCase extends AbstractMuleTestCase
{

    public static final String REGEX_SUGGESTION_MESSAGE = "Consider using a regex-filter instead";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WildcardFilter filter = new WildcardFilter();

    private void setUpPattern(String pattern) throws InitialisationException
    {
        filter.setPattern(pattern);
        filter.initialise();
    }

    @Test
    public void noPattern() throws InitialisationException
    {
        filter.initialise();
        assertThat(filter.accept("No tengo dinero"), is(false));
        assertThat(filter.accept(""), is(false));
    }

    @Test
    public void simpleWildcard() throws InitialisationException
    {
        setUpPattern("*");
        assertThat(filter.accept("all"), is(true));
        assertThat(filter.accept("none"), is(true));
    }

    @Test
    public void doubleWildcard() throws InitialisationException
    {
        setUpPattern("**");
        assertThat(filter.accept("all"), is(true));
        assertThat(filter.accept("none"), is(true));
    }

    @Test
    public void exactMatch() throws InitialisationException
    {
        setUpPattern("fox");
        assertThat(filter.accept("fox"), is(true));
        assertThat(filter.accept("fire fox"), is(false));
        assertThat(filter.accept("fox games"), is(false));
        assertThat(filter.accept("The quick fox run"), is(false));
    }

    @Test
    public void prefix() throws InitialisationException
    {
        setUpPattern("* brown fox");
        assertThat(filter.accept("The quick brown fox"), is(true));
        assertThat(filter.accept("* brown fox"), is(true));
        assertThat(filter.accept("The quickbrown fox"), is(false));
        assertThat(filter.accept("The quick brown fo"), is(false));

    }

    @Test
    public void postfix() throws InitialisationException
    {
        WildcardFilter filter = new WildcardFilter("The quick *");
        filter.initialise();
        assertThat(filter.getPattern(), is(notNullValue()));
        assertThat(filter.accept("The quick brown fox"), is(true));
        assertThat(filter.accept("The quick *"), is(true));
        assertThat(filter.accept("The quickbrown fox"), is(false));
        assertThat(filter.accept("he quick brown fox"), is(false));
    }

    @Test
    public void enclosing() throws InitialisationException
    {
        setUpPattern("* brown *");
        assertThat(filter.accept("The quick brown fox"), is(true));
        assertThat(filter.accept("* brown fox"), is(true));
        assertThat(filter.accept("The quickbrown fox"), is(false));
        assertThat(filter.accept("The quick brock fox"), is(false));
    }

    @Test
    public void multiplePatterns() throws InitialisationException
    {
        setUpPattern("* brown*, The*");
        assertThat(filter.accept("The quick brown fox"), is(true));
        assertThat(filter.accept(" brown fox"), is(true));
        assertThat(filter.accept("The quickbrown fox"), is(true));
        assertThat(filter.accept("This brock"), is(false));
    }

    @Test
    public void caseSensitive() throws InitialisationException
    {
        setUpPattern("* Brown fox");
        assertThat(filter.accept("The quick Brown fox"), is(true));
        assertThat(filter.accept("* brown fox"), is(false));
    }

    @Test
    public void caseInsensitive() throws InitialisationException
    {
        filter.setPattern("* Brown fox");
        filter.setCaseSensitive(false);
        filter.initialise();
        assertThat(filter.accept("The quick Brown fox"), is(true));
        assertThat(filter.accept("* brown fox"), is(true));
    }

    @Test
    public void midPatternWildcardNotAccepted() throws InitialisationException
    {
        filter.setPattern("The quick * fox");
        expectedException.expect(InitialisationException.class);
        expectedException.expectMessage(REGEX_SUGGESTION_MESSAGE);
        filter.initialise();
    }

    @Test
    public void twoWildcardsStartMidPatternNotAccepted() throws InitialisationException
    {
        filter.setPattern("*the quick * fox");
        expectedException.expect(InitialisationException.class);
        expectedException.expectMessage(REGEX_SUGGESTION_MESSAGE);
        filter.initialise();
    }

    @Test
    public void twoWildcardsMidPatternNotAccepted() throws InitialisationException
    {
        filter.setPattern("the * brown * run");
        expectedException.expect(InitialisationException.class);
        expectedException.expectMessage(REGEX_SUGGESTION_MESSAGE);
        filter.initialise();
    }

    @Test
    public void twoWildcardsMidEndPatternNotAccepted() throws InitialisationException
    {
        filter.setPattern("the * brown fox*");
        expectedException.expect(InitialisationException.class);
        expectedException.expectMessage(REGEX_SUGGESTION_MESSAGE);
        filter.initialise();
    }

    @Test
    public void subclassesFilter() throws InitialisationException
    {
        setUpPattern("java.lang.Throwable+");
        assertThat(filter.accept(new Exception()), is(true));
        assertThat(filter.accept(new Throwable()), is(true));
        assertThat(filter.accept(new Object()), is(false));
    }

    @Test
    public void classFilter() throws InitialisationException
    {
        setUpPattern("java.lang.Throwable");
        assertThat(filter.accept(new Exception()), is(false));
        assertThat(filter.accept(new Throwable()), is(true));
        assertThat(filter.accept(new Object()), is(false));
    }

    @Test
    public void subclassesUsingStringFilter() throws InitialisationException
    {
        setUpPattern("java.lang.Throwable+");
        assertThat(filter.accept(new Exception().getClass().getName()), is(true));
        assertThat(filter.accept(new Throwable().getClass().getName()), is(true));
        assertThat(filter.accept(new Object().getClass().getName()), is(false));
    }

    @Test
    public void classUsingStringFilter() throws InitialisationException
    {
        setUpPattern("java.lang.Throwable");
        assertThat(filter.accept(new Exception().getClass().getName()), is(false));
        assertThat(filter.accept(new Throwable().getClass().getName()), is(true));
        assertThat(filter.accept(new Object().getClass().getName()), is(false));
    }
}
