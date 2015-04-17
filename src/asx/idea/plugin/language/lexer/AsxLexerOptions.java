package asx.idea.plugin.language.lexer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.intellij.lang.javascript.dialects.JSLanguageFeature;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * Created by Sergey on 4/5/15.
 */
public class AsxLexerOptions {
    static final ImmutableSet<JSLanguageFeature> ASX = Sets.immutableEnumSet(
        JSLanguageFeature.LET_DEFINITIONS,
        JSLanguageFeature.FOR_EACH,
        JSLanguageFeature.YIELD_GENERATORS,
        JSLanguageFeature.ARRAY_COMPREHENSIONS,
        JSLanguageFeature.LET_SCOPE,
        JSLanguageFeature.DESTRUCTURING_ASSIGNMENT,
        JSLanguageFeature.EXPRESSION_CLOSURES,
        JSLanguageFeature.GENERATOR_EXPRESSIONS,
        JSLanguageFeature.ACCESSORS,
        JSLanguageFeature.ARROW_FUNCTIONS,
        JSLanguageFeature.REST_PARAMETERS,
        JSLanguageFeature.GENERICS,
        JSLanguageFeature.IMPORT_DECLARATIONS,
        JSLanguageFeature.CLASSES
    );
}

