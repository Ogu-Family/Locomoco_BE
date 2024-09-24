package org.prgms.locomocoserver.global.common.contributor;

import static org.hibernate.type.StandardBasicTypes.DOUBLE;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

public class MatchAgainstContributor implements FunctionContributor {

    private static final String FUNCTION_NAME = "MATCH_AGAINST";
    private static final String FUNCTION_PATTERN = "MATCH(?1) AGAINST(?2 IN BOOLEAN MODE)";

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry()
            .registerPattern(
                FUNCTION_NAME,
                FUNCTION_PATTERN,
                functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(DOUBLE)
            );
    }
}
