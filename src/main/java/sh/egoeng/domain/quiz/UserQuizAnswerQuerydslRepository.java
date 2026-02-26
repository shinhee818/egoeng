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
import static sh.egoeng.domain.quiz.QUserQuizAnswer.userQuizAnswer;


@Repository
@RequiredArgsConstructor
public class UserQuizAnswerQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public Page<UserQuizAnswer> findUserQuizHistory(
            Long userId,
            QuizType quizType,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        BooleanBuilder builder = new BooleanBuilder()
                .and(userQuizAnswer.user.id.eq(userId));

        if (quizType != null) {
            builder.and(quiz.type.eq(quizType));
        }
        if (fromDate != null) {
            builder.and(userQuizAnswer.answeredAt.goe(fromDate));
        }
        if (toDate != null) {
            builder.and(userQuizAnswer.answeredAt.loe(toDate));
        }

        List<UserQuizAnswer> content = queryFactory
                .selectFrom(userQuizAnswer)
                .join(userQuizAnswer.quiz, quiz).fetchJoin()
                .where(builder)
                .orderBy(
                        userQuizAnswer.answeredAt.desc(),
                        userQuizAnswer.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(userQuizAnswer.count())
                .from(userQuizAnswer)
                .join(userQuizAnswer.quiz, quiz)
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total != null ? total : 0L);
    }
}

