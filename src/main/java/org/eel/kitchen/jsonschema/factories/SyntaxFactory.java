/*
 * Copyright (c) 2011, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eel.kitchen.jsonschema.factories;

import org.eel.kitchen.jsonschema.base.AlwaysFalseValidator;
import org.eel.kitchen.jsonschema.base.MatchAllValidator;
import org.eel.kitchen.jsonschema.base.Validator;
import org.eel.kitchen.jsonschema.bundle.ValidatorBundle;
import org.eel.kitchen.jsonschema.main.ValidationContext;
import org.eel.kitchen.jsonschema.syntax.SyntaxValidator;
import org.eel.kitchen.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Factory providing syntax checking validators for a schema
 *
 * <p>Syntax validators are used to validate the schema itself. In doing so,
 * they ensure that keyword validators always have correct data to deal with.
 * </p>
 *
 * <p>Note that unknown keywords to this factory trigger a validation
 * <b>failure</b>. Therefore, it is important that <b>all</b> keywords be
 * registered (even if knowingly ignored). This is on purpose.</p>
 */
public final class SyntaxFactory
{
    /*
     * Our logger
     */
    private static final Logger logger
        = LoggerFactory.getLogger(SyntaxFactory.class);

    /**
     * Map pairing a schema keyword with its corresponding syntax validator
     */
    private final Map<String, SyntaxValidator> validators;

    /**
     * Constructor
     *
     * @param bundle the validator bundle to use
     */
    public SyntaxFactory(final ValidatorBundle bundle)
    {
        validators = new HashMap<String, SyntaxValidator>(bundle
            .syntaxValidators());
    }

    /**
     * Get the syntax validator for a given context
     *
     * <p>As the summary mentions, an unknown keyword to this factory will
     * trigger a failure by returning an {@link AlwaysFalseValidator}.</p>
     *
     * @param context the validation context
     * @return the matching validator
     */
    public Validator getValidator(final ValidationContext context)
    {
        final Set<String> fields
            = CollectionUtils.toSet(context.getSchema().getFieldNames());

        final Collection<Validator> collection = getValidators(fields);

        return collection.size() == 1 ? collection.iterator().next()
            : new MatchAllValidator(collection);
    }

    /**
     * Return a list of validators for a schema node
     *
     * @param fields the list of keywords
     * @return the list of validators
     */
    private Collection<Validator> getValidators(final Set<String> fields)
    {
        final Set<Validator> ret = new HashSet<Validator>();

        for (final String field: fields)
            if (validators.containsKey(field))
                ret.add(validators.get(field));
            else
                logger.warn("ignored keyword {}", field);

        return ret;
    }
}
