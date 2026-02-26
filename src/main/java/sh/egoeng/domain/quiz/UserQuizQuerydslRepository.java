package sh.egoeng.domain.quiz;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static sh.egoeng.domain.quiz.QQuiz.quiz;
import static sh.egoeng.domain.quiz.QUserQuiz.userQuiz;


@Repository
@RequiredArgsConstructor
public class UserQuizQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public Page<UserQuiz> findUserQuizHistory(
            Long userId,
            QuizType quizType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(userQuiz.user.id.eq(userId));

        if (quizType != null) {
            builder.and(quiz.type.eq(quizType));
        }
        if (fromDate != null) {
            builder.and(userQuiz.createdAt.goe(fromDate));
        }
        if (toDate != null) {
            builder.and(userQuiz.createdAt.loe(toDate));
        }

        List<UserQuiz> content = queryFactory
                .selectFrom(userQuiz)
                .join(userQuiz.quiz, quiz).fetchJoin()
                .where(builder)
                .orderBy(
                        userQuiz.createdAt.desc(),
                        userQuiz.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(userQuiz.count())
                .from(userQuiz)
                .join(userQuiz.quiz, quiz)
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total != null ? total : 0L);
    }
}













