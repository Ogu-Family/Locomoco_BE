package org.prgms.locomocoserver.mogakkos.domain;

import static org.prgms.locomocoserver.categories.domain.QCategory.category;
import static org.prgms.locomocoserver.mogakkos.domain.QMogakko.mogakko;
import static org.prgms.locomocoserver.mogakkos.domain.location.QMogakkoLocation.mogakkoLocation;
import static org.prgms.locomocoserver.mogakkos.domain.mogakkotags.QMogakkoTag.mogakkoTag;
import static org.prgms.locomocoserver.tags.domain.QTag.tag;
import static org.prgms.locomocoserver.user.domain.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.categories.domain.Category;
import org.prgms.locomocoserver.categories.domain.QCategory;
import org.prgms.locomocoserver.mogakkos.domain.vo.QAddressInfo;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchConditionDto;
import org.prgms.locomocoserver.mogakkos.dto.request.SearchParameterDto;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MogakkoFilterRepository {
    private final JPAQueryFactory queryFactory;

    public List<Mogakko> findAll(SearchParameterDto searchParameterDto, SearchConditionDto searchConditionDto) {
        List<Category> categories = getCategories(searchParameterDto);

        return joinAll()
            .where(mogakko.deletedAt.isNull(),
                    mogakko.deadline.after(searchConditionDto.searchTime()),
                    tagsIn(searchParameterDto.tagIds()),
                    matchAgainst(searchParameterDto.totalSearch(), mogakko.title),
                    nicknameEq(searchParameterDto.nickname()),
                    locationLike(searchParameterDto.location())
                )
            .groupBy(mogakko.id).having(checkCategories(categories))
            .orderBy(mogakko.deadline.asc())
            .offset(searchConditionDto.offset())
            .limit(searchConditionDto.pageSize())
            .fetch();
    }

    private List<Category> getCategories(SearchParameterDto searchParameterDto) {
        List<Long> tagIds = searchParameterDto.tagIds();

        if (Objects.isNull(tagIds) || tagIds.isEmpty())
            return Collections.emptyList();

        return queryFactory.selectFrom(category).distinct()
            .innerJoin(tag).on(tag.category.eq(category))
            .where(tagsIn(tagIds))
            .fetch();
    }

    private JPAQuery<Mogakko> joinAll() {
        return queryFactory.selectFrom(mogakko)
            .innerJoin(user).on(mogakko.creator.eq(user))
            .innerJoin(mogakkoLocation).on(mogakkoLocation.mogakko.eq(mogakko))
            .innerJoin(mogakkoTag).on(mogakkoTag.mogakko.eq(mogakko))
            .innerJoin(tag).on(mogakkoTag.tag.eq(tag))
            .innerJoin(category).on(tag.category.eq(category));
    }

    private BooleanExpression tagsIn(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty())
            return null;

        return tag.id.in(tagIds);
    }

    private BooleanExpression locationLike(String location) {
        if (location == null || location.isBlank())
            return null;

        QAddressInfo qAddressInfo = mogakkoLocation.addressInfo;

        return qAddressInfo.address.startsWith(location)
            .or(qAddressInfo.city.startsWith(location))
            .or(qAddressInfo.hCity.startsWith(location));
    }

    private BooleanExpression nicknameEq(String nickname) {
        return (nickname == null || nickname.isBlank()) ? null : user.nickname.eq(nickname);
    }

    private BooleanExpression checkCategories(List<Category> categories) {
        BooleanExpression be = mogakko.id.isNotNull();

        for (Category category : categories) {
            BooleanExpression categoryCondition = new CaseBuilder().when(QCategory.category.eq(category))
                .then(tag.id).otherwise((Long) null).count().gt(0);

            be = be.and(categoryCondition);
        }

        return be;
    }

    private BooleanExpression matchAgainst(String keyword, StringPath... fields) {
        if (keyword == null || keyword.isBlank())
            return null;

        NumberTemplate<Double> match = Expressions.numberTemplate(Double.class, "FUNCTION('MATCH_AGAINST', {0}, {1})",
            Expressions.list(fields),
            keyword);
        return match.gt(0);
    }
}
